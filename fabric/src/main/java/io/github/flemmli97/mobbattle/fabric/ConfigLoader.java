package io.github.flemmli97.mobbattle.fabric;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.common.Config;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.GsonHelper;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigLoader {

    public static ConfigLoader configLoader;

    public static void initConfig() {
        configLoader = new ConfigLoader();
        reload();
    }

    public static void reload() {
        configLoader.load();
    }

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private File configFile;

    private ConfigLoader() {
        File configDir = FabricLoader.getInstance().getConfigDir().resolve("mobbattle").toFile();
        try {
            if (!configDir.exists())
                configDir.mkdirs();
            this.configFile = new File(configDir, "mobbattle_config.json");
            if (!this.configFile.exists()) {
                this.configFile.createNewFile();
                this.save();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        try {
            FileReader reader = new FileReader(this.configFile);
            JsonObject obj = GSON.fromJson(reader, JsonObject.class);
            reader.close();
            Config.showTeamParticleTypes = GsonHelper.getAsBoolean(obj, "showTeamParticleTypes", Config.showTeamParticleTypes);
            Config.autoAddAI = GsonHelper.getAsBoolean(obj, "autoAddAI", Config.autoAddAI);
            Config.followRangeIncrease = GsonHelper.getAsDouble(obj, "followRangeIncrease", Config.followRangeIncrease);
        } catch (IOException e) {
            MobBattle.LOGGER.error(e);
        }
    }

    private void save() {
        JsonObject obj = new JsonObject();
        obj.addProperty("__showTeamParticleTypes", "If the team has a color will play particles of that color above the mob");
        obj.addProperty("showTeamParticleTypes", Config.showTeamParticleTypes);
        obj.addProperty("__autoAddAI", "Auto target mobs from other teams (if e.g. done per command)");
        obj.addProperty("autoAddAI", Config.autoAddAI);
        obj.addProperty("__followRangeIncrease", "Follow range increase for battling mobs");
        obj.addProperty("followRangeIncrease", Config.followRangeIncrease);
        try {
            FileWriter writer = new FileWriter(this.configFile);
            GSON.toJson(obj, writer);
            writer.close();
        } catch (IOException e) {
            MobBattle.LOGGER.error(e);
        }
    }
}
