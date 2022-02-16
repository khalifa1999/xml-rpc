package sn.esp.chatroom;

import java.util.Vector;

import org.apache.xmlrpc.WebServer;


public class Serveur
{
	
	private static Vector<String> user = new Vector<String>();
	String msg_courant = null;
	
	public static void main(String[] args)
	{
		try
		{
			System.out.println("[*] Tentative de démarrage du serveur ...");
			WebServer server = new WebServer(8080);
			server.addHandler("sample", new Serveur());
			server.start();
			System.out.println("[*] Demarrage avec succes.");
			System.out.println("[*] En attente des prochaines requetes. (CTRL-C pour stopper.)");
		} catch (Exception exception)
		{
			System.err.println("[*] Serveur Java: " + exception);
		}
	}
	
	public boolean subscribe(String pseudo)
	{
		boolean ok = false;
		if (!user.contains(pseudo))
		{
			user.add(pseudo);
			ok = true;
		}
		postMessage(pseudo, " s'est connecte.");
		return ok;
	}
	
	public String postMessage(String pseudo, String message)
	{
		String messageEntier;
		if (message.endsWith(" s'est connecte).") || message.endsWith("s'est deconnecte(e)."))
		{
			messageEntier = "[" + pseudo + "]" + message;
		}
		else
		{
			messageEntier = "[" + pseudo + "] >>> " + message;
		}
		// System.out.println(messageEntier);
		msg_courant = messageEntier;
		return messageEntier;
	}
	
	public String getMessage()
	{
		return msg_courant;
	}
	
	public boolean unsubscribe(String pseudo)
	{
		boolean ok = false;
		if (user.contains(pseudo))
		{
			user.remove(pseudo);
			ok = true;
			postMessage(pseudo, " s'est deconnecte(e).");
		}
		return ok;
	}
	
}
