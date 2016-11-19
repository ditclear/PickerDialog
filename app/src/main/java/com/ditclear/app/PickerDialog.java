package com.ditclear.app;

import android.content.DialogInterface;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 说明：单选及多选对话框，屏幕旋转时恢复状态
 */

public class PickerDialog<T extends IContent> extends DialogFragment implements PickerAdapter.OnSelectChangeListener {

    public static final String TAG = "PickerDialog";

    private static final String MAX_NUM = "max";
    private static final String SELECTED_NUM = "selected_num";
    private static final String SELECTED_POS_SET = "posSet";
    private PickerAdapter adapter;
    private int width = -1;
    private int height = -1;
    private int maxSelected, hasSelectedNum;
    private String selectedPos;
    private boolean isSingleMode;
    private RecyclerView recyclerView;
    private TextView tipsTv;


    public PickerDialog() {
        // Required empty public constructor
    }

    /**
     * 新建一个dialog
     *
     * @param maxSelected 最大可选数
     * @return
     */
    public static PickerDialog newInstance(int maxSelected) {
        Bundle args = new Bundle();
        args.putInt(MAX_NUM, maxSelected);
        PickerDialog fragment = new PickerDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            maxSelected = getArguments().getInt(MAX_NUM, -1);//-1代表无限制
            isSingleMode = maxSelected == 1;
        }
        // 去掉顶部title
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (getDialog() == null) { // Returns mDialog
            setShowsDialog(false);
        }
        super.onActivityCreated(savedInstanceState);
        final DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        final WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        if (width > 0) {
            layoutParams.width = width;
        } else {
            layoutParams.width = dm.widthPixels / 3 * 2;
        }
        if (height > 0) {
            layoutParams.height = height;
        } else {
            layoutParams.height = dm.heightPixels / 3 * 2;
        }
        getDialog().onWindowAttributesChanged(layoutParams);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Toast.makeText(getActivity(), adapter.getSelectedPos(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MAX_NUM, maxSelected);
        outState.putInt(SELECTED_NUM, hasSelectedNum);
        outState.putString(SELECTED_POS_SET, adapter.getSelectedPos());
    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState == null) {
            return;
        }
        hasSelectedNum = savedInstanceState.getInt(SELECTED_NUM, 0);
        selectedPos = savedInstanceState.getString(SELECTED_POS_SET);
        tipsTv.setText(String.format(getString(R.string.has_selected), String.valueOf(hasSelectedNum)));
        if (adapter != null) {
            adapter.setSelectedPosSet(getSelectedPos());
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
        initEvent();
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        return view;
    }

    /**
     * 添加内容
     */
    private void initEvent() {

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerView.setAdapter(adapter = new PickerAdapter(maxSelected, new PickerItem.PickerItems().getList(), getActivity()));
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
        }
        if (adapter != null) {
            adapter.setSelectedPosSet(getSelectedPos());
        }
    }

    @Override
    public void onSelect(int pos, int hasSelectedNum) {
        this.hasSelectedNum = hasSelectedNum;

        if (isSingleMode) {
            this.dismiss();
            return;
        }
        tipsTv.setText(String.format(getString(R.string.has_selected), String.valueOf(hasSelectedNum)));
    }

    @Override
    public void onStop() {
        this.selectedPos = adapter.getSelectedPos();
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
}
