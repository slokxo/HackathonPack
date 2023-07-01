package me.slokxoxo.hackathon.command;

import com.projectkorra.projectkorra.command.PKCommand;
import me.slokxoxo.hackathon.HackathonPack;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class HackathonCommand extends PKCommand {

    public HackathonCommand(String name, String properUse, String description, String[] aliases) {
        super(name, properUse, description, aliases);
    }

    @Override
    public void execute(CommandSender commandSender, List<String> list) {
        if (commandSender instanceof Player && list.size() == 0) {
            sendInfo(commandSender);
        } else if (list.size() == 1) {
            if (list.get(0).equalsIgnoreCase("reload") && hasPermission(commandSender, "reload")) {
                HackathonPack.hp.reloadConfig();
                commandSender.sendMessage(ChatColor.RED + "" + ChatColor.UNDERLINE + "HackathonPack config successfully reloaded.");
            } else if (list.get(0).equalsIgnoreCase("config") && hasPermission(commandSender, "config")) {
                listConfig(commandSender);
            }
        } else {
            commandSender.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Invalid command. Valid commands: " +
                    ChatColor.RED + "" + ChatColor.UNDERLINE + "\n[/b hackathonpack]: " + ChatColor.GRAY + "Displays general information about the addon pack." +
                    ChatColor.RED + "" + ChatColor.UNDERLINE + "\n[/b hackathonpack reload]: " + ChatColor.GRAY + "Reloads the config." +
                    ChatColor.RED + "" + ChatColor.UNDERLINE + "\n[/b hackathonpack config]: " + ChatColor.GRAY + "Lists the config.");
        }
    }

    private void listConfig(CommandSender commandSender) {
        FileConfiguration config = HackathonPack.hp.getConfig();
        commandSender.sendMessage(ChatColor.RED + "" + ChatColor.UNDERLINE + "HackathonPack Config:");
        listConfigSection(commandSender, config, "");
    }

    private void listConfigSection(CommandSender commandSender, ConfigurationSection section, String path) {
        for (String key : section.getKeys(false)) {
            String fullPath = (path.isEmpty() ? key : path + "." + key);
            if (section.isConfigurationSection(key)) {
                ConfigurationSection subsection = section.getConfigurationSection(key);
                assert subsection != null;
                listConfigSection(commandSender, subsection, fullPath);
            } else {
                Object value = section.get(key);
                commandSender.sendMessage(ChatColor.GRAY + fullPath + ": " + ChatColor.YELLOW + value);
            }
        }
    }
    private void sendInfo(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Version:" + ChatColor.ITALIC + HackathonPack.hp.version());
        sender.sendMessage(ChatColor.RED + "Author:"  + ChatColor.YELLOW + " " + HackathonPack.hp.dev() + " / @slokx on " + ChatColor.LIGHT_PURPLE + "Discord.");
        sender.sendMessage(ChatColor.RED + "Source Code:" + ChatColor.YELLOW + ChatColor.UNDERLINE + " https://github.com/slokxo/HackathonPack");
    }
}