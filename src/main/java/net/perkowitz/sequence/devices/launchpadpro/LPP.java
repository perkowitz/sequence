package net.perkowitz.sequence.devices.launchpadpro;

import net.perkowitz.sequence.MidiUtil;
import org.apache.commons.lang3.StringUtils;

import javax.sound.midi.*;
import java.util.Timer;
import java.util.concurrent.CountDownLatch;


public class LPP {

    private static MidiDevice lppInput;
    private static MidiDevice lppOutput;
    private static Transmitter transmitter;
    private static Receiver receiver;

    private static CountDownLatch stop = new CountDownLatch(1);
    private Timer timer = null;

    public static void main(String args[]) throws Exception {

        String[] lppNames = new String[] { "Launchpad", "Standalone" };
        lppInput = MidiUtil.findMidiDevice(lppNames, false, true);
        if (lppInput == null) {
            System.err.printf("Unable to find controller input device matching name: %s\n", StringUtils.join(lppNames, ", "));
            System.exit(1);
        }
        lppOutput = MidiUtil.findMidiDevice(lppNames, true, false);
        if (lppOutput == null) {
            System.err.printf("Unable to find controller output device matching name: %s\n", StringUtils.join(lppNames, ", "));
            System.exit(1);
        }

        lppInput.open();
        transmitter = lppInput.getTransmitter();
        lppOutput.open();
        receiver = lppOutput.getReceiver();

        LaunchpadPro lpp = new LaunchpadPro(receiver, null);

        transmitter.setReceiver(lpp);

        lpp.initialize();

        int channel = 0;
        Color color = Color.fromIndex(9);
        for (int y = 0; y < 8; y++) {
            color = Color.fromIndex((int) (Math.random() * 127) + 1);
            lpp.setButton(new Button(Button.Side.Left, y), color);
            for (int x = 0; x < 8; x++) {
                lpp.setPad(new Pad(x, y), color);
            }
            lpp.setButton(new Button(Button.Side.Right, y), color);
        }

        color = Color.fromIndex((int) (Math.random() * 127) + 1);
        Color color2 = Color.fromIndex((int) (Math.random() * 127) + 1);
        for (int index = 0; index < 8; index++) {
            lpp.setButton(new Button(Button.Side.Top, index), color);
            lpp.setButton(new Button(Button.Side.Bottom, index), color2);
        }



        lpp.initialize();

        palette(lpp, false);
//        palette(lpp, Color.oranges);
//        lpp.setPads(Sprites.hachi, Color.DIM_GREEN);


//        int s = Sprites.sprites.length;
//        for (int i = 0; i < 16; i++) {
//            lpp.initialize();
//            color = (int)(Math.random() * 127) + 1;
//            lpp.setPads(Sprites.sprites[i % s], color);
//            Thread.sleep(250);
//        }

        int[] colors = new int[] { 64, 66, 67, 68, 71, 78, 79, 84, 87, 90, 91, 92, 95, 103, 104, 112, 115, 116, 117, 118, 119, 120, 122, 123};

//        startTimer();
//        stop.await();

        lppInput.close();
        lppOutput.close();

        System.exit(0);


    }

    private static void palette(LaunchpadPro lpp, boolean upper) {

        int c = 0;
        if (upper) {
            c = 64;
        }
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                lpp.setPad(new Pad(x, 7-y), Color.fromIndex(c));
                c++;
            }
        }

    }

    private static void palette(LaunchpadPro lpp, int[] colors) {

        int c = 0;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                int index = colors[c % colors.length];
                lpp.setPad(new Pad(x, y), Color.fromIndex(index));
                c++;
            }
        }

    }


}
