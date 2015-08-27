package me.liupei.swipedismisslistview;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SwipeDismissListView listView;
    private List<Integer> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        fillData();
        setContentView(listView = new SwipeDismissListView(this));
        listView.setBackgroundColor(Color.BLACK);
        final MyAdapter adapter = new MyAdapter(data);
        listView.setAdapter(adapter);
        listView.setOnDismissListener(new SwipeDismissListView.OnDismissListener() {
            @Override
            public void onDismiss(int position) {
                Toast.makeText(MainActivity.this, "onDismiss:" + position, Toast.LENGTH_SHORT).show();

                data.remove(position);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static class MyAdapter extends BaseAdapter {

        private List<Integer> data;

        public MyAdapter(List<Integer> data){
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Integer getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            TextView tv = new TextView(parent.getContext());
            tv.setText(getItem(position) + "");
            tv.setTextSize(50);
            tv.setBackgroundColor(Color.WHITE);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "onClick:" + getItem(position), Toast.LENGTH_SHORT).show();
                }
            });
            return tv;
        }
    }

    private void fillData() {
        for (int i = 0; i < 10; i++) {
            data.add(i);
        }
    }
}
