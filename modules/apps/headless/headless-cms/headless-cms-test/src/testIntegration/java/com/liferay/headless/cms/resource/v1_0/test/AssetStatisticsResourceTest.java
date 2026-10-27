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
import com.liferay.headless.cms.resource.v1_0.test.util.CMSFreeTierTestUtil;
import com.liferay.headless.cms.resource.v1_0.test.util.CMSOutboundLinkTestUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
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
import com.liferay.portal.search.document.DocumentBuilderFactory;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.document.DeleteDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentRequest;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;
import com.liferay.portal.workflow.kaleo.service.KaleoTaskInstanceTokenLocalService;
import com.liferay.portal.workflow.metrics.sla.processor.WorkflowMetricsSLAStatus;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

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

	@FeatureFlag("LPD-82226")
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

		_assertAssetStatistics(groupId, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

		ObjectDefinition objectDefinition =
			_getCMSBasicWebContentObjectDefinition();

		Date date = new Date();

		// Add object entry in a status that is not visible in the All view

		ObjectEntry objectEntry1 = _addObjectEntry(
			depotEntry, objectDefinition);

		_objectEntryLocalService.updateStatus(
			TestPropsValues.getUserId(), objectEntry1.getObjectEntryId(),
			WorkflowConstants.STATUS_DENIED, serviceContext);

		_assertAssetStatistics(groupId, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

		// Add object entry with already passed expiration date

		ObjectEntry objectEntry2 = _addObjectEntry(
			depotEntry, objectDefinition);

		objectEntry2.setExpirationDate(
			new Date(date.getTime() - (2 * Time.DAY)));

		_objectEntryLocalService.updateObjectEntry(objectEntry2);

		_assertAssetStatistics(groupId, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0);

		// Add object entry with distant expiration date

		ObjectEntry objectEntry3 = _addObjectEntry(
			depotEntry, objectDefinition);

		objectEntry3.setExpirationDate(
			new Date(date.getTime() + (10 * Time.DAY)));

		_objectEntryLocalService.updateObjectEntry(objectEntry3);

		_assertAssetStatistics(groupId, 2, 0, 1, 0, 0, 0, 0, 0, 2, 0);

		// Add object entry with future review date

		ObjectEntry objectEntry4 = _addObjectEntry(
			depotEntry, objectDefinition);

		objectEntry4.setReviewDate(new Date(date.getTime() + (5 * Time.DAY)));

		_objectEntryLocalService.updateObjectEntry(objectEntry4);

		_assertAssetStatistics(groupId, 3, 0, 1, 0, 0, 0, 0, 0, 3, 1);

		// Add object entry with imminent expiration date

		ObjectEntry objectEntry5 = _addObjectEntry(
			depotEntry, objectDefinition);

		objectEntry5.setExpirationDate(
			new Date(date.getTime() + (3 * Time.DAY)));

		_objectEntryLocalService.updateObjectEntry(objectEntry5);

		_assertAssetStatistics(groupId, 4, 0, 2, 0, 0, 0, 0, 0, 4, 1);

		// Add object entry with overdue review date

		ObjectEntry objectEntry6 = _addObjectEntry(
			depotEntry, objectDefinition);

		objectEntry6.setReviewDate(new Date(date.getTime() - (2 * Time.DAY)));

		_objectEntryLocalService.updateObjectEntry(objectEntry6);

		_assertAssetStatistics(groupId, 5, 0, 2, 0, 0, 0, 1, 0, 5, 1);

		// Add object entry with status approved not modified for more than 30
		// days

		ObjectEntry objectEntry7 = _addObjectEntry(
			depotEntry, objectDefinition);

		objectEntry7.setModifiedDate(
			new Date(date.getTime() - (31 * Time.DAY)));

		_objectEntryLocalService.updateObjectEntry(objectEntry7);

		_assertAssetStatistics(groupId, 6, 0, 2, 0, 0, 0, 1, 0, 6, 1);

		// Add object entry with status draft

		ObjectEntry objectEntry8 = _addObjectEntry(
			depotEntry, objectDefinition);

		_objectEntryLocalService.updateStatus(
			TestPropsValues.getUserId(), objectEntry8.getObjectEntryId(),
			WorkflowConstants.STATUS_DRAFT, serviceContext);

		_assertAssetStatistics(groupId, 6, 0, 2, 1, 0, 0, 1, 0, 7, 1);

		// Add object entry with status draft not modified for more than 30 days

		ObjectEntry objectEntry9 = _addObjectEntry(
			depotEntry, objectDefinition);

		objectEntry9 = _objectEntryLocalService.updateStatus(
			TestPropsValues.getUserId(), objectEntry9.getObjectEntryId(),
			WorkflowConstants.STATUS_DRAFT, serviceContext);

		objectEntry9.setModifiedDate(
			new Date(date.getTime() - (31 * Time.DAY)));

		_objectEntryLocalService.updateObjectEntry(objectEntry9);

		_assertAssetStatistics(groupId, 6, 0, 2, 2, 1, 0, 1, 0, 8, 1);

		// Add object entry with status expired

		ObjectEntry objectEntry10 = _addObjectEntry(
			depotEntry, objectDefinition);

		_objectEntryLocalService.updateStatus(
			TestPropsValues.getUserId(), objectEntry10.getObjectEntryId(),
			WorkflowConstants.STATUS_EXPIRED, serviceContext);

		_assertAssetStatistics(groupId, 6, 1, 2, 2, 1, 0, 1, 0, 9, 1);

		_objectDefinitionLocalService.deleteObjectDefinition(
			irrelevantObjectDefinition);

		_depotEntryLocalService.deleteDepotEntry(depotEntry.getDepotEntryId());

		_testGetAssetStatisticsBrokenLinksCount();
		_testGetAssetStatisticsByAssetLibrary();
		_testGetAssetStatisticsMissingRequiredFieldsCount();
		_testGetAssetStatisticsSLABreachedWorkflowTasksCount();
		_testGetAssetStatisticsUnlinkedCount();
		_testGetAssetStatisticsWithFreeTier();
		_testGetAssetStatisticsWorkflowTasksCounts();
	}

	@Override
	@Test
	public void testGraphQLGetAssetStatistics() throws Exception {
	}

	private ObjectEntry _addObjectEntry(
			DepotEntry depotEntry, ObjectDefinition objectDefinition)
		throws Exception {

		return _addObjectEntry(
			RandomTestUtil.randomString(), depotEntry, objectDefinition);
	}

	private ObjectEntry _addObjectEntry(
			String content, DepotEntry depotEntry,
			ObjectDefinition objectDefinition)
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
					"en_US", content
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

	private ObjectEntry _addTextObjectEntry(
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
				"text", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());
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
			long expectedInDraftCount, long expectedLongStandingDraftsCount,
			long expectedPendingCount, long expectedReviewDateOverdueCount,
			long expectedScheduledCount, long expectedTotalCount,
			long expectedUpcomingReviewCount)
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
				expectedLongStandingDraftsCount,
				GetterUtil.getLong(
					assetStatistics.getLongStandingDraftsCount()));
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

	private void _assertBrokenLinksCount(
			Long assetLibraryId, long expectedBrokenLinksCount)
		throws Exception {

		for (AssetStatisticsResource assetStatisticsResource :
				_assetStatisticsResources) {

			AssetStatistics assetStatistics =
				assetStatisticsResource.getAssetStatistics(assetLibraryId);

			Assert.assertEquals(
				expectedBrokenLinksCount,
				GetterUtil.getLong(assetStatistics.getBrokenLinksCount()));
		}
	}

	private void _assertMissingRequiredFieldsCount(
			Long assetLibraryId, long expectedMissingRequiredFieldsCount)
		throws Exception {

		for (AssetStatisticsResource assetStatisticsResource :
				_assetStatisticsResources) {

			AssetStatistics assetStatistics =
				assetStatisticsResource.getAssetStatistics(assetLibraryId);

			Assert.assertEquals(
				expectedMissingRequiredFieldsCount,
				GetterUtil.getLong(
					assetStatistics.getMissingRequiredFieldsCount()));
		}
	}

	private void _assertSLABreachedWorkflowTasksCount(
			Long assetLibraryId, long expectedSLABreachedWorkflowTasksCount)
		throws Exception {

		for (AssetStatisticsResource assetStatisticsResource :
				_assetStatisticsResources) {

			AssetStatistics assetStatistics =
				assetStatisticsResource.getAssetStatistics(assetLibraryId);

			Assert.assertEquals(
				expectedSLABreachedWorkflowTasksCount,
				GetterUtil.getLong(
					assetStatistics.getSlaBreachedWorkflowTasksCount()));
		}
	}

	private void _assertUnlinkedCount(
			Long assetLibraryId, long expectedUnlinkedCount)
		throws Exception {

		for (AssetStatisticsResource assetStatisticsResource :
				_assetStatisticsResources) {

			AssetStatistics assetStatistics =
				assetStatisticsResource.getAssetStatistics(assetLibraryId);

			Assert.assertEquals(
				expectedUnlinkedCount,
				GetterUtil.getLong(assetStatistics.getUnlinkedCount()));
		}
	}

	private void _assertWorkflowTasksCounts(
			Long assetLibraryId, long expectedOverdueWorkflowTasksCount,
			long expectedWorkflowTasksCount)
		throws Exception {

		for (AssetStatisticsResource assetStatisticsResource :
				_assetStatisticsResources) {

			AssetStatistics assetStatistics =
				assetStatisticsResource.getAssetStatistics(assetLibraryId);

			Assert.assertEquals(
				expectedOverdueWorkflowTasksCount,
				GetterUtil.getLong(
					assetStatistics.getOverdueWorkflowTasksCount()));
			Assert.assertEquals(
				expectedWorkflowTasksCount,
				GetterUtil.getLong(assetStatistics.getWorkflowTasksCount()));
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

	private ObjectDefinition _getCMSBasicWebContentObjectDefinition()
		throws Exception {

		return _objectDefinitionLocalService.
			getObjectDefinitionByExternalReferenceCode(
				"L_CMS_BASIC_WEB_CONTENT", TestPropsValues.getCompanyId());
	}

	private String _getSLATaskResultsIndexName() throws Exception {
		return _indexNameBuilder.getIndexName(TestPropsValues.getCompanyId()) +
			"-workflow-metrics-sla-task-results";
	}

	private void _indexSLATaskResult(
			boolean breached, ObjectDefinition objectDefinition,
			ObjectEntry objectEntry, boolean onTime,
			WorkflowMetricsSLAStatus workflowMetricsSLAStatus)
		throws Exception {

		List<KaleoTaskInstanceToken> kaleoTaskInstanceTokens =
			_kaleoTaskInstanceTokenLocalService.getKaleoTaskInstanceTokens(
				objectDefinition.getClassName(),
				objectEntry.getObjectEntryId());

		KaleoTaskInstanceToken kaleoTaskInstanceToken =
			kaleoTaskInstanceTokens.get(0);

		String uid = RandomTestUtil.randomString();

		IndexDocumentRequest indexDocumentRequest = new IndexDocumentRequest(
			_getSLATaskResultsIndexName(), uid,
			DocumentBuilderFactory.builder(
			).setValue(
				"breached", breached
			).setLong(
				"companyId", TestPropsValues.getCompanyId()
			).setValue(
				"deleted", false
			).setValue(
				"onTime", onTime
			).setLong(
				"slaDefinitionId", RandomTestUtil.randomLong()
			).setString(
				"status", workflowMetricsSLAStatus.name()
			).setLong(
				"taskId", kaleoTaskInstanceToken.getKaleoTaskInstanceTokenId()
			).setString(
				"uid", uid
			).build());

		indexDocumentRequest.setRefresh(true);

		_searchEngineAdapter.execute(indexDocumentRequest);

		_slaTaskResultUids.add(uid);
	}

	private void _testGetAssetStatisticsBrokenLinksCount() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		DepotEntry depotEntry = _addSpaceDepotEntry(serviceContext);

		try {
			ObjectDefinition objectDefinition =
				_getCMSBasicWebContentObjectDefinition();

			ObjectEntry targetObjectEntry = _addObjectEntry(
				depotEntry, objectDefinition);

			_addObjectEntry(
				CMSOutboundLinkTestUtil.getImageHTML(
					targetObjectEntry.getExternalReferenceCode()),
				depotEntry, objectDefinition);

			_assertBrokenLinksCount(depotEntry.getGroupId(), 0);

			_objectEntryLocalService.updateStatus(
				TestPropsValues.getUserId(),
				targetObjectEntry.getObjectEntryId(),
				WorkflowConstants.STATUS_EXPIRED, serviceContext);

			_assertBrokenLinksCount(depotEntry.getGroupId(), 1);

			ObjectEntry referringObjectEntry = _addObjectEntry(
				CMSOutboundLinkTestUtil.getImageHTML(
					targetObjectEntry.getExternalReferenceCode()),
				depotEntry, objectDefinition);

			_assertBrokenLinksCount(depotEntry.getGroupId(), 2);

			ObjectEntry otherTargetObjectEntry = _addObjectEntry(
				depotEntry, objectDefinition);

			_objectEntryLocalService.updateStatus(
				TestPropsValues.getUserId(),
				otherTargetObjectEntry.getObjectEntryId(),
				WorkflowConstants.STATUS_EXPIRED, serviceContext);

			String imageHTML = CMSOutboundLinkTestUtil.getImageHTML(
				targetObjectEntry.getExternalReferenceCode());
			String otherImageHTML = CMSOutboundLinkTestUtil.getImageHTML(
				otherTargetObjectEntry.getExternalReferenceCode());

			_addObjectEntry(
				imageHTML + otherImageHTML, depotEntry, objectDefinition);

			_assertBrokenLinksCount(depotEntry.getGroupId(), 3);

			_objectEntryLocalService.updateStatus(
				TestPropsValues.getUserId(),
				referringObjectEntry.getObjectEntryId(),
				WorkflowConstants.STATUS_EXPIRED, serviceContext);

			_assertBrokenLinksCount(depotEntry.getGroupId(), 2);

			ObjectEntry trashedObjectEntry = _addObjectEntry(
				depotEntry, objectDefinition);

			_addObjectEntry(
				CMSOutboundLinkTestUtil.getImageHTML(
					trashedObjectEntry.getExternalReferenceCode()),
				depotEntry, objectDefinition);

			_assertBrokenLinksCount(depotEntry.getGroupId(), 2);

			_objectEntryLocalService.moveObjectEntryToTrash(
				TestPropsValues.getUserId(), trashedObjectEntry,
				serviceContext);

			_assertBrokenLinksCount(depotEntry.getGroupId(), 3);

			ObjectEntry deletedObjectEntry = _addObjectEntry(
				depotEntry, objectDefinition);

			_addObjectEntry(
				CMSOutboundLinkTestUtil.getImageHTML(
					deletedObjectEntry.getExternalReferenceCode()),
				depotEntry, objectDefinition);

			_assertBrokenLinksCount(depotEntry.getGroupId(), 3);

			_objectEntryLocalService.deleteObjectEntry(deletedObjectEntry);

			_assertBrokenLinksCount(depotEntry.getGroupId(), 4);
		}
		finally {
			_depotEntryLocalService.deleteDepotEntry(
				depotEntry.getDepotEntryId());
		}
	}

	private void _testGetAssetStatisticsByAssetLibrary() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		DepotEntry depotEntry1 = _addSpaceDepotEntry(serviceContext);
		DepotEntry depotEntry2 = _addSpaceDepotEntry(serviceContext);

		ObjectDefinition objectDefinition =
			_getCMSBasicWebContentObjectDefinition();

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

		ObjectEntry longStandingDraftObjectEntry = _addObjectEntry(
			depotEntry2, objectDefinition);

		longStandingDraftObjectEntry = _objectEntryLocalService.updateStatus(
			TestPropsValues.getUserId(),
			longStandingDraftObjectEntry.getObjectEntryId(),
			WorkflowConstants.STATUS_DRAFT, serviceContext);

		longStandingDraftObjectEntry.setModifiedDate(
			new Date(date.getTime() - (31 * Time.DAY)));

		_objectEntryLocalService.updateObjectEntry(
			longStandingDraftObjectEntry);

		_assertAssetStatistics(
			depotEntry1.getGroupId(), 2, 0, 0, 0, 0, 0, 0, 0, 2, 1);
		_assertAssetStatistics(
			depotEntry1.getDepotEntryId(), 2, 0, 0, 0, 0, 0, 0, 0, 2, 1);

		_assertAssetStatistics(
			depotEntry2.getGroupId(), 1, 0, 0, 1, 1, 1, 0, 0, 3, 0);
		_assertAssetStatistics(
			depotEntry2.getDepotEntryId(), 1, 0, 0, 1, 1, 1, 0, 0, 3, 0);

		_depotEntryLocalService.deleteDepotEntry(depotEntry1.getDepotEntryId());
		_depotEntryLocalService.deleteDepotEntry(depotEntry2.getDepotEntryId());
	}

	private void _testGetAssetStatisticsMissingRequiredFieldsCount()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		DepotEntry depotEntry1 = _addSpaceDepotEntry(serviceContext);
		DepotEntry depotEntry2 = _addSpaceDepotEntry(serviceContext);

		ObjectFolder objectFolder =
			_objectFolderLocalService.getObjectFolderByExternalReferenceCode(
				ObjectFolderConstants.
					EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES,
				TestPropsValues.getCompanyId());

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				true, false, true, ObjectDefinitionTestUtil.getRandomName(),
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), "text")),
				objectFolder.getObjectFolderId(),
				ObjectDefinitionConstants.SCOPE_DEPOT,
				TestPropsValues.getUserId());

		try {
			_objectDefinitionSettingLocalService.addObjectDefinitionSetting(
				TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId(),
				ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS,
				"true");

			ObjectEntry objectEntry = _addTextObjectEntry(
				depotEntry1, objectDefinition);

			_addTextObjectEntry(depotEntry2, objectDefinition);

			_assertMissingRequiredFieldsCount(depotEntry1.getGroupId(), 0);

			ObjectField objectField =
				_objectFieldLocalService.addCustomObjectField(
					null, TestPropsValues.getUserId(), 0,
					objectDefinition.getObjectDefinitionId(),
					ObjectFieldConstants.BUSINESS_TYPE_INTEGER,
					ObjectFieldConstants.DB_TYPE_INTEGER, null, false, false,
					null,
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()),
					false, "integer", null, null, true, false,
					Collections.emptyList());

			_assertMissingRequiredFieldsCount(depotEntry1.getGroupId(), 1);
			_assertMissingRequiredFieldsCount(depotEntry2.getGroupId(), 1);

			ObjectEntryFolder objectEntryFolder =
				_objectEntryFolderLocalService.
					getObjectEntryFolderByExternalReferenceCode(
						"L_CONTENTS", depotEntry1.getGroupId(),
						depotEntry1.getCompanyId());

			_objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
				objectEntryFolder.getObjectEntryFolderId(),
				HashMapBuilder.<String, Serializable>put(
					"integer", 0
				).put(
					"text", RandomTestUtil.randomString()
				).build(),
				serviceContext);

			_assertMissingRequiredFieldsCount(depotEntry1.getGroupId(), 0);
			_assertMissingRequiredFieldsCount(depotEntry2.getGroupId(), 1);

			_objectFieldLocalService.deleteObjectField(
				objectField.getObjectFieldId());

			_assertMissingRequiredFieldsCount(depotEntry2.getGroupId(), 0);
		}
		finally {
			_objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition.getObjectDefinitionId());

			_depotEntryLocalService.deleteDepotEntry(
				depotEntry1.getDepotEntryId());
			_depotEntryLocalService.deleteDepotEntry(
				depotEntry2.getDepotEntryId());
		}
	}

	private void _testGetAssetStatisticsSLABreachedWorkflowTasksCount()
		throws Exception {

		// Add object entry with breached workflow task

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		DepotEntry depotEntry1 = _addSpaceDepotEntry(serviceContext);
		DepotEntry depotEntry2 = _addSpaceDepotEntry(serviceContext);

		ObjectDefinition objectDefinition =
			_getCMSBasicWebContentObjectDefinition();

		for (DepotEntry depotEntry :
				new DepotEntry[] {depotEntry1, depotEntry2}) {

			_workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
				TestPropsValues.getUserId(), TestPropsValues.getCompanyId(),
				depotEntry.getGroupId(), objectDefinition.getClassName(), 0, 0,
				"Single Approver", 1);
		}

		_indexSLATaskResult(
			true, objectDefinition,
			_addObjectEntry(depotEntry1, objectDefinition), false,
			WorkflowMetricsSLAStatus.RUNNING);

		_assertSLABreachedWorkflowTasksCount(depotEntry1.getGroupId(), 1);

		// Add object entry with breached workflow task on another space

		_indexSLATaskResult(
			true, objectDefinition,
			_addObjectEntry(depotEntry2, objectDefinition), false,
			WorkflowMetricsSLAStatus.RUNNING);

		_assertSLABreachedWorkflowTasksCount(depotEntry1.getGroupId(), 1);
		_assertSLABreachedWorkflowTasksCount(depotEntry1.getDepotEntryId(), 1);

		_assertSLABreachedWorkflowTasksCount(depotEntry2.getGroupId(), 1);
		_assertSLABreachedWorkflowTasksCount(depotEntry2.getDepotEntryId(), 1);

		_assertSLABreachedWorkflowTasksCount(null, 2);

		// Add object entry with completed breached workflow task

		ObjectEntry objectEntry1 = _addObjectEntry(
			depotEntry1, objectDefinition);

		_indexSLATaskResult(
			true, objectDefinition, objectEntry1, false,
			WorkflowMetricsSLAStatus.COMPLETED);

		_updateKaleoTaskInstanceToken(
			true, null, objectDefinition, objectEntry1);

		_assertSLABreachedWorkflowTasksCount(depotEntry1.getGroupId(), 1);

		// Add object entry with workflow task breached by two SLAs

		ObjectEntry objectEntry2 = _addObjectEntry(
			depotEntry1, objectDefinition);

		_indexSLATaskResult(
			true, objectDefinition, objectEntry2, false,
			WorkflowMetricsSLAStatus.RUNNING);
		_indexSLATaskResult(
			true, objectDefinition, objectEntry2, false,
			WorkflowMetricsSLAStatus.RUNNING);

		_assertSLABreachedWorkflowTasksCount(depotEntry1.getGroupId(), 2);

		// Add object entry with workflow task on time

		_indexSLATaskResult(
			false, objectDefinition,
			_addObjectEntry(depotEntry1, objectDefinition), true,
			WorkflowMetricsSLAStatus.RUNNING);

		_assertSLABreachedWorkflowTasksCount(depotEntry1.getGroupId(), 2);

		// Add object entry with workflow task created after the SLA deadline

		_indexSLATaskResult(
			false, objectDefinition,
			_addObjectEntry(depotEntry1, objectDefinition), false,
			WorkflowMetricsSLAStatus.RUNNING);

		_assertSLABreachedWorkflowTasksCount(depotEntry1.getGroupId(), 2);

		// Add object entry with workflow task whose SLA has not started

		_indexSLATaskResult(
			true, objectDefinition,
			_addObjectEntry(depotEntry1, objectDefinition), false,
			WorkflowMetricsSLAStatus.NEW);

		_assertSLABreachedWorkflowTasksCount(depotEntry1.getGroupId(), 2);

		for (String slaTaskResultUid : _slaTaskResultUids) {
			DeleteDocumentRequest deleteDocumentRequest =
				new DeleteDocumentRequest(
					_getSLATaskResultsIndexName(), slaTaskResultUid);

			deleteDocumentRequest.setRefresh(true);

			_searchEngineAdapter.execute(deleteDocumentRequest);
		}

		_depotEntryLocalService.deleteDepotEntry(depotEntry1.getDepotEntryId());
		_depotEntryLocalService.deleteDepotEntry(depotEntry2.getDepotEntryId());
	}

	private void _testGetAssetStatisticsUnlinkedCount() throws Exception {
		DepotEntry depotEntry = _addSpaceDepotEntry(
			ServiceContextTestUtil.getServiceContext());

		try {
			ObjectDefinition objectDefinition =
				_getCMSBasicWebContentObjectDefinition();

			ObjectEntry objectEntry = _addObjectEntry(
				depotEntry, objectDefinition);

			_assertUnlinkedCount(depotEntry.getGroupId(), 1);

			_addObjectEntry(depotEntry, objectDefinition);

			_assertUnlinkedCount(depotEntry.getGroupId(), 2);

			_addObjectEntry(
				CMSOutboundLinkTestUtil.getImageHTML(
					objectEntry.getExternalReferenceCode()),
				depotEntry, objectDefinition);

			_assertUnlinkedCount(depotEntry.getGroupId(), 2);
		}
		finally {
			_depotEntryLocalService.deleteDepotEntry(
				depotEntry.getDepotEntryId());
		}
	}

	private void _testGetAssetStatisticsWithFreeTier() throws Exception {
		try (AutoCloseable autoCloseable = CMSFreeTierTestUtil.withFreeTier()) {
			assertHttpResponseStatusCode(
				400,
				assetStatisticsResource.getAssetStatisticsHttpResponse(null));
		}
	}

	private void _testGetAssetStatisticsWorkflowTasksCounts() throws Exception {

		// Add object entry with completed workflow task past its due date

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		DepotEntry depotEntry1 = _addSpaceDepotEntry(serviceContext);
		DepotEntry depotEntry2 = _addSpaceDepotEntry(serviceContext);

		ObjectDefinition objectDefinition =
			_getCMSBasicWebContentObjectDefinition();

		for (DepotEntry depotEntry :
				new DepotEntry[] {depotEntry1, depotEntry2}) {

			_workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
				TestPropsValues.getUserId(), TestPropsValues.getCompanyId(),
				depotEntry.getGroupId(), objectDefinition.getClassName(), 0, 0,
				"Single Approver", 1);
		}

		Date date = new Date();

		_updateKaleoTaskInstanceToken(
			true, new Date(date.getTime() - Time.DAY), objectDefinition,
			_addObjectEntry(depotEntry1, objectDefinition));

		_assertWorkflowTasksCounts(depotEntry1.getGroupId(), 0, 0);

		// Add object entry with overdue workflow task

		_updateKaleoTaskInstanceToken(
			false, new Date(date.getTime() - Time.DAY), objectDefinition,
			_addObjectEntry(depotEntry1, objectDefinition));

		_assertWorkflowTasksCounts(depotEntry1.getGroupId(), 1, 1);

		// Add object entry with overdue workflow task on another space

		_updateKaleoTaskInstanceToken(
			false, new Date(date.getTime() - Time.DAY), objectDefinition,
			_addObjectEntry(depotEntry2, objectDefinition));

		_assertWorkflowTasksCounts(depotEntry1.getGroupId(), 1, 1);
		_assertWorkflowTasksCounts(depotEntry1.getDepotEntryId(), 1, 1);

		_assertWorkflowTasksCounts(depotEntry2.getGroupId(), 1, 1);
		_assertWorkflowTasksCounts(depotEntry2.getDepotEntryId(), 1, 1);

		// Add object entry with workflow task due in the future

		_updateKaleoTaskInstanceToken(
			false, new Date(date.getTime() + Time.DAY), objectDefinition,
			_addObjectEntry(depotEntry1, objectDefinition));

		_assertWorkflowTasksCounts(depotEntry1.getGroupId(), 1, 2);

		// Add object entry with workflow task without due date

		_addObjectEntry(depotEntry1, objectDefinition);

		_assertWorkflowTasksCounts(depotEntry1.getGroupId(), 1, 3);

		_depotEntryLocalService.deleteDepotEntry(depotEntry1.getDepotEntryId());
		_depotEntryLocalService.deleteDepotEntry(depotEntry2.getDepotEntryId());
	}

	private void _updateKaleoTaskInstanceToken(
			boolean completed, Date dueDate, ObjectDefinition objectDefinition,
			ObjectEntry objectEntry)
		throws Exception {

		List<KaleoTaskInstanceToken> kaleoTaskInstanceTokens =
			_kaleoTaskInstanceTokenLocalService.getKaleoTaskInstanceTokens(
				objectDefinition.getClassName(),
				objectEntry.getObjectEntryId());

		KaleoTaskInstanceToken kaleoTaskInstanceToken =
			kaleoTaskInstanceTokens.get(0);

		kaleoTaskInstanceToken.setCompleted(completed);
		kaleoTaskInstanceToken.setDueDate(dueDate);

		_kaleoTaskInstanceTokenLocalService.updateKaleoTaskInstanceToken(
			kaleoTaskInstanceToken);
	}

	private AssetStatisticsResource[] _assetStatisticsResources;
	private User _cmsAdministratorUser;
	private User _companyAdminUser;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private IndexNameBuilder _indexNameBuilder;

	@Inject
	private KaleoTaskInstanceTokenLocalService
		_kaleoTaskInstanceTokenLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectFolderLocalService _objectFolderLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private SearchEngineAdapter _searchEngineAdapter;

	private final List<String> _slaTaskResultUids = new ArrayList<>();

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}