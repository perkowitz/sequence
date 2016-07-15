package net.perkowitz.sequence;

import net.thecodersbreakfast.lp4j.api.Button;
import net.thecodersbreakfast.lp4j.api.Color;
import net.thecodersbreakfast.lp4j.api.Pad;

/**
 * Created by optic on 7/10/16.
 */
public class LaunchpadUtil {

    public static int SESSIONS_MIN_ROW = 0;
    public static int SESSIONS_MAX_ROW = 0;
    public static int PATTERNS_MIN_ROW = 1;
    public static int PATTERNS_MAX_ROW = 2;
    public static int TRACKS_MIN_ROW = 4;
    public static int TRACKS_MAX_ROW = 5;
    public static int STEPS_MIN_ROW = 6;
    public static int STEPS_MAX_ROW = 7;

    public static Color COLOR_EMPTY = Color.of(0,0);
    public static Color COLOR_ENABLED = Color.of(3,0);
    public static Color COLOR_DISABLED = Color.of(1,0);
    public static Color COLOR_SELECTED = Color.of(3,1);
    public static Color COLOR_SELECTED_DIM = Color.of(1,1);
    public static Color COLOR_PLAYING = Color.of(0,3);
    public static Color COLOR_PLAYING_DIM = Color.of(0,1);
    public static Color COLOR_PLAYING_SELECTED = Color.of(1,3);

    public static Button BUTTON_PLAY = Button.RIGHT;
    public static Button BUTTON_EXIT = Button.MIXER;
    public static Button BUTTON_SAVE = Button.SESSION;
    public static Button BUTTON_HELP = Button.UP;

    public static int MODE_ROW = 3;
    public static Pad TRACK_MUTE_MODE = Pad.at(0, MODE_ROW);
    public static Pad TRACK_SELECT_MODE = Pad.at(1, MODE_ROW);
    public static Pad STEP_MUTE_MODE = Pad.at(4, MODE_ROW);
    public static Pad STEP_VELOCITY_MODE = Pad.at(5, MODE_ROW);
    public static Pad STEP_JUMP_MODE = Pad.at(6, MODE_ROW);
    public static Pad STEP_PLAY_MODE = Pad.at(7, MODE_ROW);

}
