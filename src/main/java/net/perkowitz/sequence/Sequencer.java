package net.perkowitz.sequence;

import net.thecodersbreakfast.lp4j.api.*;
import net.thecodersbreakfast.lp4j.midi.MidiDeviceConfiguration;
import net.thecodersbreakfast.lp4j.midi.MidiLaunchpad;

import javax.sound.midi.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

/**
 * Created by optic on 7/8/16.
 */
public class Sequencer extends LaunchpadListenerAdapter {

    private static int TRACKS_MIN_ROW = 4;
    private static int TRACKS_MAX_ROW = 5;
    private static int STEPS_MIN_ROW = 6;
    private static int STEPS_MAX_ROW = 7;

    private static Color COLOR_EMPTY = Color.of(0,0);
    private static Color COLOR_ENABLED = Color.of(1,0);
    private static Color COLOR_DISABLED = Color.of(3,0);
    private static Color COLOR_SELECTED = Color.of(3,3);
    private static Color COLOR_PLAYING = Color.of(0,3);


    private MidiDevice controllerInput;
    private MidiDevice controllerOutput;
    private MidiDevice sequenceOutput;
    private Launchpad launchpad;
    private LaunchpadClient launchpadClient;

    private Memory memory;
    private Session selectedSession;
    private Pattern selectedPattern;
    private Track selectedTrack;
    private int selectedTrackNumber = 0;

    private static CountDownLatch stop = new CountDownLatch(1);

    private int timerCount = 0;


    public Sequencer(MidiDevice controllerInput, MidiDevice controllerOutput, MidiDevice sequenceOutput) throws Exception {

        this.controllerInput = controllerInput;
        this.controllerOutput = controllerOutput;
        this.sequenceOutput = sequenceOutput;

        try {

            launchpad = new MidiLaunchpad(new MidiDeviceConfiguration(controllerInput, controllerOutput));
            launchpadClient = launchpad.getClient();
//            launchpad.setListener(new ControlListener(this, launchpadClient));
            launchpad.setListener(this);

        } catch (MidiUnavailableException e) {
            System.err.printf("%s\n", e.getStackTrace().toString());
        }

        memory = new Memory();
        selectedSession = memory.getSession(0);
        selectedPattern = selectedSession.getPattern(0);
        selectedTrack = selectedPattern.getTrack(0);
        selectedTrack.setStep(0, true);
        selectedTrack.setStep(4, true);
        selectedTrack.setStep(8, true);
        selectedTrack.setStep(12, true);

        warmupDisplay();
        timedDisplay();

        stop.await();

    }

    public void shutdown() {
        System.exit(0);
    }

    public void warmupDisplay() {

        launchpadClient.reset();

        // turn off all buttons
        for (int x = 0; x < 8; x++) {
            launchpadClient.setButtonLight(Button.atTop(x), COLOR_EMPTY, BackBufferOperation.NONE);
            launchpadClient.setButtonLight(Button.atRight(x), COLOR_EMPTY, BackBufferOperation.NONE);
        }
        launchpadClient.setButtonLight(Button.UP, COLOR_EMPTY, BackBufferOperation.NONE); // somehow the loop doesn't get it

        // turn off all pads
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                launchpadClient.setPadLight(Pad.at(x, y), COLOR_EMPTY, BackBufferOperation.NONE);
            }
        }

    }

    public void timedDisplay() {

        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                launchpadClient.setPadLight(Pad.at(timerCount % 8, timerCount  / 8), COLOR_EMPTY, BackBufferOperation.NONE);
                timerCount = (timerCount+1) % 16;
                launchpadClient.setPadLight(Pad.at(timerCount % 8, timerCount / 8), COLOR_PLAYING, BackBufferOperation.NONE);


            }
        }, 125, 125);


    }

    /************************************************************************
     * Launchpad display implementation
     *
     */
    private void renderTrack(int index, Track track, boolean selected) {

        int x = index % 8;
        int y = TRACKS_MIN_ROW + index / 8;
        if (selected) {
            launchpadClient.setPadLight(Pad.at(x, y), COLOR_SELECTED, BackBufferOperation.NONE);
            for (int i = 0; i < Track.getStepCount(); i++) {
                renderStep(i, track.getStep(i));
            }
        } else {
            launchpadClient.setPadLight(Pad.at(x, y), COLOR_EMPTY, BackBufferOperation.NONE);

        }

    }

    private void renderStep(int index, Step step) {

        int x = index % 8;
        int y = STEPS_MIN_ROW + index / 8;
        if (step.isOn()) {
            launchpadClient.setPadLight(Pad.at(x, y), COLOR_ENABLED, BackBufferOperation.NONE);
        } else {
            launchpadClient.setPadLight(Pad.at(x, y), COLOR_EMPTY, BackBufferOperation.NONE);
        }

    }

    /************************************************************************
     * Launchpad listener implementation
     *
     */
    @Override
    public void onPadPressed(Pad pad, long timestamp) {
//        launchpadClient.setPadLight(pad, COLOR_SELECTED, BackBufferOperation.NONE);
//        System.out.printf("Pressed pad %s\n", pad.toString());

        try {
            if (pad.getY() >= TRACKS_MIN_ROW && pad.getY() <= TRACKS_MAX_ROW) {
                // update the display of the currently selected track
                renderTrack(selectedTrackNumber, selectedTrack, false);

                // selected the new track
                selectedTrackNumber = pad.getX() + (pad.getY() - TRACKS_MIN_ROW) * 8;
                selectedTrack = selectedPattern.getTrack(selectedTrackNumber);
                renderTrack(selectedTrackNumber, selectedTrack, true);
                System.out.printf("Selected track %d\n", selectedTrackNumber);

            } else if (pad.getY() >= STEPS_MIN_ROW && pad.getY() <= STEPS_MAX_ROW) {
                int stepNumber = pad.getX() + (pad.getY() - STEPS_MIN_ROW) * 8;
                Step step = selectedTrack.getStep(stepNumber);
                step.setOn(!step.isOn());
                System.out.printf("Toggled step %d from %b to %b\n", stepNumber, step.isOn(), !step.isOn());
                renderStep(stepNumber, step);
            }



        } catch (Exception e) {
            System.err.println(e.toString());
        }

    }

    @Override
    public void onPadReleased(Pad pad, long timestamp) {
//        launchpadClient.setPadLight(pad, COLOR_EMPTY, BackBufferOperation.NONE);
//        System.out.printf("Released pad %s\n", pad.toString());

    }

    @Override
    public void onButtonReleased(Button button, long timestamp) {

        if (button.equals(Button.UP)) {
            shutdown();
        }
    }



}
