package net.perkowitz.sequence;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import static javax.sound.midi.ShortMessage.*;

/**
 * Created by optic on 8/8/16.
 */
public class SequencerReceiver implements Receiver {

    private SequencerInterface sequencer;

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
                    System.out.printf("NOTE_ON %d, %d\n", shortMessage.getData1(), shortMessage.getData2());

                    break;
                case NOTE_OFF:
                    System.out.printf("NOTE_OFF\n");
                    break;
                case START:
                    System.out.printf("START\n");
                    break;
                case STOP:
                    System.out.printf("STOP\n");
                    break;
                case CONTROL_CHANGE:
                    System.out.printf("CC %d, %d\n", shortMessage.getData1(), shortMessage.getData2());
                    break;
                case TIMING_CLOCK:
                    System.out.printf("TIMING_CLOCK\n");
                    break;
                case 0xF0:
                    // sysex
                    break;
                default:
                    System.out.printf("Other: %d, %s\n", command, shortMessage);
                    break;
            }
        } else {
            System.out.printf("Not short: %s\n", message);

        }

    }

}
