package net.perkowitz.issho.hachi;

import net.perkowitz.issho.devices.GridDisplay;
import net.perkowitz.issho.devices.console.Console;
import net.perkowitz.issho.devices.launchpadpro.Button;
import net.perkowitz.issho.devices.launchpadpro.LaunchpadPro;
import net.perkowitz.issho.devices.launchpadpro.Pad;
import net.perkowitz.issho.hachi.modules.BasicModule;
import net.perkowitz.issho.hachi.modules.Module;
import net.perkowitz.issho.hachi.modules.PaletteModule;
import net.perkowitz.issho.util.MidiUtil;
import net.perkowitz.issho.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;

import javax.sound.midi.MidiDevice;
import java.util.Properties;
import java.util.Scanner;

import static net.perkowitz.issho.devices.GridButton.Side.Top;

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
        GridDisplay gridDisplay = launchpadPro;
        if (launchpadPro == null) {
            System.err.printf("Unable to find controller device matching name: %s\n", properties.getProperty(CONTROLLER_NAME_PROPERTY));
//            System.exit(1);
            gridDisplay = new Console();
        }

        Module[] modules = new Module[3];
        modules[0] = new BasicModule();
        modules[1] = new PaletteModule(false);
        modules[2] = new PaletteModule(true);

        System.out.println("Creating modules...");
        controller = new HachiController(modules, gridDisplay);

        // send each module a random pad press
        for (int index = 0; index < modules.length; index++) {
            System.out.printf("Selecting module %d: ", index);
            System.in.read();
            controller.onButtonPressed(Button.at(Top, index), 64);

            int x = (int)(Math.random() * 8);
            int y = (int)(Math.random() * 8);
            int v = (int)(Math.random() * 127 + 1);
            Pad pad = Pad.at(x, y);
            System.out.printf("Pressing pad %s, v=%d: ", pad, v);
            System.in.read();
            controller.onPadPressed(pad, v);

            System.out.println();
        }


    }


    private static LaunchpadPro findDevice() {

        // find the controller midi device
        System.out.println("Finding controller device..");
        String[] controllerNames = properties.getProperty(CONTROLLER_NAME_PROPERTY).split("/");
        controllerInput = MidiUtil.findMidiDevice(controllerNames, false, true);
        controllerOutput = MidiUtil.findMidiDevice(controllerNames, true, false);
        if (controllerInput == null || controllerOutput == null) {
            return null;
        }

        try {
            String type = properties.getProperty(CONTROLLER_TYPE_PROPERTY);
            if (type.toLowerCase().equals("launchpadpro")) {
                return new LaunchpadPro(controllerOutput.getReceiver(), null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return null;
    }


}
