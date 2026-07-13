package com.coolerpromc.experienceskills.command;

import com.coolerpromc.experienceskills.command.custom.ExperienceSkillsCommand;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ModCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection commandSelection) {
        ExperienceSkillsCommand.register(dispatcher);
    }
}
