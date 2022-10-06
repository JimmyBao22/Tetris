package assignment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.StringTokenizer;

public class JBrainTraining3 extends JTetris {
    private static Brain currentBrain;

    private int getNumBlocksPlayed(Brain b) throws InterruptedException{
        stopGame();
        currentBrain = b;
        startGame();
        while (board.getMaxHeight() <= HEIGHT) {
            // System.out.println("not done yet: " + board.getRowsCleared());
            Thread.sleep(10);
        }
        // now the game is stopped
        return count;
    }

    private static final int NUM_AGENTS = 500;
    private static final int NUM_RUNS_PER_AGENT = 3;
    private static final int NUM_GENERATIONS = 10;
    private static final int NUM_PIECE_TYPES = Piece.PieceType.values().length;
    private int numTopBrains;
    private static int numMetrics;
    private double[][] weights;
    private static final String FILE_NAME = "weights4.txt";

    private double[][] makeRandomStartingWeights() {
        double[][] randomWeights = new double[NUM_AGENTS][numMetrics];
        for (int i = 0; i < randomWeights.length; i++) {
            for (int j = 0; j < randomWeights[i].length; j++) {
                randomWeights[i][j] = Math.random();
            }
        }
        return randomWeights;
    }

    private void train() throws InterruptedException, IOException {
        int maximumResult = 0;
        for (int numGenerations = 0; numGenerations < NUM_GENERATIONS; numGenerations++) {
            int[][] results = new int[NUM_AGENTS][NUM_RUNS_PER_AGENT];
            double[] medians = new double[NUM_AGENTS];

            for (int weightIndex = 0; weightIndex < NUM_AGENTS; weightIndex++) {
                Brain weightBrain = new WeightBrain3(board.getWidth(), board.getHeight(), weights[weightIndex]);
                //Brain weightBrain = new LameBrain();
                for (int runIndex = 0; runIndex < NUM_RUNS_PER_AGENT; runIndex++) {
                    results[weightIndex][runIndex] = getNumBlocksPlayed(weightBrain);
                    maximumResult = Math.max(maximumResult, results[weightIndex][runIndex]);
                }

                Arrays.sort(results[weightIndex]);

//                for (int j = 0; j < NUM_RUNS_PER_AGENT; j++) {
//                    averages[weightIndex] += results[weightIndex][j];
//                }
//                averages[weightIndex] /= NUM_RUNS_PER_AGENT;
                medians[weightIndex] = results[weightIndex][NUM_RUNS_PER_AGENT / 2];
            }

            // find the indices of top brains by median
            numTopBrains = 10;
            int[] bestBrainIndices = new int[numTopBrains];
            for (int i = 0; i < numTopBrains && i < NUM_AGENTS; i++) {
                bestBrainIndices[i] = i;
            }
            for (int i = numTopBrains; i < NUM_AGENTS; i++) {
                for (int j = numTopBrains - 1; j >= 0; j--) {
                    if (medians[i] <= bestBrainIndices[j]) {
                        // set it to the previous one, and push those back
                        int currentIndex = i;
                        for (int k = j + 1; k < numTopBrains; k++) {
                            int temp = bestBrainIndices[k];
                            bestBrainIndices[k] = currentIndex;
                            currentIndex = temp;
                        }
                        break;
                    } else if (j == 0) {
                        int currentIndex = i;
                        for (int k = j; k < numTopBrains; k++) {
                            int temp = bestBrainIndices[k];
                            bestBrainIndices[k] = currentIndex;
                            currentIndex = temp;
                        }
                    }
                }
            }
            weights = generateNewWeightsFromBestOldOnes(weights, bestBrainIndices);

            printWeights();
            System.out.println(numGenerations + 1 +" complete");
        }

        System.out.println("Done.");
        System.out.println(maximumResult);
    }

    private void printWeights() throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(FILE_NAME));
        for (int i = 0; i < NUM_AGENTS; i++) {
            for (int j = 0; j < numMetrics; j++) {
                out.print(weights[i][j] + " ");
            }
            out.println();
        }
        out.flush();
        out.close();
    }

    private double[][] generateNewWeightsFromBestOldOnes(double[][] weights, int[] bestBrainIndices) {
        double[][] updatedWeights = new double[NUM_AGENTS][numMetrics];
        // copy over the current best brains
        int i = 0;
        for (; i < numTopBrains; i++) {
            updatedWeights[i] = weights[bestBrainIndices[i]];
        }

        // loop over every pair
        for (int j = 0; j < numTopBrains; j++) {
            for (int k = 0; k < numTopBrains; k++) {
                if (j != k && i < NUM_AGENTS) {
                    updatedWeights[i++] = reproductionCrossover(weights, j, k);
                }
            }
        }

        for (; i < NUM_AGENTS; i++) {
            // note: indices can be same (design decision)
            int firstIndex = (int)(Math.random() * numTopBrains);
            int secondIndex = (int)(Math.random() * numTopBrains);
            updatedWeights[i] = reproductionCrossover(weights, firstIndex, secondIndex);
        }

        return updatedWeights;
    }

    private double[] reproductionCrossover(double[][] weights, int indexOne, int indexTwo) {
        // create a crossover between the weights of indexOne and indexTwo, with added randomness for a mutation
        double[] reproducedWeights = new double[numMetrics];
        double chooseOneProb = 0.75;
        for (int i = 0; i < numMetrics; i++) {
            if (Math.random() < 0.1) {
                // with a 10% chance, set it to a random variable
                reproducedWeights[i] = Math.random();
            } else {
                // with a chooseOneProb chance, choose the weight of index one
                if (Math.random() < chooseOneProb) {
                    reproducedWeights[i] = weights[indexOne][i];
                } else {
                    reproducedWeights[i] = weights[indexTwo][i];
                }
            }
        }
        return reproducedWeights;
    }

    private double[][] readFromFile() throws IOException {
        double[][] weights = new double[NUM_AGENTS][numMetrics];
        BufferedReader in = new BufferedReader(new FileReader(FILE_NAME));
        for (int i = 0; i < NUM_AGENTS; i++) {
            StringTokenizer st = new StringTokenizer(in.readLine());
            for (int j = 0; j < numMetrics; j++) {
                weights[i][j] = Double.parseDouble(st.nextToken());
            }
        }
        return weights;
    }

    public static void main(String[] args) {
        try {
            JBrainTraining3 self = new JBrainTraining3();
            createGUI(self);
            self.train();
//            System.out.println(self.getNumBlocksPlayed(new WeightBrain(board.getWidth(), board.getHeight(), weights[weightIndex])));
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }
    JBrainTraining3() throws IOException {
        numMetrics = NUM_PIECE_TYPES + 1 + board.getWidth() + 2 + board.getWidth() - 1 + board.getHeight();

        weights = new double[NUM_AGENTS][numMetrics];

        File f = new File(FILE_NAME);
        if (f.exists()) {
            // read from the file
            weights = readFromFile();
        } else {
            // generate random weights
            weights = makeRandomStartingWeights();
        }

        timer = new javax.swing.Timer(DELAY, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tick(currentBrain.nextMove(board));
            }
        });
    }
}
