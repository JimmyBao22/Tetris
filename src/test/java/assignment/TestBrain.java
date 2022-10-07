package assignment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class TestBrain extends JTetris {
    private Brain brain;

    public static void main(String[] args) {
        TestBrain self = new TestBrain();
        createGUI(self);
    }

    TestBrain() {
        brain = new WeightBrain3(board.getWidth(), board.getHeight(), null);

//        brain = new LameBrain();

        run();
    }

    @Test
    void run() {
        timer = new javax.swing.Timer(DELAY, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tick(brain.nextMove(board));
                Assertions.assertTrue(Board.Result.SUCCESS.equals(board.getLastResult()) || (Board.Result.PLACE.equals(board.getLastResult())));
            }
        });
    }
}
