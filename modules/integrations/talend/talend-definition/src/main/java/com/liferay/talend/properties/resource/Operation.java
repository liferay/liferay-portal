/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.properties.resource;

import com.liferay.talend.common.oas.constants.OASConstants;

/**
 * @author Zoltán Takács
 */
public enum Operation {

	Delete(OASConstants.OPERATION_DELETE), Get(OASConstants.OPERATION_GET),
	Insert(OASConstants.OPERATION_POST), Replace(OASConstants.OPERATION_PUT),
	Unavailable("noop"), Update(OASConstants.OPERATION_PATCH);

	public static Operation toOperation(String methodName) {
		for (Operation operation : values()) {
			if (methodName.equals(operation._httpMethod)) {
				return operation;
			}
		}

		return Unavailable;
	}

	public String getHttpMethod() {
		return _httpMethod;
	}

	private Operation(String httpMethod) {
		_httpMethod = httpMethod;
	}

	private final String _httpMethod;

}