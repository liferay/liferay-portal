/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.web.internal.object.action.executor.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
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
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.initializer.SiteInitializerRegistry;

import java.io.Serializable;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;

/**
 * @author Jonathan McCann
 */
public abstract class BaseObjectActionExecutorTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		group = GroupTestUtil.addGroup();

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId()));

		SiteInitializer siteInitializer =
			_siteInitializerRegistry.getSiteInitializer(
				"com.liferay.seo.studio.site.initializer");

		siteInitializer.initialize(group.getGroupId());

		seoStudioDomainObjectDefinition =
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

		accountEntry = _addAccountEntry();

		seoStudioInstanceObjectEntry = _addSEOStudioInstanceObjectEntry();
	}

	@After
	public void tearDown() throws Exception {
		if (seoStudioDomainObjectEntry != null) {
			ObjectEntry seoStudioScanRunObjectEntry =
				fetchSEOStudioScanRunObjectEntry(seoStudioDomainObjectEntry);

			if (seoStudioScanRunObjectEntry != null) {
				for (ObjectEntry seoStudioScanObjectEntry :
						getSEOStudioScanObjectEntries(
							seoStudioScanRunObjectEntry)) {

					objectEntryLocalService.deleteObjectEntry(
						seoStudioScanObjectEntry.getObjectEntryId());
				}

				objectEntryLocalService.deleteObjectEntry(
					seoStudioScanRunObjectEntry.getObjectEntryId());
			}

			objectEntryLocalService.deleteObjectEntry(
				seoStudioDomainObjectEntry.getObjectEntryId());
		}

		if (seoStudioInstanceObjectEntry != null) {
			objectEntryLocalService.deleteObjectEntry(
				seoStudioInstanceObjectEntry.getObjectEntryId());
		}

		ServiceContextThreadLocal.popServiceContext();
	}

	protected ObjectEntry fetchSEOStudioScanRunObjectEntry(
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

	protected List<ObjectEntry> getSEOStudioScanObjectEntries(
			ObjectEntry seoStudioScanRunObjectEntry)
		throws Exception {

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.fetchObjectRelationship(
				_seoStudioScanRunObjectDefinition.getObjectDefinitionId(),
				"seoStudioScanRunToSEOStudioScans");

		return objectEntryLocalService.getOneToManyObjectEntries(
			seoStudioScanRunObjectEntry.getGroupId(),
			objectRelationship.getObjectRelationshipId(), null, true,
			seoStudioScanRunObjectEntry.getObjectEntryId(), true, null,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	protected AccountEntry accountEntry;
	protected Group group;

	@Inject
	protected ObjectEntryLocalService objectEntryLocalService;

	protected ObjectDefinition seoStudioDomainObjectDefinition;
	protected ObjectEntry seoStudioDomainObjectEntry;
	protected ObjectEntry seoStudioInstanceObjectEntry;

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

	private ObjectEntry _addSEOStudioInstanceObjectEntry() throws Exception {
		return objectEntryLocalService.addObjectEntry(
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
				group.getGroupId(), TestPropsValues.getUserId()));
	}

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private ObjectActionLocalService _objectActionLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	private ObjectDefinition _seoStudioInstanceObjectDefinition;
	private ObjectDefinition _seoStudioScanRunObjectDefinition;

	@Inject
	private SiteInitializerRegistry _siteInitializerRegistry;

}