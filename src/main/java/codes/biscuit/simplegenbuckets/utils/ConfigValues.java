package codes.biscuit.simplegenbuckets.utils;

import codes.biscuit.simplegenbuckets.SimpleGenBuckets;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ConfigValues {

    private SimpleGenBuckets main;

    public ConfigValues(SimpleGenBuckets main) {
        this.main = main;
    }

    HashMap<String, Material> getBucketMaterialList() {
        HashMap<String, Material> materials = new HashMap<>();
        for (String key : main.getConfig().getConfigurationSection("items").getKeys(false)) {
            try {
                materials.put(key, Material.valueOf(main.getConfig().getString("items."+key+".item.material")));
            } catch (Exception ignored) {}
        }
        return materials;
    }

    String getBucketItemName(String bucket) {
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("items."+bucket+".item.name"));
    }

    public Long getBlockSpeedDelay() {
        double bps = main.getConfig().getDouble("blockspeed");
        if (bps<1) bps = 1;
        else if (bps>20) bps = 20;
        return Math.round(1 / bps * 20);
    }

    public Material getBucketBlockMaterial(String bucket) {
        String rawMaterial = main.getConfig().getString("items."+bucket+".block.material");
        Material mat;
        if (rawMaterial.contains(":")) {
            rawMaterial = rawMaterial.split(":")[0];
        }
        try {
            mat = Material.valueOf(rawMaterial);
        } catch (IllegalArgumentException ex) {
            mat = Material.valueOf("DIRT");
            main.getLogger().severe("Your bucket block material for "+bucket+" is invalid!");
        }
        return mat;
    }

    ItemStack getBucketIngameItemStack(String bucket, int amount) {
        String rawMaterial = main.getConfig().getString("items."+bucket+".item.material");
        Material mat;
        if (rawMaterial.contains(":")) {
            String[] materialSplit = rawMaterial.split(":");
            try {
                mat = Material.valueOf(materialSplit[0]);
            } catch (IllegalArgumentException ex) {
                mat = Material.valueOf("LAVA_BUCKET");
                main.getLogger().severe("Your bucket item material for "+bucket+" is invalid!");
            }
            short damage;
            try {
                damage = Short.valueOf(materialSplit[1]);
            } catch (IllegalArgumentException ex) {
                damage = 1;
                main.getLogger().severe("Your bucket item damage/data for "+bucket+" is invalid!");
            }
            return new ItemStack(mat, amount, damage);
        } else {
            try {
                mat = Material.valueOf(rawMaterial);
            } catch (IllegalArgumentException ex) {
                mat = Material.valueOf("LAVA_BUCKET");
                main.getLogger().severe("Your bucket item material for "+bucket+" is invalid!");
            }
            return new ItemStack(mat, amount);
        }
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

    public String getBucketDirection(String bucket) {
        return main.getConfig().getString("items."+bucket+".block.direction");
    }

    public boolean bucketExists(String bucket) {
        return main.getConfig().isSet("items."+bucket);
    }

    boolean bucketItemShouldGlow(String bucket) {
        return main.getConfig().getBoolean("items."+bucket+".item.glow");
    }

    public Set<String> getBucketList() {
        return main.getConfig().getConfigurationSection("items").getKeys(false);
    }

    List<String> getBucketItemLore(String bucket) {
        List<String> uncolouredList = main.getConfig().getStringList("items."+bucket+".item.lore");
        List<String> colouredList = new ArrayList<>();
        for (String s : uncolouredList) {
            colouredList.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        return colouredList;
    }

    String getBucketName(String bucket) {
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("items."+bucket+".item.name"));
    }

    public boolean giveShouldDropItem() {
        return main.getConfig().getBoolean("give-drop-item-if-full");
    }

    public boolean shopShouldDropItem() {
        return main.getConfig().getBoolean("shop-drop-item-if-full");
    }

    public String getGiveMessage(Player p, int amount, String bucket) {
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.give"))
                .replace("{player}", p.getName()).replace("{amount}", String.valueOf(amount)).replace("{bucket}", bucket);
    }

    public String getReceiveMessage(int amount, double price, String bucket) {
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.receive"))
                .replace("{amount}", String.valueOf(amount)).replace("{price}", String.valueOf(price)).replace("{amount}", String.valueOf(amount)).replace("{bucket}", bucket);
    }

    public String getNoPermissionCommandMessage() {
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.no-permission-command"));
    }

    public String getNoPermissionPlaceMessage() {
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.no-permission-place"));
    }

    public String getNoFactionMessage() {
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.no-faction"));
    }

    public String getOnlyClaimMessage() {
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.cannot-place-claim"));
    }

    public String getRegionProtectedMessage() {
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.region-protected"));
    }

    public String getPlaceNormalMessage() {
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.place-message-regular"));
    }

    public String getPlaceInfiniteMessage(double money) {
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.place-message-infinite").replace("{money}", String.valueOf(money)));
    }

    public String notEnoughMoneyPlaceMessage(double money) {
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.not-enough-money-place").replace("{cost}", String.valueOf(money)));
    }

    public String notEnoughMoneyBuyMessage(double money) {
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.not-enough-money-shop").replace("{cost}", String.valueOf(money)));
    }

    public String getWrongDirectionMessage() {
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.wrong-direction"));
    }

    public String getNoSpaceBuyMessage() {
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.not-enough-space-buy"));
    }

    public String getCannotPlaceYLevelMessage() {
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.cannot-place-y-level"));
    }

    public String getBuyConfirmationMessage(int amount) {
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.buy-success").replace("{amount}", String.valueOf(amount)));
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

    public boolean isBucketInfinite(String bucket) {
        return main.getConfig().getBoolean("items."+bucket+".infinite");
    }

    public double getBucketPlaceCost(String bucket) {
        return main.getConfig().getDouble("items."+bucket+".infinite-place-price");
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

    public boolean isNotInfinite(String bucket) {
        return !main.getConfig().getBoolean("items." + bucket + ".infinite");
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
                mat = Material.valueOf("BARRIER");
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
                mat = Material.valueOf("BARRIER");
                main.getLogger().severe("Your fill block material is invalid!");
            }
            return new ItemStack(mat, 1);
        }
    }

    public String getGUITitle() {
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("gui.title"));
    }

    public String getExitName() {
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("gui.exit.name"));
    }

    public String getFillName() {
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("gui.fill.name"));
    }

    public List<String> getExitLore() {
        List<String> uncolouredList = main.getConfig().getStringList("gui.exit.lore");
        List<String> colouredList = new ArrayList<>();
        for (String s : uncolouredList) {
            colouredList.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        return colouredList;
    }

    public List<String> getFillLore() {
        List<String> uncolouredList = main.getConfig().getStringList("gui.fill.lore");
        List<String> colouredList = new ArrayList<>();
        for (String s : uncolouredList) {
            colouredList.add(ChatColor.translateAlternateColorCodes('&', s));
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

    public String getBucketFromSlot(int slot) {
        for (String bucket : getBucketList()) {
            if (main.getConfig().getInt("items."+bucket+".gui.slot") == slot) {
                return bucket;
            }
        }
        return null;
    }

    public ItemStack getBucketShopItemStack(String bucket) {
        String rawMaterial = main.getConfig().getString("items."+bucket+".gui.material");
        Material mat;
        if (rawMaterial.contains(":")) {
            String[] materialSplit = rawMaterial.split(":");
            try {
                mat = Material.valueOf(materialSplit[0]);
            } catch (IllegalArgumentException ex) {
                mat = Material.valueOf("LAVA_BUCKET");
                main.getLogger().severe("Your bucket shop item material for "+bucket+" is invalid!");
            }
            short damage;
            try {
                damage = Short.valueOf(materialSplit[1]);
            } catch (IllegalArgumentException ex) {
                damage = 1;
                main.getLogger().severe("Your bucket shop item damage/data for "+bucket+" is invalid!");
            }
            return new ItemStack(mat, 1, damage);
        } else {
            try {
                mat = Material.valueOf(rawMaterial);
            } catch (IllegalArgumentException ex) {
                mat = Material.valueOf("LAVA_BUCKET");
                main.getLogger().severe("Your bucket shop item material for "+bucket+" is invalid!");
            }
            return new ItemStack(mat, 1);
        }
    }

    public String getBucketShopName(String bucket) {
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("items."+bucket+".gui.name"));
    }


    public List<String> getBucketShopLore(String bucket) {
        List<String> uncolouredList = main.getConfig().getStringList("items."+bucket+".gui.lore");
        List<String> colouredList = new ArrayList<>();
        for (String s : uncolouredList) {
            colouredList.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        return colouredList;
    }

    public boolean bucketShopShouldGlow(String bucket) {
        return main.getConfig().getBoolean("items."+bucket+".gui.glow");
    }

    public String getBucketFromShopName(String name) {
        name = ChatColor.translateAlternateColorCodes('&', name);
        for (String bucket : getBucketList()) {
            if (ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("items."+bucket+".gui.name")).equals(name)) {
                return bucket;
            }
        }
        return null;
    }

    public double getBucketShopPrice(String bucket) {
        return main.getConfig().getDouble("items."+bucket+".gui-price");
    }

    public boolean showUpdateMessage() {
        return main.getConfig().getBoolean("show-update-messages");
    }

    public int getMaxY() {
        return main.getConfig().getInt("y-limit");
    }

    public int getMaxChunks() {
        return main.getConfig().getInt("chunk-travel");
    }

    Set<String> getRecipeBuckets() {
        if (main.getConfig().getConfigurationSection("recipes") != null) {
            Set<String> bucketList = new HashSet<>();
            for(String key : main.getConfig().getConfigurationSection("recipes").getKeys(false)) {
                if (bucketExists(key)) bucketList.add(key);
            }
            return bucketList;
        } else {
            return null;
        }
    }

    HashMap<Character, HashMap<Material, Short>> getIngredients(String bucket) {
        HashMap<Character, HashMap<Material, Short>> ingredients = new HashMap<>();
        for (String ingredientSymbol : main.getConfig().getConfigurationSection("recipes."+bucket+".symbols").getKeys(false)) {
            String rawMaterial = main.getConfig().getString("recipes."+bucket+".symbols."+ingredientSymbol);
            Material mat;
            short damage = 1;
            if (rawMaterial.contains(":")) {
                String[] materialSplit = rawMaterial.split(":");
                try {
                    mat = Material.valueOf(materialSplit[0]);
                } catch (IllegalArgumentException ex) {
                    main.getLogger().severe("Your ingredient material for symbol "+ingredientSymbol+" in bucket "+bucket+" is invalid!");
                    return null;
                }
                try {
                    damage = Short.valueOf(materialSplit[1]);
                } catch (IllegalArgumentException ex) {
                    main.getLogger().severe("Your ingredient data/damage for symbol "+ingredientSymbol+" in bucket "+bucket+" is invalid!");
                    return null;
                }
            } else {
                try {
                    mat = Material.valueOf(rawMaterial);
                } catch (IllegalArgumentException ex) {
                    main.getLogger().severe("Your ingredient material for symbol "+ingredientSymbol+" in bucket "+bucket+" is invalid!");
                    return null;
                }
            }
            HashMap<Material, Short> item = new HashMap<>();
            item.put(mat, damage);
            ingredients.put(ingredientSymbol.toCharArray()[0], item);
        }
        if (ingredients.size() > 0) {
            return ingredients;
        } else {
            return null;
        }
    }

    int getRecipeAmount(String bucket) {
        return main.getConfig().getInt("recipes."+bucket+".outcome-amount");
    }

    List<String> getRecipeShape(String bucket) {
        List<String> shapeList = main.getConfig().getStringList("recipes."+bucket+".recipe");
        if(shapeList.size() == 3) {
            return shapeList;
        } else {
            return null;
        }
    }

    double getConfigVersion() {
        return main.getConfig().getDouble("config-version");
    }
}
