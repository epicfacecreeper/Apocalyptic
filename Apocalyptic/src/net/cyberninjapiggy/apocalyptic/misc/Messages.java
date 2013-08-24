package net.cyberninjapiggy.apocalyptic.misc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import org.bukkit.plugin.Plugin;

public class Messages {

	private static final Properties strings = new Properties();
	public static void load(Plugin p) {
		File folder = p.getDataFolder();
		if (!new File(folder, "strings.properties").exists()) {
			try {
				InputStream input = p.getClass().getClassLoader().getResourceAsStream("defaultStrings.properties");
				FileOutputStream output = new FileOutputStream(new File(folder, "strings.properties"));
				byte [] buffer = new byte[4096];
				int bytesRead = input.read(buffer);
				while (bytesRead != -1) {
				    output.write(buffer, 0, bytesRead);
				    bytesRead = input.read(buffer);
				}
				output.close();
				input.close();
			}
			catch(IOException e) {
				p.getLogger().warning("Could not save strings, using defaults instead");
			}
		}
	}

	private Messages() {
	}

	public static String getString(String key) {
		try {
			return strings.getProperty(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
