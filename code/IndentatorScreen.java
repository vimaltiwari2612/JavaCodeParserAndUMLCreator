import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

public class IndentatorScreen extends JFrame{
	private JPanel panel1,panel2,codePanel,symbolTablePanel,nonCodePanel,errorPanel;
	private JLabel label ;
	private JButton button;
	private JScrollPane jScrollPane1,jScrollPane2, jScrollPane3,jScrollPane4;
    	private JTextArea textArea1,textArea2, symbolTableTextArea,errorArea;	
	private TitledBorder border1, border2,border3,border4;

    	public IndentatorScreen() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		this.setTitle("JAVA Code Parser");
		button = new JButton("Parse");
		Dimension d=Toolkit.getDefaultToolkit().getScreenSize();
		int x = 30;
		int y = 35;
		textArea1 = new JTextArea(x,y);
		textArea2 = new JTextArea(x,y);
		symbolTableTextArea = new JTextArea(x - 16,y+5);
		symbolTableTextArea.setEditable(false);
		errorArea = new JTextArea(x - 16,y+2);
		errorArea.setEditable(false);
		Font font = new Font("Times New Roman", Font.BOLD, 15);
        	textArea1.setFont(font);
        	textArea1.setForeground(Color.RED);
		textArea2.setFont(font);
        	textArea2.setForeground(Color.BLUE);
		textArea2.setEditable(false);
		symbolTableTextArea.setFont(font);
		symbolTableTextArea.setForeground(Color.BLACK);
		errorArea.setFont(font);
		errorArea.setForeground(Color.BLACK);
		jScrollPane1 = new JScrollPane(textArea1);
		jScrollPane2 = new JScrollPane(textArea2);
		jScrollPane3 = new JScrollPane(symbolTableTextArea);
		jScrollPane4 = new JScrollPane(errorArea);
		border1 = new TitledBorder("UnIndented Code");
		border1.setTitleJustification(TitledBorder.CENTER);
		border1.setTitlePosition(TitledBorder.TOP);
		panel1 = new JPanel();
		panel1.setBorder(border1);
		border2 = new TitledBorder("Indented Code");
		border2.setTitleJustification(TitledBorder.CENTER);
		border2.setTitlePosition(TitledBorder.TOP);		
		panel2 = new JPanel();
		panel2.setBorder(border2);
		panel1.add(jScrollPane1);
		panel2.add(jScrollPane2);
		codePanel = new JPanel();
		codePanel.setLayout(new FlowLayout());
        	codePanel.add(panel1);
		codePanel.add(button);
		codePanel.add(panel2);
		nonCodePanel = new JPanel();
		nonCodePanel.setLayout(new FlowLayout());
		symbolTablePanel = new JPanel();
		border3 = new TitledBorder("Symbol Table");
		border3.setTitleJustification(TitledBorder.CENTER);
		border3.setTitlePosition(TitledBorder.TOP);
		symbolTablePanel.setBorder(border3);
		symbolTablePanel.add(jScrollPane3);
		errorPanel = new JPanel();
		errorPanel.add(jScrollPane4);
		border4 = new TitledBorder("Parsing Errors");
		border4.setTitleJustification(TitledBorder.CENTER);
		border4.setTitlePosition(TitledBorder.TOP);
		errorPanel.setBorder(border4);
		nonCodePanel.add(symbolTablePanel);
		nonCodePanel.add(errorPanel);
		this.setLayout(new FlowLayout());
		this.add(codePanel);
		this.add(nonCodePanel);
		this.nonCodePanel.setLocation(x,y);
       		this.setSize(1000, 950);
		this.setResizable(false);
		this.setLocation(d.width/2-this.getWidth()/2,d.height/2-this.getHeight()/2);
        	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        	this.setVisible(true);
	
		button.addActionListener(new ActionListener()
		{
		  public void actionPerformed(ActionEvent e)
		  {
			try{
				JavaIndentator ji = JavaIndentator.getInstance();
				String code = textArea1.getText();
				if(code.trim().equals("")) throw new Exception("Enter some Input!!");
				textArea2.setText(ji.getIndentedCode(code.trim()));
				symbolTableTextArea.setText(ji.getSymbolTable());
				if(!ji.isValidCode()){
					JOptionPane.showMessageDialog(null,"This Code won't compile. It's not a valid JAVA code.");
					errorArea.setText(ji.getParsingErrors());
				}
				else{
					errorArea.setText("No Errors.");
				}
			}catch(Exception ex){
				//textArea2.setText("");
				//symbolTableTextArea.setText("");
				//errorArea.setText("");
				JOptionPane.showMessageDialog(null,ex.getMessage());
			}
		  }
		});
    }
	
	public static void main(String...arg){
		new IndentatorScreen();
	}
}