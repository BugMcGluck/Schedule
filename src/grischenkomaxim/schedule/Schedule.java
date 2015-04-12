package grischenkomaxim.schedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.graphics.Bitmap;

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
		String lastName;
		String firstName;
		String middleName;
		Post post = new Post();
		//Bitmap photo;
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
		//Bitmap photo;
		String address;
	}

	class Class {
		String fullName;
		String shortName;
		String faculty;
		String eduOrg;
		String city;
	}

	class Task_time {
		String startTime;
		String endTime;
		String description;
	}	
	
	public String getTaskFull() {
		return this.task.task_type.fullName + " " + this.task.fullName;
	}

	public String getTaskShort() {
		return this.task.task_type.shortName + " " + this.task.shortName;
	}
	
	public String getTaskName() {
		return this.task.fullName;
	}
		
	public void setTask(String taskFullName, String taskShortName, String taskTypeFullName, String taskTypeShortName) {
		this.task.fullName = taskFullName;
		this.task.shortName = taskShortName;
		this.task.task_type.fullName = taskTypeFullName;
		this.task.task_type.shortName = taskTypeShortName;
	}
	
	public String getTeacherFull() { //ФИО полностью
		if (this.teacher.middleName.isEmpty()){
			return this.teacher.lastName + " " + this.teacher.firstName;
		}else{
			return this.teacher.lastName + " " + this.teacher.firstName + " " + this.teacher.middleName;
		}
	}
	
	public String getTeacherShort() { //Фамилия и инициалы
		if (this.teacher.middleName.isEmpty()){
			return this.teacher.lastName + " " + this.teacher.firstName.charAt(0) + ".";
		}else{
			return this.teacher.lastName + " " + this.teacher.firstName.charAt(0) + ". " + this.teacher.middleName.charAt(0) + ".";
		}
	}

	public void setTeacher(String teacherFirstName, String teacherLastName, String teacherMiddleName, String postFullName, String postShortName) {
		this.teacher.firstName = teacherFirstName;
		this.teacher.lastName = teacherLastName;
		this.teacher.middleName = teacherMiddleName;
		this.teacher.post.fullName = postFullName;
		this.teacher.post.shortName = postShortName;
	}

	public String getPostFullName(){
		return this.teacher.post.fullName;
	}
	
	public String getPostShortName(){
		return this.teacher.post.shortName;
	}
	
	public String getRoom() {
		return "ауд." + this.room.name + ", корп." + this.room.building.name;
	}

	public void setRoom(String roomName, String buildingName, String buildingGeo, String buildingAddress) {
		this.room.name = roomName;
		this.room.building.name = buildingName;
		this.room.building.geo = buildingGeo;
		this.room.building.address = buildingAddress;
	}

	public String getClassFullName() {
		return this.clas.fullName;
	}
	
	public String getClassShortName() {
		return this.clas.shortName;
	}

	public void setClass(String classFullName, String classShortName) {
		this.clas.fullName = classFullName;
		this.clas.shortName = classShortName;
	}

	public String getTask_timeStart() {
		return this.task_time.startTime;
	}
	
	public String getTask_timeEnd() {
		return this.task_time.endTime;
	}

	public String getTask_timeDescription() {
		return this.task_time.description;
	}
	
	public void setTask_time(String start, String end, String description) {
		this.task_time.startTime = start;
		this.task_time.endTime = end;
		this.task_time.description = description;
	}

	public Calendar getDay() {
		return day;
	}

	public void setDay(Calendar day) {
		this.day = day;
	}
}