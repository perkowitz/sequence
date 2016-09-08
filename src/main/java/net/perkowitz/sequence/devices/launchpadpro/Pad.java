package net.perkowitz.sequence.devices.launchpadpro;

import lombok.Getter;

/**
 * Created by optic on 9/3/16.
 */
public class Pad {

    @Getter private final int x;
    @Getter private final int y;
    @Getter private final int note;

    public Pad(int x, int y) {
        this.x = x;
        this.y = y;
        this.note = (7-y) * 10 + x + 11;
    }

    /***** static methods ********************************/

    public static Pad fromNote(int note) {
        int x = note % 10 - 1;
        int y = 7 - (note / 10 - 1);
        return new Pad(x, y);
    }

    public static Pad at(int x, int y) {
        return new Pad(x, y);
    }

}
