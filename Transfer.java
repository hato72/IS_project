package routesearch;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

import routesearch.data.Data;
import routesearch.data.javafile.Line;
import routesearch.data.javafile.Station;


public class Transfer {
    private final double SPEED = 130;//予測コストに使う列車の最高速度130km/h
    private final double ACCELERATION = 3.3;//予測コストに使う列車の最高加速度3.3km/(h・s)
    private final int STOP_SEC = 15;//(s)予測コストに使う駅で停車する最短時間(s)
    
    //isFrom = trueなら通常の出発時刻指定
    public TransferResult[] minTimeResult(Station start, Station goal, Calendar rowDateTime, int searchNum, boolean isFrom, Data data){
    	if(start==null || goal==null || start==goal) {
    		TransferResult[] none = new TransferResult[searchNum];
    		return none;
        }else if(!start.isComplete() || !goal.isComplete()) {
        	TransferResult[] none = new TransferResult[searchNum];
    		return none;
        }
    	//searchNum = 1; // 現時点では候補1つに固定
        TransferResult[] result = new TransferResult[searchNum];
        //System.out.println("rowDateTime: "+rowDateTime.getTime());
        int time = calendarToTime(rowDateTime);//6桁で表した時間(3:00:00～26:59:59)
        Calendar date = reCalendar(rowDateTime);//dateは日付のみ考慮する(年月日)(時分秒は00:00:00と格納されており、時分秒はdateでなくtimeで管理する)
        //また実際の日付と異なる場合がある(例えば11/3 00:10のとき11/2 24:10として、11/2と入れる(この変換をreCalendarで行う))
        
        //isFrom = trueなら通常の出発時刻指定
        if(isFrom) {
        	//rowDateTime以降出発で最短時間で行ける経路一つを返す 経路がなければnullが返ってくる
        	result[0] = minTimeOneResult(start,goal,date,time,data);
        	/*
        	if(resultArrayList!=null) {
	        	for(int i=0; i<searchNum && i<resultArrayList.size() ; i++) {
	        		result[i] = resultArrayList.get(i);
	        	}
        	}
        	*/
        }else {
        	
        	//rowDateTimeは到着したい時間
        	TransferResult preResult = minTimeOneResult(start,goal,date,time,data).clone();
        	
        	TransferResult preResult1 = preResult.clone();
        	//System.out.println("pre1_first " +  preResult1.getGDate().getTime()); // Tue Dec 19 04:43:00 JST 2023
//        	while(preResult1.getNext() != null) {
//        		preResult1 = preResult1.getNext().clone();
//        	}
        	preResult1 = moveResult(preResult); 
        	
        	//System.out.println("pre1_last " + preResult1.getGDate().getTime()); //pre1 Tue Dec 19 04:43:00 JST 2023
        	
        	//System.out.println("rowDate " + rowDateTime.getTime()); // rowDate Tue Dec 19 03:10:00 JST 2023
        	//System.out.println("date " + date.getTime());
        	//Calendar date_1 = reCalendar(rowDateTime);
        	//System.out.println("date1 " + rowDateTime.getTime()); 
        	
        	//preResult1は電車の到着時間
        	if (!preResult1.getGDate().before(rowDateTime)) { //preResult1がrowDateTimeよりも遅い
        		
        		//1時30をこえていたら1時30に設定
        		if(time >= 253000 && time <= 265900) { 
        			time = 253000;
        			
        			while(!preResult1.getGDate().before(rowDateTime)) {
        				time -= 1000;
        				preResult1 = minTimeOneResult(start,goal,date,time,data).clone();
        				preResult1 = moveResult(preResult1);
        			}
        			
        			while(true) { //preResult1がrowDateTimeよりもはやいときだけ繰り返す
                		
//                		int new_time = TRToInt(preResult2);
//                		int dif_time = time - new_time;
//                		System.out.println("dif_time " + dif_time); //300=5分
//                		nexttime = Time.plus(nexttime, Math.max(dif_time, 1));
//                		System.out.println("nt2 " + nexttime); //nt2 192800
                		
                		
                		time = Time.plus(time, 60); //一分
                		//System.out.println("abcd" + nexttime); 
                		
                		TransferResult preResult2 = minTimeOneResult(start,goal,date,time,data);
                		TransferResult preResult3 = preResult2.clone();
//                    	while(preResult3.getNext() != null) {
//                    		preResult3 = preResult3.getNext().clone();
//                    	}
                		preResult3 = moveResult(preResult3);
                		System.out.println("pre4" + preResult3.getGDate().getTime()); 
                    	
                		if (!preResult3.getGDate().before(rowDateTime)){
                			break;
                		}
                		preResult1 = preResult3; //まだあればpreResult1を更新
                		
                		//System.out.println("pre2_rec" + preResult2.getGDate().getTime()); 
                		
            		}
                	 
                	result[0] = TransferResult.getFirst(preResult1);
                	
        		}// 3じから4じなら一日まえにずらして１じはんに
        		else if(time <= 40000 && time >= 30000) {
        			date.add(Calendar.DAY_OF_MONTH, -1);
        			time = 253000;
        			System.out.println(time);
        			while(!preResult1.getGDate().before(rowDateTime)) {
        				time -= 1000;
        				preResult1 = minTimeOneResult(start,goal,date,time,data).clone();
        				preResult1 = moveResult(preResult1);
        				System.out.println(preResult1.getGDate().getTime()); //pre1_last Tue Dec 19 00:42:00 JST 2023でぬける
        			}
        			
        			while(true) { //preResult1がrowDateTimeよりもはやいときだけ繰り返す
                		
//                		int new_time = TRToInt(preResult2);
//                		int dif_time = time - new_time;
//                		System.out.println("dif_time " + dif_time); //300=5分
//                		nexttime = Time.plus(nexttime, Math.max(dif_time, 1));
//                		System.out.println("nt2 " + nexttime); //nt2 192800
                		
                		
                		time = Time.plus(time, 60); //一分
                		//System.out.println("abcd" + nexttime); 
                		
                		TransferResult preResult2 = minTimeOneResult(start,goal,date,time,data);
                		TransferResult preResult3 = preResult2.clone();
//                    	while(preResult3.getNext() != null) {
//                    		preResult3 = preResult3.getNext().clone();
//                    	}
                		preResult3 = moveResult(preResult3);
                		System.out.println("pre4" + preResult3.getGDate().getTime()); 
                    	
                		if (!preResult3.getGDate().before(rowDateTime)){
                			break;
                		}
                		preResult1 = preResult3; //まだあればpreResult1を更新
                		
                		//System.out.println("pre2_rec" + preResult2.getGDate().getTime()); 
                		
            		}
                	 
                	result[0] = TransferResult.getFirst(preResult1);
        		}
        		else {
        			TransferResult preResult_1 = minTimeOneResult(start,goal,date,time,data).clone();
            		System.out.println("pre1_last " + preResult_1.getGDate().getTime()); 
            		        		        		
            		//Calendar ca = reCalendar(preResult1.getGDate());
            		int newtime = TRToInt(preResult_1); //
            		int diftime = newtime - time; //
            		int nexttime = 0;
            		int timeper = time % 10000; //timeの下四桁を取り出す
            		if(timeper - diftime >=0) { //timeの下四桁がdiftimeよりも大きいとき
            			nexttime = time-diftime;
            		}
            		else {
            			nexttime = time-diftime-4000;
            		}
            		
            		//System.out.println("next_time " + nexttime);//nexttime 13700 1じ37分
            		
            		TransferResult preResult2 = minTimeOneResult(start,goal,date,nexttime,data);//rowDateTimeよりもdiftime分だけはやく出発した
            		preResult2 = moveResult(preResult2);
            		
            		//System.out.println("pre2 " + preResult2.getGDate().getTime()); //pre2 Tue Dec 19 04:43:00 JST 2023 
            		//preResult2の時点でrowDateTimeを超えていた場合の判定がない
//            		int n_time = TRToInt(preResult2);
//            		while(n_time > time) {
//            			n_time -= diftime;
//            		}
                	
            		while(!preResult2.getGDate().before(rowDateTime)) { //rowDateTimeよりおそいときだけ
            			int n_time = TRToInt(preResult2); 
            			int dif_time = n_time-time; 
            			nexttime -= dif_time;
            			System.out.println("nexttime1 " + nexttime); 
            			
            			
            			preResult2 = minTimeOneResult(start,goal,date,nexttime,data).clone();
            			preResult2 = moveResult(preResult2);
            			//System.out.println("pre2_re " + preResult2.getGDate().getTime()); 
            			
            		}
            		
        		
            		while(true) { //preResult2がrowDateTimeよりもはやいときだけ繰り返す
                		
//                		int new_time = TRToInt(preResult2);
//                		int dif_time = time - new_time;
//                		System.out.println("dif_time " + dif_time); //300=5分
//                		nexttime = Time.plus(nexttime, Math.max(dif_time, 1));
//                		System.out.println("nt2 " + nexttime); //nt2 192800
                		
                		
                		nexttime = Time.plus(nexttime, 60); //一分
                		//System.out.println("abcd" + nexttime); 
                		
                		TransferResult preResult3 = minTimeOneResult(start,goal,date,nexttime,data);
                		TransferResult preResult4 = preResult3.clone();
//                    	while(preResult3.getNext() != null) {
//                    		preResult3 = preResult3.getNext().clone();
//                    	}
                		preResult4 = moveResult(preResult4);
                		System.out.println("pre4" + preResult4.getGDate().getTime()); 
                    	
                		if (!preResult4.getGDate().before(rowDateTime)){
                			break;
                		}
                		preResult2 = preResult3; //まだあればpreResult2を更新
                		
                		//System.out.println("pre2_rec" + preResult2.getGDate().getTime()); 
                		
            		}
                	 
                	result[0] = TransferResult.getFirst(preResult2);
        		}
        		
        	}
        	else { //preResult1がrowDateTimeより速い
        		TransferResult preResult2 = preResult1.clone();
//            	while(preResult2.getNext() != null) {
//            		preResult2 = preResult2.getNext().clone();
//            	}
        		preResult2 = moveResult(preResult2);
        		//System.out.println("pre2_else " + preResult2.getGDate().getTime()); 
        		
        		while(true) { //電車の到着時間がrowDateTimeよりもはやいときだけ繰り返す
        			time = Time.plus(time, 60); //一分
        			
            		TransferResult preResult3 = minTimeOneResult(start,goal,date,time,data);
            		TransferResult preResult4 = preResult3.clone();
//                	while(preResult3.getNext() != null) {
//                		preResult3 = preResult3.getNext().clone();
//                	}
            		preResult4 = moveResult(preResult4);
                	
            		if (!preResult4.getGDate().before(rowDateTime)){
            			break;
            		}
            		preResult2 = preResult3;
            	}
            	result[0] = TransferResult.getFirst(preResult2);
        		
        	}
        	//preResult1.getGDate() - rowDateTime < 0になるようなpreResult1をかえしたい
        	/*
        	 * ---服部担当---
        	 * ここを埋めてpreResult.goalDateがrowDateTimeより早くなるようにする
        	 * つまりpreResult.goalDate.before(rowDateTime)==trueとなるようなpreResultを求める
        	 */
       
        }
       /*
        if(result[0]!=null) {
            //System.out.println("result is not null");
        }
        */
        return result;
        
    }
    
    private int TRToInt(TransferResult preResult) { //preResultを受け取って6桁のint型で返す
    	Calendar calendar = preResult.getGDate();

        // Calendar オブジェクトの get メソッドを使用して時分秒だけを取り出す
        int hour = calendar.get(Calendar.HOUR_OF_DAY); // 24 時間制
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        // 取り出した時分秒を文字列に結合
        String formattedTime = String.format("%02d%02d%02d", hour, minute, second);

        // 文字列を int に変換
        int intTime = Integer.parseInt(formattedTime);
        return intTime;
    }
    
    private TransferResult moveResult(TransferResult result) { //追加
        while (result.getNext() != null) {
            result = result.getNext().clone();
        }
        return result;
    }

