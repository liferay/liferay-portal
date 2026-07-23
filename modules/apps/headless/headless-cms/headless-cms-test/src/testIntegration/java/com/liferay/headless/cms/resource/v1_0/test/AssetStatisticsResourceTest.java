/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.headless.cms.client.dto.v1_0.AssetStatistics;
import com.liferay.headless.cms.client.resource.v1_0.AssetStatisticsResource;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cms.site.initializer.test.util.CMSTestUtil;

import java.io.Serializable;

import java.util.Collections;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Crescenzo Rega
 */
@RunWith(Arquillian.class)
public class AssetStatisticsResourceTest
	extends BaseAssetStatisticsResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_cmsAdministratorUser = _addUserWithRole(
			RoleConstants.CMS_ADMINISTRATOR);
		_companyAdminUser = _addUserWithRole(RoleConstants.ADMINISTRATOR);

		_assetStatisticsResources = new AssetStatisticsResource[] {
			assetStatisticsResource,
			_buildAssetStatisticsResource(_cmsAdministratorUser),
			_buildAssetStatisticsResource(_companyAdminUser)
		};
	}

	@Override
	@Test
	public void testGetAssetStatistics() throws Exception {

		// Add object entry on irrelevant group and irrelevant object definition

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		DepotEntry depotEntry = _addSpaceDepotEntry(serviceContext);

		long groupId = depotEntry.getGroupId();

		ObjectDefinition irrelevantObjectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), "name")),
				ObjectDefinitionConstants.SCOPE_SITE);

		ObjectEntry irrelevantObjectEntry =
			_objectEntryLocalService.addObjectEntry(
				irrelevantGroup.getGroupId(), TestPropsValues.getUserId(),
				irrelevantObjectDefinition.getObjectDefinitionId(), 0, "en_US",
				HashMapBuilder.<String, Serializable>put(
					"name", RandomTestUtil.randomString()
				).build(),
				serviceContext);

		_objectEntryLocalService.updateStatus(
			TestPropsValues.getUserId(),
			irrelevantObjectEntry.getObjectEntryId(),
			WorkflowConstants.STATUS_DRAFT, serviceContext);

		_assertAssetStatistics(groupId, 0, 0, 0, 0, 0, 0, 0, 0, 0);

		ObjectDefinition objectDefinition =
			_getBasicWebContentObjectDefinition();

		Date date = new Date();

		// Add object entry with distant expiration date

		ObjectEntry objectEntry1 = _addObjectEntry(
			depotEntry, objectDefinition);

		objectEntry1.setExpirationDate(
			new Date(date.getTime() + (10 * Time.DAY)));

		_objectEntryLocalService.updateObjectEntry(objectEntry1);

		_assertAssetStatistics(groupId, 1, 0, 0, 0, 0, 0, 0, 1, 0);

		// Add object entry with future review date

		ObjectEntry objectEntry2 = _addObjectEntry(
			depotEntry, objectDefinition);

		objectEntry2.setReviewDate(new Date(date.getTime() + (5 * Time.DAY)));

		_objectEntryLocalService.updateObjectEntry(objectEntry2);

		_assertAssetStatistics(groupId, 2, 0, 0, 0, 0, 0, 0, 2, 1);

		// Add object entry with imminent expiration date

		ObjectEntry objectEntry3 = _addObjectEntry(
			depotEntry, objectDefinition);

		objectEntry3.setExpirationDate(
			new Date(date.getTime() + (3 * Time.DAY)));

		_objectEntryLocalService.updateObjectEntry(objectEntry3);

		_assertAssetStatistics(groupId, 3, 0, 1, 0, 0, 0, 0, 3, 1);

		// Add object entry with already passed expiration date

		ObjectEntry objectEntry4 = _addObjectEntry(
			depotEntry, objectDefinition);

		objectEntry4.setExpirationDate(
			new Date(date.getTime() - (2 * Time.DAY)));

		_objectEntryLocalService.updateObjectEntry(objectEntry4);

		_assertAssetStatistics(groupId, 4, 0, 2, 0, 0, 0, 0, 4, 1);

		// Add object entry with overdue review date

		ObjectEntry objectEntry5 = _addObjectEntry(
			depotEntry, objectDefinition);

		objectEntry5.setReviewDate(new Date(date.getTime() - (2 * Time.DAY)));

		_objectEntryLocalService.updateObjectEntry(objectEntry5);

		_assertAssetStatistics(groupId, 5, 0, 2, 0, 0, 1, 0, 5, 1);

		// Add object entry with status draft

		ObjectEntry objectEntry6 = _addObjectEntry(
			depotEntry, objectDefinition);

		_objectEntryLocalService.updateStatus(
			TestPropsValues.getUserId(), objectEntry6.getObjectEntryId(),
			WorkflowConstants.STATUS_DRAFT, serviceContext);

		_assertAssetStatistics(groupId, 5, 0, 2, 1, 0, 1, 0, 6, 1);

		// Add object entry with status expired

		ObjectEntry objectEntry7 = _addObjectEntry(
			depotEntry, objectDefinition);

		_objectEntryLocalService.updateStatus(
			TestPropsValues.getUserId(), objectEntry7.getObjectEntryId(),
			WorkflowConstants.STATUS_EXPIRED, serviceContext);

		_assertAssetStatistics(groupId, 5, 1, 2, 1, 0, 1, 0, 7, 1);

		// Add object entry in a status that is not visible in the All view

		ObjectEntry objectEntry8 = _addObjectEntry(
			depotEntry, objectDefinition);

		_objectEntryLocalService.updateStatus(
			TestPropsValues.getUserId(), objectEntry8.getObjectEntryId(),
			WorkflowConstants.STATUS_DENIED, serviceContext);

		_assertAssetStatistics(groupId, 5, 1, 2, 1, 0, 1, 0, 7, 1);

		_objectDefinitionLocalService.deleteObjectDefinition(
			irrelevantObjectDefinition);

		_depotEntryLocalService.deleteDepotEntry(depotEntry.getDepotEntryId());

		_testGetAssetStatisticsByAssetLibrary();
	}

	@Override
	@Test
	public void testGraphQLGetAssetStatistics() throws Exception {
	}

	private ObjectEntry _addObjectEntry(
			DepotEntry depotEntry, ObjectDefinition objectDefinition)
		throws Exception {

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					"L_CONTENTS", depotEntry.getGroupId(),
					depotEntry.getCompanyId());

		return _objectEntryLocalService.addObjectEntry(
			depotEntry.getGroupId(), depotEntry.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			objectEntryFolder.getObjectEntryFolderId(), "en_US",
			HashMapBuilder.<String, Serializable>put(
				"content_i18n",
				HashMapBuilder.put(
					"en_US", RandomTestUtil.randomString()
				).build()
			).put(
				"title_i18n",
				HashMapBuilder.put(
					"en_US", RandomTestUtil.randomString()
				).build()
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private DepotEntry _addSpaceDepotEntry(ServiceContext serviceContext)
		throws Exception {

		return _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			DepotConstants.TYPE_SPACE, serviceContext);
	}

	private User _addUserWithRole(String roleName) throws Exception {
		User user = UserTestUtil.addUser(
			testCompany, RandomTestUtil.randomString());

		Role role = _roleLocalService.getRole(
			testCompany.getCompanyId(), roleName);

		_userLocalService.addRoleUser(role.getRoleId(), user.getUserId());

		return user;
	}

	private void _assertAssetStatistics(
			Long assetLibraryId, long expectedApprovedCount,
			long expectedExpiredCount, long expectedExpiringSoonCount,
			long expectedInDraftCount, long expectedPendingCount,
			long expectedReviewDateOverdueCount, long expectedScheduledCount,
			long expectedTotalCount, long expectedUpcomingReviewCount)
		throws Exception {

		for (AssetStatisticsResource assetStatisticsResource :
				_assetStatisticsResources) {

			AssetStatistics assetStatistics =
				assetStatisticsResource.getAssetStatistics(assetLibraryId);

			Assert.assertEquals(
				expectedApprovedCount,
				GetterUtil.getLong(assetStatistics.getApprovedCount()));
			Assert.assertEquals(
				expectedExpiredCount,
				GetterUtil.getLong(assetStatistics.getExpiredCount()));
			Assert.assertEquals(
				expectedExpiringSoonCount,
				GetterUtil.getLong(assetStatistics.getExpiringSoonCount()));
			Assert.assertEquals(
				expectedInDraftCount,
				GetterUtil.getLong(assetStatistics.getInDraftCount()));
			Assert.assertEquals(
				expectedPendingCount,
				GetterUtil.getLong(assetStatistics.getPendingCount()));
			Assert.assertEquals(
				expectedReviewDateOverdueCount,
				GetterUtil.getLong(
					assetStatistics.getReviewDateOverdueCount()));
			Assert.assertEquals(
				expectedScheduledCount,
				GetterUtil.getLong(assetStatistics.getScheduledCount()));
			Assert.assertEquals(
				expectedTotalCount,
				GetterUtil.getLong(assetStatistics.getTotalCount()));
			Assert.assertEquals(
				expectedUpcomingReviewCount,
				GetterUtil.getLong(assetStatistics.getUpcomingReviewCount()));
		}
	}

	private AssetStatisticsResource _buildAssetStatisticsResource(User user) {
		return AssetStatisticsResource.builder(
		).authentication(
			user.getEmailAddress(), user.getPasswordUnencrypted()
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	private ObjectDefinition _getBasicWebContentObjectDefinition()
		throws Exception {

		Group cmsGroup = CMSTestUtil.getOrAddGroup(
			AssetStatisticsResourceTest.class);

		return _objectDefinitionLocalService.
			getObjectDefinitionByExternalReferenceCode(
				"L_CMS_BASIC_WEB_CONTENT", cmsGroup.getCompanyId());
	}

	private void _testGetAssetStatisticsByAssetLibrary() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		DepotEntry depotEntry1 = _addSpaceDepotEntry(serviceContext);
		DepotEntry depotEntry2 = _addSpaceDepotEntry(serviceContext);

		ObjectDefinition objectDefinition =
			_getBasicWebContentObjectDefinition();

		Date date = new Date();

		_addObjectEntry(depotEntry1, objectDefinition);

		ObjectEntry objectEntry = _addObjectEntry(
			depotEntry1, objectDefinition);

		objectEntry.setReviewDate(new Date(date.getTime() + (3 * Time.DAY)));

		_objectEntryLocalService.updateObjectEntry(objectEntry);

		_addObjectEntry(depotEntry2, objectDefinition);

		ObjectEntry pendingObjectEntry = _addObjectEntry(
			depotEntry2, objectDefinition);

		_objectEntryLocalService.updateStatus(
			TestPropsValues.getUserId(), pendingObjectEntry.getObjectEntryId(),
			WorkflowConstants.STATUS_PENDING, serviceContext);

		_assertAssetStatistics(
			depotEntry1.getGroupId(), 2, 0, 0, 0, 0, 0, 0, 2, 1);
		_assertAssetStatistics(
			depotEntry1.getDepotEntryId(), 2, 0, 0, 0, 0, 0, 0, 2, 1);

		_assertAssetStatistics(
			depotEntry2.getGroupId(), 1, 0, 0, 0, 1, 0, 0, 2, 0);
		_assertAssetStatistics(
			depotEntry2.getDepotEntryId(), 1, 0, 0, 0, 1, 0, 0, 2, 0);

		_depotEntryLocalService.deleteDepotEntry(depotEntry1.getDepotEntryId());
		_depotEntryLocalService.deleteDepotEntry(depotEntry2.getDepotEntryId());
	}

	private AssetStatisticsResource[] _assetStatisticsResources;
	private User _cmsAdministratorUser;
	private User _companyAdminUser;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}