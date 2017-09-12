import java.awt.Dimension;
import java.awt.Label;
import java.awt.Rectangle;
import java.awt.TextField;
import java.util.HashMap;

import eu.telecomlille.sdl.IProcess;
import eu.telecomlille.sdl.ISignal;
import eu.telecomlille.sdl.Stepper;
import eu.telecomlille.sdl.tcp.TCPLayer;
import eu.telecomlille.sdlchat.client.ChatClient;
import eu.telecomlille.sdlchat.client.ServerProxy;
import eu.telecomlille.sdlchat.server.ChatServer;
import eu.telecomlille.sdlchat.signals.ConnectUI;
import eu.telecomlille.sdlchat.signals.ConnectedUI;
import eu.telecomlille.sdlchat.signals.DataUI;
import eu.telecomlille.sdlchat.signals.Disconnect;
import eu.telecomlille.sdlchat.signals.DisconnectedUI;

/**
 * A ChatClient GUI.
 * 
 * @author C. TOMBELLE
 */
public class ChatClientGUI extends javax.swing.JFrame implements IProcess {
	private static final long serialVersionUID = 9050725683083146683L;
	private ChatClient _chatClient;
	private javax.swing.JButton _btnConnect;
	private javax.swing.JButton _btnSend;
	private javax.swing.JButton _btnDisconnect;
	private javax.swing.JLabel _jlblLeft;
	private javax.swing.JLabel _jlblMembersList;
	private javax.swing.JLabel _jlblRight;
	private javax.swing.JMenuBar _jmbStd;
	private javax.swing.JMenuItem _jmiExit;
	private javax.swing.JMenu _jmnFile;
	private javax.swing.JPanel _jpnlMain;
	private javax.swing.JPanel _jpnlStatus;
	private javax.swing.JToolBar _jtlbStd;
	private java.awt.Label _lblToSend;
	private java.awt.TextField _txfToSend;
	private java.awt.Label _lblReceived;
	private java.awt.TextField _txfReceived;
	private java.awt.List _lstMembers;
	private HashMap<String, IProcess> _hmMembers;

	/**
	 * Create an instance of ChatClientGUI.
	 * 
	 * @param server
	 *            ChatServer to test with no transport layer
	 */
	public ChatClientGUI(ChatServer server, String name) {
		initComponents();
		_hmMembers = new HashMap<String, IProcess>();
		initMembers();

		if (server != null) {
			// create the ChatClient
			_chatClient = new ChatClient(name);
			// make it know its GUI
			_chatClient.setEnv(this);

			// make the ChatClient know the server
			_chatClient.setR2Process(server);
		} else {
			// create the ChatClient
			_chatClient = new ChatClient(null);
			// make it know its GUI
			_chatClient.setEnv(this);

			// create server proxy
			TCPLayer sock1 = new TCPLayer();
			sock1.setParent(null, null);
			ServerProxy sp1 = new ServerProxy("localhost", 13455);
			sp1.setRTCP(sock1);

			// make the ChatClient know its ServerProxy
			_chatClient.setR2Process(sp1);
			// and the ServerProxy know its ChatClient
			sp1.setR2(_chatClient);
			// start ServerProxy
			sp1.setParent(null, null);
		}
		// and the ChatClient
		_chatClient.setParent(null, null);

//		Toolkit tk = Toolkit.getDefaultToolkit();
//		Dimension dimScreen = tk.getScreenSize();
//		setMaximizedBounds(new Rectangle(dimScreen));
//		setExtendedState(MAXIMIZED_BOTH);
		setBounds(new Rectangle(new Dimension(400, 320)));
//		add(new ConnectedUI(_chatClient, _chatClient));
	}

	protected void initMembers() {
		String strNull = null;
		_lstMembers.add(strNull);
		_hmMembers.put(null, null);
	}

