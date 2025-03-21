/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.web.internal.admin.search;

import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.PortletRequest;

/**
 * @author Rafael Praxedes
 */
public class DefinitionDisplayTerms extends DisplayTerms {

	public static final String DEFINITION_NAME = "definitionName";

	public static final String DESCRIPTION = "description";

	public static final String REPORT_NAME = "reportName";

	public static final String SOURCE_ID = "sourceId";

	public DefinitionDisplayTerms(PortletRequest portletRequest) {
		super(portletRequest);

		definitionName = ParamUtil.getString(portletRequest, DEFINITION_NAME);
		description = ParamUtil.getString(portletRequest, DESCRIPTION);
		reportName = ParamUtil.getString(portletRequest, REPORT_NAME);
		sourceId = ParamUtil.getString(portletRequest, SOURCE_ID);
	}

	public String getDefinitionName() {
		return definitionName;
	}

	public String getDescription() {
		return description;
	}

	public String getReportName() {
		return reportName;
	}

	public String getSourceId() {
		return sourceId;
	}

	protected String definitionName;
	protected String description;
	protected String reportName;
	protected String sourceId;

}