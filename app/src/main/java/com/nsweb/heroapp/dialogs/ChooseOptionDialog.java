package com.nsweb.heroapp.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nsweb.heroapp.R;

public class ChooseOptionDialog {

    private final Context context;

    private String chooseOptionText;

    private OnGalleryButtonClickListener onGalleryButtonClickListener;

    private OnCameraButtonClickListener onCameraButtonClickListener;

    private AlertDialog alertDialog;

    public ChooseOptionDialog(Context context, String chooseOptionText) {
        this.context = context;
        this.chooseOptionText = chooseOptionText;
        createDialog();
    }

    private void createDialog() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.choose_option_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        builder.setCancelable(false);

        setOptionsDialog(view);
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void setOptionsDialog(View view) {
        TextView optionText = view.findViewById(R.id.dialog_text);
        optionText.setText(chooseOptionText);
        ListView optionsList = view.findViewById(R.id.choose_option_list_view);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.dialog_options, android.R.layout.select_dialog_item);
        optionsList.setAdapter(adapter);
        optionsList.setOnItemClickListener((parent, view1, position, id) -> {
            switch (position) {
                case 0:
                    chooseGallery();
                    break;
                case 1:
                    chooseCamera();
                    break;
            }
        });
    }

    private void chooseGallery() {
        if(onGalleryButtonClickListener != null) {
            onGalleryButtonClickListener.onGalleryButtonClick();
        }
        alertDialog.dismiss();
    }

    private void chooseCamera() {
        if(onCameraButtonClickListener != null) {
            onCameraButtonClickListener.onCameraButtonClick();
        }
        alertDialog.dismiss();
    }

    public interface OnGalleryButtonClickListener {
        void onGalleryButtonClick();
    }

    public void setOnGalleryButtonClickListener(OnGalleryButtonClickListener onGalleryButtonClickListener) {
        this.onGalleryButtonClickListener = onGalleryButtonClickListener;
    }

    public interface OnCameraButtonClickListener {
        void onCameraButtonClick();
    }

    public void setOnCameraButtonClickListener(OnCameraButtonClickListener onCameraButtonClickListener) {
        this.onCameraButtonClickListener = onCameraButtonClickListener;
    }
}
