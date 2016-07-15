package net.perkowitz.sequence;

import net.thecodersbreakfast.lp4j.api.*;

import java.util.Map;

import static net.perkowitz.sequence.LaunchpadUtil.*;

/**
 * Created by optic on 7/10/16.
 */
public class LaunchpadDisplay implements SequencerDisplay {

    private LaunchpadClient launchpadClient;

    public LaunchpadDisplay(LaunchpadClient launchpadClient) {
        this.launchpadClient = launchpadClient;
    }

    public void initialize() {
        launchpadClient.reset();
    }

    public void displayAll(Memory memory, Map<DisplayButton, ButtonState> buttonStateMap) {

        Pattern pattern = memory.getSelectedPattern();
        for (Track track : pattern.getTracks()) {
            displayTrack(track);
        }

        if (buttonStateMap != null) {
            displayButtons(buttonStateMap);
        }

    }

    public void displayHelp() {

        // session buttons are yellow
        Color sessionColor = Color.of(1,2);
        for (int y = SESSIONS_MIN_ROW; y <= SESSIONS_MAX_ROW; y++) {
            for (int x = 0; x < 8; x++) {
                launchpadClient.setPadLight(Pad.at(x, y), sessionColor, BackBufferOperation.NONE);
            }
        }

        // pattern buttons are green
        Color patternColor = COLOR_PLAYING_DIM;
        for (int y = PATTERNS_MIN_ROW; y <= PATTERNS_MAX_ROW; y++) {
            for (int x = 0; x < 8; x++) {
                launchpadClient.setPadLight(Pad.at(x, y), patternColor, BackBufferOperation.NONE);
            }
        }
        launchpadClient.setPadLight(TRACK_MUTE_MODE, patternColor, BackBufferOperation.NONE);
        launchpadClient.setPadLight(TRACK_SELECT_MODE, patternColor, BackBufferOperation.NONE);

        // track buttons are orange
        Color trackColor = COLOR_SELECTED;
        for (int y = TRACKS_MIN_ROW; y <= TRACKS_MAX_ROW; y++) {
            for (int x = 0; x < 8; x++) {
                launchpadClient.setPadLight(Pad.at(x, y), trackColor, BackBufferOperation.NONE);
            }
        }
        launchpadClient.setPadLight(TRACK_MUTE_MODE, trackColor, BackBufferOperation.NONE);
        launchpadClient.setPadLight(TRACK_SELECT_MODE, trackColor, BackBufferOperation.NONE);

        // step buttons are red
        Color stepColor = COLOR_DISABLED;
        for (int y = STEPS_MIN_ROW; y <= STEPS_MAX_ROW; y++) {
            for (int x = 0; x < 8; x++) {
                launchpadClient.setPadLight(Pad.at(x, y), stepColor, BackBufferOperation.NONE);
            }
        }
        // step mode buttons
        launchpadClient.setPadLight(STEP_MUTE_MODE, stepColor, BackBufferOperation.NONE);
        launchpadClient.setPadLight(STEP_VELOCITY_MODE, stepColor, BackBufferOperation.NONE);
        launchpadClient.setPadLight(STEP_JUMP_MODE, stepColor, BackBufferOperation.NONE);
        launchpadClient.setPadLight(STEP_PLAY_MODE, stepColor, BackBufferOperation.NONE);

    }


    public void displayTrack(Track track) {

        int x = getX(track.getIndex());
        int y = LaunchpadUtil.TRACKS_MIN_ROW + getY(track.getIndex());
        if (track.isPlaying()) {
            if (track.isEnabled()) {
                launchpadClient.setPadLight(Pad.at(x, y), COLOR_PLAYING, BackBufferOperation.NONE);
            } else {
                launchpadClient.setPadLight(Pad.at(x, y), COLOR_PLAYING_DIM, BackBufferOperation.NONE);
            }
        } else if (track.isSelected()) {
            if (track.isEnabled()) {
                launchpadClient.setPadLight(Pad.at(x, y), COLOR_SELECTED, BackBufferOperation.NONE);
            } else {
                launchpadClient.setPadLight(Pad.at(x, y), COLOR_SELECTED_DIM, BackBufferOperation.NONE);
            }
            for (int i = 0; i < Track.getStepCount(); i++) {
                displayStep(track.getStep(i));
            }
        } else {
            if (track.isEnabled()) {
                launchpadClient.setPadLight(Pad.at(x, y), COLOR_DISABLED, BackBufferOperation.NONE);
            } else {
                launchpadClient.setPadLight(Pad.at(x, y), COLOR_EMPTY, BackBufferOperation.NONE);
            }
        }
    }

