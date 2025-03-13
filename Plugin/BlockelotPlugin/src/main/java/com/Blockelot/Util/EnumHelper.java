package com.Blockelot.Util;

import java.util.logging.Logger;
import org.bukkit.DyeColor;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;

/**
 *
 * @author geev
 */
public class EnumHelper {

    //Returns the enum value for the string name of an entity passed in.
    public static EntityType getEntityType(String name) {
        for (EntityType e : EntityType.values()) {
            if (e.name().equals(name)) {
                return e;
            }
        }
        return EntityType.CHICKEN;
    }

    //Return the enum value for the color string passed in.
    public static DyeColor GetDyeColor(String color) {
        for (DyeColor c : DyeColor.values()) {
            if (c.name().equals(color)) {
                return c;
            }
        }
        return DyeColor.RED;
    }

    //Because my code can rotate the paste in any direction I needed
    //an enumeration ability to specify block facing friendly.
    //This is so a string like -0+ will translate to a facing, etc.
    //This function takes the string and converts it to a block facing.
    public static BlockFace ToBlockFaceFromCode(String code) {
        if (code.length() != 3) {
            return BlockFace.SELF;
        }
        char[] let = code.toCharArray();
        int x = 0;
        int y = 0;
        int z = 0;
        switch (let[0]) {
            case '-' -> {
                x = -1;
            }
            case '+' -> {
                x = 1;
            }
            case '0' -> {
                x = 0;
            }
        }
        switch (let[1]) {
            case '-' -> {
                y = -1;
            }
            case '+' -> {
                y = 1;
            }
            case '0' -> {
                y = 0;
            }
        }
        switch (let[2]) {
            case '-' -> {
                z = -1;
            }
            case '+' -> {
                z = 1;
            }
            case '0' -> {
                z = 0;
            }
        }
        return MaterialUtil.getFacingByMod(x, y, z);
    }

    //This function takes a blockfacing and converts it to a string representation.
    //Which can be read in by the code above.
    public static String ToCodeFromBlockFace(BlockFace bf) {
        int x = bf.getModX();
        int y = bf.getModY();
        int z = bf.getModZ();
        String xc = "*";
        String yc = "*";
        String zc = "*";
        switch (x) {
            case 0 -> {
                xc = "0";
            }
            case 1 -> {
                xc = "+";
            }
            case -1 -> {
                xc = "-";
            }
        }
        switch (y) {
            case 0 -> {
                yc = "0";
            }
            case 1 -> {
                yc = "+";
            }
            case -1 -> {
                yc = "-";
            }
        }
        switch (z) {
            case 0 -> {
                zc = "0";
            }
            case 1 -> {
                zc = "+";
            }
            case -1 -> {
                zc = "-";
            }
        }
        return xc + yc + zc;
    }
    private static final Logger LOG = Logger.getLogger(EnumHelper.class.getName());
}
