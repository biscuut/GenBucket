package codes.biscuit.genbucket.utils;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class Bucket {

    private String id;
    private ItemStack item;
    private ItemStack blockItem;
    private Direction direction;
    private boolean byChunk;
    private boolean isPatch;
    private boolean isDelete;
    private ItemStack guiItem;
    private int slot;
    private double buyPrice;
    private boolean infinite;
    private double placePrice;
    private Map<Character, ItemStack> ingredients;
    private List<String> recipeShape;
    private int recipeAmount;

    Bucket(String id) {
        this.id = id;
    }

    String getId() {
        return id;
    }

    public ItemStack getItem() {
        return item.clone();
    }

    void setItem(ItemStack item) {
        this.item = item;
    }

    public ItemStack getGuiItem() {
        return guiItem.clone();
    }

    void setGuiItem(ItemStack guiItem) {
        this.guiItem = guiItem;
    }

    public Direction getDirection() {
        return direction;
    }

    void setDirection(Direction direction) {
        this.direction = direction;
    }

    public boolean isByChunk() {
        return byChunk;
    }

    void setByChunk(boolean byChunk) {
        this.byChunk = byChunk;
    }

    public boolean isPatch() {
        return isPatch;
    }

    void setPatch(boolean patch) {
        isPatch = patch;
    }

    int getSlot() {
        return slot;
    }

    void setSlot(int slot) {
        this.slot = slot;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
    }

    public boolean isInfinite() {
        return infinite;
    }

    void setInfinite(boolean infinite) {
        this.infinite = infinite;
    }

    public double getPlacePrice() {
        return placePrice;
    }

    void setPlacePrice(double placePrice) {
        this.placePrice = placePrice;
    }

    public ItemStack getBlockItem() {
        return blockItem;
    }

    void setBlockItem(ItemStack blockItem) {
        this.blockItem = blockItem;
    }

    List<String> getRecipeShape() {
        return recipeShape;
    }

    void setRecipeShape(List<String> recipeShape) {
        this.recipeShape = recipeShape;
    }

    Map<Character, ItemStack> getIngredients() {
        return ingredients;
    }

    void setIngredients(Map<Character, ItemStack> ingredients) {
        this.ingredients = ingredients;
    }

    int getRecipeAmount() {
        return recipeAmount;
    }

    void setRecipeAmount(int recipeAmount) {
        this.recipeAmount = recipeAmount;
    }

    void setDelete(boolean delete) {
        isDelete = delete;
    }

    public boolean isDelete() {
        return isDelete;
    }

    @SuppressWarnings("unused")
    public enum Direction {
        ANY,
        HORIZONTAL,
        UPWARDS,
        DOWNWARDS
    }
}
