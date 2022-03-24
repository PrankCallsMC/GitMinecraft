package com.desolatelands.gitminecraft.git;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GitCommand implements CommandExecutor {
    private List<SubCommand> commands = new ArrayList<>();
    private GitManager manager;

    public GitCommand(GitManager manager) {
        commands.add(new GitInit());
        commands.add(new GitStatus());
        commands.add(new GitPull());
        commands.add(new GitPush());
        commands.add(new GitRegister());
        commands.add(new GitCheckout());
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission("git.admin")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to do this!");
                return true;
            }
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.BLUE + "== GitMinecraft Commands ==");
            for (SubCommand cmd : commands) {
                sender.sendMessage(ChatColor.AQUA + cmd.usage() + ": "
                        + ChatColor.WHITE + cmd.description());
            }
            return true;
        }

        String errorMsg = ChatColor.RED + "Invalid command!";

        for (SubCommand subCmd : commands) {
            String[] subCmdArgs = subCmd.args();
            boolean matches = true;

            for (int i = 0; i < subCmdArgs.length; i++) {
                if (i >= args.length || !subCmdArgs[i].equals(args[i])) {
                    matches = false;
                    break;
                }
            }
            if (matches) {
                if (args.length < subCmd.minArgs() || args.length > subCmd.maxArgs()) {
                    errorMsg = ChatColor.RED + "Usage: " + subCmd.usage();
                } else {
                    subCmd.run(sender, args);
                    return true;
                }
            }
        }

        sender.sendMessage(errorMsg);
        return true;
    }

    private interface SubCommand {
        int minArgs();
        int maxArgs();
        String[] args();
        String usage();
        String description();
        void run(CommandSender sender, String[] args);
    }

    private class GitPull implements SubCommand {
        public int minArgs() { return 1; }
        public int maxArgs() { return 2; }
        public String[] args() { return new String[]{"pull"}; }

        public String usage() {
            return "/git pull {branch name}";
        }

        public String description() {
            return "Pulls a branch from Git to the current repository.";
        }

        public void run(CommandSender sender, String[] args) {
            sender.sendMessage(ChatColor.GREEN + "Called /git pull");
        }

    }

    private class GitCheckout implements SubCommand {
        public int minArgs() { return 2; }
        public int maxArgs() { return 2; }
        public String[] args() { return new String[]{"checkout"}; }

        public String usage() {
            return "/git checkout (branch)";
        }

        public String description() {
            return "Enters the provided branch.";
        }

        public void run(CommandSender sender, String[] args) {
            sender.sendMessage(ChatColor.GREEN + "Called /git checkout");
        }

    }

    private class GitPush implements SubCommand {
        public int minArgs() { return 1; }
        public int maxArgs() { return 2; }
        public String[] args() { return new String[]{"push"}; }

        public String usage() {
            return "/git push {commit message}";
        }

        public String description() {
            return "Pushes the current branch to the remote repository.";
        }

        public void run(CommandSender sender, String[] args) {
            if (manager.push(args.length > 1 ? args[1] : "")) {
                sender.sendMessage(ChatColor.AQUA + "Successfully pushed to remote repository.");
            } else {
                sender.sendMessage(ChatColor.RED + "Error with Git Push. Check console.");
            }
        }

    }

    private class GitInit implements SubCommand {
        public int minArgs() { return 1; }
        public int maxArgs() { return 1; }
        public String[] args() { return new String[]{"init"}; }

        public String usage() {
            return "/git init";
        }

        public String description() {
            return "Initializes a Git repository in the plugins folder.";
        }

        public void run(CommandSender sender, String[] args) {
            if (manager.gitInit()) {
                sender.sendMessage(ChatColor.AQUA + "Initialized Git ignore file. " +
                        "NOTE: you must provide remote-url in the config, upload .git, and fill the ssh-key.yml file.");
            } else {
                sender.sendMessage(ChatColor.RED + "You have already initialized the repository.");
            }
        }

    }

    private class GitStatus implements SubCommand {
        public int minArgs() { return 1; }
        public int maxArgs() { return 1; }
        public String[] args() { return new String[]{"status"}; }

        public String usage() {
            return "/git status";
        }

        public String description() {
            return "Displays some status information about the repository.";
        }

        public void run(CommandSender sender, String[] args) {
            manager.status(sender);
        }

    }

    private class GitRegister implements SubCommand {
        public int minArgs() { return 2; }
        public int maxArgs() { return 2; }
        public String[] args() { return new String[]{"register"}; }

        public String usage() {
            return "/git register (path from plugins folder)";
        }

        public String description() {
            return "Registers a file path to be used in the Git repository.";
        }

        public void run(CommandSender sender, String[] args) {
            if (manager.register(args[1])) {
                sender.sendMessage(ChatColor.AQUA + "Successfully registered a file for Git.");
            } else {
                sender.sendMessage(ChatColor.RED + "Error with registering Git file.");
            }
        }

    }

}
