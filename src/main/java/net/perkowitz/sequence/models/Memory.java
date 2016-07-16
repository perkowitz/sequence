package net.perkowitz.sequence.models;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by optic on 7/9/16.
 */
public class Memory {

    @Getter @Setter private static int sessionCount = 8;
    @Getter private Session[] sessions;

    @Getter @Setter private Session selectedSession;
    @Getter @Setter private Pattern selectedPattern;
    @Getter @Setter private Track selectedTrack;
    @Getter @Setter private Step selectedStep;

    public Memory() {

        this.sessions = new Session[sessionCount];
        for (int i = 0; i < sessionCount; i++) {
            sessions[i] = new Session();
        }

        select(getSession(0));
        select(getSelectedSession().getPattern(0));
        select(getSelectedPattern().getTrack(0));

    }

    public Session getSession(int index) {
        return sessions[index % sessionCount];
    }

    public void select(Session session) {
        if (selectedSession != null) {
//            selectedSession.setSelected(false);
        }
        selectedSession = session;
//        selectedSession.setSelected(true);
    }

    public void select(Pattern pattern) {
        if (selectedPattern != null) {
//            selectedPattern.setSelected(false);
        }
        selectedPattern = pattern;
//        selectedPattern.setSelected(true);
    }

    public void select(Track track) {
        if (selectedTrack != null) {
            selectedTrack.setSelected(false);
        }
        selectedTrack = track;
        selectedTrack.setSelected(true);
    }

    public void select(Step step) {
        if (selectedStep != null) {
            selectedStep.setSelected(false);
        }
        selectedStep = step;
        selectedStep.setSelected(true);
    }




}
