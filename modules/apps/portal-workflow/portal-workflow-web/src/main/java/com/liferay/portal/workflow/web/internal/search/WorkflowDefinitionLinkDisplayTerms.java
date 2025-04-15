/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.web.internal.search;

import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.PortletRequest;

/**
 * @author Leonardo Barros
 */
public class WorkflowDefinitionLinkDisplayTerms extends DisplayTerms {

	public static final String RESOURCE = "resource";

	public static final String WORKFLOW = "workflow";

	public WorkflowDefinitionLinkDisplayTerms(PortletRequest portletRequest) {
		super(portletRequest);

		resource = ParamUtil.getString(portletRequest, RESOURCE);
		workflow = ParamUtil.getString(portletRequest, WORKFLOW);
	}

	public String getResource() {
		return resource;
	}

	public String getWorkflow() {
		return workflow;
	}

	protected String resource;
	protected String workflow;

}