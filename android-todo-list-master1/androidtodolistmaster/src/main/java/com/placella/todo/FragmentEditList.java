package com.placella.todo;

import android.annotation.TargetApi;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Fragment for editing a list.
 */
public class FragmentEditList extends Fragment implements Savable {

    private Activity parent;
    private FragmentEditList.Callback callback;

    private Item item;
    private int mode;
    private float scale;

    private Intent extras;
    private int resultCode;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        refresh();
        if (RTL.requiresSupport()){
            return inflater.inflate(R.layout.rtl_fragment_editlist, container, false);
        }
        else {
            return inflater.inflate(R.layout.fragment_editlist, container, false);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        parent = activity;
        callback = (FragmentEditList.Callback)activity;
    }

    @Override
    @TargetApi(17)
    public void onStart() {
        super.onStart();
        scale = parent.getResources().getDisplayMetrics().density;

        extras = new Intent();

        Bundle bundle = getArguments();
        item = (Item) bundle.getSerializable("item");
        mode = bundle.getInt("action", 0);

        update(RESPONSE.CANCELLED);

        EditText et;
        et = (EditText) parent.findViewById(R.id.list_title);
        if (mode == REQUEST.EDIT) {
            et.setText(item.getName());
        }
        et.addTextChangedListener(new NoteTextWatcher(this));

        if (mode == REQUEST.EDIT) {
            TextView t = (TextView) parent.findViewById(R.id.heading);
            t.setText(R.string.edit_list);
        }

        refresh();

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
                callback.editListFinished(extras, resultCode, mode);
            }
        });
        b = (Button) parent.findViewById(R.id.cancel);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                callback.editListFinished(extras, resultCode, mode);
            }
        });
        b = (Button) parent.findViewById(R.id.add);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                final EditText input = new EditText(parent);
                // support for RTL languages
                if (RTL.requiresSupport()){
                    input.setGravity(Gravity.END);
                }
                else {
                    input.setTextDirection(View.TEXT_DIRECTION_LOCALE);
                }

                new Dialog_Confirm(parent, R.string.add_item, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String value = input.getText().toString();
                        List<Item> list = item.getListcontent();
                        list.add(
                                new Item(value, Item.NOTE)
                        );
                        refresh();
                    }
                }).addView(input).show();
            }
        });
    }

    @Override
    public void save() {
        TextView tv;
        tv = (TextView) parent.findViewById(R.id.list_title);
        item.setName(tv.getText().toString());
    }


    /**
     * Refreshes the list's contents.
     */
    @TargetApi(17)
    private void refresh() {

        if (item == null){
            return;
        }

        List<Item> list = item.getListcontent();
        LinearLayout l = (LinearLayout) parent.findViewById(R.id.listitems);
        l.removeAllViews();
        TextView t;
        if (list.size() > 0) {
            parent.findViewById(R.id.hint).setVisibility(View.GONE);
            l.setPadding(0, 10, 0, 10);
            Util.hr(l, parent);
            int count = 0;
            for (Item i : list) {
                LinearLayout il = new LinearLayout(parent);
                il.setOrientation(LinearLayout.HORIZONTAL);
                il.setLayoutParams(
                        new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                );

                t = new TextView(parent);
                t.setLayoutParams(
                        new TableLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                1
                        )
                );
                t.setText(i.getName());
                t.setTextSize(18);
                t.setPadding(
                        (int) (5 * scale + 0.5f),
                        (int) (12 * scale + 0.5f),
                        (int) (5 * scale + 0.5f),
                        (int) (12 * scale + 0.5f)
                );

                ImageButton ib = new ImageButton(parent);
                ib.setImageResource(R.drawable.ic_delete_small);
                ib.setTag(count);
                ib.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        List<Item> l = item.getListcontent();
                        l.remove(Integer.parseInt(arg0.getTag().toString()));
                        refresh();
                    }
                });
                ib.setPadding(0, 0, 0, 0);

                if (RTL.requiresSupport()){
                    t.setGravity(Gravity.END);
                    il.addView(ib);
                    il.addView(t);
                }
                else {
                    t.setTextDirection(View.TEXT_DIRECTION_LOCALE);
                    il.addView(t);
                    il.addView(ib);
                }

                l.addView(il);
                Util.hr(l, parent);

                count++;
            }
        } else {
            parent.findViewById(R.id.hint).setVisibility(View.VISIBLE);
        }
    }


    /**
     * Updates the state of the fragment.
     *
     * @param resultCode updated result code
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
        void editListFinished(Intent extras, int resultCode, int requestCode);

    }

}
