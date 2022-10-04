package assignment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JTetrisBrain extends JTetris {
    private Brain brain;

    public static void main(String[] args) {
        createGUI(new JTetrisBrain());
    }
    JTetrisBrain() {
//        super();
        brain = new SmartBrain();
        timer = new javax.swing.Timer(DELAY, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tick(brain.nextMove(board));
                tick(Board.Action.DOWN);
            }
        });
    }
}
