package grischenkomaxim.schedule;

import java.util.Set;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class PageFragment extends Fragment {
	  
	  static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
	  TaskListAdapter adapter;
	  static Context ctx;
	  
	  int pageNumber;
//	  static List<Item> schedules = new ArrayList<MainActivity.Item>();
	  static LayoutInflater lInflater;
	  
	  static PageFragment newInstance(int page, Context context) {
	    PageFragment pageFragment = new PageFragment();
	    Bundle arguments = new Bundle();
	    arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
	    pageFragment.setArguments(arguments);
	    lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    ctx = context;
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
	    LinearLayout linLayout = new LinearLayout(ctx);
//		LinearLayout linLayout = (LinearLayout) view.findViewById(R.id.linLayout);
//		LayoutInflater ltInflater = ctx.getLayoutInflater();
		Boolean changeBackColor = false; 
		
		adapter = new TaskListAdapter(ctx, MainActivity.schedules.get(pageNumber).schedule);
		 
//		ActivityManager a = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		getActivity().setContentView(R.layout.fragment);
		
		/*ListView lv = (ListView) getActivity().findViewById(R.id.listView);
		Log.d("!!!Activity", getActivity().toString());
		Log.d("!!!ListView", getActivity().findViewById(R.id.listView).toString());
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
			}
		});*/
		
		for (Item sh : MainActivity.schedules.get(pageNumber).schedule) {
			View item = lInflater.inflate(R.layout.list_element, linLayout,false);
			TextView tv_task = (TextView) item.findViewById(R.id.textTask);
			TextView tv_teacher = (TextView) item.findViewById(R.id.textTeacher);
			TextView tv_room = (TextView) item.findViewById(R.id.textRoom);
			TextView tv_task_start_time = (TextView) item.findViewById(R.id.textTaskStartTime);
			TextView tv_task_end_time = (TextView) item.findViewById(R.id.textTaskEndTime);
//			Log.d("!", "!!!!!!!!!!!!!!!!");
//			Log.d("!", sh.task.shortName); 
			
//			String task = sh.task.task_type.shortName + " " + sh.task.shortName;
			tv_task.setText(sh.getTaskShort());
//			String teacher = sh.teacher.post.shortName + " " 
//							+ sh.teacher.lastName + " "
//							+ sh.teacher.firstName.charAt(0) + ". " 
//							+ sh.teacher.middleName.charAt(0) + ".";
			tv_teacher.setText(sh.getPostShortName() + " " + sh.getTeacherShort());
			tv_room.setText(sh.getRoom());
			tv_task_start_time.setText(sh.getTask_timeStart());
			tv_task_end_time.setText(sh.getTask_timeEnd());
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
