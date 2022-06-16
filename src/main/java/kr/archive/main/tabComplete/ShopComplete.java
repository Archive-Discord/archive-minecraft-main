package kr.archive.main.tabComplete;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import kr.archive.main.Main;
import kr.archive.main.database.Shop;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ShopComplete implements TabCompleter {
    List<String> arguments = new ArrayList<String>();
    List<String> shopArguments = new ArrayList<String>();
    private final MongoCollection<Shop> ShopData = Main.mongoDatabase.getCollection("minecraftShopData", Shop.class);

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] arg) {
        if(arguments.isEmpty()) {
            arguments.add("생성");
            arguments.add("삭제");
            arguments.add("목록");
            arguments.add("물품");
            arguments.add("열기");
            return arguments;
        } else if(arg[0].equals("열기")) {
            MongoCursor<Shop> shopList = ShopData.find().iterator();
            while (shopList.hasNext()) {
                shopArguments.add(shopList.next().getName());
            }
            return shopArguments;
        } else {
            return arguments;
        }
    }
}
