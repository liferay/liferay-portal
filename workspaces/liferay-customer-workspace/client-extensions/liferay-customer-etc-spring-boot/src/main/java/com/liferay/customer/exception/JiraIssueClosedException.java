/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.customer.exception;

/**
 * @author Karoline Silva
 */
public class JiraIssueClosedException extends Exception {

	public JiraIssueClosedException() {
	}

	public JiraIssueClosedException(Throwable throwable) {
		super(throwable);
	}

}