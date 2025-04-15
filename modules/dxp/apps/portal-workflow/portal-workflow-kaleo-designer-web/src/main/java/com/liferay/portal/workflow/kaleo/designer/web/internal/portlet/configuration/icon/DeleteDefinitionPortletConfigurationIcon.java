/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.designer.web.internal.portlet.configuration.icon;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.configuration.icon.BaseJSPPortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.workflow.kaleo.designer.web.constants.KaleoDesignerPortletKeys;
import com.liferay.portal.workflow.kaleo.designer.web.internal.constants.KaleoDesignerWebKeys;
import com.liferay.portal.workflow.kaleo.designer.web.internal.permission.KaleoDefinitionVersionPermission;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.ServletContext;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides the configuration icon for deleting a workflow definition.
 *
 * @author Adam Brandizzi
 */
@Component(
	property = {
		"jakarta.portlet.name=" + KaleoDesignerPortletKeys.KALEO_DESIGNER,
		"path=/designer/edit_kaleo_definition_version.jsp"
	},
	service = PortletConfigurationIcon.class
)
public class DeleteDefinitionPortletConfigurationIcon
	extends BaseJSPPortletConfigurationIcon {

	@Override
	public Map<String, Object> getContext(PortletRequest portletRequest) {
		return HashMapBuilder.<String, Object>put(
			"action", getNamespace(portletRequest) + "deleteDefinition"
		).put(
			"globalAction", true
		).build();
	}

	@Override
	public String getJspPath() {
		return "/designer/configuration/icon/delete_definition.jsp";
	}

	@Override
	public String getMessage(PortletRequest portletRequest) {
		return _language.get(getLocale(portletRequest), "delete");
	}

	@Override
	public boolean isShow(PortletRequest portletRequest) {
		KaleoDefinitionVersion kaleoDefinitionVersion =
			(KaleoDefinitionVersion)portletRequest.getAttribute(
				KaleoDesignerWebKeys.KALEO_DRAFT_DEFINITION);

		if (kaleoDefinitionVersion == null) {
			return false;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		try {
			boolean hasDeletePermission =
				KaleoDefinitionVersionPermission.contains(
					themeDisplay.getPermissionChecker(), kaleoDefinitionVersion,
					ActionKeys.DELETE);

			KaleoDefinition kaleoDefinition =
				kaleoDefinitionVersion.getKaleoDefinition();

			if (hasDeletePermission &&
				(!kaleoDefinition.isActive() ||
				 kaleoDefinitionVersion.isDraft())) {

				return true;
			}

			return false;
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return false;
	}

	@Override
	protected ServletContext getServletContext() {
		return _servletContext;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DeleteDefinitionPortletConfigurationIcon.class);

	@Reference
	private Language _language;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.portal.workflow.kaleo.designer.web)"
	)
	private ServletContext _servletContext;

}