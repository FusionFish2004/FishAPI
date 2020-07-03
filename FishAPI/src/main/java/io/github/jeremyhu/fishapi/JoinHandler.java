package io.github.jeremyhu.fishapi;

import com.sun.net.httpserver.HttpExchange;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;

public class JoinHandler {
    private HttpExchange e;
    private HashMap<String,String> query;
    private FileConfiguration players;

    public JoinHandler(HttpExchange e,FileConfiguration players){
        this.players = players;
        this.e = e;
    }

    public void response(){
        String response = "login=" + getState();
        try {
            e.sendResponseHeaders(200,response.getBytes().length);
            OutputStream body = e.getResponseBody();
            body.write(response.getBytes());
            body.flush();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        e.close();
    }

    public void getQuery(){
        String query = e.getRequestURI().getQuery();
        String[] args = query.split("&");
        HashMap<String,String> map = new HashMap<String, String>();
        for(String str : args){
            String[] args2 = str.split("=");
            map.put(args2[0],args2[1]);
        }
        this.query = map;
    }

    private boolean getState(){
        String querypass = query.get("password");
        String serverpass = players.getString(query.get("username") + ".password");
        return querypass.equalsIgnoreCase(serverpass);
    }

    public void handle(){
        long timenow = new Date().getTime();
        Bukkit.getLogger().info("player " + query.get("username") + " tried to login from the client.");
        if(getState()){
            Bukkit.getLogger().info("player " + query.get("username") + " logged in.");
            Bukkit.getLogger().info("the login info of " + query.get("username") + "will expire in 10 minutes.");
            players.set(query.get("username") + ".querytime",timenow);
        }
    }
}
