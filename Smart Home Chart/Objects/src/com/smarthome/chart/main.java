package com.smarthome.chart;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = true;
	public static final boolean includeTitle = false;
    public static WeakReference<Activity> previousOne;
    public static boolean dontPause;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mostCurrent = this;
		if (processBA == null) {
			processBA = new BA(this.getApplicationContext(), null, null, "com.smarthome.chart", "com.smarthome.chart.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.setActivityPaused(true);
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(this, processBA, wl, false))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "com.smarthome.chart", "com.smarthome.chart.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.smarthome.chart.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEventFromUI(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null)
            return;
        if (this != mostCurrent)
			return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        if (!dontPause)
            BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        else
            BA.LogInfo("** Activity (main) Pause event (activity is not paused). **");
        if (mostCurrent != null)
            processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        if (!dontPause) {
            processBA.setActivityPaused(true);
            mostCurrent = null;
        }

        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
            main mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
            if (mc != mostCurrent)
                return;
		    processBA.raiseEvent(mc._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        for (int i = 0;i < permissions.length;i++) {
            Object[] o = new Object[] {permissions[i], grantResults[i] == 0};
            processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
        }
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.objects.B4XViewWrapper.XUI _xui = null;
public static anywheresoftware.b4a.phone.Phone.PhoneWakeState _awake = null;
public static anywheresoftware.b4a.objects.Timer _temperaturehourlytimer = null;
public static anywheresoftware.b4a.objects.Timer _humidityhourlytimer = null;
public static anywheresoftware.b4a.objects.Timer _temperaturedailytimer = null;
public static anywheresoftware.b4a.objects.Timer _humiditydailytimer = null;
public androidplotwrapper.lineChartWrapper _linechart = null;
public static String _am12 = "";
public static String _am1 = "";
public static String _am2 = "";
public static String _am3 = "";
public static String _am4 = "";
public static String _am5 = "";
public static String _am6 = "";
public static String _am7 = "";
public static String _am8 = "";
public static String _am9 = "";
public static String _am10 = "";
public static String _am11 = "";
public static String _pm12 = "";
public static String _pm1 = "";
public static String _pm2 = "";
public static String _pm3 = "";
public static String _pm4 = "";
public static String _pm5 = "";
public static String _pm6 = "";
public static String _pm7 = "";
public static String _pm8 = "";
public static String _pm9 = "";
public static String _pm10 = "";
public static String _pm11 = "";
public static String _temprightnow = "";
public static long _timerightnow = 0L;
public static String[] _timearray = null;
public static float _zerorange = 0f;
public static float _tempzerorange = 0f;
public static float _tempmaxrange = 0f;
public static float _tempminrange = 0f;
public anywheresoftware.b4a.objects.ButtonWrapper _btnhumidityhourly = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btntemphourly = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnhumiditydaily = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btntempdaily = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel1 = null;
public b4a.example.dateutils _dateutils = null;
public com.smarthome.chart.starter _starter = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 69;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 70;BA.debugLine="Activity.LoadLayout(\"Main\")";
mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 //BA.debugLineNum = 72;BA.debugLine="btnTempHourly_Click";
_btntemphourly_click();
 //BA.debugLineNum = 73;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 79;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 80;BA.debugLine="Awake.ReleaseKeepAlive";
_awake.ReleaseKeepAlive();
 //BA.debugLineNum = 92;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 75;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 76;BA.debugLine="Awake.KeepAlive(True)";
_awake.KeepAlive(processBA,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 77;BA.debugLine="End Sub";
return "";
}
public static String  _activity_windowfocuschanged(boolean _hasfocus) throws Exception{
anywheresoftware.b4j.object.JavaObject _jo = null;
 //BA.debugLineNum = 2244;BA.debugLine="Sub Activity_WindowFocusChanged(HasFocus As Boolea";
 //BA.debugLineNum = 2245;BA.debugLine="If HasFocus Then";
if (_hasfocus) { 
 //BA.debugLineNum = 2246;BA.debugLine="Try";
try { //BA.debugLineNum = 2247;BA.debugLine="Dim jo As JavaObject = Activity";
_jo = new anywheresoftware.b4j.object.JavaObject();
_jo = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(mostCurrent._activity.getObject()));
 //BA.debugLineNum = 2249;BA.debugLine="jo.RunMethod(\"setSystemUiVisibility\", Array As";
_jo.RunMethod("setSystemUiVisibility",new Object[]{(Object)(5894)});
 } 
       catch (Exception e6) {
			processBA.setLastException(e6); };
 };
 //BA.debugLineNum = 2255;BA.debugLine="End Sub";
return "";
}
public static String  _btnhumiditydaily_click() throws Exception{
 //BA.debugLineNum = 127;BA.debugLine="Sub btnHumidityDaily_Click";
 //BA.debugLineNum = 128;BA.debugLine="TemperatureHourlyTimer.Enabled = False";
_temperaturehourlytimer.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 129;BA.debugLine="HumidityHourlyTimer.Enabled = False";
_humidityhourlytimer.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 130;BA.debugLine="TemperatureDailyTimer.Enabled = False";
_temperaturedailytimer.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 131;BA.debugLine="tempMaxRange=0";
_tempmaxrange = (float) (0);
 //BA.debugLineNum = 132;BA.debugLine="tempMinRange=0";
_tempminrange = (float) (0);
 //BA.debugLineNum = 133;BA.debugLine="HumidityDailyCreate";
_humiditydailycreate();
 //BA.debugLineNum = 134;BA.debugLine="HumidityDailyTimer.Initialize(\"HumidityDailyTimer";
_humiditydailytimer.Initialize(processBA,"HumidityDailyTimer",(long) (1000));
 //BA.debugLineNum = 135;BA.debugLine="HumidityDailyTimer.Enabled = True 'start timer";
_humiditydailytimer.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 136;BA.debugLine="End Sub";
return "";
}
public static String  _btnhumidityhourly_click() throws Exception{
 //BA.debugLineNum = 105;BA.debugLine="Sub btnHumidityHourly_Click";
 //BA.debugLineNum = 106;BA.debugLine="TemperatureDailyTimer.Enabled = False";
_temperaturedailytimer.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 107;BA.debugLine="TemperatureHourlyTimer.Enabled = False";
_temperaturehourlytimer.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 108;BA.debugLine="HumidityDailyTimer.Enabled = False";
_humiditydailytimer.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 109;BA.debugLine="tempMaxRange=0";
_tempmaxrange = (float) (0);
 //BA.debugLineNum = 110;BA.debugLine="tempMinRange=0";
_tempminrange = (float) (0);
 //BA.debugLineNum = 111;BA.debugLine="HumidityHourlyCreate";
_humidityhourlycreate();
 //BA.debugLineNum = 112;BA.debugLine="HumidityHourlyTimer.Initialize(\"HumidityHourlyTim";
_humidityhourlytimer.Initialize(processBA,"HumidityHourlyTimer",(long) (1000));
 //BA.debugLineNum = 113;BA.debugLine="HumidityHourlyTimer.Enabled = True 'start timer";
_humidityhourlytimer.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 114;BA.debugLine="End Sub";
return "";
}
public static String  _btntempdaily_click() throws Exception{
 //BA.debugLineNum = 116;BA.debugLine="Sub btnTempDaily_Click";
 //BA.debugLineNum = 117;BA.debugLine="TemperatureHourlyTimer.Enabled = False";
_temperaturehourlytimer.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 118;BA.debugLine="HumidityHourlyTimer.Enabled = False";
_humidityhourlytimer.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 119;BA.debugLine="HumidityDailyTimer.Enabled = False";
_humiditydailytimer.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 120;BA.debugLine="tempMaxRange=0";
_tempmaxrange = (float) (0);
 //BA.debugLineNum = 121;BA.debugLine="tempMinRange=0";
_tempminrange = (float) (0);
 //BA.debugLineNum = 122;BA.debugLine="TemperatureDailyCreate";
_temperaturedailycreate();
 //BA.debugLineNum = 123;BA.debugLine="TemperatureDailyTimer.Initialize(\"TemperatureDail";
_temperaturedailytimer.Initialize(processBA,"TemperatureDailyTimer",(long) (1000));
 //BA.debugLineNum = 124;BA.debugLine="TemperatureDailyTimer.Enabled = True 'start timer";
_temperaturedailytimer.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 125;BA.debugLine="End Sub";
return "";
}
public static String  _btntemphourly_click() throws Exception{
 //BA.debugLineNum = 94;BA.debugLine="Sub btnTempHourly_Click";
 //BA.debugLineNum = 95;BA.debugLine="TemperatureDailyTimer.Enabled = False";
_temperaturedailytimer.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 96;BA.debugLine="HumidityHourlyTimer.Enabled = False";
_humidityhourlytimer.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 97;BA.debugLine="HumidityDailyTimer.Enabled = False";
_humiditydailytimer.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 98;BA.debugLine="tempMaxRange=0";
_tempmaxrange = (float) (0);
 //BA.debugLineNum = 99;BA.debugLine="tempMinRange=0";
_tempminrange = (float) (0);
 //BA.debugLineNum = 100;BA.debugLine="TemperatureHourlyCreate";
_temperaturehourlycreate();
 //BA.debugLineNum = 101;BA.debugLine="TemperatureHourlyTimer.Initialize(\"TemperatureHou";
_temperaturehourlytimer.Initialize(processBA,"TemperatureHourlyTimer",(long) (1000));
 //BA.debugLineNum = 102;BA.debugLine="TemperatureHourlyTimer.Enabled = True 'start time";
_temperaturehourlytimer.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 103;BA.debugLine="End Sub";
return "";
}
public static String  _checktempboundaries() throws Exception{
anywheresoftware.b4a.objects.collections.List _templist = null;
float _minvalue = 0f;
float _maxvalue = 0f;
 //BA.debugLineNum = 2042;BA.debugLine="Sub CheckTempBoundaries";
 //BA.debugLineNum = 2043;BA.debugLine="Dim tempList As List";
_templist = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 2044;BA.debugLine="tempList.Initialize";
_templist.Initialize();
 //BA.debugLineNum = 2045;BA.debugLine="tempList.AddAll(Array As Float (am12, am1, am2, a";
_templist.AddAll(anywheresoftware.b4a.keywords.Common.ArrayToList(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5)),(float)(Double.parseDouble(mostCurrent._pm6)),(float)(Double.parseDouble(mostCurrent._pm7)),(float)(Double.parseDouble(mostCurrent._pm8)),(float)(Double.parseDouble(mostCurrent._pm9)),(float)(Double.parseDouble(mostCurrent._pm10)),(float)(Double.parseDouble(mostCurrent._pm11))}));
 //BA.debugLineNum = 2046;BA.debugLine="tempList.Sort(True)";
