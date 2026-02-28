package com.epherical.eights.permissions;

import net.minecraft.commands.CommandSourceStack;

import java.util.Objects;
import java.util.function.Predicate;

public class CommonPermissionBridge {

    private static PermissionChecker checker = (source, permissionNode, fallbackOpLevel) -> source.hasPermission(fallbackOpLevel);

    public static void setChecker(PermissionChecker permissionChecker) {
        checker = Objects.requireNonNull(permissionChecker);
    }

    public static boolean has(CommandSourceStack source, String permissionNode, int fallbackOpLevel) {
        return checker.hasPermission(source, permissionNode, fallbackOpLevel);
    }

    public static Predicate<CommandSourceStack> require(String permissionNode, int fallbackOpLevel) {
        return source -> has(source, permissionNode, fallbackOpLevel);
    }
}
