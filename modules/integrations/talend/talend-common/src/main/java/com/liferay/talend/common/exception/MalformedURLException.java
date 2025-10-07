/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.common.exception;

/**
 * @author Zoltán Takács
 */
public class MalformedURLException extends RuntimeException {

	public MalformedURLException(String message) {
		super(message);
	}

	public MalformedURLException(Throwable throwable) {
		super(throwable);
	}

}