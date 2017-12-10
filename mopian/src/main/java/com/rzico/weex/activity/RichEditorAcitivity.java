package com.rzico.weex.activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.rzico.weex.R;
import com.rzico.weex.model.info.Message;
import com.rzico.weex.module.JSCallBaskManager;
import com.rzico.weex.utils.BarTextColorUtils;
import com.yalantis.ucrop.view.TopView;

import java.io.Console;
import java.util.List;

import cn.finalteam.rxgalleryfinal.utils.OsCompat;
import cn.finalteam.rxgalleryfinal.utils.ThemeUtils;
import jp.wasabeef.richeditor.RichEditor;

public class RichEditorAcitivity extends AppCompatActivity {


    private RichEditor mEditor;
    private TextView mPreview;
    private final int firstFontSize = 3;

    private TopView topView;

    private boolean blBold = false, blItalic = false, blStrikethrough = false, blUnderline = false, blBlack = true,blBlue = false, blRed = false;
    private ImageButton btnBold,btnItalic, btnStrikethrough, btnUnderline, btnLeft, btnRight, btnCenter, btnH2, btnH3, btnH4;
    private LinearLayout btnRed, btnBlack, btnBlue;
    private TextView tvSelectAll;

    private String htmlContent;


    private boolean boldsOpen =  false;//底部弹窗 是否弹出
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BarTextColorUtils.StatusBarLightMode(this, R.color.wxColor);
        setContentView(R.layout.activity_rich_editor);
        topView = (TopView) findViewById(R.id.toolbar);

        mEditor = (RichEditor) findViewById(R.id.editor);
        btnBold = (ImageButton)findViewById(R.id.action_bold);
        btnItalic = (ImageButton)findViewById(R.id.action_italic);
//        btnStrikethrough =  (ImageButton)findViewById(R.id.action_strikethrough);
        btnUnderline = (ImageButton)findViewById(R.id.action_underline);
        btnLeft = (ImageButton)findViewById(R.id.action_align_left);
        btnRight = (ImageButton)findViewById(R.id.action_align_right);
        btnCenter = (ImageButton)findViewById(R.id.action_align_center);
        btnRed = (LinearLayout)findViewById(R.id.action_txt_red);
        btnBlack = (LinearLayout)findViewById(R.id.action_txt_black);
        btnBlue = (LinearLayout)findViewById(R.id.action_txt_blue);
        btnH2 = (ImageButton)findViewById(R.id.action_font_h2);
        btnH3 = (ImageButton)findViewById(R.id.action_font_h3);
        btnH4 = (ImageButton)findViewById(R.id.action_font_h4);

        tvSelectAll = (TextView)findViewById(R.id.select_all);
//        mPreview = (TextView) findViewById(R.id.preview);
        initToolBar();
        mEditor.setEditorHeight(200);
        mEditor.setEditorFontSize(16);
        mEditor.setEditorFontColor(getResources().getColor(R.color.txt_black));
        mEditor.setEditorHeading(2);
        mEditor.setPadding(10, 10, 10, 10);
        mEditor.setPlaceholder("Insert text here...");
        //获取焦点并且设置内容<h2>
        mEditor.focusEditor();
        String centent = getIntent().getStringExtra("data");
        if(centent != null && !centent.equals("")){
            mEditor.setHtml(centent);
        }
        mEditor.setHeading(firstFontSize);
        mEditor.focusEditor();
//        mEditor.setTextColor(getResources().getColor(R.color.txt_black));
        //mEditor.setInputEnabled(false);
//        mPreview = (TextView) findViewById(R.id.preview);
        mEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {
                //如果删到空的话 要默认加上h3了 这里面每次是空的话 只能执行一次
                if(text.equals("")){
                    mEditor.setHeading(firstFontSize);
                    mEditor.focusEditor();
                    //把所有状态都设置成初始化
                    initBtnState();
                }
                //这是换行
                if (text.endsWith("</div>")) {
                        mEditor.setHeading(firstFontSize);
                        mEditor.focusEditor();
                        //这里要判断当前的状态初始化输入状态
                        if(blBold){
                            mEditor.setBold();
                        }
                        if(blItalic){
                            mEditor.setItalic();
                        }
                        if(blUnderline){
                            mEditor.setUnderline();
                        }
                        if(blRed){
                            mEditor.setTextColor(getResources().getColor(R.color.txt_red));
                        }
                        if(blBlack){
                            mEditor.setTextColor(getResources().getColor(R.color.txt_black));
                        }
                        if(blBlue){
                            mEditor.setTextColor(getResources().getColor(R.color.txt_blue));
                        }
                }
                //
                htmlContent = text;
//                mPreview.setText(text);
//                mEditor.getSelection();
            }

