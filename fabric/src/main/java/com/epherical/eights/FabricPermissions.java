package com.epherical.eights;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;

public final class FabricPermissions {

    private FabricPermissions() {
    }

    public static boolean has(CommandSourceStack source, String permissionNode, int fallbackOpLevel) {
        return Permissions.check(source, permissionNode, source.hasPermission(fallbackOpLevel));
    }
}
