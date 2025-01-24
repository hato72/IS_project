package routesearch.data;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import routesearch.data.javafile.Company;
import routesearch.data.javafile.Line;
import routesearch.data.javafile.Station;

public class CreateLine { 
	private ArrayList<Line> allLine = new ArrayList<>();
/*
	//デバッグ用のmain関数
    public static void main(String[] args){
    	//CreateLineFromFiles("/pleiades/2023-09/workspace/RouteSearch/src/routesearch/data/");
    	//jsonファイルやcsvファイルが入ってるdatafileのパスを与える
        //　↓記入必須
    	String path = "E:\\pleiades\\2023-09\\workspace\\RouteSearch\\src\\routesearch\\data\\datafile\\";
    	
    	createAllStationAndLine(path,new Company("JR東日本(test)"));
    }
  */  
    public HashMap<String,Station> createAllStationAndLine(String path,Company company){
    	//ArrayList<Station> allStation = new ArrayList<>();
    	
    	//CreateStationDataで全てのStationを生成
    	HashMap<String,Station> stationMap = CreateStationData.CreateStationFromFile(path+"JR_edit.csv");
    	/*
    	for(Station s : stationMap.values()) {
    		allStation.add(s);
    	}
    	*/
    	CreateLineFromFiles(path,stationMap,company);
    	return stationMap;
    }

    
    //jsonファイルやcsvファイルが入ってるファイルのパスを与える
    //　例) "/pleiades/2023-09/workspace/RouteSearch/src/routesearch/data/"
    private void CreateLineFromFiles(String path, HashMap<String,Station> stationMap,Company company)
    {   
        BufferedReader buffReader = null;
        try {
          // LineDictionaryEtoJ.jsonを読み込む
          FileInputStream fileInput = new FileInputStream(path+"LineDictionaryEtoJ.json");
          // バイトストリームをテキスト形式に変換
          InputStreamReader inputStream = new InputStreamReader(fileInput); 
          // テキスト形式のファイルを読み込む
          buffReader = new BufferedReader(inputStream); 

          //1行ずつの格納用変数
          String currentContent;
          //1行目は"{"なので、除外
          currentContent = buffReader.readLine();
          
          while((currentContent = buffReader.readLine()) != null) { 
        	  //路線の日本語名を取得
        	  String lineName = currentContent.replaceAll("[^:]*:", "").replaceAll("[ |　]*\"", "").replace("東京メトロ　", "").replaceAll(" .*","").replaceAll("　.*","" );
        	  if(lineName.equals("東西線") || lineName.contains("新幹線") || lineName.contains("特急")) {
        		//東京メトロ　東西線,新幹線,特急は除外
        		  if((currentContent = buffReader.readLine()) != null) {
        			  continue;
        		  }else {
        			  break;
        		  }
        	  }else if(lineName.equals("南武支線")) {//南武支線は南武線に変える
        		  lineName = "南武線";
        	  }else if(lineName.equals("東北本線（宇都宮線）")) {
        		  lineName = "宇都宮線";
        	  }else if(lineName.equals("常磐線・常磐線（快速）")) {
        		  lineName = "常磐線快速";
        	  }else if(lineName.equals("千代田線・常磐線（各駅停車）")) {
        		  lineName = "常磐線各停";
        	  }else if(lineName.equals("東海道線・伊東線・伊豆急行")) {
        		  lineName = "東海道線";
        	  }else if(lineName.equals("埼京線・川越線・東京臨海高速鉄道（りんかい線）・相鉄線")) {
        		  lineName = "埼京線・りんかい線";
        	  }else if(lineName.equals("総武本線・成田線")) {
        		  lineName = "総武本線";
        	  }else if(lineName.equals("高崎線・上越線")) {
        		  lineName = "高崎線";
        	  }else if(lineName.equals("八高線・川越線")) {
        		  lineName = "八高線";
        	  }
        	  lineName = lineName.replaceAll("（快速）","快速").replaceAll("（各駅停車）","各停");
        	  
        	  System.out.println(lineName);
        	  
        	  currentContent = currentContent.replaceAll(":.*", "").replaceAll("\"", "").replace(" ", "");
        	  //平日の時刻表のcsvファイル名を取得
        	  String weekdaysFile = currentContent+".csv";
        	  if((currentContent = buffReader.readLine()) != null) {
        		  currentContent = currentContent.replaceAll(":.*", "").replaceAll("\"", "").replace(" ", "");
        		  //休日の時刻表のcsvファイル名を取得
        		  String holidaysFile = currentContent+".csv";
        		  createLine(lineName,weekdaysFile,holidaysFile,stationMap,path+"timetable\\",company);
        	  }
          }
        } catch(Exception ex) {
          ex.printStackTrace();
        } finally {
          try{
            buffReader.close(); 
          } catch(Exception ex) {
            ex.printStackTrace();
          }
        }
    }
    
