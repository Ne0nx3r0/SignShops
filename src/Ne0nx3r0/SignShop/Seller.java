package Ne0nx3r0.SignShop;

import org.bukkit.block.Block;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class Seller{
    private int x;
    private int y;
    private int z;

    private int[] items;
    private int[] amounts;

    public Seller(Block bSign,ItemStack[] isChestItems){
        Location lSign = bSign.getLocation();

        this.x = lSign.getBlockX();
        this.y = lSign.getBlockY();
        this.z = lSign.getBlockZ();

        this.items = new int[items.length];
        this.amounts = new int[items.length];

        for(int i = 0;i<isChestItems.length;i++){
            this.items[i] = isChestItems[i].getTypeId();
            this.amounts[i] = isChestItems[i].getAmount();
        }
    }
}
