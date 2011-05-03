package Ne0nx3r0.SignShop;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import com.nijiko.coelho.iConomy.iConomy;
import org.bukkit.Server;
import java.io.File;

public class SignShop extends JavaPlugin{
    private final SignShopPlayerListener playerListener = new SignShopPlayerListener(this);
    private final SignShopBlockListener blockListener = new SignShopBlockListener(this);
    private static PluginListener PluginListener = null;
    public static iConomy iConomy = null;
    private static Server Server = null;
    public static Storage Storage;

    public void onDisable(){
        Storage.Save();
        System.out.println("[SignShop] Disabled");
    }

    public void onEnable(){
        //enable iConomy
        PluginListener = new PluginListener();

        if(iConomy == null){
            System.out.println("[SignShop] iConomy not found, halting.");
            return;
        }

        PluginManager pm = getServer().getPluginManager();

        pm.registerEvent(Event.Type.PLUGIN_ENABLE, PluginListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Low, this);
        pm.registerEvent(Event.Type.BLOCK_BURN, blockListener, Priority.Low, this);

        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println("[SignShop] v" + pdfFile.getVersion() + " ENABLED" );

        Storage = new Storage(new File(this.getDataFolder(),"sellers.yml"));
    }

    //iConomy stuff
    public static Server getBukkitServer() {
        return Server;
    }

    public static iConomy getiConomy() {
        return iConomy;
    }

    public static boolean setiConomy(iConomy plugin) {
        if (iConomy == null) {
            iConomy = plugin;
        } else {
            return false;
        }
        return true;
    }

}