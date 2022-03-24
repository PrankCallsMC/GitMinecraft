package com.desolatelands.gitminecraft;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileUtil {
    public static FileConfiguration loadConfigFile(GitMinecraft plugin, String fileName) {
        FileConfiguration file = new YamlConfiguration();

        try {
            File data = new File(plugin.getDataFolder(), fileName + ".yml");

            if (data.exists()) {
                file.load(data);
            }

        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        } catch (InvalidConfigurationException exception) {
            exception.printStackTrace();
            return null;
        }

        return file;
    }

    public static boolean saveFile(File file, FileConfiguration config) {
        try {
            config.save(file);
        } catch (IOException exception) {
            exception.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean isFile(String filePath) {
        return (new File(filePath)).exists();
    }

}
