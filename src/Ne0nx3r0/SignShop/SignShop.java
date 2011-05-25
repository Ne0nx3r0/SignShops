package Ne0nx3r0.SignShop;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import java.io.File;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import org.bukkit.util.config.Configuration;

//permissions
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.plugin.Plugin;

import com.iConomy.*;

public class SignShop extends JavaPlugin{
    private final SignShopPlayerListener playerListener = new SignShopPlayerListener(this);
    private final SignShopBlockListener blockListener = new SignShopBlockListener(this);
    public static iConomy iConomy = null;
    public static Storage Storage;
    public static HashMap<String,HashMap<String,String>> Messages;


    //Permissions
    public static PermissionHandler permissionHandler;
    public boolean USE_PERMISSIONS = false;

    public void onDisable(){
        this.Storage.Save();

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

        //Storage
        this.Storage = new Storage(new File(this.getDataFolder(),"sellers.yml"));
        this.Storage.Save();

        //Shop Messages
        File fConfig = new File(this.getDataFolder(),"messages.yml");
        if(!fConfig.isFile()){
            try{
                File jarloc = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getCanonicalFile();
                if(jarloc.isFile()){
                    JarFile jar = new JarFile(jarloc);
                    JarEntry entry = jar.getJarEntry("messages.yml");
                    
                    if(entry != null && !entry.isDirectory()){
                        InputStream in = jar.getInputStream(entry);
                        FileOutputStream out = new FileOutputStream(fConfig);
                        byte[] tempbytes = new byte[512];
                        int readbytes = in.read(tempbytes,0,512);
                        while(readbytes>-1){
                            out.write(tempbytes,0,readbytes);
                            readbytes = in.read(tempbytes,0,512);
                        }
                        out.close();
                        in.close();
                        
                        System.out.println("[SignShop] Created default messages.yml");
                    }   
                }
            }catch (Exception e){
                System.out.println("[SignShop] Unable to create default messages.yml:" + e);
            }
        }

        Configuration config = new Configuration(fConfig);
        config.load();
        this.Messages = (HashMap<String,HashMap<String,String>>) config.getProperty("messages");

        //log
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println("[SignShop] v" + pdfFile.getVersion() + " ENABLED" );

        //Permissions
        setupPermissions();
    }

    private void setupPermissions() {
        Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");

        if(this.permissionHandler == null){
            if(permissionsPlugin != null){
                this.permissionHandler = ((Permissions) permissionsPlugin).getHandler();
                this.USE_PERMISSIONS = true;
                System.out.println("[SignShop] Hooked into Permissions");
            }
            else{
                System.out.println("[SignShop] Permissions not found, defaulting to OP for iSell/iBuy, all others allowed by everyone.");
            }
        }
    }
}