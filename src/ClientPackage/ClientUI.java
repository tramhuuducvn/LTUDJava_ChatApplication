package ClientPackage;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.*;

import ServerPackage.ChatServer;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.net.*;
import java.io.*;

public class ClientUI extends JFrame{
	Socket client;
	DataInputStream dataIn;
	DataOutputStream dataOut;
	String acc;
	String name;
	
	MessageListen messagelisten;
//	boolean havingMess;
//	String messReceived;
	
	String curChatPerson;
	ArrayList<JButton> useronlines;	
	
	public ClientUI(String acc, String name, Socket client) {
		this.name = name;
		this.acc = acc;
		this.client = client;
		
		useronlines = new ArrayList<JButton>();
		try {
			dataIn = new DataInputStream(client.getInputStream());
			dataOut = new DataOutputStream(client.getOutputStream());
		}
		catch(IOException e) {
			e.printStackTrace();
		}	
		
		initUI();
		messagelisten = new MessageListen(dataIn, this);
		Thread t = new Thread(messagelisten);
		t.start();
		
		addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
            	try {
            		dataOut.writeUTF("close");
            		dataOut.flush();
            	}
            	catch(IOException e) {
            		e.printStackTrace();
            	}
                dispose();
            }
        });
		
		setTitle("Chat Apllication");
		setSize(670, 700);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	private JPanel mainPane;
	private JPanel userPane;
	private HashMap<String, JPanel> chatPanes = new HashMap<String, JPanel>();
	private JPanel curChatPane;
	private JLabel otherLb;
	private JTextArea textChat;
	JScrollPane chatSP;
	
	private JButton sendButton;
	private JButton sendFileButton;
	private JButton dolphinB;
	private JButton heartEyeB;
	private JButton smileB;
	private JButton sadB;
	private JButton angryB;
	
	private Color youColor = new Color(10, 136, 242);
	private Color otherColor = new Color(12, 208, 136);
	private JPanel tempane;
	
	private void initUI() {
		mainPane = new JPanel();
		mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.X_AXIS));
		add(mainPane);
//-------leftPane contains users online----------------------------------
		JPanel leftPane = new JPanel();
		leftPane.setLayout(new BoxLayout(leftPane, BoxLayout.Y_AXIS));
		JLabel lb1 = new JLabel("Users online");
		lb1.setForeground(otherColor);
		JPanel pn1 = new JPanel();
		pn1.setLayout(new BoxLayout(pn1, BoxLayout.X_AXIS));
		//pn1.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		pn1.add(lb1);
		leftPane.add(pn1);
		userPane = new JPanel();
		userPane.setLayout(new BoxLayout(userPane, BoxLayout.Y_AXIS));
		JScrollPane userSP = new JScrollPane(userPane);
		
		leftPane.add(userSP);
		leftPane.setMaximumSize(new Dimension(274, 1000));
		leftPane.setMinimumSize(new Dimension(274, 1000));
		leftPane.setPreferredSize(new Dimension(274, 1000));		
		mainPane.add(leftPane);
//----------------------------------------------------------------------------
		// rightPane contains Chat Box and Chat activities(text, emoji, file)
		JPanel rightPane = new JPanel();
		rightPane.setLayout(new BoxLayout(rightPane, BoxLayout.Y_AXIS));
		mainPane.add(rightPane);
		JPanel chatTitle = new JPanel();
		chatTitle.setLayout(new BoxLayout(chatTitle, BoxLayout.X_AXIS));
		JLabel youLb = new JLabel("You: " + name + "    ");
		youLb.setForeground(youColor);
		otherLb = new JLabel("(Nobody):");
		otherLb.setForeground(otherColor);
		chatTitle.add(otherLb);
		chatTitle.add(Box.createHorizontalGlue());
		chatTitle.add(youLb);
		//chatTitle.setBorder(BorderFactory.createLineBorder(new Color(77, 177,81), 1));		
		rightPane.add(chatTitle);
		
		//chatPane = new JPanel();
		//chatPane.setLayout(new BoxLayout(chatPane, BoxLayout.Y_AXIS));
		chatSP = new JScrollPane();
		rightPane.add(chatSP);
		
		JLabel templb = new JLabel("---end---");
		templb.setForeground(new Color(25, 77, 51));
		tempane = new JPanel();
		tempane.add(templb);	    
		rightPane.add(Box.createRigidArea(new Dimension(0, 5)));
		
