package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import yuku.ambilwarna.AmbilWarnaDialog;


public class MainActivity extends AppCompatActivity {
    int defaultColor;
    DrawingView drawingView;
    ImageButton imgEraser,imgColor,imgSave;
    SeekBar seekBar;
    TextView txtPenSize, txtLine, txtCircle,txtSquare,txtClear ;

    private  static  String fileName;
    File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Paint");




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //define all the variable related by id in layout
        drawingView =findViewById(R.id.Drawing_view);
        imgEraser = findViewById(R.id.btnEraser);
        imgColor = findViewById(R.id.btnColor);
        imgSave = findViewById(R.id.btnSave);
        seekBar = findViewById(R.id.penSize);
        txtPenSize = findViewById(R.id.txtPenSize);
        txtLine =findViewById(R.id.btnLine);
        txtCircle =findViewById(R.id.btnCircle);
        txtSquare = findViewById(R.id.btnSquare);
        txtClear =findViewById(R.id.btnClear);

        //ask for permission
        askPermission();
        //date format
        SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault());
        String date = format.format(new Date());
        fileName = path + "/" + date + ".png";

        if (!path.exists()){
            path.mkdirs();
        }

        //define default color use for color pen
        defaultColor = ContextCompat.getColor(MainActivity.this, R.color.black);

        //initialize seekbar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                txtPenSize.setText(i + "dp");
                drawingView.setPenSize(i);
                seekBar.setMax(50);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //action for changing color
        imgColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openColorPicker();
            }
        });

        //action for erase figure
        imgEraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { erase();
            }
        });

        //action for save image in the directory
        imgSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!drawingView.isBitmapEmpty()){

                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
//set icon
                            .setIcon(android.R.drawable.ic_dialog_alert)
//set title
                            .setTitle("Sauvegarde")
//set message
                            .setMessage("voulez-vous sauvegardez la photo?")
//set positive button
                            .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    try {
                                        saveImage();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }


                                }
                            })
//set negative button
                            .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //set what should happen when negative button is clicked

                                }
                            })
                            .show();

                }
            }

        });
        //action for clear canvas
        txtClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!drawingView.isBitmapEmpty()){
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
//set icon
                            .setIcon(android.R.drawable.ic_dialog_alert)
//set title
                            .setTitle("Reinitialisez la page")
//set message
                            .setMessage("voulez-vous faire une nouvelle dessin?")
//set positive button
                            .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    drawingView.clearCanvas();
                                }
                            })
//set negative button
                            .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //set what should happen when negative button is clicked

                                }
                            })
                            .show();

                }


            }
        });
        //action to draw line
        txtLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawingView.setPenColor(defaultColor);
                drawingView.setPenSize(15);

            }
        });
        txtCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void saveImage() throws IOException {
        File file = new File(fileName);
        Bitmap bitmap = drawingView.getSignatureBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,0,bos);

        byte[] bitMapData = bos.toByteArray();

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bitMapData);
        fos.flush();
        fos.close();
        Toast.makeText(this, "image saved  successfully! at sary", Toast.LENGTH_SHORT).show();
    }



    private void erase() {
        int colorWhite = ContextCompat.getColor(MainActivity.this,R.color.white);
        drawingView.setPenColor(colorWhite);
        drawingView.setPenSize(50);
    }

    private void  openColorPicker() {
        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(this, defaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                defaultColor = color;
                drawingView.setPenColor(color);
            }
        });
        ambilWarnaDialog.show();
    }

    private void askPermission() {
        Dexter.withContext(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        Toast.makeText(MainActivity.this, "permission avec succées", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }



}

