package APIValidation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

public class APIValidation {
	//defining the acceptance criteria, description of specific promotion is case-senstive
	public static String key_Name = "Name", key_Relist = "CanRelist", key_Promotions = "Promotions", 
				promotionsKey = "Name", promotionsValidationKey = "Description",
					promotionsKeyValidator = "Gallery", promotionsValidatorfactor = "2x larger image";
	public static HashMap ValidationOutput = new HashMap();

	public static void main(String[] args) {
		
		APIValidation thisObj = new APIValidation();
		//Calling the validator method, all acceptance criteria's are defined globally.
		thisObj.APIAcceptanceValidator();
		System.out.println("Based on validation criteria, Output for Validation is:\n\n "+ ValidationOutput);
	}
	
	/**
	 * Method to Validate globally defined acceptance criteria
	 */
	public void APIAcceptanceValidator()
	{
		//API to be called
		String APIURL = "https://api.tmsandbox.co.nz/v1/Categories/6327/Details.json?catalogue=false";
		try
		{
			URL urlObj = new URL(APIURL);
			HttpsURLConnection apiConnection = (HttpsURLConnection) urlObj.openConnection();

			apiConnection.setRequestMethod("GET");
			int responseCode = apiConnection.getResponseCode();
			//Validation of connection establishment
			if(responseCode == HttpsURLConnection.HTTP_OK)
			{
				BufferedReader apiResponseReader = new BufferedReader(new InputStreamReader(apiConnection.getInputStream()));
				String inputLine = null, jsonKeyReader = null,message = "fail";
				//Reading the response from API
				while((inputLine = apiResponseReader.readLine()) != null)
				{
					JSONObject apiJSON = new JSONObject(inputLine);
					/*
					 * 
					 * Acceptance criteria is already defined, 
					 * If expected object type isn't matched, validation fails with invalid type.
					 */
					if(apiJSON.get(key_Name) instanceof CharSequence)
					{
						jsonKeyReader = apiJSON.get(key_Name).toString();
						ValidationOutput.put("Name", jsonKeyReader.equalsIgnoreCase("Carbon Credits")?true:false);						
					}
					else
					{
						ValidationOutput.put("Name Validation", "Failed because, API response contains Name of unexpected type");
					}
					
					if(apiJSON.get(key_Relist) instanceof Boolean)
					{
						ValidationOutput.put("Relist", (Boolean)apiJSON.get(key_Relist)?true:false);
					}
					else
					{
						ValidationOutput.put("Relist Validation", "Failed because, API response contains Relist of unexpected type");
					}
					
					if(apiJSON.get(key_Promotions) instanceof JSONArray)
					{
						JSONArray jsonPromotionArray = (JSONArray)apiJSON.get(key_Promotions);
						JSONObject promotionObj;
						for(int i = 0; i < jsonPromotionArray.length(); i++)
						{
							promotionObj = (JSONObject) jsonPromotionArray.get(i);
							if(promotionObj.get(promotionsKey) instanceof CharSequence && promotionObj.get(promotionsKey).toString().equalsIgnoreCase(promotionsKeyValidator))
							{
								ValidationOutput.put("Promotions", promotionObj.get(promotionsValidationKey).toString().contains(promotionsValidatorfactor)?true:false);
							}
						}
					}
					else
					{
						ValidationOutput.put("Promotions Validation", "Failed because, API response contains Promotions of unexpected type");
					}
				}
			}
			else
			{
				ValidationOutput.put("{ERROR}:","Connection cannot be established");
			}
			
		}
		catch(Exception ex)
		{
			ValidationOutput.put("Exception:", ex.fillInStackTrace());
		}
	}

}
