package cloyd.smart.home.monitor;


import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.objects.ServiceHelper;
import anywheresoftware.b4a.debug.*;

public class notificationservice extends  android.app.Service{
	public static class notificationservice_BR extends android.content.BroadcastReceiver {

		@Override
		public void onReceive(android.content.Context context, android.content.Intent intent) {
            BA.LogInfo("** Receiver (notificationservice) OnReceive **");
			android.content.Intent in = new android.content.Intent(context, notificationservice.class);
			if (intent != null)
				in.putExtra("b4a_internal_intent", intent);
            ServiceHelper.StarterHelper.startServiceFromReceiver (context, in, false, BA.class);
		}

	}
    static notificationservice mostCurrent;
	public static BA processBA;
    private ServiceHelper _service;
    public static Class<?> getObject() {
		return notificationservice.class;
	}
	@Override
	public void onCreate() {
        super.onCreate();
        mostCurrent = this;
        if (processBA == null) {
		    processBA = new BA(this, null, null, "cloyd.smart.home.monitor", "cloyd.smart.home.monitor.notificationservice");
            if (BA.isShellModeRuntimeCheck(processBA)) {
                processBA.raiseEvent2(null, true, "SHELL", false);
		    }
            try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            processBA.loadHtSubs(this.getClass());
            ServiceHelper.init();
        }
        _service = new ServiceHelper(this);
        processBA.service = this;
        
        if (BA.isShellModeRuntimeCheck(processBA)) {
			processBA.raiseEvent2(null, true, "CREATE", true, "cloyd.smart.home.monitor.notificationservice", processBA, _service, anywheresoftware.b4a.keywords.Common.Density);
		}
        if (!false && ServiceHelper.StarterHelper.startFromServiceCreate(processBA, false) == false) {
				
		}
		else {
            processBA.setActivityPaused(false);
            BA.LogInfo("*** Service (notificationservice) Create ***");
            processBA.raiseEvent(null, "service_create");
        }
        processBA.runHook("oncreate", this, null);
        if (false) {
			ServiceHelper.StarterHelper.runWaitForLayouts();
		}
    }
		@Override
	public void onStart(android.content.Intent intent, int startId) {
		onStartCommand(intent, 0, 0);
    }
    @Override
    public int onStartCommand(final android.content.Intent intent, int flags, int startId) {
    	if (ServiceHelper.StarterHelper.onStartCommand(processBA, new Runnable() {
            public void run() {
                handleStart(intent);
            }}))
			;
		else {
			ServiceHelper.StarterHelper.addWaitForLayout (new Runnable() {
				public void run() {
                    processBA.setActivityPaused(false);
                    BA.LogInfo("** Service (notificationservice) Create **");
                    processBA.raiseEvent(null, "service_create");
					handleStart(intent);
                    ServiceHelper.StarterHelper.removeWaitForLayout();
				}
			});
		}
        processBA.runHook("onstartcommand", this, new Object[] {intent, flags, startId});
		return android.app.Service.START_NOT_STICKY;
    }
    public void onTaskRemoved(android.content.Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        if (false)
            processBA.raiseEvent(null, "service_taskremoved");
            
    }
    private void handleStart(android.content.Intent intent) {
    	BA.LogInfo("** Service (notificationservice) Start **");
    	java.lang.reflect.Method startEvent = processBA.htSubs.get("service_start");
    	if (startEvent != null) {
    		if (startEvent.getParameterTypes().length > 0) {
    			anywheresoftware.b4a.objects.IntentWrapper iw = ServiceHelper.StarterHelper.handleStartIntent(intent, _service, processBA);
    			processBA.raiseEvent(null, "service_start", iw);
    		}
    		else {
    			processBA.raiseEvent(null, "service_start");
    		}
    	}
    }
	
