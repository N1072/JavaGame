import java.util.*;

class MapEdit implements Cloneable {
    private int xSize; // マップの横サイズ
    private int ySize; // マップの縦サイズ
    private ArrayList<String> mapData; // マップデータ

    /**
     * コンストラクタ
     * 
     * @param xSize 横方向のマップサイズ
     * @param ySize 縦方向のマップサイズ
     * 
     * @param bg    デフォルトのマップチップ(最初はこのマップチップで埋められている)
     */
    MapEdit(int xSize, int ySize, String bg) {
        ArrayList<String> mapData = new ArrayList<String>();
        this.mapData = mapData;
        this.xSize = xSize;
        this.ySize = ySize;

        clear(bg);
    }

    MapEdit(ArrayList<String> map) {
        importMap(map);
    }

    /**
     * マップデータのクリア(指定したマップチップで埋めつくす)
     * 
     * @param var 使用するマップチップ
     */
    void clear(String var) {
        // まずマップデータを空っぽにする
        mapData.clear();

        // 埋めつくすための１行の文字列を格納するための変数を用意する
        String slice = "";

        // マップチップをマップの横サイズ分繰り替えした文字列を作成する
        for (int i = 0; i < xSize; i++) {
            slice += var;
        }

        // 作成した文字列をマップの縦サイズ分格納する
        for (int i = 0; i < ySize; i++) {
            mapData.add(slice);
        }
    }

    /**
     * マップデータの表示
     */
    void view() {
        for (String i : mapData) {
            System.out.println(i);
        }
    }

    /**
     * マップサイズの取得
     * 
     * @return [横サイズ, 縦サイズ]
     */
    int[] getSize() {
        int[] rslt = { xSize, ySize };
        return rslt;
    }

    /**
     * マップサイズの変更
     * 
     * @param x    新しい横サイズ
     * @param y    新しい縦サイズ
     * @param var  余白部分のマップチップ(拡大した際に使用)
     * @param mode モード
     *             False = 絶対モード(指定したサイズに直接設定)
     *             True = 相対モード(現在のサイズから指定しただけ拡大・縮小)
     */
    int[] setSize(int x, int y, String var, boolean mode) {
        int[] result = new int[2];

        // 縦サイズが小さくなる場合の処理
        if (mode == true ? y < 0 : y < ySize) {
            while (mapData.size() != (mode == true ? ySize + y : y)) {
                mapData.remove(mode == true ? ySize + y : y);
            }
        }

        // 横サイズが大きくなる場合の処理
        if (mode == true ? x > 0 : x > xSize) {
            // 余白部分を生成
            String mgn = "";
            for (int i = 1; i <= (mode == true ? x : x - xSize); i++) {
                mgn += var;
            }

            // 既存のマップに追加
            for (int i = 0; i < mapData.size(); i++) {
                mapData.set(i, mapData.get(i) + mgn);
            }
        }

        // 横サイズが小さくなる場合の処理
        if (mode == true ? x < 0 : x < xSize) {
            // 既存のマップを削っていく
            for (int i = 0; i < mapData.size(); i++) {
                mapData.set(i, mapData.get(i).substring(0, mode == true ? xSize + x : x));
            }
        }

        xSize = x + (mode == true ? xSize : 0);

        // 縦サイズが大きくなる場合の処理
        if (mode == true ? y > 0 : y > ySize) {
            // 余白部分を生成
            String mgn = "";
            for (int i = 1; i <= xSize; i++) {
                mgn += var;
            }

            // 既存のマップに追加
            while (mapData.size() != (mode == true ? ySize + y : y)) {
                mapData.add(mgn);
            }
        }

        ySize = y + (mode == true ? ySize : 0);
        return result;
    }

    /**
     * マップチップの設置
     * 
     * @param x   設置するx座標
     * @param y   設置するy座標
     * @param var 設置するマップチップ
     */
    void put(int x, int y, char var) {
        mapData.set(y, mapData.get(y).substring(0, x) + String.valueOf(var) + mapData.get(y).substring(x + 1));
    }

    /**
     * 指定座標のマップチップを取得
     * 
     * @param x x座標
     * @param y y座標
     * @return そこにあるマップチップ
     */
    char get(int x, int y) {
        return mapData.get(y).charAt(x);
    }

    /**
     * マップをインポートする(文字列型リストをマップデータとして読み込む)
     * 
     * @param map 読み込むデータ
     */
    void importMap(ArrayList<String> map) {
        mapData = new ArrayList<String>(map);
        xSize = map.get(0).length();
        ySize = map.size();
    }

    /**
     * マップをエクスポートする(マップデータを文字列型リストとして返す)
     * 
     * @return マップデータ
     */
    ArrayList<String> exportMap() {
        return new ArrayList<String>(mapData);
    }

}
