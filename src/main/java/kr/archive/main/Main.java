package kr.archive.main;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import kr.archive.main.commands.MoneyCommand;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public final class Main extends JavaPlugin {
    public static MongoClient mongoClient;
    public static MongoDatabase mongoDatabase;
    @Override
    public void onEnable() {
        saveDefaultConfig();
        connectDataBase();
        getCommand("돈").setExecutor(new MoneyCommand());
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

    }
}
