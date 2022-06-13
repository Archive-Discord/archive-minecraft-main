package kr.archive.main.service;

import kr.archive.main.events.LoginEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.PlayerEvent;

public class ListenerService {
    public static boolean shouldCancelEvent(EntityEvent event) {
        Entity entity = event.getEntity();
        return shouldCancelEvent(entity);
    }

    public static boolean shouldCancelEvent(Entity entity) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            return shouldCancelEvent(player);
        }
        return false;
    }
    public static boolean shouldCancelEvent(PlayerEvent event) {
        Player player = event.getPlayer();
        return shouldCancelEvent(player);
    }

    public static boolean shouldCancelEvent(Player player) {
        return player != null && !LoginEvent.LoginCheck(player) && !isNpc(player);
    }
    public static boolean isNpc(Player player) {
        return player.hasMetadata("NPC");
    }
}
