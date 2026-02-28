package com.epherical.eights;

import com.epherical.eights.user.PlayerUser;
import com.epherical.octoecon.api.OctoEconomy;
import com.epherical.octoecon.api.user.UniqueUser;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BalanceMethods {

    private static final int BALTOP_PAGE_SIZE = 10;
    private static final ResourceLocation DEFAULT_CHAT_FONT = ResourceLocation.withDefaultNamespace("default");

    private static ResourceLocation configFont = null;

    private static OctoEconomy<?, ?> provider;

    public static void applyProvider(OctoEconomy<?, ?> economyProvider) {
        provider = economyProvider;
    }

    public static @Nullable OctoEconomy<?, ?> getProvider() {
        return provider;
    }

    protected static int checkBalance(CommandContext<CommandSourceStack> context, ServerPlayer player) {
        if (provider == null) {
            return 0;
        }

        UniqueUser user = provider.getOrCreatePlayerAccount(player.getUUID());
        double balance = user.getBalance("command.balance.check");
        Component text = moneyComponent(balance);
        Component playerName = Component.literal(player.getScoreboardName()).setStyle(EightsEconMod.VARIABLE_STYLE);
        Component actualMessage = Component.translatable("%s has %s.", playerName, text).setStyle(EightsEconMod.APPROVAL_STYLE);
        context.getSource().sendSuccess(() -> actualMessage, true);

        return 1;
    }

    protected static int addMoney(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        if (provider == null) {
            return 0;
        }

        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        int amount = IntegerArgumentType.getInteger(context, "amount");
        UniqueUser user = provider.getOrCreatePlayerAccount(player.getUUID());
        user.depositMoney(amount, "command.balance.add");

        Component playerName = pluralize(player.getScoreboardName(), EightsEconMod.VARIABLE_STYLE);
        Component component = Component.translatable("Added %s to %s account.", moneyComponent(amount), playerName).setStyle(EightsEconMod.APPROVAL_STYLE);
        context.getSource().sendSuccess(() -> component, false);

        return 1;
    }

    protected static int removeMoney(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        if (provider == null) {
            return 0;
        }

        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        int amount = IntegerArgumentType.getInteger(context, "amount");
        UniqueUser user = provider.getOrCreatePlayerAccount(player.getUUID());
        user.withdrawMoney(amount, "command.balance.remove");

        Component playerName = pluralize(player.getScoreboardName(), EightsEconMod.VARIABLE_STYLE);
        Component component = Component.translatable("Removed %s from %s account.", moneyComponent(amount), playerName).setStyle(EightsEconMod.APPROVAL_STYLE);
        context.getSource().sendSuccess(() -> component, false);

        return 1;
    }

    protected static int setMoney(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        if (provider == null) {
            return 0;
        }

        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        int amount = IntegerArgumentType.getInteger(context, "amount");
        UniqueUser user = provider.getOrCreatePlayerAccount(player.getUUID());
        user.setBalance(amount, "command.balance.set");

        Component playerName = pluralize(player.getScoreboardName(), EightsEconMod.VARIABLE_STYLE);
        Component component = Component.translatable("Set money to %s in %s account.", moneyComponent(amount), playerName).setStyle(EightsEconMod.APPROVAL_STYLE);
        context.getSource().sendSuccess(() -> component, false);

        return 1;
    }

    protected static int payMoney(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        if (provider == null) {
            return 0;
        }

        ServerPlayer source = EntityArgument.getPlayer(context, "player");
        ServerPlayer target = EntityArgument.getPlayer(context, "target");
        int amount = IntegerArgumentType.getInteger(context, "amount");
        UniqueUser sourceUser = provider.getOrCreatePlayerAccount(source.getUUID());
        UniqueUser targetUser = provider.getOrCreatePlayerAccount(target.getUUID());

        if (source.equals(target)) {
            Component message = Component.literal("You can't send money to yourself!").setStyle(EightsEconMod.ERROR_STYLE);
            context.getSource().sendFailure(message);
            return 0;
        }

        if (!sourceUser.hasAmount(amount, "command.balance.pay.check")) {
            Component message = Component.literal("You do not have enough money.").setStyle(EightsEconMod.ERROR_STYLE);
            context.getSource().sendFailure(message);
            return 0;
        }

        sourceUser.sendTo(targetUser, amount, "command.balance.pay");
        Component targetName = Component.literal(target.getScoreboardName()).withStyle(EightsEconMod.VARIABLE_STYLE);
        Component sourceName = Component.literal(source.getScoreboardName()).withStyle(EightsEconMod.VARIABLE_STYLE);
        Component amountComponent = moneyComponent(amount);
        Component sourceMessage = Component.translatable("%s sent %s to %s!", sourceName, amountComponent, targetName).setStyle(EightsEconMod.APPROVAL_STYLE);
        Component targetMessage = Component.translatable("You have received %s from %s!", amountComponent, sourceName).setStyle(EightsEconMod.APPROVAL_STYLE);
        context.getSource().sendSuccess(() -> sourceMessage, true);
        target.sendSystemMessage(targetMessage);
        return 1;
    }

    protected static int balTop(CommandContext<CommandSourceStack> context) {
        return balTop(context, 1);
    }

    protected static int balTopPage(CommandContext<CommandSourceStack> context) {
        int page = IntegerArgumentType.getInteger(context, "page");
        return balTop(context, page);
    }

    private static int balTop(CommandContext<CommandSourceStack> context, int requestedPage) {
        if (!(provider instanceof EightsEconomyProvider eightsProvider)) {
            context.getSource().sendFailure(Component.literal("Economy provider does not support baltop."));
            return 0;
        }

        List<PlayerUser> sorted = eightsProvider.getTopPlayersByBalance();
        int total = sorted.size();
        int maxPages = Math.max(1, (int) Math.ceil(total / (double) BALTOP_PAGE_SIZE));
        int page = Math.max(1, Math.min(requestedPage, maxPages));

        int start = (page - 1) * BALTOP_PAGE_SIZE;
        int end = Math.min(total, start + BALTOP_PAGE_SIZE);
        ResourceLocation baltopFont = resolveBaltopFont();

        Style monoBase = Style.EMPTY.withFont(baltopFont).withColor(EightsEconMod.CONSTANTS_STYLE.getColor());
        context.getSource().sendSuccess(() -> Component.literal(" ").withStyle(monoBase), false);
        context.getSource().sendSuccess(() -> Component.literal("== Balance Top ==").withStyle(monoBase), false);

        if (total == 0) {
            context.getSource().sendSuccess(() -> Component.literal("No users found.").withStyle(monoBase), false);
        } else {
            for (int i = start; i < end; i++) {
                PlayerUser user = sorted.get(i);
                int rank = i + 1;
                MutableComponent line = Component.literal(String.format("%2d. %-16s ", rank, user.getIdentity())).withStyle(monoBase);
                line.append(moneyComponent(user.getBalance("command.baltop.display")).copy().withStyle(Style.EMPTY.withFont(baltopFont).withColor(EightsEconMod.VARIABLE_STYLE.getColor())));
                context.getSource().sendSuccess(() -> line, false);
            }
        }

        context.getSource().sendSuccess(() -> navigationRow(page, maxPages, baltopFont), false);
        return 1;
    }

    private static MutableComponent navigationRow(int page, int maxPages, ResourceLocation font) {
        MutableComponent root = Component.literal("").withStyle(Style.EMPTY.withFont(font).withColor(EightsEconMod.CONSTANTS_STYLE.getColor()));
        root.append(pageButton("[<<]", 1, page > 1, font));
        root.append(Component.literal("-"));
        root.append(pageButton("[<]", page - 1, page > 1, font));
        root.append(Component.literal("--"));
        root.append(Component.literal(page + "/" + maxPages).withStyle(Style.EMPTY.withFont(font).withColor(EightsEconMod.VARIABLE_STYLE.getColor())));
        root.append(Component.literal("--"));
        root.append(pageButton("[>]", page + 1, page < maxPages, font));
        root.append(Component.literal("-"));
        root.append(pageButton("[>>]", maxPages, page < maxPages, font));
        return root;
    }

    private static MutableComponent pageButton(String text, int targetPage, boolean enabled, ResourceLocation font) {
        Style style = Style.EMPTY.withFont(font)
                .withColor((enabled ? EightsEconMod.APPROVAL_STYLE : EightsEconMod.ERROR_STYLE).getColor());
        MutableComponent component = Component.literal(text).withStyle(style);
        if (enabled) {
            component.withStyle(style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/baltop " + targetPage)));
        }
        return component;
    }

    private static ResourceLocation resolveBaltopFont() {
        String configured = ConfigConstants.getInstance().baltopFont;
        if (configFont != null) {
            return configFont;
        }
        if (configured == null || configured.isBlank()) {
            return DEFAULT_CHAT_FONT;
        }

        String trim = configured.trim();
        try {
            return configFont = trim.contains(":") ? ResourceLocation.parse(trim) : ResourceLocation.withDefaultNamespace(trim);
        } catch (Exception ignored) {
            return DEFAULT_CHAT_FONT;
        }
    }

    private static Component moneyComponent(double amount) {
        return Component.literal(String.format("$%.2f", amount)).setStyle(EightsEconMod.VARIABLE_STYLE);
    }

    private static Component pluralize(String name, Style style) {
        name = name.endsWith("s") ? name + "'" : name + "'s";
        return Component.literal(name).setStyle(style);
    }
}
