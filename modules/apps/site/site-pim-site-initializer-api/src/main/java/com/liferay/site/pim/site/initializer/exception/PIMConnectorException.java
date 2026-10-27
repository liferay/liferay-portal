/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Stefano Motta
 */
public class PIMConnectorException extends PortalException {

	public PIMConnectorException() {
	}

	public PIMConnectorException(String msg) {
		super(msg);
	}

	public PIMConnectorException(
		String msg, String output, Throwable throwable) {

		super(msg, throwable);

		_output = output;
	}

	public PIMConnectorException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

	public PIMConnectorException(Throwable throwable) {
		super(throwable);
	}

	public String getOutput() {
		return _output;
	}

	private String _output;

}