Lab10_6

Use a DFS (Depth-First Search) with coordinate tracking approach:

Whenever the robot moves to a new cell, it records the current position (coordinates) into a set called visited.

Before the recursion proceeds to the next cell, it first checks whether the target coordinate is already in the visited set.

If the coordinate has already been visited, it means the robot has been there before, so it immediately backtracks instead of exploring that path again.


Lab10_6_1

When the robot steps onto a cell and finds one beeper on the ground, it cannot distinguish between the two situations:

Whether it has reached the actual goal (a beeper originally placed on the map), or
Whether it has returned to a spot it previously visited, encountering its own marker after looping around an island.

Because both cases look identical (a single beeper), the robot cannot determine whether it should declare success (goal reached) or treat it as a dead end and backtrack.

Lab10_6_2

Use the property that the goal beeper is typically isolated (no neighboring beepers) to distinguish it from the trail (a sequence of connected beepers).
