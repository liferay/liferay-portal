/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.persistence.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link OpenIdConnectSession}.
 * </p>
 *
 * @author Arthur Chan
 * @see OpenIdConnectSession
 * @generated
 */
public class OpenIdConnectSessionWrapper
	extends BaseModelWrapper<OpenIdConnectSession>
	implements ModelWrapper<OpenIdConnectSession>, OpenIdConnectSession {

	public OpenIdConnectSessionWrapper(
		OpenIdConnectSession openIdConnectSession) {

		super(openIdConnectSession);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("openIdConnectSessionId", getOpenIdConnectSessionId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("accessToken", getAccessToken());
		attributes.put(
			"accessTokenExpirationDate", getAccessTokenExpirationDate());
		attributes.put("authServerWellKnownURI", getAuthServerWellKnownURI());
		attributes.put("clientId", getClientId());
		attributes.put("idToken", getIdToken());
		attributes.put("issuer", getIssuer());
		attributes.put("refreshToken", getRefreshToken());
		attributes.put("sessionId", getSessionId());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		Long openIdConnectSessionId = (Long)attributes.get(
			"openIdConnectSessionId");

		if (openIdConnectSessionId != null) {
			setOpenIdConnectSessionId(openIdConnectSessionId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Long userId = (Long)attributes.get("userId");

		if (userId != null) {
			setUserId(userId);
		}

		Date modifiedDate = (Date)attributes.get("modifiedDate");

		if (modifiedDate != null) {
			setModifiedDate(modifiedDate);
		}

		String accessToken = (String)attributes.get("accessToken");

		if (accessToken != null) {
			setAccessToken(accessToken);
		}

		Date accessTokenExpirationDate = (Date)attributes.get(
			"accessTokenExpirationDate");

		if (accessTokenExpirationDate != null) {
			setAccessTokenExpirationDate(accessTokenExpirationDate);
		}

		String authServerWellKnownURI = (String)attributes.get(
			"authServerWellKnownURI");

		if (authServerWellKnownURI != null) {
			setAuthServerWellKnownURI(authServerWellKnownURI);
		}

		String clientId = (String)attributes.get("clientId");

		if (clientId != null) {
			setClientId(clientId);
		}

		String idToken = (String)attributes.get("idToken");

		if (idToken != null) {
			setIdToken(idToken);
		}

		String issuer = (String)attributes.get("issuer");

		if (issuer != null) {
			setIssuer(issuer);
		}

		String refreshToken = (String)attributes.get("refreshToken");

		if (refreshToken != null) {
			setRefreshToken(refreshToken);
		}

		String sessionId = (String)attributes.get("sessionId");

		if (sessionId != null) {
			setSessionId(sessionId);
		}
	}

	@Override
	public OpenIdConnectSession cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the access token of this open ID connect session.
	 *
	 * @return the access token of this open ID connect session
	 */
	@Override
	public String getAccessToken() {
		return model.getAccessToken();
	}

	/**
	 * Returns the access token expiration date of this open ID connect session.
	 *
	 * @return the access token expiration date of this open ID connect session
	 */
	@Override
	public Date getAccessTokenExpirationDate() {
		return model.getAccessTokenExpirationDate();
	}

	/**
	 * Returns the auth server well known uri of this open ID connect session.
	 *
	 * @return the auth server well known uri of this open ID connect session
	 */
	@Override
	public String getAuthServerWellKnownURI() {
		return model.getAuthServerWellKnownURI();
	}

	/**
	 * Returns the client ID of this open ID connect session.
	 *
	 * @return the client ID of this open ID connect session
	 */
	@Override
	public String getClientId() {
		return model.getClientId();
	}

	/**
	 * Returns the company ID of this open ID connect session.
	 *
	 * @return the company ID of this open ID connect session
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the id token of this open ID connect session.
	 *
	 * @return the id token of this open ID connect session
	 */
	@Override
	public String getIdToken() {
		return model.getIdToken();
	}

	/**
	 * Returns the issuer of this open ID connect session.
	 *
	 * @return the issuer of this open ID connect session
	 */
	@Override
	public String getIssuer() {
		return model.getIssuer();
	}

	/**
	 * Returns the modified date of this open ID connect session.
	 *
	 * @return the modified date of this open ID connect session
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this open ID connect session.
	 *
	 * @return the mvcc version of this open ID connect session
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the open ID connect session ID of this open ID connect session.
	 *
	 * @return the open ID connect session ID of this open ID connect session
	 */
	@Override
	public long getOpenIdConnectSessionId() {
		return model.getOpenIdConnectSessionId();
	}

	/**
	 * Returns the primary key of this open ID connect session.
	 *
	 * @return the primary key of this open ID connect session
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the refresh token of this open ID connect session.
	 *
	 * @return the refresh token of this open ID connect session
	 */
	@Override
	public String getRefreshToken() {
		return model.getRefreshToken();
	}

	/**
	 * Returns the session ID of this open ID connect session.
	 *
	 * @return the session ID of this open ID connect session
	 */
	@Override
	public String getSessionId() {
		return model.getSessionId();
	}

	/**
	 * Returns the user ID of this open ID connect session.
	 *
	 * @return the user ID of this open ID connect session
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user uuid of this open ID connect session.
	 *
	 * @return the user uuid of this open ID connect session
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the access token of this open ID connect session.
	 *
	 * @param accessToken the access token of this open ID connect session
	 */
	@Override
	public void setAccessToken(String accessToken) {
		model.setAccessToken(accessToken);
	}

	/**
	 * Sets the access token expiration date of this open ID connect session.
	 *
	 * @param accessTokenExpirationDate the access token expiration date of this open ID connect session
	 */
	@Override
	public void setAccessTokenExpirationDate(Date accessTokenExpirationDate) {
		model.setAccessTokenExpirationDate(accessTokenExpirationDate);
	}

	/**
	 * Sets the auth server well known uri of this open ID connect session.
	 *
	 * @param authServerWellKnownURI the auth server well known uri of this open ID connect session
	 */
	@Override
	public void setAuthServerWellKnownURI(String authServerWellKnownURI) {
		model.setAuthServerWellKnownURI(authServerWellKnownURI);
	}

	/**
	 * Sets the client ID of this open ID connect session.
	 *
	 * @param clientId the client ID of this open ID connect session
	 */
	@Override
	public void setClientId(String clientId) {
		model.setClientId(clientId);
	}

	/**
	 * Sets the company ID of this open ID connect session.
	 *
	 * @param companyId the company ID of this open ID connect session
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the id token of this open ID connect session.
	 *
	 * @param idToken the id token of this open ID connect session
	 */
	@Override
	public void setIdToken(String idToken) {
		model.setIdToken(idToken);
	}

	/**
	 * Sets the issuer of this open ID connect session.
	 *
	 * @param issuer the issuer of this open ID connect session
	 */
	@Override
	public void setIssuer(String issuer) {
		model.setIssuer(issuer);
	}

	/**
	 * Sets the modified date of this open ID connect session.
	 *
	 * @param modifiedDate the modified date of this open ID connect session
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this open ID connect session.
	 *
	 * @param mvccVersion the mvcc version of this open ID connect session
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the open ID connect session ID of this open ID connect session.
	 *
	 * @param openIdConnectSessionId the open ID connect session ID of this open ID connect session
	 */
	@Override
	public void setOpenIdConnectSessionId(long openIdConnectSessionId) {
		model.setOpenIdConnectSessionId(openIdConnectSessionId);
	}

	/**
	 * Sets the primary key of this open ID connect session.
	 *
	 * @param primaryKey the primary key of this open ID connect session
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the refresh token of this open ID connect session.
	 *
	 * @param refreshToken the refresh token of this open ID connect session
	 */
	@Override
	public void setRefreshToken(String refreshToken) {
		model.setRefreshToken(refreshToken);
	}

	/**
	 * Sets the session ID of this open ID connect session.
	 *
	 * @param sessionId the session ID of this open ID connect session
	 */
	@Override
	public void setSessionId(String sessionId) {
		model.setSessionId(sessionId);
	}

	/**
	 * Sets the user ID of this open ID connect session.
	 *
	 * @param userId the user ID of this open ID connect session
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user uuid of this open ID connect session.
	 *
	 * @param userUuid the user uuid of this open ID connect session
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	protected OpenIdConnectSessionWrapper wrap(
		OpenIdConnectSession openIdConnectSession) {

		return new OpenIdConnectSessionWrapper(openIdConnectSession);
	}

}