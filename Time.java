package routesearch;


//時間を 30000～265959 で管理するためのクラス
//基本秒で管理
public class Time {
	private final int BEGIN = 30000;// 1日の始まりの時間
	
	//時刻timeに対してdeltaTime(sec)だけ足した時間を返す
	//deltaTimeは非負を与える.
	public static int plus(int time, int deltaTime) {
		if(deltaTime<0) {
			return time;
		}
		int deltaHour = deltaTime/60/60;
		int deltaMin = (deltaTime-deltaHour*60*60)/60;
		int deltaSec = deltaTime % 60;
		
		TimeData timeData = new TimeData(time);
		timeData.plusSec(deltaSec);
		timeData.plusMin(deltaMin);
		timeData.plusHour(deltaHour);
		
		return timeData.getTime();
	}
	private Time intToTime(int time) {
		return new Time();
	}
}

//時刻データを管理
class TimeData {
	private int sec;
	private int min;
	private int hour;
	 
	TimeData(int time){
		this.sec = time % 100;
		this.min = time/100 % 100;
		this.hour = time/10000;
	}
	
	TimeData(int sec, int min, int hour){
		this.sec = sec;
		this.min = min;
		this.hour = hour;
	}
	
	//与えられるsecは0以上59以下
	public void plusSec(int sec) {
		int tempSec = sec + this.sec;
		int tempMin = min;
		if(tempSec>=60) {
			tempSec = tempSec-60;
			tempMin++;
		}
		this.sec = tempSec;
		this.min = tempMin;
	}
	
	//与えられるminは0以上59以下
	public void plusMin(int min) {
		int tempMin = min + this.min;
		int tempHour = hour;
		if(tempMin>=60) {
			tempMin = tempMin-60;
			tempHour++;
		}
		this.min = tempMin;
		this.hour = tempHour;
	}
	
	//与えられるhourは0以上
	public void plusHour(int hour) {
		this.hour = hour + this.hour;
	}
	
	int getSec() {
		return sec;
	}
	
	int getMin() {
		return min;
	}
	
	int getHour() {
		return hour;
	}
	
	int getTime() {
		return sec + 100*min + 10000*hour;
	}
}