package counter.step.ro.stepcounter;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity implements SensorEventListener, StepListener {

    private TextView textView;
    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private final String TEXT_NUM_STEPS = "Number of Steps: ";
    private int numSteps;

    private TextView tvSteps;
    private Button btnStart;
    private Button btnStop;
    private Button btnViewChart;
    private ProgressBar progressBar;
    private ListView listView;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference db;
    String today;
    DatabaseReference d1;

    Date d;
    SimpleDateFormat sdf;
    HashMap s;

    String string;
    HashMap<String, Integer> stepsPerDay = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        db = firebaseDatabase.getReference();

        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        d = Calendar.getInstance().getTime();
        today = sdf.format(d);

        db.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(getClass().getName(), "Data changed!!!!!");
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Object value =  data.getValue();
                    Log.d(getClass().getName(), "Value is: " + value);
                    Log.d(getClass().getName(), "children: "+data.getChildren());
//                    for (DataSnapshot children : data.getChildren()) {
////                        stepsPerDay.put(children.)
//                        Log.d("!!!!!!!!!!!", "asda" + children.getValue());
//                    }

                    Iterable children = data.getChildren();
                    while (children.iterator().hasNext()) {
                        string = children.iterator().next().toString();
                        Log.d(getClass().getName(), "show children: " + string);
                        String[] content = string.split("=");
                        String[] content2 = content[1].split(",");
                        Log.d(getClass().getName(), "zile " + content2[0]);
                        String[] content3 = content[3].split(",");
                        Log.d(getClass().getName(), "pasi " + content3[0]);
                        stepsPerDay.put(content2[0], Integer.parseInt(content3[0]));



                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(getClass().getName(), "Failed to read value.", error.toException());
            }
        });

        setContentView(R.layout.activity_main);

        // Get an instance of the SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);

        tvSteps = (TextView) findViewById(R.id.tv_steps);
        btnStart = (Button) findViewById(R.id.btn_start);
        btnStop = (Button) findViewById(R.id.btn_stop);
        btnViewChart = (Button) findViewById(R.id.btn_stop);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
//        progressBar.setMin(0);
        progressBar.setMax(50);



        btnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                numSteps = 0;
                sensorManager.registerListener(MainActivity.this, accel, SensorManager.SENSOR_DELAY_FASTEST);

            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sensorManager.unregisterListener(MainActivity.this);
            }
        });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    public boolean isNewDay() {
        String current = sdf.format(Calendar.getInstance().getTime());
        return !today.equals(current);
    }

    @Override
    public void step(long timeNs) {
        //check if it is a new day
        if (isNewDay()) {
            numSteps = 0;
            d = Calendar.getInstance().getTime();
            today = sdf.format(d);
        } else {
            numSteps++;
        }

        progressBar.setProgress(numSteps);

        //display the steps in Logcat
        Steps steps = new Steps(numSteps);
        Log.d(getClass().getName(), "Number of steps: " + numSteps);
        tvSteps.setText(TEXT_NUM_STEPS + numSteps);  // send the steps in android interface

        if(numSteps > steps.THRESHOLD){
            Log.d(getClass().getName(), "Target reached");
        }

        s = new HashMap<String, Object>();
        s.put("", today);

        db.child("steps").child(today).setValue(steps);  // saves the steps in db
        db.child("steps").updateChildren(s); // update a new day
    }
}
