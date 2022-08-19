import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.IOException;
// import java.io.Writer;
import java.util.ArrayList;

//ステージファイル読み書きクラス

public class StageIO {
    static FileWriter fWrite;

    /**
     * ステージデータ読み込み
     * 
     * @param name ファイル名
     * @return ステージデータ
     */
    static ArrayList<String> load(File file) {
        try {

            BufferedReader loader = new BufferedReader(new FileReader(file));

            // ---ファイルからステージデータを読み込む---//
            // 結果を格納する文字列型リストを用意
            ArrayList<String> result = new ArrayList<>();

            String line;
            // 1行ずつ読み込んでステージデータに格納
            try {
                while (true) {
                    // ファイルから１行読み込む
                    line = loader.readLine();
                    // "end"に当たるかファイルを最後まで調べると終了
                    if (line.indexOf("end") != -1 || line == null) {
                        break;
                    }
                    // 読み込んだ値をステージデータに追加
                    result.add(line);
                }
            } catch (IOException e) {
                // 何かしらのエラーが起こった場合(書かないと動かない)
                System.out.println("エラー！");
            }

            loader.close();
            return result;

        } catch (IOException e) {
            // ステージファイルが見つからない場合、エラーメッセージを表示しnullを返す
            System.out.println("ステージファイルが見つかりません");
            return null;
        }

    }

    /**
     * ステージファイル書き込み
     * 
     * @param file  書き出し先ファイル
     * @param stage 書き出すステージデータ
     */
    static void save(File file, ArrayList<String> stage) {
        try {
            // ファイル書き込み用インスタンスを用意
            fWrite = new FileWriter(file);

            // １行ずつ保存していく
            for (int i = 0; i < stage.size(); i++) {
                fWrite.write(stage.get(i) + "\n");
                fWrite.flush();
            }
            // 最後に"end"という行を追加
            fWrite.write("end");
            fWrite.flush();
        } catch (IOException e) {
            // 何かしらのエラーが起きた(書かないと動かない)
        }

    }

}
