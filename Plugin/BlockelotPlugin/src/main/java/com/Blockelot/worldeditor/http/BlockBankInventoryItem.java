package com.Blockelot.worldeditor.http;

import org.bukkit.Material;

/**
 *
 * @author geev
 */
public class BlockBankInventoryItem {

    public BlockBankInventoryItem(Material mat, int count) {
        MaterialName = mat.name();
        Count = count;
    }

    public BlockBankInventoryItem(String mat, int count) {
        MaterialName = mat;
        Count = count;
    }

    private String MaterialName;
    private int Count;

    public String getMaterialName() {
        return MaterialName;
    }

    public void setMaterialName(String name) {
        MaterialName = name;
    }

    public int getCount() {
        return Count;
    }

    public void setCount(int c) {
        Count = c;
    }
}