    //最短時間経路を返す.なければnullを返す
    private TransferResult minTimeOneResult(Station start, Station goal, Calendar date,int time, Data data){
        if(start==null || goal==null || start==goal) {
        	return null;
        }
        
        ArrayList<TransferCost> open = new ArrayList<>();//未探索の経路
        ArrayList<Station> close = new ArrayList<>();//探索済みの駅

        //ArrayList<TransferCost> resultTCost = new ArrayList<>();
        boolean isFindGoal = false;
        close.add(start);
        //出発駅から1駅となりを探索
        ArrayList<TransferResult> nextTransferList = firstOneSearch(start,goal,null,date,time,close,data,null);
        for(TransferResult tr : nextTransferList) {
            /* firstOneSearchについて
             * startから1つ隣の駅に行く各最短経路TransferCostをopenリストに追加
             * ここでのみ最後の引数にtrueを与える.
             */
            float estSec = estCost(tr.getGoal(),goal,data);//最後の引数2はstartとその隣の駅の2駅
            TransferCost tc = new TransferCost(tr,estSec);
            //resultTCost.add(tc);
            open.add(tc);
            if(!isFindGoal && tc.getTransferResult().getGoal()==goal) {
            	isFindGoal = true;
                System.out.println("目的地までの経路が見つかりました.");
            }
        }    

        if(open.size()==0) {
            System.out.println(start.getName()+"駅から行ける駅が見つかりません.");
            return null;
        }
     
        Collections.sort(open, new TransferCostComparator(goal));//openリストをソート
        
        //同じ駅に着くルートが既にopenにあった場合は、累積推定所要時間が最も小さいものだけ残す
        //累計推定所要時間が最も小さいものが複数ある場合(違う路線で同じ駅に同じ時間に着く場合)は全てopenに残す
        open = TransferCost.dedupe(open);//TransferCostの重複を取り除く openは早い順にソート済みなので重複する場合は最も早い方が残る
        
        
        //boolean isShonan = false;
        //boolean isSaikyo = false;
        //出発駅から2駅以上離れている駅を探索
        while(open.size()>0) {//目的地までの最短時間経路を見つける. open.size()=0になったときはgoalまでの経路が存在しないこと意味する.
        	/*//デバッグ用
        	System.out.println("vvvvvvvvvvvvvvvvvvvvvvvvvv");
        	System.out.println("open.size() = "+open.size());  	
        	//boolean isOsaki = false;
        	int i = 1;
        	for(TransferCost tc : open) {
        		if(tc.getEstGoalDate()==null) {
        			continue;
        		}
        		System.out.println((i++)+": "+tc.getTransferResult().getGoal().getName()+"("+tc.getEstGoalDate().getTime()+")");
        		System.out.println("実際の到着時間: "+tc.getTransferResult().getLine().getName()+" "+tc.getTransferResult().getGoal().getName()+"("+tc.getTransferResult().getGDate().getTime()+")");
        		
        		if(tc.getTransferResult().getGoal().getName().equals("大崎")) {
        			//isFirstOsaki = true;
        			//isOsaki = true;
        			try {
        				//System.out.println("実際の到着時間: "+tc.getTransferResult().getGoal().getName()+"("+tc.getTransferResult().getGDate().getTime()+")");
        				Thread.sleep(1); // 一定時間処理を止める
        			} catch (InterruptedException e) {
        			}
        		}
        		
        	}
        	
        	//if(isFirstOsaki && !isOsaki) {
        		//System.exit(0);
        	//}
        	
        	System.out.println("==========================");
			*/
        	
        	if(isFindGoal) {
                if(open.get(0).getTransferResult().getGoal()==goal) {
                	//System.out.println("open.get(0).transfer.goal: "+open.get(0).getTransferResult().getGoal().getName());
                	//System.out.println("goal: "+goal.getName());
                	//到着までの経路が最短結果ならreturn(openは早い経路順になっているので先頭の要素がgoalかを判別)
                	//ArrayList <TransferResult> resultList = new ArrayList<>();
                	
                	TransferResult resultLast1 = open.get(0).getTransferResult().clone();
                	resultLast1.setFare(fare(resultLast1));
                	//resultList.add(TransferResult.getFirst(resultLast1));
                	
                	//resultLast.print(resultLast1);
                	//System.out.println("");
                	/*
                	for(int j=1; j<open.size() ;j++) {
                		TransferResult resultLastj = open.get(j).getTransferResult().clone();
                		if(resultLastj.getGoal()==goal 
                				&& resultLastj.getGDate().get(Calendar.SECOND)==resultLast1.getGDate().get(Calendar.SECOND)
                				&& resultLastj.getGMinute()==resultLast1.getGMinute()
                				&& resultLastj.getGHour()==resultLast1.getGHour()
                				&& resultLastj.getGDay()==resultLast1.getGDay()
                				&& resultLastj.getGMonth()==resultLast1.getGMonth()
                				&& resultLastj.getGYear()==resultLast1.getGYear()) {
                			resultLastj.setFare(fare(resultLastj));
                        	resultList.add(TransferResult.getFirst(open.get(j).getTransferResult()));
                		}       	
                	}
                	*/
                	return TransferResult.getFirst(resultLast1);
                }
            }
        	//TransferCost openFirst = open.get(0);
            if(close.contains(open.get(0).getTransferResult().getGoal())) {//デバッグ用
            	System.out.println("open.get(0).transfer.goal: "
            			+ open.get(0).getTransferResult().getGoal().getName());
            	System.out.printf("close: ");
            	Station.printList(close);
            	System.out.println("Exeption: 既に探索済みの"
            			+ open.get(0).getTransferResult().getGoal().getName()
            			+"駅を探索しようとしました."
                        + "\n oneSearch内でcloseが探索候補から除外されていない可能性があります.");
                //continue;
            	return null;
            }
            
            close.add(open.get(0).getTransferResult().getGoal());
            
            TransferCost openFirst = open.get(0);
            ArrayList<TransferCost> resultList = oneSearch(open.get(0).getTransferResult(),goal,close,data);//open.get(0).transfer.goalから1つ隣の駅(closeに入ってる駅は除く)に行く各最短経路TransferCostをopenリストに追加
            open.remove(0);//探索し終えたopenの最初の要素を削除する
            //int i = 1;
            ArrayList<TransferCost> openClone = TransferCost.cloneList(open);
            for(TransferCost tc : openClone) { 
            	if(tc.getTransferResult().getGDate().get(Calendar.SECOND)==openFirst.getTransferResult().getGDate().get(Calendar.SECOND)
              			&& tc.getTransferResult().getGDate().get(Calendar.MINUTE)==openFirst.getTransferResult().getGDate().get(Calendar.MINUTE)
              			&& tc.getTransferResult().getGDate().get(Calendar.HOUR_OF_DAY)==openFirst.getTransferResult().getGDate().get(Calendar.HOUR_OF_DAY)
              			&& tc.getTransferResult().getGDate().get(Calendar.DAY_OF_MONTH)==openFirst.getTransferResult().getGDate().get(Calendar.DAY_OF_MONTH)
              			&& tc.getTransferResult().getGDate().get(Calendar.MONTH)==openFirst.getTransferResult().getGDate().get(Calendar.MONTH)
              			&& tc.getTransferResult().getGDate().get(Calendar.YEAR)==openFirst.getTransferResult().getGDate().get(Calendar.YEAR)) {
            		 //openにopenFirstの他にopenFirstと同じ時間・同じ駅に着く経路がある場合(別の列車で別の駅から来る経路など)はそれらも展開する(次の経路を求める)   	
	            	if(tc.getTransferResult().getGoal()==openFirst.getTransferResult().getGoal()) {
	            		resultList.addAll(oneSearch(tc.getTransferResult(),goal,close,data));
	            		open.remove(open.indexOf(tc));//探索し終えたopenの要素を削除する
	            	}
            	}else {
            		break;
            	}
            }
            //open.remove(open.indexOf(openFirst));
            //close.add(open.get(0).getTransferResult().getGoal());
            
            open.addAll(resultList);//openに探索結果を追加する
            /*
            for(TransferCost tc : open) {
        		if(tc.getEstGoalDate()==null) {
        			continue;
        		}
        		//System.out.println((i++)+": "+tc.getTransferResult().getGoal().getName()+"("+tc.getEstGoalDate().getTime()+")");
        		//System.out.println("実際の到着時間: "+tc.getTransferResult().getGoal().getName()+"("+tc.getTransferResult().getGDate().getTime()+")");
        		
        		if(tc.getTransferResult().getGoal().getName().equals("赤羽")) {
        			try {
        				System.out.println("実際の到着時間(ソート前): "+tc.getTransferResult().getLine().getName()+" "+tc.getTransferResult().getGoal().getName()+"("+tc.getTransferResult().getGDate().getTime()+")");
        				Thread.sleep(1); // 一定時間処理を止める
        			} catch (InterruptedException e) {
        			}
        		}
        	}
        	*/
            Collections.sort(open, new TransferCostComparator(goal));//openリストをソート(累計推定所要時間が短い順)
            /*
            for(TransferCost tc : open) {
        		if(tc.getEstGoalDate()==null) {
        			continue;
        		}
        		//System.out.println((i++)+": "+tc.getTransferResult().getGoal().getName()+"("+tc.getEstGoalDate().getTime()+")");
        		//System.out.println("実際の到着時間: "+tc.getTransferResult().getGoal().getName()+"("+tc.getTransferResult().getGDate().getTime()+")");
        		
        		if(tc.getTransferResult().getGoal().getName().equals("赤羽")) {
        			try {
        				System.out.println("実際の到着時間(ソート後): "+tc.getTransferResult().getLine().getName()+" "+tc.getTransferResult().getGoal().getName()+"("+tc.getTransferResult().getGDate().getTime()+")");
        				Thread.sleep(1); // 一定時間処理を止める
        			} catch (InterruptedException e) {
        			}
        		}
        	}
            */
            //System.out.printf("open(before dedupe): ");
            //TransferCost.printGoal(open);
            //同じ駅に着くルートが既にopenにあった場合は、累積推定所要時間が最も小さいものだけ残す
            //累計推定所要時間が最も小さいものが複数ある場合(違う路線で同じ駅に同じ時間に着く場合)は全てopenに残す
            open = TransferCost.dedupe(open);//TransferCostの重複を取り除く openは早い順にソート済みなので重複する場合は最も早い方が残る
            /*
            for(TransferCost tc : open) {
        		if(tc.getEstGoalDate()==null) {
        			continue;
        		}
        		//System.out.println((i++)+": "+tc.getTransferResult().getGoal().getName()+"("+tc.getEstGoalDate().getTime()+")");
        		//System.out.println("実際の到着時間: "+tc.getTransferResult().getGoal().getName()+"("+tc.getTransferResult().getGDate().getTime()+")");
        		
        		if(tc.getTransferResult().getGoal().getName().equals("赤羽")) {
        			try {
        				if(tc.getTransferResult().getLine().getName().equals("湘南新宿ライン")) {
        					isShonan = true;
        				}else if(tc.getTransferResult().getLine().getName().equals("埼京線・りんかい線")) {
        					isSaikyo = true;
        				}
        				System.out.println("実際の到着時間(重複削除後): "+tc.getTransferResult().getLine().getName()+" "+tc.getTransferResult().getGoal().getName()+"("+tc.getTransferResult().getGDate().getTime()+")");
        				Thread.sleep(1); // 一定時間処理を止める
        			} catch (InterruptedException e) {
        			}
        		}
        	}
            if(isShonan && isSaikyo) {
            	//System.exit(1);
            }
            */
            //System.out.printf("open(after dedupe): ");
            //TransferCost.printGoal(open);
            if(!(isFindGoal)) {
                for(TransferCost t : resultList) {
                    if(t.getTransferResult().getGoal()==goal) {
                        isFindGoal = true;
                        System.out.println("目的地までの経路が見つかりました.");
                        break;
                    }
                }
            }
        }
        System.out.println("目的地までの経路が見つかりませんでした.");
        return null;
    }

    private int calendarToTime(Calendar date) {
        int hour = date.get(Calendar.HOUR_OF_DAY);
        int minute = date.get(Calendar.MINUTE);
        int second = date.get(Calendar.SECOND);
        int time = hour*10000 + minute*100 + second;
        if(time<30000) {
            time = time + 240000;
        }
        return time;
    }

    private Calendar reCalendar(Calendar date) {
        /*Calendar reDate = (Calendar)date.clone();
    	if(reDate.get(Calendar.HOUR_OF_DAY)<3) {
            reDate.add(Calendar.DAY_OF_MONTH, -1);
        }
        return reDate;
        */
    	Calendar reDate = Calendar.getInstance();
    	reDate.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
    	if(date.get(Calendar.HOUR_OF_DAY)<3) {
            reDate.add(Calendar.DAY_OF_MONTH, -1);
        }
        return reDate;
    }

    //環状線(山手線)も対応
    //ある駅startから1駅で行ける各駅への最短経路を返す(違う路線で同じ駅に同じ時間に着く場合も複数入れて返す)
    private ArrayList<TransferResult> firstOneSearch(Station start,Station goal,Line line1, Calendar date, int time, ArrayList<Station> close,Data data, TransferResult preTransfer/*, boolean isFirst*/){
        //line1は乗換前乗っていた路線(出発地から乗った場合はline1=null)
        ArrayList<TransferResult> choiceList = new ArrayList<>();
        for(Line line2: start.getLineList()) {
            int dn = line2.diaNum(date);
            ArrayList<String[][]> dia = line2.getDiagram();
            if(line2.isComplete()) {//路線データが完全に格納されているか
                int i;
                for(i =3; i<dia.get(dn).length; i++) {//i=0,1,2は列車番号、列車名、運転日が少なくともあるので飛ばしてi=3から
                    if(noise_Remove(dia.get(dn)[i][0]).equals(start.getName())
                            && noise_Remove(dia.get(dn)[i][1]).matches("発")) {
                        //System.out.println(noise_Remove(dia.get(dn)[i][0]));
                        //System.out.println("i= "+i);
                        //.out.println("路線は"+line2.getName());
                        //System.out.println("17インデックス目の駅欄は"+noise_Remove(dia.get(dn)[17][0]));
                        //System.out.println("2インデックス目の駅欄は"+noise_Remove(dia.get(dn)[2][0]));
                        
                        //System.out.println("firstOneSearch "+start.getName()+"駅(発)は"+line2.getName()+"の時刻表の"+(i+1)+"行目にあります");
                        break;
                    }
                }
                if(i==dia.get(dn).length) {
                    //System.out.println(line2.getName()+"の時刻表データに"+start.getName()+"駅発がありません");
                    continue;
                }else if(i==dia.get(dn).length-1){//startがlineにおける最も端にある駅(終着など)の場合はbreak
                    //System.out.println(start.getName()+"駅は"+line2.getName()+"の最端駅です");
                    continue;
                }else if(data.searchStation(noise_Remove(dia.get(dn)[i+1][0]))==null) {
                    //System.out.println(dia.get(dn)[i+1][0]);
                	String str = noise_Remove(dia.get(dn)[i+1][0]);//i+2行目の最左の文字列(駅名が書かれている列)
                    if(str.contains("番線")) {
                        //System.out.println(str+"をみつけました");
                        if(dia.get(dn).length<=i+2) {//i+2行目が最後の行なら路線の最端の駅
                            //System.out.println(start.getName()+"駅は"+line2.getName()+"の最端駅です");
                            continue;
                        }else {
                            if(data.searchStation(noise_Remove(dia.get(dn)[i+2][0]))!=null) {
                                //登録されている駅名なのでcontinueせず続ける.
                                //System.out.println("'番線'をみつけたが、その1つ下に登録されている駅が見つかったので,探索を続ける");
                            }else {//登録されている駅名でなければ、continue
                                //System.out.println(start.getName()+"駅は"+line2.getName()+"の最端駅,もしくは検索対象の最端駅です1");
                                continue;
                            }
                        }
                    }else {
                        //System.out.println(start.getName()+"駅は"+line2.getName()+"の最端駅,もしくは検索対象の最端駅です2");
                        continue;
                    }
                }
                
                //int newTime = Time.plus(time, start.getTransferSec(line1,line2));//乗換時間を考慮した出発時間を与える
                /*
                //デバッグ用
                if(line2.getName().equals("武蔵野線")&&start.getName().equals("柏")) {
                	System.out.println("i = "+i);
                }
                */
                
                //first1Search1Lineではstartから次行ける駅を特定する
                //ある路線の1駅で行ける駅をgoalとした最短の各TransferResult
                //例えば中央線(快速)下りで中野駅から1駅で行けるのは、高円寺(普通)、三鷹(快速)、荻窪(通勤快速)の3通りでその3通りに最短で到着する経路をすべて返す
                ArrayList<TransferResult> oneLineTransferList = first1Search1Line(start,line2,date,time,i,close,data,preTransfer/*,isFirst*/);
                for(TransferResult newT: oneLineTransferList) {
                    if(isInTransferList(newT.getGoal(),choiceList)) {
                    	ArrayList<TransferResult> choiceListCopy = new ArrayList<>();
                    	choiceListCopy.addAll(choiceList);
                    	boolean isAddNewT = false;
                        for(TransferResult cT: choiceListCopy) {
                            if(cT.getGoal()==newT.getGoal()) {
                                if(cT.getGDate().before(newT.getGDate())) {
                                    //first1Search1Lineで得られた結果より早くnewT.goalに着く経路が既に見つかっている場合
                                	
                                	if(preTransfer!=null && preTransfer.getLine()==newT.getLine()) {
                            			//newTが前の乗換と同じ路線であればnewTを上書きする(直通運転はできるだけ前の路線を引き継ぐ)
                            			choiceList.remove(choiceList.indexOf(cT));
                            			choiceList.add(newT);
                                    	isAddNewT = true;
                                    	break;
                            		}
                                	//候補リストchoiceListに追加しない.
                                    break;
                                    //経路違いで同じ到着時間経路が複数ある場合でもすべてchoiceListには加えられないのでnewTは追加しない
                                }else if(cT.getGDate().after(newT.getGDate())) {
                                    //first1Search1Lineで得られた結果が早いなら
                                    //choiceListを更新する

                                    //newTより遅くに着く経路がchoiceListCopyに複数ある場合は全て削除し、newTの追加は1度だけ行う(重複を避けるため)
                                    if(newT.getTrainIDLastName().equals(cT.getTrainIDLastName())/*choiceList.contains(newT)*/) {
                                    	//列車番号が同じとき(newTの路線の時刻表には着時刻が書かれているが、cTの路線の時刻表には発時刻しか書かれておらずそれを着時刻としている場合)
                                    	if(preTransfer!=null && preTransfer.getLine()==newT.getLine()) {
                                			//newTが前の乗換と同じ路線であればnewTを上書きする(直通運転はできるだけ前の路線を引き継ぐ)
                                			choiceList.remove(choiceList.indexOf(cT));
                                			choiceList.add(newT);
                                        	isAddNewT = true;
                                        	break;
                                		}
                                    	/*
                                    	choiceList.remove(choiceList.indexOf(cT));
                                        if(!isAddNewT) {
                                        	choiceList.add(newT);
                                        	isAddNewT = true;
                                        }
                                        */
                                    }else {
                                    	//列車が違うとき
                                        choiceList.remove(choiceList.indexOf(cT));
                                        if(!isAddNewT) {
                                        	choiceList.add(newT);
                                        	isAddNewT = true;
                                        }
                                    }
                                    //newTより遅くに着く経路がchoiceListCopyに複数ある場合は全て削除するのでcontinue
                                    continue;
                                }else {
                                    //first1Search1Lineで得られた到着時間がcTと同じなら
                                    //さらに候補リストに加える
                                	/*
                                    if(!choiceList.contains(newT)) {
                                        choiceList.add(newT);
                                    }
                                    */
                                	if(newT.getTrainIDLastName().equals(cT.getTrainIDLastName())) {
                                		//到着する駅、到着時間、列車番号が全て同じであれば、同一の列車
                                		
                                		if(preTransfer!=null && preTransfer.getLine()==newT.getLine()) {
                                			//newTが前の乗換と同じ路線であればnewTを上書きする(直通運転はできるだけ前の路線を引き継ぐ)
                                			choiceList.remove(choiceList.indexOf(cT));
                                			choiceList.add(newT);
                                        	isAddNewT = true;
                                        	break;
                                		}else {
                                			break;
                                		}
                                	}else {
                                		//列車が違いで同じ到着時間経路がchoiceListに複数ある場合は全部追加しないといけないのでcontinue
                                        continue;
                                	}
                                }//if(cT.getGDate().before(newT.getGDate())) else
                            }//if(cT.getGoal().equals(newT.getGoal()))
                        }//for(TransferResult cT: choiceListCopy)
                    }else {
                        //候補にnewTと同じ駅に到着する経路がなかった場合は候補に新しく追加する
                        choiceList.add(newT);
                    }//if(isInTransferList(newT.getGoal(),choiceList)) else
                }//for
            }//if(line.isComplete())
        }//for(Line line: start.getLineList())
        /*
        ArrayList<TransferCost> result = new ArrayList<TransferCost>();

        for(TransferResult t: choiceList) {
            //t.print();
            float estSec = estCost(start,goal);
            TransferCost tCost = new TransferCost(t,estSec);
            tCost.print();
            result.add(tCost);
        }
        */
        return choiceList;
    }

