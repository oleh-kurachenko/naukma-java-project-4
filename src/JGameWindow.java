import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * @author Oleh Kurachenko
 *
 */
@SuppressWarnings("serial")
final class JGameWindow extends JFrame {

	private static int lining;
	private static final int TABLE_SIZE = 600;
	private static final int LINE_WIDTH = 2;

	private static int gap;

	private JPanel gamePanel;
	private JPanel[][] stonesAll;
	private PlayerPanel player1, player2;

	private List<JPanel> lines = new LinkedList<JPanel>(), stones = new LinkedList<JPanel>();

	private int[][] gameTable;
	private List<Integer> turn = new LinkedList<Integer>();
	private int tempPlayer, winner;
	private int counter = 0;

	private final Color color1 = new Color(123, 13, 150), color2 = new Color(231, 161, 8),
			color1Won = new Color(38, 4, 46), color2Won = new Color(89, 62, 3);
	
	private String pl1, pl2;

	private Thread playerChooserThread = new Thread(new Runnable() {
		@Override
		public void run() {
			tempPlayer = 1;
			while (winner == 0 && counter < lining * lining) {
				((tempPlayer == 1) ? player1 : player2).makeTurn();
				gameTable[turn.get(0)][turn.get(1)] = tempPlayer;
				addStone(turn.get(0), turn.get(1), (tempPlayer == 1) ? color1 : color2);

				checkWin();

				counter++;

				tempPlayer = (tempPlayer == 1) ? 2 : 1;
			}
		}
	});

