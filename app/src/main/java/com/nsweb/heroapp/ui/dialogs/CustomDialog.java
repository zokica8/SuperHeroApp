package com.nsweb.heroapp.ui.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.nsweb.heroapp.R;

public class CustomDialog extends AlertDialog {

    private String areYouSureText;
    private String negativeButtonText;
    private String positiveButtonText;
    private PositiveButtonClickListener onPositiveButtonClickListener;
    private NegativeButtonClickListener onNegativeButtonClickListener;

    public CustomDialog(Context context, String areYouSureText, String negativeButtonText, String positiveButtonText) {
        super(context);
        setCancelable(false);
        this.areYouSureText = areYouSureText;
        this.negativeButtonText = negativeButtonText;
        this.positiveButtonText = positiveButtonText;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.delete_superhero_dialog, null, false);
        initializeView(view);
        setView(view);
    }

    private void initializeView(View view) {
        createPositiveButton(view);
        createNegativeButton(view);

        TextView dialogText = view.findViewById(R.id.dialog_text);
        dialogText.setText(areYouSureText);
    }

    private void createPositiveButton(View view) {
        TextView positiveButton = view.findViewById(R.id.yes_button);
        positiveButton.setText(positiveButtonText);
        positiveButton.setOnClickListener(v -> {
            if(onPositiveButtonClickListener != null) {
                onPositiveButtonClickListener.onPositiveButtonClick();
            }
            dismiss();
        });
    }

    private void createNegativeButton(View view) {
        TextView negativeButton = view.findViewById(R.id.no_button);
        if(negativeButtonText.equals("")) {
            negativeButton.setVisibility(View.GONE);
        }
        else {
            negativeButton.setText(negativeButtonText);
            negativeButton.setOnClickListener(v -> {
                if(onNegativeButtonClickListener != null) {
                    onNegativeButtonClickListener.onNegativeButtonClick();
                }
                dismiss();
            });
        }
    }

    public interface PositiveButtonClickListener {
        void onPositiveButtonClick();
    }

    public interface NegativeButtonClickListener {
        void onNegativeButtonClick();
    }

    public void setOnPositiveButtonClickListener(PositiveButtonClickListener onPositiveButtonClickListener) {
        this.onPositiveButtonClickListener = onPositiveButtonClickListener;
    }

    public void setOnNegativeButtonClickListener(NegativeButtonClickListener onNegativeButtonClickListener) {
        this.onNegativeButtonClickListener = onNegativeButtonClickListener;
    }
}
