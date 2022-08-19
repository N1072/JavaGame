import javax.swing.JFrame;
import javax.swing.JPanel;

//ウィンドウの設定用クラス
class Screen extends JFrame {
    private String title;
    private int w;
    private int h;
    private JPanel panel;

    /**
     * コンストラクタ
     * 
     * @param title タイトル
     * @param w     横幅
     * @param h     縦幅
     */
    Screen(String title, int w, int h) {
        // 引数で指定した属性を設定
        this.title = title;
        this.w = w;
        this.h = h;
        // ウィンドウのタイトルを設定
        setTitle(this.title);
        // ウィンドウサイズを設定
        fixedSetSize(this.w, this.h);
        // 閉じたらプログラムを終了するように設定
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // リサイズ不可能にする
        setResizable(false);
        // ウィンドウを真ん中に持ってくる
        setLocationRelativeTo(null);

    }

    /**
     * ウィンドウサイズの指定(元のメソッドを使うと指定したサイズより少し小さくなるため、その分調整を加えたサイズにする)
     * ついでにウィンドウを真ん中に持ってくる
     * 
     * @param w 幅
     * @param h 高さ
     */
    void fixedSetSize(int w, int h) {
        setSize(w + 16, h + 39);
        setLocationRelativeTo(null);
    }

    /**
     * ウィンドウの使用を開始する(見えるようにする)
     * 
     * @param panel 加えるJpanelのインスタンス
     *              加えるJpanelの生成をコンストラクタの処理とこの処理の間に行う必要があるため分離
     */
    void start(JPanel panel) {
        // Jpanel(画面描写用クラス)のインスタンスをウィンドウに加える
        this.add(panel);

        // 加えたJpanelをメモしておく
        this.panel = panel;
        // ウィンドウを表示する
        setVisible(true);
    }

    JPanel getPanel() {
        return panel;
    }
}
