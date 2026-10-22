package com.coolerpromc.experienceskills.command.custom;

import com.coolerpromc.experienceskills.command.argument.ExperienceTypeArgument;
import com.coolerpromc.experienceskills.config.ModGeneralConfig;
import com.coolerpromc.experienceskills.skill.SkillManager;
import com.coolerpromc.experienceskills.entity.custom.ModExperienceOrb;
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

    private static final SimpleCommandExceptionType ERROR_TRANSFER_DISABLED = new SimpleCommandExceptionType(
        Component.translatable("commands.experienceskills.transfer.disabled")
    );
    private static final SimpleCommandExceptionType ERROR_TRANSFER_SAME = new SimpleCommandExceptionType(
        Component.translatable("commands.experienceskills.transfer.same")
    );
    private static final SimpleCommandExceptionType ERROR_TRANSFER_DISABLED_SKILL = new SimpleCommandExceptionType(
        Component.translatable("commands.experienceskills.transfer.disabled_skill")
    );
    private static final SimpleCommandExceptionType ERROR_TRANSFER_EMPTY = new SimpleCommandExceptionType(
        Component.translatable("commands.experienceskills.transfer.empty")
    );

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(
            Commands.literal("experienceskills")
                .then(
                    Commands.literal("transfer")
                        .then(
                            Commands.argument("from", new ExperienceTypeArgument())
                                .then(
                                    Commands.argument("to", new ExperienceTypeArgument())
                                        .then(
                                            Commands.argument("points", IntegerArgumentType.integer(1))
                                                .executes(c -> transferExperience(
                                                    c.getSource(),
                                                    ExperienceTypeArgument.getExperienceType(c, "from"),
                                                    ExperienceTypeArgument.getExperienceType(c, "to"),
                                                    IntegerArgumentType.getInteger(c, "points")
                                                ))
                                        )
                                )
                        )
                )
                .then(
                    Commands.literal("add")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
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
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
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
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
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

    private static int transferExperience(CommandSourceStack source, ExperienceType from, ExperienceType to, int points) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        if (!ModGeneralConfig.TRANSFER_ENABLED.get()) {
            throw ERROR_TRANSFER_DISABLED.create();
        }
        if (from == to || from == ExperienceType.VANILLA || to == ExperienceType.VANILLA) {
            throw ERROR_TRANSFER_SAME.create();
        }
        if (!from.isEnabled() || !to.isEnabled()) {
            throw ERROR_TRANSFER_DISABLED_SKILL.create();
        }

        int available = SkillManager.getPoints(player, from);
        int moved = Math.min(points, available);
        if (moved <= 0) {
            throw ERROR_TRANSFER_EMPTY.create();
        }
        int received = (int) Math.floor(moved * ModGeneralConfig.TRANSFER_EFFICIENCY.get());

        SkillManager.setPoints(player, from, available - moved, false);
        SkillManager.addPoints(player, to, received, true);

        source.sendSuccess(() -> Component.translatable("commands.experienceskills.transfer.success", moved,
            Component.translatable("skill.experienceskills." + from.getSerializedName()), received,
            Component.translatable("skill.experienceskills." + to.getSerializedName())), false);
        return received;
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
        POINTS("points", ModExperienceOrb::giveExperiencePoints, (p, a, experienceType) -> {
            if (a >= ModExperienceOrb.getXpNeededForNextLevel(p, experienceType.getKey())) {
                return false;
            } else {
                ModExperienceOrb.setPoints(p, a, experienceType.getKey());
                return true;
            }
        }, ModExperienceOrb::getCurrentLevelPoints),
        LEVELS("levels", ModExperienceOrb::giveExperienceLevels, (p, a, experienceType) -> {
            ModExperienceOrb.setLevel(p, a, experienceType.getKey());
            return true;
        }, (p, e) -> ModExperienceOrb.getLevel(p, e.getKey()));

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
