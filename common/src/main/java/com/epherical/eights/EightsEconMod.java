package com.epherical.eights;

import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

public abstract class EightsEconMod {

    public static final Style CONSTANTS_STYLE = Style.EMPTY.withColor(TextColor.parseColor("#999999").getOrThrow());
    public static final Style VARIABLE_STYLE = Style.EMPTY.withColor(TextColor.parseColor("#ffd500").getOrThrow());
    public static final Style APPROVAL_STYLE = Style.EMPTY.withColor(TextColor.parseColor("#6ba4ff").getOrThrow());
    public static final Style ERROR_STYLE = Style.EMPTY.withColor(TextColor.parseColor("#b31717").getOrThrow());

    public static final String MOD_ID = "eights_economy_p";

    public static final String CHECK = MOD_ID + ".command.balance.check";
    public static final String CHECK_OTHER = MOD_ID + ".command.balance.check.other";
    public static final String ADD = MOD_ID + ".command.balance.add";
    public static final String REMOVE = MOD_ID + ".command.balance.remove";
    public static final String SET = MOD_ID + ".command.balance.set";
    public static final String PAY = MOD_ID + ".command.balance.pay";
    public static final String BALTOP = MOD_ID + ".command.balance.top";

    public static final int OP_CHECK = 0;
    public static final int OP_CHECK_OTHER = 0;
    public static final int OP_ADD = 4;
    public static final int OP_REMOVE = 4;
    public static final int OP_SET = 4;
    public static final int OP_PAY = 0;
    public static final int OP_BALTOP = 0;

}
