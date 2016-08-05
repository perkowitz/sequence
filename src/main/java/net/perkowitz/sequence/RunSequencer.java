package net.perkowitz.sequence;

import net.perkowitz.sequence.launchpad.LaunchpadController;
import net.perkowitz.sequence.launchpad.LaunchpadDisplay;
import net.perkowitz.sequence.models.Memory;
import net.perkowitz.sequence.models.Pattern;
import net.perkowitz.sequence.models.Session;
import net.thecodersbreakfast.lp4j.api.Launchpad;
import net.thecodersbreakfast.lp4j.api.LaunchpadClient;
import net.thecodersbreakfast.lp4j.midi.MidiDeviceConfiguration;
import net.thecodersbreakfast.lp4j.midi.MidiLaunchpad;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class RunSequencer {

    private static String CONTROLLER_NAME_PROPERTY = "controller.name";
    private static String SEQUENCE_NAME_PROPERTY = "output.name";

    private static Properties properties = null;


    public static void main(String args[]) throws Exception {

        // load settings
        System.out.println("Getting app settings..");
        properties = getProperties("sequence.properties");

        // set all the counts of sessions, patterns, tracks, steps
        System.out.println("Setting memory sizes..");
        Memory.setSessionCount(new Integer((String)properties.get("sessions")));
        Session.setPatternCount(new Integer((String)properties.get("patterns")));
        Pattern.setTrackCount(new Integer((String) properties.get("tracks")));
        net.perkowitz.sequence.models.Track.setStepCount(16);

        // find the controller midi device
        System.out.println("Finding controller device..");
        String controllerName = properties.getProperty(CONTROLLER_NAME_PROPERTY);
        MidiDevice controllerInput = MidiUtil.findMidiDevice(controllerName, false, true);
        MidiDevice controllerOutput = MidiUtil.findMidiDevice(controllerName, true, false);
        if (controllerInput == null) {
            System.err.printf("Unable to find controller input device matching name: %s\n", controllerName);
            System.exit(1);
        }
        if (controllerOutput == null) {
            System.err.printf("Unable to find controller output device matching name: %s\n", controllerName);
            System.exit(1);
        }

        // find the midi device for sequencer output
        System.out.println("Finding output device..");
        String outputName = properties.getProperty(SEQUENCE_NAME_PROPERTY);
        MidiDevice sequenceOutput = MidiUtil.findMidiDevice(outputName, true, false);
        if (sequenceOutput == null) {
            System.err.printf("Unable to find sequence output device matching name: %s\n", outputName);
            System.exit(1);
        }

        try {

            Launchpad launchpad = new MidiLaunchpad(new MidiDeviceConfiguration(controllerInput, controllerOutput));
            LaunchpadClient launchpadClient = launchpad.getClient();
            SequencerDisplay launchpadDisplay = new LaunchpadDisplay(launchpadClient);
            LaunchpadController launchpadController = new LaunchpadController();
            launchpad.setListener(launchpadController);

            Sequencer sequencer = new Sequencer(launchpadController, launchpadDisplay, sequenceOutput);

        } catch (MidiUnavailableException e) {
            System.err.printf("%s\n", e.getStackTrace().toString());
        }

    }


    private static Properties getProperties(String filename) throws IOException {

        InputStream inputStream = null;
        try {
            Properties properties = new Properties();

            inputStream = RunSequencer.class.getClassLoader().getResourceAsStream(filename);

            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + filename + "' not found in the classpath");
            }

            return properties;

        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return null;
    }




}
