package cn.finalteam.rxgalleryfinal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.finalteam.rxgalleryfinal.model.Coordinates;
import cn.finalteam.rxgalleryfinal.model.ImageItem;
import cn.finalteam.rxgalleryfinal.model.Puzzle;
import cn.finalteam.rxgalleryfinal.model.Type;
import cn.finalteam.rxgalleryfinal.utils.BarTextColorUtils;
import cn.finalteam.rxgalleryfinal.view.PuzzleView;
import com.yalantis.ucrop.view.TopView;
import com.yixiang.mopian.constant.AllConstant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.finalteam.rxgalleryfinal.bean.MediaBean;
import cn.finalteam.rxgalleryfinal.imageloader.ImageLoaderType;
import cn.finalteam.rxgalleryfinal.rxbus.RxBusResultDisposable;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageMultipleResultEvent;

/**
 * Created by Jinlesoft on 2017/11/4.
 */

public class PuzzleActivity extends AppCompatActivity implements View.OnClickListener {

    public final static int SUCCESS = 1;
    public final static int ERROR = 0;
    private Context context;
    private TopView topView;
    public LinearLayout puzzleLL;
    private PuzzleView puzzleView;
    private TextView templateTv;
    private List<MediaBean> imageBeans;
    private Puzzle puzzleEntity;
    //    private TemplateDialog templateDialog; 底部弹窗
    private String pathFileName;
    private int lastSelect = 0;
    //传过来的像素
    private int width = 1000;
    private int height = 500;
    //边框的大小
    private int lineWidth = 5;

