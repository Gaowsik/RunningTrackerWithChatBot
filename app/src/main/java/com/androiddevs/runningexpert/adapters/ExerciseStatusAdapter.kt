package com.androiddevs.runningexpert.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.runningexpert.R
import com.androiddevs.runningexpert.db.ExerciseModel
import kotlinx.android.synthetic.main.item_exercise_layout.view.*

class ExerciseStatusAdapter( val items:ArrayList<ExerciseModel>) : RecyclerView.Adapter<ExerciseStatusAdapter.ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseStatusAdapter.ViewHolder {

        return ViewHolder(

            LayoutInflater.from(parent.context).inflate(R.layout.item_exercise_layout,parent,false)


        )
    }


    override fun onBindViewHolder(holder: ExerciseStatusAdapter.ViewHolder, position: Int) {
        val model:ExerciseModel=items[position]
        holder.tvItem.text=model.getId().toString()

        if(model.getIsSelected())
        {
           holder.tvItem.setBackgroundResource(R.drawable.item_circular_thin_color_accent_border)
            holder.tvItem.setTextColor(Color.parseColor("#212121"))
        }

        else if(model.getIsCompleted())
        {
         holder.tvItem.setBackgroundResource(R.drawable.item_circular_color_accent_background)
            holder.tvItem.setTextColor(Color.parseColor("#FFFFFF"))
        }

        else
        {
          holder.tvItem.setBackgroundResource(R.drawable.item_cicular_color_grey_background)
            holder.tvItem.setTextColor(Color.parseColor("#212121"))

        }
    }

    override fun getItemCount(): Int {
       return items.size
    }


    class ViewHolder(view: View):RecyclerView.ViewHolder(view)
    {


        val tvItem=view.tvItem
    }

}