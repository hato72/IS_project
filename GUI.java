package routesearch;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.VLineTo;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import routesearch.data.Data;
import routesearch.data.javafile.Station;

public class GUI extends Application {

	boolean fullScreen = false;
	private List<Node> originalChildren;
	boolean isFrom = true;//isFrom=trueで出発時刻検索, ifFrom=falseで到着時間検索

	boolean dep = true, arr = false;

	@Override
	public void start(Stage primaryStage) {

		double goldenRatio = (1 + Math.sqrt(5)) / 2;

		double fonS = 10 * goldenRatio; // font size
		double preH = 20 * goldenRatio; // prefheight

		// ラベルを作成する
		Label label = new Label("出発");
		label.setFont(new Font(fonS));
		label.setPrefHeight(preH);
		label.setPrefWidth(preH * goldenRatio);
		label.setAlignment(Pos.CENTER);
		label.getStyleClass().add("dep_arr_label");
		// テキストフィールドを作成する
		TextField startText = new TextField("柏");
		startText.getStyleClass().add("text-field2");
		// テキストフィールドを左詰めにする
		startText.setAlignment(Pos.CENTER_LEFT);
		// テキストフィールドのフォントサイズを18に、優先高を40にする
		startText.setFont(new Font(fonS));
		startText.setPrefHeight(preH);
		HBox hbox1 = new HBox(0, label, startText); // レイアウト1行目

		Label label2 = new Label("到着");
		label2.setFont(new Font(fonS));
		label2.setPrefHeight(preH);
		label2.setPrefWidth(preH * goldenRatio);
		label2.setAlignment(Pos.CENTER);
		label2.getStyleClass().add("dep_arr_label");
		// テキストフィールドを作成する
		TextField goalText = new TextField("奥多摩");
		// テキストフィールドを左詰めにする
		goalText.setAlignment(Pos.CENTER_LEFT);
		goalText.getStyleClass().add("text-field2");
		// テキストフィールドのフォントサイズを18に、優先高を40にする
		goalText.setFont(new Font(fonS));
		goalText.setPrefHeight(preH);
		HBox hbox2 = new HBox(0, label2, goalText); // レイアウト2行目

		// 共有する空白部分を作成
		Region blankSpace = new Region();
		blankSpace.setPrefWidth(130); // 必要な幅に設定します。これが空白になります

		Button swapBtn = new Button();
		//swapBtn.setFont(new Font(fonS));
		swapBtn.setMinSize(20 * goldenRatio, 20 * goldenRatio);
		swapBtn.getStyleClass().add("button");
		ImageView swapIcon = new ImageView(getClass().getResource("imgs/swap.png").toString());
		swapIcon.setFitHeight(15 * goldenRatio);
		swapIcon.setFitWidth(15 * goldenRatio);
		swapBtn.setGraphic(swapIcon);
		swapBtn.setAlignment(Pos.CENTER);
		this.buttonPressedAnimation(swapBtn);

		swapBtn.setOnAction(event -> {
			swapText(startText, goalText);
		});

		HBox swapBox = new HBox(blankSpace, swapBtn);

		Label label3 = new Label("出発時刻");
		label3.setFont(new Font(fonS));
		label3.setPrefHeight(preH);

		Button depBtn = new Button("出発");
		depBtn.getStyleClass().add("dep-button");
		depBtn.setMinSize(20 * goldenRatio, 20 * goldenRatio);
		this.buttonPressedAnimation(depBtn);

		Button arrBtn = new Button("到着");
		arrBtn.getStyleClass().add("arr-button");
		arrBtn.setMinSize(20 * goldenRatio, 20 * goldenRatio);
		this.buttonPressedAnimation(arrBtn);

		HBox depArrBtns = new HBox(depBtn, arrBtn);

		this.fadeNode(arrBtn, true);
		// 全画面表示ボタンのイベントハンドラ
		depBtn.setOnAction(event -> {
			if (!dep) {
				this.fadeNode(depBtn, dep);
				this.fadeNode(arrBtn, true);
			}

			isFrom = true;
			dep = true;
			arr = false;
			System.out.println("isFrom: " + isFrom);

		});

		// 全画面表示ボタンのイベントハンドラ
		arrBtn.setOnAction(event -> {
			if (!arr) {
				this.fadeNode(arrBtn, false);
				this.fadeNode(depBtn, true);
			}

			isFrom = false;
			arr = true;
			dep = false;
			System.out.println("isFrom: " + isFrom);

		});

		// 現在時刻の取得
		LocalDateTime now = LocalDateTime.now();

		TextField yearText = new TextField(String.valueOf(now.getYear())); // 現在の年を設定
		yearText.setAlignment(Pos.CENTER_RIGHT);
		yearText.setFont(new Font(fonS));
		yearText.setPrefHeight(preH);
		yearText.setMaxWidth(fonS * 4);
		Label yearLabel = new Label("年");
		yearLabel.setFont(new Font(fonS));
		yearLabel.setPrefHeight(preH);

		TextField monthText = new TextField(String.valueOf(now.getMonthValue())); // 現在の月を設定
		monthText.setAlignment(Pos.CENTER_RIGHT);
		monthText.setFont(new Font(fonS));
		monthText.setPrefHeight(preH);
		monthText.setMaxWidth(fonS * 3);
		Label monthLabel = new Label("月");
		monthLabel.setFont(new Font(fonS));
		monthLabel.setPrefHeight(preH);

		TextField dayText = new TextField(String.valueOf(now.getDayOfMonth())); // 現在の日を設定
		dayText.setAlignment(Pos.CENTER_RIGHT);
		dayText.setFont(new Font(fonS));
		dayText.setPrefHeight(preH);
		dayText.setMaxWidth(fonS * 3);
		Label dayLabel = new Label("日");
		dayLabel.setFont(new Font(fonS));
		dayLabel.setPrefHeight(preH);

		goalText.setFont(new Font(fonS));
		goalText.setPrefHeight(preH);
		HBox hbox3 = new HBox(5 * goldenRatio, depArrBtns, yearText, yearLabel, monthText, monthLabel, dayText,
				dayLabel);

		TextField hourText = new TextField(String.valueOf(now.getHour())); // 現在の時間を設定
		hourText.setAlignment(Pos.CENTER_RIGHT);
		hourText.setFont(new Font(fonS));
		hourText.setPrefHeight(preH);
		hourText.setMaxWidth(fonS * 3);
		Label hourLabel = new Label("時");
		hourLabel.setFont(new Font(fonS));
		hourLabel.setPrefHeight(preH);

		TextField minuteText = new TextField(String.valueOf(now.getMinute())); // 現在の分を設定
		minuteText.setAlignment(Pos.CENTER_RIGHT);
		minuteText.setFont(new Font(fonS));
		minuteText.setPrefHeight(preH);
		minuteText.setMaxWidth(fonS * 3);
		Label minuteLabel = new Label("分");
		minuteLabel.setFont(new Font(fonS));
		minuteLabel.setPrefHeight(preH);

		// 各テキストフィールドにイベントフィルターを設定して、非数値の入力を無視します。
		yearText.addEventFilter(KeyEvent.KEY_TYPED, numericValidationHandler());
		monthText.addEventFilter(KeyEvent.KEY_TYPED, numericValidationHandler());
		dayText.addEventFilter(KeyEvent.KEY_TYPED, numericValidationHandler());
		hourText.addEventFilter(KeyEvent.KEY_TYPED, numericValidationHandler());
		minuteText.addEventFilter(KeyEvent.KEY_TYPED, numericValidationHandler());

		HBox hbox4 = new HBox(5 * goldenRatio, hourText, hourLabel, minuteText, minuteLabel);

		Button searchBtn = new Button();
		searchBtn.setFont(new Font(fonS));
		searchBtn.setMinSize(20 * goldenRatio, 20 * goldenRatio);
		searchBtn.getStyleClass().add("button");
		ImageView searchIcon = new ImageView(getClass().getResource("imgs/search.png").toString());
		searchIcon.setFitHeight(15 * goldenRatio);
		searchIcon.setFitWidth(15 * goldenRatio);
		searchBtn.setGraphic(searchIcon);
		searchBtn.setAlignment(Pos.CENTER);
		this.buttonPressedAnimation(searchBtn);

		// 全画面表示ボタンを追加
		Button fullScreenBtn = new Button();
		fullScreenBtn.getStyleClass().add("button");
		fullScreenBtn.setMinSize(20 * goldenRatio, 20 * goldenRatio);
		this.buttonPressedAnimation(fullScreenBtn);

		this.hideNode(fullScreenBtn);

		ImageView upIcon = new ImageView(getClass().getResource("imgs/up_icon.png").toString());
		upIcon.setFitHeight(12 * goldenRatio);
		upIcon.setFitWidth(12 * goldenRatio);

		ImageView downIcon = new ImageView(getClass().getResource("imgs/down_icon.png").toString());
		downIcon.setFitHeight(12 * goldenRatio);
		downIcon.setFitWidth(12 * goldenRatio);

		fullScreenBtn.setGraphic(upIcon);
		fullScreenBtn.setAlignment(Pos.CENTER);

		HBox hbox5 = new HBox(310, searchBtn, fullScreenBtn); //レイアウト5行目

		HBox hbox6 = new HBox();

		hbox6.setPrefWidth(384);

		hbox6.getStyleClass().add("cyan_back");

		// スクロールバー追加 
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(hbox6);
		scrollPane.getStyleClass().add("cyan_back");
		hideNode(scrollPane);
		scrollPane.setPrefHeight(450);

		//routesearchファイルの絶対パスを指定する(末尾がroutesearch\\)
		//String path = "E:\\pleiades\\2023-09\\workspace\\RouteSearch\\src\\routesearch\\";
		String path = "C:\\pleiades\\2023-12\\workspace\\project_fx\\src\\routesearch\\";
		//String path = "C:\\pleiades\\2022-09\\workspace\\routesearch\\src\\routesearch\\";
		//String path = new File("routesearch\\").getAbsolutePath()+"\\";//これで取得できそうならこれ使ってください
		Data data = new Data(path);
		//Data data = new Data();
		System.out.println("data load");

		boolean isSearch = false;

		searchBtn.setOnAction(event -> {
			GUI gui = new GUI();
			int year = Integer.parseInt(yearText.getText());
			int month = Integer.parseInt(monthText.getText()) - 1;//Calendarクラスは月またぎの問題で、0から1月となっているので-1をする.
			int day = Integer.parseInt(dayText.getText());
			int hour = Integer.parseInt(hourText.getText());
			int minute = Integer.parseInt(minuteText.getText());

			String yearStr = yearText.getText();
			String monthStr = monthText.getText();
			String dayStr = dayText.getText();
			String hourStr = hourText.getText();
			String minuteStr = minuteText.getText();

			if (!isValidDate(yearStr, monthStr, dayStr, hourStr, minuteStr)) {
				showError(scrollPane, hbox6, "日付または時刻が不正です。");
				return;
			}

			Calendar date = Calendar.getInstance(); //CalendarクラスはCalendar date = new Calendar();のようには宣言せずこのように宣言するので注意.
			date.set(year, month, day, hour, minute, 0);
			System.out.println(date.getTime());
			gui.input(event, startText, goalText, hbox6, data, date, scrollPane, primaryStage, isSearch, isFrom);
			showNode(fullScreenBtn);
		});

		// blankSpaceを新たに生成し、再使用
		blankSpace = new Region();
		blankSpace.setPrefHeight(5 * goldenRatio);
		Group space = new Group();
		space.getChildren().add(blankSpace);

		VBox vbox = new VBox(5 * goldenRatio, space, hbox1, swapBox, hbox2, hbox3, hbox4, hbox5);
		vbox.setPrefWidth(400 * goldenRatio);
		//vbox.setPrefHeight(600 * goldenRatio);

		vbox.getChildren().addAll(hbox6, scrollPane);

		// 全画面表示ボタンのイベントハンドラ
		fullScreenBtn.setOnAction(event -> {
			originalChildren = new ArrayList<>(vbox.getChildren());
			ScrollPane tmpPane = new ScrollPane();
			tmpPane.setContent(hbox6);
			tmpPane.getStyleClass().add("cyan_back");

			if (!fullScreen) {
				//fullScreenBtn.setText("縮小");
				fullScreenBtn.setGraphic(downIcon);
				this.hideNode(hbox1);
				this.hideNode(hbox2);
				this.hideNode(hbox3);
				this.hideNode(hbox4);
				this.hideNode(swapBox);

				vbox.getChildren().remove(space);
				vbox.getChildren().remove(hbox1);
				vbox.getChildren().remove(swapBox);
				vbox.getChildren().remove(hbox2);
				vbox.getChildren().remove(hbox3);
				vbox.getChildren().remove(hbox4);

				this.hideNode(hbox5.getChildren().get(0));

				//this.swapNodesInVBox(vbox, 0, 6);

				vbox.getChildren().remove(1);

				vbox.getChildren().add(tmpPane);
				this.moveNodeVerticallyFromTo(vbox, 575, 350, 500);
				//this.swapNodesInVBox(vbox, 1, 7);

				fullScreen = true;
			} else {

				this.moveNodeVerticallyFromTo(vbox, 100, 350, 500);

				vbox.getChildren().clear();
				vbox.getChildren().add(space);
				vbox.getChildren().add(hbox1);
				vbox.getChildren().add(swapBox);
				vbox.getChildren().add(hbox2);
				vbox.getChildren().add(hbox3);
				vbox.getChildren().add(hbox4);
				vbox.getChildren().add(hbox5);
				vbox.getChildren().add(tmpPane);

				//this.swapNodesInVBox(vbox, 1, 7);
				//this.swapNodesInVBox(vbox, 0, 6);

				this.showNode(hbox1);
				this.showNode(hbox2);
				this.showNode(hbox3);
				this.showNode(hbox4);
				this.showNode(swapBox);
				this.showNode(hbox5.getChildren().get(0));
				fullScreen = false;
				//fullScreenBtn.setText("拡大");
				fullScreenBtn.setGraphic(upIcon);
			}

		});

		// VBoxペインのパディングを6にする
		//vbox.setPadding(new Insets(6));
		// シーンを作成し、ペインに入れる

		Scene scene = new Scene(vbox, 400, 700);

		scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

		// ステージにVBoxペインを入れる
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		// ステージのタイトルバーを設定する
		primaryStage.setTitle("乗換案内");
		// ステージを表示する
		primaryStage.show();

		this.nodesAnimation(vbox.getChildren(), "launch");

	}

