package app.activityplanner

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import app.activityplanner.databinding.FragmentSecondBinding
import app.db.ActivityDataManager
import app.db.MyDatabaseHelper
import app.model.ActivityItem
import app.utilities.ActivityAdapter

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class CalendarScreen : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private lateinit var activityDataManager: ActivityDataManager

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)


        val dbHelper = context?.let { MyDatabaseHelper(it) }
        val db = dbHelper?.writableDatabase
        activityDataManager = ActivityDataManager(requireContext())


        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val activities = activityDataManager.readActivities()
        activities.sortedWith(compareBy({it.date},{it.time}))


        // Set an OnDateChangedListener to handle date changes
        binding.datePicker.init(
            /* year = */ 2023,
            /* monthOfYear = */ 11,  // Months are zero-based, so 0 is January
            /* dayOfMonth = */ 1
        ) { view, year, monthOfYear, dayOfMonth ->
            var selectedActivities: List<ActivityItem> = emptyList()
            // Handle the date change
            val selectedDate = "$dayOfMonth/${monthOfYear + 1}/$year"
            //Toast.makeText(this@DatePickerActivity, "Selected date: $selectedDate", Toast.LENGTH_SHORT).show()

            for (activity in activities) {
                val parts = activity.date.split(".")

                Log.v("LOG_TAG", "activiti datum sirov " +activity.date + "  activity datum " + parts[0] + " "+ parts[1] +" " + parts[2] +"  datum sa kalendara " + dayOfMonth+""+ monthOfYear+"" + year)


                if (parts[0].toInt()==dayOfMonth && parts[1].toInt()==(monthOfYear + 1) && parts[2].toInt()==year)
                {
                    Log.v("LOG_TAG","uso u ovo")
                        selectedActivities += activity;
                }
            }

            val adapter = ActivityAdapter(selectedActivities, object : ActivityAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    // Handle item click here using the position
                    val clickedActivity = selectedActivities[position]
                    // Do something with the clicked item
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Select an Option")
                    val options = arrayOf(clickedActivity.title, "View Location", "Delete")

                    builder.setItems(options) { dialog, which ->
                        when (which) {
                            0 -> {
                                Log.v("LOG_TAG","success")
                            }
                            1 -> dialog.dismiss()
                            2 -> dialog.dismiss()
                        }
                    }
                    builder.show()
                }
            })
            recyclerView.adapter = adapter
        }






        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}