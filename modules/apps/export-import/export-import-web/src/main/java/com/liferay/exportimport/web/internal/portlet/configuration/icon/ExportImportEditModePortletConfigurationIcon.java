/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.web.internal.portlet.configuration.icon;

import com.liferay.exportimport.kernel.lar.DefaultConfigurationPortletDataHandler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.configuration.icon.EditModePortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = EditModePortletConfigurationIcon.class)
public class ExportImportEditModePortletConfigurationIcon
	implements EditModePortletConfigurationIcon {

	@Override
	public String getIcon() {
		return "order-arrow";
	}

	@Override
	public String getTitle(HttpServletRequest httpServletRequest) {
		return _language.get(httpServletRequest, "export-import");
	}

	@Override
	public String getURL(
		HttpServletRequest httpServletRequest, String portletResource) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				httpServletRequest, PortletKeys.EXPORT_IMPORT,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/export_import/export_import"
		).setRedirect(
			themeDisplay.getURLCurrent()
		).setPortletResource(
			portletResource
		).setParameter(
			"returnToFullPageURL", themeDisplay.getURLCurrent()
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	@Override
	public boolean isShow(
		HttpServletRequest httpServletRequest, String portletResource) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Portlet portlet = _portletLocalService.getPortletById(
			themeDisplay.getCompanyId(), portletResource);

		try {
			if (!PortletPermissionUtil.contains(
					themeDisplay.getPermissionChecker(),
					themeDisplay.getScopeGroupId(), themeDisplay.getLayout(),
					portlet, ActionKeys.CONFIGURATION) &&
				(portlet.getConfigurationActionInstance() == null) &&
				(portlet.getPortletDataHandlerInstance() instanceof
					DefaultConfigurationPortletDataHandler)) {

				return false;
			}

			return GroupPermissionUtil.contains(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroup(),
				ActionKeys.EXPORT_IMPORT_PORTLET_INFO);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ExportImportEditModePortletConfigurationIcon.class);

	@Reference
	private Language _language;

	@Reference
	private PortletLocalService _portletLocalService;

}