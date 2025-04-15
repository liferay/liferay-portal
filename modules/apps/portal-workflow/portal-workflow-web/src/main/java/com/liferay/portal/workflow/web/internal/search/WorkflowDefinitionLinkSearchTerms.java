/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.web.internal.search;

import com.liferay.portal.kernel.dao.search.DAOParamUtil;

import jakarta.portlet.PortletRequest;

/**
 * @author Leonardo Barros
 */
public class WorkflowDefinitionLinkSearchTerms
	extends WorkflowDefinitionLinkDisplayTerms {

	public WorkflowDefinitionLinkSearchTerms(PortletRequest portletRequest) {
		super(portletRequest);

		resource = DAOParamUtil.getString(portletRequest, RESOURCE);
		workflow = DAOParamUtil.getString(portletRequest, WORKFLOW);
	}

}