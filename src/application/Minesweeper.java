package application;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane; 
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.BorderPane;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import java.util.ArrayList;
import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public class Minesweeper extends Application {
	
	private int SIZE = 8;
	private int DEACTIVATION = 6;
	private final int HEADQUARTERS = 2;
	private EventHandler<MouseEvent> buttonOpenListener;
	private EventHandler<ActionEvent> buttonResetListener;
	private EventHandler<ActionEvent> buttonBeginnerListener;
	private EventHandler<ActionEvent> buttonIntermediateListener;
	ArrayList<MineTile> mineList;
	ArrayList<MineTile> mineListHeadquarters;
	ArrayList<MineTile> headquartersList; 
	ArrayList<MineTile> headquartersOpenList;
	MineTile[][] board = new MineTile[SIZE][SIZE];
	Number vboxWidth = 0;
	
	Label textLabel = new Label();
	Label openedLabel = new Label();
	Label minesLabel = new Label();
	Label headquartersLabel = new Label();
	Label timerLabel = new Label();
	
	Label combinationLabel = new Label();
	Label timerDeactivationLabel = new Label();
	
	Button resetButton;
	Button beginnerButton;
	Button intermediateButton;
	
	Random random = new Random();

    int tilesClicked = 0; 
    boolean gameOver = false;
    boolean timerStarted = false; 
    int remainingHeadquarters = HEADQUARTERS;
    
    int mineCount = 10;
    int flagCount = 10;
    
    Timeline timeline;
    Timeline timelineDeactivation;
    int timeElapsed = 0;
    int timeElapsedDeactivation = 6;
    
    int openedCellsCounter = 0;
    
    private String generatedSequence;
    
    private StringBuilder userInputSequence = new StringBuilder();
    
    boolean isDeactivationStarted = false;
    
    MineTile sharedBtn;
	
	private class MineTile extends Button {
        int row;
        int col;

        public MineTile(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }
	
	@Override
	public void start(Stage stage) {
		
		initListener();
		
		GridPane grid = new GridPane();
		
		for (int row = 0; row < SIZE; row++) {
			for (int col = 0; col < SIZE; col++) {
				MineTile btn = new MineTile(row, col);
				board[row][col] = btn;
				btn.setPrefSize(40, 40);
				btn.addEventHandler(MouseEvent.MOUSE_CLICKED, buttonOpenListener);
				grid.add(btn, col, row);
			}
		}
		
		setMines();
		setHeadquarters();
		
		BorderPane root = new BorderPane();
		root.setCenter(grid);
		
		textLabel.setText("In progress");
		
		timerLabel.setText("0 seconds");
		
		VBox statusBox = new VBox(2);
		statusBox.getChildren().addAll(textLabel, timerLabel);
		
		minesLabel.setText(Integer.toString(flagCount) + " mines left");
		
		openedLabel.setText("0 cells opened");
		
		headquartersLabel.setText(remainingHeadquarters + " headquarters left");
		
		combinationLabel.setText("");
		timerDeactivationLabel.setText("");
		
		VBox data = new VBox(2);
		data.getChildren().addAll(minesLabel, headquartersLabel, openedLabel);
		
		Region spacerH = new Region();
		Region spacerH2 = new Region();
		Region spacerV1 = new Region();
		Region spacerV2 = new Region();
		Region spacerV3 = new Region();
		Region spacerV4 = new Region();
		VBox.setVgrow(spacerV1, Priority.ALWAYS);
		VBox.setVgrow(spacerV2, Priority.ALWAYS);
		VBox.setVgrow(spacerV3, Priority.ALWAYS);
		VBox.setVgrow(spacerV4, Priority.ALWAYS);
		HBox.setHgrow(spacerH, Priority.ALWAYS);
		HBox.setHgrow(spacerH2, Priority.ALWAYS);
		
		resetButton = new Button("Reset");
		resetButton.setOnAction(buttonResetListener);
		
		beginnerButton = new Button("Beginner");
		beginnerButton.setOnAction(buttonBeginnerListener);
		
		intermediateButton = new Button("Intermediate");
		intermediateButton.setOnAction(buttonIntermediateListener);
		
		HBox buttons = new HBox(10);
		buttons.getChildren().addAll(beginnerButton, intermediateButton);
		
		VBox vbox = new VBox(2);
		vbox.getChildren().addAll(statusBox, spacerV1, data, spacerV2, resetButton);
		vbox.setPadding(new Insets(10));
		vbox.widthProperty().addListener((observable, oldValue, newValue) -> {
			vboxWidth = newValue;
		    System.out.println("New VBox Width: " + newValue);
		});
		
		HBox deactivation = new HBox();
		deactivation.getChildren().addAll(combinationLabel, spacerH2, timerDeactivationLabel);
		
		VBox level = new VBox(10);
		level.getChildren().addAll(root, spacerV3, deactivation, spacerV4, buttons);
		level.setPadding(new Insets(10));
		
		HBox hbox = new HBox(2);
		hbox.getChildren().addAll(vbox, spacerH, level);

		Scene scene = new Scene(hbox, 500, 430);
		scene.setOnKeyPressed(event -> handleKeyPress(event));
		stage.setTitle("Minesweeper");
		stage.setScene(scene);
		stage.show();
		
		timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeElapsed++;
            timerLabel.setText(timeElapsed + " seconds");
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
	}
	
	public static void main(String[] args) { 
		launch();
	} 
	
	public void initListener()
	{
		buttonResetListener = new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event) 
			{
				resetGame();  
			}
		};
		
		buttonBeginnerListener = new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event) 
			{
				System.out.println("Beginner level selected");
			}
		};
		
		buttonIntermediateListener = new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event) 
			{
				System.out.println("Intermediate level selected");
			}
		};
		
	    buttonOpenListener = new EventHandler<MouseEvent>() 
	    {
	        @Override
	        public void handle(MouseEvent event) 
	        {
	        	if (gameOver) {
                    return;
                }
	        	
	        	MineTile btn = (MineTile) event.getSource();
	        	
	            if (event.getButton() == MouseButton.PRIMARY) 
	            {
	            	if (btn.getText().isEmpty() || btn.getText().equals("💥")) 
	            	{
	            		if (!timerStarted) {
	            			timerStarted = true;
	            			timeline.playFromStart();
	            		}
	            			
	            		if (mineList.contains(btn)) 
	            		{
		                	revealMines();
		                }
		                else 
		                {
		                	checkMine(btn.row, btn.col);
		                	openedCellsCounter++;
		                }
	            		
	            		if (headquartersList.contains(btn)) 
	            		{
	            			revealHeadquarter(btn.row, btn.col);
	            			openedCellsCounter++;
	            		}
	            	}
	            	else {
	            		System.out.println("Not empty");
	            	}
	            }
	            else if (event.getButton() == MouseButton.SECONDARY) 
	            {
	            	if (btn.getText().isEmpty() && !btn.isDisable()) {
	            		btn.setText("🚩");
	            		btn.setDisable(false);
	            		flagCount -= 1;
	            		minesLabel.setText(flagCount + " mines left");
	            		
	            		if (headquartersList.contains(btn)) {
	            			gameOver = true;
	            	        textLabel.setText("Game Over!");
	            	        timeline.stop(); 
	            		}
	            	}
	            	else if (btn.getText().equals("🚩")) {
	            		btn.setText("");
	            		btn.setDisable(false);
	            		flagCount += 1;
	            		minesLabel.setText(flagCount + " mines left");
	            	}
	            }
	        }
	    };
	}
	
	void setMines() 
	{
		mineList = new ArrayList<MineTile>();
		mineListHeadquarters = new ArrayList<MineTile>();
		
//		mineList.add(board[6][0]);
//		mineList.add(board[6][2]);
//		mineList.add(board[6][4]);
//		mineList.add(board[6][6]);
//		mineList.add(board[7][1]);
//		mineList.add(board[7][3]);
//		mineList.add(board[7][5]);
//		mineList.add(board[7][7]);
//		mineList.add(board[7][6]);
//		mineList.add(board[6][7]);
		
		int mineLeft = mineCount;
        while (mineLeft > 0) {
            int row = random.nextInt(SIZE); 
            int col = random.nextInt(SIZE);

            MineTile btn = board[row][col]; 
            if (!mineList.contains(btn)) {
                mineList.add(btn);
                System.out.println("Mine (" + row + "; " + col + ")");
                mineLeft -= 1;
            }
        }	 
    }
	
	void setHeadquarters() {
		headquartersList = new ArrayList<>();
		headquartersOpenList = new ArrayList<>();
		
//		headquartersList.add(board[1][1]);
//		headquartersList.add(board[0][0]);
		
		int headquarterLeft = HEADQUARTERS;
		while (headquarterLeft > 0) {
			 int row = random.nextInt(SIZE);
			 int col = random.nextInt(SIZE);
			 
			 MineTile btn = board[row][col];
			 if (!mineList.contains(btn) && !isNearMine(row, col) && !headquartersList.contains(btn) && !isNearHeadquarter(row, col)) {
	                headquartersList.add(btn);
	                headquarterLeft -= 1;
	                System.out.println("Headquarter (" + row + "; " + col + ")");
	         }
		}
	}
	
	 boolean isNearMine(int row, int col) {
		 
		 int minesFound = 0;
			
		 minesFound += countMine(row-1, col-1);
		 minesFound += countMine(row-1, col); 
	 	 minesFound += countMine(row-1, col+1);
		
		 minesFound += countMine(row, col-1);
		 minesFound += countMine(row, col+1);
		
		 minesFound += countMine(row+1, col-1);
		 minesFound += countMine(row+1, col); 
		 minesFound += countMine(row+1, col+1);
		 
		 if (minesFound > 0)
			 return true;
		 
		 return false;
    }
	 
	 boolean isNearHeadquarter(int row, int col) {
		 
		 int headquartersFound = 0;
			
		 headquartersFound += countHeadquarters(row-1, col-1);
		 headquartersFound += countHeadquarters(row-1, col); 
		 headquartersFound += countHeadquarters(row-1, col+1);
		
		 headquartersFound += countHeadquarters(row, col-1);
		 headquartersFound += countHeadquarters(row, col+1);
		
		 headquartersFound += countHeadquarters(row+1, col-1);
		 headquartersFound += countHeadquarters(row+1, col); 
		 headquartersFound += countHeadquarters(row+1, col+1);
		 
		 if (headquartersFound > 0)
			 return true;
		 
		 return false;
    }
	
	void revealMines() 
	{
        for (int i = 0; i < mineList.size(); i++) 
        {
        	MineTile btn = mineList.get(i);
            btn.setText("💣");
        }
        
        gameOver = true;
        textLabel.setText("Game Over!");
        timeline.stop(); 
    }
	
	void revealHeadquarter(int row, int col) 
	{
		if (board[row][col].getText().isEmpty()) {
			System.out.println("revealHeadquarter " + row + "; " + col);
			board[row][col].setText("🏠");
			board[row][col].setDisable(true); 
			remainingHeadquarters--; 
	        headquartersLabel.setText(remainingHeadquarters + " headquarters left");
	        
	        headquartersOpenList.add(board[row][col]);
	        
	        for (int i = row - 1; i <= row + 1; i++) {
	            for (int j = col - 1; j <= col + 1; j++) {
	                if (i >= 0 && i < SIZE && j >= 0 && j < SIZE) {
	                    MineTile btn = board[i][j];
	                    if (!mineList.contains(btn) && btn.getText().isEmpty()) {
	                        checkMine(i, j); 
	                    }
	                }
	            }
	        }
		}
    }
	
	void checkMine(int row, int col) {
		
		if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) {
			return;
		}
		
	    if (isDeactivationStarted) {
			gameOver = true;
	        textLabel.setText("Game Over!");
	        timeline.stop();
	        return;
		}
	    
	    MineTile btn = board[row][col];
	    
	    if (mineListHeadquarters.contains(btn)) {
			
			sharedBtn = btn;
			
			String[] symbols = {"W", "A", "S", "D"};
			StringBuilder randomSequence = new StringBuilder();
			for (int i = 0; i < 6; i++) {
				randomSequence.append(symbols[random.nextInt(symbols.length)]);
		    }
			generatedSequence = randomSequence.toString();
			System.out.println("Generated sequence: " + generatedSequence); 
			
			combinationLabel.setText(generatedSequence);
			
			StringBuilder userInput = new StringBuilder();
			
			startDeactivationTimer();
			
			isDeactivationStarted = true;
			
			return;
		}
	    
	    if (!mineListHeadquarters.isEmpty() && !isDeactivationStarted) {
	        gameOver = true;
	        textLabel.setText("Game Over!");
	        timeline.stop();
	        for (MineTile mineSpot : mineListHeadquarters) {
	            mineSpot.setDisable(true);
	        }
	        return;
	    }
	    
	    if (!headquartersOpenList.isEmpty() && openedCellsCounter >= 4) {
    	    MineTile randomHQ = headquartersOpenList.get(random.nextInt(headquartersOpenList.size()));
    	    addMineAroundHQ(randomHQ);
    	    openedCellsCounter = 0;
    	}
		
		if (headquartersList.contains(btn)) {
			revealHeadquarter(btn.row, btn.col);
		}
		
		if (btn.isDisable()) {
			return;
		}
	
		btn.setDisable(true);
		tilesClicked += 1;
		openedLabel.setText(tilesClicked + " cells opened");
		
		int minesFound = 0;
		
		minesFound += countMine(row-1, col-1);
		minesFound += countMine(row-1, col);
		minesFound += countMine(row-1, col+1);
		
		minesFound += countMine(row, col-1);
		minesFound += countMine(row, col+1);
		
		minesFound += countMine(row+1, col-1);
		minesFound += countMine(row+1, col);
		minesFound += countMine(row+1, col+1);
		
		if (minesFound > 0) {
			btn.setText(Integer.toString(minesFound));
		}
		else {
			btn.setText("");
			
			checkMine(row-1, col-1);
			checkMine(row-1, col);
			checkMine(row-1, col+1);
			checkMine(row, col-1);
			checkMine(row, col+1);
			checkMine(row+1, col-1);
			checkMine(row+1, col);
			checkMine(row+1, col+1);
		}
		
		if (tilesClicked == SIZE * SIZE - mineList.size() - headquartersList.size()) {
			gameOver = true;
			textLabel.setText("Mines Cleared!");
			timeline.stop();
		}
	}
	
	private void handleKeyPress(KeyEvent event) {
		if (!isDeactivationStarted)
			return;
		
        String input = "";
        
        switch (event.getCode()) {
	        case W:
	            input = "W";
	            break;
	        case A:
	            input = "A";
	            break;
	        case S:
	            input = "S";
	            break;
	        case D:
	            input = "D";
	            break;
	        default:
	            return;
        }
        
        userInputSequence.append(input);
        System.out.println("User input: " + userInputSequence.toString());
        
        if (userInputSequence.length() == 6) {
            checkUserInput(userInputSequence.toString());
        }
	}
	
	private void checkUserInput(String userInput) {
        if (userInput.equals(generatedSequence)) {
            System.out.println("Correct input!");
			sharedBtn.setText("");
			sharedBtn.setDisable(true); 
			mineListHeadquarters.remove(sharedBtn);
			System.out.println("Mine has been removed. HQ has been saved!");
			userInputSequence.setLength(0);
			isDeactivationStarted = false;
			combinationLabel.setText("");
			timerDeactivationLabel.setText("");
			timeElapsedDeactivation = 6;
			//
			if (timelineDeactivation != null) timelineDeactivation.setCycleCount(6);  
			
        } else {
            System.out.println("Incorrect input. Game Over!");
            gameOver = true;
            textLabel.setText("Game Over!");
            timeline.stop(); 
        }
    }
	
	private void startDeactivationTimer() {
		timeElapsedDeactivation = 6;
		timerDeactivationLabel.setText(timeElapsedDeactivation + " seconds");
		
		if (timelineDeactivation != null) timelineDeactivation.stop(); 
		
		timelineDeactivation = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
			timeElapsedDeactivation--;
			timerDeactivationLabel.setText(timeElapsedDeactivation + " seconds");
			
			if (!isDeactivationStarted) {
				if (timelineDeactivation != null) timelineDeactivation.stop(); 
				timerDeactivationLabel.setText("");
			}
			
			if (timeElapsedDeactivation <= 0) {
				System.out.println("Time is up! Game Over!");
				gameOver = true;
	            textLabel.setText("Game Over!");
	            timeline.stop();
			}
        }));
		
		timelineDeactivation.setCycleCount(6);  
	    timelineDeactivation.play();
	}
	
	int countMine(int row, int col) {
		if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) {
			return 0;
		}
		if (mineList.contains(board[row][col])) {
			return 1;
		}
		return 0;
	}
	
	int countHeadquarters(int row, int col) {
		if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) {
			return 0;
		}
		if (headquartersList.contains(board[row][col])) {
			return 1;
		}
		return 0;
	}
	
	void addMineAroundHQ(MineTile hq) {
	    ArrayList<MineTile> availableCells = new ArrayList<>();

	    for (int i = hq.row - 1; i <= hq.row + 1; i++) {
	        for (int j = hq.col - 1; j <= hq.col + 1; j++) {
	            if (i >= 0 && i < SIZE && j >= 0 && j < SIZE && !mineList.contains(board[i][j]) && board[i][j].getText().isEmpty()) {
	                availableCells.add(board[i][j]);
	            }
	        }
	    }

	    if (!availableCells.isEmpty() && mineListHeadquarters.isEmpty()) {
	        MineTile mineSpot = availableCells.get(random.nextInt(availableCells.size()));
	        mineSpot.setDisable(false); 
	        mineSpot.setText("💥");
	        mineListHeadquarters.add(mineSpot);
	    }
	}
	
	private void resetGame() {
	    gameOver = false;
	    tilesClicked = 0;
	    flagCount = mineCount;
	    remainingHeadquarters = HEADQUARTERS;
	    mineList.clear();
	    headquartersList.clear();
	    headquartersOpenList.clear();
	    
	    timerStarted = false;  
        timeElapsed = 0;  
        timeElapsedDeactivation = 6;
        timeline.stop(); 
        if (timelineDeactivation != null) timelineDeactivation.stop(); 
        timerLabel.setText("0 seconds");  
        timerDeactivationLabel.setText("");  
        openedLabel.setText("0 cells opened");
        
        //
        if (timelineDeactivation != null) timelineDeactivation.setCycleCount(6);  
        
        openedCellsCounter = 0;
        
        isDeactivationStarted = false;
        
        userInputSequence.setLength(0);
        
        combinationLabel.setText("");
	    
	    textLabel.setText("In progress");
	    minesLabel.setText(Integer.toString(flagCount) + " mines left");
	    headquartersLabel.setText(remainingHeadquarters + " headquarters left");

	    for (int row = 0; row < SIZE; row++) {
	        for (int col = 0; col < SIZE; col++) {
	            MineTile btn = board[row][col];
	            btn.setText("");
	            btn.setDisable(false);
	        }
	    }
	    
	    setMines();
	    setHeadquarters();
	}
}
