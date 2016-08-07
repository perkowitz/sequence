package net.perkowitz.sequence.launchpad;

import com.google.common.collect.Sets;
import net.perkowitz.sequence.SequencerController;
import net.perkowitz.sequence.SequencerInterface;
import net.thecodersbreakfast.lp4j.api.Button;
import net.thecodersbreakfast.lp4j.api.LaunchpadListenerAdapter;
import net.thecodersbreakfast.lp4j.api.Pad;

import java.util.Set;

import static net.perkowitz.sequence.launchpad.LaunchpadUtil.*;

/**
 * Created by mperkowi on 7/15/16.
 */
public class LaunchpadController extends LaunchpadListenerAdapter implements SequencerController {

    private SequencerInterface sequencer = null;

    private Set<Integer> patternsPressed = Sets.newHashSet();
    private int patternsReleasedCount = 0;

    public LaunchpadController() {
    }

    public void setSequencer(SequencerInterface sequencer) {
        this.sequencer = sequencer;
    }

    @Override
    public void onPadPressed(Pad pad, long timestamp) {

//        System.out.printf("onPadPressed: %s, %s\n", pad, timestamp);

        try {
            if (pad.getY() >= PATTERNS_MIN_ROW && pad.getY() <= PATTERNS_MAX_ROW) {
                // pressing a pattern pad
                int index = pad.getX() + (pad.getY() - PATTERNS_MIN_ROW) * 8;
                patternsPressed.add(index);

            } else if (pad.getY() >= TRACKS_MIN_ROW && pad.getY() <= TRACKS_MAX_ROW) {
                // pressing a track pad
                int index = pad.getX() + (pad.getY() - TRACKS_MIN_ROW) * 8;
                sequencer.selectTrack(index);

            } else if (pad.getY() >= STEPS_MIN_ROW && pad.getY() <= STEPS_MAX_ROW) {
                // pressing a step pad
                int index = pad.getX() + (pad.getY() - STEPS_MIN_ROW) * 8;
                sequencer.selectStep(index);

            } else if (pad.equals(modePadMap.get(SequencerInterface.Mode.TRACK_MUTE))) {
                sequencer.selectMode(SequencerInterface.Mode.TRACK_MUTE);

            } else if (pad.equals(modePadMap.get(SequencerInterface.Mode.TRACK_EDIT))) {
                sequencer.selectMode(SequencerInterface.Mode.TRACK_EDIT);

            } else if (pad.equals(modePadMap.get(SequencerInterface.Mode.STEP_MUTE))) {
                sequencer.selectMode(SequencerInterface.Mode.STEP_MUTE);

            } else if (pad.equals(modePadMap.get(SequencerInterface.Mode.STEP_VELOCITY))) {
                sequencer.selectMode(SequencerInterface.Mode.STEP_VELOCITY);

            } else if (pad.equals(modePadMap.get(SequencerInterface.Mode.STEP_JUMP))) {
                sequencer.selectMode(SequencerInterface.Mode.STEP_JUMP);

            } else if (pad.equals(modePadMap.get(SequencerInterface.Mode.STEP_PLAY))) {
                sequencer.selectMode(SequencerInterface.Mode.STEP_PLAY);

            }

        } catch (Exception e) {
            System.err.println(e.toString());
        }

    }


    @Override
    public void onPadReleased(Pad pad, long timestamp) {

//        System.out.printf("onPadReleased: %s, %s\n", pad, timestamp);

        try {
            if (pad.getY() >= PATTERNS_MIN_ROW && pad.getY() <= PATTERNS_MAX_ROW) {
                // releasing a pattern pad
                // don't activate until the last pattern pad is released (so additional releases don't look like a new press/release)
                patternsReleasedCount++;
                if (patternsReleasedCount >= patternsPressed.size()) {
                    int index = pad.getX() + (pad.getY() - PATTERNS_MIN_ROW) * 8;
                    patternsPressed.add(index); // just to make sure
                    if (patternsPressed.size() == 1) {
                        sequencer.selectPatterns(index, index);
                    } else {
                        int min = 100;
                        int max = -1;
                        for (Integer pattern : patternsPressed) {
                            if (pattern < min) {
                                min = pattern;
                            }
                            if (pattern > max) {
                                max = pattern;
                            }
                        }
                        sequencer.selectPatterns(min, max);
                    }
                    patternsPressed.clear();
                    patternsReleasedCount = 0;
                }
            }

        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    @Override
    public void onButtonPressed(Button button, long timestamp) {

        System.out.printf("onButtonPressed: %s, %s\n", button, timestamp);

        if (button.equals(modeButtonMap.get(SequencerInterface.Mode.PLAY))) {
            sequencer.selectMode(SequencerInterface.Mode.PLAY);

        } else if (button.equals(modeButtonMap.get(SequencerInterface.Mode.EXIT))) {
            sequencer.selectMode(SequencerInterface.Mode.EXIT);

        } else if (button.equals(modeButtonMap.get(SequencerInterface.Mode.TEMPO))) {
            sequencer.selectMode(SequencerInterface.Mode.TEMPO);

        } else if (button.equals(modeButtonMap.get(SequencerInterface.Mode.SAVE))) {
            sequencer.selectMode(SequencerInterface.Mode.SAVE);

//        } else if (button.equals(modeButtonMap.get(SequencerInterface.Mode.HELP))) {
//            sequencer.selectMode(SequencerInterface.Mode.HELP);
//
        } else if (button.equals(modeButtonMap.get(SequencerInterface.Mode.PATTERN_EDIT))) {
            sequencer.selectMode(SequencerInterface.Mode.PATTERN_EDIT);

        } else if (button.isRightButton()) {
            // pressing one of the value buttons
            int index = 7 - button.getCoordinate();
            sequencer.selectValue(index);
        }
    }

    @Override
    public void onButtonReleased(Button button, long timestamp) {

        if (button.equals(modeButtonMap.get(SequencerInterface.Mode.PATTERN_EDIT))) {
            sequencer.selectMode(SequencerInterface.Mode.PATTERN_PLAY);

        } else if (button.equals(modeButtonMap.get(SequencerInterface.Mode.TEMPO))) {
            sequencer.selectMode(SequencerInterface.Mode.NO_VALUE);

        }

    }


}
