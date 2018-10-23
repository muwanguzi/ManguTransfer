package com.mangu.transfer.util;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

/**
 * Created by FRANCIS on 29/04/2017.
 */

public class AutoCompleteTextView extends android.support.v7.widget.AppCompatAutoCompleteTextView {

    private static final String TAG = "AC";
    private ProgressBar loadingIndicator = null;


    public AutoCompleteTextView(Context var1) {
        super(var1);
    }

    public AutoCompleteTextView(Context var1, AttributeSet var2) {
        super(var1, var2);
    }

    public AutoCompleteTextView(Context var1, AttributeSet var2, int var3) {
        super(var1, var2, var3);
    }

    public void onFilterComplete(int var1) {
        if (this.loadingIndicator != null) {
            this.loadingIndicator.setVisibility(View.GONE);
        }

        super.onFilterComplete(var1);
    }

    protected void performFiltering(CharSequence var1, int var2) {
        if (this.loadingIndicator != null) {
            //this.loadingIndicator.setVisibility(0);
        }

        super.performFiltering(var1, var2);
    }

    protected void replaceText(CharSequence var1) {
    }

    public void setLoadingIndicator(ProgressBar var1) {
        this.loadingIndicator = var1;
    }

    public void showDropDown() {
        Rect var3 = new Rect();
        this.getWindowVisibleDisplayFrame(var3);
        int[] var4 = new int[]{0, 0};
        this.getLocationInWindow(var4);
        int var2 = var3.bottom - (var4[1] + this.getHeight());
        int var1 = var2;
        if (var2 < 100) {
            var1 = 100;
        }

        Log.d("AC", "show: display frame=" + var3 + "; anchorPos1=" + var4[1] + "; h=" + this.getHeight() + "; dist=" + var1);
        this.setDropDownHeight(var1);
        super.showDropDown();
    }
}
