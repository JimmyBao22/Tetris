package assignment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.StringTokenizer;

public class JBrainTraining extends JTetris {
    private static Brain currentBrain;

    private int getNumBlocksPlayed(Brain b) throws InterruptedException{
        stopGame();
        currentBrain = b;
        startGame();
        while (board.getMaxHeight() <= HEIGHT) {
            // System.out.println("not done yet: " + board.getRowsCleared());
            Thread.sleep(50);
        }
        // now the game is stopped
        return board.getRowsCleared();
    }

    private static final int NUM_AGENTS = 20;
    private static final int NUM_RUNS_PER_AGENT = 5;
    private static final int NUM_GENERATIONS = 10;
    private static final int NUM_PIECE_TYPES = Piece.PieceType.values().length;
    private int numTopBrains;
    private static int numMetrics;
    private double[][] weights;
    private static final String FILE_NAME = "weights.txt";

    private double[][] makeRandomStartingWeights() {
        double[][] randomWeights = new double[NUM_AGENTS][numMetrics];
        for (int i = 0; i < randomWeights.length; i++) {
            for (int j = 0; j < randomWeights[i].length; j++) {
                // random value between -1 and 1
                randomWeights[i][j] = Math.random();
            }
        }
        return randomWeights;
    }

    private void train() throws InterruptedException, IOException {
        int maximumResult = 0;
        for (int numGenerations = 0; numGenerations < NUM_GENERATIONS; numGenerations++) {
            int[][] results = new int[NUM_AGENTS][NUM_RUNS_PER_AGENT];
            double[] averages = new double[NUM_AGENTS];

            for (int weightIndex = 0; weightIndex < NUM_AGENTS; weightIndex++) {
                Brain weightBrain = new WeightBrain(board.getWidth(), board.getHeight(), weights[weightIndex]);
                //Brain weightBrain = new LameBrain();
                for (int runIndex = 0; runIndex < NUM_RUNS_PER_AGENT; runIndex++) {
                    results[weightIndex][runIndex] = getNumBlocksPlayed(weightBrain);
                    maximumResult = Math.max(maximumResult, results[weightIndex][runIndex]);
                }

                Arrays.sort(results[weightIndex]);

                for (int j = 0; j < NUM_RUNS_PER_AGENT; j++) {
                    averages[weightIndex] += results[weightIndex][j];
                }
                averages[weightIndex] /= NUM_RUNS_PER_AGENT;
            }

            // find the indices of top brains by median
            numTopBrains = 5;
            int[] bestBrainIndices = new int[numTopBrains];
            int i = 0;
            for (; i < numTopBrains && i < NUM_AGENTS; i++) {
                bestBrainIndices[i] = i;
            }
            for (; i < NUM_AGENTS; i++) {
                for (int j = numTopBrains - 1; j >= 0; j--) {
                    if (averages[i] <= bestBrainIndices[j]) {
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
        }

        System.out.println("Done.");
        System.out.println(maximumResult);
    }

    private void printWeights() throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter("weights.txt"));
        for (int i = 0; i < NUM_AGENTS; i++) {
            for (int j = 0; j < numMetrics; j++) {
                out.print(weights[i][j] + " ");
            }
            out.println();
        }
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
            int firstIndex = (int)(Math.random() * 5);
            int secondIndex = firstIndex;
            // make sure second index is not first index
            while (secondIndex == firstIndex) {
                secondIndex = (int)(Math.random() * 5);
            }
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
            JBrainTraining self = new JBrainTraining();
            createGUI(self);
            self.train();
//            System.out.println(self.getNumBlocksPlayed(new WeightBrain(board.getWidth(), board.getHeight(), weights[weightIndex])));
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }
    JBrainTraining() throws IOException {
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
