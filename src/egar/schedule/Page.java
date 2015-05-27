package egar.schedule;

import egar.schedule.R;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class Page extends ListFragment {

	static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
	private static Context ctx;
	int pageNumber;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
		super.onCreate(savedInstanceState);
	}

	static Page newInstance(int page, Context context){
		Page pageFragment = new Page();
	    Bundle arguments = new Bundle();
	    arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
	    pageFragment.setArguments(arguments);
	    ctx = context;
	    return pageFragment;
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		TaskListAdapter adapter = new TaskListAdapter(ctx, MainActivity.schedules.get(pageNumber).date, MainActivity.schedules.get(pageNumber).schedule);
		setListAdapter(adapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.fragment, null);
	}
	
	@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			super.onListItemClick(l, v, position, id);
			Intent intent =  new Intent(ctx, TaskDetailed.class);
			intent.putExtra("position", position);
			intent.putExtra("pageNumber", pageNumber);
			startActivity(intent); 
		}
}
