/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.customer.constants;

import com.liferay.petra.string.StringPool;

/**
 * @author Jenny Chen
 */
public interface JiraIssueConstants {

	public static final String STATUS_CLOSED = "Closed";

	public static final String STATUS_FLS_CLOSED = "Closed (FLS)";

	public static final String STATUS_FLS_SOLVED = "Solved (FLS)";

	public static final String STATUS_INACTIVE = "Inactive";

	public static final String STATUS_SOLUTION_ACCEPTED = "Solution Accepted";

	public static final String STATUS_SOLUTION_PROPOSED = "Solution Proposed";

	public static final String[] STATUSES_CLOSED = {
		STATUS_CLOSED, STATUS_FLS_CLOSED, STATUS_SOLUTION_ACCEPTED
	};

	public static final String[] STATUSES_SOLVED_AND_CLOSED = {
		STATUS_CLOSED, STATUS_FLS_CLOSED, STATUS_FLS_SOLVED, STATUS_INACTIVE,
		STATUS_SOLUTION_ACCEPTED, STATUS_SOLUTION_PROPOSED
	};

	public static final String TYPE_GENERAL_REQUEST = "General Request";

	public static String toJQLCustomField(String customField) {
		int pos = customField.indexOf(StringPool.UNDERLINE);

		return "cf[" + customField.substring(pos + 1) + "]";
	}

}