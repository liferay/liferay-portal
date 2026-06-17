/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.web.internal.object.action.executor.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
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
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
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
public class ComputeSEOStudioDomainNextScanDateObjectActionExecutorTest {

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
			for (ObjectEntry seoStudioScanObjectEntry :
					_getSEOStudioScanObjectEntries(
						_seoStudioDomainObjectEntry)) {

				_objectEntryLocalService.deleteObjectEntry(
					seoStudioScanObjectEntry.getObjectEntryId());
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
		_addSEOStudioDomainObjectEntry();

		_updateSEOStudioDomainObjectEntry(true);

		Map<String, Serializable> values = _objectEntryLocalService.getValues(
			_seoStudioDomainObjectEntry.getObjectEntryId());

		Date nextScanDate = (Date)values.get("nextScanDate");

		Assert.assertTrue(nextScanDate.after(new Date()));
	}

	@Test
	public void testExecuteWithAutoScanDisabled() throws Exception {
		_addSEOStudioDomainObjectEntry();

		_updateSEOStudioDomainObjectEntry(false);

		Map<String, Serializable> values = _objectEntryLocalService.getValues(
			_seoStudioDomainObjectEntry.getObjectEntryId());

		Assert.assertNull(values.get("nextScanDate"));
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

	private void _addSEOStudioDomainObjectEntry() throws Exception {
		AccountEntry accountEntry = _addAccountEntry();

		_seoStudioInstanceObjectEntry = _addSEOStudioInstanceObjectEntry(
			accountEntry);

		_seoStudioDomainObjectEntry = _objectEntryLocalService.addObjectEntry(
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
				_seoStudioInstanceObjectEntry.getObjectEntryId()
			).build(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	private void _updateSEOStudioDomainObjectEntry(boolean autoScanEnabled)
		throws Exception {

		_objectEntryLocalService.partialUpdateObjectEntry(
			TestPropsValues.getUserId(),
			_seoStudioDomainObjectEntry.getObjectEntryId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			HashMapBuilder.<String, Serializable>put(
				"autoScanEnabled", autoScanEnabled
			).put(
				"scanFrequency", "daily"
			).put(
				"scanTime", "09:00"
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

	private List<ObjectEntry> _getSEOStudioScanObjectEntries(
			ObjectEntry seoStudioDomainObjectEntry)
		throws Exception {

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.fetchObjectRelationship(
				_seoStudioDomainObjectDefinition.getObjectDefinitionId(),
				"seoStudioDomainToSEOStudioScans");

		return _objectEntryLocalService.getOneToManyObjectEntries(
			seoStudioDomainObjectEntry.getGroupId(),
			objectRelationship.getObjectRelationshipId(), null, true,
			seoStudioDomainObjectEntry.getObjectEntryId(), true, null,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	private Group _group;

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

	@Inject
	private SiteInitializerRegistry _siteInitializerRegistry;

}