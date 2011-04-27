package Ne0nx3r0.SignShop;
import org.bukkit.util.config.Configuration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.Block;
import java.io.File;

public class Storage{
    public static Configuration database;

    public Storage(File dbfile){
        database = new Configuration(dbfile);
        database.load();
    }

    public boolean addSeller(Block bSign,Block bChest,ItemStack[] isItems){
        

        return true;
    }
}
