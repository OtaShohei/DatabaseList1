package jp.egaono.database.list1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
 * こちらのクラスはDatabaseBasic1と特に変更はない。
 * パッケージ名が異なるので、データベース名が同じでも別物としてAndroidからは扱われる。
 */
public class SimpleDatabaseHelper extends SQLiteOpenHelper {
	static final private String DBNAME = "sample.sqlite";
	static final private int VERSION = 4;// ここが違うくらい。

	public SimpleDatabaseHelper(Context context) {
		super(context, DBNAME, null, VERSION);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE books ("
				+ "isbn TEXT PRIMARY KEY, title TEXT, price INTEGER)");
		db.execSQL("INSERT INTO books(isbn, title, price)"
				+ " VALUES('1:', 'Android1-', 100)");// 強いて言うなら、ISBNをシンプルにしたくらいがDatabaseBasic1との違い。
		db.execSQL("INSERT INTO books(isbn, title, price)"
				+ " VALUES('2:', 'Android2-', 200)");
		db.execSQL("INSERT INTO books(isbn, title, price)"
				+ " VALUES('3:', 'Android3-', 300)");
		db.execSQL("INSERT INTO books(isbn, title, price)"
				+ " VALUES('4:', 'Android4-', 400)");
		db.execSQL("INSERT INTO books(isbn, title, price)"
				+ " VALUES('5:', 'Android5-', 500)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int old_v, int new_v) {
		db.execSQL("DROP TABLE IF EXISTS books");
		onCreate(db);
	}
}
