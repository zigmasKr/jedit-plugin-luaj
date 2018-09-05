
/**
 * (C) 2017-2018, Zigmantas Kryzius <zigmas.kr@gmail.com>
 * class LuaJProviderOptionPane
 * An option pane that can be used to configure the LuaJ jar
 */

package luaj;

//{{{ Imports
import luaj.LuaJPlugin;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JSeparator;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.browser.VFSFileChooserDialog;
import org.gjt.sp.jedit.jEdit;
//}}}

public class LuaJProviderOptionPane extends AbstractOptionPane {

	// included: included into the plugin's library
	// custom: chosen via plugin's option pane

	private JRadioButton btnCoreIncluded;
	private JRadioButton btnCoreCustom;
	private JTextField jtxtCorePath;
	private JButton btnCoreBrowse;

	private LuaJPlugin plugin;

	public LuaJProviderOptionPane() {
		super("luaj-provider");
		plugin = (LuaJPlugin) jEdit.getPlugin("luaj.LuaJPlugin");
	}

	protected void _init() {
		ButtonListener listener = new ButtonListener();

		// Core
		JPanel panelCore = new JPanel();
		panelCore.setLayout(new BoxLayout(panelCore, BoxLayout.X_AXIS));
		panelCore.add(btnCoreIncluded =
			new JRadioButton(jEdit.getProperty("options.luaj.included-core-label")));
		panelCore.add(btnCoreCustom =
			new JRadioButton(jEdit.getProperty("options.luaj.choose-label")));
		ButtonGroup groupCore = new ButtonGroup();
		groupCore.add(btnCoreIncluded);
		groupCore.add(btnCoreCustom);
		panelCore.add(new JSeparator(JSeparator.VERTICAL));
		panelCore.add(jtxtCorePath = new JTextField());
		btnCoreBrowse = new JButton(jEdit.getProperty("vfs.browser.browse.label"));
		btnCoreBrowse.addActionListener(new BrowseListener(jtxtCorePath));
		panelCore.add(btnCoreBrowse);
		String core = plugin.getLuaJCore();
		if (core.equals(LuaJPlugin.includedCore)) {
			btnCoreIncluded.setSelected(true);
			jtxtCorePath.setEnabled(false);
			btnCoreBrowse.setEnabled(false);
		} else {
			btnCoreCustom.setSelected(true);
			jtxtCorePath.setText(core);
		}
		btnCoreIncluded.addActionListener(listener);
		btnCoreCustom.addActionListener(listener);
		addComponent("Core:", panelCore);
	}

	protected void _save() {

		if (btnCoreIncluded.isSelected()) {
			plugin.setLuaJCore(LuaJPlugin.includedCore);
		} else {
			plugin.setLuaJCore(jtxtCorePath.getText());
		}
		
		plugin.setVars();
	}

	class ButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == btnCoreIncluded) {
				jtxtCorePath.setEnabled(false);
				btnCoreBrowse.setEnabled(false);
			} else if (source == btnCoreCustom) {
				jtxtCorePath.setEnabled(true);
				btnCoreBrowse.setEnabled(true);
			}
		}

	}

	class BrowseListener implements ActionListener {

		private JTextField txt;

		public BrowseListener(JTextField txt) {
			this.txt = txt;
		}

		public void actionPerformed(ActionEvent e) {
			VFSFileChooserDialog dialog = new VFSFileChooserDialog(
				jEdit.getActiveView(), System.getProperty("user.dir")+File.separator,
				VFSBrowser.OPEN_DIALOG, false, true);
			String[] files = dialog.getSelectedFiles();
			if (files != null && files.length == 1) {
				txt.setText(files[0]);
			}
		}

	}
}