    //ある路線の1駅で行ける駅をgoalとした最短の各TransferResultのリストを返す
    //例えば中央線(快速)下りで中野駅から1駅で行けるのは、高円寺(普通)、三鷹(快速)、荻窪(通勤快速)の3通りでその3駅にこの路線を使って最短で到着する経路をすべて返す
    private ArrayList<TransferResult>first1Search1Line(Station start,Line line, Calendar date, int startTime, int startRow, ArrayList<Station> close, Data data, TransferResult preTransfer/*, boolean isFirst*/) {
        ArrayList<TransferResult> resultList = new ArrayList<>();
        ArrayList<Station> doneStationList = new ArrayList<>();//最終的に1駅で行ける駅全てと他で調査済みの駅のリストcloseの駅が格納される.
        for(Station s : close) {
            doneStationList.add(s);
        }
        while(true) {
            TransferResult transfer = first1Search1Line1Station(start,line,date,startTime,startRow,data,doneStationList,preTransfer/*,isFirst*/);
            if(transfer==null) {
                break;
            }
            resultList.add(transfer);
            if(doneStationList.contains(transfer.getGoal())) {
            	System.out.println("Exception first1Search1Line: 既に調査済みの駅に到着する経路がfirst1Search1Line1Stationから返されました.\n"
            		+ "first1Search1Line1StationのdoneStationListの処理が間違っている可能性があります.");
            }
            doneStationList.add(transfer.getGoal());
        }
        return resultList;
    }
    //この列車の終点駅名を返す (切り離しがある場合は分岐する列車のなかで時刻表の最も左にある列車の終着を返す)

    //出発地点からある駅を通りさらに1つ隣の駅(closeに入ってる駅は除く)に行く各最短経路(その経路に乗換があるなら一番最後のTransferResultを持つ)TransferCostを返す
    private ArrayList<TransferCost> oneSearch(TransferResult transfer,Station goal/*,Calendar date*/,ArrayList<Station> close,Data data){
        ArrayList<TransferResult> choiceList = new ArrayList<>();
        //Station transferStart = transfer.getStart();
        //ArrayList<TransferCost> result = new ArrayList<TransferCost>();
        ArrayList<TransferResult> nextTransferList = firstOneSearch(transfer.getGoal(), goal, transfer.getLine(),transfer.getDate(), transfer.getGoalTime(), close, data,transfer/*, false*/);

        for(TransferResult nextTransfer : nextTransferList) {
            TransferResult combTransfer = combine(transfer,nextTransfer,data);
            //combTransfer.print();
                if(combTransfer!=null) {
                    choiceList.add(combTransfer);
                }
        }

        ArrayList<TransferCost> resultTCost = new ArrayList<>();
        for(TransferResult tr : choiceList) {
            float estSec = estCost(tr.getGoal(),goal,data);
            TransferCost tc = new TransferCost(tr,estSec);
            resultTCost.add(tc);
        }

        return resultTCost;
    }

