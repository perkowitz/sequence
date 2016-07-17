package net.perkowitz.sequence;

import com.google.common.collect.Maps;
import net.perkowitz.sequence.models.*;
import net.perkowitz.sequence.models.Track;
import net.thecodersbreakfast.lp4j.api.*;
import org.codehaus.jackson.map.ObjectMapper;

import javax.sound.midi.*;
import java.io.File;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import static net.perkowitz.sequence.launchpad.LaunchpadUtil.*;

/**
 * Created by optic on 7/8/16.
 */
public class Sequencer implements SequencerInterface {

    public enum StepMode { MUTE, VELOCITY, JUMP, PLAY }

    ObjectMapper objectMapper = new ObjectMapper();

    private SequencerController controller;
    private SequencerDisplay display;
    private MidiDevice sequenceOutput;
    private Receiver sequenceReceiver;

    private Map<Mode, Boolean> modeIsActiveMap = Maps.newHashMap();
    private Map<SequencerDisplay.DisplayButton, SequencerDisplay.ButtonState> buttonStateMap = Maps.newHashMap();

    private Memory memory;
    private int totalStepCount = 0;
    private int playingStepNumber = 0;

    // sequencer states
    private boolean playing = false;
    private boolean trackSelectMode = true;
    private StepMode stepMode = StepMode.MUTE;

    private static CountDownLatch stop = new CountDownLatch(1);


    /***** constructor *********************************************************************/

    public Sequencer(SequencerController controller, SequencerDisplay display, MidiDevice sequenceOutput) throws Exception {

        this.controller = controller;
        this.controller.setSequencer(this);
        this.display = display;

        this.sequenceOutput = sequenceOutput;
        this.sequenceOutput.open();
        this.sequenceReceiver = sequenceOutput.getReceiver();

//        load();
        if (memory == null) {
            memory = new Memory();
            memory.select(memory.getSelectedPattern().getTrack(8));
        }

        for (Mode mode : Mode.values()) {
            modeIsActiveMap.put(mode, false);
        }
        Mode[] activeModes = new Mode[] { Mode.PATTERN_PLAY, Mode.TRACK_EDIT, Mode.STEP_MUTE };
        for (Mode mode : activeModes) {
            modeIsActiveMap.put(mode, true);
        }


        display.initialize();
        display.displayHelp();
        Thread.sleep(1000);
        display.displayAll(memory, modeIsActiveMap);

        startTimer();
        stop.await();

    }


    /***** public interface *********************************************************************/

    public void selectSession(int index) {

    }

    public void selectPattern(int index) {

    }

    public void selectTrack(int index) {

        if (trackSelectMode) {
            // unselect the currently selected track
            memory.getSelectedTrack().setSelected(false);
            display.displayTrack(memory.getSelectedTrack());

            // select the new track
            int selectedTrackNumber = index;
            net.perkowitz.sequence.models.Track selectedTrack = memory.getSelectedPattern().getTrack(selectedTrackNumber);
            memory.setSelectedTrack(selectedTrack);
            selectedTrack.setSelected(true);
            display.displayTrack(selectedTrack);

        } else {
            // toggle track enabled
            net.perkowitz.sequence.models.Track track = memory.getSelectedPattern().getTrack(index);
            track.setEnabled(!track.isEnabled());
            display.displayTrack(track);

        }

    }

    public void selectStep(int index) {

        Step step = memory.getSelectedTrack().getStep(index);
        if (stepMode == StepMode.MUTE) {
            step.setOn(!step.isOn());
            display.displayStep(step);
            memory.setSelectedStep(step);
            display.displayValue(step.getVelocity());
        } else if (stepMode == StepMode.JUMP) {
            playingStepNumber = (index + (net.perkowitz.sequence.models.Track.getStepCount() - 1)) % net.perkowitz.sequence.models.Track.getStepCount();
        } else if (stepMode == StepMode.VELOCITY) {
            memory.setSelectedStep(step);
            display.displayValue(step.getVelocity());
        } else if (stepMode == StepMode.PLAY) {
            net.perkowitz.sequence.models.Track track = memory.getSelectedPattern().getTrack(index);
            sendMidiNote(track.getMidiChannel(), track.getNoteNumber(), 100);
        }
    }

    public void selectValue(int index) {
        if (memory.getSelectedStep() != null) {
            int velocity = ((index+1)*16) - 1;
            memory.getSelectedStep().setVelocity(velocity);
            display.displayValue(velocity);
        }
    }

