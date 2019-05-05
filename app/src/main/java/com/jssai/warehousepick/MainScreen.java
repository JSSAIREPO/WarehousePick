package com.jssai.warehousepick;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import com.jssai.warehousepick.Adapter.MainscreenAdapter;
import com.jssai.warehousepick.Util.Constants;
import com.jssai.warehousepick.Util.GlobalMethods;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainScreen extends AppCompatActivity {

    @BindView(R.id.mainRecycler)
    RecyclerView mainRecycler;
    MainscreenAdapter mainscreenAdapter;
    String[] titles;
    int[] imageListl;
    AlertDialog aboutusDilaog;

    @Override
    protected void onStop() {
        super.onStop();
        imageListl = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        ButterKnife.bind(this);

        View view = ((LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.aboutus_layout,null);
        TextView versionTextView =(TextView)view.findViewById(R.id.versionText);
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;
        if(!version.isEmpty()){
            versionTextView.setText("v"+version);
        }
        AlertDialog.Builder builder = new  AlertDialog.Builder(this);
        builder.setView(view);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        aboutusDilaog = builder.create();


        titles = new String[]{"eSignOrder"};
        imageListl = new int[]{R.drawable.ic_sign};

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3);
        mainRecycler.setLayoutManager(gridLayoutManager);
        mainscreenAdapter = new MainscreenAdapter(imageListl,titles,new ClickHandler(new WeakReference<>(this)));
        mainRecycler.setAdapter(mainscreenAdapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.logout){
            SharedPreferences sharedPreferences = GlobalMethods.getSharedPrefrence(MainScreen.this.getApplicationContext());
            SharedPreferences.Editor editor  = sharedPreferences.edit();
            editor.remove(Constants.USER);
            editor.remove(Constants.PASSWORD);
            editor.remove(Constants.REMEMBERME);
            editor.apply();
            Intent intent = new Intent(MainScreen.this,Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    static class ClickHandler extends Handler{

        private WeakReference<MainScreen> mainScreenWeakReference;

         ClickHandler(WeakReference<MainScreen> mainScreenWeakReference) {
            this.mainScreenWeakReference = mainScreenWeakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainScreen mainScreen = mainScreenWeakReference.get();
            if (mainScreen != null) {
                Bundle bundle = msg.getData();
                int pos = bundle.getInt("pos");
                switch (pos){
                    case 0:
                        Intent intent = new Intent(mainScreen,SalesOrderListActivity.class);
                        mainScreen.startActivity(intent);
                        break;
                }
            }
        }
    }
}
