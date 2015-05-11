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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import android.R.color;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity /* extends ActionBarActivity */extends FragmentActivity
		implements OnClickListener {

	DBHelper dbHelper;
	SQLiteDatabase db;
	private static final String DBNAME = "mydb.db";
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
	
	public class scheduleListItem{
		public scheduleListItem(int _id, String _name) {
			id = _id;
			name = _name;
		}
		int id;
		String name;
	}
	public static List<scheduleListItem> schedulesList = new ArrayList<scheduleListItem>();
	public Integer currentSchedule = 1;
	
	ViewPager pager;
	PagerAdapter pagerAdapter;
	int PAGE_COUNT = 5;
	
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v7.app.ActionBarActivity#onCreate(android.os.Bundle)
	 */
	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		dbHelper = new DBHelper(this, DBNAME);
//		maxDate = getScheduleLastDate();
		fillDates();
		fillTasks();
//		Log.d("!!!MAXDATE", maxDate.toString());
		
		pager = (ViewPager) findViewById(R.id.pager);
		Log.d("!!PAGER", pager.toString());
		pagerAdapter = new MyFragmentStatePagerAdapter(
				getSupportFragmentManager(), this);
		pager.setAdapter(pagerAdapter);
	
		pager.setCurrentItem(getStartPosition());
		
        getActionBar().setDisplayHomeAsUpEnabled(true);
       // getActionBar().setHomeButtonEnabled(true);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        updateSchedulesList();
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, getSchedulesListStrings()));
        mDrawerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				changeSchedule(position);				
			}
		});
		
		mDrawerToggle =  new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.app_name, R.string.app_name){

			@Override
			public void onDrawerClosed(View drawerView) {
				// TODO Auto-generated method stub
				super.onDrawerClosed(drawerView);
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				// TODO Auto-generated method stub
				super.onDrawerOpened(drawerView);
			}
			
		};
		
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		
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

	private int getStartPosition() {
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
		return startPosition;
	}

	
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data == null) {
			return;
		}
		// GregorianCalendar calendar = new
		// GregorianCalendar(arg2.getIntExtra("selectedYear", 0),
		// arg2.getIntExtra("selectedMonth", 0),
		// arg2.getIntExtra("selectedDay", 0));
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 1:
				Date selectedDate = new Date(data.getLongExtra("selectedDate",
						0));

				int startPosition = 0;
				for (int i = 0; i < schedules.size(); i++) {
					if (selectedDate.compareTo(schedules.get(i).date) >= 0) {
						if (selectedDate.compareTo(schedules.get(i).date) == 0) {
							startPosition = i;
						} else {
							startPosition = i + 1;
						}
					} else {
						break;
					}
				}
				pager.setCurrentItem(startPosition);
				break;

