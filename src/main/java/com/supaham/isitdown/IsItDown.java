package com.supaham.isitdown;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class IsItDown extends JavaPlugin {

    private static String PREFIX = ChatColor.GRAY + "[" + ChatColor.GOLD + "MCStatus"
            + ChatColor.GRAY + "] " + ChatColor.RESET;
    private boolean lastCheckWasDown = false;

    @Override
    public void onEnable() {

        this.saveDefaultConfig();
        int interval = getSchedulerInterval() * 20;
        new BukkitRunnable() {

            public void run() {

                URL url;
                try {
                    url = new URL("https://status.mojang.com/");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(1000);
                    conn.setRequestMethod("GET");
                    conn.setReadTimeout(1000);
                    InputStream is = conn.getInputStream();
                    if (is == null) {
                        return;
                    }
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String output = br.readLine();
                    if (!output.equalsIgnoreCase("OK")) {
                        broadcast(PREFIX
                                + "Servers are down! Please do not log out until you are notified the servers are up.");
                        lastCheckWasDown = true;
                    } else if (lastCheckWasDown) {
                        broadcast(PREFIX + ChatColor.GREEN + "Servers are now up!");
                        lastCheckWasDown = false;
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskTimer(this, interval, interval);
    }

    /**
     * Loads the config file from the jar file.
     */
    public void loadConfig() {

        this.saveDefaultConfig();
        this.reloadConfig();
    }

    /**
     * Broadcasts a message to all players and console.
     * 
     * @param message message to broadcast
     */
    public void broadcast(String message) {

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
        }
        getLogger().info(message);
    }

    /**
     * Gets the interval in seconds for the scheduler that broadcasts a message if the
     * server is down.
     * 
     * @return interval in seconds
     */
    public int getSchedulerInterval() {

        if (!this.getConfig().isSet("interval")) {
            this.getConfig().set("interval", 60);
        }
        return this.getConfig().getInt("interval");
    }
}
