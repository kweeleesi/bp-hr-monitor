package com.example.hp.heartrytcare.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.example.hp.heartrytcare.R;
import com.example.hp.heartrytcare.db.UserFirebase;

import java.util.ArrayList;
import java.util.List;


public class AddPatientDialogFragment extends DialogFragment implements View.OnClickListener{

    private AutoCompleteTextView autoCompleteTextView;
    private Button sendCode;
    private Button cancel;

    private List<UserFirebase> userDataList;
    private List<String> userNumberList;
    private ArrayAdapter<String> autoCompleteAdapter;
    private OnUserSelected onUserSelected;

    public static AddPatientDialogFragment newInstance(List<UserFirebase> fullUserDataList, OnUserSelected onUserSelected) {

        Bundle args = new Bundle();

        AddPatientDialogFragment fragment = new AddPatientDialogFragment()
                .setUserDataList(fullUserDataList)
                .setOnUserSelected(onUserSelected);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialogfragment_add_patient, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialize(view);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addPatientBtn:
                //send code
                if (onUserSelected != null) {
                    onUserSelected.onSelected(
                            getUserModel(autoCompleteTextView.getText().toString())
                    );
                }
                dismiss();
                break;
            case R.id.cancelBtn:
                dismiss();
                break;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////
    private void initialize(View v) {
        autoCompleteTextView = (AutoCompleteTextView) v.findViewById(R.id.autoCompleteTextView);
        sendCode = (Button) v.findViewById(R.id.addPatientBtn);
        cancel = (Button) v.findViewById(R.id.cancelBtn);

        sendCode.setOnClickListener(this);
        cancel.setOnClickListener(this);

        autoCompleteAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, userNumberList);
        autoCompleteTextView.setAdapter(autoCompleteAdapter);
        autoCompleteTextView.setThreshold(1);
    }

    private UserFirebase getUserModel(String number) {
        for (UserFirebase userFirebase : this.userDataList) {
            if (userFirebase.contact_number.equals(number)) {
                return userFirebase;
            }
        }
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    ///////////////////////////////////////////////////////////////////////////
    public AddPatientDialogFragment setUserDataList(List<UserFirebase> userDataList) {
        this.userDataList = userDataList;
        if (this.userNumberList == null) {
            this.userNumberList = new ArrayList<>();
        }
        this.userNumberList.clear();
        for (UserFirebase user : this.userDataList) {
            this.userNumberList.add(user.contact_number);
        }

        return this;
    }

    public AddPatientDialogFragment setOnUserSelected(OnUserSelected onUserSelected) {
        this.onUserSelected = onUserSelected;
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////
    // INTERFACE
    ///////////////////////////////////////////////////////////////////////////
    public interface OnUserSelected {
        void onSelected(UserFirebase userModel);
    }
}
