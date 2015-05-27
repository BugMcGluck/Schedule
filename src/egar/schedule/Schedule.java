package egar.schedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.graphics.Bitmap;
import android.util.Log;

class Schedule {
	Date date;
	List<Item> schedule = new ArrayList<Item>();
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public List<Item> getSchedule() {
		return schedule;
	}
	public void setSchedule(List<Item> schedule) {
		this.schedule = schedule;
	}
}

class Item {
	Task task = new Task();
	Teacher teacher = new Teacher();
	Room room = new Room();
	Class clas = new Class();
	Task_time task_time = new Task_time();
	Calendar day;

	class Teacher {
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		private Long id;
		private String name;

	}

	class Task {
		public String getTask_type() {
			return task_type;
		}
		public void setTask_type(String task_type) {
			this.task_type = task_type;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		private String task_type;
		private String name;
	}


	class Room {
		public String getName() {
			return name;
		}
		public void setName(String name) {
			if ((null != name) && ("null" != name.toLowerCase())) {
				this.name = name;
			}
		}
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			if ((null != address) && ("null" != address.toLowerCase())) {
				this.address = address;
			}
		}
		private String name;
		private String address;
	}

	class Class {
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		private Long id;
		private String name;
	}

	class Task_time {
		public String getStartTime() {
			return startTime;
		}
		public void setStartTime(String startTime) {
			this.startTime = startTime;
		}
		public String getEndTime() {
			return endTime;
		}
		public void setEndTime(String endTime) {
			this.endTime = endTime;
		}
		private String startTime;
		private String endTime;
	}	
	
	public String getTaskFullName() {
		return this.task.getTask_type() + " " + this.task.getName();
	}

	public String getRoom() {
		return "ауд." + this.room.getName();
	}

	public String getTask_timeStart() {
		return this.task_time.getStartTime();
	}
	
	public String getTask_timeEnd() {
		return this.task_time.getEndTime();
	}

	public Calendar getDay() {
		return day;
	}

	public void setDay(Calendar day) {
		this.day = day;
	}
}