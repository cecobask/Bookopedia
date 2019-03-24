package ie.bask.fragments;


import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;

import ie.bask.R;
import ie.bask.main.BookopediaApp;
import ie.bask.models.Book;
import ie.bask.models.Coordinates;


public class MapsFragment extends SupportMapFragment implements
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        OnMapReadyCallback {

    private Book book;
    private GoogleMap mMap;
    private BookopediaApp app = BookopediaApp.getInstance();

    public MapsFragment() {
        // Required empty public constructor
    }

    public static MapsFragment newInstance(Book book) {
        MapsFragment fragment = new MapsFragment();
        // Pass book object to onCreate for later use
        Bundle args = new Bundle();
        args.putSerializable("book", book);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Assign the book object to a local variable for ease of use
            book = (Book) getArguments().getSerializable("book");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set callback to retrieve map object
        getMapAsync(this);
        if (mMap != null) {
            initCamera(book.getCoordinates());
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.v("Bookopedia", ""+latLng);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Assign map object to local variable
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        initListeners();

        // Move camera to book location
        initCamera(book.getCoordinates());

        // Set map settings
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    public void initListeners() {
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMapClickListener(this);
    }

    private void initCamera(Coordinates coordinates) {
        // Create a CameraPosition object which will be passed to animateCamera method of GoogleMap object
        CameraPosition position = CameraPosition.builder()
                .target(new LatLng(coordinates.getLatitude(), coordinates.getLongitude()))
                .zoom(17).bearing(0.0f)
                .tilt(0.0f).build();

        // Add marker for current book and display info window
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(coordinates.getLatitude(), coordinates.getLongitude()))
                .title(book.getTitle())
                .snippet(getAddressFromLocation(coordinates))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.book_marker))).showInfoWindow();

        // Move camera to book's location
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), null);

        // Add markers for all books in user's collection
        addBooks();
    }

    public void addBooks(){
        for(Book bookElem : app.booksToRead)
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(bookElem.getCoordinates().getLatitude(), bookElem.getCoordinates().getLongitude()))
                    .title(bookElem.getTitle())
                    .snippet(getAddressFromLocation(bookElem.getCoordinates()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.book_marker)));

    }

    private String getAddressFromLocation(Coordinates coordinates) {
        Geocoder geocoder = new Geocoder( getActivity() );
        String strAddress = "";

        try {
            // Decode coordinates to address
            Address address = geocoder
                    .getFromLocation( coordinates.getLatitude(), coordinates.getLongitude(), 1 )
                    .get( 0 );
            strAddress = address.getAddressLine(0);
        }
        catch (IOException e ) {
            Log.v("Bookopedia", "" + e);
        }

        return strAddress;
    }
}
