package cloyd.smart.home.monitor;


import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.B4AClass;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.debug.*;

public class bitmapcreatoreffects extends B4AClass.ImplB4AClass implements BA.SubDelegator{
    private static java.util.HashMap<String, java.lang.reflect.Method> htSubs;
    private void innerInitialize(BA _ba) throws Exception {
        if (ba == null) {
            ba = new BA(_ba, this, htSubs, "cloyd.smart.home.monitor.bitmapcreatoreffects");
            if (htSubs == null) {
                ba.loadHtSubs(this.getClass());
                htSubs = ba.htSubs;
            }
            
        }
        if (BA.isShellModeRuntimeCheck(ba)) 
			   this.getClass().getMethod("_class_globals", cloyd.smart.home.monitor.bitmapcreatoreffects.class).invoke(this, new Object[] {null});
        else
            ba.raiseEvent2(null, true, "class_globals", false);
    }

 public anywheresoftware.b4a.keywords.Common __c = null;
public anywheresoftware.b4a.objects.B4XViewWrapper.XUI _xui = null;
public int _sleepduration = 0;
public b4a.example.dateutils _dateutils = null;
public cloyd.smart.home.monitor.main _main = null;
public cloyd.smart.home.monitor.smarthomemonitor _smarthomemonitor = null;
public cloyd.smart.home.monitor.notificationservice _notificationservice = null;
public cloyd.smart.home.monitor.statemanager _statemanager = null;
public cloyd.smart.home.monitor.starter _starter = null;
public cloyd.smart.home.monitor.httputils2service _httputils2service = null;
public static class _bcepixelgroup{
public boolean IsInitialized;
public int SrcX;
public int SrcY;
public float x;
public float y;
public float dx;
public float dy;
public void Initialize() {
IsInitialized = true;
SrcX = 0;
SrcY = 0;
x = 0f;
y = 0f;
dx = 0f;
dy = 0f;
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public static class _hsvcolor{
public boolean IsInitialized;
public int H;
public float S;
public float V;
public float A;
public void Initialize() {
IsInitialized = true;
H = 0;
S = 0f;
V = 0f;
A = 0f;
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper  _adjustcolors(anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper _bmp,int _hueoffset,float _saturationfactor) throws Exception{
b4a.example.bitmapcreator _bc = null;
cloyd.smart.home.monitor.bitmapcreatoreffects._hsvcolor _hsv = null;
b4a.example.bitmapcreator._argbcolor _argb = null;
int _y = 0;
int _x = 0;
 //BA.debugLineNum = 372;BA.debugLine="Public Sub AdjustColors(Bmp As B4XBitmap, HueOffse";
 //BA.debugLineNum = 373;BA.debugLine="Dim bc As BitmapCreator = CreateBC(Bmp)";
_bc = _createbc(_bmp);
 //BA.debugLineNum = 374;BA.debugLine="Dim hsv As HSVColor";
_hsv = new cloyd.smart.home.monitor.bitmapcreatoreffects._hsvcolor();
 //BA.debugLineNum = 375;BA.debugLine="Dim argb As ARGBColor";
_argb = new b4a.example.bitmapcreator._argbcolor();
 //BA.debugLineNum = 376;BA.debugLine="For y = 0 To bc.mHeight - 1";
{
final int step4 = 1;
final int limit4 = (int) (_bc._mheight-1);
_y = (int) (0) ;
for (;_y <= limit4 ;_y = _y + step4 ) {
 //BA.debugLineNum = 377;BA.debugLine="For x = 0 To bc.mWidth - 1";
{
final int step5 = 1;
final int limit5 = (int) (_bc._mwidth-1);
_x = (int) (0) ;
for (;_x <= limit5 ;_x = _x + step5 ) {
 //BA.debugLineNum = 378;BA.debugLine="bc.GetARGB(x, y, argb)";
_bc._getargb(_x,_y,_argb);
 //BA.debugLineNum = 379;BA.debugLine="ARGBToHSV(argb, hsv)";
_argbtohsv(_argb,_hsv);
 //BA.debugLineNum = 380;BA.debugLine="hsv.S = hsv.S * SaturationFactor";
_hsv.S /*float*/  = (float) (_hsv.S /*float*/ *_saturationfactor);
 //BA.debugLineNum = 381;BA.debugLine="hsv.H = (hsv.H + HueOffset) Mod 360";
_hsv.H /*int*/  = (int) ((_hsv.H /*int*/ +_hueoffset)%360);
 //BA.debugLineNum = 382;BA.debugLine="bc.SetHSV(x, y, hsv.A, hsv.H, hsv.S, hsv.V)";
_bc._sethsv(_x,_y,(int) (_hsv.A /*float*/ ),_hsv.H /*int*/ ,_hsv.S /*float*/ ,_hsv.V /*float*/ );
 }
};
 }
};
 //BA.debugLineNum = 385;BA.debugLine="Return bc.Bitmap";
if (true) return _bc._getbitmap();
 //BA.debugLineNum = 386;BA.debugLine="End Sub";
return null;
}
public String  _argbtohsv(b4a.example.bitmapcreator._argbcolor _argb,cloyd.smart.home.monitor.bitmapcreatoreffects._hsvcolor _hsv) throws Exception{
int _a = 0;
int _r = 0;
int _g = 0;
int _b = 0;
float _h = 0f;
float _s = 0f;
float _v = 0f;
int _cmax = 0;
int _cmin = 0;
float _rc = 0f;
float _gc = 0f;
float _bc = 0f;
 //BA.debugLineNum = 388;BA.debugLine="Private Sub ARGBToHSV(argb As ARGBColor, HSV As HS";
 //BA.debugLineNum = 389;BA.debugLine="Dim a As Int = argb.a";
_a = _argb.a;
 //BA.debugLineNum = 390;BA.debugLine="Dim r As Int = argb.r";
_r = _argb.r;
 //BA.debugLineNum = 391;BA.debugLine="Dim g As Int = argb.g";
_g = _argb.g;
 //BA.debugLineNum = 392;BA.debugLine="Dim b As Int = argb.b";
_b = _argb.b;
 //BA.debugLineNum = 393;BA.debugLine="Dim h, s, v As Float";
_h = 0f;
_s = 0f;
_v = 0f;
 //BA.debugLineNum = 394;BA.debugLine="Dim cmax As Int = Max(Max(r, g), b)";
_cmax = (int) (__c.Max(__c.Max(_r,_g),_b));
 //BA.debugLineNum = 395;BA.debugLine="Dim cmin As Int = Min(Min(r, g), b)";
_cmin = (int) (__c.Min(__c.Min(_r,_g),_b));
 //BA.debugLineNum = 396;BA.debugLine="v = cmax / 255";
_v = (float) (_cmax/(double)255);
 //BA.debugLineNum = 397;BA.debugLine="If cmax <> 0 Then";
if (_cmax!=0) { 
 //BA.debugLineNum = 398;BA.debugLine="s = (cmax - cmin) / cmax";
_s = (float) ((_cmax-_cmin)/(double)_cmax);
 };
 //BA.debugLineNum = 400;BA.debugLine="If s = 0 Then";
if (_s==0) { 
 //BA.debugLineNum = 401;BA.debugLine="h = 0";
_h = (float) (0);
 }else {
 //BA.debugLineNum = 403;BA.debugLine="Dim rc As Float = (cmax - r) / (cmax - cmin)";
_rc = (float) ((_cmax-_r)/(double)(_cmax-_cmin));
 //BA.debugLineNum = 404;BA.debugLine="Dim gc As Float = (cmax - g) / (cmax - cmin)";
_gc = (float) ((_cmax-_g)/(double)(_cmax-_cmin));
 //BA.debugLineNum = 405;BA.debugLine="Dim bc As Float = (cmax - b) / (cmax - cmin)";
_bc = (float) ((_cmax-_b)/(double)(_cmax-_cmin));
 //BA.debugLineNum = 406;BA.debugLine="If r = cmax Then";
if (_r==_cmax) { 
 //BA.debugLineNum = 407;BA.debugLine="h = bc - gc";
_h = (float) (_bc-_gc);
 }else if(_g==_cmax) { 
 //BA.debugLineNum = 409;BA.debugLine="h = 2 + rc - bc";
_h = (float) (2+_rc-_bc);
 }else {
 //BA.debugLineNum = 411;BA.debugLine="h = 4 + gc - rc";
_h = (float) (4+_gc-_rc);
 };
 //BA.debugLineNum = 413;BA.debugLine="h = h / 6";
_h = (float) (_h/(double)6);
 //BA.debugLineNum = 414;BA.debugLine="If h < 0 Then h = h + 1";
if (_h<0) { 
_h = (float) (_h+1);};
 };
 //BA.debugLineNum = 416;BA.debugLine="HSV.H = h * 360";
_hsv.H /*int*/  = (int) (_h*360);
 //BA.debugLineNum = 417;BA.debugLine="HSV.S = s";
_hsv.S /*float*/  = _s;
 //BA.debugLineNum = 418;BA.debugLine="HSV.V = v";
_hsv.V /*float*/  = _v;
 //BA.debugLineNum = 419;BA.debugLine="HSV.A = a";
_hsv.A /*float*/  = (float) (_a);
 //BA.debugLineNum = 420;BA.debugLine="End Sub";
return "";
}
public anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper  _blur(anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper _bmp) throws Exception{
b4a.example.bitmapcreator _bc = null;
int _reducescale = 0;
int _count = 0;
b4a.example.bitmapcreator._argbcolor[] _clrs = null;
b4a.example.bitmapcreator._argbcolor _temp = null;
int _m = 0;
int _steps = 0;
int _y = 0;
int _x = 0;
anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper _res = null;
 //BA.debugLineNum = 55;BA.debugLine="Public Sub Blur (bmp As B4XBitmap) As B4XBitmap";
 //BA.debugLineNum = 56;BA.debugLine="Dim bc As BitmapCreator";
_bc = new b4a.example.bitmapcreator();
 //BA.debugLineNum = 57;BA.debugLine="Dim ReduceScale As Int = 2";
_reducescale = (int) (2);
 //BA.debugLineNum = 58;BA.debugLine="bc.Initialize(bmp.Width / ReduceScale / bmp.Scale";
_bc._initialize(ba,(int) (_bmp.getWidth()/(double)_reducescale/(double)_bmp.getScale()),(int) (_bmp.getHeight()/(double)_reducescale/(double)_bmp.getScale()));
 //BA.debugLineNum = 59;BA.debugLine="bc.CopyPixelsFromBitmap(bmp)";
_bc._copypixelsfrombitmap(_bmp);
 //BA.debugLineNum = 60;BA.debugLine="Dim count As Int = 3";
_count = (int) (3);
 //BA.debugLineNum = 61;BA.debugLine="Dim clrs(3) As ARGBColor";
_clrs = new b4a.example.bitmapcreator._argbcolor[(int) (3)];
{
int d0 = _clrs.length;
for (int i0 = 0;i0 < d0;i0++) {
_clrs[i0] = new b4a.example.bitmapcreator._argbcolor();
}
}
;
 //BA.debugLineNum = 62;BA.debugLine="Dim temp As ARGBColor";
_temp = new b4a.example.bitmapcreator._argbcolor();
 //BA.debugLineNum = 63;BA.debugLine="Dim m As Int";
_m = 0;
 //BA.debugLineNum = 64;BA.debugLine="For steps = 1 To count";
{
final int step9 = 1;
final int limit9 = _count;
_steps = (int) (1) ;
for (;_steps <= limit9 ;_steps = _steps + step9 ) {
 //BA.debugLineNum = 65;BA.debugLine="For y = 0 To bc.mHeight - 1";
{
final int step10 = 1;
final int limit10 = (int) (_bc._mheight-1);
_y = (int) (0) ;
for (;_y <= limit10 ;_y = _y + step10 ) {
 //BA.debugLineNum = 66;BA.debugLine="For x = 0 To 2";
{
final int step11 = 1;
final int limit11 = (int) (2);
_x = (int) (0) ;
for (;_x <= limit11 ;_x = _x + step11 ) {
 //BA.debugLineNum = 67;BA.debugLine="bc.GetARGB(x, y, clrs(x))";
_bc._getargb(_x,_y,_clrs[_x]);
 }
};
 //BA.debugLineNum = 69;BA.debugLine="SetAvg(bc, 1, y, clrs, temp)";
_setavg(_bc,(int) (1),_y,_clrs,_temp);
 //BA.debugLineNum = 70;BA.debugLine="m = 0";
_m = (int) (0);
 //BA.debugLineNum = 71;BA.debugLine="For x = 2 To bc.mWidth - 2";
{
final int step16 = 1;
final int limit16 = (int) (_bc._mwidth-2);
_x = (int) (2) ;
for (;_x <= limit16 ;_x = _x + step16 ) {
 //BA.debugLineNum = 72;BA.debugLine="bc.GetARGB(x + 1, y, clrs(m))";
_bc._getargb((int) (_x+1),_y,_clrs[_m]);
 //BA.debugLineNum = 73;BA.debugLine="m = (m + 1) Mod clrs.Length";
_m = (int) ((_m+1)%_clrs.length);
 //BA.debugLineNum = 74;BA.debugLine="SetAvg(bc, x, y, clrs, temp)";
_setavg(_bc,_x,_y,_clrs,_temp);
 }
};
 }
};
 //BA.debugLineNum = 77;BA.debugLine="For x = 0 To bc.mWidth - 1";
{
final int step22 = 1;
final int limit22 = (int) (_bc._mwidth-1);
_x = (int) (0) ;
for (;_x <= limit22 ;_x = _x + step22 ) {
 //BA.debugLineNum = 78;BA.debugLine="For y = 0 To 2";
{
final int step23 = 1;
final int limit23 = (int) (2);
_y = (int) (0) ;
for (;_y <= limit23 ;_y = _y + step23 ) {
 //BA.debugLineNum = 79;BA.debugLine="bc.GetARGB(x, y, clrs(y))";
_bc._getargb(_x,_y,_clrs[_y]);
 }
};
 //BA.debugLineNum = 81;BA.debugLine="SetAvg(bc, x, 1, clrs, temp)";
_setavg(_bc,_x,(int) (1),_clrs,_temp);
 //BA.debugLineNum = 82;BA.debugLine="m = 0";
_m = (int) (0);
 //BA.debugLineNum = 83;BA.debugLine="For y = 2 To bc.mHeight - 2";
{
final int step28 = 1;
final int limit28 = (int) (_bc._mheight-2);
_y = (int) (2) ;
for (;_y <= limit28 ;_y = _y + step28 ) {
 //BA.debugLineNum = 84;BA.debugLine="bc.GetARGB(x, y + 1, clrs(m))";
_bc._getargb(_x,(int) (_y+1),_clrs[_m]);
 //BA.debugLineNum = 85;BA.debugLine="m = (m + 1) Mod clrs.Length";
_m = (int) ((_m+1)%_clrs.length);
 //BA.debugLineNum = 86;BA.debugLine="SetAvg(bc, x, y, clrs, temp)";
_setavg(_bc,_x,_y,_clrs,_temp);
 }
};
 }
};
 }
};
 //BA.debugLineNum = 90;BA.debugLine="Dim res As B4XBitmap = bc.Bitmap";
_res = new anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper();
_res = _bc._getbitmap();
 //BA.debugLineNum = 91;BA.debugLine="If ReduceScale > 1 Then res = res.Resize(ReduceSc";
if (_reducescale>1) { 
_res = _res.Resize((int) (_reducescale*_res.getWidth()*_xui.getScale()),(int) (_reducescale*_res.getHeight()*_xui.getScale()),__c.False);};
 //BA.debugLineNum = 92;BA.debugLine="Return res";
if (true) return _res;
 //BA.debugLineNum = 93;BA.debugLine="End Sub";
return null;
}
public anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper  _brightness(anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper _bmp,float _factor) throws Exception{
b4a.example.bitmapcreator _bc = null;
b4a.example.bitmapcreator._argbcolor _argb = null;
int _x = 0;
int _y = 0;
 //BA.debugLineNum = 109;BA.debugLine="Public Sub Brightness(Bmp As B4XBitmap, Factor As";
 //BA.debugLineNum = 110;BA.debugLine="Dim bc As BitmapCreator = CreateBC(Bmp)";
_bc = _createbc(_bmp);
 //BA.debugLineNum = 111;BA.debugLine="Dim argb As ARGBColor";
_argb = new b4a.example.bitmapcreator._argbcolor();
 //BA.debugLineNum = 112;BA.debugLine="For x = 0 To bc.mWidth - 1";
{
final int step3 = 1;
final int limit3 = (int) (_bc._mwidth-1);
_x = (int) (0) ;
for (;_x <= limit3 ;_x = _x + step3 ) {
 //BA.debugLineNum = 113;BA.debugLine="For y = 0 To bc.mHeight - 1";
{
final int step4 = 1;
final int limit4 = (int) (_bc._mheight-1);
_y = (int) (0) ;
for (;_y <= limit4 ;_y = _y + step4 ) {
 //BA.debugLineNum = 114;BA.debugLine="bc.GetARGB(x, y, argb)";
_bc._getargb(_x,_y,_argb);
 //BA.debugLineNum = 115;BA.debugLine="argb.r = Min(255, argb.r * Factor)";
_argb.r = (int) (__c.Min(255,_argb.r*_factor));
 //BA.debugLineNum = 116;BA.debugLine="argb.g = Min(255, argb.g * Factor)";
_argb.g = (int) (__c.Min(255,_argb.g*_factor));
 //BA.debugLineNum = 117;BA.debugLine="argb.b = Min(255, argb.b * Factor)";
_argb.b = (int) (__c.Min(255,_argb.b*_factor));
 //BA.debugLineNum = 118;BA.debugLine="bc.SetARGB(x, y, argb)";
_bc._setargb(_x,_y,_argb);
 }
};
 }
};
 //BA.debugLineNum = 121;BA.debugLine="Return bc.Bitmap";
if (true) return _bc._getbitmap();
 //BA.debugLineNum = 122;BA.debugLine="End Sub";
return null;
}
public String  _class_globals() throws Exception{
 //BA.debugLineNum = 2;BA.debugLine="Sub Class_Globals";
 //BA.debugLineNum = 3;BA.debugLine="Private xui As XUI 'ignore";
_xui = new anywheresoftware.b4a.objects.B4XViewWrapper.XUI();
 //BA.debugLineNum = 4;BA.debugLine="Type BCEPixelGroup (SrcX As Int, SrcY As Int, x A";
;
 //BA.debugLineNum = 5;BA.debugLine="Type HSVColor (H As Int, S As Float, V As Float,";
;
 //BA.debugLineNum = 6;BA.debugLine="Private SleepDuration As Int";
_sleepduration = 0;
 //BA.debugLineNum = 7;BA.debugLine="End Sub";
return "";
}
public b4a.example.bitmapcreator  _createbc(anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper _bmp) throws Exception{
b4a.example.bitmapcreator _bc = null;
 //BA.debugLineNum = 17;BA.debugLine="Private Sub CreateBC(bmp As B4XBitmap) As BitmapCr";
 //BA.debugLineNum = 18;BA.debugLine="Dim bc As BitmapCreator";
_bc = new b4a.example.bitmapcreator();
 //BA.debugLineNum = 19;BA.debugLine="bc.Initialize(bmp.Width / bmp.Scale, bmp.Height /";
_bc._initialize(ba,(int) (_bmp.getWidth()/(double)_bmp.getScale()),(int) (_bmp.getHeight()/(double)_bmp.getScale()));
 //BA.debugLineNum = 20;BA.debugLine="bc.CopyPixelsFromBitmap(bmp)";
_bc._copypixelsfrombitmap(_bmp);
 //BA.debugLineNum = 21;BA.debugLine="Return bc";
if (true) return _bc;
 //BA.debugLineNum = 22;BA.debugLine="End Sub";
return null;
}
public anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper  _drawoutsidemask(anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper _bmp,anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper _mask) throws Exception{
b4a.example.bitmapcreator _source = null;
b4a.example.bitmapcreator _maskbc = null;
b4a.example.bitmapcreator._argbcolor _transparent = null;
b4a.example.bitmapcreator._argbcolor _argb1 = null;
b4a.example.bitmapcreator._argbcolor _argb2 = null;
int _x = 0;
int _y = 0;
 //BA.debugLineNum = 178;BA.debugLine="Public Sub DrawOutsideMask (Bmp As B4XBitmap, Mask";
 //BA.debugLineNum = 179;BA.debugLine="Dim source As BitmapCreator = CreateBC(Bmp)";
_source = _createbc(_bmp);
 //BA.debugLineNum = 180;BA.debugLine="Dim maskbc As BitmapCreator = CreateBC(Mask)";
_maskbc = _createbc(_mask);
 //BA.debugLineNum = 181;BA.debugLine="Dim transparent As ARGBColor";
_transparent = new b4a.example.bitmapcreator._argbcolor();
 //BA.debugLineNum = 182;BA.debugLine="transparent.a = 0";
_transparent.a = (int) (0);
 //BA.debugLineNum = 183;BA.debugLine="Dim argb1, argb2 As ARGBColor";
_argb1 = new b4a.example.bitmapcreator._argbcolor();
_argb2 = new b4a.example.bitmapcreator._argbcolor();
 //BA.debugLineNum = 184;BA.debugLine="For x = 0 To source.mWidth - 1";
{
final int step6 = 1;
final int limit6 = (int) (_source._mwidth-1);
_x = (int) (0) ;
for (;_x <= limit6 ;_x = _x + step6 ) {
 //BA.debugLineNum = 185;BA.debugLine="For y = 0 To source.mHeight - 1";
{
final int step7 = 1;
final int limit7 = (int) (_source._mheight-1);
_y = (int) (0) ;
for (;_y <= limit7 ;_y = _y + step7 ) {
 //BA.debugLineNum = 186;BA.debugLine="If maskbc.IsTransparent(x, y) Then";
if (_maskbc._istransparent(_x,_y)) { 
 }else {
 //BA.debugLineNum = 189;BA.debugLine="maskbc.GetARGB(x, y, argb1)";
_maskbc._getargb(_x,_y,_argb1);
 //BA.debugLineNum = 190;BA.debugLine="If argb1.a = 255 Then";
if (_argb1.a==255) { 
 //BA.debugLineNum = 191;BA.debugLine="source.SetARGB(x, y, transparent)";
_source._setargb(_x,_y,_transparent);
 }else {
 //BA.debugLineNum = 193;BA.debugLine="source.GetARGB(x, y, argb2)";
_source._getargb(_x,_y,_argb2);
 //BA.debugLineNum = 194;BA.debugLine="argb2.a = (argb2.a * (255 - argb1.a)) / 255";
_argb2.a = (int) ((_argb2.a*(255-_argb1.a))/(double)255);
 //BA.debugLineNum = 195;BA.debugLine="source.SetARGB(x, y, argb2)";
_source._setargb(_x,_y,_argb2);
 };
 };
 }
};
 }
};
 //BA.debugLineNum = 200;BA.debugLine="Return source.Bitmap";
if (true) return _source._getbitmap();
 //BA.debugLineNum = 201;BA.debugLine="End Sub";
return null;
}
public anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper  _drawthroughmask(anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper _bmp,anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper _mask) throws Exception{
b4a.example.bitmapcreator _source = null;
b4a.example.bitmapcreator _maskbc = null;
b4a.example.bitmapcreator._argbcolor _transparent = null;
b4a.example.bitmapcreator._argbcolor _argb1 = null;
b4a.example.bitmapcreator._argbcolor _argb2 = null;
int _x = 0;
int _y = 0;
 //BA.debugLineNum = 154;BA.debugLine="Public Sub DrawThroughMask (Bmp As B4XBitmap, Mask";
 //BA.debugLineNum = 155;BA.debugLine="Dim source As BitmapCreator = CreateBC(Bmp)";
_source = _createbc(_bmp);
 //BA.debugLineNum = 156;BA.debugLine="Dim maskbc As BitmapCreator = CreateBC(Mask)";
_maskbc = _createbc(_mask);
 //BA.debugLineNum = 157;BA.debugLine="Dim transparent As ARGBColor";
_transparent = new b4a.example.bitmapcreator._argbcolor();
 //BA.debugLineNum = 158;BA.debugLine="transparent.a = 0";
_transparent.a = (int) (0);
 //BA.debugLineNum = 159;BA.debugLine="Dim argb1, argb2 As ARGBColor";
_argb1 = new b4a.example.bitmapcreator._argbcolor();
_argb2 = new b4a.example.bitmapcreator._argbcolor();
 //BA.debugLineNum = 160;BA.debugLine="For x = 0 To source.mWidth - 1";
{
final int step6 = 1;
final int limit6 = (int) (_source._mwidth-1);
_x = (int) (0) ;
for (;_x <= limit6 ;_x = _x + step6 ) {
 //BA.debugLineNum = 161;BA.debugLine="For y = 0 To source.mHeight - 1";
{
final int step7 = 1;
final int limit7 = (int) (_source._mheight-1);
_y = (int) (0) ;
for (;_y <= limit7 ;_y = _y + step7 ) {
 //BA.debugLineNum = 162;BA.debugLine="If maskbc.IsTransparent(x, y) Then";
if (_maskbc._istransparent(_x,_y)) { 
 //BA.debugLineNum = 163;BA.debugLine="source.SetARGB(x, y, transparent)";
_source._setargb(_x,_y,_transparent);
 }else {
 //BA.debugLineNum = 165;BA.debugLine="maskbc.GetARGB(x, y, argb1)";
_maskbc._getargb(_x,_y,_argb1);
 //BA.debugLineNum = 166;BA.debugLine="If argb1.a < 255 Then";
if (_argb1.a<255) { 
 //BA.debugLineNum = 167;BA.debugLine="source.GetARGB(x, y, argb2)";
_source._getargb(_x,_y,_argb2);
 //BA.debugLineNum = 168;BA.debugLine="argb2.a = (argb2.a * argb1.a) / 255";
_argb2.a = (int) ((_argb2.a*_argb1.a)/(double)255);
 //BA.debugLineNum = 169;BA.debugLine="source.SetARGB(x, y, argb2)";
_source._setargb(_x,_y,_argb2);
 };
 };
 }
};
 }
};
 //BA.debugLineNum = 174;BA.debugLine="Return source.Bitmap";
if (true) return _source._getbitmap();
 //BA.debugLineNum = 175;BA.debugLine="End Sub";
return null;
}
public anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper  _fadeborders(anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper _bmp,int _width) throws Exception{
b4a.example.bitmapcreator _bc = null;
b4a.example.bitmapcreator._argbcolor _argb = null;
int _y = 0;
int _x = 0;
float _f = 0f;
 //BA.debugLineNum = 316;BA.debugLine="Public Sub FadeBorders (bmp As B4XBitmap, Width As";
 //BA.debugLineNum = 317;BA.debugLine="Dim bc As BitmapCreator = CreateBC(bmp)";
_bc = _createbc(_bmp);
 //BA.debugLineNum = 318;BA.debugLine="Dim argb As ARGBColor";
_argb = new b4a.example.bitmapcreator._argbcolor();
 //BA.debugLineNum = 319;BA.debugLine="For y = 0 To bc.mHeight - 1";
{
final int step3 = 1;
final int limit3 = (int) (_bc._mheight-1);
_y = (int) (0) ;
for (;_y <= limit3 ;_y = _y + step3 ) {
 //BA.debugLineNum = 320;BA.debugLine="For x = 0 To Width - 1";
{
final int step4 = 1;
final int limit4 = (int) (_width-1);
_x = (int) (0) ;
for (;_x <= limit4 ;_x = _x + step4 ) {
 //BA.debugLineNum = 321;BA.debugLine="Dim f As Float = (x + 1) / Width";
_f = (float) ((_x+1)/(double)_width);
 //BA.debugLineNum = 322;BA.debugLine="FadePixel(bc, argb, x, y, f)";
_fadepixel(_bc,_argb,_x,_y,_f);
 //BA.debugLineNum = 323;BA.debugLine="FadePixel(bc, argb, bc.mWidth - 1 - x, y, f)";
_fadepixel(_bc,_argb,(int) (_bc._mwidth-1-_x),_y,_f);
 }
};
 }
};
 //BA.debugLineNum = 326;BA.debugLine="For x = 0 To bc.mWidth - 1";
{
final int step10 = 1;
final int limit10 = (int) (_bc._mwidth-1);
_x = (int) (0) ;
for (;_x <= limit10 ;_x = _x + step10 ) {
 //BA.debugLineNum = 327;BA.debugLine="For y = 0 To Width - 1";
{
final int step11 = 1;
final int limit11 = (int) (_width-1);
_y = (int) (0) ;
for (;_y <= limit11 ;_y = _y + step11 ) {
 //BA.debugLineNum = 328;BA.debugLine="Dim f As Float = (y + 1) / Width";
_f = (float) ((_y+1)/(double)_width);
 //BA.debugLineNum = 329;BA.debugLine="FadePixel(bc, argb, x, y, f)";
_fadepixel(_bc,_argb,_x,_y,_f);
 //BA.debugLineNum = 330;BA.debugLine="FadePixel(bc, argb, x, bc.mHeight - 1 - y, f)";
_fadepixel(_bc,_argb,_x,(int) (_bc._mheight-1-_y),_f);
 }
};
 }
};
 //BA.debugLineNum = 333;BA.debugLine="Return bc.Bitmap";
if (true) return _bc._getbitmap();
 //BA.debugLineNum = 334;BA.debugLine="End Sub";
return null;
}
public String  _fadepixel(b4a.example.bitmapcreator _bc,b4a.example.bitmapcreator._argbcolor _argb,int _x,int _y,float _f) throws Exception{
 //BA.debugLineNum = 310;BA.debugLine="Private Sub FadePixel(bc As BitmapCreator, argb As";
 //BA.debugLineNum = 311;BA.debugLine="bc.GetARGB(x, y, argb)";
_bc._getargb(_x,_y,_argb);
 //BA.debugLineNum = 312;BA.debugLine="argb.a = argb.a * f";
_argb.a = (int) (_argb.a*_f);
 //BA.debugLineNum = 313;BA.debugLine="bc.SetARGB(x, y, argb)";
_bc._setargb(_x,_y,_argb);
 //BA.debugLineNum = 314;BA.debugLine="End Sub";
return "";
}
public anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper  _fliphorizontal(anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper _bmp) throws Exception{
b4a.example.bitmapcreator _bc = null;
float _c = 0f;
b4a.example.bitmapcreator._argbcolor _argbleft = null;
b4a.example.bitmapcreator._argbcolor _argbright = null;
int _x = 0;
int _y = 0;
 //BA.debugLineNum = 280;BA.debugLine="Public Sub FlipHorizontal (bmp As B4XBitmap) As B4";
 //BA.debugLineNum = 281;BA.debugLine="Dim bc As BitmapCreator = CreateBC(bmp)";
_bc = _createbc(_bmp);
 //BA.debugLineNum = 282;BA.debugLine="Dim c As Float = (bc.mWidth - 1) / 2";
_c = (float) ((_bc._mwidth-1)/(double)2);
 //BA.debugLineNum = 283;BA.debugLine="Dim argbLeft, argbRight As ARGBColor";
_argbleft = new b4a.example.bitmapcreator._argbcolor();
_argbright = new b4a.example.bitmapcreator._argbcolor();
 //BA.debugLineNum = 284;BA.debugLine="For x = 0 To c - 0.5";
{
final int step4 = 1;
final int limit4 = (int) (_c-0.5);
_x = (int) (0) ;
for (;_x <= limit4 ;_x = _x + step4 ) {
 //BA.debugLineNum = 285;BA.debugLine="For y = 0 To bc.mHeight - 1";
{
final int step5 = 1;
final int limit5 = (int) (_bc._mheight-1);
_y = (int) (0) ;
for (;_y <= limit5 ;_y = _y + step5 ) {
 //BA.debugLineNum = 286;BA.debugLine="bc.GetARGB(x, y, argbLeft)";
_bc._getargb(_x,_y,_argbleft);
 //BA.debugLineNum = 287;BA.debugLine="bc.GetARGB(bc.mWidth -1 - x, y, argbRight)";
_bc._getargb((int) (_bc._mwidth-1-_x),_y,_argbright);
 //BA.debugLineNum = 288;BA.debugLine="bc.SetARGB(x, y,argbRight)";
_bc._setargb(_x,_y,_argbright);
 //BA.debugLineNum = 289;BA.debugLine="bc.SetARGB(bc.mWidth -1 - x, y, argbLeft)";
_bc._setargb((int) (_bc._mwidth-1-_x),_y,_argbleft);
 }
};
 }
};
 //BA.debugLineNum = 292;BA.debugLine="Return bc.Bitmap";
if (true) return _bc._getbitmap();
 //BA.debugLineNum = 293;BA.debugLine="End Sub";
return null;
}
public anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper  _flipvertical(anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper _bmp) throws Exception{
b4a.example.bitmapcreator _bc = null;
float _c = 0f;
b4a.example.bitmapcreator._argbcolor _argbtop = null;
b4a.example.bitmapcreator._argbcolor _argbdown = null;
int _x = 0;
int _y = 0;
 //BA.debugLineNum = 295;BA.debugLine="Public Sub FlipVertical (bmp As B4XBitmap) As B4XB";
 //BA.debugLineNum = 296;BA.debugLine="Dim bc As BitmapCreator = CreateBC(bmp)";
_bc = _createbc(_bmp);
 //BA.debugLineNum = 297;BA.debugLine="Dim c As Float = (bc.mHeight - 1) / 2";
_c = (float) ((_bc._mheight-1)/(double)2);
 //BA.debugLineNum = 298;BA.debugLine="Dim argbTop, argbDown As ARGBColor";
_argbtop = new b4a.example.bitmapcreator._argbcolor();
_argbdown = new b4a.example.bitmapcreator._argbcolor();
 //BA.debugLineNum = 299;BA.debugLine="For x = 0 To bc.mWidth - 1";
{
final int step4 = 1;
final int limit4 = (int) (_bc._mwidth-1);
_x = (int) (0) ;
for (;_x <= limit4 ;_x = _x + step4 ) {
 //BA.debugLineNum = 300;BA.debugLine="For y = 0 To c";
{
final int step5 = 1;
final int limit5 = (int) (_c);
_y = (int) (0) ;
for (;_y <= limit5 ;_y = _y + step5 ) {
 //BA.debugLineNum = 301;BA.debugLine="bc.GetARGB(x, y, argbTop)";
_bc._getargb(_x,_y,_argbtop);
 //BA.debugLineNum = 302;BA.debugLine="bc.GetARGB(x, bc.mHeight - 1 - y, argbDown)";
_bc._getargb(_x,(int) (_bc._mheight-1-_y),_argbdown);
 //BA.debugLineNum = 303;BA.debugLine="bc.SetARGB(x, y,argbDown)";
_bc._setargb(_x,_y,_argbdown);
 //BA.debugLineNum = 304;BA.debugLine="bc.SetARGB(x, bc.mHeight - 1 - y, argbTop)";
_bc._setargb(_x,(int) (_bc._mheight-1-_y),_argbtop);
 }
};
 }
};
 //BA.debugLineNum = 307;BA.debugLine="Return bc.Bitmap";
if (true) return _bc._getbitmap();
 //BA.debugLineNum = 308;BA.debugLine="End Sub";
return null;
}
public anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper  _greyscale(anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper _bmp) throws Exception{
b4a.example.bitmapcreator _bc = null;
b4a.example.bitmapcreator._argbcolor _argb = null;
int _x = 0;
int _y = 0;
int _c = 0;
 //BA.debugLineNum = 24;BA.debugLine="Public Sub GreyScale (bmp As B4XBitmap) As B4XBitm";
 //BA.debugLineNum = 25;BA.debugLine="Dim bc As BitmapCreator = CreateBC(bmp)";
_bc = _createbc(_bmp);
 //BA.debugLineNum = 26;BA.debugLine="Dim argb As ARGBColor";
_argb = new b4a.example.bitmapcreator._argbcolor();
 //BA.debugLineNum = 27;BA.debugLine="For x = 0 To bc.mWidth - 1";
{
final int step3 = 1;
final int limit3 = (int) (_bc._mwidth-1);
_x = (int) (0) ;
for (;_x <= limit3 ;_x = _x + step3 ) {
 //BA.debugLineNum = 28;BA.debugLine="For y = 0 To bc.mHeight - 1";
{
final int step4 = 1;
final int limit4 = (int) (_bc._mheight-1);
_y = (int) (0) ;
for (;_y <= limit4 ;_y = _y + step4 ) {
 //BA.debugLineNum = 29;BA.debugLine="bc.GetARGB(x, y, argb)";
_bc._getargb(_x,_y,_argb);
 //BA.debugLineNum = 30;BA.debugLine="Dim c As Int = argb.r * 0.21 + argb.g * 0.72 +";
_c = (int) (_argb.r*0.21+_argb.g*0.72+0.07*_argb.b);
 //BA.debugLineNum = 31;BA.debugLine="argb.r = c";
_argb.r = _c;
 //BA.debugLineNum = 32;BA.debugLine="argb.g = c";
_argb.g = _c;
 //BA.debugLineNum = 33;BA.debugLine="argb.b = c";
_argb.b = _c;
 //BA.debugLineNum = 34;BA.debugLine="bc.SetARGB(x, y, argb)";
_bc._setargb(_x,_y,_argb);
 }
};
 }
};
 //BA.debugLineNum = 37;BA.debugLine="Return bc.Bitmap";
if (true) return _bc._getbitmap();
 //BA.debugLineNum = 38;BA.debugLine="End Sub";
return null;
}
public anywheresoftware.b4a.keywords.Common.ResumableSubWrapper  _implodeanimated(int _duration,anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper _bmp,anywheresoftware.b4a.objects.B4XViewWrapper _imageview,int _piecesize) throws Exception{
ResumableSub_ImplodeAnimated rsub = new ResumableSub_ImplodeAnimated(this,_duration,_bmp,_imageview,_piecesize);
rsub.resume(ba, null);
return (anywheresoftware.b4a.keywords.Common.ResumableSubWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper(), rsub);
}
public static class ResumableSub_ImplodeAnimated extends BA.ResumableSub {
public ResumableSub_ImplodeAnimated(cloyd.smart.home.monitor.bitmapcreatoreffects parent,int _duration,anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper _bmp,anywheresoftware.b4a.objects.B4XViewWrapper _imageview,int _piecesize) {
this.parent = parent;
this._duration = _duration;
this._bmp = _bmp;
this._imageview = _imageview;
this._piecesize = _piecesize;
}
cloyd.smart.home.monitor.bitmapcreatoreffects parent;
int _duration;
anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper _bmp;
anywheresoftware.b4a.objects.B4XViewWrapper _imageview;
int _piecesize;
b4a.example.bitmapcreator _source = null;
int _numberofsteps = 0;
int _steps = 0;
int _groupsize = 0;
int _w = 0;
int _h = 0;
cloyd.smart.home.monitor.bitmapcreatoreffects._bcepixelgroup[][] _pgs = null;
b4a.example.bitmapcreator _target = null;
int _x = 0;
int _y = 0;
cloyd.smart.home.monitor.bitmapcreatoreffects._bcepixelgroup _pg = null;
anywheresoftware.b4a.objects.B4XCanvas.B4XRect _r = null;
int _i = 0;
int step10;
int limit10;
int step11;
int limit11;
int step35;
int limit35;
int step37;
int limit37;
int step38;
int limit38;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
{
parent.__c.ReturnFromResumableSub(this,null);return;}
case 0:
//C
this.state = 1;
 //BA.debugLineNum = 204;BA.debugLine="Dim source As BitmapCreator = CreateBC(Bmp)";
_source = parent._createbc(_bmp);
 //BA.debugLineNum = 206;BA.debugLine="Dim NumberOfSteps As Int = Duration / 20";
_numberofsteps = (int) (_duration/(double)20);
 //BA.debugLineNum = 207;BA.debugLine="Dim steps As Int = NumberOfSteps";
_steps = _numberofsteps;
 //BA.debugLineNum = 208;BA.debugLine="Dim GroupSize As Int = PieceSize";
_groupsize = _piecesize;
 //BA.debugLineNum = 209;BA.debugLine="Dim w As Int = Bmp.Width / Bmp.Scale / GroupSize";
_w = (int) (_bmp.getWidth()/(double)_bmp.getScale()/(double)_groupsize);
 //BA.debugLineNum = 210;BA.debugLine="Dim h As Int = Bmp.Height / Bmp.Scale / GroupSize";
_h = (int) (_bmp.getHeight()/(double)_bmp.getScale()/(double)_groupsize);
 //BA.debugLineNum = 211;BA.debugLine="Dim pgs(w, h) As BCEPixelGroup";
_pgs = new cloyd.smart.home.monitor.bitmapcreatoreffects._bcepixelgroup[_w][];
{
int d0 = _pgs.length;
int d1 = _h;
for (int i0 = 0;i0 < d0;i0++) {
_pgs[i0] = new cloyd.smart.home.monitor.bitmapcreatoreffects._bcepixelgroup[d1];
for (int i1 = 0;i1 < d1;i1++) {
_pgs[i0][i1] = new cloyd.smart.home.monitor.bitmapcreatoreffects._bcepixelgroup();
}
}
}
;
 //BA.debugLineNum = 212;BA.debugLine="Dim target As BitmapCreator";
_target = new b4a.example.bitmapcreator();
 //BA.debugLineNum = 213;BA.debugLine="target.Initialize(w * GroupSize, h * GroupSize)";
_target._initialize(ba,(int) (_w*_groupsize),(int) (_h*_groupsize));
 //BA.debugLineNum = 214;BA.debugLine="For x = 0 To w - 1";
if (true) break;

case 1:
//for
this.state = 18;
step10 = 1;
limit10 = (int) (_w-1);
_x = (int) (0) ;
this.state = 31;
if (true) break;

case 31:
//C
this.state = 18;
if ((step10 > 0 && _x <= limit10) || (step10 < 0 && _x >= limit10)) this.state = 3;
if (true) break;

case 32:
//C
this.state = 31;
_x = ((int)(0 + _x + step10)) ;
if (true) break;

case 3:
//C
this.state = 4;
 //BA.debugLineNum = 215;BA.debugLine="For y = 0 To h - 1";
if (true) break;

case 4:
//for
this.state = 17;
step11 = 1;
limit11 = (int) (_h-1);
_y = (int) (0) ;
this.state = 33;
if (true) break;

case 33:
//C
this.state = 17;
if ((step11 > 0 && _y <= limit11) || (step11 < 0 && _y >= limit11)) this.state = 6;
if (true) break;

case 34:
//C
this.state = 33;
_y = ((int)(0 + _y + step11)) ;
if (true) break;

case 6:
//C
this.state = 7;
 //BA.debugLineNum = 216;BA.debugLine="Dim pg As BCEPixelGroup = pgs(x, y)";
_pg = _pgs[_x][_y];
 //BA.debugLineNum = 217;BA.debugLine="pg.SrcX = x * GroupSize";
_pg.SrcX /*int*/  = (int) (_x*_groupsize);
 //BA.debugLineNum = 218;BA.debugLine="pg.Srcy = y * GroupSize";
_pg.SrcY /*int*/  = (int) (_y*_groupsize);
 //BA.debugLineNum = 219;BA.debugLine="Select Rnd(0, 4)";
if (true) break;

case 7:
//select
this.state = 16;
switch (BA.switchObjectToInt(parent.__c.Rnd((int) (0),(int) (4)),(int) (0),(int) (1),(int) (2),(int) (3))) {
case 0: {
this.state = 9;
if (true) break;
}
case 1: {
this.state = 11;
if (true) break;
}
case 2: {
this.state = 13;
if (true) break;
}
case 3: {
this.state = 15;
if (true) break;
}
}
if (true) break;

case 9:
//C
this.state = 16;
 //BA.debugLineNum = 221;BA.debugLine="pg.x = 0";
_pg.x /*float*/  = (float) (0);
 //BA.debugLineNum = 222;BA.debugLine="pg.y = Rnd(0, target.mHeight)";
_pg.y /*float*/  = (float) (parent.__c.Rnd((int) (0),_target._mheight));
 if (true) break;

case 11:
//C
this.state = 16;
 //BA.debugLineNum = 224;BA.debugLine="pg.x = target.mWidth - 1";
_pg.x /*float*/  = (float) (_target._mwidth-1);
 //BA.debugLineNum = 225;BA.debugLine="pg.y = Rnd(0, target.mHeight)";
_pg.y /*float*/  = (float) (parent.__c.Rnd((int) (0),_target._mheight));
 if (true) break;

case 13:
//C
this.state = 16;
 //BA.debugLineNum = 227;BA.debugLine="pg.x = Rnd(0, target.mWidth)";
_pg.x /*float*/  = (float) (parent.__c.Rnd((int) (0),_target._mwidth));
 //BA.debugLineNum = 228;BA.debugLine="pg.y = 0";
_pg.y /*float*/  = (float) (0);
 if (true) break;

case 15:
//C
this.state = 16;
 //BA.debugLineNum = 230;BA.debugLine="pg.x = Rnd(0, target.mWidth)";
_pg.x /*float*/  = (float) (parent.__c.Rnd((int) (0),_target._mwidth));
 //BA.debugLineNum = 231;BA.debugLine="pg.y = target.mHeight - 1";
_pg.y /*float*/  = (float) (_target._mheight-1);
 if (true) break;

case 16:
//C
this.state = 34;
;
 //BA.debugLineNum = 234;BA.debugLine="pg.dx = (pg.SrcX - pg.x) / steps";
_pg.dx /*float*/  = (float) ((_pg.SrcX /*int*/ -_pg.x /*float*/ )/(double)_steps);
 //BA.debugLineNum = 235;BA.debugLine="pg.dy = (pg.SrcY - pg.y) / steps";
_pg.dy /*float*/  = (float) ((_pg.SrcY /*int*/ -_pg.y /*float*/ )/(double)_steps);
 if (true) break;
if (true) break;

case 17:
//C
this.state = 32;
;
 if (true) break;
if (true) break;

case 18:
//C
this.state = 19;
;
 //BA.debugLineNum = 238;BA.debugLine="Dim r As B4XRect";
_r = new anywheresoftware.b4a.objects.B4XCanvas.B4XRect();
 //BA.debugLineNum = 239;BA.debugLine="r.Initialize(0, 0, 0, 0)";
_r.Initialize((float) (0),(float) (0),(float) (0),(float) (0));
 //BA.debugLineNum = 240;BA.debugLine="For i = 0 To steps - 1";
if (true) break;

case 19:
//for
this.state = 30;
step35 = 1;
limit35 = (int) (_steps-1);
_i = (int) (0) ;
this.state = 35;
if (true) break;

case 35:
//C
this.state = 30;
if ((step35 > 0 && _i <= limit35) || (step35 < 0 && _i >= limit35)) this.state = 21;
if (true) break;

case 36:
//C
this.state = 35;
_i = ((int)(0 + _i + step35)) ;
if (true) break;

case 21:
//C
this.state = 22;
 //BA.debugLineNum = 241;BA.debugLine="target.FillRect(xui.Color_Transparent, target.Ta";
_target._fillrect(parent._xui.Color_Transparent,_target._targetrect);
 //BA.debugLineNum = 242;BA.debugLine="For x = 0 To w - 1";
if (true) break;

case 22:
//for
this.state = 29;
step37 = 1;
limit37 = (int) (_w-1);
_x = (int) (0) ;
this.state = 37;
if (true) break;

case 37:
//C
this.state = 29;
if ((step37 > 0 && _x <= limit37) || (step37 < 0 && _x >= limit37)) this.state = 24;
if (true) break;

case 38:
//C
this.state = 37;
_x = ((int)(0 + _x + step37)) ;
if (true) break;

case 24:
//C
this.state = 25;
 //BA.debugLineNum = 243;BA.debugLine="For y = 0 To h - 1";
if (true) break;

case 25:
//for
this.state = 28;
step38 = 1;
limit38 = (int) (_h-1);
_y = (int) (0) ;
this.state = 39;
if (true) break;

case 39:
//C
this.state = 28;
if ((step38 > 0 && _y <= limit38) || (step38 < 0 && _y >= limit38)) this.state = 27;
if (true) break;

case 40:
//C
this.state = 39;
_y = ((int)(0 + _y + step38)) ;
if (true) break;

case 27:
//C
this.state = 40;
 //BA.debugLineNum = 244;BA.debugLine="Dim pg As BCEPixelGroup = pgs(x, y)";
_pg = _pgs[_x][_y];
 //BA.debugLineNum = 245;BA.debugLine="pg.x = pg.x + pg.dx";
_pg.x /*float*/  = (float) (_pg.x /*float*/ +_pg.dx /*float*/ );
 //BA.debugLineNum = 246;BA.debugLine="pg.y = pg.y + pg.dy";
_pg.y /*float*/  = (float) (_pg.y /*float*/ +_pg.dy /*float*/ );
 //BA.debugLineNum = 247;BA.debugLine="r.Left = pg.SrcX";
_r.setLeft((float) (_pg.SrcX /*int*/ ));
 //BA.debugLineNum = 248;BA.debugLine="r.Right = pg.SrcX + GroupSize";
_r.setRight((float) (_pg.SrcX /*int*/ +_groupsize));
 //BA.debugLineNum = 249;BA.debugLine="r.Top = pg.SrcY";
_r.setTop((float) (_pg.SrcY /*int*/ ));
 //BA.debugLineNum = 250;BA.debugLine="r.Bottom = pg.SrcY + GroupSize";
_r.setBottom((float) (_pg.SrcY /*int*/ +_groupsize));
 //BA.debugLineNum = 251;BA.debugLine="target.DrawBitmapCreator(source, r, pg.x, pg.y";
_target._drawbitmapcreator(_source,_r,(int) (_pg.x /*float*/ ),(int) (_pg.y /*float*/ ),parent.__c.True);
 if (true) break;
if (true) break;

case 28:
//C
this.state = 38;
;
 if (true) break;
if (true) break;

case 29:
//C
this.state = 36;
;
 //BA.debugLineNum = 254;BA.debugLine="ImageView.SetBitmap(target.Bitmap)";
_imageview.SetBitmap((android.graphics.Bitmap)(_target._getbitmap().getObject()));
 //BA.debugLineNum = 255;BA.debugLine="Sleep(SleepDuration)";
parent.__c.Sleep(parent.getActivityBA(),this,parent._sleepduration);
this.state = 41;
return;
case 41:
//C
this.state = 36;
;
 if (true) break;
if (true) break;

case 30:
//C
this.state = -1;
;
 //BA.debugLineNum = 257;BA.debugLine="ImageView.SetBitmap(Bmp)";
_imageview.SetBitmap((android.graphics.Bitmap)(_bmp.getObject()));
 //BA.debugLineNum = 258;BA.debugLine="Return True";
if (true) {
parent.__c.ReturnFromResumableSub(this,(Object)(parent.__c.True));return;};
 //BA.debugLineNum = 259;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public String  _initialize(anywheresoftware.b4a.BA _ba) throws Exception{
innerInitialize(_ba);
 //BA.debugLineNum = 9;BA.debugLine="Public Sub Initialize";
 //BA.debugLineNum = 10;BA.debugLine="If xui.IsB4i Then";
if (_xui.getIsB4i()) { 
 //BA.debugLineNum = 11;BA.debugLine="SleepDuration = 5";
_sleepduration = (int) (5);
 }else {
 //BA.debugLineNum = 13;BA.debugLine="SleepDuration = 16";
_sleepduration = (int) (16);
 };
 //BA.debugLineNum = 15;BA.debugLine="End Sub";
return "";
}
public anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper  _negate(anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper _bmp) throws Exception{
b4a.example.bitmapcreator _bc = null;
b4a.example.bitmapcreator._argbcolor _argb = null;
int _x = 0;
int _y = 0;
 //BA.debugLineNum = 40;BA.debugLine="Public Sub Negate (bmp As B4XBitmap) As B4XBitmap";
 //BA.debugLineNum = 41;BA.debugLine="Dim bc As BitmapCreator = CreateBC(bmp)";
_bc = _createbc(_bmp);
 //BA.debugLineNum = 42;BA.debugLine="Dim argb As ARGBColor";
_argb = new b4a.example.bitmapcreator._argbcolor();
 //BA.debugLineNum = 43;BA.debugLine="For x = 0 To bc.mWidth - 1";
{
final int step3 = 1;
final int limit3 = (int) (_bc._mwidth-1);
_x = (int) (0) ;
for (;_x <= limit3 ;_x = _x + step3 ) {
 //BA.debugLineNum = 44;BA.debugLine="For y = 0 To bc.mHeight - 1";
{
final int step4 = 1;
final int limit4 = (int) (_bc._mheight-1);
_y = (int) (0) ;
for (;_y <= limit4 ;_y = _y + step4 ) {
 //BA.debugLineNum = 45;BA.debugLine="bc.GetARGB(x, y, argb)";
_bc._getargb(_x,_y,_argb);
 //BA.debugLineNum = 46;BA.debugLine="argb.r = Bit.Xor(argb.r, 0xff)";
_argb.r = __c.Bit.Xor(_argb.r,(int) (0xff));
 //BA.debugLineNum = 47;BA.debugLine="argb.g = Bit.Xor(argb.g, 0xff)";
_argb.g = __c.Bit.Xor(_argb.g,(int) (0xff));
 //BA.debugLineNum = 48;BA.debugLine="argb.b = Bit.Xor(argb.b, 0xff)";
_argb.b = __c.Bit.Xor(_argb.b,(int) (0xff));
 //BA.debugLineNum = 49;BA.debugLine="bc.SetARGB(x, y, argb)";
_bc._setargb(_x,_y,_argb);
 }
};
 }
};
 //BA.debugLineNum = 52;BA.debugLine="Return bc.Bitmap";
if (true) return _bc._getbitmap();
 //BA.debugLineNum = 53;BA.debugLine="End Sub";
return null;
}
public anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper  _pixelate(anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper _bmp,int _boxsize) throws Exception{
b4a.example.bitmapcreator _bc = null;
anywheresoftware.b4a.objects.B4XCanvas.B4XRect _rect = null;
int _x = 0;
int _y = 0;
 //BA.debugLineNum = 124;BA.debugLine="Public Sub Pixelate (Bmp As B4XBitmap, BoxSize As";
 //BA.debugLineNum = 125;BA.debugLine="Dim bc As BitmapCreator = CreateBC(Bmp)";
_bc = _createbc(_bmp);
 //BA.debugLineNum = 126;BA.debugLine="Dim rect As B4XRect";
_rect = new anywheresoftware.b4a.objects.B4XCanvas.B4XRect();
 //BA.debugLineNum = 127;BA.debugLine="rect.Initialize(0, 0, 0, 0)";
_rect.Initialize((float) (0),(float) (0),(float) (0),(float) (0));
 //BA.debugLineNum = 128;BA.debugLine="For x = 0 To bc.mWidth - 1 Step BoxSize";
{
final int step4 = _boxsize;
final int limit4 = (int) (_bc._mwidth-1);
_x = (int) (0) ;
for (;(step4 > 0 && _x <= limit4) || (step4 < 0 && _x >= limit4) ;_x = ((int)(0 + _x + step4))  ) {
 //BA.debugLineNum = 129;BA.debugLine="rect.Left = x";
_rect.setLeft((float) (_x));
 //BA.debugLineNum = 130;BA.debugLine="rect.Width = BoxSize";
_rect.setWidth(_boxsize);
 //BA.debugLineNum = 131;BA.debugLine="For y = 0 To bc.mHeight - 1 Step BoxSize";
{
final int step7 = _boxsize;
final int limit7 = (int) (_bc._mheight-1);
_y = (int) (0) ;
for (;(step7 > 0 && _y <= limit7) || (step7 < 0 && _y >= limit7) ;_y = ((int)(0 + _y + step7))  ) {
 //BA.debugLineNum = 132;BA.debugLine="rect.Top = y";
_rect.setTop((float) (_y));
 //BA.debugLineNum = 133;BA.debugLine="rect.Height = BoxSize";
_rect.setHeight((float) (_boxsize));
 //BA.debugLineNum = 134;BA.debugLine="bc.FillRect(bc.GetColor(Min(bc.mWidth - 1, x +";
_bc._fillrect(_bc._getcolor((int) (__c.Min(_bc._mwidth-1,_x+_boxsize/(double)2)),(int) (__c.Min(_bc._mheight-1,_y+_boxsize/(double)2))),_rect);
 }
};
 }
};
 //BA.debugLineNum = 138;BA.debugLine="Return bc.Bitmap";
if (true) return _bc._getbitmap();
 //BA.debugLineNum = 139;BA.debugLine="End Sub";
return null;
}
public anywheresoftware.b4a.keywords.Common.ResumableSubWrapper  _pixelateanimated(int _duration,anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper _bmp,int _fromboxsize,int _toboxsize,anywheresoftware.b4a.objects.B4XViewWrapper _imageview) throws Exception{
ResumableSub_PixelateAnimated rsub = new ResumableSub_PixelateAnimated(this,_duration,_bmp,_fromboxsize,_toboxsize,_imageview);
rsub.resume(ba, null);
return (anywheresoftware.b4a.keywords.Common.ResumableSubWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper(), rsub);
}
public static class ResumableSub_PixelateAnimated extends BA.ResumableSub {
public ResumableSub_PixelateAnimated(cloyd.smart.home.monitor.bitmapcreatoreffects parent,int _duration,anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper _bmp,int _fromboxsize,int _toboxsize,anywheresoftware.b4a.objects.B4XViewWrapper _imageview) {
this.parent = parent;
this._duration = _duration;
this._bmp = _bmp;
this._fromboxsize = _fromboxsize;
this._toboxsize = _toboxsize;
this._imageview = _imageview;
}
cloyd.smart.home.monitor.bitmapcreatoreffects parent;
int _duration;
anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper _bmp;
int _fromboxsize;
int _toboxsize;
anywheresoftware.b4a.objects.B4XViewWrapper _imageview;
int _steps = 0;
int _sleeplength = 0;
int _delta = 0;
int _i = 0;
int step4;
int limit4;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
{
parent.__c.ReturnFromResumableSub(this,null);return;}
case 0:
//C
this.state = 1;
 //BA.debugLineNum = 142;BA.debugLine="Dim steps As Int = Min(10, Abs(ToBoxSize - FromBo";
_steps = (int) (parent.__c.Min(10,parent.__c.Abs(_toboxsize-_fromboxsize)));
 //BA.debugLineNum = 143;BA.debugLine="Dim SleepLength As Int = Duration / steps";
_sleeplength = (int) (_duration/(double)_steps);
 //BA.debugLineNum = 144;BA.debugLine="Dim delta As Int = Round((ToBoxSize - FromBoxSize";
_delta = (int) (parent.__c.Round((_toboxsize-_fromboxsize)/(double)_steps));
 //BA.debugLineNum = 145;BA.debugLine="For i = FromBoxSize To ToBoxSize Step delta";
if (true) break;

case 1:
//for
this.state = 4;
step4 = _delta;
limit4 = _toboxsize;
_i = _fromboxsize ;
this.state = 5;
if (true) break;

case 5:
//C
this.state = 4;
if ((step4 > 0 && _i <= limit4) || (step4 < 0 && _i >= limit4)) this.state = 3;
if (true) break;

case 6:
//C
this.state = 5;
_i = ((int)(0 + _i + step4)) ;
if (true) break;

case 3:
//C
this.state = 6;
 //BA.debugLineNum = 146;BA.debugLine="ImageView.SetBitmap(Pixelate(Bmp, i))";
_imageview.SetBitmap((android.graphics.Bitmap)(parent._pixelate(_bmp,_i).getObject()));
 //BA.debugLineNum = 147;BA.debugLine="Sleep(SleepLength)";
parent.__c.Sleep(parent.getActivityBA(),this,_sleeplength);
this.state = 7;
return;
case 7:
//C
this.state = 6;
;
 if (true) break;
if (true) break;

case 4:
//C
this.state = -1;
;
 //BA.debugLineNum = 149;BA.debugLine="ImageView.SetBitmap(Pixelate(Bmp, ToBoxSize))";
_imageview.SetBitmap((android.graphics.Bitmap)(parent._pixelate(_bmp,_toboxsize).getObject()));
 //BA.debugLineNum = 150;BA.debugLine="Return True";
if (true) {
parent.__c.ReturnFromResumableSub(this,(Object)(parent.__c.True));return;};
 //BA.debugLineNum = 151;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper  _replacecolor(anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper _bmp,int _oldcolor,int _newcolor,boolean _keepalphalevel) throws Exception{
b4a.example.bitmapcreator _bc = null;
b4a.example.bitmapcreator._argbcolor _oldargb = null;
b4a.example.bitmapcreator._argbcolor _newargb = null;
b4a.example.bitmapcreator._argbcolor _a = null;
int _x = 0;
int _y = 0;
 //BA.debugLineNum = 263;BA.debugLine="Public Sub ReplaceColor (bmp As B4XBitmap, OldColo";
 //BA.debugLineNum = 264;BA.debugLine="Dim bc As BitmapCreator = CreateBC(bmp)";
_bc = _createbc(_bmp);
 //BA.debugLineNum = 265;BA.debugLine="Dim oldargb, newargb, a As ARGBColor";
_oldargb = new b4a.example.bitmapcreator._argbcolor();
_newargb = new b4a.example.bitmapcreator._argbcolor();
_a = new b4a.example.bitmapcreator._argbcolor();
 //BA.debugLineNum = 266;BA.debugLine="bc.ColorToARGB(OldColor, oldargb)";
_bc._colortoargb(_oldcolor,_oldargb);
 //BA.debugLineNum = 267;BA.debugLine="bc.ColorToARGB(NewColor, newargb)";
_bc._colortoargb(_newcolor,_newargb);
 //BA.debugLineNum = 268;BA.debugLine="For x = 0 To bc.mWidth - 1";
{
final int step5 = 1;
final int limit5 = (int) (_bc._mwidth-1);
_x = (int) (0) ;
for (;_x <= limit5 ;_x = _x + step5 ) {
 //BA.debugLineNum = 269;BA.debugLine="For y = 0 To bc.mHeight - 1";
{
final int step6 = 1;
final int limit6 = (int) (_bc._mheight-1);
_y = (int) (0) ;
for (;_y <= limit6 ;_y = _y + step6 ) {
 //BA.debugLineNum = 270;BA.debugLine="bc.GetARGB(x, y, a)";
_bc._getargb(_x,_y,_a);
 //BA.debugLineNum = 271;BA.debugLine="If (KeepAlphaLevel Or a.a = oldargb.a) And a.r";
if ((_keepalphalevel || _a.a==_oldargb.a) && _a.r==_oldargb.r && _a.g==_oldargb.g && _a.b==_oldargb.b) { 
 //BA.debugLineNum = 272;BA.debugLine="newargb.a = a.a";
_newargb.a = _a.a;
 //BA.debugLineNum = 273;BA.debugLine="bc.SetARGB(x, y, newargb)";
_bc._setargb(_x,_y,_newargb);
 };
 }
};
 }
};
 //BA.debugLineNum = 277;BA.debugLine="Return bc.Bitmap";
if (true) return _bc._getbitmap();
 //BA.debugLineNum = 278;BA.debugLine="End Sub";
return null;
}
public String  _setavg(b4a.example.bitmapcreator _bc,int _x,int _y,b4a.example.bitmapcreator._argbcolor[] _clrs,b4a.example.bitmapcreator._argbcolor _temp) throws Exception{
b4a.example.bitmapcreator._argbcolor _c = null;
 //BA.debugLineNum = 95;BA.debugLine="Private Sub SetAvg(bc As BitmapCreator, x As Int,";
 //BA.debugLineNum = 96;BA.debugLine="temp.Initialize";
_temp.Initialize();
 //BA.debugLineNum = 97;BA.debugLine="For Each c As ARGBColor In clrs";
{
final b4a.example.bitmapcreator._argbcolor[] group2 = _clrs;
final int groupLen2 = group2.length
;int index2 = 0;
;
for (; index2 < groupLen2;index2++){
_c = group2[index2];
 //BA.debugLineNum = 98;BA.debugLine="temp.r = temp.r + c.r";
_temp.r = (int) (_temp.r+_c.r);
 //BA.debugLineNum = 99;BA.debugLine="temp.g = temp.g + c.g";
_temp.g = (int) (_temp.g+_c.g);
 //BA.debugLineNum = 100;BA.debugLine="temp.b = temp.b + c.b";
_temp.b = (int) (_temp.b+_c.b);
 }
};
 //BA.debugLineNum = 102;BA.debugLine="temp.a = 255";
_temp.a = (int) (255);
 //BA.debugLineNum = 103;BA.debugLine="temp.r = temp.r / clrs.Length";
_temp.r = (int) (_temp.r/(double)_clrs.length);
 //BA.debugLineNum = 104;BA.debugLine="temp.g = temp.g / clrs.Length";
_temp.g = (int) (_temp.g/(double)_clrs.length);
 //BA.debugLineNum = 105;BA.debugLine="temp.b = temp.b / clrs.Length";
_temp.b = (int) (_temp.b/(double)_clrs.length);
 //BA.debugLineNum = 106;BA.debugLine="bc.SetARGB(x, y, temp)";
_bc._setargb(_x,_y,_temp);
 //BA.debugLineNum = 107;BA.debugLine="End Sub";
return "";
}
public anywheresoftware.b4a.keywords.Common.ResumableSubWrapper  _transitionanimated(int _duration,anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper _frombmp,anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper _tobmp,anywheresoftware.b4a.objects.B4XViewWrapper _imageview) throws Exception{
ResumableSub_TransitionAnimated rsub = new ResumableSub_TransitionAnimated(this,_duration,_frombmp,_tobmp,_imageview);
rsub.resume(ba, null);
return (anywheresoftware.b4a.keywords.Common.ResumableSubWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper(), rsub);
}
public static class ResumableSub_TransitionAnimated extends BA.ResumableSub {
public ResumableSub_TransitionAnimated(cloyd.smart.home.monitor.bitmapcreatoreffects parent,int _duration,anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper _frombmp,anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper _tobmp,anywheresoftware.b4a.objects.B4XViewWrapper _imageview) {
this.parent = parent;
this._duration = _duration;
this._frombmp = _frombmp;
this._tobmp = _tobmp;
this._imageview = _imageview;
}
cloyd.smart.home.monitor.bitmapcreatoreffects parent;
int _duration;
anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper _frombmp;
anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper _tobmp;
anywheresoftware.b4a.objects.B4XViewWrapper _imageview;
b4a.example.bitmapcreator _frombc = null;
b4a.example.bitmapcreator _tobc = null;
b4a.example.bitmapcreator _target = null;
long _starttime = 0L;
long _endtime = 0L;
b4a.example.bitmapcreator._argbcolor _fromclr = null;
b4a.example.bitmapcreator._argbcolor _toclr = null;
float _progress = 0f;
int _x = 0;
int _y = 0;
int step10;
int limit10;
int step11;
int limit11;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
{
parent.__c.ReturnFromResumableSub(this,null);return;}
case 0:
//C
this.state = 1;
 //BA.debugLineNum = 345;BA.debugLine="Dim frombc As BitmapCreator = CreateBC(FromBmp)";
_frombc = parent._createbc(_frombmp);
 //BA.debugLineNum = 346;BA.debugLine="Dim tobc As BitmapCreator = CreateBC(ToBmp)";
_tobc = parent._createbc(_tobmp);
 //BA.debugLineNum = 347;BA.debugLine="Dim target As BitmapCreator";
_target = new b4a.example.bitmapcreator();
 //BA.debugLineNum = 348;BA.debugLine="target.Initialize(frombc.mWidth, frombc.mHeight)";
_target._initialize(ba,_frombc._mwidth,_frombc._mheight);
 //BA.debugLineNum = 349;BA.debugLine="Dim StartTime As Long = DateTime.Now";
_starttime = parent.__c.DateTime.getNow();
 //BA.debugLineNum = 350;BA.debugLine="Dim EndTime As Long = StartTime + Duration";
_endtime = (long) (_starttime+_duration);
 //BA.debugLineNum = 351;BA.debugLine="Dim fromclr, toclr As ARGBColor";
_fromclr = new b4a.example.bitmapcreator._argbcolor();
_toclr = new b4a.example.bitmapcreator._argbcolor();
 //BA.debugLineNum = 352;BA.debugLine="Do While DateTime.Now < EndTime";
if (true) break;

case 1:
//do while
this.state = 12;
while (parent.__c.DateTime.getNow()<_endtime) {
this.state = 3;
if (true) break;
}
if (true) break;

case 3:
//C
this.state = 4;
 //BA.debugLineNum = 353;BA.debugLine="Dim progress As Float = (DateTime.Now - StartTim";
_progress = (float) ((parent.__c.DateTime.getNow()-_starttime)/(double)_duration);
 //BA.debugLineNum = 354;BA.debugLine="For x = 0 To frombc.mWidth - 1";
if (true) break;

case 4:
//for
this.state = 11;
step10 = 1;
limit10 = (int) (_frombc._mwidth-1);
_x = (int) (0) ;
this.state = 13;
if (true) break;

case 13:
//C
this.state = 11;
if ((step10 > 0 && _x <= limit10) || (step10 < 0 && _x >= limit10)) this.state = 6;
if (true) break;

case 14:
//C
this.state = 13;
_x = ((int)(0 + _x + step10)) ;
if (true) break;

case 6:
//C
this.state = 7;
 //BA.debugLineNum = 355;BA.debugLine="For y = 0 To frombc.mHeight - 1";
if (true) break;

case 7:
//for
this.state = 10;
step11 = 1;
limit11 = (int) (_frombc._mheight-1);
_y = (int) (0) ;
this.state = 15;
if (true) break;

case 15:
//C
this.state = 10;
if ((step11 > 0 && _y <= limit11) || (step11 < 0 && _y >= limit11)) this.state = 9;
if (true) break;

case 16:
//C
this.state = 15;
_y = ((int)(0 + _y + step11)) ;
if (true) break;

case 9:
//C
this.state = 16;
 //BA.debugLineNum = 356;BA.debugLine="frombc.GetARGB(x, y, fromclr)";
_frombc._getargb(_x,_y,_fromclr);
 //BA.debugLineNum = 357;BA.debugLine="tobc.GetARGB(x, y, toclr)";
_tobc._getargb(_x,_y,_toclr);
 //BA.debugLineNum = 358;BA.debugLine="toclr.a = fromclr.a + progress * (toclr.a - fr";
_toclr.a = (int) (_fromclr.a+_progress*(_toclr.a-_fromclr.a));
 //BA.debugLineNum = 359;BA.debugLine="toclr.r = fromclr.r + progress * (toclr.r - fr";
_toclr.r = (int) (_fromclr.r+_progress*(_toclr.r-_fromclr.r));
 //BA.debugLineNum = 360;BA.debugLine="toclr.g = fromclr.g + progress * (toclr.g - fr";
_toclr.g = (int) (_fromclr.g+_progress*(_toclr.g-_fromclr.g));
 //BA.debugLineNum = 361;BA.debugLine="toclr.b = fromclr.b + progress * (toclr.b - fr";
_toclr.b = (int) (_fromclr.b+_progress*(_toclr.b-_fromclr.b));
 //BA.debugLineNum = 362;BA.debugLine="target.SetARGB(x, y, toclr)";
_target._setargb(_x,_y,_toclr);
 if (true) break;
if (true) break;

case 10:
//C
this.state = 14;
;
 if (true) break;
if (true) break;

case 11:
//C
this.state = 1;
;
 //BA.debugLineNum = 365;BA.debugLine="ImageView.SetBitmap(target.Bitmap)";
_imageview.SetBitmap((android.graphics.Bitmap)(_target._getbitmap().getObject()));
 //BA.debugLineNum = 366;BA.debugLine="Sleep(SleepDuration)";
parent.__c.Sleep(parent.getActivityBA(),this,parent._sleepduration);
this.state = 17;
return;
case 17:
//C
this.state = 1;
;
 if (true) break;

case 12:
//C
this.state = -1;
;
 //BA.debugLineNum = 368;BA.debugLine="ImageView.SetBitmap(target.Bitmap)";
_imageview.SetBitmap((android.graphics.Bitmap)(_target._getbitmap().getObject()));
 //BA.debugLineNum = 369;BA.debugLine="Return True";
if (true) {
parent.__c.ReturnFromResumableSub(this,(Object)(parent.__c.True));return;};
 //BA.debugLineNum = 370;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public Object callSub(String sub, Object sender, Object[] args) throws Exception {
BA.senderHolder.set(sender);
return BA.SubDelegator.SubNotFound;
}
}
