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
import com.liferay.portal.kernel.repository.DocumentRepository;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.sharepoint.rest.repository.internal.configuration.SharepointRepositoryConfiguration;
import com.liferay.sharepoint.rest.repository.internal.document.library.repository.authorization.oauth2.SharepointRepositoryAuthenticationResult;
import com.liferay.sharepoint.rest.repository.internal.document.library.repository.authorization.oauth2.SharepointRepositoryRequestState;
import com.liferay.sharepoint.rest.repository.internal.document.library.repository.authorization.oauth2.SharepointRepositoryTokenBroker;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * @author Adolfo Pérez
 */
public class SharepointRepositoryAuthorizationCapability
	implements AuthorizationCapability {

	public SharepointRepositoryAuthorizationCapability(
		DocumentRepository documentRepository, TokenStore tokenStore,
		SharepointRepositoryConfiguration sharepointRepositoryConfiguration,
		SharepointRepositoryTokenBroker sharepointOAuth2AuthorizationServer) {

		_documentRepository = documentRepository;
		_tokenStore = tokenStore;
		_sharepointRepositoryConfiguration = sharepointRepositoryConfiguration;
		_sharepointOAuth2AuthorizationServer =
			sharepointOAuth2AuthorizationServer;
	}

	@Override
	public void authorize(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws PortalException {

		_authorize(
			PortalUtil.getOriginalServletRequest(httpServletRequest),
			httpServletResponse);
	}

	@Override
	public void authorize(
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws PortalException {

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

		if (token.isExpired()) {
			return Validator.isNull(token.getRefreshToken());
		}

		return false;
	}

	private void _authorize(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws PortalException {

		_validateRequest(httpServletRequest);

		if (_hasAuthorizationGrant(httpServletRequest)) {
			_requestAccessToken(httpServletRequest, httpServletResponse);
		}
		else {
			Token token = _tokenStore.get(
				_sharepointRepositoryConfiguration.name(),
				PortalUtil.getUserId(httpServletRequest));

			if (token != null) {
				_requestAccessToken(httpServletRequest, token);
			}
			else {
				_requestAuthorizationGrant(
					httpServletRequest, httpServletResponse);
			}
		}
	}

	private String _getRedirectURI(HttpServletRequest httpServletRequest) {
		return PortalUtil.getAbsoluteURL(
			httpServletRequest,
			PortalUtil.getPathMain() + "/document_library/sharepoint/oauth2");
	}

	private boolean _hasAuthorizationGrant(
		HttpServletRequest httpServletRequest) {

		String code = ParamUtil.getString(httpServletRequest, "code");

		return Validator.isNotNull(code);
	}

	private void _requestAccessToken(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws PortalException {

		SharepointRepositoryRequestState sharepointRepositoryRequestState =
			SharepointRepositoryRequestState.get(httpServletRequest);

		sharepointRepositoryRequestState.validateState(
			ParamUtil.getString(httpServletRequest, "state"));

		try {
			SharepointRepositoryAuthenticationResult
				sharepointRepositoryAuthenticationResult =
					_sharepointOAuth2AuthorizationServer.requestAccessToken(
						ParamUtil.getString(httpServletRequest, "code"),
						_getRedirectURI(httpServletRequest));

			sharepointRepositoryRequestState.validateNonce(
				sharepointRepositoryAuthenticationResult.getNonce());

			_tokenStore.save(
				_sharepointRepositoryConfiguration.name(),
				PortalUtil.getUserId(httpServletRequest),
				sharepointRepositoryAuthenticationResult.getToken());

			sharepointRepositoryRequestState.restore(
				httpServletRequest, httpServletResponse);
		}
		catch (ExecutionException | InterruptedException | IOException |
			   URISyntaxException exception) {

			throw new PortalException(exception);
		}
	}

	private void _requestAccessToken(
			HttpServletRequest httpServletRequest, Token token)
		throws PortalException {

		try {
			SharepointRepositoryAuthenticationResult
				sharepointRepositoryAuthenticationResult =
					_sharepointOAuth2AuthorizationServer.
						requestAccessTokenSilently(token);

			_tokenStore.save(
				_sharepointRepositoryConfiguration.name(),
				PortalUtil.getUserId(httpServletRequest),
				sharepointRepositoryAuthenticationResult.getToken());
		}
		catch (ExecutionException | InterruptedException | MalformedURLException
					exception) {

			throw new PortalException(exception);
		}
	}

	private void _requestAuthorizationGrant(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws PortalException {

		String nonce = String.valueOf(UUID.randomUUID());
		String state = String.valueOf(UUID.randomUUID());

		SharepointRepositoryRequestState.save(
			httpServletRequest, nonce, _documentRepository.getRepositoryId(),
			state);

		try {
			httpServletResponse.sendRedirect(
				_sharepointOAuth2AuthorizationServer.getAuthorizationRequestUrl(
					nonce, _getRedirectURI(httpServletRequest), state));
		}
		catch (IOException ioException) {
			throw new PortalException(ioException);
		}
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

	private final DocumentRepository _documentRepository;
	private final SharepointRepositoryTokenBroker
		_sharepointOAuth2AuthorizationServer;
	private final SharepointRepositoryConfiguration
		_sharepointRepositoryConfiguration;
	private final TokenStore _tokenStore;

}