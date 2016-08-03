package net.perkowitz.sequence.models;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by optic on 7/9/16.
 */
public class Memory {

    @Getter @Setter private static int sessionCount = 8;
    @Getter private Session[] sessions;

    @Setter private int selectedSessionIndex;
    @Setter private int selectedPatternIndex;
    @Setter private int selectedTrackIndex;
    @Setter private int selectedStepIndex;

    @Getter @Setter private int playingPatternIndex;// the currently playing pattern (which might not be in the range, if a new one has been selected)
    @Getter @Setter private int patternRangeMin;    // the index of the first of the playing pattern range
    @Getter @Setter private int patternRangeMax;    // the index of the last of the pattern range
    @Getter @Setter private int patternRangeIndex;  // the index of the NEXT pattern to play

    public Memory() {

        this.sessions = new Session[sessionCount];
        for (int i = 0; i < sessionCount; i++) {
            sessions[i] = new Session(i);
        }

        select(getSession(0));
        select(selectedSession().getPattern(0));
        select(selectedPattern().getTrack(8));
        setPatternRange(0, 0, 0);
        playingPatternIndex = 0;

    }

    public Session selectedSession() {
        return sessions[selectedSessionIndex];
    }

    public Pattern selectedPattern() {
        return selectedSession().getPattern(selectedPatternIndex);
    }

    public Track selectedTrack() {
        return selectedPattern().getTrack(selectedTrackIndex);
    }

    public Step selectedStep() {
        return selectedTrack().getStep(selectedStepIndex);
    }

    public Pattern playingPattern() {
        return selectedSession().getPattern(playingPatternIndex);
    }

    public Session getSession(int index) {
        return sessions[index % sessionCount];
    }

    public void select(Session session) {
        Session selectedSession = selectedSession();
        if (selectedSession != null) {
            selectedSession.setSelected(false);
        }
        selectedSession = session;
        selectedSession.setSelected(true);
    }

    public void select(Pattern pattern) {
        Pattern selectedPattern = selectedPattern();
        if (selectedPattern != null) {
            selectedPattern.setSelected(false);
        }
        pattern.setSelected(true);
        selectedPatternIndex = pattern.getIndex();
    }

    public void select(Track track) {
//        Track selectedTrack = selectedTrack();
//        if (selectedTrack != null) {
//            selectedTrack.setSelected(false);
//        }
        setSelectedTrackIndex(track.getIndex());
        for (Track t : selectedPattern().getTracks()) {
            t.setSelected(false);
        }
        track.setSelected(true);
    }

    public void select(Step step) {
        Step selectedStep = selectedStep();
        if (selectedStep != null) {
            selectedStep.setSelected(false);
        }
        selectedStep = step;
        selectedStep.setSelected(true);
    }

    public void setPattern(int index) {
        setPatternRange(index, index, index);
    }

    public void setPatternRange(int min, int max, int index) {

        for (int i = patternRangeMin; i <= patternRangeMax; i++ ) {
            selectedSession().getPattern(i).setChained(false);
        }

        patternRangeMin = min;
        patternRangeMax = max;
        patternRangeIndex = index;
        for (int i = patternRangeMin; i <= patternRangeMax; i++ ) {
            selectedSession().getPattern(i).setChained(true);
        }
    }

    public List<Pattern> getPatternRange() {
        List<Pattern> patterns = Lists.newArrayList();
        for (int i = patternRangeMin; i <= patternRangeMax; i++ ) {
            patterns.add(selectedSession().getPattern(i));
        }
        return patterns;
    }

    public Pattern getNextPattern() {
        return selectedSession().getPattern(patternRangeIndex);
    }


    public Pattern advancePattern() {

        Pattern currentPattern = playingPattern();
        currentPattern.setPlaying(false);

        playingPatternIndex = patternRangeIndex;
        patternRangeIndex++;
        if (patternRangeIndex > patternRangeMax) {
            patternRangeIndex = patternRangeMin;
        }
        Pattern pattern = playingPattern();
        pattern.setPlaying(true);

        return pattern;
    }


}
