package com.rzico.weex.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.rzico.weex.R;
import com.rzico.weex.WXApplication;
import com.rzico.weex.model.info.Message;
import com.rzico.weex.utils.ImageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.finalteam.rxgalleryfinal.RxGalleryFinal;
import cn.finalteam.rxgalleryfinal.bean.MediaBean;
import cn.finalteam.rxgalleryfinal.imageloader.ImageLoaderType;
import cn.finalteam.rxgalleryfinal.rxbus.RxBusResultDisposable;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageMultipleResultEvent;

public class ImageActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button select;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        imageView = (ImageView) findViewById(R.id.imageView);
        select    = (Button)findViewById(R.id.select);

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxGalleryFinal rxGalleryFinal = RxGalleryFinal
                        .with(WXApplication.getActivity())
                        .image()
                        .multiple();
                rxGalleryFinal.maxSize(6)
                        .imageLoader(ImageLoaderType.PICASSO)
                        .subscribe(new RxBusResultDisposable<ImageMultipleResultEvent>() {
                            @Override
                            protected void onEvent(ImageMultipleResultEvent imageMultipleResultEvent) throws Exception {
                                List<MediaBean> list = imageMultipleResultEvent.getResult();
                                if(list != null && list.size() > 0) {
                                    List<Bitmap> bitmaps = new ArrayList<>();
                                    for (int i = 0; i <list.size() ; i++){
                                        String imagePath = list.get(i).getOriginalPath();
                                        File file = new File(imagePath);
                                        if (file.exists()) {
                                            Bitmap bm = BitmapFactory.decodeFile(imagePath);
                                            bitmaps.add(bm);
                                        }
                                    }
                                    Bitmap newBitmap = null;

                                    switch (bitmaps.size()){
                                        case 2:
                                            newBitmap = ImageUtils.add2Bitmap(ImageUtils.centerSquareScaleBitmap(bitmaps.get(0),300), ImageUtils.centerSquareScaleBitmap(bitmaps.get(1),300));
                                            imageView.setImageBitmap(newBitmap);
                                            break;
                                        case 3:
                                            newBitmap = ImageUtils.add2Bitmap(ImageUtils.add2Bitmap(bitmaps.get(0), bitmaps.get(1)), bitmaps.get(2));
                                            imageView.setImageBitmap(newBitmap);
                                            break;
                                    }
                                }else {

                                }
                            }

                        }).openGallery();
            }
        });


    }
}
