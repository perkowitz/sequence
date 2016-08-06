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

    @Getter private int selectedSessionIndex;
    @Getter private int selectedPatternIndex;
    @Getter private int selectedTrackIndex;
    @Getter private int selectedStepIndex;

    @Getter private int playingPatternIndex;// the currently playing pattern (which might not be in the chain, if a new one has been selected)
    @Getter private int patternChainMin;    // the index of the first of the playing pattern chain
    @Getter private int patternChainMax;    // the index of the last of the pattern chain
    @Getter private int patternChainIndex;  // the index of the NEXT pattern to play

    @Getter @Setter private boolean specialSelected = false;
    @Getter @Setter private boolean copyMutesToNew = true;

    public Memory() {

        this.sessions = new Session[sessionCount];
        for (int i = 0; i < sessionCount; i++) {
            sessions[i] = new Session(i);
        }

        select(getSession(0));
        select(selectedSession().getPattern(0));
        select(selectedPattern().getTrack(8));
        setPatternChain(0, 0, 0);
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

    public Pattern nextPattern() {
        return selectedSession().getPattern(patternChainIndex);
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
        pattern.selectTrack(selectedTrackIndex);
    }

    public void select(Track track) {
        selectedTrackIndex = track.getIndex();
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
        selectedStepIndex = step.getIndex();
        step.setSelected(true);
    }

    public List<Pattern> setPatternChain(int min, int max, int index) {

        for (int i = patternChainMin; i <= patternChainMax; i++ ) {
            selectedSession().getPattern(i).setChained(false);
        }

        List<Pattern> newChain = Lists.newArrayList();
        patternChainMin = min;
        patternChainMax = max;
        patternChainIndex = index;
        for (int i = patternChainMin; i <= patternChainMax; i++ ) {
            Pattern pattern = selectedSession().getPattern(i);
            pattern.setChained(true);
            newChain.add(pattern);
        }

        return newChain;
    }

    public void resetPatternChainIndex() {
        patternChainIndex = patternChainMin;
    }

    public List<Pattern> getPatternChain() {
        List<Pattern> patterns = Lists.newArrayList();
        for (int i = patternChainMin; i <= patternChainMax; i++ ) {
            patterns.add(selectedSession().getPattern(i));
        }
        return patterns;
    }


    public Pattern advancePattern() {

        Pattern playing = playingPattern();
        Pattern next = nextPattern();

        if (playing != next) {
            playing.setPlaying(false);
            playingPatternIndex = patternChainIndex;
            patternChainIndex++;
            if (patternChainIndex > patternChainMax) {
                patternChainIndex = patternChainMin;
            }
            next = playingPattern();
            next.setPlaying(true);
            if (copyMutesToNew) {
                next.copyMutes(playing);
            }

            if (!specialSelected) {
                select(next);
                next.selectTrack(selectedTrackIndex);
            }

        }

        return next;
    }


}
