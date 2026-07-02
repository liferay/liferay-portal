/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.web.internal.object.action.executor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.action.engine.ObjectActionEngine;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jonathan McCann
 */
@FeatureFlag("LPD-44511")
@RunWith(Arquillian.class)
public class CreateSEOStudioScansObjectActionExecutorTest
	extends BaseObjectActionExecutorTestCase {

	@Test
	public void testExecute() throws Exception {
		String hostname = RandomTestUtil.randomString();
		String includedPaths = RandomTestUtil.randomString();
		int maxPagesPerScan = RandomTestUtil.randomInt();
		String scope = RandomTestUtil.randomString();

		seoStudioDomainObjectEntry = _addSEOStudioDomainObjectEntry(
			hostname,
			JSONUtil.put(
				"engines",
				JSONUtil.put(
					"aiGenerated", JSONUtil.put("enabled", true)
				).put(
					"crawler", JSONUtil.put("enabled", true)
				).put(
					"gsc", JSONUtil.put("enabled", false)
				).put(
					"pageSpeed",
					JSONUtil.put(
						"enabled", true
					).put(
						"includedPaths", includedPaths
					).put(
						"maxPagesPerScan", maxPagesPerScan
					).put(
						"scope", scope
					)
				)
			).toString());

		_executeCreateScans();

		ObjectEntry seoStudioScanRunObjectEntry =
			_fetchSEOStudioScanRunObjectEntry(seoStudioDomainObjectEntry);

		Map<String, Serializable> scanRunValues =
			objectEntryLocalService.getValues(
				seoStudioScanRunObjectEntry.getObjectEntryId());

		Assert.assertEquals(hostname, MapUtil.getString(scanRunValues, "name"));
		Assert.assertEquals(
			accountEntry.getAccountEntryId(),
			MapUtil.getLong(
				scanRunValues, "r_accountToSEOStudioScanRuns_accountEntryId"));
		Assert.assertEquals(
			"running", MapUtil.getString(scanRunValues, "state"));
		Assert.assertEquals(
			"manual", MapUtil.getString(scanRunValues, "triggeredBy"));
		Assert.assertEquals(
			TestPropsValues.getUserId(),
			MapUtil.getLong(scanRunValues, "triggeringUserId"));

		List<ObjectEntry> seoStudioScanObjectEntries =
			_getSEOStudioScanObjectEntries(seoStudioScanRunObjectEntry);

		Assert.assertEquals(
			seoStudioScanObjectEntries.toString(), 3,
			seoStudioScanObjectEntries.size());

		Map<String, ObjectEntry> seoStudioScanObjectEntryMap =
			_getSEOStudioScanObjectEntryMap(seoStudioScanObjectEntries);

		Assert.assertNotNull(seoStudioScanObjectEntryMap.get("aiGenerated"));
		Assert.assertNotNull(seoStudioScanObjectEntryMap.get("crawler"));
		Assert.assertNull(seoStudioScanObjectEntryMap.get("gsc"));

		ObjectEntry seoStudioScanObjectEntry = seoStudioScanObjectEntryMap.get(
			"pageSpeed");

		Map<String, Serializable> values = objectEntryLocalService.getValues(
			seoStudioScanObjectEntry.getObjectEntryId());

		Assert.assertEquals(
			accountEntry.getAccountEntryId(),
			MapUtil.getLong(
				values, "r_accountToSEOStudioScans_accountEntryId"));
		Assert.assertEquals(
			"entireDomain", MapUtil.getString(values, "scanScope"));
		Assert.assertEquals("queued", MapUtil.getString(values, "state"));

		JSONObject scopeConfigJSONObject = JSONFactoryUtil.createJSONObject(
			MapUtil.getString(values, "scopeConfig"));

		Assert.assertFalse(
			scopeConfigJSONObject.toString(),
			scopeConfigJSONObject.has("enabled"));
		Assert.assertEquals(
			includedPaths, scopeConfigJSONObject.getString("includedPaths"));
		Assert.assertEquals(
			maxPagesPerScan, scopeConfigJSONObject.getInt("maxPagesPerScan"));
		Assert.assertEquals(scope, scopeConfigJSONObject.getString("scope"));
	}

	@Test
	public void testExecuteWithNoEnabledEngines() throws Exception {
		seoStudioDomainObjectEntry = _addSEOStudioDomainObjectEntry(
			RandomTestUtil.randomString(),
			JSONUtil.put(
				"engines",
				JSONUtil.put(
					"aiGenerated", JSONUtil.put("enabled", false)
				).put(
					"crawler", JSONUtil.put("enabled", false)
				).put(
					"gsc", JSONUtil.put("enabled", false)
				).put(
					"pageSpeed", JSONUtil.put("enabled", false)
				)
			).toString());

		_executeCreateScans();

		Assert.assertNull(
			_fetchSEOStudioScanRunObjectEntry(seoStudioDomainObjectEntry));
	}

	private ObjectEntry _addSEOStudioDomainObjectEntry(
			String hostname, String scanConfigJSON)
		throws Exception {

		return addObjectEntry(
			seoStudioDomainObjectDefinition,
			HashMapBuilder.<String, Serializable>put(
				"hostname", hostname
			).put(
				"name", RandomTestUtil.randomString()
			).put(
				"r_accountToSEOStudioDomains_accountEntryId",
				accountEntry.getAccountEntryId()
			).put(
				"r_seoStudioInstanceToSEOStudioDomains_seoStudioInstanceId",
				seoStudioInstanceObjectEntry.getObjectEntryId()
			).put(
				"scanConfig", scanConfigJSON
			).build());
	}

	private void _executeCreateScans() throws Exception {
		_objectActionEngine.executeObjectAction(
			"createScans", ObjectActionTriggerConstants.KEY_STANDALONE,
			seoStudioDomainObjectDefinition.getObjectDefinitionId(),
			JSONUtil.put(
				"classPK", seoStudioDomainObjectEntry.getObjectEntryId()
			).put(
				"objectEntry",
				HashMapBuilder.<String, Object>putAll(
					seoStudioDomainObjectEntry.getModelAttributes()
				).put(
					"values", seoStudioDomainObjectEntry.getValues()
				).build()
			),
			TestPropsValues.getUserId());
	}

	private ObjectEntry _fetchSEOStudioScanRunObjectEntry(
			ObjectEntry seoStudioDomainObjectEntry)
		throws Exception {

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.fetchObjectRelationship(
				seoStudioDomainObjectDefinition.getObjectDefinitionId(),
				"seoStudioDomainToSEOStudioScanRuns");

		List<ObjectEntry> seoStudioScanRunObjectEntries =
			objectEntryLocalService.getOneToManyObjectEntries(
				seoStudioDomainObjectEntry.getGroupId(),
				objectRelationship.getObjectRelationshipId(), null, true,
				seoStudioDomainObjectEntry.getObjectEntryId(), true, null,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		if (ListUtil.isEmpty(seoStudioScanRunObjectEntries)) {
			return null;
		}

		return seoStudioScanRunObjectEntries.get(0);
	}

	private List<ObjectEntry> _getSEOStudioScanObjectEntries(
			ObjectEntry seoStudioScanRunObjectEntry)
		throws Exception {

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.fetchObjectRelationship(
				seoStudioScanRunObjectEntry.getObjectDefinitionId(),
				"seoStudioScanRunToSEOStudioScans");

		return objectEntryLocalService.getOneToManyObjectEntries(
			seoStudioScanRunObjectEntry.getGroupId(),
			objectRelationship.getObjectRelationshipId(), null, true,
			seoStudioScanRunObjectEntry.getObjectEntryId(), true, null,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	private Map<String, ObjectEntry> _getSEOStudioScanObjectEntryMap(
			List<ObjectEntry> seoStudioScanObjectEntries)
		throws Exception {

		Map<String, ObjectEntry> seoStudioScanObjectEntryMap =
			new LinkedHashMap<>();

		for (ObjectEntry seoStudioScanObjectEntry :
				seoStudioScanObjectEntries) {

			seoStudioScanObjectEntryMap.put(
				MapUtil.getString(
					objectEntryLocalService.getValues(
						seoStudioScanObjectEntry.getObjectEntryId()),
					"scanType"),
				seoStudioScanObjectEntry);
		}

		return seoStudioScanObjectEntryMap;
	}

	@Inject
	private ObjectActionEngine _objectActionEngine;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

}