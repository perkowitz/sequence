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
//    private Session selectedSession;
//    private Pattern selectedPattern;
//    private Track selectedTrack;
    private int selectedTrackNumber = 0;
    private int playingStepNumber = 0;

    // sequencer states
    private boolean playing = false;
    private boolean trackSelectMode = true;

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

        load();
        if (memory == null) {
            memory = new Memory();
        }

        // initialize the state of the buttons
        buttonStateMap.put(SequencerDisplay.DisplayButton.PLAY, SequencerDisplay.ButtonState.DISABLED);
        buttonStateMap.put(SequencerDisplay.DisplayButton.EXIT, SequencerDisplay.ButtonState.ENABLED);
        buttonStateMap.put(SequencerDisplay.DisplayButton.SAVE, SequencerDisplay.ButtonState.ENABLED);
        buttonStateMap.put(SequencerDisplay.DisplayButton.TRACK_MUTE_MODE, SequencerDisplay.ButtonState.DISABLED);
        buttonStateMap.put(SequencerDisplay.DisplayButton.TRACK_SELECT_MODE, SequencerDisplay.ButtonState.ENABLED);

        display.initialize();
        display.displayAll(memory, buttonStateMap);
        timedDisplay();

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
            // TODO deal with this somehow
            launchpadClient.setPadLight(Pad.at(playingStepNumber % 8, playingStepNumber / 8), COLOR_EMPTY, BackBufferOperation.NONE);
        }
        playingStepNumber = Track.getStepCount()-1;
    }

    public void timedDisplay() {

        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (playing) {
                    advance(false);
                }
            }
        }, 125, 125);


    }

    private void advance(boolean andReset) {

        // reset display of current step
        launchpadClient.setPadLight(Pad.at(playingStepNumber % 8, playingStepNumber / 8), COLOR_EMPTY, BackBufferOperation.NONE);

        if (andReset) {
            playingStepNumber = 0;
        } else {
            playingStepNumber = (playingStepNumber + 1) % Track.getStepCount();
        }

        // send the midi notes
        for (Track track : memory.getSelectedPattern().getTracks()) {
            if (track.isEnabled()) {
                Step step = track.getStep(playingStepNumber);
                if (step.isOn()) {
                    sendMidiNote(track.getMidiChannel(), track.getNoteNumber(), step.getVelocity());
                }
            }
        }

        // display new step
        launchpadClient.setPadLight(Pad.at(playingStepNumber % 8, playingStepNumber / 8), COLOR_PLAYING, BackBufferOperation.NONE);

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
     * Launchpad display implementation
     *
     */
//    private void displayTrack(int index, Track track, boolean selected) {
//
//        int x = index % 8;
//        int y = TRACKS_MIN_ROW + index / 8;
//        if (selected) {
//            launchpadClient.setPadLight(Pad.at(x, y), COLOR_SELECTED, BackBufferOperation.NONE);
//            for (int i = 0; i < Track.getStepCount(); i++) {
//                displayStep(i, track.getStep(i));
//            }
//        } else {
//            if (track.isEnabled()) {
//                launchpadClient.setPadLight(Pad.at(x, y), COLOR_ENABLED, BackBufferOperation.NONE);
//            } else {
//                launchpadClient.setPadLight(Pad.at(x, y), COLOR_DISABLED, BackBufferOperation.NONE);
//            }
//
//        }
//
//    }
//
//    private void displayStep(int index, Step step) {
//
//        int x = index % 8;
//        int y = STEPS_MIN_ROW + index / 8;
//        if (step.isOn()) {
//            launchpadClient.setPadLight(Pad.at(x, y), COLOR_ENABLED, BackBufferOperation.NONE);
//        } else {
//            launchpadClient.setPadLight(Pad.at(x, y), COLOR_EMPTY, BackBufferOperation.NONE);
//        }
//
//    }
//
//    public void initializeDisplay() {
//
//        // turn everything off
//        launchpadClient.reset();
//
//        // turn off all buttons
//        for (int x = 0; x < 8; x++) {
////            launchpadClient.setButtonLight(Button.atTop(x), COLOR_EMPTY, BackBufferOperation.NONE);
////            launchpadClient.setButtonLight(Button.atRight(x), COLOR_EMPTY, BackBufferOperation.NONE);
//        }
//        launchpadClient.setButtonLight(BUTTON_SELECT, COLOR_SELECTED_DIM, BackBufferOperation.NONE);
//        launchpadClient.setButtonLight(BUTTON_PLAY, Color.of(0, 3), BackBufferOperation.NONE);
//        launchpadClient.setButtonLight(BUTTON_EXIT, Color.of(3, 0), BackBufferOperation.NONE);
//        launchpadClient.setButtonLight(BUTTON_SAVE, Color.of(1, 3), BackBufferOperation.NONE);
//
//        // turn off all pads
//        for (int x = 0; x < 8; x++) {
//            for (int y = 0; y < 8; y++) {
////                launchpadClient.setPadLight(Pad.at(x, y), COLOR_EMPTY, BackBufferOperation.NONE);
//            }
//        }
//
//        // display the track section
//        for (int y = TRACKS_MIN_ROW; y <= TRACKS_MAX_ROW; y++) {
//            for (int x = 0; x < 8; x++) {
//                launchpadClient.setPadLight(Pad.at(x, y), COLOR_SPLUNGE, BackBufferOperation.NONE);
//            }
//        }
//        displayTrack(8, selectedTrack, true);
//
//    }



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
                    // unselect the currently selected track
                    memory.getSelectedTrack().setSelected(false);
                    display.displayTrack(memory.getSelectedTrack());

                    // select the new track
                    selectedTrackNumber = trackNumber;
                    Track selectedTrack = memory.getSelectedPattern().getTrack(selectedTrackNumber);
                    memory.setSelectedTrack(selectedTrack);
                    selectedTrack.setSelected(true);
                    display.displayTrack(selectedTrack);

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
                step.setOn(!step.isOn());
                display.displayStep(step);

            } else if (pad.equals(TRACK_SELECT_MODE)) {
                trackSelectMode = true;
                display.displayButton(SequencerDisplay.DisplayButton.TRACK_MUTE_MODE, SequencerDisplay.ButtonState.DISABLED);
                display.displayButton(SequencerDisplay.DisplayButton.TRACK_SELECT_MODE, SequencerDisplay.ButtonState.ENABLED);

            } else if (pad.equals(TRACK_MUTE_MODE)) {
                trackSelectMode = false;
                display.displayButton(SequencerDisplay.DisplayButton.TRACK_MUTE_MODE, SequencerDisplay.ButtonState.ENABLED);
                display.displayButton(SequencerDisplay.DisplayButton.TRACK_SELECT_MODE, SequencerDisplay.ButtonState.DISABLED);
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
        }
    }


    @Override
    public void onButtonReleased(Button button, long timestamp) {

    }



}
