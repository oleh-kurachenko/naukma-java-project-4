import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.List;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * @author Oleh Kurachenko
 *
 */
public class SollArIPlayerPanel extends PlayerPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1856483711487141097L;
	private volatile ActionListener listener;
	private JLabel label;

	/**
	 * @param turn
	 * @param gameTable
	 * @param gameThread
	 */
	public SollArIPlayerPanel(List<Integer> turn, int[][] gameTable, Thread gameThread,
			int number) {
		super(turn, gameTable, gameThread, number);
		listener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// NO REACTION
			}
		};

		setLayout(new BorderLayout());
		setSize(new Dimension(250, 600));
		setPreferredSize(new Dimension(250, 600));
		setMinimumSize(new Dimension(250, 600));
		setMaximumSize(new Dimension(250, 600));

		label = new JLabel("SollArI.Gomoky 0.1");
		label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 25));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setForeground(Color.WHITE);
		add(label, BorderLayout.NORTH);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PlayerPanel#getActionListener()
	 */
	@Override
	public ActionListener getActionListener() {
		return listener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PlayerPanel#makeTurn()
	 */
	@Override
	public void makeTurn() {

		Thread temp = getGameThread(), analyser = new Thread(new Runnable() {

			int size = getGameTable().length;
			int tempGameField[][] = new int[size][size];

			@Override
			public void run() {

				for (int i = 0; i < size; i++)
					for (int j = 0; j < size; j++)
						tempGameField[i][j] = getGameTable()[i][j];

				int bestX = -1, bestY = -1, bestresult = -1000000000, tempResult;

				for (int is = 0; is < size; is++)
					for (int js = 0; js < size; js++) {
						if (tempGameField[is][js] == 0) {
							tempGameField[is][js] = getNumber();
							tempResult = result();
							tempGameField[is][js] = 0;

							if (tempResult > bestresult
									|| (tempResult == bestresult && ((Math.abs(is - size / 2)
											+ Math.abs(js - size / 2)) < (Math.abs(bestX - size / 2)
													+ Math.abs(bestY - size / 2))))) {
								bestX = is;
								bestY = js;

								bestresult = tempResult;
							}
						}
					}

				getTurn().clear();
				getTurn().add(new Integer(bestX));
				getTurn().add(new Integer(bestY));
				synchronized (temp) {
					temp.notify();
				}
			}

			int[] sum = new int[2], couH = new int[2], couV = new int[2];
			boolean[] lIsClH = new boolean[2], lIsClV = new boolean[2];

			private void analyseNext(int number, int f, int s, int[] count, boolean[] lIsCl) {

				if (tempGameField[f][s] == number) {
					count[number - 1]++;
				} else {
					if (count[number - 1] != 0) {
						if (!(lIsCl[number - 1] && (tempGameField[f][s] != 0))) {
							switch (count[number - 1]) {
							case 1:
								if (!(lIsCl[number - 1] || (tempGameField[f][s] != 0)))
									sum[number - 1]++;
								break;
							case 2:
								if ((lIsCl[number - 1] || (tempGameField[f][s] != 0)))
									sum[number - 1] += 40;
								else
									sum[number - 1] += 6;
								break;
							case 3:
								if ((lIsCl[number - 1] || (tempGameField[f][s] != 0)))
									sum[number - 1] += 200;
								else
									sum[number - 1] += 80;
								break;
							case 4:
								if ((lIsCl[number - 1] || (tempGameField[f][s] != 0)))
									sum[number - 1] += 20000;
								else
									sum[number - 1] += 230;
								break;
							case 5:
								if ((lIsCl[number - 1] || (tempGameField[f][s] != 0)))
									sum[number - 1] += 2000000;
								else
									sum[number - 1] += 2000000;
								break;
							default:
								break;
							}
						}
					}
					count[number - 1] = 0;
					lIsCl[number - 1] = (tempGameField[f][s] != 0);
				}
			}

			private int result() {
				
				sum[0] = 0;
				sum[1] = 0;

				for (int i = 0; i < size; i++) {
					couH[0] = 0;
					couH[1] = 0;
					couV[0] = 0;
					couV[1] = 0;

					lIsClH[0] = true;
					lIsClH[1] = true;
					lIsClV[0] = true;
					lIsClV[1] = true;

					for (int j = 0; j < size; j++) {

						analyseNext(1, i, j, couH, lIsClH);

						analyseNext(2, i, j, couH, lIsClH);

						analyseNext(1, j, i, couV, lIsClV);

						analyseNext(2, j, i, couV, lIsClV);
					}
				}

				return (getNumber() == 1) ? (sum[0] - 5000 * sum[1]) : (sum[1] - 5000 * sum[0]);
			}
		});

		analyser.start();
		synchronized (temp) {
			try {
				temp.wait();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
