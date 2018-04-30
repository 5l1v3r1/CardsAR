package com.wear.cardsar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;

public class SpecifyNewMapping extends AppCompatActivity {

    public static final String EXTRA_REPLY_MAPPING_NAME = "com.example.android.wordlistsql.NAME";
    public static final String EXTRA_REPLY_MAPPING_DESCRIPTION = "com.example.android.wordlistsql.DESCRIPTION";
    public static final String EXTRA_REPLY_MAPPING_QUANTITY = "com.example.android.wordlistsql.QUANTITY";
    public static final String EXTRA_REPLY_MAPPING_URI = "com.example.android.wordlistsql.URI";

    public static final int GET_FROM_GALLERY = 3;

    private static final String TAG = "SNewMappingActivity";

    private EditText mEditMappingNameTextView;
    private EditText mEditmappingDescriptionTextView;
    private EditText mEditmappingQuatityTextView;
    private Uri selectedImage;

    Bitmap bitmap = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specify_new_mapping);
        mEditMappingNameTextView = findViewById(R.id.edit_mapping_name);
        mEditmappingDescriptionTextView = findViewById(R.id.edit_mapping_description);
        mEditmappingQuatityTextView = findViewById(R.id.edit_mapping_quantity);

        final Button imageButton = findViewById(R.id.button_add_picture);
        imageButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }

        });

        //final ImageView iv = (ImageView) findViewById(R.id.iv);

        final Button button = findViewById(R.id.button_save);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent replyIntent = new Intent();
                if (TextUtils.isEmpty(mEditMappingNameTextView.getText())
                        || TextUtils.isEmpty(mEditmappingDescriptionTextView.getText())
                        || TextUtils.isEmpty(mEditmappingQuatityTextView.getText())) {
                    //setResult(RESULT_CANCELED, replyIntent);
                    Log.v(TAG, "No mapping name!");
                    //TODO: make toast that lets user know what went wrong

                    Toast.makeText(
                            getApplicationContext(),
                            R.string.mapping_empty_not_saved,
                            Toast.LENGTH_LONG).show();

                } else {

                    try {
                        int tmp = Integer.parseInt(mEditmappingQuatityTextView.getText().toString());

                        //vars to pass
                        String mappingName = mEditMappingNameTextView.getText().toString();
                        String mappingDescription = mEditmappingDescriptionTextView.getText().toString();
                        String mappingQuantity = mEditmappingQuatityTextView.getText().toString();
                        String mappingUri = null;
                        if(selectedImage != null) {
                            mappingUri = selectedImage.toString();
                        }

                        // extra intent stuff
                        replyIntent.putExtra(EXTRA_REPLY_MAPPING_NAME, mappingName);
                        replyIntent.putExtra(EXTRA_REPLY_MAPPING_DESCRIPTION, mappingDescription);
                        replyIntent.putExtra(EXTRA_REPLY_MAPPING_QUANTITY, mappingQuantity);
                        if( bitmap != null) {
                            replyIntent.putExtra(EXTRA_REPLY_MAPPING_URI, mappingUri);
                            replyIntent.putExtra("pic", "true");
                        }else { // no img
                            replyIntent.putExtra("pic", "false");
                        }

                        //send result
                        setResult(RESULT_OK, replyIntent);

                        finish();

                    }catch(java.lang.NumberFormatException e){
                        Toast.makeText(
                                getApplicationContext(),
                                R.string.invalid_quantity_not_saved,
                                Toast.LENGTH_LONG).show();
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request codes for img
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            selectedImage = data.getData();
            //to display image
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ImageView iv = findViewById(R.id.iv);
                iv.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }
}

