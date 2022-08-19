import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

class Door extends EntityBase {
    static List<Door> menber = new ArrayList<>();//全てのドアを格納するリスト

    /**
     * コンストラクタ
     * @param x x座標
     * @param y y座標
     */
    Door(int x, int y) {
        super(x, y);

        // パーツ指定
        Parts[] parts = { new Parts(this, 0, 0, Common.GRID, Common.GRID, Color.ORANGE) };
        setParts(parts);

        // リストに登録
        menber.add(this);
    }

    /**
     * ドアをオープンする
     */
    static void open() {
        System.out.println("ドアがオープン");
        
        // 全てのドアに空ける処理をする
        for (int i = 0; i < menber.size(); i++) {
            // ドアのパーツをnullにする
            menber.get(i).setPartsInd(0, null);
        }
    }

    @Override
    public void action() {
        // (使わないが、インターフェイスの仕様上書かないと動かない)
    }

}
