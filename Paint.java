import javax.swing.*;
import java.awt.*;
import java.awt.event.*;  
import java.util.List;
import java.util.ArrayList;
import java.awt.image.BufferedImage;

abstract class Draw{
	private List<Point> points = new ArrayList<>();
	private Color color;
	
	public Draw(List<Point> points, Color color){
		this.points = points;
		this.color = color;
	}
	
	public int getSize(){
		return points.size();
	}
	
	public Color getColor(){
		return color;
	}
	
	public Point getPoint(int i){
		return points.get(i);
	}
}

// 2 points
class Line extends Draw{
	public Line(List<Point> points, Color color){
		super(points, color);
	}
}

// many points
class Curve extends Draw{
	public Curve(List<Point> points, Color color){
		super(points, color);
	}
}

// 2 points
class Rectangle extends Draw{
	public Rectangle(List<Point> points, Color color){
		super(points, color);
	}
}

// 2 points
class Circle extends Draw{
	public Circle(List<Point> points, Color color){
		super(points, color);
	}
}

// many points
class Eraser extends Draw{
	public Eraser(List<Point> points){
		super(points, Color.WHITE);
	}
}

// many points
class Brush extends Draw{
	public Brush(List<Point> points, Color color){
		super(points, color);
	}
}

class DrawingBoard extends JPanel implements MouseMotionListener{
	private List<Point> points = new ArrayList<>();
	private List<Object> objects = new ArrayList<>();
	private List<Object> undoList = new ArrayList<>();
	private boolean isRectangle = false;
	private boolean isCircle = false;
	private boolean isDraw = true;
	private boolean isEraser = false;
	private boolean isLine = false;
	private boolean isBrush = false;
	private Color color = Color.BLACK;
	
	public void setIsRectangle(boolean trueOrFalse){isRectangle = trueOrFalse;}
	public void setIsCircle(boolean trueOrFalse){isCircle = trueOrFalse;}
	public void setIsDraw(boolean trueOrFalse){isDraw = trueOrFalse;}
	public void setIsEraser(boolean trueOrFalse){isEraser = trueOrFalse;}
	public void setIsLine(boolean trueOrFalse){isLine = trueOrFalse;}
	public void setIsBrush(boolean trueOrFalse){isBrush = trueOrFalse;}
	public void setColor(Color color){this.color = color;}
	public Color getColor(){return color;}
	public boolean isBrush(){return isBrush;}
	
	public void clearBoard(){
		objects = new ArrayList<>();
		undoList = new ArrayList<>();
		repaint();
	}

	public void undo(){
		if(objects.size() > 0){
			undoList.add(objects.get(objects.size() - 1));
			objects.remove(objects.size() - 1);
			repaint();
		}
	}
	public void redo(){
		if(undoList.size() > 0){
			objects.add(undoList.get(undoList.size() - 1));
			undoList.remove(undoList.size() - 1);
			repaint();
		}
	}
	
	public DrawingBoard(){
		setBackground(Color.WHITE);
		addMouseMotionListener(this); 
		addMouseListener(new MouseAdapter(){
			public void mouseReleased(MouseEvent e){
				if(isDraw){
					objects.add(new Curve(points, color));
				} 
				else if(isRectangle || isCircle || isLine){
					List<Point> temp = new ArrayList<>();
					if(isRectangle || isCircle){
						temp.add(new Point(points.get(points.size() - 2).x, points.get(points.size() - 2).y));
						temp.add(new Point(points.get(points.size() - 1).x, points.get(points.size() - 1).y));
						if(isRectangle){
							objects.add(new Rectangle(temp, color));
						}
						else if(isCircle){
							objects.add(new Circle(temp, color));
						}
					} 
					else if(isLine){
						temp.add(new Point(points.get(0)));
						temp.add(new Point(e.getX(), e.getY()));
						objects.add(new Line(temp, color));
					}
				}
				else if(isEraser){
					objects.add(new Eraser(points));
				}
				else if(isBrush){
					objects.add(new Brush(points, color));
				}
				points = new ArrayList<>();
			}
			public void mousePressed(MouseEvent e){
				points.add(new Point(e.getX(), e.getY()));
			}
		});
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		for(Object o : objects){
			if(o instanceof Curve){
				Curve curve = (Curve)o;
				g.setColor(curve.getColor());
				for(int i = 0; i < curve.getSize() - 1; i++)
						g.drawLine(curve.getPoint(i).x, curve.getPoint(i).y, 
						curve.getPoint(i + 1).x, curve.getPoint(i + 1).y);
			}
			else if(o instanceof Circle){
				Circle circle = (Circle)o;
				g.setColor(circle.getColor());
				g.drawOval(circle.getPoint(0).x, circle.getPoint(0).y, circle.getPoint(1).x, circle.getPoint(1).y);
			}
			else if(o instanceof Eraser){
				g.setColor(Color.WHITE);
				Eraser eraser = (Eraser)o;
				for(int i = 0; i < eraser.getSize(); i++)
					g.fillRect(eraser.getPoint(i).x, eraser.getPoint(i).y, 10, 10);
			}
			else if(o instanceof Rectangle){
				Rectangle rectangle = (Rectangle)o;
				g.setColor(rectangle.getColor());
				g.drawRect(rectangle.getPoint(0).x, rectangle.getPoint(0).y, rectangle.getPoint(1).x, rectangle.getPoint(1).y);
			}
			else if(o instanceof Line){
				Line line = (Line)o;
				g.setColor(line.getColor());
				g.drawLine(line.getPoint(0).x, line.getPoint(0).y, line.getPoint(1).x, line.getPoint(1).y);
			}
			else if(o instanceof Brush){
				Brush brush = (Brush)o;
				g.setColor(brush.getColor());
				for(int i = 0; i < brush.getSize(); i++)
					g.fillOval(brush.getPoint(i).x, brush.getPoint(i).y, 10, 10);
			}
		}
		
		g.setColor(color);
	
		if(isRectangle && points.size() > 0){
			g.drawRect(points.get(points.size() - 2).x, points.get(points.size() - 2).y, 
							points.get(points.size() - 1).x, points.get(points.size() - 1).y);
		}
		else if(isCircle && points.size() > 0){
			g.drawOval(points.get(points.size() - 2).x, points.get(points.size() - 2).y, 
								points.get(points.size() - 1).x, points.get(points.size() - 1).y);
		}
		else if(isLine && points.size() > 0){
			g.drawLine(points.get(0).x, points.get(0).y, points.get(points.size() - 1).x, points.get(points.size() - 1).y);
		}	
	}

