package com.example.slashdelay;

import com.destroystokyo.paper.event.brigadier.AsyncPlayerSendCommandsEvent;
import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.RootCommandNode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class SlashDelay extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    /**
     * Replace the command tree sent to the client with one greedy argument
     * whose suggestions come from the server. The client can no longer build
     * the list locally, so it asks the server on every keystroke.
     * The server's real command tree is untouched, so commands still run.
     */
    @EventHandler
    public void onSendCommands(AsyncPlayerSendCommandsEvent<?> event) {
        if (event.getPlayer().hasPermission("slashdelay.bypass")) return;
        rewrite(event.getCommandNode());
    }

    private <S> void rewrite(RootCommandNode<S> root) {
        root.getChildren().clear();
        root.addChild(
            RequiredArgumentBuilder.<S, String>argument("cmd", StringArgumentType.greedyString())
                .suggests((ctx, builder) -> builder.buildFuture())
                .build()
        );
    }

    /** Return nothing for a bare "/", normal completions otherwise. */
    @EventHandler
    public void onTab(AsyncTabCompleteEvent event) {
        if (!(event.getSender() instanceof Player player)) return;
        if (player.hasPermission("slashdelay.bypass")) return;
        if (event.getBuffer().trim().equals("/")) {
            event.setCompletions(List.of());
            event.setHandled(true);
        }
    }
}
