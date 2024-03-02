package routesearch;

import java.util.ArrayList;
import java.util.Calendar;

import routesearch.data.javafile.Line;
import routesearch.data.javafile.Station;

/*
TransferResult:
ある一つの路線をまたぐ、出発地(乗換駅)から目的地(乗換駅)までの経路情報
例) 柏→(常磐線)→上野→(山手線)→東京と上野で乗り換える場合、柏→上野区間と上野→東京区間の2つに分けて扱う。
　　この例における柏→上野区間は、start = kashiwa, goal = ueno, ..., isFirstTransfer = true, line = jobanLIneRapid,...
　　上野→東京区間は、dep = ueno, arr = tokyo, ..., isFirstTransfer = false, ...
*/

public class TransferResult {
	private Station start,goal;
	private final Calendar date;
	private int startTime,goalTime;
	//private int terminalTime;//終着に着く時間 30000～265959で表す.山手線など終着がない列車は-1
	private boolean isFirstTransfer = true; // 出発駅から次乗り換えるまでの最初の路線のみtrue,それ以降の路線はfalse
	private Line line;//この経路で使う路線
	private final Terminal terminalData;
	//private String terminalName;//terminalの型をStationからStringに変数名をterminalからterminalName変更( 11/3(他谷))
	private String terminalStr;//"(品川行)"や"(大崎止)"など行き先が入る, 終電以外の山手線は終着がないので"(外回り)"か"(内回り)"が入る ("上り"や"下り"の表示はできない)
	//private int transferMin = -1; //this.start駅で前の路線からこの路線に乗り換えるまでの最短時間(分)、設定しない場合は-1
	private int transferSec = -1; //this.start駅で前の路線からこの路線に乗り換えるまでの最短時間(秒)、設定しない場合は-1	
	private int startNum,goalNum; //何番線から出発するか。何番線に到着するか。設定しない場合は-1
	private boolean isConnect; // 直前の路線と違うが直通運転扱いであるならtrue 他はfalse
	private boolean isCompetTransfer;// 直後のTransferResultとは別の会社の路線に乗り換えてきた場合true、今回の目標ではJRしか扱わないので常にfalseをもらう
	private TransferResult previous = null; //一つ前の乗換経路(ない場合はnull)
	private TransferResult next = null; //次の乗換経路(ない場合はnull)
	private Calendar startDate;//startを出発する実際の日時(年月日時分)
	private Calendar goalDate;//goalに着く実際の日時(年月日時分)
	private ArrayList<TrainID> trainIDList = new ArrayList<>();//乗る列車の列車番号とその列車の時刻表の列インデックス(途中で列車番号が変わる列車があり複数管理できるようArrayList) 万が一時刻表に列車番号が書かれていない場合は空文字("",-2)を与える
	private ArrayList<Station> stationList = new ArrayList<>();//startからgoalに行くまでのこの路線の停車駅(start,goalを含む)(快速などで駅を飛ばす場合も間の駅を含む)
	private ArrayList<Station> realStationList = new ArrayList<>();//GUIではstationListではなくこちらを使用する startからgoalに行くまでのこの列車の実際の停車駅(start,goalを含む)(快速などで駅を飛ばす場合、飛ばした駅は除く)
	private int fare = -1;//this.goalまでのIC運賃(円)未確定なら-1 運賃が確定したらそれまでの区間で未確定だった運賃分も足された値が設定される(今回では経由地がなく、終電後に始発まで待って行く経路でない場合、nextで辿った最後のTransferResultにのみ設定される)
	
