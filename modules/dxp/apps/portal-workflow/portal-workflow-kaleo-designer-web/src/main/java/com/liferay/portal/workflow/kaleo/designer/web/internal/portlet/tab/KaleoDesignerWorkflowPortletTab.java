/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.designer.web.internal.portlet.tab;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.security.script.management.configuration.helper.ScriptManagementConfigurationHelper;
import com.liferay.portal.workflow.constants.WorkflowWebKeys;
import com.liferay.portal.workflow.kaleo.designer.web.internal.constants.KaleoDesignerWebKeys;
import com.liferay.portal.workflow.kaleo.designer.web.internal.portlet.display.context.KaleoDesignerDisplayContext;
import com.liferay.portal.workflow.kaleo.exception.DuplicateKaleoDefinitionNameException;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;
import com.liferay.portal.workflow.kaleo.runtime.action.ActionExecutorManager;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionVersionLocalService;
import com.liferay.portal.workflow.portlet.tab.BaseWorkflowPortletTab;
import com.liferay.portal.workflow.portlet.tab.WorkflowPortletTab;

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
	property = {
		"portal.workflow.tabs.name=" + WorkflowWebKeys.WORKFLOW_TAB_DEFINITION,
		"service.ranking:Integer=100"
	},
	service = WorkflowPortletTab.class
)
public class KaleoDesignerWorkflowPortletTab extends BaseWorkflowPortletTab {

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

		if (!SessionErrors.contains(
				renderRequest, DuplicateKaleoDefinitionNameException.class)) {

			try {
				_setKaleoDefinitionVersionRenderRequestAttribute(renderRequest);

				_setKaleoDesignerServletContextRequestAttribute(renderRequest);
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}
	}

	@Override
	protected String getJspPath() {
		return "/designer/view_workflow_definitions.jsp";
	}

	private void _setKaleoDefinitionVersionRenderRequestAttribute(
			RenderRequest renderRequest)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		renderRequest.setAttribute(
			KaleoDesignerWebKeys.KALEO_DESIGNER_DISPLAY_CONTEXT,
			new KaleoDesignerDisplayContext(
				_actionExecutorManager, renderRequest,
				_kaleoDefinitionVersionLocalService, _portletResourcePermission,
				ResourceBundleLoaderUtil.getPortalResourceBundleLoader(),
				_scriptManagementConfigurationHelper, _userLocalService));

		String name = ParamUtil.getString(renderRequest, "name");

		if (Validator.isNull(name)) {
			return;
		}

		KaleoDefinitionVersion kaleoDefinitionVersion = null;

		String draftVersion = ParamUtil.getString(
			renderRequest, "draftVersion");

		if (Validator.isNull(draftVersion)) {
			kaleoDefinitionVersion =
				_kaleoDefinitionVersionLocalService.
					fetchLatestKaleoDefinitionVersion(
						themeDisplay.getCompanyId(), name, null);
		}
		else {
			kaleoDefinitionVersion =
				_kaleoDefinitionVersionLocalService.getKaleoDefinitionVersion(
					themeDisplay.getCompanyId(), name, draftVersion);
		}

		renderRequest.setAttribute(
			KaleoDesignerWebKeys.KALEO_DRAFT_DEFINITION,
			kaleoDefinitionVersion);
	}

	private void _setKaleoDesignerServletContextRequestAttribute(
		RenderRequest renderRequest) {

		renderRequest.setAttribute(
			"portletTabServletContext", getServletContext());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		KaleoDesignerWorkflowPortletTab.class);

	@Reference
	private ActionExecutorManager _actionExecutorManager;

	@Reference
	private KaleoDefinitionVersionLocalService
		_kaleoDefinitionVersionLocalService;

	@Reference(
		target = "(resource.name=" + WorkflowConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

	@Reference
	private ScriptManagementConfigurationHelper
		_scriptManagementConfigurationHelper;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.portal.workflow.kaleo.designer.web)"
	)
	private ServletContext _servletContext;

	@Reference
	private UserLocalService _userLocalService;

}