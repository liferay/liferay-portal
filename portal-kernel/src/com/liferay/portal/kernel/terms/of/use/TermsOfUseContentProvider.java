/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.terms.of.use;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Eduardo García
 */
@ProviderType
public interface TermsOfUseContentProvider {

	public void includeConfig(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception;

	public void includeView(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception;

}