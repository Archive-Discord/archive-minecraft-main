package kr.archive.main.listener;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import kr.archive.main.Main;
import kr.archive.main.database.Shop;
import kr.archive.main.database.ShopItem;
import kr.archive.main.service.ListenerService;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.eq;

public class PlayerShopListener implements Listener {
    private final MongoCollection<Shop> ShopData = Main.mongoDatabase.getCollection("minecraftShopData", Shop.class);

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInventoryViewClick(InventoryClickEvent event) {
        if(event.getView().getTitle().contains("§8§l[SHOP]")) {
            String shopName = event.getView().getTitle().replace("§8§l[SHOP]", "").replace("§8", "").replace("아카이브 상점", "").replace(" ", "");
            event.setCancelled(true);
            Shop SearchShop = ShopData.find(eq("name", shopName)).first();
            ItemStack selectItem = event.getCurrentItem();
            for(ShopItem item: SearchShop.getItems()) {
                ItemStack searchItem = new ItemStack(item.getItem());
                if(!searchItem.equals(selectItem)) return;

            }
        }
    }
}
