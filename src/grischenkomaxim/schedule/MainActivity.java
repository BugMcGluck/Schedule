package grischenkomaxim.schedule;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements OnClickListener {

	DBHelper dbHelper;
	SQLiteDatabase db;
	List<Schedule> schedules = new ArrayList<MainActivity.Schedule>();

	/* (non-Javadoc)
	 * @see android.support.v7.app.ActionBarActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);



		dbHelper = new DBHelper(this);
		// подключаемся к базе
//		db = dbHelper.getWritableDatabase();
//		String sqlQuery = "select class.fullname as ClassFullName, "
//				+ "class.shortname as ClassShortName, "
////				+ "room.name as RoomName, "
////				+ "building.name as BuidingName, building.geo as BuildingGeo, "
////				+ "building.photo as BuildingPhoto, "
//				+ "task_type.fullname as Task_typeFullName, "
//				+ "task_type.shortname as Task_typeShortName, task.fullname as TaskFullName, "
//				+ "task.shortname as TaskShortName, task_time.starttime as Task_timeStartTime, "
//				+ "task_time.endtime as Task_timeEndTime, task_time.description as Task_timeDescription, "
//				+ "post.fullname as PostFullName, post.shortname as PostShortName, "
//				+ "teacher.firstname as TeacherFirstName, teacher.lastname as TeacherLastName, "
//				+ "teacher.middlename as TeacherMiddleName, teacher.photo as TeacherPhoto, "
//				+ "schedule.day as ScheduleDay "
//				+ "from schedule "
//				+ "inner join task on schedule.task_id = task.id "
////				+ "inner join room on schedule.room_id = room.id "
//				+ "inner join teacher on schedule.teacher_id = teacher.id "
//				+ "inner join class on schedule.class_id = class.id "
//				+ "inner join task_time on schedule.task_time_id = task_time.id "
////				+ "inner join building on room.building_id = building.id "
//				+ "inner join task_type on task.task_type_id = task_type.id "
//				+ "inner join post on teacher.post_id = post.id "
//				+ "where schedule.is_active = 1"
//				+ "and schedule.day = ?";
		Cursor c = null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", new Locale("ru"));
		GregorianCalendar calendar = new GregorianCalendar();
		Date date = calendar.getTime();
		Log.d("!!!DATE", date.toString());
//		c = db.rawQuery(sqlQuery, new String[] {simpleDateFormat.format(new GregorianCalendar().getTime())});
		Date maxDate = getScheduleLastDate(dbHelper);
		Log.d("!!!MAXDATE", date.toString());
		do {
			Log.d("!!!REQUESTDATE", simpleDateFormat.format(date));
			c = getCursorByDate(simpleDateFormat.format(date), dbHelper);
			Log.d("!!!CURSOR", String.valueOf(c.getCount()));
			calendar.add(Calendar.DATE, 1);
			date = calendar.getTime();
			Log.d("!!!DATE", date.toString());
		} while (c == null || c.getCount() == 0 || !date.after(maxDate));
		
		if (c != null) {
			if (c.moveToFirst()) {
				do {
					Schedule sh = new Schedule();
/*					sh.new  Schedule.Teacher(c.getString(c.getColumnIndex("TeacherLastName")), 
							c.getString(c.getColumnIndex("TeacherFirstName")), 
							c.getString(c.getColumnIndex("TeacherMiddleName")), 
							null, 
							null);*/
					sh.clas.fullName = c.getString(c
							.getColumnIndex("ClassFullName"));
					sh.clas.shortName = c.getString(c
							.getColumnIndex("ClassShortName"));
//					sh.room.name = c.getString(c.getColumnIndex("RoomName"));
//					sh.room.building.name = c.getString(c
//							.getColumnIndex("BuidingName"));
//					sh.room.building.geo = c.getString(c
//							.getColumnIndex("BuildingGeo"));

					sh.task.fullName = c.getString(c
							.getColumnIndex("TaskFullName"));
					sh.task.shortName = c.getString(c
							.getColumnIndex("TaskShortName"));
					sh.task.task_type.fullName = c.getString(c
							.getColumnIndex("Task_typeFullName"));
					sh.task.task_type.shortName = c.getString(c
							.getColumnIndex("Task_typeShortName"));
					sh.teacher.firstName = c.getString(c
							.getColumnIndex("TeacherFirstName"));
					sh.teacher.lastName = c.getString(c
							.getColumnIndex("TeacherLastName"));
					sh.teacher.middleName = c.getString(c
							.getColumnIndex("TeacherMiddleName"));
					sh.teacher.post.fullName = c.getString(c
							.getColumnIndex("PostFullName"));
					sh.teacher.post.shortName = c.getString(c
							.getColumnIndex("PostShortName"));
					sh.task_time.startTime = c.getString(c
							.getColumnIndex("Task_timeStartTime"));
					sh.task_time.endTime = c.getString(c
							.getColumnIndex("Task_timeEndTime"));

					schedules.add(sh);
				} while (c.moveToNext());
			}
			c.close();
		}

		dbHelper.close();
		showList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

	private Cursor getCursorByDate(String date, DBHelper dbHelper){
		db = dbHelper.getWritableDatabase();
		String sqlQuery = "select class.fullname as ClassFullName, "
				+ "class.shortname as ClassShortName, "
//				+ "room.name as RoomName, "
//				+ "building.name as BuidingName, building.geo as BuildingGeo, "
//				+ "building.photo as BuildingPhoto, "
				+ "task_type.fullname as Task_typeFullName, "
				+ "task_type.shortname as Task_typeShortName, task.fullname as TaskFullName, "
				+ "task.shortname as TaskShortName, task_time.starttime as Task_timeStartTime, "
				+ "task_time.endtime as Task_timeEndTime, task_time.description as Task_timeDescription, "
				+ "post.fullname as PostFullName, post.shortname as PostShortName, "
				+ "teacher.firstname as TeacherFirstName, teacher.lastname as TeacherLastName, "
				+ "teacher.middlename as TeacherMiddleName, teacher.photo as TeacherPhoto, "
				+ "schedule.day as ScheduleDay "
				+ "from schedule "
				+ "inner join task on schedule.task_id = task.id "
//				+ "inner join room on schedule.room_id = room.id "
				+ "inner join teacher on schedule.teacher_id = teacher.id "
				+ "inner join class on schedule.class_id = class.id "
				+ "inner join task_time on schedule.task_time_id = task_time.id "
//				+ "inner join building on room.building_id = building.id "
				+ "inner join task_type on task.task_type_id = task_type.id "
				+ "inner join post on teacher.post_id = post.id "
				+ "where schedule.is_active = 1 "
				+ "and schedule.day = ?;";
		Cursor c = null;
		c = db.rawQuery(sqlQuery, new String[] {date});
		return c;
	}
	
	private Date getScheduleLastDate(DBHelper dbHelper) {
		db = dbHelper.getWritableDatabase();
		String sqlQuery = "select max(day) from schedule where is_active = 1;";
		Cursor c = null;
		c = db.rawQuery(sqlQuery, null);
		if (c != null){
			java.text.DateFormat df = java.text.DateFormat.getDateInstance(java.text.DateFormat.MEDIUM, new Locale("EN"));
			Date d = new Date();
			c.moveToFirst();
			Log.d("MAXDATE", c.getString(0));
			try {
				d = df.parse(c.getString(0));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return d;
		}
		return null;
	}
	
	private void showList() {
		LinearLayout linLayout = (LinearLayout) findViewById(R.id.linLayout);
		LayoutInflater ltInflater = getLayoutInflater();
		Boolean changeBackColor = false; 

		for (Schedule sh : schedules) {
			View item = ltInflater.inflate(R.layout.list_element, linLayout,false);
			TextView tv_task = (TextView) item.findViewById(R.id.textTask);
			TextView tv_teacher = (TextView) item.findViewById(R.id.textTeacher);
			TextView tv_room = (TextView) item.findViewById(R.id.textRoom);
			TextView tv_task_start_time = (TextView) item.findViewById(R.id.textTaskStartTime);
			TextView tv_task_end_time = (TextView) item.findViewById(R.id.textTaskEndTime);
//			Log.d("!", "!!!!!!!!!!!!!!!!");
//			Log.d("!", sh.task.shortName); 
			
			tv_task.setText(sh.task.shortName);
			tv_teacher.setText(sh.teacher.lastName);
			tv_room.setText(sh.room.name);
			tv_task_start_time.setText(sh.task_time.startTime);
			tv_task_end_time.setText(sh.task_time.endTime);
			if (changeBackColor)
			{
				item.setBackgroundColor(Color.LTGRAY);
			}
			changeBackColor = !changeBackColor;
			linLayout.addView(item);
		}
	}

	class DBHelper extends SQLiteOpenHelper {

		private static final String DB = "myDB";

		public DBHelper(Context context) {
			// конструктор суперкласса
			super(context, DB, null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			InputStream is = null;
			is = getResources().openRawResource(R.raw.makedb);
			byte[] buffer = new byte[10000];
			try {
				is.read(buffer);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String sql = new String(buffer);

			// создаем таблицу с полями
			db.beginTransaction();
			db.execSQL(sql);
			db.endTransaction();
			try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}
	}

	class Schedule {
		Task task = new Task();
		Teacher teacher =  new Teacher();
		Room room =  new Room();
		Class clas = new Class();
		Task_time task_time = new Task_time();
		Calendar day;

		class Teacher {
			String lastName;
			String firstName;
			String middleName;
			Post post =  new Post();
			Bitmap photo;
		}

		class Post {
			String fullName;
			String shortName;
		}

		class Task {
			Task_type task_type =  new Task_type();
			String fullName;
			String shortName;
		}

		class Task_type {
			String fullName;
			String shortName;
		}

		class Room {
			Building building = new Building();
			String name;
		}

		class Building {
			String name;
			String geo;
			Bitmap photo;
		}

		class Class {
			String fullName;
			String shortName;
		}

		class Task_time {
			String startTime;
			String endTime;
			String description;
		}
	}
}
