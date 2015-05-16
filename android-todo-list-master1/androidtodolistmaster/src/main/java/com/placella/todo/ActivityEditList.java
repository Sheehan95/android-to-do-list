package com.placella.todo;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

/**
 * A wrapper activity for {@link FragmentEditList}.
 */
public class ActivityEditList extends Activity implements FragmentEditList.Callback {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editlist);

		FragmentEditList childFragment;

		// Programmatically creates an instance of FragmentEditList, sets its arguments to the
		// activity's intent, and then adds the fragment to the fragment container.
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		childFragment = new FragmentEditList();
		childFragment.setArguments(getIntent().getExtras());
		ft.add(R.id.fragment_editlist_container, childFragment);
		ft.commit();
	}


	@Override
	public void editListFinished(Intent extras, int resultCode, int requestCode){
		setResult(resultCode, extras);
		finish();
	}

}
