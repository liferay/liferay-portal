/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.constants;

/**
 * @author Tancredi Covioli
 */
public class CommerceAccountEntryValidationConstants {

	public static final String VALIDATION_MODE_ALLOW_ALL = "allow-all";

	public static final String VALIDATION_MODE_ALLOW_SUCCESSES_ONLY =
		"allow-successes-only";

	public static final String VALIDATION_MODE_ALLOW_TECHNICAL_FAILURES =
		"allow-technical-failures";

	public static final String VALIDATION_MODE_DISABLED = "disabled";

	public static final String[] VALIDATION_MODES = {
		"allow-all", "allow-successes-only", "allow-technical-failures",
		"disabled"
	};

}