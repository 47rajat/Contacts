package com.wssholmes.stark.contacts;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

class MainActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final int CONTACTS_PERMISSION_ID = 100;
    private static final int CONTACTS_LOADER_ID = 0;

    private static final String[] PROJECTION = new String[] { ContactsContract.RawContacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
            ContactsContract.CommonDataKinds.Email.DATA,
            ContactsContract.Contacts.LAST_TIME_CONTACTED,
            ContactsContract.Contacts.HAS_PHONE_NUMBER,
            ContactsContract.CommonDataKinds.Phone.NUMBER}; //TODO: find a way to get mobile number, this is not working.

    public static final int CONTACT_ID = 0;
    public static final int CONTACT_NAME = 1;
    public static final int CONTACT_PHOTO = 2;
    public static final int CONTACT_EMAIL = 3;
    public static final int CONTACT_LAST_CONTACTED= 4;
    public static final int CONTACT_HAS_PHONE_NUMBER = 5;
    public static final int CONTACT_PHONE_NUMBER = 6;



    private static final String FILTER = ContactsContract.CommonDataKinds.Email.DATA
            + " NOT LIKE ''";

    String ORDER = "CASE WHEN "
            + ContactsContract.Contacts.DISPLAY_NAME
            + " NOT LIKE '%@%' THEN 1 ELSE 2 END, "
            + ContactsContract.Contacts.DISPLAY_NAME
            + ", "
            + ContactsContract.CommonDataKinds.Email.DATA
            + " COLLATE NOCASE";

    private RecyclerView mContactList;
    private ContactListAdapter mContactListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkContactPermission();
        mContactListAdapter = new ContactListAdapter(this);
        mContactList = (RecyclerView) findViewById(R.id.contact_list);
        mContactList.setLayoutManager(new GridLayoutManager(this, 2));
        mContactList.setAdapter(mContactListAdapter);
    }

    private void checkContactPermission(){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_CONTACTS},
                    CONTACTS_PERMISSION_ID);
        } else {
            getLoaderManager().initLoader(CONTACTS_LOADER_ID, null, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CONTACTS_PERMISSION_ID:
                if(grantResults.length > 0 && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED) {
                    getLoaderManager().initLoader(CONTACTS_LOADER_ID, null, this);
                } else {
                    Snackbar.make(findViewById(R.id.activity_main),
                            getString(R.string.permission_denied_message),
                            Snackbar.LENGTH_INDEFINITE).show();
                }
                break;
            default:
                Log.v(LOG_TAG, "Unknown permission requested");
        }
    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new android.content.CursorLoader(this,
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                PROJECTION,
                FILTER,
                null,
                ORDER);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        Log.v(LOG_TAG, "" + cursor.getCount());
        mContactListAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {

    }
}