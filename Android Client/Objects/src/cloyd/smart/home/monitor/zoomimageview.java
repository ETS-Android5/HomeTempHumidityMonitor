package cloyd.smart.home.monitor;


import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.B4AClass;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.debug.*;

public class zoomimageview extends B4AClass.ImplB4AClass implements BA.SubDelegator{
    private static java.util.HashMap<String, java.lang.reflect.Method> htSubs;
    private void innerInitialize(BA _ba) throws Exception {
        if (ba == null) {
            ba = new BA(_ba, this, htSubs, "cloyd.smart.home.monitor.zoomimageview");
            if (htSubs == null) {
                ba.loadHtSubs(this.getClass());
                htSubs = ba.htSubs;
            }
            
        }
        if (BA.isShellModeRuntimeCheck(ba)) 
			   this.getClass().getMethod("_class_globals", cloyd.smart.home.monitor.zoomimageview.class).invoke(this, new Object[] {null});
        else
            ba.raiseEvent2(null, true, "class_globals", false);
    }

 public anywheresoftware.b4a.keywords.Common __c = null;
public String _meventname = "";
public Object _mcallback = null;
public anywheresoftware.b4a.objects.B4XViewWrapper _mbase = null;
public anywheresoftware.b4a.objects.B4XViewWrapper.XUI _xui = null;
public Object _tag = null;
public anywheresoftware.b4a.objects.B4XViewWrapper _imageview = null;
public anywheresoftware.b4a.objects.B4XViewWrapper _pnl = null;
public anywheresoftware.b4a.objects.B4XViewWrapper _pnlbackground = null;
public float _ivoffsetx = 0f;
public float _ivoffsety = 0f;
public float _imageratio = 0f;
public anywheresoftware.b4j.object.JavaObject _scaledetector = null;
public float _prevspan = 0f;
public boolean _touchdown = false;
public int _startleft = 0;
public int _starttop = 0;
public int _startx = 0;
public int _starty = 0;
public int _clickthreshold = 0;
public long _clickstart = 0L;
public boolean _disableclickevent = false;
public b4a.example.dateutils _dateutils = null;
public cloyd.smart.home.monitor.main _main = null;
public cloyd.smart.home.monitor.smarthomemonitor _smarthomemonitor = null;
public cloyd.smart.home.monitor.notificationservice _notificationservice = null;
public cloyd.smart.home.monitor.statemanager _statemanager = null;
public cloyd.smart.home.monitor.starter _starter = null;
public cloyd.smart.home.monitor.httputils2service _httputils2service = null;
public cloyd.smart.home.monitor.xuiviewsutils _xuiviewsutils = null;
public cloyd.smart.home.monitor.b4xcollections _b4xcollections = null;
public String  _base_resize(double _width,double _height) throws Exception{
 //BA.debugLineNum = 101;BA.debugLine="Private Sub Base_Resize (Width As Double, Height A";
 //BA.debugLineNum = 102;BA.debugLine="pnl.SetLayoutAnimated(0, 0, 0, Width, Height)";
_pnl.SetLayoutAnimated((int) (0),(int) (0),(int) (0),(int) (_width),(int) (_height));
 //BA.debugLineNum = 103;BA.debugLine="Reset";
_reset();
 //BA.debugLineNum = 104;BA.debugLine="End Sub";
return "";
}
public String  _class_globals() throws Exception{
 //BA.debugLineNum = 2;BA.debugLine="Sub Class_Globals";
 //BA.debugLineNum = 3;BA.debugLine="Private mEventName As String 'ignore";
_meventname = "";
 //BA.debugLineNum = 4;BA.debugLine="Private mCallBack As Object 'ignore";
_mcallback = new Object();
 //BA.debugLineNum = 5;BA.debugLine="Public mBase As B4XView";
_mbase = new anywheresoftware.b4a.objects.B4XViewWrapper();
 //BA.debugLineNum = 6;BA.debugLine="Private xui As XUI 'ignore";
_xui = new anywheresoftware.b4a.objects.B4XViewWrapper.XUI();
 //BA.debugLineNum = 7;BA.debugLine="Public Tag As Object";
_tag = new Object();
 //BA.debugLineNum = 8;BA.debugLine="Public ImageView As B4XView";
_imageview = new anywheresoftware.b4a.objects.B4XViewWrapper();
 //BA.debugLineNum = 9;BA.debugLine="Private pnl As B4XView";
_pnl = new anywheresoftware.b4a.objects.B4XViewWrapper();
 //BA.debugLineNum = 10;BA.debugLine="Public pnlBackground As B4XView";
_pnlbackground = new anywheresoftware.b4a.objects.B4XViewWrapper();
 //BA.debugLineNum = 11;BA.debugLine="Private IVOffsetX, IVOffsetY As Float";
_ivoffsetx = 0f;
_ivoffsety = 0f;
 //BA.debugLineNum = 12;BA.debugLine="Private ImageRatio As Float";
_imageratio = 0f;
 //BA.debugLineNum = 14;BA.debugLine="Private ScaleDetector As JavaObject";
_scaledetector = new anywheresoftware.b4j.object.JavaObject();
 //BA.debugLineNum = 15;BA.debugLine="Private PrevSpan As Float";
_prevspan = 0f;
 //BA.debugLineNum = 19;BA.debugLine="Private TouchDown As Boolean";
_touchdown = false;
 //BA.debugLineNum = 20;BA.debugLine="Private StartLeft, StartTop, StartX, StartY As In";
_startleft = 0;
_starttop = 0;
_startx = 0;
_starty = 0;
 //BA.debugLineNum = 21;BA.debugLine="Public ClickThreshold As Int = 200";
_clickthreshold = (int) (200);
 //BA.debugLineNum = 22;BA.debugLine="Private ClickStart As Long";
_clickstart = 0L;
 //BA.debugLineNum = 23;BA.debugLine="Private DisableClickEvent As Boolean 'ignore";
_disableclickevent = false;
 //BA.debugLineNum = 24;BA.debugLine="End Sub";
return "";
}
public String  _designercreateview(Object _base,anywheresoftware.b4a.objects.LabelWrapper _lbl,anywheresoftware.b4a.objects.collections.Map _props) throws Exception{
anywheresoftware.b4a.objects.ImageViewWrapper _iv = null;
anywheresoftware.b4j.object.JavaObject _jo = null;
Object _touchlistener = null;
Object _scalelistener = null;
anywheresoftware.b4j.object.JavaObject _ctxt = null;
anywheresoftware.b4j.object.JavaObject _version = null;
int _sdk = 0;
 //BA.debugLineNum = 32;BA.debugLine="Public Sub DesignerCreateView (Base As Object, Lbl";
 //BA.debugLineNum = 33;BA.debugLine="mBase = Base";
_mbase = (anywheresoftware.b4a.objects.B4XViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.B4XViewWrapper(), (java.lang.Object)(_base));
 //BA.debugLineNum = 34;BA.debugLine="Tag = mBase.Tag";
_tag = _mbase.getTag();
 //BA.debugLineNum = 35;BA.debugLine="mBase.Tag = Me";
_mbase.setTag(this);
 //BA.debugLineNum = 36;BA.debugLine="pnlBackground = xui.CreatePanel(\"\")";
_pnlbackground = _xui.CreatePanel(ba,"");
 //BA.debugLineNum = 37;BA.debugLine="mBase.SetColorAndBorder(mBase.Color, 0, 0, 0)";
_mbase.SetColorAndBorder(_mbase.getColor(),(int) (0),(int) (0),(int) (0));
 //BA.debugLineNum = 38;BA.debugLine="Dim IV As ImageView";
_iv = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 39;BA.debugLine="IV.Initialize(\"\")";
_iv.Initialize(ba,"");
 //BA.debugLineNum = 40;BA.debugLine="ImageView = IV";
_imageview = (anywheresoftware.b4a.objects.B4XViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.B4XViewWrapper(), (java.lang.Object)(_iv.getObject()));
 //BA.debugLineNum = 41;BA.debugLine="pnl = xui.CreatePanel(\"pnl\")";
_pnl = _xui.CreatePanel(ba,"pnl");
 //BA.debugLineNum = 42;BA.debugLine="mBase.AddView(pnl, 0, 0, mBase.Width, mBase.Heigh";
_mbase.AddView((android.view.View)(_pnl.getObject()),(int) (0),(int) (0),_mbase.getWidth(),_mbase.getHeight());
 //BA.debugLineNum = 43;BA.debugLine="pnl.AddView(pnlBackground, 0, 0, mBase.Width, mBa";
_pnl.AddView((android.view.View)(_pnlbackground.getObject()),(int) (0),(int) (0),_mbase.getWidth(),_mbase.getHeight());
 //BA.debugLineNum = 44;BA.debugLine="pnlBackground.AddView(ImageView, 0, 0, mBase.Widt";
_pnlbackground.AddView((android.view.View)(_imageview.getObject()),(int) (0),(int) (0),_mbase.getWidth(),_mbase.getHeight());
 //BA.debugLineNum = 50;BA.debugLine="Dim jo As JavaObject = pnl";
_jo = new anywheresoftware.b4j.object.JavaObject();
_jo = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(_pnl.getObject()));
 //BA.debugLineNum = 51;BA.debugLine="Dim TouchListener As Object = jo.CreateEvent(\"and";
_touchlistener = _jo.CreateEvent(ba,"android.view.View$OnTouchListener","TouchListener",(Object)(__c.False));
 //BA.debugLineNum = 52;BA.debugLine="jo.RunMethod(\"setOnTouchListener\", Array(TouchLis";
_jo.RunMethod("setOnTouchListener",new Object[]{_touchlistener});
 //BA.debugLineNum = 53;BA.debugLine="Dim ScaleListener As Object = jo.CreateEventFromU";
_scalelistener = _jo.CreateEventFromUI(ba,"android.view.ScaleGestureDetector$OnScaleGestureListener","ScaleListener",(Object)(__c.True));
 //BA.debugLineNum = 54;BA.debugLine="Dim ctxt As JavaObject";
_ctxt = new anywheresoftware.b4j.object.JavaObject();
 //BA.debugLineNum = 55;BA.debugLine="ctxt.InitializeContext";
_ctxt.InitializeContext(ba);
 //BA.debugLineNum = 56;BA.debugLine="ScaleDetector.InitializeNewInstance(\"android.view";
_scaledetector.InitializeNewInstance("android.view.ScaleGestureDetector",new Object[]{(Object)(_ctxt.getObject()),_scalelistener});
 //BA.debugLineNum = 57;BA.debugLine="Dim version As JavaObject";
_version = new anywheresoftware.b4j.object.JavaObject();
 //BA.debugLineNum = 58;BA.debugLine="Dim sdk As Int = version.InitializeStatic(\"androi";
_sdk = (int)(BA.ObjectToNumber(_version.InitializeStatic("android.os.Build$VERSION").GetField("SDK_INT")));
 //BA.debugLineNum = 59;BA.debugLine="If sdk >= 19 Then";
if (_sdk>=19) { 
 //BA.debugLineNum = 60;BA.debugLine="ScaleDetector.RunMethod(\"setQuickScaleEnabled\",";
_scaledetector.RunMethod("setQuickScaleEnabled",new Object[]{(Object)(__c.False)});
 };
 //BA.debugLineNum = 67;BA.debugLine="End Sub";
return "";
}
public String  _initialize(anywheresoftware.b4a.BA _ba,Object _callback,String _eventname) throws Exception{
innerInitialize(_ba);
 //BA.debugLineNum = 26;BA.debugLine="Public Sub Initialize (Callback As Object, EventNa";
 //BA.debugLineNum = 27;BA.debugLine="mEventName = EventName";
_meventname = _eventname;
 //BA.debugLineNum = 28;BA.debugLine="mCallBack = Callback";
_mcallback = _callback;
 //BA.debugLineNum = 29;BA.debugLine="End Sub";
return "";
}
public String  _pnl_touch(int _action,float _x1,float _y1) throws Exception{
 //BA.debugLineNum = 199;BA.debugLine="Private Sub pnl_Touch (Action As Int, X1 As Float,";
 //BA.debugLineNum = 200;BA.debugLine="If Action = pnl.TOUCH_ACTION_DOWN Or TouchDown =";
if (_action==_pnl.TOUCH_ACTION_DOWN || _touchdown==__c.False) { 
 //BA.debugLineNum = 201;BA.debugLine="StartLeft = pnlBackground.Left";
_startleft = _pnlbackground.getLeft();
 //BA.debugLineNum = 202;BA.debugLine="StartTop = pnlBackground.Top";
_starttop = _pnlbackground.getTop();
 //BA.debugLineNum = 203;BA.debugLine="StartX = X1";
_startx = (int) (_x1);
 //BA.debugLineNum = 204;BA.debugLine="StartY = Y1";
_starty = (int) (_y1);
 //BA.debugLineNum = 205;BA.debugLine="TouchDown = True";
_touchdown = __c.True;
 //BA.debugLineNum = 206;BA.debugLine="If xui.IsB4A = False Then ClickStart = DateTime.";
if (_xui.getIsB4A()==__c.False) { 
_clickstart = __c.DateTime.getNow();};
 }else if(_action==_pnl.TOUCH_ACTION_MOVE && _touchdown) { 
 //BA.debugLineNum = 208;BA.debugLine="pnlBackground.Left = Min(0.5 * mBase.Width, Star";
_pnlbackground.setLeft((int) (__c.Min(0.5*_mbase.getWidth(),_startleft+1.2*(_x1-_startx))));
 //BA.debugLineNum = 209;BA.debugLine="pnlBackground.Left = Max(-(pnlBackground.Width -";
_pnlbackground.setLeft((int) (__c.Max(-(_pnlbackground.getWidth()-0.5*_mbase.getWidth()),_pnlbackground.getLeft())));
 //BA.debugLineNum = 210;BA.debugLine="pnlBackground.Top = Min(0.5 * mBase.Height, Star";
_pnlbackground.setTop((int) (__c.Min(0.5*_mbase.getHeight(),_starttop+1.2*(_y1-_starty))));
 //BA.debugLineNum = 211;BA.debugLine="pnlBackground.Top = Max(-(pnlBackground.Height -";
_pnlbackground.setTop((int) (__c.Max(-(_pnlbackground.getHeight()-0.5*_mbase.getHeight()),_pnlbackground.getTop())));
 //BA.debugLineNum = 212;BA.debugLine="SetImageViewLayout";
_setimageviewlayout();
 }else if(_action==_pnl.TOUCH_ACTION_UP) { 
 //BA.debugLineNum = 214;BA.debugLine="TouchDown = False";
_touchdown = __c.False;
 //BA.debugLineNum = 215;BA.debugLine="If DateTime.Now - ClickStart < ClickThreshold An";
if (__c.DateTime.getNow()-_clickstart<_clickthreshold && _disableclickevent==__c.False) { 
 //BA.debugLineNum = 216;BA.debugLine="If xui.SubExists(mCallBack, mEventName & \"_Clic";
if (_xui.SubExists(ba,_mcallback,_meventname+"_Click",(int) (0))) { 
 //BA.debugLineNum = 217;BA.debugLine="CallSub(mCallBack, mEventName & \"_Click\")";
__c.CallSubNew(ba,_mcallback,_meventname+"_Click");
 };
 };
 }else {
 };
 //BA.debugLineNum = 223;BA.debugLine="End Sub";
return "";
}
public String  _reset() throws Exception{
int _containerwidth = 0;
int _containerheight = 0;
float _ivr = 0f;
int _left = 0;
int _top = 0;
 //BA.debugLineNum = 106;BA.debugLine="Private Sub Reset";
 //BA.debugLineNum = 107;BA.debugLine="pnlBackground.SetLayoutAnimated(0, 0, 0, mBase.Wi";
_pnlbackground.SetLayoutAnimated((int) (0),(int) (0),(int) (0),_mbase.getWidth(),_mbase.getHeight());
 //BA.debugLineNum = 108;BA.debugLine="If ImageView.GetBitmap.IsInitialized Then";
if (_imageview.GetBitmap().IsInitialized()) { 
 //BA.debugLineNum = 109;BA.debugLine="Dim ContainerWidth As Int = mBase.Width";
_containerwidth = _mbase.getWidth();
 //BA.debugLineNum = 110;BA.debugLine="Dim ContainerHeight As Int = mBase.Height";
_containerheight = _mbase.getHeight();
 //BA.debugLineNum = 111;BA.debugLine="Dim ivr As Float = ContainerWidth / ContainerHei";
_ivr = (float) (_containerwidth/(double)_containerheight);
 //BA.debugLineNum = 112;BA.debugLine="If ImageRatio > ivr Then";
if (_imageratio>_ivr) { 
 //BA.debugLineNum = 113;BA.debugLine="IVOffsetX = 0";
_ivoffsetx = (float) (0);
 //BA.debugLineNum = 114;BA.debugLine="IVOffsetY = (ContainerHeight - 1 / ImageRatio *";
_ivoffsety = (float) ((_containerheight-1/(double)_imageratio*_containerwidth)/(double)2/(double)_containerheight);
 }else {
 //BA.debugLineNum = 116;BA.debugLine="IVOffsetY = 0";
_ivoffsety = (float) (0);
 //BA.debugLineNum = 117;BA.debugLine="IVOffsetX = (ContainerWidth - ImageRatio * Cont";
_ivoffsetx = (float) ((_containerwidth-_imageratio*_containerheight)/(double)2/(double)_containerwidth);
 };
 //BA.debugLineNum = 119;BA.debugLine="Dim left As Int = pnlBackground.Width * IVOffset";
_left = (int) (_pnlbackground.getWidth()*_ivoffsetx);
 //BA.debugLineNum = 120;BA.debugLine="Dim top As Int = pnlBackground.Height * IVOffset";
_top = (int) (_pnlbackground.getHeight()*_ivoffsety);
 //BA.debugLineNum = 121;BA.debugLine="ImageView.SetLayoutAnimated(0, left, top, pnlBac";
_imageview.SetLayoutAnimated((int) (0),_left,_top,(int) (_pnlbackground.getWidth()-2*_left),(int) (_pnlbackground.getHeight()-2*_top));
 }else {
 //BA.debugLineNum = 123;BA.debugLine="ImageView.SetLayoutAnimated(0, 0, 0, mBase.Width";
_imageview.SetLayoutAnimated((int) (0),(int) (0),(int) (0),_mbase.getWidth(),_mbase.getWidth());
 };
 //BA.debugLineNum = 125;BA.debugLine="End Sub";
return "";
}
public Object  _scalelistener_event(String _methodname,Object[] _args) throws Exception{
anywheresoftware.b4j.object.JavaObject _scalegesturedetector = null;
float _x = 0f;
float _y = 0f;
float _currentspan = 0f;
float _delta = 0f;
 //BA.debugLineNum = 139;BA.debugLine="Private Sub ScaleListener_Event (MethodName As Str";
 //BA.debugLineNum = 140;BA.debugLine="Dim ScaleGestureDetector As JavaObject = Args(0)";
_scalegesturedetector = new anywheresoftware.b4j.object.JavaObject();
_scalegesturedetector = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(_args[(int) (0)]));
 //BA.debugLineNum = 141;BA.debugLine="If ScaleGestureDetector.RunMethod(\"isInProgress\",";
