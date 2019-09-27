package ece.course.eesm5060_lab3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;

public class GameView extends SurfaceView {

    public static final String TAG_ON_TOUCH_X = "tagOnTouchX";
    public static final String TAG_ON_TOUCH_Y = "tagOnTouchY";
    public static final int LINE_H_TOP = 0;
    public static final int LINE_H_MID = 1;
    public static final int LINE_H_BOT = 2;
    public static final int LINE_V_LEFT = 3;
    public static final int LINE_V_MID = 4;
    public static final int LINE_V_RIGHT = 5;
    public static final int LINE_D_TOPLEFT_BOTRIGHT = 6;
    public static final int LINE_D_TOPRIGHT_BOTLEFT = 7;

    public static final int PIECE_NONE = 0;
    public static final int PIECE_BLUE = 1;
    public static final int PIECE_RED = 2;

    private float mDivision = 0f;
    private final Paint mPaintBoard, mPaintRedCircle, mPaintBlueCross, mPaintWinLine;
    private Handler mHandler;

    private int[][] mBoardState = new int[][]{
            {PIECE_NONE, PIECE_NONE, PIECE_NONE},
            {PIECE_NONE, PIECE_NONE, PIECE_NONE},
            {PIECE_NONE, PIECE_NONE, PIECE_NONE}
    };
    private boolean[] mWinLines = new boolean[] {
            false, false, false, false,
            false, false, false, false
    };

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        mPaintBoard = new Paint();
        mPaintBoard.setColor(Color.GREEN);
        mPaintBoard.setStrokeWidth(5.0f);

        mPaintRedCircle = new Paint();
        mPaintRedCircle.setColor(Color.RED);
        mPaintRedCircle.setStyle(Paint.Style.STROKE);
        mPaintRedCircle.setStrokeWidth(5.0f);

        mPaintBlueCross = new Paint();
        mPaintBlueCross.setColor(Color.BLUE);
        mPaintBlueCross.setStrokeWidth(5.0f);

