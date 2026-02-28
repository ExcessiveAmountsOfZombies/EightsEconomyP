package com.epherical.eights.permissions;

import net.minecraft.commands.CommandSourceStack;

@FunctionalInterface
public interface PermissionChecker {

    boolean hasPermission(CommandSourceStack source, String permissionNode, int fallbackOpLevel);
}
