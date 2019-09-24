import java.util.*;

/**
 * the launch point of the code
 * @author zjx
 *
 */
public class EightPuzzlePrint {
    State initial;
    State goal;
    int tiles = 8;
    double breadthFirstTime;
    double bestFirstTime;

    enum SearchAlgorithm {
        BREADTHFIRSTSEARCH,
        BESTFIRSTSEARCH
    }

    public EightPuzzlePrint() {
        super();
    }

    public EightPuzzlePrint(State initial, State goal, int tiles) {
        super();
    }

    public void start() {
        //initialize the init state and goal state as 2d array
        int[][] init_tile = {{2,3,6}, {1,4,8}, {7,5,0}};
        //int[][] init_tile = {{2,3,1}, {0,4,6}, {7,5,8}};
        State init = new State(init_tile, 0);
        int[][] goal_tile = {{1,2,3}, {4,5,6}, {7,8,0}};
        State goal = new State(goal_tile, 0);

        this.initial = init;
        this.goal = goal;
        this.tiles = 8;
        try {
            long start = System.currentTimeMillis();
            new PuzzleSolverPrint(this.initial, this.goal, SearchAlgorithm.BREADTHFIRSTSEARCH).run();
            long end = System.currentTimeMillis();
            this.breadthFirstTime = (end - start);
            System.out.println("It took breadth first search "+ breadthFirstTime + " milliseconds");
            System.out.println("====================================================================");
            start = System.currentTimeMillis();
            new PuzzleSolverPrint(this.initial, this.goal, SearchAlgorithm.BESTFIRSTSEARCH).run();
            end = System.currentTimeMillis();
            this.bestFirstTime = (end - start);
            System.out.println("It took best first search "+ bestFirstTime + " milliseconds");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //start the puzzle game
        EightPuzzlePrint epp = new EightPuzzlePrint();
        epp.start();
    }
}

class PuzzleSolverPrint {
    State current;
    State goal;
    List<State> open;
    List<State> closed;
    int depth = 0;

    Enum algorithm;

    public PuzzleSolverPrint(State current, State goal, Enum searchAlgorithm) {
        this.current = current;
        this.goal = goal;
        this.algorithm = searchAlgorithm;
        open = new ArrayList<State>();
        closed = new ArrayList<State>();
        open.add(current);

    }

    /**
     * check if the generated state is in open or closed
     * the purpose is to avoid a circle
     * @param s
     * @return
     */
    public int[] check_inclusive(State s) {
        int in_open = 0;
        int in_closed = 0;
        int[] ret = {-1,-1};

        for(int i = 0; i < open.size(); i++){
            State temp = open.get(i);
            if (temp.equals(s)) {
                in_open = 1;
                ret[1] = i;
                break;
            }
        }
        for(int j = 0; j < closed.size(); j++){
            State temp = closed.get(j);
            if (temp.equals(s)) {
                in_closed = 1;
                ret[1] = j;
                break;
            }
        }
        if (in_open == 0 && in_closed == 0) {
            ret[0] = 1;     //the child is not in open or closed
        } else if (in_open == 1 && in_closed == 0) {
            ret[0] = 2;     //the child is already in open
        } else if(in_open == 0 && in_closed == 1) {
            ret[0] = 3;     //the child is already in closed
        }
        return ret;
    }

    /**
     * four types of walks
     * best first search
     *  ¡ü ¡ý ¡û ¡ú
     *  the blank tile is represent by '0'.
     * @throws CloneNotSupportedException
     */
    public void breadth_first_state_walk() throws CloneNotSupportedException {
        //add closed state
        closed.add(current);
        open.remove(current);

        //move to the next heuristic state
        int[][] walk_state = current.getTile_seq();
        int row = 0, col = 0;

        for (int i = 0; i < walk_state.length; i++) {
            for (int j = 0; j < walk_state[i].length; j++) {
                if (walk_state[i][j] == 0) {
                    row = i;
                    col = j;
                    break;
                }
            }
        }
        depth += 1;

        //do the state space walk
        //¡ü (up)
        if (row - 1 >= 0) {
            breadthFirstSearch(walk_state, row, col, row - 1, col);
        }
        //¡ý (down)
        if (row + 1 < walk_state.length) {
            breadthFirstSearch(walk_state, row, col, row + 1, col);

        }
        //¡û (left)
        if (col - 1 >= 0) {
            breadthFirstSearch(walk_state, row, col, row, col - 1);
        }
        //¡ú (right)
        if (col + 1 < walk_state.length) {
            breadthFirstSearch(walk_state, row, col, row, col + 1);
        }

        current = open.get(0);
    }

