package ge.nikka.gtutable;

import android.animation.*;
import android.content.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.*;

public class EditTextCursorWatcher extends EditText {
    public EditTextCursorWatcher(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ValueAnimator anim2 = ValueAnimator.ofFloat(1f, 1.025f);
                anim2.setDuration(100);
                anim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        EditTextCursorWatcher.this.setScaleX((Float)animation.getAnimatedValue());
                        EditTextCursorWatcher.this.setScaleY((Float)animation.getAnimatedValue());
                    }
                });
                anim2.setRepeatCount(1);
                anim2.setRepeatMode(ValueAnimator.REVERSE);
                if (event.getAction() == MotionEvent.ACTION_DOWN)    
                anim2.start();
                return false;
            }
        });
    }

    public EditTextCursorWatcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ValueAnimator anim2 = ValueAnimator.ofFloat(1f, 1.025f);
                anim2.setDuration(100);
                anim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        EditTextCursorWatcher.this.setScaleX((Float)animation.getAnimatedValue());
                        EditTextCursorWatcher.this.setScaleY((Float)animation.getAnimatedValue());
                    }
                });
                anim2.setRepeatCount(1);
                anim2.setRepeatMode(ValueAnimator.REVERSE);
                if (event.getAction() == MotionEvent.ACTION_DOWN)    
                anim2.start();
                return false;
            }
        });
    }

    public EditTextCursorWatcher(Context context) {
        super(context);
        this.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ValueAnimator anim2 = ValueAnimator.ofFloat(1f, 1.025f);
                anim2.setDuration(100);
                anim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        EditTextCursorWatcher.this.setScaleX((Float)animation.getAnimatedValue());
                        EditTextCursorWatcher.this.setScaleY((Float)animation.getAnimatedValue());
                    }
                });
                anim2.setRepeatCount(1);
                anim2.setRepeatMode(ValueAnimator.REVERSE);
                if (event.getAction() == MotionEvent.ACTION_DOWN)    
                anim2.start();
                return false;
            }
        });
    }

    @Override   
    protected void onSelectionChanged(int selStart, int selEnd) {
        ValueAnimator anim2 = ValueAnimator.ofFloat(1f, 1.02f);
        anim2.setDuration(100);
        anim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                EditTextCursorWatcher.this.setScaleX((Float)animation.getAnimatedValue());
                EditTextCursorWatcher.this.setScaleY((Float)animation.getAnimatedValue());
            }
        });
        anim2.setRepeatCount(1);
        anim2.setRepeatMode(ValueAnimator.REVERSE);
        anim2.start();
    }
}