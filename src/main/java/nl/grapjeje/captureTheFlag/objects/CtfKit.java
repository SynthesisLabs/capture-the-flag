package nl.grapjeje.captureTheFlag.objects;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static nl.grapjeje.captureTheFlag.enums.Kit.*;

public class CtfKit {
    public static void apply(CtfPlayer player) {
        switch (kit){
            case SCOUT -> {
                giveItems(player,
                    makeItem(Material.IRON_SWORD, "Scout sword"),
                    makeItem(Material.BOW, "Scout Bow"),
                    new ItemStack(Material.ARROW, 16)
                );
                giveArmor(player,
                        new ItemStack(Material.LEATHER_HELMET),
                        new ItemStack(Material.LEATHER_CHESTPLATE),
                        new ItemStack(Material.LEATHER_LEGGINGS),
                        new ItemStack(Material.LEATHER_BOOTS)
                );
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 72000, 0));
            }

            case SOLDIER -> {
                giveItems(player,
                        makeItem(Material.IRON_SWORD, "soldier sword"),
                        makeItem(Material.BOW, "Soldier bow"),
                        new ItemStack(Material.ARROW, 24)
                );
                giveArmor(player,
                        new ItemStack(Material.IRON_HELMET),
                        new ItemStack(Material.IRON_CHESTPLATE),
                        new ItemStack(Material.IRON_LEGGINGS),
                        new ItemStack(Material.IRON_BOOTS)
                );
            }

            case HEAVY -> {
                giveItem(player,
                        makeItem(Material.IRON_AXE, "Heavy axe")
                );
                giveArmor(player,
                        new ItemStack(Material.IRON_HELMET),
                        new ItemStack(Material.IRON_CHESTPLATE),
                        new ItemStack(Material.IRON_LEGGINGS),
                        new ItemStack(Material.IRON_BOOTS)
                );
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 72000, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 72000, 0));
            }

            case SNIPER -> {
                giveItem(player,
                        makeItem(Material.BOW, "sniper bow"),
                        new ItemStack(Material.ARROW, 64)
                        );
                giveArmor(player,
                        new ItemStack(Material.LEATHER_HELMET),
                        new ItemStack(Material.LEATHER_CHESTPLATE),
                        new ItemStack(Material.LEATHER_LEGGINGS),
                        new ItemStack(Material.LEATHER_BOOTS)
                );
            }

            case BESERKER -> {
                giveItems(player,
                        makeItem(Material.IRON_AXE, "Berserker Axe")
                );
                giveArmor(player,
                        new ItemStack(Material.CHAINMAIL_HELMET),
                        new ItemStack(Material.CHAINMAIL_CHESTPLATE),
                        new ItemStack(Material.CHAINMAIL_LEGGINGS),
                        new ItemStack(Material.CHAINMAIL_BOOTS)
                );
            }
        }
    }
}
