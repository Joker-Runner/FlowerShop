package com.joker.flowershop.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.joker.flowershop.R;
import com.joker.flowershop.ui.fragment.HomeFragment;
import com.joker.flowershop.ui.fragment.ShoppingCartFragment;
import com.joker.flowershop.ui.qrcode.ScanActivity;
import com.joker.flowershop.utils.Constants;

/**
 * MainActivity
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    private ImageView nav_icon; // 头像
    private TextView userName; //用户名

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTheme(R.style.AppTheme_NoActionBar);
        this.getWindow().setBackgroundDrawableResource(R.drawable.main_activity_bg);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        userName = (TextView) headerView.findViewById(R.id.nav_user_name);
        nav_icon = (ImageView) headerView.findViewById(R.id.nav_icon);
        sharedPreferences = getSharedPreferences(Constants.INIT_SETTING_SHARED, MODE_APPEND);
        editor = sharedPreferences.edit();

        HomeFragment homeFragment = new HomeFragment();
        setMainContent(homeFragment);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sharedPreferences.getBoolean(Constants.LOGGED_IN, false)) {
            nav_icon.setImageResource(R.drawable.icon_default);
            userName.setText("Joker_Runner");
        } else {
            nav_icon.setImageResource(R.drawable.icon_none);
            userName.setText("登录/注册");
        }
        nav_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPreferences.getBoolean(Constants.LOGGED_IN, false)) {
//                    Toast.makeText(MainActivity.this, "已经登录", Toast.LENGTH_LONG).show();
                } else {
                    drawer.closeDrawer(GravityCompat.START);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivityForResult(intent, 0);
                        }
                    }, 200);
                }
            }
        });
    }

    /**
     * 设置 HomeActivity 的内容
     *
     * @param fragment 要呈现的 Fragment
     */
    public void setMainContent(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().
                replace(R.id.main_container, fragment).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 1:
                if (data.getBooleanExtra("logged_in", false)) {
                    nav_icon.setImageResource(R.drawable.icon_default);
                    userName.setText("Joker_Runner");
                    editor.putBoolean(Constants.LOGGED_IN, true).commit();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            Toast.makeText(this, "点击了搜索框", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        // Handle navigation view item clicks here.
        final int id = item.getItemId();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (id) {
                    case R.id.nav_home:
                        HomeFragment homeFragment = new HomeFragment();
                        setMainContent(homeFragment);
                        break;
                    case R.id.nav_subject:
                        break;
                    case R.id.nav_star:
                        break;
                    case R.id.nav_shopping_car:
                        if (sharedPreferences.getBoolean(Constants.LOGGED_IN, false)) {
                            ShoppingCartFragment shoppingCartFragment = new ShoppingCartFragment();
                            setMainContent(shoppingCartFragment);
                        } else {
                            Toast.makeText(MainActivity.this, "请先登录", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivityForResult(intent, 0);
                            item.setChecked(false);
                        }
                        break;
                    case R.id.nav_order:
                        break;
                    case R.id.nav_scan:
                        Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_settings:
                        Intent intent1 = new Intent(MainActivity.this, SettingActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.nav_help_feedback:
                        break;
                    case R.id.nav_share:
                        break;
                    default:
                        break;
                }
            }
        }, 200);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
