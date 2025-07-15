package io.github.flemmli97.mobbattle.neoforge;

import io.github.flemmli97.mobbattle.common.Config;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ConfigLoader {

    static final IConfigSpec CLIENT_SPEC;
    public static final ClientConfig CLIENT_CONF;

    static final IConfigSpec COMMON_SPEC;
    public static final CommonConfig COMMON_CONF;

    public static class ClientConfig {

        public final ModConfigSpec.BooleanValue showTeamParticleTypes;

        public ClientConfig(ModConfigSpec.Builder builder) {
            this.showTeamParticleTypes = builder.comment("If the team has a color will play particles of that color above the mob").translation("conf.mobbattle.particle")
                    .define("showTeamParticle", true);
        }

        public void reload() {
            Config.showTeamParticleTypes = this.showTeamParticleTypes.get();
        }
    }

    public static class CommonConfig {

        public final ModConfigSpec.BooleanValue autoAddAI;

        public CommonConfig(ModConfigSpec.Builder builder) {
            this.autoAddAI = builder.comment("Auto target mobs from other teams (if e.g. done per command)").translation("conf.mobbattle.addai")
                    .define("autoAddAI", true);
        }

        public void reload() {
            Config.autoAddAI = this.autoAddAI.get();
        }
    }

    static {
        Pair<ClientConfig, ModConfigSpec> specPair1 = new ModConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT_SPEC = specPair1.getRight();
        CLIENT_CONF = specPair1.getLeft();

        Pair<CommonConfig, ModConfigSpec> specPair2 = new ModConfigSpec.Builder().configure(CommonConfig::new);
        COMMON_SPEC = specPair2.getRight();
        COMMON_CONF = specPair2.getLeft();
    }
}
