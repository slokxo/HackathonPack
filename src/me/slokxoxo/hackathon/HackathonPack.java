package me.slokxoxo.hackathon;

import co.aikar.commands.PaperCommandManager;
import com.projectkorra.projectkorra.ability.CoreAbility;
import me.slokxoxo.hackathon.command.ActionBarCommand;
import me.slokxoxo.hackathon.command.HackathonCommand;
import me.slokxoxo.hackathon.config.HackathonConfig;
import me.slokxoxo.hackathon.listener.HPListener;
import me.slokxoxo.hackathon.listener.Listeners;
import me.slokxoxo.hackathon.listener.test;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class HackathonPack extends JavaPlugin {

    public static HackathonPack hp;

    @Override
    public void onEnable() {
        hp = this;
        getLogger().info("[HackathonPack] Enabling HackathonPack");
        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.enableUnstableAPI("help");
        commandManager.registerCommand(new ActionBarCommand());
        new HackathonCommand("hackathon", "/bending hackathon", "Present the HackathonPack statistics and version.", new String[] {"hackathonpack", "hp", "hackathon"});
        CoreAbility.registerPluginAbilities(this, "me.slokxoxo.hackathon.ability");
        getServer().getPluginManager().registerEvents(new HPListener(), this);
        getServer().getPluginManager().registerEvents(new Listeners(), this);
        getServer().getPluginManager().registerEvents(new test(), this);
        new HackathonConfig();
    }

    @Override
    public void onDisable() {
        getLogger().info("[HackathonPack] Disabling HackathonPack");
    }


    public String prefix() {
        return ChatColor.DARK_GRAY + "[" + ChatColor.BLUE + "HackathonPack" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY;
    }

    public String dev() {
        return "slokxoxo";
    }

    public String version() {
        return prefix() + " v. 1.0.0";
    }

}
