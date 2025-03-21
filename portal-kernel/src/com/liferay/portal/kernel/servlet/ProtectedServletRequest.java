/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import jakarta.servlet.http.HttpServletRequest;

import java.security.Principal;

/**
 * @author Brian Wing Shun Chan
 */
public class ProtectedServletRequest
	extends PersistentHttpServletRequestWrapper {

	public ProtectedServletRequest(
		HttpServletRequest httpServletRequest, String remoteUser) {

		this(httpServletRequest, remoteUser, null);
	}

	public ProtectedServletRequest(
		HttpServletRequest httpServletRequest, String remoteUser,
		String authType) {

		super(httpServletRequest);

		if (remoteUser == null) {
			throw new NullPointerException("Remote user is null");
		}

		if (httpServletRequest instanceof ProtectedServletRequest) {
			ProtectedServletRequest parentRequest =
				(ProtectedServletRequest)httpServletRequest;

			setRequest(parentRequest.getRequest());
		}

		_remoteUser = remoteUser;
		_authType = authType;

		_userPrincipal = new ProtectedPrincipal(remoteUser);
	}

	@Override
	public String getAuthType() {
		if (_authType == null) {
			return super.getAuthType();
		}

		return _authType;
	}

	@Override
	public String getRemoteUser() {
		return _remoteUser;
	}

	@Override
	public Principal getUserPrincipal() {
		return _userPrincipal;
	}

	private final String _authType;
	private final String _remoteUser;
	private final Principal _userPrincipal;

}