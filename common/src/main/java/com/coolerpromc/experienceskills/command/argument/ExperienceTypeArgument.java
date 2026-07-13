package com.coolerpromc.experienceskills.command.argument;

import com.coolerpromc.experienceskills.api.type.ExperienceType;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.locale.Language;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class ExperienceTypeArgument implements ArgumentType<ExperienceType> {
    private static final Dynamic2CommandExceptionType INVALID_ENUM = new Dynamic2CommandExceptionType(
        (found, constants) -> Component.translatableWithFallback("commands.experienceskills.arguments.enum.invalid", Language.getInstance().getOrDefault("commands.experienceskills.arguments.enum.invalid"), constants, found));

    public static ExperienceType getExperienceType(final CommandContext<?> context, final String name){
        return context.getArgument(name, ExperienceType.class);
    }

    @Override
    public ExperienceType parse(StringReader reader) throws CommandSyntaxException {
        String name = reader.readUnquotedString();
        return ExperienceType.byName(name).orElseThrow(() -> INVALID_ENUM.createWithContext(reader, name, Arrays.toString(ExperienceType.ALL.stream().filter(ExperienceType::isEnabled).filter(type -> !type.getSerializedName().equals("vanilla")).map(StringRepresentable::getSerializedName).toArray())));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(ExperienceType.ALL.stream().filter(ExperienceType::isEnabled).filter(type -> !type.getSerializedName().equals("vanilla")).map(StringRepresentable::getSerializedName), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return ExperienceType.ALL.stream().filter(ExperienceType::isEnabled).filter(type -> !type.getSerializedName().equals("vanilla")).map(StringRepresentable::getSerializedName).toList();
    }

    public static class Info implements ArgumentTypeInfo<ExperienceTypeArgument, Info.Template>{
        @Override
        public void serializeToNetwork(Info.Template template, FriendlyByteBuf out) {

        }

        @Override
        public Info.Template deserializeFromNetwork(FriendlyByteBuf in) {
            return new Info.Template();
        }

        @Override
        public void serializeToJson(Info.Template template, JsonObject out) {
            out.addProperty("enum", template.enumClass.getName());
        }

        @Override
        public Template unpack(ExperienceTypeArgument argument) {
            return new Template();
        }

        public class Template implements ArgumentTypeInfo.Template<ExperienceTypeArgument>{
            final Class<ExperienceType> enumClass = ExperienceType.class;

            @Override
            public ExperienceTypeArgument instantiate(CommandBuildContext context) {
                return new ExperienceTypeArgument();
            }

            @Override
            public ArgumentTypeInfo<ExperienceTypeArgument, ?> type() {
                return Info.this;
            }
        }
    }
}
