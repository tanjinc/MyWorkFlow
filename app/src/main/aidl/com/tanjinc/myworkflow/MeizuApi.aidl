// MeizuApi.aidl
package com.tanjinc.myworkflow;

// Declare any non-default types here with import statements
interface MeizuApi {
	String getDump();
	boolean sleep(int time);
	boolean type(String str);
	boolean press(String str);
	String adbshell(String cmd);
	boolean hasFocused(String str);
	boolean startActivity(String str);
	String getAllText(boolean isNeedContent);
	boolean searchTextIndex(String str,int index,boolean fresh);
	boolean searchContentTextIndex(String text, int index, boolean fresh);
	boolean searchIdByIndex(String viewIdName,int index,boolean fresh);
	boolean touchIdByIndex(String viewIdName, int index,boolean fresh,boolean even,int times);
	boolean touchTextByIndex(String str,int index,boolean fresh,boolean even,int times);
	boolean touchContentTextByIndex(String text, int index, boolean fresh,boolean even,int times);
	boolean touchTextByAddXY(String text, int index, boolean fresh,boolean even,int times,int addx,int addy);
	boolean touchContentTextByAddXY(String text, int index, boolean fresh,boolean even,int times,int addx,int addy);
	boolean touchIdByAddXY(String viewIdName, int index,boolean fresh,boolean even,int times,int addx,int addy);
	boolean getSwitchIsChecked(String text,int index,boolean fresh,String viewIdName,int times);
	boolean touchSwitchByText(String text,int index,boolean fresh,boolean even,String viewIdName,int wait,int times);
	String getSearchIdNumber(String viewIdName,boolean fresh);
	boolean isTextEnabled(String text,int index,boolean fresh);
	boolean isIdEnabled(String viewIdName,int index,boolean fresh);
	boolean isContentTextEnabled(String text,int index,boolean fresh);
	boolean isViewIdChecked(String viewIdName,int index,boolean fresh);
	boolean touch(int  x,int  y,boolean even,int times);
	boolean dragTo(int  x,int  y,int  x1,int  y1,int times);
	boolean setDeviceType(String str,int language);
	boolean fileSave(String file,String step,boolean result,String stand,String note);
	boolean fileSaveStr(String file,String step,String result,String stand,String note,String photo);
	boolean touchPicIndex(String path,int index,boolean even,int times);
	boolean searchPicIndex(String path,int index);
	String takepicture(int num,String file);
	List<String> getItemVal(String str,int index,boolean fresh,String val);
	boolean zoomSlider( in List<String> xyList);
	boolean multiDrag( in List<String> xyList);
	List<String> getLogcat( String str,int timeout);
	boolean startFpsMem(String pack,String model,String str,int times);
	boolean endFpsMem();
	boolean performAction(int model);
	boolean  setState(String packageName,int opNum,int StateNum);

}
