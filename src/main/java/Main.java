import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Random;
import java.util.concurrent.*;
import javafx.scene.text.Text;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class Main extends Application {
	private static String[] gameBoard = {"b", "b", "b", "b", "b", "b", "b", "b", "b"};
	private static ArrayList<Text> textBoard = new ArrayList<Text>();
	private static GridPane gridBoard;
	private MenuBar menuBar;
    private Menu gameMenu;
    private MenuItem newGameOption;
    private BorderPane root;
    
    
	public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
    	root = new BorderPane();
    	
    	gridBoard = new GridPane();
    	gridBoard.setAlignment(Pos.CENTER);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                Text text = new Text(gameBoard[row*3+col]);   
                textBoard.add(text);
                gridBoard.add(text, col, row);
            }
        }
        
        root.setCenter(gridBoard);
        
        
        menuBar = new MenuBar();
        gameMenu = new Menu("Options");
        newGameOption = new MenuItem("Restart Game");

        gameMenu.getItems().add(newGameOption);
        menuBar.getMenus().add(gameMenu);
        newGameOption.setOnAction(e -> {
        	restartGame();
        });
        
        root.setTop(menuBar);

        Scene scene = new Scene(root);
        primaryStage.setTitle("AI TicTacToe");
        primaryStage.setScene(scene);
        
        
        ExecutorService ex = Executors.newFixedThreadPool(5);
        ex.submit(new MainLoopThread());

        primaryStage.show();
    
    }
    
    private void restartGame() {
    	 for(int iter = 0; iter < 9; iter++) {
    		 gameBoard[iter] = "b";
    		 textBoard.get(iter).setText("b");
    	 }
    	 ExecutorService ex = Executors.newFixedThreadPool(5);
         ex.submit(new MainLoopThread());
    }
    
    private void finishGame (String winner) {
    	System.out.println("test");
    	
        Alert gameFinishMsg = new Alert(AlertType.INFORMATION, "", new ButtonType("Start New Game"));

        gameFinishMsg.setTitle("Game Over");
        gameFinishMsg.setHeaderText(null);
        
        if (winner == "") 
        	gameFinishMsg.setContentText("Draw!");
        else 
        	gameFinishMsg.setContentText("Computer " + winner + " won!");
        
        gameFinishMsg.setOnHidden(e -> {
        	gameFinishMsg.close();
            restartGame();
        });
        
        System.out.println("test2");
    	
        
        gameFinishMsg.show();
    }

    class MainLoopThread implements Callable<Integer>{
		
		@Override
		public Integer call() throws Exception {
	
	    	ExecutorService ex = Executors.newFixedThreadPool(5);
			
			String turn = "X";
			
			for(int iter = 0; iter < 9; iter++) { // total 9 turns
				AI_MinMax newAIPrediction = new AI_MinMax(gameBoard);
				ArrayList<Integer> bestMovesList = newAIPrediction.printBestMoves(false);
	
				Future<Integer> future = ex.submit(new MakeTurnThread(gameBoard, turn, bestMovesList));
			
				try {
					Integer index = future.get(); // result of promise, index to set character  on
					gameBoard[index] = turn; // sets x or o depending on term
					textBoard.get(index).setText(turn);
					
					// print updated gameboard
					System.out.println(gameBoard[0]+ " "+gameBoard[1]+" "+gameBoard[2]);
					System.out.println(gameBoard[3]+ " "+gameBoard[4]+" "+gameBoard[5]);
					System.out.println(gameBoard[6]+ " "+gameBoard[7]+" "+gameBoard[8]);
				
				} 
				catch(Exception e){System.out.println(e.getMessage());}
				
				if(turn == "X") {
					turn = "O";
				}
				else {
					turn = "X";
				}
				
				if (iter == 8) {
					finishGame(turn);
				}
			}

			ex.shutdown();
			
			
			
			return 0;
		}
	
    }
}





class MakeTurnThread implements Callable<Integer>{

	String[] board;
	ArrayList<Integer> movesList = new ArrayList<Integer>();
	String move;
	
	MakeTurnThread(String[] game, String move, ArrayList<Integer> moveslist){
		this.board = game; // board
		this.move = move; // x or o
		this.movesList = moveslist;
	}
	
	@Override
	public Integer call() throws Exception {
		Random r = new Random(); 
		Integer move =  movesList.get(r.nextInt(movesList.size()));
		
		Thread.sleep(1000);
		System.out.println("\n" + "player: " + move + " chooses index: "+move);
		return move-1; //index of move
	}
	
}