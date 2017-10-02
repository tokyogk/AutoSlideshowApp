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
import android.widget.Toast;

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


        PlayStoptButton = (Button) findViewById(R.id.play_stop_button);
        GoButton = (Button) findViewById(R.id.go_button);
        BackButton = (Button) findViewById(R.id.back_button);
        GSlideImage = (ImageView)findViewById(R.id.gSlideImage);


        PlayStoptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (runflg) {
                    // 停止中->再生の処理
                    runflg = false;
                    stopTimer();
                    stopButton();
                    canPushButton();
                    return ;

                } else {
                    // 再生中->停止の処理
                    runflg = true;
                    photoTimer();
                    startButton();
                    cantPushButton();
                }
                if(GCursor != null){
                    // 最初で戻るボタンを押したら最後に戻る
                    if(!GCursor.moveToNext()) GCursor.moveToFirst();
                    // 画像を表示させる関数を呼ぶ

                }
            }
        });

        GoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(GCursor != null){
                    // 最初で戻るボタンを押したら最後に戻る
                    if(!GCursor.moveToNext()) GCursor.moveToFirst();
                    // 画像を表示させる関数を呼ぶ
                    setImage();
                }
            }
        });

        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(GCursor != null){
                    // 最後まで来たらあたまから
                    if(!GCursor.moveToPrevious()) GCursor.moveToLast();
                    // 画像を表示させる関数を呼ぶ
                    setImage();
                }
            }
        });

        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                GCursor = getContentsInfo();
            } else {
                // 許可されていないので許可ダイアログを表示する
                Toast.makeText(this,"許可しないと見れません。許可してください！",Toast.LENGTH_SHORT).show();
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
            // Android 5系以下の場合
        } else {
            GCursor = getContentsInfo();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    GCursor = getContentsInfo();
                }
                break;
            default:
                break;
        }
    }

    private  void photoTimer()
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
                            if(GCursor != null){
                                // 最後に行ったら最初に戻る
                                if(!GCursor.moveToNext()) GCursor.moveToFirst();
                                // 画像を表示させる関数を呼ぶ
                                setImage();
                            }
                        }
                    });
                }
            }, 2000, 2000);
        }
    }


    private  void stopTimer()
    {
        GTimer.cancel();
        GTimer = null;
    }

    private  void stopButton() // ボタン再生時のボタン表示
    {
        Button play_stop_button = (Button) findViewById(R.id.play_stop_button);
        play_stop_button.setText("再生");
    }

    private  void startButton() // ボタン停止時のボタン表示
    {
        Button play_stop_button = (Button) findViewById(R.id.play_stop_button);
        play_stop_button.setText("停止");
    }

    private  void cantPushButton() // スライドショー中の他ボタンの操作禁止
    {
        Button go_button = (Button) findViewById(R.id.go_button);
        Button back_button = (Button) findViewById(R.id.back_button);
        go_button.setEnabled(false);
        back_button.setEnabled(false);
    }

    private  void canPushButton() // スライドショー中の他ボタンの操作禁止
    {
        Button go_button = (Button) findViewById(R.id.go_button);
        Button back_button = (Button) findViewById(R.id.back_button);
        go_button.setEnabled(true);
        back_button.setEnabled(true);
    }
    /* 先生に教えてもらった一つにする場合
    private void setPushButton(boolean flag) // スライドショー中の他ボタンの操作禁止
{
  Button go_button = (Button) findViewById(R.id.go_button);
  Button back_button = (Button) findViewById(R.id.back_button);
  go_button.setEnabled(flag);
  back_button.setEnabled(flag);
}
     */

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

    private void setImage(){
        int fieldIndex =
                GCursor.getColumnIndex(MediaStore.Images.Media._ID);
        Long id = GCursor.getLong(fieldIndex);
        Uri imageUri =ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
        GSlideImage.setImageURI(imageUri);
         }
    }