	public void mouseDragged(MouseEvent e){  
		Graphics g = getGraphics();
		g.setColor(color);
		
		int xr = 0, yr = 0, widthr = 0, heightr = 0;
		
		int xx = e.getX();
		int yy = e.getY();
		
		int x = points.get(0).x;
		int y = points.get(0).y;
		int dx = xx-x;
		int dy = yy-y;
	
	    if(isRectangle || isCircle){
			if(dx >= 0 && dy >= 0){
				xr = x;
				yr = y;
				widthr = dx;
				heightr = dy;
			} else if(dx < 0 && dy < 0){
				xr = xx;
				yr = yy;
				widthr = -dx;
				heightr = -dy;
			} else if(dx >= 0 && dy < 0){
				xr = x;
				yr = yy;
				widthr = dx;
				heightr = -dy;
			} else if(dx < 0 && dy >= 0){
				xr = xx;
				yr = y;
				widthr = -dx;
				heightr = dy;
			}
			points.add(new Point(xr, yr));
			points.add(new Point(widthr, heightr));
			repaint();
		} 
		else if(isDraw){
			g.drawLine(xx, yy, points.get(points.size() - 1).x, points.get(points.size() - 1).y);
			points.add(new Point(xx, yy));
		}
		else if(isLine){
			points.add(new Point(e.getX(), e.getY()));
			repaint();
		}
		else if(isEraser){
			g.setColor(Color.WHITE);
			g.fillRect(e.getX(), e.getY(), 10, 10);
			points.add(new Point(e.getX(), e.getY()));
		} 
		else if(isBrush){
			g.setColor(color);
			g.fillOval(e.getX(), e.getY(), 10, 10);
			points.add(new Point(e.getX(), e.getY()));
		}
	}
	
	public void mouseMoved(MouseEvent e) {}  
	
}

class GUI extends JFrame implements ActionListener{
	private JPanel[] panel;
	private JButton[] button;
	private DrawingBoard board;
	
	public GUI(String title){
		super(title);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = screenDimension.width;
		int screenHeight = screenDimension.height;
		setExtendedState(JFrame.MAXIMIZED_BOTH); 
		setSize(screenWidth, screenHeight);
		
		board = new DrawingBoard();
		
		panel = new JPanel[2];
		panel[0] = new JPanel();
		panel[1] = new JPanel();
		panel[0].setLayout(null);
		panel[1].setLayout(null);
		panel[0].setBounds(0, 0, screenWidth, 20);
		board.setBounds(0,20, screenWidth, screenHeight);
		panel[1].add(panel[0]);
		panel[1].add(board);
		
		button = new JButton[10];
		button[0] = new JButton("Draw");
		button[1] = new JButton("Rectangle");
		button[2] = new JButton("Circle");
		button[3] = new JButton("Eraser");
		button[4] = new JButton("Clear");
		button[5] = new JButton("Line");
		button[6] = new JButton("Choose Color"); 
		button[7] = new JButton("Undo");
		button[8] = new JButton("Redo");
		button[9] = new JButton("Brush"); 
		button[0].addActionListener(this);
		button[1].addActionListener(this);
		button[2].addActionListener(this);
		button[3].addActionListener(this);
		button[4].addActionListener(this);
		button[5].addActionListener(this);
		button[6].addActionListener(this);
		button[7].addActionListener(this);
		button[8].addActionListener(this);
		button[9].addActionListener(this);
		setFocusToButton(0);

		int dist = Math.round(screenWidth / button.length) + 1;

		button[0].setBounds(0, 0, dist, 20);      // Draw pos: 0
		button[1].setBounds(2*dist, 0, dist, 20); // Rectangle pos: 2 
		button[2].setBounds(3*dist, 0, dist, 20); // Circle pos: 3
		button[3].setBounds(5*dist, 0, dist, 20); // Eraser pos: 5
		button[4].setBounds(9*dist, 0, dist, 20); // Clear pos: 9
		button[5].setBounds(4*dist, 0, dist, 20); // Line pos: 4
		button[6].setBounds(6*dist, 0, dist, 20); // Change color pos: 6
		button[7].setBounds(7*dist, 0, dist, 20); // Undo pos: 7
		button[8].setBounds(8*dist, 0, dist, 20); // Redo pos: 8
		button[9].setBounds(dist, 0, dist, 20);   // Brush pos: 1
		
		panel[0].add(button[0]);
		panel[0].add(button[1]);
		panel[0].add(button[2]);
		panel[0].add(button[3]);
		panel[0].add(button[4]);
		panel[0].add(button[5]);
		panel[0].add(button[6]);
		panel[0].add(button[7]);
		panel[0].add(button[8]);
		panel[0].add(button[9]);
		
		add(panel[1]);	
		
		show();
	}
	