    public void selectMode(Mode mode) {

        switch (mode) {
            case TRACK_MUTE:
                trackSelectMode = false;
                display.displayModeChoice(Mode.TRACK_MUTE, TRACK_MODES);
                break;

            case TRACK_EDIT:
                if (trackSelectMode) {
                    // if you press select mode a second time, it unselects the selected track (so no track is selected)
                    memory.getSelectedTrack().setSelected(false);
                    display.displayTrack(memory.getSelectedTrack());
//                    display.clearSteps();
                }
                trackSelectMode = true;
                display.displayModeChoice(Mode.TRACK_EDIT, TRACK_MODES);
                break;

            case STEP_MUTE:
                stepMode = StepMode.MUTE;
                display.displayModeChoice(Mode.STEP_MUTE, STEP_MODES);
                display.displayTrack(memory.getSelectedTrack());
                break;

            case STEP_VELOCITY:
                stepMode = stepMode.VELOCITY;
                display.displayModeChoice(Mode.STEP_VELOCITY, STEP_MODES);
                display.displayTrack(memory.getSelectedTrack());
                break;

            case STEP_JUMP:
                stepMode = stepMode.JUMP;
                display.displayModeChoice(Mode.STEP_JUMP, STEP_MODES);
                memory.setSelectedStep(null);
                display.clearSteps();
                break;

            case STEP_PLAY:
                stepMode = stepMode.PLAY;
                display.displayModeChoice(Mode.STEP_PLAY, STEP_MODES);
                memory.setSelectedStep(null);
                display.clearSteps();
                break;

            case PLAY:
                toggleStartStop();
                break;

            case EXIT:
                shutdown();
                break;

            case SAVE:
                save();
                break;

            case HELP:
                display.displayHelp();
                break;

            //         LOAD, COPY, CLEAR, PATTERN_PLAY, PATTERN_EDIT,

        }
    }


    /***** private implementation *********************************************************************/


    public void shutdown() {
//        save();
        display.initialize();
        sequenceOutput.close();
        System.exit(0);
    }

    public void toggleStartStop() {
        playing = !playing;
        if (playing) {
            display.displayMode(Mode.PLAY, true);
        } else {
            display.displayMode(Mode.PLAY, false);
            totalStepCount = 0;
        }
        playingStepNumber = net.perkowitz.sequence.models.Track.getStepCount()-1;
    }

    public void startTimer() {

        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (playing) {
                    boolean andReset = false;
                    if (totalStepCount % net.perkowitz.sequence.models.Track.getStepCount() == 0) {
                        andReset = true;
                    }
                    advance(andReset);
                }
            }
        }, 125, 125);


    }

    private void advancePlayStep() {
        setPlayStep(playingStepNumber+1);
    }

    private void setPlayStep(int stepNumber) {

        // reset current step to normal appearance
        // NB: assumes that the play steps are always displayed using the step buttons
        int oldStepNumber = playingStepNumber;
        display.displayStep(memory.getSelectedTrack().getStep(oldStepNumber));

        // move the playing step and display it
        playingStepNumber = (stepNumber + Track.getStepCount()) % Track.getStepCount();
        display.displayPlayingStep(playingStepNumber);


    }

    private void advance(boolean andReset) {

        totalStepCount++;

        // determine the new step number and display it
        int newStepNumber = playingStepNumber + 1;
        if (andReset) {
            newStepNumber = 0;
        }
        setPlayStep(newStepNumber);

        // send the midi notes
        for (net.perkowitz.sequence.models.Track track : memory.getSelectedPattern().getTracks()) {
            Step step = track.getStep(playingStepNumber);
            if (step.isOn()) {
                track.setPlaying(true);
            }
            if (track.isEnabled()) {
                if (step.isOn()) {
                    sendMidiNote(track.getMidiChannel(), track.getNoteNumber(), step.getVelocity());
                }
            }
            display.displayTrack(track);
            track.setPlaying(false);

        }

    }

    private void save() {

        try {
            objectMapper.writeValue(new File("sequencer.json"), memory);
//            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(memory);
//            System.out.println(json);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void load() {

        try {
            memory = objectMapper.readValue(new File("sequencer.json"), Memory.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /************************************************************************
     * midi output implementation
     *
     */
    private void sendMidiNote(int channel, int noteNumber, int velocity) {
//        System.out.printf("Note: %d, %d, %d\n", channel, noteNumber, velocity);
        // TODO how do we send note off?

        try {
            ShortMessage noteMessage = new ShortMessage();
            noteMessage.setMessage(ShortMessage.NOTE_ON, channel, noteNumber, velocity);
            sequenceReceiver.send(noteMessage, -1);

        } catch (InvalidMidiDataException e) {
            System.err.println(e);
        }

    }


}
