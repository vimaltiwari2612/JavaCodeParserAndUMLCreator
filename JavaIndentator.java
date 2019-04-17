import java.util.*;
import java.io.*;
public class JavaIndentator implements AbstractIndentator{

	private ArrayList<String> symbols;
	private ArrayList<String> parsedLines;
	private static JavaIndentator INSTANCE;
	private RandomAccessFile file;
	private JavaIndentator(){

	}

	public static JavaIndentator getInstance(){
		if(INSTANCE == null)
			INSTANCE = new JavaIndentator();
		return INSTANCE;
	}

	private void push(String element){
		symbols.add(element); 
	}

	private String pop(){
		return symbols.get(symbols.size() - 1); 
	}

	public void parse(){
		try{
			prepareSymbolTable();
			indentCodeUsingSymbolTable();
			reWriteTheFile();
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	public void setFileStream(RandomAccessFile file){
		this.file = file;
	}

	private void prepareSymbolTable() throws Exception{
		this.symbols = new ArrayList<String>();
		while(this.file.getFilePointer() < this.file.length()){
			String[] tokens = this.file.readLine().trim().split(" ");
			this.symbols.addAll(Arrays.asList(tokens));
		}
	}

	private void reWriteTheFile() throws Exception{
		this.file.seek(0);
		for(String line : parsedLines){
			this.file.write(line.getBytes());
			this.file.writeBytes(System.getProperty("line.separator"));
		}
		this.file.close();
	}
	
	private String getIndentationForThisLine(Integer count){
		String tabs = "";
		while(count > 0)
		{
			tabs+="\t";
			count--;
		}
		return tabs;
	}

	private void indentCodeUsingSymbolTable(){
		parsedLines = new ArrayList<String>();
		Integer tabCount = 0;
		LinkedList<String> localStack = new LinkedList<String>();
		String currentLine = "";
		for(String element : this.symbols){
			element = element.trim();
			if(!element.contains("{") && !element.contains(";")){
				if(element.contains("}")) {
					String topElement = localStack.peekLast();
					if(topElement!=null && topElement.contains("{")){
						localStack.removeLast();
						currentLine+=element +" \n";
						parsedLines.add(this.getIndentationForThisLine(localStack.size())+currentLine); 
						currentLine="";
						continue;
					}
				}
				currentLine += element + " ";
			}
			else{	
				
				currentLine+=element +" \n";
				parsedLines.add(this.getIndentationForThisLine(localStack.size())+currentLine); 
				currentLine="";
				if(element.contains("{")) {
					localStack.add(element);
				}
			}
		}
	}
}