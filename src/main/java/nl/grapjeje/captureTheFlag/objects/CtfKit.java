package nl.grapjeje.captureTheFlag.objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import nl.grapjeje.captureTheFlag.Main;
import nl.grapjeje.captureTheFlag.enums.Kit;
import nl.grapjeje.core.gui.Gui;
import nl.grapjeje.core.gui.GuiButton;
import nl.grapjeje.core.items.Item;
import nl.grapjeje.core.registry.AutoRegistry;
import nl.grapjeje.core.registry.Registry;
import nl.grapjeje.core.text.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@AutoRegistry
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CtfKit {
    private final CtfPlayer player;

    public static CtfKit get(CtfPlayer player) {
        return Registry.get(
                CtfKit.class,
                player.getPlayer().getUniqueId().toString(),
                (args) -> new CtfKit((CtfPlayer) args[0]),
                player
        );
    }

    public void open() {
        Gui.Builder builder = Gui.builder(InventoryType.CHEST, Component.text("Select a kit"));
        builder.withSize(27);

        Kit[] kits = Kit.values();
        for (int i = 0; i < kits.length; i++) {
            Kit kit = kits[i];
            GuiButton button = GuiButton.builder()
                    .withMaterial(kit.getMaterial())
                    .withName(MessageUtil.filterMessage(kit.getColorCode() + MessageUtil.capitalizeWords(kit.name().toLowerCase())))
                    .withLore(MessageUtil.filterMessage("<gray>Click to equip kit"))
                    .withClickEvent((g, p, c) -> this.apply(kit))
                    .build();

            builder.withButton(i, button);
        }
        Gui gui = builder.build();
        gui.register();
        gui.open(player.getPlayer());
    }

    private void apply(Kit kit) {

        player.getPlayer().closeInventory();
        player.getPlayer().getInventory().clear();
        player.getPlayer().getActivePotionEffects().forEach(effect ->
                player.getPlayer().removePotionEffect(effect.getType())
        );

        switch (kit) {
            case SCOUT -> {
                giveItems(
                        Item.from(Material.IRON_SWORD).toBukkit(),
                        Item.from(Material.BOW).toBukkit(),
                        Item.from(Material.ARROW).toBukkit()
                );

                giveArmor(
                        Item.from(Material.LEATHER_HELMET).toBukkit(),
                        Item.from(Material.LEATHER_CHESTPLATE).toBukkit(),
                        Item.from(Material.LEATHER_LEGGINGS).toBukkit(),
                        Item.from(Material.LEATHER_BOOTS).toBukkit()
                );

                giveEffects(
                        new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1)
                );
            }
            case SOLDIER -> {
                giveItems(
                        Item.from(Material.IRON_SWORD).toBukkit(),
                        Item.from(Material.BOW).toBukkit(),
                        Item.from(Material.ARROW).toBukkit()
                );

                giveArmor(
                        Item.from(Material.IRON_HELMET).toBukkit(),
                        Item.from(Material.IRON_CHESTPLATE).toBukkit(),
                        Item.from(Material.IRON_LEGGINGS).toBukkit(),
                        Item.from(Material.IRON_BOOTS).toBukkit()
                );
            }

            case HEAVY -> {
                giveItems(
                        Item.from(Material.IRON_AXE).toBukkit(),
                        Item.from(Material.SHIELD).toBukkit()
                );

                giveArmor(
                        Item.from(Material.IRON_HELMET).toBukkit(),
                        Item.from(Material.IRON_CHESTPLATE).toBukkit(),
                        Item.from(Material.IRON_LEGGINGS).toBukkit(),
                        Item.from(Material.IRON_BOOTS).toBukkit()
                );

                giveEffects(
                        new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 0),
                        new PotionEffect(PotionEffectType.ABSORPTION, Integer.MAX_VALUE, 1)
                );
            }

            case SNIPER -> {
                giveItems(
                        Item.from(Material.BOW).toBukkit(),
                        Item.from(Material.ARROW).toBukkit()
                );

                giveArmor(
                        Item.from(Material.LEATHER_HELMET).toBukkit(),
                        Item.from(Material.LEATHER_CHESTPLATE).toBukkit(),
                        Item.from(Material.LEATHER_LEGGINGS).toBukkit(),
                        Item.from(Material.LEATHER_BOOTS).toBukkit()
                );

                giveEffects(
                        new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 0)
                );
            }

            case BESERKER -> {
                giveItems(
                        Item.from(Material.IRON_AXE).toBukkit()
                );

                giveArmor(
                        Item.from(Material.CHAINMAIL_HELMET).toBukkit(),
                        Item.from(Material.CHAINMAIL_CHESTPLATE).toBukkit(),
                        Item.from(Material.CHAINMAIL_LEGGINGS).toBukkit(),
                        Item.from(Material.CHAINMAIL_BOOTS).toBukkit()
                );

                giveEffects(
                        new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 0)
                );
            }
        }

    }
    private void giveItems(ItemStack... items) {
        player.getPlayer().getInventory().addItem(items);
    }

    private void giveArmor(ItemStack helmet, ItemStack chest, ItemStack legs, ItemStack boots) {
        player.getPlayer().getInventory().setHelmet(helmet);
        player.getPlayer().getInventory().setChestplate(chest);
        player.getPlayer().getInventory().setLeggings(legs);
        player.getPlayer().getInventory().setBoots(boots);
    }

    private void giveEffects(PotionEffect... effects) {
        for (PotionEffect effect : effects) {
            player.getPlayer().addPotionEffect(effect);
        }
    }

//
}
