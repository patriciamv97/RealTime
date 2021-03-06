package com.dam2a.realtime

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.dam2a.realtime.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    companion object {
        const val REQUEST_CODE_LOCATION = 0
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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
    private fun createMarker() {
        val vigo = LatLng(42.23282, -8.72264)
        mMap.addMarker(MarkerOptions().position(vigo).title("Vigo"))
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(vigo, 18f),
            4000,
            null
        )
    }

    /**
     * M??todo que comprueba que el permiso este activado, pidiendo el permiso y viendo si es igual a el PERMISSION_GRANTED
     */
    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED


    //SupressLint es una interfaz que indica que se deben ignoar las advertencias especificadas

    /**
     * M??todo que intenta activar la localizaci??n
     */
    @SuppressLint("MissingPermission")
    private fun enableLocation() {
        //Si el mapa no est?? inicializado vete
        if (!::mMap.isInitialized) return
        if (isLocationPermissionGranted()) {
            //si  ha aceptado los permisos permitimos la localizaci??n
            mMap.isMyLocationEnabled = true
        } else {
            //no est?? activado el permiso , se lo tenemos que pedir a trav??s
            // del siguiente m??todo
            requestLocationPermission()
        }
    }

    /**
     * M??todo para pedir al usuario que active los permisos
     */
    private fun requestLocationPermission() {
        //Si cumple el primer if significa que le hemos pedido ya al usuario los permisos y los ha rechazado
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            Toast.makeText(this, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()

        } else {
            //Si entra por el else es la primera vez que le pedimos los permisos
            //Se le pasa el companion object para saber si ha aceptado los permisos
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION
            )
        }
    }

    /**
     * M??todo que captura la respuesta del usuario si acepta los permisos
     */
    @SuppressLint("MissingSuperCall", "MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        // cuando el requestcode se igual al companion object definido
        when (requestCode) {
            // Si grantResults no est?? vacio y el permiso 0 est?? aceptado signfica que ha acptado nuestros permisos
            REQUEST_CODE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.isMyLocationEnabled = true
            } else {
                Toast.makeText(
                    this,
                    "Para activar la localizaci??n ve a ajustes y acepta los permisos",
                    Toast.LENGTH_SHORT
                ).show()

            }
            //Por si ha aceptado otro permiso
            else -> {
            }
        }
    }

    /**
     * M??todo que sirve para comprobar si los permisos siguen activos caundo el usuario
     * se va de la aplicaci??n y vuelve para que la aplicaci??n no rompa
     */
    override fun onResumeFragments() {
        super.onResumeFragments()
        if (!::mMap.isInitialized) return
        if (!isLocationPermissionGranted()) {
            !mMap.isMyLocationEnabled
            Toast.makeText(this, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()


        }
    }

    /**
     * M??todo que sirve para que cuando el usuario pulse el bot??n OnMyLocation
     * le lleve a la ubicaci??n
     */
    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "Boton pulsado", Toast.LENGTH_SHORT).show()
        return false
    }

    /**
     * M??todo que muestra la latitud y longitud de nuestra ubicaci??n cuando pulsamos sobre ella
     */
    override fun onMyLocationClick(p0: Location) {
        Toast.makeText(this, "Est??s en ${p0.latitude},${p0.longitude} ", Toast.LENGTH_SHORT).show()
    }
}