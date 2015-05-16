package com.placella.todo;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

/**
 * Main activity and entry point of the application.
 */
public class ActivityMain extends Activity implements FragmentMain.Callback, FragmentAdd.Callback,
		FragmentEditList.Callback, FragmentEditNote.Callback, FragmentViewList.Callback,
		FragmentViewNote.Callback {

	private FragmentMain childFragment;

	private boolean isTablet;

	private FragmentTransaction transaction;


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		childFragment = (FragmentMain) getFragmentManager().findFragmentById(R.id.fragment_main);
		isTablet = findViewById(R.id.fragment_container) != null;

		if (isTablet){
			transaction = getFragmentManager().beginTransaction();
			transaction.replace(R.id.fragment_container, new FragmentPlaceholder());
			transaction.commit();
		}

	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	childFragment.onActivityResult(requestCode, resultCode, intent);
    }
	
	@Override
    public void onPause() {
        super.onPause();
    	childFragment.todo.close();
    }


	// FragmentMain.Callback Implementation
	@Override
	public void add(Bundle extras) {
		if (isTablet){
			FragmentAdd fragment = new FragmentAdd();
			fragment.setArguments(extras);
			transaction = getFragmentManager().beginTransaction();
			transaction.replace(R.id.fragment_container, fragment);
			transaction.commit();
		}
		else {
			Intent intent = new Intent(this, ActivityAdd.class);
			intent.putExtras(extras);
			startActivityForResult(intent, REQUEST.ADD);
		}
	}

	@Override
	public void viewNote(Bundle extras) {
		if (isTablet){
			FragmentViewNote fragment = new FragmentViewNote();
			fragment.setArguments(extras);
			transaction = getFragmentManager().beginTransaction();
			transaction.replace(R.id.fragment_container, fragment);
			transaction.commit();
		}
		else {
			Intent intent = new Intent(this, ActivityViewNote.class);
			intent.putExtras(extras);
			startActivityForResult(intent, REQUEST.EDIT);
		}
	}

	@Override
	public void viewList(Bundle extras) {
		if (isTablet){
			FragmentViewList fragment = new FragmentViewList();
			fragment.setArguments(extras);
			transaction = getFragmentManager().beginTransaction();
			transaction.replace(R.id.fragment_container, fragment);
			transaction.commit();
		}
		else {
			Intent intent = new Intent(this, ActivityViewList.class);
			intent.putExtras(extras);
			startActivityForResult(intent, REQUEST.EDIT);
		}
	}

	@Override
	public void editNote(Bundle extras) {
		if (isTablet){
			FragmentEditList fragment = new FragmentEditList();
			fragment.setArguments(extras);
			transaction = getFragmentManager().beginTransaction();
			transaction.replace(R.id.fragment_container, fragment);
			transaction.commit();
		}
		else {
			Intent intent = new Intent(this, ActivityEditNote.class);
			intent.putExtras(extras);
			startActivityForResult(intent, REQUEST.EDIT);
		}
	}

	@Override
	public void editList(Bundle extras) {
		if (isTablet){
			FragmentEditList fragment = new FragmentEditList();
			fragment.setArguments(extras);
			transaction = getFragmentManager().beginTransaction();
			transaction.replace(R.id.fragment_container, fragment);
			transaction.commit();
		}
		else {
			Intent intent = new Intent(this, ActivityEditList.class);
			intent.putExtras(extras);
			startActivityForResult(intent, REQUEST.EDIT);
		}
	}

	@Override
	public void returnToDefault(){
		if (isTablet){
			transaction = getFragmentManager().beginTransaction();
			transaction.replace(R.id.fragment_container, new FragmentPlaceholder());
			transaction.commit();
		}
	}


	// FragmentAdd.Callback Implementation
	public void addFinished(Intent extras, int resultCode){
		onActivityResult(REQUEST.ADD, resultCode, extras);
	}

	public void addNote(Bundle extras){
		transaction = getFragmentManager().beginTransaction();
		FragmentEditNote fragment = new FragmentEditNote();
		fragment.setArguments(extras);
		transaction.replace(R.id.fragment_container, fragment);
		transaction.commit();
	}

	public void addList(Bundle extras){
		transaction = getFragmentManager().beginTransaction();
		FragmentEditList fragment = new FragmentEditList();
		fragment.setArguments(extras);
		transaction.replace(R.id.fragment_container, fragment);
		transaction.commit();
	}


	// FragmentEditList.Callback Implementation
	public void editListFinished(Intent extras, int resultCode, int requestCode){
		onActivityResult(requestCode, resultCode, extras);

		if (resultCode != RESPONSE.CANCELLED && resultCode != RESPONSE.DELETED){
			Item item = (Item)extras.getSerializableExtra("item");
			childFragment.viewItem(item);
		}
	}


	// FragmentEditNote.Callback Implementation
	public void editNoteFinished(Intent extras, int resultCode, int requestCode){
		onActivityResult(requestCode, resultCode, extras);

		if (resultCode != RESPONSE.CANCELLED && resultCode != RESPONSE.DELETED){
			Item item = (Item)extras.getSerializableExtra("item");
			childFragment.viewItem(item);
		}
	}


	// FragmentViewList.Callback Implementation
	@Override
	public void viewListFinished(Intent extras, int resultCode) {
		onActivityResult(REQUEST.EDIT, resultCode, extras);
	}


	// FragmentViewNote.Callback Implementation
	public void viewNoteFinished(Intent extras, int resultCode){
		onActivityResult(REQUEST.EDIT, resultCode, extras);
	}

}
