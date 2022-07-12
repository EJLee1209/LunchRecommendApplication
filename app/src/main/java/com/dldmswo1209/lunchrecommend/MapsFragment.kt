package com.dldmswo1209.lunchrecommend

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationRequest
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.dldmswo1209.lunchrecommend.databinding.FragmentMapsBinding
import com.google.android.gms.common.api.GoogleApi
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CircleOptions

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment(), OnMapReadyCallback {
    var mBinding :FragmentMapsBinding? = null
    val binding get() = mBinding!!
    private var mapView: MapView? = null
    private var mGoogleMap : GoogleMap? = null
    private var mFusedLocationProviderClient : FusedLocationProviderClient? = null
    private var isClicked = false
    lateinit var mLastLocation: Location
    internal lateinit var mLocationRequest: com.google.android.gms.location.LocationRequest


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
        // 버튼 이벤트를 통해 현재 위치 탐색 시작
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
        // 위치 업데이트 전에 이 전의 위치 요청을 삭제, 그래야 다시 새로운 위치 요청이 가능
        removeLocationListener()
        // FusedLocationProviderClient의 인스턴스 생성.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.requireContext())
        if(ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
                return
        // 기기의 위치에 관한 정기 업데이트를 요청하는 메서드 실행
        // 지정한 루퍼 스레드에서 콜백으로 위치 업데이트를 요청
        mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
    }
    // 시스템으로 부터 위치 정보를 콜백으로 받음
    private val mLocationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            // 시스템에서 받은 location 정보를 onLocationChanged() 에 전달
            onLocationChanged(locationResult.lastLocation!!)
        }
    }
    // 시스템으로 부터 받은 위치 정보를 갱신해서 지도에 현재 위치를 표시
    private fun onLocationChanged(location: Location){
        isClicked = true
        mLastLocation = location
        val currentLocation = LatLng(mLastLocation.latitude, mLastLocation.longitude)
        val markerOptions = MarkerOptions()
        markerOptions.position(currentLocation)
        markerOptions.title("나")
        mGoogleMap?.addMarker(markerOptions)
        mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16F))
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
        // 현재 위치 요청을 삭제
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

    // 인터페이스 onMapReadyCallback 구현
    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        startLocationUpdates()
    }
}