/**
 *
 * Program to practice Simulated Annealing concepts for a tour
 * Artificial Intelligence, Spring 2018
 * 02 - 18 - 2019
 *
 * @author Allison Smith
 *
 */

import java.util.*;

public class SATour2 {

    /* appropriate data structure to represent a tour: adjacency matrix
       5x5 matrix for 5 locations on tour
       {0: Snell's Farm, 1: Planter's Farm, 2: School, 3: Gym, 4: Movies} */
    private static int tourLength = 6;
    private static String[] locations = {"Snell's Farm", "Planter's Farm", "School", "Gym", "Movies"};

    private static int[][] tBoard = {  {0, 20, 0, 0, 7},
            {20, 0, 14, 8, 12},
            {0, 14, 0, 10, 10},
            {0, 8, 10, 0, 15},
            {7, 12, 10, 15, 0}};

    /**
     *
     * Function to create a tour S that starts and ends at the same location and all locations have an existing
     * path to the subsequent location within the generated tour
     *
     * @return {Array} int[] tourPath sequence of locations for S
     */

    public static int[] makeTour() {
        //int[] tourPath = {0, 1, 2, 3, 4, 0};
        int[] tourPath = new int[tourLength];

        do {
            Integer[] values = new Integer[]{0, 1, 2, 3, 4};

            //shuffle values in array using Java Collection.shuffle() method
            List<Integer> sList = Arrays.asList(values);
            Collections.shuffle(sList);

            //fill tourPath array using shuffled values
            for (int i = 0; i < tourLength - 1; i++) {
                tourPath[i] = sList.get(i);
            }
            tourPath[tourLength - 1] = tourPath[0];

        } while (!tourValid(tourPath));

        return tourPath;
    }

    /**
     *
     * Function to ensure that perturbed or randomly generated tour is valid i.e. every location has a path to next
     * location in the sequence
     *
     * @param tourPath int[] array with order of locations visited on the tour
     * @return {boolean} true if tourPath is valid, false otherwise
     */

    public static boolean tourValid(int[] tourPath) {

        //increment through tourPath and check to ensure that every location has a path to subsequent location
        for (int x = 0; x <tourLength-1; x++) {
            if (tBoard[tourPath[x]][tourPath[x+1]] == 0)
                return false;

        }
        return true;
    }

    /**
     *
     * Evaluation function E() to measure the total tour length (Euclidean distance)
     *
     * @param tour int[] array with order of locations visited on the tour
     * @return {int}, total tour length measured via Euclidean distance referencing tBoard adjacency array
     */

    public static int evalFunction(int[] tour) {
        int total = 0;
        for (int x = 0; x <tourLength-1; x++) {
            total += tBoard[tour[x]][tour[x+1]];
        }

        return total;
    }

    /**
     *
     * Perturbation function the alters tour by probabilistically swapping two cities in a tour
     *
     * @param tourPath original tour sequence to be perturbed via function
     * @return {Array}, new int[] array with perturbed sequence of locations visited
     */

    public static int[] perturbTour(int[] tourPath){
        Random r = new Random();
        int[] tempArray = new int[tourLength];

        int r1, r2;

        do {

            for (int x = 0; x < tourLength; x++) {
                tempArray[x] = tourPath[x];
                //System.out.print(locations[tempArray[x]] + " ");
            }
            //choose two random locations in index positions tourPath[1] through tourPath[4]
            do {
                r1 = r.nextInt(5);
                r2 = r.nextInt(5);
            } while (r1 == r2 || r1 == tourPath[0] || r2 == tourPath[0]);

            //perform swap
            for (int i = 0; i < tourLength; i++) {
                if (tempArray[i] == r1)
                    tempArray[i] = r2;
                else if (tempArray[i] == r2)
                    tempArray[i] = r1;
            }

        } while (!tourValid(tempArray));

        return tempArray;

    }


    /**
     *
     * The ΔE function to measure the change in energy states S and S'
     *
     * @param e1 E() value for Tour S
     * @param e2 E() value for Tour S'
     * @return int, representing change in E() between Tour S and Tour S'
     */

    public static int deltE(int e1, int e2) {

        return e1 - e2;

    }

    /**
     *
     * Function to print a tour route in index form as string locations
     *
     * @param tour the array of location indexes for a certain tour route
     */
    public static void print (int[] tour) {

        for (int i = 0; i < tourLength; i++) {
            System.out.print(locations[tour[i]] + " ");
        }
        System.out.println();

    }



    public static void main (String[] args) {

        //output list of 10 arbitrary S states and corresponding S' states with E and ΔE
        for (int i = 0; i < 10; i++) {
            System.out.println("Run[" + i + "]");
            int[] tourS = makeTour();
            print(tourS);
            int e1 = evalFunction(tourS);
            System.out.println("E(S): " + e1);
            int[] tourS1 = perturbTour(tourS);
            print(tourS1);
            int e2 = evalFunction(tourS1);
            System.out.println("E(S'): " + e2);
            int deltE = deltE(e1, e2);
            System.out.println("ΔE: " + deltE);
            System.out.println("\n\n");

        }

        //EXTRA CREDIT: optimum for Snell's Farm problem using SA algorithm
        int[] currentSolution = {0, 1, 2, 3, 4, 0}; //tour given starting and ending at Snell's Farm
        int eCurrentSolution = evalFunction(currentSolution);
        double temp = 1;   //set T to hot temperature
        double tempdecay = 0.999;
        int maxIterations = 10;
        while (temp > 0.0001) {
            for (int iteration = 0; iteration < maxIterations; iteration++) {
                int[] tempSolution = perturbTour(currentSolution);
                int eTempSolution = evalFunction(tempSolution);

                int deltE = deltE(eCurrentSolution, eTempSolution);

                if (deltE > 0) { //shorter path found - make new current solution
                    for (int i = 0; i < tourLength; i++) {
                        currentSolution[i] = tempSolution[i];
                    }
                } else {
                    if (eTempSolution * -(deltE/temp) > Math.random()) { //probabilistically accept a worse solution
                        for (int i = 0; i < tourLength; i++) {
                            currentSolution[i] = tempSolution[i];
                        }
                    }
                }

             temp *= tempdecay;
            }

        }
        System.out.println("optimum found:");
        print(currentSolution);
        System.out.println("E(S) = " + evalFunction(currentSolution));

    }
}

