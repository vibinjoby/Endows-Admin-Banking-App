package com.endows.endowsadminapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.endows.endowsadminapp.callback.Callback;
import com.endows.endowsadminapp.helper.CommonHelper;
import com.endows.endowsadminapp.model.db.AccountDetails;
import com.endows.endowsadminapp.model.db.CardDetails;
import com.endows.endowsadminapp.model.db.Customers;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity implements TextWatcher {

    EditText firstName, lastName, address, dob, sinNumber, mobileNumber, emailId;
    Button submitBtn;
    Context context;
    DatePickerDialog picker;
    FirebaseDatabase database = null;
    DatabaseReference dbRef = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.create_account_activity);
        context = this;

        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference();

        final int customerId = 0001;
        final long creditCardNum = 8765433890135634L;
        final long debitCardNum =  1984738921372517L;
        final long savingsAccountNum = 5342745209L;
        final long chequingAccountNum = 7352910903L;

        firstName = findViewById(R.id.first_name_txt);
        firstName.addTextChangedListener(this);
        lastName = findViewById(R.id.last_name_txt);
        lastName.addTextChangedListener(this);
        address = findViewById(R.id.address_txt);
        address.addTextChangedListener(this);
        dob = findViewById(R.id.dob_txt);
        dob.addTextChangedListener(this);
        sinNumber = findViewById(R.id.sin_number_txt);
        sinNumber.addTextChangedListener(this);
        mobileNumber = findViewById(R.id.phone_number_txt);
        mobileNumber.addTextChangedListener(this);
        emailId = findViewById(R.id.email_txt);
        emailId.addTextChangedListener(this);
        submitBtn = findViewById(R.id.submitBtn);
        //Date picker
        dob.setInputType(InputType.TYPE_NULL);
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(CreateAccountActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                dob.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidEntries()) {
                    CommonHelper.isEmailAlreadyExists(new Callback() {
                        @Override
                        public void onCallback(boolean isDuplicateEmail) {
                            if(!isDuplicateEmail) {
                                CommonHelper.isMobileNumberAlreadyExists(new Callback() {
                                    @Override
                                    public void onCallback(boolean isDuplicatePhoneNum) {
                                        if(!isDuplicatePhoneNum) {
                                            dbRef.child("Customers").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    long count = dataSnapshot.getChildrenCount();
                                                    long custId = customerId + count;
                                                    long creditCardNumbr = creditCardNum + count;
                                                    long debitCardNumbr = debitCardNum + count;
                                                    long savingsAcctNum = savingsAccountNum + count;
                                                    long chequingAcctNum = chequingAccountNum + count;

                                                    final Customers customers = new Customers();
                                                    customers.setFirstName(firstName.getText().toString().trim());
                                                    customers.setLastName(lastName.getText().toString().trim());
                                                    customers.setAddress(address.getText().toString().trim());
                                                    customers.setDob(dob.getText().toString().trim());
                                                    customers.setSinNumber(sinNumber.getText().toString().trim());
                                                    customers.setPhoneNumber(mobileNumber.getText().toString().trim());
                                                    customers.setEmailId(emailId.getText().toString().trim());

                                                    customers.setCustomerId(String.valueOf(custId));

                                                    //Create credit and debit card for customer
                                                    List<CardDetails> cardsList = new ArrayList<>();
                                                    CommonHelper.createNewCreditCard(cardsList, creditCardNumbr);
                                                    CommonHelper.createNewDebitCard(cardsList, debitCardNumbr);

                                                    customers.setCardDetails(cardsList);

                                                    //Create savings and chequing account for customer
                                                    List<AccountDetails> accDetailsLst = new ArrayList<>();
                                                    CommonHelper.createNewChequingAccount(accDetailsLst, chequingAcctNum);
                                                    CommonHelper.createNewSavingsAccount(accDetailsLst, savingsAcctNum);

                                                    customers.setMobileAppPassword(CommonHelper.generateAppPassword(8));
                                                    customers.setAccountDetails(accDetailsLst);
                                                    customers.setIsFirstTimeLogin("Y");

                                                    //Update the changes in DB
                                                    Map<String, Object> updateMap = new HashMap<>();
                                                    updateMap.put("Customers/" + customers.getCustomerId(), customers);
                                                    dbRef.updateChildren(updateMap);
                                                    showAlertBox("Account Created Successfully", "Details sent to Customer's email successfully", android.R.drawable.ic_menu_save);

                                                    CommonHelper.sendWelcomeEmail(context, customers.getEmailId(), customers.getCardDetails(),customers.getMobileAppPassword());
                                                    reinitializeWidgets();
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }  else {
                                            //Duplicate Phone Numnber
                                            showAlertBox("Invalid Phone Number","This Phone number already exists in our database..Please use another",android.R.drawable.ic_dialog_alert);
                                        }
                                    }
                                }, dbRef, mobileNumber.getText().toString().trim());
                            } else {
                                //Duplicate Email
                                showAlertBox("Invalid Email","This Email already exists in our database..Please use another",android.R.drawable.ic_dialog_alert);
                            }
                        }
                    }, dbRef, emailId.getText().toString().trim());

                }
            }
        });
    }

    private void showAlertBox(String title, String message, int resId) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(android.R.string.ok, null)
                .setIcon(resId)
                .show();
    }

    private void reinitializeWidgets() {
        firstName.setText("");
        lastName.setText("");
        address.setText("");
        sinNumber.setText("");
        dob.setText("");
        mobileNumber.setText("");
        emailId.setText("");
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        firstName.setError(null);
        lastName.setError(null);
        address.setError(null);
        dob.setError(null);
        sinNumber.setError(null);
        mobileNumber.setError(null);
        emailId.setError(null);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private boolean isValidEntries() {
        if (firstName.getText().toString().isEmpty()) {
            firstName.setError("First name is required");
            return false;
        }
        if (lastName.getText().toString().isEmpty()) {
            lastName.setError("Last name is required");
            return false;
        }
        if (address.getText().toString().isEmpty()) {
            address.setError("Address is required");
            return false;
        }
        if (dob.getText().toString().isEmpty()) {
            dob.setError("Date of birth is required");
            return false;
        }
        if (sinNumber.getText().toString().isEmpty()) {
            sinNumber.setError("SIN Number is required");
            return false;
        } else if (sinNumber.getText().length() > 9 || sinNumber.getText().length() < 9) {
            sinNumber.setError("SIN Number should be maximum of 9 digits");
            return false;
        }
        if (mobileNumber.getText().toString().isEmpty()) {
            mobileNumber.setError("Mobile Number is required");
            return false;
        } else if (mobileNumber.getText().length() > 10 || mobileNumber.getText().length() < 10) {
            mobileNumber.setError("Phone number should be a maximum of 10 digits");
            return false;
        }
        if (emailId.getText().toString().isEmpty()) {
            emailId.setError("Email id is required");
            return false;
        } else if (!CommonHelper.isValidEmail(emailId.getText().toString())) {
            emailId.setError("Invalid Email");
            return false;
        }
        return true;
    }
}
