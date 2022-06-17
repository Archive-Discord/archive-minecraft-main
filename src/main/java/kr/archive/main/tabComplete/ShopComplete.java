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
    List<String> makeArguments = new ArrayList<String>();
    List<String> itemArguments = new ArrayList<String>();
    private final MongoCollection<Shop> ShopData = Main.mongoDatabase.getCollection("minecraftShopData", Shop.class);

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] arg) {
        if(arguments.isEmpty()) {
            arguments.add("생성");
            arguments.add("삭제");
            arguments.add("목록");
            arguments.add("줄");
            arguments.add("물품");
            arguments.add("열기");
            return arguments;
        } else if(arg[0].equals("열기")) {
            for (Shop shop : ShopData.find()) {
                shopArguments.add(shop.getName());
            }
            return shopArguments;
        } else if(arg[0].equals("줄")) {
            for (Shop shop : ShopData.find()) {
                shopArguments.add(shop.getName());
            }
            return shopArguments;
        } else if (arg[0].equals("생성")) {
            return null;
        } else if (arg[0].equals("삭제")) {
            for (Shop shop : ShopData.find()) {
                shopArguments.add(shop.getName());
            }
            return shopArguments;
        } else if (arg[0].equals("물품")) {
            itemArguments.add("등록");
            itemArguments.add("삭제");
            return itemArguments;
        }
        return arguments;
    }
}
