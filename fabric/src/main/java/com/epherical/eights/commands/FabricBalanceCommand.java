package com.epherical.eights.commands;

import com.epherical.eights.BalanceMethods;
import com.epherical.eights.EightsEconomyProvider;
import com.epherical.eights.data.EconomyData;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;

public class FabricBalanceCommand extends BalanceMethods {

    public static void applyProviders(EightsEconomyProvider econProvider, EconomyData economyData) {
        BalanceMethods.applyProviders(econProvider, economyData);
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> mainCommand = dispatcher.register(Commands.literal("bal")
                .requires(commandSourceStack -> Permissions.check(commandSourceStack, "eights.command.balance.check", 0))
                .executes(context -> checkBalance(context, context.getSource().getPlayerOrException()))
                .then(Commands.literal("add")
                        .requires(commandSourceStack -> Permissions.check(commandSourceStack, "eights.command.balance.add", 2))
                        .then(Commands.argument("player", EntityArgument.players())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(BalanceMethods::addMoney))))
                .then(Commands.literal("remove")
                        .requires(commandSourceStack -> Permissions.check(commandSourceStack, "eights.command.balance.remove", 2))
                        .then(Commands.argument("player", EntityArgument.players())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(BalanceMethods::removeMoney))))
                .then(Commands.literal("set")
                        .requires(commandSourceStack -> Permissions.check(commandSourceStack, "eights.command.balance.set", 2))
                        .then(Commands.argument("player", EntityArgument.players())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(BalanceMethods::setMoney))))
                .then(Commands.literal("pay")
                        .requires(commandSourceStack -> Permissions.check(commandSourceStack, "eights.command.balance.pay", 0))
                        .then(Commands.argument("player", EntityArgument.players())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(BalanceMethods::payMoney))))
                .then(Commands.argument("player", EntityArgument.players())
                        .executes(context -> checkBalance(context, EntityArgument.getPlayer(context, "player")))));
        dispatcher.register(Commands.literal("balance").redirect(mainCommand));
        dispatcher.register(Commands.literal("money").redirect(mainCommand));
    }
}
