package com.swill.hitbox;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import org.lwjgl.glfw.GLFW;

public class HitboxMod implements ClientModInitializer {

    private static boolean enabled = false;
    private static final double MULTIPLIER = 2.5;

    @Override
    public void onInitializeClient() {
        KeyBinding toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Visual Hitbox", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, "Hitbox"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;

            if (toggleKey.wasPressed()) {
                enabled = !enabled;
                client.player.sendMessage(Text.literal("§7[§cH§7] " + (enabled ? "§aON" : "§cOFF")), true);
            }

            if (!enabled) return;

            for (Entity entity : client.world.getEntities()) {
                if (entity == client.player) continue;
                if (!(entity instanceof LivingEntity)) continue;
                if (entity instanceof PlayerEntity p && (p.isCreative() || p.isSpectator())) continue;

                Box original = entity.getBoundingBox();
                double width = original.getXLength() * MULTIPLIER;
                double height = original.getYLength() * MULTIPLIER;

                double cx = (original.minX + original.maxX) / 2;
                double cy = (original.minY + original.maxY) / 2;
                double cz = (original.minZ + original.maxZ) / 2;

                Box newBox = new Box(
                    cx - width / 2,
                    cy - height / 2,
                    cz - width / 2,
                    cx + width / 2,
                    cy + height / 2,
                    cz + width / 2
                );
                entity.setBoundingBox(newBox);
            }
        });
    }
}
