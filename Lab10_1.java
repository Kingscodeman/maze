import edu.fcps.karel2.Display;
import javax.swing.JOptionPane;

public class Lab10_1 {

	public static void main(String[] args) {
		String filename = JOptionPane.showInputDialog("What robot world?");
		Display.openWorld("maps/" + filename + ".map");
		Display.setSize(10, 10);
		Display.setSpeed(5);

		Athlete bot = new Athlete(1, 1, Display.NORTH, Display.INFINITY);

		// 50-50 chance: choose one wall-follow rule for this entire maze run.
		boolean useLeftWall = Math.random() < 0.5;

		while (!bot.nextToABeeper()) {
			if (useLeftWall) {
				followWallsLeft(bot);
			} else {
				followWallsRight(bot);
			}
		}
	}

	// Left-hand wall-following: left, then front, else turn right.
	public static void followWallsLeft(Athlete bot) {
		if (bot.leftIsClear()) {
			bot.turnLeft();
			bot.move();
		} else if (bot.frontIsClear()) {
			bot.move();
		} else {
			bot.turnRight();
		}
	}

	// Right-hand wall-following: right, then front, else turn left.
	public static void followWallsRight(Athlete bot) {
		if (bot.rightIsClear()) {
			bot.turnRight();
			bot.move();
		} else if (bot.frontIsClear()) {
			bot.move();
		} else {
			bot.turnLeft();
		}
	}
}
