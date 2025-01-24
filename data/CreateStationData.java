package routesearch.data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import routesearch.data.javafile.Station;
import routesearch.data.javafile.StationBranch;

/**
 * 注)以下のプログラムを扱うにあたって
 * 1.以下のプログラムを実験的に扱う場合は、JR.csv,営業キロを記述したファイル(distance1.csvなど)を同じフォルダ内に置くこと。
 * 
 * 2.routesearchフォルダ(Javaでいうとroutesearchパッケージ)も同一フォルダ内に置くこと。
 *   フォルダ構成)
 *     sample--- CreateStationData.java
 *            |
 *            -- CSVファイル(JR.csv(座標を記述したcsvファイル), yamanotesen.csv(営業キロ関連のcsvファイル), ...)
 *            |
 *            -- routesearchフォルダ
 * 
 * 3.実際のプログラムを実行する際のフォルダ構成は以下が望ましいと考えられる。
 *     routesearch---他班の作成したプログラム(Dist.java、Transfer.javaなど)
 *                |
 *                ---data ---Data.java(ここでCreateStationData.java)
 *                        |
 *                        ---CSVファイル
 *                        |
 *                        ---各データ構造を記述したファイル(Station.java、Line.javaなど)
 *                        |
 *                        ---CreateStationData.java
 *                        |
 *                        ---Lineを作るJavaファイル
 * 
 */

public class CreateStationData{

