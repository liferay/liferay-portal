/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.internal.runtime.integration.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.constants.AccountRoleConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.engine.CommerceOrderEngine;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalServiceUtil;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.document.library.kernel.service.DLTrashService;
import com.liferay.dynamic.data.lists.constants.DDLRecordConstants;
import com.liferay.dynamic.data.lists.constants.DDLRecordSetConstants;
import com.liferay.dynamic.data.lists.model.DDLRecord;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.lists.model.DDLRecordVersion;
import com.liferay.dynamic.data.lists.service.DDLRecordLocalService;
import com.liferay.dynamic.data.lists.service.DDLRecordSetLocalService;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestHelper;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalFolderLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.model.WorkflowInstanceLink;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.randomizerbumpers.NumericStringRandomizerBumper;
import com.liferay.portal.kernel.test.randomizerbumpers.UniqueStringRandomizerBumper;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.portal.kernel.workflow.WorkflowInstance;
import com.liferay.portal.kernel.workflow.WorkflowTask;
import com.liferay.portal.kernel.workflow.WorkflowTaskManager;
import com.liferay.portal.kernel.workflow.search.WorkflowModelSearchResult;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.security.permission.SimplePermissionChecker;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.util.PortalInstances;
import com.liferay.portal.workflow.comparator.WorkflowComparatorFactory;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Inácio Nery
 */
