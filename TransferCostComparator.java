package routesearch;
import java.util.Calendar;
import java.util.Comparator;

import routesearch.data.javafile.Station;

public class TransferCostComparator implements Comparator<TransferCost> {
  private final Station goal;
  TransferCostComparator(Station goal){
	  this.goal = goal;
  }
	/*
	 * Collections.sort(open, new TransferCostComparator())が呼び出されると
	 * 
     * compare(tc1,tc2)の返り値が
     * -1のとき tc1 → tc2 の順にソート
     * 0のとき t1, f2 の順は変えない
     *  1のとき tc1 ← tc2 の順にソート
     * される
     */
  public int compare(TransferCost tc1, TransferCost tc2) {
    //累積推定所要時間を比較
  	Calendar estGDate1 = (Calendar)tc1.getTransferResult().getGDate().clone();
  	Calendar estGDate2 = (Calendar)tc2.getTransferResult().getGDate().clone();
    //推定コストの元の型はfloatであるが四捨五入される
  	estGDate1.add(Calendar.SECOND, (int)tc1.getEstSec());
  	estGDate2.add(Calendar.SECOND, (int)tc2.getEstSec());
  	//System.out.println("1. "+tc1.getTransferResult().getGoal().getName()+"駅の到着予想時刻は"+estGDate1.getTime());
  	//System.out.println("2. "+tc2.getTransferResult().getGoal().getName()+"駅の到着予想時刻は"+estGDate2.getTime());
  	int compareResult; 
  	if(estGDate1.get(Calendar.SECOND)==estGDate2.get(Calendar.SECOND)
  			&& estGDate1.get(Calendar.MINUTE)==estGDate2.get(Calendar.MINUTE)
  			&& estGDate1.get(Calendar.HOUR_OF_DAY)==estGDate2.get(Calendar.HOUR_OF_DAY)
  			&& estGDate1.get(Calendar.DAY_OF_WEEK)==estGDate2.get(Calendar.DAY_OF_WEEK)
  			&& estGDate1.get(Calendar.MONTH)==estGDate2.get(Calendar.MONTH)
  			&& estGDate1.get(Calendar.YEAR)==estGDate2.get(Calendar.YEAR)) {
  		if(tc1.getTransferResult().getGoal()!=tc2.getTransferResult().getGoal()) {
  			return tc1.getTransferResult().getGoal().getName().compareTo(tc2.getTransferResult().getGoal().getName());
  		}else {
  			compareResult = 0;
  		}
  	}else if(estGDate1.before(estGDate2)) {
  		return -1;
  	}else if(estGDate1.after(estGDate2)){
  		return 1;
  	}else {
  		compareResult = 0;
  	}
  	
    //累積推定所要時間が同じ場合 (goalが到着駅となるものを後ろにする)
    if (compareResult == 0 ) {
    	TransferResult t1 = tc1.getTransferResult();
    	TransferResult t2 = tc2.getTransferResult();
    	if(t1.getGoal()==goal && t2.getGoal()==goal) {
    		//どちらもgoalに着く場合,続ける
    	}else if(t1.getGoal()==goal) {
    		return 1;//t1.goalのみgoalならt2.goalを優先する順序にする
    	}else if(t2.getGoal()==goal) {
    		return -1;//t2.goalのみgoalならt1.goalを優先する順序にする
    	}
    	
    	TransferResult startT1 = tc1.getTransferResult().clone();//t1で最初に乗る路線の経路が最終的に得られる
    	Integer t1Count = 0;// t1の乗換回数
    	Integer fare1 = 0;// t1の運賃
    	while(startT1.getPrevious()!=null) {
    		if(!startT1.isConnect()) {//直通でなければ乗換回数をインクリメント
    			t1Count++;
    		}
    		if(startT1.getFare()!=-1) {
    			fare1 =+ startT1.getFare();//運賃を算出
    		}
    		startT1 = startT1.getPrevious().clone();
    	}
    	TransferResult startT2 = tc2.getTransferResult().clone();//t2で最初に乗る路線の経路が最終的に得られる
    	Integer t2Count = 0;// t2の乗換回数
    	Integer fare2 = 0;// t2の運賃
    	while(startT2.getPrevious()!=null) {
    		if(!startT2.isConnect()) {//直通でなければ乗換回数をインクリメント
    			t2Count++;
    		}
    		if(startT2.getFare()!=-1) {
    			fare2 =+ startT2.getFare();//運賃を算出
    		}
    		startT2 = startT2.getPrevious().clone();
    	}
    	
    	if(startT1.getSDate().get(Calendar.SECOND)==startT2.getSDate().get(Calendar.SECOND)
      			&& startT1.getSDate().get(Calendar.MINUTE)==startT2.getSDate().get(Calendar.MINUTE)
      			&& startT1.getSDate().get(Calendar.HOUR_OF_DAY)==startT2.getSDate().get(Calendar.HOUR_OF_DAY)
      			&& startT1.getSDate().get(Calendar.DAY_OF_WEEK)==startT2.getSDate().get(Calendar.DAY_OF_WEEK)
      			&& startT1.getSDate().get(Calendar.MONTH)==startT2.getSDate().get(Calendar.MONTH)
      			&& startT1.getSDate().get(Calendar.YEAR)==startT2.getSDate().get(Calendar.YEAR)) {
    		if(Integer.compare(fare1,fare2)!=0) {//発車時刻が同じで運賃が違う場合
        		compareResult = Integer.compare(fare1,fare2);//IC運賃が安い方を優先
        	}else {//発車時刻が同じでIC運賃も同じとき
        		compareResult = Integer.compare(t1Count,t2Count);//乗換回数が少ない方を優先(tc1とtc2が同じならそのままの順序)
        	}
      	}else if(startT1.getSDate().before(startT2.getSDate())) {
    		//T1←T2の順にする startT2の方が発車時刻が早いのでstartT2を優先させる順序にする
    		compareResult = 1;
    	}else if(startT1.getSDate().after(startT2.getSDate())) {
    		//T1→T2の順にする startT1の方が発車時刻が早いのでstartT1を優先させる順序にする
    		compareResult = -1;
    	}else {
    		compareResult = 0;
    	}
    	startT1 = null;
    	startT2 = null;
    }

    return compareResult;
  }
}