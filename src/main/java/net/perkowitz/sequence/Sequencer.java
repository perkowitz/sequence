package net.perkowitz.sequence;

import com.google.common.collect.Maps;
import net.perkowitz.sequence.models.*;
import net.perkowitz.sequence.models.Track;
import org.codehaus.jackson.map.ObjectMapper;

import javax.sound.midi.*;
import java.io.File;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * Created by optic on 7/8/16.
 */
public class Sequencer implements SequencerInterface {

    public enum StepMode { MUTE, VELOCITY, JUMP, PLAY }
    private static final int DEFAULT_TIMER = 125;

    ObjectMapper objectMapper = new ObjectMapper();

    private SequencerController controller;
    private SequencerDisplay display;
    private MidiDevice sequenceOutput;
    private Receiver sequenceReceiver;

    private Map<Mode, Boolean> modeIsActiveMap = Maps.newHashMap();
    private Map<SequencerDisplay.DisplayButton, SequencerDisplay.ButtonState> buttonStateMap = Maps.newHashMap();

    private Memory memory;
    private int totalStepCount = 0;
    private int nextStepIndex = 0;

    // sequencer states
    private boolean playing = false;
    private boolean trackSelectMode = true;
    private StepMode stepMode = StepMode.MUTE;
    private boolean patternEditMode = false;

    private static CountDownLatch stop = new CountDownLatch(1);


    /***** constructor *********************************************************************/

    public Sequencer(SequencerController controller, SequencerDisplay display, MidiDevice sequenceOutput) throws Exception {

        this.controller = controller;
        this.controller.setSequencer(this);
        this.display = display;

        this.sequenceOutput = sequenceOutput;
        this.sequenceOutput.open();
        this.sequenceReceiver = sequenceOutput.getReceiver();

        load();
        if (memory == null) {
            memory = new Memory();
            memory.select(memory.selectedPattern().getTrack(8));
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

        // retrieve current selected pattern and range; get new pattern
        Pattern currentSelected = memory.selectedPattern();
        List<Pattern> currentRange = memory.getPatternRange();
        Pattern newPattern = memory.selectedSession().getPattern(index);
        System.out.printf("selectPattern: current=%s, chained=%s\n", currentSelected, newPattern);

        // select the new pattern and set it as the range (chained)
        memory.select(newPattern);
        memory.setPatternRange(index, index, index);
        if (!playing) {
            // if not currently playing, you can advance directly to the new pattern
            memory.advancePattern();
        }

        // update display of all affected patterns
        for (Pattern pattern : currentRange) {
            display.displayPattern(pattern);
        }
        display.displayPattern(newPattern);

    }

    public void selectPatterns(int minIndex, int maxIndex) {
        System.out.printf("selectPatterns: %d - %d\n", minIndex, maxIndex);

        // retrieve current selected pattern and range; get new pattern
        Pattern currentSelected = memory.selectedPattern();
        List<Pattern> currentRange = memory.getPatternRange();

        // select the new pattern and set it as the range (chained)
        Pattern newPattern = memory.selectedSession().getPattern(minIndex);
        memory.select(newPattern);
        memory.setPatternRange(minIndex, maxIndex, minIndex);
        List<Pattern> newRange = memory.getPatternRange();
        if (!playing) {
            // if not currently playing, you can advance directly to the new pattern
//            memory.advancePattern();
        }

        // update display of all affected patterns
        display.displayPattern(currentSelected);
        for (Pattern pattern : currentRange) {
            display.displayPattern(pattern);
        }
        for (Pattern pattern : newRange) {
            display.displayPattern(pattern);
        }


    }


    public void selectTrack(int index) {

        Track track = memory.selectedPattern().getTrack(index);
        System.out.printf("selectTrack: %d, %s\n", index, track);
        if (trackSelectMode) {
            // unselect the currently selected track
            Track currentTrack = memory.selectedTrack();
            memory.select(track);
            display.displayTrack(currentTrack);
            display.displayTrack(track);

        } else {
            // toggle track enabled
            track.setEnabled(!track.isEnabled());
            display.displayTrack(track);

        }

    }

    public void selectStep(int index) {

        System.out.printf("selectStep: %d\n", index);
        Step step = memory.selectedTrack().getStep(index);
        if (stepMode == StepMode.MUTE) {
            // in mute mode, both mute/unmute and select that step
            step.setOn(!step.isOn());
            memory.setSelectedStepIndex(index);
//            step.setSelected(true);    // are we doing this?
            display.displayStep(step);
            display.displayValue(step.getVelocity());
        } else if (stepMode == StepMode.JUMP) {
            setNextStepIndex(index);
            nextStepIndex = (index + net.perkowitz.sequence.models.Track.getStepCount()) % net.perkowitz.sequence.models.Track.getStepCount();
        } else if (stepMode == StepMode.VELOCITY) {
//            step.setSelected(true);    // are we doing this?
            memory.setSelectedStepIndex(index);
            display.displayValue(step.getVelocity());
        } else if (stepMode == StepMode.PLAY) {
            net.perkowitz.sequence.models.Track track = memory.selectedPattern().getTrack(index);
            sendMidiNote(track.getMidiChannel(), track.getNoteNumber(), 100);
        }
    }

    public void selectValue(int index) {
        System.out.printf("selectValue: %d\n", index);
        if (memory.selectedStep() != null) {
            int velocity = ((index+1)*16) - 1;
            memory.selectedStep().setVelocity(velocity);
            display.displayValue(velocity);
        }
    }

    public void selectMode(Mode mode) {

        System.out.printf("selectMode: %s\n", mode);
        switch (mode) {

            case PATTERN_PLAY:
                patternEditMode = false;
                display.displayMode(Mode.PATTERN_EDIT, false);
                break;

            case PATTERN_EDIT:
                patternEditMode = true;
                display.displayMode(Mode.PATTERN_EDIT, true);
                break;

            case TRACK_MUTE:
                trackSelectMode = false;
                display.displayModeChoice(Mode.TRACK_MUTE, TRACK_MODES);
                break;

            case TRACK_EDIT:
                if (trackSelectMode) {
                    // if you press select mode a second time, it unselects the selected track (so no track is selected)
                    memory.selectedTrack().setSelected(false);
                    display.displayTrack(memory.selectedTrack());
//                    display.clearSteps();
                }
                trackSelectMode = true;
                display.displayModeChoice(Mode.TRACK_EDIT, TRACK_MODES);
                break;

            case STEP_MUTE:
                stepMode = StepMode.MUTE;
                display.displayModeChoice(Mode.STEP_MUTE, STEP_MODES);
                display.displayTrack(memory.selectedTrack());
                break;

            case STEP_VELOCITY:
                stepMode = stepMode.VELOCITY;
                display.displayModeChoice(Mode.STEP_VELOCITY, STEP_MODES);
                display.displayTrack(memory.selectedTrack());
                break;

            case STEP_JUMP:
                stepMode = stepMode.JUMP;
                display.displayModeChoice(Mode.STEP_JUMP, STEP_MODES);
//                memory.setSelectedStep(null);
                display.clearSteps();
                break;

            case STEP_PLAY:
                stepMode = stepMode.PLAY;
                display.displayModeChoice(Mode.STEP_PLAY, STEP_MODES);
//                memory.setSelectedStep(null);
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
        nextStepIndex = 0;
        List<Pattern> patternRange = memory.getPatternRange();
        if (patternRange.size() > 0) {
            nextPattern(patternRange.get(0));
        }

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
        }, DEFAULT_TIMER, DEFAULT_TIMER);


    }

