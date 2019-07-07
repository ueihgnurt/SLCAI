
package chess;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;

import Training.LR;

public class Main 
{
	private WelcomeWindow welcomeWindow;
	private GameLoaderWindow gameLoaderWindow;
	private GameModeWindow gameModeWindow;
	private SelectPlayerWindow selectPlayerWindow;
	private SelectColourWindow selectColourWindow;
	private ChessWindow chessWindow;
	private Player player, player2;
	private String colour, colour2;
	private GraphicsHandler graphicsHandler;
	private Board board;
	private Movement movement;
	private Game game;
	private static LR lr;
	private int gameMode;
	private JFrame[] windowList;
	
	/**
	 * Creates welcomeWindow, gameLoaderWindow, gameModeWindow,
	 * a new board, chessWindow, and graphics handler object.
	 * 
	 * Launches the welcomeWindow having options new game and load game
	 * */
	
	public Main()
	{
		welcomeWindow = new WelcomeWindow();
		welcomeWindow.setSize(300, 300);
		welcomeWindow.setVisible(true);
		
		gameLoaderWindow = new GameLoaderWindow();
		gameLoaderWindow.setSize(300, 300);
		gameLoaderWindow.setVisible(false);
		
		gameModeWindow = new GameModeWindow();
		gameModeWindow.setSize(300, 300);
		gameModeWindow.setVisible(false);
		
		board = new Board(true);
		movement = board.getMovement();
		board.print();
		
		chessWindow = new ChessWindow();
		chessWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		graphicsHandler= new GraphicsHandler(board, 50, 50, 75, 75, 1);
		chessWindow.add(graphicsHandler);
		
		chessWindow.setSize(800, 800);
		chessWindow.setVisible(false);
		
	}

	public static void main(String args[])
	{
		new Main();
		try {
			lr.Train();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * This method is relevant only in 1 player mode.
	 * Sets the human colour to given colour.
	 * Creates an AI with the opposite colour.
	 * */
	private void setHumanPlayerColour(String colour)
	{
		//System.out.println(colour);
		this.colour = colour;

		this.colour2 = Board.opposite(colour);
		
		try 
		{
			movement = board.getMovement();
			if(gameMode==1)graphicsHandler.setAI(new AI(board, colour2, movement));
			if(gameMode==3) graphicsHandler.setAI(new AI2(board,colour2,movement,lr));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception in setHumanPlayerColour() of Main.");
		}
	}
	
	/**
	 * Finds the next window after this window in the window list, 
	 * Sets this window as invisible, and next window as visible.
	 * */
	public void nextWindow(JFrame window)
	{
		int order=0;
		
		while(windowList[order] != window)
			order++;
		windowList[order].setVisible(false);
		windowList[order+1].setVisible(true);
	}
	
	/**
	 * Informs about the game mode to graphics handler class.
	 * Sets the game's windows' sequence.
	 * 
	 * If game is in single player mode, the sequence will be as follows:
	 * player selection window, human colour selection window, main chess window.
	 * 
	 * If game is in multiplayer mode, the sequence will be as follows:
	 * both player selection window, main chess window.
	 * 
	 * Sets the first of the windows to visible.
	 * All the windows call nextWindow method to enable next window.
	 * */
	public void setWindowList()
	{
		try 
		{
			graphicsHandler.setGameMode(gameMode);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception in setWindowList() of Main"
					+ "while setting game mode.");
		}
		
		if(gameMode == 1)
		{
			selectPlayerWindow = new SelectPlayerWindow(1);
			selectPlayerWindow.setSize(300, 300);
			
			selectColourWindow = new SelectColourWindow();
			selectColourWindow.setSize(300, 300);
			
			JFrame[] arr = {selectPlayerWindow, selectColourWindow, chessWindow};
			windowList = arr;
		}
		else if(gameMode == 3)
		{
			selectPlayerWindow = new SelectPlayerWindow(1);
			selectPlayerWindow.setSize(300, 300);
			
			selectColourWindow = new SelectColourWindow();
			selectColourWindow.setSize(300, 300);
			
			JFrame[] arr = {selectPlayerWindow, selectColourWindow, chessWindow};
			windowList = arr;
		}
		else
		{	
			selectPlayerWindow = new SelectPlayerWindow(2);
			selectPlayerWindow.setSize(300, 300);
	
			JFrame[] arr = {selectPlayerWindow,	chessWindow};
			windowList = arr;
		}	
		windowList[0].setVisible(true);
	}
	
	/**
	 * On closing of the chessWindow, updates the statistics of the player(s),
	 * and saves game statistics.
	 * (If game is new, creates new game object. 
	 * Calls storeMoves() and storeGame() of the game object.)
	 * 
	 * Displays a toolbar containing undo button.	
	 * On clicking undo button, undoes last opponent move and last own move,
	 * And, lets the current player play again.
	 * 
	 * TODO: add more buttons and corresponding features.
	 * 
	 * */
	private class ChessWindow extends JFrame
	{
		private static final long serialVersionUID = 1L;

		public ChessWindow()
		{
			//Setting toolbar of chessWindow:
			JToolBar toolbar = new JToolBar("Menu");
			
			JButton undoButton = new JButton(getImage(20, 20, "undo.png"));
	        undoButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                //JOptionPane.showMessageDialog(ChessWindow.this, "undo clicked");
	                Main.this.movement.undoMove();
	                Main.this.movement.undoMove();
	                //calls undo twice to clear last move of opponent and own.
	                Main.this.graphicsHandler.repaint();
	            }
	        });
	        toolbar.add(undoButton);
	        
