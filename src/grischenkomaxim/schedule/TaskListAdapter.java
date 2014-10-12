package grischenkomaxim.schedule;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TaskListAdapter extends BaseAdapter {

	Context ctx;
	List<Item> tasks;
	LayoutInflater lInflater;
	
	public TaskListAdapter(Context context, List<Item> items) {
		super();
		ctx = context;
	    tasks = items;
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
	public View getView(int position, View taskView, ViewGroup parent) {
		if (taskView == null){
			taskView = lInflater.inflate(R.layout.list_element, parent, false);
		}
		
		Item it = (Item) getItem(position);
		
		TextView tv_task = (TextView) taskView.findViewById(R.id.textTask);
		TextView tv_teacher = (TextView) taskView.findViewById(R.id.textTeacher);
		TextView tv_room = (TextView) taskView.findViewById(R.id.textRoom);
		TextView tv_task_start_time = (TextView) taskView.findViewById(R.id.textTaskStartTime);
		TextView tv_task_end_time = (TextView) taskView.findViewById(R.id.textTaskEndTime);
		TextView tv_letter = (TextView) taskView.findViewById(R.id.textLetterIcon);

		
		String task = it.task.task_type.shortName + " " + it.task.shortName;
		tv_task.setText(task);
		String teacher = it.teacher.post.shortName + " " 
						+ it.teacher.lastName + " "
						+ it.teacher.firstName.charAt(0) + ". " 
						+ it.teacher.middleName.charAt(0) + ".";
		tv_teacher.setText(teacher);
		String room = "ê." + it.room.building.name + ", à." + it.room.name;
		tv_room.setText(room);
		tv_task_start_time.setText(it.task_time.startTime);
		tv_task_end_time.setText(it.task_time.endTime);
		String letter = it.task.fullName.substring(0, 1);
		tv_letter.setText(letter); 
		for (int i = 0; i < MainActivity.tasks.size(); i++) {
			if (MainActivity.tasks.get(i).equals(it.task.fullName)){
				tv_letter.setBackgroundColor(MainActivity.colors[i]);
			}
		}
		
		
		/*Log.d("!!!POsition", String.valueOf(position));
		Log.d("!!!POs % 2", String.valueOf(position % 2));
		if ((position % 2) != 0 ){
			Log.d("!!!SetGray", "!!!SetGray");
			taskView.setBackgroundColor(Color.LTGRAY);
		}*/
		return taskView;
	}

}
