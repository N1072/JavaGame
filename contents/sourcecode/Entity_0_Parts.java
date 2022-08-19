import java.awt.Graphics;
import java.awt.Color;

import java.awt.Rectangle;

//エンティティのパーツクラス(当たり判定や表示に使う)
class Parts {
    private Rectangle rect;// 当たり判定用の長方形インスタンス

    private EntityBase master;// パーツの持ち主
    private int x;// 持ち主の座標から見たx座標
    private int y;// 持ち主の座標から見たy座標
    private int h;// 横幅
    private int v;// 縦幅
    private Color color;// 表示色

/**
     * コンストラクタ
     * 
     * @param master パーツの持ち主
     * @param x      x座標
     * @param y      y座標
     * @param h      横幅
     * @param v      縦幅
     * @param color  表示色
     */
    Parts(EntityBase master, int x, int y, int h, int v, Color color) {
        // ---引数で指定した属性の設定---//
        this.master = master;
        this.x = x;
        this.y = y;
        this.h = h;
        this.v = v;
        this.color = color;

        // ---判定用長方形の生成・設定---//
        this.rect = new Rectangle(x, y, h, v);
        rectmove();
    }

    /**
     * パーツの描写
     * @param g グラフィッグコンポーネント
     */
    void draw(Graphics g) {
        g.setColor(color);
        g.fillRect((int) ((x + master.getX()) * Common.getsize()), (int) ((y + master.getY()) * Common.getsize()),
                (int) (h * Common.getsize()), (int) (v * Common.getsize()));
    }

    /**
     * 指定座標にそのパーツがぶつかるか確認
     * 
     * @param x x座標
     * @param y y座標
     * @return ぶつかったらtrue
     */
    boolean chkCol(int x, int y) {
        return rect.contains(x, y);
    }

    

    /**
     * 判定を移動させる(このパーツが生成された時と・持ち主のエンティティが移動した時のみに使う)
     * 
     * @param x x座標
     * @param y y座標
     */
    void rectmove() {
        rect.setLocation(master.getX() + this.x, master.getY() + this.y);
    }

    /**
     * サイズ変更
     * 
     * @param h 新しい横幅
     * @param v 新しい縦幅
     */
    void setSize(int h, int v) {
        this.h = h;
        this.v = v;
        rect.setSize(h, v);
    }
}