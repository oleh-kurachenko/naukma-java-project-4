import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JPanel;

/**
 * @author Oleh Kurachenko
 *
 */
public abstract class PlayerPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2996378375465345006L;
	private List<Integer> turn;
	private int[][] gameTable;
	private Thread gameThread;
	private int number;
	
	/**
	 * 
	 */
	public PlayerPanel(List<Integer> turn, int[][] gameTable, Thread gameThread, int number) {
		this.turn = turn;
		this.gameTable = gameTable;
		this.gameThread = gameThread;
		this.number = number;
	}

	/**
	 * @return the turn
	 */
	List<Integer> getTurn() {
		return turn;
	}

	/**
	 * @return the gameTable
	 */
	int[][] getGameTable() {
		return gameTable;
	}

	/**
	 * @return the gameThread
	 */
	Thread getGameThread() {
		return gameThread;
	}

	/**
	 * @return the number
	 */
	int getNumber() {
		return number;
	}

	public abstract ActionListener getActionListener();
	
	public abstract void makeTurn();
}
