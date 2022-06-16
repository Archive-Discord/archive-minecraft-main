package kr.archive.main.commands;

import com.mongodb.DBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.UpdateOptions;
import kr.archive.main.Main;
import kr.archive.main.database.Money;
import kr.archive.main.database.Shop;
import kr.archive.main.database.ShopItem;
import kr.archive.main.database.User;
import kr.archive.main.utils.MessageFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.TranslationRegistry;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static com.mongodb.client.model.Filters.eq;

public class ShopCommand implements CommandExecutor {
    private final MongoCollection<Money> money = Main.mongoDatabase.getCollection("money", Money.class);
    private final MongoCollection<User> userData = Main.mongoDatabase.getCollection("userData", User.class);
    private final MongoCollection<Shop> ShopData = Main.mongoDatabase.getCollection("minecraftShopData", Shop.class);
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("이 명령어는 유저만 사용가능합니다");
            return true;
        }
        Player player = (Player) sender;
        if (args.length < 1) {
            player.sendMessage(MessageFormat.SuccessMessage(""));
            player.sendMessage(MessageFormat.SuccessMessage("/상점 생성 [이름] - 새로운 상점을 생성합니다."));
            player.sendMessage(MessageFormat.SuccessMessage("/상점 삭제 [이름] - 생성된 상점을 삭제합니다."));
            player.sendMessage(MessageFormat.SuccessMessage("/상점 목록 - 생성된 상점의 목록을 불러옵니다."));
            player.sendMessage(MessageFormat.SuccessMessage("/상점 줄 [이름] [1~6] - 생성된 상점의 GUI 줄 수를 설정합니다."));
            player.sendMessage(MessageFormat.SuccessMessage("/상점 물품 등록 [이름] [슬롯] [구매가] [판매가] - 생성된 상점에 아이템을 등록합니다."));
            player.sendMessage(MessageFormat.SuccessMessage("/상점 물품 삭제 [이름] [슬롯] - 등록한 아이템을 삭제합니다."));
            player.sendMessage(MessageFormat.SuccessMessage("/상점 열기 [이름] - 생성된 상점을 이용합니다."));
            player.sendMessage(MessageFormat.SuccessMessage(""));
            return true;
        }
        switch (args[0]) {
            case "생성": {
                if (args[1] == null) {
                    player.sendMessage(MessageFormat.SuccessMessage("/상점 생성 [이름] - 새로운 상점을 생성합니다."));
                    return true;
                }
                Shop shop = ShopData.find(eq("name", args[1])).first();
                if (shop == null) {
                    UpdateOptions options = new UpdateOptions().upsert(true);
                    ShopData.updateOne(eq("name", args[1]), new Document("$set", new Document("name", args[1])), options);
                    player.sendMessage(MessageFormat.SuccessMessage(args[1] + " 상점을 생성하였습니다. [/상점 목록]"));
                    return true;
                }
                player.sendMessage(MessageFormat.SuccessMessage(args[1] + " 상점은 이미 생성되어 있습니다. &a[/상점 목록]"));
                return true;
            }
            case "삭제": {
                if (args[1] == null) {
                    player.sendMessage(MessageFormat.SuccessMessage("/상점 삭제 [이름] - 생성된 상점을 삭제합니다."));
                    return true;
                }
                Shop shop = ShopData.find(eq("name", args[1])).first();
                if (shop == null) {
                    player.sendMessage(MessageFormat.SuccessMessage(args[1] + " 상점은 생성되어 있지 않습니다. [/상점 생성]"));
                    return true;
                }
                ShopData.deleteOne(eq("name", args[1]));
                player.sendMessage(MessageFormat.SuccessMessage(args[1] + " 상점을 삭제하였습니다."));
                return true;
            }
            case "목록": {
                MongoCursor<Shop> shop = ShopData.find().iterator();
                long shopSize = ShopData.countDocuments();
                if (shopSize <= 0) {
                    player.sendMessage(MessageFormat.ErrorMessage("서버에 생성된 상점이 존재하지 않습니다."));
                    return true;
                }
                player.sendMessage(ChatColor.GRAY + "─────────상점목록─────────");
                for (int i = 0; i < shopSize; i++) {
                    player.sendMessage(MessageFormat.SuccessMessage(i + "번쨰 > " + shop.next().getName()));
                }
                player.sendMessage(ChatColor.GRAY + "──────────────────────");
                return true;
            }
            case "물품":
                if (args[1] == null) {
                    player.sendMessage(MessageFormat.SuccessMessage("/상점 물품 등록 [이름] [슬롯] [구매가] [판매가] - 생성된 상점에 아이템을 등록합니다."));
                    player.sendMessage(MessageFormat.SuccessMessage("/상점 물품 삭제 [이름] [슬롯] - 등록한 아이템을 삭제합니다."));
                    return true;
                }
                if (args[1].equals("등록")) {
                    if (args.length != 6) {
                        player.sendMessage(MessageFormat.SuccessMessage("/상점 물품 등록 [이름] [슬롯] [구매가] [판매가] - 생성된 상점에 아이템을 등록합니다."));
                        return true;
                    }
                    if (args[2] != null) {
                        if (args[3].matches("-?\\d+")) {
                            if (args[4].matches("-?\\d+")) {
                                if (args[5].matches("-?\\d+")) {
                                    ItemStack item = player.getInventory().getItemInMainHand();
                                    if (item.getType() != Material.AIR) {
                                        Shop shop = ShopData.find(eq("name", args[2])).first();
                                        if (shop == null) {
                                            player.sendMessage(MessageFormat.ErrorMessage(args[2] + " 상점은 생성되어 있지 않습니다. [/상점 생성]"));
                                            return true;
                                        }
                                        ShopData.updateOne(eq("name", args[2]), new Document("$push", new Document("items", new Document("row", Integer.parseInt(args[3])).append("item", item.getType()).append("buy", Integer.parseInt(args[4])).append("sell", Integer.parseInt(args[5])))));
                                        player.sendMessage(MessageFormat.SuccessMessage(args[2] + " 상점에 " + item.getType().name() + "가 " + "구매가" + Integer.parseInt(args[4]) + "원, 판매가" + Integer.parseInt(args[5]) + "원 으로 추가가 완료되었습니다"));
                                        return true;
                                    } else {
                                        player.sendMessage(MessageFormat.ErrorMessage("상점에 등록할 아이템을 들고 입력해주시기 바랍니다."));
                                        return true;
                                    }
                                } else {
                                    player.sendMessage(MessageFormat.ErrorMessage("상점의 판매가는 숫자만 입력해주시기 바랍니다."));
                                    return true;
                                }
                            } else {
                                player.sendMessage(MessageFormat.ErrorMessage("상점의 구매가는 숫자만 입력해주시기 바랍니다."));
                                return true;
                            }
                        } else {
                            player.sendMessage(MessageFormat.ErrorMessage("상점의 슬롯은 숫자만 입력해주시기 바랍니다."));
                            return true;
                        }
                    } else {
                        player.sendMessage(MessageFormat.SuccessMessage("/상점 물품 등록 [이름] [슬롯] [구매가] [판매가] - 생성된 상점에 아이템을 등록합니다."));
                        return true;
                    }
                }
                break;
            case "열기":
                if (args[1] != null) {
                    Shop shop = ShopData.find(eq("name", args[1])).first();
                    if (shop == null) {
                        player.sendMessage(MessageFormat.ErrorMessage("찾을 수 없는 상점입니다"));
                        return true;
                    }
                    Inventory chestShop = Bukkit.createInventory(player, 27, "§8§l[SHOP] §8"+ shop.getName() + "아카이브 상점");
                    for (ShopItem shopItem : shop.getItems()) {
                        ItemStack item = new ItemStack(shopItem.getItem());
                        ItemMeta meta = item.getItemMeta();
                        List<String> lore = new ArrayList<>();
                        lore.add(ChatColor.WHITE + " ");
                        lore.add("" + ChatColor.DARK_GRAY + ChatColor.BOLD + "> " + ChatColor.WHITE + "아이템 구매가: " + (shopItem.getBuy() == 0 ? ChatColor.RED + "구매불가" : ChatColor.GREEN + Integer.toString(shopItem.getBuy()) + "원"));
                        lore.add("" + ChatColor.DARK_GRAY + ChatColor.BOLD + "> " + ChatColor.WHITE + "아이탬 판매가: " + (shopItem.getBuy() == 0 ? ChatColor.RED + "판매불가" : ChatColor.GREEN + Integer.toString(shopItem.getSell()) + "원"));
                        lore.add("" + ChatColor.GOLD + ChatColor.BOLD + "> " + ChatColor.WHITE + "좌클릭 시 아이템을 구매합니다");
                        lore.add("" + ChatColor.GOLD + ChatColor.BOLD + "  └ " + ChatColor.WHITE + "쉬프트 + 좌클릭 시 아이템 64개를 구매합니다.");
                        lore.add("" + ChatColor.GOLD + ChatColor.BOLD + "> " + ChatColor.WHITE + "우클릭 시 아이템을 판매합니다.");
                        lore.add("" + ChatColor.GOLD + ChatColor.BOLD + "  └ " + ChatColor.WHITE + "쉬프트 + 우클릭 시 아이템을 모두 판매합니다.");
                        lore.add(ChatColor.WHITE + "");
                        meta.setLore(lore);
                        item.setItemMeta(meta);
                        chestShop.setItem(shopItem.getRow(), item);
                    }
                    player.openInventory(chestShop);
                } else {
                    player.sendMessage(MessageFormat.ErrorMessage("오픈할 상점을 입력해주세요."));
                    return true;
                }
                return true;
            default:
                player.sendMessage(MessageFormat.ErrorMessage("올바르지 않은 명령어 사용법입니다."));
                return true;
        }
        return true;
    }
}
