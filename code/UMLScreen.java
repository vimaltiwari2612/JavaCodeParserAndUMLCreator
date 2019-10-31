import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.color.ColorSpace;
import java.io.*;
import javax.imageio.*;
import java.text.*;
import java.util.*;


public class UMLScreen extends JPanel{

	String output = null;
	JButton save = null;
	public UMLScreen(String output) {
		this.output = output;
		save = new JButton("Save");
		save.addActionListener(new ActionListener()
		{
		  public void actionPerformed(ActionEvent e)
		  {
			try{
				saveImage();
				JOptionPane.showMessageDialog(null,"Saved!");
			}catch(Exception ex){
				JOptionPane.showMessageDialog(null,ex.getMessage());
			}
		  }
		});
		this.add(save);
	}
	
	public static BufferedImage getScreenShot(Component component) throws AWTException {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		Robot robot = new Robot(gd);
		Rectangle bounds = new Rectangle(component.getLocationOnScreen(), component.getSize());
		return robot.createScreenCapture(bounds);
	}
	
	private void saveImage() throws Exception{
		//create image
		BufferedImage imagebuf = UMLScreen.getScreenShot(this);
		//save file
		File dir = new File(System.getProperty("user.dir"));
		Date date = new Date() ;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss") ;
		String name = "uml_"+dateFormat.format(date)+".jpeg";
		File f = new File(dir.getParent()+"/uml/"+name);
		ImageIO.write(imagebuf,"jpeg", f);
	}
	
    public void paint(Graphics g) {
		Font f = new Font("TimesRoman", Font.PLAIN, 15);
		g.setFont(f);
		FontMetrics fm = getFontMetrics(f);
		String output[] = this.output.split("\n");
		
		int x = 50;
		int y = 50;
		
		int h = fm.getHeight() * output.length;
		int w = x + h;
		g.drawRect(x, y, x+w, y+h);
		
		Boolean classNameDone = false;
		Boolean variableDone = false;
		Boolean methodDone = false;
		y=y+7;
		for(String s :output){
			
			int width = g.getFontMetrics().stringWidth(s);
			int height = g.getFontMetrics().getHeight();
			if(!classNameDone){
				g.drawString(s,(x+x+w)/2,y+10);
				y += height - 2;
				g.drawLine(x, y, x+x+w, y);
				classNameDone = true;
			}
			else if(!variableDone){
				if(s.contains("Variable")) continue;
				else if(s.contains("Method")){
					variableDone = true;
					y += height - 2;
					g.drawLine(x, y, x+x+w, y);
					y=y+20;
					continue;
				}
				g.drawString(s,x+10,y+10);
			}
			else if(!methodDone){
				g.drawString(s,x+10,y+10);
			}
			y=y+20;
		}
	}
}