if ((_scalegesturedetector.RunMethod("isInProgress",(Object[])(__c.Null))).equals((Object)(__c.False))) { 
 //BA.debugLineNum = 142;BA.debugLine="Return True";
if (true) return (Object)(__c.True);
 };
 //BA.debugLineNum = 144;BA.debugLine="TouchDown = False";
_touchdown = __c.False;
 //BA.debugLineNum = 145;BA.debugLine="Dim x As Float = ScaleGestureDetector.RunMethod(\"";
_x = (float)(BA.ObjectToNumber(_scalegesturedetector.RunMethod("getFocusX",(Object[])(__c.Null))));
 //BA.debugLineNum = 146;BA.debugLine="Dim y As Float = ScaleGestureDetector.RunMethod(\"";
_y = (float)(BA.ObjectToNumber(_scalegesturedetector.RunMethod("getFocusY",(Object[])(__c.Null))));
 //BA.debugLineNum = 147;BA.debugLine="Dim currentspan As Float = ScaleGestureDetector.R";
_currentspan = (float)(BA.ObjectToNumber(_scalegesturedetector.RunMethod("getCurrentSpan",(Object[])(__c.Null))));
 //BA.debugLineNum = 148;BA.debugLine="If MethodName = \"onScaleBegin\" Then";