	public TransferResult(Station start, Station goal, Calendar date, int startTime, int goalTime, 
				/*boolean isFirstTransfer,*/ Line line, Terminal terminalData, int transferSec, 
				int startNum, int goalNum, boolean isConnect, boolean isCompetTransfer){//terminalの型をStationからStringに変更( 11/3(他谷))
		 	/*
		 	start, goal: 出発駅、到着駅
		 	date: 日付//実際の日付とは異なる場合がある(11/3 0:10の場合11/2)
		 	startTime, goalTime: 列車の出発時間、到着時間。 0:00:00～2:59:59は24:00:00から26:59:59で格納(30000以上265959以下の整数値が与えられる)
		 	isFirstTransfer: 出発駅から次乗り換えるまでの最初の路線のみtrue,それ以降の路線はfalse
		 	line: 何線か
    		terminalData: この列車の終着駅名(終着がない場合はnull)とその終着に着く時間(30000以上265959以下の整数値)(終着がない場合は-1)
    		trensferMin: start駅で前の路線からこの路線に乗り換えるまでの時間(分)、設定しない場合は-1
    		startNum: 何番線から出発するか。設定しない場合は-1
    		goalNum: 何番線に到着するか。設定しない場合は-1
    		isConnect: 直前の路線と違うが直通運転扱いであるならtrue
    		isCompetTransfer: 直後のTransferResultとは別の会社の路線に乗り換えてきた場合true、今回の目標ではJRしか扱わないので常にfalseをもらう
		 	*/
			this.start = start;
			this.goal = goal;
			this.date = date;
			this.startTime = startTime;
			this.goalTime = goalTime;
			//this.terminalTime = terminalData.getTime();
			//this.isFirstTransfer = isFirstTransfer;
			this.line = line;
			if(terminalData==null) {
				System.out.println("new TransferRersult: terminalData = nullが与えられました");
			}
			this.terminalData = terminalData;
			//this.terminalName = terminalData.getName();
			/*
			if(!isFirstTransfer) {//乗換2路線目以降の場合
				if(transferMin < start.getTransferSec()/60) {
					this.transferMin = (int) Math.ceil(start.getTransferSec()/60); //切り上げて分に変換
				}else {
					this.transferMin = transferMin;
				}
			}
			*/
			if(transferSec>=0) {
				this.transferSec = transferSec;
			}
			//if(transferSec<0) {//CONTINUE
				//System.out.println("transferSec = "+transferSec);
				//this.transferSec = start.getTransferSec();//乗換時間を駅の基本の乗換時間に設定60(s)
				//System.out.println("this.transferSec = "+this.transferSec);
			//}
			if(!terminalData.getName().equals("")) {//直通運転の場合などで行き先がStationオブジェクトとして登録されていないことがありうるので、Stringで管理
				String tail = "";
				if(line.getIsLoop()&&line.isExistStationName(terminalData.getName())) {//line.isExistStationNameはterminalNameと同じ名前の駅がlineにあるかどうか確認
					tail = "止";//環状線の終電の場合 池袋止 などと表示させる
				}else {
					tail = "行";//通常の行き先、直通運転の場合なども表示できる 向ヶ丘遊園行 など
				}
				this.terminalStr = "("+terminalData.getName()+tail+")";//"(品川行)"や"(大崎止)"などが入る
			}else {
				this.terminalStr = "("+line.getLoopStr()+")";//"(外回り)"や"(内回り)"が入る (環状線で終点がない場合)
			}
			this.startNum = startNum;
			this.goalNum = goalNum;
			this.isConnect = isConnect;
			this.isCompetTransfer = isCompetTransfer;
			setDate();
	}
	
	private Calendar getDate(int time) {
		int year = date.get(Calendar.YEAR);
    	int clMonth = date.get(Calendar.MONTH);
    	int day = date.get(Calendar.DAY_OF_MONTH);
		int hour,minute,second;
    	
		second = time % 100;
    	
    	time = (time-second)/100;
    	minute = time % 100;
    	
    	time = (time-minute)/100;
    	hour = time % 100; //25時なども1時などにせずそのまま
    	
    	//CalendarクラスはCalendar date = new Calendar();のようには宣言せずこのように宣言するので注意.
    	Calendar date = Calendar.getInstance(); 
    	date.set(year,clMonth,day,hour,minute,second);
    	return date;
	}
	/*
	private static Calendar getDate(Calendar date, int time) {
		int year = date.get(Calendar.YEAR);
    	int clMonth = date.get(Calendar.MONTH);
    	int day = date.get(Calendar.DATE);
		int hour,minute,second;
    	
		second = time % 100;
    	
    	time = (time-second)/100;
    	minute = time % 100;
    	
    	time = (time-minute)/100;
    	hour = time % 100; //25時なども1時などにせずそのまま
    	
    	//CalendarクラスはCalendar date = new Calendar();のようには宣言せずこのように宣言するので注意.
    	Calendar rowDate = Calendar.getInstance(); 
    	rowDate.set(year,clMonth,day,hour,minute,second);
    	return rowDate;
	}
	*/
	private void setDate() {
		startDate = getDate(startTime);
		goalDate = getDate(goalTime);
	}
	
