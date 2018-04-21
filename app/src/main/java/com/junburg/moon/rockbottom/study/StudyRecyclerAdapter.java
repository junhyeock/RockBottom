package com.junburg.moon.rockbottom.study;

import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.junburg.moon.rockbottom.R;
import com.junburg.moon.rockbottom.model.Chapter;
import com.junburg.moon.rockbottom.model.Subject;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Junburg on 2018. 3. 11..
 */

public class StudyRecyclerAdapter extends RecyclerView.Adapter<StudyRecyclerViewHolder>{
    private List<Subject> subjectList;
    private Context context;
    private android.support.v4.app.FragmentManager fm;

    public StudyRecyclerAdapter(
            List<Subject> subjectList
            , Context context
            , android.support.v4.app.FragmentManager fm) {
        this.subjectList = subjectList;
        this.context = context;
        this.fm = fm;
    }

    @Override
    public StudyRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.study_recycler_list_item, null);
        StudyRecyclerViewHolder holder = new StudyRecyclerViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(StudyRecyclerViewHolder holder, int position) {
        holder.studySubjectNameTxt.setText(subjectList.get(position).getName());
        holder.studySubjectExplainTxt.setText(subjectList.get(position).getExplain());
        final List<Chapter> chapterList = subjectList.get(position).getChapterList();
        holder.chapterRecyclerAdapter.setData(chapterList);
        holder.chapterRecyclerAdapter.setOnItemClickListener(new ChapterRecyclerViewHolder.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                showDialog(position, chapterList.get(position).getChapterId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return (subjectList != null) ? subjectList.size() : 0;
    }

    private void showDialog(int position, String chapterId) {
        ChapterDialogFragment chapterDialogFragment = new ChapterDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        bundle.putString("chapterId", chapterId);
        chapterDialogFragment.setArguments(bundle);

        chapterDialogFragment.setCancelable(false);
        chapterDialogFragment.show(fm, "ChapterDialogFragment");
    }
}