    private void breadthFirstSearch(int[][] walk_state, int blankRow, int blankCol, int row, int col) {
        int[][] tempCurrent = new int[walk_state.length][walk_state[0].length];
        for (int i = 0; i < walk_state.length; i++) {
            for (int j = 0; j < walk_state[i].length; j++) {
                tempCurrent[i][j] = walk_state[i][j];
            }
        }
        int temp = tempCurrent[blankRow][blankCol];
        tempCurrent[blankRow][blankCol] = tempCurrent[row][col];
        tempCurrent[row][col] = temp;

        State tempState = new State(tempCurrent, depth);
        int[] flag = check_inclusive(tempState);
        if (flag[0] == 1) {
            open.add(tempState);
        }
        if (flag[0] == 2) {
            if (open.get(flag[1]).getDepth() > tempState.getDepth()) {
                open.set(flag[1], tempState);
            }
        }
        if (flag[0] == 3) {
            if (closed.get(flag[1]).getDepth() > tempState.getDepth()) {
                closed.remove(flag[1]);
                open.add(tempState);
            }
        }
    }

    /**
     * solve the game using heuristic search
     * heuristics
     * (1) Tiles out of place
     * (2) Sum of distances out of place
     * (3) 2 x the number of direct tile reversals
     * evaluation function
     * f(n) = g(n) + h(n)
     * g(n) = depth of path length to start state
     * h(n) = (1) + (2) + (3)
     */
    public int calculate_heuristic(State s){
        int[][] curr_seq = s.getTile_seq();
        int[][] goal_seq = goal.getTile_seq();

        //(1) Tiles out of place
        int h1 = 0;
        for (int i = 0; i < curr_seq.length; i++){
            for (int j = 0; j < curr_seq[i].length; j++){
                if(curr_seq[i][j] != goal_seq[i][j]){
                    h1++;
                }
            }
        }

        //(2) Sum of distances out of place
        int h2 = 0;
        for (int i = 0; i < curr_seq.length; i++){
            for (int j = 0; j < curr_seq[i].length; j++){

                for (int x = 0; x < goal_seq.length; x++){
                    for (int y = 0; y< goal_seq[x].length; y++){
                        if( curr_seq[i][j] == goal_seq[x][y]){
                            h2 += Math.abs(i - x) + Math.abs(j - y);
                        }
                    }
                }
            }
        }

        //(3) 2 x the number of direct tile reversals

        int h3 = 0;
        for (int i = 0; i < curr_seq.length; i++){
            for (int j = 0; j < curr_seq[i].length; j++){
                if( j+1 < goal_seq[i].length && curr_seq[i][j] == goal_seq[i][j+1] && curr_seq[i][j+1] == goal_seq[i][j]){
                    h3++;
                }
                if(i+1 < goal_seq.length && curr_seq[i][j] == goal_seq[i+1][j] && curr_seq[i+1][j] == goal_seq[i][j]){
                    h3++;
                }
            }
        }
        h3 *= 2;

       return h1+h2+h3;
    }

    public void heuristic_test(State tempState) {

        int hn = calculate_heuristic(tempState);
        int hc = calculate_heuristic(this.current);
        int cost = Math.abs(tempState.getDepth() - current.getDepth());
        if (hc > cost + hn)
            hc = cost + hn;
        current.setWeight(current.getDepth() + hc);
        tempState.setWeight(tempState.getDepth() + hn);

        //set the heuristic value for current state

    }

