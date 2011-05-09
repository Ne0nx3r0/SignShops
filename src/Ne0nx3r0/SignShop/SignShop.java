package Ne0nx3r0.SignShop;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

import com.iConomy.*;
import java.io.File;

public class SignShop extends JavaPlugin{
    private final SignShopPlayerListener playerListener = new SignShopPlayerListener(this);
    private final SignShopBlockListener blockListener = new SignShopBlockListener(this);
    public static iConomy iConomy = null;
    public static Storage Storage;

    public void onDisable(){
        Storage.Save();
        System.out.println("[SignShop] Disabled");
    }

    public void onEnable(){
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Low, this);
        pm.registerEvent(Event.Type.BLOCK_BURN, blockListener, Priority.Low, this);

        //iConomy
        pm.registerEvent(Event.Type.PLUGIN_ENABLE, new server(this), Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLUGIN_DISABLE, new server(this), Priority.Monitor, this);

        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println("[SignShop] v" + pdfFile.getVersion() + " ENABLED" );

        Storage = new Storage(new File(this.getDataFolder(),"sellers.yml"));
    }
}