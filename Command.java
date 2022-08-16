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

package client;

public class Command {
	String command;
	String input;
	int port;
	String username;
	String password;
	String item;
	String description;
	
	protected Command(String input, String cmd, int port) {
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
