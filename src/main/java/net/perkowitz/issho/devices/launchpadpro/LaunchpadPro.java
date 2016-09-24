package net.perkowitz.issho.devices.launchpadpro;


import lombok.Setter;
import net.perkowitz.issho.devices.*;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import static javax.sound.midi.ShortMessage.*;

/**
 * Created by optic on 9/3/16.
 */
public class LaunchpadPro implements Receiver, GridDisplay {

    private static int MIDI_REALTIME_COMMAND = 0xF0;

    private static int CHANNEL = 15;

    private Receiver receiver;
    @Setter private GridListener listener;

    public LaunchpadPro(Receiver receiver, GridListener listener) {
        this.receiver = receiver;
        this.listener = listener;
    }


    /****** public logical implementation ***********************************************************/

    public void initialize(Color color, boolean doButtons) {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                setPad(GridPad.at(x, y), color);
                if (doButtons) {
                    setButton(new Button(Button.Side.Top, x), color);
                    setButton(new Button(Button.Side.Bottom, x), color);
                }
            }
            if (doButtons) {
                setButton(new Button(Button.Side.Left, y), color);
                setButton(new Button(Button.Side.Right, y), color);
            }
        }

    }

    public void initialize(boolean doButtons) {
        initialize(Color.OFF, doButtons);
    }

    public void initialize() {
        initialize(Color.OFF, true);
    }

    public void setPads(GridPad[] pads, Color color) {
        for (GridPad pad : pads) {
            setPad(pad, color);
        }
    }

    public void setPad(GridPad pad, GridColor color) {
        note(CHANNEL, padToNote(pad), color.getIndex());
    }

    public void setButton(GridButton button, GridColor color) {
        Button lppButton = (Button) button;
        cc(CHANNEL, lppButton.getCc(), color.getIndex());
    }


    /***** midi receiver implementation **************************************************************/

    public void send(MidiMessage message, long timeStamp) {

        if (message instanceof ShortMessage) {
            ShortMessage shortMessage = (ShortMessage) message;
            int command = shortMessage.getCommand();
            int status = shortMessage.getStatus();

            if (command == MIDI_REALTIME_COMMAND) {
                switch (status) {
                    case START:
                        System.out.println("START");
                        break;
                    case STOP:
                        System.out.println("STOP");
                        break;
                    case TIMING_CLOCK:
                        System.out.println("TICK");
                        break;
                    default:
//                        System.out.printf("REALTIME: %d\n", status);
                        break;
                }


            } else {
                switch (command) {
                    case NOTE_ON:
//                        System.out.printf("NOTE ON: %d, %d, %d\n", shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                        if (listener != null) {
                            GridPad pad = noteToPad(shortMessage.getData1());
                            int velocity = shortMessage.getData2();
                            if (velocity == 0) {
                                listener.onPadReleased(pad);
                            } else {
                                listener.onPadPressed(pad, velocity);
                            }
                        }
                        break;
                    case NOTE_OFF:
//                        System.out.printf("NOTE OFF: %d, %d, %d\n", shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                        if (listener != null) {
                            GridPad pad = noteToPad(shortMessage.getData1());
                            listener.onPadReleased(pad);
                        }
                        break;
                    case CONTROL_CHANGE:
//                        System.out.printf("MIDI CC: %d, %d, %d\n", shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                        if (listener != null) {
                            Button button = Button.fromCC(shortMessage.getData1());
                            int velocity = shortMessage.getData2();
                            if (velocity == 0) {
                                listener.onButtonReleased(button);
                            } else {
                                listener.onButtonPressed(button, velocity);
                            }
                        }
                        break;
                    default:
                }
            }
        }
    }

    public void close() {

    }


    /***** private implementation **************************************************************/

    private void note(int channel, int noteNumber, int velocity) {

        try {
            ShortMessage message = new ShortMessage();
            message.setMessage(ShortMessage.NOTE_ON, channel, noteNumber, velocity);
            receiver.send(message, -1);

        } catch (InvalidMidiDataException e) {
            System.err.println(e);
        }

    }

    private void cc(int channel, int ccNumber, int value) {

        try {
            ShortMessage message = new ShortMessage();
            message.setMessage(ShortMessage.CONTROL_CHANGE, channel, ccNumber, value);
            receiver.send(message, -1);

        } catch (InvalidMidiDataException e) {
            System.err.println(e);
        }

    }

    private int padToNote(GridPad pad) {
        return (7-pad.getY()) * 10 + pad.getX() + 11;
    }

    private GridPad noteToPad(int note) {
        int x = note % 10 - 1;
        int y = 7 - (note / 10 - 1);
        return GridPad.at(x, y);

    }



}