	private String stringValueOf(int week) {
		switch (week) { 
	    case Calendar.SUNDAY:     // Calendar.SUNDAY:1 
	        //日曜日
	        return "日";
	    case Calendar.MONDAY:     // Calendar.MONDAY:2
	        //月曜日
	        return "月";
	    case Calendar.TUESDAY:    // Calendar.TUESDAY:3
	        //火曜日
	        return "火";
	    case Calendar.WEDNESDAY:  // Calendar.WEDNESDAY:4
	        //水曜日
	    	return "水";
	    case Calendar.THURSDAY:   // Calendar.THURSDAY:5
	        //木曜日
	    	return "木";
	    case Calendar.FRIDAY:     // Calendar.FRIDAY:6
	        //金曜日
	    	return "金";
	    case Calendar.SATURDAY:   // Calendar.SATURDAY:7
	        //土曜日
	    	return "土";
	    default:
	    	return "";
		}
	}
	
	private String valueOf2Digits(int n) {
    	if(n<10){
    		return "0"+String.valueOf(n);
    	}
    	return String.valueOf(n);
    }
	

	/*
	//デバッグ用
	//TransferResultのCUI出力
	public void print() {
		System.out.println("");
		//System.out.println("print self");
		System.out.println(getSTime()+" "+start.getName());
		System.out.println("↓ "+line.getName()+" "+terminalStr);
		System.out.println(getGTime()+" "+goal.getName());
		String startDateStr = getSYear()+"年"+getSMonth()+"月"+getSDay()+"日"+getSTime()+"("+getSWeek()+")";
		String goalDateStr;
		TransferResult t = next;
		if(t==null) {
			goalDateStr = getGYear()+"年"+getGMonth()+"月"+getGDay()+"日"+getGTime()+"("+getGWeek()+")";
		}else{
			goalDateStr = printNotFirst(t);
		}
		System.out.println("("+startDateStr+"\n→"+goalDateStr+")");
	}
	*/
	//デバッグ用
	//TransferResult tのCUI出力
	public void print(TransferResult t) {
		System.out.println("");
		String startDateStr;
		if(t==null) {
			System.out.print("print: TransferResultがnullのため出力できない(Errorではない)");
			return;
		}else if(t.next==null) {
			startDateStr = t.getSYear()+"年"+t.getSMonth()+"月"+t.getSDay()+"日"+t.getSTime()+"("+t.getSWeek()+")";
			printCurrent(t);
			System.out.println(Transfer.fare(t)+"円");
			/*
			System.out.println(t.getSTime()+" "+t.start.getName());
			System.out.println("↓ "+t.line.getName()+" "+t.terminalStr);
			System.out.println(t.getGTime()+" "+t.goal.getName());
			System.out.println(t.getGYear()+"年"+t.getGMonth()+"月"+t.getGDay()+"日"+t.getGTime()+"("+t.getGWeek()+") 到着");
			startDateStr = t.getSYear()+"年"+t.getSMonth()+"月"+t.getSDay()+"日"+t.getSTime()+"("+t.getSWeek()+")";
			*/
			String goalDateStr = t.getGYear()+"年"+t.getGMonth()+"月"+t.getGDay()+"日"+t.getGTime()+"("+t.getGWeek()+")";
			
			System.out.println("("+startDateStr+"\n→"+goalDateStr+")");
			return;
		}else {
			startDateStr = t.getSYear()+"年"+t.getSMonth()+"月"+t.getSDay()+"日"+t.getSTime()+"("+t.getSWeek()+")";
			printCurrent(t);
			if(t.getFare()!=-1) {
				System.out.println(t.getFare()+"円");
			}
			print(t.next,startDateStr);
			return;
		}
	}
	
