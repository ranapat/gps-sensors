package org.ranapat.sensors.gps.example.ui.map

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import org.ranapat.sensors.gps.example.R
import org.ranapat.sensors.gps.example.data.entity.Location
import org.ranapat.sensors.gps.example.tools.OnLatterAction

class MapFragment : Fragment(), OnMapReadyCallback {
    companion object {
        const val RAW_PATH_COLOR = Color.MAGENTA
        const val PROCESSED_PATH_COLOR = Color.GREEN
    }

    private var mapView: MapView? = null
    private var googleMap: GoogleMap? = null

    private var currentLocationBitmapRaw: Bitmap? = null
    private var currentLocationBitmapProcessed: Bitmap? = null
    private var wrongLocationBitmap: Bitmap? = null

    private var pendingAction: OnLatterAction? = null

    private var _currentLocationRawVisible: Boolean = true
    private var currentLocationRaw: Location? = null
    private var currentLocationMarkerRaw: Marker? = null
    private var currentLocationPolylineRaw: Polyline? = null
    private var currentLocationPopylineOptionsRaw: PolylineOptions? = null
    private var currentLocationZoomLevelSet: Boolean = false

    private var _currentLocationProcessedVisible: Boolean = true
    private var currentLocationProcessed: Location? = null
    private var currentLocationMarkerProcessed: Marker? = null
    private var currentLocationPolylineProcessed: Polyline? = null
    private var currentLocationPopylineOptionsProcessed: PolylineOptions? = null

    private val coloredPolylines: HashMap<Int, Pair<PolylineOptions, Polyline?>> = hashMapOf()

    private val wrongLocations: ArrayList<Location> = arrayListOf()
    private val wrongLocationMarkers: ArrayList<Marker> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val view = inflater.inflate(R.layout.fragment_map, container, false)

