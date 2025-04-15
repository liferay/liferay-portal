/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.configuration.web.internal.portlet.configuration.icon;

import com.liferay.exportimport.constants.ExportImportPortletKeys;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.configuration.icon.BaseJSPPortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;
import com.liferay.staging.constants.StagingConfigurationPortletKeys;
import com.liferay.staging.constants.StagingProcessesPortletKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.ServletContext;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = PortletConfigurationIcon.class)
public class StagingPortletConfigurationIcon
	extends BaseJSPPortletConfigurationIcon {

	@Override
	public Map<String, Object> getContext(PortletRequest portletRequest) {
		return HashMapBuilder.<String, Object>put(
			"action", getNamespace(portletRequest) + "staging"
		).put(
			"globalAction", true
		).build();
	}

	@Override
	public String getIconCssClass() {
		return "portlet-export-import portlet-export-import-icon";
	}

	@Override
	public String getJspPath() {
		return "/configuration/icon/staging.jsp";
	}

	@Override
	public String getMessage(PortletRequest portletRequest) {
		return _language.get(getLocale(portletRequest), "staging");
	}

	@Override
	public double getWeight() {
		return 16.0;
	}

	@Override
	public boolean isShow(PortletRequest portletRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Group group = themeDisplay.getSiteGroup();

		if (group.hasLocalOrRemoteStagingGroup() &&
			!PropsValues.STAGING_LIVE_GROUP_REMOTE_STAGING_ENABLED) {

			return false;
		}

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		String rootPortletId = portletDisplay.getRootPortletId();

		if (rootPortletId.equals(ExportImportPortletKeys.EXPORT) ||
			rootPortletId.equals(ExportImportPortletKeys.EXPORT_IMPORT) ||
			rootPortletId.equals(ExportImportPortletKeys.IMPORT) ||
			rootPortletId.equals(
				StagingConfigurationPortletKeys.STAGING_CONFIGURATION) ||
			rootPortletId.equals(
				StagingProcessesPortletKeys.STAGING_PROCESSES) ||
			!portletDisplay.isShowStagingIcon()) {

			return false;
		}

		Portlet portlet = _portletLocalService.getPortletById(
			portletDisplay.getId());

		PortletDataHandler portletDataHandler =
			portlet.getPortletDataHandlerInstance();

		if ((portletDataHandler == null) ||
			!portletDataHandler.isConfigurationEnabled()) {

			return false;
		}

		try {
			return GroupPermissionUtil.contains(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroup(), ActionKeys.PUBLISH_PORTLET_INFO);
		}
		catch (PortalException portalException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return false;
		}
	}

	@Override
	protected ServletContext getServletContext() {
		return _servletContext;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		StagingPortletConfigurationIcon.class);

	@Reference
	private Language _language;

	@Reference
	private PortletLocalService _portletLocalService;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.staging.configuration.web)"
	)
	private ServletContext _servletContext;

}