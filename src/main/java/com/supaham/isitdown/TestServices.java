package com.supaham.isitdown;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;

public class TestServices implements Runnable {

    private IsItDown plugin;
    private List<String> downServices;

    public static final String SESSION_SERVER = "sessionserver.mojang.com";
    public static final String ACCOUNT = "account.mojang.com";

    public TestServices(IsItDown instance) {

        this.plugin = instance;
        downServices = new ArrayList<>();

        int interval = plugin.getSchedulerInterval() * 20;
        plugin.getServer().getScheduler().runTaskTimer(plugin, this, interval, interval);
    }

    @Override
    public void run() {

        Map<String, String> result = plugin.testServices();
        if (result != null) {
            String session = result.get(SESSION_SERVER);
            String account = result.get(ACCOUNT);

            // TODO use these later possibly
            // String website = result.get("minecraft.net");
            // String login = result.get("login.minecraft.net");
            // String auth = result.get("auth.mojang.com");
            // String skins = result.get("skins.minecraft.net");

            boolean recentlyDown = false;
            boolean recentlyUp = false;

            if (!session.equals("green")) {
                plugin.broadcast(IsItDown.PREFIX + ChatColor.RED
                        + "Minecraft sessions service is down!");
                if (!isRed(session)) {
                    recentlyDown = true;
                    addRedService(session);
                }
            } else if (isRed(session)) {
                removeRedService(session);
                recentlyUp = true;
            }

            if (!account.equals("green")) {
                plugin.broadcast(IsItDown.PREFIX + "Mojang's account service is down!");
                if (!isRed(account)) {
                    recentlyDown = true;
                    addRedService(account);
                }
            } else if (isRed(account)) {
                removeRedService(account);
                recentlyUp = true;
            }

            if (recentlyDown) {
                plugin.broadcast(IsItDown.PREFIX
                        + ChatColor.RED
                        + " Please do not close Minecraft or log out until you are notified the service(s) are up.");
            } else if (recentlyUp) {

                plugin.broadcast(IsItDown.PREFIX + ChatColor.GREEN
                        + "Hooray! Minecraft services are all green.");
                plugin.broadcast(IsItDown.PREFIX + ChatColor.GREEN
                        + "You may now close Minecraft.");
            }
        }
    }

    /**
     * Checks if the {@code serviceName} is red (Down).
     * 
     * @param serviceName name of the service to check
     * @return true if serviceName is on the list of red servers
     */
    public boolean isRed(String serviceName) {

        return this.downServices.contains(serviceName);
    }

    /**
     * Adds the {@code serviceName} to the list of the red servers.
     * 
     * @param serviceName name of the service to add
     */
    public void addRedService(String serviceName) {

        if (!downServices.contains(serviceName)) {
            downServices.add(serviceName);
        }
    }

    /**
     * Removes {@code serviceName} from the list of red (down) servers.
     * 
     * @param serviceName name of the service to remove
     * @return true if {@code serviceName} was removed, otherwise false
     */
    public boolean removeRedService(String serviceName) {

        return downServices.remove(downServices);
    }
}
