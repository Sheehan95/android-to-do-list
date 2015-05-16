package com.placella.todo;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

/**
 * A wrapper activity for {@link FragmentViewList}.
 */
public class ActivityViewList extends Activity implements FragmentViewList.Callback {

	private FragmentViewList childFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewlist);

		// Programmatically creates an instance of FragmentViewList, sets its arguments to the
		// activity's intent, and then adds the fragment to the fragment container.
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		childFragment = new FragmentViewList();
		childFragment.setArguments(getIntent().getExtras());
		ft.add(R.id.fragment_viewlist_container, childFragment);
		ft.commit();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		childFragment.onActivityResult(requestCode, resultCode, data);
	}


	@Override
	public void viewListFinished(Intent extras, int resultCode){
		setResult(resultCode, extras);
		finish();
	}

	@Override
	public void editList(Bundle extras){
		Intent intent = new Intent(this, ActivityEditList.class);
		intent.putExtras(extras);
		startActivityForResult(intent, REQUEST.EDIT);
	}

}
