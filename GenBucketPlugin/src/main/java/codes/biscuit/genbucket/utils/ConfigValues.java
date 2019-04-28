package codes.biscuit.genbucket.utils;

import codes.biscuit.genbucket.GenBucket;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ConfigValues {

    private GenBucket main;
    private long blockSpeed;
    private List<Material> ignoredMaterials;
    private String giveMessage;
    private String recieveMessage;
    private String noPermissionCommandMessage;
    private String noPermissionPlaceMessage;
    private String noFactionMessage;
    private String onlyClaimMessage;
    private String placeNormalMessage;
    private String placeInfiniteMessage;
    private String notEnoughMoneyPlaceMessage;
    private String notEnoughMoneyBuyMessage;
    private String wrongDirectionMessage;
    private String noSpaceBuyMessage;
    private String cannotPlaceYLevelMessage;
    private String buyConfirmationMessage;
    private String nearbyPlayerMessage;
    private ItemStack exitItemStack;
    private ItemStack fillItemStack;
    private String guiTitle;
    private String exitName;
    private String fillName;
    private List<String> exitLore;
    private List<String> fillLore;
    private List<Integer> exitSlots;
    private Set<String> recipeBuckets;
    private ItemStack gravityBlock;

    public ConfigValues(GenBucket main) {
        this.main = main;
    }

    public void loadBuckets() {
        main.getBucketManager().getBuckets().clear();
        if (main.getConfig().contains("items")) {
            for (String bucketId : main.getConfig().getConfigurationSection("items").getKeys(false)) {
                Bucket bucket = main.getBucketManager().createBucket(bucketId);
                ItemStack item = main.getUtils().itemFromString(main.getConfig().getString("items." + bucketId + ".item.material").toUpperCase());
                ItemMeta itemMeta = item.getItemMeta();
                itemMeta.setDisplayName(Utils.color(main.getConfig().getString("items." + bucketId + ".item.name")));
                itemMeta.setLore(Utils.colorLore(main.getConfig().getStringList("items." + bucketId + ".item.lore")));
                item.setItemMeta(itemMeta);
                if (main.getConfig().getBoolean("items." + bucketId + ".item.glow")) {
                    main.getUtils().addGlow(item);
                }
                bucket.setItem(item);
                bucket.setBlockItem(main.getUtils().itemFromString(main.getConfig().getString("items." + bucketId + ".block.material")));
                try {
                    bucket.setDirection(Bucket.Direction.valueOf(main.getConfig().getString("items." + bucketId + ".block.direction").toUpperCase()));
                } catch (IllegalArgumentException ex) {
                    main.getBucketManager().getBuckets().remove(bucketId);
                    continue;
                }
                bucket.setByChunk(main.getConfig().getBoolean("items." + bucketId + ".block.count-by-chunk"));
                bucket.setPatch(main.getConfig().getBoolean("items." + bucketId + ".block.patch"));
                ItemStack guiItem = main.getUtils().itemFromString(main.getConfig().getString("items." + bucketId + ".gui.material").toUpperCase());
                itemMeta = guiItem.getItemMeta();
                itemMeta.setDisplayName(Utils.color(main.getConfig().getString("items." + bucketId + ".gui.name")));
                itemMeta.setLore(Utils.colorLore(main.getConfig().getStringList("items." + bucketId + ".gui.lore")));
                guiItem.setItemMeta(itemMeta);
                if (main.getConfig().getBoolean("items." + bucketId + ".gui.glow")) {
                    main.getUtils().addGlow(guiItem);
                }
                bucket.setGuiItem(guiItem);
                bucket.setSlot(main.getConfig().getInt("items." + bucketId + ".gui.slot"));
                bucket.setBuyPrice(main.getConfig().getDouble("items." + bucketId + ".buy-price"));
                bucket.setPlacePrice(main.getConfig().getDouble("items." + bucketId + ".place-price"));
                bucket.setInfinite(main.getConfig().getBoolean("items." + bucketId + ".infinite"));

                bucket.setIngredients(null);
                Map<Character, ItemStack> ingredients = new HashMap<>();
                if (main.getConfig().contains("recipes." + bucketId + ".symbols")) {
                    for (String ingredientSymbol : main.getConfig().getConfigurationSection("recipes." + bucketId + ".symbols").getKeys(false)) {
                        ItemStack ingredientItem = main.getUtils().itemFromString(main.getConfig().getString("recipes." + bucketId + ".symbols." + ingredientSymbol));
                        ingredients.put(ingredientSymbol.toCharArray()[0], ingredientItem);
                    }
                    if (ingredients.size() > 0) {
                        bucket.setIngredients(ingredients);
                    }
                }
                bucket.setRecipeShape(null);
                if (main.getConfig().contains("recipes." + bucketId + ".recipe")) {
                    List<String> shapeList = main.getConfig().getStringList("recipes." + bucketId + ".recipe");
                    if (shapeList.size() == 3) {
                        bucket.setRecipeShape(shapeList);
                    }
                }
                if (main.getConfig().contains("recipes." + bucketId + ".outcome-amount")) {
                    bucket.setRecipeAmount(main.getConfig().getInt("recipes." + bucketId + ".outcome-amount"));
                } else {
                    bucket.setRecipeAmount(0);
                }
                exitSlots = main.getConfig().getIntegerList("gui.exit.slots");
            }
        }

        // other config values
        double bps = main.getConfig().getDouble("speed");
        if (bps<1) bps = 1;
        else if (bps>20) bps = 20;
        blockSpeed = Math.round(1 / bps * 20);

        ignoredMaterials = new ArrayList<>();
        for (String rawMaterial : main.getConfig().getStringList("ignored-blocks")) {
            try {
                ignoredMaterials.add(Material.valueOf(rawMaterial));
            } catch (Exception ignored) {}
        }

        giveMessage = Utils.color(main.getConfig().getString("messages.give"));
        recieveMessage = Utils.color(main.getConfig().getString("messages.receive"));
        noPermissionCommandMessage = Utils.color(main.getConfig().getString("messages.no-permission-command"));
        noPermissionPlaceMessage = Utils.color(main.getConfig().getString("messages.no-permission-place"));
        noFactionMessage = Utils.color(main.getConfig().getString("messages.no-faction"));
        onlyClaimMessage = Utils.color(main.getConfig().getString("messages.cannot-place-claim"));
        placeNormalMessage = Utils.color(main.getConfig().getString("messages.place-message-regular"));
        placeInfiniteMessage = Utils.color(main.getConfig().getString("messages.place-message-infinite"));
        notEnoughMoneyPlaceMessage = Utils.color(main.getConfig().getString("messages.not-enough-money-place"));
        notEnoughMoneyBuyMessage = Utils.color(main.getConfig().getString("messages.not-enough-money-shop"));
        wrongDirectionMessage = Utils.color(main.getConfig().getString("messages.wrong-direction"));
        noSpaceBuyMessage = Utils.color(main.getConfig().getString("messages.not-enough-space-buy"));
        cannotPlaceYLevelMessage = Utils.color(main.getConfig().getString("messages.cannot-place-y-level"));
        buyConfirmationMessage = Utils.color(main.getConfig().getString("messages.buy-success"));
        nearbyPlayerMessage = Utils.color(main.getConfig().getString("messages.cannot-place-nearby-players"));
        exitItemStack = main.getUtils().itemFromString(main.getConfig().getString("gui.exit.item"));
        fillItemStack = main.getUtils().itemFromString(main.getConfig().getString("gui.fill.item"));
        guiTitle = Utils.color(main.getConfig().getString("gui.title"));
        exitName = Utils.color(main.getConfig().getString("gui.exit.name"));
        fillName = Utils.color(main.getConfig().getString("gui.fill.name"));
        exitLore = Utils.colorLore(main.getConfig().getStringList("gui.exit.lore"));
        fillLore = Utils.colorLore(main.getConfig().getStringList("gui.fill.lore"));
        if (main.getConfig().getConfigurationSection("recipes") != null) {
            recipeBuckets = new HashSet<>();
            for(String key : main.getConfig().getConfigurationSection("recipes").getKeys(false)) {
                if (main.getBucketManager().getBuckets().containsKey(key)) recipeBuckets.add(key);
            }
        } else {
            recipeBuckets = null;
        }
        gravityBlock = main.getUtils().itemFromString(main.getConfig().getString("gravity-block"));
    }

    public Long getBlockSpeedDelay() {
        return blockSpeed;
    }

    public List<Material> getIgnoredBlockList() {
        return ignoredMaterials;
    }

    public boolean giveShouldDropItem() {
        return main.getConfig().getBoolean("give-drop-item-if-full");
    }

    public boolean shopShouldDropItem() {
        return main.getConfig().getBoolean("shop-drop-item-if-full");
    }

    public String getGiveMessage(Player p, int amount, Bucket bucket) {
        return giveMessage.replace("{player}", p.getName()).replace("{amount}", String.valueOf(amount)).replace("{bucket}", bucket.getId());
    }

    public String getReceiveMessage(int amount, double price, Bucket bucket) {
        return recieveMessage.replace("{amount}", String.valueOf(amount)).replace("{price}", String.valueOf(price)).replace("{amount}", String.valueOf(amount)).replace("{bucket}", bucket.getId());
    }

    public String getNoPermissionCommandMessage() {
        return noPermissionCommandMessage;
    }

    public String getNoPermissionPlaceMessage() {
        return noPermissionPlaceMessage;
    }

    public String getNoFactionMessage() {
        return noFactionMessage;
    }

    public String getOnlyClaimMessage() {
        return onlyClaimMessage;
    }

    public String getPlaceNormalMessage(double money) {
        return placeNormalMessage.replace("{money}", String.valueOf(money));
    }

    public String getPlaceInfiniteMessage(double money) {
        return placeInfiniteMessage.replace("{money}", String.valueOf(money));
    }

    public String notEnoughMoneyPlaceMessage(double money) {
        return notEnoughMoneyPlaceMessage.replace("{cost}", String.valueOf(money));
    }

    public String notEnoughMoneyBuyMessage(double money) {
        return notEnoughMoneyBuyMessage.replace("{cost}", String.valueOf(money));
    }

    public String getWrongDirectionMessage() {
        return wrongDirectionMessage;
    }

    public String getNoSpaceBuyMessage() {
        return noSpaceBuyMessage;
    }

    public String getCannotPlaceYLevelMessage() {
        return cannotPlaceYLevelMessage;
    }

    public String getBuyConfirmationMessage(int amount) {
        return buyConfirmationMessage.replace("{amount}", String.valueOf(amount));
    }

    public String getNearbyPlayerMessage() {
        return nearbyPlayerMessage;
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

    public boolean addBlockUnderGravity() {
        return main.getConfig().getBoolean("add-block-under-gravity-blocks");
    }

    public ItemStack getGravityBlock() {
        return gravityBlock;
    }

    public ItemStack getExitItemStack() {
        return exitItemStack;
    }

    public ItemStack getFillItemStack() {
        return fillItemStack;
    }

    public String getGUITitle() {
        return guiTitle;
    }

    public String getExitName() {
        return exitName;
    }

    public String getFillName() {
        return fillName;
    }

    public List<String> getExitLore() {
        return exitLore;
    }

    public List<String> getFillLore() {
        return fillLore;
    }

    public List<Integer> getExitSlots() {
        return exitSlots;
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
        return recipeBuckets;
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
