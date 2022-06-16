package kr.archive.main.listener;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import kr.archive.main.Main;
import kr.archive.main.database.Money;
import kr.archive.main.database.Shop;
import kr.archive.main.database.ShopItem;
import kr.archive.main.utils.MessageFormat;
import kr.archive.main.utils.MoneyLoader;
import kr.archive.main.service.ListenerService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.elemMatch;
import static com.mongodb.client.model.Filters.eq;

public class PlayerShopListener implements Listener {
    private final MongoCollection<Shop> ShopData = Main.mongoDatabase.getCollection("minecraftShopData", Shop.class);
    private final MongoCollection<Money> money = Main.mongoDatabase.getCollection("money", Money.class);

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInventoryViewClick(InventoryClickEvent event) {
        if(event.getView().getTitle().contains("§8§l[SHOP]")) {
            ItemStack selectItem = event.getCurrentItem();
            if(selectItem == null) return;
            if(selectItem.getType() == Material.AIR) return;
            String shopName = event.getView().getTitle().replace("§8§l[SHOP]", "").replace("§8", "").replace("아카이브 상점", "").replace(" ", "");
            Player player = (Player) event.getView().getPlayer();
            event.setCancelled(true);
            Shop SearchShop = ShopData.find(eq("name", shopName)).first();
            ShopItem dbItem = SearchShop.getItems().stream().filter(shopItem -> shopItem.getItem().name().equals(selectItem.getType().name())).findAny().orElse(null);
            int userMoney = MoneyLoader.getUserMoney(player);
            switch (event.getClick()) {
                case LEFT:
                    if(userMoney < dbItem.getBuy()) {
                        player.sendMessage(MessageFormat.ErrorMessage("해당 아이템을 구매하기엔 " +  NumberFormat.getInstance().format(dbItem.getBuy() - userMoney) + "원이 부족합니다"));
                        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0f, 1.0f);
                        return;
                    }

                    player.sendMessage(MessageFormat.SuccessMessage("해당 아이템을 구매했어요! §c( -" + NumberFormat.getInstance().format(dbItem.getBuy()) + " )"));
                    player.getInventory().addItem(selectItem);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 1.0f, 1.0f);
                    MoneyLoader.removeUserMoney(player, dbItem.getBuy());
                    return;
            }
        }
    }
}
