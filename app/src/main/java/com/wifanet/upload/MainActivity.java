package com.wifanet.upload;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_IMAGE_CAMERA = 2;
    static final Integer CAMERA = 0x5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnChooseImageOrPick = (Button) findViewById(R.id.btnChooseImageOrPick);
        btnChooseImageOrPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });
    }

    void openImageChooser() {
        try {
            PackageManager pm = getPackageManager();
            int hasPerm = pm.checkPermission(android.Manifest.permission.CAMERA, getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                final CharSequence[] options = {"From camera", "From gallery", "Cancel"};
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("From camera")) {
                            dialog.dismiss();
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, PICK_IMAGE_CAMERA);
                        } else if (options[item].equals("From gallery")) {
                            dialog.dismiss();
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            } else {
                Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
                askForPermission(android.Manifest.permission.CAMERA, CAMERA);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();

            String str = getImageBase64(uri);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HHmm");
            String currentDateandTime = sdf.format(new Date());
            RequestParams params = new RequestParams();
            params.put("encoded_string", str);
            params.put("image", "KRB_" + currentDateandTime + ".png");

            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(60000);
            client.post("http://rest.wifanet.com/upload.php", params, new TextHttpResponseHandler() {
                ProgressDialog pd;

                @Override
                public void onStart() {

                    pd = new ProgressDialog(MainActivity.this);
                    pd.setTitle("Please Wait");
                    pd.setMessage("Uploading Image In Progress");
                    pd.setIndeterminate(false);
                    pd.setCancelable(true);
                    pd.show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.d("myLOGError", statusCode + "");
                    Toast.makeText(MainActivity.this, statusCode + "", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Log.d("myLOGSuccess", responseString);
                    Toast.makeText(MainActivity.this, responseString, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFinish() {
                    pd.dismiss();
                }
            });
        }
        else if (requestCode == PICK_IMAGE_CAMERA && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //bitmap = (Bitmap) data.getExtras().get("data");
            Uri uri = data.getData();

            String str = getImageBase64(uri);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HHmm");
            String currentDateandTime = sdf.format(new Date());
            RequestParams params = new RequestParams();
            params.put("encoded_string", str);
            params.put("image", "KRB_" + currentDateandTime + ".png");

            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(60000);
            client.post("http://rest.wifanet.com/upload.php", params, new TextHttpResponseHandler() {
                ProgressDialog pd;

                @Override
                public void onStart() {

                    pd = new ProgressDialog(MainActivity.this);
                    pd.setTitle("Please Wait");
                    pd.setMessage("Uploading Image In Progress");
                    pd.setIndeterminate(false);
                    pd.setCancelable(true);
                    pd.show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.d("myLOGError", statusCode + "");
                    Toast.makeText(MainActivity.this, statusCode + "", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Log.d("myLOGSuccess", responseString);
                    Toast.makeText(MainActivity.this, responseString, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFinish() {
                    pd.dismiss();
                }
            });
        }
    }

    //Converting Selected Image to Base64Encode String
    private String getImageBase64(Uri selectedImage) {
        Bitmap myImg = null;
        try {
            myImg = decodeUri(selectedImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Must compress the Image to reduce image size to make upload easy
        myImg.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byte_arr = stream.toByteArray();
        // Encode Image to String
        return android.util.Base64.encodeToString(byte_arr, 0);
    }

    //Reducing Image Size of a selected Image
    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 500;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
                    || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);

    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);

            } else {
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            }
        } else {
            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }
}
