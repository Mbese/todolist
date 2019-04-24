package com.example.todolist;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ToDoListView {
    private final static String SHARE_PREFERENCES_FILE = "itemsList";
    private final static String SHARED_PREFERENCES_KEY = "items_list";


    private ToDoListAdapter adapter;
    private EditText newItemEditText;
    private ArrayList<ItemsModel> itemsToDo = new ArrayList<>();
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView percentageText;
//    private ItemsListCache cache;
    private ActionMode actionMode;
    private ActionModeCallback actionModeCallback;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getApplicationContext().getSharedPreferences(SHARE_PREFERENCES_FILE, MODE_PRIVATE);

        percentageText = findViewById(R.id.percentage_text);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recycler_view);
        newItemEditText = findViewById(R.id.new_item_edit_text);
        Button button = findViewById(R.id.addItemButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });
        adapter = new ToDoListAdapter(itemsToDo, this, this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        itemsToDo.addAll(loadListFromSharedPreferences());
        recyclerView.setAdapter(adapter);
        refreshItems();

        adapter.setOnClickListener(new OnClickListener() {
            @Override
            public void onItemLongClick(View view, ItemsModel itemsModel, int position) {
                enableActionMode(position);
            }
        });

        actionModeCallback = new ActionModeCallback();
    }

    private Collection<? extends ItemsModel> loadListFromSharedPreferences() {
        String itemsListString = sharedPreferences.getString(SHARED_PREFERENCES_KEY, "");
        ItemsModel itemsModelArray[] = gson.fromJson(itemsListString, ItemsModel[].class);

        if(itemsModelArray != null){
            return Arrays.asList(itemsModelArray);
        }

        return new ArrayList<>();
    }

    private void enableActionMode(int position) {
        if(actionMode == null){
            actionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        adapter.toggleSelection(position);

        int count = adapter.getSelectedItemCount();

        if(count == 0){
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }

    }

    private void addItem() {
        String item = newItemEditText.getText().toString();
        ItemsModel model = new ItemsModel();
        if (!TextUtils.isEmpty(item)) {
            model.setItemDescription(item);
            model.setCompleted(false);

            itemsToDo.add(model);
            newItemEditText.setText("");
            refreshItems();
        } else {
            Toast.makeText(this, "Please enter valid item", Toast.LENGTH_LONG).show();
        }
    }

    private void refreshItems() {
        String itemsListString = gson.toJson(itemsToDo);
        adapter = new ToDoListAdapter(itemsToDo, this, this);
        recyclerView.setAdapter(adapter);

        adapter.setOnClickListener(new OnClickListener() {
            @Override
            public void onItemLongClick(View view, ItemsModel itemsModel, int position) {
                enableActionMode(position);
            }
        });

        updateProgressMaxItems(itemsToDo.size());

        editor = sharedPreferences.edit();
        editor.putString(SHARED_PREFERENCES_KEY, itemsListString);
        editor.apply();
    }

    private void updateProgressMaxItems(int size) {
        progressBar.setMax(size);
    }

    @Override
    public void onCheckChanged(String itemDescription) {
        int itemsCompleted = progressBar.getProgress() + 1;
        progressBar.setProgress(itemsCompleted);

        calculateItemsCompleted(itemsCompleted);

        updateItemStatus(itemDescription, true);
    }

    @Override
    public void onCheckBoxUnchecked(String itemDescription) {
        int itemsCompleted = progressBar.getProgress() - 1;
        progressBar.setProgress(itemsCompleted);

        calculateItemsCompleted(itemsCompleted);
        updateItemStatus(itemDescription, false);
    }

    private void updateItemStatus(String itemDescription, boolean isItemCompleted) {
//        for (ItemsModel itemsModel : cache.getItems()) {
//            if (itemDescription.equals(itemsModel.getItemDescription())) {
//                itemsModel.setCompleted(isItemCompleted);
//            }
//        }
    }

    private void calculateItemsCompleted(double itemsCompleted) {
        double temp = itemsCompleted / itemsToDo.size();
        int progressPercentage = (int) (temp * 100);
        String percentage = getString(R.string.percentage, String.valueOf(progressPercentage), "%");
        percentageText.setText(percentage);
    }

    private  class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            Tools.setSystemBarColor(MainActivity.this, R.color.colorPrimary);
            actionMode.getMenuInflater().inflate(R.menu.menu_delete, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            int id = menuItem.getItemId();

            if(id == R.id.action_delete){
                deleteItems();
                actionMode.finish();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            adapter.clearSelection();
            actionMode = null;

            Tools.setSystemBarColor(MainActivity.this, R.color.colorPrimaryDark);
        }
    }

    private void deleteItems(){
        List<Integer> selectedItemsPositions = adapter.getSelectedItems();

        for(int i = selectedItemsPositions.size() - 1; i >= 0; i--){
            adapter.removeData(selectedItemsPositions.get(i));
        }

        refreshItems();
        adapter.notifyDataSetChanged();
    }
}
