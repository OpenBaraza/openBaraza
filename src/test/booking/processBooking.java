
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
	    
import org.json.JSONObject;
import org.json.JSONArray;

public class processBooking {

	Map<String, JSONObject> mPnrs;
	Map<String, JSONObject> mMookings;

	public static void main(String[] args)  {
		try {
			Path fileName = Paths.get("booking.json");
			List<String> lBooking = Files.readAllLines(fileName);
			String sBooking = "";
			for(String sLine : lBooking) sBooking += sLine + "\n";
			
			processBooking bp = new processBooking();
			bp.process(sBooking);
	    } catch(IOException ex) {
	    	System.out.println("Booking file reading error : " + ex.toString());
	    }
	}
	
	public processBooking() {
		mPnrs = new HashMap<String, JSONObject>();
		mMookings = new HashMap<String, JSONObject>();
	}

	public void process(String sBooking) {
		JSONObject jResp = new JSONObject();

		JSONObject jBooking = new JSONObject(sBooking);
		for(String sKey : jBooking.keySet()) {
			System.out.println(sKey);
		}
		
		String bookingKey = jBooking.getString("CurrentAgencyPcc") + jBooking.getString("RecordLocator");
		
		mPnrs.put(bookingKey, jBooking);
		
		jResp.put("RecordLocator", jBooking.getString("RecordLocator"));
		jResp.put("CreationDate", jBooking.getString("CreationDate"));
		jResp.put("CreatorSignOn", jBooking.getString("CreatorSignOn"));
		jResp.put("CurrentAgencyPcc", jBooking.getString("CurrentAgencyPcc"));

		System.out.println("\n----------------\n");
		JSONArray jAirSegments = jBooking.getJSONArray("AirSegments");
		JSONArray jrAirSegments = new JSONArray();
		for(int j = 0; j < jAirSegments.length(); j++) {
			JSONObject jAirSegment = jAirSegments.getJSONObject(j);
			JSONObject jrAirSegment = new JSONObject();
			
			jrAirSegment.put("Origin", jAirSegment.getJSONObject("Origin"));
			jrAirSegment.put("Destination", jAirSegment.getJSONObject("Destination"));
			jrAirSegment.put("Connection", jAirSegment.getBoolean("Connection"));
			jrAirSegment.put("StartDateTime", jAirSegment.getString("StartDateTime"));
			jrAirSegment.put("EndDateTime", jAirSegment.getString("EndDateTime"));
			jrAirSegment.put("SegmentNumber", jAirSegment.getInt("SegmentNumber"));
			jrAirSegments.put(jrAirSegment);
     
			
			for(String sKey : jAirSegment.keySet()) System.out.println(sKey);
		}
		jResp.put("AirSegments", jrAirSegments);
		
		System.out.println("\n----------------\n");
		JSONArray jPassengers = jBooking.getJSONArray("Passengers");
		JSONArray jrPassengers = new JSONArray();
		for(int j = 0; j < jPassengers.length(); j++) {
			JSONObject jPassenger = jPassengers.getJSONObject(j);
			JSONObject jrPassenger = new JSONObject();
			jrPassenger.put("PassengerNumber", jPassenger.getInt("PassengerNumber"));
			jrPassenger.put("FullName", jPassenger.getString("FullName"));
			jrPassengers.put(jrPassenger);
			
			for(String sKey : jPassenger.keySet()) System.out.println(sKey);
		}
		jResp.put("Passengers", jrPassengers);
		
		System.out.println("\n----------------\n");
		JSONArray jFiledFares = jBooking.getJSONArray("FiledFares");
		JSONArray jrFiledFares = new JSONArray();
		for(int j = 0; j < jFiledFares.length(); j++) {
			JSONObject jFiledFare = jFiledFares.getJSONObject(j);
			JSONObject jrFiledFare = new JSONObject();
			jrFiledFare.put("Number", jFiledFare.getInt("Number"));
			jrFiledFare.put("SegmentNumbers", jFiledFare.getJSONArray("SegmentNumbers"));
			
			JSONArray jFares = jFiledFare.getJSONArray("Fares");
			for(int i = 0; i < jFares.length(); i++) {
				JSONObject jFare = jFares.getJSONObject(i);
				jrFiledFare.put("BaseAmount", jFare.getFloat("BaseAmount"));
				jrFiledFare.put("TotalAmount", jFare.getFloat("TotalAmount"));
				jrFiledFare.put("EquivalentAmount", jFare.getFloat("EquivalentAmount"));
				jrFiledFare.put("TaxAmount", jFare.getFloat("TaxAmount"));
				jrFiledFare.put("NetFareAmount", jFare.getFloat("NetFareAmount"));
				
				jrFiledFare.put("BaseAmountCurrency", jFare.getString("BaseAmountCurrency"));
				jrFiledFare.put("TotalAmountCurrency", jFare.getString("TotalAmountCurrency"));
				jrFiledFare.put("EquivalentAmountCurrency", jFare.getString("EquivalentAmountCurrency"));
				jrFiledFare.put("TaxCurrency", jFare.getString("TaxCurrency"));
				jrFiledFare.put("NetFareCurrency", jFare.getString("NetFareCurrency"));
				
				jrFiledFares.put(jrFiledFare);
			}
			
			for(String sKey : jFiledFare.keySet()) System.out.println(sKey);
		}
		jResp.put("FiledFares", jrFiledFares);
		
		System.out.println("\n----------------\n");
		JSONArray jSegments = jBooking.getJSONArray("Segments");
		for(int j = 0; j < jSegments.length(); j++) {
			JSONObject jSegment = jSegments.getJSONObject(j);
			
			for(String sKey : jSegment.keySet()) System.out.println(sKey);
		}
		
		mMookings.put(bookingKey, jResp);
		
		System.out.println("\n----------------\n");
		System.out.println(jResp.toString());
	}
	
}
