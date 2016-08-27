package net.perkowitz.sequence.models;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by optic on 7/9/16.
 */
public class Session {

    @Getter @Setter private static int patternCount = 16;
    @Getter private Pattern[] patterns;

    @Getter @Setter private static int fillCount = 8;
    @Getter private FillPattern[] fills;

    @Getter private int index;
    @Getter @Setter private boolean selected = false;
    @Getter @Setter private boolean next = false;

    // only used for deserializing JSON; Session should always be created with an index
    public Session() {}

    public Session(int index) {

        this.index = index;

        this.patterns = new Pattern[patternCount];
//        for (int i = 0; i < patternCount; i++) {
//            patterns[i] = new Pattern(i);
//        }

        this.fills = new FillPattern[fillCount];
//        for (int i = 0; i < fillCount; i++) {
//            fills[i] = new FillPattern(i, (int)Math.pow(2, (i / 2) + 1));
//        }

    }

    public Pattern getPattern(int index) {
        index = index % patternCount;
        Pattern pattern = patterns[index];
        if (pattern == null) {
            pattern = new Pattern(index);
            patterns[index] = pattern;
        }
        return pattern;
    }

    public FillPattern getFill(int index) {
        index = index % fillCount;
        FillPattern fill = fills[index];
        if (fill == null) {
            fill = new FillPattern(index, (int)Math.pow(2, (index / 2) + 1));
            fills[index] = fill;
        }
        return fill;
    }

}
