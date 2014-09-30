package grischenkomaxim.schedule;

import grischenkomaxim.schedule.MainActivity.Item;
import grischenkomaxim.schedule.MainActivity.Schedule;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PageFragment extends Fragment {
	  
	  static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
	  
	  int pageNumber;
	  int backColor;
//	  static List<Item> schedules = new ArrayList<MainActivity.Item>();
	  static Schedule schedule;
	  static LayoutInflater lInflater;
	  
	  static PageFragment newInstance(int page, Schedule sched, Context context) {
	    PageFragment pageFragment = new PageFragment();
	    Bundle arguments = new Bundle();
	    arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
	    pageFragment.setArguments(arguments);
	    schedule = sched;
	    lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    return pageFragment;
	  }
	  
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
	    

	  }
	  
	  @Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	      Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.fragment, null);
	    
		LinearLayout linLayout = (LinearLayout) view.findViewById(R.id.linLayout);
//		LayoutInflater ltInflater = ctx.getLayoutInflater();
		Boolean changeBackColor = false; 

		for (Item sh : schedule.schedule) {
			View item = lInflater.inflate(R.layout.list_element, linLayout,false);
			TextView tv_task = (TextView) item.findViewById(R.id.textTask);
			TextView tv_teacher = (TextView) item.findViewById(R.id.textTeacher);
			TextView tv_room = (TextView) item.findViewById(R.id.textRoom);
			TextView tv_task_start_time = (TextView) item.findViewById(R.id.textTaskStartTime);
			TextView tv_task_end_time = (TextView) item.findViewById(R.id.textTaskEndTime);
//			Log.d("!", "!!!!!!!!!!!!!!!!");
//			Log.d("!", sh.task.shortName); 
			
			String task = sh.task.task_type.shortName + " " + sh.task.shortName;
			tv_task.setText(task);
			String teacher = sh.teacher.lastName + " " + sh.teacher.firstName.charAt(0) + ". " + 
								sh.teacher.middleName.charAt(0) + ".";
			tv_teacher.setText(teacher);
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
	    
	    return view;
	  }
	}
