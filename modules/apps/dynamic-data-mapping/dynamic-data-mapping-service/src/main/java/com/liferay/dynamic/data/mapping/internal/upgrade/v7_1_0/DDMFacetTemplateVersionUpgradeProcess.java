/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.upgrade.v7_1_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Map;

/**
 * @author Yuri Monteiro
 */
public class DDMFacetTemplateVersionUpgradeProcess extends UpgradeProcess {

	public DDMFacetTemplateVersionUpgradeProcess(
		ClassNameLocalService classNameLocalService) {

		_classNameLocalService = classNameLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_updateFacetTemplates();
	}

	private void _updateFacetTemplates() throws Exception {
		long resourceClassNameId = _classNameLocalService.getClassNameId(
			"com.liferay.portlet.display.template.PortletDisplayTemplate");

		for (Map.Entry<String, String> entry :
				_newFacetTemplateClassNames.entrySet()) {

			long newClassNameId = _classNameLocalService.getClassNameId(
				entry.getValue());
			long oldClassNameId = _classNameLocalService.getClassNameId(
				entry.getKey());

			runSQL(
				StringBundler.concat(
					"update DDMTemplateVersion set classNameId = ",
					newClassNameId, " where classNameId = ", oldClassNameId,
					" and templateId in (select templateId from DDMTemplate ",
					"where resourceClassNameId = ", resourceClassNameId, ")"));
		}
	}

	private final ClassNameLocalService _classNameLocalService;
	private final Map<String, String> _newFacetTemplateClassNames =
		HashMapBuilder.put(
			"com.liferay.portal.search.web.internal.custom.facet.display." +
				"context.CustomFacetDisplayContext",
			"com.liferay.portal.search.web.internal.custom.facet.portlet." +
				"CustomFacetPortlet"
		).put(
			"com.liferay.portal.search.web.internal.custom.facet.display." +
				"context.CustomFacetTermDisplayContext",
			"com.liferay.portal.search.web.internal.custom.facet.portlet." +
				"CustomFacetPortlet"
		).put(
			"com.liferay.portal.search.web.internal.facet.display.context." +
				"AssetCategoriesSearchFacetDisplayContext",
			"com.liferay.portal.search.web.internal.category.facet.portlet." +
				"CategoryFacetPortlet"
		).put(
			"com.liferay.portal.search.web.internal.facet.display.context." +
				"AssetCategoriesSearchFacetTermDisplayContext",
			"com.liferay.portal.search.web.internal.category.facet.portlet." +
				"CategoryFacetPortlet"
		).put(
			"com.liferay.portal.search.web.internal.facet.display.context." +
				"AssetEntriesSearchFacetDisplayContext",
			"com.liferay.portal.search.web.internal.type.facet.portlet." +
				"TypeFacetPortlet"
		).put(
			"com.liferay.portal.search.web.internal.facet.display.context." +
				"AssetEntriesSearchFacetTermDisplayContext",
			"com.liferay.portal.search.web.internal.type.facet.portlet." +
				"TypeFacetPortlet"
		).put(
			"com.liferay.portal.search.web.internal.facet.display.context." +
				"AssetTagsSearchFacetDisplayContext",
			"com.liferay.portal.search.web.internal.tag.facet.portlet." +
				"TagFacetPortlet"
		).put(
			"com.liferay.portal.search.web.internal.facet.display.context." +
				"AssetTagsSearchFacetTermDisplayContext",
			"com.liferay.portal.search.web.internal.tag.facet.portlet." +
				"TagFacetPortlet"
		).put(
			"com.liferay.portal.search.web.internal.facet.display.context." +
				"FolderSearchFacetDisplayContext",
			"com.liferay.portal.search.web.internal.folder.facet.portlet." +
				"FolderFacetPortlet"
		).put(
			"com.liferay.portal.search.web.internal.facet.display.context." +
				"FolderSearchFacetTermDisplayContext",
			"com.liferay.portal.search.web.internal.folder.facet.portlet." +
				"FolderFacetPortlet"
		).put(
			"com.liferay.portal.search.web.internal.facet.display.context." +
				"ScopeSearchFacetDisplayContext",
			"com.liferay.portal.search.web.internal.site.facet.portlet." +
				"SiteFacetPortlet"
		).put(
			"com.liferay.portal.search.web.internal.facet.display.context." +
				"ScopeSearchFacetTermDisplayContext",
			"com.liferay.portal.search.web.internal.site.facet.portlet." +
				"SiteFacetPortlet"
		).put(
			"com.liferay.portal.search.web.internal.facet.display.context." +
				"UserSearchFacetTermDisplayContext",
			"com.liferay.portal.search.web.internal.user.facet.portlet." +
				"UserFacetPortlet"
		).put(
			"com.liferay.portal.search.web.internal.modified.facet.display." +
				"context.ModifiedFacetTermDisplayContext",
			"com.liferay.portal.search.web.internal.modified.facet.portlet." +
				"ModifiedFacetPortlet"
		).build();

}