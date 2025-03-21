/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.runtime.servlet.profile;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.saml.persistence.model.SamlSpSession;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Mika Koivisto
 */
public interface SingleLogoutProfile {

	public SamlSpSession getSamlSpSession(
		HttpServletRequest httpServletRequest);

	public boolean isSingleLogoutSupported(
		HttpServletRequest httpServletRequest);

	public void logout(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse);

	public void processIdpLogout(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws PortalException;

	public void processSingleLogout(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws PortalException;

	public void processSpLogout(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws PortalException;

	public void terminateSpSession(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse);

	public void terminateSsoSession(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse);

}