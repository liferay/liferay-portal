/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.report.exception;

import com.liferay.portal.kernel.exception.NoSuchModelException;

/**
 * @author Carlos Correa
 */
public class NoSuchExportImportReportEntryException
	extends NoSuchModelException {

	public NoSuchExportImportReportEntryException() {
	}

	public NoSuchExportImportReportEntryException(String msg) {
		super(msg);
	}

	public NoSuchExportImportReportEntryException(
		String msg, Throwable throwable) {

		super(msg, throwable);
	}

	public NoSuchExportImportReportEntryException(Throwable throwable) {
		super(throwable);
	}

}