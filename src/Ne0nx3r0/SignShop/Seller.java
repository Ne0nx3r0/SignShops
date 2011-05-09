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
    public Byte[] datas;
    public Short[] durabilities;

    public String owner;

    public Seller(String sPlayer,Block bChest,ItemStack[] isChestItems){
        this.owner = sPlayer;

        this.world = bChest.getWorld().getName();
        this.x = bChest.getLocation().getBlockX();
        this.y = bChest.getLocation().getBlockY();
        this.z = bChest.getLocation().getBlockZ();

        this.items = new Integer[isChestItems.length];
        this.amounts = new Integer[isChestItems.length];
        this.durabilities = new Short[isChestItems.length];
        this.datas = new Byte[isChestItems.length];

        for(int i=0;i<isChestItems.length;i++){
            if(isChestItems[i] != null && isChestItems[i].getAmount() > 0){
                this.items[i] = isChestItems[i].getTypeId();
                this.amounts[i] = isChestItems[i].getAmount();
                this.durabilities[i] = isChestItems[i].getDurability();
                
                if(isChestItems[i].getData() != null){
                    this.datas[i] = isChestItems[i].getData().getData();
                }
            }
        }
    }

    public ItemStack[] getItems(){
        ItemStack[] isItems = new ItemStack[items.length];

        for(int i=0;i<items.length;i++){
            isItems[i] = new ItemStack(items[i],amounts[i]);
            if(datas[i] != null){
                isItems[i].getData().setData(datas[i]);
            }
            if(durabilities[i] != null){
                isItems[i].setDurability(durabilities[i]);
            }
        }

        return isItems;
    }

    public Block getChest(){
        return Bukkit.getServer().getWorld(this.world).getBlockAt(this.x,this.y,this.z);
    }
}
