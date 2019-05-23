package com.alchemi.al.objects.GUI;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.alchemi.al.objects.handling.ItemFactory;
import com.alchemi.al.objects.handling.SexyRunnable;
import com.alchemi.al.objects.meta.GUIPageMeta;
import com.alchemi.al.objects.meta.PersistentMeta;

public abstract class GUIBase {
	
	protected String guiName = "";
	protected int guiSize = 0;
	protected static JavaPlugin plugin;
	protected final OfflinePlayer player;
	protected final CommandSender sender;
	protected Inventory gui;
	
	protected HashMap<Integer, ItemStack> contents = new HashMap<Integer, ItemStack>();
	protected HashMap<Integer, SexyRunnable> commands = new HashMap<Integer, SexyRunnable>();
	protected HashMap<Integer, Object[]> arguments = new HashMap<Integer, Object[]>();
	
	protected ItemStack nextPage = new ItemFactory(Material.REDSTONE_TORCH).setName("Next Page");
	protected ItemStack prevPage = new ItemFactory(Material.LEVER).setName("Previous Page");
	
	public GUIBase(JavaPlugin plug, String name, int size, OfflinePlayer player, CommandSender sender) {
		GUIBase.plugin = plug;
		guiName = name;
		guiSize = size;
		gui = Bukkit.createInventory(null, guiSize, guiName);
		this.sender = sender;
		this.player = player;
	}
	
	public void openGUI(Player pl) {
		
		int page = 0;
		if (!PersistentMeta.hasMeta(pl, GUIPageMeta.class)) pl.setMetadata(GUIPageMeta.class.getSimpleName(), new GUIPageMeta(GUIBase.plugin, page));
		else page = PersistentMeta.getMeta(pl, GUIPageMeta.class).asInt();
		
		TreeMap<Integer, ItemStack> mapped = new TreeMap<>(contents);
		
		for (Entry<Integer, ItemStack> ent : mapped.entrySet()) {
			if (mapped.lastKey() > guiSize - 1) {
				int pageMax = mapped.lastKey()/(guiSize-9) > Integer.valueOf(Float.valueOf(mapped.lastKey()/(guiSize-9)).toString().replaceAll("\\..*", "")) ? Integer.valueOf(Float.valueOf(mapped.lastKey()/(guiSize-9)).toString().replaceAll("\\..*", "")) + 1 : Integer.valueOf(Float.valueOf(mapped.lastKey()/(guiSize-9)).toString().replaceAll("\\..*", "")); 
				
				if (ent.getKey() <= guiSize - 9 && page == 0) gui.setItem(ent.getKey(), ent.getValue());
				else if (page >= 1) {
					
					int newPlace = ent.getKey() - page * (guiSize - 9);
					if (newPlace >= 0 && newPlace < guiSize - 9) {
						gui.setItem(newPlace, ent.getValue());
					}
				}
				
				if (page != 0) gui.setItem(guiSize - 9, prevPage);
				if (page != pageMax) gui.setItem(guiSize - 1, nextPage);
				
			} else {
				gui.setItem(ent.getKey(), ent.getValue());
			}
		}
		
		pl.openInventory(gui);
	}
	
	public void openGUI(CommandSender sender, Player player) {
		
		if (!(sender instanceof Player)) return;
		
		int page = 0;
		if (!PersistentMeta.hasMeta((Player) sender, GUIPageMeta.class)) ((Player) sender).setMetadata(GUIPageMeta.class.getSimpleName(), new GUIPageMeta(GUIBase.plugin, page));
		else page = PersistentMeta.getMeta((Player) sender, GUIPageMeta.class).asInt();
		
		TreeMap<Integer, ItemStack> mapped = new TreeMap<>(contents);
		
		for (Entry<Integer, ItemStack> ent : mapped.entrySet()) {
			if (mapped.lastKey() > guiSize - 1) {
				int pageMax = mapped.lastKey()/(guiSize-9) > Integer.valueOf(Float.valueOf(mapped.lastKey()/(guiSize-9)).toString().replaceAll("\\..*", "")) ? Integer.valueOf(Float.valueOf(mapped.lastKey()/(guiSize-9)).toString().replaceAll("\\..*", "")) + 1 : Integer.valueOf(Float.valueOf(mapped.lastKey()/(guiSize-9)).toString().replaceAll("\\..*", "")); 
				
				if (ent.getKey() < guiSize - 9 && page == 0) gui.setItem(ent.getKey(), ent.getValue());
				else if (page >= 1) {
					
					int newPlace = ent.getKey() - page * (guiSize - 9);
					if (newPlace >= 0 && newPlace < guiSize - 9) {
						gui.setItem(newPlace, ent.getValue());
					}
				}
				
				if (page != 0) gui.setItem(guiSize - 9, prevPage);
				if (page != pageMax) gui.setItem(guiSize - 1, nextPage);
				
			} else {
				gui.setItem(ent.getKey(), ent.getValue());
			}
		}
		
		((HumanEntity) sender).openInventory(gui);
		
	}
	
	public void onClicked(int slot, Player pl, ClickType click) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		
		if (nextPage.isSimilar(gui.getItem(slot))) {
			pageNext.run(pl);
			return;
		} else if (prevPage.isSimilar(gui.getItem(slot))) {
			pagePrev.run(pl);
			return;
		}
		
		if (!contents.containsKey(slot) || !commands.containsKey(slot)) pl.playSound(pl.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
		
		else {
			
			SexyRunnable m = commands.get(slot);
			if (arguments.containsKey(slot)) {
				Object[] args = arguments.get(slot);
				
				int i = 0;
				for (Object arg : arguments.get(slot)) {
					if (arg.equals("<player>")) {
						args[i] = pl;
					}
					i++;
				}
				
				m.run(args);
			}
			else m.run();
			
		}
	}
	
	public abstract void setContents();
	public abstract void setCommands();
	public abstract void onClose();
	
	public String getGuiName() {
		return guiName;
	}
	
	public int getGuiSize() {
		return guiSize;
	}
	
	public Inventory getGui() {
		return gui;
	}
	
	
	
	/**
	 * @return the player
	 */
	public OfflinePlayer getPlayer() {
		return player;
	}

	/**
	 * @return the sender
	 */
	public CommandSender getSender() {
		return sender;
	}

	protected void putArgument(Integer key, Object...args) {
		arguments.put(key, args);
	}

	SexyRunnable pageNext = new SexyRunnable() { 
		
		@Override
		public void run(Object... args) {
			// player
			gui.clear();
			int page = PersistentMeta.getMeta((Player) args[0], GUIPageMeta.class).asInt();
			page ++;
			((Player) args[0]).setMetadata(GUIPageMeta.class.getSimpleName(), new GUIPageMeta(GUIBase.plugin, page));
			openGUI((Player) args[0]);
		}
	};
	 
	SexyRunnable pagePrev = new SexyRunnable() {
		
		@Override
		public void run(Object... args) {
			// player
			gui.clear();
			int page = PersistentMeta.getMeta((Player) args[0], GUIPageMeta.class).asInt();
			page --;
			((Player) args[0]).setMetadata(GUIPageMeta.class.getSimpleName(), new GUIPageMeta(GUIBase.plugin, page));
			openGUI((Player) args[0]);
		}
	};
}