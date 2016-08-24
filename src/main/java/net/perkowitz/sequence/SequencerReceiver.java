package net.perkowitz.sequence;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import static javax.sound.midi.ShortMessage.*;

/**
 * Created by optic on 8/8/16.
 */
public class SequencerReceiver implements Receiver {

    private static int STEP_MIN = 0;
    private static int STEP_MAX = 110;
    private static int RESET_MIN = 11;
    private static int RESET_MAX = 127;

    private SequencerInterface sequencer;
    private int triggerChannel = 15;
    private int stepNote = 36;

    public SequencerReceiver(SequencerInterface sequencer) {
        this.sequencer = sequencer;
    }

    public void close() {

    }

    public void send(MidiMessage message, long timeStamp) {
        if (message instanceof ShortMessage) {
            ShortMessage shortMessage = (ShortMessage) message;
            int command = shortMessage.getCommand();
            switch (command) {
                case NOTE_ON:
//                    System.out.printf("NOTE_ON %d, %d, %d\n", shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                    if (shortMessage.getChannel() == triggerChannel && shortMessage.getData1() == stepNote &&
                            shortMessage.getData2() >= STEP_MIN && shortMessage.getData2() <= STEP_MAX) {
                        sequencer.trigger(false);
                    } else if (shortMessage.getChannel() == triggerChannel && shortMessage.getData1() == stepNote &&
                            shortMessage.getData2() >= RESET_MIN && shortMessage.getData2() <= RESET_MAX) {
                        sequencer.trigger(true);
                    }
                    break;
                case NOTE_OFF:
                    break;
                case START:
                    break;
                case STOP:
                    break;
                case CONTROL_CHANGE:
                    break;
                case TIMING_CLOCK:
                    break;
                case 0xF0:
                    // sysex
                    break;
                default:
                    break;
            }
        } else {
//            System.out.printf("Not short: %s\n", message);
        }

    }

}
