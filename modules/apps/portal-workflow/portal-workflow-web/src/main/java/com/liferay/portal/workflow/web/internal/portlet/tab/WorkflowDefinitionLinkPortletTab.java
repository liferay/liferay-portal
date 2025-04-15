/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.web.internal.portlet.tab;

import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.workflow.WorkflowHandlerVisibleFilter;
import com.liferay.portal.workflow.comparator.WorkflowComparatorFactory;
import com.liferay.portal.workflow.constants.WorkflowWebKeys;
import com.liferay.portal.workflow.portlet.tab.BaseWorkflowPortletTab;
import com.liferay.portal.workflow.portlet.tab.WorkflowPortletTab;
import com.liferay.portal.workflow.web.internal.display.context.WorkflowDefinitionLinkDisplayContext;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adam Brandizzi
 */
@Component(
	property = "portal.workflow.tabs.name=" + WorkflowWebKeys.WORKFLOW_TAB_DEFINITION_LINK,
	service = WorkflowPortletTab.class
)
public class WorkflowDefinitionLinkPortletTab extends BaseWorkflowPortletTab {

	@Override
	public String getName() {
		return WorkflowWebKeys.WORKFLOW_TAB_DEFINITION_LINK;
	}

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public void prepareRender(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		WorkflowDefinitionLinkDisplayContext displayContext =
			new WorkflowDefinitionLinkDisplayContext(
				renderRequest, renderResponse,
				workflowDefinitionLinkLocalService,
				ResourceBundleLoaderUtil.getPortalResourceBundleLoader(),
				_workflowHandlerVisibleFilterSnapshot.get(),
				_workflowComparatorFactory);

		renderRequest.setAttribute(
			WorkflowWebKeys.WORKFLOW_DEFINITION_LINK_DISPLAY_CONTEXT,
			displayContext);
	}

	@Override
	protected String getJspPath() {
		return "/definition_link/view.jsp";
	}

	@Reference(unbind = "-")
	protected WorkflowDefinitionLinkLocalService
		workflowDefinitionLinkLocalService;

	private static final Snapshot<WorkflowHandlerVisibleFilter>
		_workflowHandlerVisibleFilterSnapshot = new Snapshot<>(
			WorkflowDefinitionLinkPortletTab.class,
			WorkflowHandlerVisibleFilter.class, null, true);

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.portal.workflow.web)"
	)
	private ServletContext _servletContext;

	@Reference
	private WorkflowComparatorFactory _workflowComparatorFactory;

}