    /* t1とt2が同じ列車の場合、一つにつなげた新たなTransferResultを作って返す.
     * ちょうど直通運転の堺の場合はt1をコピーしたnewT1に対してnewT1.next=t2, t2.previous=newT1, t2.isFirstTransfer=false, t2.isConnect=trueとしてt2を返す
     * 乗換時間を考慮して乗り換えできる場合はnewT1.next=t2, t2.previous=newT1, t2.isFirstTransfer=falseとしt2を返し,
     * 
     * 乗換が間に合わない場合と
     * 別の路線の直通運転だが、t1の時刻表にもまだ次の時刻が書いてある場合(重複するため省いてよい)
     * はnullを返す.
     *
     * t2は書き換えられるので注意
     * 山手線にも対応
     */
    private TransferResult combine(TransferResult t1, TransferResult t2, Data data) {
        //条件分岐させて↑の4つの場合に対応させて返すようにする
        if(t1.getTrainIDList().size()==0) {
            System.out.println("Exception combine: t1.TrainIDListが空");
            return null;
        }
        

        //t1.goalからt1の列車に乗り続けて次行ける駅(時刻)までの経路(1駅間)
        //System.out.println("t1.getTrainIDList().get(t1.getTrainIDList().size()-1).getRow(): "+t1.getTrainIDList().get(t1.getTrainIDList().size()-1).getRow());
        //System.out.println("t1.getTrainIDList().get(t1.getTrainIDList().size()-1).getColumn(): "+t1.getTrainIDList().get(t1.getTrainIDList().size()-1).getColumn());
        //System.out.println("t1.getGoal().getName(): "+t1.getGoal().getName());
        //System.out.println("t1.getLine().getName(): "+t1.getLine().getName());
        TransferResult nextT1 = search1Line1Station1Day(t1.getGoal(),t1.getLine(),
                t1.getDate(), t1.getGoalTime(),
                t1.getTrainIDList().get(t1.getTrainIDList().size()-1).getRow(),
                t1.getTrainIDList().get(t1.getTrainIDList().size()-1).getColumn(),
                data, new ArrayList<Station>());
        /*
        //デバッグ用
        if(t1.getTerminalData().getName().equals("東京")
    			&& t1.getTerminalData().getTime()==190300
    			&& t2.getTerminalData().getName().equals("東京")
    			&& t2.getTerminalData().getTime()==190300
    			&& t2.isConnect()) {
    		System.out.println("combine 前の路線: "+t1.getLineName());
    		System.out.println("combine 後の路線: "+t2.getLineName());
    		if(nextT1==null) {
    			System.out.println("ok: nextT1 = null");
    		} else {
    			System.out.println("ng: nextT1 != null");
    			System.out.println("nextT1.goal: "+nextT1.getGoal().getName());
    			System.out.println("t2.goal: "+t2.getGoal().getName());
    		}
    		System.out.println("");
        }
        */
        // 日をまたぐ場合 t1が終電でt2が始発などの場合
        // 山手線の場合も処理できる(t1が大塚→池袋(池袋止), t2が池袋→目白 など)
        //System.out.println("t1.date.day = "+t1.getDate().get(Calendar.DAY_OF_MONTH)+", t2.date.day = "+t2.getDate().get(Calendar.DAY_OF_MONTH));
        if(t1.getDate().get(Calendar.DAY_OF_MONTH)!=t2.getDate().get(Calendar.DAY_OF_MONTH)){
        	TransferResult newT1 = t1.clone();
            newT1.setNext(t2);
            if(newT1.getPrevious()!=null) {
            	newT1.getPrevious().setNext(newT1);
            }
            t2.setPrevious(newT1);
            t2.setFalseIsFirstTransfer();
            int waitSec = t2.getStart().getTransferSec(t1.getLine(),t2.getLine());
            t2.setTransferSec(waitSec);//同じ路線での乗換なので基本15s(電車が日をまたぐので電車は来ないが)
            newT1.setFare(fare(newT1));//日をまたぐのでここまでの運賃を精算する
            if(t1.getLine().getCompany()!=t2.getLine().getCompany()) {//運行会社が変わるとき精算するが、今回はここは通らない
            	System.out.println("Exception combine: 運行会社のCompanyオブジェクトが違います1");
            	t2.setIsCompetTransfer(true);
            	newT1.setFare(fare(newT1));
            }
            return t2;
        }else if(nextT1!=null) {//t1.line.diagram内にt1.goalの次に行ける駅が存在するとき
        	//System.out.println("nexT1.goal :"+nextT1.getGoal().getName());
        	//System.out.println("t2.goal :"+t2.getGoal().getName());
        	//System.out.println("nexT1.line :"+nextT1.getLine().getName());
        	//System.out.println("t2.line :"+t2.getLine().getName());
        	if(nextT1.getGoal()==t2.getGoal() && nextT1.getLine()==t2.getLine()) {//t1とt2は同じ路線          
            	
            	if(nextT1.getGoalTime() == t2.getGoalTime()
                        && nextT1.getTrainIDLastName().equals(t2.getTrainIDLastName())
                        && nextT1.getTerminalData().equals(t2.getTerminalData())) {//t1とt2は同一列車
                    TransferResult combT = new TransferResult(t1.getStart(), t2.getGoal(), t1.getDate(), t1.getStartTime(), t2.getGoalTime(),
                            t1.getLine(), t2.getTerminalData(), t1.getTransferSec(),
                            t1.getStartNum(), t2.getGoalNum(), t1.isConnect(), t1.isCompetTransfer());
                    combT.setPrevious(t1.getPrevious());
                    if(combT.getPrevious()!=null) {
                    	combT.getPrevious().setNext(combT);
                    }
                    combT.setIsFirstTransfer(t1.isFirstTransfer());
                    
            		combT.setNext(t2.getNext());
            		if(combT.getNext()!=null) {
            			combT.getNext().setPrevious(combT);
                    }
            		
            		//t2が後のため時刻表の行列番号はt2を引き継ぐ(列車番号はt1もt2も同じ)
            		combT.setTrainID(t2.getTrainIDLast());
            		/*TransferResult nextConnect = search1Line1Station1Day(t1.getGoal(),t1.getLine(),
                            t1.getDate(), t1.getGoalTime(),
                            t1.getTrainIDList().get(t1.getTrainIDList().size()-1).getRow(),
                            t1.getTrainIDList().get(t1.getTrainIDList().size()-1).getColumn(),
                            data, new ArrayList<Station>());
            		
            		TransferResult nextConnect = first1Search1Line1Station1Day(start,line,date,firstStartTime,
            				startRow,data,doneStationList,preTransfer);
            		*/
            		
            		ArrayList<Station> t2reStationList = new ArrayList<>();
            		t2reStationList.addAll(t2.getStationList());
            		if(t2reStationList.size()>=2) {//必ずstartとgoalの2駅以上含まれている
            			t2reStationList.remove(0);//t2.startを除く t2.startとt1.goalは同じなので除く
            		}else {
            			System.out.println("Exception combine: t2.stationListに含まれている駅数が2未満");
            		}
            		combT.addAllStation(t1.getStationList());
            		combT.addAllStation(t2reStationList);
            		
            		ArrayList<Station> t2reRealStationList = new ArrayList<>();
            		t2reRealStationList.addAll(t2.getRealStationList());
            		if(t2reRealStationList.size()>=2) {//必ずstartとgoalの2駅以上含まれている
            			t2reRealStationList.remove(0);//t2.startを除く t2.startとt1.goalは同じなので除く
            		}else {
            			System.out.println("Exception combine: t2.realStationListに含まれている駅数が2未満");
            		}
            		combT.addAllRealStation(t1.getRealStationList());
            		combT.addAllRealStation(t2reRealStationList);
            		//同じ路線なので運賃は確定しない
                    return combT;
                }else {// 同じ路線だが列車が違う 乗り換える
	                	TransferResult newT1 = t1.clone();
	                    newT1.setNext(t2);
	                    if(newT1.getPrevious()!=null) {
	                    	newT1.getPrevious().setNext(newT1);
	                    }
	                    t2.setPrevious(newT1);
	                    t2.setFalseIsFirstTransfer();
	                    int waitSec = t2.getStart().getTransferSec(t1.getLine(),t2.getLine());
	                    t2.setTransferSec(waitSec);//同じ路線での乗換なので基本0s
	                    if(t1.getLine().getCompany()!=t2.getLine().getCompany()) {//運行会社が変わるとき精算するが、今回はここは通らない
	                    	System.out.println("Exception combine: 運行会社のCompanyオブジェクトが違います1");
	                    	t2.setIsCompetTransfer(true);
	                    	newT1.setFare(fare(newT1));
	                    }
	                    return t2;	
                }
            }else if(nextT1.getGoal()==t2.getGoal()//t1とt2の路線は違う
            		&& t1.getTrainIDLastName().equals(t2.getTrainIDFirstName())//t1最後の列車番号とt2最初の列車番号が同じ
                    && t1.getTerminalData().equals(t2.getTerminalData())) {//路線が違くても列車番号などが同じならそれは直通運転なので、一つのTransferResultに統合する
            	/*
            	 * 京浜東北線・根岸線と横浜線は桜木町(京浜東北線・根岸線側)～東神奈川(横浜線側) 間で共通の列車番号をもつ
            	 * 例えば、京浜東北線・根岸線(横浜線直通)で東神奈川まで行けば東神奈川まで京浜東北線・根岸線で行った扱いに
            	 * 横浜線(京浜東北線・根岸線直通)で桜木町まで行けば桜木町まで横浜線で行ける扱いになる(本来桜木町は横浜線の駅ではないが、このプログラムではこう扱う)
            	 */
            	//CONTINUE
            	
            	/*first1Search1Line1Station1Day(t1.getGoal(),t1.getLine(),
                      t1.getDate(), t1.getGoalTime(), t1.getTrainIDLast().getRow(), data, ArrayList<Station> doneStationList, TransferResult preTransfer);l 
            	search1Line1Station1Day(t1.getGoal(),t1.getLine(),
                        t1.getDate(), t1.getGoalTime(), t1.getTrainIDLast().getRow(),
                        t1.getTrainIDLast().getColumn(), Data data, ArrayList<Station> doneStationList)
            	return null;
            	*/
            	
            	//直通運転でつなぐ
            	TransferResult newT1 = t1.clone();
                newT1.setNext(t2);
                if(newT1.getPrevious()!=null) {
                	newT1.getPrevious().setNext(newT1);
                }
                t2.setPrevious(newT1);
                t2.setFalseIsFirstTransfer();
                //int waitSec = t2.getStart().getTransferSec(t1.getLine(),t2.getLine());
                t2.setTransferSec(0);//同じ列車なので0s
                t2.setIsConnect(true);
                if(t1.getLine().getCompany()!=t2.getLine().getCompany()) {//運行会社が変わるとき精算するが、今回はここは通らない
                	System.out.println("Exception combine: 運行会社のCompanyオブジェクトが違います1");
                	t2.setIsCompetTransfer(true);
                	newT1.setFare(fare(newT1));
                }
                return t2;	
            	
            	/*
            	//t2の路線名を引き受ける isConnectはtrueにしない(列車番号がかわってもなお直通運転の場合にisConnect=trueとする)
            	TransferResult combT = new TransferResult(t1.getStart(), t2.getGoal(), t1.getDate(), t1.getStartTime(), t2.getGoalTime(),
                        t2.getLine(), t2.getTerminalData(), t1.getTransferSec(),
                        t2.getStartNum(), t2.getGoalNum(), t1.isConnect(), t1.isCompetTransfer());
                combT.setPrevious(t1.getPrevious());
                if(combT.getPrevious()!=null) {
                	combT.getPrevious().setNext(combT);
                }
                combT.setIsFirstTransfer(t1.isFirstTransfer());
                
        		combT.setNext(t2.getNext());
        		if(combT.getNext()!=null) {
        			combT.getNext().setPrevious(combT);
                }
        		
        		
        		combT.setTrainID(t2.getTrainIDLast());

        		ArrayList<Station> t2reStationList = new ArrayList<>();
        		t2reStationList.addAll(t2.getStationList());
        		if(t2reStationList.size()>=2) {//必ずstartとgoalの2駅以上含まれている
        			t2reStationList.remove(0);//t2.startを除く t2.startとt1.goalは同じなので除く
        		}else {
        			System.out.println("Exception combine: t2.stationListに含まれている駅数が2未満");
        		}
        		combT.addAllStation(t1.getStationList());
        		combT.addAllStation(t2reStationList);
        		
        		ArrayList<Station> t2reRealStationList = new ArrayList<>();
        		t2reRealStationList.addAll(t2.getRealStationList());
        		if(t2reRealStationList.size()>=2) {//必ずstartとgoalの2駅以上含まれている
        			t2reRealStationList.remove(0);//t2.startを除く t2.startとt1.goalは同じなので除く
        		}else {
        			System.out.println("Exception combine: t2.realStationListに含まれている駅数が2未満");
        		}
        		combT.addAllRealStation(t1.getRealStationList());
        		combT.addAllRealStation(t2reRealStationList);
        		//同じ路線なので運賃は確定しない
                return combT;
                */
            }else {//t1とt2は通常の乗換 乗換時間が間にあうか調べる
            	int waitSec = t2.getStart().getTransferSec(t1.getLine(),t2.getLine());
            	t2.setTransferSec(waitSec);
            	//System.out.println("t1.getGoalTime() = "+t1.getGoalTime());
            	//System.out.println("waitSec = "+waitSec);
            	//System.out.println("Time.plus(t1.getGoalTime(),waitSec) = "+Time.plus(t1.getGoalTime(),waitSec));
            	//System.out.println("t2.getStartTime() = "+t2.getStartTime());
            	if(Time.plus(t1.getGoalTime(),waitSec)<= t2.getStartTime()) {//乗換が間に合う場合
            		TransferResult newT1 = t1.clone();
                    newT1.setNext(t2);
                    if(newT1.getPrevious()!=null) {
                    	newT1.getPrevious().setNext(newT1);
                    }
                    t2.setPrevious(newT1);
                    t2.setFalseIsFirstTransfer();
                    if(t1.getLine().getCompany()!=t2.getLine().getCompany()) {//運行会社が変わるとき精算するが、今回はここは通らない
                    	System.out.println("Exception combine: 運行会社のオブジェクトが違います2");
                    	t2.setIsCompetTransfer(true);
                    	newT1.setFare(fare(newT1));
                    }
                    return t2;
            	}else {//乗換が間に合わない場合
            		return null;
            	}
            }
        }else {//t1.line.diagram内にt1.goalの次に行ける駅が存在しないとき
        	Station t1Terminal = parseStation(t1.getTerminalName(),t1.getLine(),data);
        	if(t1.getLine().getIsLoop() && t1.getLine()==t2.getLine()){//t1とt2が同じ路線で環状線のとき
        		if(t1.getLine().getName().equals("山手線")) {//t1もt2も山手線のとき
        			String t1idStr = t1.getTrainIDLastName().replace("G", "");
        			String t2idStr = t2.getTrainIDFirstName().replace("G", "");
        			if(!(t1idStr.matches("[0-9]{3,}") && t1idStr.matches("[0-9]{3,}"))) {
        				System.out.println("Exceptino combine: t1かt2に格納されているTrainIDに不備があります");
        				return null;
        			}
        			int t1id = Integer.parseInt(t1idStr);
        			int t2id = Integer.parseInt(t2idStr);
        			if(t1id%100 == t2id%100 
        				&& (((int)t2id/100 -(int)t1id/100)==1 || ((int)t2id/100 -(int)t1id/100)==2)) {
        				//t1とt2は同一の列車(山手線)
        				TransferResult combT = new TransferResult(t1.getStart(), t2.getGoal(), t1.getDate(), t1.getStartTime(), t2.getGoalTime(),
                                t1.getLine(), t2.getTerminalData(), t1.getTransferSec(),
                                t1.getStartNum(), t2.getGoalNum(), t1.isConnect(), t1.isCompetTransfer());
        				combT.setPrevious(t1.getPrevious());
                        if(combT.getPrevious()!=null) {
                        	combT.getPrevious().setNext(combT);
                        }
                        combT.setIsFirstTransfer(t1.isFirstTransfer());
                        
                		combT.setNext(t2.getNext());
                		if(combT.getNext()!=null) {
                			combT.getNext().setPrevious(combT);
                        }
                		
                		//combT.setCombTrainID(t1.getTrainIDList(),t2.getTrainIDLast());
                		combT.setTrainID(t2.getTrainIDLast());
                		
                		ArrayList<Station> t2reStationList = new ArrayList<>();
                		t2reStationList.addAll(t2.getStationList());
                		if(t2reStationList.size()>=2) {//必ずstartとgoalの2駅以上含まれている
                			t2reStationList.remove(0);//t2.startを除く t2.startとt1.goalは同じなので除く
                		}else {
                			System.out.println("Exception combine: t2.stationListに含まれている駅数が2未満");
                		}
                		combT.addAllStation(t1.getStationList());
                		combT.addAllStation(t2reStationList);
                		
                		ArrayList<Station> t2reRealStationList = new ArrayList<>();
                		t2reRealStationList.addAll(t2.getRealStationList());
                		if(t2reRealStationList.size()>=2) {//必ずstartとgoalの2駅以上含まれている
                			t2reRealStationList.remove(0);//t2.startを除く t2.startとt1.goalは同じなので除く
                		}else {
                			System.out.println("Exception combine: t2.realStationListに含まれている駅数が2未満");
                		}
                		combT.addAllRealStation(t1.getRealStationList());
                		combT.addAllRealStation(t2reRealStationList);
                		//同じ路線なので運賃は確定しない
        				return combT;
        			}else {//山手線と山手線とで乗換が必要な場合
        				/*
        				 * t1が五反田→大崎(大崎止), t2が大崎→品川のときなど
        				 * (後でソートするときに乗り換えが少ない後の経路が優先されるのでGUIには基本出力されない)
        				 */
        				TransferResult newT1 = t1.clone();
	                    newT1.setNext(t2);
	                    if(newT1.getPrevious()!=null) {
	                    	newT1.getPrevious().setNext(newT1);
	                    }
	                    t2.setPrevious(newT1);
	                    t2.setFalseIsFirstTransfer();
	                    int waitSec = t2.getStart().getTransferSec(t1.getLine(),t2.getLine());
	                    t2.setTransferSec(waitSec);//同じ路線での乗換なので基本15s
	                    if(t1.getLine().getCompany()!=t2.getLine().getCompany()) {//運行会社が変わるとき精算するが、今回はここは通らない
	                    	System.out.println("Exception combine: 運行会社のオブジェクトが違います3");
	                    	t2.setIsCompetTransfer(true);
	                    	newT1.setFare(fare(newT1));
	                    }
	                    return t2;
        			}
        		}else {
        			System.out.println("Exception combine: 山手線以外の環状線が探索されています");
        			return null;
        		}
        	}else if(t1Terminal!=null && Arrays.asList(t1.getLine().getStationList()).contains(t1Terminal)/*(t1Terminal,t1.getStationList())*/) {//主にt1.goalが終着のとき
        		if(t1.getGoal()==t1Terminal) {//t1.goalが終着駅の場合
        			int waitSec = t2.getStart().getTransferSec(t1.getLine(),t2.getLine());
                	t2.setTransferSec(waitSec);
                	if(Time.plus(t1.getGoalTime(),waitSec)<= t2.getStartTime()) {//乗換が間にあうので乗り換える
                		TransferResult newT1 = t1.clone();
                        newT1.setNext(t2);
                        if(newT1.getPrevious()!=null) {
                        	newT1.getPrevious().setNext(newT1);
                        }
                        t2.setPrevious(newT1);
                        t2.setFalseIsFirstTransfer();
                        if(t1.getLine().getCompany()!=t2.getLine().getCompany()) {//運行会社が変わるとき精算するが、今回はここは通らない
                        	System.out.println("Exception combine: 運行会社のオブジェクトが違います4");
                        	t2.setIsCompetTransfer(true);
                        	newT1.setFare(fare(newT1));
                        }
                        return t2;
        			}else {//乗換が間に合わない
        				return null;
        			}
        		}else {//今回は通らない (ここを満たすデータの形がないため、もしあるなら乗換結果を返すでよいので乗換結果を返すようにしている)
        			System.out.println("Exception combine: 登録外の駅を通るが終着が登録されている場合に処理されうるが、今回のデータではその例はない.");
        			System.out.println("getTerminalDataが間違っている可能性もあり");
        			System.out.println("t1.line: "+t1.getLine().getName());
        			System.out.println("t1.goal: "+t1.getGoal().getName());
        			System.out.println("t1.goalTime: "+t1.getGoalTime());
        			System.out.println("t2.line: "+t2.getLine().getName());
        			System.out.println("t2.start: "+t2.getStart().getName());
        			System.out.println("t2.startTime: "+t2.getStartTime());
        			//t1.lineとt2.lineが同一の可能性もあり
        			int waitSec = t2.getStart().getTransferSec(t1.getLine(),t2.getLine());
                	t2.setTransferSec(waitSec);
                	if(Time.plus(t1.getGoalTime(),waitSec)<= t2.getStartTime()) {//乗換が間にあうので乗り換える
                		TransferResult newT1 = t1.clone();
                        newT1.setNext(t2);
                        if(newT1.getPrevious()!=null) {
                        	newT1.getPrevious().setNext(newT1);
                        }
                        t2.setPrevious(newT1);
                        t2.setFalseIsFirstTransfer();
                        if(t1.getLine().getCompany()!=t2.getLine().getCompany()) {//運行会社が変わるとき精算するが、今回はここは通らない
                        	System.out.println("Exception combine: 運行会社のオブジェクトが違います6");
                        	t2.setIsCompetTransfer(true);
                        	newT1.setFare(fare(newT1));
                        }
                        return t2;
        			}else {//乗換が間に合わない
        				return null;
        			}
        		}
        	}else {
        		if(t1.getTerminalData().equals(t2.getTerminalData()) && t1.getGoalTime()<=t2.getStartTime()) {
        			//System.out.println("t1.terminalName: "+t1.getTerminalData().getName());
        			//System.out.println("t2.terminalName: "+t2.getTerminalData().getName());
        			//System.out.println("t1.terminalName: "+t1.getTerminalData().getName());
        			//t1がt2に直通するとき
        			TransferResult newT1 = t1.clone();
                    newT1.setNext(t2);
                    if(newT1.getPrevious()!=null) {
                    	newT1.getPrevious().setNext(newT1);
                    }
                    t2.setPrevious(newT1);
                    t2.setFalseIsFirstTransfer();
                    t2.setIsConnect(true);//直通運転である.
                    t2.setTransferSec(-1);//同じ列車に乗っているだけなので未設定とする
                    if(t1.getLine().getCompany()!=t2.getLine().getCompany()) {//運行会社が変わるとき精算するが、今回はここは通らない
                    	System.out.println("Exception combine: 運行会社のオブジェクトが違います7");
                    	t2.setIsCompetTransfer(true);
                    	newT1.setFare(fare(newT1));
                    }
                    return t2;
        		}else {//その他の乗換 
        			/*
        			 * t1.lineが山手線,t1.goalが大崎でt2が別の路線のときや
        			 * t1.goalより先の駅が登録されていないがt2.lineに乗り換えられるとき(t1.line=t2.lineである可能性もあり)など
        			 */
        			//t1.lineとt2.lineが同一の可能性もあり
        			int waitSec = t2.getStart().getTransferSec(t1.getLine(),t2.getLine());
                	t2.setTransferSec(waitSec);//t1.line=t2.lineなら15(s)が与えられる
                	if(Time.plus(t1.getGoalTime(),waitSec)<= t2.getStartTime()) {//乗換が間にあうので乗り換える
                		TransferResult newT1 = t1.clone();
                        newT1.setNext(t2);
                        if(newT1.getPrevious()!=null) {
                        	newT1.getPrevious().setNext(newT1);
                        }
                        t2.setPrevious(newT1);
                        t2.setFalseIsFirstTransfer();
                        if(t1.getLine().getCompany()!=t2.getLine().getCompany()) {//運行会社が変わるとき精算するが、今回はここは通らない
                        	System.out.println("Exception combine: 運行会社のオブジェクトが違います8");
                        	t2.setIsCompetTransfer(true);
                        	newT1.setFare(fare(newT1));
                        }
                        return t2;
        			}else {//乗換が間に合わない
        				return null;
        			}
        		}
        	}
        }
    }

