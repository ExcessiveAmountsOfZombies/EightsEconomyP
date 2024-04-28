package com.epherical.eights;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import net.minecraftforge.server.permission.nodes.PermissionDynamicContext;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import net.minecraftforge.server.permission.nodes.PermissionTypes;

import java.util.UUID;

public class EightsPermissions {

    private static final String MODID = "eights";

    public static final PermissionNode<Boolean> CHECK = new PermissionNode<>(MODID, "command.balance.check", PermissionTypes.BOOLEAN, EightsPermissions::defaultTrue);
    public static final PermissionNode<Boolean> CHECK_OTHER = new PermissionNode<>(MODID, "command.balance.check.other", PermissionTypes.BOOLEAN, EightsPermissions::defaultTrue);
    public static final PermissionNode<Boolean> ADD = new PermissionNode<>(MODID, "command.balance.add", PermissionTypes.BOOLEAN, EightsPermissions::defaultTwo);
    public static final PermissionNode<Boolean> REMOVE = new PermissionNode<>(MODID, "command.balance.remove", PermissionTypes.BOOLEAN, EightsPermissions::defaultTwo);
    public static final PermissionNode<Boolean> SET = new PermissionNode<>(MODID, "command.balance.set", PermissionTypes.BOOLEAN, EightsPermissions::defaultTwo);
    public static final PermissionNode<Boolean> PAY = new PermissionNode<>(MODID, "command.balance.pay", PermissionTypes.BOOLEAN, EightsPermissions::defaultTrue);


    @SubscribeEvent
    public void registerPermissions(PermissionGatherEvent.Nodes event) {
        event.addNodes(CHECK, CHECK_OTHER, ADD, REMOVE, SET, PAY);
    }


    private static Boolean defaultFour(ServerPlayer player, UUID playerUUID, PermissionDynamicContext<?>... context) {
        return player == null || player.hasPermissions(4);
    }

    private static Boolean defaultThree(ServerPlayer player, UUID playerUUID, PermissionDynamicContext<?>... context) {
        return player == null || player.hasPermissions(3);
    }

    private static Boolean defaultTwo(ServerPlayer player, UUID playerUUID, PermissionDynamicContext<?>... context) {
        return player == null || player.hasPermissions(2);
    }

    private static Boolean defaultTrue(ServerPlayer player, UUID playerUUID, PermissionDynamicContext<?>... context) {
        return true;
    }
}
