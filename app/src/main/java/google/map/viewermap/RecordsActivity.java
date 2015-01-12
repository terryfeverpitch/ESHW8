package google.map.viewermap;

import google.map.viewermap.ui.MapView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class RecordsActivity extends Activity {
    private static final int VIEWER_MODE = 0;
    private static final int DELETE_MODE = 1;

    private ArrayList<MapView.Place> records;
	private ListView records_lv_display;
    private TextView records_tv_mode;

    private SimpleAdapter adapter;
    private boolean[] selectedArray;
    private int LIST_MODE = VIEWER_MODE;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_records);
		
		records_lv_display = (ListView) findViewById(R.id.records_lv_display);
        records_tv_mode = (TextView) findViewById(R.id.records_tv_mode);

		Bundle bundle;
		bundle = this.getIntent().getExtras();
		records = (ArrayList<MapView.Place>) bundle.getSerializable("records_list");
		ArrayList<HashMap<String,String>> formatedList = new ArrayList<HashMap<String,String>>();
		
		for(MapView.Place place : records) {
			HashMap<String,String> item = new HashMap<String,String>();
			item.put("name", place.getName());
			item.put("location", place.getLocation());
			formatedList.add(item);
		}

        adapter = new SimpleAdapter(this, formatedList, android.R.layout.simple_list_item_2,
				new String[] { "name", "location" },  new int[] { android.R.id.text1, android.R.id.text2 } );


        selectedArray = new boolean[records.size()];
        Arrays.fill(selectedArray, false);

        records_lv_display.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		records_lv_display.setAdapter(adapter);
		records_lv_display.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if(LIST_MODE == VIEWER_MODE) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("currentIndex", position);

                    Intent intent = new Intent();
                    intent.setClass(RecordsActivity.this, MapActivity.class);
                    intent.putExtras(bundle);

                    MapView map_mv_map = (MapView) MapFragment.getRootView().findViewById(R.id.map_mv_map);
                    map_mv_map.getFocus(records.get(position));
                    finish();
                }
                else {
                    boolean checked = selectedArray[position];
                    if(checked)
                        view.setBackgroundColor(getResources().getColor(R.color.list_item_title));
                    else
                        view.setBackgroundColor(getResources().getColor(R.color.list_background_selected));
                    selectedArray[position] = !checked;
                }
            }
        });
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.records_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_viewer:
                LIST_MODE = VIEWER_MODE;
                refresh();
                return true;
            case R.id.action_delete:
                LIST_MODE = DELETE_MODE;
                doDelete();
                return true;
            case R.id.action_selectAll:
                LIST_MODE = DELETE_MODE;
                selectAll();
                return true;
            case R.id.action_deseletAll:
                LIST_MODE = DELETE_MODE;
                deselectAll();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    public void refresh() {
        ArrayList<HashMap<String,String>> formatedList = new ArrayList<HashMap<String,String>>();

        for(MapView.Place place : records) {
            HashMap<String,String> item = new HashMap<String,String>();
            item.put("name", place.getName());
            item.put("location", place.getLocation());
            formatedList.add(item);
        }

        adapter = new SimpleAdapter(this, formatedList, android.R.layout.simple_list_item_2,
                new String[] { "name", "location" },  new int[] { android.R.id.text1, android.R.id.text2 } );

        records_lv_display.setAdapter(adapter);
        selectedArray = new boolean[records.size()];
        Arrays.fill(selectedArray, false);
    }

    public void doDelete() {
        ArrayList<String> list = new ArrayList<String>();
        for(int i = 0; i < selectedArray.length; i++) {
            if (selectedArray[i]) {
//                MapActivity.deleteFromDB(records.get(i).getName());
                list.add(records.get(i).getName());
                records.remove(i);
            }
        }

        if(list.isEmpty() == false)
            MapActivity.deleteFromDB((String[]) list.toArray(new String[0]));

        refresh();
    }

    public void selectAll() {

    }

    public void deselectAll() {

    }
}