    //終着駅とその終着駅に到着する時間を返す
    private Terminal getTerminalData(Line line,int trainColumn, Calendar date) {
        //trainColumnは発・着どちらでも対応
        int dn = line.diaNum(date);
        ArrayList<String[][]> dia = line.getDiagram();
        boolean isExistTime = false;
        for(int i=3;i<dia.get(dn).length; i++) {//i=0,1,2には列車番号、列車名、運転日が少なくともあるので、i=3から
            String diaStr = noise_Remove(dia.get(dn)[i][trainColumn]);
            //System.out.println("getTerminalData 時刻表内のデータ: "+diaStr);
            if(!isExistTime && isTimeStr(diaStr)) {//diaStrが初めて時刻データだったとき
                isExistTime = true;
            }else if(isExistTime && !isTimeStr(diaStr)) {
                if(diaStr.matches("")) {
                    if(trainColumn+1<dia.get(dn)[i].length &&
                            noise_Remove(dia.get(dn)[i-1][trainColumn+1]).matches("┐")) {//同じ時刻表内で列車番号が変わるとき1
                        /*line.diagram.get(dn)={
                         * 　　・
                         * 　　・
                         * 　　・
                         * {"稲毛"  ,"発","0538"          ,""    ,...},
                         * {"千葉"  ,"着","0542"          ,"┐"  ,...},//←i-1行インデックス
                         * {"千葉"  ,"発",""(diaStrはここ),"0546",...},//←i行インデックス
                         * {"東千葉","発",""              ,"レ"  ,...},
                         * 　　・
                         * 　　・
                         * 　　・
                         * }							 * の位置に┐がある場合
                         */
                        i=i-1;//continue後,このiに+1されることに注意
                        trainColumn++;
                        continue;
                    }else if(trainColumn+1<dia.get(dn)[i].length &&
                    		noise_Remove(dia.get(dn)[i-2][trainColumn+1]).matches("┐")) {//同じ時刻表内で列車番号が変わるとき2
                        /*line.diagram.get(dn)={
                         * 　　・
                         * 　　・
                         * 　　・
                         * {"稲毛"  ,"発","0538"          ,"┐"  ,...},//←i-2行インデックス
                         * {"千葉"  ,"発","0546"          ,"0546",...},//←i-1行インデックス
                         * {"東千葉","発",""(diaStrはここ),"レ"  ,...},//←i行インデックス
                         * 　　・
                         * 　　・
                         * 　　・
                         * }
                         * や
                         * line.diagram.get(dn)={
                         * 　　・
                         * 　　・
                         * 　　・
                         * {"品川"    ,"発","1833"          ,""    ,...},//←i-3行インデックス
                         * {"東京"    ,"着","1841"          ,"┐"  ,...},//←i-2行インデックス
                         * {"東京番線",""  ,"(4)"           ,"(4)" ,...},//←i-1行インデックス
                         * {"東京"    ,"発",""(diaStrはここ),"1842",...},//←i行インデックス
                         * 　　・
                         * 　　・
                         * 　　・
                         * }
                         * の位置に┐がある場合
                         */
                        i=i-2;//continue後,このiに+1されることに注意
                        trainColumn++;
                        continue;
                    }
                    //列車番号が変わらないとき
                    while( !(noise_Remove(dia.get(dn)[i-1][1]).matches("発")||noise_Remove(dia.get(dn)[i-1][1]).matches("着")) ) {
                        i=i-1;
                    }
                    //山手線の時刻表データの格納の仕方の場合ここにたどり着くことはない.
                    Terminal result = new Terminal(noise_Remove(dia.get(dn)[i-1][0]),
                            parseTime(noise_Remove(dia.get(dn)[i-1][trainColumn])));//i-1行目は必ず着時間(発と書いてあるときも発・着同時刻として扱う発である)
                    return result;
                    /*<while内で1度もi=i-1されない例>
                     * line.diagram.get(dn)={
                     * 　　・
                     * 　　・
                     * 　　・
                     * {"武蔵増戸"  ,"発","0919"          ,"",...},//←i-2行インデックス
                     * {"武蔵五日市","着","0923"          ,"",...},//←i-1行インデックス
                     * {"牛浜"      ,"発",""(diaStrはここ),"",...},//←i行インデックス
                     * 　　・
                     * 　　・
                     * 　　・
                     * }
                     *や
                     *line.diagram.get(dn)={
                     * 　　・
                     * 　　・
                     * 　　・
                     * {"武蔵増戸"  ,"発","0919"          ,"",...},//←i-2行インデックス
                     * {"武蔵五日市","発","0923"          ,"",...},//←i-1行インデックス
                     * {"牛浜"      ,"発",""(diaStrはここ),"",...},//←i行インデックス
                     * 　　・
                     * 　　・
                     * 　　・
                     * }
                     * 、
                     *line.diagram.get(dn)={
                     * 　　・
                     * 　　・
                     * 　　・
                     * {"武蔵増戸"  ,"発","0919"          ,"",...},//←i-3行インデックス
                     * {"武蔵五日市","着","0923"          ,"",...},//←i-2行インデックス
                     * {"武蔵五日市","発","0924"          ,"",...},//←i-1行インデックス
                     * {"牛浜"      ,"発",""(diaStrはここ),"",...},//←i行インデックス
                     * 　　・
                     * 　　・
                     * 　　・
                     * }
                     * 、
                     * line.diagram.get(dn)={
                     * 　　・
                     * 　　・
                     * 　　・
                     * {"武蔵増戸"  ,"発","0919"          ,"",...},//←i-2行インデックス
                     * {"武蔵五日市","着","0923"          ,"",...},//←i-1行インデックス
                     * {"終着"      ,""  ,""(diaStrはここ),"",...}...//←i行インデックス 　　
                     * }
                     * のときは全て"武蔵五日市"がterminalName(終着駅名)となる.(i=i-1されることはない)
                     *
                     * <while内でi=i-1される例>
                     * line.diagram.get(dn)={
                     * 　　・
                     * 　　・
                     * 　　・
                     * {"品川"    ,"発"                ,"1833"          ,"",...},//←i-3行インデックス
                     * {"東京"    ,"着"                ,"1841"          ,"",...},//←i-2行インデックス
                     * {"東京番線",""(発でも着でもない),"(4)"           ,"",...},//←i-1行インデックス
                     * {"東京"    ,"発"                ,""(diaStrはここ),"",...},//←i行インデックス
                     * 　　・
                     * 　　・
                     * 　　・
                     * }
                     * や
                     * line.diagram.get(dn)={
                     * 　　・
                     * 　　・
                     * 　　・
                     * {"品川"    ,"発"                ,"1833"          ,"",...},//←i-3行インデックス
                     * {"東京"    ,"着"                ,"1841"          ,"",...},//←i-2行インデックス
                     * {"東京番線",""(発でも着でもない),"(4)"           ,"",...},//←i-1行インデックス
                     * {"終着"    ,""                  ,""(diaStrはここ),"",...}...//←i行インデックス
                     * }
                     * は東京が終着駅名となる.
                     */
                }else if(diaStr.matches("＝")||diaStr.matches("=")) {
                    if(trainColumn+1<dia.get(dn)[i].length &&
                    		noise_Remove(dia.get(dn)[i-1][trainColumn+1]).matches("┐")) {//同じ時刻表内で列車番号が変わるとき1
                        /*line.diagram.get(dn)={
                         * 　　・
                         * 　　・
                         * 　　・
                         * {"町屋"  ,"発","0723"            ,""    ,...},
                         * {"北千住","発","0726"            ,"┐"  ,...},//←i-1行インデックス
                         * {"綾瀬"  ,"発","＝"(diaStrはここ),"0730",...},//←i行インデックス
                         * {"北綾瀬","着",""                ,"||"  ,...},
                         * 　　・
                         * 　　・
                         * 　　・
                         * }
                         * や
                         * line.diagram.get(dn)={
                         * 　　・
                         * 　　・
                         * 　　・
                         * {"町屋"  ,"発","0723"            ,""    ,...},
                         * {"北千住","発","0726"            ,""    ,...},//←i-2行インデックス
                         * {"綾瀬"  ,"着","0729"            ,"┐"  ,...},//←i-1行インデックス
                         * {"綾瀬"  ,"発","＝"(diaStrはここ),"0730",...},//←i行インデックス
                         * {"北綾瀬","着",""                ,"||"  ,...},
                         * 　　・
                         * 　　・
                         * 　　・
                         * }
                         * の位置に┐がある場合
                         */
                        i=i-1;//continue後,このiに+1されることに注意
                        trainColumn++;
                        continue;
                    }else if(trainColumn+1<dia.get(dn)[i].length &&
                    		noise_Remove(dia.get(dn)[i-2][trainColumn+1]).matches("┐")) {//同じ時刻表内で列車番号が変わるとき2
                        /*line.diagram.get(dn)={
                         * 　　・
                         * 　　・
                         * 　　・
                         * {"町屋"  ,"発","0723"            ,""    ,...},
                         * {"北千住","発","0726"            ,"┐"  ,...},//←i-2行インデックス
                         * {"綾瀬"  ,"発","0729"            ,"0730",...},//←i-1行インデックス
                         * {"北綾瀬","着","＝"(diaStrはここ),"||"  ,...},//←i行インデックス
                         * {"亀有"  ,"発",""                ,"0733",...},
                         * 　　・
                         * 　　・
                         * 　　・
                         * }
                         * や
                         * line.diagram.get(dn)={
                         * 　　・
                         * 　　・
                         * 　　・
                         * {"町屋"  ,"発","0723"            ,""    ,...},
                         * {"北千住","発","0726"            ,""    ,...},//←i-3行インデックス
                         * {"綾瀬"  ,"着","0729"            ,"┐"  ,...},//←i-2行インデックス
                         * {"綾瀬"  ,"発","0730"            ,"0730",...},//←i-1行インデックス
                         * {"北綾瀬","着","＝"(diaStrはここ),"||"  ,...},//←i行インデックス
                         * {"亀有"  ,"発",""                ,"0733",...},
                         * 　　・
                         * 　　・
                         * 　　・
                         * }
                         * の位置に┐がある場合
                         */
                        i=i-2;//continue後,このiに+1されることに注意
                        trainColumn++;
                        continue;
                    }
                    //列車番号が変わらないとき
                    while( !(noise_Remove(dia.get(dn)[i-1][1]).matches("発")||noise_Remove(dia.get(dn)[i-1][1]).matches("着")) ) {
                        i=i-1;
                    }
                    Terminal result = new Terminal(noise_Remove(dia.get(dn)[i-1][0]),
                            parseTime(noise_Remove(dia.get(dn)[i-1][trainColumn])));//i-1行目は必ず着時間(発と書いてあるときも発・着同時刻として扱う発である)
                    return result;
                }else if(diaStr.matches("┘")) {//連結
                    i=i-1;
                    trainColumn--;
                    continue;
                    /*
                     * line.diagram.get(dn)={
                     * 　　・
                     * 　　・
                     * 　　・
                     * {"品川"    ,"発","0949","レ"              ,...},//←i-3行インデックス
                     * {"東京"    ,"着","1003","1000             ,...},//←i-2行インデックス
                     * {"東京番線",""  ,"-4"  ,"-4"              ,...},//←i-1行インデックス
                     * {"東京"    ,"発","1003","┘"(diaStrはここ),...},//←i行インデックス(他の例(ここでは省略)も含め┘は必ず連結した駅の発着時間の欄にあると思われる)
                     * {"秋葉原"  ,"発","||"  ,""                ,...},//←i+1行インデックス
                     * 　　・
                     * 　　・
                     * 　　・
                     * }
                     */
                }else if(diaStr.matches("レ")) {//通過
                    continue;
                }else if(diaStr.contains("||")) {//別のルート
                    continue;
                }else if(diaStr.matches("・")) {//別のルート
                    continue;
                }else if(diaStr.matches("\\([^0-9]*[0-9]+\\)")||diaStr.matches("（[^0-9]*[0-9]+）")
                        ||diaStr.matches("-[0-9]+")) {//-2や(2),(京1)など番線が書かれている場合
                    if(i+1<dia.get(dn).length) {
                        /*
                         *
                         */
                        continue;
                    }else if(i==dia.get(dn).length-1) {
                        while( !(noise_Remove(dia.get(dn)[i-1][1]).matches("発")||noise_Remove(dia.get(dn)[i-1][1]).matches("着")) ) {
                            i=i-1;
                        }
                        Terminal result = new Terminal(noise_Remove(dia.get(dn)[i-1][0]),
                                parseTime(noise_Remove(dia.get(dn)[i-1][trainColumn])));//i-1行目は必ず着時間(発と書いてあるときも発・着同時刻として扱う発である)
                        return result;
                        /*<while内で1度もi=i-1されない例>
                         * line.diagram.get(dn)={
                         * 　　・
                         * 　　・
                         * 　　・
                         * {"長町"    ,"発","0619"             ,"0635",...},//←i-2行インデックス
                         * {"仙台"    ,"着","0624"             ,"0641",...},//←i-1行インデックス
                         * {"仙台番線",""  ,"(6)"(diaStrはここ),"(4)" ,...} //←i行インデックス
                         * }
                         *
                         * <while内でi=i-1される例はおそらくないので省略>
                         */
                    }
                    System.out.println("Exception getTerminalData: out of index Errorがでないのはおかしい");
                    continue;
                }else if(noise_Remove(dia.get(dn)[i][0]).equals("終着")) {//一部の直通運転のとき
                    Terminal result = new Terminal(noise_Remove(dia.get(dn)[i][trainColumn]),
                            parseTime(noise_Remove(dia.get(dn)[i][trainColumn].replaceFirst("[^0-9]*",""))));
                    return result;
                }
                System.out.println("Exception getTerminalData: 時刻表内の"+diaStr+"は想定してない文字列です");
                continue;
            }
            if(i==dia.get(dn).length-1) {//「終着や次の区間の行がなく、最終行が駅名のとき」
                String terminalName = noise_Remove(dia.get(dn)[i][0]);
                if(terminalName.matches("終着")||terminalName.matches("次の区間")||terminalName.contains("番線")) {
                    System.out.println("Exception getTerminalData: 時刻表の最終行が駅でないのに最終行まで検索しています.");
                    System.out.println("考慮できてない事項がある可能性");
                    return new Terminal("",-1);
                }
                if(line.getIsLoop()&&line.getName().equals("山手線")) {
                    //terminalName = null;
                    int tailNum = Integer.parseInt(noise_Remove(dia.get(dn)[0][trainColumn]).replaceAll("[^0-9]", "")) % 100;//最終的に列車番号の下2桁か1桁の数字になる(列車番号が"2366G"なら66となる)
                    for(int j=trainColumn+1; j<dia.get(dn)[0].length; j++) {
                        if(tailNum==Integer.parseInt(noise_Remove(dia.get(dn)[0][j]).replaceAll("[^0-9]", "")) % 100) {
                             //列車はもう1周するので終着駅はなしとする
                            return new Terminal("",-1);
                        }
                    }
                }
                return new Terminal(terminalName,parseTime(noise_Remove(dia.get(dn)[i][trainColumn])));

                /*
                 * line.diagram.get(dn)={
                 * 　　・
                 * 　　・
                 * 　　・
                 * {"鳩ノ巣","発","0725"              ,...},//←i-2行インデックス
                 * {"白丸"  ,"発","0728"              ,...},//←i-1行インデックス
                 * {"奥多摩","着","0731"(diaStrはここ),...} //←i行インデックス(最終行)
                 * }
                 */
            }
            //diaStrが2度目以降で時刻データで前のifを満たさない場合、何もせず次の行を調べに行く(for文を続ける)
        }
        //for文を抜けてもreturnしないでここまで来るのは
        //「検索した列(trainColumn)になにも時刻データがなかったとき(Exception)」のみ
        if(!isExistTime) {//「検索した列(trainColumn)になにも時刻データがなかったとき(Exception)」
            System.out.println("Exception getTerminalData: 検索した列に時刻データがありません");
            return new Terminal("",-1);
        }
        System.out.println("Exception getTerminalData: 考慮できてない事項がある可能性");
        return new Terminal("",-1);
    }

    //startから1駅で行ける駅で未調査の駅を探す.もしこの日dateで未調査が見つからなくても次の日の終電まで探す
    //dateが深夜だった場合,次の日の始発で乗れる経路を探すため
    private TransferResult first1Search1Line1Station(Station start,Line line,
            Calendar date, int firstStartTime, int startRow, Data data, ArrayList<Station> doneStationList, TransferResult preTransfer/*,boolean isFirst*/) {
        //ArrayList<TransferResult> resultList = new ArrayList<>();
        TransferResult result = first1Search1Line1Station1Day(start,line,date,firstStartTime,startRow,data,doneStationList,preTransfer);

        if(result==null) {
            Calendar nextDate = (Calendar)date.clone();
            nextDate.add(Calendar.DAY_OF_MONTH, 1);
            int nextStartRow = startRow;
            int dn = line.diaNum(nextDate);
            
            ArrayList<String[][]> dia = line.getDiagram();
            if(dn!=line.diaNum(date)) {//前日と時刻表が変わるとき(前日は土祝日だったが、次の日が平日だった時など)
            	for(int i=2; i<dia.get(dn).length ;i++) {
            		if(noise_Remove(dia.get(dn)[i][0]).equals(start.getName())
                            && noise_Remove(dia.get(dn)[i][1]).matches("発")) {
                        //System.out.println(noise_Remove(dia.get(dn)[i][0]));
                        //System.out.println("i= "+i);
                        //System.out.println("路線は"+line2.getName());
                        //System.out.println("17インデックス目の駅欄は"+noise_Remove(dia.get(dn)[17][0]));
                        //System.out.println("2インデックス目の駅欄は"+noise_Remove(dia.get(dn)[2][0]));
                        
                        //System.out.println("firstOneSearch "+start.getName()+"駅(発)は"+line2.getName()+"の時刻表の"+(i+1)+"行目にあります");
                        nextStartRow = i;
                        break;
                    }
            	}
            }
            result=first1Search1Line1Station1Day(start,line,nextDate,30000,nextStartRow,data,doneStationList,null);
            //3:00:00を指定することで必ず始発から調査できる
        }
        return result;
    }
    
