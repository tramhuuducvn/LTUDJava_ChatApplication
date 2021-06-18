package ServerPackage;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import javax.swing.table.*;

class Reception implements Runnable{
	private String id;
	private String pass;
	private String name;
	private Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;
	
	DefaultTableModel table;
	private boolean isOnline;
	Object lock;
	
	private boolean detonator;

	public Reception(String id, String pass, String name, Socket socket, DefaultTableModel table, Object lock) throws IOException{
		this.isOnline = true;
		this.id = id;
		this.pass = pass;
		this.name = name;
		this.socket = socket;
		this.dis = new DataInputStream(socket.getInputStream());
		this.dos = new DataOutputStream(socket.getOutputStream());
		this.table = table;
		this.detonator = true;
		this.lock = lock;
	}	

	public Reception(String id, String pass, String name, DefaultTableModel table, Object lock) {
		this.isOnline = false;
		this.id = id;
		this.pass = pass;
		this.name = name;
		this.table = table;
		this.detonator = true;
		this.lock = lock;
	}
	
	public void run() {
		try{
			if(detonator == true) {				
				for(String key : ChatServer.reps.keySet()) {
					if(key.equals(id) == false) {
						Reception temp = ChatServer.reps.get(key);
						if(temp.isOnline()) {
							temp.sendNewPerson(id, name);
							dos.writeUTF("newperson_" + temp.getId() + "_" + temp.getName());
							dos.flush();
						}
					}
					Thread.sleep(15);
				}
				detonator = false;
			}
			
			while(true) {				
				String m = dis.readUTF();
				Thread.sleep(27);
				
				if(m.equals("close")) {
					isOnline = false;										
					for(int i = 0; i < table.getRowCount(); ++i) {
						if(table.getValueAt(i, 0).toString().equals(id)) {
							table.removeRow(i);
							break;
						}
					}
					
					for(String key : ChatServer.reps.keySet()) {
						if(key.equals(id) == false) {
							Reception temp = ChatServer.reps.get(key);
							if(temp.isOnline()) {
								temp.removePerson(id);
							}
						}
						Thread.sleep(15);
					}
					dis.close();
					socket.close();
					socket = null;
					break;
				}					
				else {
					String info[] = m.split("_", 2);
					if(info[0].equals("text")) {
						info = m.split("_", 3);
						Reception temp = ChatServer.reps.get(info[1]);
						temp.sendTextMessage("text_" + id + "_" + info[2]);
					}
					else if(info[0].equals("emoji")) {
						info = m.split("_", 3);
						Reception temp = ChatServer.reps.get(info[1]);
						temp.sendEmojiMessage("emoji_" + id + "_" + info[2]);
					}
					else if(info[0].equals("file")){
						info = m.split("_", 3);
						long size = dis.readLong();
						Reception temp = ChatServer.reps.get(info[1]);
						temp.sendFileMessage("file_" + id + "_" + info[2]);
						
						FileOutputStream file = new FileOutputStream("dataserver/filestore/" + info[2]);
						byte[] buffer = new byte[4096];
						while(size >= 4096) {
							dis.read(buffer);
							file.write(buffer);
							size -= 4096;
						}
						if(size > 0) {
							dis.read(buffer);
							file.write(buffer, 0, (int)size);
						}
//						while(dis.readBoolean() == true) {
//							dis.read(buffer);
//							file.write(buffer);
//						}
					}
					else if(info[0].equals("downloadfile")){
						File f = new File("dataserver/filestore/" + info[1]);
						long size = f.length();
						FileInputStream file = new FileInputStream("dataserver/filestore/" + info[1]);
						byte[] buffer = new byte[4096];
						dos.writeUTF("downfileAccept_" + info[1]);
						dos.flush();						
						dos.writeLong(size);
						dos.flush();
						while(file.read(buffer) > 0) {							
							dos.write(buffer);
						}
					}
				}
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
		catch(InterruptedException e1) {
			e1.printStackTrace();
		}
	}
	//--------------Actions------------------------------------------------
	public void sendNewPerson(String acc, String name) {
		synchronized (lock) {
			try {
				dos.writeUTF("newperson_" + acc + "_" + name);
				dos.flush();
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void removePerson(String id) {
		synchronized (lock) {
			try {
				dos.writeUTF("removeperson_" + id + "_" + id);
				dos.flush();
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void sendTextMessage(String textmess) {
		synchronized (lock) {
			try {
				dos.writeUTF(textmess);
				dos.flush();
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void sendEmojiMessage(String textmess) {
		synchronized (lock) {
			try {
				dos.writeUTF(textmess);
				dos.flush();
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void sendFileMessage(String textmess) {
		synchronized (lock) {
			try {
				dos.writeUTF(textmess);
				dos.flush();
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	// getter setter------------------------------------------------------
	public boolean isOnline() {
		return isOnline;
	}

	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
		try {
			this.dis = new DataInputStream(socket.getInputStream());
			this.dos = new DataOutputStream(socket.getOutputStream());
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	public DataInputStream getDis() {
		return dis;
	}

	public void setDis(DataInputStream dis) {
		this.dis = dis;
	}

	public DataOutputStream getDos() {
		return dos;
	}

	public void setDos(DataOutputStream dos) {
		this.dos = dos;
	}
	
	public DefaultTableModel getTable() {
		return table;
	}

	public void setTable(DefaultTableModel table) {
		this.table = table;
	}
	
	
	//------------------------------------0--------------------------------
		
	
}