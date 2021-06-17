package ClientPackage;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.net.*;
import java.io.*;

public class LoginUI extends JFrame{	
	private DefaultTableModel svTableModel;
	private JTable svTable;
	
	public LoginUI() {
		JPanel floor = new JPanel();
		floor.setLayout(new BoxLayout(floor, BoxLayout.Y_AXIS));
		add(floor);
		
		JPanel floor1 = new JPanel();
		floor1.setLayout(new BoxLayout(floor1, BoxLayout.X_AXIS));
		floor.add(floor1);
		
		JLabel hostLb = new JLabel(" Host: ");
		JTextField hostTF = new JTextField("localhost");
		hostTF.setMaximumSize(new Dimension(200, 37));
		floor1.add(hostLb);
		floor1.add(hostTF);
		
		JLabel portLb = new JLabel("   Port");
		JTextField portTF = new JTextField("7800");
		portTF.setMaximumSize(new Dimension(200, 37));
		floor1.add(portLb);
		floor1.add(portTF);
		floor1.add(Box.createRigidArea(new Dimension(50, 0)));
		
		JButton them = new JButton("Thêm");
		them.setMaximumSize(new Dimension(100, 37));
		floor1.add(them);		
		JButton xoa = new JButton("Xóa");
		xoa.setMaximumSize(new Dimension(100, 37));
		floor1.add(xoa);		
		JButton sua = new JButton("Sửa");
		sua.setMaximumSize(new Dimension(100, 37));
		floor1.add(sua);
		
		JScrollPane floor2 = new JScrollPane();
		
		String  col[] = {"Host", "Port"};
		svTableModel = new DefaultTableModel(col, 0);	
		svTable = new JTable(svTableModel) {
			@Override
			public Class getColumnClass(int column) {
				switch(column) {
					case 0:
						return String.class;
					case 1:
						return String.class;
					default:
						return Object.class;
				}
			}
		};
		svTable.setPreferredScrollableViewportSize(svTable.getPreferredSize());
		svTable.setFillsViewportHeight(true);
		svTable.setRowHeight(37);
		floor2.setViewportView(svTable);
		floor.add(floor2);
		
		try {
			File infile = new File("dataclient/serverlist.conf");
			FileReader fileReader = new FileReader(infile);
			BufferedReader bufReader = new BufferedReader(fileReader);
			String line = null;
			while((line = bufReader.readLine()) != null) {
				String info[] = line.split("_", 2);					
				Object obs[] = {info[0], info[1]};
				svTableModel.addRow(obs);
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		JPanel floor3 = new JPanel();
		floor3.setLayout(new GridLayout(1, 1));
		floor3.setMaximumSize(new Dimension(1000, 37));
		floor.add(floor3);
		
		JButton luu = new JButton("Lưu danh sách");
		luu.setPreferredSize(new Dimension(10, 37));
		floor3.add(luu);
		floor.add(Box.createRigidArea(new Dimension(0, 30)));
		
		JPanel floor4 = new JPanel();
		floor4.setLayout(new GridLayout(3, 2));
		floor4.setMaximumSize(new Dimension(1000, 37*3));
		floor.add(floor4);
		
		JLabel accLb = new JLabel("Tên đăng nhập: ");
		JTextField accTF = new JTextField();
		accTF.setPreferredSize(new Dimension(200, 37));
		floor4.add(accLb);
		floor4.add(accTF);
		
		JLabel passLb = new JLabel("Mật khẩu: ");
		JPasswordField passTF = new JPasswordField();
		floor4.add(passLb);
		floor4.add(passTF);
		
		JButton login = new JButton("Login");
		JButton signup = new JButton("Sign Up");
		floor4.add(signup);
		floor4.add(login);		
		
		svTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(SwingUtilities.isLeftMouseButton(e)) {
					int r = svTable.getSelectedRow();
					if(r >= 0 && r <svTableModel.getRowCount()) {
						String host = svTableModel.getValueAt(r, 0).toString();
						String port = svTableModel.getValueAt(r, 1).toString();
						hostTF.setText(host);
						portTF.setText(port);
					}
				}
			}
		});
		
