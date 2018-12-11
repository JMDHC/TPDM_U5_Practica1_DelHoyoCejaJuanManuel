package mx.edu.ittepic.tpdm_u5_practica1_delhoyocejajuanmanuel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Toast;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    float x=100,y=100;
    int radio=80, num_color=(int)(Math.random()*8);
    int color[] = {Color.BLUE,Color.BLACK, Color.GRAY, Color.GREEN, Color.RED, Color.YELLOW, Color.CYAN, Color.MAGENTA};
    CanvasView canvas;
    Handler handler;
    Timer timer;
    SensorManager sensormanager;
    Sensor acelerometro;
    Sensor gyroscopio;
    float sensorx=0,sensory=0;
    Display display;
    Point tamaño;
    SensorEventListener gyroscopioListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toast.makeText(MainActivity.this,"Mueve el telefono para mover la bolita, Agitalo para cambiar el color de la bolita",Toast.LENGTH_LONG).show();

        sensormanager = (SensorManager) getSystemService(SENSOR_SERVICE);
        acelerometro= sensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscopio= sensormanager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensormanager.registerListener(this,acelerometro,SensorManager.SENSOR_DELAY_NORMAL);
        sensormanager.registerListener(this,gyroscopio,SensorManager.SENSOR_DELAY_NORMAL);

        canvas = new CanvasView(MainActivity.this);
        setContentView(canvas);

        display = getWindowManager().getDefaultDisplay();
        tamaño=new Point();
        display.getSize(tamaño);

        handler = new Handler(){
            @Override
            public void handleMessage(Message message){
                canvas.invalidate();
            }
        };

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                x-=sensorx;
                y+=sensory;
                if(x<=radio) x+=10;
                if(y<=radio) y+=10;
                if(x>=tamaño.x-(radio/2)) x-=10;
                if(y>=tamaño.y-(radio/2)) y-=10;

                handler.sendEmptyMessage(0);
            }
        },0,30);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if(sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            sensorx=event.values[0];
            sensory=event.values[1];
        }
        else if (sensor.getType()==Sensor.TYPE_GYROSCOPE){
            if(event.values[2]>0.8f)
                num_color=(int)(Math.random()*8);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private class CanvasView extends View {
        private Paint p;
        public CanvasView(Context context){
            super(context);
            setFocusable(true);
            p=new Paint();
        }
        public void onDraw(Canvas c){
            p.setStyle(Paint.Style.FILL);
            p.setAntiAlias(true);
            p.setTextSize(30f);
            p.setColor(color[num_color]);
            c.drawCircle(x,y,radio,p);
        }
    }

}
