/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service.impl;

import com.liferay.oauth.client.persistence.exception.DuplicateOAuthClientPRLocalMetadataException;
import com.liferay.oauth.client.persistence.exception.OAuthClientPRLocalMetadataLocalWellKnownURIException;
import com.liferay.oauth.client.persistence.exception.OAuthClientPRLocalMetadataMetadataJSONException;
import com.liferay.oauth.client.persistence.exception.OAuthClientPRLocalMetadataProtectedResourceURIException;
import com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata;
import com.liferay.oauth.client.persistence.service.base.OAuthClientPRLocalMetadataLocalServiceBaseImpl;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;

import java.net.URI;
import java.net.URISyntaxException;

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

		JSONObject metadataJSONObject = _toMetadataJSONObject(metadataJSON);

		return addOAuthClientPRLocalMetadata(
			null, userId,
			JSONUtil.toStringArray(
				metadataJSONObject.getJSONArray("authorization_servers")),
			JSONUtil.toStringArray(
				metadataJSONObject.getJSONArray("bearer_methods_supported")),
			false, metadataJSONObject.getString("resource"),
			metadataJSONObject.getString("resource_name"),
			JSONUtil.toStringArray(
				metadataJSONObject.getJSONArray("scopes_supported")));
	}

	@Override
	public OAuthClientPRLocalMetadata addOAuthClientPRLocalMetadata(
			String externalReferenceCode, long userId,
			String[] authorizationServers, String[] bearerMethodsSupported,
			boolean localWellKnownEnabled, String protectedResourceURI,
			String resourceName, String[] scopesSupported)
		throws PortalException {

		if (Validator.isNull(protectedResourceURI)) {
			throw new OAuthClientPRLocalMetadataProtectedResourceURIException();
		}

		if (!_isValidURL(protectedResourceURI)) {
			throw new OAuthClientPRLocalMetadataProtectedResourceURIException(
				protectedResourceURI);
		}

		protectedResourceURI = _removeTrailingSlash(protectedResourceURI);

		User user = _userLocalService.getUser(userId);

		String localWellKnownURI = _generateLocalWellKnownURI(
			protectedResourceURI);

		_validate(
			null, user.getCompanyId(), authorizationServers,
			bearerMethodsSupported, localWellKnownURI, protectedResourceURI);

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
				authorizationServers, bearerMethodsSupported,
				protectedResourceURI, resourceName, scopesSupported));
		oAuthClientPRLocalMetadata.setProtectedResourceURI(
			protectedResourceURI);

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
		long companyId, String protectedResourceURI) {

		return oAuthClientPRLocalMetadataPersistence.fetchByC_PRURI(
			companyId, protectedResourceURI);
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

		JSONObject metadataJSONObject = _toMetadataJSONObject(metadataJSON);

		return updateOAuthClientPRLocalMetadata(
			oAuthClientPRLocalMetadataId,
			JSONUtil.toStringArray(
				metadataJSONObject.getJSONArray("authorization_servers")),
			JSONUtil.toStringArray(
				metadataJSONObject.getJSONArray("bearer_methods_supported")),
			false, metadataJSONObject.getString("resource"),
			metadataJSONObject.getString("resource_name"),
			JSONUtil.toStringArray(
				metadataJSONObject.getJSONArray("scopes_supported")));
	}

	@Override
	public OAuthClientPRLocalMetadata updateOAuthClientPRLocalMetadata(
			long oAuthClientPRLocalMetadataId, String[] authorizationServers,
			String[] bearerMethodsSupported, boolean localWellKnownEnabled,
			String protectedResourceURI, String resourceName,
			String[] scopesSupported)
		throws PortalException {

		if (Validator.isNull(protectedResourceURI)) {
			throw new OAuthClientPRLocalMetadataProtectedResourceURIException();
		}

		if (!_isValidURL(protectedResourceURI)) {
			throw new OAuthClientPRLocalMetadataProtectedResourceURIException(
				protectedResourceURI);
		}

		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata =
			oAuthClientPRLocalMetadataLocalService.
				getOAuthClientPRLocalMetadata(oAuthClientPRLocalMetadataId);

		String localWellKnownURI =
			oAuthClientPRLocalMetadata.getLocalWellKnownURI();

		protectedResourceURI = _removeTrailingSlash(protectedResourceURI);

		if (!protectedResourceURI.equals(
				oAuthClientPRLocalMetadata.getProtectedResourceURI())) {

			localWellKnownURI = _generateLocalWellKnownURI(
				protectedResourceURI);
		}

		_validate(
			oAuthClientPRLocalMetadata,
			oAuthClientPRLocalMetadata.getCompanyId(), authorizationServers,
			bearerMethodsSupported, localWellKnownURI, protectedResourceURI);

		oAuthClientPRLocalMetadata.setLocalWellKnownEnabled(
			localWellKnownEnabled);
		oAuthClientPRLocalMetadata.setLocalWellKnownURI(localWellKnownURI);
		oAuthClientPRLocalMetadata.setMetadataJSON(
			_generateMetadataJSON(
				authorizationServers, bearerMethodsSupported,
				protectedResourceURI, resourceName, scopesSupported));
		oAuthClientPRLocalMetadata.setProtectedResourceURI(
			protectedResourceURI);

		return oAuthClientPRLocalMetadataPersistence.update(
			oAuthClientPRLocalMetadata);
	}

	private String _generateLocalWellKnownURI(String protectedResourceURI)
		throws PortalException {

		try {
			URI uri = URI.create(protectedResourceURI);

			if (Validator.isNotNull(uri.getRawQuery())) {
				return StringBundler.concat(
					uri.getScheme(), "://", uri.getAuthority(),
					"/.well-known/oauth-protected-resource", uri.getPath(),
					StringPool.QUESTION, uri.getRawQuery());
			}

			return StringBundler.concat(
				uri.getScheme(), "://", uri.getAuthority(),
				"/.well-known/oauth-protected-resource", uri.getPath());
		}
		catch (Exception exception) {
			throw new OAuthClientPRLocalMetadataLocalWellKnownURIException(
				exception);
		}
	}

	private String _generateMetadataJSON(
			String[] authorizationServers, String[] bearerMethodsSupported,
			String protectedResourceURI, String resourceName,
			String[] scopesSupported)
		throws PortalException {

		try {
			return JSONUtil.put(
				"authorization_servers",
				JSONUtil.putAll((Object[])authorizationServers)
			).put(
				"bearer_methods_supported",
				JSONUtil.putAll((Object[])bearerMethodsSupported)
			).put(
				"resource", protectedResourceURI
			).put(
				"resource_name", resourceName
			).put(
				"scopes_supported",
				() -> {
					if (ArrayUtil.isEmpty(scopesSupported)) {
						return null;
					}

					return JSONUtil.putAll((Object[])scopesSupported);
				}
			).toString();
		}
		catch (Exception exception) {
			throw new OAuthClientPRLocalMetadataMetadataJSONException(
				exception.getMessage(), exception);
		}
	}

	private boolean _isValidURL(String urlString) {
		if (Validator.isNull(urlString)) {
			return true;
		}

		try {
			URI uri = new URI(urlString);

			String scheme = uri.getScheme();

			if (!Http.HTTP.equalsIgnoreCase(scheme) &&
				!Http.HTTPS.equalsIgnoreCase(scheme)) {

				return false;
			}

			String host = uri.getHost();

			if (Validator.isNull(host)) {
				return false;
			}

			if (Validator.isNotNull(uri.getFragment()) ||
				(!Http.HTTPS.equalsIgnoreCase(scheme) &&
				 !Objects.equals(host, "127.0.0.1") &&
				 !Objects.equals(host, "[::1]") &&
				 !Objects.equals(host, "localhost"))) {

				return false;
			}
		}
		catch (URISyntaxException uriSyntaxException) {
			if (_log.isDebugEnabled()) {
				_log.debug(uriSyntaxException);
			}

			return false;
		}

		return true;
	}

	private String _removeTrailingSlash(String urlString) {
		if ((urlString == null) || !urlString.endsWith(StringPool.SLASH)) {
			return urlString;
		}

		return urlString.substring(0, urlString.length() - 1);
	}

	private JSONObject _toMetadataJSONObject(String metadataJSON)
		throws PortalException {

		try {
			return _jsonFactory.createJSONObject(metadataJSON);
		}
		catch (Exception exception) {
			throw new OAuthClientPRLocalMetadataMetadataJSONException(
				exception.getMessage(), exception);
		}
	}

	private void _validate(
			OAuthClientPRLocalMetadata oldOAuthClientPRLocalMetadata,
			long companyId, String[] authorizationServers,
			String[] bearerMethodsSupported, String localWellKnownURI,
			String protectedResourceURI)
		throws PortalException {

		if (ArrayUtil.isEmpty(authorizationServers)) {
			throw new OAuthClientPRLocalMetadataMetadataJSONException(
				"\"authorization_servers\" is required");
		}

		for (String authorizationServer : authorizationServers) {
			if (!_isValidURL(authorizationServer)) {
				throw new OAuthClientPRLocalMetadataMetadataJSONException(
					authorizationServer);
			}
		}

		if (ArrayUtil.isEmpty(bearerMethodsSupported)) {
			throw new OAuthClientPRLocalMetadataMetadataJSONException(
				"\"bearer_methods_supported\" is required");
		}

		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata =
			oAuthClientPRLocalMetadataPersistence.fetchByC_LWKURI(
				companyId, localWellKnownURI);

		if ((oAuthClientPRLocalMetadata != null) &&
			!Objects.equals(
				oldOAuthClientPRLocalMetadata, oAuthClientPRLocalMetadata)) {

			throw new DuplicateOAuthClientPRLocalMetadataException();
		}

		if (Validator.isNull(protectedResourceURI)) {
			throw new OAuthClientPRLocalMetadataProtectedResourceURIException();
		}

		oAuthClientPRLocalMetadata =
			oAuthClientPRLocalMetadataPersistence.fetchByC_PRURI(
				companyId, protectedResourceURI);

		if ((oAuthClientPRLocalMetadata != null) &&
			!Objects.equals(
				oldOAuthClientPRLocalMetadata, oAuthClientPRLocalMetadata)) {

			throw new DuplicateOAuthClientPRLocalMetadataException();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OAuthClientPRLocalMetadataLocalServiceImpl.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private UserLocalService _userLocalService;

}