	public void print(TransferResult t, String startDateStr) {
		System.out.println("");
		String goalDateStr;
		//startDateStr = t.getSYear()+"年"+t.getSMonth()+"月"+t.getSDay()+"日"+t.getSTime()+"("+t.getSWeek()+")";
		if(t==null) {
			System.out.print("print: TransferResultがnullのため出力できない(Errorではない)");
			return;
		}else if(t.next==null) {
			printCurrent(t);
			System.out.println(Transfer.fare(t)+"円");
			/*
			System.out.println(t.getSTime()+" "+t.start.getName());
			System.out.println("↓ "+t.line.getName()+" "+t.terminalStr);
			System.out.println(t.getGTime()+" "+t.goal.getName());
			System.out.println(t.getGYear()+"年"+t.getGMonth()+"月"+t.getGDay()+"日"+t.getGTime()+"("+t.getGWeek()+") 到着");
			startDateStr = t.getSYear()+"年"+t.getSMonth()+"月"+t.getSDay()+"日"+t.getSTime()+"("+t.getSWeek()+")";
			*/
			
			goalDateStr = t.getGYear()+"年"+t.getGMonth()+"月"+t.getGDay()+"日"+t.getGTime()+"("+t.getGWeek()+")";
			
			System.out.println("("+startDateStr+"\n→"+goalDateStr+")");
			return;
		}else {
			printCurrent(t);
			if(t.getFare()!=-1) {
				System.out.println(t.getFare()+"円");
			}
			print(t.next,startDateStr);
			return;
		}
	}
	
	private String printNotFirst(TransferResult transfer) {
		TransferResult t = transfer.clone();
		for(int i=0; i<30 && t.next!=null; i++) {//30回も乗り換えすることは基本ないのでループしたときに止まるように制限
			t = t.clone();
			t = t.next;
			if(t.isConnect()) {
				System.out.println(" 直通運転(乗換不要)");
			}else {
				System.out.printf(" 乗換");
				if(t.getTransferMin()!=-1)
					//System.out.println(" "+t.getTransferSec()+"秒");
					System.out.println(" "+t.getTransferMin()+"分");
				System.out.printf("");
			}
			System.out.println(t.getSTime()+" "+t.start.getName());
			System.out.println("↓ "+t.line.getName()+" "+t.terminalStr);
			System.out.println(t.getGTime()+" "+t.goal.getName());			
			System.out.println(t.getGYear()+"年"+t.getGMonth()+"月"+t.getGDay()+"日"+t.getGTime()+"("+t.getGWeek()+") 到着");
			if(i>=29) {
				System.out.println("TransferResultが循環している可能性があるので出力を停止しました");
				break;
			}
		}
		
		String goalDateStr = t.getGYear()+"年"+t.getGMonth()+"月"+t.getGDay()+"日"+t.getGTime()+"("+t.getGWeek()+")";	
		return goalDateStr;
	}
	/*
	//デバッグ用
		//TransferResult tのCUI出力
		public void print(TransferResult transfer) {
			System.out.println("");
			//System.out.println("print any");
			String startDateStr;
			String goalDateStr;
			TransferResult t = transfer.clone();
			if(t==null) {
				System.out.print("print: TransferResultがnullのため出力できない(Errorではない)");
			}else {
				System.out.println(t.getSTime()+" "+t.start.getName());
				System.out.println("↓ "+t.line.getName()+" "+t.terminalStr);
				System.out.println(t.getGTime()+" "+t.goal.getName());
				System.out.println(t.getGYear()+"年"+t.getGMonth()+"月"+t.getGDay()+"日"+t.getGTime()+"("+t.getGWeek()+") 到着");
				startDateStr = t.getSYear()+"年"+t.getSMonth()+"月"+t.getSDay()+"日"+t.getSTime()+"("+t.getSWeek()+")";
				goalDateStr = printNotFirst(t);
				System.out.println("("+startDateStr+"\n→"+goalDateStr+")");
			}
		}
		
		private String printNotFirst(TransferResult transfer) {
			TransferResult t = transfer.clone();
			for(int i=0; i<30 && t.next!=null; i++) {//30回も乗り換えすることは基本ないのでループしたときに止まるように制限
				t = t.clone();
				t = t.next;
				if(t.isConnect()) {
					System.out.println(" 直通運転(乗換不要)");
				}else {
					System.out.printf(" 乗換");
					if(t.getTransferMin()!=-1)
						//System.out.println(" "+t.getTransferSec()+"秒");
						System.out.println(" "+t.getTransferMin()+"分");
					System.out.printf("");
				}
				System.out.println(t.getSTime()+" "+t.start.getName());
				System.out.println("↓ "+t.line.getName()+" "+t.terminalStr);
				System.out.println(t.getGTime()+" "+t.goal.getName());			
				System.out.println(t.getGYear()+"年"+t.getGMonth()+"月"+t.getGDay()+"日"+t.getGTime()+"("+t.getGWeek()+") 到着");
				if(i>=29) {
					System.out.println("TransferResultが循環している可能性があるので出力を停止しました");
					break;
				}
			}
			
			String goalDateStr = t.getGYear()+"年"+t.getGMonth()+"月"+t.getGDay()+"日"+t.getGTime()+"("+t.getGWeek()+")";	
			return goalDateStr;
		}
	*/
	
	
	//TransferResult tのCUI出力
	public void printCurrent(TransferResult transfer) {
		//System.out.println("");
		//System.out.println("print any");
		String startDateStr;
		String goalDateStr;
		TransferResult t = transfer.clone();
		if(t==null) {
			System.out.print("print: TransferResultがnullのため出力できない(Errorではない)");
		}else {
			if(transfer.isConnect) {
				System.out.println("直通運転");
			}
			System.out.println(t.getSTime()+" "+t.start.getName());
			System.out.println("↓ "+t.line.getName()+" "+t.terminalStr);
			System.out.println(t.getGTime()+" "+t.goal.getName());
			//System.out.println(t.getGYear()+"年"+t.getGMonth()+"月"+t.getGDay()+"日"+t.getGTime()+"("+t.getGWeek()+") 到着");
			startDateStr = t.getSYear()+"年"+t.getSMonth()+"月"+t.getSDay()+"日"+t.getSTime()+"("+t.getSWeek()+")";
			goalDateStr = t.getGYear()+"年"+t.getGMonth()+"月"+t.getGDay()+"日"+t.getGTime()+"("+t.getGWeek()+")";
			//System.out.println("("+startDateStr+"\n→"+goalDateStr+")");
		}
	}
	
