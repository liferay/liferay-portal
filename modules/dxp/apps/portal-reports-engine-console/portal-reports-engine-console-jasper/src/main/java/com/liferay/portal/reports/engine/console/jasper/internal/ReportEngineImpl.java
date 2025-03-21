/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.jasper.internal;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StackTraceUtil;
import com.liferay.portal.reports.engine.ReportDataSourceType;
import com.liferay.portal.reports.engine.ReportEngine;
import com.liferay.portal.reports.engine.ReportFormat;
import com.liferay.portal.reports.engine.ReportFormatExporter;
import com.liferay.portal.reports.engine.ReportGenerationException;
import com.liferay.portal.reports.engine.ReportRequest;
import com.liferay.portal.reports.engine.ReportRequestContext;
import com.liferay.portal.reports.engine.ReportResultContainer;
import com.liferay.portal.reports.engine.console.jasper.internal.compiler.CachedReportCompiler;
import com.liferay.portal.reports.engine.console.jasper.internal.compiler.DefaultReportCompiler;
import com.liferay.portal.reports.engine.console.jasper.internal.compiler.ReportCompiler;
import com.liferay.portal.reports.engine.console.jasper.internal.fill.manager.CsvReportFillManager;
import com.liferay.portal.reports.engine.console.jasper.internal.fill.manager.EmptyReportFillManager;
import com.liferay.portal.reports.engine.console.jasper.internal.fill.manager.JdbcReportFillManager;
import com.liferay.portal.reports.engine.console.jasper.internal.fill.manager.PortalReportFillManager;
import com.liferay.portal.reports.engine.console.jasper.internal.fill.manager.ReportFillManager;
import com.liferay.portal.reports.engine.console.jasper.internal.fill.manager.XlsReportFillManager;
import com.liferay.portal.reports.engine.console.jasper.internal.fill.manager.XmlReportFillManager;

import jakarta.servlet.ServletContext;

import java.util.EnumMap;
import java.util.Map;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Michael C. Han
 * @author Brian Wing Shun Chan
 * @author Brian Greenwald
 */
@Component(service = ReportEngine.class)
public class ReportEngineImpl implements ReportEngine {

	@Override
	public void compile(ReportRequest reportRequest)
		throws ReportGenerationException {

		try {
			_reportCompiler.compile(
				reportRequest.getReportDesignRetriever(), true);
		}
		catch (Exception exception) {
			throw new ReportGenerationException(
				"Unable to compile report: " +
					StackTraceUtil.getStackTrace(exception));
		}
	}

	@Override
	public void destroy() {
	}

	@Override
	public void execute(
			ReportRequest reportRequest, ReportResultContainer resultContainer)
		throws ReportGenerationException {

		try {
			ReportRequestContext reportRequestContext =
				reportRequest.getReportRequestContext();

			ReportFillManager reportFillManager = _reportFillManagers.get(
				reportRequestContext.getReportDataSourceType());

			if (reportFillManager == null) {
				throw new IllegalArgumentException(
					"No report fill manager found for " +
						reportRequestContext.getReportDataSourceType());
			}

			JasperReport jasperReport = _reportCompiler.compile(
				reportRequest.getReportDesignRetriever());

			JasperPrint jasperPrint = reportFillManager.fillReport(
				jasperReport, reportRequest);

			ReportFormatExporter reportFormatExporter =
				_serviceTrackerMap.getService(reportRequest.getReportFormat());

			if (reportFormatExporter == null) {
				throw new IllegalArgumentException(
					"No report format exporter found for " +
						reportRequest.getReportFormat());
			}

			reportFormatExporter.format(
				jasperPrint, reportRequest, resultContainer);
		}
		catch (Exception exception) {
			throw new ReportGenerationException(
				"Unable to execute report: " +
					StackTraceUtil.getStackTrace(exception));
		}
	}

	@Override
	public Map<String, String> getEngineParameters() {
		return _engineParameters;
	}

	@Override
	public void init(ServletContext servletContext) {
	}

	@Override
	public void setEngineParameters(Map<String, String> engineParameters) {
		_engineParameters = engineParameters;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_reportFillManagers.put(
			ReportDataSourceType.CSV, new CsvReportFillManager());
		_reportFillManagers.put(
			ReportDataSourceType.EMPTY, new EmptyReportFillManager());
		_reportFillManagers.put(
			ReportDataSourceType.JDBC, new JdbcReportFillManager());
		_reportFillManagers.put(
			ReportDataSourceType.PORTAL, new PortalReportFillManager());
		_reportFillManagers.put(
			ReportDataSourceType.XLS, new XlsReportFillManager());
		_reportFillManagers.put(
			ReportDataSourceType.XML, new XmlReportFillManager());

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, ReportFormatExporter.class, null,
			(serviceReference, emitter) -> {
				String reportFormatString = GetterUtil.getString(
					serviceReference.getProperty("reportFormat"));

				emitter.emit(ReportFormat.parse(reportFormatString));
			});
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private Map<String, String> _engineParameters;
	private final ReportCompiler _reportCompiler = new CachedReportCompiler(
		new DefaultReportCompiler());
	private final EnumMap<ReportDataSourceType, ReportFillManager>
		_reportFillManagers = new EnumMap<>(ReportDataSourceType.class);
	private ServiceTrackerMap<ReportFormat, ReportFormatExporter>
		_serviceTrackerMap;

}