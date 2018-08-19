package com.thesisproject.fikri.messengers;

import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.thesisproject.fikri.messengers.contacts.ContactDetails;
import com.thesisproject.fikri.messengers.databases.DatabaseHandler;
import com.thesisproject.fikri.messengers.mainfragments.AboutFragment;
import com.thesisproject.fikri.messengers.mainfragments.AddFragment;
import com.thesisproject.fikri.messengers.mainfragments.DeleteFragment;
import com.thesisproject.fikri.messengers.mainfragments.SettingFragment;
import com.thesisproject.fikri.messengers.mainfragments.UnhideFragment;
import com.thesisproject.fikri.messengers.mainfragments.homefragments.HomeFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String myHope, pack;
    Toolbar toolbar;
    Intent startMain;
    DrawerLayout drawer;
    public static String def;
    DatabaseHandler dbHandler;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    public static boolean HIDE_MODE = true;
    FragmentTransaction fragmentTransaction;
    Fragment homeFragment, unhideFragment, addFragment ,deleteFragment ,aboutFragment, settingFragment, contactDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settingFragment = new SettingFragment();
        homeFragment = new HomeFragment();
        unhideFragment = new UnhideFragment();
        addFragment = new AddFragment();
        deleteFragment = new DeleteFragment();
        aboutFragment = new AboutFragment();
        contactDetails = new ContactDetails();
        dbHandler = new DatabaseHandler(this);
        def = Telephony.Sms.getDefaultSmsPackage(this);
        pack = this.getApplicationContext().getPackageName();
        if (!def.equals(pack)) {
            Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,pack);
            startActivity(intent);
        }

        if (!HIDE_MODE) {
            dbHandler.updateMode("hidden");
            HIDE_MODE = true;
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, homeFragment);
        fragmentTransaction.commit();

        if (dbHandler.chekcHideMode("unhidden")) {
            toolbar.setTitle("Messenger");
            toggle.setDrawerIndicatorEnabled(false);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(!homeFragment.isResumed()) {
            if (contactDetails.isResumed()) {
                movePage("Add", addFragment);
            } else {
                movePage("Home", homeFragment);
            }
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            movePage("Home", homeFragment);
        } else if (id == R.id.nav_unhide) {
            movePage("Unhide", unhideFragment);
        } else if (id == R.id.nav_add) {
            movePage("Add", addFragment);
        } else if (id == R.id.nav_delete) {
            movePage("Delete", deleteFragment);
        } else if (id == R.id.nav_setting) {
            movePage("Setting", settingFragment);
        } else if (id == R.id.nav_about) {

            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.fragment_about);
            dialog.setTitle("About");

            dialog.show();
        } else if (id == R.id.nav_close) {
            exitApp();
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void movePage(String namePage, Fragment nameFragment) {
        //Handle moving page and change toolbar title
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, nameFragment);
        fragmentTransaction.commit();

        toolbar.setTitle(namePage);
    }

    public void exitApp() {
        Intent intent = new Intent("CLOSE_ALL");
        this.sendBroadcast(intent);
        finish();

        dbHandler.updateMode("unhidden");


//        getPackageManager().setComponentEnabledSetting(
//                new ComponentName(this, "com.thesisproject.fikri.messengers.MainActivity"),
//                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        getPackageManager().setComponentEnabledSetting(
                new ComponentName(this, "com.thesisproject.fikri.messengers.MainActivity_Alias"),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        Intent appIntents = new Intent();
        appIntents.setComponent(new ComponentName(this, "com.thesisproject.fikri.messengers.MainActivity"));
        appIntents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(appIntents);

    }

//    public void entrance(String entering) {
//        HIDE_MODE = entering;
//        //Toast.makeText(this, "blebleblebble", Toast.LENGTH_LONG).show();
//    }

}
