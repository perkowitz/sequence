package net.perkowitz.sequence;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by optic on 7/9/16.
 */
public class Memory {

    @Getter @Setter private static int sessionCount = 8;
    @Getter private Session[] sessions;

    public Memory() {

        this.sessions = new Session[sessionCount];
        for (int i = 0; i < sessionCount; i++) {
            sessions[i] = new Session();
        }

    }

    public Session getSession(int index) {
        return sessions[index % sessionCount];
    }

}
