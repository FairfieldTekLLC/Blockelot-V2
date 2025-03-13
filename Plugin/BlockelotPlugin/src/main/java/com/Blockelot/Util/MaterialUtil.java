package com.Blockelot.Util;

import java.util.logging.Logger;
import org.bukkit.block.BlockFace;

public class MaterialUtil {

    //Returns the block face by name
    public static BlockFace getFacingByName(String name) {
        for (BlockFace face : BlockFace.values()) {
            if (!face.name().equals(name)) {
                continue;
            }
            return face;
        }
        return null;
    }

    public static BlockFace getFacingByMod(int x, int y, int z) {
        for (BlockFace face : BlockFace.values()) {
            if (face.getModX() != x || face.getModY() != y || face.getModZ() != z) {
                continue;
            }
            return face;
        }
        return null;
    }
    private static final Logger LOG = Logger.getLogger(MaterialUtil.class.getName());

}