	@Override
	public void onDestroy() {
        super.onDestroy();
        if (false) {
            BA.LogInfo("** Service (notificationservice) Destroy (ignored)**");
        }
        else {
            BA.LogInfo("** Service (notificationservice) Destroy **");
		    processBA.raiseEvent(null, "service_destroy");
            processBA.service = null;
		    mostCurrent = null;
		    processBA.setActivityPaused(true);
            processBA.runHook("ondestroy", this, null);
        }
	}

@Override
	public android.os.IBinder onBind(android.content.Intent intent) {
		return null;
	}public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.objects.NotificationListenerWrapper.NotificationListener _listener = null;
public static anywheresoftware.b4a.objects.Timer _timer = null;
public static String _response = "";
public static String _authtoken = "";
public static String _userregion = "";
public static String _emailaddress = "";
public static String _password = "";
public static String _twoclientfaverificationrequired = "";
public b4a.example.dateutils _dateutils = null;
public cloyd.smart.home.monitor.main _main = null;
public cloyd.smart.home.monitor.smarthomemonitor _smarthomemonitor = null;
public cloyd.smart.home.monitor.statemanager _statemanager = null;
public cloyd.smart.home.monitor.starter _starter = null;
public cloyd.smart.home.monitor.httputils2service _httputils2service = null;
public cloyd.smart.home.monitor.b4xcollections _b4xcollections = null;
public static String  _getauthinfo(String _json) throws Exception{
anywheresoftware.b4a.objects.collections.JSONParser _parser = null;
anywheresoftware.b4a.objects.collections.Map _root = null;
String _force_password_reset = "";
int _lockout_time_remaining = 0;
anywheresoftware.b4a.objects.collections.Map _authtokenmap = null;
String _message = "";
anywheresoftware.b4a.objects.collections.Map _client = null;
int _allow_pin_resend_seconds = 0;
anywheresoftware.b4a.objects.collections.Map _region = null;
String _code = "";
String _description = "";
anywheresoftware.b4a.objects.collections.Map _account = null;
String _verification_required = "";
int _id = 0;
 //BA.debugLineNum = 268;BA.debugLine="Sub GetAuthInfo(json As String)";
 //BA.debugLineNum = 269;BA.debugLine="Try";
try { //BA.debugLineNum = 270;BA.debugLine="Dim parser As JSONParser";
_parser = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 271;BA.debugLine="parser.Initialize(json)";
_parser.Initialize(_json);
 //BA.debugLineNum = 272;BA.debugLine="Dim root As Map = parser.NextObject";
_root = new anywheresoftware.b4a.objects.collections.Map();
_root = _parser.NextObject();
 //BA.debugLineNum = 273;BA.debugLine="Dim force_password_reset As String = root.Get(\"f";
_force_password_reset = BA.ObjectToString(_root.Get((Object)("force_password_reset")));
 //BA.debugLineNum = 274;BA.debugLine="Dim lockout_time_remaining As Int = root.Get(\"lo";
_lockout_time_remaining = (int)(BA.ObjectToNumber(_root.Get((Object)("lockout_time_remaining"))));
 //BA.debugLineNum = 275;BA.debugLine="Dim authtokenmap As Map = root.Get(\"authtoken\")";
_authtokenmap = new anywheresoftware.b4a.objects.collections.Map();
_authtokenmap = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (anywheresoftware.b4a.objects.collections.Map.MyMap)(_root.Get((Object)("authtoken"))));
 //BA.debugLineNum = 276;BA.debugLine="authToken = authtokenmap.Get(\"authtoken\")";
_authtoken = BA.ObjectToString(_authtokenmap.Get((Object)("authtoken")));
 //BA.debugLineNum = 277;BA.debugLine="Dim message As String = authtokenmap.Get(\"messag";
_message = BA.ObjectToString(_authtokenmap.Get((Object)("message")));
 //BA.debugLineNum = 278;BA.debugLine="Dim client As Map = root.Get(\"client\")";
_client = new anywheresoftware.b4a.objects.collections.Map();
_client = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (anywheresoftware.b4a.objects.collections.Map.MyMap)(_root.Get((Object)("client"))));
 //BA.debugLineNum = 279;BA.debugLine="TwoClientFAVerificationRequired = client.Get(\"ve";
_twoclientfaverificationrequired = BA.ObjectToString(_client.Get((Object)("verification_required")));
 //BA.debugLineNum = 280;BA.debugLine="Dim allow_pin_resend_seconds As Int = root.Get(\"";
_allow_pin_resend_seconds = (int)(BA.ObjectToNumber(_root.Get((Object)("allow_pin_resend_seconds"))));
 //BA.debugLineNum = 281;BA.debugLine="Dim region As Map = root.Get(\"region\")";
_region = new anywheresoftware.b4a.objects.collections.Map();
_region = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (anywheresoftware.b4a.objects.collections.Map.MyMap)(_root.Get((Object)("region"))));
 //BA.debugLineNum = 282;BA.debugLine="Dim code As String = region.Get(\"code\") 'ignore";
_code = BA.ObjectToString(_region.Get((Object)("code")));
 //BA.debugLineNum = 283;BA.debugLine="userRegion = region.Get(\"tier\")";
_userregion = BA.ObjectToString(_region.Get((Object)("tier")));
 //BA.debugLineNum = 284;BA.debugLine="Dim description As String = region.Get(\"descript";
_description = BA.ObjectToString(_region.Get((Object)("description")));
 //BA.debugLineNum = 285;BA.debugLine="Dim account As Map = root.Get(\"account\")";
_account = new anywheresoftware.b4a.objects.collections.Map();
_account = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (anywheresoftware.b4a.objects.collections.Map.MyMap)(_root.Get((Object)("account"))));
 //BA.debugLineNum = 286;BA.debugLine="Dim verification_required As String = account.Ge";
_verification_required = BA.ObjectToString(_account.Get((Object)("verification_required")));
 //BA.debugLineNum = 287;BA.debugLine="Dim id As Int = account.Get(\"id\") 'ignore";
_id = (int)(BA.ObjectToNumber(_account.Get((Object)("id"))));
 } 
       catch (Exception e21) {
			processBA.setLastException(e21); //BA.debugLineNum = 289;BA.debugLine="response = \"ERROR: GetAuthInfo - \" & LastExcepti";
_response = "ERROR: GetAuthInfo - "+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(processBA));
 //BA.debugLineNum = 290;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("83014678",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(processBA)),0);
 };
 //BA.debugLineNum = 293;BA.debugLine="End Sub";
return "";
}
public static anywheresoftware.b4a.keywords.Common.ResumableSubWrapper  _getunwatchedvideosservice() throws Exception{
ResumableSub_GetUnwatchedVideosService rsub = new ResumableSub_GetUnwatchedVideosService(null);
rsub.resume(processBA, null);
return (anywheresoftware.b4a.keywords.Common.ResumableSubWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper(), rsub);
}
public static class ResumableSub_GetUnwatchedVideosService extends BA.ResumableSub {
public ResumableSub_GetUnwatchedVideosService(cloyd.smart.home.monitor.notificationservice parent) {
this.parent = parent;
}
cloyd.smart.home.monitor.notificationservice parent;
anywheresoftware.b4a.keywords.Common.ResumableSubWrapper _rs = null;
Object _result = null;
int _unwatchedvideocount = 0;
anywheresoftware.b4a.objects.collections.JSONParser _parser = null;
anywheresoftware.b4a.objects.collections.Map _root = null;
anywheresoftware.b4a.objects.collections.List _media = null;
long _n = 0L;
anywheresoftware.b4a.objects.collections.Map _colmedia = null;
String _watched = "";
anywheresoftware.b4a.objects.IntentWrapper _intent1 = null;
anywheresoftware.b4a.phone.Phone _phone = null;
anywheresoftware.b4a.BA.IterableList group11;
int index11;
int groupLen11;

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
 //BA.debugLineNum = 150;BA.debugLine="Try";
if (true) break;

case 1:
//try
this.state = 26;
this.catchState = 25;
this.state = 3;
if (true) break;

case 3:
//C
this.state = 4;
this.catchState = 25;
 //BA.debugLineNum = 155;BA.debugLine="Dim rs As ResumableSub = RESTGet(\"https://rest-\"";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _restget("https://rest-"+parent._userregion+".immedia-semi.com/api/v1/accounts/88438/media/changed?since=-999999999-01-01T00:00:00+18:00&page=1");
 //BA.debugLineNum = 156;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 27;
return;
case 27:
//C
this.state = 4;
_result = (Object) result[0];
;
 //BA.debugLineNum = 158;BA.debugLine="If response.Contains(\"ERROR\") Or response.Contai";
if (true) break;

case 4:
//if
this.state = 9;
if (parent._response.contains("ERROR") || parent._response.contains("System is busy, please wait")) { 
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
 //BA.debugLineNum = 160;BA.debugLine="Dim unwatchedVideoCount As Int = 0";
_unwatchedvideocount = (int) (0);
 //BA.debugLineNum = 161;BA.debugLine="Dim parser As JSONParser";
_parser = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 162;BA.debugLine="parser.Initialize(response)";
_parser.Initialize(parent._response);
 //BA.debugLineNum = 163;BA.debugLine="Dim root As Map = parser.NextObject";
_root = new anywheresoftware.b4a.objects.collections.Map();
_root = _parser.NextObject();
 //BA.debugLineNum = 164;BA.debugLine="Dim media As List = root.Get(\"media\")";
_media = new anywheresoftware.b4a.objects.collections.List();
_media = (anywheresoftware.b4a.objects.collections.List) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.List(), (java.util.List)(_root.Get((Object)("media"))));
 //BA.debugLineNum = 166;BA.debugLine="Dim n As Long = DateTime.Now";
_n = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 167;BA.debugLine="For Each colmedia As Map In media";
if (true) break;

case 10:
//for
this.state = 17;
_colmedia = new anywheresoftware.b4a.objects.collections.Map();
group11 = _media;
index11 = 0;
groupLen11 = group11.getSize();
this.state = 28;
if (true) break;

case 28:
//C
this.state = 17;
if (index11 < groupLen11) {
this.state = 12;
_colmedia = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (anywheresoftware.b4a.objects.collections.Map.MyMap)(group11.Get(index11)));}
if (true) break;

