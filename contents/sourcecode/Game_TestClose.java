import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

//テストプレイのウィンドウを閉じた際の処理
class TestClose extends WindowAdapter {
    MDraw mDraw;
    Screen scr;

    /**
     * コンストラクタ
     * 
     * @param mDraw エディタのメニュー画面描写用インスタンス
     * @param scr   テストプレイ用画面インスタンス
     */
    TestClose(MDraw mDraw, Screen scr) {
        // 引数の属性を設定
        this.mDraw = mDraw;
        this.scr = scr;
    }

    /**
     * ファイルを閉じた時の処理
     */
    public void windowClosing(WindowEvent e) {
        mDraw.endTest();
        scr.dispose();
    }
}
