/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util.comparator;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.OrderByComparator;

/**
 * @author Balazs Breier
 */
public class UserLastLoginDateComparator extends OrderByComparator<User> {

	public static final String ORDER_BY_ASC =
		"lastLoginDate ASC, lastName ASC, firstName ASC, middleName ASC";

	public static final String ORDER_BY_DESC =
		"lastLoginDate DESC, lastName DESC, firstName DESC, middleName DESC";

	public static final String[] ORDER_BY_FIELDS = {
		"lastLoginDate", "lastName", "firstName", "middleName"
	};

	public static UserLastLoginDateComparator getInstance(boolean ascending) {
		if (ascending) {
			return _INSTANCE_ASCENDING;
		}

		return _INSTANCE_DESCENDING;
	}

	@Override
	public int compare(User user1, User user2) {
		int value = DateUtil.compareTo(
			user1.getLastLoginDate(), user2.getLastLoginDate());

		if (value == 0) {
			String lastName1 = user1.getLastName();
			String lastName2 = user2.getLastName();

			value = lastName1.compareTo(lastName2);
		}

		if (value == 0) {
			String firstName1 = user1.getFirstName();
			String firstName2 = user2.getFirstName();

			value = firstName1.compareTo(firstName2);
		}

		if (value == 0) {
			String middleName1 = user1.getMiddleName();
			String middleName2 = user2.getMiddleName();

			value = middleName1.compareTo(middleName2);
		}

		if (_ascending) {
			return value;
		}

		return -value;
	}

	@Override
	public String getOrderBy() {
		if (_ascending) {
			return ORDER_BY_ASC;
		}

		return ORDER_BY_DESC;
	}

	@Override
	public String[] getOrderByFields() {
		return ORDER_BY_FIELDS;
	}

	@Override
	public boolean isAscending() {
		return _ascending;
	}

	private UserLastLoginDateComparator(boolean ascending) {
		_ascending = ascending;
	}

	private static final UserLastLoginDateComparator _INSTANCE_ASCENDING =
		new UserLastLoginDateComparator(true);

	private static final UserLastLoginDateComparator _INSTANCE_DESCENDING =
		new UserLastLoginDateComparator(false);

	private final boolean _ascending;

}