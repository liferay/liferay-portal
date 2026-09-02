/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.fragment.entry.processor.analytics;

import com.liferay.fragment.entry.processor.analytics.AnalyticsAttributesContributor;
import com.liferay.fragment.entry.processor.helper.InfoItemFieldMapped;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.site.cmp.site.initializer.internal.util.CMPObjectEntryUtil;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcos Martins
 */
@Component(service = AnalyticsAttributesContributor.class)
public class CMPAnalyticsAttributesContributor
	implements AnalyticsAttributesContributor {

	@Override
	public Map<String, Object> getAnalyticsAttributes(
			InfoItemFieldMapped infoItemFieldMapped, Locale locale)
		throws PortalException {

		if (!(infoItemFieldMapped.getObject() instanceof
				ObjectEntry objectEntry)) {

			return Collections.emptyMap();
		}

		long companyId = objectEntry.getCompanyId();

		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-58677")) {
			return Collections.emptyMap();
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_CMP_PROJECT", companyId);

		if ((objectDefinition == null) ||
			!CMPObjectEntryUtil.isCMSAsset(
				objectEntry, _objectEntryFolderLocalService)) {

			return Collections.emptyMap();
		}

		long[] cmpProjectObjectEntryIds =
			CMPObjectEntryUtil.getCMPProjectObjectEntryIds(
				CMPObjectEntryUtil.getCMPTaskObjectEntryIds(
					_filterFactory, _groupLocalService,
					_objectDefinitionLocalService, objectEntry,
					_objectEntryLocalService),
				_filterFactory, _groupLocalService,
				_objectDefinitionLocalService, objectEntry,
				_objectEntryLocalService);

		if (cmpProjectObjectEntryIds.length == 0) {
			return Collections.emptyMap();
		}

		return HashMapBuilder.<String, Object>put(
			"analytics-cmp-projects",
			_getAnalyticsCMPProjects(cmpProjectObjectEntryIds, objectDefinition)
		).build();
	}

	private String _getAnalyticsCMPProjects(
		long[] cmpProjectObjectEntryIds, ObjectDefinition objectDefinition) {

		JSONArray jsonArray = JSONUtil.toJSONArray(
			ListUtil.fromArray(cmpProjectObjectEntryIds),
			cmpProjectObjectEntryId -> JSONUtil.put(
				"id", cmpProjectObjectEntryId
			).put(
				"name",
				_objectEntryLocalService.getTitleValue(
					objectDefinition.getObjectDefinitionId(),
					cmpProjectObjectEntryId)
			),
			_log);

		return jsonArray.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CMPAnalyticsAttributesContributor.class);

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

}