if ((_methodname).equals("onScaleBegin")) { 
 //BA.debugLineNum = 149;BA.debugLine="PrevSpan = currentspan";
_prevspan = _currentspan;
 //BA.debugLineNum = 150;BA.debugLine="pnl_Touch(pnl.TOUCH_ACTION_DOWN, x, y)";
_pnl_touch(_pnl.TOUCH_ACTION_DOWN,_x,_y);
 //BA.debugLineNum = 151;BA.debugLine="Return True";
if (true) return (Object)(__c.True);
 }else if((_methodname).equals("onScaleEnd") || _currentspan==0) { 
 //BA.debugLineNum = 153;BA.debugLine="Return True";
if (true) return (Object)(__c.True);
 };
 //BA.debugLineNum = 155;BA.debugLine="Dim delta As Float = Power(currentspan / PrevSpan";
_delta = (float) (__c.Power(_currentspan/(double)_prevspan,2));
 //BA.debugLineNum = 156;BA.debugLine="PrevSpan = currentspan";
_prevspan = _currentspan;
 //BA.debugLineNum = 157;BA.debugLine="ZoomChanged(x, y, delta)";
_zoomchanged((int) (_x),(int) (_y),_delta);
 //BA.debugLineNum = 158;BA.debugLine="Return True";
