package Ne0nx3r0.SignShop;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Bukkit;

public class Seller{
    public String world;
    public int x;
    public int y;
    public int z;

    public Integer[] items;
    public Integer[] amounts;

    public Seller(Block bChest,ItemStack[] isChestItems){
        this.world = bChest.getWorld().getName();
        this.x = bChest.getLocation().getBlockX();
        this.y = bChest.getLocation().getBlockY();
        this.z = bChest.getLocation().getBlockZ();

        this.items = new Integer[isChestItems.length];
        this.amounts = new Integer[isChestItems.length];

        for(int i=0;i<isChestItems.length;i++){
            if(isChestItems[i] != null && isChestItems[i].getAmount() > 0){
                this.items[i] = isChestItems[i].getTypeId();
                this.amounts[i] = isChestItems[i].getAmount();
            }
        }
    }

    public ItemStack[] getItems(){
        ItemStack[] isItems = new ItemStack[items.length];

        for(int i=0;i<items.length;i++){
            isItems[i] = new ItemStack(items[i],amounts[i]);
        }

        return isItems;
    }

    public Block getChest(){
        return Bukkit.getServer().getWorld(this.world).getBlockAt(this.x,this.y,this.z);
    }
}