_templist.Sort(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2049;BA.debugLine="tempZeroRange = tempList.Get(0)-1.5";
_tempzerorange = (float) ((double)(BA.ObjectToNumber(_templist.Get((int) (0))))-1.5);
 //BA.debugLineNum = 2051;BA.debugLine="If am12 = zeroRange Then am12 = tempZeroRange";
if ((mostCurrent._am12).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._am12 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2052;BA.debugLine="If am1 = zeroRange Then am1 = tempZeroRange";
if ((mostCurrent._am1).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._am1 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2053;BA.debugLine="If am2 = zeroRange Then am2 = tempZeroRange";
if ((mostCurrent._am2).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._am2 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2054;BA.debugLine="If am3 = zeroRange Then am3 = tempZeroRange";
if ((mostCurrent._am3).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._am3 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2055;BA.debugLine="If am4 = zeroRange Then am4 = tempZeroRange";
if ((mostCurrent._am4).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._am4 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2056;BA.debugLine="If am5 = zeroRange Then am5 = tempZeroRange";
if ((mostCurrent._am5).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._am5 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2057;BA.debugLine="If am6 = zeroRange Then am6 = tempZeroRange";
if ((mostCurrent._am6).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._am6 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2058;BA.debugLine="If am7 = zeroRange Then am7 = tempZeroRange";
if ((mostCurrent._am7).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._am7 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2059;BA.debugLine="If am8 = zeroRange Then am8 = tempZeroRange";
if ((mostCurrent._am8).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._am8 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2060;BA.debugLine="If am9 = zeroRange Then am9 = tempZeroRange";
if ((mostCurrent._am9).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._am9 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2061;BA.debugLine="If am10 = zeroRange Then am10 = tempZeroRange";
if ((mostCurrent._am10).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._am10 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2062;BA.debugLine="If am11 = zeroRange Then am11 = tempZeroRange";
if ((mostCurrent._am11).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._am11 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2063;BA.debugLine="If pm12 = zeroRange Then pm12 = tempZeroRange";
if ((mostCurrent._pm12).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._pm12 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2064;BA.debugLine="If pm1 = zeroRange Then pm1 = tempZeroRange";
if ((mostCurrent._pm1).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._pm1 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2065;BA.debugLine="If pm2 = zeroRange Then pm2 = tempZeroRange";
if ((mostCurrent._pm2).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._pm2 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2066;BA.debugLine="If pm3 = zeroRange Then pm3 = tempZeroRange";
if ((mostCurrent._pm3).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._pm3 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2067;BA.debugLine="If pm4 = zeroRange Then pm4 = tempZeroRange";
if ((mostCurrent._pm4).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._pm4 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2068;BA.debugLine="If pm5 = zeroRange Then pm5 = tempZeroRange";
if ((mostCurrent._pm5).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._pm5 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2069;BA.debugLine="If pm6 = zeroRange Then pm6 = tempZeroRange";
if ((mostCurrent._pm6).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._pm6 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2070;BA.debugLine="If pm7 = zeroRange Then pm7 = tempZeroRange";
if ((mostCurrent._pm7).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._pm7 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2071;BA.debugLine="If pm8 = zeroRange Then pm8 = tempZeroRange";
if ((mostCurrent._pm8).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._pm8 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2072;BA.debugLine="If pm9 = zeroRange Then pm9 = tempZeroRange";
if ((mostCurrent._pm9).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._pm9 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2073;BA.debugLine="If pm10 = zeroRange Then pm10 = tempZeroRange";
if ((mostCurrent._pm10).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._pm10 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2074;BA.debugLine="If pm11 = zeroRange Then pm11 = tempZeroRange";
if ((mostCurrent._pm11).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._pm11 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2076;BA.debugLine="tempList.Initialize";
_templist.Initialize();
 //BA.debugLineNum = 2077;BA.debugLine="tempList.AddAll(Array As Float (am12, am1, am2, a";
_templist.AddAll(anywheresoftware.b4a.keywords.Common.ArrayToList(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5)),(float)(Double.parseDouble(mostCurrent._pm6)),(float)(Double.parseDouble(mostCurrent._pm7)),(float)(Double.parseDouble(mostCurrent._pm8)),(float)(Double.parseDouble(mostCurrent._pm9)),(float)(Double.parseDouble(mostCurrent._pm10)),(float)(Double.parseDouble(mostCurrent._pm11))}));
 //BA.debugLineNum = 2078;BA.debugLine="tempList.Sort(True)";
_templist.Sort(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2080;BA.debugLine="Dim minValue=0, maxValue=0 As Float";
_minvalue = (float) (0);
_maxvalue = (float) (0);
 //BA.debugLineNum = 2081;BA.debugLine="If tempRightNow <= tempList.Get(0) Then";
if ((double)(Double.parseDouble(mostCurrent._temprightnow))<=(double)(BA.ObjectToNumber(_templist.Get((int) (0))))) { 
 //BA.debugLineNum = 2082;BA.debugLine="minValue = tempRightNow-1.5";
_minvalue = (float) ((double)(Double.parseDouble(mostCurrent._temprightnow))-1.5);
 }else {
 //BA.debugLineNum = 2084;BA.debugLine="minValue = tempList.Get(0)-1.5";
_minvalue = (float) ((double)(BA.ObjectToNumber(_templist.Get((int) (0))))-1.5);
 };
 //BA.debugLineNum = 2087;BA.debugLine="If tempList.Get(tempList.Size-1) >= 88.88 Then";
if ((double)(BA.ObjectToNumber(_templist.Get((int) (_templist.getSize()-1))))>=88.88) { 
 //BA.debugLineNum = 2088;BA.debugLine="If tempRightNow >= (tempList.Get(tempList.Size-2";
if ((double)(Double.parseDouble(mostCurrent._temprightnow))>=(double)(BA.ObjectToNumber((_templist.Get((int) (_templist.getSize()-2)))))) { 
 //BA.debugLineNum = 2089;BA.debugLine="maxValue = tempRightNow+1.5";
_maxvalue = (float) ((double)(Double.parseDouble(mostCurrent._temprightnow))+1.5);
 }else {
 //BA.debugLineNum = 2091;BA.debugLine="maxValue = (tempList.Get(tempList.Size-2))+1.5";
_maxvalue = (float) ((double)(BA.ObjectToNumber((_templist.Get((int) (_templist.getSize()-2)))))+1.5);
 };
 }else {
 //BA.debugLineNum = 2094;BA.debugLine="If tempRightNow >= (tempList.Get(tempList.Size-1";
if ((double)(Double.parseDouble(mostCurrent._temprightnow))>=(double)(BA.ObjectToNumber((_templist.Get((int) (_templist.getSize()-1)))))) { 
 //BA.debugLineNum = 2095;BA.debugLine="maxValue = tempRightNow+1.5";
_maxvalue = (float) ((double)(Double.parseDouble(mostCurrent._temprightnow))+1.5);
 }else {
 //BA.debugLineNum = 2097;BA.debugLine="maxValue = (tempList.Get(tempList.Size-1))+1.5";
_maxvalue = (float) ((double)(BA.ObjectToNumber((_templist.Get((int) (_templist.getSize()-1)))))+1.5);
 };
 };
 //BA.debugLineNum = 2100;BA.debugLine="LineChart.YaxisRange(minValue, maxValue)";
mostCurrent._linechart.YaxisRange(_minvalue,_maxvalue);
 //BA.debugLineNum = 2101;BA.debugLine="End Sub";
return "";
}
public static String  _checktempboundariesdaily() throws Exception{
anywheresoftware.b4a.objects.collections.List _templist = null;
float _minvalue = 0f;
float _maxvalue = 0f;
 //BA.debugLineNum = 2103;BA.debugLine="Sub CheckTempBoundariesDaily";
 //BA.debugLineNum = 2104;BA.debugLine="Dim tempList As List";
_templist = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 2105;BA.debugLine="tempList.Initialize";
_templist.Initialize();
 //BA.debugLineNum = 2106;BA.debugLine="tempList.AddAll(Array As Float (am12, am1, am2, a";
_templist.AddAll(anywheresoftware.b4a.keywords.Common.ArrayToList(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5)),(float)(Double.parseDouble(mostCurrent._pm6)),(float)(Double.parseDouble(mostCurrent._pm7)),(float)(Double.parseDouble(mostCurrent._pm8)),(float)(Double.parseDouble(mostCurrent._pm9)),(float)(Double.parseDouble(mostCurrent._pm10)),(float)(Double.parseDouble(mostCurrent._pm11))}));
 //BA.debugLineNum = 2107;BA.debugLine="tempList.Sort(True)";
_templist.Sort(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2109;BA.debugLine="tempZeroRange = tempList.Get(0)-1.5";
_tempzerorange = (float) ((double)(BA.ObjectToNumber(_templist.Get((int) (0))))-1.5);
 //BA.debugLineNum = 2111;BA.debugLine="If am12 = zeroRange Then am12 = tempZeroRange";
if ((mostCurrent._am12).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._am12 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2112;BA.debugLine="If am1 = zeroRange Then am1 = tempZeroRange";
if ((mostCurrent._am1).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._am1 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2113;BA.debugLine="If am2 = zeroRange Then am2 = tempZeroRange";
if ((mostCurrent._am2).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._am2 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2114;BA.debugLine="If am3 = zeroRange Then am3 = tempZeroRange";
if ((mostCurrent._am3).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._am3 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2115;BA.debugLine="If am4 = zeroRange Then am4 = tempZeroRange";
if ((mostCurrent._am4).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._am4 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2116;BA.debugLine="If am5 = zeroRange Then am5 = tempZeroRange";
if ((mostCurrent._am5).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._am5 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2117;BA.debugLine="If am6 = zeroRange Then am6 = tempZeroRange";
if ((mostCurrent._am6).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._am6 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2118;BA.debugLine="If am7 = zeroRange Then am7 = tempZeroRange";
if ((mostCurrent._am7).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._am7 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2119;BA.debugLine="If am8 = zeroRange Then am8 = tempZeroRange";
if ((mostCurrent._am8).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._am8 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2120;BA.debugLine="If am9 = zeroRange Then am9 = tempZeroRange";
if ((mostCurrent._am9).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._am9 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2121;BA.debugLine="If am10 = zeroRange Then am10 = tempZeroRange";
if ((mostCurrent._am10).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._am10 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2122;BA.debugLine="If am11 = zeroRange Then am11 = tempZeroRange";
if ((mostCurrent._am11).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._am11 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2123;BA.debugLine="If pm12 = zeroRange Then pm12 = tempZeroRange";
if ((mostCurrent._pm12).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._pm12 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2124;BA.debugLine="If pm1 = zeroRange Then pm1 = tempZeroRange";
if ((mostCurrent._pm1).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._pm1 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2125;BA.debugLine="If pm2 = zeroRange Then pm2 = tempZeroRange";
if ((mostCurrent._pm2).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._pm2 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2126;BA.debugLine="If pm3 = zeroRange Then pm3 = tempZeroRange";
if ((mostCurrent._pm3).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._pm3 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2127;BA.debugLine="If pm4 = zeroRange Then pm4 = tempZeroRange";
if ((mostCurrent._pm4).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._pm4 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2128;BA.debugLine="If pm5 = zeroRange Then pm5 = tempZeroRange";
if ((mostCurrent._pm5).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._pm5 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2129;BA.debugLine="If pm6 = zeroRange Then pm6 = tempZeroRange";
if ((mostCurrent._pm6).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._pm6 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2130;BA.debugLine="If pm7 = zeroRange Then pm7 = tempZeroRange";
if ((mostCurrent._pm7).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._pm7 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2131;BA.debugLine="If pm8 = zeroRange Then pm8 = tempZeroRange";
if ((mostCurrent._pm8).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._pm8 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2132;BA.debugLine="If pm9 = zeroRange Then pm9 = tempZeroRange";
if ((mostCurrent._pm9).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._pm9 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2133;BA.debugLine="If pm10 = zeroRange Then pm10 = tempZeroRange";
if ((mostCurrent._pm10).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._pm10 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2134;BA.debugLine="If pm11 = zeroRange Then pm11 = tempZeroRange";
if ((mostCurrent._pm11).equals(BA.NumberToString(_zerorange))) { 
mostCurrent._pm11 = BA.NumberToString(_tempzerorange);};
 //BA.debugLineNum = 2136;BA.debugLine="tempList.Initialize";
_templist.Initialize();
 //BA.debugLineNum = 2137;BA.debugLine="tempList.AddAll(Array As Float (am12, am1, am2, a";
_templist.AddAll(anywheresoftware.b4a.keywords.Common.ArrayToList(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5)),(float)(Double.parseDouble(mostCurrent._pm6)),(float)(Double.parseDouble(mostCurrent._pm7)),(float)(Double.parseDouble(mostCurrent._pm8)),(float)(Double.parseDouble(mostCurrent._pm9)),(float)(Double.parseDouble(mostCurrent._pm10)),(float)(Double.parseDouble(mostCurrent._pm11))}));
 //BA.debugLineNum = 2138;BA.debugLine="tempList.Sort(True)";
_templist.Sort(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2140;BA.debugLine="Dim minValue=0, maxValue=0 As Float";
_minvalue = (float) (0);
_maxvalue = (float) (0);
 //BA.debugLineNum = 2142;BA.debugLine="If tempRightNow <= tempList.Get(0) Then";
if ((double)(Double.parseDouble(mostCurrent._temprightnow))<=(double)(BA.ObjectToNumber(_templist.Get((int) (0))))) { 
 //BA.debugLineNum = 2143;BA.debugLine="If tempMinRange <= tempRightNow Then";
if (_tempminrange<=(double)(Double.parseDouble(mostCurrent._temprightnow))) { 
 //BA.debugLineNum = 2144;BA.debugLine="minValue = tempMinRange-1.5";
_minvalue = (float) (_tempminrange-1.5);
 }else {
 //BA.debugLineNum = 2146;BA.debugLine="minValue = tempRightNow-1.5";
_minvalue = (float) ((double)(Double.parseDouble(mostCurrent._temprightnow))-1.5);
 };
 }else {
 //BA.debugLineNum = 2149;BA.debugLine="If tempMinRange > 0 And tempMinRange <= tempList";
if (_tempminrange>0 && _tempminrange<=(double)(BA.ObjectToNumber(_templist.Get((int) (0))))) { 
 //BA.debugLineNum = 2150;BA.debugLine="minValue = tempMinRange-1.5";
_minvalue = (float) (_tempminrange-1.5);
 }else {
 //BA.debugLineNum = 2152;BA.debugLine="minValue = tempList.Get(0)-1.5";
_minvalue = (float) ((double)(BA.ObjectToNumber(_templist.Get((int) (0))))-1.5);
 };
 };
 //BA.debugLineNum = 2156;BA.debugLine="If tempList.Get(tempList.Size-1) >= 88.88 Then";
if ((double)(BA.ObjectToNumber(_templist.Get((int) (_templist.getSize()-1))))>=88.88) { 
 //BA.debugLineNum = 2157;BA.debugLine="If tempRightNow >= (tempList.Get(tempList.Size-2";
if ((double)(Double.parseDouble(mostCurrent._temprightnow))>=(double)(BA.ObjectToNumber((_templist.Get((int) (_templist.getSize()-2)))))) { 
 //BA.debugLineNum = 2158;BA.debugLine="If tempMaxRange >= tempRightNow Then";
if (_tempmaxrange>=(double)(Double.parseDouble(mostCurrent._temprightnow))) { 
 //BA.debugLineNum = 2159;BA.debugLine="maxValue =  tempMaxRange+1.5";
_maxvalue = (float) (_tempmaxrange+1.5);
 }else {
 //BA.debugLineNum = 2161;BA.debugLine="maxValue = tempRightNow+1.5";
_maxvalue = (float) ((double)(Double.parseDouble(mostCurrent._temprightnow))+1.5);
 };
 }else {
 //BA.debugLineNum = 2164;BA.debugLine="If tempMaxRange >= (tempList.Get(tempList.Size-";
if (_tempmaxrange>=(double)(BA.ObjectToNumber((_templist.Get((int) (_templist.getSize()-2)))))) { 
 //BA.debugLineNum = 2165;BA.debugLine="maxValue =  tempMaxRange+1.5";
_maxvalue = (float) (_tempmaxrange+1.5);
 }else {
 //BA.debugLineNum = 2167;BA.debugLine="maxValue = (tempList.Get(tempList.Size-2))+1.5";
_maxvalue = (float) ((double)(BA.ObjectToNumber((_templist.Get((int) (_templist.getSize()-2)))))+1.5);
 };
 };
 }else {
 //BA.debugLineNum = 2171;BA.debugLine="If tempRightNow >= (tempList.Get(tempList.Size-1";
if ((double)(Double.parseDouble(mostCurrent._temprightnow))>=(double)(BA.ObjectToNumber((_templist.Get((int) (_templist.getSize()-1)))))) { 
 //BA.debugLineNum = 2172;BA.debugLine="If tempMaxRange >= tempRightNow Then";
if (_tempmaxrange>=(double)(Double.parseDouble(mostCurrent._temprightnow))) { 
 //BA.debugLineNum = 2173;BA.debugLine="maxValue =  tempMaxRange+1.5";
_maxvalue = (float) (_tempmaxrange+1.5);
 }else {
 //BA.debugLineNum = 2175;BA.debugLine="maxValue = tempRightNow+1.5";
_maxvalue = (float) ((double)(Double.parseDouble(mostCurrent._temprightnow))+1.5);
 };
 }else {
 //BA.debugLineNum = 2178;BA.debugLine="If tempMaxRange >= (tempList.Get(tempList.Size-";
if (_tempmaxrange>=(double)(BA.ObjectToNumber((_templist.Get((int) (_templist.getSize()-1)))))) { 
 //BA.debugLineNum = 2179;BA.debugLine="maxValue =  tempMaxRange+1.5";
_maxvalue = (float) (_tempmaxrange+1.5);
 }else {
 //BA.debugLineNum = 2181;BA.debugLine="maxValue = (tempList.Get(tempList.Size-1))+1.5";
_maxvalue = (float) ((double)(BA.ObjectToNumber((_templist.Get((int) (_templist.getSize()-1)))))+1.5);
 };
 };
 };
 //BA.debugLineNum = 2186;BA.debugLine="If (maxValue-1.5) >= tempMaxRange Then";
if ((_maxvalue-1.5)>=_tempmaxrange) { 
 //BA.debugLineNum = 2187;BA.debugLine="tempMaxRange = maxValue-1.5";
_tempmaxrange = (float) (_maxvalue-1.5);
 };
 //BA.debugLineNum = 2190;BA.debugLine="tempMinRange = minValue+1.5";
_tempminrange = (float) (_minvalue+1.5);
 //BA.debugLineNum = 2193;BA.debugLine="LineChart.YaxisRange(minValue, maxValue)";
mostCurrent._linechart.YaxisRange(_minvalue,_maxvalue);
 //BA.debugLineNum = 2194;BA.debugLine="End Sub";
return "";
}
public static anywheresoftware.b4a.keywords.LayoutValues  _getrealsize() throws Exception{
anywheresoftware.b4a.keywords.LayoutValues _lv = null;
anywheresoftware.b4a.phone.Phone _p = null;
anywheresoftware.b4j.object.JavaObject _ctxt = null;
anywheresoftware.b4j.object.JavaObject _display = null;
anywheresoftware.b4j.object.JavaObject _point = null;
 //BA.debugLineNum = 1508;BA.debugLine="Sub GetRealSize As LayoutValues";
 //BA.debugLineNum = 1509;BA.debugLine="Dim lv As LayoutValues";
_lv = new anywheresoftware.b4a.keywords.LayoutValues();
 //BA.debugLineNum = 1510;BA.debugLine="Dim p As Phone";
_p = new anywheresoftware.b4a.phone.Phone();
 //BA.debugLineNum = 1511;BA.debugLine="If p.SdkVersion >= 17 Then";
if (_p.getSdkVersion()>=17) { 
 //BA.debugLineNum = 1512;BA.debugLine="Dim ctxt As JavaObject";
_ctxt = new anywheresoftware.b4j.object.JavaObject();
 //BA.debugLineNum = 1513;BA.debugLine="ctxt.InitializeContext";
_ctxt.InitializeContext(processBA);
 //BA.debugLineNum = 1514;BA.debugLine="Dim display As JavaObject = ctxt.RunMethodJO(\"ge";
_display = new anywheresoftware.b4j.object.JavaObject();
_display = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(_ctxt.RunMethodJO("getSystemService",new Object[]{(Object)("window")}).RunMethod("getDefaultDisplay",(Object[])(anywheresoftware.b4a.keywords.Common.Null))));
 //BA.debugLineNum = 1515;BA.debugLine="Dim point As JavaObject";
_point = new anywheresoftware.b4j.object.JavaObject();
 //BA.debugLineNum = 1516;BA.debugLine="point.InitializeNewInstance(\"android.graphics.Po";
_point.InitializeNewInstance("android.graphics.Point",(Object[])(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 1517;BA.debugLine="display.RunMethod(\"getRealSize\", Array(point))";
_display.RunMethod("getRealSize",new Object[]{(Object)(_point.getObject())});
 //BA.debugLineNum = 1518;BA.debugLine="lv.Width = point.GetField(\"x\")";
_lv.Width = (int)(BA.ObjectToNumber(_point.GetField("x")));
 //BA.debugLineNum = 1519;BA.debugLine="lv.Height = point.GetField(\"y\")";
_lv.Height = (int)(BA.ObjectToNumber(_point.GetField("y")));
 }else {
 //BA.debugLineNum = 1521;BA.debugLine="lv.Width = 100%x";
_lv.Width = anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA);
 //BA.debugLineNum = 1522;BA.debugLine="lv.Height = 100%y";
_lv.Height = anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA);
 };
 //BA.debugLineNum = 1524;BA.debugLine="lv.Scale = 100dip / 100";
_lv.Scale = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (100))/(double)100);
 //BA.debugLineNum = 1525;BA.debugLine="Return lv";
if (true) return _lv;
 //BA.debugLineNum = 1526;BA.debugLine="End Sub";
return null;
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 27;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 30;BA.debugLine="Private LineChart As LineChart";
mostCurrent._linechart = new androidplotwrapper.lineChartWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Private am12 As String";
mostCurrent._am12 = "";
 //BA.debugLineNum = 32;BA.debugLine="Private am1 As String";
mostCurrent._am1 = "";
 //BA.debugLineNum = 33;BA.debugLine="Private am2 As String";
mostCurrent._am2 = "";
 //BA.debugLineNum = 34;BA.debugLine="Private am3 As String";
mostCurrent._am3 = "";
 //BA.debugLineNum = 35;BA.debugLine="Private am4 As String";
mostCurrent._am4 = "";
 //BA.debugLineNum = 36;BA.debugLine="Private am5 As String";
mostCurrent._am5 = "";
 //BA.debugLineNum = 37;BA.debugLine="Private am6 As String";
mostCurrent._am6 = "";
 //BA.debugLineNum = 38;BA.debugLine="Private am7 As String";
mostCurrent._am7 = "";
 //BA.debugLineNum = 39;BA.debugLine="Private am8 As String";
mostCurrent._am8 = "";
 //BA.debugLineNum = 40;BA.debugLine="Private am9 As String";
mostCurrent._am9 = "";
 //BA.debugLineNum = 41;BA.debugLine="Private am10 As String";
mostCurrent._am10 = "";
 //BA.debugLineNum = 42;BA.debugLine="Private am11 As String";
mostCurrent._am11 = "";
 //BA.debugLineNum = 43;BA.debugLine="Private pm12 As String";
mostCurrent._pm12 = "";
 //BA.debugLineNum = 44;BA.debugLine="Private pm1 As String";
mostCurrent._pm1 = "";
 //BA.debugLineNum = 45;BA.debugLine="Private pm2 As String";
mostCurrent._pm2 = "";
 //BA.debugLineNum = 46;BA.debugLine="Private pm3 As String";
mostCurrent._pm3 = "";
 //BA.debugLineNum = 47;BA.debugLine="Private pm4 As String";
mostCurrent._pm4 = "";
 //BA.debugLineNum = 48;BA.debugLine="Private pm5 As String";
mostCurrent._pm5 = "";
 //BA.debugLineNum = 49;BA.debugLine="Private pm6 As String";
mostCurrent._pm6 = "";
 //BA.debugLineNum = 50;BA.debugLine="Private pm7 As String";
mostCurrent._pm7 = "";
 //BA.debugLineNum = 51;BA.debugLine="Private pm8 As String";
mostCurrent._pm8 = "";
 //BA.debugLineNum = 52;BA.debugLine="Private pm9 As String";
mostCurrent._pm9 = "";
 //BA.debugLineNum = 53;BA.debugLine="Private pm10 As String";
mostCurrent._pm10 = "";
 //BA.debugLineNum = 54;BA.debugLine="Private pm11 As String";
mostCurrent._pm11 = "";
 //BA.debugLineNum = 55;BA.debugLine="Private tempRightNow As String";
mostCurrent._temprightnow = "";
 //BA.debugLineNum = 56;BA.debugLine="Private timeRightNow As Long";
_timerightnow = 0L;
 //BA.debugLineNum = 57;BA.debugLine="Private timeArray(24) As String";
mostCurrent._timearray = new String[(int) (24)];
java.util.Arrays.fill(mostCurrent._timearray,"");
 //BA.debugLineNum = 58;BA.debugLine="Private zeroRange As Float = 88.88";
_zerorange = (float) (88.88);
 //BA.debugLineNum = 59;BA.debugLine="Private tempZeroRange As Float";
_tempzerorange = 0f;
 //BA.debugLineNum = 60;BA.debugLine="Private tempMaxRange As Float";
_tempmaxrange = 0f;
 //BA.debugLineNum = 61;BA.debugLine="Private tempMinRange As Float";
_tempminrange = 0f;
 //BA.debugLineNum = 62;BA.debugLine="Private btnHumidityHourly As Button";
mostCurrent._btnhumidityhourly = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 63;BA.debugLine="Private btnTempHourly As Button";
mostCurrent._btntemphourly = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 64;BA.debugLine="Private btnHumidityDaily As Button";
mostCurrent._btnhumiditydaily = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 65;BA.debugLine="Private btnTempDaily As Button";
mostCurrent._btntempdaily = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 66;BA.debugLine="Private Panel1 As Panel";
mostCurrent._panel1 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 67;BA.debugLine="End Sub";
return "";
}
public static String  _humiditydailycreate() throws Exception{
anywheresoftware.b4a.keywords.LayoutValues _lv = null;
anywheresoftware.b4j.object.JavaObject _jo = null;
long _yesterday = 0L;
 //BA.debugLineNum = 1091;BA.debugLine="Private Sub HumidityDailyCreate()";
 //BA.debugLineNum = 1092;BA.debugLine="Try";
try { //BA.debugLineNum = 1094;BA.debugLine="Activity_WindowFocusChanged(True)";
_activity_windowfocuschanged(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1095;BA.debugLine="Dim lv As LayoutValues = GetRealSize";
_lv = _getrealsize();
 //BA.debugLineNum = 1096;BA.debugLine="Dim jo As JavaObject = Activity";
_jo = new anywheresoftware.b4j.object.JavaObject();
_jo = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(mostCurrent._activity.getObject()));
 //BA.debugLineNum = 1097;BA.debugLine="jo.RunMethod(\"setBottom\", Array(lv.Height))";
_jo.RunMethod("setBottom",new Object[]{(Object)(_lv.Height)});
 //BA.debugLineNum = 1098;BA.debugLine="jo.RunMethod(\"setRight\", Array(lv.Width))";
_jo.RunMethod("setRight",new Object[]{(Object)(_lv.Width)});
 //BA.debugLineNum = 1099;BA.debugLine="Activity.Height = lv.Height";
mostCurrent._activity.setHeight(_lv.Height);
 //BA.debugLineNum = 1100;BA.debugLine="Activity.Width = lv.Width";
mostCurrent._activity.setWidth(_lv.Width);
 //BA.debugLineNum = 1103;BA.debugLine="Activity.LoadLayout(\"Main\")";
mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 //BA.debugLineNum = 1105;BA.debugLine="LineChart.GraphBackgroundColor = Colors.DarkGray";
mostCurrent._linechart.setGraphBackgroundColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 1106;BA.debugLine="LineChart.GraphFrameColor = Colors.Blue";
mostCurrent._linechart.setGraphFrameColor(anywheresoftware.b4a.keywords.Common.Colors.Blue);
 //BA.debugLineNum = 1107;BA.debugLine="LineChart.GraphFrameWidth = 4.0";
mostCurrent._linechart.setGraphFrameWidth((float) (4.0));
 //BA.debugLineNum = 1108;BA.debugLine="LineChart.GraphPlotAreaBackgroundColor = Colors.";
mostCurrent._linechart.setGraphPlotAreaBackgroundColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (50),(int) (0),(int) (0),(int) (255)));
 //BA.debugLineNum = 1109;BA.debugLine="LineChart.GraphTitleTextSize = 15";
mostCurrent._linechart.setGraphTitleTextSize((int) (15));
 //BA.debugLineNum = 1110;BA.debugLine="LineChart.GraphTitleColor = Colors.White";
mostCurrent._linechart.setGraphTitleColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 1111;BA.debugLine="LineChart.GraphTitleSkewX = -0.25";
mostCurrent._linechart.setGraphTitleSkewX((float) (-0.25));
 //BA.debugLineNum = 1112;BA.debugLine="LineChart.GraphTitleUnderline = True";
mostCurrent._linechart.setGraphTitleUnderline(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1113;BA.debugLine="LineChart.GraphTitleBold = True";
mostCurrent._linechart.setGraphTitleBold(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1114;BA.debugLine="LineChart.GraphTitle = \" HUMIDITY DAILY  \"";
mostCurrent._linechart.setGraphTitle(" HUMIDITY DAILY  ");
 //BA.debugLineNum = 1116;BA.debugLine="LineChart.LegendBackgroundColor = Colors.White";
mostCurrent._linechart.setLegendBackgroundColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 1117;BA.debugLine="LineChart.LegendTextColor = Colors.Black";
mostCurrent._linechart.setLegendTextColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 //BA.debugLineNum = 1118;BA.debugLine="LineChart.LegendTextSize = 18.0";
mostCurrent._linechart.setLegendTextSize((float) (18.0));
 //BA.debugLineNum = 1120;BA.debugLine="DateTime.TimeFormat = \"h:mm a\"";
anywheresoftware.b4a.keywords.Common.DateTime.setTimeFormat("h:mm a");
 //BA.debugLineNum = 1121;BA.debugLine="LineChart.DomianLabel = \"The time now is: \" & Da";
mostCurrent._linechart.setDomianLabel("The time now is: "+anywheresoftware.b4a.keywords.Common.DateTime.Time(anywheresoftware.b4a.keywords.Common.DateTime.getNow()));
 //BA.debugLineNum = 1122;BA.debugLine="LineChart.DomainLabelColor = Colors.Green";
mostCurrent._linechart.setDomainLabelColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 1123;BA.debugLine="LineChart.DomainLabelTextSize = 25.0";
mostCurrent._linechart.setDomainLabelTextSize((float) (25.0));
 //BA.debugLineNum = 1125;BA.debugLine="LineChart.XaxisGridLineColor = Colors.ARGB(100,2";
mostCurrent._linechart.setXaxisGridLineColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (100),(int) (255),(int) (255),(int) (255)));
 //BA.debugLineNum = 1126;BA.debugLine="LineChart.XaxisGridLineWidth = 2.0";
mostCurrent._linechart.setXaxisGridLineWidth((float) (2.0));
 //BA.debugLineNum = 1127;BA.debugLine="LineChart.XaxisLabelTicks = 1";
mostCurrent._linechart.setXaxisLabelTicks((int) (1));
 //BA.debugLineNum = 1128;BA.debugLine="LineChart.XaxisLabelOrientation = 0";
mostCurrent._linechart.setXaxisLabelOrientation((float) (0));
 //BA.debugLineNum = 1129;BA.debugLine="LineChart.XaxisLabelTextColor = Colors.White";
mostCurrent._linechart.setXaxisLabelTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 1130;BA.debugLine="LineChart.XaxisLabelTextSize = 32.0";
mostCurrent._linechart.setXaxisLabelTextSize((float) (32.0));
 //BA.debugLineNum = 1131;BA.debugLine="LineChart.XAxisLabels = Array As String(\"12 am\",";
mostCurrent._linechart.setXAxisLabels(new String[]{"12 am","1 am","2 am","3 am","4 am","5 am","6 am","7 am","8 am","9 am","10 am","11 am","12 pm","1 pm","2 pm","3 pm","4 pm","5 pm","6 pm","7 pm","8 pm","9 pm","10 pm","11 pm"});
 //BA.debugLineNum = 1133;BA.debugLine="LineChart.YaxisDivisions = 10";
mostCurrent._linechart.setYaxisDivisions((int) (10));
 //BA.debugLineNum = 1135;BA.debugLine="LineChart.YaxisValueFormat = LineChart.ValueForm";
mostCurrent._linechart.setYaxisValueFormat(mostCurrent._linechart.ValueFormat_2);
 //BA.debugLineNum = 1136;BA.debugLine="LineChart.YaxisGridLineColor = Colors.Black";
mostCurrent._linechart.setYaxisGridLineColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 //BA.debugLineNum = 1137;BA.debugLine="LineChart.YaxisGridLineWidth = 2";
mostCurrent._linechart.setYaxisGridLineWidth((float) (2));
 //BA.debugLineNum = 1138;BA.debugLine="LineChart.YaxisLabelTicks = 1";
mostCurrent._linechart.setYaxisLabelTicks((int) (1));
 //BA.debugLineNum = 1139;BA.debugLine="LineChart.YaxisLabelColor = Colors.Yellow";
mostCurrent._linechart.setYaxisLabelColor(anywheresoftware.b4a.keywords.Common.Colors.Yellow);
 //BA.debugLineNum = 1140;BA.debugLine="LineChart.YaxisLabelOrientation = -30";
mostCurrent._linechart.setYaxisLabelOrientation((float) (-30));
 //BA.debugLineNum = 1141;BA.debugLine="LineChart.YaxisLabelTextSize = 25.0";
mostCurrent._linechart.setYaxisLabelTextSize((float) (25.0));
 //BA.debugLineNum = 1142;BA.debugLine="LineChart.YaxisTitleColor = Colors.Green";
mostCurrent._linechart.setYaxisTitleColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 1143;BA.debugLine="LineChart.YaxisTitleFakeBold = False";
mostCurrent._linechart.setYaxisTitleFakeBold(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1144;BA.debugLine="LineChart.YaxisTitleTextSize = 20.0";
mostCurrent._linechart.setYaxisTitleTextSize((float) (20.0));
 //BA.debugLineNum = 1145;BA.debugLine="LineChart.YaxisTitleUnderline = True";
mostCurrent._linechart.setYaxisTitleUnderline(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1146;BA.debugLine="LineChart.YaxisTitleTextSkewness = 0";
mostCurrent._linechart.setYaxisTitleTextSkewness((float) (0));
 //BA.debugLineNum = 1147;BA.debugLine="LineChart.YaxisLabelAndTitleDistance = 60.0";
mostCurrent._linechart.setYaxisLabelAndTitleDistance((float) (60.0));
 //BA.debugLineNum = 1148;BA.debugLine="LineChart.YaxisTitle = \"Humidity (Percentage)\"";
mostCurrent._linechart.setYaxisTitle("Humidity (Percentage)");
 //BA.debugLineNum = 1150;BA.debugLine="LineChart.MaxNumberOfEntriesPerLineChart = 24";
mostCurrent._linechart.setMaxNumberOfEntriesPerLineChart((int) (24));
 //BA.debugLineNum = 1151;BA.debugLine="LineChart.GraphLegendVisibility = False";
mostCurrent._linechart.setGraphLegendVisibility(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1155;BA.debugLine="ReadHumidityDaily(\"Today\")";
_readhumiditydaily("Today");
 //BA.debugLineNum = 1157;BA.debugLine="DateTime.DateFormat = \"MMM d, yyyy\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("MMM d, yyyy");
 //BA.debugLineNum = 1158;BA.debugLine="LineChart.Line_1_LegendText = \"Today, \" & DateTi";
mostCurrent._linechart.setLine_1_LegendText("Today, "+anywheresoftware.b4a.keywords.Common.DateTime.Date(anywheresoftware.b4a.keywords.Common.DateTime.getNow()));
 //BA.debugLineNum = 1160;BA.debugLine="CheckTempBoundariesDaily";
_checktempboundariesdaily();
 //BA.debugLineNum = 1162;BA.debugLine="If am12 <> tempZeroRange Then";
if ((mostCurrent._am12).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1163;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12)";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12))});
 };
 //BA.debugLineNum = 1165;BA.debugLine="If am1 <> tempZeroRange Then";
if ((mostCurrent._am1).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1166;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1))});
 };
 //BA.debugLineNum = 1168;BA.debugLine="If am2 <> tempZeroRange Then";
if ((mostCurrent._am2).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1169;BA.debugLine="If am1 = tempZeroRange Then";
if ((mostCurrent._am1).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1170;BA.debugLine="am1 = (am12 + am2)/2";
mostCurrent._am1 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am12))+(double)(Double.parseDouble(mostCurrent._am2)))/(double)2);
 };
 //BA.debugLineNum = 1172;BA.debugLine="If am12 = tempZeroRange Then";
if ((mostCurrent._am12).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1173;BA.debugLine="am12 = am1";
mostCurrent._am12 = mostCurrent._am1;
 };
 //BA.debugLineNum = 1175;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2))});
 };
 //BA.debugLineNum = 1177;BA.debugLine="If am3 <> tempZeroRange Then";
if ((mostCurrent._am3).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1178;BA.debugLine="If am2 = tempZeroRange Then";
if ((mostCurrent._am2).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1179;BA.debugLine="am2 = (am1 + am3)/2";
mostCurrent._am2 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am1))+(double)(Double.parseDouble(mostCurrent._am3)))/(double)2);
 };
 //BA.debugLineNum = 1181;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3))});
 };
 //BA.debugLineNum = 1183;BA.debugLine="If am4 <> tempZeroRange Then";
if ((mostCurrent._am4).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1184;BA.debugLine="If am3 = tempZeroRange Then";
if ((mostCurrent._am3).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1185;BA.debugLine="am3 = (am2 + am4)/2";
mostCurrent._am3 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am2))+(double)(Double.parseDouble(mostCurrent._am4)))/(double)2);
 };
 //BA.debugLineNum = 1187;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4))});
 };
 //BA.debugLineNum = 1189;BA.debugLine="If am5 <> tempZeroRange Then";
if ((mostCurrent._am5).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1190;BA.debugLine="If am4 = tempZeroRange Then";
if ((mostCurrent._am4).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1191;BA.debugLine="am4 = (am3 + am5)/2";
mostCurrent._am4 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am3))+(double)(Double.parseDouble(mostCurrent._am5)))/(double)2);
 };
 //BA.debugLineNum = 1193;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5))});
 };
 //BA.debugLineNum = 1195;BA.debugLine="If am6 <> tempZeroRange Then";
if ((mostCurrent._am6).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1196;BA.debugLine="If am5 = tempZeroRange Then";
if ((mostCurrent._am5).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1197;BA.debugLine="am5 = (am4 + am6)/2";
mostCurrent._am5 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am4))+(double)(Double.parseDouble(mostCurrent._am6)))/(double)2);
 };
 //BA.debugLineNum = 1199;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6))});
 };
 //BA.debugLineNum = 1201;BA.debugLine="If am7 <> tempZeroRange Then";
if ((mostCurrent._am7).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1202;BA.debugLine="If am6 = tempZeroRange Then";
if ((mostCurrent._am6).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1203;BA.debugLine="am6 = (am5 + am7)/2";
mostCurrent._am6 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am5))+(double)(Double.parseDouble(mostCurrent._am7)))/(double)2);
 };
 //BA.debugLineNum = 1205;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7))});
 };
 //BA.debugLineNum = 1207;BA.debugLine="If am8 <> tempZeroRange Then";
if ((mostCurrent._am8).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1208;BA.debugLine="If am7 = tempZeroRange Then";
if ((mostCurrent._am7).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1209;BA.debugLine="am7 = (am6 + am8)/2";
mostCurrent._am7 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am6))+(double)(Double.parseDouble(mostCurrent._am8)))/(double)2);
 };
 //BA.debugLineNum = 1211;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8))});
 };
 //BA.debugLineNum = 1213;BA.debugLine="If am9 <> tempZeroRange Then";
if ((mostCurrent._am9).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1214;BA.debugLine="If am8 = tempZeroRange Then";
if ((mostCurrent._am8).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1215;BA.debugLine="am8 = (am7 + am9)/2";
mostCurrent._am8 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am7))+(double)(Double.parseDouble(mostCurrent._am9)))/(double)2);
 };
 //BA.debugLineNum = 1217;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9))});
 };
 //BA.debugLineNum = 1219;BA.debugLine="If am10 <> tempZeroRange Then";
if ((mostCurrent._am10).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1220;BA.debugLine="If am9 = tempZeroRange Then";
if ((mostCurrent._am9).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1221;BA.debugLine="am9 = (am8 + am10)/2";
mostCurrent._am9 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am8))+(double)(Double.parseDouble(mostCurrent._am10)))/(double)2);
 };
 //BA.debugLineNum = 1223;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10))});
 };
 //BA.debugLineNum = 1225;BA.debugLine="If am11 <> tempZeroRange Then";
if ((mostCurrent._am11).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1226;BA.debugLine="If am10 = tempZeroRange Then";
if ((mostCurrent._am10).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1227;BA.debugLine="am10 = (am9 + am11)/2";
mostCurrent._am10 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am9))+(double)(Double.parseDouble(mostCurrent._am11)))/(double)2);
 };
 //BA.debugLineNum = 1229;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11))});
 };
 //BA.debugLineNum = 1231;BA.debugLine="If pm12 <> tempZeroRange Then";
if ((mostCurrent._pm12).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1232;BA.debugLine="If am11 = tempZeroRange Then";
if ((mostCurrent._am11).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1233;BA.debugLine="am11 = (am10 + pm12)/2";
mostCurrent._am11 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am10))+(double)(Double.parseDouble(mostCurrent._pm12)))/(double)2);
 };
 //BA.debugLineNum = 1235;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12))});
 };
 //BA.debugLineNum = 1237;BA.debugLine="If pm1 <> tempZeroRange Then";
if ((mostCurrent._pm1).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1238;BA.debugLine="If pm12 = tempZeroRange Then";
if ((mostCurrent._pm12).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1239;BA.debugLine="pm12 = (am11 + pm1)/2";
mostCurrent._pm12 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am11))+(double)(Double.parseDouble(mostCurrent._pm1)))/(double)2);
 };
 //BA.debugLineNum = 1241;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1))});
 };
 //BA.debugLineNum = 1243;BA.debugLine="If pm2 <> tempZeroRange Then";
if ((mostCurrent._pm2).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1244;BA.debugLine="If pm1 = tempZeroRange Then";
if ((mostCurrent._pm1).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1245;BA.debugLine="pm1 = (pm12 + pm2)/2";
mostCurrent._pm1 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm12))+(double)(Double.parseDouble(mostCurrent._pm2)))/(double)2);
 };
 //BA.debugLineNum = 1247;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2))});
 };
 //BA.debugLineNum = 1249;BA.debugLine="If pm3 <> tempZeroRange Then";
if ((mostCurrent._pm3).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1250;BA.debugLine="If pm2 = tempZeroRange Then";
if ((mostCurrent._pm2).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1251;BA.debugLine="pm2 = (pm1 + pm3)/2";
mostCurrent._pm2 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm1))+(double)(Double.parseDouble(mostCurrent._pm3)))/(double)2);
 };
 //BA.debugLineNum = 1253;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3))});
 };
 //BA.debugLineNum = 1255;BA.debugLine="If pm4 <> tempZeroRange Then";
if ((mostCurrent._pm4).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1256;BA.debugLine="If pm3 = tempZeroRange Then";
if ((mostCurrent._pm3).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1257;BA.debugLine="pm3 = (pm2 + pm4)/2";
mostCurrent._pm3 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm2))+(double)(Double.parseDouble(mostCurrent._pm4)))/(double)2);
 };
 //BA.debugLineNum = 1259;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4))});
 };
 //BA.debugLineNum = 1261;BA.debugLine="If pm5 <> tempZeroRange Then";
if ((mostCurrent._pm5).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1262;BA.debugLine="If pm4 = tempZeroRange Then";
if ((mostCurrent._pm4).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1263;BA.debugLine="pm4 = (pm3 + pm5)/2";
mostCurrent._pm4 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm3))+(double)(Double.parseDouble(mostCurrent._pm5)))/(double)2);
 };
 //BA.debugLineNum = 1265;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5))});
 };
 //BA.debugLineNum = 1267;BA.debugLine="If pm6 <> tempZeroRange Then";
if ((mostCurrent._pm6).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1268;BA.debugLine="If pm5 = tempZeroRange Then";
if ((mostCurrent._pm5).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1269;BA.debugLine="pm5 = (pm4 + pm6)/2";
mostCurrent._pm5 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm4))+(double)(Double.parseDouble(mostCurrent._pm6)))/(double)2);
 };
 //BA.debugLineNum = 1271;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5)),(float)(Double.parseDouble(mostCurrent._pm6))});
 };
 //BA.debugLineNum = 1273;BA.debugLine="If pm7 <> tempZeroRange Then";
if ((mostCurrent._pm7).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1274;BA.debugLine="If pm6 = tempZeroRange Then";
if ((mostCurrent._pm6).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1275;BA.debugLine="pm6 = (pm5 + pm7)/2";
mostCurrent._pm6 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm5))+(double)(Double.parseDouble(mostCurrent._pm7)))/(double)2);
 };
 //BA.debugLineNum = 1277;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5)),(float)(Double.parseDouble(mostCurrent._pm6)),(float)(Double.parseDouble(mostCurrent._pm7))});
 };
 //BA.debugLineNum = 1279;BA.debugLine="If pm8 <> tempZeroRange Then";
if ((mostCurrent._pm8).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1280;BA.debugLine="If pm7 = tempZeroRange Then";
if ((mostCurrent._pm7).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1281;BA.debugLine="pm7 = (pm6 + pm8)/2";
mostCurrent._pm7 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm6))+(double)(Double.parseDouble(mostCurrent._pm8)))/(double)2);
 };
 //BA.debugLineNum = 1283;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5)),(float)(Double.parseDouble(mostCurrent._pm6)),(float)(Double.parseDouble(mostCurrent._pm7)),(float)(Double.parseDouble(mostCurrent._pm8))});
 };
 //BA.debugLineNum = 1285;BA.debugLine="If pm9 <> tempZeroRange Then";
if ((mostCurrent._pm9).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1286;BA.debugLine="If pm8 = tempZeroRange Then";
if ((mostCurrent._pm8).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1287;BA.debugLine="pm8 = (pm7 + pm9)/2";
mostCurrent._pm8 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm7))+(double)(Double.parseDouble(mostCurrent._pm9)))/(double)2);
 };
 //BA.debugLineNum = 1289;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5)),(float)(Double.parseDouble(mostCurrent._pm6)),(float)(Double.parseDouble(mostCurrent._pm7)),(float)(Double.parseDouble(mostCurrent._pm8)),(float)(Double.parseDouble(mostCurrent._pm9))});
 };
 //BA.debugLineNum = 1291;BA.debugLine="If pm10 <> tempZeroRange Then";
if ((mostCurrent._pm10).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1292;BA.debugLine="If pm9 = tempZeroRange Then";
if ((mostCurrent._pm9).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1293;BA.debugLine="pm9 = (pm8 + pm10)/2";
mostCurrent._pm9 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm8))+(double)(Double.parseDouble(mostCurrent._pm10)))/(double)2);
 };
 //BA.debugLineNum = 1295;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5)),(float)(Double.parseDouble(mostCurrent._pm6)),(float)(Double.parseDouble(mostCurrent._pm7)),(float)(Double.parseDouble(mostCurrent._pm8)),(float)(Double.parseDouble(mostCurrent._pm9)),(float)(Double.parseDouble(mostCurrent._pm10))});
 };
 //BA.debugLineNum = 1297;BA.debugLine="If pm11 <> tempZeroRange Then";
if ((mostCurrent._pm11).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1298;BA.debugLine="If pm10 = tempZeroRange Then";
if ((mostCurrent._pm10).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1299;BA.debugLine="pm10 = (pm9 + pm11)/2";
mostCurrent._pm10 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm9))+(double)(Double.parseDouble(mostCurrent._pm11)))/(double)2);
 };
 //BA.debugLineNum = 1301;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5)),(float)(Double.parseDouble(mostCurrent._pm6)),(float)(Double.parseDouble(mostCurrent._pm7)),(float)(Double.parseDouble(mostCurrent._pm8)),(float)(Double.parseDouble(mostCurrent._pm9)),(float)(Double.parseDouble(mostCurrent._pm10)),(float)(Double.parseDouble(mostCurrent._pm11))});
 };
 //BA.debugLineNum = 1304;BA.debugLine="LineChart.Line_1_PointLabelTextColor = Colors.Ye";
mostCurrent._linechart.setLine_1_PointLabelTextColor(anywheresoftware.b4a.keywords.Common.Colors.Yellow);
 //BA.debugLineNum = 1305;BA.debugLine="LineChart.Line_1_PointLabelTextSize = 35.0";
mostCurrent._linechart.setLine_1_PointLabelTextSize((float) (35.0));
 //BA.debugLineNum = 1306;BA.debugLine="LineChart.Line_1_LineColor = Colors.Red";
mostCurrent._linechart.setLine_1_LineColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 1307;BA.debugLine="LineChart.Line_1_LineWidth = 11.0";
mostCurrent._linechart.setLine_1_LineWidth((float) (11.0));
 //BA.debugLineNum = 1308;BA.debugLine="LineChart.Line_1_PointColor = Colors.Black";
mostCurrent._linechart.setLine_1_PointColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 //BA.debugLineNum = 1309;BA.debugLine="LineChart.Line_1_PointSize = 25.0";
mostCurrent._linechart.setLine_1_PointSize((float) (25.0));
 //BA.debugLineNum = 1310;BA.debugLine="LineChart.Line_1_PointShape = LineChart.SHAPE_RO";
mostCurrent._linechart.setLine_1_PointShape(mostCurrent._linechart.SHAPE_ROUND);
 //BA.debugLineNum = 1311;BA.debugLine="LineChart.Line_1_DrawDash = False";
mostCurrent._linechart.setLine_1_DrawDash(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1312;BA.debugLine="LineChart.Line_1_DrawCubic = False";
mostCurrent._linechart.setLine_1_DrawCubic(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1318;BA.debugLine="ReadHumidityDaily(\"Yesterday\")";
_readhumiditydaily("Yesterday");
 //BA.debugLineNum = 1320;BA.debugLine="Dim Yesterday As Long";
_yesterday = 0L;
 //BA.debugLineNum = 1321;BA.debugLine="Yesterday = DateTime.add(DateTime.Now, 0, 0, -1)";
_yesterday = anywheresoftware.b4a.keywords.Common.DateTime.Add(anywheresoftware.b4a.keywords.Common.DateTime.getNow(),(int) (0),(int) (0),(int) (-1));
 //BA.debugLineNum = 1323;BA.debugLine="DateTime.DateFormat = \"MMM d, yyyy\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("MMM d, yyyy");
 //BA.debugLineNum = 1324;BA.debugLine="LineChart.Line_2_LegendText = \"Yesterday, \" & Da";
mostCurrent._linechart.setLine_2_LegendText("Yesterday, "+anywheresoftware.b4a.keywords.Common.DateTime.Date(_yesterday));
 //BA.debugLineNum = 1326;BA.debugLine="CheckTempBoundariesDaily";
_checktempboundariesdaily();
 //BA.debugLineNum = 1328;BA.debugLine="If am12 <> tempZeroRange Then";
if ((mostCurrent._am12).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1329;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12)";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12))});
 };
 //BA.debugLineNum = 1331;BA.debugLine="If am1 <> tempZeroRange Then";
if ((mostCurrent._am1).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1332;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1))});
 };
 //BA.debugLineNum = 1334;BA.debugLine="If am2 <> tempZeroRange Then";
