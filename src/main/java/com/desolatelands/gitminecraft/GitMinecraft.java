package com.desolatelands.gitminecraft;

import com.desolatelands.gitminecraft.git.GitCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class GitMinecraft extends JavaPlugin {

    @Override
    public void onEnable() {
        registerCommands();
    }

    @Override
    public void onDisable() {

    }

    // Register commands
    public void registerCommands() {
        getCommand("git").setExecutor(new GitCommand());
    }

}
