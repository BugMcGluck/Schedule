package egar.schedule;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.sip.SipAudioCall.Listener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity /* extends ActionBarActivity */extends FragmentActivity 
		implements OnClickListener {

	DBHelper dbHelper;
	SQLiteDatabase db;
	private static final String DBNAME = "myDB.db";
	public static List<Schedule> schedules = new ArrayList<Schedule>();
//	GregorianCalendar calendar = new GregorianCalendar();
//	Date date = calendar.getTime();
//	Date maxDate;
/*	public static final int[] colors = {
		Color.CYAN,
		Color.GREEN,
		Color.MAGENTA,
		Color.RED,
		Color.YELLOW,
		Color.DKGRAY,
		Color.LTGRAY
	};*/
//	public static List<String> tasks = new ArrayList<String>();
	
	private class ListItem{
		Long id;
		String name;
		public ListItem(Long id, String name) {
			super();
			this.id = id;
			this.name = name;
		}
	}
	private List<ListItem> schedulesList = new ArrayList<ListItem>();
	private Long currentSchedule = (long) 1;
	public static schedType currentScheduleType = schedType.Group;
	
	private SharedPreferences sPref;
	
	List<ListItem> levelList;
	
	ViewPager pager;
	PagerAdapter pagerAdapter;
//	int PAGE_COUNT = 5;
	
    private DrawerLayout mDrawerLayout;
//    private ListView mDrawerList;
    private View mDrawerView;
    private ActionBarDrawerToggle mDrawerToggle;
    
    private static final String SERVER = "https://3574a6a2.ngrok.com/";
    
    private enum schedTree {City, Univercity, Faculty, Teacher, Group, TeacherSchedule, GroupSchedule};
    public enum schedType {Teacher, Group};
    
    private class UpdatedScheduleTree{
		private String city;
    	private String university;
    	private String faculty;
    	private String group;
    	private Long groupId;
    	private String teacher;
    	private Long teacherId;
    	private schedType type;
    	
    }
    private UpdatedScheduleTree updatedScheduleTree = new UpdatedScheduleTree();

    //Переменные для сохранения обновлённого расписания в БД
//	private List<ContentValues> tableClass, tableRoom, tableTask, tableTaskType, tableTaskTime, tableTeacher, tableSchedule;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v7.app.ActionBarActivity#onCreate(android.os.Bundle)
	 */
	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sPref = getPreferences(MODE_PRIVATE);
		currentSchedule = sPref.getLong("CurrentSchedule", 1);
		Log.d("Cuurent Schedule", currentSchedule.toString());
		currentScheduleType = schedType.valueOf(sPref.getString("ScheduleType","Group"));
		setContentView(R.layout.main);

		dbHelper = new DBHelper(this, DBNAME);
//		maxDate = getScheduleLastDate();
		updateSchedulesList();
		fillDates();
//		fillTasks();
//		Log.d("!!!MAXDATE", maxDate.toString());
		
/*		pager = (ViewPager) findViewById(R.id.pager);
		Log.d("!!PAGER", pager.toString());
		pagerAdapter = new MyFragmentStatePagerAdapter(
				getSupportFragmentManager(), this);
		pager.setAdapter(pagerAdapter);
	
		pager.setCurrentItem(getStartPosition());*/
		if (schedulesList.size() == 0){
			DialogFragment dlg = new ScheduleTypeDialog();
			dlg.show(getFragmentManager(), "dlg");
		}
		android.app.Fragment scheduleFragment = new ScheduleFragment(this);
		android.app.FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.contentFrame, scheduleFragment).commit();
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
       // getActionBar().setHomeButtonEnabled(true);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mDrawerView = findViewById(R.id.left_drawer);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        
        android.app.Fragment drawerFragment = new DrawerFragment(this, getListStrings(schedulesList));
        fragmentManager.beginTransaction().replace(R.id.left_drawer, drawerFragment).commit();