	public boolean isFirstTransfer() {
		return isFirstTransfer;
	}
	
	public boolean isConnect() {
		return isConnect;
	}
	
	public boolean isCompetTransfer() {
		return isCompetTransfer;
	}
	
	/*
	public boolean isFromToEquals(TransferResult t) {
		return t.getStart()==start && t.getGoal()==goal;
	}
	*/
	public void clearStationList() {
		stationList.clear();
	}
	
	/*
	public void addStation(Station station) {
		stationList.add(station);
	}
	*/
	
	public void addAllStation(ArrayList<Station> stationList) {
		this.stationList.addAll(stationList);
	}
	
	public void addStationFromLine(Station start, Station goal, Line line) {
		//stationList.add(start);
		 ArrayList<Station> tempStationList = new ArrayList<>();
		if(line.getIsLoop()){//環状線の場合
			boolean isExistS = false;
			for(Station s : line.getStationList()) {
				if(s==start || isExistS) {
					isExistS = true;
					tempStationList.add(s);
					if(s==goal) {
						tempStationList.add(s);
						stationList.addAll(tempStationList);
						return;
					}
				}
			}
			if(isExistS) {//二周目
				for(Station s : line.getStationList()) {
					tempStationList.add(s);
					if(s==goal) {
						tempStationList.add(s);
						stationList.addAll(tempStationList);
						return;
					}
				}
				return;  // line.stationListにstartは存在したがgoalが存在しなかった場合
			}else {
				return; // line.stationListにstartが存在しなかった場合
			}
		}else {
			boolean isExistS = false;
			for(Station s : line.getStationList()) {
				if(s==start || isExistS) {
					isExistS = true;
					tempStationList.add(s);
					if(s==goal) {
						tempStationList.add(s);
						stationList.addAll(tempStationList);
						return;
					}
				}
			}
			return; // line.stationListにstartかgoalが存在しなかった場合
		}
	}
	
	public void addRealStation(Station station) {
		realStationList.add(station);
	}
	
	public void addAllRealStation(ArrayList<Station> stationList) {
		realStationList.addAll(stationList);
	}
	
	public void addTrainID(String trainID, int row, int column) {
		trainIDList.add(new TrainID(trainID, row, column));
	}
	
	public void addAllTrainID(ArrayList<TrainID> trainIDList) {
		this.trainIDList.addAll(trainIDList);
		/*
		for(TrainID id : trainIDList) {
			TrainID newId = id.clone();
			this.trainIDList.add(newId);
		}
		*/
	}
	