	/**
	 * @param title
	 * @throws HeadlessException
	 */
	JGameWindow() throws HeadlessException {
		super("Gomoky");
		
		JDialog loadingDialog = new JDialog(JGameWindow.this, "Choose sides",
				ModalityType.APPLICATION_MODAL) {
			JLabel chooseSides;
			JComboBox<String> size1CB, size2CB, tableSizeCB;
			JPanel middlePanel;
			JButton okButton;
			String[] gamers = { "Player", "SollArI.Gomoky 0.1", "SollArI.Gomoky 1.0",
					"SollArI.Gomoky 1.2", "SollArI.Gomoky Middle" };
			String[] tableSizes = {"10", "12", "15", "20", "25", "30", "40", "50", "100"};

			{
				setSize(500, 200);
				setLocation(
						(int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2
								- this.getWidth() / 2,
						(int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2
								- this.getHeight() / 2);
				getContentPane().setLayout(new BorderLayout());

				chooseSides = new JLabel("Choose the sides to play", SwingConstants.CENTER);
				chooseSides.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));

				add(chooseSides, BorderLayout.NORTH);

				okButton = new JButton("Launch");
				okButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));

				add(okButton, BorderLayout.SOUTH);

				middlePanel = new JPanel() {
					{
						setLayout(new GridLayout(2, 2));

						size1CB = new JComboBox<String>(gamers);
						size1CB.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
						size1CB.setBackground(color1);

						add(size1CB);

						size2CB = new JComboBox<String>(gamers);
						size2CB.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
						size2CB.setBackground(color2);

						add(size2CB);
						
						JLabel tableSizeLabel = new JLabel("Table size:");
						tableSizeLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
						
						add(tableSizeLabel);
						
						tableSizeCB = new JComboBox<String>(tableSizes);
						tableSizeCB.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
						tableSizeCB.setSelectedItem(tableSizes[3]);
						
						add(tableSizeCB);
					}
				};

				add(middlePanel, BorderLayout.CENTER);

				okButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						pl1 = (String) size1CB.getSelectedItem();
						pl2 = (String) size2CB.getSelectedItem();
						lining = Integer.valueOf((String) tableSizeCB.getSelectedItem());
						dispose();
					}
				});

				setResizable(false);
			}
		};
		loadingDialog.setVisible(true);

		if (pl1 == null)
			System.exit(0);
		
		gap = TABLE_SIZE / lining;
		
		stoneSize = new Dimension(gap, gap);
		
		gameTable = new int[lining][lining];
		for (int i = 0; i < lining; i++)
			for (int j = 0; j < lining; j++)
				gameTable[i][j] = 0;
		stonesAll = new JPanel[lining][lining];

		setSize(1100, 600);
		setResizable(false);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());

		gamePanel = new JPanel() {
			{
				Dimension size = new Dimension(TABLE_SIZE, TABLE_SIZE);
				setSize(size);
				setPreferredSize(size);
				setMinimumSize(size);
				setMaximumSize(size);

				setLayout(null);

				setBackground(Color.darkGray);

				for (int i = 0; i < lining + 1; i++) {
					addVerticalLineLabel(i * gap - 1);
					addHorizontalLineLabel(i * gap - 1);
				}
			}
		};
		repaintGamePanel();
		add(gamePanel, BorderLayout.CENTER);
		
		switch (pl1) {
		case "Player":
			player1 = new PersonPlayerPanel(turn, gameTable, playerChooserThread,
					1);
			break;
		case "SollArI.Gomoky 0.1":
			player1 = new SollArIPlayerPanel(turn, gameTable, playerChooserThread,
					1);
			break;
		case "SollArI.Gomoky 1.0":
			player1 = new SollArIPlayerPanel2(turn, gameTable, playerChooserThread,
					1);
			break;
		case "SollArI.Gomoky 1.2":
			player1 = new SollArIPlayerPanel3(turn, gameTable, playerChooserThread,
					1);
			break;
		case "SollArI.Gomoky Middle":
			player1 = new SollArIPlayerPanel4(turn, gameTable, playerChooserThread,
					1);
			break;
		default:
			break;
		}

		switch (pl2) {
		case "Player":
			player2 = new PersonPlayerPanel(turn, gameTable, playerChooserThread,
					2);
			break;
		case "SollArI.Gomoky 0.1":
			player2 = new SollArIPlayerPanel(turn, gameTable, playerChooserThread,
					2);
			break;
		case "SollArI.Gomoky 1.0":
			player2 = new SollArIPlayerPanel2(turn, gameTable, playerChooserThread,
					2);
			break;
		case "SollArI.Gomoky 1.2":
			player2 = new SollArIPlayerPanel3(turn, gameTable, playerChooserThread,
					2);
			break;
		case "SollArI.Gomoky Middle":
			player2 = new SollArIPlayerPanel4(turn, gameTable, playerChooserThread,
					2);
			break;
		default:
			break;
		}
		
		player1.setBackground(color1);
		player2.setBackground(color2);

		add(player1, BorderLayout.LINE_START);
		add(player2, BorderLayout.LINE_END);

		gamePanel.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mousePressed(MouseEvent e) {
				Object dim = new Dimension(e.getX() / gap, e.getY() / gap);

				player1.getActionListener().actionPerformed(new ActionEvent(dim, 0, null));
				player2.getActionListener().actionPerformed(new ActionEvent(dim, 0, null));
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});

		pack();
		setVisible(true);

		winner = 0;

		playerChooserThread.setDaemon(true);
		playerChooserThread.start();
	}

	private void checkWin() {
		int counterHorizontal1 = 0, counterVertical1 = 0, counterHorizontal2 = 0,
				counterVertical2 = 0;

		for (int i = 0; i < lining; i++) {
			counterHorizontal1 = 0;
			counterVertical1 = 0;
			counterHorizontal2 = 0;
			counterVertical2 = 0;

			for (int j = 0; j < lining; j++) {
				if (gameTable[i][j] == 1) {
					if (++counterHorizontal1 == 5) {
						winner = 1;
						for (int p = 0; p < 5; p++) {
							stonesAll[i][j - p].setBackground(color1Won);
						}
						gamePanel.repaint();
						return;
					}
				} else
					counterHorizontal1 = 0;

				if (gameTable[i][j] == 2) {
					if (++counterHorizontal2 == 5) {
						winner = 2;
						for (int p = 0; p < 5; p++) {
							stonesAll[i][j - p].setBackground(color2Won);
						}
						gamePanel.repaint();
						return;
					}
				} else
					counterHorizontal2 = 0;

				if (gameTable[j][i] == 1) {
					if (++counterVertical1 == 5) {
						winner = 1;
						for (int p = 0; p < 5; p++) {
							stonesAll[j-p][i].setBackground(color1Won);
						}
						gamePanel.repaint();
						return;
					}
				} else
					counterVertical1 = 0;

				if (gameTable[j][i] == 2) {
					if (++counterVertical2 == 5) {
						winner = 2;
						for (int p = 0; p < 5; p++) {
							stonesAll[j-p][i].setBackground(color2Won);
						}
						gamePanel.repaint();
						return;
					}
				} else
					counterVertical2 = 0;
			}
		}

		for (int i = 4; i < lining; i++) {
			counterHorizontal1 = 0;
			counterVertical1 = 0;
			counterHorizontal2 = 0;
			counterVertical2 = 0;

			for (int j = 0; j <= i; j++) {
				if (gameTable[j][i - j] == 1) {
					if (++counterHorizontal1 == 5) {
						winner = 1;
						for (int p = 0; p < 5; p++) {
							stonesAll[j - p][i - j + p].setBackground(color1Won);
						}
						gamePanel.repaint();
						return;
					}
				} else
					counterHorizontal1 = 0;

				if (gameTable[j][i - j] == 2) {
					if (++counterHorizontal2 == 5) {
						winner = 2;
						for (int p = 0; p < 5; p++) {
							stonesAll[j - p][i - j + p].setBackground(color2Won);
						}
						gamePanel.repaint();
						return;
					}
				} else
					counterHorizontal2 = 0;

				if (gameTable[j][lining - 1 - i + j] == 1) {
					if (++counterVertical1 == 5) {
						winner = 1;
						for (int p = 0; p < 5; p++) {
							stonesAll[j - p][lining - 1 - i + j - p].setBackground(color1Won);
						}
						gamePanel.repaint();
						return;
					}
				} else
					counterVertical1 = 0;

				if (gameTable[j][lining - 1 - i + j] == 2) {
					if (++counterVertical2 == 5) {
						winner = 2;
						for (int p = 0; p < 5; p++) {
							stonesAll[j - p][lining - 1 - i + j - p].setBackground(color2Won);
						}
						gamePanel.repaint();
						return;
					}
				} else
					counterVertical2 = 0;
			}
		}

		for (int i = 4; i < lining; i++) {
			counterHorizontal1 = 0;
			counterVertical1 = 0;
			counterHorizontal2 = 0;
			counterVertical2 = 0;

			for (int j = 0; j <= i; j++) {
				if (gameTable[lining - 1 - j][i - j] == 1) {
					if (++counterHorizontal1 == 5) {
						winner = 1;
						for (int p = 0; p < 5; p++) {
							stonesAll[lining - 1 - j + p][i - j + p].setBackground(color1Won);
						}
						gamePanel.repaint();
						return;
					}
				} else
					counterHorizontal1 = 0;

				if (gameTable[lining - 1 - j][i - j] == 2) {
					if (++counterHorizontal2 == 5) {
						winner = 2;
						for (int p = 0; p < 5; p++) {
							stonesAll[lining - 1 - j + p][i - j + p].setBackground(color2Won);
						}
						gamePanel.repaint();
						return;
					}
				} else
					counterHorizontal2 = 0;

				if (gameTable[lining - 1 - j][lining - 1 - i + j] == 1) {
					if (++counterVertical1 == 5) {
						winner = 1;
						for (int p = 0; p < 5; p++) {
							stonesAll[lining - 1 - j + p][lining - 1 - i + j - p]
									.setBackground(color1Won);
						}
						gamePanel.repaint();
						return;
					}
				} else
					counterVertical1 = 0;

				if (gameTable[lining - 1 - j][lining - 1 - i + j] == 2) {
					if (++counterVertical2 == 5) {
						winner = 2;
						for (int p = 0; p < 5; p++) {
							stonesAll[lining - 1 - j + p][lining - 1 - i + j - p]
									.setBackground(color2Won);
						}
						gamePanel.repaint();
						return;
					}
				} else
					counterVertical2 = 0;
			}
		}
	}

	private Dimension stoneSize;

	private JPanel addStone(Point point, Color color) {
		JPanel tempPanel = new JPanel() {
			{
				setBorder(BorderFactory.createRaisedBevelBorder());
				setBackground(color);
				setSize(stoneSize);
				setLocation(point);
			}
		};
		stones.add(tempPanel);
		repaintGamePanel();
		gamePanel.repaint();
		return tempPanel;
	}

	private void addStone(int x, int y, Color color) {
		stonesAll[x][y] = addStone(new Point(x * gap, y * gap), color);
	}

	private static final Dimension horizontalLineShape = new Dimension(TABLE_SIZE, LINE_WIDTH);
	private static final Dimension verticalLineShape = new Dimension(LINE_WIDTH, TABLE_SIZE);

	private void addHorizontalLineLabel(int y) {
		JPanel tempLabel = new JPanel();
		tempLabel.setBackground(Color.BLACK);
		tempLabel.setSize(horizontalLineShape);
		tempLabel.setLocation(0, y);
		lines.add(tempLabel);
	}

	private void addVerticalLineLabel(int x) {
		JPanel tempLabel = new JPanel();
		tempLabel.setBackground(Color.BLACK);
		tempLabel.setSize(verticalLineShape);
		tempLabel.setLocation(x, 0);
		lines.add(tempLabel);
	}

	private void repaintGamePanel() {
		gamePanel.removeAll();
		for (JPanel stone : stones)
			gamePanel.add(stone);
		for (JPanel line : lines)
			gamePanel.add(line);
	}
}
