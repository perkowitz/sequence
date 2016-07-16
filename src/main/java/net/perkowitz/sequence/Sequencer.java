package net.perkowitz.sequence;

import com.google.common.collect.Maps;
import net.thecodersbreakfast.lp4j.api.*;
import net.thecodersbreakfast.lp4j.midi.MidiDeviceConfiguration;
import net.thecodersbreakfast.lp4j.midi.MidiLaunchpad;
import org.codehaus.jackson.map.ObjectMapper;

import javax.sound.midi.*;
import java.io.File;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import static net.perkowitz.sequence.LaunchpadUtil.*;

/**
 * Created by optic on 7/8/16.
 */
public class Sequencer extends LaunchpadListenerAdapter {

    public enum StepMode { MUTE, VELOCITY, JUMP, PLAY }

    ObjectMapper objectMapper = new ObjectMapper();

    private SequencerDisplay display;
    private MidiDevice controllerInput;
    private MidiDevice controllerOutput;
    private MidiDevice sequenceOutput;
    private Receiver sequenceReceiver;
    private Launchpad launchpad;
    private LaunchpadClient launchpadClient;

    private Map<SequencerDisplay.DisplayButton, SequencerDisplay.ButtonState> buttonStateMap = Maps.newHashMap();

    private Memory memory;
    private int totalStepCount = 0;
    private int playingStepNumber = 0;

    // sequencer states
    private boolean playing = false;
    private boolean trackSelectMode = true;
    private StepMode stepMode = StepMode.MUTE;

    private static CountDownLatch stop = new CountDownLatch(1);



    public Sequencer(SequencerDisplay display, MidiDevice controllerInput, MidiDevice controllerOutput, MidiDevice sequenceOutput) throws Exception {

        this.display = display;
        this.controllerInput = controllerInput;
        this.controllerOutput = controllerOutput;
        this.sequenceOutput = sequenceOutput;
        this.sequenceOutput.open();


        try {

            launchpad = new MidiLaunchpad(new MidiDeviceConfiguration(controllerInput, controllerOutput));
            launchpadClient = launchpad.getClient();
//            launchpad.setListener(new ControlListener(this, launchpadClient));
            launchpad.setListener(this);

            sequenceReceiver = sequenceOutput.getReceiver();

        } catch (MidiUnavailableException e) {
            System.err.printf("%s\n", e.getStackTrace().toString());
        }

//        load();
        if (memory == null) {
            memory = new Memory();
            memory.select(memory.getSelectedPattern().getTrack(8));
        }

        // initialize the state of the buttons
        buttonStateMap.put(SequencerDisplay.DisplayButton.PLAY, SequencerDisplay.ButtonState.DISABLED);
        buttonStateMap.put(SequencerDisplay.DisplayButton.EXIT, SequencerDisplay.ButtonState.ENABLED);
        buttonStateMap.put(SequencerDisplay.DisplayButton.SAVE, SequencerDisplay.ButtonState.ENABLED);
        buttonStateMap.put(SequencerDisplay.DisplayButton.TRACK_MUTE_MODE, SequencerDisplay.ButtonState.DISABLED);
        buttonStateMap.put(SequencerDisplay.DisplayButton.TRACK_SELECT_MODE, SequencerDisplay.ButtonState.ENABLED);
        buttonStateMap.put(SequencerDisplay.DisplayButton.STEP_MUTE_MODE, SequencerDisplay.ButtonState.ENABLED);
        buttonStateMap.put(SequencerDisplay.DisplayButton.STEP_VELOCITY_MODE, SequencerDisplay.ButtonState.DISABLED);
        buttonStateMap.put(SequencerDisplay.DisplayButton.STEP_JUMP_MODE, SequencerDisplay.ButtonState.DISABLED);
        buttonStateMap.put(SequencerDisplay.DisplayButton.STEP_PLAY_MODE, SequencerDisplay.ButtonState.DISABLED);

        display.initialize();
        display.displayHelp();
        Thread.sleep(1000);
        display.displayAll(memory, buttonStateMap);

        startTimer();
        stop.await();

    }

