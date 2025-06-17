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
		HttpServletRequest httpServletRequest, String state) {

		HttpSession httpSession = httpServletRequest.getSession();

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST);

		httpSession.setAttribute(
			SharepointRepositoryRequestState.class.getName(),
			new SharepointRepositoryRequestState(
				ParamUtil.getLong(portletRequest, "folderId"),
				PortalUtil.getCurrentCompleteURL(httpServletRequest), state));
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

	public void validate(String state) throws AuthorizationException {
		if (!state.equals(_state)) {
			throw new OAuth2AuthorizationException.InvalidState(
				String.format(
					"The Sharepoint server returned an invalid state %s that " +
						"does not match the expected state %s",
					_state, state));
		}
	}

	private SharepointRepositoryRequestState(
		long folderId, String url, String state) {

		_folderId = folderId;
		_url = url;
		_state = state;
	}

	private static final long serialVersionUID = 1L;

	private final long _folderId;
	private final String _state;
	private final String _url;

}