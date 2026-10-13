/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.jasper.internal.fill.manager;

import com.liferay.portal.reports.engine.ReportRequest;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperReportsContext;

/**
 * @author Gavin Wan
 * @author Brian Wing Shun Chan
 */
public interface ReportFillManager {

	public JasperPrint fillReport(
			JasperReport jasperReport,
			JasperReportsContext jasperReportsContext,
			ReportRequest reportRequest)
		throws JRException;

}