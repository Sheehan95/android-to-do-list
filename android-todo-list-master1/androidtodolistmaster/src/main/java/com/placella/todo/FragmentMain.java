package com.placella.todo;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * The main fragment.
 */
public class FragmentMain extends Fragment {

    public ToDoList todo;
    private final int dataListId = R.id.data_list;
    private Item currentItem;
    public List<Item> mainList;
    private Button syncButton;

    private ActivityMain parent;
    private FragmentMain.Callback callback;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (RTL.requiresSupport()){
            return inflater.inflate(R.layout.rtl_fragment_main, container, false);
        }
        else {
            return inflater.inflate(R.layout.fragment_main, container, false);
        }
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        parent = (ActivityMain) activity;
        callback = (FragmentMain.Callback) activity;
    }

    @Override
    public void onStart() {
        super.onStart();

        todo = new ToDoList(parent);

        parent.findViewById(R.id.buttonAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putInt("action", REQUEST.ADD);
                callback.add(b);
            }
        });


        parent.findViewById(R.id.buttonSync).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(parent, R.string.synchronise_toast, Toast.LENGTH_SHORT).show();
                Synchronise.start(parent, todo.getList());
            }
        });

        ScrollView scrollView = (ScrollView) parent.findViewById(R.id.scrollView);
        scrollView.removeAllViews();
        scrollView.addView(getList());

        syncButton = (Button)parent.findViewById(R.id.buttonSync);


        if (mainList.isEmpty()){
            syncButton.setVisibility(View.GONE);
        }
        else {
            syncButton.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        this.todo.close();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST.EDIT && resultCode == RESPONSE.MODIFIED) {
            Item item = (Item) intent.getSerializableExtra("item");
            if (todo.replace(item)) {
                refresh();
            } else {
                new Dialog_Message(parent, R.string.fatal_error).show();
            }
        } else if (requestCode == REQUEST.EDIT && resultCode == RESPONSE.DELETED) {
            Item item = (Item) intent.getSerializableExtra("item");
            if (todo.delete(item)) {
                refresh();
                callback.returnToDefault();
            } else {
                new Dialog_Message(parent, R.string.fatal_error).show();
            }
            callback.returnToDefault();
        } else if (requestCode == REQUEST.ADD && resultCode == RESPONSE.ADDED) {
            Item item = (Item) intent.getSerializableExtra("item");
            Log.e("Add", "Attempting to add " + item.toString());
            if (todo.add(item)) {
                refresh();
            } else {
                new Dialog_Message(parent, R.string.fatal_error).show();
            }
        } else if (resultCode == RESPONSE.CANCELLED || resultCode == RESPONSE.DELETED){
            callback.returnToDefault();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        parent.getMenuInflater().inflate(R.menu.context_main, menu);
        currentItem = todo.find(v.getTag());
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_view){
            viewItem(currentItem);
            return true;
        } else if(item.getItemId() == R.id.menu_edit){
            Bundle b = new Bundle();
            b.putInt("action", REQUEST.EDIT);
            b.putSerializable("item", currentItem);

            if (currentItem.getType() == Item.NOTE){
                callback.editNote(b);
            }
            else {
                callback.editList(b);
            }
            return true;
        } else if(item.getItemId() == R.id.menu_delete){
            new Dialog_Confirm(parent, R.string.delete_confirmation, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    todo.delete(currentItem);
                    refresh();
                    callback.returnToDefault();
                }
            }).show();
            return true;
        } else {
            return false;
        }
    }


    /**
     * Creates a LinearLayout containing all lists & notes on the system.
     *
     * @return a view containing all lists & notes
     */
    public LinearLayout getList() {
        TextView t;
        mainList = todo.getList();

        LinearLayout l = new LinearLayout(parent);
        l.setId(dataListId);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setLayoutParams(
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );

        if (mainList.size() > 0) {
            Util.hr(l, parent);
            for (Item i : mainList) {
                LinearLayout innerLayout = new LinearLayout(parent);
                innerLayout.setOrientation(LinearLayout.HORIZONTAL);
                innerLayout.setVerticalGravity(Gravity.CENTER_VERTICAL);
                innerLayout.setTag(i.getId());

                float scale = 0.0F;

                ImageView img = new ImageView(parent);
                if (i.getType() == Item.NOTE) {
                    img.setImageDrawable(getResources().getDrawable(R.drawable.ic_note));
                } else {
                    img.setImageDrawable(getResources().getDrawable(R.drawable.ic_list));
                }

                TextView b = new TextView(parent);
                String name = i.getName();
                if (name.length() == 0) {
                    name = getResources().getString(R.string.no_name);
                }
                b.setText(name);
                b.setTextSize(18);
                b.setPadding(
                        (int) (5 * scale + 0.5f),
                        (int) (12 * scale + 0.5f),
                        (int) (5 * scale + 0.5f),
                        (int) (12 * scale + 0.5f)
                );

                innerLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        viewItem(todo.find(arg0.getTag()));
                    }
                });
                registerForContextMenu(innerLayout);

                l.addView(innerLayout);

                if (RTL.requiresSupport()){
                    innerLayout.setHorizontalGravity(Gravity.END);
                    innerLayout.addView(b);
                    innerLayout.addView(img);
                }
                else {
                    innerLayout.addView(img);
                    innerLayout.addView(b);
                }



                Util.hr(l, parent);
            }
        } else {
            t = new TextView(parent);
            t.setText(R.string.no_items_found);
            l.addView(t);
        }
        return l;
    }

    /**
     * Refreshes the view of notes & lists.
     */
    public void refresh() {
        LinearLayout l = (LinearLayout) parent.findViewById(dataListId);
        l.removeAllViews();

        ScrollView s = new ScrollView(parent);
        s.setLayoutParams(
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );
        s.addView(getList());
        l.addView(s);
        if (mainList.isEmpty()) {
            syncButton.setVisibility(View.GONE);
        } else {
            syncButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Launches an activity to handle viewing the item.
     *
     * @param i the item to view
     */
    public void viewItem(Item i) {
        Bundle b = new Bundle();
        b.putInt("action", REQUEST.EDIT);
        b.putSerializable("item", i);

        if (i.getType() == Item.NOTE){
            callback.viewNote(b);
        }
        else {
            callback.viewList(b);
        }
    }


    /**
     * Callback interface to allow fragment communication.
     */
    public interface Callback {

        /**
         * Adds a new note or list.
         *
         * @param extras arguments to create the note or list
         */
        void add(Bundle extras);

        /**
         * Views a note.
         *
         * @param extras arguments for which note to view
         */
        void viewNote(Bundle extras);

        /**
         * Views a list.
         *
         * @param extras arguments for which list to view
         */
        void viewList(Bundle extras);

        /**
         * Edits an existing note.
         *
         * @param extras arguments for which note to edit
         */
        void editNote(Bundle extras);

        /**
         * Edits an existing list.
         *
         * @param extras arguments for which list to edit.
         */
        void editList(Bundle extras);

        /**
         * Indicates the display should return to the default state.
         */
        void returnToDefault();

    }

}
