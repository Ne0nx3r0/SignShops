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
import org.bukkit.event.block.Action;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class SignShopPlayerListener extends PlayerListener {
    private final SignShop plugin;
    private static Map<String, Block> mClickedSigns  = new HashMap<String, Block>();
    private static Map<String,Location> mConfirmSigns = new HashMap<String,Location>();

    public SignShopPlayerListener(SignShop instance){
        this.plugin = instance;
    }

    public void msg(Player player,String msg){
        player.sendMessage(ChatColor.GOLD+"[SignShop] "+ChatColor.YELLOW+msg);
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event){
        if(event.getItem() != null 
        && event.getItem().getType() == Material.REDSTONE
        && event.getClickedBlock() != null){
            if((event.getClickedBlock().getType() == Material.SIGN_POST
                    || event.getClickedBlock().getType() == Material.WALL_SIGN)
            && plugin.Storage.getSeller(event.getClickedBlock().getLocation()) == null){
                mClickedSigns.put(event.getPlayer().getName(),event.getClickedBlock());
                msg(event.getPlayer(),"Sign location stored!");

            }else if(event.getAction() == Action.LEFT_CLICK_BLOCK
            && event.getClickedBlock().getType() == Material.CHEST
            && mClickedSigns.containsKey(event.getPlayer().getName())){
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
                    if(item != null && item.getAmount() > 0) {
                        tempItems.add(item);
                    }
                }
                isShopItems = tempItems.toArray(new ItemStack[tempItems.size()]);

                if(isShopItems.length == 0){
                    msg(event.getPlayer(),"You have to put some items in the chest to sell!");
                    return;
                }

                String sForSale = "";

                for(ItemStack item : isShopItems){
                    sForSale += item.getAmount()+" "+formatMaterialName(item.getType().name())+", ";
                }

                msg(event.getPlayer(),sForSale.substring(0,sForSale.length()-2)
                        +" have been put up for sale, with a pricetag of "
                        +((Sign) bSign.getState()).getLine(3)+" "+plugin.iConomy.getBank().getCurrency()+"!");

                plugin.Storage.addSeller(event.getPlayer().getName(),bSign,event.getClickedBlock(),isShopItems);

                mClickedSigns.remove(event.getPlayer().getName());
            }
        }else if(event.getClickedBlock() != null &&
             (event.getClickedBlock().getType() == Material.SIGN_POST
            ||event.getClickedBlock().getType() == Material.WALL_SIGN)
        ){
            Sign sSign = (Sign) event.getClickedBlock().getState();

            float fPrice;

            //verify the sign has a price
            try{
                fPrice = Float.parseFloat(sSign.getLine(3));
            }
            catch(NumberFormatException nfe){
                return;
            }

            String sPlayerName = event.getPlayer().getName();

            if(!plugin.iConomy.getBank().getAccount(sPlayerName).hasEnough(fPrice)){
                msg(event.getPlayer(),"You don't have enough money! ( /money )");
                return;
            }

            Seller seller = plugin.Storage.getSeller(event.getClickedBlock().getLocation());
            if(seller == null){
                return;
            }

            if(event.getAction() != Action.RIGHT_CLICK_BLOCK &&
             (!mConfirmSigns.containsKey(sPlayerName)
             ||!mConfirmSigns.get(sPlayerName).equals(event.getClickedBlock().getLocation()))){
                mConfirmSigns.put(sPlayerName,event.getClickedBlock().getLocation());

                String sSelling = "Buy ";

                for(ItemStack item : seller.getItems()){
                    sSelling += item.getAmount()+" "+formatMaterialName(item.getType().name())+", ";
                }

                msg(event.getPlayer(),sSelling.substring(0,sSelling.length()-2)+" for "+fPrice+" "+plugin.iConomy.getBank().getCurrency()+"?");
                msg(event.getPlayer(),"Click again to confirm)");
            }else{
                ItemStack[] isItemsToGive = plugin.Storage.getSeller(event.getClickedBlock().getLocation()).getItems();

                Block bChest = seller.getChest();

                if(bChest.getType() != Material.CHEST){
                    plugin.Storage.removeSeller(event.getClickedBlock().getLocation());

                    msg(event.getPlayer(),"This shop seems to be closed! (Chest was destroyed)");

                    return;
                }

                Chest cbChest = (Chest) bChest.getState();

                ItemStack[] cbChestCurrentItems = cbChest.getInventory().getContents();

                HashMap<Integer,ItemStack> isItemsLeftover = cbChest.getInventory().removeItem(isItemsToGive);

                if(!isItemsLeftover.isEmpty()){
                    cbChest.getInventory().setContents(cbChestCurrentItems);

                    mConfirmSigns.remove(sPlayerName);

                    msg(event.getPlayer(),"This shop is out of stock!");

                    return;
                }

                if(fPrice < 0.0f){
                    fPrice = 0.0f;
                }

                plugin.iConomy.getBank().getAccount(sPlayerName).subtract(fPrice);

                if(seller.owner != null){
                    plugin.iConomy.getBank().getAccount(seller.owner).add(fPrice);
                }

                String sBuying = "Bought ";

                for(ItemStack item : isItemsToGive){
                    sBuying += item.getAmount()+" "+formatMaterialName(item.getType().name())+", ";
                    event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(),item);
                }

                msg(event.getPlayer(),
                    sBuying.substring(0,sBuying.length()-2)
                    +" for "+fPrice+" "
                    +plugin.iConomy.getBank().getCurrency()+"!");

                Player[] players = event.getPlayer().getServer().getOnlinePlayers();

                for(Player player : players){
                    if(player.getName() == seller.owner){
                        msg(player,ChatColor.GREEN+event.getPlayer().getName()+" has paid you "+fPrice+" "+plugin.iConomy.getBank().getCurrency()+"!");
                    }
                }

                mConfirmSigns.remove(sPlayerName);
            }
        }
    }

    public static String formatMaterialName(String sMaterial){
        sMaterial = sMaterial.replace("_"," ");
        Pattern p = Pattern.compile("(^|\\W)([a-z])");
        Matcher m = p.matcher(sMaterial.toLowerCase());
        StringBuffer sb = new StringBuffer(sMaterial.length());
        while(m.find()) {
                m.appendReplacement(sb, m.group(1) + m.group(2).toUpperCase() );
        }
        m.appendTail(sb);
        return sb.toString();
    }
}