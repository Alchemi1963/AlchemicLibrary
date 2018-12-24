package com.alchemi.al;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Messenger{

	private final JavaPlugin plugin;
	private final FileManager fm;
	
	public Messenger(JavaPlugin plug, FileManager fileManager) {
		this.plugin = plug;
		this.fm = fileManager;
	}
	
	public String getMessage(String key) {
		
		String msg = this.fm.getConfig("messages.yml").getString(this.plugin.getDescription().getName() + "." + key);
		return msg;
		
	}
	
	public String getTag() {
		String msg = this.fm.getConfig("messages.yml").getString(this.plugin.getDescription().getName() + ".Tag");
		return msg;
	}
	
	public static String cc(String msg){
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
	
	public static String parseVars(String msg, Map<String, String> vals) {
		for (Entry<String, String> ent : vals.entrySet()) {
			
			while (msg.contains(ent.getKey())) msg = msg.replace(ent.getKey(), ent.getValue());	
		}
		
		return msg;
	}
	
	public void print(Object msg) { print(msg, true, new HashMap<String, String>()); }
	
	public void print(Object msg, boolean tag) { print(msg, tag, new HashMap<String, String>()); } 
	
	public void print(Object msg, Map<String, String> vals) {
		print(msg, true, vals);
	}
	
	public void print(Object msg, boolean tag, Map<String, String> vals) {
		if (String.valueOf(msg).contains("\n")) {
			for (String m : String.valueOf(msg).split("\n")) {
				print(m, tag, vals);
			}
			return;
		}
		
		if (tag) Bukkit.getConsoleSender().sendMessage(cc(getTag() + " " + parseVars(String.valueOf(msg), vals)));
		else Bukkit.getConsoleSender().sendMessage(cc(parseVars(String.valueOf(msg), vals)));
	}
		
	public void broadcast(String msg) {
		if (msg.contains("\n")) {
			for (String msg2 : msg.split("\n")) {
				broadcast(msg2);
			}
			return;
		}
		
		for (Player r : Library.instance.getServer().getOnlinePlayers()) {
			sendMsg(cc(getTag() + " " + msg), r);
		}
//		Bukkit.broadcastMessage(getTag() + " " + cc(msg));
		
	}
	
	public void broadcast(String msg, Map<String, String> vs) {
		broadcast(parseVars(msg, vs));
	}
	
	public static void sendMsg(String msg, CommandSender reciever){
		reciever.sendMessage(cc(msg));
	}
	
	public static void sendMsg(String msg, CommandSender reciever, Map<String, String> vars) {
		reciever.sendMessage(cc(parseVars(msg, vars)));
	}
	
	public void broadcastHover(String mainText, String hoverText, Map<String, String> vars) {
		
		mainText = colourMessage(mainText);
		
		if (mainText.contains("\n")) {
			for (String msg : mainText.split("\n")) {
				broadcastHover(msg, hoverText, vars);
			}
			return;
		}
		for (Player r : Library.instance.getServer().getOnlinePlayers()) {
			sendHoverMsg(r, getTag() + " " + mainText, hoverText, vars);
		}
	}
	
	public void broadcastHover(String mainText, String hoverText) {
		
		mainText = colourMessage(mainText);
		
		if (mainText.contains("\n")) {
			for (String msg : mainText.split("\n")) {
				broadcastHover(msg, hoverText);
			}
			return;
		}
		for (Player r : Library.instance.getServer().getOnlinePlayers()) {
			sendHoverMsg(r, getTag() + " " + mainText, hoverText);
		}
	}
	
	public static void sendHoverMsg(Player reciever, String mainText, String hoverText, Map<String, String> vars) {
		sendHoverMsg(reciever, parseVars(mainText, vars), parseVars(hoverText, vars));
		
	}
	
	public static void sendHoverMsg(Player reciever, String mainText, String hoverText) {
		
		if (hoverText.substring(0, 1).equals("\n")) hoverText = hoverText.replaceFirst("\n", "");
		
		mainText = colourMessage(mainText);
		
		TextComponent mainComponent = new TextComponent(cc(mainText));
		mainComponent.setHoverEvent(new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(cc(hoverText)).create() ));
		
		reciever.spigot().sendMessage(mainComponent);
	}
	
	public static String colourMessage(String message) { return colourMessage(message, "&9"); }
	
	public static String colourMessage(String message, String defaultCol) {
		
		String mod = defaultCol;
		String newText = "";
		
		for (String s : message.split(" ")) {
			
			if (s.contains("&")) {
				int i = s.indexOf("&") + 1;
				if (s.charAt(i) != ' ')	mod = "&" + s.charAt(i);
			}
			
			if (message.split(" ")[0].equals(s)) newText = s;
			else {
				
				if (s.contains("&")) newText += " " + s;
				else newText += " " + mod + s;
				
			}
			
			continue;
		}
		
		return newText;
		
	}
}