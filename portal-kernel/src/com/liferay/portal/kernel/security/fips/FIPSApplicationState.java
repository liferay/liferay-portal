/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

/**
 * @author Jorge García Jiménez
 */
public enum FIPSApplicationState {

	ERROR, INITIALIZING, KEY_CSP_ENTRY, OPERATIONAL, POWER_OFF, QUIESCENT,
	SELF_TEST

}