            @Override
            public void onStateChange(String text, List<RichEditor.Type> types) {
                changeState(text, types);
            }

            @Override
            public void onSelectionChange(String s) {
                if (s.equals("")){
                    tvSelectAll.setText("全选");
                }else {
                    tvSelectAll.setText("取消全选");
                }
            }
        });
        mEditor.setOnDecorationChangeListener(new RichEditor.OnDecorationStateListener() {
            @Override
            public void onStateChangeListener(String text, List<RichEditor.Type> types) {
                changeState(text, types);
            }
        });

        tvSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                initBtnState();

//                btnRed.setBackgroundResource(R.color.white);
//                btnBlack.setBackgroundResource(R.color.white);
//                btnBlue.setBackgroundResource(R.color.white);
//                btnLeft.setImageResource(R.drawable.ico_left);
//                btnLeft.setBackgroundResource(R.color.white);
//                btnCenter.setImageResource(R.drawable.ico_center);
//                btnCenter.setBackgroundResource(R.color.white);
//                btnRight.setImageResource(R.drawable.ico_right);
//                btnRight.setBackgroundResource(R.color.white);

                if(tvSelectAll.getText().equals("全选")){
                    mEditor.selectAll();
                    tvSelectAll.setText("取消全选");
                }else{
                    tvSelectAll.setText("全选");
                    mEditor.focusEditor();
                }
            }
        });

        findViewById(R.id.action_undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.undo();
            }
        });

        findViewById(R.id.action_redo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.redo();
            }
        });


        btnBold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //改变前端样式
                if(!blBold){
                    btnBold.setImageResource(R.drawable.ico_bold_focus);
                    btnBold.setBackgroundResource(R.drawable.corner_button_bg);
                    blBold = true;
                }else{
                    btnBold.setImageResource(R.drawable.ico_bold);
                    btnBold.setBackgroundColor(getResources().getColor(R.color.rich_editor_bottom));
                    blBold = false;
                }
                mEditor.setBold();
            }
        });

        btnItalic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //改变前端样式
                if(!blItalic){
                    btnItalic.setImageResource(R.drawable.ico_italic_focus);
                    btnItalic.setBackgroundResource(R.drawable.corner_button_bg);
                    blItalic = true;
                }else{
                    btnItalic.setImageResource(R.drawable.ico_italic);
                    btnItalic.setBackgroundColor(getResources().getColor(R.color.rich_editor_bottom));
                    blItalic = false;
                }
                mEditor.setItalic();
            }
        });


        btnUnderline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //改变前端样式
                if(!blUnderline){
                    btnUnderline.setImageResource(R.drawable.ico_underline_focus);
                    btnUnderline.setBackgroundResource(R.drawable.corner_button_bg);
                    blUnderline = true;

                }else{
                    btnUnderline.setImageResource(R.drawable.ico_underline);
                    btnUnderline.setBackgroundColor(getResources().getColor(R.color.rich_editor_bottom));
                    blUnderline = false;
                }
                mEditor.setUnderline();
            }
        });

