package net.teamfruit.fruitlib.command;

import javax.annotation.Nonnull;

import net.teamfruit.lib.command.RootCommand;

public class FruitCommands extends RootCommand {
	public final static @Nonnull FruitCommands instance = new FruitCommands();

	private FruitCommands() {
		super("fruitlib", "fruit", "fruits");
	}
}
