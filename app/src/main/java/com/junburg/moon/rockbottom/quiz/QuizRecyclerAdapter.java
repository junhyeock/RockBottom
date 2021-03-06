package com.junburg.moon.rockbottom.quiz;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.junburg.moon.rockbottom.R;
import com.junburg.moon.rockbottom.util.ItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Junburg on 2018. 5. 24..
 */

public class QuizRecyclerAdapter extends RecyclerView.Adapter<QuizRecyclerViewHolder>{

    private List<String> exampleList;
    int row_index = -1;

    public QuizRecyclerAdapter(ArrayList<String> exampleList) {
        this.exampleList = exampleList;
    }

    @NonNull
    @Override
    public QuizRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_recycler_list_item, null);
        QuizRecyclerViewHolder holder = new QuizRecyclerViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull QuizRecyclerViewHolder holder, int position) {
        String append = null;
        switch (position) {
            case 0: 
                append = "a. ";
                break;
            case 1: 
                append = "b. ";
                break;
            case 2:
                append = "c. ";
                break;
            case 3:
                append = "d. ";
                break;
        }
        holder.quizExampleTxt.setText(append + exampleList.get(position));

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                row_index = position;
                notifyDataSetChanged();
            }
        });

        if(row_index == position) {
            holder.itemView.setBackgroundColor(Color.parseColor("#F8F8FA"));
            holder.quizExampleTxt.setTextColor(Color.parseColor("#c5c5c7"));
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            holder.quizExampleTxt.setTextColor(Color.parseColor("#000000"));
        }
    }

    @Override
    public int getItemCount() {
        return (exampleList != null) ? exampleList.size() : 0;
    }
}
