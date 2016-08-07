package net.perkowitz.sequence.launchpad;

import com.google.common.collect.Maps;
import net.perkowitz.sequence.SequencerInterface;
import net.thecodersbreakfast.lp4j.api.Button;
import net.thecodersbreakfast.lp4j.api.Color;
import net.thecodersbreakfast.lp4j.api.Pad;

import java.util.Map;

/**
 * Created by optic on 7/10/16.
 */
public class LaunchpadUtil {

//    public static int SESSIONS_MIN_ROW = 0;
//    public static int SESSIONS_MAX_ROW = 0;
    public static int PATTERNS_MIN_ROW = 0;
    public static int PATTERNS_MAX_ROW = 1;
    public static int FILLS_MIN_ROW = 2;
    public static int FILLS_MAX_ROW = 2;
    public static int MODE_ROW = 3;
    public static int TRACKS_MIN_ROW = 4;
    public static int TRACKS_MAX_ROW = 5;
    public static int STEPS_MIN_ROW = 6;
    public static int STEPS_MAX_ROW = 7;

    public static Color COLOR_EMPTY = Color.of(0,0);
    public static Color COLOR_ENABLED = Color.of(3,0);
    public static Color COLOR_DISABLED = Color.of(1,0);
    public static Color COLOR_SELECTED = Color.of(3,1);
    public static Color COLOR_SELECTED_DIM = Color.of(2,2);
    public static Color COLOR_PLAYING = Color.of(0,3);
    public static Color COLOR_PLAYING_DIM = Color.of(0,1);
    public static Color COLOR_PLAYING_SELECTED = Color.of(1,3);

    public static Map<SequencerInterface.Mode, Button> modeButtonMap = Maps.newHashMap();
    public static Map<SequencerInterface.Mode, Pad> modePadMap = Maps.newHashMap();
    static {
        modeButtonMap.put(SequencerInterface.Mode.PLAY, Button.RIGHT);
        modeButtonMap.put(SequencerInterface.Mode.EXIT, Button.MIXER);
        modeButtonMap.put(SequencerInterface.Mode.SAVE, Button.SESSION);
        modeButtonMap.put(SequencerInterface.Mode.LOAD, Button.USER_1);
        modeButtonMap.put(SequencerInterface.Mode.HELP, Button.USER_2);
//        modeButtonMap.put(SequencerInterface.Mode.COPY, Button.UP);
//        modeButtonMap.put(SequencerInterface.Mode.CLEAR, Button.DOWN);
        modeButtonMap.put(SequencerInterface.Mode.PATTERN_EDIT, Button.UP);

        int modeButtonRow = 3;
        modePadMap.put(SequencerInterface.Mode.TRACK_MUTE, Pad.at(0, modeButtonRow));
        modePadMap.put(SequencerInterface.Mode.TRACK_EDIT, Pad.at(1, modeButtonRow));
        modePadMap.put(SequencerInterface.Mode.STEP_MUTE, Pad.at(4, modeButtonRow));
        modePadMap.put(SequencerInterface.Mode.STEP_VELOCITY, Pad.at(5, modeButtonRow));
        modePadMap.put(SequencerInterface.Mode.STEP_JUMP, Pad.at(6, modeButtonRow));
        modePadMap.put(SequencerInterface.Mode.STEP_PLAY, Pad.at(7, modeButtonRow));
    }



}
