package com.placella.todo;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

/**
 * Fragment for adding new notes & lists.
 */
public class FragmentAdd extends Fragment {

    private Activity parent;
    private FragmentAdd.Callback callback;
    private Item item;

    private Intent extras;
    private int resultCode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (RTL.requiresSupport()){
            return inflater.inflate(R.layout.rtl_fragment_add, container, false);
        }
        else {
            return inflater.inflate(R.layout.fragment_add, container, false);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        parent = activity;
        callback = (FragmentAdd.Callback) activity;
    }

    @Override
    public void onStart() {

        super.onStart();
        extras = new Intent();

        // setting "ok" button listener
        parent.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                RadioGroup r = (RadioGroup) parent.findViewById(R.id.type);

                Bundle bundle = new Bundle();
                bundle.putInt("action", REQUEST.ADD);

                if (r.getCheckedRadioButtonId() == R.id.note) {
                    item = new Item("", Item.NOTE);
                    bundle.putSerializable("item", item);
                    callback.addNote(bundle);
                } else {
                    item = new Item("", Item.LIST);
                    bundle.putSerializable("item", item);
                    callback.addList(bundle);
                }

            }
        });

        update(RESPONSE.CANCELLED);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST.EDIT && resultCode == RESPONSE.MODIFIED) {
            item = (Item) intent.getSerializableExtra("item");
        } else if (requestCode == REQUEST.ADD && resultCode == RESPONSE.ADDED) {
            item = (Item) intent.getSerializableExtra("item");
        }
        update(resultCode);
        callback.addFinished(extras, this.resultCode);
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
        void addFinished(Intent extras, int resultCode);

        /**
         * Adds a new note.
         *
         * @param extras arguments for creating the note
         */
        void addNote(Bundle extras);

        /**
         * Adds a new list.
         *
         * @param extras arguments for creating the list
         */
        void addList(Bundle extras);

    }

}
