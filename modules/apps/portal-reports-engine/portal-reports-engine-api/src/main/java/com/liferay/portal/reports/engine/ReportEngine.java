/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine;

import jakarta.servlet.ServletContext;

import java.util.Map;

/**
 * @author Michael C. Han
 */
public interface ReportEngine {

	public void compile(ReportRequest reportRequest)
		throws ReportGenerationException;

	public void destroy();

	public void execute(
			ReportRequest reportRequest,
			ReportResultContainer reportResultContainer)
		throws ReportGenerationException;

	public Map<String, String> getEngineParameters();

	public void init(ServletContext servletContext);

	public void setEngineParameters(Map<String, String> engineParameters);

}