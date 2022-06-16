package kr.archive.main.utils;

import com.mongodb.client.MongoCollection;
import kr.archive.main.Main;
import kr.archive.main.database.Money;
import kr.archive.main.database.User;
import org.bson.Document;
import org.bukkit.entity.Player;

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
            money.updateOne(eq("userid", userDB.getId()), eq("minecraftId", userDB.getMinecraft_id()));
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
}
