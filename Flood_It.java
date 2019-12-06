/**
 * Created by Xahid's PC on 10/12/2017.
 */
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import static java.lang.System.out;


public class Flood_It {
    //Input
    public static int[][] input_1 = new int[][]{
            { 1, 2, 2, 3, 4, 5},
            { 1, 3, 3, 4, 4, 6},
            { 5, 6, 6, 2, 3, 2},
            { 5, 6, 3, 2, 1, 1},
            { 1, 2, 2, 3, 4, 5},
            { 1, 3, 3, 4, 4, 6}
    };

    /*public static int[][] input_1 = new int[][]{
            { 1, 1, 4, 5},
            { 2, 4, 4, 5},
            { 2, 1, 3, 6},
            { 2, 1, 1, 6}
    };*/



    //public static int row = 4;
    public static int colour = 6;
    public static int minimum_step=0;
    public static Node test = new Node();
    public static Node debugger = new Node();




    //implementing A* search
    public static void AStarSearch(int[][] input)
    {
        //for already evaluated nodes
        ArrayList<Node> ClosedSet = new ArrayList<Node>();
        //for storing neighbours
        ArrayList<Node> NeighbourSet = new ArrayList<Node>();

        //for currently discovered nodes
        Comparator<Node> comparator = new MyComparator();
        PriorityQueue<Node> OpenSet = new PriorityQueue<Node>(100, comparator);

        //creating starting node
        Node start = new Node();

        /*for (int[] i: input) {
            for (int j: i) {
                out.print(j);
                out.print("  ");
            }
            out.print("\n");
        }*/

        //copy the array of input to Start node
        for(int i = 0; i< input.length; i++){
            for (int j = 0; j < input[i].length; j++){
                start.node[i][j] = input[i][j];
                //out.print(input.length);
            }
        }

        start.g = 0;
        //out.print(start.f);
        start.f = heuristic_cost_estimate_3(start);
        OpenSet.add(start);





        //while loop on openSet
        Node current;
        while ((current = OpenSet.poll()) != null)
        {
            if(goal_matching(current))
            {
                out.print("Sequence of States: \n");
                printSequenceOfState(current);
                out.print("\n");
                out.print("Minimum Step : ");
                out.print(minimum_step);
                return;
            }

            //out.print("didn't get \n");


            OpenSet.remove(current);
            ClosedSet.add(current);

            NeighbourSet = finding_neighbours(current,current.node[0][0]);
            //out.print("\n");
            //printNode(current);
            //out.print("\n");
            //out.print(NeighbourSet.size());
            for(Node cur_neigh : NeighbourSet)
            {
                //printNode(cur_neigh);
                //out.print("\n");
                if(exitsInClosedSet(ClosedSet,cur_neigh))  //alreasy explored
                    continue;
                if(!exitsInOpenSet(OpenSet,cur_neigh))  //discovering new node
                    OpenSet.add(cur_neigh);

                //out.print("\n");
                //out.print(OpenSet.size());
                //out.print("\n");


                //This is not a good path
                if(current.g+1 >= cur_neigh.g)
                    continue;

                // This path is the best for cur_neigh
                cur_neigh.cameFrom = current;
                cur_neigh.g = current.g+1;
                cur_neigh.f = cur_neigh.g+heuristic_cost_estimate_3(cur_neigh);



            }





        }







    }


    //whether exits in closedSet or not
    static boolean exitsInClosedSet(ArrayList<Node> list , Node neigh)
    {
        for(Node n:list)
        {
            if(matched(n,neigh))
                return true;
        }

        return false;
    }

    //whether exits in OpenSet or not
    static boolean exitsInOpenSet(PriorityQueue<Node> list , Node neigh)
    {
        for(Node n:list)
        {
            if(matched(n,neigh))
                return true;
        }

        return false;
    }

    //matches two nodes
    static boolean matched(Node n , Node m)
    {
        for(int i = 0; i< n.node.length; i++){
            for (int j = 0; j < n.node[i].length; j++){
                if(n.node[i][j] != m.node[i][j])
                    return false;
            }
        }
        return true;
    }


    /* I have to implement heuristic in at least two ways */


    //------------------------------------------------------


