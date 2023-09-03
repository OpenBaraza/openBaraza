
import java.util.logging.Logger;
import java.util.concurrent.TimeUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.LinkedHashMap;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.baraza.DB.BDB;
import org.baraza.DB.BQuery;

public class BFCMApi {
	Logger log = Logger.getLogger(BFCMApi.class.getName());

	private static final String PARTNER_URL = "https://demo-fcm-kenya.traacs.co:8181/nucorelib/common_api_integration/partners_list";
	private static final String VOUCHER_URL = "https://demo-fcm-kenya.traacs.co:8181/nucorelib/common_api_integration/save_Transfer_voucher";
	private static final String USERNAME = "Fcmsafarilogix";
	private static final String PASSWORD = "Fcm@safari123";
	
	public static void main(String[] args) {
		System.out.println("Starting");
		BFCMApi bfcmApi = new BFCMApi();

		if(args.length == 0) {
			bfcmApi.getPartners();
			bfcmApi.createVoucher();
		} else if(args[0].equals("customer")) {
			bfcmApi.getPartners();
		} else if(args[0].equals("voucher")) {
			bfcmApi.createVoucher();
		}
	}

	public void getPartners() {
		JSONObject jData = new JSONObject();
		jData.put("STR_USER_NAME", USERNAME);
		jData.put("STR_PASSWORD", PASSWORD);
		jData.put("STR_PARTNER_TYPE", "CUSTOMER");
		jData.put("STR_PARTNER_CATEGORY", "");
		jData.put("STR_PARTNER_GROUP", "");

		String sResp = makeCall(PARTNER_URL, jData.toString());
		JSONObject jResp = new JSONObject(sResp);
		JSONArray jaData = jResp.getJSONArray("data");

		String jdbcUrl = "jdbc:postgresql://kifaru:5432/fcm";
		String username = "postgres";
		String password = "Invent2k";
		BDB db = new BDB("org.postgresql.Driver", jdbcUrl, username, password);

		String inSql = "INSERT INTO traacs_customers (ProfileId, MainLedgerCode, "
			+ "Name, Currency, strCreatedBy, intPartnerProfileId, CustomerCategory) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?)";

		for (int i = 0; i < jaData.length(); i++) {
			JSONObject jCust = jaData.getJSONObject(i);

			String mySql = "SELECT name FROM traacs_customers "
			+ "WHERE ProfileId = '" + jCust.getString("ProfileId") + "'";
			String customerName = db.executeFunction(mySql);

			if(customerName == null) {
				Map<String, String> mData = new LinkedHashMap<String, String>();
				mData.put("ProfileId", jCust.getString("ProfileId"));
				mData.put("MainLedgerCode", jCust.getString("MainLedgerCode"));
				mData.put("Name", jCust.getString("Name"));
				mData.put("Currency", jCust.getString("Currency"));
				mData.put("strCreatedBy", jCust.getString("strCreatedBy"));
				mData.put("intPartnerProfileId", jCust.get("intPartnerProfileId").toString());
				mData.put("CustomerCategory", jCust.getString("CustomerCategory"));
				String customerProfileId = db.saveRec(inSql, mData);
			} else if(!jCust.getString("Name").equals(customerName)) {
				String updSql = "UPDATE traacs_customers SET Name = '" + jCust.getString("Name")
					+ "' WHERE ProfileId = '" + jCust.getString("ProfileId") + "'";
				db.executeUpdate(updSql);
			}
		}

		db.close();
	}

