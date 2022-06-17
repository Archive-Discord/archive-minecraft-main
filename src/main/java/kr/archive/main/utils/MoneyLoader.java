package kr.archive.main.utils;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import kr.archive.main.Main;
import kr.archive.main.database.Money;
import kr.archive.main.database.User;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.NumberFormat;

import static com.mongodb.client.model.Filters.eq;

public class MoneyLoader {
    private static final MongoCollection<Money> money = Main.mongoDatabase.getCollection("money", Money.class);
    private static final MongoCollection<User> userData = Main.mongoDatabase.getCollection("userData", User.class);

    public static int getUserMoney(Player player) {
        Money doc = money.find(eq("minecraftId", player.getUniqueId().toString())).first();
        if (doc == null) {
            User userDB = userData.find(eq("minecraft_id", player.getUniqueId().toString())).first();
            if(userDB == null) {
                return 0;
            }
            money.updateOne(eq("userid", userDB.getId()), new Document("$set", new Document("minecraftId", userDB.getMinecraft_id()).append("money", 5000)), new UpdateOptions().upsert(true));
            doc = money.find(eq("minecraftId", player.getUniqueId().toString())).first();
        }
        return doc.getMoney();
    }

    public static int addUserMoney(Player player, int UpdateMoney) {
        int nowMoney = getUserMoney(player);
        money.updateOne(eq("minecraftId", player.getUniqueId().toString()), new Document("$inc", new Document("money", UpdateMoney)));
        Money doc = money.find(eq("minecraftId", player.getUniqueId().toString())).first();
        return doc.getMoney();
    }

    public static int removeUserMoney(Player player, int UpdateMoney) {
        int nowMoney = getUserMoney(player);
        money.updateOne(eq("minecraftId", player.getUniqueId().toString()), new Document("$inc", new Document("money", -UpdateMoney)));
        Money doc = money.find(eq("minecraftId", player.getUniqueId().toString())).first();
        return doc.getMoney();
    }

    public static ItemStack MoneyItemLoader(Player player) {
        int userMoney = MoneyLoader.getUserMoney(player);
        ItemStack moneyItemStack = new ItemStack(Material.DIAMOND);
        ItemMeta MoneyMeta = moneyItemStack.getItemMeta();
        MoneyMeta.setDisplayName("§a보유중인 금액: " + NumberFormat.getInstance().format(userMoney) + "원");
        moneyItemStack.setItemMeta(MoneyMeta);
        return moneyItemStack;
    }
}
