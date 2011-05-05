package Ne0nx3r0.SignShop;

import org.bukkit.event.server.ServerListener;

import com.iConomy.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

public class server extends ServerListener {
    private SignShop plugin;

    public server(SignShop plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPluginDisable(PluginDisableEvent event) {
        if (plugin.iConomy != null) {
            if (event.getPlugin().getDescription().getName().equals("iConomy")) {
                plugin.iConomy = null;
                System.out.println("[MyPlugin] un-hooked from iConomy.");
            }
        }
    }

    @Override
    public void onPluginEnable(PluginEnableEvent event) {
        if (plugin.iConomy == null) {
            Plugin iConomy = plugin.getServer().getPluginManager().getPlugin("iConomy");

            if (iConomy != null) {
                if (iConomy.isEnabled()) {
                    plugin.iConomy = (iConomy)iConomy;
                    System.out.println("[MyPlugin] hooked into iConomy.");
                }
            }
        }
    }
}