import java.util.*;
import java.io.*;
public class JavaIndentator implements AbstractIndentator{

	private ArrayList<String> symbols;
	private ArrayList<String> parsedLines;
	private static JavaIndentator INSTANCE;
	private RandomAccessFile file;
	
	private JavaIndentator(){
		//can't use new operator now
		if(this.symbols == null) this.symbols = new ArrayList<String>();
		this.symbols.clear();
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
		if(this.symbols == null) this.symbols = new ArrayList<String>();
		this.symbols.clear();
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
	private void indentCodeUsingSymbolTable() throws Exception{
		if(this.parsedLines == null) this.parsedLines = new ArrayList<String>();
		this.parsedLines.clear();
		LinkedList<String> localStack = new LinkedList<String>();
		String currentLine = "";
		for(String element : this.symbols){
			//trim the symbol
			element = element.trim();
			//check for  { - open bracket , ; - semicolon, /* - comment opening
			if(!element.contains("{") && !element.contains(";") && !element.contains("/*")){
				//check for closing } , */
				if(element.contains("}") || element.contains("*/")) {
					// get the top element of stack , it would be either { or /*
					String topElement = localStack.peekLast();
					// if matched, remove it and close the line
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
				//if any open line found
				currentLine+=element +" \n";
				this.addLine(currentLine, localStack.size());
				currentLine="";
				//add to stack
				if(element.contains("{") || element.contains("/*")) {
					localStack.add(element);
				}
			}
		}
		if(this.parsedLines.isEmpty()) throw new Exception("Please enter a valid code.");
	}
	//add it to global list
	private void addLine(String currentLine, Integer tabCount){
		this.parsedLines.add(this.getIndentationForThisLine(tabCount)+currentLine); 
	}
	
	///**********************FOR GUI************************///////////////
	public String getIndentedCode(String code){
		String toBeReturned = "";
		try{
			prepareSymbolTable(code);
			indentCodeUsingSymbolTable();
			for(String line : parsedLines){
				toBeReturned+=line;
			}
		}catch(Exception e){
			toBeReturned="";
			System.out.println(e.getMessage());
		}
		return toBeReturned;
	}
	
	private void prepareSymbolTable(String code){
		if(this.symbols == null) this.symbols = new ArrayList<String>();
		this.symbols.clear();
		code = code.replaceAll("\n"," ");
		for(String c : code.split(" ")){
				if(!c.trim().isEmpty())
				this.symbols.add(c.trim());
		}
	}
}