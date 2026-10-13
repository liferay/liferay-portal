/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.jasper.internal;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StackTraceUtil;
import com.liferay.portal.kernel.util.StringUtil;
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
import com.liferay.portal.reports.engine.console.jasper.internal.configuration.JasperReportCompilerConfiguration;
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

import net.sf.jasperreports.compilers.ReportClassFilter;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.SimpleJasperReportsContext;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Michael C. Han
 * @author Brian Wing Shun Chan
 * @author Brian Greenwald
 */
@Component(
	configurationPid = "com.liferay.portal.reports.engine.console.jasper.internal.configuration.JasperReportCompilerConfiguration",
	service = ReportEngine.class
)
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
				jasperReport, _jasperReportsContext, reportRequest);

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
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

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

		_updateReportCompiler(properties);
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	@Modified
	protected void modified(Map<String, Object> properties) {
		_updateReportCompiler(properties);
	}

	private void _updateReportCompiler(Map<String, Object> properties) {
		SimpleJasperReportsContext simpleJasperReportsContext =
			new SimpleJasperReportsContext(
				DefaultJasperReportsContext.getInstance());

		JasperReportCompilerConfiguration jasperReportCompilerConfiguration =
			ConfigurableUtil.createConfigurable(
				JasperReportCompilerConfiguration.class, properties);

		simpleJasperReportsContext.setProperty(
			ReportClassFilter.PROPERTY_PREFIX_CLASS_FILTER_ENABLED,
			String.valueOf(jasperReportCompilerConfiguration.enabled()));
		simpleJasperReportsContext.setProperty(
			ReportClassFilter.PROPERTY_PREFIX_CLASS_WHITELIST + "liferay",
			StringUtil.merge(
				jasperReportCompilerConfiguration.allowedClassNames(),
				StringPool.COMMA));

		_jasperReportsContext = simpleJasperReportsContext;

		_reportCompiler = new CachedReportCompiler(
			new DefaultReportCompiler(simpleJasperReportsContext));
	}

	private Map<String, String> _engineParameters;
	private volatile JasperReportsContext _jasperReportsContext;
	private volatile ReportCompiler _reportCompiler;
	private final EnumMap<ReportDataSourceType, ReportFillManager>
		_reportFillManagers = new EnumMap<>(ReportDataSourceType.class);
	private ServiceTrackerMap<ReportFormat, ReportFormatExporter>
		_serviceTrackerMap;

}