    //heuristic 1. the number of distinct colour remained
    public static int heuristic_cost_estimate_1(Node n) {

        int count = 0,check = 0;
        int j;
        for (j=1; j<=colour ; j++)
            for (int i=0; i<n.node.length; i++) {
                for (int k = 0; k < n.node.length; k++)
                    if (j == n.node[i][k]) {
                        count++;
                        check = 1;
                        break;
                    }
                if(check==1) {
                    check = 0;
                    break;
                }
            }
        out.print("\n\n\n heuristic for :\n");
        printNode(n);
        out.print("   ");
        out.print(count-1);
        out.print("\n");
        return count-1;
    }

    //-------------------------------------------------



    //heuristic 2. taking upper right angle distance
    public static int heuristic_cost_estimate_2(Node n)
    {
        int cost = 0;

        for(int j = 0 ; j < n.node.length-1 ; j++)
            if(n.node[0][j] != n.node[0][j+1])
                cost++;


        for(int j = 0 ; j < n.node.length-1 ; j++)
            if(n.node[j][n.node.length-1] != n.node[j+1][n.node.length-1])
                cost++;

        out.print("\n\n\n heuristic for :\n");
        printNode(n);
        out.print("   ");
        out.print(cost);
        out.print("\n");


        return cost;
    }

    //-------------------------------------------------
    //heuristic 3. taking connected component
    public static int heuristic_cost_estimate_3(Node n)
    {
        int cost = 0;

        for(int i = 0; i< n.node.length; i++){
            for (int j = 0; j < n.node[i].length; j++){
                test.node[i][j] = n.node[i][j];
            }
        }

        /*out.print("\n paisi\n");
        printNode(test);
        out.print("\n");
        printNode(n);
        */
        //printNode(n);
        while(connectedComponent(test))
        {
            cost++;
            //out.print("\n paisi\n");
            //printNode(test);
            //out.print(cost);
            //out.print("\n");
            //printNode(n);

        }

        //printNode(n);
        //out.print("\ncost: ");
        //out.print(cost);
        //out.print("\n");
        return cost;
    }

    static boolean connectedComponent(Node n)
    {
        for(int i = 0 ; i < n.node.length ; i++)
            for(int j = 0 ; j < n.node.length ; j++)
                if(n.node[i][j] != 0)
                {
                    for(int h = 0; h< debugger.node.length; h++){
                        for (int k = 0; k < debugger.node[h].length; k++){
                            debugger.node[h][k] = n.node[h][k];
                        }
                    }
                    Node o = CCfinder(n,i,j);

                    for(int q = 0; q< o.node.length; q++){
                        for (int w = 0; w < o.node[q].length; w++){
                            test.node[q][w] = o.node[q][w];
                        }
                    }

                    return true;

                }




        return false;
    }


    static Node CCfinder(Node neigh,int x,int y)
    {
        int min = neigh.node[0].length-1;
        int basis = neigh.node[x][y];
        //Node n = new Node();
        //printNode(debugger);

        try {
            if (x < min ) {
                if (debugger.node[x + 1][y] != 0) {
                    if (neigh.node[x + 1][y] == basis) {
                        debugger.node[x][y] = 0;
                        CCfinder(neigh, x + 1, y);
                    }
                }
            }
        }
        catch(Exception e) {
            out.print(x+1);
            out.print(y);
            out.print("exception x+1\n");
        }
        try{
            if (y < min ) {
                if (debugger.node[x][y + 1] != 0){
                    if (neigh.node[x][y + 1] == basis) {
                        debugger.node[x][y] = 0;//printNode(debugger);out.print("\n");
                        CCfinder(neigh, x, y + 1);
                    }
            }
            }
        }
        catch(Exception e) {
            out.print(x);
            out.print(y+1);
            out.print("exception y+1\n");
        }

        try {
            if (x > 0) {
                if (debugger.node[x - 1][y] != 0) {
                    if (neigh.node[x - 1][y] == basis) {
                        debugger.node[x][y] = 0;
                        CCfinder(neigh, x - 1, y);
                    }
                }
            }
        }
        catch(Exception e) {
            out.print(x-1);
            out.print(y);
            out.print("exception x+1\n");
        }

        try {
            if (y > 0) {
                if (debugger.node[x][y - 1] != 0) {
                    if (neigh.node[x][y - 1] == basis) {
                        debugger.node[x][y] = 0;//printNode(debugger);out.print("\n");
                        CCfinder(neigh, x, y - 1);
                    }
                }
            }
        }
        catch(Exception e) {
            out.print(x);
            out.print(y-1);
            out.print("exception x+1\n");
        }

        neigh.node[x][y]=0;

        //if(x < min & y < min)


        return neigh;
    }





