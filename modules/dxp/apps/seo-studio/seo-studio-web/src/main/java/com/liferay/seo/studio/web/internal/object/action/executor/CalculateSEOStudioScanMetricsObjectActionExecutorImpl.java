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
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;

import java.io.Serializable;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

		List<ObjectEntry> seoStudioScanObjectEntries = _getRelatedObjectEntries(
			seoStudioScanRunObjectEntry,
			_fetchObjectRelationship(
				companyId, "L_SEO_STUDIO_SCAN_RUN",
				"seoStudioScanRunToSEOStudioScans"));

		if (ListUtil.isEmpty(seoStudioScanObjectEntries)) {
			return;
		}

		for (ObjectEntry seoStudioScanObjectEntry :
				seoStudioScanObjectEntries) {

			Map<String, Serializable> values =
				seoStudioScanObjectEntry.getValues();

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
			_getRelatedObjectEntries(
				seoStudioScanRunObjectEntry,
				_fetchObjectRelationship(
					companyId, "L_SEO_STUDIO_SCAN_RUN",
					"seoStudioScanRunToSEOStudioScanMetrics"));

		if (ListUtil.isEmpty(seoStudioScanMetricObjectEntries)) {
			_addSEOStudioScanMetrics(
				companyId, seoStudioScanObjectEntries,
				seoStudioScanRunObjectEntry, seoStudioScanRunValues, userId);
		}

		_partialUpdateSEOStudioScanRunState(
			seoStudioScanRunObjectEntry, "completed", userId);
	}

	private void _addSEOStudioScanMetric(
			Set<Long> affectedPageIds, long companyId,
			Map<String, Map<String, Integer>> impactMixMap, String scope,
			ObjectEntry seoStudioScanRunObjectEntry,
			Map<String, Serializable> seoStudioScanRunValues, long userId)
		throws Exception {

		JSONObject categoryBreakdownJSONObject =
			_jsonFactory.createJSONObject();
		JSONObject impactMixJSONObject = _jsonFactory.createJSONObject();

		int criticalInsights = 0;
		int totalInsights = 0;

		for (Map.Entry<String, Map<String, Integer>> entry :
				impactMixMap.entrySet()) {

			String category = entry.getKey();
			Map<String, Integer> categoryImpactMixMap = entry.getValue();

			JSONObject categoryImpactMixJSONObject =
				_jsonFactory.createJSONObject();

			int categoryTotalInsights = 0;

			for (Map.Entry<String, Integer> impactCountEntry :
					categoryImpactMixMap.entrySet()) {

				categoryImpactMixJSONObject.put(
					impactCountEntry.getKey(), impactCountEntry.getValue());

				categoryTotalInsights += impactCountEntry.getValue();
			}

			criticalInsights += categoryImpactMixMap.getOrDefault("3", 0);
			totalInsights += categoryTotalInsights;

			categoryBreakdownJSONObject.put(category, categoryTotalInsights);
			impactMixJSONObject.put(category, categoryImpactMixJSONObject);
		}

		double averageInsightsPerAffectedPage = 0.0;

		if (!affectedPageIds.isEmpty()) {
			averageInsightsPerAffectedPage =
				(double)totalInsights / affectedPageIds.size();
		}

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
				"affectedPagesCount", affectedPageIds.size()
			).put(
				"averageInsightsPerAffectedPage", averageInsightsPerAffectedPage
			).put(
				"categoryBreakdown", categoryBreakdownJSONObject.toString()
			).put(
				"computedDate", new Date()
			).put(
				"criticalInsights", criticalInsights
			).put(
				"impactMix", impactMixJSONObject.toString()
			).put(
				"r_accountToSEOStudioScanMetrics_accountEntryId",
				GetterUtil.getLong(
					seoStudioScanRunValues.get(
						"r_accountToSEOStudioScanRuns_accountEntryId"))
			).put(
				"r_seoStudioScanRunToSEOStudioScanMetrics_seoStudioScanRunId",
				seoStudioScanRunObjectEntry.getObjectEntryId()
			).put(
				"scope", scope
			).put(
				"totalInsights", totalInsights
			).build(),
			serviceContext);
	}

	private void _addSEOStudioScanMetrics(
			long companyId, List<ObjectEntry> seoStudioScanObjectEntries,
			ObjectEntry seoStudioScanRunObjectEntry,
			Map<String, Serializable> seoStudioScanRunValues, long userId)
		throws Exception {

		Map<String, Set<Long>> affectedPageIdsMapByScope = new HashMap<>();
		Map<String, Map<String, Map<String, Integer>>> impactMixMapByScope =
			new HashMap<>();

		for (String scope : _categoriesMapByScope.keySet()) {
			affectedPageIdsMapByScope.put(scope, new HashSet<>());
			impactMixMapByScope.put(scope, new HashMap<>());
		}

		ObjectRelationship objectRelationship = _fetchObjectRelationship(
			companyId, "L_SEO_STUDIO_SCAN",
			"seoStudioScanToSEOStudioScanInsights");

		Map<Long, Map<String, Serializable>> seoStudioInsightTypeValuesMap =
			new HashMap<>();

		for (ObjectEntry seoStudioScanObjectEntry :
				seoStudioScanObjectEntries) {

			for (ObjectEntry seoStudioScanInsightObjectEntry :
					_getRelatedObjectEntries(
						seoStudioScanObjectEntry, objectRelationship)) {

				Map<String, Serializable> seoStudioScanInsightValues =
					seoStudioScanInsightObjectEntry.getValues();

				long seoStudioInsightTypeId = GetterUtil.getLong(
					seoStudioScanInsightValues.get(
						"r_seoStudioInsightTypeToScanInsights_" +
							"seoStudioInsightTypeId"));

				Map<String, Serializable> seoStudioInsightTypeValues =
					seoStudioInsightTypeValuesMap.get(seoStudioInsightTypeId);

				if (seoStudioInsightTypeValues == null) {
					seoStudioInsightTypeValues =
						_objectEntryLocalService.getValues(
							seoStudioInsightTypeId);

					seoStudioInsightTypeValuesMap.put(
						seoStudioInsightTypeId, seoStudioInsightTypeValues);
				}

				String category = GetterUtil.getString(
					seoStudioInsightTypeValues.get("category"));

				String scope = _getScope(category);

				if (scope == null) {
					continue;
				}

				Map<String, Map<String, Integer>> impactMixMap =
					impactMixMapByScope.get(scope);

				Map<String, Integer> categoryImpactMixMap =
					impactMixMap.computeIfAbsent(
						category, categoryKey -> new HashMap<>());

				categoryImpactMixMap.merge(
					GetterUtil.getString(
						seoStudioInsightTypeValues.get("severity")),
					1, Integer::sum);

				Set<Long> affectedPageIds = affectedPageIdsMapByScope.get(
					scope);

				affectedPageIds.add(
					GetterUtil.getLong(
						seoStudioScanInsightValues.get(
							"r_seoStudioPageToSEOStudioScanInsights_" +
								"seoStudioPageId")));
			}
		}

		for (String scope : _categoriesMapByScope.keySet()) {
			_addSEOStudioScanMetric(
				affectedPageIdsMapByScope.get(scope), companyId,
				impactMixMapByScope.get(scope), scope,
				seoStudioScanRunObjectEntry, seoStudioScanRunValues, userId);
		}
	}

	private ObjectRelationship _fetchObjectRelationship(
			long companyId, String objectDefinitionExternalReferenceCode,
			String relationshipName)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					objectDefinitionExternalReferenceCode, companyId);

		return _objectRelationshipLocalService.fetchObjectRelationship(
			objectDefinition.getObjectDefinitionId(), relationshipName);
	}

	private List<ObjectEntry> _getRelatedObjectEntries(
			ObjectEntry objectEntry, ObjectRelationship objectRelationship)
		throws Exception {

		return _objectEntryLocalService.getOneToManyObjectEntries(
			objectEntry.getGroupId(),
			objectRelationship.getObjectRelationshipId(), null, true,
			objectEntry.getObjectEntryId(), true, null, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	private String _getScope(String category) {
		for (Map.Entry<String, Set<String>> entry :
				_categoriesMapByScope.entrySet()) {

			Set<String> categories = entry.getValue();

			if (categories.contains(category)) {
				return entry.getKey();
			}
		}

		return null;
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

	private static final Map<String, Set<String>> _categoriesMapByScope =
		Map.of("onPage", Set.of("contentStructure", "images", "metadata"));

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

}