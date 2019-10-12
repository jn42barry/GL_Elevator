import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static java.lang.Thread.sleep;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

public class AscenseurGUI {
	public static class ElevatorVisualizationPanel extends JPanel {
		private int y = 250;
		private int floor_height =50;
		private Action action;
		private int door_l=2,door_r=2;
		private int animation=0;
		private boolean do_animate=false;

		ElevatorVisualizationPanel(Action action) {
			this.action=action;
			Timer timer = new Timer(40, e -> {
				if(!do_animate) {
					if (floor_detected()) {
						Action.output_text("[ASCENSEUR] Etage détecté", true);
						action.detected_floor();
						//do_animate = true;
					}
					Point coord = action.moveElevator();
					y = coord.y;
				}else{
					door_animation();
				}
				repaint();
			});
			timer.start();
		}

		private void door_animation() {
			if(animation<=60) {
				if(animation<25) {
					door_r++;
					door_l++;
				}
				if(animation>=25&&animation<=35) {
					try {
						sleep(100);
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
				}
				if(animation>35){
					door_r--;
					door_l--;
				}
				animation++;
			}else{
				animation=0;
				do_animate=false;
			}
		}

		boolean floor_detected(){
			if(action.is_moving()&&(y==0||y==50||y==100||y==150||y==200||y==250)) {
				return true;
			}else{
				return false;
			}
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(220, 300);
		}

		@Override
		protected void paintComponent(Graphics g) {

			super.paintComponent(g);
			Graphics2D elevator = (Graphics2D) g.create();
			for(int i=0;i<5;i++) {
				elevator.setColor(Color.ORANGE);
				elevator.fillRect(0, i* floor_height, 200, floor_height);
				elevator.setColor(Color.YELLOW);
				elevator.fillRect(2, (i* floor_height)+2, 196, floor_height -4);
				elevator.setColor(Color.BLACK);
				elevator.drawString("[" + (5-i) + "]",180,(i* floor_height)+(floor_height /2));
			}
			elevator.setColor(Color.ORANGE);
			elevator.fillRect(0, 5* floor_height, 200, floor_height);
			elevator.setColor(Color.YELLOW);
			elevator.fillRect(2, (5* floor_height)+2, 196, floor_height -4);
			elevator.setColor(Color.BLACK);
			elevator.drawString("[RDC]",163,(5* floor_height)+(floor_height /2));

			elevator.setColor(Color.BLACK);
			elevator.fillRect(0,y, 80, floor_height);

			elevator.setColor(Color.LIGHT_GRAY);

			elevator.fillRect(0,y,(floor_height -10)-door_l, floor_height);
			elevator.fillRect(floor_height -10+door_r,y,(floor_height -10)-door_r, floor_height);
			elevator.dispose();
		}

	}


