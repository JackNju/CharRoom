package chat.client;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class MyFrame extends Frame{
	private TextField tf = null;
	private TextArea ta = null;
	private Socket socket = null;
	private DataInputStream dis = null;
	private DataOutputStream dos = null;
	private boolean start = false;
	
	/*Todo :*/
	private String name = null;
	
	public String getName() {
		return name;
	}

	public MyFrame(String name,String string) {
		super(string);
		this.name = name;
	}

	public void launchFrame() {
		this.setBounds(300, 400, 300, 300);
		tf = new TextField();
		ta = new TextArea();
		connect();
		new Thread(new RecThread()).start();
		tf.addActionListener(new ActionMonitor());
		add(tf,"South");
		add(ta,"Center");
		pack();
		setVisible(true);
		this.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				disconnect();
				System.exit(0);
			}
			
		});
		
	}
	
	public void connect() {
		try {
			socket = new Socket("192.168.1.100",8888);
			System.out.println("客户端已连接到服务器。");
			start = true;
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void disconnect() {
		try {
			dis.close();
			dos.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	class ActionMonitor implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			String s = tf.getText();
			tf.setText("");
			try {
				dos.writeUTF(getName());
				dos.writeUTF(s);
				dos.writeUTF("\n");
				dos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
	}
	
	class RecThread implements Runnable{

		public void run() {
			try {
				while(start) {
					String s = dis.readUTF();
					ta.setText(ta.getText()+'\n'+s);
				}
			}catch(SocketException e) {
				System.out.println("客户端退出 !");
			}catch(IOException e) {
				e.printStackTrace();
			}	
		}
	
	}
	
}


