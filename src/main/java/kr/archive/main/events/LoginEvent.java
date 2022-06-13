package kr.archive.main.events;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import kr.archive.main.Main;
import kr.archive.main.database.MinecraftAuth;
import kr.archive.main.database.User;
import kr.archive.main.utils.Generater;
import kr.archive.main.utils.MessageFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

import static com.mongodb.client.model.Filters.eq;
import static kr.archive.main.listener.PlayerListener.playerLocation;

public class LoginEvent implements Listener {
    private final MongoCollection<User> userData = Main.mongoDatabase.getCollection("userData", User.class);
    private final MongoCollection<MinecraftAuth> minecraftAuth = Main.mongoDatabase.getCollection("minecraftAuth", MinecraftAuth.class);
    public static HashMap<String, Boolean> loginUsers = new HashMap<String, Boolean>();
    public static HashMap<String, String> LastToken = new HashMap<String, String>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        User userDB = userData.find(eq("minecraft_id", player.getUniqueId().toString())).first();
        if(userDB == null) {
            String AccessToken = Generater.randomString(30);
            String lastToken = LastToken.get(player.getUniqueId().toString());
            if(lastToken == null) {
                UpdateOptions options = new UpdateOptions().upsert(true);
                Document updateData = new Document("$set", new Document().append("accessToken", AccessToken).append("minecraftName", player.getName()).append("status", "pending"));
                minecraftAuth.updateOne(eq("minecraftId", player.getUniqueId().toString()), updateData, options);
                LastToken.put(player.getUniqueId().toString(), AccessToken);
                authCheckTask(player, AccessToken);
                loginUsers.put(player.getUniqueId().toString(), false);
            } else {
                authCheckTask(player, lastToken);
            }
            return;
        }
        loginUsers.put(player.getUniqueId().toString(), true);
        player.sendMessage(MessageFormat.SuccessMessage(player.getName() + "님 어서오세요!"));
    }

    private void authCheckTask(Player player, String AccessToken) {
        BukkitTask task = new BukkitRunnable() {
            int countdown = 50;
            @Override
            public void run() {
                User userDB = userData.find(eq("minecraft_id", player.getUniqueId().toString())).first();
                if(userDB == null) {
                    Component textComponent = Component.text(MessageFormat.ErrorMessage("서버를 이용하시기전 여기를 눌러 디스코드연동을 해주세요")).clickEvent(ClickEvent.openUrl("https://battlebot.kr/connect/minecraft?token=" + AccessToken));
                    player.sendMessage(textComponent);
                    countdown--;
                    if(countdown < 0) {
                        loginUsers.put(player.getUniqueId().toString(), false);
                        player.kick(Component.text(MessageFormat.ErrorMessage("서버를 이용하시기전 디스코드연동을 해주세요")), PlayerKickEvent.Cause.PLUGIN);
                    }
                } else {
                    for (int x = 0; x < 150; x++){
                        player.sendMessage("");
                    }
                    player.sendMessage(MessageFormat.SuccessMessage("디스코드 연동이 확인되었습니다!"));
                    loginUsers.put(player.getUniqueId().toString(), true);
                    this.cancel();
                }
            }
        }.runTaskTimer(Main.getPlugin(), 10 , 10);
    }

    public static Boolean LoginCheck(Player player) {
        Boolean isLogin = loginUsers.get(player.getUniqueId().toString());
        if(isLogin == null) {
            return false;
        }
        return isLogin;
    }

    public static void LogOut(Player player) {
        loginUsers.remove(player.getUniqueId().toString());
    }
}
