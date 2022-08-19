import java.awt.event.*;

// キーイベント付きウィンドウの設定用クラス
class KeyScreen extends Screen implements KeyListener {

    /**
     * コンストラクタ
     * 
     * @param title ウィンドウタイトル
     * @param w     横幅
     * @param h     縦幅
     */
    KeyScreen(String title, int w, int h) {
        super(title, w, h);
        addKeyListener(this);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // (使わないが、インターフェイスの仕様上書かないと動かない)

    }

    /**
     * 各キーが押されたときの処理
     */
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            // 右キー
            case KeyEvent.VK_RIGHT:
                GDraw.press(0, 1);
                break;
            // 左キー
            case KeyEvent.VK_LEFT:
                GDraw.press(1, 1);
                break;
            // 上キー
            case KeyEvent.VK_UP:
                GDraw.press(2, 1);
                break;
            // 下キー
            case KeyEvent.VK_DOWN:
                GDraw.press(3, 1);
                break;
            // エンターキー
            case KeyEvent.VK_ENTER:
                // エンディング表示中でないなら
                if (GDraw.getEnding() == false) {
                    // ゲーム開始処理(リトライ)
                    GDraw.gameStart();
                }
                break;
            // Escキー
            case KeyEvent.VK_ESCAPE:
                // ゲーム終了
                GDraw.gameEnd();
                break;
            // スペースキー
            case KeyEvent.VK_SPACE:
                // 次のステージへ
                GDraw.nextStage();
                break;
        }

    }

    /**
     * 各キーが離されたときの処理
     */
    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            // 右キー
            case KeyEvent.VK_RIGHT:
                GDraw.press(0, 0);
                break;
            // 左キー
            case KeyEvent.VK_LEFT:
                GDraw.press(1, 0);
                break;
            // 上キー
            case KeyEvent.VK_UP:
                GDraw.press(2, 0);
                break;
            // 下キー
            case KeyEvent.VK_DOWN:
                GDraw.press(3, 0);
                break;
        }

    }
}