/*        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, getListStrings(schedulesList)));
        mDrawerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				changeCurrentSchedule(position);				
			}
		});
		*/
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

	@Override
	protected void onDestroy() {
		sPref = getPreferences(MODE_PRIVATE);
		Editor e = sPref.edit();
		e.putLong("CurrentSchedule", currentSchedule);
		e.putString("ScheduleType", currentScheduleType.toString());
		e.commit();
		Log.d("SaveleSchedule", currentSchedule.toString());
		super.onDestroy();
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

		switch (item.getItemId()) {
		case R.id.action_calendar:
			Intent calendarIntent = new Intent(this,
					egar.schedule.Calendar.class);
			startActivityForResult(calendarIntent, 1);
			return true;
		case R.id.action_refresh:
			if(updateCurrentSchedule()){
				Toast.makeText(this, "Расписание обновлено", Toast.LENGTH_LONG).show();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

/*	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}*/

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

					item.clas.setName(c.getString(c.getColumnIndex("ClassName")));
					item.clas.setId(c.getLong(c.getColumnIndex("ClassId")));
					item.room.setName(c.getString(c.getColumnIndex("RoomName")));
					item.room.setAddress(c.getString(c.getColumnIndex("RoomAddress"))); 
					item.task.setName(c.getString(c.getColumnIndex("TaskName")));
					item.task.setTask_type(c.getString(c.getColumnIndex("Task_typeName")));
					item.teacher.setName(c.getString(c.getColumnIndex("TeacherName")));
					item.teacher.setId(c.getLong(c.getColumnIndex("TeacherId")));
					item.task_time.setStartTime(c.getString(c.getColumnIndex("Task_timeStartTime")));
					item.task_time.setEndTime(c.getString(c.getColumnIndex("Task_timeEndTime")));
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
	
/*	private void saveSchedule() {
		List<ContentValues> tableClass = new ArrayList<ContentValues>();
		List<ContentValues> tableRoom = new ArrayList<ContentValues>();
		List<ContentValues> tableTask = new ArrayList<ContentValues>();
		List<ContentValues> tabletaskType = new ArrayList<ContentValues>();
		List<ContentValues> tableTaskTime = new ArrayList<ContentValues>();
		List<ContentValues> tableTeacher = new ArrayList<ContentValues>();
		List<ContentValues> tableSchedule = new ArrayList<ContentValues>();
		saveTable(tableClass);
		saveTable(tableRoom);
		saveTable(tableTask);
		saveTable(tabletaskType);
		saveTable(tableTaskTime);
		saveTable(tableTeacher);
		saveTable(tableSchedule);
	}
	
*/
/*	private void saveTable(List<ContentValues> table) {
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
	}*/

	private Cursor getCursorByDate(String date, DBHelper dbHelper) {
		Cursor c = null;
		String sqlQuery = "select "
				+ "class.name as ClassName, class.id as ClassId, "
				+ "room.name as RoomName, "
				+ "room.address as RoomAddress, "
				+ "task_type.name as Task_typeName, "
				+ "task.name as TaskName, task_time.starttime as Task_timeStartTime, "
				+ "task_time.endtime as Task_timeEndTime,  "
				+ "teacher.name as TeacherName, teacher.id as TeacherId, "
				+ "schedule.day as ScheduleDay "
				+ "from schedule "
				+ "left join task on schedule.task_id = task.id "
				+ "left join room on schedule.room_id = room.id "
				+ "left join teacher on schedule.teacher_id = teacher.id "
				+ "left join class on schedule.class_id = class.id "
				+ "left join task_time on schedule.task_time_id = task_time.id "
				+ "left join task_type on task.task_type_id = task_type.id ";
		if(currentScheduleType.equals(schedType.Group)){
			sqlQuery = sqlQuery + "where schedule.Class_id = ? " + "and schedule.day = ?";
		}else{
			sqlQuery = sqlQuery + "where schedule.Teacher_id = ? " + "and schedule.day = ?";
		}
		sqlQuery = sqlQuery + "order by Task_timeStartTime;";
		c = dbHelper.getWritableDatabase().rawQuery(sqlQuery, new String[] { currentSchedule.toString(), date });
		return c;
	}

	private void deleteSchedule(int position){
		ContentValues cv = new ContentValues();
		cv.put("hasSchedule", 0);
		db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		if (1 == db.update("Class", cv, "Id = ?", new String[] {schedulesList.get(position).id.toString()})){
			db.setTransactionSuccessful();
			Toast.makeText(this, "Расписание удалено", Toast.LENGTH_LONG).show();
		}
		db.endTransaction();
		db.close();
		updateSchedulesList();
		mDrawerLayout.requestLayout();
	}
	
	private void updateSchedulesList(){
		String sqlQuery = "select t.Id AS Id, t.Name AS Name "
				+"from teacher t "
				+"where t.hasSchedule = 1 "
				+"union "
				+"select c.Id AS Id, c.Name AS Name from class c "
				+"where c.hasSchedule = 1;"; 
		Cursor c = dbHelper.getReadableDatabase().rawQuery(sqlQuery, null);
		schedulesList.clear();
		if (c != null) {
			if (c.moveToFirst()){
				do {
					schedulesList.add(new ListItem(c.getLong(0), c.getString(1)));
				}while (c.moveToNext());
			}
		}
	}
	
	
	private List<String> getListStrings(List<ListItem> baseList){
		List<String> list = new ArrayList<String>();
		for (ListItem item : baseList) {
			list.add(item.name);
		}
		Log.d("!!!LIST", list.toString());
		return list;
	};
	
	private void fillDates(){
		db = dbHelper.getWritableDatabase();
		String sqlQuery;
		if (currentScheduleType.equals(schedType.Group)){
			sqlQuery = "select distinct day from schedule where class_id = ? order by day;";
		}else{
			sqlQuery = "select distinct day from schedule where teacher_id = ? order by day;";
		}
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
	
/*	private void fillTasks(){
		db = dbHelper.getWritableDatabase();
		String sqlQuery = "select distinct name from task;";
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
	}*/
	
	public void changeCurrentSchedule(int position) {
		currentSchedule = schedulesList.get(position).id;
		schedules.clear();
		fillDates();
		android.app.Fragment scheduleFragment = new ScheduleFragment(this);
		android.app.FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.contentFrame, scheduleFragment).commit();
//		pagerAdapter = new MyFragmentStatePagerAdapter(
//				getSupportFragmentManager(), this);
//		pager.setAdapter(pagerAdapter);
//		pager.setCurrentItem(getStartPosition());
		Log.d("!!!CurrentPOSITION", String.valueOf(getStartPosition()));
		Log.d("!!!COUNT", String.valueOf(schedules.size()));
		mDrawerLayout.closeDrawer(mDrawerView);
	}

	private String makeGetRequest(String url) {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		HttpResponse response = null;
		HttpEntity entity;
		try {
	        response = client.execute(request);
	        Log.d("URL", url); 
	        Log.d("Response of GET request", response.toString());
	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
		/*byte[] buffer = null;
		try {
			response.getEntity().getContent().read(buffer);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		entity = response.getEntity();
		String message = null;
		try {
			message = EntityUtils.toString(entity);
		} catch (org.apache.http.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return message;
	}
	
	private String makeRequestByUrl(String url){
/*		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
			}
		});*/
		String response = null;
		Task t = new Task();
		t.execute(url);
		try {
			response = t.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d("Response", response);
		return response;
	}

	private void startUpdatingShedule (){
		
	}
	
	//Запускает поиск и обновление расписания с сервера
	private void updateSchedulesFromServer(schedTree treeLevel, long position){
		List<String> labels;
		android.app.Fragment fragment;
		android.app.FragmentManager fragmentManager = getFragmentManager();
		switch (treeLevel){
		case City:
			levelList = parseJsonToList(makeRequestByUrl(SERVER + "cities.json"), "id", "title");
			labels = getListStrings(levelList);
			getActionBar().setTitle("Выбор города");
			fragment = new SearchFragment(this, labels, treeLevel);
			fragmentManager.beginTransaction().replace(R.id.contentFrame, fragment).commit();
			break;
		case Faculty:
			updatedScheduleTree.university = levelList.get((int) position).name;
			levelList = parseJsonToList(makeRequestByUrl(SERVER + "faculties/by_university/" + levelList.get((int) position).id.toString() + ".json"), "id", "title");
			labels = getListStrings(levelList);
			getActionBar().setTitle("Выбор факультета");
			fragment = new SearchFragment(this, labels, treeLevel);
			fragmentManager.beginTransaction().replace(R.id.contentFrame, fragment).commit();
			break;
		case Group:
			updatedScheduleTree.faculty = levelList.get((int) position).name;
			levelList = parseJsonToList(makeRequestByUrl(SERVER + "groups/by_faculty/" + levelList.get((int) position).id.toString() + ".json"), "id", "title");
			labels = getListStrings(levelList);
			getActionBar().setTitle("Выбор группы");
			fragment = new SearchFragment(this, labels, treeLevel);
			fragmentManager.beginTransaction().replace(R.id.contentFrame, fragment).commit();
			break;
		case Teacher:
			updatedScheduleTree.university = levelList.get((int) position).name;
			levelList = parseJsonToList(makeRequestByUrl(SERVER + "teacher/by_university/" + levelList.get((int) position).id.toString() + ".json"), "id", "name");
			labels = getListStrings(levelList);
			getActionBar().setTitle("Выбор преподавателя");
			fragment = new SearchFragment(this, labels, treeLevel);
			fragmentManager.beginTransaction().replace(R.id.contentFrame, fragment).commit();
			break;
		case Univercity:
			updatedScheduleTree.city = levelList.get((int) position).name;
			levelList = parseJsonToList(makeRequestByUrl(SERVER + "universities/by_city/" + levelList.get((int) position).id.toString() + ".json"), "id", "title");
			labels = getListStrings(levelList);
			getActionBar().setTitle("Выбор университета");
			fragment = new SearchFragment(this, labels, treeLevel);
			fragmentManager.beginTransaction().replace(R.id.contentFrame, fragment).commit();
			break;
		case TeacherSchedule:
			updatedScheduleTree.teacher = levelList.get((int) position).name;
			updatedScheduleTree.teacherId = levelList.get((int) position).id;
			updatedScheduleTree.type = schedType.Teacher;
			writeUpdatedSchedule(makeRequestByUrl(SERVER + "lessons/by_teacher/" + levelList.get((int) position).id.toString() + ".json"));
			break;
		case GroupSchedule:
			updatedScheduleTree.group = levelList.get((int) position).name;
			updatedScheduleTree.groupId = levelList.get((int) position).id;
			updatedScheduleTree.type = schedType.Group;
			writeUpdatedSchedule(makeRequestByUrl(SERVER + "lessons/by_group/" + levelList.get((int) position).id.toString() + ".json"));
			break;
		default:
			break;
		}
	}
	
	private List<ListItem> parseJsonToList(String str, String id, String title){
		JSONArray jArray = null;
		JSONObject jObject = null;
		List<ListItem> list = new ArrayList<MainActivity.ListItem>();
		try {
			jArray = new JSONArray(str);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i = 0; i < jArray.length(); i++){
			try {
				jObject = new JSONObject(jArray.getString(i));
				list.add(new ListItem(jObject.getLong(id), jObject.getString(title)));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}
	
	private boolean updateCurrentSchedule(){
		db = dbHelper.getReadableDatabase();
		Cursor c;
		if (currentScheduleType == schedType.Group){
			c = db.query("Class", new String[] {"Name", "Faculty", "EduOrg", "City"}, "Id = ?", new String[] {currentSchedule.toString()}, null, null, null);
			if (c != null && c.moveToFirst()){
				updatedScheduleTree.city = c.getString(c.getColumnIndex("City"));
				updatedScheduleTree.faculty = c.getString(c.getColumnIndex("Faculty"));
				updatedScheduleTree.university = c.getString(c.getColumnIndex("EduOrg"));
				updatedScheduleTree.group = c.getString(c.getColumnIndex("Name"));
				updatedScheduleTree.groupId = currentSchedule;
				updatedScheduleTree.type = schedType.Group;
			}
			return writeUpdatedSchedule(makeRequestByUrl(SERVER + "lessons/by_group/" + currentSchedule.toString() + ".json"));
		}else{
			c = db.query("Teacher", new String[] {"Name", "EduOrg", "City"}, "Id = ?", new String[] {currentSchedule.toString()}, null, null, null);
			if (c != null && c.moveToFirst()){
				updatedScheduleTree.city = c.getString(c.getColumnIndex("City"));
				updatedScheduleTree.university = c.getString(c.getColumnIndex("EduOrg"));
				updatedScheduleTree.teacher = c.getString(c.getColumnIndex("Name"));
				updatedScheduleTree.teacherId = currentSchedule;
				updatedScheduleTree.type = schedType.Teacher;
			}
			return writeUpdatedSchedule(makeRequestByUrl(SERVER + "lessons/by_teacher/" + currentSchedule.toString() + ".json"));
		}
	
	}
	
	private boolean writeUpdatedSchedule(String jsonString) {
		Log.d("Tree", updatedScheduleTree.faculty
				+ updatedScheduleTree.university + updatedScheduleTree.city);
		db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		clearOldSchedule();
		boolean isSucceful = parseJsonSchedule(jsonString);
		if (isSucceful) {
			db.setTransactionSuccessful();
			db.endTransaction();
			dbHelper.close();
			updateSchedulesList();
			mDrawerLayout.requestLayout();
			Log.d("ScheduleList", String.valueOf(schedulesList.size()));
			Log.d("Type", updatedScheduleTree.type.toString());
			Log.d("ID", updatedScheduleTree.groupId.toString());
			if (updatedScheduleTree.type.equals(schedType.Group)) {
				for (ListItem item : schedulesList) {
					if (item.id.equals(updatedScheduleTree.groupId)) {
						Log.d("Name", item.name);
						changeCurrentSchedule(schedulesList.indexOf(item));
					}
				}
			} else {
				for (ListItem item : schedulesList) {
					if (item.id.equals(updatedScheduleTree.teacherId)) {
						changeCurrentSchedule(schedulesList.indexOf(item));
					}
				}
			}
			return true;
		} else {
			db.endTransaction();
			dbHelper.close();
			android.app.Fragment scheduleFragment = new ScheduleFragment(this);
			android.app.FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.contentFrame, scheduleFragment).commit();
			return false;
		}

	}
	
	private void clearOldSchedule(){
//		db = dbHelper.getWritableDatabase();
//		db.beginTransaction();
		if (updatedScheduleTree.type.equals(schedType.Group)){
			db.delete("Schedule", "Class_id = ?", new String[] {String.valueOf(updatedScheduleTree.groupId)});
		} else if (updatedScheduleTree.type.equals(schedType.Teacher)){
			db.delete("Schedule", "Teacher_id = ?", new String[] {String.valueOf(updatedScheduleTree.teacherId)});
		}
//		db.setTransactionSuccessful();
//		db.endTransaction();
//		dbHelper.close();
	}
	
	private boolean parseJsonSchedule(String jsonString){
		JSONArray jArray = null;
		JSONObject jObject = null;
//		db = dbHelper.getWritableDatabase();
//		db.beginTransaction();
		ContentValues row;
		Cursor c;
		Long rowid;
		int rows;
		try {
			jArray = new JSONArray(jsonString);
		} catch (JSONException e) {
			Toast.makeText(this, "Ошибка соединения с сервером", Toast.LENGTH_LONG).show();
			e.printStackTrace();
			return false;
		}

		for(int i = 0; i < jArray.length(); i++){
			try {
				jObject = new JSONObject(jArray.getString(i));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Парсим таблицу Room
			row = new ContentValues();
			row.put("Id", jObject.optLong("auditory_id"));
			row.put("address", jObject.optString("address"));
			row.put("Name", jObject.optString("auditory_title"));
			if (row.getAsLong("Id") != 0) {
				c = db.query("Room", new String[] { "Id" }, "Id = ?",
						new String[] { jObject.optString("auditory_id") },
						null, null, null);
				Log.d("Room IF", c.toString());
				if (!c.moveToFirst()) {
					rowid = db.insert("Room", null, row);
					Log.d("Room Insert", rowid.toString());
				} else {
					rows = db.update("Room", row, "Id = ?",
							new String[] { jObject.optString("auditory_id") });
					Log.d("Room update", String.valueOf(rows));
				}
			}
			
			//Парсим таблицу Task
			row = new ContentValues();
			row.put("Id", jObject.optLong("id"));
			row.put("Name", jObject.optString("lesson_title"));
			row.put("Task_type_id", jObject.optLong("type_of_lesson_id"));
			if (row.getAsLong("Id") != 0) {
				c = db.query("Task", new String[] { "Id" }, "Id = ?",
						new String[] { jObject.optString("id") }, null, null,
						null);
				if (!c.moveToFirst()) {
					rowid = db.insert("Task", null, row);
					Log.d("Task Insert", rowid.toString());
				} else {
					rows = db.update("Task", row, "Id = ?",
							new String[] { jObject.optString("id") });
					Log.d("Task update", String.valueOf(rows));
				}
			}
			
			//Парсим таблицу Task_type
			row = new ContentValues();
			row.put("Id", jObject.optLong("type_of_lesson_id"));
			row.put("Name", jObject.optString("type_of_lesson_title"));
			if (row.getAsLong("Id") != 0) {
				c = db.query(
						"Task_type",
						new String[] { "Id" },
						"Id = ?",
						new String[] { jObject.optString("type_of_lesson_id") },
						null, null, null);
				if (!c.moveToFirst()) {
					rowid = db.insert("Task_type", null, row);
					Log.d("Task_type Insert", rowid.toString());
				} else {
					rows = db.update("Task_type", row, "Id = ?",
							new String[] { jObject
									.optString("type_of_lesson_id") });
					Log.d("Task_type update", String.valueOf(rows));
				}
			}

			//Парсим таблицу Task_time
			row = new ContentValues();
			Date startTime = new Date();
			Date endTime = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
			try {
				startTime = sdf.parse(jObject.optString("start_time"));
				endTime = sdf.parse(jObject.optString("end_time"));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sdf.applyPattern("HH:mm");
			row.put("Id", jObject.optLong("id"));
			row.put("StartTime", sdf.format(startTime));
			row.put("EndTime", sdf.format(endTime));
			if (row.getAsLong("Id") != 0) {
				c = db.query("Task_time", new String[] { "Id" }, "Id = ?",
						new String[] { jObject.optString("id") }, null, null,
						null);
				if (!c.moveToFirst()) {
					rowid = db.insert("Task_time", null, row);
					Log.d("Task_time Insert", rowid.toString());
				} else {
					rows = db.update("Task_time", row, "Id = ?",
							new String[] { jObject.optString("id") });
					Log.d("Task_time update", String.valueOf(rows));
				}
			}
			
			//Парсим таблицу Schedule
			//Определяем начальную и конечную даты
			Date startDate = new Date();
			Date endDate = new Date();
			sdf.applyPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			try {
				startDate =  sdf.parse(jObject.optString("start_date"));
				endDate =  sdf.parse(jObject.optString("end_date"));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			GregorianCalendar startCal = new GregorianCalendar();
			GregorianCalendar endCal = new GregorianCalendar();
			GregorianCalendar firstDate, iter;
			startCal.setTime(startDate);
			endCal.setTime(endDate);
			int dayOfWeek = jObject.optInt("day_of_week");
			int odd = jObject.optInt("even_odd");
			int weekAdd = 1;
			//Определяем первую дату по дню недели
			for (firstDate = startCal; firstDate.getTimeInMillis() < endCal.getTimeInMillis(); firstDate.add(GregorianCalendar.DAY_OF_YEAR, 1)) {
				if (odd == 0){
					weekAdd = 1;
					if (firstDate.get(GregorianCalendar.DAY_OF_WEEK) == (dayOfWeek + 1)) {
					break;
					}
				}else if(odd == 1){
					weekAdd = 2;
					if ((firstDate.get(GregorianCalendar.DAY_OF_WEEK) == (dayOfWeek + 1)) & (0 != (firstDate.get(GregorianCalendar.WEEK_OF_YEAR) % 2))){
						break;
					}
				}else if(odd == 2){
					weekAdd = 2;
					if ((firstDate.get(GregorianCalendar.DAY_OF_WEEK) == (dayOfWeek + 1)) & (0 == (firstDate.get(GregorianCalendar.WEEK_OF_YEAR) % 2))){
						break;
					}
				}
			}
			//Заполняем все даты с интервалом неделя или две
			for (iter = firstDate; iter.getTimeInMillis() <= endCal
					.getTimeInMillis(); iter.add(
					GregorianCalendar.WEEK_OF_YEAR, weekAdd)) {
				row = new ContentValues();
				row.put("Task_id", jObject.optLong("id"));
				row.put("Room_id", jObject.optLong("auditory_id"));
				if (updatedScheduleTree.type.equals(schedType.Teacher)) {
					row.put("Teacher_id", updatedScheduleTree.teacherId);
					row.put("Class_id", jObject.optLong("group_id"));
				} else {
					row.put("Teacher_id", jObject.optLong("teacher_id"));
					row.put("Class_id", updatedScheduleTree.groupId);
				}
				row.put("Task_time_id", jObject.optLong("id"));
				sdf.applyPattern("yyyy-MM-dd");
				row.put("Day", sdf.format(iter.getTime()));
				rowid = db.insert("Schedule", null, row);
				Log.d("Schedule Insert", rowid.toString());
			}
			
			//Парсим таблицу Class
			row = new ContentValues();
			if (updatedScheduleTree.type.equals(schedType.Group)) {
				row.put("Id", updatedScheduleTree.groupId);
				row.put("Name", updatedScheduleTree.group);
				row.put("Faculty", updatedScheduleTree.faculty);
				row.put("EduOrg", updatedScheduleTree.university);
				row.put("City", updatedScheduleTree.city);
				row.put("HasSchedule", 1);
				row.put("LastUpdate", sdf.format(new Date()));
			} else {
				row.put("Id", jObject.optLong("group_id"));
				row.put("Name", jObject.optString("group_title"));
			}
			if (row.getAsLong("Id") != 0) {
				c = db.query("Class", new String[] { "Id" }, "Id = ?",
						new String[] { row.getAsString("Id") }, null, null,
						null);
				if (!c.moveToFirst()) {
					rowid = db.insert("Class", null, row);
					Log.d("Class Insert", rowid.toString());
				} else {
					rows = db.update("Class", row, "Id = ?",
							new String[] { row.getAsString("Id") });
					Log.d("Class update", String.valueOf(rows));
				}
			}

			//Парсим таблицу Teacher
			row = new ContentValues();
			if (updatedScheduleTree.type.equals(schedType.Teacher)) {
				row.put("Id", updatedScheduleTree.teacherId);
				row.put("Name", updatedScheduleTree.teacher);
				row.put("EduOrg", updatedScheduleTree.university);
				row.put("City", updatedScheduleTree.city);
				row.put("HasSchedule", 1);
				row.put("LastUpdate", sdf.format(new Date()));
			} else {
				row.put("Id", jObject.optLong("teacher_id"));
				row.put("Name", jObject.optString("teacher_name"));
			}
			if (row.getAsLong("Id") != 0) {
				c = db.query("Teacher", new String[] { "Id" }, "Id = ?",
						new String[] { row.getAsString("Id") }, null, null,
						null);
				if (!c.moveToFirst()) {
					if (row.getAsLong("Id") != 0) {
						rowid = db.insert("Teacher", null, row);
						Log.d("Teacher Insert", rowid.toString());
					}
				} else {
					rows = db.update("Teacher", row, "Id = ?",
							new String[] { row.getAsString("Id") });
					Log.d("Teacher update", String.valueOf(rows));
				}
			}
		}
//		db.setTransactionSuccessful(); 
//		db.endTransaction();
//		dbHelper.close();
		return true;
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
		    Log.d("NewInstance", schedules.get(position).schedule.get(0).getTaskFullName());
			return Page.newInstance(position, ctx);
		}

		@Override
		public int getCount() {
			return schedules.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE. ddMMM",
					new Locale("ru"));
			return simpleDateFormat.format(schedules.get(position).date);
		}
	}
	
	private class Task extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... url) {
			// TODO Auto-generated method stub
			return makeGetRequest(url[0]);
		}
		
	}
	
	private class ScheduleFragment extends android.app.Fragment{
		
		Context ctx;
		public ScheduleFragment(Context context) {
			super();
			ctx = context;
			for (ListItem item : schedulesList){
				if (item.id == currentSchedule){
					getActionBar().setTitle(item.name);
				}
			}
			
		}

		@Override
		public View onCreateView(LayoutInflater inflater,
				@Nullable ViewGroup container,
				@Nullable Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.pager, container, false);
			pager = (ViewPager) rootView.findViewById(R.id.pager);
			pagerAdapter = new MyFragmentStatePagerAdapter(
					getSupportFragmentManager(), ctx);
			pager.setAdapter(pagerAdapter);
			pager.setCurrentItem(getStartPosition());
			return rootView;
		}
		
	}
	
	private class SearchFragment extends android.app.Fragment{
		Context ctx;
		List<String> list;
		schedTree level;

		public SearchFragment(Context ctx, List<String> list, schedTree level) {
			super();
			this.ctx = ctx;
			this.list = list;
			this.level = level;
		}

		@Override
		public View onCreateView(LayoutInflater inflater,
				@Nullable ViewGroup container,
				@Nullable Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.search_list, container, false);
			ListView lv = (ListView) rootView.findViewById(R.id.searchList);
			lv.setAdapter(new ArrayAdapter<String>(ctx, android.R.layout.simple_list_item_1, list));
			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					switch (level){
					case City:
						updateSchedulesFromServer(schedTree.Univercity, id);
						break;
					case Faculty:
						updateSchedulesFromServer(schedTree.Group, id);
						break;
					case Group:
						updateSchedulesFromServer(schedTree.GroupSchedule, id);
						break;
					case Teacher:
						updateSchedulesFromServer(schedTree.TeacherSchedule, id);
						break;
					case Univercity:
						if (currentScheduleType.equals(schedType.Group)){
							updateSchedulesFromServer(schedTree.Faculty, id);
						}else{
						updateSchedulesFromServer(schedTree.Teacher, id);
						}
						break;
					default:
						break;
					}
										
				}
			});
			return rootView;
		}
		
	}

	private class DrawerFragment extends android.app.Fragment{
		Context ctx;
		List<String> list;
		
		public DrawerFragment(Context ctx, List<String> list) {
			super();
			this.ctx = ctx;
			this.list = list;
		}

		@Override
		public View onCreateView(LayoutInflater inflater,
				@Nullable ViewGroup container,
				@Nullable Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.drawer, container, false);
			ListView lv = (ListView) rootView.findViewById(R.id.scheduleList);
			lv.setAdapter(new ScheduleListAdapter(list, ctx));
			LinearLayout lAdd = (LinearLayout) rootView.findViewById(R.id.addSchedulРµ);
			lAdd.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mDrawerLayout.closeDrawer(mDrawerView);
					DialogFragment dlg = new ScheduleTypeDialog();
					dlg.show(getFragmentManager(), "dlg");
				}
			});
			return rootView;
		}
		
	}
	private class ScheduleListAdapter extends BaseAdapter {

		List<String> list;
		Context ctx;
		LayoutInflater lInflater;
		
		public ScheduleListAdapter(List<String> list, Context ctx) {
			super();
			this.list = list;
			this.ctx = ctx;
			this.lInflater = (LayoutInflater) ctx
			        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = lInflater.inflate(R.layout.drawer_list_element, parent, false);
			}
			TextView tv_schedule = (TextView) convertView.findViewById(R.id.text_schedule);
			tv_schedule.setText((String)getItem(position));
			ImageView iv_delete =  (ImageView) convertView.findViewById(R.id.deleteSchedule);

			tv_schedule.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					changeCurrentSchedule(position);
				}
			});
			
			iv_delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
