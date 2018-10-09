import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import java.awt.Font;
import java.awt.Image;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.awt.Color;
import java.awt.SystemColor;

public class FTP1 extends JFrame{
	Socket ctrlSocket;
	public String host;
	public PrintWriter ctrlOutput;
	String msg;
	public BufferedReader ctrlInput;
	public AtomicInteger dataPort = new AtomicInteger(0);
	public AtomicBoolean isConnected = new AtomicBoolean(false);
	
	private JPanel contentPane;
	private JTextField textField;
	private JPasswordField passwordField;
	private JButton btnConnect;
	private static TextArea textArea_1;
	private static JLabel lblFolder;
	private Thread listenerthread;
	private TextArea textArea;
	private File folder;
	private JButton btnChangeFolder;
	private JButton btnUpload;
	private JButton btnDown;
	private JButton btnNewDirection;
	private JButton btnDelDir;
	private JButton btnDelFile;
	private JButton btnFiledetail;
	
	final int CTRLPORT=21;
	public FTP1() {
		setTitle("ftp FileTransfer");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 989, 728);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(240, 255, 240));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textField = new JTextField();
		textField.setFont(new Font("Times New Roman", Font.PLAIN, 27));
		textField.setBounds(132, 23, 206, 40);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel lblAccount = new JLabel("account:");
		lblAccount.setFont(new Font("Times New Roman", Font.PLAIN, 27));
		lblAccount.setBounds(34, 26, 103, 34);
		contentPane.add(lblAccount);
		
		JLabel lblPassword = new JLabel("password:");
		lblPassword.setFont(new Font("Times New Roman", Font.PLAIN, 27));
		lblPassword.setBounds(362, 26, 127, 34);
		contentPane.add(lblPassword);
		
		passwordField = new JPasswordField();
		passwordField.setFont(new Font("Times New Roman", Font.PLAIN, 27));
		passwordField.setBounds(497, 23, 186, 40);
		contentPane.add(passwordField);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(518, 152, 388, 365);
		contentPane.add(panel_1);
		panel_1.setLayout(null);
		
		textArea_1 = new TextArea();
		textArea_1.setFont(new Font("Times New Roman", Font.PLAIN, 27));
		textArea_1.setEditable(false);
		textArea_1.setBounds(0, 0, 388, 362);
		panel_1.add(textArea_1);
		
		btnConnect = new JButton("connect");
		btnConnect.setBackground(new Color(255, 215, 0));
		btnConnect.setFont(new Font("Times New Roman", Font.PLAIN, 27));
		btnConnect.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
				if(btnConnect.getText().equals("connect")) {
					doLogin();
					doLs();
					doPWD();
					folder=new File("./");
					String[] list=folder.list();
					for(int i=0;i<list.length;++i)
						textArea.setText(textArea.getText()+list[i]+"\n");
					btnChangeFolder.setEnabled(true);
					btnUpload.setEnabled(true);
					btnDown.setEnabled(true);
					btnNewDirection.setEnabled(true);
					btnDelDir.setEnabled(true);
					btnDelFile.setEnabled(true);
					btnFiledetail.setEnabled(true);
				}
				else {
					doQuit();
				}
			}
		});
		btnConnect.setBounds(744, 22, 191, 43);
		contentPane.add(btnConnect);
		
		JPanel panel = new JPanel();
		panel.setBounds(37, 152, 398, 365);
		contentPane.add(panel);
		panel.setLayout(null);
		
		textArea = new TextArea();
		textArea.setFont(new Font("Times New Roman", Font.PLAIN, 27));
		textArea.setEditable(false);
		textArea.setBounds(0, 0, 398, 365);
		panel.add(textArea);
		
		btnNewDirection = new JButton("new Direction");
		btnNewDirection.setEnabled(false);
		btnNewDirection.setBackground(new Color(245, 222, 179));
		btnNewDirection.setFont(new Font("Times New Roman", Font.PLAIN, 27));
		btnNewDirection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String input=JOptionPane.showInputDialog("請輸入目錄名稱");
				try {
					doMKD(input);
					textArea_1.setText("");
					doLs();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnNewDirection.setBounds(518, 532, 206, 43);
		contentPane.add(btnNewDirection);
		
		btnDelDir = new JButton("del Direction");
		btnDelDir.setEnabled(false);
		btnDelDir.setBackground(new Color(245, 222, 179));
		btnDelDir.setFont(new Font("Times New Roman", Font.PLAIN, 27));
		btnDelDir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String input=JOptionPane.showInputDialog("請輸入目錄名稱");
				try {
					doRMD(input);
					textArea_1.setText("");
					doLs();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnDelDir.setBounds(521, 583, 203, 43);
		contentPane.add(btnDelDir);
		
		btnDelFile = new JButton("del file");
		btnDelFile.setEnabled(false);
		btnDelFile.setFont(new Font("Times New Roman", Font.PLAIN, 27));
		btnDelFile.setBackground(new Color(245, 222, 179));
		btnDelFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String input=JOptionPane.showInputDialog("請輸入檔案名稱");
				try {
					doDel(input);
					textArea_1.setText("");
					doLs();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnDelFile.setBounds(755, 532, 127, 43);
		contentPane.add(btnDelFile);
		
		btnChangeFolder = new JButton("change folder");
		btnChangeFolder.setEnabled(false);
		btnChangeFolder.setBackground(new Color(210, 105, 30));
		btnChangeFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String input=JOptionPane.showInputDialog("請輸入目錄名稱");
				doCd(input);
				doPWD();
				textArea_1.setText("");
				doLs();
			}
		});
		btnChangeFolder.setFont(new Font("Times New Roman", Font.PLAIN, 27));
		btnChangeFolder.setBounds(744, 99, 191, 43);
		contentPane.add(btnChangeFolder);
		
		lblFolder = new JLabel("folder");
		lblFolder.setFont(new Font("Times New Roman", Font.PLAIN, 27));
		lblFolder.setBounds(517, 103, 228, 34);
		contentPane.add(lblFolder);
		
		btnUpload = new JButton("");
		btnUpload.setEnabled(false);
		btnUpload.setIcon(new ImageIcon("Webp.net-resizeimage.png"));
		btnUpload.setFont(new Font("Times New Roman", Font.PLAIN, 27));
		btnUpload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String input=JOptionPane.showInputDialog("請輸入檔案名稱");
				Object[] pattern= {"Binary","Ascii"};
				
				String mode=(String) JOptionPane.showInputDialog(null,"請選擇資料傳送方式","transferMode",JOptionPane.QUESTION_MESSAGE,null,pattern,pattern[0]); 
				System.out.println("mode"+mode);
				if(mode.equals("Binary")) {
					doBinary();
				}
				else {
					doAscii();
				}
				doPut(input);
				textArea_1.setText("");
				
				try {
					listenerthread.sleep(2000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				doLs();
			}
		});
		btnUpload.setBounds(446, 248, 56, 43);
		contentPane.add(btnUpload);
		
		btnDown = new JButton("");
		btnDown.setEnabled(false);
		btnDown.setIcon(new ImageIcon("Right.png"));
		btnDown.setFont(new Font("Times New Roman", Font.PLAIN, 27));
		btnDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				String input=JOptionPane.showInputDialog("請輸入檔案名稱");
				Object[] pattern= {"Binary","Ascii"};
				
				String mode=(String) JOptionPane.showInputDialog(null,"請選擇資料傳送方式","transferMode",JOptionPane.QUESTION_MESSAGE,null,pattern,pattern[0]); 
				System.out.println("mode"+mode);
				if(mode.equals("Binary")) {
					doBinary();
				}
				else {
					doAscii();
				}
				doGet(input);
				folder=new File("./");
				String[] list=folder.list();
				textArea.setText("");
				for(int i=0;i<list.length;++i)
					textArea.setText(textArea.getText()+list[i]+"\n");
			}
		});
		btnDown.setBounds(447, 354, 56, 43);
		contentPane.add(btnDown);
		
		btnFiledetail = new JButton("FileDetail");
		btnFiledetail.setEnabled(false);
		btnFiledetail.setBackground(new Color(245, 222, 179));
		btnFiledetail.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(btnFiledetail.getText().equals("FileDetail")) {
					textArea_1.setText("");
					doLsD();
					btnFiledetail.setText("FileName");
				}
				else {
					textArea_1.setText("");
					doLs();
					btnFiledetail.setText("FileDetail");
				}
			}
		});
		btnFiledetail.setFont(new Font("Times New Roman", Font.PLAIN, 27));
		btnFiledetail.setBounds(753, 583, 153, 43);
		contentPane.add(btnFiledetail);
	}
	
	public void openConnection(String host) throws IOException,UnknownHostException
	{
		this.host=host;
		ctrlSocket=new Socket(host,CTRLPORT);
		ctrlOutput=new PrintWriter(ctrlSocket.getOutputStream());
		ctrlInput=new BufferedReader(new InputStreamReader(ctrlSocket.getInputStream()));
	}
	
	public void closeConnection() throws IOException{
		ctrlSocket.close();
	}
	
	public void doCd(String dir){
		try{
			ctrlOutput.println("CWD "+dir);
			ctrlOutput.flush();
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}
	public void doLs(){

			try{
				int n;
				byte[] buff=new byte[65530];
				Socket dataSocket=dataConnection("NLST ");
				BufferedInputStream dataInput=new BufferedInputStream(dataSocket.getInputStream());
				
				while((n=dataInput.read(buff))>0){
					textArea_1.setText(textArea_1.getText()+new String(buff, 0, n));
					System.out.write(buff,0,n);
				}
				dataSocket.close();
			}catch(Exception ee){
				ee.printStackTrace();
				System.exit(1);
			}

	}
	public void doLsD(){

			try{
				int n;
				byte[] buff=new byte[65530];
				Socket dataSocket=dataConnection("LIST ");
				BufferedInputStream dataInput=new BufferedInputStream(dataSocket.getInputStream());
				
				while((n=dataInput.read(buff))>0){
					textArea_1.setText(textArea_1.getText()+new String(buff, 0, n));
					System.out.write(buff,0,n);
				}
				dataSocket.close();
			}catch(Exception ee){
				ee.printStackTrace();
				System.exit(1);
			}

	}
	public Socket dataConnection(String ctrlcmd){
		String cmd="PASV ";
		int i;
		Socket dataSocket=null;
		try{
			byte[] address=InetAddress.getLocalHost().getAddress();
			
			if (!isConnected.get()) {
				dataSocket = new Socket();
				for(i=0;i<4;++i){
					cmd=cmd+(address[i]&0xff)+",";
				}
				cmd=cmd+(((dataSocket.getLocalPort())/256)&0xff)+","+(dataSocket.getLocalPort()&0xff);
				ctrlOutput.println(cmd);
				ctrlOutput.flush();
				while (!isConnected.get()) {
					Thread.sleep(1000);
				}
				try {
					dataSocket.connect(new InetSocketAddress(host, dataPort.get()), 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
				isConnected.set(false);
				
			}
			ctrlOutput.println(ctrlcmd);
			ctrlOutput.flush();

		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
		return dataSocket;
	}

	public void doGet(String fileName){
		
		try{
			int n;
			byte[] buff=new byte[1024];

			FileOutputStream outfile=new FileOutputStream(fileName);
			Socket dataSocket=dataConnection("RETR "+fileName);
			BufferedInputStream dataInput=new BufferedInputStream(dataSocket.getInputStream());
			while((n=dataInput.read(buff))>0){
				outfile.write(buff, 0, n);
			}
			dataSocket.close();
			outfile.close();
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}
	public void doPut(String fileName){
		
		try{
			int n;
			byte[] buff=new byte[1024];
			
			FileInputStream sendfile=new FileInputStream(fileName);
			Socket dataSocket=dataConnection("STOR "+fileName);
			OutputStream outstr=dataSocket.getOutputStream();
			while((n=sendfile.read(buff))>0)
				outstr.write(buff,0,n);
			dataSocket.close();
			sendfile.close();
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}
	public void doAscii(){
		try{
			ctrlOutput.println("TYPE A");
			ctrlOutput.flush();
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}
	public void doBinary(){
		try{
			ctrlOutput.println("TYPE I");
			ctrlOutput.flush();
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}
	public void doLogin(){
		String loginName="";
		String password="";
		try{
			loginName=textField.getText();
			ctrlOutput.println("USER "+loginName);
			password=passwordField.getText();
			ctrlOutput.println("PASS "+password);
			btnConnect.setText("DisConnect");
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void doQuit(){
		try{
			ctrlOutput.println("QUIT ");
			System.exit(0);
			closeConnection();
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}
	public void doDel(String file) throws IOException {

		ctrlOutput.println("DELE "+file);
		ctrlOutput.flush();
		
	}
	public void doRMD(String dir) throws IOException {

		ctrlOutput.println("RMD "+dir);
		ctrlOutput.flush();
	}
	public void doMKD(String dir) throws IOException {

		ctrlOutput.println("MKD "+dir);
		ctrlOutput.flush();
	}
	public void doPWD() {
		ctrlOutput.println("PWD ");
		ctrlOutput.flush();
	}
	
	public void main_proc(){
		while(true);
	}
	
	public void getMsgs(){
		try{
			CtrlListen listener=new CtrlListen(ctrlInput, dataPort, isConnected);
			listenerthread=new Thread(listener);
			listenerthread.start();
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void main(String[] arg){
		try{
			FTP1 f=new FTP1();
			f.setVisible(true);
			f.openConnection("localhost");
			f.getMsgs();
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}
	public class CtrlListen implements Runnable{

		BufferedReader ctrlInput = null;
		AtomicBoolean isConnected = null;
		AtomicInteger dataPort = null;

		public CtrlListen(BufferedReader in, AtomicInteger dataPort, AtomicBoolean isConnected) {
			ctrlInput = in;
			this.dataPort = dataPort;
			this.isConnected = isConnected;
		}

		public void run() {
			String msg = null;
			while (true) {
				try {
					
					msg = ctrlInput.readLine();
					System.out.println(msg);
					if(msg.startsWith("257")) {
						msg=msg.substring(5);
						String[] dir=msg.split("\" ");
						if(dir[1].startsWith("is"))
							FTP1.lblFolder.setText("Folder: "+dir[0]+"\n"); 
					}
					if (msg.startsWith("227") && !isConnected.get()) {
	            		int startIndex = msg.indexOf('(') + 1, endIndex = msg.indexOf(')');
	            		String[] addr = msg.substring(startIndex, endIndex).split(",");
	            		dataPort.set(256 * Integer.valueOf(addr[4]) + Integer.valueOf(addr[5]));
	            		isConnected.set(true);
	            	}
				} catch (Exception e) {
					System.exit(1);
				}
			}
		}
}
}
