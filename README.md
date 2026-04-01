Lab10_6

Use a DFS (Depth-First Search) with coordinate tracking approach:

Whenever the robot moves to a new cell, it records the current position (coordinates) into a set called visited.

Before the recursion proceeds to the next cell, it first checks whether the target coordinate is already in the visited set.

If the coordinate has already been visited, it means the robot has been there before, so it immediately backtracks instead of exploring that path again.
