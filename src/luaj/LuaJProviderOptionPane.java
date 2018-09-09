
/**
 * (C) 2017-2018, Zigmantas Kryzius <zigmas.kr@gmail.com>
 * class LuaJProviderOptionPane
 * An option pane that can be used to configure the LuaJ jar
 */

package luaj;

//{{{ Imports
import luaj.LuaJPlugin;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import java.io.File;
import java.io.FilenameFilter;
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

	private JRadioButton btnIncludedCore;
	private JRadioButton btnCustomCore;
	private JTextField jtxtIncludedCorePath;
	private JTextField jtxtCustomCorePath;
	private JButton btnBrowseCore;

	private LuaJPlugin plugin;

	public LuaJProviderOptionPane() {
		super("luaj-provider");
		plugin = (LuaJPlugin) jEdit.getPlugin("luaj.LuaJPlugin");
	}

	private JPanel buildIncludeCustomPanel(
	   ButtonGroup groupBtn,
	   JRadioButton btnIncluded,
	   JRadioButton btnCustom,
	   JButton btnBrowse,
	   JTextField jtxtIncluded,
	   JTextField jtxtCustom
	   ) {
	   groupBtn.add(btnIncluded);
		groupBtn.add(btnCustom);
		//
	   JPanel panel = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		panel.setLayout(gridbag);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		//
		c.weightx = 0.02;
		c.gridx = 0;
		c.gridy = 0;
		gridbag.setConstraints(btnIncluded, c);
		panel.add(btnIncluded);
		//
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = 0;
		gridbag.setConstraints(jtxtIncluded, c);
		panel.add(jtxtIncluded);
		//
		c.weightx = 0.02;
		c.gridx = 0;
		c.gridy = 1;
		gridbag.setConstraints(btnCustom, c);
		panel.add(btnCustom);
		//
		c.gridx = 1;
		c.gridy = 1;
		gridbag.setConstraints(jtxtCustom, c);
		panel.add(jtxtCustom);
		//
		c.weightx = 0.02;
		c.gridx = 2;
		c.gridy = 1;
		gridbag.setConstraints(btnBrowse, c);
		panel.add(btnBrowse);
		//
		return panel;
	}

	protected void _init() {
		ButtonListener listener = new ButtonListener();
		// Core
		// ========
		ButtonGroup groupCore = new ButtonGroup();
		btnIncludedCore =
			new JRadioButton(jEdit.getProperty("options.luaj.included-core-label"));
		btnCustomCore =
		   new JRadioButton(jEdit.getProperty("options.luaj.choose-label"));
		btnBrowseCore = new JButton(jEdit.getProperty("vfs.browser.browse.label"));
		//
		btnIncludedCore.addActionListener(listener);
		btnCustomCore.addActionListener(listener);
		btnBrowseCore.addActionListener(new BrowseListener(jtxtCustomCorePath));
		//
		jtxtIncludedCorePath = new JTextField();
		jtxtCustomCorePath = new JTextField();
		//
		JPanel panelCore = buildIncludeCustomPanel(
		   groupCore, btnIncludedCore, btnCustomCore, btnBrowseCore,
		   jtxtIncludedCorePath, jtxtCustomCorePath
		   );
		//
		addComponent("Core:", panelCore);
		//
		String core = plugin.getLuaJCore();
		if (core.equals(LuaJPlugin.includedCore)) {
		   jtxtIncludedCorePath.setText(LuaJPlugin.includedCore);
		   jtxtCustomCorePath.setText("");
			jtxtCustomCorePath.setEnabled(false);
			btnIncludedCore.setSelected(true);
			btnBrowseCore.setEnabled(false);
		} else {
			jtxtIncludedCorePath.setEnabled(false);
			jtxtCustomCorePath.setText(core);
			btnCustomCore.setSelected(true);
		}
	}

	protected void _save() {

		if (btnIncludedCore.isSelected()) {
			plugin.setLuaJCore(LuaJPlugin.includedCore);
		} else {
			plugin.setLuaJCore(jtxtCustomCorePath.getText());
		}

		plugin.setVars();
	}

	class ButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == btnIncludedCore) {
			   jtxtIncludedCorePath.setEnabled(true);
			   jtxtCustomCorePath.setText("");
				jtxtCustomCorePath.setEnabled(false);
				btnBrowseCore.setEnabled(false);
			} else if (source == btnCustomCore) {
			   jtxtIncludedCorePath.setEnabled(false);
				jtxtCustomCorePath.setEnabled(true);
				btnBrowseCore.setEnabled(true);
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
				jEdit.getActiveView(), System.getProperty("user.dir") + File.separator,
				VFSBrowser.OPEN_DIALOG, false, true);
			String[] files = dialog.getSelectedFiles();
			if (files != null && files.length == 1) {
				txt.setText(files[0]);
			}
		}

	}
}
