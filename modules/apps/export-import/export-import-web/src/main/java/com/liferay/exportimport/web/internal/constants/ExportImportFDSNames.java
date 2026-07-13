/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.web.internal.constants;

import com.liferay.exportimport.constants.ExportImportPortletKeys;
import com.liferay.portal.kernel.util.PortletKeys;

/**
 * @author Jorge González
 */
public class ExportImportFDSNames {

	public static final String COMPANY_EXPORT_PROCESSES =
		PortletKeys.COMPANY_EXPORT + "-exportProcesses";

	public static final String COMPANY_IMPORT_PROCESSES =
		PortletKeys.COMPANY_IMPORT + "-importProcesses";

	public static final String COMPANY_IMPORT_REPORT_ENTRIES =
		PortletKeys.COMPANY_IMPORT + "-importReportEntries";

	public static final String EXPORT_PROCESSES =
		ExportImportPortletKeys.EXPORT + "-exportProcesses";

	public static final String IMPORT_PROCESSES =
		ExportImportPortletKeys.IMPORT + "-importProcesses";

	public static final String IMPORT_REPORT_ENTRIES =
		ExportImportPortletKeys.IMPORT + "-importReportEntries";

}