/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.frontend.data.set.filter;

import com.liferay.frontend.data.set.filter.GroupedFDSFilters;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.site.cms.site.initializer.internal.constants.CMSSiteInitializerFDSNames;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;

/**
 * @author Daniel Sanz
 */
@Component(
	property = {
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.ALL_RELATED_ASSETS_SECTION,
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.ALL_SECTION,
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.CONTENTS_SECTION,
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.FILES_SECTION,
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.RECYCLE_BIN_SECTION,
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.STRUCTURE_USAGES,
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.VIEW_CONTENTS_FOLDER,
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.VIEW_FILES_FOLDER,
		"service.ranking:Integer=99"
	},
	service = GroupedFDSFilters.class
)
public class CMSGroupedFDSFilters implements GroupedFDSFilters {

	@Override
	public JSONArray getGroupedFDSFiltersJSONArray(
		HttpServletRequest httpServletRequest) {

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", PortalUtil.getLocale(httpServletRequest),
			getClass());

		return JSONUtil.putAll(
			JSONUtil.put(
				LanguageUtil.get(resourceBundle, "filter-by"),
				JSONUtil.putAll(
					"scopeGroupId", "objectDefinitionExternalReferenceCode",
					"taxonomyCategoryIds", "keywords",
					"cmpProjectObjectEntryIds", "extension", "creatorId",
					"status")),
			JSONUtil.put(
				LanguageUtil.get(resourceBundle, "filter-by-date"),
				JSONUtil.putAll(
					"dateCreated", "dateDisplay", "dateExpiration",
					"dateModified", "dateReview")));
	}

}