package net.perkowitz.sequence.models;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by optic on 7/9/16.
 */
public class Session {

    @Getter @Setter private static int patternCount = 8;
    @Getter private Pattern[] patterns;

    @Getter private int index;
    @Getter @Setter private boolean selected = false;

    // only used for deserializing JSON; Session should always be created with an index
    public Session() {}

    public Session(int index) {

        this.index = index;

        this.patterns = new Pattern[patternCount];
        for (int i = 0; i < patternCount; i++) {
            patterns[i] = new Pattern(i);
        }

    }

    public Pattern getPattern(int index) {
        return patterns[index % patternCount];
    }

}