	public void createVoucher() {
		BDB db = new BDB("org.postgresql.Driver", "jdbc:postgresql://kifaru/fcm", "postgres", "Invent2k");
		String mySql  = "SELECT to_char(booking_date, 'DD/MM/YYYY') as trx_date, "
			+ "to_char(pickup_date, 'DD/MM/YYYY') as f_pickup_date, "
			+ "COALESCE(to_char(time_end, 'HH24:MM:SS'), pickup_time) as f_time_end, * "
			+ "FROM vw_transfer_assignments "
			+ "WHERE (transfer_status_id = 4) "
			+ "AND (time_end is not null) AND (time_end > pickup_time::time) "
			+ "AND (voucher_ref = '330/366/325')";
			//+ "AND (synched = false) LIMIT 1";
			///AND (closed = true)";
		BQuery rs = new BQuery(db, mySql);

		while(rs.moveNext()){
			JSONObject jData = new JSONObject();

			JSONObject jAuth = new JSONObject();
			jAuth.put("STR_USER_NAME", USERNAME);
			jAuth.put("STR_PASSWORD", PASSWORD);
			jData.put("str_authentication_key", jAuth);

			String voucherRef = rs.getString("voucher_ref");
			voucherRef = voucherRef.replace("/", "");

			JSONObject jMaster = new JSONObject();
			jMaster.put("STR_VOUCHER_NO", voucherRef);
			jMaster.put("STR_AUTO_INVOICE", "NO");
			jMaster.put("STR_ACTION", "NEW");
			jMaster.put("STR_STATUS", "ISSUE");
			jMaster.put("STR_FILE_NUMBER", "");
			jMaster.put("STR_ISSUE_DATE",  rs.getString("trx_date"));
			jMaster.put("STR_COST_CENTRE_CODE",  "002");
			jMaster.put("STR_DEPARTMENT_CODE",  "002");
			jMaster.put("STR_SERVICE",  "Car Hire");
			jMaster.put("STR_SUPPLIER_CODE", "SA005");					//rs.getString("supplier_code"));
			jMaster.put("STR_ACCOUNT_CODE", rs.getString("customer_code"));
			jMaster.put("STR_SUB_CUSTOMER_CODE",  "");				// ask if its customer code
			jMaster.put("STR_MODE_OF_PAYMENT", rs.getString("payment_type_name"));
			jMaster.put("STR_CORP_CREDIT_CARD_ACCOUNT_CODE",  "");
			jMaster.put("STR_CUSTOMER_CORP_CREDIT_CARD_ACCOUNT_CODE",  "");
			jMaster.put("STR_TRAVELER_ID",  "");
			jMaster.put("STR_PAX_NAME",  rs.getString("passanger_name"));
			jMaster.put("STR_SUPPLIER_CONF_NUMBER",  "");
			jMaster.put("STR_STAFF",  "ANAIS DUSHIME"); 	//rs.getString("booker_name"));
			jMaster.put("STR_PICK_UP_DATE", rs.getString("f_pickup_date"));
			jMaster.put("STR_PICK_UP_TIME", rs.getString("pickup_time"));
			jMaster.put("STR_DROP_OFF_DATE",  rs.getString("f_pickup_date"));
			jMaster.put("STR_DROP_OFF_TIME",  "15:20:00");	//rs.getString("f_time_end"));
			jMaster.put("STR_LOCATION",  rs.getString("pickup"));
			jMaster.put("STR_DROP_OFF_LOCATION",  rs.getString("dropoff"));
			jMaster.put("STR_ADDITIONAL_SERVICE",  "");
			jMaster.put("STR_PAYBACK_ACCOUNT_CODE",  "");
			jMaster.put("STR_PNR_NO",  "232434-1");
			jMaster.put("STR_LPO_NO",  rs.getString("lpo_number"));
			jMaster.put("STR_LPO_DATE",  "");
			jMaster.put("STR_CUSTOMER_REF_NO",  "REFE_CUST");
			jMaster.put("STR_CUSTOMER_EMP_NO",  "EMP_CUST_NO");
			jMaster.put("STR_REFUND_DATE",  "");
			jMaster.put("STR_REFUND_STAFF",  "");
			jMaster.put("STR_REFUND_STATUS",  "");
			jMaster.put("DBL_PURCHASE_ROE",  "1");
			jMaster.put("STR_PURCHASE_CUR_CODE",  "KES");
			jMaster.put("DBL_SELLING_ROE",  "1");
			jMaster.put("STR_SELLING_CUR_CODE",  "KES");
			jData.put("json_master", jMaster);

			JSONObject jDetail = new JSONObject();
			jDetail.put("STR_FLEET_TYPE",  "Own fleet own chauffeur");
			jDetail.put("STR_CHAUFFER",  rs.getString("driver_name"));
			jDetail.put("STR_CAR_PLATE_NO",  "");
			jDetail.put("STR_CAR_MODEL",  "");
			jDetail.put("STR_CAR_MAKE",  "");
			jDetail.put("STR_CAR_YEAR",  "");
			jDetail.put("DBL_PURCHASE_CUR_TOTAL_RATE",  "0");
			jDetail.put("DBL_PURCHASE_CUR_TOTAL_TAX",  "0");
			jDetail.put("DBL_PURCHASE_CUR_SUP_COMMISION",  "");
			jDetail.put("DBL_PURCHASE_CUR_SUPPLIER_FEE",  "");
			jDetail.put("DBL_PURCHASE_CUR_SUPPLIER_CHARGE",  "");
			jDetail.put("DBL_PURCHASE_CUR_SUPPLIER_AMOUNT",  "");
			jDetail.put("DBL_PURCHASE_CUR_AGENCY_CHARGE",  "");
			jDetail.put("DBL_PURCHASE_CUR_SERVICE_FEE",  "100");
			jDetail.put("DBL_PURCHASE_CUR_EXTRA_EARNING",  "");
			jDetail.put("DBL_PURCHASE_CUR_PAYBACK_AMOUNT",  "");
			jDetail.put("DBL_PURCHASE_CUR_CC_CHARGES",  "");
			jDetail.put("DBL_PURCHASE_CUR_DISCOUNT",  "");
			jDetail.put("DBL_PURCHASE_CUR_PRICE",  "");
			jDetail.put("DBL_BASE_CUR_TOTAL_RATE",  "");
			jDetail.put("DBL_BASE_CUR_TOTAL_TAX",  "");
			jDetail.put("DBL_BASE_CUR_SUP_COMMISION",  "");
			jDetail.put("DBL_BASE_CUR_SUPPLIER_FEE",  "");
			jDetail.put("DBL_BASE_CUR_SUPPLIER_CHARGE",  "");
			jDetail.put("DBL_BASE_CUR_SUPPLIER_AMOUNT",  "");
			jDetail.put("DBL_BASE_CUR_AGENCY_CHARGE",  "");
			jDetail.put("DBL_BASE_CUR_SERVICE_FEE",  "");
			jDetail.put("DBL_BASE_CUR_EXTRA_EARNING",  "");
			jDetail.put("DBL_BASE_CUR_PAYBACK_AMOUNT",  "");
			jDetail.put("DBL_BASE_CUR_CC_CHARGES",  "");
			jDetail.put("DBL_BASE_CUR_DISCOUNT",  "");
			jDetail.put("DBL_BASE_CUR_PRICE",  "");
			jDetail.put("DBL_SELLING_CUR_PRICE",  "");
			jDetail.put("DBL_SELLING_CUR_INPUT_VAT",  "");
			jDetail.put("DBL_PURCHASE_CUR_INPUT_VAT",  "");
			jDetail.put("DBL_PURCHASE_CUR_OUTPUT_VAT",  "");
			jDetail.put("DBL_BASE_CUR_OUTPUT_VAT",  JSONObject.NULL);
			jDetail.put("DBL_SELLING_CUR_OUTPUT_VAT",  JSONObject.NULL);
			JSONArray jaDetails = new JSONArray();
			jaDetails.put(jDetail);
			jData.put("json_details", jaDetails);

			String sResp = makeCall(VOUCHER_URL, jData.toString());

			String updSql = "UPDATE transfer_assignments SET synched = true WHERE transfer_assignment_id = " + rs.getString("transfer_assignment_id");
			db.executeUpdate(updSql);
		}

		rs.close();
		db.close();
	}
	
	public String makeCall(String url, String jData) {
		String sResp = null;

		try {
System.out.println("BASE URL : " + url);
System.out.println("BASE 2010 : " + jData);
			
			OkHttpClient client = new OkHttpClient();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(jData, mediaType);
			Request request = new Request.Builder()
				.url(url)
				.post(body)
				.addHeader("content-type", "application/json")
				.build();
			Response response = client.newCall(request).execute();
			sResp = response.body().string();
System.out.println(sResp);

		} catch(IOException ex) {
			System.out.println("IO Error : " + ex);
		}

		return sResp;
	}


}
