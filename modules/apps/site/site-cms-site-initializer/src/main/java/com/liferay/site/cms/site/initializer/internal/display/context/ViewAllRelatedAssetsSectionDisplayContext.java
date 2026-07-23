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
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.translation.exporter.TranslationInfoItemFieldValuesExporterRegistry;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Carolina Barbosa
 */
public class ViewAllRelatedAssetsSectionDisplayContext
	extends BaseRelatedAssetsSectionDisplayContext {

	public ViewAllRelatedAssetsSectionDisplayContext(
		DepotEntryLocalService depotEntryLocalService,
		DLConfiguration dlConfiguration, GroupLocalService groupLocalService,
		HttpServletRequest httpServletRequest, Language language,
		ObjectDefinition objectDefinition,
		ObjectDefinitionService objectDefinitionService,
		ObjectEntry objectEntry,
		ObjectEntryLocalService objectEntryLocalService,
		ObjectRelationship objectRelationship, Portal portal,
		TranslationInfoItemFieldValuesExporterRegistry
			translationInfoItemFieldValuesExporterRegistry) {

		super(
			depotEntryLocalService, dlConfiguration, groupLocalService,
			httpServletRequest, language, objectDefinition,
			objectDefinitionService, objectEntry, portal,
			translationInfoItemFieldValuesExporterRegistry);

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
	protected String getRelatedObjectEntriesFilterString() {
		String cmpProjectFilterString = getRelatedObjectEntriesFilterString(
			"cmpProjectObjectEntryIds", objectEntry.getObjectEntryId());
		String cmpTaskFilterString = getRelatedObjectEntriesFilterString(
			"cmpTaskObjectEntryIds", _getRelatedCMPTaskObjectEntryIds());

		if (Validator.isNull(cmpTaskFilterString)) {
			return cmpProjectFilterString;
		}

		return StringBundler.concat(
			"(", cmpProjectFilterString, " or ", cmpTaskFilterString, ")");
	}

	private long[] _getRelatedCMPTaskObjectEntryIds() {
		try {
			return TransformUtil.transformToLongArray(
				_objectEntryLocalService.getOneToManyObjectEntries(
					objectEntry.getGroupId(),
					_objectRelationship.getObjectRelationshipId(), null, false,
					objectEntry.getObjectEntryId(), true, null,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null),
				ObjectEntry::getObjectEntryId);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return new long[0];
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ViewAllRelatedAssetsSectionDisplayContext.class);

	private final ObjectEntryLocalService _objectEntryLocalService;
	private final ObjectRelationship _objectRelationship;

}