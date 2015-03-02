package jp.egaonohon.database.list1;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.Toast;

/*
 * こちらでは、一覧をリスト表示にしている
 * リストビュー部分のみが変わっている。
 */
public class MainActivity extends Activity {
	private SimpleDatabaseHelper helper = null;

	private LinearLayout base = null;
	private TableLayout tl = null;
	private EditText txtIsbn = null;
	private EditText txtTitle = null;
	private EditText txtPrice = null;

	// ここからがDatabaseBasic1との違い!
	private ListView lv;
	// ListViewでの追加メンバー変数
	// リスト表示のためのアダプタ。仲介役の【アダプター】。
	private ArrayAdapter<String> adapter = null;
	// リストビューの参照変数。元データが必要。dblistがそれに相当。可変長のデータ。もう一つ必要なのが間に入るAdapter。
	// なぜAdapterか。複数のデータを扱う部品は他にもある。コンボボックス(選択するとビュッと選択しが表示されるやつ）など。
	// だから、Adapterをセットする。そうすればUI側がコンボボックスになろうがリストビューだろうが変化を吸収できる。
	// 一行分のフォーマットを決めるのがAdapterの役目。

	// dblistは、1レコードを1つのString型のインスタンスにまとめて、ArrayList化したもの。これが【元データ】。
	private ArrayList<String> dblist = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		base = (LinearLayout) findViewById(R.id.base);
		tl = (TableLayout) findViewById(R.id.tl);
		txtIsbn = (EditText) findViewById(R.id.txtIsbtn);
		txtTitle = (EditText) findViewById(R.id.txtTitle);
		txtPrice = (EditText) findViewById(R.id.txtPrice);
		// リストビューの参照を取得
		lv = (ListView) findViewById(R.id.listView1);
		// リストビューのアダプタを作成。リストビューの一行の構造をここで決定している。
		// 【複数データを扱うときにはAdapterを作る】とおぼえておく!
		// 第一引数はコンテキスト。第2引数が一行のフォーマット。一般的にはR.のレイアウトを指定。第3引数が元データのアレイリスト。
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dblist);
		// リストにひも付。ここでAdapterをリストビューの参照にsetAdapter()メソッドでセットしている。
		lv.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		// 画面が占有する直前のタイミング
		super.onResume();
		// データベースのオープン処理
		helper = new SimpleDatabaseHelper(this);
		// 全レコード取得。複数のデータを呼び出してアレイリストにセット。
		dblist = getAllRecord();
		// アダプタに元データが更新した事を伝えるのがnotifyDataSetChanged()の役目。
		// そうしないとリストビューに何も表示されないままになってしまう。
		// リストビューにデータが反映される(表示が更新される）。
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onPause() {
		// 画面の占有が解除される瞬間
		super.onPause();
		// データベースのクローズ処理。画面が非表示になったらクローズする。
		helper.close();
	}

	/*
	 * 全レコードの取得ーー＞リストビューの元データに設定する 前回は1件だけを呼び出していたが今度は全件を呼び出すのが違い。
	 */
	public ArrayList<String> getAllRecord() {
		SQLiteDatabase db = helper.getReadableDatabase();
		// 元データのArrayListの中身をクリアする。そうしないと呼び出すたびにどんどん同じものが増えていってしまう。
		dblist.clear();
		// Ｓｅｌｅｃｔ文の「＊」に相当する
		String[] cols = { "isbn", "title", "price" };
		// ｗｈｅｒｅ句に相当する部分は無（null）とする
		// 【Select * books;】<=のＳＱＬと同じ事をする
		// 結果はCursor（カーソル）でアクセスできる
		// query()でデータを引っ張ってくる。
		// 第7引数は、並び順をISBN順に指定している。したがって新規追加データもISBNが小さければ上に来る。
		Cursor cs = db.query("books", cols, null, null, null, null, "isbn",
				null);
		String msg = "";
		Toast.makeText(this, "登録件数" + cs.getCount() + " 件", Toast.LENGTH_SHORT).show();
		while (cs.moveToNext()) { // 今回はwhile文で先頭に持って行きながら回していく。何件あるかわからないから。
			// データがあれば、取得する
			msg = cs.getString(0);
			msg += cs.getString(1); // 引っ張り終えたらmsgに追加している。
			msg += cs.getInt(2);
			// アレーリストに追加する
			dblist.add(msg);
		}
		cs.close();// 最後までいったらカーサーもDBもクローズする。
		db.close();
		// アダプタに元データが更新した事を伝えるnotifyDataSetChanged()
		// リストビューにデータが反映される
		adapter.notifyDataSetChanged();
		return dblist;
	}

	public void onSave(View view) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("isbn", txtIsbn.getText().toString());
		cv.put("title", txtTitle.getText().toString());
		cv.put("price", txtPrice.getText().toString());
		db.insert("books", null, cv);
		Toast.makeText(this, "繝�繝ｼ繧ｿ縺ｮ逋ｻ骭ｲ縺ｫ謌仙粥縺励∪縺励◆縲�", Toast.LENGTH_SHORT)
				.show();
		db.close();// ちゃんとDBをクローズしましょう。そうしないとメモリリーク状態になってしまう。
		dblist = getAllRecord();// 全件を読み直して更新をかける。
	}

	public void onDelete(View view) {
		String[] params = { txtIsbn.getText().toString() };
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("books", "isbn = ?", params);
		Toast.makeText(this, "繝�繝ｼ繧ｿ縺ｮ蜑企勁縺ｫ謌仙粥縺励∪縺励◆縲�", Toast.LENGTH_SHORT)
				.show();
		db.close();// ちゃんとDBをクローズしましょう。そうしないとメモリリーク状態になってしまう。
		dblist = getAllRecord();// 全件を読み直して更新をかける。
	}

	public void onSearch(View view) {
		SQLiteDatabase db = helper.getReadableDatabase();
		String[] cols = { "isbn", "title", "price" };
		String[] params = { txtIsbn.getText().toString() };
		Cursor cs = db.query("books", cols, "isbn = ?", params, null, null,
				null, null);
		if (cs.moveToFirst()) {
			txtTitle.setText(cs.getString(1));
			txtPrice.setText(cs.getString(2));
		} else {
			Toast.makeText(this, "繝�繝ｼ繧ｿ縺後≠繧翫∪縺帙ｓ縲�", Toast.LENGTH_SHORT)
					.show();
		}
		cs.close();// カーサーもきちんと閉じます。
		db.close();// ちゃんとDBをクローズしましょう。そうしないとメモリリーク状態になってしまう。
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
