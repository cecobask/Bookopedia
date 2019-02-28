package ie.bask.main;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Base extends AppCompatActivity {

    public BookopediaApp app;
    public ProgressBar pbSearch;
    public TextView tvNoResults;


    @Override
    public void onBackPressed() {
        if(isTaskRoot()){
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setMessage("Exit application?");
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            alertDialog.show();
        } else {
            super.onBackPressed();
        }

    }


}