    private void setNextStepIndex(int stepNumber) {
        nextStepIndex = (stepNumber + Track.getStepCount()) % Track.getStepCount();
    }

    private void nextPattern(Pattern nextPattern) {
        Pattern playingPattern = memory.playingPattern();
        System.out.printf("nextPattern: playing=%s, chained=%s\n", playingPattern, nextPattern);
        if (nextPattern != playingPattern) {
            int selectedTrackIndex = memory.selectedTrack().getIndex();
            memory.select(nextPattern);
            memory.select(nextPattern.getTrack(selectedTrackIndex));
            display.displayPattern(playingPattern);
            display.displayPattern(nextPattern);
        }

    }

    private void advance(boolean andReset) {

        if (andReset) {
            nextStepIndex = 0;
        }

        // new pattern on reset/0
        if (nextStepIndex == 0) {
            System.out.printf("Advance selectedPattern=%s, selectedTrack=%s\n", memory.selectedPattern(), memory.selectedTrack());
            nextPattern(memory.advancePattern());
        }

        // send the midi notes
//        System.out.printf("Advance: send midi\n");
        for (net.perkowitz.sequence.models.Track track : memory.playingPattern().getTracks()) {
            Step step = track.getStep(nextStepIndex);
            if (step.isOn()) {
                track.setPlaying(true);
            }
            if (track.isEnabled()) {
                if (step.isOn()) {
                    sendMidiNote(track.getMidiChannel(), track.getNoteNumber(), step.getVelocity());
                }
            }
            display.displayTrack(track, false);
            track.setPlaying(false);

        }

        totalStepCount++;
        int oldStepNumber = nextStepIndex;
        setNextStepIndex(nextStepIndex + 1);
        // NB: assumes that the play steps are always displayed using the step buttons
        display.displayStep(memory.selectedTrack().getStep(oldStepNumber));
        display.displayPlayingStep(nextStepIndex);
//        System.out.printf("Advance: done\n");

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