@RunWith(Arquillian.class)
public class WorkflowTaskManagerImplTest extends BaseWorkflowManagerTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		_company = CompanyTestUtil.addCompany();

		PortalInstances.initCompany(_company);

		_companyAdminUser = UserTestUtil.addCompanyAdminUser(_company);

		_configuration = _configurationAdmin.getConfiguration(
			"com.liferay.portal.workflow.configuration." +
				"WorkflowDefinitionConfiguration",
			StringPool.QUESTION);

		ConfigurationTestUtil.saveConfiguration(
			_configuration,
			HashMapDictionaryBuilder.<String, Object>put(
				"company.administrator.can.publish", true
			).build());

		_originalName = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(TestPropsValues.getUserId());
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_companyLocalService.deleteCompany(_company);

		ConfigurationTestUtil.deleteConfiguration(_configuration);

		PrincipalThreadLocal.setName(_originalName);
	}

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup(
			_company.getCompanyId(), _companyAdminUser.getUserId(), 0);

		_childGroup = GroupTestUtil.addGroup(_group.getGroupId());

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group, _companyAdminUser.getUserId());

		_setUpPermissionThreadLocal();
		_setUpPrincipalThreadLocal();
		_setUpUsers();
		_setUpWorkflow();
	}

	@After
	public void tearDown() throws PortalException {
		PermissionThreadLocal.setPermissionChecker(_permissionChecker);

		PrincipalThreadLocal.setName(_name);
	}

	@Test
	public void testApproveDLFileEntryInDLFolderWhenHomeDLFolderHasWorkflow()
		throws Exception {

		_activateSingleApproverWorkflow(DLFolder.class.getName(), 0, -1);

		Folder folder = _addFolder();

		FileVersion fileVersion1 = _addFileVersion(folder.getFolderId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, fileVersion1.getStatus());

		FileVersion fileVersion2 = _addFileVersion(folder.getFolderId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, fileVersion2.getStatus());

		_assignWorkflowTaskToUser(
			_adminUser, _adminUser, _REVIEW, DLFileEntry.class.getName(),
			fileVersion1.getFileVersionId());

		_assignWorkflowTaskToUser(
			_adminUser, _adminUser, _REVIEW, DLFileEntry.class.getName(),
			fileVersion2.getFileVersionId());

		_completeWorkflowTask(
			_adminUser, Constants.APPROVE, _REVIEW, DLFileEntry.class.getName(),
			fileVersion1.getFileVersionId());

		_completeWorkflowTask(
			_adminUser, Constants.APPROVE, _REVIEW, DLFileEntry.class.getName(),
			fileVersion2.getFileVersionId());

		fileVersion1 = _dlAppService.getFileVersion(
			fileVersion1.getFileVersionId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, fileVersion1.getStatus());

		fileVersion2 = _dlAppService.getFileVersion(
			fileVersion2.getFileVersionId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, fileVersion2.getStatus());

		_deactivateWorkflow(DLFolder.class.getName(), 0, -1);
	}

	@Test
	public void testApproveDLFileEntryInDLFolderWithSpecificType()
		throws Exception {

		DLFileEntryType fileEntryType = _addFileEntryType();

		Map<String, String> dlFileEntryTypeMap = HashMapBuilder.put(
			String.valueOf(fileEntryType.getFileEntryTypeId()),
			"Single Approver@1"
		).put(
			() -> {
				DLFileEntryType basicFileEntryType = _getBasicFileEntryType();

				return String.valueOf(basicFileEntryType.getFileEntryTypeId());
			},
			StringPool.BLANK
		).build();

		Folder folder = _addFolder();

		folder = _updateFolder(
			folder,
			DLFolderConstants.RESTRICTION_TYPE_FILE_ENTRY_TYPES_AND_WORKFLOW,
			fileEntryType.getFileEntryTypeId(), dlFileEntryTypeMap);

		FileVersion fileVersion1 = _addFileVersion(
			folder.getFolderId(), fileEntryType.getFileEntryTypeId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, fileVersion1.getStatus());

		FileVersion fileVersion2 = _addFileVersion(folder.getFolderId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, fileVersion2.getStatus());

		FileVersion fileVersion3 = _addFileVersion(
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, fileVersion3.getStatus());

		_assignWorkflowTaskToUser(
			_adminUser, _adminUser, _REVIEW, DLFileEntry.class.getName(),
			fileVersion1.getFileVersionId());

		_completeWorkflowTask(
			_adminUser, Constants.APPROVE, _REVIEW, DLFileEntry.class.getName(),
			fileVersion1.getFileVersionId());

		fileVersion1 = _dlAppService.getFileVersion(
			fileVersion1.getFileVersionId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, fileVersion1.getStatus());
	}

	@Test
	public void testApproveDLFileEntryInDLFolderWithWorkflow()
		throws Exception {

		Folder folder = _addFolder();

		folder = _updateFolder(
			folder, DLFolderConstants.RESTRICTION_TYPE_WORKFLOW,
			HashMapBuilder.put(
				String.valueOf(DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_ALL),
				"Single Approver@1"
			).build());

		FileVersion fileVersion1 = _addFileVersion(
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, fileVersion1.getStatus());

		FileVersion fileVersion2 = _addFileVersion(folder.getFolderId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, fileVersion2.getStatus());

		_assignWorkflowTaskToUser(
			_adminUser, _adminUser, _REVIEW, DLFileEntry.class.getName(),
			fileVersion2.getFileVersionId());

		_completeWorkflowTask(
			_adminUser, Constants.APPROVE, _REVIEW, DLFileEntry.class.getName(),
			fileVersion2.getFileVersionId());

		fileVersion2 = _dlAppService.getFileVersion(
			fileVersion2.getFileVersionId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, fileVersion2.getStatus());
	}

	@Test
	public void testApproveDLFileEntryInDLFolderWithoutWorkflowWhenHomeDLFolderHasWorkflow()
		throws Exception {

		_activateSingleApproverWorkflow(DLFolder.class.getName(), 0, -1);

		Folder folder = _addFolder();

		folder = _updateFolder(
			folder, DLFolderConstants.RESTRICTION_TYPE_WORKFLOW);

		FileVersion fileVersion1 = _addFileVersion(folder.getFolderId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, fileVersion1.getStatus());

		FileVersion fileVersion2 = _addFileVersion(
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, fileVersion2.getStatus());

		_assignWorkflowTaskToUser(
			_adminUser, _adminUser, _REVIEW, DLFileEntry.class.getName(),
			fileVersion2.getFileVersionId());

		_completeWorkflowTask(
			_adminUser, Constants.APPROVE, _REVIEW, DLFileEntry.class.getName(),
			fileVersion2.getFileVersionId());

		fileVersion2 = _dlAppService.getFileVersion(
			fileVersion2.getFileVersionId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, fileVersion2.getStatus());

		_deactivateWorkflow(DLFolder.class.getName(), 0, -1);
	}

	@Test
	public void testApproveJoinXorWorkflow() throws Exception {
		_activateWorkflow(BlogsEntry.class.getName(), 0, 0, _JOIN_XOR, 1);

		BlogsEntry blogsEntry = _addBlogsEntry();

		_assignWorkflowTaskToUser(_siteAdminUser, _siteAdminUser, "task1");

		_completeWorkflowTask(_siteAdminUser, "join-xor", "task1");

		WorkflowTask workflowTask2 = _getWorkflowTask(
			_siteAdminUser, "task2", true, BlogsEntry.class.getName(),
			blogsEntry.getEntryId());

		Assert.assertTrue(workflowTask2.isCompleted());

		blogsEntry = _blogsEntryLocalService.getBlogsEntry(
			blogsEntry.getEntryId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, blogsEntry.getStatus());

		_deactivateWorkflow(BlogsEntry.class.getName(), 0, 0);
	}

	@Test
	public void testApproveJournalArticleAsAdmin() throws Exception {
		_activateSingleApproverWorkflow(
			JournalFolder.class.getName(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.DDM_STRUCTURE_ID_ALL);

		JournalArticle article = _addJournalArticle(
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, article.getStatus());

		_checkUserNotificationEventsByUsers(
			1, _adminUser, _portalContentReviewerUser, _siteAdminUser);

		_assignWorkflowTaskToUser(_adminUser, _adminUser);

		_completeWorkflowTask(_adminUser, Constants.APPROVE);

		_getWorkflowInstance(JournalArticle.class.getName(), article.getId());

		article = _journalArticleLocalService.getArticle(article.getId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, article.getStatus());

		_deactivateWorkflow(
			JournalFolder.class.getName(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.DDM_STRUCTURE_ID_ALL);
	}

	@Test
	public void testApproveJournalArticleInFolderInheritedWorkflow()
		throws Exception {

		_activateSingleApproverWorkflow(
			JournalFolder.class.getName(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.DDM_STRUCTURE_ID_ALL);

		JournalFolder folder = _addJournalFolder();

		JournalArticle article = _addJournalArticle(folder.getFolderId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, article.getStatus());

		_checkUserNotificationEventsByUsers(
			1, _adminUser, _portalContentReviewerUser, _siteAdminUser);

		_assignWorkflowTaskToUser(_adminUser, _adminUser);

		_completeWorkflowTask(_adminUser, Constants.APPROVE);

		_getWorkflowInstance(JournalArticle.class.getName(), article.getId());

		article = _journalArticleLocalService.getArticle(article.getId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, article.getStatus());

		_deactivateWorkflow(
			JournalFolder.class.getName(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.DDM_STRUCTURE_ID_ALL);
	}

	@Test
	public void testApproveJournalArticleInFolderStructureSpecificWorkflow()
		throws Exception {

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		JournalFolder folder = _addJournalFolder(
			ddmStructure.getStructureId(),
			JournalFolderConstants.
				RESTRICTION_TYPE_DDM_STRUCTURES_AND_WORKFLOW);

		_activateSingleApproverWorkflow(
			JournalFolder.class.getName(), folder.getFolderId(),
			ddmStructure.getStructureId());

		JournalArticle article = _addJournalArticle(
			folder.getFolderId(), ddmStructure);

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, article.getStatus());

		_checkUserNotificationEventsByUsers(
			1, _adminUser, _portalContentReviewerUser, _siteAdminUser);

		_assignWorkflowTaskToUser(_adminUser, _adminUser);

		_completeWorkflowTask(_adminUser, Constants.APPROVE);

		_getWorkflowInstance(JournalArticle.class.getName(), article.getId());

		article = _journalArticleLocalService.getArticle(article.getId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, article.getStatus());

		_deactivateWorkflow(
			JournalFolder.class.getName(), folder.getFolderId(),
			ddmStructure.getStructureId());
	}

	@Test
	public void testApproveJournalArticleUsingFolderSpecificWorkflow()
		throws Exception {

		JournalFolder folder = _addJournalFolder(
			0, JournalFolderConstants.RESTRICTION_TYPE_WORKFLOW);

		_activateSingleApproverWorkflow(
			JournalFolder.class.getName(), folder.getFolderId(),
			JournalArticleConstants.DDM_STRUCTURE_ID_ALL);

		JournalArticle article = _addJournalArticle(folder.getFolderId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, article.getStatus());

		_checkUserNotificationEventsByUsers(
			1, _adminUser, _portalContentReviewerUser, _siteAdminUser);

		_assignWorkflowTaskToUser(_adminUser, _adminUser);

		_completeWorkflowTask(_adminUser, Constants.APPROVE);

		_getWorkflowInstance(JournalArticle.class.getName(), article.getId());

		article = _journalArticleLocalService.getArticle(article.getId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, article.getStatus());

		_deactivateWorkflow(
			JournalFolder.class.getName(), folder.getFolderId(),
			JournalArticleConstants.DDM_STRUCTURE_ID_ALL);
	}

	@Test
	public void testApproveOrganizationParentReviewer() throws Exception {
		Organization parentOrganization = _createOrganization(true);

		User reviewerUser = _createUser(
			_ORGANIZATION_CONTENT_REVIEWER, parentOrganization.getGroup());

		_organizationLocalService.addUserOrganization(
			reviewerUser.getUserId(), parentOrganization);

		Organization childOrganization = _createOrganization(
			parentOrganization.getOrganizationId(), true);

		User memberUser = _createUser(
			RoleConstants.ORGANIZATION_ADMINISTRATOR,
			childOrganization.getGroup());

		_organizationLocalService.addUserOrganization(
			memberUser.getUserId(), childOrganization);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			childOrganization.getGroupId());

		_activateSingleApproverWorkflow(
			childOrganization.getGroupId(), BlogsEntry.class.getName(), 0, 0);

		BlogsEntry blogsEntry = _addBlogsEntry(memberUser);

		_checkUserNotificationEventsByUsers(1, reviewerUser);

		_assignWorkflowTaskToUser(reviewerUser, reviewerUser);

		_completeWorkflowTask(reviewerUser, Constants.APPROVE);

		blogsEntry = _blogsEntryLocalService.getBlogsEntry(
			blogsEntry.getEntryId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, blogsEntry.getStatus());

		_deactivateWorkflow(
			childOrganization.getGroupId(), BlogsEntry.class.getName(), 0, 0);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());
	}

	@Test
	public void testApproveOrganizationParentReviewerWithoutSite()
		throws Exception {

		Organization parentOrganization = _createOrganization(false);

		User reviewerUser = _createUser(
			_ORGANIZATION_CONTENT_REVIEWER, parentOrganization.getGroup());

		_organizationLocalService.addUserOrganization(
			reviewerUser.getUserId(), parentOrganization);

		Organization childOrganization = _createOrganization(
			parentOrganization.getOrganizationId(), true);

		User memberUser = _createUser(
			RoleConstants.ORGANIZATION_ADMINISTRATOR,
			childOrganization.getGroup());

		_organizationLocalService.addUserOrganization(
			memberUser.getUserId(), childOrganization);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			childOrganization.getGroupId());

		_activateSingleApproverWorkflow(
			childOrganization.getGroupId(), BlogsEntry.class.getName(), 0, 0);

		BlogsEntry blogsEntry = _addBlogsEntry(memberUser);

		_checkUserNotificationEventsByUsers(1, reviewerUser);

		_assignWorkflowTaskToUser(reviewerUser, reviewerUser);

		_completeWorkflowTask(reviewerUser, Constants.APPROVE);

		blogsEntry = _blogsEntryLocalService.getBlogsEntry(
			blogsEntry.getEntryId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, blogsEntry.getStatus());

		_deactivateWorkflow(
			childOrganization.getGroupId(), BlogsEntry.class.getName(), 0, 0);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());
	}

	@Test
	public void testApproveSiteMember() throws Exception {
		_activateWorkflow(
			JournalFolder.class.getName(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.DDM_STRUCTURE_ID_ALL,
			_SITE_MEMBER_SINGLE_APPROVER, 1);

		JournalArticle article = _addJournalArticle(
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, article.getStatus());

		_checkUserNotificationEventsByUsers(1, _siteMemberUser);

		Assert.assertTrue(_hasAssignableUsers(_adminUser));

		_assignWorkflowTaskToUser(_adminUser, _siteMemberUser);

		_completeWorkflowTask(_siteMemberUser, Constants.APPROVE);

		_getWorkflowInstance(JournalArticle.class.getName(), article.getId());

		article = _journalArticleLocalService.getArticle(article.getId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, article.getStatus());

		_deactivateWorkflow(
			JournalFolder.class.getName(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.DDM_STRUCTURE_ID_ALL);
	}

	@Test
	public void testApproveWorkflowBlogsEntryAsSiteAdmin() throws Exception {
		_activateSingleApproverWorkflow(BlogsEntry.class.getName(), 0, 0);

		BlogsEntry blogsEntry = _addBlogsEntry();

		_checkUserNotificationEventsByUsers(
			1, _adminUser, _portalContentReviewerUser, _siteAdminUser);

		_assignWorkflowTaskToUser(_siteAdminUser, _siteAdminUser);

		_completeWorkflowTask(_siteAdminUser, Constants.APPROVE);

		blogsEntry = _blogsEntryLocalService.getBlogsEntry(
			blogsEntry.getEntryId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, blogsEntry.getStatus());

		_deactivateWorkflow(BlogsEntry.class.getName(), 0, 0);
	}

	@Test
	public void testApproveWorkflowDDLRecordAsAdmin() throws Exception {
		DDLRecordSet recordSet = _addRecordSet();

		_activateSingleApproverWorkflow(
			DDLRecordSet.class.getName(), recordSet.getRecordSetId(), 0);

		String fieldName = RandomTestUtil.randomString();

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			DDMFormTestUtil.createDDMForm(fieldName));

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createLocalizedDDMFormFieldValue(
				fieldName, StringPool.BLANK));

		DDLRecord record = _ddlRecordLocalService.addRecord(
			_adminUser.getUserId(), _group.getGroupId(),
			recordSet.getRecordSetId(),
			DDLRecordConstants.DISPLAY_INDEX_DEFAULT, ddmFormValues,
			_serviceContext);

		_checkUserNotificationEventsByUsers(
			1, _adminUser, _portalContentReviewerUser, _siteAdminUser);

		_assignWorkflowTaskToUser(_adminUser, _adminUser);

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, record.getStatus());

		_completeWorkflowTask(_adminUser, Constants.APPROVE);

		DDLRecordVersion recordVersion = record.getRecordVersion();

		_getWorkflowInstance(
			DDLRecord.class.getName(), recordVersion.getRecordVersionId());

		record = _ddlRecordLocalService.getRecord(record.getRecordId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, record.getStatus());

		_deactivateWorkflow(
			DDLRecordSet.class.getName(), recordSet.getRecordSetId(), 0);
	}

	@Test
	public void testAssignApproveWorkflowBlogsEntryAsPortalContentReviewer()
		throws Exception {

		_activateSingleApproverWorkflow(BlogsEntry.class.getName(), 0, 0);

		BlogsEntry blogsEntry = _addBlogsEntry();

		_checkUserNotificationEventsByUsers(
			1, _adminUser, _portalContentReviewerUser, _siteAdminUser);

		_assignWorkflowTaskToUser(_portalContentReviewerUser, _adminUser);

		_checkUserNotificationEventsByUsers(1, _adminUser);

		_assignWorkflowTaskToUser(_adminUser, _portalContentReviewerUser);

		_checkUserNotificationEventsByUsers(1, _portalContentReviewerUser);

		_completeWorkflowTask(_portalContentReviewerUser, Constants.APPROVE);

		blogsEntry = _blogsEntryLocalService.getBlogsEntry(
			blogsEntry.getEntryId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, blogsEntry.getStatus());

		_deactivateWorkflow(BlogsEntry.class.getName(), 0, 0);
	}

	@Test
	public void testDeleteUserWithWorkflowTaskAssigned() throws Exception {
		_activateSingleApproverWorkflow(BlogsEntry.class.getName(), 0, 0);

		_addBlogsEntry();

		_checkUserNotificationEventsByUsers(1, _siteAdminUser);

		User user = _createUser(RoleConstants.SITE_ADMINISTRATOR);

		_assignWorkflowTaskToUser(_siteAdminUser, user);

		Assert.assertEquals(
			1,
			_workflowTaskManager.getWorkflowTaskCountByUser(
				user.getCompanyId(), user.getUserId(), false));

		_userLocalService.deleteUser(user);

		Assert.assertEquals(
			0,
			_workflowTaskManager.getWorkflowTaskCountByUser(
				user.getCompanyId(), user.getUserId(), false));

		_deactivateWorkflow(BlogsEntry.class.getName(), 0, 0);
	}

	@Test
	public void testGetNotifiableUsersRoleType() throws Exception {
		String emailAddress =
			StringUtil.toLowerCase(RandomTestUtil.randomString()) +
				RandomTestUtil.nextLong() + "@liferay.com";

		User user = UserTestUtil.addUser(
			_company.getCompanyId(), _adminUser.getUserId(), StringPool.BLANK,
			emailAddress,
			RandomTestUtil.randomString(
				NumericStringRandomizerBumper.INSTANCE,
				UniqueStringRandomizerBumper.INSTANCE),
			LocaleUtil.getDefault(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), null, _serviceContext);

		AccountEntry accountEntry = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, _adminUser.getUserId(), 0L,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			null, null, RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
			WorkflowConstants.STATUS_APPROVED, _serviceContext);

		CommerceAccountTestUtil.addAccountEntryUserRels(
			accountEntry.getAccountEntryId(), new long[] {user.getUserId()},
			ServiceContextTestUtil.getServiceContext());

		Role role = RoleLocalServiceUtil.getRole(
			_company.getCompanyId(),
			AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_ADMINISTRATOR);

		UserGroupRoleLocalServiceUtil.addUserGroupRoles(
			user.getUserId(), accountEntry.getAccountEntryGroupId(),
			new long[] {role.getRoleId()});

		CommerceCurrency commerceCurrency =
			CommerceCurrencyTestUtil.addCommerceCurrency(_group.getCompanyId());

		CommerceChannel commerceChannel =
			CommerceChannelLocalServiceUtil.addCommerceChannel(
				null, AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
				_group.getGroupId(), RandomTestUtil.randomString(),
				CommerceChannelConstants.CHANNEL_TYPE_SITE, null,
				commerceCurrency.getCode(), _serviceContext);

		workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
			_adminUser.getUserId(), commerceChannel.getCompanyId(),
			commerceChannel.getGroupId(), CommerceOrder.class.getName(), 0, 0,
			"Single Approver", 1);

		CommerceOrder commerceOrder = CommerceTestUtil.addB2BCommerceOrder(
			_group.getGroupId(), _adminUser.getUserId(),
			accountEntry.getAccountEntryId(),
			commerceCurrency.getCommerceCurrencyId());

		commerceOrder = _commerceOrderEngine.transitionCommerceOrder(
			commerceOrder, CommerceOrderConstants.ORDER_STATUS_IN_PROGRESS,
			_adminUser.getUserId(), true);

		WorkflowTask workflowTask = _getWorkflowTask(
			_adminUser, null, false, null, 0);

		List<User> notifiableUsers = ListUtil.filter(
			_workflowTaskManager.getNotifiableUsers(
				workflowTask.getWorkflowTaskId()),
			notifiableUser -> StringUtil.equals(
				emailAddress, notifiableUser.getEmailAddress()));

		Assert.assertEquals(
			notifiableUsers.toString(), 1, notifiableUsers.size());

		_commerceOrderLocalService.deleteCommerceOrder(
			commerceOrder.getCommerceOrderId());
	}

	@Test
	public void testGetNotifiableUsersScriptedAssignment() throws Exception {

		// User Scripted Assignment

		_activateWorkflow(
			0, BlogsEntry.class.getName(), 0, 0, _SCRIPTED_SINGLE_APPROVER_2,
			1);

		User user1 = UserTestUtil.addUser(
			_company.getCompanyId(), _companyAdminUser.getUserId(),
			StringPool.BLANK, "user1@liferay.com",
			RandomTestUtil.randomString(
				NumericStringRandomizerBumper.INSTANCE,
				UniqueStringRandomizerBumper.INSTANCE),
			LocaleUtil.getDefault(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), null,
			ServiceContextTestUtil.getServiceContext());

		_addBlogsEntry(user1);

		WorkflowTask workflowTask = _getWorkflowTask(
			user1, null, false, null, 0);

		Assert.assertEquals(
			Collections.singletonList(user1),
			_workflowTaskManager.getNotifiableUsers(
				workflowTask.getWorkflowTaskId()));

		_completeWorkflowTask(user1, Constants.APPROVE);

		_deactivateWorkflow(0, BlogsEntry.class.getName(), 0, 0);

		// Users Scripted Assignment

		User user2 = UserTestUtil.addUser(
			_company.getCompanyId(), _companyAdminUser.getUserId(),
			StringPool.BLANK, "user2@liferay.com",
			RandomTestUtil.randomString(
				NumericStringRandomizerBumper.INSTANCE,
				UniqueStringRandomizerBumper.INSTANCE),
			LocaleUtil.getDefault(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), null,
			ServiceContextTestUtil.getServiceContext());

		_activateWorkflow(
			0, BlogsEntry.class.getName(), 0, 0, _SCRIPTED_SINGLE_APPROVER_3,
			1);

		_addBlogsEntry(user2);

		workflowTask = _getWorkflowTask(user1, null, false, null, 0);

		Assert.assertEquals(
			Arrays.asList(user1, user2),
			_sort(
				_workflowTaskManager.getNotifiableUsers(
					workflowTask.getWorkflowTaskId())));

		_assignWorkflowTaskToUser(user1, user2);

		_completeWorkflowTask(user2, Constants.APPROVE);

		_deactivateWorkflow(0, BlogsEntry.class.getName(), 0, 0);

		_userLocalService.deleteUser(user1);
		_userLocalService.deleteUser(user2);
	}

	@Test
	public void testIsNotifiableUser() throws Exception {
		User user = UserTestUtil.addUser(_company);

		_activateSingleApproverWorkflow(BlogsEntry.class.getName(), 0, 0);

		_addBlogsEntry();

		WorkflowTask workflowTask = _getWorkflowTask();

		Assert.assertFalse(
			_workflowTaskManager.isNotifiableUser(
				user.getUserId(), workflowTask.getWorkflowTaskId()));

		Role role = _roleLocalService.getRole(
			_company.getCompanyId(), RoleConstants.ADMINISTRATOR);

		_userLocalService.addRoleUser(role.getRoleId(), user.getUserId());

		Assert.assertTrue(
			_workflowTaskManager.isNotifiableUser(
				user.getUserId(), workflowTask.getWorkflowTaskId()));

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(user));

			_workflowTaskManager.assignWorkflowTaskToUser(
				_company.getCompanyId(), user.getUserId(),
				workflowTask.getWorkflowTaskId(), user.getUserId(),
				StringPool.BLANK, null, null);

			_workflowTaskManager.completeWorkflowTask(
				_company.getCompanyId(), user.getUserId(),
				workflowTask.getWorkflowTaskId(), Constants.APPROVE,
				StringPool.BLANK, null);
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}

		Assert.assertTrue(
			_workflowTaskManager.isNotifiableUser(
				user.getUserId(), workflowTask.getWorkflowTaskId()));
	}

	@Test
	public void testMovetoTrashAndRestoreFromTrashPendingDLFileEntryInDLFolderWithWorkflow()
		throws Exception {

		Folder folder = _addFolder();

		folder = _updateFolder(
			folder, DLFolderConstants.RESTRICTION_TYPE_WORKFLOW,
			HashMapBuilder.put(
				String.valueOf(DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_ALL),
				"Single Approver@1"
			).build());

		FileVersion fileVersion = _addFileVersion(folder.getFolderId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, fileVersion.getStatus());

		_assignWorkflowTaskToUser(
			_adminUser, _adminUser, _REVIEW, DLFileEntry.class.getName(),
			fileVersion.getFileVersionId());

		_completeWorkflowTask(
			_adminUser, Constants.APPROVE, _REVIEW, DLFileEntry.class.getName(),
			fileVersion.getFileVersionId());

		fileVersion = _dlAppService.getFileVersion(
			fileVersion.getFileVersionId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, fileVersion.getStatus());

		fileVersion = _updateFileVersion(fileVersion.getFileEntryId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, fileVersion.getStatus());

		FileEntry fileEntry = _dlTrashService.moveFileEntryToTrash(
			fileVersion.getFileEntryId());

		Assert.assertNull(
			_fetchWorkflowInstanceLink(
				DLFileEntryConstants.getClassName(),
				fileVersion.getFileVersionId()));

		_dlTrashService.restoreFileEntryFromTrash(fileVersion.getFileEntryId());

		fileVersion = fileEntry.getLatestFileVersion();

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, fileVersion.getStatus());
	}

	@Test
	public void testPreventNotifyingAncestorSites() throws Exception {

		// Notifiy ancestor sites

		_activateWorkflow(
			0, BlogsEntry.class.getName(), 0, 0, _SITE_MEMBER_SINGLE_APPROVER,
			1);

		User childSiteMemberUser = _createUser(
			RoleConstants.SITE_MEMBER, _childGroup);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_childGroup.getGroupId());

		BlogsEntry blogsEntry = _addBlogsEntry();

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, blogsEntry.getStatus());

		_checkUserNotificationEventsByUsers(
			1, childSiteMemberUser, _siteMemberUser);

		// Prevent notifiying ancestor sites

		ConfigurationTestUtil.saveConfiguration(
			_configuration,
			HashMapDictionaryBuilder.<String, Object>put(
				"preventNotifyingAncestorSites", true
			).build());

		blogsEntry = _addBlogsEntry();

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, blogsEntry.getStatus());

		_checkUserNotificationEventsByUsers(1, childSiteMemberUser);
		_checkUserNotificationEventsByUsers(0, _siteMemberUser);
	}

	@Test
	public void testRejectDLFileEntry() throws Exception {
		_activateSingleApproverWorkflow(DLFolder.class.getName(), 0, -1);

		FileVersion fileVersion1 = _addFileVersion(
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, fileVersion1.getStatus());

		_assignWorkflowTaskToUser(
			_adminUser, _adminUser, _REVIEW, DLFileEntry.class.getName(),
			fileVersion1.getFileVersionId());

		_completeWorkflowTask(
			_adminUser, Constants.REJECT, _REVIEW, DLFileEntry.class.getName(),
			fileVersion1.getFileVersionId());

		fileVersion1 = _dlAppService.getFileVersion(
			fileVersion1.getFileVersionId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, fileVersion1.getStatus());

		_getWorkflowTask(
			_adminUser, Constants.UPDATE, false, DLFileEntry.class.getName(),
			fileVersion1.getFileVersionId());

		_deactivateWorkflow(DLFolder.class.getName(), 0, -1);
	}

	@Test
	public void testRejectWorkflowBlogsEntryAndViewAssignee() throws Exception {
		_activateSingleApproverWorkflow(BlogsEntry.class.getName(), 0, 0);

		BlogsEntry blogsEntry = _addBlogsEntry();

		_checkUserNotificationEventsByUsers(
			1, _adminUser, _portalContentReviewerUser, _siteAdminUser);

		_assignWorkflowTaskToUser(_adminUser, _portalContentReviewerUser);

		_checkUserNotificationEventsByUsers(1, _portalContentReviewerUser);

		_completeWorkflowTask(_portalContentReviewerUser, Constants.REJECT);

		_checkUserNotificationEventsByUsers(1, _adminUser);

		blogsEntry = _blogsEntryLocalService.getBlogsEntry(
			blogsEntry.getEntryId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, blogsEntry.getStatus());

		WorkflowTask workflowTask = _getWorkflowTask();

		Assert.assertEquals(
			_adminUser.getUserId(), workflowTask.getAssigneeUserId());

		_deactivateWorkflow(BlogsEntry.class.getName(), 0, 0);
	}

	@Test
	public void testScriptedAssignment() throws Exception {

		// Roles Scripted Assignment

		Organization organization = _createOrganization(true);

		User organizationReviewerUser = _createUser(
			_ORGANIZATION_CONTENT_REVIEWER, organization.getGroup());

		_organizationLocalService.addUserOrganization(
			organizationReviewerUser.getUserId(), organization);

		User siteAdministratorUser = _createUser(
			RoleConstants.SITE_ADMINISTRATOR);

		_organizationLocalService.addUserOrganization(
			siteAdministratorUser.getUserId(), organization);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			organization.getGroupId());

		_activateWorkflow(
			organization.getGroupId(), BlogsEntry.class.getName(), 0, 0,
			_SCRIPTED_SINGLE_APPROVER_1, 1);

		BlogsEntry blogsEntry = _addBlogsEntry(siteAdministratorUser);

		_assignWorkflowTaskToUser(
			organizationReviewerUser, organizationReviewerUser);

		_completeWorkflowTask(organizationReviewerUser, Constants.APPROVE);

		blogsEntry = _blogsEntryLocalService.getBlogsEntry(
			blogsEntry.getEntryId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, blogsEntry.getStatus());

		_deactivateWorkflow(
			organization.getGroupId(), BlogsEntry.class.getName(), 0, 0);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());

		// User Scripted Assignment

		_activateWorkflow(
			0, BlogsEntry.class.getName(), 0, 0, _SCRIPTED_SINGLE_APPROVER_2,
			1);

		User user1 = UserTestUtil.addUser(
			_company.getCompanyId(), _companyAdminUser.getUserId(),
			StringPool.BLANK, "user1@liferay.com",
			RandomTestUtil.randomString(
				NumericStringRandomizerBumper.INSTANCE,
				UniqueStringRandomizerBumper.INSTANCE),
			LocaleUtil.getDefault(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), null,
			ServiceContextTestUtil.getServiceContext());

		blogsEntry = _addBlogsEntry(user1);

		_completeWorkflowTask(user1, Constants.APPROVE);

		blogsEntry = _blogsEntryLocalService.getBlogsEntry(
			blogsEntry.getEntryId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, blogsEntry.getStatus());

		_deactivateWorkflow(0, BlogsEntry.class.getName(), 0, 0);

		// Users Scripted Assignment

		_activateWorkflow(
			0, BlogsEntry.class.getName(), 0, 0, _SCRIPTED_SINGLE_APPROVER_2,
			1);

		User user2 = UserTestUtil.addUser(
			_company.getCompanyId(), _companyAdminUser.getUserId(),
			StringPool.BLANK, "user2@liferay.com",
			RandomTestUtil.randomString(
				NumericStringRandomizerBumper.INSTANCE,
				UniqueStringRandomizerBumper.INSTANCE),
			LocaleUtil.getDefault(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), null,
			ServiceContextTestUtil.getServiceContext());

		_activateWorkflow(
			0, BlogsEntry.class.getName(), 0, 0, _SCRIPTED_SINGLE_APPROVER_3,
			1);

		blogsEntry = _addBlogsEntry(user2);

		_assignWorkflowTaskToUser(user1, user2);

		_completeWorkflowTask(user2, Constants.APPROVE);

		blogsEntry = _blogsEntryLocalService.getBlogsEntry(
			blogsEntry.getEntryId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, blogsEntry.getStatus());

		_deactivateWorkflow(0, BlogsEntry.class.getName(), 0, 0);

		_userLocalService.deleteUser(user1);
		_userLocalService.deleteUser(user2);
	}

	@Test
	public void testSearchWorkflowTaskByAssetTitle1() throws Exception {
		_activateSingleApproverWorkflow(BlogsEntry.class.getName(), 0, 0);

		BlogsEntry blogsEntry = _addBlogsEntry();

		int total = _searchCount(blogsEntry.getTitle());

		Assert.assertEquals(1, total);

		total = _searchCount(RandomTestUtil.randomString());

		Assert.assertEquals(0, total);

		_deactivateWorkflow(BlogsEntry.class.getName(), 0, 0);
	}

	@Test
	public void testSearchWorkflowTaskByAssetTitle2() throws Exception {
		_activateSingleApproverWorkflow(
			JournalFolder.class.getName(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.DDM_STRUCTURE_ID_ALL);

		JournalArticle article = _addJournalArticle(
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		int total = _searchCount(article.getTitle());

		Assert.assertEquals(1, total);

		total = _searchCount(RandomTestUtil.randomString());

		Assert.assertEquals(0, total);

		_deactivateWorkflow(
			JournalFolder.class.getName(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.DDM_STRUCTURE_ID_ALL);
	}

	@Test
	public void testSearchWorkflowTaskByDeletedAsset() throws Exception {
		_activateSingleApproverWorkflow(
			JournalFolder.class.getName(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.DDM_STRUCTURE_ID_ALL);

		JournalArticle article = _addJournalArticle(
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		int total = _searchCount(article.getTitle());

		Assert.assertEquals(1, total);

		_journalArticleLocalService.deleteArticle(article);

		total = _searchCount(article.getTitle());

		Assert.assertEquals(0, total);

		_deactivateWorkflow(
			JournalFolder.class.getName(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.DDM_STRUCTURE_ID_ALL);
	}

	@Test
	public void testSearchWorkflowTaskByUserRoles() throws Exception {
		_activateSingleApproverWorkflow(BlogsEntry.class.getName(), 0, 0);

		_addBlogsEntry();

		int total = _searchCountByUserRoles(_siteContentReviewerUser);

		Assert.assertEquals(1, total);

		_deactivateWorkflow(BlogsEntry.class.getName(), 0, 0);
	}

	@Test
	public void testSearchWorkflowTaskByUserRolesWhenGroupIsInactive()
		throws Exception {

		_activateSingleApproverWorkflow(BlogsEntry.class.getName(), 0, 0);

		_addBlogsEntry();

		_group.setActive(false);

		_group = _groupLocalService.updateGroup(_group);

		int total = _searchCountByUserRoles(_siteContentReviewerUser);

		Assert.assertEquals(1, total);

		_deactivateWorkflow(BlogsEntry.class.getName(), 0, 0);
	}

	@Test
	public void testSearchWorkflowTasksByAssetTypesAndAssetPrimaryKeys()
		throws Exception {

		_activateSingleApproverWorkflow(BlogsEntry.class.getName(), 0, 0);
		_activateSingleApproverWorkflow(DLFolder.class.getName(), 0, -1);

		_addBlogsEntry();
		_addFileVersion(DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		BlogsEntry blogsEntry = _addBlogsEntry();

		List<WorkflowTask> workflowTasks =
			_searchByAssetTypesAndAssetPrimaryKeys(null, null);

		Assert.assertEquals(workflowTasks.toString(), 3, workflowTasks.size());

		workflowTasks = _searchByAssetTypesAndAssetPrimaryKeys(
			new String[] {BlogsEntry.class.getName()}, null);

		Assert.assertEquals(workflowTasks.toString(), 2, workflowTasks.size());

		workflowTasks = _searchByAssetTypesAndAssetPrimaryKeys(
			new String[] {BlogsEntry.class.getName()},
			new Long[] {blogsEntry.getEntryId()});

		Assert.assertEquals(workflowTasks.toString(), 1, workflowTasks.size());

		WorkflowTask workflowTask = workflowTasks.get(0);

		Assert.assertEquals(
			blogsEntry.getEntryId(),
			MapUtil.getLong(
				workflowTask.getOptionalAttributes(),
				WorkflowConstants.CONTEXT_ENTRY_CLASS_PK));

		_deactivateWorkflow(BlogsEntry.class.getName(), 0, 0);
		_deactivateWorkflow(DLFolder.class.getName(), 0, -1);
	}

	@Test
	public void testSearchWorkflowTasksOrderByModifiedDate() throws Exception {
		_activateSingleApproverWorkflow(BlogsEntry.class.getName(), 0, 0);

		BlogsEntry blogsEntry1 = _addBlogsEntry();

		_assignWorkflowTaskToUser(
			_adminUser, _adminUser, _REVIEW, BlogsEntry.class.getName(),
			blogsEntry1.getEntryId());

		BlogsEntry blogsEntry2 = _addBlogsEntry();

		_assignWorkflowTaskToUser(
			_adminUser, _adminUser, _REVIEW, BlogsEntry.class.getName(),
			blogsEntry2.getEntryId());

		List<WorkflowTask> workflowTasks = new ArrayList<>();

		workflowTasks.add(
			_completeWorkflowTask(
				_adminUser, Constants.REJECT, _REVIEW,
				BlogsEntry.class.getName(), blogsEntry1.getEntryId()));

		workflowTasks.add(
			_completeWorkflowTask(
				_adminUser, Constants.REJECT, _REVIEW,
				BlogsEntry.class.getName(), blogsEntry2.getEntryId()));

		workflowTasks.add(
			_completeWorkflowTask(
				_adminUser, "resubmit", "update", BlogsEntry.class.getName(),
				blogsEntry2.getEntryId()));

		workflowTasks.add(
			_completeWorkflowTask(
				_adminUser, "resubmit", "update", BlogsEntry.class.getName(),
				blogsEntry1.getEntryId()));

		WorkflowModelSearchResult<WorkflowTask> workflowModelSearchResult =
			_workflowTaskManager.searchWorkflowTasks(
				_adminUser.getCompanyId(), _adminUser.getUserId(), null, null,
				null, null, User.class.getName(),
				new Long[] {_adminUser.getUserId()}, null, null, true, false,
				false, null, null, false, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				_workflowComparatorFactory.getTaskModifiedDateComparator(true));

		_assertEquals(
			workflowTasks, workflowModelSearchResult.getWorkflowModels());

		workflowModelSearchResult = _workflowTaskManager.searchWorkflowTasks(
			_adminUser.getCompanyId(), _adminUser.getUserId(), null, null, null,
			null, User.class.getName(), new Long[] {_adminUser.getUserId()},
			null, null, true, false, false, null, null, false,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			_workflowComparatorFactory.getTaskModifiedDateComparator(false));

		Collections.reverse(workflowTasks);

		_assertEquals(
			workflowTasks, workflowModelSearchResult.getWorkflowModels());

		_deactivateWorkflow(BlogsEntry.class.getName(), 0, 0);
	}

	@Test
	public void testSearchWorkflowTasksWhenThereIsAnUnregisteredHandler()
		throws Exception {

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));

		try (ServiceRegistrationHolder serviceRegistrationHolder =
				registryWorkflowHandler()) {

			Class<?> clazz = getClass();

			WorkflowHandlerRegistryUtil.startWorkflowInstance(
				TestPropsValues.getCompanyId(), 0, TestPropsValues.getUserId(),
				clazz.getName(), 1, null, new ServiceContext());

			WorkflowModelSearchResult<WorkflowTask> workflowModelSearchResult =
				_workflowTaskManager.searchWorkflowTasks(
					TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
					StringPool.BLANK, new String[] {StringPool.BLANK}, null,
					null, null, null, null, null, null, true, true, null, null,
					false, 0, 1,
					_workflowComparatorFactory.getTaskModifiedDateComparator(
						false));

			List<WorkflowTask> workflowTasks =
				workflowModelSearchResult.getWorkflowModels();

			Assert.assertEquals(
				workflowTasks.toString(), 1, workflowTasks.size());
		}

		WorkflowModelSearchResult<WorkflowTask> workflowModelSearchResult =
			_workflowTaskManager.searchWorkflowTasks(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				StringPool.BLANK, new String[] {StringPool.BLANK}, null, null,
				null, null, null, null, null, true, true, null, null, false, 0,
				1,
				_workflowComparatorFactory.getTaskModifiedDateComparator(
					false));

		List<WorkflowTask> workflowTasks =
			workflowModelSearchResult.getWorkflowModels();

		Assert.assertEquals(workflowTasks.toString(), 0, workflowTasks.size());
	}

	@Test
	public void testUpdateDueDate() throws Exception {
		_activateSingleApproverWorkflow(BlogsEntry.class.getName(), 0, 0);

		BlogsEntry blogsEntry = _addBlogsEntry();

		WorkflowTask workflowTask = _getWorkflowTask();

		Date date = new Date(System.currentTimeMillis() + Time.DAY);

		workflowTask = _workflowTaskManager.updateDueDate(
			_siteAdminUser.getCompanyId(), _siteAdminUser.getUserId(),
			workflowTask.getWorkflowTaskId(), StringPool.BLANK, date);

		Assert.assertEquals(date, workflowTask.getDueDate());

		_blogsEntryLocalService.deleteEntry(blogsEntry);

		_deactivateWorkflow(BlogsEntry.class.getName(), 0, 0);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	private void _activateSingleApproverWorkflow(
			long groupId, String className, long classPK, long typePK)
		throws Exception {

		_activateWorkflow(
			groupId, className, classPK, typePK, "Single Approver", 1);
	}

	private void _activateSingleApproverWorkflow(
			String className, long classPK, long typePK)
		throws Exception {

		_activateWorkflow(
			_group.getGroupId(), className, classPK, typePK, "Single Approver",
			1);
	}

	private void _activateWorkflow(
			long groupId, String className, long classPK, long typePK,
			String workflowDefinitionName, int workflowDefinitionVersion)
		throws Exception {

		workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
			_adminUser.getUserId(), _company.getCompanyId(), groupId, className,
			classPK, typePK, workflowDefinitionName, workflowDefinitionVersion);
	}

	private void _activateWorkflow(
			String className, long classPK, long typePK,
			String workflowDefinitionName, int workflowDefinitionVersion)
		throws Exception {

		_activateWorkflow(
			_group.getGroupId(), className, classPK, typePK,
			workflowDefinitionName, workflowDefinitionVersion);
	}

	private BlogsEntry _addBlogsEntry() throws Exception {
		return _addBlogsEntry(_adminUser);
	}

	private BlogsEntry _addBlogsEntry(User user) throws Exception {
		return _blogsEntryLocalService.addEntry(
			user.getUserId(), StringUtil.randomString(),
			StringUtil.randomString(),
			new Date(System.currentTimeMillis() - Time.SECOND),
			_serviceContext);
	}

	private DLFileEntryType _addFileEntryType() throws Exception {
		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			DLFileEntryMetadata.class.getName());

		LocalizedValuesMap localizedValuesMap = new LocalizedValuesMap(
			"defaultValue");

		localizedValuesMap.put(LocaleUtil.US, RandomTestUtil.randomString());

		Map<Locale, String> map = LocalizationUtil.getMap(localizedValuesMap);

		return _dlFileEntryTypeLocalService.addFileEntryType(
			null, _adminUser.getUserId(), _group.getGroupId(),
			ddmStructure.getStructureId(), null, map, map,
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_SCOPE_DEFAULT,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	private FileVersion _addFileVersion(long folderId) throws Exception {
		return _addFileVersion(folderId, 0);
	}

	private FileVersion _addFileVersion(long folderId, long fileEntryTypeId)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAttribute("fileEntryTypeId", fileEntryTypeId);

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, _adminUser.getUserId(), _group.getGroupId(), folderId,
			RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, TestDataConstants.TEST_BYTE_ARRAY, null, null,
			null, serviceContext);

		return fileEntry.getLatestFileVersion();
	}

	private Folder _addFolder() throws Exception {
		return _dlAppService.addFolder(
			null, _group.getGroupId(), 0, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), _serviceContext);
	}

	private JournalArticle _addJournalArticle(long folderId) throws Exception {
		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		return _addJournalArticle(folderId, ddmStructure);
	}

	private JournalArticle _addJournalArticle(
			long folderId, DDMStructure ddmStructure)
		throws Exception {

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), ddmStructure.getStructureId(),
			_portal.getClassNameId(JournalArticle.class));

		Map<Locale, String> titleMap = HashMapBuilder.put(
			LocaleUtil.getDefault(), RandomTestUtil.randomString()
		).build();

		Map<Locale, String> descriptionMap = HashMapBuilder.put(
			LocaleUtil.getDefault(), RandomTestUtil.randomString()
		).build();

		String content = DDMStructureTestUtil.getSampleStructuredContent();

		return _journalArticleLocalService.addArticle(
			null, _adminUser.getUserId(), _group.getGroupId(), folderId,
			titleMap, descriptionMap, content, ddmStructure.getStructureId(),
			ddmTemplate.getTemplateKey(), _serviceContext);
	}

	private JournalFolder _addJournalFolder() throws Exception {
		return _journalFolderLocalService.addFolder(
			null, _adminUser.getUserId(), _group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			_serviceContext);
	}

	private JournalFolder _addJournalFolder(
			long ddmStructureId, int restrictionType)
		throws Exception {

		long[] ddmStructureIds = {ddmStructureId};

		JournalFolder folder = _addJournalFolder();

		return _journalFolderLocalService.updateFolder(
			_adminUser.getUserId(), _group.getGroupId(), folder.getFolderId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			ddmStructureIds, restrictionType, false, _serviceContext);
	}

	private DDLRecordSet _addRecordSet() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			RandomTestUtil.randomString());

		DDMStructureTestHelper ddmStructureTestHelper =
			new DDMStructureTestHelper(
				_portal.getClassNameId(DDLRecordSet.class), _group);

		DDMStructure ddmStructure = ddmStructureTestHelper.addStructure(
			ddmForm, StorageType.DEFAULT.toString());

		return _ddlRecordSetLocalService.addRecordSet(
			_adminUser.getUserId(), _group.getGroupId(),
			ddmStructure.getStructureId(), null,
			HashMapBuilder.put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			null, DDLRecordSetConstants.MIN_DISPLAY_ROWS_DEFAULT,
			DDLRecordSetConstants.SCOPE_DYNAMIC_DATA_LISTS, _serviceContext);
	}

	private void _assertEquals(
		List<WorkflowTask> workflowTasks1, List<WorkflowTask> workflowTasks2) {

		Assert.assertEquals(
			workflowTasks1.toString() + " does not equal " +
				workflowTasks2.toString(),
			workflowTasks1.size(), workflowTasks2.size());

		for (int i = 0; i < workflowTasks1.size(); i++) {
			WorkflowTask workflowTask1 = workflowTasks1.get(i);
			WorkflowTask workflowTask2 = workflowTasks2.get(i);

			Assert.assertEquals(
				workflowTask1.getWorkflowTaskId() + " does not equal " +
					workflowTask2.getWorkflowTaskId(),
				workflowTask1.getWorkflowTaskId(),
				workflowTask2.getWorkflowTaskId());
		}
	}

	private void _assignWorkflowTaskToUser(User user, User assigneeUser)
		throws Exception {

		_assignWorkflowTaskToUser(user, assigneeUser, null, null, 0);
	}

	private void _assignWorkflowTaskToUser(
			User user, User assigneeUser, String taskName)
		throws Exception {

		_assignWorkflowTaskToUser(user, assigneeUser, taskName, null, 0);
	}

	private void _assignWorkflowTaskToUser(
			User user, User assigneeUser, String taskName, String className,
			long classPK)
		throws Exception {

		WorkflowTask workflowTask = _getWorkflowTask(
			user, taskName, false, className, classPK);

		PermissionChecker userPermissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		PermissionThreadLocal.setPermissionChecker(userPermissionChecker);

		_workflowTaskManager.assignWorkflowTaskToUser(
			_group.getCompanyId(), user.getUserId(),
			workflowTask.getWorkflowTaskId(), assigneeUser.getUserId(),
			StringPool.BLANK, null, null);
	}

	private void _checkUserNotificationEventsByUsers(
		long expected, User... users) {

		for (User user : users) {
			List<UserNotificationEvent> userNotificationEvents =
				_userNotificationEventLocalService.
					getArchivedUserNotificationEvents(
						user.getUserId(),
						UserNotificationDeliveryConstants.TYPE_WEBSITE, false);

			Assert.assertEquals(
				userNotificationEvents.toString(), expected,
				userNotificationEvents.size());

			if (expected > 0) {
				UserNotificationEvent userNotificationEvent =
					userNotificationEvents.get(0);

				userNotificationEvent.setArchived(true);

				_userNotificationEventLocalService.updateUserNotificationEvent(
					userNotificationEvent);
			}
		}
	}

	private void _completeWorkflowTask(User user, String transition)
		throws Exception {

		_completeWorkflowTask(user, transition, null, null, 0);
	}

	private void _completeWorkflowTask(
			User user, String transition, String taskName)
		throws Exception {

		_completeWorkflowTask(user, transition, taskName, null, 0);
	}

	private WorkflowTask _completeWorkflowTask(
			User user, String transition, String taskName, String className,
			long classPK)
		throws Exception {

		WorkflowTask workflowTask = _getWorkflowTask(
			user, taskName, false, className, classPK);

		PermissionChecker userPermissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		PermissionThreadLocal.setPermissionChecker(userPermissionChecker);

		return _workflowTaskManager.completeWorkflowTask(
			_group.getCompanyId(), user.getUserId(),
			workflowTask.getWorkflowTaskId(), transition, StringPool.BLANK,
			null);
	}

	private void _createJoinXorWorkflow() throws Exception {
		try {
			_workflowDefinitionManager.getWorkflowDefinition(
				_adminUser.getCompanyId(), _JOIN_XOR, 1);
		}
		catch (NoSuchModelException noSuchModelException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchModelException);
			}

			String content = readFileToJSON("join-xor-workflow-definition.xml");

			_workflowDefinitionManager.deployWorkflowDefinition(
				content.getBytes(), _adminUser.getCompanyId(), null, _JOIN_XOR,
				_JOIN_XOR, _adminUser.getUserId());
		}
	}

	private Organization _createOrganization(boolean site) throws Exception {
		return _createOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID, site);
	}

	private Organization _createOrganization(
			long parentOrganizationId, boolean site)
		throws Exception {

		return _organizationLocalService.addOrganization(
			_adminUser.getUserId(), parentOrganizationId,
			StringUtil.randomString(), site);
	}

	private void _createScriptedAssignmentWorkflow(String fileName, String name)
		throws Exception {

		try {
			_workflowDefinitionManager.getWorkflowDefinition(
				_adminUser.getCompanyId(), name, 1);
		}
		catch (NoSuchModelException noSuchModelException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchModelException);
			}

			String content = readFileToJSON(fileName);

			_workflowDefinitionManager.deployWorkflowDefinition(
				content.getBytes(), _adminUser.getCompanyId(), null, name, name,
				_adminUser.getUserId());
		}
	}

	private void _createSiteMemberWorkflow() throws Exception {
		try {
			_workflowDefinitionManager.getWorkflowDefinition(
				_adminUser.getCompanyId(), _SITE_MEMBER_SINGLE_APPROVER, 1);
		}
		catch (NoSuchModelException noSuchModelException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchModelException);
			}

			String content = readFileToJSON(
				"single-approver-site-member-workflow-definition.xml");

			_workflowDefinitionManager.deployWorkflowDefinition(
				content.getBytes(), _adminUser.getCompanyId(), null,
				_SITE_MEMBER_SINGLE_APPROVER, _SITE_MEMBER_SINGLE_APPROVER,
				_adminUser.getUserId());
		}
	}

	private User _createUser(String roleName) throws Exception {
		return _createUser(roleName, _group, true);
	}

	private User _createUser(String roleName, Group group) throws Exception {
		return _createUser(roleName, group, true);
	}

	private User _createUser(
			String roleName, Group group, boolean addUserToRole)
		throws Exception {

		User user = UserTestUtil.addUser(
			_company.getCompanyId(), _companyAdminUser.getUserId(),
			RandomTestUtil.randomString(
				NumericStringRandomizerBumper.INSTANCE,
				UniqueStringRandomizerBumper.INSTANCE),
			LocaleUtil.getDefault(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), new long[] {group.getGroupId()},
			ServiceContextTestUtil.getServiceContext());

		Role role = _roleLocalService.getRole(
			_company.getCompanyId(), roleName);

		if (addUserToRole) {
			_userLocalService.addRoleUser(role.getRoleId(), user);
		}

		_userGroupRoleLocalService.addUserGroupRoles(
			new long[] {user.getUserId()}, group.getGroupId(),
			role.getRoleId());

		return user;
	}

	private void _deactivateWorkflow(
			long groupId, String className, long classPK, long typePK)
		throws Exception {

		workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
			_adminUser.getUserId(), _company.getCompanyId(), groupId, className,
			classPK, typePK, null);
	}

	private void _deactivateWorkflow(
			String className, long classPK, long typePK)
		throws Exception {

		_deactivateWorkflow(_group.getGroupId(), className, classPK, typePK);
	}

	private WorkflowInstanceLink _fetchWorkflowInstanceLink(
			String className, long classPK)
		throws Exception {

		return workflowInstanceLinkLocalService.fetchWorkflowInstanceLink(
			_adminUser.getCompanyId(), _adminUser.getGroupId(), className,
			classPK);
	}

	private DLFileEntryType _getBasicFileEntryType() throws Exception {
		return _dlFileEntryTypeLocalService.getFileEntryType(
			0, "BASIC-DOCUMENT");
	}

	private WorkflowInstance _getWorkflowInstance(
			String className, long classPK)
		throws Exception {

		return _getWorkflowInstance(className, classPK, true);
	}

	private WorkflowInstance _getWorkflowInstance(
			String className, long classPK, boolean completed)
		throws Exception {

		List<WorkflowInstance> workflowInstances =
			workflowInstanceManager.getWorkflowInstances(
				_adminUser.getCompanyId(), _adminUser.getUserId(), className,
				classPK, completed, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			workflowInstances.toString(), 1, workflowInstances.size());

		return workflowInstances.get(0);
	}

	private WorkflowTask _getWorkflowTask() throws Exception {
		return _getWorkflowTask(_adminUser, null, false, null, 0);
	}

	private WorkflowTask _getWorkflowTask(
			User user, String taskName, boolean completed, String className,
			long classPK)
		throws Exception {

		List<WorkflowTask> workflowTasks = _getWorkflowTasks(user, completed);

		WorkflowInstance workflowInstance = null;

		if (Validator.isNotNull(className) && (classPK > 0)) {
			workflowInstance = _getWorkflowInstance(
				className, classPK, completed);

			if (workflowTasks.isEmpty()) {
				workflowTasks.addAll(
					_workflowTaskManager.getWorkflowTasksByWorkflowInstance(
						user.getCompanyId(), user.getUserId(),
						workflowInstance.getWorkflowInstanceId(), completed,
						QueryUtil.ALL_POS, QueryUtil.ALL_POS, null));
			}
		}

		for (WorkflowTask workflowTask : workflowTasks) {
			if (Objects.equals(taskName, workflowTask.getName())) {
				if ((workflowInstance != null) &&
					(workflowInstance.getWorkflowInstanceId() !=
						workflowTask.getWorkflowInstanceId())) {

					continue;
				}

				return workflowTask;
			}
		}

		Assert.assertNull(taskName);

		Assert.assertNull(className);

		Assert.assertEquals(workflowTasks.toString(), 1, workflowTasks.size());

		return workflowTasks.get(0);
	}

	private List<WorkflowTask> _getWorkflowTasks(User user, boolean completed)
		throws Exception {

		List<WorkflowTask> workflowTasks = new ArrayList<>();

		workflowTasks.addAll(
			_workflowTaskManager.getWorkflowTasksByUserRoles(
				user.getCompanyId(), user.getUserId(), completed,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null));

		workflowTasks.addAll(
			_workflowTaskManager.getWorkflowTasksByUser(
				user.getCompanyId(), user.getUserId(), completed,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null));

		Assert.assertFalse(workflowTasks.isEmpty());

		return workflowTasks;
	}

	private boolean _hasAssignableUsers(User user) throws Exception {
		WorkflowTask workflowTask = _getWorkflowTask(
			user, null, false, null, 0);

		return _workflowTaskManager.hasAssignableUsers(
			workflowTask.getWorkflowTaskId());
	}

	private List<WorkflowTask> _searchByAssetTypesAndAssetPrimaryKeys(
			String[] assetTypes, Long[] assetPrimaryKeys)
		throws Exception {

		return _workflowTaskManager.search(
			_adminUser.getCompanyId(), _adminUser.getUserId(), null, null,
			assetTypes, assetPrimaryKeys, null, null, null, null, false, true,
			null, null, false, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			_workflowComparatorFactory.getTaskModifiedDateComparator(true));
	}

	private int _searchCount(String keywords) throws Exception {
		return _workflowTaskManager.searchCount(
			_adminUser.getCompanyId(), _adminUser.getUserId(), keywords,
			new String[] {keywords}, null, null, null, null, null, null, false,
			true, null, null, false);
	}

	private int _searchCountByUserRoles(User user) throws Exception {
		return _workflowTaskManager.searchCount(
			user.getCompanyId(), user.getUserId(), null, null, null, null, null,
			null, null, null, false, true, null, null, false);
	}

	private void _setUpPermissionThreadLocal() throws Exception {
		_permissionChecker = PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			new SimplePermissionChecker() {
				{
					init(_companyAdminUser);
				}

				@Override
				public boolean hasOwnerPermission(
					long companyId, String name, String primKey, long ownerId,
					String actionId) {

					return true;
				}

			});
	}

	private void _setUpPrincipalThreadLocal() throws Exception {
		_name = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(_companyAdminUser.getUserId());
	}

	private void _setUpUsers() throws Exception {
		_adminUser = _createUser(RoleConstants.ADMINISTRATOR);

		_portalContentReviewerUser = _createUser(
			RoleConstants.PORTAL_CONTENT_REVIEWER);

		_siteAdminUser = _createUser(RoleConstants.SITE_ADMINISTRATOR);

		_siteContentReviewerUser = _createUser(
			RoleConstants.SITE_CONTENT_REVIEWER);

		_siteMemberUser = _createUser(RoleConstants.SITE_MEMBER);
	}

	private void _setUpWorkflow() throws Exception {
		_createJoinXorWorkflow();
		_createScriptedAssignmentWorkflow(
			"single-approver-scripted-assignment-1-workflow-definition.xml",
			_SCRIPTED_SINGLE_APPROVER_1);
		_createScriptedAssignmentWorkflow(
			"single-approver-scripted-assignment-2-workflow-definition.xml",
			_SCRIPTED_SINGLE_APPROVER_2);
		_createScriptedAssignmentWorkflow(
			"single-approver-scripted-assignment-3-workflow-definition.xml",
			_SCRIPTED_SINGLE_APPROVER_3);
		_createSiteMemberWorkflow();
	}

	private List<User> _sort(List<User> users) {
		Collections.sort(
			users,
			new Comparator<User>() {

				@Override
				public int compare(User user1, User user2) {
					String emailAddress1 = user1.getEmailAddress();
					String emailAddress2 = user2.getEmailAddress();

					return emailAddress1.compareTo(emailAddress2);
				}

			});

		return users;
	}

	private FileVersion _updateFileVersion(long fileEntryId) throws Exception {
		FileEntry fileEntry = _dlAppService.updateFileEntry(
			fileEntryId, StringPool.BLANK, ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			StringPool.BLANK, null, DLVersionNumberIncrease.AUTOMATIC, null, 0,
			null, null, null, _serviceContext);

		return fileEntry.getLatestFileVersion();
	}

	private Folder _updateFolder(Folder folder, int restrictionType)
		throws Exception {

		return _updateFolder(folder, restrictionType, -1, new HashMap<>());
	}

	private Folder _updateFolder(
			Folder folder, int restrictionType, long defaultFileEntryTypeId,
			Map<String, String> dlFileEntryTypeMap)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAttribute("restrictionType", restrictionType);

		if (defaultFileEntryTypeId > -1) {
			serviceContext.setAttribute(
				"defaultFileEntryTypeId", defaultFileEntryTypeId);
		}

		serviceContext.setAttribute(
			"dlFileEntryTypesSearchContainerPrimaryKeys",
			StringUtil.merge(dlFileEntryTypeMap.keySet()));

		dlFileEntryTypeMap.forEach(
			(dlFileEntryType, workflowDefinition) ->
				serviceContext.setAttribute(
					"workflowDefinition" + dlFileEntryType,
					workflowDefinition));

		return _dlAppService.updateFolder(
			folder.getFolderId(), folder.getName(), folder.getDescription(),
			serviceContext);
	}

	private Folder _updateFolder(
			Folder folder, int restrictionType,
			Map<String, String> dlFileEntryTypeMap)
		throws Exception {

		return _updateFolder(folder, restrictionType, -1, dlFileEntryTypeMap);
	}

	private static final String _JOIN_XOR = "Join Xor";

	private static final String _ORGANIZATION_CONTENT_REVIEWER =
		"Organization Content Reviewer";

	private static final String _REVIEW = "review";

	private static final String _SCRIPTED_SINGLE_APPROVER_1 =
		"Scripted Single Approver 1";

	private static final String _SCRIPTED_SINGLE_APPROVER_2 =
		"Scripted Single Approver 2";

	private static final String _SCRIPTED_SINGLE_APPROVER_3 =
		"Scripted Single Approver 3";

	private static final String _SITE_MEMBER_SINGLE_APPROVER =
		"Site Member Single Approver";

	private static final Log _log = LogFactoryUtil.getLog(
		WorkflowTaskManagerImplTest.class);

	private static Company _company;
	private static User _companyAdminUser;

	@Inject
	private static CompanyLocalService _companyLocalService;

	private static Configuration _configuration;

	@Inject
	private static ConfigurationAdmin _configurationAdmin;

	private static String _originalName;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@DeleteAfterTestRun
	private User _adminUser;

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	@DeleteAfterTestRun
	private Group _childGroup;

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Inject
	private CommerceOrderEngine _commerceOrderEngine;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Inject
	private DDLRecordLocalService _ddlRecordLocalService;

	@Inject
	private DDLRecordSetLocalService _ddlRecordSetLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLAppService _dlAppService;

	@Inject
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Inject
	private DLTrashService _dlTrashService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private JournalFolderLocalService _journalFolderLocalService;

	private String _name;

	@Inject
	private OrganizationLocalService _organizationLocalService;

	private PermissionChecker _permissionChecker;

	@Inject
	private Portal _portal;

	@DeleteAfterTestRun
	private User _portalContentReviewerUser;

	@Inject
	private RoleLocalService _roleLocalService;

	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private User _siteAdminUser;

	@DeleteAfterTestRun
	private User _siteContentReviewerUser;

	@DeleteAfterTestRun
	private User _siteMemberUser;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

	@Inject
	private WorkflowComparatorFactory _workflowComparatorFactory;

	@Inject
	private WorkflowDefinitionManager _workflowDefinitionManager;

	@Inject
	private WorkflowTaskManager _workflowTaskManager;

}