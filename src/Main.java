
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import parser.*;


public class Main {

	/**
	 * @param args
	 */
	
	public static void main(String[] args) throws IOException {
		
		Queue<ArrayList<String>> kolejka = new LinkedList<ArrayList<String>>();
		
		InputStream in = new FileInputStream("/home/polluma/Dokumenty/TO2/pdf/m.pdf");
		
		
		try
		{
			Parser _parser = Parser.getInstance();
			_parser.init(kolejka);
			ArrayList<String> lista = _parser.process(in);
			for(String s: lista)
			{
				System.out.println(s);
			}
		}
		catch(ParserNotInitialized e)
		{
			System.out.println("dziala");
		}
		
		in.close();
		
		

	}

}
