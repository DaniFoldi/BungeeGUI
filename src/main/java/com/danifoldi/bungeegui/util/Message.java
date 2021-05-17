package com.danifoldi.bungeegui.util;

import com.danifoldi.bungeegui.main.BungeeGuiAPI;
import com.electronwill.nightconfig.core.Config;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Message {

    PLAYER_ONLY("playerOnly", "&cOnly players can execute this command"),
    TARGET_REQUIRED("targetRequired", "&cThis command requires a target player"),
    NO_SELF_TARGET("noSelfTarget", "&cYou can't target yourself with this command"),
    SERVER_DISABLED("serverDisabled", "&cYou can't use this command on this server"),
    TARGET_BYPASS("targetBypass", "&cThis player can't be targeted with this command"),
    TARGET_NOT_FOUND("targetNotFound", "&cTarget {target} could not be found"),
    RELOAD_SUCCESS("reloadSuccess", "&bPlugin reloaded successfully in &l{time}ms"),
    COMMAND_HELP("commandHelp", "&7---- &6&l%bungeegui% help &7----"),
    COMMAND_RELOAD("commandReload", "/bungeegui reload &7- Reload the plugin"),
    COMMAND_GUIS("commandGuis", "/bungeegui guis &7- List the loaded GUIs"),
    COMMAND_BROADCAST("commandBroadcast", "/bungeegui broadcast all|s:<server>|p:<player> <message> &7- Send a message to one or more players"),
    COMMAND_LOG("commandLog", "/bungeegui log <message> &7- Log a message into the console"),
    COMMAND_SEND("commandSend", "/bungeegui send all|s:<server>|p:<player> <send> &7- Send a player to a server"),
    COMMAND_CHAT("commandChat", "/bungeegui chat <player> <message> &7- Send a message to chat as a player"),
    COMMAND_ACTIONBAR("commandActionbar", "/bungeegui actionbar all|s:<server>|p:<player> <actionbar> &7- Show a player a message in their action bar"),
    COMMAND_TITLE("commandTitle", "/bungeegui title all|s:<server>|p:<player> title|subtitle fadeIn stay fadeOut <message>"),
    COMMAND_SOUND("commandSound", "/bungeegui sound all|s:<server>|p:<player> <sound> [soundcategory] [volume] [pitch]"),
    COMMAND_OPEN("commandOpen", "/bungeegui open all|s:<server>|p:<player> <gui> [target]"),
    COMMAND_CLOSE("commandClose", "/bungeegui close <player>"),
    ACTION_COMPLETE("actionComplete", "&bAction completed for {count} players"),
    GUI_NOT_FOUND("guiNotFound", "&cGUI {name} not found"),
    GUI_TARGET_REQUIRED("guiTargetRequired", "&cThis GUI requires a target player"),
    INVALID_PROPERTY("invalidProperty", "&cA property is invalid"),
    SERVER_NOT_FOUND("serverNotFound", "&cServer {name} not found"),
    EMPTY_MESSAGE("emptyMessage", "&cMessage can't be empty"),
    NO_PERMISSION("noPermission", "&cYou don't have permission to execute that command"),
    GUI_LIST_TOP("guiListTop", "&6{count} GUIs are loaded:"),
    GUI_LIST_ITEM("guiListItem", "&7- &6&l{name}");

    private static Map<String, String> messages = new HashMap<>();
    public static void setMessageProvider(Config config) {
        final Map<String, String> messages = new HashMap<>();
        for (Config.Entry message: ((Config)config.get("messages")).entrySet()) {
            messages.put(message.getKey(), message.getValue());
        }

        Message.messages = messages;
    }

    private static final Pattern HEX = Pattern.compile("(&#[a-fA-F0-9]{6})");

    private final String messageId;
    private final String defaultValue;

    Message(String messageId, String defaultValue) {
        this.messageId = messageId;
        this.defaultValue = defaultValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    @SafeVarargs
    public static String replace(String text, Pair<String, String>... replacements) {
        String result = text;
        for (Pair<String, String> replacement: replacements) {
            result = result.replace("{" + replacement.getFirst() + "}", replacement.getSecond());
        }

        return result;
    }

    public static String colorCodes(String text) {
        String colorized = text;
        Matcher matcher = HEX.matcher(colorized);

        while (matcher.find()) {
            String color = matcher.group(1);
            String value = ChatColor.COLOR_CHAR + "x" + color.replace("&#", "").chars().mapToObj(i -> (char)i).map(String::valueOf).map(s -> ChatColor.COLOR_CHAR + s).collect(Collectors.joining());
            colorized = colorized.replace(color, value);
        }
        return ChatColor.translateAlternateColorCodes('&', colorized);
    }

    @SafeVarargs
    public final void send(ProxiedPlayer player, Pair<String, String>... replacements) {
        if (!Message.messages.containsKey(messageId) || Message.messages.get(messageId).equals("")) {
            send(player, defaultValue, replacements);
        }

        send(player, Message.messages.get(messageId), replacements);
    }

    @SafeVarargs
    public static void send(ProxiedPlayer player, String value, Pair<String, String>... replacements) {
        player.sendMessage(toComponent(player, value, replacements));
    }

    @SafeVarargs
    public final BaseComponent[] toComponent(ProxiedPlayer player, Pair<String, String>... replacements) {
        if (!Message.messages.containsKey(messageId) || Message.messages.get(messageId).equals("")) {
            toComponent(player, defaultValue, replacements);
        }

        return toComponent(player, Message.messages.get(messageId), replacements);
    }

    @SafeVarargs
    public static BaseComponent[] toComponent(ProxiedPlayer player, String value, Pair<String, String>... replacements) {
        return new BaseComponent[] {new TextComponent(colorCodes(BungeeGuiAPI.getInstance().parsePlaceholders(player, replace(value, replacements))))};
    }
}
