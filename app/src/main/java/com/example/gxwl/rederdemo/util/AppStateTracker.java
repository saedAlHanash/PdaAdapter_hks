package com.example.gxwl.rederdemo.util;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

public class AppStateTracker {
  public static final int STATE_BACKGROUND = 1;
  
  public static final int STATE_FOREGROUND = 0;
  
  private static int currentState;
  
  public static int getCurrentState() {
    return currentState;
  }
  
  public static void track(Application paramApplication, final AppStateChangeListener appStateChangeListener) {
    paramApplication.registerActivityLifecycleCallbacks(new SimpleActivityLifecycleCallbacks() {
          private int resumeActivityCount = 0;
          
          public void onActivityStarted(Activity param1Activity) {
            if (this.resumeActivityCount == 0) {
              appStateChangeListener.appTurnIntoForeground();
            } 
            this.resumeActivityCount++;
          }
          
          public void onActivityStopped(Activity param1Activity) {
            int i = this.resumeActivityCount - 1;
            this.resumeActivityCount = i;
            if (i == 0) {
              appStateChangeListener.appTurnIntoBackGround();
            } 
          }
        });
  }
  
  public  interface AppStateChangeListener {
    void appTurnIntoBackGround();
    
    void appTurnIntoForeground();
  }
  
  private static class SimpleActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
    private SimpleActivityLifecycleCallbacks() {}
    
    public void onActivityCreated(Activity param1Activity, Bundle param1Bundle) {}
    
    public void onActivityDestroyed(Activity param1Activity) {}
    
    public void onActivityPaused(Activity param1Activity) {}
    
    public void onActivityResumed(Activity param1Activity) {}
    
    public void onActivitySaveInstanceState(Activity param1Activity, Bundle param1Bundle) {}
    
    public void onActivityStarted(Activity param1Activity) {}
    
    public void onActivityStopped(Activity param1Activity) {}
  }
}
