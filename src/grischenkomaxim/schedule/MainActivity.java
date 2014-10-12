package grischenkomaxim.schedule;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import android.R.color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.content.Context;
import android.content.Intent;
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

public class MainActivity /* extends ActionBarActivity */extends FragmentActivity
		implements OnClickListener {

	DBHelper dbHelper;
	SQLiteDatabase db;
	public static List<Schedule> schedules = new ArrayList<Schedule>();
//	GregorianCalendar calendar = new GregorianCalendar();
//	Date date = calendar.getTime();
//	Date maxDate;
	public static final int[] colors = {
		Color.CYAN,
		Color.GREEN,
		Color.MAGENTA,
		Color.RED,
		Color.YELLOW,
		Color.DKGRAY,
		Color.LTGRAY
	};
	public static List<String> tasks = new ArrayList<String>();
	
	ViewPager pager;
	PagerAdapter pagerAdapter;
	int PAGE_COUNT = 5;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v7.app.ActionBarActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		dbHelper = new DBHelper(this);
//		maxDate = getScheduleLastDate();
		FillDates();
		FillTasks();
//		Log.d("!!!MAXDATE", maxDate.toString());
		
		pager = (ViewPager) findViewById(R.id.pager);
		Log.d("!!PAGER", pager.toString());
		pagerAdapter = new MyFragmentStatePagerAdapter(
				getSupportFragmentManager(), this);
		pager.setAdapter(pagerAdapter);

//		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
//				"yyyy-MM-dd", new Locale("ru"));
		GregorianCalendar cal = new GregorianCalendar();
		Date currentDate = new GregorianCalendar().getTime();
		cal.setTime(currentDate);
		cal.clear(GregorianCalendar.MILLISECOND);
		cal.clear(GregorianCalendar.SECOND);
		cal.clear(GregorianCalendar.MINUTE);
		cal.clear(GregorianCalendar.HOUR);
		cal.clear(GregorianCalendar.HOUR_OF_DAY);
		Log.d("!!!Calendar", cal.toString());
		currentDate = cal.getTime();
		
//		try {
//			currentDate = simpleDateFormat.parse(simpleDateFormat.format(currentDate));
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
		int startPosition = 0;
		for (int i = 0; i < schedules.size(); i++) {
			Log.d("!!!CurrentDate", currentDate.toString());
			Log.d("!!!ScheduleDate", schedules.get(i).date.toString());
			if (currentDate.compareTo(schedules.get(i).date) >= 0){
				if (currentDate.compareTo(schedules.get(i).date) == 0){
					startPosition = i;
				}else{
					startPosition = i + 1;
				}			
			} else {
				break;
			}
		}
		
		pager.setCurrentItem(startPosition);
//		pager.setOnPageChangeListener(new OnPageChangeListener() {
//
//			@Override
//			public void onPageSelected(int position) {
//
//			}
//
//			@Override
//			public void onPageScrolled(int position, float positionOffset,
//					int positionOffsetPixels) {
//			}
//
//			@Override
//			public void onPageScrollStateChanged(int state) {
//			}
//		});

		// readSchedule();
		// showList();
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if (arg2 == null) {return;}
		GregorianCalendar calendar = new GregorianCalendar(arg2.getIntExtra("selectedYear", 0), 
				arg2.getIntExtra("selectedMonth", 0), 
				arg2.getIntExtra("selectedDay", 0));
		Date selectedDate = calendar.getTime();
		Log.d("selectedDate", String.valueOf(arg2.getIntExtra("selectedDay", 0)) + "/" +
				String.valueOf(arg2.getIntExtra("selectedMonth", 0)) + "/" +
				String.valueOf(arg2.getIntExtra("selectedYear", 0)));
		Log.d("selectedDate", selectedDate.toString());
		int startPosition = 0;
		for (int i = 0; i < schedules.size(); i++) {
			if (selectedDate.compareTo(schedules.get(i).date) >= 0){
				if (selectedDate.compareTo(schedules.get(i).date) == 0){
					startPosition = i;
				}else{
					startPosition = i + 1;
				}			
			} else {
				break;
			}
		}
		pager.setCurrentItem(startPosition);	
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()){
		case R.id.action_settings:
			return true;
		case R.id.action_calendar:
			Intent intent =  new Intent(this, grischenkomaxim.schedule.Calendar.class);
			startActivityForResult(intent, 1);
			return true;
		default:
            return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

	private void readSchedule(Date d) {
		// dbHelper = new DBHelper(this);
		Cursor c = null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd",
				new Locale("ru"));
//		Log.d("!!!DATE", date.toString());

	
//		do {
//			date = calendar.getTime();
			Log.d("!!!REQUESTDATE", simpleDateFormat.format(d));
			c = getCursorByDate(simpleDateFormat.format(d), dbHelper);
			Log.d("!!!CURSOR", String.valueOf(c.getCount()));
//			calendar.add(Calendar.DATE, 1);
			// date = calendar.getTime();
			// Log.d("!!!DATE", date.toString());
//		} while (!d.after(maxDate));

		if (c != null) {
			int pos = 0;
			for (Schedule s : schedules) {
				if (s.date == d){
					pos = schedules.indexOf(s);
					break;
				}
			}
			if (c.moveToFirst()) {
				Schedule sh = schedules.get(pos);
				do {
					Item item = new Item();

					item.clas.fullName = c.getString(c
							.getColumnIndex("ClassFullName"));
					item.clas.shortName = c.getString(c
							.getColumnIndex("ClassShortName"));
					item.room.name = c.getString(c.getColumnIndex("RoomName"));
					item.room.building.name = c.getString(c
							.getColumnIndex("BuidingName"));
					item.room.building.geo = c.getString(c
							.getColumnIndex("BuildingGeo"));

					item.task.fullName = c.getString(c
							.getColumnIndex("TaskFullName"));
					item.task.shortName = c.getString(c
							.getColumnIndex("TaskShortName"));
					item.task.task_type.fullName = c.getString(c
							.getColumnIndex("Task_typeFullName"));
					item.task.task_type.shortName = c.getString(c
							.getColumnIndex("Task_typeShortName"));
					item.teacher.firstName = c.getString(c
							.getColumnIndex("TeacherFirstName"));
					item.teacher.lastName = c.getString(c
							.getColumnIndex("TeacherLastName"));
					item.teacher.middleName = c.getString(c
							.getColumnIndex("TeacherMiddleName"));
					item.teacher.post.fullName = c.getString(c
							.getColumnIndex("PostFullName"));
					item.teacher.post.shortName = c.getString(c
							.getColumnIndex("PostShortName"));
					item.task_time.startTime = c.getString(c
							.getColumnIndex("Task_timeStartTime"));
					item.task_time.endTime = c.getString(c
							.getColumnIndex("Task_timeEndTime"));
					
					sh.schedule.add(item);
				} while (c.moveToNext());
				schedules.set(pos, sh);
			}
			c.close();
		}

		dbHelper.close();
	}

	private Cursor getCursorByDate(String date, DBHelper dbHelper) {
		db = dbHelper.getWritableDatabase();
		String sqlQuery = "select class.fullname as ClassFullName, "
				+ "class.shortname as ClassShortName, "
				+ "room.name as RoomName, "
				+ "building.name as BuidingName, building.geo as BuildingGeo, "
				+ "building.photo as BuildingPhoto, "
				+ "task_type.fullname as Task_typeFullName, "
				+ "task_type.shortname as Task_typeShortName, task.fullname as TaskFullName, "
				+ "task.shortname as TaskShortName, task_time.starttime as Task_timeStartTime, "
				+ "task_time.endtime as Task_timeEndTime, task_time.description as Task_timeDescription, "
				+ "post.fullname as PostFullName, post.shortname as PostShortName, "
				+ "teacher.firstname as TeacherFirstName, teacher.lastname as TeacherLastName, "
				+ "teacher.middlename as TeacherMiddleName, teacher.photo as TeacherPhoto, "
				+ "schedule.day as ScheduleDay "
				+ "from schedule "
				+ "left join task on schedule.task_id = task.id "
				+ "left join room on schedule.room_id = room.id "
				+ "left join teacher on schedule.teacher_id = teacher.id "
				+ "left join class on schedule.class_id = class.id "
				+ "left join task_time on schedule.task_time_id = task_time.id "
				+ "left join building on room.building_id = building.id "
				+ "left join task_type on task.task_type_id = task_type.id "
				+ "left join post on teacher.post_id = post.id "
				+ "where schedule.is_active = 1 " + "and schedule.day = ?;";
		Cursor c = null;
		c = db.rawQuery(sqlQuery, new String[] { date });
		return c;
	}

