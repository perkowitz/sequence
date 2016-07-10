package net.perkowitz.sequence;

import net.thecodersbreakfast.lp4j.api.*;

/**
 * Created by optic on 7/9/16.
 */
public class ControlListener extends LaunchpadListenerAdapter {

    private final Sequencer sequencer;
    private final LaunchpadClient client;

    public ControlListener(Sequencer sequencer, LaunchpadClient client) {
        this.sequencer = sequencer;
        this.client = client;
    }

    @Override
    public void onPadPressed(Pad pad, long timestamp) {
        client.setPadLight(pad, Color.of(3,3), BackBufferOperation.NONE);
        System.out.printf("CL: Pressed pad %s\n", pad.toString());
    }

    @Override
    public void onPadReleased(Pad pad, long timestamp) {
        client.setPadLight(pad, Color.of(1,0), BackBufferOperation.NONE);
        System.out.printf("CL: Released pad %s\n ", pad.toString());
    }

    @Override
    public void onButtonReleased(Button button, long timestamp) {

        if (button.equals(Button.UP)) {
            sequencer.shutdown();
        }
    }

}