    public void shutdown() {

//        save();
        launchpadClient.reset();

        try {
            sequenceOutput.open();
        } catch (MidiUnavailableException e) {
            System.err.printf("%s\n", e.getStackTrace().toString());
        }

        System.exit(0);
    }

    public void toggleStartStop() {
        playing = !playing;
        if (playing) {
            display.displayButton(SequencerDisplay.DisplayButton.PLAY, SequencerDisplay.ButtonState.ENABLED);
        } else {
            display.displayButton(SequencerDisplay.DisplayButton.PLAY, SequencerDisplay.ButtonState.DISABLED);
            totalStepCount = 0;
            // TODO deal with this somehow
        }
        playingStepNumber = Track.getStepCount()-1;
    }

    public void startTimer() {

        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (playing) {
                    boolean andReset = false;
                    if (totalStepCount % Track.getStepCount() == 0) {
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
        for (Track track : memory.getSelectedPattern().getTracks()) {
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




    /************************************************************************
     * Launchpad listener implementation
     *
     */
    @Override
    public void onPadPressed(Pad pad, long timestamp) {

        try {
            if (pad.getY() >= TRACKS_MIN_ROW && pad.getY() <= TRACKS_MAX_ROW) {
                // pressing a track pad

                int trackNumber = pad.getX() + (pad.getY() - TRACKS_MIN_ROW) * 8;
                if (trackSelectMode) {
                    Track oldTrack = memory.getSelectedTrack();
                    Track newTrack = memory.getSelectedPattern().getTrack(trackNumber);
                    memory.select(newTrack);
                    display.displayTrack(oldTrack);
                    display.displayTrack(newTrack);

                } else {
                    // toggle track enabled
                    Track track = memory.getSelectedPattern().getTrack(trackNumber);
                    track.setEnabled(!track.isEnabled());
                    display.displayTrack(track);

                }

            } else if (pad.getY() >= STEPS_MIN_ROW && pad.getY() <= STEPS_MAX_ROW) {
                // pressing a step pad
                int stepNumber = pad.getX() + (pad.getY() - STEPS_MIN_ROW) * 8;
                Step step = memory.getSelectedTrack().getStep(stepNumber);
                if (stepMode == StepMode.MUTE) {
                    step.setOn(!step.isOn());
                    display.displayStep(step);
                    memory.select(step);
                    display.displayValue(step.getVelocity());
                } else if (stepMode == StepMode.JUMP) {
                    playingStepNumber = (stepNumber + (Track.getStepCount() - 1)) % Track.getStepCount();
                } else if (stepMode == StepMode.VELOCITY) {
                    memory.select(step);
                    display.displayValue(step.getVelocity());
                } else if (stepMode == StepMode.PLAY) {
                    Track track = memory.getSelectedPattern().getTrack(stepNumber);
                    sendMidiNote(track.getMidiChannel(), track.getNoteNumber(), 100);
                }

            } else if (pad.equals(TRACK_MUTE_MODE)) {
                trackSelectMode = false;
                display.displayButton(SequencerDisplay.DisplayButton.TRACK_MUTE_MODE, SequencerDisplay.ButtonState.ENABLED);
                display.displayButton(SequencerDisplay.DisplayButton.TRACK_SELECT_MODE, SequencerDisplay.ButtonState.DISABLED);

            } else if (pad.equals(TRACK_SELECT_MODE)) {
                if (trackSelectMode) {
                    // if you press select mode a second time, it unselects the selected track (so no track is selected)
                    memory.getSelectedTrack().setSelected(false);
                    display.displayTrack(memory.getSelectedTrack());
//                    display.clearSteps();
                }
                trackSelectMode = true;
                display.displayButton(SequencerDisplay.DisplayButton.TRACK_MUTE_MODE, SequencerDisplay.ButtonState.DISABLED);
                display.displayButton(SequencerDisplay.DisplayButton.TRACK_SELECT_MODE, SequencerDisplay.ButtonState.ENABLED);

            } else if (pad.equals(STEP_MUTE_MODE)) {
                stepMode = StepMode.MUTE;
                display.displayButton(SequencerDisplay.DisplayButton.STEP_MUTE_MODE, SequencerDisplay.ButtonState.ENABLED);
                display.displayButton(SequencerDisplay.DisplayButton.STEP_VELOCITY_MODE, SequencerDisplay.ButtonState.DISABLED);
                display.displayButton(SequencerDisplay.DisplayButton.STEP_JUMP_MODE, SequencerDisplay.ButtonState.DISABLED);
                display.displayButton(SequencerDisplay.DisplayButton.STEP_PLAY_MODE, SequencerDisplay.ButtonState.DISABLED);
                display.displayTrack(memory.getSelectedTrack());

            } else if (pad.equals(STEP_VELOCITY_MODE)) {
                stepMode = stepMode.VELOCITY;
                display.displayButton(SequencerDisplay.DisplayButton.STEP_MUTE_MODE, SequencerDisplay.ButtonState.DISABLED);
                display.displayButton(SequencerDisplay.DisplayButton.STEP_VELOCITY_MODE, SequencerDisplay.ButtonState.ENABLED);
                display.displayButton(SequencerDisplay.DisplayButton.STEP_JUMP_MODE, SequencerDisplay.ButtonState.DISABLED);
                display.displayButton(SequencerDisplay.DisplayButton.STEP_PLAY_MODE, SequencerDisplay.ButtonState.DISABLED);
                display.displayTrack(memory.getSelectedTrack());

            } else if (pad.equals(STEP_JUMP_MODE)) {
                stepMode = stepMode.JUMP;
                display.displayButton(SequencerDisplay.DisplayButton.STEP_MUTE_MODE, SequencerDisplay.ButtonState.DISABLED);
                display.displayButton(SequencerDisplay.DisplayButton.STEP_VELOCITY_MODE, SequencerDisplay.ButtonState.DISABLED);
                display.displayButton(SequencerDisplay.DisplayButton.STEP_JUMP_MODE, SequencerDisplay.ButtonState.ENABLED);
                display.displayButton(SequencerDisplay.DisplayButton.STEP_PLAY_MODE, SequencerDisplay.ButtonState.DISABLED);
                memory.setSelectedStep(null);
                display.clearSteps();

            } else if (pad.equals(STEP_PLAY_MODE)) {
                stepMode = stepMode.PLAY;
                display.displayButton(SequencerDisplay.DisplayButton.STEP_MUTE_MODE, SequencerDisplay.ButtonState.DISABLED);
                display.displayButton(SequencerDisplay.DisplayButton.STEP_VELOCITY_MODE, SequencerDisplay.ButtonState.DISABLED);
                display.displayButton(SequencerDisplay.DisplayButton.STEP_JUMP_MODE, SequencerDisplay.ButtonState.DISABLED);
                display.displayButton(SequencerDisplay.DisplayButton.STEP_PLAY_MODE, SequencerDisplay.ButtonState.ENABLED);
                memory.setSelectedStep(null);
                display.clearSteps();

            }



        } catch (Exception e) {
            System.err.println(e.toString());
        }

    }

    @Override
    public void onPadReleased(Pad pad, long timestamp) {
    }

    @Override
    public void onButtonPressed(Button button, long timestamp) {

        if (button.equals(BUTTON_PLAY)) {
            toggleStartStop();
        } else if (button.equals(BUTTON_EXIT)) {
            shutdown();
        } else if (button.equals(BUTTON_SAVE)) {
            save();
        } else if (button.equals(BUTTON_HELP)) {
            display.displayHelp();
        } else if (button.isRightButton()) {
            if (memory.getSelectedStep() != null) {
                int velocity = ((8-button.getCoordinate())*16) - 1;
                memory.getSelectedStep().setVelocity(velocity);
                display.displayValue(velocity);
            }
        }
    }


    @Override
    public void onButtonReleased(Button button, long timestamp) {

        if (button.equals(BUTTON_HELP)) {
            display.displayAll(memory, null);
        }
    }


}
