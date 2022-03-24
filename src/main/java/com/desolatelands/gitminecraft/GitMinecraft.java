package com.desolatelands.gitminecraft;

import com.desolatelands.gitminecraft.git.GitCommand;
import com.desolatelands.gitminecraft.git.GitManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class GitMinecraft extends JavaPlugin {

    private GitManager gitManager;

    @Override
    public void onEnable() {
        initializeData();
        gitManager = new GitManager(this, FileUtil.loadConfigFile(this, "registered-files"));
        registerCommands();
    }

    @Override
    public void onDisable() {

    }

    // Register commands
    public void registerCommands() {
        getCommand("git").setExecutor(new GitCommand(gitManager));
    }

    public void initializeData() {
        File dataFile = getDataFolder();

        if (!dataFile.exists()) {
            dataFile.mkdirs();
            info("[GitMinecraft] Created plugin folder.");
        }

        File configFile = new File(getDataFolder() + File.separator + "config.yml");

        if (!configFile.exists()) {
            FileConfiguration config = new YamlConfiguration();
            config.set("user", "");
            config.set("email", "");
            config.set("remote-url", "");

            if (FileUtil.saveFile(configFile, config)) {
                info("[GitMinecraft] Created empty config file.");
            }
        }

        File keyFile = new File(getDataFolder() + File.separator + "ssh-key.yml");

        if (!keyFile.exists()) {
            FileConfiguration config = new YamlConfiguration();

            if (FileUtil.saveFile(keyFile, config)) {
                info("[GitMinecraft] Created empty ssh-key file.");
            }
        }
    }

    public void info(String msg) {
        getServer().getLogger().info(msg);
    }

}
