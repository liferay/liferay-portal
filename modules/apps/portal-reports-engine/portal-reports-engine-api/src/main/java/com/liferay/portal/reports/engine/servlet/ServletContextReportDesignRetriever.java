/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.servlet;

import com.liferay.portal.reports.engine.ReportDesignRetriever;

import jakarta.servlet.ServletContext;

import java.io.InputStream;

import java.util.Date;

/**
 * @author Michael C. Han
 */
public class ServletContextReportDesignRetriever
	implements ReportDesignRetriever {

	public ServletContextReportDesignRetriever(
		ServletContext servletContext, String reportName, String prefix,
		String postfix) {

		_servletContext = servletContext;
		_reportName = reportName;
		_prefix = prefix;
		_postfix = postfix;
	}

	@Override
	public InputStream getInputStream() {
		return _servletContext.getResourceAsStream(
			_prefix + _reportName + _postfix);
	}

	@Override
	public Date getModifiedDate() {
		return new Date();
	}

	@Override
	public String getReportName() {
		return _reportName;
	}

	private final String _postfix;
	private final String _prefix;
	private final String _reportName;
	private final ServletContext _servletContext;

}