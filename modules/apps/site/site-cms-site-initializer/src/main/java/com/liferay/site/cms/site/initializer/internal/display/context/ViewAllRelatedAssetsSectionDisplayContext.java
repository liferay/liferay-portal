/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.configuration.DLConfiguration;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.translation.exporter.TranslationInfoItemFieldValuesExporterRegistry;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Carolina Barbosa
 */
public class ViewAllRelatedAssetsSectionDisplayContext
	extends BaseRelatedAssetsSectionDisplayContext {

	public ViewAllRelatedAssetsSectionDisplayContext(
		AssetTagLocalService assetTagLocalService,
		DepotEntryLocalService depotEntryLocalService,
		DLConfiguration dlConfiguration, GroupLocalService groupLocalService,
		HttpServletRequest httpServletRequest, Language language,
		ObjectDefinition objectDefinition,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectDefinitionService objectDefinitionService,
		ObjectEntry objectEntry,
		ObjectEntryLocalService objectEntryLocalService,
		ObjectRelationship objectRelationship, Portal portal,
		TranslationInfoItemFieldValuesExporterRegistry
			translationInfoItemFieldValuesExporterRegistry) {

		super(
			assetTagLocalService, depotEntryLocalService, dlConfiguration,
			groupLocalService, httpServletRequest, language, objectDefinition,
			objectDefinitionService, objectEntry, portal,
			translationInfoItemFieldValuesExporterRegistry);

		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectEntryLocalService = objectEntryLocalService;
		_objectRelationship = objectRelationship;
	}

	@Override
	public Map<String, Object> getAdditionalProps() {
		Map<String, Object> additionalProps = super.getAdditionalProps();

		try {
			additionalProps.put("breadcrumbProps", getBreadcrumbProps());
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return additionalProps;
	}

	public Map<String, Object> getAIAssistantChatProps() {
		String title = MapUtil.getString(objectEntry.getValues(), "title");

		return HashMapBuilder.<String, Object>put(
			"context",
			HashMapBuilder.<String, Object>put(
				"cmsGroupId", objectEntry.getGroupId()
			).put(
				"focusScope", "full-matrix"
			).put(
				"projectId", objectEntry.getObjectEntryId()
			).build()
		).put(
			"initialMessage",
			LanguageUtil.format(
				httpServletRequest,
				"get-ai-insights-for-the-x-content-coverage-matrix", title)
		).put(
			"instructionDefinitionScope", "cms"
		).put(
			"triggerLabel",
			LanguageUtil.get(httpServletRequest, "get-ai-insights")
		).build();
	}

	@Override
	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		List<FDSActionDropdownItem> fdsActionDropdownItems =
			super.getFDSActionDropdownItems();

		fdsActionDropdownItems.add(
			0,
			new FDSActionDropdownItem(
				StringPool.BLANK, "info-circle-open", "show-details",
				LanguageUtil.get(httpServletRequest, "show-details"), null,
				null, "infoPanel"));

		return fdsActionDropdownItems;
	}

	@Override
	protected String[] getKeywords() {
		Set<String> tagNames = new HashSet<>();

		try {
			for (ObjectEntry relatedObjectEntry :
					_objectEntryLocalService.getOneToManyObjectEntries(
						objectEntry.getGroupId(),
						_objectRelationship.getObjectRelationshipId(), null,
						false, objectEntry.getObjectEntryId(), true, null,
						QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

				tagNames.addAll(
					getTagNames(
						_objectDefinitionLocalService.fetchObjectDefinition(
							relatedObjectEntry.getObjectDefinitionId()),
						relatedObjectEntry));
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return tagNames.toArray(new String[0]);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ViewAllRelatedAssetsSectionDisplayContext.class);

	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final ObjectRelationship _objectRelationship;

}