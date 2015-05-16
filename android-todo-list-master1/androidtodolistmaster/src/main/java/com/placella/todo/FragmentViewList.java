package com.placella.todo;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

/**
 * Fragment for viewing a list.
 */
public class FragmentViewList extends Fragment {

    private Activity parent;
    private FragmentViewList.Callback callback;

    private Item item;
    private Intent extras;
    private int resultCode;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (RTL.requiresSupport()){
            return inflater.inflate(R.layout.rtl_fragment_viewlist, container, false);
        }
        else {
            return inflater.inflate(R.layout.fragment_viewlist, container, false);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        parent = activity;
        callback = (FragmentViewList.Callback) activity;
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle bundle = getArguments();

        item = (Item) bundle.getSerializable("item");
        TextView t = (TextView) parent.findViewById(R.id.list_header);
        t.setText(item.getName());

        TableLayout table = (TableLayout) parent.findViewById(R.id.content);
        int count = 0;
        for (Item i : item.getListcontent()) {
            TableRow tr = new TableRow(parent);
            CheckBox x = new CheckBox(parent);
            if (i.getState() == Item.CHECKED) {
                x.setChecked(true);
            }
            x.setTag(count);
            x.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton arg0, boolean newState) {
                    List<Item> list = item.getListcontent();
                    Item i = list.get(Integer.parseInt(arg0.getTag().toString()));
                    if (newState) {
                        i.setState(Item.CHECKED);
                    } else {
                        i.setState(Item.UNCHECKED);
                    }
                    item.setListcontent(list);
                    update(RESPONSE.MODIFIED);
                }
            });
            t = new TextView(parent);
            t.setPadding(10, 5, 10, 5);
            t.setText(i.getName());
            t.setTextSize(18);
            t.setGravity(Gravity.END);

            if (RTL.requiresSupport()){
                tr.setGravity(Gravity.END);
                tr.addView(t);
                tr.addView(x);
            }
            else {
                tr.addView(x);
                tr.addView(t);
            }

            table.addView(tr);
            count++;
        }

        update(RESPONSE.CANCELLED);

        Button b;
        b = (Button) parent.findViewById(R.id.back);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                callback.viewListFinished(extras, resultCode);
            }
        });
        b = (Button) parent.findViewById(R.id.edit_list);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putInt("action", REQUEST.EDIT);
                b.putSerializable("item", item);
                callback.editList(b);
            }
        });
        b = (Button) parent.findViewById(R.id.delete_list);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Dialog_Confirm(parent, R.string.delete_confirmation, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        update(RESPONSE.DELETED);
                        callback.viewListFinished(extras, resultCode);
                    }
                }).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST.EDIT && resultCode == RESPONSE.MODIFIED) {
            item = (Item) intent.getSerializableExtra("item");
            update(resultCode);
            callback.viewListFinished(extras, resultCode);
        } else if (requestCode == REQUEST.EDIT && resultCode == RESPONSE.DELETED) {
            item = (Item) intent.getSerializableExtra("item");
            update(resultCode);
            callback.viewListFinished(extras, resultCode);
        }
    }


    /**
     * Saves the current input data in order to later
     * pass it on to the calling activity
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
        void viewListFinished(Intent extras, int resultCode);

        /**
         * Edits an existing list.
         *
         * @param extras arguments for which list to edit
         */
        void editList(Bundle extras);

    }

}