	//別のオブジェクトとして同じインスタンスを持つTransferResultを作成
	public TransferResult clone() {
		TransferResult clone = new TransferResult(start, goal, date, startTime, goalTime, 
				line, terminalData, transferSec, 
				startNum, goalNum, isConnect, isCompetTransfer);
		clone.setIsFirstTransfer(isFirstTransfer);
		clone.setPrevious(previous);
		clone.setNext(next);
		clone.addAllTrainID(trainIDList);
		clone.addAllStation(stationList);
		clone.addAllRealStation(realStationList);
		clone.setFare(fare);
		return clone;
	}
	
	//getter
	public Station getStart() {
		return start;
	}
	
	public Station getGoal() {
		return goal;
	}
	
	public Calendar getDate() {
		return date;
	}
	
	public int getStartTime() {
		return startTime;
	}
	
	public int getGoalTime() {
		return goalTime;
	}
	
	public Line getLine() {
		return line;
	}
	
	public Terminal getTerminalData() {
		return terminalData;
	}
	
	public int getTerminalTime() {
		return terminalData.getTime();
	}
	
	public String getTerminalName() {
		return terminalData.getName();
	}
	
	public String getTerminalStr() {
		return terminalStr;
	}
	
	public int getTransferMin() {
		if(transferSec<0) {
			return -1;
		}
		return (int)transferSec/60;//切り捨て
	}
	
	public int getTransferSec() {
		return transferSec;
	}
	
	public int getStartNum() {
		return startNum;
	}
	
	public int getGoalNum() {
		return goalNum;
	}
	
	public TransferResult getNext() {
		return next;
	}
	
	public TransferResult getPrevious() {
		return previous;
	}
	
	public int getSYear() {
		return startDate.get(Calendar.YEAR);
	}
	
	public int getSMonth() {
		return startDate.get(Calendar.MONTH)+1;
	}
	
	public int getSDay() {
		return startDate.get(Calendar.DATE);
	}
	
	public int getSWeekNum() {
		return startDate.get(Calendar.DAY_OF_WEEK);
	}
	
	public String getSWeek() {
		return stringValueOf(startDate.get(Calendar.DAY_OF_WEEK));
	}
	
	public int getSHour() {
		return startDate.get(Calendar.HOUR_OF_DAY);
	}
	
	public int getSMinute() {
		return startDate.get(Calendar.MINUTE);
	}
	
	public String getSTime() {
		return valueOf2Digits(startDate.get(Calendar.HOUR_OF_DAY))+":"+valueOf2Digits(startDate.get(Calendar.MINUTE));
	}
	
	public Calendar getSDate() {
		return startDate;
	}
	
	public String getSName() {
		return start.getName();
	}
	
	public int getGYear() {
		return goalDate.get(Calendar.YEAR);
	}
	
	public int getGMonth() {
		return goalDate.get(Calendar.MONTH)+1;
	}
	
	public int getGDay() {
		return goalDate.get(Calendar.DATE);
	}
	
	public int getGWeekNum() {
		return goalDate.get(Calendar.DAY_OF_WEEK);
	}
	
	public String getGWeek() {
		return stringValueOf(goalDate.get(Calendar.DAY_OF_WEEK));
	}
	
	public int getGHour() {
		return goalDate.get(Calendar.HOUR_OF_DAY);
	}
	
	public int getGMinute() {
		return goalDate.get(Calendar.MINUTE);
	}
	
	public String getGTime() {
		return valueOf2Digits(goalDate.get(Calendar.HOUR_OF_DAY))+":"+valueOf2Digits(goalDate.get(Calendar.MINUTE));
	}
	
	public Calendar getGDate() {
		return goalDate;
	}
	
	public String getGName() {
		return goal.getName();
	}
	
	public String getLineName() {
		return line.getName()+" "+terminalStr;
	}
	
	public ArrayList<TrainID> getTrainIDList() {
		return trainIDList;
	}
	
	private TrainID getTrainIDFirst() {
		return trainIDList.get(0);
	}
	
	//startに乗る時点での列車番号 "2366G"など
	public String getTrainIDFirstName() {
		return getTrainIDFirst().getID();
	}
	
	public TrainID getTrainIDLast() {
		return trainIDList.get(trainIDList.size()-1);
	}
	
	//goalに着いた時点での列車番号 "2366G"など
	public String getTrainIDLastName() {
		return getTrainIDLast().getID();
	}
	
	public ArrayList<Station> getStationList() {
		return stationList;
	}
	
