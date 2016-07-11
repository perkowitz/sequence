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

        displayButtons(buttonStateMap);

    }

    public void displayTrack(Track track) {

        int x = getX(track.getIndex());
        int y = LaunchpadUtil.TRACKS_MIN_ROW + getY(track.getIndex());
        if (track.isSelected()) {
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
                launchpadClient.setPadLight(Pad.at(x, y), COLOR_ENABLED, BackBufferOperation.NONE);
            } else {
                launchpadClient.setPadLight(Pad.at(x, y), COLOR_DISABLED, BackBufferOperation.NONE);
            }

        }
    }

    public void displayStep(Step step) {
        int x = getX(step.getIndex());
        int y = STEPS_MIN_ROW + getY(step.getIndex());
        if (step.isSelected()) {
            launchpadClient.setPadLight(Pad.at(x, y), COLOR_SELECTED, BackBufferOperation.NONE);
        } else if (step.isOn()) {
            launchpadClient.setPadLight(Pad.at(x, y), COLOR_ENABLED, BackBufferOperation.NONE);
        } else {
            launchpadClient.setPadLight(Pad.at(x, y), COLOR_EMPTY, BackBufferOperation.NONE);
        }
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
        }

    }

    public void displayButtons(Map<DisplayButton, ButtonState> buttonStateMap) {
        for (Map.Entry<DisplayButton, ButtonState> entry : buttonStateMap.entrySet()) {
            displayButton(entry.getKey(), entry.getValue());
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