    private LinearLayout ll_puzzle_1, ll_puzzle_2, ll_puzzle_3, ll_puzzle_4, ll_puzzle_5, ll_puzzle_6;
    private ImageView iv_puzzle_1, iv_puzzle_2, iv_puzzle_3, iv_puzzle_4, iv_puzzle_5, iv_puzzle_6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BarTextColorUtils.StatusBarLightMode(PuzzleActivity.this, R.color.wxColor);
        setContentView(R.layout.activity_puzzle);
        init();
    }
    private void init() {
        context = PuzzleActivity.this;
        initView();
        initData();
        initEvent();
    }

    private void initView() {

        topView = (TopView) findViewById(R.id.top_view);
        puzzleLL = (LinearLayout) findViewById(R.id.puzzle_ll);
        puzzleView = (PuzzleView) findViewById(R.id.puzzle_view);
        templateTv = (TextView) findViewById(R.id.template_tv);
        ll_puzzle_1 = (LinearLayout) findViewById(R.id.ll_puzzle_1);
        ll_puzzle_2 = (LinearLayout) findViewById(R.id.ll_puzzle_2);
        ll_puzzle_3 = (LinearLayout) findViewById(R.id.ll_puzzle_3);
        ll_puzzle_4 = (LinearLayout) findViewById(R.id.ll_puzzle_4);
        ll_puzzle_5 = (LinearLayout) findViewById(R.id.ll_puzzle_5);
        ll_puzzle_6 = (LinearLayout) findViewById(R.id.ll_puzzle_6);

        ll_puzzle_1.setOnClickListener(this);
        ll_puzzle_2.setOnClickListener(this);
        ll_puzzle_3.setOnClickListener(this);
        ll_puzzle_4.setOnClickListener(this);
        ll_puzzle_5.setOnClickListener(this);
        ll_puzzle_6.setOnClickListener(this);

        iv_puzzle_1 = (ImageView) findViewById(R.id.iv_puzzle_1);
        iv_puzzle_2 = (ImageView) findViewById(R.id.iv_puzzle_2);
        iv_puzzle_3 = (ImageView) findViewById(R.id.iv_puzzle_3);
        iv_puzzle_4 = (ImageView) findViewById(R.id.iv_puzzle_4);
        iv_puzzle_5 = (ImageView) findViewById(R.id.iv_puzzle_5);
        iv_puzzle_6 = (ImageView) findViewById(R.id.iv_puzzle_6);
    }
    private void showUi(int positon){
        switch (positon){
            case 1:
                ll_puzzle_1.setBackgroundColor(getResources().getColor(R.color.wxColor));
                ll_puzzle_2.setBackgroundColor(getResources().getColor(R.color.puzzleBg));
                ll_puzzle_3.setBackgroundColor(getResources().getColor(R.color.puzzleBg));
                ll_puzzle_4.setBackgroundColor(getResources().getColor(R.color.puzzleBg));
                ll_puzzle_5.setBackgroundColor(getResources().getColor(R.color.puzzleBg));
                ll_puzzle_6.setBackgroundColor(getResources().getColor(R.color.puzzleBg));
                iv_puzzle_1.setImageResource(R.drawable.ico_puzzle1_focus);
                iv_puzzle_2.setImageResource(R.drawable.ico_puzzle2);
                iv_puzzle_3.setImageResource(R.drawable.ico_puzzle3);
                iv_puzzle_4.setImageResource(R.drawable.ico_puzzle4);
                iv_puzzle_5.setImageResource(R.drawable.ico_puzzle5);
                iv_puzzle_6.setImageResource(R.drawable.ico_puzzle6);
                break;
            case 2:
                ll_puzzle_1.setBackgroundColor(getResources().getColor(R.color.puzzleBg));
                ll_puzzle_2.setBackgroundColor(getResources().getColor(R.color.wxColor));
                ll_puzzle_3.setBackgroundColor(getResources().getColor(R.color.puzzleBg));
                ll_puzzle_4.setBackgroundColor(getResources().getColor(R.color.puzzleBg));
                ll_puzzle_5.setBackgroundColor(getResources().getColor(R.color.puzzleBg));
                ll_puzzle_6.setBackgroundColor(getResources().getColor(R.color.puzzleBg));
                iv_puzzle_1.setImageResource(R.drawable.ico_puzzle1);
                iv_puzzle_2.setImageResource(R.drawable.ico_puzzle2_focus);
                iv_puzzle_3.setImageResource(R.drawable.ico_puzzle3);
                iv_puzzle_4.setImageResource(R.drawable.ico_puzzle4);
                iv_puzzle_5.setImageResource(R.drawable.ico_puzzle5);
                iv_puzzle_6.setImageResource(R.drawable.ico_puzzle6);
                break;
            case 3:
                ll_puzzle_1.setBackgroundColor(getResources().getColor(R.color.puzzleBg));
                ll_puzzle_2.setBackgroundColor(getResources().getColor(R.color.puzzleBg));
                ll_puzzle_3.setBackgroundColor(getResources().getColor(R.color.wxColor));
                ll_puzzle_4.setBackgroundColor(getResources().getColor(R.color.puzzleBg));
                ll_puzzle_5.setBackgroundColor(getResources().getColor(R.color.puzzleBg));
                ll_puzzle_6.setBackgroundColor(getResources().getColor(R.color.puzzleBg));
                iv_puzzle_1.setImageResource(R.drawable.ico_puzzle1);
                iv_puzzle_2.setImageResource(R.drawable.ico_puzzle2);
                iv_puzzle_3.setImageResource(R.drawable.ico_puzzle3_focus);
                iv_puzzle_4.setImageResource(R.drawable.ico_puzzle4);
                iv_puzzle_5.setImageResource(R.drawable.ico_puzzle5);
                iv_puzzle_6.setImageResource(R.drawable.ico_puzzle6);
                break;
            case 4:
                ll_puzzle_1.setBackgroundColor(getResources().getColor(R.color.puzzleBg));
                ll_puzzle_2.setBackgroundColor(getResources().getColor(R.color.puzzleBg));
                ll_puzzle_3.setBackgroundColor(getResources().getColor(R.color.puzzleBg));
                ll_puzzle_4.setBackgroundColor(getResources().getColor(R.color.wxColor));
                ll_puzzle_5.setBackgroundColor(getResources().getColor(R.color.puzzleBg));
                ll_puzzle_6.setBackgroundColor(getResources().getColor(R.color.puzzleBg));
                iv_puzzle_1.setImageResource(R.drawable.ico_puzzle1);
                iv_puzzle_2.setImageResource(R.drawable.ico_puzzle2);
                iv_puzzle_3.setImageResource(R.drawable.ico_puzzle3);
                iv_puzzle_4.setImageResource(R.drawable.ico_puzzle4_focus);
                iv_puzzle_5.setImageResource(R.drawable.ico_puzzle5);
                iv_puzzle_6.setImageResource(R.drawable.ico_puzzle6);
                break;
            case 5:
                ll_puzzle_1.setBackgroundColor(getResources().getColor(R.color.puzzleBg));
                ll_puzzle_2.setBackgroundColor(getResources().getColor(R.color.puzzleBg));
                ll_puzzle_3.setBackgroundColor(getResources().getColor(R.color.puzzleBg));
                ll_puzzle_4.setBackgroundColor(getResources().getColor(R.color.puzzleBg));
                ll_puzzle_5.setBackgroundColor(getResources().getColor(R.color.wxColor));
                ll_puzzle_6.setBackgroundColor(getResources().getColor(R.color.puzzleBg));
                iv_puzzle_1.setImageResource(R.drawable.ico_puzzle1);
                iv_puzzle_2.setImageResource(R.drawable.ico_puzzle2);
                iv_puzzle_3.setImageResource(R.drawable.ico_puzzle3);
                iv_puzzle_4.setImageResource(R.drawable.ico_puzzle4);
                iv_puzzle_5.setImageResource(R.drawable.ico_puzzle5_focus);
                iv_puzzle_6.setImageResource(R.drawable.ico_puzzle6);
                break;
            case 6:
                ll_puzzle_1.setBackgroundColor(getResources().getColor(R.color.puzzleBg));
                ll_puzzle_2.setBackgroundColor(getResources().getColor(R.color.puzzleBg));
                ll_puzzle_3.setBackgroundColor(getResources().getColor(R.color.puzzleBg));
                ll_puzzle_4.setBackgroundColor(getResources().getColor(R.color.puzzleBg));
                ll_puzzle_5.setBackgroundColor(getResources().getColor(R.color.puzzleBg));
                ll_puzzle_6.setBackgroundColor(getResources().getColor(R.color.wxColor));
                iv_puzzle_1.setImageResource(R.drawable.ico_puzzle1);
                iv_puzzle_2.setImageResource(R.drawable.ico_puzzle2);
                iv_puzzle_3.setImageResource(R.drawable.ico_puzzle3);
                iv_puzzle_4.setImageResource(R.drawable.ico_puzzle4);
                iv_puzzle_5.setImageResource(R.drawable.ico_puzzle5);
                iv_puzzle_6.setImageResource(R.drawable.ico_puzzle6_focus);
                break;
        }
    }
    private void initData() {
        imageBeans = (List<MediaBean>) getIntent().getSerializableExtra("pics");
        width = getIntent().getIntExtra("width",width);
        height = getIntent().getIntExtra("height",height);
        getFileName(imageBeans.size());
        showUi(imageBeans.size());
        //templateDialog = new TemplateDialog(context, imageBeans.size());
        topView.setTitle("拼图");
        topView.setRightWord("完成");
        topView.setLeftIcon(R.drawable.gallery_ic_cross);
        puzzleView.setPics(imageBeans, 380, 380 * height / width);
        if (pathFileName != null) {
            initCoordinateData(pathFileName, 0);
        }
    }

    private void initEvent() {
        templateTv.setOnClickListener(this);

        topView.setOnLeftClickListener(new TopView.OnLeftClickListener() {
            @Override
            public void leftClick() {
                back();
            }
        });
        topView.setOnRightClickListener(new TopView.OnRightClickListener() {
            @Override
            public void rightClick() {
                savePuzzle();
                finish();
            }
        });
    }
    protected void back() {
        setResult(ERROR);
        finish();
    }

    private void getFileName(int picNum) {

        switch (picNum) {
            case 1:
                pathFileName = "one_style";
            case 2:
                pathFileName = "two_style";
                break;
            case 3:
                pathFileName = "three_style";
                break;
            case 4:
                pathFileName = "four_style";
                break;
            case 5:
                pathFileName = "five_style";
                break;
            case 6:
                pathFileName = "six_style";
                break;
            default:
                break;
        }
    }
    private void initCoordinateData(String fileName, int templateNum) {
        //上面是默认的
        if (width != 0 && height != 0) {
            puzzleEntity = handlePuzzle(width, height, imageBeans.size());
        }
        if (puzzleEntity != null && puzzleEntity.getStyle() != null && puzzleEntity.getStyle().get(templateNum).getPic() != null) {
            puzzleView.setPathCoordinate(puzzleEntity.getStyle().get(templateNum).getPic());
        }
    }
    private Puzzle handlePuzzle(int w, int h, int imageCount) {
        int width = 380;
        int height = width * h / w;
        Puzzle p = new Puzzle();
        List<Type> types = new ArrayList<>();
        List<ImageItem> imageitems = new ArrayList<>();
        //用于计算宽度
        int[] imageLineCount = {1, 2, 3, 2, 3, 3};//根据图片的数量计算 宽
        int imageHcount = imageCount > 3 ? 2 : 1;
        //这里5的要另外计算 因为 含两种宽高
        int addw = (width - ((imageLineCount[imageCount - 1] + 1) * lineWidth)) % imageLineCount[imageCount - 1];
        int addh = (width - ((imageHcount + 1) * lineWidth)) % imageHcount;
        //小图宽度与高度
        int sw = (width - ((imageLineCount[imageCount - 1] + 1) * lineWidth) - addw) / imageLineCount[imageCount - 1];
        int sh = (height - (imageHcount + 1) * lineWidth - addh) / imageHcount;
        int[][] xxx = {{1}, {1, 2}, {1, 2, 3}, {1, 2, 1, 2}, {1, 2, 3, 1, 2}, {1, 2, 3, 1, 2, 3}};
        int[] xx = xxx[imageCount - 1];
        for (int i = 0; i < imageCount; i++) {
            List<Coordinates> coordinates = new ArrayList<>();
            int x = sw * (xx[i] - 1) + lineWidth * xx[i];
            int y = sh * ((imageCount > 3) && (i >= (imageCount / 2 + imageCount % 2)) ? 1 : 0) + lineWidth * (imageCount > 3 && i >= (imageCount / 2 + imageCount % 2) ? 2 : 1);
            if (imageCount == 5 && i > 2) {//如果是第5行 的第二排不一样
                sw = (width - (3 * lineWidth) - addw) / 2;
            }
            coordinates.add(new Coordinates(x, y));// 第一个点
            coordinates.add(new Coordinates(x + sw + (i == imageCount - 1 ? addw : 0), y));// 第二个点
            coordinates.add(new Coordinates(x + sw + (i == imageCount - 1 ? addw : 0), y + sh + ((i >= (imageCount / 2 + imageCount % 2)) ? addh : 0)));// 第三个点
            coordinates.add(new Coordinates(x, y + sh + ((i >= (imageCount / 2 + imageCount % 2)) ? addh : 0)));// 第四个点
            imageitems.add(new ImageItem(coordinates));
        }
        types.add(new Type(imageitems));
        p.setStyle(types);
        return p;
    }
    //保存图片
    public static File saveBitmapJPG(String mBitmapName, Bitmap mBitmap) throws IOException {

//        File fileDir = new File(context.getExternalCacheDir() + "/MOPIAN");
        //这里因为是封面调用的 布放入 temp
        File fileDir = new File(AllConstant.CACHEIMAGEPATH);
        if (!fileDir.exists()) fileDir.mkdirs();
        String fileName =mBitmapName + ".jpg";
        File f = new File(fileDir, fileName);
        FileOutputStream fOut = new FileOutputStream(f);
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        fOut.flush();
        fOut.close();
        mBitmap.recycle();
        mBitmap = null;
        return f;
    }
    protected void savePuzzle() {
        buildDrawingCache(puzzleLL);
        puzzleLL.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        Bitmap bitmap = puzzleLL.getDrawingCache().copy(Bitmap.Config.RGB_565, true);
        try {
            File file =  saveBitmapJPG("MOPIAN" + System.currentTimeMillis(), bitmap);
            // 最后通知图库更新
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(file);
            intent.setData(uri);
            sendBroadcast(intent);//这个广播的目的就是更新图库，发了这个广播进入相册就可以找到你保存的图片了！，记得要传你更新的file哦

            Intent reIntent = new Intent();
            reIntent.putExtra("path", file.getPath());
            setResult(Activity.RESULT_OK, reIntent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void buildDrawingCache(View view) {
        try {
            view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        } catch (Exception e) {
            e.printStackTrace();
        }
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
    }

    /**
     * @param number
     * @param isSelected
     */
    //设置拼图
    private void setPics(final int number, boolean isSelected){
        int len = imageBeans.size();
        List<MediaBean> mediaBeens = new ArrayList<>();
        
        if(isSelected){
            for (int i = 0; i < number; i++){
                mediaBeens.add(imageBeans.get(i));
            }
            imageBeans.clear();
            imageBeans.addAll(mediaBeens);
            puzzleView.setPics(imageBeans, 380, 380 * height / width);
            if (pathFileName != null) {//这个 pathfile 是以防以后要用配置文件设置尺寸 目前是用计算的 固定尺寸
                initCoordinateData(pathFileName, 0);
            }
            puzzleView.invalidate();
        }else{
            Toast.makeText(PuzzleActivity.this, "您需要选择" + number + "张图片", Toast.LENGTH_SHORT).show();
            //跳转选图片
            RxGalleryFinal rxGalleryFinal = RxGalleryFinal
                    .with(PuzzleActivity.this)
                    .image()
                    .multiple();
            rxGalleryFinal.maxSize(number)
                    .imageLoader(ImageLoaderType.PICASSO)
                    .subscribe(new RxBusResultDisposable<ImageMultipleResultEvent>() {
                        @Override
                        protected void onEvent(ImageMultipleResultEvent imageMultipleResultEvent) throws Exception {
                            List<MediaBean> list = imageMultipleResultEvent.getResult();
                            if(list != null && list.size() > 0) {
                                imageBeans.clear();
                                imageBeans.addAll(list);
                                setPics(imageBeans.size(), true);
                            }
                            showUi(imageBeans.size());
                        }
                    }).openGallery();
        }
    }
    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.template_tv) {//  templateDialog.show();.

        } else if (i == R.id.ll_puzzle_1) {
            showUi(1);
            setPics(1, false);

        } else if (i == R.id.ll_puzzle_2) {
            showUi(2);
            setPics(2, false);

        } else if (i == R.id.ll_puzzle_3) {
            showUi(3);
            setPics(3, false);

        } else if (i == R.id.ll_puzzle_4) {
            showUi(4);
            setPics(4, false);

        } else if (i == R.id.ll_puzzle_5) {
            showUi(5);
            setPics(5, false);

        } else if (i == R.id.ll_puzzle_6) {
            showUi(6);
            setPics(6, false);

        } else {
        }
    }
}
