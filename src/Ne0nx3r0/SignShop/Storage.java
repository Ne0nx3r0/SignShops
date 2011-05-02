package Ne0nx3r0.SignShop;
import org.bukkit.util.config.Configuration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.Block;
import java.io.File;
import java.util.Map;
import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.Bukkit;

public class Storage{
    public static Configuration yml;

    private static Map<Location,Seller> sellers;

    public Storage(File ymlFile){
        this.yml = new Configuration(ymlFile);
        this.yml.load();

        this.Load();
    }

    public void Load(){
        this.sellers = new HashMap<Location,Seller>();
        Map<String,Object> tempSellers = (Map<String,Object>) this.yml.getProperty("sellers");

        if(tempSellers == null){
            this.sellers = new HashMap <Location,Seller>();
            return;
        }

        Map<String,Object> tempSeller;

        String[] sSignLocation;
        Location lSign;
        Block bChest;
        ItemStack[] isItems;
        Integer[] items;
        Integer[] amounts;
        for(String sKey : tempSellers.keySet()){
            sSignLocation = sKey.split("/");

            lSign = Bukkit.getServer().getWorld(sSignLocation[0]).getBlockAt(
                Integer.parseInt(sSignLocation[1]),
                Integer.parseInt(sSignLocation[2]),
                Integer.parseInt(sSignLocation[3])).getLocation();

            tempSeller = (Map<String,Object>) tempSellers.get(sKey);

            bChest = Bukkit.getServer().getWorld((String) tempSeller.get("chestworld")).getBlockAt(
                (Integer) tempSeller.get("chestx"),
                (Integer) tempSeller.get("chesty"),
                (Integer) tempSeller.get("chestz"));

            items = (Integer[]) tempSeller.get("items");
            amounts = (Integer[]) tempSeller.get("amounts");
            isItems = new ItemStack[items.length];

            for(int i=0;i<items.length;i++){
                isItems[i] = new ItemStack(items[i],amounts[i]);
            }

            this.sellers.put(lSign, new Seller(bChest,isItems));
        }
    }

    public void Save(){
        Map<String,Object> tempSellers = new HashMap<String,Object>();

        Seller seller;
        Map<String,Object> temp;
        for(Location lKey : this.sellers.keySet()){
            temp = new HashMap<String,Object>();

            seller = sellers.get(lKey);

            temp.put("chestworld",seller.world);
            temp.put("chestx",seller.x);
            temp.put("chesty",seller.y);
            temp.put("chestz",seller.z);

            temp.put("items",seller.items);
            temp.put("amounts",seller.amounts);

            tempSellers.put(lKey.getWorld().getName()+"/"+lKey.getBlockX()+"/"+lKey.getBlockY()+"/"+lKey.getBlockZ(),temp);
        }
        
        this.yml.setProperty("sellers",tempSellers);
        this.yml.save();
    }

    public void addSeller(Block bSign,Block bChest,ItemStack[] isItems){
        this.sellers.put(bSign.getLocation(), new Seller(bChest,isItems));
        
        this.Save();
    }

    public Seller getSeller(Location lKey){
        if(this.sellers.containsKey(lKey)){
            return this.sellers.get(lKey);
        }
        return null;
    }

    public void removeSeller(Location lKey){
        if(this.sellers.containsKey(lKey)){
            this.sellers.remove(lKey);
        
            this.Save();
        }
    }
}
