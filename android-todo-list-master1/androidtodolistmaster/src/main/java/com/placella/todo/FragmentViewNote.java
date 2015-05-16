package com.placella.todo;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Fragment for viewing a note.
 */
public class FragmentViewNote extends Fragment  {

    private Activity parent;
    private FragmentViewNote.Callback callback;

    private Intent extras;
    private Item item;
    private int resultCode;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (RTL.requiresSupport()){
            return inflater.inflate(R.layout.rtl_fragment_viewnote, container, false);
        }
        else {
            return inflater.inflate(R.layout.fragment_viewnote, container, false);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        parent = activity;
        callback = (FragmentViewNote.Callback) activity;
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle bundle = getArguments();

        item = (Item) bundle.getSerializable("item");
        TextView t = (TextView) parent.findViewById(R.id.note_header);
        t.setText(item.getName());
        t = (TextView) parent.findViewById(R.id.content);
        t.setText(item.getNotecontent());

        update(RESPONSE.CANCELLED);

        Button b;
        b = (Button) parent.findViewById(R.id.back);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                callback.viewNoteFinished(extras, resultCode);
            }
        });
        b = (Button) parent.findViewById(R.id.edit_note);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putInt("action", REQUEST.EDIT);
                b.putSerializable("item", item);
                callback.editNote(b);
            }
        });
        b = (Button) parent.findViewById(R.id.delete_note);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Dialog_Confirm(parent, R.string.delete_confirmation, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        update(RESPONSE.DELETED);
                        callback.viewNoteFinished(extras, resultCode);
                    }
                }).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST.EDIT && resultCode == RESPONSE.MODIFIED) {
            item = (Item) intent.getSerializableExtra("item");
        } else if (requestCode == REQUEST.EDIT && resultCode == RESPONSE.DELETED) {
            item = (Item) intent.getSerializableExtra("item");
        }
        update(resultCode);
        callback.viewNoteFinished(extras, resultCode);
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
         */
        void viewNoteFinished(Intent extras, int resultCode);

        /**
         * Edits an existing note.
         *
         * @param extras arguments for which note to edit
         */
        void editNote(Bundle extras);

    }

}
