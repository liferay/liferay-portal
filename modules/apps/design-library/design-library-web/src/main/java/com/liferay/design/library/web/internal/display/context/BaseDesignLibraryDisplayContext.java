/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.design.library.web.internal.display.context;

import com.liferay.depot.model.DepotEntry;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Mario Leandro
 */
public abstract class BaseDesignLibraryDisplayContext {

	public BaseDesignLibraryDisplayContext(
		DepotEntry depotEntry, HttpServletRequest httpServletRequest) {

		this.depotEntry = depotEntry;
		this.httpServletRequest = httpServletRequest;

		themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	protected Map<String, Object> buildEmptyState(
		String descriptionKey, String titleKey) {

		return HashMapBuilder.<String, Object>put(
			"description", LanguageUtil.get(httpServletRequest, descriptionKey)
		).put(
			"image", "/states/design_library_empty_state.svg"
		).put(
			"title", LanguageUtil.get(httpServletRequest, titleKey)
		).build();
	}

	protected String getAssetLibraryURL(Group group, String path) {
		return StringBundler.concat(
			"/o/headless-asset-library/v1.0/asset-libraries/",
			group.getExternalReferenceCode(), path);
	}

	protected Group getGroup() throws PortalException {
		return depotEntry.getGroup();
	}

	protected final DepotEntry depotEntry;
	protected final HttpServletRequest httpServletRequest;
	protected final ThemeDisplay themeDisplay;

}