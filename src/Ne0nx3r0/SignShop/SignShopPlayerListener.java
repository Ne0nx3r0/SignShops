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
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.bukkit.Bukkit;
import org.bukkit.event.block.Action;

public class SignShopPlayerListener extends PlayerListener {
    private final SignShop plugin;
    private static Map<String, Location> mClicks  = new HashMap<String, Location>();
    private static Map<String,Location> mConfirmSigns = new HashMap<String,Location>();

    public SignShopPlayerListener(SignShop instance){
        this.plugin = instance;
    }

    //msg a player object
    public void msg(Player player,String msg){
        player.sendMessage(ChatColor.GOLD+"[SignShop] "+ChatColor.YELLOW+msg);
    }

    //look up a player by player.getName()
    public boolean msg(String sPlayer,String msg){
        Player[] players = Bukkit.getServer().getOnlinePlayers();

        for(Player player : players){
            if(player.getName().equals(sPlayer)){
                msg(player,msg);
                return true;
            }
        }
        return false;
    }

    public static String stringFormat(Material material){
        String sMaterial = material.name().replace("_"," ");
        Pattern p = Pattern.compile("(^|\\W)([a-z])");
        Matcher m = p.matcher(sMaterial.toLowerCase());
        StringBuffer sb = new StringBuffer(sMaterial.length());

        while(m.find()){
            m.appendReplacement(sb, m.group(1) + m.group(2).toUpperCase() );
        }

        m.appendTail(sb);

        return sb.toString();
    }



