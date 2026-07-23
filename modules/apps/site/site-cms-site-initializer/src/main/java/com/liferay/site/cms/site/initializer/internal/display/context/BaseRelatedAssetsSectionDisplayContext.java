/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.configuration.DLConfiguration;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.translation.exporter.TranslationInfoItemFieldValuesExporterRegistry;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Carolina Barbosa
 */
public abstract class BaseRelatedAssetsSectionDisplayContext
	extends BaseSectionDisplayContext {

	public BaseRelatedAssetsSectionDisplayContext(
		DepotEntryLocalService depotEntryLocalService,
		DLConfiguration dlConfiguration, GroupLocalService groupLocalService,
		HttpServletRequest httpServletRequest, Language language,
		ObjectDefinition objectDefinition,
		ObjectDefinitionService objectDefinitionService,
		ObjectEntry objectEntry, Portal portal,
		TranslationInfoItemFieldValuesExporterRegistry
			translationInfoItemFieldValuesExporterRegistry) {

		super(
			depotEntryLocalService, dlConfiguration, groupLocalService,
			httpServletRequest, language, objectDefinitionService, portal,
			translationInfoItemFieldValuesExporterRegistry);

		this.objectDefinition = objectDefinition;
		this.objectEntry = objectEntry;
	}

	@Override
	public Map<String, Object> getEmptyState() {
		return HashMapBuilder.<String, Object>put(
			"image", "/states/cms_empty_state_preview.svg"
		).put(
			"title", LanguageUtil.get(httpServletRequest, "no-assets-yet")
		).build();
	}

	@Override
	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				StringBundler.concat(
					themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
					GroupConstants.CMS_FRIENDLY_URL,
					"/edit_content_item?objectEntryId={embedded.id}&",
					"redirect=", themeDisplay.getURLCurrent()),
				"pencil", "actionLink",
				LanguageUtil.get(httpServletRequest, "edit"), "get", "update",
				null),
			new FDSActionDropdownItem(
				StringBundler.concat(
					themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
					GroupConstants.CMS_FRIENDLY_URL,
					"/edit_content_item?p_l_mode=read&p_p_state=",
					LiferayWindowState.POP_UP, "&redirect=",
					themeDisplay.getURLCurrent(),
					"&objectEntryId={embedded.id}"),
				"view", "view-content",
				LanguageUtil.get(httpServletRequest, "view"), null, "get",
				null),
			new FDSActionDropdownItem(
				StringPool.BLANK, "view", "view-file",
				LanguageUtil.get(httpServletRequest, "view"), null, "get",
				null),
			new FDSActionDropdownItem(
				null, "share", "share",
				LanguageUtil.get(httpServletRequest, "share"), "get", "share",
				"link"));
	}

	@Override
	protected String getCMSSectionFilterString() {
		return appendStatus(
			StringBundler.concat(
				"(cmsSection eq 'contents' or cmsSection eq 'files') and ",
				getRelatedObjectEntriesFilterString(),
				" and rootDescendantNode eq false"));
	}

	protected abstract String getRelatedObjectEntriesFilterString();

	protected String getRelatedObjectEntriesFilterString(
		String fieldName, long... objectEntryIds) {

		if (objectEntryIds.length == 0) {
			return StringPool.BLANK;
		}

		return StringBundler.concat(
			fieldName, " in (",
			StringUtil.merge(objectEntryIds, StringPool.COMMA), ")");
	}

	protected final ObjectDefinition objectDefinition;
	protected final ObjectEntry objectEntry;

}