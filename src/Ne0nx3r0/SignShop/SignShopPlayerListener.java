package Ne0nx3r0.SignShop;

import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerInteractEvent;
import java.util.Map;
import java.util.HashMap;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

public class SignShopPlayerListener extends PlayerListener {
    private final SignShop plugin;
    private static Map<String, Block> mClickedSigns  = new HashMap<String, Block>();

    public SignShopPlayerListener(SignShop instance){
        this.plugin = instance;
    }

    public void msg(Player player,String msg){
        player.sendMessage(ChatColor.GRAY+"[SignShop] "+ChatColor.DARK_GRAY+msg);
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event){
        if(event.getItem() != null && event.getItem().getType() == Material.REDSTONE){
            if((event.getClickedBlock().getType() == Material.SIGN_POST
                    || event.getClickedBlock().getType() == Material.WALL_SIGN)
                &&!mClickedSigns.containsKey(event.getPlayer().getName())
                /* Also need to make sure this sign isn't already hooked up to a chest */
                /* Also need to make sure the signs 4th line is a number */
            ){
                mClickedSigns.put(event.getPlayer().getName(),event.getClickedBlock());
                msg(event.getPlayer(),"Sign location stored!");

            }else if(event.getClickedBlock().getType() == Material.CHEST
                && mClickedSigns.containsKey(event.getPlayer().getName())
            ){
                Block bSign = mClickedSigns.get(event.getPlayer().getName());

                if(bSign.getType() != Material.WALL_SIGN || bSign.getType() != Material.SIGN_POST){
                    mClickedSigns.remove(event.getPlayer().getName());
                    msg(event.getPlayer(),"Then sign you placed doesn't exist anymore.");
                    return;
                }

                Chest cbChest = (Chest) event.getClickedBlock();

                ItemStack[] isShopItems = cbChest.getInventory().getContents();

                for(ItemStack item : isShopItems){
                    msg(event.getPlayer(),""+item.getAmount()+" "+item.getType().name());
                }

                msg(event.getPlayer(),"has been put up for sale, with a pricetag of "+((Sign) bSign).getLine(3)+"!");

                plugin.Storage.addSeller(bSign,event.getClickedBlock(),isShopItems);
            }
        }
    }
}