    //--------------------------------------------------

    //heuristic 4. taking diagonal distance as heuristic
    public static int heuristic_cost_estimate_4(Node n)
    {
        int cost = 0;

        for(int i = 0 ; i<n.node.length-1 ; i++)
            for(int j = 0 ; j< n.node.length-1 ; j++)
                if(i==j){
                    if(n.node[i][j] != n.node[i+1][j+1])
                        cost++;
                }

        out.print("\n cos: ");
        out.print(cost);
        out.print("\n");
        printNode(n);

        return cost;
    }





    //---------------------------------------------------

    //matching with goal
    public static boolean goal_matching(Node n) {
        int colour = n.node[0][0];

        for (int[] i: n.node)
            for (int j: i)
                if(j != colour)
                    return false;

        return  true;

    }

    //evaluating neighbours
    public static ArrayList<Node> finding_neighbours(Node n, int color)
    {
        ArrayList<Node> array = new ArrayList<Node>();
        int base_colour = n.node[0][0];

        for(int i = 1; i<=colour; i++)
        {
            if(i==color)
                continue;
            Node neigh = new Node();

            //copy the array of n to neigh
            for(int j = 0; j< n.node.length; j++){
                for (int k = 0; k < n.node[j].length; k++){
                    neigh.node[j][k] = n.node[j][k];
                }
            }

            array.add(getNeigh(neigh,i,base_colour,0,0));

            //printing neighbour
            //printNode(neigh);

        }

        return  array;
    }


    //recursive function for getting neighbour
    public static Node getNeigh(Node neigh, int color,int basis,int x,int y)
    {
        int min = neigh.node[0].length-1;
        //out.print(min);
        try {
            if (x < min)
                if(neigh.node[x + 1][y] == basis)
                    getNeigh(neigh, color, basis, x + 1, y);
        }
        catch(ArrayIndexOutOfBoundsException ignored) {
            out.print(x+1);
            out.print(y);
            out.print("exception x+1\n");
        }
        try{
            if (y < min)
                if(neigh.node[x][y+1] == basis)
                    getNeigh(neigh, color, basis, x , y+1);
        }
        catch(ArrayIndexOutOfBoundsException ignored) {
            out.print(x);
            out.print(y+1);
            out.print("exception y+1\n");
        }

        neigh.node[x][y]=color;

        //if(x < min & y < min)


        return neigh;
    }


    //for printing node
    public static void printNode(Node input) {
        for (int[] i: input.node) {
            for (int j: i) {
                out.print(j);
                out.print("  ");
            }
            out.print("\n");
        }
    }

    static void printSequenceOfState(Node n)
    {
        if(n.cameFrom !=null) {
            minimum_step++;
            printSequenceOfState(n.cameFrom);
        }
        printNode(n);
        out.print("\n      |      \n      |      \n");

    }



    //executing main function
    public static void main  (String args[])
    {
        AStarSearch(input_1);

    }
}





//for Nodes
class Node{
    int[][] node = new int[][]{
        { 0, 0, 0, 0, 0, 0},
        { 0, 0, 0, 0, 0, 0},
        { 0, 0, 0, 0, 0, 0},
        { 0, 0, 0, 0, 0, 0},
        { 0, 0, 0, 0, 0, 0},
        { 0, 0, 0, 0, 0, 0}
    };
    int f = 100, g = 100, h;
    Node cameFrom = null;
}



//for priority queue
class MyComparator implements Comparator<Node> {

    public int compare(Node a, Node b) {
        Integer obj1 = new Integer(a.f);
        Integer obj2 = new Integer(b.f);
        return obj1.compareTo(obj2);
    }
}
