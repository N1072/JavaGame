import java.awt.Color;

//ボタンクラス
class Button extends EntityBase {
    private int rot;// ボタンの向き
    private boolean pressed = false;// 押されたかどうか

    static private int left = 0;// 残りボタン数

    /**
     * コンストラクタ
     * 
     * @param x   x座標
     * @param y   y座標
     * @param rot 向き
     *            0=上方向
     *            1=左方向
     *            2=上方向
     *            3=左方向
     */
    Button(int x, int y, int rot) {
        super(x, y);
        this.rot = rot;

        // ボタン数を1つ増やす
        Button.left++;

        // 上方向
        if (rot == 0) {
            Parts[] parts = { new Parts(this, 2, 20, 28, 12, Color.RED), new Parts(this, 0, 28, 32, 4, Color.GRAY) };
            setParts(parts);
        }

        // 左方向
        if (rot == 1) {
            Parts[] parts = { new Parts(this, 20, 2, 12, 28, Color.RED), new Parts(this, 28, 0, 4, 32, Color.GRAY) };
            setParts(parts);
        }

        // 上方向
        if (rot == 2) {
            Parts[] parts = { new Parts(this, 2, 0, 28, 12, Color.RED), new Parts(this, 0, 0, 32, 4, Color.GRAY) };
            setParts(parts);
        }

        // 左方向
        if (rot == 3) {
            Parts[] parts = { new Parts(this, 0, 2, 12, 28, Color.RED), new Parts(this, 0, 0, 4, 32, Color.GRAY) };
            setParts(parts);
        }

    }

    /**
     * プレイヤーとぶつかった時の処理(ボタンが押される)
     */
    @Override
    public void action() {
        // 押されてなくて、ボタンの向きとプレイヤーの向きが同じなら
        if (!pressed && EntityBase.getPlayer().getGrav() == rot) {
            // ボタンの赤い部分をnullにする(消す)
            setPartsInd(0, null);

            // 押されたことを記録
            pressed = true;

            // ボタン数を1つ減らす
            left--;

            System.out.println("残り" + left);
            // ボタン数が0ならドアをオープン
            if (left <= 0) {
                Door.open();
            }
        }

    }

    /**
     * 残りボタン数取得
     * 
     * @return 残りボタン数
     */
    static int getLeft() {
        return left;
    }

    /**
     * リセット処理
     */
    static void reset() {
        left = 0;
    }
}