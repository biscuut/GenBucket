package codes.biscuit.genbucket;

import codes.biscuit.genbucket.commands.GenBucketAdminCommand;
import codes.biscuit.genbucket.commands.GenBucketCommand;
import codes.biscuit.genbucket.listeners.PlayerListener;
import codes.biscuit.genbucket.hooks.HookUtils;
import codes.biscuit.genbucket.utils.BucketManager;
import codes.biscuit.genbucket.utils.ConfigValues;
import codes.biscuit.genbucket.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class GenBucket extends JavaPlugin {

    private ConfigValues configValues;
    private Utils utils;
    private HookUtils hookUtils;
    private BucketManager bucketManager;

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
}
