package codes.biscuit.genbucket;

import codes.biscuit.genbucket.commands.GenBucketAdminCommand;
import codes.biscuit.genbucket.commands.GenBucketCommand;
import codes.biscuit.genbucket.hooks.HookUtils;
import codes.biscuit.genbucket.hooks.MetricsLite;
import codes.biscuit.genbucket.listeners.PlayerListener;
import codes.biscuit.genbucket.utils.BucketManager;
import codes.biscuit.genbucket.utils.ConfigValues;
import codes.biscuit.genbucket.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.regex.Pattern;

public class GenBucket extends JavaPlugin {

    private ConfigValues configValues;
    private Utils utils;
    private HookUtils hookUtils;
    private BucketManager bucketManager;
    private int minecraftVersion = -1;

    @Override
    public void onEnable() {
        bucketManager = new BucketManager();
        utils = new Utils(this);
        configValues = new ConfigValues(this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        getCommand("genbucket").setExecutor(new GenBucketCommand(this));
        GenBucketAdminCommand gbaCommand = new GenBucketAdminCommand(this);
        getCommand("genbucketadmin").setExecutor(gbaCommand);
        getCommand("genbucketadmin").setTabCompleter(gbaCommand);
        saveDefaultConfig();
        hookUtils = new HookUtils(this);
        utils.registerRecipes();
        utils.updateConfig();
        configValues.loadBuckets();
        if (minecraftVersion == -1) {
            minecraftVersion = Integer.valueOf(Bukkit.getBukkitVersion().split(Pattern.quote("-"))[0].split(Pattern.quote("."))[1]);
        }
        new MetricsLite(this);
    }

    public ConfigValues getConfigValues() {
        return configValues;
    }

    public Utils getUtils() {
        return utils;
    }

    public HookUtils getHookUtils() {
        return hookUtils;
    }

    public BucketManager getBucketManager() {
        return bucketManager;
    }

    // using mc 1.8 to 1.12
    public boolean usingOldAPI() {
        return minecraftVersion < 13;
    }
}
