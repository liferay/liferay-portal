/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.avro.exception;

import com.liferay.talend.exception.BaseComponentException;

/**
 * @author Igor Beslic
 */
public class ConverterException extends BaseComponentException {

	public ConverterException(String message) {
		super(message, 0);
	}

	public ConverterException(String message, Throwable throwable) {
		super(message, 0, throwable);
	}

}