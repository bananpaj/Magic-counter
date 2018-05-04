package com.example.magicapp;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.DrawableRes;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;

import static com.example.magicapp.R.drawable.nickel;

public class MainActivity extends AppCompatActivity {

    private TextView player1_hp;
    private TextView player2_hp;
    private DrawerLayout drawerLayout;
    private String p1_newName="";
    private String p2_newName="";
    private LinearLayout drawerListView;
    private TextView p1_add, p1_subtract,p1_add_5,p1_subtract_5,p2_add,p2_subtract,p2_add_5,p2_subtract_5,b_newGame;
    private int p1;
    private int p2;
    private int p1_prev,p2_prev;
    private int p1_currentHp;
    private int p2_currentHp;
    private TextView p1_name;
    private TextView p2_name;
    private EditText p1_name_edit;
    private EditText p2_name_edit;
    private ImageView diceImg;
    private TextView dmgText1,dmgText2;
    private hpCounter counter = new hpCounter();
    private boolean newGame = true;
    private int p1_hp_add_clicked = 0;
    private int p2_hp_add_clicked = 0;
    private int p1_hp_subtract_clicked = 0;
    private int p2_hp_subtract_clicked = 0;
    private boolean mAutoIncrement = false;
    private boolean mAutoDecrement = false;
    private boolean mAutoIncrement2 = false;
    private boolean mAutoDecrement2 = false;
    private Handler counterUpdateHandler = new Handler();
    private long DELAY = 100;
    private boolean coinScreen = false;
    private AlertDialog myAlertDialog;
    private ArrayList<Integer> p1_dmg_hist = new ArrayList<Integer>();
    private ArrayList<Integer> p2_dmg_hist = new ArrayList<Integer>();
    private String coinFlip = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerListView = (LinearLayout) findViewById(R.id.left_drawer);
        b_newGame = (TextView) findViewById(R.id.newGameText);
        b_newGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newGame();
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        //initialize buttons and hp texts
        player1_hp = (TextView) findViewById(R.id.player1_HP);
        p1_add = (TextView) findViewById(R.id.button_add);
        p1_add_5 = (TextView) findViewById(R.id.button_add_5);
        p1_subtract = (TextView) findViewById(R.id.button_subtract);
        p1_subtract_5 = (TextView) findViewById(R.id.button_subtract_5);
        player2_hp = (TextView) findViewById(R.id.player2_HP);
        p2_add = (TextView) findViewById(R.id.button2_add);
        p2_add_5 = (TextView) findViewById(R.id.button2_add_5);
        p2_subtract = (TextView) findViewById(R.id.button2_subtract);
        p2_subtract_5 = (TextView) findViewById(R.id.button2_subtract_5);
        p1_name = (TextView) findViewById(R.id.p1_textview);
        p2_name = (TextView) findViewById(R.id.p2_textview);
        diceImg = (ImageView) findViewById(R.id.dice_image);
        dmgText1 = (TextView) findViewById(R.id.damage_text1);
        dmgText2 = (TextView) findViewById(R.id.damage_text2);

