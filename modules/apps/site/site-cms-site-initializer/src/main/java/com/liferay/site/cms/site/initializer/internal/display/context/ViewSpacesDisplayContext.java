/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.depot.constants.DepotActionKeys;
import com.liferay.headless.asset.library.dto.v1_0.AssetLibrary;
import com.liferay.headless.asset.library.resource.v1_0.AssetLibraryResource;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.site.cms.site.initializer.internal.util.ActionUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Roberto Díaz
 */
public class ViewSpacesDisplayContext {

	public ViewSpacesDisplayContext(
		AssetLibraryResource.Factory assetLibraryResourceFactory,
		HttpServletRequest httpServletRequest, JSONFactory jsonFactory,
		PortletResourcePermission portletResourcePermission) {

		_assetLibraryResourceFactory = assetLibraryResourceFactory;
		_jsonFactory = jsonFactory;
		_portletResourcePermission = portletResourcePermission;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getProps() throws Exception {
		Page<AssetLibrary> page = _getPage();

		return HashMapBuilder.<String, Object>put(
			"allSpacesURL",
			StringBundler.concat(
				_themeDisplay.getPathFriendlyURLPublic(),
				GroupConstants.CMS_FRIENDLY_URL, "/all-spaces")
		).put(
			"assetLibraries",
			JSONUtil.toJSONArray(
				page.getItems(),
				assetLibrary -> JSONUtil.put(
					"id", assetLibrary.getId()
				).put(
					"name", assetLibrary.getName()
				).put(
					"settings",
					_jsonFactory.createJSONObject(
						_jsonFactory.looseSerialize(assetLibrary.getSettings()))
				).put(
					"url",
					ActionUtil.getSpaceURL(assetLibrary.getId(), _themeDisplay)
				))
		).put(
			"assetLibrariesCount", page.getTotalCount()
		).put(
			"newSpaceURL",
			StringBundler.concat(
				_themeDisplay.getPathFriendlyURLPublic(),
				GroupConstants.CMS_FRIENDLY_URL, "/new-space")
		).put(
			"showAddButton",
			_portletResourcePermission.contains(
				_themeDisplay.getPermissionChecker(),
				_themeDisplay.getScopeGroupId(),
				DepotActionKeys.ADD_DEPOT_ENTRY)
		).build();
	}

	private Collection<AssetLibrary> _getAssetLibraries(
		Page<AssetLibrary> assetLibrariesPage,
		Page<AssetLibrary> pinnedByMeAssetLibrariesPage) {

		if (assetLibrariesPage.getTotalCount() == 0) {
			return Collections.emptyList();
		}

		if (pinnedByMeAssetLibrariesPage.getTotalCount() == 5) {
			return pinnedByMeAssetLibrariesPage.getItems();
		}

		List<AssetLibrary> assetLibraries = new ArrayList<>(
			pinnedByMeAssetLibrariesPage.getItems());

		List<Long> assetLibraryIds = ListUtil.toList(
			assetLibraries, AssetLibrary::getId);

		for (AssetLibrary assetLibrary : assetLibrariesPage.getItems()) {
			if (!assetLibraryIds.contains(assetLibrary.getId())) {
				assetLibraries.add(assetLibrary);
			}

			if (assetLibraries.size() == 5) {
				return assetLibraries;
			}
		}

		return assetLibraries;
	}

	private Page<AssetLibrary> _getPage() throws Exception {
		AssetLibraryResource.Builder builder =
			_assetLibraryResourceFactory.create();

		AssetLibraryResource assetLibraryResource = builder.user(
			_themeDisplay.getUser()
		).build();

		Page<AssetLibrary> assetLibrariesPage =
			assetLibraryResource.getAssetLibrariesPage(
				null, null, null, Pagination.of(1, 5), null);

		return Page.of(
			assetLibrariesPage.getActions(),
			_getAssetLibraries(
				assetLibrariesPage,
				assetLibraryResource.getAssetLibrariesPinnedByMePage(
					Pagination.of(1, 5))),
			Pagination.of(1, 5), assetLibrariesPage.getTotalCount());
	}

	private final AssetLibraryResource.Factory _assetLibraryResourceFactory;
	private final JSONFactory _jsonFactory;
	private final PortletResourcePermission _portletResourcePermission;
	private final ThemeDisplay _themeDisplay;

}