  /** 
  * (目的) ファイルに記されたすべての駅名から、それらに対応したStationオブジェクトを作成する
  * 引数: fileName ファイルの名前
  * 内部処理: 駅オブジェクトに座標の情報を格納
  * 返戻値: 駅名から生成されたStationオブジェクトの配列
  */
  public static HashMap<String,Station> CreateStationFromFile(String fileName){
    //格納用のHashMap (駅名 -> 駅オブジェクトの検索がかけられる)
    HashMap<String,Station> stationMap = new HashMap<String,Station>();
    BufferedReader buffReader = null;
    
    try {
      // sample1.csvファイルを読み込みます
      FileInputStream fileInput = new FileInputStream(fileName); 
      // バイトストリームをテキスト形式に変換
      InputStreamReader inputStream = new InputStreamReader(fileInput); 
      // テキスト形式のファイルを読み込む
      buffReader = new BufferedReader(inputStream); 

      //1行ずつの格納用変数
      String currentContent;
      //1行目はコラムなので、除外
      currentContent = buffReader.readLine();
      
      while((currentContent = buffReader.readLine()) != null) { 
        
        String[] arrayColumnData = currentContent.split(",");

        

        // 駅名の取得
        String station_name = arrayColumnData[1].replaceAll("[^ぁ-んァ-ヶー一-龯々]", "");
        
        
        // 座標
        float x = Float.parseFloat(arrayColumnData[2]);
        float y = Float.parseFloat(arrayColumnData[3]);

        // stationMapのキーに既に駅名があるかどうかの確認
        if(!stationMap.keySet().contains(station_name)){
          // 駅名が無い場合は、駅オブジェクト作成
          Station station = new Station(station_name);
          station.setX(x);
          station.setY(y);
          stationMap.put(station_name,station);
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

    // 全駅が格納されたHashMap
    return stationMap;
  }





  


  /**
   *  (目的) ファイルに記された隣り合った駅の接続、及び営業キロの格納
   *   
   * 引数:fileName ファイル名
   *      stationMap 全駅が格納されたHashMap
   * 内部処理:ファイルに記載された駅の隣接情報をstationMap内の駅に格納
   *         StationBranchオブジェクトの作成、及び営業キロの格納
   * 返戻値:ファイルに書かれた隣接情報を基に作ったStationBranchを格納したLinkedHashMap
   * 注意点:このメソッドは駅名の前にJK47のような英数字の羅列が付与された駅名のみを取り出し、それらの隣接をデータに格納する役割をする
   *        そのため、駅名の前に英数字が無い駅名を取り扱う場合は別のメソッドを用意する必要がある
   */
  public static LinkedHashMap<ArrayList<String>,StationBranch> ConnectStation(String fileName,HashMap<String,Station> stationMap){
    LinkedHashMap<ArrayList<String>,StationBranch> stationBranchMap = new LinkedHashMap<ArrayList<String>,StationBranch>();
    BufferedReader buffReader = null;

    // 以下、駅名は先頭に英字が2文字、数字が2文字並ぶ文字列を含むもののみpre_station_name、current_station_nameに格納
    Pattern p = Pattern.compile(".*[A-Z]{2}[0-9]{2}.*");
 

    try {
      // sample1.csvファイルを読み込みます
      FileInputStream fileInput = new FileInputStream(fileName); 
      // バイトストリームをテキスト形式に変換
      InputStreamReader inputStream = new InputStreamReader(fileInput); 
      // テキスト形式のファイルを読み込む
      buffReader = new BufferedReader(inputStream); 

      //1行ずつの格納用変数
      String content;
      String[] preColumnData = null;
      
      
      while((content = buffReader.readLine()) != null){
        preColumnData = content.split(",");
        Matcher m = p.matcher(preColumnData[preColumnData.length - 1]);
        // preColumnDataの最後の要素が先頭にアルファベットを持つならループを抜け出す
        // そうでない場合はwhile文で再検索(最終行にたどり着いた場合はnullが格納される)
        if(m.find()){
          break;
        }
      }
      //preColumData = currentContent.split(",");
      
      while((content = buffReader.readLine()) != null) { 
        String[] currentColumnData = null;

        

        while(content != null){
          currentColumnData = content.split(",");
          Matcher m = p.matcher(currentColumnData[currentColumnData.length - 1]);
          
          if(content.split(",")[0].equals("---")){
            // 再びpreColumnDataの方で駅名を検索
            while((content = buffReader.readLine()) != null){
              
              preColumnData = content.split(",");
              Matcher m2 = p.matcher(preColumnData[preColumnData.length - 1]);
              // preColumnDataの最後の要素が先頭にアルファベットを持つならループを抜け出す
              // そうでない場合はwhile文で再検索(最終行にたどり着いた場合はnullが格納される)
              if(m2.find()){
                break;
              }
            }
          }

          if(m.find()){
            break;
          }
          content = buffReader.readLine();
        }

        // contentがnullだった場合はループを抜け出す
        if(content == null){
          break;
        }

        

        String[] preLastContent = preColumnData[preColumnData.length - 1].split("[A-Z]{2}[0-9]{2}");
        String pre_station_name = preLastContent[preLastContent.length - 1].split("駅")[0].replaceAll("[0-9\\#\\[\\]\\(\\)\\*-]", "");
        
        String[] currentLastContent = currentColumnData[currentColumnData.length - 1].split("[A-Z]{2}[0-9]{2}");
        String current_station_name = currentLastContent[currentLastContent.length - 1].split("駅")[0].replaceAll("[0-9\\#\\[\\]\\(\\)\\*-]", "");
        
        

        // 駅オブジェクトの取得
        Station stationA = stationMap.get(pre_station_name);
        Station stationB = stationMap.get(current_station_name);

        String preDistStr = preColumnData[0].replaceAll("[^0-9.\\-]", "");
        String currentDistStr = currentColumnData[0].replaceAll("[^0-9.\\-]", "");

        if(preDistStr.equals("-") || currentDistStr.equals("-")){
          preDistStr = preColumnData[1].replaceAll("[^0-9.\\-]", "");
          currentDistStr = currentColumnData[1].replaceAll("[^0-9.\\-]", "");
        }

        float preDist = Float.parseFloat(preDistStr);
        float currentDist = Float.parseFloat(currentDistStr);
        
        // 営業キロの計算 (差をとり、小数第二位で四捨五入)
        float salesDistance = (float)Math.round(Math.abs(currentDist - preDist) * 10) / 10;

        // StationBranchで接続したい二つの駅名を格納する集合
        ArrayList<String> stationSet1 =  new ArrayList<String>();
        stationSet1.add(pre_station_name);
        stationSet1.add(current_station_name);

        ArrayList<String> stationSet2 =  new ArrayList<String>();
        stationSet2.add(current_station_name);
        stationSet2.add(pre_station_name);

        System.out.println(pre_station_name);
        System.out.println(current_station_name);

        
        // stationBranchオブジェクトの生成
        if(stationBranchMap.get(stationSet1) == null && stationBranchMap.get(stationSet2) == null){
          StationBranch stationBranch = new StationBranch(stationA,stationB,salesDistance);
          stationBranchMap.put(stationSet1, stationBranch);
          stationA.getBranch().add(stationBranch);
          stationB.getBranch().add(stationBranch);
        }

        // stationA,stationBが逆であった時の処理(プログラムとしては省略)
        /*if(stationBranchMap.get(stationSet2) == null){
          StationBranch stationBranch = new StationBranch(stationB,stationA,salesDistance);
          stationBranchMap.put(stationSet2, stationBranch);
          stationA.getBranch().add(stationBranch);
          stationB.getBranch().add(stationBranch);
        }*/

        //隣接した駅の格納
        if(!(stationA.getNextSt().contains(stationB))){
          stationA.getNextSt().add(stationB);
        }
        if(!(stationB.getNextSt().contains(stationA))){
          stationB.getNextSt().add(stationA);
        }

        // 現在の駅データをpreColumnDataに保存
        preColumnData = currentColumnData;

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

    return stationBranchMap;
  }
}