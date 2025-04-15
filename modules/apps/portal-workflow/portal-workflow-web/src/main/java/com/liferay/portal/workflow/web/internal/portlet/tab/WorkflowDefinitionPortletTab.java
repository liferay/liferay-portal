/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.web.internal.portlet.tab;

import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.workflow.constants.WorkflowWebKeys;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;
import com.liferay.portal.workflow.portlet.tab.BaseWorkflowPortletTab;
import com.liferay.portal.workflow.portlet.tab.WorkflowPortletTab;
import com.liferay.portal.workflow.web.internal.display.context.WorkflowDefinitionDisplayContext;
import com.liferay.portal.workflow.web.internal.request.preprocessor.helper.WorkflowPreprocessorHelper;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.ServletContext;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adam Brandizzi
 */
@Component(
	property = "portal.workflow.tabs.name=" + WorkflowWebKeys.WORKFLOW_TAB_DEFINITION,
	service = WorkflowPortletTab.class
)
public class WorkflowDefinitionPortletTab extends BaseWorkflowPortletTab {

	@Override
	public String getName() {
		return WorkflowWebKeys.WORKFLOW_TAB_DEFINITION;
	}

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public void prepareRender(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			String path = workflowPreprocessorHelper.getPath(
				renderRequest, renderResponse);

			WorkflowDefinitionDisplayContext displayContext =
				new WorkflowDefinitionDisplayContext(
					ctEntryLocalService, portal, _portletResourcePermission,
					renderRequest,
					ResourceBundleLoaderUtil.getPortalResourceBundleLoader(),
					userLocalService);

			renderRequest.setAttribute(
				WorkflowWebKeys.WORKFLOW_DEFINITION_DISPLAY_CONTEXT,
				displayContext);

			if (Objects.equals(
					path, "/definition/edit_workflow_definition.jsp") ||
				Objects.equals(
					path, "/definition/view_workflow_definition.jsp")) {

				_setWorkflowDefinitionRenderRequestAttribute(renderRequest);
			}
		}
		catch (Exception exception) {
			if (workflowPreprocessorHelper.isSessionErrorException(exception)) {
				workflowPreprocessorHelper.hideDefaultErrorMessage(
					renderRequest);

				SessionErrors.add(renderRequest, exception.getClass());
			}
			else {
				throw new PortletException(exception);
			}
		}
	}

	@Override
	protected String getJspPath() {
		return "/definition/view.jsp";
	}

	@Reference
	protected CTEntryLocalService ctEntryLocalService;

	@Reference
	protected Portal portal;

	@Reference
	protected UserLocalService userLocalService;

	@Reference
	protected WorkflowPreprocessorHelper workflowPreprocessorHelper;

	private void _setWorkflowDefinitionRenderRequestAttribute(
			RenderRequest renderRequest)
		throws PortalException {

		String name = ParamUtil.getString(renderRequest, "name");

		if (Validator.isNull(name)) {
			return;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		int version = ParamUtil.getInteger(renderRequest, "version");

		renderRequest.setAttribute(
			WebKeys.WORKFLOW_DEFINITION,
			_workflowDefinitionManager.liberalGetWorkflowDefinition(
				themeDisplay.getCompanyId(), name, version));
	}

	@Reference(
		target = "(resource.name=" + WorkflowConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.portal.workflow.web)"
	)
	private ServletContext _servletContext;

	@Reference
	private WorkflowDefinitionManager _workflowDefinitionManager;

}