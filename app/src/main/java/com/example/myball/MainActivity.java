package com.example.myball;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import static com.example.myball.R.drawable.bg;


public class MainActivity extends AppCompatActivity implements Runnable {

    private ImageView imageView;
    private ImageView imageView_hole;
    private SensorManager sm = null;
    private Sensor s = null;
    //   球的坐标
    private int leftX = 0;
    private int topY = 0;
    //    中心坐标
    private final int leftX_CENTER = 425;
    private final int topY_CENTER = 754;
    //    最大坐标
    private final int leftX_MAX = 800;
    private final int topY_MAX = 1600;
    //    X,Y,Z上的重力坐标
    private float gX = 0;
    private float gY = 0;
    private float gZ = 0;

//洞的坐标
    private int leftX_hole = 0;
    private int topY_hole = 0;

    private final int r_hole  = 30;
    private boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        imageView_hole = (ImageView) findViewById(R.id.hole);
        leftX_hole = (new Random()).nextInt(800);
        topY_hole = (new Random()).nextInt(1600);
        setLayoutX(imageView_hole,leftX_hole,topY_hole);

        imageView = (ImageView) findViewById(R.id.ball);
        leftX = leftX_CENTER;
        topY = topY_CENTER;
        setLayoutX(imageView, leftX, topY);
        imageView.setFocusable(true);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        s = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        final Thread thread = new Thread(this);
        thread.start();
        SensorEventListener sel = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent e) {

                gX = e.values[SensorManager.DATA_X];
                gY = e.values[SensorManager.DATA_Y];
                gZ = e.values[SensorManager.DATA_Z];

                leftX += (gX * 10);
                topY -= (gY * 10);

                if (leftX < 0) {
                    leftX = 0;
                } else if (leftX > leftX_MAX) {
                    leftX = leftX_MAX;
                }
                if (topY < 0) {
                    topY = 50;
                } else if (topY > topY_MAX) {
                    topY = topY_MAX;
                }
                setLayoutX(imageView, leftX, topY);
                double dis = Math.sqrt((leftX-leftX_hole)*(leftX-leftX_hole)+(topY-topY_hole)*(topY-topY_hole));
                if(dis <= r_hole){
                    imageView.setVisibility(View.GONE);
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                    builder.setTitle("");
                    builder.setMessage("小老板，有点东西！");
                    builder.setNegativeButton("走了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            Toast.makeText(MainActivity.this, "点击了确定 " + which, Toast.LENGTH_SHORT).show();
                            System.exit(0);
                        }
                    });
                    builder.setPositiveButton("再来", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            Toast.makeText(MainActivity.this, "点击了取消 " + which, Toast.LENGTH_SHORT).show();
                            restartApp();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        sm.registerListener(sel, s, SensorManager.SENSOR_DELAY_GAME);

    }

    private static void setLayoutX(View view, int left, int top) {
        ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(view.getLayoutParams());
        margin.setMargins(left, top, margin.rightMargin, margin.bottomMargin);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
        view.setLayoutParams(layoutParams);
    }


    @Override
    public void run() {

        while (isRunning) {
            try {
                setLayoutX(imageView, leftX, topY);
                Thread.sleep(50);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void restartApp() {
        final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
