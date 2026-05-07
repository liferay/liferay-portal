/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service.impl;

import com.liferay.oauth.client.persistence.exception.DuplicateOAuthClientPRLocalMetadataException;
import com.liferay.oauth.client.persistence.exception.OAuthClientPRLocalMetadataLocalWellKnownURIException;
import com.liferay.oauth.client.persistence.exception.OAuthClientPRLocalMetadataMetadataJSONException;
import com.liferay.oauth.client.persistence.exception.OAuthClientPRLocalMetadataResourceException;
import com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata;
import com.liferay.oauth.client.persistence.service.base.OAuthClientPRLocalMetadataLocalServiceBaseImpl;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge García Jiménez
 */
@Component(
	property = "model.class.name=com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata",
	service = AopService.class
)
public class OAuthClientPRLocalMetadataLocalServiceImpl
	extends OAuthClientPRLocalMetadataLocalServiceBaseImpl {

	@Override
	public OAuthClientPRLocalMetadata addOAuthClientPRLocalMetadata(
			long userId, String metadataJSON)
		throws PortalException {

		JSONObject metadataJSONObject = _parseMetadataJSON(metadataJSON);

		return addOAuthClientPRLocalMetadata(
			null, userId,
			_toStringArray(
				metadataJSONObject.getJSONArray("authorization_servers")),
			_toStringArray(
				metadataJSONObject.getJSONArray("bearer_methods_supported")),
			false, metadataJSONObject.getString("resource"),
			metadataJSONObject.getString("resource_name"),
			_toStringArray(
				metadataJSONObject.getJSONArray("scopes_supported")));
	}

	public OAuthClientPRLocalMetadata addOAuthClientPRLocalMetadata(
			String externalReferenceCode, long userId,
			String[] authorizationServers, String[] bearerMethodsSupported,
			boolean localWellKnownEnabled, String resource, String resourceName,
			String[] scopesSupported)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		String localWellKnownURI = _generateLocalWellKnownURI(resource);

		_validate(
			null, user.getCompanyId(), authorizationServers, localWellKnownURI,
			resource);

		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata =
			oAuthClientPRLocalMetadataPersistence.create(
				counterLocalService.increment());

		oAuthClientPRLocalMetadata.setExternalReferenceCode(
			externalReferenceCode);
		oAuthClientPRLocalMetadata.setCompanyId(user.getCompanyId());
		oAuthClientPRLocalMetadata.setUserId(user.getUserId());
		oAuthClientPRLocalMetadata.setUserName(user.getFullName());
		oAuthClientPRLocalMetadata.setLocalWellKnownEnabled(
			localWellKnownEnabled);
		oAuthClientPRLocalMetadata.setLocalWellKnownURI(localWellKnownURI);
		oAuthClientPRLocalMetadata.setMetadataJSON(
			_generateMetadataJSON(
				authorizationServers, bearerMethodsSupported, resource,
				resourceName, scopesSupported));
		oAuthClientPRLocalMetadata.setResource(resource);

		oAuthClientPRLocalMetadata =
			oAuthClientPRLocalMetadataPersistence.update(
				oAuthClientPRLocalMetadata);

		_resourceLocalService.addResources(
			oAuthClientPRLocalMetadata.getCompanyId(),
			GroupConstants.DEFAULT_LIVE_GROUP_ID,
			oAuthClientPRLocalMetadata.getUserId(),
			OAuthClientPRLocalMetadata.class.getName(),
			oAuthClientPRLocalMetadata.getOAuthClientPRLocalMetadataId(), false,
			false, false);

		return oAuthClientPRLocalMetadata;
	}

	@Override
	public OAuthClientPRLocalMetadata deleteOAuthClientPRLocalMetadata(
			long oAuthClientPRLocalMetadataId)
		throws PortalException {

		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata =
			oAuthClientPRLocalMetadataPersistence.findByPrimaryKey(
				oAuthClientPRLocalMetadataId);

		return deleteOAuthClientPRLocalMetadata(oAuthClientPRLocalMetadata);
	}

	@Override
	public OAuthClientPRLocalMetadata deleteOAuthClientPRLocalMetadata(
			long companyId, String localWellKnownURI)
		throws PortalException {

		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata =
			oAuthClientPRLocalMetadataPersistence.findByC_LWKURI(
				companyId, localWellKnownURI);

		return deleteOAuthClientPRLocalMetadata(oAuthClientPRLocalMetadata);
	}

	@Override
	public OAuthClientPRLocalMetadata deleteOAuthClientPRLocalMetadata(
			OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata)
		throws PortalException {

		oAuthClientPRLocalMetadata =
			oAuthClientPRLocalMetadataPersistence.remove(
				oAuthClientPRLocalMetadata);

		_resourceLocalService.deleteResource(
			oAuthClientPRLocalMetadata.getCompanyId(),
			OAuthClientPRLocalMetadata.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			oAuthClientPRLocalMetadata.getOAuthClientPRLocalMetadataId());

		return oAuthClientPRLocalMetadata;
	}

	@Override
	public OAuthClientPRLocalMetadata fetchOAuthClientPRLocalMetadata(
		long companyId, boolean localWellKnownEnabled,
		OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator) {

		return oAuthClientPRLocalMetadataPersistence.fetchByC_L_First(
			companyId, localWellKnownEnabled, orderByComparator);
	}

	@Override
	public OAuthClientPRLocalMetadata fetchOAuthClientPRLocalMetadata(
		long companyId, String resource) {

		return oAuthClientPRLocalMetadataPersistence.fetchByC_R(
			companyId, resource);
	}

	@Override
	public OAuthClientPRLocalMetadata
		fetchOAuthClientPRLocalMetadataByLocalWellKnownURI(
			long companyId, String localWellKnownURI) {

		return oAuthClientPRLocalMetadataPersistence.fetchByC_LWKURI(
			companyId, localWellKnownURI);
	}

	@Override
	public List<OAuthClientPRLocalMetadata>
		getCompanyOAuthClientPRLocalMetadata(long companyId) {

		return oAuthClientPRLocalMetadataPersistence.findByCompanyId(companyId);
	}

	@Override
	public List<OAuthClientPRLocalMetadata>
		getCompanyOAuthClientPRLocalMetadata(
			long companyId, int start, int end) {

		return oAuthClientPRLocalMetadataPersistence.findByCompanyId(
			companyId, start, end);
	}

	@Override
	public OAuthClientPRLocalMetadata getOAuthClientPRLocalMetadata(
			long companyId, String localWellKnownURI)
		throws PortalException {

		return oAuthClientPRLocalMetadataPersistence.findByC_LWKURI(
			companyId, localWellKnownURI);
	}

	@Override
	public int getOAuthClientPRLocalMetadatasCount(long companyId) {
		return oAuthClientPRLocalMetadataPersistence.countByCompanyId(
			companyId);
	}

	@Override
	public List<OAuthClientPRLocalMetadata> getUserOAuthClientPRLocalMetadata(
		long userId) {

		return oAuthClientPRLocalMetadataPersistence.findByUserId(userId);
	}

	@Override
	public List<OAuthClientPRLocalMetadata> getUserOAuthClientPRLocalMetadata(
		long userId, int start, int end) {

		return oAuthClientPRLocalMetadataPersistence.findByUserId(
			userId, start, end);
	}

	@Override
	public OAuthClientPRLocalMetadata updateOAuthClientPRLocalMetadata(
			long oAuthClientPRLocalMetadataId, String metadataJSON)
		throws PortalException {

		JSONObject metadataJSONObject = _parseMetadataJSON(metadataJSON);

		return updateOAuthClientPRLocalMetadata(
			oAuthClientPRLocalMetadataId,
			_toStringArray(
				metadataJSONObject.getJSONArray("authorization_servers")),
			_toStringArray(
				metadataJSONObject.getJSONArray("bearer_methods_supported")),
			false, metadataJSONObject.getString("resource"),
			metadataJSONObject.getString("resource_name"),
			_toStringArray(
				metadataJSONObject.getJSONArray("scopes_supported")));
	}

	public OAuthClientPRLocalMetadata updateOAuthClientPRLocalMetadata(
			long oAuthClientPRLocalMetadataId, String[] authorizationServers,
			String[] bearerMethodsSupported, boolean localWellKnownEnabled,
			String resource, String resourceName, String[] scopesSupported)
		throws PortalException {

		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata =
			oAuthClientPRLocalMetadataLocalService.
				getOAuthClientPRLocalMetadata(oAuthClientPRLocalMetadataId);

		String localWellKnownURI =
			oAuthClientPRLocalMetadata.getLocalWellKnownURI();

		if (!resource.equals(oAuthClientPRLocalMetadata.getResource())) {
			localWellKnownURI = _generateLocalWellKnownURI(resource);
		}

		_validate(
			oAuthClientPRLocalMetadata,
			oAuthClientPRLocalMetadata.getCompanyId(), authorizationServers,
			localWellKnownURI, resource);

		oAuthClientPRLocalMetadata.setLocalWellKnownEnabled(
			localWellKnownEnabled);
		oAuthClientPRLocalMetadata.setLocalWellKnownURI(localWellKnownURI);
		oAuthClientPRLocalMetadata.setMetadataJSON(
			_generateMetadataJSON(
				authorizationServers, bearerMethodsSupported, resource,
				resourceName, scopesSupported));
		oAuthClientPRLocalMetadata.setResource(resource);

		return oAuthClientPRLocalMetadataPersistence.update(
			oAuthClientPRLocalMetadata);
	}

	private String _generateLocalWellKnownURI(String resource)
		throws PortalException {

		try {
			URI resourceURI = URI.create(resource);

			return StringBundler.concat(
				resourceURI.getScheme(), "://", resourceURI.getAuthority(),
				"/.well-known/oauth-protected-resource", resourceURI.getPath());
		}
		catch (Exception exception) {
			throw new OAuthClientPRLocalMetadataLocalWellKnownURIException(
				exception);
		}
	}

	private String _generateMetadataJSON(
			String[] authorizationServers, String[] bearerMethodsSupported,
			String resource, String resourceName, String[] scopesSupported)
		throws PortalException {

		try {
			JSONObject metadataJSONObject = JSONUtil.put(
				"authorization_servers",
				JSONUtil.putAll((Object[])authorizationServers)
			).put(
				"bearer_methods_supported",
				JSONUtil.putAll((Object[])bearerMethodsSupported)
			).put(
				"resource", resource
			).put(
				"resource_name", resourceName
			);

			if ((scopesSupported != null) && (scopesSupported.length > 0)) {
				metadataJSONObject.put(
					"scopes_supported",
					JSONUtil.putAll((Object[])scopesSupported));
			}

			return metadataJSONObject.toString();
		}
		catch (Exception exception) {
			throw new OAuthClientPRLocalMetadataMetadataJSONException(
				exception.getMessage(), exception);
		}
	}

	private JSONObject _parseMetadataJSON(String metadataJSON)
		throws PortalException {

		try {
			return _jsonFactory.createJSONObject(metadataJSON);
		}
		catch (Exception exception) {
			throw new OAuthClientPRLocalMetadataMetadataJSONException(
				exception.getMessage(), exception);
		}
	}

	private String[] _toStringArray(JSONArray jsonArray) {
		if (jsonArray == null) {
			return new String[0];
		}

		String[] strings = new String[jsonArray.length()];

		for (int i = 0; i < jsonArray.length(); i++) {
			strings[i] = jsonArray.getString(i);
		}

		return strings;
	}

	private void _validate(
			OAuthClientPRLocalMetadata oldOAuthClientPRLocalMetadata,
			long companyId, String[] authorizationServers,
			String localWellKnownURI, String resource)
		throws PortalException {

		if (Validator.isNull(resource)) {
			throw new OAuthClientPRLocalMetadataResourceException();
		}

		_validateURL(resource);

		if (authorizationServers != null) {
			for (String authorizationServer : authorizationServers) {
				_validateURL(authorizationServer);
			}
		}

		if (oldOAuthClientPRLocalMetadata == null) {
			OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata =
				oAuthClientPRLocalMetadataPersistence.fetchByC_LWKURI(
					companyId, localWellKnownURI);

			if (oAuthClientPRLocalMetadata != null) {
				throw new DuplicateOAuthClientPRLocalMetadataException();
			}
		}

		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata =
			oAuthClientPRLocalMetadataPersistence.fetchByC_R(
				companyId, resource);

		if ((oAuthClientPRLocalMetadata != null) &&
			!Objects.equals(
				oldOAuthClientPRLocalMetadata, oAuthClientPRLocalMetadata)) {

			throw new DuplicateOAuthClientPRLocalMetadataException();
		}
	}

	private void _validateURL(String urlString) throws PortalException {
		if (Validator.isNull(urlString)) {
			return;
		}

		try {
			URL url = new URL(urlString);

			if (!Http.HTTPS.equalsIgnoreCase(url.getProtocol())) {
				throw new OAuthClientPRLocalMetadataResourceException(
					urlString);
			}
		}
		catch (MalformedURLException malformedURLException) {
			throw new OAuthClientPRLocalMetadataResourceException(
				urlString, malformedURLException);
		}
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private UserLocalService _userLocalService;

}