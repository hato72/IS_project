package routesearch.data.javafile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

/* 路線の情報 */ 
public class Line{
  private String name; //路線名
  //private ArrayList<Station> stationList; //路線に属する駅
  private Station[] stationList; //路線に属する駅
  //private ArrayList<ArrayList<ArrayList<String>>> diagram; //路線の時刻表 
  private ArrayList<String[][]> diagram= new ArrayList<>();//路線の時刻表
  private boolean isLoop = false; //環状を持つかどうか
  private ArrayList<Calendar> spDiaDateList = new ArrayList<>(); //特別な時刻表を用いる日
  private String loopStr; //山手線のみ使用 "外回り" もしくは "内回り",他の路線は""
  private final Company company;//運行会社 今回は同じオブジェクトの"JR東日本"
  /*
  public Line(String name, Station[] stationList,boolean isLoop, String loopStr){
    this.name = name;
    this.stationList = stationList;
    this.diagram = null;
    this.isLoop = isLoop;
    this.spDiaDateList = new ArrayList<Calendar>();
    this.loopStr = loopStr;
  }
  */
  public Line(String name, Station[] stationList,boolean isLoop, String loopStr, Company company){
	    this.name = name;
	    this.stationList = stationList;
	    this.diagram = null;
	    this.isLoop = isLoop;
	    this.spDiaDateList = new ArrayList<Calendar>();
	    this.loopStr = loopStr;
	    this.company = company;
  }

  //diagramに必要なデータが格納されているか確認する.
  public boolean isComplete() {
	  return (diagram!=null);
  }

  //平日は0,休日は1を返す
  public int diaNum(Calendar date){
    int i = 0;
    for(Calendar spDate : spDiaDateList){
      if(date==spDate){
        return i+4;
      }
      i = i + 1;
    }
    // holidayのライブラリが無いため、下に関数isHolidayを定義してある。
    if(isHoliday(date)){
      return 1;
    }

    return weekNum(date);
  }

  /*
  // 祝日の判定
  private boolean isHoliday(Calendar date){
    // 祝日の判定関数を入れる 
    return false;
  }
  */

  private static String holidayData;
  static {
	  try {
		  holidayapi.HolidayData();
		  holidayData = holidayapi.getHolidayData();
	  }catch(IOException e){
		  e.printStackTrace();
	  }
  }
  
  // 祝日の判定
  private boolean isHoliday(Calendar date){
    /* 祝日の判定関数を入れる */
	int year = date.get(Calendar.YEAR);
    int month = date.get(Calendar.MONTH) + 1; 
    int day = date.get(Calendar.DAY_OF_MONTH);
    
    String dateString = String.format("%d-%02d-%02d", year, month, day);
    return holidayData != null && holidayData.contains(dateString);
    //httpの部分だけ別で行って取得したデータだけを使う
  }



  // 土日、平日の判定
  private static int weekNum(Calendar date){
    if(date.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY || 
    		date.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
      return 1;
    }  
    return 0;
  }
  
  public boolean isExistStationName(String stationName) {
	  for(Station s: stationList) {
		  if(stationName.equals(s.getName())) {
			  return true;
		  }
	  }
	  return false;
  }

  /* getter setter */

  public String getName() {
    return name;
  }

  public Station[] getStationList() {
    return stationList;
  }

  public ArrayList<String[][]> getDiagram() {
    return diagram;
  }

  public boolean getIsLoop(){
    return isLoop;
  }

  public ArrayList<Calendar> getSpDiaDateList() {
    return spDiaDateList;
  }

  public String getLoopStr() {
    return loopStr;
  }
  
  public Company getCompany() {
	  return company;
  }
  
  public void setDiagram(ArrayList<String[][]> diagram) {
	    this.diagram = diagram;
  }
}