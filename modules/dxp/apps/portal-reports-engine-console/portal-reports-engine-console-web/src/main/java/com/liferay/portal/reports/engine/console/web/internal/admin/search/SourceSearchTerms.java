/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.web.internal.admin.search;

import com.liferay.portal.kernel.dao.search.DAOParamUtil;

import jakarta.portlet.PortletRequest;

/**
 * @author Rafael Praxedes
 */
public class SourceSearchTerms extends SourceDisplayTerms {

	public SourceSearchTerms(PortletRequest portletRequest) {
		super(portletRequest);

		name = DAOParamUtil.getString(portletRequest, NAME);
		driverUrl = DAOParamUtil.getString(portletRequest, DRIVER_URL);
	}

}