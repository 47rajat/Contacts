package com.wssholmes.stark.contacts;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by stark on 16/11/16.
 */

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ContactListAdapterViewHolder> {
    private static final String LOG_TAG = ContactListAdapter.class.getSimpleName();

    private Cursor mCursor;
    private final Context mContext;

    public ContactListAdapter(Context context){
        mContext = context;
    }


    @Override
    public ContactListAdapter.ContactListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(parent instanceof RecyclerView){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_item, parent, false);
            view.setFocusable(true);
            return new ContactListAdapterViewHolder(view);
        } else {
            throw new RuntimeException("Not bound to Recycler View");
        }
    }

    @Override
    public void onBindViewHolder(ContactListAdapter.ContactListAdapterViewHolder holder, int position) {

        mCursor.moveToPosition(position);

        if(mCursor != null){
            holder.mContactName.setText(mCursor.getString(MainActivity.CONTACT_NAME));
            holder.mContactEmail.setText(mCursor.getString(MainActivity.CONTACT_EMAIL));
            holder.mContactLastContact.setText(mContext.getString(R.string.contact_last_contact,
                    getDate(mCursor.getLong(MainActivity.CONTACT_LAST_CONTACTED))));
            if(mCursor.getString(MainActivity.CONTACT_PHOTO) != null) {
                Picasso.with(mContext)
                        .load(Uri.parse(mCursor.getString(MainActivity.CONTACT_PHOTO)))
                        .into(holder.mContactImage);
            } else{
                holder.mContactImage.setImageDrawable(mContext.getResources().
                        getDrawable(R.drawable.ic_account_box_black_24dp));
            }
        }

    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0: mCursor.getCount();
    }

    public void swapCursor(Cursor cursor){
        mCursor = cursor;
        notifyDataSetChanged();
    }

    public class ContactListAdapterViewHolder extends RecyclerView.ViewHolder{

        private TextView mContactName;
        private TextView mContactEmail;
        private ImageView mContactImage;
        private TextView mContactLastContact;

        public ContactListAdapterViewHolder(View itemView) {
            super(itemView);

            mContactName = (TextView) itemView.findViewById(R.id.contact_name);
            mContactEmail = (TextView) itemView.findViewById(R.id.contact_email);
            mContactImage = (ImageView) itemView.findViewById(R.id.contact_photo);
            mContactLastContact = (TextView) itemView.findViewById(R.id.contact_last_contact);
        }
    }

    private String getDate(long time) {
        if (time == 0){
            return mContext.getString(R.string.no_contact);
        }
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }
}
