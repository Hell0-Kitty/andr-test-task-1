package com.example.test;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.parsers.Menu;
import com.example.test.parsers.XMLTable;
import com.example.test.ui.RecycleAdapter;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    Menu map;
    RecycleAdapter recycleAdapter;
    RecyclerView recyclerView;
    Toolbar toolbar;
    DrawerLayout drawer;
    public static final String logTag = "workError";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initRecycleView();
        createMenu();
    }

    private void initRecycleView(){
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recycleAdapter = new RecycleAdapter();
        recyclerView.setAdapter(recycleAdapter);
    } //создание RecycleView

    private void createMenu(){ // создание меню
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        buildMenu(navigationView.getMenu());
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                onSelectMenu(menuItem);
                return true;
            }
        });
        onSelectMenu(navigationView.getMenu().getItem(0));
    }

    private void onSelectMenu(MenuItem menuItem){
        recycleAdapter.clearItemList();
        recyclerView.setAdapter(recycleAdapter);
        getSupportActionBar().setTitle(menuItem.getTitle());
        drawer.closeDrawers();
        for (Menu.MenuItem item : map.getMenuItems())
            if (item.getName().contentEquals(menuItem.getTitle()) && !item.getUrl().isEmpty())
                loadItems(item.getUrl());
    }

    private void loadItems(String url){
        try {
            recycleAdapter.setItemList(new XMLTable(url, this).getElementList());
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка загрузки данных", Toast.LENGTH_LONG).show();
            Log.d(logTag, e.getMessage());
        }
    }

    private void buildMenu(android.view.Menu menu){
        try {
            map = new Menu(getResources().getXml(R.xml.menu));
            if (map.isParse())
                for (Menu.MenuItem item : map.getMenuItems())
                    if (item.isVisible())
                        menu.add(item.getName());
        } catch (Exception e){
            Log.d(logTag, e.getMessage());
        }
    } // генерация меню
}
