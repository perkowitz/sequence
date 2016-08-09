package net.perkowitz.sequence;

import net.perkowitz.sequence.models.Memory;
import net.perkowitz.sequence.models.Pattern;
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
    public void displayAll(Memory memory, Map<SequencerInterface.Mode,Boolean> modeIsActiveMap);
    public void displayHelp();

    public void displayPattern(Pattern pattern);

    public void displayTrack(Track track, boolean displaySteps);
    public void displayTrack(Track track);

    public void displayStep(Step step);
    public void clearSteps();
    public void displayPlayingStep(int stepNumber);

    public void displayMode(SequencerInterface.Mode mode, boolean isActive);
    public void displayModes(Map<SequencerInterface.Mode,Boolean> modeIsActiveMap);
    public void displayModeChoice(SequencerInterface.Mode mode, SequencerInterface.Mode[] modeChoices);

    public void clearValue();
    public void displayValue(int value, int minValue, int maxValue, SequencerInterface.ValueMode valueMode);

}
