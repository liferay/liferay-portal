/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.constants;

/**
 * @author Shinn Lok
 */
public class FaroConstants {

	public static final String APPLICATION_ASAH = "asah";

	public static final String APPLICATION_CONTACTS = "contacts";

	public static final String APPLICATION_MAIN = "main";

	public static final int TYPE_ACCOUNT = 0;

	public static final int TYPE_ASSET = 5;

	public static final int TYPE_DATA_SOURCE = 1;

	public static final int TYPE_INDIVIDUAL = 2;

	public static final int TYPE_PAGE = 6;

	public static final int TYPE_SEGMENT_ACCOUNTS = 3;

	public static final int TYPE_SEGMENT_INDIVIDUALS = 4;

	public static int[] getContactsTypes() {
		return new int[] {
			TYPE_ACCOUNT, TYPE_INDIVIDUAL, TYPE_SEGMENT_ACCOUNTS,
			TYPE_SEGMENT_INDIVIDUALS
		};
	}

}