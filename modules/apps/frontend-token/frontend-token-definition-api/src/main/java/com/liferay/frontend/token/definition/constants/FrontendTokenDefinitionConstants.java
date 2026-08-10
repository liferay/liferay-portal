/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.token.definition.constants;

/**
 * @author Thiago Buarque
 */
public class FrontendTokenDefinitionConstants {

	public static final int PRIORITY_CUSTOM = 500;

	public static final int PRIORITY_GLOBAL = 100;

	public static final int PRIORITY_LEGACY =
		FrontendTokenDefinitionConstants.PRIORITY_THEME;

	public static final int PRIORITY_THEME = 300;

	public static final String THEME_TYPE_BUNDLE = "bundle";

	public static final String THEME_TYPE_GLOBAL = "global";

	public static final String THEME_TYPE_THEME_CSS_CET = "themeCSSCET";

}