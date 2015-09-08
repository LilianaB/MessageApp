package com.bertha.messages;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.app.ActionBar;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.parse.ParseAnalytics;
import com.parse.ParseUser;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.datatype.Duration;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int PICK_PHOTO_REQUEST = 1;
    public static final int TAKE_VIDEO_REQUEST = 2;
    public static final int PICK_VIDEO_REQUEST = 3;

    public static final int MEDIA_TYPE_IMAGE = 4;
    public static final int MEDIA_TYPE_VIDEO = 5;

    protected Uri mMediaUri; //path to file in android system


    protected DialogInterface.OnClickListener mOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case 0://take pic
                    Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                    if (mMediaUri == null) {
                        //display error
                        Toast.makeText(MainActivity.this, R.string.error_external_storage, Toast.LENGTH_LONG).show();
                    } else {
                        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                        startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
                    }
                case 1://take video
                    Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
                    if (mMediaUri == null) {
                        //display error
                        Toast.makeText(MainActivity.this, R.string.error_external_storage, Toast.LENGTH_LONG).show();
                    } else {
                        videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                        videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);//10 sec
                        videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0); //lowest quality

                        startActivityForResult(videoIntent, TAKE_VIDEO_REQUEST);
                    }

                case 2: //Choose picture
                    break;
                case 3: //choose video
                    break;
            }
        }

        private Uri getOutputMediaFileUri(int mediaType) {
            // To be safe, you should check that the SDCard is mounted
            // using Environment.getExternalStorageState() before doing this.
            if (isExternalStorageAvailable()) {
                //1. get external storage directory
                String appName = MainActivity.this.getString(R.string.app_name);
                File mediaStorageDir = new File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        appName);
                //2. create our directory
                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()){
                        Log.e(TAG, "failed to create directory");
                    }
                }

                //3. create file name
                //4. create file
                File mediaFile;
                Date now = new Date();
                String timestammp= new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.GERMAN).format(now);

                String path = mediaStorageDir.getPath()+ File.separator;
                if (mediaType == MEDIA_TYPE_IMAGE) {
                    mediaFile = new File(path + "IMG_" + timestammp + ".jpg");
                } else if (mediaType == MEDIA_TYPE_VIDEO) {
                    mediaFile = new File(path + "VID_" + timestammp + ".mp4");
                } else {
                    return null;
                }

                Log.d(TAG, "File: "+Uri.fromFile(mediaFile) );

                //5. return file URI
                return Uri.fromFile(mediaFile);

            }

            return null;
        }

        private boolean isExternalStorageAvailable() {
            String state = Environment.getExternalStorageState();
            if (state.equals(Environment.MEDIA_MOUNTED)) {
                return true;
            }
            return false;

        }
    };

    

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            navegateToLogin();
        } else {
            Log.i(TAG, currentUser.getUsername());

        }
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        // Create a tab listener that is called when the user changes tabs.
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(this,
                getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager
                .setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        actionBar.setSelectedNavigationItem(position);
                    }
                });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(actionBar.newTab()
                    .setText(mSectionsPagerAdapter.getPageTitle(i))
                    .setTabListener(this));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            //successful - add gallery - tell gallery file is available
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(mMediaUri);
            sendBroadcast(mediaScanIntent);

        } else if (resultCode != RESULT_CANCELED) {
            Toast.makeText(MainActivity.this, R.string.general_camera_error, Toast.LENGTH_LONG );
            //change
        }
    }

    private void navegateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); //cannot nav back to main
        startActivity(intent);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_logout:
                ParseUser.logOut();
                navegateToLogin();
            case R.id.action_edit_friends:
                Intent intent = new Intent(this, EditFriendsActivity.class);
                startActivity(intent);
            case R.id.action_camera:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setItems(R.array.camera_choices, mOnClickListener);
                AlertDialog dialog = builder.create();
                dialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }
}
