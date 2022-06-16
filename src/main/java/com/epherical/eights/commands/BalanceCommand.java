package com.epherical.eights.commands;

import com.epherical.eights.EightsEconMod;
import com.epherical.eights.EightsEconomyProvider;
import com.epherical.eights.data.EconomyData;
import com.epherical.octoecon.api.Currency;
import com.epherical.octoecon.api.user.UniqueUser;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;

public class BalanceCommand {

    private static EightsEconomyProvider provider;
    private static EconomyData data;

    public static void applyProviders(EightsEconomyProvider econProvider, EconomyData economyData) {
        provider = econProvider;
        data = economyData;
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> mainCommand = dispatcher.register(Commands.literal("bal")
                .requires(commandSourceStack -> Permissions.check(commandSourceStack, "eights.command.balance.check", 0))
                .executes(context -> checkBalance(context, context.getSource().getPlayerOrException()))
                .then(Commands.literal("add")
                        .requires(commandSourceStack -> Permissions.check(commandSourceStack, "eights.command.balance.add", 4))
                        .then(Commands.argument("player", EntityArgument.players())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(BalanceCommand::addMoney))))
                .then(Commands.literal("remove")
                        .requires(commandSourceStack -> Permissions.check(commandSourceStack, "eights.command.balance.remove", 4))
                        .then(Commands.argument("player", EntityArgument.players())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(BalanceCommand::removeMoney))))
                .then(Commands.literal("set")
                        .requires(commandSourceStack -> Permissions.check(commandSourceStack, "eights.command.balance.set", 4))
                        .then(Commands.argument("player", EntityArgument.players())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(BalanceCommand::setMoney))))
                .then(Commands.literal("pay")
                        .requires(commandSourceStack -> Permissions.check(commandSourceStack, "eights.command.balance.pay", 0))
                        .then(Commands.argument("player", EntityArgument.players())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(BalanceCommand::payMoney))))
                .then(Commands.argument("player", EntityArgument.players())
                        .executes(context -> checkBalance(context, EntityArgument.getPlayer(context, "player")))));
        dispatcher.register(Commands.literal("balance").redirect(mainCommand));
        dispatcher.register(Commands.literal("money").redirect(mainCommand));
    }


    private static int checkBalance(CommandContext<CommandSourceStack> context, ServerPlayer player) {
        UniqueUser user = provider.getOrCreatePlayerAccount(player.getUUID());
        if (user != null) {
            Currency currency = provider.getDefaultCurrency();
            double balance = user.getBalance(currency);
            Component text = currency.format(balance, 2);
            Component playerName = Component.literal(player.getScoreboardName()).setStyle(EightsEconMod.VARIABLE_STYLE);
            Component actualMessage = Component.translatable("%s has %s.", playerName, text).setStyle(EightsEconMod.APPROVAL_STYLE);
            context.getSource().sendSuccess(actualMessage, true);
        }

        return 1;
    }

    private static int addMoney(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        int amount = IntegerArgumentType.getInteger(context, "amount");
        UniqueUser user = provider.getOrCreatePlayerAccount(player.getUUID());
        if (user != null) {
            Currency currency = provider.getDefaultCurrency();
            user.depositMoney(currency, amount, "command");
            Component playerName = pluralize(player.getScoreboardName(), EightsEconMod.VARIABLE_STYLE);
            Component component = Component.translatable("Added %s to %s account.", currency.format(amount, 2), playerName)
                    .setStyle(EightsEconMod.APPROVAL_STYLE);
            context.getSource().sendSuccess(component, false);
        }

        return 1;
    }

    private static int removeMoney(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        int amount = IntegerArgumentType.getInteger(context, "amount");
        UniqueUser user = provider.getOrCreatePlayerAccount(player.getUUID());
        if (user != null) {
            Currency currency = provider.getDefaultCurrency();
            user.withdrawMoney(currency, amount, "command");
            Component playerName = pluralize(player.getScoreboardName(), EightsEconMod.VARIABLE_STYLE);
            Component component = Component.translatable("Removed %s from %s account.", currency.format(amount, 2), playerName)
                    .setStyle(EightsEconMod.APPROVAL_STYLE);
            context.getSource().sendSuccess(component, false);
        }

        return 1;
    }

    private static int setMoney(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        int amount = IntegerArgumentType.getInteger(context, "amount");
        UniqueUser user = provider.getOrCreatePlayerAccount(player.getUUID());
        if (user != null) {
            Currency currency = provider.getDefaultCurrency();
            user.setBalance(currency, amount);
            Component playerName = pluralize(player.getScoreboardName(), EightsEconMod.VARIABLE_STYLE);
            Component component = Component.translatable("Set money to %s in %s account.", currency.format(amount, 2), playerName)
                    .setStyle(EightsEconMod.APPROVAL_STYLE);
            context.getSource().sendSuccess(component, false);
        }

        return 1;
    }

    private static int payMoney(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer source = context.getSource().getPlayerOrException();
        ServerPlayer target = EntityArgument.getPlayer(context, "player");
        int amount = IntegerArgumentType.getInteger(context, "amount");
        UniqueUser sourceUser = provider.getOrCreatePlayerAccount(source.getUUID());
        UniqueUser targetUser = provider.getOrCreatePlayerAccount(target.getUUID());
        if (sourceUser != null && targetUser != null) {
            if (source.equals(target)) {
                Component message = Component.literal("You can't send money to yourself!").setStyle(EightsEconMod.ERROR_STYLE);
                context.getSource().sendFailure(message);
                return 0;
            }

            Currency currency = provider.getDefaultCurrency();
            if (sourceUser.hasAmount(currency, amount)) {
                sourceUser.sendTo(targetUser, currency, amount);
                Component targetName = Component.literal(target.getScoreboardName()).withStyle(EightsEconMod.VARIABLE_STYLE);
                Component sourceName = Component.literal(source.getScoreboardName()).withStyle(EightsEconMod.VARIABLE_STYLE);
                Component sourceMessage = Component.translatable("You have sent %s to %s!", currency.format(amount, 2), targetName)
                        .setStyle(EightsEconMod.APPROVAL_STYLE);
                Component targetMessage = Component.translatable("You have received %s from %s!", currency.format(amount, 2), sourceName)
                        .setStyle(EightsEconMod.APPROVAL_STYLE);
                context.getSource().sendSuccess(sourceMessage, true);
                target.sendSystemMessage(targetMessage);
            }
        }
        return 1;
    }

    private static Component pluralize(String name, Style style) {
        name = name.endsWith("s") ? name + "'" : name + "'s";
        return Component.literal(name).setStyle(style);

    }
}
