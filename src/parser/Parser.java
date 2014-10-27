package parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Queue;

import org.apache.poi.util.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.apache.tika.*;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

public class Parser {

	private static volatile Parser _parser = null;
	private Queue<ArrayList<String>> _queue;
	private ArrayList<String> temp = new ArrayList<String>();
	//get instance for Singleton
	public static Parser getInstance()
	{
		if(Parser._parser == null)
		{
			Parser._parser = new Parser();
			return Parser._parser;
		}
		return Parser._parser;
	}
	
	//initialize Singleton with Queue for text blocks
	public void init(Queue<ArrayList<String>> q)
	{
		if(this._queue == null)
		{
			this._queue = q;
			temp.add("");
		}
		else
		{
			//if Singleton is initialized throw exception
			throw new ParserAlreadyInitialized();
		}
	}
	
	public ArrayList<String> process(InputStream in) throws IOException
	{
		if(this._queue == null)
		{
			throw new ParserNotInitialized();
		}
		byte[] byteArray = null;
		//to bypass problem with loading a pdf doc from stream which has been used before by Tika
		byteArray = IOUtils.toByteArray(in); 
	    InputStream input = new ByteArrayInputStream(byteArray);
	    InputStream check = new ByteArrayInputStream(byteArray);
	    ArrayList<String> URLsList = new ArrayList<String>();
		ArrayList<String> textList = new ArrayList<String>();
		Tika tike = new Tika();
		String type = null;
		try {
			type = tike.detect(check);
	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(type.compareTo("text/html") == 0)
		{
			try {
				Document doc = Jsoup.parse(in,null,"");
				Elements links = doc.select("a[href]");
				for(Element link : links)
				{
					URLsList.add(link.attr("abs:href"));
					textList.add(link.text());
				}
				textList.add(doc.body().text());
				URLsList.removeAll(temp);
				this._queue.add(textList);
				return URLsList;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(type.compareTo("application/pdf") == 0)
		{
			PdfReader reader = new PdfReader(input);
			String rawTextFromPdf = "";
	        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
	        	rawTextFromPdf += PdfTextExtractor.getTextFromPage(reader, i);
	        }
	        
	        System.out.println(rawTextFromPdf);
			/*
			 * To do:
			 * extract urls from rawText
			 */
			return URLsList;
		}
		if(type.compareTo("text/plain") == 0)
		{
			System.out.println("txt");
			return null;
		}
		return null;
		
	}
	
	private Parser(){};
	
	
}
