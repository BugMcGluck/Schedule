package grischenkomaxim.schedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.graphics.Bitmap;

class Schedule {
	Date date;
	List<Item> schedule = new ArrayList<Item>();
}

class Item {
	Task task = new Task();
	Teacher teacher = new Teacher();
	Room room = new Room();
	Class clas = new Class();
	Task_time task_time = new Task_time();
	Calendar day;

	class Teacher {
		String lastName;
		String firstName;
		String middleName;
		Post post = new Post();
		Bitmap photo;
	}

	class Post {
		String fullName;
		String shortName;
	}

	class Task {
		Task_type task_type = new Task_type();
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