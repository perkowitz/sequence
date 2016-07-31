package net.perkowitz.sequence.launchpad;

import com.google.common.collect.Maps;
import net.perkowitz.sequence.SequencerDisplay;
import net.perkowitz.sequence.SequencerInterface;
import net.perkowitz.sequence.models.*;
import net.thecodersbreakfast.lp4j.api.*;

import java.util.Map;

import static net.perkowitz.sequence.launchpad.LaunchpadUtil.*;

/**
 * Created by optic on 7/10/16.
 */
public class LaunchpadDisplay implements SequencerDisplay {

    private LaunchpadClient launchpadClient;

    private static Map<SequencerInterface.Mode, Button> modeButtonMap = Maps.newHashMap();
    private static Map<SequencerInterface.Mode, Pad> modePadMap = Maps.newHashMap();
    static {
        modeButtonMap.put(SequencerInterface.Mode.PLAY, Button.RIGHT);
        modeButtonMap.put(SequencerInterface.Mode.EXIT, Button.MIXER);
        modeButtonMap.put(SequencerInterface.Mode.SAVE, Button.SESSION);
        modeButtonMap.put(SequencerInterface.Mode.LOAD, Button.USER_1);
        modeButtonMap.put(SequencerInterface.Mode.HELP, Button.USER_2);
        modeButtonMap.put(SequencerInterface.Mode.COPY, Button.UP);
        modeButtonMap.put(SequencerInterface.Mode.CLEAR, Button.DOWN);
        modeButtonMap.put(SequencerInterface.Mode.PATTERN_EDIT, Button.LEFT);

        int modeButtonRow = 3;
        modePadMap.put(SequencerInterface.Mode.TRACK_MUTE, Pad.at(0, modeButtonRow));
        modePadMap.put(SequencerInterface.Mode.TRACK_EDIT, Pad.at(1, modeButtonRow));
        modePadMap.put(SequencerInterface.Mode.STEP_MUTE, Pad.at(4, modeButtonRow));
        modePadMap.put(SequencerInterface.Mode.STEP_VELOCITY, Pad.at(5, modeButtonRow));
        modePadMap.put(SequencerInterface.Mode.STEP_JUMP, Pad.at(6, modeButtonRow));
        modePadMap.put(SequencerInterface.Mode.STEP_PLAY, Pad.at(7, modeButtonRow));
    }


    public LaunchpadDisplay(LaunchpadClient launchpadClient) {
        this.launchpadClient = launchpadClient;
    }

    public void initialize() {
        launchpadClient.reset();
    }

    public void displayAll(Memory memory, Map<SequencerInterface.Mode,Boolean> modeIsActiveMap) {

        Session session = memory.selectedSession();
        for (Pattern pattern : session.getPatterns()) {
            displayPattern(pattern);
        }

        Pattern pattern = memory.selectedPattern();
        for (Track track : pattern.getTracks()) {
            displayTrack(track);
        }

        if (modeIsActiveMap != null) {
            displayModes(modeIsActiveMap);
        }

    }

