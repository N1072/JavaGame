//メインクラス
public class GameMain{
    public static void main(String[] args) {
        Screen scr = new KeyScreen("ゲーム画面", 0, 0);
        scr.start(new GDraw(scr));
    }
}
// プログラム内では、プレイヤーキャラや、ボタン、ドアなど、動きや変化がある物、タイル単位でない当たり判定があるものを「entity(エンティティ)」と呼んでいる