	//GUIで表示
	public ArrayList<Station> getRealStationList() {
		return realStationList;
	}
	
	public ArrayList<String> getStationNameList() {
		ArrayList<String> stationNameList = new ArrayList<>();
		for(Station s: stationList) {
			stationNameList.add(s.getName());
		}
		return stationNameList;
	}
	
	public int getFare() {
		return fare;
	}
	
	// previousで辿った最初のTransferResultを返す
	public static TransferResult getFirst(TransferResult t) {
		if(t==null) {
			return t;
		}else if( t.previous==null){
			//t.printCurrent(t);
			//System.out.println("");
			return t;
		}else {
			t.printCurrent(t);
			System.out.println("");
			t.previous.next = t;
			Calendar preGoalDate = (Calendar)t.previous.goalDate.clone();
			preGoalDate.add(Calendar.MINUTE, 1);
			if(!t.isConnect && preGoalDate.before(t.startDate) ||
					(preGoalDate.get(Calendar.SECOND)==t.startDate.get(Calendar.SECOND)
					&& preGoalDate.get(Calendar.MINUTE)==t.startDate.get(Calendar.MINUTE)
					&& preGoalDate.get(Calendar.HOUR_OF_DAY)==t.startDate.get(Calendar.HOUR_OF_DAY)
					&& preGoalDate.get(Calendar.DAY_OF_MONTH)==t.startDate.get(Calendar.DAY_OF_MONTH)
					&& preGoalDate.get(Calendar.YEAR)==t.startDate.get(Calendar.YEAR))) {
				if(t.transferSec<60) {//未設定も含めて
					t.transferSec = 60;
				}
			}
			
			return getFirst(t.previous);
		}
	}	
	
	/*
	// previousで辿った最初のTransferResultを返す
	public static TransferResult getFirst(TransferResult t) {
		if(t==null || t.previous==null) {
			return t;
		}else if(t.previous.previous==null) {
			return t.previous;
		}
		t.print(t);
		TransferResult first = t.clone();
		while(true) {
			//first = first.clone();
			first = first.previous.clone();
			//first = first.previous;
			first.print(first);
			if(first.previous.previous==null) {
				return first.previous;
			}
		}
	}
	*/
	
	//setter
	public void setPrevious(TransferResult previous) {
		this.previous = previous;
	}
	
	public void setNext(TransferResult next) {
		this.next = next;
	}
	
	/*
	public void setTransferMin(int transferMin) {
		this.transferMin = transferMin;
	}
	*/
	
	public void setTransferSec(int transferSec) {
		this.transferSec = transferSec;
	}
	
	public void setIsConnect(boolean isConnect) {
		this.isConnect = isConnect;
	}
	
	public void setIsFirstTransfer(boolean isFirstTransfer) {
		this.isFirstTransfer = isFirstTransfer;
	}
	
	public void setFalseIsFirstTransfer() {
		isFirstTransfer = false;
	}
	
	public void setTerminalTime(int time) {
		terminalData.setTime(time);
	}
	
	public void setFare(int fare) {
		this.fare = fare;
	}
	
	public void setIsCompetTransfer(boolean bool) {
		isCompetTransfer = bool;
	}
	
	/*
	//TrainIDを設定する TransferResult同士を結合させる時に使う
	//同じ路線で同じ列車番号のとき、山手線同士の列車番号が違うが同じ列車のとき、路線が違うが直通運転で列車番号が同じとき　に結合する際に使う
	//TrainIDのリストlist1とTrainID idを受け取り,
	//list1の最後とidの列車番号が同じ場合list1だけをそのままtrainIDListに設定する(時刻表の行列番号はそのままlist1の方を残す)
	//列車番号が違う場合list1にidを付加したリストをtrainIDListに設定する
	public void setCombTrainID(ArrayList<TrainID> list1, TrainID id) {
		trainIDList.clear();
		
		trainIDList.addAll(list1);
		if(!list1.get(list1.size()-1).getID().equals(id.getID())) {//列車番号が違うとき
			trainIDList.add(id);
		}else if() {
			
		}
	}
	*/
	
	// trainIDListを与えられたtrainIDだけにする(trainIDはこの経路のgoalに着く時刻がかかれている時刻表の行列番号とそのときの列車番号をもつ)
	public void setTrainID(TrainID trainID) {
		trainIDList.clear();
		trainIDList.add(trainID);
	}
}