	private void setFocusToButton(int i){
		Color color1 = Color.GREEN.brighter();
		Color color2 = Color.YELLOW.brighter();
		for(int j = 0; j < button.length; j++){
			if(j == i) button[j].setBackground(color1);
			else button[j].setBackground(color2);
			button[j].setForeground(Color.RED.darker());
		}
	}
	
	private void setCursorFor(String eraserOrBrush){
		BufferedImage bufferedImage = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bufferedImage.createGraphics();
		if(eraserOrBrush.equals("Brush")){
			g.setColor(board.getColor());
			g.fillOval(0, 0, 10, 10);
			
		} 
		else if(eraserOrBrush.equals("Eraser")){
			g.setColor(Color.RED);
			g.fillRect(0, 0, 10, 10);
		}
		g.dispose();
		Cursor c = Toolkit.getDefaultToolkit().createCustomCursor(bufferedImage, new Point(1, 1), "cursor"); 
		board.setCursor(c);
	}
	
	public void actionPerformed(ActionEvent e){
		String command = e.getActionCommand();
		if(command.equals("Draw")){
			setFocusToButton(0);
			board.setCursor(Cursor.getDefaultCursor());
			board.setIsDraw(true);
			board.setIsRectangle(false);
			board.setIsCircle(false);
			board.setIsEraser(false);
			board.setIsLine(false);
			board.setIsBrush(false);
		}
		else if(command.equals("Rectangle")){
			setFocusToButton(1);
			board.setCursor(Cursor.getDefaultCursor());
			board.setIsRectangle(true);
			board.setIsCircle(false);
			board.setIsDraw(false);
			board.setIsEraser(false);
			board.setIsLine(false);
			board.setIsBrush(false);
		}
		else if(command.equals("Circle")){
			setFocusToButton(2);
			board.setCursor(Cursor.getDefaultCursor());
			board.setIsCircle(true);
			board.setIsRectangle(false);
			board.setIsDraw(false);
			board.setIsEraser(false);
			board.setIsLine(false);
			board.setIsBrush(false);
		}
		else if(command.equals("Eraser")){
			setFocusToButton(3);
			setCursorFor("Eraser");
			board.setIsEraser(true);
			board.setIsDraw(false);
			board.setIsRectangle(false);
			board.setIsCircle(false);
			board.setIsLine(false);
			board.setIsBrush(false);
		}
		else if(command.equals("Clear")){
			int answer = JOptionPane.showConfirmDialog(this, "Are you sure you want to clear the board?");  
			if(answer == JOptionPane.YES_OPTION){  
				board.clearBoard();
			}  
		}
		else if(command.equals("Line")){
			setFocusToButton(5);
			board.setCursor(Cursor.getDefaultCursor());
			board.setIsLine(true);
			board.setIsDraw(false);
			board.setIsRectangle(false);
			board.setIsCircle(false);
			board.setIsEraser(false);
			board.setIsBrush(false);
		}
		else if(command.equals("Choose Color")){
			Color newColor = JColorChooser.showDialog(this, "Choose Color", Color.BLACK);
			board.setColor(newColor);
			if(board.isBrush()){
				setCursorFor("Brush");
			}
		}
		else if(command.equals("Undo")){
			board.undo();
		}
		else if(command.equals("Redo")){
			board.redo();
		}
		else if(command.equals("Brush")){
			setFocusToButton(9);
			setCursorFor("Brush");
			board.setIsBrush(true);
			board.setIsLine(false);
			board.setIsDraw(false);
			board.setIsRectangle(false);
			board.setIsCircle(false);
			board.setIsEraser(false);
		}
	}
}

public class Paint{
	public static void main(String []str){
		new GUI("Paint");
	}
}