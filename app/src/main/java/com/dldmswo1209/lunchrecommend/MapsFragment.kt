package com.dldmswo1209.lunchrecommend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dldmswo1209.lunchrecommend.databinding.FragmentMapsBinding
import com.google.android.gms.common.api.GoogleApi
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment(), OnMapReadyCallback {
    private var mapView: MapView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_maps, container, false)
        mapView = layout.findViewById(R.id.map) as MapView
        mapView?.getMapAsync(this)
        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (mapView != null)
            mapView?.onCreate(savedInstanceState)

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
        var SEOUL = LatLng(37.56, 126.97)
        val markerOptions = MarkerOptions()
        markerOptions.position(SEOUL)
        markerOptions.title("서울")
        markerOptions.snippet("수도")
        googleMap.addMarker(markerOptions)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 16F))

    }
}