case 29:
//C
this.state = 28;
index11++;
if (true) break;

case 12:
//C
this.state = 13;
 //BA.debugLineNum = 168;BA.debugLine="Dim watched As String = colmedia.Get(\"watched\")";
_watched = BA.ObjectToString(_colmedia.Get((Object)("watched")));
 //BA.debugLineNum = 169;BA.debugLine="If watched = False Then";
if (true) break;

case 13:
//if
this.state = 16;
if ((_watched).equals(BA.ObjectToString(anywheresoftware.b4a.keywords.Common.False))) { 
this.state = 15;
}if (true) break;

case 15:
//C
this.state = 16;
 //BA.debugLineNum = 170;BA.debugLine="unwatchedVideoCount = unwatchedVideoCount + 1";
_unwatchedvideocount = (int) (_unwatchedvideocount+1);
 if (true) break;

case 16:
//C
this.state = 29;
;
 if (true) break;
if (true) break;

case 17:
//C
this.state = 18;
;
 //BA.debugLineNum = 173;BA.debugLine="Log(\"From Service: Loading unwatched videos took";
anywheresoftware.b4a.keywords.Common.LogImpl("82752536","From Service: Loading unwatched videos took: "+BA.NumberToString((anywheresoftware.b4a.keywords.Common.DateTime.getNow()-_n))+"ms",0);
 //BA.debugLineNum = 174;BA.debugLine="StateManager.SetSetting(\"UnwatchedVideoClips\",un";
parent.mostCurrent._statemanager._setsetting /*String*/ (processBA,"UnwatchedVideoClips",BA.NumberToString(_unwatchedvideocount));
 //BA.debugLineNum = 175;BA.debugLine="StateManager.SaveSettings";
parent.mostCurrent._statemanager._savesettings /*String*/ (processBA);
 //BA.debugLineNum = 179;BA.debugLine="If unwatchedVideoCount > 0 Then";
if (true) break;

case 18:
//if
this.state = 23;
if (_unwatchedvideocount>0) { 
this.state = 20;
}else {
this.state = 22;
}if (true) break;

case 20:
//C
this.state = 23;
 //BA.debugLineNum = 180;BA.debugLine="Dim Intent1 As Intent";
_intent1 = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 181;BA.debugLine="Intent1.Initialize(\"blink.video.add\", \"\")";
_intent1.Initialize("blink.video.add","");
 //BA.debugLineNum = 182;BA.debugLine="Dim Phone As Phone";
_phone = new anywheresoftware.b4a.phone.Phone();
 //BA.debugLineNum = 183;BA.debugLine="Phone.SendBroadcastIntent(Intent1)";
_phone.SendBroadcastIntent((android.content.Intent)(_intent1.getObject()));
 //BA.debugLineNum = 184;BA.debugLine="timer.Enabled = False";
parent._timer.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 if (true) break;

case 22:
//C
this.state = 23;
 if (true) break;

case 23:
//C
this.state = 26;
;
 if (true) break;

case 25:
//C
this.state = 26;
this.catchState = 0;
 //BA.debugLineNum = 189;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("82752552",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(processBA)),0);
 if (true) break;
if (true) break;

case 26:
//C
this.state = -1;
this.catchState = 0;
;
 //BA.debugLineNum = 191;BA.debugLine="Return Null";
