package com.placella.todo;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Fragment for editing notes.
 */
public class FragmentEditNote extends Fragment implements Savable {

    private Activity parent;
    private FragmentEditNote.Callback callback;

    private Intent extras;
    private Item item;
    private int resultCode;
    private int mode;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (RTL.requiresSupport()){
            return inflater.inflate(R.layout.rtl_fragment_editnote, container, false);
        }
        else {
            return inflater.inflate(R.layout.fragment_editnote, container, false);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        parent = activity;
        callback = (FragmentEditNote.Callback) activity;
    }

    @Override
    public void onStart() {
        super.onStart();

        extras = new Intent();

        Bundle bundle = getArguments();
        item = (Item) bundle.getSerializable("item");
        mode = bundle.getInt("action", 0);

        update(RESPONSE.CANCELLED);

        EditText et;
        et = (EditText) parent.findViewById(R.id.note_title);
        if (mode == REQUEST.EDIT) {
            et.setText(item.getName());
        }
        et.addTextChangedListener(new NoteTextWatcher(this));
        et = (EditText) parent.findViewById(R.id.content);
        if (mode == REQUEST.EDIT) {
            et.setText(item.getNotecontent());
        }
        et.addTextChangedListener(new NoteTextWatcher(this));

        if (mode == REQUEST.ADD) {
            TextView t = (TextView) parent.findViewById(R.id.heading);
            t.setText(R.string.add_note);
        }

        Button b;
        b = (Button) parent.findViewById(R.id.ok);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (mode == REQUEST.ADD) {
                    update(RESPONSE.ADDED);
                } else {
                    update(RESPONSE.MODIFIED);
                }
                callback.editNoteFinished(extras, resultCode, mode);
            }
        });
        b = (Button) parent.findViewById(R.id.cancel);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                callback.editNoteFinished(extras, resultCode, mode);
            }
        });
    }

    @Override
    public void save() {
        EditText et;
        et = (EditText) parent.findViewById(R.id.note_title);
        item.setName(et.getText().toString());
        TextView tv;
        tv = (TextView) parent.findViewById(R.id.content);
        item.setNotecontent(tv.getText().toString());
    }


    /**
     * Updates the state of the fragment.
     *
     * @param resultCode the result code
     */
    private void update(int resultCode) {
        extras = new Intent();
        extras.putExtra("item", item);
        this.resultCode = resultCode;
    }


    /**
     * Callback interface to allow fragment communication.
     */
    public interface Callback {

        /**
         * Indicates the fragment has finished running.
         *
         * @param extras the resulting Intent of the fragment
         * @param resultCode the result code
         * @param requestCode the initial request code
         */
        void editNoteFinished(Intent extras, int resultCode, int requestCode);

    }

}
