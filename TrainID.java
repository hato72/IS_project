package routesearch;

public class TrainID{//終着駅の名前と到着時間を管理する
	private String trainID;//列車番号の名前　環状線で終着がない場合はnull
	private int row;//駅が時刻表の何行目にかかれているか
	private int column;////この列車が時刻表の何列目にかかれているか
	
	TrainID(String trainID, int row, int column){
		this.trainID = trainID;
		this.row = row;
		this.column = column;
	}
	
	public boolean equals(TrainID id) {
		return id.trainID.equals(this.trainID) 
				&& (id.row==this.row) && (id.column==this.column);
	}
	
	public TrainID clone() {
		return new TrainID(trainID,row,column);
	}
	
	public void print() {
		//System.out.println("列車番号: "+trainID+", row="+row+", column="+column);
	}
	
	public String getID() {
		return trainID;
	}
	
	public int getRow() {
		return row;
	}
	
	public int getColumn() {
		return column;
	}
}