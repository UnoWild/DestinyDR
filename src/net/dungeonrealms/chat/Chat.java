package net.dungeonrealms.chat;

import net.dungeonrealms.guild.Guild;
import net.dungeonrealms.mongo.DatabaseAPI;
import net.dungeonrealms.mongo.EnumData;
import net.dungeonrealms.mongo.EnumGuildData;
import net.dungeonrealms.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by Nick on 9/26/2015.
 */
public class Chat {

    static Chat instance = null;

    public static Chat getInstance() {
        if (instance == null) {
            instance = new Chat();
        }
        return instance;
    }

    List<String> TERRIBLE_WORDS = Arrays.asList("shit", "nigger", "wynncraft", "dungeonrealms.us", "myspace.com");

    /**
     * Monitor the players primary language also check for bad words.
     *
     * @param event
     * @since 1.0
     */
    public void doChat(AsyncPlayerChatEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        StringBuilder prefix = new StringBuilder();
        Rank.RankBlob r = Rank.getInstance().getRank(uuid);
        if (r != null && !r.getPrefix().equals("null")) {
            prefix.append(ChatColor.translateAlternateColorCodes('&', "[" + r.getPrefix() + ChatColor.RESET + "]"));
        }

        if (Guild.getInstance().isGuildNull(uuid)) {
            String clanTag = (String) DatabaseAPI.getInstance().getData(EnumGuildData.CLAN_TAG, (String) DatabaseAPI.getInstance().getData(EnumData.GUILD, uuid));
            prefix.append(ChatColor.translateAlternateColorCodes('&', " (" + clanTag + ChatColor.RESET + ")"));
        }
        event.setFormat(prefix.toString() + " " + event.getPlayer().getName() + ChatColor.GRAY + ": " + event.getMessage());
    }

}
