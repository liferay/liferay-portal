/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharepoint.rest.repository.internal.document.library.repository.authorization.capability;

import com.liferay.document.library.repository.authorization.capability.AuthorizationCapability;
import com.liferay.document.library.repository.authorization.capability.AuthorizationException;
import com.liferay.document.library.repository.authorization.oauth2.OAuth2AuthorizationException;
import com.liferay.document.library.repository.authorization.oauth2.Token;
import com.liferay.document.library.repository.authorization.oauth2.TokenStore;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.sharepoint.rest.repository.internal.configuration.SharepointRepositoryConfiguration;
import com.liferay.sharepoint.rest.repository.internal.document.library.repository.authorization.oauth2.SharepointRepositoryRequestState;
import com.liferay.sharepoint.rest.repository.internal.document.library.repository.authorization.oauth2.SharepointRepositoryTokenBroker;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author Adolfo Pérez
 */
public class SharepointRepositoryAuthorizationCapability
	implements AuthorizationCapability {

	public SharepointRepositoryAuthorizationCapability(
		TokenStore tokenStore,
		SharepointRepositoryConfiguration sharepointRepositoryConfiguration,
		SharepointRepositoryTokenBroker sharepointOAuth2AuthorizationServer) {

		_tokenStore = tokenStore;
		_sharepointRepositoryConfiguration = sharepointRepositoryConfiguration;
		_sharepointOAuth2AuthorizationServer =
			sharepointOAuth2AuthorizationServer;
	}

	@Override
	public void authorize(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, PortalException {

		_authorize(
			PortalUtil.getOriginalServletRequest(httpServletRequest),
			httpServletResponse);
	}

	@Override
	public void authorize(
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws IOException, PortalException {

		authorize(
			PortalUtil.getHttpServletRequest(portletRequest),
			PortalUtil.getHttpServletResponse(portletResponse));
	}

	@Override
	public boolean hasCustomRedirectFlow(
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws PortalException {

		if (_hasAuthorizationGrant(
				PortalUtil.getHttpServletRequest(portletRequest))) {

			return true;
		}

		Token token = _tokenStore.get(
			_sharepointRepositoryConfiguration.name(),
			PortalUtil.getUserId(
				PortalUtil.getHttpServletRequest(portletRequest)));

		if (token == null) {
			return true;
		}
		else if (token.isExpired()) {
			if (Validator.isNotNull(token.getRefreshToken())) {
				return false;
			}

			return true;
		}

		return false;
	}

	private void _authorize(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, PortalException {

		_validateRequest(httpServletRequest);

		if (_hasAuthorizationGrant(httpServletRequest)) {
			_requestAccessToken(httpServletRequest, httpServletResponse);
		}
		else {
			Token token = _tokenStore.get(
				_sharepointRepositoryConfiguration.name(),
				PortalUtil.getUserId(httpServletRequest));

			if (token == null) {
				_requestAuthorizationGrant(
					httpServletRequest, httpServletResponse);
			}
			else if (token.isExpired()) {
				if (Validator.isNotNull(token.getRefreshToken())) {
					_refreshAccessToken(token, httpServletRequest);
				}
				else {
					_requestAccessToken(
						httpServletRequest, httpServletResponse);
				}
			}
		}
	}

	private String _getGrantURL(
		HttpServletRequest httpServletRequest, String state) {

		String url =
			_sharepointRepositoryConfiguration.authorizationGrantEndpoint();

		url = HttpComponentsUtil.addParameter(
			url, "client_id", _sharepointRepositoryConfiguration.clientId());

		url = HttpComponentsUtil.addParameter(
			url, "redirect_uri", _getRedirectURI(httpServletRequest));
		url = HttpComponentsUtil.addParameter(url, "response_type", "code");
		url = HttpComponentsUtil.addParameter(
			url, "scope", _sharepointRepositoryConfiguration.scope());
		url = HttpComponentsUtil.addParameter(url, "state", state);

		return url;
	}

	private String _getRedirectURI(HttpServletRequest httpServletRequest) {
		return PortalUtil.getAbsoluteURL(
			httpServletRequest,
			PortalUtil.getPathMain() + "/document_library/sharepoint/oauth2");
	}

	private boolean _hasAuthorizationGrant(
		HttpServletRequest httpServletRequest) {

		String code = ParamUtil.getString(httpServletRequest, "code");

		if (Validator.isNull(code)) {
			return false;
		}

		return true;
	}

	private void _refreshAccessToken(
			Token token, HttpServletRequest httpServletRequest)
		throws IOException, PortalException {

		long userId = PortalUtil.getUserId(httpServletRequest);

		try {
			Token freshToken =
				_sharepointOAuth2AuthorizationServer.refreshAccessToken(token);

			_tokenStore.save(
				_sharepointRepositoryConfiguration.name(), userId, freshToken);
		}
		catch (AuthorizationException authorizationException) {
			_tokenStore.delete(
				_sharepointRepositoryConfiguration.name(), userId);

			throw authorizationException;
		}
	}

	private void _requestAccessToken(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, PortalException {

		SharepointRepositoryRequestState sharepointRepositoryRequestState =
			SharepointRepositoryRequestState.get(httpServletRequest);

		sharepointRepositoryRequestState.validate(
			ParamUtil.getString(httpServletRequest, "state"));

		long userId = PortalUtil.getUserId(httpServletRequest);

		String code = ParamUtil.getString(httpServletRequest, "code");

		Token accessToken =
			_sharepointOAuth2AuthorizationServer.requestAccessToken(
				code, _getRedirectURI(httpServletRequest));

		_tokenStore.save(
			_sharepointRepositoryConfiguration.name(), userId, accessToken);

		sharepointRepositoryRequestState.restore(
			httpServletRequest, httpServletResponse);
	}

	private void _requestAuthorizationGrant(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		String state = StringUtil.randomString(5);

		SharepointRepositoryRequestState.save(httpServletRequest, state);

		httpServletResponse.sendRedirect(
			_getGrantURL(httpServletRequest, state));
	}

	private void _validateRequest(HttpServletRequest httpServletRequest)
		throws AuthorizationException {

		String error = ParamUtil.getString(httpServletRequest, "error");

		if (Validator.isNotNull(error)) {
			String description = ParamUtil.getString(
				httpServletRequest, "error_description");

			if (Validator.isNull(description)) {
				description = error;
			}

			throw OAuth2AuthorizationException.getErrorException(
				error, description);
		}
	}

	private final SharepointRepositoryTokenBroker
		_sharepointOAuth2AuthorizationServer;
	private final SharepointRepositoryConfiguration
		_sharepointRepositoryConfiguration;
	private final TokenStore _tokenStore;

}