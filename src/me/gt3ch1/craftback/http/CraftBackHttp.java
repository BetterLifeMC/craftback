package me.gt3ch1.craftback.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketImpl;

import me.gt3ch1.craftback.Main.Main;

public class CraftBackHttp {

	public static void printHeaders(PrintWriter out) {
		out.println("HTTP/1.0 200 OK");
		out.println("Content-Type: text/text");
		out.println("Access-Control-Allow-Origin: *");
		out.println("");
	}

	@SuppressWarnings("resource")
	public static void start(int p) throws IOException {

		ServerSocket ss = new ServerSocket(p);

		while (true) {
			try {

				Socket s = ss.accept();
				BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
				PrintWriter out = new PrintWriter(s.getOutputStream());
				String string = br.readLine();
				String getString = "";
				String url = "";
				try {

					getString = string.split("/\\?")[1].split(" ")[0];
					url = string.split("/\\?")[0].split(" ")[1];

				} catch (ArrayIndexOutOfBoundsException e) {

					if (url.length() < 6) {

						url = string.split(" ")[1];

					}

				}

				if (url.equals("/sendMessage")) {
					
					
					
					String[] parameters = getString.split("=");
				
					if (parameters[0].equals("message")) {
						printHeaders(out);
						out.println("ok");
						Main.setCommand(parameters[1].replace("+", " ").replace("%20", " ").replace("%3F", "?"));
					}

				} else if (url.contentEquals("/getLog")) {

					File f = null;
					f = new File("logs/latest.log");
					printHeaders(out);

					FileReader fr = new FileReader(f);

					while (true) {

						int read = fr.read();

						if (read == -1) {
							break;
						}

						out.write(read);

					}
				} else if (url.contentEquals("/getPlayerUUIDS")) {
					printHeaders(out);
					out.println(Main.playerUUIDArrayList.toString());

				} else if (url.contentEquals("/getPlayerNames")) {
					printHeaders(out);
					out.println(Main.playerNameArrayList.toString());
				}

				else {
					printHeaders(out);

				}

				out.flush();
				out.close();

			} catch (Exception e) {
//				e.printStackTrace();
			}
		}
	}

}
