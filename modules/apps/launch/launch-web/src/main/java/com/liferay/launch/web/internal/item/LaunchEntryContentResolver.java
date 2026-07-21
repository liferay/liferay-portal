/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.web.internal.item;

import com.liferay.portal.kernel.exception.PortalException;

import java.util.Locale;

/**
 * @author David Truong
 */
public interface LaunchEntryContentResolver {

	public LaunchEntryContent resolve(
			long classPK, String classVersion, Locale locale)
		throws PortalException;

}