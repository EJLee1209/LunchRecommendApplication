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
            //중복 마커 제거
            val hashSet: HashSet<Marker> = HashSet()
            hashSet.addAll(previous_marker)
            previous_marker.clear()
            previous_marker.addAll(hashSet)
        })
    }
    fun getCurrentAddress(latlng: LatLng): String {
        //지오코더... GPS를 주소로 변환
        val geocoder = Geocoder(this.requireContext(), Locale.getDefault())
        val addresses: List<Address>?
        addresses = try {
            geocoder.getFromLocation(
                latlng.latitude,
                latlng.longitude,
                1
            )
        } catch (ioException: IOException) {
            //네트워크 문제
            return "지오코더 서비스 사용불가"
        } catch (illegalArgumentException: IllegalArgumentException) {
            return "잘못된 GPS 좌표"
        }
        return if (addresses == null || addresses.size == 0) {
            "주소 미발견"
        } else {
            val address: Address = addresses[0]
            Log.d("testt", address.toString())
            address.getAddressLine(0).toString()
        }
    }
    fun showPlaceInformation(location: LatLng) {
        mGoogleMap?.clear() //지도 클리어
        previous_marker.clear() //지역정보 마커 클리어
        NRPlaces.Builder()
            .listener(this)
            .key("AIzaSyDXPrkprv1T8DqwwOSWdP6PMCuzJh6MKso")
            .latlng(location.latitude, location.longitude) //현재 위치
            .radius(2000) //1000 미터 내에서 검색
            .type(PlaceType.RESTAURANT) //음식점
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
        Log.d("testt","call startLocationUpdates()")
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
            Log.d("testt","callback")
            onLocationChanged(locationResult.lastLocation!!)
        }
    }
    // 시스템으로 부터 받은 위치 정보를 갱신해서 지도에 현재 위치를 표시
    private fun onLocationChanged(location: Location){
        Log.d("testt","call onLocationChanged()")
        isClicked = true
        mLastLocation = location
        currentLocation = LatLng(mLastLocation.latitude, mLastLocation.longitude)
        val markerOptions = MarkerOptions()
        markerOptions.position(currentLocation)
        markerOptions.title("나")
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
        currentLocation = LatLng(37.56, 126.97)

        val markerOptions = MarkerOptions()
        markerOptions.position(currentLocation)
        markerOptions.title("서울")
        markerOptions.snippet("한국의 수도")
        mGoogleMap?.addMarker(markerOptions)
        mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 13f))
        //startLocationUpdates()
    }
}