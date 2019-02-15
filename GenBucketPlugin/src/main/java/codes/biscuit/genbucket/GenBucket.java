package codes.biscuit.genbucket;

import codes.biscuit.genbucket.commands.GenBucketAdminCommand;
import codes.biscuit.genbucket.commands.GenBucketCommand;
import codes.biscuit.genbucket.listeners.PlayerListener;
import codes.biscuit.genbucket.hooks.HookUtils;
import codes.biscuit.genbucket.utils.ConfigValues;
import codes.biscuit.genbucket.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class GenBucket extends JavaPlugin {

    private ConfigValues configValues =  new ConfigValues(this);
    private Utils utils =  new Utils(this);
    private HookUtils hookUtils;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        getCommand("genbucket").setExecutor(new GenBucketCommand(this));
        GenBucketAdminCommand gbaCommand = new GenBucketAdminCommand(this);
        getCommand("genbucketadmin").setExecutor(gbaCommand);
        getCommand("genbucketadmin").setTabCompleter(gbaCommand);
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        reloadConfig();
        hookUtils = new HookUtils(this);
        utils.registerRecipes();
        utils.updateConfig();
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
}
