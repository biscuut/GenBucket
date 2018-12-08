package codes.biscuit.simplegenbuckets;

import codes.biscuit.simplegenbuckets.commands.GenBucketAdminCommand;
import codes.biscuit.simplegenbuckets.commands.GenBucketCommand;
import codes.biscuit.simplegenbuckets.events.PlayerEvents;
import codes.biscuit.simplegenbuckets.hooks.HookUtils;
import codes.biscuit.simplegenbuckets.utils.ConfigValues;
import codes.biscuit.simplegenbuckets.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SimpleGenBuckets  extends JavaPlugin {

    private ConfigValues configValues =  new ConfigValues(this);
    private Utils utils =  new Utils(this);
    private HookUtils hookUtils;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new PlayerEvents(this), this);
        getCommand("genbucket").setExecutor(new GenBucketCommand(this));
        GenBucketAdminCommand gbaCommand = new GenBucketAdminCommand(this);
        getCommand("genbucketadmin").setExecutor(gbaCommand);
        getCommand("genbucketadmin").setTabCompleter(gbaCommand.TAB_COMPLETER);
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        reloadConfig();
        hookUtils = new HookUtils(this);
        utils.registerRecipes();
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