if (true) return (Object)(__c.True);
 //BA.debugLineNum = 159;BA.debugLine="End Sub";
return null;
}
public String  _setbitmap(anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper _bmp) throws Exception{
anywheresoftware.b4a.objects.ImageViewWrapper _iv = null;
 //BA.debugLineNum = 86;BA.debugLine="Public Sub SetBitmap(bmp As B4XBitmap)";
 //BA.debugLineNum = 87;BA.debugLine="If bmp.IsInitialized = False Then";
if (_bmp.IsInitialized()==__c.False) { 
 //BA.debugLineNum = 88;BA.debugLine="ImageView.SetBitmap(Null)";
_imageview.SetBitmap((android.graphics.Bitmap)(__c.Null));
 //BA.debugLineNum = 89;BA.debugLine="Return";
if (true) return "";
 }else {
 //BA.debugLineNum = 91;BA.debugLine="ImageView.SetBitmap(bmp)";
_imageview.SetBitmap((android.graphics.Bitmap)(_bmp.getObject()));
 };
 //BA.debugLineNum = 94;BA.debugLine="Dim iv As ImageView = ImageView";
_iv = new anywheresoftware.b4a.objects.ImageViewWrapper();
_iv = (anywheresoftware.b4a.objects.ImageViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ImageViewWrapper(), (android.widget.ImageView)(_imageview.getObject()));
 //BA.debugLineNum = 95;BA.debugLine="iv.Gravity = Gravity.FILL";
_iv.setGravity(__c.Gravity.FILL);
 //BA.debugLineNum = 97;BA.debugLine="ImageRatio = bmp.Width / bmp.Height";
_imageratio = (float) (_bmp.getWidth()/(double)_bmp.getHeight());
 //BA.debugLineNum = 98;BA.debugLine="Reset";
_reset();
 //BA.debugLineNum = 99;BA.debugLine="End Sub";
return "";
}
public String  _setimageviewlayout() throws Exception{
int _left = 0;
int _top = 0;
 //BA.debugLineNum = 225;BA.debugLine="Private Sub SetImageViewLayout";
 //BA.debugLineNum = 226;BA.debugLine="Dim left As Int = pnlBackground.Width * IVOffsetX";
_left = (int) (_pnlbackground.getWidth()*_ivoffsetx);
 //BA.debugLineNum = 227;BA.debugLine="Dim top As Int = pnlBackground.Height * IVOffsetY";
_top = (int) (_pnlbackground.getHeight()*_ivoffsety);
 //BA.debugLineNum = 228;BA.debugLine="ImageView.SetLayoutAnimated(0, left, top, pnlBack";
_imageview.SetLayoutAnimated((int) (0),_left,_top,(int) (_pnlbackground.getWidth()-2*_left),(int) (_pnlbackground.getHeight()-2*_top));
 //BA.debugLineNum = 229;BA.debugLine="End Sub";
return "";
}
public Object  _touchlistener_event(String _methodname,Object[] _args) throws Exception{
anywheresoftware.b4j.object.JavaObject _motionevent = null;
int _action = 0;
 //BA.debugLineNum = 128;BA.debugLine="Private Sub TouchListener_Event (MethodName As Str";
 //BA.debugLineNum = 129;BA.debugLine="Dim MotionEvent As JavaObject = Args(1)";
_motionevent = new anywheresoftware.b4j.object.JavaObject();
_motionevent = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(_args[(int) (1)]));
 //BA.debugLineNum = 130;BA.debugLine="If 1 = MotionEvent.RunMethod(\"getPointerCount\", N";
