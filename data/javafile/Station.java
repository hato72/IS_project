package routesearch.data.javafile;
import java.util.ArrayList;

/* 駅情報 */
public class Station{
  private String name; //駅名
  private ArrayList<Station> nextSt = new ArrayList<Station>(); //隣り合った駅
  private ArrayList<StationBranch> branch = new ArrayList<StationBranch>(); //接続される区間(グラフ理論でいうところの辺)
  private ArrayList<Integer> numList = new ArrayList<Integer>(); //駅の保有する番線 [1,2,3,4]
  private float x = -1.0f; //地図上の座標x
  private float y = -1.0f; //座標y
  private int transferSec = 60; //通常の乗り換えに要する時間
  private final int sameTransferSec = 0;//同じ路線に乗り換えるときの乗換時間(s)(ホームが同じかすぐ近くにあるため) 
  private ArrayList<PairTransferSec> spTransferSecList= new ArrayList<>();// 東京駅の京葉線から京葉線以外に乗り換える場合に1200(s)にするなど, 特定の路線間での乗換時間を設定する際に使う.
  private ArrayList<Line> lineList = new ArrayList<Line>(); //駅の属する路線


  public Station(String name){
    this.name = name;
  }
  
  public static void printList(ArrayList<Station> stationList) {
  	for(Station s: stationList) {
  		if(s == null) {
  			System.out.printf("null");
  		}else {
  			System.out.printf(s.getName());
  		}
  		System.out.printf(" ");
  	}
  	System.out.println("");
  }

  // 最低限持っていなければならない駅情報の確認
  public boolean isComplete(){ 
    return nextSt.size() >= 1 &&
            branch.size() >= 1 &&
            //numList.size() >= 1 &&
            x >= 0 &&
            y >= 0 &&
            transferSec >= 0 &&
            lineList.size() >0;
  }

  
  //add
  public void addLine(Line line) {// 
	  lineList.add(line);
  }
  
  /* lineName1からlineName2に乗り換える時間を設定 
   * isOverwrite=falseのときはすでに登録されている時間より長い場合にのみ上書きされる
   * isOverwrite=trueのときはすでに登録されている場合にいつでも上書きされる
   */
  public void addTransferSec(String lineName1, String lineName2, int transferSec, boolean isOverwrite, boolean isSecond) {
	  if(lineName1.equals(lineName2)) {
		  return;
	  }
	  
	  for(Line line1 : lineList) {
		  if(line1.getName().equals(lineName1)) {
			 for(Line line2: lineList) {
				 if(line2.getName().equals(lineName2)) {
					 PairTransferSec newPair = new PairTransferSec(line1.getName(),line2.getName(),transferSec);
					 //System.out.println("("+newPair.get1()+", "+newPair.get2()+")");
					 //ArrayList<PairTransferSec> clone = new ArrayList<>();
					 //clone.addAll(spTransferSecList);
					 boolean isExist = false;
					 for(PairTransferSec p : spTransferSecList) {
						 if(newPair.pairEquals(p)) {
							 //System.out.println(newPair.getValue()+"が上書きされます");
							 isExist = true;
							 if (newPair.getValue()>p.getValue() || isOverwrite) {
								 p.setValue(transferSec);
								 return;
							 }
							 break;
						 }
					 }
					 if(!isExist) {
						 spTransferSecList.add(newPair);
						 return;
					 }
				 }
			 }
		  }
	  }
	  return;
  }
  
  public void addTransferMin(String lineName1, String lineName2, int transferMin) {
	  addTransferSec(lineName1, lineName2, transferMin*60,false,false); 	
  }
  
  public void addTransferMin(String lineName1, String lineName2, int transferMin, boolean isOverwrite) {
	  addTransferSec(lineName1, lineName2, transferMin*60, isOverwrite, false); 	
  }
  
//lineNameから別の路線全てに乗り換える時間を一律に設定 すでに登録されている時間より長い場合は上書きされる
  public void addTransferSec(String lineName, int transferSec) {	  
	  for(Line line1 : lineList) {
		  if(line1.getName().equals(lineName)) {
			 for(Line line2: lineList) {
				 if(!line2.getName().equals(lineName)) {
					 PairTransferSec newPair = new PairTransferSec(line1.getName(),line2.getName(),transferSec);
					 //ArrayList<PairTransferSec> clone = new ArrayList<>();
					 //clone.addAll(spTransferSecList);
					 boolean isExist = false;
					 for(PairTransferSec p : spTransferSecList) {
						 if(newPair.pairEquals(p)) {
							 isExist = true;
							 if(newPair.getValue()>p.getValue()) {
								 p.setValue(transferSec);
							 }
							 break;
						 }
					 }
					 if(!isExist) {
						 spTransferSecList.add(newPair);
					 }
				 }
			 }
		  }
	  }
  }
  
  public void addTransferMin(String lineName, int transferMin) {
  	addTransferSec(lineName,transferMin*60);
  }
  
  /* getter setter */

  public void setX(float x) {
    this.x = x;
  }

  public void setY(float y) {
    this.y = y;
  }

  public void setTransferSec(int transferSec) {
	    this.transferSec = transferSec;
  }	  
  
  public void setTransferMin(int transferMin) {
    this.transferSec = transferMin*60;
  }

  public String getName(){
    return name;
  }

  public ArrayList<Station> getNextSt() {
    return nextSt;
  }

  public ArrayList<StationBranch> getBranch() {
    return branch;
  }

  public ArrayList<Integer> getNumList() {
    return numList;
  }

  public float getX() {
    return x;
  }

  public float getY() {
    return y;
  }
  
  //通常の乗換時間を返す
  public int getTransferSec() {
    return transferSec;
  }
  
  //特殊な場合も考慮した乗り換え時間を返す
  public int getTransferSec(Line line1, Line line2) {
	if(line1==null && line2==null) {
		System.out.println("Exception Station.getTransferSec: line1=nullかつline2=null");
		return 0;
	}else if(line1==null) {//line1=nullは乗換せずに路線line2に乗ることを意味し、待ち時間は0(s)
		return 0;
	}
	for(PairTransferSec p : spTransferSecList) {
		 if(p.containsPair(line1.getName(),line2.getName())) {
			 return p.getValue();
		 }
	}
	if(line1==line2) {
		return sameTransferSec;
	}
	return transferSec;
  }

  public ArrayList<Line> getLineList() {
    return lineList;
  }
  
  public ArrayList<PairTransferSec> getSpTransferSecList(){
	  return spTransferSecList;
  }
}