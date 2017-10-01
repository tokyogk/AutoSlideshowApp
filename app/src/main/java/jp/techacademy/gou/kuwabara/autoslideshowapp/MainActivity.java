package jp.techacademy.gou.kuwabara.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    Timer GTimer;
    private TimerTask timerTask;
    private boolean runflg = false;
    TextView GTimerText;
    double GTimerSec = 0.0;

    Handler GHandler = new Handler();

    Button PlayStoptButton;
    Button GoButton;
    Button BackButton;
    ImageView GSlideImage;

    private Cursor GCursor;



    private static final int PERMISSIONS_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GTimerText = (TextView) findViewById(R.id.TextView);
        PlayStoptButton = (Button) findViewById(R.id.play_stop_button);
        GoButton = (Button) findViewById(R.id.go_button);
        BackButton = (Button) findViewById(R.id.back_button);
        GSlideImage = (ImageView)findViewById(R.id.gSlideImage);


        PlayStoptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (runflg) {
                    // 再生中->停止の処理
                    runflg = false;
                    stopTimer();
                } else {
                    // 停止中->再生の処理
                    runflg = true;
                    restartTimer();
                }
            }
        });

        GoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GTimerSec  += 0.1;
                GTimerText.setText(String.format("%.1f", GTimerSec));

                if (GTimer != null) {
                    GTimer.cancel();
                    GTimer = null;
                }
            }
        });

        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GTimerSec  -= 0.1;
                GTimerText.setText(String.format("%.1f", GTimerSec));

                if (GTimer != null) {
                    GTimer.cancel();
                    GTimer = null;
                }
            }
        });

        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo();
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();
                }
                break;
            default:
                break;
        }
    }

    private  void restartTimer()
    {
        if (GTimer == null) {
            GTimer = new Timer();
            GTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    GTimerSec += 0.1;

                    GHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            GTimerText.setText(String.format("%.1f", GTimerSec));
                        }
                    });
                }
            }, 100, 100);
        }
    }

    private  void stopTimer()
    {
        GTimer.cancel();
        GTimer = null;
    }


    private Cursor getContentsInfo() {
        // 画像の情報を取得する
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );
        if (cursor.moveToFirst()) {
            return cursor;
        } else {
            cursor.close();
            return null;
        }
    }


        /*
        if (cursor.moveToPosition()) {
            // indexからIDを取得し、そのIDから画像のURIを取得する
            int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = cursor.getLong(fieldIndex);
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
            imageVIew.setImageURI(imageUri);
        }
        */


    }