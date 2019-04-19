import java.util.*;
import java.io.*;
public class JavaIndentator implements AbstractIndentator{

	private ArrayList<String> symbols;
	private ArrayList<String> parsedLines;
	private static JavaIndentator INSTANCE;
	private RandomAccessFile file;
	
	private JavaIndentator(){
		//can't use new operator now
	}

	public static JavaIndentator getInstance(){
		if(INSTANCE == null)
			INSTANCE = new JavaIndentator();
		return INSTANCE;
	}
	
	public void parse(){
		try{
			prepareSymbolTable();
			indentCodeUsingSymbolTable();
			reWriteTheFile();
			System.out.println("Parsing Completed!");
		}catch(Exception e){
			System.out.println("Error while parsing === "+e.getMessage());
		}finally{
			try{
				//free the resources
				this.file.close();
			}catch(IOException io){
				System.out.println("Error while closing file === "+io.getMessage());
			}
		}
	}
	
	public void setFileStream(RandomAccessFile file){
		this.file = file;
	}
	//symbol table formation
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
	
	//main parsing logic
	//lexical anaylsis
	private void indentCodeUsingSymbolTable(){
		parsedLines = new ArrayList<String>();
		LinkedList<String> localStack = new LinkedList<String>();
		String currentLine = "";
		for(String element : this.symbols){
			element = element.trim();
			if(!element.contains("{") && !element.contains(";") && !element.contains("/*")){
				if(element.contains("}") || element.contains("*/")) {
					String topElement = localStack.peekLast();
					if(topElement!=null && (topElement.contains("{") || topElement.contains("/*"))){
						localStack.removeLast();
						currentLine+=element +" \n";
						this.addLine(currentLine, localStack.size());
						currentLine="";
						continue;
					}
				}
				currentLine += element + " ";
			}
			else{	
				
				currentLine+=element +" \n";
				this.addLine(currentLine, localStack.size()); 
				currentLine="";
				if(element.contains("{") || element.contains("/*")) {
					localStack.add(element);
				}
			}
		}
	}
	//add it to global list
	private void addLine(String currentLine, Integer tabCount){
		this.parsedLines.add(this.getIndentationForThisLine(tabCount)+currentLine); 
	}
}
