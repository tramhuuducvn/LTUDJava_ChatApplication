package ServerPackage;

import javax.swing.*;
//import javax.swing.event.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.net.*;
import java.io.*;

public class ServerUI extends JFrame {	
	private int port = 7800;
	private JButton start;
	private JButton stop;
	
	
	DefaultTableModel tableModel;
	JTable userList;
	
	ChatServer chatServer;
	
	public ServerUI() throws IOException {
		JPanel floor = new JPanel();
		floor.setLayout(new BoxLayout(floor, BoxLayout.Y_AXIS));
		this.add(floor);
		
		JScrollPane floor1 = new JScrollPane();
		floor.add(floor1);
		
		String  col[] = {"Client's id online list"};
		tableModel = new DefaultTableModel(col, 0);	
		userList = new JTable(tableModel) {
			@Override
			public Class getColumnClass(int column) {
				switch(column) {
					case 0:
						return String.class;
					default:
						return Object.class;
				}
			}
		};
		
		floor1.add(userList);
		userList.setPreferredScrollableViewportSize(userList.getPreferredSize());
		userList.setFillsViewportHeight(true);
		userList.setRowHeight(37);
		floor1.setViewportView(userList);		
		
		JPanel floor2 = new JPanel();
		floor2.setLayout(new BoxLayout(floor2, BoxLayout.X_AXIS));
		floor.add(floor2);
		
		JLabel status = new JLabel();
		JLabel info = new JLabel("Host: " + InetAddress.getLocalHost().getHostAddress() + "  Port: ");
		JTextField portTextField = new JTextField("7800");
		portTextField.setMaximumSize(new Dimension(0, 36));
		
		this.start = new JButton("Start");
		this.stop = new JButton("Stop");
		this.start.setPreferredSize(new Dimension(100, 37));
		this.start.setMaximumSize(new Dimension(100, 100));
		this.start.setForeground(new Color(32, 137, 84));
		this.stop.setPreferredSize(new Dimension(100, 37));
		this.stop.setMaximumSize(new Dimension(100, 100));
		this.stop.setForeground(new Color(255, 50, 50));
		floor2.add(info);
		floor2.add(portTextField);
		floor2.add(status);
		floor2.add(Box.createHorizontalGlue());
		floor2.add(this.start);
		floor2.add(Box.createRigidArea(new Dimension(25, 0)));
		floor2.add(this.stop);
		floor2.add(Box.createRigidArea(new Dimension(25, 0)));
		
		this.start.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(SwingUtilities.isLeftMouseButton(e)) {
					port = Integer.parseInt(portTextField.getText());
					chatServer = new ChatServer(port, tableModel);
					chatServer.start();
					
					status.setText("  Running...");
					status.setForeground(new Color(32, 137, 84));					
				}
			}
		});
		
		this.stop.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(SwingUtilities.isLeftMouseButton(e)) {
					if(chatServer != null) {
						chatServer.kill();
					}
					status.setText("  Stopped!");
					status.setForeground(new Color(255, 37, 37));
				}
			}
		});
		
		addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
            	System.exit(0);
            }
        });
		
		this.setTitle("Chat Server");
		this.setSize(700, 700);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
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
				try {
					ServerUI main = new ServerUI();
					main.setVisible(true);
				}
				catch(IOException e) {
					e.printStackTrace();
				}
			}
		});		
	}
}