		them.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(SwingUtilities.isLeftMouseButton(e)) {
					String host = hostTF.getText();
					String port = portTF.getText();
					boolean ex = false;
					for(int i = 0; i < svTableModel.getRowCount(); ++i) {
						if(svTableModel.getValueAt(i, 0).equals(host) && svTableModel.getValueAt(i, 1).equals(port)) {
							ex = true;
							break;
						}
					}
					if(ex == false) {
						Object[] obs = {host, port};
						svTableModel.addRow(obs);
					}
					else {
						JOptionPane.showMessageDialog(floor, "Server đã tồn tại!");
					}
				}
			}
		});
		
		xoa.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(SwingUtilities.isLeftMouseButton(e)) {
					int r = svTable.getSelectedRow();
					if(r >= 0 && r <svTableModel.getRowCount()) {
						 svTableModel.removeRow(r);
					}
				}
			}
		});
		
		sua.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(SwingUtilities.isLeftMouseButton(e)) {
					int r = svTable.getSelectedRow();
					String host = hostTF.getText();
					String port = portTF.getText();
					if(r >= 0 && r < svTableModel.getRowCount()) {
						svTableModel.setValueAt(host, r, 0);
						svTableModel.setValueAt(port, r, 1);
					}
				}
			}
		});
		
		luu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(SwingUtilities.isLeftMouseButton(e)) {
					try {
						FileWriter file = new FileWriter("dataclient/serverlist.conf");
						for(int i = 0; i < svTableModel.getRowCount(); ++i) {
							String info = svTableModel.getValueAt(i, 0).toString() + "_" + svTableModel.getValueAt(i, 1).toString() + "\n";
							file.write(info);
						}
						file.close();
						JOptionPane.showMessageDialog(floor, "Đã lưu danh sách");
					}
					catch(IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		login.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(SwingUtilities.isLeftMouseButton(e)) {
					String acc = accTF.getText();
					String pass = String.copyValueOf(passTF.getPassword());
					String host = hostTF.getText();
					int port = Integer.parseInt(portTF.getText());
					try {
						Socket client = new Socket(host, port);
						DataInputStream in = new DataInputStream(client.getInputStream());
						DataOutputStream out = new DataOutputStream(client.getOutputStream());
						
						out.writeUTF("login");
						String signal = in.readUTF();
						if(signal.equals("acc_pass")) {
							out.writeUTF(acc);
							out.writeUTF(pass);
							signal = in.readUTF();												
							if(signal.equals("login_success")) {
								String name = in.readUTF();
								out.flush();
//								JOptionPane.showMessageDialog(floor, "Đăng nhập thành công");								
								ClientUI chatClient = new ClientUI(acc, name, client);
								chatClient.setVisible(true);
								dispose();
							}
							else if (signal.equals("login_fail_pass")){
								JOptionPane.showMessageDialog(floor, "Sai mật khẩu!");
							}
							else if (signal.equals("login_fail_acc")){
								JOptionPane.showMessageDialog(floor, "Tên tài khoản không tồn tại!");
							}
							else if (signal.equals("other_logged_this_acc")){
								JOptionPane.showMessageDialog(floor, "Tài khoản đã được đăng nhập!");
							}
						}
					}
					catch(IOException e1) {
						JOptionPane.showMessageDialog(floor, "Hiện tại server này không hoạt động!");
						e1.printStackTrace();
					}
				}
			}
		});
		
		signup.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(SwingUtilities.isLeftMouseButton(e)) {
					String acc = accTF.getText();
					String pass = String.copyValueOf(passTF.getPassword());
					String host = hostTF.getText();
					String name = JOptionPane.showInputDialog("Nhập tên (nickname) của bạn:");
					
					int port = Integer.parseInt(portTF.getText());
					try {
						Socket client = new Socket(host, port);
						DataInputStream in = new DataInputStream(client.getInputStream());
						DataOutputStream out = new DataOutputStream(client.getOutputStream());
						
						out.writeUTF("signup");out.flush();
						String signal = in.readUTF();
						if(signal.equals("acc_pass_name")) {
							out.writeUTF(acc);//out.flush();
							out.writeUTF(pass);//out.flush();
							out.writeUTF(name);//out.flush();
							signal = in.readUTF();
							if(signal.equals("signup_success")) {
								JOptionPane.showMessageDialog(floor, "Đăng ký thành công");
								ClientUI chatClient = new ClientUI(acc, name, client);
								chatClient.setVisible(true);
								dispose();
							}
							else if (signal.equals("existed_acc")){
								JOptionPane.showMessageDialog(floor, "Tài khoản đã tồn tại!");
							}
						}
					}
					catch(IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});		
		
		setTitle("Login");
		setSize(600, 700);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) {
		try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("GTK+".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            Enumeration keys = UIManager.getDefaults().keys();
            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                Object value = UIManager.get(key);
                if (value instanceof FontUIResource) {
                    FontUIResource orig = (FontUIResource) value;
                    Font font = new Font("TimeNewRoman" , Font.PLAIN, 17);
                    UIManager.put(key, new FontUIResource(font));
                }
            }            
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (java.lang.InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }		
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				LoginUI loginFrame = new LoginUI();
				loginFrame.setVisible(true);
			}
		});
	}
}