	private ArrayList<JButton> createFloorButtons(Action action){
		ArrayList<JButton> ButtonsList = new ArrayList<>();
		JButton Floor_button;
		Image button_icon;

		for(int i=5;i>=0;i--) {
			if(i!=0)
				Floor_button = new JButton("Etage "+i);
			else
				Floor_button = new JButton("   RDC  ");
			try {
				button_icon = ImageIO.read(getClass().getResource("etage"+i+"off.png"));
				Floor_button.setIcon(new ImageIcon(button_icon));
				button_icon = ImageIO.read(getClass().getResource("etage"+i+"on.png"));
				Floor_button.setPressedIcon (new ImageIcon(button_icon));
				Floor_button.setMaximumSize(new Dimension(194,60));
				final int finalI = i;
				Floor_button.addActionListener(e -> {
					action.get_Instructions().add_internal(finalI);
					action.print_externals_instructions();
				});
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
			ButtonsList.add(Floor_button);
		}
		Floor_button = new JButton();
		//Floor_button.setMaximumSize(new Dimension(100,100));

		try {
			button_icon = ImageIO.read(getClass().getResource("boutonsAUoff.png"));
			Floor_button.setIcon(new ImageIcon(button_icon));
			button_icon = ImageIO.read(getClass().getResource("boutonsAUon.png"));
			Floor_button.setPressedIcon(new ImageIcon(button_icon));
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		ButtonsList.add(Floor_button);
		return ButtonsList;
	}

	private AscenseurGUI(Action action){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		JFrame f=new JFrame();//creating instance of JFrame

		GridBagLayout grid = new GridBagLayout();
		f.getContentPane().setLayout(grid);

		GridBagConstraints gbc = new GridBagConstraints();
		ElevatorVisualizationPanel EVP = new ElevatorVisualizationPanel(action);


		JPanel Panel_InternalCommand=new JPanel();
		Panel_InternalCommand.setLayout(new BoxLayout(Panel_InternalCommand, BoxLayout.Y_AXIS));
		JLabel Label_InternalCommand=new JLabel("Commandes internes");
		gbc.gridx = 0;
		gbc.gridy = 0;
		//gbc.anchor = GridBagConstraints.WEST;
		Panel_InternalCommand.add(Label_InternalCommand);
		for(JButton Floor_button:createFloorButtons(action)) {
			Panel_InternalCommand.add(Floor_button);
		}
		f.add(Panel_InternalCommand,gbc);

		JPanel Panel_Visualization=new JPanel();
		Panel_Visualization.setLayout(new BoxLayout(Panel_Visualization, BoxLayout.Y_AXIS));
		JLabel Label_Visualization=new JLabel("Visualisation");
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.insets = new Insets(10, 100, 10, 10);
		//gbc.weighty = 1.0;
		//gbc.fill = GridBagConstraints.BOTH;
		Panel_Visualization.add(Label_Visualization);
		Panel_Visualization.add(EVP);
		f.add(Panel_Visualization,gbc);

		JPanel row,column;
		gbc.gridx = 2;
		gbc.gridy = 0;
		//gbc.weighty = 1.0;
		//gbc.fill = GridBagConstraints.FIRST_LINE_START;
		//gbc.gridheight = 5;
		column=new JPanel();
		column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
		column.add(new JLabel("Appel de la cabine"));
		for(int i=5;i>=0;i--){
			row = new JPanel();
			//Declaration et initialisation des boutons qui serviront aux appels dans les étages
			row.add(new JLabel(" Etage "+i+" "));
			if(i!=5) {
				JButton jb = new JButton("/\\");
				final int finalI = i;
				jb.addActionListener(e -> {
					action.get_Instructions().add_external(finalI, Instructions.Sens.HAUT);
					action.print_externals_instructions();
				});
				row.add(jb);
			}
			if(i!=0) {
				JButton jb2 = new JButton("\\/");
				final int finalI = i;
				jb2.addActionListener(e -> {
					action.get_Instructions().add_external(finalI, Instructions.Sens.BAS);
					action.print_externals_instructions();
				});
				row.add(jb2);
			}
			column.add(row);
		}
		f.add(column,gbc);

		JPanel Panel_OperativeControl=new JPanel();
		JPanel column1=new JPanel();
		JPanel column2=new JPanel();
		Panel_OperativeControl.setLayout(new BoxLayout(Panel_OperativeControl, BoxLayout.Y_AXIS));
		JLabel Label_OperativeControl=new JLabel("Contrôle du moteur");
		//gbc.ipady = 100;
		//gbc.ipadx = 200;
		gbc.gridx = 0;
		gbc.gridy = 1;
		Panel_OperativeControl.add(Label_OperativeControl);
		JButton Up=new JButton("Monter");
		Up.addActionListener(e -> action.go_upstair());
		JButton Down=new JButton("Descendre");
		Down.addActionListener(e -> action.go_downstair());
		column1.add(Up);
		column1.add(Down);
		Panel_OperativeControl.add(column1);
		JButton StopNextFloor=new JButton("Arrêter au prochain niveau");
		StopNextFloor.addActionListener(e -> action.next_floor());
		JButton Stop=new JButton("ARRET D'URGENCE");
		Stop.addActionListener(e -> action.stop_all());
		column2.add(StopNextFloor);
		column2.add(Stop);
		Panel_OperativeControl.add(column1);
		Panel_OperativeControl.add(column2);
		f.add(Panel_OperativeControl,gbc);

		JPanel Panel_Output=new JPanel();
		Panel_Output.setLayout(new BoxLayout(Panel_Output, BoxLayout.Y_AXIS));
		JLabel Label_Output=new JLabel("Output :");
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		action.get_text_area().setEditable(false);
		JScrollPane scrollableTextArea = new JScrollPane(action.get_text_area());
		scrollableTextArea.setPreferredSize(new Dimension(500,200));
		scrollableTextArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		Panel_Output.add(Label_Output);
		Panel_Output.add(scrollableTextArea);
		f.add(Panel_Output,gbc);

		f.setTitle("Projet Ascenseur");
		f.setSize(1120,650);
		f.setDefaultCloseOperation(EXIT_ON_CLOSE);
		f.setVisible(true);//making the frame visible
	}
	public static void main(String[] args) {
		Instructions ins = new Instructions();
		Action action = new Action(ins);
		AscenseurGUI AscGUI = new AscenseurGUI(action);
		//action.get_text_area().append("Test");
	}
}


