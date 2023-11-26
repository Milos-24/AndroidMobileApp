package app.utilities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.activityplanner.R
import app.model.ActivityItem

class ActivityAdapter(private val activityList: List<ActivityItem>, private val onItemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<ActivityAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val dateAndTimeTextView: TextView = itemView.findViewById(R.id.dateAndTimeTextView)
        var activityImageView: ImageView = itemView.findViewById(R.id.activityImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_activity, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val activityItem = activityList[position]

        // Set text and image based on the data
        holder.titleTextView.text = activityItem.title
        holder.descriptionTextView.text = activityItem.description
        holder.dateAndTimeTextView.text = "${activityItem.date} ${activityItem.time}"

        if (activityItem.type.equals("Work"))
            holder.activityImageView.setImageResource(R.drawable.work)
        else if (activityItem.type.equals("Personal"))
            holder.activityImageView.setImageResource(R.drawable.personal)
        else
            holder.activityImageView.setImageResource(R.drawable.other)

        // Set click listener for the item
        holder.itemView.setOnClickListener {
            // Notify the click event to the listener
            onItemClickListener.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return activityList.size
    }
}
