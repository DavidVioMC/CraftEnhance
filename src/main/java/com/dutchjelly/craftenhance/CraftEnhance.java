package com.dutchjelly.craftenhance;

import java.util.Arrays;

import com.dutchjelly.craftenhance.commands.*;
import com.dutchjelly.craftenhance.updatechecking.VersionChecker;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.dutchjelly.craftenhance.commandhandling.CustomCmdHandler;
import com.dutchjelly.craftenhance.crafthandling.RecipeInjector;
import com.dutchjelly.craftenhance.crafthandling.RecipeLoader;
import com.dutchjelly.craftenhance.data.ConfigFormatter;
import com.dutchjelly.craftenhance.data.FileManager;
import com.dutchjelly.craftenhance.gui.GUIContainer;
import com.dutchjelly.craftenhance.messaging.Debug;
import com.dutchjelly.craftenhance.messaging.Messenger;
import com.dutchjelly.craftenhance.util.GUIButtons;
import com.dutchjelly.craftenhance.util.CraftRecipe;
import com.dutchjelly.itemcreation.commands.DisplayNameCmd;
import com.dutchjelly.itemcreation.commands.DurabilityCmd;
import com.dutchjelly.itemcreation.commands.EnchantCmd;
import com.dutchjelly.itemcreation.commands.ItemFlagCmd;
import com.dutchjelly.itemcreation.commands.LocalizedNameCmd;
import com.dutchjelly.itemcreation.commands.LoreCmd;

public class CraftEnhance extends JavaPlugin{

	//TODO Try to add categories.
	//TODO Clean up redundant setcancelled in GUIs onclick event.
	//TODO Fix the bug where you can't put the recipe in the left top.

	
	private FileManager fm;
	private RecipeLoader loader;
	private GUIContainer guiContainer;
	private RecipeInjector injector;
	private CustomCmdHandler commandHandler;
	private Messenger messenger;
	
	@Override
	public void onEnable(){
		
		//The filemanager needs serialization, so firstly register the classes.
		registerSerialization();

		saveDefaultConfig();
		Debug.init(this);
		//Most other instances use the filemanager, so setup before everything.
		setupFileManager();
		
		GUIButtons.init();
		ConfigFormatter.init(this).formatConfigMessages();
		createInstances();

		loader.loadRecipes();
		setupListeners();
		setupCommands();

		getMessenger().message("CraftEnhance is managed and developed by DutchJelly.");
		getMessenger().message("If you find a bug in the plugin, please report it to https://dev.bukkit.org/projects/craftenhance.");
		VersionChecker.init(this).runVersionCheck();
        loader.disable(new ItemStack(Material.DIAMOND_SWORD));
	}
	
	@Override
	public void onDisable(){
		guiContainer.closeAll();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		commandHandler.handleCommand(sender, label, args);
		return true;
	}
	
	//Registers the classes that extend ConfigurationSerializable.
	private void registerSerialization(){
		ConfigurationSerialization.registerClass(CraftRecipe.class, "Recipe");
	}
	
	//Create basic instances where order doesn't matter.
	private void createInstances(){
		loader = new RecipeLoader(this);
		guiContainer = new GUIContainer(this);
		injector = new RecipeInjector(fm);
		messenger = new Messenger(this);
	}
	
	//Assigns executor classes for the commands.
	private void setupCommands(){
		commandHandler = new CustomCmdHandler(this);
		//All commands with the base /edititem
		commandHandler.loadCommandClasses(Arrays.asList(new DisplayNameCmd(commandHandler), new DurabilityCmd(commandHandler),
				new EnchantCmd(commandHandler), new ItemFlagCmd(commandHandler), new LocalizedNameCmd(commandHandler), 
				new LoreCmd(commandHandler)));
		//All command with the base /ceh
		commandHandler.loadCommandClasses(Arrays.asList(new CreateRecipeCmd(commandHandler), new OrderRecipesCmd(commandHandler),
				new RecipesCmd(commandHandler), new SpecsCommand(commandHandler), new ChangeKeyCmd(commandHandler), 
				new CleanItemFileCmd(commandHandler), new SetPermissionCmd(commandHandler), new DisableRecipes(commandHandler)));
		
	}
	
	//Registers the listener class to the server.
	private void setupListeners(){
		getServer().getPluginManager().registerEvents(new EventClass(this), this);
	}
	
	private void setupFileManager(){
		fm = FileManager.init(this);
		fm.cacheItems();
		fm.cacheRecipes();
	}
	
	public FileManager getFileManager(){
		return fm;
	}
	public RecipeLoader getRecipeLoader(){
		return loader;
	}
	public GUIContainer getGUIContainer(){
		return guiContainer;
	}
	public RecipeInjector getRecipeInjector(){
		return injector;
	}
	public Messenger getMessenger(){
		return messenger;
	}
	
	
}
