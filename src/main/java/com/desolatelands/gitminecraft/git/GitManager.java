package com.desolatelands.gitminecraft.git;

import com.desolatelands.gitminecraft.FileUtil;
import com.desolatelands.gitminecraft.GitMinecraft;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.sshd.SshdSessionFactory;
import org.eclipse.jgit.transport.sshd.SshdSessionFactoryBuilder;
import org.eclipse.jgit.util.FS;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GitManager {
    private GitMinecraft plugin;
    private List<String> register;

    public GitManager(GitMinecraft plugin, FileConfiguration config) {
        this.plugin = plugin;
        if (config != null) {
            this.register = config.getStringList("registered-files");
        }
    }

    public boolean gitInit() {
        if (FileUtil.isFile(getGitFolder())) {
            return false;
        }
        saveRegisterFile(new ArrayList<>());
        return true;
    }

    public void status(CommandSender sender) {
        Repository repo = loadRepo();

        if (repo == null) {
            sender.sendMessage(ChatColor.RED +
                    "Error: Could not load Git Repository. Did you follow the initialization steps?");
            return;
        }
        try {
            sender.sendMessage(ChatColor.BLUE + "== Git Status ==");
            sender.sendMessage(ChatColor.AQUA + "Branch: " + repo.getBranch());
            sender.sendMessage(ChatColor.AQUA + "Remote Name: " + repo.getRemoteNames());

            Config cfg = repo.getConfig();
            String name = cfg.getString("user", null, "name");
            String email = cfg.getString("user", null, "email");

            sender.sendMessage(ChatColor.AQUA + "User: " + name);
            sender.sendMessage(ChatColor.AQUA + "Email: " + email);
        } catch (IOException e) {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED +
                    "Error with reading git repository. Please check console.");
        }
    }

    public boolean register(String filePath) {
        if (register == null) {
            return false;
        }
        register.add(filePath);
        return saveRegisterFile(register);
    }

    public boolean push(String msg) {
        Repository repo = loadRepo();
        Git git = new Git(repo);
        File sshDir = new File("plugins/GitMinecraft");
        SshdSessionFactory sshSessionFactory = new SshdSessionFactoryBuilder()
                .setPreferredAuthentications("publickey")
                .setHomeDirectory(new File("plugins"))
                .setSshDirectory(sshDir)
                .build(null);
        try {
            for (String registeredFile : register) {
                git.add().addFilepattern("plugins/" + registeredFile).call();
            }
            SshdSessionFactory.setInstance(sshSessionFactory);
            git.commit().setMessage(msg).call();
            git.push()
                    .setRemote("origin")
                    .call();
        } catch (GitAPIException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    private Repository loadRepo() {
        Repository repo = null;
        try {
            repo = new FileRepositoryBuilder()
                    .setGitDir(new File(getGitFolder()))
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return repo;
    }
    private String getGitFolder() {
        return plugin.getDataFolder().toString().replace("GitMinecraft", ".git");
    }
    private boolean saveRegisterFile(List<String> list) {
        FileConfiguration registerConfig = new YamlConfiguration();
        registerConfig.set("registered-files", list);
        return FileUtil.saveFile(new File(plugin.getDataFolder(), "registered-files.yml"), registerConfig);
    }


}
