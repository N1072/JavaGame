import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.FontMetrics;

// import java.awt.Color;

import java.util.ArrayList;
import java.util.Objects;
// import java.util.Random;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

//描画用クラス(ゲーム用)
class GDraw extends JPanel implements Runnable {

    // ---ステージ関連---//
    static private ArrayList<String> stage;// ステージデータ
    static private int mapx;// マップxサイズ
    static private int mapy;// マップyサイズ

    static private Thread t;// スレッドインスタンス
    static private Screen scr;// 画面インスタンス
    static private Player player;// プレイヤーインスタンス

    static private int[] press;// ボタン状況格納用配列(右、左、上、下キーが押されているかを管理する)

    static private GDraw ins;// インスタンス

    static private boolean tStop = false;// メインループストップ用(これがtrueになるとループを抜ける)

    static private boolean clear = false;// クリアしたかどうか

    static private double congColor = 0;// 「おめでとうございます」の文字色指定用
    static private int congInter = 500;// 「おめでとうございます」の間隔
    static private Font congFont;// 「おめでとうございます」のフォント
    static private int congX;// 「おめでとうございます」のx座標

    static private Font nextFont;// 「スペースで次へ」のフォント

    static private String world;// 現在プレイしているワールド
    static private int level;// 現在プレイしているレベル
    static private File stageFile;// 現在プレイしているステージのファイル

    static private JFileChooser selecter;// ファイル選択画面表示用インスタンス
    static private int selected;// selecterでの選択状況格納用変数

    static private boolean testPlay = false;// エディタのテストプレイから起動したかどうか

    static private boolean ending = false;// エンディングを流しているかどうか
    private static MDraw mDraw;

