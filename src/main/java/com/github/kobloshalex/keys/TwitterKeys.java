package com.github.kobloshalex.keys;

public enum TwitterKeys {
    API_KEY("guHeU1ifrfQMIwFSo2TI6ZGJg"),
    API_SECRET_KEY("5yPJV8LLpRBmqftctToKIeKXP6Xr2PEMJCzLvGbkPoRXPadpAa"),
    TOKEN("1345882447259570182-V6UcpjVxvn7E2VA28h8NE3vnNI0gm9"),
    SECRET("KapHWpQieLSs07CRbrK24i7b6JhYqxczdR7a7zOQxQdaM");

    private final String value;

    TwitterKeys(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
