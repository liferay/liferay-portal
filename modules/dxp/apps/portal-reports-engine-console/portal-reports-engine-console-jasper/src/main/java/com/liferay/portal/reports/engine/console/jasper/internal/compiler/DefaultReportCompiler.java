/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.jasper.internal.compiler;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.portal.reports.engine.ReportDesignRetriever;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

/**
 * @author Michael C. Han
 * @author Brian Wing Shun Chan
 */
public class DefaultReportCompiler implements ReportCompiler {

	public DefaultReportCompiler(JasperReportsContext jasperReportsContext) {
		_jasperReportsContext = jasperReportsContext;
	}

	@Override
	public JasperReport compile(ReportDesignRetriever reportDesignRetriever)
		throws JRException {

		return compile(reportDesignRetriever, false);
	}

	@Override
	public JasperReport compile(
			ReportDesignRetriever reportDesignRetriever, boolean force)
		throws JRException {

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				DefaultReportCompiler.class.getClassLoader())) {

			JasperDesign jasperDesign = JRXmlLoader.load(
				_jasperReportsContext, reportDesignRetriever.getInputStream());

			JasperCompileManager jasperCompileManager =
				JasperCompileManager.getInstance(_jasperReportsContext);

			return jasperCompileManager.compile(jasperDesign);
		}
	}

	@Override
	public void flush() {
	}

	private final JasperReportsContext _jasperReportsContext;

}