if ((mostCurrent._am2).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1335;BA.debugLine="If am1 = tempZeroRange Then";
if ((mostCurrent._am1).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1336;BA.debugLine="am1 = (am12 + am2)/2";
mostCurrent._am1 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am12))+(double)(Double.parseDouble(mostCurrent._am2)))/(double)2);
 };
 //BA.debugLineNum = 1338;BA.debugLine="If am12 = tempZeroRange Then";
if ((mostCurrent._am12).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1339;BA.debugLine="am12 = am1";
mostCurrent._am12 = mostCurrent._am1;
 };
 //BA.debugLineNum = 1341;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2))});
 };
 //BA.debugLineNum = 1343;BA.debugLine="If am3 <> tempZeroRange Then";
if ((mostCurrent._am3).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1344;BA.debugLine="If am2 = tempZeroRange Then";
if ((mostCurrent._am2).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1345;BA.debugLine="am2 = (am1 + am3)/2";
mostCurrent._am2 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am1))+(double)(Double.parseDouble(mostCurrent._am3)))/(double)2);
 };
 //BA.debugLineNum = 1347;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3))});
 };
 //BA.debugLineNum = 1349;BA.debugLine="If am4 <> tempZeroRange Then";
if ((mostCurrent._am4).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1350;BA.debugLine="If am3 = tempZeroRange Then";
if ((mostCurrent._am3).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1351;BA.debugLine="am3 = (am2 + am4)/2";
mostCurrent._am3 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am2))+(double)(Double.parseDouble(mostCurrent._am4)))/(double)2);
 };
 //BA.debugLineNum = 1353;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4))});
 };
 //BA.debugLineNum = 1355;BA.debugLine="If am5 <> tempZeroRange Then";
if ((mostCurrent._am5).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1356;BA.debugLine="If am4 = tempZeroRange Then";
if ((mostCurrent._am4).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1357;BA.debugLine="am4 = (am3 + am5)/2";
mostCurrent._am4 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am3))+(double)(Double.parseDouble(mostCurrent._am5)))/(double)2);
 };
 //BA.debugLineNum = 1359;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5))});
 };
 //BA.debugLineNum = 1361;BA.debugLine="If am6 <> tempZeroRange Then";
if ((mostCurrent._am6).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1362;BA.debugLine="If am5 = tempZeroRange Then";
if ((mostCurrent._am5).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1363;BA.debugLine="am5 = (am4 + am6)/2";
mostCurrent._am5 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am4))+(double)(Double.parseDouble(mostCurrent._am6)))/(double)2);
 };
 //BA.debugLineNum = 1365;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6))});
 };
 //BA.debugLineNum = 1367;BA.debugLine="If am7 <> tempZeroRange Then";
if ((mostCurrent._am7).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1368;BA.debugLine="If am6 = tempZeroRange Then";
if ((mostCurrent._am6).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1369;BA.debugLine="am6 = (am5 + am7)/2";
mostCurrent._am6 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am5))+(double)(Double.parseDouble(mostCurrent._am7)))/(double)2);
 };
 //BA.debugLineNum = 1371;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7))});
 };
 //BA.debugLineNum = 1373;BA.debugLine="If am8 <> tempZeroRange Then";
if ((mostCurrent._am8).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1374;BA.debugLine="If am7 = tempZeroRange Then";
if ((mostCurrent._am7).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1375;BA.debugLine="am7 = (am6 + am8)/2";
mostCurrent._am7 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am6))+(double)(Double.parseDouble(mostCurrent._am8)))/(double)2);
 };
 //BA.debugLineNum = 1377;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8))});
 };
 //BA.debugLineNum = 1379;BA.debugLine="If am9 <> tempZeroRange Then";
if ((mostCurrent._am9).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1380;BA.debugLine="If am8 = tempZeroRange Then";
if ((mostCurrent._am8).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1381;BA.debugLine="am8 = (am7 + am9)/2";
mostCurrent._am8 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am7))+(double)(Double.parseDouble(mostCurrent._am9)))/(double)2);
 };
 //BA.debugLineNum = 1383;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9))});
 };
 //BA.debugLineNum = 1385;BA.debugLine="If am10 <> tempZeroRange Then";
if ((mostCurrent._am10).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1386;BA.debugLine="If am9 = tempZeroRange Then";
if ((mostCurrent._am9).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1387;BA.debugLine="am9 = (am8 + am10)/2";
mostCurrent._am9 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am8))+(double)(Double.parseDouble(mostCurrent._am10)))/(double)2);
 };
 //BA.debugLineNum = 1389;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10))});
 };
 //BA.debugLineNum = 1391;BA.debugLine="If am11 <> tempZeroRange Then";
if ((mostCurrent._am11).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1392;BA.debugLine="If am10 = tempZeroRange Then";
if ((mostCurrent._am10).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1393;BA.debugLine="am10 = (am9 + am11)/2";
mostCurrent._am10 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am9))+(double)(Double.parseDouble(mostCurrent._am11)))/(double)2);
 };
 //BA.debugLineNum = 1395;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11))});
 };
 //BA.debugLineNum = 1397;BA.debugLine="If pm12 <> tempZeroRange Then";
if ((mostCurrent._pm12).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1398;BA.debugLine="If am11 = tempZeroRange Then";
if ((mostCurrent._am11).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1399;BA.debugLine="am11 = (am10 + pm12)/2";
mostCurrent._am11 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am10))+(double)(Double.parseDouble(mostCurrent._pm12)))/(double)2);
 };
 //BA.debugLineNum = 1401;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12))});
 };
 //BA.debugLineNum = 1403;BA.debugLine="If pm1 <> tempZeroRange Then";
if ((mostCurrent._pm1).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1404;BA.debugLine="If pm12 = tempZeroRange Then";
if ((mostCurrent._pm12).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1405;BA.debugLine="pm12 = (am11 + pm1)/2";
mostCurrent._pm12 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am11))+(double)(Double.parseDouble(mostCurrent._pm1)))/(double)2);
 };
 //BA.debugLineNum = 1407;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1))});
 };
 //BA.debugLineNum = 1409;BA.debugLine="If pm2 <> tempZeroRange Then";
if ((mostCurrent._pm2).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1410;BA.debugLine="If pm1 = tempZeroRange Then";
if ((mostCurrent._pm1).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1411;BA.debugLine="pm1 = (pm12 + pm2)/2";
mostCurrent._pm1 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm12))+(double)(Double.parseDouble(mostCurrent._pm2)))/(double)2);
 };
 //BA.debugLineNum = 1413;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2))});
 };
 //BA.debugLineNum = 1415;BA.debugLine="If pm3 <> tempZeroRange Then";
if ((mostCurrent._pm3).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1416;BA.debugLine="If pm2 = tempZeroRange Then";
if ((mostCurrent._pm2).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1417;BA.debugLine="pm2 = (pm1 + pm3)/2";
mostCurrent._pm2 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm1))+(double)(Double.parseDouble(mostCurrent._pm3)))/(double)2);
 };
 //BA.debugLineNum = 1419;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3))});
 };
 //BA.debugLineNum = 1421;BA.debugLine="If pm4 <> tempZeroRange Then";
if ((mostCurrent._pm4).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1422;BA.debugLine="If pm3 = tempZeroRange Then";
if ((mostCurrent._pm3).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1423;BA.debugLine="pm3 = (pm2 + pm4)/2";
mostCurrent._pm3 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm2))+(double)(Double.parseDouble(mostCurrent._pm4)))/(double)2);
 };
 //BA.debugLineNum = 1425;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4))});
 };
 //BA.debugLineNum = 1427;BA.debugLine="If pm5 <> tempZeroRange Then";
if ((mostCurrent._pm5).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1428;BA.debugLine="If pm4 = tempZeroRange Then";
if ((mostCurrent._pm4).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1429;BA.debugLine="pm4 = (pm3 + pm5)/2";
mostCurrent._pm4 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm3))+(double)(Double.parseDouble(mostCurrent._pm5)))/(double)2);
 };
 //BA.debugLineNum = 1431;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5))});
 };
 //BA.debugLineNum = 1433;BA.debugLine="If pm6 <> tempZeroRange Then";
if ((mostCurrent._pm6).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1434;BA.debugLine="If pm5 = tempZeroRange Then";
if ((mostCurrent._pm5).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1435;BA.debugLine="pm5 = (pm4 + pm6)/2";
mostCurrent._pm5 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm4))+(double)(Double.parseDouble(mostCurrent._pm6)))/(double)2);
 };
 //BA.debugLineNum = 1437;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5)),(float)(Double.parseDouble(mostCurrent._pm6))});
 };
 //BA.debugLineNum = 1439;BA.debugLine="If pm7 <> tempZeroRange Then";
if ((mostCurrent._pm7).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1440;BA.debugLine="If pm6 = tempZeroRange Then";
if ((mostCurrent._pm6).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1441;BA.debugLine="pm6 = (pm5 + pm7)/2";
mostCurrent._pm6 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm5))+(double)(Double.parseDouble(mostCurrent._pm7)))/(double)2);
 };
 //BA.debugLineNum = 1443;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5)),(float)(Double.parseDouble(mostCurrent._pm6)),(float)(Double.parseDouble(mostCurrent._pm7))});
 };
 //BA.debugLineNum = 1445;BA.debugLine="If pm8 <> tempZeroRange Then";
if ((mostCurrent._pm8).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1446;BA.debugLine="If pm7 = tempZeroRange Then";
if ((mostCurrent._pm7).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1447;BA.debugLine="pm7 = (pm6 + pm8)/2";
mostCurrent._pm7 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm6))+(double)(Double.parseDouble(mostCurrent._pm8)))/(double)2);
 };
 //BA.debugLineNum = 1449;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5)),(float)(Double.parseDouble(mostCurrent._pm6)),(float)(Double.parseDouble(mostCurrent._pm7)),(float)(Double.parseDouble(mostCurrent._pm8))});
 };
 //BA.debugLineNum = 1451;BA.debugLine="If pm9 <> tempZeroRange Then";
if ((mostCurrent._pm9).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1452;BA.debugLine="If pm8 = tempZeroRange Then";
if ((mostCurrent._pm8).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1453;BA.debugLine="pm8 = (pm7 + pm9)/2";
mostCurrent._pm8 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm7))+(double)(Double.parseDouble(mostCurrent._pm9)))/(double)2);
 };
 //BA.debugLineNum = 1455;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5)),(float)(Double.parseDouble(mostCurrent._pm6)),(float)(Double.parseDouble(mostCurrent._pm7)),(float)(Double.parseDouble(mostCurrent._pm8)),(float)(Double.parseDouble(mostCurrent._pm9))});
 };
 //BA.debugLineNum = 1457;BA.debugLine="If pm10 <> tempZeroRange Then";
if ((mostCurrent._pm10).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1458;BA.debugLine="If pm9 = tempZeroRange Then";
if ((mostCurrent._pm9).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1459;BA.debugLine="pm9 = (pm8 + pm10)/2";
mostCurrent._pm9 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm8))+(double)(Double.parseDouble(mostCurrent._pm10)))/(double)2);
 };
 //BA.debugLineNum = 1461;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5)),(float)(Double.parseDouble(mostCurrent._pm6)),(float)(Double.parseDouble(mostCurrent._pm7)),(float)(Double.parseDouble(mostCurrent._pm8)),(float)(Double.parseDouble(mostCurrent._pm9)),(float)(Double.parseDouble(mostCurrent._pm10))});
 };
 //BA.debugLineNum = 1463;BA.debugLine="If pm11 <> tempZeroRange Then";
if ((mostCurrent._pm11).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1464;BA.debugLine="If pm10 = tempZeroRange Then";
if ((mostCurrent._pm10).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1465;BA.debugLine="pm10 = (pm9 + pm11)/2";
mostCurrent._pm10 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm9))+(double)(Double.parseDouble(mostCurrent._pm11)))/(double)2);
 };
 //BA.debugLineNum = 1467;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5)),(float)(Double.parseDouble(mostCurrent._pm6)),(float)(Double.parseDouble(mostCurrent._pm7)),(float)(Double.parseDouble(mostCurrent._pm8)),(float)(Double.parseDouble(mostCurrent._pm9)),(float)(Double.parseDouble(mostCurrent._pm10)),(float)(Double.parseDouble(mostCurrent._pm11))});
 };
 //BA.debugLineNum = 1470;BA.debugLine="LineChart.Line_2_PointLabelTextColor = Colors.Cy";
mostCurrent._linechart.setLine_2_PointLabelTextColor(anywheresoftware.b4a.keywords.Common.Colors.Cyan);
 //BA.debugLineNum = 1471;BA.debugLine="LineChart.Line_2_PointLabelTextSize = 35.0";
mostCurrent._linechart.setLine_2_PointLabelTextSize((float) (35.0));
 //BA.debugLineNum = 1472;BA.debugLine="LineChart.Line_2_LineColor = Colors.White";
mostCurrent._linechart.setLine_2_LineColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 1473;BA.debugLine="LineChart.Line_2_LineWidth = 7.0";
mostCurrent._linechart.setLine_2_LineWidth((float) (7.0));
 //BA.debugLineNum = 1474;BA.debugLine="LineChart.Line_2_PointColor = Colors.Cyan";
mostCurrent._linechart.setLine_2_PointColor(anywheresoftware.b4a.keywords.Common.Colors.Cyan);
 //BA.debugLineNum = 1475;BA.debugLine="LineChart.Line_2_PointSize = 10.0";
mostCurrent._linechart.setLine_2_PointSize((float) (10.0));
 //BA.debugLineNum = 1476;BA.debugLine="LineChart.Line_2_PointShape = LineChart.SHAPE_RO";
mostCurrent._linechart.setLine_2_PointShape(mostCurrent._linechart.SHAPE_ROUND);
 //BA.debugLineNum = 1477;BA.debugLine="LineChart.Line_2_DrawDash = False";
mostCurrent._linechart.setLine_2_DrawDash(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1478;BA.debugLine="LineChart.Line_2_DrawCubic = False";
mostCurrent._linechart.setLine_2_DrawCubic(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1484;BA.debugLine="LineChart.Line_3_LegendText = \"Real time\"";
mostCurrent._linechart.setLine_3_LegendText("Real time");
 //BA.debugLineNum = 1485;BA.debugLine="LineChart.Line_3_Data = Array As Float (tempRigh";
mostCurrent._linechart.setLine_3_Data(new float[]{(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow))});
 //BA.debugLineNum = 1486;BA.debugLine="LineChart.Line_3_PointLabelTextColor = Colors.Gr";
mostCurrent._linechart.setLine_3_PointLabelTextColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 1487;BA.debugLine="LineChart.Line_3_PointLabelTextSize = 30.0";
mostCurrent._linechart.setLine_3_PointLabelTextSize((float) (30.0));
 //BA.debugLineNum = 1488;BA.debugLine="LineChart.Line_3_LineColor = Colors.Green";
mostCurrent._linechart.setLine_3_LineColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 1489;BA.debugLine="LineChart.Line_3_LineWidth = 5.0";
mostCurrent._linechart.setLine_3_LineWidth((float) (5.0));
 //BA.debugLineNum = 1490;BA.debugLine="LineChart.Line_3_PointColor = Colors.Green";
mostCurrent._linechart.setLine_3_PointColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 1491;BA.debugLine="LineChart.Line_3_PointSize = 1.0";
mostCurrent._linechart.setLine_3_PointSize((float) (1.0));
 //BA.debugLineNum = 1492;BA.debugLine="LineChart.Line_3_PointShape = LineChart.SHAPE_RO";
mostCurrent._linechart.setLine_3_PointShape(mostCurrent._linechart.SHAPE_ROUND);
 //BA.debugLineNum = 1493;BA.debugLine="LineChart.Line_3_DrawDash = False";
mostCurrent._linechart.setLine_3_DrawDash(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1494;BA.debugLine="LineChart.Line_3_DrawCubic = False";
mostCurrent._linechart.setLine_3_DrawCubic(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1498;BA.debugLine="LineChart.NumberOfLineCharts = 3";
mostCurrent._linechart.setNumberOfLineCharts((int) (3));
 //BA.debugLineNum = 1500;BA.debugLine="LineChart.DrawTheGraphs";
mostCurrent._linechart.DrawTheGraphs();
 } 
       catch (Exception e375) {
			processBA.setLastException(e375); //BA.debugLineNum = 1503;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("6786844",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 //BA.debugLineNum = 1504;BA.debugLine="ToastMessageShow (LastException,True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getObject()),anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 1506;BA.debugLine="End Sub";
return "";
}
public static String  _humiditydailytimer_tick() throws Exception{
 //BA.debugLineNum = 2232;BA.debugLine="Sub HumidityDailyTimer_Tick";
 //BA.debugLineNum = 2233;BA.debugLine="Activity.RequestFocus";
mostCurrent._activity.RequestFocus();
 //BA.debugLineNum = 2234;BA.debugLine="btnHumidityHourly.RemoveView";
mostCurrent._btnhumidityhourly.RemoveView();
 //BA.debugLineNum = 2235;BA.debugLine="btnTempHourly.RemoveView";
mostCurrent._btntemphourly.RemoveView();
 //BA.debugLineNum = 2236;BA.debugLine="btnHumidityDaily.RemoveView";
mostCurrent._btnhumiditydaily.RemoveView();
 //BA.debugLineNum = 2237;BA.debugLine="btnTempDaily.RemoveView";
mostCurrent._btntempdaily.RemoveView();
 //BA.debugLineNum = 2238;BA.debugLine="LineChart.RemoveView";
mostCurrent._linechart.RemoveView();
 //BA.debugLineNum = 2239;BA.debugLine="tempMaxRange=0";
_tempmaxrange = (float) (0);
 //BA.debugLineNum = 2240;BA.debugLine="tempMinRange=0";
_tempminrange = (float) (0);
 //BA.debugLineNum = 2241;BA.debugLine="HumidityDailyCreate";
_humiditydailycreate();
 //BA.debugLineNum = 2242;BA.debugLine="End Sub";
return "";
}
public static String  _humidityhourlycreate() throws Exception{
anywheresoftware.b4a.keywords.LayoutValues _lv = null;
anywheresoftware.b4j.object.JavaObject _jo = null;
int _i = 0;
b4a.example.dateutils._period _p = null;
long _nexttime = 0L;
 //BA.debugLineNum = 406;BA.debugLine="Private Sub HumidityHourlyCreate()";
 //BA.debugLineNum = 407;BA.debugLine="Try";
try { //BA.debugLineNum = 409;BA.debugLine="Activity_WindowFocusChanged(True)";
_activity_windowfocuschanged(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 410;BA.debugLine="Dim lv As LayoutValues = GetRealSize";
_lv = _getrealsize();
 //BA.debugLineNum = 411;BA.debugLine="Dim jo As JavaObject = Activity";
_jo = new anywheresoftware.b4j.object.JavaObject();
_jo = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(mostCurrent._activity.getObject()));
 //BA.debugLineNum = 412;BA.debugLine="jo.RunMethod(\"setBottom\", Array(lv.Height))";
_jo.RunMethod("setBottom",new Object[]{(Object)(_lv.Height)});
 //BA.debugLineNum = 413;BA.debugLine="jo.RunMethod(\"setRight\", Array(lv.Width))";
_jo.RunMethod("setRight",new Object[]{(Object)(_lv.Width)});
 //BA.debugLineNum = 414;BA.debugLine="Activity.Height = lv.Height";
mostCurrent._activity.setHeight(_lv.Height);
 //BA.debugLineNum = 415;BA.debugLine="Activity.Width = lv.Width";
mostCurrent._activity.setWidth(_lv.Width);
 //BA.debugLineNum = 418;BA.debugLine="Activity.LoadLayout(\"Main\")";
mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 //BA.debugLineNum = 420;BA.debugLine="LineChart.GraphBackgroundColor = Colors.DarkGray";
mostCurrent._linechart.setGraphBackgroundColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 421;BA.debugLine="LineChart.GraphFrameColor = Colors.Blue";
mostCurrent._linechart.setGraphFrameColor(anywheresoftware.b4a.keywords.Common.Colors.Blue);
 //BA.debugLineNum = 422;BA.debugLine="LineChart.GraphFrameWidth = 4.0";
mostCurrent._linechart.setGraphFrameWidth((float) (4.0));
 //BA.debugLineNum = 423;BA.debugLine="LineChart.GraphPlotAreaBackgroundColor = Colors.";
mostCurrent._linechart.setGraphPlotAreaBackgroundColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (50),(int) (0),(int) (0),(int) (255)));
 //BA.debugLineNum = 424;BA.debugLine="LineChart.GraphTitleTextSize = 15";
mostCurrent._linechart.setGraphTitleTextSize((int) (15));
 //BA.debugLineNum = 425;BA.debugLine="LineChart.GraphTitleColor = Colors.White";
mostCurrent._linechart.setGraphTitleColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 426;BA.debugLine="LineChart.GraphTitleSkewX = -0.25";
mostCurrent._linechart.setGraphTitleSkewX((float) (-0.25));
 //BA.debugLineNum = 427;BA.debugLine="LineChart.GraphTitleUnderline = True";
mostCurrent._linechart.setGraphTitleUnderline(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 428;BA.debugLine="LineChart.GraphTitleBold = True";
mostCurrent._linechart.setGraphTitleBold(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 429;BA.debugLine="LineChart.GraphTitle = \" HUMIDITY HOURLY  \"";
mostCurrent._linechart.setGraphTitle(" HUMIDITY HOURLY  ");
 //BA.debugLineNum = 431;BA.debugLine="LineChart.LegendBackgroundColor = Colors.White";
mostCurrent._linechart.setLegendBackgroundColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 432;BA.debugLine="LineChart.LegendTextColor = Colors.Black";
mostCurrent._linechart.setLegendTextColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 //BA.debugLineNum = 433;BA.debugLine="LineChart.LegendTextSize = 18.0";
mostCurrent._linechart.setLegendTextSize((float) (18.0));
 //BA.debugLineNum = 435;BA.debugLine="DateTime.TimeFormat = \"h:mm a\"";
anywheresoftware.b4a.keywords.Common.DateTime.setTimeFormat("h:mm a");
 //BA.debugLineNum = 436;BA.debugLine="LineChart.DomianLabel = \"The time now is: \" & Da";
mostCurrent._linechart.setDomianLabel("The time now is: "+anywheresoftware.b4a.keywords.Common.DateTime.Time(anywheresoftware.b4a.keywords.Common.DateTime.getNow()));
 //BA.debugLineNum = 437;BA.debugLine="LineChart.DomainLabelColor = Colors.Green";
mostCurrent._linechart.setDomainLabelColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 438;BA.debugLine="LineChart.DomainLabelTextSize = 25.0";
mostCurrent._linechart.setDomainLabelTextSize((float) (25.0));
 //BA.debugLineNum = 440;BA.debugLine="LineChart.XaxisGridLineColor = Colors.ARGB(100,2";
mostCurrent._linechart.setXaxisGridLineColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (100),(int) (255),(int) (255),(int) (255)));
 //BA.debugLineNum = 441;BA.debugLine="LineChart.XaxisGridLineWidth = 2.0";
mostCurrent._linechart.setXaxisGridLineWidth((float) (2.0));
 //BA.debugLineNum = 442;BA.debugLine="LineChart.XaxisLabelTicks = 1";
mostCurrent._linechart.setXaxisLabelTicks((int) (1));
 //BA.debugLineNum = 443;BA.debugLine="LineChart.XaxisLabelOrientation = 0";
mostCurrent._linechart.setXaxisLabelOrientation((float) (0));
 //BA.debugLineNum = 444;BA.debugLine="LineChart.XaxisLabelTextColor = Colors.White";
mostCurrent._linechart.setXaxisLabelTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 445;BA.debugLine="LineChart.XaxisLabelTextSize = 32.0";
mostCurrent._linechart.setXaxisLabelTextSize((float) (32.0));
 //BA.debugLineNum = 449;BA.debugLine="timeRightNow = DateTime.Now";
_timerightnow = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 451;BA.debugLine="For i = 23 To 0 Step -1";
{
final int step34 = -1;
final int limit34 = (int) (0);
_i = (int) (23) ;
for (;_i >= limit34 ;_i = _i + step34 ) {
 //BA.debugLineNum = 452;BA.debugLine="Dim p As Period";
_p = new b4a.example.dateutils._period();
 //BA.debugLineNum = 453;BA.debugLine="p.Hours = 0";
_p.Hours = (int) (0);
 //BA.debugLineNum = 454;BA.debugLine="p.Minutes = (i+1) * -5";
_p.Minutes = (int) ((_i+1)*-5);
 //BA.debugLineNum = 455;BA.debugLine="p.Seconds = 0";
_p.Seconds = (int) (0);
 //BA.debugLineNum = 456;BA.debugLine="Dim NextTime As Long";
_nexttime = 0L;
 //BA.debugLineNum = 457;BA.debugLine="NextTime = DateUtils.AddPeriod(timeRightNow, p)";
_nexttime = mostCurrent._dateutils._addperiod(mostCurrent.activityBA,_timerightnow,_p);
 //BA.debugLineNum = 458;BA.debugLine="DateTime.TimeFormat = \"HH:mm\"";
anywheresoftware.b4a.keywords.Common.DateTime.setTimeFormat("HH:mm");
 //BA.debugLineNum = 460;BA.debugLine="timeArray(23-i) = DateTime.Time(NextTime) 'Date";
mostCurrent._timearray[(int) (23-_i)] = anywheresoftware.b4a.keywords.Common.DateTime.Time(_nexttime);
 }
};
 //BA.debugLineNum = 462;BA.debugLine="LineChart.XAxisLabels = timeArray 'Array As Stri";
mostCurrent._linechart.setXAxisLabels(mostCurrent._timearray);
 //BA.debugLineNum = 465;BA.debugLine="LineChart.YaxisDivisions = 10";
mostCurrent._linechart.setYaxisDivisions((int) (10));
 //BA.debugLineNum = 467;BA.debugLine="LineChart.YaxisValueFormat = LineChart.ValueForm";
mostCurrent._linechart.setYaxisValueFormat(mostCurrent._linechart.ValueFormat_2);
 //BA.debugLineNum = 468;BA.debugLine="LineChart.YaxisGridLineColor = Colors.Black";
mostCurrent._linechart.setYaxisGridLineColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 //BA.debugLineNum = 469;BA.debugLine="LineChart.YaxisGridLineWidth = 2";
mostCurrent._linechart.setYaxisGridLineWidth((float) (2));
 //BA.debugLineNum = 470;BA.debugLine="LineChart.YaxisLabelTicks = 1";
mostCurrent._linechart.setYaxisLabelTicks((int) (1));
 //BA.debugLineNum = 471;BA.debugLine="LineChart.YaxisLabelColor = Colors.Yellow";
mostCurrent._linechart.setYaxisLabelColor(anywheresoftware.b4a.keywords.Common.Colors.Yellow);
 //BA.debugLineNum = 472;BA.debugLine="LineChart.YaxisLabelOrientation = -30";
mostCurrent._linechart.setYaxisLabelOrientation((float) (-30));
 //BA.debugLineNum = 473;BA.debugLine="LineChart.YaxisLabelTextSize = 25.0";
mostCurrent._linechart.setYaxisLabelTextSize((float) (25.0));
 //BA.debugLineNum = 474;BA.debugLine="LineChart.YaxisTitleColor = Colors.Green";
mostCurrent._linechart.setYaxisTitleColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 475;BA.debugLine="LineChart.YaxisTitleFakeBold = False";
mostCurrent._linechart.setYaxisTitleFakeBold(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 476;BA.debugLine="LineChart.YaxisTitleTextSize = 20.0";
mostCurrent._linechart.setYaxisTitleTextSize((float) (20.0));
 //BA.debugLineNum = 477;BA.debugLine="LineChart.YaxisTitleUnderline = True";
mostCurrent._linechart.setYaxisTitleUnderline(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 478;BA.debugLine="LineChart.YaxisTitleTextSkewness = 0";
mostCurrent._linechart.setYaxisTitleTextSkewness((float) (0));
 //BA.debugLineNum = 479;BA.debugLine="LineChart.YaxisLabelAndTitleDistance = 60.0";
mostCurrent._linechart.setYaxisLabelAndTitleDistance((float) (60.0));
 //BA.debugLineNum = 480;BA.debugLine="LineChart.YaxisTitle = \"Humidity (Percentage)\"";
mostCurrent._linechart.setYaxisTitle("Humidity (Percentage)");
 //BA.debugLineNum = 482;BA.debugLine="LineChart.MaxNumberOfEntriesPerLineChart = 24";
mostCurrent._linechart.setMaxNumberOfEntriesPerLineChart((int) (24));
 //BA.debugLineNum = 483;BA.debugLine="LineChart.GraphLegendVisibility = False";
mostCurrent._linechart.setGraphLegendVisibility(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 487;BA.debugLine="ReadHumidityHourly(\"Today\")";
_readhumidityhourly("Today");
 //BA.debugLineNum = 489;BA.debugLine="DateTime.DateFormat = \"MMM d, yyyy\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("MMM d, yyyy");
 //BA.debugLineNum = 490;BA.debugLine="LineChart.Line_1_LegendText = \"From \" & timeArra";
mostCurrent._linechart.setLine_1_LegendText("From "+mostCurrent._timearray[(int) (0)]+" to "+mostCurrent._timearray[(int) (23)]);
 //BA.debugLineNum = 492;BA.debugLine="CheckTempBoundaries";
_checktempboundaries();
 //BA.debugLineNum = 494;BA.debugLine="If am12 <> tempZeroRange Then";
if ((mostCurrent._am12).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 495;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12)";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12))});
 };
 //BA.debugLineNum = 497;BA.debugLine="If am1 <> tempZeroRange Then";
if ((mostCurrent._am1).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 498;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1))});
 };
 //BA.debugLineNum = 500;BA.debugLine="If am2 <> tempZeroRange Then";
if ((mostCurrent._am2).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 501;BA.debugLine="If am1 = tempZeroRange Then";
if ((mostCurrent._am1).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 502;BA.debugLine="am1 = (am12 + am2)/2";
mostCurrent._am1 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am12))+(double)(Double.parseDouble(mostCurrent._am2)))/(double)2);
 };
 //BA.debugLineNum = 504;BA.debugLine="If am12 = tempZeroRange Then";
if ((mostCurrent._am12).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 505;BA.debugLine="am12 = am1";
mostCurrent._am12 = mostCurrent._am1;
 };
 //BA.debugLineNum = 507;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2))});
 };
 //BA.debugLineNum = 509;BA.debugLine="If am3 <> tempZeroRange Then";
