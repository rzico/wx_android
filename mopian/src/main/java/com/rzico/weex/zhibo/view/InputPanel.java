package com.rzico.weex.zhibo.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rzico.weex.R;
import com.rzico.weex.WXApplication;
import com.rzico.weex.utils.ScreenHelper;

import java.util.Timer;
import java.util.TimerTask;


public class InputPanel extends LinearLayout  implements View.OnLayoutChangeListener{

    private final static String TAG = "InputPanel";

    private ViewGroup inputBar;
    private EditText textEditor;
    private ImageView emojiBtn;
    private TextView sendBtn;
    private EmojiBoard emojiBoard;
    private Context context;
    private InputPanelListener listener;

    boolean isshow=false;
    public interface InputPanelListener{
        void onSendClick(String text);
    }

    public InputPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        initView();
    }


    /**
     * 将焦点移到输入框，弹起输入法
     */
    public void focusKeywordView() {
        if (textEditor != null) {
            textEditor.requestFocus();
            textEditor.setSelection(textEditor.getText().length());
            showInputMethod(textEditor, true, 500);
        }
    }

    /**
     * 弹起输入法
     * @param edit
     * @param delay
     * @param delayTime
     */
    private void showInputMethod(final EditText edit, boolean delay, int delayTime) {
        if (delay) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    InputMethodManager imm = (InputMethodManager) WXApplication.getInstance().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(edit, 0);
                    }

                }
            }, delayTime);
        } else {
            InputMethodManager imm = (InputMethodManager) WXApplication.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edit, 0);
        }
    }
    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.widget_input_panel, this);
        keyHeight = ScreenHelper.getScreenHeightPix(context)/3;
        inputBar = (ViewGroup) findViewById(R.id.input_bar);
        textEditor = (EditText) findViewById(R.id.input_editor);
        emojiBtn = (ImageView) findViewById(R.id.input_emoji_btn);
        sendBtn = (TextView) findViewById(R.id.input_send);
        emojiBoard = (EmojiBoard) findViewById(R.id.input_emoji_board);
        imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(textEditor, InputMethodManager.HIDE_NOT_ALWAYS);
        textEditor.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(emojiBoard.getVisibility()==VISIBLE){
                    emojiBoard.setVisibility(GONE);
                }
                isshow=false;

//                inputBar.setSelected(hasFocus);
                emojiBtn.setSelected(emojiBoard.getVisibility() == VISIBLE);
            }
        });
        textEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==0){
                    sendBtn.setEnabled(false);
                }else
                    sendBtn.setEnabled(true);
            }
        });

        emojiBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emojiBoard.getVisibility()==VISIBLE){
                    emojiBoard.setVisibility(GONE);
                    imm.showSoftInput(textEditor, InputMethodManager.SHOW_FORCED);
                }else{
                    emojiBoard.setVisibility(VISIBLE);
                    imm.hideSoftInputFromWindow(textEditor.getWindowToken(), 0);
                }
                emojiBtn.setSelected(emojiBoard.getVisibility() == VISIBLE);
            }
        });

        sendBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(textEditor.getText().toString().equals(""))
                    return;
                if (listener != null) {
                    listener.onSendClick(textEditor.getText().toString());
                }
                textEditor.getText().clear();
            }
        });

        emojiBoard.setItemClickListener(new EmojiBoard.OnEmojiItemClickListener() {
            @Override
            public void onClick(String code) {
                if (code.equals("/DEL")) {
                    textEditor.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                } else {
                    textEditor.getText().insert(textEditor.getSelectionStart(), code);
                }
            }
        });
    }
    private boolean isshowjianpan=false;
    //软件盘弹起后所占高度阀值
    private int keyHeight = 0;
    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {
            isshowjianpan = true;
//           showToast("监听到软键盘弹起...");

        } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight)) {
            isshowjianpan = false;
//            showToast(  "监听到软件盘关闭...");

        }
    }
    public void setPanelListener(InputPanelListener l) {
        listener = l;
    }

    /**
     * back键或者空白区域点击事件处理
     *
     * @return 已处理true, 否则false
     */
    public boolean onBackAction() {
        if (emojiBoard.getVisibility() == VISIBLE) {
            emojiBoard.setVisibility(GONE);
            emojiBtn.setSelected(false);
            return true;
        }
        hideKeyboard();
        return false;
    }
    private InputMethodManager imm;
    private void hideKeyboard() {
        imm.hideSoftInputFromWindow(textEditor.getWindowToken(), 0);
    }

}
