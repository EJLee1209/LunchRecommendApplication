package com.dldmswo1209.lunchrecommend

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TableLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import com.dldmswo1209.lunchrecommend.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {
    var mBinding : ActivityMainBinding? = null
    val binding get() = mBinding!!
    private val GPS_ENABLE_REQUEST_CODE = 2001
    private val PERMISSION_REQUEST_CODE = 100
    private val REQUIRED_PERMISSION = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        makeTabBar()
        if(!checkLocationServicesStatus()){
            showDialogForLocationServiceSetting()
        }else{
            checkRunTimePermission()
        }

    }
    private fun checkLocationServicesStatus(): Boolean{
        val locationManager: LocationManager = (getSystemService(LOCATION_SERVICE) as LocationManager)
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    private fun showDialogForLocationServiceSetting(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("위치 서비스 필요")
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정해주세요")
        builder.setCancelable(true)
        builder.setPositiveButton("설정", DialogInterface.OnClickListener { dialogInterface, i ->
            val callGPSSettingIntent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE)
        })
        builder.setNegativeButton("취소", DialogInterface.OnClickListener { dialogInterface, i ->
            dialogInterface.cancel()
        })
        builder.create().show()
    }
    private fun checkRunTimePermission(){ // 권한 부여 여부 확인
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            this, android.Manifest.permission.ACCESS_FINE_LOCATION)
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            this, android.Manifest.permission.ACCESS_COARSE_LOCATION)

        if(hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED){
            // 이미 퍼미션을 가지고 있다면
            showGoogleMap()

            // 위치 값을 가져올 수 있음
        }
        else{ // 퍼미션 요청을 허용한 적이 없으면 퍼미션 요청이 필요
            // 사용사작 퍼미션 거부를 한 적이 있는 경우
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSION[0])){
                Toast.makeText(this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSION, PERMISSION_REQUEST_CODE)
            }
            else{// 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 함
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSION,
                    PERMISSION_REQUEST_CODE)
            }
        }

    }
    private fun showGoogleMap(){
        val fragment = MapsFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }
    private fun showFeed(){
        val fragment = PostFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    private fun makeTabBar(){
        binding.mainTabLayout.addTab(binding.mainTabLayout.newTab().setText("매장"))
        binding.mainTabLayout.addTab(binding.mainTabLayout.newTab().setText("피드"))
        binding.mainTabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if(tab != null){
                    if(tab.position == 0) showGoogleMap()
                    else showFeed()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
}