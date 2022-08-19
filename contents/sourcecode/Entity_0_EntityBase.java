
import java.awt.Graphics;
// import java.awt.Color;

import java.util.ArrayList;
import java.util.List;

// プレイヤーやボタンなどエンティティのベースとなる抽象クラス
abstract class EntityBase implements EntityInter {

    private int x;// x座標
    private int y;// y座標
    private Parts[] parts;// 自分を構成するパーツ

    static private List<EntityBase> entities = new ArrayList<>();// 生成したエンティティを格納したリスト
    static private ArrayList<String> stage;// 参照するステージデータ

    static private Player player;// プレイヤー

    /**
     * コンストラクタ
     * 
     * @param x x初期位置
     * @param y y初期位置
     */
    EntityBase(int x, int y) {
        this.x = x;
        this.y = y;

        // エンティティリストに自分を追加
        entities.add(this);
    }

    /**
     * エンティティリストのリセット
     */
    static void reset() {
        entities.clear();
    }

    /**
     * 使用するステージデータを設定
     * 
     * @param stage ステージデータ
     */
    public static void setStage(ArrayList<String> stage) {
        EntityBase.stage = stage;
    }

    public static ArrayList<String> getStage() {
        return stage;
    }

    /**
     * 画面に描写する
     * 
     * @param g
     */
    void draw(Graphics g) {
        // パーツごとに描写処理
        for (int i = 0; i < parts.length; i++) {
            try {
                parts[i].draw(g);
            } catch (NullPointerException e) {

            }
        }
    }

    boolean contains(int x, int y) {
        // パーツごとに衝突判定確認
        for (int i = 0; i < parts.length; i++) {
            try {
                if (parts[i].chkCol(x, y)) {
                    return true;
                }
            } catch (NullPointerException e) {

            }

        }
        return false;
    }

    /**
     * 指定の位置に移動
     * 
     * @param mode モード
     *             False = 絶対モード(指定した座標に直接移動)
     *             True = 相対モード(現在地から指定しただけ移動)
     * @param x    x座標
     * @param y    y座標
     */
    void move(boolean mode, int x, int y) {
        this.x = (mode ? this.x : 0) + x;
        this.y = (mode ? this.y : 0) + y;
        for (Parts i : parts) {
            i.rectmove();
        }
    }

    /**
     * x座標の取得
     * 
     * @return x座標
     */
    protected int getX() {
        return x;
    }

    /**
     * y座標の取得
     * 
     * @return y座標
     */
    protected int getY() {
        return y;
    }

    /**
     * エンティティリストの取得
     * 
     * @return エンティティリスト
     */
    public static List<EntityBase> getEntities() {
        return new ArrayList<>(entities);

    }

    /**
     * プレイヤーインスタンスへの参照先取得
     * 
     * @return プレイヤーインスタンスへの参照先取得
     */
    protected static Player getPlayer() {
        return player;
    }

    /**
     * パーツの設定
     * 
     * @param parts 設定するパーツ
     */
    protected void setParts(Parts[] parts) {
        this.parts = parts;
    }

    /**
     * プレイヤーインスタンスへの参照先設定
     * 
     * @param var 設定する値
     */
    protected static void setPlayer(Player var) {
        player = var;
    }

    /**
     * 座標をタイルの大きさに合わせたものに修正する(描写用)
     * 
     * @param x x座標
     * @param y y座標
     * @return {修正したx座標, 修正したy座標}
     */
    static int[] fix(int x, int y) {
        int[] result = { (int) (Common.GRID), (int) ((double) y / 32 * Common.GRID) };
        return result;
    }

    /**
     * 指定インデックス番号のパーツを取得
     * 
     * @param ind インデックス番号
     * @return そのインデックス番号にあるパーツ
     */
    Parts getPartsInd(int ind) {
        return parts[ind];
    }

    void setPartsInd(int ind, Parts var) {
        parts[ind] = var;
    }

    /**
     * 全てのエンティティを画面に描写する
     * 
     * @param g グラフィッグコンポーネント
     */
    static void drawAll(Graphics g) {
        for (int i = 0; i < entities.size(); i++) {
            entities.get(i).draw(g);
        }
    }
}
