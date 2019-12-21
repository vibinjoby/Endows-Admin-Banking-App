package com.endows.endowsadminapp.helper;

import android.content.Context;

import androidx.annotation.NonNull;

import com.endows.endowsadminapp.callback.Callback;
import com.endows.endowsadminapp.model.EmailTemplateDetails;
import com.endows.endowsadminapp.model.db.AccountDetails;
import com.endows.endowsadminapp.model.db.CardDetails;
import com.endows.endowsadminapp.model.db.Customers;
import com.endows.endowsadminapp.model.db.TransactionHistory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonHelper {
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static String generateRandomPinNumber() {
        Random rnd = new Random();
        int number = rnd.nextInt(9999);

        return String.format("%04d", number);
    }

    public static String generateRandomCVV() {
        Random rnd = new Random();
        int number = rnd.nextInt(999);

        return String.format("%03d", number);
    }

    public static void createNewCreditCard(List<CardDetails> cardsList, long creditCardNumbr) {
        com.endows.endowsadminapp.model.db.CardDetails creditCard = new CardDetails();
        creditCard.setCardNumber(String.valueOf(creditCardNumbr));
        creditCard.setCardType(1);
        creditCard.setCvv(generateRandomCVV());
        creditCard.setExpiryDate("09/23");
        creditCard.setPinNumber(generateRandomPinNumber());
        cardsList.add(creditCard);
    }

    public static void createNewDebitCard(List<CardDetails> cardsList, long debitCardNumbr) {
        com.endows.endowsadminapp.model.db.CardDetails debitCard = new CardDetails();
        debitCard.setCardNumber(String.valueOf(debitCardNumbr));
        debitCard.setCardType(2);
        debitCard.setCvv(generateRandomCVV());
        debitCard.setExpiryDate("09/23");
        debitCard.setPinNumber(generateRandomPinNumber());
        cardsList.add(debitCard);
    }

    public static void createNewChequingAccount(List<AccountDetails> accDetailsLst, long chequingAcctNum) {
        AccountDetails chequingAcc = new AccountDetails();
        chequingAcc.setAccountNumber(String.valueOf(chequingAcctNum));
        chequingAcc.setAccountType("1");
        chequingAcc.setAccountBalance("1000");
        chequingAcc.setCreditedAmt("1000");
        chequingAcc.setTransactionHistory(new ArrayList<TransactionHistory>());
        accDetailsLst.add(chequingAcc);
    }

    public static void createNewSavingsAccount(List<AccountDetails> accDetailsLst, long savingsAcctNum) {
        AccountDetails savingsAcc = new AccountDetails();
        savingsAcc.setAccountNumber(String.valueOf(savingsAcctNum));
        savingsAcc.setAccountType("2");
        savingsAcc.setAccountBalance("500");
        savingsAcc.setCreditedAmt("500");
        savingsAcc.setTransactionHistory(new ArrayList<TransactionHistory>());
        accDetailsLst.add(savingsAcc);
    }

    public static void sendWelcomeEmail(Context context, String emailId, List<CardDetails> cardDetailsList,String password) {
        EmailTemplateDetails welcomeTemplate = new EmailTemplateDetails("welcome.html", emailId, password, false, false, false, true, null, null);
        EmailHelper helper = new EmailHelper(context, welcomeTemplate);
        helper.execute("");

        for (CardDetails cardDetails : cardDetailsList) {
            String templateName = 1 == cardDetails.getCardType() ? "credit_email_template.html" : "debit_email_template.html";
            EmailTemplateDetails cardTemplate = new EmailTemplateDetails(templateName, emailId, null, true, false, false, false, cardDetails, null);
            EmailHelper cardEmailHelper = new EmailHelper(context, cardTemplate);
            cardEmailHelper.execute("");
        }
    }

    public static boolean isValidEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    public static void isEmailAlreadyExists(final Callback callback, DatabaseReference dbRef, final String newEmail) {
        dbRef.child("Customers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isDuplicateEmail = false;
                List<String> emailLst = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    try {
                        Customers customerObj = snap.getValue(Customers.class);
                        emailLst.add(customerObj.getEmailId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                for (String emailId : emailLst) {
                    if (emailId.equalsIgnoreCase(newEmail)) {
                        isDuplicateEmail = true;
                    }
                }
                callback.onCallback(isDuplicateEmail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void isMobileNumberAlreadyExists(final Callback callback, DatabaseReference dbRef, final String newPhoneNum) {
        dbRef.child("Customers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isDuplicatePhone = false;
                List<String> phoneLst = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    try {
                        Customers customerObj = snap.getValue(Customers.class);
                        phoneLst.add(customerObj.getPhoneNumber());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                for (String phoneNum : phoneLst) {
                    if (phoneNum.equalsIgnoreCase(newPhoneNum)) {
                        isDuplicatePhone = true;
                    }
                }
                callback.onCallback(isDuplicatePhone);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static String generateAppPassword(int numberOfDigits) {
        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        StringBuilder sb = new StringBuilder(numberOfDigits);

        for (int i = 0; i < numberOfDigits; i++) {
            int index = (int) (AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
    }
}
