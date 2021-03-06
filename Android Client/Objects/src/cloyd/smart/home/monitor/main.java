package cloyd.smart.home.monitor;

import android.app.Activity;

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

public class main extends androidx.appcompat.app.AppCompatActivity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;
    public static boolean dontPause;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mostCurrent = this;
		if (processBA == null) {
			processBA = new BA(this.getApplicationContext(), null, null, "cloyd.smart.home.monitor", "cloyd.smart.home.monitor.main");
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
		activityBA = new BA(this, layout, processBA, "cloyd.smart.home.monitor", "cloyd.smart.home.monitor.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "cloyd.smart.home.monitor.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
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
public static anywheresoftware.b4j.objects.MqttAsyncClientWrapper _mqtt = null;
public static String _mqttuser = "";
public static String _mqttpassword = "";
public static String _mqttserveruri = "";
public static anywheresoftware.b4a.agraham.byteconverter.ByteConverter _bc = null;
public static b4a.example.callsubutils _csu = null;
public static anywheresoftware.b4a.objects.IntentWrapper _oldintent = null;
public static de.amberhome.objects.preferenceactivity.PreferenceManager _manager = null;
public static de.amberhome.objects.preferenceactivity.PreferenceScreenWrapper _screen = null;
public static String _emailaddress = "";
public static String _password = "";
public static String _authtoken = "";
public static String _userregion = "";
public static String _accountid = "";
public static String _networkid = "";
public static String _commandid = "";
public static boolean _commandcomplete = false;
public static String _camerathumbnail = "";
public static String _response = "";
public static int _previousselectedindex = 0;
public static cloyd.smart.home.monitor.cmediadata _mediametadata = null;
public static String _sideyardarmedstatus = "";
public static String _frontyardarmedstatus = "";
public static String _backyardarmedstatus = "";
public static boolean _isthereunwatchedvideo = false;
public static anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper _bmp = null;
public static String _prevcamerathumbnail = "";
public static String _sideyardbattstatus = "";
public static String _frontyardbattstatus = "";
public static String _backyardbattstatus = "";
public static int _currentpage = 0;
public static anywheresoftware.b4a.objects.collections.List _pages = null;
public static anywheresoftware.b4a.phone.Phone.PhoneWakeState _awake = null;
public static cloyd.smart.home.monitor.bitmapcreatoreffects _effects = null;
public static String _compiletimestamp = "";
public de.amberhome.objects.appcompat.ACToolbarLightWrapper _actoolbarlight1 = null;
public de.amberhome.objects.appcompat.ACActionBar _toolbarhelper = null;
public de.amberhome.objects.appcompat.ACMenuWrapper _gblacmenu = null;
public anywheresoftware.b4a.objects.B4XViewWrapper.XUI _xui = null;
public cloyd.smart.home.monitor.gauge _gaugehumidity = null;
public cloyd.smart.home.monitor.gauge _gaugetemp = null;
public cloyd.smart.home.monitor.gauge _gaugedewpoint = null;
public cloyd.smart.home.monitor.gauge _gaugeheatindex = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblcomfort = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblperception = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbllastupdate = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblping = null;
public anywheresoftware.b4a.objects.TabStripViewPager _tabstrip1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblfontawesome = null;
public cloyd.smart.home.monitor.gauge _gaugeairquality = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblairquality = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblairqualitylastupdate = null;
public anywheresoftware.b4a.objects.ScrollViewWrapper _scrollview1 = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel1 = null;
public cloyd.smart.home.monitor.gauge _gaugeairqualitybasement = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblairqualitybasement = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblairqualitylastupdatebasement = null;
public anywheresoftware.b4a.objects.PanelWrapper _panelairqualitybasement = null;
public cloyd.smart.home.monitor.gauge _gaugedewpointbasement = null;
public cloyd.smart.home.monitor.gauge _gaugeheatindexbasement = null;
public cloyd.smart.home.monitor.gauge _gaugehumiditybasement = null;
public cloyd.smart.home.monitor.gauge _gaugetempbasement = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblcomfortbasement = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbllastupdatebasement = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblperceptionbasement = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblpingbasement = null;
public anywheresoftware.b4a.objects.ScrollViewWrapper _scrollviewbasement = null;
public anywheresoftware.b4a.objects.PanelWrapper _paneltemphumiditybasement = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblstatus = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _ivsideyard = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _ivfrontyard = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _ivbackyard = null;
public anywheresoftware.b4a.objects.ScrollViewWrapper _scrollviewblink = null;
public anywheresoftware.b4a.objects.PanelWrapper _panelblink = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblsideyard = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblfrontyard = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblbackyard = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblsideyardbatt = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblsideyardtimestamp = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblsideyardwifi = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblfrontyardbatt = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblfrontyardtimestamp = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblfrontyardwifi = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblbackyardbatt = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblbackyardtimestamp = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblbackyardwifi = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblsyncmodule = null;
public cloyd.smart.home.monitor.b4xpageindicator _b4xpageindicator1 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _ivscreenshot = null;
public anywheresoftware.b4a.objects.B4XViewWrapper _lbldate = null;
public anywheresoftware.b4a.objects.B4XViewWrapper _lbldeviceinfo = null;
public anywheresoftware.b4a.objects.B4XViewWrapper _lblfileinfo = null;
public b4a.example3.customlistview _clvactivity = null;
public anywheresoftware.b4a.objects.WebViewWrapper _wvmedia = null;
public uk.co.martinpearman.b4a.webviewsettings.WebViewSettings _webviewsettings1 = null;
public cloyd.smart.home.monitor.b4xloadingindicator _b4xloadingindicator4 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblduration = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _ivwatched = null;
public cloyd.smart.home.monitor.badger _badger1 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _btnsideyardnewclip = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _btnfrontyardnewclip = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _btnbackyardnewclip = null;
public cloyd.smart.home.monitor.swiftbutton _btnrefresh = null;
public cloyd.smart.home.monitor.swiftbutton _btnsideyard = null;
public anywheresoftware.b4a.objects.B4XViewWrapper _lblmediaurl = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _ivplay = null;
public anywheresoftware.b4j.object.JavaObject _nativeme = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _btnbackyardrefresh = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _btnfrontyardrefresh = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _btnsideyardrefresh = null;
public cloyd.smart.home.monitor.b4xswitch _swarmed = null;
public cloyd.smart.home.monitor.b4xdialog _dialog = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnchart = null;
public b4a.example.dateutils _dateutils = null;
public cloyd.smart.home.monitor.chart _chart = null;
public cloyd.smart.home.monitor.smarthomemonitor _smarthomemonitor = null;
public cloyd.smart.home.monitor.notificationservice _notificationservice = null;
public cloyd.smart.home.monitor.statemanager _statemanager = null;
public cloyd.smart.home.monitor.starter _starter = null;
public cloyd.smart.home.monitor.b4xcollections _b4xcollections = null;
public cloyd.smart.home.monitor.httputils2service _httputils2service = null;
public cloyd.smart.home.monitor.xuiviewsutils _xuiviewsutils = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
vis = vis | (chart.mostCurrent != null);
return vis;}
public static class _carddata{
public boolean IsInitialized;
public anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper screenshot;
public String filedate;
public String deviceinfo;
public boolean iswatchedvisible;
public String mediaURL;
public void Initialize() {
IsInitialized = true;
screenshot = new anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper();
filedate = "";
deviceinfo = "";
iswatchedvisible = false;
mediaURL = "";
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public static class _videoinfo{
public boolean IsInitialized;
public String ThumbnailPath;
public String DateCreated;
public String Watched;
public String DeviceName;
public String VideoID;
public byte[] ThumbnailBLOB;
public void Initialize() {
IsInitialized = true;
ThumbnailPath = "";
DateCreated = "";
Watched = "";
DeviceName = "";
VideoID = "";
ThumbnailBLOB = new byte[0];
;
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public static void  _activity_create(boolean _firsttime) throws Exception{
ResumableSub_Activity_Create rsub = new ResumableSub_Activity_Create(null,_firsttime);
rsub.resume(processBA, null);
}
public static class ResumableSub_Activity_Create extends BA.ResumableSub {
public ResumableSub_Activity_Create(cloyd.smart.home.monitor.main parent,boolean _firsttime) {
this.parent = parent;
this._firsttime = _firsttime;
}
cloyd.smart.home.monitor.main parent;
boolean _firsttime;
anywheresoftware.b4a.objects.collections.Map _compiledata = null;
anywheresoftware.b4a.objects.IntentWrapper _icrash = null;
anywheresoftware.b4a.objects.LabelWrapper _lbl = null;
anywheresoftware.b4a.objects.ConcreteViewWrapper _v = null;
anywheresoftware.b4a.objects.drawable.BitmapDrawable _bd = null;
anywheresoftware.b4a.objects.CSBuilder _cs = null;
anywheresoftware.b4j.object.JavaObject _jo = null;
anywheresoftware.b4a.object.XmlLayoutBuilder _xl = null;
String _strhumidityaddvalue = "";
anywheresoftware.b4a.objects.B4XViewWrapper _lblbadge = null;
int _width = 0;
int _height = 0;
anywheresoftware.b4a.objects.PanelWrapper _p = null;
anywheresoftware.b4a.keywords.Common.ResumableSubWrapper _rs = null;
Object _result = null;
anywheresoftware.b4a.BA.IterableList group39;
int index39;
int groupLen39;
anywheresoftware.b4a.BA.IterableList group44;
int index44;
int groupLen44;
anywheresoftware.b4a.BA.IterableList group111;
int index111;
int groupLen111;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
try {

        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 145;BA.debugLine="Try";
if (true) break;

case 1:
//try
this.state = 51;
this.catchState = 50;
this.state = 3;
if (true) break;

case 3:
//C
this.state = 4;
this.catchState = 50;
 //BA.debugLineNum = 146;BA.debugLine="nativeMe.InitializeContext";
parent.mostCurrent._nativeme.InitializeContext(processBA);
 //BA.debugLineNum = 147;BA.debugLine="nativeMe.RunMethod(\"setDefaultUncaughtExceptionH";
parent.mostCurrent._nativeme.RunMethod("setDefaultUncaughtExceptionHandler",(Object[])(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 149;BA.debugLine="If FirstTime Then";
if (true) break;

case 4:
//if
this.state = 13;
if (_firsttime) { 
this.state = 6;
}if (true) break;

case 6:
//C
this.state = 7;
 //BA.debugLineNum = 150;BA.debugLine="CreatePreferenceScreen";
_createpreferencescreen();
 //BA.debugLineNum = 151;BA.debugLine="If manager.GetAll.Size = 0 Then SetDefaults";
if (true) break;

case 7:
//if
this.state = 12;
if (parent._manager.GetAll().getSize()==0) { 
this.state = 9;
;}if (true) break;

case 9:
//C
this.state = 12;
_setdefaults();
if (true) break;

case 12:
//C
this.state = 13;
;
 //BA.debugLineNum = 153;BA.debugLine="StartService(SmartHomeMonitor)";
anywheresoftware.b4a.keywords.Common.StartService(processBA,(Object)(parent.mostCurrent._smarthomemonitor.getObject()));
 //BA.debugLineNum = 154;BA.debugLine="csu.Initialize";
parent._csu._initialize(processBA);
 //BA.debugLineNum = 155;BA.debugLine="MQTT_Connect";
_mqtt_connect();
 //BA.debugLineNum = 157;BA.debugLine="Dim compiledata As Map";
_compiledata = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 158;BA.debugLine="compiledata = File.ReadMap(File.DirAssets, \"com";
_compiledata = anywheresoftware.b4a.keywords.Common.File.ReadMap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"compiletime.txt");
 //BA.debugLineNum = 159;BA.debugLine="Log($\"autoversion: ${compiledata.Get(\"autoversi";
anywheresoftware.b4a.keywords.Common.LogImpl("3131087",("autoversion: "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",_compiledata.Get((Object)("autoversion")))+""),0);
 //BA.debugLineNum = 160;BA.debugLine="Log($\"compilation time: $DateTime{compiledata.G";
anywheresoftware.b4a.keywords.Common.LogImpl("3131088",("compilation time: "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("datetime",_compiledata.Get((Object)("time")))+""),0);
 //BA.debugLineNum = 161;BA.debugLine="compileTimeStamp = compiledata.Get(\"time\")";
parent._compiletimestamp = BA.ObjectToString(_compiledata.Get((Object)("time")));
 if (true) break;

case 13:
//C
this.state = 14;
;
 //BA.debugLineNum = 163;BA.debugLine="Activity.LoadLayout(\"Main\")";
parent.mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 //BA.debugLineNum = 165;BA.debugLine="dialog.Initialize(Activity)";
parent.mostCurrent._dialog._initialize /*String*/ (mostCurrent.activityBA,(anywheresoftware.b4a.objects.B4XViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.B4XViewWrapper(), (java.lang.Object)(parent.mostCurrent._activity.getObject())));
 //BA.debugLineNum = 166;BA.debugLine="dialog.Title = \"Smart Home Monitor\"";
parent.mostCurrent._dialog._title /*Object*/  = (Object)("Smart Home Monitor");
 //BA.debugLineNum = 168;BA.debugLine="Dim iCrash As Intent";
_icrash = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 169;BA.debugLine="iCrash = Activity.GetStartingIntent";
_icrash = parent.mostCurrent._activity.GetStartingIntent();
 //BA.debugLineNum = 170;BA.debugLine="If iCrash.HasExtra(\"Crash\") Then";
if (true) break;

case 14:
//if
this.state = 17;
if (_icrash.HasExtra("Crash")) { 
this.state = 16;
}if (true) break;

case 16:
//C
this.state = 17;
 //BA.debugLineNum = 171;BA.debugLine="Log(\"After crash: \" & iCrash.GetExtra(\"Crash\"))";
anywheresoftware.b4a.keywords.Common.LogImpl("3131099","After crash: "+BA.ObjectToString(_icrash.GetExtra("Crash")),0);
 if (true) break;

case 17:
//C
this.state = 18;
;
 //BA.debugLineNum = 174;BA.debugLine="TabStrip1.LoadLayout(\"1ScrollView\", \"LIVING AREA";
parent.mostCurrent._tabstrip1.LoadLayout("1ScrollView",BA.ObjectToCharSequence("LIVING AREA"+anywheresoftware.b4a.keywords.Common.CRLF+"Temp & Humidity  "+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (0xf2c7)))));
 //BA.debugLineNum = 175;BA.debugLine="ScrollView1.Panel.LoadLayout(\"1\")";
parent.mostCurrent._scrollview1.getPanel().LoadLayout("1",mostCurrent.activityBA);
 //BA.debugLineNum = 176;BA.debugLine="Panel1.Height = ScrollView1.Height 'Panel1.Heigh";
parent.mostCurrent._panel1.setHeight(parent.mostCurrent._scrollview1.getHeight());
 //BA.debugLineNum = 177;BA.debugLine="ScrollView1.Panel.Height = Panel1.Height";
parent.mostCurrent._scrollview1.getPanel().setHeight(parent.mostCurrent._panel1.getHeight());
 //BA.debugLineNum = 178;BA.debugLine="TabStrip1.LoadLayout(\"2\", \"LIVING AREA\" & CRLF &";
parent.mostCurrent._tabstrip1.LoadLayout("2",BA.ObjectToCharSequence("LIVING AREA"+anywheresoftware.b4a.keywords.Common.CRLF+"Air Quality (CO)  "+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (0xf289)))));
 //BA.debugLineNum = 179;BA.debugLine="TabStrip1.LoadLayout(\"ScrollViewBasement\", \"BASE";
parent.mostCurrent._tabstrip1.LoadLayout("ScrollViewBasement",BA.ObjectToCharSequence("BASEMENT"+anywheresoftware.b4a.keywords.Common.CRLF+"Temp & Humidity  "+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (0xf2c7)))));
 //BA.debugLineNum = 180;BA.debugLine="ScrollViewBasement.Panel.LoadLayout(\"TempHumidit";
parent.mostCurrent._scrollviewbasement.getPanel().LoadLayout("TempHumidityBasement",mostCurrent.activityBA);
 //BA.debugLineNum = 181;BA.debugLine="PanelTempHumidityBasement.Height = ScrollViewBas";
parent.mostCurrent._paneltemphumiditybasement.setHeight(parent.mostCurrent._scrollviewbasement.getHeight());
 //BA.debugLineNum = 182;BA.debugLine="ScrollViewBasement.Panel.Height = PanelTempHumid";
parent.mostCurrent._scrollviewbasement.getPanel().setHeight(parent.mostCurrent._paneltemphumiditybasement.getHeight());
 //BA.debugLineNum = 183;BA.debugLine="TabStrip1.LoadLayout(\"AirQualityBasement\", \"BASE";
parent.mostCurrent._tabstrip1.LoadLayout("AirQualityBasement",BA.ObjectToCharSequence("BASEMENT"+anywheresoftware.b4a.keywords.Common.CRLF+"Air Quality (CO)  "+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (0xf289)))));
 //BA.debugLineNum = 184;BA.debugLine="TabStrip1.LoadLayout(\"blinkscrollview\", \"BLINK C";
parent.mostCurrent._tabstrip1.LoadLayout("blinkscrollview",BA.ObjectToCharSequence("BLINK CAMERAS"+anywheresoftware.b4a.keywords.Common.CRLF+"Live View  "+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (0xf030)))));
 //BA.debugLineNum = 185;BA.debugLine="ScrollViewBlink.Panel.LoadLayout(\"blink\")";
parent.mostCurrent._scrollviewblink.getPanel().LoadLayout("blink",mostCurrent.activityBA);
 //BA.debugLineNum = 187;BA.debugLine="ScrollViewBlink.panel.height = 910dip";
parent.mostCurrent._scrollviewblink.getPanel().setHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (910)));
 //BA.debugLineNum = 188;BA.debugLine="panelBlink.Height = 910dip";
parent.mostCurrent._panelblink.setHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (910)));
 //BA.debugLineNum = 189;BA.debugLine="TabStrip1.LoadLayout(\"blinkactivity\", \"BLINK CAM";
parent.mostCurrent._tabstrip1.LoadLayout("blinkactivity",BA.ObjectToCharSequence("BLINK CAMERAS"+anywheresoftware.b4a.keywords.Common.CRLF+"Events  "+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (0xf03d)))));
 //BA.debugLineNum = 191;BA.debugLine="For Each lbl As Label In GetAllTabLabels(TabStri";
if (true) break;

case 18:
//for
this.state = 21;
_lbl = new anywheresoftware.b4a.objects.LabelWrapper();
group39 = _getalltablabels(parent.mostCurrent._tabstrip1);
index39 = 0;
groupLen39 = group39.getSize();
this.state = 52;
if (true) break;

case 52:
//C
this.state = 21;
if (index39 < groupLen39) {
this.state = 20;
_lbl = (anywheresoftware.b4a.objects.LabelWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.LabelWrapper(), (android.widget.TextView)(group39.Get(index39)));}
if (true) break;

case 53:
//C
this.state = 52;
index39++;
if (true) break;

case 20:
//C
this.state = 53;
 //BA.debugLineNum = 193;BA.debugLine="lbl.SingleLine = False";
_lbl.setSingleLine(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 195;BA.debugLine="lbl.Typeface = Typeface.FONTAWESOME";
_lbl.setTypeface(anywheresoftware.b4a.keywords.Common.Typeface.getFONTAWESOME());
 //BA.debugLineNum = 197;BA.debugLine="lbl.Padding = Array As Int(0, 0, 0, 0)";
_lbl.setPadding(new int[]{(int) (0),(int) (0),(int) (0),(int) (0)});
 if (true) break;
if (true) break;
;
 //BA.debugLineNum = 201;BA.debugLine="For Each v As View In GetAllTabLabels(TabStrip1)";

case 21:
//for
this.state = 24;
_v = new anywheresoftware.b4a.objects.ConcreteViewWrapper();
group44 = _getalltablabels(parent.mostCurrent._tabstrip1);
index44 = 0;
groupLen44 = group44.getSize();
this.state = 54;
if (true) break;

case 54:
//C
this.state = 24;
if (index44 < groupLen44) {
this.state = 23;
_v = (anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(group44.Get(index44)));}
if (true) break;

case 55:
//C
this.state = 54;
index44++;
if (true) break;

case 23:
//C
this.state = 55;
 //BA.debugLineNum = 203;BA.debugLine="v.Width = 33%x";
_v.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (33),mostCurrent.activityBA));
 if (true) break;
if (true) break;

case 24:
//C
this.state = 25;
;
 //BA.debugLineNum = 206;BA.debugLine="Dim bd As BitmapDrawable";
_bd = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
 //BA.debugLineNum = 207;BA.debugLine="bd.Initialize(LoadBitmapResize(File.DirAssets, \"";
_bd.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmapResize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"0.png",anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (32)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (32)),anywheresoftware.b4a.keywords.Common.True).getObject()));
 //BA.debugLineNum = 208;BA.debugLine="ACToolBarLight1.NavigationIconDrawable = bd";
parent.mostCurrent._actoolbarlight1.setNavigationIconDrawable((android.graphics.drawable.Drawable)(_bd.getObject()));
 //BA.debugLineNum = 209;BA.debugLine="ToolbarHelper.Initialize";
parent.mostCurrent._toolbarhelper.Initialize(mostCurrent.activityBA);
 //BA.debugLineNum = 210;BA.debugLine="Dim cs As CSBuilder";
_cs = new anywheresoftware.b4a.objects.CSBuilder();
 //BA.debugLineNum = 211;BA.debugLine="ToolbarHelper.Title= cs.Initialize.Size(22).Appe";
parent.mostCurrent._toolbarhelper.setTitle(BA.ObjectToCharSequence(_cs.Initialize().Size((int) (22)).Append(BA.ObjectToCharSequence("Smart Home Monitor")).PopAll().getObject()));
 //BA.debugLineNum = 212;BA.debugLine="ToolbarHelper.subTitle = \"\"";
parent.mostCurrent._toolbarhelper.setSubtitle(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 213;BA.debugLine="ToolbarHelper.ShowUpIndicator = False 'set to tr";
parent.mostCurrent._toolbarhelper.setShowUpIndicator(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 214;BA.debugLine="ACToolBarLight1.InitMenuListener";
parent.mostCurrent._actoolbarlight1.InitMenuListener();
 //BA.debugLineNum = 215;BA.debugLine="Dim jo As JavaObject = ACToolBarLight1";
_jo = new anywheresoftware.b4j.object.JavaObject();
_jo = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(parent.mostCurrent._actoolbarlight1.getObject()));
 //BA.debugLineNum = 216;BA.debugLine="Dim xl As XmlLayoutBuilder";
_xl = new anywheresoftware.b4a.object.XmlLayoutBuilder();
 //BA.debugLineNum = 217;BA.debugLine="jo.RunMethod(\"setPopupTheme\", Array(xl.GetResour";
_jo.RunMethod("setPopupTheme",new Object[]{(Object)(_xl.GetResourceId("style","ToolbarMenu"))});
 //BA.debugLineNum = 219;BA.debugLine="GaugeHumidity.SetRanges(Array(GaugeHumidity.Crea";
parent.mostCurrent._gaugehumidity._setranges /*String*/ (anywheresoftware.b4a.keywords.Common.ArrayToList(new Object[]{(Object)(parent.mostCurrent._gaugehumidity._createrange /*cloyd.smart.home.monitor.gauge._gaugerange*/ ((float) (0),(float) (70),parent.mostCurrent._xui.Color_RGB((int) (100),(int) (221),(int) (23)))),(Object)(parent.mostCurrent._gaugehumidity._createrange /*cloyd.smart.home.monitor.gauge._gaugerange*/ ((float) (70),(float) (80),parent.mostCurrent._xui.Color_Yellow)),(Object)(parent.mostCurrent._gaugehumidity._createrange /*cloyd.smart.home.monitor.gauge._gaugerange*/ ((float) (80),(float) (100),parent.mostCurrent._xui.Color_Red))}));
 //BA.debugLineNum = 222;BA.debugLine="GaugeTemp.SetRanges(Array(GaugeTemp.CreateRange(";
parent.mostCurrent._gaugetemp._setranges /*String*/ (anywheresoftware.b4a.keywords.Common.ArrayToList(new Object[]{(Object)(parent.mostCurrent._gaugetemp._createrange /*cloyd.smart.home.monitor.gauge._gaugerange*/ ((float) (0),(float) (75),parent.mostCurrent._xui.Color_RGB((int) (100),(int) (221),(int) (23)))),(Object)(parent.mostCurrent._gaugetemp._createrange /*cloyd.smart.home.monitor.gauge._gaugerange*/ ((float) (75),(float) (90),parent.mostCurrent._xui.Color_Yellow)),(Object)(parent.mostCurrent._gaugetemp._createrange /*cloyd.smart.home.monitor.gauge._gaugerange*/ ((float) (90),(float) (100),parent.mostCurrent._xui.Color_Red))}));
 //BA.debugLineNum = 225;BA.debugLine="GaugeHeatIndex.SetRanges(Array(GaugeHeatIndex.Cr";
parent.mostCurrent._gaugeheatindex._setranges /*String*/ (anywheresoftware.b4a.keywords.Common.ArrayToList(new Object[]{(Object)(parent.mostCurrent._gaugeheatindex._createrange /*cloyd.smart.home.monitor.gauge._gaugerange*/ ((float) (0),(float) (75),parent.mostCurrent._xui.Color_RGB((int) (100),(int) (221),(int) (23)))),(Object)(parent.mostCurrent._gaugeheatindex._createrange /*cloyd.smart.home.monitor.gauge._gaugerange*/ ((float) (75),(float) (90),parent.mostCurrent._xui.Color_Yellow)),(Object)(parent.mostCurrent._gaugeheatindex._createrange /*cloyd.smart.home.monitor.gauge._gaugerange*/ ((float) (90),(float) (100),parent.mostCurrent._xui.Color_Red))}));
 //BA.debugLineNum = 230;BA.debugLine="GaugeDewPoint.SetRanges(Array(GaugeDewPoint.Crea";
parent.mostCurrent._gaugedewpoint._setranges /*String*/ (anywheresoftware.b4a.keywords.Common.ArrayToList(new Object[]{(Object)(parent.mostCurrent._gaugedewpoint._createrange /*cloyd.smart.home.monitor.gauge._gaugerange*/ ((float) (0),(float) (60.8),parent.mostCurrent._xui.Color_RGB((int) (100),(int) (221),(int) (23)))),(Object)(parent.mostCurrent._gaugedewpoint._createrange /*cloyd.smart.home.monitor.gauge._gaugerange*/ ((float) (60.8),(float) (64.4),parent.mostCurrent._xui.Color_RGB((int) (100),(int) (240),(int) (23)))),(Object)(parent.mostCurrent._gaugedewpoint._createrange /*cloyd.smart.home.monitor.gauge._gaugerange*/ ((float) (64.4),(float) (78.8),parent.mostCurrent._xui.Color_Yellow)),(Object)(parent.mostCurrent._gaugedewpoint._createrange /*cloyd.smart.home.monitor.gauge._gaugerange*/ ((float) (78.8),(float) (100),parent.mostCurrent._xui.Color_Red))}));
 //BA.debugLineNum = 236;BA.debugLine="GaugeAirQuality.SetRanges(Array(GaugeAirQuality.";
parent.mostCurrent._gaugeairquality._setranges /*String*/ (anywheresoftware.b4a.keywords.Common.ArrayToList(new Object[]{(Object)(parent.mostCurrent._gaugeairquality._createrange /*cloyd.smart.home.monitor.gauge._gaugerange*/ ((float) (0),(float) (10),parent.mostCurrent._xui.Color_RGB((int) (100),(int) (221),(int) (23)))),(Object)(parent.mostCurrent._gaugeairquality._createrange /*cloyd.smart.home.monitor.gauge._gaugerange*/ ((float) (10),(float) (40),parent.mostCurrent._xui.Color_RGB((int) (100),(int) (240),(int) (23)))),(Object)(parent.mostCurrent._gaugeairquality._createrange /*cloyd.smart.home.monitor.gauge._gaugerange*/ ((float) (40),(float) (90),parent.mostCurrent._xui.Color_Yellow)),(Object)(parent.mostCurrent._gaugeairquality._createrange /*cloyd.smart.home.monitor.gauge._gaugerange*/ ((float) (90),(float) (100),parent.mostCurrent._xui.Color_Red))}));
 //BA.debugLineNum = 240;BA.debugLine="GaugeAirQuality.CurrentValue=0";
parent.mostCurrent._gaugeairquality._setcurrentvalue /*float*/ ((float) (0));
 //BA.debugLineNum = 242;BA.debugLine="GaugeHumidityBasement.SetRanges(Array(GaugeHumid";
parent.mostCurrent._gaugehumiditybasement._setranges /*String*/ (anywheresoftware.b4a.keywords.Common.ArrayToList(new Object[]{(Object)(parent.mostCurrent._gaugehumiditybasement._createrange /*cloyd.smart.home.monitor.gauge._gaugerange*/ ((float) (0),(float) (70),parent.mostCurrent._xui.Color_RGB((int) (100),(int) (221),(int) (23)))),(Object)(parent.mostCurrent._gaugehumiditybasement._createrange /*cloyd.smart.home.monitor.gauge._gaugerange*/ ((float) (70),(float) (80),parent.mostCurrent._xui.Color_Yellow)),(Object)(parent.mostCurrent._gaugehumiditybasement._createrange /*cloyd.smart.home.monitor.gauge._gaugerange*/ ((float) (80),(float) (100),parent.mostCurrent._xui.Color_Red))}));
 //BA.debugLineNum = 245;BA.debugLine="GaugeTempBasement.SetRanges(Array(GaugeTempBasem";
parent.mostCurrent._gaugetempbasement._setranges /*String*/ (anywheresoftware.b4a.keywords.Common.ArrayToList(new Object[]{(Object)(parent.mostCurrent._gaugetempbasement._createrange /*cloyd.smart.home.monitor.gauge._gaugerange*/ ((float) (0),(float) (75),parent.mostCurrent._xui.Color_RGB((int) (100),(int) (221),(int) (23)))),(Object)(parent.mostCurrent._gaugetempbasement._createrange /*cloyd.smart.home.monitor.gauge._gaugerange*/ ((float) (75),(float) (90),parent.mostCurrent._xui.Color_Yellow)),(Object)(parent.mostCurrent._gaugetempbasement._createrange /*cloyd.smart.home.monitor.gauge._gaugerange*/ ((float) (90),(float) (100),parent.mostCurrent._xui.Color_Red))}));
 //BA.debugLineNum = 248;BA.debugLine="GaugeHeatIndexBasement.SetRanges(Array(GaugeHeat";
parent.mostCurrent._gaugeheatindexbasement._setranges /*String*/ (anywheresoftware.b4a.keywords.Common.ArrayToList(new Object[]{(Object)(parent.mostCurrent._gaugeheatindexbasement._createrange /*cloyd.smart.home.monitor.gauge._gaugerange*/ ((float) (0),(float) (75),parent.mostCurrent._xui.Color_RGB((int) (100),(int) (221),(int) (23)))),(Object)(parent.mostCurrent._gaugeheatindexbasement._createrange /*cloyd.smart.home.monitor.gauge._gaugerange*/ ((float) (75),(float) (90),parent.mostCurrent._xui.Color_Yellow)),(Object)(parent.mostCurrent._gaugeheatindexbasement._createrange /*cloyd.smart.home.monitor.gauge._gaugerange*/ ((float) (90),(float) (100),parent.mostCurrent._xui.Color_Red))}));
 //BA.debugLineNum = 253;BA.debugLine="GaugeDewPointBasement.SetRanges(Array(GaugeDewPo";
parent.mostCurrent._gaugedewpointbasement._setranges /*String*/ (anywheresoftware.b4a.keywords.Common.ArrayToList(new Object[]{(Object)(parent.mostCurrent._gaugedewpointbasement._createrange /*cloyd.smart.home.monitor.gauge._gaugerange*/ ((float) (0),(float) (60.8),parent.mostCurrent._xui.Color_RGB((int) (100),(int) (221),(int) (23)))),(Object)(parent.mostCurrent._gaugedewpointbasement._createrange /*cloyd.smart.home.monitor.gauge._gaugerange*/ ((float) (60.8),(float) (64.4),parent.mostCurrent._xui.Color_RGB((int) (100),(int) (240),(int) (23)))),(Object)(parent.mostCurrent._gaugedewpointbasement._createrange /*cloyd.smart.home.monitor.gauge._gaugerange*/ ((float) (64.4),(float) (78.8),parent.mostCurrent._xui.Color_Yellow)),(Object)(parent.mostCurrent._gaugedewpointbasement._createrange /*cloyd.smart.home.monitor.gauge._gaugerange*/ ((float) (78.8),(float) (100),parent.mostCurrent._xui.Color_Red))}));
 //BA.debugLineNum = 259;BA.debugLine="GaugeAirQualityBasement.SetRanges(Array(GaugeAir";
parent.mostCurrent._gaugeairqualitybasement._setranges /*String*/ (anywheresoftware.b4a.keywords.Common.ArrayToList(new Object[]{(Object)(parent.mostCurrent._gaugeairqualitybasement._createrange /*cloyd.smart.home.monitor.gauge._gaugerange*/ ((float) (0),(float) (10),parent.mostCurrent._xui.Color_RGB((int) (100),(int) (221),(int) (23)))),(Object)(parent.mostCurrent._gaugeairqualitybasement._createrange /*cloyd.smart.home.monitor.gauge._gaugerange*/ ((float) (10),(float) (40),parent.mostCurrent._xui.Color_RGB((int) (100),(int) (240),(int) (23)))),(Object)(parent.mostCurrent._gaugeairqualitybasement._createrange /*cloyd.smart.home.monitor.gauge._gaugerange*/ ((float) (40),(float) (90),parent.mostCurrent._xui.Color_Yellow)),(Object)(parent.mostCurrent._gaugeairqualitybasement._createrange /*cloyd.smart.home.monitor.gauge._gaugerange*/ ((float) (90),(float) (100),parent.mostCurrent._xui.Color_Red))}));
 //BA.debugLineNum = 263;BA.debugLine="GaugeAirQualityBasement.CurrentValue=0";
parent.mostCurrent._gaugeairqualitybasement._setcurrentvalue /*float*/ ((float) (0));
 //BA.debugLineNum = 265;BA.debugLine="Dim cs As CSBuilder";
_cs = new anywheresoftware.b4a.objects.CSBuilder();
 //BA.debugLineNum = 266;BA.debugLine="cs.Initialize";
_cs.Initialize();
 //BA.debugLineNum = 267;BA.debugLine="lblPerception.Text = cs.Initialize.Bold.Append(\"";
parent.mostCurrent._lblperception.setText(BA.ObjectToCharSequence(_cs.Initialize().Bold().Append(BA.ObjectToCharSequence("Human Perception: ")).Pop().Append(BA.ObjectToCharSequence("Waiting for data...")).PopAll().getObject()));
 //BA.debugLineNum = 268;BA.debugLine="lblComfort.Text = cs.Initialize.Bold.Append(\"The";
parent.mostCurrent._lblcomfort.setText(BA.ObjectToCharSequence(_cs.Initialize().Bold().Append(BA.ObjectToCharSequence("Thermal Comfort: ")).Pop().Append(BA.ObjectToCharSequence("Waiting for data...")).PopAll().getObject()));
 //BA.debugLineNum = 269;BA.debugLine="DateTime.DateFormat = \"MMMM d, h:mm:ss a\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("MMMM d, h:mm:ss a");
 //BA.debugLineNum = 270;BA.debugLine="lblLastUpdate.Text = cs.Initialize.Bold.Append(\"";
parent.mostCurrent._lbllastupdate.setText(BA.ObjectToCharSequence(_cs.Initialize().Bold().Append(BA.ObjectToCharSequence("Last update: ")).Pop().Append(BA.ObjectToCharSequence("")).PopAll().getObject()));
 //BA.debugLineNum = 271;BA.debugLine="lblPing.Visible = False";
parent.mostCurrent._lblping.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 272;BA.debugLine="GaugeAirQuality.CurrentValue = 0";
parent.mostCurrent._gaugeairquality._setcurrentvalue /*float*/ ((float) (0));
 //BA.debugLineNum = 273;BA.debugLine="GaugeAirQualityBasement.CurrentValue = 0";
parent.mostCurrent._gaugeairqualitybasement._setcurrentvalue /*float*/ ((float) (0));
 //BA.debugLineNum = 274;BA.debugLine="lblAirQuality.Text = cs.Initialize.Bold.Append(\"";
parent.mostCurrent._lblairquality.setText(BA.ObjectToCharSequence(_cs.Initialize().Bold().Append(BA.ObjectToCharSequence("Air Quality: ")).Pop().Append(BA.ObjectToCharSequence("Waiting for data...")).PopAll().getObject()));
 //BA.debugLineNum = 275;BA.debugLine="lblAirQualityLastUpdate.Text = cs.Initialize.Bol";
parent.mostCurrent._lblairqualitylastupdate.setText(BA.ObjectToCharSequence(_cs.Initialize().Bold().Append(BA.ObjectToCharSequence("Last update: ")).PopAll().getObject()));
 //BA.debugLineNum = 277;BA.debugLine="lblPerceptionBasement.Text = cs.Initialize.Bold.";
parent.mostCurrent._lblperceptionbasement.setText(BA.ObjectToCharSequence(_cs.Initialize().Bold().Append(BA.ObjectToCharSequence("Human Perception: ")).Pop().Append(BA.ObjectToCharSequence("Waiting for data...")).PopAll().getObject()));
 //BA.debugLineNum = 278;BA.debugLine="lblComfortBasement.Text = cs.Initialize.Bold.App";
parent.mostCurrent._lblcomfortbasement.setText(BA.ObjectToCharSequence(_cs.Initialize().Bold().Append(BA.ObjectToCharSequence("Thermal Comfort: ")).Pop().Append(BA.ObjectToCharSequence("Waiting for data...")).PopAll().getObject()));
 //BA.debugLineNum = 279;BA.debugLine="DateTime.DateFormat = \"MMMM d, h:mm:ss a\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("MMMM d, h:mm:ss a");
 //BA.debugLineNum = 280;BA.debugLine="lblLastUpdateBasement.Text = cs.Initialize.Bold.";
parent.mostCurrent._lbllastupdatebasement.setText(BA.ObjectToCharSequence(_cs.Initialize().Bold().Append(BA.ObjectToCharSequence("Last update: ")).Pop().Append(BA.ObjectToCharSequence("")).PopAll().getObject()));
 //BA.debugLineNum = 281;BA.debugLine="lblPingBasement.Visible = False";
parent.mostCurrent._lblpingbasement.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 282;BA.debugLine="lblAirQualityBasement.Text = cs.Initialize.Bold.";
parent.mostCurrent._lblairqualitybasement.setText(BA.ObjectToCharSequence(_cs.Initialize().Bold().Append(BA.ObjectToCharSequence("Air Quality: ")).Pop().Append(BA.ObjectToCharSequence("Waiting for data...")).PopAll().getObject()));
 //BA.debugLineNum = 283;BA.debugLine="lblAirQualityLastUpdateBasement.Text = cs.Initia";
parent.mostCurrent._lblairqualitylastupdatebasement.setText(BA.ObjectToCharSequence(_cs.Initialize().Bold().Append(BA.ObjectToCharSequence("Last update: ")).PopAll().getObject()));
 //BA.debugLineNum = 285;BA.debugLine="If MQTT.IsInitialized And MQTT.Connected  Then";
if (true) break;

case 25:
//if
this.state = 32;
if (parent._mqtt.IsInitialized() && parent._mqtt.getConnected()) { 
this.state = 27;
}if (true) break;

case 27:
//C
this.state = 28;
 //BA.debugLineNum = 286;BA.debugLine="MQTT.Publish(\"TempHumid\", bc.StringToBytes(\"Rea";
parent._mqtt.Publish("TempHumid",parent._bc.StringToBytes("Read weather","utf8"));
 //BA.debugLineNum = 288;BA.debugLine="Dim strHumidityAddValue As String = StateManage";
_strhumidityaddvalue = parent.mostCurrent._statemanager._getsetting /*String*/ (mostCurrent.activityBA,"HumidityAddValue");
 //BA.debugLineNum = 289;BA.debugLine="If strHumidityAddValue = \"\" Then";
if (true) break;

case 28:
//if
this.state = 31;
if ((_strhumidityaddvalue).equals("")) { 
this.state = 30;
}if (true) break;

case 30:
//C
this.state = 31;
 //BA.debugLineNum = 290;BA.debugLine="strHumidityAddValue = \"0\"";
_strhumidityaddvalue = "0";
 if (true) break;

case 31:
//C
this.state = 32;
;
 //BA.debugLineNum = 292;BA.debugLine="MQTT.Publish(\"HumidityAddValue\", bc.StringToByt";
parent._mqtt.Publish("HumidityAddValue",parent._bc.StringToBytes(_strhumidityaddvalue,"utf8"));
 if (true) break;

case 32:
//C
this.state = 33;
;
 //BA.debugLineNum = 295;BA.debugLine="SetTextShadow(lblSideYardBatt)";
_settextshadow(parent.mostCurrent._lblsideyardbatt);
 //BA.debugLineNum = 296;BA.debugLine="SetTextShadow(lblSideYardTimestamp)";
_settextshadow(parent.mostCurrent._lblsideyardtimestamp);
 //BA.debugLineNum = 297;BA.debugLine="SetTextShadow(lblSideYardWifi)";
_settextshadow(parent.mostCurrent._lblsideyardwifi);
 //BA.debugLineNum = 298;BA.debugLine="SetTextShadow(lblFrontYardBatt)";
_settextshadow(parent.mostCurrent._lblfrontyardbatt);
 //BA.debugLineNum = 299;BA.debugLine="SetTextShadow(lblFrontYardTimestamp)";
_settextshadow(parent.mostCurrent._lblfrontyardtimestamp);
 //BA.debugLineNum = 300;BA.debugLine="SetTextShadow(lblFrontYardWiFi)";
_settextshadow(parent.mostCurrent._lblfrontyardwifi);
 //BA.debugLineNum = 301;BA.debugLine="SetTextShadow(lblBackyardBatt)";
_settextshadow(parent.mostCurrent._lblbackyardbatt);
 //BA.debugLineNum = 302;BA.debugLine="SetTextShadow(lblBackyardTimestamp)";
_settextshadow(parent.mostCurrent._lblbackyardtimestamp);
 //BA.debugLineNum = 303;BA.debugLine="SetTextShadow(lblBackyardWiFi)";
_settextshadow(parent.mostCurrent._lblbackyardwifi);
 //BA.debugLineNum = 304;BA.debugLine="SetTextShadow(lblDuration)";
_settextshadow(parent.mostCurrent._lblduration);
 //BA.debugLineNum = 305;BA.debugLine="SetTextShadow(lblSideYard)";
_settextshadow(parent.mostCurrent._lblsideyard);
 //BA.debugLineNum = 306;BA.debugLine="SetTextShadow(lblFrontYard)";
_settextshadow(parent.mostCurrent._lblfrontyard);
 //BA.debugLineNum = 307;BA.debugLine="SetTextShadow(lblBackyard)";
_settextshadow(parent.mostCurrent._lblbackyard);
 //BA.debugLineNum = 309;BA.debugLine="badger1.Initialize";
parent.mostCurrent._badger1._initialize /*String*/ (mostCurrent.activityBA);
 //BA.debugLineNum = 311;BA.debugLine="For Each lblBadge As B4XView In GetAllTabLabelsF";
if (true) break;

case 33:
//for
this.state = 44;
_lblbadge = new anywheresoftware.b4a.objects.B4XViewWrapper();
group111 = _getalltablabelsforbadge(parent.mostCurrent._tabstrip1);
index111 = 0;
groupLen111 = group111.getSize();
this.state = 56;
if (true) break;

case 56:
//C
this.state = 44;
if (index111 < groupLen111) {
this.state = 35;
_lblbadge = (anywheresoftware.b4a.objects.B4XViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.B4XViewWrapper(), (java.lang.Object)(group111.Get(index111)));}
if (true) break;

case 57:
//C
this.state = 56;
index111++;
if (true) break;

case 35:
//C
this.state = 36;
 //BA.debugLineNum = 312;BA.debugLine="Dim Width, Height As Int";
_width = 0;
_height = 0;
 //BA.debugLineNum = 313;BA.debugLine="Dim jo As JavaObject = lblBadge";
_jo = new anywheresoftware.b4j.object.JavaObject();
_jo = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(_lblbadge.getObject()));
 //BA.debugLineNum = 314;BA.debugLine="Do While True";
if (true) break;

case 36:
//do while
this.state = 43;
while (anywheresoftware.b4a.keywords.Common.True) {
this.state = 38;
if (true) break;
}
if (true) break;

case 38:
//C
this.state = 39;
 //BA.debugLineNum = 315;BA.debugLine="Width = jo.RunMethod(\"getMeasuredWidth\", Null)";
_width = (int)(BA.ObjectToNumber(_jo.RunMethod("getMeasuredWidth",(Object[])(anywheresoftware.b4a.keywords.Common.Null))));
 //BA.debugLineNum = 316;BA.debugLine="Height = jo.RunMethod(\"getMeasuredHeight\", Nul";
_height = (int)(BA.ObjectToNumber(_jo.RunMethod("getMeasuredHeight",(Object[])(anywheresoftware.b4a.keywords.Common.Null))));
 //BA.debugLineNum = 317;BA.debugLine="If Width > 0 And Height > 0 Then";
if (true) break;

case 39:
//if
this.state = 42;
if (_width>0 && _height>0) { 
this.state = 41;
}if (true) break;

case 41:
//C
this.state = 42;
 //BA.debugLineNum = 318;BA.debugLine="Exit";
this.state = 43;
if (true) break;
 if (true) break;

case 42:
//C
this.state = 36;
;
 //BA.debugLineNum = 320;BA.debugLine="Sleep(50)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (50));
this.state = 58;
return;
case 58:
//C
this.state = 36;
;
 if (true) break;

case 43:
//C
this.state = 57;
;
 //BA.debugLineNum = 322;BA.debugLine="Dim p As Panel = xui.CreatePanel(\"\")";
_p = new anywheresoftware.b4a.objects.PanelWrapper();
_p = (anywheresoftware.b4a.objects.PanelWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.PanelWrapper(), (android.view.ViewGroup)(parent.mostCurrent._xui.CreatePanel(processBA,"").getObject()));
 //BA.debugLineNum = 323;BA.debugLine="p.Tag = lblBadge";
_p.setTag((Object)(_lblbadge.getObject()));
 //BA.debugLineNum = 324;BA.debugLine="lblBadge.Parent.AddView(p, 0, 0, Width, Height)";
_lblbadge.getParent().AddView((android.view.View)(_p.getObject()),(int) (0),(int) (0),_width,_height);
 //BA.debugLineNum = 325;BA.debugLine="lblBadge.RemoveViewFromParent";
_lblbadge.RemoveViewFromParent();
 //BA.debugLineNum = 326;BA.debugLine="p.AddView(lblBadge, 0, 0, Width, Height)";
_p.AddView((android.view.View)(_lblbadge.getObject()),(int) (0),(int) (0),_width,_height);
 if (true) break;
if (true) break;

case 44:
//C
this.state = 45;
;
 //BA.debugLineNum = 329;BA.debugLine="CheckDataFile(\"account.txt\")";
_checkdatafile("account.txt");
 //BA.debugLineNum = 331;BA.debugLine="authToken = StateManager.GetSetting(\"authToken\")";
parent._authtoken = parent.mostCurrent._statemanager._getsetting /*String*/ (mostCurrent.activityBA,"authToken");
 //BA.debugLineNum = 332;BA.debugLine="Log(\"authToken: \" & authToken)";
anywheresoftware.b4a.keywords.Common.LogImpl("3131260","authToken: "+parent._authtoken,0);
 //BA.debugLineNum = 334;BA.debugLine="If authToken = \"\" Then";
if (true) break;

case 45:
//if
this.state = 48;
if ((parent._authtoken).equals("")) { 
this.state = 47;
}if (true) break;

case 47:
//C
this.state = 48;
 //BA.debugLineNum = 335;BA.debugLine="Dim rs As ResumableSub = RequestAuthToken";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _requestauthtoken();
 //BA.debugLineNum = 336;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 59;
return;
case 59:
//C
this.state = 48;
_result = (Object) result[0];
;
 if (true) break;

case 48:
//C
this.state = 51;
;
 //BA.debugLineNum = 339;BA.debugLine="Pages = Array(True, True, True, True, True, True";
parent._pages = anywheresoftware.b4a.keywords.Common.ArrayToList(new Object[]{(Object)(anywheresoftware.b4a.keywords.Common.True),(Object)(anywheresoftware.b4a.keywords.Common.True),(Object)(anywheresoftware.b4a.keywords.Common.True),(Object)(anywheresoftware.b4a.keywords.Common.True),(Object)(anywheresoftware.b4a.keywords.Common.True),(Object)(anywheresoftware.b4a.keywords.Common.True)});
 if (true) break;

case 50:
//C
this.state = 51;
this.catchState = 0;
 //BA.debugLineNum = 342;BA.debugLine="ToastMessageShow(LastException,True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getObject()),anywheresoftware.b4a.keywords.Common.True);
 if (true) break;
if (true) break;

case 51:
//C
this.state = -1;
this.catchState = 0;
;
 //BA.debugLineNum = 344;BA.debugLine="End Sub";
if (true) break;
}} 
       catch (Exception e0) {
			
if (catchState == 0)
    throw e0;
else {
    state = catchState;
processBA.setLastException(e0);}
            }
        }
    }
}
public static void  _complete(Object _result) throws Exception{
}
public static String  _activity_createmenu(de.amberhome.objects.appcompat.ACMenuWrapper _menu) throws Exception{
 //BA.debugLineNum = 725;BA.debugLine="Sub Activity_Createmenu(Menu As ACMenu)";
 //BA.debugLineNum = 726;BA.debugLine="Try";
try { //BA.debugLineNum = 727;BA.debugLine="Menu.Clear";
_menu.Clear();
 //BA.debugLineNum = 728;BA.debugLine="gblACMenu = Menu";
mostCurrent._gblacmenu = _menu;
 //BA.debugLineNum = 729;BA.debugLine="Menu.Add(0, 0, \"Settings\",Null)";
_menu.Add((int) (0),(int) (0),BA.ObjectToCharSequence("Settings"),(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 730;BA.debugLine="Menu.Add(0, 0, \"Refresh video list\",Null)";
_menu.Add((int) (0),(int) (0),BA.ObjectToCharSequence("Refresh video list"),(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 731;BA.debugLine="Menu.Add(0, 0, \"Restart Application\",Null)";
_menu.Add((int) (0),(int) (0),BA.ObjectToCharSequence("Restart Application"),(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 732;BA.debugLine="Menu.Add(0, 0, \"Show free memory\",Null)";
_menu.Add((int) (0),(int) (0),BA.ObjectToCharSequence("Show free memory"),(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 733;BA.debugLine="Menu.Add(0, 0, \"Show chart\",Null)";
_menu.Add((int) (0),(int) (0),BA.ObjectToCharSequence("Show chart"),(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 734;BA.debugLine="Menu.Add(0, 0, \"About\",Null)";
_menu.Add((int) (0),(int) (0),BA.ObjectToCharSequence("About"),(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null));
 } 
       catch (Exception e11) {
			processBA.setLastException(e11); //BA.debugLineNum = 736;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("335389451",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 };
 //BA.debugLineNum = 738;BA.debugLine="End Sub";
return "";
}
public static boolean  _activity_keypress(int _keycode) throws Exception{
 //BA.debugLineNum = 960;BA.debugLine="Sub Activity_KeyPress (KeyCode As Int) As Boolean";
 //BA.debugLineNum = 961;BA.debugLine="If KeyCode = KeyCodes.KEYCODE_BACK Then";
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_BACK) { 
 //BA.debugLineNum = 962;BA.debugLine="If TabStrip1.CurrentPage = 2 Then";
if (mostCurrent._tabstrip1.getCurrentPage()==2) { 
 //BA.debugLineNum = 963;BA.debugLine="TabStrip1.ScrollTo(1,False)";
mostCurrent._tabstrip1.ScrollTo((int) (1),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 964;BA.debugLine="TabStrip1_PageSelected(1)";
_tabstrip1_pageselected((int) (1));
 //BA.debugLineNum = 965;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 }else if(mostCurrent._tabstrip1.getCurrentPage()==1) { 
 //BA.debugLineNum = 967;BA.debugLine="TabStrip1.ScrollTo(0,False)";
mostCurrent._tabstrip1.ScrollTo((int) (0),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 968;BA.debugLine="TabStrip1_PageSelected(0)";
_tabstrip1_pageselected((int) (0));
 //BA.debugLineNum = 969;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 }else if(mostCurrent._tabstrip1.getCurrentPage()==3) { 
 //BA.debugLineNum = 971;BA.debugLine="TabStrip1.ScrollTo(2,False)";
mostCurrent._tabstrip1.ScrollTo((int) (2),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 972;BA.debugLine="TabStrip1_PageSelected(2)";
_tabstrip1_pageselected((int) (2));
 //BA.debugLineNum = 973;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 }else if(mostCurrent._tabstrip1.getCurrentPage()==4) { 
 //BA.debugLineNum = 975;BA.debugLine="TabStrip1.ScrollTo(3,False)";
mostCurrent._tabstrip1.ScrollTo((int) (3),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 976;BA.debugLine="TabStrip1_PageSelected(3)";
_tabstrip1_pageselected((int) (3));
 //BA.debugLineNum = 977;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 }else if(mostCurrent._tabstrip1.getCurrentPage()==5) { 
 //BA.debugLineNum = 979;BA.debugLine="TabStrip1.ScrollTo(4,False)";
mostCurrent._tabstrip1.ScrollTo((int) (4),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 980;BA.debugLine="TabStrip1_PageSelected(4)";
_tabstrip1_pageselected((int) (4));
 //BA.debugLineNum = 981;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 }else if(mostCurrent._tabstrip1.getCurrentPage()==6) { 
 //BA.debugLineNum = 983;BA.debugLine="TabStrip1.ScrollTo(5,False)";
mostCurrent._tabstrip1.ScrollTo((int) (5),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 984;BA.debugLine="TabStrip1_PageSelected(5)";
_tabstrip1_pageselected((int) (5));
 //BA.debugLineNum = 985;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 };
 };
 //BA.debugLineNum = 989;BA.debugLine="End Sub";
return false;
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 427;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 428;BA.debugLine="Awake.ReleaseKeepAlive";
_awake.ReleaseKeepAlive();
 //BA.debugLineNum = 430;BA.debugLine="wvMedia.LoadUrl(\"\")";
mostCurrent._wvmedia.LoadUrl("");
 //BA.debugLineNum = 432;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
anywheresoftware.b4a.objects.IntentWrapper _in = null;
String _notificationclicked = "";
 //BA.debugLineNum = 346;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 347;BA.debugLine="Try";
try { //BA.debugLineNum = 348;BA.debugLine="Awake.KeepAlive(True)";
_awake.KeepAlive(processBA,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 350;BA.debugLine="If File.Exists(File.DirInternal, \"SideYard.jpg\")";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"SideYard.jpg")) { 
 //BA.debugLineNum = 351;BA.debugLine="bmp = xui.LoadBitmapResize(File.DirInternal, \"S";
_bmp = mostCurrent._xui.LoadBitmapResize(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"SideYard.jpg",mostCurrent._ivsideyard.getWidth(),mostCurrent._ivsideyard.getHeight(),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 352;BA.debugLine="ivSideYard.Bitmap = bmp";
mostCurrent._ivsideyard.setBitmap((android.graphics.Bitmap)(_bmp.getObject()));
 };
 //BA.debugLineNum = 354;BA.debugLine="If File.Exists(File.DirInternal, \"FrontYard.jpg\"";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"FrontYard.jpg")) { 
 //BA.debugLineNum = 355;BA.debugLine="bmp = xui.LoadBitmapResize(File.DirInternal, \"F";
_bmp = mostCurrent._xui.LoadBitmapResize(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"FrontYard.jpg",mostCurrent._ivfrontyard.getWidth(),mostCurrent._ivfrontyard.getHeight(),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 356;BA.debugLine="ivFrontYard.Bitmap = bmp";
mostCurrent._ivfrontyard.setBitmap((android.graphics.Bitmap)(_bmp.getObject()));
 };
 //BA.debugLineNum = 358;BA.debugLine="If File.Exists(File.DirInternal, \"Backyard.jpg\")";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"Backyard.jpg")) { 
 //BA.debugLineNum = 359;BA.debugLine="bmp = xui.LoadBitmapResize(File.DirInternal, \"B";
_bmp = mostCurrent._xui.LoadBitmapResize(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"Backyard.jpg",mostCurrent._ivfrontyard.getWidth(),mostCurrent._ivfrontyard.getHeight(),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 360;BA.debugLine="ivBackyard.Bitmap = bmp";
mostCurrent._ivbackyard.setBitmap((android.graphics.Bitmap)(_bmp.getObject()));
 };
 //BA.debugLineNum = 363;BA.debugLine="HandleSettings";
_handlesettings();
 //BA.debugLineNum = 364;BA.debugLine="Dim in As Intent = Activity.GetStartingIntent";
_in = new anywheresoftware.b4a.objects.IntentWrapper();
_in = mostCurrent._activity.GetStartingIntent();
 //BA.debugLineNum = 365;BA.debugLine="Dim NotificationClicked As String";
_notificationclicked = "";
 //BA.debugLineNum = 366;BA.debugLine="If in.IsInitialized And in <> OldIntent Then";
if (_in.IsInitialized() && (_in).equals(_oldintent) == false) { 
 //BA.debugLineNum = 367;BA.debugLine="OldIntent = in";
_oldintent = _in;
 //BA.debugLineNum = 368;BA.debugLine="If in.HasExtra(\"Notification_Tag\") Then";
if (_in.HasExtra("Notification_Tag")) { 
 //BA.debugLineNum = 369;BA.debugLine="NotificationClicked = in.GetExtra(\"Notificatio";
_notificationclicked = BA.ObjectToString(_in.GetExtra("Notification_Tag"));
 };
 };
 //BA.debugLineNum = 373;BA.debugLine="If NotificationClicked = \"Living area temperatur";
if ((_notificationclicked).equals("Living area temperature")) { 
 //BA.debugLineNum = 374;BA.debugLine="TabStrip1.ScrollTo(0,False)";
mostCurrent._tabstrip1.ScrollTo((int) (0),anywheresoftware.b4a.keywords.Common.False);
 }else if((_notificationclicked).equals("Living area carbon monoxide")) { 
 //BA.debugLineNum = 377;BA.debugLine="TabStrip1.ScrollTo(1,False)";
mostCurrent._tabstrip1.ScrollTo((int) (1),anywheresoftware.b4a.keywords.Common.False);
 }else if((_notificationclicked).equals("Basement temperature")) { 
 //BA.debugLineNum = 380;BA.debugLine="TabStrip1.ScrollTo(2,False)";
mostCurrent._tabstrip1.ScrollTo((int) (2),anywheresoftware.b4a.keywords.Common.False);
 }else if((_notificationclicked).equals("Basement carbon monoxide")) { 
 //BA.debugLineNum = 383;BA.debugLine="TabStrip1.ScrollTo(3,False)";
mostCurrent._tabstrip1.ScrollTo((int) (3),anywheresoftware.b4a.keywords.Common.False);
 }else if((_notificationclicked).equals("Basement DHT22 sensor issue")) { 
 //BA.debugLineNum = 386;BA.debugLine="TabStrip1.ScrollTo(2,False)";
mostCurrent._tabstrip1.ScrollTo((int) (2),anywheresoftware.b4a.keywords.Common.False);
 }else if((_notificationclicked).equals("Living area DHT22 sensor issue")) { 
 //BA.debugLineNum = 389;BA.debugLine="TabStrip1.ScrollTo(0,False)";
mostCurrent._tabstrip1.ScrollTo((int) (0),anywheresoftware.b4a.keywords.Common.False);
 }else if((_notificationclicked).equals("Living area CO sensor issue")) { 
 //BA.debugLineNum = 392;BA.debugLine="TabStrip1.ScrollTo(1,False)";
mostCurrent._tabstrip1.ScrollTo((int) (1),anywheresoftware.b4a.keywords.Common.False);
 }else if((_notificationclicked).equals("Basement CO sensor issue")) { 
 //BA.debugLineNum = 395;BA.debugLine="TabStrip1.ScrollTo(3,False)";
mostCurrent._tabstrip1.ScrollTo((int) (3),anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 399;BA.debugLine="If TabStrip1.CurrentPage = 0 Then";
if (mostCurrent._tabstrip1.getCurrentPage()==0) { 
 //BA.debugLineNum = 400;BA.debugLine="TabStrip1_PageSelected(0)";
_tabstrip1_pageselected((int) (0));
 }else if(mostCurrent._tabstrip1.getCurrentPage()==1) { 
 //BA.debugLineNum = 402;BA.debugLine="TabStrip1_PageSelected(1)";
_tabstrip1_pageselected((int) (1));
 }else if(mostCurrent._tabstrip1.getCurrentPage()==2) { 
 //BA.debugLineNum = 404;BA.debugLine="TabStrip1_PageSelected(2)";
_tabstrip1_pageselected((int) (2));
 }else if(mostCurrent._tabstrip1.getCurrentPage()==3) { 
 //BA.debugLineNum = 406;BA.debugLine="TabStrip1_PageSelected(3)";
_tabstrip1_pageselected((int) (3));
 }else if(mostCurrent._tabstrip1.getCurrentPage()==4) { 
 //BA.debugLineNum = 408;BA.debugLine="TabStrip1_PageSelected(4)";
_tabstrip1_pageselected((int) (4));
 }else if(mostCurrent._tabstrip1.getCurrentPage()==5) { 
 //BA.debugLineNum = 410;BA.debugLine="TabStrip1_PageSelected(5)";
_tabstrip1_pageselected((int) (5));
 }else if(mostCurrent._tabstrip1.getCurrentPage()==6) { 
 //BA.debugLineNum = 412;BA.debugLine="TabStrip1_PageSelected(6)";
_tabstrip1_pageselected((int) (6));
 };
 } 
       catch (Exception e57) {
			processBA.setLastException(e57); //BA.debugLineNum = 415;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("3196677",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 };
 //BA.debugLineNum = 418;BA.debugLine="Try";
try { //BA.debugLineNum = 419;BA.debugLine="If MQTT.IsInitialized = False Or MQTT.Connected";
if (_mqtt.IsInitialized()==anywheresoftware.b4a.keywords.Common.False || _mqtt.getConnected()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 420;BA.debugLine="MQTT_Connect";
_mqtt_connect();
 };
 } 
       catch (Exception e64) {
			processBA.setLastException(e64); //BA.debugLineNum = 423;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("3196685",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 };
 //BA.debugLineNum = 425;BA.debugLine="End Sub";
return "";
}
public static void  _actoolbarlight1_menuitemclick(de.amberhome.objects.appcompat.ACMenuItemWrapper _item) throws Exception{
ResumableSub_ACToolBarLight1_MenuItemClick rsub = new ResumableSub_ACToolBarLight1_MenuItemClick(null,_item);
rsub.resume(processBA, null);
}
public static class ResumableSub_ACToolBarLight1_MenuItemClick extends BA.ResumableSub {
public ResumableSub_ACToolBarLight1_MenuItemClick(cloyd.smart.home.monitor.main parent,de.amberhome.objects.appcompat.ACMenuItemWrapper _item) {
this.parent = parent;
this._item = _item;
}
cloyd.smart.home.monitor.main parent;
de.amberhome.objects.appcompat.ACMenuItemWrapper _item;
anywheresoftware.b4a.objects.drawable.BitmapDrawable _bd = null;
anywheresoftware.b4a.objects.IntentWrapper _intent1 = null;
anywheresoftware.b4a.phone.Phone _phone = null;
String _info = "";
anywheresoftware.b4a.agraham.reflection.Reflection _r = null;
int _countdeletevideo = 0;
String _f = "";
anywheresoftware.b4a.keywords.Common.ResumableSubWrapper _rs = null;
Object _results = null;
int _result = 0;
anywheresoftware.b4a.BA.IterableList group35;
int index35;
int groupLen35;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
try {

        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 592;BA.debugLine="Try";
if (true) break;

case 1:
//try
this.state = 90;
this.catchState = 89;
this.state = 3;
if (true) break;

case 3:
//C
this.state = 4;
this.catchState = 89;
 //BA.debugLineNum = 593;BA.debugLine="If Item.Title = \"About\" Then";
if (true) break;

case 4:
//if
this.state = 87;
if ((_item.getTitle()).equals("About")) { 
this.state = 6;
}else if((_item.getTitle()).equals("Settings")) { 
this.state = 8;
}else if((_item.getTitle()).equals("Restart Application")) { 
this.state = 10;
}else if((_item.getTitle()).equals("Show free memory")) { 
this.state = 16;
}else if((_item.getTitle()).equals("Show chart")) { 
this.state = 18;
}else if((_item.getTitle()).equals("Refresh video list")) { 
this.state = 20;
}else if((_item.getTitle()).equals("Restart board")) { 
this.state = 38;
}if (true) break;

case 6:
//C
this.state = 87;
 //BA.debugLineNum = 594;BA.debugLine="ShowAboutMenu";
_showaboutmenu();
 if (true) break;

case 8:
//C
this.state = 87;
 //BA.debugLineNum = 596;BA.debugLine="StartActivity(screen.CreateIntent)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(parent._screen.CreateIntent()));
 if (true) break;

case 10:
//C
this.state = 11;
 //BA.debugLineNum = 598;BA.debugLine="Dim bd As BitmapDrawable";
_bd = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
 //BA.debugLineNum = 599;BA.debugLine="bd.Initialize(LoadBitmapResize(File.DirAssets,";
_bd.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmapResize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"0.png",anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (32)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (32)),anywheresoftware.b4a.keywords.Common.True).getObject()));
 //BA.debugLineNum = 600;BA.debugLine="If  Msgbox2(\"Restart the application?\", \"Smart";
if (true) break;

case 11:
//if
this.state = 14;
if (anywheresoftware.b4a.keywords.Common.Msgbox2(BA.ObjectToCharSequence("Restart the application?"),BA.ObjectToCharSequence("Smart Home Monitor"),"Yes","","No",_bd.getBitmap(),mostCurrent.activityBA)==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
this.state = 13;
}if (true) break;

case 13:
//C
this.state = 14;
 //BA.debugLineNum = 602;BA.debugLine="Dim Intent1 As Intent";
_intent1 = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 603;BA.debugLine="Intent1.Initialize(\"smart.home.restart\", \"\")";
_intent1.Initialize("smart.home.restart","");
 //BA.debugLineNum = 604;BA.debugLine="Dim Phone As Phone";
_phone = new anywheresoftware.b4a.phone.Phone();
 //BA.debugLineNum = 605;BA.debugLine="Phone.SendBroadcastIntent(Intent1)";
_phone.SendBroadcastIntent((android.content.Intent)(_intent1.getObject()));
 if (true) break;

case 14:
//C
this.state = 87;
;
 if (true) break;

case 16:
//C
this.state = 87;
 //BA.debugLineNum = 608;BA.debugLine="Dim info As String";
_info = "";
 //BA.debugLineNum = 609;BA.debugLine="info = Starter.kvs.ListKeys.Size & \" video clip";
_info = BA.NumberToString(parent.mostCurrent._starter._kvs /*cloyd.smart.home.monitor.keyvaluestore*/ ._listkeys /*anywheresoftware.b4a.objects.collections.List*/ ().getSize())+" video clips"+anywheresoftware.b4a.keywords.Common.CRLF+anywheresoftware.b4a.keywords.Common.CRLF;
 //BA.debugLineNum = 610;BA.debugLine="Dim r As Reflector";
_r = new anywheresoftware.b4a.agraham.reflection.Reflection();
 //BA.debugLineNum = 611;BA.debugLine="r.Target = r.RunStaticMethod(\"java.lang.Runtime";
_r.Target = _r.RunStaticMethod("java.lang.Runtime","getRuntime",(Object[])(anywheresoftware.b4a.keywords.Common.Null),(String[])(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 612;BA.debugLine="info = info & \"Available memory: \" & NumberForm";
_info = _info+"Available memory: "+anywheresoftware.b4a.keywords.Common.NumberFormat2((((double)(BA.ObjectToNumber(_r.RunMethod("maxMemory")))-(double)(BA.ObjectToNumber(_r.RunMethod("totalMemory"))))/(double)(1024*1024)),(int) (0),(int) (2),(int) (2),anywheresoftware.b4a.keywords.Common.False)+" MB"+anywheresoftware.b4a.keywords.Common.CRLF;
 //BA.debugLineNum = 613;BA.debugLine="info = info & \"Free memory: \" & NumberFormat2(G";
_info = _info+"Free memory: "+anywheresoftware.b4a.keywords.Common.NumberFormat2(_getfreemem()/(double)1000,(int) (0),(int) (2),(int) (2),anywheresoftware.b4a.keywords.Common.False)+" MB";
 //BA.debugLineNum = 614;BA.debugLine="Msgbox(info,\"Smart Home Monitor\") 'ignore";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence(_info),BA.ObjectToCharSequence("Smart Home Monitor"),mostCurrent.activityBA);
 if (true) break;

case 18:
//C
this.state = 87;
 //BA.debugLineNum = 616;BA.debugLine="StartActivity(\"Chart\")";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)("Chart"));
 if (true) break;

case 20:
//C
this.state = 21;
 //BA.debugLineNum = 618;BA.debugLine="Dim bd As BitmapDrawable";
_bd = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
 //BA.debugLineNum = 619;BA.debugLine="bd.Initialize(LoadBitmapResize(File.DirAssets,";
_bd.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmapResize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"0.png",anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (32)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (32)),anywheresoftware.b4a.keywords.Common.True).getObject()));
 //BA.debugLineNum = 620;BA.debugLine="If  Msgbox2(\"Refresh video list?\", \"Smart Home";
if (true) break;

case 21:
//if
this.state = 36;
if (anywheresoftware.b4a.keywords.Common.Msgbox2(BA.ObjectToCharSequence("Refresh video list?"),BA.ObjectToCharSequence("Smart Home Monitor"),"Yes","","No",_bd.getBitmap(),mostCurrent.activityBA)==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
this.state = 23;
}if (true) break;

case 23:
//C
this.state = 24;
 //BA.debugLineNum = 621;BA.debugLine="B4XLoadingIndicator4.Show";
parent.mostCurrent._b4xloadingindicator4._show /*String*/ ();
 //BA.debugLineNum = 623;BA.debugLine="lblDuration.Text = \"0:00\"";
parent.mostCurrent._lblduration.setText(BA.ObjectToCharSequence("0:00"));
 //BA.debugLineNum = 624;BA.debugLine="wvMedia.LoadUrl(\"\")";
parent.mostCurrent._wvmedia.LoadUrl("");
 //BA.debugLineNum = 625;BA.debugLine="Starter.kvs.DeleteAll";
parent.mostCurrent._starter._kvs /*cloyd.smart.home.monitor.keyvaluestore*/ ._deleteall /*String*/ ();
 //BA.debugLineNum = 626;BA.debugLine="clvActivity.Clear";
parent.mostCurrent._clvactivity._clear();
 //BA.debugLineNum = 629;BA.debugLine="Dim countDeleteVideo As Int = 0";
_countdeletevideo = (int) (0);
 //BA.debugLineNum = 630;BA.debugLine="For Each f As String In File.ListFiles(File.Di";
if (true) break;

case 24:
//for
this.state = 35;
group35 = anywheresoftware.b4a.keywords.Common.File.ListFiles(anywheresoftware.b4a.keywords.Common.File.getDirInternal());
index35 = 0;
groupLen35 = group35.getSize();
this.state = 91;
if (true) break;

case 91:
//C
this.state = 35;
if (index35 < groupLen35) {
this.state = 26;
_f = BA.ObjectToString(group35.Get(index35));}
if (true) break;

case 92:
//C
this.state = 91;
index35++;
if (true) break;

case 26:
//C
this.state = 27;
 //BA.debugLineNum = 631;BA.debugLine="If f.ToLowerCase.EndsWith(\".mp4\") Then";
if (true) break;

case 27:
//if
this.state = 34;
if (_f.toLowerCase().endsWith(".mp4")) { 
this.state = 29;
}if (true) break;

case 29:
//C
this.state = 30;
 //BA.debugLineNum = 632;BA.debugLine="If File.Delete(File.DirInternal, f) Then";
if (true) break;

case 30:
//if
this.state = 33;
if (anywheresoftware.b4a.keywords.Common.File.Delete(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),_f)) { 
this.state = 32;
}if (true) break;

case 32:
//C
this.state = 33;
 //BA.debugLineNum = 633;BA.debugLine="countDeleteVideo = countDeleteVideo + 1";
_countdeletevideo = (int) (_countdeletevideo+1);
 if (true) break;

case 33:
//C
this.state = 34;
;
 if (true) break;

case 34:
//C
this.state = 92;
;
 if (true) break;
if (true) break;

case 35:
//C
this.state = 36;
;
 //BA.debugLineNum = 637;BA.debugLine="ToastMessageShow(countDeleteVideo & \" videos d";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence(BA.NumberToString(_countdeletevideo)+" videos deleted"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 640;BA.debugLine="Dim rs As ResumableSub = GetUnwatchedVideos";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _getunwatchedvideos();
 //BA.debugLineNum = 641;BA.debugLine="wait for (rs) complete (Results As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 93;
return;
case 93:
//C
this.state = 36;
_results = (Object) result[0];
;
 //BA.debugLineNum = 644;BA.debugLine="Dim rs As ResumableSub = GetVideos(response)";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _getvideos(parent._response);
 //BA.debugLineNum = 645;BA.debugLine="wait for (rs) complete (Results As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 94;
return;
case 94:
//C
this.state = 36;
_results = (Object) result[0];
;
 //BA.debugLineNum = 647;BA.debugLine="TabStrip1.ScrollTo(5,False)";
parent.mostCurrent._tabstrip1.ScrollTo((int) (5),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 648;BA.debugLine="Sleep(100)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (100));
this.state = 95;
return;
case 95:
//C
this.state = 36;
;
 //BA.debugLineNum = 649;BA.debugLine="B4XLoadingIndicator4.Hide";
parent.mostCurrent._b4xloadingindicator4._hide /*String*/ ();
 //BA.debugLineNum = 650;BA.debugLine="Return";
if (true) return ;
 if (true) break;

case 36:
//C
this.state = 87;
;
 if (true) break;

case 38:
//C
this.state = 39;
 //BA.debugLineNum = 653;BA.debugLine="Try";
if (true) break;

case 39:
//try
this.state = 86;
this.catchState = 85;
this.state = 41;
if (true) break;

case 41:
//C
this.state = 42;
this.catchState = 85;
 //BA.debugLineNum = 654;BA.debugLine="Dim Result As Int";
_result = 0;
 //BA.debugLineNum = 655;BA.debugLine="Dim bd As BitmapDrawable";
_bd = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
 //BA.debugLineNum = 656;BA.debugLine="bd.Initialize(LoadBitmapResize(File.DirAssets,";
_bd.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmapResize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"0.png",anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (32)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (32)),anywheresoftware.b4a.keywords.Common.True).getObject()));
 //BA.debugLineNum = 657;BA.debugLine="If TabStrip1.CurrentPage = 2 Then";
if (true) break;

case 42:
//if
this.state = 83;
if (parent.mostCurrent._tabstrip1.getCurrentPage()==2) { 
this.state = 44;
}else if(parent.mostCurrent._tabstrip1.getCurrentPage()==1) { 
this.state = 54;
}else if(parent.mostCurrent._tabstrip1.getCurrentPage()==3) { 
this.state = 64;
}else {
this.state = 74;
}if (true) break;

case 44:
//C
this.state = 45;
 //BA.debugLineNum = 658;BA.debugLine="Result = Msgbox2(\"Restart the BASEMENT contro";
_result = anywheresoftware.b4a.keywords.Common.Msgbox2(BA.ObjectToCharSequence("Restart the BASEMENT controller?"),BA.ObjectToCharSequence("Smart Home Monitor"),"Yes","","No",_bd.getBitmap(),mostCurrent.activityBA);
 //BA.debugLineNum = 659;BA.debugLine="If Result = DialogResponse.POSITIVE Then";
if (true) break;

case 45:
//if
this.state = 52;
if (_result==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
this.state = 47;
}if (true) break;

case 47:
//C
this.state = 48;
 //BA.debugLineNum = 660;BA.debugLine="If MQTT.IsInitialized = False Or MQTT.Connec";
if (true) break;

case 48:
//if
this.state = 51;
if (parent._mqtt.IsInitialized()==anywheresoftware.b4a.keywords.Common.False || parent._mqtt.getConnected()==anywheresoftware.b4a.keywords.Common.False) { 
this.state = 50;
}if (true) break;

case 50:
//C
this.state = 51;
 //BA.debugLineNum = 661;BA.debugLine="MQTT_Connect";
_mqtt_connect();
 if (true) break;

case 51:
//C
this.state = 52;
;
 //BA.debugLineNum = 663;BA.debugLine="MQTT.Publish(\"TempHumidBasement\", bc.StringT";
parent._mqtt.Publish("TempHumidBasement",parent._bc.StringToBytes("Restart controller","utf8"));
 if (true) break;

case 52:
//C
this.state = 83;
;
 if (true) break;

case 54:
//C
this.state = 55;
 //BA.debugLineNum = 666;BA.debugLine="Result = Msgbox2(\"Restart the AIR QUALITY con";
_result = anywheresoftware.b4a.keywords.Common.Msgbox2(BA.ObjectToCharSequence("Restart the AIR QUALITY controller?"),BA.ObjectToCharSequence("Smart Home Monitor"),"Yes","","No",_bd.getBitmap(),mostCurrent.activityBA);
 //BA.debugLineNum = 667;BA.debugLine="If Result = DialogResponse.POSITIVE Then";
if (true) break;

case 55:
//if
this.state = 62;
if (_result==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
this.state = 57;
}if (true) break;

case 57:
//C
this.state = 58;
 //BA.debugLineNum = 668;BA.debugLine="If MQTT.IsInitialized = False Or MQTT.Connec";
if (true) break;

case 58:
//if
this.state = 61;
if (parent._mqtt.IsInitialized()==anywheresoftware.b4a.keywords.Common.False || parent._mqtt.getConnected()==anywheresoftware.b4a.keywords.Common.False) { 
this.state = 60;
}if (true) break;

case 60:
//C
this.state = 61;
 //BA.debugLineNum = 669;BA.debugLine="MQTT_Connect";
_mqtt_connect();
 if (true) break;

case 61:
//C
this.state = 62;
;
 //BA.debugLineNum = 671;BA.debugLine="MQTT.Publish(\"MQ7\", bc.StringToBytes(\"Restar";
parent._mqtt.Publish("MQ7",parent._bc.StringToBytes("Restart controller","utf8"));
 if (true) break;

case 62:
//C
this.state = 83;
;
 if (true) break;

case 64:
//C
this.state = 65;
 //BA.debugLineNum = 674;BA.debugLine="Result = Msgbox2(\"Restart the BASEMENT AIR QU";
_result = anywheresoftware.b4a.keywords.Common.Msgbox2(BA.ObjectToCharSequence("Restart the BASEMENT AIR QUALITY controller?"),BA.ObjectToCharSequence("Smart Home Monitor"),"Yes","","No",_bd.getBitmap(),mostCurrent.activityBA);
 //BA.debugLineNum = 675;BA.debugLine="If Result = DialogResponse.POSITIVE Then";
if (true) break;

case 65:
//if
this.state = 72;
if (_result==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
this.state = 67;
}if (true) break;

case 67:
//C
this.state = 68;
 //BA.debugLineNum = 676;BA.debugLine="If MQTT.IsInitialized = False Or MQTT.Connec";
if (true) break;

case 68:
//if
this.state = 71;
if (parent._mqtt.IsInitialized()==anywheresoftware.b4a.keywords.Common.False || parent._mqtt.getConnected()==anywheresoftware.b4a.keywords.Common.False) { 
this.state = 70;
}if (true) break;

case 70:
//C
this.state = 71;
 //BA.debugLineNum = 677;BA.debugLine="MQTT_Connect";
_mqtt_connect();
 if (true) break;

case 71:
//C
this.state = 72;
;
 //BA.debugLineNum = 679;BA.debugLine="MQTT.Publish(\"MQ7Basement\", bc.StringToBytes";
parent._mqtt.Publish("MQ7Basement",parent._bc.StringToBytes("Restart controller","utf8"));
 if (true) break;

case 72:
//C
this.state = 83;
;
 if (true) break;

case 74:
//C
this.state = 75;
 //BA.debugLineNum = 682;BA.debugLine="Result = Msgbox2(\"Restart the WEATHER control";
_result = anywheresoftware.b4a.keywords.Common.Msgbox2(BA.ObjectToCharSequence("Restart the WEATHER controller?"),BA.ObjectToCharSequence("Smart Home Monitor"),"Yes","","No",_bd.getBitmap(),mostCurrent.activityBA);
 //BA.debugLineNum = 683;BA.debugLine="If Result = DialogResponse.POSITIVE Then";
if (true) break;

case 75:
//if
this.state = 82;
if (_result==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
this.state = 77;
}if (true) break;

case 77:
//C
this.state = 78;
 //BA.debugLineNum = 684;BA.debugLine="If MQTT.IsInitialized = False Or MQTT.Connec";
if (true) break;

case 78:
//if
this.state = 81;
if (parent._mqtt.IsInitialized()==anywheresoftware.b4a.keywords.Common.False || parent._mqtt.getConnected()==anywheresoftware.b4a.keywords.Common.False) { 
this.state = 80;
}if (true) break;

case 80:
//C
this.state = 81;
 //BA.debugLineNum = 685;BA.debugLine="MQTT_Connect";
_mqtt_connect();
 if (true) break;

case 81:
//C
this.state = 82;
;
 //BA.debugLineNum = 687;BA.debugLine="MQTT.Publish(\"TempHumid\", bc.StringToBytes(\"";
parent._mqtt.Publish("TempHumid",parent._bc.StringToBytes("Restart controller","utf8"));
 if (true) break;

case 82:
//C
this.state = 83;
;
 if (true) break;

case 83:
//C
this.state = 86;
;
 if (true) break;

case 85:
//C
this.state = 86;
this.catchState = 89;
 //BA.debugLineNum = 691;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("335192932",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 if (true) break;
if (true) break;

case 86:
//C
this.state = 87;
this.catchState = 89;
;
 if (true) break;

case 87:
//C
this.state = 90;
;
 if (true) break;

case 89:
//C
this.state = 90;
this.catchState = 0;
 //BA.debugLineNum = 695;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("335192936",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 if (true) break;
if (true) break;

case 90:
//C
this.state = -1;
this.catchState = 0;
;
 //BA.debugLineNum = 697;BA.debugLine="End Sub";
if (true) break;
}} 
       catch (Exception e0) {
			
if (catchState == 0)
    throw e0;
else {
    state = catchState;
processBA.setLastException(e0);}
            }
        }
    }
}
public static String  _bluriv(String _image,anywheresoftware.b4a.objects.ImageViewWrapper _iv) throws Exception{
 //BA.debugLineNum = 2384;BA.debugLine="Sub BlurIV (image As String,iv As ImageView)";
 //BA.debugLineNum = 2385;BA.debugLine="Try";
try { //BA.debugLineNum = 2386;BA.debugLine="bmp = xui.LoadBitmapResize(File.DirInternal, ima";
_bmp = mostCurrent._xui.LoadBitmapResize(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),_image,_iv.getWidth(),_iv.getHeight(),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2387;BA.debugLine="effects.Initialize";
_effects._initialize /*String*/ (processBA);
 //BA.debugLineNum = 2388;BA.debugLine="iv.Bitmap = effects.Blur(bmp)";
_iv.setBitmap((android.graphics.Bitmap)(_effects._blur /*anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper*/ (_bmp).getObject()));
 } 
       catch (Exception e6) {
			processBA.setLastException(e6); //BA.debugLineNum = 2390;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("337486598",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 };
 //BA.debugLineNum = 2392;BA.debugLine="End Sub";
return "";
}
public static void  _btnbackyardnewclip_click() throws Exception{
ResumableSub_btnBackyardNewClip_Click rsub = new ResumableSub_btnBackyardNewClip_Click(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_btnBackyardNewClip_Click extends BA.ResumableSub {
public ResumableSub_btnBackyardNewClip_Click(cloyd.smart.home.monitor.main parent) {
this.parent = parent;
}
cloyd.smart.home.monitor.main parent;
anywheresoftware.b4a.objects.drawable.BitmapDrawable _bd = null;
anywheresoftware.b4a.objects.IntentWrapper _intent1 = null;
anywheresoftware.b4a.phone.Phone _phone = null;
anywheresoftware.b4a.keywords.Common.ResumableSubWrapper _rs = null;
Object _result = null;
int _i = 0;
int step44;
int limit44;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
try {

        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 3246;BA.debugLine="Dim bd As BitmapDrawable";
_bd = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
 //BA.debugLineNum = 3247;BA.debugLine="bd.Initialize(LoadBitmapResize(File.DirAssets, \"0";
_bd.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmapResize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"0.png",anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (32)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (32)),anywheresoftware.b4a.keywords.Common.True).getObject()));
 //BA.debugLineNum = 3248;BA.debugLine="If Msgbox2(\"Capture video from the Backyard camer";
if (true) break;

case 1:
//if
this.state = 24;
if (anywheresoftware.b4a.keywords.Common.Msgbox2(BA.ObjectToCharSequence("Capture video from the Backyard camera?"),BA.ObjectToCharSequence("Smart Home Monitor"),"Yes","","No",_bd.getBitmap(),mostCurrent.activityBA)==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
this.state = 3;
}if (true) break;

case 3:
//C
this.state = 4;
 //BA.debugLineNum = 3249;BA.debugLine="Try";
if (true) break;

case 4:
//try
this.state = 23;
this.catchState = 22;
this.state = 6;
if (true) break;

case 6:
//C
this.state = 7;
this.catchState = 22;
 //BA.debugLineNum = 3251;BA.debugLine="Dim Intent1 As Intent";
_intent1 = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 3252;BA.debugLine="Intent1.Initialize(\"blink.liveview.backyard\", \"";
_intent1.Initialize("blink.liveview.backyard","");
 //BA.debugLineNum = 3253;BA.debugLine="Dim Phone As Phone";
_phone = new anywheresoftware.b4a.phone.Phone();
 //BA.debugLineNum = 3254;BA.debugLine="Phone.SendBroadcastIntent(Intent1)";
_phone.SendBroadcastIntent((android.content.Intent)(_intent1.getObject()));
 //BA.debugLineNum = 3256;BA.debugLine="ScrollViewBlink.ScrollToNow(0)";
parent.mostCurrent._scrollviewblink.ScrollToNow((int) (0));
 //BA.debugLineNum = 3257;BA.debugLine="btnSideYard.Enabled = False";
parent.mostCurrent._btnsideyard._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3258;BA.debugLine="btnRefresh.Enabled = False";
parent.mostCurrent._btnrefresh._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3259;BA.debugLine="btnSideYardNewClip.Enabled = False";
parent.mostCurrent._btnsideyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3260;BA.debugLine="btnFrontYardNewClip.Enabled = False";
parent.mostCurrent._btnfrontyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3261;BA.debugLine="btnSideYardRefresh.Enabled = False";
parent.mostCurrent._btnsideyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3262;BA.debugLine="btnFrontYardRefresh.Enabled = False";
parent.mostCurrent._btnfrontyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3263;BA.debugLine="btnBackyardRefresh.Enabled = False";
parent.mostCurrent._btnbackyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3264;BA.debugLine="swArmed.Enabled = False";
parent.mostCurrent._swarmed._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3265;BA.debugLine="ivSideYard.Enabled = False";
parent.mostCurrent._ivsideyard.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3266;BA.debugLine="ivFrontYard.Enabled = False";
parent.mostCurrent._ivfrontyard.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3267;BA.debugLine="ivBackyard.Enabled = False";
parent.mostCurrent._ivbackyard.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3268;BA.debugLine="lblStatus.Text = \"Capturing a new Backyard vide";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("Capturing a new Backyard video clip..."));
 //BA.debugLineNum = 3270;BA.debugLine="Dim rs As ResumableSub = RESTPost(\"https://rest";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _restpost("https://rest-"+parent._userregion+".immedia-semi.com/network/"+parent._networkid+"/camera/458236/clip");
 //BA.debugLineNum = 3271;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 25;
return;
case 25:
//C
this.state = 7;
_result = (Object) result[0];
;
 //BA.debugLineNum = 3272;BA.debugLine="If response.StartsWith(\"ERROR: \") Or response.C";
if (true) break;

case 7:
//if
this.state = 10;
if (parent._response.startsWith("ERROR: ") || parent._response.contains("System is busy, please wait")) { 
this.state = 9;
}if (true) break;

case 9:
//C
this.state = 10;
 //BA.debugLineNum = 3273;BA.debugLine="btnSideYard.Enabled = True";
parent.mostCurrent._btnsideyard._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3274;BA.debugLine="btnRefresh.Enabled = True";
parent.mostCurrent._btnrefresh._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3275;BA.debugLine="btnSideYardNewClip.Enabled = True";
parent.mostCurrent._btnsideyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3276;BA.debugLine="btnFrontYardNewClip.Enabled = True";
parent.mostCurrent._btnfrontyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3277;BA.debugLine="btnBackyardNewClip.Enabled = True";
parent.mostCurrent._btnbackyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3278;BA.debugLine="btnSideYardRefresh.Enabled = True";
parent.mostCurrent._btnsideyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3279;BA.debugLine="btnFrontYardRefresh.Enabled = True";
parent.mostCurrent._btnfrontyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3280;BA.debugLine="btnBackyardRefresh.Enabled = True";
parent.mostCurrent._btnbackyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3281;BA.debugLine="ivSideYard.Enabled = True";
parent.mostCurrent._ivsideyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3282;BA.debugLine="ivFrontYard.Enabled = True";
parent.mostCurrent._ivfrontyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3283;BA.debugLine="ivBackyard.Enabled = True";
parent.mostCurrent._ivbackyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3284;BA.debugLine="swArmed.Enabled = True";
parent.mostCurrent._swarmed._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3285;BA.debugLine="lblStatus.Text = response";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence(parent._response));
 //BA.debugLineNum = 3286;BA.debugLine="Return";
if (true) return ;
 if (true) break;

case 10:
//C
this.state = 11;
;
 //BA.debugLineNum = 3289;BA.debugLine="Dim rs As ResumableSub = GetCommandID(response)";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _getcommandid(parent._response);
 //BA.debugLineNum = 3290;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 26;
return;
case 26:
//C
this.state = 11;
_result = (Object) result[0];
;
 //BA.debugLineNum = 3292;BA.debugLine="Dim rs As ResumableSub = RESTGet(\"https://rest-";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _restget("https://rest-"+parent._userregion+".immedia-semi.com/network/"+parent._networkid+"/command/"+parent._commandid);
 //BA.debugLineNum = 3293;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 27;
return;
case 27:
//C
this.state = 11;
_result = (Object) result[0];
;
 //BA.debugLineNum = 3294;BA.debugLine="For i = 1 To 60";
if (true) break;

case 11:
//for
this.state = 20;
step44 = 1;
limit44 = (int) (60);
_i = (int) (1) ;
this.state = 28;
if (true) break;

case 28:
//C
this.state = 20;
if ((step44 > 0 && _i <= limit44) || (step44 < 0 && _i >= limit44)) this.state = 13;
if (true) break;

case 29:
//C
this.state = 28;
_i = ((int)(0 + _i + step44)) ;
if (true) break;

case 13:
//C
this.state = 14;
 //BA.debugLineNum = 3296;BA.debugLine="Dim rs As ResumableSub = GetCommandStatus(resp";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _getcommandstatus(parent._response);
 //BA.debugLineNum = 3297;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 30;
return;
case 30:
//C
this.state = 14;
_result = (Object) result[0];
;
 //BA.debugLineNum = 3298;BA.debugLine="If commandComplete Then";
if (true) break;

case 14:
//if
this.state = 19;
if (parent._commandcomplete) { 
this.state = 16;
}else {
this.state = 18;
}if (true) break;

case 16:
//C
this.state = 19;
 //BA.debugLineNum = 3303;BA.debugLine="Dim rs As ResumableSub = GetUnwatchedVideos";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _getunwatchedvideos();
 //BA.debugLineNum = 3304;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 31;
return;
case 31:
//C
this.state = 19;
_result = (Object) result[0];
;
 //BA.debugLineNum = 3305;BA.debugLine="Exit";
this.state = 20;
if (true) break;
 if (true) break;

case 18:
//C
this.state = 19;
 //BA.debugLineNum = 3307;BA.debugLine="lblStatus.Text = \"Awaiting for the Backyard v";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("Awaiting for the Backyard video clip... "+BA.NumberToString(_i)+"/60"));
 if (true) break;

case 19:
//C
this.state = 29;
;
 //BA.debugLineNum = 3310;BA.debugLine="Dim rs As ResumableSub = RESTGet(\"https://rest";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _restget("https://rest-"+parent._userregion+".immedia-semi.com/network/"+parent._networkid+"/command/"+parent._commandid);
 //BA.debugLineNum = 3311;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 32;
return;
case 32:
//C
this.state = 29;
_result = (Object) result[0];
;
 //BA.debugLineNum = 3312;BA.debugLine="Sleep(1000)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (1000));
this.state = 33;
return;
case 33:
//C
this.state = 29;
;
 if (true) break;
if (true) break;

case 20:
//C
this.state = 23;
;
 if (true) break;

case 22:
//C
this.state = 23;
this.catchState = 0;
 //BA.debugLineNum = 3315;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("338797382",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 if (true) break;
if (true) break;

case 23:
//C
this.state = 24;
this.catchState = 0;
;
 //BA.debugLineNum = 3317;BA.debugLine="btnSideYard.Enabled = True";
parent.mostCurrent._btnsideyard._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3318;BA.debugLine="btnRefresh.Enabled = True";
parent.mostCurrent._btnrefresh._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3319;BA.debugLine="btnSideYardNewClip.Enabled = True";
parent.mostCurrent._btnsideyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3320;BA.debugLine="btnFrontYardNewClip.Enabled = True";
parent.mostCurrent._btnfrontyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3321;BA.debugLine="btnBackyardNewClip.Enabled = True";
parent.mostCurrent._btnbackyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3322;BA.debugLine="btnSideYardRefresh.Enabled = True";
parent.mostCurrent._btnsideyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3323;BA.debugLine="btnFrontYardRefresh.Enabled = True";
parent.mostCurrent._btnfrontyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3324;BA.debugLine="btnBackyardRefresh.Enabled = True";
parent.mostCurrent._btnbackyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3325;BA.debugLine="ivSideYard.Enabled = True";
parent.mostCurrent._ivsideyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3326;BA.debugLine="ivFrontYard.Enabled = True";
parent.mostCurrent._ivfrontyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3327;BA.debugLine="ivBackyard.Enabled = True";
parent.mostCurrent._ivbackyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3328;BA.debugLine="swArmed.Enabled = True";
parent.mostCurrent._swarmed._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3329;BA.debugLine="lblStatus.Text = \"Ready\"";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("Ready"));
 if (true) break;

case 24:
//C
this.state = -1;
;
 //BA.debugLineNum = 3331;BA.debugLine="End Sub";
if (true) break;
}} 
       catch (Exception e0) {
			
if (catchState == 0)
    throw e0;
else {
    state = catchState;
processBA.setLastException(e0);}
            }
        }
    }
}
public static void  _btnbackyardrefresh_click() throws Exception{
ResumableSub_btnBackyardRefresh_Click rsub = new ResumableSub_btnBackyardRefresh_Click(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_btnBackyardRefresh_Click extends BA.ResumableSub {
public ResumableSub_btnBackyardRefresh_Click(cloyd.smart.home.monitor.main parent) {
this.parent = parent;
}
cloyd.smart.home.monitor.main parent;
anywheresoftware.b4a.keywords.Common.ResumableSubWrapper _rs = null;
Object _result = null;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = -1;
 //BA.debugLineNum = 3465;BA.debugLine="btnSideYard.Enabled = False";
parent.mostCurrent._btnsideyard._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3466;BA.debugLine="btnRefresh.Enabled = False";
parent.mostCurrent._btnrefresh._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3467;BA.debugLine="btnSideYardNewClip.Enabled = False";
parent.mostCurrent._btnsideyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3468;BA.debugLine="btnFrontYardNewClip.Enabled = False";
parent.mostCurrent._btnfrontyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3469;BA.debugLine="btnBackyardNewClip.Enabled = False";
parent.mostCurrent._btnbackyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3470;BA.debugLine="btnSideYardRefresh.Enabled = False";
parent.mostCurrent._btnsideyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3471;BA.debugLine="btnFrontYardRefresh.Enabled = False";
parent.mostCurrent._btnfrontyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3472;BA.debugLine="btnBackyardRefresh.Enabled = False";
parent.mostCurrent._btnbackyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3473;BA.debugLine="swArmed.Enabled = False";
parent.mostCurrent._swarmed._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3474;BA.debugLine="ivSideYard.Enabled = False";
parent.mostCurrent._ivsideyard.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3475;BA.debugLine="ivFrontYard.Enabled = False";
parent.mostCurrent._ivfrontyard.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3476;BA.debugLine="ivBackyard.Enabled = False";
parent.mostCurrent._ivbackyard.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3477;BA.debugLine="ScrollViewBlink.ScrollToNow(0)";
parent.mostCurrent._scrollviewblink.ScrollToNow((int) (0));
 //BA.debugLineNum = 3478;BA.debugLine="BlurIV(\"Backyard.jpg\",ivBackyard)";
_bluriv("Backyard.jpg",parent.mostCurrent._ivbackyard);
 //BA.debugLineNum = 3479;BA.debugLine="Dim rs As ResumableSub = RefreshCameras(False, \"4";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _refreshcameras(anywheresoftware.b4a.keywords.Common.False,"458236");
 //BA.debugLineNum = 3480;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 1;
return;
case 1:
//C
this.state = -1;
_result = (Object) result[0];
;
 //BA.debugLineNum = 3481;BA.debugLine="btnSideYard.Enabled = True";
parent.mostCurrent._btnsideyard._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3482;BA.debugLine="btnRefresh.Enabled = True";
parent.mostCurrent._btnrefresh._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3483;BA.debugLine="btnSideYardNewClip.Enabled = True";
parent.mostCurrent._btnsideyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3484;BA.debugLine="btnFrontYardNewClip.Enabled = True";
parent.mostCurrent._btnfrontyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3485;BA.debugLine="btnBackyardNewClip.Enabled = True";
parent.mostCurrent._btnbackyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3486;BA.debugLine="btnSideYardRefresh.Enabled = True";
parent.mostCurrent._btnsideyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3487;BA.debugLine="btnFrontYardRefresh.Enabled = True";
parent.mostCurrent._btnfrontyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3488;BA.debugLine="btnBackyardRefresh.Enabled = True";
parent.mostCurrent._btnbackyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3489;BA.debugLine="ivSideYard.Enabled = True";
parent.mostCurrent._ivsideyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3490;BA.debugLine="ivFrontYard.Enabled = True";
parent.mostCurrent._ivfrontyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3491;BA.debugLine="ivBackyard.Enabled = True";
parent.mostCurrent._ivbackyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3492;BA.debugLine="swArmed.Enabled = True";
parent.mostCurrent._swarmed._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3493;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _btnchart_click() throws Exception{
 //BA.debugLineNum = 3706;BA.debugLine="Private Sub btnChart_Click";
 //BA.debugLineNum = 3707;BA.debugLine="StartActivity(\"Chart\")";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)("Chart"));
 //BA.debugLineNum = 3708;BA.debugLine="End Sub";
return "";
}
public static void  _btnfrontyardnewclip_click() throws Exception{
ResumableSub_btnFrontYardNewClip_Click rsub = new ResumableSub_btnFrontYardNewClip_Click(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_btnFrontYardNewClip_Click extends BA.ResumableSub {
public ResumableSub_btnFrontYardNewClip_Click(cloyd.smart.home.monitor.main parent) {
this.parent = parent;
}
cloyd.smart.home.monitor.main parent;
anywheresoftware.b4a.objects.drawable.BitmapDrawable _bd = null;
anywheresoftware.b4a.objects.IntentWrapper _intent1 = null;
anywheresoftware.b4a.phone.Phone _phone = null;
anywheresoftware.b4a.keywords.Common.ResumableSubWrapper _rs = null;
Object _result = null;
int _i = 0;
int step45;
int limit45;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
try {

        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 3161;BA.debugLine="Dim bd As BitmapDrawable";
_bd = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
 //BA.debugLineNum = 3162;BA.debugLine="bd.Initialize(LoadBitmapResize(File.DirAssets, \"0";
_bd.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmapResize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"0.png",anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (32)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (32)),anywheresoftware.b4a.keywords.Common.True).getObject()));
 //BA.debugLineNum = 3163;BA.debugLine="If Msgbox2(\"Capture video from the Front Yard cam";
if (true) break;

case 1:
//if
this.state = 24;
if (anywheresoftware.b4a.keywords.Common.Msgbox2(BA.ObjectToCharSequence("Capture video from the Front Yard camera?"),BA.ObjectToCharSequence("Smart Home Monitor"),"Yes","","No",_bd.getBitmap(),mostCurrent.activityBA)==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
this.state = 3;
}if (true) break;

case 3:
//C
this.state = 4;
 //BA.debugLineNum = 3164;BA.debugLine="Try";
if (true) break;

case 4:
//try
this.state = 23;
this.catchState = 22;
this.state = 6;
if (true) break;

case 6:
//C
this.state = 7;
this.catchState = 22;
 //BA.debugLineNum = 3166;BA.debugLine="Dim Intent1 As Intent";
_intent1 = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 3167;BA.debugLine="Intent1.Initialize(\"blink.liveview.frontyard\",";
_intent1.Initialize("blink.liveview.frontyard","");
 //BA.debugLineNum = 3168;BA.debugLine="Dim Phone As Phone";
_phone = new anywheresoftware.b4a.phone.Phone();
 //BA.debugLineNum = 3169;BA.debugLine="Phone.SendBroadcastIntent(Intent1)";
_phone.SendBroadcastIntent((android.content.Intent)(_intent1.getObject()));
 //BA.debugLineNum = 3171;BA.debugLine="ScrollViewBlink.ScrollToNow(0)";
parent.mostCurrent._scrollviewblink.ScrollToNow((int) (0));
 //BA.debugLineNum = 3172;BA.debugLine="btnSideYard.Enabled = False";
parent.mostCurrent._btnsideyard._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3173;BA.debugLine="btnRefresh.Enabled = False";
parent.mostCurrent._btnrefresh._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3174;BA.debugLine="btnSideYardNewClip.Enabled = False";
parent.mostCurrent._btnsideyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3175;BA.debugLine="btnFrontYardNewClip.Enabled = False";
parent.mostCurrent._btnfrontyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3176;BA.debugLine="btnBackyardNewClip.Enabled = False";
parent.mostCurrent._btnbackyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3177;BA.debugLine="btnSideYardRefresh.Enabled = False";
parent.mostCurrent._btnsideyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3178;BA.debugLine="btnFrontYardRefresh.Enabled = False";
parent.mostCurrent._btnfrontyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3179;BA.debugLine="btnBackyardRefresh.Enabled = False";
parent.mostCurrent._btnbackyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3180;BA.debugLine="swArmed.Enabled = False";
parent.mostCurrent._swarmed._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3181;BA.debugLine="ivSideYard.Enabled = False";
parent.mostCurrent._ivsideyard.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3182;BA.debugLine="ivFrontYard.Enabled = False";
parent.mostCurrent._ivfrontyard.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3183;BA.debugLine="ivBackyard.Enabled = False";
parent.mostCurrent._ivbackyard.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3184;BA.debugLine="lblStatus.Text = \"Capturing a new Front Yard vi";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("Capturing a new Front Yard video clip..."));
 //BA.debugLineNum = 3186;BA.debugLine="Dim rs As ResumableSub = RESTPost(\"https://rest";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _restpost("https://rest-"+parent._userregion+".immedia-semi.com/network/"+parent._networkid+"/camera/236967/clip");
 //BA.debugLineNum = 3187;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 25;
return;
case 25:
//C
this.state = 7;
_result = (Object) result[0];
;
 //BA.debugLineNum = 3188;BA.debugLine="If response.StartsWith(\"ERROR: \") Or response.C";
if (true) break;

case 7:
//if
this.state = 10;
if (parent._response.startsWith("ERROR: ") || parent._response.contains("System is busy, please wait")) { 
this.state = 9;
}if (true) break;

case 9:
//C
this.state = 10;
 //BA.debugLineNum = 3189;BA.debugLine="btnSideYard.Enabled = True";
parent.mostCurrent._btnsideyard._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3190;BA.debugLine="btnRefresh.Enabled = True";
parent.mostCurrent._btnrefresh._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3191;BA.debugLine="btnSideYardNewClip.Enabled = True";
parent.mostCurrent._btnsideyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3192;BA.debugLine="btnFrontYardNewClip.Enabled = True";
parent.mostCurrent._btnfrontyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3193;BA.debugLine="btnBackyardNewClip.Enabled = True";
parent.mostCurrent._btnbackyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3194;BA.debugLine="btnSideYardRefresh.Enabled = True";
parent.mostCurrent._btnsideyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3195;BA.debugLine="btnFrontYardRefresh.Enabled = True";
parent.mostCurrent._btnfrontyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3196;BA.debugLine="btnBackyardRefresh.Enabled = True";
parent.mostCurrent._btnbackyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3197;BA.debugLine="ivSideYard.Enabled = True";
parent.mostCurrent._ivsideyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3198;BA.debugLine="ivFrontYard.Enabled = True";
parent.mostCurrent._ivfrontyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3199;BA.debugLine="ivBackyard.Enabled = True";
parent.mostCurrent._ivbackyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3200;BA.debugLine="swArmed.Enabled = True";
parent.mostCurrent._swarmed._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3201;BA.debugLine="lblStatus.Text = response";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence(parent._response));
 //BA.debugLineNum = 3202;BA.debugLine="Return";
if (true) return ;
 if (true) break;

case 10:
//C
this.state = 11;
;
 //BA.debugLineNum = 3205;BA.debugLine="Dim rs As ResumableSub = GetCommandID(response)";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _getcommandid(parent._response);
 //BA.debugLineNum = 3206;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 26;
return;
case 26:
//C
this.state = 11;
_result = (Object) result[0];
;
 //BA.debugLineNum = 3208;BA.debugLine="Dim rs As ResumableSub = RESTGet(\"https://rest-";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _restget("https://rest-"+parent._userregion+".immedia-semi.com/network/"+parent._networkid+"/command/"+parent._commandid);
 //BA.debugLineNum = 3209;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 27;
return;
case 27:
//C
this.state = 11;
_result = (Object) result[0];
;
 //BA.debugLineNum = 3210;BA.debugLine="For i = 1 To 60";
if (true) break;

case 11:
//for
this.state = 20;
step45 = 1;
limit45 = (int) (60);
_i = (int) (1) ;
this.state = 28;
if (true) break;

case 28:
//C
this.state = 20;
if ((step45 > 0 && _i <= limit45) || (step45 < 0 && _i >= limit45)) this.state = 13;
if (true) break;

case 29:
//C
this.state = 28;
_i = ((int)(0 + _i + step45)) ;
if (true) break;

case 13:
//C
this.state = 14;
 //BA.debugLineNum = 3212;BA.debugLine="Dim rs As ResumableSub = GetCommandStatus(resp";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _getcommandstatus(parent._response);
 //BA.debugLineNum = 3213;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 30;
return;
case 30:
//C
this.state = 14;
_result = (Object) result[0];
;
 //BA.debugLineNum = 3214;BA.debugLine="If commandComplete Then";
if (true) break;

case 14:
//if
this.state = 19;
if (parent._commandcomplete) { 
this.state = 16;
}else {
this.state = 18;
}if (true) break;

case 16:
//C
this.state = 19;
 //BA.debugLineNum = 3215;BA.debugLine="Dim rs As ResumableSub = GetUnwatchedVideos";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _getunwatchedvideos();
 //BA.debugLineNum = 3216;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 31;
return;
case 31:
//C
this.state = 19;
_result = (Object) result[0];
;
 //BA.debugLineNum = 3217;BA.debugLine="Exit";
this.state = 20;
if (true) break;
 if (true) break;

case 18:
//C
this.state = 19;
 //BA.debugLineNum = 3219;BA.debugLine="lblStatus.Text = \"Awaiting for the Front Yard";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("Awaiting for the Front Yard video clip... "+BA.NumberToString(_i)+"/60"));
 if (true) break;

case 19:
//C
this.state = 29;
;
 //BA.debugLineNum = 3222;BA.debugLine="Dim rs As ResumableSub = RESTGet(\"https://rest";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _restget("https://rest-"+parent._userregion+".immedia-semi.com/network/"+parent._networkid+"/command/"+parent._commandid);
 //BA.debugLineNum = 3223;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 32;
return;
case 32:
//C
this.state = 29;
_result = (Object) result[0];
;
 //BA.debugLineNum = 3224;BA.debugLine="Sleep(1000)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (1000));
this.state = 33;
return;
case 33:
//C
this.state = 29;
;
 if (true) break;
if (true) break;

case 20:
//C
this.state = 23;
;
 if (true) break;

case 22:
//C
this.state = 23;
this.catchState = 0;
 //BA.debugLineNum = 3227;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("338731843",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 if (true) break;
if (true) break;

case 23:
//C
this.state = 24;
this.catchState = 0;
;
 //BA.debugLineNum = 3229;BA.debugLine="btnSideYard.Enabled = True";
parent.mostCurrent._btnsideyard._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3230;BA.debugLine="btnRefresh.Enabled = True";
parent.mostCurrent._btnrefresh._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3231;BA.debugLine="btnSideYardNewClip.Enabled = True";
parent.mostCurrent._btnsideyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3232;BA.debugLine="btnFrontYardNewClip.Enabled = True";
parent.mostCurrent._btnfrontyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3233;BA.debugLine="btnBackyardNewClip.Enabled = True";
parent.mostCurrent._btnbackyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3234;BA.debugLine="btnSideYardRefresh.Enabled = True";
parent.mostCurrent._btnsideyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3235;BA.debugLine="btnFrontYardRefresh.Enabled = True";
parent.mostCurrent._btnfrontyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3236;BA.debugLine="btnBackyardRefresh.Enabled = True";
parent.mostCurrent._btnbackyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3237;BA.debugLine="ivSideYard.Enabled = True";
parent.mostCurrent._ivsideyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3238;BA.debugLine="ivFrontYard.Enabled = True";
parent.mostCurrent._ivfrontyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3239;BA.debugLine="ivBackyard.Enabled = True";
parent.mostCurrent._ivbackyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3240;BA.debugLine="swArmed.Enabled = True";
parent.mostCurrent._swarmed._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3241;BA.debugLine="lblStatus.Text = \"Ready\"";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("Ready"));
 if (true) break;

case 24:
//C
this.state = -1;
;
 //BA.debugLineNum = 3243;BA.debugLine="End Sub";
if (true) break;
}} 
       catch (Exception e0) {
			
if (catchState == 0)
    throw e0;
else {
    state = catchState;
processBA.setLastException(e0);}
            }
        }
    }
}
public static void  _btnfrontyardrefresh_click() throws Exception{
ResumableSub_btnFrontYardRefresh_Click rsub = new ResumableSub_btnFrontYardRefresh_Click(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_btnFrontYardRefresh_Click extends BA.ResumableSub {
public ResumableSub_btnFrontYardRefresh_Click(cloyd.smart.home.monitor.main parent) {
this.parent = parent;
}
cloyd.smart.home.monitor.main parent;
anywheresoftware.b4a.keywords.Common.ResumableSubWrapper _rs = null;
Object _result = null;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = -1;
 //BA.debugLineNum = 3496;BA.debugLine="btnSideYard.Enabled = False";
parent.mostCurrent._btnsideyard._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3497;BA.debugLine="btnRefresh.Enabled = False";
parent.mostCurrent._btnrefresh._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3498;BA.debugLine="btnSideYardNewClip.Enabled = False";
parent.mostCurrent._btnsideyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3499;BA.debugLine="btnFrontYardNewClip.Enabled = False";
parent.mostCurrent._btnfrontyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3500;BA.debugLine="btnBackyardNewClip.Enabled = False";
parent.mostCurrent._btnbackyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3501;BA.debugLine="btnSideYardRefresh.Enabled = False";
parent.mostCurrent._btnsideyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3502;BA.debugLine="btnFrontYardRefresh.Enabled = False";
parent.mostCurrent._btnfrontyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3503;BA.debugLine="btnBackyardRefresh.Enabled = False";
parent.mostCurrent._btnbackyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3504;BA.debugLine="swArmed.Enabled = False";
parent.mostCurrent._swarmed._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3505;BA.debugLine="ivSideYard.Enabled = False";
parent.mostCurrent._ivsideyard.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3506;BA.debugLine="ivFrontYard.Enabled = False";
parent.mostCurrent._ivfrontyard.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3507;BA.debugLine="ivBackyard.Enabled = False";
parent.mostCurrent._ivbackyard.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3508;BA.debugLine="ScrollViewBlink.ScrollToNow(0)";
parent.mostCurrent._scrollviewblink.ScrollToNow((int) (0));
 //BA.debugLineNum = 3509;BA.debugLine="BlurIV(\"FrontYard.jpg\",ivFrontYard)";
_bluriv("FrontYard.jpg",parent.mostCurrent._ivfrontyard);
 //BA.debugLineNum = 3510;BA.debugLine="Dim rs As ResumableSub = RefreshCameras(False, \"2";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _refreshcameras(anywheresoftware.b4a.keywords.Common.False,"236967");
 //BA.debugLineNum = 3511;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 1;
return;
case 1:
//C
this.state = -1;
_result = (Object) result[0];
;
 //BA.debugLineNum = 3512;BA.debugLine="btnSideYard.Enabled = True";
parent.mostCurrent._btnsideyard._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3513;BA.debugLine="btnRefresh.Enabled = True";
parent.mostCurrent._btnrefresh._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3514;BA.debugLine="btnSideYardNewClip.Enabled = True";
parent.mostCurrent._btnsideyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3515;BA.debugLine="btnFrontYardNewClip.Enabled = True";
parent.mostCurrent._btnfrontyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3516;BA.debugLine="btnBackyardNewClip.Enabled = True";
parent.mostCurrent._btnbackyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3517;BA.debugLine="btnSideYardRefresh.Enabled = True";
parent.mostCurrent._btnsideyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3518;BA.debugLine="btnFrontYardRefresh.Enabled = True";
parent.mostCurrent._btnfrontyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3519;BA.debugLine="btnBackyardRefresh.Enabled = True";
parent.mostCurrent._btnbackyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3520;BA.debugLine="ivSideYard.Enabled = True";
parent.mostCurrent._ivsideyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3521;BA.debugLine="ivFrontYard.Enabled = True";
parent.mostCurrent._ivfrontyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3522;BA.debugLine="ivBackyard.Enabled = True";
parent.mostCurrent._ivbackyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3523;BA.debugLine="swArmed.Enabled = True";
parent.mostCurrent._swarmed._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3524;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static void  _btnrefresh_click() throws Exception{
ResumableSub_btnRefresh_Click rsub = new ResumableSub_btnRefresh_Click(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_btnRefresh_Click extends BA.ResumableSub {
public ResumableSub_btnRefresh_Click(cloyd.smart.home.monitor.main parent) {
this.parent = parent;
}
cloyd.smart.home.monitor.main parent;
anywheresoftware.b4a.keywords.Common.ResumableSubWrapper _rs = null;
Object _result = null;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = -1;
 //BA.debugLineNum = 3334;BA.debugLine="btnSideYard.Enabled = False";
parent.mostCurrent._btnsideyard._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3335;BA.debugLine="btnRefresh.Enabled = False";
parent.mostCurrent._btnrefresh._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3336;BA.debugLine="btnSideYardNewClip.Enabled = False";
parent.mostCurrent._btnsideyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3337;BA.debugLine="btnFrontYardNewClip.Enabled = False";
parent.mostCurrent._btnfrontyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3338;BA.debugLine="btnBackyardNewClip.Enabled = False";
parent.mostCurrent._btnbackyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3339;BA.debugLine="btnSideYardRefresh.Enabled = False";
parent.mostCurrent._btnsideyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3340;BA.debugLine="btnFrontYardRefresh.Enabled = False";
parent.mostCurrent._btnfrontyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3341;BA.debugLine="btnBackyardRefresh.Enabled = False";
parent.mostCurrent._btnbackyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3342;BA.debugLine="swArmed.Enabled = False";
parent.mostCurrent._swarmed._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3343;BA.debugLine="ivSideYard.Enabled = False";
parent.mostCurrent._ivsideyard.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3344;BA.debugLine="ivFrontYard.Enabled = False";
parent.mostCurrent._ivfrontyard.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3345;BA.debugLine="ivBackyard.Enabled = False";
parent.mostCurrent._ivbackyard.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3346;BA.debugLine="ScrollViewBlink.ScrollToNow(0)";
parent.mostCurrent._scrollviewblink.ScrollToNow((int) (0));
 //BA.debugLineNum = 3347;BA.debugLine="BlurIV(\"SideYard.jpg\",ivSideYard)";
_bluriv("SideYard.jpg",parent.mostCurrent._ivsideyard);
 //BA.debugLineNum = 3348;BA.debugLine="BlurIV(\"FrontYard.jpg\",ivFrontYard)";
_bluriv("FrontYard.jpg",parent.mostCurrent._ivfrontyard);
 //BA.debugLineNum = 3349;BA.debugLine="BlurIV(\"Backyard.jpg\",ivBackyard)";
_bluriv("Backyard.jpg",parent.mostCurrent._ivbackyard);
 //BA.debugLineNum = 3350;BA.debugLine="Dim rs As ResumableSub = RefreshCameras(True, \"Al";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _refreshcameras(anywheresoftware.b4a.keywords.Common.True,"All");
 //BA.debugLineNum = 3351;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 1;
return;
case 1:
//C
this.state = -1;
_result = (Object) result[0];
;
 //BA.debugLineNum = 3352;BA.debugLine="btnSideYard.Enabled = True";
parent.mostCurrent._btnsideyard._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3353;BA.debugLine="btnRefresh.Enabled = True";
parent.mostCurrent._btnrefresh._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3354;BA.debugLine="btnSideYardNewClip.Enabled = True";
parent.mostCurrent._btnsideyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3355;BA.debugLine="btnFrontYardNewClip.Enabled = True";
parent.mostCurrent._btnfrontyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3356;BA.debugLine="btnBackyardNewClip.Enabled = True";
parent.mostCurrent._btnbackyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3357;BA.debugLine="btnSideYardRefresh.Enabled = True";
parent.mostCurrent._btnsideyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3358;BA.debugLine="btnFrontYardRefresh.Enabled = True";
parent.mostCurrent._btnfrontyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3359;BA.debugLine="btnBackyardRefresh.Enabled = True";
parent.mostCurrent._btnbackyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3360;BA.debugLine="ivSideYard.Enabled = True";
parent.mostCurrent._ivsideyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3361;BA.debugLine="ivFrontYard.Enabled = True";
parent.mostCurrent._ivfrontyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3362;BA.debugLine="ivBackyard.Enabled = True";
parent.mostCurrent._ivbackyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3363;BA.debugLine="swArmed.Enabled = True";
parent.mostCurrent._swarmed._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3364;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static void  _btnsideyard_click() throws Exception{
ResumableSub_btnSideYard_Click rsub = new ResumableSub_btnSideYard_Click(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_btnSideYard_Click extends BA.ResumableSub {
public ResumableSub_btnSideYard_Click(cloyd.smart.home.monitor.main parent) {
this.parent = parent;
}
cloyd.smart.home.monitor.main parent;
anywheresoftware.b4a.objects.drawable.BitmapDrawable _bd = null;
anywheresoftware.b4a.keywords.Common.ResumableSubWrapper _rs = null;
Object _result = null;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 2338;BA.debugLine="Dim bd As BitmapDrawable";
_bd = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
 //BA.debugLineNum = 2339;BA.debugLine="bd.Initialize(LoadBitmapResize(File.DirAssets, \"0";
_bd.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmapResize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"0.png",anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (32)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (32)),anywheresoftware.b4a.keywords.Common.True).getObject()));
 //BA.debugLineNum = 2340;BA.debugLine="If Msgbox2(\"Capture new camera thumbnails?\", \"Sma";
if (true) break;

case 1:
//if
this.state = 4;
if (anywheresoftware.b4a.keywords.Common.Msgbox2(BA.ObjectToCharSequence("Capture new camera thumbnails?"),BA.ObjectToCharSequence("Smart Home Monitor"),"Yes","","No",_bd.getBitmap(),mostCurrent.activityBA)==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
this.state = 3;
}if (true) break;

case 3:
//C
this.state = 4;
 //BA.debugLineNum = 2341;BA.debugLine="ScrollViewBlink.ScrollToNow(0)";
parent.mostCurrent._scrollviewblink.ScrollToNow((int) (0));
 //BA.debugLineNum = 2342;BA.debugLine="btnSideYard.Enabled = False";
parent.mostCurrent._btnsideyard._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2343;BA.debugLine="btnRefresh.Enabled = False";
parent.mostCurrent._btnrefresh._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2344;BA.debugLine="btnSideYardNewClip.Enabled = False";
parent.mostCurrent._btnsideyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2345;BA.debugLine="btnFrontYardNewClip.Enabled = False";
parent.mostCurrent._btnfrontyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2346;BA.debugLine="btnBackyardNewClip.Enabled = False";
parent.mostCurrent._btnbackyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2347;BA.debugLine="btnSideYardRefresh.Enabled = False";
parent.mostCurrent._btnsideyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2348;BA.debugLine="btnFrontYardRefresh.Enabled = False";
parent.mostCurrent._btnfrontyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2349;BA.debugLine="btnBackyardRefresh.Enabled = False";
parent.mostCurrent._btnbackyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2350;BA.debugLine="swArmed.Enabled = False";
parent.mostCurrent._swarmed._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2351;BA.debugLine="ivSideYard.Enabled = False";
parent.mostCurrent._ivsideyard.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2352;BA.debugLine="ivFrontYard.Enabled = False";
parent.mostCurrent._ivfrontyard.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2353;BA.debugLine="ivBackyard.Enabled = False";
parent.mostCurrent._ivbackyard.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2354;BA.debugLine="BlurIV(\"SideYard.jpg\",ivSideYard)";
_bluriv("SideYard.jpg",parent.mostCurrent._ivsideyard);
 //BA.debugLineNum = 2355;BA.debugLine="BlurIV(\"FrontYard.jpg\",ivFrontYard)";
_bluriv("FrontYard.jpg",parent.mostCurrent._ivfrontyard);
 //BA.debugLineNum = 2356;BA.debugLine="BlurIV(\"Backyard.jpg\",ivBackyard)";
_bluriv("Backyard.jpg",parent.mostCurrent._ivbackyard);
 //BA.debugLineNum = 2360;BA.debugLine="Dim rs As ResumableSub = RefreshCameras(False, \"";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _refreshcameras(anywheresoftware.b4a.keywords.Common.False,"347574");
 //BA.debugLineNum = 2361;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 5;
return;
case 5:
//C
this.state = 4;
_result = (Object) result[0];
;
 //BA.debugLineNum = 2363;BA.debugLine="Dim rs As ResumableSub = RefreshCameras(False, \"";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _refreshcameras(anywheresoftware.b4a.keywords.Common.False,"236967");
 //BA.debugLineNum = 2364;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 6;
return;
case 6:
//C
this.state = 4;
_result = (Object) result[0];
;
 //BA.debugLineNum = 2366;BA.debugLine="Dim rs As ResumableSub = RefreshCameras(False, \"";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _refreshcameras(anywheresoftware.b4a.keywords.Common.False,"458236");
 //BA.debugLineNum = 2367;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 7;
return;
case 7:
//C
this.state = 4;
_result = (Object) result[0];
;
 //BA.debugLineNum = 2369;BA.debugLine="btnSideYard.Enabled = True";
parent.mostCurrent._btnsideyard._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2370;BA.debugLine="btnRefresh.Enabled = True";
parent.mostCurrent._btnrefresh._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2371;BA.debugLine="btnSideYardNewClip.Enabled = True";
parent.mostCurrent._btnsideyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2372;BA.debugLine="btnFrontYardNewClip.Enabled = True";
parent.mostCurrent._btnfrontyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2373;BA.debugLine="btnBackyardNewClip.Enabled = True";
parent.mostCurrent._btnbackyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2374;BA.debugLine="btnSideYardRefresh.Enabled = True";
parent.mostCurrent._btnsideyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2375;BA.debugLine="btnFrontYardRefresh.Enabled = True";
parent.mostCurrent._btnfrontyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2376;BA.debugLine="btnBackyardRefresh.Enabled = True";
parent.mostCurrent._btnbackyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2377;BA.debugLine="ivSideYard.Enabled = True";
parent.mostCurrent._ivsideyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2378;BA.debugLine="ivFrontYard.Enabled = True";
parent.mostCurrent._ivfrontyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2379;BA.debugLine="ivBackyard.Enabled = True";
parent.mostCurrent._ivbackyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2380;BA.debugLine="swArmed.Enabled = True";
parent.mostCurrent._swarmed._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 if (true) break;

case 4:
//C
this.state = -1;
;
 //BA.debugLineNum = 2382;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static void  _btnsideyardnewclip_click() throws Exception{
ResumableSub_btnSideYardNewClip_Click rsub = new ResumableSub_btnSideYardNewClip_Click(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_btnSideYardNewClip_Click extends BA.ResumableSub {
public ResumableSub_btnSideYardNewClip_Click(cloyd.smart.home.monitor.main parent) {
this.parent = parent;
}
cloyd.smart.home.monitor.main parent;
anywheresoftware.b4a.objects.drawable.BitmapDrawable _bd = null;
anywheresoftware.b4a.objects.IntentWrapper _intent1 = null;
anywheresoftware.b4a.phone.Phone _phone = null;
anywheresoftware.b4a.keywords.Common.ResumableSubWrapper _rs = null;
Object _result = null;
int _i = 0;
int step44;
int limit44;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
try {

        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 3072;BA.debugLine="Dim bd As BitmapDrawable";
_bd = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
 //BA.debugLineNum = 3073;BA.debugLine="bd.Initialize(LoadBitmapResize(File.DirAssets, \"0";
_bd.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmapResize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"0.png",anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (32)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (32)),anywheresoftware.b4a.keywords.Common.True).getObject()));
 //BA.debugLineNum = 3074;BA.debugLine="If Msgbox2(\"Capture video from the Side Yard came";
if (true) break;

case 1:
//if
this.state = 24;
if (anywheresoftware.b4a.keywords.Common.Msgbox2(BA.ObjectToCharSequence("Capture video from the Side Yard camera?"),BA.ObjectToCharSequence("Smart Home Monitor"),"Yes","","No",_bd.getBitmap(),mostCurrent.activityBA)==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
this.state = 3;
}if (true) break;

case 3:
//C
this.state = 4;
 //BA.debugLineNum = 3075;BA.debugLine="Try";
if (true) break;

case 4:
//try
this.state = 23;
this.catchState = 22;
this.state = 6;
if (true) break;

case 6:
//C
this.state = 7;
this.catchState = 22;
 //BA.debugLineNum = 3077;BA.debugLine="Dim Intent1 As Intent";
_intent1 = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 3078;BA.debugLine="Intent1.Initialize(\"blink.liveview.sideyard\", \"";
_intent1.Initialize("blink.liveview.sideyard","");
 //BA.debugLineNum = 3079;BA.debugLine="Dim Phone As Phone";
_phone = new anywheresoftware.b4a.phone.Phone();
 //BA.debugLineNum = 3080;BA.debugLine="Phone.SendBroadcastIntent(Intent1)";
_phone.SendBroadcastIntent((android.content.Intent)(_intent1.getObject()));
 //BA.debugLineNum = 3082;BA.debugLine="btnSideYard.Enabled = False";
parent.mostCurrent._btnsideyard._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3083;BA.debugLine="btnRefresh.Enabled = False";
parent.mostCurrent._btnrefresh._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3084;BA.debugLine="btnSideYardNewClip.Enabled = False";
parent.mostCurrent._btnsideyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3085;BA.debugLine="btnFrontYardNewClip.Enabled = False";
parent.mostCurrent._btnfrontyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3086;BA.debugLine="btnBackyardNewClip.Enabled = False";
parent.mostCurrent._btnbackyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3087;BA.debugLine="btnSideYardRefresh.Enabled = False";
parent.mostCurrent._btnsideyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3088;BA.debugLine="btnFrontYardRefresh.Enabled = False";
parent.mostCurrent._btnfrontyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3089;BA.debugLine="btnBackyardRefresh.Enabled = False";
parent.mostCurrent._btnbackyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3090;BA.debugLine="swArmed.Enabled = False";
parent.mostCurrent._swarmed._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3091;BA.debugLine="ivSideYard.Enabled = False";
parent.mostCurrent._ivsideyard.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3092;BA.debugLine="ivFrontYard.Enabled = False";
parent.mostCurrent._ivfrontyard.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3093;BA.debugLine="ivBackyard.Enabled = False";
parent.mostCurrent._ivbackyard.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3094;BA.debugLine="lblStatus.Text = \"Capturing a new Side Yard vid";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("Capturing a new Side Yard video clip..."));
 //BA.debugLineNum = 3096;BA.debugLine="Dim rs As ResumableSub = RESTPost(\"https://rest";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _restpost("https://rest-"+parent._userregion+".immedia-semi.com/network/"+parent._networkid+"/camera/347574/clip");
 //BA.debugLineNum = 3097;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 25;
return;
case 25:
//C
this.state = 7;
_result = (Object) result[0];
;
 //BA.debugLineNum = 3098;BA.debugLine="If response.StartsWith(\"ERROR: \") Or response.C";
if (true) break;

case 7:
//if
this.state = 10;
if (parent._response.startsWith("ERROR: ") || parent._response.contains("System is busy, please wait")) { 
this.state = 9;
}if (true) break;

case 9:
//C
this.state = 10;
 //BA.debugLineNum = 3099;BA.debugLine="btnSideYard.Enabled = True";
parent.mostCurrent._btnsideyard._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3100;BA.debugLine="btnRefresh.Enabled = True";
parent.mostCurrent._btnrefresh._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3101;BA.debugLine="btnSideYardNewClip.Enabled = True";
parent.mostCurrent._btnsideyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3102;BA.debugLine="btnFrontYardNewClip.Enabled = True";
parent.mostCurrent._btnfrontyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3103;BA.debugLine="btnBackyardNewClip.Enabled = True";
parent.mostCurrent._btnbackyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3104;BA.debugLine="btnSideYardRefresh.Enabled = True";
parent.mostCurrent._btnsideyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3105;BA.debugLine="btnFrontYardRefresh.Enabled = True";
parent.mostCurrent._btnfrontyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3106;BA.debugLine="btnBackyardRefresh.Enabled = True";
parent.mostCurrent._btnbackyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3107;BA.debugLine="ivSideYard.Enabled = True";
parent.mostCurrent._ivsideyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3108;BA.debugLine="ivFrontYard.Enabled = True";
parent.mostCurrent._ivfrontyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3109;BA.debugLine="ivBackyard.Enabled = True";
parent.mostCurrent._ivbackyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3110;BA.debugLine="swArmed.Enabled = True";
parent.mostCurrent._swarmed._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3111;BA.debugLine="lblStatus.Text = response";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence(parent._response));
 //BA.debugLineNum = 3112;BA.debugLine="Return";
if (true) return ;
 if (true) break;

case 10:
//C
this.state = 11;
;
 //BA.debugLineNum = 3115;BA.debugLine="Dim rs As ResumableSub = GetCommandID(response)";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _getcommandid(parent._response);
 //BA.debugLineNum = 3116;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 26;
return;
case 26:
//C
this.state = 11;
_result = (Object) result[0];
;
 //BA.debugLineNum = 3118;BA.debugLine="Dim rs As ResumableSub = RESTGet(\"https://rest-";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _restget("https://rest-"+parent._userregion+".immedia-semi.com/network/"+parent._networkid+"/command/"+parent._commandid);
 //BA.debugLineNum = 3119;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 27;
return;
case 27:
//C
this.state = 11;
_result = (Object) result[0];
;
 //BA.debugLineNum = 3121;BA.debugLine="For i = 1 To 60";
if (true) break;

case 11:
//for
this.state = 20;
step44 = 1;
limit44 = (int) (60);
_i = (int) (1) ;
this.state = 28;
if (true) break;

case 28:
//C
this.state = 20;
if ((step44 > 0 && _i <= limit44) || (step44 < 0 && _i >= limit44)) this.state = 13;
if (true) break;

case 29:
//C
this.state = 28;
_i = ((int)(0 + _i + step44)) ;
if (true) break;

case 13:
//C
this.state = 14;
 //BA.debugLineNum = 3123;BA.debugLine="Dim rs As ResumableSub = GetCommandStatus(resp";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _getcommandstatus(parent._response);
 //BA.debugLineNum = 3124;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 30;
return;
case 30:
//C
this.state = 14;
_result = (Object) result[0];
;
 //BA.debugLineNum = 3125;BA.debugLine="If commandComplete Then";
if (true) break;

case 14:
//if
this.state = 19;
if (parent._commandcomplete) { 
this.state = 16;
}else {
this.state = 18;
}if (true) break;

case 16:
//C
this.state = 19;
 //BA.debugLineNum = 3130;BA.debugLine="Dim rs As ResumableSub = GetUnwatchedVideos";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _getunwatchedvideos();
 //BA.debugLineNum = 3131;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 31;
return;
case 31:
//C
this.state = 19;
_result = (Object) result[0];
;
 //BA.debugLineNum = 3132;BA.debugLine="Exit";
this.state = 20;
if (true) break;
 if (true) break;

case 18:
//C
this.state = 19;
 //BA.debugLineNum = 3134;BA.debugLine="lblStatus.Text = \"Awaiting for the Side Yard";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("Awaiting for the Side Yard video clip... "+BA.NumberToString(_i)+"/60"));
 if (true) break;

case 19:
//C
this.state = 29;
;
 //BA.debugLineNum = 3137;BA.debugLine="Dim rs As ResumableSub = RESTGet(\"https://rest";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _restget("https://rest-"+parent._userregion+".immedia-semi.com/network/"+parent._networkid+"/command/"+parent._commandid);
 //BA.debugLineNum = 3138;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 32;
return;
case 32:
//C
this.state = 29;
_result = (Object) result[0];
;
 //BA.debugLineNum = 3139;BA.debugLine="Sleep(1000)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (1000));
this.state = 33;
return;
case 33:
//C
this.state = 29;
;
 if (true) break;
if (true) break;

case 20:
//C
this.state = 23;
;
 if (true) break;

case 22:
//C
this.state = 23;
this.catchState = 0;
 //BA.debugLineNum = 3142;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("338666311",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 if (true) break;
if (true) break;

case 23:
//C
this.state = 24;
this.catchState = 0;
;
 //BA.debugLineNum = 3144;BA.debugLine="btnSideYard.Enabled = True";
parent.mostCurrent._btnsideyard._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3145;BA.debugLine="btnRefresh.Enabled = True";
parent.mostCurrent._btnrefresh._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3146;BA.debugLine="btnSideYardNewClip.Enabled = True";
parent.mostCurrent._btnsideyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3147;BA.debugLine="btnFrontYardNewClip.Enabled = True";
parent.mostCurrent._btnfrontyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3148;BA.debugLine="btnBackyardNewClip.Enabled = True";
parent.mostCurrent._btnbackyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3149;BA.debugLine="btnSideYardRefresh.Enabled = True";
parent.mostCurrent._btnsideyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3150;BA.debugLine="btnFrontYardRefresh.Enabled = True";
parent.mostCurrent._btnfrontyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3151;BA.debugLine="btnBackyardRefresh.Enabled = True";
parent.mostCurrent._btnbackyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3152;BA.debugLine="ivSideYard.Enabled = True";
parent.mostCurrent._ivsideyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3153;BA.debugLine="ivFrontYard.Enabled = True";
parent.mostCurrent._ivfrontyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3154;BA.debugLine="ivBackyard.Enabled = True";
parent.mostCurrent._ivbackyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3155;BA.debugLine="swArmed.Enabled = True";
parent.mostCurrent._swarmed._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3156;BA.debugLine="lblStatus.Text = \"Ready\"";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("Ready"));
 if (true) break;

case 24:
//C
this.state = -1;
;
 //BA.debugLineNum = 3158;BA.debugLine="End Sub";
if (true) break;
}} 
       catch (Exception e0) {
			
if (catchState == 0)
    throw e0;
else {
    state = catchState;
processBA.setLastException(e0);}
            }
        }
    }
}
public static void  _btnsideyardrefresh_click() throws Exception{
ResumableSub_btnSideYardRefresh_Click rsub = new ResumableSub_btnSideYardRefresh_Click(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_btnSideYardRefresh_Click extends BA.ResumableSub {
public ResumableSub_btnSideYardRefresh_Click(cloyd.smart.home.monitor.main parent) {
this.parent = parent;
}
cloyd.smart.home.monitor.main parent;
anywheresoftware.b4a.keywords.Common.ResumableSubWrapper _rs = null;
Object _result = null;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = -1;
 //BA.debugLineNum = 3527;BA.debugLine="btnSideYard.Enabled = False";
parent.mostCurrent._btnsideyard._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3528;BA.debugLine="btnRefresh.Enabled = False";
parent.mostCurrent._btnrefresh._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3529;BA.debugLine="btnSideYardNewClip.Enabled = False";
parent.mostCurrent._btnsideyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3530;BA.debugLine="btnFrontYardNewClip.Enabled = False";
parent.mostCurrent._btnfrontyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3531;BA.debugLine="btnBackyardNewClip.Enabled = False";
parent.mostCurrent._btnbackyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3532;BA.debugLine="btnSideYardRefresh.Enabled = False";
parent.mostCurrent._btnsideyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3533;BA.debugLine="btnFrontYardRefresh.Enabled = False";
parent.mostCurrent._btnfrontyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3534;BA.debugLine="btnBackyardRefresh.Enabled = False";
parent.mostCurrent._btnbackyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3535;BA.debugLine="swArmed.Enabled = False";
parent.mostCurrent._swarmed._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3536;BA.debugLine="ivSideYard.Enabled = False";
parent.mostCurrent._ivsideyard.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3537;BA.debugLine="ivFrontYard.Enabled = False";
parent.mostCurrent._ivfrontyard.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3538;BA.debugLine="ivBackyard.Enabled = False";
parent.mostCurrent._ivbackyard.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3539;BA.debugLine="ScrollViewBlink.ScrollToNow(0)";
parent.mostCurrent._scrollviewblink.ScrollToNow((int) (0));
 //BA.debugLineNum = 3540;BA.debugLine="BlurIV(\"SideYard.jpg\",ivSideYard)";
_bluriv("SideYard.jpg",parent.mostCurrent._ivsideyard);
 //BA.debugLineNum = 3541;BA.debugLine="Dim rs As ResumableSub = RefreshCameras(False, \"3";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _refreshcameras(anywheresoftware.b4a.keywords.Common.False,"347574");
 //BA.debugLineNum = 3542;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 1;
return;
case 1:
//C
this.state = -1;
_result = (Object) result[0];
;
 //BA.debugLineNum = 3543;BA.debugLine="btnSideYard.Enabled = True";
parent.mostCurrent._btnsideyard._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3544;BA.debugLine="btnRefresh.Enabled = True";
parent.mostCurrent._btnrefresh._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3545;BA.debugLine="btnSideYardNewClip.Enabled = True";
parent.mostCurrent._btnsideyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3546;BA.debugLine="btnFrontYardNewClip.Enabled = True";
parent.mostCurrent._btnfrontyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3547;BA.debugLine="btnBackyardNewClip.Enabled = True";
parent.mostCurrent._btnbackyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3548;BA.debugLine="btnSideYardRefresh.Enabled = True";
parent.mostCurrent._btnsideyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3549;BA.debugLine="btnFrontYardRefresh.Enabled = True";
parent.mostCurrent._btnfrontyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3550;BA.debugLine="btnBackyardRefresh.Enabled = True";
parent.mostCurrent._btnbackyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3551;BA.debugLine="ivSideYard.Enabled = True";
parent.mostCurrent._ivsideyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3552;BA.debugLine="ivFrontYard.Enabled = True";
parent.mostCurrent._ivfrontyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3553;BA.debugLine="ivBackyard.Enabled = True";
parent.mostCurrent._ivbackyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3554;BA.debugLine="swArmed.Enabled = True";
parent.mostCurrent._swarmed._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3555;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _checkairqualitysetting() throws Exception{
anywheresoftware.b4a.objects.CSBuilder _cs = null;
String _status = "";
String[] _a = null;
long _tomorrow = 0L;
long _ticks = 0L;
long _lngticks = 0L;
b4a.example.dateutils._period _p = null;
 //BA.debugLineNum = 991;BA.debugLine="Sub CheckAirQualitySetting";
 //BA.debugLineNum = 992;BA.debugLine="Dim cs As CSBuilder";
_cs = new anywheresoftware.b4a.objects.CSBuilder();
 //BA.debugLineNum = 993;BA.debugLine="cs.Initialize";
_cs.Initialize();
 //BA.debugLineNum = 994;BA.debugLine="Try";
try { //BA.debugLineNum = 995;BA.debugLine="Dim status As String";
_status = "";
 //BA.debugLineNum = 996;BA.debugLine="status = StateManager.GetSetting(\"AirQuality\")";
_status = mostCurrent._statemanager._getsetting /*String*/ (mostCurrent.activityBA,"AirQuality");
 //BA.debugLineNum = 997;BA.debugLine="status = status.Replace(\"|24:\",\"|00:\")";
_status = _status.replace("|24:","|00:");
 //BA.debugLineNum = 998;BA.debugLine="Dim a() As String = Regex.Split(\"\\|\",status)";
_a = anywheresoftware.b4a.keywords.Common.Regex.Split("\\|",_status);
 //BA.debugLineNum = 999;BA.debugLine="If a.Length = 3 Then";
if (_a.length==3) { 
 //BA.debugLineNum = 1000;BA.debugLine="If IsNumber(a(0)) And a(0) > 0 Then";
if (anywheresoftware.b4a.keywords.Common.IsNumber(_a[(int) (0)]) && (double)(Double.parseDouble(_a[(int) (0)]))>0) { 
 //BA.debugLineNum = 1001;BA.debugLine="GaugeAirQuality.CurrentValue = (a(0)/10)";
mostCurrent._gaugeairquality._setcurrentvalue /*float*/ ((float) (((double)(Double.parseDouble(_a[(int) (0)]))/(double)10)));
 //BA.debugLineNum = 1002;BA.debugLine="If (a(0)/10) > 40 Then";
if (((double)(Double.parseDouble(_a[(int) (0)]))/(double)10)>40) { 
 //BA.debugLineNum = 1003;BA.debugLine="lblAirQuality.Text = cs.Initialize.Bold.Appen";
mostCurrent._lblairquality.setText(BA.ObjectToCharSequence(_cs.Initialize().Bold().Append(BA.ObjectToCharSequence("Air Quality: ")).Pop().Color(anywheresoftware.b4a.keywords.Common.Colors.Red).Append(BA.ObjectToCharSequence(_getairquality((int) (((double)(Double.parseDouble(_a[(int) (0)]))/(double)10))))).PopAll().getObject()));
 }else {
 //BA.debugLineNum = 1005;BA.debugLine="lblAirQuality.Text = cs.Initialize.Bold.Appen";
mostCurrent._lblairquality.setText(BA.ObjectToCharSequence(_cs.Initialize().Bold().Append(BA.ObjectToCharSequence("Air Quality: ")).Pop().Append(BA.ObjectToCharSequence(_getairquality((int) (((double)(Double.parseDouble(_a[(int) (0)]))/(double)10))))).PopAll().getObject()));
 };
 //BA.debugLineNum = 1007;BA.debugLine="If a(1) = \"\" Then";
if ((_a[(int) (1)]).equals("")) { 
 //BA.debugLineNum = 1008;BA.debugLine="Dim Tomorrow As Long";
_tomorrow = 0L;
 //BA.debugLineNum = 1009;BA.debugLine="Tomorrow = DateTime.add(DateTime.Now, 0, 0, 1";
_tomorrow = anywheresoftware.b4a.keywords.Common.DateTime.Add(anywheresoftware.b4a.keywords.Common.DateTime.getNow(),(int) (0),(int) (0),(int) (1));
 //BA.debugLineNum = 1010;BA.debugLine="DateTime.DateFormat = \"yy-MM-dd\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("yy-MM-dd");
 //BA.debugLineNum = 1011;BA.debugLine="a(1) = DateTime.Date(Tomorrow)";
_a[(int) (1)] = anywheresoftware.b4a.keywords.Common.DateTime.Date(_tomorrow);
 };
 //BA.debugLineNum = 1013;BA.debugLine="If a(2).Contains(\"|24:\") Then";
if (_a[(int) (2)].contains("|24:")) { 
 //BA.debugLineNum = 1014;BA.debugLine="a(2) = a(2).Replace(\"|24:\",\"|00:\")";
_a[(int) (2)] = _a[(int) (2)].replace("|24:","|00:");
 //BA.debugLineNum = 1015;BA.debugLine="Dim Tomorrow As Long";
_tomorrow = 0L;
 //BA.debugLineNum = 1016;BA.debugLine="Tomorrow = DateTime.add(DateTime.Now, 0, 0, 1";
_tomorrow = anywheresoftware.b4a.keywords.Common.DateTime.Add(anywheresoftware.b4a.keywords.Common.DateTime.getNow(),(int) (0),(int) (0),(int) (1));
 //BA.debugLineNum = 1017;BA.debugLine="DateTime.DateFormat = \"yy-MM-dd\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("yy-MM-dd");
 //BA.debugLineNum = 1018;BA.debugLine="a(2) = DateTime.Date(Tomorrow)";
_a[(int) (2)] = anywheresoftware.b4a.keywords.Common.DateTime.Date(_tomorrow);
 };
 //BA.debugLineNum = 1020;BA.debugLine="DateTime.DateFormat = \"yy-MM-dd HH:mm:ss z\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("yy-MM-dd HH:mm:ss z");
 //BA.debugLineNum = 1021;BA.debugLine="Dim ticks As Long = DateTime.DateParse(a(1) &";
_ticks = anywheresoftware.b4a.keywords.Common.DateTime.DateParse(_a[(int) (1)]+" "+_a[(int) (2)]+" GMT");
 //BA.debugLineNum = 1022;BA.debugLine="DateTime.DateFormat = \"MMM d, yyyy h:mm:ss a z";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("MMM d, yyyy h:mm:ss a z");
 //BA.debugLineNum = 1023;BA.debugLine="Dim lngTicks As Long = ticks";
_lngticks = _ticks;
 //BA.debugLineNum = 1024;BA.debugLine="Dim p As Period = DateUtils.PeriodBetween(lngT";
_p = mostCurrent._dateutils._periodbetween(mostCurrent.activityBA,_lngticks,anywheresoftware.b4a.keywords.Common.DateTime.getNow());
 //BA.debugLineNum = 1026;BA.debugLine="If p.Minutes > = 5 Then";
if (_p.Minutes>=5) { 
 //BA.debugLineNum = 1027;BA.debugLine="lblAirQualityLastUpdate.Text = cs.Initialize.";
mostCurrent._lblairqualitylastupdate.setText(BA.ObjectToCharSequence(_cs.Initialize().Bold().Append(BA.ObjectToCharSequence("Last update: ")).Pop().Color(anywheresoftware.b4a.keywords.Common.Colors.Red).Append(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.DateTime.Date(_lngticks))).PopAll().getObject()));
 }else {
 //BA.debugLineNum = 1029;BA.debugLine="lblAirQualityLastUpdate.Text = cs.Initialize.";
mostCurrent._lblairqualitylastupdate.setText(BA.ObjectToCharSequence(_cs.Initialize().Bold().Append(BA.ObjectToCharSequence("Last update: ")).Pop().Append(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.DateTime.Date(_lngticks))).PopAll().getObject()));
 };
 }else if((_a[(int) (2)]).equals("00:00:00")) { 
 //BA.debugLineNum = 1032;BA.debugLine="lblAirQualityLastUpdate.Text = cs.Initialize.B";
mostCurrent._lblairqualitylastupdate.setText(BA.ObjectToCharSequence(_cs.Initialize().Bold().Append(BA.ObjectToCharSequence("Last update: ")).Pop().Color(anywheresoftware.b4a.keywords.Common.Colors.Red).Append(BA.ObjectToCharSequence(mostCurrent._lblairqualitylastupdate.getText().replace("Last update: ",""))).PopAll().getObject()));
 };
 };
 } 
       catch (Exception e44) {
			processBA.setLastException(e44); //BA.debugLineNum = 1036;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("335913773",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 //BA.debugLineNum = 1037;BA.debugLine="lblAirQualityLastUpdate.Text = cs.Initialize.Bol";
mostCurrent._lblairqualitylastupdate.setText(BA.ObjectToCharSequence(_cs.Initialize().Bold().Append(BA.ObjectToCharSequence("Exception: ")).Pop().Color(anywheresoftware.b4a.keywords.Common.Colors.Red).Append(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getObject())).PopAll().getObject()));
 };
 //BA.debugLineNum = 1039;BA.debugLine="End Sub";
return "";
}
public static String  _checkairqualitysettingbasement() throws Exception{
anywheresoftware.b4a.objects.CSBuilder _cs = null;
String _status = "";
String[] _a = null;
long _tomorrow = 0L;
long _ticks = 0L;
long _lngticks = 0L;
b4a.example.dateutils._period _p = null;
 //BA.debugLineNum = 1041;BA.debugLine="Sub CheckAirQualitySettingBasement";
 //BA.debugLineNum = 1042;BA.debugLine="Dim cs As CSBuilder";
_cs = new anywheresoftware.b4a.objects.CSBuilder();
 //BA.debugLineNum = 1043;BA.debugLine="cs.Initialize";
_cs.Initialize();
 //BA.debugLineNum = 1044;BA.debugLine="Try";
try { //BA.debugLineNum = 1045;BA.debugLine="Dim status As String";
_status = "";
 //BA.debugLineNum = 1046;BA.debugLine="status = StateManager.GetSetting(\"AirQualityBase";
_status = mostCurrent._statemanager._getsetting /*String*/ (mostCurrent.activityBA,"AirQualityBasement");
 //BA.debugLineNum = 1047;BA.debugLine="status = status.Replace(\"|24:\",\"|00:\")";
_status = _status.replace("|24:","|00:");
 //BA.debugLineNum = 1048;BA.debugLine="Dim a() As String = Regex.Split(\"\\|\",status)";
_a = anywheresoftware.b4a.keywords.Common.Regex.Split("\\|",_status);
 //BA.debugLineNum = 1049;BA.debugLine="If a.Length = 3 Then";
if (_a.length==3) { 
 //BA.debugLineNum = 1050;BA.debugLine="If IsNumber(a(0)) And a(0) > 0 Then";
if (anywheresoftware.b4a.keywords.Common.IsNumber(_a[(int) (0)]) && (double)(Double.parseDouble(_a[(int) (0)]))>0) { 
 //BA.debugLineNum = 1051;BA.debugLine="GaugeAirQualityBasement.CurrentValue = (a(0)/1";
mostCurrent._gaugeairqualitybasement._setcurrentvalue /*float*/ ((float) (((double)(Double.parseDouble(_a[(int) (0)]))/(double)10)));
 //BA.debugLineNum = 1052;BA.debugLine="If (a(0)/10) > 40 Then";
if (((double)(Double.parseDouble(_a[(int) (0)]))/(double)10)>40) { 
 //BA.debugLineNum = 1053;BA.debugLine="lblAirQualityBasement.Text = cs.Initialize.Bo";
mostCurrent._lblairqualitybasement.setText(BA.ObjectToCharSequence(_cs.Initialize().Bold().Append(BA.ObjectToCharSequence("Air Quality: ")).Pop().Color(anywheresoftware.b4a.keywords.Common.Colors.Red).Append(BA.ObjectToCharSequence(_getairquality((int) (((double)(Double.parseDouble(_a[(int) (0)]))/(double)10))))).PopAll().getObject()));
 }else {
 //BA.debugLineNum = 1055;BA.debugLine="lblAirQualityBasement.Text = cs.Initialize.Bo";
mostCurrent._lblairqualitybasement.setText(BA.ObjectToCharSequence(_cs.Initialize().Bold().Append(BA.ObjectToCharSequence("Air Quality: ")).Pop().Append(BA.ObjectToCharSequence(_getairquality((int) (((double)(Double.parseDouble(_a[(int) (0)]))/(double)10))))).PopAll().getObject()));
 };
 //BA.debugLineNum = 1057;BA.debugLine="If a(1) = \"\" Then";
if ((_a[(int) (1)]).equals("")) { 
 //BA.debugLineNum = 1058;BA.debugLine="Dim Tomorrow As Long";
_tomorrow = 0L;
 //BA.debugLineNum = 1059;BA.debugLine="Tomorrow = DateTime.add(DateTime.Now, 0, 0, 1";
_tomorrow = anywheresoftware.b4a.keywords.Common.DateTime.Add(anywheresoftware.b4a.keywords.Common.DateTime.getNow(),(int) (0),(int) (0),(int) (1));
 //BA.debugLineNum = 1060;BA.debugLine="DateTime.DateFormat = \"yy-MM-dd\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("yy-MM-dd");
 //BA.debugLineNum = 1061;BA.debugLine="a(1) = DateTime.Date(Tomorrow)";
_a[(int) (1)] = anywheresoftware.b4a.keywords.Common.DateTime.Date(_tomorrow);
 };
 //BA.debugLineNum = 1063;BA.debugLine="If a(2).Contains(\"|24:\") Then";
if (_a[(int) (2)].contains("|24:")) { 
 //BA.debugLineNum = 1064;BA.debugLine="a(2) = a(2).Replace(\"|24:\",\"|00:\")";
_a[(int) (2)] = _a[(int) (2)].replace("|24:","|00:");
 //BA.debugLineNum = 1065;BA.debugLine="Dim Tomorrow As Long";
_tomorrow = 0L;
 //BA.debugLineNum = 1066;BA.debugLine="Tomorrow = DateTime.add(DateTime.Now, 0, 0, 1";
_tomorrow = anywheresoftware.b4a.keywords.Common.DateTime.Add(anywheresoftware.b4a.keywords.Common.DateTime.getNow(),(int) (0),(int) (0),(int) (1));
 //BA.debugLineNum = 1067;BA.debugLine="DateTime.DateFormat = \"yy-MM-dd\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("yy-MM-dd");
 //BA.debugLineNum = 1068;BA.debugLine="a(2) = DateTime.Date(Tomorrow)";
_a[(int) (2)] = anywheresoftware.b4a.keywords.Common.DateTime.Date(_tomorrow);
 };
 //BA.debugLineNum = 1071;BA.debugLine="DateTime.DateFormat = \"yy-MM-dd HH:mm:ss z\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("yy-MM-dd HH:mm:ss z");
 //BA.debugLineNum = 1072;BA.debugLine="Dim ticks As Long = DateTime.DateParse(a(1) &";
_ticks = anywheresoftware.b4a.keywords.Common.DateTime.DateParse(_a[(int) (1)]+" "+_a[(int) (2)]+" GMT");
 //BA.debugLineNum = 1073;BA.debugLine="DateTime.DateFormat = \"MMM d, yyyy h:mm:ss a z";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("MMM d, yyyy h:mm:ss a z");
 //BA.debugLineNum = 1074;BA.debugLine="Dim lngTicks As Long = ticks";
_lngticks = _ticks;
 //BA.debugLineNum = 1075;BA.debugLine="Dim p As Period = DateUtils.PeriodBetween(lngT";
_p = mostCurrent._dateutils._periodbetween(mostCurrent.activityBA,_lngticks,anywheresoftware.b4a.keywords.Common.DateTime.getNow());
 //BA.debugLineNum = 1077;BA.debugLine="If p.Minutes > = 5 Then";
if (_p.Minutes>=5) { 
 //BA.debugLineNum = 1078;BA.debugLine="lblAirQualityLastUpdateBasement.Text = cs.Ini";
mostCurrent._lblairqualitylastupdatebasement.setText(BA.ObjectToCharSequence(_cs.Initialize().Bold().Append(BA.ObjectToCharSequence("Last update: ")).Pop().Color(anywheresoftware.b4a.keywords.Common.Colors.Red).Append(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.DateTime.Date(_lngticks))).PopAll().getObject()));
 }else {
 //BA.debugLineNum = 1080;BA.debugLine="lblAirQualityLastUpdateBasement.Text = cs.Ini";
mostCurrent._lblairqualitylastupdatebasement.setText(BA.ObjectToCharSequence(_cs.Initialize().Bold().Append(BA.ObjectToCharSequence("Last update: ")).Pop().Append(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.DateTime.Date(_lngticks))).PopAll().getObject()));
 };
 }else if((_a[(int) (2)]).equals("00:00:00")) { 
 //BA.debugLineNum = 1083;BA.debugLine="lblAirQualityLastUpdateBasement.Text = cs.Init";
mostCurrent._lblairqualitylastupdatebasement.setText(BA.ObjectToCharSequence(_cs.Initialize().Bold().Append(BA.ObjectToCharSequence("Last update: ")).Pop().Color(anywheresoftware.b4a.keywords.Common.Colors.Red).Append(BA.ObjectToCharSequence(mostCurrent._lblairqualitylastupdatebasement.getText().replace("Last update: ",""))).PopAll().getObject()));
 };
 };
 } 
       catch (Exception e44) {
			processBA.setLastException(e44); //BA.debugLineNum = 1087;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("335979310",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 //BA.debugLineNum = 1088;BA.debugLine="lblAirQualityLastUpdateBasement.Text = cs.Initia";
mostCurrent._lblairqualitylastupdatebasement.setText(BA.ObjectToCharSequence(_cs.Initialize().Bold().Append(BA.ObjectToCharSequence("Exception: ")).Pop().Color(anywheresoftware.b4a.keywords.Common.Colors.Red).Append(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getObject())).PopAll().getObject()));
 };
 //BA.debugLineNum = 1090;BA.debugLine="End Sub";
return "";
}
public static String  _checkdatafile(String _filename) throws Exception{
anywheresoftware.b4a.objects.collections.List _list1 = null;
int _i = 0;
 //BA.debugLineNum = 3667;BA.debugLine="Sub CheckDataFile(filename As String)";
 //BA.debugLineNum = 3668;BA.debugLine="If File.Exists(File.DirInternal, filename) Then";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),_filename)) { 
 //BA.debugLineNum = 3669;BA.debugLine="If filename = \"account.txt\" Then";
if ((_filename).equals("account.txt")) { 
 //BA.debugLineNum = 3670;BA.debugLine="Dim List1 As List";
_list1 = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 3671;BA.debugLine="List1.Initialize";
_list1.Initialize();
 //BA.debugLineNum = 3672;BA.debugLine="List1 = File.ReadList(File.DirInternal, filenam";
_list1 = anywheresoftware.b4a.keywords.Common.File.ReadList(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),_filename);
 //BA.debugLineNum = 3673;BA.debugLine="For i = 0 To List1.Size - 1";
{
final int step6 = 1;
final int limit6 = (int) (_list1.getSize()-1);
_i = (int) (0) ;
for (;_i <= limit6 ;_i = _i + step6 ) {
 //BA.debugLineNum = 3674;BA.debugLine="If i = 0 Then";
if (_i==0) { 
 //BA.debugLineNum = 3675;BA.debugLine="emailAddress = List1.Get(i)";
_emailaddress = BA.ObjectToString(_list1.Get(_i));
 }else if(_i==1) { 
 //BA.debugLineNum = 3677;BA.debugLine="password = List1.Get(i)";
_password = BA.ObjectToString(_list1.Get(_i));
 };
 }
};
 };
 }else if(anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),_filename)) { 
 //BA.debugLineNum = 3682;BA.debugLine="File.Copy(File.DirRootExternal,filename,File.Dir";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),_filename,anywheresoftware.b4a.keywords.Common.File.getDirInternal(),_filename);
 //BA.debugLineNum = 3683;BA.debugLine="If File.Exists(File.DirInternal, filename) Then";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),_filename)) { 
 //BA.debugLineNum = 3684;BA.debugLine="If filename = \"account.txt\" Then";
if ((_filename).equals("account.txt")) { 
 //BA.debugLineNum = 3685;BA.debugLine="Dim List1 As List";
_list1 = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 3686;BA.debugLine="List1.Initialize";
_list1.Initialize();
 //BA.debugLineNum = 3687;BA.debugLine="List1 = File.ReadList(File.DirInternal, filena";
_list1 = anywheresoftware.b4a.keywords.Common.File.ReadList(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),_filename);
 //BA.debugLineNum = 3688;BA.debugLine="For i = 0 To List1.Size - 1";
{
final int step21 = 1;
final int limit21 = (int) (_list1.getSize()-1);
_i = (int) (0) ;
for (;_i <= limit21 ;_i = _i + step21 ) {
 //BA.debugLineNum = 3689;BA.debugLine="If i = 0 Then";
if (_i==0) { 
 //BA.debugLineNum = 3690;BA.debugLine="emailAddress = List1.Get(i)";
_emailaddress = BA.ObjectToString(_list1.Get(_i));
 }else if(_i==1) { 
 //BA.debugLineNum = 3692;BA.debugLine="password = List1.Get(i)";
_password = BA.ObjectToString(_list1.Get(_i));
 };
 }
};
 };
 }else {
 //BA.debugLineNum = 3697;BA.debugLine="Msgbox(filename & \" not found\",\"Smart Home Moni";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence(_filename+" not found"),BA.ObjectToCharSequence("Smart Home Monitor"),mostCurrent.activityBA);
 //BA.debugLineNum = 3698;BA.debugLine="ExitApplication";
anywheresoftware.b4a.keywords.Common.ExitApplication();
 };
 }else {
 //BA.debugLineNum = 3701;BA.debugLine="Msgbox(filename & \" not found\",\"Smart Home Monit";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence(_filename+" not found"),BA.ObjectToCharSequence("Smart Home Monitor"),mostCurrent.activityBA);
 //BA.debugLineNum = 3702;BA.debugLine="ExitApplication";
anywheresoftware.b4a.keywords.Common.ExitApplication();
 };
 //BA.debugLineNum = 3704;BA.debugLine="End Sub";
return "";
}
public static String  _checklfrlevel(int _lfrlevel) throws Exception{
 //BA.debugLineNum = 2285;BA.debugLine="Sub CheckLFRLevel(lfrlevel As Int) As String";
 //BA.debugLineNum = 2286;BA.debugLine="Try";
try { //BA.debugLineNum = 2288;BA.debugLine="If lfrlevel > -67 Then";
if (_lfrlevel>-67) { 
 //BA.debugLineNum = 2289;BA.debugLine="Return \"Amazing\"";
if (true) return "Amazing";
 }else if(_lfrlevel>-70 && _lfrlevel<=-67) { 
 //BA.debugLineNum = 2291;BA.debugLine="Return \"Very good\"";
if (true) return "Very good";
 }else if(_lfrlevel>-80 && _lfrlevel<=-70) { 
 //BA.debugLineNum = 2293;BA.debugLine="Return \"OK\"";
if (true) return "OK";
 }else if(_lfrlevel>-90 && _lfrlevel<=-80) { 
 //BA.debugLineNum = 2295;BA.debugLine="Return \"Not Good\"";
if (true) return "Not Good";
 }else {
 //BA.debugLineNum = 2297;BA.debugLine="Return \"Unusable\"";
if (true) return "Unusable";
 };
 } 
       catch (Exception e14) {
			processBA.setLastException(e14); //BA.debugLineNum = 2300;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("337224463",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 //BA.debugLineNum = 2301;BA.debugLine="lblStatus.Text = \"CheckLFRLevel LastException: \"";
mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("CheckLFRLevel LastException: "+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA))));
 //BA.debugLineNum = 2302;BA.debugLine="Return \"\"";
if (true) return "";
 };
 //BA.debugLineNum = 2304;BA.debugLine="End Sub";
return "";
}
public static String  _checktemphumiditysetting() throws Exception{
anywheresoftware.b4a.objects.CSBuilder _cs = null;
String _status = "";
String[] _a = null;
long _tomorrow = 0L;
long _ticks = 0L;
long _lngticks = 0L;
b4a.example.dateutils._period _p = null;
 //BA.debugLineNum = 1092;BA.debugLine="Sub CheckTempHumiditySetting";
 //BA.debugLineNum = 1093;BA.debugLine="Dim cs As CSBuilder";
_cs = new anywheresoftware.b4a.objects.CSBuilder();
 //BA.debugLineNum = 1094;BA.debugLine="cs.Initialize";
_cs.Initialize();
 //BA.debugLineNum = 1095;BA.debugLine="Try";
try { //BA.debugLineNum = 1096;BA.debugLine="Dim status As String";
_status = "";
 //BA.debugLineNum = 1097;BA.debugLine="status = StateManager.GetSetting(\"TempHumidity\")";
_status = mostCurrent._statemanager._getsetting /*String*/ (mostCurrent.activityBA,"TempHumidity");
 //BA.debugLineNum = 1098;BA.debugLine="status = status.Replace(\"|24:\",\"|00:\")";
_status = _status.replace("|24:","|00:");
 //BA.debugLineNum = 1099;BA.debugLine="Dim a() As String = Regex.Split(\"\\|\",status)";
_a = anywheresoftware.b4a.keywords.Common.Regex.Split("\\|",_status);
 //BA.debugLineNum = 1100;BA.debugLine="If a.Length = 9 Then";
if (_a.length==9) { 
 //BA.debugLineNum = 1101;BA.debugLine="Dim cs As CSBuilder";
_cs = new anywheresoftware.b4a.objects.CSBuilder();
 //BA.debugLineNum = 1102;BA.debugLine="cs.Initialize";
_cs.Initialize();
 //BA.debugLineNum = 1103;BA.debugLine="If a(0) = \"OK\" And a(1) > 0 Then";
if ((_a[(int) (0)]).equals("OK") && (double)(Double.parseDouble(_a[(int) (1)]))>0) { 
 //BA.debugLineNum = 1104;BA.debugLine="GaugeTemp.CurrentValue = a(1)";
mostCurrent._gaugetemp._setcurrentvalue /*float*/ ((float)(Double.parseDouble(_a[(int) (1)])));
 //BA.debugLineNum = 1105;BA.debugLine="GaugeHumidity.CurrentValue = a(2)";
mostCurrent._gaugehumidity._setcurrentvalue /*float*/ ((float)(Double.parseDouble(_a[(int) (2)])));
 //BA.debugLineNum = 1106;BA.debugLine="lblPerception.Text = cs.Initialize.Bold.Append";
mostCurrent._lblperception.setText(BA.ObjectToCharSequence(_cs.Initialize().Bold().Append(BA.ObjectToCharSequence("Human Perception: ")).Pop().Append(BA.ObjectToCharSequence(_getperception(_a[(int) (3)]))).PopAll().getObject()));
 //BA.debugLineNum = 1107;BA.debugLine="If a(4) = 2 Or a(4) = 6 Or a(4) = 10 Then";
if ((_a[(int) (4)]).equals(BA.NumberToString(2)) || (_a[(int) (4)]).equals(BA.NumberToString(6)) || (_a[(int) (4)]).equals(BA.NumberToString(10))) { 
 //BA.debugLineNum = 1108;BA.debugLine="lblComfort.Text = cs.Initialize.Bold.Append(\"";
mostCurrent._lblcomfort.setText(BA.ObjectToCharSequence(_cs.Initialize().Bold().Append(BA.ObjectToCharSequence("Thermal Comfort: ")).Pop().Color(anywheresoftware.b4a.keywords.Common.Colors.Blue).Append(BA.ObjectToCharSequence(_getcomfort(_a[(int) (4)]))).PopAll().getObject()));
 }else if((_a[(int) (4)]).equals(BA.NumberToString(0))) { 
 //BA.debugLineNum = 1110;BA.debugLine="lblComfort.Text = cs.Initialize.Bold.Append(\"";
mostCurrent._lblcomfort.setText(BA.ObjectToCharSequence(_cs.Initialize().Bold().Append(BA.ObjectToCharSequence("Thermal Comfort: ")).Pop().Append(BA.ObjectToCharSequence(_getcomfort(_a[(int) (4)]))).PopAll().getObject()));
 }else {
 //BA.debugLineNum = 1112;BA.debugLine="lblComfort.Text = cs.Initialize.Bold.Append(\"";
mostCurrent._lblcomfort.setText(BA.ObjectToCharSequence(_cs.Initialize().Bold().Append(BA.ObjectToCharSequence("Thermal Comfort: ")).Pop().Color(anywheresoftware.b4a.keywords.Common.Colors.Red).Append(BA.ObjectToCharSequence(_getcomfort(_a[(int) (4)]))).PopAll().getObject()));
 };
 //BA.debugLineNum = 1115;BA.debugLine="GaugeHeatIndex.CurrentValue = a(5)";
mostCurrent._gaugeheatindex._setcurrentvalue /*float*/ ((float)(Double.parseDouble(_a[(int) (5)])));
 //BA.debugLineNum = 1116;BA.debugLine="GaugeDewPoint.CurrentValue = a(6)";
mostCurrent._gaugedewpoint._setcurrentvalue /*float*/ ((float)(Double.parseDouble(_a[(int) (6)])));
 //BA.debugLineNum = 1117;BA.debugLine="If a(7) = \"\" Then";
if ((_a[(int) (7)]).equals("")) { 
 //BA.debugLineNum = 1118;BA.debugLine="Dim Tomorrow As Long";
_tomorrow = 0L;
 //BA.debugLineNum = 1119;BA.debugLine="Tomorrow = DateTime.add(DateTime.Now, 0, 0, 1";
_tomorrow = anywheresoftware.b4a.keywords.Common.DateTime.Add(anywheresoftware.b4a.keywords.Common.DateTime.getNow(),(int) (0),(int) (0),(int) (1));
 //BA.debugLineNum = 1120;BA.debugLine="DateTime.DateFormat = \"yy-MM-dd\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("yy-MM-dd");
 //BA.debugLineNum = 1121;BA.debugLine="a(7) = DateTime.Date(Tomorrow)";
_a[(int) (7)] = anywheresoftware.b4a.keywords.Common.DateTime.Date(_tomorrow);
 };
 //BA.debugLineNum = 1123;BA.debugLine="If a(8).Contains(\"|24:\") Then";
if (_a[(int) (8)].contains("|24:")) { 
 //BA.debugLineNum = 1124;BA.debugLine="a(8) = a(8).Replace(\"|24:\",\"|00:\")";
_a[(int) (8)] = _a[(int) (8)].replace("|24:","|00:");
 //BA.debugLineNum = 1125;BA.debugLine="Dim Tomorrow As Long";
_tomorrow = 0L;
 //BA.debugLineNum = 1126;BA.debugLine="Tomorrow = DateTime.add(DateTime.Now, 0, 0, 1";
_tomorrow = anywheresoftware.b4a.keywords.Common.DateTime.Add(anywheresoftware.b4a.keywords.Common.DateTime.getNow(),(int) (0),(int) (0),(int) (1));
 //BA.debugLineNum = 1127;BA.debugLine="DateTime.DateFormat = \"yy-MM-dd\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("yy-MM-dd");
 //BA.debugLineNum = 1128;BA.debugLine="a(7) = DateTime.Date(Tomorrow)";
_a[(int) (7)] = anywheresoftware.b4a.keywords.Common.DateTime.Date(_tomorrow);
 };
 //BA.debugLineNum = 1130;BA.debugLine="DateTime.DateFormat = \"yy-MM-dd HH:mm:ss z\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("yy-MM-dd HH:mm:ss z");
 //BA.debugLineNum = 1131;BA.debugLine="Dim ticks As Long = DateTime.DateParse(a(7) &";
_ticks = anywheresoftware.b4a.keywords.Common.DateTime.DateParse(_a[(int) (7)]+" "+_a[(int) (8)]+" GMT");
 //BA.debugLineNum = 1132;BA.debugLine="DateTime.DateFormat = \"MMM d, yyyy h:mm:ss a z";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("MMM d, yyyy h:mm:ss a z");
 //BA.debugLineNum = 1133;BA.debugLine="Dim lngTicks As Long = ticks";
_lngticks = _ticks;
 //BA.debugLineNum = 1134;BA.debugLine="Dim p As Period = DateUtils.PeriodBetween(lngT";
_p = mostCurrent._dateutils._periodbetween(mostCurrent.activityBA,_lngticks,anywheresoftware.b4a.keywords.Common.DateTime.getNow());
 //BA.debugLineNum = 1136;BA.debugLine="If p.Minutes > = 5 Then";
if (_p.Minutes>=5) { 
 //BA.debugLineNum = 1137;BA.debugLine="lblLastUpdate.Text = cs.Initialize.Bold.Appen";
mostCurrent._lbllastupdate.setText(BA.ObjectToCharSequence(_cs.Initialize().Bold().Append(BA.ObjectToCharSequence("Last update: ")).Pop().Color(anywheresoftware.b4a.keywords.Common.Colors.Red).Append(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.DateTime.Date(_lngticks))).PopAll().getObject()));
 }else {
 //BA.debugLineNum = 1139;BA.debugLine="lblLastUpdate.Text = cs.Initialize.Bold.Appen";
mostCurrent._lbllastupdate.setText(BA.ObjectToCharSequence(_cs.Initialize().Bold().Append(BA.ObjectToCharSequence("Last update: ")).Pop().Append(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.DateTime.Date(_lngticks))).PopAll().getObject()));
 };
 }else if((_a[(int) (8)]).equals("00:00:00")) { 
 //BA.debugLineNum = 1142;BA.debugLine="lblLastUpdate.Text = cs.Initialize.Bold.Append";
mostCurrent._lbllastupdate.setText(BA.ObjectToCharSequence(_cs.Initialize().Bold().Append(BA.ObjectToCharSequence("Last update: ")).Pop().Color(anywheresoftware.b4a.keywords.Common.Colors.Red).Append(BA.ObjectToCharSequence(mostCurrent._lbllastupdate.getText().replace("Last update: ",""))).PopAll().getObject()));
 };
 };
 } 
       catch (Exception e52) {
			processBA.setLastException(e52); //BA.debugLineNum = 1146;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("336044854",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 //BA.debugLineNum = 1147;BA.debugLine="lblLastUpdate.Text = cs.Initialize.Bold.Append(\"";
mostCurrent._lbllastupdate.setText(BA.ObjectToCharSequence(_cs.Initialize().Bold().Append(BA.ObjectToCharSequence("Exception: ")).Pop().Color(anywheresoftware.b4a.keywords.Common.Colors.Red).Append(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getObject())).PopAll().getObject()));
 };
 //BA.debugLineNum = 1149;BA.debugLine="End Sub";
return "";
}
public static String  _checktemphumiditysettingbasement() throws Exception{
anywheresoftware.b4a.objects.CSBuilder _cs = null;
String _status = "";
String[] _a = null;
long _tomorrow = 0L;
long _ticks = 0L;
long _lngticks = 0L;
b4a.example.dateutils._period _p = null;
 //BA.debugLineNum = 1151;BA.debugLine="Sub CheckTempHumiditySettingBasement";
 //BA.debugLineNum = 1152;BA.debugLine="Dim cs As CSBuilder";
_cs = new anywheresoftware.b4a.objects.CSBuilder();
 //BA.debugLineNum = 1153;BA.debugLine="cs.Initialize";
_cs.Initialize();
 //BA.debugLineNum = 1154;BA.debugLine="Try";
try { //BA.debugLineNum = 1155;BA.debugLine="Dim status As String";
_status = "";
 //BA.debugLineNum = 1156;BA.debugLine="status = StateManager.GetSetting(\"TempHumidityBa";
_status = mostCurrent._statemanager._getsetting /*String*/ (mostCurrent.activityBA,"TempHumidityBasement");
 //BA.debugLineNum = 1157;BA.debugLine="status = status.Replace(\"|24:\",\"|00:\")";
_status = _status.replace("|24:","|00:");
 //BA.debugLineNum = 1158;BA.debugLine="Dim a() As String = Regex.Split(\"\\|\",status)";
_a = anywheresoftware.b4a.keywords.Common.Regex.Split("\\|",_status);
 //BA.debugLineNum = 1159;BA.debugLine="If a.Length = 9 Then";
if (_a.length==9) { 
 //BA.debugLineNum = 1160;BA.debugLine="Dim cs As CSBuilder";
_cs = new anywheresoftware.b4a.objects.CSBuilder();
 //BA.debugLineNum = 1161;BA.debugLine="cs.Initialize";
_cs.Initialize();
 //BA.debugLineNum = 1162;BA.debugLine="If a(0) = \"OK\" And a(1) > 0 Then";
if ((_a[(int) (0)]).equals("OK") && (double)(Double.parseDouble(_a[(int) (1)]))>0) { 
 //BA.debugLineNum = 1163;BA.debugLine="GaugeTempBasement.CurrentValue = a(1)";
mostCurrent._gaugetempbasement._setcurrentvalue /*float*/ ((float)(Double.parseDouble(_a[(int) (1)])));
 //BA.debugLineNum = 1164;BA.debugLine="GaugeHumidityBasement.CurrentValue = a(2)";
mostCurrent._gaugehumiditybasement._setcurrentvalue /*float*/ ((float)(Double.parseDouble(_a[(int) (2)])));
 //BA.debugLineNum = 1165;BA.debugLine="lblPerceptionBasement.Text = cs.Initialize.Bol";
mostCurrent._lblperceptionbasement.setText(BA.ObjectToCharSequence(_cs.Initialize().Bold().Append(BA.ObjectToCharSequence("Human Perception: ")).Pop().Append(BA.ObjectToCharSequence(_getperception(_a[(int) (3)]))).PopAll().getObject()));
 //BA.debugLineNum = 1166;BA.debugLine="If a(4) = 2 Or a(4) = 6 Or a(4) = 10 Then";
if ((_a[(int) (4)]).equals(BA.NumberToString(2)) || (_a[(int) (4)]).equals(BA.NumberToString(6)) || (_a[(int) (4)]).equals(BA.NumberToString(10))) { 
 //BA.debugLineNum = 1167;BA.debugLine="lblComfortBasement.Text = cs.Initialize.Bold.";
mostCurrent._lblcomfortbasement.setText(BA.ObjectToCharSequence(_cs.Initialize().Bold().Append(BA.ObjectToCharSequence("Thermal Comfort: ")).Pop().Color(anywheresoftware.b4a.keywords.Common.Colors.Blue).Append(BA.ObjectToCharSequence(_getcomfort(_a[(int) (4)]))).PopAll().getObject()));
 }else if((_a[(int) (4)]).equals(BA.NumberToString(0))) { 
 //BA.debugLineNum = 1169;BA.debugLine="lblComfortBasement.Text = cs.Initialize.Bold.";
mostCurrent._lblcomfortbasement.setText(BA.ObjectToCharSequence(_cs.Initialize().Bold().Append(BA.ObjectToCharSequence("Thermal Comfort: ")).Pop().Append(BA.ObjectToCharSequence(_getcomfort(_a[(int) (4)]))).PopAll().getObject()));
 }else {
 //BA.debugLineNum = 1171;BA.debugLine="lblComfortBasement.Text = cs.Initialize.Bold.";
mostCurrent._lblcomfortbasement.setText(BA.ObjectToCharSequence(_cs.Initialize().Bold().Append(BA.ObjectToCharSequence("Thermal Comfort: ")).Pop().Color(anywheresoftware.b4a.keywords.Common.Colors.Red).Append(BA.ObjectToCharSequence(_getcomfort(_a[(int) (4)]))).PopAll().getObject()));
 };
 //BA.debugLineNum = 1173;BA.debugLine="GaugeHeatIndexBasement.CurrentValue = a(5)";
mostCurrent._gaugeheatindexbasement._setcurrentvalue /*float*/ ((float)(Double.parseDouble(_a[(int) (5)])));
 //BA.debugLineNum = 1174;BA.debugLine="GaugeDewPointBasement.CurrentValue = a(6)";
mostCurrent._gaugedewpointbasement._setcurrentvalue /*float*/ ((float)(Double.parseDouble(_a[(int) (6)])));
 //BA.debugLineNum = 1175;BA.debugLine="If a(7) = \"\" Then";
if ((_a[(int) (7)]).equals("")) { 
 //BA.debugLineNum = 1176;BA.debugLine="Dim Tomorrow As Long";
_tomorrow = 0L;
 //BA.debugLineNum = 1177;BA.debugLine="Tomorrow = DateTime.add(DateTime.Now, 0, 0, 1";
_tomorrow = anywheresoftware.b4a.keywords.Common.DateTime.Add(anywheresoftware.b4a.keywords.Common.DateTime.getNow(),(int) (0),(int) (0),(int) (1));
 //BA.debugLineNum = 1178;BA.debugLine="DateTime.DateFormat = \"yy-MM-dd\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("yy-MM-dd");
 //BA.debugLineNum = 1179;BA.debugLine="a(7) = DateTime.Date(Tomorrow)";
_a[(int) (7)] = anywheresoftware.b4a.keywords.Common.DateTime.Date(_tomorrow);
 };
 //BA.debugLineNum = 1181;BA.debugLine="If a(8).Contains(\"|24:\") Then";
if (_a[(int) (8)].contains("|24:")) { 
 //BA.debugLineNum = 1182;BA.debugLine="a(8) = a(8).Replace(\"|24:\",\"|00:\")";
_a[(int) (8)] = _a[(int) (8)].replace("|24:","|00:");
 //BA.debugLineNum = 1183;BA.debugLine="Dim Tomorrow As Long";
_tomorrow = 0L;
 //BA.debugLineNum = 1184;BA.debugLine="Tomorrow = DateTime.add(DateTime.Now, 0, 0, 1";
_tomorrow = anywheresoftware.b4a.keywords.Common.DateTime.Add(anywheresoftware.b4a.keywords.Common.DateTime.getNow(),(int) (0),(int) (0),(int) (1));
 //BA.debugLineNum = 1185;BA.debugLine="DateTime.DateFormat = \"yy-MM-dd\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("yy-MM-dd");
 //BA.debugLineNum = 1186;BA.debugLine="a(7) = DateTime.Date(Tomorrow)";
_a[(int) (7)] = anywheresoftware.b4a.keywords.Common.DateTime.Date(_tomorrow);
 };
 //BA.debugLineNum = 1188;BA.debugLine="DateTime.DateFormat = \"yy-MM-dd HH:mm:ss z\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("yy-MM-dd HH:mm:ss z");
 //BA.debugLineNum = 1189;BA.debugLine="Dim ticks As Long = DateTime.DateParse(a(7) &";
_ticks = anywheresoftware.b4a.keywords.Common.DateTime.DateParse(_a[(int) (7)]+" "+_a[(int) (8)]+" GMT");
 //BA.debugLineNum = 1190;BA.debugLine="DateTime.DateFormat = \"MMM d, yyyy h:mm:ss a z";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("MMM d, yyyy h:mm:ss a z");
 //BA.debugLineNum = 1191;BA.debugLine="Dim lngTicks As Long = ticks";
_lngticks = _ticks;
 //BA.debugLineNum = 1192;BA.debugLine="Dim p As Period = DateUtils.PeriodBetween(lngT";
_p = mostCurrent._dateutils._periodbetween(mostCurrent.activityBA,_lngticks,anywheresoftware.b4a.keywords.Common.DateTime.getNow());
 //BA.debugLineNum = 1194;BA.debugLine="If p.Minutes > = 5 Then";
if (_p.Minutes>=5) { 
 //BA.debugLineNum = 1195;BA.debugLine="lblLastUpdateBasement.Text = cs.Initialize.Bo";
mostCurrent._lbllastupdatebasement.setText(BA.ObjectToCharSequence(_cs.Initialize().Bold().Append(BA.ObjectToCharSequence("Last update: ")).Pop().Color(anywheresoftware.b4a.keywords.Common.Colors.Red).Append(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.DateTime.Date(_lngticks))).PopAll().getObject()));
 }else {
 //BA.debugLineNum = 1197;BA.debugLine="lblLastUpdateBasement.Text = cs.Initialize.Bo";
mostCurrent._lbllastupdatebasement.setText(BA.ObjectToCharSequence(_cs.Initialize().Bold().Append(BA.ObjectToCharSequence("Last update: ")).Pop().Append(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.DateTime.Date(_lngticks))).PopAll().getObject()));
 };
 }else if((_a[(int) (8)]).equals("00:00:00")) { 
 //BA.debugLineNum = 1200;BA.debugLine="lblLastUpdateBasement.Text = cs.Initialize.Bol";
mostCurrent._lbllastupdatebasement.setText(BA.ObjectToCharSequence(_cs.Initialize().Bold().Append(BA.ObjectToCharSequence("Last update: ")).Pop().Color(anywheresoftware.b4a.keywords.Common.Colors.Red).Append(BA.ObjectToCharSequence(mostCurrent._lbllastupdatebasement.getText().replace("Last update: ",""))).PopAll().getObject()));
 };
 };
 } 
       catch (Exception e52) {
			processBA.setLastException(e52); //BA.debugLineNum = 1204;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("336110389",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 //BA.debugLineNum = 1205;BA.debugLine="lblLastUpdateBasement.Text = cs.Initialize.Bold.";
mostCurrent._lbllastupdatebasement.setText(BA.ObjectToCharSequence(_cs.Initialize().Bold().Append(BA.ObjectToCharSequence("Exception: ")).Pop().Color(anywheresoftware.b4a.keywords.Common.Colors.Red).Append(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getObject())).PopAll().getObject()));
 };
 //BA.debugLineNum = 1207;BA.debugLine="End Sub";
return "";
}
public static void  _clvactivity_itemclick(int _index,Object _value) throws Exception{
ResumableSub_clvActivity_ItemClick rsub = new ResumableSub_clvActivity_ItemClick(null,_index,_value);
rsub.resume(processBA, null);
}
public static class ResumableSub_clvActivity_ItemClick extends BA.ResumableSub {
public ResumableSub_clvActivity_ItemClick(cloyd.smart.home.monitor.main parent,int _index,Object _value) {
this.parent = parent;
this._index = _index;
this._value = _value;
}
cloyd.smart.home.monitor.main parent;
int _index;
Object _value;
anywheresoftware.b4a.objects.B4XViewWrapper _p = null;
anywheresoftware.b4a.objects.B4XViewWrapper _contentlabel = null;
cloyd.smart.home.monitor.main._carddata _cd = null;
String _videourl = "";
anywheresoftware.b4a.objects.collections.List _list1 = null;
int _i = 0;
Object _mytypes = null;
cloyd.smart.home.monitor.main._videoinfo _videos = null;
anywheresoftware.b4a.keywords.Common.ResumableSubWrapper _rs = null;
Object _result = null;
Object _results = null;
int step15;
int limit15;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
try {

        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 2660;BA.debugLine="Try";
if (true) break;

case 1:
//try
this.state = 18;
this.catchState = 17;
this.state = 3;
if (true) break;

case 3:
//C
this.state = 4;
this.catchState = 17;
 //BA.debugLineNum = 2661;BA.debugLine="Dim p As B4XView = clvActivity.GetPanel(Index)";
_p = new anywheresoftware.b4a.objects.B4XViewWrapper();
_p = parent.mostCurrent._clvactivity._getpanel(_index);
 //BA.debugLineNum = 2662;BA.debugLine="If p.NumberOfViews > 0 Then";
if (true) break;

case 4:
//if
this.state = 7;
if (_p.getNumberOfViews()>0) { 
this.state = 6;
}if (true) break;

case 6:
//C
this.state = 7;
 //BA.debugLineNum = 2663;BA.debugLine="Dim ContentLabel As B4XView = p.GetView(0).GetV";
_contentlabel = new anywheresoftware.b4a.objects.B4XViewWrapper();
_contentlabel = _p.GetView((int) (0)).GetView((int) (6));
 //BA.debugLineNum = 2664;BA.debugLine="ContentLabel.Visible = False";
_contentlabel.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2666;BA.debugLine="Dim ContentLabel As B4XView = p.GetView(0).GetV";
_contentlabel = new anywheresoftware.b4a.objects.B4XViewWrapper();
_contentlabel = _p.GetView((int) (0)).GetView((int) (4));
 //BA.debugLineNum = 2667;BA.debugLine="ContentLabel.Visible = False";
_contentlabel.setVisible(anywheresoftware.b4a.keywords.Common.False);
 if (true) break;

case 7:
//C
this.state = 8;
;
 //BA.debugLineNum = 2670;BA.debugLine="UpdateItemColor(Index)";
_updateitemcolor(_index);
 //BA.debugLineNum = 2671;BA.debugLine="wvMedia.LoadUrl(\"\")";
parent.mostCurrent._wvmedia.LoadUrl("");
 //BA.debugLineNum = 2673;BA.debugLine="Dim cd As CardData = clvActivity.GetValue(Index)";
_cd = (cloyd.smart.home.monitor.main._carddata)(parent.mostCurrent._clvactivity._getvalue(_index));
 //BA.debugLineNum = 2674;BA.debugLine="Dim videoURL As String = cd.mediaURL";
_videourl = _cd.mediaURL /*String*/ ;
 //BA.debugLineNum = 2676;BA.debugLine="B4XLoadingIndicator4.Show";
parent.mostCurrent._b4xloadingindicator4._show /*String*/ ();
 //BA.debugLineNum = 2678;BA.debugLine="Dim list1 As List = Starter.kvs.ListKeys";
_list1 = new anywheresoftware.b4a.objects.collections.List();
_list1 = parent.mostCurrent._starter._kvs /*cloyd.smart.home.monitor.keyvaluestore*/ ._listkeys /*anywheresoftware.b4a.objects.collections.List*/ ();
 //BA.debugLineNum = 2679;BA.debugLine="For i =  0 To list1.Size-1";
if (true) break;

case 8:
//for
this.state = 15;
step15 = 1;
limit15 = (int) (_list1.getSize()-1);
_i = (int) (0) ;
this.state = 19;
if (true) break;

case 19:
//C
this.state = 15;
if ((step15 > 0 && _i <= limit15) || (step15 < 0 && _i >= limit15)) this.state = 10;
if (true) break;

case 20:
//C
this.state = 19;
_i = ((int)(0 + _i + step15)) ;
if (true) break;

case 10:
//C
this.state = 11;
 //BA.debugLineNum = 2680;BA.debugLine="Dim mytypes As Object = Starter.kvs.Get(list1.G";
_mytypes = parent.mostCurrent._starter._kvs /*cloyd.smart.home.monitor.keyvaluestore*/ ._get /*Object*/ (BA.ObjectToString(_list1.Get(_i)));
 //BA.debugLineNum = 2681;BA.debugLine="Dim videos = mytypes As VideoInfo";
_videos = (cloyd.smart.home.monitor.main._videoinfo)(_mytypes);
 //BA.debugLineNum = 2682;BA.debugLine="If videoURL.Contains(videos.VideoID) Then";
if (true) break;

case 11:
//if
this.state = 14;
if (_videourl.contains(_videos.VideoID /*String*/ )) { 
this.state = 13;
}if (true) break;

case 13:
//C
this.state = 14;
 //BA.debugLineNum = 2683;BA.debugLine="Starter.kvs.Put(videos.VideoID,CreateCustomTyp";
parent.mostCurrent._starter._kvs /*cloyd.smart.home.monitor.keyvaluestore*/ ._put /*String*/ (_videos.VideoID /*String*/ ,(Object)(_createcustomtype(_videos.ThumbnailPath /*String*/ ,_videos.DateCreated /*String*/ ,"true",_videos.DeviceName /*String*/ ,_videos.VideoID /*String*/ ,_videos.ThumbnailBLOB /*byte[]*/ )));
 if (true) break;

case 14:
//C
this.state = 20;
;
 if (true) break;
if (true) break;

case 15:
//C
this.state = 18;
;
 //BA.debugLineNum = 2686;BA.debugLine="lblDuration.Text = \"0:00\"";
parent.mostCurrent._lblduration.setText(BA.ObjectToCharSequence("0:00"));
 //BA.debugLineNum = 2688;BA.debugLine="Dim rs As ResumableSub = ShowVideo(videoURL)";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _showvideo(_videourl);
 //BA.debugLineNum = 2689;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 21;
return;
case 21:
//C
this.state = 18;
_result = (Object) result[0];
;
 //BA.debugLineNum = 2692;BA.debugLine="Dim rs As ResumableSub = GetUnwatchedVideos";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _getunwatchedvideos();
 //BA.debugLineNum = 2693;BA.debugLine="wait for (rs) complete (Results As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 22;
return;
case 22:
//C
this.state = 18;
_results = (Object) result[0];
;
 if (true) break;

case 17:
//C
this.state = 18;
this.catchState = 0;
 //BA.debugLineNum = 2696;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("338010917",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 if (true) break;
if (true) break;

case 18:
//C
this.state = -1;
this.catchState = 0;
;
 //BA.debugLineNum = 2698;BA.debugLine="B4XLoadingIndicator4.Hide";
parent.mostCurrent._b4xloadingindicator4._hide /*String*/ ();
 //BA.debugLineNum = 2699;BA.debugLine="End Sub";
if (true) break;
}} 
       catch (Exception e0) {
			
if (catchState == 0)
    throw e0;
else {
    state = catchState;
processBA.setLastException(e0);}
            }
        }
    }
}
public static String  _clvactivity_visiblerangechanged(int _firstindex,int _lastindex) throws Exception{
int _extrasize = 0;
int _i = 0;
anywheresoftware.b4a.objects.B4XViewWrapper _p = null;
cloyd.smart.home.monitor.main._carddata _cd = null;
String _dayname = "";
anywheresoftware.b4a.objects.B4XViewWrapper _backpane = null;
anywheresoftware.b4a.objects.B4XViewWrapper _contentlabel = null;
anywheresoftware.b4a.objects.ImageViewWrapper _ivcleanup = null;
 //BA.debugLineNum = 2582;BA.debugLine="Sub clvActivity_VisibleRangeChanged (FirstIndex As";
 //BA.debugLineNum = 2583;BA.debugLine="Dim ExtraSize As Int = 1";
_extrasize = (int) (1);
 //BA.debugLineNum = 2584;BA.debugLine="For i = 0 To clvActivity.Size - 1";
{
final int step2 = 1;
final int limit2 = (int) (mostCurrent._clvactivity._getsize()-1);
_i = (int) (0) ;
for (;_i <= limit2 ;_i = _i + step2 ) {
 //BA.debugLineNum = 2585;BA.debugLine="Dim p As B4XView = clvActivity.GetPanel(i)";
_p = new anywheresoftware.b4a.objects.B4XViewWrapper();
_p = mostCurrent._clvactivity._getpanel(_i);
 //BA.debugLineNum = 2586;BA.debugLine="If i > FirstIndex - ExtraSize And i < LastIndex";
if (_i>_firstindex-_extrasize && _i<_lastindex+_extrasize) { 
 //BA.debugLineNum = 2588;BA.debugLine="If p.NumberOfViews = 0 Then";
if (_p.getNumberOfViews()==0) { 
 //BA.debugLineNum = 2589;BA.debugLine="Dim cd As CardData = clvActivity.GetValue(i)";
_cd = (cloyd.smart.home.monitor.main._carddata)(mostCurrent._clvactivity._getvalue(_i));
 //BA.debugLineNum = 2590;BA.debugLine="p.LoadLayout(\"blinkcellitem\")";
_p.LoadLayout("blinkcellitem",mostCurrent.activityBA);
 //BA.debugLineNum = 2591;BA.debugLine="ivScreenshot.Bitmap = cd.screenshot";
mostCurrent._ivscreenshot.setBitmap((android.graphics.Bitmap)(_cd.screenshot /*anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper*/ .getObject()));
 //BA.debugLineNum = 2593;BA.debugLine="Dim dayname As String";
_dayname = "";
 //BA.debugLineNum = 2594;BA.debugLine="dayname = ConvertDayName(cd.filedate)";
_dayname = _convertdayname(_cd.filedate /*String*/ );
 //BA.debugLineNum = 2595;BA.debugLine="If cd.iswatchedvisible Then";
if (_cd.iswatchedvisible /*boolean*/ ) { 
 //BA.debugLineNum = 2596;BA.debugLine="ivWatched.Visible = True";
mostCurrent._ivwatched.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2597;BA.debugLine="lblDate.Text = \"   \" & dayname & \" \" & Conver";
mostCurrent._lbldate.setText(BA.ObjectToCharSequence("   "+_dayname+" "+_convertdatetimeperiod(_cd.filedate /*String*/ ,_dayname)));
 }else {
 //BA.debugLineNum = 2599;BA.debugLine="ivWatched.Visible = False";
mostCurrent._ivwatched.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2600;BA.debugLine="lblDate.Text = \"   \" & dayname";
mostCurrent._lbldate.setText(BA.ObjectToCharSequence("   "+_dayname));
 };
 //BA.debugLineNum = 2602;BA.debugLine="lblFileInfo.Text = \"   \" & ConvertFullDateTime";
mostCurrent._lblfileinfo.setText(BA.ObjectToCharSequence("   "+_convertfulldatetime(_cd.filedate /*String*/ )));
 //BA.debugLineNum = 2603;BA.debugLine="lblDeviceInfo.Text = \"   \" & cd.deviceinfo";
mostCurrent._lbldeviceinfo.setText(BA.ObjectToCharSequence("   "+_cd.deviceinfo /*String*/ ));
 //BA.debugLineNum = 2604;BA.debugLine="lblMediaURL.Text = cd.mediaURL";
mostCurrent._lblmediaurl.setText(BA.ObjectToCharSequence(_cd.mediaURL /*String*/ ));
 //BA.debugLineNum = 2606;BA.debugLine="If previousSelectedIndex > (clvActivity.Size-1";
if (_previousselectedindex>(mostCurrent._clvactivity._getsize()-1)) { 
 //BA.debugLineNum = 2607;BA.debugLine="previousSelectedIndex = 0";
_previousselectedindex = (int) (0);
 };
 //BA.debugLineNum = 2609;BA.debugLine="If previousSelectedIndex = i Then";
if (_previousselectedindex==_i) { 
 //BA.debugLineNum = 2610;BA.debugLine="Dim p As B4XView = clvActivity.GetPanel(previ";
_p = new anywheresoftware.b4a.objects.B4XViewWrapper();
_p = mostCurrent._clvactivity._getpanel(_previousselectedindex);
 //BA.debugLineNum = 2611;BA.debugLine="If p.NumberOfViews > 0 Then";
if (_p.getNumberOfViews()>0) { 
 //BA.debugLineNum = 2612;BA.debugLine="Dim backPane As B4XView = p.getview(0)";
_backpane = new anywheresoftware.b4a.objects.B4XViewWrapper();
_backpane = _p.GetView((int) (0));
 //BA.debugLineNum = 2613;BA.debugLine="backPane.Color = xui.Color_ARGB(255,217,215,";
_backpane.setColor(mostCurrent._xui.Color_ARGB((int) (255),(int) (217),(int) (215),(int) (222)));
 //BA.debugLineNum = 2615;BA.debugLine="Dim ContentLabel As B4XView = p.GetView(0).G";
_contentlabel = new anywheresoftware.b4a.objects.B4XViewWrapper();
_contentlabel = _p.GetView((int) (0)).GetView((int) (6));
 //BA.debugLineNum = 2616;BA.debugLine="ContentLabel.Visible = True";
_contentlabel.setVisible(anywheresoftware.b4a.keywords.Common.True);
 };
 };
 };
 }else {
 //BA.debugLineNum = 2622;BA.debugLine="If p.NumberOfViews > 0 Then";
if (_p.getNumberOfViews()>0) { 
 //BA.debugLineNum = 2623;BA.debugLine="Dim ivCleanUP As ImageView = p.GetView(0).GetV";
_ivcleanup = new anywheresoftware.b4a.objects.ImageViewWrapper();
_ivcleanup = (anywheresoftware.b4a.objects.ImageViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ImageViewWrapper(), (android.widget.ImageView)(_p.GetView((int) (0)).GetView((int) (3)).getObject()));
 //BA.debugLineNum = 2624;BA.debugLine="ivCleanUP.Bitmap = Null";
_ivcleanup.setBitmap((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 2625;BA.debugLine="p.RemoveAllViews";
_p.RemoveAllViews();
 };
 };
 }
};
 //BA.debugLineNum = 2629;BA.debugLine="End Sub";
return "";
}
public static String  _convertdatetime(String _inputtime) throws Exception{
long _ticks = 0L;
long _lngticks = 0L;
 //BA.debugLineNum = 2306;BA.debugLine="Sub ConvertDateTime(inputTime As String) As String";
 //BA.debugLineNum = 2308;BA.debugLine="Dim ticks As Long = ParseUTCstring(inputTime.Repl";
_ticks = _parseutcstring(_inputtime.replace("+00:00","+0000"));
 //BA.debugLineNum = 2310;BA.debugLine="DateTime.DateFormat = \"yyyy-MM-dd h:mm:ss a\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("yyyy-MM-dd h:mm:ss a");
 //BA.debugLineNum = 2311;BA.debugLine="Dim lngTicks As Long = ticks";
_lngticks = _ticks;
 //BA.debugLineNum = 2314;BA.debugLine="Return DateTime.Date(lngTicks)";
if (true) return anywheresoftware.b4a.keywords.Common.DateTime.Date(_lngticks);
 //BA.debugLineNum = 2315;BA.debugLine="End Sub";
return "";
}
public static String  _convertdatetimeperiod(String _inputtime,String _dayname) throws Exception{
long _ticks = 0L;
long _lngticks = 0L;
b4a.example.dateutils._period _p = null;
 //BA.debugLineNum = 2769;BA.debugLine="Sub ConvertDateTimePeriod(inputTime As String, day";
 //BA.debugLineNum = 2771;BA.debugLine="Dim ticks As Long = ParseUTCstring(inputTime.Repl";
_ticks = _parseutcstring(_inputtime.replace("+00:00","+0000"));
 //BA.debugLineNum = 2772;BA.debugLine="DateTime.DateFormat = \"MMM d, yyyy h:mm:ss a\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("MMM d, yyyy h:mm:ss a");
 //BA.debugLineNum = 2773;BA.debugLine="Dim lngTicks As Long = ticks";
_lngticks = _ticks;
 //BA.debugLineNum = 2774;BA.debugLine="Dim p As Period = DateUtils.PeriodBetween(lngTick";
_p = mostCurrent._dateutils._periodbetween(mostCurrent.activityBA,_lngticks,anywheresoftware.b4a.keywords.Common.DateTime.getNow());
 //BA.debugLineNum = 2776;BA.debugLine="If dayname.Contains(\"Today\") Then";
if (_dayname.contains("Today")) { 
 //BA.debugLineNum = 2777;BA.debugLine="If p.Days = 0 Then";
if (_p.Days==0) { 
 //BA.debugLineNum = 2778;BA.debugLine="If p.Hours = 0 Then";
if (_p.Hours==0) { 
 //BA.debugLineNum = 2779;BA.debugLine="If p.Minutes = 0 Then";
if (_p.Minutes==0) { 
 //BA.debugLineNum = 2780;BA.debugLine="Return p.Seconds & \"s ago\"";
if (true) return BA.NumberToString(_p.Seconds)+"s ago";
 }else {
 //BA.debugLineNum = 2782;BA.debugLine="Return p.Minutes & \"m \" & p.Seconds & \"s ago\"";
if (true) return BA.NumberToString(_p.Minutes)+"m "+BA.NumberToString(_p.Seconds)+"s ago";
 };
 }else {
 //BA.debugLineNum = 2785;BA.debugLine="Return p.Hours & \"h \" & p.Minutes & \"m \" & p.S";
if (true) return BA.NumberToString(_p.Hours)+"h "+BA.NumberToString(_p.Minutes)+"m "+BA.NumberToString(_p.Seconds)+"s ago";
 };
 }else if(_p.Hours==0) { 
 //BA.debugLineNum = 2788;BA.debugLine="If p.Minutes = 0 Then";
if (_p.Minutes==0) { 
 //BA.debugLineNum = 2789;BA.debugLine="Return p.Seconds & \"s ago\"";
if (true) return BA.NumberToString(_p.Seconds)+"s ago";
 }else {
 //BA.debugLineNum = 2791;BA.debugLine="Return p.Minutes & \"m \" & p.Seconds & \"s ago\"";
if (true) return BA.NumberToString(_p.Minutes)+"m "+BA.NumberToString(_p.Seconds)+"s ago";
 };
 }else if(_p.Minutes==0) { 
 //BA.debugLineNum = 2794;BA.debugLine="Return p.Seconds & \"s ago\"";
if (true) return BA.NumberToString(_p.Seconds)+"s ago";
 }else {
 //BA.debugLineNum = 2796;BA.debugLine="Return p.Days & \"d \" & p.Hours & \"h \" & p.Minut";
if (true) return BA.NumberToString(_p.Days)+"d "+BA.NumberToString(_p.Hours)+"h "+BA.NumberToString(_p.Minutes)+"m "+BA.NumberToString(_p.Seconds)+"s ago";
 };
 }else if(_dayname.contains("Yesterday")) { 
 //BA.debugLineNum = 2799;BA.debugLine="If p.Days = 0 Then";
if (_p.Days==0) { 
 //BA.debugLineNum = 2800;BA.debugLine="If p.Hours = 0 Then";
if (_p.Hours==0) { 
 //BA.debugLineNum = 2801;BA.debugLine="If p.Minutes = 0 Then";
if (_p.Minutes==0) { 
 //BA.debugLineNum = 2802;BA.debugLine="Return p.Seconds & \"s ago\"";
if (true) return BA.NumberToString(_p.Seconds)+"s ago";
 }else {
 //BA.debugLineNum = 2804;BA.debugLine="Return p.Minutes & \"m \" & p.Seconds & \"s ago\"";
if (true) return BA.NumberToString(_p.Minutes)+"m "+BA.NumberToString(_p.Seconds)+"s ago";
 };
 }else {
 //BA.debugLineNum = 2807;BA.debugLine="Return p.Hours & \"h \" & p.Minutes & \"m \" & p.S";
if (true) return BA.NumberToString(_p.Hours)+"h "+BA.NumberToString(_p.Minutes)+"m "+BA.NumberToString(_p.Seconds)+"s ago";
 };
 }else {
 //BA.debugLineNum = 2810;BA.debugLine="Return p.Days & \"d \" & p.Hours & \"h \" & p.Minut";
if (true) return BA.NumberToString(_p.Days)+"d "+BA.NumberToString(_p.Hours)+"h "+BA.NumberToString(_p.Minutes)+"m "+BA.NumberToString(_p.Seconds)+"s ago";
 };
 }else {
 //BA.debugLineNum = 2813;BA.debugLine="Return p.Days & \"d \" & p.Hours & \"h \" & p.Minute";
if (true) return BA.NumberToString(_p.Days)+"d "+BA.NumberToString(_p.Hours)+"h "+BA.NumberToString(_p.Minutes)+"m "+BA.NumberToString(_p.Seconds)+"s ago";
 };
 //BA.debugLineNum = 2816;BA.debugLine="End Sub";
return "";
}
public static String  _convertdayname(String _inputtime) throws Exception{
long _ticks = 0L;
long _lngticks = 0L;
long _yesterday = 0L;
long _timestamp = 0L;
long _tempcurrentdate = 0L;
long _temppastdate = 0L;
 //BA.debugLineNum = 2818;BA.debugLine="Sub ConvertDayName(inputTime As String) As String";
 //BA.debugLineNum = 2819;BA.debugLine="Try";
try { //BA.debugLineNum = 2821;BA.debugLine="Dim ticks As Long = ParseUTCstring(inputTime.Rep";
_ticks = _parseutcstring(_inputtime.replace("+00:00","+0000"));
 //BA.debugLineNum = 2822;BA.debugLine="DateTime.DateFormat = \"MMM d, yyyy h:mm:ss a\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("MMM d, yyyy h:mm:ss a");
 //BA.debugLineNum = 2823;BA.debugLine="Dim lngTicks As Long = ticks";
_lngticks = _ticks;
 //BA.debugLineNum = 2825;BA.debugLine="Dim Yesterday As Long";
_yesterday = 0L;
 //BA.debugLineNum = 2826;BA.debugLine="Dim timestamp As Long";
_timestamp = 0L;
 //BA.debugLineNum = 2827;BA.debugLine="DateTime.DateFormat = \"yyyyMMdd\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("yyyyMMdd");
 //BA.debugLineNum = 2828;BA.debugLine="Yesterday = DateTime.Date(DateTime.add(DateTime.";
_yesterday = (long)(Double.parseDouble(anywheresoftware.b4a.keywords.Common.DateTime.Date(anywheresoftware.b4a.keywords.Common.DateTime.Add(anywheresoftware.b4a.keywords.Common.DateTime.getNow(),(int) (0),(int) (0),(int) (-1)))));
 //BA.debugLineNum = 2829;BA.debugLine="timestamp = DateTime.Date(lngTicks)";
_timestamp = (long)(Double.parseDouble(anywheresoftware.b4a.keywords.Common.DateTime.Date(_lngticks)));
 //BA.debugLineNum = 2831;BA.debugLine="Dim tempCurrentDate As Long";
_tempcurrentdate = 0L;
 //BA.debugLineNum = 2832;BA.debugLine="Dim tempPastDate As Long";
_temppastdate = 0L;
 //BA.debugLineNum = 2834;BA.debugLine="tempCurrentDate = DateTime.Date(DateTime.add(Dat";
_tempcurrentdate = (long)(Double.parseDouble(anywheresoftware.b4a.keywords.Common.DateTime.Date(anywheresoftware.b4a.keywords.Common.DateTime.Add(anywheresoftware.b4a.keywords.Common.DateTime.getNow(),(int) (0),(int) (0),(int) (0)))));
 //BA.debugLineNum = 2835;BA.debugLine="tempPastDate = DateTime.Date(DateTime.add(DateTi";
_temppastdate = (long)(Double.parseDouble(anywheresoftware.b4a.keywords.Common.DateTime.Date(anywheresoftware.b4a.keywords.Common.DateTime.Add(anywheresoftware.b4a.keywords.Common.DateTime.getNow(),(int) (0),(int) (0),(int) (-7)))));
 //BA.debugLineNum = 2837;BA.debugLine="DateTime.DateFormat = \"h:mm:ss a\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("h:mm:ss a");
 //BA.debugLineNum = 2838;BA.debugLine="If DateUtils.IsSameDay(lngTicks,DateTime.now) Th";
if (mostCurrent._dateutils._issameday(mostCurrent.activityBA,_lngticks,anywheresoftware.b4a.keywords.Common.DateTime.getNow())) { 
 //BA.debugLineNum = 2839;BA.debugLine="Return \"Today\"";
if (true) return "Today";
 }else if(_yesterday==_timestamp) { 
 //BA.debugLineNum = 2841;BA.debugLine="Return \"Yesterday\"";
if (true) return "Yesterday";
 }else if(_timestamp>_temppastdate && _timestamp<_tempcurrentdate) { 
 //BA.debugLineNum = 2843;BA.debugLine="Return DateUtils.GetDayOfWeekName(lngTicks)";
if (true) return mostCurrent._dateutils._getdayofweekname(mostCurrent.activityBA,_lngticks);
 }else {
 //BA.debugLineNum = 2845;BA.debugLine="DateTime.DateFormat = \"MMMM d, yyyy\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("MMMM d, yyyy");
 //BA.debugLineNum = 2846;BA.debugLine="Return DateTime.Date(lngTicks) '& \" (\" & DateUt";
if (true) return anywheresoftware.b4a.keywords.Common.DateTime.Date(_lngticks);
 };
 } 
       catch (Exception e26) {
			processBA.setLastException(e26); //BA.debugLineNum = 2849;BA.debugLine="Return inputTime";
if (true) return _inputtime;
 //BA.debugLineNum = 2850;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("338207520",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 };
 //BA.debugLineNum = 2852;BA.debugLine="End Sub";
return "";
}
public static String  _convertfulldatetime(String _inputtime) throws Exception{
long _ticks = 0L;
long _lngticks = 0L;
long _yesterday = 0L;
long _timestamp = 0L;
 //BA.debugLineNum = 2854;BA.debugLine="Sub ConvertFullDateTime(inputTime As String) As St";
 //BA.debugLineNum = 2855;BA.debugLine="Try";
try { //BA.debugLineNum = 2857;BA.debugLine="Dim ticks As Long = ParseUTCstring(inputTime.Rep";
_ticks = _parseutcstring(_inputtime.replace("+00:00","+0000"));
 //BA.debugLineNum = 2858;BA.debugLine="DateTime.DateFormat = \"MMM d, yyyy h:mm:ss a\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("MMM d, yyyy h:mm:ss a");
 //BA.debugLineNum = 2859;BA.debugLine="Dim lngTicks As Long = ticks";
_lngticks = _ticks;
 //BA.debugLineNum = 2861;BA.debugLine="Dim Yesterday As Long";
_yesterday = 0L;
 //BA.debugLineNum = 2862;BA.debugLine="Dim timestamp As Long";
_timestamp = 0L;
 //BA.debugLineNum = 2863;BA.debugLine="DateTime.DateFormat = \"yyyyMMdd\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("yyyyMMdd");
 //BA.debugLineNum = 2864;BA.debugLine="Yesterday = DateTime.Date(DateTime.add(DateTime.";
_yesterday = (long)(Double.parseDouble(anywheresoftware.b4a.keywords.Common.DateTime.Date(anywheresoftware.b4a.keywords.Common.DateTime.Add(anywheresoftware.b4a.keywords.Common.DateTime.getNow(),(int) (0),(int) (0),(int) (-1)))));
 //BA.debugLineNum = 2865;BA.debugLine="timestamp = DateTime.Date(lngTicks)";
_timestamp = (long)(Double.parseDouble(anywheresoftware.b4a.keywords.Common.DateTime.Date(_lngticks)));
 //BA.debugLineNum = 2867;BA.debugLine="DateTime.DateFormat = \"h:mm:ss a\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("h:mm:ss a");
 //BA.debugLineNum = 2868;BA.debugLine="If DateUtils.IsSameDay(lngTicks,DateTime.now) Th";
if (mostCurrent._dateutils._issameday(mostCurrent.activityBA,_lngticks,anywheresoftware.b4a.keywords.Common.DateTime.getNow())) { 
 //BA.debugLineNum = 2869;BA.debugLine="Return DateTime.Date(lngTicks)";
if (true) return anywheresoftware.b4a.keywords.Common.DateTime.Date(_lngticks);
 }else if(_yesterday==_timestamp) { 
 //BA.debugLineNum = 2871;BA.debugLine="Return DateTime.Date(lngTicks)";
if (true) return anywheresoftware.b4a.keywords.Common.DateTime.Date(_lngticks);
 }else {
 //BA.debugLineNum = 2873;BA.debugLine="Return DateTime.Date(lngTicks)";
if (true) return anywheresoftware.b4a.keywords.Common.DateTime.Date(_lngticks);
 };
 } 
       catch (Exception e19) {
			processBA.setLastException(e19); //BA.debugLineNum = 2876;BA.debugLine="Return inputTime";
if (true) return _inputtime;
 //BA.debugLineNum = 2877;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("338273047",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 };
 //BA.debugLineNum = 2879;BA.debugLine="End Sub";
return "";
}
public static String  _converttickstotimestring(long _t) throws Exception{
int _hours = 0;
int _minutes = 0;
int _seconds = 0;
 //BA.debugLineNum = 2979;BA.debugLine="Sub ConvertTicksToTimeString(t As Long) As String";
 //BA.debugLineNum = 2980;BA.debugLine="Dim  hours, minutes, seconds As Int 'ignore";
_hours = 0;
_minutes = 0;
_seconds = 0;
 //BA.debugLineNum = 2981;BA.debugLine="hours = t / DateTime.TicksPerHour";
_hours = (int) (_t/(double)anywheresoftware.b4a.keywords.Common.DateTime.TicksPerHour);
 //BA.debugLineNum = 2982;BA.debugLine="minutes = (t Mod DateTime.TicksPerHour) / DateTim";
_minutes = (int) ((_t%anywheresoftware.b4a.keywords.Common.DateTime.TicksPerHour)/(double)anywheresoftware.b4a.keywords.Common.DateTime.TicksPerMinute);
 //BA.debugLineNum = 2983;BA.debugLine="seconds = (t Mod DateTime.TicksPerMinute) / DateT";
_seconds = (int) ((_t%anywheresoftware.b4a.keywords.Common.DateTime.TicksPerMinute)/(double)anywheresoftware.b4a.keywords.Common.DateTime.TicksPerSecond);
 //BA.debugLineNum = 2984;BA.debugLine="Return NumberFormat(minutes, 1, 0) & \":\" & Number";
if (true) return anywheresoftware.b4a.keywords.Common.NumberFormat(_minutes,(int) (1),(int) (0))+":"+anywheresoftware.b4a.keywords.Common.NumberFormat(_seconds,(int) (2),(int) (0));
 //BA.debugLineNum = 2985;BA.debugLine="End Sub";
return "";
}
public static cloyd.smart.home.monitor.main._videoinfo  _createcustomtype(String _thumbnailpath,String _datecreated,String _watched,String _devicename,String _videoid,byte[] _thumbnailblob) throws Exception{
cloyd.smart.home.monitor.main._videoinfo _ct = null;
 //BA.debugLineNum = 2569;BA.debugLine="Private Sub CreateCustomType(ThumbnailPath As Stri";
 //BA.debugLineNum = 2570;BA.debugLine="Dim ct As VideoInfo";
_ct = new cloyd.smart.home.monitor.main._videoinfo();
 //BA.debugLineNum = 2571;BA.debugLine="ct.Initialize";
_ct.Initialize();
 //BA.debugLineNum = 2572;BA.debugLine="ct.ThumbnailPath = ThumbnailPath";
_ct.ThumbnailPath /*String*/  = _thumbnailpath;
 //BA.debugLineNum = 2573;BA.debugLine="ct.DateCreated = DateCreated";
_ct.DateCreated /*String*/  = _datecreated;
 //BA.debugLineNum = 2574;BA.debugLine="ct.Watched = Watched";
_ct.Watched /*String*/  = _watched;
 //BA.debugLineNum = 2575;BA.debugLine="ct.DeviceName = DeviceName";
_ct.DeviceName /*String*/  = _devicename;
 //BA.debugLineNum = 2576;BA.debugLine="ct.ThumbnailBLOB = ThumbnailBLOB";
_ct.ThumbnailBLOB /*byte[]*/  = _thumbnailblob;
 //BA.debugLineNum = 2577;BA.debugLine="ct.VideoID = VideoID";
_ct.VideoID /*String*/  = _videoid;
 //BA.debugLineNum = 2578;BA.debugLine="Return ct";
if (true) return _ct;
 //BA.debugLineNum = 2579;BA.debugLine="End Sub";
return null;
}
public static String  _createpreferencescreen() throws Exception{
de.amberhome.objects.preferenceactivity.PreferenceCategoryWrapper _cat1 = null;
de.amberhome.objects.preferenceactivity.PreferenceCategoryWrapper _cat2 = null;
de.amberhome.objects.preferenceactivity.PreferenceCategoryWrapper _cat3 = null;
anywheresoftware.b4a.objects.IntentWrapper _in = null;
 //BA.debugLineNum = 1209;BA.debugLine="Sub CreatePreferenceScreen";
 //BA.debugLineNum = 1210;BA.debugLine="screen.Initialize(\"Settings\", \"\")";
_screen.Initialize("Settings","");
 //BA.debugLineNum = 1212;BA.debugLine="Dim cat1,cat2,cat3 As AHPreferenceCategory";
_cat1 = new de.amberhome.objects.preferenceactivity.PreferenceCategoryWrapper();
_cat2 = new de.amberhome.objects.preferenceactivity.PreferenceCategoryWrapper();
_cat3 = new de.amberhome.objects.preferenceactivity.PreferenceCategoryWrapper();
 //BA.debugLineNum = 1214;BA.debugLine="cat1.Initialize(\"Temperature & Humidity\")";
_cat1.Initialize("Temperature & Humidity");
 //BA.debugLineNum = 1215;BA.debugLine="cat1.AddEditText(\"HumidityAddValue\", \"Humidity Ad";
_cat1.AddEditText("HumidityAddValue","Humidity Additional Value","Value to be added to humidity to improve accuracy","6","");
 //BA.debugLineNum = 1217;BA.debugLine="cat2.Initialize(\"Special Settings\")";
_cat2.Initialize("Special Settings");
 //BA.debugLineNum = 1218;BA.debugLine="Dim In As Intent";
_in = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 1219;BA.debugLine="In.Initialize(\"android.settings.ACTION_NOTIFICATI";
_in.Initialize("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS","");
 //BA.debugLineNum = 1220;BA.debugLine="cat2.AddIntent(\"Notification Access\", \"Enable or";
_cat2.AddIntent("Notification Access","Enable or disable listening to notifications",(android.content.Intent)(_in.getObject()),BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 1222;BA.debugLine="cat3.Initialize(\"Sensors\")";
_cat3.Initialize("Sensors");
 //BA.debugLineNum = 1223;BA.debugLine="cat3.AddEditText(\"SensorNotRespondingTime\", \"Sens";
_cat3.AddEditText("SensorNotRespondingTime","Sensor Not Responding","Data age when to restart sensor","10","");
 //BA.debugLineNum = 1225;BA.debugLine="screen.AddPreferenceCategory(cat2)";
_screen.AddPreferenceCategory(_cat2);
 //BA.debugLineNum = 1226;BA.debugLine="screen.AddPreferenceCategory(cat1)";
_screen.AddPreferenceCategory(_cat1);
 //BA.debugLineNum = 1227;BA.debugLine="screen.AddPreferenceCategory(cat3)";
_screen.AddPreferenceCategory(_cat3);
 //BA.debugLineNum = 1228;BA.debugLine="StateManager.SetSetting(\"HumidityAddValue\",\"6\")";
mostCurrent._statemanager._setsetting /*String*/ (mostCurrent.activityBA,"HumidityAddValue","6");
 //BA.debugLineNum = 1229;BA.debugLine="StateManager.SetSetting(\"SensorNotRespondingTime\"";
mostCurrent._statemanager._setsetting /*String*/ (mostCurrent.activityBA,"SensorNotRespondingTime","10");
 //BA.debugLineNum = 1230;BA.debugLine="StateManager.SaveSettings";
mostCurrent._statemanager._savesettings /*String*/ (mostCurrent.activityBA);
 //BA.debugLineNum = 1231;BA.debugLine="End Sub";
return "";
}
public static anywheresoftware.b4a.keywords.Common.ResumableSubWrapper  _createtab4() throws Exception{
ResumableSub_CreateTab4 rsub = new ResumableSub_CreateTab4(null);
rsub.resume(processBA, null);
return (anywheresoftware.b4a.keywords.Common.ResumableSubWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper(), rsub);
}
public static class ResumableSub_CreateTab4 extends BA.ResumableSub {
public ResumableSub_CreateTab4(cloyd.smart.home.monitor.main parent) {
this.parent = parent;
}
cloyd.smart.home.monitor.main parent;
anywheresoftware.b4a.keywords.Common.ResumableSubWrapper _rs = null;
Object _result = null;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
try {

        switch (state) {
            case -1:
{
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,null);return;}
case 0:
//C
this.state = 1;
 //BA.debugLineNum = 894;BA.debugLine="Try";
if (true) break;

case 1:
//try
this.state = 22;
this.catchState = 21;
this.state = 3;
if (true) break;

case 3:
//C
this.state = 4;
this.catchState = 21;
 //BA.debugLineNum = 895;BA.debugLine="Pages = Array(False, False, False, False, True,";
parent._pages = anywheresoftware.b4a.keywords.Common.ArrayToList(new Object[]{(Object)(anywheresoftware.b4a.keywords.Common.False),(Object)(anywheresoftware.b4a.keywords.Common.False),(Object)(anywheresoftware.b4a.keywords.Common.False),(Object)(anywheresoftware.b4a.keywords.Common.False),(Object)(anywheresoftware.b4a.keywords.Common.True),(Object)(anywheresoftware.b4a.keywords.Common.False)});
 //BA.debugLineNum = 897;BA.debugLine="If lblSideYardBatt.Text = \"\" Or lblFrontYardBatt";
if (true) break;

case 4:
//if
this.state = 19;
if ((parent.mostCurrent._lblsideyardbatt.getText()).equals("") || (parent.mostCurrent._lblfrontyardbatt.getText()).equals("") || (parent.mostCurrent._lblbackyardbatt.getText()).equals("") || parent.mostCurrent._lblstatus.getText().contains("REST endpoint URL not found")) { 
this.state = 6;
}else if(parent.mostCurrent._ivsideyard.getBitmap()== null || parent.mostCurrent._ivfrontyard.getBitmap()== null || parent.mostCurrent._ivbackyard.getBitmap()== null) { 
this.state = 8;
}if (true) break;

case 6:
//C
this.state = 19;
 //BA.debugLineNum = 899;BA.debugLine="btnSideYard.Enabled = False";
parent.mostCurrent._btnsideyard._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 900;BA.debugLine="btnRefresh.Enabled = False";
parent.mostCurrent._btnrefresh._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 901;BA.debugLine="btnSideYardNewClip.Enabled = False";
parent.mostCurrent._btnsideyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 902;BA.debugLine="btnFrontYardNewClip.Enabled = False";
parent.mostCurrent._btnfrontyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 903;BA.debugLine="btnBackyardNewClip.Enabled = False";
parent.mostCurrent._btnbackyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 904;BA.debugLine="ScrollViewBlink.ScrollToNow(0)";
parent.mostCurrent._scrollviewblink.ScrollToNow((int) (0));
 //BA.debugLineNum = 905;BA.debugLine="BlurIV(\"SideYard.jpg\",ivSideYard)";
_bluriv("SideYard.jpg",parent.mostCurrent._ivsideyard);
 //BA.debugLineNum = 906;BA.debugLine="BlurIV(\"FrontYard.jpg\",ivFrontYard)";
_bluriv("FrontYard.jpg",parent.mostCurrent._ivfrontyard);
 //BA.debugLineNum = 907;BA.debugLine="BlurIV(\"Backyard.jpg\",ivBackyard)";
_bluriv("Backyard.jpg",parent.mostCurrent._ivbackyard);
 //BA.debugLineNum = 908;BA.debugLine="Dim rs As ResumableSub = RefreshCameras(True,\"A";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _refreshcameras(anywheresoftware.b4a.keywords.Common.True,"All");
 //BA.debugLineNum = 909;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 23;
return;
case 23:
//C
this.state = 19;
_result = (Object) result[0];
;
 if (true) break;

case 8:
//C
this.state = 9;
 //BA.debugLineNum = 912;BA.debugLine="If File.Exists(File.DirInternal, \"SideYard.jpg\"";
if (true) break;

case 9:
//if
this.state = 12;
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"SideYard.jpg")) { 
this.state = 11;
}if (true) break;

case 11:
//C
this.state = 12;
 //BA.debugLineNum = 913;BA.debugLine="bmp = xui.LoadBitmapResize(File.DirInternal, \"";
parent._bmp = parent.mostCurrent._xui.LoadBitmapResize(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"SideYard.jpg",parent.mostCurrent._ivsideyard.getWidth(),parent.mostCurrent._ivsideyard.getHeight(),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 914;BA.debugLine="ivSideYard.Bitmap = bmp";
parent.mostCurrent._ivsideyard.setBitmap((android.graphics.Bitmap)(parent._bmp.getObject()));
 if (true) break;
;
 //BA.debugLineNum = 916;BA.debugLine="If File.Exists(File.DirInternal, \"FrontYard.jpg";

case 12:
//if
this.state = 15;
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"FrontYard.jpg")) { 
this.state = 14;
}if (true) break;

case 14:
//C
this.state = 15;
 //BA.debugLineNum = 917;BA.debugLine="bmp = xui.LoadBitmapResize(File.DirInternal, \"";
parent._bmp = parent.mostCurrent._xui.LoadBitmapResize(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"FrontYard.jpg",parent.mostCurrent._ivfrontyard.getWidth(),parent.mostCurrent._ivfrontyard.getHeight(),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 918;BA.debugLine="ivFrontYard.Bitmap = bmp";
parent.mostCurrent._ivfrontyard.setBitmap((android.graphics.Bitmap)(parent._bmp.getObject()));
 if (true) break;
;
 //BA.debugLineNum = 920;BA.debugLine="If File.Exists(File.DirInternal, \"Backyard.jpg\"";

case 15:
//if
this.state = 18;
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"Backyard.jpg")) { 
this.state = 17;
}if (true) break;

case 17:
//C
this.state = 18;
 //BA.debugLineNum = 921;BA.debugLine="bmp = xui.LoadBitmapResize(File.DirInternal, \"";
parent._bmp = parent.mostCurrent._xui.LoadBitmapResize(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"Backyard.jpg",parent.mostCurrent._ivfrontyard.getWidth(),parent.mostCurrent._ivfrontyard.getHeight(),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 922;BA.debugLine="ivBackyard.Bitmap = bmp";
parent.mostCurrent._ivbackyard.setBitmap((android.graphics.Bitmap)(parent._bmp.getObject()));
 if (true) break;

case 18:
//C
this.state = 19;
;
 if (true) break;

case 19:
//C
this.state = 22;
;
 if (true) break;

case 21:
//C
this.state = 22;
this.catchState = 0;
 //BA.debugLineNum = 926;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("335651617",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 if (true) break;
if (true) break;

case 22:
//C
this.state = -1;
this.catchState = 0;
;
 //BA.debugLineNum = 928;BA.debugLine="Pages = Array(True, True, True, True, True, True)";
parent._pages = anywheresoftware.b4a.keywords.Common.ArrayToList(new Object[]{(Object)(anywheresoftware.b4a.keywords.Common.True),(Object)(anywheresoftware.b4a.keywords.Common.True),(Object)(anywheresoftware.b4a.keywords.Common.True),(Object)(anywheresoftware.b4a.keywords.Common.True),(Object)(anywheresoftware.b4a.keywords.Common.True),(Object)(anywheresoftware.b4a.keywords.Common.True)});
 //BA.debugLineNum = 929;BA.debugLine="Return Null";
if (true) {
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,anywheresoftware.b4a.keywords.Common.Null);return;};
 //BA.debugLineNum = 930;BA.debugLine="End Sub";
if (true) break;
}} 
       catch (Exception e0) {
			
if (catchState == 0)
    throw e0;
else {
    state = catchState;
processBA.setLastException(e0);}
            }
        }
    }
}
public static anywheresoftware.b4a.keywords.Common.ResumableSubWrapper  _downloadimage(String _link,anywheresoftware.b4a.objects.ImageViewWrapper _iv,String _camera) throws Exception{
ResumableSub_DownloadImage rsub = new ResumableSub_DownloadImage(null,_link,_iv,_camera);
rsub.resume(processBA, null);
return (anywheresoftware.b4a.keywords.Common.ResumableSubWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper(), rsub);
}
public static class ResumableSub_DownloadImage extends BA.ResumableSub {
public ResumableSub_DownloadImage(cloyd.smart.home.monitor.main parent,String _link,anywheresoftware.b4a.objects.ImageViewWrapper _iv,String _camera) {
this.parent = parent;
this._link = _link;
this._iv = _iv;
this._camera = _camera;
}
cloyd.smart.home.monitor.main parent;
String _link;
anywheresoftware.b4a.objects.ImageViewWrapper _iv;
String _camera;
cloyd.smart.home.monitor.httpjob _j = null;
anywheresoftware.b4a.objects.streams.File.OutputStreamWrapper _out = null;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
try {

        switch (state) {
            case -1:
{
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,null);return;}
case 0:
//C
this.state = 1;
 //BA.debugLineNum = 1672;BA.debugLine="Try";
if (true) break;

case 1:
//try
this.state = 20;
this.catchState = 19;
this.state = 3;
if (true) break;

case 3:
//C
this.state = 4;
this.catchState = 19;
 //BA.debugLineNum = 1673;BA.debugLine="Dim j As HttpJob";
_j = new cloyd.smart.home.monitor.httpjob();
 //BA.debugLineNum = 1674;BA.debugLine="response = \"\"";
parent._response = "";
 //BA.debugLineNum = 1675;BA.debugLine="j.Initialize(\"\", Me)";
_j._initialize /*String*/ (processBA,"",main.getObject());
 //BA.debugLineNum = 1676;BA.debugLine="j.Download(Link)";
_j._download /*String*/ (_link);
 //BA.debugLineNum = 1677;BA.debugLine="j.GetRequest.SetHeader(\"TOKEN_AUTH\", authToken)";
_j._getrequest /*anywheresoftware.b4h.okhttp.OkHttpClientWrapper.OkHttpRequest*/ ().SetHeader("TOKEN_AUTH",parent._authtoken);
 //BA.debugLineNum = 1678;BA.debugLine="Wait For (j) JobDone(j As HttpJob)";
anywheresoftware.b4a.keywords.Common.WaitFor("jobdone", processBA, this, (Object)(_j));
this.state = 21;
return;
case 21:
//C
this.state = 4;
_j = (cloyd.smart.home.monitor.httpjob) result[0];
;
 //BA.debugLineNum = 1679;BA.debugLine="If j.Success Then";
if (true) break;

case 4:
//if
this.state = 17;
if (_j._success /*boolean*/ ) { 
this.state = 6;
}else {
this.state = 16;
}if (true) break;

case 6:
//C
this.state = 7;
 //BA.debugLineNum = 1681;BA.debugLine="If camera = \"347574\" Then";
if (true) break;

case 7:
//if
this.state = 14;
if ((_camera).equals("347574")) { 
this.state = 9;
}else if((_camera).equals("236967")) { 
this.state = 11;
}else if((_camera).equals("458236")) { 
this.state = 13;
}if (true) break;

case 9:
//C
this.state = 14;
 //BA.debugLineNum = 1682;BA.debugLine="Dim out As OutputStream = File.OpenOutput(File";
_out = new anywheresoftware.b4a.objects.streams.File.OutputStreamWrapper();
_out = anywheresoftware.b4a.keywords.Common.File.OpenOutput(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"SideYard.jpg",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1683;BA.debugLine="File.Copy2(j.GetInputStream, out)";
anywheresoftware.b4a.keywords.Common.File.Copy2((java.io.InputStream)(_j._getinputstream /*anywheresoftware.b4a.objects.streams.File.InputStreamWrapper*/ ().getObject()),(java.io.OutputStream)(_out.getObject()));
 //BA.debugLineNum = 1684;BA.debugLine="out.Close '<------ very important";
_out.Close();
 //BA.debugLineNum = 1685;BA.debugLine="bmp = j.GetBitmapResize(iv.Width, iv.Height,Tr";
parent._bmp = (anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper(), (android.graphics.Bitmap)(_j._getbitmapresize /*anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper*/ (_iv.getWidth(),_iv.getHeight(),anywheresoftware.b4a.keywords.Common.True).getObject()));
 if (true) break;

case 11:
//C
this.state = 14;
 //BA.debugLineNum = 1687;BA.debugLine="Dim out As OutputStream = File.OpenOutput(File";
_out = new anywheresoftware.b4a.objects.streams.File.OutputStreamWrapper();
_out = anywheresoftware.b4a.keywords.Common.File.OpenOutput(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"FrontYard.jpg",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1688;BA.debugLine="File.Copy2(j.GetInputStream, out)";
anywheresoftware.b4a.keywords.Common.File.Copy2((java.io.InputStream)(_j._getinputstream /*anywheresoftware.b4a.objects.streams.File.InputStreamWrapper*/ ().getObject()),(java.io.OutputStream)(_out.getObject()));
 //BA.debugLineNum = 1689;BA.debugLine="out.Close '<------ very important";
_out.Close();
 //BA.debugLineNum = 1690;BA.debugLine="bmp = j.GetBitmapResize(iv.Width, iv.Height,Tr";
parent._bmp = (anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper(), (android.graphics.Bitmap)(_j._getbitmapresize /*anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper*/ (_iv.getWidth(),_iv.getHeight(),anywheresoftware.b4a.keywords.Common.True).getObject()));
 if (true) break;

case 13:
//C
this.state = 14;
 //BA.debugLineNum = 1692;BA.debugLine="Dim out As OutputStream = File.OpenOutput(File";
_out = new anywheresoftware.b4a.objects.streams.File.OutputStreamWrapper();
_out = anywheresoftware.b4a.keywords.Common.File.OpenOutput(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"Backyard.jpg",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1693;BA.debugLine="File.Copy2(j.GetInputStream, out)";
anywheresoftware.b4a.keywords.Common.File.Copy2((java.io.InputStream)(_j._getinputstream /*anywheresoftware.b4a.objects.streams.File.InputStreamWrapper*/ ().getObject()),(java.io.OutputStream)(_out.getObject()));
 //BA.debugLineNum = 1694;BA.debugLine="out.Close '<------ very important";
_out.Close();
 //BA.debugLineNum = 1695;BA.debugLine="bmp = j.GetBitmapResize(iv.Width, iv.Height,Tr";
parent._bmp = (anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper(), (android.graphics.Bitmap)(_j._getbitmapresize /*anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper*/ (_iv.getWidth(),_iv.getHeight(),anywheresoftware.b4a.keywords.Common.True).getObject()));
 if (true) break;

case 14:
//C
this.state = 17;
;
 //BA.debugLineNum = 1697;BA.debugLine="iv.Bitmap = bmp";
_iv.setBitmap((android.graphics.Bitmap)(parent._bmp.getObject()));
 if (true) break;

case 16:
//C
this.state = 17;
 //BA.debugLineNum = 1699;BA.debugLine="response = \"ERROR: \" & j.ErrorMessage";
parent._response = "ERROR: "+_j._errormessage /*String*/ ;
 //BA.debugLineNum = 1700;BA.debugLine="lblStatus.Text = GetRESTError(j.ErrorMessage)";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence(_getresterror(_j._errormessage /*String*/ )));
 if (true) break;

case 17:
//C
this.state = 20;
;
 //BA.debugLineNum = 1702;BA.debugLine="j.Release";
_j._release /*String*/ ();
 if (true) break;

case 19:
//C
this.state = 20;
this.catchState = 0;
 //BA.debugLineNum = 1704;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("336700193",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 if (true) break;
if (true) break;

case 20:
//C
this.state = -1;
this.catchState = 0;
;
 //BA.debugLineNum = 1706;BA.debugLine="Return Null";
if (true) {
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,anywheresoftware.b4a.keywords.Common.Null);return;};
 //BA.debugLineNum = 1707;BA.debugLine="End Sub";
if (true) break;
}} 
       catch (Exception e0) {
			
if (catchState == 0)
    throw e0;
else {
    state = catchState;
processBA.setLastException(e0);}
            }
        }
    }
}
public static void  _jobdone(cloyd.smart.home.monitor.httpjob _j) throws Exception{
}
public static anywheresoftware.b4a.keywords.Common.ResumableSubWrapper  _downloadimagefullscreen(String _link,String _camera) throws Exception{
ResumableSub_DownloadImageFullscreen rsub = new ResumableSub_DownloadImageFullscreen(null,_link,_camera);
rsub.resume(processBA, null);
return (anywheresoftware.b4a.keywords.Common.ResumableSubWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper(), rsub);
}
public static class ResumableSub_DownloadImageFullscreen extends BA.ResumableSub {
public ResumableSub_DownloadImageFullscreen(cloyd.smart.home.monitor.main parent,String _link,String _camera) {
this.parent = parent;
this._link = _link;
this._camera = _camera;
}
cloyd.smart.home.monitor.main parent;
String _link;
String _camera;
cloyd.smart.home.monitor.httpjob _j = null;
anywheresoftware.b4a.objects.streams.File.OutputStreamWrapper _out = null;
String _filename = "";
anywheresoftware.b4a.objects.IntentWrapper _in = null;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
try {

        switch (state) {
            case -1:
{
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,null);return;}
case 0:
//C
this.state = 1;
 //BA.debugLineNum = 3367;BA.debugLine="Try";
if (true) break;

case 1:
//try
this.state = 16;
this.catchState = 15;
this.state = 3;
if (true) break;

case 3:
//C
this.state = 4;
this.catchState = 15;
 //BA.debugLineNum = 3368;BA.debugLine="Dim j As HttpJob";
_j = new cloyd.smart.home.monitor.httpjob();
 //BA.debugLineNum = 3369;BA.debugLine="response = \"\"";
parent._response = "";
 //BA.debugLineNum = 3370;BA.debugLine="j.Initialize(\"\", Me)";
_j._initialize /*String*/ (processBA,"",main.getObject());
 //BA.debugLineNum = 3371;BA.debugLine="j.Download(Link)";
_j._download /*String*/ (_link);
 //BA.debugLineNum = 3372;BA.debugLine="j.GetRequest.SetHeader(\"TOKEN_AUTH\", authToken)";
_j._getrequest /*anywheresoftware.b4h.okhttp.OkHttpClientWrapper.OkHttpRequest*/ ().SetHeader("TOKEN_AUTH",parent._authtoken);
 //BA.debugLineNum = 3373;BA.debugLine="Wait For (j) JobDone(j As HttpJob)";
anywheresoftware.b4a.keywords.Common.WaitFor("jobdone", processBA, this, (Object)(_j));
this.state = 17;
return;
case 17:
//C
this.state = 4;
_j = (cloyd.smart.home.monitor.httpjob) result[0];
;
 //BA.debugLineNum = 3374;BA.debugLine="If j.Success Then";
if (true) break;

case 4:
//if
this.state = 13;
if (_j._success /*boolean*/ ) { 
this.state = 6;
}else {
this.state = 12;
}if (true) break;

case 6:
//C
this.state = 7;
 //BA.debugLineNum = 3376;BA.debugLine="Dim out As OutputStream = File.OpenOutput(File.";
_out = new anywheresoftware.b4a.objects.streams.File.OutputStreamWrapper();
_out = anywheresoftware.b4a.keywords.Common.File.OpenOutput(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"screenshot.jpg",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3377;BA.debugLine="File.Copy2(j.GetInputStream, out)";
anywheresoftware.b4a.keywords.Common.File.Copy2((java.io.InputStream)(_j._getinputstream /*anywheresoftware.b4a.objects.streams.File.InputStreamWrapper*/ ().getObject()),(java.io.OutputStream)(_out.getObject()));
 //BA.debugLineNum = 3378;BA.debugLine="out.Close '<------ very important";
_out.Close();
 //BA.debugLineNum = 3380;BA.debugLine="Dim FileName As String = \"screenshot.jpg\"";
_filename = "screenshot.jpg";
 //BA.debugLineNum = 3381;BA.debugLine="If File.Exists(File.DirInternal, FileName) Then";
if (true) break;

case 7:
//if
this.state = 10;
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),_filename)) { 
this.state = 9;
}if (true) break;

case 9:
//C
this.state = 10;
 //BA.debugLineNum = 3382;BA.debugLine="File.Copy(File.DirInternal, FileName, Starter.";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),_filename,parent.mostCurrent._starter._provider /*cloyd.smart.home.monitor.fileprovider*/ ._sharedfolder /*String*/ ,_filename);
 //BA.debugLineNum = 3383;BA.debugLine="Dim in As Intent";
_in = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 3384;BA.debugLine="in.Initialize(in.ACTION_VIEW, \"\")";
_in.Initialize(_in.ACTION_VIEW,"");
 //BA.debugLineNum = 3385;BA.debugLine="Starter.Provider.SetFileUriAsIntentData(in, Fi";
parent.mostCurrent._starter._provider /*cloyd.smart.home.monitor.fileprovider*/ ._setfileuriasintentdata /*String*/ (_in,_filename);
 //BA.debugLineNum = 3387;BA.debugLine="in.SetType(\"image/*\")";
_in.SetType("image/*");
 //BA.debugLineNum = 3388;BA.debugLine="StartActivity(in)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_in.getObject()));
 if (true) break;

case 10:
//C
this.state = 13;
;
 if (true) break;

case 12:
//C
this.state = 13;
 //BA.debugLineNum = 3391;BA.debugLine="response = \"ERROR: \" & j.ErrorMessage";
parent._response = "ERROR: "+_j._errormessage /*String*/ ;
 //BA.debugLineNum = 3392;BA.debugLine="lblStatus.Text = GetRESTError(j.ErrorMessage)";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence(_getresterror(_j._errormessage /*String*/ )));
 if (true) break;

case 13:
//C
this.state = 16;
;
 //BA.debugLineNum = 3394;BA.debugLine="j.Release";
_j._release /*String*/ ();
 if (true) break;

case 15:
//C
this.state = 16;
this.catchState = 0;
 //BA.debugLineNum = 3396;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("338928414",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 if (true) break;
if (true) break;

case 16:
//C
this.state = -1;
this.catchState = 0;
;
 //BA.debugLineNum = 3398;BA.debugLine="Return Null";
if (true) {
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,anywheresoftware.b4a.keywords.Common.Null);return;};
 //BA.debugLineNum = 3399;BA.debugLine="End Sub";
if (true) break;
}} 
       catch (Exception e0) {
			
if (catchState == 0)
    throw e0;
else {
    state = catchState;
processBA.setLastException(e0);}
            }
        }
    }
}
public static String  _getairquality(int _number) throws Exception{
 //BA.debugLineNum = 503;BA.debugLine="Sub GetAirQuality(number As Int) As String";
 //BA.debugLineNum = 506;BA.debugLine="If number <= 10 Then";
if (_number<=10) { 
 //BA.debugLineNum = 507;BA.debugLine="Return(\"Carbon monoxide perfect\")";
if (true) return ("Carbon monoxide perfect");
 }else if(((_number>10) && (_number<40)) || _number==40) { 
 //BA.debugLineNum = 509;BA.debugLine="Return(\"Carbon monoxide normal\")";
if (true) return ("Carbon monoxide normal");
 }else if(((_number>40) && (_number<90)) || _number==90) { 
 //BA.debugLineNum = 511;BA.debugLine="Return(\"Carbon monoxide high\")";
if (true) return ("Carbon monoxide high");
 }else if(_number>90) { 
 //BA.debugLineNum = 513;BA.debugLine="Return(\"ALARM Carbon monoxide very high\")";
if (true) return ("ALARM Carbon monoxide very high");
 }else {
 //BA.debugLineNum = 515;BA.debugLine="Return(\"MQ-7 - cant read any value - check the s";
if (true) return ("MQ-7 - cant read any value - check the sensor!");
 };
 //BA.debugLineNum = 517;BA.debugLine="End Sub";
return "";
}
public static anywheresoftware.b4a.objects.collections.List  _getalltablabels(anywheresoftware.b4a.objects.TabStripViewPager _tabstrip) throws Exception{
anywheresoftware.b4j.object.JavaObject _jo = null;
anywheresoftware.b4a.agraham.reflection.Reflection _r = null;
anywheresoftware.b4a.objects.PanelWrapper _tc = null;
anywheresoftware.b4a.objects.collections.List _res = null;
anywheresoftware.b4a.objects.ConcreteViewWrapper _v = null;
 //BA.debugLineNum = 932;BA.debugLine="Public Sub GetAllTabLabels (tabstrip As TabStrip)";
 //BA.debugLineNum = 933;BA.debugLine="Dim jo As JavaObject = tabstrip";
_jo = new anywheresoftware.b4j.object.JavaObject();
_jo = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(_tabstrip));
 //BA.debugLineNum = 934;BA.debugLine="Dim r As Reflector";
_r = new anywheresoftware.b4a.agraham.reflection.Reflection();
 //BA.debugLineNum = 935;BA.debugLine="r.Target = jo.GetField(\"tabStrip\")";
_r.Target = _jo.GetField("tabStrip");
 //BA.debugLineNum = 936;BA.debugLine="Dim tc As Panel = r.GetField(\"tabsContainer\")";
_tc = new anywheresoftware.b4a.objects.PanelWrapper();
_tc = (anywheresoftware.b4a.objects.PanelWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.PanelWrapper(), (android.view.ViewGroup)(_r.GetField("tabsContainer")));
 //BA.debugLineNum = 937;BA.debugLine="Dim res As List";
_res = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 938;BA.debugLine="res.Initialize";
_res.Initialize();
 //BA.debugLineNum = 939;BA.debugLine="For Each v As View In tc";
_v = new anywheresoftware.b4a.objects.ConcreteViewWrapper();
{
final anywheresoftware.b4a.BA.IterableList group7 = _tc;
final int groupLen7 = group7.getSize()
;int index7 = 0;
;
for (; index7 < groupLen7;index7++){
_v = (anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(group7.Get(index7)));
 //BA.debugLineNum = 940;BA.debugLine="If v Is Label Then res.Add(v)";
if (_v.getObjectOrNull() instanceof android.widget.TextView) { 
_res.Add((Object)(_v.getObject()));};
 }
};
 //BA.debugLineNum = 942;BA.debugLine="Return res";
if (true) return _res;
 //BA.debugLineNum = 943;BA.debugLine="End Sub";
return null;
}
public static anywheresoftware.b4a.objects.collections.List  _getalltablabelsforbadge(anywheresoftware.b4a.objects.TabStripViewPager _tabstrip) throws Exception{
anywheresoftware.b4j.object.JavaObject _jo = null;
anywheresoftware.b4a.agraham.reflection.Reflection _r = null;
anywheresoftware.b4a.objects.PanelWrapper _tc = null;
anywheresoftware.b4a.objects.collections.List _res = null;
anywheresoftware.b4a.objects.ConcreteViewWrapper _v = null;
 //BA.debugLineNum = 946;BA.debugLine="Public Sub GetAllTabLabelsForBadge (tabstrip As Ta";
 //BA.debugLineNum = 947;BA.debugLine="Dim jo As JavaObject = tabstrip";
_jo = new anywheresoftware.b4j.object.JavaObject();
_jo = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(_tabstrip));
 //BA.debugLineNum = 948;BA.debugLine="Dim r As Reflector";
_r = new anywheresoftware.b4a.agraham.reflection.Reflection();
 //BA.debugLineNum = 949;BA.debugLine="r.Target = jo.GetField(\"tabStrip\")";
_r.Target = _jo.GetField("tabStrip");
 //BA.debugLineNum = 950;BA.debugLine="Dim tc As Panel = r.GetField(\"tabsContainer\")";
_tc = new anywheresoftware.b4a.objects.PanelWrapper();
_tc = (anywheresoftware.b4a.objects.PanelWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.PanelWrapper(), (android.view.ViewGroup)(_r.GetField("tabsContainer")));
 //BA.debugLineNum = 951;BA.debugLine="Dim res As List";
_res = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 952;BA.debugLine="res.Initialize";
_res.Initialize();
 //BA.debugLineNum = 953;BA.debugLine="For Each v As View In tc";
_v = new anywheresoftware.b4a.objects.ConcreteViewWrapper();
{
final anywheresoftware.b4a.BA.IterableList group7 = _tc;
final int groupLen7 = group7.getSize()
;int index7 = 0;
;
for (; index7 < groupLen7;index7++){
_v = (anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(group7.Get(index7)));
 //BA.debugLineNum = 954;BA.debugLine="If v Is Label Then res.Add(v)";
if (_v.getObjectOrNull() instanceof android.widget.TextView) { 
_res.Add((Object)(_v.getObject()));};
 //BA.debugLineNum = 955;BA.debugLine="If v.Tag Is Label Then res.Add(v.Tag)";
if (_v.getTag() instanceof android.widget.TextView) { 
_res.Add(_v.getTag());};
 }
};
 //BA.debugLineNum = 957;BA.debugLine="Return res";
if (true) return _res;
 //BA.debugLineNum = 958;BA.debugLine="End Sub";
return null;
}
public static String  _getauthinfo(String _json) throws Exception{
anywheresoftware.b4a.objects.collections.JSONParser _parser = null;
anywheresoftware.b4a.objects.collections.Map _root = null;
anywheresoftware.b4a.objects.collections.Map _auth = null;
anywheresoftware.b4a.objects.collections.Map _authtokenmap = null;
anywheresoftware.b4a.objects.collections.Map _phone = null;
String _valid = "";
String _number = "";
String _last_4_digits = "";
String _country_calling_code = "";
String _force_password_reset = "";
int _lockout_time_remaining = 0;
int _allow_pin_resend_seconds = 0;
anywheresoftware.b4a.objects.collections.Map _account = null;
int _account_id = 0;
int _user_id = 0;
String _account_verification_required = "";
String _region = "";
String _phone_verification_required = "";
String _verification_channel = "";
String _new_account = "";
anywheresoftware.b4a.objects.collections.Map _verification = null;
String _channel = "";
String _required = "";
anywheresoftware.b4a.objects.collections.Map _email = null;
 //BA.debugLineNum = 1740;BA.debugLine="Sub GetAuthInfo(json As String)";
 //BA.debugLineNum = 1741;BA.debugLine="Try";
try { //BA.debugLineNum = 1742;BA.debugLine="lblStatus.Text = \"Getting authtoken...\"";
mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("Getting authtoken..."));
 //BA.debugLineNum = 1744;BA.debugLine="Dim parser As JSONParser";
_parser = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 1745;BA.debugLine="parser.Initialize(json)";
_parser.Initialize(_json);
 //BA.debugLineNum = 1746;BA.debugLine="Dim root As Map = parser.NextObject";
_root = new anywheresoftware.b4a.objects.collections.Map();
_root = _parser.NextObject();
 //BA.debugLineNum = 1747;BA.debugLine="Dim auth As Map = root.Get(\"auth\")";
_auth = new anywheresoftware.b4a.objects.collections.Map();
_auth = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (anywheresoftware.b4a.objects.collections.Map.MyMap)(_root.Get((Object)("auth"))));
 //BA.debugLineNum = 1748;BA.debugLine="Dim authtokenmap As Map = root.Get(\"authtoken\")";
_authtokenmap = new anywheresoftware.b4a.objects.collections.Map();
_authtokenmap = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (anywheresoftware.b4a.objects.collections.Map.MyMap)(_root.Get((Object)("authtoken"))));
 //BA.debugLineNum = 1749;BA.debugLine="authToken = auth.Get(\"token\")";
_authtoken = BA.ObjectToString(_auth.Get((Object)("token")));
 //BA.debugLineNum = 1750;BA.debugLine="StateManager.SetSetting(\"authToken\",authToken)";
mostCurrent._statemanager._setsetting /*String*/ (mostCurrent.activityBA,"authToken",_authtoken);
 //BA.debugLineNum = 1751;BA.debugLine="StateManager.SaveSettings";
mostCurrent._statemanager._savesettings /*String*/ (mostCurrent.activityBA);
 //BA.debugLineNum = 1752;BA.debugLine="Dim phone As Map = root.Get(\"phone\")";
_phone = new anywheresoftware.b4a.objects.collections.Map();
_phone = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (anywheresoftware.b4a.objects.collections.Map.MyMap)(_root.Get((Object)("phone"))));
 //BA.debugLineNum = 1753;BA.debugLine="Dim valid As String = phone.Get(\"valid\") 'ignore";
_valid = BA.ObjectToString(_phone.Get((Object)("valid")));
 //BA.debugLineNum = 1754;BA.debugLine="Dim number As String = phone.Get(\"number\") 'igno";
_number = BA.ObjectToString(_phone.Get((Object)("number")));
 //BA.debugLineNum = 1755;BA.debugLine="Dim last_4_digits As String = phone.Get(\"last_4_";
_last_4_digits = BA.ObjectToString(_phone.Get((Object)("last_4_digits")));
 //BA.debugLineNum = 1756;BA.debugLine="Dim country_calling_code As String = phone.Get(\"";
_country_calling_code = BA.ObjectToString(_phone.Get((Object)("country_calling_code")));
 //BA.debugLineNum = 1757;BA.debugLine="Dim force_password_reset As String = root.Get(\"f";
_force_password_reset = BA.ObjectToString(_root.Get((Object)("force_password_reset")));
 //BA.debugLineNum = 1758;BA.debugLine="Dim lockout_time_remaining As Int = root.Get(\"lo";
_lockout_time_remaining = (int)(BA.ObjectToNumber(_root.Get((Object)("lockout_time_remaining"))));
 //BA.debugLineNum = 1759;BA.debugLine="Dim allow_pin_resend_seconds As Int = root.Get(\"";
_allow_pin_resend_seconds = (int)(BA.ObjectToNumber(_root.Get((Object)("allow_pin_resend_seconds"))));
 //BA.debugLineNum = 1760;BA.debugLine="Dim account As Map = root.Get(\"account\")";
_account = new anywheresoftware.b4a.objects.collections.Map();
_account = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (anywheresoftware.b4a.objects.collections.Map.MyMap)(_root.Get((Object)("account"))));
 //BA.debugLineNum = 1762;BA.debugLine="Dim account_id As Int = account.Get(\"account_id\"";
_account_id = (int)(BA.ObjectToNumber(_account.Get((Object)("account_id"))));
 //BA.debugLineNum = 1763;BA.debugLine="userRegion = account.Get(\"tier\")";
_userregion = BA.ObjectToString(_account.Get((Object)("tier")));
 //BA.debugLineNum = 1764;BA.debugLine="Dim user_id As Int = account.Get(\"user_id\") 'ign";
_user_id = (int)(BA.ObjectToNumber(_account.Get((Object)("user_id"))));
 //BA.debugLineNum = 1765;BA.debugLine="Dim account_verification_required As String = ac";
_account_verification_required = BA.ObjectToString(_account.Get((Object)("account_verification_required")));
 //BA.debugLineNum = 1766;BA.debugLine="Dim region As String = account.Get(\"region\") 'ig";
_region = BA.ObjectToString(_account.Get((Object)("region")));
 //BA.debugLineNum = 1767;BA.debugLine="Dim phone_verification_required As String = acco";
_phone_verification_required = BA.ObjectToString(_account.Get((Object)("phone_verification_required")));
 //BA.debugLineNum = 1768;BA.debugLine="Dim verification_channel As String = account.Get";
_verification_channel = BA.ObjectToString(_account.Get((Object)("verification_channel")));
 //BA.debugLineNum = 1770;BA.debugLine="Dim new_account As String = account.Get(\"new_acc";
_new_account = BA.ObjectToString(_account.Get((Object)("new_account")));
 //BA.debugLineNum = 1771;BA.debugLine="Dim verification As Map = root.Get(\"verification";
_verification = new anywheresoftware.b4a.objects.collections.Map();
_verification = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (anywheresoftware.b4a.objects.collections.Map.MyMap)(_root.Get((Object)("verification"))));
 //BA.debugLineNum = 1772;BA.debugLine="Dim phone As Map = verification.Get(\"phone\")";
_phone = new anywheresoftware.b4a.objects.collections.Map();
_phone = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (anywheresoftware.b4a.objects.collections.Map.MyMap)(_verification.Get((Object)("phone"))));
 //BA.debugLineNum = 1773;BA.debugLine="Dim channel As String = phone.Get(\"channel\") 'ig";
_channel = BA.ObjectToString(_phone.Get((Object)("channel")));
 //BA.debugLineNum = 1774;BA.debugLine="Dim required As String = phone.Get(\"required\")";
_required = BA.ObjectToString(_phone.Get((Object)("required")));
 //BA.debugLineNum = 1775;BA.debugLine="Dim email As Map = verification.Get(\"email\")";
_email = new anywheresoftware.b4a.objects.collections.Map();
_email = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (anywheresoftware.b4a.objects.collections.Map.MyMap)(_verification.Get((Object)("email"))));
 //BA.debugLineNum = 1776;BA.debugLine="Dim required As String = email.Get(\"required\") '";
_required = BA.ObjectToString(_email.Get((Object)("required")));
 } 
       catch (Exception e35) {
			processBA.setLastException(e35); //BA.debugLineNum = 1778;BA.debugLine="lblStatus.Text = \"ERROR: GetAuthInfo - \" & LastE";
mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("ERROR: GetAuthInfo - "+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA))));
 //BA.debugLineNum = 1779;BA.debugLine="response = \"ERROR: GetAuthInfo - \" & LastExcepti";
_response = "ERROR: GetAuthInfo - "+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA));
 //BA.debugLineNum = 1780;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("336765736",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 };
 //BA.debugLineNum = 1783;BA.debugLine="End Sub";
return "";
}
public static anywheresoftware.b4a.keywords.Common.ResumableSubWrapper  _getcamerainfo(String _json,boolean _isjustinfoneeded) throws Exception{
ResumableSub_GetCameraInfo rsub = new ResumableSub_GetCameraInfo(null,_json,_isjustinfoneeded);
rsub.resume(processBA, null);
return (anywheresoftware.b4a.keywords.Common.ResumableSubWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper(), rsub);
}
public static class ResumableSub_GetCameraInfo extends BA.ResumableSub {
public ResumableSub_GetCameraInfo(cloyd.smart.home.monitor.main parent,String _json,boolean _isjustinfoneeded) {
this.parent = parent;
this._json = _json;
this._isjustinfoneeded = _isjustinfoneeded;
}
cloyd.smart.home.monitor.main parent;
String _json;
boolean _isjustinfoneeded;
anywheresoftware.b4a.objects.collections.JSONParser _parser = null;
anywheresoftware.b4a.objects.collections.Map _root = null;
anywheresoftware.b4a.objects.collections.Map _camera_status = null;
int _battery_voltage = 0;
int _wifi_strength = 0;
String _updated_at = "";
String _temperature = "";
int _camera_id = 0;
String _fw_version = "";

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
try {

        switch (state) {
            case -1:
{
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,null);return;}
case 0:
//C
this.state = 1;
 //BA.debugLineNum = 1879;BA.debugLine="Try";
if (true) break;

case 1:
//try
this.state = 45;
this.catchState = 44;
this.state = 3;
if (true) break;

case 3:
//C
this.state = 4;
this.catchState = 44;
 //BA.debugLineNum = 1880;BA.debugLine="Dim parser As JSONParser";
_parser = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 1881;BA.debugLine="parser.Initialize(json)";
_parser.Initialize(_json);
 //BA.debugLineNum = 1882;BA.debugLine="Dim root As Map = parser.NextObject";
_root = new anywheresoftware.b4a.objects.collections.Map();
_root = _parser.NextObject();
 //BA.debugLineNum = 1883;BA.debugLine="Dim camera_status As Map = root.Get(\"camera_stat";
_camera_status = new anywheresoftware.b4a.objects.collections.Map();
_camera_status = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (anywheresoftware.b4a.objects.collections.Map.MyMap)(_root.Get((Object)("camera_status"))));
 //BA.debugLineNum = 1885;BA.debugLine="Dim battery_voltage As Int = camera_status.Get(\"";
_battery_voltage = (int)(BA.ObjectToNumber(_camera_status.Get((Object)("battery_voltage"))));
 //BA.debugLineNum = 1889;BA.debugLine="Dim wifi_strength As Int = camera_status.Get(\"wi";
_wifi_strength = (int)(BA.ObjectToNumber(_camera_status.Get((Object)("wifi_strength"))));
 //BA.debugLineNum = 1896;BA.debugLine="Dim updated_at As String = camera_status.Get(\"up";
_updated_at = BA.ObjectToString(_camera_status.Get((Object)("updated_at")));
 //BA.debugLineNum = 1899;BA.debugLine="Dim temperature As String = camera_status.Get(\"t";
_temperature = BA.ObjectToString(_camera_status.Get((Object)("temperature")));
 //BA.debugLineNum = 1906;BA.debugLine="cameraThumbnail = camera_status.Get(\"thumbnail\")";
parent._camerathumbnail = BA.ObjectToString(_camera_status.Get((Object)("thumbnail")));
 //BA.debugLineNum = 1909;BA.debugLine="Dim camera_id As Int = camera_status.Get(\"camera";
_camera_id = (int)(BA.ObjectToNumber(_camera_status.Get((Object)("camera_id"))));
 //BA.debugLineNum = 1927;BA.debugLine="Dim fw_version As String = camera_status.Get(\"fw";
_fw_version = BA.ObjectToString(_camera_status.Get((Object)("fw_version")));
 //BA.debugLineNum = 1934;BA.debugLine="If isJustInfoNeeded Then Return Null";
if (true) break;

case 4:
//if
this.state = 9;
if (_isjustinfoneeded) { 
this.state = 6;
;}if (true) break;

case 6:
//C
this.state = 9;
if (true) {
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,anywheresoftware.b4a.keywords.Common.Null);return;};
if (true) break;

case 9:
//C
this.state = 10;
;
 //BA.debugLineNum = 1936;BA.debugLine="If camera_id = \"347574\" Then";
if (true) break;

case 10:
//if
this.state = 35;
if (_camera_id==(double)(Double.parseDouble("347574"))) { 
this.state = 12;
}else if(_camera_id==(double)(Double.parseDouble("236967"))) { 
this.state = 20;
}else if(_camera_id==(double)(Double.parseDouble("458236"))) { 
this.state = 28;
}if (true) break;

case 12:
//C
this.state = 13;
 //BA.debugLineNum = 1937;BA.debugLine="lblSideYardBatt.Text = NumberFormat2((battery_v";
parent.mostCurrent._lblsideyardbatt.setText(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.NumberFormat2((_battery_voltage/(double)100),(int) (0),(int) (2),(int) (2),anywheresoftware.b4a.keywords.Common.False)+"V "+parent._sideyardbattstatus));
 //BA.debugLineNum = 1938;BA.debugLine="lblSideYardTimestamp.Text = ConvertDateTime(upd";
parent.mostCurrent._lblsideyardtimestamp.setText(BA.ObjectToCharSequence(_convertdatetime(_updated_at)));
 //BA.debugLineNum = 1939;BA.debugLine="lblSideYard.Text = \"Side Yard v\" & fw_version &";
parent.mostCurrent._lblsideyard.setText(BA.ObjectToCharSequence("Side Yard v"+_fw_version+" "+parent._sideyardarmedstatus));
 //BA.debugLineNum = 1940;BA.debugLine="If CheckLFRLevel(wifi_strength) = \"Very good\" T";
if (true) break;

case 13:
//if
this.state = 18;
if ((_checklfrlevel(_wifi_strength)).equals("Very good")) { 
this.state = 15;
}else {
this.state = 17;
}if (true) break;

case 15:
//C
this.state = 18;
 //BA.debugLineNum = 1941;BA.debugLine="lblSideYardWifi.Text = \" |  \" & temperature &";
parent.mostCurrent._lblsideyardwifi.setText(BA.ObjectToCharSequence(" |  "+_temperature+"°F  |  "+BA.NumberToString(_wifi_strength)+"dBm - "+_checklfrlevel(_wifi_strength)+" | "));
 if (true) break;

case 17:
//C
this.state = 18;
 //BA.debugLineNum = 1943;BA.debugLine="lblSideYardWifi.Text = \" |  \" & temperature &";
parent.mostCurrent._lblsideyardwifi.setText(BA.ObjectToCharSequence(" |  "+_temperature+"°F  |  "+BA.NumberToString(_wifi_strength)+"dBm - "+_checklfrlevel(_wifi_strength)+"  | "));
 if (true) break;

case 18:
//C
this.state = 35;
;
 if (true) break;

case 20:
//C
this.state = 21;
 //BA.debugLineNum = 1946;BA.debugLine="lblFrontYardBatt.Text = NumberFormat2((battery_";
parent.mostCurrent._lblfrontyardbatt.setText(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.NumberFormat2((_battery_voltage/(double)100),(int) (0),(int) (2),(int) (2),anywheresoftware.b4a.keywords.Common.False)+"V "+parent._frontyardbattstatus));
 //BA.debugLineNum = 1947;BA.debugLine="lblFrontYardTimestamp.Text = ConvertDateTime(up";
parent.mostCurrent._lblfrontyardtimestamp.setText(BA.ObjectToCharSequence(_convertdatetime(_updated_at)));
 //BA.debugLineNum = 1948;BA.debugLine="lblFrontYard.Text = \"Front Yard v\" & fw_version";
parent.mostCurrent._lblfrontyard.setText(BA.ObjectToCharSequence("Front Yard v"+_fw_version+" "+parent._frontyardarmedstatus));
 //BA.debugLineNum = 1949;BA.debugLine="If CheckLFRLevel(wifi_strength) = \"Very good\" T";
if (true) break;

case 21:
//if
this.state = 26;
if ((_checklfrlevel(_wifi_strength)).equals("Very good")) { 
this.state = 23;
}else {
this.state = 25;
}if (true) break;

case 23:
//C
this.state = 26;
 //BA.debugLineNum = 1950;BA.debugLine="lblFrontYardWiFi.Text = \" |  \" & temperature &";
parent.mostCurrent._lblfrontyardwifi.setText(BA.ObjectToCharSequence(" |  "+_temperature+"°F  |  "+BA.NumberToString(_wifi_strength)+"dBm - "+_checklfrlevel(_wifi_strength)+" | "));
 if (true) break;

case 25:
//C
this.state = 26;
 //BA.debugLineNum = 1952;BA.debugLine="lblFrontYardWiFi.Text = \" |  \" & temperature &";
parent.mostCurrent._lblfrontyardwifi.setText(BA.ObjectToCharSequence(" |  "+_temperature+"°F  |  "+BA.NumberToString(_wifi_strength)+"dBm - "+_checklfrlevel(_wifi_strength)+"  | "));
 if (true) break;

case 26:
//C
this.state = 35;
;
 if (true) break;

case 28:
//C
this.state = 29;
 //BA.debugLineNum = 1955;BA.debugLine="lblBackyardBatt.Text = NumberFormat2((battery_v";
parent.mostCurrent._lblbackyardbatt.setText(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.NumberFormat2((_battery_voltage/(double)100),(int) (0),(int) (2),(int) (2),anywheresoftware.b4a.keywords.Common.False)+"V "+parent._backyardbattstatus));
 //BA.debugLineNum = 1956;BA.debugLine="lblBackyardTimestamp.Text = ConvertDateTime(upd";
parent.mostCurrent._lblbackyardtimestamp.setText(BA.ObjectToCharSequence(_convertdatetime(_updated_at)));
 //BA.debugLineNum = 1957;BA.debugLine="lblBackyard.Text = \"Backyard v\" & fw_version  &";
parent.mostCurrent._lblbackyard.setText(BA.ObjectToCharSequence("Backyard v"+_fw_version+" "+parent._backyardarmedstatus));
 //BA.debugLineNum = 1958;BA.debugLine="If CheckLFRLevel(wifi_strength) = \"Very good\" T";
if (true) break;

case 29:
//if
this.state = 34;
if ((_checklfrlevel(_wifi_strength)).equals("Very good")) { 
this.state = 31;
}else {
this.state = 33;
}if (true) break;

case 31:
//C
this.state = 34;
 //BA.debugLineNum = 1959;BA.debugLine="lblBackyardWiFi.Text = \" |  \" & temperature &";
parent.mostCurrent._lblbackyardwifi.setText(BA.ObjectToCharSequence(" |  "+_temperature+"°F  |  "+BA.NumberToString(_wifi_strength)+"dBm - "+_checklfrlevel(_wifi_strength)+" | "));
 if (true) break;

case 33:
//C
this.state = 34;
 //BA.debugLineNum = 1961;BA.debugLine="lblBackyardWiFi.Text = \" |  \" & temperature &";
parent.mostCurrent._lblbackyardwifi.setText(BA.ObjectToCharSequence(" |  "+_temperature+"°F  |  "+BA.NumberToString(_wifi_strength)+"dBm - "+_checklfrlevel(_wifi_strength)+"  | "));
 if (true) break;

case 34:
//C
this.state = 35;
;
 if (true) break;
;
 //BA.debugLineNum = 1964;BA.debugLine="If lblBackyardBatt.Text.Contains(\"OFFLINE\") Then";

case 35:
//if
this.state = 42;
if (parent.mostCurrent._lblbackyardbatt.getText().contains("OFFLINE")) { 
this.state = 37;
}else if(parent.mostCurrent._lblsideyardbatt.getText().contains("OFFLINE")) { 
this.state = 39;
}else if(parent.mostCurrent._lblfrontyardbatt.getText().contains("OFFLINE")) { 
this.state = 41;
}if (true) break;

case 37:
//C
this.state = 42;
 //BA.debugLineNum = 1965;BA.debugLine="lblBackyardBatt.Text = NumberFormat2((battery_v";
parent.mostCurrent._lblbackyardbatt.setText(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.NumberFormat2((_battery_voltage/(double)100),(int) (0),(int) (2),(int) (2),anywheresoftware.b4a.keywords.Common.False)+"V "+parent._backyardbattstatus));
 if (true) break;

case 39:
//C
this.state = 42;
 //BA.debugLineNum = 1967;BA.debugLine="lblSideYardBatt.Text = NumberFormat2((battery_v";
parent.mostCurrent._lblsideyardbatt.setText(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.NumberFormat2((_battery_voltage/(double)100),(int) (0),(int) (2),(int) (2),anywheresoftware.b4a.keywords.Common.False)+"V "+parent._sideyardbattstatus));
 if (true) break;

case 41:
//C
this.state = 42;
 //BA.debugLineNum = 1969;BA.debugLine="lblFrontYardBatt.Text = NumberFormat2((battery_";
parent.mostCurrent._lblfrontyardbatt.setText(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.NumberFormat2((_battery_voltage/(double)100),(int) (0),(int) (2),(int) (2),anywheresoftware.b4a.keywords.Common.False)+"V "+parent._frontyardbattstatus));
 if (true) break;

case 42:
//C
this.state = 45;
;
 if (true) break;

case 44:
//C
this.state = 45;
this.catchState = 0;
 //BA.debugLineNum = 1972;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("337027934",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 if (true) break;
if (true) break;

case 45:
//C
this.state = -1;
this.catchState = 0;
;
 //BA.debugLineNum = 1974;BA.debugLine="Return Null";
if (true) {
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,anywheresoftware.b4a.keywords.Common.Null);return;};
 //BA.debugLineNum = 1975;BA.debugLine="End Sub";
if (true) break;
}} 
       catch (Exception e0) {
			
if (catchState == 0)
    throw e0;
else {
    state = catchState;
processBA.setLastException(e0);}
            }
        }
    }
}
public static String  _getcomfort(String _dht11comfortstatus) throws Exception{
String _localcomfortstatus = "";
 //BA.debugLineNum = 553;BA.debugLine="Sub GetComfort(DHT11ComfortStatus As String) As St";
 //BA.debugLineNum = 554;BA.debugLine="Dim localcomfortstatus As String";
_localcomfortstatus = "";
 //BA.debugLineNum = 555;BA.debugLine="Select Case DHT11ComfortStatus";
switch (BA.switchObjectToInt(_dht11comfortstatus,BA.NumberToString(0),BA.NumberToString(1),BA.NumberToString(2),BA.NumberToString(4),BA.NumberToString(5),BA.NumberToString(6),BA.NumberToString(8),BA.NumberToString(9),BA.NumberToString(10))) {
case 0: {
 //BA.debugLineNum = 557;BA.debugLine="localcomfortstatus = \"OK\"";
_localcomfortstatus = "OK";
 break; }
case 1: {
 //BA.debugLineNum = 559;BA.debugLine="localcomfortstatus = \"Too hot\"";
_localcomfortstatus = "Too hot";
 break; }
case 2: {
 //BA.debugLineNum = 561;BA.debugLine="localcomfortstatus = \"Too cold\"";
_localcomfortstatus = "Too cold";
 break; }
case 3: {
 //BA.debugLineNum = 563;BA.debugLine="localcomfortstatus = \"Too dry\"";
_localcomfortstatus = "Too dry";
 break; }
case 4: {
 //BA.debugLineNum = 565;BA.debugLine="localcomfortstatus = \"Hot and dry\"";
_localcomfortstatus = "Hot and dry";
 break; }
case 5: {
 //BA.debugLineNum = 567;BA.debugLine="localcomfortstatus = \"Cold and dry\"";
_localcomfortstatus = "Cold and dry";
 break; }
case 6: {
 //BA.debugLineNum = 569;BA.debugLine="localcomfortstatus = \"Too humid\"";
_localcomfortstatus = "Too humid";
 break; }
case 7: {
 //BA.debugLineNum = 571;BA.debugLine="localcomfortstatus = \"Hot and humid\"";
_localcomfortstatus = "Hot and humid";
 break; }
case 8: {
 //BA.debugLineNum = 573;BA.debugLine="localcomfortstatus = \"Cold and humid\"";
_localcomfortstatus = "Cold and humid";
 break; }
default: {
 //BA.debugLineNum = 575;BA.debugLine="localcomfortstatus = \"Unknown\"";
_localcomfortstatus = "Unknown";
 break; }
}
;
 //BA.debugLineNum = 577;BA.debugLine="Return localcomfortstatus";
if (true) return _localcomfortstatus;
 //BA.debugLineNum = 578;BA.debugLine="End Sub";
return "";
}
public static anywheresoftware.b4a.keywords.Common.ResumableSubWrapper  _getcommandid(String _json) throws Exception{
ResumableSub_GetCommandID rsub = new ResumableSub_GetCommandID(null,_json);
rsub.resume(processBA, null);
return (anywheresoftware.b4a.keywords.Common.ResumableSubWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper(), rsub);
}
public static class ResumableSub_GetCommandID extends BA.ResumableSub {
public ResumableSub_GetCommandID(cloyd.smart.home.monitor.main parent,String _json) {
this.parent = parent;
this._json = _json;
}
cloyd.smart.home.monitor.main parent;
String _json;
anywheresoftware.b4a.objects.collections.JSONParser _parser = null;
anywheresoftware.b4a.objects.collections.Map _root = null;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
try {

        switch (state) {
            case -1:
{
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,null);return;}
case 0:
//C
this.state = 1;
 //BA.debugLineNum = 1787;BA.debugLine="Try";
if (true) break;

case 1:
//try
this.state = 6;
this.catchState = 5;
this.state = 3;
if (true) break;

case 3:
//C
this.state = 6;
this.catchState = 5;
 //BA.debugLineNum = 1788;BA.debugLine="Dim parser As JSONParser";
_parser = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 1789;BA.debugLine="parser.Initialize(json)";
_parser.Initialize(_json);
 //BA.debugLineNum = 1790;BA.debugLine="Dim root As Map = parser.NextObject";
_root = new anywheresoftware.b4a.objects.collections.Map();
_root = _parser.NextObject();
 //BA.debugLineNum = 1805;BA.debugLine="commandID = root.Get(\"id\")";
parent._commandid = BA.ObjectToString(_root.Get((Object)("id")));
 //BA.debugLineNum = 1806;BA.debugLine="Log(\"commandID: \" & commandID)";
anywheresoftware.b4a.keywords.Common.LogImpl("336831252","commandID: "+parent._commandid,0);
 if (true) break;

case 5:
//C
this.state = 6;
this.catchState = 0;
 //BA.debugLineNum = 1828;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("336831274",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 if (true) break;
if (true) break;

case 6:
//C
this.state = -1;
this.catchState = 0;
;
 //BA.debugLineNum = 1831;BA.debugLine="Return Null";
if (true) {
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,anywheresoftware.b4a.keywords.Common.Null);return;};
 //BA.debugLineNum = 1832;BA.debugLine="End Sub";
if (true) break;
}} 
       catch (Exception e0) {
			
if (catchState == 0)
    throw e0;
else {
    state = catchState;
processBA.setLastException(e0);}
            }
        }
    }
}
public static anywheresoftware.b4a.keywords.Common.ResumableSubWrapper  _getcommandresponse(anywheresoftware.b4a.objects.ImageViewWrapper _iv,String _camera,String _attempts,String _attemptsallowed) throws Exception{
ResumableSub_GetCommandResponse rsub = new ResumableSub_GetCommandResponse(null,_iv,_camera,_attempts,_attemptsallowed);
rsub.resume(processBA, null);
return (anywheresoftware.b4a.keywords.Common.ResumableSubWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper(), rsub);
}
public static class ResumableSub_GetCommandResponse extends BA.ResumableSub {
public ResumableSub_GetCommandResponse(cloyd.smart.home.monitor.main parent,anywheresoftware.b4a.objects.ImageViewWrapper _iv,String _camera,String _attempts,String _attemptsallowed) {
this.parent = parent;
this._iv = _iv;
this._camera = _camera;
this._attempts = _attempts;
this._attemptsallowed = _attemptsallowed;
}
cloyd.smart.home.monitor.main parent;
anywheresoftware.b4a.objects.ImageViewWrapper _iv;
String _camera;
String _attempts;
String _attemptsallowed;
anywheresoftware.b4a.keywords.Common.ResumableSubWrapper _rs = null;
Object _result = null;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
{
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,null);return;}
case 0:
//C
this.state = 1;
 //BA.debugLineNum = 1561;BA.debugLine="GetCommandStatus(response)";
_getcommandstatus(parent._response);
 //BA.debugLineNum = 1562;BA.debugLine="If commandComplete Then";
if (true) break;

case 1:
//if
this.state = 4;
if (parent._commandcomplete) { 
this.state = 3;
}if (true) break;

case 3:
//C
this.state = 4;
 //BA.debugLineNum = 1564;BA.debugLine="Dim rs As ResumableSub = RESTGet(\"https://rest-\"";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _restget("https://rest-"+parent._userregion+".immedia-semi.com/network/"+parent._networkid+"/camera/"+_camera);
 //BA.debugLineNum = 1565;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 19;
return;
case 19:
//C
this.state = 4;
_result = (Object) result[0];
;
 //BA.debugLineNum = 1567;BA.debugLine="Dim rs As ResumableSub = GetCameraInfo(response,";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _getcamerainfo(parent._response,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1568;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 20;
return;
case 20:
//C
this.state = 4;
_result = (Object) result[0];
;
 //BA.debugLineNum = 1570;BA.debugLine="Dim rs As ResumableSub = DownloadImage(\"https://";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _downloadimage("https://rest-"+parent._userregion+".immedia-semi.com/"+parent._camerathumbnail+".jpg",_iv,_camera);
 //BA.debugLineNum = 1571;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 21;
return;
case 21:
//C
this.state = 4;
_result = (Object) result[0];
;
 //BA.debugLineNum = 1572;BA.debugLine="Return True";
if (true) {
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,(Object)(anywheresoftware.b4a.keywords.Common.True));return;};
 if (true) break;

case 4:
//C
this.state = 5;
;
 //BA.debugLineNum = 1575;BA.debugLine="Dim rs As ResumableSub = RESTGet(\"https://rest-\"";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _restget("https://rest-"+parent._userregion+".immedia-semi.com/network/"+parent._networkid+"/camera/"+_camera);
 //BA.debugLineNum = 1576;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 22;
return;
case 22:
//C
this.state = 5;
_result = (Object) result[0];
;
 //BA.debugLineNum = 1578;BA.debugLine="Dim rs As ResumableSub = GetCameraInfo(response,T";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _getcamerainfo(parent._response,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1579;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 23;
return;
case 23:
//C
this.state = 5;
_result = (Object) result[0];
;
 //BA.debugLineNum = 1581;BA.debugLine="If prevCameraThumbnail <> \"\" And prevCameraThumbn";
if (true) break;

case 5:
//if
this.state = 18;
if ((parent._prevcamerathumbnail).equals("") == false && (parent._prevcamerathumbnail).equals(parent._camerathumbnail) == false) { 
this.state = 7;
}else {
this.state = 9;
}if (true) break;

case 7:
//C
this.state = 18;
 //BA.debugLineNum = 1582;BA.debugLine="Log(\"**** ALRIGHT \" & attempts & \"/\" & attemptsA";
anywheresoftware.b4a.keywords.Common.LogImpl("336503574","**** ALRIGHT "+_attempts+"/"+_attemptsallowed+" *****",0);
 //BA.debugLineNum = 1583;BA.debugLine="Dim rs As ResumableSub = RESTGet(\"https://rest-\"";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _restget("https://rest-"+parent._userregion+".immedia-semi.com/network/"+parent._networkid+"/camera/"+_camera);
 //BA.debugLineNum = 1584;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 24;
return;
case 24:
//C
this.state = 18;
_result = (Object) result[0];
;
 //BA.debugLineNum = 1586;BA.debugLine="Dim rs As ResumableSub = GetCameraInfo(response,";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _getcamerainfo(parent._response,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1587;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 25;
return;
case 25:
//C
this.state = 18;
_result = (Object) result[0];
;
 //BA.debugLineNum = 1589;BA.debugLine="Dim rs As ResumableSub = DownloadImage(\"https://";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _downloadimage("https://rest-"+parent._userregion+".immedia-semi.com/"+parent._camerathumbnail+".jpg",_iv,_camera);
 //BA.debugLineNum = 1590;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 26;
return;
case 26:
//C
this.state = 18;
_result = (Object) result[0];
;
 //BA.debugLineNum = 1591;BA.debugLine="Return True";
if (true) {
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,(Object)(anywheresoftware.b4a.keywords.Common.True));return;};
 if (true) break;

case 9:
//C
this.state = 10;
 //BA.debugLineNum = 1593;BA.debugLine="If camera = \"347574\" Then";
if (true) break;

case 10:
//if
this.state = 17;
if ((_camera).equals("347574")) { 
this.state = 12;
}else if((_camera).equals("236967")) { 
this.state = 14;
}else if((_camera).equals("458236")) { 
this.state = 16;
}if (true) break;

case 12:
//C
this.state = 17;
 //BA.debugLineNum = 1594;BA.debugLine="lblStatus.Text = \"Awaiting for the Side Yard th";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("Awaiting for the Side Yard thumbnail... "+_attempts+"/"+_attemptsallowed));
 if (true) break;

case 14:
//C
this.state = 17;
 //BA.debugLineNum = 1596;BA.debugLine="lblStatus.Text = \"Awaiting for the Front Yard t";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("Awaiting for the Front Yard thumbnail...  "+_attempts+"/"+_attemptsallowed));
 if (true) break;

case 16:
//C
this.state = 17;
 //BA.debugLineNum = 1598;BA.debugLine="lblStatus.Text = \"Awaiting for the Backyard thu";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("Awaiting for the Backyard thumbnail... "+_attempts+"/"+_attemptsallowed));
 if (true) break;

case 17:
//C
this.state = 18;
;
 //BA.debugLineNum = 1600;BA.debugLine="Return False";
if (true) {
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,(Object)(anywheresoftware.b4a.keywords.Common.False));return;};
 if (true) break;

case 18:
//C
this.state = -1;
;
 //BA.debugLineNum = 1602;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static anywheresoftware.b4a.keywords.Common.ResumableSubWrapper  _getcommandstatus(String _json) throws Exception{
ResumableSub_GetCommandStatus rsub = new ResumableSub_GetCommandStatus(null,_json);
rsub.resume(processBA, null);
return (anywheresoftware.b4a.keywords.Common.ResumableSubWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper(), rsub);
}
public static class ResumableSub_GetCommandStatus extends BA.ResumableSub {
public ResumableSub_GetCommandStatus(cloyd.smart.home.monitor.main parent,String _json) {
this.parent = parent;
this._json = _json;
}
cloyd.smart.home.monitor.main parent;
String _json;
anywheresoftware.b4a.objects.collections.JSONParser _parser = null;
anywheresoftware.b4a.objects.collections.Map _root = null;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
try {

        switch (state) {
            case -1:
{
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,null);return;}
case 0:
//C
this.state = 1;
 //BA.debugLineNum = 1835;BA.debugLine="Try";
if (true) break;

case 1:
//try
this.state = 6;
this.catchState = 5;
this.state = 3;
if (true) break;

case 3:
//C
this.state = 6;
this.catchState = 5;
 //BA.debugLineNum = 1836;BA.debugLine="Dim parser As JSONParser";
_parser = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 1837;BA.debugLine="parser.Initialize(json)";
_parser.Initialize(_json);
 //BA.debugLineNum = 1838;BA.debugLine="Dim root As Map = parser.NextObject";
_root = new anywheresoftware.b4a.objects.collections.Map();
_root = _parser.NextObject();
 //BA.debugLineNum = 1839;BA.debugLine="commandComplete = root.Get(\"complete\")";
parent._commandcomplete = BA.ObjectToBoolean(_root.Get((Object)("complete")));
 //BA.debugLineNum = 1840;BA.debugLine="Log(\"commandComplete: \" & commandComplete)";
anywheresoftware.b4a.keywords.Common.LogImpl("336896774","commandComplete: "+BA.ObjectToString(parent._commandcomplete),0);
 if (true) break;

case 5:
//C
this.state = 6;
this.catchState = 0;
 //BA.debugLineNum = 1842;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("336896776",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 if (true) break;
if (true) break;

case 6:
//C
this.state = -1;
this.catchState = 0;
;
 //BA.debugLineNum = 1845;BA.debugLine="Return Null";
if (true) {
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,anywheresoftware.b4a.keywords.Common.Null);return;};
 //BA.debugLineNum = 1846;BA.debugLine="End Sub";
if (true) break;
}} 
       catch (Exception e0) {
			
if (catchState == 0)
    throw e0;
else {
    state = catchState;
processBA.setLastException(e0);}
            }
        }
    }
}
public static float  _getfreemem() throws Exception{
anywheresoftware.b4a.agraham.reflection.Reflection _r = null;
int _mm = 0;
int _tm = 0;
int _fm = 0;
int _total = 0;
 //BA.debugLineNum = 580;BA.debugLine="Sub GetFreeMem As Float";
 //BA.debugLineNum = 581;BA.debugLine="Dim r As Reflector";
_r = new anywheresoftware.b4a.agraham.reflection.Reflection();
 //BA.debugLineNum = 582;BA.debugLine="Dim MM, TM, FM, Total As Int";
_mm = 0;
_tm = 0;
_fm = 0;
_total = 0;
 //BA.debugLineNum = 583;BA.debugLine="r.Target = r.RunStaticMethod(\"java.lang.Runtime\",";
_r.Target = _r.RunStaticMethod("java.lang.Runtime","getRuntime",(Object[])(anywheresoftware.b4a.keywords.Common.Null),(String[])(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 584;BA.debugLine="MM = r.RunMethod(\"maxMemory\")";
_mm = (int)(BA.ObjectToNumber(_r.RunMethod("maxMemory")));
 //BA.debugLineNum = 585;BA.debugLine="FM = r.RunMethod(\"freeMemory\")";
_fm = (int)(BA.ObjectToNumber(_r.RunMethod("freeMemory")));
 //BA.debugLineNum = 586;BA.debugLine="TM = r.RunMethod(\"totalMemory\")";
_tm = (int)(BA.ObjectToNumber(_r.RunMethod("totalMemory")));
 //BA.debugLineNum = 587;BA.debugLine="Total = MM + FM - TM";
_total = (int) (_mm+_fm-_tm);
 //BA.debugLineNum = 588;BA.debugLine="Return Total / 1024";
if (true) return (float) (_total/(double)1024);
 //BA.debugLineNum = 589;BA.debugLine="End Sub";
return 0f;
}
public static anywheresoftware.b4a.keywords.Common.ResumableSubWrapper  _gethomescreen(String _json) throws Exception{
ResumableSub_GetHomescreen rsub = new ResumableSub_GetHomescreen(null,_json);
rsub.resume(processBA, null);
return (anywheresoftware.b4a.keywords.Common.ResumableSubWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper(), rsub);
}
public static class ResumableSub_GetHomescreen extends BA.ResumableSub {
public ResumableSub_GetHomescreen(cloyd.smart.home.monitor.main parent,String _json) {
this.parent = parent;
this._json = _json;
}
cloyd.smart.home.monitor.main parent;
String _json;
anywheresoftware.b4a.objects.collections.JSONParser _parser = null;
anywheresoftware.b4a.objects.collections.Map _root = null;
anywheresoftware.b4a.objects.collections.List _cameras = null;
anywheresoftware.b4a.objects.collections.Map _colcameras = null;
String _battery_state = "";
String _enabled = "";
anywheresoftware.b4a.objects.collections.Map _signals = null;
int _battery = 0;
String _name = "";
String _status = "";
anywheresoftware.b4a.objects.IntentWrapper _intent1 = null;
anywheresoftware.b4a.phone.Phone _phone = null;
anywheresoftware.b4j.object.JavaObject _jo = null;
float _radius = 0f;
float _dx = 0f;
float _dy = 0f;
anywheresoftware.b4a.objects.collections.List _networks = null;
anywheresoftware.b4a.objects.collections.Map _colnetworks = null;
String _armed = "";
anywheresoftware.b4a.BA.IterableList group6;
int index6;
int groupLen6;
anywheresoftware.b4a.BA.IterableList group129;
int index129;
int groupLen129;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
try {

        switch (state) {
            case -1:
{
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,null);return;}
case 0:
//C
this.state = 1;
 //BA.debugLineNum = 2023;BA.debugLine="Try";
if (true) break;

case 1:
//try
this.state = 100;
this.catchState = 99;
this.state = 3;
if (true) break;

case 3:
//C
this.state = 4;
this.catchState = 99;
 //BA.debugLineNum = 2024;BA.debugLine="Dim parser As JSONParser";
_parser = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 2025;BA.debugLine="parser.Initialize(json)";
_parser.Initialize(_json);
 //BA.debugLineNum = 2026;BA.debugLine="Dim root As Map = parser.NextObject";
_root = new anywheresoftware.b4a.objects.collections.Map();
_root = _parser.NextObject();
 //BA.debugLineNum = 2027;BA.debugLine="Dim cameras As List = root.Get(\"cameras\")";
_cameras = new anywheresoftware.b4a.objects.collections.List();
_cameras = (anywheresoftware.b4a.objects.collections.List) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.List(), (java.util.List)(_root.Get((Object)("cameras"))));
 //BA.debugLineNum = 2054;BA.debugLine="For Each colcameras As Map In cameras";
if (true) break;

case 4:
//for
this.state = 87;
_colcameras = new anywheresoftware.b4a.objects.collections.Map();
group6 = _cameras;
index6 = 0;
groupLen6 = group6.getSize();
this.state = 101;
if (true) break;

case 101:
//C
this.state = 87;
if (index6 < groupLen6) {
this.state = 6;
_colcameras = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (anywheresoftware.b4a.objects.collections.Map.MyMap)(group6.Get(index6)));}
if (true) break;

case 102:
//C
this.state = 101;
index6++;
if (true) break;

case 6:
//C
this.state = 7;
 //BA.debugLineNum = 2058;BA.debugLine="Dim battery_state As String = colcameras.Get(\"b";
_battery_state = BA.ObjectToString(_colcameras.Get((Object)("battery")));
 //BA.debugLineNum = 2060;BA.debugLine="Dim enabled As String = colcameras.Get(\"enabled";
_enabled = BA.ObjectToString(_colcameras.Get((Object)("enabled")));
 //BA.debugLineNum = 2062;BA.debugLine="Dim signals As Map = colcameras.Get(\"signals\")";
_signals = new anywheresoftware.b4a.objects.collections.Map();
_signals = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (anywheresoftware.b4a.objects.collections.Map.MyMap)(_colcameras.Get((Object)("signals"))));
 //BA.debugLineNum = 2066;BA.debugLine="Dim battery As Int = signals.Get(\"battery\")";
_battery = (int)(BA.ObjectToNumber(_signals.Get((Object)("battery"))));
 //BA.debugLineNum = 2070;BA.debugLine="Dim name As String = colcameras.Get(\"name\")";
_name = BA.ObjectToString(_colcameras.Get((Object)("name")));
 //BA.debugLineNum = 2073;BA.debugLine="Dim status As String = colcameras.Get(\"status\")";
_status = BA.ObjectToString(_colcameras.Get((Object)("status")));
 //BA.debugLineNum = 2076;BA.debugLine="Dim Intent1 As Intent";
_intent1 = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 2077;BA.debugLine="Dim Phone As Phone";
_phone = new anywheresoftware.b4a.phone.Phone();
 //BA.debugLineNum = 2079;BA.debugLine="If name = \"Side Yard\" Then";
if (true) break;

case 7:
//if
this.state = 86;
if ((_name).equals("Side Yard")) { 
this.state = 9;
}else if((_name).equals("Front Yard")) { 
this.state = 35;
}else if((_name).equals("Backyard")) { 
this.state = 61;
}if (true) break;

case 9:
//C
this.state = 10;
 //BA.debugLineNum = 2080;BA.debugLine="If enabled = \"true\" Then";
if (true) break;

case 10:
//if
this.state = 21;
if ((_enabled).equals("true")) { 
this.state = 12;
}else {
this.state = 20;
}if (true) break;

case 12:
//C
this.state = 13;
 //BA.debugLineNum = 2081;BA.debugLine="If enabled = \"true\" Then";
if (true) break;

case 13:
//if
this.state = 18;
if ((_enabled).equals("true")) { 
this.state = 15;
}else {
this.state = 17;
}if (true) break;

case 15:
//C
this.state = 18;
 //BA.debugLineNum = 2082;BA.debugLine="SideYardArmedStatus = \"\"";
parent._sideyardarmedstatus = "";
 //BA.debugLineNum = 2083;BA.debugLine="Dim jo As JavaObject = lblSideYard";
_jo = new anywheresoftware.b4j.object.JavaObject();
_jo = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(parent.mostCurrent._lblsideyard.getObject()));
 //BA.debugLineNum = 2084;BA.debugLine="Dim radius = 2dip, dx = 0dip, dy = 0dip As F";
_radius = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (2)));
_dx = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)));
_dy = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)));
 //BA.debugLineNum = 2085;BA.debugLine="jo.RunMethod(\"setShadowLayer\", Array(radius,";
_jo.RunMethod("setShadowLayer",new Object[]{(Object)(_radius),(Object)(_dx),(Object)(_dy),(Object)(anywheresoftware.b4a.keywords.Common.Colors.Black)});
 if (true) break;

case 17:
//C
this.state = 18;
 //BA.debugLineNum = 2087;BA.debugLine="SideYardArmedStatus = \"MOTION DETECTION IS D";
parent._sideyardarmedstatus = "MOTION DETECTION IS DISABLED!";
 //BA.debugLineNum = 2088;BA.debugLine="Dim jo As JavaObject = lblSideYard";
_jo = new anywheresoftware.b4j.object.JavaObject();
_jo = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(parent.mostCurrent._lblsideyard.getObject()));
 //BA.debugLineNum = 2089;BA.debugLine="Dim radius = 4dip, dx = 0dip, dy = 0dip As F";
_radius = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (4)));
_dx = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)));
_dy = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)));
 //BA.debugLineNum = 2090;BA.debugLine="jo.RunMethod(\"setShadowLayer\", Array(radius,";
_jo.RunMethod("setShadowLayer",new Object[]{(Object)(_radius),(Object)(_dx),(Object)(_dy),(Object)(anywheresoftware.b4a.keywords.Common.Colors.Red)});
 if (true) break;

case 18:
//C
this.state = 21;
;
 //BA.debugLineNum = 2093;BA.debugLine="Intent1.Initialize(\"blink.noti.sideyard.armed";
_intent1.Initialize("blink.noti.sideyard.armed","");
 //BA.debugLineNum = 2094;BA.debugLine="Phone.SendBroadcastIntent(Intent1)";
_phone.SendBroadcastIntent((android.content.Intent)(_intent1.getObject()));
 if (true) break;

case 20:
//C
this.state = 21;
 if (true) break;
;
 //BA.debugLineNum = 2104;BA.debugLine="If battery > 1 And battery_state = \"ok\" Then";

case 21:
//if
this.state = 30;
if (_battery>1 && (_battery_state).equals("ok")) { 
this.state = 23;
}else {
this.state = 25;
}if (true) break;

case 23:
//C
this.state = 30;
 //BA.debugLineNum = 2105;BA.debugLine="SideYardBattStatus = \"[Level \" & battery & \"";
parent._sideyardbattstatus = "[Level "+BA.NumberToString(_battery)+" - "+_battery_state.toUpperCase()+"]";
 //BA.debugLineNum = 2106;BA.debugLine="Dim jo As JavaObject = lblSideYardBatt";
_jo = new anywheresoftware.b4j.object.JavaObject();
_jo = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(parent.mostCurrent._lblsideyardbatt.getObject()));
 //BA.debugLineNum = 2107;BA.debugLine="Dim radius = 2dip, dx = 0dip, dy = 0dip As Fl";
_radius = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (2)));
_dx = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)));
_dy = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)));
 //BA.debugLineNum = 2108;BA.debugLine="jo.RunMethod(\"setShadowLayer\", Array(radius,";
_jo.RunMethod("setShadowLayer",new Object[]{(Object)(_radius),(Object)(_dx),(Object)(_dy),(Object)(anywheresoftware.b4a.keywords.Common.Colors.Green)});
 if (true) break;

case 25:
//C
this.state = 26;
 //BA.debugLineNum = 2110;BA.debugLine="SideYardBattStatus = \"[Level \" & battery & \"";
parent._sideyardbattstatus = "[Level "+BA.NumberToString(_battery)+" - "+_battery_state.toUpperCase()+"]";
 //BA.debugLineNum = 2111;BA.debugLine="If SideYardBattStatus = \"[Level 1 - LOW]\" The";
if (true) break;

case 26:
//if
this.state = 29;
if ((parent._sideyardbattstatus).equals("[Level 1 - LOW]")) { 
this.state = 28;
}if (true) break;

case 28:
//C
this.state = 29;
 //BA.debugLineNum = 2112;BA.debugLine="SideYardBattStatus = \"[REPLACE]\"";
parent._sideyardbattstatus = "[REPLACE]";
 if (true) break;

case 29:
//C
this.state = 30;
;
 //BA.debugLineNum = 2114;BA.debugLine="Dim jo As JavaObject = lblSideYardBatt";
_jo = new anywheresoftware.b4j.object.JavaObject();
_jo = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(parent.mostCurrent._lblsideyardbatt.getObject()));
 //BA.debugLineNum = 2115;BA.debugLine="Dim radius = 4dip, dx = 0dip, dy = 0dip As Fl";
_radius = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (4)));
_dx = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)));
_dy = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)));
 //BA.debugLineNum = 2116;BA.debugLine="jo.RunMethod(\"setShadowLayer\", Array(radius,";
_jo.RunMethod("setShadowLayer",new Object[]{(Object)(_radius),(Object)(_dx),(Object)(_dy),(Object)(anywheresoftware.b4a.keywords.Common.Colors.Red)});
 if (true) break;
;
 //BA.debugLineNum = 2118;BA.debugLine="If status = \"offline\" Then";

case 30:
//if
this.state = 33;
if ((_status).equals("offline")) { 
this.state = 32;
}if (true) break;

case 32:
//C
this.state = 33;
 //BA.debugLineNum = 2119;BA.debugLine="SideYardBattStatus = \"OFFLINE\"";
parent._sideyardbattstatus = "OFFLINE";
 //BA.debugLineNum = 2120;BA.debugLine="Dim jo As JavaObject = lblSideYardBatt";
_jo = new anywheresoftware.b4j.object.JavaObject();
_jo = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(parent.mostCurrent._lblsideyardbatt.getObject()));
 //BA.debugLineNum = 2121;BA.debugLine="Dim radius = 4dip, dx = 0dip, dy = 0dip As Fl";
_radius = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (4)));
_dx = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)));
_dy = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)));
 //BA.debugLineNum = 2122;BA.debugLine="jo.RunMethod(\"setShadowLayer\", Array(radius,";
_jo.RunMethod("setShadowLayer",new Object[]{(Object)(_radius),(Object)(_dx),(Object)(_dy),(Object)(anywheresoftware.b4a.keywords.Common.Colors.Red)});
 if (true) break;

case 33:
//C
this.state = 86;
;
 if (true) break;

case 35:
//C
this.state = 36;
 //BA.debugLineNum = 2125;BA.debugLine="If enabled = \"true\" Then";
if (true) break;

case 36:
//if
this.state = 47;
if ((_enabled).equals("true")) { 
this.state = 38;
}else {
this.state = 46;
}if (true) break;

case 38:
//C
this.state = 39;
 //BA.debugLineNum = 2126;BA.debugLine="If enabled = \"true\" Then";
if (true) break;

case 39:
//if
this.state = 44;
if ((_enabled).equals("true")) { 
this.state = 41;
}else {
this.state = 43;
}if (true) break;

case 41:
//C
this.state = 44;
 //BA.debugLineNum = 2127;BA.debugLine="FrontYardArmedStatus = \"\"";
parent._frontyardarmedstatus = "";
 //BA.debugLineNum = 2128;BA.debugLine="Dim jo As JavaObject = lblFrontYard";
_jo = new anywheresoftware.b4j.object.JavaObject();
_jo = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(parent.mostCurrent._lblfrontyard.getObject()));
 //BA.debugLineNum = 2129;BA.debugLine="Dim radius = 2dip, dx = 0dip, dy = 0dip As F";
_radius = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (2)));
_dx = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)));
_dy = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)));
 //BA.debugLineNum = 2130;BA.debugLine="jo.RunMethod(\"setShadowLayer\", Array(radius,";
_jo.RunMethod("setShadowLayer",new Object[]{(Object)(_radius),(Object)(_dx),(Object)(_dy),(Object)(anywheresoftware.b4a.keywords.Common.Colors.Black)});
 if (true) break;

case 43:
//C
this.state = 44;
 //BA.debugLineNum = 2132;BA.debugLine="FrontYardArmedStatus = \"MOTION DETECTION IS";
parent._frontyardarmedstatus = "MOTION DETECTION IS DISABLED!";
 //BA.debugLineNum = 2133;BA.debugLine="Dim jo As JavaObject = lblFrontYard";
_jo = new anywheresoftware.b4j.object.JavaObject();
_jo = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(parent.mostCurrent._lblfrontyard.getObject()));
 //BA.debugLineNum = 2134;BA.debugLine="Dim radius = 4dip, dx = 0dip, dy = 0dip As F";
_radius = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (4)));
_dx = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)));
_dy = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)));
 //BA.debugLineNum = 2135;BA.debugLine="jo.RunMethod(\"setShadowLayer\", Array(radius,";
_jo.RunMethod("setShadowLayer",new Object[]{(Object)(_radius),(Object)(_dx),(Object)(_dy),(Object)(anywheresoftware.b4a.keywords.Common.Colors.Red)});
 if (true) break;

case 44:
//C
this.state = 47;
;
 //BA.debugLineNum = 2138;BA.debugLine="Intent1.Initialize(\"blink.noti.frontyard.arme";
_intent1.Initialize("blink.noti.frontyard.armed","");
 //BA.debugLineNum = 2139;BA.debugLine="Phone.SendBroadcastIntent(Intent1)";
_phone.SendBroadcastIntent((android.content.Intent)(_intent1.getObject()));
 if (true) break;

case 46:
//C
this.state = 47;
 if (true) break;
;
 //BA.debugLineNum = 2149;BA.debugLine="If battery > 1 And battery_state = \"ok\" Then";

case 47:
//if
this.state = 56;
if (_battery>1 && (_battery_state).equals("ok")) { 
this.state = 49;
}else {
this.state = 51;
}if (true) break;

case 49:
//C
this.state = 56;
 //BA.debugLineNum = 2150;BA.debugLine="FrontYardBattStatus = \"[Level \" & battery & \"";
parent._frontyardbattstatus = "[Level "+BA.NumberToString(_battery)+" - "+_battery_state.toUpperCase()+"]";
 //BA.debugLineNum = 2151;BA.debugLine="Dim jo As JavaObject = lblFrontYardBatt";
_jo = new anywheresoftware.b4j.object.JavaObject();
_jo = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(parent.mostCurrent._lblfrontyardbatt.getObject()));
 //BA.debugLineNum = 2152;BA.debugLine="Dim radius = 2dip, dx = 0dip, dy = 0dip As Fl";
_radius = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (2)));
_dx = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)));
_dy = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)));
 //BA.debugLineNum = 2153;BA.debugLine="jo.RunMethod(\"setShadowLayer\", Array(radius,";
_jo.RunMethod("setShadowLayer",new Object[]{(Object)(_radius),(Object)(_dx),(Object)(_dy),(Object)(anywheresoftware.b4a.keywords.Common.Colors.Green)});
 if (true) break;

case 51:
//C
this.state = 52;
 //BA.debugLineNum = 2155;BA.debugLine="FrontYardBattStatus = \"[Level \" & battery & \"";
parent._frontyardbattstatus = "[Level "+BA.NumberToString(_battery)+" - "+_battery_state.toUpperCase()+"]";
 //BA.debugLineNum = 2156;BA.debugLine="If FrontYardBattStatus = \"[Level 1 - LOW]\" Th";
if (true) break;

case 52:
//if
this.state = 55;
if ((parent._frontyardbattstatus).equals("[Level 1 - LOW]")) { 
this.state = 54;
}if (true) break;

case 54:
//C
this.state = 55;
 //BA.debugLineNum = 2157;BA.debugLine="FrontYardBattStatus = \"[REPLACE]\"";
parent._frontyardbattstatus = "[REPLACE]";
 if (true) break;

case 55:
//C
this.state = 56;
;
 //BA.debugLineNum = 2159;BA.debugLine="Dim jo As JavaObject = lblFrontYardBatt";
_jo = new anywheresoftware.b4j.object.JavaObject();
_jo = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(parent.mostCurrent._lblfrontyardbatt.getObject()));
 //BA.debugLineNum = 2160;BA.debugLine="Dim radius = 4dip, dx = 0dip, dy = 0dip As Fl";
_radius = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (4)));
_dx = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)));
_dy = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)));
 //BA.debugLineNum = 2161;BA.debugLine="jo.RunMethod(\"setShadowLayer\", Array(radius,";
_jo.RunMethod("setShadowLayer",new Object[]{(Object)(_radius),(Object)(_dx),(Object)(_dy),(Object)(anywheresoftware.b4a.keywords.Common.Colors.Red)});
 if (true) break;
;
 //BA.debugLineNum = 2163;BA.debugLine="If status = \"offline\" Then";

case 56:
//if
this.state = 59;
if ((_status).equals("offline")) { 
this.state = 58;
}if (true) break;

case 58:
//C
this.state = 59;
 //BA.debugLineNum = 2164;BA.debugLine="FrontYardBattStatus = \"OFFLINE\"";
parent._frontyardbattstatus = "OFFLINE";
 //BA.debugLineNum = 2165;BA.debugLine="Dim jo As JavaObject = lblFrontYardBatt";
_jo = new anywheresoftware.b4j.object.JavaObject();
_jo = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(parent.mostCurrent._lblfrontyardbatt.getObject()));
 //BA.debugLineNum = 2166;BA.debugLine="Dim radius = 4dip, dx = 0dip, dy = 0dip As Fl";
_radius = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (4)));
_dx = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)));
_dy = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)));
 //BA.debugLineNum = 2167;BA.debugLine="jo.RunMethod(\"setShadowLayer\", Array(radius,";
_jo.RunMethod("setShadowLayer",new Object[]{(Object)(_radius),(Object)(_dx),(Object)(_dy),(Object)(anywheresoftware.b4a.keywords.Common.Colors.Red)});
 if (true) break;

case 59:
//C
this.state = 86;
;
 if (true) break;

case 61:
//C
this.state = 62;
 //BA.debugLineNum = 2170;BA.debugLine="If enabled = \"true\" Then";
if (true) break;

case 62:
//if
this.state = 73;
if ((_enabled).equals("true")) { 
this.state = 64;
}else {
this.state = 72;
}if (true) break;

case 64:
//C
this.state = 65;
 //BA.debugLineNum = 2171;BA.debugLine="If enabled = \"true\" Then";
if (true) break;

case 65:
//if
this.state = 70;
if ((_enabled).equals("true")) { 
this.state = 67;
}else {
this.state = 69;
}if (true) break;

case 67:
//C
this.state = 70;
 //BA.debugLineNum = 2172;BA.debugLine="BackyardArmedStatus = \"\"";
parent._backyardarmedstatus = "";
 //BA.debugLineNum = 2173;BA.debugLine="Dim jo As JavaObject = lblBackyard";
_jo = new anywheresoftware.b4j.object.JavaObject();
_jo = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(parent.mostCurrent._lblbackyard.getObject()));
 //BA.debugLineNum = 2174;BA.debugLine="Dim radius = 2dip, dx = 0dip, dy = 0dip As F";
_radius = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (2)));
_dx = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)));
_dy = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)));
 //BA.debugLineNum = 2175;BA.debugLine="jo.RunMethod(\"setShadowLayer\", Array(radius,";
_jo.RunMethod("setShadowLayer",new Object[]{(Object)(_radius),(Object)(_dx),(Object)(_dy),(Object)(anywheresoftware.b4a.keywords.Common.Colors.Black)});
 if (true) break;

case 69:
//C
this.state = 70;
 //BA.debugLineNum = 2177;BA.debugLine="BackyardArmedStatus =\"MOTION DETECTION IS DI";
parent._backyardarmedstatus = "MOTION DETECTION IS DISABLED!";
 //BA.debugLineNum = 2178;BA.debugLine="Dim jo As JavaObject = lblBackyard";
_jo = new anywheresoftware.b4j.object.JavaObject();
_jo = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(parent.mostCurrent._lblbackyard.getObject()));
 //BA.debugLineNum = 2179;BA.debugLine="Dim radius = 4dip, dx = 0dip, dy = 0dip As F";
_radius = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (4)));
_dx = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)));
_dy = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)));
 //BA.debugLineNum = 2180;BA.debugLine="jo.RunMethod(\"setShadowLayer\", Array(radius,";
_jo.RunMethod("setShadowLayer",new Object[]{(Object)(_radius),(Object)(_dx),(Object)(_dy),(Object)(anywheresoftware.b4a.keywords.Common.Colors.Red)});
 if (true) break;

case 70:
//C
this.state = 73;
;
 //BA.debugLineNum = 2183;BA.debugLine="Intent1.Initialize(\"blink.noti.backyard.armed";
_intent1.Initialize("blink.noti.backyard.armed","");
 //BA.debugLineNum = 2184;BA.debugLine="Phone.SendBroadcastIntent(Intent1)";
_phone.SendBroadcastIntent((android.content.Intent)(_intent1.getObject()));
 if (true) break;

case 72:
//C
this.state = 73;
 if (true) break;
;
 //BA.debugLineNum = 2194;BA.debugLine="If battery > 1 And battery_state = \"ok\" Then";

case 73:
//if
this.state = 82;
if (_battery>1 && (_battery_state).equals("ok")) { 
this.state = 75;
}else {
this.state = 77;
}if (true) break;

case 75:
//C
this.state = 82;
 //BA.debugLineNum = 2195;BA.debugLine="BackyardBattStatus = \"[Level \" & battery & \"";
parent._backyardbattstatus = "[Level "+BA.NumberToString(_battery)+" - "+_battery_state.toUpperCase()+"]";
 //BA.debugLineNum = 2196;BA.debugLine="Dim jo As JavaObject = lblBackyardBatt";
_jo = new anywheresoftware.b4j.object.JavaObject();
_jo = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(parent.mostCurrent._lblbackyardbatt.getObject()));
 //BA.debugLineNum = 2197;BA.debugLine="Dim radius = 2dip, dx = 0dip, dy = 0dip As Fl";
_radius = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (2)));
_dx = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)));
_dy = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)));
 //BA.debugLineNum = 2198;BA.debugLine="jo.RunMethod(\"setShadowLayer\", Array(radius,";
_jo.RunMethod("setShadowLayer",new Object[]{(Object)(_radius),(Object)(_dx),(Object)(_dy),(Object)(anywheresoftware.b4a.keywords.Common.Colors.Green)});
 if (true) break;

case 77:
//C
this.state = 78;
 //BA.debugLineNum = 2200;BA.debugLine="BackyardBattStatus = \"[Level \" & battery & \"";
parent._backyardbattstatus = "[Level "+BA.NumberToString(_battery)+" - "+_battery_state.toUpperCase()+"]";
 //BA.debugLineNum = 2201;BA.debugLine="If BackyardBattStatus = \"[Level 1 - LOW]\" The";
if (true) break;

case 78:
//if
this.state = 81;
if ((parent._backyardbattstatus).equals("[Level 1 - LOW]")) { 
this.state = 80;
}if (true) break;

case 80:
//C
this.state = 81;
 //BA.debugLineNum = 2202;BA.debugLine="BackyardBattStatus = \"[REPLACE]\"";
parent._backyardbattstatus = "[REPLACE]";
 if (true) break;

case 81:
//C
this.state = 82;
;
 //BA.debugLineNum = 2204;BA.debugLine="Dim jo As JavaObject = lblBackyardBatt";
_jo = new anywheresoftware.b4j.object.JavaObject();
_jo = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(parent.mostCurrent._lblbackyardbatt.getObject()));
 //BA.debugLineNum = 2205;BA.debugLine="Dim radius = 4dip, dx = 0dip, dy = 0dip As Fl";
_radius = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (4)));
_dx = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)));
_dy = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)));
 //BA.debugLineNum = 2206;BA.debugLine="jo.RunMethod(\"setShadowLayer\", Array(radius,";
_jo.RunMethod("setShadowLayer",new Object[]{(Object)(_radius),(Object)(_dx),(Object)(_dy),(Object)(anywheresoftware.b4a.keywords.Common.Colors.Red)});
 if (true) break;
;
 //BA.debugLineNum = 2208;BA.debugLine="If status = \"offline\" Then";

case 82:
//if
this.state = 85;
if ((_status).equals("offline")) { 
this.state = 84;
}if (true) break;

case 84:
//C
this.state = 85;
 //BA.debugLineNum = 2209;BA.debugLine="BackyardBattStatus = \"OFFLINE\"";
parent._backyardbattstatus = "OFFLINE";
 //BA.debugLineNum = 2210;BA.debugLine="Dim jo As JavaObject = lblBackyardBatt";
_jo = new anywheresoftware.b4j.object.JavaObject();
_jo = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(parent.mostCurrent._lblbackyardbatt.getObject()));
 //BA.debugLineNum = 2211;BA.debugLine="Dim radius = 4dip, dx = 0dip, dy = 0dip As Fl";
_radius = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (4)));
_dx = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)));
_dy = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)));
 //BA.debugLineNum = 2212;BA.debugLine="jo.RunMethod(\"setShadowLayer\", Array(radius,";
_jo.RunMethod("setShadowLayer",new Object[]{(Object)(_radius),(Object)(_dx),(Object)(_dy),(Object)(anywheresoftware.b4a.keywords.Common.Colors.Red)});
 if (true) break;

case 85:
//C
this.state = 86;
;
 if (true) break;

case 86:
//C
this.state = 102;
;
 if (true) break;
if (true) break;

case 87:
//C
this.state = 88;
;
 //BA.debugLineNum = 2217;BA.debugLine="Dim networks As List = root.Get(\"networks\")";
_networks = new anywheresoftware.b4a.objects.collections.List();
_networks = (anywheresoftware.b4a.objects.collections.List) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.List(), (java.util.List)(_root.Get((Object)("networks"))));
 //BA.debugLineNum = 2218;BA.debugLine="For Each colnetworks As Map In networks";
if (true) break;

case 88:
//for
this.state = 91;
_colnetworks = new anywheresoftware.b4a.objects.collections.Map();
group129 = _networks;
index129 = 0;
groupLen129 = group129.getSize();
this.state = 103;
if (true) break;

case 103:
//C
this.state = 91;
if (index129 < groupLen129) {
this.state = 90;
_colnetworks = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (anywheresoftware.b4a.objects.collections.Map.MyMap)(group129.Get(index129)));}
if (true) break;

case 104:
//C
this.state = 103;
index129++;
if (true) break;

case 90:
//C
this.state = 104;
 //BA.debugLineNum = 2221;BA.debugLine="Dim armed As String = colnetworks.Get(\"armed\")";
_armed = BA.ObjectToString(_colnetworks.Get((Object)("armed")));
 if (true) break;
if (true) break;

case 91:
//C
this.state = 92;
;
 //BA.debugLineNum = 2229;BA.debugLine="swArmed.Value = armed";
parent.mostCurrent._swarmed._setvalue /*boolean*/ (BA.ObjectToBoolean(_armed));
 //BA.debugLineNum = 2231;BA.debugLine="If armed <> \"true\" Then";
if (true) break;

case 92:
//if
this.state = 97;
if ((_armed).equals("true") == false) { 
this.state = 94;
}else {
this.state = 96;
}if (true) break;

case 94:
//C
this.state = 97;
 //BA.debugLineNum = 2232;BA.debugLine="SideYardArmedStatus = \"SYSTEM NOT ARMED!\"";
parent._sideyardarmedstatus = "SYSTEM NOT ARMED!";
 //BA.debugLineNum = 2233;BA.debugLine="Dim jo As JavaObject = lblSideYard";
_jo = new anywheresoftware.b4j.object.JavaObject();
_jo = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(parent.mostCurrent._lblsideyard.getObject()));
 //BA.debugLineNum = 2234;BA.debugLine="Dim radius = 4dip, dx = 0dip, dy = 0dip As Floa";
_radius = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (4)));
_dx = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)));
_dy = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)));
 //BA.debugLineNum = 2235;BA.debugLine="jo.RunMethod(\"setShadowLayer\", Array(radius, dx";
_jo.RunMethod("setShadowLayer",new Object[]{(Object)(_radius),(Object)(_dx),(Object)(_dy),(Object)(anywheresoftware.b4a.keywords.Common.Colors.Red)});
 //BA.debugLineNum = 2237;BA.debugLine="FrontYardArmedStatus = \"SYSTEM NOT ARMED!\"";
parent._frontyardarmedstatus = "SYSTEM NOT ARMED!";
 //BA.debugLineNum = 2238;BA.debugLine="Dim jo As JavaObject = lblFrontYard";
_jo = new anywheresoftware.b4j.object.JavaObject();
_jo = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(parent.mostCurrent._lblfrontyard.getObject()));
 //BA.debugLineNum = 2239;BA.debugLine="Dim radius = 4dip, dx = 0dip, dy = 0dip As Floa";
_radius = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (4)));
_dx = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)));
_dy = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)));
 //BA.debugLineNum = 2240;BA.debugLine="jo.RunMethod(\"setShadowLayer\", Array(radius, dx";
_jo.RunMethod("setShadowLayer",new Object[]{(Object)(_radius),(Object)(_dx),(Object)(_dy),(Object)(anywheresoftware.b4a.keywords.Common.Colors.Red)});
 //BA.debugLineNum = 2242;BA.debugLine="BackyardArmedStatus = \"SYSTEM NOT ARMED!\"";
parent._backyardarmedstatus = "SYSTEM NOT ARMED!";
 //BA.debugLineNum = 2243;BA.debugLine="Dim jo As JavaObject = lblBackyard";
_jo = new anywheresoftware.b4j.object.JavaObject();
_jo = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(parent.mostCurrent._lblbackyard.getObject()));
 //BA.debugLineNum = 2244;BA.debugLine="Dim radius = 4dip, dx = 0dip, dy = 0dip As Floa";
_radius = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (4)));
_dx = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)));
_dy = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)));
 //BA.debugLineNum = 2245;BA.debugLine="jo.RunMethod(\"setShadowLayer\", Array(radius, dx";
_jo.RunMethod("setShadowLayer",new Object[]{(Object)(_radius),(Object)(_dx),(Object)(_dy),(Object)(anywheresoftware.b4a.keywords.Common.Colors.Red)});
 //BA.debugLineNum = 2247;BA.debugLine="Intent1.Initialize(\"blink.noti.disarmed\", \"\")";
_intent1.Initialize("blink.noti.disarmed","");
 //BA.debugLineNum = 2248;BA.debugLine="Phone.SendBroadcastIntent(Intent1)";
_phone.SendBroadcastIntent((android.content.Intent)(_intent1.getObject()));
 if (true) break;

case 96:
//C
this.state = 97;
 //BA.debugLineNum = 2250;BA.debugLine="Intent1.Initialize(\"blink.noti.armed\", \"\")";
_intent1.Initialize("blink.noti.armed","");
 //BA.debugLineNum = 2251;BA.debugLine="Phone.SendBroadcastIntent(Intent1)";
_phone.SendBroadcastIntent((android.content.Intent)(_intent1.getObject()));
 if (true) break;

case 97:
//C
this.state = 100;
;
 if (true) break;

case 99:
//C
this.state = 100;
this.catchState = 0;
 //BA.debugLineNum = 2254;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("337159144",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 if (true) break;
if (true) break;

case 100:
//C
this.state = -1;
this.catchState = 0;
;
 //BA.debugLineNum = 2256;BA.debugLine="Return Null";
if (true) {
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,anywheresoftware.b4a.keywords.Common.Null);return;};
 //BA.debugLineNum = 2257;BA.debugLine="End Sub";
if (true) break;
}} 
       catch (Exception e0) {
			
if (catchState == 0)
    throw e0;
else {
    state = catchState;
processBA.setLastException(e0);}
            }
        }
    }
}
public static String  _getperception(String _dht11perception) throws Exception{
String _localperception = "";
 //BA.debugLineNum = 519;BA.debugLine="Sub GetPerception(DHT11Perception As String) As St";
 //BA.debugLineNum = 530;BA.debugLine="Dim localperception As String";
_localperception = "";
 //BA.debugLineNum = 531;BA.debugLine="Select Case DHT11Perception";
switch (BA.switchObjectToInt(_dht11perception,BA.NumberToString(0),BA.NumberToString(1),BA.NumberToString(2),BA.NumberToString(3),BA.NumberToString(4),BA.NumberToString(5),BA.NumberToString(6),BA.NumberToString(7))) {
case 0: {
 //BA.debugLineNum = 534;BA.debugLine="localperception = \"A bit dry\"";
_localperception = "A bit dry";
 break; }
case 1: {
 //BA.debugLineNum = 536;BA.debugLine="localperception = \"Very comfortable\"";
_localperception = "Very comfortable";
 break; }
case 2: {
 //BA.debugLineNum = 538;BA.debugLine="localperception = \"Comfortable\"";
_localperception = "Comfortable";
 break; }
case 3: {
 //BA.debugLineNum = 540;BA.debugLine="localperception = \"Okay but sticky\"";
_localperception = "Okay but sticky";
 break; }
case 4: {
 //BA.debugLineNum = 542;BA.debugLine="localperception = \"Slightly uncomfortable and t";
_localperception = "Slightly uncomfortable and the humidity is at upper limit";
 break; }
case 5: {
 //BA.debugLineNum = 544;BA.debugLine="localperception = \"Very humid and uncomfortable";
_localperception = "Very humid and uncomfortable";
 break; }
case 6: {
 //BA.debugLineNum = 546;BA.debugLine="localperception = \"Extremely uncomfortable and";
_localperception = "Extremely uncomfortable and oppressive";
 break; }
case 7: {
 //BA.debugLineNum = 548;BA.debugLine="localperception = \"Humidity is severely high an";
_localperception = "Humidity is severely high and intolerable";
 break; }
}
;
 //BA.debugLineNum = 550;BA.debugLine="Return localperception";
if (true) return _localperception;
 //BA.debugLineNum = 551;BA.debugLine="End Sub";
return "";
}
public static String  _getresterror(String _json) throws Exception{
anywheresoftware.b4a.objects.collections.JSONParser _parser = null;
anywheresoftware.b4a.objects.collections.Map _root = null;
int _code = 0;
String _message = "";
 //BA.debugLineNum = 1848;BA.debugLine="Sub GetRESTError(json As String) As String";
 //BA.debugLineNum = 1849;BA.debugLine="Try";
try { //BA.debugLineNum = 1855;BA.debugLine="If json.Contains(\"<h1>Not Found</h1>\") Then";
if (_json.contains("<h1>Not Found</h1>")) { 
 //BA.debugLineNum = 1856;BA.debugLine="Return \"REST endpoint URL not found. Try again.";
if (true) return "REST endpoint URL not found. Try again.";
 }else {
 //BA.debugLineNum = 1858;BA.debugLine="If json.IndexOf(\"{\") <> -1 Then";
if (_json.indexOf("{")!=-1) { 
 //BA.debugLineNum = 1859;BA.debugLine="json = json.SubString(json.IndexOf(\"{\"))";
_json = _json.substring(_json.indexOf("{"));
 //BA.debugLineNum = 1860;BA.debugLine="Dim parser As JSONParser";
_parser = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 1861;BA.debugLine="parser.Initialize(json)";
_parser.Initialize(_json);
 //BA.debugLineNum = 1862;BA.debugLine="Dim root As Map = parser.NextObject";
_root = new anywheresoftware.b4a.objects.collections.Map();
_root = _parser.NextObject();
 //BA.debugLineNum = 1863;BA.debugLine="Dim code As Int = root.Get(\"code\")";
_code = (int)(BA.ObjectToNumber(_root.Get((Object)("code"))));
 //BA.debugLineNum = 1864;BA.debugLine="Dim message As String = root.Get(\"message\")";
_message = BA.ObjectToString(_root.Get((Object)("message")));
 //BA.debugLineNum = 1865;BA.debugLine="Log(\"Code: \" & code & \" Message: \" & message)";
anywheresoftware.b4a.keywords.Common.LogImpl("336962321","Code: "+BA.NumberToString(_code)+" Message: "+_message,0);
 //BA.debugLineNum = 1866;BA.debugLine="Return \"Code: \" & code & \" Message: \" & messag";
if (true) return "Code: "+BA.NumberToString(_code)+" Message: "+_message;
 }else {
 //BA.debugLineNum = 1868;BA.debugLine="Return json";
if (true) return _json;
 };
 };
 } 
       catch (Exception e19) {
			processBA.setLastException(e19); //BA.debugLineNum = 1872;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("336962328",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 //BA.debugLineNum = 1874;BA.debugLine="Return json";
if (true) return _json;
 };
 //BA.debugLineNum = 1876;BA.debugLine="End Sub";
return "";
}
public static anywheresoftware.b4a.keywords.Common.ResumableSubWrapper  _getsyncmoduleinfo(String _json) throws Exception{
ResumableSub_GetSyncModuleInfo rsub = new ResumableSub_GetSyncModuleInfo(null,_json);
rsub.resume(processBA, null);
return (anywheresoftware.b4a.keywords.Common.ResumableSubWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper(), rsub);
}
public static class ResumableSub_GetSyncModuleInfo extends BA.ResumableSub {
public ResumableSub_GetSyncModuleInfo(cloyd.smart.home.monitor.main parent,String _json) {
this.parent = parent;
this._json = _json;
}
cloyd.smart.home.monitor.main parent;
String _json;
anywheresoftware.b4a.objects.collections.JSONParser _parser = null;
anywheresoftware.b4a.objects.collections.Map _root = null;
anywheresoftware.b4a.objects.collections.Map _syncmodule = null;
String _fw_version = "";
String _status = "";
anywheresoftware.b4j.object.JavaObject _jo = null;
float _radius = 0f;
float _dx = 0f;
float _dy = 0f;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
try {

        switch (state) {
            case -1:
{
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,null);return;}
case 0:
//C
this.state = 1;
 //BA.debugLineNum = 1978;BA.debugLine="Try";
if (true) break;

case 1:
//try
this.state = 12;
this.catchState = 11;
this.state = 3;
if (true) break;

case 3:
//C
this.state = 4;
this.catchState = 11;
 //BA.debugLineNum = 1979;BA.debugLine="Dim parser As JSONParser";
_parser = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 1980;BA.debugLine="parser.Initialize(json)";
_parser.Initialize(_json);
 //BA.debugLineNum = 1981;BA.debugLine="Dim root As Map = parser.NextObject";
_root = new anywheresoftware.b4a.objects.collections.Map();
_root = _parser.NextObject();
 //BA.debugLineNum = 1982;BA.debugLine="Dim syncmodule As Map = root.Get(\"syncmodule\")";
_syncmodule = new anywheresoftware.b4a.objects.collections.Map();
_syncmodule = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (anywheresoftware.b4a.objects.collections.Map.MyMap)(_root.Get((Object)("syncmodule"))));
 //BA.debugLineNum = 2004;BA.debugLine="Dim fw_version As String = syncmodule.Get(\"fw_ve";
_fw_version = BA.ObjectToString(_syncmodule.Get((Object)("fw_version")));
 //BA.debugLineNum = 2006;BA.debugLine="Dim status As String = syncmodule.Get(\"status\")";
_status = BA.ObjectToString(_syncmodule.Get((Object)("status")));
 //BA.debugLineNum = 2007;BA.debugLine="Dim jo As JavaObject = lblSyncModule";
_jo = new anywheresoftware.b4j.object.JavaObject();
_jo = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(parent.mostCurrent._lblsyncmodule.getObject()));
 //BA.debugLineNum = 2008;BA.debugLine="Dim radius = 2dip, dx = 0dip, dy = 0dip As Float";
_radius = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (2)));
_dx = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)));
_dy = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)));
 //BA.debugLineNum = 2010;BA.debugLine="If status = \"online\" Then";
if (true) break;

case 4:
//if
this.state = 9;
if ((_status).equals("online")) { 
this.state = 6;
}else {
this.state = 8;
}if (true) break;

case 6:
//C
this.state = 9;
 //BA.debugLineNum = 2011;BA.debugLine="jo.RunMethod(\"setShadowLayer\", Array(radius, dx";
_jo.RunMethod("setShadowLayer",new Object[]{(Object)(_radius),(Object)(_dx),(Object)(_dy),(Object)(anywheresoftware.b4a.keywords.Common.Colors.Green)});
 if (true) break;

case 8:
//C
this.state = 9;
 //BA.debugLineNum = 2013;BA.debugLine="jo.RunMethod(\"setShadowLayer\", Array(radius, dx";
_jo.RunMethod("setShadowLayer",new Object[]{(Object)(_radius),(Object)(_dx),(Object)(_dy),(Object)(anywheresoftware.b4a.keywords.Common.Colors.Red)});
 if (true) break;

case 9:
//C
this.state = 12;
;
 //BA.debugLineNum = 2015;BA.debugLine="lblSyncModule.Text = \"Sync Module is \" & status";
parent.mostCurrent._lblsyncmodule.setText(BA.ObjectToCharSequence("Sync Module is "+_status+anywheresoftware.b4a.keywords.Common.CRLF+"Firmware version: "+_fw_version));
 if (true) break;

case 11:
//C
this.state = 12;
this.catchState = 0;
 //BA.debugLineNum = 2017;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("337093416",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 if (true) break;
if (true) break;

case 12:
//C
this.state = -1;
this.catchState = 0;
;
 //BA.debugLineNum = 2019;BA.debugLine="Return Null";
if (true) {
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,anywheresoftware.b4a.keywords.Common.Null);return;};
 //BA.debugLineNum = 2020;BA.debugLine="End Sub";
if (true) break;
}} 
       catch (Exception e0) {
			
if (catchState == 0)
    throw e0;
else {
    state = catchState;
processBA.setLastException(e0);}
            }
        }
    }
}
public static anywheresoftware.b4a.keywords.Common.ResumableSubWrapper  _getunwatchedvideos() throws Exception{
ResumableSub_GetUnwatchedVideos rsub = new ResumableSub_GetUnwatchedVideos(null);
rsub.resume(processBA, null);
return (anywheresoftware.b4a.keywords.Common.ResumableSubWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper(), rsub);
}
public static class ResumableSub_GetUnwatchedVideos extends BA.ResumableSub {
public ResumableSub_GetUnwatchedVideos(cloyd.smart.home.monitor.main parent) {
this.parent = parent;
}
cloyd.smart.home.monitor.main parent;
anywheresoftware.b4a.keywords.Common.ResumableSubWrapper _rs = null;
Object _result = null;
int _unwatchedvideocount = 0;
anywheresoftware.b4a.objects.collections.JSONParser _parser = null;
anywheresoftware.b4a.objects.collections.Map _root = null;
anywheresoftware.b4a.objects.collections.List _media = null;
anywheresoftware.b4a.objects.collections.Map _colmedia = null;
String _watched = "";
anywheresoftware.b4a.objects.B4XViewWrapper _lbl = null;
anywheresoftware.b4a.BA.IterableList group9;
int index9;
int groupLen9;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
try {

        switch (state) {
            case -1:
{
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,null);return;}
case 0:
//C
this.state = 1;
 //BA.debugLineNum = 2988;BA.debugLine="Try";
if (true) break;

case 1:
//try
this.state = 20;
this.catchState = 19;
this.state = 3;
if (true) break;

case 3:
//C
this.state = 4;
this.catchState = 19;
 //BA.debugLineNum = 2989;BA.debugLine="Dim rs As ResumableSub = RESTGet(\"https://rest-\"";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _restget("https://rest-"+parent._userregion+".immedia-semi.com/api/v1/accounts/88438/media/changed?since=-999999999-01-01T00:00:00+18:00&page=1");
 //BA.debugLineNum = 2990;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 21;
return;
case 21:
//C
this.state = 4;
_result = (Object) result[0];
;
 //BA.debugLineNum = 2992;BA.debugLine="Dim unwatchedVideoCount As Int = 0";
_unwatchedvideocount = (int) (0);
 //BA.debugLineNum = 2993;BA.debugLine="Dim parser As JSONParser";
_parser = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 2994;BA.debugLine="parser.Initialize(response)";
_parser.Initialize(parent._response);
 //BA.debugLineNum = 2995;BA.debugLine="Dim root As Map = parser.NextObject";
_root = new anywheresoftware.b4a.objects.collections.Map();
_root = _parser.NextObject();
 //BA.debugLineNum = 2996;BA.debugLine="Dim media As List = root.Get(\"media\")";
_media = new anywheresoftware.b4a.objects.collections.List();
_media = (anywheresoftware.b4a.objects.collections.List) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.List(), (java.util.List)(_root.Get((Object)("media"))));
 //BA.debugLineNum = 2999;BA.debugLine="For Each colmedia As Map In media";
if (true) break;

case 4:
//for
this.state = 11;
_colmedia = new anywheresoftware.b4a.objects.collections.Map();
group9 = _media;
index9 = 0;
groupLen9 = group9.getSize();
this.state = 22;
if (true) break;

case 22:
//C
this.state = 11;
if (index9 < groupLen9) {
this.state = 6;
_colmedia = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (anywheresoftware.b4a.objects.collections.Map.MyMap)(group9.Get(index9)));}
if (true) break;

case 23:
//C
this.state = 22;
index9++;
if (true) break;

case 6:
//C
this.state = 7;
 //BA.debugLineNum = 3000;BA.debugLine="Dim watched As String = colmedia.Get(\"watched\")";
_watched = BA.ObjectToString(_colmedia.Get((Object)("watched")));
 //BA.debugLineNum = 3004;BA.debugLine="If watched = False Then";
if (true) break;

case 7:
//if
this.state = 10;
if ((_watched).equals(BA.ObjectToString(anywheresoftware.b4a.keywords.Common.False))) { 
this.state = 9;
}if (true) break;

case 9:
//C
this.state = 10;
 //BA.debugLineNum = 3005;BA.debugLine="unwatchedVideoCount = unwatchedVideoCount + 1";
_unwatchedvideocount = (int) (_unwatchedvideocount+1);
 if (true) break;

case 10:
//C
this.state = 23;
;
 if (true) break;
if (true) break;

case 11:
//C
this.state = 12;
;
 //BA.debugLineNum = 3009;BA.debugLine="Dim lbl As B4XView = GetAllTabLabelsForBadge(Tab";
_lbl = new anywheresoftware.b4a.objects.B4XViewWrapper();
_lbl = (anywheresoftware.b4a.objects.B4XViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.B4XViewWrapper(), (java.lang.Object)(_getalltablabelsforbadge(parent.mostCurrent._tabstrip1).Get((int) (5))));
 //BA.debugLineNum = 3010;BA.debugLine="badger1.SetBadge(lbl, unwatchedVideoCount)";
parent.mostCurrent._badger1._setbadge /*String*/ (_lbl,_unwatchedvideocount);
 //BA.debugLineNum = 3011;BA.debugLine="StateManager.SetSetting(\"UnwatchedVideoClips\",un";
parent.mostCurrent._statemanager._setsetting /*String*/ (mostCurrent.activityBA,"UnwatchedVideoClips",BA.NumberToString(_unwatchedvideocount));
 //BA.debugLineNum = 3012;BA.debugLine="StateManager.SaveSettings";
parent.mostCurrent._statemanager._savesettings /*String*/ (mostCurrent.activityBA);
 //BA.debugLineNum = 3013;BA.debugLine="If unwatchedVideoCount > 0 Then";
if (true) break;

case 12:
//if
this.state = 17;
if (_unwatchedvideocount>0) { 
this.state = 14;
}else {
this.state = 16;
}if (true) break;

case 14:
//C
this.state = 17;
 //BA.debugLineNum = 3014;BA.debugLine="isThereUnwatchedVideo = True";
parent._isthereunwatchedvideo = anywheresoftware.b4a.keywords.Common.True;
 if (true) break;

case 16:
//C
this.state = 17;
 //BA.debugLineNum = 3016;BA.debugLine="isThereUnwatchedVideo = False";
parent._isthereunwatchedvideo = anywheresoftware.b4a.keywords.Common.False;
 if (true) break;

case 17:
//C
this.state = 20;
;
 if (true) break;

case 19:
//C
this.state = 20;
this.catchState = 0;
 //BA.debugLineNum = 3019;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("338535200",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 if (true) break;
if (true) break;

case 20:
//C
this.state = -1;
this.catchState = 0;
;
 //BA.debugLineNum = 3021;BA.debugLine="Return Null";
if (true) {
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,anywheresoftware.b4a.keywords.Common.Null);return;};
 //BA.debugLineNum = 3022;BA.debugLine="End Sub";
if (true) break;
}} 
       catch (Exception e0) {
			
if (catchState == 0)
    throw e0;
else {
    state = catchState;
processBA.setLastException(e0);}
            }
        }
    }
}
public static anywheresoftware.b4a.keywords.Common.ResumableSubWrapper  _getunwatchedvideospage2() throws Exception{
ResumableSub_GetUnwatchedVideosPage2 rsub = new ResumableSub_GetUnwatchedVideosPage2(null);
rsub.resume(processBA, null);
return (anywheresoftware.b4a.keywords.Common.ResumableSubWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper(), rsub);
}
public static class ResumableSub_GetUnwatchedVideosPage2 extends BA.ResumableSub {
public ResumableSub_GetUnwatchedVideosPage2(cloyd.smart.home.monitor.main parent) {
this.parent = parent;
}
cloyd.smart.home.monitor.main parent;
anywheresoftware.b4a.keywords.Common.ResumableSubWrapper _rs = null;
Object _result = null;
int _unwatchedvideocount = 0;
anywheresoftware.b4a.objects.collections.JSONParser _parser = null;
anywheresoftware.b4a.objects.collections.Map _root = null;
anywheresoftware.b4a.objects.collections.List _media = null;
anywheresoftware.b4a.objects.collections.Map _colmedia = null;
String _watched = "";
anywheresoftware.b4a.objects.B4XViewWrapper _lbl = null;
anywheresoftware.b4a.BA.IterableList group9;
int index9;
int groupLen9;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
try {

        switch (state) {
            case -1:
{
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,null);return;}
case 0:
//C
this.state = 1;
 //BA.debugLineNum = 3025;BA.debugLine="Try";
if (true) break;

case 1:
//try
this.state = 20;
this.catchState = 19;
this.state = 3;
if (true) break;

case 3:
//C
this.state = 4;
this.catchState = 19;
 //BA.debugLineNum = 3026;BA.debugLine="Dim rs As ResumableSub = RESTGet(\"https://rest-\"";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _restget("https://rest-"+parent._userregion+".immedia-semi.com/api/v1/accounts/88438/media/changed?since=-999999999-01-01T00:00:00+18:00&page=2");
 //BA.debugLineNum = 3027;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 21;
return;
case 21:
//C
this.state = 4;
_result = (Object) result[0];
;
 //BA.debugLineNum = 3029;BA.debugLine="Dim unwatchedVideoCount As Int = 0";
_unwatchedvideocount = (int) (0);
 //BA.debugLineNum = 3030;BA.debugLine="Dim parser As JSONParser";
_parser = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 3031;BA.debugLine="parser.Initialize(response)";
_parser.Initialize(parent._response);
 //BA.debugLineNum = 3032;BA.debugLine="Dim root As Map = parser.NextObject";
_root = new anywheresoftware.b4a.objects.collections.Map();
_root = _parser.NextObject();
 //BA.debugLineNum = 3033;BA.debugLine="Dim media As List = root.Get(\"media\")";
_media = new anywheresoftware.b4a.objects.collections.List();
_media = (anywheresoftware.b4a.objects.collections.List) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.List(), (java.util.List)(_root.Get((Object)("media"))));
 //BA.debugLineNum = 3036;BA.debugLine="For Each colmedia As Map In media";
if (true) break;

case 4:
//for
this.state = 11;
_colmedia = new anywheresoftware.b4a.objects.collections.Map();
group9 = _media;
index9 = 0;
groupLen9 = group9.getSize();
this.state = 22;
if (true) break;

case 22:
//C
this.state = 11;
if (index9 < groupLen9) {
this.state = 6;
_colmedia = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (anywheresoftware.b4a.objects.collections.Map.MyMap)(group9.Get(index9)));}
if (true) break;

case 23:
//C
this.state = 22;
index9++;
if (true) break;

case 6:
//C
this.state = 7;
 //BA.debugLineNum = 3037;BA.debugLine="Dim watched As String = colmedia.Get(\"watched\")";
_watched = BA.ObjectToString(_colmedia.Get((Object)("watched")));
 //BA.debugLineNum = 3041;BA.debugLine="If watched = False Then";
if (true) break;

case 7:
//if
this.state = 10;
if ((_watched).equals(BA.ObjectToString(anywheresoftware.b4a.keywords.Common.False))) { 
this.state = 9;
}if (true) break;

case 9:
//C
this.state = 10;
 //BA.debugLineNum = 3042;BA.debugLine="unwatchedVideoCount = unwatchedVideoCount + 1";
_unwatchedvideocount = (int) (_unwatchedvideocount+1);
 if (true) break;

case 10:
//C
this.state = 23;
;
 if (true) break;
if (true) break;

case 11:
//C
this.state = 12;
;
 //BA.debugLineNum = 3046;BA.debugLine="Dim lbl As B4XView = GetAllTabLabelsForBadge(Tab";
_lbl = new anywheresoftware.b4a.objects.B4XViewWrapper();
_lbl = (anywheresoftware.b4a.objects.B4XViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.B4XViewWrapper(), (java.lang.Object)(_getalltablabelsforbadge(parent.mostCurrent._tabstrip1).Get((int) (5))));
 //BA.debugLineNum = 3047;BA.debugLine="badger1.SetBadge(lbl, unwatchedVideoCount)";
parent.mostCurrent._badger1._setbadge /*String*/ (_lbl,_unwatchedvideocount);
 //BA.debugLineNum = 3048;BA.debugLine="StateManager.SetSetting(\"UnwatchedVideoClips\",un";
parent.mostCurrent._statemanager._setsetting /*String*/ (mostCurrent.activityBA,"UnwatchedVideoClips",BA.NumberToString(_unwatchedvideocount));
 //BA.debugLineNum = 3049;BA.debugLine="StateManager.SaveSettings";
parent.mostCurrent._statemanager._savesettings /*String*/ (mostCurrent.activityBA);
 //BA.debugLineNum = 3050;BA.debugLine="If unwatchedVideoCount > 0 Then";
if (true) break;

case 12:
//if
this.state = 17;
if (_unwatchedvideocount>0) { 
this.state = 14;
}else {
this.state = 16;
}if (true) break;

case 14:
//C
this.state = 17;
 //BA.debugLineNum = 3051;BA.debugLine="isThereUnwatchedVideo = True";
parent._isthereunwatchedvideo = anywheresoftware.b4a.keywords.Common.True;
 if (true) break;

case 16:
//C
this.state = 17;
 //BA.debugLineNum = 3053;BA.debugLine="isThereUnwatchedVideo = False";
parent._isthereunwatchedvideo = anywheresoftware.b4a.keywords.Common.False;
 if (true) break;

case 17:
//C
this.state = 20;
;
 if (true) break;

case 19:
//C
this.state = 20;
this.catchState = 0;
 //BA.debugLineNum = 3056;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("338600736",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 if (true) break;
if (true) break;

case 20:
//C
this.state = -1;
this.catchState = 0;
;
 //BA.debugLineNum = 3058;BA.debugLine="Return Null";
if (true) {
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,anywheresoftware.b4a.keywords.Common.Null);return;};
 //BA.debugLineNum = 3059;BA.debugLine="End Sub";
if (true) break;
}} 
       catch (Exception e0) {
			
if (catchState == 0)
    throw e0;
else {
    state = catchState;
processBA.setLastException(e0);}
            }
        }
    }
}
public static String  _getversioncode() throws Exception{
String _appversion = "";
anywheresoftware.b4a.phone.PackageManagerWrapper _pm = null;
String _packagename = "";
 //BA.debugLineNum = 712;BA.debugLine="Sub GetVersionCode() As String";
 //BA.debugLineNum = 713;BA.debugLine="Dim AppVersion As String";
_appversion = "";
 //BA.debugLineNum = 714;BA.debugLine="Try";
try { //BA.debugLineNum = 715;BA.debugLine="Dim pm As PackageManager";
_pm = new anywheresoftware.b4a.phone.PackageManagerWrapper();
 //BA.debugLineNum = 716;BA.debugLine="Dim packageName As String";
_packagename = "";
 //BA.debugLineNum = 717;BA.debugLine="packageName =  Application.PackageName";
_packagename = anywheresoftware.b4a.keywords.Common.Application.getPackageName();
 //BA.debugLineNum = 718;BA.debugLine="AppVersion = pm.GetVersionName(packageName)";
_appversion = _pm.GetVersionName(_packagename);
 } 
       catch (Exception e8) {
			processBA.setLastException(e8); //BA.debugLineNum = 720;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("335323912",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 };
 //BA.debugLineNum = 722;BA.debugLine="Return AppVersion";
if (true) return _appversion;
 //BA.debugLineNum = 723;BA.debugLine="End Sub";
return "";
}
public static anywheresoftware.b4a.keywords.Common.ResumableSubWrapper  _getvideos(String _json) throws Exception{
ResumableSub_GetVideos rsub = new ResumableSub_GetVideos(null,_json);
rsub.resume(processBA, null);
return (anywheresoftware.b4a.keywords.Common.ResumableSubWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper(), rsub);
}
public static class ResumableSub_GetVideos extends BA.ResumableSub {
public ResumableSub_GetVideos(cloyd.smart.home.monitor.main parent,String _json) {
this.parent = parent;
this._json = _json;
}
cloyd.smart.home.monitor.main parent;
String _json;
anywheresoftware.b4a.objects.collections.JSONParser _parser = null;
anywheresoftware.b4a.objects.collections.Map _root = null;
anywheresoftware.b4a.objects.collections.List _media = null;
anywheresoftware.b4a.objects.collections.Map _colmedia = null;
String _thumbnail = "";
String _created_at = "";
String _device_name = "";
String _watched = "";
String _medianame = "";
String _videoid = "";
Object _mytypes = null;
cloyd.smart.home.monitor.main._videoinfo _videos = null;
cloyd.smart.home.monitor.httpjob _j = null;
anywheresoftware.b4a.objects.streams.File.OutputStreamWrapper _out = null;
anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper _image = null;
anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _bmp2 = null;
int _jumptothisitem = 0;
anywheresoftware.b4a.objects.collections.List _list1 = null;
int _i = 0;
String _videoname = "";
anywheresoftware.b4a.objects.streams.File.InputStreamWrapper _in = null;
cloyd.smart.home.monitor.main._carddata _cd = null;
anywheresoftware.b4a.objects.B4XViewWrapper _p = null;
anywheresoftware.b4a.keywords.Common.ResumableSubWrapper _rs = null;
Object _results = null;
anywheresoftware.b4a.BA.IterableList group8;
int index8;
int groupLen8;
int step41;
int limit41;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
try {

        switch (state) {
            case -1:
{
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,null);return;}
case 0:
//C
this.state = 1;
 //BA.debugLineNum = 2444;BA.debugLine="Try";
if (true) break;

case 1:
//try
this.state = 46;
this.catchState = 45;
this.state = 3;
if (true) break;

case 3:
//C
this.state = 4;
this.catchState = 45;
 //BA.debugLineNum = 2445;BA.debugLine="Dim parser As JSONParser";
_parser = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 2446;BA.debugLine="parser.Initialize(json)";
_parser.Initialize(_json);
 //BA.debugLineNum = 2447;BA.debugLine="Dim root As Map = parser.NextObject";
_root = new anywheresoftware.b4a.objects.collections.Map();
_root = _parser.NextObject();
 //BA.debugLineNum = 2448;BA.debugLine="Dim media As List = root.Get(\"media\")";
_media = new anywheresoftware.b4a.objects.collections.List();
_media = (anywheresoftware.b4a.objects.collections.List) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.List(), (java.util.List)(_root.Get((Object)("media"))));
 //BA.debugLineNum = 2450;BA.debugLine="clvActivity.Clear";
parent.mostCurrent._clvactivity._clear();
 //BA.debugLineNum = 2453;BA.debugLine="Pages = Array(False, False, False, False, False,";
parent._pages = anywheresoftware.b4a.keywords.Common.ArrayToList(new Object[]{(Object)(anywheresoftware.b4a.keywords.Common.False),(Object)(anywheresoftware.b4a.keywords.Common.False),(Object)(anywheresoftware.b4a.keywords.Common.False),(Object)(anywheresoftware.b4a.keywords.Common.False),(Object)(anywheresoftware.b4a.keywords.Common.False),(Object)(anywheresoftware.b4a.keywords.Common.True)});
 //BA.debugLineNum = 2454;BA.debugLine="For Each colmedia As Map In media";
if (true) break;

case 4:
//for
this.state = 15;
_colmedia = new anywheresoftware.b4a.objects.collections.Map();
group8 = _media;
index8 = 0;
groupLen8 = group8.getSize();
this.state = 47;
if (true) break;

case 47:
//C
this.state = 15;
if (index8 < groupLen8) {
this.state = 6;
_colmedia = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (anywheresoftware.b4a.objects.collections.Map.MyMap)(group8.Get(index8)));}
if (true) break;

case 48:
//C
this.state = 47;
index8++;
if (true) break;

case 6:
//C
this.state = 7;
 //BA.debugLineNum = 2455;BA.debugLine="Dim thumbnail As String = colmedia.Get(\"thumbna";
_thumbnail = BA.ObjectToString(_colmedia.Get((Object)("thumbnail")));
 //BA.debugLineNum = 2456;BA.debugLine="Dim created_at As String = colmedia.Get(\"create";
_created_at = BA.ObjectToString(_colmedia.Get((Object)("created_at")));
 //BA.debugLineNum = 2457;BA.debugLine="Dim device_name As String = colmedia.Get(\"devic";
_device_name = BA.ObjectToString(_colmedia.Get((Object)("device_name")));
 //BA.debugLineNum = 2458;BA.debugLine="Dim watched As String = colmedia.Get(\"watched\")";
_watched = BA.ObjectToString(_colmedia.Get((Object)("watched")));
 //BA.debugLineNum = 2459;BA.debugLine="Dim medianame As String = colmedia.Get(\"media\")";
_medianame = BA.ObjectToString(_colmedia.Get((Object)("media")));
 //BA.debugLineNum = 2460;BA.debugLine="Dim VideoID As String = colmedia.Get(\"id\")";
_videoid = BA.ObjectToString(_colmedia.Get((Object)("id")));
 //BA.debugLineNum = 2461;BA.debugLine="Dim mytypes As Object = Starter.kvs.Get(VideoID";
_mytypes = parent.mostCurrent._starter._kvs /*cloyd.smart.home.monitor.keyvaluestore*/ ._get /*Object*/ (_videoid);
 //BA.debugLineNum = 2462;BA.debugLine="Dim videos = mytypes As VideoInfo";
_videos = (cloyd.smart.home.monitor.main._videoinfo)(_mytypes);
 //BA.debugLineNum = 2463;BA.debugLine="If (videos = Null) Or (watched <> videos.Watche";
if (true) break;

case 7:
//if
this.state = 14;
if ((_videos== null) || ((_watched).equals(_videos.Watched /*String*/ ) == false)) { 
this.state = 9;
}if (true) break;

case 9:
//C
this.state = 10;
 //BA.debugLineNum = 2464;BA.debugLine="Dim j As HttpJob";
_j = new cloyd.smart.home.monitor.httpjob();
 //BA.debugLineNum = 2465;BA.debugLine="j.Initialize(\"\", Me)";
_j._initialize /*String*/ (processBA,"",main.getObject());
 //BA.debugLineNum = 2466;BA.debugLine="j.Download(\"https://rest-\" & userRegion &\".imm";
_j._download /*String*/ ("https://rest-"+parent._userregion+".immedia-semi.com"+_thumbnail+".jpg");
 //BA.debugLineNum = 2467;BA.debugLine="j.GetRequest.SetHeader(\"TOKEN_AUTH\", authToken";
_j._getrequest /*anywheresoftware.b4h.okhttp.OkHttpClientWrapper.OkHttpRequest*/ ().SetHeader("TOKEN_AUTH",parent._authtoken);
 //BA.debugLineNum = 2468;BA.debugLine="Wait For (j) JobDone(j As HttpJob)";
anywheresoftware.b4a.keywords.Common.WaitFor("jobdone", processBA, this, (Object)(_j));
this.state = 49;
return;
case 49:
//C
this.state = 10;
_j = (cloyd.smart.home.monitor.httpjob) result[0];
;
 //BA.debugLineNum = 2469;BA.debugLine="If j.Success Then";
if (true) break;

case 10:
//if
this.state = 13;
if (_j._success /*boolean*/ ) { 
this.state = 12;
}if (true) break;

case 12:
//C
this.state = 13;
 //BA.debugLineNum = 2470;BA.debugLine="Dim out As OutputStream = File.OpenOutput(Fil";
_out = new anywheresoftware.b4a.objects.streams.File.OutputStreamWrapper();
_out = anywheresoftware.b4a.keywords.Common.File.OpenOutput(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"screenshot.jpg",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2471;BA.debugLine="File.Copy2(j.GetInputStream, out)";
anywheresoftware.b4a.keywords.Common.File.Copy2((java.io.InputStream)(_j._getinputstream /*anywheresoftware.b4a.objects.streams.File.InputStreamWrapper*/ ().getObject()),(java.io.OutputStream)(_out.getObject()));
 //BA.debugLineNum = 2472;BA.debugLine="out.Close '<------ very important";
_out.Close();
 //BA.debugLineNum = 2473;BA.debugLine="Dim image As B4XBitmap = xui.LoadBitmapResize";
_image = new anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper();
_image = parent.mostCurrent._xui.LoadBitmapResize(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"screenshot.jpg",anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (178)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (100)),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2474;BA.debugLine="Dim out As OutputStream";
_out = new anywheresoftware.b4a.objects.streams.File.OutputStreamWrapper();
 //BA.debugLineNum = 2475;BA.debugLine="out.InitializeToBytesArray(0)";
_out.InitializeToBytesArray((int) (0));
 //BA.debugLineNum = 2476;BA.debugLine="image.WriteToStream(out, 100, \"JPEG\")";
_image.WriteToStream((java.io.OutputStream)(_out.getObject()),(int) (100),BA.getEnumFromString(android.graphics.Bitmap.CompressFormat.class,"JPEG"));
 //BA.debugLineNum = 2477;BA.debugLine="Starter.kvs.Put(VideoID, CreateCustomType(med";
parent.mostCurrent._starter._kvs /*cloyd.smart.home.monitor.keyvaluestore*/ ._put /*String*/ (_videoid,(Object)(_createcustomtype(_medianame,_created_at,_watched,_device_name,_videoid,_out.ToBytesArray())));
 //BA.debugLineNum = 2478;BA.debugLine="out.Close";
_out.Close();
 if (true) break;

case 13:
//C
this.state = 14;
;
 //BA.debugLineNum = 2480;BA.debugLine="j.Release";
_j._release /*String*/ ();
 if (true) break;

case 14:
//C
this.state = 48;
;
 if (true) break;
if (true) break;

case 15:
//C
this.state = 16;
;
 //BA.debugLineNum = 2484;BA.debugLine="Dim bmp2 As Bitmap";
_bmp2 = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
 //BA.debugLineNum = 2485;BA.debugLine="Dim jumpToThisItem As Int = 0";
_jumptothisitem = (int) (0);
 //BA.debugLineNum = 2486;BA.debugLine="clvActivity.Clear";
parent.mostCurrent._clvactivity._clear();
 //BA.debugLineNum = 2487;BA.debugLine="Dim list1 As List = Starter.kvs.ListKeys";
_list1 = new anywheresoftware.b4a.objects.collections.List();
_list1 = parent.mostCurrent._starter._kvs /*cloyd.smart.home.monitor.keyvaluestore*/ ._listkeys /*anywheresoftware.b4a.objects.collections.List*/ ();
 //BA.debugLineNum = 2488;BA.debugLine="For i =  0 To list1.Size-1";
if (true) break;

case 16:
//for
this.state = 37;
step41 = 1;
limit41 = (int) (_list1.getSize()-1);
_i = (int) (0) ;
this.state = 50;
if (true) break;

case 50:
//C
this.state = 37;
if ((step41 > 0 && _i <= limit41) || (step41 < 0 && _i >= limit41)) this.state = 18;
if (true) break;

case 51:
//C
this.state = 50;
_i = ((int)(0 + _i + step41)) ;
if (true) break;

case 18:
//C
this.state = 19;
 //BA.debugLineNum = 2492;BA.debugLine="If i > 49 Then";
if (true) break;

case 19:
//if
this.state = 36;
if (_i>49) { 
this.state = 21;
}else {
this.state = 31;
}if (true) break;

case 21:
//C
this.state = 22;
 //BA.debugLineNum = 2493;BA.debugLine="Dim mytypes As Object = Starter.kvs.Get(list1.";
_mytypes = parent.mostCurrent._starter._kvs /*cloyd.smart.home.monitor.keyvaluestore*/ ._get /*Object*/ (BA.ObjectToString(_list1.Get(_i)));
 //BA.debugLineNum = 2494;BA.debugLine="Dim videos = mytypes As VideoInfo";
_videos = (cloyd.smart.home.monitor.main._videoinfo)(_mytypes);
 //BA.debugLineNum = 2496;BA.debugLine="Dim videoName As String";
_videoname = "";
 //BA.debugLineNum = 2497;BA.debugLine="videoName = \"https://rest-\" & userRegion &\".im";
_videoname = "https://rest-"+parent._userregion+".immedia-semi.com"+_videos.ThumbnailPath /*String*/ ;
 //BA.debugLineNum = 2498;BA.debugLine="If videoName.ToLowerCase.EndsWith(\".mp4\") = Fa";
if (true) break;

case 22:
//if
this.state = 25;
if (_videoname.toLowerCase().endsWith(".mp4")==anywheresoftware.b4a.keywords.Common.False) { 
this.state = 24;
}if (true) break;

case 24:
//C
this.state = 25;
 //BA.debugLineNum = 2499;BA.debugLine="videoName = videoName.Trim & \".mp4\"";
_videoname = _videoname.trim()+".mp4";
 if (true) break;

case 25:
//C
this.state = 26;
;
 //BA.debugLineNum = 2501;BA.debugLine="videoName = videoName.SubString(videoName.Last";
_videoname = _videoname.substring((int) (_videoname.lastIndexOf("/")+1));
 //BA.debugLineNum = 2503;BA.debugLine="If File.Exists(File.DirInternal, videoName) Th";
if (true) break;

case 26:
//if
this.state = 29;
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),_videoname)) { 
this.state = 28;
}if (true) break;

case 28:
//C
this.state = 29;
 //BA.debugLineNum = 2504;BA.debugLine="File.Delete(File.DirInternal, videoName)";
anywheresoftware.b4a.keywords.Common.File.Delete(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),_videoname);
 if (true) break;

case 29:
//C
this.state = 36;
;
 //BA.debugLineNum = 2506;BA.debugLine="Starter.kvs.Remove(list1.Get(i))";
parent.mostCurrent._starter._kvs /*cloyd.smart.home.monitor.keyvaluestore*/ ._remove /*String*/ (BA.ObjectToString(_list1.Get(_i)));
 if (true) break;

case 31:
//C
this.state = 32;
 //BA.debugLineNum = 2508;BA.debugLine="Dim mytypes As Object = Starter.kvs.Get(list1.";
_mytypes = parent.mostCurrent._starter._kvs /*cloyd.smart.home.monitor.keyvaluestore*/ ._get /*Object*/ (BA.ObjectToString(_list1.Get(_i)));
 //BA.debugLineNum = 2509;BA.debugLine="Dim videos = mytypes As VideoInfo";
_videos = (cloyd.smart.home.monitor.main._videoinfo)(_mytypes);
 //BA.debugLineNum = 2510;BA.debugLine="Dim In As InputStream";
_in = new anywheresoftware.b4a.objects.streams.File.InputStreamWrapper();
 //BA.debugLineNum = 2511;BA.debugLine="In.InitializeFromBytesArray(videos.ThumbnailBL";
_in.InitializeFromBytesArray(_videos.ThumbnailBLOB /*byte[]*/ ,(int) (0),_videos.ThumbnailBLOB /*byte[]*/ .length);
 //BA.debugLineNum = 2513;BA.debugLine="bmp2.Initialize2(In)";
_bmp2.Initialize2((java.io.InputStream)(_in.getObject()));
 //BA.debugLineNum = 2514;BA.debugLine="Dim cd As CardData";
_cd = new cloyd.smart.home.monitor.main._carddata();
 //BA.debugLineNum = 2515;BA.debugLine="cd.Initialize";
_cd.Initialize();
 //BA.debugLineNum = 2516;BA.debugLine="cd.screenshot = bmp2";
_cd.screenshot /*anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper*/  = (anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper(), (android.graphics.Bitmap)(_bmp2.getObject()));
 //BA.debugLineNum = 2517;BA.debugLine="cd.filedate = videos.DateCreated";
_cd.filedate /*String*/  = _videos.DateCreated /*String*/ ;
 //BA.debugLineNum = 2518;BA.debugLine="cd.deviceinfo = videos.DeviceName";
_cd.deviceinfo /*String*/  = _videos.DeviceName /*String*/ ;
 //BA.debugLineNum = 2519;BA.debugLine="cd.iswatchedvisible = Not(videos.Watched)";
_cd.iswatchedvisible /*boolean*/  = anywheresoftware.b4a.keywords.Common.Not(BA.ObjectToBoolean(_videos.Watched /*String*/ ));
 //BA.debugLineNum = 2520;BA.debugLine="If videos.Watched = False Then";
if (true) break;

case 32:
//if
this.state = 35;
if ((_videos.Watched /*String*/ ).equals(BA.ObjectToString(anywheresoftware.b4a.keywords.Common.False))) { 
this.state = 34;
}if (true) break;

case 34:
//C
this.state = 35;
 //BA.debugLineNum = 2521;BA.debugLine="jumpToThisItem = i";
_jumptothisitem = _i;
 if (true) break;

case 35:
//C
this.state = 36;
;
 //BA.debugLineNum = 2523;BA.debugLine="cd.mediaURL = \"https://rest-\" & userRegion &\".";
_cd.mediaURL /*String*/  = "https://rest-"+parent._userregion+".immedia-semi.com"+_videos.ThumbnailPath /*String*/ ;
 //BA.debugLineNum = 2524;BA.debugLine="Dim p As B4XView = xui.CreatePanel(\"\")";
_p = new anywheresoftware.b4a.objects.B4XViewWrapper();
_p = parent.mostCurrent._xui.CreatePanel(processBA,"");
 //BA.debugLineNum = 2525;BA.debugLine="p.SetLayoutAnimated(0, 0, 0, clvActivity.AsVie";
_p.SetLayoutAnimated((int) (0),(int) (0),(int) (0),parent.mostCurrent._clvactivity._asview().getWidth(),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (100)));
 //BA.debugLineNum = 2526;BA.debugLine="clvActivity.Add(p, cd)";
parent.mostCurrent._clvactivity._add(_p,(Object)(_cd));
 if (true) break;

case 36:
//C
this.state = 51;
;
 if (true) break;
if (true) break;
;
 //BA.debugLineNum = 2530;BA.debugLine="If clvActivity.Size < 50 Then";

case 37:
//if
this.state = 40;
if (parent.mostCurrent._clvactivity._getsize()<50) { 
this.state = 39;
}if (true) break;

case 39:
//C
this.state = 40;
 //BA.debugLineNum = 2531;BA.debugLine="B4XLoadingIndicator4.Show";
parent.mostCurrent._b4xloadingindicator4._show /*String*/ ();
 //BA.debugLineNum = 2533;BA.debugLine="lblDuration.Text = \"0:00\"";
parent.mostCurrent._lblduration.setText(BA.ObjectToCharSequence("0:00"));
 //BA.debugLineNum = 2534;BA.debugLine="wvMedia.LoadUrl(\"\")";
parent.mostCurrent._wvmedia.LoadUrl("");
 //BA.debugLineNum = 2539;BA.debugLine="Dim rs As ResumableSub = GetUnwatchedVideosPage";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _getunwatchedvideospage2();
 //BA.debugLineNum = 2540;BA.debugLine="wait for (rs) complete (Results As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 52;
return;
case 52:
//C
this.state = 40;
_results = (Object) result[0];
;
 //BA.debugLineNum = 2543;BA.debugLine="Dim rs As ResumableSub = GetVideos(response)";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _getvideos(parent._response);
 //BA.debugLineNum = 2544;BA.debugLine="wait for (rs) complete (Results As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 53;
return;
case 53:
//C
this.state = 40;
_results = (Object) result[0];
;
 //BA.debugLineNum = 2546;BA.debugLine="TabStrip1.ScrollTo(5,False)";
parent.mostCurrent._tabstrip1.ScrollTo((int) (5),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2547;BA.debugLine="Sleep(100)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (100));
this.state = 54;
return;
case 54:
//C
this.state = 40;
;
 //BA.debugLineNum = 2548;BA.debugLine="B4XLoadingIndicator4.Hide";
parent.mostCurrent._b4xloadingindicator4._hide /*String*/ ();
 if (true) break;
;
 //BA.debugLineNum = 2551;BA.debugLine="If list1.Size > 0 Then";

case 40:
//if
this.state = 43;
if (_list1.getSize()>0) { 
this.state = 42;
}if (true) break;

case 42:
//C
this.state = 43;
 //BA.debugLineNum = 2552;BA.debugLine="Sleep(100)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (100));
this.state = 55;
return;
case 55:
//C
this.state = 43;
;
 //BA.debugLineNum = 2553;BA.debugLine="clvActivity.JumpToItem(jumpToThisItem)";
parent.mostCurrent._clvactivity._jumptoitem(_jumptothisitem);
 //BA.debugLineNum = 2554;BA.debugLine="Sleep(100)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (100));
this.state = 56;
return;
case 56:
//C
this.state = 43;
;
 //BA.debugLineNum = 2555;BA.debugLine="clvActivity.JumpToItem(jumpToThisItem)";
parent.mostCurrent._clvactivity._jumptoitem(_jumptothisitem);
 //BA.debugLineNum = 2556;BA.debugLine="Sleep(100)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (100));
this.state = 57;
return;
case 57:
//C
this.state = 43;
;
 //BA.debugLineNum = 2557;BA.debugLine="clvActivity_ItemClick(jumpToThisItem,\"\") '\"http";
_clvactivity_itemclick(_jumptothisitem,(Object)(""));
 //BA.debugLineNum = 2558;BA.debugLine="Sleep(100)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (100));
this.state = 58;
return;
case 58:
//C
this.state = 43;
;
 if (true) break;

case 43:
//C
this.state = 46;
;
 if (true) break;

case 45:
//C
this.state = 46;
this.catchState = 0;
 //BA.debugLineNum = 2562;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("337814391",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 if (true) break;
if (true) break;

case 46:
//C
this.state = -1;
this.catchState = 0;
;
 //BA.debugLineNum = 2564;BA.debugLine="B4XLoadingIndicator4.hide";
parent.mostCurrent._b4xloadingindicator4._hide /*String*/ ();
 //BA.debugLineNum = 2565;BA.debugLine="Pages = Array(True, True, True, True, True, True)";
parent._pages = anywheresoftware.b4a.keywords.Common.ArrayToList(new Object[]{(Object)(anywheresoftware.b4a.keywords.Common.True),(Object)(anywheresoftware.b4a.keywords.Common.True),(Object)(anywheresoftware.b4a.keywords.Common.True),(Object)(anywheresoftware.b4a.keywords.Common.True),(Object)(anywheresoftware.b4a.keywords.Common.True),(Object)(anywheresoftware.b4a.keywords.Common.True)});
 //BA.debugLineNum = 2566;BA.debugLine="Return Null";
if (true) {
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,anywheresoftware.b4a.keywords.Common.Null);return;};
 //BA.debugLineNum = 2567;BA.debugLine="End Sub";
if (true) break;
}} 
       catch (Exception e0) {
			
if (catchState == 0)
    throw e0;
else {
    state = catchState;
processBA.setLastException(e0);}
            }
        }
    }
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 63;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 64;BA.debugLine="Private ACToolBarLight1 As ACToolBarLight";
mostCurrent._actoolbarlight1 = new de.amberhome.objects.appcompat.ACToolbarLightWrapper();
 //BA.debugLineNum = 65;BA.debugLine="Private ToolbarHelper As ACActionBar";
mostCurrent._toolbarhelper = new de.amberhome.objects.appcompat.ACActionBar();
 //BA.debugLineNum = 66;BA.debugLine="Private gblACMenu As ACMenu";
mostCurrent._gblacmenu = new de.amberhome.objects.appcompat.ACMenuWrapper();
 //BA.debugLineNum = 67;BA.debugLine="Private xui As XUI";
mostCurrent._xui = new anywheresoftware.b4a.objects.B4XViewWrapper.XUI();
 //BA.debugLineNum = 68;BA.debugLine="Private GaugeHumidity As Gauge";
mostCurrent._gaugehumidity = new cloyd.smart.home.monitor.gauge();
 //BA.debugLineNum = 69;BA.debugLine="Private GaugeTemp As Gauge";
mostCurrent._gaugetemp = new cloyd.smart.home.monitor.gauge();
 //BA.debugLineNum = 70;BA.debugLine="Private GaugeDewPoint As Gauge";
mostCurrent._gaugedewpoint = new cloyd.smart.home.monitor.gauge();
 //BA.debugLineNum = 71;BA.debugLine="Private GaugeHeatIndex As Gauge";
mostCurrent._gaugeheatindex = new cloyd.smart.home.monitor.gauge();
 //BA.debugLineNum = 72;BA.debugLine="Private lblComfort As Label";
mostCurrent._lblcomfort = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 73;BA.debugLine="Private lblPerception As Label";
mostCurrent._lblperception = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 74;BA.debugLine="Private lblLastUpdate As Label";
mostCurrent._lbllastupdate = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 75;BA.debugLine="Private lblPing As Label";
mostCurrent._lblping = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 76;BA.debugLine="Private TabStrip1 As TabStrip";
mostCurrent._tabstrip1 = new anywheresoftware.b4a.objects.TabStripViewPager();
 //BA.debugLineNum = 77;BA.debugLine="Private lblFontAwesome As Label";
mostCurrent._lblfontawesome = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 78;BA.debugLine="Private GaugeAirQuality As Gauge";
mostCurrent._gaugeairquality = new cloyd.smart.home.monitor.gauge();
 //BA.debugLineNum = 79;BA.debugLine="Private lblAirQuality As Label";
mostCurrent._lblairquality = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 80;BA.debugLine="Private lblAirQualityLastUpdate As Label";
mostCurrent._lblairqualitylastupdate = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 81;BA.debugLine="Private ScrollView1 As ScrollView";
mostCurrent._scrollview1 = new anywheresoftware.b4a.objects.ScrollViewWrapper();
 //BA.debugLineNum = 82;BA.debugLine="Private Panel1 As Panel";
mostCurrent._panel1 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 83;BA.debugLine="Private GaugeAirQualityBasement As Gauge";
mostCurrent._gaugeairqualitybasement = new cloyd.smart.home.monitor.gauge();
 //BA.debugLineNum = 84;BA.debugLine="Private lblAirQualityBasement As Label";
mostCurrent._lblairqualitybasement = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 85;BA.debugLine="Private lblAirQualityLastUpdateBasement As Label";
mostCurrent._lblairqualitylastupdatebasement = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 86;BA.debugLine="Private PanelAirQualityBasement As Panel";
mostCurrent._panelairqualitybasement = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 87;BA.debugLine="Private GaugeDewPointBasement As Gauge";
mostCurrent._gaugedewpointbasement = new cloyd.smart.home.monitor.gauge();
 //BA.debugLineNum = 88;BA.debugLine="Private GaugeHeatIndexBasement As Gauge";
mostCurrent._gaugeheatindexbasement = new cloyd.smart.home.monitor.gauge();
 //BA.debugLineNum = 89;BA.debugLine="Private GaugeHumidityBasement As Gauge";
mostCurrent._gaugehumiditybasement = new cloyd.smart.home.monitor.gauge();
 //BA.debugLineNum = 90;BA.debugLine="Private GaugeTempBasement As Gauge";
mostCurrent._gaugetempbasement = new cloyd.smart.home.monitor.gauge();
 //BA.debugLineNum = 91;BA.debugLine="Private lblComfortBasement As Label";
mostCurrent._lblcomfortbasement = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 92;BA.debugLine="Private lblLastUpdateBasement As Label";
mostCurrent._lbllastupdatebasement = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 93;BA.debugLine="Private lblPerceptionBasement As Label";
mostCurrent._lblperceptionbasement = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 94;BA.debugLine="Private lblPingBasement As Label";
mostCurrent._lblpingbasement = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 95;BA.debugLine="Private ScrollViewBasement As ScrollView";
mostCurrent._scrollviewbasement = new anywheresoftware.b4a.objects.ScrollViewWrapper();
 //BA.debugLineNum = 96;BA.debugLine="Private PanelTempHumidityBasement As Panel";
mostCurrent._paneltemphumiditybasement = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 97;BA.debugLine="Private lblStatus As Label";
mostCurrent._lblstatus = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 98;BA.debugLine="Private ivSideYard As ImageView";
mostCurrent._ivsideyard = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 99;BA.debugLine="Private ivFrontYard As ImageView";
mostCurrent._ivfrontyard = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 100;BA.debugLine="Private ivBackyard As ImageView";
mostCurrent._ivbackyard = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 101;BA.debugLine="Private ScrollViewBlink As ScrollView";
mostCurrent._scrollviewblink = new anywheresoftware.b4a.objects.ScrollViewWrapper();
 //BA.debugLineNum = 102;BA.debugLine="Private panelBlink As Panel";
mostCurrent._panelblink = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 103;BA.debugLine="Private lblSideYard As Label";
mostCurrent._lblsideyard = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 104;BA.debugLine="Private lblFrontYard As Label";
mostCurrent._lblfrontyard = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 105;BA.debugLine="Private lblBackyard As Label";
mostCurrent._lblbackyard = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 106;BA.debugLine="Private lblSideYardBatt As Label";
mostCurrent._lblsideyardbatt = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 107;BA.debugLine="Private lblSideYardTimestamp As Label";
mostCurrent._lblsideyardtimestamp = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 108;BA.debugLine="Private lblSideYardWifi As Label";
mostCurrent._lblsideyardwifi = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 109;BA.debugLine="Private lblFrontYardBatt As Label";
mostCurrent._lblfrontyardbatt = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 110;BA.debugLine="Private lblFrontYardTimestamp As Label";
mostCurrent._lblfrontyardtimestamp = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 111;BA.debugLine="Private lblFrontYardWiFi As Label";
mostCurrent._lblfrontyardwifi = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 112;BA.debugLine="Private lblBackyardBatt As Label";
mostCurrent._lblbackyardbatt = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 113;BA.debugLine="Private lblBackyardTimestamp As Label";
mostCurrent._lblbackyardtimestamp = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 114;BA.debugLine="Private lblBackyardWiFi As Label";
mostCurrent._lblbackyardwifi = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 115;BA.debugLine="Private lblSyncModule As Label";
mostCurrent._lblsyncmodule = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 116;BA.debugLine="Private B4XPageIndicator1 As B4XPageIndicator";
mostCurrent._b4xpageindicator1 = new cloyd.smart.home.monitor.b4xpageindicator();
 //BA.debugLineNum = 117;BA.debugLine="Private ivScreenshot As ImageView";
mostCurrent._ivscreenshot = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 118;BA.debugLine="Private lblDate As B4XView";
mostCurrent._lbldate = new anywheresoftware.b4a.objects.B4XViewWrapper();
 //BA.debugLineNum = 119;BA.debugLine="Private lblDeviceInfo As B4XView";
mostCurrent._lbldeviceinfo = new anywheresoftware.b4a.objects.B4XViewWrapper();
 //BA.debugLineNum = 120;BA.debugLine="Private lblFileInfo As B4XView";
mostCurrent._lblfileinfo = new anywheresoftware.b4a.objects.B4XViewWrapper();
 //BA.debugLineNum = 121;BA.debugLine="Private clvActivity As CustomListView";
mostCurrent._clvactivity = new b4a.example3.customlistview();
 //BA.debugLineNum = 122;BA.debugLine="Private wvMedia As WebView";
mostCurrent._wvmedia = new anywheresoftware.b4a.objects.WebViewWrapper();
 //BA.debugLineNum = 123;BA.debugLine="Private WebViewSettings1 As WebViewSettings";
mostCurrent._webviewsettings1 = new uk.co.martinpearman.b4a.webviewsettings.WebViewSettings();
 //BA.debugLineNum = 124;BA.debugLine="Private B4XLoadingIndicator4 As B4XLoadingIndicat";
mostCurrent._b4xloadingindicator4 = new cloyd.smart.home.monitor.b4xloadingindicator();
 //BA.debugLineNum = 125;BA.debugLine="Private lblDuration As Label";
mostCurrent._lblduration = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 126;BA.debugLine="Private ivWatched As ImageView";
mostCurrent._ivwatched = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 127;BA.debugLine="Private badger1 As Badger";
mostCurrent._badger1 = new cloyd.smart.home.monitor.badger();
 //BA.debugLineNum = 128;BA.debugLine="Private btnSideYardNewClip As ImageView";
mostCurrent._btnsideyardnewclip = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 129;BA.debugLine="Private btnFrontYardNewClip As ImageView";
mostCurrent._btnfrontyardnewclip = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 130;BA.debugLine="Private btnBackyardNewClip As ImageView";
mostCurrent._btnbackyardnewclip = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 131;BA.debugLine="Private btnRefresh As SwiftButton";
mostCurrent._btnrefresh = new cloyd.smart.home.monitor.swiftbutton();
 //BA.debugLineNum = 132;BA.debugLine="Private btnSideYard As SwiftButton";
mostCurrent._btnsideyard = new cloyd.smart.home.monitor.swiftbutton();
 //BA.debugLineNum = 133;BA.debugLine="Private lblMediaURL As B4XView";
mostCurrent._lblmediaurl = new anywheresoftware.b4a.objects.B4XViewWrapper();
 //BA.debugLineNum = 134;BA.debugLine="Private ivPlay As ImageView";
mostCurrent._ivplay = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 135;BA.debugLine="Private nativeMe As JavaObject";
mostCurrent._nativeme = new anywheresoftware.b4j.object.JavaObject();
 //BA.debugLineNum = 136;BA.debugLine="Private btnBackyardRefresh As ImageView";
mostCurrent._btnbackyardrefresh = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 137;BA.debugLine="Private btnFrontYardRefresh As ImageView";
mostCurrent._btnfrontyardrefresh = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 138;BA.debugLine="Private btnSideYardRefresh As ImageView";
mostCurrent._btnsideyardrefresh = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 139;BA.debugLine="Private swArmed As B4XSwitch";
mostCurrent._swarmed = new cloyd.smart.home.monitor.b4xswitch();
 //BA.debugLineNum = 140;BA.debugLine="Private dialog As B4XDialog";
mostCurrent._dialog = new cloyd.smart.home.monitor.b4xdialog();
 //BA.debugLineNum = 141;BA.debugLine="Private btnChart As Button";
mostCurrent._btnchart = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 142;BA.debugLine="End Sub";
return "";
}
public static String  _handlesettings() throws Exception{
 //BA.debugLineNum = 1243;BA.debugLine="Sub HandleSettings";
 //BA.debugLineNum = 1244;BA.debugLine="StateManager.SetSetting(\"HumidityAddValue\",manage";
mostCurrent._statemanager._setsetting /*String*/ (mostCurrent.activityBA,"HumidityAddValue",_manager.GetString("HumidityAddValue"));
 //BA.debugLineNum = 1245;BA.debugLine="StateManager.SetSetting(\"SensorNotRespondingTime\"";
mostCurrent._statemanager._setsetting /*String*/ (mostCurrent.activityBA,"SensorNotRespondingTime",_manager.GetString("SensorNotRespondingTime"));
 //BA.debugLineNum = 1246;BA.debugLine="StateManager.SaveSettings";
mostCurrent._statemanager._savesettings /*String*/ (mostCurrent.activityBA);
 //BA.debugLineNum = 1247;BA.debugLine="End Sub";
return "";
}
public static String  _hideping() throws Exception{
 //BA.debugLineNum = 740;BA.debugLine="Private Sub HidePing";
 //BA.debugLineNum = 741;BA.debugLine="lblPing.SetVisibleAnimated(200, False)";
mostCurrent._lblping.SetVisibleAnimated((int) (200),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 742;BA.debugLine="End Sub";
return "";
}
public static String  _hidepingbasement() throws Exception{
 //BA.debugLineNum = 744;BA.debugLine="Private Sub HidePingBasement";
 //BA.debugLineNum = 745;BA.debugLine="lblPingBasement.SetVisibleAnimated(200, False)";
mostCurrent._lblpingbasement.SetVisibleAnimated((int) (200),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 746;BA.debugLine="End Sub";
return "";
}
public static void  _ivbackyard_click() throws Exception{
ResumableSub_ivBackyard_Click rsub = new ResumableSub_ivBackyard_Click(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_ivBackyard_Click extends BA.ResumableSub {
public ResumableSub_ivBackyard_Click(cloyd.smart.home.monitor.main parent) {
this.parent = parent;
}
cloyd.smart.home.monitor.main parent;
anywheresoftware.b4a.keywords.Common.ResumableSubWrapper _rs = null;
Object _result = null;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 2401;BA.debugLine="Dim rs As ResumableSub = RESTGet(\"https://rest-\"";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _restget("https://rest-"+parent._userregion+".immedia-semi.com/network/"+parent._networkid+"/camera/458236");
 //BA.debugLineNum = 2402;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 5;
return;
case 5:
//C
this.state = 1;
_result = (Object) result[0];
;
 //BA.debugLineNum = 2404;BA.debugLine="Dim rs As ResumableSub = GetCameraInfo(response,F";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _getcamerainfo(parent._response,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2405;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 6;
return;
case 6:
//C
this.state = 1;
_result = (Object) result[0];
;
 //BA.debugLineNum = 2408;BA.debugLine="Dim rs As ResumableSub = DownloadImageFullscreen(";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _downloadimagefullscreen("https://rest-"+parent._userregion+".immedia-semi.com/"+parent._camerathumbnail+".jpg","458236");
 //BA.debugLineNum = 2409;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 7;
return;
case 7:
//C
this.state = 1;
_result = (Object) result[0];
;
 //BA.debugLineNum = 2410;BA.debugLine="If response.StartsWith(\"ERROR: \") = False Then";
if (true) break;

case 1:
//if
this.state = 4;
if (parent._response.startsWith("ERROR: ")==anywheresoftware.b4a.keywords.Common.False) { 
this.state = 3;
}if (true) break;

case 3:
//C
this.state = 4;
 //BA.debugLineNum = 2411;BA.debugLine="lblStatus.Text = \"Ready\"";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("Ready"));
 if (true) break;

case 4:
//C
this.state = -1;
;
 //BA.debugLineNum = 2413;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static void  _ivfrontyard_click() throws Exception{
ResumableSub_ivFrontYard_Click rsub = new ResumableSub_ivFrontYard_Click(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_ivFrontYard_Click extends BA.ResumableSub {
public ResumableSub_ivFrontYard_Click(cloyd.smart.home.monitor.main parent) {
this.parent = parent;
}
cloyd.smart.home.monitor.main parent;
anywheresoftware.b4a.keywords.Common.ResumableSubWrapper _rs = null;
Object _result = null;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 2416;BA.debugLine="Dim rs As ResumableSub = RESTGet(\"https://rest-\"";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _restget("https://rest-"+parent._userregion+".immedia-semi.com/network/"+parent._networkid+"/camera/236967");
 //BA.debugLineNum = 2417;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 5;
return;
case 5:
//C
this.state = 1;
_result = (Object) result[0];
;
 //BA.debugLineNum = 2419;BA.debugLine="Dim rs As ResumableSub = GetCameraInfo(response,F";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _getcamerainfo(parent._response,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2420;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 6;
return;
case 6:
//C
this.state = 1;
_result = (Object) result[0];
;
 //BA.debugLineNum = 2422;BA.debugLine="Dim rs As ResumableSub = DownloadImageFullscreen(";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _downloadimagefullscreen("https://rest-"+parent._userregion+".immedia-semi.com/"+parent._camerathumbnail+".jpg","236967");
 //BA.debugLineNum = 2423;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 7;
return;
case 7:
//C
this.state = 1;
_result = (Object) result[0];
;
 //BA.debugLineNum = 2424;BA.debugLine="If response.StartsWith(\"ERROR: \") = False Then";
if (true) break;

case 1:
//if
this.state = 4;
if (parent._response.startsWith("ERROR: ")==anywheresoftware.b4a.keywords.Common.False) { 
this.state = 3;
}if (true) break;

case 3:
//C
this.state = 4;
 //BA.debugLineNum = 2425;BA.debugLine="lblStatus.Text = \"Ready\"";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("Ready"));
 if (true) break;

case 4:
//C
this.state = -1;
;
 //BA.debugLineNum = 2427;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static void  _ivplay_click() throws Exception{
ResumableSub_ivPlay_Click rsub = new ResumableSub_ivPlay_Click(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_ivPlay_Click extends BA.ResumableSub {
public ResumableSub_ivPlay_Click(cloyd.smart.home.monitor.main parent) {
this.parent = parent;
}
cloyd.smart.home.monitor.main parent;
anywheresoftware.b4a.phone.Phone _p = null;
cloyd.smart.home.monitor.main._carddata _cd = null;
anywheresoftware.b4a.keywords.Common.ResumableSubWrapper _rs = null;
Object _result = null;
String _filename = "";
anywheresoftware.b4a.objects.IntentWrapper _in = null;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
try {

        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 3402;BA.debugLine="Try";
if (true) break;

case 1:
//try
this.state = 14;
this.catchState = 13;
this.state = 3;
if (true) break;

case 3:
//C
this.state = 4;
this.catchState = 13;
 //BA.debugLineNum = 3403;BA.debugLine="Dim p As Phone";
_p = new anywheresoftware.b4a.phone.Phone();
 //BA.debugLineNum = 3404;BA.debugLine="p.SetMute(3,True)";
_p.SetMute((int) (3),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3406;BA.debugLine="B4XLoadingIndicator4.Show";
parent.mostCurrent._b4xloadingindicator4._show /*String*/ ();
 //BA.debugLineNum = 3407;BA.debugLine="wvMedia.LoadUrl(\"\")";
parent.mostCurrent._wvmedia.LoadUrl("");
 //BA.debugLineNum = 3409;BA.debugLine="If previousSelectedIndex > (clvActivity.Size-1)";
if (true) break;

case 4:
//if
this.state = 7;
if (parent._previousselectedindex>(parent.mostCurrent._clvactivity._getsize()-1)) { 
this.state = 6;
}if (true) break;

case 6:
//C
this.state = 7;
 //BA.debugLineNum = 3410;BA.debugLine="previousSelectedIndex = 0";
parent._previousselectedindex = (int) (0);
 if (true) break;

case 7:
//C
this.state = 8;
;
 //BA.debugLineNum = 3413;BA.debugLine="Dim cd As CardData = clvActivity.GetValue(previo";
_cd = (cloyd.smart.home.monitor.main._carddata)(parent.mostCurrent._clvactivity._getvalue(parent._previousselectedindex));
 //BA.debugLineNum = 3414;BA.debugLine="Dim rs As ResumableSub = ShowVideo(cd.mediaURL)";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _showvideo(_cd.mediaURL /*String*/ );
 //BA.debugLineNum = 3415;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 15;
return;
case 15:
//C
this.state = 8;
_result = (Object) result[0];
;
 //BA.debugLineNum = 3417;BA.debugLine="Dim FileName As String";
_filename = "";
 //BA.debugLineNum = 3418;BA.debugLine="FileName = cd.mediaURL.SubString(cd.mediaURL.Las";
_filename = _cd.mediaURL /*String*/ .substring((int) (_cd.mediaURL /*String*/ .lastIndexOf("/")+1));
 //BA.debugLineNum = 3420;BA.debugLine="If File.Exists(File.DirInternal, FileName) Then";
if (true) break;

case 8:
//if
this.state = 11;
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),_filename)) { 
this.state = 10;
}if (true) break;

case 10:
//C
this.state = 11;
 //BA.debugLineNum = 3421;BA.debugLine="File.Copy(File.DirInternal, FileName, Starter.P";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),_filename,parent.mostCurrent._starter._provider /*cloyd.smart.home.monitor.fileprovider*/ ._sharedfolder /*String*/ ,_filename);
 //BA.debugLineNum = 3422;BA.debugLine="Dim in As Intent";
_in = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 3423;BA.debugLine="in.Initialize(in.ACTION_VIEW, \"\")";
_in.Initialize(_in.ACTION_VIEW,"");
 //BA.debugLineNum = 3424;BA.debugLine="Starter.Provider.SetFileUriAsIntentData(in, Fil";
parent.mostCurrent._starter._provider /*cloyd.smart.home.monitor.fileprovider*/ ._setfileuriasintentdata /*String*/ (_in,_filename);
 //BA.debugLineNum = 3426;BA.debugLine="in.SetType(\"video/*\")";
_in.SetType("video/*");
 //BA.debugLineNum = 3427;BA.debugLine="StartActivity(in)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_in.getObject()));
 if (true) break;

case 11:
//C
this.state = 14;
;
 if (true) break;

case 13:
//C
this.state = 14;
this.catchState = 0;
 //BA.debugLineNum = 3430;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("338993949",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 if (true) break;
if (true) break;

case 14:
//C
this.state = -1;
this.catchState = 0;
;
 //BA.debugLineNum = 3432;BA.debugLine="B4XLoadingIndicator4.Hide";
parent.mostCurrent._b4xloadingindicator4._hide /*String*/ ();
 //BA.debugLineNum = 3433;BA.debugLine="End Sub";
if (true) break;
}} 
       catch (Exception e0) {
			
if (catchState == 0)
    throw e0;
else {
    state = catchState;
processBA.setLastException(e0);}
            }
        }
    }
}
public static void  _ivsideyard_click() throws Exception{
ResumableSub_ivSideYard_Click rsub = new ResumableSub_ivSideYard_Click(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_ivSideYard_Click extends BA.ResumableSub {
public ResumableSub_ivSideYard_Click(cloyd.smart.home.monitor.main parent) {
this.parent = parent;
}
cloyd.smart.home.monitor.main parent;
anywheresoftware.b4a.keywords.Common.ResumableSubWrapper _rs = null;
Object _result = null;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 2430;BA.debugLine="Dim rs As ResumableSub = RESTGet(\"https://rest-\"";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _restget("https://rest-"+parent._userregion+".immedia-semi.com/network/"+parent._networkid+"/camera/347574");
 //BA.debugLineNum = 2431;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 5;
return;
case 5:
//C
this.state = 1;
_result = (Object) result[0];
;
 //BA.debugLineNum = 2433;BA.debugLine="Dim rs As ResumableSub = GetCameraInfo(response,F";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _getcamerainfo(parent._response,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2434;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 6;
return;
case 6:
//C
this.state = 1;
_result = (Object) result[0];
;
 //BA.debugLineNum = 2436;BA.debugLine="Dim rs As ResumableSub = DownloadImageFullscreen(";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _downloadimagefullscreen("https://rest-"+parent._userregion+".immedia-semi.com/"+parent._camerathumbnail+".jpg","347574");
 //BA.debugLineNum = 2437;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 7;
return;
case 7:
//C
this.state = 1;
_result = (Object) result[0];
;
 //BA.debugLineNum = 2438;BA.debugLine="If response.StartsWith(\"ERROR: \") = False Then";
if (true) break;

case 1:
//if
this.state = 4;
if (parent._response.startsWith("ERROR: ")==anywheresoftware.b4a.keywords.Common.False) { 
this.state = 3;
}if (true) break;

case 3:
//C
this.state = 4;
 //BA.debugLineNum = 2439;BA.debugLine="lblStatus.Text = \"Ready\"";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("Ready"));
 if (true) break;

case 4:
//C
this.state = -1;
;
 //BA.debugLineNum = 2441;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _mqtt_connect() throws Exception{
String _clientid = "";
anywheresoftware.b4j.objects.MqttAsyncClientWrapper.MqttConnectOptionsWrapper _connopt = null;
 //BA.debugLineNum = 435;BA.debugLine="Sub MQTT_Connect";
 //BA.debugLineNum = 436;BA.debugLine="Try";
try { //BA.debugLineNum = 437;BA.debugLine="Dim ClientId As String = Rnd(0, 999999999) 'crea";
_clientid = BA.NumberToString(anywheresoftware.b4a.keywords.Common.Rnd((int) (0),(int) (999999999)));
 //BA.debugLineNum = 438;BA.debugLine="MQTT.Initialize(\"MQTT\", MQTTServerURI, ClientId)";
_mqtt.Initialize(processBA,"MQTT",_mqttserveruri,_clientid);
 //BA.debugLineNum = 440;BA.debugLine="Dim ConnOpt As MqttConnectOptions";
_connopt = new anywheresoftware.b4j.objects.MqttAsyncClientWrapper.MqttConnectOptionsWrapper();
 //BA.debugLineNum = 441;BA.debugLine="ConnOpt.Initialize(MQTTUser, MQTTPassword)";
_connopt.Initialize(_mqttuser,_mqttpassword);
 //BA.debugLineNum = 442;BA.debugLine="MQTT.Connect2(ConnOpt)";
_mqtt.Connect2((org.eclipse.paho.client.mqttv3.MqttConnectOptions)(_connopt.getObject()));
 } 
       catch (Exception e8) {
			processBA.setLastException(e8); //BA.debugLineNum = 444;BA.debugLine="Log(\"MQTT_Connect: \" & LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("334668553","MQTT_Connect: "+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 };
 //BA.debugLineNum = 446;BA.debugLine="End Sub";
return "";
}
public static String  _mqtt_connected(boolean _success) throws Exception{
 //BA.debugLineNum = 448;BA.debugLine="Sub MQTT_Connected (Success As Boolean)";
 //BA.debugLineNum = 449;BA.debugLine="Try";
try { //BA.debugLineNum = 450;BA.debugLine="If Success = False Then";
if (_success==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 451;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("334734083",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 //BA.debugLineNum = 452;BA.debugLine="MQTT_Connect";
_mqtt_connect();
 }else {
 //BA.debugLineNum = 454;BA.debugLine="Log(\"Connected to MQTT broker\")";
anywheresoftware.b4a.keywords.Common.LogImpl("334734086","Connected to MQTT broker",0);
 //BA.debugLineNum = 455;BA.debugLine="MQTT.Subscribe(\"TempHumid\", 0)";
_mqtt.Subscribe("TempHumid",(int) (0));
 //BA.debugLineNum = 456;BA.debugLine="MQTT.Subscribe(\"MQ7\", 0)";
_mqtt.Subscribe("MQ7",(int) (0));
 //BA.debugLineNum = 457;BA.debugLine="MQTT.Subscribe(\"MQ7Basement\", 0)";
_mqtt.Subscribe("MQ7Basement",(int) (0));
 //BA.debugLineNum = 458;BA.debugLine="MQTT.Subscribe(\"TempHumidBasement\", 0)";
_mqtt.Subscribe("TempHumidBasement",(int) (0));
 //BA.debugLineNum = 459;BA.debugLine="MQTT.Subscribe(\"HumidityAddValue\", 0)";
_mqtt.Subscribe("HumidityAddValue",(int) (0));
 };
 } 
       catch (Exception e14) {
			processBA.setLastException(e14); //BA.debugLineNum = 462;BA.debugLine="Log(\"MQTT_Connected: \" & LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("334734094","MQTT_Connected: "+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 };
 //BA.debugLineNum = 464;BA.debugLine="End Sub";
return "";
}
public static String  _mqtt_disconnected() throws Exception{
 //BA.debugLineNum = 466;BA.debugLine="Private Sub MQTT_Disconnected";
 //BA.debugLineNum = 467;BA.debugLine="Try";
try { //BA.debugLineNum = 468;BA.debugLine="gblACMenu.Clear";
mostCurrent._gblacmenu.Clear();
 //BA.debugLineNum = 469;BA.debugLine="gblACMenu.Add(0, 0, \"Settings\",Null)";
mostCurrent._gblacmenu.Add((int) (0),(int) (0),BA.ObjectToCharSequence("Settings"),(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 470;BA.debugLine="gblACMenu.Add(0, 0, \"Refresh video list\",Null)";
mostCurrent._gblacmenu.Add((int) (0),(int) (0),BA.ObjectToCharSequence("Refresh video list"),(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 471;BA.debugLine="gblACMenu.Add(0, 0, \"Restart application\",Null)";
mostCurrent._gblacmenu.Add((int) (0),(int) (0),BA.ObjectToCharSequence("Restart application"),(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 472;BA.debugLine="gblACMenu.Add(0, 0, \"Show free memory\",Null)";
mostCurrent._gblacmenu.Add((int) (0),(int) (0),BA.ObjectToCharSequence("Show free memory"),(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 473;BA.debugLine="gblACMenu.Add(0, 0, \"Show chart\",Null)";
mostCurrent._gblacmenu.Add((int) (0),(int) (0),BA.ObjectToCharSequence("Show chart"),(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 474;BA.debugLine="gblACMenu.Add(0, 0, \"About\",Null)";
mostCurrent._gblacmenu.Add((int) (0),(int) (0),BA.ObjectToCharSequence("About"),(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 475;BA.debugLine="Log(\"Disconnected from MQTT broker\")";
anywheresoftware.b4a.keywords.Common.LogImpl("334799625","Disconnected from MQTT broker",0);
 //BA.debugLineNum = 476;BA.debugLine="MQTT_Connect";
_mqtt_connect();
 } 
       catch (Exception e12) {
			processBA.setLastException(e12); //BA.debugLineNum = 478;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("334799628",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 };
 //BA.debugLineNum = 480;BA.debugLine="End Sub";
return "";
}
public static String  _mqtt_messagearrived(String _topic,byte[] _payload) throws Exception{
 //BA.debugLineNum = 482;BA.debugLine="Private Sub MQTT_MessageArrived (Topic As String,";
 //BA.debugLineNum = 483;BA.debugLine="Try";
try { //BA.debugLineNum = 484;BA.debugLine="If Topic = \"TempHumid\" Then";
if ((_topic).equals("TempHumid")) { 
 //BA.debugLineNum = 485;BA.debugLine="lblPing.SetVisibleAnimated(500, True)";
mostCurrent._lblping.SetVisibleAnimated((int) (500),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 486;BA.debugLine="csu.CallSubPlus(Me, \"HidePing\", 700)";
_csu._v7(main.getObject(),"HidePing",(int) (700));
 //BA.debugLineNum = 487;BA.debugLine="CheckTempHumiditySetting";
_checktemphumiditysetting();
 }else if((_topic).equals("MQ7")) { 
 //BA.debugLineNum = 489;BA.debugLine="CheckAirQualitySetting";
_checkairqualitysetting();
 }else if((_topic).equals("MQ7Basement")) { 
 //BA.debugLineNum = 491;BA.debugLine="CheckAirQualitySettingBasement";
_checkairqualitysettingbasement();
 }else if((_topic).equals("TempHumidBasement")) { 
 //BA.debugLineNum = 493;BA.debugLine="lblPingBasement.SetVisibleAnimated(500, True)";
mostCurrent._lblpingbasement.SetVisibleAnimated((int) (500),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 494;BA.debugLine="csu.CallSubPlus(Me, \"HidePingBasement\", 700)";
_csu._v7(main.getObject(),"HidePingBasement",(int) (700));
 //BA.debugLineNum = 496;BA.debugLine="CheckTempHumiditySettingBasement";
_checktemphumiditysettingbasement();
 };
 } 
       catch (Exception e16) {
			processBA.setLastException(e16); //BA.debugLineNum = 499;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("334865169",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 };
 //BA.debugLineNum = 501;BA.debugLine="End Sub";
return "";
}
public static long  _parseutcstring(String _utc) throws Exception{
String _df = "";
long _res = 0L;
 //BA.debugLineNum = 2317;BA.debugLine="Sub ParseUTCstring(utc As String) As Long";
 //BA.debugLineNum = 2318;BA.debugLine="Dim df As String = DateTime.DateFormat";
_df = anywheresoftware.b4a.keywords.Common.DateTime.getDateFormat();
 //BA.debugLineNum = 2319;BA.debugLine="Dim res As Long";
_res = 0L;
 //BA.debugLineNum = 2320;BA.debugLine="If utc.CharAt(10) = \"T\" Then";
if (_utc.charAt((int) (10))==BA.ObjectToChar("T")) { 
 //BA.debugLineNum = 2322;BA.debugLine="If utc.CharAt(19) = \".\" Then utc = utc.SubString";
if (_utc.charAt((int) (19))==BA.ObjectToChar(".")) { 
_utc = _utc.substring((int) (0),(int) (19))+"+0000";};
 //BA.debugLineNum = 2323;BA.debugLine="DateTime.DateFormat = \"yyyy-MM-dd'T'HH:mm:ssZ\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
 }else {
 //BA.debugLineNum = 2326;BA.debugLine="DateTime.DateFormat = \"EEE MMM dd HH:mm:ss Z yyy";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
 };
 //BA.debugLineNum = 2328;BA.debugLine="Try";
try { //BA.debugLineNum = 2329;BA.debugLine="res = DateTime.DateParse(utc)";
_res = anywheresoftware.b4a.keywords.Common.DateTime.DateParse(_utc);
 } 
       catch (Exception e12) {
			processBA.setLastException(e12); //BA.debugLineNum = 2331;BA.debugLine="res = 0";
_res = (long) (0);
 };
 //BA.debugLineNum = 2333;BA.debugLine="DateTime.DateFormat = df";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat(_df);
 //BA.debugLineNum = 2334;BA.debugLine="Return res";
if (true) return _res;
 //BA.debugLineNum = 2335;BA.debugLine="End Sub";
return 0L;
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        b4a.example.dateutils._process_globals();
main._process_globals();
chart._process_globals();
smarthomemonitor._process_globals();
notificationservice._process_globals();
statemanager._process_globals();
starter._process_globals();
b4xcollections._process_globals();
httputils2service._process_globals();
xuiviewsutils._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 20;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 21;BA.debugLine="Private MQTT As MqttClient";
_mqtt = new anywheresoftware.b4j.objects.MqttAsyncClientWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Private MQTTUser As String = \"vynckfaq\"";
_mqttuser = "vynckfaq";
 //BA.debugLineNum = 23;BA.debugLine="Private MQTTPassword As String = \"KHSV1Q1qSUUY\"";
_mqttpassword = "KHSV1Q1qSUUY";
 //BA.debugLineNum = 24;BA.debugLine="Private MQTTServerURI As String = \"tcp://m14.clou";
_mqttserveruri = "tcp://m14.cloudmqtt.com:11816";
 //BA.debugLineNum = 25;BA.debugLine="Private bc As ByteConverter";
_bc = new anywheresoftware.b4a.agraham.byteconverter.ByteConverter();
 //BA.debugLineNum = 26;BA.debugLine="Private csu As CallSubUtils";
_csu = new b4a.example.callsubutils();
 //BA.debugLineNum = 27;BA.debugLine="Private OldIntent As Intent";
_oldintent = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Dim manager As AHPreferenceManager";
_manager = new de.amberhome.objects.preferenceactivity.PreferenceManager();
 //BA.debugLineNum = 29;BA.debugLine="Dim screen As AHPreferenceScreen";
_screen = new de.amberhome.objects.preferenceactivity.PreferenceScreenWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Private emailAddress As String";
_emailaddress = "";
 //BA.debugLineNum = 32;BA.debugLine="Private password As String";
_password = "";
 //BA.debugLineNum = 33;BA.debugLine="Private authToken As String";
_authtoken = "";
 //BA.debugLineNum = 34;BA.debugLine="Private userRegion As String = \"u006\"";
_userregion = "u006";
 //BA.debugLineNum = 35;BA.debugLine="Private accountID As String = \"88438\" 'ignore";
_accountid = "88438";
 //BA.debugLineNum = 36;BA.debugLine="Private networkID As String = \"94896\"";
_networkid = "94896";
 //BA.debugLineNum = 39;BA.debugLine="Private commandID As String";
_commandid = "";
 //BA.debugLineNum = 40;BA.debugLine="Private commandComplete As Boolean";
_commandcomplete = false;
 //BA.debugLineNum = 41;BA.debugLine="Private cameraThumbnail As String";
_camerathumbnail = "";
 //BA.debugLineNum = 42;BA.debugLine="Private response As String";
_response = "";
 //BA.debugLineNum = 43;BA.debugLine="Private previousSelectedIndex As Int";
_previousselectedindex = 0;
 //BA.debugLineNum = 44;BA.debugLine="Private mediaMetaData As cMediaData";
_mediametadata = new cloyd.smart.home.monitor.cmediadata();
 //BA.debugLineNum = 45;BA.debugLine="Private SideYardArmedStatus As String";
_sideyardarmedstatus = "";
 //BA.debugLineNum = 46;BA.debugLine="Private FrontYardArmedStatus As String";
_frontyardarmedstatus = "";
 //BA.debugLineNum = 47;BA.debugLine="Private BackyardArmedStatus As String";
_backyardarmedstatus = "";
 //BA.debugLineNum = 48;BA.debugLine="Type CardData (screenshot As B4XBitmap,filedate A";
;
 //BA.debugLineNum = 49;BA.debugLine="Type VideoInfo (ThumbnailPath As String, DateCrea";
;
 //BA.debugLineNum = 50;BA.debugLine="Public isThereUnwatchedVideo As Boolean";
_isthereunwatchedvideo = false;
 //BA.debugLineNum = 51;BA.debugLine="Private bmp As B4XBitmap";
_bmp = new anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper();
 //BA.debugLineNum = 52;BA.debugLine="Private prevCameraThumbnail As String";
_prevcamerathumbnail = "";
 //BA.debugLineNum = 53;BA.debugLine="Private SideYardBattStatus As String";
_sideyardbattstatus = "";
 //BA.debugLineNum = 54;BA.debugLine="Private FrontYardBattStatus As String";
_frontyardbattstatus = "";
 //BA.debugLineNum = 55;BA.debugLine="Private BackyardBattStatus As String";
_backyardbattstatus = "";
 //BA.debugLineNum = 56;BA.debugLine="Private CurrentPage As Int";
_currentpage = 0;
 //BA.debugLineNum = 57;BA.debugLine="Private Pages As List";
_pages = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 58;BA.debugLine="Private Awake As PhoneWakeState";
_awake = new anywheresoftware.b4a.phone.Phone.PhoneWakeState();
 //BA.debugLineNum = 59;BA.debugLine="Private effects As BitmapCreatorEffects";
_effects = new cloyd.smart.home.monitor.bitmapcreatoreffects();
 //BA.debugLineNum = 60;BA.debugLine="Private compileTimeStamp As String";
_compiletimestamp = "";
 //BA.debugLineNum = 61;BA.debugLine="End Sub";
return "";
}
public static String  _randomstring(int _length) throws Exception{
String _abc = "";
String _randomstr = "";
int _i = 0;
 //BA.debugLineNum = 3658;BA.debugLine="Sub RandomString(length As Int) As String";
 //BA.debugLineNum = 3659;BA.debugLine="Dim abc As String = \"0123456789ABCDEFGHIJKLMNOPQR";
_abc = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
 //BA.debugLineNum = 3660;BA.debugLine="Dim randomstr As String = \"\"";
_randomstr = "";
 //BA.debugLineNum = 3661;BA.debugLine="For i = 0 To length - 1";
{
final int step3 = 1;
final int limit3 = (int) (_length-1);
_i = (int) (0) ;
for (;_i <= limit3 ;_i = _i + step3 ) {
 //BA.debugLineNum = 3662;BA.debugLine="randomstr = randomstr & (abc.CharAt(Rnd(0,abc.Le";
_randomstr = _randomstr+BA.ObjectToString((_abc.charAt(anywheresoftware.b4a.keywords.Common.Rnd((int) (0),_abc.length()))));
 }
};
 //BA.debugLineNum = 3664;BA.debugLine="Return randomstr";
if (true) return _randomstr;
 //BA.debugLineNum = 3665;BA.debugLine="End Sub";
return "";
}
public static anywheresoftware.b4a.keywords.Common.ResumableSubWrapper  _refreshcameras(boolean _firstrun,String _whatcamera) throws Exception{
ResumableSub_RefreshCameras rsub = new ResumableSub_RefreshCameras(null,_firstrun,_whatcamera);
rsub.resume(processBA, null);
return (anywheresoftware.b4a.keywords.Common.ResumableSubWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper(), rsub);
}
public static class ResumableSub_RefreshCameras extends BA.ResumableSub {
public ResumableSub_RefreshCameras(cloyd.smart.home.monitor.main parent,boolean _firstrun,String _whatcamera) {
this.parent = parent;
this._firstrun = _firstrun;
this._whatcamera = _whatcamera;
}
cloyd.smart.home.monitor.main parent;
boolean _firstrun;
String _whatcamera;
String _camera = "";
anywheresoftware.b4a.objects.ImageViewWrapper _iv = null;
anywheresoftware.b4a.objects.collections.List _links = null;
int _attempts = 0;
anywheresoftware.b4a.keywords.Common.ResumableSubWrapper _rs = null;
Object _result = null;
String _link = "";
boolean _rechecked = false;
int _i = 0;
anywheresoftware.b4a.BA.IterableList group53;
int index53;
int groupLen53;
int step128;
int limit128;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
try {

        switch (state) {
            case -1:
{
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,null);return;}
case 0:
//C
this.state = 1;
 //BA.debugLineNum = 1322;BA.debugLine="Try";
if (true) break;

case 1:
//try
this.state = 95;
this.catchState = 94;
this.state = 3;
if (true) break;

case 3:
//C
this.state = 4;
this.catchState = 94;
 //BA.debugLineNum = 1323;BA.debugLine="Dim camera As String";
_camera = "";
 //BA.debugLineNum = 1324;BA.debugLine="Dim iv As ImageView";
_iv = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 1325;BA.debugLine="Dim links As List";
_links = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 1326;BA.debugLine="Dim attempts As Int";
_attempts = 0;
 //BA.debugLineNum = 1327;BA.debugLine="links = Array(\"347574\", \"236967\", \"458236\")";
_links = anywheresoftware.b4a.keywords.Common.ArrayToList(new Object[]{(Object)("347574"),(Object)("236967"),(Object)("458236")});
 //BA.debugLineNum = 1329;BA.debugLine="Dim rs As ResumableSub = RESTGet(\"https://rest-\"";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _restget("https://rest-"+parent._userregion+".immedia-semi.com/network/"+parent._networkid+"/syncmodules");
 //BA.debugLineNum = 1330;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 104;
return;
case 104:
//C
this.state = 4;
_result = (Object) result[0];
;
 //BA.debugLineNum = 1332;BA.debugLine="If response.StartsWith(\"ERROR: \") Or response.Co";
if (true) break;

case 4:
//if
this.state = 7;
if (parent._response.startsWith("ERROR: ") || parent._response.contains("System is busy, please wait")) { 
this.state = 6;
}if (true) break;

case 6:
//C
this.state = 7;
 //BA.debugLineNum = 1333;BA.debugLine="btnSideYard.Enabled = True";
parent.mostCurrent._btnsideyard._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1334;BA.debugLine="btnRefresh.Enabled = True";
parent.mostCurrent._btnrefresh._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1335;BA.debugLine="btnSideYardNewClip.Enabled = True";
parent.mostCurrent._btnsideyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1336;BA.debugLine="btnFrontYardNewClip.Enabled = True";
parent.mostCurrent._btnfrontyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1337;BA.debugLine="btnBackyardNewClip.Enabled = True";
parent.mostCurrent._btnbackyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1338;BA.debugLine="btnSideYardRefresh.Enabled = True";
parent.mostCurrent._btnsideyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1339;BA.debugLine="btnFrontYardRefresh.Enabled = True";
parent.mostCurrent._btnfrontyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1340;BA.debugLine="btnBackyardRefresh.Enabled = True";
parent.mostCurrent._btnbackyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1341;BA.debugLine="swArmed.Enabled = True";
parent.mostCurrent._swarmed._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1342;BA.debugLine="Return Null";
if (true) {
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,anywheresoftware.b4a.keywords.Common.Null);return;};
 if (true) break;

case 7:
//C
this.state = 8;
;
 //BA.debugLineNum = 1345;BA.debugLine="Dim rs As ResumableSub = GetSyncModuleInfo(respo";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _getsyncmoduleinfo(parent._response);
 //BA.debugLineNum = 1346;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 105;
return;
case 105:
//C
this.state = 8;
_result = (Object) result[0];
;
 //BA.debugLineNum = 1349;BA.debugLine="Dim rs As ResumableSub = RESTGet(\"https://rest-\"";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _restget("https://rest-"+parent._userregion+".immedia-semi.com/api/v3/accounts/88438/homescreen");
 //BA.debugLineNum = 1350;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 106;
return;
case 106:
//C
this.state = 8;
_result = (Object) result[0];
;
 //BA.debugLineNum = 1351;BA.debugLine="If response.StartsWith(\"ERROR: \") Or response.Co";
if (true) break;

case 8:
//if
this.state = 11;
if (parent._response.startsWith("ERROR: ") || parent._response.contains("System is busy, please wait")) { 
this.state = 10;
}if (true) break;

case 10:
//C
this.state = 11;
 //BA.debugLineNum = 1352;BA.debugLine="btnSideYard.Enabled = True";
parent.mostCurrent._btnsideyard._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1353;BA.debugLine="btnRefresh.Enabled = True";
parent.mostCurrent._btnrefresh._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1354;BA.debugLine="btnSideYardNewClip.Enabled = True";
parent.mostCurrent._btnsideyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1355;BA.debugLine="btnFrontYardNewClip.Enabled = True";
parent.mostCurrent._btnfrontyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1356;BA.debugLine="btnBackyardNewClip.Enabled = True";
parent.mostCurrent._btnbackyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1357;BA.debugLine="btnSideYardRefresh.Enabled = True";
parent.mostCurrent._btnsideyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1358;BA.debugLine="btnFrontYardRefresh.Enabled = True";
parent.mostCurrent._btnfrontyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1359;BA.debugLine="btnBackyardRefresh.Enabled = True";
parent.mostCurrent._btnbackyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1360;BA.debugLine="swArmed.Enabled = True";
parent.mostCurrent._swarmed._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1362;BA.debugLine="Return Null";
if (true) {
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,anywheresoftware.b4a.keywords.Common.Null);return;};
 if (true) break;

case 11:
//C
this.state = 12;
;
 //BA.debugLineNum = 1366;BA.debugLine="Dim rs As ResumableSub = GetHomescreen(response)";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _gethomescreen(parent._response);
 //BA.debugLineNum = 1367;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 107;
return;
case 107:
//C
this.state = 12;
_result = (Object) result[0];
;
 //BA.debugLineNum = 1370;BA.debugLine="Dim rs As ResumableSub = GetUnwatchedVideos";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _getunwatchedvideos();
 //BA.debugLineNum = 1371;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 108;
return;
case 108:
//C
this.state = 12;
_result = (Object) result[0];
;
 //BA.debugLineNum = 1373;BA.debugLine="lblSideYardBatt.Visible = True";
parent.mostCurrent._lblsideyardbatt.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1374;BA.debugLine="lblSideYardTimestamp.Visible = True";
parent.mostCurrent._lblsideyardtimestamp.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1375;BA.debugLine="lblSideYardWifi.Visible = True";
parent.mostCurrent._lblsideyardwifi.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1376;BA.debugLine="lblSideYard.Visible = True";
parent.mostCurrent._lblsideyard.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1377;BA.debugLine="lblFrontYardBatt.Visible = True";
parent.mostCurrent._lblfrontyardbatt.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1378;BA.debugLine="lblFrontYardTimestamp.Visible = True";
parent.mostCurrent._lblfrontyardtimestamp.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1379;BA.debugLine="lblFrontYardWiFi.Visible = True";
parent.mostCurrent._lblfrontyardwifi.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1380;BA.debugLine="lblFrontYard.Visible = True";
parent.mostCurrent._lblfrontyard.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1381;BA.debugLine="lblBackyardBatt.Visible = True";
parent.mostCurrent._lblbackyardbatt.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1382;BA.debugLine="lblBackyardTimestamp.Visible = True";
parent.mostCurrent._lblbackyardtimestamp.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1383;BA.debugLine="lblBackyardWiFi.Visible = True";
parent.mostCurrent._lblbackyardwifi.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1384;BA.debugLine="lblBackyard.Visible = True";
parent.mostCurrent._lblbackyard.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1386;BA.debugLine="For Each link As String In links";
if (true) break;

case 12:
//for
this.state = 92;
group53 = _links;
index53 = 0;
groupLen53 = group53.getSize();
this.state = 109;
if (true) break;

case 109:
//C
this.state = 92;
if (index53 < groupLen53) {
this.state = 14;
_link = BA.ObjectToString(group53.Get(index53));}
if (true) break;

case 110:
//C
this.state = 109;
index53++;
if (true) break;

case 14:
//C
this.state = 15;
 //BA.debugLineNum = 1387;BA.debugLine="camera = link";
_camera = _link;
 //BA.debugLineNum = 1388;BA.debugLine="If camera <> whatCamera And whatCamera <> \"All\"";
if (true) break;

case 15:
//if
this.state = 18;
if ((_camera).equals(_whatcamera) == false && (_whatcamera).equals("All") == false) { 
this.state = 17;
}if (true) break;

case 17:
//C
this.state = 18;
 //BA.debugLineNum = 1389;BA.debugLine="Continue";
this.state = 110;
if (true) break;;
 if (true) break;
;
 //BA.debugLineNum = 1391;BA.debugLine="If FirstRun Then";

case 18:
//if
this.state = 39;
if (_firstrun) { 
this.state = 20;
}else {
this.state = 30;
}if (true) break;

case 20:
//C
this.state = 21;
 //BA.debugLineNum = 1392;BA.debugLine="If camera = \"347574\" Then";
if (true) break;

case 21:
//if
this.state = 28;
if ((_camera).equals("347574")) { 
this.state = 23;
}else if((_camera).equals("236967")) { 
this.state = 25;
}else if((_camera).equals("458236")) { 
this.state = 27;
}if (true) break;

case 23:
//C
this.state = 28;
 //BA.debugLineNum = 1393;BA.debugLine="lblStatus.Text = \"Retrieving Side Yard thumbn";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("Retrieving Side Yard thumbnail..."));
 //BA.debugLineNum = 1394;BA.debugLine="iv = ivSideYard";
_iv = parent.mostCurrent._ivsideyard;
 if (true) break;

case 25:
//C
this.state = 28;
 //BA.debugLineNum = 1396;BA.debugLine="lblStatus.Text = \"Retrieving Front Yard thumb";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("Retrieving Front Yard thumbnail..."));
 //BA.debugLineNum = 1397;BA.debugLine="iv = ivFrontYard";
_iv = parent.mostCurrent._ivfrontyard;
 if (true) break;

case 27:
//C
this.state = 28;
 //BA.debugLineNum = 1399;BA.debugLine="lblStatus.Text = \"Retrieving Backyard thumbna";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("Retrieving Backyard thumbnail..."));
 //BA.debugLineNum = 1400;BA.debugLine="iv = ivBackyard";
_iv = parent.mostCurrent._ivbackyard;
 if (true) break;

case 28:
//C
this.state = 39;
;
 if (true) break;

case 30:
//C
this.state = 31;
 //BA.debugLineNum = 1403;BA.debugLine="If camera = \"347574\" Then";
if (true) break;

case 31:
//if
this.state = 38;
if ((_camera).equals("347574")) { 
this.state = 33;
}else if((_camera).equals("236967")) { 
this.state = 35;
}else if((_camera).equals("458236")) { 
this.state = 37;
}if (true) break;

case 33:
//C
this.state = 38;
 //BA.debugLineNum = 1404;BA.debugLine="lblStatus.Text = \"Capturing a new Side Yard t";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("Capturing a new Side Yard thumbnail..."));
 //BA.debugLineNum = 1405;BA.debugLine="iv = ivSideYard";
_iv = parent.mostCurrent._ivsideyard;
 //BA.debugLineNum = 1406;BA.debugLine="attempts = 15";
_attempts = (int) (15);
 if (true) break;

case 35:
//C
this.state = 38;
 //BA.debugLineNum = 1408;BA.debugLine="lblStatus.Text = \"Capturing a new Front Yard";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("Capturing a new Front Yard thumbnail..."));
 //BA.debugLineNum = 1409;BA.debugLine="iv = ivFrontYard";
_iv = parent.mostCurrent._ivfrontyard;
 //BA.debugLineNum = 1410;BA.debugLine="attempts = 15";
_attempts = (int) (15);
 if (true) break;

case 37:
//C
this.state = 38;
 //BA.debugLineNum = 1412;BA.debugLine="lblStatus.Text = \"Capturing a new Backyard th";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("Capturing a new Backyard thumbnail..."));
 //BA.debugLineNum = 1413;BA.debugLine="iv = ivBackyard";
_iv = parent.mostCurrent._ivbackyard;
 //BA.debugLineNum = 1414;BA.debugLine="attempts = 15";
_attempts = (int) (15);
 if (true) break;

case 38:
//C
this.state = 39;
;
 if (true) break;
;
 //BA.debugLineNum = 1418;BA.debugLine="If FirstRun Then";

case 39:
//if
this.state = 91;
if (_firstrun) { 
this.state = 41;
}else {
this.state = 43;
}if (true) break;

case 41:
//C
this.state = 91;
 //BA.debugLineNum = 1419;BA.debugLine="Dim rs As ResumableSub = RESTGet(\"https://rest";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _restget("https://rest-"+parent._userregion+".immedia-semi.com/network/"+parent._networkid+"/camera/"+_camera);
 //BA.debugLineNum = 1420;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 111;
return;
case 111:
//C
this.state = 91;
_result = (Object) result[0];
;
 //BA.debugLineNum = 1422;BA.debugLine="Dim rs As ResumableSub = GetCameraInfo(respons";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _getcamerainfo(parent._response,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1423;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 112;
return;
case 112:
//C
this.state = 91;
_result = (Object) result[0];
;
 //BA.debugLineNum = 1425;BA.debugLine="Dim rs As ResumableSub = DownloadImage(\"https:";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _downloadimage("https://rest-"+parent._userregion+".immedia-semi.com/"+parent._camerathumbnail+".jpg",_iv,_camera);
 //BA.debugLineNum = 1426;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 113;
return;
case 113:
//C
this.state = 91;
_result = (Object) result[0];
;
 if (true) break;

case 43:
//C
this.state = 44;
 //BA.debugLineNum = 1428;BA.debugLine="Dim rs As ResumableSub = RESTGet(\"https://rest";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _restget("https://rest-"+parent._userregion+".immedia-semi.com/network/"+parent._networkid+"/camera/"+_camera);
 //BA.debugLineNum = 1429;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 114;
return;
case 114:
//C
this.state = 44;
_result = (Object) result[0];
;
 //BA.debugLineNum = 1431;BA.debugLine="Dim rs As ResumableSub = GetCameraInfo(respons";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _getcamerainfo(parent._response,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1432;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 115;
return;
case 115:
//C
this.state = 44;
_result = (Object) result[0];
;
 //BA.debugLineNum = 1434;BA.debugLine="prevCameraThumbnail = cameraThumbnail";
parent._prevcamerathumbnail = parent._camerathumbnail;
 //BA.debugLineNum = 1436;BA.debugLine="Dim rs As ResumableSub = RESTPost(\"https://res";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _restpost("https://rest-"+parent._userregion+".immedia-semi.com/network/"+parent._networkid+"/camera/"+_camera+"/thumbnail");
 //BA.debugLineNum = 1437;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 116;
return;
case 116:
//C
this.state = 44;
_result = (Object) result[0];
;
 //BA.debugLineNum = 1438;BA.debugLine="If response.StartsWith(\"ERROR: \") Or response.";
if (true) break;

case 44:
//if
this.state = 90;
if (parent._response.startsWith("ERROR: ") || parent._response.contains("System is busy, please wait")) { 
this.state = 46;
}else {
this.state = 48;
}if (true) break;

case 46:
//C
this.state = 90;
 //BA.debugLineNum = 1439;BA.debugLine="btnSideYard.Enabled = True";
parent.mostCurrent._btnsideyard._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1440;BA.debugLine="btnRefresh.Enabled = True";
parent.mostCurrent._btnrefresh._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1441;BA.debugLine="btnSideYardNewClip.Enabled = True";
parent.mostCurrent._btnsideyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1442;BA.debugLine="btnFrontYardNewClip.Enabled = True";
parent.mostCurrent._btnfrontyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1443;BA.debugLine="btnBackyardNewClip.Enabled = True";
parent.mostCurrent._btnbackyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1444;BA.debugLine="btnSideYardRefresh.Enabled = True";
parent.mostCurrent._btnsideyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1445;BA.debugLine="btnFrontYardRefresh.Enabled = True";
parent.mostCurrent._btnfrontyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1446;BA.debugLine="btnBackyardRefresh.Enabled = True";
parent.mostCurrent._btnbackyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1447;BA.debugLine="swArmed.Enabled = True";
parent.mostCurrent._swarmed._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1448;BA.debugLine="Return Null";
if (true) {
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,anywheresoftware.b4a.keywords.Common.Null);return;};
 if (true) break;

case 48:
//C
this.state = 49;
 //BA.debugLineNum = 1450;BA.debugLine="Dim rs As ResumableSub = GetCommandID(respons";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _getcommandid(parent._response);
 //BA.debugLineNum = 1451;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 117;
return;
case 117:
//C
this.state = 49;
_result = (Object) result[0];
;
 //BA.debugLineNum = 1453;BA.debugLine="Dim rs As ResumableSub = RESTGet(\"https://res";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _restget("https://rest-"+parent._userregion+".immedia-semi.com/network/"+parent._networkid+"/command/"+parent._commandid);
 //BA.debugLineNum = 1454;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 118;
return;
case 118:
//C
this.state = 49;
_result = (Object) result[0];
;
 //BA.debugLineNum = 1456;BA.debugLine="If response.StartsWith(\"ERROR: \") Or response";
if (true) break;

case 49:
//if
this.state = 89;
if (parent._response.startsWith("ERROR: ") || parent._response.contains("System is busy, please wait")) { 
this.state = 51;
}else {
this.state = 53;
}if (true) break;

case 51:
//C
this.state = 89;
 //BA.debugLineNum = 1457;BA.debugLine="btnSideYard.Enabled = True";
parent.mostCurrent._btnsideyard._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1458;BA.debugLine="btnRefresh.Enabled = True";
parent.mostCurrent._btnrefresh._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1459;BA.debugLine="btnSideYardNewClip.Enabled = True";
parent.mostCurrent._btnsideyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1460;BA.debugLine="btnFrontYardNewClip.Enabled = True";
parent.mostCurrent._btnfrontyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1461;BA.debugLine="btnBackyardNewClip.Enabled = True";
parent.mostCurrent._btnbackyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1462;BA.debugLine="btnSideYardRefresh.Enabled = True";
parent.mostCurrent._btnsideyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1463;BA.debugLine="btnFrontYardRefresh.Enabled = True";
parent.mostCurrent._btnfrontyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1464;BA.debugLine="btnBackyardRefresh.Enabled = True";
parent.mostCurrent._btnbackyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1465;BA.debugLine="swArmed.Enabled = True";
parent.mostCurrent._swarmed._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1466;BA.debugLine="Return Null";
if (true) {
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,anywheresoftware.b4a.keywords.Common.Null);return;};
 if (true) break;

case 53:
//C
this.state = 54;
 //BA.debugLineNum = 1468;BA.debugLine="Dim reChecked As Boolean";
_rechecked = false;
 //BA.debugLineNum = 1469;BA.debugLine="For i = 1 To attempts";
if (true) break;

case 54:
//for
this.state = 73;
step128 = 1;
limit128 = _attempts;
_i = (int) (1) ;
this.state = 119;
if (true) break;

case 119:
//C
this.state = 73;
if ((step128 > 0 && _i <= limit128) || (step128 < 0 && _i >= limit128)) this.state = 56;
if (true) break;

case 120:
//C
this.state = 119;
_i = ((int)(0 + _i + step128)) ;
if (true) break;

case 56:
//C
this.state = 57;
 //BA.debugLineNum = 1471;BA.debugLine="Dim rs As ResumableSub = GetCommandResponse";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _getcommandresponse(_iv,_camera,BA.NumberToString(_i),BA.NumberToString(_attempts));
 //BA.debugLineNum = 1472;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 121;
return;
case 121:
//C
this.state = 57;
_result = (Object) result[0];
;
 //BA.debugLineNum = 1473;BA.debugLine="If Result Then Exit";
if (true) break;

case 57:
//if
this.state = 62;
if (BA.ObjectToBoolean(_result)) { 
this.state = 59;
;}if (true) break;

case 59:
//C
this.state = 62;
this.state = 73;
if (true) break;
if (true) break;

case 62:
//C
this.state = 63;
;
 //BA.debugLineNum = 1475;BA.debugLine="Dim rs As ResumableSub = RESTGet(\"https://r";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _restget("https://rest-"+parent._userregion+".immedia-semi.com/network/"+parent._networkid+"/command/"+parent._commandid);
 //BA.debugLineNum = 1476;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 122;
return;
case 122:
//C
this.state = 63;
_result = (Object) result[0];
;
 //BA.debugLineNum = 1477;BA.debugLine="Sleep(1000) ' 1 second";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (1000));
this.state = 123;
return;
case 123:
//C
this.state = 63;
;
 //BA.debugLineNum = 1479;BA.debugLine="If i = attempts And reChecked = False Then";
if (true) break;

case 63:
//if
this.state = 72;
if (_i==_attempts && _rechecked==anywheresoftware.b4a.keywords.Common.False) { 
this.state = 65;
}if (true) break;

case 65:
//C
this.state = 66;
 //BA.debugLineNum = 1480;BA.debugLine="reChecked = True";
_rechecked = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 1481;BA.debugLine="Log(\"*********** HERE *********** i = \" &";
anywheresoftware.b4a.keywords.Common.LogImpl("336438176","*********** HERE *********** i = "+BA.NumberToString(_i),0);
 //BA.debugLineNum = 1482;BA.debugLine="Dim rs As ResumableSub = RESTPost(\"https:/";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _restpost("https://rest-"+parent._userregion+".immedia-semi.com/network/"+parent._networkid+"/camera/"+_camera+"/thumbnail");
 //BA.debugLineNum = 1483;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 124;
return;
case 124:
//C
this.state = 66;
_result = (Object) result[0];
;
 //BA.debugLineNum = 1485;BA.debugLine="If response.StartsWith(\"ERROR: \") Or respo";
if (true) break;

case 66:
//if
this.state = 71;
if (parent._response.startsWith("ERROR: ") || parent._response.contains("System is busy, please wait")) { 
this.state = 68;
}else {
this.state = 70;
}if (true) break;

case 68:
//C
this.state = 71;
 //BA.debugLineNum = 1486;BA.debugLine="btnSideYard.Enabled = True";
parent.mostCurrent._btnsideyard._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1487;BA.debugLine="btnRefresh.Enabled = True";
parent.mostCurrent._btnrefresh._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1488;BA.debugLine="btnSideYardNewClip.Enabled = True";
parent.mostCurrent._btnsideyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1489;BA.debugLine="btnFrontYardNewClip.Enabled = True";
parent.mostCurrent._btnfrontyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1490;BA.debugLine="btnBackyardNewClip.Enabled = True";
parent.mostCurrent._btnbackyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1491;BA.debugLine="btnSideYardRefresh.Enabled = True";
parent.mostCurrent._btnsideyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1492;BA.debugLine="btnFrontYardRefresh.Enabled = True";
parent.mostCurrent._btnfrontyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1493;BA.debugLine="btnBackyardRefresh.Enabled = True";
parent.mostCurrent._btnbackyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1494;BA.debugLine="swArmed.Enabled = True";
parent.mostCurrent._swarmed._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1495;BA.debugLine="Return Null";
if (true) {
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,anywheresoftware.b4a.keywords.Common.Null);return;};
 if (true) break;

case 70:
//C
this.state = 71;
 //BA.debugLineNum = 1497;BA.debugLine="i = 1";
_i = (int) (1);
 if (true) break;

case 71:
//C
this.state = 72;
;
 //BA.debugLineNum = 1499;BA.debugLine="Log(\"*********** HERE TWO ***********\")";
anywheresoftware.b4a.keywords.Common.LogImpl("336438194","*********** HERE TWO ***********",0);
 //BA.debugLineNum = 1500;BA.debugLine="Dim rs As ResumableSub = GetCommandID(resp";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _getcommandid(parent._response);
 //BA.debugLineNum = 1501;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 125;
return;
case 125:
//C
this.state = 72;
_result = (Object) result[0];
;
 //BA.debugLineNum = 1503;BA.debugLine="Dim rs As ResumableSub = RESTGet(\"https://";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _restget("https://rest-"+parent._userregion+".immedia-semi.com/network/"+parent._networkid+"/command/"+parent._commandid);
 //BA.debugLineNum = 1504;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 126;
return;
case 126:
//C
this.state = 72;
_result = (Object) result[0];
;
 if (true) break;

case 72:
//C
this.state = 120;
;
 if (true) break;
if (true) break;
;
 //BA.debugLineNum = 1507;BA.debugLine="If response.StartsWith(\"ERROR: \") Or respons";

case 73:
//if
this.state = 88;
if (parent._response.startsWith("ERROR: ") || parent._response.contains("System is busy, please wait")) { 
this.state = 75;
}else if(parent._commandcomplete==anywheresoftware.b4a.keywords.Common.False) { 
this.state = 77;
}else {
this.state = 87;
}if (true) break;

case 75:
//C
this.state = 88;
 //BA.debugLineNum = 1508;BA.debugLine="btnSideYard.Enabled = True";
parent.mostCurrent._btnsideyard._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1509;BA.debugLine="btnRefresh.Enabled = True";
parent.mostCurrent._btnrefresh._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1510;BA.debugLine="btnSideYardNewClip.Enabled = True";
parent.mostCurrent._btnsideyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1511;BA.debugLine="btnFrontYardNewClip.Enabled = True";
parent.mostCurrent._btnfrontyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1512;BA.debugLine="btnBackyardNewClip.Enabled = True";
parent.mostCurrent._btnbackyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1513;BA.debugLine="btnSideYardRefresh.Enabled = True";
parent.mostCurrent._btnsideyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1514;BA.debugLine="btnFrontYardRefresh.Enabled = True";
parent.mostCurrent._btnfrontyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1515;BA.debugLine="btnBackyardRefresh.Enabled = True";
parent.mostCurrent._btnbackyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1516;BA.debugLine="swArmed.Enabled = True";
parent.mostCurrent._swarmed._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1517;BA.debugLine="Return Null";
if (true) {
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,anywheresoftware.b4a.keywords.Common.Null);return;};
 if (true) break;

case 77:
//C
this.state = 78;
 //BA.debugLineNum = 1519;BA.debugLine="If camera = \"347574\" Then";
if (true) break;

case 78:
//if
this.state = 85;
if ((_camera).equals("347574")) { 
this.state = 80;
}else if((_camera).equals("236967")) { 
this.state = 82;
}else if((_camera).equals("458236")) { 
this.state = 84;
}if (true) break;

case 80:
//C
this.state = 85;
 //BA.debugLineNum = 1520;BA.debugLine="lblStatus.Text = \"Failed to retrieve Side";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("Failed to retrieve Side Yard thumbnail..."));
 if (true) break;

case 82:
//C
this.state = 85;
 //BA.debugLineNum = 1522;BA.debugLine="lblStatus.Text = \"Failed to retrieve Front";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("Failed to retrieve Front Yard thumbnail..."));
 if (true) break;

case 84:
//C
this.state = 85;
 //BA.debugLineNum = 1524;BA.debugLine="lblStatus.Text = \"Failed to retrieve Backy";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("Failed to retrieve Backyard thumbnail..."));
 if (true) break;

case 85:
//C
this.state = 88;
;
 if (true) break;

case 87:
//C
this.state = 88;
 if (true) break;

case 88:
//C
this.state = 89;
;
 if (true) break;

case 89:
//C
this.state = 90;
;
 if (true) break;

case 90:
//C
this.state = 91;
;
 if (true) break;

case 91:
//C
this.state = 110;
;
 if (true) break;
if (true) break;

case 92:
//C
this.state = 95;
;
 //BA.debugLineNum = 1534;BA.debugLine="lblStatus.Text = \"Ready\"";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("Ready"));
 if (true) break;

case 94:
//C
this.state = 95;
this.catchState = 0;
 //BA.debugLineNum = 1536;BA.debugLine="Log(\"RefreshCamera LastException: \" & LastExcept";
anywheresoftware.b4a.keywords.Common.LogImpl("336438231","RefreshCamera LastException: "+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 if (true) break;
if (true) break;

case 95:
//C
this.state = 96;
this.catchState = 0;
;
 //BA.debugLineNum = 1538;BA.debugLine="btnSideYard.Enabled = True";
parent.mostCurrent._btnsideyard._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1539;BA.debugLine="btnRefresh.Enabled = True";
parent.mostCurrent._btnrefresh._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1540;BA.debugLine="btnSideYardNewClip.Enabled = True";
parent.mostCurrent._btnsideyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1541;BA.debugLine="btnFrontYardNewClip.Enabled = True";
parent.mostCurrent._btnfrontyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1542;BA.debugLine="btnBackyardNewClip.Enabled = True";
parent.mostCurrent._btnbackyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1543;BA.debugLine="btnSideYardRefresh.Enabled = True";
parent.mostCurrent._btnsideyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1544;BA.debugLine="btnFrontYardRefresh.Enabled = True";
parent.mostCurrent._btnfrontyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1545;BA.debugLine="btnBackyardRefresh.Enabled = True";
parent.mostCurrent._btnbackyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1546;BA.debugLine="swArmed.Enabled = True";
parent.mostCurrent._swarmed._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1547;BA.debugLine="If lblBackyardBatt.Text.Contains(\"OFFLINE\") Then";
if (true) break;

case 96:
//if
this.state = 103;
if (parent.mostCurrent._lblbackyardbatt.getText().contains("OFFLINE")) { 
this.state = 98;
}else if(parent.mostCurrent._lblsideyardbatt.getText().contains("OFFLINE")) { 
this.state = 100;
}else if(parent.mostCurrent._lblfrontyardbatt.getText().contains("OFFLINE")) { 
this.state = 102;
}if (true) break;

case 98:
//C
this.state = 103;
 //BA.debugLineNum = 1549;BA.debugLine="BlurIV(\"Backyard.jpg\",ivBackyard)";
_bluriv("Backyard.jpg",parent.mostCurrent._ivbackyard);
 if (true) break;

case 100:
//C
this.state = 103;
 //BA.debugLineNum = 1552;BA.debugLine="BlurIV(\"SideYard.jpg\",ivSideYard)";
_bluriv("SideYard.jpg",parent.mostCurrent._ivsideyard);
 if (true) break;

case 102:
//C
this.state = 103;
 //BA.debugLineNum = 1555;BA.debugLine="BlurIV(\"FrontYard.jpg\",ivFrontYard)";
_bluriv("FrontYard.jpg",parent.mostCurrent._ivfrontyard);
 if (true) break;

case 103:
//C
this.state = -1;
;
 //BA.debugLineNum = 1557;BA.debugLine="Return Null";
if (true) {
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,anywheresoftware.b4a.keywords.Common.Null);return;};
 //BA.debugLineNum = 1558;BA.debugLine="End Sub";
if (true) break;
}} 
       catch (Exception e0) {
			
if (catchState == 0)
    throw e0;
else {
    state = catchState;
processBA.setLastException(e0);}
            }
        }
    }
}
public static anywheresoftware.b4a.keywords.Common.ResumableSubWrapper  _requestauthtoken() throws Exception{
ResumableSub_RequestAuthToken rsub = new ResumableSub_RequestAuthToken(null);
rsub.resume(processBA, null);
return (anywheresoftware.b4a.keywords.Common.ResumableSubWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper(), rsub);
}
public static class ResumableSub_RequestAuthToken extends BA.ResumableSub {
public ResumableSub_RequestAuthToken(cloyd.smart.home.monitor.main parent) {
this.parent = parent;
}
cloyd.smart.home.monitor.main parent;
cloyd.smart.home.monitor.httpjob _joblogin = null;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
try {

        switch (state) {
            case -1:
{
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,null);return;}
case 0:
//C
this.state = 1;
 //BA.debugLineNum = 1255;BA.debugLine="Try";
if (true) break;

case 1:
//try
this.state = 18;
this.catchState = 17;
this.state = 3;
if (true) break;

case 3:
//C
this.state = 4;
this.catchState = 17;
 //BA.debugLineNum = 1256;BA.debugLine="lblStatus.Text = \"Authenticating...\"";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("Authenticating..."));
 //BA.debugLineNum = 1257;BA.debugLine="Dim jobLogin As HttpJob";
_joblogin = new cloyd.smart.home.monitor.httpjob();
 //BA.debugLineNum = 1258;BA.debugLine="jobLogin.Initialize(\"\", Me)";
_joblogin._initialize /*String*/ (processBA,"",main.getObject());
 //BA.debugLineNum = 1259;BA.debugLine="jobLogin.PostString(\"https://rest-prod.immedia-s";
_joblogin._poststring /*String*/ ("https://rest-prod.immedia-semi.com/api/v5/account/login","email="+parent._emailaddress+"&password="+parent._password+"&reauth=true");
 //BA.debugLineNum = 1264;BA.debugLine="jobLogin.GetRequest.SetContentType(\"application/";
_joblogin._getrequest /*anywheresoftware.b4h.okhttp.OkHttpClientWrapper.OkHttpRequest*/ ().SetContentType("application/x-www-form-urlencoded");
 //BA.debugLineNum = 1265;BA.debugLine="jobLogin.GetRequest.SetHeader(\"User-Agent\",Rando";
_joblogin._getrequest /*anywheresoftware.b4h.okhttp.OkHttpClientWrapper.OkHttpRequest*/ ().SetHeader("User-Agent",_randomstring((int) (12)));
 //BA.debugLineNum = 1266;BA.debugLine="Wait For (jobLogin) JobDone(jobLogin As HttpJob)";
anywheresoftware.b4a.keywords.Common.WaitFor("jobdone", processBA, this, (Object)(_joblogin));
this.state = 19;
return;
case 19:
//C
this.state = 4;
_joblogin = (cloyd.smart.home.monitor.httpjob) result[0];
;
 //BA.debugLineNum = 1267;BA.debugLine="If jobLogin.Success Then";
if (true) break;

case 4:
//if
this.state = 15;
if (_joblogin._success /*boolean*/ ) { 
this.state = 6;
}else {
this.state = 14;
}if (true) break;

case 6:
//C
this.state = 7;
 //BA.debugLineNum = 1268;BA.debugLine="lblStatus.Text = \"Successfully logged in to the";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("Successfully logged in to the Blink server..."));
 //BA.debugLineNum = 1269;BA.debugLine="GetAuthInfo(jobLogin.GetString)";
_getauthinfo(_joblogin._getstring /*String*/ ());
 //BA.debugLineNum = 1293;BA.debugLine="If response.StartsWith(\"ERROR: \") Or response.C";
if (true) break;

case 7:
//if
this.state = 12;
if (parent._response.startsWith("ERROR: ") || parent._response.contains("System is busy, please wait")) { 
this.state = 9;
}else {
this.state = 11;
}if (true) break;

case 9:
//C
this.state = 12;
 //BA.debugLineNum = 1294;BA.debugLine="btnSideYard.Enabled = True";
parent.mostCurrent._btnsideyard._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1295;BA.debugLine="btnRefresh.Enabled = True";
parent.mostCurrent._btnrefresh._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1296;BA.debugLine="btnSideYardNewClip.Enabled = True";
parent.mostCurrent._btnsideyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1297;BA.debugLine="btnFrontYardNewClip.Enabled = True";
parent.mostCurrent._btnfrontyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1298;BA.debugLine="btnBackyardNewClip.Enabled = True";
parent.mostCurrent._btnbackyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1299;BA.debugLine="btnSideYardRefresh.Enabled = True";
parent.mostCurrent._btnsideyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1300;BA.debugLine="btnFrontYardRefresh.Enabled = True";
parent.mostCurrent._btnfrontyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1301;BA.debugLine="btnBackyardRefresh.Enabled = True";
parent.mostCurrent._btnbackyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1302;BA.debugLine="swArmed.Enabled = True";
parent.mostCurrent._swarmed._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1303;BA.debugLine="jobLogin.Release";
_joblogin._release /*String*/ ();
 //BA.debugLineNum = 1304;BA.debugLine="Return Null";
if (true) {
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,anywheresoftware.b4a.keywords.Common.Null);return;};
 if (true) break;

case 11:
//C
this.state = 12;
 //BA.debugLineNum = 1306;BA.debugLine="lblStatus.Text = \"Authtoken acquired...\"";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("Authtoken acquired..."));
 if (true) break;

case 12:
//C
this.state = 15;
;
 if (true) break;

case 14:
//C
this.state = 15;
 //BA.debugLineNum = 1309;BA.debugLine="lblStatus.Text = GetRESTError(jobLogin.ErrorMes";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence(_getresterror(_joblogin._errormessage /*String*/ )));
 //BA.debugLineNum = 1310;BA.debugLine="Log(\"RequestAuthToken error: \" & jobLogin.Error";
anywheresoftware.b4a.keywords.Common.LogImpl("336372541","RequestAuthToken error: "+_joblogin._errormessage /*String*/ ,0);
 //BA.debugLineNum = 1311;BA.debugLine="jobLogin.Release";
_joblogin._release /*String*/ ();
 //BA.debugLineNum = 1312;BA.debugLine="Return Null";
if (true) {
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,anywheresoftware.b4a.keywords.Common.Null);return;};
 if (true) break;

case 15:
//C
this.state = 18;
;
 //BA.debugLineNum = 1314;BA.debugLine="jobLogin.Release";
_joblogin._release /*String*/ ();
 if (true) break;

case 17:
//C
this.state = 18;
this.catchState = 0;
 //BA.debugLineNum = 1316;BA.debugLine="Log(\"RequestAuthToken LastException: \" & LastExc";
anywheresoftware.b4a.keywords.Common.LogImpl("336372547","RequestAuthToken LastException: "+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 if (true) break;
if (true) break;

case 18:
//C
this.state = -1;
this.catchState = 0;
;
 //BA.debugLineNum = 1318;BA.debugLine="Return Null";
if (true) {
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,anywheresoftware.b4a.keywords.Common.Null);return;};
 //BA.debugLineNum = 1319;BA.debugLine="End Sub";
if (true) break;
}} 
       catch (Exception e0) {
			
if (catchState == 0)
    throw e0;
else {
    state = catchState;
processBA.setLastException(e0);}
            }
        }
    }
}
public static anywheresoftware.b4a.keywords.Common.ResumableSubWrapper  _restget(String _url) throws Exception{
ResumableSub_RESTGet rsub = new ResumableSub_RESTGet(null,_url);
rsub.resume(processBA, null);
return (anywheresoftware.b4a.keywords.Common.ResumableSubWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper(), rsub);
}
public static class ResumableSub_RESTGet extends BA.ResumableSub {
public ResumableSub_RESTGet(cloyd.smart.home.monitor.main parent,String _url) {
this.parent = parent;
this._url = _url;
}
cloyd.smart.home.monitor.main parent;
String _url;
cloyd.smart.home.monitor.httpjob _j = null;
anywheresoftware.b4a.keywords.Common.ResumableSubWrapper _rs = null;
Object _result = null;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
try {

        switch (state) {
            case -1:
{
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,null);return;}
case 0:
//C
this.state = 1;
 //BA.debugLineNum = 1605;BA.debugLine="Try";
if (true) break;

case 1:
//try
this.state = 15;
this.catchState = 14;
this.state = 3;
if (true) break;

case 3:
//C
this.state = 4;
this.catchState = 14;
 //BA.debugLineNum = 1606;BA.debugLine="Dim j As HttpJob";
_j = new cloyd.smart.home.monitor.httpjob();
 //BA.debugLineNum = 1607;BA.debugLine="response = \"\"";
parent._response = "";
 //BA.debugLineNum = 1608;BA.debugLine="j.Initialize(\"\", Me) 'name is empty as it is no";
_j._initialize /*String*/ (processBA,"",main.getObject());
 //BA.debugLineNum = 1609;BA.debugLine="j.Download(url)";
_j._download /*String*/ (_url);
 //BA.debugLineNum = 1610;BA.debugLine="j.GetRequest.SetHeader(\"TOKEN_AUTH\", authToken)";
_j._getrequest /*anywheresoftware.b4h.okhttp.OkHttpClientWrapper.OkHttpRequest*/ ().SetHeader("TOKEN_AUTH",parent._authtoken);
 //BA.debugLineNum = 1611;BA.debugLine="Wait For (j) JobDone(j As HttpJob)";
anywheresoftware.b4a.keywords.Common.WaitFor("jobdone", processBA, this, (Object)(_j));
this.state = 20;
return;
case 20:
//C
this.state = 4;
_j = (cloyd.smart.home.monitor.httpjob) result[0];
;
 //BA.debugLineNum = 1612;BA.debugLine="If j.Success Then";
if (true) break;

case 4:
//if
this.state = 9;
if (_j._success /*boolean*/ ) { 
this.state = 6;
}else {
this.state = 8;
}if (true) break;

case 6:
//C
this.state = 9;
 //BA.debugLineNum = 1613;BA.debugLine="response = j.GetString";
parent._response = _j._getstring /*String*/ ();
 if (true) break;

case 8:
//C
this.state = 9;
 //BA.debugLineNum = 1615;BA.debugLine="response = \"ERROR: \" & j.ErrorMessage";
parent._response = "ERROR: "+_j._errormessage /*String*/ ;
 //BA.debugLineNum = 1616;BA.debugLine="lblStatus.Text = GetRESTError(j.ErrorMessage)";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence(_getresterror(_j._errormessage /*String*/ )));
 if (true) break;
;
 //BA.debugLineNum = 1618;BA.debugLine="If response.Contains(\"System is busy, please wai";

case 9:
//if
this.state = 12;
if (parent._response.contains("System is busy, please wait")) { 
this.state = 11;
}if (true) break;

case 11:
//C
this.state = 12;
 //BA.debugLineNum = 1619;BA.debugLine="lblStatus.Text = \"System is busy, please wait\"";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("System is busy, please wait"));
 if (true) break;

case 12:
//C
this.state = 15;
;
 //BA.debugLineNum = 1621;BA.debugLine="j.Release";
_j._release /*String*/ ();
 if (true) break;

case 14:
//C
this.state = 15;
this.catchState = 0;
 //BA.debugLineNum = 1623;BA.debugLine="response = \"ERROR: \" & LastException";
parent._response = "ERROR: "+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA));
 //BA.debugLineNum = 1624;BA.debugLine="Log(\"RESTDownload LastException: \" & LastExcepti";
anywheresoftware.b4a.keywords.Common.LogImpl("336569108","RESTDownload LastException: "+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 if (true) break;
if (true) break;

case 15:
//C
this.state = 16;
this.catchState = 0;
;
 //BA.debugLineNum = 1626;BA.debugLine="Log(\"URL: \" & url)";
anywheresoftware.b4a.keywords.Common.LogImpl("336569110","URL: "+_url,0);
 //BA.debugLineNum = 1627;BA.debugLine="Log(\"Response: \" & response)";
anywheresoftware.b4a.keywords.Common.LogImpl("336569111","Response: "+parent._response,0);
 //BA.debugLineNum = 1629;BA.debugLine="If response.Contains(\"Unauthorized\") Then";
if (true) break;

case 16:
//if
this.state = 19;
if (parent._response.contains("Unauthorized")) { 
this.state = 18;
}if (true) break;

case 18:
//C
this.state = 19;
 //BA.debugLineNum = 1630;BA.debugLine="Dim rs As ResumableSub = RequestAuthToken";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _requestauthtoken();
 //BA.debugLineNum = 1631;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 21;
return;
case 21:
//C
this.state = 19;
_result = (Object) result[0];
;
 if (true) break;

case 19:
//C
this.state = -1;
;
 //BA.debugLineNum = 1634;BA.debugLine="Return(response)";
if (true) {
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,(Object)((parent._response)));return;};
 //BA.debugLineNum = 1635;BA.debugLine="End Sub";
if (true) break;
}} 
       catch (Exception e0) {
			
if (catchState == 0)
    throw e0;
else {
    state = catchState;
processBA.setLastException(e0);}
            }
        }
    }
}
public static anywheresoftware.b4a.keywords.Common.ResumableSubWrapper  _restpost(String _url) throws Exception{
ResumableSub_RESTPost rsub = new ResumableSub_RESTPost(null,_url);
rsub.resume(processBA, null);
return (anywheresoftware.b4a.keywords.Common.ResumableSubWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper(), rsub);
}
public static class ResumableSub_RESTPost extends BA.ResumableSub {
public ResumableSub_RESTPost(cloyd.smart.home.monitor.main parent,String _url) {
this.parent = parent;
this._url = _url;
}
cloyd.smart.home.monitor.main parent;
String _url;
cloyd.smart.home.monitor.httpjob _j = null;
anywheresoftware.b4a.keywords.Common.ResumableSubWrapper _rs = null;
Object _result = null;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
try {

        switch (state) {
            case -1:
{
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,null);return;}
case 0:
//C
this.state = 1;
 //BA.debugLineNum = 1638;BA.debugLine="Try";
if (true) break;

case 1:
//try
this.state = 15;
this.catchState = 14;
this.state = 3;
if (true) break;

case 3:
//C
this.state = 4;
this.catchState = 14;
 //BA.debugLineNum = 1639;BA.debugLine="Dim j As HttpJob";
_j = new cloyd.smart.home.monitor.httpjob();
 //BA.debugLineNum = 1640;BA.debugLine="response = \"\"";
parent._response = "";
 //BA.debugLineNum = 1641;BA.debugLine="j.Initialize(\"\", Me) 'name is empty as it is no";
_j._initialize /*String*/ (processBA,"",main.getObject());
 //BA.debugLineNum = 1642;BA.debugLine="j.PostString(url,\"\")";
_j._poststring /*String*/ (_url,"");
 //BA.debugLineNum = 1643;BA.debugLine="j.GetRequest.SetContentType(\"application/x-www-f";
_j._getrequest /*anywheresoftware.b4h.okhttp.OkHttpClientWrapper.OkHttpRequest*/ ().SetContentType("application/x-www-form-urlencoded");
 //BA.debugLineNum = 1644;BA.debugLine="j.GetRequest.SetHeader(\"TOKEN_AUTH\", authToken)";
_j._getrequest /*anywheresoftware.b4h.okhttp.OkHttpClientWrapper.OkHttpRequest*/ ().SetHeader("TOKEN_AUTH",parent._authtoken);
 //BA.debugLineNum = 1645;BA.debugLine="Wait For (j) JobDone(j As HttpJob)";
anywheresoftware.b4a.keywords.Common.WaitFor("jobdone", processBA, this, (Object)(_j));
this.state = 20;
return;
case 20:
//C
this.state = 4;
_j = (cloyd.smart.home.monitor.httpjob) result[0];
;
 //BA.debugLineNum = 1646;BA.debugLine="If j.Success Then";
if (true) break;

case 4:
//if
this.state = 9;
if (_j._success /*boolean*/ ) { 
this.state = 6;
}else {
this.state = 8;
}if (true) break;

case 6:
//C
this.state = 9;
 //BA.debugLineNum = 1647;BA.debugLine="response = j.GetString";
parent._response = _j._getstring /*String*/ ();
 if (true) break;

case 8:
//C
this.state = 9;
 //BA.debugLineNum = 1649;BA.debugLine="response = \"ERROR: \" & j.ErrorMessage";
parent._response = "ERROR: "+_j._errormessage /*String*/ ;
 //BA.debugLineNum = 1650;BA.debugLine="lblStatus.Text = GetRESTError(j.ErrorMessage)";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence(_getresterror(_j._errormessage /*String*/ )));
 if (true) break;
;
 //BA.debugLineNum = 1652;BA.debugLine="If response.Contains(\"System is busy, please wai";

case 9:
//if
this.state = 12;
if (parent._response.contains("System is busy, please wait")) { 
this.state = 11;
}if (true) break;

case 11:
//C
this.state = 12;
 //BA.debugLineNum = 1653;BA.debugLine="lblStatus.Text = \"System is busy, please wait\"";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("System is busy, please wait"));
 if (true) break;

case 12:
//C
this.state = 15;
;
 //BA.debugLineNum = 1655;BA.debugLine="j.Release";
_j._release /*String*/ ();
 if (true) break;

case 14:
//C
this.state = 15;
this.catchState = 0;
 //BA.debugLineNum = 1657;BA.debugLine="response = \"ERROR: \" & LastException";
parent._response = "ERROR: "+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA));
 //BA.debugLineNum = 1658;BA.debugLine="Log(\"RESTPost LastException: \" & LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("336634645","RESTPost LastException: "+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 if (true) break;
if (true) break;

case 15:
//C
this.state = 16;
this.catchState = 0;
;
 //BA.debugLineNum = 1660;BA.debugLine="Log(\"URL: \" & url)";
anywheresoftware.b4a.keywords.Common.LogImpl("336634647","URL: "+_url,0);
 //BA.debugLineNum = 1661;BA.debugLine="Log(\"Response: \" & response)";
anywheresoftware.b4a.keywords.Common.LogImpl("336634648","Response: "+parent._response,0);
 //BA.debugLineNum = 1663;BA.debugLine="If response.Contains(\"Unauthorized\") Then";
if (true) break;

case 16:
//if
this.state = 19;
if (parent._response.contains("Unauthorized")) { 
this.state = 18;
}if (true) break;

case 18:
//C
this.state = 19;
 //BA.debugLineNum = 1664;BA.debugLine="Dim rs As ResumableSub = RequestAuthToken";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _requestauthtoken();
 //BA.debugLineNum = 1665;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 21;
return;
case 21:
//C
this.state = 19;
_result = (Object) result[0];
;
 if (true) break;

case 19:
//C
this.state = -1;
;
 //BA.debugLineNum = 1668;BA.debugLine="Return(response)";
if (true) {
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,(Object)((parent._response)));return;};
 //BA.debugLineNum = 1669;BA.debugLine="End Sub";
if (true) break;
}} 
       catch (Exception e0) {
			
if (catchState == 0)
    throw e0;
else {
    state = catchState;
processBA.setLastException(e0);}
            }
        }
    }
}
public static String  _setdefaults() throws Exception{
 //BA.debugLineNum = 1234;BA.debugLine="Sub SetDefaults";
 //BA.debugLineNum = 1236;BA.debugLine="manager.SetString(\"HumidityAddValue\", \"6\")";
_manager.SetString("HumidityAddValue","6");
 //BA.debugLineNum = 1237;BA.debugLine="manager.SetString(\"SensorNotRespondingTime\", \"10\"";
_manager.SetString("SensorNotRespondingTime","10");
 //BA.debugLineNum = 1238;BA.debugLine="StateManager.SetSetting(\"HumidityAddValue\",\"6\")";
mostCurrent._statemanager._setsetting /*String*/ (mostCurrent.activityBA,"HumidityAddValue","6");
 //BA.debugLineNum = 1239;BA.debugLine="StateManager.SetSetting(\"SensorNotRespondingTime\"";
mostCurrent._statemanager._setsetting /*String*/ (mostCurrent.activityBA,"SensorNotRespondingTime","10");
 //BA.debugLineNum = 1240;BA.debugLine="StateManager.SaveSettings";
mostCurrent._statemanager._savesettings /*String*/ (mostCurrent.activityBA);
 //BA.debugLineNum = 1241;BA.debugLine="End Sub";
return "";
}
public static String  _settextshadow(anywheresoftware.b4a.objects.LabelWrapper _lbl) throws Exception{
anywheresoftware.b4j.object.JavaObject _jo = null;
float _radius = 0f;
float _dx = 0f;
float _dy = 0f;
 //BA.debugLineNum = 2394;BA.debugLine="Sub SetTextShadow(lbl As Label)";
 //BA.debugLineNum = 2395;BA.debugLine="Dim jo As JavaObject = lbl";
_jo = new anywheresoftware.b4j.object.JavaObject();
_jo = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(_lbl.getObject()));
 //BA.debugLineNum = 2396;BA.debugLine="Dim radius = 2dip, dx = 0dip, dy = 0dip As Float";
_radius = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (2)));
_dx = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)));
_dy = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)));
 //BA.debugLineNum = 2397;BA.debugLine="jo.RunMethod(\"setShadowLayer\", Array(radius, dx,";
_jo.RunMethod("setShadowLayer",new Object[]{(Object)(_radius),(Object)(_dx),(Object)(_dy),(Object)(anywheresoftware.b4a.keywords.Common.Colors.Black)});
 //BA.debugLineNum = 2398;BA.debugLine="End Sub";
return "";
}
public static String  _showaboutmenu() throws Exception{
anywheresoftware.b4a.objects.drawable.BitmapDrawable _bd = null;
String _tempdate = "";
 //BA.debugLineNum = 699;BA.debugLine="Sub ShowAboutMenu";
 //BA.debugLineNum = 700;BA.debugLine="Try";
try { //BA.debugLineNum = 701;BA.debugLine="Dim bd As BitmapDrawable";
_bd = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
 //BA.debugLineNum = 702;BA.debugLine="bd.Initialize(LoadBitmapResize(File.DirAssets, \"";
_bd.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmapResize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"cloyd.png",anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (32)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (32)),anywheresoftware.b4a.keywords.Common.True).getObject()));
 //BA.debugLineNum = 703;BA.debugLine="Dim tempDate As String";
_tempdate = "";
 //BA.debugLineNum = 704;BA.debugLine="DateTime.DateFormat=\"EEE, MMM d, yyyy h:mm aa\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("EEE, MMM d, yyyy h:mm aa");
 //BA.debugLineNum = 705;BA.debugLine="tempDate = DateTime.Date(compileTimeStamp)";
_tempdate = anywheresoftware.b4a.keywords.Common.DateTime.Date((long)(Double.parseDouble(_compiletimestamp)));
 //BA.debugLineNum = 706;BA.debugLine="Msgbox2(\"Smart Home Monitor v\" & GetVersionCode";
anywheresoftware.b4a.keywords.Common.Msgbox2(BA.ObjectToCharSequence("Smart Home Monitor v"+_getversioncode()+anywheresoftware.b4a.keywords.Common.CRLF+anywheresoftware.b4a.keywords.Common.CRLF+"Developed by: Cloyd Nino Catanaoan"+anywheresoftware.b4a.keywords.Common.CRLF+"Compiled: "+_tempdate),BA.ObjectToCharSequence("About"),"OK","","",_bd.getBitmap(),mostCurrent.activityBA);
 } 
       catch (Exception e9) {
			processBA.setLastException(e9); //BA.debugLineNum = 708;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("335258377",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 };
 //BA.debugLineNum = 710;BA.debugLine="End Sub";
return "";
}
public static anywheresoftware.b4a.keywords.Common.ResumableSubWrapper  _showvideo(String _link) throws Exception{
ResumableSub_ShowVideo rsub = new ResumableSub_ShowVideo(null,_link);
rsub.resume(processBA, null);
return (anywheresoftware.b4a.keywords.Common.ResumableSubWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper(), rsub);
}
public static class ResumableSub_ShowVideo extends BA.ResumableSub {
public ResumableSub_ShowVideo(cloyd.smart.home.monitor.main parent,String _link) {
this.parent = parent;
this._link = _link;
}
cloyd.smart.home.monitor.main parent;
String _link;
String _videoname = "";
cloyd.smart.home.monitor.httpjob _j = null;
anywheresoftware.b4a.objects.streams.File.OutputStreamWrapper _out = null;
anywheresoftware.b4a.keywords.StringBuilderWrapper _sb = null;
anywheresoftware.b4a.agraham.reflection.Reflection _r = null;
long _ticks = 0L;
anywheresoftware.b4a.objects.collections.List _list1 = null;
int _i = 0;
Object _mytypes = null;
cloyd.smart.home.monitor.main._videoinfo _videos = null;
anywheresoftware.b4a.objects.IntentWrapper _intent1 = null;
anywheresoftware.b4a.phone.Phone _phone = null;
anywheresoftware.b4a.objects.B4XViewWrapper _p = null;
anywheresoftware.b4a.objects.ImageViewWrapper _contentlabel = null;
cloyd.smart.home.monitor.main._carddata _cd = null;
anywheresoftware.b4a.objects.LabelWrapper _contentlabel1 = null;
int step38;
int limit38;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
try {

        switch (state) {
            case -1:
{
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,null);return;}
case 0:
//C
this.state = 1;
 //BA.debugLineNum = 2882;BA.debugLine="Try";
if (true) break;

case 1:
//try
this.state = 28;
this.catchState = 27;
this.state = 3;
if (true) break;

case 3:
//C
this.state = 4;
this.catchState = 27;
 //BA.debugLineNum = 2883;BA.debugLine="Dim videoName As String";
_videoname = "";
 //BA.debugLineNum = 2884;BA.debugLine="videoName = Link.SubString(Link.LastIndexOf(\"/\")";
_videoname = _link.substring((int) (_link.lastIndexOf("/")+1));
 //BA.debugLineNum = 2886;BA.debugLine="If File.Exists(File.DirInternal, videoName) = Fa";
if (true) break;

case 4:
//if
this.state = 13;
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),_videoname)==anywheresoftware.b4a.keywords.Common.False) { 
this.state = 6;
}if (true) break;

case 6:
//C
this.state = 7;
 //BA.debugLineNum = 2887;BA.debugLine="Dim j As HttpJob";
_j = new cloyd.smart.home.monitor.httpjob();
 //BA.debugLineNum = 2888;BA.debugLine="j.Initialize(\"\", Me)";
_j._initialize /*String*/ (processBA,"",main.getObject());
 //BA.debugLineNum = 2889;BA.debugLine="j.Download(Link)";
_j._download /*String*/ (_link);
 //BA.debugLineNum = 2890;BA.debugLine="j.GetRequest.SetHeader(\"TOKEN_AUTH\", authToken)";
_j._getrequest /*anywheresoftware.b4h.okhttp.OkHttpClientWrapper.OkHttpRequest*/ ().SetHeader("TOKEN_AUTH",parent._authtoken);
 //BA.debugLineNum = 2891;BA.debugLine="Wait For (j) JobDone(j As HttpJob)";
anywheresoftware.b4a.keywords.Common.WaitFor("jobdone", processBA, this, (Object)(_j));
this.state = 42;
return;
case 42:
//C
this.state = 7;
_j = (cloyd.smart.home.monitor.httpjob) result[0];
;
 //BA.debugLineNum = 2892;BA.debugLine="If j.Success Then";
if (true) break;

case 7:
//if
this.state = 12;
if (_j._success /*boolean*/ ) { 
this.state = 9;
}else {
this.state = 11;
}if (true) break;

case 9:
//C
this.state = 12;
 //BA.debugLineNum = 2894;BA.debugLine="Dim out As OutputStream = File.OpenOutput(File";
_out = new anywheresoftware.b4a.objects.streams.File.OutputStreamWrapper();
_out = anywheresoftware.b4a.keywords.Common.File.OpenOutput(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),_videoname,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2895;BA.debugLine="File.Copy2(j.GetInputStream, out)";
anywheresoftware.b4a.keywords.Common.File.Copy2((java.io.InputStream)(_j._getinputstream /*anywheresoftware.b4a.objects.streams.File.InputStreamWrapper*/ ().getObject()),(java.io.OutputStream)(_out.getObject()));
 //BA.debugLineNum = 2896;BA.debugLine="out.Close '<------ very important";
_out.Close();
 if (true) break;

case 11:
//C
this.state = 12;
 if (true) break;

case 12:
//C
this.state = 13;
;
 if (true) break;

case 13:
//C
this.state = 14;
;
 //BA.debugLineNum = 2902;BA.debugLine="B4XLoadingIndicator4.hide";
parent.mostCurrent._b4xloadingindicator4._hide /*String*/ ();
 //BA.debugLineNum = 2904;BA.debugLine="Dim sb As StringBuilder";
_sb = new anywheresoftware.b4a.keywords.StringBuilderWrapper();
 //BA.debugLineNum = 2905;BA.debugLine="sb.Initialize";
_sb.Initialize();
 //BA.debugLineNum = 2906;BA.debugLine="sb.Append(\"<video width='100%' height='100%' con";
_sb.Append("<video width='100%' height='100%' controls autoplay muted>");
 //BA.debugLineNum = 2907;BA.debugLine="sb.Append(\"<source src='\" & File.Combine(File.Di";
_sb.Append("<source src='"+anywheresoftware.b4a.keywords.Common.File.Combine(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"\\"+_videoname)+"' Type='video/mp4'/>");
 //BA.debugLineNum = 2908;BA.debugLine="sb.Append(\"</video>\")";
_sb.Append("</video>");
 //BA.debugLineNum = 2909;BA.debugLine="Dim WebViewSettings1 As WebViewSettings";
parent.mostCurrent._webviewsettings1 = new uk.co.martinpearman.b4a.webviewsettings.WebViewSettings();
 //BA.debugLineNum = 2910;BA.debugLine="WebViewSettings1.setMediaPlaybackRequiresUserGes";
parent.mostCurrent._webviewsettings1.setMediaPlaybackRequiresUserGesture((android.webkit.WebView)(parent.mostCurrent._wvmedia.getObject()),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2911;BA.debugLine="wvMedia.LoadHtml(sb.ToString)";
parent.mostCurrent._wvmedia.LoadHtml(_sb.ToString());
 //BA.debugLineNum = 2913;BA.debugLine="Dim r As Reflector";
_r = new anywheresoftware.b4a.agraham.reflection.Reflection();
 //BA.debugLineNum = 2914;BA.debugLine="r.Target = wvMedia";
_r.Target = (Object)(parent.mostCurrent._wvmedia.getObject());
 //BA.debugLineNum = 2915;BA.debugLine="r.Target = r.RunMethod(\"getSettings\")";
_r.Target = _r.RunMethod("getSettings");
 //BA.debugLineNum = 2916;BA.debugLine="r.RunMethod2(\"setBuiltInZoomControls\", True, \"ja";
_r.RunMethod2("setBuiltInZoomControls",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.True),"java.lang.boolean");
 //BA.debugLineNum = 2917;BA.debugLine="r.RunMethod2(\"setDisplayZoomControls\", False, \"j";
_r.RunMethod2("setDisplayZoomControls",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.False),"java.lang.boolean");
 //BA.debugLineNum = 2919;BA.debugLine="mediaMetaData.Initialize";
parent._mediametadata._initialize /*String*/ (processBA);
 //BA.debugLineNum = 2920;BA.debugLine="mediaMetaData.ProcessMediaFile(File.DirInternal,";
parent._mediametadata._processmediafile /*boolean*/ (anywheresoftware.b4a.keywords.Common.File.getDirInternal(),_videoname);
 //BA.debugLineNum = 2921;BA.debugLine="Dim ticks As Long = (mediaMetaData.GetDuration/1";
_ticks = (long) (((double)(Double.parseDouble(parent._mediametadata._getduration /*String*/ ()))/(double)1000)*anywheresoftware.b4a.keywords.Common.DateTime.TicksPerSecond);
 //BA.debugLineNum = 2922;BA.debugLine="lblDuration.Text = ConvertTicksToTimeString(tick";
parent.mostCurrent._lblduration.setText(BA.ObjectToCharSequence(_converttickstotimestring(_ticks)));
 //BA.debugLineNum = 2924;BA.debugLine="If j.ErrorMessage.Contains(\"Media not found\") Th";
if (true) break;

case 14:
//if
this.state = 25;
if (_j._errormessage /*String*/ .contains("Media not found")) { 
this.state = 16;
}if (true) break;

case 16:
//C
this.state = 17;
 //BA.debugLineNum = 2925;BA.debugLine="clvActivity.RemoveAt(previousSelectedIndex)";
parent.mostCurrent._clvactivity._removeat(parent._previousselectedindex);
 //BA.debugLineNum = 2926;BA.debugLine="Dim list1 As List = Starter.kvs.ListKeys";
_list1 = new anywheresoftware.b4a.objects.collections.List();
_list1 = parent.mostCurrent._starter._kvs /*cloyd.smart.home.monitor.keyvaluestore*/ ._listkeys /*anywheresoftware.b4a.objects.collections.List*/ ();
 //BA.debugLineNum = 2927;BA.debugLine="For i =  0 To list1.Size-1";
if (true) break;

case 17:
//for
this.state = 24;
step38 = 1;
limit38 = (int) (_list1.getSize()-1);
_i = (int) (0) ;
this.state = 43;
if (true) break;

case 43:
//C
this.state = 24;
if ((step38 > 0 && _i <= limit38) || (step38 < 0 && _i >= limit38)) this.state = 19;
if (true) break;

case 44:
//C
this.state = 43;
_i = ((int)(0 + _i + step38)) ;
if (true) break;

case 19:
//C
this.state = 20;
 //BA.debugLineNum = 2928;BA.debugLine="Dim mytypes As Object = Starter.kvs.Get(list1.";
_mytypes = parent.mostCurrent._starter._kvs /*cloyd.smart.home.monitor.keyvaluestore*/ ._get /*Object*/ (BA.ObjectToString(_list1.Get(_i)));
 //BA.debugLineNum = 2929;BA.debugLine="Dim videos = mytypes As VideoInfo";
_videos = (cloyd.smart.home.monitor.main._videoinfo)(_mytypes);
 //BA.debugLineNum = 2930;BA.debugLine="If Link.Contains(videos.VideoID) Then";
if (true) break;

case 20:
//if
this.state = 23;
if (_link.contains(_videos.VideoID /*String*/ )) { 
this.state = 22;
}if (true) break;

case 22:
//C
this.state = 23;
 //BA.debugLineNum = 2931;BA.debugLine="Starter.kvs.Remove(list1.Get(i))";
parent.mostCurrent._starter._kvs /*cloyd.smart.home.monitor.keyvaluestore*/ ._remove /*String*/ (BA.ObjectToString(_list1.Get(_i)));
 //BA.debugLineNum = 2932;BA.debugLine="Exit";
this.state = 24;
if (true) break;
 if (true) break;

case 23:
//C
this.state = 44;
;
 if (true) break;
if (true) break;

case 24:
//C
this.state = 25;
;
 //BA.debugLineNum = 2935;BA.debugLine="ToastMessageShow(\"Media not found. Removed from";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Media not found. Removed from the list."),anywheresoftware.b4a.keywords.Common.True);
 if (true) break;

case 25:
//C
this.state = 28;
;
 //BA.debugLineNum = 2937;BA.debugLine="j.Release";
_j._release /*String*/ ();
 //BA.debugLineNum = 2940;BA.debugLine="Dim Intent1 As Intent";
_intent1 = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 2941;BA.debugLine="Intent1.Initialize(\"blink.noti.clear\", \"\")";
_intent1.Initialize("blink.noti.clear","");
 //BA.debugLineNum = 2942;BA.debugLine="Dim Phone As Phone";
_phone = new anywheresoftware.b4a.phone.Phone();
 //BA.debugLineNum = 2943;BA.debugLine="Phone.SendBroadcastIntent(Intent1)";
_phone.SendBroadcastIntent((android.content.Intent)(_intent1.getObject()));
 if (true) break;

case 27:
//C
this.state = 28;
this.catchState = 0;
 //BA.debugLineNum = 2945;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("338338624",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 if (true) break;
if (true) break;
;
 //BA.debugLineNum = 2948;BA.debugLine="Try";

case 28:
//try
this.state = 41;
this.catchState = 0;
this.catchState = 40;
this.state = 30;
if (true) break;

case 30:
//C
this.state = 31;
this.catchState = 40;
 //BA.debugLineNum = 2949;BA.debugLine="If previousSelectedIndex > (clvActivity.Size-1)";
if (true) break;

case 31:
//if
this.state = 34;
if (parent._previousselectedindex>(parent.mostCurrent._clvactivity._getsize()-1)) { 
this.state = 33;
}if (true) break;

case 33:
//C
this.state = 34;
 //BA.debugLineNum = 2950;BA.debugLine="previousSelectedIndex = 0";
parent._previousselectedindex = (int) (0);
 if (true) break;

case 34:
//C
this.state = 35;
;
 //BA.debugLineNum = 2952;BA.debugLine="Dim p As B4XView = clvActivity.GetPanel(previous";
_p = new anywheresoftware.b4a.objects.B4XViewWrapper();
_p = parent.mostCurrent._clvactivity._getpanel(parent._previousselectedindex);
 //BA.debugLineNum = 2953;BA.debugLine="If p.NumberOfViews > 0 Then";
if (true) break;

case 35:
//if
this.state = 38;
if (_p.getNumberOfViews()>0) { 
this.state = 37;
}if (true) break;

case 37:
//C
this.state = 38;
 //BA.debugLineNum = 2955;BA.debugLine="Dim ContentLabel As ImageView = p.GetView(0).Ge";
_contentlabel = new anywheresoftware.b4a.objects.ImageViewWrapper();
_contentlabel = (anywheresoftware.b4a.objects.ImageViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ImageViewWrapper(), (android.widget.ImageView)(_p.GetView((int) (0)).GetView((int) (4)).getObject()));
 //BA.debugLineNum = 2956;BA.debugLine="ContentLabel.Visible = False";
_contentlabel.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2958;BA.debugLine="Dim cd As CardData = clvActivity.GetValue(previ";
_cd = (cloyd.smart.home.monitor.main._carddata)(parent.mostCurrent._clvactivity._getvalue(parent._previousselectedindex));
 //BA.debugLineNum = 2959;BA.debugLine="cd.iswatchedvisible = False";
_cd.iswatchedvisible /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 2961;BA.debugLine="Dim ContentLabel1 As Label = p.GetView(0).GetVi";
_contentlabel1 = new anywheresoftware.b4a.objects.LabelWrapper();
_contentlabel1 = (anywheresoftware.b4a.objects.LabelWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.LabelWrapper(), (android.widget.TextView)(_p.GetView((int) (0)).GetView((int) (0)).getObject()));
 //BA.debugLineNum = 2962;BA.debugLine="ContentLabel1.Text = \"   \" & ConvertDayName(cd.";
_contentlabel1.setText(BA.ObjectToCharSequence("   "+_convertdayname(_cd.filedate /*String*/ )));
 //BA.debugLineNum = 2964;BA.debugLine="Dim ContentLabel As ImageView = p.GetView(0).Ge";
_contentlabel = new anywheresoftware.b4a.objects.ImageViewWrapper();
_contentlabel = (anywheresoftware.b4a.objects.ImageViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ImageViewWrapper(), (android.widget.ImageView)(_p.GetView((int) (0)).GetView((int) (6)).getObject()));
 //BA.debugLineNum = 2965;BA.debugLine="ContentLabel.Visible = True";
_contentlabel.setVisible(anywheresoftware.b4a.keywords.Common.True);
 if (true) break;

case 38:
//C
this.state = 41;
;
 if (true) break;

case 40:
//C
this.state = 41;
this.catchState = 0;
 //BA.debugLineNum = 2970;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("338338649",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 if (true) break;
if (true) break;

case 41:
//C
this.state = -1;
this.catchState = 0;
;
 //BA.debugLineNum = 2972;BA.debugLine="Return Null";
if (true) {
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,anywheresoftware.b4a.keywords.Common.Null);return;};
 //BA.debugLineNum = 2973;BA.debugLine="End Sub";
if (true) break;
}} 
       catch (Exception e0) {
			
if (catchState == 0)
    throw e0;
else {
    state = catchState;
processBA.setLastException(e0);}
            }
        }
    }
}
public static void  _swarmed_valuechanged(boolean _value) throws Exception{
ResumableSub_swArmed_ValueChanged rsub = new ResumableSub_swArmed_ValueChanged(null,_value);
rsub.resume(processBA, null);
}
public static class ResumableSub_swArmed_ValueChanged extends BA.ResumableSub {
public ResumableSub_swArmed_ValueChanged(cloyd.smart.home.monitor.main parent,boolean _value) {
this.parent = parent;
this._value = _value;
}
cloyd.smart.home.monitor.main parent;
boolean _value;
anywheresoftware.b4a.keywords.Common.ResumableSubWrapper _rs = null;
Object _result = null;
int _i = 0;
int step42;
int limit42;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
try {

        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 3558;BA.debugLine="Try";
if (true) break;

case 1:
//try
this.state = 36;
this.catchState = 35;
this.state = 3;
if (true) break;

case 3:
//C
this.state = 4;
this.catchState = 35;
 //BA.debugLineNum = 3559;BA.debugLine="btnSideYard.Enabled = False";
parent.mostCurrent._btnsideyard._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3560;BA.debugLine="btnRefresh.Enabled = False";
parent.mostCurrent._btnrefresh._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3561;BA.debugLine="btnSideYardNewClip.Enabled = False";
parent.mostCurrent._btnsideyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3562;BA.debugLine="btnFrontYardNewClip.Enabled = False";
parent.mostCurrent._btnfrontyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3563;BA.debugLine="btnBackyardNewClip.Enabled = False";
parent.mostCurrent._btnbackyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3564;BA.debugLine="btnSideYardRefresh.Enabled = False";
parent.mostCurrent._btnsideyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3565;BA.debugLine="btnFrontYardRefresh.Enabled = False";
parent.mostCurrent._btnfrontyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3566;BA.debugLine="btnBackyardRefresh.Enabled = False";
parent.mostCurrent._btnbackyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3567;BA.debugLine="ivSideYard.Enabled = False";
parent.mostCurrent._ivsideyard.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3568;BA.debugLine="ivFrontYard.Enabled = False";
parent.mostCurrent._ivfrontyard.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3569;BA.debugLine="ivBackyard.Enabled = False";
parent.mostCurrent._ivbackyard.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3571;BA.debugLine="ScrollViewBlink.ScrollToNow(0)";
parent.mostCurrent._scrollviewblink.ScrollToNow((int) (0));
 //BA.debugLineNum = 3573;BA.debugLine="If Value Then";
if (true) break;

case 4:
//if
this.state = 9;
if (_value) { 
this.state = 6;
}else {
this.state = 8;
}if (true) break;

case 6:
//C
this.state = 9;
 //BA.debugLineNum = 3574;BA.debugLine="lblStatus.Text = \"Arming the system...\"";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("Arming the system..."));
 //BA.debugLineNum = 3575;BA.debugLine="Dim rs As ResumableSub = RESTPost(\"https://rest";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _restpost("https://rest-"+parent._userregion+".immedia-semi.com/network/"+parent._networkid+"/arm");
 if (true) break;

case 8:
//C
this.state = 9;
 //BA.debugLineNum = 3577;BA.debugLine="lblStatus.Text = \"Disarming the system...\"";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("Disarming the system..."));
 //BA.debugLineNum = 3578;BA.debugLine="Dim rs As ResumableSub = RESTPost(\"https://rest";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _restpost("https://rest-"+parent._userregion+".immedia-semi.com/network/"+parent._networkid+"/disarm");
 if (true) break;

case 9:
//C
this.state = 10;
;
 //BA.debugLineNum = 3581;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 37;
return;
case 37:
//C
this.state = 10;
_result = (Object) result[0];
;
 //BA.debugLineNum = 3583;BA.debugLine="If response.StartsWith(\"ERROR: \") Or response.Co";
if (true) break;

case 10:
//if
this.state = 13;
if (parent._response.startsWith("ERROR: ") || parent._response.contains("System is busy, please wait") || parent._response.contains("Command not found")) { 
this.state = 12;
}if (true) break;

case 12:
//C
this.state = 13;
 //BA.debugLineNum = 3584;BA.debugLine="btnSideYard.Enabled = True";
parent.mostCurrent._btnsideyard._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3585;BA.debugLine="btnRefresh.Enabled = True";
parent.mostCurrent._btnrefresh._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3586;BA.debugLine="btnSideYardNewClip.Enabled = True";
parent.mostCurrent._btnsideyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3587;BA.debugLine="btnFrontYardNewClip.Enabled = True";
parent.mostCurrent._btnfrontyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3588;BA.debugLine="btnBackyardNewClip.Enabled = True";
parent.mostCurrent._btnbackyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3589;BA.debugLine="btnSideYardRefresh.Enabled = True";
parent.mostCurrent._btnsideyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3590;BA.debugLine="btnFrontYardRefresh.Enabled = True";
parent.mostCurrent._btnfrontyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3591;BA.debugLine="btnBackyardRefresh.Enabled = True";
parent.mostCurrent._btnbackyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3592;BA.debugLine="ivSideYard.Enabled = True";
parent.mostCurrent._ivsideyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3593;BA.debugLine="ivFrontYard.Enabled = True";
parent.mostCurrent._ivfrontyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3594;BA.debugLine="ivBackyard.Enabled = True";
parent.mostCurrent._ivbackyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3595;BA.debugLine="lblStatus.Text = GetRESTError(response)";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence(_getresterror(parent._response)));
 //BA.debugLineNum = 3596;BA.debugLine="swArmed.Value = Not(Value)";
parent.mostCurrent._swarmed._setvalue /*boolean*/ (anywheresoftware.b4a.keywords.Common.Not(_value));
 //BA.debugLineNum = 3597;BA.debugLine="Return";
if (true) return ;
 if (true) break;

case 13:
//C
this.state = 14;
;
 //BA.debugLineNum = 3599;BA.debugLine="Dim rs As ResumableSub = GetCommandID(response)";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _getcommandid(parent._response);
 //BA.debugLineNum = 3600;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 38;
return;
case 38:
//C
this.state = 14;
_result = (Object) result[0];
;
 //BA.debugLineNum = 3602;BA.debugLine="Dim rs As ResumableSub = RESTGet(\"https://rest-\"";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _restget("https://rest-"+parent._userregion+".immedia-semi.com/network/"+parent._networkid+"/command/"+parent._commandid);
 //BA.debugLineNum = 3603;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 39;
return;
case 39:
//C
this.state = 14;
_result = (Object) result[0];
;
 //BA.debugLineNum = 3605;BA.debugLine="For i = 1 To 10";
if (true) break;

case 14:
//for
this.state = 33;
step42 = 1;
limit42 = (int) (10);
_i = (int) (1) ;
this.state = 40;
if (true) break;

case 40:
//C
this.state = 33;
if ((step42 > 0 && _i <= limit42) || (step42 < 0 && _i >= limit42)) this.state = 16;
if (true) break;

case 41:
//C
this.state = 40;
_i = ((int)(0 + _i + step42)) ;
if (true) break;

case 16:
//C
this.state = 17;
 //BA.debugLineNum = 3606;BA.debugLine="Dim rs As ResumableSub = GetCommandStatus(respo";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _getcommandstatus(parent._response);
 //BA.debugLineNum = 3607;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 42;
return;
case 42:
//C
this.state = 17;
_result = (Object) result[0];
;
 //BA.debugLineNum = 3608;BA.debugLine="If commandComplete Then";
if (true) break;

case 17:
//if
this.state = 28;
if (parent._commandcomplete) { 
this.state = 19;
}else {
this.state = 21;
}if (true) break;

case 19:
//C
this.state = 28;
 //BA.debugLineNum = 3609;BA.debugLine="btnRefresh_Click";
_btnrefresh_click();
 //BA.debugLineNum = 3610;BA.debugLine="Exit";
this.state = 33;
if (true) break;
 if (true) break;

case 21:
//C
this.state = 22;
 //BA.debugLineNum = 3612;BA.debugLine="If Value Then";
if (true) break;

case 22:
//if
this.state = 27;
if (_value) { 
this.state = 24;
}else {
this.state = 26;
}if (true) break;

case 24:
//C
this.state = 27;
 //BA.debugLineNum = 3613;BA.debugLine="lblStatus.Text = \"Arming the system... \" & i";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("Arming the system... "+BA.NumberToString(_i)+"/10"));
 if (true) break;

case 26:
//C
this.state = 27;
 //BA.debugLineNum = 3615;BA.debugLine="lblStatus.Text = \"Disarming the system... \" &";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("Disarming the system... "+BA.NumberToString(_i)+"/10"));
 if (true) break;

case 27:
//C
this.state = 28;
;
 if (true) break;

case 28:
//C
this.state = 29;
;
 //BA.debugLineNum = 3618;BA.debugLine="Dim rs As ResumableSub = RESTGet(\"https://rest-";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _restget("https://rest-"+parent._userregion+".immedia-semi.com/network/"+parent._networkid+"/command/"+parent._commandid);
 //BA.debugLineNum = 3619;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 43;
return;
case 43:
//C
this.state = 29;
_result = (Object) result[0];
;
 //BA.debugLineNum = 3620;BA.debugLine="Sleep(1000)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (1000));
this.state = 44;
return;
case 44:
//C
this.state = 29;
;
 //BA.debugLineNum = 3622;BA.debugLine="If response.StartsWith(\"ERROR: \") Or response.C";
if (true) break;

case 29:
//if
this.state = 32;
if (parent._response.startsWith("ERROR: ") || parent._response.contains("System is busy, please wait") || parent._response.contains("Command not found")) { 
this.state = 31;
}if (true) break;

case 31:
//C
this.state = 32;
 //BA.debugLineNum = 3623;BA.debugLine="btnSideYard.Enabled = True";
parent.mostCurrent._btnsideyard._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3624;BA.debugLine="btnRefresh.Enabled = True";
parent.mostCurrent._btnrefresh._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3625;BA.debugLine="btnSideYardNewClip.Enabled = True";
parent.mostCurrent._btnsideyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3626;BA.debugLine="btnFrontYardNewClip.Enabled = True";
parent.mostCurrent._btnfrontyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3627;BA.debugLine="btnBackyardNewClip.Enabled = True";
parent.mostCurrent._btnbackyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3628;BA.debugLine="btnSideYardRefresh.Enabled = True";
parent.mostCurrent._btnsideyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3629;BA.debugLine="btnFrontYardRefresh.Enabled = True";
parent.mostCurrent._btnfrontyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3630;BA.debugLine="btnBackyardRefresh.Enabled = True";
parent.mostCurrent._btnbackyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3631;BA.debugLine="ivSideYard.Enabled = True";
parent.mostCurrent._ivsideyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3632;BA.debugLine="ivFrontYard.Enabled = True";
parent.mostCurrent._ivfrontyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3633;BA.debugLine="ivBackyard.Enabled = True";
parent.mostCurrent._ivbackyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3634;BA.debugLine="lblStatus.Text = GetRESTError(response)";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence(_getresterror(parent._response)));
 //BA.debugLineNum = 3635;BA.debugLine="swArmed.Value = Not(Value)";
parent.mostCurrent._swarmed._setvalue /*boolean*/ (anywheresoftware.b4a.keywords.Common.Not(_value));
 //BA.debugLineNum = 3636;BA.debugLine="Return";
if (true) return ;
 if (true) break;

case 32:
//C
this.state = 41;
;
 if (true) break;
if (true) break;

case 33:
//C
this.state = 36;
;
 //BA.debugLineNum = 3640;BA.debugLine="btnSideYard.Enabled = True";
parent.mostCurrent._btnsideyard._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3641;BA.debugLine="btnRefresh.Enabled = True";
parent.mostCurrent._btnrefresh._setenabled /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3642;BA.debugLine="btnSideYardNewClip.Enabled = True";
parent.mostCurrent._btnsideyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3643;BA.debugLine="btnFrontYardNewClip.Enabled = True";
parent.mostCurrent._btnfrontyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3644;BA.debugLine="btnBackyardNewClip.Enabled = True";
parent.mostCurrent._btnbackyardnewclip.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3645;BA.debugLine="btnSideYardRefresh.Enabled = True";
parent.mostCurrent._btnsideyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3646;BA.debugLine="btnFrontYardRefresh.Enabled = True";
parent.mostCurrent._btnfrontyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3647;BA.debugLine="btnBackyardRefresh.Enabled = True";
parent.mostCurrent._btnbackyardrefresh.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3648;BA.debugLine="ivSideYard.Enabled = True";
parent.mostCurrent._ivsideyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3649;BA.debugLine="ivFrontYard.Enabled = True";
parent.mostCurrent._ivfrontyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3650;BA.debugLine="ivBackyard.Enabled = True";
parent.mostCurrent._ivbackyard.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3651;BA.debugLine="lblStatus.Text = \"Ready\"";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("Ready"));
 if (true) break;

case 35:
//C
this.state = 36;
this.catchState = 0;
 //BA.debugLineNum = 3654;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("339256161",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 if (true) break;
if (true) break;

case 36:
//C
this.state = -1;
this.catchState = 0;
;
 //BA.debugLineNum = 3656;BA.debugLine="End Sub";
if (true) break;
}} 
       catch (Exception e0) {
			
if (catchState == 0)
    throw e0;
else {
    state = catchState;
processBA.setLastException(e0);}
            }
        }
    }
}
public static void  _tabstrip1_pageselected(int _position) throws Exception{
ResumableSub_TabStrip1_PageSelected rsub = new ResumableSub_TabStrip1_PageSelected(null,_position);
rsub.resume(processBA, null);
}
public static class ResumableSub_TabStrip1_PageSelected extends BA.ResumableSub {
public ResumableSub_TabStrip1_PageSelected(cloyd.smart.home.monitor.main parent,int _position) {
this.parent = parent;
this._position = _position;
}
cloyd.smart.home.monitor.main parent;
int _position;
int _fixedposition = 0;
int _direction = 0;
int _i = 0;
anywheresoftware.b4a.keywords.Common.ResumableSubWrapper _rs = null;
Object _result = null;
String _unwatchedvideoclips = "";
anywheresoftware.b4a.objects.B4XViewWrapper _p = null;
anywheresoftware.b4a.objects.B4XViewWrapper _backpane = null;
anywheresoftware.b4a.objects.B4XViewWrapper _contentlabel = null;
cloyd.smart.home.monitor.main._carddata _cd = null;
String _firstvideo = "";

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
try {

        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 762;BA.debugLine="Try";
if (true) break;

case 1:
//try
this.state = 97;
this.catchState = 96;
this.state = 3;
if (true) break;

case 3:
//C
this.state = 4;
this.catchState = 96;
 //BA.debugLineNum = 763;BA.debugLine="Dim FixedPosition As Int = -1";
_fixedposition = (int) (-1);
 //BA.debugLineNum = 764;BA.debugLine="If Pages.Get(Position) = False Then";
if (true) break;

case 4:
//if
this.state = 22;
if ((parent._pages.Get(_position)).equals((Object)(anywheresoftware.b4a.keywords.Common.False))) { 
this.state = 6;
}else {
this.state = 21;
}if (true) break;

case 6:
//C
this.state = 7;
 //BA.debugLineNum = 765;BA.debugLine="Dim direction As Int = Position - CurrentPage";
_direction = (int) (_position-parent._currentpage);
 //BA.debugLineNum = 766;BA.debugLine="Dim i As Int = Position + direction";
_i = (int) (_position+_direction);
 //BA.debugLineNum = 767;BA.debugLine="Do While i >= 0 And i < Pages.Size";
if (true) break;

case 7:
//do while
this.state = 14;
while (_i>=0 && _i<parent._pages.getSize()) {
this.state = 9;
if (true) break;
}
if (true) break;

case 9:
//C
this.state = 10;
 //BA.debugLineNum = 768;BA.debugLine="If Pages.Get(i) = True Then";
if (true) break;

case 10:
//if
this.state = 13;
if ((parent._pages.Get(_i)).equals((Object)(anywheresoftware.b4a.keywords.Common.True))) { 
this.state = 12;
}if (true) break;

case 12:
//C
this.state = 13;
 //BA.debugLineNum = 769;BA.debugLine="FixedPosition = i";
_fixedposition = _i;
 //BA.debugLineNum = 770;BA.debugLine="Exit";
this.state = 14;
if (true) break;
 if (true) break;

case 13:
//C
this.state = 7;
;
 //BA.debugLineNum = 772;BA.debugLine="i = i + direction";
_i = (int) (_i+_direction);
 if (true) break;
;
 //BA.debugLineNum = 774;BA.debugLine="If FixedPosition = -1 Then FixedPosition = Curr";

case 14:
//if
this.state = 19;
if (_fixedposition==-1) { 
this.state = 16;
;}if (true) break;

case 16:
//C
this.state = 19;
_fixedposition = parent._currentpage;
if (true) break;

case 19:
//C
this.state = 22;
;
 if (true) break;

case 21:
//C
this.state = 22;
 //BA.debugLineNum = 776;BA.debugLine="FixedPosition = Position";
_fixedposition = _position;
 if (true) break;

case 22:
//C
this.state = 23;
;
 //BA.debugLineNum = 778;BA.debugLine="CurrentPage = FixedPosition";
parent._currentpage = _fixedposition;
 //BA.debugLineNum = 779;BA.debugLine="TabStrip1.ScrollTo(FixedPosition, False)";
parent.mostCurrent._tabstrip1.ScrollTo(_fixedposition,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 780;BA.debugLine="If CurrentPage <> Position Then Return";
if (true) break;

case 23:
//if
this.state = 28;
if (parent._currentpage!=_position) { 
this.state = 25;
;}if (true) break;

case 25:
//C
this.state = 28;
if (true) return ;
if (true) break;

case 28:
//C
this.state = 29;
;
 //BA.debugLineNum = 782;BA.debugLine="B4XPageIndicator1.CurrentPage = Position";
parent.mostCurrent._b4xpageindicator1._setcurrentpage /*int*/ (_position);
 //BA.debugLineNum = 784;BA.debugLine="If Position > 3 Then";
if (true) break;

case 29:
//if
this.state = 34;
if (_position>3) { 
this.state = 31;
}else {
this.state = 33;
}if (true) break;

case 31:
//C
this.state = 34;
 //BA.debugLineNum = 785;BA.debugLine="btnChart.Visible = False";
parent.mostCurrent._btnchart.setVisible(anywheresoftware.b4a.keywords.Common.False);
 if (true) break;

case 33:
//C
this.state = 34;
 //BA.debugLineNum = 787;BA.debugLine="btnChart.Visible = True";
parent.mostCurrent._btnchart.setVisible(anywheresoftware.b4a.keywords.Common.True);
 if (true) break;
;
 //BA.debugLineNum = 790;BA.debugLine="If Position = 0 Then";

case 34:
//if
this.state = 41;
if (_position==0) { 
this.state = 36;
}if (true) break;

case 36:
//C
this.state = 37;
 //BA.debugLineNum = 791;BA.debugLine="If MQTT.IsInitialized And MQTT.Connected  Then";
if (true) break;

case 37:
//if
this.state = 40;
if (parent._mqtt.IsInitialized() && parent._mqtt.getConnected()) { 
this.state = 39;
}if (true) break;

case 39:
//C
this.state = 40;
 //BA.debugLineNum = 792;BA.debugLine="MQTT.Publish(\"TempHumid\", bc.StringToBytes(\"Re";
parent._mqtt.Publish("TempHumid",parent._bc.StringToBytes("Read weather","utf8"));
 if (true) break;

case 40:
//C
this.state = 41;
;
 //BA.debugLineNum = 794;BA.debugLine="CheckTempHumiditySetting";
_checktemphumiditysetting();
 if (true) break;
;
 //BA.debugLineNum = 796;BA.debugLine="If Position = 1 Then";

case 41:
//if
this.state = 48;
if (_position==1) { 
this.state = 43;
}if (true) break;

case 43:
//C
this.state = 44;
 //BA.debugLineNum = 797;BA.debugLine="If MQTT.IsInitialized And MQTT.Connected  Then";
if (true) break;

case 44:
//if
this.state = 47;
if (parent._mqtt.IsInitialized() && parent._mqtt.getConnected()) { 
this.state = 46;
}if (true) break;

case 46:
//C
this.state = 47;
 //BA.debugLineNum = 798;BA.debugLine="MQTT.Publish(\"MQ7\", bc.StringToBytes(\"Read vol";
parent._mqtt.Publish("MQ7",parent._bc.StringToBytes("Read voltage","utf8"));
 if (true) break;

case 47:
//C
this.state = 48;
;
 //BA.debugLineNum = 800;BA.debugLine="CheckAirQualitySetting";
_checkairqualitysetting();
 if (true) break;
;
 //BA.debugLineNum = 802;BA.debugLine="If Position = 2 Then";

case 48:
//if
this.state = 55;
if (_position==2) { 
this.state = 50;
}if (true) break;

case 50:
//C
this.state = 51;
 //BA.debugLineNum = 803;BA.debugLine="If MQTT.IsInitialized And MQTT.Connected  Then";
if (true) break;

case 51:
//if
this.state = 54;
if (parent._mqtt.IsInitialized() && parent._mqtt.getConnected()) { 
this.state = 53;
}if (true) break;

case 53:
//C
this.state = 54;
 //BA.debugLineNum = 804;BA.debugLine="MQTT.Publish(\"TempHumidBasement\", bc.StringToB";
parent._mqtt.Publish("TempHumidBasement",parent._bc.StringToBytes("Read weather","utf8"));
 if (true) break;

case 54:
//C
this.state = 55;
;
 //BA.debugLineNum = 806;BA.debugLine="CheckTempHumiditySettingBasement";
_checktemphumiditysettingbasement();
 if (true) break;
;
 //BA.debugLineNum = 808;BA.debugLine="If Position = 3 Then";

case 55:
//if
this.state = 62;
if (_position==3) { 
this.state = 57;
}if (true) break;

case 57:
//C
this.state = 58;
 //BA.debugLineNum = 809;BA.debugLine="If MQTT.IsInitialized And MQTT.Connected  Then";
if (true) break;

case 58:
//if
this.state = 61;
if (parent._mqtt.IsInitialized() && parent._mqtt.getConnected()) { 
this.state = 60;
}if (true) break;

case 60:
//C
this.state = 61;
 //BA.debugLineNum = 810;BA.debugLine="MQTT.Publish(\"MQ7Basement\", bc.StringToBytes(\"";
parent._mqtt.Publish("MQ7Basement",parent._bc.StringToBytes("Read voltage","utf8"));
 if (true) break;

case 61:
//C
this.state = 62;
;
 //BA.debugLineNum = 812;BA.debugLine="CheckAirQualitySettingBasement";
_checkairqualitysettingbasement();
 if (true) break;
;
 //BA.debugLineNum = 814;BA.debugLine="If Position = 4 Then";

case 62:
//if
this.state = 65;
if (_position==4) { 
this.state = 64;
}if (true) break;

case 64:
//C
this.state = 65;
 //BA.debugLineNum = 816;BA.debugLine="Dim rs As ResumableSub = CreateTab4";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _createtab4();
 //BA.debugLineNum = 817;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 98;
return;
case 98:
//C
this.state = 65;
_result = (Object) result[0];
;
 if (true) break;

case 65:
//C
this.state = 66;
;
 //BA.debugLineNum = 821;BA.debugLine="Dim rs As ResumableSub = GetUnwatchedVideos";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _getunwatchedvideos();
 //BA.debugLineNum = 822;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 99;
return;
case 99:
//C
this.state = 66;
_result = (Object) result[0];
;
 //BA.debugLineNum = 824;BA.debugLine="Dim UnwatchedVideoClips As String = StateManager";
_unwatchedvideoclips = parent.mostCurrent._statemanager._getsetting /*String*/ (mostCurrent.activityBA,"UnwatchedVideoClips");
 //BA.debugLineNum = 825;BA.debugLine="If IsNumber(UnwatchedVideoClips) Or isThereUnwat";
if (true) break;

case 66:
//if
this.state = 77;
if (anywheresoftware.b4a.keywords.Common.IsNumber(_unwatchedvideoclips) || parent._isthereunwatchedvideo) { 
this.state = 68;
}if (true) break;

case 68:
//C
this.state = 69;
 //BA.debugLineNum = 826;BA.debugLine="If UnwatchedVideoClips > 0 Or isThereUnwatchedV";
if (true) break;

case 69:
//if
this.state = 76;
if ((double)(Double.parseDouble(_unwatchedvideoclips))>0 || parent._isthereunwatchedvideo) { 
this.state = 71;
}if (true) break;

case 71:
//C
this.state = 72;
 //BA.debugLineNum = 827;BA.debugLine="If TabStrip1.CurrentPage <> 5 Then";
if (true) break;

case 72:
//if
this.state = 75;
if (parent.mostCurrent._tabstrip1.getCurrentPage()!=5) { 
this.state = 74;
}if (true) break;

case 74:
//C
this.state = 75;
 //BA.debugLineNum = 828;BA.debugLine="TabStrip1.ScrollTo(5,False)";
parent.mostCurrent._tabstrip1.ScrollTo((int) (5),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 829;BA.debugLine="Sleep(100)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (100));
this.state = 100;
return;
case 100:
//C
this.state = 75;
;
 //BA.debugLineNum = 830;BA.debugLine="Return";
if (true) return ;
 if (true) break;

case 75:
//C
this.state = 76;
;
 //BA.debugLineNum = 832;BA.debugLine="clvActivity.Clear";
parent.mostCurrent._clvactivity._clear();
 //BA.debugLineNum = 838;BA.debugLine="Dim rs As ResumableSub = GetVideos(response)";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _getvideos(parent._response);
 //BA.debugLineNum = 839;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 101;
return;
case 101:
//C
this.state = 76;
_result = (Object) result[0];
;
 //BA.debugLineNum = 842;BA.debugLine="Dim rs As ResumableSub = GetUnwatchedVideos";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _getunwatchedvideos();
 //BA.debugLineNum = 843;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 102;
return;
case 102:
//C
this.state = 76;
_result = (Object) result[0];
;
 //BA.debugLineNum = 844;BA.debugLine="Return";
if (true) return ;
 if (true) break;

case 76:
//C
this.state = 77;
;
 if (true) break;
;
 //BA.debugLineNum = 848;BA.debugLine="If Position = 5 Then";

case 77:
//if
this.state = 94;
if (_position==5) { 
this.state = 79;
}if (true) break;

case 79:
//C
this.state = 80;
 //BA.debugLineNum = 849;BA.debugLine="If clvActivity.Size > 0 Then";
if (true) break;

case 80:
//if
this.state = 93;
if (parent.mostCurrent._clvactivity._getsize()>0) { 
this.state = 82;
}else {
this.state = 92;
}if (true) break;

case 82:
//C
this.state = 83;
 //BA.debugLineNum = 850;BA.debugLine="If previousSelectedIndex > (clvActivity.Size-1";
if (true) break;

case 83:
//if
this.state = 86;
if (parent._previousselectedindex>(parent.mostCurrent._clvactivity._getsize()-1)) { 
this.state = 85;
}if (true) break;

case 85:
//C
this.state = 86;
 //BA.debugLineNum = 851;BA.debugLine="previousSelectedIndex = 0";
parent._previousselectedindex = (int) (0);
 if (true) break;

case 86:
//C
this.state = 87;
;
 //BA.debugLineNum = 853;BA.debugLine="Sleep(100)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (100));
this.state = 103;
return;
case 103:
//C
this.state = 87;
;
 //BA.debugLineNum = 854;BA.debugLine="clvActivity.JumpToItem(previousSelectedIndex)";
parent.mostCurrent._clvactivity._jumptoitem(parent._previousselectedindex);
 //BA.debugLineNum = 855;BA.debugLine="Sleep(100)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (100));
this.state = 104;
return;
case 104:
//C
this.state = 87;
;
 //BA.debugLineNum = 856;BA.debugLine="clvActivity.JumpToItem(previousSelectedIndex)";
parent.mostCurrent._clvactivity._jumptoitem(parent._previousselectedindex);
 //BA.debugLineNum = 857;BA.debugLine="Sleep(100)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (100));
this.state = 105;
return;
case 105:
//C
this.state = 87;
;
 //BA.debugLineNum = 858;BA.debugLine="Dim p As B4XView = clvActivity.GetPanel(previo";
_p = new anywheresoftware.b4a.objects.B4XViewWrapper();
_p = parent.mostCurrent._clvactivity._getpanel(parent._previousselectedindex);
 //BA.debugLineNum = 859;BA.debugLine="If p.NumberOfViews > 0 Then";
if (true) break;

case 87:
//if
this.state = 90;
if (_p.getNumberOfViews()>0) { 
this.state = 89;
}if (true) break;

case 89:
//C
this.state = 90;
 //BA.debugLineNum = 860;BA.debugLine="Dim backPane As B4XView = p.getview(0)";
_backpane = new anywheresoftware.b4a.objects.B4XViewWrapper();
_backpane = _p.GetView((int) (0));
 //BA.debugLineNum = 861;BA.debugLine="backPane.Color = xui.Color_ARGB(255,217,215,2";
_backpane.setColor(parent.mostCurrent._xui.Color_ARGB((int) (255),(int) (217),(int) (215),(int) (222)));
 //BA.debugLineNum = 863;BA.debugLine="Dim ContentLabel As B4XView = p.GetView(0).Ge";
_contentlabel = new anywheresoftware.b4a.objects.B4XViewWrapper();
_contentlabel = _p.GetView((int) (0)).GetView((int) (6));
 //BA.debugLineNum = 864;BA.debugLine="ContentLabel.Visible = True";
_contentlabel.setVisible(anywheresoftware.b4a.keywords.Common.True);
 if (true) break;

case 90:
//C
this.state = 93;
;
 //BA.debugLineNum = 866;BA.debugLine="B4XLoadingIndicator4.Show";
parent.mostCurrent._b4xloadingindicator4._show /*String*/ ();
 //BA.debugLineNum = 867;BA.debugLine="Dim cd As CardData = clvActivity.GetValue(prev";
_cd = (cloyd.smart.home.monitor.main._carddata)(parent.mostCurrent._clvactivity._getvalue(parent._previousselectedindex));
 //BA.debugLineNum = 868;BA.debugLine="Dim firstvideo As String";
_firstvideo = "";
 //BA.debugLineNum = 869;BA.debugLine="firstvideo = cd.mediaURL";
_firstvideo = _cd.mediaURL /*String*/ ;
 //BA.debugLineNum = 870;BA.debugLine="lblDuration.Text = \"0:00\"";
parent.mostCurrent._lblduration.setText(BA.ObjectToCharSequence("0:00"));
 //BA.debugLineNum = 871;BA.debugLine="ShowVideo(firstvideo)";
_showvideo(_firstvideo);
 if (true) break;

case 92:
//C
this.state = 93;
 //BA.debugLineNum = 873;BA.debugLine="clvActivity.Clear";
parent.mostCurrent._clvactivity._clear();
 //BA.debugLineNum = 879;BA.debugLine="Dim rs As ResumableSub = GetVideos(response)";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _getvideos(parent._response);
 //BA.debugLineNum = 880;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 106;
return;
case 106:
//C
this.state = 93;
_result = (Object) result[0];
;
 //BA.debugLineNum = 883;BA.debugLine="Dim rs As ResumableSub = GetUnwatchedVideos";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _getunwatchedvideos();
 //BA.debugLineNum = 884;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 107;
return;
case 107:
//C
this.state = 93;
_result = (Object) result[0];
;
 if (true) break;

case 93:
//C
this.state = 94;
;
 if (true) break;

case 94:
//C
this.state = 97;
;
 if (true) break;

case 96:
//C
this.state = 97;
this.catchState = 0;
 //BA.debugLineNum = 889;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("335586176",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 if (true) break;
if (true) break;

case 97:
//C
this.state = -1;
this.catchState = 0;
;
 //BA.debugLineNum = 891;BA.debugLine="End Sub";
if (true) break;
}} 
       catch (Exception e0) {
			
if (catchState == 0)
    throw e0;
else {
    state = catchState;
processBA.setLastException(e0);}
            }
        }
    }
}
public static String  _updateitemcolor(int _index) throws Exception{
anywheresoftware.b4a.objects.B4XViewWrapper _p = null;
anywheresoftware.b4a.objects.B4XViewWrapper _backpane = null;
anywheresoftware.b4a.objects.B4XViewWrapper _contentlabel = null;
 //BA.debugLineNum = 2701;BA.debugLine="Sub UpdateItemColor (Index As Int)";
 //BA.debugLineNum = 2702;BA.debugLine="Try";
try { //BA.debugLineNum = 2703;BA.debugLine="If previousSelectedIndex > (clvActivity.Size-1)";
if (_previousselectedindex>(mostCurrent._clvactivity._getsize()-1)) { 
 //BA.debugLineNum = 2704;BA.debugLine="previousSelectedIndex = 0";
_previousselectedindex = (int) (0);
 };
 //BA.debugLineNum = 2707;BA.debugLine="If previousSelectedIndex <> Index Then";
if (_previousselectedindex!=_index) { 
 //BA.debugLineNum = 2708;BA.debugLine="Dim p As B4XView = clvActivity.GetPanel(previou";
_p = new anywheresoftware.b4a.objects.B4XViewWrapper();
_p = mostCurrent._clvactivity._getpanel(_previousselectedindex);
 //BA.debugLineNum = 2709;BA.debugLine="If p.NumberOfViews > 0 Then";
if (_p.getNumberOfViews()>0) { 
 //BA.debugLineNum = 2710;BA.debugLine="Dim backPane As B4XView = p.getview(0)";
_backpane = new anywheresoftware.b4a.objects.B4XViewWrapper();
_backpane = _p.GetView((int) (0));
 //BA.debugLineNum = 2711;BA.debugLine="backPane.Color = xui.Color_White";
_backpane.setColor(mostCurrent._xui.Color_White);
 //BA.debugLineNum = 2713;BA.debugLine="Dim ContentLabel As B4XView = p.GetView(0).Get";
_contentlabel = new anywheresoftware.b4a.objects.B4XViewWrapper();
_contentlabel = _p.GetView((int) (0)).GetView((int) (6));
 //BA.debugLineNum = 2714;BA.debugLine="ContentLabel.Visible = False";
_contentlabel.setVisible(anywheresoftware.b4a.keywords.Common.False);
 };
 };
 //BA.debugLineNum = 2718;BA.debugLine="Dim p As B4XView = clvActivity.GetPanel(Index)";
_p = new anywheresoftware.b4a.objects.B4XViewWrapper();
_p = mostCurrent._clvactivity._getpanel(_index);
 //BA.debugLineNum = 2719;BA.debugLine="If p.NumberOfViews > 0 Then";
if (_p.getNumberOfViews()>0) { 
 //BA.debugLineNum = 2720;BA.debugLine="Dim backPane As B4XView = p.getview(0)";
_backpane = new anywheresoftware.b4a.objects.B4XViewWrapper();
_backpane = _p.GetView((int) (0));
 //BA.debugLineNum = 2721;BA.debugLine="backPane.Color = xui.Color_ARGB(255,217,215,222";
_backpane.setColor(mostCurrent._xui.Color_ARGB((int) (255),(int) (217),(int) (215),(int) (222)));
 //BA.debugLineNum = 2723;BA.debugLine="Dim ContentLabel As B4XView = p.GetView(0).GetV";
_contentlabel = new anywheresoftware.b4a.objects.B4XViewWrapper();
_contentlabel = _p.GetView((int) (0)).GetView((int) (6));
 //BA.debugLineNum = 2724;BA.debugLine="ContentLabel.Visible = True";
_contentlabel.setVisible(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 2727;BA.debugLine="previousSelectedIndex = Index";
_previousselectedindex = _index;
 } 
       catch (Exception e23) {
			processBA.setLastException(e23); //BA.debugLineNum = 2729;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("338076444",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 };
 //BA.debugLineNum = 2731;BA.debugLine="End Sub";
return "";
}
public static String  _wvmedia_pagefinished(String _url) throws Exception{
 //BA.debugLineNum = 2975;BA.debugLine="Sub wvMedia_PageFinished (Url As String)";
 //BA.debugLineNum = 2977;BA.debugLine="End Sub";
return "";
}

public boolean _onCreateOptionsMenu(android.view.Menu menu) {
    if (processBA.subExists("activity_createmenu")) {
        processBA.raiseEvent2(null, true, "activity_createmenu", false, new de.amberhome.objects.appcompat.ACMenuWrapper(menu));
        return true;
    }
    else
        return false;
}
//Sources:
//https://medium.com/@ssaurel/how-to-auto-restart-an-android-application-after-a-crash-or-a-force-close-error-1a361677c0ce
//https://stackoverflow.com/a/2903866
//https://mobikul.com/auto-restart-application-crashforce-close-android/

public void setDefaultUncaughtExceptionHandler() {
   
   Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this));
}


public class MyExceptionHandler implements Thread.UncaughtExceptionHandler {

  private Activity activity;

  public MyExceptionHandler(Activity a) {
    activity = a;
  }

  @Override
  public void uncaughtException(Thread thread, Throwable ex) {
    activity.finish();
    System.exit(2);
  }
}
}