        this@MapFragment.mapView = view.findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)

        initialize()

        return view
    }

    override fun onResume() {
        mapView?.onResume()
        super.onResume()
    }

    override fun onPause() {
        mapView?.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapView?.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        mapView?.onLowMemory()
        super.onLowMemory()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this@MapFragment.googleMap = googleMap

        //googleMap?.uiSettings?.setAllGesturesEnabled(false)

        googleMap?.setOnMapLoadedCallback {
            this@MapFragment.pendingAction?.perform()
            pendingAction = null
        }
    }

    var currentLocationRawVisible: Boolean
        get() = _currentLocationRawVisible
        set(value) {
            _currentLocationRawVisible = value

            currentLocationPolylineRaw = if (!value) {
                currentLocationPolylineRaw?.remove()
                null
            } else {
                googleMap?.addPolyline(currentLocationPopylineOptionsRaw!!)
            }
        }

    fun clearCurrentLocationRaw() {
        currentLocationPolylineRaw?.remove()
        initializeCurrentLocationRawPolylineOptions()
    }

    fun currentLocationRaw(currentLocation: Location?, focus: Boolean = false) {
        this@MapFragment.currentLocationRaw = currentLocation

        currentLocationMarkerRaw?.remove()
        currentLocationMarkerRaw = null

        if (currentLocation != null) {
            val point = LatLng(currentLocation.latitude, currentLocation.longitude)

            currentLocationMarkerRaw = googleMap?.addMarker(
                MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(currentLocationBitmapRaw!!))
                    .position(point)
                    .title("current location raw")
            )

            currentLocationPopylineOptionsRaw?.add(point)
            if (_currentLocationRawVisible) {
                currentLocationPolylineRaw?.remove()
                currentLocationPolylineRaw =
                    googleMap?.addPolyline(currentLocationPopylineOptionsRaw!!)
            }

            if (focus) {
                focus(point)
            }
        }
    }

    fun clearCurrentLocationProcessed() {
        currentLocationPolylineProcessed?.remove()
        initializeCurrentLocationProcessed()
    }

    var currentLocationProcessedVisible: Boolean
        get() = _currentLocationProcessedVisible
        set(value) {
            _currentLocationProcessedVisible = value

            currentLocationPolylineProcessed = if (!value) {
                currentLocationPolylineProcessed?.remove()
                null
            } else {
                googleMap?.addPolyline(currentLocationPopylineOptionsProcessed!!)
            }
        }

    fun currentLocationProcessed(currentLocation: Location?, focus: Boolean = false) {
        this@MapFragment.currentLocationProcessed = currentLocation

        currentLocationMarkerProcessed?.remove()
        currentLocationMarkerProcessed = null

        if (currentLocation != null) {
            val point = LatLng(currentLocation.latitude, currentLocation.longitude)

            currentLocationMarkerProcessed = googleMap?.addMarker(
                MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(currentLocationBitmapProcessed!!))
                    .position(point)
                    .title("current location processed")
            )

            currentLocationPopylineOptionsProcessed?.add(point)
            if (_currentLocationProcessedVisible) {
                currentLocationPolylineProcessed?.remove()
                currentLocationPolylineProcessed =
                    googleMap?.addPolyline(currentLocationPopylineOptionsProcessed!!)
            }

            if (focus) {
                focus(point)
            }
        }
    }

    fun wrongLocation(wrongLocation: Location) {
        this@MapFragment.wrongLocations.add(wrongLocation)

        googleMap?.addMarker(
            MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(wrongLocationBitmap!!))
                .position(LatLng(wrongLocation.latitude, wrongLocation.longitude))
                .title("wrong location")
        )?.also {
            this@MapFragment.wrongLocationMarkers.add(
                it
            )
        }
    }

    fun setPath(path: List<Location>, color: Int) {
        if (path.isNotEmpty()) {
            val popylineOptions = PolylineOptions()
                .clickable(false)
                .width(5.0f)
                .color(color)
                .addAll(
                    path.map { LatLng(it.latitude, it.longitude) }
                )

            if (googleMap != null) {
                applyPath(popylineOptions)
            } else {
                pendingAction =
                    OnLatterAction {
                        applyPath(popylineOptions)
                    }
            }
        }
    }

    fun setPathVisibility(color: Int, visible: Boolean) {
        coloredPolylines[color]?.also { pair ->
            if (pair.second != null && !visible) {
                pair.second?.remove()
                coloredPolylines[color] = Pair(pair.first, null)
            } else if (pair.second == null && visible) {
                applyPath(pair.first)
            }
        }
    }

    fun swapPathVisibility(color: Int): Boolean {
        return if (coloredPolylines[color]?.second != null) {
            setPathVisibility(color, false)

            false
        } else {
            setPathVisibility(color, true)

            true
        }
    }

    private fun initialize() {
        initializeCurrentLocationRaw()
        initializeCurrentLocationProcessed()
        initializeWrongLocation()
    }

    private fun initializeCurrentLocationRaw() {
        val vectorDrawable: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.ic_map_marker_raw, view?.context?.theme)
        val width = 96
        val height = 96
        vectorDrawable?.setBounds(0, 0, width, height)
        currentLocationBitmapRaw = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(currentLocationBitmapRaw!!)
        vectorDrawable?.draw(canvas)

        initializeCurrentLocationRawPolylineOptions()
    }

    private fun initializeCurrentLocationRawPolylineOptions() {
        currentLocationPopylineOptionsRaw = PolylineOptions()
            .clickable(false)
            .width(5.0f)
            .color(RAW_PATH_COLOR)
    }

    private fun initializeCurrentLocationProcessed() {
        val vectorDrawable: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.ic_map_marker_processed, view?.context?.theme)
        val width = 96
        val height = 96
        vectorDrawable?.setBounds(0, 0, width, height)
        currentLocationBitmapProcessed = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(currentLocationBitmapProcessed!!)
        vectorDrawable?.draw(canvas)

        initializeCurrentLocationProcessedPolylineOptions()
    }

    private fun initializeCurrentLocationProcessedPolylineOptions() {
        currentLocationPopylineOptionsProcessed = PolylineOptions()
            .clickable(false)
            .width(5.0f)
            .color(PROCESSED_PATH_COLOR)
    }

    private fun initializeWrongLocation() {
        val vectorDrawable: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.ic_map_marker_off, view?.context?.theme)
        val width = 96
        val height = 96
        vectorDrawable?.setBounds(0, 0, width, height)
        wrongLocationBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(wrongLocationBitmap!!)
        vectorDrawable?.draw(canvas)
    }

    private fun focus(point: LatLng) {
        googleMap?.animateCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder()
                    .target(point)
                    .zoom(
                        if (!currentLocationZoomLevelSet) {
                            21.0f
                        } else {
                            googleMap?.cameraPosition?.zoom ?: 21.0f
                        }
                    )
                    .build()
            )
        )
        if (!currentLocationZoomLevelSet) {
            currentLocationZoomLevelSet = true
        }
    }

    private fun applyPath(popylineOptions: PolylineOptions) {
        coloredPolylines[popylineOptions.color] = Pair(
            popylineOptions, googleMap?.addPolyline(popylineOptions)
        )

        val builder: LatLngBounds.Builder = LatLngBounds.Builder()

        popylineOptions.points.forEach { location ->
            builder.include(LatLng(location.latitude, location.longitude))
        }

        val bounds: LatLngBounds = builder.build()

        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 36)
        googleMap?.animateCamera(cameraUpdate)
    }
}
