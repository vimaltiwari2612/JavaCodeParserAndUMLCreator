import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

public class IndentatorScreen extends JFrame{
	private JPanel panel1,panel2 ;
	private JLabel label ;
	private JButton button;
	private JScrollPane jScrollPane1,jScrollPane2;
    private JTextArea textArea1,textArea2;	

    public IndentatorScreen() {
		this.setTitle("Code Indentor");
		button = new JButton("Indent");
		Dimension d=Toolkit.getDefaultToolkit().getScreenSize();
		int x = 30;
		int y = 35;
		textArea1 = new JTextArea(x,y);
		textArea2 = new JTextArea(x,y);
		Font font = new Font("Times New Roman", Font.BOLD, 15);
        textArea1.setFont(font);
        textArea1.setForeground(Color.RED);
		textArea2.setFont(font);
        textArea2.setForeground(Color.BLUE);
		jScrollPane1 = new JScrollPane(textArea1);
		jScrollPane2 = new JScrollPane(textArea2);
		panel1 = new JPanel(); 
		panel2 = new JPanel();
		panel1.setBackground(Color.BLACK);
		panel2.setBackground(Color.BLACK);
		panel1.add(jScrollPane1);
		panel2.add(jScrollPane2);
		this.setLayout(new FlowLayout());
        this.add(panel1);
		this.add(button);
		this.add(panel2);
        this.setSize(1000, 600);
		this.setResizable(false);
		this.setLocation(d.width/2-this.getWidth()/2,d.height/2-this.getHeight()/2);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
	
		button.addActionListener(new ActionListener()
		{
		  public void actionPerformed(ActionEvent e)
		  {
			try{
				textArea2.setText(JavaIndentator.getInstance().getIndentedCode(textArea1.getText()));
			}catch(Exception ex){
				textArea2.setText("");
				JOptionPane.showMessageDialog(null,ex.getMessage());
			}
		  }
		});
    }
	
	public static void main(String...arg){
		new IndentatorScreen();
	}
}