package com.placella.todo;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

/**
 * A wrapper activity for {@link FragmentAdd}.
 */
public class ActivityAdd extends Activity implements FragmentAdd.Callback {

	private FragmentAdd childFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);

		// Programmatically creates an instance of FragmentAdd, sets its arguments to the activity's
		// intent, and then adds the fragment to the fragment container.
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		childFragment = new FragmentAdd();
		childFragment.setArguments(getIntent().getExtras());
		ft.add(R.id.fragment_add_container, childFragment);
		ft.commit();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		childFragment.onActivityResult(requestCode, resultCode, data);
	}


	@Override
	public void addFinished(Intent extras, int resultCode){
		setResult(resultCode, extras);
		finish();
	}

	@Override
	public void addNote(Bundle extras){
		Intent intent = new Intent(this, ActivityEditNote.class);
		intent.putExtras(extras);
		startActivityForResult(intent, REQUEST.ADD);
	}

	@Override
	public void addList(Bundle extras){
		Intent intent = new Intent(this, ActivityEditList.class);
		intent.putExtras(extras);
		startActivityForResult(intent, REQUEST.ADD);
	}

}
