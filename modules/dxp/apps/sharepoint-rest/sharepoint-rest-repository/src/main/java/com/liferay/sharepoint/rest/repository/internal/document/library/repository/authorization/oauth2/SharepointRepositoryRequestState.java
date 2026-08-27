/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharepoint.rest.repository.internal.document.library.repository.authorization.oauth2;

import com.liferay.document.library.repository.authorization.capability.AuthorizationException;
import com.liferay.document.library.repository.authorization.oauth2.OAuth2AuthorizationException;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.Serializable;

import java.nio.charset.StandardCharsets;

import java.security.MessageDigest;

/**
 * @author Adolfo Pérez
 */
public final class SharepointRepositoryRequestState implements Serializable {

	public static SharepointRepositoryRequestState get(
		HttpServletRequest httpServletRequest) {

		HttpSession httpSession = httpServletRequest.getSession();

		return (SharepointRepositoryRequestState)httpSession.getAttribute(
			SharepointRepositoryRequestState.class.getName());
	}

	public static void save(
		HttpServletRequest httpServletRequest, String nonce, String state) {

		HttpSession httpSession = httpServletRequest.getSession();

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST);

		httpSession.setAttribute(
			SharepointRepositoryRequestState.class.getName(),
			new SharepointRepositoryRequestState(
				ParamUtil.getLong(portletRequest, "folderId"), nonce, state,
				PortalUtil.getCurrentCompleteURL(httpServletRequest)));
	}

	public long getFolderId() {
		return _folderId;
	}

	public void restore(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		HttpSession httpSession = httpServletRequest.getSession();

		httpSession.removeAttribute(
			SharepointRepositoryRequestState.class.getName());

		httpServletResponse.sendRedirect(_url);
	}

	public void validateNonce(String nonce) throws AuthorizationException {
		if ((nonce == null) ||
			!MessageDigest.isEqual(
				nonce.getBytes(StandardCharsets.UTF_8),
				_nonce.getBytes(StandardCharsets.UTF_8))) {

			throw new OAuth2AuthorizationException.InvalidNonce(
				"The Sharepoint server returned an invalid nonce");
		}
	}

	public void validateState(String state) throws AuthorizationException {
		if ((state == null) ||
			!MessageDigest.isEqual(
				state.getBytes(StandardCharsets.UTF_8),
				_state.getBytes(StandardCharsets.UTF_8))) {

			throw new OAuth2AuthorizationException.InvalidState(
				"The Sharepoint server returned an invalid state");
		}
	}

	private SharepointRepositoryRequestState(
		long folderId, String nonce, String state, String url) {

		_folderId = folderId;
		_nonce = nonce;
		_state = state;
		_url = url;
	}

	private static final long serialVersionUID = 1L;

	private final long _folderId;
	private final String _nonce;
	private final String _state;
	private final String _url;

}