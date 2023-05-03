package io.github.flemmli97.mobbattle.data;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import io.github.flemmli97.mobbattle.MobBattle;
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

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
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
        this.add("item.mobbattle.egg_ex", "Mob Spawner");

        this.add("mobbattle.tab", "Mob Battle");

        this.add("conf.mobbattle.particle", "Show Team Particles");
        this.add("conf.mobbattle.addai", "Auto add team-target-ai");

        this.add("tooltip.spawnegg", "Left click an entity to save it. Shift while doing saves nbt too.");
        this.add("tooltip.spawnegg.spawn", "Spawns %s");
        this.add("tooltip.spawnegg.save", "Saved Entity %s");
        this.add("tooltip.spawnegg.creative", "Needs to be in creative mode to copy entity");

        this.add("tooltip.armor", "Right click an entity to edit their equipment");

        this.add("tooltip.army.first", "Right click block to set first, and then second corner of the box");
        this.add("tooltip.army.second", "Right click into air to to add entities in the box to the team with the name of this item (if exists, else DEFAULT)");
        this.add("tooltip.army.third", "Shift-Right click to reset box");
        this.add("tooltip.army.forth", "Left click to add entities to the team with the name of this item (if exists, else DEFAULT)");
        this.add("tooltip.army.fifth", "Use vanilla /team command to show/change team color");
        this.add("tooltip.army.add", "Added entity to team %s");
        this.add("tooltip.army.add.box", "Added entities in the box to team %s");
        this.add("tooltip.army.reset", "Reset Positions");

        this.add("tooltip.effect.remove", "Left click an entity to remove their potion effects");
        this.add("tooltip.effect.remove.clear", "Effects cleared");

        this.add("tooltip.effect.give.first", "Left click an entity to remove their potion effects");
        this.add("tooltip.effect.give.second", "Left click an entity to remove their potion effects");
        this.add("tooltip.effect.give.add", "Added effect %1$s with amplifier %2$s for %3$s ticks");

        this.add("tooltip.equip.first", "Right click block to set first, and then second corner of the box");
        this.add("tooltip.equip.second", "Right click into air to to make entities able to pickup items");
        this.add("tooltip.equip.third", "Shift-Right click to reset box");
        this.add("tooltip.equip.box.add", "Entities in box can now pickup items");
        this.add("tooltip.equip.add", "Entity can pickup items now");
        this.add("tooltip.equip.reset", "Reset Positions");

        this.add("tooltip.group.first", "Left click to select entities");
        this.add("tooltip.group.second", "Right click on entity to set the target");
        this.add("tooltip.group.third", "Shift right click to reset");
        this.add("tooltip.group.remove", "Removed an entity");
        this.add("tooltip.group.reset", "Reset all entities");
        this.add("tooltip.group.add", "Added an entity");

        this.add("tooltip.heal", "Left click on entity to heal it");

        this.add("tooltip.kill", "Left click on entity to kill it");

        this.add("tooltip.mount.first", "Left click an entity to select");
        this.add("tooltip.mount.second", "Left click another entity to add selected entity as rider");
        this.add("tooltip.mount.reset", "Reset entities");

        this.add("tooltip.stick.contains", "Asigned entity: %s");
        this.add("tooltip.stick.first", "Left click to asign an entity");
        this.add("tooltip.stick.second", "Right click to reset");
        this.add("tooltip.stick.reset", "Reset entities");
        this.add("tooltip.stick.add", "First entity set, hit another entity to set target");
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
