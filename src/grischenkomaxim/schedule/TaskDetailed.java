package grischenkomaxim.schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TaskDetailed extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_detailed);
		int position = getIntent().getIntExtra("position", 0);
		int pageNumber = getIntent().getIntExtra("pageNumber", 0);
		
		TextView startTime = (TextView)findViewById(R.id.tvDetailedStartTime);
		TextView endTime = (TextView)findViewById(R.id.tvDetailedEndTime);
		TextView roomName = (TextView)findViewById(R.id.tvDetailedRoomName);
		TextView taskName = (TextView)findViewById(R.id.tvDetailedTaskName);
		TextView teacherName = (TextView)findViewById(R.id.tvDetailedTeacherName);
		ProgressBar bar = (ProgressBar) findViewById(R.id.progressBarTime);
		
		String s;
		Item it = MainActivity.schedules.get(pageNumber).schedule.get(position);
		startTime.setText(it.task_time.startTime);
		endTime.setText(it.task_time.endTime);
		s = it.room.name + ", корпус " + it.room.building.name;
		roomName.setText(s);
		s = it.task.task_type.fullName + " " + it.task.fullName;
		taskName.setText(s);
		s = it.teacher.post.fullName + " " + it.teacher.lastName + " "
				+it.teacher.firstName + " " + it.teacher.middleName;
		teacherName.setText(s);
		
		Date currentDate = GregorianCalendar.getInstance().getTime();
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(currentDate);
		calendar.clear(GregorianCalendar.HOUR);
		calendar.clear(GregorianCalendar.HOUR_OF_DAY);
		calendar.clear(GregorianCalendar.MINUTE);
		calendar.clear(GregorianCalendar.SECOND);
		calendar.clear(GregorianCalendar.MILLISECOND);
		currentDate = calendar.getTime();
		Date date = MainActivity.schedules.get(pageNumber).date;
		
		Log.d("!CurrentDate", currentDate.toString());
		Log.d("!Date", date.toString());
		if (date.compareTo(currentDate) == 0){
			SimpleDateFormat sdf = new SimpleDateFormat("hh:mm", new Locale("ru"));
			Date start = new Date(), end = new Date();
			try {
				start = sdf.parse(it.task_time.startTime);
				Log.d("!!StartTime", start.toString());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			try {
				end = sdf.parse(it.task_time.endTime);
				Log.d("!!EndTime", end.toString());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			calendar = new GregorianCalendar();
			calendar.clear();
			calendar.set(GregorianCalendar.HOUR_OF_DAY, GregorianCalendar.getInstance().get(GregorianCalendar.HOUR_OF_DAY));
			calendar.set(GregorianCalendar.MINUTE, GregorianCalendar.getInstance().get(GregorianCalendar.MINUTE));
			Log.d("!!Calendar", calendar.toString());
			Date currentTime = calendar.getTime();
			Log.d("!!CurrentTime", currentTime.toString());
			if (currentTime.after(start) & currentTime.before(end)){
				bar.setMax((int) ((end.getTime() - start.getTime())/1000));
				bar.setProgress((int) ((currentTime.getTime() - start.getTime())/1000));
			}
			
		}
	}

}
