package io.github.jeremyhu.fishapi;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

import static io.github.jeremyhu.fishapi.LoadFile.getPlayers;


class MainThread extends Thread{
    private JavaPlugin plugin;
    private HttpServer server;
    private int port;

    public HttpServer getServer(){
        return server;
    }

    public MainThread(JavaPlugin plugin,int port){
        super();
        this.plugin = plugin;
        this.port = port;
    }

    @Override
    public void run(){
        plugin.getLogger().info("Started the HTTP server!");
        try{
            server = HttpServer.create(new InetSocketAddress(port),0);
            server.createContext("/server/login",new LoginHandler());
            server.createContext("/server/register",new RegHandler());
            server.createContext("/server/api/online",new APIHandler(1));
            server.start();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    class LoginHandler implements HttpHandler{
        public void handle(HttpExchange e){
            File file = new File(plugin.getDataFolder(),"players.yml");
            FileConfiguration players = LoadFile.getPlayers(file);
            JoinHandler jh = new JoinHandler(e,players);
            jh.getQuery();
            jh.response();
            jh.handle();
            try {
                players.save(file);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    class RegHandler implements HttpHandler{
        public void handle(HttpExchange e){
            File file = new File(plugin.getDataFolder(),"players.yml");
            FileConfiguration players = LoadFile.getPlayers(file);
            RegisterHandler rh = new RegisterHandler(e,players);
            rh.getQuery();
            rh.register();
            rh.response();
            try {
                players.save(file);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    class APIHandler implements HttpHandler{
        private int type;
        public APIHandler(int type){
            this.type = type;
        }

        public void handle(HttpExchange e){
            File file = new File(plugin.getDataFolder(),"players.yml");
            FileConfiguration players = LoadFile.getPlayers(file);
            io.github.jeremyhu.fishapi.APIHandler ah = new io.github.jeremyhu.fishapi.APIHandler(e,players,type);
            ah.response();
        }
    }

}

    public final class Main extends JavaPlugin {
        private MainThread t;
        @Override
        public void onEnable(){
            saveDefaultConfig();
            File file = new File(getDataFolder(),"players.yml");
            FileConfiguration players = getPlayers(file);
            Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this),this);
            FileConfiguration config = this.getConfig();
            int port = config.getInt("port");
            t = new MainThread(this,port);
            t.start();
            this.getLogger().info(String.valueOf(players.isSet("players")));
        }


    public void onDisable(){
        this.getLogger().info("Stopped the HTTP server!");
        t.getServer().stop(2);
    }
}