if ((mostCurrent._am3).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 510;BA.debugLine="If am2 = tempZeroRange Then";
if ((mostCurrent._am2).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 511;BA.debugLine="am2 = (am1 + am3)/2";
mostCurrent._am2 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am1))+(double)(Double.parseDouble(mostCurrent._am3)))/(double)2);
 };
 //BA.debugLineNum = 513;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3))});
 };
 //BA.debugLineNum = 515;BA.debugLine="If am4 <> tempZeroRange Then";
if ((mostCurrent._am4).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 516;BA.debugLine="If am3 = tempZeroRange Then";
if ((mostCurrent._am3).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 517;BA.debugLine="am3 = (am2 + am4)/2";
mostCurrent._am3 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am2))+(double)(Double.parseDouble(mostCurrent._am4)))/(double)2);
 };
 //BA.debugLineNum = 519;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4))});
 };
 //BA.debugLineNum = 521;BA.debugLine="If am5 <> tempZeroRange Then";
if ((mostCurrent._am5).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 522;BA.debugLine="If am4 = tempZeroRange Then";
if ((mostCurrent._am4).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 523;BA.debugLine="am4 = (am3 + am5)/2";
mostCurrent._am4 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am3))+(double)(Double.parseDouble(mostCurrent._am5)))/(double)2);
 };
 //BA.debugLineNum = 525;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5))});
 };
 //BA.debugLineNum = 527;BA.debugLine="If am6 <> tempZeroRange Then";
if ((mostCurrent._am6).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 528;BA.debugLine="If am5 = tempZeroRange Then";
if ((mostCurrent._am5).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 529;BA.debugLine="am5 = (am4 + am6)/2";
mostCurrent._am5 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am4))+(double)(Double.parseDouble(mostCurrent._am6)))/(double)2);
 };
 //BA.debugLineNum = 531;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6))});
 };
 //BA.debugLineNum = 533;BA.debugLine="If am7 <> tempZeroRange Then";
if ((mostCurrent._am7).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 534;BA.debugLine="If am6 = tempZeroRange Then";
if ((mostCurrent._am6).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 535;BA.debugLine="am6 = (am5 + am7)/2";
mostCurrent._am6 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am5))+(double)(Double.parseDouble(mostCurrent._am7)))/(double)2);
 };
 //BA.debugLineNum = 537;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7))});
 };
 //BA.debugLineNum = 539;BA.debugLine="If am8 <> tempZeroRange Then";
if ((mostCurrent._am8).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 540;BA.debugLine="If am7 = tempZeroRange Then";
if ((mostCurrent._am7).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 541;BA.debugLine="am7 = (am6 + am8)/2";
mostCurrent._am7 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am6))+(double)(Double.parseDouble(mostCurrent._am8)))/(double)2);
 };
 //BA.debugLineNum = 543;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8))});
 };
 //BA.debugLineNum = 545;BA.debugLine="If am9 <> tempZeroRange Then";
if ((mostCurrent._am9).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 546;BA.debugLine="If am8 = tempZeroRange Then";
if ((mostCurrent._am8).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 547;BA.debugLine="am8 = (am7 + am9)/2";
mostCurrent._am8 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am7))+(double)(Double.parseDouble(mostCurrent._am9)))/(double)2);
 };
 //BA.debugLineNum = 549;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9))});
 };
 //BA.debugLineNum = 551;BA.debugLine="If am10 <> tempZeroRange Then";
if ((mostCurrent._am10).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 552;BA.debugLine="If am9 = tempZeroRange Then";
if ((mostCurrent._am9).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 553;BA.debugLine="am9 = (am8 + am10)/2";
mostCurrent._am9 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am8))+(double)(Double.parseDouble(mostCurrent._am10)))/(double)2);
 };
 //BA.debugLineNum = 555;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10))});
 };
 //BA.debugLineNum = 557;BA.debugLine="If am11 <> tempZeroRange Then";
if ((mostCurrent._am11).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 558;BA.debugLine="If am10 = tempZeroRange Then";
if ((mostCurrent._am10).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 559;BA.debugLine="am10 = (am9 + am11)/2";
mostCurrent._am10 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am9))+(double)(Double.parseDouble(mostCurrent._am11)))/(double)2);
 };
 //BA.debugLineNum = 561;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11))});
 };
 //BA.debugLineNum = 563;BA.debugLine="If pm12 <> tempZeroRange Then";
if ((mostCurrent._pm12).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 564;BA.debugLine="If am11 = tempZeroRange Then";
if ((mostCurrent._am11).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 565;BA.debugLine="am11 = (am10 + pm12)/2";
mostCurrent._am11 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am10))+(double)(Double.parseDouble(mostCurrent._pm12)))/(double)2);
 };
 //BA.debugLineNum = 567;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12))});
 };
 //BA.debugLineNum = 569;BA.debugLine="If pm1 <> tempZeroRange Then";
if ((mostCurrent._pm1).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 570;BA.debugLine="If pm12 = tempZeroRange Then";
if ((mostCurrent._pm12).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 571;BA.debugLine="pm12 = (am11 + pm1)/2";
mostCurrent._pm12 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am11))+(double)(Double.parseDouble(mostCurrent._pm1)))/(double)2);
 };
 //BA.debugLineNum = 573;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1))});
 };
 //BA.debugLineNum = 575;BA.debugLine="If pm2 <> tempZeroRange Then";
if ((mostCurrent._pm2).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 576;BA.debugLine="If pm1 = tempZeroRange Then";
if ((mostCurrent._pm1).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 577;BA.debugLine="pm1 = (pm12 + pm2)/2";
mostCurrent._pm1 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm12))+(double)(Double.parseDouble(mostCurrent._pm2)))/(double)2);
 };
 //BA.debugLineNum = 579;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2))});
 };
 //BA.debugLineNum = 581;BA.debugLine="If pm3 <> tempZeroRange Then";
if ((mostCurrent._pm3).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 582;BA.debugLine="If pm2 = tempZeroRange Then";
if ((mostCurrent._pm2).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 583;BA.debugLine="pm2 = (pm1 + pm3)/2";
mostCurrent._pm2 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm1))+(double)(Double.parseDouble(mostCurrent._pm3)))/(double)2);
 };
 //BA.debugLineNum = 585;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3))});
 };
 //BA.debugLineNum = 587;BA.debugLine="If pm4 <> tempZeroRange Then";
if ((mostCurrent._pm4).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 588;BA.debugLine="If pm3 = tempZeroRange Then";
if ((mostCurrent._pm3).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 589;BA.debugLine="pm3 = (pm2 + pm4)/2";
mostCurrent._pm3 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm2))+(double)(Double.parseDouble(mostCurrent._pm4)))/(double)2);
 };
 //BA.debugLineNum = 591;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4))});
 };
 //BA.debugLineNum = 593;BA.debugLine="If pm5 <> tempZeroRange Then";
if ((mostCurrent._pm5).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 594;BA.debugLine="If pm4 = tempZeroRange Then";
if ((mostCurrent._pm4).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 595;BA.debugLine="pm4 = (pm3 + pm5)/2";
mostCurrent._pm4 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm3))+(double)(Double.parseDouble(mostCurrent._pm5)))/(double)2);
 };
 //BA.debugLineNum = 597;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5))});
 };
 //BA.debugLineNum = 599;BA.debugLine="If pm6 <> tempZeroRange Then";
if ((mostCurrent._pm6).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 600;BA.debugLine="If pm5 = tempZeroRange Then";
if ((mostCurrent._pm5).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 601;BA.debugLine="pm5 = (pm4 + pm6)/2";
mostCurrent._pm5 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm4))+(double)(Double.parseDouble(mostCurrent._pm6)))/(double)2);
 };
 //BA.debugLineNum = 603;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5)),(float)(Double.parseDouble(mostCurrent._pm6))});
 };
 //BA.debugLineNum = 605;BA.debugLine="If pm7 <> tempZeroRange Then";
if ((mostCurrent._pm7).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 606;BA.debugLine="If pm6 = tempZeroRange Then";
if ((mostCurrent._pm6).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 607;BA.debugLine="pm6 = (pm5 + pm7)/2";
mostCurrent._pm6 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm5))+(double)(Double.parseDouble(mostCurrent._pm7)))/(double)2);
 };
 //BA.debugLineNum = 609;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5)),(float)(Double.parseDouble(mostCurrent._pm6)),(float)(Double.parseDouble(mostCurrent._pm7))});
 };
 //BA.debugLineNum = 611;BA.debugLine="If pm8 <> tempZeroRange Then";
if ((mostCurrent._pm8).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 612;BA.debugLine="If pm7 = tempZeroRange Then";
if ((mostCurrent._pm7).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 613;BA.debugLine="pm7 = (pm6 + pm8)/2";
mostCurrent._pm7 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm6))+(double)(Double.parseDouble(mostCurrent._pm8)))/(double)2);
 };
 //BA.debugLineNum = 615;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5)),(float)(Double.parseDouble(mostCurrent._pm6)),(float)(Double.parseDouble(mostCurrent._pm7)),(float)(Double.parseDouble(mostCurrent._pm8))});
 };
 //BA.debugLineNum = 617;BA.debugLine="If pm9 <> tempZeroRange Then";
if ((mostCurrent._pm9).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 618;BA.debugLine="If pm8 = tempZeroRange Then";
if ((mostCurrent._pm8).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 619;BA.debugLine="pm8 = (pm7 + pm9)/2";
mostCurrent._pm8 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm7))+(double)(Double.parseDouble(mostCurrent._pm9)))/(double)2);
 };
 //BA.debugLineNum = 621;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5)),(float)(Double.parseDouble(mostCurrent._pm6)),(float)(Double.parseDouble(mostCurrent._pm7)),(float)(Double.parseDouble(mostCurrent._pm8)),(float)(Double.parseDouble(mostCurrent._pm9))});
 };
 //BA.debugLineNum = 623;BA.debugLine="If pm10 <> tempZeroRange Then";
if ((mostCurrent._pm10).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 624;BA.debugLine="If pm9 = tempZeroRange Then";
if ((mostCurrent._pm9).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 625;BA.debugLine="pm9 = (pm8 + pm10)/2";
mostCurrent._pm9 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm8))+(double)(Double.parseDouble(mostCurrent._pm10)))/(double)2);
 };
 //BA.debugLineNum = 627;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5)),(float)(Double.parseDouble(mostCurrent._pm6)),(float)(Double.parseDouble(mostCurrent._pm7)),(float)(Double.parseDouble(mostCurrent._pm8)),(float)(Double.parseDouble(mostCurrent._pm9)),(float)(Double.parseDouble(mostCurrent._pm10))});
 };
 //BA.debugLineNum = 629;BA.debugLine="If pm11 <> tempZeroRange Then";
if ((mostCurrent._pm11).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 630;BA.debugLine="If pm10 = tempZeroRange Then";
if ((mostCurrent._pm10).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 631;BA.debugLine="pm10 = (pm9 + pm11)/2";
mostCurrent._pm10 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm9))+(double)(Double.parseDouble(mostCurrent._pm11)))/(double)2);
 };
 //BA.debugLineNum = 633;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5)),(float)(Double.parseDouble(mostCurrent._pm6)),(float)(Double.parseDouble(mostCurrent._pm7)),(float)(Double.parseDouble(mostCurrent._pm8)),(float)(Double.parseDouble(mostCurrent._pm9)),(float)(Double.parseDouble(mostCurrent._pm10)),(float)(Double.parseDouble(mostCurrent._pm11))});
 };
 //BA.debugLineNum = 636;BA.debugLine="LineChart.Line_1_PointLabelTextColor = Colors.Ye";
mostCurrent._linechart.setLine_1_PointLabelTextColor(anywheresoftware.b4a.keywords.Common.Colors.Yellow);
 //BA.debugLineNum = 637;BA.debugLine="LineChart.Line_1_PointLabelTextSize = 35.0";
mostCurrent._linechart.setLine_1_PointLabelTextSize((float) (35.0));
 //BA.debugLineNum = 638;BA.debugLine="LineChart.Line_1_LineColor = Colors.Red";
mostCurrent._linechart.setLine_1_LineColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 639;BA.debugLine="LineChart.Line_1_LineWidth = 11.0";
mostCurrent._linechart.setLine_1_LineWidth((float) (11.0));
 //BA.debugLineNum = 640;BA.debugLine="LineChart.Line_1_PointColor = Colors.Black";
mostCurrent._linechart.setLine_1_PointColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 //BA.debugLineNum = 641;BA.debugLine="LineChart.Line_1_PointSize = 25.0";
mostCurrent._linechart.setLine_1_PointSize((float) (25.0));
 //BA.debugLineNum = 642;BA.debugLine="LineChart.Line_1_PointShape = LineChart.SHAPE_RO";
mostCurrent._linechart.setLine_1_PointShape(mostCurrent._linechart.SHAPE_ROUND);
 //BA.debugLineNum = 643;BA.debugLine="LineChart.Line_1_DrawDash = False";
mostCurrent._linechart.setLine_1_DrawDash(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 644;BA.debugLine="LineChart.Line_1_DrawCubic = False";
mostCurrent._linechart.setLine_1_DrawCubic(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 651;BA.debugLine="LineChart.Line_2_Data = Array As Float (tempRigh";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow))});
 //BA.debugLineNum = 652;BA.debugLine="LineChart.Line_2_PointLabelTextColor = Colors.Gr";
mostCurrent._linechart.setLine_2_PointLabelTextColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 653;BA.debugLine="LineChart.Line_2_PointLabelTextSize = 30.0";
mostCurrent._linechart.setLine_2_PointLabelTextSize((float) (30.0));
 //BA.debugLineNum = 654;BA.debugLine="LineChart.Line_2_LineColor = Colors.Green";
mostCurrent._linechart.setLine_2_LineColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 655;BA.debugLine="LineChart.Line_2_LineWidth = 5.0";
mostCurrent._linechart.setLine_2_LineWidth((float) (5.0));
 //BA.debugLineNum = 656;BA.debugLine="LineChart.Line_2_PointColor = Colors.Green";
mostCurrent._linechart.setLine_2_PointColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 657;BA.debugLine="LineChart.Line_2_PointSize = 1.0";
mostCurrent._linechart.setLine_2_PointSize((float) (1.0));
 //BA.debugLineNum = 658;BA.debugLine="LineChart.Line_2_PointShape = LineChart.SHAPE_RO";
mostCurrent._linechart.setLine_2_PointShape(mostCurrent._linechart.SHAPE_ROUND);
 //BA.debugLineNum = 659;BA.debugLine="LineChart.Line_2_DrawDash = False";
mostCurrent._linechart.setLine_2_DrawDash(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 660;BA.debugLine="LineChart.Line_2_DrawCubic = False";
mostCurrent._linechart.setLine_2_DrawCubic(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 664;BA.debugLine="LineChart.NumberOfLineCharts = 2";
mostCurrent._linechart.setNumberOfLineCharts((int) (2));
 //BA.debugLineNum = 666;BA.debugLine="LineChart.DrawTheGraphs";
mostCurrent._linechart.DrawTheGraphs();
 } 
       catch (Exception e229) {
			processBA.setLastException(e229); //BA.debugLineNum = 669;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("6655623",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 //BA.debugLineNum = 670;BA.debugLine="ToastMessageShow (LastException,True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getObject()),anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 672;BA.debugLine="End Sub";
return "";
}
public static String  _humidityhourlytimer_tick() throws Exception{
 //BA.debugLineNum = 2208;BA.debugLine="Sub HumidityHourlyTimer_Tick";
 //BA.debugLineNum = 2209;BA.debugLine="Activity.RequestFocus";
mostCurrent._activity.RequestFocus();
 //BA.debugLineNum = 2210;BA.debugLine="btnHumidityHourly.RemoveView";
mostCurrent._btnhumidityhourly.RemoveView();
 //BA.debugLineNum = 2211;BA.debugLine="btnTempHourly.RemoveView";
mostCurrent._btntemphourly.RemoveView();
 //BA.debugLineNum = 2212;BA.debugLine="btnHumidityDaily.RemoveView";
mostCurrent._btnhumiditydaily.RemoveView();
 //BA.debugLineNum = 2213;BA.debugLine="btnTempDaily.RemoveView";
mostCurrent._btntempdaily.RemoveView();
 //BA.debugLineNum = 2214;BA.debugLine="LineChart.RemoveView";
mostCurrent._linechart.RemoveView();
 //BA.debugLineNum = 2215;BA.debugLine="tempMaxRange=0";
_tempmaxrange = (float) (0);
 //BA.debugLineNum = 2216;BA.debugLine="tempMinRange=0";
_tempminrange = (float) (0);
 //BA.debugLineNum = 2217;BA.debugLine="HumidityHourlyCreate";
_humidityhourlycreate();
 //BA.debugLineNum = 2218;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        b4a.example.dateutils._process_globals();
main._process_globals();
starter._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 16;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 19;BA.debugLine="Private xui As XUI";
_xui = new anywheresoftware.b4a.objects.B4XViewWrapper.XUI();
 //BA.debugLineNum = 20;BA.debugLine="Private Awake As PhoneWakeState";
_awake = new anywheresoftware.b4a.phone.Phone.PhoneWakeState();
 //BA.debugLineNum = 21;BA.debugLine="Private TemperatureHourlyTimer As Timer";
_temperaturehourlytimer = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 22;BA.debugLine="Private HumidityHourlyTimer As Timer";
_humidityhourlytimer = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 23;BA.debugLine="Private TemperatureDailyTimer As Timer";
_temperaturedailytimer = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 24;BA.debugLine="Private HumidityDailyTimer As Timer";
_humiditydailytimer = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 25;BA.debugLine="End Sub";
return "";
}
public static String  _readhumiditydaily(String _fileday) throws Exception{
anywheresoftware.b4a.objects.streams.File.TextReaderWrapper _textreader1 = null;
long _now = 0L;
int _month = 0;
int _day = 0;
int _year = 0;
String _filename = "";
String _line = "";
String[] _a = null;
String _timestamp = "";
 //BA.debugLineNum = 1655;BA.debugLine="Sub ReadHumidityDaily(fileDay As String)";
 //BA.debugLineNum = 1656;BA.debugLine="Try";
try { //BA.debugLineNum = 1657;BA.debugLine="Dim TextReader1 As TextReader";
_textreader1 = new anywheresoftware.b4a.objects.streams.File.TextReaderWrapper();
 //BA.debugLineNum = 1658;BA.debugLine="Dim Now As Long";
_now = 0L;
 //BA.debugLineNum = 1659;BA.debugLine="Dim Month As Int";
_month = 0;
 //BA.debugLineNum = 1660;BA.debugLine="Dim Day As Int";
_day = 0;
 //BA.debugLineNum = 1661;BA.debugLine="Dim Year As Int";
_year = 0;
 //BA.debugLineNum = 1662;BA.debugLine="Dim FileName As String";
_filename = "";
 //BA.debugLineNum = 1664;BA.debugLine="am12 = zeroRange";
mostCurrent._am12 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1665;BA.debugLine="am1 = zeroRange";
mostCurrent._am1 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1666;BA.debugLine="am2 = zeroRange";
mostCurrent._am2 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1667;BA.debugLine="am3 = zeroRange";
mostCurrent._am3 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1668;BA.debugLine="am4 = zeroRange";
mostCurrent._am4 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1669;BA.debugLine="am5 = zeroRange";
mostCurrent._am5 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1670;BA.debugLine="am6 = zeroRange";
mostCurrent._am6 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1671;BA.debugLine="am7 = zeroRange";
mostCurrent._am7 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1672;BA.debugLine="am8 = zeroRange";
mostCurrent._am8 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1673;BA.debugLine="am9 = zeroRange";
mostCurrent._am9 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1674;BA.debugLine="am10 = zeroRange";
mostCurrent._am10 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1675;BA.debugLine="am11 = zeroRange";
mostCurrent._am11 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1676;BA.debugLine="pm12 = zeroRange";
mostCurrent._pm12 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1677;BA.debugLine="pm1 = zeroRange";
mostCurrent._pm1 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1678;BA.debugLine="pm2 = zeroRange";
mostCurrent._pm2 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1679;BA.debugLine="pm3 = zeroRange";
mostCurrent._pm3 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1680;BA.debugLine="pm4 = zeroRange";
mostCurrent._pm4 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1681;BA.debugLine="pm5 = zeroRange";
mostCurrent._pm5 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1682;BA.debugLine="pm6 = zeroRange";
mostCurrent._pm6 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1683;BA.debugLine="pm7 = zeroRange";
mostCurrent._pm7 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1684;BA.debugLine="pm8 = zeroRange";
mostCurrent._pm8 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1685;BA.debugLine="pm9 = zeroRange";
mostCurrent._pm9 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1686;BA.debugLine="pm10 = zeroRange";
mostCurrent._pm10 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1687;BA.debugLine="pm11 = zeroRange";
mostCurrent._pm11 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1689;BA.debugLine="Now = DateTime.Now";
_now = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 1690;BA.debugLine="Month = DateTime.GetMonth(Now)";
_month = anywheresoftware.b4a.keywords.Common.DateTime.GetMonth(_now);
 //BA.debugLineNum = 1691;BA.debugLine="Day = DateTime.GetDayOfMonth (Now)";
_day = anywheresoftware.b4a.keywords.Common.DateTime.GetDayOfMonth(_now);
 //BA.debugLineNum = 1692;BA.debugLine="Year = DateTime.GetYear(Now)";
_year = anywheresoftware.b4a.keywords.Common.DateTime.GetYear(_now);
 //BA.debugLineNum = 1694;BA.debugLine="If fileDay = \"Today\" Then";
if ((_fileday).equals("Today")) { 
 //BA.debugLineNum = 1695;BA.debugLine="FileName = \"LivingRoomTempHumid_\" & Year & \"-\"";
_filename = "LivingRoomTempHumid_"+BA.NumberToString(_year)+"-"+anywheresoftware.b4a.keywords.Common.NumberFormat(_month,(int) (2),(int) (0))+"-"+anywheresoftware.b4a.keywords.Common.NumberFormat(_day,(int) (2),(int) (0))+".log";
 }else {
 //BA.debugLineNum = 1697;BA.debugLine="Now = DateTime.add(DateTime.Now, 0, 0, -1)";
_now = anywheresoftware.b4a.keywords.Common.DateTime.Add(anywheresoftware.b4a.keywords.Common.DateTime.getNow(),(int) (0),(int) (0),(int) (-1));
 //BA.debugLineNum = 1698;BA.debugLine="Month = DateTime.GetMonth(Now)";
_month = anywheresoftware.b4a.keywords.Common.DateTime.GetMonth(_now);
 //BA.debugLineNum = 1699;BA.debugLine="Day = DateTime.GetDayOfMonth (Now)";
_day = anywheresoftware.b4a.keywords.Common.DateTime.GetDayOfMonth(_now);
 //BA.debugLineNum = 1700;BA.debugLine="Year = DateTime.GetYear(Now)";
_year = anywheresoftware.b4a.keywords.Common.DateTime.GetYear(_now);
 //BA.debugLineNum = 1701;BA.debugLine="FileName = \"LivingRoomTempHumid_\" & Year & \"-\"";
_filename = "LivingRoomTempHumid_"+BA.NumberToString(_year)+"-"+anywheresoftware.b4a.keywords.Common.NumberFormat(_month,(int) (2),(int) (0))+"-"+anywheresoftware.b4a.keywords.Common.NumberFormat(_day,(int) (2),(int) (0))+".log";
 };
 //BA.debugLineNum = 1704;BA.debugLine="TextReader1.Initialize(File.OpenInput(File.DirRo";
_textreader1.Initialize((java.io.InputStream)(anywheresoftware.b4a.keywords.Common.File.OpenInput(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),_filename).getObject()));
 //BA.debugLineNum = 1705;BA.debugLine="Dim line As String";
_line = "";
 //BA.debugLineNum = 1706;BA.debugLine="line = TextReader1.ReadLine";
_line = _textreader1.ReadLine();
 //BA.debugLineNum = 1707;BA.debugLine="Do While line <> Null";
while (_line!= null) {
 //BA.debugLineNum = 1709;BA.debugLine="line = TextReader1.ReadLine";
_line = _textreader1.ReadLine();
 //BA.debugLineNum = 1710;BA.debugLine="If line = Null Then";
if (_line== null) { 
 //BA.debugLineNum = 1711;BA.debugLine="Exit";
if (true) break;
 };
 //BA.debugLineNum = 1713;BA.debugLine="Dim a() As String = Regex.Split(\"\\|\",line)";
_a = anywheresoftware.b4a.keywords.Common.Regex.Split("\\|",_line);
 //BA.debugLineNum = 1714;BA.debugLine="If a.Length = 9 Then";
if (_a.length==9) { 
 //BA.debugLineNum = 1715;BA.debugLine="Dim timeStamp As String";
_timestamp = "";
 //BA.debugLineNum = 1716;BA.debugLine="timeStamp = a(0).SubString2(0,2)";
_timestamp = _a[(int) (0)].substring((int) (0),(int) (2));
 //BA.debugLineNum = 1718;BA.debugLine="If IsNumber(a(2)) = False Then Continue";
if (anywheresoftware.b4a.keywords.Common.IsNumber(_a[(int) (2)])==anywheresoftware.b4a.keywords.Common.False) { 
if (true) continue;};
 //BA.debugLineNum = 1720;BA.debugLine="Select timeStamp";
switch (BA.switchObjectToInt(_timestamp,"00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23")) {
case 0: {
 //BA.debugLineNum = 1722;BA.debugLine="If am12 = zeroRange Or am12 = \"\" Then am12 =";
if ((mostCurrent._am12).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am12).equals("")) { 
mostCurrent._am12 = _a[(int) (2)];};
 break; }
case 1: {
 //BA.debugLineNum = 1724;BA.debugLine="If am1 = zeroRange Or am1 = \"\" Then am1 = a(";
if ((mostCurrent._am1).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am1).equals("")) { 
mostCurrent._am1 = _a[(int) (2)];};
 break; }
case 2: {
 //BA.debugLineNum = 1726;BA.debugLine="If am2 = zeroRange Or am2 = \"\" Then am2 = a(";
if ((mostCurrent._am2).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am2).equals("")) { 
mostCurrent._am2 = _a[(int) (2)];};
 break; }
case 3: {
 //BA.debugLineNum = 1728;BA.debugLine="If am3 = zeroRange Or am3 = \"\" Then am3 = a(";
if ((mostCurrent._am3).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am3).equals("")) { 
mostCurrent._am3 = _a[(int) (2)];};
 break; }
case 4: {
 //BA.debugLineNum = 1730;BA.debugLine="If am4 = zeroRange Or am4 = \"\" Then am4 = a(";
if ((mostCurrent._am4).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am4).equals("")) { 
mostCurrent._am4 = _a[(int) (2)];};
 break; }
case 5: {
 //BA.debugLineNum = 1732;BA.debugLine="If am5 = zeroRange Or am5 = \"\" Then am5 = a(";
if ((mostCurrent._am5).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am5).equals("")) { 
mostCurrent._am5 = _a[(int) (2)];};
 break; }
case 6: {
 //BA.debugLineNum = 1734;BA.debugLine="If am6 = zeroRange Or am6 = \"\" Then am6 = a(";
if ((mostCurrent._am6).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am6).equals("")) { 
mostCurrent._am6 = _a[(int) (2)];};
 break; }
case 7: {
 //BA.debugLineNum = 1736;BA.debugLine="If am7 = zeroRange Or am7 = \"\" Then am7 = a(";
if ((mostCurrent._am7).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am7).equals("")) { 
mostCurrent._am7 = _a[(int) (2)];};
 break; }
case 8: {
 //BA.debugLineNum = 1738;BA.debugLine="If am8 = zeroRange Or am8 = \"\" Then am8 = a(";
if ((mostCurrent._am8).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am8).equals("")) { 
mostCurrent._am8 = _a[(int) (2)];};
 break; }
case 9: {
 //BA.debugLineNum = 1740;BA.debugLine="If am9 = zeroRange Or am9 = \"\" Then am9 = a(";
if ((mostCurrent._am9).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am9).equals("")) { 
mostCurrent._am9 = _a[(int) (2)];};
 break; }
case 10: {
 //BA.debugLineNum = 1742;BA.debugLine="If am10 = zeroRange Or am10 = \"\" Then am10 =";
if ((mostCurrent._am10).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am10).equals("")) { 
mostCurrent._am10 = _a[(int) (2)];};
 break; }
case 11: {
 //BA.debugLineNum = 1744;BA.debugLine="If am11 = zeroRange Or am11 = \"\" Then am11 =";
if ((mostCurrent._am11).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am11).equals("")) { 
mostCurrent._am11 = _a[(int) (2)];};
 break; }
case 12: {
 //BA.debugLineNum = 1746;BA.debugLine="If pm12 = zeroRange Or pm12 = \"\" Then pm12 =";
if ((mostCurrent._pm12).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm12).equals("")) { 
mostCurrent._pm12 = _a[(int) (2)];};
 break; }
case 13: {
 //BA.debugLineNum = 1748;BA.debugLine="If pm1 = zeroRange Or pm1 = \"\" Then pm1 = a(";
if ((mostCurrent._pm1).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm1).equals("")) { 
mostCurrent._pm1 = _a[(int) (2)];};
 break; }
case 14: {
 //BA.debugLineNum = 1750;BA.debugLine="If pm2 = zeroRange Or pm2 = \"\" Then pm2 = a(";
if ((mostCurrent._pm2).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm2).equals("")) { 
mostCurrent._pm2 = _a[(int) (2)];};
 break; }
case 15: {
 //BA.debugLineNum = 1752;BA.debugLine="If pm3 = zeroRange Or pm3 = \"\" Then pm3 = a(";
if ((mostCurrent._pm3).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm3).equals("")) { 
mostCurrent._pm3 = _a[(int) (2)];};
 break; }
case 16: {
 //BA.debugLineNum = 1754;BA.debugLine="If pm4 = zeroRange Or pm4 = \"\" Then pm4 = a(";
if ((mostCurrent._pm4).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm4).equals("")) { 
mostCurrent._pm4 = _a[(int) (2)];};
 break; }
case 17: {
 //BA.debugLineNum = 1756;BA.debugLine="If pm5 = zeroRange Or pm5 = \"\" Then pm5 = a(";
if ((mostCurrent._pm5).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm5).equals("")) { 
mostCurrent._pm5 = _a[(int) (2)];};
 break; }
case 18: {
 //BA.debugLineNum = 1758;BA.debugLine="If pm6 = zeroRange Or pm6 = \"\" Then pm6 = a(";
if ((mostCurrent._pm6).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm6).equals("")) { 
mostCurrent._pm6 = _a[(int) (2)];};
 break; }
case 19: {
 //BA.debugLineNum = 1760;BA.debugLine="If pm7 = zeroRange Or pm7 = \"\" Then pm7 = a(";
if ((mostCurrent._pm7).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm7).equals("")) { 
mostCurrent._pm7 = _a[(int) (2)];};
 break; }