/*	private Date getScheduleLastDate() {
		db = dbHelper.getWritableDatabase();
		String sqlQuery = "select max(day) from schedule where is_active = 1;";
		Cursor c = null;
		c = db.rawQuery(sqlQuery, null);
		if (c != null) {
			// java.text.DateFormat df =
			// java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT,
			// new Locale("EN"));
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd", new Locale("ru"));
			Date d = new Date();
			c.moveToFirst();
			Log.d("MAXDATE", c.getString(0));
			try {
				// d = df.parse(c.getString(0));
				d = simpleDateFormat.parse(c.getString(0));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			c.close();
			return d;
		}
		return null;
	}*/

	private void FillDates(){
		db = dbHelper.getWritableDatabase();
		String sqlQuery = "select distinct day from schedule where is_active = 1;";
		schedules.clear();
		Cursor c = null;
		c = db.rawQuery(sqlQuery, null);
		if (c != null) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd", new Locale("ru"));
			if (c.moveToFirst()){
				do {
					Schedule sh = new Schedule();
					try {
						sh.date = simpleDateFormat.parse(c.getString(0));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					schedules.add(sh);
				}while (c.moveToNext());			
			}
		}
		c.close();
	}
	
	private void FillTasks(){
		db = dbHelper.getWritableDatabase();
		String sqlQuery = "select distinct fullname from task;";
		tasks.clear();
		Cursor c = null;
		c = db.rawQuery(sqlQuery, null);
		if (c != null) {
			if (c.moveToFirst()){
				do {
					tasks.add(c.getString(0));
				}while (c.moveToNext());			
			}
		}
		c.close();
	}
	
	/*
	 * private void showList() { LinearLayout linLayout = (LinearLayout)
	 * findViewById(R.id.linLayout); LayoutInflater ltInflater =
	 * getLayoutInflater(); Boolean changeBackColor = false;
	 * 
	 * for (Schedule sh : schedules) { View item =
	 * ltInflater.inflate(R.layout.list_element, linLayout,false); TextView
	 * tv_task = (TextView) item.findViewById(R.id.textTask); TextView
	 * tv_teacher = (TextView) item.findViewById(R.id.textTeacher); TextView
	 * tv_room = (TextView) item.findViewById(R.id.textRoom); TextView
	 * tv_task_start_time = (TextView)
	 * item.findViewById(R.id.textTaskStartTime); TextView tv_task_end_time =
	 * (TextView) item.findViewById(R.id.textTaskEndTime); // Log.d("!",
	 * "!!!!!!!!!!!!!!!!"); // Log.d("!", sh.task.shortName);
	 * 
	 * String task = sh.task.task_type.shortName + " " + sh.task.shortName;
	 * tv_task.setText(task); String teacher = sh.teacher.lastName + " " +
	 * sh.teacher.firstName.charAt(0) + ". " + sh.teacher.middleName.charAt(0) +
	 * "."; tv_teacher.setText(teacher); tv_room.setText(sh.room.name);
	 * tv_task_start_time.setText(sh.task_time.startTime);
	 * tv_task_end_time.setText(sh.task_time.endTime); if (changeBackColor) {
	 * item.setBackgroundColor(Color.LTGRAY); } changeBackColor =
	 * !changeBackColor; linLayout.addView(item); } }
	 */

	class DBHelper extends SQLiteOpenHelper {

		private static final String DB = "myDB.db";

		public DBHelper(Context context) {
			// ����������� �����������
			super(context, DB, null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			InputStream is = null;
			is = getResources().openRawResource(R.raw.makedb);
			byte[] buffer = new byte[20000];
			try {
				is.read(buffer);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String sql = new String(buffer);
			Log.d("!!!SQLString", sql);
			db.beginTransaction();
			int index = 0;
			while ((index = sql.indexOf(";")) != -1){
				String s = sql.substring(0, index);
				Log.d("!!!SQLString", s); 
				db.execSQL(s);
				sql = sql.substring(index + 1);
			}
			// ������� ������� � ������
			
			db.setTransactionSuccessful();
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

	

	private class MyFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

		Context ctx;

		public MyFragmentStatePagerAdapter(FragmentManager fm, Context context) {
			super(fm);
			ctx = context;
//			this.setPrimaryItem(pager, 5, this.instantiateItem(pager, 5));
		}

		@Override
		public Fragment getItem(int position) {
			Log.d("!!!POSITION", String.valueOf(position));
			Log.d("!!!SIZE", String.valueOf(schedules.get(position).schedule.size()));
			if (schedules.get(position).schedule.size() == 0){
				readSchedule(schedules.get(position).date);
			}
		    Log.d("!!!NewInstance", String.valueOf(position));
		    Log.d("NewInstance", schedules.get(position).date.toString());
		    Log.d("NewInstance", schedules.get(position).schedule.get(0).task.shortName);
			return Page.newInstance(position, ctx);
		}

		@Override
		public int getCount() {
			return schedules.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE dd MMM",
					new Locale("ru"));
			return simpleDateFormat.format(schedules.get(position).date);
		}
	}
}
