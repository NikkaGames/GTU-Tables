package ge.nikka.gtutable;

import android.graphics.*;
import android.os.*;
import java.lang.*;
import android.widget.*;

public class Singleton {
    private static Singleton instance;
    
    private String tdata = "";
    private String myUid = "";
    
    private Singleton() {
        
    }
    
    public void setData(String dat) {
        this.tdata = dat;
    }
    
    public void setTableUid(String uuid) {
        this.myUid = uuid;
    }
    
    public String getMyTable() {
        return this.myUid;
    }
    
    public String getData() {
        return this.tdata;
    }
    
    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
 }