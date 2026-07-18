/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key;

import com.liferay.portal.kernel.util.Validator;

/**
 * @author Christopher Kian
 */
public class ServiceIndicator {

	public ServiceIndicator(boolean approved, String securityFunctionName) {
		if (Validator.isNull(securityFunctionName)) {
			throw new IllegalArgumentException(
				"Security function name is null");
		}

		_approved = approved;
		_securityFunctionName = securityFunctionName;
	}

	public String getSecurityFunctionName() {
		return _securityFunctionName;
	}

	public boolean isApproved() {
		return _approved;
	}

	private final boolean _approved;
	private final String _securityFunctionName;

}