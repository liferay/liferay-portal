/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.jasper.internal.fill.manager;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.reports.engine.ReportRequest;
import com.liferay.portal.reports.engine.ReportRequestContext;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.sql.Connection;

import java.text.DateFormat;
import java.text.ParseException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperReportsContext;

/**
 * @author Gavin Wan
 * @author Brian Wing Shun Chan
 */
public abstract class BaseReportFillManager implements ReportFillManager {

	@Override
	public JasperPrint fillReport(
			JasperReport jasperReport,
			JasperReportsContext jasperReportsContext,
			ReportRequest reportRequest)
		throws JRException {

		Connection connection = null;

		try {
			connection = getConnection(reportRequest);
		}
		catch (JRException jrException) {
			throw jrException;
		}
		catch (Exception exception) {
			throw new JRException(exception);
		}

		JasperFillManager jasperFillManager = JasperFillManager.getInstance(
			jasperReportsContext);

		Map<String, Object> reportParameters = _getReportParameters(
			jasperReport, reportRequest);

		if (connection != null) {
			try {
				return jasperFillManager.fill(
					jasperReport, reportParameters, connection);
			}
			finally {
				DataAccess.cleanUp(connection);
			}
		}

		JRDataSource jrDataSource = null;

		try {
			jrDataSource = getJRDataSource(reportRequest);
		}
		catch (JRException jrException) {
			throw jrException;
		}
		catch (Exception exception) {
			throw new JRException(exception);
		}

		if (jrDataSource != null) {
			return jasperFillManager.fill(
				jasperReport, reportParameters, jrDataSource);
		}

		return jasperFillManager.fill(jasperReport, reportParameters);
	}

	protected Connection getConnection(ReportRequest reportRequest)
		throws Exception {

		return null;
	}

	protected InputStream getDataSourceByteArrayInputStream(
		ReportRequest reportRequest) {

		ReportRequestContext reportRequestContext =
			reportRequest.getReportRequestContext();

		byte[] dataSourceByteArray = (byte[])reportRequestContext.getAttribute(
			ReportRequestContext.DATA_SOURCE_BYTE_ARRAY);

		return new ByteArrayInputStream(dataSourceByteArray);
	}

	protected String getDataSourceCharSet(ReportRequest reportRequest) {
		ReportRequestContext reportRequestContext =
			reportRequest.getReportRequestContext();

		return GetterUtil.getString(
			(String)reportRequestContext.getAttribute(
				ReportRequestContext.DATA_SOURCE_CHARSET),
			StringPool.UTF8);
	}

	protected String[] getDataSourceColumnNames(ReportRequest reportRequest) {
		ReportRequestContext reportRequestContext =
			reportRequest.getReportRequestContext();

		String dataSourceColumnNames =
			(String)reportRequestContext.getAttribute(
				ReportRequestContext.DATA_SOURCE_COLUMN_NAMES);

		if (Validator.isNotNull(dataSourceColumnNames)) {
			return StringUtil.split(dataSourceColumnNames, StringPool.COMMA);
		}

		return null;
	}

	protected JRDataSource getJRDataSource(ReportRequest reportRequest)
		throws Exception {

		return null;
	}

	private Map<String, Object> _getReportParameters(
		JasperReport jasperReport, ReportRequest reportRequest) {

		Map<String, Object> reportParameters = new HashMap<>();

		Map<String, String> stringReportParameters =
			reportRequest.getReportParameters();

		JRParameter[] jrParameters = jasperReport.getParameters();

		for (JRParameter jrParameter : jrParameters) {
			String name = jrParameter.getName();
			Class<?> clazz = jrParameter.getValueClass();

			String stringValue = stringReportParameters.get(name);

			Object value = stringValue;

			if (clazz.equals(Date.class)) {
				DateFormat dateFormat =
					DateFormatFactoryUtil.getSimpleDateFormat("yyyy-MM-dd");

				try {
					value = dateFormat.parse(stringValue);
				}
				catch (ParseException parseException) {
					_log.error(
						stringValue + " is not yyyy-MM-dd", parseException);
				}
			}
			else if (clazz.equals(List.class)) {
				List<String> listValue = ListUtil.fromArray(
					StringUtil.split(stringValue));

				value = new ArrayList<>(listValue);
			}

			if (value == null) {
				continue;
			}

			reportParameters.put(name, value);
		}

		return reportParameters;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseReportFillManager.class);

}