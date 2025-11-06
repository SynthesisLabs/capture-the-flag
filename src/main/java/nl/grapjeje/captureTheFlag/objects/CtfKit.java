package nl.grapjeje.captureTheFlag.objects;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static nl.grapjeje.captureTheFlag.enums.Kit.*;

public class CtfKit {
    public static void apply(CtfPlayer player) {
        switch (player.getKit()){
            case SCOUT -> {
                giveItems(player,
                        new ItemStack(Material.IRON_SWORD),
                        new ItemStack(Material.BOW),
                    new ItemStack(Material.ARROW, 16)
                );
                giveArmor(player,
                        new ItemStack(Material.LEATHER_HELMET),
                        new ItemStack(Material.LEATHER_CHESTPLATE),
                        new ItemStack(Material.LEATHER_LEGGINGS),
                        new ItemStack(Material.LEATHER_BOOTS)
                );
                player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
            }

            case SOLDIER -> {
                giveItems(player,
                        new ItemStack(Material.IRON_SWORD),
                        new ItemStack(Material.BOW),
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
                        new ItemStack(Material.IRON_AXE)
                );
                giveArmor(player,
                        new ItemStack(Material.IRON_HELMET),
                        new ItemStack(Material.IRON_CHESTPLATE),
                        new ItemStack(Material.IRON_LEGGINGS),
                        new ItemStack(Material.IRON_BOOTS)
                );
                player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 0));
                player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, 0));
            }

            case SNIPER -> {
                giveItem(player,
                        new ItemStack(Material.BOW),
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
                        new ItemStack(Material.IRON_AXE)
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
