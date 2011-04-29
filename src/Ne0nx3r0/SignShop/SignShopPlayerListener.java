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
import java.util.List;
import java.util.ArrayList;
import org.bukkit.Location;

public class SignShopPlayerListener extends PlayerListener {
    private final SignShop plugin;
    private static Map<String, Block> mClickedSigns  = new HashMap<String, Block>();
    private static Map<String,Location> mConfirmSigns = new HashMap<String,Location>();

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
                /* Also need to make sure this sign isn't already hooked up to a chest */
                /* Also need to make sure the signs 4th line is a number */
            ){
                mClickedSigns.put(event.getPlayer().getName(),event.getClickedBlock());
                msg(event.getPlayer(),"Sign location stored!");

            }else if(event.getClickedBlock().getType() == Material.CHEST
                && mClickedSigns.containsKey(event.getPlayer().getName())
            ){
                Block bSign = mClickedSigns.get(event.getPlayer().getName());

                if(bSign.getType() != Material.WALL_SIGN && bSign.getType() != Material.SIGN_POST){
                    mClickedSigns.remove(event.getPlayer().getName());
                    msg(event.getPlayer(),"The sign doesn't exist anymore.");
                    return;
                }

                Chest cbChest = (Chest) event.getClickedBlock().getState();

                ItemStack[] isShopItems = cbChest.getInventory().getContents();

                //remove extra values
                List<ItemStack> tempItems = new ArrayList<ItemStack>();
                for(ItemStack item : isShopItems) {
                    if(item != null) {
                        tempItems.add(item);
                    }
                }
                isShopItems = tempItems.toArray(new ItemStack[tempItems.size()]);

                for(ItemStack item : isShopItems){
                    if(item != null){
                        msg(event.getPlayer(),"+"+item.getAmount()+" "+item.getType().name());
                    }
                }

                msg(event.getPlayer(),"have been put up for sale, with a pricetag of "+((Sign) bSign.getState()).getLine(3)+"!");

                plugin.Storage.addSeller(bSign,event.getClickedBlock(),isShopItems);

                mClickedSigns.remove(event.getPlayer().getName());
            }
        }else if(event.getClickedBlock() != null &&
             (event.getClickedBlock().getType() == Material.SIGN_POST
            ||event.getClickedBlock().getType() == Material.WALL_SIGN)
        ){
            Sign sSign = (Sign) event.getClickedBlock().getState();

            float iPrice;

            //verify the sign has a price
            try{
                iPrice = Float.parseFloat(sSign.getLine(3));
            }
            catch(NumberFormatException nfe){
                System.out.println("WHAAAA?!");
                return;
            }

            if(!mConfirmSigns.containsKey(event.getPlayer().getName())){
                Seller seller = plugin.Storage.getSeller(event.getClickedBlock().getLocation());

                if(seller != null){
                    mConfirmSigns.put(event.getPlayer().getName(),event.getClickedBlock().getLocation());

                    String sSelling = "Buy ";

                    for(ItemStack item : seller.items){
                        sSelling += item.getType().name()+"("+item.getAmount()+"), ";
                    }

                    sSelling = sSelling.substring(0,sSelling.length()-2)+" for "+iPrice+"? (click again to confirm your purchase)";

                    msg(event.getPlayer(),sSelling);
                }
            }else{
                msg(event.getPlayer(),"Purchase confirmed! (no code yet :( )");
            }
        }
    }
}