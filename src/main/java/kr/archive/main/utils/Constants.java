package kr.archive.main.utils;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Constants {
    public class Sound {
        public static void playSuccessSound(Player player) {
            player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
        }

        public static void playFailSound(Player player) {
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_IRON_GOLEM_DAMAGE, 0.2f, 0.5f);
        }
    }
}
