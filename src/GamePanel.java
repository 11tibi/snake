import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static private final int SCREEN_WIDTH = 600;
    static private final int SCREEN_HEIGHT = 600;
    static private final int UNIT_SIZE = 25;
    static private final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE*UNIT_SIZE);
    static private final int DELAY = 80;
    final private int[] x = new int[GAME_UNITS];
    final private int[] y = new int[GAME_UNITS];
    private int bodyParts = 6;
    private int appleeEaten = 0;
    private int appleX;
    private int appleY;
    private char direction = 'R';
    private boolean running = false;
    private Timer timer;
    private final Random random;

    public GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    private void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        if(running) {
            g.setColor(Color.RED);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.CYAN);
                } else {
                    g.setColor(Color.GREEN);
                }
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }

        } else {
            gameOver(g);
        }
    }

    private void newApple() {
        appleX = random.nextInt(SCREEN_WIDTH/UNIT_SIZE) * UNIT_SIZE;
        appleY = random.nextInt(SCREEN_HEIGHT/UNIT_SIZE) * UNIT_SIZE;
    }

    private void move() {
        for(int i = bodyParts; i > 0; i--){
            x[i] = x[i-1];
            y[i] = y[i-1];
        }
        switch (direction) {
            case 'U' -> y[0] = y[0] - UNIT_SIZE;
            case 'D' -> y[0] = y[0] + UNIT_SIZE;
            case 'L' -> x[0] = x[0] - UNIT_SIZE;
            case 'R' -> x[0] = x[0] + UNIT_SIZE;
        }
    }

    private void checkApple() {
        if(appleX == x[0] && appleY == y[0]){
            appleeEaten ++;
            bodyParts ++;
            newApple();
        }
    }

    private void checkCollisions() {
        for(int i = bodyParts; i > 0; i--){
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
                break;
            }
        }
        if(x[0] < 0){
            running = false;
        }
        if(x[0] > SCREEN_WIDTH){
            running = false;
        }
        if(y[0] < 0){
            running = false;
        }
        if(y[0] > SCREEN_HEIGHT){
            running = false;
        }
        if(!running){
            timer.stop();
        }
    }

    private void playAgain(){
        for(int i = 0; i<bodyParts; i++){
            x[i] = 0;
            y[i] = 0;
        }
        bodyParts = 6;
        appleeEaten = 0;
        timer.setDelay(DELAY);
        direction = 'R';
        running = false;
        startGame();
        newApple();
    }

    private void gameOver(Graphics g) {
        timer.stop();
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Consolas", Font.PLAIN,75));
        FontMetrics metrics = getFontMetrics(g.getFont());

        g.drawString("Score:" + appleeEaten, (SCREEN_WIDTH - metrics.stringWidth("Score:" + appleeEaten)) / 2, 75);
        g.drawString("Game Over", (SCREEN_WIDTH - metrics.stringWidth("Game Over")) / 2, SCREEN_HEIGHT/2);

        g.setFont(new Font("Consolas", Font.PLAIN,25));
        metrics = getFontMetrics(g.getFont());
        g.drawString("Press enter to play again", (SCREEN_WIDTH - metrics.stringWidth("Press enter to play again")) / 2, SCREEN_HEIGHT-SCREEN_HEIGHT/4);
    }

    private class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e){
            switch (e.getKeyCode()){
                case KeyEvent.VK_LEFT:
                    if (direction != 'R'){
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L'){
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D'){
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U'){
                        direction = 'D';
                    }
                    break;
                case KeyEvent.VK_ENTER:
                    if (!running){
                        playAgain();
                    }
            }
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if(running){
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }
}
