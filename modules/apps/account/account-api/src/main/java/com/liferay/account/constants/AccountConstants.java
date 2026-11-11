/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.constants;

/**
 * @author Pei-Jung Lan
 */
public class AccountConstants {

	public static final long ACCOUNT_ENTRY_ID_ADMIN = Long.MIN_VALUE;

	public static final long ACCOUNT_ENTRY_ID_ANY = -1;

	public static final long ACCOUNT_ENTRY_ID_DEFAULT = 0;

	public static final long ACCOUNT_ENTRY_ID_GUEST = -1;

	public static final String ACCOUNT_ENTRY_TYPE_BUSINESS = "business";

	public static final String ACCOUNT_ENTRY_TYPE_GUEST = "guest";

	public static final String ACCOUNT_ENTRY_TYPE_PERSON = "person";

	public static final String ACCOUNT_ENTRY_TYPE_SUPPLIER = "supplier";

	public static final String[] ACCOUNT_ENTRY_TYPES = {
		ACCOUNT_ENTRY_TYPE_BUSINESS, ACCOUNT_ENTRY_TYPE_GUEST,
		ACCOUNT_ENTRY_TYPE_PERSON, ACCOUNT_ENTRY_TYPE_SUPPLIER
	};

	public static final String[] ACCOUNT_ENTRY_TYPES_DEFAULT_ALLOWED_TYPES = {
		ACCOUNT_ENTRY_TYPE_BUSINESS, ACCOUNT_ENTRY_TYPE_PERSON,
		ACCOUNT_ENTRY_TYPE_SUPPLIER
	};

	public static final String ACCOUNT_GROUP_NAME_GUEST = "Guest";

	public static final String ACCOUNT_GROUP_TYPE_DYNAMIC = "dynamic";

	public static final String ACCOUNT_GROUP_TYPE_GUEST = "guest";

	public static final String ACCOUNT_GROUP_TYPE_STATIC = "static";

	public static final String[] ACCOUNT_GROUP_TYPES = {
		ACCOUNT_GROUP_TYPE_STATIC, ACCOUNT_GROUP_TYPE_DYNAMIC
	};

	public static final long PARENT_ACCOUNT_ENTRY_ID_DEFAULT = 0;

	public static final String RESOURCE_NAME = "com.liferay.account";

}