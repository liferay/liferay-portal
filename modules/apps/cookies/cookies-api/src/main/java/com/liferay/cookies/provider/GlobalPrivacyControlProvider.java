/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.provider;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Christian Moura
 */
@ProviderType
public interface GlobalPrivacyControlProvider {

	public boolean isEnabled(HttpServletRequest httpServletRequest);

	public boolean isSignalActive(HttpServletRequest httpServletRequest);

}