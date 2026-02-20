/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.util;

import com.liferay.exportimport.constants.ExportImportPortletKeys;
import com.liferay.exportimport.kernel.lar.DefaultConfigurationPortletDataHandler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Jürgen Kappler
 */
public class ExportImportUtil {

	public static JSONObject getActionItemJSONObject(
		HttpServletRequest httpServletRequest, String languageKey,
		String portletResource, ThemeDisplay themeDisplay) {

		if (!_hasConfigurationPermission(portletResource, themeDisplay)) {
			return null;
		}

		try {
			return JSONUtil.put(
				"href",
				_getControlPanelPortletURL(
					themeDisplay.getScopeGroup(), httpServletRequest,
					portletResource)
			).put(
				"label", LanguageUtil.get(httpServletRequest, languageKey)
			).put(
				"redirect", themeDisplay.getURLCurrent()
			).put(
				"target", "modal"
			);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return null;
		}
	}

	private static String _getControlPanelPortletURL(
			Group group, HttpServletRequest httpServletRequest,
			String portletResource)
		throws Exception {

		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				httpServletRequest, group,
				ExportImportPortletKeys.EXPORT_IMPORT, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/export_import/export_import"
		).setRedirect(
			PortalUtil.getCurrentURL(httpServletRequest)
		).setPortletResource(
			portletResource
		).setParameter(
			"returnToFullPageURL", PortalUtil.getCurrentURL(httpServletRequest)
		).setParameter(
			"sourceModule", "site-cms-site-initializer"
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	private static boolean _hasConfigurationPermission(
		String portletResource, ThemeDisplay themeDisplay) {

		try {
			Portlet portlet = PortletLocalServiceUtil.getPortletById(
				themeDisplay.getCompanyId(), portletResource);

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

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ExportImportUtil.class);

}