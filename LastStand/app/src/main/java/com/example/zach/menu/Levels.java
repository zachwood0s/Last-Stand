package com.example.zach.menu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.ViewFlipper;

import com.example.zach.laststand.OpenGLES20Activity;
import com.example.zach.laststand.R;

public class Levels extends FragmentActivity {




    public static int rows = 3;
    public static int coloums = 3;
    public static int worlds = 3;
    private int buttonMarginsHoriz = 8;
    private int buttonMarginsVert = 5;
    private int buttonPaddingTop = 10;
    private int buttonPaddingRight = 21;
    private int buttonWidth = 80;
    private int buttonHeight = 80;
    private ViewFlipper viewFlipper;
    private float lastX;
    //Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.levels);

        viewFlipper = (ViewFlipper) findViewById(R.id.flipDatView);

        createTables();
    }
    public boolean onTouchEvent(MotionEvent touchevent) {

        switch (touchevent.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                lastX = touchevent.getX();
                break;
            }

            case MotionEvent.ACTION_UP:
            {
                float currentX = touchevent.getX();

                if (lastX < currentX)
                {
                    if (viewFlipper.getDisplayedChild()==0)
                        break;

                    viewFlipper.setInAnimation(this, R.transition.in_from_left);
                    viewFlipper.setOutAnimation(this, R.transition.out_to_right);
//                    vf.showNext();
                    viewFlipper.showPrevious();
                }

                if (lastX > currentX)
                {
//                    if (vf.getDisplayedChild()==1)
                    if (viewFlipper.getDisplayedChild()==viewFlipper.getChildCount()-1)
                        break;

                    viewFlipper.setInAnimation(this, R.transition.in_from_right);
                    viewFlipper.setOutAnimation(this, R.transition.out_to_left);
//                    vf.showPrevious();
                    viewFlipper.showNext();
                }

                break;
            }

            case MotionEvent.ACTION_MOVE:
            {
                float tempX = touchevent.getX();
                int scrollX = (int) (tempX - lastX);

                //vf.scrollBy(scrollX, 0);

                break;
            }

        }

        return false;

    }


    /*public void addListenerOnButton() {
        final Context context = this;

      //  for(int i = 0; i<9; i++) {
            button[0] = (Button) findViewById(R.id.level1);

            button[0].setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    Intent intent = new Intent(context, OpenGLES20Activity.class);
                    startActivity(intent);

                }

            });
       // }

    }*/
    public void createTables(){

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/manteka.ttf");


        // Button testBut = (Button) findViewById(R.id.level2);


        for(int j = 0; j<worlds; j++){
            TableLayout table = new TableLayout(this);
            ViewFlipper.LayoutParams tb = new ViewFlipper.LayoutParams(ViewFlipper.LayoutParams.WRAP_CONTENT, ViewFlipper.LayoutParams.WRAP_CONTENT);
            tb.gravity = Gravity.CENTER;

            final int worldNum = j;

            for(int i = 0; i<rows; i++){
                TableRow row = new TableRow(this);
                TableLayout.LayoutParams tl = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
                tl.setMargins(dpToPix(buttonMarginsVert),dpToPix(buttonMarginsVert),dpToPix(buttonMarginsVert),dpToPix(buttonMarginsVert));

                for(int x = 0; x<coloums; x++){
                    final int index = i*coloums+x;

                    TableRow.LayoutParams tr = new TableRow.LayoutParams(dpToPix(buttonWidth), dpToPix(buttonHeight));
                    tr.gravity = Gravity.CENTER;
                    tr.setMargins(dpToPix(buttonMarginsHoriz),dpToPix(buttonMarginsHoriz),dpToPix(buttonMarginsHoriz),dpToPix(buttonMarginsHoriz));


                    Button button = new Button(this);

                    SharedPreferences saves = getSharedPreferences("label", 0);
                    final boolean accessLevel = saves.getBoolean("level" + index + "world" + worldNum, false);

                    //Drawable buttonImage = getResources().getDrawable(R.drawable.ic_launcher);

                    if(index == 0){
                        button.setBackgroundResource(R.drawable.level1);
                    } else if(index == 1){
                        button.setBackgroundResource(R.drawable.level2);
                    } else if(index == 2){
                        button.setBackgroundResource(R.drawable.level3);
                    } else if(index == 3){
                        button.setBackgroundResource(R.drawable.level4);
                    } else if(index == 4){
                        button.setBackgroundResource(R.drawable.level5);
                    } else if(index == 5){
                        button.setBackgroundResource(R.drawable.level6);
                    } else if(index == 6){
                        button.setBackgroundResource(R.drawable.level7);
                    } else if(index == 7){
                        button.setBackgroundResource(R.drawable.level8);
                    } else if(index == 8) {
                        button.setBackgroundResource(R.drawable.level9);
                    }
                    if(!accessLevel){
                        button.setAlpha(.5f);
                        //transparency
                    }


                    button.setPadding(0, dpToPix(buttonPaddingTop), dpToPix(buttonPaddingRight), 0);


                    button.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            if(accessLevel) {
                                Intent intent = new Intent(view.getContext(), OpenGLES20Activity.class);
                                intent.putExtra("level", index);
                                intent.putExtra("world", worldNum);
                                startActivity(intent);
                            }
                        }
                    });
                    button.setGravity(Gravity.TOP | Gravity.RIGHT);
                    button.setTypeface(font);
                    button.setTextColor(Color.parseColor("#FFFFFF"));
                    button.setTextSize(22);

                    row.addView(button,tr);
                }
                table.addView(row,tl);
            }
            viewFlipper.addView(table,tb);
        }
    }
    public int dpToPix(int dps){
        final float scale = this.getResources().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Levels.this, Menu.class));
        finish();
    }
}
