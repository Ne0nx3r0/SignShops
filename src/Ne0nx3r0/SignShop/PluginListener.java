package Ne0nx3r0.SignShop;

import org.bukkit.event.server.PluginEvent;
import org.bukkit.event.server.ServerListener;

import com.nijiko.coelho.iConomy.iConomy;
import org.bukkit.plugin.Plugin;

public class PluginListener extends ServerListener {
    public PluginListener() { }

//    @Override
    public void onEnabled(PluginEvent event) {
        if(SignShop.getiConomy() == null){
            Plugin iConomy = SignShop.getBukkitServer().getPluginManager().getPlugin("iConomy");

            if (iConomy != null) {
                if(iConomy.isEnabled()) {
                    SignShop.setiConomy((iConomy)iConomy);
                    System.out.println("[SignShop] Successfully linked with iConomy.");
                    return;
                }
            }
        }
        System.out.println("[SignShop] could not link with iConomy, so ... what's the point? O.o");
    }
}
