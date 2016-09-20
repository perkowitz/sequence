package net.perkowitz.issho.hachi;

import com.google.common.collect.Lists;
import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridDisplay;
import net.perkowitz.issho.devices.GridListener;
import net.perkowitz.issho.devices.GridPad;
import net.perkowitz.issho.hachi.modules.Module;

import java.util.List;

/**
 * Created by optic on 9/12/16.
 */
public class HachiController implements GridListener {

    private Module[] modules = null;
    private Module activeModule;
    private GridListener[] moduleListeners = null;
    private GridListener activeListener = null;
    private SwitchableDisplay[] displays;


    public HachiController(Module[] modules, GridDisplay display) {

        this.modules = modules;
        moduleListeners = new GridListener[modules.length];
        displays = new SwitchableDisplay[modules.length];
        for (int i = 0; i < modules.length; i++) {
            moduleListeners[i] = modules[i].getGridListener();
            SwitchableDisplay switchableDisplay = new SwitchableDisplay(display);
            displays[i] = switchableDisplay;
            modules[i].setDisplay(switchableDisplay);
        }

        selectModule(0);
    }

    /***** private implementation ***************/

    private void selectModule(int index) {
        if (index < modules.length && modules[index] != null) {
            selectDisplay(index);
            activeModule = modules[index];
            activeListener = moduleListeners[index];
            activeModule.redraw();
        }
    }

    private void selectDisplay(int index) {
        for (int i = 0; i < displays.length; i++) {
            if (i == index) {
                displays[i].setEnabled(true);
            } else {
                displays[i].setEnabled(false);
            }
        }
    }


    /***** GridListener implementation ***************/

    public void onPadPressed(GridPad pad, int velocity) {
        if (activeListener != null) {
            activeListener.onPadPressed(pad, velocity);
        }
    }

    public void onPadReleased(GridPad pad) {
        if (activeListener != null) {
            activeListener.onPadReleased(pad);
        }
    }

    public void onButtonPressed(GridButton button, int velocity) {
        if (button.getSide() == HachiUtil.MODULE_BUTTON_SIDE) {
            // top row used for module switching
            selectModule(button.getIndex());
        } else {
            // everything else passed through to active module
            if (activeListener != null) {
                activeListener.onButtonPressed(button, velocity);
            }
        }
    }

    public void onButtonReleased(GridButton button) {
        if (button.getSide() == HachiUtil.MODULE_BUTTON_SIDE) {
            // top row used for module switching
        } else {
            // everything else passed through to active module
            if (activeListener != null) {
                activeListener.onButtonReleased(button);
            }
        }
    }




}
