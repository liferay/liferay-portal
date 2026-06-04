/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.endpoint.access.token.grant.handler;

import com.liferay.oauth2.provider.constants.OAuth2ProviderActionKeys;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.rest.internal.endpoint.constants.OAuth2ProviderRESTEndpointConstants;
import com.liferay.oauth2.provider.rest.internal.endpoint.util.OAuth2ErrorUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.ServerAccessToken;
import org.apache.cxf.rs.security.oauth2.provider.AccessTokenGrantHandler;
import org.apache.cxf.rs.security.oauth2.provider.OAuthServiceException;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
public abstract class BaseAccessTokenGrantHandler
	implements AccessTokenGrantHandler {

	@Override
	public ServerAccessToken createAccessToken(
			Client client, MultivaluedMap<String, String> params)
		throws OAuthServiceException {

		if (!isGrantHandlerEnabled()) {
			throw new OAuthServiceException("Grant handler is not enabled");
		}

		if (!hasPermission(client, params)) {
			throw new OAuthServiceException(
				"User does not have permission to create token");
		}

		return doCreateAccessToken(client, params);
	}

	protected boolean clientsMatch(Client client1, Client client2) {
		if (!Objects.equals(client1.getClientId(), client2.getClientId())) {
			return false;
		}

		String companyId1 = MapUtil.getString(
			client1.getProperties(),
			OAuth2ProviderRESTEndpointConstants.PROPERTY_KEY_COMPANY_ID);
		String companyId2 = MapUtil.getString(
			client2.getProperties(),
			OAuth2ProviderRESTEndpointConstants.PROPERTY_KEY_COMPANY_ID);

		return Objects.equals(companyId1, companyId2);
	}

	protected ServerAccessToken createAccessTokenWithResources(
		Client client, MultivaluedMap<String, String> params,
		Supplier<ServerAccessToken> supplier) {

		List<String> resources = params.get("resource");

		if (ListUtil.isEmpty(resources)) {
			return supplier.get();
		}

		for (String resource : resources) {
			_validateResource(resource);
		}

		List<String> registeredAudiences = client.getRegisteredAudiences();

		if (ListUtil.isNotEmpty(registeredAudiences)) {
			for (String resource : resources) {
				if (!registeredAudiences.contains(resource)) {
					OAuth2ErrorUtil.reportInvalidRequestError(
						"The resource parameter is not a registered audience",
						"invalid_target", Response.Status.BAD_REQUEST);
				}
			}
		}

		try {
			client.setRegisteredAudiences(new ArrayList<>(resources));

			return supplier.get();
		}
		finally {
			client.setRegisteredAudiences(registeredAudiences);
		}
	}

	protected abstract ServerAccessToken doCreateAccessToken(
		Client client, MultivaluedMap<String, String> params);

	protected boolean hasCreateTokenPermission(
		long userId, OAuth2Application oAuth2Application) {

		PermissionChecker permissionChecker;

		try {
			User user = userLocalService.getUserById(userId);

			permissionChecker = PermissionCheckerFactoryUtil.create(user);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to create permission checker for user " + userId,
					exception);
			}

			return false;
		}

		try {
			if (modelResourcePermission.contains(
					permissionChecker, oAuth2Application,
					OAuth2ProviderActionKeys.CREATE_TOKEN)) {

				return true;
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to check permissions for application " +
						oAuth2Application,
					portalException);
			}
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"User ", userId,
					" does not have permission to create access token for ",
					"client ", oAuth2Application.getClientId()));
		}

		return false;
	}

	protected abstract boolean hasPermission(
		Client client, MultivaluedMap<String, String> params);

	protected abstract boolean isGrantHandlerEnabled();

	@Reference(
		target = "(model.class.name=com.liferay.oauth2.provider.model.OAuth2Application)"
	)
	protected ModelResourcePermission<OAuth2Application>
		modelResourcePermission;

	@Reference
	protected UserLocalService userLocalService;

	private void _validateResource(String resource) {
		if (!Validator.isBlank(resource)) {
			try {
				URI uri = new URI(resource);

				if (uri.isAbsolute() && (uri.getFragment() == null)) {
					return;
				}
			}
			catch (URISyntaxException uriSyntaxException) {
				if (_log.isDebugEnabled()) {
					_log.debug(uriSyntaxException);
				}
			}
		}

		OAuth2ErrorUtil.reportInvalidRequestError(
			"The resource parameter must be an absolute URI without a fragment",
			"invalid_target", Response.Status.BAD_REQUEST);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseAccessTokenGrantHandler.class);

}