    //この日(date)に出発駅startから1駅で行ける駅で未調査の駅を探す.
    //firstStartTimeは乗り換え時間を考慮しない(preTransferのデータから関数内で乗り換え時間を考慮する)
    private TransferResult first1Search1Line1Station1Day(Station start,Line line,
            Calendar date, int firstStartTime, int startRow, Data data, ArrayList<Station> doneStationList, TransferResult preTransfer) {
        int dn = line.diaNum(date);
        ArrayList<String[][]> dia = line.getDiagram();
        //System.out.println("line.name: "+line.getName());
        //System.out.println("dia.get(dn).length: "+dia.get(dn).length);
        if(dia.get(dn).length <= startRow) {
        	System.out.println("Exception first1Search1Line1Station1Day: 行startRowがline.diaの行数よりも大きい");
        	return null;
        }
        int rowLength = dia.get(dn)[startRow].length;
        
       /*
        //startRowが1行後になってる問題を解消したい CONTINUE
        int firstTrainColumn = 2; //startRow行で時刻が書かれている一番左(早い時刻)の列インデックスとなる
        for(int trainColumn=2;trainColumn < rowLength ;trainColumn++) {//trainColumn=0は駅名、=1は"発"や"着"が格納されているので=2から始める
        	int getOnTime = parseTime(noise_Remove(dia.get(dn)[startRow][trainColumn]));//parseTimeは時刻(4桁以下のint)でない場合-1を返す
        	firstTrainColumn = trainColumn;
        	if(getOnTime>=0) {
        		if(start.getName().equals("柏") && line.getName().equals("武蔵野線")) {
	        		 try {
	        			 System.out.println("柏発武蔵野線");
	        			 System.out.println("firstTrainColumn = "+firstTrainColumn);
	        			 System.out.println("getOnTime = "+getOnTime);
	        			 System.out.println("dia.get(dn)[startRow][0] = \n"+noise_Remove(dia.get(dn)[startRow][0]));
	        			 System.out.println("startRow = "+startRow);
	                     //preResult4.print(preResult4);
	                     Thread.sleep(10000); // 一定時間処理を止める
	                     System.exit(0);
	                 } catch (InterruptedException e) {
	                 }
        		}
        		break;
        	}
        }
        */
        for(int trainColumn=2;trainColumn < rowLength ;trainColumn++) {
            //startRowは時刻表にstartがある行インデックス(必ず発時刻)
            int getOnTime = parseTime(noise_Remove(dia.get(dn)[startRow][trainColumn]));//parseTimeは時刻(4桁以下のint)でない場合-1を返す
            //getOnTimeはstart駅から乗れる列車の発車時刻(185500など)
            if(getOnTime>=firstStartTime && isFreeTrain(line,date,trainColumn)
                    && isOperated(line,date,trainColumn)) {
                //isFreeTrainとは追加料金のかからない列車かどうか
                //isOperatedとはこの列車jがこの日dateで運転しているか
                ArrayList<TrainID> trainIDList = new ArrayList<>();//列車番号を格納する、列車番号が途中で変わる場合も考慮し配列として格納
                for(int i=startRow+1; i<dia.get(dn).length; i++) {//列車trainColum(行)に乗って1駅で行ける未調査の駅を探す
                    //startがstartRow行インデックスにあるので探すgoalはそれよりは1つ下という意味でi=startRow+1
                    String goalTimeStr = noise_Remove(dia.get(dn)[i][trainColumn]);//goalTimeStrには候補となり得るgoalの到着時刻などになる
                    //System.out.println("時刻表内のデータ: "+goalTimeStr);
                    Station goal = parseStation(noise_Remove(dia.get(dn)[i][0]), line,data);
                    //goalTimeStrは発時刻でも今回は着時刻とみなすので問題ない(また発着時刻が両方書かれる駅の場合も必ず上の行に着時刻が書かれている)
                    if(isTimeStr(goalTimeStr) ) {//時刻が格納されているとき
                        //千代田線など検索範囲外の駅名の場合はgoalがnullになる可能性を考慮しgoal==nullのときは飛ばす(別の列車を探す)
                        if(doneStationList.contains(goal)/*isInStationList(goal,doneStationList)*/ || goal==null) {//調査済の駅のとき別の列車trainColumnを探す
                            //System.out.println("isInStationList(goal,doneStationList) || goal==null");
                            break;//別の列車trainColumnを探す
                        }

                        if(goal.equals(start)) {
                            System.out.println("Exception first1Search1Line1Station1Day: start=goalで、startRowが着時刻の可能性がある");
                            System.out.println("elseの空文字や┐などの処理が適切でない可能性");
                            System.out.println("想定外の時刻表の並びの可能性もあり");
                            System.out.println("時刻表の駅名の取得の仕方が間違っている可能性");
                            System.out.println("hint: 発・着の除外など");
                            continue;
                        }
                        /*
                         * line.diagram.get(dn)={
                         * 　　・
                         * 　　・
                         * 　　・
                         * {"品川"    ,"発"                ,"1833"                   ,"",...},//←i-2行インデックス
                         * {"東京番線",""(発でも着でもない),"(4)"                    ,"",...},//←i-1行インデックス
                         * {"東京"    ,"発"                ,"1841"(goalTimeStrはここ),"",...},//←i行インデックス
                         * 　　・
                         * 　　・
                         * 　　・
                         * }
                         * も東京が次の駅となる.
                         */
                        
                        int goalTime = parseTime(goalTimeStr);
                        //goalTimeStrは発時刻でも今回は着時刻とみなすので問題ない
                        //(また発着時刻が両方書かれる駅の場合も必ず上の行に着時刻が書かれている)
                        
                        int startNum = getStartNum(start,line,trainColumn,date);
                        int goalNum = getGoalNum(goal,line,trainColumn,date);
                        TransferResult result;
                        Terminal terminalData = getTerminalData(line,trainColumn,date);
                        int transferSec;
                        if(preTransfer!=null) {//乗り換えにかかる時間を与える
                        	transferSec = start.getTransferSec(preTransfer.getLine(),line);
                        }else {
                        	transferSec = 0;
                        }
                        
                        
                        
                        if(preTransfer!=null && terminalData.equals(preTransfer.getTerminalData()) 
                        		&& preTransfer.getDate().get(Calendar.DAY_OF_MONTH)==date.get(Calendar.DAY_OF_MONTH)
                        		&& preTransfer.getDate().get(Calendar.MONTH)==date.get(Calendar.MONTH) 
                        		&& preTransfer.getDate().get(Calendar.YEAR)==date.get(Calendar.YEAR)) {
                        	//System.out.println("ok");
                        	//System.out.println("i: "+i+", trainColumn: "+trainColumn);
                        	/*
                        	//デバッグ用 CONTINUE
                            if(terminalData.getName().equals("東京") 
                            		&& terminalData.getTime()==190300
                            		&& preTransfer.getTerminalData().getName().equals("東京") 
                            		&& preTransfer.getTerminalData().getTime()==190300) {
                            	 try {
                                     //preResult4.print(preResult4);
                                     System.out.println("前の路線: "+preTransfer.getLineName());
                                     System.out.println("後の路線; "+line.getName());
                                     System.out.println(goal.getName()+"に着く時間は"+goalTime);
                                     
                            		 System.out.println("ok");
                            		 Thread.sleep(1000); // 一定時間処理を止める
                                 } catch (InterruptedException e) {
                                 }
                            }
                            **/
                            if(line==preTransfer.getLine()) {
                            	result = new TransferResult(start,goal,date,getOnTime,goalTime,line,terminalData
                                        ,-1,startNum,goalNum,false,false);
                            }else{
                            	//直通運転の同じ列車
                            	result = new TransferResult(start,goal,date,getOnTime,goalTime,line,terminalData
                                    ,-1,startNum,goalNum,true,false);
                            }
                        }else if(Time.plus(firstStartTime, transferSec)
                        			<=getOnTime) {
                        	//乗換できる
                        	//System.out.println("?");
                        	result = new TransferResult(start,goal,date,getOnTime,goalTime,line,terminalData
                                    ,-1,startNum,goalNum,false,false);
                        }else {
                        	//乗換に間に合わないので別の列車trainColumnを探す
                        	//System.out.println("ok");
                        	break;
                        }
                        
                        
                        result.addStationFromLine(start,goal,line);
                        result.addRealStation(start);
                        result.addRealStation(goal);
                        if(noise_Remove(dia.get(dn)[0][0]).contains("列車番号")) {
                            trainIDList.add(new TrainID(noise_Remove(dia.get(dn)[0][trainColumn]),i,trainColumn));
                            result.addAllTrainID(trainIDList);
                        }else {// 全ての時刻表に列車番号は書かれているのでこのelseは通ることはない
                            //result.addTrainID("");
                        	System.out.println("時刻表の(0,0): "+noise_Remove(dia.get(dn)[0][0]));
                            System.out.println("Exception first1Search1Line1Station1Day: "+line.getName()+"の時刻表に列車番号が書かれていない");
                            trainIDList.add(new TrainID("",i, trainColumn));
                            result.addAllTrainID(trainIDList);
                        }
                        //System.out.println("first1Search1Line1Station1Day");
                        //System.out.println("Time.plus(firstStartTime, transferSec) = "+Time.plus(firstStartTime, transferSec));
                        //System.out.println("firstStartTime"+firstStartTime);
                        //System.out.println("transferSec = "+transferSec);
                        //System.out.println("getOnTime = "+getOnTime);
                        //System.out.println(goal.getName());
                        result.getTrainIDList().get(result.getTrainIDList().size()-1).print();
                        return result;
                        //ここでこれより早くgoalに着く列車がないかについてだが、結論はこれで問題ない.
                        //同じ路線、同じ列車種別(普通、快速など)の中では出発時刻が早い列車が最も早く到着する.(追い越されることはない)
                        //同じ路線で別の列車種別が早く到着する例があるが(快速の方が遅く出発して、早く到着するなど)は既にA*のOPENリストに格納されているため、そのOPENリストのソート時に除外できる.
                        //別の路線が早く到着するときもOPENリストのソート時に除外できる.
                    }else {//elseに来てもレや||では行けることがある
                        //=や空文字でも列車番号が変わるときや切り離しがある場合などは次に行けることがあるので注意
                        if(goalTimeStr.matches("")) {
                            //System.out.println("goalTimeStr.matches(\"\")");
                            if(trainColumn+1<dia.get(dn)[i].length &&
                            		noise_Remove(dia.get(dn)[i-2][trainColumn+1]).matches("┐")) {//同じ時刻表内で列車番号が変わるとき2
                                /*line.diagram.get(dn)={
                                 * 　　・
                                 * 　　・
                                 * 　　・
                                 * {"稲毛"  ,"発","0538"               ,"┐"  ,...},//←i-2行インデックス
                                 * {"千葉"  ,"発","0546"               ,"0546",...},//←i-1行インデックス
                                 * {"東千葉","発",""(goalTimeStrはここ),"レ"  ,...},//←i行インデックス
                                 * 　　・
                                 * 　　・
                                 * 　　・
                                 * }
                                 * の位置に┐がある場合
                                 */
                                i=i-1;//continue後,このiに+1されることに注意
                                trainColumn++;
                                if(trainColumn>=dia.get(dn)[0].length) {
                                	String week = dn==0 ? "平日" : "土休日";
                                	System.out.println("Exception first1Search1Line1Station1Day 1:\n"+line.getName()+" "+week+" の時刻表の"+(i+1)+"行目と"+
                                "1行目の要素数が一致しません");
                                	return null;
                                }
                                trainIDList.add(new TrainID(noise_Remove(dia.get(dn)[0][trainColumn]),i,trainColumn));
                                continue;
                            }else if(trainColumn+1<dia.get(dn)[i].length &&
                            		noise_Remove(dia.get(dn)[i-1][trainColumn+1]).matches("┐")) {//同じ時刻表内で列車番号が変わるとき2
                                //System.out.println("dia.get(dn)[i-1][trainColumn+1].matches(\"┐\")");
                                /*line.diagram.get(dn)={
                                 * 　　・
                                 * 　　・
                                 * 　　・
                                 * {"稲毛"  ,"発","0538"               ,""  ,...},//←i-1行インデックス
                                 * {"千葉"  ,"発","0546"               ,"┐",...},//←i行インデックス
                                 * {"東千葉","発",""(goalTimeStrはここ),"レ",...},//←i+1行インデックス
                                 * 　　・
                                 * 　　・
                                 * 　　・
                                 * }
                                 * の位置に┐がある場合
                                 */
                                i=i-1;//continue後,このiにi++されることに注意
                                trainColumn++;
                                if(trainColumn>=dia.get(dn)[0].length) {
                                	String week = dn==0 ? "平日" : "土休日";
                                	System.out.println("Exception first1Search1Line1Station1Day 2:\n"+line.getName()+" "+week+" の時刻表の"+(i+1)+"行目と"+
                                "1行目の要素数が一致しません");
                                	return null;
                                }
                                trainIDList.add(new TrainID(noise_Remove(dia.get(dn)[0][trainColumn]),i,trainColumn));
                                continue;
                            }
                            //列車番号が変わらないときはこの列車trainColumnではstart止まりなので別の列車を探す
                            break;
                            /*line.diagram.get(dn)={
                             * 　　・
                             * 　　・
                             * 　　・
                             * {"武蔵増戸"  ,"発","0919"               ,"",...},//←i-2行インデックス
                             * {"武蔵五日市","発","0923"               ,"",...},//←i-1行インデックス
                             * {"牛浜"      ,"発",""(goalTimeStrはここ),"",...},//←i行インデックス
                             * 　　・
                             * 　　・
                             * 　　・
                             * }、
                             *line.diagram.get(dn)={
                             * 　　・
                             * 　　・
                             * 　　・
                             * {"武蔵増戸"  ,"発","0919"               ,"",...},//←i-3行インデックス
                             * {"武蔵五日市","着","0923"               ,"",...},//←i-2行インデックス
                             * {"武蔵五日市","発","0924"               ,"",...},//←i-1行インデックス
                             * {"牛浜"      ,"発",""(goalTimeStrはここ),"",...},//←i行インデックス
                             * 　　・
                             * 　　・
                             * 　　・
                             * }
                             * 、
                             * line.diagram.get(dn)={//この例に当てはまることはおそらくないが一応示す
                             * 　　・
                             * 　　・
                             * 　　・
                             * {"武蔵増戸"  ,"発","0919"               ,"",...},//←i-2行インデックス
                             * {"武蔵五日市","発","0923"               ,"",...},//←i-1行インデックス
                             * {"終着"      ,""  ,""(goalTimeStrはここ),"",...}...//←i行インデックス 　　
                             * }
                             * のときは全てstart武蔵五日市はtrainCulmnでは終着駅となるので、別の列車を探す.
                             */
                        }else if(goalTimeStr.matches("＝")||goalTimeStr.matches("=")) {
                            if(trainColumn+1<dia.get(dn)[i].length &&
                                    noise_Remove(dia.get(dn)[i-1][trainColumn+1]).matches("┐")) {
                                //同じ時刻表内で列車番号が変わるとき1
                                /*line.diagram.get(dn)={
                                 * 　　・
                                 * 　　・
                                 * 　　・
                                 * {"町屋"  ,"発","0723"                 ,""    ,...},
                                 * {"北千住","発","0726"                 ,"┐"  ,...},//←i-1行インデックス
                                 * {"綾瀬"  ,"発","＝"(goalTimeStrはここ),"0730",...},//←i行インデックス
                                 * {"北綾瀬","着",""                     ,"||"  ,...},
                                 * 　　・
                                 * 　　・
                                 * 　　・
                                 * }
                                 * の位置に┐がある場合
                                 */
                                i=i-1;//continue後,このiに+1されることに注意
                                trainColumn++;
                                if(trainColumn>=dia.get(dn)[0].length) {
                                	String week = dn==0 ? "平日" : "土休日";
                                	System.out.println("Exception first1Search1Line1Station1Day 3:\n"+line.getName()+" "+week+" の時刻表の"+(i+1)+"行目と"+
                                "1行目の要素数が一致しません");
                                	return null;
                                }
                                trainIDList.add(new TrainID(noise_Remove(dia.get(dn)[0][trainColumn]),i,trainColumn));
                                continue;
                            }else if(trainColumn+1<dia.get(dn)[i].length &&
                            		noise_Remove(dia.get(dn)[i-2][trainColumn+1]).matches("┐")) {//同じ時刻表内で列車番号が変わるとき2
                                /*line.diagram.get(dn)={
                                 * 　　・
                                 * 　　・
                                 * 　　・
                                 * {"町屋"  ,"発","0723"            ,""    ,...},
                                 * {"北千住","発","0726"            ,"┐"  ,...},//←i-2行インデックス
                                 * {"綾瀬"  ,"発","0729"            ,"0730",...},//←i-1行インデックス
                                 * {"北綾瀬","着","＝"(diaStrはここ),"||"  ,...},//←i行インデックス
                                 * {"亀有"  ,"発",""                ,"0733",...},
                                 * 　　・
                                 * 　　・
                                 * 　　・
                                 * }
                                 * や
                                 * line.diagram.get(dn)={
                                 * 　　・
                                 * 　　・
                                 * 　　・
                                 * {"町屋"  ,"発","0723"            ,""    ,...},
                                 * {"北千住","発","0726"            ,""    ,...},//←i-3行インデックス
                                 * {"綾瀬"  ,"着","0729"            ,"┐"  ,...},//←i-2行インデックス
                                 * {"綾瀬"  ,"発","0730"            ,"0730",...},//←i-1行インデックス
                                 * {"北綾瀬","着","＝"(diaStrはここ),"||"  ,...},//←i行インデックス
                                 * {"亀有"  ,"発",""                ,"0733",...},
                                 * 　　・
                                 * 　　・
                                 * 　　・
                                 * }
                                 * の位置に┐がある場合
                                 */
                                i=i-1;//continue後,このiに+1されることに注意
                                trainColumn++;
                                if(trainColumn>=dia.get(dn)[0].length) {
                                	String week = dn==0 ? "平日" : "土休日";
                                	System.out.println("Exception first1Search1Line1Station1Day 4:\n"+line.getName()+" "+week+" の時刻表の"+(i+1)+"行目と"+
                                "1行目の要素数が一致しません");
                                	return null;
                                }
                                trainIDList.add(new TrainID(noise_Remove(dia.get(dn)[0][trainColumn]),i,trainColumn));
                                continue;
                            }
                            //列車番号が変わらないときはこの列車trainColumnではstart止まりなので別の列車を探す
                            break;
                        }else if(goalTimeStr.matches("┘")) {//連結
                            i=i-1;
                            trainColumn--;
                            trainIDList.add(new TrainID(noise_Remove(dia.get(dn)[0][trainColumn]),i,trainColumn));
                            continue;
                            /*
                             * line.diagram.get(dn)={
                             * 　　・
                             * 　　・
                             * 　　・
                             * {"品川"    ,"発","0949","レ"                   ,...},//←i-1行インデックス
                             * {"東京"    ,"発","1003","┘"(goalTimeStrはここ),...},//←i行インデックス(他の例(ここでは省略)も含め┘は必ず連結した駅の発着時間の欄にあると思われる)
                             * {"秋葉原"  ,"発","||"  ,""                     ,...},//←i+1行インデックス
                             * 　　・
                             * 　　・
                             * 　　・
                             * }
                             * や
                             * line.diagram.get(dn)={//こんな例はおそらくないと思われるが念のため示す
                             * 　　・
                             * 　　・
                             * 　　・
                             * {"品川"    ,"発","0949","レ"                   ,...},//←i-2行インデックス
                             * {"東京番線",""  ,"-4"  ,"-4"                   ,...},//←i-1行インデックス
                             * {"東京"    ,"発","1003","┘"(goalTimeStrはここ),...},//←i行インデックス(他の例(ここでは省略)も含め┘は必ず連結した駅の発着時間の欄にあると思われる)
                             * {"秋葉原"  ,"発","||"  ,""                     ,...},//←i+1行インデックス
                             * 　　・
                             * 　　・
                             * 　　・
                             * }
                             */
                        }else if(goalTimeStr.matches("レ")) {//通過
                            continue;//この列車trainColumnで次行ける駅を探す
                        }else if(goalTimeStr.contains("||") ) {//別のルート
                            continue;//この列車trainColumnで次行ける駅を探す
                        }else if(goalTimeStr.matches("・")) {//別のルート
                            continue;//この列車trainColumnで次行ける駅を探す
                        }else if(goalTimeStr.matches("\\([^0-9]*[0-9]+\\)")||goalTimeStr.matches("（[^0-9]*[0-9]+）")
                                ||goalTimeStr.matches("-[0-9]+")) {//-2や(2),(京1)など番線が書かれている場合
                            if(i+1<dia.get(dn).length) {
                                /*
                                 *
                                 */
                                continue;//この列車trainColumnで次行ける駅を探す
                            }else if(i==dia.get(dn).length-1) {//おそらくこの条件に当てはまることはない
                                //startはこの列車trainColumnの終着なので、次以降の列車を探す
                                break;
                                /*line.diagram.get(dn)={//おそらくこの例はないと思われるが一応考慮
                                 * 　　・
                                 * 　　・
                                 * 　　・
                                 * {"長町"    ,"発","0619"                  ,"0635",...},//←i-2行インデックス
                                 * {"仙台"    ,"発","0624"                  ,"0641",...},//←i-1行インデックス
                                 * {"仙台番線",""  ,"(6)"(goalTimeStrはここ),"(4)" ,...} //←i行インデックス
                                 * }
                                 */
                            }
                            System.out.println("Exception first1Search1Line1Station1Day: out of index Errorがでない");
                            continue;
                        }else if(noise_Remove(dia.get(dn)[i][0]).matches("終着")) {
                            //時刻表の作り上、この条件に当てはまることはおそらくない
                            break;//startはこの列車trainColumnの終着なので、次以降の列車を探す
                            /*line.diagram.get(dn)={//おそらくこの例はないと思われるが一応考慮
                             * 　　・
                             * 　　・
                             * 　　・
                             * {"長町","発","0619"               ,"0635",...},//←i-2行インデックス
                             * {"仙台","発","0624"               ,"0641",...},//←i-1行インデックス
                             * {"終着",""  ,""(goalTimeStrはここ),""    ,...} //←i行インデックス
                             * }
                             */
                        }
                        System.out.println("Exception first1Search1Line1Station1Day: 時刻表内の"+goalTimeStr+"は想定してない文字列です");
                        break;
                    }
                }//for(i++)              
            }//if(getOnTime>firstStartTime && isFreeTrain(line,date,trainColumn) && isOperated(line,date,trainColumn))
            //getOnTimeが-1もしくは列車trainColumnがこの日(date)運転日でない場合、別の列車を探す
        }//for(trainColumn++)
        //この日行ける未調査の駅はない
        return null;
    }
    
