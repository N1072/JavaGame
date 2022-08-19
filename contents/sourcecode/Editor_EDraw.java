import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.*;

import java.io.File;
// import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Objects;

// エディターのメイン部分に関するクラス
class EDraw extends JPanel implements MouseListener, MouseMotionListener {
    private ArrayList<String> stage;// 編集しているステージデータ
    private char var = '2';// 現在選択しているタイル

    private boolean Drawing = false;// trueの間はドラッグでタイルを置き続ける

    private static Screen scr; // 画面インスタンス参照先
    private MapEdit map;// マップ選択用インスタンス参照先

    private Screen menu;// メニュー画面インスタンス参照先
    private JFileChooser selecter;// ファイル選択画面表示用インスタンス
    private MDraw menuDraw;// メニュー画面表示用インスタンス

    private int selected;// ファイル選択結果格納先
    private int mapx;// マップの横サイズ
    private int mapy;// マップの縦サイズ

    private File editingFile;// 編集中ファイル
    private boolean saved = true;// 保存済みかどうか

    private int playerCount = 0;// 設置したプレイヤーキャラの数

    /**
     * コンストラクタ
     */
    EDraw(Screen scr) {
        // ---画面表示用クラスを結びつける---//
        EDraw.scr = scr;
        // 「閉じる」ボタンを押しても閉じないようにする(押したときの処理を別で定義している)
        scr.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // 「閉じる」をおしたときの処理を定義したクラスのインスタンスを生成
        EditClose close = new EditClose();

        close.setEDraw(this);
        close.setScr(EDraw.scr);

        scr.addWindowListener(close);

        // ---空のマップデータの作成---//
        map = new MapEdit(20, 15, "0");
        updateSize();

        // ---メニュー画面を用意する---//
        menu = new Screen("メニュー", 200, 100);
        menuDraw = new MDraw(this, menu, scr, map);

        // ---「閉じる」ボタンを押しても閉じないようにする---//
        menu.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        menu.start(menuDraw);

        // ---ファイル選択画面表示用インスタンスの生成(その際カレントディレクトリはstagesとする)---//
        selecter = new JFileChooser(new File("stages"));

        // ---ファイル選択画面のファイルフィルターを設定---//
        FileNameExtensionFilter filter = new FileNameExtensionFilter("ステージファイル(.stage)", "stage");
        selecter.addChoosableFileFilter(filter);
        // 「すべてのファイル」を選べなくする
        selecter.setAcceptAllFileFilterUsed(false);

        // ---ウィンドウサイズを設定---//
        resize();

        // ---ウィンドウの位置を変更---//
        menu.setLocation(scr.getX() - menu.getWidth(), scr.getY());

        // マウスリスナーなどを有効にする
        addMouseListener(this);
        addMouseMotionListener(this);

        // 編集中のファイルをnullとする
        setEditingFile(null);
    }

    /**
     * 編集中ファイルの設定
     * 
     * @param file ファイル
     */
    void setEditingFile(File file) {
        editingFile = file;
        // ついでにウィンドウタイトルも更新しておく
        updeteTitle();
    }

    /**
     * ウィンドウタイトルの更新
     */
    void updeteTitle() {
        // 表示例：マップエディタ - (編集中のファイル名)*
        scr.setTitle("マップエディタ - " + (Objects.isNull(editingFile) ? "無題" : editingFile.getName()) + (saved ? "" : "*"));
    }

    /**
     * 描写用メソッド
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // マップ描写
        DrawMap.run(map.exportMap(), true, g, Common.fix());

        // ---グリッドの描写---//
        g.setColor(Color.GRAY);
        // 縦方向
        for (int i = 0; i < mapx; i++) {
            for (int j = 0; j < mapy * Common.fix(); j += 2) {
                g.fillRect(i * Common.fix(), j, 1, 1);
            }
        }

        // 横方向
        for (int i = 0; i < mapy; i++) {
            for (int j = 0; j < mapx * Common.fix(); j += 2) {
                g.fillRect(j, i * Common.fix(), 1, 1);
            }
        }
    }

    /**
     * マップサイズの更新
     */
    void updateSize() {
        // マップサイズの取得
        int[] size = map.getSize();
        mapx = size[0];
        mapy = size[1];
    }

    /**
     * プレイヤーの数取得
     * 
     * @return プレイヤーの数
     */
    public int getPlayerCount() {
        return playerCount;
    }

    /**
     * 現在のマップチップの設置 & 再描写処理
     * 
     * @param x 設置するx座標
     * @param y 設置するx座標
     */
    void draw1(int x, int y) {
        try {
            setSaved(false);

            // 設置場所にプレイヤーがいたなら、プレイヤーの数を減らす
            if (map.get(x, y) == '1') {
                playerCount--;
            }

            map.put(x, y, var);

            // 設置したのがプレイヤーなら、プレイヤーの数を増やす
            if (var == '1') {
                playerCount++;
            }


            repaint();
        } catch (StringIndexOutOfBoundsException e) {
            // 横方向にはみ出すエラーを防止するためのcatch文
        } catch (IndexOutOfBoundsException e) {
            // 縦方向にはみ出すエラーを防止するためのcatch文
        }

    }

