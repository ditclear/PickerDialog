package com.ditclear.app;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 说明：单选及多选对话框，屏幕旋转时恢复状态
 */

public class PickerDialog<T extends IContent> extends DialogFragment implements PickerAdapter.OnSelectChangeListener, DialogInterface.OnKeyListener {

    public static final String TAG = "PickerDialog";
    private static final String MAX_NUM = "max";
    private static final String TITLE = "title";
    private static final String SELECTED_NUM = "selected_num";
    private static final String SELECTED_POS_SET = "posSet";
    private static final String SOURCE = "source";
    private PickerAdapter adapter;
    private int maxSelected, hasSelectedNum;
    private String selectedPos;
    private String title;
    private boolean isSingleMode;
    private RecyclerView recyclerView;
    private TextView tipsTv;
    private GridLayoutManager layoutManager;
    private List<T> mList;
    private OnSelectedListener mOnSelectedListener;
    private TextView titleTv;
    private TextView submitBtn;
    private boolean backPressed = false;

    public PickerDialog() {
        // Required empty public constructor
    }

    /**
     * 新建一个dialog
     *
     * @param maxSelected 最大可选数
     * @return
     */
    public static PickerDialog newInstance(int maxSelected, String title, ArrayList<? extends IContent> list) {
        Bundle args = new Bundle();
        args.putInt(MAX_NUM, maxSelected);
        args.putString(TITLE, title);
        args.putParcelableArrayList(SOURCE, list);
        PickerDialog fragment = new PickerDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public void setSourceList(List<T> list) {
        this.mList = list;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            maxSelected = getArguments().getInt(MAX_NUM, -1);//-1代表无限制
            title = getArguments().getString(TITLE, "标题");
            mList = getArguments().getParcelableArrayList(SOURCE);
            isSingleMode = maxSelected == 1;
        }
        backPressed=false;
        // 去掉顶部title
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (getDialog() == null) { // Returns mDialog
            setShowsDialog(false);
        }
        super.onActivityCreated(savedInstanceState);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        layoutParams.width = dm.widthPixels / 3 * 2;
        layoutParams.height = dm.heightPixels / 3 * 2;
        getDialog().onWindowAttributesChanged(layoutParams);
        getDialog().setCanceledOnTouchOutside(false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_NUM, hasSelectedNum);
        outState.putString(SELECTED_POS_SET, adapter.getSelectedPos());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Configuration newConfig = getActivity().getResources().getConfiguration();
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager.setSpanCount(2);
        } else {
            layoutManager.setSpanCount(3);
        }
        recyclerView.setLayoutManager(layoutManager);
        if (savedInstanceState == null) {
            return;
        }
        hasSelectedNum = savedInstanceState.getInt(SELECTED_NUM, 0);
        selectedPos = savedInstanceState.getString(SELECTED_POS_SET);
        tipsTv.setText(String.format(getString(R.string.has_selected), String.valueOf(hasSelectedNum)));
        if (adapter != null) {
            adapter.setSelectedPosSet(getSelectedPos());
            adapter.setList(mList);
        }

    }

    private int[] stringToInt(String str) {
        if (isEmpty(str)) {
            return null;
        }
        String array[] = str.split("\\|");
        int posSet[] = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            try {
                posSet[i] = Integer.parseInt(array[i]);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return posSet;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.dialog_picker, null, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        tipsTv = (TextView) view.findViewById(R.id.tip_tv);
        titleTv = (TextView) view.findViewById(R.id.title_tv);
        submitBtn = (TextView) view.findViewById(R.id.submit);
        initEvent();
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        return view;
    }

    /**
     * 添加内容
     */
    private void initEvent() {

        recyclerView.setLayoutManager(layoutManager = new GridLayoutManager(getActivity(), 2));
        recyclerView.setAdapter(adapter = new PickerAdapter(maxSelected, mList, getActivity()));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.left = 10;
                outRect.right = 10;
                outRect.bottom = 5;
                outRect.top = 5;
            }

        });
        adapter.setOnSelectChangeListener(this);
        if (!isSingleMode) {
            tipsTv.setText(String.format(getString(R.string.has_selected), String.valueOf(hasSelectedNum)));
        } else {
            submitBtn.setVisibility(View.GONE);
        }
        titleTv.setText(title);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mOnSelectedListener != null) {
                    mOnSelectedListener.onSelected(adapter.getSelectedItems());
                }
            }
        });
        if (adapter != null) {
            adapter.setSelectedPosSet(getSelectedPos());
        }
    }

    @Override
    public void onSelect(int pos, int hasSelectedNum) {
        this.hasSelectedNum = hasSelectedNum;

        if (isSingleMode) {
            getView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismiss();
                    if (mOnSelectedListener != null) {
                        mOnSelectedListener.onSelected(adapter.getSelectedItems());
                    }
                }
            }, 300);

            return;
        }
        tipsTv.setText(String.format(getString(R.string.has_selected), String.valueOf(hasSelectedNum)));
    }

    @Override
    public void onStop() {
        if (!backPressed) {
            this.selectedPos = adapter.getSelectedPos();
        }
        super.onStop();
    }

    @Override
    public void unSelect(int pos) {
        hasSelectedNum--;
        if (!isSingleMode) {
            tipsTv.setText(String.format(getString(R.string.has_selected), String.valueOf(hasSelectedNum)));
        }
    }

    private boolean isEmpty(String str) {
        if (TextUtils.isEmpty(str) || "".equals(str)) {
            return true;
        }
        return false;
    }

    public int[] getSelectedPos() {
        return stringToInt(selectedPos);
    }

    public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
        mOnSelectedListener = onSelectedListener;
    }

    /**
     * Called when a key is dispatched to a dialog. This allows listeners to get a chance to respond
     * before the dialog.
     *
     * @param dialog  The dialog the key has been dispatched to.
     * @param keyCode The code for the physical key that was pressed
     * @param event   The KeyEvent object containing full information about the event.
     * @return True if the listener has consumed the event, false otherwise.
     */
    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backPressed = true;
        }
        return false;
    }

    public interface OnSelectedListener {
        void onSelected(List<? extends IContent> pos);
    }
}
