package com.placella.todo;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

/**
 * A wrapper activity for {@link FragmentAdd}.
 */
public class ActivityViewNote extends Activity implements FragmentViewNote.Callback {

	private FragmentViewNote childFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewnote);

		// Programmatically creates an instance of FragmentViewNote, sets its arguments to the
		// activity's intent, and then adds the fragment to the fragment container.
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		childFragment = new FragmentViewNote();
		childFragment.setArguments(getIntent().getExtras());
		ft.add(R.id.fragment_viewnote_container, childFragment);
		ft.commit();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		childFragment.onActivityResult(requestCode, resultCode, data);
	}


	@Override
	public void viewNoteFinished(Intent extras, int resultCode){
		setResult(resultCode, extras);
		finish();
	}

	@Override
	public void editNote(Bundle extras){
		Intent intent = new Intent(this, ActivityEditNote.class);
		intent.putExtras(extras);
		startActivityForResult(intent, REQUEST.EDIT);
	}

}