if (1==(double)(BA.ObjectToNumber(_motionevent.RunMethod("getPointerCount",(Object[])(__c.Null))))) { 
 //BA.debugLineNum = 131;BA.debugLine="Dim action As Int = MotionEvent.RunMethod(\"getAc";
_action = (int)(BA.ObjectToNumber(_motionevent.RunMethod("getAction",(Object[])(__c.Null))));
 //BA.debugLineNum = 132;BA.debugLine="if action = 0 Then ClickStart = DateTime.Now";
if (_action==0) { 
_clickstart = __c.DateTime.getNow();};
 //BA.debugLineNum = 133;BA.debugLine="pnl_Touch(action, MotionEvent.RunMethod(\"getX\",";
_pnl_touch(_action,(float)(BA.ObjectToNumber(_motionevent.RunMethod("getX",(Object[])(__c.Null)))),(float)(BA.ObjectToNumber(_motionevent.RunMethod("getY",(Object[])(__c.Null)))));
 };
 //BA.debugLineNum = 135;BA.debugLine="ScaleDetector.RunMethod(\"onTouchEvent\", Array(Mot";
_scaledetector.RunMethod("onTouchEvent",new Object[]{(Object)(_motionevent.getObject())});
 //BA.debugLineNum = 136;BA.debugLine="Return True";