case 20: {
 //BA.debugLineNum = 1762;BA.debugLine="If pm8 = zeroRange Or pm8 = \"\" Then pm8 = a(";
if ((mostCurrent._pm8).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm8).equals("")) { 
mostCurrent._pm8 = _a[(int) (2)];};
 break; }
case 21: {
 //BA.debugLineNum = 1764;BA.debugLine="If pm9 = zeroRange Or pm9 = \"\" Then pm9 = a(";
if ((mostCurrent._pm9).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm9).equals("")) { 
mostCurrent._pm9 = _a[(int) (2)];};
 break; }
case 22: {
 //BA.debugLineNum = 1766;BA.debugLine="If pm10 = zeroRange Or pm10 = \"\" Then pm10 =";
if ((mostCurrent._pm10).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm10).equals("")) { 
mostCurrent._pm10 = _a[(int) (2)];};
 break; }
case 23: {
 //BA.debugLineNum = 1768;BA.debugLine="If pm11 = zeroRange Or pm11 = \"\" Then pm11 =";
if ((mostCurrent._pm11).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm11).equals("")) { 
mostCurrent._pm11 = _a[(int) (2)];};
 break; }
}
;
 //BA.debugLineNum = 1770;BA.debugLine="If fileDay = \"Today\" Then";
if ((_fileday).equals("Today")) { 
 //BA.debugLineNum = 1771;BA.debugLine="tempRightNow = a(2)";
mostCurrent._temprightnow = _a[(int) (2)];
 };
 };
 }
;
 //BA.debugLineNum = 1776;BA.debugLine="TextReader1.Close";
_textreader1.Close();
 } 
       catch (Exception e115) {
			processBA.setLastException(e115); //BA.debugLineNum = 1778;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("6983163",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 };
 //BA.debugLineNum = 1780;BA.debugLine="End Sub";
return "";
}
public static String  _readhumidityhourly(String _fileday) throws Exception{
anywheresoftware.b4a.objects.streams.File.TextReaderWrapper _textreader1 = null;
long _now = 0L;
int _month = 0;
int _day = 0;
int _year = 0;
String _filename = "";
String _line = "";
String[] _a = null;
String _timestamp = "";
 //BA.debugLineNum = 1912;BA.debugLine="Sub ReadHumidityHourly(fileDay As String)";
 //BA.debugLineNum = 1913;BA.debugLine="Try";
try { //BA.debugLineNum = 1914;BA.debugLine="Dim TextReader1 As TextReader";
_textreader1 = new anywheresoftware.b4a.objects.streams.File.TextReaderWrapper();
 //BA.debugLineNum = 1915;BA.debugLine="Dim Now As Long";
_now = 0L;
 //BA.debugLineNum = 1916;BA.debugLine="Dim Month As Int";
_month = 0;
 //BA.debugLineNum = 1917;BA.debugLine="Dim Day As Int";
_day = 0;
 //BA.debugLineNum = 1918;BA.debugLine="Dim Year As Int";
_year = 0;
 //BA.debugLineNum = 1919;BA.debugLine="Dim FileName As String";
_filename = "";
 //BA.debugLineNum = 1921;BA.debugLine="am12 = zeroRange";
mostCurrent._am12 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1922;BA.debugLine="am1 = zeroRange";
mostCurrent._am1 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1923;BA.debugLine="am2 = zeroRange";
mostCurrent._am2 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1924;BA.debugLine="am3 = zeroRange";
mostCurrent._am3 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1925;BA.debugLine="am4 = zeroRange";
mostCurrent._am4 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1926;BA.debugLine="am5 = zeroRange";
mostCurrent._am5 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1927;BA.debugLine="am6 = zeroRange";
mostCurrent._am6 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1928;BA.debugLine="am7 = zeroRange";
mostCurrent._am7 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1929;BA.debugLine="am8 = zeroRange";
mostCurrent._am8 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1930;BA.debugLine="am9 = zeroRange";
mostCurrent._am9 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1931;BA.debugLine="am10 = zeroRange";
mostCurrent._am10 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1932;BA.debugLine="am11 = zeroRange";
mostCurrent._am11 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1933;BA.debugLine="pm12 = zeroRange";
mostCurrent._pm12 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1934;BA.debugLine="pm1 = zeroRange";
mostCurrent._pm1 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1935;BA.debugLine="pm2 = zeroRange";
mostCurrent._pm2 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1936;BA.debugLine="pm3 = zeroRange";
mostCurrent._pm3 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1937;BA.debugLine="pm4 = zeroRange";
mostCurrent._pm4 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1938;BA.debugLine="pm5 = zeroRange";
mostCurrent._pm5 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1939;BA.debugLine="pm6 = zeroRange";
mostCurrent._pm6 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1940;BA.debugLine="pm7 = zeroRange";
mostCurrent._pm7 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1941;BA.debugLine="pm8 = zeroRange";
mostCurrent._pm8 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1942;BA.debugLine="pm9 = zeroRange";
mostCurrent._pm9 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1943;BA.debugLine="pm10 = zeroRange";
mostCurrent._pm10 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1944;BA.debugLine="pm11 = zeroRange";
mostCurrent._pm11 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1946;BA.debugLine="Now = DateTime.Now";
_now = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 1947;BA.debugLine="Month = DateTime.GetMonth(Now)";
_month = anywheresoftware.b4a.keywords.Common.DateTime.GetMonth(_now);
 //BA.debugLineNum = 1948;BA.debugLine="Day = DateTime.GetDayOfMonth (Now)";
_day = anywheresoftware.b4a.keywords.Common.DateTime.GetDayOfMonth(_now);
 //BA.debugLineNum = 1949;BA.debugLine="Year = DateTime.GetYear(Now)";
_year = anywheresoftware.b4a.keywords.Common.DateTime.GetYear(_now);
 //BA.debugLineNum = 1951;BA.debugLine="If fileDay = \"Today\" Then";
if ((_fileday).equals("Today")) { 
 //BA.debugLineNum = 1952;BA.debugLine="FileName = \"LivingRoomTempHumid_\" & Year & \"-\"";
_filename = "LivingRoomTempHumid_"+BA.NumberToString(_year)+"-"+anywheresoftware.b4a.keywords.Common.NumberFormat(_month,(int) (2),(int) (0))+"-"+anywheresoftware.b4a.keywords.Common.NumberFormat(_day,(int) (2),(int) (0))+".log";
 }else {
 //BA.debugLineNum = 1954;BA.debugLine="Now = DateTime.add(DateTime.Now, 0, 0, -1)";
_now = anywheresoftware.b4a.keywords.Common.DateTime.Add(anywheresoftware.b4a.keywords.Common.DateTime.getNow(),(int) (0),(int) (0),(int) (-1));
 //BA.debugLineNum = 1955;BA.debugLine="Month = DateTime.GetMonth(Now)";
_month = anywheresoftware.b4a.keywords.Common.DateTime.GetMonth(_now);
 //BA.debugLineNum = 1956;BA.debugLine="Day = DateTime.GetDayOfMonth (Now)";
_day = anywheresoftware.b4a.keywords.Common.DateTime.GetDayOfMonth(_now);
 //BA.debugLineNum = 1957;BA.debugLine="Year = DateTime.GetYear(Now)";
_year = anywheresoftware.b4a.keywords.Common.DateTime.GetYear(_now);
 //BA.debugLineNum = 1958;BA.debugLine="FileName = \"LivingRoomTempHumid_\" & Year & \"-\"";
_filename = "LivingRoomTempHumid_"+BA.NumberToString(_year)+"-"+anywheresoftware.b4a.keywords.Common.NumberFormat(_month,(int) (2),(int) (0))+"-"+anywheresoftware.b4a.keywords.Common.NumberFormat(_day,(int) (2),(int) (0))+".log";
 };
 //BA.debugLineNum = 1961;BA.debugLine="TextReader1.Initialize(File.OpenInput(File.DirRo";
_textreader1.Initialize((java.io.InputStream)(anywheresoftware.b4a.keywords.Common.File.OpenInput(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),_filename).getObject()));
 //BA.debugLineNum = 1962;BA.debugLine="Dim line As String";
_line = "";
 //BA.debugLineNum = 1963;BA.debugLine="line = TextReader1.ReadLine";
_line = _textreader1.ReadLine();
 //BA.debugLineNum = 1964;BA.debugLine="Do While line <> Null";
while (_line!= null) {
 //BA.debugLineNum = 1966;BA.debugLine="line = TextReader1.ReadLine";
_line = _textreader1.ReadLine();
 //BA.debugLineNum = 1967;BA.debugLine="If line = Null Then";
if (_line== null) { 
 //BA.debugLineNum = 1968;BA.debugLine="Exit";
if (true) break;
 };
 //BA.debugLineNum = 1970;BA.debugLine="Dim a() As String = Regex.Split(\"\\|\",line)";
_a = anywheresoftware.b4a.keywords.Common.Regex.Split("\\|",_line);
 //BA.debugLineNum = 1971;BA.debugLine="If a.Length = 9 Then";
if (_a.length==9) { 
 //BA.debugLineNum = 1972;BA.debugLine="Dim timeStamp As String";
_timestamp = "";
 //BA.debugLineNum = 1973;BA.debugLine="timeStamp = a(0).SubString2(0,5)";
_timestamp = _a[(int) (0)].substring((int) (0),(int) (5));
 //BA.debugLineNum = 1975;BA.debugLine="If IsNumber(a(2)) = False Then Continue";
if (anywheresoftware.b4a.keywords.Common.IsNumber(_a[(int) (2)])==anywheresoftware.b4a.keywords.Common.False) { 
if (true) continue;};
 //BA.debugLineNum = 1977;BA.debugLine="Select timeStamp";
switch (BA.switchObjectToInt(_timestamp,mostCurrent._timearray[(int) (0)],mostCurrent._timearray[(int) (1)],mostCurrent._timearray[(int) (2)],mostCurrent._timearray[(int) (3)],mostCurrent._timearray[(int) (4)],mostCurrent._timearray[(int) (5)],mostCurrent._timearray[(int) (6)],mostCurrent._timearray[(int) (7)],mostCurrent._timearray[(int) (8)],mostCurrent._timearray[(int) (9)],mostCurrent._timearray[(int) (10)],mostCurrent._timearray[(int) (11)],mostCurrent._timearray[(int) (12)],mostCurrent._timearray[(int) (13)],mostCurrent._timearray[(int) (14)],mostCurrent._timearray[(int) (15)],mostCurrent._timearray[(int) (16)],mostCurrent._timearray[(int) (17)],mostCurrent._timearray[(int) (18)],mostCurrent._timearray[(int) (19)],mostCurrent._timearray[(int) (20)],mostCurrent._timearray[(int) (21)],mostCurrent._timearray[(int) (22)],mostCurrent._timearray[(int) (23)])) {
case 0: {
 //BA.debugLineNum = 1979;BA.debugLine="If am12 = zeroRange Or am12 = \"\" Then am12 =";
if ((mostCurrent._am12).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am12).equals("")) { 
mostCurrent._am12 = _a[(int) (2)];};
 break; }
case 1: {
 //BA.debugLineNum = 1981;BA.debugLine="If am1 = zeroRange Or am1 = \"\" Then am1 = a(";
if ((mostCurrent._am1).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am1).equals("")) { 
mostCurrent._am1 = _a[(int) (2)];};
 break; }
case 2: {
 //BA.debugLineNum = 1983;BA.debugLine="If am2 = zeroRange Or am2 = \"\" Then am2 = a(";
if ((mostCurrent._am2).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am2).equals("")) { 
mostCurrent._am2 = _a[(int) (2)];};
 break; }
case 3: {
 //BA.debugLineNum = 1985;BA.debugLine="If am3 = zeroRange Or am3 = \"\" Then am3 = a(";
if ((mostCurrent._am3).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am3).equals("")) { 
mostCurrent._am3 = _a[(int) (2)];};
 break; }
case 4: {
 //BA.debugLineNum = 1987;BA.debugLine="If am4 = zeroRange Or am4 = \"\" Then am4 = a(";
if ((mostCurrent._am4).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am4).equals("")) { 
mostCurrent._am4 = _a[(int) (2)];};
 break; }
case 5: {
 //BA.debugLineNum = 1989;BA.debugLine="If am5 = zeroRange Or am5 = \"\" Then am5 = a(";
if ((mostCurrent._am5).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am5).equals("")) { 
mostCurrent._am5 = _a[(int) (2)];};
 break; }
case 6: {
 //BA.debugLineNum = 1991;BA.debugLine="If am6 = zeroRange Or am6 = \"\" Then am6 = a(";
if ((mostCurrent._am6).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am6).equals("")) { 
mostCurrent._am6 = _a[(int) (2)];};
 break; }
case 7: {
 //BA.debugLineNum = 1993;BA.debugLine="If am7 = zeroRange Or am7 = \"\" Then am7 = a(";
if ((mostCurrent._am7).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am7).equals("")) { 
mostCurrent._am7 = _a[(int) (2)];};
 break; }
case 8: {
 //BA.debugLineNum = 1995;BA.debugLine="If am8 = zeroRange Or am8 = \"\" Then am8 = a(";
if ((mostCurrent._am8).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am8).equals("")) { 
mostCurrent._am8 = _a[(int) (2)];};
 break; }
case 9: {
 //BA.debugLineNum = 1997;BA.debugLine="If am9 = zeroRange Or am9 = \"\" Then am9 = a(";
if ((mostCurrent._am9).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am9).equals("")) { 
mostCurrent._am9 = _a[(int) (2)];};
 break; }
case 10: {
 //BA.debugLineNum = 1999;BA.debugLine="If am10 = zeroRange Or am10 = \"\" Then am10 =";
if ((mostCurrent._am10).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am10).equals("")) { 
mostCurrent._am10 = _a[(int) (2)];};
 break; }
case 11: {
 //BA.debugLineNum = 2001;BA.debugLine="If am11 = zeroRange Or am11 = \"\" Then am11 =";
if ((mostCurrent._am11).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am11).equals("")) { 
mostCurrent._am11 = _a[(int) (2)];};
 break; }
case 12: {
 //BA.debugLineNum = 2003;BA.debugLine="If pm12 = zeroRange Or pm12 = \"\" Then pm12 =";
if ((mostCurrent._pm12).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm12).equals("")) { 
mostCurrent._pm12 = _a[(int) (2)];};
 break; }
case 13: {
 //BA.debugLineNum = 2005;BA.debugLine="If pm1 = zeroRange Or pm1 = \"\" Then pm1 = a(";
if ((mostCurrent._pm1).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm1).equals("")) { 
mostCurrent._pm1 = _a[(int) (2)];};
 break; }
case 14: {
 //BA.debugLineNum = 2007;BA.debugLine="If pm2 = zeroRange Or pm2 = \"\" Then pm2 = a(";
if ((mostCurrent._pm2).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm2).equals("")) { 
mostCurrent._pm2 = _a[(int) (2)];};
 break; }
case 15: {
 //BA.debugLineNum = 2009;BA.debugLine="If pm3 = zeroRange Or pm3 = \"\" Then pm3 = a(";
if ((mostCurrent._pm3).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm3).equals("")) { 
mostCurrent._pm3 = _a[(int) (2)];};
 break; }
case 16: {
 //BA.debugLineNum = 2011;BA.debugLine="If pm4 = zeroRange Or pm4 = \"\" Then pm4 = a(";
if ((mostCurrent._pm4).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm4).equals("")) { 
mostCurrent._pm4 = _a[(int) (2)];};
 break; }
case 17: {
 //BA.debugLineNum = 2013;BA.debugLine="If pm5 = zeroRange Or pm5 = \"\" Then pm5 = a(";
if ((mostCurrent._pm5).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm5).equals("")) { 
mostCurrent._pm5 = _a[(int) (2)];};
 break; }
case 18: {
 //BA.debugLineNum = 2015;BA.debugLine="If pm6 = zeroRange Or pm6 = \"\" Then pm6 = a(";
if ((mostCurrent._pm6).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm6).equals("")) { 
mostCurrent._pm6 = _a[(int) (2)];};
 break; }
case 19: {
 //BA.debugLineNum = 2017;BA.debugLine="If pm7 = zeroRange Or pm7 = \"\" Then pm7 = a(";
if ((mostCurrent._pm7).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm7).equals("")) { 
mostCurrent._pm7 = _a[(int) (2)];};
 break; }
case 20: {
 //BA.debugLineNum = 2019;BA.debugLine="If pm8 = zeroRange Or pm8 = \"\" Then pm8 = a(";
if ((mostCurrent._pm8).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm8).equals("")) { 
mostCurrent._pm8 = _a[(int) (2)];};
 break; }
case 21: {
 //BA.debugLineNum = 2021;BA.debugLine="If pm9 = zeroRange Or pm9 = \"\" Then pm9 = a(";
if ((mostCurrent._pm9).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm9).equals("")) { 
mostCurrent._pm9 = _a[(int) (2)];};
 break; }
case 22: {
 //BA.debugLineNum = 2023;BA.debugLine="If pm10 = zeroRange Or pm10 = \"\" Then pm10 =";
if ((mostCurrent._pm10).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm10).equals("")) { 
mostCurrent._pm10 = _a[(int) (2)];};
 break; }
case 23: {
 //BA.debugLineNum = 2025;BA.debugLine="If pm11 = zeroRange Or pm11 = \"\" Then pm11 =";
if ((mostCurrent._pm11).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm11).equals("")) { 
mostCurrent._pm11 = _a[(int) (2)];};
 break; }
}
;
 //BA.debugLineNum = 2027;BA.debugLine="If fileDay = \"Today\" Then";
if ((_fileday).equals("Today")) { 
 //BA.debugLineNum = 2028;BA.debugLine="tempRightNow = a(2)";
mostCurrent._temprightnow = _a[(int) (2)];
 //BA.debugLineNum = 2029;BA.debugLine="DateTime.TimeFormat = \"h:mm a\"";
anywheresoftware.b4a.keywords.Common.DateTime.setTimeFormat("h:mm a");
 };
 };
 }
;
 //BA.debugLineNum = 2034;BA.debugLine="TextReader1.Close";
_textreader1.Close();
 } 
       catch (Exception e116) {
			processBA.setLastException(e116); //BA.debugLineNum = 2038;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("61114238",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 };
 //BA.debugLineNum = 2040;BA.debugLine="End Sub";
return "";
}
public static String  _readtemperaturedaily(String _fileday) throws Exception{
anywheresoftware.b4a.objects.streams.File.TextReaderWrapper _textreader1 = null;
long _now = 0L;
int _month = 0;
int _day = 0;
int _year = 0;
String _filename = "";
String _line = "";
String[] _a = null;
String _timestamp = "";
 //BA.debugLineNum = 1528;BA.debugLine="Sub ReadTemperatureDaily(fileDay As String)";
 //BA.debugLineNum = 1529;BA.debugLine="Try";
try { //BA.debugLineNum = 1530;BA.debugLine="Dim TextReader1 As TextReader";
_textreader1 = new anywheresoftware.b4a.objects.streams.File.TextReaderWrapper();
 //BA.debugLineNum = 1531;BA.debugLine="Dim Now As Long";
_now = 0L;
 //BA.debugLineNum = 1532;BA.debugLine="Dim Month As Int";
_month = 0;
 //BA.debugLineNum = 1533;BA.debugLine="Dim Day As Int";
_day = 0;
 //BA.debugLineNum = 1534;BA.debugLine="Dim Year As Int";
_year = 0;
 //BA.debugLineNum = 1535;BA.debugLine="Dim FileName As String";
_filename = "";
 //BA.debugLineNum = 1537;BA.debugLine="am12 = zeroRange";
mostCurrent._am12 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1538;BA.debugLine="am1 = zeroRange";
mostCurrent._am1 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1539;BA.debugLine="am2 = zeroRange";
mostCurrent._am2 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1540;BA.debugLine="am3 = zeroRange";
mostCurrent._am3 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1541;BA.debugLine="am4 = zeroRange";
mostCurrent._am4 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1542;BA.debugLine="am5 = zeroRange";
mostCurrent._am5 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1543;BA.debugLine="am6 = zeroRange";
mostCurrent._am6 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1544;BA.debugLine="am7 = zeroRange";
mostCurrent._am7 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1545;BA.debugLine="am8 = zeroRange";
mostCurrent._am8 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1546;BA.debugLine="am9 = zeroRange";
mostCurrent._am9 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1547;BA.debugLine="am10 = zeroRange";
mostCurrent._am10 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1548;BA.debugLine="am11 = zeroRange";
mostCurrent._am11 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1549;BA.debugLine="pm12 = zeroRange";
mostCurrent._pm12 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1550;BA.debugLine="pm1 = zeroRange";
mostCurrent._pm1 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1551;BA.debugLine="pm2 = zeroRange";
mostCurrent._pm2 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1552;BA.debugLine="pm3 = zeroRange";
mostCurrent._pm3 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1553;BA.debugLine="pm4 = zeroRange";
mostCurrent._pm4 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1554;BA.debugLine="pm5 = zeroRange";
mostCurrent._pm5 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1555;BA.debugLine="pm6 = zeroRange";
mostCurrent._pm6 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1556;BA.debugLine="pm7 = zeroRange";
mostCurrent._pm7 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1557;BA.debugLine="pm8 = zeroRange";
mostCurrent._pm8 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1558;BA.debugLine="pm9 = zeroRange";
mostCurrent._pm9 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1559;BA.debugLine="pm10 = zeroRange";
mostCurrent._pm10 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1560;BA.debugLine="pm11 = zeroRange";
mostCurrent._pm11 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1562;BA.debugLine="Now = DateTime.Now";
_now = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 1563;BA.debugLine="Month = DateTime.GetMonth(Now)";
_month = anywheresoftware.b4a.keywords.Common.DateTime.GetMonth(_now);
 //BA.debugLineNum = 1564;BA.debugLine="Day = DateTime.GetDayOfMonth (Now)";
_day = anywheresoftware.b4a.keywords.Common.DateTime.GetDayOfMonth(_now);
 //BA.debugLineNum = 1565;BA.debugLine="Year = DateTime.GetYear(Now)";
_year = anywheresoftware.b4a.keywords.Common.DateTime.GetYear(_now);
 //BA.debugLineNum = 1567;BA.debugLine="If fileDay = \"Today\" Then";
if ((_fileday).equals("Today")) { 
 //BA.debugLineNum = 1568;BA.debugLine="FileName = \"LivingRoomTempHumid_\" & Year & \"-\"";
_filename = "LivingRoomTempHumid_"+BA.NumberToString(_year)+"-"+anywheresoftware.b4a.keywords.Common.NumberFormat(_month,(int) (2),(int) (0))+"-"+anywheresoftware.b4a.keywords.Common.NumberFormat(_day,(int) (2),(int) (0))+".log";
 }else {
 //BA.debugLineNum = 1570;BA.debugLine="Now = DateTime.add(DateTime.Now, 0, 0, -1)";
_now = anywheresoftware.b4a.keywords.Common.DateTime.Add(anywheresoftware.b4a.keywords.Common.DateTime.getNow(),(int) (0),(int) (0),(int) (-1));
 //BA.debugLineNum = 1571;BA.debugLine="Month = DateTime.GetMonth(Now)";
_month = anywheresoftware.b4a.keywords.Common.DateTime.GetMonth(_now);
 //BA.debugLineNum = 1572;BA.debugLine="Day = DateTime.GetDayOfMonth (Now)";
_day = anywheresoftware.b4a.keywords.Common.DateTime.GetDayOfMonth(_now);
 //BA.debugLineNum = 1573;BA.debugLine="Year = DateTime.GetYear(Now)";
_year = anywheresoftware.b4a.keywords.Common.DateTime.GetYear(_now);
 //BA.debugLineNum = 1574;BA.debugLine="FileName = \"LivingRoomTempHumid_\" & Year & \"-\"";
_filename = "LivingRoomTempHumid_"+BA.NumberToString(_year)+"-"+anywheresoftware.b4a.keywords.Common.NumberFormat(_month,(int) (2),(int) (0))+"-"+anywheresoftware.b4a.keywords.Common.NumberFormat(_day,(int) (2),(int) (0))+".log";
 };
 //BA.debugLineNum = 1577;BA.debugLine="TextReader1.Initialize(File.OpenInput(File.DirRo";
_textreader1.Initialize((java.io.InputStream)(anywheresoftware.b4a.keywords.Common.File.OpenInput(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),_filename).getObject()));
 //BA.debugLineNum = 1578;BA.debugLine="Dim line As String";
_line = "";
 //BA.debugLineNum = 1579;BA.debugLine="line = TextReader1.ReadLine";
_line = _textreader1.ReadLine();
 //BA.debugLineNum = 1580;BA.debugLine="Do While line <> Null";
while (_line!= null) {
 //BA.debugLineNum = 1582;BA.debugLine="line = TextReader1.ReadLine";
_line = _textreader1.ReadLine();
 //BA.debugLineNum = 1583;BA.debugLine="If line = Null Then";
if (_line== null) { 
 //BA.debugLineNum = 1584;BA.debugLine="Exit";
if (true) break;
 };
 //BA.debugLineNum = 1586;BA.debugLine="Dim a() As String = Regex.Split(\"\\|\",line)";
_a = anywheresoftware.b4a.keywords.Common.Regex.Split("\\|",_line);
 //BA.debugLineNum = 1587;BA.debugLine="If a.Length = 9 Then";
if (_a.length==9) { 
 //BA.debugLineNum = 1588;BA.debugLine="Dim timeStamp As String";
_timestamp = "";
 //BA.debugLineNum = 1589;BA.debugLine="timeStamp = a(0).SubString2(0,2)";
_timestamp = _a[(int) (0)].substring((int) (0),(int) (2));
 //BA.debugLineNum = 1591;BA.debugLine="If IsNumber(a(1)) = False Then Continue";
if (anywheresoftware.b4a.keywords.Common.IsNumber(_a[(int) (1)])==anywheresoftware.b4a.keywords.Common.False) { 
if (true) continue;};
 //BA.debugLineNum = 1593;BA.debugLine="Select timeStamp";
switch (BA.switchObjectToInt(_timestamp,"00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23")) {
case 0: {
 //BA.debugLineNum = 1595;BA.debugLine="If am12 = zeroRange Or am12 = \"\" Then am12 =";
if ((mostCurrent._am12).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am12).equals("")) { 
mostCurrent._am12 = _a[(int) (1)];};
 break; }
case 1: {
 //BA.debugLineNum = 1597;BA.debugLine="If am1 = zeroRange Or am1 = \"\" Then am1 = a(";
if ((mostCurrent._am1).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am1).equals("")) { 
mostCurrent._am1 = _a[(int) (1)];};
 break; }
case 2: {
 //BA.debugLineNum = 1599;BA.debugLine="If am2 = zeroRange Or am2 = \"\" Then am2 = a(";
if ((mostCurrent._am2).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am2).equals("")) { 
mostCurrent._am2 = _a[(int) (1)];};
 break; }
case 3: {
 //BA.debugLineNum = 1601;BA.debugLine="If am3 = zeroRange Or am3 = \"\" Then am3 = a(";
if ((mostCurrent._am3).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am3).equals("")) { 
mostCurrent._am3 = _a[(int) (1)];};
 break; }
case 4: {
 //BA.debugLineNum = 1603;BA.debugLine="If am4 = zeroRange Or am4 = \"\" Then am4 = a(";
if ((mostCurrent._am4).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am4).equals("")) { 
mostCurrent._am4 = _a[(int) (1)];};
 break; }
case 5: {
 //BA.debugLineNum = 1605;BA.debugLine="If am5 = zeroRange Or am5 = \"\" Then am5 = a(";
if ((mostCurrent._am5).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am5).equals("")) { 
mostCurrent._am5 = _a[(int) (1)];};
 break; }
case 6: {
 //BA.debugLineNum = 1607;BA.debugLine="If am6 = zeroRange Or am6 = \"\" Then am6 = a(";
if ((mostCurrent._am6).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am6).equals("")) { 
mostCurrent._am6 = _a[(int) (1)];};
 break; }
case 7: {
 //BA.debugLineNum = 1609;BA.debugLine="If am7 = zeroRange Or am7 = \"\" Then am7 = a(";
if ((mostCurrent._am7).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am7).equals("")) { 
mostCurrent._am7 = _a[(int) (1)];};
 break; }
case 8: {
 //BA.debugLineNum = 1611;BA.debugLine="If am8 = zeroRange Or am8 = \"\" Then am8 = a(";
if ((mostCurrent._am8).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am8).equals("")) { 
mostCurrent._am8 = _a[(int) (1)];};
 break; }
case 9: {
 //BA.debugLineNum = 1613;BA.debugLine="If am9 = zeroRange Or am9 = \"\" Then am9 = a(";
if ((mostCurrent._am9).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am9).equals("")) { 
mostCurrent._am9 = _a[(int) (1)];};
 break; }
case 10: {
 //BA.debugLineNum = 1615;BA.debugLine="If am10 = zeroRange Or am10 = \"\" Then am10 =";
if ((mostCurrent._am10).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am10).equals("")) { 
mostCurrent._am10 = _a[(int) (1)];};
 break; }
case 11: {
 //BA.debugLineNum = 1617;BA.debugLine="If am11 = zeroRange Or am11 = \"\" Then am11 =";
if ((mostCurrent._am11).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am11).equals("")) { 
mostCurrent._am11 = _a[(int) (1)];};
 break; }
case 12: {
 //BA.debugLineNum = 1619;BA.debugLine="If pm12 = zeroRange Or pm12 = \"\" Then pm12 =";
if ((mostCurrent._pm12).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm12).equals("")) { 
mostCurrent._pm12 = _a[(int) (1)];};
 break; }
case 13: {
 //BA.debugLineNum = 1621;BA.debugLine="If pm1 = zeroRange Or pm1 = \"\" Then pm1 = a(";
if ((mostCurrent._pm1).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm1).equals("")) { 
mostCurrent._pm1 = _a[(int) (1)];};
 break; }
case 14: {
 //BA.debugLineNum = 1623;BA.debugLine="If pm2 = zeroRange Or pm2 = \"\" Then pm2 = a(";
if ((mostCurrent._pm2).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm2).equals("")) { 
mostCurrent._pm2 = _a[(int) (1)];};
 break; }
case 15: {
 //BA.debugLineNum = 1625;BA.debugLine="If pm3 = zeroRange Or pm3 = \"\" Then pm3 = a(";
if ((mostCurrent._pm3).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm3).equals("")) { 
mostCurrent._pm3 = _a[(int) (1)];};
 break; }
case 16: {
 //BA.debugLineNum = 1627;BA.debugLine="If pm4 = zeroRange Or pm4 = \"\" Then pm4 = a(";
if ((mostCurrent._pm4).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm4).equals("")) { 
mostCurrent._pm4 = _a[(int) (1)];};
 break; }
case 17: {
 //BA.debugLineNum = 1629;BA.debugLine="If pm5 = zeroRange Or pm5 = \"\" Then pm5 = a(";
if ((mostCurrent._pm5).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm5).equals("")) { 
mostCurrent._pm5 = _a[(int) (1)];};
 break; }
case 18: {
 //BA.debugLineNum = 1631;BA.debugLine="If pm6 = zeroRange Or pm6 = \"\" Then pm6 = a(";
if ((mostCurrent._pm6).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm6).equals("")) { 
mostCurrent._pm6 = _a[(int) (1)];};
 break; }
