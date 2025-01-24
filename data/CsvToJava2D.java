package routesearch.data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CsvToJava2D {
	/*
    public static void main(String[] args) {       
        // For Example
         
        ArrayList<String[][]> test = new ArrayList<String[][]>();
        //Path path = Paths.get("C:\\Users\\yusuk\\OneDrive\\デスクトップ\\プロジェクト課題\\csv_data\\Chuo_Line_Rapid_Downbound_Tokyo-Takao_Saturdays_Holidays.csv");
        Path path = Paths.get("E:\\pleiades\\2023-09\\workspace\\RouteSearch\\src\\routesearch\\data\\Keihin_Tohoku_Line_Negishi_Line_Northbound_Ofuna_-_Yokohama_-_Tokyo_-_Ueno_-_Akabane_-_Omiya_weekdays.csv");
        
        
        String[][] test2 = Create2D(path).get(0);
        for(String[] rowList : test2) {
        	for(String s : rowList) {
        		System.out.printf(s+" ");
        	}
        	System.out.println("");
        }
        //test = Create2D(path);
        //System.out.println(test);
        /*
        for (String[][] row : test) {
            System.out.println(Arrays.deepToString(row));
        }
        */
        /*
        test = ArrayCreate(path);
        for (String[][] row : test) {
            System.out.println(Arrays.deepToString(row));
        }
        /*
        for(String[][] sListList : test) {
        	for(String[] sList : sListList) {
        		for(String s : sList) {
        			System.out.printf(s);
        		}
        		System.out.println("");
        	}
        }
        */
        //System.out.println(test);
    //}
	//E:\pleiades\2023-09\workspace\RouteSearch\src\routesearch\data\datafile\timetable\Keiyo_Line_Down_Tokyo_-_Nishi-Funabashi_Suga_weekdays.csv
	//E:\pleiades\2023-09\workspace\RouteSearch\src\routesearch\data\datafile\timetable\Keiyo_Line_Down_Tokyo_-_Nishi-Funabashi_Suga_weekdays.csv
    public static String[][] Create2D(String path){   
    	BufferedReader buffReader = null;
        try {
        	// csvを読み込む
        	FileInputStream fileInput = new FileInputStream(path);
        	// バイトストリームをテキスト形式に変換
        	InputStreamReader inputStream = new InputStreamReader(fileInput); 
        	// テキスト形式のファイルを読み込む
        	buffReader = new BufferedReader(inputStream); 

        	//1行ずつの格納用変数
        	String currentContent;

        	
        	//列車番号が格納されている1行目を読み込む
        	currentContent = buffReader.readLine();
        	
        	ArrayList<String[]> dataArray = new ArrayList<>();
        	dataArray.add(currentContent.split(","));
        	// 1行目の列の数
        	int column = dataArray.get(0).length;
        	
        	
        	while((currentContent = buffReader.readLine()) != null) { 
        		//路線の日本語名を取得
        		//String lineName = currentContent.replaceAll("[^:]*:", "").replaceAll("[ |　]*\"", "").replace("東京メトロ　", "").replaceAll(" .*","").replaceAll("　.*","" );
        		
        		String[] receiveList = currentContent.split(",");
        		String[] dataList = new String[column];
        		
        		//空文字で初期化
        		for(int i=0; i<column ;i++) {
        			dataList[i] = "";
        		}
        		for(int i=0; i<receiveList.length ;i++) {
        			dataList[i] = receiveList[i];
        		}
        		/*
        		for(int i=0; i<data.length ;i++) {
        			if(data[i]==null) {
        				data[i] = "";
        			}
        		}
        		*/
        		dataArray.add(dataList);
        		
        		//System.out.println(column);
        	}
        	String[][] result = new String[dataArray.size()][column];
        	
        	//int i = 0;
        	for(int i=0 ; i<dataArray.size(); i++) {
        		for(int j=0; j<column ;j++) {
        			result[i][j] = dataArray.get(i)[j];
        		}
        	}
        	
        	//ArrayList<String[][]> resultList = new ArrayList<>();
        	//resultList.add(result);
        	
        	
        	return result;
        } catch(Exception ex) {
        	ex.printStackTrace();
        } finally {
        	try{
        		buffReader.close(); 
        	} catch(Exception ex) {
        		ex.printStackTrace();
        	}
        }
        return null;
    }
    
    // pathと行を指定すると指定csvファイルの指定行をArrayListに変換して返す関数。
    public static ArrayList<String> _changeJavaArray(Path path,int line){
        
        ArrayList<String> datas = new ArrayList<String>();
        
        try {
            // CSVファイルの読み込み*UTF-8以外なら変更してください。
            List<String> lines = Files.readAllLines(path, Charset.forName("UTF-8"));
            
            String[] data = lines.get(line).split(",");
            
            for (int j = 0; j < data.length ; j++) {
                datas.add(data[j]);
            }

        } catch (IOException e) {
            System.out.println("ファイル読み込みに失敗");
        }

        return (datas);
    }
    
    
    public static ArrayList<String[][]> ArrayCreate(Path path){
        ArrayList<String[][]> creation = new ArrayList<>();
        //File file = new File(pathsame);
        //FileReader fileReader = new FileReader(file);
        //BufferedReader bufferedReader = new BufferedReader(fileReader);
        //String cell;
        //ArrayList<String> datas = new ArrayList<String>();
        try {
            List<String> lines = Files.readAllLines(path, Charset.forName("UTF-8"));

            int i = 0;
            //while((cell = bufferedReader.readLine()) != null)
            for(String line : lines)
            {
                
                String[] data = lines.get(i).split(",");
                String[][] datas = new String[1][data.length];
                for (int j = 0; j < data.length ; j++) {
                    datas[0][j]=data[j];
                    //creation.add(datas);
                }
                String[][] copy = new String[1][data.length];
            System.arraycopy(datas[0], 0, copy[0], 0, data.length);
                creation.add(copy);
                i += 1;
            }
        } catch (IOException e) {
            System.out.println("ファイル読み込みに失敗");
        }

        return (creation);

    }

    /*
    public static ArrayList<String[][]> Create2D(Path path){
        ArrayList<String[][]> creation = new ArrayList<>();
        try{
        List<String> lines = Files.readAllLines(path, Charset.forName("UTF-8"));
        
        //行数をあらかじめ取得
        int size = Count(path);

        String[][] datas = new String[size][lines.get(0).split(",").length];
        int i = 0;
        for(String line : lines){
            String[] data = lines.get(i).split(",");
            
            
            for (int j = 0; j < data.length ; j++) {
                    datas[i][j]=data[j];
                }
            
            
            
            if(line.isEmpty()) break;

            i += 1;
        }
        
        creation.add(datas);
        
        } catch (IOException e) {
            System.out.println("ファイル読み込みに失敗");
        }
        /*
        //正しく読み込めているか確認
        if(creation.size()>0) {
	        for(String[] rowList : creation.get(0)) {
	        	for(String s : rowList) {
	        		System.out.printf(s+" ");
	        	}
	        	System.out.println("");
	        }
        }
        *//*
        return creation;
    }
*/
    public static int Count(Path path){
        int i = 0;
        try{
        List<String> lines = Files.readAllLines(path, Charset.forName("UTF-8"));
        
        for(String line : lines){
            if(line.isEmpty()) break;
            i += 1;
        }
        } catch (IOException e) {
            System.out.println("ファイル読み込みに失敗");
        }
        return i;
    }
    

}