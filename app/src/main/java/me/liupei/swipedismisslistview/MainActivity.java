package me.liupei.swipedismisslistview;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
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
    private List<Integer> data = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        fillData();
        setContentView(listView = new SwipeDismissListView(this));
        listView.setBackgroundColor(Color.BLACK);
        final MyAdapter adapter = new MyAdapter(data);

        TextView header0 = new TextView(this);
        header0.setText("Header0");
        header0.setBackgroundColor(Color.GREEN);
        header0.setTextSize(50);
        listView.addHeaderView(header0);

        TextView header1 = new TextView(this);
        header1.setText("Header1");
        header1.setBackgroundColor(Color.GREEN);
        header1.setTextSize(50);
        listView.addHeaderView(header1);

        TextView header2 = new TextView(this);
        header2.setText("Header2");
        header2.setBackgroundColor(Color.GREEN);
        header2.setTextSize(50);
        listView.addHeaderView(header2);

        TextView footer0 = new TextView(this);
        footer0.setText("Footer0");
        footer0.setBackgroundColor(Color.RED);
        footer0.setTextSize(50);
        listView.addFooterView(footer0);

        TextView footer1 = new TextView(this);
        footer1.setText("Footer1");
        footer1.setBackgroundColor(Color.RED);
        footer1.setTextSize(50);
        listView.addFooterView(footer1);


        listView.setAdapter(adapter);
        listView.setDivider(new ColorDrawable(Color.BLUE));
        listView.setDividerHeight(2);
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

        private static class ViewHolder{

            final TextView tv;

            public ViewHolder(View convertView){
                tv = (TextView) convertView.findViewById(R.id.tv);
            }

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

            ViewHolder viewHolder = null;

            if(convertView == null) {

                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_type_a, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.tv.setText(getItem(position) + "");
            viewHolder.tv.setTextSize(50);
            viewHolder.tv.setBackgroundColor(Color.WHITE);
            viewHolder.tv.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "onClick:" + getItem(position), Toast.LENGTH_SHORT).show();
                }
            });
            return convertView;
        }
    }

    private void fillData() {
        for (int i = 0; i < 15; i++) {
            data.add(i);
        }
    }
}
