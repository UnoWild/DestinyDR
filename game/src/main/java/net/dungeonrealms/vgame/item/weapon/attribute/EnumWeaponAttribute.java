package net.dungeonrealms.vgame.item.weapon.attribute;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.dungeonrealms.api.collection.AtomicCollection;
import net.dungeonrealms.vgame.item.EnumItemTier;
import net.dungeonrealms.vgame.item.AttributeMeta;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

/**
 * Created by Giovanni on 29-10-2016.
 * <p>
 * This file is part of the Dungeon Realms project.
 * Copyright (c) 2016 Dungeon Realms;www.vawke.io / development@vawke.io
 */
public enum EnumWeaponAttribute
{
    /**
     * % List
     * <p>
     * CRIT
     * MON_DMG
     * PLAYER_DMG
     * LIFE_STEAL
     * ACCURACY
     */

    PURE_DAMAGE("PURE DMG",
            new AttributeMeta(EnumItemTier.ONE, 1, 5),
            new AttributeMeta(EnumItemTier.TWO, 1, 8),
            new AttributeMeta(EnumItemTier.THREE, 1, 15),
            new AttributeMeta(EnumItemTier.FOUR, 1, 25),
            new AttributeMeta(EnumItemTier.FIVE, 1, 45)),

    CRITICAL_HIT("CRIT. CHANCE",
            new AttributeMeta(EnumItemTier.ONE, 1, 2, true),
            new AttributeMeta(EnumItemTier.TWO, 1, 4, true),
            new AttributeMeta(EnumItemTier.THREE, 1, 5, true),
            new AttributeMeta(EnumItemTier.FOUR, 1, 7, true),
            new AttributeMeta(EnumItemTier.FIVE, 1, 10, true)),


    PENETRATION("ARMOR PEN.",
            new AttributeMeta(EnumItemTier.ONE, 1, 1, true),
            new AttributeMeta(EnumItemTier.TWO, 1, 3, true),
            new AttributeMeta(EnumItemTier.THREE, 1, 5, true),
            new AttributeMeta(EnumItemTier.FOUR, 1, 8, true),
            new AttributeMeta(EnumItemTier.FIVE, 1, 10, true)),

    MON_DMG("MONSTER DMG",
            new AttributeMeta(EnumItemTier.ONE, 1, 10),
            new AttributeMeta(EnumItemTier.TWO, 1, 12),
            new AttributeMeta(EnumItemTier.THREE, 1, 15),
            new AttributeMeta(EnumItemTier.FOUR, 1, 20),
            new AttributeMeta(EnumItemTier.FIVE, 1, 27)),

    PLAYER_DMG("PLAYER DMG",
            new AttributeMeta(EnumItemTier.ONE, 1, 10),
            new AttributeMeta(EnumItemTier.TWO, 1, 12),
            new AttributeMeta(EnumItemTier.THREE, 1, 15),
            new AttributeMeta(EnumItemTier.FOUR, 1, 20),
            new AttributeMeta(EnumItemTier.FIVE, 1, 27)),

    LIFE_STEAL("LIFE STEAL",
            new AttributeMeta(EnumItemTier.ONE, 1, 30),
            new AttributeMeta(EnumItemTier.TWO, 1, 15),
            new AttributeMeta(EnumItemTier.THREE, 1, 12),
            new AttributeMeta(EnumItemTier.FOUR, 1, 7),
            new AttributeMeta(EnumItemTier.FIVE, 1, 8)),

    ICE_DAMAGE("ICE DMG",
            new AttributeMeta(EnumItemTier.ONE, 1, 4),
            new AttributeMeta(EnumItemTier.TWO, 1, 9),
            new AttributeMeta(EnumItemTier.THREE, 1, 15),
            new AttributeMeta(EnumItemTier.FOUR, 1, 25),
            new AttributeMeta(EnumItemTier.FIVE, 1, 55)),

    FIRE_DAMAGE("FIRE DMG",
            new AttributeMeta(EnumItemTier.ONE, 1, 4),
            new AttributeMeta(EnumItemTier.TWO, 1, 9),
            new AttributeMeta(EnumItemTier.THREE, 1, 15),
            new AttributeMeta(EnumItemTier.FOUR, 1, 25),
            new AttributeMeta(EnumItemTier.FIVE, 1, 55)),

    POISON_DAMAGE("POISON DMG",
            new AttributeMeta(EnumItemTier.ONE, 1, 4),
            new AttributeMeta(EnumItemTier.TWO, 1, 9),
            new AttributeMeta(EnumItemTier.THREE, 1, 15),
            new AttributeMeta(EnumItemTier.FOUR, 1, 25),
            new AttributeMeta(EnumItemTier.FIVE, 1, 55)),

    ACCURACY("ACCURACY",
            new AttributeMeta(EnumItemTier.ONE, 1, 10),
            new AttributeMeta(EnumItemTier.TWO, 1, 12),
            new AttributeMeta(EnumItemTier.THREE, 1, 25),
            new AttributeMeta(EnumItemTier.FOUR, 1, 28),
            new AttributeMeta(EnumItemTier.FIVE, 1, 35));

    @Getter
    private String name;

    @Getter
    private AttributeMeta[] attributeMetas;

    private static AtomicCollection<EnumWeaponAttribute> atomicCollection = new AtomicCollection<>();

    private static boolean loaded;

    EnumWeaponAttribute(String name, AttributeMeta... metas)
    {
        this.name = ChatColor.RED + name;
        this.attributeMetas = metas;
    }

    public static List<EnumWeaponAttribute> random(int maxAttributes)
    {
        AtomicReference<List<EnumWeaponAttribute>> attributeList;
        if (loaded)
        {
            attributeList = new AtomicReference<List<EnumWeaponAttribute>>(); // Create a new list per process
            attributeList.set(Lists.newArrayList());
            if (attributeList.get().isEmpty()) // Better be safe than sorry
            {
                IntStream.range(0, maxAttributes).parallel().forEach(max -> {
                    attributeList.get().add(atomicCollection.next());
                });
                return attributeList.get();
            }
        } else
        {
            // Weight is not final
            for (EnumWeaponAttribute enumWeaponAttribute : EnumWeaponAttribute.values())
                atomicCollection.getMap().get().put(0.5, enumWeaponAttribute);

            // Always add an empty entry
            atomicCollection.getMap().get().put(0.8, null);
            loaded = true;
            return random(maxAttributes); // Redo the process
        }
        return null;
    }
}
