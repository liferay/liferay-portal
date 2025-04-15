/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.filters.secure;

import jakarta.servlet.FilterConfig;

/**
 * @author Mariano Álvaro Sáiz
 * @author Arthur Chan
 */
public class SecureFilter extends BaseAuthFilter {

	@Override
	public void init(FilterConfig filterConfig) {
		super.init(filterConfig);

		setFilterEnabled(true);
	}

	@Override
	public boolean isFilterEnabled() {
		return true;
	}

}