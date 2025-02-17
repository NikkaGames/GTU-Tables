
package ge.nikka.gtutable;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.*;
import android.os.Handler;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.appcompat.app.*;
import android.os.Bundle;
import androidx.core.content.res.ResourcesCompat;
import androidx.loader.content.Loader;
import com.google.android.material.button.*;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import ge.nikka.gtutable.databinding.ActivityMainBinding;
import java.util.concurrent.locks.*;

public class MainActivity extends Activity {
    private ActivityMainBinding binding;
    
    public static Activity currentActivity;
    
    private native String getData(String tid);
    private native void collect();
    private native String checkk(String m);
    private native String getPrg();
    private ValueAnimator anim, anim2;
    private boolean editable = true;
    private Lock lock = new ReentrantLock();
    public static SharedPreferences prefs;
    private static String progr = "Loading…";
    
    public static void setProg(String val) {
        //progr = val;
    }
    
    public static void setDone(String val) {
        //done = val;
    }
    
    private boolean isInternetAvailable() {
       ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
       NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
       return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentActivity = this;
        System.loadLibrary("yoriichi");
        requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 9876);
        
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        prefs = getSharedPreferences("Table", MODE_PRIVATE);
        
        TextView htext = (TextView)findViewById(R.id.hlabel);
        htext.setTypeface(ResourcesCompat.getFont(MainActivity.this, R.font.googlebold));
        
        EditTextCursorWatcher code = (EditTextCursorWatcher)findViewById(R.id.loginEdit);
        
        if (prefs.contains("table_id")) code.setText(prefs.getString("table_id", null));
        code.setEnabled(editable);
        
        MaterialCheckBox check = (MaterialCheckBox)findViewById(R.id.saveBox);
        check.setTypeface(ResourcesCompat.getFont(MainActivity.this, R.font.googlebold));
        if (prefs.contains("checkbox")) check.setChecked(prefs.getBoolean("checkbox", true));
        if (!check.isChecked()) code.setText(null);
        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ValueAnimator anim21 = ValueAnimator.ofFloat(1f, 1.02f);
                anim21.setDuration(100);
                anim21.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        check.setScaleX((Float)animation.getAnimatedValue());
                        check.setScaleY((Float)animation.getAnimatedValue());
                    }
                });
                anim21.setRepeatCount(1);
                anim21.setRepeatMode(ValueAnimator.REVERSE);
                anim21.start();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("checkbox", isChecked);
                editor.apply();
                if (isChecked) {
                    if (prefs.contains("table_id")) code.setText(prefs.getString("table_id", null));
                }
            }
        });
        
        MaterialButton btn = (MaterialButton)findViewById(R.id.loginBtn);
        btn.setTypeface(ResourcesCompat.getFont(MainActivity.this, R.font.sc_ccbackbeatregular));
        btn.setTextSize(18);
        btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ValueAnimator anim2 = ValueAnimator.ofFloat(1f, 1.1f);
                anim2.setDuration(100);
                anim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        btn.setScaleX((Float)animation.getAnimatedValue());
                        btn.setScaleY((Float)animation.getAnimatedValue());
                    }
                });
                anim2.setRepeatCount(1);
                anim2.setRepeatMode(ValueAnimator.REVERSE);
                if (event.getAction() == MotionEvent.ACTION_DOWN)    
                anim2.start();
                return false;
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if (isInternetAvailable()) {
                dismissKeyboard(MainActivity.this);
                ValueAnimator anim2 = ValueAnimator.ofFloat(1f, 0f);
                anim2.setDuration(249);
                anim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        MainActivity.this.binding.getRoot().setAlpha((Float)animation.getAnimatedValue());
                    }
                });
                anim2.setRepeatCount(0);
                //anim2.setRepeatMode(ValueAnimator.REVERSE);
                anim2.start();
                MaterialAlertDialogBuilder bld = new MaterialAlertDialogBuilder(MainActivity.this);
                bld.setTitle("Please wait…").setCancelable(false).setView(R.layout.prog).create();
                AlertDialog tar = bld.show();
                TextView prt = (TextView)tar.getWindow().findViewById(R.id.dlt);
                prt.setTypeface(ResourcesCompat.getFont(MainActivity.this, R.font.googlebold));
                LinearProgressIndicator pid = (LinearProgressIndicator)tar.getWindow().findViewById(R.id.hbar);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            if (prt != null && pid != null) {
                                String prs = getPrg();        
                                if (prs.length() != 0) {
                                    int pv = Integer.parseInt(prs);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            prt.setText("Fetching data… " + prs + "%");
                                            if (pv >= 10 && pv < 80) {
                                                pid.setIndeterminate(false);
                                                pid.setProgress(pv);        
                                            } else if (pv >= 80) pid.setIndeterminate(true);            
                                        }
                                    });
                                    if (checkk("L").contains("done")) {
                                        break;
                                    }
                                }
                            }
                            try {
                                Thread.sleep(25);
                            } catch (InterruptedException ex) {}
                        }
                    }
                }).start();            
                collect();    
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
					@Override
                    public void run() {
                        new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (true) {
                                if (checkk("T").contains("done")) {
                                    break;                
                                }
                                try {
                                    Thread.sleep(25);
                                } catch (InterruptedException ex) {}
                            }
                            runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
                                   String ddata = getData(code.getText().toString());
                        if (ddata.equals("NOT_FOUND")) {
                            ValueAnimator anim5 = ValueAnimator.ofFloat(0f, 1f);
                            anim5.setDuration(249);
                            anim5.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    MainActivity.this.binding.getRoot().setAlpha((Float)animation.getAnimatedValue());
                                }
                            });
                            anim5.setRepeatCount(0);
                            //anim2.setRepeatMode(ValueAnimator.REVERSE);
                            anim5.start();
                            Toast.makeText(MainActivity.this, "Table " + code.getText().toString() + " not found!", Toast.LENGTH_SHORT).show();
                            Singleton.getInstance().setTableUid("");   
                            Singleton.getInstance().setData("");
                            tar.dismiss();
                        } else {
                            if (check.isChecked()) {
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("table_id", code.getText().toString());
                                editor.apply();
                            }        
                            Singleton.getInstance().setTableUid(code.getText().toString());   
                            Singleton.getInstance().setData(ddata);
                            MainActivity.this.startActivity(new Intent(MainActivity.this, Table.class));
                            tar.dismiss();        
                        }
                               }
                            });
                    }
                }).start();        
                        
                    }
                }, 250);    
              } else {
                  Toast.makeText(MainActivity.this, "No internet connection!", Toast.LENGTH_LONG).show();
              }
            }
        });
        anim2 = ValueAnimator.ofFloat(0f, 1f);
        anim2.setDuration(500);
        anim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                MainActivity.this.binding.getRoot().setAlpha((Float)animation.getAnimatedValue());
            }
        });
        anim2.setRepeatCount(0);
        anim2.start();
        
        anim = ValueAnimator.ofFloat(1.2f, 1f);
        anim.setDuration(500);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                binding.getRoot().setScaleX((Float)animation.getAnimatedValue());
                binding.getRoot().setScaleY((Float)animation.getAnimatedValue());
            }
        });
        anim.setRepeatCount(0);
        //anim.setRepeatMode(ValueAnimator.REVERSE);
        anim.start();
    }
    
    public static void dismissKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != activity.getCurrentFocus()) imm.hideSoftInputFromWindow(activity.getCurrentFocus().getApplicationWindowToken(), 0);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (anim != null) anim.start();
        if (anim2 != null) anim2.start();
    }
    
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }
}