if (true) {
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,anywheresoftware.b4a.keywords.Common.Null);return;};
 //BA.debugLineNum = 192;BA.debugLine="End Sub";
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
public static void  _listener_notificationposted(anywheresoftware.b4a.objects.NotificationListenerWrapper.StatusBarNotificationWrapper _sbn) throws Exception{
ResumableSub_Listener_NotificationPosted rsub = new ResumableSub_Listener_NotificationPosted(null,_sbn);
rsub.resume(processBA, null);
}
public static class ResumableSub_Listener_NotificationPosted extends BA.ResumableSub {
public ResumableSub_Listener_NotificationPosted(cloyd.smart.home.monitor.notificationservice parent,anywheresoftware.b4a.objects.NotificationListenerWrapper.StatusBarNotificationWrapper _sbn) {
this.parent = parent;
this._sbn = _sbn;
}
cloyd.smart.home.monitor.notificationservice parent;
anywheresoftware.b4a.objects.NotificationListenerWrapper.StatusBarNotificationWrapper _sbn;
anywheresoftware.b4a.phone.Phone _p = null;
anywheresoftware.b4j.object.JavaObject _jno = null;
anywheresoftware.b4j.object.JavaObject _extras = null;
anywheresoftware.b4a.objects.collections.List _list1 = null;
int _i = 0;
anywheresoftware.b4a.keywords.Common.ResumableSubWrapper _rs = null;
Object _result = null;
int step30;
int limit30;
int step43;
int limit43;

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
 //BA.debugLineNum = 26;BA.debugLine="Try";
if (true) break;

case 1:
//try
this.state = 74;
this.catchState = 73;
this.state = 3;
if (true) break;

case 3:
//C
this.state = 4;
this.catchState = 73;
 //BA.debugLineNum = 28;BA.debugLine="Dim p As Phone";
_p = new anywheresoftware.b4a.phone.Phone();
 //BA.debugLineNum = 29;BA.debugLine="If p.SdkVersion >= 19 Then";
if (true) break;

case 4:
//if
this.state = 71;
if (_p.getSdkVersion()>=19) { 
this.state = 6;
}if (true) break;

case 6:
//C
this.state = 7;
 //BA.debugLineNum = 30;BA.debugLine="Dim jno As JavaObject = SBN.Notification";
_jno = new anywheresoftware.b4j.object.JavaObject();
_jno = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(_sbn.getNotification().getObject()));
 //BA.debugLineNum = 31;BA.debugLine="Dim extras As JavaObject = jno.GetField(\"extras";
_extras = new anywheresoftware.b4j.object.JavaObject();
_extras = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(_jno.GetField("extras")));
 //BA.debugLineNum = 32;BA.debugLine="extras.RunMethod(\"size\", Null)";
_extras.RunMethod("size",(Object[])(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 33;BA.debugLine="If SBN.PackageName = \"cloyd.smart.home.monitor\"";
if (true) break;

case 7:
//if
this.state = 70;
if ((_sbn.getPackageName()).equals("cloyd.smart.home.monitor")) { 
this.state = 9;
}else if((_sbn.getPackageName()).equals("com.immediasemi.android.blink")) { 
this.state = 29;
}if (true) break;

case 9:
//C
this.state = 10;
 //BA.debugLineNum = 34;BA.debugLine="If SBN.Id = 726 Then";
if (true) break;

case 10:
//if
this.state = 27;
if (_sbn.getId()==726) { 
this.state = 12;
}else if(_sbn.getId()==725) { 
this.state = 14;
}else if(_sbn.getId()==727) { 
this.state = 16;
}else if(_sbn.getId()==728) { 
this.state = 18;
}else if(_sbn.getId()==730) { 
this.state = 20;
}else if(_sbn.getId()==729) { 
this.state = 22;
}else if(_sbn.getId()==731) { 
this.state = 24;
}else if(_sbn.getId()==732) { 
this.state = 26;
}if (true) break;

case 12:
//C
this.state = 27;
 //BA.debugLineNum = 35;BA.debugLine="SmartHomeMonitor.IsAirQualityNotificationOnGo";
parent.mostCurrent._smarthomemonitor._isairqualitynotificationongoing /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
 if (true) break;

case 14:
//C
this.state = 27;
 //BA.debugLineNum = 37;BA.debugLine="SmartHomeMonitor.IsTempHumidityNotificationOn";
parent.mostCurrent._smarthomemonitor._istemphumiditynotificationongoing /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
 if (true) break;

case 16:
//C
this.state = 27;
 //BA.debugLineNum = 39;BA.debugLine="SmartHomeMonitor.IsAirQualityNotificationOnGo";
parent.mostCurrent._smarthomemonitor._isairqualitynotificationongoingbasement /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
 if (true) break;

case 18:
//C
this.state = 27;
 //BA.debugLineNum = 41;BA.debugLine="SmartHomeMonitor.IsTempHumidityNotificationOn";
parent.mostCurrent._smarthomemonitor._istemphumiditynotificationongoingbasement /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
 if (true) break;

case 20:
//C
this.state = 27;
 //BA.debugLineNum = 43;BA.debugLine="SmartHomeMonitor.IsOldTempHumidityNotificatio";
parent.mostCurrent._smarthomemonitor._isoldtemphumiditynotificationongoingbasement /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
 if (true) break;

case 22:
//C
this.state = 27;
 //BA.debugLineNum = 45;BA.debugLine="SmartHomeMonitor.IsOldTempHumidityNotificatio";
parent.mostCurrent._smarthomemonitor._isoldtemphumiditynotificationongoing /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
 if (true) break;

case 24:
//C
this.state = 27;
 //BA.debugLineNum = 47;BA.debugLine="SmartHomeMonitor.IsOldAirQualityNotificationO";
parent.mostCurrent._smarthomemonitor._isoldairqualitynotificationongoing /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
 if (true) break;

case 26:
//C
this.state = 27;
 //BA.debugLineNum = 49;BA.debugLine="SmartHomeMonitor.IsOldAirQualityNotificationO";
parent.mostCurrent._smarthomemonitor._isoldairqualitynotificationongoingbasement /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
 if (true) break;

case 27:
//C
this.state = 70;
;
 if (true) break;

case 29:
//C
this.state = 30;
 //BA.debugLineNum = 52;BA.debugLine="If File.Exists(File.DirInternal, \"account.txt\"";
if (true) break;

case 30:
//if
this.state = 63;
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"account.txt")) { 
this.state = 32;
}else if(anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),"account.txt")) { 
this.state = 44;
}else {
this.state = 62;
}if (true) break;

case 32:
//C
this.state = 33;
 //BA.debugLineNum = 53;BA.debugLine="Dim List1 As List";
_list1 = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 54;BA.debugLine="List1.Initialize";
_list1.Initialize();
 //BA.debugLineNum = 55;BA.debugLine="List1 = File.ReadList(File.DirInternal, \"acco";
_list1 = anywheresoftware.b4a.keywords.Common.File.ReadList(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"account.txt");
 //BA.debugLineNum = 56;BA.debugLine="For i = 0 To List1.Size - 1";
if (true) break;

case 33:
//for
this.state = 42;
step30 = 1;
limit30 = (int) (_list1.getSize()-1);
_i = (int) (0) ;
this.state = 75;
if (true) break;

case 75:
//C
this.state = 42;
if ((step30 > 0 && _i <= limit30) || (step30 < 0 && _i >= limit30)) this.state = 35;
if (true) break;

