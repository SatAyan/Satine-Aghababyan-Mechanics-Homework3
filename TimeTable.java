import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import javax.swing.*;

public class TimeTable extends JFrame implements ActionListener {

	private JPanel screen = new JPanel(), tools = new JPanel();
	private JButton tool[];
	private JTextField field[];
	private CourseArray courses;
	private Color CRScolor[] = {Color.RED, Color.GREEN, Color.BLACK};
	private Autoassociator associator;
	
	public TimeTable() {
		super("Dynamic Time Table");
		setSize(1000, 1000);
		setLayout(new FlowLayout());
		
		screen.setPreferredSize(new Dimension(400, 800));
		add(screen);
		
		setTools();
		add(tools);
		
		setVisible(true);
	}
	
	public void setTools() {
		String capField[] = {"Slots:", "Courses:", "Clash File:", "Iters:", "Shift:"};
		field = new JTextField[capField.length];
		
	String capButton[] = {"Load", "Start", "Step", "Print", "Exit", "Continue", "Train"};
		tool = new JButton[capButton.length];
		
		tools.setLayout(new GridLayout(2 * capField.length + capButton.length, 1));
		
		for (int i = 0; i < field.length; i++) {
			tools.add(new JLabel(capField[i]));
			field[i] = new JTextField(5);
			tools.add(field[i]);
		}
		
		for (int i = 0; i < tool.length; i++) {
			tool[i] = new JButton(capButton[i]);
			tool[i].addActionListener(this);
			tools.add(tool[i]);
		}
		
		field[0].setText("17");
		field[1].setText("381");
		field[2].setText("lse-f-91.stu"); //run yor-f-83.stu (19 slots) and rye-s-93.stu (21 slots)
		field[3].setText("1");
	}
	
	public void draw() {
		Graphics g = screen.getGraphics();
		int width = Integer.parseInt(field[0].getText()) * 10;
		for (int courseIndex = 1; courseIndex < courses.length(); courseIndex++) {
			g.setColor(CRScolor[courses.status(courseIndex) > 0 ? 0 : 1]);
			g.drawLine(0, courseIndex, width, courseIndex);
			g.setColor(CRScolor[CRScolor.length - 1]);
			g.drawLine(10 * courses.slot(courseIndex), courseIndex, 10 * courses.slot(courseIndex) + 10, courseIndex);
		}
	}
	
	private int getButtonIndex(JButton source) {
		int result = 0;
		while (source != tool[result]) result++;
		return result;
	}
	
	public void actionPerformed(ActionEvent click) {
		int min, step, clashes;
		try(BufferedWriter writer = new BufferedWriter(new FileWriter("log.txt", true))) {
			LocalDateTime start = LocalDateTime.now();
			writer.write("Algorithm Run at: " + start + "\n");
			writer.write("Slots: " + field[0].getText() + ", Courses: " + field[1].getText() + ", File: " + field[2].getText() + ", Iters: " + field[3].getText() + ", Shifts: " + field[4].getText() + "\n");
			
			switch (getButtonIndex((JButton) click.getSource())) {
			case 0:
				int slots = Integer.parseInt(field[0].getText());
				courses = new CourseArray(Integer.parseInt(field[1].getText()) + 1, slots);
				associator = new Autoassociator(courses);
				courses.readClashes(field[2].getText());
				draw();
				writer.write("\t\t\"Load\"\n");
				break;
			case 1:
				min = Integer.MAX_VALUE;
				step = 0;
				for (int i = 1; i < courses.length(); i++) courses.setSlot(i, 0);
				
				for (int iteration = 1; iteration <= Integer.parseInt(field[3].getText()); iteration++) {
					courses.iterate(Integer.parseInt(field[4].getText()));
					draw();
					clashes = courses.clashesLeft();
					if (clashes < min) {
						min = clashes;
						step = iteration;
					}
				}
				System.out.println("Shift = " + field[4].getText() + "\tMin clashes = " + min + "\tat step " + step);
				setVisible(true);
				writer.write("\t\t\"Start\"\n");
				writer.write("\t\t\t\tMin clashes = " + min + "\tat step " + step + "\n");
				break;
			case 2:
				courses.iterate(Integer.parseInt(field[4].getText()));
				draw();
				writer.write("\t\t\"Step\"\n");
				break;
			case 3:
				System.out.println("Exam\tSlot\tClashes");
				for (int i = 1; i < courses.length(); i++)
					System.out.println(i + "\t" + courses.slot(i) + "\t" + courses.status(i));
				writer.write("\t\t\"Print\"\n");
				break;
			case 4:
				writer.write("\t\t\"Exit\"\n");
				System.exit(0);
				break;
			case 5:
				min = Integer.MAX_VALUE;
				step = 0;
				for (int iteration = 1; iteration <= Integer.parseInt(field[3].getText()); iteration++) {
					courses.iterate(Integer.parseInt(field[4].getText()));
					draw();
					clashes = courses.clashesLeft();
					if (clashes < min) {
						min = clashes;
						step = iteration;
					}
				}
				System.out.println("Shift = " + field[4].getText() + "\tMin clashes = " + min + "\tat step " + step);
				setVisible(true);
				writer.write("\t\t\"Continue\"\n");
				writer.write("\t\t\t\tMin clashes = " + min + "\tat step " + step + "\n");
				break;
			case 6:
				min = Integer.MAX_VALUE;
				step = 0;
				for (int i = 1; i < courses.length(); i++) {
					courses.setSlot(i, 0);
				}
				
				for (int iteration = 1; iteration <= Integer.parseInt(field[3].getText()); iteration++) {
					courses.iterate(Integer.parseInt(field[4].getText()));
					associator.chainUpdate(courses.getTimeSlot(1), 1);
					draw();
					clashes = courses.clashesLeft();
					if (clashes < min) {
						min = clashes;
						step = iteration;
					}
				}
				System.out.println("Shift = " + field[4].getText() + "\tMin clashes = " + min + "\tat step " + step);
				setVisible(true);
				writer.write("\t\t\"Train\"\n");
				writer.write("\t\t\t\tMin clashes = " + min + "\tat step " + step + "\n");
				break;
			}
			LocalDateTime end = LocalDateTime.now();
			Duration duration = Duration.between(start, end);
			writer.write("Algorithm Finished at: " + end + "\n");
			writer.write("Duration of the algorithm = " + duration.toMillis() + " milliseconds\n");
            writer.write("-------------------------------------------------------------------\n");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new TimeTable();
	}
}