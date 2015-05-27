package egar.schedule;

import egar.schedule.R;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.R.color;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.ArcShape;
import android.opengl.Visibility;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TaskListAdapter extends BaseAdapter {

	Context ctx;
	List<Item> tasks;
	LayoutInflater lInflater;
	Date scheduleDate;
	
	public TaskListAdapter(Context context, Date date, List<Item> items) {
		super();
		ctx = context;
	    tasks = items;
	    scheduleDate = date;
	    lInflater = (LayoutInflater) ctx
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {

		return tasks.size();
	}

	@Override
	public Object getItem(int position) {

		return tasks.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View taskView, final ViewGroup parent) {
		if (taskView == null){
			taskView = lInflater.inflate(R.layout.list_element, parent, false);
		}
		
		Item it = (Item) getItem(position);
		
		TextView tv_task = (TextView) taskView.findViewById(R.id.textTask);
		TextView tv_taskType = (TextView) taskView.findViewById(R.id.textTaskType);
		TextView tv_teacher = (TextView) taskView.findViewById(R.id.textTeacher);
		TextView tv_room = (TextView) taskView.findViewById(R.id.textRoom);
		TextView tv_task_start_time = (TextView) taskView.findViewById(R.id.textTaskStartTime);
		TextView tv_task_end_time = (TextView) taskView.findViewById(R.id.textTaskEndTime);
		final TextView tv_letter = (TextView) taskView.findViewById(R.id.textLetterIcon);

		
//		String task = it.task.task_type.shortName + " " + it.task.shortName;
		tv_task.setText(it.task.getName());
		tv_taskType.setText(it.task.getTask_type());
//		String teacher = it.teacher.post.shortName + " " 
//						+ it.teacher.lastName + " "
//						+ it.teacher.firstName.charAt(0) + ". " 
//						+ it.teacher.middleName.charAt(0) + ".";
		tv_teacher.setText(it.teacher.getName());
//		String room = "к." + it.room.building.name + ", а." + it.room.name;
		tv_room.setText(it.getRoom());
		tv_task_start_time.setText(it.getTask_timeStart());
		tv_task_end_time.setText(it.getTask_timeEnd());

		 
/*		for (int i = 0; i < MainActivity.tasks.size(); i++) {
			if (MainActivity.tasks.get(i).equals(it.getTaskName())) {
				if (i >= MainActivity.colors.length) {
					tv_letter.setBackgroundColor(MainActivity.colors[i- MainActivity.colors.length]);
				} else {
					tv_letter.setBackgroundColor(MainActivity.colors[i]);
				}
			}
		}*/

		Date currentDate = GregorianCalendar.getInstance().getTime();
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(currentDate);
		calendar.clear(GregorianCalendar.HOUR);
		calendar.clear(GregorianCalendar.HOUR_OF_DAY);
		calendar.clear(GregorianCalendar.MINUTE);
		calendar.clear(GregorianCalendar.SECOND);
		calendar.clear(GregorianCalendar.MILLISECOND);
		currentDate = calendar.getTime();
		
		Log.d("!!CompareDate", String.valueOf(scheduleDate.compareTo(currentDate)));
		Log.d("!!DATE", scheduleDate.toString());
		Log.d("!!CurrentDate", currentDate.toString());
		if (scheduleDate.compareTo(currentDate) == 0){
			SimpleDateFormat sdf = new SimpleDateFormat("hh:mm", new Locale("ru"));
			Date start = new Date(), end = new Date();
			try {
				start = sdf.parse(it.getTask_timeStart());
				Log.d("!!StartTime", start.toString());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			try {
				end = sdf.parse(it.getTask_timeEnd());
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

				GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[] {Color.LTGRAY, Color.DKGRAY});
				gd.setShape(GradientDrawable.RECTANGLE);
				
				//gd.setSize((int) (80 * ctx.getResources().getDisplayMetrics().density), (int) ((80 * ctx.getResources().getDisplayMetrics().density) * (int) ((currentTime.getTime() - start.getTime()) / (end.getTime() - start.getTime()))));
				tv_letter.setHeight((int) ((80 * ctx.getResources().getDisplayMetrics().density) * (int) ((currentTime.getTime() - start.getTime()) / (end.getTime() - start.getTime()))));
				Log.d("!!GRADIENT_Width", String.valueOf(tv_letter.getWidth()));
				Log.d("!!GRADIENT_Heigth", String.valueOf(tv_letter.getHeight() * (int) ((currentTime.getTime() - start.getTime()) / (end.getTime() - start.getTime()))));
			    ShapeDrawable shape = new ShapeDrawable(new ArcShape(0, 360 * (int) ((currentTime.getTime() - start.getTime()) / (end.getTime() - start.getTime()))));
				//arcShape.resize((int) ((80 * ctx.getResources().getDisplayMetrics().density)), (int) ((80 * ctx.getResources().getDisplayMetrics().density)));
			    
			    shape.setIntrinsicHeight((int) ((80 * ctx.getResources().getDisplayMetrics().density)));
			    shape.setIntrinsicWidth((int) ((80 * ctx.getResources().getDisplayMetrics().density)));
			    shape.getPaint().setColor(Color.GRAY);
			    Log.d("!!!ARC", String.valueOf(shape.getIntrinsicHeight()));
			    tv_letter.setBackgroundDrawable(shape);
			}
			
			if(currentTime.after(end)){
				//tv_letter.setBackgroundColor(Color.GRAY);
				tv_letter.setBackgroundResource(R.drawable.back_left); 
				tv_letter.setText("✔");
			}
			
		}
		
		if (scheduleDate.compareTo(currentDate) < 0){
			//tv_letter.setBackgroundColor(Color.GRAY);
			tv_letter.setBackgroundResource(R.drawable.back_left);
			tv_letter.setText("✔");
		}
		
		//Log.d("!!Background", String.valueOf(tv_letter.getDrawingCacheBackgroundColor()));
		/*Log.d("!!!POsition", String.valueOf(position));
		Log.d("!!!POs % 2", String.valueOf(position % 2));
		if ((position % 2) != 0 ){
			Log.d("!!!SetGray", "!!!SetGray");
			taskView.setBackgroundColor(Color.LTGRAY);
		}*/
		
		LinearLayout taskLayout = (LinearLayout) taskView.findViewById(R.id.taskListElement);
		final LinearLayout taskActions = (LinearLayout) taskView.findViewById(R.id.taskActions);
		
		taskLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (taskActions.getVisibility() == View.GONE) {
					taskActions.setVisibility(View.VISIBLE);
					tv_letter.setBackgroundResource(R.drawable.back_left_top);
				} else if (taskActions.getVisibility() == View.VISIBLE) {
					taskActions.setVisibility(View.GONE);
					tv_letter.setBackgroundResource(R.drawable.back_left);
				}
			}
		});
		
		LinearLayout taskActionPlace = (LinearLayout) taskView.findViewById(R.id.taskActionPlace);
		taskActionPlace.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				taskActions.setVisibility(View.GONE);
				tv_letter.setBackgroundResource(R.drawable.back_left);
			}
		});
		
		LinearLayout taskActionSchedule = (LinearLayout) taskView.findViewById(R.id.taskActionSchedule);
		taskActionSchedule.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				taskActions.setVisibility(View.GONE);
				tv_letter.setBackgroundResource(R.drawable.back_left);
			}
		});
		return taskView;
	}

}
