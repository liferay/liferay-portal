/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.designer.web.internal.search;

import com.liferay.portal.kernel.dao.search.DAOParamUtil;

import jakarta.portlet.PortletRequest;

/**
 * @author Inácio Nery
 */
public class KaleoDefinitionVersionSearchTerms
	extends KaleoDefinitionVersionDisplayTerms {

	public KaleoDefinitionVersionSearchTerms(PortletRequest portletRequest) {
		super(portletRequest);

		description = DAOParamUtil.getString(portletRequest, DESCRIPTION);
		title = DAOParamUtil.getString(portletRequest, TITLE);
	}

}