    // クラス初期化子
    static {
        GDraw.press = new int[4];
        //---「おめでとうございます」「スペースで次へ」のフォント設定---//
        try {
            congFont = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream("logo.ttf"));
            nextFont = congFont.deriveFont(30F);
            congFont = congFont.deriveFont(50F);
        } catch (FontFormatException e) {
            // 指定されたフォントが無効のときの例外処理(書かないとエラーが出る)
            e.printStackTrace();
        } catch (IOException e) {
            // 読み込み時に何かしらのエラーが出た(書かないとエラーが出る)
            e.printStackTrace();
        }
    }
    // インスタンス初期化子
    {
        ins = this;
    }

    /**
     * コンストラクタ
     * 
     * @param scr 画面インスタンス
     */

    GDraw(Screen scr) {

        GDraw.scr = scr;

        // ---ファイル選択画面表示用インスタンスの生成(その際カレントディレクトリはstagesとする)---//
        selecter = new JFileChooser(new File("stages"));

        // ---ファイル選択画面のファイルフィルターを設定---//
        FileNameExtensionFilter filter = new FileNameExtensionFilter("ステージファイル(.stage)", "stage");
        selecter.addChoosableFileFilter(filter);
        // 「すべてのファイル」を選べなくする
        selecter.setAcceptAllFileFilterUsed(false);
        // selecter.setCurrentDirectory();

        // ステージロード＆ゲーム開始処理を行い、キャンセルされたらプログラム終了
        if (!loadAndStart()) {
            System.exit(0);
        }

    }

    /**
     * コンストラクタ(テストプレイ用)
     * 
     * @param scr   画面インスタンス
     * @param stage ステージデータ
     */
    GDraw(Screen scr, ArrayList<String> stage, MDraw mDraw) {
        // 引数からデータを設定
        GDraw.scr = scr;
        GDraw.stage = stage;
        GDraw.mDraw = mDraw;
        // マップサイズを設定
        GDraw.mapx = stage.get(0).length();
        GDraw.mapy = stage.size();

        // テストプレイを有効にする
        testPlay = true;

        // ゲーム開始処理
        gameStart();

    }

    /**
     * ファイルを読み込んで、ステージの準備をする
     * 
     * @param stagedata 読み込むステージファイル
     */
    static void loadStage(File stagedata) {
        // ---ステージを読み込む---//
        stageFile = stagedata;
        stage = StageIO.load(stagedata);

        try {
            // マップサイズを設定
            GDraw.mapx = stage.get(0).length();
            GDraw.mapy = stage.size();

            // ---ステージファイル名からワールドとレベルを抽出--- //
            world = stagedata.getName();
            level = 0;

            int nextDigit = 1;// 次に追加する桁

            char at;// ステージファイル名から1文字とって格納する

            // 後ろから1文字ずつ確認していく
            for (int i = world.length() - 7; i >= 0; i--) {
                at = world.charAt(i);
                // 数字ならレベルに加える
                if ('0' <= at && at <= '9') {
                    level += (at - 48) * nextDigit;
                    nextDigit *= 10;
                }
                // 数字でないなら最後の処理をし、繰り返し終了
                else {
                    // 数字を一切抽出せずに終わった場合、レベルは-1とする
                    if (i == world.length() - 7) {
                        level = -1;
                    }
                    // ワールド名をファイル名からレベルの数字をとったものにする
                    world = world.substring(0, i + 1);

                    break;
                }
            }
        } catch (NullPointerException e) {
            // stageがnullの場合
        }
    }

    /**
     * メインループ(スレッド)を終了させる
     */
    static void endThread() {
        // メインループに終了の指示を出す(ループを抜けさせる)
        tStop = true;

        // メインループが終了するまで待機(繰り返す)
        try {
            while (t.isAlive()) {
            }
        } catch (NullPointerException e) {

        }
    }

    /**
     * ゲームスタート・リトライ時の処理
     */
    static boolean gameStart() {

        // メインループを終了させる
        endThread();

        // スレッドインスタンスを生成
        t = new Thread(ins);

        // ステージクリアしていない状態にする
        clear = false;

        // リセット処理
        player = null;
        EntityBase.reset();
        Button.reset();
        congX = congInter - 50;

        // ウィンドウサイズの設定
        scr.fixedSetSize((int) (GDraw.mapx * Common.fix()), (int) (GDraw.mapy * Common.fix()));

        // エンティティがステージを参照できるようにする
        EntityBase.setStage(stage);

        // マップ上にインスタンスの生成
        for (int i = 0; i < mapy; i++) {
            for (int j = 0; j < mapx; j++) {
                // プレイヤー
                if (stage.get(i).charAt(j) == '1') {
                    player = new Player(j * Common.GRID + 11, i * Common.GRID + 16);
                }
                // ボタン(上向き)
                if (stage.get(i).charAt(j) == '7') {
                    new Button(j * Common.GRID, i * Common.GRID, 0);
                }
                // ボタン(下向き)
                if (stage.get(i).charAt(j) == '8') {
                    new Button(j * Common.GRID, i * Common.GRID, 2);
                }
                // ボタン(右向き)
                if (stage.get(i).charAt(j) == ':') {
                    new Button(j * Common.GRID, i * Common.GRID, 1);
                }
                // ボタン(左向き)
                if (stage.get(i).charAt(j) == '9') {
                    new Button(j * Common.GRID, i * Common.GRID, 3);
                }
                // ドア
                if (stage.get(i).charAt(j) == ';') {
                    new Door(j * Common.GRID, i * Common.GRID);
                }
            }
        }

        // ボタンが1つも設置されていない場合、最初からドアを開いておく
        if (Button.getLeft() == 0) {
            Door.open();
        }

        // プレイヤーがいない場合、falseを返す
        if (Objects.isNull(player)) {
            return false;
        }

        // エンディングが表示されているなら、終了させておく
        ending = false;

        // テストプレイでないなら、ウィンドウタイトルを変更
        if (!testPlay) {
            scr.setTitle(stageFile.getName());
        }

        t.start();
        return true;
    }

    /**
     * ファイル選択、ロード、ゲーム開始の処理を行う
     */
    static boolean loadAndStart() {
        while (true) {
            // ---ファイル選択画面を表示し、ステージを選択させる---//
            selected = selecter.showOpenDialog(ins);
            if (selected != 0) {
                return false;
            }

            // ---選択したステージを読み込む---//
            loadStage(selecter.getSelectedFile());

            // ゲーム開始処理
            if (gameStart() == true) {
                break;
            }

            JOptionPane.showMessageDialog(scr, "このステージにはプレイヤーがいません");
        }
        return true;
    }

    /**
     * ボタンの状況を受け取る
     * 
     * @param num ボタン番号(0 = 右, 1 = 左, 2 = 上, 3 = 下)
     * @param val 押されているかどうか(0 = 押されていない 1 = 押されている)
     */
    static void press(int num, int val) {
        press[num] = val;
    }

    /**
     * 指定y座標の中央に文字描写
     * 
     * @param y      y座標
     * @param string 文字列
     * @param font   フォント
     * @param g      グラフィッグコンポーネント
     */
    void drawStringCenter(int y, String string, Font font, Graphics g) {
        // 使用するフォントのFontMetricsを取得
        FontMetrics fm = g.getFontMetrics(font);
        // フォント設定
        g.setFont(font);
        // 描写する
        g.drawString(string, (scr.getWidth() - 16 - fm.stringWidth(string)) / 2, y);

    }

    /**
     * 描写メソッド
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // ---エンディングではい場合の表示---//
        if (!ending) {
            // ---背景色の設定---//
            setBackground(new Color(238, 238, 238));

            // ---マップ描写---//
            DrawMap.run(stage, false, g, Common.fix());

            // ---インスタンスを一つ一つ描写する---//
            EntityBase.drawAll(g);

            // ---クリアした際の画面表示---//
            if (clear) {
                // Pfont
                // 文字色を設定する
                congColor = (congColor + 0.01) % 1;
                g.setColor(Color.getHSBColor((float) congColor, (float) 0.8, (float) 0.9));

                // 文字の表示位置を格納しておく変数
                int drawX;
                int drawY = (scr.getSize().height - 39) / 2;

                // 文字描写
                for (int i = -1; i <= (scr.getWidth() - 16) / congInter; i++) {
                    // 表示するx座標を決める
                    drawX = -congX + congInter + i * congInter;
                    // 「おめでとうございます」
                    g.setFont(congFont);
                    g.drawString("おめでとうございます", drawX, drawY);
                    // 「スペースで次へ」(テストプレイでなければ表示)
                    if (!testPlay) {
                        g.setFont(nextFont);
                        g.drawString("スペースで次へ", drawX + 115, drawY + 40);
                    }

                }
                congX = (congX + 1) % congInter;
            }
        }
        // ---エンディングの場合の表示---//
        else {
            // 背景色の設定
            setBackground(Color.BLACK);

            // 文字色を設定する
            congColor = (congColor + 0.01) % 1;
            g.setColor(Color.getHSBColor((float) congColor, (float) 0.8, (float) 0.9));

            // 文字描画
            drawStringCenter(130, "おめでとうございます", congFont, g);
            drawStringCenter(180, (level <= 0 ? "" : "全ての") + "ステージをクリアしました", nextFont, g);
            drawStringCenter(250, "スペースキーでファイル選択画面", nextFont, g);
            drawStringCenter(280, "Escキーで終了", nextFont, g);
            drawStringCenter(380, "ありがとうございます", nextFont, g);
        }

    }

    /**
     * 指定したキーが押されたらプレイヤーを操作する処理
     * 
     * @param key     判定するキー(0 = 右, 1 = 左, 2 = 上, 3 = 下)
     * @param gravDir 重力の向き(0 = 縦, 1 = 横)
     * @param walkDir 歩く方向(true = 右 or下, false 左 or 上)
     * @param grav    突進する際の重力の向き
     */
    private void controle(int key, int gravDir, boolean walkDir, int grav) {
        if (press[key] > 0) {
            // 重力が指定方向なら
            if (player.getGrav() % 2 == gravDir) {
                // 歩く
                player.walk(walkDir);
            }
            // そうでないなら
            else {
                // 重力を反転して突進する
                player.setGrav(grav);
            }
        }
    }

    // --------------------------------------------------------------------------------------------------------------//

    /**
     * メインループ
     */
    public void run() {
        tStop = false;
        int ground;

        while (!tStop) {

            // ---16ミリ秒処理を停止---//
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
            }

            // ---ゲーム中の処理---//
            if (!ending) {

                // ---着地しているかどうか---//
                ground = player.chkGround();

                // ---もし着地しているなら---//
                if (ground >= 0) {
                    // ---もしも突進中かつ坂に着地したなら---//
                    if (player.getRushing() && player.chkGround() == 2) {
                        // 坂に当たった時に跳ね返る処理

                        if (player.chkGround(1) <= 0) {
                            if (player.getGrav() % 2 == 0) {
                                player.rotGrav(player.getGrav() == 0 ? 1 : -1);
                            } else {
                                player.rotGrav(player.getGrav() == 1 ? -1 : 1);
                            }
                        }

                        else if (player.chkGround(-1) <= 0) {
                            if (player.getGrav() % 2 == 0) {
                                player.rotGrav(player.getGrav() == 0 ? -1 : 1);
                            } else {
                                player.rotGrav(player.getGrav() == 1 ? 1 : -1);
                            }
                        }

                        // 坂に当たらなかった場合突進を終了
                        else {
                            player.setRushing(false);
                        }
                    }
                    // ---キャラ操作(突進中でない場合)---//
                    else {
                        player.setRushing(false);

                        // 右方向
                        controle(0, 0, true, 1);

                        // 左方向
                        controle(1, 0, false, 3);

                        // 上方向
                        controle(2, 1, false, 2);

                        // 下方向
                        controle(3, 1, true, 0);
                    }

                }
                // ---着地していない場合---//
                else {
                    player.fall(player.getRushing() ? 12 : 6);
                    if (ground == -2) {
                        clear = true;
                    }
                }
            }
            repaint();

        }
    }

    // --------------------------------------------------------------------------------------------------------------//

    /**
     * ゲームの終了処理(Escキーを押した際に実行する)
     */
    static void gameEnd() {
        if (testPlay) {
            // エディタのテストプレイなら終了処理をし、ゲーム画面を破棄する
            mDraw.endTest();
            scr.dispose();
        } else {
            // 通常のプレイならプログラム終了
            System.exit(0);
        }
    }

    static boolean getEnding() {
        return ending;
    }

    /**
     * 次のステージに移行する
     */
    static void nextStage() {
        // ---エンディングでなければ次のステージを読み込む---//
        if (!ending) {
            // 現在のステージをクリアしていない、またはエディタのテストプレイなら中断
            if (!clear || testPlay) {
                return;
            }

            // 現在のステージのパスを取得し、文字列変換
            String path = stageFile.toPath().toString();
            // 次のステージのパスを設定しておく
            String nextStage = path.substring(0, path.length() - stageFile.getName().length()) + world + (level + 1)
                    + ".stage";

            // 次のステージを読み込む
            loadStage(new File(nextStage));

            // ゲーム開始処理
            try {
                gameStart();
            }
            // 次のステージがnullなら
            catch (NullPointerException e) {
                // エンディングを流す
                if (!ending) {
                    endThread();
                    ending = true;
                    scr.setTitle("エンディング");
                    scr.fixedSetSize(640, 480);
                    t.start();
                }

            }
        }
        // ---エンディングが表示されている場合、ファイルから選択する---//
        else {
            loadAndStart();
        }

    }

}
