/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service.impl;

import com.liferay.oauth.client.persistence.exception.DuplicateOAuthClientEntryException;
import com.liferay.oauth.client.persistence.exception.OAuthClientEntryAuthRequestParametersJSONException;
import com.liferay.oauth.client.persistence.exception.OAuthClientEntryAuthServerWellKnownURIException;
import com.liferay.oauth.client.persistence.exception.OAuthClientEntryInfoJSONException;
import com.liferay.oauth.client.persistence.exception.OAuthClientEntryOIDCUserInfoMapperJSONException;
import com.liferay.oauth.client.persistence.exception.OAuthClientEntryTokenRequestParametersJSONException;
import com.liferay.oauth.client.persistence.model.OAuthClientEntry;
import com.liferay.oauth.client.persistence.model.OAuthClientEntryTable;
import com.liferay.oauth.client.persistence.service.OAuthClientASLocalMetadataLocalService;
import com.liferay.oauth.client.persistence.service.base.OAuthClientEntryLocalServiceBaseImpl;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.client.ClientInformation;
import com.nimbusds.oauth2.sdk.client.ClientMetadata;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;

import java.net.URI;
import java.net.URL;

import java.util.List;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Arthur Chan
 */
@Component(
	property = "model.class.name=com.liferay.oauth.client.persistence.model.OAuthClientEntry",
	service = AopService.class
)
public class OAuthClientEntryLocalServiceImpl
	extends OAuthClientEntryLocalServiceBaseImpl {

	@Override
	public OAuthClientEntry addOAuthClientEntry(
			long userId, String authRequestParametersJSON,
			String authServerWellKnownURI, String customClaimsJSON,
			String infoJSON, long metadataCacheTime,
			String oidcUserInfoMapperJSON, String tokenRequestParametersJSON)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		_validateAuthServerWellKnownURI(authServerWellKnownURI);

		ClientInformation clientInformation = _parseClientInformation(
			authServerWellKnownURI, infoJSON);

		ClientMetadata clientMetadata = clientInformation.getMetadata();

		clientMetadata.applyDefaults();

		String clientId = String.valueOf(clientInformation.getID());

		_validateClientId(
			0, user.getCompanyId(), authServerWellKnownURI, clientId);

		if (Validator.isNull(authRequestParametersJSON)) {
			authRequestParametersJSON = "{}";
		}
		else {
			_validateAuthRequestParametersJSON(authRequestParametersJSON);
		}

		if (customClaimsJSON == null) {
			customClaimsJSON = "{}";
		}

		if (Validator.isNull(tokenRequestParametersJSON)) {
			tokenRequestParametersJSON = "{}";
		}
		else {
			_validateTokenRequestParametersJSON(tokenRequestParametersJSON);
		}

		if (authServerWellKnownURI.contains("openid-configuration")) {
			_validateOIDCUserInfoMapperJSON(oidcUserInfoMapperJSON);
		}
		else {
			oidcUserInfoMapperJSON = "{}";
		}

		JSONObject clientInformationJSONObject =
			clientInformation.toJSONObject();

		OAuthClientEntry oAuthClientEntry = oAuthClientEntryPersistence.create(
			counterLocalService.increment());

		oAuthClientEntry.setCompanyId(user.getCompanyId());
		oAuthClientEntry.setUserId(user.getUserId());
		oAuthClientEntry.setUserName(user.getFullName());
		oAuthClientEntry.setAuthRequestParametersJSON(
			authRequestParametersJSON);
		oAuthClientEntry.setAuthServerWellKnownURI(authServerWellKnownURI);
		oAuthClientEntry.setClientId(clientId);
		oAuthClientEntry.setCustomClaimsJSON(customClaimsJSON);
		oAuthClientEntry.setInfoJSON(clientInformationJSONObject.toString());
		oAuthClientEntry.setMetadataCacheTime(metadataCacheTime);
		oAuthClientEntry.setOIDCUserInfoMapperJSON(oidcUserInfoMapperJSON);
		oAuthClientEntry.setTokenRequestParametersJSON(
			tokenRequestParametersJSON);

		oAuthClientEntry = oAuthClientEntryPersistence.update(oAuthClientEntry);

		_resourceLocalService.addResources(
			oAuthClientEntry.getCompanyId(),
			GroupConstants.DEFAULT_LIVE_GROUP_ID, oAuthClientEntry.getUserId(),
			OAuthClientEntry.class.getName(),
			oAuthClientEntry.getOAuthClientEntryId(), false, false, false);

		return oAuthClientEntry;
	}

	@Override
	public OAuthClientEntry deleteOAuthClientEntry(long oAuthClientEntryId)
		throws PortalException {

		OAuthClientEntry oAuthClientEntry =
			oAuthClientEntryPersistence.findByPrimaryKey(oAuthClientEntryId);

		return deleteOAuthClientEntry(oAuthClientEntry);
	}

	@Override
	public OAuthClientEntry deleteOAuthClientEntry(
			long companyId, String authServerWellKnownURI, String clientId)
		throws PortalException {

		OAuthClientEntry oAuthClientEntry =
			oAuthClientEntryPersistence.findByC_A_C(
				companyId, authServerWellKnownURI, clientId);

		return deleteOAuthClientEntry(oAuthClientEntry);
	}

	@Override
	public OAuthClientEntry deleteOAuthClientEntry(
			OAuthClientEntry oAuthClientEntry)
		throws PortalException {

		oAuthClientEntry = oAuthClientEntryPersistence.remove(oAuthClientEntry);

		_resourceLocalService.deleteResource(
			oAuthClientEntry.getCompanyId(), OAuthClientEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			oAuthClientEntry.getOAuthClientEntryId());

		return oAuthClientEntry;
	}

	@Override
	public OAuthClientEntry fetchOAuthClientEntry(
		long companyId, String authServerWellKnownURI, String clientId) {

		return oAuthClientEntryPersistence.fetchByC_A_C(
			companyId, authServerWellKnownURI, clientId);
	}

	@Override
	public List<OAuthClientEntry>
		getAuthServerWellKnownURISuffixOAuthClientEntries(
			long companyId, String authServerWellKnownURISuffix) {

		return oAuthClientEntryPersistence.dslQuery(
			DSLQueryFactoryUtil.select(
				OAuthClientEntryTable.INSTANCE
			).from(
				OAuthClientEntryTable.INSTANCE
			).where(
				OAuthClientEntryTable.INSTANCE.companyId.eq(
					companyId
				).and(
					OAuthClientEntryTable.INSTANCE.authServerWellKnownURI.like(
						StringUtil.quote(authServerWellKnownURISuffix, '%'))
				)
			));
	}

	@Override
	public List<OAuthClientEntry> getCompanyOAuthClientEntries(long companyId) {
		return oAuthClientEntryPersistence.findByCompanyId(companyId);
	}

	@Override
	public OAuthClientEntry getOAuthClientEntry(
			long companyId, String authServerWellKnownURI, String clientId)
		throws PortalException {

		return oAuthClientEntryPersistence.findByC_A_C(
			companyId, authServerWellKnownURI, clientId);
	}

	@Override
	public List<OAuthClientEntry> getUserOAuthClientEntries(long userId) {
		return oAuthClientEntryPersistence.findByUserId(userId);
	}

	@Override
	public OAuthClientEntry updateOAuthClientEntry(
			long oAuthClientEntryId, String authRequestParametersJSON,
			String authServerWellKnownURI, String customClaimsJSON,
			String infoJSON, long metadataCacheTime,
			String oidcUserInfoMapperJSON, String tokenRequestParametersJSON)
		throws PortalException {

		OAuthClientEntry oAuthClientEntry =
			oAuthClientEntryLocalService.getOAuthClientEntry(
				oAuthClientEntryId);

		_validateAuthServerWellKnownURI(authServerWellKnownURI);

		ClientInformation clientInformation = _parseClientInformation(
			authServerWellKnownURI, infoJSON);

		ClientMetadata clientMetadata = clientInformation.getMetadata();

		clientMetadata.applyDefaults();

		String clientId = String.valueOf(clientInformation.getID());

		_validateClientId(
			oAuthClientEntryId, oAuthClientEntry.getCompanyId(),
			authServerWellKnownURI, clientId);

		if (Validator.isNull(authRequestParametersJSON)) {
			authRequestParametersJSON = "{}";
		}
		else {
			_validateAuthRequestParametersJSON(authRequestParametersJSON);
		}

		if (customClaimsJSON != null) {
			oAuthClientEntry.setCustomClaimsJSON(customClaimsJSON);
		}

		if (Validator.isNull(tokenRequestParametersJSON)) {
			tokenRequestParametersJSON = "{}";
		}
		else {
			_validateTokenRequestParametersJSON(tokenRequestParametersJSON);
		}

		if (authServerWellKnownURI.contains("openid-configuration")) {
			_validateOIDCUserInfoMapperJSON(oidcUserInfoMapperJSON);
		}
		else {
			oidcUserInfoMapperJSON = "{}";
		}

		JSONObject clientInformationJSONObject =
			clientInformation.toJSONObject();

		oAuthClientEntry.setAuthRequestParametersJSON(
			authRequestParametersJSON);
		oAuthClientEntry.setAuthServerWellKnownURI(authServerWellKnownURI);
		oAuthClientEntry.setClientId(clientId);
		oAuthClientEntry.setInfoJSON(clientInformationJSONObject.toString());
		oAuthClientEntry.setMetadataCacheTime(metadataCacheTime);
		oAuthClientEntry.setOIDCUserInfoMapperJSON(oidcUserInfoMapperJSON);
		oAuthClientEntry.setTokenRequestParametersJSON(
			tokenRequestParametersJSON);

		return oAuthClientEntryPersistence.update(oAuthClientEntry);
	}

	private ClientInformation _parseClientInformation(
			String authServerWellKnownURI, String infoJSON)
		throws PortalException {

		try {
			if (authServerWellKnownURI.contains("openid-configuration")) {
				return OIDCClientInformation.parse(
					JSONObjectUtils.parse(infoJSON));
			}

			return ClientInformation.parse(JSONObjectUtils.parse(infoJSON));
		}
		catch (Exception exception) {
			throw new OAuthClientEntryInfoJSONException(
				exception.getMessage(), exception);
		}
	}

	private void _validateAuthRequestParametersJSON(
			String authRequestParametersJSON)
		throws PortalException {

		try {
			_validateRequestParametersJSON(authRequestParametersJSON);
		}
		catch (Exception exception) {
			throw new OAuthClientEntryAuthRequestParametersJSONException(
				exception.getMessage(), exception);
		}
	}

	private void _validateAuthServerWellKnownURI(String authServerWellKnownURI)
		throws PortalException {

		try {
			if (authServerWellKnownURI.endsWith("local")) {
				_oAuthClientASLocalMetadataLocalService.
					getOAuthClientASLocalMetadata(authServerWellKnownURI);

				return;
			}

			HTTPRequest httpRequest = new HTTPRequest(
				HTTPRequest.Method.GET, new URL(authServerWellKnownURI));

			HTTPResponse httpResponse = httpRequest.send();

			if (httpResponse.getStatusCode() != HTTPResponse.SC_OK) {
				throw new OAuthClientEntryAuthServerWellKnownURIException(
					httpResponse.getStatusMessage());
			}
		}
		catch (Exception exception) {
			throw new OAuthClientEntryAuthServerWellKnownURIException(
				exception);
		}
	}

	private void _validateClientId(
			long oAuthClientEntryId, long companyId,
			String authServerWellKnownURI, String clientId)
		throws PortalException {

		OAuthClientEntry oAuthClientEntry = null;

		if (oAuthClientEntryId > 0) {
			oAuthClientEntry = oAuthClientEntryPersistence.findByPrimaryKey(
				oAuthClientEntryId);

			if (authServerWellKnownURI.equals(
					oAuthClientEntry.getAuthServerWellKnownURI()) &&
				clientId.equals(oAuthClientEntry.getClientId())) {

				return;
			}
		}

		oAuthClientEntry = oAuthClientEntryPersistence.fetchByC_A_C(
			companyId, authServerWellKnownURI, clientId);

		if (oAuthClientEntry != null) {
			throw new DuplicateOAuthClientEntryException(
				"Client ID " + clientId);
		}
	}

	private void _validateCustomRequestParameters(
			JSONObject requestParametersJSONObject)
		throws Exception {

		if (requestParametersJSONObject.containsKey(
				"custom_request_parameters")) {

			JSONObject customRequestParametersJSONObject =
				JSONObjectUtils.getJSONObject(
					requestParametersJSONObject, "custom_request_parameters");

			for (String key : customRequestParametersJSONObject.keySet()) {
				JSONArray valueJSONArray = JSONObjectUtils.getJSONArray(
					customRequestParametersJSONObject, key);

				for (Object value : valueJSONArray) {
					if (!(value instanceof String)) {
						throw new ParseException("Value is not a string");
					}
				}
			}
		}
	}

	private void _validateOIDCUserInfoMapperJSON(String oidcUserInfoMapperJSON)
		throws PortalException {

		try {
			JSONObject oidcUserInfoMapperJSONObject = JSONObjectUtils.parse(
				oidcUserInfoMapperJSON);

			_validateOIDCUserInfoMapperJSON(
				"user", oidcUserInfoMapperJSONObject,
				new String[] {"emailAddress", "firstName", "lastName"});

			if (oidcUserInfoMapperJSONObject.containsKey("address")) {
				_validateOIDCUserInfoMapperJSON(
					"address", oidcUserInfoMapperJSONObject,
					new String[] {"city", "street", "zip"});
			}

			if (oidcUserInfoMapperJSONObject.containsKey("contact")) {
				_validateOIDCUserInfoMapperJSON(
					"contact", oidcUserInfoMapperJSONObject, new String[0]);
			}

			if (oidcUserInfoMapperJSONObject.containsKey("phone")) {
				_validateOIDCUserInfoMapperJSON(
					"phone", oidcUserInfoMapperJSONObject,
					new String[] {"phone"});
			}
		}
		catch (Exception exception) {
			throw new OAuthClientEntryOIDCUserInfoMapperJSONException(
				exception.getMessage(), exception);
		}
	}

	private void _validateOIDCUserInfoMapperJSON(
			String key, JSONObject oidcUserInfoMapperJSONObject,
			String[] requiredKeys)
		throws Exception {

		JSONObject jsonObject = JSONObjectUtils.getJSONObject(
			oidcUserInfoMapperJSONObject, key);

		for (String requiredKey : requiredKeys) {
			if (Validator.isNull(jsonObject.getAsString(requiredKey))) {
				throw new OAuthClientEntryOIDCUserInfoMapperJSONException(
					requiredKey + " is required for " + key);
			}
		}

		for (Object object : jsonObject.values()) {
			if (!(object instanceof String)) {
				throw new OAuthClientEntryOIDCUserInfoMapperJSONException(
					"Value is not a string");
			}
		}
	}

	private void _validateRequestParametersJSON(String requestParametersJSON)
		throws Exception {

		JSONObject requestParametersJSONObject = JSONObjectUtils.parse(
			requestParametersJSON);

		_validateSpecsRequestParameters(requestParametersJSONObject);

		_validateCustomRequestParameters(requestParametersJSONObject);
	}

	private void _validateSpecsRequestParameters(
			JSONObject requestParametersJSONObject)
		throws Exception {

		if (requestParametersJSONObject.containsKey("redirect_uri")) {
			URI.create(
				JSONObjectUtils.getString(
					requestParametersJSONObject, "redirect_uri"));
		}

		if (requestParametersJSONObject.containsKey("resource")) {
			for (Object uriObject :
					JSONObjectUtils.getJSONArray(
						requestParametersJSONObject, "resource")) {

				if (!(uriObject instanceof String)) {
					throw new ParseException(
						"Resource must be a JSON array of Strings");
				}

				URI.create((String)uriObject);
			}
		}

		if (requestParametersJSONObject.containsKey("response_type")) {
			ResponseType.parse(
				JSONObjectUtils.getString(
					requestParametersJSONObject, "response_type"));
		}

		if (requestParametersJSONObject.containsKey("scope")) {
			Scope.parse(
				JSONObjectUtils.getString(
					requestParametersJSONObject, "scope"));
		}
	}

	private void _validateTokenRequestParametersJSON(
			String tokenRequestParametersJSON)
		throws PortalException {

		try {
			_validateRequestParametersJSON(tokenRequestParametersJSON);
		}
		catch (Exception exception) {
			throw new OAuthClientEntryTokenRequestParametersJSONException(
				exception.getMessage(), exception);
		}
	}

	@Reference
	private OAuthClientASLocalMetadataLocalService
		_oAuthClientASLocalMetadataLocalService;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private UserLocalService _userLocalService;

}