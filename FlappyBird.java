import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.image.ImageObserver;
import javax.swing.ImageIcon;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


class Bird extends GameObject {
    private ProxyImage proxyImage; 
    private Tube[] tube; 

   
    public Bird(int x, int y){
        super(x, y);
        if(proxyImage == null) {
            proxyImage = new ProxyImage("bird.png");
        }
        this.image = proxyImage.loadImage().getImage();
        this.width = image.getWidth(null); 
        this.height = image.getHeight(null);
        this.x -= width; // Adjust the x position of the bird
        this.y -= height; // Adjust the y position of the bird
        tube = new Tube[1]; // Create a new array of Tube objects
        tube[0] = new Tube(900, Window.HEIGHT - 60); 
        this.dy = 2; 
    }

    
    public void tick() {
        if(dy < 5) { 
            dy += 2; 
        }
        this.y += dy; 
        tube[0].tick(); 
        checkWindowBorder(); 
    }

    public void jump() {
        if(dy > 0) { 
            dy = 0; 
        }
        dy -= 15; 
    }
    
    // Method used to check if the bird has hit the top or bottom of the screen
    private void checkWindowBorder() { 
        if(this.x > Window.WIDTH) { 
            this.x = Window.WIDTH; 
        }
        if(this.x < 0) { 
            this.x = 0; 
        }
        if(this.y > Window.HEIGHT - 50) { 
            this.y = Window.HEIGHT - 50; 
        }
        if(this.y < 0) { 
            this.y = 0; 
        }
    }

    
    public void render(Graphics2D g, ImageObserver obs) { 
        g.drawImage(image, x, y, obs); 
        tube[0].render(g, obs); 
    }
    
    
    public Rectangle getBounds() {
        return new Rectangle(this.x, this.y, this.width, this.height);
    }
}


class TubeColumn {

    private int base = Window.HEIGHT - 60;

    private List<Tube> tubes;
    private Random random;
    private int points = 0; 
    private int speed = 5; 
    private int changeSpeed = speed; 

    public TubeColumn() { 
        tubes = new ArrayList<>();
        random = new Random();
        initTubes();
    }

    // Method used to create the wall
    private void initTubes() {

        int last = base;
        int randWay = random.nextInt(10);

         
        for (int i = 0; i < 20; i++) {

            Tube tempTube = new Tube(900, last); // Create a new Tube object
            tempTube.setDx(speed); // Set the speed of the wall
            last = tempTube.getY() - tempTube.getHeight(); // Set the position of the wall
            if (i < randWay || i > randWay + 4) { // If the wall is not in the middle of the screen 
                tubes.add(tempTube); // Add the wall to the array of Tube objects
            }

        }

    }

    
    public void tick() { 

        for (int i = 0; i < tubes.size(); i++) {  
            tubes.get(i).tick(); // Get the position of the wall

            if (tubes.get(i).getX() < 0) { 
                tubes.remove(tubes.get(i)); 
            }
        }
        if (tubes.isEmpty()) { 
            this.points += 1; 
            if (changeSpeed == points) {
                this.speed += 1; 
                changeSpeed += 5;
            }
            initTubes(); // Create a new wall
        }

    }

   
    public void render(Graphics2D g, ImageObserver obs) {
        for (int i = 0; i < tubes.size(); i++) { // Loop through the array of Tube objects
            tubes.get(i).render(g, obs); // Draw the wall 
        }

    }


    public List<Tube> getTubes() {
        return tubes;
    }

    public void setTubes(List<Tube> tubes) {
        this.tubes = tubes;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

}

interface IStrategy {
    
    public void controller(Bird bird, KeyEvent kevent);
    public void controllerReleased(Bird bird, KeyEvent kevent);
}

// Controller class is used to control the movement of the bird
class Controller implements IStrategy {

    public void controller(Bird bird, KeyEvent kevent) {
    }

    public void controllerReleased(Bird bird, KeyEvent kevent) {
        if(kevent.getKeyCode() == KeyEvent.VK_SPACE) { 
            bird.jump();
        }
    }
    
}

interface IImage {
    public ImageIcon loadImage();
}


class ProxyImage implements IImage {

    private final String src;
    private RealImage realImage;
    
    public ProxyImage(String src) {
        this.src = src;
    }
    
    public ImageIcon loadImage() {
        if(realImage == null) {  
            this.realImage = new RealImage(src); 
        }
        
        return this.realImage.loadImage(); 
    }
    
}

class RealImage implements IImage {

    private final String src;
    private ImageIcon imageIcon;
    
    public RealImage(String src) {
        this.src = src;
    }
    @Override
    public ImageIcon loadImage() {
        if(imageIcon == null) {
            this.imageIcon = new ImageIcon(getClass().getResource(src));
        }
        return imageIcon;
    }
    
}

abstract class GameObject {
    protected int x, y;
    protected int dx, dy;
    protected int width, height;
    protected Image image;