	// ノードのフェードメソッド
	public void fadeNode(Node node, boolean fadeOut) {
		FadeTransition ft = new FadeTransition(Duration.seconds(0.25), node); // アニメーションの持続時間を0.5秒に設定
		if (fadeOut) {
			// フェードアウト（透明度を1から0.5に）
			ft.setFromValue(1.0);
			ft.setToValue(0.5);
		} else {
			// フェードイン（透明度を0.5から1に）
			ft.setFromValue(0.5);
			ft.setToValue(1.0);
		}
		ft.play(); // アニメーションを開始
	}

	public void moveNodeVerticallyFromTo(Node node, double startY, double endY, long durationMillis) {
		Path path = new Path();
		path.getElements().add(new MoveTo(200, startY)); // 移動を開始するY座標を設定
		path.getElements().add(new VLineTo(endY)); // 移動を終了するY座標を設定

		PathTransition transition = new PathTransition(Duration.millis(durationMillis), path, node);
		transition.setCycleCount(1);
		transition.setAutoReverse(false);
		transition.play();
	}

	public void swapNodesInVBox(VBox vbox, int index1, int index2) {
		// VBox内のノードリストを取得
		ObservableList<Node> children = vbox.getChildren();

		// インデックスが範囲内か検証
		if (index1 >= 0 && index1 < children.size() && index2 >= 0 && index2 < children.size()) {
			// 指定された二つのノードを取得
			Node node1 = children.get(index1);
			Node node2 = children.get(index2);

			// ノードをリストから一時的に削除
			children.remove(index1);
			children.remove(index2 > index1 ? index2 - 1 : index2);

			// ノードを交換してリストに再追加
			if (index1 < index2) {
				children.add(index1, node2);
				children.add(index2, node1);
			} else {
				children.add(index2, node1);
				children.add(index1, node2);
			}
		} else {
			// インデックスが範囲外の場合はエラーメッセージを出力
			System.out.println("指定されたインデックスが範囲外です。");
		}
	}

