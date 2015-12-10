package com.example.administrator.work6;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.io.File;
import java.util.Vector;

public class AlbumActivity extends Activity {

    private ViewFlipper flipper;
    private Bitmap[] mBgList;   //ͼƬ�洢�б�
    private long startTime = 0;
    private SensorManager sm;      //������ӦӲ��������
    private SensorEventListener sel;  //������Ӧ����

    /**
     * �������
     *
     */

    public String[] loadAlbum(){
        String pathName = android.os.Environment.
                getExternalStorageDirectory().getPath()
                +"/com.demo.pr4";
        //�����ļ�
        File file = new File(pathName);
        Vector<Bitmap> fileName = new Vector<>();
        if(file.exists()&&file.isDirectory()){
            String[] str = file.list();
            for (String s: str){
                if(new File(pathName+"/"+s).isFile()){
                    fileName.addElement(loadImage(pathName+"/"+s));

                }
            }
            mBgList = fileName.toArray(new Bitmap[]{});
        }

        return null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        flipper = (ViewFlipper) this.findViewById(R.id.ViewFlipper01);
        loadAlbum();
        if(mBgList == null){
            Toast.makeText(this,"�����ͼƬ",Toast.LENGTH_SHORT).show();
            finish();
            return;

        }else{
            for (int i = 0; i<=mBgList.length-1; i++){
                flipper.addView(addImage(mBgList[i]),i,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                        ViewGroup.LayoutParams.FILL_PARENT));
            }
        }
        //���������ӦӲ��������
        sm = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        Sensor sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //���������Ӧ����
        sel = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float x = event.values[SensorManager.DATA_X];
                //   float y = event.values[SensorManager.DATA_Y];
                //   float z = event.values[SensorManager.DATA_Z];
                // System.currentTimeMillis()>startTime+1000  ����˦���ı�����1����ֻ��һ��˦��
                if (x > 10 && System.currentTimeMillis() > startTime + 1000) //��˦��
                {
                    //��¼˦����ʼʱ��
                    startTime = System.currentTimeMillis();
                    flipper.setInAnimation(AnimationUtils.loadAnimation(AlbumActivity.this,R.anim.push_right_in));
                    flipper.setOutAnimation(AnimationUtils.loadAnimation(AlbumActivity.this,R.anim.push_left_in));
                    flipper.showPrevious();

                }else if( x < -10 && System.currentTimeMillis() > startTime + 1000) //��˦��
                {
                    startTime = System.currentTimeMillis();
                    flipper.setInAnimation(AnimationUtils.loadAnimation(AlbumActivity.this,R.anim.push_left_in));
                    flipper.setInAnimation(AnimationUtils.loadAnimation(AlbumActivity.this,R.anim.push_left_out));
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        //ע��Listener,SENSOR_DELAY_GAMEΪ���ľ�׼��,
        sm.registerListener(sel,sensor,SensorManager.SENSOR_DELAY_GAME);
    }


    @Override
    protected void onDestroy(){
        super.onDestroy();
        //ע��������Ӧ����
        sm.unregisterListener(sel);
    }

    public Bitmap loadImage(String pathName) {
        //��ȡ��Ƭ������ͼƬ������С
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //��ʱ����bitmapΪ��
        Bitmap bitmap = BitmapFactory.decodeFile(pathName, options);

        //��ȡ��Ļ�Ŀ��
        WindowManager manager = getWindowManager();
        Display display = manager.getDefaultDisplay();
        //����ϣ�� Bitmap ����ʾ���Ϊ�ֻ���Ļ�Ŀ��
        int screenWidth = display.getWidth();
        // int screenHeigh = display.getHeight();
        //���� Bitmap �ĸ߶ȵȱȱ仯��ֵ
        options.inSampleSize = options.outWidth / screenWidth;
        //�� inJustDecodeBounds ����Ϊfalse,�Ա��ڿ��Խ���Ϊ Bitmap�ļ�
        options.inJustDecodeBounds = false;
        //��ȡ��Ƭ Bitmap
        bitmap = BitmapFactory.decodeFile(pathName,options);
        return bitmap;
    }
    //�����ʾͼƬView
    private View addImage(Bitmap bitmap) {
        ImageView img = new ImageView(this);
        img.setImageBitmap(bitmap);
        return null;
    }
}
