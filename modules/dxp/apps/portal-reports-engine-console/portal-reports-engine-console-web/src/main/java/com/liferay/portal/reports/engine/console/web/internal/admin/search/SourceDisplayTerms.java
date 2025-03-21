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
public class SourceDisplayTerms extends DisplayTerms {

	public static final String DRIVER_URL = "driverUrl";

	public static final String NAME = "name";

	public SourceDisplayTerms(PortletRequest portletRequest) {
		super(portletRequest);

		name = ParamUtil.getString(portletRequest, NAME);
		driverUrl = ParamUtil.getString(portletRequest, DRIVER_URL);
	}

	public String getDriverUrl() {
		return driverUrl;
	}

	public String getName() {
		return name;
	}

	protected String driverUrl;
	protected String name;

}