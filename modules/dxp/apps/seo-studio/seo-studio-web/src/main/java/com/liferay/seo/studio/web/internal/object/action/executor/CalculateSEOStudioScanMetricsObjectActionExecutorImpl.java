/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.web.internal.object.action.executor;

import com.liferay.object.action.executor.BaseObjectActionExecutor;
import com.liferay.object.action.executor.ObjectActionExecutor;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.scope.ObjectDefinitionScoped;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Noor Najjar
 */
@Component(service = ObjectActionExecutor.class)
public class CalculateSEOStudioScanMetricsObjectActionExecutorImpl
	extends BaseObjectActionExecutor implements ObjectDefinitionScoped {

	@Override
	public List<String> getAllowedObjectDefinitionNames() {
		return List.of("SEOStudioScan");
	}

	@Override
	public String getKey() {
		return "calculate-seo-studio-scan-metrics";
	}

	@Override
	protected void doExecute(
			long companyId, long objectActionId,
			UnicodeProperties parametersUnicodeProperties,
			JSONObject payloadJSONObject, long userId)
		throws Exception {

		long seoStudioScanId = payloadJSONObject.getLong("classPK");

		Map<String, Serializable> seoStudioScanValues =
			_objectEntryLocalService.getValues(seoStudioScanId);

		long seoStudioScanRunId = GetterUtil.getLong(
			seoStudioScanValues.get(
				"r_seoStudioScanRunToSEOStudioScans_seoStudioScanRunId"));

		if (seoStudioScanRunId <= 0) {
			return;
		}

		ObjectEntry seoStudioScanRunObjectEntry =
			_objectEntryLocalService.fetchObjectEntry(seoStudioScanRunId);

		if (seoStudioScanRunObjectEntry == null) {
			return;
		}

		Map<String, Serializable> seoStudioScanRunValues =
			seoStudioScanRunObjectEntry.getValues();

		String seoStudioScanRunState = GetterUtil.getString(
			seoStudioScanRunValues.get("state"));

		if (ArrayUtil.contains(
				new String[] {"cancelled", "completed", "failed"},
				seoStudioScanRunState)) {

			return;
		}

		List<ObjectEntry> seoStudioScanObjectEntries =
			_getSEOStudioScanRunRelatedObjectEntries(
				companyId, seoStudioScanRunObjectEntry,
				"seoStudioScanRunToSEOStudioScans");

		if (ListUtil.isEmpty(seoStudioScanObjectEntries)) {
			return;
		}

		for (ObjectEntry seoStudioScanObjectEntry :
				seoStudioScanObjectEntries) {

			Map<String, Serializable> values =
				_objectEntryLocalService.getValues(
					seoStudioScanObjectEntry.getObjectEntryId());

			String seoStudioScanState = GetterUtil.getString(
				values.get("state"));

			if (ArrayUtil.contains(
					new String[] {"queued", "running"}, seoStudioScanState)) {

				return;
			}

			if (ArrayUtil.contains(
					new String[] {"cancelled", "failed"}, seoStudioScanState)) {

				_partialUpdateSEOStudioScanRunState(
					seoStudioScanRunObjectEntry, "failed", userId);

				return;
			}
		}

		List<ObjectEntry> seoStudioScanMetricObjectEntries =
			_getSEOStudioScanRunRelatedObjectEntries(
				companyId, seoStudioScanRunObjectEntry,
				"seoStudioScanRunToSEOStudioScanMetrics");

		if (seoStudioScanMetricObjectEntries.isEmpty()) {
			_addOnPageSEOStudioScanMetric(
				companyId, userId, seoStudioScanRunObjectEntry,
				seoStudioScanRunValues);
		}

		_partialUpdateSEOStudioScanRunState(
			seoStudioScanRunObjectEntry, "completed", userId);
	}

	private void _addOnPageSEOStudioScanMetric(
			long companyId, long userId,
			ObjectEntry seoStudioScanRunObjectEntry,
			Map<String, Serializable> seoStudioScanRunValues)
		throws Exception {

		ObjectDefinition seoStudioScanMetricObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_SEO_STUDIO_SCAN_METRIC", companyId);

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);
		serviceContext.setUserId(userId);

		_objectEntryLocalService.addObjectEntry(
			0, userId,
			seoStudioScanMetricObjectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"affectedPagesCount", 0
			).put(
				"averageInsightsPerAffectedPage", 0.0D
			).put(
				"categoryBreakdown", "{}"
			).put(
				"computedDate", new Date()
			).put(
				"criticalInsights", 0
			).put(
				"impactMix", "{}"
			).put(
				"r_accountToSEOStudioScanMetrics_accountEntryId",
				GetterUtil.getLong(
					seoStudioScanRunValues.get(
						"r_accountToSEOStudioScanRuns_accountEntryId"))
			).put(
				"r_seoStudioScanRunToSEOStudioScanMetrics_seoStudioScanRunId",
				seoStudioScanRunObjectEntry.getObjectEntryId()
			).put(
				"scope", "onPage"
			).put(
				"totalInsights", 0
			).build(),
			serviceContext);
	}

	private List<ObjectEntry> _getSEOStudioScanRunRelatedObjectEntries(
			long companyId, ObjectEntry seoStudioScanRunObjectEntry,
			String relationshipName)
		throws Exception {

		ObjectDefinition seoStudioScanRunObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_SEO_STUDIO_SCAN_RUN", companyId);

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.fetchObjectRelationship(
				seoStudioScanRunObjectDefinition.getObjectDefinitionId(),
				relationshipName);

		return _objectEntryLocalService.getOneToManyObjectEntries(
			seoStudioScanRunObjectEntry.getGroupId(),
			objectRelationship.getObjectRelationshipId(), null, true,
			seoStudioScanRunObjectEntry.getObjectEntryId(), true, null,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	private void _partialUpdateSEOStudioScanRunState(
			ObjectEntry seoStudioScanRunObjectEntry, String state, long userId)
		throws Exception {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(seoStudioScanRunObjectEntry.getCompanyId());
		serviceContext.setUserId(userId);

		_objectEntryLocalService.partialUpdateObjectEntry(
			userId, seoStudioScanRunObjectEntry.getObjectEntryId(),
			seoStudioScanRunObjectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"state", state
			).build(),
			serviceContext);
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

}