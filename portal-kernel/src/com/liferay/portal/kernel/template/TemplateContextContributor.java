/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.template;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Julio Camarero
 */
public interface TemplateContextContributor {

	public static final String TYPE_GLOBAL = "GLOBAL";

	public static final String TYPE_THEME = "THEME";

	public void prepare(
		Map<String, Object> contextObjects,
		HttpServletRequest httpServletRequest);

}