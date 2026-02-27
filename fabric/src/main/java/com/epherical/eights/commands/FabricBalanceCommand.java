package com.epherical.eights.commands;

import com.epherical.eights.BalanceMethods;
import com.epherical.octoecon.api.OctoEconomy;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;

public class FabricBalanceCommand extends BalanceMethods {

    public static void applyProvider(OctoEconomy<?, ?> econProvider) {
        BalanceMethods.applyProvider(econProvider);
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> mainCommand = dispatcher.register(Commands.literal("bal")
                .requires(commandSourceStack -> commandSourceStack.hasPermission(0))
                .executes(context -> checkBalance(context, context.getSource().getPlayerOrException()))
                .then(Commands.literal("add")
                        .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(BalanceMethods::addMoney))))
                .then(Commands.literal("remove")
                        .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(BalanceMethods::removeMoney))))
                .then(Commands.literal("set")
                        .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(BalanceMethods::setMoney))))
                .then(Commands.literal("pay")
                        .requires(commandSourceStack -> commandSourceStack.hasPermission(0))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(BalanceMethods::payMoney))))
                .then(Commands.argument("player", EntityArgument.player())
                        .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                        .executes(context -> checkBalance(context, EntityArgument.getPlayer(context, "player")))));
        dispatcher.register(Commands.literal("balance").redirect(mainCommand));
        dispatcher.register(Commands.literal("money").redirect(mainCommand));
    }
}
