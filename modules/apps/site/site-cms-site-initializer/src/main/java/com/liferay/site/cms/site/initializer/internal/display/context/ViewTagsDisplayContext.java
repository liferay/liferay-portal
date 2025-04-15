/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Noor Najjar
 */
public class ViewTagsDisplayContext {

	public ViewTagsDisplayContext(
		HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay) {

		_httpServletRequest = httpServletRequest;
		_themeDisplay = themeDisplay;
	}

	public Map<String, Object> getReactData() throws PortalException {
		return HashMapBuilder.<String, Object>put(
			"assetLibraryId",
			() -> {
				List<DepotEntry> depotEntries =
					DepotEntryLocalServiceUtil.getDepotEntries(
						QueryUtil.ALL_POS, QueryUtil.ALL_POS);

				if (!depotEntries.isEmpty()) {
					DepotEntry depotEntry = depotEntries.get(0);

					return depotEntry.getDepotEntryId();
				}

				return DepotEntryLocalServiceUtil.addDepotEntry(
					HashMapBuilder.put(
						LocaleUtil.getDefault(), "Depot"
					).build(),
					new HashMap<>(),
					ServiceContextFactory.getInstance(_httpServletRequest));
			}
		).put(
			"tagsURL",
			PortalUtil.getLayoutFullURL(
				LayoutLocalServiceUtil.getLayoutByFriendlyURL(
					_themeDisplay.getScopeGroupId(), false,
					"/categorization/view_tags"),
				_themeDisplay)
		).put(
			"vocabulariesURL",
			PortalUtil.getLayoutFullURL(
				LayoutLocalServiceUtil.getLayoutByFriendlyURL(
					_themeDisplay.getScopeGroupId(), false,
					"/categorization/view_vocabularies"),
				_themeDisplay)
		).build();
	}

	private final HttpServletRequest _httpServletRequest;
	private final ThemeDisplay _themeDisplay;

}