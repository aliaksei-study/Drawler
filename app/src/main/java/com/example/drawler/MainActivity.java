package com.example.drawler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.PermissionRequest;
import android.widget.Toast;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private PaintView paintView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        paintView = findViewById(R.id.paintView);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init(metrics);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.normal:
                paintView.normal();
                return true;
            case R.id.emboss:
                paintView.emboss();
                return true;
            case R.id.blur:
                paintView.blur();
                return true;
            case R.id.clear:
                paintView.clear();
                return true;
            case R.id.size_small:
                paintView.size_small();
                return true;
            case R.id.size_normal:
                paintView.size_normal();
                return true;
            case R.id.size_big:
                paintView.size_big();
                return true;
            case R.id.color_green:
                paintView.color_green();
                return true;
            case R.id.color_red:
                paintView.color_red();
                return true;
            case R.id.color_black:
                paintView.color_black();
                return true;
            case R.id.remove_last:
                paintView.deleteLastPath();
                return true;
            case R.id.figure_rectangle:
                paintView.setFigure(Figure.RECTANGLE);
                return true;
            case R.id.figure_circle:
                paintView.setFigure(Figure.CIRCLE);
                return true;
            case R.id.figure_triangle:
                paintView.setFigure(Figure.TRIANGLE);
                return true;
            case R.id.figure_line:
                paintView.setFigure(Figure.LINE);
                return true;
            case R.id.save:
                showSavePaintingConfirmationDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showSavePaintingConfirmationDialog(){
        AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
        saveDialog.setTitle("Save drawing");
        saveDialog.setMessage("Save drawing to device Gallery?");
        saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                //save drawing
                paintView.setDrawingCacheEnabled(true);
                String imgSaved = MediaStore.Images.Media.insertImage(
                        getContentResolver(), paintView.getDrawingCache(),
                        UUID.randomUUID().toString()+".png", "drawing");
                if(imgSaved!=null){
                    Toast savedToast = Toast.makeText(getApplicationContext(),
                            "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                    savedToast.show();
                }
                else{
                    Toast unsavedToast = Toast.makeText(getApplicationContext(),
                            "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
                    unsavedToast.show();
                }
                // Destroy the current cache.
                paintView.destroyDrawingCache();
            }
        });
        saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
            }
        });
        saveDialog.show();
    }

}