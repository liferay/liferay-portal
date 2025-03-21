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
public class EntrySearchTerms extends EntryDisplayTerms {

	public EntrySearchTerms(PortletRequest portletRequest) {
		super(portletRequest);

		definitionName = DAOParamUtil.getString(
			portletRequest, DEFINITION_NAME);
		endDateDay = DAOParamUtil.getInteger(portletRequest, END_DATE_DAY);
		endDateMonth = DAOParamUtil.getInteger(portletRequest, END_DATE_MONTH);
		endDateYear = DAOParamUtil.getInteger(portletRequest, END_DATE_YEAR);
		startDateDay = DAOParamUtil.getInteger(portletRequest, START_DATE_DAY);
		startDateMonth = DAOParamUtil.getInteger(
			portletRequest, START_DATE_MONTH);
		startDateYear = DAOParamUtil.getInteger(
			portletRequest, START_DATE_YEAR);
		userName = DAOParamUtil.getString(portletRequest, USER_NAME);
	}

}