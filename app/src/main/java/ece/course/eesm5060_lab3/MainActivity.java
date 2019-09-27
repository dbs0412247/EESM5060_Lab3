package ece.course.eesm5060_lab3;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import static ece.course.eesm5060_lab3.GameView.LINE_D_TOPLEFT_BOTRIGHT;
import static ece.course.eesm5060_lab3.GameView.LINE_D_TOPRIGHT_BOTLEFT;
import static ece.course.eesm5060_lab3.GameView.LINE_H_BOT;
import static ece.course.eesm5060_lab3.GameView.LINE_H_MID;
import static ece.course.eesm5060_lab3.GameView.LINE_H_TOP;
import static ece.course.eesm5060_lab3.GameView.LINE_V_LEFT;
import static ece.course.eesm5060_lab3.GameView.LINE_V_MID;
import static ece.course.eesm5060_lab3.GameView.LINE_V_RIGHT;
import static ece.course.eesm5060_lab3.GameView.PIECE_NONE;
import static ece.course.eesm5060_lab3.GameView.PIECE_BLUE;
import static ece.course.eesm5060_lab3.GameView.PIECE_RED;

public class MainActivity extends AppCompatActivity {

    private GameView mGameView;
    private Button mBtnStart;
    private Toolbar mToolbar;

    private final int STATE_WAIT_START = 0;
    private final int STATE_PLAYING = 1;
    private final int STATE_BLUE_WIN = 2;
    private final int STATE_RED_WIN = 3;
    private final int STATE_DRAW_GAME = 4;

    private final String TAG_GAME_STATE = "tagGameState";
    private final String TAG_PIECE_COUNT = "tagPieceCount";
    private final String TAG_IS_BLUE_TURN = "tagIsBlueTurn";
    private final String TAG_LINE_LEFT = "tagLineLeft";
    private final String TAG_LINE_MIDDLE = "tagLineMiddle";
    private final String TAG_LINE_RIGHT = "tagLineRight";
    private final String TAG_WIN_LINE = "tagWinLine";

    private int[][] mBoardState = new int[][]{
            {PIECE_NONE, PIECE_NONE, PIECE_NONE},
            {PIECE_NONE, PIECE_NONE, PIECE_NONE},
            {PIECE_NONE, PIECE_NONE, PIECE_NONE}
    };
    private boolean[] mWinLines = new boolean[] {
            false, false, false, false,
            false, false, false, false
    };
    private int mGameState = STATE_WAIT_START;
    private boolean mIsBlueTurn = true;
    private int mPieceCount = 0;

    private boolean isOnTopLeftBotRightDiagonal(int posX, int posY) {
        return (posX == 0 && posY == 0)
            || (posX == 1 && posY == 1)
            || (posX == 2 && posY == 2);
    }

    private boolean isOnTopRightBotLeftDiagonal(int posX, int posY) {
        return (posX == 0 && posY == 2)
            || (posX == 1 && posY == 1)
            || (posX == 2 && posY == 0);
    }

    private void inputPiece(int posX, int posY) {
        mPieceCount += 1;
        if (mBoardState[posX][posY] != PIECE_NONE)
            return;

        if (mIsBlueTurn) {
            mBoardState[posX][posY] = PIECE_BLUE;
            mGameView.setBlueCross(posX, posY);
        } else {
            mBoardState[posX][posY] = PIECE_RED;
            mGameView.setRedCircle(posX, posY);
        }

        boolean isWin = false;
        // Check horizontal line at y = posY
        if (mBoardState[0][posY] == mBoardState[1][posY] && mBoardState[1][posY] == mBoardState[2][posY]) {
            isWin = true;
            switch (posY) {
                case 0:
                    mGameView.setWinLine(LINE_H_TOP);
                    break;
                case 1:
                    mGameView.setWinLine(LINE_H_MID);
                    break;
                case 2:
                    mGameView.setWinLine(LINE_H_BOT);
                    break;
            }
        }
        // Check vertical line at x = posX
        if (mBoardState[posX][0] == mBoardState[posX][1] && mBoardState[posX][1] == mBoardState[posX][2]) {
            isWin = true;
            switch (posX) {
                case 0:
                    mGameView.setWinLine(LINE_V_LEFT);
                    break;
                case 1:
                    mGameView.setWinLine(LINE_V_MID);
                    break;
                case 2:
                    mGameView.setWinLine(LINE_V_RIGHT);
                    break;
            }
        }
        // Check top-left-bot-right diagonal line
        if(isOnTopLeftBotRightDiagonal(posX, posY) && mBoardState[0][0] == mBoardState[1][1] && mBoardState[1][1] == mBoardState[2][2]) {
            isWin = true;
            mGameView.setWinLine(LINE_D_TOPLEFT_BOTRIGHT);
        }
        if(isOnTopRightBotLeftDiagonal(posX, posY) && mBoardState[2][0] == mBoardState[1][1] && mBoardState[1][1] == mBoardState[0][2]) {
            isWin = true;
            mGameView.setWinLine(LINE_D_TOPRIGHT_BOTLEFT);
        }

        if (isWin)
            mGameState = mIsBlueTurn ? STATE_BLUE_WIN : STATE_RED_WIN;
        else if (mPieceCount == 9)
            mGameState = STATE_DRAW_GAME;
        else
            mIsBlueTurn = !mIsBlueTurn;

        setUiByState();
    }

