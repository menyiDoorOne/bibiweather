package com.bibiweather.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.bibiweather.android.db.City;
import com.bibiweather.android.db.County;
import com.bibiweather.android.db.Province;
import com.bibiweather.android.util.HttpUtil;
import com.bibiweather.android.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.bibiweather.android.ChooseAreaFragment.LEVEL_COUNTY;

//在这次APP中没有用到


public class SearchCityActivity extends AppCompatActivity {

    private SearchView mSearchView;
    private String search;
    private SearchView.SearchAutoComplete mSearchAutoComplete;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private Adapter searchAdapter;
    private List<String> dataList = new ArrayList<>();
    /**省列表*/
    private List<Province> provinceList;
    /**市列表*/
    private List<City> cityList;
    /**县列表*/
    private List<County> countyList;
    /**选中的省份*/
    private Province selectedProvince;
    /**选中的城市*/
    private City selectedCity;
    /**当前选中的级别*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_city);
        Toolbar toolbar = findViewById(R.id.toolbar);
        listView = findViewById(R.id.lv);
        /*adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);*/

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*queryCounties();*/
            }
        });



        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSearchAutoComplete.isShown()) {
                    try {
                        mSearchAutoComplete.setText("");
                        Method method = mSearchView.getClass().getDeclaredMethod("onCloseClicked");
                        method.setAccessible(true);
                        method.invoke(mSearchView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    finish();
                }
            }
        });



        /*// 监听搜索框文字变化
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {



                Cursor cursor = TextUtils.isEmpty(s) ? null : queryCounties(s);
                // 设置或更新ListView的适配器
                setAdapter(cursor);


                return false;
            }
        });*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);

        //通过MenuItem得到SearchView
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchAutoComplete = mSearchView.findViewById(R.id.search_src_text);
        mSearchView.setQueryHint("搜索城市名");

        //设置输入框提示文字样式
        mSearchAutoComplete.setHintTextColor(getResources().getColor(android.R.color.darker_gray));
        mSearchAutoComplete.setTextColor(getResources().getColor(android.R.color.background_light));
        mSearchAutoComplete.setTextSize(14);

        //设置搜索框有字时显示叉叉，无字时隐藏叉叉
        mSearchView.onActionViewExpanded();
        mSearchView.setIconified(true);


        return super.onCreateOptionsMenu(menu);
    }


    // 让菜单同时显示图标和文字
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }



    /**查询选中市内所有的县，直接从数据库查询
     */
    private Cursor queryCounties(String search){

        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(getFilesDir() + "bibi_weather.db", null);
        Cursor cursor = null;
        try {
        String querySql = "select * from tb_County where countyName like '%" + search + "%'";
        cursor = db.rawQuery(querySql, null);
    } catch (Exception e){
            e.printStackTrace();
    }

        /*countyList = DataSupport.where("countyName =?",search).find(County.class);
        if(countyList.size()>0){
            dataList.clear();
            for(County county:countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
        }else{


            Toast.makeText(SearchCityActivity.this,"没有该城市",Toast.LENGTH_SHORT).show();

        }*/
        return cursor;
    }

    private void setAdapter(Cursor cursor) {
        if (listView.getAdapter() == null) {
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(SearchCityActivity.this, R.layout.item_layout, cursor, new String[]{"countyName"}, new int[]{R.id.text1});
            listView.setAdapter(adapter);
        } else {
            ((SimpleCursorAdapter) listView.getAdapter()).changeCursor(cursor);
        }
    }




}


