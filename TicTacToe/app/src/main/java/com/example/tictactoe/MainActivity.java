package com.example.tictactoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    // Represents the internal state of the game
    private TicTacToeGame mGame;
    // Buttons making up the board
    private Button mBoardButtons[];
    // Various text displayed
    private TextView mInfoTextView;
    private TextView YourScore;
    private TextView ComputerScore;
    private TextView Ties;
    // Restart Button
    private Button startButton;
    // Game Over
    Boolean mGameOver;
    private Menu menu;
    public static int game_diff = 0;
    public static int first =0;
    private int scoreStatus[]= {0,0,0};
    private SoundPool Music;
    private int MusicID[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadMusic();
        mGame = new TicTacToeGame();
        mBoardButtons = new Button[mGame.BOARD_SIZE];
        mBoardButtons[0] = (Button) findViewById(R.id.button0);
        mBoardButtons[1] = (Button) findViewById(R.id.button1);
        mBoardButtons[2] = (Button) findViewById(R.id.button2);
        mBoardButtons[3] = (Button) findViewById(R.id.button3);
        mBoardButtons[4] = (Button) findViewById(R.id.button4);
        mBoardButtons[5] = (Button) findViewById(R.id.button5);
        mBoardButtons[6] = (Button) findViewById(R.id.button6);
        mBoardButtons[7] = (Button) findViewById(R.id.button7);
        mBoardButtons[8] = (Button) findViewById(R.id.button8);
        mInfoTextView = (TextView) findViewById(R.id.information);
        YourScore = (TextView) findViewById(R.id.YourScore);
        ComputerScore=(TextView)findViewById(R.id.ComputerScore);
        Ties=(TextView)findViewById(R.id.TieScore);
        mGame = new TicTacToeGame();
        startNewGame();
    }
    @Override
    protected void onStart() {
        super.onStart();
        loadScore();
        updateScore();
    }
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mInfoTextView.setText(savedInstanceState.getString("mInfoTextView"));
        mInfoTextView.setTextColor(savedInstanceState.getInt("mInfoTextViewColor"));
        char[] mBoard = savedInstanceState.getCharArray("mBoard");
        for (int i = 0 ; i < mBoard.length ; i++) {
            if (mBoard[i] != ' ') {
                setMove(mBoard[i], i);
            }
        }
        mGameOver = savedInstanceState.getBoolean("mGameOver");
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("mInfoTextView", mInfoTextView.getText().toString());
        outState.putInt("mInfoTextViewColor", mInfoTextView.getCurrentTextColor());
        outState.putCharArray("mBoard", mGame.getmBoard());
        outState.putBoolean("mGameOver", mGameOver);
    }

    public void loadScore() {
        YourScore.setText(String.valueOf(scoreStatus[0]));
        ComputerScore.setText(String.valueOf(scoreStatus[1]));
        Ties.setText(String.valueOf(scoreStatus[2]));
    }

    public void updateScore(){
        if(mGame.checkForWinner()==2){
            scoreStatus[0]+=1;
        }
        if(mGame.checkForWinner()==3){
            scoreStatus[1]+=1;
        }
        if(mGame.checkForWinner()==1){
            scoreStatus[2]+=1;
        }
        loadScore();
    }

    private void playMusic(int soundId) {
        Music.play(soundId, 1, 1, 1, 0, 1);
    }

    private void loadMusic() {
        this.Music = new SoundPool.Builder().build();
        TypedArray allSounds = getResources().obtainTypedArray(R.array.sounds);
        this.MusicID = new int[allSounds.length()];
        for (int i = 0 ; i < allSounds.length() ; i++) {
            MusicID[i] = Music.load(this, allSounds.getResourceId(i, -1), 1);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the aaction bar if it is present
        this.menu = menu;
        getMenuInflater().inflate(R.menu.options_menu, this.menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.Help:
                Uri uri = Uri.parse(getResources().getString(R.string.help_url));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            case R.id.First_move:
                FirstMoveDialog firstMoveDialog = new FirstMoveDialog();
                firstMoveDialog.show(getSupportFragmentManager(),"FirstMoveDialog");
                return true;
            case R.id.Difficulty:
                DifficultyDialog difficultyDialog = new DifficultyDialog();
                difficultyDialog.show(getSupportFragmentManager(),"DifficultyDialog");
                return true;
            case R.id.Quit:
                this.finish();
                System.exit(0);
        }
        return false;
    }
    //--- Set up the game board.
    private void startNewGame() {
        mGameOver = false;
        mGame.clearBoard();
        //---Reset all buttons
        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setText("");
            mBoardButtons[i].setEnabled(true);
            mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));
        }
        // Random
        if (first == 2) {
            first = (int) Math.round(Math.random());
            if (first == 1) {
                //---Computer goes first
                int move = mGame.getComputerMove(game_diff,true);
                setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                mInfoTextView.setText(R.string.You_turn_text);
            }
            else {
                //---Human goes first
                mInfoTextView.setText(R.string.You_go_first_text);
            }
        }
        else{
            if (first == 1) {
                //---Computer goes first
                int move = mGame.getComputerMove(game_diff,true);
                setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                mInfoTextView.setText(R.string.You_turn_text);
            }
            else {
                //---Human goes first
                mInfoTextView.setText(R.string.You_go_first_text);
            }
        }

    }
    //---Handles clicks on the game board buttons
    private class ButtonClickListener implements View.OnClickListener {
        int location;
        int musicid;
        public ButtonClickListener(int location) {
            this.location = location;
        }
        @Override
        public void onClick(View v) {
            if (mGameOver == false) {
                if (mBoardButtons[location].isEnabled()) {
                    setMove(TicTacToeGame.HUMAN_PLAYER, location);
                    //--- If no winner yet, let the computer make a move
                    int winner = mGame.checkForWinner();
                    if (winner == 0) {
                        mInfoTextView.setText(R.string.Android_turn_text);
                        int move = mGame.getComputerMove(game_diff, false);
                        setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                        winner = mGame.checkForWinner();
                    }
                    if (winner == 0) {
                        mInfoTextView.setTextColor(Color.rgb(0, 0, 0));
                        mInfoTextView.setText(R.string.You_turn_text);
                        musicid = 3;
                    } else if (winner == 1) {
                        mInfoTextView.setTextColor(Color.rgb(0, 0, 200));
                        mInfoTextView.setText(R.string.Game_tie_text);
                        updateScore();
                        musicid = 2;
                        mGameOver = true;
                    } else if (winner == 2) {
                        mInfoTextView.setTextColor(Color.rgb(0, 200, 0));
                        mInfoTextView.setText(R.string.You_won_text);
                        updateScore();
                        musicid= 0;
                        mGameOver = true;
                    } else {
                        mInfoTextView.setTextColor(Color.rgb(200, 0, 0));
                        mInfoTextView.setText(R.string.Android_won_text);
                        updateScore();
                        musicid = 1;
                        mGameOver = true;
                    }
                    playMusic(MusicID[musicid]);
                }
            }
        }
    }
    private void setMove(char player, int location) {
        mGame.setMove(player, location);
        mBoardButtons[location].setEnabled(false);
        mBoardButtons[location].setText(String.valueOf(player));
        if (player == TicTacToeGame.HUMAN_PLAYER)
            mBoardButtons[location].setTextColor(Color.rgb(0, 200, 0));
        else
            mBoardButtons[location].setTextColor(Color.rgb(200, 0, 0));
    }
    public void newGame(View v) {
        startNewGame();
    }
}