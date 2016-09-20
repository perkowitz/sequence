package net.perkowitz.issho.hachi;

import net.perkowitz.issho.devices.GridDisplay;
import net.perkowitz.issho.devices.launchpadpro.LaunchpadPro;
import net.perkowitz.issho.hachi.modules.BasicModule;
import net.perkowitz.issho.hachi.modules.Module;
import net.perkowitz.issho.hachi.modules.PaletteModule;
import net.perkowitz.issho.util.MidiUtil;
import net.perkowitz.issho.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;

import javax.sound.midi.MidiDevice;
import java.util.Properties;

/**
 * Created by optic on 9/19/16.
 */
public class Hachi {

    private static String CONTROLLER_NAME_PROPERTY = "controller.name";
    private static String CONTROLLER_TYPE_PROPERTY = "controller.type";

    private static Properties properties;

    private static MidiDevice controllerInput;
    private static MidiDevice controllerOutput;

    private static HachiController controller;


    public static void main(String args[]) throws Exception {

        String propertyFile = null;
        if (args.length > 0) {
            propertyFile = args[0];
        }
        // load settings
        if (propertyFile == null) {
            System.out.println("Getting app settings..");
            properties = PropertiesUtil.getProperties("hachi.properties");
        } else {
            System.out.printf("Getting app settings from %s..\n", propertyFile);
            properties = PropertiesUtil.getProperties(propertyFile);
        }

        LaunchpadPro launchpadPro = findDevice();


        Module[] modules = new Module[2];
        modules[0] = new PaletteModule(false);
        modules[1] = new PaletteModule(true);

        controller = new HachiController(modules, launchpadPro);

    }


    private static LaunchpadPro findDevice() {

        // find the controller midi device
        System.out.println("Finding controller device..");
        String[] controllerNames = properties.getProperty(CONTROLLER_NAME_PROPERTY).split("/");
        controllerInput = MidiUtil.findMidiDevice(controllerNames, false, true);
        if (controllerInput == null) {
            System.err.printf("Unable to find controller input device matching name: %s\n", StringUtils.join(controllerNames, ","));
            System.exit(1);
        }
        controllerOutput = MidiUtil.findMidiDevice(controllerNames, true, false);
        if (controllerOutput == null) {
            System.err.printf("Unable to find controller output device matching name: %s\n", StringUtils.join(controllerNames, ","));
            System.exit(1);
        }

        try {
            String type = properties.getProperty(CONTROLLER_TYPE_PROPERTY);
            if (type.toLowerCase().equals("launchpadpro")) {
                LaunchpadPro launchpadPro = new LaunchpadPro(controllerOutput.getReceiver(), null);
                return launchpadPro;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }

        return null;
    }


}