case 19: {
 //BA.debugLineNum = 1633;BA.debugLine="If pm7 = zeroRange Or pm7 = \"\" Then pm7 = a(";
if ((mostCurrent._pm7).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm7).equals("")) { 
mostCurrent._pm7 = _a[(int) (1)];};
 break; }
case 20: {
 //BA.debugLineNum = 1635;BA.debugLine="If pm8 = zeroRange Or pm8 = \"\" Then pm8 = a(";
if ((mostCurrent._pm8).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm8).equals("")) { 
mostCurrent._pm8 = _a[(int) (1)];};
 break; }
case 21: {
 //BA.debugLineNum = 1637;BA.debugLine="If pm9 = zeroRange Or pm9 = \"\" Then pm9 = a(";
if ((mostCurrent._pm9).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm9).equals("")) { 
mostCurrent._pm9 = _a[(int) (1)];};
 break; }
case 22: {
 //BA.debugLineNum = 1639;BA.debugLine="If pm10 = zeroRange Or pm10 = \"\" Then pm10 =";
if ((mostCurrent._pm10).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm10).equals("")) { 
mostCurrent._pm10 = _a[(int) (1)];};
 break; }
case 23: {
 //BA.debugLineNum = 1641;BA.debugLine="If pm11 = zeroRange Or pm11 = \"\" Then pm11 =";
if ((mostCurrent._pm11).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm11).equals("")) { 
mostCurrent._pm11 = _a[(int) (1)];};
 break; }
}
;
 //BA.debugLineNum = 1643;BA.debugLine="If fileDay = \"Today\" Then";
if ((_fileday).equals("Today")) { 
 //BA.debugLineNum = 1644;BA.debugLine="tempRightNow = a(1)";
mostCurrent._temprightnow = _a[(int) (1)];
 };
 };
 }
;
 //BA.debugLineNum = 1649;BA.debugLine="TextReader1.Close";
_textreader1.Close();
 } 
       catch (Exception e115) {
			processBA.setLastException(e115); //BA.debugLineNum = 1651;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("6917627",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 };
 //BA.debugLineNum = 1653;BA.debugLine="End Sub";
return "";
}
public static String  _readtemperaturehourly(String _fileday) throws Exception{
anywheresoftware.b4a.objects.streams.File.TextReaderWrapper _textreader1 = null;
long _now = 0L;
int _month = 0;
int _day = 0;
int _year = 0;
String _filename = "";
String _line = "";
String[] _a = null;
String _timestamp = "";
 //BA.debugLineNum = 1782;BA.debugLine="Sub ReadTemperatureHourly(fileDay As String)";
 //BA.debugLineNum = 1783;BA.debugLine="Try";
try { //BA.debugLineNum = 1784;BA.debugLine="Dim TextReader1 As TextReader";
_textreader1 = new anywheresoftware.b4a.objects.streams.File.TextReaderWrapper();
 //BA.debugLineNum = 1785;BA.debugLine="Dim Now As Long";
_now = 0L;
 //BA.debugLineNum = 1786;BA.debugLine="Dim Month As Int";
_month = 0;
 //BA.debugLineNum = 1787;BA.debugLine="Dim Day As Int";
_day = 0;
 //BA.debugLineNum = 1788;BA.debugLine="Dim Year As Int";
_year = 0;
 //BA.debugLineNum = 1789;BA.debugLine="Dim FileName As String";
_filename = "";
 //BA.debugLineNum = 1791;BA.debugLine="am12 = zeroRange";
mostCurrent._am12 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1792;BA.debugLine="am1 = zeroRange";
mostCurrent._am1 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1793;BA.debugLine="am2 = zeroRange";
mostCurrent._am2 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1794;BA.debugLine="am3 = zeroRange";
mostCurrent._am3 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1795;BA.debugLine="am4 = zeroRange";
mostCurrent._am4 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1796;BA.debugLine="am5 = zeroRange";
mostCurrent._am5 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1797;BA.debugLine="am6 = zeroRange";
mostCurrent._am6 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1798;BA.debugLine="am7 = zeroRange";
mostCurrent._am7 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1799;BA.debugLine="am8 = zeroRange";
mostCurrent._am8 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1800;BA.debugLine="am9 = zeroRange";
mostCurrent._am9 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1801;BA.debugLine="am10 = zeroRange";
mostCurrent._am10 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1802;BA.debugLine="am11 = zeroRange";
mostCurrent._am11 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1803;BA.debugLine="pm12 = zeroRange";
mostCurrent._pm12 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1804;BA.debugLine="pm1 = zeroRange";
mostCurrent._pm1 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1805;BA.debugLine="pm2 = zeroRange";
mostCurrent._pm2 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1806;BA.debugLine="pm3 = zeroRange";
mostCurrent._pm3 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1807;BA.debugLine="pm4 = zeroRange";
mostCurrent._pm4 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1808;BA.debugLine="pm5 = zeroRange";
mostCurrent._pm5 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1809;BA.debugLine="pm6 = zeroRange";
mostCurrent._pm6 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1810;BA.debugLine="pm7 = zeroRange";
mostCurrent._pm7 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1811;BA.debugLine="pm8 = zeroRange";
mostCurrent._pm8 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1812;BA.debugLine="pm9 = zeroRange";
mostCurrent._pm9 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1813;BA.debugLine="pm10 = zeroRange";
mostCurrent._pm10 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1814;BA.debugLine="pm11 = zeroRange";
mostCurrent._pm11 = BA.NumberToString(_zerorange);
 //BA.debugLineNum = 1816;BA.debugLine="Now = DateTime.Now";
_now = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 1817;BA.debugLine="Month = DateTime.GetMonth(Now)";
_month = anywheresoftware.b4a.keywords.Common.DateTime.GetMonth(_now);
 //BA.debugLineNum = 1818;BA.debugLine="Day = DateTime.GetDayOfMonth (Now)";
_day = anywheresoftware.b4a.keywords.Common.DateTime.GetDayOfMonth(_now);
 //BA.debugLineNum = 1819;BA.debugLine="Year = DateTime.GetYear(Now)";
_year = anywheresoftware.b4a.keywords.Common.DateTime.GetYear(_now);
 //BA.debugLineNum = 1821;BA.debugLine="If fileDay = \"Today\" Then";
if ((_fileday).equals("Today")) { 
 //BA.debugLineNum = 1822;BA.debugLine="FileName = \"LivingRoomTempHumid_\" & Year & \"-\"";
_filename = "LivingRoomTempHumid_"+BA.NumberToString(_year)+"-"+anywheresoftware.b4a.keywords.Common.NumberFormat(_month,(int) (2),(int) (0))+"-"+anywheresoftware.b4a.keywords.Common.NumberFormat(_day,(int) (2),(int) (0))+".log";
 }else {
 //BA.debugLineNum = 1824;BA.debugLine="Now = DateTime.add(DateTime.Now, 0, 0, -1)";
_now = anywheresoftware.b4a.keywords.Common.DateTime.Add(anywheresoftware.b4a.keywords.Common.DateTime.getNow(),(int) (0),(int) (0),(int) (-1));
 //BA.debugLineNum = 1825;BA.debugLine="Month = DateTime.GetMonth(Now)";
_month = anywheresoftware.b4a.keywords.Common.DateTime.GetMonth(_now);
 //BA.debugLineNum = 1826;BA.debugLine="Day = DateTime.GetDayOfMonth (Now)";
_day = anywheresoftware.b4a.keywords.Common.DateTime.GetDayOfMonth(_now);
 //BA.debugLineNum = 1827;BA.debugLine="Year = DateTime.GetYear(Now)";
_year = anywheresoftware.b4a.keywords.Common.DateTime.GetYear(_now);
 //BA.debugLineNum = 1828;BA.debugLine="FileName = \"LivingRoomTempHumid_\" & Year & \"-\"";
_filename = "LivingRoomTempHumid_"+BA.NumberToString(_year)+"-"+anywheresoftware.b4a.keywords.Common.NumberFormat(_month,(int) (2),(int) (0))+"-"+anywheresoftware.b4a.keywords.Common.NumberFormat(_day,(int) (2),(int) (0))+".log";
 };
 //BA.debugLineNum = 1831;BA.debugLine="TextReader1.Initialize(File.OpenInput(File.DirRo";
_textreader1.Initialize((java.io.InputStream)(anywheresoftware.b4a.keywords.Common.File.OpenInput(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),_filename).getObject()));
 //BA.debugLineNum = 1832;BA.debugLine="Dim line As String";
_line = "";
 //BA.debugLineNum = 1833;BA.debugLine="line = TextReader1.ReadLine";
_line = _textreader1.ReadLine();
 //BA.debugLineNum = 1834;BA.debugLine="Do While line <> Null";
while (_line!= null) {
 //BA.debugLineNum = 1836;BA.debugLine="line = TextReader1.ReadLine";
_line = _textreader1.ReadLine();
 //BA.debugLineNum = 1837;BA.debugLine="If line = Null Then";
if (_line== null) { 
 //BA.debugLineNum = 1838;BA.debugLine="Exit";
if (true) break;
 };
 //BA.debugLineNum = 1840;BA.debugLine="Dim a() As String = Regex.Split(\"\\|\",line)";
_a = anywheresoftware.b4a.keywords.Common.Regex.Split("\\|",_line);
 //BA.debugLineNum = 1841;BA.debugLine="If a.Length = 9 Then";
if (_a.length==9) { 
 //BA.debugLineNum = 1842;BA.debugLine="Dim timeStamp As String";
_timestamp = "";
 //BA.debugLineNum = 1843;BA.debugLine="timeStamp = a(0).SubString2(0,5)";
_timestamp = _a[(int) (0)].substring((int) (0),(int) (5));
 //BA.debugLineNum = 1845;BA.debugLine="If IsNumber(a(1)) = False Then Continue";
if (anywheresoftware.b4a.keywords.Common.IsNumber(_a[(int) (1)])==anywheresoftware.b4a.keywords.Common.False) { 
if (true) continue;};
 //BA.debugLineNum = 1847;BA.debugLine="Select timeStamp";
switch (BA.switchObjectToInt(_timestamp,mostCurrent._timearray[(int) (0)],mostCurrent._timearray[(int) (1)],mostCurrent._timearray[(int) (2)],mostCurrent._timearray[(int) (3)],mostCurrent._timearray[(int) (4)],mostCurrent._timearray[(int) (5)],mostCurrent._timearray[(int) (6)],mostCurrent._timearray[(int) (7)],mostCurrent._timearray[(int) (8)],mostCurrent._timearray[(int) (9)],mostCurrent._timearray[(int) (10)],mostCurrent._timearray[(int) (11)],mostCurrent._timearray[(int) (12)],mostCurrent._timearray[(int) (13)],mostCurrent._timearray[(int) (14)],mostCurrent._timearray[(int) (15)],mostCurrent._timearray[(int) (16)],mostCurrent._timearray[(int) (17)],mostCurrent._timearray[(int) (18)],mostCurrent._timearray[(int) (19)],mostCurrent._timearray[(int) (20)],mostCurrent._timearray[(int) (21)],mostCurrent._timearray[(int) (22)],mostCurrent._timearray[(int) (23)])) {
case 0: {
 //BA.debugLineNum = 1849;BA.debugLine="If am12 = zeroRange Or am12 = \"\" Then am12 =";
if ((mostCurrent._am12).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am12).equals("")) { 
mostCurrent._am12 = _a[(int) (1)];};
 break; }
case 1: {
 //BA.debugLineNum = 1851;BA.debugLine="If am1 = zeroRange Or am1 = \"\" Then am1 = a(";
if ((mostCurrent._am1).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am1).equals("")) { 
mostCurrent._am1 = _a[(int) (1)];};
 break; }
case 2: {
 //BA.debugLineNum = 1853;BA.debugLine="If am2 = zeroRange Or am2 = \"\" Then am2 = a(";
if ((mostCurrent._am2).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am2).equals("")) { 
mostCurrent._am2 = _a[(int) (1)];};
 break; }
case 3: {
 //BA.debugLineNum = 1855;BA.debugLine="If am3 = zeroRange Or am3 = \"\" Then am3 = a(";
if ((mostCurrent._am3).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am3).equals("")) { 
mostCurrent._am3 = _a[(int) (1)];};
 break; }
case 4: {
 //BA.debugLineNum = 1857;BA.debugLine="If am4 = zeroRange Or am4 = \"\" Then am4 = a(";
if ((mostCurrent._am4).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am4).equals("")) { 
mostCurrent._am4 = _a[(int) (1)];};
 break; }
case 5: {
 //BA.debugLineNum = 1859;BA.debugLine="If am5 = zeroRange Or am5 = \"\" Then am5 = a(";
if ((mostCurrent._am5).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am5).equals("")) { 
mostCurrent._am5 = _a[(int) (1)];};
 break; }
case 6: {
 //BA.debugLineNum = 1861;BA.debugLine="If am6 = zeroRange Or am6 = \"\" Then am6 = a(";
if ((mostCurrent._am6).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am6).equals("")) { 
mostCurrent._am6 = _a[(int) (1)];};
 break; }
case 7: {
 //BA.debugLineNum = 1863;BA.debugLine="If am7 = zeroRange Or am7 = \"\" Then am7 = a(";
if ((mostCurrent._am7).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am7).equals("")) { 
mostCurrent._am7 = _a[(int) (1)];};
 break; }
case 8: {
 //BA.debugLineNum = 1865;BA.debugLine="If am8 = zeroRange Or am8 = \"\" Then am8 = a(";
if ((mostCurrent._am8).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am8).equals("")) { 
mostCurrent._am8 = _a[(int) (1)];};
 break; }
case 9: {
 //BA.debugLineNum = 1867;BA.debugLine="If am9 = zeroRange Or am9 = \"\" Then am9 = a(";
if ((mostCurrent._am9).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am9).equals("")) { 
mostCurrent._am9 = _a[(int) (1)];};
 break; }
case 10: {
 //BA.debugLineNum = 1869;BA.debugLine="If am10 = zeroRange Or am10 = \"\" Then am10 =";
if ((mostCurrent._am10).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am10).equals("")) { 
mostCurrent._am10 = _a[(int) (1)];};
 break; }
case 11: {
 //BA.debugLineNum = 1871;BA.debugLine="If am11 = zeroRange Or am11 = \"\" Then am11 =";
if ((mostCurrent._am11).equals(BA.NumberToString(_zerorange)) || (mostCurrent._am11).equals("")) { 
mostCurrent._am11 = _a[(int) (1)];};
 break; }
case 12: {
 //BA.debugLineNum = 1873;BA.debugLine="If pm12 = zeroRange Or pm12 = \"\" Then pm12 =";
if ((mostCurrent._pm12).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm12).equals("")) { 
mostCurrent._pm12 = _a[(int) (1)];};
 break; }
case 13: {
 //BA.debugLineNum = 1875;BA.debugLine="If pm1 = zeroRange Or pm1 = \"\" Then pm1 = a(";
if ((mostCurrent._pm1).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm1).equals("")) { 
mostCurrent._pm1 = _a[(int) (1)];};
 break; }
case 14: {
 //BA.debugLineNum = 1877;BA.debugLine="If pm2 = zeroRange Or pm2 = \"\" Then pm2 = a(";
if ((mostCurrent._pm2).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm2).equals("")) { 
mostCurrent._pm2 = _a[(int) (1)];};
 break; }
case 15: {
 //BA.debugLineNum = 1879;BA.debugLine="If pm3 = zeroRange Or pm3 = \"\" Then pm3 = a(";
if ((mostCurrent._pm3).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm3).equals("")) { 
mostCurrent._pm3 = _a[(int) (1)];};
 break; }
case 16: {
 //BA.debugLineNum = 1881;BA.debugLine="If pm4 = zeroRange Or pm4 = \"\" Then pm4 = a(";
if ((mostCurrent._pm4).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm4).equals("")) { 
mostCurrent._pm4 = _a[(int) (1)];};
 break; }
case 17: {
 //BA.debugLineNum = 1883;BA.debugLine="If pm5 = zeroRange Or pm5 = \"\" Then pm5 = a(";
if ((mostCurrent._pm5).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm5).equals("")) { 
mostCurrent._pm5 = _a[(int) (1)];};
 break; }
case 18: {
 //BA.debugLineNum = 1885;BA.debugLine="If pm6 = zeroRange Or pm6 = \"\" Then pm6 = a(";
if ((mostCurrent._pm6).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm6).equals("")) { 
mostCurrent._pm6 = _a[(int) (1)];};
 break; }
case 19: {
 //BA.debugLineNum = 1887;BA.debugLine="If pm7 = zeroRange Or pm7 = \"\" Then pm7 = a(";
if ((mostCurrent._pm7).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm7).equals("")) { 
mostCurrent._pm7 = _a[(int) (1)];};
 break; }
case 20: {
 //BA.debugLineNum = 1889;BA.debugLine="If pm8 = zeroRange Or pm8 = \"\" Then pm8 = a(";
if ((mostCurrent._pm8).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm8).equals("")) { 
mostCurrent._pm8 = _a[(int) (1)];};
 break; }
case 21: {
 //BA.debugLineNum = 1891;BA.debugLine="If pm9 = zeroRange Or pm9 = \"\" Then pm9 = a(";
if ((mostCurrent._pm9).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm9).equals("")) { 
mostCurrent._pm9 = _a[(int) (1)];};
 break; }
case 22: {
 //BA.debugLineNum = 1893;BA.debugLine="If pm10 = zeroRange Or pm10 = \"\" Then pm10 =";
if ((mostCurrent._pm10).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm10).equals("")) { 
mostCurrent._pm10 = _a[(int) (1)];};
 break; }
case 23: {
 //BA.debugLineNum = 1895;BA.debugLine="If pm11 = zeroRange Or pm11 = \"\" Then pm11 =";
if ((mostCurrent._pm11).equals(BA.NumberToString(_zerorange)) || (mostCurrent._pm11).equals("")) { 
mostCurrent._pm11 = _a[(int) (1)];};
 break; }
}
;
 //BA.debugLineNum = 1897;BA.debugLine="If fileDay = \"Today\" Then";
if ((_fileday).equals("Today")) { 
 //BA.debugLineNum = 1898;BA.debugLine="tempRightNow = a(1)";
mostCurrent._temprightnow = _a[(int) (1)];
 //BA.debugLineNum = 1899;BA.debugLine="DateTime.TimeFormat = \"h:mm a\"";
anywheresoftware.b4a.keywords.Common.DateTime.setTimeFormat("h:mm a");
 };
 };
 }
;
 //BA.debugLineNum = 1904;BA.debugLine="TextReader1.Close";
_textreader1.Close();
 } 
       catch (Exception e116) {
			processBA.setLastException(e116); //BA.debugLineNum = 1908;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("61048702",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 };
 //BA.debugLineNum = 1910;BA.debugLine="End Sub";
return "";
}
public static String  _temperaturedailycreate() throws Exception{
anywheresoftware.b4a.keywords.LayoutValues _lv = null;
anywheresoftware.b4j.object.JavaObject _jo = null;
long _yesterday = 0L;
 //BA.debugLineNum = 674;BA.debugLine="Private Sub TemperatureDailyCreate()";
 //BA.debugLineNum = 675;BA.debugLine="Try";
try { //BA.debugLineNum = 677;BA.debugLine="Activity_WindowFocusChanged(True)";
_activity_windowfocuschanged(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 678;BA.debugLine="Dim lv As LayoutValues = GetRealSize";
_lv = _getrealsize();
 //BA.debugLineNum = 679;BA.debugLine="Dim jo As JavaObject = Activity";
_jo = new anywheresoftware.b4j.object.JavaObject();
_jo = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(mostCurrent._activity.getObject()));
 //BA.debugLineNum = 680;BA.debugLine="jo.RunMethod(\"setBottom\", Array(lv.Height))";
_jo.RunMethod("setBottom",new Object[]{(Object)(_lv.Height)});
 //BA.debugLineNum = 681;BA.debugLine="jo.RunMethod(\"setRight\", Array(lv.Width))";
_jo.RunMethod("setRight",new Object[]{(Object)(_lv.Width)});
 //BA.debugLineNum = 682;BA.debugLine="Activity.Height = lv.Height";
mostCurrent._activity.setHeight(_lv.Height);
 //BA.debugLineNum = 683;BA.debugLine="Activity.Width = lv.Width";
mostCurrent._activity.setWidth(_lv.Width);
 //BA.debugLineNum = 686;BA.debugLine="Activity.LoadLayout(\"Main\")";
mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 //BA.debugLineNum = 688;BA.debugLine="LineChart.GraphBackgroundColor = Colors.DarkGray";
mostCurrent._linechart.setGraphBackgroundColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 689;BA.debugLine="LineChart.GraphFrameColor = Colors.Blue";
mostCurrent._linechart.setGraphFrameColor(anywheresoftware.b4a.keywords.Common.Colors.Blue);
 //BA.debugLineNum = 690;BA.debugLine="LineChart.GraphFrameWidth = 4.0";
mostCurrent._linechart.setGraphFrameWidth((float) (4.0));
 //BA.debugLineNum = 691;BA.debugLine="LineChart.GraphPlotAreaBackgroundColor = Colors.";
mostCurrent._linechart.setGraphPlotAreaBackgroundColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (50),(int) (0),(int) (0),(int) (255)));
 //BA.debugLineNum = 692;BA.debugLine="LineChart.GraphTitleTextSize = 15";
mostCurrent._linechart.setGraphTitleTextSize((int) (15));
 //BA.debugLineNum = 693;BA.debugLine="LineChart.GraphTitleColor = Colors.White";
mostCurrent._linechart.setGraphTitleColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 694;BA.debugLine="LineChart.GraphTitleSkewX = -0.25";
mostCurrent._linechart.setGraphTitleSkewX((float) (-0.25));
 //BA.debugLineNum = 695;BA.debugLine="LineChart.GraphTitleUnderline = True";
mostCurrent._linechart.setGraphTitleUnderline(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 696;BA.debugLine="LineChart.GraphTitleBold = True";
mostCurrent._linechart.setGraphTitleBold(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 697;BA.debugLine="LineChart.GraphTitle = \"TEMPERATURE DAILY  \"";
mostCurrent._linechart.setGraphTitle("TEMPERATURE DAILY  ");
 //BA.debugLineNum = 699;BA.debugLine="LineChart.LegendBackgroundColor = Colors.White";
mostCurrent._linechart.setLegendBackgroundColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 700;BA.debugLine="LineChart.LegendTextColor = Colors.Black";
mostCurrent._linechart.setLegendTextColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 //BA.debugLineNum = 701;BA.debugLine="LineChart.LegendTextSize = 18.0";
mostCurrent._linechart.setLegendTextSize((float) (18.0));
 //BA.debugLineNum = 703;BA.debugLine="DateTime.TimeFormat = \"h:mm a\"";
anywheresoftware.b4a.keywords.Common.DateTime.setTimeFormat("h:mm a");
 //BA.debugLineNum = 704;BA.debugLine="LineChart.DomianLabel = \"The time now is: \" & Da";
mostCurrent._linechart.setDomianLabel("The time now is: "+anywheresoftware.b4a.keywords.Common.DateTime.Time(anywheresoftware.b4a.keywords.Common.DateTime.getNow()));
 //BA.debugLineNum = 705;BA.debugLine="LineChart.DomainLabelColor = Colors.Green";
mostCurrent._linechart.setDomainLabelColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 706;BA.debugLine="LineChart.DomainLabelTextSize = 25.0";
mostCurrent._linechart.setDomainLabelTextSize((float) (25.0));
 //BA.debugLineNum = 708;BA.debugLine="LineChart.XaxisGridLineColor = Colors.ARGB(100,2";
mostCurrent._linechart.setXaxisGridLineColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (100),(int) (255),(int) (255),(int) (255)));
 //BA.debugLineNum = 709;BA.debugLine="LineChart.XaxisGridLineWidth = 2.0";
mostCurrent._linechart.setXaxisGridLineWidth((float) (2.0));
 //BA.debugLineNum = 710;BA.debugLine="LineChart.XaxisLabelTicks = 1";
mostCurrent._linechart.setXaxisLabelTicks((int) (1));
 //BA.debugLineNum = 711;BA.debugLine="LineChart.XaxisLabelOrientation = 0";
mostCurrent._linechart.setXaxisLabelOrientation((float) (0));
 //BA.debugLineNum = 712;BA.debugLine="LineChart.XaxisLabelTextColor = Colors.White";
mostCurrent._linechart.setXaxisLabelTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 713;BA.debugLine="LineChart.XaxisLabelTextSize = 32.0";
mostCurrent._linechart.setXaxisLabelTextSize((float) (32.0));
 //BA.debugLineNum = 714;BA.debugLine="LineChart.XAxisLabels = Array As String(\"12 am\",";
mostCurrent._linechart.setXAxisLabels(new String[]{"12 am","1 am","2 am","3 am","4 am","5 am","6 am","7 am","8 am","9 am","10 am","11 am","12 pm","1 pm","2 pm","3 pm","4 pm","5 pm","6 pm","7 pm","8 pm","9 pm","10 pm","11 pm"});
 //BA.debugLineNum = 716;BA.debugLine="LineChart.YaxisDivisions = 10";
mostCurrent._linechart.setYaxisDivisions((int) (10));
 //BA.debugLineNum = 718;BA.debugLine="LineChart.YaxisValueFormat = LineChart.ValueForm";
mostCurrent._linechart.setYaxisValueFormat(mostCurrent._linechart.ValueFormat_2);
 //BA.debugLineNum = 719;BA.debugLine="LineChart.YaxisGridLineColor = Colors.Black";
mostCurrent._linechart.setYaxisGridLineColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 //BA.debugLineNum = 720;BA.debugLine="LineChart.YaxisGridLineWidth = 2";
mostCurrent._linechart.setYaxisGridLineWidth((float) (2));
 //BA.debugLineNum = 721;BA.debugLine="LineChart.YaxisLabelTicks = 1";
mostCurrent._linechart.setYaxisLabelTicks((int) (1));
 //BA.debugLineNum = 722;BA.debugLine="LineChart.YaxisLabelColor = Colors.Yellow";
mostCurrent._linechart.setYaxisLabelColor(anywheresoftware.b4a.keywords.Common.Colors.Yellow);
 //BA.debugLineNum = 723;BA.debugLine="LineChart.YaxisLabelOrientation = -30";
mostCurrent._linechart.setYaxisLabelOrientation((float) (-30));
 //BA.debugLineNum = 724;BA.debugLine="LineChart.YaxisLabelTextSize = 25.0";
mostCurrent._linechart.setYaxisLabelTextSize((float) (25.0));
 //BA.debugLineNum = 725;BA.debugLine="LineChart.YaxisTitleColor = Colors.Green";
mostCurrent._linechart.setYaxisTitleColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 726;BA.debugLine="LineChart.YaxisTitleFakeBold = False";
mostCurrent._linechart.setYaxisTitleFakeBold(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 727;BA.debugLine="LineChart.YaxisTitleTextSize = 20.0";
mostCurrent._linechart.setYaxisTitleTextSize((float) (20.0));
 //BA.debugLineNum = 728;BA.debugLine="LineChart.YaxisTitleUnderline = True";
mostCurrent._linechart.setYaxisTitleUnderline(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 729;BA.debugLine="LineChart.YaxisTitleTextSkewness = 0";
mostCurrent._linechart.setYaxisTitleTextSkewness((float) (0));
 //BA.debugLineNum = 730;BA.debugLine="LineChart.YaxisLabelAndTitleDistance = 60.0";
mostCurrent._linechart.setYaxisLabelAndTitleDistance((float) (60.0));
 //BA.debugLineNum = 731;BA.debugLine="LineChart.YaxisTitle = \"Temperature (Fahrenheit)";
mostCurrent._linechart.setYaxisTitle("Temperature (Fahrenheit)");
 //BA.debugLineNum = 733;BA.debugLine="LineChart.MaxNumberOfEntriesPerLineChart = 24";
mostCurrent._linechart.setMaxNumberOfEntriesPerLineChart((int) (24));
 //BA.debugLineNum = 734;BA.debugLine="LineChart.GraphLegendVisibility = False";
mostCurrent._linechart.setGraphLegendVisibility(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 738;BA.debugLine="ReadTemperatureDaily(\"Today\")";
_readtemperaturedaily("Today");
 //BA.debugLineNum = 740;BA.debugLine="DateTime.DateFormat = \"MMM d, yyyy\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("MMM d, yyyy");
 //BA.debugLineNum = 741;BA.debugLine="LineChart.Line_1_LegendText = \"Today, \" & DateTi";
mostCurrent._linechart.setLine_1_LegendText("Today, "+anywheresoftware.b4a.keywords.Common.DateTime.Date(anywheresoftware.b4a.keywords.Common.DateTime.getNow()));
 //BA.debugLineNum = 743;BA.debugLine="CheckTempBoundariesDaily";
_checktempboundariesdaily();
 //BA.debugLineNum = 745;BA.debugLine="If am12 <> tempZeroRange Then";
if ((mostCurrent._am12).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 746;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12)";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12))});
 };
 //BA.debugLineNum = 748;BA.debugLine="If am1 <> tempZeroRange Then";
if ((mostCurrent._am1).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 749;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1))});
 };
 //BA.debugLineNum = 751;BA.debugLine="If am2 <> tempZeroRange Then";
if ((mostCurrent._am2).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 752;BA.debugLine="If am1 = tempZeroRange Then";
if ((mostCurrent._am1).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 753;BA.debugLine="am1 = (am12 + am2)/2";
mostCurrent._am1 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am12))+(double)(Double.parseDouble(mostCurrent._am2)))/(double)2);
 };
 //BA.debugLineNum = 755;BA.debugLine="If am12 = tempZeroRange Then";
if ((mostCurrent._am12).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 756;BA.debugLine="am12 = am1";
mostCurrent._am12 = mostCurrent._am1;
 };
 //BA.debugLineNum = 758;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2))});
 };
 //BA.debugLineNum = 760;BA.debugLine="If am3 <> tempZeroRange Then";
if ((mostCurrent._am3).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 761;BA.debugLine="If am2 = tempZeroRange Then";
if ((mostCurrent._am2).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 762;BA.debugLine="am2 = (am1 + am3)/2";
mostCurrent._am2 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am1))+(double)(Double.parseDouble(mostCurrent._am3)))/(double)2);
 };
 //BA.debugLineNum = 764;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3))});
 };
 //BA.debugLineNum = 766;BA.debugLine="If am4 <> tempZeroRange Then";
if ((mostCurrent._am4).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 767;BA.debugLine="If am3 = tempZeroRange Then";
if ((mostCurrent._am3).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 768;BA.debugLine="am3 = (am2 + am4)/2";
mostCurrent._am3 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am2))+(double)(Double.parseDouble(mostCurrent._am4)))/(double)2);
 };
 //BA.debugLineNum = 770;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4))});
 };
 //BA.debugLineNum = 772;BA.debugLine="If am5 <> tempZeroRange Then";
