package net.perkowitz.sequence;

import net.perkowitz.sequence.models.Memory;
import net.perkowitz.sequence.models.Step;
import net.perkowitz.sequence.models.Track;

import java.util.Map;

/**
 * Created by optic on 7/10/16.
 */
public interface SequencerDisplay {

    public enum DisplayButton {
        PLAY, EXIT, SAVE, HELP,
        TRACK_MUTE_MODE, TRACK_SELECT_MODE,
        STEP_MUTE_MODE, STEP_VELOCITY_MODE, STEP_JUMP_MODE, STEP_PLAY_MODE
    }
    public enum ButtonState { EMPTY, ENABLED, DISABLED, SELECTED, PLAYING, PLAYING_SELECTED }

    public void initialize();
    public void displayAll(Memory memory, Map<DisplayButton, ButtonState> buttonStateMap);
    public void displayHelp();

    public void displayTrack(Track track);

    public void displayStep(Step step);
    public void clearSteps();

    public void displayButton(DisplayButton displayButton, ButtonState buttonState);
    public void displayButtons(Map<DisplayButton, ButtonState> buttonStateMap);

    public void displayValue(int value);

}
