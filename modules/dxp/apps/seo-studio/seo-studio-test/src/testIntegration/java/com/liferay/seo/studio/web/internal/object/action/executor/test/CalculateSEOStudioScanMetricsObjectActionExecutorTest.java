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
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.initializer.SiteInitializerRegistry;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Noor Najjar
 */
@FeatureFlag("LPD-44511")
@RunWith(Arquillian.class)
public class CalculateSEOStudioScanMetricsObjectActionExecutorTest {

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
		_seoStudioScanObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_SEO_STUDIO_SCAN", TestPropsValues.getCompanyId());
		_seoStudioScanRunObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_SEO_STUDIO_SCAN_RUN", TestPropsValues.getCompanyId());

		for (ObjectAction objectAction :
				_objectActionLocalService.getObjectActions(
					_seoStudioScanObjectDefinition.getObjectDefinitionId())) {

			if (!Objects.equals(
					objectAction.getName(), "calculateScanMetrics")) {

				_objectActionLocalService.deleteObjectAction(objectAction);
			}
		}
	}

	@After
	public void tearDown() throws Exception {
		if (_seoStudioScanRunObjectEntry != null) {
			for (ObjectEntry seoStudioScanMetricObjectEntry :
					_getSEOStudioScanMetricObjectEntries(
						_seoStudioScanRunObjectEntry)) {

				_objectEntryLocalService.deleteObjectEntry(
					seoStudioScanMetricObjectEntry.getObjectEntryId());
			}

			for (ObjectEntry seoStudioScanObjectEntry :
					_getSEOStudioScanObjectEntries(
						_seoStudioScanRunObjectEntry)) {

				_objectEntryLocalService.deleteObjectEntry(
					seoStudioScanObjectEntry.getObjectEntryId());
			}

			_objectEntryLocalService.deleteObjectEntry(
				_seoStudioScanRunObjectEntry.getObjectEntryId());
		}

		if (_seoStudioDomainObjectEntry != null) {
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
	public void testDoesNotFinalizeWhenScanStillRunning() throws Exception {
		_seedScanRun();

		ObjectEntry completedScanObjectEntry = _addSEOStudioScanObjectEntry(
			"crawler", "completed");

		_addSEOStudioScanObjectEntry("pageSpeed", "running");

		_executeCalculateScanMetrics(completedScanObjectEntry);

		Assert.assertEquals("running", _getState(_seoStudioScanRunObjectEntry));
		Assert.assertTrue(
			_getSEOStudioScanMetricObjectEntries(
				_seoStudioScanRunObjectEntry
			).isEmpty());
	}

	@Test
	public void testFinalizesWhenAllScansCompleted() throws Exception {
		_seedScanRun();

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

		Map<String, Serializable> values = _objectEntryLocalService.getValues(
			seoStudioScanMetricObjectEntries.get(
				0
			).getObjectEntryId());

		Assert.assertEquals("onPage", MapUtil.getString(values, "scope"));
		Assert.assertEquals(0, MapUtil.getInteger(values, "totalInsights"));
		Assert.assertEquals(0, MapUtil.getInteger(values, "criticalInsights"));
		Assert.assertEquals(
			0, MapUtil.getInteger(values, "affectedPagesCount"));
	}

	@Test
	public void testIsIdempotent() throws Exception {
		_seedScanRun();

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
	public void testSetsScanRunToFailedWhenScanCancelled() throws Exception {
		_seedScanRun();

		_addSEOStudioScanObjectEntry("crawler", "completed");

		ObjectEntry cancelledScanObjectEntry = _addSEOStudioScanObjectEntry(
			"pageSpeed", "cancelled");

		_executeCalculateScanMetrics(cancelledScanObjectEntry);

		Assert.assertEquals("failed", _getState(_seoStudioScanRunObjectEntry));
		Assert.assertTrue(
			_getSEOStudioScanMetricObjectEntries(
				_seoStudioScanRunObjectEntry
			).isEmpty());
	}

	@Test
	public void testSetsScanRunToFailedWhenScanFailed() throws Exception {
		_seedScanRun();

		_addSEOStudioScanObjectEntry("crawler", "completed");

		ObjectEntry failedScanObjectEntry = _addSEOStudioScanObjectEntry(
			"pageSpeed", "failed");

		_executeCalculateScanMetrics(failedScanObjectEntry);

		Assert.assertEquals("failed", _getState(_seoStudioScanRunObjectEntry));
		Assert.assertTrue(
			_getSEOStudioScanMetricObjectEntries(
				_seoStudioScanRunObjectEntry
			).isEmpty());
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
			AccountEntry accountEntry, ObjectEntry seoStudioInstanceObjectEntry)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_seoStudioDomainObjectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"hostname", RandomTestUtil.randomString()
			).put(
				"name", RandomTestUtil.randomString()
			).put(
				"r_accountToSEOStudioDomains_accountEntryId",
				accountEntry.getAccountEntryId()
			).put(
				"r_seoStudioInstanceToSEOStudioDomains_seoStudioInstanceId",
				seoStudioInstanceObjectEntry.getObjectEntryId()
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

	private ObjectEntry _addSEOStudioScanObjectEntry(
			String scanType, String state)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_seoStudioScanObjectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"r_accountToSEOStudioScans_accountEntryId",
				_accountEntry.getAccountEntryId()
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
			).build(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	private ObjectEntry _addSEOStudioScanRunObjectEntry(
			AccountEntry accountEntry, ObjectEntry seoStudioDomainObjectEntry)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_seoStudioScanRunObjectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
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
			).build(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
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

		return _objectEntryLocalService.getOneToManyObjectEntries(
			seoStudioScanRunObjectEntry.getGroupId(),
			objectRelationship.getObjectRelationshipId(), null, true,
			seoStudioScanRunObjectEntry.getObjectEntryId(), true, null,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
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

	private String _getState(ObjectEntry objectEntry) throws Exception {
		return MapUtil.getString(
			_objectEntryLocalService.getValues(objectEntry.getObjectEntryId()),
			"state");
	}

	private void _seedScanRun() throws Exception {
		_accountEntry = _addAccountEntry();

		_seoStudioInstanceObjectEntry = _addSEOStudioInstanceObjectEntry(
			_accountEntry);

		_seoStudioDomainObjectEntry = _addSEOStudioDomainObjectEntry(
			_accountEntry, _seoStudioInstanceObjectEntry);

		_seoStudioScanRunObjectEntry = _addSEOStudioScanRunObjectEntry(
			_accountEntry, _seoStudioDomainObjectEntry);
	}

	private AccountEntry _accountEntry;

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
	private ObjectDefinition _seoStudioScanObjectDefinition;
	private ObjectDefinition _seoStudioScanRunObjectDefinition;
	private ObjectEntry _seoStudioScanRunObjectEntry;

	@Inject
	private SiteInitializerRegistry _siteInitializerRegistry;

}