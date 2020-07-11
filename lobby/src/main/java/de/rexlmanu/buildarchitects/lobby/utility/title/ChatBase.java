/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.buildarchitects.lobby.utility.title;

import org.bukkit.plugin.java.*;
import org.bukkit.entity.*;
import java.lang.reflect.*;
import org.bukkit.scheduler.*;
import org.bukkit.plugin.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.event.player.*;
import org.bukkit.event.*;

public class ChatBase {

    private static boolean useOldMethods;
    public static String nmsver;

    static {
        nmsver = Bukkit.getServer().getClass().getPackage().getName();
        nmsver = nmsver.substring(nmsver.lastIndexOf(".") + 1);
        if (nmsver.equalsIgnoreCase("v1_8_R1") || nmsver.equalsIgnoreCase("v1_7_")) {
            useOldMethods = true;
        }
    }

    @Deprecated
    public static void sendTitle(final Player player, final Integer fadeIn, final Integer stay, final Integer fadeOut, final String message) {
        sendTitle(player, fadeIn, stay, fadeOut, message, null);
    }

    @Deprecated
    public static void sendSubtitle(final Player player, final Integer fadeIn, final Integer stay, final Integer fadeOut, final String message) {
        sendTitle(player, fadeIn, stay, fadeOut, null, message);
    }

    @Deprecated
    public static void sendFullTitle(final Player player, final Integer fadeIn, final Integer stay, final Integer fadeOut, final String title, final String subtitle) {
        sendTitle(player, fadeIn, stay, fadeOut, title, subtitle);
    }

    @Deprecated
    public static Integer getPlayerProtocol(final Player player) {
        return 47;
    }

