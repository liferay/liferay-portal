/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.web.internal.object.action.executor.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.action.engine.ObjectActionEngine;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.initializer.SiteInitializerRegistry;

import java.io.Serializable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jonathan McCann
 */
@FeatureFlag("LPD-44511")
@RunWith(Arquillian.class)
public class CreateSEOStudioScansObjectActionExecutorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		SiteInitializer siteInitializer =
			_siteInitializerRegistry.getSiteInitializer(
				"com.liferay.seo.studio.site.initializer");

		siteInitializer.initialize(_group.getGroupId());

		_seoStudioDomainObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_SEO_STUDIO_DOMAIN", TestPropsValues.getCompanyId());
		_seoStudioInstanceObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_SEO_STUDIO_INSTANCE", TestPropsValues.getCompanyId());
		_seoStudioScanRunObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_SEO_STUDIO_SCAN_RUN", TestPropsValues.getCompanyId());

		ObjectDefinition seoStudioScanObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_SEO_STUDIO_SCAN", TestPropsValues.getCompanyId());

		for (ObjectAction objectAction :
				_objectActionLocalService.getObjectActions(
					seoStudioScanObjectDefinition.getObjectDefinitionId())) {

			_objectActionLocalService.deleteObjectAction(objectAction);
		}
	}

	@After
	public void tearDown() throws Exception {
		if (_seoStudioDomainObjectEntry != null) {
			ObjectEntry seoStudioScanRunObjectEntry =
				_getSEOStudioScanRunObjectEntry(_seoStudioDomainObjectEntry);

			if (seoStudioScanRunObjectEntry != null) {
				for (ObjectEntry seoStudioScanObjectEntry :
						_getSEOStudioScanObjectEntries(
							seoStudioScanRunObjectEntry)) {

					_objectEntryLocalService.deleteObjectEntry(
						seoStudioScanObjectEntry.getObjectEntryId());
				}

				_objectEntryLocalService.deleteObjectEntry(
					seoStudioScanRunObjectEntry.getObjectEntryId());
			}

			_objectEntryLocalService.deleteObjectEntry(
				_seoStudioDomainObjectEntry.getObjectEntryId());
		}

		if (_seoStudioInstanceObjectEntry != null) {
			_objectEntryLocalService.deleteObjectEntry(
				_seoStudioInstanceObjectEntry.getObjectEntryId());
		}

		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testExecute() throws Exception {
		AccountEntry accountEntry = _addAccountEntry();

		_seoStudioInstanceObjectEntry = _addSEOStudioInstanceObjectEntry(
			accountEntry);

		String hostname = RandomTestUtil.randomString();
		String includedPaths = RandomTestUtil.randomString();
		int maxPagesPerScan = RandomTestUtil.randomInt();
		String scope = RandomTestUtil.randomString();

		_seoStudioDomainObjectEntry = _addSEOStudioDomainObjectEntry(
			accountEntry, hostname,
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
			).toString(),
			_seoStudioInstanceObjectEntry);

		_executeCreateScans(_seoStudioDomainObjectEntry);

		ObjectEntry seoStudioScanRunObjectEntry =
			_getSEOStudioScanRunObjectEntry(_seoStudioDomainObjectEntry);

		Map<String, Serializable> scanRunValues =
			_objectEntryLocalService.getValues(
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

		Map<String, Serializable> values = _objectEntryLocalService.getValues(
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

	private AccountEntry _addAccountEntry() throws Exception {
		return _accountEntryLocalService.addAccountEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), null, null,
			RandomTestUtil.randomString() + "@liferay.com", null, null,
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext());
	}

	private ObjectEntry _addSEOStudioDomainObjectEntry(
			AccountEntry accountEntry, String hostname, String scanConfigJSON,
			ObjectEntry seoStudioInstanceObjectEntry)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_seoStudioDomainObjectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
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
			).build(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	private ObjectEntry _addSEOStudioInstanceObjectEntry(
			AccountEntry accountEntry)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_seoStudioInstanceObjectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"hostname", RandomTestUtil.randomString()
			).put(
				"name", RandomTestUtil.randomString()
			).put(
				"r_accountToSEOStudioInstances_accountEntryId",
				accountEntry.getAccountEntryId()
			).build(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	private void _executeCreateScans(ObjectEntry seoStudioDomainObjectEntry)
		throws Exception {

		_objectActionEngine.executeObjectAction(
			"createScans", ObjectActionTriggerConstants.KEY_STANDALONE,
			_seoStudioDomainObjectDefinition.getObjectDefinitionId(),
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

	private List<ObjectEntry> _getSEOStudioScanObjectEntries(
			ObjectEntry seoStudioScanRunObjectEntry)
		throws Exception {

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.fetchObjectRelationship(
				_seoStudioScanRunObjectDefinition.getObjectDefinitionId(),
				"seoStudioScanRunToSEOStudioScans");

		return _objectEntryLocalService.getOneToManyObjectEntries(
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
					_objectEntryLocalService.getValues(
						seoStudioScanObjectEntry.getObjectEntryId()),
					"scanType"),
				seoStudioScanObjectEntry);
		}

		return seoStudioScanObjectEntryMap;
	}

	private ObjectEntry _getSEOStudioScanRunObjectEntry(
			ObjectEntry seoStudioDomainObjectEntry)
		throws Exception {

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.fetchObjectRelationship(
				_seoStudioDomainObjectDefinition.getObjectDefinitionId(),
				"seoStudioDomainToSEOStudioScanRuns");

		List<ObjectEntry> seoStudioScanRunObjectEntries =
			_objectEntryLocalService.getOneToManyObjectEntries(
				seoStudioDomainObjectEntry.getGroupId(),
				objectRelationship.getObjectRelationshipId(), null, true,
				seoStudioDomainObjectEntry.getObjectEntryId(), true, null,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		if (ListUtil.isEmpty(seoStudioScanRunObjectEntries)) {
			return null;
		}

		return seoStudioScanRunObjectEntries.get(0);
	}

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	private Group _group;

	@Inject
	private ObjectActionEngine _objectActionEngine;

	@Inject
	private ObjectActionLocalService _objectActionLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	private ObjectDefinition _seoStudioDomainObjectDefinition;
	private ObjectEntry _seoStudioDomainObjectEntry;
	private ObjectDefinition _seoStudioInstanceObjectDefinition;
	private ObjectEntry _seoStudioInstanceObjectEntry;
	private ObjectDefinition _seoStudioScanRunObjectDefinition;

	@Inject
	private SiteInitializerRegistry _siteInitializerRegistry;

}