case 76:
//C
this.state = 75;
_i = ((int)(0 + _i + step30)) ;
if (true) break;

case 35:
//C
this.state = 36;
 //BA.debugLineNum = 57;BA.debugLine="If i = 0 Then";
if (true) break;

case 36:
//if
this.state = 41;
if (_i==0) { 
this.state = 38;
}else if(_i==1) { 
this.state = 40;
}if (true) break;

case 38:
//C
this.state = 41;
 //BA.debugLineNum = 58;BA.debugLine="emailAddress = List1.Get(i)";
parent._emailaddress = BA.ObjectToString(_list1.Get(_i));
 if (true) break;

case 40:
//C
this.state = 41;
 //BA.debugLineNum = 60;BA.debugLine="password = List1.Get(i)";
parent._password = BA.ObjectToString(_list1.Get(_i));
 if (true) break;

case 41:
//C
this.state = 76;
;
 if (true) break;
if (true) break;

case 42:
//C
this.state = 63;
;
 if (true) break;

case 44:
//C
this.state = 45;
 //BA.debugLineNum = 64;BA.debugLine="File.Copy(File.DirRootExternal,\"account.txt\",";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),"account.txt",anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"account.txt");
 //BA.debugLineNum = 65;BA.debugLine="If File.Exists(File.DirInternal, \"account.txt";
if (true) break;

case 45:
//if
this.state = 60;
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"account.txt")) { 
this.state = 47;
}else {
this.state = 59;
}if (true) break;

case 47:
//C
this.state = 48;
 //BA.debugLineNum = 66;BA.debugLine="Dim List1 As List";
_list1 = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 67;BA.debugLine="List1.Initialize";
_list1.Initialize();
 //BA.debugLineNum = 68;BA.debugLine="List1 = File.ReadList(File.DirInternal, \"acc";
_list1 = anywheresoftware.b4a.keywords.Common.File.ReadList(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"account.txt");
 //BA.debugLineNum = 69;BA.debugLine="For i = 0 To List1.Size - 1";
if (true) break;

case 48:
//for
this.state = 57;
step43 = 1;
limit43 = (int) (_list1.getSize()-1);
_i = (int) (0) ;
this.state = 77;
if (true) break;

case 77:
//C
this.state = 57;
if ((step43 > 0 && _i <= limit43) || (step43 < 0 && _i >= limit43)) this.state = 50;
if (true) break;

case 78:
//C
this.state = 77;
_i = ((int)(0 + _i + step43)) ;
if (true) break;

case 50:
//C
this.state = 51;
 //BA.debugLineNum = 70;BA.debugLine="If i = 0 Then";
if (true) break;

case 51:
//if
this.state = 56;
if (_i==0) { 
this.state = 53;
}else if(_i==1) { 
this.state = 55;
}if (true) break;

case 53:
//C
this.state = 56;
 //BA.debugLineNum = 71;BA.debugLine="emailAddress = List1.Get(i)";
parent._emailaddress = BA.ObjectToString(_list1.Get(_i));
 if (true) break;

case 55:
//C
this.state = 56;
 //BA.debugLineNum = 73;BA.debugLine="password = List1.Get(i)";
parent._password = BA.ObjectToString(_list1.Get(_i));
 if (true) break;

case 56:
//C
this.state = 78;
;
 if (true) break;
if (true) break;

case 57:
//C
this.state = 60;
;
 if (true) break;

case 59:
//C
this.state = 60;
 //BA.debugLineNum = 77;BA.debugLine="Return";
if (true) return ;
 if (true) break;

case 60:
//C
this.state = 63;
;
 if (true) break;

case 62:
//C
this.state = 63;
 //BA.debugLineNum = 80;BA.debugLine="timer.Enabled = False";
parent._timer.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 81;BA.debugLine="Return";
if (true) return ;
 if (true) break;

case 63:
//C
this.state = 64;
;
 //BA.debugLineNum = 84;BA.debugLine="Dim rs As ResumableSub = RequestAuthToken";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _requestauthtoken();
 //BA.debugLineNum = 85;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 79;
return;
case 79:
//C
this.state = 64;
_result = (Object) result[0];
;
 //BA.debugLineNum = 87;BA.debugLine="If response.Contains(\"ERROR\") Then Return";
if (true) break;

case 64:
//if
this.state = 69;
if (parent._response.contains("ERROR")) { 
this.state = 66;
;}if (true) break;

case 66:
//C
this.state = 69;
if (true) return ;
if (true) break;

case 69:
//C
this.state = 70;
;
 //BA.debugLineNum = 89;BA.debugLine="timer.Initialize(\"SDTime\", 1500)  '1000 Millis";
parent._timer.Initialize(processBA,"SDTime",(long) (1500));
 //BA.debugLineNum = 90;BA.debugLine="timer.Enabled = True";
parent._timer.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 if (true) break;

case 70:
//C
this.state = 71;
;
 if (true) break;

case 71:
//C
this.state = 74;
;
 if (true) break;

case 73:
//C
this.state = 74;
this.catchState = 0;
 //BA.debugLineNum = 94;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("82490437",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(processBA)),0);
 if (true) break;
if (true) break;

