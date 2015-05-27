package egar.schedule;

import egar.schedule.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;

public class Calendar extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar);
		final CalendarView calendarView = (CalendarView) findViewById(R.id.calendar);
		
//		calendarView.setOnDateChangeListener(new OnDateChangeListener() {
//			
//			@Override
//			public void onSelectedDayChange(CalendarView view, int year, int month,
//					int dayOfMonth) {
//				Intent intent = new Intent();
//				intent.putExtra("selectedDay", dayOfMonth);
//				intent.putExtra("selectedMonth", month);
//				intent.putExtra("selectedYear", year);
//				Log.d("OnSelectedDateChange", String.valueOf(year) + "/" + String.valueOf(month) + "/" + String.valueOf(dayOfMonth));
//				setResult(RESULT_OK, intent);
//				finish();
//			}
//		});
//		calendarView.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				CalendarView calView = (CalendarView) v;
//				Log.d("!OnClick", v.toString());
//				Intent intent = new Intent();
//				intent.putExtra("selectedDate", calView.getDate());
//				setResult(RESULT_OK, intent);
//				finish();
//			}
//		});

		Button butOk = (Button) findViewById(R.id.buttonOK);
		Button butCancel = (Button) findViewById(R.id.buttonCancel);
		butOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("selectedDate", calendarView.getDate());
				setResult(RESULT_OK, intent);
				finish();			
			}
		});
		
		butCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setResult(RESULT_OK, null);
				finish();
			}
		});
	}

}
