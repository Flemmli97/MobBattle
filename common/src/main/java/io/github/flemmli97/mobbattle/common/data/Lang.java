package io.github.flemmli97.mobbattle.common.data;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.common.items.MobKill;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.util.GsonHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Lang implements DataProvider {

    private final Map<String, String> data = new LinkedHashMap<>();
    private final PackOutput packOutput;
    private final String modid;
    private final String locale;

    public Lang(PackOutput output) {
        this.packOutput = output;
        this.modid = MobBattle.MODID;
        this.locale = "en_us";
    }

    protected void addTranslations() {
        this.add("item.mobbattle.mob_stick", "Mob Enrager");
        this.add("item.mobbattle.mob_kill", "Mob Killer");
        this.add("item.mobbattle.mob_heal", "Mob Healer");
        this.add("item.mobbattle.mob_group", "Mob Enrager (Multi)");
        this.add("item.mobbattle.mob_effect", "Effect Remover");
        this.add("item.mobbattle.mob_effect_give", "Effect Giver");
        this.add("item.mobbattle.mob_armor", "Armor Editor");
        this.add("item.mobbattle.mob_army", "Mob Army");
        this.add("item.mobbattle.mob_mount", "Mob Mount");
        this.add("item.mobbattle.mob_equip", "Mob Equip");
        this.add("item.mobbattle.boss_bar_adder", "Boss Bar Adder");
        this.add("item.mobbattle.egg_ex", "Mob Spawner");

        this.add("mobbattle.tab", "Mob Battle");

        this.add("conf.mobbattle.particle", "Show Team Particles");
        this.add("conf.mobbattle.addai", "Auto add team-target-ai");

        this.add("mobbattle.gui.potion", "Potion");
        this.add("mobbattle.gui.duration", "Duration");
        this.add("mobbattle.gui.amplifier", "Amplifier");
        this.add("mobbattle.gui.particle", "Particle");
        this.add("mobbattle.gui.team", "Team");
        this.add("mobbattle.gui.amount", "Amount");
        this.add("mobbattle.gui.spacing", "Spacing");
        this.add("mobbattle.gui.save", "Save");

        this.add("tooltip.mobbattle.spawnegg", "Left click an entity to save it. Shift while doing saves nbt too.");
        this.add("tooltip.mobbattle.spawnegg.spawn", "Spawns %s");
        this.add("tooltip.mobbattle.spawnegg.spawn.nbt", "Spawns %s (+NBT)");
        this.add("tooltip.mobbattle.spawnegg.save", "Saved Entity %s");
        this.add("tooltip.mobbattle.spawnegg.save.nbt", "Saved Entity %s with NBT");
        this.add("tooltip.mobbattle.spawnegg.creative", "Needs to be in creative mode to copy entity");

        this.add("tooltip.mobbattle.armor", "Right click an entity to edit their equipment");

        this.add("tooltip.mobbattle.army.first", "Right click block to set first, and then second corner of the box");
        this.add("tooltip.mobbattle.army.second", "Right click into air to to add entities in the box to the team with the name of this item (if exists, else DEFAULT)");
        this.add("tooltip.mobbattle.army.third", "Shift-Right click to reset box");
        this.add("tooltip.mobbattle.army.forth", "Left click to add entities to the team with the name of this item (if exists, else DEFAULT)");
        this.add("tooltip.mobbattle.army.fifth", "Use vanilla /team command to show/change team color");
        this.add("tooltip.mobbattle.army.add", "Added entity to team %s");
        this.add("tooltip.mobbattle.army.add.box", "Added entities in the box to team %s");
        this.add("tooltip.mobbattle.army.reset", "Reset Positions");

        this.add("tooltip.mobbattle.effect.remove", "Left click an entity to remove their potion effects");
        this.add("tooltip.mobbattle.effect.remove.clear", "Effects cleared");

        this.add("tooltip.mobbattle.effect.give.first", "Left click an entity to add saved potion effects");
        this.add("tooltip.mobbattle.effect.give.second", "Right click to edit potion effect");
        this.add("tooltip.mobbattle.effect.give.add", "Added effect %1$s with amplifier %2$s for %3$s ticks");

        this.add("tooltip.mobbattle.equip.first", "Right click block to set first, and then second corner of the box");
        this.add("tooltip.mobbattle.equip.second", "Right click into air to to make entities able to pickup items");
        this.add("tooltip.mobbattle.equip.third", "Shift-Right click to reset box");
        this.add("tooltip.mobbattle.equip.box.add", "Entities in box can now pickup items");
        this.add("tooltip.mobbattle.equip.add", "Entity can pickup items now");
        this.add("tooltip.mobbattle.equip.reset", "Reset Positions");

        this.add("tooltip.mobbattle.group.first", "Left click to select entities");
        this.add("tooltip.mobbattle.group.second", "Right click on entity to set the target");
        this.add("tooltip.mobbattle.group.third", "Shift right click to reset");
        this.add("tooltip.mobbattle.group.remove", "Removed an entity");
        this.add("tooltip.mobbattle.group.reset", "Reset all entities");
        this.add("tooltip.mobbattle.group.add", "Added an entity");

        this.add("tooltip.mobbattle.heal", "Left click on entity to heal it");

        this.add("tooltip.mobbattle.kill", "Left click on entity to kill it");
        this.add("tooltip.mobbattle.kill.mode", "Current mode: %s");
        this.add("tooltip.mobbattle.kill.mode.switch", "Press [%s] to switch between modes");
        this.add("tooltip.mobbattle.kill.all.success", "Cleared all entities");
        this.add(MobKill.Mode.SINGLE.translationKey, "Single Target");
        this.add(MobKill.Mode.ALL.translationKey, "All (right click to kill all)");

        this.add("tooltip.mobbattle.mount.first", "Left click an entity to select");
        this.add("tooltip.mobbattle.mount.second", "Left click another entity to add selected entity as rider");
        this.add("tooltip.mobbattle.mount.reset", "Reset entities");

        this.add("tooltip.mobbattle.bossbar", "Left click an entity to add a bossbar to it");
        this.add("tooltip.mobbattle.bossbar.remove", "Right click an entity to remove the bossbar");
        this.add("tooltip.mobbattle.bossbar.color", "Press [%s] to change bossbar colors");

        this.add("tooltip.mobbattle.stick.contains", "Asigned entity: %s");
        this.add("tooltip.mobbattle.stick.first", "Left click to asign an entity");
        this.add("tooltip.mobbattle.stick.second", "Right click to reset");
        this.add("tooltip.mobbattle.stick.reset", "Reset entities");
        this.add("tooltip.mobbattle.stick.add", "First entity set, hit another entity to set target");
        this.add("mobbattle.gui.potions", "Potions");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return CompletableFuture.runAsync(() -> {
            this.addTranslations();
            if (!this.data.isEmpty()) {
                try {
                    this.save(cache, this.packOutput.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve(this.modid + "/lang/" + this.locale + ".json"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public String getName() {
        return "Languages: " + this.locale;
    }

    private void save(CachedOutput cache, Path target) throws IOException {
        JsonObject json = new JsonObject();
        for (Map.Entry<String, String> pair : this.data.entrySet()) {
            json.addProperty(pair.getKey(), pair.getValue());
        }
        saveTo(cache, json, target);
    }

    public void add(String key, String value) {
        if (this.data.put(key, value) != null)
            throw new IllegalStateException("Duplicate translation key " + key);
    }

    @SuppressWarnings({"UnstableApiUsage", "deprecation"})
    private static void saveTo(CachedOutput cachedOutput, JsonElement jsonElement, Path path) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        HashingOutputStream hashingOutputStream = new HashingOutputStream(Hashing.sha1(), byteArrayOutputStream);
        OutputStreamWriter writer = new OutputStreamWriter(hashingOutputStream, StandardCharsets.UTF_8);
        JsonWriter jsonWriter = new JsonWriter(writer);
        jsonWriter.setSerializeNulls(false);
        jsonWriter.setIndent("  ");
        GsonHelper.writeValue(jsonWriter, jsonElement, null);
        jsonWriter.close();
        cachedOutput.writeIfNeeded(path, byteArrayOutputStream.toByteArray(), hashingOutputStream.hash());
    }
}