case 74:
//C
this.state = -1;
this.catchState = 0;
;
 //BA.debugLineNum = 97;BA.debugLine="End Sub";
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
public static String  _listener_notificationremoved(anywheresoftware.b4a.objects.NotificationListenerWrapper.StatusBarNotificationWrapper _sbn) throws Exception{
anywheresoftware.b4a.objects.IntentWrapper _intent1 = null;
anywheresoftware.b4a.phone.Phone _phone = null;
 //BA.debugLineNum = 99;BA.debugLine="Sub Listener_NotificationRemoved (SBN As StatusBar";
 //BA.debugLineNum = 100;BA.debugLine="Try";
try { //BA.debugLineNum = 102;BA.debugLine="If SBN.PackageName = \"cloyd.smart.home.monitor\"";
if ((_sbn.getPackageName()).equals("cloyd.smart.home.monitor")) { 
 //BA.debugLineNum = 103;BA.debugLine="If SBN.Id = 726 Then";
if (_sbn.getId()==726) { 
 //BA.debugLineNum = 104;BA.debugLine="SmartHomeMonitor.IsAirQualityNotificationOnGoi";
mostCurrent._smarthomemonitor._isairqualitynotificationongoing /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
 }else if(_sbn.getId()==725) { 
 //BA.debugLineNum = 106;BA.debugLine="SmartHomeMonitor.lngTicks = DateTime.now";
mostCurrent._smarthomemonitor._lngticks /*long*/  = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 107;BA.debugLine="SmartHomeMonitor.lngTicksTempHumid = DateTime.";
mostCurrent._smarthomemonitor._lngtickstemphumid /*long*/  = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 108;BA.debugLine="SmartHomeMonitor.IsTempHumidityNotificationOnG";
mostCurrent._smarthomemonitor._istemphumiditynotificationongoing /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
 }else if(_sbn.getId()==727) { 
 //BA.debugLineNum = 110;BA.debugLine="SmartHomeMonitor.IsAirQualityNotificationOnGoi";
mostCurrent._smarthomemonitor._isairqualitynotificationongoingbasement /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
 }else if(_sbn.getId()==728) { 
 //BA.debugLineNum = 112;BA.debugLine="SmartHomeMonitor.lngTicksTempHumidBasement = D";
mostCurrent._smarthomemonitor._lngtickstemphumidbasement /*long*/  = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 113;BA.debugLine="SmartHomeMonitor.IsTempHumidityNotificationOnG";
mostCurrent._smarthomemonitor._istemphumiditynotificationongoingbasement /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
 }else if(_sbn.getId()==730) { 
 //BA.debugLineNum = 115;BA.debugLine="SmartHomeMonitor.IsOldTempHumidityNotification";
mostCurrent._smarthomemonitor._isoldtemphumiditynotificationongoingbasement /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
 }else if(_sbn.getId()==729) { 
 //BA.debugLineNum = 117;BA.debugLine="SmartHomeMonitor.IsOldTempHumidityNotification";
mostCurrent._smarthomemonitor._isoldtemphumiditynotificationongoing /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
 }else if(_sbn.getId()==731) { 
 //BA.debugLineNum = 119;BA.debugLine="SmartHomeMonitor.IsOldAirQualityNotificationOn";
mostCurrent._smarthomemonitor._isoldairqualitynotificationongoing /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
 }else if(_sbn.getId()==732) { 
 //BA.debugLineNum = 121;BA.debugLine="SmartHomeMonitor.IsOldAirQualityNotificationOn";
mostCurrent._smarthomemonitor._isoldairqualitynotificationongoingbasement /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
 };
 }else if((_sbn.getPackageName()).equals("com.immediasemi.android.blink")) { 
 //BA.debugLineNum = 124;BA.debugLine="timer.Enabled = False";
_timer.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 125;BA.debugLine="Dim Intent1 As Intent";
_intent1 = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 126;BA.debugLine="Intent1.Initialize(\"blink.video.remove\", \"\")";
_intent1.Initialize("blink.video.remove","");
 //BA.debugLineNum = 127;BA.debugLine="Dim Phone As Phone";
_phone = new anywheresoftware.b4a.phone.Phone();
 //BA.debugLineNum = 128;BA.debugLine="Phone.SendBroadcastIntent(Intent1)";
_phone.SendBroadcastIntent((android.content.Intent)(_intent1.getObject()));
 };
 } 
       catch (Exception e31) {
			processBA.setLastException(e31); //BA.debugLineNum = 131;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("82555936",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(processBA)),0);
 };
 //BA.debugLineNum = 134;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 7;BA.debugLine="Private listener As NotificationListener";
_listener = new anywheresoftware.b4a.objects.NotificationListenerWrapper.NotificationListener();
 //BA.debugLineNum = 8;BA.debugLine="Private timer As Timer";
_timer = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 9;BA.debugLine="Private response As String";
_response = "";
 //BA.debugLineNum = 10;BA.debugLine="Private authToken As String";
_authtoken = "";
 //BA.debugLineNum = 11;BA.debugLine="Private userRegion As String = \"u006\"";
_userregion = "u006";
 //BA.debugLineNum = 12;BA.debugLine="Private emailAddress As String";
_emailaddress = "";
 //BA.debugLineNum = 13;BA.debugLine="Private password As String";
_password = "";
 //BA.debugLineNum = 14;BA.debugLine="Private TwoClientFAVerificationRequired As String";
_twoclientfaverificationrequired = "";
 //BA.debugLineNum = 15;BA.debugLine="End Sub";
return "";
}
public static String  _randomstring(int _length) throws Exception{
String _abc = "";
String _randomstr = "";
int _i = 0;
 //BA.debugLineNum = 259;BA.debugLine="Sub RandomString(length As Int) As String";
 //BA.debugLineNum = 260;BA.debugLine="Dim abc As String = \"0123456789ABCDEFGHIJKLMNOPQR";
_abc = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
 //BA.debugLineNum = 261;BA.debugLine="Dim randomstr As String = \"\"";
_randomstr = "";
 //BA.debugLineNum = 262;BA.debugLine="For i = 0 To length - 1";
{
final int step3 = 1;
final int limit3 = (int) (_length-1);
_i = (int) (0) ;
for (;_i <= limit3 ;_i = _i + step3 ) {
 //BA.debugLineNum = 263;BA.debugLine="randomstr = randomstr & (abc.CharAt(Rnd(0,abc.Le";
_randomstr = _randomstr+BA.ObjectToString((_abc.charAt(anywheresoftware.b4a.keywords.Common.Rnd((int) (0),_abc.length()))));
 }
};
 //BA.debugLineNum = 265;BA.debugLine="Return randomstr";
if (true) return _randomstr;
 //BA.debugLineNum = 266;BA.debugLine="End Sub";
return "";
}
public static anywheresoftware.b4a.keywords.Common.ResumableSubWrapper  _requestauthtoken() throws Exception{
ResumableSub_RequestAuthToken rsub = new ResumableSub_RequestAuthToken(null);
rsub.resume(processBA, null);
return (anywheresoftware.b4a.keywords.Common.ResumableSubWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper(), rsub);
}
public static class ResumableSub_RequestAuthToken extends BA.ResumableSub {
public ResumableSub_RequestAuthToken(cloyd.smart.home.monitor.notificationservice parent) {
this.parent = parent;
}
cloyd.smart.home.monitor.notificationservice parent;
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
 //BA.debugLineNum = 226;BA.debugLine="Try";
if (true) break;

case 1:
//try
this.state = 21;
this.catchState = 20;
this.state = 3;
if (true) break;

case 3:
//C
this.state = 4;
this.catchState = 20;
 //BA.debugLineNum = 227;BA.debugLine="Dim jobLogin As HttpJob";
_joblogin = new cloyd.smart.home.monitor.httpjob();
 //BA.debugLineNum = 228;BA.debugLine="jobLogin.Initialize(\"\", Me)";
_joblogin._initialize /*String*/ (processBA,"",notificationservice.getObject());
 //BA.debugLineNum = 229;BA.debugLine="jobLogin.PostString(\"https://rest-prod.immedia-s";
_joblogin._poststring /*String*/ ("https://rest-prod.immedia-semi.com/api/v4/account/login","email="+parent._emailaddress+"&password="+parent._password);
 //BA.debugLineNum = 230;BA.debugLine="jobLogin.GetRequest.SetContentType(\"application/";
_joblogin._getrequest /*anywheresoftware.b4h.okhttp.OkHttpClientWrapper.OkHttpRequest*/ ().SetContentType("application/x-www-form-urlencoded");
 //BA.debugLineNum = 231;BA.debugLine="jobLogin.GetRequest.SetHeader(\"User-Agent\",Rando";
_joblogin._getrequest /*anywheresoftware.b4h.okhttp.OkHttpClientWrapper.OkHttpRequest*/ ().SetHeader("User-Agent",_randomstring((int) (12)));
 //BA.debugLineNum = 232;BA.debugLine="Wait For (jobLogin) JobDone(jobLogin As HttpJob)";
anywheresoftware.b4a.keywords.Common.WaitFor("jobdone", processBA, this, (Object)(_joblogin));
this.state = 22;
return;
case 22:
//C
this.state = 4;
_joblogin = (cloyd.smart.home.monitor.httpjob) result[0];
;
 //BA.debugLineNum = 233;BA.debugLine="If jobLogin.Success Then";
if (true) break;

case 4:
//if
this.state = 18;
if (_joblogin._success /*boolean*/ ) { 
this.state = 6;
}else {
this.state = 17;
}if (true) break;

case 6:
//C
this.state = 7;
 //BA.debugLineNum = 234;BA.debugLine="GetAuthInfo(jobLogin.GetString)";
_getauthinfo(_joblogin._getstring /*String*/ ());
 //BA.debugLineNum = 236;BA.debugLine="If TwoClientFAVerificationRequired Then";
if (true) break;

case 7:
//if
this.state = 10;
if (BA.ObjectToBoolean(parent._twoclientfaverificationrequired)) { 
this.state = 9;
}if (true) break;

case 9:
//C
this.state = 10;
 //BA.debugLineNum = 237;BA.debugLine="response = \"ERROR TwoClientFAVerificationRequi";
parent._response = "ERROR TwoClientFAVerificationRequired";
 //BA.debugLineNum = 238;BA.debugLine="Return Null";
if (true) {
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,anywheresoftware.b4a.keywords.Common.Null);return;};
 if (true) break;
