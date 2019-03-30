package codes.biscuit.genbucket.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Bucket {

    private String id;
    private ItemStack item;
    private Material blockMaterial;
    private Direction direction;
    private boolean byChunk;
    private boolean isPatch;
//    private int width;
    private ItemStack guiItem;
    private int slot;
    private double buyPrice;
    private boolean infinite;
    private double placePrice;

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

    public Material getBlockMaterial() {
        return blockMaterial;
    }

    void setBlockMaterial(Material blockMaterial) {
        this.blockMaterial = blockMaterial;
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

    @SuppressWarnings("unused")
    public enum Direction {
        ANY,
        HORIZONTAL,
        UPWARDS,
        DOWNWARDS
    }
}
