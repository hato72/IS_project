package routesearch.data;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import routesearch.data.javafile.Company;
import routesearch.data.javafile.Line;
import routesearch.data.javafile.Station;

public class Data { //アプリケーション起動時に1回きり呼び出される
	//private ArrayList<Station> allStation = new ArrayList<Station>();
	private final HashMap<String,Station> stationMap;
	private float maxNextDistance;
	//private LinkedHashMap<ArrayList<String>,StationBranch> stationBranchMap = new LinkedHashMap<ArrayList<String>,StationBranch>();
	//ファイルパスを取得する
	//↓routesearchがあるパス
	private final String path;
    
	public Data(String path){
		this.path = path;
		
		Company JR_east = new Company("JR東日本");
		
		//ArrayList<Station> allStation = new ArrayList<Station>();
		CreateLine cl = new CreateLine();
		stationMap = cl.createAllStationAndLine(this.path+"data\\datafile\\",JR_east);
		for(Line line : cl.getAllLine()) {
			System.out.println(line.getName());
		}
		//営業キロデータのファイルのパスを指定
		File dir = new File(this.path+"data\\datafile\\salesdistance\\");
		File[] salesdistance = dir.listFiles();

		//営業キロデータのファイル内全てのファイルからデータを取得
		for(File f : salesdistance) {
			// 指定した営業キロファイルのパスと、全駅データが保存されたHashMapを引数に与える
			CreateStationData.ConnectStation(f.getPath(), stationMap);
		}
		
		//maxNextDistance = 0;
		for(Station station : stationMap.values()) {
			for(int i=0; i<station.getBranch().size(); i++) {
				if(station.getBranch().get(i).getDistance()>=maxNextDistance) {
					maxNextDistance = station.getBranch().get(i).getDistance();
				}
			}
		}
		//System.out.println("駅間の最大営業キロは"+maxNextDistance);
		
		ArrayList<String> station2Min = new ArrayList<>();
		station2Min.add("新松戸");
		station2Min.add("池袋");
		station2Min.add("市川");
		station2Min.add("津田沼");
		
		for(String stationName : station2Min) {
			if(stationMap.containsKey(stationName)) {
				stationMap.get(stationName).setTransferMin(2);
			}
		}
		
		ArrayList<String> station3Min = new ArrayList<>();
		station3Min.add("東京");
		station3Min.add("品川");
		station3Min.add("大宮");
		station3Min.add("千葉");
		station3Min.add("新宿");
		station3Min.add("田端");
		station3Min.add("池袋");
		station3Min.add("上野");
		station3Min.add("横浜");
		station3Min.add("秋葉原");
		station3Min.add("日暮里");
		station3Min.add("神田");
		station3Min.add("代々木");
		station3Min.add("西船橋");
		station3Min.add("川崎");
		station3Min.add("大崎");
		station3Min.add("大船");
		station3Min.add("南浦和");
		station3Min.add("西国分寺");
		station3Min.add("拝島");
		station3Min.add("八王子");
		station3Min.add("錦糸町");
		for(String stationName : station3Min) {
			if(stationMap.containsKey(stationName)) {
				stationMap.get(stationName).setTransferMin(3);
			}
		}
		
		ArrayList<String> station4Min = new ArrayList<>();
		station4Min.add("上野");
		station4Min.add("赤羽");
		station4Min.add("武蔵浦和");
		station4Min.add("府中本町");
		station4Min.add("立川");
		for(String stationName : station4Min) {
			if(stationMap.containsKey(stationName)) {
				stationMap.get(stationName).setTransferMin(4);
			}
		}
		
		String stationName = "東京";
		if(stationMap.containsKey(stationName)) {
			/*
			System.out.println(stationName+"駅の乗り入れ路線");
			for(Line line : stationMap.get(stationName).getLineList()) {
				//System.out.println(line.getName());
			}
			*/
			stationMap.get(stationName).addTransferMin("山手線","京浜東北線", 1);
			stationMap.get(stationName).addTransferMin("常磐線快速","青梅線・五日市線", 5);
			stationMap.get(stationName).addTransferMin("京葉線", 10);
			stationMap.get(stationName).addTransferMin("総武線快速・横須賀線", 13);
			stationMap.get(stationName).addTransferMin("総武本線", 13);
			stationMap.get(stationName).addTransferMin("総武線快速・横須賀線","総武本線", 1, true);
			/*
			for(PairTransferSec pair : stationMap.get(stationName).getSpTransferSecList()) {
				//System.out.println("("+pair.get1()+", "+pair.get2()+"): "+pair.getValue()+"秒");
			}
			*/
		}
		
		stationName = "武蔵小杉";
		if(stationMap.containsKey(stationName)) {
			/*
			System.out.println(stationName+"駅の乗り入れ路線");
			for(Line line : stationMap.get(stationName).getLineList()) {
				//System.out.println(line.getName());
			}
			*/
			stationMap.get(stationName).addTransferMin("南武線", 7);
		}
	
		stationName = "新橋";
		if(stationMap.containsKey(stationName)) {
			/*
			System.out.println(stationName+"駅の乗り入れ路線");
			for(Line line : stationMap.get(stationName).getLineList()) {
				//System.out.println(line.getName());
			}
			*/
			stationMap.get(stationName).addTransferMin("総武線快速・横須賀線", 5);
			stationMap.get(stationName).addTransferMin("山手線","上野東京ライン", 4);
			stationMap.get(stationName).addTransferMin("山手線","東海道線", 3);
			stationMap.get(stationName).addTransferMin("山手線","常磐線快速", 3);
		}
		
		stationName = "品川";
		if(stationMap.containsKey(stationName)) {
			/*
			System.out.println("\n"+stationName+"駅の乗り入れ路線");
			for(Line line : stationMap.get(stationName).getLineList()) {
				//System.out.println(line.getName());
			}
			*/
			stationMap.get(stationName).addTransferMin("山手線","上野東京ライン", 4);
			stationMap.get(stationName).addTransferMin("山手線","東海道線", 4);
			stationMap.get(stationName).addTransferMin("京浜東北線・根岸線","東海道線", 4);
		}
		
		stationName = "池袋";
		if(stationMap.containsKey(stationName)) {
			/*
			System.out.println("\n"+stationName+"駅の乗り入れ路線");
			for(Line line : stationMap.get(stationName).getLineList()) {
				//System.out.println(line.getName());
			}
			*/
			stationMap.get(stationName).addTransferMin("埼京線・りんかい線","湘南新宿ライン", 1, true);
			stationMap.get(stationName).addTransferMin("埼京線","湘南新宿ライン", 1, true);
		}
		
		stationName = "大宮";
		if(stationMap.containsKey(stationName)) {
			/*
			System.out.println("\n"+stationName+"駅の乗り入れ路線");
			for(Line line : stationMap.get(stationName).getLineList()) {
				//System.out.println(line.getName());
			}
			*/
			stationMap.get(stationName).addTransferMin("埼京線・りんかい線","京浜東北線・根岸線", 6, true);
			stationMap.get(stationName).addTransferMin("埼京線","京浜東北線・根岸線", 6, true);
			stationMap.get(stationName).addTransferMin("埼京線・りんかい線","湘南新宿ライン", 5, true);
			stationMap.get(stationName).addTransferMin("埼京線","湘南新宿ライン", 5, true);
		}
		
		stationName = "八王子";
		if(stationMap.containsKey(stationName)) {
			/*
			System.out.println("\n"+stationName+"駅の乗り入れ路線");
			for(Line line : stationMap.get(stationName).getLineList()) {
				System.out.println(line.getName());
			}
			*/
			stationMap.get(stationName).addTransferMin("横浜線","八高線", 4, true);
		}
		/*
		System.out.println("↓↓↓登録駅情報↓↓↓");
		int i = 1;
		for(Station s : stationMap.values()) {
			if(s.isComplete()) {
				System.out.println((i++)+": "+s.getName());
			}else {
				continue;
			}
		}
		System.out.println("登録駅は以上");
		*/
		/*
		//ここから...
		
		Station st1= new Station("駅1");
		allStation.add(st1);
		Station st2= new Station("駅2");
		allStation.add(st2);
		Station st3= new Station("駅3");
		allStation.add(st3);
		Station st4= new Station("駅4");
		allStation.add(st4);
		Station st5= new Station("駅5");
		allStation.add(st5);
		Station st6= new Station("駅6");
		allStation.add(st6);
		Station st7= new Station("駅7");
		allStation.add(st7);
		
		Station[] stLine1In = {st1,st2};
		Line line1In = new Line("路線1",stLine1In, false,"",JR_east);//末尾について、上りはin,下りはoutなどなんでもよいので区別する
		
		Station[] stLine1Out = {st2,st1};
		Line line1Out = new Line("路線1",stLine1Out, false,"",JR_east);
		
		st1.addLine(line1In);
		st1.addLine(line1Out);
		
		st2.addLine(line1In);
		st2.addLine(line1Out);
		
		String[][] line1InWeek =
			{{"列車番号","","407B","463C","617B","617A"},
			{"列車名_列車名","","普通_普通","普通_普通","普通_普通","普通_普通"},
			//{"設備","","","","",""},
			{"運転日","","全日","全日","全日","全日"},
			{"駅1_駅1","発","601","610","619","1850"},//←時刻は0601でも601でも大丈夫です60100にはしないでください
			{"駅2_駅2","着","605","616","622","1854"},
			};
		//ここまで平日ダイヤ
		String[][] line1InHoli =
			{{"列車番号","","407B","463C","617A"},
			{"列車名_列車名","","普通_普通","普通_普通","普通_普通"},
			{"運転日","","全日","全日","全日"},
			{"駅1_駅1","発","701","1850","0050"},
			{"駅2_駅2","着","705","1854","0054"},
			};
		
		Station[] stLine2In = {st2,st3,st4,st5};	
		Line line2In = new Line("路線2",stLine2In, false,"",JR_east);
		
		Station[] stLine2Out = {st5,st4,st3,st2};	
		Line line2Out = new Line("路線2",stLine2In, false,"",JR_east);
		st2.addLine(line2In);
		st3.addLine(line2In);
		st4.addLine(line2In);
		st5.addLine(line2In);
		st2.addLine(line2Out);
		st3.addLine(line2Out);
		st4.addLine(line2Out);
		st5.addLine(line2Out);
		String[][] line2InWeek =
			{{"列車番号","","407B","463C","617B","617A"},
			{"列車名_列車名","","普通_普通","普通_普通","普通_普通","普通_普通"},
			//{"設備","","","",""},┐
			{"運転日"         ,""  ,"全日","全日","全日","全日"},
			{"前の区間"        ,""   ,""   ,""   ,""                 ,"↑"},
			{"始発"            ,""   ,""   ,""   ,"駅0_1839_駅0_1839",""},
			{"駅2_駅2"         ,"発" ,"601","610","1850"             ,""},
			{"駅3_駅3"         ,"着" ,"604","613","1853"             ,"┐"},
			{"駅3番線_駅3番線" ,""   ,"-1" ,"-1" ,"-2"               ,"-2"},
			{"駅3_駅3"         ,"発" ,"604","614",""                 ,"1854"},
			{"駅4_駅4"         ,"発" ,"＝" ,"618",""                 ,"レ"},
			{"駅5_駅5"         ,"着" ,""   ,""   ,""                 ,"1902"},
			{"終着"            ,""   ,"___","___","___"              ,"駅10_1930_駅10_1930"},
			{"次の区間"        ,""   ,""   ,""   ,""                 ,"↓"}
			};
		String[][] line2InHoli =
			{{"列車番号","","407B","463C","617B","617A"},
			{"列車名_列車名","","普通_普通","普通_普通","普通_普通","普通_普通"},
			//{"設備","","","",""},
			{"運転日","","全日","全日","全日","全日"},
			{"前の区間",""  ,""   ,""   ,""   ,"↑"},
			{"始発","" ,""  ,""   ,""   ,"駅0_1839_駅0_1839"},
			{"駅2_駅2" ,"発","601","610","619","1850"},
			{"駅3_駅3" ,"発","604","614","623","1854"},
			{"駅4_駅4" ,"発",""   ,"618","627","1858"},
			{"駅5_駅5" ,"着",""   ,""   ,"631","1901"},
			{"終着"    ,""  ,"___","___","___","駅10_1930_駅10_1930"},
			{"次の区間",""  ,""   ,""   ,""   ,"↓"}
			}; 
		 /*
			//* GUIには
			 * -----------
			 * 駅1
			 * ↓路線2
			 * 駅5　(直通運転)
			 * ↓路線3
			 * 駅10
			 * -----------
			 * などと表示される
		 */
		/*
		Station[] stLine3In = {st2,st3,st4,st5,st6};	
		Line line3In = new Line("路線3",stLine3In, false,"",JR_east);
		
		Station[] stLine3Out = {st6,st5,st4,st3,st2};	
		Line line3Out = new Line("路線3",stLine3In, false,"",JR_east);
		st2.addLine(line3In);
		st3.addLine(line3In);
		st4.addLine(line3In);
		st5.addLine(line3In);
		st6.addLine(line3In);
		st2.addLine(line3Out);
		st3.addLine(line3Out);
		st4.addLine(line3Out);
		st5.addLine(line3Out);
		st6.addLine(line3Out);
		String[][] line3InWeek =
			{{"列車番号"       ,""  ,"407B","463C","617B"            ,"617A","712B"},
			{"列車名_列車名"  ,"","普通_普通","普通_普通","普通_普通","普通_普通","普通_普通"},
			//{"設備","","","",""},┐
			{"運転日"         ,""  ,"全日","全日","全日"             ,"全日","全日"},
			{"前の区間"        ,""   ,""   ,""   ,""                 ,"↑"  ,""},
			{"始発"            ,""   ,""   ,""   ,"駅0_1839_駅0_1839",""    ,""},
			{"駅2_駅2"         ,"発" ,"601","610","1850"             ,""    ,""},
			{"駅3_駅3"         ,"発" ,"604","614","1854"             ,"┐"  ,""},
			{"駅4_駅4"         ,"発" ,"＝" ,"618",""                 ,"1858",""},
			{"駅5_駅5"         ,"発" ,""   ,""   ,""                 ,"1903","┐"},
			{"駅6_駅6"         ,"着" ,""   ,""   ,""                 ,""    ,"1908"},
			{"終着"            ,""   ,"___","___","___"              ,"___" ,"駅9_1930_駅10_1930"},
			{"次の区間"        ,""   ,""   ,""   ,""                 ,"↓"                 ,""}
			};
		String[][] line3InHoli =
			{{"列車番号","","407B","463C","617B","617A"},
			{"列車名_列車名","","普通_普通","普通_普通","普通_普通","普通_普通"},
			//{"設備","","","",""},
			{"運転日","","全日","全日","全日","全日"},
			{"前の区間",""  ,""   ,""   ,""   ,"↑"},
			{"始発","" ,""  ,""   ,""   ,"駅0_1839_駅0_1839"},
			{"駅2_駅2" ,"発","601","610","619","1850"},
			{"駅3_駅3" ,"発","604","614","623","1854"},
			{"駅4_駅4" ,"発",""   ,"618","627","1858"},
			{"駅5_駅5" ,"発",""   ,""   ,"631","1903"},
			{"駅6_駅6" ,"着",""   ,""   ,""   ,"1908"},
			{"終着"    ,""  ,"___","___","___","駅10_1930_駅10_1930"},
			{"次の区間",""  ,""   ,""   ,""   ,"↓"}
			}; 
		
		ArrayList<String[][]> line1InDia = new ArrayList<>();
		line1InDia.add(line1InWeek);
		line1InDia.add(line1InHoli);
		
		line1In.setDiagram(line1InDia);
		
		ArrayList<String[][]> line2InDia = new ArrayList<>();
		line2InDia.add(line2InWeek);
		line2InDia.add(line2InHoli);
		
		line2In.setDiagram(line2InDia);
		
		ArrayList<String[][]> line3InDia = new ArrayList<>();
		line3InDia.add(line3InWeek);
		line3InDia.add(line3InHoli);
		
		line3In.setDiagram(line3InDia);
		
		
		//...ここまではあくまでサンプルなので最終的には書き換えて、検索対象の駅をすべて格納する。完成するまではサンプルデータも残す
		this.allStation = allStation;
		*/
	}
	/*
	public boolean isRegisterStation(String string) {
		for(Station station: allStation) {
			if(station.getName().equals(string)) {
				return true;
			}
		}
		return false;
	}
	*/
	/*
	public Station parseStation(String stationStr, Line line) {//Stationに変換できない場合はnull
		for(Station station: allStation) {
			if(station.getName().equals(stationStr)) {
				for(Line l: station.getLineList()) {
					if(l.equals(line)) {
						return station;
					}
				}
			}
		}
		return null;
	}
	*/
	public Station searchStation(String stationStr) {
		if(stationStr==null) {
			return null;
		}
		for(Station s : stationMap.values()) {
			if(s.getName().equals(stationStr)) {
				if(s.isComplete()) {
					return s;
				}else {
					return null;
				}
			}
		}
		/*
		for(Station station: allStation) {
			if(station.getName().equals(stationStr)) {
					return station;
			}
		}
		*/
		return null;
	}
	
	public float getMaxNextDistance() {
		return maxNextDistance;
	}
	/*
	//最終的にはこれは使わない サンプルデータ用
	public Station parseStation(String stationStr) {
		for(Station station: allStation) {
			if(station.getName().equals(stationStr)) {
					return station;
			}
		}
		return null;
	}
	
	public ArrayList<Station> getAllStation(){
		//登録されている駅全てのArrayListを取得
		return allStation;
	}
	*/
}