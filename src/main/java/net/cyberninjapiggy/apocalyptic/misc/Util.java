package net.cyberninjapiggy.apocalyptic.misc;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Util {
    public static String title(String str) {
        String[] strs = new String[str.split(" ").length];
        int i=0;
        for (String s : str.split(" ")) {
            strs[i] = s.substring(0, 1).toUpperCase() + s.substring(1, s.length());
            i++;
        }
        String titled = "";
        for (int j=0;j<strs.length;j++) {
            titled += strs[j];
            if (j != strs.length-1) {
                titled += " ";
            }
        }
        return titled;
    }
    public static ItemStack setName(ItemStack is, String name){
        ItemMeta m = is.getItemMeta();
        m.setDisplayName(name);
        is.setItemMeta(m);
        return is;
    }
}
