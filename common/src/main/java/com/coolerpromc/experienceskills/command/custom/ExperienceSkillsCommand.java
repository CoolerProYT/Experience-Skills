package com.coolerpromc.experienceskills.command.custom;

import com.coolerpromc.experienceskills.command.argument.ExperienceTypeArgument;
import com.coolerpromc.experienceskills.entity.custom.AbstractExperienceOrb;
import com.coolerpromc.experienceskills.api.type.ExperienceType;
import com.coolerpromc.experienceskills.util.TriPredicate;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.Collection;
import java.util.function.ToIntBiFunction;

public class ExperienceSkillsCommand {
    private static final SimpleCommandExceptionType ERROR_SET_POINTS_INVALID = new SimpleCommandExceptionType(
        Component.translatable("commands.experience.set.points.invalid")
    );

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(
            Commands.literal("experienceskills")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(
                    Commands.literal("add")
                        .then(
                            Commands.argument("target", EntityArgument.players())
                                .then(
                                    Commands.argument("type", new ExperienceTypeArgument())
                                        .then(
                                            Commands.argument("amount", IntegerArgumentType.integer())
                                                .executes(
                                                    c -> addExperience(
                                                        c.getSource(),
                                                        EntityArgument.getPlayers(c, "target"),
                                                        IntegerArgumentType.getInteger(c, "amount"),
                                                        Type.POINTS,
                                                        ExperienceTypeArgument.getExperienceType(c, "type")
                                                    )
                                                )
                                                .then(
                                                    Commands.literal("points")
                                                        .executes(
                                                            c -> addExperience(
                                                                c.getSource(),
                                                                EntityArgument.getPlayers(c, "target"),
                                                                IntegerArgumentType.getInteger(c, "amount"),
                                                                Type.POINTS,
                                                                ExperienceTypeArgument.getExperienceType(c, "type")
                                                            )
                                                        )
                                                )
                                                .then(
                                                    Commands.literal("levels")
                                                        .executes(
                                                            c -> addExperience(
                                                                c.getSource(),
                                                                EntityArgument.getPlayers(c, "target"),
                                                                IntegerArgumentType.getInteger(c, "amount"),
                                                                Type.LEVELS,
                                                                ExperienceTypeArgument.getExperienceType(c, "type")
                                                            )
                                                        )
                                                )
                                        )
                                )
                        )
                )
                .then(
                    Commands.literal("set")
                        .then(
                            Commands.argument("target", EntityArgument.players())
                                .then(
                                    Commands.argument("type", new ExperienceTypeArgument())
                                        .then(
                                            Commands.argument("amount", IntegerArgumentType.integer(0))
                                                .executes(
                                                    c -> setExperience(
                                                        c.getSource(),
                                                        EntityArgument.getPlayers(c, "target"),
                                                        IntegerArgumentType.getInteger(c, "amount"),
                                                        Type.POINTS,
                                                        ExperienceTypeArgument.getExperienceType(c, "type")
                                                    )
                                                )
                                                .then(
                                                    Commands.literal("points")
                                                        .executes(
                                                            c -> setExperience(
                                                                c.getSource(),
                                                                EntityArgument.getPlayers(c, "target"),
                                                                IntegerArgumentType.getInteger(c, "amount"),
                                                                Type.POINTS,
                                                                ExperienceTypeArgument.getExperienceType(c, "type")
                                                            )
                                                        )
                                                )
                                                .then(
                                                    Commands.literal("levels")
                                                        .executes(
                                                            c -> setExperience(
                                                                c.getSource(),
                                                                EntityArgument.getPlayers(c, "target"),
                                                                IntegerArgumentType.getInteger(c, "amount"),
                                                                Type.LEVELS,
                                                                ExperienceTypeArgument.getExperienceType(c, "type")
                                                            )
                                                        )
                                                )
                                        )
                                )
                        )
                )
                .then(
                    Commands.literal("query")
                        .then(
                            Commands.argument("target", EntityArgument.player())
                                .then(
                                    Commands.argument("type", new ExperienceTypeArgument())
                                        .then(
                                            Commands.literal("points")
                                                .executes(c -> queryExperience(c.getSource(), EntityArgument.getPlayer(c, "target"), Type.POINTS, ExperienceTypeArgument.getExperienceType(c, "type")))
                                        )
                                        .then(
                                            Commands.literal("levels")
                                                .executes(c -> queryExperience(c.getSource(), EntityArgument.getPlayer(c, "target"), Type.LEVELS, ExperienceTypeArgument.getExperienceType(c, "type")))
                                        )
                                )
                        )
                )
        );
    }

    private static int queryExperience(CommandSourceStack source, ServerPlayer target, Type type, ExperienceType experienceType) {
        int result = type.query.applyAsInt(target, experienceType);
        source.sendSuccess(() -> Component.translatable("commands.experience.query." + type.name, target.getDisplayName(), result), false);
        return result;
    }

    private static int addExperience(CommandSourceStack source, Collection<? extends ServerPlayer> players, int amount, Type type, ExperienceType experienceType) {
        for (ServerPlayer player : players) {
            type.add.accept(player, amount, experienceType);
        }

        if (players.size() == 1) {
            source.sendSuccess(
                () -> Component.translatable("commands.experience.add." + type.name + ".success.single", amount, players.iterator().next().getDisplayName()),
                true
            );
        } else {
            source.sendSuccess(() -> Component.translatable("commands.experience.add." + type.name + ".success.multiple", amount, players.size()), true);
        }

        return players.size();
    }

    private static int setExperience(CommandSourceStack source, Collection<? extends ServerPlayer> players, int amount, Type type, ExperienceType experienceType) throws CommandSyntaxException {
        int success = 0;

        for (ServerPlayer player : players) {
            if (type.set.test(player, amount, experienceType)) {
                success++;
            }
        }

        if (success == 0) {
            throw ERROR_SET_POINTS_INVALID.create();
        } else {
            if (players.size() == 1) {
                source.sendSuccess(
                    () -> Component.translatable("commands.experience.set." + type.name + ".success.single", amount, players.iterator().next().getDisplayName()),
                    true
                );
            } else {
                source.sendSuccess(() -> Component.translatable("commands.experience.set." + type.name + ".success.multiple", amount, players.size()), true);
            }

            return players.size();
        }
    }

    private enum Type {
        POINTS("points", AbstractExperienceOrb::giveExperiencePoints, (p, a, experienceType) -> {
            if (a >= AbstractExperienceOrb.getXpNeededForNextLevel(p, experienceType.getKey())) {
                return false;
            } else {
                AbstractExperienceOrb.setPoints(p, a, experienceType.getKey());
                return true;
            }
        }, AbstractExperienceOrb::getCurrentLevelPoints),
        LEVELS("levels", AbstractExperienceOrb::giveExperienceLevels, (p, a, experienceType) -> {
            AbstractExperienceOrb.setLevel(p, a, experienceType.getKey());
            return true;
        }, (p, e) -> AbstractExperienceOrb.getLevel(p, e.getKey()));

        public final TriConsumer<ServerPlayer, Integer, ExperienceType> add;
        public final TriPredicate<ServerPlayer, Integer, ExperienceType> set;
        public final String name;
        private final ToIntBiFunction<ServerPlayer, ExperienceType> query;

        Type(String name, TriConsumer<ServerPlayer, Integer, ExperienceType> add, TriPredicate<ServerPlayer, Integer, ExperienceType> set, ToIntBiFunction<ServerPlayer, ExperienceType> query) {
            this.add = add;
            this.name = name;
            this.set = set;
            this.query = query;
        }
    }
}
