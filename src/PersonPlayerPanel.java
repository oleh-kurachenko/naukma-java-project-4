import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * @author Oleh Kurachenko
 *
 */
public class PersonPlayerPanel extends PlayerPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4440719099478248104L;
	private volatile ActionListener listener;
	private volatile boolean isActive = false;
	private JLabel label;

	/**
	 * @param turn
	 * @param gameTable
	 * @param gameThread
	 */
	public PersonPlayerPanel(List<Integer> turn, int[][] gameTable, Thread gameThread, int number) {
		super(turn, gameTable, gameThread, number);
		listener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (isActive) {
					Dimension dim = (Dimension) e.getSource();
					if (gameTable[dim.width][dim.height] == 0) {
						turn.clear();
						turn.add(new Integer(dim.width));
						turn.add(new Integer(dim.height));
						isActive = false;
						synchronized (gameThread) {
							label.setForeground(Color.WHITE);
							gameThread.notify();
						}
					}
				}
			}
		};

		setLayout(new BorderLayout());
		setBorder(BorderFactory.createRaisedBevelBorder());
		setSize(new Dimension(250, 600));
		setPreferredSize(new Dimension(250, 600));
		setMinimumSize(new Dimension(250, 600));
		setMaximumSize(new Dimension(250, 600));

		label = new JLabel("Player " + getNumber());
		label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
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
		isActive = true;
		Thread temp = getGameThread();
		synchronized (temp) {
			try {
				label.setForeground(new Color(232, 0, 0));
				temp.wait();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
