package assignment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class JBrainTraining extends JTetris {
    private static Brain currentBrain;

    private int getNumBlocksPlayed(Brain b) throws InterruptedException{
        stopGame();
        currentBrain = b;
        startGame();
        while (board.getMaxHeight() <= HEIGHT) {
            System.out.println("not done yet: " + board.getRowsCleared());
            Thread.sleep(1000);
        }
        // now the game is stopped
        return board.getRowsCleared();
    }

    private static final int NUM_METRICS = 5;
    private static final int NUM_AGENTS = 5;
    private static final int NUM_RUNS_PER_AGENT = 5;
    private static final int NUM_GENERATIONS = 5;

    private double[][] makeRandomStartingWeights(int numAgents) {
        double[][] randomWeights = new double[numAgents][NUM_METRICS];
        for (int i = 0; i < randomWeights.length; i++) {
            for (int j = 0; j < randomWeights[i].length; j++) {
                // random value between -1 and 1
                randomWeights[i][j] = Math.random() * 2 - 1;
            }
        }
    }

    private void train() throws InterruptedException {
        double[][] weights = makeRandomStartingWeights(NUM_AGENTS);
        for (int numGenerations = 0; numGenerations < NUM_GENERATIONS; numGenerations++) {
            int[][] results = new int[NUM_AGENTS][NUM_RUNS_PER_AGENT];
            int[] medians = new int[NUM_AGENTS];

            for (int weightIndex = 0; weightIndex < NUM_AGENTS; weightIndex++) {
                Brain weightBrain = new WeightBrain(weights[weightIndex]); //TODO use jimmys weightedbrain
                for (int runIndex = 0; runIndex < NUM_RUNS_PER_AGENT; runIndex++) {
                    results[weightIndex][runIndex] = getNumBlocksPlayed(weightBrain);
                }

                Arrays.sort(results[weightIndex]);

                medians[weightIndex] = results[weightIndex][results.length / 2];
            }

            int[] bestBrainIndices = // find the indices of top 5 brains by median;
            weights = generateNewWeightsFromBestOldOnes(weights, bestBrainIndices);
        }

        System.out.println(weights);
    }

    public static void main(String[] args) throws InterruptedException {
        JBrainTraining self = new assignment.JBrainTraining();
        createGUI(self);
        self.train();
        System.out.println(self.getNumBlocksPlayed(new LameBrain()));
    }
    JBrainTraining() {
        timer = new javax.swing.Timer(DELAY, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tick(currentBrain.nextMove(board));
            }
        });
    }
}