//------ Area for chat activity------------------------------------
		JPanel floor = new JPanel();
		floor.setLayout(new BoxLayout(floor, BoxLayout.Y_AXIS));
		floor.setMaximumSize(new Dimension(1000, 100));
		floor.setMinimumSize(new Dimension(100, 100));
		floor.setPreferredSize(new Dimension(100, 100));
		
		initEmoji();
		JPanel emojiPane = new JPanel();
		emojiPane.setLayout(new BoxLayout(emojiPane, BoxLayout.X_AXIS));		
		emojiPane.add(dolphinB);
		emojiPane.add(heartEyeB);
		emojiPane.add(smileB);
		emojiPane.add(sadB);
		emojiPane.add(angryB);
		emojiPane.add(Box.createRigidArea(new Dimension(25, 0)));		
		emojiPane.add(sendFileButton);
		emojiPane.add(sendButton);
		floor.add(emojiPane);
		
		textChat = new JTextArea();
		JScrollPane textSP = new JScrollPane(textChat);
		
		InputMap input = textChat.getInputMap();
	    KeyStroke enter = KeyStroke.getKeyStroke("ENTER");
	    KeyStroke shiftEnter = KeyStroke.getKeyStroke("ctrl ENTER");
	    input.put(shiftEnter, "insert-break");
	    input.put(enter, "text-submit");
		
		
		floor.add(textSP);
		rightPane.add(floor);
		//userPane.setBackground(new Color(, 154, 132));
		//leftPane.setBorder(BorderFactory.createLineBorder(Color.CYAN, 1));
	}
	
	public JPanel getMainPane() {
		return this.mainPane;
	}
	
	private ImageIcon NewIcon(String srcFile, int x, int y) {	
		ImageIcon src = new ImageIcon(srcFile);
		Image scaled = src.getImage().getScaledInstance(x, y, Image.SCALE_SMOOTH);
		return new ImageIcon(scaled);
	}
	
	private void initEmoji() {
		sendButton = new JButton(NewIcon("img/send.png", 25, 25));
		sendFileButton = new JButton(NewIcon("img/file.png", 25, 25));
		dolphinB = new JButton(NewIcon("img/dolphin_icon.png", 25, 25));
		heartEyeB = new JButton(NewIcon("img/heart_eyes_icon.png", 25, 25));
		smileB = new JButton(NewIcon("img/smile_icon.png", 25, 25));
		sadB = new JButton(NewIcon("img/sad_icon.png", 25, 25));
		angryB = new JButton(NewIcon("img/angry_icon.png", 25, 25));
		
		this.getRootPane().setDefaultButton(sendButton);
//		sendButton.setMnemonic(KeyEvent.VK_ENTER);
//	    JRootPane rootpane = SwingUtilities.getRootPane(sendButton);
//	    rootpane.setDefaultButton(sendButton);
	    
	    KeyStroke enter = KeyStroke.getKeyStroke("ENTER");
	    sendButton.getInputMap().put(enter, Action.DEFAULT);		
	    sendButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String text = textChat.getText();
				showSendMessage(text);
				textChat.setText(null);
				String packText = "text_" + curChatPerson + "_" + text;
				try {
					dataOut.writeUTF(packText);
				}
				catch(IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		sendFileButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(SwingUtilities.isLeftMouseButton(e)) {
					JFileChooser filechooser = new JFileChooser();
					filechooser.showOpenDialog(curChatPane);
					File file = filechooser.getSelectedFile();					
					showFileSend(file.getName());
					try {
						dataOut.writeUTF("file_" + curChatPerson + "_" + file.getName());
						sendFile(file.getAbsolutePath());
					}
					catch(IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		dolphinB.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(SwingUtilities.isLeftMouseButton(e)) {
					EmojiAction("img/dolphin_icon.png");
				}
			}
		});

		heartEyeB.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(SwingUtilities.isLeftMouseButton(e)) {
					EmojiAction("img/heart_eyes_icon.png");
				}
			}
		});

		smileB.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(SwingUtilities.isLeftMouseButton(e)) {
					EmojiAction("img/smile_icon.png");
				}
			}
		});

		sadB.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(SwingUtilities.isLeftMouseButton(e)) {
					EmojiAction("img/sad_icon.png");
				}
			}
		});

		angryB.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(SwingUtilities.isLeftMouseButton(e)) {
					EmojiAction("img/angry_icon.png");
				}
			}
		});
	}
	
	public void addUserOnline(String id, String username) {
		JButton u = new JButton(username);
		u.setToolTipText(id);
		useronlines.add(u);
		u.setPreferredSize(new Dimension(250,50));
		u.setMaximumSize(new Dimension(250,50));		
		
		u.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(SwingUtilities.isLeftMouseButton(e)) {
					curChatPerson = u.getToolTipText();
					if(chatPanes.containsKey(curChatPerson)) {
						curChatPane = chatPanes.get(curChatPerson);					
						curChatPane.setVisible(false);
						curChatPane.setVisible(true);
						chatSP.setViewportView(curChatPane);
					}					
					otherLb.setText(u.getText() + ": ");
					
					for(JButton b : useronlines) {
						b.setBackground(null);
						b.setForeground(null);
					}
					u.setOpaque(true);
					u.setBackground(new Color(254, 243, 189));
					u.setForeground(otherColor);
				}
			}
		});
		
		userPane.add(u);
		userPane.setVisible(false);
		userPane.setVisible(true);
	}
	
	public void removeUser(String idbtn) {
		for(JButton b : useronlines) {
			if(b.getToolTipText().equals(idbtn)) {
				userPane.remove(b);
				chatPanes.remove(idbtn);
				chatSP.setViewportView(null);
				userPane.setVisible(false);
				userPane.setVisible(true);
				break;
			}
		}
	}
	
	public void showSendMessage(String mess) {
		String lines[] = mess.split("\n");
		String ret = lines[0];
		for(int i = 1; i < lines.length; ++i) {
			ret += "<br>" + lines[i];
		}
//		<p style="text-align:center;">Centered paragraph.</p>
		JLabel lb = new JLabel("<html> <p style=\"text-align:right;\">"  + ret + "</p> </html> ", SwingConstants.RIGHT);
		lb.setOpaque(true);
		//lb.setBackground(youColor);
		lb.setForeground(youColor);
		JPanel contain = new JPanel();		
		contain.setLayout(new BoxLayout(contain, BoxLayout.X_AXIS));	
		contain.add(Box.createHorizontalGlue());
		contain.add(lb);
		updateChatSP(contain);
	}
	
	public void showReceivedMessage(String mess, String id) {
		String lines[] = mess.split("\n");
		String ret = lines[0];
		for(int i = 1; i < lines.length; ++i) {
			ret += "<br>" + lines[i];
		}
		
		JLabel lb = new JLabel("<html>" + ret + "</html>");		
		lb.setOpaque(true);
		//lb.setBackground(otherColor);
		lb.setForeground(otherColor);
		JPanel contain = new JPanel();
		contain.setLayout(new BoxLayout(contain, BoxLayout.X_AXIS));
		contain.add(lb);
		contain.add(Box.createHorizontalGlue());	
		
		updateChatSP(contain);
	}
	
	public void EmojiAction(String img) {
		JLabel lb = new JLabel(NewIcon(img, 30, 30));		
		lb.setOpaque(true);
		//lb.setBackground(youColor);
		JPanel contain = new JPanel();
		contain.setLayout(new BoxLayout(contain, BoxLayout.X_AXIS));
		contain.add(Box.createHorizontalGlue());
		contain.add(lb);
		updateChatSP(contain);		
		try {
			dataOut.writeUTF("emoji_" + curChatPerson + "_" + img);
//			dataOut.flush();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void ReceiveEmoji(String img, String id) {
		JLabel lb = new JLabel(NewIcon(img, 30, 30));		
		lb.setOpaque(true);
		//lb.setBackground(otherColor);
		JPanel contain = new JPanel();
		contain.setLayout(new BoxLayout(contain, BoxLayout.X_AXIS));		
		contain.add(lb);
		contain.add(Box.createHorizontalGlue());

		updateChatSP(contain);
	}
	
	public void sendFile(String fname) {
		try {
			FileInputStream file = new FileInputStream(fname);
			byte[] buffer = new byte[1];
			
			while(file.read(buffer) > 0) {
				dataOut.writeBoolean(true);
				dataOut.write(buffer);
			}
			dataOut.writeBoolean(false);
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch(IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void showFileSend(String fname) {
		JButton fbutton = new JButton(fname, NewIcon("img/file_1.png", 30, 30));
		fbutton.setForeground(new Color(252, 185, 0));
		fbutton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(SwingUtilities.isLeftMouseButton(e)) {
					try {
						dataOut.writeUTF("downloadfile_" + fbutton.getText());
						dataOut.flush();
					}
					catch(IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		
		JPanel contain = new JPanel();
		contain.setLayout(new BoxLayout(contain, BoxLayout.X_AXIS));
		contain.add(Box.createHorizontalGlue());
		contain.add(fbutton);
		updateChatSP(contain);
	}
	
	public void showFileReceived(String fname, String id) {
		JButton fbutton = new JButton(fname, NewIcon("img/file_2.png", 30, 30));
		fbutton.setForeground(new Color(245, 54, 115));
		fbutton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(SwingUtilities.isLeftMouseButton(e)) {
					try {
						dataOut.writeUTF("downloadfile_" + fbutton.getText());
						dataOut.flush();
					}
					catch(IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		JPanel contain = new JPanel();
		contain.setLayout(new BoxLayout(contain, BoxLayout.X_AXIS));		
		contain.add(fbutton);
		contain.add(Box.createHorizontalGlue());	
		updateChatSP(contain);
	}
	
	public void HandlingMessReceived(String mess, boolean b) {
		if(b == true) {
			String info[] = mess.split("_", 3);
			JPanel tempPanel = curChatPane;
			curChatPane = chatPanes.get(info[1]);
			
			if(info[0].equals("newperson")) { // new person
				JPanel cp = new JPanel();
				cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
				if(chatPanes.containsKey(info[1]) == false) {
					chatPanes.put(info[1], cp);
				}				
				addUserOnline(info[1], info[2]);
			}
			else if(info[0].equals("text")) { // text
				showReceivedMessage(info[2], info[1]);
			}
			else if(info[0].equals("emoji")) { // emoji
				ReceiveEmoji(info[2], info[1]);
			}
			else if(info[0].equals("file")) {
				showFileReceived(info[2], info[1]);
			}
			else if(info[0].equals("removeperson")) {
				removeUser(info[1]);
				tempPanel = null;
			}
			curChatPane = tempPanel;
		}
	}	
	
	
	public void updateChatSP(JPanel contain) {
		if(curChatPane != null) {
//			contain.setBorder(BorderFactory.createLineBorder(new Color(7, 177, 187), 0));
			curChatPane.add(contain);
			curChatPane.add(Box.createRigidArea(new Dimension(0, 10)));
			
			curChatPane.remove(tempane);
			curChatPane.add(tempane);
			chatSP.getVerticalScrollBar().setValue(chatSP.getVerticalScrollBar().getMaximum());
			curChatPane.setVisible(false);
			curChatPane.setVisible(true);
		}
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
					Socket socket = new Socket("localhost", 7800);
					ClientUI clientFrame = new ClientUI("duc", "Duc",socket);
					clientFrame.setVisible(true);
				}
				catch(IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