    public void displayHelp() {

        // session buttons are yellow
//        Color sessionColor = Color.of(1,2);
//        for (int y = SESSIONS_MIN_ROW; y <= SESSIONS_MAX_ROW; y++) {
//            for (int x = 0; x < 8; x++) {
//                launchpadClient.setPadLight(Pad.at(x, y), sessionColor, BackBufferOperation.NONE);
//            }
//        }

        // pattern buttons are green
        Color patternColor = LaunchpadUtil.COLOR_PLAYING_DIM;
        for (int y = LaunchpadUtil.PATTERNS_MIN_ROW; y <= LaunchpadUtil.PATTERNS_MAX_ROW; y++) {
            for (int x = 0; x < 8; x++) {
                launchpadClient.setPadLight(Pad.at(x, y), patternColor, BackBufferOperation.NONE);
            }
        }
        for (int y = LaunchpadUtil.FILLS_MIN_ROW; y <= LaunchpadUtil.FILLS_MAX_ROW; y++) {
            for (int x = 0; x < 8; x++) {
                launchpadClient.setPadLight(Pad.at(x, y), patternColor, BackBufferOperation.NONE);
            }
        }
        launchpadClient.setPadLight(LaunchpadUtil.TRACK_MUTE_MODE, patternColor, BackBufferOperation.NONE);
        launchpadClient.setPadLight(LaunchpadUtil.TRACK_SELECT_MODE, patternColor, BackBufferOperation.NONE);

        // track buttons are orange
        Color trackColor = LaunchpadUtil.COLOR_SELECTED_DIM;
        for (int y = LaunchpadUtil.TRACKS_MIN_ROW; y <= LaunchpadUtil.TRACKS_MAX_ROW; y++) {
            for (int x = 0; x < 8; x++) {
                launchpadClient.setPadLight(Pad.at(x, y), trackColor, BackBufferOperation.NONE);
            }
        }
        launchpadClient.setPadLight(LaunchpadUtil.TRACK_MUTE_MODE, trackColor, BackBufferOperation.NONE);
        launchpadClient.setPadLight(LaunchpadUtil.TRACK_SELECT_MODE, trackColor, BackBufferOperation.NONE);

        // step buttons are red
        Color stepColor = LaunchpadUtil.COLOR_DISABLED;
        for (int y = STEPS_MIN_ROW; y <= LaunchpadUtil.STEPS_MAX_ROW; y++) {
            for (int x = 0; x < 8; x++) {
                launchpadClient.setPadLight(Pad.at(x, y), stepColor, BackBufferOperation.NONE);
            }
        }
        // step mode buttons
        launchpadClient.setPadLight(LaunchpadUtil.STEP_MUTE_MODE, stepColor, BackBufferOperation.NONE);
        launchpadClient.setPadLight(LaunchpadUtil.STEP_VELOCITY_MODE, stepColor, BackBufferOperation.NONE);
        launchpadClient.setPadLight(LaunchpadUtil.STEP_JUMP_MODE, stepColor, BackBufferOperation.NONE);
        launchpadClient.setPadLight(LaunchpadUtil.STEP_PLAY_MODE, stepColor, BackBufferOperation.NONE);

    }

    public void displayPattern(Pattern pattern) {

        int x = getX(pattern.getIndex());
        int y = LaunchpadUtil.PATTERNS_MIN_ROW + getY(pattern.getIndex());
//        System.out.printf("displayPattern: %s, x=%d, y=%d, sel=%s, play=%s, next=%s\n", pattern, x, y, pattern.isSelected(), pattern.isPlaying(), pattern.isNext());

        Color color = COLOR_PLAYING_DIM;
        if (pattern.isSelected() && pattern.isPlaying()) {
            color = COLOR_PLAYING;
        } else if (pattern.isSelected()) {
            color = COLOR_SELECTED;
        } else if (pattern.isPlaying())  {
            color = COLOR_PLAYING;
        } else if (pattern.isNext())  {
            color = Color.of(2, 0);
        }

        launchpadClient.setPadLight(Pad.at(x, y), color, BackBufferOperation.NONE);

        if (pattern.isSelected()) {
            for (Track track : pattern.getTracks()) {
//                displayTrack(track);
            }
        }

    }


