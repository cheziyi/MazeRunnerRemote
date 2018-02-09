package ntu.cz3004.mazerunnerremote;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import ntu.cz3004.mazerunnerremote.fragments.CheckC1Fragment;
import ntu.cz3004.mazerunnerremote.fragments.CheckC3Fragment;
import ntu.cz3004.mazerunnerremote.fragments.CheckC567Fragment;
import ntu.cz3004.mazerunnerremote.fragments.CheckC8Fragment;
import ntu.cz3004.mazerunnerremote.fragments.ManualFragment;
import ntu.cz3004.mazerunnerremote.fragments.OnFragmentViewCreatedListener;
import ntu.cz3004.mazerunnerremote.fragments.TraverseFragment;
import ntu.cz3004.mazerunnerremote.managers.BluetoothManager;

import static ntu.cz3004.mazerunnerremote.managers.BluetoothManager.bt;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnFragmentViewCreatedListener {

    private static final int ACCESS_LOCATION_REQUEST_CODE = 1;

    private DrawerLayout drawer;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        replaceFragment(new TraverseFragment(), "0");

        bt = new BluetoothSPP(getApplicationContext());

        if (!bt.isBluetoothAvailable()) {
            Toast.makeText(this
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
        }

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext()
                        , "Connected to " + name + "\n" + address
                        , Toast.LENGTH_SHORT).show();
            }

            public void onDeviceDisconnected() {
                Toast.makeText(getApplicationContext()
                        , "Connection lost", Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() {
                Toast.makeText(getApplicationContext()
                        , "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void onDestroy() {
        super.onDestroy();
        bt.stopService();
    }


    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) {
            BluetoothManager.setBtEnabled(true, this);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
            }
        }
    }


    @Override
    public void onBackPressed() {
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_traverse:
                replaceFragment(new TraverseFragment(), "0");
                break;
            case R.id.nav_manual:
                replaceFragment(new ManualFragment(), "1");
                break;
            case R.id.nav_check_c1:
                replaceFragment(new CheckC1Fragment(), "c1");
                break;
            case R.id.nav_check_c2:
                //check for location permission
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    startBlutoothIntent();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},ACCESS_LOCATION_REQUEST_CODE);
                }
                break;
            case R.id.nav_check_c3:
                replaceFragment(new CheckC3Fragment(), "c3");
                break;
            case R.id.nav_check_c567:
                replaceFragment(new CheckC567Fragment(), "c5, c6, c7");
                break;
            case R.id.nav_check_c8:
                replaceFragment(new CheckC8Fragment(), "c8");
                break;

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startBlutoothIntent(){
        Intent intent = new Intent(this, ConnectBluetoothActivity.class);
        startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ACCESS_LOCATION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startBlutoothIntent();
                } else {
                    Toast.makeText(getApplicationContext(), "Please give location permission in the setting.", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
            } else {
                Toast.makeText(this
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void replaceFragment(Fragment fragment, String TAG) {

        FragmentManager manager = getSupportFragmentManager();
        if (!manager.popBackStackImmediate(TAG, 0)) {
            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            fragmentTransaction.replace(R.id.content_container, fragment, TAG);
            fragmentTransaction.addToBackStack(TAG);
            fragmentTransaction.commit();
        }

    }

    @Override
    public void onViewCreated(int id) {
        navigationView.setCheckedItem(id);
    }
}
