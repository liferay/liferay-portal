/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.designer.web.internal.portlet.configuration.icon;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.configuration.icon.BaseJSPPortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.workflow.kaleo.designer.web.constants.KaleoDesignerPortletKeys;
import com.liferay.portal.workflow.kaleo.designer.web.internal.constants.KaleoDesignerActionKeys;
import com.liferay.portal.workflow.kaleo.designer.web.internal.constants.KaleoDesignerWebKeys;
import com.liferay.portal.workflow.kaleo.designer.web.internal.permission.KaleoDesignerPermission;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.ServletContext;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Defines the icon triggering duplication of the workflow definition.
 *
 * @author Jeyvison Nascimento
 */
@Component(
	property = {
		"jakarta.portlet.name=" + KaleoDesignerPortletKeys.KALEO_DESIGNER,
		"path=/designer/edit_kaleo_definition_version.jsp"
	},
	service = PortletConfigurationIcon.class
)
public class DuplicateDefinitionPortletConfigurationIcon
	extends BaseJSPPortletConfigurationIcon {

	@Override
	public Map<String, Object> getContext(PortletRequest portletRequest) {
		return HashMapBuilder.<String, Object>put(
			"action", getNamespace(portletRequest) + "duplicateDefinition"
		).put(
			"globalAction", true
		).build();
	}

	@Override
	public String getJspPath() {
		return "/designer/configuration/icon/duplicate_definition.jsp";
	}

	@Override
	public String getMessage(PortletRequest portletRequest) {
		return _language.get(getLocale(portletRequest), "duplicate");
	}

	@Override
	public boolean isShow(PortletRequest portletRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (!_portletResourcePermission.contains(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getCompanyGroupId(), ActionKeys.VIEW)) {

			return false;
		}

		KaleoDefinitionVersion kaleoDefinitionVersion =
			getKaleoDefinitionVersion(portletRequest);

		if (kaleoDefinitionVersion == null) {
			return false;
		}

		return KaleoDesignerPermission.contains(
			themeDisplay.getPermissionChecker(),
			themeDisplay.getCompanyGroupId(),
			KaleoDesignerActionKeys.ADD_NEW_WORKFLOW);
	}

	protected KaleoDefinitionVersion getKaleoDefinitionVersion(
		PortletRequest portletRequest) {

		return (KaleoDefinitionVersion)portletRequest.getAttribute(
			KaleoDesignerWebKeys.KALEO_DRAFT_DEFINITION);
	}

	@Override
	protected ServletContext getServletContext() {
		return _servletContext;
	}

	@Reference
	private Language _language;

	@Reference(
		target = "(resource.name=" + WorkflowConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.portal.workflow.kaleo.designer.web)"
	)
	private ServletContext _servletContext;

}