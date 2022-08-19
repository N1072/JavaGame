//メインクラス(ステージエディタ)
public class LevelEditor{
    public static void main(String[] args) {
        Screen scr = new Screen("マップエディタ", 0, 0);
        scr.start(new EDraw(scr));
    }
    
}

