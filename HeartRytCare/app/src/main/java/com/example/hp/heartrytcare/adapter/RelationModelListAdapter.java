package com.example.hp.heartrytcare.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.heartrytcare.R;
import com.example.hp.heartrytcare.db.RelationModel;
import com.example.hp.heartrytcare.db.UserFirebase;
import com.example.hp.heartrytcare.helper.Constants;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class RelationModelListAdapter extends ArrayAdapter<RelationModel> {

    private List<RelationModel> relationModelList;
    private List<UserFirebase> userRelatedList;
    private Context context;
    private VerificationRefresh verificationRefresh;

    public RelationModelListAdapter(@NonNull Context context, int resource, List<RelationModel> relationModelList, List<UserFirebase> userRelatedList) {
        super(context, resource);
        this.relationModelList = relationModelList;
        this.userRelatedList = userRelatedList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return relationModelList.size();
    }

    @Nullable
    @Override
    public RelationModel getItem(int position) {
        return relationModelList.get(position);
    }

    @Override
    public int getPosition(@Nullable RelationModel item) {
        return relationModelList.indexOf(item);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(this.context).inflate(R.layout.listview_relation_items, parent, false);
            viewHolder.patientName = (TextView) convertView.findViewById(R.id.patientName);
            viewHolder.patientNumber = (TextView) convertView.findViewById(R.id.patientNumber);
            viewHolder.verificationStatus = (TextView) convertView.findViewById(R.id.verificationStatus);
            viewHolder.verificationCode = (TextView) convertView.findViewById(R.id.vCode);
            viewHolder.refreshCode = (ImageButton) convertView.findViewById(R.id.resetVerificationCode);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        UserFirebase userFirebase;
        if (Constants.FIREBASE_USER_TYPE == Constants.TYPE_USER_PATIENT) {
            userFirebase = getUserInfo(this.relationModelList.get(position).doctorUID);
        } else {
            userFirebase = getUserInfo(this.relationModelList.get(position).patientUID);
        }
        if (userFirebase != null) {
            viewHolder.patientName.setText(userFirebase.last_name.toString().toUpperCase(Locale.getDefault()) + ", " + userFirebase.first_name);
            viewHolder.patientNumber.setText(userFirebase.contact_number);
            viewHolder.verificationStatus.setText((this.relationModelList.get(position).verified ? "CONFIRMED" : "UNCONFIRMED"));
            viewHolder.verificationStatus.setTextColor(
                    (this.relationModelList.get(position).verified ?
                            ContextCompat.getColor(context, R.color.green) :
                            ContextCompat.getColor(context, R.color.orange)
                    )
            );
            if (!this.relationModelList.get(position).verified) {
                if (isCodeValid(this.relationModelList.get(position).validity)) {
                    viewHolder.refreshCode.setVisibility(View.VISIBLE);
                    viewHolder.refreshCode.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(context, "refresh verification code...", Toast.LENGTH_SHORT).show();
                            if (verificationRefresh != null) {
                                verificationRefresh.onRefreshRequest(relationModelList.get(position));
                            }
                        }
                    });
                }
                viewHolder.verificationCode.setVisibility(View.VISIBLE);
                viewHolder.verificationCode.setText(this.relationModelList.get(position).verficationCode);
            }
        }

        return convertView;
    }

    ///////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////
    private UserFirebase getUserInfo(String patientUID) {
        for (UserFirebase userModel : this.userRelatedList) {
            if (userModel.firebase_user_id.equals(patientUID)) {
                return userModel;
            }
        }
        return null;
    }

    private boolean isCodeValid(long timestamp) {
        Date dt = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.setTime(dt);
        Date time = calendar.getTime();
        Log.d("isCodeValid", "isCodeValid: " + (time.getTime()-timestamp));
        return (time.getTime()-timestamp) > 300000;
    }

    ///////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    ///////////////////////////////////////////////////////////////////////////
    public void setVerificationRefreshRequest(VerificationRefresh verificationRefreshRequest) {
        this.verificationRefresh = verificationRefreshRequest;
    }

    ///////////////////////////////////////////////////////////////////////////
    // INTERFACE
    ///////////////////////////////////////////////////////////////////////////
    public interface VerificationRefresh{
        void onRefreshRequest(RelationModel model);
    }

    ///////////////////////////////////////////////////////////////////////////
    // STATIC CLASS
    ///////////////////////////////////////////////////////////////////////////
    static class ViewHolder {
        public TextView patientName;
        public TextView patientNumber;
        public TextView verificationStatus;
        public TextView verificationCode;
        public ImageButton refreshCode;
    }
}