//        btnStrikethrough.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //改变前端样式
//                if(!blStrikethrough){
//                    btnStrikethrough.setImageResource(R.drawable.ico_strikethrough_focus);
//                    btnStrikethrough.setBackgroundResource(R.drawable.corner_button_bg);
//                    blStrikethrough = true;
//                    if(blUnderline){
//                        mEditor.setUnderline();
//                        btnUnderline.setImageResource(R.drawable.ico_underline);
//                        btnUnderline.setBackgroundColor(getResources().getColor(R.color.white));
//                        blUnderline = false;
//                    }
//                }else{
//                    btnStrikethrough.setImageResource(R.drawable.ico_strikethrough);
//                    btnStrikethrough.setBackgroundColor(getResources().getColor(R.color.white));
//                    blStrikethrough = false;
//                }
//
//                mEditor.setStrikeThrough();
//            }
//        });
        findViewById(R.id.action_font_h2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(2);

                btnH2.setImageResource(R.drawable.ico_h1_focus);
                btnH2.setBackgroundResource(R.drawable.corner_button_bg);
                btnH3.setImageResource(R.drawable.ico_h2);
                btnH3.setBackgroundResource(R.color.rich_editor_bottom);
                btnH4.setImageResource(R.drawable.ico_h3);
                btnH4.setBackgroundResource(R.color.rich_editor_bottom);
            }
        });

        findViewById(R.id.action_font_h3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(3);

                btnH2.setImageResource(R.drawable.ico_h1);
                btnH2.setBackgroundResource(R.color.rich_editor_bottom);
                btnH3.setImageResource(R.drawable.ico_h2_focus);
                btnH3.setBackgroundResource(R.drawable.corner_button_bg);
                btnH4.setImageResource(R.drawable.ico_h3);
                btnH4.setBackgroundResource(R.color.rich_editor_bottom);
            }
        });

        findViewById(R.id.action_font_h4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(4);

                btnH2.setImageResource(R.drawable.ico_h1);
                btnH2.setBackgroundResource(R.color.rich_editor_bottom);
                btnH3.setImageResource(R.drawable.ico_h2);
                btnH3.setBackgroundResource(R.color.rich_editor_bottom);
                btnH4.setImageResource(R.drawable.ico_h3_focus);
                btnH4.setBackgroundResource(R.drawable.corner_button_bg);
            }
        });



        btnRed.setOnClickListener(new View.OnClickListener() {
//            private boolean isChanged;

            @Override
            public void onClick(View v) {
                btnRed.setBackgroundResource(R.drawable.corner_button_bg);
                btnBlack.setBackgroundResource(R.color.rich_editor_bottom);
                btnBlue.setBackgroundResource(R.color.rich_editor_bottom);
                mEditor.setTextColor(getResources().getColor(R.color.txt_red));
                blBlack = false;
                blBlue  = false;
                blRed   = true;

            }
        });
        btnBlack.setOnClickListener(new View.OnClickListener() {
//            private boolean isChanged;

            @Override
            public void onClick(View v) {
                btnRed.setBackgroundResource(R.color.rich_editor_bottom);
                btnBlack.setBackgroundResource(R.drawable.corner_button_bg);
                btnBlue.setBackgroundResource(R.color.rich_editor_bottom);
                mEditor.setTextColor(getResources().getColor(R.color.txt_black));
                blBlack = true;
                blBlue  = false;
                blRed   = false;

            }
        });
        btnBlue.setOnClickListener(new View.OnClickListener() {
//            private boolean isChanged;

            @Override
            public void onClick(View v) {
                btnRed.setBackgroundResource(R.color.rich_editor_bottom);
                btnBlack.setBackgroundResource(R.color.rich_editor_bottom);
                btnBlue.setBackgroundResource(R.drawable.corner_button_bg);
                 mEditor.setTextColor(getResources().getColor(R.color.txt_blue));
                blBlack = false;
                blBlue    = true;
                blRed   = false;

            }
        });


        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLeft.setImageResource(R.drawable.ico_left_focus);
                btnLeft.setBackgroundResource(R.drawable.corner_button_bg);
                btnCenter.setImageResource(R.drawable.ico_center);
                btnCenter.setBackgroundResource(R.color.rich_editor_bottom);
                btnRight.setImageResource(R.drawable.ico_right);
                btnRight.setBackgroundResource(R.color.rich_editor_bottom);
                mEditor.setAlignLeft();
            }
        });

        btnCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLeft.setImageResource(R.drawable.ico_left);
                btnLeft.setBackgroundResource(R.color.rich_editor_bottom);
                btnCenter.setImageResource(R.drawable.ico_center_focus);
                btnCenter.setBackgroundResource(R.drawable.corner_button_bg);
                btnRight.setImageResource(R.drawable.ico_right);
                btnRight.setBackgroundResource(R.color.rich_editor_bottom);
                mEditor.setAlignCenter();
            }
        });

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLeft.setImageResource(R.drawable.ico_left);
                btnLeft.setBackgroundResource(R.color.rich_editor_bottom);
                btnCenter.setImageResource(R.drawable.ico_center);
                btnCenter.setBackgroundResource(R.color.rich_editor_bottom);
                btnRight.setImageResource(R.drawable.ico_right_focus);
                btnRight.setBackgroundResource(R.drawable.corner_button_bg);
                mEditor.setAlignRight();
            }
        });




        mEditor.openKeyboard();
    }

    private void initBtnState(){
        btnBold.setImageResource(R.drawable.ico_bold);
        btnBold.setBackgroundColor(getResources().getColor(R.color.rich_editor_bottom));
        btnItalic.setImageResource(R.drawable.ico_italic);
        btnItalic.setBackgroundColor(getResources().getColor(R.color.rich_editor_bottom));
        btnUnderline.setImageResource(R.drawable.ico_underline);
        btnUnderline.setBackgroundColor(getResources().getColor(R.color.rich_editor_bottom));

        btnLeft.setImageResource(R.drawable.ico_left_focus);
        btnLeft.setBackgroundResource(R.drawable.corner_button_bg);
        btnCenter.setImageResource(R.drawable.ico_center);
        btnCenter.setBackgroundResource(R.color.rich_editor_bottom);
        btnRight.setImageResource(R.drawable.ico_right);
        btnRight.setBackgroundResource(R.color.rich_editor_bottom);
        blBold = false;
        blItalic = false;
        blUnderline = false;
        blStrikethrough = false;
    }

    private void changeState(String text, List<RichEditor.Type> types){
        System.out.println("texttext:" + text);
        System.out.println("style:");
        initBtnState();
        for (RichEditor.Type item: types) {
            System.out.println("item:" + types);
            switch (item){
                case BOLD:
                    btnBold.setImageResource(R.drawable.ico_bold_focus);
                    btnBold.setBackgroundResource(R.drawable.corner_button_bg);
                    blBold = true;
                    break;
                case ITALIC:
                    btnItalic.setImageResource(R.drawable.ico_italic_focus);
                    btnItalic.setBackgroundResource(R.drawable.corner_button_bg);
                    blItalic = true;
                    break;
                case UNDERLINE:
                    btnUnderline.setImageResource(R.drawable.ico_underline_focus);
                    btnUnderline.setBackgroundResource(R.drawable.corner_button_bg);
                    blUnderline = true;
                    break;
                case STRIKETHROUGH:
//                            btnStrikethrough.setImageResource(R.drawable.ico_strikethrough_focus);
//                            btnStrikethrough.setBackgroundResource(R.drawable.corner_button_bg);
//                            blStrikethrough = true;
                    break;
                case JUSTIFYCENTER:

                    btnLeft.setImageResource(R.drawable.ico_left);
                    btnLeft.setBackgroundResource(R.color.rich_editor_bottom);
                    btnCenter.setImageResource(R.drawable.ico_center_focus);
                    btnCenter.setBackgroundResource(R.drawable.corner_button_bg);
                    btnRight.setImageResource(R.drawable.ico_right);
                    btnRight.setBackgroundResource(R.color.rich_editor_bottom);

                    break;
                case JUSTUFYLEFT:
                    btnLeft.setImageResource(R.drawable.ico_left_focus);
                    btnLeft.setBackgroundResource(R.drawable.corner_button_bg);
                    btnCenter.setImageResource(R.drawable.ico_center);
                    btnCenter.setBackgroundResource(R.color.rich_editor_bottom);
                    btnRight.setImageResource(R.drawable.ico_right);
                    btnRight.setBackgroundResource(R.color.rich_editor_bottom);
                    break;
                case JUSTIFYRIGHT:
                    btnLeft.setImageResource(R.drawable.ico_left);
                    btnLeft.setBackgroundResource(R.color.rich_editor_bottom);
                    btnCenter.setImageResource(R.drawable.ico_center);
                    btnCenter.setBackgroundResource(R.color.rich_editor_bottom);
                    btnRight.setImageResource(R.drawable.ico_right_focus);
                    btnRight.setBackgroundResource(R.drawable.corner_button_bg);
                    break;

                case H2:
                    btnH2.setImageResource(R.drawable.ico_h1_focus);
                    btnH2.setBackgroundResource(R.drawable.corner_button_bg);
                    btnH3.setImageResource(R.drawable.ico_h2);
                    btnH3.setBackgroundResource(R.color.rich_editor_bottom);
                    btnH4.setImageResource(R.drawable.ico_h3);
                    btnH4.setBackgroundResource(R.color.rich_editor_bottom);
                    break;

                case H3:
                    btnH2.setImageResource(R.drawable.ico_h1);
                    btnH2.setBackgroundResource(R.color.rich_editor_bottom);
                    btnH3.setImageResource(R.drawable.ico_h2_focus);
                    btnH3.setBackgroundResource(R.drawable.corner_button_bg);
                    btnH4.setImageResource(R.drawable.ico_h3);
                    btnH4.setBackgroundResource(R.color.rich_editor_bottom);
                    break;

                case H4:
                    btnH2.setImageResource(R.drawable.ico_h1);
                    btnH2.setBackgroundResource(R.color.rich_editor_bottom);
                    btnH3.setImageResource(R.drawable.ico_h2);
                    btnH3.setBackgroundResource(R.color.rich_editor_bottom);
                    btnH4.setImageResource(R.drawable.ico_h3_focus);
                    btnH4.setBackgroundResource(R.drawable.corner_button_bg);
                    break;
            }
        }
        setFontColorState(text);
    }
    private void initToolBar() {
        topView = (TopView) findViewById(R.id.toolbar);
        topView.setTitle("编辑文字");
        topView.setRightWord("完成");
        topView.setLeftIcon(cn.finalteam.rxgalleryfinal.R.drawable.gallery_ic_cross);
        topView.setOnLeftClickListener(new TopView.OnLeftClickListener() {
            @Override
            public void leftClick() {
                String key = getIntent().getStringExtra("key");
                if(key != null && !key.equals("") && JSCallBaskManager.get(key) != null){

                    Message message = new Message();
                    message.setError("用户取消");
                    JSCallBaskManager.get(key).invoke(message);
                    JSCallBaskManager.remove(key);
                }
                finish();
            }
        });
        topView.setOnRightClickListener(new TopView.OnRightClickListener() {
            @Override
            public void rightClick() {
                String key = getIntent().getStringExtra("key");
                if(key != null && !key.equals("") && JSCallBaskManager.get(key) != null){
                    Message message = new Message();
                    message.setType("success");
                    message.setContent("success");
                    //过滤 字符
                    message.setData(htmlContent);
                    JSCallBaskManager.get(key).invoke(message);
                    JSCallBaskManager.remove(key);
                }
                finish();
            }
        });

    }

    private StateListDrawable createDefaultOverButtonBgDrawable() {
        int dp12 = (int) ThemeUtils.applyDimensionDp(this, 12.f);
        int dp8 = (int) ThemeUtils.applyDimensionDp(this, 8.f);
        float dp4 = ThemeUtils.applyDimensionDp(this, 4.f);
        float[] round = new float[]{dp4, dp4, dp4, dp4, dp4, dp4, dp4, dp4};
        ShapeDrawable pressedDrawable = new ShapeDrawable(new RoundRectShape(round, null, null));
        pressedDrawable.setPadding(dp12, dp8, dp12, dp8);
        int pressedColor = ThemeUtils.resolveColor(this, cn.finalteam.rxgalleryfinal.R.attr.gallery_toolbar_over_button_pressed_color, cn.finalteam.rxgalleryfinal.R.color.gallery_default_toolbar_bg);
        pressedDrawable.getPaint().setColor(pressedColor);

        int normalColor = ThemeUtils.resolveColor(this, cn.finalteam.rxgalleryfinal.R.attr.gallery_toolbar_over_button_normal_color, cn.finalteam.rxgalleryfinal.R.color.gallery_default_toolbar_bg);
        ShapeDrawable normalDrawable = new ShapeDrawable(new RoundRectShape(round, null, null));
        normalDrawable.setPadding(dp12, dp8, dp12, dp8);
        normalDrawable.getPaint().setColor(normalColor);

        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressedDrawable);
        stateListDrawable.addState(new int[]{}, normalDrawable);

        return stateListDrawable;
    }

    public void setFontColorState(String text) {
        //处理颜色
        if(text.contains("RGB")){
            try{
                int index = text.indexOf("RGB");

                String rgb = text.substring(index);
                int first = rgb.indexOf("(");
                int end   = rgb.indexOf(")");
                rgb = rgb.substring(first + 1, end);
                String[] rgbs = rgb.split(",");
                String hexRgb = "#";
                for (String it: rgbs){
                    String hex = Integer.toHexString(Integer.valueOf(it.trim())).toUpperCase();
                    if(hex.length() == 1){
                        hex = "0"+hex;
                    }
                    hexRgb += hex;
                }
                int lll = getResources().getColor(R.color.txt_blue);
                String black = String.format("#%06X", (0xFFFFFF & getResources().getColor(R.color.txt_black)));
                String blue = String.format("#%06X", (0xFFFFFF & getResources().getColor(R.color.txt_blue)));
                String red = String.format("#%06X", (0xFFFFFF & getResources().getColor(R.color.txt_red)));

                if(hexRgb.equals(black)){
                    btnBlack.setBackgroundResource(R.drawable.corner_button_bg);
                    btnBlue.setBackgroundResource(R.color.rich_editor_bottom);
                    btnRed.setBackgroundResource(R.color.rich_editor_bottom);
                    blBlack = true;
                    blBlue = false;
                    blRed = false;
                }else if(hexRgb.equals(blue)){
                    btnBlack.setBackgroundResource(R.color.rich_editor_bottom);
                    btnBlue.setBackgroundResource(R.drawable.corner_button_bg);
                    btnRed.setBackgroundResource(R.color.rich_editor_bottom);
                    blBlack = false;
                    blBlue = true;
                    blRed = false;
                }else if(hexRgb.equals(red)){
                    btnBlack.setBackgroundResource(R.color.rich_editor_bottom);
                    btnBlue.setBackgroundResource(R.color.rich_editor_bottom);
                    btnRed.setBackgroundResource(R.drawable.corner_button_bg);
                    blBlack = false;
                    blBlue  = false;
                    blRed = true;
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
}