    /**
     * print current state unit
     */
    public void print() {
        int[][] test = current.getTile_seq();
        for(int i = 0; i < test.length; i++) {
            for(int j = 0; j < test[i].length; j++) {
                System.out.print(test[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void run() {
        //output the start state
        print();
        System.out.println("start state !!!!!");
        //heuristic_test(current);
        /**/
        try {
            int path = 0;
            boolean monotonicity = true;
            while (!current.equals(goal)) {
                if(algorithm.equals(EightPuzzlePrint.SearchAlgorithm.BREADTHFIRSTSEARCH)) {
                    breadth_first_state_walk();
                }
                else{
                    State parentNode = current;
                    best_first_state_walk();
                    State childNode = current;

                    if (parentNode.getWeight() > (childNode.getDepth() - parentNode.getDepth()) + childNode.getWeight()){
                        monotonicity = false;
                        //System.out.println(parentNode.getWeight());
                    }
                }
                //print();
                path++;

            }
            System.out.println("It took " + path + " iterations");
            System.out.println("The length of the path is: " + current.getDepth());
            System.out.println("Monotonicity bound: " + monotonicity);
            //output the goal state
            int[][] test = goal.getTile_seq();
            for (int i = 0; i < test.length; i++) {
                for (int j = 0; j < test[i].length; j++) {
                    System.out.print(test[i][j] + " ");
                }
                System.out.println();
            }
            System.out.println("goal state !!!!!");


        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        /* */
    }

    private void best_first_state_walk() {
        //add closed state
        closed.add(current);
        open.remove(current);
        //move to the next heuristic state
        int[][] walk_state = current.getTile_seq();
        int row=0,col=0;

        for(int i = 0 ; i<walk_state.length ; i++){
            for(int j = 0 ; j<walk_state[i].length ; j++){
                if(walk_state[i][j] == 0){
                    row = i;
                    col = j;
                    break;
                }
            }
        }
        depth += 1;
        //do the state space walk
        //¡ü (up)
        if (row - 1 >= 0) {
            bestFirstSearch(walk_state, row, col, row - 1, col);
        }
        //¡ý (down)
        if (row + 1 < walk_state.length) {
            bestFirstSearch(walk_state, row, col, row + 1, col);

        }
        //¡û (left)
        if (col - 1 >= 0) {
            bestFirstSearch(walk_state, row, col, row, col - 1);
        }
        //¡ú (right)
        if (col + 1 < walk_state.length) {
            bestFirstSearch(walk_state, row, col, row, col + 1);
        }

        //sort the open list first by h(n) then g(n)
        Collections.sort(open, new Comparator<State>() {
            @Override
            public int compare(State a1, State a2) {
                if(a1.getWeight() > a2.getWeight()){
                    return 1;
                }else if(a1.getWeight() == a2.getWeight()){
                    if(a1.getDepth() > a2.getDepth()){
                        return 1;
                    }else{
                        return 0;
                    }
                }else{
                    return -1;
                }
            }
        });

        current = open.get(0);
    }

    private void bestFirstSearch(int[][] walk_state, int blankRow, int blankCol, int row, int col) {
        int[][] tempCurrent = new int[walk_state.length][walk_state[0].length];
        for (int i = 0; i < walk_state.length; i++) {
            for (int j = 0; j < walk_state[i].length; j++) {
                tempCurrent[i][j] = walk_state[i][j];
            }
        }
        int temp = tempCurrent[blankRow][blankCol];
        tempCurrent[blankRow][blankCol] = tempCurrent[row][col];
        tempCurrent[row][col] = temp;

        State tempState = new State(tempCurrent, depth);
        int[] flag = check_inclusive(tempState);
        if (flag[0] == 1) {
            heuristic_test(tempState);
            open.add(tempState);
        }
        if (flag[0] == 2) {
            if (open.get(flag[1]).getDepth() > tempState.getDepth()) {
                open.set(flag[1], tempState);
            }
        }
        if (flag[0] == 3) {
            if (closed.get(flag[1]).getDepth() > tempState.getDepth()) {
                closed.remove(flag[1]);
                open.add(tempState);
            }
        }
    }

}
