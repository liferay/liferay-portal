/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.fragment.internal.display.context;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Alessio Antonio Rendina
 */
public class BaseDisplayContext {

	public BaseDisplayContext(
		Map<String, Object> configurationValues,
		HttpServletRequest httpServletRequest) {

		this.configurationValues = configurationValues;
		this.httpServletRequest = httpServletRequest;
	}

	public Object getConfigurationValue(String name) {
		return configurationValues.get(name);
	}

	protected final Map<String, Object> configurationValues;
	protected final HttpServletRequest httpServletRequest;

}