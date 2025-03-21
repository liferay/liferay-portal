/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.auth;

import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierResult;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tomas Polesovsky
 * @author Raymond Augé
 */
public class AccessControlContext {

	public AuthVerifierResult getAuthVerifierResult() {
		return _authVerifierResult;
	}

	public HttpServletRequest getRequest() {
		return _httpServletRequest;
	}

	public HttpServletResponse getResponse() {
		return _httpServletResponse;
	}

	public Map<String, Object> getSettings() {
		return _settings;
	}

	public void setAuthVerifierResult(AuthVerifierResult authVerifierResult) {
		_authVerifierResult = authVerifierResult;
	}

	public void setRequest(HttpServletRequest httpServletRequest) {
		_httpServletRequest = httpServletRequest;
	}

	public void setResponse(HttpServletResponse httpServletResponse) {
		_httpServletResponse = httpServletResponse;
	}

	public static enum Settings {

		SERVICE_DEPTH

	}

	private AuthVerifierResult _authVerifierResult;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private final Map<String, Object> _settings = new HashMap<>();

}