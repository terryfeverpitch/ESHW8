package google.map.viewermap.ui;

import java.io.Serializable;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import google.map.viewermap.MapActivity;

public class MapView extends WebView {	
	private String MAP_URL = "file:///android_asset/map.html";
	private boolean loaded = false;
	private ArrayList<String> searchHistory = new ArrayList<String>();
	private Context ctx;
//	private ArrayList<Place> records = new ArrayList<Place>();
	private WebViewClient client = new WebViewClient() {
		@Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {          
        }
		
		@Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        return shouldOverrideUrlLoading(view, url);
	    }
		
		@Override  
		public void onPageFinished(WebView view, String url) {
			MapView.this.loaded = true;
			initMap();
		}
	};
	
	public MapView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		ctx = context;
		getSettings().setJavaScriptEnabled(true);
		setWebViewClient(client);
		loadUrl(MAP_URL);
		addJavascriptInterface(new GetJavascriptVarHandler(), "getJsVar");
	}
	
	public void initMap() {
		// 25.074378, 121.661085 = home
		// 25.066884, 121.522144 = school
		loadUrl("javascript:initialize(25.074378, 121.661085)");
	}
	
	public void goSearch(String place) {
		loadUrl("javascript:goSearch('" + place + "')");
		if(!searchHistory.contains(place))
			searchHistory.add(place);
	}
	
	public ArrayList<String> getHistory() {
		return searchHistory;
	}
	
	public void markPlace() {
		loadUrl("javascript:createMarker()");
	}
	
	public void recordPlace() {
		markPlace();
		loadUrl("javascript:getPlaceInfo()");
	}

    public void setPath(String start, String end) {
        Toast t = Toast.makeText(ctx, "From " + start + " to " + end, Toast.LENGTH_SHORT);
        t.show();

        Place aPoint = MapActivity.queryFromDB(start);
        Place bPoint = MapActivity.queryFromDB(end);

        if(aPoint == null)
            loadUrl("javascript:setStart('" + start + "')");
        else
            loadUrl("javascript:setStart('" + aPoint.getLocation() + "')");

        if(bPoint == null)
            loadUrl("javascript:setEnd('" + end + "')");
        else
            loadUrl("javascript:setEnd('" + bPoint.getLocation() + "')");

        loadUrl("javascript:calRoute()");
    }
	
	public void getFocus(Place p) {
		Toast t = Toast.makeText(ctx, "focus on " + p.getName(), Toast.LENGTH_SHORT);
        String js = "javascript:focusOn('" + p.getName() + "' , '" + p.getLat() + "', '" + p.getLng() + "')";
		loadUrl(js);
		
		t.show();
	}

    public void clearMap() {
        loadUrl("javascript:clearMap()");
        Toast t = Toast.makeText(ctx, "clean up.", Toast.LENGTH_SHORT);
        t.show();
    }

	final class GetJavascriptVarHandler {

		GetJavascriptVarHandler() {

        }

	   // This annotation is required in Jelly Bean and later:
	   @JavascriptInterface
	   public void sendToAndroid(String text) {
		   Toast t = Toast.makeText(ctx, text, Toast.LENGTH_SHORT);
		   t.show();
	   }
	   
	   @JavascriptInterface
	   public void sendLatLng(float lat, float lng) {
		   Toast t = Toast.makeText(ctx, "lat = " + lat + ", lng = " + lng, Toast.LENGTH_SHORT);
		   t.show();
	   }
	   
	   @JavascriptInterface
	   public void sendPlaceInfo(String place, float lat, float lng) {
           Place tmp = MapActivity.queryFromDB(place);

           if(tmp != null) {
               Toast t = Toast.makeText(ctx, place + " has already existed.", Toast.LENGTH_SHORT);
               t.show();
           }
           else {
               tmp = new Place(place, lat, lng);

               if(MapActivity.addToDB(tmp)) {
                   Toast t = Toast.makeText(ctx, place + " (" + lat + ", " + lng + ")", Toast.LENGTH_SHORT);
                   t.show();
               }
               else {
                   Toast t = Toast.makeText(ctx, "add to DB failed", Toast.LENGTH_SHORT);
                   t.show();
               }
           }
	   }
	}
	
	public static class Place implements Serializable {
		private String name;
		private float lat;
		private float lng;

        public Place(String name, float lat, float lng) {
			this.name = name;
			this.lat = lat;
			this.lng = lng;
		}

        public float getLat() {
            return lat;
        }

        public float getLng() {
            return lng;
        }
		
		public String getName() {
			return this.name;
		}
		
		public String getLocation() {
			return "(" + lat + ", " + lng + ")";
		}
		
		public String getInfo() {
			return this.name + " (" + lat + ", " + lng + ")";
		}
	}
}
