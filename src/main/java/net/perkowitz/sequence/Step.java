package net.perkowitz.sequence;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by optic on 7/9/16.
 */
public class Step {

    @Getter private int index;
    @Getter @Setter private boolean selected = false;

    @Getter @Setter private boolean on = false;
    @Getter @Setter private int velocity = 100;

    public Step(int index) {
        this.index = index;
    }

}
