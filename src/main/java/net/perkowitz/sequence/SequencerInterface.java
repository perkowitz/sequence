package net.perkowitz.sequence;

/**
 * Created by mperkowi on 7/15/16.
 */
public interface SequencerInterface {

    public enum Mode {
        PLAY, EXIT, SAVE, LOAD, HELP,
        COPY, CLEAR,
        PATTERN_PLAY, PATTERN_EDIT,
        TRACK_MUTE, TRACK_EDIT,
        STEP_MUTE, STEP_VELOCITY, STEP_JUMP, STEP_PLAY
    }

    public void selectSession(int index);
    public void selectPattern(int index);
    public void selectTrack(int index);
    public void selectStep(int index);
    public void selectValue(int index);
    public void selectMode(Mode mode);

}