//					deleteSchedule(position);
					mDrawerLayout.closeDrawer(mDrawerView);
					DialogFragment dlg = new ScheduleDeleteDialog().newInstance(position);
					dlg.show(getFragmentManager(), "dlg2");
				}
			});
			return convertView;
		}

	}
	
	private class ScheduleTypeDialog extends DialogFragment implements android.content.DialogInterface.OnClickListener{

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
					.setTitle(R.string.text_addSchedule)
					.setPositiveButton(R.string.text_Teacher, this)
					.setNegativeButton(R.string.text_Group, this)
					.setMessage(R.string.text_ChooseScheduleType);
			return adb.create();
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch(which){
			case Dialog.BUTTON_POSITIVE:
				currentScheduleType = schedType.Teacher;
				break;
			case Dialog.BUTTON_NEGATIVE:
				currentScheduleType = schedType.Group;
				break;
			}
			updateSchedulesFromServer(schedTree.City, 0);
		}
		
	}
	
	private class ScheduleDeleteDialog extends DialogFragment implements DialogInterface.OnClickListener{

		int position;
		
		public ScheduleDeleteDialog newInstance(int position) {
			ScheduleDeleteDialog frag = new ScheduleDeleteDialog();
			this.position = position;
			return frag;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch(which){
			case Dialog.BUTTON_POSITIVE:
				deleteSchedule(position);
				break;
			case Dialog.BUTTON_NEGATIVE:
				
				break;
			}
		}


		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
					.setTitle(R.string.text_DeleteSchedule)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setPositiveButton(android.R.string.yes, this)
					.setNegativeButton(android.R.string.no, this)
					.setMessage(R.string.text_AcceptDeletingShedule);
			return adb.create();
		}
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}
