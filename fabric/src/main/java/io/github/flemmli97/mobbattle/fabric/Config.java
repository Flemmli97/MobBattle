package io.github.flemmli97.mobbattle.fabric;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Config {

    public static Config config;

    public static void initConfig() {
        config = new Config();
        reload();
    }

    public static void reload() {
        config.load();
    }

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private File configFile;

    public boolean showTeamParticleTypes;
    public boolean autoAddAI;

    private Config() {
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
            this.showTeamParticleTypes = obj.has("showTeamParticleTypes") && obj.get("showTeamParticleTypes").getAsBoolean();
            this.autoAddAI = obj.has("autoAddAI") && obj.get("autoAddAI").getAsBoolean();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void save() {
        JsonObject obj = new JsonObject();
        obj.addProperty("__showTeamParticleTypes", "If the team has a color will play particles of that color above the mob");
        obj.addProperty("showTeamParticleTypes", this.showTeamParticleTypes);
        obj.addProperty("__autoAddAI", "Auto target mobs from other teams (if e.g. done per command)");
        obj.addProperty("autoAddAI", this.autoAddAI);
        try {
            FileWriter writer = new FileWriter(this.configFile);
            GSON.toJson(obj, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*static final ForgeConfigSpec clientSpec;
    public static final ClientConfig clientConf;

    static final ForgeConfigSpec commonSpec;
    public static final ServerConfig commonConf;

    public static class ClientConfig {

        public final BooleanValue showTeamParticleTypes;

        public ClientConfig(ForgeConfigSpec.Builder builder) {
            this.showTeamParticleTypes = builder.translation("conf.mobbattle.particle").define("showTeamParticle", true);
        }
    }

    public static class ServerConfig {

        public final BooleanValue autoAddAI;

        public ServerConfig(ForgeConfigSpec.Builder builder) {
            this.autoAddAI = builder.comment("Auto target mobs from other teams (if e.g. done per command)").translation("conf.mobbattle.addai")
                    .define("autoAddAI", true);
            //builder.pop();
        }
    }

    static {
        Pair<ClientConfig, ForgeConfigSpec> specPair1 = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        clientSpec = specPair1.getRight();
        clientConf = specPair1.getLeft();

        Pair<ServerConfig, ForgeConfigSpec> specPair2 = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        commonSpec = specPair2.getRight();
        commonConf = specPair2.getLeft();
    }*/
}
