package com.dldmswo1209.lunchrecommend

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.dldmswo1209.lunchrecommend.databinding.FragmentMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import noman.googleplaces.*
import java.io.IOException
import java.util.*


class MapsFragment : Fragment(), OnMapReadyCallback,PlacesListener {
    var mBinding :FragmentMapsBinding? = null
    val binding get() = mBinding!!
    private var mapView: MapView? = null
    private var mGoogleMap : GoogleMap? = null
    private var mFusedLocationProviderClient : FusedLocationProviderClient? = null
    private var isClicked = false
    private val previous_marker = mutableListOf<Marker>()
    lateinit var mLastLocation: Location
    lateinit var mainActivity: MainActivity
    lateinit var currentLocation: LatLng
    internal lateinit var mLocationRequest: com.google.android.gms.location.LocationRequest

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity

    }
    override fun onPlacesFailure(e: PlacesException?) {

    }

    override fun onPlacesStart() {

    }

    override fun onPlacesSuccess(places: MutableList<Place>?) {
        mainActivity.runOnUiThread(Runnable {
            places!!.forEach {
                val latLng = LatLng(
                    it.latitude, it.longitude
                )
                val markerSnippet: String = getCurrentAddress(latLng)
                val markerOptions = MarkerOptions()
                markerOptions.position(latLng)
                markerOptions.title(it.name)
                markerOptions.snippet(markerSnippet)
                val item: Marker? = mGoogleMap?.addMarker(markerOptions)
                if (item != null) {
                    previous_marker.add(item)
                }
            }
            //?????? ?????? ??????
            val hashSet: HashSet<Marker> = HashSet()
            hashSet.addAll(previous_marker)
            previous_marker.clear()
            previous_marker.addAll(hashSet)
        })
    }
    fun getCurrentAddress(latlng: LatLng): String {
        //????????????... GPS??? ????????? ??????
        val geocoder = Geocoder(this.requireContext(), Locale.getDefault())
        val addresses: List<Address>?
        addresses = try {
            geocoder.getFromLocation(
                latlng.latitude,
                latlng.longitude,
                1
            )
        } catch (ioException: IOException) {
            //???????????? ??????
            return "???????????? ????????? ????????????"
        } catch (illegalArgumentException: IllegalArgumentException) {
            return "????????? GPS ??????"
        }
        return if (addresses == null || addresses.size == 0) {
            "?????? ?????????"
        } else {
            val address: Address = addresses[0]
            Log.d("testt", address.toString())
            address.getAddressLine(0).toString()
        }
    }
    fun showPlaceInformation(location: LatLng) {
        mGoogleMap?.clear() //?????? ?????????
        previous_marker.clear() //???????????? ?????? ?????????
        NRPlaces.Builder()
            .listener(this)
            .key("AIzaSyDXPrkprv1T8DqwwOSWdP6PMCuzJh6MKso")
            .latlng(location.latitude, location.longitude) //?????? ??????
            .radius(2000) //1000 ?????? ????????? ??????
            .type(PlaceType.RESTAURANT) //?????????
            .build()
            .execute()
    }
    override fun onPlacesFinished() {

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentMapsBinding.inflate(inflater, container, false)
        mapView = binding.map
        mapView?.getMapAsync(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (mapView != null)
            mapView?.onCreate(savedInstanceState)

        mLocationRequest = com.google.android.gms.location.LocationRequest.create().apply {
            priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        // ?????? ???????????? ?????? ?????? ?????? ?????? ??????
        onClickEvent()
    }

    private fun onClickEvent(){
        binding.findMyLocateButton.setOnClickListener {
//            if(isClicked) {
//                onLocationChanged(mLastLocation)
//                return@setOnClickListener
//            }
            if(checkPermissionForLocation(this.requireContext()))
                startLocationUpdates()

            Log.d("testt","button clicked")
        }
    }

    private fun startLocationUpdates(){
        // ?????? ???????????? ?????? ??? ?????? ?????? ????????? ??????, ????????? ?????? ????????? ?????? ????????? ??????
        removeLocationListener()
        Log.d("testt","call startLocationUpdates()")
        // FusedLocationProviderClient??? ???????????? ??????.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.requireContext())
        if(ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
                return
        // ????????? ????????? ?????? ?????? ??????????????? ???????????? ????????? ??????
        // ????????? ?????? ??????????????? ???????????? ?????? ??????????????? ??????
        mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
    }
    // ??????????????? ?????? ?????? ????????? ???????????? ??????
    private val mLocationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            // ??????????????? ?????? location ????????? onLocationChanged() ??? ??????
            Log.d("testt","callback")
            onLocationChanged(locationResult.lastLocation!!)
        }
    }
    // ??????????????? ?????? ?????? ?????? ????????? ???????????? ????????? ?????? ????????? ??????
    private fun onLocationChanged(location: Location){
        Log.d("testt","call onLocationChanged()")
        isClicked = true
        mLastLocation = location
        currentLocation = LatLng(mLastLocation.latitude, mLastLocation.longitude)
        val markerOptions = MarkerOptions()
        markerOptions.position(currentLocation)
        markerOptions.title("???")
        mGoogleMap?.addMarker(markerOptions)
        mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 13F))
        showPlaceInformation(currentLocation)
    }

    private fun checkPermissionForLocation(context : Context) : Boolean{
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) true
            else false
        }else true
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }
    @Override
    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    @Override
    override fun onSaveInstanceState(outState : Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    @Override
    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    @Override
    override fun onPause() {
        super.onPause()
        mapView?.onPause()
        removeLocationListener()
    }
    private fun removeLocationListener(){
        // ?????? ?????? ????????? ??????
        mFusedLocationProviderClient?.removeLocationUpdates(mLocationCallback)
    }

    @Override
    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    @Override
    override fun onDestroy() {
        super.onDestroy()
        mapView?.onLowMemory()
    }

    // ??????????????? onMapReadyCallback ??????
    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        currentLocation = LatLng(37.56, 126.97)

        val markerOptions = MarkerOptions()
        markerOptions.position(currentLocation)
        markerOptions.title("??????")
        markerOptions.snippet("????????? ??????")
        mGoogleMap?.addMarker(markerOptions)
        mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 13f))
        //startLocationUpdates()
    }
}