package net.perkowitz.sequence.models;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by optic on 7/9/16.
 */
public class Session {

    @Getter @Setter private static int patternCount = 8;
    @Getter private Pattern[] patterns;

    public Session() {

        this.patterns = new Pattern[patternCount];
        for (int i = 0; i < patternCount; i++) {
            patterns[i] = new Pattern();
        }

    }

    public Pattern getPattern(int index) {
        return patterns[index % patternCount];
    }

}
