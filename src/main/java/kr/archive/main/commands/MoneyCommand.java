package kr.archive.main.commands;
import static com.mongodb.client.model.Filters.eq;

import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import com.mongodb.client.model.UpdateOptions;
import kr.archive.main.Main;
import kr.archive.main.database.Money;
import kr.archive.main.utils.MessageFormat;
import net.md_5.bungee.api.chat.TextComponent;
import org.bson.BasicBSONObject;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.NumberFormat;

public class MoneyCommand implements CommandExecutor {
    private final MongoCollection<Money> money = Main.mongoDatabase.getCollection("money", Money.class);
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("이 명령어는 유저만 사용가능합니다");
            return true;
        }
        Player player = (Player) sender;
        Money doc = money.find(eq("minecraftId", player.getUniqueId().toString())).first();
        if (doc == null) {
            sender.sendMessage(MessageFormat.ErrorMessage("여기를 클릭해 디스코드 연동 후 이용해주세요"));
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(MessageFormat.SuccessMessage("보유중인 금액 [ " + NumberFormat.getInstance().format(doc.getMoney()) + "원 ]"));
            return true;
        } else if (args.length == 3) {
            System.out.println(args[0] + args[1] + args[2]);
            if(args[0].equals("송금")) {
                Player target = Bukkit.getPlayer(args[1]);
                if(target == null) {
                    sender.sendMessage(MessageFormat.ErrorMessage("서버에서 "+ args[1] + "님을 찾을 수 없습니다."));
                    return true;
                }
                Money targetDoc = money.find(eq("minecraftId", target.getUniqueId().toString())).first();
                if(targetDoc == null) {
                    sender.sendMessage(MessageFormat.ErrorMessage("서버에서 "+ target.getName() + "님이 이용한 기록이 없습니다."));
                    return true;
                }
                if(!args[2].matches("-?\\d+")) {
                    sender.sendMessage(MessageFormat.ErrorMessage("송금할 금액은 숫자만 입력가능합니다."));
                    return true;
                }
                if(doc.getMoney() < Integer.parseInt(args[2])) {
                    sender.sendMessage(MessageFormat.ErrorMessage("송금가능한 최대 금액은 " + NumberFormat.getInstance().format(doc.getMoney()) + "원 입니다"));
                    return true;
                }
                if(Integer.parseInt(args[2]) < 100) {
                    sender.sendMessage(MessageFormat.ErrorMessage("송금가능한 최소 금액은 100원 입니다"));
                    return true;
                }
                money.updateOne(eq("minecraftId", target.getUniqueId().toString()), new Document("$inc", new Document("money", Integer.parseInt(args[2]))));
                money.updateOne(eq("minecraftId", player.getUniqueId().toString()), new Document("$inc", new Document("money", -Integer.parseInt(args[2]))));
                Money newMoney = money.find(eq("minecraftId", player.getUniqueId().toString())).first();
                sender.sendMessage(MessageFormat.SuccessMessage(target.getName() + "님에게 성공적으로 " +  NumberFormat.getInstance().format(Integer.parseInt(args[2]))) + "원을 송급했습니다. 송금 후 잔액은 " + NumberFormat.getInstance().format(newMoney.getMoney()) + "원 입니다.");
                return true;
            }
        }
        sender.sendMessage(MessageFormat.ErrorMessage("찾을 수 없는 명령어입니다."));
        return true;
    }
}