    public static void sendPacket(final Player player, final Object packet) {
        try {
            final Object handle = player.getClass().getMethod("getHandle", (Class<?>[])new Class[0]).invoke(player, new Object[0]);
            final Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Class<?> getNMSClass(final String name) {
        final String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void sendTitle(final Player player, final Integer fadeIn, final Integer stay, final Integer fadeOut, String title, String subtitle) {
        try {
            if (title != null) {
                title = ChatColor.translateAlternateColorCodes('&', title);
                title = title.replaceAll("%player%", player.getDisplayName());
                Object e = Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")))))).getDeclaredClasses()[0].getField("TIMES").get(null);
                Object chatTitle = Objects.requireNonNull(getNMSClass("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + title + "\"}");
                Constructor<?> subtitleConstructor = Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getConstructor(Objects.requireNonNull(Objects.requireNonNull(getNMSClass("PacketPlayOutTitle"))).getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE);
                Object titlePacket = subtitleConstructor.newInstance(e, chatTitle, fadeIn, stay, fadeOut);
                sendPacket(player, titlePacket);
                e = Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getDeclaredClasses()[0].getField("TITLE").get(null);
                chatTitle = Objects.requireNonNull(getNMSClass("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + title + "\"}");
                subtitleConstructor = Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getConstructor(Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"));
                titlePacket = subtitleConstructor.newInstance(e, chatTitle);
                sendPacket(player, titlePacket);
            }
            if (subtitle != null) {
                subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
                subtitle = subtitle.replaceAll("%player%", player.getDisplayName());
                Object e = Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getDeclaredClasses()[0].getField("TIMES").get(null);
                Object chatSubtitle = Objects.requireNonNull(getNMSClass("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + title + "\"}");
                Constructor<?> subtitleConstructor = Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getConstructor(Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE);
                Object subtitlePacket = subtitleConstructor.newInstance(e, chatSubtitle, fadeIn, stay, fadeOut);
                sendPacket(player, subtitlePacket);
                e = Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getDeclaredClasses()[0].getField("SUBTITLE").get(null);
                chatSubtitle = Objects.requireNonNull(getNMSClass("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + subtitle + "\"}");
                subtitleConstructor = Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getConstructor(Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE);
                subtitlePacket = subtitleConstructor.newInstance(e, chatSubtitle, fadeIn, stay, fadeOut);
                sendPacket(player, subtitlePacket);
            }
        }
        catch (Exception var11) {
            var11.printStackTrace();
        }
    }

    public static void clearTitle(final Player player) {
        sendTitle(player, 0, 0, 0, "", "");
    }

    public static void sendTabTitle(final Player player, String header, String footer) {
        if (header == null) {
            header = "";
        }
        header = ChatColor.translateAlternateColorCodes('&', header);
        if (footer == null) {
            footer = "";
        }
        footer = ChatColor.translateAlternateColorCodes('&', footer);
        header = header.replaceAll("%player%", player.getDisplayName());
        footer = footer.replaceAll("%player%", player.getDisplayName());
        try {
            final Object tabHeader = Objects.requireNonNull(getNMSClass("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + header + "\"}");
            final Object tabFooter = Objects.requireNonNull(getNMSClass("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + footer + "\"}");
            final Constructor<?> titleConstructor = Objects.requireNonNull(getNMSClass("PacketPlayOutPlayerListHeaderFooter")).getConstructor((Class<?>[])new Class[0]);
            final Object packet = titleConstructor.newInstance(new Object[0]);
            try {
                final Field aField = packet.getClass().getDeclaredField("a");
                aField.setAccessible(true);
                aField.set(packet, tabHeader);
                final Field bField = packet.getClass().getDeclaredField("b");
                bField.setAccessible(true);
                bField.set(packet, tabFooter);
            }
            catch (Exception e) {
                final Field aField2 = packet.getClass().getDeclaredField("header");
                aField2.setAccessible(true);
                aField2.set(packet, tabHeader);
                final Field bField2 = packet.getClass().getDeclaredField("footer");
                bField2.setAccessible(true);
                bField2.set(packet, tabFooter);
            }
            sendPacket(player, packet);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void sendActionBar(final Player player, final String message) {
        if (!player.isOnline()) {
            return;
        }
        try {
            final Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + nmsver + ".entity.CraftPlayer");
            final Object craftPlayer = craftPlayerClass.cast(player);
            final Class<?> packetPlayOutChatClass = Class.forName("net.minecraft.server." + nmsver + ".PacketPlayOutChat");
            final Class<?> packetClass = Class.forName("net.minecraft.server." + nmsver + ".Packet");
            Object packet;
            if (useOldMethods) {
                final Class<?> chatSerializerClass = Class.forName("net.minecraft.server." + nmsver + ".ChatSerializer");
                final Class<?> iChatBaseComponentClass = Class.forName("net.minecraft.server." + nmsver + ".IChatBaseComponent");
                final Method m3 = chatSerializerClass.getDeclaredMethod("a", String.class);
                final Object cbc = iChatBaseComponentClass.cast(m3.invoke(chatSerializerClass, "{\"text\": \"" + message + "\"}"));
                packet = packetPlayOutChatClass.getConstructor(iChatBaseComponentClass, Byte.TYPE).newInstance(cbc, 2);
            }
            else {
                final Class<?> chatComponentTextClass = Class.forName("net.minecraft.server." + nmsver + ".ChatComponentText");
                final Class<?> iChatBaseComponentClass = Class.forName("net.minecraft.server." + nmsver + ".IChatBaseComponent");
                try {
                    final Class<?> chatMessageTypeClass = Class.forName("net.minecraft.server." + nmsver + ".ChatMessageType");
                    final Object[] chatMessageTypes = (Object[])chatMessageTypeClass.getEnumConstants();
                    Object chatMessageType = null;
                    Object[] array;
                    for (int length = (array = chatMessageTypes).length, i = 0; i < length; ++i) {
                        final Object obj = array[i];
                        if (obj.toString().equals("GAME_INFO")) {
                            chatMessageType = obj;
                        }
                    }
                    final Object chatCompontentText = chatComponentTextClass.getConstructor(String.class).newInstance(message);
                    packet = packetPlayOutChatClass.getConstructor(iChatBaseComponentClass, chatMessageTypeClass).newInstance(chatCompontentText, chatMessageType);
                }
                catch (ClassNotFoundException cnfe) {
                    final Object chatCompontentText2 = chatComponentTextClass.getConstructor(String.class).newInstance(message);
                    packet = packetPlayOutChatClass.getConstructor(iChatBaseComponentClass, Byte.TYPE).newInstance(chatCompontentText2, 2);
                }
            }
            final Method craftPlayerHandleMethod = craftPlayerClass.getDeclaredMethod("getHandle", (Class<?>[])new Class[0]);
            final Object craftPlayerHandle = craftPlayerHandleMethod.invoke(craftPlayer, new Object[0]);
            final Field playerConnectionField = craftPlayerHandle.getClass().getDeclaredField("playerConnection");
            final Object playerConnection = playerConnectionField.get(craftPlayerHandle);
            final Method sendPacketMethod = playerConnection.getClass().getDeclaredMethod("sendPacket", packetClass);
            sendPacketMethod.invoke(playerConnection, packet);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendActionBarPost112(final Player player, final String message) {
        if (!player.isOnline()) {
            return;
        }
        try {
            final Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + nmsver + ".entity.CraftPlayer");
            final Object craftPlayer = craftPlayerClass.cast(player);
            final Class<?> c4 = Class.forName("net.minecraft.server." + nmsver + ".PacketPlayOutChat");
            final Class<?> c5 = Class.forName("net.minecraft.server." + nmsver + ".Packet");
            final Class<?> c6 = Class.forName("net.minecraft.server." + nmsver + ".ChatComponentText");
            final Class<?> c7 = Class.forName("net.minecraft.server." + nmsver + ".IChatBaseComponent");
            final Class<?> chatMessageTypeClass = Class.forName("net.minecraft.server." + nmsver + ".ChatMessageType");
            final Object[] chatMessageTypes = (Object[])chatMessageTypeClass.getEnumConstants();
            Object chatMessageType = null;
            Object[] array;
            for (int length = (array = chatMessageTypes).length, i = 0; i < length; ++i) {
                final Object obj = array[i];
                if (obj.toString().equals("GAME_INFO")) {
                    chatMessageType = obj;
                }
            }
            final Object o = c6.getConstructor(String.class).newInstance(message);
            final Object ppoc = c4.getConstructor(c7, chatMessageTypeClass).newInstance(o, chatMessageType);
            final Method m1 = craftPlayerClass.getDeclaredMethod("getHandle", (Class<?>[])new Class[0]);
            final Object h = m1.invoke(craftPlayer, new Object[0]);
            final Field f1 = h.getClass().getDeclaredField("playerConnection");
            final Object pc = f1.get(h);
            final Method m2 = pc.getClass().getDeclaredMethod("sendPacket", c5);
            m2.invoke(pc, ppoc);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void sendActionBarPre112(final Player player, final String message) {
        if (!player.isOnline()) {
            return;
        }
        try {
            final Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + nmsver + ".entity.CraftPlayer");
            final Object craftPlayer = craftPlayerClass.cast(player);
            final Class<?> c4 = Class.forName("net.minecraft.server." + nmsver + ".PacketPlayOutChat");
            final Class<?> c5 = Class.forName("net.minecraft.server." + nmsver + ".Packet");
            Object ppoc;
            if (useOldMethods) {
                final Class<?> c6 = Class.forName("net.minecraft.server." + nmsver + ".ChatSerializer");
                final Class<?> c7 = Class.forName("net.minecraft.server." + nmsver + ".IChatBaseComponent");
                final Method m3 = c6.getDeclaredMethod("a", String.class);
                final Object cbc = c7.cast(m3.invoke(c6, "{\"text\": \"" + message + "\"}"));
                ppoc = c4.getConstructor(c7, Byte.TYPE).newInstance(cbc, 2);
            }
            else {
                final Class<?> c6 = Class.forName("net.minecraft.server." + nmsver + ".ChatComponentText");
                final Class<?> c7 = Class.forName("net.minecraft.server." + nmsver + ".IChatBaseComponent");
                final Object o = c6.getConstructor(String.class).newInstance(message);
                ppoc = c4.getConstructor(c7, Byte.TYPE).newInstance(o, 2);
            }
            final Method m4 = craftPlayerClass.getDeclaredMethod("getHandle", (Class<?>[])new Class[0]);
            final Object h = m4.invoke(craftPlayer, new Object[0]);
            final Field f1 = h.getClass().getDeclaredField("playerConnection");
            final Object pc = f1.get(h);
            final Method m5 = pc.getClass().getDeclaredMethod("sendPacket", c5);
            m5.invoke(pc, ppoc);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
