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
public class SollArIPlayerPanel4 extends PlayerPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2558900256214287218L;
	private volatile ActionListener listener;
	private JLabel label;

	/**
	 * @param turn
	 * @param gameTable
	 * @param gameThread
	 */
	public SollArIPlayerPanel4(List<Integer> turn, int[][] gameTable, Thread gameThread,
			int number) {
		super(turn, gameTable, gameThread, number);
		listener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// NO REACTION
			}
		};

		setLayout(new BorderLayout());
		setBackground(new Color(96, 6, 152));
		setSize(new Dimension(250, 600));
		setPreferredSize(new Dimension(250, 600));
		setMinimumSize(new Dimension(250, 600));
		setMaximumSize(new Dimension(250, 600));

		label = new JLabel("SollArI.Gomoky Middle");
		label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
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
			int tempGameField[][] = new int[size + 8][size + 8];

			@Override
			public void run() {

				for (int i = 0; i < size + 8; i++)
					for (int j = 0; j < size + 8; j++)
						tempGameField[i][j] = 3;

				for (int i = 0; i < size; i++)
					for (int j = 0; j < size; j++)
						tempGameField[i + 4][j + 4] = getGameTable()[i][j];

				int bestX = -1, bestY = -1, bestresult = -1000000000, tempResult;

				for (int is = 0; is < size; is++)
					for (int js = 0; js < size; js++) {
						if (tempGameField[is + 4][js + 4] == 0) {
							tempGameField[is + 4][js + 4] = getNumber();
							tempResult = result(is, js);
							tempGameField[is + 4][js + 4] = 0;

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

			private int result(int x, int y) {
				x += 4;
				y += 4;

				int sum = 0, tX, tY;

				int n, s, e, w, ne, se, nw, sw;
				n = s = e = w = ne = se = nw = sw = 0;

				boolean nc, sc, ec, wc, nec, sec, nwc, swc;
				nc = sc = ec = wc = nec = sec = nwc = swc = false;

				int on, os, oe, ow, one, ose, onw, osw;
				on = os = oe = ow = one = ose = onw = osw = 0;

				boolean onc, osc, oec, owc, onec, osec, onwc, oswc;
				onc = osc = oec = owc = onec = osec = onwc = oswc = false;

				// SINGLE
				tX = x;
				tY = y - 1;
				while (tempGameField[tX][tY] == getNumber()) {
					tY--;
					n++;
				}
				nc = !(tempGameField[tX][tY] == 0);

				tX = x;
				tY = y + 1;
				while (tempGameField[tX][tY] == getNumber()) {
					tY++;
					s++;
				}
				sc = !(tempGameField[tX][tY] == 0);

				tX = x - 1;
				tY = y;
				while (tempGameField[tX][tY] == getNumber()) {
					tX--;
					w++;
				}
				wc = !(tempGameField[tX][tY] == 0);

				tX = x + 1;
				tY = y;
				while (tempGameField[tX][tY] == getNumber()) {
					tX++;
					e++;
				}
				ec = !(tempGameField[tX][tY] == 0);
				// COMBINED
				tX = x - 1;
				tY = y - 1;
				while (tempGameField[tX][tY] == getNumber()) {
					tY--;
					tX--;
					nw++;
				}
				nwc = !(tempGameField[tX][tY] == 0);

				tX = x + 1;
				tY = y - 1;
				while (tempGameField[tX][tY] == getNumber()) {
					tY--;
					tX++;
					ne++;
				}
				nec = !(tempGameField[tX][tY] == 0);

				tX = x - 1;
				tY = y + 1;
				while (tempGameField[tX][tY] == getNumber()) {
					tY++;
					tX--;
					sw++;
				}
				swc = !(tempGameField[tX][tY] == 0);

				tX = x + 1;
				tY = y + 1;
				while (tempGameField[tX][tY] == getNumber()) {
					tY++;
					tX++;
					se++;
				}
				sec = !(tempGameField[tX][tY] == 0);

				// OPONENT

				int opNumber = (getNumber() == 1) ? 2 : 1;

				// SINGLE
				tX = x;
				tY = y - 1;
				while (tempGameField[tX][tY] == opNumber) {
					tY--;
					on++;
				}
				onc = !(tempGameField[tX][tY] == 0);

				tX = x;
				tY = y + 1;
				while (tempGameField[tX][tY] == opNumber) {
					tY++;
					os++;
				}
				osc = !(tempGameField[tX][tY] == 0);

				tX = x - 1;
				tY = y;
				while (tempGameField[tX][tY] == opNumber) {
					tX--;
					ow++;
				}
				owc = !(tempGameField[tX][tY] == 0);

				tX = x + 1;
				tY = y;
				while (tempGameField[tX][tY] == opNumber) {
					tX++;
					oe++;
				}
				oec = !(tempGameField[tX][tY] == 0);
				// COMBINED
				tX = x - 1;
				tY = y - 1;
				while (tempGameField[tX][tY] == opNumber) {
					tY--;
					tX--;
					onw++;
				}
				onwc = !(tempGameField[tX][tY] == 0);

				tX = x + 1;
				tY = y - 1;
				while (tempGameField[tX][tY] == opNumber) {
					tY--;
					tX++;
					one++;
				}
				onec = !(tempGameField[tX][tY] == 0);

				tX = x - 1;
				tY = y + 1;
				while (tempGameField[tX][tY] == opNumber) {
					tY++;
					tX--;
					osw++;
				}
				oswc = !(tempGameField[tX][tY] == 0);

				tX = x + 1;
				tY = y + 1;
				while (tempGameField[tX][tY] == opNumber) {
					tY++;
					tX++;
					ose++;
				}
				osec = !(tempGameField[tX][tY] == 0);

				// 5
				if ((n + s > 3) || (w + e > 3) || (nw + se > 3) || (ne + sw > 3))
					return 2000000000;

				if ((on + os > 3) || (ow + oe > 3) || (onw + ose > 3) || (one + osw > 3))
					return 1900000000;

				// 4
				if ((n + s == 3 && (!nc && !sc)) || (w + e == 3 && (!wc && !ec))
						|| (nw + se == 3 && (!nwc && !sec)) || (ne + sw == 3 && (!nec && !swc)))
					sum += surrand50(40000000);
				if ((n + s == 3 && (nc ^ sc)) || (w + e == 3 && (wc ^ ec))
						|| (nw + se == 3 && (nwc ^ sec)) || (ne + sw == 3 && (nec ^ swc)))
					sum += surrand50(25000000);

				if ((on + os == 3 && (!onc && !osc)) || (ow + oe == 3 && (!owc && !oec))
						|| (onw + ose == 3 && (!onwc && !osec))
						|| (one + osw == 3 && (!onec && !oswc)))
					sum += surrand50(35000000);
				if ((on + os == 3 && (onc ^ osc)) || (ow + oe == 3 && (owc ^ oec))
						|| (onw + ose == 3 && (onwc ^ osec)) || (one + osw == 3 && (onec ^ oswc)))
					sum += surrand50(15000000);

				// 3
				if ((n + s == 2 && (!nc && !sc)) || (w + e == 2 && (!wc && !ec))
						|| (nw + se == 2 && (!nwc && !sec)) || (ne + sw == 2 && (!nec && !swc)))
					sum += surrand50(14000000);
				if ((n + s == 2 && (nc ^ sc)) || (w + e == 2 && (wc ^ ec))
						|| (nw + se == 2 && (nwc ^ sec)) || (ne + sw == 2 && (nec ^ swc)))
					sum += surrand50(7000000);

				if ((on + os == 2 && (!onc && !osc)) || (ow + oe == 2 && (!owc && !oec))
						|| (onw + ose == 2 && (!onwc && !osec))
						|| (one + osw == 2 && (!onec && !oswc)))
					sum += surrand50(2000000);
				if ((on + os == 2 && (onc ^ osc)) || (ow + oe == 2 && (owc ^ oec))
						|| (onw + ose == 2 && (onwc ^ osec)) || (one + osw == 2 && (onec ^ oswc)))
					sum += surrand50(1000000);

				// 2
				if ((n + s == 1 && (!nc && !sc)) || (w + e == 1 && (!wc && !ec))
						|| (nw + se == 1 && (!nwc && !sec)) || (ne + sw == 1 && (!nec && !swc)))
					sum += surrand50(1800000);
				if ((n + s == 1 && (nc ^ sc)) || (w + e == 1 && (wc ^ ec))
						|| (nw + se == 1 && (nwc ^ sec)) || (ne + sw == 1 && (nec ^ swc)))
					sum += surrand50(900000);
				
				if ((on + os == 1 && (!onc && !osc)) || (ow + oe == 1 && (!owc && !oec))
						|| (onw + ose == 1 && (!onwc && !osec))
						|| (one + osw == 1 && (!onec && !oswc)))
					sum += surrand50(300000);
				if ((on + os == 1 && (onc ^ osc)) || (ow + oe == 1 && (owc ^ oec))
						|| (onw + ose == 1 && (onwc ^ osec)) || (one + osw == 1 && (onec ^ oswc)))
					sum += surrand50(100000);

				// 1

				return sum;
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
	
	private int surrand50(int value) {
		return (int) (value * (1+ Math.random()/2));
	}

}
