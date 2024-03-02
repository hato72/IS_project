package routesearch;
import java.util.ArrayList;
import java.util.Calendar;

public class TransferCost{
	private final TransferResult transfer;
	//private float waitSec; //真の出発駅から出発するまでの待ち時間(sec)
	//private float cumSec; //累積時間(sec)(真の出発駅から出発するまでの待ち時間を含める。乗換の待ち時間も含む。)
	private final float estSec; //推定所要時間(sec)
	private final Calendar estGoalDate;//推定到着する日付と時刻の情報
	
	TransferCost(TransferResult transfer, float estSec) {
		this.transfer = transfer;
		this.estSec = estSec;
		//Calendar startDate= transfer.getSDate();
		
		
		TransferResult transferClone = transfer.clone();
		while(transferClone.getNext()!=null) {
			transferClone = transferClone.getNext().clone();
		}
		Calendar goalDateClone = Calendar.getInstance();
		Calendar goalDate = transferClone.getGDate();
		//System.out.println(transferClone.getGoal().getName()+": "+goalDate.getTime());
		//System.out.println("estSec: "+estSec);
		goalDateClone.set(goalDate.get(Calendar.YEAR),goalDate.get(Calendar.MONTH),goalDate.get(Calendar.DAY_OF_MONTH),
				goalDate.get(Calendar.HOUR_OF_DAY),goalDate.get(Calendar.MINUTE),goalDate.get(Calendar.SECOND));
		goalDateClone.add(Calendar.SECOND, (int)estSec);//推定所要時間(s)は四捨五入して加える
		estGoalDate = goalDateClone;
		//System.out.println("予想到着時刻: "+estGoalDate.getTime());		
	}
	
	//リスト内にtransfer.goal(到着駅)が同じで到着時間の違う経路がある場合,先頭の経路だけ残し、他は取り除く
	//(到着時間が同じで別の列車の経路はどちらも格納されて返される
	//到着時間も到着駅もその駅に着くとき乗る列車も同じ経路は先頭の経路だけ残し後は消す)
	public static ArrayList<TransferCost> dedupe(ArrayList<TransferCost> list){
		ArrayList<TransferCost> resultList = new ArrayList<>();
		ArrayList<TransferResult> transferResult = new ArrayList<>();
		for(TransferCost tc : list) {
			TransferResult tr = tc.getTransferResult();
			boolean isEqualsExist = false;//重複があればtrueになる
			
			for(TransferResult resTr : transferResult) {
				if(resTr.getGoal() == tr.getGoal()) {
					if(resTr.getGMinute()==tr.getGMinute() && resTr.getGHour()==tr.getGHour() &&
							resTr.getGYear()==tr.getGYear() && resTr.getGMonth()==tr.getGMonth() 
							&& resTr.getGDay()==tr.getGDay()
							&& resTr.getGDate().get(Calendar.SECOND)==tr.getGDate().get(Calendar.SECOND)) {
						if(resTr.getTrainIDLastName().equals(tr.getTrainIDLastName())) {
							//到着時間も到着駅もその駅に着くときに乗る列車もtrとresTrが同じなら
							//重複するのでresultListに追加しない
							//System.out.println(tr.getLine().getName()+"1");
							isEqualsExist = true;
							break;
						}else {
							//同じ駅に同じ時間に着くがresTrとtrが別の列車の場合は消さない
							if(tr.getLine().getName().contains("湘南新宿ライン")
									|| tr.getLine().getName().contains("埼京線")) {
								//System.out.println(tr.getLine().getName()+"ok");
							}
							continue;
						}
					}else {
						//到着駅が同じで到着時間の違う場合は消す
						//System.out.println("resTr.getGDate: "+resTr.getGDate().getTimeInMillis());
						//System.out.println("tr.getGDate: "+tr.getGDate().getTimeInMillis());
						
						//System.out.println(tr.getLine().getName()+"2");
						isEqualsExist = true;
						break;
					}
				}else {
					//trとresTrの到着駅が違う場合は消さない
					continue;
				}
			}
			
			if(!isEqualsExist) {//resultListにある要素と重複しなければresultListに追加
				transferResult.add(tr);
				resultList.add(tc);
				//System.out.println("dedupe: resultList.add("+tr.getGoal().getName()+")");
			}else {
				//tc = null; // ヒープ領域を明示的に解放
			}
		}
		return resultList;
	}
	
	private long getDiffSec(Calendar calendar1, Calendar calendar2) {
        //==== ミリ秒単位での差分算出 ====//
        long diffTime = calendar1.getTimeInMillis() - calendar2.getTimeInMillis();
     
        return diffTime/1000;//秒単位での差分算出
    }
	/*
	void print() {
		if(transfer==null) {
			System.out.println("Exception TransferCost print: transferがnullです");
			return;
		}
		transfer.print(transfer);
		System.out.println("出発するまでの待ち時間+累積所要時間+推定所要時間"
				+"\n="+String.valueOf(waitSec)+String.valueOf(cumSec)+"+"+String.valueOf(estSec)
				+"\n="+String.valueOf(getSumSec())+"(sec)");
	}
	*/
	/*
	public static void printGoal(ArrayList<TransferCost> list) {
		for(TransferCost tc : list) {
			System.out.printf(tc.getTransferResult().getGoal().getName());
			System.out.printf("("+String.valueOf(tc.getSumSec())+"s)");
			
			System.out.printf(" ");
		}
		System.out.println("");
	}
	*/
	public static ArrayList<TransferCost> cloneList(ArrayList<TransferCost> tcList){
		ArrayList<TransferCost> cloneList = new ArrayList<TransferCost>();
		cloneList.addAll(tcList);
		return cloneList;
	}
	
	TransferResult getTransferResult() {
		return transfer;
	}
	/*
	float getSumSec() {
		return waitSec+cumSec+estSec;
	}
	*/
	float getEstSec() {
		return estSec;
	}
	/*
	void setWaitSec(float waitSec) {
		this.waitSec = waitSec;
	}
	*/
	Calendar getEstGoalDate() {
		return estGoalDate;
	}
}