        mPaintWinLine = new Paint();
        mPaintWinLine.setStrokeWidth(10.0f);
    }

    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    public void cleanAll() {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                mBoardState[r][c] = PIECE_NONE;
            }
        }
        for (int i = 0; i < 8; i++) {
            mWinLines[i] = false;
        }
    }

    public void setBlueCross(int posX, int posY) {
        mBoardState[posX][posY] = PIECE_BLUE;
    }

    public void setRedCircle(int posX, int posY) {
        mBoardState[posX][posY] = PIECE_RED;
    }

    public void setWinLine(int line) {
        if (line >= 0 && line < 8) mWinLines[line] = true;
    }

    private void drawRedCircle(Canvas canvas, int posX, int posY) {
        if (canvas == null)
            return;

        canvas.drawCircle(
                mDivision * (posX * 2 + 2),
                mDivision * (posY * 2 + 2),
                mDivision - 10,
                mPaintRedCircle);
    }

    private void drawBlueCross(Canvas canvas, int posX, int posY) {
        canvas.drawLine(
                mDivision * (posX * 2 + 1) + 10,
                mDivision * (posY * 2 + 1) + 10.0f,
                mDivision * (posX * 2 + 3) - 10,
                mDivision * (posY * 2 + 3) - 10.0f,
                mPaintBlueCross);
        canvas.drawLine(
                mDivision * (posX * 2 + 3) - 10,
                mDivision * (posY * 2 + 1) + 10.0f,
                mDivision * (posX * 2 + 1) + 10,
                mDivision * (posY * 2 + 3) - 10.0f,
                mPaintBlueCross);
    }

    private void drawWinLine(Canvas canvas, int line, boolean isBlue) {
        if (canvas == null)
            return;

        mPaintWinLine.setColor(isBlue? Color.BLUE : Color.RED);
        switch (line) {
            case LINE_H_TOP:
                canvas.drawLine(
                        mDivision * 2,
                        mDivision * 2,
                        mDivision * 6,
                        mDivision * 2,
                        mPaintWinLine);
                break;
            case LINE_H_MID:
                canvas.drawLine(
                        mDivision * 2,
                        mDivision * 4,
                        mDivision * 6,
                        mDivision * 4,
                        mPaintWinLine);
                break;
            case LINE_H_BOT:
                canvas.drawLine(
                        mDivision * 2,
                        mDivision * 6,
                        mDivision * 6,
                        mDivision * 6,
                        mPaintWinLine);
                break;
            case LINE_V_LEFT:
                canvas.drawLine(
                        mDivision * 2,
                        mDivision * 2,
                        mDivision * 2,
                        mDivision * 6,
                        mPaintWinLine);
                break;
            case LINE_V_MID:
                canvas.drawLine(
                        mDivision * 4,
                        mDivision * 2,
                        mDivision * 4,
                        mDivision * 6,
                        mPaintWinLine);
                break;
            case LINE_V_RIGHT:
                canvas.drawLine(
                        mDivision * 6,
                        mDivision * 2,
                        mDivision * 6,
                        mDivision * 6,
                        mPaintWinLine);
                break;
            case LINE_D_TOPLEFT_BOTRIGHT:
                canvas.drawLine(
                        mDivision * 2,
                        mDivision * 2,
                        mDivision * 6,
                        mDivision * 6,
                        mPaintWinLine);
                break;
            case LINE_D_TOPRIGHT_BOTLEFT:
                canvas.drawLine(
                        mDivision * 2,
                        mDivision * 6,
                        mDivision * 6,
                        mDivision * 2,
                        mPaintWinLine);
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mHandler == null || event.getAction() != MotionEvent.ACTION_DOWN)
            return false;
        int ptrCount = event.getPointerCount();
        for (int i = 0; i < ptrCount; i++) {
            float tmpX = event.getX();
            float tmpY = event.getY();
            if (tmpX > mDivision && tmpX < mDivision*7 &&
                tmpY > mDivision && tmpY < mDivision*7) {
                int posX = (tmpX > mDivision*5)? 2 : (tmpX > mDivision*3)? 1 : 0;
                int posY = (tmpY > mDivision*5)? 2 : (tmpY > mDivision*3)? 1 : 0;
                Message msg = mHandler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putInt(TAG_ON_TOUCH_X, posX);
                bundle.putInt(TAG_ON_TOUCH_Y, posY);
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
            else
            {
                Log.i("GameView.onTouchEvent", "event X or Y outside range!");
            }
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoard(canvas);

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                switch (mBoardState[r][c]) {
                    case PIECE_BLUE:
                        drawBlueCross(canvas, r, c);
                        break;
                    case PIECE_RED:
                        drawRedCircle(canvas, r, c);
                        break;
                }
            }
        }
        if (mWinLines[LINE_H_TOP])
            drawWinLine(canvas, LINE_H_TOP, mBoardState[0][0] == PIECE_BLUE);
        if (mWinLines[LINE_H_MID])
            drawWinLine(canvas, LINE_H_MID, mBoardState[0][1] == PIECE_BLUE);
        if (mWinLines[LINE_H_BOT])
            drawWinLine(canvas, LINE_H_BOT, mBoardState[0][2] == PIECE_BLUE);
        if (mWinLines[LINE_V_LEFT])
            drawWinLine(canvas, LINE_V_LEFT, mBoardState[0][0] == PIECE_BLUE);
        if (mWinLines[LINE_V_MID])
            drawWinLine(canvas, LINE_V_MID, mBoardState[1][0] == PIECE_BLUE);
        if (mWinLines[LINE_V_RIGHT])
            drawWinLine(canvas, LINE_V_RIGHT, mBoardState[2][0] == PIECE_BLUE);
        if (mWinLines[LINE_D_TOPLEFT_BOTRIGHT])
            drawWinLine(canvas, LINE_D_TOPLEFT_BOTRIGHT, mBoardState[0][0] == PIECE_BLUE);
        if (mWinLines[LINE_D_TOPRIGHT_BOTLEFT])
            drawWinLine(canvas, LINE_D_TOPRIGHT_BOTLEFT, mBoardState[2][0] == PIECE_BLUE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mDivision = ((w < h)? w : h) / 8f;
    }

    private void drawBoard(Canvas canvas) {
        if (canvas == null)
            return;

        canvas.drawColor(Color.BLACK);
        canvas.drawLine(mDivision * 1, mDivision * 3, mDivision * 7, mDivision * 3, mPaintBoard);
        canvas.drawLine(mDivision * 1, mDivision * 5, mDivision * 7, mDivision * 5, mPaintBoard);
        canvas.drawLine(mDivision * 3, mDivision * 1, mDivision * 3, mDivision * 7, mPaintBoard);
        canvas.drawLine(mDivision * 5, mDivision * 1, mDivision * 5, mDivision * 7, mPaintBoard);
    }

}
