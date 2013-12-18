package com.hyc;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileSystemView;

public class AndroidLogAnalyser extends JFrame implements ActionListener, ItemListener {
	private JPanel panelTop = new JPanel();
	private JPanel panelButtom = new JPanel();
	private JButton chooseLogFileBtn = new JButton("选择log文件");
	private JButton searchBtn = new JButton("筛选");
	private JLabel label1 = new JLabel("查找关键字：");
	private JLabel label2 = new JLabel("排除关键字：");
	private TextField keyWordsTF1 = new TextField("", 10);
	private TextField keyWordsTF2 = new TextField("", 10);
	private TextArea showLogTA = new TextArea(40, 150);
	private JComboBox logTypeJC;
	private Checkbox showLineNumber;
	private boolean boolShowLineNumber;
	private File logFile;
	private ArrayList<String> logs = new ArrayList<String>();
	private StringBuilder showResult = new StringBuilder("没有可以显示的结果！");
	private LoadFileThread loadFileThread = new LoadFileThread();
	private RefreshThread refreshThread;

	public AndroidLogAnalyser() {
		String[] logType = { "verbose", "debug", "info", "warn", "error", "assert" };
		logTypeJC = new JComboBox(logType);
		showLineNumber = new Checkbox("显示行号");
		boolShowLineNumber = false;
		JPanel contentpane = (JPanel) getContentPane();
		contentpane.add(panelTop, BorderLayout.CENTER);
		contentpane.add(panelButtom, BorderLayout.SOUTH);
		showLineNumber.addItemListener(this);
		chooseLogFileBtn.addActionListener(this);
		searchBtn.addActionListener(this);
		panelTop.add(chooseLogFileBtn);
		panelTop.add(logTypeJC);
		panelTop.add(label1);
		panelTop.add(keyWordsTF1);
		panelTop.add(label2);
		panelTop.add(keyWordsTF2);
		panelTop.add(showLineNumber);
		panelTop.add(searchBtn);
		panelButtom.add(showLogTA);
	}

	public void itemStateChanged(ItemEvent e) {
		boolShowLineNumber = !boolShowLineNumber;
	}

	public void actionPerformed(ActionEvent event) {
		JButton button = (JButton) event.getSource();

		if (!loadFileThread.isAlive()) {
			if (button.getText().equals("选择log文件")) {
				try {
					loadFileThread = new LoadFileThread();
					loadFileThread.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (button.getText().equals("筛选")) {
				try {
					showLog(keyWordsTF1.getText(), keyWordsTF2.getText());
					showLogTA.setText(showResult.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			JOptionPane.showMessageDialog(getContentPane(), "不要心急你等我加载完么!", "不要着急.", JOptionPane.QUESTION_MESSAGE);
		}
	}

	private void showLog(String keyWord1, String keyWord2) {
		ArrayList<String> temp = new ArrayList<String>();
		showResult = new StringBuilder();
		String logTypeKeyWord = "";
		switch (logTypeJC.getSelectedIndex()) {
		case 1:
			logTypeKeyWord = "D/";
			break;
		case 2:
			logTypeKeyWord = "I/";
			break;
		case 3:
			logTypeKeyWord = "W/";
			break;
		case 4:
			logTypeKeyWord = "E/";
			break;
		case 5:
			logTypeKeyWord = "A/";
			break;
		default:
			break;
		}
		if (keyWord2.length() > 0) {
			for (int i = 0; i < logs.size(); i++) {
				if (-1 != logs.get(i).indexOf(keyWord1) && -1 == logs.get(i).indexOf(keyWord2)) {
					addLog(temp, logs, i);
				}
			}
		} else {
			for (int i = 0; i < logs.size(); i++) {
				if (-1 != logs.get(i).indexOf(keyWord1)) {
					addLog(temp, logs, i);
				}
			}
		}
		if ("" != logTypeKeyWord) {
			for (int i = 0; i < temp.size(); i++) {
				if (-1 != temp.get(i).indexOf(logTypeKeyWord)) {
					showResult.append(temp.get(i));
				}
			}
		} else {
			for (int i = 0; i < temp.size(); i++) {
				showResult.append(temp.get(i));
			}
		}
	}

	private void addLog(ArrayList<String> temp, ArrayList<String> logs, int i) {
		if (boolShowLineNumber) {
			temp.add(i + "行" + '\t' + logs.get(i));
		} else {
			temp.add(logs.get(i));
		}
	}

	public final static void main(String[] args) {
		JFrame frame = new AndroidLogAnalyser();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1280, 960);
		frame.setVisible(true);
	}

	class RefreshThread extends Thread {
		private boolean stop = true;

		public void run() {
			while (stop) {
				try {
					Thread.sleep(1000);
					showLogTA.setText(FileOpen.getLog());
					setTitle(logFile.getPath() + "            加载中........" + FileOpen.getI() + "行，共 "
							+ FileOpen.getLineNumber());
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}

		public void ThreadStop() {
			stop = false;
		}
	}

	class LoadFileThread extends Thread {
		public void run() {
			try {
				refreshThread = new RefreshThread();
				refreshThread.start();
				JFileChooser fileChooserDialog = new JFileChooser();
				FileSystemView fsv = FileSystemView.getFileSystemView();
				fileChooserDialog.setCurrentDirectory(fsv.getHomeDirectory());
				fileChooserDialog.showOpenDialog(null);
				logFile = fileChooserDialog.getSelectedFile();
				logs = FileOpen.readFile02(logFile.getPath());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				refreshThread.ThreadStop();
				setTitle(logFile.getPath());
			}
		}
	}
}