	        this.getContentPane().add(toolbar, BorderLayout.PAGE_START);
	        
	        this.addWindowListener(new ClosingHandler());
	    		
		}
		
		private ImageIcon getImage(int height, int width, String path)
		{
			BufferedImage img = null;
			
			try 
			{
				img = ImageIO.read(this.getClass().getResource(path));
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
				System.out.println("Exception while getting "+path+" icon"
						+ " in ChessWindow class.");
			}
			
			Image icon = img.getScaledInstance(height, width, Image.SCALE_SMOOTH);
			//Scale the icon to the given size.
			
			return new ImageIcon(icon);
		}
		private class ClosingHandler extends WindowAdapter
		{
			private BufferedReader reader;

			public void windowClosing(WindowEvent e) 
			{
				if(Main.this.game == null)
				{
					try 
					{
						game = new Game(gameMode, player.toString(), 
								player2.toString(),	colour, colour2, 
								board.getCurrentTurn());
					} 
					catch (Exception e1) 
					{
						e1.printStackTrace();
					}
				}
					
				game.storeMoves(movement);
				game.storeGame();			//store game object into file.
				
				if(board.isCheckMate(colour))
				{	
					player2.won();
					player.lost();
					File file = new File(System.getProperty("user.dir")+ File.separator 
							+ colour + "TrainingData.dat");
					if(!file.exists())
						try {
							file.createNewFile();
						} catch (IOException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
					ArrayList<int[][]> map = graphicsHandler.getmap();
					try {
						reader = new BufferedReader(new FileReader(file));
						while(reader.ready())
						{
							int[][] temp = new int [8][8];
							for (int i =0;i<8;i++)
								{
									for(int j =0;j<8;j++)
									{
										temp[i][j]= reader.read();
									}
								}
							map.add(temp);
						}
						BufferedWriter writer = new BufferedWriter(new FileWriter(file));
						for(int i = 0;i<map.size();i++)
						{
							for(int j =0;j<8;j++)
								for(int k =0;k<8;k++)
									writer.write(map.get(i)[j][k]);
						}
						writer.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				else if(board.isCheckMate(colour2))
				{	
					player.won();
					player2.lost();
					File file = new File(System.getProperty("user.dir")+ File.separator 
							+ colour2 + "TrainingData.dat");
					if(!file.exists())
						try {
							file.createNewFile();
						} catch (IOException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
					ArrayList<int[][]> map = graphicsHandler.getmap();
					try {
						reader = new BufferedReader(new FileReader(file));
						while(reader.ready())
						{
							int[][] temp = new int [8][8];
							for (int i =0;i<8;i++)
								{
									for(int j =0;j<8;j++)
									{
										temp[i][j]= reader.read();
									}
								}
							map.add(temp);
						}
						BufferedWriter writer = new BufferedWriter(new FileWriter(file));
						for(int i = 0;i<map.size();i++)
						{
							for(int j =0;j<8;j++)
								for(int k =0;k<8;k++)
									writer.write(map.get(i)[j][k]);
						}
						writer.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				}
				
				player.gamePlayed();
				player.storePlayer();
			    player2.gamePlayed();
			    player2.storePlayer();
			}
		}
	}
	
	/**
	 * This window has relevance only when game mode is 1 player.
	 * Lets the user select his/her colour. 
	 * Sets the AI colour to opposite of the player colour.
	 * */
	private class SelectColourWindow extends JFrame
	{
		private static final long serialVersionUID = 1L;
		private ButtonGroup colourChoice;
		
		public SelectColourWindow()
		{
			super("Select colour");
			setLayout(new FlowLayout());
			colourChoice = new ButtonGroup();
			JRadioButton whiteButton = new JRadioButton(Board.White);
			JRadioButton blackButton = new JRadioButton(Board.Black);
			add(whiteButton);
			add(blackButton);
			colourChoice.add(whiteButton);
			colourChoice.add(blackButton);
			whiteButton.addItemListener(new ColourHandler(Board.White));
			blackButton.addItemListener(new ColourHandler(Board.Black));
		}
		
		/**
		 * Closes the current window, and calls nextWindow() from main, to
		 * display the next window in the sequence.
		 * */
		public void CloseFrame()
		{
			Main.this.nextWindow(this);
			super.dispose();
		}
		
		/**
		 * As soon as user selects the colour, set the colour for it, and
		 * close this window.
		 * */
		private class ColourHandler implements ItemListener
		{
			private String color;
			
			public ColourHandler(String s)
			{
				color = s;
			}
			
			@Override
			public void itemStateChanged(ItemEvent e) 
			{
				Main.this.setHumanPlayerColour(this.color);
				CloseFrame();
			}
		}
	}
	
	/**
	 * Displays a window to let user(s) select their name(s) from the saved 
	 * players' statistics file.
	 * 
	 * Also supports creation of a new Player (with a new name).
	 * (Two players can't have same name.)
	 * 
	 * If game is single player, shows only one drop down menu for name.
	 * Otherwise shows two drop-down menus for selecting names.
	 * 
	 * Sets the colours of the two players too.
	 * 
	 * Creates a new player iff one with such a name doesn't exist.
	 * */
	private class SelectPlayerWindow extends JFrame
	{
		private static final long serialVersionUID = 1L;
		private JComboBox<Player> selectPlayer, selectPlayer2;
		private int mode;
		private ArrayList<Player> allPlayers;
		
		public SelectPlayerWindow(int mode)
		{
			super("Please select your name");
			setLayout(new FlowLayout());
			
			this.mode = mode;
			
			allPlayers = Player.getPlayersList();
			Player players[] = allPlayers.toArray(new Player[allPlayers.size()]);
			
			selectPlayer = new JComboBox<>(players);
			selectPlayer.setEditable(true);
			add(selectPlayer);
			HandlerClass handler = new HandlerClass();
			selectPlayer.addActionListener(handler);
	
			if(mode == 2)
			{
				JLabel white = new JLabel("White");
				add(white);
				
				selectPlayer2 = new JComboBox<>(players);
				selectPlayer2.setEditable(true);
				add(selectPlayer2);
				selectPlayer2.addActionListener(handler);
				JLabel black = new JLabel("Black");
				add(black);
				
				colour = "White";
				colour2 = "Black";
			}
		}
		
		/**
		 * Closes the current window, and calls nextWindow() from main, to
		 * display the next window in the sequence.
		 * */
		public void CloseFrame()
		{
			Main.this.nextWindow(this);
			super.dispose();
		}
		
		/**
		 * If user enters an already existing name, it loads the player object
		 * from the stored file.
		 * Otherwise creates a new Player with given (typed) name.
		 * 
		 * Validates whether two names are equal in 2 player mode.
		 * 
		 * If both names are valid, creates/sets the required players, and
		 * closes the window.
		 * */
		private class HandlerClass implements ActionListener
		{
			public void actionPerformed(ActionEvent event) 
			{
				String name = selectPlayer.getSelectedItem().toString();
				player = Player.getPlayer(allPlayers, name);
				if(player == null)	//not yet selected.
					return;
				
				if(mode == 2)
				{
					if(selectPlayer2.getSelectedItem() == null)
						return;
					String name2 = selectPlayer2.getSelectedItem().toString();
					if(name2 == null)
						return;
					if(name.equals(name2))
						return;
					//both names must be different.
					
					player2 = Player.getPlayer(allPlayers, name2);
					if(player2 == null)	//not yet selected.
						return;
				}
				else
				{
					player2 = Player.getPlayer(allPlayers, "AI");
				}
				CloseFrame();
			}
		}
	}
	
	/**
	 * Displays a window asking user for game mode, and sets the game mode.
	 * The game has two modes currently : single player and multiplayer.
	 * After setting the game mode successfully, this class calls the 
	 * setWindowList() method of the main class, which continues the flow.
	 * 
	 * In multiplayer mode, both the players play on the same PC using mouse,
	 * alternatively (as per turns).
	 * 
	 * In single player mode, an AI plays with the human player.
	 * */
	private class GameModeWindow extends JFrame
	{
		private static final long serialVersionUID = 1L;
		
		/**
		 * Creates a new welcome window, having buttons for single-player and
		 * multiplayer game mode.
		 * 
		 * Whenever the user clicks the button, the game mode should be set.
		 * */
		
		public GameModeWindow()
		{
			super("Select game mode");
			setLayout(new FlowLayout());
			
			ButtonGroup group = new ButtonGroup();
			//JRadioButton zeroPlayer = new JRadioButton("Zero player mode");
			JRadioButton singlePlayer = new JRadioButton("AI mode");
			JRadioButton multiPlayer = new JRadioButton("Multi player mode");
			JRadioButton singlePlayer2 = new JRadioButton("self learning AI mode");
			//add(zeroPlayer);
			add(singlePlayer);
			add(multiPlayer);
			add(singlePlayer2);
			//group.add(zeroPlayer);
			group.add(singlePlayer);
			group.add(multiPlayer);
			group.add(singlePlayer2);
			//zeroPlayer.addItemListener(new HandlerClass(0));;
			singlePlayer.addItemListener(new HandlerClass(1));
			multiPlayer.addItemListener(new HandlerClass(2));
			singlePlayer2.addItemListener(new HandlerClass(3));
		}
		
		/**
		 * Closes the frame and sets the windows list for the UI.
		 * */
		public void CloseFrame()
		{
			super.dispose();
			Main.this.setWindowList();
		}
		
		/**
		 * Sets the game mode according to the clicked button.
		 * On successful set, immediately closes the frame.
		 * */
		private class HandlerClass implements ItemListener
		{
			private int mode;
			public HandlerClass (int x)
			{
				this.mode = x;
			}
			
			@Override
			public void itemStateChanged(ItemEvent e) 
			{
				Main.this.gameMode = this.mode;
				CloseFrame();
			}
		}
	}
	
	/**
	 * Welcomes the user and gives a choice to either start a new game
	 * or load a previous game.
	 * */
	private class WelcomeWindow extends JFrame
	{
		private static final long serialVersionUID = -735673551329590382L;
		private JRadioButton newGame, loadGame;
		public WelcomeWindow()
		{
			super("Welcome to chess!");
			setLayout(new FlowLayout());
			
			ButtonGroup group = new ButtonGroup();
			newGame = new JRadioButton("New game");
			loadGame = new JRadioButton("Load game");
			add(newGame);
			add(loadGame);
			
			group.add(loadGame);
			group.add(newGame);
			
			loadGame.addItemListener(new HandlerClass());
			newGame.addItemListener(new HandlerClass());
		}
		
		/**
		 * Closes the frame.
		 * */
		public void CloseFrame()
		{
			this.setVisible(false);
			super.dispose();
		}
		
		/**
		 * If player wants a new game, call game mode window.
		 * Otherwise call game loader window.
		 * */
		private class HandlerClass implements ItemListener
		{
			@Override
			public void itemStateChanged(ItemEvent e) 
			{
				if(e.getSource() == newGame)
				{
					Main.this.gameModeWindow.setVisible(true);
				}
				else if(e.getSource() == loadGame)
				{
					Main.this.gameLoaderWindow.setVisible(true);
				}
				else
					return;	//nothing changed.
				CloseFrame();
			}
		}
	}

	/**
	 * If user chooses to load a previous game,
	 * displays all the previously stored games.
	 * (Sorted by time of creating the game object.)
	 * */
	private class GameLoaderWindow extends JFrame
	{
		private static final long serialVersionUID = 1L;
		private JComboBox<Game> selectGame;
		private ArrayList<Game> allGames;
		private ArrayList<Player> allPlayers;
		
		public GameLoaderWindow()
		{
			super("Please choose a game.");
			setLayout(new FlowLayout());
			allPlayers = Player.getPlayersList();
			allGames = Game.getGamesList();
			Game games[] = allGames.toArray(new Game[allGames.size()]);
			selectGame = new JComboBox<>(games);
			selectGame.setEditable(false);
			add(selectGame);
			game = null;
			HandlerClass handler = new HandlerClass();
			selectGame.addActionListener(handler);
		}
		
		/**
		 * Closes the current window, reloads the game, 
		 * makes chessWindow visible, and sets game mode to whatever it was.
		 * If game was one player, sets human colour to what it should be.
		 * */
		public void CloseFrame()
		{
			this.setVisible(false);
			Main.this.movement = Main.this.game.reloadGame(Main.this.board);
			Main.this.chessWindow.setVisible(true);
			try 
			{
				Main.this.graphicsHandler.setGameMode(game.mode);
				if(game.mode == 1)
				{
					if(player2.toString().equals("AI"))
						Main.this.setHumanPlayerColour(game.colour1);
					else
						Main.this.setHumanPlayerColour(game.colour2);
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			super.dispose();
		}
		
		/**
		 * If user selects a game, it loads the game object
		 * from the stored file.
		 * */
		private class HandlerClass implements ActionListener
		{
			public void actionPerformed(ActionEvent event) 
			{
				if(Main.this.game != null)
					return;
				
				Main.this.game = (Game) selectGame.getSelectedItem();
				if(Main.this.game == null)	//not yet selected.
					return;
				Main.this.player = Player.getPlayer(allPlayers, game.player1);
				Main.this.player2 = Player.getPlayer(allPlayers, game.player2);
				Main.this.gameMode = game.mode;
				Main.this.colour = game.colour1;
				Main.this.colour2 = game.colour2;
				
				CloseFrame();
			}
		}
	}
}