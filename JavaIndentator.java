import java.util.*;
import java.io.*;
public class JavaIndentator implements AbstractIndentator{

	private ArrayList<String> symbols; //symbol table
	private ArrayList<String> parsedLines; //indented lines
	private LinkedList<String> localStack; //keep track of tokens while parsing
	private ArrayList<String> literals; // literals for parsing
	private static JavaIndentator INSTANCE; //singleton pattern
	private RandomAccessFile file; //for file input
	
	private JavaIndentator(){
		//can't use new operator now
		if(this.symbols == null) this.symbols = new ArrayList<String>();
		this.symbols.clear();
		if(this.literals == null)
			literals = new ArrayList<String>(Arrays.asList(this.populateLiterals())); 
	}
	
	//Only one instance
	public static JavaIndentator getInstance(){
		if(INSTANCE == null)
			INSTANCE = new JavaIndentator();
		return INSTANCE;
	}
	
	//literal table
	private String[] populateLiterals(){
		String[] literals = new String[]{"{","}",";","/*","*/","(",")"};
		return literals;
	}
	
	public void parse(){
		try{
			prepareSymbolTable(null);
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
	private void prepareSymbolTable(String code) throws Exception{
		if(this.symbols == null) this.symbols = new ArrayList<String>();
		this.symbols.clear();
		if(code == null){
			while(this.file.getFilePointer() < this.file.length()){
				String[] tokens = this.file.readLine().trim().split(" ");
				this.addToSymbolTable(tokens);
			}
		}
		else{
			code = code.replaceAll("\n"," ");
			for(String c : code.split(" ")){
				if(!c.trim().isEmpty()){
					this.addToSymbolTable(new String[]{c});
				}
			}
		}
	}
	
	//get the filtered tokens and populate the Symbol table
	private void addToSymbolTable(String[] tokens){
		if(tokens!=null && tokens.length > 0){
			for(String token : tokens)
				{
					ArrayList<String> filteredTokens = this.filterToken(token);
					if(filteredTokens != null)
						this.symbols.addAll(filteredTokens);
				}
		}
	}
	
	
	/*
	*	filtering tokens
	*    For Example :  main(){ is consists of 4 token  main , (,),{
	*/
	private ArrayList<String> filterToken(String token){
		ArrayList<String> filtered = null;
		String[] splitted = token.trim().split("");
		String temp = "";
		for(String str : splitted){
			if(this.literals.contains(str)){
				if(filtered == null)
					filtered = new ArrayList<String>();
				if(!temp.trim().equals(""))
					filtered.add(temp);
				filtered.add(str);
				temp = "";
			}
			else{
				temp+=str;
			}
		}
		if(filtered == null) filtered = new ArrayList<String>();
		if(!temp.trim().equals("")) filtered.add(temp);
		return filtered;
	}
	
	//for rewriting the given file with indented code
	private void reWriteTheFile() throws Exception{
		this.file.seek(0);
		for(String line : parsedLines){
			this.file.write(line.getBytes());
			this.file.writeBytes(System.getProperty("line.separator"));
		}
		this.file.close();
	}
	
	//tab indentations 
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
		if(this.localStack == null) this.localStack = new LinkedList<String>();
		this.localStack.clear();
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
		if(this.parsedLines.isEmpty() || !this.localStack.isEmpty()) throw new Exception("Please enter a valid JAVA code.");
	}
	//add it to global list
	private void addLine(String currentLine, Integer tabCount){
		this.parsedLines.add(this.getIndentationForThisLine(tabCount)+currentLine); 
	}
	
	///**********************FOR GUI************************///////////////
	public String getIndentedCode(String code) throws Exception{
		String toBeReturned = "";
		prepareSymbolTable(code);
		indentCodeUsingSymbolTable();
		for(String line : parsedLines){
			toBeReturned+=line;
		}
		return toBeReturned;
	}
}