if (true) return (Object)(__c.True);
 //BA.debugLineNum = 137;BA.debugLine="End Sub";
return null;
}
public String  _zoomchanged(int _x,int _y,float _zoomdelta) throws Exception{
float _ivx = 0f;
float _ivy = 0f;
 //BA.debugLineNum = 191;BA.debugLine="Private Sub ZoomChanged (x As Int, y As Int, ZoomD";
 //BA.debugLineNum = 192;BA.debugLine="Dim ivx As Float = x - pnlBackground.Left";
_ivx = (float) (_x-_pnlbackground.getLeft());
 //BA.debugLineNum = 193;BA.debugLine="Dim ivy As Float = y - pnlBackground.Top";
_ivy = (float) (_y-_pnlbackground.getTop());
 //BA.debugLineNum = 194;BA.debugLine="ZoomDelta = Max(ZoomDelta, mBase.Width / pnlBackg";
_zoomdelta = (float) (__c.Max(_zoomdelta,_mbase.getWidth()/(double)_pnlbackground.getWidth()));
 //BA.debugLineNum = 195;BA.debugLine="pnlBackground.SetLayoutAnimated(0, x - ivx * Zoom";
_pnlbackground.SetLayoutAnimated((int) (0),(int) (_x-_ivx*_zoomdelta),(int) (_y-_ivy*_zoomdelta),(int) (_pnlbackground.getWidth()*_zoomdelta),(int) (_pnlbackground.getHeight()*_zoomdelta));
 //BA.debugLineNum = 196;BA.debugLine="SetImageViewLayout";
_setimageviewlayout();
 //BA.debugLineNum = 197;BA.debugLine="End Sub";
return "";
}
public Object callSub(String sub, Object sender, Object[] args) throws Exception {
BA.senderHolder.set(sender);
return BA.SubDelegator.SubNotFound;
}
}
