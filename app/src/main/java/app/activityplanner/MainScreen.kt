package app.activityplanner

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import app.activityplanner.databinding.FragmentFirstBinding
import app.db.ActivityDataManager
import app.db.MyDatabaseHelper
import app.db.SettingsDataManager
import app.utilities.ActivityAdapter
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MainScreen : Fragment(){

    private var _binding: FragmentFirstBinding? = null
    private lateinit var settingsDataManager: SettingsDataManager
    private lateinit var activityDataManager: ActivityDataManager
     var activityNameList: Array<String> = emptyArray()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


  @RequiresApi(Build.VERSION_CODES.O)
  override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View? {
      _binding = FragmentFirstBinding.inflate(inflater, container, false)

      val dbHelper = context?.let { MyDatabaseHelper(it) }
      val db = dbHelper?.writableDatabase

      //flush db
      //dbHelper?.deleteAllData()


      activityDataManager = ActivityDataManager(requireContext())


      val recyclerView = binding.recyclerView
      recyclerView.layoutManager = LinearLayoutManager(requireContext())

      val activities = activityDataManager.readActivities()


      for(activity in activities) {
          activityNameList+=activity.title
      }

     val searchView = binding.searchView


      searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
          override fun onQueryTextSubmit(query: String?): Boolean {
              if (query != null) {
                  performSearch(query)
              }
              return true
          }

          override fun onQueryTextChange(newText: String?): Boolean {
              if (newText != null) {
                  filterList(newText)
              }
              return true
          }
      })




      //activities.sortedWith(compareBy({it.date},{it.time}))

      val sortedList = activities.sortedWith(compareBy {
          LocalDateTime.parse(
              "${it.date} ${it.time}",
              DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
          )
      })

      val adapter = ActivityAdapter(sortedList, object : ActivityAdapter.OnItemClickListener {
          override fun onItemClick(position: Int) {
              // Handle item click here using the position
              val clickedActivity = sortedList[position]
              // Do something with the clicked item
              val builder = AlertDialog.Builder(context)
              builder.setTitle("Select an Option")
              val options = arrayOf("View Photos", "View Location", "Delete")

              builder.setItems(options) { dialog, which ->
                  when (which) {
                      0 -> {
                          val builder = AlertDialog.Builder(context)
                          val inflater = LayoutInflater.from(context)
                          val dialogView = inflater.inflate(R.layout.display_photos, null)
                          val imageView1 = dialogView.findViewById<ImageView>(R.id.imageView1)
                          val imageView2 = dialogView.findViewById<ImageView>(R.id.imageView2)
                          val imageView3 = dialogView.findViewById<ImageView>(R.id.imageView3)

                          Log.v("LOG_TAG", clickedActivity.url1 + "    "+ clickedActivity.url2+"    "+ clickedActivity.url3)
                          // Customize the view as needed (e.g., set images on ImageViews)

                          context?.let {
                              Glide.with(it)
                                  .load(clickedActivity.url1)
                                  .into(imageView1)
                          }

                          context?.let {
                              Glide.with(it)
                                  .load(clickedActivity.url2)
                                  .into(imageView2)
                          }

                          context?.let {
                              Glide.with(it)
                                  .load(clickedActivity.url3)
                                  .into(imageView3)
                          }


                          builder.setView(dialogView)
                              .setTitle("Image View")
                              .setPositiveButton("Close") { dialog, _ ->
                                  dialog.dismiss()
                              }

                          val alertDialog = builder.create()
                          alertDialog.show()                      }
                      1 -> {
                          val builder = AlertDialog.Builder(context)
                          val inflater = LayoutInflater.from(context)
                          val dialogView = inflater.inflate(R.layout.display_map, null)

                          val mapView = dialogView.findViewById<MapView>(R.id.mapView)
                          mapView.onCreate(null)
                          mapView.onResume()

                          val pattern = Regex("-?\\d+\\.\\d+")
                          val matches = pattern.findAll(clickedActivity.lanLeng)
                          val numericValues = matches.map { it.value.toDouble()}

                          val location = LatLng(numericValues.elementAt(0), numericValues.elementAt(1)) // Replace with your desired coordinates
                          mapView.getMapAsync { googleMap ->
                              googleMap.addMarker(MarkerOptions().position(location).title("Marker Title"))
                              googleMap.moveCamera(CameraUpdateFactory.newLatLng(location))
                          }
                          builder.setView(dialogView)
                              .setTitle("Location View")
                              .setPositiveButton("Close") { dialog, _ ->
                                  dialog.dismiss()
                              }

                          val alertDialog = builder.create()

                          alertDialog.show()
                      }
                      2 -> {
                          activityDataManager.deleteActivity(clickedActivity.title)
                            dialog.dismiss()
                      }
                  }
              }
              builder.show()
          }
      })
      recyclerView.adapter = adapter


      return binding.root
  }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchView = binding.searchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    performSearch(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    filterList(newText)
                }
                return true
            }
        })
        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun performSearch(query: String) {
        // Implement search functionality here
        // For example, initiate a search operation based on the query
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        var activities = activityDataManager.readActivities()
        activities.sortedWith(compareBy({it.date},{it.time}))

        if(this.activityNameList.contains(query))
        {

            for(activity in activities)
            {
                if(activity.title.equals(query))
                    activities= listOf(activity)
            }

            val adapter = ActivityAdapter(activities, object : ActivityAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    // Handle item click here using the position
                    val clickedActivity = activities[position]
                    // Do something with the clicked item
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Select an Option")
                    val options = arrayOf("View Photos", "View Location", "Delete")

                    builder.setItems(options) { dialog, which ->
                        when (which) {
                            0 -> {
                                Log.v("LOG_TAG","success")
                            }
                            1 -> {
                                val builder = AlertDialog.Builder(context)
                                val inflater = LayoutInflater.from(context)
                                val dialogView = inflater.inflate(R.layout.display_map, null)

                                val mapView = dialogView.findViewById<MapView>(R.id.mapView)
                                mapView.onCreate(null)
                                mapView.onResume()

                                val pattern = Regex("-?\\d+\\.\\d+")
                                val matches = pattern.findAll(clickedActivity.lanLeng)
                                val numericValues = matches.map { it.value.toDouble()}

                                val location = LatLng(numericValues.elementAt(0), numericValues.elementAt(1)) // Replace with your desired coordinates
                                mapView.getMapAsync { googleMap ->
                                    googleMap.addMarker(MarkerOptions().position(location).title("Marker Title"))
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(location))
                                }
                                builder.setView(dialogView)
                                    .setTitle("Location View")
                                    .setPositiveButton("Close") { dialog, _ ->
                                        dialog.dismiss()
                                    }

                                val alertDialog = builder.create()

                                alertDialog.show()
                            }
                            2 -> {
                                activityDataManager.deleteActivity(clickedActivity.title)
                                dialog.dismiss()
                            }
                        }
                    }
                    builder.show()
                }
            })
            recyclerView.adapter = adapter
        }
        else{
            activities= emptyList()
            val adapter = ActivityAdapter(activities, object : ActivityAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    // Handle item click here using the position
                    val clickedActivity = activities[position]
                    // Do something with the clicked item
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Select an Option")
                    val options = arrayOf("View Photos", "View Location", "Delete")

                    builder.setItems(options) { dialog, which ->
                        when (which) {
                            0 -> {
                                Log.v("LOG_TAG","success")
                            }
                            1 -> {
                                val builder = AlertDialog.Builder(context)
                                val inflater = LayoutInflater.from(context)
                                val dialogView = inflater.inflate(R.layout.display_map, null)

                                val mapView = dialogView.findViewById<MapView>(R.id.mapView)
                                mapView.onCreate(null)
                                mapView.onResume()

                                val pattern = Regex("-?\\d+\\.\\d+")
                                val matches = pattern.findAll(clickedActivity.lanLeng)
                                val numericValues = matches.map { it.value.toDouble()}

                                val location = LatLng(numericValues.elementAt(0), numericValues.elementAt(1)) // Replace with your desired coordinates
                                mapView.getMapAsync { googleMap ->
                                    googleMap.addMarker(MarkerOptions().position(location).title("Marker Title"))
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(location))
                                }
                                builder.setView(dialogView)
                                    .setTitle("Location View")
                                    .setPositiveButton("Close") { dialog, _ ->
                                        dialog.dismiss()
                                    }

                                val alertDialog = builder.create()

                                alertDialog.show()
                            }
                            2 -> {
                                activityDataManager.deleteActivity(clickedActivity.title)
                                dialog.dismiss()
                            }
                        }
                    }
                    builder.show()
                }
            })
            recyclerView.adapter = adapter
        }
    }

    private fun filterList(newText: String) {
        // Implement real-time filtering logic here
    }
}
