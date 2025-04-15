/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.access.control;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.AuthException;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierResult;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

/**
 * @author Raymond Augé
 */
public interface AccessControl {

	public void initAccessControlContext(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, Map<String, Object> settings);

	public void initContextUser(long userId) throws AuthException;

	public AuthVerifierResult.State verifyRequest() throws PortalException;

}