    //この日"真の出発駅"でないgoalに行くまでの途中駅startから1駅で行ける駅で未調査の駅を探す.
    /*private TransferResult first1Search1Line1Station1Day(Station start,Line line,
            Calendar date, int firstStartTime, int startRow, Data data, ArrayList<Station> doneStationList) {
     */
    private TransferResult search1Line1Station1Day(Station start,Line line,
            Calendar date, int firstStartTime, int startRow,
            int trainColumn, Data data, ArrayList<Station> doneStationList) {
    	//System.out.println("search1Line1Station1Day");
    	//System.out.println(line.getName());
    	//System.out.println("startRow = "+startRow);
    	//System.out.println("trainColumn = "+trainColumn);
        int dn = line.diaNum(date);
        ArrayList<String[][]> dia = line.getDiagram();
        
      //startRowは時刻表にstartがある行インデックス(必ず発時刻)
        int getOnTime = parseTime(noise_Remove(dia.get(dn)[startRow][trainColumn]));//parseTimeは時刻(4桁以下のint)でない場合-1を返す
        //getOnTimeはstart駅から乗れる列車の発車時刻(185500など)
        if(getOnTime>=firstStartTime && isFreeTrain(line,date,trainColumn)
                && isOperated(line,date,trainColumn)) {
            //isFreeTrainとは追加料金のかからない列車かどうか
            //isOperatedとはこの列車jがこの日dateで運転しているか
            ArrayList<TrainID> trainIDList = new ArrayList<>();//列車番号を格納する、列車番号が途中で変わる場合も考慮し配列として格納
            for(int i=startRow+1; i<dia.get(dn).length; i++) {//列車trainColum(行)に乗って1駅で行ける未調査の駅を探す
                //startがstartRow行インデックスにあるので探すgoalはそれよりは1つ下という意味でi=startRow+1
                String goalTimeStr = noise_Remove(dia.get(dn)[i][trainColumn]);//goalTimeStrには候補となり得るgoalの到着時刻などになる
                Station goal = parseStation(noise_Remove(dia.get(dn)[i][0]), line,data);
                //goalTimeStrは発時刻でも今回は着時刻とみなすので問題ない(また発着時刻が両方書かれる駅の場合も必ず上の行に着時刻が書かれている)
                if(isTimeStr(goalTimeStr) ) {//時刻が格納されているとき
                    //千代田線など検索範囲外の駅名の場合はgoalがnullになる可能性を考慮しgoal==nullのときは飛ばす(別の列車を探す)
                    if(doneStationList.contains(goal)/*isInStationList(goal,doneStationList)*/ || goal==null) {//調査済の駅のとき別の列車trainColumnを探す
                        //System.out.println("isInStationList(goal,doneStationList) || goal==null");
                        break;//別の列車trainColumnを探す
                    }

                    if(goal.equals(start)) {//startRowの行で着時刻だったとき,その下に同じ駅の発時刻が書いてある場合
                        //System.out.println("Exception search1Line1Station: start=goalで、startRowが着時刻の可能性がある");
                        //System.out.println("elseの空文字や┐などの処理が適切でない可能性");
                        //System.out.println("想定外の時刻表の並びの可能性もあり");
                        //System.out.println("時刻表の駅名の取得の仕方が間違っている可能性");
                        //System.out.println("hint: 発・着の除外など");
                        continue;
                    }
                    /*
                     * line.diagram.get(dn)={
                     * 　　・
                     * 　　・
                     * 　　・
                     * {"品川"    ,"発"                ,"1833"                   ,"",...},//←i-2行インデックス
                     * {"東京番線",""(発でも着でもない),"(4)"                    ,"",...},//←i-1行インデックス
                     * {"東京"    ,"発"                ,"1841"(goalTimeStrはここ),"",...},//←i行インデックス
                     * 　　・
                     * 　　・
                     * 　　・
                     * }
                     * も東京が次の駅となる.
                     */
                    
                    int goalTime = parseTime(goalTimeStr);
                    //goalTimeStrは発時刻でも今回は着時刻とみなすので問題ない
                    //(また発着時刻が両方書かれる駅の場合も必ず上の行に着時刻が書かれている)
                    Terminal terminalData = getTerminalData(line,trainColumn,date);
                    int startNum = getStartNum(start,line,trainColumn,date);
                    int goalNum = getGoalNum(goal,line,trainColumn,date);
                    TransferResult result = new TransferResult(start,goal,date,getOnTime,goalTime,line,terminalData
                            ,-1,startNum,goalNum,false,false);
                    result.addStationFromLine(start,goal,line);
                    result.addRealStation(start);
                    result.addRealStation(goal);
                    if(noise_Remove(dia.get(dn)[0][0]).contains("列車番号")) {
                        trainIDList.add(new TrainID(noise_Remove(dia.get(dn)[0][trainColumn]),i,trainColumn));
                        result.addAllTrainID(trainIDList);
                    }else {// 全ての時刻表に列車番号は書かれているのでこのelseは通ることはない
                        //result.addTrainID("");
                    	System.out.println("時刻表の(0,0): "+noise_Remove(dia.get(dn)[0][0]));
                        System.out.println("Exception search1Line1Station1Day:\n"+line.getName()+"の時刻表に列車番号が書かれていない");
                        trainIDList.add(new TrainID("",i, trainColumn));
                        result.addAllTrainID(trainIDList);
                    }
                    return result;
                    //ここでこれより早くgoalに着く列車がないかについてだが、結論はこれで問題ない.
                    //同じ路線、同じ列車種別(普通、快速など)の中では出発時刻が早い列車が最も早く到着する.(追い越されることはない)
                    //同じ路線で別の列車種別が早く到着する例があるが(快速の方が遅く出発して、早く到着するなど)は既にA*のOPENリストに格納されているため、そのOPENリストのソート時に除外できる.
                    //別の路線が早く到着するときもOPENリストのソート時に除外できる.
                }else {//elseに来てもレや||では行けることがある
                    //=や空文字でも列車番号が変わるときや切り離しがある場合などは次に行けることがあるので注意
                    if(goalTimeStr.matches("")) {
                        //System.out.println("goalTimeStr.matches(\"\")");
                        if(trainColumn+1<dia.get(dn)[i].length &&
                                noise_Remove(dia.get(dn)[i-2][trainColumn+1]).matches("┐")) {//同じ時刻表内で列車番号が変わるとき2
                            /*line.diagram.get(dn)={
                             * 　　・
                             * 　　・
                             * 　　・
                             * {"稲毛"  ,"発","0538"               ,"┐"  ,...},//←i-2行インデックス
                             * {"千葉"  ,"発","0546"               ,"0546",...},//←i-1行インデックス
                             * {"東千葉","発",""(goalTimeStrはここ),"レ"  ,...},//←i行インデックス
                             * 　　・
                             * 　　・
                             * 　　・
                             * }
                             * の位置に┐がある場合
                             */
                            i=i-1;//continue後,このiに+1されることに注意
                            trainColumn++;
                            if(trainColumn>=dia.get(dn)[0].length) {
                            	String week = dn==0 ? "平日" : "土休日";
                            	System.out.println("Exception search1Line1Station1Day 1:\n"+line.getName()+" "+week+" の時刻表の"+(i+1)+"行目と"+
                            "1行目の要素数が一致しません");
                            	return null;
                            }
                            trainIDList.add(new TrainID(noise_Remove(dia.get(dn)[0][trainColumn]),i,trainColumn));
                            continue;
                        }else if(trainColumn+1<dia.get(dn)[i].length &&
                        		noise_Remove(dia.get(dn)[i-1][trainColumn+1]).matches("┐")) {//同じ時刻表内で列車番号が変わるとき2
                            //System.out.println("dia.get(dn)[i-1][trainColumn+1].matches(\"┐\")");
                            /*line.diagram.get(dn)={
                             * 　　・
                             * 　　・
                             * 　　・
                             * {"稲毛"  ,"発","0538"               ,""  ,...},//←i-1行インデックス
                             * {"千葉"  ,"発","0546"               ,"┐",...},//←i行インデックス
                             * {"東千葉","発",""(goalTimeStrはここ),"レ",...},//←i+1行インデックス
                             * 　　・
                             * 　　・
                             * 　　・
                             * }
                             * の位置に┐がある場合
                             */
                            i=i-1;//continue後,このiにi++されることに注意
                            trainColumn++;
                            if(trainColumn>=dia.get(dn)[0].length) {
                            	String week = dn==0 ? "平日" : "土休日";
                            	System.out.println("Exception search1Line1Station1Day 2:\n"+line.getName()+" "+week+" の時刻表の"+(i+1)+"行目と"+
                            "1行目の要素数が一致しません");
                            	return null;
                            }
                            trainIDList.add(new TrainID(noise_Remove(dia.get(dn)[0][trainColumn]),i,trainColumn));
                            continue;
                        }
                        //列車番号が変わらないときはこの列車trainColumnではstart止まりなので別の列車を探す
                        break;
                        /*line.diagram.get(dn)={
                         * 　　・
                         * 　　・
                         * 　　・
                         * {"武蔵増戸"  ,"発","0919"               ,"",...},//←i-2行インデックス
                         * {"武蔵五日市","発","0923"               ,"",...},//←i-1行インデックス
                         * {"牛浜"      ,"発",""(goalTimeStrはここ),"",...},//←i行インデックス
                         * 　　・
                         * 　　・
                         * 　　・
                         * }、
                         *line.diagram.get(dn)={
                         * 　　・
                         * 　　・
                         * 　　・
                         * {"武蔵増戸"  ,"発","0919"               ,"",...},//←i-3行インデックス
                         * {"武蔵五日市","着","0923"               ,"",...},//←i-2行インデックス
                         * {"武蔵五日市","発","0924"               ,"",...},//←i-1行インデックス
                         * {"牛浜"      ,"発",""(goalTimeStrはここ),"",...},//←i行インデックス
                         * 　　・
                         * 　　・
                         * 　　・
                         * }
                         * 、
                         * line.diagram.get(dn)={//この例に当てはまることはおそらくないが一応示す
                         * 　　・
                         * 　　・
                         * 　　・
                         * {"武蔵増戸"  ,"発","0919"               ,"",...},//←i-2行インデックス
                         * {"武蔵五日市","発","0923"               ,"",...},//←i-1行インデックス
                         * {"終着"      ,""  ,""(goalTimeStrはここ),"",...}...//←i行インデックス 　　
                         * }
                         * のときは全てstart武蔵五日市はtrainCulmnでは終着駅となるので、別の列車を探す.
                         */
                    }else if(goalTimeStr.matches("＝")||goalTimeStr.matches("=")) {
                        if(trainColumn+1<dia.get(dn)[i].length &&
                        		noise_Remove(dia.get(dn)[i-1][trainColumn+1]).matches("┐")) {
                            //同じ時刻表内で列車番号が変わるとき1
                            /*line.diagram.get(dn)={
                             * 　　・
                             * 　　・
                             * 　　・
                             * {"町屋"  ,"発","0723"                 ,""    ,...},
                             * {"北千住","発","0726"                 ,"┐"  ,...},//←i-1行インデックス
                             * {"綾瀬"  ,"発","＝"(goalTimeStrはここ),"0730",...},//←i行インデックス
                             * {"北綾瀬","着",""                     ,"||"  ,...},
                             * 　　・
                             * 　　・
                             * 　　・
                             * }
                             * の位置に┐がある場合
                             */
                            i=i-1;//continue後,このiに+1されることに注意
                            trainColumn++;
                            if(trainColumn>=dia.get(dn)[0].length) {
                            	String week = dn==0 ? "平日" : "土休日";
                            	System.out.println("Exception search1Line1Station1Day 3:\\n"+line.getName()+" "+week+" の時刻表の"+(i+1)+"行目と"+
                            "1行目の要素数が一致しません");
                            	return null;
                            }
                            trainIDList.add(new TrainID(noise_Remove(dia.get(dn)[0][trainColumn]),i,trainColumn));
                            continue;
                        }else if(trainColumn+1<dia.get(dn)[i].length &&
                        		noise_Remove(dia.get(dn)[i-2][trainColumn+1]).matches("┐")) {//同じ時刻表内で列車番号が変わるとき2
                            /*line.diagram.get(dn)={
                             * 　　・
                             * 　　・
                             * 　　・
                             * {"町屋"  ,"発","0723"            ,""    ,...},
                             * {"北千住","発","0726"            ,"┐"  ,...},//←i-2行インデックス
                             * {"綾瀬"  ,"発","0729"            ,"0730",...},//←i-1行インデックス
                             * {"北綾瀬","着","＝"(diaStrはここ),"||"  ,...},//←i行インデックス
                             * {"亀有"  ,"発",""                ,"0733",...},
                             * 　　・
                             * 　　・
                             * 　　・
                             * }
                             * や
                             * line.diagram.get(dn)={
                             * 　　・
                             * 　　・
                             * 　　・
                             * {"町屋"  ,"発","0723"            ,""    ,...},
                             * {"北千住","発","0726"            ,""    ,...},//←i-3行インデックス
                             * {"綾瀬"  ,"着","0729"            ,"┐"  ,...},//←i-2行インデックス
                             * {"綾瀬"  ,"発","0730"            ,"0730",...},//←i-1行インデックス
                             * {"北綾瀬","着","＝"(diaStrはここ),"||"  ,...},//←i行インデックス
                             * {"亀有"  ,"発",""                ,"0733",...},
                             * 　　・
                             * 　　・
                             * 　　・
                             * }
                             * の位置に┐がある場合
                             */
                            i=i-1;//continue後,このiに+1されることに注意
                            trainColumn++;
                            if(trainColumn>=dia.get(dn)[0].length) {
                            	String week = dn==0 ? "平日" : "土休日";
                            	System.out.println("Exception search1Line1Station1Day 4:\n"+line.getName()+" "+week+" の時刻表の"+(i+1)+"行目と"+
                            "1行目の要素数が一致しません");
                            	return null;
                            }
                            trainIDList.add(new TrainID(noise_Remove(dia.get(dn)[0][trainColumn]),i,trainColumn));
                            continue;
                        }
                        //列車番号が変わらないときはこの列車trainColumnではstart止まりなので別の列車を探す
                        break;
                    }else if(goalTimeStr.matches("┘")) {//連結
                        i=i-1;
                        trainColumn--;
                        trainIDList.add(new TrainID(noise_Remove(dia.get(dn)[0][trainColumn]),i,trainColumn));
                        continue;
                        /*
                         * line.diagram.get(dn)={
                         * 　　・
                         * 　　・
                         * 　　・
                         * {"品川"    ,"発","0949","レ"                   ,...},//←i-1行インデックス
                         * {"東京"    ,"発","1003","┘"(goalTimeStrはここ),...},//←i行インデックス(他の例(ここでは省略)も含め┘は必ず連結した駅の発着時間の欄にあると思われる)
                         * {"秋葉原"  ,"発","||"  ,""                     ,...},//←i+1行インデックス
                         * 　　・
                         * 　　・
                         * 　　・
                         * }
                         * や
                         * line.diagram.get(dn)={//こんな例はおそらくないと思われるが念のため示す
                         * 　　・
                         * 　　・
                         * 　　・
                         * {"品川"    ,"発","0949","レ"                   ,...},//←i-2行インデックス
                         * {"東京番線",""  ,"-4"  ,"-4"                   ,...},//←i-1行インデックス
                         * {"東京"    ,"発","1003","┘"(goalTimeStrはここ),...},//←i行インデックス(他の例(ここでは省略)も含め┘は必ず連結した駅の発着時間の欄にあると思われる)
                         * {"秋葉原"  ,"発","||"  ,""                     ,...},//←i+1行インデックス
                         * 　　・
                         * 　　・
                         * 　　・
                         * }
                         */
                    }else if(goalTimeStr.matches("レ")) {//通過
                        continue;//この列車trainColumnで次行ける駅を探す
                    }else if(goalTimeStr.contains("||")) {//別のルート
                        continue;//この列車trainColumnで次行ける駅を探す
                    }else if(goalTimeStr.matches("・")) {//別のルート
                        continue;//この列車trainColumnで次行ける駅を探す
                    }else if(goalTimeStr.matches("\\([^0-9]*[0-9]+\\)")||goalTimeStr.matches("（[^0-9]*[0-9]+）")
                            ||goalTimeStr.matches("-[0-9]+")) {//-2や(2),(京1)など番線が書かれている場合
                        if(i+1<dia.get(dn).length) {
                            /*
                             *
                             */
                            continue;//この列車trainColumnで次行ける駅を探す
                        }else if(i==dia.get(dn).length-1) {//おそらくこの条件に当てはまることはない
                            //startはこの列車trainColumnの終着なので、次以降の列車を探す
                            break;
                            /*line.diagram.get(dn)={//おそらくこの例はないと思われるが一応考慮
                             * 　　・
                             * 　　・
                             * 　　・
                             * {"長町"    ,"発","0619"                  ,"0635",...},//←i-2行インデックス
                             * {"仙台"    ,"発","0624"                  ,"0641",...},//←i-1行インデックス
                             * {"仙台番線",""  ,"(6)"(goalTimeStrはここ),"(4)" ,...} //←i行インデックス
                             * }
                             */
                        }
                        System.out.println("Exception search1Line1Station1Day 1:\n out of index Errorがでない");
                        continue;
                    }else if(noise_Remove(dia.get(dn)[i][0]).matches("終着")) {
                        //時刻表の作り上、この条件に当てはまることはおそらくない
                        break;//startはこの列車trainColumnの終着なので、次以降の列車を探す
                        /*line.diagram.get(dn)={//おそらくこの例はないと思われるが一応考慮
                         * 　　・
                         * 　　・
                         * 　　・
                         * {"長町","発","0619"               ,"0635",...},//←i-2行インデックス
                         * {"仙台","発","0624"               ,"0641",...},//←i-1行インデックス
                         * {"終着",""  ,""(goalTimeStrはここ),""    ,...} //←i行インデックス
                         * }
                         */
                    }
                    System.out.println("Exception search1Line1Station1Day 1:\n 時刻表内の'"+goalTimeStr+"'は想定してない文字列です");
                    break;
                }
            }//for(i++)              
        }//if(getOnTime>firstStartTime && isFreeTrain(line,date,trainColumn) && isOperated(line,date,trainColumn))
        //getOnTimeが-1もしくは列車trainColumnがこの日(date)運転日でない場合
        //この列車で次の駅に行けるかは、時刻表からはわからない
        //(直通運転のように時刻表をまたぐ場合や山手線のように参照する列が飛ぶ場合はcombineで判定する)
        return null;
    }
/*
    //stationがstationListに入っているかどうか nullはfalseと返す
    private boolean isInStationList(Station station, ArrayList<Station> stationList) {
        if(station==null) {
        	return false;
        }
    	for(Station s: stationList) {
            if(station.equals(s)) {
                return true;
            }
        }
        return false;
    }
    private boolean isInStationList(Station station, Station[] stationList) {
    	if(station==null) {
        	return false;
        }
    	for(Station s: stationList) {
            if(station.equals(s)) {
                return true;
            }
        }
        return false;
    }
*/

