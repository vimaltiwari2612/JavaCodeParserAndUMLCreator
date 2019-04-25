import java.io.*;
public class IndentationRunner{

	public static void main(String arg[]) throws Exception{
		RandomAccessFile file = new RandomAccessFile(arg[0], "rw");  
        	file.seek(0);
		JavaIndentator javaIndentator = JavaIndentator.getInstance();
		javaIndentator.setFileStream(file);
		javaIndentator.parse(null);   
	}
}