    /**
     * 選択中のタイルの取得
     */
    char getVar() {
        return var;
    }

    /**
     * 選択中のタイルの変更
     */
    void setVar(char set) {
        var = set;
    }

    /**
     * セーブする処理
     * 
     * @return 正常に保存できたらtrue
     */
    boolean save() {
        // ファイル選択画面表示
        selected = selecter.showSaveDialog(this);
        // selectedが0の場合(保存処理)
        if (selected == 0) {
            // 保存先ファイルの名前を取得
            String name = selecter.getSelectedFile().getPath();

            // 保存先ファイルを格納する
            File toSave;

            // 指定した保存先ファイルに.stageの拡張子がついていない場合、保存先ファイル名を.stageをつけたものにする
            if (!name.substring(name.length() - 6).equals(".stage")) {
                String newname = name + ".stage";
                toSave = new File(newname);
            }
            // ついているならそのまま保存先を設定
            else {
                toSave = selecter.getSelectedFile();
            }

            // 編集中マップを更新
            setEditingFile(toSave);

            // 保存処理を行う
            setSaved(true);
            StageIO.save(toSave, map.exportMap());
            return true;
        }
        return false;
    }

    void save(File toSave) {
        try {
            setSaved(true);
            StageIO.save(toSave, map.exportMap());
        } catch (NullPointerException e) {
            save();
        }

    }

    /** 
     * ロードする処理
     */
    void load() {
        // ファイル選択画面表示
        selected = selecter.showOpenDialog(this);

        // selectedが0の場合(ステージデータを読み込み、その結果からステージデータを生成)
        if (selected == 0) {
            File toLoad = selecter.getSelectedFile();

            // 選んだファイルからステージデータを読み込む
            stage = StageIO.load(toLoad);
            // 読み込んだステージをマップ編集用インスタンスにインポート
            map.importMap(stage);

            setEditingFile(toLoad);

            // サイズ格納変数の更新
            updateSize();

            // プレイヤーの数をカウントしなおす
            playerCount = 0;
            for (int i = 0; i < mapy; i++) {
                for (int j = 0; j < mapx; j++) {
                    if (map.get(j, i) == '1') {
                        playerCount++;
                    }

                }

            }

            // ウィンドウサイズ変更
            resize();
            // 再描写
            repaint();
        }
    }

    /**
     * 編集中ファイルの取得
     * 
     * @return
     */
    public File getEditingFile() {
        return editingFile;
    }

    /**
     * 保存済み状態の変更
     * 
     * @param saved 変更後の値
     */
    public void setSaved(boolean saved) {
        this.saved = saved;
        updeteTitle();
    }

    /**
     * 保存済み状態の取得
     * 
     * @return 保存済み状態
     */
    public boolean getSaved() {
        return saved;
    }

    /**
     * ウィンドウサイズをマップサイズに
     */
    void resize() {
        // マップサイズの取得
        int[] size = map.getSize();
        // ウィンドウのサイズを変更する
        scr.fixedSetSize(size[0] * Common.fix(), size[1] * Common.fix());
        // int[] size = master.map.getSize();
        menuDraw.fixedSetSizeBox(String.valueOf(size[0]), String.valueOf(size[1]));
    }

    /**
     * ステージの取得
     * 
     * @return ステージデータ
     */
    ArrayList<String> getStage() {
        return map.exportMap();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // マウスがクリックされた(使わないが、インターフェイスの仕様上書かないと動かない)
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // マウスのボタンが押し下げられた
        switch (e.getButton()) {
            case MouseEvent.BUTTON1:
                // マップチップの設置・Drawingをtrueに
                draw1((int) (e.getPoint().getX() / Common.fix()), (int) (e.getPoint().getY() / Common.fix()));
                Drawing = true;
                break;
            case MouseEvent.BUTTON3:
                // マップチップの取得
                setVar(map.get((int) (e.getPoint().getX() / Common.fix()), (int) (e.getPoint().getY() / Common.fix())));
                // メニュー画面の再描写
                menu.repaint();
                break;
        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // マウスのボタンが離された
        switch (e.getButton()) {
            case MouseEvent.BUTTON1:
                // Drawingをfalseに
                Drawing = false;
                break;
        }

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
        // Drawingがtrueならドラッグで描き続ける
        if (Drawing) {
            draw1((int) (e.getPoint().getX() / Common.fix()), (int) (e.getPoint().getY() / Common.fix()));
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // (使わないが、インターフェイスの仕様上書かないと動かない)
    }

}
