package com.kpohto.venuesearch;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Simple text change listener that reports changes to presenter
 */
public class QueryChangeListener implements TextWatcher {

    VenuePresenter presenter;

    public QueryChangeListener(VenuePresenter pre) {
        presenter = pre;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    /**
     * This method only calls presenter to act on query text change
     * @param s New text
     */
    @Override
    public void afterTextChanged(Editable s) {
        if (presenter != null) {
            presenter.queryChanged(s.toString());
        }
    }
}