    public void displayTrack(Track track) {

        int x = getX(track.getIndex());
        int y = LaunchpadUtil.TRACKS_MIN_ROW + getY(track.getIndex());
//        System.out.printf("displayTrack: %s, x=%d, y=%d\n", track, x, y);
        if (track.isPlaying()) {
            if (track.isEnabled()) {
                launchpadClient.setPadLight(Pad.at(x, y), LaunchpadUtil.COLOR_PLAYING, BackBufferOperation.NONE);
            } else {
                launchpadClient.setPadLight(Pad.at(x, y), LaunchpadUtil.COLOR_SELECTED_DIM, BackBufferOperation.NONE);
            }
        } else if (track.isSelected()) {
            if (track.isEnabled()) {
                launchpadClient.setPadLight(Pad.at(x, y), LaunchpadUtil.COLOR_SELECTED, BackBufferOperation.NONE);
            } else {
                launchpadClient.setPadLight(Pad.at(x, y), LaunchpadUtil.COLOR_SELECTED_DIM, BackBufferOperation.NONE);
            }
            for (int i = 0; i < Track.getStepCount(); i++) {
                displayStep(track.getStep(i));
            }
        } else {
            if (track.isEnabled()) {
                launchpadClient.setPadLight(Pad.at(x, y), LaunchpadUtil.COLOR_DISABLED, BackBufferOperation.NONE);
            } else {
                launchpadClient.setPadLight(Pad.at(x, y), LaunchpadUtil.COLOR_EMPTY, BackBufferOperation.NONE);
            }
        }
    }

    public void displayStep(Step step) {
        int x = getX(step.getIndex());
        int y = STEPS_MIN_ROW + getY(step.getIndex());
//        System.out.printf("displayStep: %s, x=%d, y=%d\n", step, x, y);
//        if (step.isSelected()) {
//            launchpadClient.setPadLight(Pad.at(x, y), COLOR_SELECTED, BackBufferOperation.NONE);
//        } else if (step.isOn()) {
        if (step.isOn()) {
            launchpadClient.setPadLight(Pad.at(x, y), COLOR_ENABLED, BackBufferOperation.NONE);
        } else {
            launchpadClient.setPadLight(Pad.at(x, y), LaunchpadUtil.COLOR_EMPTY, BackBufferOperation.NONE);
        }
    }

    public void clearSteps() {
        for (int index = 0; index < Track.getStepCount(); index++) {
//            Step step = new Step(index);
//            displayStep(step);
            int x = getX(index);
            int y = STEPS_MIN_ROW + getY(index);
            launchpadClient.setPadLight(Pad.at(x, y), LaunchpadUtil.COLOR_EMPTY, BackBufferOperation.NONE);
        }
    }

    public void displayPlayingStep(int stepNumber) {
        int x = getX(stepNumber);
        int y = STEPS_MIN_ROW + getY(stepNumber);
        launchpadClient.setPadLight(Pad.at(x, y), COLOR_PLAYING, BackBufferOperation.NONE);
    }

    public void displayMode(SequencerInterface.Mode mode, boolean isActive) {

        Color color = Color.of(2,1);
        if (isActive) {
            color = COLOR_PLAYING;
        }

        if (modeButtonMap.get(mode) != null) {
            launchpadClient.setButtonLight(modeButtonMap.get(mode), color, BackBufferOperation.NONE);
        } else if (modePadMap.get(mode) != null) {
            launchpadClient.setPadLight(modePadMap.get(mode), color, BackBufferOperation.NONE);
        }

    }

    public void displayModes(Map<SequencerInterface.Mode,Boolean> modeIsActiveMap) {
        for (Map.Entry<SequencerInterface.Mode, Boolean> entry : modeIsActiveMap.entrySet()) {
            displayMode(entry.getKey(), entry.getValue());
        }
    }

    public void displayModeChoice(SequencerInterface.Mode mode, SequencerInterface.Mode[] modeChoices) {
        for (SequencerInterface.Mode modeChoice : modeChoices) {
            if (modeChoice == mode) {
                displayMode(modeChoice, true);
            } else {
                displayMode(modeChoice, false);
            }
        }
    }

    public void displayValue(int value) {

        int buttons = (value * 8) / 128;

        for (int b = 0; b < 8; b++) {
            Color color = LaunchpadUtil.COLOR_EMPTY;
            if (b <= buttons) {
                color = LaunchpadUtil.COLOR_DISABLED;
            }
            launchpadClient.setButtonLight(Button.atRight(7-b), color, BackBufferOperation.NONE);
        }

    }


    /***** private implementation ***************************************************************/

    private int getX(int index) {
        return index % 8;
    }

    private int getY(int index) {
        return index / 8;
    }


}