	protected void resetMembers() {
		_lstMembers.removeAll();
		_hmMembers.clear();
		initMembers();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	private void initComponents() {
		setTitle("Chat Client");
		// the menu bar wih only File /Exit
		_jmbStd = new javax.swing.JMenuBar();
		_jmnFile = new javax.swing.JMenu();
		_jmiExit = new javax.swing.JMenuItem();
		// create the toolbar with its buttons
		_jtlbStd = new javax.swing.JToolBar();
		_btnConnect = new javax.swing.JButton();
		_btnSend = new javax.swing.JButton();
		_btnDisconnect = new javax.swing.JButton();
		// create client zone
		_jpnlMain = new javax.swing.JPanel();
		// with the text field for message to send
		_lblToSend = new Label("To send :");
		_txfToSend = new TextField();
		// with the text field for the received message
		_lblReceived = new Label("Received : ");
		_txfReceived = new TextField();
		// with the list of members
		_lstMembers = new java.awt.List();
		_jlblMembersList = new javax.swing.JLabel();
		// Create the status bar
		_jpnlStatus = new javax.swing.JPanel();
		_jlblLeft = new javax.swing.JLabel();
		_jlblRight = new javax.swing.JLabel();

		// define event handlers
		// react to window events
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				exitForm(evt);
			}
		});
		// Connect button
		_btnConnect.setText("Connect");
		_btnConnect.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				connectActionPerformed(evt);
			}
		});
		_jtlbStd.add(_btnConnect);
		// Send button
		_btnSend.setText("Send");
		_btnSend.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				sendActionPerformed(evt);
			}
		});
		_jtlbStd.add(_btnSend);
		// Disconnect button
		_btnDisconnect.setText("Disconnect");
		_btnDisconnect.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				disconnectActionPerformed(evt);
			}
		});
		_jtlbStd.add(_btnDisconnect);
		
		// layout the client zone
		getContentPane().add(_jtlbStd, java.awt.BorderLayout.NORTH);
		_jpnlMain.setLayout(null);
		
		_lblToSend.setBounds(10, 10, 60, 20);
		_jpnlMain.add(_lblToSend);
		_jpnlMain.add(_txfToSend);
		_txfToSend.setBounds(90, 10, 270, 20);

		_lblReceived.setBounds(10, 40, 60, 20);
		_jpnlMain.add(_lblReceived);
		_jpnlMain.add(_txfReceived);
		_txfReceived.setBounds(90, 40, 270, 20);

		_jlblMembersList.setText("Connected members are :");
		_jpnlMain.add(_jlblMembersList);
		_jlblMembersList.setBounds(100, 60, 170, 16);
		_jpnlMain.add(_lstMembers);
		_lstMembers.setBounds(90, 80, 270, 130);

		getContentPane().add(_jpnlMain, java.awt.BorderLayout.CENTER);
		_jpnlStatus.setLayout(new java.awt.GridLayout());
		_jlblLeft.setText("Ready.");
		_jpnlStatus.add(_jlblLeft);
		_jpnlStatus.add(_jlblRight);
		getContentPane().add(_jpnlStatus, java.awt.BorderLayout.SOUTH);
		_jmnFile.setText("File");
		_jmiExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_X,
				java.awt.event.InputEvent.ALT_MASK));
		_jmiExit.setText("Exit");
		_jmiExit.setToolTipText("Exit the application.");
		_jmiExit.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				exitActionPerformed(evt);
			}
		});
		_jmnFile.add(_jmiExit);
		_jmbStd.add(_jmnFile);
		setJMenuBar(_jmbStd);
		pack();
	}

	/**
	 * Connect button action performed event handling.
	 */
	private void connectActionPerformed(java.awt.event.ActionEvent evt) {
		_chatClient.add(new ConnectUI(this));
	}

	/**
	 * Connect button action performed event handling.
	 */
	private void sendActionPerformed(java.awt.event.ActionEvent evt) {
		String strMember = _lstMembers.getSelectedItem();
		IProcess pidMember = _hmMembers.get(strMember);
		String strMsg = _txfToSend.getText();
		_chatClient.add(new DataUI(this, strMsg, pidMember));
	}

	/**
	 * Disconnect button action performed event handling.
	 */
	private void disconnectActionPerformed(java.awt.event.ActionEvent evt) {
		_chatClient.add(new Disconnect(this));
	}

	/**
	 * Normal application exit operation.
	 */
	private void normalExit() {
		System.exit(0);
	}

	/**
	 * File / Exit event.
	 */
	private void exitActionPerformed(java.awt.event.ActionEvent evt) {
		normalExit();
	}

	/** Exit the Application */
	private void exitForm(java.awt.event.WindowEvent evt) {
		normalExit();
	}

	/**
	 * Called when a signal is received.
	 * 
	 * @param sig
	 *            the received signal
	 */
	public void add(ISignal sig) {
		// Make the signal visible in the GUI.
		if (sig instanceof ConnectedUI) {
			ConnectedUI cui = (ConnectedUI) sig;
			IProcess pidMember = cui.p1;
			String strMember = pidMember.toString();
			if (pidMember == _chatClient)
				_jlblLeft.setText("Connected as "+strMember);
			else
				_lstMembers.add(strMember);
			_hmMembers.put(strMember, pidMember);
		} else if (sig instanceof DataUI) {
			DataUI dui = (DataUI) sig;
			_txfReceived.setText(dui.p2+" : "+dui.p1);
		} else if (sig instanceof DisconnectedUI) {
			DisconnectedUI dui = (DisconnectedUI) sig;
			IProcess pidMember = dui.p1;
			if (pidMember == _chatClient) {
				resetMembers();
				_jlblLeft.setText("Disconnected.");
			} else {
				String strMember = pidMember.toString();
				try {
					_lstMembers.remove(strMember);
				} catch (Exception e) {
				}
				_hmMembers.remove(strMember);
			}
		}
	}

	/**
	 * When a process is created at runtime its PARENT is not null.
	 * 
	 * @param pidProc
	 *            the parent PROCESS.
	 */
	public void setParent(IProcess pidProc, Stepper s) {
	}

	/**
	 * Run the ChatClient application with its GUI.
	 * 
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		ChatClientGUI chatClientGUI = new ChatClientGUI(null, null);
		chatClientGUI.setVisible(true);
	}
}