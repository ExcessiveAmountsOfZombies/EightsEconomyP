package com.epherical.eights;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.server.permission.nodes.PermissionDynamicContext;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;

import java.util.Map;
import java.util.UUID;

import static com.epherical.eights.EightsEconMod.MOD_ID;

public class PermissionsNeoForge {

    public static final PermissionNode<Boolean> CHECK = new PermissionNode<>(MOD_ID, "command.balance.check", PermissionTypes.BOOLEAN, PermissionsNeoForge::defaultTrue);
    public static final PermissionNode<Boolean> CHECK_OTHER = new PermissionNode<>(MOD_ID, "command.balance.check.other", PermissionTypes.BOOLEAN, PermissionsNeoForge::defaultTrue);
    public static final PermissionNode<Boolean> ADD = new PermissionNode<>(MOD_ID, "command.balance.add", PermissionTypes.BOOLEAN, PermissionsNeoForge::defaultFour);
    public static final PermissionNode<Boolean> REMOVE = new PermissionNode<>(MOD_ID, "command.balance.remove", PermissionTypes.BOOLEAN, PermissionsNeoForge::defaultFour);
    public static final PermissionNode<Boolean> SET = new PermissionNode<>(MOD_ID, "command.balance.set", PermissionTypes.BOOLEAN, PermissionsNeoForge::defaultFour);
    public static final PermissionNode<Boolean> PAY = new PermissionNode<>(MOD_ID, "command.balance.pay", PermissionTypes.BOOLEAN, PermissionsNeoForge::defaultTrue);
    public static final PermissionNode<Boolean> BALTOP = new PermissionNode<>(MOD_ID, "command.balance.top", PermissionTypes.BOOLEAN, PermissionsNeoForge::defaultTrue);

    private static final Map<String, PermissionNode<Boolean>> NODE_LOOKUP = Map.of(
            EightsEconMod.CHECK, CHECK,
            EightsEconMod.CHECK_OTHER, CHECK_OTHER,
            EightsEconMod.ADD, ADD,
            EightsEconMod.REMOVE, REMOVE,
            EightsEconMod.SET, SET,
            EightsEconMod.PAY, PAY,
            EightsEconMod.BALTOP, BALTOP
    );

    public static void registerPermissions(PermissionGatherEvent.Nodes event) {
        event.addNodes(CHECK, CHECK_OTHER, ADD, REMOVE, SET, PAY, BALTOP);
    }

    public static boolean has(CommandSourceStack source, String permissionNode, int fallbackOpLevel) {
        if (source.hasPermission(fallbackOpLevel)) {
            return true;
        }

        PermissionNode<Boolean> node = NODE_LOOKUP.get(permissionNode);
        if (node == null) {
            return false;
        }

        ServerPlayer player;
        try {
            player = source.getPlayerOrException();
        } catch (Exception ignored) {
            return false;
        }

        return PermissionAPI.getPermission(player, node);
    }

    private static Boolean defaultFour(ServerPlayer player, UUID playerUUID, PermissionDynamicContext<?>... context) {
        return player != null && player.hasPermissions(4);
    }

    private static Boolean defaultTrue(ServerPlayer player, UUID playerUUID, PermissionDynamicContext<?>... context) {
        return true;
    }
}
