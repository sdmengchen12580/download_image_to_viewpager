package com.example.yunwen.load_picture;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    int img_id[] = {R.id.img1,R.id.img2,R.id.img3,R.id.img4,R.id.img5,R.id.img6,R.id.img7,R.id.img8,R.id.img9};
    ImageView imageViews[] = new ImageView[9];
    ProgressBar pro;
    List<Bitmap> list = new ArrayList<>();


    String imgurl[] = {
            "http://robotcdn.infinitus.com.cn/daping/daping1.jpg",
            "http://robotcdn.infinitus.com.cn/daping/daping2.jpg",
            "http://robotcdn.infinitus.com.cn/daping/daping3.jpg",
            "http://robotcdn.infinitus.com.cn/daping/daping4.jpg",
            "http://robotcdn.infinitus.com.cn/daping/daping5.jpg",
            "http://robotcdn.infinitus.com.cn/daping/daping6.jpg",
            "http://robotcdn.infinitus.com.cn/daping/daping7.jpg",
            "http://robotcdn.infinitus.com.cn/daping/daping8.jpg",
            "http://robotcdn.infinitus.com.cn/daping/daping9.jpg",
    };

    MyAsyncTask myasy = new MyAsyncTask();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pro = (ProgressBar) findViewById(R.id.progress);

        for (int i = 0; i < 9; i++) {
            imageViews[i] = (ImageView) findViewById(img_id[i]);
        }

        /**下载*/
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //异步加载图片，传入数组
                myasy.execute(imgurl);
            }
        });

        /**加载*/
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int a = list.size();
                for (int i = 0; i <a ; i++) {
                    imageViews[i].setImageBitmap(list.get(i));
                }
            }
        });
    }


        /**全局变量的bitmap，存每次网络上下载的图片**/
        Bitmap bitmap= null;
        class  MyAsyncTask extends AsyncTask<String,Void,List<Bitmap>> {
            /**第一阶段————准备阶段让进度条显示*/
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pro.setVisibility(View.VISIBLE);
            }
            /**
             *   第二阶段——网络获取图片——返回类型为list集合
             *   doInBackground方法传进来可变长的数组p----但是此方法中每次
             *  只能从数组中拿到一个图片的网址，所以使用for循环，不停的拿出网址。
             */
            @Override
            protected List<Bitmap> doInBackground(String... params) {
                for (int i = 0; i <params.length ; i++) {
                    //从可变参数的数组中拿到图片地址
                    String newurl = params[i];
                    URLConnection urlConnection;
                    InputStream is = null;
                    try {
                        //从url路径中打开网络连接
                        urlConnection = new URL(newurl).openConnection();
                        //从网络连接中获取流，并存入到缓存中
                        is = urlConnection.getInputStream();
                        BufferedInputStream bf=new BufferedInputStream(is);
                        //图片加载的太快，可以用线程睡眠
//                        Thread.sleep(1000);
                        //decodeStream从流中获取bitmap
                        bitmap = BitmapFactory.decodeStream(bf);
                        list.add(bitmap);
                        //关闭流不用finally，直接放到try里面
                        is.close();
                        bf.close();
                    } catch (IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pro.setVisibility(View.INVISIBLE);
                            }
                        });
                        myasy.cancel(true);
                    }
                }
                //返回装有bitmap的集合给onPostExecute方法
                return list;
            }
            //第三阶段——————doInBackground返回的bitmap图片————操作ui，设置图像
            @Override
            protected void onPostExecute(List<Bitmap> list) {
                super.onPostExecute(list);
                //获取到bitmap后就隐藏图片，然后progressbar隐藏，占的位置也不见了
               pro.setVisibility(View.INVISIBLE);
            }
        }
    }

