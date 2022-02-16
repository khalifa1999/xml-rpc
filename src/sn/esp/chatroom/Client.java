package sn.esp.chatroom;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;

public class Client
{
	private String title = "Logiciel de discussion en ligne";
	private String pseudo = null;
	private XmlRpcClient server = null;
	private JFrame window = new JFrame(this.title);
	private JTextArea txtOutput = new JTextArea();
	private JTextField txtMessage = new JTextField();
	private JButton btnSend = new JButton("Envoyer");
	
	public void connexion()
	{
		try
		{
			this.server = new XmlRpcClient("http://localhost:8080/RPC2");
		} catch (Exception exception)
		{
			System.err.println("Client: " + exception);
		}
	}
	
	public Client()
	{
		this.createIHM();
		connexion();
		this.requestPseudo();
	}
	
	public void createIHM()
	{
		
		JPanel panel = (JPanel) this.window.getContentPane();
		JScrollPane sclPane = new JScrollPane(txtOutput);
		panel.add(sclPane, BorderLayout.CENTER);
		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.add(this.txtMessage, BorderLayout.CENTER);
		southPanel.add(this.btnSend, BorderLayout.EAST);
		panel.add(southPanel, BorderLayout.SOUTH);
		
		window.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				window_windowClosing(e);
			}
		});
		btnSend.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				btnSend_actionPerformed(e);
			}
		});
		
		txtMessage.addKeyListener(new KeyAdapter()
		{
			public void keyReleased(KeyEvent event)
			{
				if (event.getKeyChar() == '\n')
					btnSend_actionPerformed(null);
			}
		});
		
		this.txtOutput.setBackground(new Color(220, 220, 220));
		this.txtOutput.setEditable(false);
		this.window.setSize(500, 400);
		this.window.setVisible(true);
		this.txtMessage.requestFocus();
	}
	
	public void requestPseudo()
	{
		this.pseudo = JOptionPane.showInputDialog(this.window, "Veuillez saisir votre pseudo svp : ", this.title,
				JOptionPane.OK_OPTION);
		if (this.pseudo == null)
			System.exit(0);
		Vector<String> inscrire = new Vector<String>();
		inscrire.add(this.pseudo);
		boolean inscrireOk;
		try
		{
			inscrireOk = (boolean) this.server.execute("sample.subscribe", inscrire);
			if (inscrireOk)
			{
				// void
			}
			else
			{
				JOptionPane.showMessageDialog(this.window,
						"Ce pseudo existe deja ! Veuillez en choisir un autre pour acceder au salon !");
				requestPseudo();
			}
		} catch (XmlRpcException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void window_windowClosing(WindowEvent e)
	{
		Vector<String> deconnect = new Vector<String>();
		deconnect.add(this.pseudo);
		boolean deconnectOk;
		try
		{
			deconnectOk = (boolean) this.server.execute("sample.unsubscribe", deconnect);
			if (deconnectOk)
			{
				System.exit(-1);
			}
		} catch (XmlRpcException e1)
		{
			e1.printStackTrace();
		} catch (IOException e1)
		{
			e1.printStackTrace();
		}
		
	}
	
	public void btnSend_actionPerformed(ActionEvent e)
	{
		
		Vector<String> message = new Vector<String>();
		message.add(this.pseudo);
		message.add(this.txtMessage.getText());
		try
		{
			@SuppressWarnings("unused")
			String recu = (String) this.server.execute("sample.postMessage", message);
		} catch (XmlRpcException e1)
		{
			e1.printStackTrace();
		} catch (IOException e1)
		{
			e1.printStackTrace();
		}
		
		this.txtMessage.setText("");
		this.txtMessage.requestFocus();
	}
	
	@SuppressWarnings("rawtypes")
	public static void main(String[] args)
	{
		Client chatUserImpl = new Client();
		boolean received = false;
		String msg = "";
		String last_msg = "";
		boolean is_first = false;
		while (true)
		{
			try
			{
				msg = (String) chatUserImpl.server.execute("sample.getMessage", new Vector());
				received = true;
				if (is_first == false)
				{
					last_msg = msg;
				}
			} catch (Exception e)
			{
				received = false;
			} finally
			{
				if (received)
				{
					if (is_first)
					{
						if (!last_msg.equals(msg))
						{
							chatUserImpl.txtOutput.append(msg + " \n");
							last_msg = msg;
						}
					}
					else
					{
						
						chatUserImpl.txtOutput.append(msg + " \n");
						last_msg = msg;
						is_first = true;
					}
				}
			}
		}
	}
}