	// 日付の有効性をチェックするメソッド（うるう年を考慮）
	private boolean isValidDate(String year, String month, String day, String hour, String minute) {
		try {
			int yearInt = Integer.parseInt(year);
			int monthInt = Integer.parseInt(month);
			int dayInt = Integer.parseInt(day);
			int hourInt = Integer.parseInt(hour);
			int minuteInt = Integer.parseInt(minute);

			if (monthInt < 1 || monthInt > 12) {
				// 月が範囲外
				return false;
			}
			if (hourInt < 0 || hourInt > 23 || minuteInt < 0 || minuteInt > 59) {
				// 時または分が範囲外
				return false;
			}

			// うるう年チェック
			if (monthInt == 2) {
				// うるう年で、2月の場合
				if (isLeapYear(yearInt)) {
					if (dayInt < 1 || dayInt > 29) {
						return false; // うるう年の2月は29日まで有効
					}
				} else {
					if (dayInt < 1 || dayInt > 28) {
						return false; // 非うるう年の2月は28日まで有効
					}
				}
			} else if (monthInt == 4 || monthInt == 6 || monthInt == 9 || monthInt == 11) {
				if (dayInt < 1 || dayInt > 30) {
					return false; // 4, 6, 9, 11月は30日まで有効
				}
			} else {
				if (dayInt < 1 || dayInt > 31) {
					return false; // それ以外の月は31日まで有効
				}
			}

			return true;
		} catch (NumberFormatException e) {
			// 解析できない場合は不正な日付とみなす
			return false;
		}
	}

