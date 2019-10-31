import java.util.*;
import java.io.*;
public class JavaUMLCreator implements AbstractUML{

	private static JavaUMLCreator INSTANCE = null;//singleton pattern
	private List<String> umlDataSet = new ArrayList<String>(); //final list to create string
	private RandomAccessFile file; //for file input
	private List<String> accessModifiers;
	private List<String> notAllowedKeyWords; // which niether represent a method, not variable
	private List<String> toBeReplacedWords; // like constant, static, final
	
	private JavaUMLCreator(){
		//nothing to do
		//init all
		umlDataSet = new ArrayList<String>();
		accessModifiers = Arrays.asList(new String[]{"private","public","protected"});
		notAllowedKeyWords = Arrays.asList(new String[]{"import","package"});
		toBeReplacedWords = Arrays.asList(new String[]{"static","final","constant"});
	}
	
	//method to form UML data
	//it calles the indentor first to make the class indentation proper 
	//after indentation, processing becoms easy
	public String createUMLData(String code){
		String response = null;
		try{
			//calls indentor
			JavaIndentator ji = JavaIndentator.getInstance();
			if(code == null){
				ji.setFileStream(this.file);
			}
			else{
				code = code.trim();
			}
			String indentedCode = ji.parse(code);
			//use the indented code to create data
			response = getUMLData(indentedCode);
		}
		catch(Exception e){
			System.out.println("Error while Creating UML === "+e.getMessage());
			e.printStackTrace();
		}
		return response;
	}
	
	//for file input
	public void setFileStream(RandomAccessFile file){
		this.file = file;
	}
		
	//process the indented code
	private String getUMLData(String code) throws Exception{
		String response = "";
		String type = getType(code);
		if(type == null) throw new Exception("Not a valid Class/Interface code.");
		String entityName = getEntityName(code,type);
		List<List<String>> processedData = getVariables(code);
		List<String> variables = processedData.get(0);
		List<String> cachedData = processedData.get(1);
		List<String> functions = getFunctions(code,cachedData);
		response = entityName + "\n";
		response += "Variable" + "\n";
		for(String var : variables){
			response += var +"\n";
		}
		response += "Method" + "\n";
		for(String var : functions){
			response += var +"\n";
		}
		return response;
	}
	
	//method to get the type of entity
	//class, interface etc
	private String getType(String code){
		if(code.contains("class")){
			return "class";
		}
		else if(code.contains("interface")){
			return "interface";
		}
		else{
			return null;
		}
	}
	
	//method to get the name of type found
	private String getEntityName(String code, String type){
		Boolean found = false;
		
		String toBeRetuned = "";
		for(String s : code.split(" ")){

			if(s.contains(type)){
				found = true;
				continue;
			}
			if(found){
				toBeRetuned = s;
				break;
			}

		}
		if(toBeRetuned.contains("{"))
			toBeRetuned = toBeRetuned.replaceAll("{","");
		return toBeRetuned;
	}
	
	//method to find variables in code
	private List<List<String>> getVariables(String code){
		List<String> variables = new ArrayList<String>();
		List<String> cachedData = new ArrayList<String>();
		List<List<String>> toBeReturned = new ArrayList<List<String>>();
		String lines[] = code.split("\\r?\\n");
		Boolean curlyBracketOn = false;
		Boolean roundBracketOn = false;
		LinkedList<String> stack = new LinkedList<String>();
		for(String line : lines){

			String response = "";
			line = line.replaceAll("\t","");
			
			if(isValidLine(line)){
				//cache it to be used in functions processing
				cachedData.add(line);
				//process the variable 
				if(line.contains("=")){
					line = line.split("=")[0];
				}
				else if(line.contains(";")){
					line = line.split(";")[0];
				}
			
				// not a variable, it's a method
				if(line.contains("{") && !line.contains("class")) {
					stack.add("{");
					//continue;
				}
				if(line.contains("}")){
					String topElement = stack.peekLast();
					if(topElement == "{") {
						stack.removeLast();
					}
					//continue;
				}
				
				if(line.contains("(")){
					//check for >
					int index = line.indexOf(">");
					int index2 =  line.indexOf("(");
					if(index != -1 && index <= index2){
						//it's a valid line
						//its a collection type variable
					}						
					else{
						stack.add("(");
						//continue;
					}
				}
				
				if(line.contains(")")){
					String topElement = stack.peekLast();
					if(topElement == "(") {
						stack.removeLast();
					}
					//continue;
				}
				if(!stack.isEmpty()) {
					cachedData.remove(cachedData.size() - 1);
					continue;
				}
				
				//futher filtering
				if(line.contains("class") || line.contains("{") || line.contains("}")) {
					cachedData.remove(cachedData.size() - 1);
					continue;
				}
				//could be variable
				line = filterLine(line);
				//System.out.println(line);
				line = this.getLineInUMLFormat(line);
				//System.out.println(line);
				variables.add(line);
			}
		}
		toBeReturned.add(variables);
		toBeReturned.add(cachedData);
		return toBeReturned;
	}
	
	//get formated accessModifier, name and type 
	private String getLineInUMLFormat(String line){
		//split the line
		String[] chunks = line.split(" ");
		String accessModifier = " ";
		String name = "";
		String type = "";
		
		for(String chunk : chunks){
			chunk = chunk.trim();
			//check for accessModifiers
			if(accessModifiers.contains(chunk)){
				if(chunk.contains("private")){
					accessModifier = "-";
				}
				else if(chunk.contains("public") || chunk.contains("protected")){
					accessModifier = "+";
				}
				//move to next
				continue;
			}
			else if(type == ""){
				type = chunk;
				continue;
			}
			else if(name == ""){
				name = chunk;
				continue;
			}
		}
		line = accessModifier + name +" : "+type;
		
		return line;
	}
	
	
	//method to find the line to be processed of not
	private boolean isValidLine(String line){
		if(line == null || line == ""){
			return false;
		}
		for(String word : this.notAllowedKeyWords){
			if(line.contains(word)) return false;
		}
		return true;
	}
	
	//method to find fucntions in code
	private List<String> getFunctions(String code,List<String> cachedData){
		List<String> functions = new ArrayList<String>();
		String lines[] = code.split("\\r?\\n");
		for(String line : lines){

			String response = "";
			line = line.replaceAll("\t","");
			
			if(isValidLine(line)){
				//check for variables
				if(line.contains("class") || cachedData.contains(line)) continue;
				//System.out.println(line);
				//could be a line
				if(line.contains("{") && line.contains("(")){
					line = filterLine(line);
					line = line.replace("{","");
					//System.out.println(line);
					//find (
					int indexofOpenBracket = line.indexOf("(");
					if(indexofOpenBracket == -1) continue;
					//split 
					String firstPart = line.substring(0,indexofOpenBracket);
					String secondPart = line.substring(indexofOpenBracket-1);
					line = this.getLineInUMLFormat(firstPart);
					//adjust in uml format
					line = line.replace(":",secondPart+" : ");
					line = line.replace("  ","");
					functions.add(line);
				}
			}
		}
		return functions;
	}
	
	//Only one instance
	public static JavaUMLCreator getInstance(){
		if(INSTANCE == null)
			INSTANCE = new JavaUMLCreator();
		return INSTANCE;
	}
	
	//filter line by repalcing unwanted things with blank
	private String filterLine(String line){
		
		for(String word : this.toBeReplacedWords){
			if(line.contains(word)){
				line = line.replace(word,"");
			}
		}
		if(line.contains("  "))
			line = line.replaceAll("  "," ");
		return line;
	}
}