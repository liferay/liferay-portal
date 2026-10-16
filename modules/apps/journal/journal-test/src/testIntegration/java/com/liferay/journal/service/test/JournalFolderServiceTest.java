/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.exception.DuplicateFolderExternalReferenceCodeException;
import com.liferay.journal.exception.InvalidDDMStructureException;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.journal.service.JournalFolderLocalService;
import com.liferay.journal.service.JournalFolderService;
import com.liferay.journal.test.util.JournalFolderFixture;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashHandlerRegistryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.trash.exception.RestoreEntryException;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Juan Fernández
 */
@RunWith(Arquillian.class)
public class JournalFolderServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_journalFolderFixture = new JournalFolderFixture(
			_journalFolderLocalService);
	}

	@Test
	public void testAddArticle() throws Exception {
		JournalFolder folder = _journalFolderFixture.addFolder(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "Test Folder");

		JournalArticle article = JournalTestUtil.addArticle(
			_group.getGroupId(), folder.getFolderId(), "Test Article",
			"This is a test article.");

		Assert.assertEquals(article.getFolderId(), folder.getFolderId());
	}

	@Test
	public void testAddArticleToRestrictedFolder() throws Exception {
		JournalFolder folder = _journalFolderFixture.addFolder(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "Test 1");

		DDMStructure ddmStructure1 = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		long[] ddmStructureIds = {ddmStructure1.getStructureId()};

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		_journalFolderLocalService.updateFolder(
			TestPropsValues.getUserId(), serviceContext.getScopeGroupId(),
			folder.getFolderId(), folder.getParentFolderId(), folder.getName(),
			folder.getDescription(), ddmStructureIds,
			JournalFolderConstants.RESTRICTION_TYPE_DDM_STRUCTURES_AND_WORKFLOW,
			false, serviceContext);

		List<DDMStructure> ddmStructures =
			_journalFolderLocalService.getDDMStructures(
				PortalUtil.getCurrentAndAncestorSiteGroupIds(
					_group.getGroupId()),
				folder.getFolderId(),
				JournalFolderConstants.
					RESTRICTION_TYPE_DDM_STRUCTURES_AND_WORKFLOW);

		Assert.assertFalse(ddmStructures.toString(), ddmStructures.isEmpty());

		String xml = DDMStructureTestUtil.getSampleStructuredContent(
			"Test Article");

		DDMStructure ddmStructure2 = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		DDMTemplate ddmTemplate2 = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), ddmStructure2.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class),
			LocaleUtil.getDefault());

		try {
			JournalTestUtil.addArticleWithXMLContent(
				_group.getGroupId(), folder.getFolderId(),
				JournalArticleConstants.CLASS_NAME_ID_DEFAULT, xml,
				ddmStructure2.getStructureKey(), ddmTemplate2.getTemplateKey());

			Assert.fail();
		}
		catch (InvalidDDMStructureException invalidDDMStructureException) {
		}

		JournalFolder subfolder = _journalFolderFixture.addFolder(
			_group.getGroupId(), folder.getFolderId(), "Test 1.1");

		try {
			JournalTestUtil.addArticleWithXMLContent(
				_group.getGroupId(), subfolder.getFolderId(),
				JournalArticleConstants.CLASS_NAME_ID_DEFAULT, xml,
				ddmStructure2.getStructureKey(), ddmTemplate2.getTemplateKey());

			Assert.fail();
		}
		catch (InvalidDDMStructureException invalidDDMStructureException) {
		}

		_journalFolderLocalService.deleteFolder(folder.getFolderId());

		ddmStructures = _journalFolderLocalService.getDDMStructures(
			PortalUtil.getCurrentAndAncestorSiteGroupIds(_group.getGroupId()),
			folder.getFolderId(),
			JournalFolderConstants.
				RESTRICTION_TYPE_DDM_STRUCTURES_AND_WORKFLOW);

		Assert.assertTrue(ddmStructures.toString(), ddmStructures.isEmpty());
	}

	@Test
	public void testAddJournalFolderWithExternalReferenceCode()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		JournalFolder folder = addJournalFolder(externalReferenceCode);

		Assert.assertEquals(
			externalReferenceCode, folder.getExternalReferenceCode());
	}

	@Test
	public void testAddJournalFolderWithoutExternalReferenceCode()
		throws Exception {

		JournalFolder folder1 = addJournalFolder(null);

		String externalReferenceCode = folder1.getExternalReferenceCode();

		Assert.assertEquals(externalReferenceCode, folder1.getUuid());

		JournalFolder folder2 =
			_journalFolderLocalService.getJournalFolderByExternalReferenceCode(
				externalReferenceCode, _group.getGroupId());

		Assert.assertEquals(folder1, folder2);
	}

	@Test
	public void testAddRestrictionToParentWithRestrictedChildFolder()
		throws Exception {

		JournalFolder parentFolder = _journalFolderFixture.addFolder(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "Test 1");

		JournalFolder childFolder = _journalFolderFixture.addFolder(
			_group.getGroupId(), parentFolder.getFolderId(), "Test 2");

		String xml = DDMStructureTestUtil.getSampleStructuredContent(
			"Test Article");

		DDMStructure childDDMStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), childDDMStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class),
			LocaleUtil.getDefault());

		JournalTestUtil.addArticleWithXMLContent(
			_group.getGroupId(), childFolder.getFolderId(),
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, xml,
			childDDMStructure.getStructureKey(), ddmTemplate.getTemplateKey());

		long[] childDDMStructureIds = {childDDMStructure.getStructureId()};

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		_journalFolderLocalService.updateFolder(
			TestPropsValues.getUserId(), serviceContext.getScopeGroupId(),
			childFolder.getFolderId(), childFolder.getParentFolderId(),
			childFolder.getName(), childFolder.getDescription(),
			childDDMStructureIds,
			JournalFolderConstants.RESTRICTION_TYPE_DDM_STRUCTURES_AND_WORKFLOW,
			false, serviceContext);

		DDMStructure parentDDMStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		parentFolder = _journalFolderLocalService.updateFolder(
			TestPropsValues.getUserId(), serviceContext.getScopeGroupId(),
			parentFolder.getFolderId(), parentFolder.getParentFolderId(),
			parentFolder.getName(), parentFolder.getDescription(),
			new long[] {parentDDMStructure.getStructureId()},
			JournalFolderConstants.RESTRICTION_TYPE_DDM_STRUCTURES_AND_WORKFLOW,
			false, serviceContext);

		Assert.assertEquals(
			JournalFolderConstants.RESTRICTION_TYPE_DDM_STRUCTURES_AND_WORKFLOW,
			parentFolder.getRestrictionType());
	}

	@Test
	public void testGetInheritedWorkflowFolderId() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		_journalFolderService.updateFolder(
			serviceContext.getScopeGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, null, null,
			new long[0], JournalFolderConstants.RESTRICTION_TYPE_WORKFLOW,
			false, serviceContext);

		JournalFolder countriesFolder = _journalFolderFixture.addFolder(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "Countries");

		Assert.assertEquals(
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			_journalFolderLocalService.getInheritedWorkflowFolderId(
				countriesFolder.getFolderId()));

		JournalFolder germanyFolder = _journalFolderFixture.addFolder(
			_group.getGroupId(), countriesFolder.getFolderId(), "Germany");

		Assert.assertEquals(
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			_journalFolderLocalService.getInheritedWorkflowFolderId(
				germanyFolder.getFolderId()));

		JournalFolder spainFolder = _journalFolderFixture.addFolder(
			_group.getGroupId(), countriesFolder.getFolderId(), "Spain");

		DDMStructure ddmStructure1 = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		_journalFolderService.updateFolder(
			serviceContext.getScopeGroupId(), spainFolder.getFolderId(),
			spainFolder.getParentFolderId(), spainFolder.getName(),
			spainFolder.getDescription(),
			new long[] {ddmStructure1.getStructureId()},
			JournalFolderConstants.RESTRICTION_TYPE_DDM_STRUCTURES_AND_WORKFLOW,
			false, serviceContext);

		Assert.assertEquals(
			spainFolder.getFolderId(),
			_journalFolderLocalService.getInheritedWorkflowFolderId(
				spainFolder.getFolderId()));

		JournalFolder madridFolder = _journalFolderFixture.addFolder(
			_group.getGroupId(), spainFolder.getFolderId(), "Madrid");

		Assert.assertEquals(
			spainFolder.getFolderId(),
			_journalFolderLocalService.getInheritedWorkflowFolderId(
				madridFolder.getFolderId()));
	}

	@Test
	public void testGetJournalFolderByExternalReferenceCode() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();

		JournalFolder folder1 = addJournalFolder(externalReferenceCode);

		JournalFolder folder2 =
			_journalFolderLocalService.getJournalFolderByExternalReferenceCode(
				externalReferenceCode, _group.getGroupId());

		Assert.assertEquals(
			folder2.getExternalReferenceCode(), externalReferenceCode);

		Assert.assertEquals(folder1.getFolderId(), folder2.getFolderId());
	}

	@Test(expected = DuplicateFolderExternalReferenceCodeException.class)
	public void testJournalFolderWithExistingExternalReferenceCode()
		throws Exception {

		JournalFolder folder = addJournalFolder(RandomTestUtil.randomString());

		addJournalFolder(folder.getExternalReferenceCode());
	}

	@Test
	public void testMoveArticleFromTrashToFolder() throws Exception {
		JournalFolder folder1 = _journalFolderFixture.addFolder(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "Test 1");

		String xml = DDMStructureTestUtil.getSampleStructuredContent(
			"Test Article");

		DDMStructure ddmStructure1 = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		DDMTemplate ddmTemplate1 = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), ddmStructure1.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class),
			LocaleUtil.getDefault());

		JournalArticle article = JournalTestUtil.addArticleWithXMLContent(
			_group.getGroupId(), folder1.getFolderId(),
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, xml,
			ddmStructure1.getStructureKey(), ddmTemplate1.getTemplateKey());

		_journalFolderLocalService.moveFolderToTrash(
			TestPropsValues.getUserId(), folder1.getFolderId());

		JournalFolder folder2 = _journalFolderFixture.addFolder(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "Test 2");

		DDMStructure ddmStructure2 = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		long[] ddmStructureIds = {ddmStructure2.getStructureId()};

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		_journalFolderLocalService.updateFolder(
			TestPropsValues.getUserId(), serviceContext.getScopeGroupId(),
			folder2.getFolderId(), folder2.getParentFolderId(),
			folder2.getName(), folder2.getDescription(), ddmStructureIds,
			JournalFolderConstants.RESTRICTION_TYPE_DDM_STRUCTURES_AND_WORKFLOW,
			false, serviceContext);

		TrashHandler trashHandler = TrashHandlerRegistryUtil.getTrashHandler(
			JournalArticle.class.getName());

		try {
			trashHandler.checkRestorableEntry(
				article.getResourcePrimKey(), folder2.getFolderId(), null);

			Assert.fail();
		}
		catch (RestoreEntryException restoreEntryException) {
			if (_log.isDebugEnabled()) {
				_log.debug(restoreEntryException);
			}
		}

		JournalFolder subfolder = _journalFolderFixture.addFolder(
			_group.getGroupId(), folder2.getFolderId(), "Test 2.1");

		try {
			trashHandler.checkRestorableEntry(
				article.getResourcePrimKey(), subfolder.getFolderId(), null);

			Assert.fail();
		}
		catch (RestoreEntryException restoreEntryException) {
			if (_log.isDebugEnabled()) {
				_log.debug(restoreEntryException);
			}
		}
	}

	@Test
	public void testMoveArticleFromTrashWithoutAddArticlePermission()
		throws Exception {

		User user = UserTestUtil.addUser();

		JournalArticle journalArticle =
			_journalArticleLocalService.moveArticleToTrash(
				TestPropsValues.getUserId(),
				JournalTestUtil.addArticle(
					user.getUserId(), _group.getGroupId(),
					JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID));

		JournalFolder journalFolder = _journalFolderFixture.addFolder(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), user.getUserId());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			_assertMustHavePermission(
				ActionKeys.ADD_ARTICLE,
				() -> _journalArticleService.moveArticleFromTrash(
					_group.getGroupId(), journalArticle.getResourcePrimKey(),
					journalFolder.getFolderId(), serviceContext));
			_assertMustHavePermission(
				ActionKeys.ADD_ARTICLE,
				() -> _journalArticleService.moveArticleFromTrash(
					_group.getGroupId(), journalArticle.getArticleId(),
					journalFolder.getFolderId(), serviceContext));
		}
	}

	@Test
	public void testMoveArticleToRestrictedFolder() throws Exception {
		DDMStructure ddmStructure1 = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		DDMTemplate ddmTemplate1 = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), ddmStructure1.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class),
			LocaleUtil.getDefault());

		String xml = DDMStructureTestUtil.getSampleStructuredContent(
			"Test Article");

		JournalArticle article = JournalTestUtil.addArticleWithXMLContent(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, xml,
			ddmStructure1.getStructureKey(), ddmTemplate1.getTemplateKey());

		JournalFolder folder = _journalFolderFixture.addFolder(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "Test 1");

		DDMStructure ddmStructure2 = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		long[] ddmStructureIds = {ddmStructure2.getStructureId()};

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		_journalFolderLocalService.updateFolder(
			TestPropsValues.getUserId(), serviceContext.getScopeGroupId(),
			folder.getFolderId(), folder.getParentFolderId(), folder.getName(),
			folder.getDescription(), ddmStructureIds,
			JournalFolderConstants.RESTRICTION_TYPE_DDM_STRUCTURES_AND_WORKFLOW,
			false, serviceContext);

		try {
			_journalArticleLocalService.moveArticle(
				_group.getGroupId(), article.getArticleId(),
				folder.getFolderId(), serviceContext);

			Assert.fail();
		}
		catch (InvalidDDMStructureException invalidDDMStructureException) {
		}

		JournalFolder subfolder = _journalFolderFixture.addFolder(
			_group.getGroupId(), folder.getFolderId(), "Test 1.1");

		try {
			_journalArticleLocalService.moveArticle(
				_group.getGroupId(), article.getArticleId(),
				subfolder.getFolderId(), serviceContext);

			Assert.fail();
		}
		catch (InvalidDDMStructureException invalidDDMStructureException) {
		}
	}

	@Test
	public void testMoveFolder() throws Exception {
		User user = UserTestUtil.addUser();

		JournalFolder journalFolder = JournalTestUtil.addFolder(
			user.getUserId(), _group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString());

		JournalFolder parentJournalFolder = _journalFolderFixture.addFolder(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString());

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), JournalFolder.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(parentJournalFolder.getFolderId()), role.getRoleId(),
			new String[] {ActionKeys.ADD_SUBFOLDER});

		_userLocalService.addRoleUser(role.getRoleId(), user.getUserId());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			journalFolder = _journalFolderService.moveFolder(
				journalFolder.getFolderId(), parentJournalFolder.getFolderId(),
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), user.getUserId()));
		}

		Assert.assertEquals(
			parentJournalFolder.getFolderId(),
			journalFolder.getParentFolderId());
	}

	@Test
	public void testMoveFolderFromTrashWithoutAddFolderPermission()
		throws Exception {

		User user = UserTestUtil.addUser();

		JournalFolder journalFolder = JournalTestUtil.addFolder(
			user.getUserId(), _group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString());

		_journalFolderLocalService.moveFolderToTrash(
			TestPropsValues.getUserId(), journalFolder.getFolderId());

		JournalFolder parentJournalFolder = _journalFolderFixture.addFolder(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			_assertMustHavePermission(
				ActionKeys.ADD_FOLDER,
				() -> _journalFolderService.moveFolderFromTrash(
					journalFolder.getFolderId(),
					parentJournalFolder.getFolderId(),
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId(), user.getUserId())));
		}
	}

	@Test
	public void testMoveFolderWithAnArticleInTrashToFolder() throws Exception {
		JournalFolder folder1 = _journalFolderFixture.addFolder(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "Test 1");

		JournalFolder folder2 = _journalFolderFixture.addFolder(
			_group.getGroupId(), folder1.getFolderId(), "Test 2");

		String xml = DDMStructureTestUtil.getSampleStructuredContent(
			"Test Article");

		DDMStructure ddmStructure1 = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		DDMTemplate ddmTemplate1 = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), ddmStructure1.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class),
			LocaleUtil.getDefault());

		JournalTestUtil.addArticleWithXMLContent(
			_group.getGroupId(), folder2.getFolderId(),
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, xml,
			ddmStructure1.getStructureKey(), ddmTemplate1.getTemplateKey());

		_journalFolderLocalService.moveFolderToTrash(
			TestPropsValues.getUserId(), folder1.getFolderId());

		JournalFolder folder3 = _journalFolderFixture.addFolder(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "Test 3");

		DDMStructure ddmStructure2 = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		long[] ddmStructureIds = {ddmStructure2.getStructureId()};

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		_journalFolderLocalService.updateFolder(
			TestPropsValues.getUserId(), serviceContext.getScopeGroupId(),
			folder3.getFolderId(), folder3.getParentFolderId(),
			folder3.getName(), folder3.getDescription(), ddmStructureIds,
			JournalFolderConstants.RESTRICTION_TYPE_DDM_STRUCTURES_AND_WORKFLOW,
			false, serviceContext);

		TrashHandler trashHandler = TrashHandlerRegistryUtil.getTrashHandler(
			JournalFolder.class.getName());

		try {
			trashHandler.checkRestorableEntry(
				folder2.getFolderId(), folder3.getFolderId(), null);

			Assert.fail();
		}
		catch (RestoreEntryException restoreEntryException) {
			if (_log.isDebugEnabled()) {
				_log.debug(restoreEntryException);
			}
		}

		JournalFolder subfolder = _journalFolderFixture.addFolder(
			_group.getGroupId(), folder3.getFolderId(), "Test 3.1");

		try {
			trashHandler.checkRestorableEntry(
				folder2.getFolderId(), subfolder.getFolderId(), null);

			Assert.fail();
		}
		catch (RestoreEntryException restoreEntryException) {
			if (_log.isDebugEnabled()) {
				_log.debug(restoreEntryException);
			}
		}
	}

	@Test
	public void testMoveFolderWithAnArticleToFolder() throws Exception {
		JournalFolder folder1 = _journalFolderFixture.addFolder(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "Test 1");

		String xml = DDMStructureTestUtil.getSampleStructuredContent(
			"Test Article");

		DDMStructure ddmStructure1 = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		DDMTemplate ddmTemplate1 = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), ddmStructure1.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class),
			LocaleUtil.getDefault());

		JournalTestUtil.addArticleWithXMLContent(
			_group.getGroupId(), folder1.getFolderId(),
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, xml,
			ddmStructure1.getStructureKey(), ddmTemplate1.getTemplateKey());

		JournalFolder folder2 = _journalFolderFixture.addFolder(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "Test 2");

		DDMStructure ddmStructure2 = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		long[] ddmStructureIds = {ddmStructure2.getStructureId()};

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		_journalFolderLocalService.updateFolder(
			TestPropsValues.getUserId(), serviceContext.getScopeGroupId(),
			folder2.getFolderId(), folder2.getParentFolderId(),
			folder2.getName(), folder2.getDescription(), ddmStructureIds,
			JournalFolderConstants.RESTRICTION_TYPE_DDM_STRUCTURES_AND_WORKFLOW,
			false, serviceContext);

		try {
			_journalFolderLocalService.moveFolder(
				folder1.getFolderId(), folder2.getFolderId(), serviceContext);

			Assert.fail();
		}
		catch (InvalidDDMStructureException invalidDDMStructureException) {
		}

		JournalFolder subfolder = _journalFolderFixture.addFolder(
			_group.getGroupId(), folder2.getFolderId(), "Test 2.1");

		try {
			_journalFolderLocalService.moveFolder(
				folder1.getFolderId(), subfolder.getFolderId(), serviceContext);

			Assert.fail();
		}
		catch (InvalidDDMStructureException invalidDDMStructureException) {
		}
	}

	@Test
	public void testMoveFolderWithoutAddFolderPermission() throws Exception {
		User user = UserTestUtil.addUser();

		JournalFolder journalFolder = JournalTestUtil.addFolder(
			user.getUserId(), _group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString());

		JournalFolder parentJournalFolder = _journalFolderFixture.addFolder(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			_assertMustHavePermission(
				ActionKeys.ADD_FOLDER,
				() -> _journalFolderService.moveFolder(
					journalFolder.getFolderId(),
					parentJournalFolder.getFolderId(),
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId(), user.getUserId())));
		}
	}

	@Test
	public void testRemoveChildRestriction() throws Exception {
		JournalFolder parentFolder = _journalFolderFixture.addFolder(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "Test 1");

		DDMStructure parentDDMStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		long[] parentDDMStructureIds = {parentDDMStructure.getStructureId()};

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		parentFolder = _journalFolderLocalService.updateFolder(
			TestPropsValues.getUserId(), serviceContext.getScopeGroupId(),
			parentFolder.getFolderId(), parentFolder.getParentFolderId(),
			parentFolder.getName(), parentFolder.getDescription(),
			parentDDMStructureIds,
			JournalFolderConstants.RESTRICTION_TYPE_DDM_STRUCTURES_AND_WORKFLOW,
			false, serviceContext);

		JournalFolder childFolder = _journalFolderFixture.addFolder(
			_group.getGroupId(), parentFolder.getFolderId(), "Test 2");

		String xml = DDMStructureTestUtil.getSampleStructuredContent(
			"Test Article");

		DDMStructure childDDMStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		long[] childDDMStructureIds = {childDDMStructure.getStructureId()};

		_journalFolderLocalService.updateFolder(
			TestPropsValues.getUserId(), serviceContext.getScopeGroupId(),
			childFolder.getFolderId(), childFolder.getParentFolderId(),
			childFolder.getName(), childFolder.getDescription(),
			childDDMStructureIds,
			JournalFolderConstants.RESTRICTION_TYPE_DDM_STRUCTURES_AND_WORKFLOW,
			false, serviceContext);

		DDMTemplate childDDMTemplate = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), childDDMStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class),
			LocaleUtil.getDefault());

		JournalTestUtil.addArticleWithXMLContent(
			_group.getGroupId(), childFolder.getFolderId(),
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, xml,
			childDDMStructure.getStructureKey(),
			childDDMTemplate.getTemplateKey());

		try {
			_journalFolderLocalService.updateFolder(
				TestPropsValues.getUserId(), serviceContext.getScopeGroupId(),
				childFolder.getFolderId(), childFolder.getParentFolderId(),
				childFolder.getName(), childFolder.getDescription(),
				new long[0], JournalFolderConstants.RESTRICTION_TYPE_INHERIT,
				false, serviceContext);

			Assert.fail();
		}
		catch (InvalidDDMStructureException invalidDDMStructureException) {
		}

		_journalFolderLocalService.updateFolder(
			TestPropsValues.getUserId(), serviceContext.getScopeGroupId(),
			parentFolder.getFolderId(), parentFolder.getParentFolderId(),
			parentFolder.getName(), parentFolder.getDescription(),
			childDDMStructureIds,
			JournalFolderConstants.RESTRICTION_TYPE_DDM_STRUCTURES_AND_WORKFLOW,
			false, serviceContext);

		childFolder = _journalFolderLocalService.updateFolder(
			TestPropsValues.getUserId(), serviceContext.getScopeGroupId(),
			childFolder.getFolderId(), childFolder.getParentFolderId(),
			childFolder.getName(), childFolder.getDescription(), new long[0],
			JournalFolderConstants.RESTRICTION_TYPE_INHERIT, false,
			serviceContext);

		Assert.assertEquals(
			JournalFolderConstants.RESTRICTION_TYPE_INHERIT,
			childFolder.getRestrictionType());
	}

	@Test
	public void testSearchDDMStructuresInRestrictedFolder() throws Exception {
		JournalFolder folder = _journalFolderFixture.addFolder(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "Test Folder");

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		long[] ddmStructureIds = {ddmStructure.getStructureId()};

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		_journalFolderLocalService.updateFolder(
			TestPropsValues.getUserId(), serviceContext.getScopeGroupId(),
			folder.getFolderId(), folder.getParentFolderId(), folder.getName(),
			folder.getDescription(), ddmStructureIds,
			JournalFolderConstants.RESTRICTION_TYPE_DDM_STRUCTURES_AND_WORKFLOW,
			false, serviceContext);

		List<DDMStructure> ddmStructures =
			_journalFolderService.searchDDMStructures(
				_group.getCompanyId(),
				PortalUtil.getCurrentAndAncestorSiteGroupIds(
					_group.getGroupId()),
				folder.getFolderId(),
				JournalFolderConstants.
					RESTRICTION_TYPE_DDM_STRUCTURES_AND_WORKFLOW,
				null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(ddmStructures.toString(), 1, ddmStructures.size());
	}

	@Test
	public void testSubfolders() throws Exception {
		JournalFolder folder1 = _journalFolderFixture.addFolder(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "Test 1");

		JournalFolder folder11 = _journalFolderFixture.addFolder(
			_group.getGroupId(), folder1.getFolderId(), "Test 1.1");

		JournalFolder folder111 = _journalFolderFixture.addFolder(
			_group.getGroupId(), folder11.getFolderId(), "Test 1.1.1");

		Assert.assertTrue(folder1.isRoot());
		Assert.assertFalse(folder11.isRoot());
		Assert.assertFalse(folder111.isRoot());
		Assert.assertEquals(
			folder1.getFolderId(), folder11.getParentFolderId());
		Assert.assertEquals(
			folder11.getFolderId(), folder111.getParentFolderId());
	}

	@Test
	public void testUpdateFolderRestrictions() throws Exception {
		JournalFolder folder = _journalFolderFixture.addFolder(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "Test 1");

		String xml = DDMStructureTestUtil.getSampleStructuredContent(
			"Test Article");

		DDMStructure ddmStructure1 = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		DDMTemplate ddmTemplate1 = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), ddmStructure1.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class),
			LocaleUtil.getDefault());

		JournalTestUtil.addArticleWithXMLContent(
			_group.getGroupId(), folder.getFolderId(),
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, xml,
			ddmStructure1.getStructureKey(), ddmTemplate1.getTemplateKey());

		DDMStructure ddmStructure2 = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		long[] ddmStructureIds = {ddmStructure2.getStructureId()};

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		try {
			_journalFolderLocalService.updateFolder(
				TestPropsValues.getUserId(), serviceContext.getScopeGroupId(),
				folder.getFolderId(), folder.getParentFolderId(),
				folder.getName(), folder.getDescription(), ddmStructureIds,
				JournalFolderConstants.
					RESTRICTION_TYPE_DDM_STRUCTURES_AND_WORKFLOW,
				false, serviceContext);

			Assert.fail();
		}
		catch (InvalidDDMStructureException invalidDDMStructureException) {
		}

		JournalFolder subfolder = _journalFolderFixture.addFolder(
			_group.getGroupId(), folder.getFolderId(), "Test 1.1");

		JournalTestUtil.addArticleWithXMLContent(
			_group.getGroupId(), subfolder.getFolderId(),
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, xml,
			ddmStructure1.getStructureKey(), ddmTemplate1.getTemplateKey());

		try {
			_journalFolderLocalService.updateFolder(
				TestPropsValues.getUserId(), serviceContext.getScopeGroupId(),
				folder.getFolderId(), folder.getParentFolderId(),
				folder.getName(), folder.getDescription(), ddmStructureIds,
				JournalFolderConstants.
					RESTRICTION_TYPE_DDM_STRUCTURES_AND_WORKFLOW,
				false, serviceContext);

			Assert.fail();
		}
		catch (InvalidDDMStructureException invalidDDMStructureException) {
		}
	}

	@Test
	public void testUpdateFolderWithAdvancedUpdatePermission()
		throws Exception {

		JournalFolder parentFolder = _journalFolderFixture.addFolder(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString());

		JournalFolder childFolder = _journalFolderFixture.addFolder(
			_group.getGroupId(), parentFolder.getFolderId(),
			RandomTestUtil.randomString());

		JournalFolder irrelevantFolder = _journalFolderFixture.addFolder(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString());

		String name = childFolder.getName();
		String description = childFolder.getDescription();

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);
		User user = UserTestUtil.addUser();

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), JournalFolder.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(childFolder.getFolderId()), role.getRoleId(),
			new String[] {ActionKeys.ADVANCED_UPDATE});

		_userLocalService.addRoleUser(role.getRoleId(), user.getUserId());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			childFolder = _journalFolderService.updateFolder(
				_group.getGroupId(), childFolder.getFolderId(),
				irrelevantFolder.getFolderId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), new long[0],
				JournalFolderConstants.RESTRICTION_TYPE_INHERIT, false,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), user.getUserId()));

			Assert.assertEquals(
				parentFolder.getFolderId(), childFolder.getParentFolderId());
			Assert.assertEquals(name, childFolder.getName());
			Assert.assertEquals(description, childFolder.getDescription());
		}
	}

	@Test
	public void testUpdateFolderWithoutAddFolderPermission() throws Exception {
		User user = UserTestUtil.addUser();

		JournalFolder journalFolder = JournalTestUtil.addFolder(
			user.getUserId(), _group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString());

		JournalFolder parentJournalFolder = _journalFolderFixture.addFolder(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			_assertMustHavePermission(
				ActionKeys.ADD_FOLDER,
				() -> _journalFolderService.updateFolder(
					_group.getGroupId(), journalFolder.getFolderId(),
					parentJournalFolder.getFolderId(), journalFolder.getName(),
					journalFolder.getDescription(), false,
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId(), user.getUserId())));
		}
	}

	@Test
	public void testUpdateParentWithRestriction() throws Exception {
		JournalFolder parentFolder = _journalFolderFixture.addFolder(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "Test 1");

		DDMStructure parentDDMStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		long[] parentDDMStructureIds = {parentDDMStructure.getStructureId()};

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		parentFolder = _journalFolderLocalService.updateFolder(
			TestPropsValues.getUserId(), serviceContext.getScopeGroupId(),
			parentFolder.getFolderId(), parentFolder.getParentFolderId(),
			parentFolder.getName(), parentFolder.getDescription(),
			parentDDMStructureIds,
			JournalFolderConstants.RESTRICTION_TYPE_DDM_STRUCTURES_AND_WORKFLOW,
			false, serviceContext);

		JournalFolder childFolder = _journalFolderFixture.addFolder(
			_group.getGroupId(), parentFolder.getFolderId(), "Test 2");

		String xml = DDMStructureTestUtil.getSampleStructuredContent(
			"Test Article");

		DDMStructure childDDMStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		_journalFolderLocalService.updateFolder(
			TestPropsValues.getUserId(), serviceContext.getScopeGroupId(),
			childFolder.getFolderId(), childFolder.getParentFolderId(),
			childFolder.getName(), childFolder.getDescription(),
			new long[] {childDDMStructure.getStructureId()},
			JournalFolderConstants.RESTRICTION_TYPE_DDM_STRUCTURES_AND_WORKFLOW,
			false, serviceContext);

		DDMTemplate childDDMTemplate = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), childDDMStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class),
			LocaleUtil.getDefault());

		JournalTestUtil.addArticleWithXMLContent(
			_group.getGroupId(), childFolder.getFolderId(),
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, xml,
			childDDMStructure.getStructureKey(),
			childDDMTemplate.getTemplateKey());

		parentFolder = _journalFolderLocalService.updateFolder(
			TestPropsValues.getUserId(), serviceContext.getScopeGroupId(),
			parentFolder.getFolderId(), parentFolder.getParentFolderId(),
			parentFolder.getName(), "Description 1", parentDDMStructureIds,
			JournalFolderConstants.RESTRICTION_TYPE_DDM_STRUCTURES_AND_WORKFLOW,
			false, serviceContext);

		Assert.assertEquals("Description 1", parentFolder.getDescription());
	}

	protected JournalFolder addJournalFolder(String externalReferenceCode)
		throws Exception {

		return _journalFolderFixture.addFolder(
			externalReferenceCode,
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));
	}

	private void _assertMustHavePermission(
			String actionId, UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try {
			unsafeRunnable.run();

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			String message = principalException.getMessage();

			Assert.assertTrue(message, message.contains(actionId));
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalFolderServiceTest.class);

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private JournalArticleService _journalArticleService;

	private JournalFolderFixture _journalFolderFixture;

	@Inject
	private JournalFolderLocalService _journalFolderLocalService;

	@Inject
	private JournalFolderService _journalFolderService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private UserLocalService _userLocalService;

}