package net.perkowitz.sequence;

import java.util.Map;

/**
 * Created by optic on 7/10/16.
 */
public interface SequencerDisplay {

    public enum DisplayButton { PLAY, EXIT, SAVE, TRACK_MUTE_MODE, TRACK_SELECT_MODE }
    public enum ButtonState { EMPTY, ENABLED, DISABLED, SELECTED, PLAYING, PLAYING_SELECTED }

    public void initialize();
    public void displayAll(Memory memory, Map<DisplayButton, ButtonState> buttonStateMap);
    public void displayTrack(Track track);
    public void displayStep(Step step);
    public void displayButton(DisplayButton displayButton, ButtonState buttonState);
    public void displayButtons(Map<DisplayButton, ButtonState> buttonStateMap);

}
