package net.perkowitz.sequence;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

/**
 * Created by optic on 7/8/16.
 */
public class MidiUtil {

    public static MidiDevice.Info[] midiDeviceInfos = null;

    public static MidiDevice findMidiDevice(String deviceName, boolean receive, boolean transmit) {

        if (midiDeviceInfos == null) {
            System.out.println("Loading device info..");
            midiDeviceInfos = MidiSystem.getMidiDeviceInfo();
//            for (int i = 0; i < midiDeviceInfos.length; i++) {
//                System.out.printf("Found midi device: %s\n", midiDeviceInfos[i].getName());
//            }
        }

        MidiDevice targetDevice = null;

        System.out.println("Checking for device name..");
        try {
            for (int i = 0; i < midiDeviceInfos.length; i++) {
                MidiDevice device = MidiSystem.getMidiDevice(midiDeviceInfos[i]);

                boolean canReceive = device.getMaxReceivers() != 0;
                boolean canTransmit = device.getMaxTransmitters() != 0;

                if (midiDeviceInfos[i].getName().toLowerCase().contains(deviceName.toLowerCase()) && receive == canReceive && transmit == canTransmit) {
                    targetDevice = device;
                } else if (midiDeviceInfos[i].getDescription().toLowerCase().contains(deviceName.toLowerCase()) && receive == canReceive && transmit == canTransmit) {
                    targetDevice = device;
                }

            }
        } catch (MidiUnavailableException e) {
            System.out.printf("MIDI not available: %s\n", e);
        }

        if (targetDevice == null) {
            for (int i = 0; i < midiDeviceInfos.length; i++) {
                System.out.printf("Found midi device: %s, %s\n", midiDeviceInfos[i].getName(), midiDeviceInfos[i].getDescription());
            }
        }

        return targetDevice;
    }

}
