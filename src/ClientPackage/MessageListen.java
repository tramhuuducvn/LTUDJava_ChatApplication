package ClientPackage;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.*;

class MessageListen implements Runnable{
	private DataInputStream dis;
	private ClientUI client;
	
	public MessageListen(DataInputStream dis, ClientUI client) {
		this.dis = dis;
		this.client = client;
	}
	
	public void downloadFileFromServer(String fname) {
		try {
			dis.read();			
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			while(true) {				
				String mess = dis.readUTF();
				String info[] = mess.split("_", 2);
				Thread.sleep(10);
				
				if(info[0].equals("downfileAccept")) {		
					long size = dis.readLong();
					JFileChooser filechooser = new JFileChooser();
					filechooser.setSelectedFile(new File(info[1]));
					filechooser.showSaveDialog(client.getMainPane());
					File f  = filechooser.getSelectedFile();
					if(f != null) {
						FileOutputStream file = new FileOutputStream(f.getAbsolutePath());
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
				}
				else {
					client.HandlingMessReceived(mess, true);
				}
				Thread.sleep(10);
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		catch(InterruptedException e1) {
			e1.printStackTrace();
		}
		
	}
}