    public GameObject(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Image getImage() {
        return image;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setImage(Image image) {
        this.image = image;
    }
    
    
    public abstract void tick();
    public abstract void render(Graphics2D g, ImageObserver obs);
}


class Tube extends GameObject {

    private ProxyImage proxyImage;
    public Tube(int x, int y) {
        super(x, y);
        if (proxyImage == null) { // If the image has not been loaded 
            proxyImage = new ProxyImage("TubeBody.png"); // Load the image

        }
        this.image = proxyImage.loadImage().getImage(); // Get the image
        this.width = image.getWidth(null); // set the width of the image
        this.height = image.getHeight(null); // set the height of the image
    }

    @Override
    public void tick() {
        this.x -= dx;
    }

    @Override
    public void render(Graphics2D g, ImageObserver obs) {
        g.drawImage(image, x, y, obs);

    }

    
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}


class Game extends JPanel implements ActionListener {

    private boolean isRunning = false; // Variable used to check if the game is running
    private ProxyImage proxyImage; // Variable used to load the image
    private Image background; // Variable used to store the image
    private Bird bird; // Variable used to store the bird object
    private TubeColumn tubeColumn; // Variable used to store the wall object
    private int score; 
    private int highScore; 

    public Game() {

        proxyImage = new ProxyImage("background.jpg"); // Load the image
        background = proxyImage.loadImage().getImage(); // Get the image
        setFocusable(true); 
        setDoubleBuffered(false);     
        addKeyListener(new GameKeyAdapter());
        Timer timer = new Timer(15, this); 
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        Toolkit.getDefaultToolkit().sync(); // Synchronize the display on some systems
        if (isRunning) { 
            bird.tick(); // Update the bird
            tubeColumn.tick(); // Update the wall
            checkColision(); // Check if the bird has collided with the wall
            score++; // Increase the score by 1
        }

        repaint(); 
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(background, 0, 0, null);
        if (isRunning) {
            this.bird.render(g2, this);
            this.tubeColumn.render(g2, this);
            g2.setColor(Color.black);
            g.setFont(new Font("MV Boli", 1, 30));
            g2.drawString("Current score: " + this.tubeColumn.getPoints(), 10, 50);
            
        } else {
            g2.setColor(Color.black);
             g.setFont(new Font("MV Boli", 1, 50));
            g2.drawString("Press Enter to Start Game", Window.WIDTH / 2 - 350, Window.HEIGHT / 2);
            g2.setColor(Color.black);
            g.setFont(new Font("MV Boli", 1, 15));
        }
        g2.setColor(Color.black);
        g.setFont(new Font("MV Boli", 1, 30));
        g2.drawString("High Score: " + highScore, Window.WIDTH - 230, 50);

        g.dispose();
    }

    private void restartGame() {
        if (!isRunning) {
            this.isRunning = true;
            this.bird = new Bird(Window.WIDTH / 2, Window.HEIGHT / 2); // Create the bird object in the middle of the screen 
            this.tubeColumn = new TubeColumn(); // Create the wall object
        }
    }

    private void endGame() {
        this.isRunning = false;
        if (this.tubeColumn.getPoints() > highScore) { // If the current score is higher than the high score
            this.highScore = this.tubeColumn.getPoints(); // Set the high score to the current score
        }
        this.tubeColumn.setPoints(0); // Set the current score to 0

    }

    private void checkColision() {
        Rectangle rectBird = this.bird.getBounds(); // Get the bounds of the bird
        Rectangle rectTube; // Create a variable to store the bounds of the wall

        for (int i = 0; i < this.tubeColumn.getTubes().size(); i++) { // Loop through all the walls
            Tube tempTube = this.tubeColumn.getTubes().get(i); // Get the current wall
            rectTube = tempTube.getBounds(); // Get the bounds of the current wall
            if (rectBird.intersects(rectTube)) { // If the bird has collided with the wall
                endGame(); // End the game
            }
        }
    }

   
    class GameKeyAdapter extends KeyAdapter {

        private final Controller controller;

        public GameKeyAdapter() {
            controller = new Controller();
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                restartGame();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (isRunning) {
                controller.controllerReleased(bird, e);
            }
        }
    }
}

class Window {
    public static int WIDTH = 900;
    public static int HEIGHT = 600;
    
    public Window(int width, int height, String title, Game game) {
        JFrame frame = new JFrame();
        frame.add(game);
        frame.setTitle(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMaximumSize(new Dimension(width, height));
        frame.setPreferredSize(new Dimension(width, height));
        frame.setMinimumSize(new Dimension(width, height));     
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}

public class FlappyBird {
    public static void main(String[] args) {
        Game game = new Game();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> {
            Window window = new Window(Window.WIDTH, Window.HEIGHT, "Flappy Bird", game);
        });
    }
}