	// うるう年を判断するメソッド
	private boolean isLeapYear(int year) {
		return (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
	}

	// 非数値入力を検証し、無視するイベントフィルター
	private EventHandler<KeyEvent> numericValidationHandler() {
		return event -> {
			if (!event.getCharacter().matches("\\d")) { // 数字だけにマッチする正規表現
				event.consume(); // このイベントを消費して、さらに上には伝播させない
			}
		};
	}

	// 検索ボタンの処理を行う
	public void input(ActionEvent event, TextField startText, TextField goalText, HBox hboxResult, Data data,
			Calendar date, ScrollPane scrollPane, Stage stage, boolean isSearch, boolean isFrom) {

		showNode(scrollPane);

		// 出発・目的地の内容を取得する
		String s = startText.getText();
		String g = goalText.getText();

		Station start = parseStation(s, data);
		Station goal = parseStation(g, data);

		if (start == null || goal == null) {
			showError(scrollPane, hboxResult, "登録されている駅を入力してください.");
		} else if (start.equals(goal)) {
			showError(scrollPane, hboxResult, "出発駅と到着駅は別の駅を入力してください.");
		} else { //startもgoalも登録済みの場合
					//ArrayList<ArrayList<TransferResult>> res = new ArrayList<>(); //TransferResultの2次元配列

			int searchNum = 1;
			Transfer t = new Transfer();
			TransferResult[] res = new TransferResult[searchNum]; //現時点では配列の長さは1としているが、2,3番目に早い経路なども検索できるように拡張予定
			res = t.minTimeResult(start, goal, date, searchNum, isFrom, data); //minTimeResult最後の引数は出発時間の指定ならtrue,到着時間の指定ならfalse(現時点ではfalseにしても出発時間で指定されたものとして処理)
			if (res[0] == null) {
				Label result = new Label("経路が見つかりませんでした.");
				result.setFont(new Font(18));
				result.setPrefHeight(40);
				hboxResult.getChildren().clear();
				hboxResult.getChildren().add(result);
				return;
			}

			VBox vbox = this.createResultVBox(res[0].clone(), data);

			ObservableList<Node> nodes = vbox.getChildren();

			hboxResult.getChildren().clear();

			hboxResult.getChildren().add(vbox);

		}

		isSearch = true;
		System.out.println("isSearch: " + isSearch);

		return;

	}

	double vbox_height = 0;
	private int fare = 0;
	private VBox fareBox = new VBox();
	VBox vbox;
	ArrayList<VBox> vboxes = new ArrayList<>();
	int vboxes_indx = 0;

	//日付をまたいだか否か
	boolean is2day = false;

	Map<VBox, String> fareMap = new HashMap<>();

	private VBox createResultVBox(TransferResult result, Data data) {

		String sYear, sMonth, sDay, sWeek, sTime;
		String gYear, gMonth, gDay, gWeek, gTime;

		sYear = String.valueOf(result.getSYear());
		sMonth = String.valueOf(result.getSMonth());
		sDay = String.valueOf(result.getSDay());
		sWeek = result.getSWeek();
		sTime = result.getSTime();

		Label resultDate = new Label();
		resultDate.setFont(new Font(17));
		resultDate.setPrefHeight(60);

		vbox = new VBox(0, resultDate);

		TransferResult pre = null;
		VBox childVbox = new VBox(0);
		VBox fareBox = new VBox();

		int transCount = 0;

		while (result != null) {

			if (pre != null) {
				if (pre.getFare() != -1) {
					childVbox = new VBox(0);
				}

				Label resultLabel4 = new Label();
				resultLabel4.setFont(new Font(18));
				resultLabel4.setPrefHeight(40);
				if (result.isConnect()) {

					resultLabel4.setText("直通");

				} else {
					resultLabel4.setText("乗換 " + result.getTransferMin() + "分");
					transCount++;
					childVbox.getChildren().add(resultLabel4);
				}

			}

			//下を任意の回数乗換がある場合でも最後まで出力できるようにしてください。

			Label resultLabel1 = new Label();

			resultLabel1.setFont(new Font(18));
			resultLabel1.setPrefHeight(40);
			resultLabel1.setText(result.getSTime() + " " + result.getSName() + "駅");

			if (!result.isConnect()) {
				childVbox.getChildren().add(resultLabel1);
			}
			

			// 駅数を調べる
			ArrayList<Station> stationList = result.getRealStationList();
			//int stationCount = getTransitStationCount(stationList);
			int stationCount = stationList.size();
			System.out.println("到着駅までの駅数: " + stationCount);

			String[] lineTermName = result.getLineName().split(" ");
			String lineName = lineTermName[0];
			String termName = lineTermName[1];
			System.out.println("###############");
			System.out.println(lineName);

			LineMap lm = new LineMap();
			String lineNameClass = lm.getMapValue(lineName);

			System.out.println(lineNameClass);

			//resultLabel2.getStyleClass().add(lineName);

			// 共有する空白部分を作成
			Region blankSpace = new Region();
			blankSpace.setPrefWidth(110); // 必要な幅に設定します。これが空白になります

			Rectangle lineRectangle = new Rectangle();

			VBox information = new VBox();
			;

			VBox staCountBox = createStaCountBox(stationList, data, lineRectangle, information, fareBox, childVbox,
					is2day);

			lineRectangle.setWidth(5);
			lineRectangle.setHeight(staCountBox.getPrefHeight());

			lineRectangle.getStyleClass().add(lineNameClass);

			//lineName = lm.getMapKey(lineName);
			System.out.println(lineName);

			Label lineNameLabel = new Label(lineName);
			lineNameLabel.setFont(new Font(16));
			lineNameLabel.setMinWidth(200);

			Label termNameLabel = new Label(termName);
			//termNameLabel.setFont(new Font(14));
			//termNameLabel.setMinHeight(20);

			if(result.isConnect()) {
				information.getChildren().add(new Label("※直通 (乗換不要)"));
			}
			information.getChildren().add(lineNameLabel);
			information.getChildren().add(termNameLabel);
			information.setAlignment(Pos.CENTER_LEFT);

			HBox hb = new HBox(10, staCountBox, lineRectangle, information, blankSpace);

			childVbox.getChildren().add(hb);
			hb.setAlignment(Pos.CENTER_LEFT);

			Label resultLabel3 = new Label();
			resultLabel3.setFont(new Font(18));
			resultLabel3.setPrefHeight(40);
			resultLabel3.setText(result.getGTime() + " " + result.getGName() + "駅");
			childVbox.getChildren().add(resultLabel3);

			int f = result.getFare();

			if (f != -1) {
				System.out.println(f + "円です");
				fare += f;

				vbox.getChildren().add(childVbox);
				vboxes.add(childVbox);
				System.out.println("リストに追加");

				vboxes_indx++;

				HBox hbox = new HBox(childVbox, fareBox);
				vbox.getChildren().add(hbox);

				addFareBox(f, fareBox, childVbox, is2day);

				is2day = true;
				fareMap.put(childVbox, String.valueOf(f));
				fareBox = new VBox();

				//break;
			}

			pre = result.clone();
			if (result.getNext() != null) {
				result = result.getNext().clone();
			} else {
				break;
			}
		}

		System.out.println("map: " + fareMap.size());
		for (VBox v : fareMap.keySet()) {
			System.out.println(v + ": " + fareMap.get(v));
		}
		result = pre;

		nodesAnimation(vbox.getChildren(), "");

		//nodesAnimation(fareBox.getChildren(), "");

		//最終的に返すVBox
		VBox finalVbox = new VBox(vbox);

		gYear = String.valueOf(result.getGYear());
		gMonth = String.valueOf(result.getGMonth());
		gDay = String.valueOf(result.getGDay());
		gWeek = result.getGWeek();
		gTime = result.getGTime();

		int durationMin = (int) this.calculateDurationInMinutes(sTime, gTime);

		if (durationMin < 0) {
			durationMin += 1440;
		}
		int durH = durationMin / 60;
		int durMi = durationMin % 60;

		String duration;
		String date = sYear + "年" + sMonth + "月" + sDay + "日";
		if (durH == 0) {
			duration = durMi + "分";
		} else {
			duration = durH + "時間" + durMi + "分";
		}

		resultDate.setText(sTime + "→" + gTime + " (" + duration + ")   " + date + "\n運賃: " + fare + "円    乗換: "
				+ transCount + "回");

		return finalVbox;

	}

	public void stopTime(int t) {
		try {
			Thread.sleep(t); // 0.1秒(1万ミリ秒)間だけ処理を止める
			return;
		} catch (InterruptedException e) {
		}
	}

	public void addFareBox(int fare, VBox fareBox, VBox childVbox, boolean is2day) {
		// hboxResultがレイアウトを更新するよう要求します。
		childVbox.requestLayout();

		// レイアウトサイクル後に縦幅を取得します。
		Platform.runLater(() -> {

			double height = childVbox.getHeight() - 60;

			System.out.println("vbox height: " + height);
			System.out.println("fare: " + fare);

			VBox tmpVBox;
			if (!is2day) {
				tmpVBox = this.createFareBox((int) ((height - 50) / 2), fare, 30);
			} else {
				tmpVBox = this.createFareBox((int) ((height - 50) / 2), fare, 50);
			}

			if (fare != -1) {
				// 修正された部分: 新しいリストに子ノードを一時的にコピーしてから追加する
				List<Node> childNodesToAdd = new ArrayList<>(tmpVBox.getChildren());
				fareBox.getChildren().addAll(childNodesToAdd); // addAllを使って全てのノードを一度に追加
			}
		});

	}

	private long calculateDurationInMinutes(String departureTimeStr, String arrivalTimeStr) {
		LocalTime departureTime = LocalTime.parse(departureTimeStr);
		LocalTime arrivalTime = LocalTime.parse(arrivalTimeStr);
		return ChronoUnit.MINUTES.between(departureTime, arrivalTime);
	}

	// 駅数表示ボックスの作成
	public VBox createFareBox(int n, int fare, int size) {
		//int fare = 120;

		// 共有する空白部分を作成
		Region blankSpace = new Region();
		blankSpace.setPrefHeight(size);

		Label fareLabel = new Label(fare + "円");
		fareLabel.setMinWidth(50);

		fareLabel.setAlignment(Pos.CENTER);
		fareLabel.getStyleClass().add("fare_label");

		Group upArrow = this.drawArrow(n, 1);

		Group downArrow = this.drawArrow(n, -1);

		VBox tmpBox = new VBox(10, blankSpace, upArrow, fareLabel, downArrow);
		tmpBox.setAlignment(Pos.CENTER);
		tmpBox.setPrefWidth(40);

		Group tmpGroup = new Group();
		tmpGroup.getChildren().add(tmpBox);
		VBox fareBox = new VBox(tmpGroup);
		fareBox.setAlignment(Pos.CENTER_RIGHT);

		return fareBox;
	}

	public Group drawArrow(int length, int direction) {
		// 矢印の線の定義
		Line arrowLine = new Line(0, 0, 0, length);

		// 矢印の先端の定義
		Line arrowHead1 = new Line(0, 0, 5, (length - 10) * (-1));
		Line arrowHead2 = new Line(0, 0, -5, (length - 10) * (-1));

		// 矢印の方向を設定
		switch (direction) {
		case 1: // 上
			// 回転なし
			arrowLine.setEndY(-length);
			arrowHead1.setStartY(-length);
			arrowHead2.setStartY(-length);
			break;
		case -1: // 下
			arrowLine.setEndY(length);
			arrowHead1.setStartY(length);
			arrowHead1.setEndY(length - 5);
			arrowHead2.setStartY(length);
			arrowHead2.setEndY(length - 5);
			break;
		default:
			throw new IllegalArgumentException("方向は1か-1でなければなりません（1: 上, -1: 下）");
		}

		// 矢印を含むグループを作成
		Group arrow = new Group();
		arrow.getChildren().addAll(arrowLine, arrowHead1, arrowHead2);

		return arrow;
	}

	// 駅数表示ボックスの作成
	public VBox createStaCountBox(ArrayList<Station> stationList, Data data, Rectangle lineRectangle,
			VBox information, VBox fareBox, VBox childVbox, boolean is2day) {
		int stationCount = getTransitStationCount(stationList);

		Label countLabel = new Label(stationCount + "駅");
		countLabel.getStyleClass().add("count_label");
		Button countButton = new Button(stationCount + "駅");
		countButton.getStyleClass().add("count_label");
		countButton.setMinWidth(50);
		countButton.setAlignment(Pos.CENTER);
		this.buttonPressedAnimation(countButton);

		Group upArrow = this.drawArrow(15, 1);

		Group downArrow = this.drawArrow(15, -1);

		VBox staCountBox = new VBox(5, upArrow, countButton, downArrow);
		staCountBox.setAlignment(Pos.CENTER);
		staCountBox.setPrefWidth(50);
		staCountBox.setPrefHeight(50);

		countButton.setOnAction(e -> {
			// 駅数ボタンが押されたときの処理
			showStationList(stationList, staCountBox, lineRectangle, data, information, fareBox, childVbox, is2day);
			nodesAnimation(staCountBox.getChildren(), "駅数");
			//nodesAnimation(information.getChildren(), "駅数");
		});

		return staCountBox;
	}

	private List<Node> originalChildren1, originalChildren2, originalChildren3;

	public void showStationList(ArrayList<Station> stationList, VBox staCountBox, Rectangle lineRectangle, Data data,
			VBox information, VBox fareBox, VBox childVbox, boolean is2day) {

		originalChildren1 = new ArrayList<>(staCountBox.getChildren());
		originalChildren2 = new ArrayList<>(information.getChildren());
		originalChildren3 = new ArrayList<>(fareBox.getChildren());

		System.out.println("farebox::: " + fareBox.getChildren().size());
		System.out.println("vboxes::: " + vboxes.size());

		staCountBox.getChildren().clear();
		VBox add_info = new VBox();
		//information.getChildren().clear();

		Button countButton = new Button("戻す");
		countButton.getStyleClass().add("count_label");
		countButton.setMinWidth(50);
		countButton.setAlignment(Pos.CENTER);
		this.buttonPressedAnimation(countButton);

		countButton.setOnAction(e -> {
			// ここにボタンが押されたときの処理を記述
			System.out.println("戻すボタンが押されました。");
			staCountBox.getChildren().clear();
			information.getChildren().clear();
			fareBox.getChildren().clear();
			staCountBox.getChildren().addAll(originalChildren1);

			information.getChildren().addAll(originalChildren2);

			fareBox.getChildren().addAll(originalChildren3);

			staCountBox.setPrefHeight(50);
			information.setPrefHeight(50);
			lineRectangle.setHeight(50);
			this.nodesAnimation(staCountBox.getChildren(), "駅数");
			//this.nodesAnimation(fareBox.getChildren(), "駅数");

		});

		staCountBox.getChildren().add(countButton);

		if (stationList.size() == 2) {
			add_info.getChildren().add(new Label("↓"));
		} else {
			for (int i = 1; i < stationList.size() - 1; i++) {

				System.out.println(stationList.get(i).getName());

				Label staName = new Label(stationList.get(i).getName());
				if (i == stationList.size() - 2) {
					add_info.getChildren().add(new Label("↓"));
					add_info.getChildren().add(staName);
					add_info.getChildren().add(new Label("↓"));

				} else {
					add_info.getChildren().add(new Label("↓"));
					add_info.getChildren().add(staName);

				}

			}
		}

		int size = add_info.getChildren().size() + 2;
		System.out.println(size);
		information.getChildren().add(add_info);
		information.setPrefHeight(20 * (size));

		if (add_info.getChildren().size() != 1) {
			lineRectangle.setHeight(information.getPrefHeight());

		} else {
			lineRectangle.setHeight(100);
		}

		nodesAnimation(add_info.getChildren(), "駅数");

		childVbox.requestLayout();

		stopTime(10);
		fareBox.getChildren().clear();
		// レイアウトサイクル後に縦幅を取得します。
		Platform.runLater(() -> {
			int fare2 = Integer.parseInt(fareMap.get(childVbox));
			System.out.println("ここまで" + fare2 + "円");
			double height = childVbox.getHeight();
			System.out.println("childVbox height: " + height);
			System.out.println("VBox height: " + vbox.getHeight());
			VBox tmpVBox;
			if (!is2day) {
				tmpVBox = this.createFareBox((int) ((height - 130) / 2), fare2, 30);
			} else {
				tmpVBox = this.createFareBox((int) ((height - 130) / 2), fare2, 50);
			}

			// 修正された部分: 新しいリストに子ノードを一時的にコピーしてから追加する
			List<Node> childNodesToAdd = new ArrayList<>(tmpVBox.getChildren());
			fareBox.getChildren().addAll(childNodesToAdd); // addAllを使って全てのノードを一度に追加
		});

	}

	private void showError(ScrollPane scrollPane, HBox hboxResult, String message) {
		showNode(scrollPane);
		Label result = new Label(message);
		result.setFont(new Font(18));
		result.setPrefHeight(40);
		// エラーメッセージの表示用処理

		hboxResult.getChildren().clear();
		hboxResult.getChildren().add(result);
	}

	//途中駅数を返す
	public int getTransitStationCount(ArrayList<Station> stationList) {
		// stopStationListがnullの場合は0を返す
		if (stationList == null) {
			System.out.println("駅一覧: null");
			return 0;
		} else if (stationList.isEmpty()) {
			System.out.println("駅一覧: No stations");
			return 0;
		} else {
			//駅名の一覧を表示するように変更
			StringBuilder sb = new StringBuilder("駅一覧: ");
			for (Station station : stationList) {
				sb.append(station.getName()).append(", "); //StationのgetNameメソッドを使用
			}
			System.out.println(sb.toString());
			// 駅数を返す（出発駅を除く）
			return stationList.size() - 1;
		}
	}

	//アニメーション関係
	public void nodesAnimation(ObservableList<Node> nodes, String patern) {
		switch (patern) {
		case "launch":
			try {
				Thread.sleep(100); // 0.1秒(1万ミリ秒)間だけ処理を止める
				return;
			} catch (InterruptedException e) {
			}
			break;
		case "駅数":
			for (int i = 0; i < nodes.size(); i++) {

				Node node = nodes.get(i);

				// 拡大のアニメーション
				ScaleTransition st = new ScaleTransition(Duration.millis(200 + i * 75), node);
				st.setFromX(0); // 開始時のX方向のスケールを0に設定
				st.setFromY(0); // 開始時のY方向のスケールを0に設定
				st.setToX(1.0); // 終了時のX方向のスケールを1（元のサイズ）に設定
				st.setToY(1.0); // 終了時のY方向のスケールを1（元のサイズ）に設定
				st.setCycleCount(1);
				st.setAutoReverse(false);

				FadeTransition ft = new FadeTransition(Duration.millis(200 + i * 75), node);
				ft.setFromValue(0);
				ft.setToValue(1.0);
				ft.setCycleCount(1);

				ParallelTransition pt = new ParallelTransition(st, ft);
				pt.play();

			}
			break;
		default:
			for (int i = 0; i < nodes.size(); i++) {

				Node node = nodes.get(i);

				TranslateTransition tt = new TranslateTransition(Duration.millis(200 + i * 150), node);
				tt.setFromX(200);
				tt.setToX(0);
				tt.setCycleCount(1);
				tt.setAutoReverse(false);

				FadeTransition ft = new FadeTransition(Duration.millis(200 + i * 150), node);
				ft.setFromValue(0);
				ft.setToValue(1.0);
				ft.setCycleCount(1);

				ParallelTransition pt = new ParallelTransition(tt, ft);
				pt.play();

			}
			break;
		}

	}

	//のちのち同名駅の区別もできるようにする ComboBoxとか使うといい気がします...
	public Station parseStation(String stationName, Data data) {
		/*
		ArrayList<Station> allStation = allData.getAllStation();
		for (int i = 0; i < allStation.size(); i++) {
			Station st = allStation.get(i);
		
			if (st.getName().equals(stationName)) {
				System.out.println(st.getName());
				return st;
			}
		}
		*/
		return data.searchStation(stationName);
	}

	private String valueOf2Digits(int n) { //一桁のintの先頭に0をつけた二桁のStringでかえす
		if (n < 10) {
			return "0" + String.valueOf(n);
		}
		return String.valueOf(n);
	}

	public static void main(String[] args) {
		// アプリケーションを起動する
		Application.launch(args);
	}

	public class LineMap {
		Map<String, String> map = new HashMap<>();

		public LineMap() {
			map.put("路線1", "line1");
			map.put("路線2", "line2");
			map.put("路線3", "line3");
			map.put("山手線", "yamanote");
			map.put("東海道本線", "toukaidouhon");
			map.put("南武線", "nanbu");
			map.put("鶴見線", "turumi");
			map.put("武蔵野線", "musashino");
			map.put("横浜線", "yokohama");
			map.put("根岸線", "negishi");
			map.put("横須賀線", "yokosuka");
			map.put("中央本線", "tyuouhon");
			map.put("青梅線", "oume");
			map.put("五日市線", "itsukaichi");
			map.put("東北本線", "touhoku");
			map.put("赤羽線", "akabane");
			map.put("常磐線", "jouban");
			map.put("総武本線", "soubu");
			map.put("京葉線", "keiyou");
			map.put("総武線快速", "soubukaisoku");
			map.put("中央線快速", "tyuoukaisoku");
			map.put("上野東京ライン", "uenotokyoline");
			map.put("常磐線快速", "joubankaisoku");
			map.put("常磐線各停", "joubankakutei");
			map.put("高崎線", "takasaki");
			map.put("宇都宮線", "utsunomiya");
			map.put("京浜東北線・根岸線", "keihintouhokunegishi");
			map.put("湘南新宿ライン", "syounanshinjuku");
			map.put("中央線・総武線各駅", "tyuousoubukakueki");
			map.put("八高線", "hachikou");
			map.put("内房線", "uchibou");
			map.put("外房線", "sotobou");
			map.put("青梅線・五日市線", "oumeitsukaichi");
			map.put("京浜東北線", "keihintouhoku");
			map.put("東海道線", "toukaidou");
			map.put("根岸線", "negishi");
			map.put("総武線快速・横須賀線", "soubukaisokuyokosuka");
			map.put("中央線", "tyuou");
			map.put("総武線各停", "soubukakutei");
			map.put("中央線・総武線各停", "soubukakutei");
			map.put("埼京線", "saikyou");
			map.put("埼京線・りんかい線", "saikyourinkai");
			map.put("相鉄・JR線", "aitetsujr");
		}

		public String getMapValue(String key) {
			if (!map.containsKey(key)) {
				return null;
			}
			return map.get(key);
		}

		public String getMapKey(String value) {
			for (String key : map.keySet()) {
				if (map.get(key).equals(value)) {
					return key;
				}
			}
			return null;
		}

	}

	// ボタンとアニメーションDURATIONを引数に取る
	public void buttonPressedAnimation(Button button) {

		button.setOnMousePressed(event -> {
			// アニメーションの定義
			Timeline timeline = new Timeline(
					new KeyFrame(Duration.ZERO, new KeyValue(button.effectProperty(), null)),

					new KeyFrame(Duration.millis(10),
							new KeyValue(button.effectProperty(), new ColorAdjust(0, -0.5, -0.5, 0))));

			timeline.play(); // アニメーション開始
		});

		button.setOnMouseReleased(event -> {
			button.setEffect(null); // アニメーション解除 
		});

	}

	// ノードを表示するメソッド
	public void showNode(Node node) {
		node.setVisible(true);
	}

	// ノードを非表示にするメソッド
	public void hideNode(Node node) {
		node.setVisible(false);
	}

	// 発着入れ替えメソッド
	public void swapText(TextField startText, TextField goalText) {

		// 現在の入力値を取得
		String temp = startText.getText();

		// テキストフィールドの値を入れ替える
		startText.setText(goalText.getText());
		goalText.setText(temp);

	}
}