    @Override
    public void onPlayerInteract(PlayerInteractEvent event){
        if(event.getClickedBlock() == null){
            return;
        }

        Block bClicked = event.getClickedBlock();
//clicked a sign with redstone
        if(event.getItem() != null && event.getItem().getType() == Material.REDSTONE){
            if(bClicked.getType() == Material.SIGN_POST
            || bClicked.getType() == Material.WALL_SIGN){

                String[] sLines = ((Sign) bClicked.getState()).getLines();

                try{
                    float fPrice = Float.parseFloat(sLines[3]);
                }
                catch(NumberFormatException nFE){
                    return;
                }

                if(!sLines[0].equals("[Buy]")
                && !sLines[0].equals("[Sell]")
                && !sLines[0].equals("[Donate]")){
                    return;
                }

                // verify this isn't a shop already
                if(plugin.Storage.getSeller(event.getClickedBlock().getLocation()) == null){
                    mClicks.put(event.getPlayer().getName(),event.getClickedBlock().getLocation());

                    msg(event.getPlayer(),"Sign location stored!");

                    return;
                }
//left clicked a chest and has already clicked a sign
            }else if(event.getAction() == Action.LEFT_CLICK_BLOCK
            && event.getClickedBlock().getType() == Material.CHEST
            && mClicks.containsKey(event.getPlayer().getName())){

                Block bSign = mClicks.get(event.getPlayer().getName()).getBlock();

                String[] sLines = ((Sign) bSign.getState()).getLines();

                try{
                    float fPrice = Float.parseFloat(sLines[3]);
                }
                catch(NumberFormatException nFE){
                    msg(event.getPlayer(),"The sign you clicked no longer has a valid price!");
                    return;
                }

                if(!sLines[0].equals("[Buy]")
                && !sLines[0].equals("[Sell]")
                && !sLines[0].equals("[Donate]")){
                    msg(event.getPlayer(),"The sign you clicked no longer has a valid operation!");
                    return;
                }

                if(!sLines[0].equals("[Buy]")
                && !sLines[0].equals("[Sell]")
                && !sLines[0].equals("[Donate]")){
                    return;
                }

                Chest cbChest = (Chest) event.getClickedBlock().getState();
                ItemStack[] isChestItems = cbChest.getInventory().getContents();

                //remove extra values
                List<ItemStack> tempItems = new ArrayList<ItemStack>();
                for(ItemStack item : isChestItems) {
                    if(item != null && item.getAmount() > 0) {
                        tempItems.add(item);
                    }
                }
                isChestItems = tempItems.toArray(new ItemStack[tempItems.size()]);

                if(isChestItems.length == 0){
                    msg(event.getPlayer(),"Chest is empty!");
                    return;
                }

                String sItemsString = "";
                for(ItemStack item : isChestItems){
                    sItemsString += item.getAmount()+" "+stringFormat(item.getType())+", ";
                }

                String sOperation = "put for sell";

                if(sLines[0].equals("[Sell]")){
                    sOperation = "put for users to sell you";
                }else if(sLines[0].equals("[Donate]")){
                    sOperation = "set as donations";
                }

                msg(event.getPlayer(),sItemsString.substring(0,sItemsString.length()-2)+" have been "+sOperation+"!");

                plugin.Storage.addSeller(event.getPlayer().getName(),bSign,event.getClickedBlock(),isChestItems);

                mClicks.remove(event.getPlayer().getName());

                return;
            }
        }
        else if(bClicked.getType() == Material.SIGN_POST
        || bClicked.getType() == Material.WALL_SIGN){
            Sign sbSign = (Sign) bClicked.getState();
            String[] sLines = sbSign.getLines();

//verify the sign operation
            if(!sLines[0].equals("[Buy]")
            && !sLines[0].equals("[Sell]")
            && !sLines[0].equals("[Donate]")){
                return;
            }

//verify the sign has a price
            float fPrice;
            if(sLines[0].equals("[Donate]")){
                fPrice = 0.0f;
            }else{
                try{
                    fPrice = Float.parseFloat(sLines[3]);
                    if(fPrice < 0.0f){
                        fPrice = 0.0f;
                    }
                }
                catch(NumberFormatException nfe){
                    return;
                }
            }

//verify the sign has a seller
            Seller seller = plugin.Storage.getSeller(event.getClickedBlock().getLocation());

            if(seller == null){
                return;
            }

            ItemStack[] isItems = seller.getItems();

            if(sLines[0].equals("[Buy]")){
//verify the player has enough money
                if(!plugin.iConomy.getAccount(event.getPlayer().getName()).getHoldings().hasEnough(fPrice)){
                    msg(event.getPlayer(),"You don't have "+plugin.iConomy.format(fPrice)+"!");

                    return;
                }

//verify the chest has the items it's selling
                Chest cbChest = (Chest) seller.getChest().getState();
                ItemStack[] isChestTemp = cbChest.getInventory().getContents();
                
                if(!(cbChest.getInventory().removeItem(isItems)).isEmpty()){
                    //reset chest inventory
                    cbChest.getInventory().setContents(isChestTemp);

                    msg(event.getPlayer(),"This shop is out of stock!");

                    return;
                }

//exchange money
                plugin.iConomy.getAccount(event.getPlayer().getName()).getHoldings().subtract(fPrice);

                if(seller.owner != null){
                    plugin.iConomy.getAccount(seller.owner).getHoldings().add(fPrice);
                    msg(seller.owner,ChatColor.GREEN+event.getPlayer().getName()
                        +" has paid you "+plugin.iConomy.format(fPrice)+" for purchased goods!");
                }

//give the items, note the items were already removed from the chest
                String sBuying = "";
                for(ItemStack item : isItems){
                    sBuying += item.getAmount()+" "+stringFormat(item.getType())+", ";
                    event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(),item);
                }

//confirmation messages
                msg(event.getPlayer(),"Bought "+sBuying.substring(0,sBuying.length()-2)
                    +" for "+plugin.iConomy.format(fPrice)+"!");

                Player[] players = event.getPlayer().getServer().getOnlinePlayers();

                return;

            }else if(sLines[0].equals("[Sell]")){
                return;
            }else if(sLines[0].equals("[Donate]")){
                return;
            }
        }
    }
}