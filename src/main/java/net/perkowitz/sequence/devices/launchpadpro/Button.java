package net.perkowitz.sequence.devices.launchpadpro;

import lombok.Getter;

import static net.perkowitz.sequence.devices.launchpadpro.Button.Side.*;

/**
 * Created by optic on 9/3/16.
 */
public class Button {

    public enum Side {
        Top, Bottom, Left, Right
    }



    @Getter private final Side side;
    @Getter private final int index;
    @Getter private final int cc;

    public Button(Side side, int index) {
        this.side = side;
        this.index = index;

        int flippedIndex = 7 - index;
        switch (side) {
            case Top:
                this.cc = 90 + flippedIndex + 1;
                break;
            case Bottom:
                this.cc = flippedIndex + 1;
                break;
            case Left:
                this.cc = 10 + flippedIndex * 10;
                break;
            case Right:
                this.cc = 19 + flippedIndex * 10;
                break;
            default:
                this.cc = 100;
        }
    }


    /***** static methods ********************************/

    public static Button fromCC(int cc) {

        Side side = Top;
        int index = 0;

        if (cc >= 10 && cc <= 89) {
            index = cc / 10 - 1;
            side = (cc % 10 == 0) ? Left : Right;
        } else {
            index = cc % 10 - 1;
            side = (cc < 10) ? Bottom : Top;
        }

        return new Button(side, 7 - index);
    }

    public static Button at(Side side, int index) {
        return new Button(side, index);
    }



}
