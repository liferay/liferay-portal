/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.configuration.DLConfiguration;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.site.cms.site.initializer.internal.util.ActionUtil;
import com.liferay.translation.exporter.TranslationInfoItemFieldValuesExporterRegistry;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Carolina Barbosa
 */
public class ViewRelatedAssetsSectionDisplayContext
	extends BaseRelatedAssetsSectionDisplayContext {

	public ViewRelatedAssetsSectionDisplayContext(
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
			httpServletRequest, language, objectDefinition,
			objectDefinitionService, objectEntry, portal,
			translationInfoItemFieldValuesExporterRegistry);

		_linkedAssetsFilterFieldName = _getLinkedAssetsFilterFieldName();
	}

	@Override
	public Map<String, Object> getAdditionalProps() {
		return HashMapBuilder.<String, Object>put(
			"objectEntryLinkContext", _getObjectEntryLinkContext()
		).putAll(
			super.getAdditionalProps()
		).build();
	}

	@Override
	public List<DropdownItem> getCreationMenuDropdownItems() {
		return ListUtil.fromArray(
			DropdownItemBuilder.putData(
				"action", "uploadMultipleFiles"
			).putData(
				"baseAssetLibraryViewURL",
				ActionUtil.getBaseSpaceURL(themeDisplay)
			).putData(
				"parentObjectEntryFolderExternalReferenceCode", StringPool.BLANK
			).setIcon(
				"upload-multiple"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "upload")
			).build(),
			DropdownItemBuilder.putData(
				"action", "selectAssets"
			).putData(
				"objectEntryId", String.valueOf(objectEntry.getObjectEntryId())
			).putData(
				"objectRelationshipFieldName", _getObjectRelationshipFieldName()
			).putData(
				"restContextPath", _getRESTContextPath()
			).putData(
				"scopeGroupId", String.valueOf(objectEntry.getGroupId())
			).putData(
				"searchAPIURL",
				() -> {
					String additionalAPIURLParameters =
						SectionDisplayContextUtil.getAdditionalAPIURLParameters(
							appendStatus(
								StringBundler.concat(
									"(cmsSection eq 'contents' or cmsSection ",
									"eq 'files') and not (",
									getLinkedAssetsFilterString(),
									") and objectDefinitionId gt 0 and ",
									"rootDescendantNode eq false")),
							httpServletRequest, null);

					return "/o/search/v1.0/search?" +
						additionalAPIURLParameters;
				}
			).setIcon(
				"sheets"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "cms-assets")
			).build());
	}

	@Override
	public Map<String, Object> getEmptyState() {
		return HashMapBuilder.<String, Object>put(
			"description",
			LanguageUtil.get(httpServletRequest, "drag-and-drop-your-files-or")
		).putAll(
			super.getEmptyState()
		).build();
	}

	@Override
	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		List<FDSActionDropdownItem> fdsActionDropdownItems =
			super.getFDSActionDropdownItems();

		fdsActionDropdownItems.add(
			new FDSActionDropdownItem(
				null, "chain-broken", "unlink-asset",
				LanguageUtil.format(
					httpServletRequest, "remove-from-x",
					objectDefinition.getLabel(themeDisplay.getLocale())),
				null, "update", null));

		return fdsActionDropdownItems;
	}

	@Override
	protected String getLinkedAssetsFilterString() {
		return getLinkedAssetsFilterString(
			_linkedAssetsFilterFieldName, objectEntry.getObjectEntryId());
	}

	private Map<String, Object> _getObjectEntryLinkContext() {
		return HashMapBuilder.<String, Object>put(
			"objectEntryId", String.valueOf(objectEntry.getObjectEntryId())
		).put(
			"objectRelationshipFieldName", _getObjectRelationshipFieldName()
		).put(
			"restContextPath", _getRESTContextPath()
		).put(
			"scopeGroupId", String.valueOf(objectEntry.getGroupId())
		).build();
	}

	private String _getLinkedAssetsFilterFieldName() {
		if (StringUtil.equals(
				objectDefinition.getExternalReferenceCode(), "L_CMP_PROJECT")) {

			return "cmpProjectObjectEntryIds";
		}

		return "cmpTaskObjectEntryIds";
	}

	private String _getObjectRelationshipFieldName() {
		if (StringUtil.equals(
				objectDefinition.getExternalReferenceCode(), "L_CMP_PROJECT")) {

			return "r_cmpProjectToCMPProjectLinks_c_cmpProjectId";
		}

		return "r_cmpTaskToCMPTaskLinks_c_cmpTaskId";
	}

	private String _getRESTContextPath() {
		if (StringUtil.equals(
				objectDefinition.getExternalReferenceCode(), "L_CMP_PROJECT")) {

			return "/o/cmp/project-links";
		}

		return "/o/cmp/task-links";
	}

	private final String _linkedAssetsFilterFieldName;

}