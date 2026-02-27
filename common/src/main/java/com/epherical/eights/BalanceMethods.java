package com.epherical.eights;

import com.epherical.octoecon.api.OctoEconomy;
import com.epherical.octoecon.api.user.UniqueUser;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public class BalanceMethods {

    private static OctoEconomy<?, ?> provider;

    public static void applyProvider(OctoEconomy<?, ?> economyProvider) {
        provider = economyProvider;
    }

    public static @Nullable OctoEconomy<?, ?> getProvider() {
        return provider;
    }

    protected static int checkBalance(CommandContext<CommandSourceStack> context, ServerPlayer player) {
        if (provider == null) {
            return 0;
        }

        UniqueUser user = provider.getOrCreatePlayerAccount(player.getUUID());
        double balance = user.getBalance("command.balance.check");
        Component text = Component.literal(String.format("$%.2f", balance)).setStyle(EightsEconMod.VARIABLE_STYLE);
        Component playerName = Component.literal(player.getScoreboardName()).setStyle(EightsEconMod.VARIABLE_STYLE);
        Component actualMessage = Component.translatable("%s has %s.", playerName, text).setStyle(EightsEconMod.APPROVAL_STYLE);
        context.getSource().sendSuccess(() -> actualMessage, true);

        return 1;
    }

    protected static int addMoney(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        if (provider == null) {
            return 0;
        }

        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        int amount = IntegerArgumentType.getInteger(context, "amount");
        UniqueUser user = provider.getOrCreatePlayerAccount(player.getUUID());
        user.depositMoney(amount, "command.balance.add");

        Component playerName = pluralize(player.getScoreboardName(), EightsEconMod.VARIABLE_STYLE);
        Component amountComponent = Component.literal(String.format("$%.2f", (double) amount)).setStyle(EightsEconMod.VARIABLE_STYLE);
        Component component = Component.translatable("Added %s to %s account.", amountComponent, playerName).setStyle(EightsEconMod.APPROVAL_STYLE);
        context.getSource().sendSuccess(() -> component, false);

        return 1;
    }

    protected static int removeMoney(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        if (provider == null) {
            return 0;
        }

        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        int amount = IntegerArgumentType.getInteger(context, "amount");
        UniqueUser user = provider.getOrCreatePlayerAccount(player.getUUID());
        user.withdrawMoney(amount, "command.balance.remove");

        Component playerName = pluralize(player.getScoreboardName(), EightsEconMod.VARIABLE_STYLE);
        Component amountComponent = Component.literal(String.format("$%.2f", (double) amount)).setStyle(EightsEconMod.VARIABLE_STYLE);
        Component component = Component.translatable("Removed %s from %s account.", amountComponent, playerName).setStyle(EightsEconMod.APPROVAL_STYLE);
        context.getSource().sendSuccess(() -> component, false);

        return 1;
    }

    protected static int setMoney(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        if (provider == null) {
            return 0;
        }

        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        int amount = IntegerArgumentType.getInteger(context, "amount");
        UniqueUser user = provider.getOrCreatePlayerAccount(player.getUUID());
        user.setBalance(amount, "command.balance.set");

        Component playerName = pluralize(player.getScoreboardName(), EightsEconMod.VARIABLE_STYLE);
        Component amountComponent = Component.literal(String.format("$%.2f", (double) amount)).setStyle(EightsEconMod.VARIABLE_STYLE);
        Component component = Component.translatable("Set money to %s in %s account.", amountComponent, playerName).setStyle(EightsEconMod.APPROVAL_STYLE);
        context.getSource().sendSuccess(() -> component, false);

        return 1;
    }

    protected static int payMoney(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        if (provider == null) {
            return 0;
        }

        ServerPlayer source = context.getSource().getPlayerOrException();
        ServerPlayer target = EntityArgument.getPlayer(context, "player");
        int amount = IntegerArgumentType.getInteger(context, "amount");
        UniqueUser sourceUser = provider.getOrCreatePlayerAccount(source.getUUID());
        UniqueUser targetUser = provider.getOrCreatePlayerAccount(target.getUUID());

        if (source.equals(target)) {
            Component message = Component.literal("You can't send money to yourself!").setStyle(EightsEconMod.ERROR_STYLE);
            context.getSource().sendFailure(message);
            return 0;
        }

        if (!sourceUser.hasAmount(amount, "command.balance.pay.check")) {
            Component message = Component.literal("You do not have enough money.").setStyle(EightsEconMod.ERROR_STYLE);
            context.getSource().sendFailure(message);
            return 0;
        }

        sourceUser.sendTo(targetUser, amount, "command.balance.pay");
        Component targetName = Component.literal(target.getScoreboardName()).withStyle(EightsEconMod.VARIABLE_STYLE);
        Component sourceName = Component.literal(source.getScoreboardName()).withStyle(EightsEconMod.VARIABLE_STYLE);
        Component amountComponent = Component.literal(String.format("$%.2f", (double) amount)).setStyle(EightsEconMod.VARIABLE_STYLE);
        Component sourceMessage = Component.translatable("You have sent %s to %s!", amountComponent, targetName).setStyle(EightsEconMod.APPROVAL_STYLE);
        Component targetMessage = Component.translatable("You have received %s from %s!", amountComponent, sourceName).setStyle(EightsEconMod.APPROVAL_STYLE);
        context.getSource().sendSuccess(() -> sourceMessage, true);
        target.sendSystemMessage(targetMessage);
        return 1;
    }

    private static Component pluralize(String name, Style style) {
        name = name.endsWith("s") ? name + "'" : name + "'s";
        return Component.literal(name).setStyle(style);
    }
}
