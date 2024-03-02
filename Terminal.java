package routesearch;

public class Terminal{//終着駅の名前と到着時間を管理する
	private String name;//終着駅の名前　環状線で終着がない場合は空文字""
	private int time;//終着駅に着く時間 30000～265959のint
	
	Terminal(String name, int time){
		this.name = name;
		this.time = time;
	}
	
	public boolean equals(Terminal t) {
		if(t==null) {
			System.out.println("Terminal equals: nullが与えられました");
			return false;
		}
		return t.getName().equals(name) && t.getTime()==time;
	}
	
	//getter
	public String getName() {
		if(name==null) {
			return "";
		}
		return name;
	}
	
	public int getTime() {
		return time;
	}
	
	//setter
	public void setTime(int time) {
		this.time = time;
	}
}