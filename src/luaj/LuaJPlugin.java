
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

	public static final String propCorePath = "options.luaj.luaj-core-path";

	public static final String nameCore = jEdit.getProperty("options.luaj.luaj-core-jar");

	public static final String dirSettings = jEdit.getSettingsDirectory();
	public static final String dirHome = jEdit.getJEditHome();

	// 'included...' jar may be placed in the jEdit settings directory:
	public static final String coreInSettings =
		MiscUtilities.constructPath(dirSettings, "jars/" + nameCore);
	// OR
	// 'included...' jar may be placed in the jEdit install directory:
	public static final String coreInHome =
		MiscUtilities.constructPath(dirHome, "jars/" + nameCore);

	private static String findIncluded(String settings, String home) {
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

	public static String includedCore = findIncluded(coreInSettings, coreInHome);
	public static String workingCore = null;

	public void start() {
		// IF property is null, OR path by the property does not exist,
		// the property is set to 'included...'
		if (jEdit.getProperty(propCorePath) == null) {
			jEdit.setProperty(propCorePath, includedCore);
			workingCore = includedCore;
		} else {
			File pathCore = new File(jEdit.getProperty(propCorePath));
			if (!pathCore.exists()) {
				jEdit.setProperty(propCorePath, includedCore);
				workingCore = includedCore;
			} else {
				// ELSE jEdit.getProperty(propCorePath) points to working core.
				workingCore = jEdit.getProperty(propCorePath);
			}
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
	 * Set the loaded LuaJ core jar; method used in ..ProviderOptionPane
	 */
	public void setLuaJCore(String path) {
		jEdit.setProperty(propCorePath, path);
		jEdit.removePluginJAR(jEdit.getPluginJAR(workingCore), false);
		jEdit.addPluginJAR(path);
		workingCore = path;
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
		return jEdit.getProperty(propCorePath);
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

/* :folding=explicit:collapseFolds=1:tabSize=4:indentSize=4:noTabs=false: */