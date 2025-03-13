package com.Blockelot.Util;

import static java.lang.Integer.parseInt;
import org.bukkit.Material;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class MiscUtil {

    public static String padRight(String s, int n, String character) {
        String newString = s.trim();
        for (int i = newString.length(); i <= n; i++) {
            newString = newString + character;
        }
        return newString;
    }

    public static String padLeft(String s, int n, String character) {
        String newString = s.trim();
        for (int i = newString.length(); i <= n; i++) {
            newString = character + newString;
        }
        return newString;
    }

    public static void DumpStringArray(String[] args) {
        for (String line : args) {
            ServerUtil.consoleLog(line);
        }
    }

    //I am not sure why I originally didn't allow the objects to be deposited.
    //There is no real reason for it except the command_block.
    public static boolean CanBeDeposited(Material mat) {
        //if (!mat.isBlock()) {
//            return false;
//        }
//        if (mat == Material.COMPARATOR) {
//            return false;
//        }
//        if (mat == Material.REPEATER) {
//            return false;
//        }
//        if (mat == Material.REDSTONE) {
//            return false;
//        }
//        if (mat == Material.REDSTONE_TORCH) {
//            return false;
//        }
//        if (mat == Material.CAVE_AIR) {
//            return false;
//        }
//        if (mat == Material.SPAWNER) {
//            return false;
//        }
//        if (mat == Material.CHEST) {
//            return false;
//        }
//        if (mat == Material.SUGAR_CANE) {
//            return false;
//        }
//        if (mat == Material.ARMOR_STAND) {
//            return false;
//        }
//        if (mat.name().endsWith("_SHULKER_BOX")) {
//            return false;
//        }
//
//        if (mat.name().endsWith("BED")) {
//            return false;
//        }
//        if (mat.name().endsWith("_BANNER")) {
//            return false;
//        }
//        if (mat.name().endsWith("RAIL")) {
//            return false;
//        }
//        if (mat.name().endsWith("_FENCE")) {
//            return false;
//        }
//        if (mat.name().endsWith("_FENCE_GATE")) {
//            return false;
//        }
//        if (mat.name().endsWith("_PLATE")) {
//            return false;
//        }
        ///        if (mat.name().endsWith("_TRAPDOOR")) {
//            return false;
//        }
//        if (mat.name().endsWith("_DOOR")) {
//            return false;
//        }
//        if (mat.name().endsWith("_WALL_SIGN")) {
//            return false;
//        }
        //      if (mat.name().endsWith("_SIGN")) {
//            return false;
//        }
//        if (mat == Material.ANVIL) {
//            return false;
//        }
//        if (mat == Material.BEEHIVE) {
//            return false;
//        }
        //      if (mat == Material.BEE_NEST) {
//            return false;
        //      }
//        if (mat == Material.COBWEB) {
//            return false;
//        }
        

        return !mat.name().startsWith("COMMAND_BLOCK");

    }

    //Conver an int to a byte array.
    public static final byte[] intToByteArray(int value) {
        return new byte[]{
            (byte) (value >>> 24),
            (byte) (value >>> 16),
            (byte) (value >>> 8),
            (byte) value};
    }

    //Convert a byteArray to an int.
    public static final int ByteArrayToInt(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }

    //Convert an arraylist to a byte array
    public static byte[] toByteArray(ArrayList<Byte> in) {
        final int n = in.size();
        byte ret[] = new byte[n];
        for (int i = 0; i < n; i++) {
            ret[i] = in.get(i);
        }
        return ret;
    }

    //Converts a string to an integer array.
    public static int[] StringArrToIntArr(String[] s) {
        int[] result = new int[s.length];

        int counter = 0;
        while (counter < s.length) {
            try {
                System.out.println(s[counter]);
                result[counter] = parseInt(s[counter]);
                counter++;
            } catch (Exception ex) {
                ServerUtil.consoleLog(ex.getLocalizedMessage());
                ServerUtil.consoleLog(ex.getMessage());
                ServerUtil.consoleLog(ex);
            }
        }

        return result;
    }

}
