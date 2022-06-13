package kr.archive.main.listener;

import kr.archive.main.Main;
import kr.archive.main.service.ListenerService;
import kr.archive.main.utils.MessageFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;

import java.util.HashMap;

import static kr.archive.main.events.LoginEvent.LogOut;

public class PlayerListener implements Listener {
    public static HashMap<String, Location> LastLocation = new HashMap<String, Location>();

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerInventoryClick(InventoryClickEvent event) {
        if (ListenerService.shouldCancelEvent(event.getWhoClicked())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerInventoryOpen(InventoryOpenEvent event) {
        final HumanEntity player = event.getPlayer();

        if (ListenerService.shouldCancelEvent(player)) {
            event.setCancelled(true);
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), player::closeInventory, 1);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerConsumeItem(PlayerItemConsumeEvent event) {
        if (ListenerService.shouldCancelEvent(event)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerHeldItem(PlayerItemHeldEvent event) {
        if (ListenerService.shouldCancelEvent(event)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (ListenerService.shouldCancelEvent(event)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (ListenerService.shouldCancelEvent(event)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        if (ListenerService.shouldCancelEvent(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerEditBook(PlayerEditBookEvent event) {
        if (ListenerService.shouldCancelEvent(event)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        if (ListenerService.shouldCancelEvent(event)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerFish(PlayerFishEvent event) {
        if (ListenerService.shouldCancelEvent(event)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerShear(PlayerShearEntityEvent event) {
        if (ListenerService.shouldCancelEvent(event)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerHitPlayerEvent(EntityDamageByEntityEvent event) {
        if (ListenerService.shouldCancelEvent(event)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (ListenerService.shouldCancelEvent(event)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (ListenerService.shouldCancelEvent(event)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (ListenerService.shouldCancelEvent(event)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (!ListenerService.shouldCancelEvent(event)) {
            return;
        }
        Location spawn = event.getPlayer().getLocation();
        if (spawn.getWorld() != null) {
            event.setRespawnLocation(spawn);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getBlockX() == to.getBlockX()
                && from.getBlockZ() == to.getBlockZ()
                && from.getY() - to.getY() >= 0) {
            return;
        }

        Player player = event.getPlayer();
        if (!ListenerService.shouldCancelEvent(player)) {
            return;
        }

        if(LastLocation.get(player.getUniqueId().toString()) == null) {
            LastLocation.put(player.getUniqueId().toString(), player.getLocation());
        }else {
            player.teleport(LastLocation.get(player.getUniqueId().toString()));
        }

    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String cmd = event.getMessage().split(" ")[0].toLowerCase();
        if ("/motd".equals(cmd)) {
            return;
        }

        final Player player = event.getPlayer();
        if (ListenerService.shouldCancelEvent(player)) {
            event.setCancelled(true);
            Component textComponent = Component.text(MessageFormat.ErrorMessage("명령어를 사용하시기전 여기를 눌러 디스코드연동을 해주세요")).clickEvent(ClickEvent.openUrl("https://discord.gg/pSG6tSxxS2"));
            player.sendMessage(textComponent);
        }
    }

    private void removeUnauthorizedRecipients(AsyncPlayerChatEvent event) {
        event.getRecipients().removeIf(ListenerService::shouldCancelEvent);
        if (event.getRecipients().isEmpty()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        final boolean mayPlayerSendChat = !ListenerService.shouldCancelEvent(player);
        if (mayPlayerSendChat) {
            removeUnauthorizedRecipients(event);
        } else {
            event.setCancelled(true);
            Component textComponent = Component.text(MessageFormat.ErrorMessage("채팅을 입력하시기전 여기를 눌러 디스코드연동을 해주세요")).clickEvent(ClickEvent.openUrl("https://discord.gg/pSG6tSxxS2"));
            player.sendMessage(textComponent);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerKick(PlayerKickEvent event) {
        if (event.getReason().contains("You logged in from another location")) {
            event.setCancelled(true);
            return;
        }
        Player player = event.getPlayer();
        LogOut(player);
        LastLocation.remove(player.getUniqueId().toString());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (ListenerService.shouldCancelEvent(event)) {
            event.setQuitMessage(null);
        }
        Player player = event.getPlayer();
        LogOut(player);
        LastLocation.remove(player.getUniqueId().toString());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoinMessage(PlayerJoinEvent event) {
        String joinMsg = event.getJoinMessage();
        if (joinMsg != null) {
            event.setJoinMessage(null);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerPreLoginEventLowest(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return;
        }

        if (event.getAddress() == null) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, MessageFormat.SuccessMessage("아이피 정보를 알 수 없습니다. 관리자에게 문의해주세요."));
            return;
        }
    }

    public static Location playerLocation(Player player) {
        return LastLocation.get(player.getUniqueId().toString());
    }
}
