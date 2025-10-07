/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.customer.exception;

/**
 * @author Karoline Silva
 */
public class JiraOrganizationNotFoundException extends Exception {

	public JiraOrganizationNotFoundException() {
	}

	public JiraOrganizationNotFoundException(String message) {
		super(message);
	}

	public JiraOrganizationNotFoundException(Throwable throwable) {
		super(throwable);
	}

}