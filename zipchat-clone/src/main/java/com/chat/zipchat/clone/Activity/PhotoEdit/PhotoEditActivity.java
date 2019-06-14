package com.chat.zipchat.clone.Activity.PhotoEdit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.chat.zipchat.clone.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.SaveSettings;

import static com.chat.zipchat.clone.Common.BaseClass.PhotoDirectoryPath;

public class PhotoEditActivity extends AppCompatActivity implements View.OnClickListener, StickerBSFragment.StickerListener {

    ImageView imagePhotoEditBack, imagePhotoEditUndo, imagePhotoEditRedo,
            imagePhotoEditCrop, imagePhotoEditSticker, imagePhotoEditText, imagePhotoEditPaint;
    VerticalSlideColorPicker colorPickerView;
    PhotoEditorView photoEditorView;
    FloatingActionButton fabPhotoDone;

    private PhotoEditor mPhotoEditor;
    private StickerBSFragment mStickerBSFragment;
    private int mSelectedColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_edit);

        imagePhotoEditBack = findViewById(R.id.img_photo_edit_back);
        imagePhotoEditUndo = findViewById(R.id.img_photo_edit_undo);
        imagePhotoEditRedo = findViewById(R.id.img_photo_edit_redo);
        imagePhotoEditCrop = findViewById(R.id.img_photo_edit_crop);
        imagePhotoEditSticker = findViewById(R.id.img_photo_edit_stickers);
        imagePhotoEditText = findViewById(R.id.img_photo_edit_text);
        imagePhotoEditPaint = findViewById(R.id.img_photo_edit_paint);

        photoEditorView = findViewById(R.id.photo_editor_view);
        fabPhotoDone = findViewById(R.id.fab_photo_done);

        mPhotoEditor = new PhotoEditor.Builder(this, photoEditorView)
                .setPinchTextScalable(true)
                .build();

        colorPickerView = findViewById(R.id.color_picker_view);
        colorPickerView.setOnColorChangeListener(
                new VerticalSlideColorPicker.OnColorChangeListener() {
                    @Override
                    public void onColorChange(int selectedColor) {
                        mSelectedColor = selectedColor;
                        if (colorPickerView.getVisibility() == View.VISIBLE) {
                            imagePhotoEditPaint.setBackgroundColor(selectedColor);
                            mPhotoEditor.setBrushColor(selectedColor);
                        }
                    }
                });

        imagePhotoEditBack.setOnClickListener(this);
        imagePhotoEditUndo.setOnClickListener(this);
        imagePhotoEditRedo.setOnClickListener(this);
        imagePhotoEditCrop.setOnClickListener(this);
        imagePhotoEditSticker.setOnClickListener(this);
        imagePhotoEditText.setOnClickListener(this);
        imagePhotoEditPaint.setOnClickListener(this);
        fabPhotoDone.setOnClickListener(this);

        mStickerBSFragment = new StickerBSFragment();
        mStickerBSFragment.setStickerListener(this);

        try {
            Uri imageUri = Uri.parse(getIntent().getStringExtra("imageUri"));
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            photoEditorView.getSource().setImageBitmap(bitmap);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.img_photo_edit_back) {
            finish();
        } else if (i == R.id.img_photo_edit_undo) {
            mPhotoEditor.undo();
        } else if (i == R.id.img_photo_edit_redo) {
            mPhotoEditor.redo();
        } else if (i == R.id.img_photo_edit_crop) {
        } else if (i == R.id.img_photo_edit_stickers) {
            ShowBrush(false);
            mStickerBSFragment.show(getSupportFragmentManager(), mStickerBSFragment.getTag());
        } else if (i == R.id.img_photo_edit_text) {
            ShowBrush(false);
            TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show(this);
            textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
                @Override
                public void onDone(String inputText, int colorCode) {
                    mPhotoEditor.addText(inputText, colorCode);
                }
            });
        } else if (i == R.id.img_photo_edit_paint) {
            if (colorPickerView.getVisibility() == View.VISIBLE) {
                ShowBrush(false);
            } else {
                ShowBrush(true);
            }
        } else if (i == R.id.fab_photo_done) {
            saveImage();
        }
    }

    @Override
    public void onStickerClick(Bitmap bitmap) {
        mPhotoEditor.addImage(bitmap);
    }

    private void ShowBrush(boolean enableBrush) {
        if (enableBrush) {
            mPhotoEditor.setBrushColor(mSelectedColor);
            imagePhotoEditPaint.setBackgroundColor(mSelectedColor);
            mPhotoEditor.setBrushDrawingMode(true);
            colorPickerView.setVisibility(View.VISIBLE);
        } else {
            imagePhotoEditPaint.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            mPhotoEditor.setBrushDrawingMode(false);
            colorPickerView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Method to save the edited image
     */
    @SuppressLint("MissingPermission")
    private void saveImage() {

        DatabaseReference referenceMessageInsert = FirebaseDatabase.getInstance().getReference("messages");
        final String mGroupId = referenceMessageInsert.push().getKey();
        File folder = new File(Environment.getExternalStorageDirectory().toString() + "/WhatsApp Clone/Photos");
        if (!folder.isDirectory()) {
            folder.mkdirs();
        }

        File file = new File(PhotoDirectoryPath + "/" + mGroupId + ".jpg");
        try {
            file.createNewFile();

            SaveSettings saveSettings = new SaveSettings.Builder()
                    .setClearViewsEnabled(true)
                    .setTransparencyEnabled(true)
                    .build();

            mPhotoEditor.saveAsFile(file.getAbsolutePath(), saveSettings, new PhotoEditor.OnSaveListener() {
                @Override
                public void onSuccess(@NonNull String imagePath) {
                    setResult(RESULT_OK, new Intent().putExtra("imagePath", imagePath));
                    finish();
                }

                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(PhotoEditActivity.this, "Failed to save Image", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}
