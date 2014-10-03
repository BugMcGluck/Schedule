package grischenkomaxim.schedule;

import android.app.Activity;
import android.os.Bundle;
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
	}

}
