import java.util.ArrayList;
import java.util.List;

//当たり判定クラス
class ColChk {

    /**
     * 指定座標にてタイルと衝突するか調べる
     * 
     * @param stage ステージデータ
     * @param x     x座標
     * @param y     y座標
     * @return 衝突しているタイル
     *         -2 = エラー(ステージからはみ出している)
     *         -1 = 何もなし
     *         1 = 壁
     *         2 = 坂
     */
    static int tile(ArrayList<String> stage, int x, int y) {

        try {
            // 指定位置にあるタイルを調べる
            char tile = stage.get((int) y / Common.GRID).charAt((int) x / Common.GRID);

            // 壁の場合
            if (tile == '2') {
                // 衝突
                return 1;

            }
            // 坂の場合
            else if ('3' <= tile && tile <= '6') {
                // そのタイルの中での座標を求める
                int xTile = (int) x % Common.GRID;
                int yTile = (int) y % Common.GRID;

                // 左向きの坂ならタイル内x座標を反転
                if (tile == '4' || tile == '6') {
                    xTile = Common.GRID - 1 - xTile;
                }

                // 下向きの坂ならタイル内y座標を反転
                if (tile >= '5') {
                    yTile = Common.GRID - 1 - yTile;
                }

                // xがy以下なら衝突
                if (xTile <= yTile) {
                    return 2;
                }
            }
        }
        // マップからはみ出していた場合
        catch (IndexOutOfBoundsException e) {
            return -2;
        }
        // タイルがなかった場合
        return -1;
    }

    /**
     * 指定座標にてエンティティと衝突するか調べる
     * 
     * @param x x座標
     * @param y y座標
     * @return 衝突したかどうか
     */
    static boolean entity(int x, int y) {
        // エンティティリストを取得
        List<EntityBase> entities = EntityBase.getEntities();
        // エンティティ一つずつ確認
        for (int i = 0; i < entities.size(); i++) {
            // あたっているなら、そのエンティティの特殊アクションを起こして、trueを返す
            if (entities.get(i).contains(x, y)) {
                entities.get(i).action();
                return true;
            }
        }
        // あたらなかった場合
        return false;
    }

    /**
     * タイル・エンティティ両方と衝突確認
     * 
     * @param stage ステージデータ
     * @param x     x座標
     * @param y     y座標
     * @return エンティティに当たれば3,当たらなければtileの返り値
     */
    static int all(ArrayList<String> stage, int x, int y) {
        // エンティティとの当たり判定処理
        if (entity(x, y)) {
            return 3;
        }
        // タイルとの当たり判定処理
        return tile(stage, x, y);
    }
}
