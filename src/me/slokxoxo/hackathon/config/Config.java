package me.slokxoxo.hackathon.config;

import me.slokxoxo.hackathon.HackathonPack;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Config {
    private final Path path;
    private final FileConfiguration config;

    public Config(String name) {
        path = Paths.get(HackathonPack.hp.getDataFolder().toString(), name);
        config = YamlConfiguration.loadConfiguration(path.toFile());
        reloadConfig();
    }

    private void createConfig() {
        try {
            Files.createFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadConfig() {
        boolean isNewConfig = Files.notExists(path);
        if (isNewConfig) {
            createConfig();
        }

        FileConfiguration oldConfig = YamlConfiguration.loadConfiguration(path.toFile());
        try {
            config.load(path.toFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        try {
            config.options().copyDefaults(true);
            config.save(path.toFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }
}