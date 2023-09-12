/*
 * FinalProject_Client Command.java
 * EE422C Final Project submission by
 * Replace <...> with your actual data.
 * Jan Rubio
 * jcr4698
 * 17125
 * Slip days used: <0>
 * Spring 2021
 */

package Client;

public class Command {
	public String command;
	public String input;
	public int port;
	public String username;
	public String password;
	public String item;
	public String[] auction_arr;
	public String description;
	
	public Command(String input, String cmd, int port) {
		this.command = cmd;
		this.input = input;
		this.port = port;
		this.username = "";
		this.password = "";
		this.item = "";
	}
	
	protected void tryToLogin(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	protected void clientPort(int port) {
		this.port = port;
	}
	
	protected void bidItem(String item) {
		this.item = item;
	}
	
	protected void setDescription(String desc) {
		description = desc;
	}
}
