/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.report.internal.util;

import com.liferay.batch.engine.thread.local.BatchEngineThreadLocal;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;

/**
 * @author Petteri Karttunen
 */
public class ExportImportReportEntryUtil {

	public static int getOrigin() {
		if (BatchEngineThreadLocal.isBatchImportInProcess()) {
			return ExportImportReportEntryConstants.ORIGIN_BATCH;
		}

		return ExportImportReportEntryConstants.ORIGIN_STAGING;
	}

}