        //Screen rotation
        if(savedInstanceState != null){
            p1_currentHp = savedInstanceState.getInt("p1_savedHP");
            p2_currentHp = savedInstanceState.getInt("p2_savedHP");
            p1_newName = savedInstanceState.getString("p1_savedName");
            p2_newName = savedInstanceState.getString("p2_savedName");
            p1_dmg_hist = savedInstanceState.getIntegerArrayList("p1_dmg_list");
            p2_dmg_hist = savedInstanceState.getIntegerArrayList("p2_dmg_list");
            player1_hp.setText("" + p1_currentHp);
            player2_hp.setText("" + p2_currentHp);
            p1 = p1_currentHp;
            p2 = p2_currentHp;
            coinScreen = savedInstanceState.getBoolean("coinScreen");
            coinFlip = savedInstanceState.getString("coinFlip");
            if(coinScreen){
                myAlertDialog = coinAlert();
                myAlertDialog.show();
            }

            //put the dmg history in an array and display it
            Integer[] a1 = p1_dmg_hist.toArray(new Integer[p1_dmg_hist.size()]);
            Integer[] a2 = p1_dmg_hist.toArray(new Integer[p2_dmg_hist.size()]);

            for(int i = 0;i<a1.length;i++ ){
                dmgText1.append(""+a1[i]);
                dmgText1.append(", ");
            }

            for(int j = 0;j<a2.length;j++ ){
                dmgText2.append(""+a2[j]);
                dmgText2.append(", ");

            }

            if (p1_newName.length()>0){
                p1_name.setText(p1_newName);
            }
            if (p2_newName.length()>0){
                p2_name.setText(p2_newName);
            }

        }else{
            p1=20;
            p2=20;
        }
        //sidebar
        //Add listeners
        p1_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player1_add(1);
            }
        });

        p1_add_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player1_add(5);
            }
        });

        p1_add.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        mAutoIncrement = true;
                        mAutoDecrement = false;
                        counterUpdateHandler.post(new CtrUpdater());
                        return true;
                    }
                }
        );

        p1_add.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ((event.getAction() == MotionEvent.ACTION_UP) || event.getAction() == MotionEvent.ACTION_CANCEL && mAutoIncrement) {
                    mAutoIncrement = false;
                    counterUpdateHandler.removeCallbacksAndMessages(null);
                }
                return false;
            }
        });
        p1_subtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player1_subtract(1);
            }
        });

        p1_subtract_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player1_subtract(5);
            }
        });

        p1_subtract.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        mAutoDecrement = true;
                        mAutoIncrement = false;
                        counterUpdateHandler.post(new CtrUpdater());
                        return true;
                    }
                }
        );

        p1_subtract.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ((event.getAction() == MotionEvent.ACTION_UP) || event.getAction() == MotionEvent.ACTION_CANCEL && mAutoDecrement) {
                    mAutoDecrement = false;
                    counterUpdateHandler.removeCallbacksAndMessages(null);
                }
                return false;
            }
        });

        p2_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player2_add(1);
                mAutoIncrement = false;}});

        p2_add_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player2_add(5);
                mAutoIncrement = false;}});

        p2_subtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player2_subtract(1);
                mAutoIncrement = false;}});

        p2_subtract_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player2_subtract(5);
                mAutoIncrement = false;}});


        p2_add.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mAutoDecrement2 = false;
                mAutoIncrement2 = true;
                counterUpdateHandler.post(new CtrUpdater());
                return true;
            }
        });

        p2_subtract.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mAutoDecrement2 = true;
                mAutoIncrement2 = false;
                counterUpdateHandler.post(new CtrUpdater());
                return true;
            }
        });

        p2_subtract.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ((event.getAction() == MotionEvent.ACTION_UP) || event.getAction() == MotionEvent.ACTION_CANCEL && mAutoDecrement2) {
                    mAutoDecrement2 = false;
                    counterUpdateHandler.removeCallbacksAndMessages(null);
                }
                return false;
            }
        });

        p2_add.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ((event.getAction() == MotionEvent.ACTION_UP) || event.getAction() == MotionEvent.ACTION_CANCEL && mAutoIncrement2) {
                    mAutoIncrement2 = false;
                    counterUpdateHandler.removeCallbacksAndMessages(null);
                }
                return false;
            }
        });

        p1_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name_dialogue(1);
                //Log.d("p1","name clicked");
            }
        });


        p2_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name_dialogue(2);
            }
        });

        diceImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAlertDialog = coinAlert();
                myAlertDialog.show();
            }
        });
    }

    public AlertDialog coinAlert(){

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                coinScreen = false;
                coinFlip = "";
            }
        });
        //builder.setTitle(R.string.dialog_dice_title);
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_dice,null);
        builder.setView(dialogView);
        coinScreen = true;

        final ImageView popupImg = (ImageView) dialogView.findViewById(R.id.popup_dice_image);
        final TextView popupText = (TextView) dialogView.findViewById(R.id.popup_dice_text);
        popupText.setText(coinFlip);

        if(coinFlip.equals("Heads")){
            popupImg.setImageResource(R.drawable.nickel);
        }else if(coinFlip.equals("Tails")){
            popupImg.setImageResource(R.drawable.nickel_tails);
        }

        popupImg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    popupImg.setAlpha(0.4f);

                }
                else if(event.getAction() == android.view.MotionEvent.ACTION_UP){
                    popupImg.setAlpha(1f);
                    if(diceRoll()==0){
                        popupText.setText("Heads");
                        popupImg.setImageResource(R.drawable.nickel);
                        coinFlip = "Heads";
                    }else{
                        popupText.setText("Tails");
                        popupImg.setImageResource(R.drawable.nickel_tails);
                        coinFlip = "Tails";
                    }
                }
                return true;
            }
        });

        AlertDialog dialog = builder.create();
        return dialog;
    }


    public int diceRoll(){
        Random r = new Random();
        int i1 = r.nextInt(2);
        return i1;
    }

    public void name_dialogue(int player){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.dialog_title);
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_name,null);
        if(player==1){
            p1_name_edit = (EditText) dialogView.findViewById(R.id.username);
            p1_name_edit.setText(p1_name.getText());
            builder.setView(dialogView).setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    p1_newName = p1_name_edit.getText().toString();
                    p1_name.setText(p1_newName);
                }})
                    .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //nothing
                        }});
        }

        if(player==2){
            p2_name_edit = (EditText) dialogView.findViewById(R.id.username);
            p2_name_edit.setText(p2_name.getText());
            builder.setView(dialogView).setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    p2_newName = p2_name_edit.getText().toString();
                    p2_name.setText(p2_newName);
                }})
                    .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //nothing
                        }});
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //functions for adding and subtracting
    public void player1_subtract(int i) {
        p1=p1-i;
        player1_hp.setText("" + p1);
        p1_hp_subtract_clicked=p1_hp_subtract_clicked+i;

        if(counter.getRunning()==false){
            counter.startTimer();
        }else{
            counter.getTimer().cancel();
            counter.startTimer();
        }

        if(p1<=0 && newGame == true){
            newGame = false;
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("New Game?")
                    .setPositiveButton("Ok",new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            newGame();
                        }
                    })
                    .setNegativeButton("Cancel",new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //nothing
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public void player1_add(int i) {
        p1=p1+i;
        player1_hp.setText("" + p1);
        p1_hp_add_clicked=p1_hp_add_clicked+i;

        if(counter.getRunning()==false) {
            counter.startTimer();
        }else{
            counter.getTimer().cancel();
            counter.startTimer();
        }
    }

    public void player2_add(int i){
        p2=p2+i;
        player2_hp.setText(""+p2);
        p2_hp_add_clicked=p2_hp_add_clicked+i;

        if(counter.getRunning()==false) {
            counter.startTimer();
        }else{
            counter.getTimer().cancel();
            counter.startTimer();
        }

    }

    public void player2_subtract(int i) {
        p2=p2-i;
        player2_hp.setText("" + p2);
        p2_hp_subtract_clicked=p2_hp_subtract_clicked+i;

        if(counter.getRunning()==false){
            counter.startTimer();
        }else{
            counter.getTimer().cancel();
            counter.startTimer();
        }

        if(p2<=0 && newGame == true){
            newGame = false;
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("New Game?")
                    .setPositiveButton("Ok",new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            newGame();
                        }
                    })
                    .setNegativeButton("Cancel",new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
    //thread handler for button holding
    class CtrUpdater implements Runnable {
        @Override
        public void run() {
            if (mAutoIncrement) {
                player1_add(1);
                counterUpdateHandler.postDelayed(new CtrUpdater(), DELAY);
            }
            if(mAutoIncrement2){
                player2_add(1);
                counterUpdateHandler.postDelayed(new CtrUpdater(), DELAY);
            }
            if(mAutoDecrement2){
                player2_subtract(1);
                counterUpdateHandler.postDelayed(new CtrUpdater(), DELAY);
            }
            else if (mAutoDecrement) {
                player1_subtract(1);
                counterUpdateHandler.postDelayed(new CtrUpdater(), DELAY);
            }
        }
    }

    public void newGame(){
            player1_hp.setText("20");
            player2_hp.setText("20");
            p1=20;
            p2=20;
            p1_currentHp=20;
            p2_currentHp=20;
            dmgText1.setText("");
            dmgText2.setText("");
            p1_dmg_hist.clear();
            p2_dmg_hist.clear();

    }

    public class hpCounter
    {
        private boolean started = false;
        private CountDownTimer timer;

        public void startTimer(){

            started = true;
            timer = new CountDownTimer(2000, 1000) {

                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {
                    started = false;

                    if(p1_hp_add_clicked >0 || p1_hp_subtract_clicked >0) {

                        int i = p1_hp_add_clicked - p1_hp_subtract_clicked;
                        if(i>0){
                            dmgText1.append("+" + i + ", ");
                            p1_hp_add_clicked = 0;
                            p1_hp_subtract_clicked = 0;
                        }else{
                            dmgText1.append(i + ", ");
                            p1_hp_add_clicked = 0;
                            p1_hp_subtract_clicked = 0;
                        }

                        p1_dmg_hist.add(i);
                        Log.d("array", p1_dmg_hist.toString());
                    }

                    if(p2_hp_add_clicked >0 || p2_hp_subtract_clicked >0) {

                        int i = p2_hp_add_clicked - p2_hp_subtract_clicked;
                        if(i>0){
                            dmgText2.append("+" + i + ", ");
                            p2_hp_add_clicked = 0;
                            p2_hp_subtract_clicked = 0;
                        }else{
                            dmgText2.append(i + ", ");
                            p2_hp_add_clicked = 0;
                            p2_hp_subtract_clicked = 0;
                        }
                        p2_dmg_hist.add(i);
                        Log.d("array", p2_dmg_hist.toString());
                    }
                }
            };
            timer.start();
        }


        public boolean getRunning(){
            return started;
        }

        public CountDownTimer getTimer(){
            return timer;
        }
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("p1_savedHP",p1);
        savedInstanceState.putInt("p2_savedHP",p2);
        savedInstanceState.putString("p1_savedName",p1_newName);
        savedInstanceState.putString("p2_savedName",p2_newName);
        savedInstanceState.putIntegerArrayList("p1_dmg_list",p1_dmg_hist);
        savedInstanceState.putIntegerArrayList("p2_dmg_list",p2_dmg_hist);
        savedInstanceState.putBoolean("coinScreen",coinScreen);
        savedInstanceState.putString("coinFlip",coinFlip);

        if(myAlertDialog != null && coinScreen == true){
            myAlertDialog.dismiss();
        }
    }

}