/*			case 2:
				currentSchedule = schedulesList.get(data.getIntExtra("selectedschedule", 0)).id;
				fillDates();
				pager.setCurrentItem(getStartPosition());
				pagerAdapter.notifyDataSetChanged();
				break;*/
			}
		}
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
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        
		switch (item.getItemId()){
		case R.id.action_settings:
			return true;
		case R.id.action_calendar:
			Intent calendarIntent =  new Intent(this, grischenkomaxim.schedule.Calendar.class);
			startActivityForResult(calendarIntent, 1);
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

					item.setClass(c.getString(c.getColumnIndex("ClassFullName")), c.getString(c.getColumnIndex("ClassShortName")));
					item.setRoom(c.getString(c.getColumnIndex("RoomName")), c.getString(c.getColumnIndex("BuidingName")), 
							c.getString(c.getColumnIndex("BuildingGeo")), null);
					item.setTask(c.getString(c.getColumnIndex("TaskFullName")), c.getString(c.getColumnIndex("TaskShortName")),
							c.getString(c.getColumnIndex("Task_typeFullName")), c.getString(c.getColumnIndex("Task_typeShortName")));
					item.setTeacher(c.getString(c.getColumnIndex("TeacherFirstName")), c.getString(c.getColumnIndex("TeacherLastName")),
							c.getString(c.getColumnIndex("TeacherMiddleName")), c.getString(c.getColumnIndex("PostFullName")),
							c.getString(c.getColumnIndex("PostShortName")));
					item.setTask_time(c.getString(c.getColumnIndex("Task_timeStartTime")), c.getString(c.getColumnIndex("Task_timeEndTime")), null);
					sh.schedule.add(item);
				} while (c.moveToNext());
				schedules.set(pos, sh);
			}
			c.close();
		}

		dbHelper.close();
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
	
	private void parseXML(){
		saveSchedule();
	}
	
	private void saveSchedule() {
		List<ContentValues> tableClass = new ArrayList<ContentValues>();
		List<ContentValues> tableRoom = new ArrayList<ContentValues>();
		List<ContentValues> tableBuilding = new ArrayList<ContentValues>();
		List<ContentValues> tableTask = new ArrayList<ContentValues>();
		List<ContentValues> tabletaskType = new ArrayList<ContentValues>();
		List<ContentValues> tableTaskTime = new ArrayList<ContentValues>();
		List<ContentValues> tablePost = new ArrayList<ContentValues>();
		List<ContentValues> tableTeacher = new ArrayList<ContentValues>();
		List<ContentValues> tableSchedule = new ArrayList<ContentValues>();
		saveTable(tableClass);
		saveTable(tableRoom);
		saveTable(tableBuilding);
		saveTable(tableTask);
		saveTable(tabletaskType);
		saveTable(tableTaskTime);
		saveTable(tablePost);
		saveTable(tableTeacher);
		saveTable(tableSchedule);
	}
	

	private void saveTable(List<ContentValues> table) {
		db = dbHelper.getWritableDatabase();
		for (ContentValues map : table) {
			Cursor c = null;
			if("Schedule".equals(map.get("table"))){	//Проверяем наличие такой-же записи
				c = db.query( map.getAsString("table")
						, null
						, "Class_id = ? and Task_time_id = ? and Day = ? and Is_active = 1"
						, new String [] {map.getAsString("Class_id"), map.getAsString("Task_time_id"), map.getAsString("Day")}
						, null
						, null,
						null);
			
				if(c.getCount()>0){	//Если есть такая запись, то проставляем ей is_active=0
					ContentValues isActive =  new ContentValues();
					isActive.put("id", c.getInt(c.getColumnIndex("id")));
					isActive.put("Task_id", c.getInt(c.getColumnIndex("Task_id")));
					isActive.put("Room_id", c.getInt(c.getColumnIndex("Room_id")));
					isActive.put("Teacher_id", c.getInt(c.getColumnIndex("Teacher_id")));
					isActive.put("Class_id", c.getInt(c.getColumnIndex("Class_id")));
					isActive.put("Task_time_id", c.getInt(c.getColumnIndex("Task_time_id")));
					isActive.put("Day", c.getString(c.getColumnIndex("Day")));
					isActive.put("Last_update", c.getString(c.getColumnIndex("Last_update")));
					isActive.put("Is_active", "0");
					db.update("Schedule", isActive, "id = ?", new String [] {map.getAsString("id")});
				}
			}
			db.insert(map.getAsString("table"), null, map);
		}
	}

	private Cursor getCursorByDate(String date, DBHelper dbHelper) {
		String sqlQuery = "select class.fullname as ClassFullName, "
				+ "class.shortname as ClassShortName, "
				+ "room.name as RoomName, "
				+ "building.name as BuidingName, building.geo as BuildingGeo, "
				+ "building.address as BuildingSddress, "
				+ "task_type.fullname as Task_typeFullName, "
				+ "task_type.shortname as Task_typeShortName, task.fullname as TaskFullName, "
				+ "task.shortname as TaskShortName, task_time.starttime as Task_timeStartTime, "
				+ "task_time.endtime as Task_timeEndTime, task_time.description as Task_timeDescription, "
				+ "post.fullname as PostFullName, post.shortname as PostShortName, "
				+ "teacher.firstname as TeacherFirstName, teacher.lastname as TeacherLastName, "
				+ "teacher.middlename as TeacherMiddleName, "
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
				+ "where schedule.Class_id = ? " + "and schedule.day = ?;";
		Cursor c = null;
		c = dbHelper.getWritableDatabase().rawQuery(sqlQuery, new String[] { currentSchedule.toString(), date });
		return c;
	}

	private void updateSchedulesList(){
		String sqlQuery = "select t.Id AS Id, t.LastName ||' '|| t.FirstName ||' '||t.MiddleName AS Name "
				+"from teacher t "
				+"where t.hasSchedule = 1 "
				+"union "
				+"select c.Id AS Id, c.FullName AS Name from class c "
				+"where c.hasSchedule = 1;"; 
		Cursor c = dbHelper.getReadableDatabase().rawQuery(sqlQuery, null);
		schedulesList.clear();
		if (c != null) {
			if (c.moveToFirst()){
				do {
					schedulesList.add(new scheduleListItem(c.getInt(0), c.getString(1)));
				}while (c.moveToNext());
			}
		}
	}
	
	
	@SuppressWarnings("null")
	private List<String> getSchedulesListStrings(){
		List<String> list = new ArrayList<String>();
		int i = 0;
		for (scheduleListItem item : schedulesList) {
			list.add(item.name);
		}
		Log.d("!!!LIST", list.toString());
		return list;
	};
	
	private void fillDates(){
		db = dbHelper.getWritableDatabase();
		String sqlQuery = "select distinct day from schedule where class_id = ?;";
		schedules.clear();
		Cursor c = null;
		c = db.rawQuery(sqlQuery, new String[] {currentSchedule.toString()}); //c = db.query("Schedule", new String[] {"Day"}, null, null, groupBy, having, orderBy);
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
		db.close();
	}
	
	private void fillTasks(){
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
		db.close();
	}
	
	private void changeSchedule(int position) {
		currentSchedule = schedulesList.get(position).id;
		schedules.clear();
		fillDates();
		pagerAdapter = new MyFragmentStatePagerAdapter(
				getSupportFragmentManager(), this);
		pager.setAdapter(pagerAdapter);
		pager.setCurrentItem(getStartPosition());
		Log.d("!!!CurrentPOSITION", String.valueOf(getStartPosition()));
		Log.d("!!!COUNT", String.valueOf(schedules.size()));
		mDrawerLayout.closeDrawer(mDrawerList);
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

		
		public DBHelper(Context context, String dbName) {
			// конструктор суперкласса
			super(context, dbName, null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			InputStream is = null;
			is = getResources().openRawResource(R.raw.makedb);
			byte[] buffer = new byte[50000];
			try {
				is.read(buffer);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String sql = new String(buffer);
			db.beginTransaction();
			int index = 0;
			while ((index = sql.indexOf(";")) != -1){
				String s = sql.substring(0, index);
				db.execSQL(s);
				sql = sql.substring(index + 1);
			}
			// создаем таблицу с полями
			
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
