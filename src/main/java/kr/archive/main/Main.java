package kr.archive.main;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import kr.archive.main.commands.MoneyCommand;
import kr.archive.main.commands.ShopCommand;
import kr.archive.main.database.User;
import kr.archive.main.events.LoginEvent;
import kr.archive.main.listener.BlockListener;
import kr.archive.main.listener.EntityListener;
import kr.archive.main.listener.PlayerListener;
import kr.archive.main.listener.PlayerShopListener;
import kr.archive.main.utils.MessageFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

import static kr.archive.main.events.LoginEvent.loginUsers;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static com.mongodb.client.model.Filters.eq;

public final class Main extends JavaPlugin {
    public static MongoClient mongoClient;
    public static MongoDatabase mongoDatabase;
    private static Logger logger = Logger.getLogger("Archive Users");
    @Override
    public void onEnable() {
        saveDefaultConfig();
        connectDataBase();
        commandLoader();
        eventLoader();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void connectDataBase() {
        System.setProperty("DEBUG.GO", "true");
        System.setProperty("DB.TRACE", "true");
        Logger mongodbLogger = Logger.getLogger("org.mongodb.driver");
        mongodbLogger.setLevel(Level.WARNING);
        String mongodbURI = this.getConfig().getString("database.url");
        if (mongodbURI == null) {
            Bukkit.getLogger().warning("몽고디비 주소를 입력해주세요: config.yml");
            return;
        }
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        mongoClient = MongoClients.create(
                MongoClientSettings.builder()
                        .codecRegistry(pojoCodecRegistry)
                        .applyConnectionString(new ConnectionString(mongodbURI))
                        .uuidRepresentation(UuidRepresentation.JAVA_LEGACY)

                        .build());
        mongoDatabase = mongoClient.getDatabase("battlebot");
        LoginUserCheck(mongoDatabase);
    }

    public static Plugin getPlugin() {
        return Main.getPlugin(Main.class);
    }

    public void commandLoader() {
        getCommand("돈").setExecutor(new MoneyCommand());
        getCommand("상점").setExecutor(new ShopCommand());
    }

    public void eventLoader() {
        getServer().getPluginManager().registerEvents(new LoginEvent(), this);
        getServer().getPluginManager().registerEvents(new BlockListener(), this);
        getServer().getPluginManager().registerEvents(new EntityListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerShopListener(), this);
    }

    public static void LoginUserCheck(MongoDatabase mongoDatabase) {
        MongoCollection<User> userData = mongoDatabase.getCollection("userData", User.class);
        for (Player player: Bukkit.getOnlinePlayers()) {
            User user = userData.find(eq("minecraft_id", player.getUniqueId().toString())).first();
            if(user == null) {
                loginUsers.put(player.getUniqueId().toString(), false);
                player.sendMessage(Component.text(MessageFormat.SuccessMessage(player.getName() + "님 성공적으로 로그인되었습니다")));
                Component textComponent = Component.text(MessageFormat.ErrorMessage("서버를 이용하시기전 여기를 눌러 디스코드연동을 해주세요")).clickEvent(ClickEvent.openUrl("https://discord.gg/pSG6tSxxS2"));
                player.sendMessage(textComponent);
            } else {
                player.sendMessage(Component.text(MessageFormat.SuccessMessage(player.getName() + "님 성공적으로 로그인되었습니다")));
                loginUsers.put(player.getUniqueId().toString(), true);
            }
        }
        logger.info("유저정보 설정완료");
    }

}
