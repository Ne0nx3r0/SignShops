package Ne0nx3r0.SignShop;

import org.bukkit.block.Block;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class Seller{
    public int sx;
    public int sy;
    public int sz;

    public int cx;
    public int cy;
    public int cz;

    public ItemStack[] items;

    public Seller(Block bSign,Block bChest,ItemStack[] isChestItems){
        Location lSign = bSign.getLocation();
        this.sx = lSign.getBlockX();
        this.sy = lSign.getBlockY();
        this.sz = lSign.getBlockZ();

        Location lChest = bChest.getLocation();
        this.cx = lChest.getBlockX();
        this.cy = lChest.getBlockY();
        this.cz = lChest.getBlockZ();

        this.items = isChestItems;
    }
}
