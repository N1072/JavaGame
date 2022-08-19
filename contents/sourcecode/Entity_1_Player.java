import java.awt.Color;

//プレイヤークラス
class Player extends EntityBase {

    private final int SPEED = 3;// 歩くスピード
    private int gravRot = 0;// 重力の方向 0:下方向 1:右方向 2:上方向 3:左方向
    private boolean rushing = false;// 突進中かどうか(突進中は落ちるのが早い & 坂で重力の方向が変わる)
    private int xsize = 10;// 横幅
    private int ysize = 16;// 縦幅(横幅以上にしないと坂で正常に跳ね返らない)

    /**
     * コンストラクタ
     * 
     * @param x
     * @param y
     */
    Player(int x, int y) {
        super(x, y);

        // 自分をプレイヤーキャラとして登録
        EntityBase.setPlayer(this);

        //パーツ設定
        Parts[] parts = { new Parts(this, 0, 0, xsize, ysize, Color.RED) };
        setParts(parts);
    }


    /**
     * その方向に移動
     * 
     * @param dir 移動方向
     *            true = 右,下
     *            false = 左,上
     */
    void walk(boolean dir) {
        boolean gV = gravRot % 2 == 0;// 重力が縦方向かどうか
        int dist = dir ? xsize : -1;// 一つ歩いた先
        int foot = gravRot <= 1 ? ysize - 1 : 0;// 足元
        Outer: for (int i = 0; i < SPEED; i++) {

            // ---壁にぶつかる処理---//
            // 進んだ先で１番上(重力と逆方向の端)の部分がぶつかる場合は止まる
            if (ColChk.all(getStage(), getX() + (gV ? dist : (gravRot == 1 ? 0 : ysize - 1)),
                    getY() + (gV ? (gravRot == 0 ? 0 : ysize - 1) : dist)) >= 0) {
                break;
            }

            // ---坂を上る処理---//
            // １つ歩いた先の足元(重力方向の端)に坂があるなら
            if (ColChk.all(getStage(), getX() + (gV ? dist : foot), getY() + (gV ? foot : dist)) >= 0) {
                while (ColChk.all(getStage(), getX() + (gV ? dist : foot), getY() + (gV ? foot : dist)) >= 0) {
                    // 天井に頭がついていないのであれば(プレイヤーの重力の逆方向に１つ進んだ先に壁等がないなら)
                    if (ColChk.all(getStage(), getX() + (gV ? (dir ? xsize - 1 : 0) : (gravRot == 1 ? -1 : ysize)),
                            getY() + (gV ? (gravRot == 0 ? -1 : ysize) : (dir ? xsize - 1 : 0))) < 0) {
                        // 重力の逆方向に１つ移動
                        move(true, (!gV ? (gravRot <= 1 ? -1 : 1) : 0), (gV ? (gravRot <= 1 ? -1 : 1) : 0));
                    }
                    // 天井に頭がついているなら
                    else {
                        // 移動処理を終了
                        break Outer;
                    }
                }
            }
            // ---坂を下る処理---//
            else {
                // 移動する先で着地していないなら
                if (chkGround(dir ? 1 : -1) <= 0) {
                    // 重力方向に１つ移動
                    move(true, (!gV ? (gravRot <= 1 ? 1 : -1) : 0), (gV ? (gravRot <= 1 ? 1 : -1) : 0));
                }
            }
            // ---実際に移動する---//
            move(true, (gV ? (dir ? 1 : -1) : 0), (!gV ? (dir ? 1 : -1) : 0));

            // ---下に足場がなくなっても終了---//
            if (chkGround() <= 0) {
                break;
            }
        }
    }


    /**
     * 重力を回転させる
     * 
     * @param rot 回転量
     */
    void rotGrav(int rot) {
        boolean rot90 = Math.abs(rot) % 2 == 1;// 回転量が90度(または270度)ならtrueになる
        int shift = Math.abs(ysize - xsize);// 位置調整時の移動量

        // 位置調整その１
        if (rot90) {
            if (gravRot == 0) {
                move(true, 0, shift);
            }

            if (gravRot == 1) {
                move(true, shift, 0);
            }
        }
        // 実際に回転させる
        gravRot = (gravRot + (rot % 4) + 4) % 4;

        // 位置調整その２
        if (rot90) {
            if (gravRot == 3) {
                move(true, -shift, 0);
            }

            if (gravRot == 2) {
                move(true, 0, -shift);
            }
        }

        // プレイヤーのサイズを変える
        getPartsInd(0).setSize(gravRot % 2 == 0 ? xsize : ysize, gravRot % 2 == 1 ? xsize : ysize);
    }


    /**
     * 重力方向を取得
     * 
     * @return 重力方向
     */
    int getGrav() {
        return gravRot;
    }


    /**
     * 重力方向を設定しながらその方向に突進する
     * 
     * @param rot 重力方向
     */
    void setGrav(int rot) {
        // 現在の重力方向と同じ向きでなければ
        if (gravRot != rot) {
            // その方向に設定し、突撃開始
            gravRot = rot;
            rushing = true;
        }
    }

    /**
     * 着地しているかどうか確認する
     * 
     * @param exX 判定をx方向にずらす距離
     * @param exY 判定をy方向にずらす距離
     * @return 着地しているならtrue
     */
    int chkGround(int exX, int exY) {
        int rot = gravRot % 2;// 重力が縦か横か(0 = 縦, 1 = 横)
        int foot = (gravRot <= 1 ? ysize : -1);// 足元をどこに設定するか
        int chkFoot = -1;// 足元に足場があったかどうか

        for (int i = 0; i < xsize; i++) {

            chkFoot = ColChk.all(getStage(), getX() + exX + (rot == 0 ? i : foot),
                    getY() + exY + (rot == 0 ? foot : i));

            if (chkFoot > 0) {
                return chkFoot;
            }

        }
        return chkFoot;
    }

    /**
     * 着地しているかどうか確認する(ずらす距離を省略)
     * 
     * @return 着地しているならtrue
     */
    int chkGround() {
        return chkGround(0, 0);
    }

    /**
     * 着地しているかどうか確認する(指定した距離歩いた場合で判定)
     * 
     * @return 着地しているならtrue
     */
    int chkGround(int dir) {

        if (gravRot % 2 == 0) {
            return chkGround(dir, 0);
        } else {
            return chkGround(0, dir);
        }

    }

    /**
     * 横幅を取得
     * 
     * @return 横幅
     */
    int getXsize() {
        return xsize;
    }

    /**
     * 縦幅を取得
     * 
     * @return 縦幅
     */
    int getYsize() {
        return ysize;
    }

    /**
     * 突進状態を取得
     * 
     * @return 突進状態
     */
    boolean getRushing() {
        return rushing;
    }

    /**
     * 突進状態の設定
     * 
     * @param rev 突進状態
     */
    void setRushing(boolean rev) {
        rushing = rev;
    }

    /**
     * 落下する
     * 
     * @param speed 落下スピード
     */
    void fall(int speed) {
        // スピード分繰り返す
        for (int i = 0; i < speed; i++) {
            // 着地したら終了
            if (chkGround() > 0) {
                break;
            }
            // 重力方向に移動していく
            if (gravRot % 2 == 0) {
                move(true, 0, (gravRot == 0 ? 1 : -1));
            } else {
                move(true, (gravRot == 1 ? 1 : -1), 0);
            }

        }
    }

    @Override
    public void action() {
        // 衝突イベントで当たった場合のアクション(使わないが、インターフェイスの仕様上書かないと動かない)
    }
}