    void setUiByState() {
        switch (mGameState) {
            case STATE_WAIT_START:
                mToolbar.setTitle(R.string.statusWaitStart);
                mBtnStart.setVisibility(View.VISIBLE);
                break;
            case STATE_PLAYING:
                mToolbar.setTitle(mIsBlueTurn? R.string.statusBlueTurn : R.string.statusRedTurn);
                mBtnStart.setVisibility(View.INVISIBLE);
                break;
            case STATE_BLUE_WIN:
                mToolbar.setTitle(R.string.statusBlueWin);
                mBtnStart.setVisibility(View.VISIBLE);
                break;
            case STATE_RED_WIN:
                mToolbar.setTitle(R.string.statusRedWin);
                mBtnStart.setVisibility(View.VISIBLE);
                break;
            case STATE_DRAW_GAME:
                mToolbar.setTitle(R.string.statusDraw);
                mBtnStart.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mGameView = findViewById(R.id.game);
        mGameView.setHandler(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (mGameState != STATE_PLAYING)
                    return;
                int posX = msg.getData().getInt(GameView.TAG_ON_TOUCH_X);
                int posY = msg.getData().getInt(GameView.TAG_ON_TOUCH_Y);
                inputPiece(posX, posY);
                mGameView.invalidate();
            }
        });

        mBtnStart = findViewById(R.id.btnStart);
        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameState = STATE_PLAYING;
                mToolbar.setTitle(mIsBlueTurn? R.string.statusBlueTurn : R.string.statusRedTurn);
                mBtnStart.setVisibility(View.INVISIBLE);
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        mBoardState[i][j] = PIECE_NONE;
                    }
                }
                for (int i = 0; i < 8; i++)
                    mWinLines[i] = false;
                mPieceCount = 0;
                mGameView.cleanAll();
                mGameView.invalidate();
            }
        });

        setUiByState();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(TAG_GAME_STATE, mGameState);
        outState.putInt(TAG_PIECE_COUNT, mPieceCount);
        outState.putBoolean(TAG_IS_BLUE_TURN, mIsBlueTurn);
        outState.putIntArray(TAG_LINE_LEFT, mBoardState[0]);
        outState.putIntArray(TAG_LINE_MIDDLE, mBoardState[1]);
        outState.putIntArray(TAG_LINE_RIGHT, mBoardState[2]);
        outState.putBooleanArray(TAG_WIN_LINE, mWinLines);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mGameState = savedInstanceState.getInt(TAG_GAME_STATE);
        mPieceCount = savedInstanceState.getInt(TAG_PIECE_COUNT);
        mIsBlueTurn = savedInstanceState.getBoolean(TAG_IS_BLUE_TURN);
        mBoardState[0] = savedInstanceState.getIntArray(TAG_LINE_LEFT);
        mBoardState[1] = savedInstanceState.getIntArray(TAG_LINE_MIDDLE);
        mBoardState[2] = savedInstanceState.getIntArray(TAG_LINE_RIGHT);
        mWinLines = savedInstanceState.getBooleanArray(TAG_WIN_LINE);
        setUiByState();
        mGameView.cleanAll();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (mBoardState[i][j] == PIECE_BLUE)
                    mGameView.setBlueCross(i, j);
                else if (mBoardState[i][j] == PIECE_RED)
                    mGameView.setRedCircle(i, j);
            }
        }
        for (int i = 0; i < 8; i++)
            if (mWinLines[i])
                mGameView.setWinLine(i);
        mGameView.invalidate();
    }
}
