/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.site.cms.site.initializer.internal.constants.CMSSiteInitializerFDSNames;

import java.util.Map;

/**
 * @author Noor Najjar
 */
public class ViewTagsDisplayContext {

	public ViewTagsDisplayContext(ThemeDisplay themeDisplay) {
		_themeDisplay = themeDisplay;
	}

	public Map<String, Object> getReactData() throws PortalException {
		return HashMapBuilder.<String, Object>put(
			"dataSetId", CMSSiteInitializerFDSNames.CATEGORIZATION_TAGS
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

	private final ThemeDisplay _themeDisplay;

}