    //出発駅の番線
    private int getStartNum(Station start,Line line, int trainColumn, Calendar date) {
        return -1;
    }

    //到着駅の番線
    private int getGoalNum(Station goal,Line line, int trainColumn, Calendar date) {
        return -1;
    }

    private boolean isTimeStr(String diaStr) {
        if(parseTime(diaStr)==-1) {
            return false;
        }else {
            return true;
        }
    }

    //isFreeTrainとは追加料金のかからない列車かどうか
    private boolean isFreeTrain(Line line, Calendar date, int trainColumn) {
        int dn = line.diaNum(date);
        ArrayList<String[][]> dia = line.getDiagram();
        for(int i=0; i<dia.get(dn).length; i++) {
            if(noise_Remove(dia.get(dn)[i][0]).equals("列車名")) {
                String trainType = dia.get(dn)[i][trainColumn];
                if(trainType.contains("特急")
                        ||trainType.contains("ＢＡＳＥ")||trainType.contains("BASE")) {
                    return false;
                }else {//普通料金の列車
                    return true;
                }
            }
        }
        System.out.println("Exception: 列車名という表記がない");
        return true;
    }

    //isOperatedとはこの列車trainColumnがこの日dateで運転しているか
    private boolean isOperated(Line line, Calendar date, int trainColumn) {
        int dn = line.diaNum(date);
        ArrayList<String[][]> dia = line.getDiagram();
        for(int i=0; i<dia.get(dn).length; i++) {
            if(noise_Remove(dia.get(dn)[i][0]).equals("運転日")) {
                String day = noise_Remove(dia.get(dn)[i][trainColumn]);
                if(day.matches("平日")||day.matches("土休日")||day.matches("全日")) {
                    //dateから実際に平日か土休日かが合致してるかを確認してもよい
                    return true;
                }else if(day.matches("時変")) {
                    //時変にも対応予定
                    return false;
                }else if(day.matches("◆.*")) {
                    //特定の日にしか出ない列車は対応予定なし(特急などが多いため)
                    return false;
                }else {
                    //System.out.println("運転日は"+day);
                    return false;
                }
            }
        }
        System.out.println("Exception: 運転日という表記がない");
        return true;
    }

    //各TransferResultの行き先(到着駅)のどれかにstationがあるか
    private boolean isInTransferList(Station station, ArrayList<TransferResult> transferList) {
        for(TransferResult t: transferList) {
            if(station==t.getGoal()) {
                return true;
            }
        }
        return false;
    }

    private String noise_Remove(String underNoiseString) {
    	if(underNoiseString==null) {
    		System.out.printf("noise_Remove 時刻表にnullがある可能性があります");
    		return "";
    	}
        String str = underNoiseString.replaceAll(" ", "");
        return str.replaceAll("_.*","");
    }

    private int parseTime(String timeHourMinStr) {//parseTime(4桁の00:00～23:59の時間(String)を3:00:00～26:59:00の6桁の時刻(int)に変換する.できないときは-1を返す)
        timeHourMinStr = noise_Remove(timeHourMinStr);
        if(timeHourMinStr.matches("[0-9]")) {
            timeHourMinStr = "000"+timeHourMinStr;
        }else if(timeHourMinStr.matches("[0-9]{2}")){
            timeHourMinStr = "00"+timeHourMinStr;
        }else if(timeHourMinStr.matches("[0-9]{3}")){
            timeHourMinStr = "0"+timeHourMinStr;
        }

        if(timeHourMinStr.matches("[0-9]{2}[0-5][0-9]")) {
            int time = Integer.parseInt(timeHourMinStr)*100;
            int over24Time = time;
            if(time<30000) {
                over24Time = time + 240000;
            }
            return over24Time;
        }
        return -1;
    }

    private float estCost(Station start, Station goal, Data data) {// startからgoalまでの推定時間(sec)
    	/*
	    private final float SPEED = 130;//予測コストに使う列車の最高速度130km/h
	    private final float ACCELERATION = (float)3.3;//予測コストに使う列車の最高加速度3.3km/(h・s)
	    private final int STOP_SEC = 15;//(s)予測コストに使う駅で停車する最短時間(s)
	    */
    	
    	float d = (float) (Dist.distance(start, goal));
    	
    	//最小の駅停車数
    	float stopStationNum = (float) Math.ceil(d/data.getMaxNextDistance());
    	
    	float interval = d/stopStationNum;
    	
    	if(SPEED*SPEED/ACCELERATION >= interval) {
    		return (float) (120 * Math.sqrt(interval/ACCELERATION) + STOP_SEC*(stopStationNum-1));
    	}else {
    		return (float) (SPEED/ACCELERATION + 60*60*interval/SPEED + STOP_SEC*(stopStationNum-1));
    	}
    	
    	/*
    	float d = (float) (Dist.distance(start, goal));
    	if(SPEED*SPEED/ACCELERATION >= d) {
    		return (float) (120 * Math.sqrt(d/ACCELERATION) );
    	}else {
    		return (float) (SPEED/ACCELERATION + 60*60*d/SPEED );
    	}
    	*/
        //return (float) ( 60 * 60 * Dist.distance(start, goal) / SPEED);
        //return 0;
    }
    
    //一番うしろのTransferResultを受け取って
    //IC運賃(円)を返す.tの運賃未確定区間までの運賃
    public static int fare(TransferResult t) {
    	TransferResult clone = t.clone();
    	while(clone.getPrevious()!=null && clone.getPrevious().getFare()==-1) {
    		clone = clone.getPrevious().clone();
    	}
    	int fare = Dist.fare(clone.getStart(), t.getGoal());
    	//System.out.println(clone.getStart().getName()+"から"+t.getGoal().getName()+"まで"+fare+"円");
    	return fare;
    }

    private Station parseStation(String stationName,Line line, Data data) {
        //return data.parseStation(noise_Remove(stationName),line);
    	Station station = data.searchStation(stationName);
    	/*
    	if(station==null) {
    		System.out.println(stationName+"駅は登録されていません");
    	}else {
    		System.out.println(station.getName()+"駅が登録されています");
    	}
    	*/
    	return station;
    }
}
