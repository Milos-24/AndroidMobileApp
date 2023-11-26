package app.activityplanner

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import app.activityplanner.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.Marker
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode

class MapsActivity : AppCompatActivity(), OnMapReadyCallback,GoogleMap.OnMapClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var selectedLocationMarker: Marker?=null

    companion object {
        private const val AUTOCOMPLETE_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)




    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled=true

        val bl =  LatLng(44.7786927, 17.1361274)

        mMap.addMarker(MarkerOptions().position(bl).title("Marker in Banja Luka"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(bl))

        mMap.setOnMapClickListener(this)

    }


    override fun onMapClick(latLng: LatLng) {
        updateMap(latLng)
    }

    private fun updateMap(latLng: LatLng) {
        selectedLocationMarker?.remove()
        selectedLocationMarker = mMap.addMarker(MarkerOptions().position(latLng).title("Selected Location"))!!

        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))

        Toast.makeText(this, "Selected Location: ${latLng.latitude}, ${latLng.longitude}", Toast.LENGTH_SHORT).show()


        val resultIntent = Intent()
        resultIntent.putExtra("selectedLatLng", latLng)
        setResult(Activity.RESULT_OK, resultIntent)

        // Finish the current activity
        finish()
    }

}