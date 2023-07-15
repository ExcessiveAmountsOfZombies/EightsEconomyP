package com.epherical.eights.commands;

import com.epherical.eights.BalanceMethods;
import com.epherical.eights.EightsEconomyProvider;
import com.epherical.eights.EightsPermissions;
import com.epherical.eights.data.EconomyData;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.nodes.PermissionNode;

import java.security.Permissions;
import java.util.function.Predicate;

import static com.epherical.eights.EightsPermissions.*;

public class ForgeBalanceCommand extends BalanceMethods {


    public static void applyProviders(EightsEconomyProvider econProvider, EconomyData economyData) {
        BalanceMethods.applyProviders(econProvider, economyData);
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> mainCommand = dispatcher.register(Commands.literal("bal")
                .requires(require(CHECK))
                .executes(context -> checkBalance(context, context.getSource().getPlayerOrException()))
                .then(Commands.literal("add")
                        .requires(require(ADD))
                        .then(Commands.argument("player", EntityArgument.players())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(BalanceMethods::addMoney))))
                .then(Commands.literal("remove")
                        .requires(require(REMOVE))
                        .then(Commands.argument("player", EntityArgument.players())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(BalanceMethods::removeMoney))))
                .then(Commands.literal("set")
                        .requires(require(SET))
                        .then(Commands.argument("player", EntityArgument.players())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(BalanceMethods::setMoney))))
                .then(Commands.literal("pay")
                        .requires(require(PAY))
                        .then(Commands.argument("player", EntityArgument.players())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(BalanceMethods::payMoney))))
                .then(Commands.argument("player", EntityArgument.players())
                        .requires(require(CHECK_OTHER))
                        .executes(context -> checkBalance(context, EntityArgument.getPlayer(context, "player")))));
        dispatcher.register(Commands.literal("balance").redirect(mainCommand));
        dispatcher.register(Commands.literal("money").redirect(mainCommand));
    }

    private static Predicate<CommandSourceStack> require(PermissionNode<Boolean> node) {
        return commandSourceStack -> {
            try {
                ServerPlayer player = commandSourceStack.getPlayerOrException();
                return PermissionAPI.getPermission(player, node);
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }
            return false;
        };
    }

}
