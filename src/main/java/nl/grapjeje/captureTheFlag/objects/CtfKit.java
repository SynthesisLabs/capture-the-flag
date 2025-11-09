package nl.grapjeje.captureTheFlag.objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import nl.grapjeje.captureTheFlag.Main;
import nl.grapjeje.captureTheFlag.enums.Kit;
import nl.grapjeje.core.gui.Gui;
import nl.grapjeje.core.gui.GuiButton;
import nl.grapjeje.core.registry.AutoRegistry;
import nl.grapjeje.core.registry.Registry;
import nl.grapjeje.core.text.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;

import java.util.Arrays;

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
                    .build();

            builder.withButton(i, button);
        }
        Gui gui = builder.build();
        this.registerGui(gui);
        gui.open(player.getPlayer());
    }

    protected void registerGui(Gui gui) {
        if (gui instanceof Listener listener)
            Bukkit.getServer().getPluginManager().registerEvents(listener, Main.getInstance());
    }

//    public static void apply(@NotNull CtfPlayer player) {
//        switch (player.getKit()){
//            case SCOUT -> {
//                giveItems(player,
//                        new ItemStack(Material.IRON_SWORD),
//                        new ItemStack(Material.BOW),
//                    new ItemStack(Material.ARROW, 16)
//                );
//                giveArmor(player,
//                        new ItemStack(Material.LEATHER_HELMET),
//                        new ItemStack(Material.LEATHER_CHESTPLATE),
//                        new ItemStack(Material.LEATHER_LEGGINGS),
//                        new ItemStack(Material.LEATHER_BOOTS)
//                );
//                player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
//            }
//
//            case SOLDIER -> {
//                giveItems(player,
//                        new ItemStack(Material.IRON_SWORD),
//                        new ItemStack(Material.BOW),
//                        new ItemStack(Material.ARROW, 24)
//                );
//                giveArmor(player,
//                        new ItemStack(Material.IRON_HELMET),
//                        new ItemStack(Material.IRON_CHESTPLATE),
//                        new ItemStack(Material.IRON_LEGGINGS),
//                        new ItemStack(Material.IRON_BOOTS)
//                );
//            }
//
//            case HEAVY -> {
//                giveItem(player,
//                        new ItemStack(Material.IRON_AXE)
//                );
//                giveArmor(player,
//                        new ItemStack(Material.IRON_HELMET),
//                        new ItemStack(Material.IRON_CHESTPLATE),
//                        new ItemStack(Material.IRON_LEGGINGS),
//                        new ItemStack(Material.IRON_BOOTS)
//                );
//                player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 0));
//                player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, 0));
//            }
//
//            case SNIPER -> {
//                giveItem(player,
//                        new ItemStack(Material.BOW),
//                        new ItemStack(Material.ARROW, 64)
//                        );
//                giveArmor(player,
//                        new ItemStack(Material.LEATHER_HELMET),
//                        new ItemStack(Material.LEATHER_CHESTPLATE),
//                        new ItemStack(Material.LEATHER_LEGGINGS),
//                        new ItemStack(Material.LEATHER_BOOTS)
//                );
//            }
//
//            case BESERKER -> {
//                giveItems(player,
//                        new ItemStack(Material.IRON_AXE)
//                );
//                giveArmor(player,
//                        new ItemStack(Material.CHAINMAIL_HELMET),
//                        new ItemStack(Material.CHAINMAIL_CHESTPLATE),
//                        new ItemStack(Material.CHAINMAIL_LEGGINGS),
//                        new ItemStack(Material.CHAINMAIL_BOOTS)
//                );
//            }
//        }
//    }
}
