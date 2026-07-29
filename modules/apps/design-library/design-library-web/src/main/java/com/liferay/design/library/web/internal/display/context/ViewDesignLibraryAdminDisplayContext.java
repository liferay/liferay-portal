/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.design.library.web.internal.display.context;

import com.liferay.depot.constants.DepotActionKeys;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.exportimport.constants.ExportImportPortletKeys;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Mario Leandro
 */
public class ViewDesignLibraryAdminDisplayContext {

	public ViewDesignLibraryAdminDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		_httpServletRequest = httpServletRequest;
		_liferayPortletResponse = liferayPortletResponse;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getAPIURL() {
		return "/o/headless-asset-library/v1.0/asset-libraries?filter=type " +
			"eq 'DesignLibrary'";
	}

	public Map<String, Object> getBreadcrumbProps() {
		return HashMapBuilder.<String, Object>put(
			"breadcrumbItems", _getBreadcrumbItemsJSONArray()
		).build();
	}

	public Map<String, Object> getEmptyState() {
		return HashMapBuilder.<String, Object>put(
			"description",
			() -> {
				if (_hasAddDepotEntryPermission()) {
					return LanguageUtil.get(
						_httpServletRequest,
						"click-new-to-create-your-first-design-library");
				}

				return StringPool.BLANK;
			}
		).put(
			"image", "/states/design_library_empty_state.svg"
		).put(
			"title",
			LanguageUtil.get(_httpServletRequest, "no-design-libraries-yet")
		).build();
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				PortletURLBuilder.createActionURL(
					_liferayPortletResponse
				).setMVCRenderCommandName(
					"/design_library/view_resources_design_library"
				).setParameter(
					"designLibraryEntryId", "{id}"
				).buildString(),
				"pencil", "edit", LanguageUtil.get(_httpServletRequest, "edit"),
				null, null, "link"),
			new FDSActionDropdownItem(
				_getExportImportPortletURL(ExportImportPortletKeys.EXPORT),
				"export", "export",
				LanguageUtil.get(_httpServletRequest, "export"), "get",
				"update", null),
			new FDSActionDropdownItem(
				_getExportImportPortletURL(ExportImportPortletKeys.IMPORT),
				"import", "import",
				LanguageUtil.get(_httpServletRequest, "import"), "get",
				"update", null),
			new FDSActionDropdownItem(
				"{actions.delete.href}", "trash", "delete",
				LanguageUtil.get(_httpServletRequest, "delete"), "delete",
				"delete", null));
	}

	public Map<String, Object> getFDSAdditionalProps() {
		return HashMapBuilder.<String, Object>put(
			"canAddDesignLibrary", _hasAddDepotEntryPermission()
		).put(
			"entryIdKey", "designLibraryEntryId"
		).put(
			"redirectURL",
			PortletURLBuilder.createRenderURL(
				_liferayPortletResponse
			).setMVCRenderCommandName(
				"/design_library/view_resources_design_library"
			).buildString()
		).build();
	}

	private JSONArray _getBreadcrumbItemsJSONArray() {
		return JSONUtil.putAll(
			JSONUtil.put(
				"active", true
			).put(
				"label",
				LanguageUtil.get(_httpServletRequest, "design-libraries")
			));
	}

	private String _getExportImportPortletURL(String portletId) {
		return HttpComponentsUtil.addParameter(
			StringBundler.concat(
				_themeDisplay.getCDNBaseURL(),
				_themeDisplay.getPathFriendlyURLPrivateGroup(),
				"/asset-library-{id}/~/control_panel/manage?p_p_id=",
				portletId),
			PortalUtil.getPortletNamespace(portletId) + "backURL",
			PortalUtil.getCurrentURL(_httpServletRequest));
	}

	private boolean _hasAddDepotEntryPermission() {
		PortletResourcePermission portletResourcePermission =
			_depotPortletResourcePermissionSnapshot.get();

		if (portletResourcePermission == null) {
			return false;
		}

		return portletResourcePermission.contains(
			_themeDisplay.getPermissionChecker(),
			_themeDisplay.getScopeGroupId(), DepotActionKeys.ADD_DEPOT_ENTRY);
	}

	private static final Snapshot<PortletResourcePermission>
		_depotPortletResourcePermissionSnapshot = new Snapshot<>(
			ViewDesignLibraryAdminDisplayContext.class,
			PortletResourcePermission.class,
			"(resource.name=" + DepotConstants.RESOURCE_NAME + ")");

	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final ThemeDisplay _themeDisplay;

}