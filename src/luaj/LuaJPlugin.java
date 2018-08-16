
/**
 * (C) 2017-2018, Zigmantas Kryzius <zigmas.kr@gmail.com>
 * class LuaJPlugin
 * The main class for the LuaJ plugin.
 * Handles loading/unloading of LuaJ jar
 */

package luaj;

//{{{ Imports
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.IOUtilities;
//}}}

public class LuaJPlugin extends EditPlugin {
	
	/** Name for plugin manager */
	public final static String NAME = "LuaJPlugin";

	public static final String propertyCorePath = "options.luaj.luaj-core-path";
	public static final String nameCore = jEdit.getProperty("options.luaj.luaj-core-jar");

	public static final String settingsJEdit = jEdit.getSettingsDirectory();
	public static final String homeJEdit = jEdit.getJEditHome();

	// 'included...' jar may be placed in the jEdit settings directory:
	public static final String coreInSettings =
		MiscUtilities.constructPath(settingsJEdit, "jars/" + nameCore);

	// 'included...' jar may be placed in the jEdit install directory, too:
	public static final String coreInJEdit =
		MiscUtilities.constructPath(homeJEdit, "jars/" + nameCore);

	private static String findIncludedJar(String settings, String home) {
		String included = null;
		File inSettings = new File(settings);
		File inHome = new File(home);
		if (inSettings.exists()) {
			included = settings;
		} else if (inHome.exists()) {
			included = home;
		}
		return included;
	}

	public static String includedCore = findIncludedJar(coreInSettings, coreInJEdit);
	private String installedCore = null;

	public void start() {

		// IF core property is not defined,
		// it is set to 'included...' jar
		if (jEdit.getProperty(propertyCorePath) == null) {
			jEdit.setProperty(propertyCorePath, includedCore);
		}

		// ELSE core is taken by property,
		// included jar is unloaded, installed jar is loaded:
		installedCore = getLuaJCore();
		if (!installedCore.equals(includedCore)) {
			jEdit.removePluginJAR(jEdit.getPluginJAR(includedCore), false);
			jEdit.addPluginJAR(installedCore);
		}

		setVars();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (jEdit.getPlugin("console.ConsolePlugin") != null) {
					File luajCommand = new File(console.ConsolePlugin.getUserCommandDirectory(), "lua.xml");
					if (!luajCommand.exists()) {
						try {
							InputStream in = getClass().getResourceAsStream(File.separator + "commands" +
								File.separator + "luaj.xml");
							OutputStream out = new FileOutputStream(luajCommand);
							IOUtilities.copyStream(null, in, out, false);
							IOUtilities.closeQuietly(in);
							IOUtilities.closeQuietly(out);
							console.ConsolePlugin.rescanCommands();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
	}

	public void stop() {}

	/**
	 * Set the loaded LuaJ core jar
	 */
	public void setLuaJCore(String path) {
		jEdit.setProperty(propertyCorePath, path);
		jEdit.removePluginJAR(jEdit.getPluginJAR(installedCore), false);
		jEdit.addPluginJAR(path);
		installedCore = path;
	}

	/**
	 * If Console is installed, set some environment variables.
	 * Set LuaJ to the path of the LuaJ jar
	 */
	public void setVars() {
		if (jEdit.getPlugin("console.ConsolePlugin") != null) {
			console.ConsolePlugin.setSystemShellVariableValue("LUAJ", getLuaJ());
		}
	}

	/**
	 * Returns the location of the LuaJ core jar
	 */
	public String getLuaJCore() {
		return jEdit.getProperty(propertyCorePath);
	}

	/**
	 * Returns the paths of core.
	 * Ideal for setting environment paths and for use in the system shell
	 */
	public String getLuaJ() {
		String core = getLuaJCore();
		return core;
	}

}

/* :folding=explicit:collapseFolds=1:tabSize=2:indentSize=2:noTabs=false: */