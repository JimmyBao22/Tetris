package assignment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Comparator;

public class JBrainTraining extends JTetris {
    private static Brain currentBrain;

    private int getNumBlocksPlayed(Brain b) throws InterruptedException{
        stopGame();
        currentBrain = b;
        startGame();
        while (board.getMaxHeight() <= HEIGHT) {
            // System.out.println("not done yet: " + board.getRowsCleared());
            Thread.sleep(1000);
        }
        // now the game is stopped
        return board.getRowsCleared();
    }

    private static final int NUM_AGENTS = 20;
    private static final int NUM_RUNS_PER_AGENT = 5;
    private static final int NUM_GENERATIONS = 5;
    private static final int NUM_PIECE_TYPES = Piece.PieceType.values().length;
    private int numTopBrains;
    private static int numMetrics;

    private double[][] makeRandomStartingWeights() {
        double[][] randomWeights = new double[NUM_AGENTS][numMetrics];
        for (int i = 0; i < randomWeights.length; i++) {
            for (int j = 0; j < randomWeights[i].length; j++) {
                // random value between -1 and 1
                randomWeights[i][j] = Math.random() * 2 - 1;
            }
        }
        return randomWeights;
    }

    private void train() throws InterruptedException {
        double[][] weights = makeRandomStartingWeights();
        for (int numGenerations = 0; numGenerations < NUM_GENERATIONS; numGenerations++) {
            int[][] results = new int[NUM_AGENTS][NUM_RUNS_PER_AGENT];
            int[] medians = new int[NUM_AGENTS];

            for (int weightIndex = 0; weightIndex < NUM_AGENTS; weightIndex++) {
                Brain weightBrain = new WeightBrain(board.getWidth(), board.getHeight(), weights[weightIndex]);
                for (int runIndex = 0; runIndex < NUM_RUNS_PER_AGENT; runIndex++) {
                    results[weightIndex][runIndex] = getNumBlocksPlayed(weightBrain);
                }

                Arrays.sort(results[weightIndex]);

                medians[weightIndex] = results[weightIndex][results[0].length / 2];
            }

            // find the indices of top brains by median
            numTopBrains = 5;
            assert(numTopBrains <= NUM_AGENTS);
            int[] bestBrainIndices = new int[numTopBrains];
            Arrays.fill(bestBrainIndices, -Integer.MAX_VALUE);
            for (int i = 0; i < NUM_AGENTS; i++) {
                for (int j = numTopBrains - 1; j >= 0; j--) {
                    if (medians[i] <= bestBrainIndices[j] || j == 0) {
                        // set it to the previous one, and push those back
                        int currentIndex = i;
                        for (int k = j + 1; k < numTopBrains; k++) {
                            int temp = bestBrainIndices[k];
                            bestBrainIndices[k] = currentIndex;
                            currentIndex = temp;
                        }
                    }
                }
            }
            weights = generateNewWeightsFromBestOldOnes(weights, bestBrainIndices);
        }

        System.out.println(weights);
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
                reproducedWeights[i] = Math.random() * 2 - 1;
            } else {
                // with a chooseOneProb chance, choose the weight of index one
                if (Math.random() < 0.75) {
                    reproducedWeights[i] = weights[indexOne][i];
                } else {
                    reproducedWeights[i] = weights[indexTwo][i];
                }
            }
        }
        return reproducedWeights;
    }

    public static void main(String[] args) {
        JBrainTraining self = new assignment.JBrainTraining();
        createGUI(self);
        try {
            self.train();
//            System.out.println(self.getNumBlocksPlayed(new WeightBrain(board.getWidth(), board.getHeight(), weights[weightIndex])));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    JBrainTraining() {
        numMetrics = NUM_PIECE_TYPES + 1 + board.getWidth() + 2 + board.getHeight() + board.getHeight();
        timer = new javax.swing.Timer(DELAY, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tick(currentBrain.nextMove(board));
            }
        });
    }
}
