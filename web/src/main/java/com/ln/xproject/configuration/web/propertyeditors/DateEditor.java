package com.ln.xproject.configuration.web.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.util.Date;

public class DateEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (text == null) {
            setValue(null);
        } else {
            setValue(new Date(Long.parseLong(text.trim())));
        }
    }

}