if ((mostCurrent._am5).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 773;BA.debugLine="If am4 = tempZeroRange Then";
if ((mostCurrent._am4).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 774;BA.debugLine="am4 = (am3 + am5)/2";
mostCurrent._am4 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am3))+(double)(Double.parseDouble(mostCurrent._am5)))/(double)2);
 };
 //BA.debugLineNum = 776;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5))});
 };
 //BA.debugLineNum = 778;BA.debugLine="If am6 <> tempZeroRange Then";
if ((mostCurrent._am6).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 779;BA.debugLine="If am5 = tempZeroRange Then";
if ((mostCurrent._am5).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 780;BA.debugLine="am5 = (am4 + am6)/2";
mostCurrent._am5 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am4))+(double)(Double.parseDouble(mostCurrent._am6)))/(double)2);
 };
 //BA.debugLineNum = 782;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6))});
 };
 //BA.debugLineNum = 784;BA.debugLine="If am7 <> tempZeroRange Then";
if ((mostCurrent._am7).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 785;BA.debugLine="If am6 = tempZeroRange Then";
if ((mostCurrent._am6).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 786;BA.debugLine="am6 = (am5 + am7)/2";
mostCurrent._am6 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am5))+(double)(Double.parseDouble(mostCurrent._am7)))/(double)2);
 };
 //BA.debugLineNum = 788;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7))});
 };
 //BA.debugLineNum = 790;BA.debugLine="If am8 <> tempZeroRange Then";
if ((mostCurrent._am8).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 791;BA.debugLine="If am7 = tempZeroRange Then";
if ((mostCurrent._am7).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 792;BA.debugLine="am7 = (am6 + am8)/2";
mostCurrent._am7 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am6))+(double)(Double.parseDouble(mostCurrent._am8)))/(double)2);
 };
 //BA.debugLineNum = 794;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8))});
 };
 //BA.debugLineNum = 796;BA.debugLine="If am9 <> tempZeroRange Then";
if ((mostCurrent._am9).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 797;BA.debugLine="If am8 = tempZeroRange Then";
if ((mostCurrent._am8).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 798;BA.debugLine="am8 = (am7 + am9)/2";
mostCurrent._am8 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am7))+(double)(Double.parseDouble(mostCurrent._am9)))/(double)2);
 };
 //BA.debugLineNum = 800;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9))});
 };
 //BA.debugLineNum = 802;BA.debugLine="If am10 <> tempZeroRange Then";
if ((mostCurrent._am10).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 803;BA.debugLine="If am9 = tempZeroRange Then";
if ((mostCurrent._am9).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 804;BA.debugLine="am9 = (am8 + am10)/2";
mostCurrent._am9 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am8))+(double)(Double.parseDouble(mostCurrent._am10)))/(double)2);
 };
 //BA.debugLineNum = 806;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10))});
 };
 //BA.debugLineNum = 808;BA.debugLine="If am11 <> tempZeroRange Then";
if ((mostCurrent._am11).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 809;BA.debugLine="If am10 = tempZeroRange Then";
if ((mostCurrent._am10).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 810;BA.debugLine="am10 = (am9 + am11)/2";
mostCurrent._am10 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am9))+(double)(Double.parseDouble(mostCurrent._am11)))/(double)2);
 };
 //BA.debugLineNum = 812;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11))});
 };
 //BA.debugLineNum = 814;BA.debugLine="If pm12 <> tempZeroRange Then";
if ((mostCurrent._pm12).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 815;BA.debugLine="If am11 = tempZeroRange Then";
if ((mostCurrent._am11).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 816;BA.debugLine="am11 = (am10 + pm12)/2";
mostCurrent._am11 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am10))+(double)(Double.parseDouble(mostCurrent._pm12)))/(double)2);
 };
 //BA.debugLineNum = 818;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12))});
 };
 //BA.debugLineNum = 820;BA.debugLine="If pm1 <> tempZeroRange Then";
if ((mostCurrent._pm1).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 821;BA.debugLine="If pm12 = tempZeroRange Then";
if ((mostCurrent._pm12).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 822;BA.debugLine="pm12 = (am11 + pm1)/2";
mostCurrent._pm12 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am11))+(double)(Double.parseDouble(mostCurrent._pm1)))/(double)2);
 };
 //BA.debugLineNum = 824;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1))});
 };
 //BA.debugLineNum = 826;BA.debugLine="If pm2 <> tempZeroRange Then";
if ((mostCurrent._pm2).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 827;BA.debugLine="If pm1 = tempZeroRange Then";
if ((mostCurrent._pm1).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 828;BA.debugLine="pm1 = (pm12 + pm2)/2";
mostCurrent._pm1 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm12))+(double)(Double.parseDouble(mostCurrent._pm2)))/(double)2);
 };
 //BA.debugLineNum = 830;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2))});
 };
 //BA.debugLineNum = 832;BA.debugLine="If pm3 <> tempZeroRange Then";
if ((mostCurrent._pm3).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 833;BA.debugLine="If pm2 = tempZeroRange Then";
if ((mostCurrent._pm2).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 834;BA.debugLine="pm2 = (pm1 + pm3)/2";
mostCurrent._pm2 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm1))+(double)(Double.parseDouble(mostCurrent._pm3)))/(double)2);
 };
 //BA.debugLineNum = 836;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3))});
 };
 //BA.debugLineNum = 838;BA.debugLine="If pm4 <> tempZeroRange Then";
if ((mostCurrent._pm4).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 839;BA.debugLine="If pm3 = tempZeroRange Then";
if ((mostCurrent._pm3).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 840;BA.debugLine="pm3 = (pm2 + pm4)/2";
mostCurrent._pm3 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm2))+(double)(Double.parseDouble(mostCurrent._pm4)))/(double)2);
 };
 //BA.debugLineNum = 842;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4))});
 };
 //BA.debugLineNum = 844;BA.debugLine="If pm5 <> tempZeroRange Then";
if ((mostCurrent._pm5).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 845;BA.debugLine="If pm4 = tempZeroRange Then";
if ((mostCurrent._pm4).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 846;BA.debugLine="pm4 = (pm3 + pm5)/2";
mostCurrent._pm4 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm3))+(double)(Double.parseDouble(mostCurrent._pm5)))/(double)2);
 };
 //BA.debugLineNum = 848;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5))});
 };
 //BA.debugLineNum = 850;BA.debugLine="If pm6 <> tempZeroRange Then";
if ((mostCurrent._pm6).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 851;BA.debugLine="If pm5 = tempZeroRange Then";
if ((mostCurrent._pm5).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 852;BA.debugLine="pm5 = (pm4 + pm6)/2";
mostCurrent._pm5 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm4))+(double)(Double.parseDouble(mostCurrent._pm6)))/(double)2);
 };
 //BA.debugLineNum = 854;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5)),(float)(Double.parseDouble(mostCurrent._pm6))});
 };
 //BA.debugLineNum = 856;BA.debugLine="If pm7 <> tempZeroRange Then";
if ((mostCurrent._pm7).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 857;BA.debugLine="If pm6 = tempZeroRange Then";
if ((mostCurrent._pm6).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 858;BA.debugLine="pm6 = (pm5 + pm7)/2";
mostCurrent._pm6 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm5))+(double)(Double.parseDouble(mostCurrent._pm7)))/(double)2);
 };
 //BA.debugLineNum = 860;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5)),(float)(Double.parseDouble(mostCurrent._pm6)),(float)(Double.parseDouble(mostCurrent._pm7))});
 };
 //BA.debugLineNum = 862;BA.debugLine="If pm8 <> tempZeroRange Then";
if ((mostCurrent._pm8).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 863;BA.debugLine="If pm7 = tempZeroRange Then";
if ((mostCurrent._pm7).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 864;BA.debugLine="pm7 = (pm6 + pm8)/2";
mostCurrent._pm7 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm6))+(double)(Double.parseDouble(mostCurrent._pm8)))/(double)2);
 };
 //BA.debugLineNum = 866;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5)),(float)(Double.parseDouble(mostCurrent._pm6)),(float)(Double.parseDouble(mostCurrent._pm7)),(float)(Double.parseDouble(mostCurrent._pm8))});
 };
 //BA.debugLineNum = 868;BA.debugLine="If pm9 <> tempZeroRange Then";
if ((mostCurrent._pm9).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 869;BA.debugLine="If pm8 = tempZeroRange Then";
if ((mostCurrent._pm8).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 870;BA.debugLine="pm8 = (pm7 + pm9)/2";
mostCurrent._pm8 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm7))+(double)(Double.parseDouble(mostCurrent._pm9)))/(double)2);
 };
 //BA.debugLineNum = 872;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5)),(float)(Double.parseDouble(mostCurrent._pm6)),(float)(Double.parseDouble(mostCurrent._pm7)),(float)(Double.parseDouble(mostCurrent._pm8)),(float)(Double.parseDouble(mostCurrent._pm9))});
 };
 //BA.debugLineNum = 874;BA.debugLine="If pm10 <> tempZeroRange Then";
if ((mostCurrent._pm10).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 875;BA.debugLine="If pm9 = tempZeroRange Then";
if ((mostCurrent._pm9).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 876;BA.debugLine="pm9 = (pm8 + pm10)/2";
mostCurrent._pm9 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm8))+(double)(Double.parseDouble(mostCurrent._pm10)))/(double)2);
 };
 //BA.debugLineNum = 878;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5)),(float)(Double.parseDouble(mostCurrent._pm6)),(float)(Double.parseDouble(mostCurrent._pm7)),(float)(Double.parseDouble(mostCurrent._pm8)),(float)(Double.parseDouble(mostCurrent._pm9)),(float)(Double.parseDouble(mostCurrent._pm10))});
 };
 //BA.debugLineNum = 880;BA.debugLine="If pm11 <> tempZeroRange Then";
if ((mostCurrent._pm11).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 881;BA.debugLine="If pm10 = tempZeroRange Then";
if ((mostCurrent._pm10).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 882;BA.debugLine="pm10 = (pm9 + pm11)/2";
mostCurrent._pm10 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm9))+(double)(Double.parseDouble(mostCurrent._pm11)))/(double)2);
 };
 //BA.debugLineNum = 884;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5)),(float)(Double.parseDouble(mostCurrent._pm6)),(float)(Double.parseDouble(mostCurrent._pm7)),(float)(Double.parseDouble(mostCurrent._pm8)),(float)(Double.parseDouble(mostCurrent._pm9)),(float)(Double.parseDouble(mostCurrent._pm10)),(float)(Double.parseDouble(mostCurrent._pm11))});
 };
 //BA.debugLineNum = 887;BA.debugLine="LineChart.Line_1_PointLabelTextColor = Colors.Ye";
mostCurrent._linechart.setLine_1_PointLabelTextColor(anywheresoftware.b4a.keywords.Common.Colors.Yellow);
 //BA.debugLineNum = 888;BA.debugLine="LineChart.Line_1_PointLabelTextSize = 35.0";
mostCurrent._linechart.setLine_1_PointLabelTextSize((float) (35.0));
 //BA.debugLineNum = 889;BA.debugLine="LineChart.Line_1_LineColor = Colors.Red";
mostCurrent._linechart.setLine_1_LineColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 890;BA.debugLine="LineChart.Line_1_LineWidth = 11.0";
mostCurrent._linechart.setLine_1_LineWidth((float) (11.0));
 //BA.debugLineNum = 891;BA.debugLine="LineChart.Line_1_PointColor = Colors.Black";
mostCurrent._linechart.setLine_1_PointColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 //BA.debugLineNum = 892;BA.debugLine="LineChart.Line_1_PointSize = 25.0";
mostCurrent._linechart.setLine_1_PointSize((float) (25.0));
 //BA.debugLineNum = 893;BA.debugLine="LineChart.Line_1_PointShape = LineChart.SHAPE_RO";
mostCurrent._linechart.setLine_1_PointShape(mostCurrent._linechart.SHAPE_ROUND);
 //BA.debugLineNum = 894;BA.debugLine="LineChart.Line_1_DrawDash = False";
mostCurrent._linechart.setLine_1_DrawDash(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 895;BA.debugLine="LineChart.Line_1_DrawCubic = False";
mostCurrent._linechart.setLine_1_DrawCubic(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 901;BA.debugLine="ReadTemperatureDaily(\"Yesterday\")";
_readtemperaturedaily("Yesterday");
 //BA.debugLineNum = 903;BA.debugLine="Dim Yesterday As Long";
_yesterday = 0L;
 //BA.debugLineNum = 904;BA.debugLine="Yesterday = DateTime.add(DateTime.Now, 0, 0, -1)";
_yesterday = anywheresoftware.b4a.keywords.Common.DateTime.Add(anywheresoftware.b4a.keywords.Common.DateTime.getNow(),(int) (0),(int) (0),(int) (-1));
 //BA.debugLineNum = 906;BA.debugLine="DateTime.DateFormat = \"MMM d, yyyy\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("MMM d, yyyy");
 //BA.debugLineNum = 907;BA.debugLine="LineChart.Line_2_LegendText = \"Yesterday, \" & Da";
mostCurrent._linechart.setLine_2_LegendText("Yesterday, "+anywheresoftware.b4a.keywords.Common.DateTime.Date(_yesterday));
 //BA.debugLineNum = 909;BA.debugLine="CheckTempBoundariesDaily";
_checktempboundariesdaily();
 //BA.debugLineNum = 911;BA.debugLine="If am12 <> tempZeroRange Then";
if ((mostCurrent._am12).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 912;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12)";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12))});
 };
 //BA.debugLineNum = 914;BA.debugLine="If am1 <> tempZeroRange Then";
if ((mostCurrent._am1).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 915;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1))});
 };
 //BA.debugLineNum = 917;BA.debugLine="If am2 <> tempZeroRange Then";
if ((mostCurrent._am2).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 918;BA.debugLine="If am1 = tempZeroRange Then";
if ((mostCurrent._am1).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 919;BA.debugLine="am1 = (am12 + am2)/2";
mostCurrent._am1 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am12))+(double)(Double.parseDouble(mostCurrent._am2)))/(double)2);
 };
 //BA.debugLineNum = 921;BA.debugLine="If am12 = tempZeroRange Then";
if ((mostCurrent._am12).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 922;BA.debugLine="am12 = am1";
mostCurrent._am12 = mostCurrent._am1;
 };
 //BA.debugLineNum = 924;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2))});
 };
 //BA.debugLineNum = 926;BA.debugLine="If am3 <> tempZeroRange Then";
if ((mostCurrent._am3).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 927;BA.debugLine="If am2 = tempZeroRange Then";
if ((mostCurrent._am2).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 928;BA.debugLine="am2 = (am1 + am3)/2";
mostCurrent._am2 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am1))+(double)(Double.parseDouble(mostCurrent._am3)))/(double)2);
 };
 //BA.debugLineNum = 930;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3))});
 };
 //BA.debugLineNum = 932;BA.debugLine="If am4 <> tempZeroRange Then";
if ((mostCurrent._am4).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 933;BA.debugLine="If am3 = tempZeroRange Then";
if ((mostCurrent._am3).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 934;BA.debugLine="am3 = (am2 + am4)/2";
mostCurrent._am3 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am2))+(double)(Double.parseDouble(mostCurrent._am4)))/(double)2);
 };
 //BA.debugLineNum = 936;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4))});
 };
 //BA.debugLineNum = 938;BA.debugLine="If am5 <> tempZeroRange Then";
if ((mostCurrent._am5).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 939;BA.debugLine="If am4 = tempZeroRange Then";
if ((mostCurrent._am4).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 940;BA.debugLine="am4 = (am3 + am5)/2";
mostCurrent._am4 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am3))+(double)(Double.parseDouble(mostCurrent._am5)))/(double)2);
 };
 //BA.debugLineNum = 942;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5))});
 };
 //BA.debugLineNum = 944;BA.debugLine="If am6 <> tempZeroRange Then";
if ((mostCurrent._am6).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 945;BA.debugLine="If am5 = tempZeroRange Then";
if ((mostCurrent._am5).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 946;BA.debugLine="am5 = (am4 + am6)/2";
mostCurrent._am5 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am4))+(double)(Double.parseDouble(mostCurrent._am6)))/(double)2);
 };
 //BA.debugLineNum = 948;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6))});
 };
 //BA.debugLineNum = 950;BA.debugLine="If am7 <> tempZeroRange Then";
if ((mostCurrent._am7).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 951;BA.debugLine="If am6 = tempZeroRange Then";
if ((mostCurrent._am6).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 952;BA.debugLine="am6 = (am5 + am7)/2";
mostCurrent._am6 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am5))+(double)(Double.parseDouble(mostCurrent._am7)))/(double)2);
 };
 //BA.debugLineNum = 954;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7))});
 };
 //BA.debugLineNum = 956;BA.debugLine="If am8 <> tempZeroRange Then";
if ((mostCurrent._am8).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 957;BA.debugLine="If am7 = tempZeroRange Then";
if ((mostCurrent._am7).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 958;BA.debugLine="am7 = (am6 + am8)/2";
mostCurrent._am7 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am6))+(double)(Double.parseDouble(mostCurrent._am8)))/(double)2);
 };
 //BA.debugLineNum = 960;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8))});
 };
 //BA.debugLineNum = 962;BA.debugLine="If am9 <> tempZeroRange Then";
if ((mostCurrent._am9).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 963;BA.debugLine="If am8 = tempZeroRange Then";
if ((mostCurrent._am8).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 964;BA.debugLine="am8 = (am7 + am9)/2";
mostCurrent._am8 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am7))+(double)(Double.parseDouble(mostCurrent._am9)))/(double)2);
 };
 //BA.debugLineNum = 966;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9))});
 };
 //BA.debugLineNum = 968;BA.debugLine="If am10 <> tempZeroRange Then";
if ((mostCurrent._am10).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 969;BA.debugLine="If am9 = tempZeroRange Then";
if ((mostCurrent._am9).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 970;BA.debugLine="am9 = (am8 + am10)/2";
mostCurrent._am9 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am8))+(double)(Double.parseDouble(mostCurrent._am10)))/(double)2);
 };
 //BA.debugLineNum = 972;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10))});
 };
 //BA.debugLineNum = 974;BA.debugLine="If am11 <> tempZeroRange Then";
if ((mostCurrent._am11).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 975;BA.debugLine="If am10 = tempZeroRange Then";
if ((mostCurrent._am10).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 976;BA.debugLine="am10 = (am9 + am11)/2";
mostCurrent._am10 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am9))+(double)(Double.parseDouble(mostCurrent._am11)))/(double)2);
 };
 //BA.debugLineNum = 978;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11))});
 };
 //BA.debugLineNum = 980;BA.debugLine="If pm12 <> tempZeroRange Then";
if ((mostCurrent._pm12).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 981;BA.debugLine="If am11 = tempZeroRange Then";
if ((mostCurrent._am11).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 982;BA.debugLine="am11 = (am10 + pm12)/2";
mostCurrent._am11 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am10))+(double)(Double.parseDouble(mostCurrent._pm12)))/(double)2);
 };
 //BA.debugLineNum = 984;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12))});
 };
 //BA.debugLineNum = 986;BA.debugLine="If pm1 <> tempZeroRange Then";
if ((mostCurrent._pm1).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 987;BA.debugLine="If pm12 = tempZeroRange Then";
if ((mostCurrent._pm12).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 988;BA.debugLine="pm12 = (am11 + pm1)/2";
mostCurrent._pm12 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am11))+(double)(Double.parseDouble(mostCurrent._pm1)))/(double)2);
 };
 //BA.debugLineNum = 990;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1))});
 };
 //BA.debugLineNum = 992;BA.debugLine="If pm2 <> tempZeroRange Then";
if ((mostCurrent._pm2).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 993;BA.debugLine="If pm1 = tempZeroRange Then";
if ((mostCurrent._pm1).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 994;BA.debugLine="pm1 = (pm12 + pm2)/2";
mostCurrent._pm1 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm12))+(double)(Double.parseDouble(mostCurrent._pm2)))/(double)2);
 };
 //BA.debugLineNum = 996;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2))});
 };
 //BA.debugLineNum = 998;BA.debugLine="If pm3 <> tempZeroRange Then";
if ((mostCurrent._pm3).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 999;BA.debugLine="If pm2 = tempZeroRange Then";
if ((mostCurrent._pm2).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1000;BA.debugLine="pm2 = (pm1 + pm3)/2";
mostCurrent._pm2 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm1))+(double)(Double.parseDouble(mostCurrent._pm3)))/(double)2);
 };
 //BA.debugLineNum = 1002;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3))});
 };
 //BA.debugLineNum = 1004;BA.debugLine="If pm4 <> tempZeroRange Then";
if ((mostCurrent._pm4).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1005;BA.debugLine="If pm3 = tempZeroRange Then";
if ((mostCurrent._pm3).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1006;BA.debugLine="pm3 = (pm2 + pm4)/2";
mostCurrent._pm3 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm2))+(double)(Double.parseDouble(mostCurrent._pm4)))/(double)2);
 };
 //BA.debugLineNum = 1008;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4))});
 };
 //BA.debugLineNum = 1010;BA.debugLine="If pm5 <> tempZeroRange Then";
if ((mostCurrent._pm5).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1011;BA.debugLine="If pm4 = tempZeroRange Then";
if ((mostCurrent._pm4).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1012;BA.debugLine="pm4 = (pm3 + pm5)/2";
mostCurrent._pm4 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm3))+(double)(Double.parseDouble(mostCurrent._pm5)))/(double)2);
 };
 //BA.debugLineNum = 1014;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5))});
 };
 //BA.debugLineNum = 1016;BA.debugLine="If pm6 <> tempZeroRange Then";
if ((mostCurrent._pm6).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1017;BA.debugLine="If pm5 = tempZeroRange Then";
if ((mostCurrent._pm5).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1018;BA.debugLine="pm5 = (pm4 + pm6)/2";
mostCurrent._pm5 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm4))+(double)(Double.parseDouble(mostCurrent._pm6)))/(double)2);
 };
 //BA.debugLineNum = 1020;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5)),(float)(Double.parseDouble(mostCurrent._pm6))});
 };
 //BA.debugLineNum = 1022;BA.debugLine="If pm7 <> tempZeroRange Then";
if ((mostCurrent._pm7).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1023;BA.debugLine="If pm6 = tempZeroRange Then";
if ((mostCurrent._pm6).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1024;BA.debugLine="pm6 = (pm5 + pm7)/2";
mostCurrent._pm6 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm5))+(double)(Double.parseDouble(mostCurrent._pm7)))/(double)2);
 };
 //BA.debugLineNum = 1026;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5)),(float)(Double.parseDouble(mostCurrent._pm6)),(float)(Double.parseDouble(mostCurrent._pm7))});
 };
 //BA.debugLineNum = 1028;BA.debugLine="If pm8 <> tempZeroRange Then";
if ((mostCurrent._pm8).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1029;BA.debugLine="If pm7 = tempZeroRange Then";
if ((mostCurrent._pm7).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1030;BA.debugLine="pm7 = (pm6 + pm8)/2";
mostCurrent._pm7 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm6))+(double)(Double.parseDouble(mostCurrent._pm8)))/(double)2);
 };
 //BA.debugLineNum = 1032;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5)),(float)(Double.parseDouble(mostCurrent._pm6)),(float)(Double.parseDouble(mostCurrent._pm7)),(float)(Double.parseDouble(mostCurrent._pm8))});
 };
 //BA.debugLineNum = 1034;BA.debugLine="If pm9 <> tempZeroRange Then";
if ((mostCurrent._pm9).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1035;BA.debugLine="If pm8 = tempZeroRange Then";
if ((mostCurrent._pm8).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1036;BA.debugLine="pm8 = (pm7 + pm9)/2";
mostCurrent._pm8 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm7))+(double)(Double.parseDouble(mostCurrent._pm9)))/(double)2);
 };
 //BA.debugLineNum = 1038;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5)),(float)(Double.parseDouble(mostCurrent._pm6)),(float)(Double.parseDouble(mostCurrent._pm7)),(float)(Double.parseDouble(mostCurrent._pm8)),(float)(Double.parseDouble(mostCurrent._pm9))});
 };
 //BA.debugLineNum = 1040;BA.debugLine="If pm10 <> tempZeroRange Then";
if ((mostCurrent._pm10).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1041;BA.debugLine="If pm9 = tempZeroRange Then";
if ((mostCurrent._pm9).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1042;BA.debugLine="pm9 = (pm8 + pm10)/2";
mostCurrent._pm9 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm8))+(double)(Double.parseDouble(mostCurrent._pm10)))/(double)2);
 };
 //BA.debugLineNum = 1044;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5)),(float)(Double.parseDouble(mostCurrent._pm6)),(float)(Double.parseDouble(mostCurrent._pm7)),(float)(Double.parseDouble(mostCurrent._pm8)),(float)(Double.parseDouble(mostCurrent._pm9)),(float)(Double.parseDouble(mostCurrent._pm10))});
 };
 //BA.debugLineNum = 1046;BA.debugLine="If pm11 <> tempZeroRange Then";
if ((mostCurrent._pm11).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 1047;BA.debugLine="If pm10 = tempZeroRange Then";
if ((mostCurrent._pm10).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 1048;BA.debugLine="pm10 = (pm9 + pm11)/2";
mostCurrent._pm10 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm9))+(double)(Double.parseDouble(mostCurrent._pm11)))/(double)2);
 };
 //BA.debugLineNum = 1050;BA.debugLine="LineChart.Line_2_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5)),(float)(Double.parseDouble(mostCurrent._pm6)),(float)(Double.parseDouble(mostCurrent._pm7)),(float)(Double.parseDouble(mostCurrent._pm8)),(float)(Double.parseDouble(mostCurrent._pm9)),(float)(Double.parseDouble(mostCurrent._pm10)),(float)(Double.parseDouble(mostCurrent._pm11))});
 };
 //BA.debugLineNum = 1053;BA.debugLine="LineChart.Line_2_PointLabelTextColor = Colors.Cy";
mostCurrent._linechart.setLine_2_PointLabelTextColor(anywheresoftware.b4a.keywords.Common.Colors.Cyan);
 //BA.debugLineNum = 1054;BA.debugLine="LineChart.Line_2_PointLabelTextSize = 35.0";
mostCurrent._linechart.setLine_2_PointLabelTextSize((float) (35.0));
 //BA.debugLineNum = 1055;BA.debugLine="LineChart.Line_2_LineColor = Colors.White";
mostCurrent._linechart.setLine_2_LineColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 1056;BA.debugLine="LineChart.Line_2_LineWidth = 7.0";
mostCurrent._linechart.setLine_2_LineWidth((float) (7.0));
 //BA.debugLineNum = 1057;BA.debugLine="LineChart.Line_2_PointColor = Colors.Cyan";
mostCurrent._linechart.setLine_2_PointColor(anywheresoftware.b4a.keywords.Common.Colors.Cyan);
 //BA.debugLineNum = 1058;BA.debugLine="LineChart.Line_2_PointSize = 10.0";
mostCurrent._linechart.setLine_2_PointSize((float) (10.0));
 //BA.debugLineNum = 1059;BA.debugLine="LineChart.Line_2_PointShape = LineChart.SHAPE_RO";
mostCurrent._linechart.setLine_2_PointShape(mostCurrent._linechart.SHAPE_ROUND);
 //BA.debugLineNum = 1060;BA.debugLine="LineChart.Line_2_DrawDash = False";
