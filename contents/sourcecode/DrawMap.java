import javax.swing.JPanel;
import java.awt.Graphics;
import java.util.ArrayList;
import java.awt.Color;

public class DrawMap extends JPanel {

    /**
     * マップ全体を表示表示
     * 
     * @param stage    描写するステージデータ
     * @param editmode エディタ専用パーツを表示するかどうか
     * @param g        グラフィッグコンポーネント
     * @param size     表示サイズ
     */
    static void run(ArrayList<String> stage, Boolean editmode, Graphics g, double size) {
        // 探索して取り出した文字を格納する変数
        char tile;
        // マップサイズ
        int mapx = stage.get(0).length();
        int mapy = stage.size();

        // １マスずつ探索して描写していく
        for (int i = 0; i < mapy; i++) {
            for (int j = 0; j < mapx; j++) {
                tile = stage.get(i).charAt(j);

                drawParts((int) Math.floor(j * size), (int) Math.floor(i * size), tile, (int) Math.floor(size),
                        editmode, g);
            }
        }

    }

    /**
     * パーツ(マップチップ)を描写する
     * 
     * @param x        設置するx座標(1px単位)
     * @param y        設置するy座標(1px単位)
     * @param var      描写するマップチップ
     * @param size     描写するサイズ
     * @param editmode レベルエディタ専用のタイルを表示するかどうか
     * @param g        グラフィッグコンポーネント
     */
    static void drawParts(int x, int y, char var, int size, Boolean editmode, Graphics g) {
        int iVar = (int) var - 48;
        String stVar = String.valueOf(var);
        g.setColor(Color.BLUE);
        Double pixel = (double)size / 16;

        // プレイヤーキャラ(エディタモードのみで表示)
        if (iVar == 1 && editmode) {
            g.setColor(Color.RED);
            g.fillRect(x + (int)(pixel * 5), y + (int)(pixel * 8), (int)(pixel * 6), (int)(pixel * 8));
        }

        
        // 壁・床
        else if (iVar == 2) {
            g.fillRect(x, y, size, size);
        }

        // 坂
        else if (3 <= iVar && iVar <= 6) {
            // 頂点の設定

            // 坂の頂点を格納する配列
            int[] polyx = new int[3];
            int[] polyy = new int[3];
            

            //---x座標の設定---//
            // 坂(左)
            if ("35".contains(stVar)) {
                polyx[0] = x;
                polyx[1] = x + size - 1;
                polyx[2] = x;

            }
            // 坂(右)
            else if ("46".contains(stVar)) {
                polyx[0] = x + size - 1;
                polyx[1] = x;
                polyx[2] = x + size - 1;

            }

            //---y座標の設定---//
            // 坂(上)
            if ("56".contains(stVar)) {
                polyy[0] = y + size - 1;
                polyy[1] = y;
                polyy[2] = y;

            }
            // 坂(下)
            else if ("34".contains(stVar)) {
                polyy[0] = y;
                polyy[1] = y + size - 1;
                polyy[2] = y + size - 1;

            }
            // 描写する
            g.drawPolygon(polyx, polyy, 3);
            g.fillPolygon(polyx, polyy, 3);

        }
        // スイッチ(エディタモードのみで表示)
        else if (7 <= iVar && iVar <= 10 && editmode) {
            int[] base = new int[4];
            int[] button = new int[4];
            

            // 縦方向
            if (iVar <= 8) {
                // ベース部分(灰色のところ)のサイズと座標の共通部分を設定
                base[0] = 0;
                base[2] = size;
                base[3] = (int)(pixel * 2);
                // ボタン部分(赤色のところ)のサイズと座標の共通部分を設定
                button[0] = size / 16;
                button[2] = (int)(pixel * 14);
                button[3] = (int)(pixel * 6);

                // 上方向
                if (iVar == 7) {
                    base[1] = (int)(pixel * 14);
                    button[1] = (int)(pixel * 10);
                }

                // 下方向
                if (iVar == 8) {
                    base[1] = 0;
                    button[1] = 0;
                }
            }

            // 横方向
            else {
                // ベース部分(灰色のところ)のサイズと座標の共通部分を設定
                base[1] = 0;
                base[2] = (int)(pixel * 2);
                base[3] = size;
                // ボタン部分(赤色のところ)のサイズと座標の共通部分を設定
                button[1] = size / 16;
                button[2] = (int)(pixel * 6);
                button[3] = (int)(pixel * 14);

                // 右方向
                if (iVar == 9) {
                    base[0] = 0;
                    button[0] = 0;
                }

                // 左方向
                if (iVar == 10) {
                    base[0] = (int)(pixel * 14);
                    button[0] = (int)(pixel * 10);
                }
            }

            g.setColor(Color.RED);
            g.fillRect(x + button[0], y + button[1], button[2], button[3]);
            g.setColor(Color.GRAY);
            g.fillRect(x + base[0], y + base[1], base[2], base[3]);
        }

        // ドア(エディタモードのみで表示)
        else if (iVar == 11 && editmode) {
            g.setColor(Color.ORANGE);
            g.fillRect(x, y, size, size);
        }
    }
}
