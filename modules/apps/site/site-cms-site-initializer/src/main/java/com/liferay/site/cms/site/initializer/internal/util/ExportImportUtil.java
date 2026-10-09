/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.util;

import com.liferay.asset.categories.admin.web.constants.AssetCategoriesAdminPortletKeys;
import com.liferay.asset.tags.constants.AssetTagsAdminPortletKeys;
import com.liferay.exportimport.constants.ExportImportPortletKeys;
import com.liferay.exportimport.kernel.lar.DefaultConfigurationPortletDataHandler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Jürgen Kappler
 */
public class ExportImportUtil {

	public static JSONObject getActionItemJSONObject(
		HttpServletRequest httpServletRequest, String labelKey,
		String portletResource, String symbolLeft, String tabs2,
		String titleKey, ThemeDisplay themeDisplay) {

		if (!_hasConfigurationPermission(portletResource, themeDisplay)) {
			return null;
		}

		return JSONUtil.put(
			"href",
			_getControlPanelPortletURL(
				themeDisplay.getScopeGroup(), httpServletRequest,
				portletResource, tabs2)
		).put(
			"label", LanguageUtil.get(httpServletRequest, labelKey)
		).put(
			"redirect", themeDisplay.getURLCurrent()
		).put(
			"symbolLeft", symbolLeft
		).put(
			"target", "modal"
		).put(
			"title", LanguageUtil.get(httpServletRequest, titleKey)
		);
	}

	public static JSONObject getActionItemJSONObject(
		HttpServletRequest httpServletRequest, String languageKey,
		String portletResource, ThemeDisplay themeDisplay) {

		if (!_hasConfigurationPermission(portletResource, themeDisplay)) {
			return null;
		}

		return JSONUtil.put(
			"href",
			_getControlPanelPortletURL(
				themeDisplay.getScopeGroup(), httpServletRequest,
				portletResource, null)
		).put(
			"label", LanguageUtil.get(httpServletRequest, languageKey)
		).put(
			"redirect", themeDisplay.getURLCurrent()
		).put(
			"target", "modal"
		);
	}

	public static JSONArray getCategorizationActionItemsJSONArray(
		HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay) {

		if (FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-57655")) {

			return _putAll(
				getExportActionItemJSONObject(
					httpServletRequest,
					AssetCategoriesAdminPortletKeys.ASSET_CATEGORIES_ADMIN,
					"export-vocabularies", themeDisplay),
				getImportActionItemJSONObject(
					httpServletRequest,
					AssetCategoriesAdminPortletKeys.ASSET_CATEGORIES_ADMIN,
					"import-vocabularies", themeDisplay),
				getExportActionItemJSONObject(
					httpServletRequest,
					AssetTagsAdminPortletKeys.ASSET_TAGS_ADMIN, "export-tags",
					themeDisplay),
				getImportActionItemJSONObject(
					httpServletRequest,
					AssetTagsAdminPortletKeys.ASSET_TAGS_ADMIN, "import-tags",
					themeDisplay));
		}

		return _putAll(
			getActionItemJSONObject(
				httpServletRequest, "export-import-vocabularies",
				AssetCategoriesAdminPortletKeys.ASSET_CATEGORIES_ADMIN,
				themeDisplay),
			getActionItemJSONObject(
				httpServletRequest, "export-import-tags",
				AssetTagsAdminPortletKeys.ASSET_TAGS_ADMIN, themeDisplay));
	}

	public static JSONObject getExportActionItemJSONObject(
		HttpServletRequest httpServletRequest, String portletResource,
		String titleKey, ThemeDisplay themeDisplay) {

		if (FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-57655")) {

			return _getActionItemJSONObject(
				httpServletRequest, "/revamp/export/view_export.jsp",
				portletResource, "export", titleKey, themeDisplay);
		}

		return getActionItemJSONObject(
			httpServletRequest, "export", portletResource, "export", "export",
			titleKey, themeDisplay);
	}

	public static JSONObject getImportActionItemJSONObject(
		HttpServletRequest httpServletRequest, String portletResource,
		String titleKey, ThemeDisplay themeDisplay) {

		if (FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-57655")) {

			return _getActionItemJSONObject(
				httpServletRequest, "/revamp/import/view_import.jsp",
				portletResource, "import", titleKey, themeDisplay);
		}

		return getActionItemJSONObject(
			httpServletRequest, "import", portletResource, "import", "import",
			titleKey, themeDisplay);
	}

	private static JSONObject _getActionItemJSONObject(
		HttpServletRequest httpServletRequest, String mvcPath,
		String portletResource, String symbolLeft, String titleKey,
		ThemeDisplay themeDisplay) {

		if (!_hasConfigurationPermission(portletResource, themeDisplay)) {
			return null;
		}

		String title = LanguageUtil.get(httpServletRequest, titleKey);

		return JSONUtil.put(
			"href",
			PortletURLBuilder.create(
				_getPortletURL(themeDisplay.getScopeGroup(), httpServletRequest)
			).setMVCPath(
				mvcPath
			).setRedirect(
				PortalUtil.getCurrentURL(httpServletRequest)
			).setPortletResource(
				portletResource
			).buildString()
		).put(
			"label", title
		).put(
			"symbolLeft", symbolLeft
		).put(
			"title", title
		);
	}

	private static String _getControlPanelPortletURL(
		Group group, HttpServletRequest httpServletRequest,
		String portletResource, String tabs2) {

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
		).setTabs2(
			tabs2
		).setParameter(
			"returnToFullPageURL", PortalUtil.getCurrentURL(httpServletRequest)
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	private static PortletURL _getPortletURL(
		Group group, HttpServletRequest httpServletRequest) {

		Layout layout = LayoutLocalServiceUtil.fetchLayoutByFriendlyURL(
			group.getGroupId(), false, "/export-import");

		if (layout == null) {
			return PortalUtil.getControlPanelPortletURL(
				httpServletRequest, group,
				ExportImportPortletKeys.EXPORT_IMPORT, 0, 0,
				PortletRequest.RENDER_PHASE);
		}

		return PortletURLFactoryUtil.create(
			httpServletRequest, ExportImportPortletKeys.EXPORT_IMPORT, layout,
			PortletRequest.RENDER_PHASE);
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

	private static JSONArray _putAll(JSONObject... jsonObjects) {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		for (JSONObject jsonObject : jsonObjects) {
			if (jsonObject == null) {
				continue;
			}

			jsonArray.put(jsonObject);
		}

		return jsonArray;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ExportImportUtil.class);

}