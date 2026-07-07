/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.web.internal.object.action.executor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.action.engine.ObjectActionEngine;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Noor Najjar
 */
@FeatureFlag("LPD-44511")
@RunWith(Arquillian.class)
public class CalculateSEOStudioScanMetricsObjectActionExecutorTest
	extends BaseObjectActionExecutorTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_seoStudioScanObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_SEO_STUDIO_SCAN", TestPropsValues.getCompanyId());
		_seoStudioScanRunObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_SEO_STUDIO_SCAN_RUN", TestPropsValues.getCompanyId());

		ObjectAction objectAction = _objectActionLocalService.fetchObjectAction(
			_seoStudioScanObjectDefinition.getObjectDefinitionId(),
			"calculateScanMetrics");

		objectAction.setActive(true);

		_objectActionLocalService.updateObjectAction(objectAction);
	}

	@Test
	public void testExecute() throws Exception {
		_addSEOStudioScanRunObjectEntry();

		ObjectEntry completedScanObjectEntry = _addSEOStudioScanObjectEntry(
			"crawler", "completed");

		ObjectEntry aeoReadinessHighInsightTypeObjectEntry =
			_addSEOStudioInsightTypeObjectEntry(
				"aeoReadiness", completedScanObjectEntry, "3");
		ObjectEntry contentStructureMediumInsightTypeObjectEntry =
			_addSEOStudioInsightTypeObjectEntry(
				"contentStructure", completedScanObjectEntry, "2");
		ObjectEntry imagesHighInsightTypeObjectEntry =
			_addSEOStudioInsightTypeObjectEntry(
				"images", completedScanObjectEntry, "3");
		ObjectEntry metadataHighInsightTypeObjectEntry =
			_addSEOStudioInsightTypeObjectEntry(
				"metadata", completedScanObjectEntry, "3");
		ObjectEntry metadataLowInsightTypeObjectEntry =
			_addSEOStudioInsightTypeObjectEntry(
				"metadata", completedScanObjectEntry, "1");

		ObjectEntry pageObjectEntry1 = _addSEOStudioPageObjectEntry(
			completedScanObjectEntry);
		ObjectEntry pageObjectEntry2 = _addSEOStudioPageObjectEntry(
			completedScanObjectEntry);
		ObjectEntry pageObjectEntry3 = _addSEOStudioPageObjectEntry(
			completedScanObjectEntry);

		_addSEOStudioScanInsightObjectEntry(
			aeoReadinessHighInsightTypeObjectEntry, pageObjectEntry1,
			completedScanObjectEntry);
		_addSEOStudioScanInsightObjectEntry(
			contentStructureMediumInsightTypeObjectEntry, pageObjectEntry3,
			completedScanObjectEntry);
		_addSEOStudioScanInsightObjectEntry(
			imagesHighInsightTypeObjectEntry, pageObjectEntry1,
			completedScanObjectEntry);
		_addSEOStudioScanInsightObjectEntry(
			metadataHighInsightTypeObjectEntry, pageObjectEntry1,
			completedScanObjectEntry);
		_addSEOStudioScanInsightObjectEntry(
			metadataHighInsightTypeObjectEntry, pageObjectEntry2,
			completedScanObjectEntry);
		_addSEOStudioScanInsightObjectEntry(
			metadataLowInsightTypeObjectEntry, pageObjectEntry1,
			completedScanObjectEntry);

		_executeCalculateScanMetrics(completedScanObjectEntry);

		List<ObjectEntry> seoStudioScanMetricObjectEntries =
			_getSEOStudioScanMetricObjectEntries(_seoStudioScanRunObjectEntry);

		ObjectEntry seoStudioScanMetricObjectEntry =
			seoStudioScanMetricObjectEntries.get(0);

		Map<String, Serializable> values = objectEntryLocalService.getValues(
			seoStudioScanMetricObjectEntry.getObjectEntryId());

		Assert.assertEquals(
			3, MapUtil.getInteger(values, "affectedPagesCount"));
		Assert.assertEquals(
			5.0 / 3.0,
			MapUtil.getDouble(values, "averageInsightsPerAffectedPage"), 0.001);
		Assert.assertEquals(3, MapUtil.getInteger(values, "criticalInsights"));
		Assert.assertEquals(5, MapUtil.getInteger(values, "totalInsights"));

		JSONObject categoryBreakdownJSONObject =
			JSONFactoryUtil.createJSONObject(
				MapUtil.getString(values, "categoryBreakdown"));

		Assert.assertFalse(categoryBreakdownJSONObject.has("aeoReadiness"));
		Assert.assertEquals(
			1, categoryBreakdownJSONObject.getInt("contentStructure"));
		Assert.assertEquals(1, categoryBreakdownJSONObject.getInt("images"));
		Assert.assertEquals(3, categoryBreakdownJSONObject.getInt("metadata"));

		JSONObject impactMixJSONObject = JSONFactoryUtil.createJSONObject(
			MapUtil.getString(values, "impactMix"));

		JSONObject contentStructureImpactMixJSONObject =
			impactMixJSONObject.getJSONObject("contentStructure");

		Assert.assertEquals(1, contentStructureImpactMixJSONObject.getInt("2"));

		JSONObject imagesImpactMixJSONObject =
			impactMixJSONObject.getJSONObject("images");

		Assert.assertEquals(1, imagesImpactMixJSONObject.getInt("3"));

		JSONObject metadataImpactMixJSONObject =
			impactMixJSONObject.getJSONObject("metadata");

		Assert.assertEquals(1, metadataImpactMixJSONObject.getInt("1"));
		Assert.assertEquals(2, metadataImpactMixJSONObject.getInt("3"));
	}

	@Test
	public void testExecuteWithExistingScanMetric() throws Exception {
		_addSEOStudioScanRunObjectEntry();

		ObjectEntry completedScanObjectEntry = _addSEOStudioScanObjectEntry(
			"crawler", "completed");

		_addSEOStudioScanObjectEntry("pageSpeed", "completed");

		_executeCalculateScanMetrics(completedScanObjectEntry);
		_executeCalculateScanMetrics(completedScanObjectEntry);

		Assert.assertEquals(
			"completed", _getState(_seoStudioScanRunObjectEntry));

		List<ObjectEntry> seoStudioScanMetricObjectEntries =
			_getSEOStudioScanMetricObjectEntries(_seoStudioScanRunObjectEntry);

		Assert.assertEquals(
			seoStudioScanMetricObjectEntries.toString(), 1,
			seoStudioScanMetricObjectEntries.size());
	}

	@Test
	public void testExecuteWithFailedScan() throws Exception {
		_testExecuteWithoutMetrics("failed", "cancelled");
		_testExecuteWithoutMetrics("failed", "failed");
	}

	@Test
	public void testExecuteWithNoScanInsights() throws Exception {
		_addSEOStudioScanRunObjectEntry();

		ObjectEntry completedScanObjectEntry = _addSEOStudioScanObjectEntry(
			"crawler", "completed");

		_addSEOStudioScanObjectEntry("pageSpeed", "completed");

		_executeCalculateScanMetrics(completedScanObjectEntry);

		Assert.assertEquals(
			"completed", _getState(_seoStudioScanRunObjectEntry));

		List<ObjectEntry> seoStudioScanMetricObjectEntries =
			_getSEOStudioScanMetricObjectEntries(_seoStudioScanRunObjectEntry);

		Assert.assertEquals(
			seoStudioScanMetricObjectEntries.toString(), 1,
			seoStudioScanMetricObjectEntries.size());

		ObjectEntry seoStudioScanMetricObjectEntry =
			seoStudioScanMetricObjectEntries.get(0);

		Map<String, Serializable> values = objectEntryLocalService.getValues(
			seoStudioScanMetricObjectEntry.getObjectEntryId());

		Assert.assertEquals(
			0, MapUtil.getInteger(values, "affectedPagesCount"));
		Assert.assertEquals(0, MapUtil.getInteger(values, "criticalInsights"));
		Assert.assertEquals("onPage", MapUtil.getString(values, "scope"));
		Assert.assertEquals(0, MapUtil.getInteger(values, "totalInsights"));
	}

	@Test
	public void testExecuteWithRunningScan() throws Exception {
		_testExecuteWithoutMetrics("running", "running");
	}

	private ObjectEntry _addSEOStudioInsightTypeObjectEntry(
			String category, ObjectEntry seoStudioScanObjectEntry,
			String severity)
		throws Exception {

		return addObjectEntry(
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_SEO_STUDIO_INSIGHT_TYPE",
					TestPropsValues.getCompanyId()),
			HashMapBuilder.<String, Serializable>put(
				"category", category
			).put(
				"name", "orphanPages"
			).put(
				"r_accountToSEOStudioInsightTypes_accountEntryId",
				accountEntry.getAccountEntryId()
			).put(
				"r_seoStudioScanToSEOStudioInsightTypes_seoStudioScanId",
				seoStudioScanObjectEntry.getObjectEntryId()
			).put(
				"severity", severity
			).build());
	}

	private ObjectEntry _addSEOStudioPageObjectEntry(
			ObjectEntry seoStudioScanObjectEntry)
		throws Exception {

		return addObjectEntry(
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_SEO_STUDIO_PAGE", TestPropsValues.getCompanyId()),
			HashMapBuilder.<String, Serializable>put(
				"pageURL", RandomTestUtil.randomString()
			).put(
				"r_accountToSEOStudioPages_accountEntryId",
				accountEntry.getAccountEntryId()
			).put(
				"r_seoStudioScanToSEOStudioPages_seoStudioScanId",
				seoStudioScanObjectEntry.getObjectEntryId()
			).build());
	}

	private ObjectEntry _addSEOStudioScanInsightObjectEntry(
			ObjectEntry seoStudioInsightTypeObjectEntry,
			ObjectEntry seoStudioPageObjectEntry,
			ObjectEntry seoStudioScanObjectEntry)
		throws Exception {

		return addObjectEntry(
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_SEO_STUDIO_SCAN_INSIGHT",
					TestPropsValues.getCompanyId()),
			HashMapBuilder.<String, Serializable>put(
				"classification", "problem"
			).put(
				"detectedDate", new Date()
			).put(
				"r_accountToSEOStudioScanInsights_accountEntryId",
				accountEntry.getAccountEntryId()
			).put(
				"r_seoStudioInsightTypeToScanInsights_seoStudioInsightTypeId",
				seoStudioInsightTypeObjectEntry.getObjectEntryId()
			).put(
				"r_seoStudioPageToSEOStudioScanInsights_seoStudioPageId",
				seoStudioPageObjectEntry.getObjectEntryId()
			).put(
				"r_seoStudioScanToSEOStudioScanInsights_seoStudioScanId",
				seoStudioScanObjectEntry.getObjectEntryId()
			).put(
				"state", RandomTestUtil.randomInt()
			).build());
	}

	private ObjectEntry _addSEOStudioScanObjectEntry(
			String scanType, String state)
		throws Exception {

		return addObjectEntry(
			_seoStudioScanObjectDefinition,
			HashMapBuilder.<String, Serializable>put(
				"r_accountToSEOStudioScans_accountEntryId",
				accountEntry.getAccountEntryId()
			).put(
				"r_seoStudioScanRunToSEOStudioScans_seoStudioScanRunId",
				_seoStudioScanRunObjectEntry.getObjectEntryId()
			).put(
				"scanRange", "full"
			).put(
				"scanScope", "entireDomain"
			).put(
				"scanType", scanType
			).put(
				"state", state
			).build());
	}

	private void _addSEOStudioScanRunObjectEntry() throws Exception {
		seoStudioDomainObjectEntry = addSEOStudioDomainObjectEntry(
			RandomTestUtil.randomString(), null);

		_seoStudioScanRunObjectEntry = addObjectEntry(
			_seoStudioScanRunObjectDefinition,
			HashMapBuilder.<String, Serializable>put(
				"name", RandomTestUtil.randomString()
			).put(
				"r_accountToSEOStudioScanRuns_accountEntryId",
				accountEntry.getAccountEntryId()
			).put(
				"r_seoStudioDomainToSEOStudioScanRuns_seoStudioDomainId",
				seoStudioDomainObjectEntry.getObjectEntryId()
			).put(
				"requestDate", new Date()
			).put(
				"state", "running"
			).put(
				"triggeredBy", "manual"
			).build());
	}

	private void _executeCalculateScanMetrics(
			ObjectEntry seoStudioScanObjectEntry)
		throws Exception {

		_objectActionEngine.executeObjectAction(
			"calculateScanMetrics",
			ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE,
			_seoStudioScanObjectDefinition.getObjectDefinitionId(),
			JSONUtil.put(
				"classPK", seoStudioScanObjectEntry.getObjectEntryId()
			).put(
				"objectEntry",
				HashMapBuilder.<String, Object>putAll(
					seoStudioScanObjectEntry.getModelAttributes()
				).put(
					"values", seoStudioScanObjectEntry.getValues()
				).build()
			),
			TestPropsValues.getUserId());
	}

	private List<ObjectEntry> _getSEOStudioScanMetricObjectEntries(
			ObjectEntry seoStudioScanRunObjectEntry)
		throws Exception {

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.fetchObjectRelationship(
				_seoStudioScanRunObjectDefinition.getObjectDefinitionId(),
				"seoStudioScanRunToSEOStudioScanMetrics");

		return objectEntryLocalService.getOneToManyObjectEntries(
			seoStudioScanRunObjectEntry.getGroupId(),
			objectRelationship.getObjectRelationshipId(), null, true,
			seoStudioScanRunObjectEntry.getObjectEntryId(), true, null,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	private String _getState(ObjectEntry objectEntry) throws Exception {
		return MapUtil.getString(
			objectEntryLocalService.getValues(objectEntry.getObjectEntryId()),
			"state");
	}

	private void _testExecuteWithoutMetrics(
			String expectedState, String scanState)
		throws Exception {

		_addSEOStudioScanRunObjectEntry();

		ObjectEntry completedScanObjectEntry = _addSEOStudioScanObjectEntry(
			"crawler", "completed");

		_addSEOStudioScanObjectEntry("pageSpeed", scanState);

		_executeCalculateScanMetrics(completedScanObjectEntry);

		Assert.assertEquals(
			expectedState, _getState(_seoStudioScanRunObjectEntry));
		Assert.assertTrue(
			ListUtil.isEmpty(
				_getSEOStudioScanMetricObjectEntries(
					_seoStudioScanRunObjectEntry)));
	}

	@Inject
	private ObjectActionEngine _objectActionEngine;

	@Inject
	private ObjectActionLocalService _objectActionLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	private ObjectDefinition _seoStudioScanObjectDefinition;
	private ObjectDefinition _seoStudioScanRunObjectDefinition;
	private ObjectEntry _seoStudioScanRunObjectEntry;

}