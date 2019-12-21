package com.endows.endowsadminapp.model;

import com.endows.endowsadminapp.model.db.CardDetails;
import com.endows.endowsadminapp.model.db.TransactionHistory;

public class EmailTemplateDetails {
    private String templateName;
    private String senderEmailId;
    private String password;
    private boolean isCardTemplate;
    private boolean isOtpTemplate;
    private boolean isTransactionTemplate;
    private boolean isWelcomeTemplate;
    private CardDetails cardDetails;
    private TransactionHistory transactionHistory;

    public EmailTemplateDetails(String templateName, String senderEmailId, String password,
                                boolean isCardTemplate, boolean isOtpTemplate, boolean isTransactionTemplate, boolean isWelcomeTemplate, CardDetails cardDetails, TransactionHistory transactionHistory) {
        this.templateName = templateName;
        this.senderEmailId = senderEmailId;
        this.password = password;
        this.isCardTemplate = isCardTemplate;
        this.isOtpTemplate = isOtpTemplate;
        this.isTransactionTemplate = isTransactionTemplate;
        this.isWelcomeTemplate = isWelcomeTemplate;
        this.cardDetails = cardDetails;
        this.transactionHistory = transactionHistory;
    }

    public String getTemplateName() {
        return templateName;
    }

    public String getSenderEmailId() {
        return senderEmailId;
    }

    public String getPassword() {
        return password;
    }

    public boolean isCardTemplate() {
        return isCardTemplate;
    }

    public boolean isOtpTemplate() {
        return isOtpTemplate;
    }

    public boolean isTransactionTemplate() {
        return isTransactionTemplate;
    }

    public CardDetails getCardDetails() {
        return cardDetails;
    }

    public TransactionHistory getTransactionHistory() {
        return transactionHistory;
    }

    public boolean isWelcomeTemplate() {
        return isWelcomeTemplate;
    }

    @Override
    public String toString() {
        return "EmailTemplateDetails{" +
                "templateName='" + templateName + '\'' +
                ", senderEmailId='" + senderEmailId + '\'' +
                ", password='" + password + '\'' +
                ", isCardTemplate=" + isCardTemplate +
                ", isOtpTemplate=" + isOtpTemplate +
                ", isTransactionTemplate=" + isTransactionTemplate +
                ", isWelcomeTemplate=" + isWelcomeTemplate +
                ", cardDetails=" + cardDetails +
                ", transactionHistory=" + transactionHistory +
                '}';
    }
}
