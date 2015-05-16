package com.placella.todo;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

/**
 * A wrapper activity for {@link FragmentEditNote}.
 */
public class ActivityEditNote extends Activity implements FragmentEditNote.Callback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editnote);

        // Programmatically creates an instance of FragmentEditNote, sets its arguments to the
        // activity's intent, and then adds the fragment to the fragment container.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        FragmentEditNote childFragment = new FragmentEditNote();
        childFragment.setArguments(getIntent().getExtras());
        ft.add(R.id.fragment_editnote_container, childFragment);
        ft.commit();
    }


    @Override
    public void editNoteFinished(Intent extras, int resultCode, int requestCode){
        setResult(resultCode, extras);
        finish();
    }

}