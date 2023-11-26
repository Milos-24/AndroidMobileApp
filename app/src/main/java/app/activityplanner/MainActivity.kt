package app.activityplanner

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import app.activityplanner.databinding.ActivityMainBinding
import app.db.ActivityDataManager
import app.db.SettingsDataManager
import app.model.ActivityItem
import app.model.Settings
import app.utilities.ActivityItemFilter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.materialswitch.MaterialSwitch
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() , OnMapReadyCallback {

    private lateinit var selectedCategory: String
    private lateinit var timeSelected: String
    internal lateinit var imageView: ImageView
    internal lateinit var imageView1: ImageView
    internal lateinit var imageView2: ImageView
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var defaultLocation =  LatLng(44.7786927, 17.1361274) //banja luka
    private lateinit var titleEditText:EditText
    private lateinit var descriptionEditText:EditText
    private lateinit var datePicker: DatePicker
    private lateinit var timePicker: TimePicker
    private lateinit var activityDataManager: ActivityDataManager
    private var currentPhotoPath: String? = null
    private var currentPhotoPath1: String? = null
    private var currentPhotoPath2: String? = null
    private lateinit var settingsDataManager: SettingsDataManager
    private var settings: Settings? = null


    companion object {
        const val MAP_ACTIVITY_REQUEST_CODE = 1
        const val REQUEST_IMAGE_CAPTURE = 2
        const val REQUEST_IMAGE_PICK = 3
        const val MY_CAMERA_PERMISSION_CODE = 1001 // You can use any unique integer value

    }
    fun Calendar.getFormattedDate(): String {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return dateFormat.format(time)
    }


    fun DatePicker.getDate(): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        return calendar.getFormattedDate();
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setNotification(readActivities: List<ActivityItem>, settings: Settings?)
    {
        var notificationText:String="";
        val activityFilter:ActivityItemFilter=ActivityItemFilter
        var activities: List<ActivityItem>? = null

        if(settings==null)
        {
            notificationText="Notifications are turned off!"
        }
        else {
            if (settings.isNotifications) {
                activities = if(settings.notificationTime.equals("1 Hour")) {
                    activityFilter.filterByOneHour(readActivities)
                } else if(settings.notificationTime.equals("1 Day")) {
                    activityFilter.filterByOneDay(readActivities)
                } else {
                    activityFilter.filterBySevenDays(readActivities)
                }
            }
            else{
                notificationText="Notifications are turned off!"
            }

            if(settings.languages.equals("English"))
            {
                val locale = Locale("en-us")
                val configuration = Configuration(resources.configuration)
                configuration.setLocale(locale)
                resources.updateConfiguration(configuration, resources.displayMetrics)
            }
            else
            {
                val locale = Locale("sr")
                val configuration = Configuration(resources.configuration)
                configuration.setLocale(locale)
                resources.updateConfiguration(configuration, resources.displayMetrics)
            }

        }

        if (activities != null) {
            for(activity in activities) {
                Log.v("LOG_TAG", "Aktivnost dobavljena"+activity.title+settings?.notificationTime)
            }
        }
        if (settings != null) {
            Log.v("LOG_TAG",settings.notificationTime)
        }
        if (activities != null)
            if(activities.isEmpty())
            {
                notificationText="No activities soon!"
            }
            else
            {
                notificationText="You have following activities:\n"
                for(activity in activities)
                {
                    notificationText+=activity.title+"\n"
                }
            }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channelId = "default_channel_id"
                    val channelName = "Default Channel"
                    val importance = NotificationManager.IMPORTANCE_DEFAULT
                    val channel = NotificationChannel(channelId, channelName, importance)
                    val notificationManager = getSystemService(NotificationManager::class.java)
                    notificationManager.createNotificationChannel(channel)
                }



        val notificationManager = getSystemService(NotificationManager::class.java)
        val channelId = "default_channel_id"

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.notification_icon) // Your notification icon
            .setContentTitle("Welcome to Activity Planner")
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationText))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notification: Notification = notificationBuilder.build()
        notificationManager.notify(1, notification) // You can use a unique ID for each notification
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




        settingsDataManager= SettingsDataManager(this)
        activityDataManager = ActivityDataManager(this)


        settings = settingsDataManager.readSettings()
        setNotification(activityDataManager.readActivities(), settings)



        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        //add activity


        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)



        binding.fab.setOnClickListener { view ->
            val dialogView = layoutInflater.inflate(R.layout.add_new_activity, null)
            val dialog = BottomSheetDialog(this)
            dialog.setContentView(dialogView)

            val saveButton = dialogView.findViewById<Button>(R.id.saveButton)
            // Handle saveButton click to save the inputs
            saveButton.setOnClickListener {
                activityDataManager.insertActivity(
                    titleEditText.text.toString(),
                    descriptionEditText.text.toString(),
                    datePicker.getDate(),
                    timeSelected,
                    defaultLocation.toString(),
                    currentPhotoPath ?: "", // Use an empty string if null
                    currentPhotoPath1 ?: "", // Use an empty string if null
                    currentPhotoPath2 ?: "", // Use an empty string if null
                    selectedCategory
                )

                Log.v("LOG_TAG", "kategorija$selectedCategory")




            dialog.dismiss() // Close the dialog when saving is done
            }

            val mapsButton = dialogView.findViewById<Button>(R.id.setLocation)

            mapsButton.setOnClickListener {
                val intent = Intent(this, MapsActivity::class.java)
                startActivityForResult(intent, Companion.MAP_ACTIVITY_REQUEST_CODE)
            }

            val uploadButton = dialogView.findViewById<Button>(R.id.uploadImageButton)

            uploadButton.setOnClickListener {
                this.onUploadButtonClick(view)
            }





            //spinner logic (work,personal,other)

            val categorySpinner = dialogView.findViewById<Spinner>(R.id.categorySpinner)

            val categories = arrayOf("Work", "Personal", "Other")

            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = adapter

            categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    // Update selectedCategory when an item is selected
                    selectedCategory = categories[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    selectedCategory="Work"
                }
            }


            dialog.show()

            imageView = dialogView.findViewById(R.id.imageView)
            imageView1 = dialogView.findViewById(R.id.imageView1)
            imageView2 = dialogView.findViewById(R.id.imageView2)
             titleEditText = dialogView.findViewById(R.id.titleEditText)
             descriptionEditText = dialogView.findViewById(R.id.descriptionEditText)
             datePicker = dialogView.findViewById(R.id.datePicker)
            timePicker = dialogView.findViewById(R.id.timePicker)

            if(isSingleDigit(timePicker.getCurrentHour()) && isSingleDigit(timePicker.getCurrentMinute()))
            {
                timeSelected="0"+timePicker.getCurrentHour().toString()+":0"+timePicker.getCurrentMinute().toString()
            }
            else if(isSingleDigit(timePicker.getCurrentMinute()))
            {
                timeSelected=timePicker.getCurrentHour().toString()+":0"+timePicker.getCurrentMinute().toString()
            }
            else if (isSingleDigit(timePicker.getCurrentHour()))
            {
                timeSelected="0"+timePicker.getCurrentHour().toString()+":"+timePicker.getCurrentMinute()
            }
            else {
                timeSelected = timePicker.getCurrentHour().toString()+":"+timePicker.getCurrentMinute().toString()
            }


            timePicker.setOnTimeChangedListener { view, hourOfDay, minute ->
                if(isSingleDigit(hourOfDay) && isSingleDigit(minute))
                {
                 timeSelected="0$hourOfDay:0$minute"
                }
                else if(isSingleDigit(minute))
                {
                    timeSelected="$hourOfDay:0$minute"
                }
                else if (isSingleDigit(hourOfDay))
                {
                    timeSelected="0$hourOfDay:$minute"
                }
                else {
                    timeSelected = "$hourOfDay:$minute"
                }
            }

        }


    }
    private fun isSingleDigit(number: Int): Boolean {
        val numberAsString = number.toString()
        return numberAsString.length == 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.v("LOG_TAG", "uso uopste u metodu");

        if (requestCode == Companion.MAP_ACTIVITY_REQUEST_CODE) {
            Log.v("LOG_TAG", "uso u ovo za mapu");

            if (resultCode == Activity.RESULT_OK) {
                val selectedLatLng = data?.getParcelableExtra<LatLng>("selectedLatLng")
                if (selectedLatLng != null) {

                    this.defaultLocation=selectedLatLng;
                    Log.i(TAG, "aloooo: $selectedLatLng");
                }
                return
            }
        }

        if (resultCode == Activity.RESULT_OK) {

            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    setPic()
                    galleryAddPic()

                    Log.v("LOG_TAG", "uso u slikanje");

                }

                REQUEST_IMAGE_PICK -> {
                    val selectedImageUri = data?.data

                    if(imageView.drawable==null)
                    {
                        imageView.setImageURI(selectedImageUri)
                        this.currentPhotoPath= selectedImageUri.toString()
                        Glide.with(this)
                            .load(selectedImageUri)
                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                            .into(imageView)
                    }
                    else if(imageView1.drawable==null)
                    {
                        imageView1.setImageURI(selectedImageUri)
                        this.currentPhotoPath1= selectedImageUri.toString()
                        Glide.with(this)
                            .load(selectedImageUri)
                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                            .into(imageView1)
                    }
                    else
                    {
                        imageView2.setImageURI(selectedImageUri)
                        this.currentPhotoPath2= selectedImageUri.toString()
                        Glide.with(this)
                            .load(selectedImageUri)
                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                            .into(imageView2)
                    }


                }
            }
        }

    }

    fun onUploadButtonClick(view: View) {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select an Option")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        // Camera permission is not granted, request it
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), MY_CAMERA_PERMISSION_CODE)
                    } else {
                        // Camera permission is already granted, proceed with camera action
                        dispatchTakePictureIntent()
                    }
                }
                1 -> dispatchPickImageIntent()
                2 -> dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun dispatchTakePictureIntent() {
        // Intent to open the camera app
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create a file to store the image
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                // Handle errors while creating the file
                ex.printStackTrace()
                null
            }

            // Continue only if the file was successfully created
            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this,
                    "com.your.package.name.fileprovider",
                    it
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, Companion.REQUEST_IMAGE_CAPTURE)
            }
        }




       // if (takePictureIntent.resolveActivity(packageManager) != null) {
       //     startActivityForResult(takePictureIntent, Companion.REQUEST_IMAGE_CAPTURE)
       // }
    }

    private fun dispatchPickImageIntent() {
        // Intent to open the gallery Single image picker
        val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickPhotoIntent.type = "image/*"
        startActivityForResult(pickPhotoIntent, Companion.REQUEST_IMAGE_PICK)

    }

    private fun galleryAddPic() {
        val file = getCurrentPhoto()?.let { File(it) }

        if (file != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }

            val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val resolver = contentResolver
            val uri = resolver.insert(contentUri, values)

            uri?.let {
                resolver.openOutputStream(it)?.use { outputStream ->
                    FileInputStream(file).use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            }
        } else if (file != null) {
            // For versions below Android 10, continue using the original method
            val contentUri = FileProvider.getUriForFile(
                this,
                "com.your.package.name.fileprovider",
                file
            )

            // Notify the MediaScanner about the new file
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, contentUri)
            sendBroadcast(mediaScanIntent)
        } else {
            // Handle the case where file is null
        }
    }

    fun getCurrentPhoto(): String? {
       if(currentPhotoPath2!=null)
       {
           return currentPhotoPath2
       }
        else if(currentPhotoPath1!=null)
       {
            return currentPhotoPath1

       }
        else
            return currentPhotoPath
    }



    private fun setPic() {
        // Get the dimensions of the View

        val targetW: Int = imageView.width
        val targetH: Int = imageView.height

        // First, obtain the dimensions of the bitmap without loading the full image into memory
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(getCurrentPhoto(), this)
        }

        // Calculate the scaleFactor based on the dimensions of the View and the bitmap
        val scaleFactor: Int = calculateInSampleSize(options, targetW, targetH)

        // Decode the image file into a Bitmap sized to fill the View
        options.inJustDecodeBounds = false
        options.inSampleSize = scaleFactor
        options.inPurgeable = true

        // Decode the image file into a Bitmap and set it to the ImageView
        BitmapFactory.decodeFile(getCurrentPhoto(), options)?.also { bitmap ->
            if(imageView.drawable==null)
            imageView.setImageBitmap(bitmap)
            else if(imageView1.drawable==null)
                imageView1.setImageBitmap(bitmap)
            else
                imageView2.setImageBitmap(bitmap)
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of the image
        val height: Int = options.outHeight
        val width: Int = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }



    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        val image = File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )

        // Save a file path for use with ACTION_VIEW intents
        if(currentPhotoPath==null)
            currentPhotoPath= image.absolutePath
        else if(currentPhotoPath1==null)
            currentPhotoPath1=image.absolutePath
        else
            currentPhotoPath2=image.absolutePath
        return image
    }





    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {

                var languageSelected:String
                var notifications:Boolean
                var notificationTime:String

                val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val popupView = inflater.inflate(R.layout.settings, null)

// Create the popup window
                val width = LinearLayout.LayoutParams.WRAP_CONTENT
                val height = LinearLayout.LayoutParams.WRAP_CONTENT
                val focusable = true // Lets taps outside the popup also dismiss it
                val popupWindow = PopupWindow(popupView, width, height, focusable)

                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)

                val serbianLanguage = popupView.findViewById<RadioButton>(R.id.radioSerbian)
                val mySwitch = popupView.findViewById<MaterialSwitch>(R.id.switchNotifications)
                val notification1Hour = popupView.findViewById<RadioButton>(R.id.notification1Hour)
                val notification1Day = popupView.findViewById<RadioButton>(R.id.notification1Day)
                val notification7Days = popupView.findViewById<RadioButton>(R.id.notification7Days)


                popupView.findViewById<Button>(R.id.saveButtonSettings).setOnClickListener {
                    settingsDataManager = SettingsDataManager(this)

                    languageSelected = if(serbianLanguage.isChecked)
                        "Serbian"
                    else
                        "English"

                    notifications = mySwitch.isChecked


                    notificationTime = if(notification1Hour.isChecked)
                        "1 Hour"
                    else if(notification1Day.isChecked)
                        "1 Day"
                    else
                        "7 Days"


                    settingsDataManager.insertSettings(languageSelected,notificationTime,notifications)

                    popupWindow.dismiss()
                }


                popupView.setOnTouchListener { v, event ->
                    popupWindow.dismiss()
                    true
                }


                return true  // Return true to indicate that the event was handled.
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

      override fun onSupportNavigateUp(): Boolean {
          val navController = findNavController(R.id.nav_host_fragment_content_main)
          return navController.navigateUp(appBarConfiguration)
                  || super.onSupportNavigateUp()
      }

    override fun onMapReady(p0: GoogleMap) {
        TODO("Not yet implemented")
    }


}