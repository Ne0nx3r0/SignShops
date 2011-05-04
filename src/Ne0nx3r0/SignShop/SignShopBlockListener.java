package Ne0nx3r0.SignShop;

import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.Material;

public class SignShopBlockListener extends BlockListener{
    private final SignShop plugin;

    public SignShopBlockListener(SignShop instance){
        this.plugin = instance;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event){
        if(event.getBlock().getType() == Material.WALL_SIGN
        || event.getBlock().getType() == Material.SIGN_POST){
            plugin.Storage.removeSeller(event.getBlock().getLocation());
        }else if(event.getBlock().getType() == Material.CHEST){
            //todo: remove signs when the chest is destroyed, need reverse lookup
        }
    }

    @Override
    public void onBlockBurn(BlockBurnEvent event){
        if(event.getBlock().getType() == Material.WALL_SIGN
        || event.getBlock().getType() == Material.SIGN_POST){
            plugin.Storage.removeSeller(event.getBlock().getLocation());
        }else if(event.getBlock().getType() == Material.CHEST){
            //todo: remove signs when the chest is destroyed, need reverse lookup
        }
    }
}
