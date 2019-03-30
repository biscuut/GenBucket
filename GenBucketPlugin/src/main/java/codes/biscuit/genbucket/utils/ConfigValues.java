package codes.biscuit.genbucket.utils;

import codes.biscuit.genbucket.GenBucket;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ConfigValues {

    private GenBucket main;

    public ConfigValues(GenBucket main) {
        this.main = main;
    }

    public void loadBuckets() {
        main.getBucketManager().getBuckets().clear();
        for (String bucketId : main.getConfig().getConfigurationSection("items").getKeys(false)) {
            Bucket bucket = main.getBucketManager().createBucket(bucketId);
            ItemStack item = main.getUtils().itemFromString(main.getConfig().getString("items."+bucketId+".item.material").toUpperCase());
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(Utils.color(main.getConfig().getString("items."+bucketId+".item.name")));
            itemMeta.setLore(Utils.colorLore(main.getConfig().getStringList("items."+bucketId+".item.lore")));
            item.setItemMeta(itemMeta);
            if (main.getConfig().getBoolean("items."+bucketId+".item.glow")) {
                main.getUtils().addGlow(item);
            }
            bucket.setItem(item);
            bucket.setBlockMaterial(main.getUtils().materialFromString(main.getConfig().getString("items."+bucketId+".block.material")));
            try {
                bucket.setDirection(Bucket.Direction.valueOf(main.getConfig().getString("items."+bucketId+".block.direction").toUpperCase()));
            } catch (IllegalArgumentException ex) {
                main.getBucketManager().getBuckets().remove(bucketId);
                continue;
            }
            bucket.setByChunk(main.getConfig().getBoolean("items."+bucketId+".block.count-by-chunk"));
            bucket.setPatch(main.getConfig().getBoolean("items."+bucketId+".block.patch"));
//            bucket.setWidth(main.getConfig().getInt("items."+bucket+".block.width"));
            ItemStack guiItem = main.getUtils().itemFromString(main.getConfig().getString("items."+bucketId+".gui.material").toUpperCase());
            itemMeta = guiItem.getItemMeta();
            itemMeta.setDisplayName(Utils.color(main.getConfig().getString("items."+bucketId+".gui.name")));
            itemMeta.setLore(Utils.colorLore(main.getConfig().getStringList("items."+bucketId+".gui.lore")));
            guiItem.setItemMeta(itemMeta);
            if (main.getConfig().getBoolean("items."+bucketId+".gui.glow")) {
                main.getUtils().addGlow(guiItem);
            }
            bucket.setGuiItem(guiItem);
            bucket.setSlot(main.getConfig().getInt("items."+bucketId+".gui.slot"));
            bucket.setBuyPrice(main.getConfig().getDouble("items."+bucketId+".buy-price"));
            bucket.setPlacePrice(main.getConfig().getDouble("items."+bucketId+".place-price"));
            bucket.setInfinite(main.getConfig().getBoolean("items."+bucketId+".infinite"));
        }
    }

    public Long getBlockSpeedDelay() {
        double bps = main.getConfig().getDouble("speed");
        if (bps<1) bps = 1;
        else if (bps>20) bps = 20;
        return Math.round(1 / bps * 20);
    }

    public List<Material> getIgnoredBlockList() {
        List<Material> materialList = new ArrayList<>();
        for (String rawMaterial : main.getConfig().getStringList("ignored-blocks")) {
            try {
                materialList.add(Material.valueOf(rawMaterial));
            } catch (Exception ignored) {}
        }
        return materialList;
    }

    public boolean giveShouldDropItem() {
        return main.getConfig().getBoolean("give-drop-item-if-full");
    }

    public boolean shopShouldDropItem() {
        return main.getConfig().getBoolean("shop-drop-item-if-full");
    }

    public String getGiveMessage(Player p, int amount, Bucket bucket) {
        return Utils.color(main.getConfig().getString("messages.give"))
                .replace("{player}", p.getName()).replace("{amount}", String.valueOf(amount)).replace("{bucket}", bucket.getId());
    }

    public String getReceiveMessage(int amount, double price, Bucket bucket) {
        return Utils.color(main.getConfig().getString("messages.receive"))
                .replace("{amount}", String.valueOf(amount)).replace("{price}", String.valueOf(price)).replace("{amount}", String.valueOf(amount)).replace("{bucket}", bucket.getId());
    }

    public String getNoPermissionCommandMessage() {
        return Utils.color(main.getConfig().getString("messages.no-permission-command"));
    }

    public String getNoPermissionPlaceMessage() {
        return Utils.color(main.getConfig().getString("messages.no-permission-place"));
    }

    public String getNoFactionMessage() {
        return Utils.color(main.getConfig().getString("messages.no-faction"));
    }

    public String getOnlyClaimMessage() {
        return Utils.color(main.getConfig().getString("messages.cannot-place-claim"));
    }

//    public String getRegionProtectedMessage() {
//        return Utils.color(main.getConfig().getString("messages.region-protected"));
//    }

    public String getPlaceNormalMessage(double money) {
        return Utils.color(main.getConfig().getString("messages.place-message-regular").replace("{money}", String.valueOf(money)));
    }

    public String getPlaceInfiniteMessage(double money) {
        return Utils.color(main.getConfig().getString("messages.place-message-infinite").replace("{money}", String.valueOf(money)));
    }

    public String notEnoughMoneyPlaceMessage(double money) {
        return Utils.color(main.getConfig().getString("messages.not-enough-money-place").replace("{cost}", String.valueOf(money)));
    }

    public String notEnoughMoneyBuyMessage(double money) {
        return Utils.color(main.getConfig().getString("messages.not-enough-money-shop").replace("{cost}", String.valueOf(money)));
    }

    public String getWrongDirectionMessage() {
        return Utils.color(main.getConfig().getString("messages.wrong-direction"));
    }

    public String getNoSpaceBuyMessage() {
        return Utils.color(main.getConfig().getString("messages.not-enough-space-buy"));
    }

    public String getCannotPlaceYLevelMessage() {
        return Utils.color(main.getConfig().getString("messages.cannot-place-y-level"));
    }

    public String getBuyConfirmationMessage(int amount) {
        return Utils.color(main.getConfig().getString("messages.buy-success").replace("{amount}", String.valueOf(amount)));
    }

    public String getNearbyPlayerMessage() {
        return Utils.color(main.getConfig().getString("messages.cannot-place-nearby-players"));
    }

    public int getVerticalTravel() {
        return main.getConfig().getInt("vertical-travel");
    }

    public int getHorizontalTravel() {
        return main.getConfig().getInt("horizontal-travel");
    }

    public boolean needsFaction() {
        return main.getConfig().getBoolean("factions.requires-faction");
    }

    public boolean cantPlaceWilderness() {
        return !main.getConfig().getBoolean("factions.can-place-wilderness");
    }

    public boolean isFactionsHookEnabled() {
        return main.getConfig().getBoolean("hooks.factions");
    }

    public boolean isWorldGuardHookEnabled() {
        return main.getConfig().getBoolean("hooks.worldguard");
    }

    public boolean isWorldBorderHookEnabled() {
        return main.getConfig().getBoolean("hooks.worldborder");
    }

    public boolean isCoreProtectHookEnabled() {
        return main.getConfig().getBoolean("hooks.coreprotect");
    }

    public boolean isGUIEnabled() { return main.getConfig().getBoolean("gui.enabled"); }

    public int getGUIRows() { return main.getConfig().getInt("gui.rows"); }

    public ItemStack getExitItemStack() {
        String rawMaterial = main.getConfig().getString("gui.exit.item");
        Material mat;
        if (rawMaterial.contains(":")) {
            String[] materialSplit = rawMaterial.split(":");
            try {
                mat = Material.valueOf(materialSplit[0]);
            } catch (IllegalArgumentException ex) {
                mat = Material.valueOf("BARRIER");
                main.getLogger().severe("Your exit item material is invalid!");
            }
            short damage;
            try {
                damage = Short.valueOf(materialSplit[1]);
            } catch (IllegalArgumentException ex) {
                damage = 1;
                main.getLogger().severe("Your exit item damage/data is invalid!");
            }
            return new ItemStack(mat, 1, damage);
        } else {
            try {
                mat = Material.valueOf(rawMaterial);
            } catch (IllegalArgumentException ex) {
                mat = Material.valueOf("BARRIER");
                main.getLogger().severe("Your exit item material is invalid!");
            }
            return new ItemStack(mat, 1);
        }
    }

    public ItemStack getFillItemStack() {
        String rawMaterial = main.getConfig().getString("gui.fill.item");
        Material mat;
        if (rawMaterial.contains(":")) {
            String[] materialSplit = rawMaterial.split(":");
            try {
                mat = Material.valueOf(materialSplit[0]);
            } catch (IllegalArgumentException ex) {
                mat = Material.valueOf("AIR");
                main.getLogger().severe("Your fill item material is invalid!");
            }
            short damage;
            try {
                damage = Short.valueOf(materialSplit[1]);
            } catch (IllegalArgumentException ex) {
                damage = 1;
                main.getLogger().severe("Your fill item damage/data is invalid!");
            }
            return new ItemStack(mat, 1, damage);
        } else {
            try {
                mat = Material.valueOf(rawMaterial);
            } catch (IllegalArgumentException ex) {
                mat = Material.valueOf("AIR");
                main.getLogger().severe("Your fill block material is invalid!");
            }
            return new ItemStack(mat, 1);
        }
    }

    public String getGUITitle() {
        return Utils.color(main.getConfig().getString("gui.title"));
    }

    public String getExitName() {
        return Utils.color(main.getConfig().getString("gui.exit.name"));
    }

    public String getFillName() {
        return Utils.color(main.getConfig().getString("gui.fill.name"));
    }

    public List<String> getExitLore() {
        List<String> uncolouredList = main.getConfig().getStringList("gui.exit.lore");
        List<String> colouredList = new ArrayList<>();
        for (String s : uncolouredList) {
            colouredList.add(Utils.color(s));
        }
        return colouredList;
    }

    public List<String> getFillLore() {
        List<String> uncolouredList = main.getConfig().getStringList("gui.fill.lore");
        List<String> colouredList = new ArrayList<>();
        for (String s : uncolouredList) {
            colouredList.add(Utils.color(s));
        }
        return colouredList;
    }

    public List<Integer> getExitSlots() {
        return main.getConfig().getIntegerList("gui.exit.slots");
    }

    public boolean isExitGlowing() {
        return main.getConfig().getBoolean("gui.exit.glow");
    }

    public boolean isFillGlowing() {
        return main.getConfig().getBoolean("gui.fill.glow");
    }

    public int getBulkBuyAmount() {
        return main.getConfig().getInt("gui.bulk-buy-amount");
    }

    public boolean showUpdateMessage() {
        return main.getConfig().getBoolean("show-update-messages");
    }

    public int getMaxY() {
        return main.getConfig().getInt("height-limit");
    }

    public int getMaxChunks() {
        return main.getConfig().getInt("chunk-travel");
    }

    Set<String> getRecipeBuckets() {
        if (main.getConfig().getConfigurationSection("recipes") != null) {
            Set<String> bucketList = new HashSet<>();
            for(String key : main.getConfig().getConfigurationSection("recipes").getKeys(false)) {
                if (main.getBucketManager().getBuckets().containsKey(key)) bucketList.add(key);
            }
            return bucketList;
        } else {
            return null;
        }
    }

    Map<Character, Map<Material, Short>> getIngredients(Bucket bucket) {
        Map<Character, Map<Material, Short>> ingredients = new HashMap<>();
        for (String ingredientSymbol : main.getConfig().getConfigurationSection("recipes."+bucket.getId()+".symbols").getKeys(false)) {
            String rawMaterial = main.getConfig().getString("recipes."+bucket.getId()+".symbols."+ingredientSymbol);
            Material mat;
            short damage = 1;
            if (rawMaterial.contains(":")) {
                String[] materialSplit = rawMaterial.split(":");
                try {
                    mat = Material.valueOf(materialSplit[0]);
                } catch (IllegalArgumentException ex) {
                    main.getLogger().severe("Your ingredient material for symbol "+ingredientSymbol+" in bucket "+bucket.getId()+" is invalid!");
                    return null;
                }
                try {
                    damage = Short.valueOf(materialSplit[1]);
                } catch (IllegalArgumentException ex) {
                    main.getLogger().severe("Your ingredient data/damage for symbol "+ingredientSymbol+" in bucket "+bucket.getId()+" is invalid!");
                    return null;
                }
            } else {
                try {
                    mat = Material.valueOf(rawMaterial);
                } catch (IllegalArgumentException ex) {
                    main.getLogger().severe("Your ingredient material for symbol "+ingredientSymbol+" in bucket "+bucket.getId()+" is invalid!");
                    return null;
                }
            }
            Map<Material, Short> item = new HashMap<>();
            item.put(mat, damage);
            ingredients.put(ingredientSymbol.toCharArray()[0], item);
        }
        if (ingredients.size() > 0) {
            return ingredients;
        } else {
            return null;
        }
    }

    int getRecipeAmount(Bucket bucket) {
        return main.getConfig().getInt("recipes."+bucket.getId()+".outcome-amount");
    }

    List<String> getRecipeShape(Bucket bucket) {
        List<String> shapeList = main.getConfig().getStringList("recipes."+bucket.getId()+".recipe");
        if(shapeList.size() == 3) {
            return shapeList;
        } else {
            return null;
        }
    }

    double getConfigVersion() {
        return main.getConfig().getDouble("config-version");
    }

    public boolean bucketsDisappearDrop() {
        return main.getConfig().getBoolean("infinite-buckets-disappear");
    }

    public double getPlayerCheckRadius() {
        return main.getConfig().getDouble("player-check-radius");
    }

    public double getSpongeCheckRadius() {
        return main.getConfig().getDouble("sponge-check-radius");
    }

    public boolean cancellingEnabled() {
        return main.getConfig().getBoolean("enable-cancelling");
    }
}
