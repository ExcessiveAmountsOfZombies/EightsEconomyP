package com.epherical.eights.commands;

import com.epherical.eights.BalanceMethods;
import com.epherical.eights.EightsEconMod;
import com.epherical.eights.permissions.CommonPermissionBridge;
import com.epherical.octoecon.api.OctoEconomy;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;

public class ForgeBalanceCommand extends BalanceMethods {

    public static void applyProvider(OctoEconomy<?, ?> economyProvider) {
        BalanceMethods.applyProvider(economyProvider);
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> mainCommand = dispatcher.register(Commands.literal("bal")
                .requires(CommonPermissionBridge.require(EightsEconMod.CHECK, EightsEconMod.OP_CHECK))
                .executes(context -> checkBalance(context, context.getSource().getPlayerOrException()))
                .then(Commands.argument("player", EntityArgument.player())
                        .requires(CommonPermissionBridge.require(EightsEconMod.CHECK_OTHER, EightsEconMod.OP_CHECK_OTHER))
                        .executes(context -> checkBalance(context, EntityArgument.getPlayer(context, "player")))
                        .then(Commands.literal("add")
                                .requires(CommonPermissionBridge.require(EightsEconMod.ADD, EightsEconMod.OP_ADD))
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(BalanceMethods::addMoney))))
                        .then(Commands.literal("remove")
                                .requires(CommonPermissionBridge.require(EightsEconMod.REMOVE, EightsEconMod.OP_REMOVE))
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(BalanceMethods::removeMoney)))
                        .then(Commands.literal("set")
                                .requires(CommonPermissionBridge.require(EightsEconMod.SET, EightsEconMod.OP_SET))
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(BalanceMethods::setMoney)))
                        .then(Commands.literal("pay")
                                .requires(CommonPermissionBridge.require(EightsEconMod.PAY, EightsEconMod.OP_PAY))
                                .then(Commands.argument("target", EntityArgument.player())
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                                .executes(BalanceMethods::payMoney)))));

        LiteralArgumentBuilder<CommandSourceStack> baltop = Commands.literal("baltop")
                .requires(CommonPermissionBridge.require(EightsEconMod.BALTOP, EightsEconMod.OP_BALTOP))
                .executes(BalanceMethods::balTop);



        dispatcher.register(baltop.then(Commands.argument("page", IntegerArgumentType.integer(1)).executes(BalanceMethods::balTopPage)));

        dispatcher.register(Commands.literal("balance").redirect(mainCommand));
        dispatcher.register(Commands.literal("money").redirect(mainCommand));
    }
}
