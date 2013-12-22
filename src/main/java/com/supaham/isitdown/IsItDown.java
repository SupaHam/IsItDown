package com.supaham.isitdown;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class IsItDown extends JavaPlugin {

    public static String PREFIX = ChatColor.GRAY + "[" + ChatColor.GOLD + "MCStatus" + ChatColor.GRAY + "] " +
            ChatColor.RESET;


    @Override
    public void onEnable() {

        this.saveDefaultConfig();
        new TestServices(this);
    }

    public Map<String, String> testServices() {

        URL url;
        try {
            url = new URL("https://status.mojang.com/check");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(1000);
            conn.setRequestMethod("GET");
            conn.setReadTimeout(1000);
            InputStream is = conn.getInputStream();
            if (is == null) {
                return Collections.emptyMap();
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            JSONParser parser = new JSONParser();
            try {
                JSONArray array = (JSONArray) parser.parse(br.readLine());
                Iterator<?> it = array.iterator();
                Map<String, String> services = new HashMap<>();
                while (it.hasNext()) {

                    Map<?, ?> json = (Map<?, ?>) parser.parse(it.next().toString());
                    Iterator<?> it2 = json.entrySet().iterator();

                    while (it2.hasNext()) {
                        Map.Entry<?, ?> entry = (Entry<?, ?>) it2.next();
                        String key = entry.getKey().toString();
                        String value = entry.getValue().toString();
                        services.put(key, value);
                    }
                }
                return services;
            } catch (ParseException e) {
                getLogger().severe("Error occurred while parsing input from URL: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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

        // We don't want to only get a default value, but to add it to the config if it doesn't exist.
        if (!this.getConfig().isSet("interval")) {
            this.getConfig().set("interval", 60);
        }
        return this.getConfig().getInt("interval");
    }
}
