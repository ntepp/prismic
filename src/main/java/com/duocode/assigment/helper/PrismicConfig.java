package com.duocode.assigment.helper;

import org.springframework.beans.factory.annotation.Value;
	import org.springframework.stereotype.Component;

	@Component
	public class PrismicConfig {

	    private String apiEndpoint;
	    private String accessToken;
	    private String clientId;
	    private String clientSecret;

	    @Value("${prismic.api.url}")
	    public void setApiEndpoint(String apiEndpoint) {
	        this.apiEndpoint = apiEndpoint;
	    }

	    @Value("${prismic.api.token}")
	    public void setAccessToken(String accessToken) {
	    	if(accessToken!=null || accessToken == "")
	    		accessToken = null;
	        this.accessToken = accessToken;
	    }

	    /**
		 * @return the apiEndpoint
		 */
		public String getApiEndpoint() {
			return apiEndpoint;
		}

		/**
		 * @return the accessToken
		 */
		public String getAccessToken() {
			return accessToken;
		}

		/**
		 * @return the clientId
		 */
		public String getClientId() {
			return clientId;
		}

		/**
		 * @return the clientSecret
		 */
		public String getClientSecret() {
			return clientSecret;
		}

		@Value("${prismic.api.client.id}")
	    public void setClientId(String clientId) {
	        this.clientId = clientId;
	    }

	    @Value("${prismic.api.client.secret}")
	    public void setClientSecret(String clientSecret) {
	        this.clientSecret = clientSecret;
	    }

	    

	
}
