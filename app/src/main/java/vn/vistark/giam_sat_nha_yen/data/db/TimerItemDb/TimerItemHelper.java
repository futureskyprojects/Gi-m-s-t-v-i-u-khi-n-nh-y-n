package vn.vistark.giam_sat_nha_yen.data.db.TimerItemDb;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import vn.vistark.giam_sat_nha_yen.data.db.modal.TimerItem;

/**
 * Project ĐK Nhà Yến
 * Created by Nguyễn Trọng Nghĩa on 10/19/2019.
 * Organization: Vistark Team
 * Email: dev.vistark@gmail.com
 */

public class TimerItemHelper extends SQLiteOpenHelper {
    private final static String TAG = TimerItemHelper.class.getSimpleName();
    private final static String DATABASE_NAME = "timer_item";
    private final static int DATABASE_VERSION = 1;

    // Các cột trong cấu trúc bảng
    private String id = "id";
    private String label = "label";
    private String port = "port";
    private String power = "power";
    private String state = "state";
    private String start = "start_time";
    private String end = "end_time";
    private String detail = "detail";

    public TimerItemHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Xây dựng mã tạo bảng
        String script = "CREATE TABLE " + DATABASE_NAME + "(" +
                id + " INTEGER PRIMARY KEY," +
                label + " TEXT," +
                port + " TEXT," +
                power + " INTEGER," +
                state + " INTEGER," +
                start + " TEXT," +
                end + " TEXT," +
                detail + " TEXT);";

        // Tiến hành khởi chạy mã
        db.execSQL(script);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // Xóa bản CSQL cũ đã có
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        // Tạo lại cơ sở dữ liệu mới
        onCreate(db);
    }

    public long addNewTimerItem(TimerItem timerItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(label, timerItem.getLabel());
        values.put(port, timerItem.getPort());
        values.put(power, timerItem.isPower() ? 1 : 0);
        values.put(state, timerItem.isState() ? 1 : 0);
        values.put(start, timerItem.getStart());
        values.put(end, timerItem.getEnd());
        values.put(detail, timerItem.getDetail());

        // Chèn dữ liệu vào cơ sơ dữ liệu
        long res = db.insert(DATABASE_NAME, null, values);

        // Đóng kết nối với cơ sở dữ liệu
        db.close();

        return res;
    }

    public List<TimerItem> getAllTimerItem() {
        List<TimerItem> timerItems = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + DATABASE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(selectQuery, null);

        // Duyệt trên từng con trỏ
        if (cursor.moveToFirst()) {
            do {
                // Nhận và lưu tạm
                TimerItem timerItem = new TimerItem(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7)
                );

                // Lưu vào danh sách
                timerItems.add(timerItem);
            } while (cursor.moveToNext());
        }

        // Trả về kết quả
        return timerItems;
    }

    public TimerItem getTimerItem(int timerItemId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DATABASE_NAME, new String[]{label, port, power, state, start, end, detail}, id + "=?", new String[]{
                String.valueOf(timerItemId)
        }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // Nhận và lưu tạm
        TimerItem timerItem = new TimerItem(
                cursor.getString(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getString(6),
                cursor.getString(7)
        );

        // Trả về kết quả
        return timerItem;
    }

    public int countAll() {
        String countQuery = "SELECT  * FROM " + DATABASE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();

        cursor.close();

        // return count
        return count;
    }

    public long updateTimerItem(TimerItem timerItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(label, timerItem.getLabel());
        values.put(port, timerItem.getPort());
        values.put(power, timerItem.isPower() ? 1 : 0);
        values.put(state, timerItem.isState() ? 1 : 0);
        values.put(start, timerItem.getStart());
        values.put(end, timerItem.getEnd());
        values.put(detail, timerItem.getDetail());

        // Chèn dữ liệu vào cơ sơ dữ liệu
        long res = db.update(DATABASE_NAME, values, id + "=?", new String[]{String.valueOf(timerItem.getId())});

        // Đóng kết nối với cơ sở dữ liệu
        db.close();

        return res;
    }

    public long deleteTimerItem(TimerItem timerItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        long res = db.delete(DATABASE_NAME, id + "=?", new String[]{String.valueOf(timerItem.getId())});
        db.close();
        return res;
    }
}
