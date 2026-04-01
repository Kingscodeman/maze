import edu.fcps.karel2.Display;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Lab10_6_2 {

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

        markStart(bot);   // 起點放 1 顆 beeper
        dfs(bot);
    }

    // 成功 -> true，bot 停在終點
    // 失敗 -> false，bot 退回原格，而且朝向**剛好相反**的方向！ (也就是退出此格的正確方向，避免多餘旋轉)
    public static boolean dfs(Athlete bot) {
        if (isGoal(bot)) {
            return true;
        }

        if (!isStart(bot)) {
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

    /* 
    // 【歷史解法備份：用放不同數量的 Beeper 來判斷狀態】
    // 計算腳下的 Beeper 數量 (最多算到 3 個就夠分辨了)
    public static int getBeeperCount(Athlete bot) {
        int count = 0;
        while(bot.nextToABeeper() && count < 3) {
            bot.pickBeeper();
            count++;
        }
        // 算完要把 beeper 放回去，恢復原狀
        for(int i = 0; i < count; i++) {
            bot.putBeeper();
        }
        return count;
    }
    // 當時的判斷邏輯如下：
    // 終點 = getBeeperCount(bot) == 1;
    // 起點 = getBeeperCount(bot) == 3;
    // 已走過 = getBeeperCount(bot) == 2;
    */

    // 終點 = 碰到 beeper，而且「前、右、左」三個方向（如果沒有牆壁的話）的下一格都沒有 beeper
    // （剛走過來的「後方」不用檢查，因為那是死角）
    public static boolean isGoal(Athlete bot) {
        // 【關鍵修復】起點絕對不會是終點，必須先排除！
        // 否則一開始站在起點(有beeper)，且四周都還沒走過(沒beeper)，就會誤以為這裡是終點而結束！
        if (isStart(bot)) {
            return false;
        }

        if (!bot.nextToABeeper()) {
            return false;
        }

        boolean hasAdjacentBeeper = false;

        // 往「前」探勘一格
        if (bot.frontIsClear()) {
            bot.move();
            if (bot.nextToABeeper()) hasAdjacentBeeper = true;
            bot.turnAround(); bot.move(); bot.turnAround();
        }
        // 往「右」探勘一格
        if (!hasAdjacentBeeper && bot.rightIsClear()) {
            bot.turnRight(); bot.move();
            if (bot.nextToABeeper()) hasAdjacentBeeper = true;
            // 從右邊退回原位。此時朝向左邊喔！如果要轉回正前方，必須「向右轉」
            bot.turnAround(); bot.move(); bot.turnRight();
        }
        // 往「左」探勘一格
        if (!hasAdjacentBeeper && bot.leftIsClear()) {
            bot.turnLeft(); bot.move();
            if (bot.nextToABeeper()) hasAdjacentBeeper = true;
            // 從左邊退回原位。此時朝向右邊喔！如果要轉回正前方，必須「向左轉」
            bot.turnAround(); bot.move(); bot.turnLeft();
        }

        // 如果周圍找不到其他的 Beeper，這顆就是孤立的終點。
        // 反之，如果周圍還有 Beeper，代表它是和別人連在一起的「痕跡 (Trail)」。
        return !hasAdjacentBeeper;
    }

    // 起點：使用預設起始座標 (1, 1) 來判斷
    public static boolean isStart(Athlete bot) {
        return bot.getX() == 1 && bot.getY() == 1;
    }

    // 如果腳下有 1 顆 beeper，而它又不是終點的話，那它就是我們留下的軌跡。
    // 注意：因為外層的 dfs 已經提早呼叫過 isGoal 了，這裡不需要重複呼叫 isGoal() 造成多檢查一次！
    public static boolean hasTrailMarker(Athlete bot) {
        return bot.nextToABeeper();
    }

    // ===== beeper 操作 =====

    public static void markStart(Athlete bot) {
        bot.putBeeper(); // 只放 1 顆
    }

    public static void placeTrailMarker(Athlete bot) {
        bot.putBeeper(); // 只放 1 顆
    }

    public static void removeTrailMarker(Athlete bot) {
        // 從冤枉路退回時，撿起 1 顆 beeper
        if (bot.nextToABeeper()) {
            bot.pickBeeper();
        }
    }
}