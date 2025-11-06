/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.endpoint.dynamic.registration.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import org.apache.cxf.rs.security.oauth2.services.ClientRegistration;

/**
 * @author Jorge García Jiménez
 */
public class LiferayClientRegistration extends ClientRegistration {

	@Override
	public String getApplicationType() {
		return applicationType;
	}

	@Override
	public String getClientName() {
		return clientName;
	}

	@Override
	public String getClientUri() {
		return clientUri;
	}

	@Override
	public List<String> getContacts() {
		return contacts;
	}

	@Override
	public List<String> getGrantTypes() {
		return grantTypes;
	}

	@Override
	public String getLogoUri() {
		return logoUri;
	}

	@Override
	public String getPolicyUri() {
		return policyUri;
	}

	@Override
	public List<String> getRedirectUris() {
		return redirectUris;
	}

	@Override
	public List<String> getResourceUris() {
		return resourceUris;
	}

	@Override
	public List<String> getResponseTypes() {
		return responseTypes;
	}

	@Override
	public String getScope() {
		return scope;
	}

	@Override
	public String getTokenEndpointAuthMethod() {
		return tokenEndpointAuthMethod;
	}

	@Override
	public String getTosUri() {
		return tosUri;
	}

	@Override
	public void setApplicationType(String applicationType) {
		this.applicationType = applicationType;
	}

	@Override
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	@Override
	public void setClientUri(String clientUri) {
		this.clientUri = clientUri;
	}

	@Override
	public void setContacts(List<String> contacts) {
		this.contacts = contacts;
	}

	@Override
	public void setGrantTypes(List<String> grantTypes) {
		this.grantTypes = grantTypes;
	}

	@Override
	public void setLogoUri(String logoUri) {
		this.logoUri = logoUri;
	}

	@Override
	public void setPolicyUri(String policyUri) {
		this.policyUri = policyUri;
	}

	@Override
	public void setRedirectUris(List<String> redirectUris) {
		this.redirectUris = redirectUris;
	}

	@Override
	public void setResourceUris(List<String> resourceUris) {
		this.resourceUris = resourceUris;
	}

	@Override
	public void setResponseTypes(List<String> responseTypes) {
		this.responseTypes = responseTypes;
	}

	@Override
	public void setScope(String scope) {
		this.scope = scope;
	}

	@Override
	public void setTokenEndpointAuthMethod(String tokenEndpointAuthMethod) {
		this.tokenEndpointAuthMethod = tokenEndpointAuthMethod;
	}

	@Override
	public void setTosUri(String tosUri) {
		this.tosUri = tosUri;
	}

	@JsonProperty("application_type")
	protected String applicationType;

	@JsonProperty("client_name")
	protected String clientName;

	@JsonProperty("client_uri")
	protected String clientUri;

	@JsonProperty("contacts")
	protected List<String> contacts;

	@JsonProperty("grant_types")
	protected List<String> grantTypes;

	@JsonProperty("logo_uri")
	protected String logoUri;

	@JsonProperty("policy_uri")
	protected String policyUri;

	@JsonProperty("redirect_uris")
	protected List<String> redirectUris;

	@JsonProperty("resource_uris")
	protected List<String> resourceUris;

	@JsonProperty("response_types")
	protected List<String> responseTypes;

	@JsonProperty("scopes")
	protected String scope;

	@JsonProperty("token_endpoint_auth_method")
	protected String tokenEndpointAuthMethod;

	@JsonProperty("tos_uri")
	protected String tosUri;

}