package Ne0nx3r0.SignShop;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import com.nijiko.coelho.iConomy.iConomy;
import org.bukkit.Server;
import java.io.File;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.Location;

public class SignShop extends JavaPlugin{
    private final SignShopPlayerListener playerListener = new SignShopPlayerListener(this);
    private static PluginListener PluginListener = null;
    private static iConomy iConomy = null;
    private static Server Server = null;
    public static Storage Storage;

    public static Seller[] sellers;

    public int MAX_DISTANCE;

    public void onDisable(){
        System.out.println("[SignShop] Disabled");
    }

    public void onEnable(){
        Server = getServer();

        PluginListener = new PluginListener();

        PluginManager pm = getServer().getPluginManager();

        pm.registerEvent(Event.Type.PLUGIN_ENABLE, PluginListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);

        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println("["+pdfFile.getName() + "] v" + pdfFile.getVersion() + " ENABLED" );

        Storage = new Storage(new File(this.getDataFolder(),"sellers.yml"));
    }



    //iConomy junk

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