import edu.fcps.karel2.Display;
import java.util.HashSet;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Lab10_6 {

    private static final int START_X = 1;
    private static final int START_Y = 1;
    private static final Set<String> visited = new HashSet<>();

    public static void main(String[] args) {
        // 和 Lab10_2006.java 類似的地圖選單式輸入
        Object[] maps = { "maze1", "maze1r", "maze2", "maze2r", "maze3", "maze3r", "maze9", "maze10", "maze11" };

        String mapName = (String) JOptionPane.showInputDialog(
                null,
                "What robot world?",
                "Choose a map",
                JOptionPane.PLAIN_MESSAGE,
                new ImageIcon(),
                maps,
                "maze1");

        // 若按取消，就結束程式
        if (mapName == null) {
            return;
        }

        Display.openWorld("maps/" + mapName + ".map");
        Display.setSize(10, 10);
        Display.setSpeed(9);

        Athlete bot = new Athlete(1, 1, Display.NORTH, Display.INFINITY);

        visited.clear();
        markStart(bot);   // 起點 = 1 顆 beeper
        dfs(bot);
    }

    // 成功 -> true，bot 停在終點
    // 失敗 -> false，bot 退回原格，而且朝向**剛好相反**的方向！ (也就是退出此格的正確方向，避免多餘旋轉)
    public static boolean dfs(Athlete bot) {
        if (isGoal(bot)) {
            return true;
        }

        if (!isStart(bot)) {
            visited.add(posKey(bot));
            placeTrailMarker(bot);
        }

        // 記下四周資訊 (以進入時的方向為準)
        boolean front = bot.frontIsClear();
        boolean right = bot.rightIsClear();
        boolean left  = bot.leftIsClear();

        // 情況1：死巷 (無路可走)
        if (!front && !right && !left) {
            if (!isStart(bot)) {
                removeTrailMarker(bot);
            }
            bot.turnAround(); // 反轉方向，準備退回
            return false;
        }

        if (front) {
            bot.move();
            if (isGoal(bot)) return true;
            
            if (hasTrailMarker(bot) || isStart(bot)) {
                bot.turnAround();
                bot.move(); 
            } else {
                if (dfs(bot)) return true;
                bot.move(); 
            }
            
            // front 嘗試失敗後，bot 退回此格且面向進入時的「後」方。
            if (!right && !left) {
                if (!isStart(bot)) removeTrailMarker(bot);
                return false; // 直接退回，完全不需要旋轉！
            } else {
                bot.turnAround(); // 把 bot 轉回原本的進入方向，以便後續嘗試 right/left
            }
        }

        if (right) {
            bot.turnRight(); 
            bot.move();
            if (isGoal(bot)) return true;
            
            if (hasTrailMarker(bot) || isStart(bot)) {
                bot.turnAround();
                bot.move(); 
            } else {
                if (dfs(bot)) return true;
                bot.move(); 
            }
            
            // right 嘗試失敗後，bot 退回此格且面向進入時的「左」方。
            if (!left) {
                if (!isStart(bot)) removeTrailMarker(bot);
                bot.turnLeft(); // 從面「左」左轉，就會變成面「後」
                return false;
            } else {
                bot.turnRight(); // 把 bot 從面「左」轉右，恢復成原本的進入方向，以便嘗試 left
            }
        }

        if (left) {
            bot.turnLeft();
            bot.move();
            if (isGoal(bot)) return true;
            
            if (hasTrailMarker(bot) || isStart(bot)) {
                bot.turnAround();
                bot.move(); 
            } else {
                if (dfs(bot)) return true;
                bot.move(); 
            }
            
            // left 嘗試失敗後，bot 退回此格且面向進入時的「右」方。
            if (!isStart(bot)) removeTrailMarker(bot);
            bot.turnRight(); // 從面「右」右轉，就會變成面「後」
            return false;
        }

        return false;
    }

    // ===== beeper 判斷 =====

    // 終點 = 有 beeper，而且不是起點、不是已走過的點
    public static boolean isGoal(Athlete bot) {
        if (!bot.nextToABeeper()) {
            return false;
        }

        if (isStart(bot) || hasTrailMarker(bot)) {
            return false;
        }

        return true;
    }

    // 起點 = 固定座標 (1,1)
    public static boolean isStart(Athlete bot) {
        return bot.getX() == START_X && bot.getY() == START_Y;
    }

    // 是否已走過
    public static boolean hasTrailMarker(Athlete bot) {
        return visited.contains(posKey(bot));
    }

    // ===== beeper 操作 =====

    public static void markStart(Athlete bot) {
        bot.putBeeper();
    }

    public static void placeTrailMarker(Athlete bot) {
        bot.putBeeper();
    }

    public static void removeTrailMarker(Athlete bot) {
        // 註解或刪除這行：保留走過的記憶，避免在有島嶼的迷宮中重複繞圈
        // visited.remove(posKey(bot));
        if (bot.nextToABeeper()) {
            bot.pickBeeper();
        }
    }

    private static String posKey(Athlete bot) {
        return bot.getX() + "," + bot.getY();
    }
}