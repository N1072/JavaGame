import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;

// マップエディタの「閉じる」を押した際の処理を定義するクラス
class EditClose extends WindowAdapter {
    EDraw eDraw;
    Screen scr;

    public void setEDraw(EDraw eDraw) {
        this.eDraw = eDraw;
    }

    public void setScr(Screen scr) {
        this.scr = scr;
    }

    /**
     * マップエディタの「閉じる」を押した際の処理
     */

    public void windowClosing(WindowEvent e) {
        // 編集中マップが保存済みならプログラム終了
        if (eDraw.getSaved()) {
            System.exit(0);
        }

        // 保存済みでないなら確認画面を出す
        int confirm = JOptionPane.showConfirmDialog(scr, "保存しますか？");

        // 「はい」の場合
        if (confirm == JOptionPane.YES_OPTION) {
            // 正常に保存出来たら閉じる
            if (eDraw.save()) {
                System.exit(0);
            }
        }

        // 「いいえ」の場合
        else if (confirm == JOptionPane.NO_OPTION) {
            // そのまま閉じる
            System.exit(0);
        }
        // 「取消」の場合は何もしない
    }
}