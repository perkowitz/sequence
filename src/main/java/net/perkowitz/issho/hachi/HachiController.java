package net.perkowitz.issho.hachi;

import com.google.common.collect.Lists;
import net.perkowitz.issho.devices.GridButton;
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


    public HachiController(GridListener[] moduleListeners) {
        this.moduleListeners = moduleListeners;
    }




    /***** interface implementation ***************/

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
        if (button.getSide() == GridButton.Side.Top) {
            // top row used for module switching
            int index = button.getIndex();
            if (index < modules.length && modules[index] != null) {
                activeModule = modules[index];
                activeListener = moduleListeners[index];
                activeModule.redraw();
            }
        } else {
            // everything else passed through to active module
            if (activeListener != null) {
                activeListener.onButtonPressed(button, velocity);
            }
        }
    }

    public void onButtonReleased(GridButton button) {
        if (button.getSide() == GridButton.Side.Top) {
            // top row used for module switching
        } else {
            // everything else passed through to active module
            if (activeListener != null) {
                activeListener.onButtonReleased(button);
            }
        }
    }

}
