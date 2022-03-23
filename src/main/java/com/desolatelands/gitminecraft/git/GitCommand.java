package com.desolatelands.gitminecraft.git;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GitCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission("git.admin")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to do this!");
                return true;
            }
        }

        sender.sendMessage(ChatColor.RED + "Invalid command!");
        return true;
    }

}
