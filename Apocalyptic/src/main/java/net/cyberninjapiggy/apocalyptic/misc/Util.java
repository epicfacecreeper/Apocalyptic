package net.cyberninjapiggy.apocalyptic.misc;

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
}