    public void displayStep(Step step) {
        int x = getX(step.getIndex());
        int y = STEPS_MIN_ROW + getY(step.getIndex());
//        if (step.isSelected()) {
//            launchpadClient.setPadLight(Pad.at(x, y), COLOR_SELECTED, BackBufferOperation.NONE);
//        } else if (step.isOn()) {
        if (step.isOn()) {
            launchpadClient.setPadLight(Pad.at(x, y), COLOR_ENABLED, BackBufferOperation.NONE);
        } else {
            launchpadClient.setPadLight(Pad.at(x, y), COLOR_EMPTY, BackBufferOperation.NONE);
        }
    }

    public void clearSteps() {
        for (int index = 0; index < Track.getStepCount(); index++) {
//            Step step = new Step(index);
//            displayStep(step);
            int x = getX(index);
            int y = STEPS_MIN_ROW + getY(index);
            launchpadClient.setPadLight(Pad.at(x, y), COLOR_EMPTY, BackBufferOperation.NONE);
        }
    }

    public void displayPlayingStep(int stepNumber) {
        int x = getX(stepNumber);
        int y = STEPS_MIN_ROW + getY(stepNumber);
        launchpadClient.setPadLight(Pad.at(x, y), COLOR_PLAYING, BackBufferOperation.NONE);
    }

    public void displayButton(DisplayButton displayButton, ButtonState buttonState) {

        switch(displayButton) {
            case PLAY:
                if (buttonState == ButtonState.ENABLED) {
                    launchpadClient.setButtonLight(BUTTON_PLAY, COLOR_PLAYING, BackBufferOperation.NONE);
                } else {
                    launchpadClient.setButtonLight(BUTTON_PLAY, COLOR_PLAYING_DIM, BackBufferOperation.NONE);
                }
                break;
            case EXIT:
                launchpadClient.setButtonLight(BUTTON_EXIT, COLOR_ENABLED, BackBufferOperation.NONE);
                break;
            case SAVE:
                launchpadClient.setButtonLight(BUTTON_SAVE, COLOR_ENABLED, BackBufferOperation.NONE);
                break;
            case TRACK_MUTE_MODE:
                if (buttonState == ButtonState.ENABLED) {
                    launchpadClient.setPadLight(LaunchpadUtil.TRACK_MUTE_MODE, COLOR_ENABLED, BackBufferOperation.NONE);
                } else {
                    launchpadClient.setPadLight(LaunchpadUtil.TRACK_MUTE_MODE, COLOR_DISABLED, BackBufferOperation.NONE);
                }
                break;
            case TRACK_SELECT_MODE:
                if (buttonState == ButtonState.ENABLED) {
                    launchpadClient.setPadLight(LaunchpadUtil.TRACK_SELECT_MODE, COLOR_SELECTED, BackBufferOperation.NONE);
                } else {
                    launchpadClient.setPadLight(LaunchpadUtil.TRACK_SELECT_MODE, COLOR_SELECTED_DIM, BackBufferOperation.NONE);
                }
                break;
            case STEP_MUTE_MODE:
                if (buttonState == ButtonState.ENABLED) {
                    launchpadClient.setPadLight(LaunchpadUtil.STEP_MUTE_MODE, COLOR_ENABLED, BackBufferOperation.NONE);
                } else {
                    launchpadClient.setPadLight(LaunchpadUtil.STEP_MUTE_MODE, COLOR_DISABLED, BackBufferOperation.NONE);
                }
                break;
            case STEP_VELOCITY_MODE:
                if (buttonState == ButtonState.ENABLED) {
                    launchpadClient.setPadLight(LaunchpadUtil.STEP_VELOCITY_MODE, COLOR_SELECTED, BackBufferOperation.NONE);
                } else {
                    launchpadClient.setPadLight(LaunchpadUtil.STEP_VELOCITY_MODE, COLOR_SELECTED_DIM, BackBufferOperation.NONE);
                }
                break;
            case STEP_JUMP_MODE:
                if (buttonState == ButtonState.ENABLED) {
                    launchpadClient.setPadLight(LaunchpadUtil.STEP_JUMP_MODE, COLOR_PLAYING, BackBufferOperation.NONE);
                } else {
                    launchpadClient.setPadLight(LaunchpadUtil.STEP_JUMP_MODE, COLOR_PLAYING_DIM, BackBufferOperation.NONE);
                }
                break;

            case STEP_PLAY_MODE:
                Color color = COLOR_PLAYING_DIM ;
                if (buttonState == ButtonState.ENABLED) {
                    color = COLOR_PLAYING;
                }
                launchpadClient.setPadLight(LaunchpadUtil.STEP_PLAY_MODE, color, BackBufferOperation.NONE);
                break;
        }

    }

    public void displayButtons(Map<DisplayButton, ButtonState> buttonStateMap) {
        for (Map.Entry<DisplayButton, ButtonState> entry : buttonStateMap.entrySet()) {
            displayButton(entry.getKey(), entry.getValue());
        }
    }

    public void displayValue(int value) {

        int buttons = (value * 8) / 128;

        for (int b = 0; b < 8; b++) {
            Color color = COLOR_EMPTY;
            if (b <= buttons) {
                color = COLOR_DISABLED;
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
