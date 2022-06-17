package kr.archive.main.listener;

import com.mongodb.client.MongoCollection;
import kr.archive.main.Main;
import kr.archive.main.database.Shop;
import kr.archive.main.database.ShopItem;
import kr.archive.main.service.ShopService;
import kr.archive.main.utils.Constants;
import kr.archive.main.utils.MessageFormat;
import kr.archive.main.utils.MoneyLoader;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import java.text.NumberFormat;

import static com.mongodb.client.model.Filters.eq;

public class PlayerShopListener implements Listener {
    private final MongoCollection<Shop> ShopData = Main.mongoDatabase.getCollection("minecraftShopData", Shop.class);

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInventoryViewClick(InventoryClickEvent event) {
        if(event.getView().getTitle().contains("§8§l[SHOP]")) {
            event.setCancelled(true);
            if(event.getSlotType() != InventoryType.SlotType.CONTAINER) return;
            ItemStack selectItem = event.getCurrentItem();
            if(selectItem == null) return;
            if(selectItem.getType() == Material.AIR) return;
            if(selectItem.getItemMeta().displayName() != null && selectItem.getItemMeta().displayName().contains(Component.text("보유중인 금액"))) {
                Bukkit.getLogger().info("asda");
                return;
            }
            String shopName = event.getView().getTitle().replace("§8§l[SHOP]", "").replace("§8", "").replace("아카이브 상점", "").replace(" ", "");
            Player player = (Player) event.getView().getPlayer();
            Shop SearchShop = ShopData.find(eq("name", shopName)).first();
            if(SearchShop == null) return;
            ShopItem dbItem = SearchShop.getItems().stream().filter(shopItem -> shopItem.getItem().name().equals(selectItem.getType().name())).findAny().orElse(null);
            if(dbItem == null) return;
            int userMoney = MoneyLoader.getUserMoney(player);
            int amountLeft;
            switch (event.getClick()) {
                case LEFT -> {
                    if (dbItem.getBuy() <= 0) {
                        player.sendMessage(MessageFormat.ErrorMessage("해당 아이템은 구매가 불가능합니다"));
                        Constants.Sound.playFailSound(player);
                        return;
                    }
                    if (userMoney < dbItem.getBuy()) {
                        player.sendMessage(MessageFormat.ErrorMessage("해당 아이템을 구매하기엔 " + NumberFormat.getInstance().format(dbItem.getBuy() - userMoney) + "원이 부족합니다"));
                        Constants.Sound.playFailSound(player);
                        return;
                    }
                    amountLeft = ShopService.add(new ItemStack(dbItem.getItem(), 1), player.getInventory());
                    if (amountLeft <= 0) {
                        player.sendMessage(MessageFormat.SuccessMessage("해당 아이템을 구매했어요! §c( -" + NumberFormat.getInstance().format(dbItem.getBuy()) + " )"));
                        Constants.Sound.playSuccessSound(player);
                        MoneyLoader.removeUserMoney(player, dbItem.getBuy());
                    } else {
                        player.sendMessage(MessageFormat.ErrorMessage("해당 아이템을 구매하지 못했어요 인벤토리가 부족한거같아요!"));
                        Constants.Sound.playFailSound(player);
                    }
                }
                case SHIFT_LEFT -> {
                    if (dbItem.getBuy() <= 0) {
                        player.sendMessage(MessageFormat.ErrorMessage("해당 아이템은 구매가 불가능합니다"));
                        Constants.Sound.playFailSound(player);
                        return;
                    }
                    if (userMoney < (dbItem.getBuy() * 64)) {
                        player.sendMessage(MessageFormat.ErrorMessage("해당 아이템을 구매하기엔 " + NumberFormat.getInstance().format((dbItem.getBuy() * 64L) - userMoney) + "원이 부족합니다"));
                        Constants.Sound.playFailSound(player);
                        return;
                    }
                    amountLeft = ShopService.add(new ItemStack(dbItem.getItem(), 64), player.getInventory());
                    if (amountLeft <= 0) {
                        player.sendMessage(MessageFormat.SuccessMessage("해당 아이템을 64개 구매했어요! §c( -" + NumberFormat.getInstance().format(dbItem.getBuy() * 64L) + " )"));
                        Constants.Sound.playSuccessSound(player);
                    } else if (amountLeft == 64) {
                        player.sendMessage(MessageFormat.ErrorMessage("해당 아이템을 구매하지 못했어요 인벤토리가 부족한거같아요!"));
                        Constants.Sound.playFailSound(player);
                    } else {
                        player.sendMessage(MessageFormat.SuccessMessage("해당 아이템을" + (64 - amountLeft) + "개 구매했어요! §c( -" + NumberFormat.getInstance().format((dbItem.getBuy() * 64L) - ((long) dbItem.getBuy() * amountLeft)) + " )"));
                        MoneyLoader.removeUserMoney(player, (dbItem.getBuy() * 64) - (dbItem.getBuy() * amountLeft));
                        Constants.Sound.playSuccessSound(player);
                    }
                }
                case RIGHT -> {
                    if (dbItem.getSell() <= 0) {
                        player.sendMessage(MessageFormat.ErrorMessage("해당 아이템은 판매가 불가능합니다"));
                        Constants.Sound.playFailSound(player);
                        return;
                    }
                    amountLeft = ShopService.remove(new ItemStack(dbItem.getItem(), 1), player.getInventory());
                    if (amountLeft <= 0) {
                        player.sendMessage(MessageFormat.SuccessMessage("해당 아이템을 판매했어요! §a( +" + NumberFormat.getInstance().format(dbItem.getSell()) + " )"));
                        Constants.Sound.playSuccessSound(player);
                        MoneyLoader.addUserMoney(player, dbItem.getSell());
                    } else {
                        player.sendMessage(MessageFormat.ErrorMessage("해당 아이템을 판매하지 못했어요 아이템이 없는거 같아요!"));
                        Constants.Sound.playFailSound(player);
                    }
                }
                case SHIFT_RIGHT -> {
                    if (dbItem.getSell() <= 0) {
                        player.sendMessage(MessageFormat.ErrorMessage("해당 아이템은 판매가 불가능합니다"));
                        Constants.Sound.playFailSound(player);
                        return;
                    }
                    amountLeft = ShopService.remove(new ItemStack(dbItem.getItem(), 64), player.getInventory());
                    if (amountLeft <= 0) {
                        player.sendMessage(MessageFormat.SuccessMessage("해당 아이템을 판매했어요! §a( +" + NumberFormat.getInstance().format(dbItem.getSell() * 64L) + " )"));
                        Constants.Sound.playSuccessSound(player);
                        MoneyLoader.addUserMoney(player, dbItem.getSell());
                    } else if (amountLeft == 64) {
                        player.sendMessage(MessageFormat.ErrorMessage("해당 아이템을 판매하지 못했어요 아이템이 없는거 같아요!"));
                        Constants.Sound.playFailSound(player);
                    } else {
                        player.sendMessage(MessageFormat.SuccessMessage("해당 아이템을 " + (64 - amountLeft) + "개 판매했어요! §a( +" + NumberFormat.getInstance().format((dbItem.getSell() * 64L) - ((long) dbItem.getSell() * amountLeft)) + " )"));
                        Constants.Sound.playSuccessSound(player);
                        MoneyLoader.addUserMoney(player, (dbItem.getSell() * 64) - (dbItem.getSell() * amountLeft));
                    }
                }
            }
        }
    }
}
