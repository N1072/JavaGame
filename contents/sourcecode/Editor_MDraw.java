import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.*;

// メニュー部分に関するクラス
class MDraw extends JPanel implements MouseListener, MouseMotionListener {
    private EDraw master;// 自分を呼び出したEDrawインスタンスを格納する

    private final int P_END = 12;// タイルの種類の数

    private int[] cPoint = { 0, 0 };// マウスボタンが押し下げられた時の座標を格納しておく
    private int[] pointer = { 0, 0 };// カーソルの位置を格納しておく

    // private JPopupMenu popup;//
    private JTextField xIn;
    private JTextField yIn;
    Screen test;// テストプレイで使うウィンドウ

    private boolean testPlaying;// テストプレイ中かどうか
    // Draw1((int)(e.getPoint().getX() / Common.GRID), (int)(e.getPoint().getY() /
    // Common.GRID));

    MDraw(EDraw eDraw, Screen menu, Screen scr, MapEdit map) {
        master = eDraw;
        // this.menu = menu;//menu.fixedSetSize(this.mapx * Common.GRID, this.mapy *
        // Common.GRID);
        // this.scr = scr;

        this.setLayout(null);
        // ウィンドウサイズ変更
        menu.fixedSetSize(P_END * 32, 100);

        // ---ポップアップメニュー設定---//
        JPopupMenu popup = new JPopupMenu();
        // ポップアップメニューの中身設定

        // 「セーブ」
        JMenuItem menuSave = new JMenuItem("上書セーブ");
        // クリックした際のアクション
        menuSave.addActionListener(e -> {
            master.save(master.getEditingFile());
        });
        popup.add(menuSave);

        // 「セーブ」
        JMenuItem menuSaveAs = new JMenuItem("別名セーブ");
        // クリックした際のアクション
        menuSaveAs.addActionListener(e -> {
            master.save();
        });
        popup.add(menuSaveAs);

        // 「ロード」
        JMenuItem menuLoad = new JMenuItem("ロード");
        // クリックした際のアクション
        menuLoad.addActionListener(e -> {
            master.load();
        });
        popup.add(menuLoad);

        // 「クリア」
        JMenuItem menuClear = new JMenuItem("クリア");
        // クリックした際のアクション
        menuClear.addActionListener(e -> {
            map.clear("0");
            master.repaint();
        });
        popup.add(menuClear);

        // ---メニューボタンの設定---//
        JButton menuButton = new JButton("メニュー");
        // クリックした際のイベント
        menuButton.addActionListener(e -> {
            // ポップアップメニュー表示
            popup.show(this, pointer[0], pointer[1]);
        });
        // ボタンの設置
        this.add(menuButton);
        menuButton.setBounds(64, 1, 100, 30);

        // ---テストプレイボタンの設定---//
        JButton testButton = new JButton("テストプレイ");
        // クリックした際のイベント
        testButton.addActionListener(e -> {
            // プレイヤーが1人だけ設置されていて、テストプレイ中でないなら
            if (master.getPlayerCount() == 1 && !testPlaying) {
                // ウィンドウ表示用インスタンスを生成
                test = new KeyScreen("テストプレイ", 0, 0);
                // ウィンドウを閉じたときの処理を変更しておく
                test.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                TestClose close = new TestClose(this, test);
                test.addWindowListener(close);

                // ウィンドウの使用をスタート
                testPlaying = true;
                test.start(new GDraw(test, master.getStage(), this));
            }
            // プレイヤーキャラが1人でない場合
            else if (master.getPlayerCount() != 1) {
                JOptionPane.showMessageDialog(master, "プレイヤーがいない、または多すぎます");
            }
            // テストプレイ中だった場合
            else if (testPlaying) {
                JOptionPane.showMessageDialog(test, "すでにテストプレイを実行中です");
            }

        });
        // ボタンの設置
        this.add(testButton);
        testButton.setBounds(160, 70, 130, 20);

        // ---マップサイズ変更用入力欄の設定---//
        // 入力欄
        xIn = new JTextField(2);
        yIn = new JTextField(2);
        // 決定ボタン
        JButton sizeSet = new JButton("Set");

        sizeSet.addActionListener(e -> {

            int[] newSize = { Integer.parseInt(xIn.getText()), Integer.parseInt(yIn.getText()) };
            map.setSize(newSize[0], newSize[1], "0", false);
            master.setSaved(false);
            master.updateSize();
            master.resize();
        });

        // 入力欄設置
        this.add(xIn);
        xIn.setBounds(0, 70, 30, 20);
        this.add(yIn);
        yIn.setBounds(50, 70, 30, 20);
        // ボタン設置
        this.add(sizeSet);
        sizeSet.setBounds(90, 70, 60, 20);

        int[] size = map.getSize();
        fixedSetSizeBox(String.valueOf(size[0]), String.valueOf(size[1]));

        // ---マウスリスナーなどを有効にする---//
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    /**
     * マップサイズ入力ボックスに指定の値を入力する
     * 
     * @param x 横幅部分に入れる値
     * @param y 縦幅部分に入れる値
     */
    void fixedSetSizeBox(String x, String y) {
        xIn.setText(x);
        yIn.setText(y);
    }

    /**
     * 描画用メソッド
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 現在選択しているタイルを表示
        DrawMap.drawParts(0, 0, master.getVar(), 32, true, g);
        // タイル番号を表示
        g.setColor(Color.BLACK);
        g.drawString(String.valueOf((int) master.getVar() - 48), 32, 32);

        // パレットを表示
        for (int i = 0; i < P_END; i++) {
            DrawMap.drawParts(i * 32, 32, (char) (i + 48), 32, true, g);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // (使わないが、インターフェイスの仕様上書かないと動かない)
    }

    /**
     * マウスが押されたときの処理
     */
    @Override
    public void mousePressed(MouseEvent e) {
        // 押された場所を記録しておく
        cPoint[0] = (int) e.getPoint().getX();
        cPoint[1] = (int) e.getPoint().getY();

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // パレットからタイルを選択する
        // [マウスのボタンを押し下げたy座標]と[離したy座標]がどちらも32で割って切り捨てると1になる場合
        if ((int) (e.getPoint().getY() / 32) == 1 && cPoint[1] / 32 == 1) {
            // 離した x座標に応じてタイルを選択する
            // 選択するタイルの計算式 = 離したx座標 ÷ 32 + 48 (48は数値を文字列に変換する際に正しく変換するため)

            // 選択したタイルの番号を取得
            int selectVar = (int) e.getPoint().getX() / 32;
            // ボタン押し下げ時点で選択していたタイルも取得
            int cVar = cPoint[0] / 32;

            // 同じタイルの上でマウスのボタンを押して離した場合
            if (selectVar == cVar) {
                master.setVar((char) (selectVar + 48));
            }

            // 再描写
            repaint();
        }

    }

    /**
     * テストプレイの終了処理
     */
    void endTest() {
        testPlaying = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // (使わないが、インターフェイスの仕様上書かないと動かない)

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // (使わないが、インターフェイスの仕様上書かないと動かない)

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        pointer[0] = (int) e.getPoint().getX();
        pointer[1] = (int) e.getPoint().getY();

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        pointer[0] = (int) e.getPoint().getX();
        pointer[1] = (int) e.getPoint().getY();

    }

}
