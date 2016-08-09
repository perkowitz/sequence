package net.perkowitz.sequence;

import static net.perkowitz.sequence.SequencerInterface.Mode.*;

/**
 * Created by mperkowi on 7/15/16.
 */
public interface SequencerInterface {

    public enum Mode {
        PLAY, EXIT, TEMPO, NO_VALUE,
        SAVE, LOAD, HELP,
        COPY, CLEAR,
        PATTERN_PLAY, PATTERN_EDIT,
        TRACK_MUTE, TRACK_EDIT,
        STEP_MUTE, STEP_VELOCITY, STEP_JUMP, STEP_PLAY
    }

    public static final Mode[] TRACK_MODES = new Mode[] { TRACK_MUTE, TRACK_EDIT };
    public static final Mode[] STEP_MODES = new Mode[] { STEP_MUTE, STEP_VELOCITY, STEP_JUMP, STEP_PLAY };

    public enum ValueMode {
        VELOCITY, TEMPO
    }

    public void selectSession(int index);
    public void selectPatterns(int minIndex, int maxIndex);
    public void selectTrack(int index);
    public void selectStep(int index);
    public void selectValue(int index);
    public void selectMode(Mode mode);

}
