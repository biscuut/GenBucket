package codes.biscuit.simplegenbuckets.events;

import codes.biscuit.simplegenbuckets.SimpleGenBuckets;
import codes.biscuit.simplegenbuckets.timers.GenningTimer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

public class PlayerEvents implements Listener {

    private SimpleGenBuckets main;

    public PlayerEvents(SimpleGenBuckets main) {
        this.main = main;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent e) {
        if (!e.isCancelled() && main.getUtils().matchBucket(e.getItemInHand()) != null) {
            e.setCancelled(true);
            String bucket = main.getUtils().matchBucket(e.getItemInHand());
            Player p = e.getPlayer();
            if (main.getHookUtils().canBePlacedHere(p, e.getBlock().getLocation(), false) && main.getHookUtils().takeBucketPlaceCost(p, bucket)) {
                if (main.getConfigValues().getBucketType(bucket).equals("HORIZONTAL") && (e.getBlockAgainst().getFace(e.getBlock()).equals(BlockFace.UP) || e.getBlockAgainst().getFace(e.getBlock()).equals(BlockFace.DOWN))) {
                    if (!main.getConfigValues().getWrongDirectionMessage().equals("")) {
                        p.sendMessage(main.getConfigValues().getWrongDirectionMessage());
                    }
                    return;
                }
                startGenBucket(bucket, p, e.getBlock(), e.getBlockAgainst().getFace(e.getBlock()));
                if (main.getConfigValues().isNotInfinite(bucket) && !main.getHookUtils().getBypassPlayers().contains(p.getUniqueId())) {
                    ItemStack removeItem = e.getItemInHand();
                    removeItem.setAmount(e.getItemInHand().getAmount()-1);
                    p.getInventory().setItemInHand(removeItem);
                    if (!main.getConfigValues().getPlaceNormalMessage().equals("")) {
                        p.sendMessage(main.getConfigValues().getPlaceNormalMessage());
                    }
                } else {
                    if (!main.getConfigValues().getPlaceInfiniteMessage(main.getConfigValues().getBucketPlaceCost(bucket)).equals("")) {
                        p.sendMessage(main.getConfigValues().getPlaceInfiniteMessage(main.getConfigValues().getBucketPlaceCost(bucket)));
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBucketPlace(PlayerInteractEvent e) {
        if (!e.isCancelled() && e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getItem() != null && (e.getItem().getType().equals(Material.LAVA_BUCKET) ||
                e.getItem().getType().equals(Material.WATER_BUCKET)) && main.getUtils().matchBucket(e.getItem()) != null) {
            e.setCancelled(true);
            String bucket = main.getUtils().matchBucket(e.getItem());
            Player p = e.getPlayer();
            if (main.getHookUtils().canBePlacedHere(p, e.getClickedBlock().getRelative(e.getBlockFace()).getLocation(), false) && main.getHookUtils().takeBucketPlaceCost(p, bucket)) {
                if ((main.getConfigValues().getBucketType(bucket).equals("HORIZONTAL") || main.getConfigValues().getBucketType(bucket).equals("HORIZONTAL_CHUNK")) &&
                        (e.getBlockFace().equals(BlockFace.UP) || e.getBlockFace().equals(BlockFace.DOWN))) {
                    if (!main.getConfigValues().getWrongDirectionMessage().equals("")) {
                        p.sendMessage(main.getConfigValues().getWrongDirectionMessage());
                    }
                    return;
                }
                startGenBucket(bucket, p, e.getClickedBlock().getRelative(e.getBlockFace()), e.getBlockFace());
                if (main.getConfigValues().isNotInfinite(bucket) && !main.getHookUtils().getBypassPlayers().contains(p.getUniqueId())) {
                    ItemStack removeItem = e.getItem();
                    removeItem.setAmount(e.getItem().getAmount()-1);
                    p.getInventory().setItemInHand(removeItem);
                    if (!main.getConfigValues().getPlaceNormalMessage().equals("")) {
                        p.sendMessage(main.getConfigValues().getPlaceNormalMessage());
                    }
                } else {
                    if (!main.getConfigValues().getPlaceInfiniteMessage(main.getConfigValues().getBucketPlaceCost(bucket)).equals("")) {
                        p.sendMessage(main.getConfigValues().getPlaceInfiniteMessage(main.getConfigValues().getBucketPlaceCost(bucket)));
                    }
                }
            }
        }
    }

    private void startGenBucket(String bucket, Player p, Block block, BlockFace direction) {
        boolean chunkLimited = false;
        switch (main.getConfigValues().getBucketType(bucket)) {
            case "UPWARDS":
                direction = BlockFace.UP;
                break;
            case "DOWNWARDS":
                direction = BlockFace.DOWN;
                break;
            case "HORIZONTAL_CHUNK": case "OMNIDIRECTIONAL_CHUNK":
                chunkLimited = true;
                break;
        }
        int limit = main.getConfigValues().getVerticalTravel();
        if (direction != BlockFace.UP && direction != BlockFace.DOWN) {
            limit = main.getConfigValues().getHorizontalTravel();
        }
        new GenningTimer(p, main.getConfigValues().getBucketBlockMaterial(bucket), block,
                direction, main, limit, chunkLimited).runTaskTimer(main, 0L, main.getConfigValues().getBlockSpeedDelay());
    }

    @EventHandler
    public void onShopClick(InventoryClickEvent e) {
        if (e.getClickedInventory() != null && e.getClickedInventory().getName() != null &&
                e.getClickedInventory().getName().equals(main.getConfigValues().getGUITitle())) {
            e.setCancelled(true);
            Player p = (Player) e.getWhoClicked();
            if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().hasDisplayName())
                if (main.getConfigValues().getBucketFromShopName(e.getCurrentItem().getItemMeta().getDisplayName()) != null) {
                    String bucket = main.getConfigValues().getBucketFromShopName(e.getCurrentItem().getItemMeta().getDisplayName());
                    double price = main.getConfigValues().getBucketShopPrice(bucket);
                    int amount = 1;
                    if (e.getClick().equals(ClickType.SHIFT_LEFT) || e.getClick().equals(ClickType.SHIFT_RIGHT)) {
                        amount = 16;
                        price *= main.getConfigValues().getBulkBuyAmount();
                    }
                    if (main.getHookUtils().takeShopMoney(p, price)) {
                        ItemStack item = main.getUtils().getBucketItemStack(bucket, amount);
                        HashMap excessItems;
                        if (!main.getConfigValues().shopShouldDropItem()) {
                            if (p.getInventory().firstEmpty() == -1) {
                                if (!main.getConfigValues().getNoSpaceBuyMessage().equals("")) {
                                    p.sendMessage(main.getConfigValues().getNoSpaceBuyMessage());
                                }
                                return;
                            }
                        }
                        excessItems = p.getInventory().addItem(item);
                        for (Object excessItem : excessItems.values()) {
                            int itemCount = ((ItemStack)excessItem).getAmount();
                            while (itemCount > 64) {
                                ((ItemStack) excessItem).setAmount(64);
                                p.getWorld().dropItemNaturally(p.getLocation(), (ItemStack)excessItem);
                                itemCount = itemCount - 64;
                            }
                            if (itemCount > 0) {
                                ((ItemStack) excessItem).setAmount(itemCount);
                                p.getWorld().dropItemNaturally(p.getLocation(), (ItemStack)excessItem);
                            }
                        }
                        if (!main.getConfigValues().getBuyConfirmationMessage(amount).equals("")) {
                            p.sendMessage(main.getConfigValues().getBuyConfirmationMessage(amount));
                        }
                    }
                } else if (main.getConfigValues().getExitName().equals(e.getCurrentItem().getItemMeta().getDisplayName())) {
                    p.closeInventory();
                }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (main.getConfigValues().showUpdateMessage() && e.getPlayer().isOp()) {
            main.getUtils().checkUpdates(e.getPlayer());
        }
    }
}
