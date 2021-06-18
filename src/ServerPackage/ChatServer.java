package ServerPackage;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import javax.swing.table.*;


//--------------------------------------------------------------------------
public class ChatServer extends Thread{
	ServerSocket serversocket;
	boolean status = true;
	DefaultTableModel table;	
	static HashMap<String, Reception> reps = new HashMap<String, Reception>();	
	private Object lock;
	
	
	public ChatServer(int port, DefaultTableModel table) {
		status = true;
		this.table = table;
		this.lock = new Object();
		
		try {
			serversocket = new ServerSocket(port);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		try {
			File infile = new File("dataserver/user.txt");
			FileReader fileReader = new FileReader(infile);
			BufferedReader bufReader = new BufferedReader(fileReader);
			String line = null;
			while((line = bufReader.readLine()) != null) {
				String info[] = line.split("_", 3);					
				Reception temp = new Reception(info[0], info[1], info[2], table, lock);
				reps.put(info[0], temp);
			}
			bufReader.close();
			fileReader.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		try {
			while(status == true) {
				Socket socket = serversocket.accept();
				DataInputStream dataIn = new DataInputStream(socket.getInputStream());
				DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());				
				
				String signal = dataIn.readUTF();				
				if(signal.equals("login")){					
					dataOut.writeUTF("acc_pass");
					
					String acc = dataIn.readUTF();
					String pass = dataIn.readUTF();				
					
					if(reps.containsKey(acc)) {
						Reception r = reps.get(acc);
						if(r.getId().equals(acc) && r.getPass().equals(pass)) {
							if(r.isOnline() == false) {
								dataOut.writeUTF("login_success");
								dataOut.writeUTF(r.getName());
								dataOut.flush();					
								r.setSocket(socket);
								r.setOnline(true);
								Thread t = new Thread(r);
								t.start();
								Object obs[] = {acc};
								table.addRow(obs);						
							}
							else {
								dataOut.writeUTF("other_logged_this_acc");
//								dataOut.flush();
							}
						}
						else {
							dataOut.writeUTF("login_fail_pass");
						}
					}
					else {
						dataOut.writeUTF("login_fail_acc");
					}
										
				}
				else if(signal.equals("signup")){	
					dataOut.writeUTF("acc_pass_name");
					String acc = dataIn.readUTF();
					String pass = dataIn.readUTF();
					String name = dataIn.readUTF();
					
					if(reps.containsKey(acc)) {
						dataOut.writeUTF("existed_acc");
					}					
					else{		
						Reception temp = new Reception(acc, pass, name, socket, table, lock);
						reps.put(acc, temp);
						dataOut.writeUTF("signup_success");
						dataOut.flush();
						Thread t = new Thread(temp);
						t.start();
						Object obs[] = {acc};
						table.addRow(obs);						
						Files.write(Paths.get("dataserver/user.txt"), ("\n" + acc + "_" + pass + "_" + name).getBytes(), StandardOpenOption.APPEND);
					}
				}					
			}
		}
		catch(SocketException e1) {
//			e1.printStackTrace();
		}
		catch(IOException e2) {			
//			e2.printStackTrace();
		}
	}
	
	public void kill() {
		try {
			serversocket.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}		
		status = false;
	}
	
	public void setTable(DefaultTableModel table) {
		this.table = table;
	}
	
	public static void main(String[] args) {
		DefaultTableModel table = new DefaultTableModel();
		ChatServer a = new ChatServer(7800, table);
		a.start();
//		a.kill();
	}
}