mostCurrent._linechart.setLine_2_DrawDash(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1061;BA.debugLine="LineChart.Line_2_DrawCubic = False";
mostCurrent._linechart.setLine_2_DrawCubic(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1067;BA.debugLine="LineChart.Line_3_LegendText = \"Real time\"";
mostCurrent._linechart.setLine_3_LegendText("Real time");
 //BA.debugLineNum = 1068;BA.debugLine="LineChart.Line_3_Data = Array As Float (tempRigh";
mostCurrent._linechart.setLine_3_Data(new float[]{(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow))});
 //BA.debugLineNum = 1069;BA.debugLine="LineChart.Line_3_PointLabelTextColor = Colors.Gr";
mostCurrent._linechart.setLine_3_PointLabelTextColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 1070;BA.debugLine="LineChart.Line_3_PointLabelTextSize = 30.0";
mostCurrent._linechart.setLine_3_PointLabelTextSize((float) (30.0));
 //BA.debugLineNum = 1071;BA.debugLine="LineChart.Line_3_LineColor = Colors.Green";
mostCurrent._linechart.setLine_3_LineColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 1072;BA.debugLine="LineChart.Line_3_LineWidth = 5.0";
mostCurrent._linechart.setLine_3_LineWidth((float) (5.0));
 //BA.debugLineNum = 1073;BA.debugLine="LineChart.Line_3_PointColor = Colors.Green";
mostCurrent._linechart.setLine_3_PointColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 1074;BA.debugLine="LineChart.Line_3_PointSize = 1.0";
mostCurrent._linechart.setLine_3_PointSize((float) (1.0));
 //BA.debugLineNum = 1075;BA.debugLine="LineChart.Line_3_PointShape = LineChart.SHAPE_RO";
mostCurrent._linechart.setLine_3_PointShape(mostCurrent._linechart.SHAPE_ROUND);
 //BA.debugLineNum = 1076;BA.debugLine="LineChart.Line_3_DrawDash = False";
mostCurrent._linechart.setLine_3_DrawDash(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1077;BA.debugLine="LineChart.Line_3_DrawCubic = False";
mostCurrent._linechart.setLine_3_DrawCubic(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1081;BA.debugLine="LineChart.NumberOfLineCharts = 3";
mostCurrent._linechart.setNumberOfLineCharts((int) (3));
 //BA.debugLineNum = 1083;BA.debugLine="LineChart.DrawTheGraphs";
mostCurrent._linechart.DrawTheGraphs();
 } 
       catch (Exception e375) {
			processBA.setLastException(e375); //BA.debugLineNum = 1086;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("6721308",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 //BA.debugLineNum = 1087;BA.debugLine="ToastMessageShow (LastException,True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getObject()),anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 1089;BA.debugLine="End Sub";
return "";
}
public static String  _temperaturedailytimer_tick() throws Exception{
 //BA.debugLineNum = 2220;BA.debugLine="Sub TemperatureDailyTimer_Tick";
 //BA.debugLineNum = 2221;BA.debugLine="Activity.RequestFocus";
mostCurrent._activity.RequestFocus();
 //BA.debugLineNum = 2222;BA.debugLine="btnHumidityHourly.RemoveView";
mostCurrent._btnhumidityhourly.RemoveView();
 //BA.debugLineNum = 2223;BA.debugLine="btnTempHourly.RemoveView";
mostCurrent._btntemphourly.RemoveView();
 //BA.debugLineNum = 2224;BA.debugLine="btnHumidityDaily.RemoveView";
mostCurrent._btnhumiditydaily.RemoveView();
 //BA.debugLineNum = 2225;BA.debugLine="btnTempDaily.RemoveView";
mostCurrent._btntempdaily.RemoveView();
 //BA.debugLineNum = 2226;BA.debugLine="LineChart.RemoveView";
mostCurrent._linechart.RemoveView();
 //BA.debugLineNum = 2227;BA.debugLine="tempMaxRange=0";
_tempmaxrange = (float) (0);
 //BA.debugLineNum = 2228;BA.debugLine="tempMinRange=0";
_tempminrange = (float) (0);
 //BA.debugLineNum = 2229;BA.debugLine="TemperatureDailyCreate";
_temperaturedailycreate();
 //BA.debugLineNum = 2230;BA.debugLine="End Sub";
return "";
}
public static String  _temperaturehourlycreate() throws Exception{
anywheresoftware.b4a.keywords.LayoutValues _lv = null;
anywheresoftware.b4j.object.JavaObject _jo = null;
int _i = 0;
b4a.example.dateutils._period _p = null;
long _nexttime = 0L;
 //BA.debugLineNum = 138;BA.debugLine="Private Sub TemperatureHourlyCreate()";
 //BA.debugLineNum = 139;BA.debugLine="Try";
try { //BA.debugLineNum = 141;BA.debugLine="Dim lv As LayoutValues = GetRealSize";
_lv = _getrealsize();
 //BA.debugLineNum = 142;BA.debugLine="Dim jo As JavaObject = Activity";
_jo = new anywheresoftware.b4j.object.JavaObject();
_jo = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(mostCurrent._activity.getObject()));
 //BA.debugLineNum = 143;BA.debugLine="jo.RunMethod(\"setBottom\", Array(lv.Height))";
_jo.RunMethod("setBottom",new Object[]{(Object)(_lv.Height)});
 //BA.debugLineNum = 144;BA.debugLine="jo.RunMethod(\"setRight\", Array(lv.Width))";
_jo.RunMethod("setRight",new Object[]{(Object)(_lv.Width)});
 //BA.debugLineNum = 145;BA.debugLine="Activity.Height = lv.Height";
mostCurrent._activity.setHeight(_lv.Height);
 //BA.debugLineNum = 146;BA.debugLine="Activity.Width = lv.Width";
mostCurrent._activity.setWidth(_lv.Width);
 //BA.debugLineNum = 149;BA.debugLine="Activity.LoadLayout(\"Main\")";
mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 //BA.debugLineNum = 151;BA.debugLine="LineChart.GraphBackgroundColor = Colors.DarkGray";
mostCurrent._linechart.setGraphBackgroundColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 152;BA.debugLine="LineChart.GraphFrameColor = Colors.Blue";
mostCurrent._linechart.setGraphFrameColor(anywheresoftware.b4a.keywords.Common.Colors.Blue);
 //BA.debugLineNum = 153;BA.debugLine="LineChart.GraphFrameWidth = 4.0";
mostCurrent._linechart.setGraphFrameWidth((float) (4.0));
 //BA.debugLineNum = 154;BA.debugLine="LineChart.GraphPlotAreaBackgroundColor = Colors.";
mostCurrent._linechart.setGraphPlotAreaBackgroundColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (50),(int) (0),(int) (0),(int) (255)));
 //BA.debugLineNum = 155;BA.debugLine="LineChart.GraphTitleTextSize = 15";
mostCurrent._linechart.setGraphTitleTextSize((int) (15));
 //BA.debugLineNum = 156;BA.debugLine="LineChart.GraphTitleColor = Colors.White";
mostCurrent._linechart.setGraphTitleColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 157;BA.debugLine="LineChart.GraphTitleSkewX = -0.25";
mostCurrent._linechart.setGraphTitleSkewX((float) (-0.25));
 //BA.debugLineNum = 158;BA.debugLine="LineChart.GraphTitleUnderline = True";
mostCurrent._linechart.setGraphTitleUnderline(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 159;BA.debugLine="LineChart.GraphTitleBold = True";
mostCurrent._linechart.setGraphTitleBold(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 160;BA.debugLine="LineChart.GraphTitle = \"TEMPERATURE HOURLY  \"";
mostCurrent._linechart.setGraphTitle("TEMPERATURE HOURLY  ");
 //BA.debugLineNum = 162;BA.debugLine="LineChart.LegendBackgroundColor = Colors.White";
mostCurrent._linechart.setLegendBackgroundColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 163;BA.debugLine="LineChart.LegendTextColor = Colors.Black";
mostCurrent._linechart.setLegendTextColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 //BA.debugLineNum = 164;BA.debugLine="LineChart.LegendTextSize = 18.0";
mostCurrent._linechart.setLegendTextSize((float) (18.0));
 //BA.debugLineNum = 166;BA.debugLine="DateTime.TimeFormat = \"h:mm a\"";
anywheresoftware.b4a.keywords.Common.DateTime.setTimeFormat("h:mm a");
 //BA.debugLineNum = 167;BA.debugLine="LineChart.DomianLabel = \"The time now is: \" & Da";
mostCurrent._linechart.setDomianLabel("The time now is: "+anywheresoftware.b4a.keywords.Common.DateTime.Time(anywheresoftware.b4a.keywords.Common.DateTime.getNow()));
 //BA.debugLineNum = 168;BA.debugLine="LineChart.DomainLabelColor = Colors.Green";
mostCurrent._linechart.setDomainLabelColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 169;BA.debugLine="LineChart.DomainLabelTextSize = 25.0";
mostCurrent._linechart.setDomainLabelTextSize((float) (25.0));
 //BA.debugLineNum = 171;BA.debugLine="LineChart.XaxisGridLineColor = Colors.ARGB(100,2";
mostCurrent._linechart.setXaxisGridLineColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (100),(int) (255),(int) (255),(int) (255)));
 //BA.debugLineNum = 172;BA.debugLine="LineChart.XaxisGridLineWidth = 2.0";
mostCurrent._linechart.setXaxisGridLineWidth((float) (2.0));
 //BA.debugLineNum = 173;BA.debugLine="LineChart.XaxisLabelTicks = 1";
mostCurrent._linechart.setXaxisLabelTicks((int) (1));
 //BA.debugLineNum = 174;BA.debugLine="LineChart.XaxisLabelOrientation = 0";
mostCurrent._linechart.setXaxisLabelOrientation((float) (0));
 //BA.debugLineNum = 175;BA.debugLine="LineChart.XaxisLabelTextColor = Colors.White";
mostCurrent._linechart.setXaxisLabelTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 176;BA.debugLine="LineChart.XaxisLabelTextSize = 32.0";
mostCurrent._linechart.setXaxisLabelTextSize((float) (32.0));
 //BA.debugLineNum = 180;BA.debugLine="timeRightNow = DateTime.Now";
_timerightnow = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 182;BA.debugLine="For i = 23 To 0 Step -1";
{
final int step33 = -1;
final int limit33 = (int) (0);
_i = (int) (23) ;
for (;_i >= limit33 ;_i = _i + step33 ) {
 //BA.debugLineNum = 183;BA.debugLine="Dim p As Period";
_p = new b4a.example.dateutils._period();
 //BA.debugLineNum = 184;BA.debugLine="p.Hours = 0";
_p.Hours = (int) (0);
 //BA.debugLineNum = 185;BA.debugLine="p.Minutes = (i+1) * -5";
_p.Minutes = (int) ((_i+1)*-5);
 //BA.debugLineNum = 186;BA.debugLine="p.Seconds = 0";
_p.Seconds = (int) (0);
 //BA.debugLineNum = 187;BA.debugLine="Dim NextTime As Long";
_nexttime = 0L;
 //BA.debugLineNum = 188;BA.debugLine="NextTime = DateUtils.AddPeriod(timeRightNow, p)";
_nexttime = mostCurrent._dateutils._addperiod(mostCurrent.activityBA,_timerightnow,_p);
 //BA.debugLineNum = 189;BA.debugLine="DateTime.TimeFormat = \"HH:mm\"";
anywheresoftware.b4a.keywords.Common.DateTime.setTimeFormat("HH:mm");
 //BA.debugLineNum = 191;BA.debugLine="timeArray(23-i) = DateTime.Time(NextTime) 'Date";
mostCurrent._timearray[(int) (23-_i)] = anywheresoftware.b4a.keywords.Common.DateTime.Time(_nexttime);
 }
};
 //BA.debugLineNum = 193;BA.debugLine="LineChart.XAxisLabels = timeArray 'Array As Stri";
mostCurrent._linechart.setXAxisLabels(mostCurrent._timearray);
 //BA.debugLineNum = 196;BA.debugLine="LineChart.YaxisDivisions = 10";
mostCurrent._linechart.setYaxisDivisions((int) (10));
 //BA.debugLineNum = 198;BA.debugLine="LineChart.YaxisValueFormat = LineChart.ValueForm";
mostCurrent._linechart.setYaxisValueFormat(mostCurrent._linechart.ValueFormat_2);
 //BA.debugLineNum = 199;BA.debugLine="LineChart.YaxisGridLineColor = Colors.Black";
mostCurrent._linechart.setYaxisGridLineColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 //BA.debugLineNum = 200;BA.debugLine="LineChart.YaxisGridLineWidth = 2";
mostCurrent._linechart.setYaxisGridLineWidth((float) (2));
 //BA.debugLineNum = 201;BA.debugLine="LineChart.YaxisLabelTicks = 1";
mostCurrent._linechart.setYaxisLabelTicks((int) (1));
 //BA.debugLineNum = 202;BA.debugLine="LineChart.YaxisLabelColor = Colors.Yellow";
mostCurrent._linechart.setYaxisLabelColor(anywheresoftware.b4a.keywords.Common.Colors.Yellow);
 //BA.debugLineNum = 203;BA.debugLine="LineChart.YaxisLabelOrientation = -30";
mostCurrent._linechart.setYaxisLabelOrientation((float) (-30));
 //BA.debugLineNum = 204;BA.debugLine="LineChart.YaxisLabelTextSize = 25.0";
mostCurrent._linechart.setYaxisLabelTextSize((float) (25.0));
 //BA.debugLineNum = 205;BA.debugLine="LineChart.YaxisTitleColor = Colors.Green";
mostCurrent._linechart.setYaxisTitleColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 206;BA.debugLine="LineChart.YaxisTitleFakeBold = False";
mostCurrent._linechart.setYaxisTitleFakeBold(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 207;BA.debugLine="LineChart.YaxisTitleTextSize = 20.0";
mostCurrent._linechart.setYaxisTitleTextSize((float) (20.0));
 //BA.debugLineNum = 208;BA.debugLine="LineChart.YaxisTitleUnderline = True";
mostCurrent._linechart.setYaxisTitleUnderline(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 209;BA.debugLine="LineChart.YaxisTitleTextSkewness = 0";
mostCurrent._linechart.setYaxisTitleTextSkewness((float) (0));
 //BA.debugLineNum = 210;BA.debugLine="LineChart.YaxisLabelAndTitleDistance = 60.0";
mostCurrent._linechart.setYaxisLabelAndTitleDistance((float) (60.0));
 //BA.debugLineNum = 211;BA.debugLine="LineChart.YaxisTitle = \"Temperature (Fahrenheit)";
mostCurrent._linechart.setYaxisTitle("Temperature (Fahrenheit)");
 //BA.debugLineNum = 213;BA.debugLine="LineChart.MaxNumberOfEntriesPerLineChart = 24";
mostCurrent._linechart.setMaxNumberOfEntriesPerLineChart((int) (24));
 //BA.debugLineNum = 214;BA.debugLine="LineChart.GraphLegendVisibility = False";
mostCurrent._linechart.setGraphLegendVisibility(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 218;BA.debugLine="ReadTemperatureHourly(\"Today\")";
_readtemperaturehourly("Today");
 //BA.debugLineNum = 220;BA.debugLine="DateTime.DateFormat = \"MMM d, yyyy\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("MMM d, yyyy");
 //BA.debugLineNum = 221;BA.debugLine="LineChart.Line_1_LegendText = \"From \" & timeArra";
mostCurrent._linechart.setLine_1_LegendText("From "+mostCurrent._timearray[(int) (0)]+" to "+mostCurrent._timearray[(int) (23)]);
 //BA.debugLineNum = 223;BA.debugLine="CheckTempBoundaries";
_checktempboundaries();
 //BA.debugLineNum = 225;BA.debugLine="If am12 <> tempZeroRange Then";
if ((mostCurrent._am12).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 226;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12)";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12))});
 };
 //BA.debugLineNum = 228;BA.debugLine="If am1 <> tempZeroRange Then";
if ((mostCurrent._am1).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 229;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1))});
 };
 //BA.debugLineNum = 231;BA.debugLine="If am2 <> tempZeroRange Then";
if ((mostCurrent._am2).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 232;BA.debugLine="If am1 = tempZeroRange Then";
if ((mostCurrent._am1).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 233;BA.debugLine="am1 = (am12 + am2)/2";
mostCurrent._am1 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am12))+(double)(Double.parseDouble(mostCurrent._am2)))/(double)2);
 };
 //BA.debugLineNum = 235;BA.debugLine="If am12 = tempZeroRange Then";
if ((mostCurrent._am12).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 236;BA.debugLine="am12 = am1";
mostCurrent._am12 = mostCurrent._am1;
 };
 //BA.debugLineNum = 238;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2))});
 };
 //BA.debugLineNum = 240;BA.debugLine="If am3 <> tempZeroRange Then";
if ((mostCurrent._am3).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 241;BA.debugLine="If am2 = tempZeroRange Then";
if ((mostCurrent._am2).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 242;BA.debugLine="am2 = (am1 + am3)/2";
mostCurrent._am2 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am1))+(double)(Double.parseDouble(mostCurrent._am3)))/(double)2);
 };
 //BA.debugLineNum = 244;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3))});
 };
 //BA.debugLineNum = 246;BA.debugLine="If am4 <> tempZeroRange Then";
if ((mostCurrent._am4).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 247;BA.debugLine="If am3 = tempZeroRange Then";
if ((mostCurrent._am3).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 248;BA.debugLine="am3 = (am2 + am4)/2";
mostCurrent._am3 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am2))+(double)(Double.parseDouble(mostCurrent._am4)))/(double)2);
 };
 //BA.debugLineNum = 250;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4))});
 };
 //BA.debugLineNum = 252;BA.debugLine="If am5 <> tempZeroRange Then";
if ((mostCurrent._am5).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 253;BA.debugLine="If am4 = tempZeroRange Then";
if ((mostCurrent._am4).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 254;BA.debugLine="am4 = (am3 + am5)/2";
mostCurrent._am4 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am3))+(double)(Double.parseDouble(mostCurrent._am5)))/(double)2);
 };
 //BA.debugLineNum = 256;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5))});
 };
 //BA.debugLineNum = 258;BA.debugLine="If am6 <> tempZeroRange Then";
if ((mostCurrent._am6).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 259;BA.debugLine="If am5 = tempZeroRange Then";
if ((mostCurrent._am5).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 260;BA.debugLine="am5 = (am4 + am6)/2";
mostCurrent._am5 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am4))+(double)(Double.parseDouble(mostCurrent._am6)))/(double)2);
 };
 //BA.debugLineNum = 262;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6))});
 };
 //BA.debugLineNum = 264;BA.debugLine="If am7 <> tempZeroRange Then";
if ((mostCurrent._am7).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 265;BA.debugLine="If am6 = tempZeroRange Then";
if ((mostCurrent._am6).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 266;BA.debugLine="am6 = (am5 + am7)/2";
mostCurrent._am6 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am5))+(double)(Double.parseDouble(mostCurrent._am7)))/(double)2);
 };
 //BA.debugLineNum = 268;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7))});
 };
 //BA.debugLineNum = 270;BA.debugLine="If am8 <> tempZeroRange Then";
if ((mostCurrent._am8).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 271;BA.debugLine="If am7 = tempZeroRange Then";
if ((mostCurrent._am7).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 272;BA.debugLine="am7 = (am6 + am8)/2";
mostCurrent._am7 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am6))+(double)(Double.parseDouble(mostCurrent._am8)))/(double)2);
 };
 //BA.debugLineNum = 274;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8))});
 };
 //BA.debugLineNum = 276;BA.debugLine="If am9 <> tempZeroRange Then";
if ((mostCurrent._am9).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 277;BA.debugLine="If am8 = tempZeroRange Then";
if ((mostCurrent._am8).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 278;BA.debugLine="am8 = (am7 + am9)/2";
mostCurrent._am8 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am7))+(double)(Double.parseDouble(mostCurrent._am9)))/(double)2);
 };
 //BA.debugLineNum = 280;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9))});
 };
 //BA.debugLineNum = 282;BA.debugLine="If am10 <> tempZeroRange Then";
if ((mostCurrent._am10).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 283;BA.debugLine="If am9 = tempZeroRange Then";
if ((mostCurrent._am9).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 284;BA.debugLine="am9 = (am8 + am10)/2";
mostCurrent._am9 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am8))+(double)(Double.parseDouble(mostCurrent._am10)))/(double)2);
 };
 //BA.debugLineNum = 286;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10))});
 };
 //BA.debugLineNum = 288;BA.debugLine="If am11 <> tempZeroRange Then";
if ((mostCurrent._am11).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 289;BA.debugLine="If am10 = tempZeroRange Then";
if ((mostCurrent._am10).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 290;BA.debugLine="am10 = (am9 + am11)/2";
mostCurrent._am10 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am9))+(double)(Double.parseDouble(mostCurrent._am11)))/(double)2);
 };
 //BA.debugLineNum = 292;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11))});
 };
 //BA.debugLineNum = 294;BA.debugLine="If pm12 <> tempZeroRange Then";
if ((mostCurrent._pm12).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 295;BA.debugLine="If am11 = tempZeroRange Then";
if ((mostCurrent._am11).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 296;BA.debugLine="am11 = (am10 + pm12)/2";
mostCurrent._am11 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am10))+(double)(Double.parseDouble(mostCurrent._pm12)))/(double)2);
 };
 //BA.debugLineNum = 298;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12))});
 };
 //BA.debugLineNum = 300;BA.debugLine="If pm1 <> tempZeroRange Then";
if ((mostCurrent._pm1).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 301;BA.debugLine="If pm12 = tempZeroRange Then";
if ((mostCurrent._pm12).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 302;BA.debugLine="pm12 = (am11 + pm1)/2";
mostCurrent._pm12 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._am11))+(double)(Double.parseDouble(mostCurrent._pm1)))/(double)2);
 };
 //BA.debugLineNum = 304;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1))});
 };
 //BA.debugLineNum = 306;BA.debugLine="If pm2 <> tempZeroRange Then";
if ((mostCurrent._pm2).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 307;BA.debugLine="If pm1 = tempZeroRange Then";
if ((mostCurrent._pm1).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 308;BA.debugLine="pm1 = (pm12 + pm2)/2";
mostCurrent._pm1 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm12))+(double)(Double.parseDouble(mostCurrent._pm2)))/(double)2);
 };
 //BA.debugLineNum = 310;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2))});
 };
 //BA.debugLineNum = 312;BA.debugLine="If pm3 <> tempZeroRange Then";
if ((mostCurrent._pm3).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 313;BA.debugLine="If pm2 = tempZeroRange Then";
if ((mostCurrent._pm2).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 314;BA.debugLine="pm2 = (pm1 + pm3)/2";
mostCurrent._pm2 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm1))+(double)(Double.parseDouble(mostCurrent._pm3)))/(double)2);
 };
 //BA.debugLineNum = 316;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3))});
 };
 //BA.debugLineNum = 318;BA.debugLine="If pm4 <> tempZeroRange Then";
if ((mostCurrent._pm4).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 319;BA.debugLine="If pm3 = tempZeroRange Then";
if ((mostCurrent._pm3).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 320;BA.debugLine="pm3 = (pm2 + pm4)/2";
mostCurrent._pm3 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm2))+(double)(Double.parseDouble(mostCurrent._pm4)))/(double)2);
 };
 //BA.debugLineNum = 322;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4))});
 };
 //BA.debugLineNum = 324;BA.debugLine="If pm5 <> tempZeroRange Then";
if ((mostCurrent._pm5).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 325;BA.debugLine="If pm4 = tempZeroRange Then";
if ((mostCurrent._pm4).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 326;BA.debugLine="pm4 = (pm3 + pm5)/2";
mostCurrent._pm4 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm3))+(double)(Double.parseDouble(mostCurrent._pm5)))/(double)2);
 };
 //BA.debugLineNum = 328;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5))});
 };
 //BA.debugLineNum = 330;BA.debugLine="If pm6 <> tempZeroRange Then";
if ((mostCurrent._pm6).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 331;BA.debugLine="If pm5 = tempZeroRange Then";
if ((mostCurrent._pm5).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 332;BA.debugLine="pm5 = (pm4 + pm6)/2";
mostCurrent._pm5 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm4))+(double)(Double.parseDouble(mostCurrent._pm6)))/(double)2);
 };
 //BA.debugLineNum = 334;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5)),(float)(Double.parseDouble(mostCurrent._pm6))});
 };
 //BA.debugLineNum = 336;BA.debugLine="If pm7 <> tempZeroRange Then";
if ((mostCurrent._pm7).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 337;BA.debugLine="If pm6 = tempZeroRange Then";
if ((mostCurrent._pm6).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 338;BA.debugLine="pm6 = (pm5 + pm7)/2";
mostCurrent._pm6 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm5))+(double)(Double.parseDouble(mostCurrent._pm7)))/(double)2);
 };
 //BA.debugLineNum = 340;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5)),(float)(Double.parseDouble(mostCurrent._pm6)),(float)(Double.parseDouble(mostCurrent._pm7))});
 };
 //BA.debugLineNum = 342;BA.debugLine="If pm8 <> tempZeroRange Then";
if ((mostCurrent._pm8).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 343;BA.debugLine="If pm7 = tempZeroRange Then";
if ((mostCurrent._pm7).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 344;BA.debugLine="pm7 = (pm6 + pm8)/2";
mostCurrent._pm7 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm6))+(double)(Double.parseDouble(mostCurrent._pm8)))/(double)2);
 };
 //BA.debugLineNum = 346;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5)),(float)(Double.parseDouble(mostCurrent._pm6)),(float)(Double.parseDouble(mostCurrent._pm7)),(float)(Double.parseDouble(mostCurrent._pm8))});
 };
 //BA.debugLineNum = 348;BA.debugLine="If pm9 <> tempZeroRange Then";
if ((mostCurrent._pm9).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 349;BA.debugLine="If pm8 = tempZeroRange Then";
if ((mostCurrent._pm8).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 350;BA.debugLine="pm8 = (pm7 + pm9)/2";
mostCurrent._pm8 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm7))+(double)(Double.parseDouble(mostCurrent._pm9)))/(double)2);
 };
 //BA.debugLineNum = 352;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5)),(float)(Double.parseDouble(mostCurrent._pm6)),(float)(Double.parseDouble(mostCurrent._pm7)),(float)(Double.parseDouble(mostCurrent._pm8)),(float)(Double.parseDouble(mostCurrent._pm9))});
 };
 //BA.debugLineNum = 354;BA.debugLine="If pm10 <> tempZeroRange Then";
if ((mostCurrent._pm10).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 355;BA.debugLine="If pm9 = tempZeroRange Then";
if ((mostCurrent._pm9).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 356;BA.debugLine="pm9 = (pm8 + pm10)/2";
mostCurrent._pm9 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm8))+(double)(Double.parseDouble(mostCurrent._pm10)))/(double)2);
 };
 //BA.debugLineNum = 358;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5)),(float)(Double.parseDouble(mostCurrent._pm6)),(float)(Double.parseDouble(mostCurrent._pm7)),(float)(Double.parseDouble(mostCurrent._pm8)),(float)(Double.parseDouble(mostCurrent._pm9)),(float)(Double.parseDouble(mostCurrent._pm10))});
 };
 //BA.debugLineNum = 360;BA.debugLine="If pm11 <> tempZeroRange Then";
if ((mostCurrent._pm11).equals(BA.NumberToString(_tempzerorange)) == false) { 
 //BA.debugLineNum = 361;BA.debugLine="If pm10 = tempZeroRange Then";
if ((mostCurrent._pm10).equals(BA.NumberToString(_tempzerorange))) { 
 //BA.debugLineNum = 362;BA.debugLine="pm10 = (pm9 + pm11)/2";
mostCurrent._pm10 = BA.NumberToString(((double)(Double.parseDouble(mostCurrent._pm9))+(double)(Double.parseDouble(mostCurrent._pm11)))/(double)2);
 };
 //BA.debugLineNum = 364;BA.debugLine="LineChart.Line_1_Data = Array As Float (am12, a";
mostCurrent._linechart.setLine_1_Data(new float[]{(float)(Double.parseDouble(mostCurrent._am12)),(float)(Double.parseDouble(mostCurrent._am1)),(float)(Double.parseDouble(mostCurrent._am2)),(float)(Double.parseDouble(mostCurrent._am3)),(float)(Double.parseDouble(mostCurrent._am4)),(float)(Double.parseDouble(mostCurrent._am5)),(float)(Double.parseDouble(mostCurrent._am6)),(float)(Double.parseDouble(mostCurrent._am7)),(float)(Double.parseDouble(mostCurrent._am8)),(float)(Double.parseDouble(mostCurrent._am9)),(float)(Double.parseDouble(mostCurrent._am10)),(float)(Double.parseDouble(mostCurrent._am11)),(float)(Double.parseDouble(mostCurrent._pm12)),(float)(Double.parseDouble(mostCurrent._pm1)),(float)(Double.parseDouble(mostCurrent._pm2)),(float)(Double.parseDouble(mostCurrent._pm3)),(float)(Double.parseDouble(mostCurrent._pm4)),(float)(Double.parseDouble(mostCurrent._pm5)),(float)(Double.parseDouble(mostCurrent._pm6)),(float)(Double.parseDouble(mostCurrent._pm7)),(float)(Double.parseDouble(mostCurrent._pm8)),(float)(Double.parseDouble(mostCurrent._pm9)),(float)(Double.parseDouble(mostCurrent._pm10)),(float)(Double.parseDouble(mostCurrent._pm11))});
 };
 //BA.debugLineNum = 367;BA.debugLine="LineChart.Line_1_PointLabelTextColor = Colors.Ye";
mostCurrent._linechart.setLine_1_PointLabelTextColor(anywheresoftware.b4a.keywords.Common.Colors.Yellow);
 //BA.debugLineNum = 368;BA.debugLine="LineChart.Line_1_PointLabelTextSize = 35.0";
mostCurrent._linechart.setLine_1_PointLabelTextSize((float) (35.0));
 //BA.debugLineNum = 369;BA.debugLine="LineChart.Line_1_LineColor = Colors.Red";
mostCurrent._linechart.setLine_1_LineColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 370;BA.debugLine="LineChart.Line_1_LineWidth = 11.0";
mostCurrent._linechart.setLine_1_LineWidth((float) (11.0));
 //BA.debugLineNum = 371;BA.debugLine="LineChart.Line_1_PointColor = Colors.Black";
mostCurrent._linechart.setLine_1_PointColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 //BA.debugLineNum = 372;BA.debugLine="LineChart.Line_1_PointSize = 25.0";
mostCurrent._linechart.setLine_1_PointSize((float) (25.0));
 //BA.debugLineNum = 373;BA.debugLine="LineChart.Line_1_PointShape = LineChart.SHAPE_RO";
mostCurrent._linechart.setLine_1_PointShape(mostCurrent._linechart.SHAPE_ROUND);
 //BA.debugLineNum = 374;BA.debugLine="LineChart.Line_1_DrawDash = False";
mostCurrent._linechart.setLine_1_DrawDash(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 375;BA.debugLine="LineChart.Line_1_DrawCubic = False";
mostCurrent._linechart.setLine_1_DrawCubic(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 382;BA.debugLine="LineChart.Line_2_Data = Array As Float (tempRigh";
mostCurrent._linechart.setLine_2_Data(new float[]{(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow)),(float)(Double.parseDouble(mostCurrent._temprightnow))});
 //BA.debugLineNum = 383;BA.debugLine="LineChart.Line_2_PointLabelTextColor = Colors.Gr";
mostCurrent._linechart.setLine_2_PointLabelTextColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 384;BA.debugLine="LineChart.Line_2_PointLabelTextSize = 30.0";
mostCurrent._linechart.setLine_2_PointLabelTextSize((float) (30.0));
 //BA.debugLineNum = 385;BA.debugLine="LineChart.Line_2_LineColor = Colors.Green";
mostCurrent._linechart.setLine_2_LineColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 386;BA.debugLine="LineChart.Line_2_LineWidth = 5.0";
mostCurrent._linechart.setLine_2_LineWidth((float) (5.0));
 //BA.debugLineNum = 387;BA.debugLine="LineChart.Line_2_PointColor = Colors.Green";
mostCurrent._linechart.setLine_2_PointColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 388;BA.debugLine="LineChart.Line_2_PointSize = 1.0";
mostCurrent._linechart.setLine_2_PointSize((float) (1.0));
 //BA.debugLineNum = 389;BA.debugLine="LineChart.Line_2_PointShape = LineChart.SHAPE_RO";
mostCurrent._linechart.setLine_2_PointShape(mostCurrent._linechart.SHAPE_ROUND);
 //BA.debugLineNum = 390;BA.debugLine="LineChart.Line_2_DrawDash = False";
mostCurrent._linechart.setLine_2_DrawDash(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 391;BA.debugLine="LineChart.Line_2_DrawCubic = False";
mostCurrent._linechart.setLine_2_DrawCubic(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 395;BA.debugLine="LineChart.NumberOfLineCharts = 2";
mostCurrent._linechart.setNumberOfLineCharts((int) (2));
 //BA.debugLineNum = 397;BA.debugLine="LineChart.DrawTheGraphs";
mostCurrent._linechart.DrawTheGraphs();
 } 
       catch (Exception e228) {
			processBA.setLastException(e228); //BA.debugLineNum = 400;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("6590086",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 //BA.debugLineNum = 401;BA.debugLine="ToastMessageShow (LastException,True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getObject()),anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 404;BA.debugLine="End Sub";
return "";
}
public static String  _temperaturehourlytimer_tick() throws Exception{
 //BA.debugLineNum = 2196;BA.debugLine="Sub TemperatureHourlyTimer_Tick";
 //BA.debugLineNum = 2197;BA.debugLine="Activity.RequestFocus";
mostCurrent._activity.RequestFocus();
 //BA.debugLineNum = 2198;BA.debugLine="btnHumidityHourly.RemoveView";
mostCurrent._btnhumidityhourly.RemoveView();
 //BA.debugLineNum = 2199;BA.debugLine="btnTempHourly.RemoveView";
mostCurrent._btntemphourly.RemoveView();
 //BA.debugLineNum = 2200;BA.debugLine="btnHumidityDaily.RemoveView";
mostCurrent._btnhumiditydaily.RemoveView();
 //BA.debugLineNum = 2201;BA.debugLine="btnTempDaily.RemoveView";
mostCurrent._btntempdaily.RemoveView();
 //BA.debugLineNum = 2202;BA.debugLine="LineChart.RemoveView";
mostCurrent._linechart.RemoveView();
 //BA.debugLineNum = 2203;BA.debugLine="tempMaxRange=0";
_tempmaxrange = (float) (0);
 //BA.debugLineNum = 2204;BA.debugLine="tempMinRange=0";
_tempminrange = (float) (0);
 //BA.debugLineNum = 2205;BA.debugLine="TemperatureHourlyCreate";
_temperaturehourlycreate();
 //BA.debugLineNum = 2206;BA.debugLine="End Sub";
return "";
}
}
