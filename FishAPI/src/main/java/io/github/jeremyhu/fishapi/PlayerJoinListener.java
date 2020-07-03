package io.github.jeremyhu.fishapi;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Date;

public class PlayerJoinListener implements Listener {
    private JavaPlugin plugin;

    public PlayerJoinListener(JavaPlugin plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        File file = new File(this.plugin.getDataFolder(),"players.yml");
        FileConfiguration players = LoadFile.getPlayers(file);
        switch(getPlayerState(e.getPlayer(),players)){
            case -1:
                e.getPlayer().kickPlayer("登录已过期！");
            case 0:
                e.getPlayer().kickPlayer("请在客户端注册账号！");
            case 1:
                e.getPlayer().sendMessage("登陆成功！");
                long time = players.getLong(e.getPlayer().getName() + ".querytime");
                long timenow = new Date().getTime();
                int timediff = Math.round((timenow - time)/(1000*60));
                plugin.getLogger().info("the login info of " + e.getPlayer().getName() + " will expire in " + String.valueOf(timediff) + " minutes.");
        }
    }

    private int getPlayerState(Player player, FileConfiguration players){
        if(players.isSet(player.getName())){
            long time = players.getLong(player.getName() + ".querytime");
            long timenow = new Date().getTime();
            long timediff = timenow - time;
            if(timediff / (1000*60)<10){
                return 1;
            }
            return -1;
        }
        return 0;
    }
}