;
 //BA.debugLineNum = 241;BA.debugLine="If response.StartsWith(\"ERROR: \") Or response.C";

case 10:
//if
this.state = 15;
if (parent._response.startsWith("ERROR: ") || parent._response.contains("System is busy, please wait")) { 
this.state = 12;
}else {
this.state = 14;
}if (true) break;

case 12:
//C
this.state = 15;
 //BA.debugLineNum = 242;BA.debugLine="jobLogin.Release";
_joblogin._release /*String*/ ();
 //BA.debugLineNum = 243;BA.debugLine="Return Null";
if (true) {
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,anywheresoftware.b4a.keywords.Common.Null);return;};
 if (true) break;

case 14:
//C
this.state = 15;
 if (true) break;

case 15:
//C
this.state = 18;
;
 if (true) break;

case 17:
//C
this.state = 18;
 //BA.debugLineNum = 248;BA.debugLine="Log(\"RequestAuthToken error: \" & jobLogin.Error";
anywheresoftware.b4a.keywords.Common.LogImpl("82883612","RequestAuthToken error: "+_joblogin._errormessage /*String*/ ,0);
 //BA.debugLineNum = 249;BA.debugLine="jobLogin.Release";
_joblogin._release /*String*/ ();
 //BA.debugLineNum = 250;BA.debugLine="Return Null";
if (true) {
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,anywheresoftware.b4a.keywords.Common.Null);return;};
 if (true) break;

case 18:
//C
this.state = 21;
;
 //BA.debugLineNum = 252;BA.debugLine="jobLogin.Release";
_joblogin._release /*String*/ ();
 if (true) break;

case 20:
//C
this.state = 21;
this.catchState = 0;
 //BA.debugLineNum = 254;BA.debugLine="Log(\"RequestAuthToken LastException: \" & LastExc";
anywheresoftware.b4a.keywords.Common.LogImpl("82883618","RequestAuthToken LastException: "+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(processBA)),0);
 if (true) break;
if (true) break;

case 21:
//C
this.state = -1;
this.catchState = 0;
;
 //BA.debugLineNum = 256;BA.debugLine="Return Null";
