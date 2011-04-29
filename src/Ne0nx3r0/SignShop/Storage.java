package Ne0nx3r0.SignShop;
import org.bukkit.util.config.Configuration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.Block;
import java.io.File;
import java.util.Map;
import java.util.HashMap;
import org.bukkit.Location;

public class Storage{
    public static Configuration database;

    private static Map<Location,Seller> sellers;

 //myArr = new ArrayList<String>();
    public Storage(File dbfile){
        //database = new Configuration(dbfile);
        //database.load();

        sellers = new HashMap<Location, Seller>();
    }

    public void addSeller(Block bSign,Block bChest,ItemStack[] isItems){
        sellers.put(bSign.getLocation(),new Seller(bSign,bChest,isItems));
        
        //database.setProperty("sellers",sellers);
        
        //database.save();
    }

    public Seller getSeller(Location lKey){
        if(sellers.containsKey(lKey)){
            return sellers.get(lKey);
        }
        return null;
    }
}
