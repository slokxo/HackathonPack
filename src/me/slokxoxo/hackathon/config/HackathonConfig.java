package me.slokxoxo.hackathon.config;

import com.projectkorra.projectkorra.configuration.ConfigManager;
import me.slokxoxo.hackathon.HackathonPack;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;

public class HackathonConfig {

    public HackathonConfig() {
        setupMainConfig();
        setupDeathMessages();
        HackathonPack.hp.getConfig().setComments("Abilities.Ice.IceGrip.Controllable", Collections.singletonList("This may break the ability. USE AT CAUTION"));
    }

    private void setupDeathMessages(){

        ConfigManager.languageConfig.get().addDefault("Abilities.Ice.IceGrip.DeathMessage", "{victim} was frozen away by {attacker}'s {ability}");
        ConfigManager.languageConfig.get().addDefault("Abilities.Ice.IceDiscs.DeathMessage", "{victim} was pelted by {attacker}'s {ability}");
        ConfigManager.languageConfig.get().addDefault("Abilities.Techno.EMPGrenade.DeathMessage", "{victim} had too much of a migraine by {attacker}'s {ability}");
        ConfigManager.languageConfig.get().addDefault("Abilities.Sand.SandSlash.DeathMessage", "{attacker}'s {ability} proved to bee to much for {victim}, as was shown by a slash of sand impaling their large and small intestines");
        ConfigManager.languageConfig.get().addDefault("Abilities.Sand.SandSpout.DeathMessage", "{attacker}'s {ability} was too fast and suffocate-able for {victim} to handle");

        ConfigManager.languageConfig.save();
    }


    private void setupMainConfig() {
        FileConfiguration c = HackathonPack.hp.getConfig();

        // IceGrip
        c.addDefault("Abilities.Ice.IceGrip.Enabled", true);
        c.addDefault("Abilities.Ice.IceGrip.Cooldown", 7000);
        c.addDefault("Abilities.Ice.IceGrip.SourceRange", 45);
        c.addDefault("Abilities.Ice.IceGrip.Speed", .8);
        c.addDefault("Abilities.Ice.IceGrip.GripDuration", 5);
        c.addDefault("Abilities.Ice.IceGrip.Range", 15);
        c.addDefault("Abilities.Ice.IceGrip.Hitbox", 1);
        c.addDefault("Abilities.Ice.IceGrip.MovementHandler", true);
        c.addDefault("Abilities.Ice.IceGrip.DisperseRadius", 3.5);
        c.addDefault("Abilities.Ice.IceGrip.Controllable", false);
        // SandSlash
        c.addDefault("Abilities.Sand.SandSlash.Enabled", true);
        c.addDefault("Abilities.Sand.SandSlash.Damage", 3);
        c.addDefault("Abilities.Sand.SandSlash.Speed", 2);
        c.addDefault("Abilities.Sand.SandSlash.Cooldown", 5000);
        c.addDefault("Abilities.Sand.SandSlash.HitRadius", 1);
        c.addDefault("Abilities.Sand.SandSlash.SourceRange", 8);
        c.addDefault("Abilities.Sand.SandSlash.Range", 20);
        c.addDefault("Abilities.Sand.SandSlash.Duration", 30000);
        c.addDefault("Abilities.Sand.SandSlash.MaxAngle", 50);
        // EMPGrenade
        c.addDefault("Abilities.Techno.EMPGrenade.Enabled", true);
        c.addDefault("Abilities.Techno.EMPGrenade.Cooldown", 4000);
        c.addDefault("Abilities.Techno.EMPGrenade.Radius", 6);
        // GrapplingHook
        c.addDefault("Abilities.Techno.GrapplingHook.Enabled", true);
        c.addDefault("Abilities.Techno.GrapplingHook.ShowBlocksInRange", true);
        c.addDefault("Abilities.Techno.GrapplingHook.Cooldown", 4000);
        c.addDefault("Abilities.Techno.GrapplingHook.Duration", 4000);
        c.addDefault("Abilities.Techno.GrapplingHook.Range", 20);
        c.addDefault("Abilities.Techno.GrapplingHook.ShiftSpeed", 0.6);
        // IceDisc
        c.addDefault("Abilities.Ice.IceDiscs.Enabled", true);
        c.addDefault("Abilities.Ice.IceDiscs.Cooldown", 8000);
        c.addDefault("Abilities.Ice.IceDiscs.Duration", 10000);
        c.addDefault("Abilities.Ice.IceDiscs.Range", 25);
        c.addDefault("Abilities.Ice.IceDiscs.Speed", 1);
        c.addDefault("Abilities.Ice.IceDiscs.Damage", 2);
        c.addDefault("Abilities.Ice.IceDiscs.SelectRange", 4);
        c.addDefault("Abilities.Ice.IceDiscs.MaxUses", 3);
        c.addDefault("Abilities.Ice.IceDiscs.HitRadius", 1);
        c.addDefault("Abilities.Ice.IceDiscs.IceDiscIntervals", 1000);
        // MetalRun
        c.addDefault("Abilities.Metal.MetalRun.Enabled", true);
        c.addDefault("Abilities.Metal.MetalRun.Cooldown", 0);
        c.addDefault("Abilities.Metal.MetalRun.Duration", 7000);
        c.addDefault("Abilities.Metal.MetalRun.Speed", 1.2);
        c.addDefault("Abilities.Metal.MetalRun.DismountSpeed", 1.2);

        c.options().copyDefaults(true);
        HackathonPack.hp.saveConfig();
    }
}