if (true) {
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,anywheresoftware.b4a.keywords.Common.Null);return;};
 //BA.debugLineNum = 257;BA.debugLine="End Sub";
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
public static void  _jobdone(cloyd.smart.home.monitor.httpjob _joblogin) throws Exception{
}
public static anywheresoftware.b4a.keywords.Common.ResumableSubWrapper  _restget(String _url) throws Exception{
ResumableSub_RESTGet rsub = new ResumableSub_RESTGet(null,_url);
rsub.resume(processBA, null);
return (anywheresoftware.b4a.keywords.Common.ResumableSubWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper(), rsub);
}
public static class ResumableSub_RESTGet extends BA.ResumableSub {
public ResumableSub_RESTGet(cloyd.smart.home.monitor.notificationservice parent,String _url) {
this.parent = parent;
this._url = _url;
}
cloyd.smart.home.monitor.notificationservice parent;
String _url;
cloyd.smart.home.monitor.httpjob _j = null;

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
 //BA.debugLineNum = 195;BA.debugLine="Try";
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
 //BA.debugLineNum = 196;BA.debugLine="Dim j As HttpJob";
_j = new cloyd.smart.home.monitor.httpjob();
 //BA.debugLineNum = 197;BA.debugLine="response = \"\"";
parent._response = "";
 //BA.debugLineNum = 198;BA.debugLine="j.Initialize(\"\", Me) 'name is empty as it is no";
_j._initialize /*String*/ (processBA,"",notificationservice.getObject());
 //BA.debugLineNum = 199;BA.debugLine="j.Download(url)";
_j._download /*String*/ (_url);
 //BA.debugLineNum = 200;BA.debugLine="j.GetRequest.SetHeader(\"TOKEN_AUTH\", authToken)";
_j._getrequest /*anywheresoftware.b4h.okhttp.OkHttpClientWrapper.OkHttpRequest*/ ().SetHeader("TOKEN_AUTH",parent._authtoken);
 //BA.debugLineNum = 201;BA.debugLine="Wait For (j) JobDone(j As HttpJob)";
anywheresoftware.b4a.keywords.Common.WaitFor("jobdone", processBA, this, (Object)(_j));
this.state = 16;
return;
case 16:
//C
this.state = 4;
_j = (cloyd.smart.home.monitor.httpjob) result[0];
;
 //BA.debugLineNum = 202;BA.debugLine="If j.Success Then";
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
 //BA.debugLineNum = 203;BA.debugLine="response = j.GetString";
parent._response = _j._getstring /*String*/ ();
 if (true) break;

case 8:
//C
this.state = 9;
 //BA.debugLineNum = 205;BA.debugLine="response = \"ERROR: \" & j.ErrorMessage";
parent._response = "ERROR: "+_j._errormessage /*String*/ ;
 if (true) break;
;
 //BA.debugLineNum = 207;BA.debugLine="If response.Contains(\"System is busy, please wai";

case 9:
//if
this.state = 12;
if (parent._response.contains("System is busy, please wait")) { 
this.state = 11;
}if (true) break;

case 11:
//C
this.state = 12;
 if (true) break;

case 12:
//C
this.state = 15;
;
 //BA.debugLineNum = 210;BA.debugLine="j.Release";
_j._release /*String*/ ();
 if (true) break;

case 14:
//C
this.state = 15;
this.catchState = 0;
 //BA.debugLineNum = 212;BA.debugLine="response = \"ERROR: \" & LastException";
parent._response = "ERROR: "+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(processBA));
 //BA.debugLineNum = 213;BA.debugLine="Log(\"RESTDownload LastException: \" & LastExcepti";
anywheresoftware.b4a.keywords.Common.LogImpl("82818067","RESTDownload LastException: "+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(processBA)),0);
 if (true) break;
if (true) break;

case 15:
//C
this.state = -1;
this.catchState = 0;
;
 //BA.debugLineNum = 215;BA.debugLine="Log(\"URL: \" & url)";
anywheresoftware.b4a.keywords.Common.LogImpl("82818069","URL: "+_url,0);
 //BA.debugLineNum = 216;BA.debugLine="Log(\"Response: \" & response)";
anywheresoftware.b4a.keywords.Common.LogImpl("82818070","Response: "+parent._response,0);
 //BA.debugLineNum = 217;BA.debugLine="Return(response)";
if (true) {
anywheresoftware.b4a.keywords.Common.ReturnFromResumableSub(this,(Object)((parent._response)));return;};
 //BA.debugLineNum = 218;BA.debugLine="End Sub";
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
public static void  _sdtime_tick() throws Exception{
ResumableSub_SDTime_tick rsub = new ResumableSub_SDTime_tick(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_SDTime_tick extends BA.ResumableSub {
public ResumableSub_SDTime_tick(cloyd.smart.home.monitor.notificationservice parent) {
this.parent = parent;
}
cloyd.smart.home.monitor.notificationservice parent;
anywheresoftware.b4a.keywords.Common.ResumableSubWrapper _rs = null;
Object _result = null;

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
 //BA.debugLineNum = 141;BA.debugLine="Try";
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
 //BA.debugLineNum = 142;BA.debugLine="Dim rs As ResumableSub = GetUnwatchedVideosServi";
_rs = new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper();
_rs = _getunwatchedvideosservice();
 //BA.debugLineNum = 143;BA.debugLine="wait for (rs) complete (Result As Object)";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, _rs);
this.state = 7;
return;
case 7:
//C
this.state = 6;
_result = (Object) result[0];
;
 if (true) break;

case 5:
//C
this.state = 6;
this.catchState = 0;
 //BA.debugLineNum = 145;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("82686981",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(processBA)),0);
 if (true) break;
if (true) break;

case 6:
//C
this.state = -1;
this.catchState = 0;
;
 //BA.debugLineNum = 147;BA.debugLine="End Sub";
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
public static String  _service_create() throws Exception{
 //BA.debugLineNum = 17;BA.debugLine="Sub Service_Create";
 //BA.debugLineNum = 18;BA.debugLine="listener.Initialize(\"listener\")";
_listener.Initialize(processBA,"listener");
 //BA.debugLineNum = 19;BA.debugLine="End Sub";
return "";
}
public static String  _service_destroy() throws Exception{
 //BA.debugLineNum = 136;BA.debugLine="Sub Service_Destroy";
 //BA.debugLineNum = 138;BA.debugLine="End Sub";
return "";
}
public static String  _service_start(anywheresoftware.b4a.objects.IntentWrapper _startingintent) throws Exception{
 //BA.debugLineNum = 21;BA.debugLine="Sub Service_Start (StartingIntent As Intent)";
 //BA.debugLineNum = 22;BA.debugLine="If listener.HandleIntent(StartingIntent) Then Ret";
if (_listener.HandleIntent(_startingintent)) { 
if (true) return "";};
 //BA.debugLineNum = 23;BA.debugLine="End Sub";
return "";
}
}
