
abstract class Common {

    private Common() {
    }

    final static public int GRID = 32;// タイル等の大きさ
    static private double screenSize = 0.75;// 拡大率(1で等倍)

    /**
     * タイルの大きさに拡大率を掛けた数を返す
     * 
     * @return タイル等の大きさ * 拡大率
     */
    static int fix() {
        return (int) (GRID * screenSize);
    }

    static double getsize() {
        return screenSize;
    }
}