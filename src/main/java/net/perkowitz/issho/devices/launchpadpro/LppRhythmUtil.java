package net.perkowitz.issho.devices.launchpadpro;

import com.google.common.collect.Maps;
import net.perkowitz.issho.devices.*;
import net.perkowitz.sequence.SequencerInterface;

import java.util.Map;



/**
 * Created by optic on 7/10/16.
 */
public class LppRhythmUtil {

    public static boolean debugMode = true;

    // settings module
    public static int SESSIONS_MIN_ROW = 0;
    public static int SESSIONS_MAX_ROW = 1;
    public static int LOAD_ROW = 2;
    public static int SAVE_ROW = 3;
    public static int SWITCHES_ROW = 7;

    // sequence module
    public static int PATTERNS_MIN_ROW = 0;
    public static int PATTERNS_MAX_ROW = 1;
    public static int FILLS_MIN_ROW = 2;
    public static int FILLS_MAX_ROW = 2;
    public static int MODE_ROW = 3;
    public static int TRACKS_MIN_ROW = 4;
    public static int TRACKS_MAX_ROW = 5;
    public static int STEPS_MIN_ROW = 6;
    public static int STEPS_MAX_ROW = 7;

    // display colors
    public static Color COLOR_STEP = Color.OFF;
    public static Color COLOR_STEP_ON = Color.MED_GRAY;
    public static Color COLOR_STEP_PLAYING = Color.BRIGHT_GREEN;

    public static Color COLOR_TRACK = Color.DARK_GRAY;
    public static Color COLOR_TRACK_SELECTED = Color.WHITE;
    public static Color COLOR_TRACK_PLAYING = Color.BRIGHT_GREEN;
    public static Color COLOR_TRACK_MUTED = Color.OFF;
    public static Color COLOR_TRACK_MUTED_PLAYING = Color.DIM_GREEN;
    public static Color COLOR_TRACK_MUTED_SELECTED = Color.LIGHT_GRAY;

    public static Color COLOR_PATTERN = Color.DIM_BLUE;
    public static Color COLOR_PATTERN_SELECTED = Color.WHITE;
    public static Color COLOR_PATTERN_PLAYING = Color.DIM_GREEN;
    public static Color COLOR_PATTERN_CHAINED = Color.DARK_GRAY;
    public static Color COLOR_PATTERN_SELECTED_PLAYING = Color.BRIGHT_GREEN;

    public static Color COLOR_MODE_ACTIVE = Color.LIGHT_BLUE;
    public static Color COLOR_MODE_INACTIVE = Color.DARK_BLUE;

    public static Color COLOR_VALUE = Color.OFF;

    public static Map<SequencerInterface.Mode, GridButton> modeButtonMap = Maps.newHashMap();
    public static Map<SequencerInterface.Mode, GridPad> modePadMap = Maps.newHashMap();
    static {
        modeButtonMap.put(SequencerInterface.Mode.PLAY, GridButton.at(GridButton.Side.Top, 3));
        modeButtonMap.put(SequencerInterface.Mode.EXIT, GridButton.at(GridButton.Side.Top, 7));
        modeButtonMap.put(SequencerInterface.Mode.SAVE, GridButton.at(GridButton.Side.Top, 4));
        modeButtonMap.put(SequencerInterface.Mode.TEMPO, GridButton.at(GridButton.Side.Top, 2));
        modeButtonMap.put(SequencerInterface.Mode.SEQUENCE, GridButton.at(GridButton.Side.Top, 5));
        modeButtonMap.put(SequencerInterface.Mode.SETTINGS, GridButton.at(GridButton.Side.Top, 6));
//        modeButtonMap.put(SequencerInterface.Mode.COPY, Button.UP);
//        modeButtonMap.put(SequencerInterface.Mode.CLEAR, Button.DOWN);
        modeButtonMap.put(SequencerInterface.Mode.PATTERN_EDIT, GridButton.at(GridButton.Side.Top, 0));

        modePadMap.put(SequencerInterface.Mode.TRACK_MUTE, GridPad.at(0, MODE_ROW));
        modePadMap.put(SequencerInterface.Mode.TRACK_EDIT, GridPad.at(1, MODE_ROW));
        modePadMap.put(SequencerInterface.Mode.STEP_MUTE, GridPad.at(4, MODE_ROW));
        modePadMap.put(SequencerInterface.Mode.STEP_VELOCITY, GridPad.at(5, MODE_ROW));
        modePadMap.put(SequencerInterface.Mode.STEP_JUMP, GridPad.at(6, MODE_ROW));
        modePadMap.put(SequencerInterface.Mode.STEP_PLAY, GridPad.at(7, MODE_ROW));
    }

    public static Map<SequencerInterface.Switch, GridPad> switchPadMap = Maps.newHashMap();
    static {
        switchPadMap.put(SequencerInterface.Switch.INTERNAL_CLOCK_ENABLED, GridPad.at(5, SWITCHES_ROW));
        switchPadMap.put(SequencerInterface.Switch.MIDI_CLOCK_ENABLED, GridPad.at(6, SWITCHES_ROW));
        switchPadMap.put(SequencerInterface.Switch.TRIGGER_ENABLED, GridPad.at(7, SWITCHES_ROW));
    }


}
