package io.github.jeremyhu.fishapi;

import com.sun.net.httpserver.HttpExchange;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

public class RegisterHandler{
        private HttpExchange e;
        private HashMap<String,String> query;
        private FileConfiguration players;

        public RegisterHandler(HttpExchange e,FileConfiguration players){
            this.players = players;
            this.e = e;
        }

        public void response(){
            String response = "reg=" + String.valueOf(getState());
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
            return players.isSet(query.get("username") + ".password");
        }

        public void register(){
            if(!getState()){
                players.set(query.get("username") + ".password",query.get("password"));
            }
        }

}
