package kr.archive.main.utils;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;


import java.util.Map;

public class MaterialUtil {
    private static final Yaml YAML = new Yaml(new YamlBukkitConstructor(), new YamlRepresenter(), new DumperOptions());

    private static class YamlBukkitConstructor extends YamlConstructor {
        public YamlBukkitConstructor() {
            this.yamlConstructors.put(new Tag(Tag.PREFIX + "org.bukkit.inventory.ItemStack"), yamlConstructors.get(Tag.MAP));
        }
    }
    public static boolean equals(ItemStack one, ItemStack two) {
        if (one == null || two == null) {
            return one == two;
        }
        if (one.isSimilar(two)) {
            return true;
        }

        // Additional checks as serialisation and de-serialisation might lead to different item meta
        // This would only be done if the items share the same item meta type so it shouldn't be too inefficient
        // Special check for books as their pages might change when serialising (See SPIGOT-3206 and ChestShop#250)
        // Special check for explorer maps/every item with a localised name (See SPIGOT-4672)
        // Special check for legacy spawn eggs (See ChestShop#264)
        if (one.getType() != two.getType()
                || one.getDurability() != two.getDurability()
                || (one.hasItemMeta() && two.hasItemMeta() && one.getItemMeta().getClass() != two.getItemMeta().getClass())) {
            return false;
        }
        if (!one.hasItemMeta() && !two.hasItemMeta()) {
            return true;
        }
        ItemMeta oneMeta = one.getItemMeta();
        ItemMeta twoMeta = two.getItemMeta();
        // return true if both are null or same, false if only one is null
        if (oneMeta == twoMeta || oneMeta == null || twoMeta == null) {
            return oneMeta == twoMeta;
        }
        Map<String, Object> oneSerMeta = oneMeta.serialize();
        Map<String, Object> twoSerMeta = twoMeta.serialize();
        if (oneSerMeta.equals(twoSerMeta)) {
            return true;
        }

        // Try to use same parsing as the YAML dumper in the ItemDatabase when generating the code as the last resort
        ItemStack oneDumped = YAML.loadAs(YAML.dump(one), ItemStack.class);
        if (oneDumped.isSimilar(two)) {
            return true;
        }

        ItemMeta oneDumpedMeta = oneDumped.getItemMeta();
        if (oneDumpedMeta != null && oneDumpedMeta.serialize().equals(twoSerMeta)) {
            return true;
        }

        ItemStack twoDumped = YAML.loadAs(YAML.dump(two), ItemStack.class);
        if (oneDumped.isSimilar(twoDumped)) {
            return true;
        }

        ItemMeta twoDumpedMeta = twoDumped.getItemMeta();
        if (oneDumpedMeta != null && twoDumpedMeta != null && oneDumpedMeta.serialize().equals(twoDumpedMeta.serialize())) {
            return true;
        }

        // return true if both are null or same, false otherwise
        return oneDumpedMeta == twoDumpedMeta;
    }

    public static boolean isEmpty(ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }
}
