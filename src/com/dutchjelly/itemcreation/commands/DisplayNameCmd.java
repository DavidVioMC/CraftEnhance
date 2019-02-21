package com.dutchjelly.itemcreation.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dutchjelly.craftenhance.commandhandling.CmdInterface;
import com.dutchjelly.craftenhance.commandhandling.CustomCmd;
import com.dutchjelly.craftenhance.commandhandling.CustomCmdHandler;
import com.dutchjelly.itemcreation.ItemCreator;
import com.dutchjelly.itemcreation.util.ParseResult;

@CustomCmd(cmdPath = "edititem.name", perms = "perms.item-editor")
public class DisplayNameCmd implements CmdInterface{

	private CustomCmdHandler handler;
	
	public DisplayNameCmd(CustomCmdHandler handler){
		this.handler = handler;
	}
	
	@Override
	public String getDescription() {
		return "With this command the displayname of the held item can be set. Here's an example of the usage: &7/edititem name example name";
	}

	@Override
	public void handlePlayerCommand(Player p, String[] args) {
		ItemCreator creator = new ItemCreator(p.getInventory().getItemInMainHand(), args);
		ParseResult result = creator.setDisplayName();
		p.getInventory().setItemInMainHand(creator.getItem());
		handler.getMain().getMessenger().message(result.getMessage(), p);
	}

	@Override
	public void handleConsoleCommand(CommandSender sender, String[] args) {
		handler.getMain().getMessenger().messageFromConfig("messages.commands.only-for-players", sender);
	}

}