    //Lineを一つ生成する. 平日データ休日データともにあれば生成される.
    private void createLine(String lineName, String weekFile, String holiFile, HashMap<String,Station> stationMap, String path, Company company) {
    	System.out.println(path+weekFile);
    	//weekFileの時刻データを格納
    	//Path pw = Paths.get(path+weekFile);
    	String[][] weekDiagram = CsvToJava2D.Create2D(path+weekFile);
    	if(weekDiagram==null) {
    		return;//weekFileが存在しなかった
    	}
    	//holiFileの時刻データを格納
    	//Path ph = Paths.get(path+holiFile);
    	String[][] holiDiagram = CsvToJava2D.Create2D(path+holiFile);
    	if(holiDiagram==null) {//これを満たすことはないが念のため
    		return;//holiFileが存在しなかった
    	}
    	
    	boolean isLoop = false;
    	String loopStr = "";
    	if(lineName.equals("山手線")) {
    		isLoop = true;
    		if(weekFile.contains("outer")) {
    			loopStr = "外回り";
    		}else {
    			loopStr = "内回り";
    		}
    	}
		//平日でも休日でも駅は変化しないのでweekの方で駅名のみを抽出
    	ArrayList<String> stationNameList = stationNameList(weekDiagram);
		
		//修正箇所(mapに存在しないstaionNameListの要素を省いてからsizeを取得してstaionListを作成することでnullの発生を抑える)
		//これによって、同時にmapからnullが返ってくるのを抑える。
		
		
		int count = 0;
		//nullでない要素数を数えるための処理
		for(String stationName : stationNameList)
		{
			if(stationMap.get(stationName) != null)
			{
				count += 1;
			}
		}
		
		int i = 0;
		//個数分の配列を作成し、そこにnullでないなら格納をする処理
		Station[] stationList = new Station[count];
		for(String stationName : stationNameList)
		{
			if(stationMap.get(stationName) != null)
			{
				stationList[i++] = stationMap.get(stationName);
			}
		}



		/*
    	Station[] stationList = new Station[stationNameList.size()];
    	int i = 0;
    	for(String stationName  : stationNameList) {
    		stationList[i++] = stationMap.get(stationName);
    	}
		*/
    	
    	// lineに渡すstationListはnullを含まないようにする
    	Line line = new Line(lineName,stationList,isLoop,loopStr,company);
    	allLine.add(line);
    	
    	for(Station s : stationList) {
    		if(!s.getLineList().contains(line)) {
    			s.addLine(line);
    		}
    	}
    	
    	ArrayList<String[][]> diagram = new ArrayList<>();
    	diagram.add(weekDiagram);
    	diagram.add(holiDiagram);
    	line.setDiagram(diagram);
    	
    	System.out.println(lineName+"を生成");
    	return;
    }
    
    //時刻表データから駅名のリストを作成
    private ArrayList<String> stationNameList(String[][] diagram){
    	ArrayList<String> stationNameList = new ArrayList<>();
    	/* diagramの1列目から駅名のリストを上から順に重複しないようにリストして
    	 * stationNameListに追加していく
		 * 
		 * [[a,b,c],[d,e,f]]のaとかdを取る。これに対して以下の処理
		 * 1.入れない文字に対するif文作成し判別
		 * 2.stationNameList内に入れようとしている要素が存在していれば除く。
		 * 3.上記の条件を抜けたモノのみを格納処理する。
		 * 4.いらない要素の排除
		 * 
    	*/ 
		//num 行数 count ArrayListの格納個数
		int num = 0;
		int count = 0;
		String object = null;

		while(num < diagram.length)
		{	
			//各行の始めの要素を抽出			
			object = diagram[num][0];

			if(object!=null && object.contains("_"))
			{
				if(object.contains("列車名") || object.contains("番線")) 
				{
					num += 1;	
					continue;
				}

				// 一個前の要素と同じモノがobjectに格納されてれば処理をしない。環状のために始めと同じかどうかも確かめる。
				// 処理を軽くするためにこの方法をとっているが、for文再帰でcount回探してもよい。
				if(count > 0)
				{
					if(object.equals(stationNameList.get(count - 1)) || object.equals(stationNameList.get(0)))
					{
						num += 1;
						continue;
					}
				}

				//	_の位置を取得
				int underscoreIndex = object.indexOf("_");
				// 始めから_前までを抽出
				object = object.substring(0, underscoreIndex);
				stationNameList.add(object);
				count += 1;
			}
			// _が無いor格納処理に成功した際のnum+1処理
			num += 1;
			
			

		}

	  	return stationNameList;
    }
    
    public ArrayList<Line> getAllLine(){
    	return allLine;
    }
}
