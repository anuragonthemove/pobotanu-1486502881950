package com.ibm.cto;

import java.util.Set;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class Configuration {

	private static Configuration instance = null;


	public static final String SERVICE_CONVERSATION = "conversation";


	public String CONVERSATION_API_URL = "https://gateway.watsonplatform.net/conversation/api";

	/**
	 * TODO: If you're testing this application locally, please get the credentials from Bluemix
	 */


	public String CONVERSATION_USERNAME = "a78f2740-3c32-4dcc-a947-75414266194b";
	public String CONVERSATION_PASSWORD = "Lm65w0UPd5lm";

	/**
	 * TODO: Get Workspace ID from IBM Watson Conversation: https://ibmwatsonconversation.com
	 */
	public String CONVERSATION_WORKSPACE_ID = "ca4d7ddf-46d9-4716-b1c6-bd3cc8fbce49";

	/**
	 * TODO: After deploy your nodejs service for controlling robot car, fill in the host name here
	 */
	public String CAR_SERVICE_HOST = "pobotanu.mybluemix.net";

	/**
	 * Load credentials and URLs
	 * 
	 * @return VCAPConfiguration
	 */
	public static Configuration getInstance() {
		if(instance == null) {
			instance = new Configuration();
			String CONVERSATION_WORKSPACE_STRING = System.getenv("CONVERSATION_WORKSPACE_ID");
			String CAR_SERVICE_HOST_STRING = System.getenv("CAR_SERVICE_HOST");

			if(CONVERSATION_WORKSPACE_STRING == null || CAR_SERVICE_HOST_STRING == null) {
				return instance;
			}
			else {
				instance.CONVERSATION_WORKSPACE_ID = CONVERSATION_WORKSPACE_STRING;
				instance.CAR_SERVICE_HOST = CAR_SERVICE_HOST_STRING;
			}

			System.out.println("### Conversation Workspace ID ###");
			System.out.println(instance.CONVERSATION_WORKSPACE_ID);
			System.out.println("### /Conversation Workspace ID ###");
			System.out.println("### Car Host ###");
			System.out.println(instance.CAR_SERVICE_HOST);
			System.out.println("### /Car Host ###");

			JSONObject vcapConfig = getObjectSettings("VCAP_SERVICES");

			if(vcapConfig == null) {
				System.out.println("VCAP_SERVICES is invalid:");
				return instance;
			}
			else {
				System.out.println("### VCAP_SERVICES ###");
				System.out.println(vcapConfig);
				System.out.println("### /VCAP_SERVICES ###");
			}
			Set<String> keySet = vcapConfig.keySet();

			for (String serviceKey : keySet) {
				System.out.println("### Iterating key: " + serviceKey + " ###");
				JSONArray serviceList = vcapConfig.getJSONArray(serviceKey);
				if(serviceList.size() > 0) {
					JSONObject serviceItem = serviceList.getJSONObject(0);
					String credentialsKey = "credentials";

					if(serviceItem.containsKey(credentialsKey)) {
						JSONObject serviceCredentials = serviceItem.getJSONObject(credentialsKey);

						if (serviceKey.startsWith(SERVICE_CONVERSATION)) {
							instance.CONVERSATION_USERNAME = serviceCredentials.get("username").toString();
							instance.CONVERSATION_PASSWORD = serviceCredentials.get("password").toString();
							instance.CONVERSATION_API_URL = serviceCredentials.get("url").toString();
						}
						else if(serviceKey.startsWith(SERVICE_SPEECH_TO_TEXT)) {
							instance.SPEECH_TO_TEXT_USERNAME = serviceCredentials.get("username").toString();
							instance.SPEECH_TO_TEXT_PASSWORD = serviceCredentials.get("password").toString();
							instance.SPEECH_TO_TEXT_API_URL = serviceCredentials.get("url").toString();
						}
						else if(serviceKey.startsWith(SERVICE_TEXT_TO_SPEECH)) {
							instance.TEXT_TO_SPEECH_USERNAME = serviceCredentials.get("username").toString();
							instance.TEXT_TO_SPEECH_PASSWORD = serviceCredentials.get("password").toString();
							instance.TEXT_TO_SPEECH_API_URL = serviceCredentials.get("url").toString();
						}
						else {
							System.out.println("### No such key: " + serviceKey);
						}
					}
					else {
						System.out.println("### No such key: " + credentialsKey);
					}

				}
				else {
					System.out.println("### Empty service list ###");
				}
			}
		}

		return instance;
	}


	/**
	 * Get VCAP configurations
	 * 
	 * @return JSONObject
	 */
	public static JSONObject getObjectSettings(String key) {
		String envServices = System.getenv(key);
		if (envServices == null)
			return null;
		JSONObject sysEnv = null;
		sysEnv = JSONObject.parseObject(envServices);
		return sysEnv;
	}
}
