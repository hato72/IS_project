package routesearch;

import java.util.Calendar;

import javafx.application.Application;
import javafx.stage.Stage;
import routesearch.data.Data;
import routesearch.data.javafile.Station;


//デバッグ用 CUIで表示する
public class CUI extends Application {

    @Override
    public void start(Stage primaryStage) {
    	//↓routesearchの絶対パスを指定する(末尾がroutesearch\\)
    	//String path = "E:\\pleiades\\2023-09\\workspace\\RouteSearch\\src\\routesearch\\";
    	String path = "C:\\pleiades\\2023-12\\workspace\\project_fx\\src\\routesearch\\";
    	//String path = new File("routesearch\\").getAbsolutePath()+"\\";//これで取得できそうならこれ使ってください
    	Data data = new Data(path);
    	
    	//サンプルデータ
    	Station sampleStart = data.searchStation("品川");
    	Station sampleGoal = data.searchStation("久里浜");
    	Calendar date = Calendar.getInstance(); //CalendarクラスはCalendar date = new Calendar();のようには宣言せずこのように宣言するので注意.
    	date.getTime();//現在時刻を取得
    	int year = date.get(Calendar.YEAR);
    	int month = date.get(Calendar.MONTH)+1;
    	int day = date.get(Calendar.DATE);
    	int hour = date.get(Calendar.HOUR_OF_DAY);
    	int min = date.get(Calendar.MINUTE);
    	year = 2023;
    	month = 12;
    	day = 17;
    	hour = 6;
    	min = 35;
    	date.set(year,month-1,day,hour,min,0);
       	int searchNum = 1;
       	boolean isFrom = true; //trueで出発時刻指定、falseで到着時刻指定
    	TransferResult[] res = new TransferResult[searchNum]; //現時点では配列の長さは1としているが、2,3番目に早い経路なども検索できるように拡張予定
    	res = new Transfer().minTimeResult(sampleStart,sampleGoal,date,searchNum,isFrom,data).clone(); //minTimeResult最後の引数は出発時間の指定ならtrue,到着時間の指定ならfalse(現時点ではfalseにしても出発時間で指定されたものとして処理)
    	for(int i=0; i<res.length ;i++) {
	    	if(res[i]!=null) {
	    		System.out.println((i+1)+": ----------------------");
	    		res[i].print(res[i]);//乗換結果を表示
	    		System.out.println("\n-------------------------");
	    	}else {
	    		break;
	    	}
    	}
    	
    	// Dist.javaの動作確認
    	Station start = data.searchStation("大井町");
    	Station goal = data.searchStation("横浜");
    	System.out.println(Dist.distance(start,goal)+"km");//営業キロを表示
    	System.out.println(Dist.fare(start,goal)+"円");//運賃を表示
    	
    	System.out.println("プログラムを終了しました.");
    	return;
    }
    public static void main(String[] args) {
        // CUIアプリケーションを起動する
        Application.launch(args);
    }
}