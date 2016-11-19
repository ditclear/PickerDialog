package com.ditclear.app;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 页面描述：
 * <p>
 * Created by ditclear on 2016/11/19.
 */

public class PickerAdapter<T extends IContent> extends RecyclerView.Adapter<PickerAdapter.ItemHolder> {
    SparseBooleanArray mCheckStates = new SparseBooleanArray();
    boolean lockState = false;
    String content;
    ArrayList<T> selItems = new ArrayList<>();
    Set<Integer> mSelectedPosSet = new HashSet<>();
    String selectedPos;
    private List<T> mList;
    private Context context;
    private int maxSelected;
    private OnSelectChangeListener mOnSelectChangeListener;

    public PickerAdapter(int maxSelected, List<T> list, Context context) {
        this.maxSelected = maxSelected;
        mList = list;
        this.context = context;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_picker, null, false);
        return new ItemHolder(v);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {

        holder.cbx.setTag(position);
        holder.cbx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (lockState) return;
                int pos = (int) buttonView.getTag();
                if (mCheckStates.get(pos, false) == isChecked) return;
                if (isChecked) {
                    if (mCheckStates.size() == maxSelected) {
                        //不然cbx改变状态.
                        lockState = true;
                        buttonView.setChecked(!isChecked);
                        lockState = false;
                        Toast.makeText(context, "不能大于" + maxSelected, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mCheckStates.put(pos, true);
                    //do something
                    if (mOnSelectChangeListener != null) {
                        mOnSelectChangeListener.onSelect(pos, mCheckStates.size());
                    }

                } else {
                    mCheckStates.delete(pos);
                    //do something else
                    if (mOnSelectChangeListener != null) {
                        mOnSelectChangeListener.unSelect(pos);
                    }
                }
            }
        });
        holder.cbx.setText(mList.get(position).getDesc());
        holder.cbx.setChecked(mCheckStates.get(position, false));
    }

    public String getContent() {
        content = "";
        for (int i = 0; i < mCheckStates.size(); i++) {
            if (mCheckStates.valueAt(i)) {
                content += mList.get(mCheckStates.keyAt(i)).getDesc();
            }
        }
        return content;
    }

    public void setSelectedPosSet(int... posSet) {
        if (posSet==null){
            return;
        }
        for (int pos : posSet) {
            mCheckStates.put(pos,true);
        }
    }

    ArrayList<T> getSelectedItems() {
        selItems.clear();
        for (int i = 0; i < mCheckStates.size(); i++) {
            if (mCheckStates.valueAt(i)) {
                selItems.add(mList.get(mCheckStates.keyAt(i)));
            }
        }
        return selItems;
    }

    String getSelectedPos() {
        selectedPos = "";
        for (int i = 0; i < mCheckStates.size(); i++) {
            if (mCheckStates.valueAt(i)) {
                if (maxSelected==1){
                    selectedPos=mCheckStates.keyAt(i)+"";
                }else {
                    selectedPos += mCheckStates.keyAt(i) + "|";
                }
            }
        }
        return selectedPos;
    }

    public int getItemCount() {
        return mList.size();
    }

    public void setOnSelectChangeListener(OnSelectChangeListener onSelectChangeListener) {
        mOnSelectChangeListener = onSelectChangeListener;
    }

    public interface OnSelectChangeListener {

        void onSelect(int pos, int selectedSize);

        void unSelect(int pos);
    }

    static class ItemHolder extends RecyclerView.ViewHolder {
        CheckBox cbx;

        public ItemHolder(View itemView) {
            super(itemView);
            cbx = (CheckBox) itemView.findViewById(R.id.checkbox);
        }
    }
}