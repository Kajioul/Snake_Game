import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {
    // height and width of the window
    private final int B_WIDTH = 600;
    private final int B_HEIGHT = 600;
    private final int DOT_SIZE = 10;
    private final int ALL_DOTS = 900;
    private final int RAND_POS = 29;
    private final int DELAY = 140;
    private final int x[] = new int[ALL_DOTS];
    private final int y[] = new int[ALL_DOTS];
    private int dots;
    private int apple_x;
    private int apple_y;
    private String highscore= "";
    private int score = 0;
    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    // Check to see if the game is running
    private boolean inGame = true;
    // Timer used to record tick times
    private Timer timer;
    private Image ball;
    private Image apple;
    private Image head;

    public Board() {
        
        initBoard();
    }
    
    private void initBoard() {
        addKeyListener(new TAdapter());
        setBackground(Color.black);
        setFocusable(true);
        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        loadImages();
        initGame();
    }

    private void loadImages() {
        //insert the image
        ImageIcon iid = new ImageIcon("src/dot.png");
        ball = iid.getImage();
        ImageIcon iia = new ImageIcon("src/apple.png");
        apple = iia.getImage();
        ImageIcon iih = new ImageIcon("src/head.png");
        head = iih.getImage();
    }

    private void initGame() {
        dots = 2;
        for (int z = 0; z < dots; z++) {
            x[z] = 50 - z * 10;
            y[z] = 50;
        }
       
        locateApple();
        timer = new Timer(DELAY, this);
        timer.start();
    }
    // Used to paint our components to the screen
    @Override
    public void paintComponent(Graphics g) {
    	if(highscore.equals(""))
    	{
    		highscore = this.GetHighScore();
    	}
        super.paintComponent(g);
        doDrawing(g);
        DrawScore(g);
    }
    //// Draw our Score & Highscore
    public void DrawScore(Graphics g)
    {
    	  g.setColor(Color.white);
          g.setFont(new Font("Int Free",Font.BOLD,25));
          FontMetrics metrics = getFontMetrics(g.getFont());
          //show score top of the screen
          g.drawString("Score:"+dots, (B_WIDTH-metrics.stringWidth("Score:"+dots))/2, g.getFont().getSize());
          //show high score
    	 g.drawString("HighScore:" +highscore, 0, B_HEIGHT-5);
    }
    //cheak if the score is grater then heighscore
    public void CheckScore()
    {
    	System.out.println(highscore);
    	//format Kajioul/:/200
    	if(dots > Integer.parseInt((highscore.split(":")[1])));
    	{
    		//user has set a new record
    		String name = JOptionPane.showInputDialog("You set a new highscore. What is your name?");
    		highscore = name + ":" + dots;
    		File scoreFile = new File ("highscore.dat");
    		if(!scoreFile.exists())
    		{
    			try {
					scoreFile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	      }
    		FileWriter writeFile = null;
    		BufferedWriter writer = null;
    		try
    		{
    			writeFile = new FileWriter(scoreFile);
    			writer = new BufferedWriter(writeFile);
    			
    			writer.write(this.highscore);
    		}
    		catch(Exception e)
    		{
    			//errors
    		}
    		finally
    		{
    			try
    			{
    				if(writer != null)
    				writer.close();
    			}
    			catch(Exception e)
    			{
    				
    			}
    			
    		}
    		}
				
    }
    // Draw our Snake & Food
    private void doDrawing(Graphics g) {
        // Only draw if the game is running / the snake is alive
        if (inGame) {
            g.drawImage(apple, apple_x, apple_y, this);
            // Draw our snake.
            for (int z = 0; z < dots; z++) {
                if (z == 0) {
                    g.drawImage(head, x[z], y[z], this);
                } else {
                    g.drawImage(ball, x[z], y[z], this);
                }
            }
            // Sync our graphics together
            Toolkit.getDefaultToolkit().sync();
        } else {
            // If we're not alive, then we end our game
            gameOver(g);
        }        
    }

    private void gameOver(Graphics g) {
         // Create a new font instance
        g.setColor(Color.white);
        g.setFont(new Font("Int Free",Font.BOLD,65));
        FontMetrics metrics = getFontMetrics(g.getFont());
        // Create a message telling the player the game is over
        g.drawString("Game Over", (B_WIDTH - metrics.stringWidth("Game Over")) / 2, B_HEIGHT / 2);
      
    }

    private void checkApple() {
        if ((x[0] == apple_x) && (y[0] == apple_y)) {
            dots++;
            locateApple();
        }
    }

    private void move() {
        for (int z = dots; z > 0; z--) {
            x[z] = x[(z - 1)];
            y[z] = y[(z - 1)];
        }
        if (leftDirection) {	
            x[0] -= DOT_SIZE;
        }
        if (rightDirection) {	
            x[0] += DOT_SIZE;
        }
        if (upDirection) {
        	
            y[0] -= DOT_SIZE;
        }

        if (downDirection) {
        	
            y[0] += DOT_SIZE;
        }
    }
    // Used to check collisions with snake's self and board edges
    private void checkCollision() {
         // If the snake hits its' own joints..
        for (int z = dots; z > 0; z--) {
             // Snake cant intersect with itself if it's not larger than 5
            if ((z > 4) && (x[0] == x[z]) && (y[0] == y[z])) {            	
                inGame = false; // then the game ends
            }
        }

        if (y[0] >= B_HEIGHT) {
        	
            inGame = false;
        }

        if (y[0] < 0) {
        	
            inGame = false;
        }

        if (x[0] >= B_WIDTH) {
        	CheckScore();
            inGame = false;
        }

        if (x[0] < 0) {
        	CheckScore();
            inGame = false;
        }
        // If the game has ended, then we can stop our timer
        if (!inGame) {
        	CheckScore();
            timer.stop();
        }
    }

    private void locateApple() {

        int r = (int) (Math.random() * RAND_POS);
        apple_x = ((r * DOT_SIZE));
        r = (int) (Math.random() * RAND_POS);
        apple_y = ((r * DOT_SIZE));
    }
    //high score
    public String GetHighScore()
    {
    	//format: Kajioul:200
    	FileReader readFile = null;
    	BufferedReader reader = null;
    	
    	try
    	{
    		readFile = new FileReader("highscore.dat");
    		reader = new BufferedReader(readFile);
    		return reader.readLine();
    	}
    	catch (Exception e)
    	{
    		return"Nobody:0";
    	}
    	finally
    	{
    		try {
    			if(reader!=null)
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (inGame) {

            checkApple();
            checkCollision();
            move();
        }

        repaint();
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();
            if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_UP) && (!downDirection)) {
                upDirection = true;
                rightDirection = false;               
                leftDirection = false;
            }

            if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
        }
    }
}