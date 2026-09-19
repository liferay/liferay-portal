/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.exception.AssetCategoryException;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetTagLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyLocalServiceUtil;
import com.liferay.asset.link.model.AssetLink;
import com.liferay.asset.link.service.AssetLinkLocalServiceUtil;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoValue;
import com.liferay.expando.test.util.ExpandoTestUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowThreadLocal;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.wiki.exception.DuplicatePageException;
import com.liferay.wiki.exception.DuplicateWikiPageExternalReferenceCodeException;
import com.liferay.wiki.exception.NoSuchPageResourceException;
import com.liferay.wiki.exception.PageTitleException;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiPageLocalService;
import com.liferay.wiki.service.WikiPageLocalServiceUtil;
import com.liferay.wiki.test.util.WikiTestUtil;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Manuel de la Peña
 * @author Roberto Díaz
 */
@RunWith(Arquillian.class)
public class WikiPageLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		_group = GroupTestUtil.addGroup();

		_node = WikiTestUtil.addNode(_group.getGroupId());
		_user = UserTestUtil.addUser(_group.getGroupId());
	}

	@Test
	public void testAddFrontPageWithoutRequiredCategory() throws Exception {
		AssetVocabulary assetVocabulary = getRequiredAssetVocabulary();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		AssetCategoryLocalServiceUtil.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(), "category 1",
			assetVocabulary.getVocabularyId(), serviceContext);

		WikiPage frontPage = WikiTestUtil.addPage(
			TestPropsValues.getUserId(), _node.getNodeId(), "FrontPage",
			RandomTestUtil.randomString(), true, serviceContext);

		Assert.assertTrue(
			ListUtil.isNull(
				AssetCategoryLocalServiceUtil.getCategories(
					WikiPage.class.getName(), frontPage.getResourcePrimKey())));
	}

	@Test(expected = DuplicateWikiPageExternalReferenceCodeException.class)
	public void testAddPageWithExistingExternalReferenceCode()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		WikiPage wikiPage = WikiTestUtil.addPage(
			TestPropsValues.getUserId(), _node.getNodeId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), true,
			serviceContext);

		WikiPageLocalServiceUtil.addPage(
			wikiPage.getExternalReferenceCode(), TestPropsValues.getUserId(),
			_node.getNodeId(), RandomTestUtil.randomString(),
			WorkflowConstants.ACTION_PUBLISH, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), false, "creole", true,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);
	}

	@Test
	public void testAddPageWithExternalReferenceCode() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();

		WikiPage wikiPage = WikiPageLocalServiceUtil.addPage(
			externalReferenceCode, TestPropsValues.getUserId(),
			_node.getNodeId(), RandomTestUtil.randomString(),
			WorkflowConstants.ACTION_PUBLISH, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), false, "creole", true,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(
			externalReferenceCode, wikiPage.getExternalReferenceCode());
	}

	@Test
	public void testAddPageWithInvalidTitle() throws Exception {
		char[] invalidCharacters = "\\[]|:;%<>".toCharArray();

		for (char invalidCharacter : invalidCharacters) {
			try {
				WikiTestUtil.addPage(
					TestPropsValues.getUserId(), _node.getNodeId(),
					"ChildPage" + invalidCharacter,
					RandomTestUtil.randomString(), true,
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId()));

				Assert.fail(
					"Created a page with invalid character " +
						invalidCharacter);
			}
			catch (PageTitleException pageTitleException) {
			}
		}
	}

	@Test
	public void testAddPageWithNbspTitle() throws Exception {
		WikiPage page = WikiTestUtil.addPage(
			TestPropsValues.getUserId(), _node.getNodeId(),
			"ChildPage" + CharPool.NO_BREAK_SPACE + "1",
			RandomTestUtil.randomString(), true,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals("ChildPage 1", page.getTitle());
	}

	@Test
	public void testAddPageWithRequiredCategory() throws Exception {
		AssetVocabulary assetVocabulary = getRequiredAssetVocabulary();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		AssetCategory assetCategory = AssetCategoryLocalServiceUtil.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(), "category 1",
			assetVocabulary.getVocabularyId(), serviceContext);

		long categoryId = assetCategory.getCategoryId();

		serviceContext.setAssetCategoryIds(new long[] {categoryId});

		WikiPage page = WikiTestUtil.addPage(
			TestPropsValues.getUserId(), _node.getNodeId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), true,
			serviceContext);

		List<AssetCategory> assetCategories =
			AssetCategoryLocalServiceUtil.getCategories(
				WikiPage.class.getName(), page.getResourcePrimKey());

		Assert.assertEquals(
			assetCategories.toString(), 1, assetCategories.size());

		AssetCategory persistedAssetCategory = assetCategories.get(0);

		Assert.assertEquals(categoryId, persistedAssetCategory.getCategoryId());
	}

	@Test
	public void testAddPageWithoutExternalReferenceCode() throws Exception {
		WikiPage wikiPage1 = WikiTestUtil.addPage(
			TestPropsValues.getUserId(), _node.getNodeId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), true,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		String externalReferenceCode = wikiPage1.getExternalReferenceCode();

		Assert.assertEquals(externalReferenceCode, wikiPage1.getUuid());

		WikiPage wikiPage2 =
			WikiPageLocalServiceUtil.getLatestPageByExternalReferenceCode(
				_group.getGroupId(), externalReferenceCode);

		Assert.assertEquals(wikiPage1, wikiPage2);
	}

	@Test(expected = AssetCategoryTestException.class)
	public void testAddPageWithoutRequiredCategory() throws Exception {
		AssetVocabulary assetVocabulary = getRequiredAssetVocabulary();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		AssetCategoryLocalServiceUtil.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(), "category 1",
			assetVocabulary.getVocabularyId(), serviceContext);

		try {
			WikiTestUtil.addPage(
				TestPropsValues.getUserId(), _node.getNodeId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				true, serviceContext);
		}
		catch (AssetCategoryException assetCategoryException) {
			if (_log.isDebugEnabled()) {
				_log.debug(assetCategoryException);
			}

			throw new AssetCategoryTestException();
		}
	}

	@Test
	public void testChangeParent() throws Exception {
		testChangeParent(false);
	}

	@Test
	public void testChangeParentChangesAllWikiPageVersionsParentTitle()
		throws Exception {

		WikiTestUtil.addPage(
			TestPropsValues.getUserId(), _group.getGroupId(), _node.getNodeId(),
			"ParentPage1", true);
		WikiTestUtil.addPage(
			TestPropsValues.getUserId(), _group.getGroupId(), _node.getNodeId(),
			"ParentPage2", true);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		WikiPage childPage = WikiTestUtil.addPage(
			TestPropsValues.getUserId(), _node.getNodeId(), "ChildPage",
			RandomTestUtil.randomString(), "ParentPage1", true, serviceContext);

		WikiTestUtil.updatePage(
			childPage, TestPropsValues.getUserId(), StringUtil.randomString(),
			serviceContext);

		WikiPageLocalServiceUtil.changeParent(
			TestPropsValues.getUserId(), _node.getNodeId(),
			childPage.getTitle(), "ParentPage2", serviceContext);

		List<WikiPage> pages = WikiPageLocalServiceUtil.getPages(
			childPage.getNodeId(), childPage.getTitle(), QueryUtil.ALL_POS,
			QueryUtil.ALL_POS);

		Assert.assertEquals(pages.toString(), 3, pages.size());

		for (WikiPage curWikiPage : pages) {
			Assert.assertEquals("ParentPage2", curWikiPage.getParentTitle());
		}
	}

	@Test
	public void testChangeParentWithExpando() throws Exception {
		testChangeParent(true);
	}

	@Test
	public void testChangeParentWithWorkflowChangesParentAfterUpdateStatus()
		throws Exception {

		WikiTestUtil.addPage(
			TestPropsValues.getUserId(), _group.getGroupId(), _node.getNodeId(),
			"ParentPage1", true);
		WikiTestUtil.addPage(
			TestPropsValues.getUserId(), _group.getGroupId(), _node.getNodeId(),
			"ParentPage2", true);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		WikiPage childPage = WikiTestUtil.addPage(
			TestPropsValues.getUserId(), _node.getNodeId(), "ChildPage",
			RandomTestUtil.randomString(), "ParentPage1", true, serviceContext);

		boolean workflowEnabled = WorkflowThreadLocal.isEnabled();

		try {
			WorkflowThreadLocal.setEnabled(true);

			serviceContext = (ServiceContext)serviceContext.clone();

			serviceContext.setWorkflowAction(
				WorkflowConstants.ACTION_SAVE_DRAFT);

			WikiPage pendingChildPage = WikiPageLocalServiceUtil.changeParent(
				TestPropsValues.getUserId(), _node.getNodeId(),
				childPage.getTitle(), "ParentPage2", serviceContext);

			childPage = WikiPageLocalServiceUtil.getPage(
				_node.getNodeId(), childPage.getTitle(), true);

			Assert.assertEquals("ParentPage1", childPage.getParentTitle());

			Map<String, Serializable> workflowContext =
				HashMapBuilder.<String, Serializable>put(
					WorkflowConstants.CONTEXT_COMMAND,
					serviceContext.getCommand()
				).build();

			WikiPageLocalServiceUtil.updateStatus(
				TestPropsValues.getUserId(), pendingChildPage,
				WorkflowConstants.STATUS_APPROVED, serviceContext,
				workflowContext);

			List<WikiPage> pages = WikiPageLocalServiceUtil.getPages(
				childPage.getNodeId(), childPage.getTitle(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

			Assert.assertEquals(pages.toString(), 2, pages.size());

			for (WikiPage curWikiPage : pages) {
				Assert.assertEquals(
					"ParentPage2", curWikiPage.getParentTitle());
			}
		}
		finally {
			WorkflowThreadLocal.setEnabled(workflowEnabled);
		}
	}

	@Test
	public void testCopyPage() throws Exception {
		WikiPage page = WikiTestUtil.addPage(
			_group.getGroupId(), _node.getNodeId(), true);

		WikiTestUtil.addPageAttachment(
			page.getUserId(), page.getNodeId(), page.getTitle(), getClass());

		List<FileEntry> attachmentsFileEntries =
			page.getAttachmentsFileEntries();

		WikiPage copyPage = WikiTestUtil.copyPage(
			page, true,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		List<FileEntry> copyAttachmentsFileEntries =
			copyPage.getAttachmentsFileEntries();

		Assert.assertEquals(
			attachmentsFileEntries.toString(),
			copyAttachmentsFileEntries.size(), attachmentsFileEntries.size());

		FileEntry fileEntry = attachmentsFileEntries.get(0);
		FileEntry copyFileEntry = copyAttachmentsFileEntries.get(0);

		Assert.assertEquals(
			copyFileEntry.getExtension(), fileEntry.getExtension());
		Assert.assertEquals(
			copyFileEntry.getMimeType(), fileEntry.getMimeType());
		Assert.assertEquals(copyFileEntry.getTitle(), fileEntry.getTitle());
		Assert.assertEquals(copyFileEntry.getSize(), fileEntry.getSize());
	}

	@Test
	public void testCopyPageWithDraftAttachments() throws Exception {
		WikiPage approvedPage = _createPageWithDraftAttachments();

		WikiPage copyPage = WikiTestUtil.copyPage(
			approvedPage, true,
			ServiceContextTestUtil.getServiceContext(
				approvedPage.getGroupId()));

		List<FileEntry> copyAttachmentsFileEntries =
			copyPage.getAttachmentsFileEntries();

		Assert.assertEquals(
			copyAttachmentsFileEntries.toString(), 1,
			copyAttachmentsFileEntries.size());
	}

	@Test(expected = NoSuchPageResourceException.class)
	public void testDeletePage() throws Exception {
		WikiPage page = WikiTestUtil.addPage(
			TestPropsValues.getUserId(), _group.getGroupId(), _node.getNodeId(),
			"TestPage", true);

		WikiPageLocalServiceUtil.deletePage(page);

		WikiPageLocalServiceUtil.getPage(page.getResourcePrimKey());
	}

	@Test
	public void testDeleteParentPageWithChangedParentChild() throws Exception {
		WikiTestUtil.addPage(
			TestPropsValues.getUserId(), _group.getGroupId(), _node.getNodeId(),
			"ParentPage1", true);
		WikiTestUtil.addPage(
			TestPropsValues.getUserId(), _group.getGroupId(), _node.getNodeId(),
			"ParentPage2", true);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		WikiPage childPage = WikiTestUtil.addPage(
			TestPropsValues.getUserId(), _node.getNodeId(), "ChildPage",
			RandomTestUtil.randomString(), "ParentPage1", true, serviceContext);

		WikiPageLocalServiceUtil.changeParent(
			TestPropsValues.getUserId(), _node.getNodeId(),
			childPage.getTitle(), "ParentPage2", serviceContext);

		WikiPageLocalServiceUtil.deletePage(_node.getNodeId(), "ParentPage1");

		childPage = WikiPageLocalServiceUtil.getPage(
			_node.getNodeId(), childPage.getTitle());

		Assert.assertEquals("ChildPage", childPage.getTitle());
	}

	@Test
	public void testDeleteTrashedPageWithExplicitTrashedRedirectPage()
		throws Exception {

		WikiPage[] pages = WikiTestUtil.addRenamedTrashedPage(
			_group.getGroupId(), _node.getNodeId(), true);

		WikiPage page = pages[0];
		WikiPage redirectPage = pages[1];

		WikiPageLocalServiceUtil.deletePage(page);

		try {
			WikiPageLocalServiceUtil.getPage(page.getResourcePrimKey());

			Assert.fail();
		}
		catch (NoSuchPageResourceException noSuchPageResourceException) {
			redirectPage = WikiPageLocalServiceUtil.getPage(
				redirectPage.getResourcePrimKey());

			Assert.assertNull(redirectPage.fetchRedirectPage());
		}
	}

	@Test(expected = NoSuchPageResourceException.class)
	public void testDeleteTrashedPageWithImplicitTrashedRedirectPage()
		throws Exception {

		WikiPage[] pages = WikiTestUtil.addRenamedTrashedPage(
			_group.getGroupId(), _node.getNodeId(), false);

		WikiPage page = pages[0];
		WikiPage redirectPage = pages[1];

		WikiPageLocalServiceUtil.deletePage(page);

		try {
			WikiPageLocalServiceUtil.getPage(page.getResourcePrimKey());

			Assert.fail();
		}
		catch (NoSuchPageResourceException noSuchPageResourceException) {
			WikiPageLocalServiceUtil.getPage(redirectPage.getResourcePrimKey());
		}
	}

	@Test
	public void testDeleteTrashedPageWithRestoredChildPage() throws Exception {
		WikiPage[] pages = WikiTestUtil.addTrashedPageWithChildPage(
			_group.getGroupId(), _node.getNodeId(), true);

		WikiPage parentPage = pages[0];

		WikiPage childPage = pages[1];

		WikiPageLocalServiceUtil.restorePageFromTrash(
			TestPropsValues.getUserId(), childPage);

		WikiPageLocalServiceUtil.deletePage(parentPage);

		try {
			WikiPageLocalServiceUtil.getPage(parentPage.getResourcePrimKey());

			Assert.fail();
		}
		catch (NoSuchPageResourceException noSuchPageResourceException) {
			childPage = WikiPageLocalServiceUtil.getPage(
				childPage.getResourcePrimKey());

			Assert.assertNull(childPage.fetchParentPage());
			Assert.assertEquals(
				WorkflowConstants.STATUS_APPROVED, childPage.getStatus());
		}
	}

	@Test
	public void testDeleteTrashedPageWithRestoredRedirectPage()
		throws Exception {

		WikiPage[] pages = WikiTestUtil.addRenamedTrashedPage(
			_group.getGroupId(), _node.getNodeId(), true);

		WikiPage page = pages[0];

		WikiPage redirectPage = pages[1];

		WikiPageLocalServiceUtil.restorePageFromTrash(
			TestPropsValues.getUserId(), redirectPage);

		WikiPageLocalServiceUtil.deletePage(page);

		try {
			WikiPageLocalServiceUtil.getPage(page.getResourcePrimKey());

			Assert.fail();
		}
		catch (NoSuchPageResourceException noSuchPageResourceException) {
			redirectPage = WikiPageLocalServiceUtil.getPageByPageId(
				redirectPage.getPageId());

			Assert.assertNull(redirectPage.fetchRedirectPage());
			Assert.assertEquals(
				WorkflowConstants.STATUS_APPROVED, redirectPage.getStatus());
		}
	}

	@Test
	public void testDeleteTrashedParentPageWithExplicitTrashedChildPage()
		throws Exception {

		WikiPage[] pages = WikiTestUtil.addTrashedPageWithChildPage(
			_group.getGroupId(), _node.getNodeId(), true);

		WikiPage parentPage = pages[0];
		WikiPage childPage = pages[1];

		WikiPageLocalServiceUtil.deletePage(parentPage);

		try {
			WikiPageLocalServiceUtil.getPage(parentPage.getResourcePrimKey());

			Assert.fail();
		}
		catch (NoSuchPageResourceException noSuchPageResourceException) {
			childPage = WikiPageLocalServiceUtil.getPageByPageId(
				childPage.getPageId());

			Assert.assertNull(childPage.fetchParentPage());
		}
	}

	@Test(expected = NoSuchPageResourceException.class)
	public void testDeleteTrashedParentPageWithImplicitTrashedChildPage()
		throws Exception {

		WikiPage[] pages = WikiTestUtil.addTrashedPageWithChildPage(
			_group.getGroupId(), _node.getNodeId(), false);

		WikiPage parentPage = pages[0];
		WikiPage childPage = pages[1];

		WikiPageLocalServiceUtil.deletePage(parentPage);

		try {
			WikiPageLocalServiceUtil.getPage(parentPage.getResourcePrimKey());

			Assert.fail();
		}
		catch (NoSuchPageResourceException noSuchPageResourceException) {
			WikiPageLocalServiceUtil.getPage(childPage.getResourcePrimKey());
		}
	}

	@Test
	public void testFetchPersistedModelByResourcePrimKey() throws Exception {
		WikiPage wikiPage = WikiTestUtil.addPage(
			_group.getGroupId(), _node.getNodeId(), true);

		PersistedModel persistedModel =
			_wikiPageLocalService.fetchPersistedModel(
				wikiPage.getResourcePrimKey());

		Assert.assertNotNull(persistedModel);
	}

	@Test
	public void testGetAttachmentsFileEntriesWithDraftPage() throws Exception {
		WikiPage approvedPage = _createPageWithDraftAttachments();

		WikiPage draftPage = WikiPageLocalServiceUtil.getLatestPage(
			approvedPage.getResourcePrimKey(), WorkflowConstants.STATUS_ANY,
			false);

		List<FileEntry> attachmentsFileEntries =
			approvedPage.getAttachmentsFileEntries();

		Assert.assertEquals(
			attachmentsFileEntries.toString(), 1,
			attachmentsFileEntries.size());

		attachmentsFileEntries = draftPage.getAttachmentsFileEntries();

		Assert.assertEquals(
			attachmentsFileEntries.toString(), 2,
			attachmentsFileEntries.size());
	}

	@Test
	public void testGetPage() throws Exception {
		WikiPage page = WikiTestUtil.addPage(
			_group.getGroupId(), _node.getNodeId(), true);

		WikiPage retrievedPage = WikiPageLocalServiceUtil.getPage(
			page.getResourcePrimKey());

		Assert.assertEquals(retrievedPage, page);
	}

	@Test
	public void testGetRecentChangesWithANonrecentCreatedPage()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		WikiPage page = WikiTestUtil.addPage(
			TestPropsValues.getUserId(), _node.getNodeId(), "Page1",
			RandomTestUtil.randomString(), true, serviceContext);

		Calendar cal = CalendarFactoryUtil.getCalendar();

		cal.add(Calendar.WEEK_OF_YEAR, -2);

		Date date = cal.getTime();

		serviceContext.setCreateDate(date);
		serviceContext.setModifiedDate(date);

		WikiTestUtil.addPage(
			TestPropsValues.getUserId(), _node.getNodeId(), "Page2",
			RandomTestUtil.randomString(), true, serviceContext);

		List<WikiPage> recentPages = WikiPageLocalServiceUtil.getRecentChanges(
			_group.getGroupId(), _node.getNodeId(), QueryUtil.ALL_POS,
			QueryUtil.ALL_POS);

		Assert.assertEquals(recentPages.toString(), 1, recentPages.size());

		Assert.assertEquals(page, recentPages.get(0));
	}

	@Test
	public void testGetRecentChangesWithAnUpdatedNonrecentCreatedPage()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		WikiPage page1 = WikiTestUtil.addPage(
			TestPropsValues.getUserId(), _node.getNodeId(), "Page1",
			RandomTestUtil.randomString(), true, serviceContext);

		Calendar cal = CalendarFactoryUtil.getCalendar();

		cal.add(Calendar.WEEK_OF_YEAR, -2);

		Date date = cal.getTime();

		serviceContext.setCreateDate(date);
		serviceContext.setModifiedDate(date);

		WikiPage page2 = WikiTestUtil.addPage(
			TestPropsValues.getUserId(), _node.getNodeId(), "Page2",
			RandomTestUtil.randomString(), true, serviceContext);

		WikiPage updatedPage2 = WikiTestUtil.updatePage(page2);

		List<WikiPage> recentPages = WikiPageLocalServiceUtil.getRecentChanges(
			_group.getGroupId(), _node.getNodeId(), QueryUtil.ALL_POS,
			QueryUtil.ALL_POS);

		Assert.assertEquals(recentPages.toString(), 2, recentPages.size());

		Assert.assertEquals(updatedPage2, recentPages.get(0));
		Assert.assertEquals(page1, recentPages.get(1));
	}

	@Test
	public void testMovePageToTrashWithDraftAttachments() throws Exception {
		WikiPage approvedPage = _createPageWithDraftAttachments();

		WikiPage draftPage = WikiPageLocalServiceUtil.getLatestPage(
			approvedPage.getResourcePrimKey(), WorkflowConstants.STATUS_ANY,
			false);

		List<FileEntry> attachmentsFileEntries =
			draftPage.getAttachmentsFileEntries();

		WikiPageLocalServiceUtil.movePageToTrash(
			approvedPage.getUserId(), approvedPage);

		FileEntry draftFileEntry = attachmentsFileEntries.get(1);

		DLFileEntry dlFileEntry = DLFileEntryLocalServiceUtil.getFileEntry(
			draftFileEntry.getFileEntryId());

		Assert.assertTrue(dlFileEntry.isInTrash());
	}

	@Test
	public void testOrderByModifiedDate() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		WikiPage page1 = WikiTestUtil.addPage(
			TestPropsValues.getUserId(), _node.getNodeId(), "Page1",
			RandomTestUtil.randomString(), true, serviceContext);

		WikiPage page2 = WikiTestUtil.addPage(
			TestPropsValues.getUserId(), _node.getNodeId(), "Page2",
			RandomTestUtil.randomString(), true, serviceContext);

		List<WikiPage> recentPages = WikiPageLocalServiceUtil.getRecentChanges(
			_group.getGroupId(), _node.getNodeId(), QueryUtil.ALL_POS,
			QueryUtil.ALL_POS);

		Assert.assertEquals(recentPages.toString(), 2, recentPages.size());

		Assert.assertEquals(page2, recentPages.get(0));
		Assert.assertEquals(page1, recentPages.get(1));
	}

	@Test
	public void testOrderByModifiedDateWithModifiedPages() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		WikiPage page1 = WikiTestUtil.addPage(
			TestPropsValues.getUserId(), _node.getNodeId(), "Page1",
			RandomTestUtil.randomString(), true, serviceContext);

		WikiPage page2 = WikiTestUtil.addPage(
			TestPropsValues.getUserId(), _node.getNodeId(), "Page2",
			RandomTestUtil.randomString(), true, serviceContext);

		WikiPage updatedPage1 = WikiTestUtil.updatePage(page1);

		List<WikiPage> recentPages = WikiPageLocalServiceUtil.getRecentChanges(
			_group.getGroupId(), _node.getNodeId(), QueryUtil.ALL_POS,
			QueryUtil.ALL_POS);

		Assert.assertEquals(recentPages.toString(), 2, recentPages.size());

		Assert.assertEquals(updatedPage1, recentPages.get(0));
		Assert.assertEquals(page2, recentPages.get(1));
	}

	@Test
	public void testRenameDefaultVersionPageWithAssetCategories()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		AssetCategory assetCategory1 = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());
		AssetCategory assetCategory2 = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		long[] assetCategoryIds = new long[2];

		assetCategoryIds[0] = assetCategory1.getCategoryId();
		assetCategoryIds[1] = assetCategory2.getCategoryId();

		serviceContext.setAssetCategoryIds(assetCategoryIds);

		WikiPage page = WikiTestUtil.addPage(
			TestPropsValues.getUserId(), _node.getNodeId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), true,
			serviceContext);

		WikiPageLocalServiceUtil.renamePage(
			TestPropsValues.getUserId(), _node.getNodeId(), page.getTitle(),
			"New Title", true,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		WikiPage renamedPage = WikiPageLocalServiceUtil.getPage(
			_node.getNodeId(), "New Title");

		long[] finalAssetCategoryIds =
			AssetCategoryLocalServiceUtil.getCategoryIds(
				WikiPage.class.getName(), renamedPage.getResourcePrimKey());

		_assertArrayEquals(assetCategoryIds, finalAssetCategoryIds);
	}

	@Test
	public void testRenameDefaultVersionPageWithAssetTags() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		AssetTag assetTag1 = AssetTestUtil.addTag(_group.getGroupId());
		AssetTag assetTag2 = AssetTestUtil.addTag(_group.getGroupId());

		String[] assetTagNames = new String[2];

		assetTagNames[0] = assetTag1.getName();
		assetTagNames[1] = assetTag2.getName();

		serviceContext.setAssetTagNames(assetTagNames);

		WikiPage page = WikiTestUtil.addPage(
			TestPropsValues.getUserId(), _node.getNodeId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), true,
			serviceContext);

		WikiPageLocalServiceUtil.renamePage(
			TestPropsValues.getUserId(), _node.getNodeId(), page.getTitle(),
			"New Title", true,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		WikiPage renamedPage = WikiPageLocalServiceUtil.getPage(
			_node.getNodeId(), "New Title");

		Assert.assertNotNull(renamedPage);

		String[] finalAssetTagNames = AssetTagLocalServiceUtil.getTagNames(
			WikiPage.class.getName(), renamedPage.getResourcePrimKey());

		_assertArrayEquals(assetTagNames, finalAssetTagNames);
	}

	@Test
	public void testRenameNondefaultVersionPageWithAssetCategories()
		throws Exception {

		ServiceContext defaultVersionPageServiceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		AssetCategory defaultVersionPageAssetCategory1 =
			AssetTestUtil.addCategory(
				_group.getGroupId(), assetVocabulary.getVocabularyId());
		AssetCategory defaultVersionPageAssetCategory2 =
			AssetTestUtil.addCategory(
				_group.getGroupId(), assetVocabulary.getVocabularyId());

		long[] defaultVersionPageAssetCategoryIds = new long[2];

		defaultVersionPageAssetCategoryIds[0] =
			defaultVersionPageAssetCategory1.getCategoryId();
		defaultVersionPageAssetCategoryIds[1] =
			defaultVersionPageAssetCategory2.getCategoryId();

		defaultVersionPageServiceContext.setAssetCategoryIds(
			defaultVersionPageAssetCategoryIds);

		WikiPage page = WikiTestUtil.addPage(
			TestPropsValues.getUserId(), _node.getNodeId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), true,
			defaultVersionPageServiceContext);

		ServiceContext nondefaultVersionPageServiceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		AssetCategory assetCategory3 = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());
		AssetCategory assetCategory4 = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		long[] nondefaultVersionPageAssetCategoryIds = new long[2];

		nondefaultVersionPageAssetCategoryIds[0] =
			assetCategory3.getCategoryId();
		nondefaultVersionPageAssetCategoryIds[1] =
			assetCategory4.getCategoryId();

		nondefaultVersionPageServiceContext.setAssetCategoryIds(
			nondefaultVersionPageAssetCategoryIds);

		WikiTestUtil.updatePage(
			page, TestPropsValues.getUserId(), StringUtil.randomString(),
			nondefaultVersionPageServiceContext);

		ServiceContext renamePageServiceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		WikiPageLocalServiceUtil.renamePage(
			TestPropsValues.getUserId(), _node.getNodeId(), page.getTitle(),
			"New Title", true, renamePageServiceContext);

		WikiPage renamedPage = WikiPageLocalServiceUtil.getPage(
			_node.getNodeId(), "New Title");

		long[] finalAssetCategoryIds =
			AssetCategoryLocalServiceUtil.getCategoryIds(
				WikiPage.class.getName(), renamedPage.getResourcePrimKey());

		_assertArrayEquals(
			nondefaultVersionPageAssetCategoryIds, finalAssetCategoryIds);
	}

	@Test
	public void testRenameNondefaultVersionPageWithAssetTags()
		throws Exception {

		ServiceContext defaultVersionPageServiceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		AssetTag defaultVersionPageAssetTag1 = AssetTestUtil.addTag(
			_group.getGroupId());
		AssetTag defaultVersionPageAssetTag2 = AssetTestUtil.addTag(
			_group.getGroupId());

		String[] defaultVersionPageAssetTagNames = new String[2];

		defaultVersionPageAssetTagNames[0] =
			defaultVersionPageAssetTag1.getName();
		defaultVersionPageAssetTagNames[1] =
			defaultVersionPageAssetTag2.getName();

		defaultVersionPageServiceContext.setAssetTagNames(
			defaultVersionPageAssetTagNames);

		WikiPage page = WikiTestUtil.addPage(
			TestPropsValues.getUserId(), _node.getNodeId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), true,
			defaultVersionPageServiceContext);

		ServiceContext nondefaultVersionPageServiceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		AssetTag nondefaultVersionPageAssetTag1 = AssetTestUtil.addTag(
			_group.getGroupId());
		AssetTag nondefaultVersionPageAssetTag2 = AssetTestUtil.addTag(
			_group.getGroupId());

		String[] nondefaultVersionPageAssetTagNames = new String[2];

		nondefaultVersionPageAssetTagNames[0] =
			nondefaultVersionPageAssetTag1.getName();
		nondefaultVersionPageAssetTagNames[1] =
			nondefaultVersionPageAssetTag2.getName();

		nondefaultVersionPageServiceContext.setAssetTagNames(
			nondefaultVersionPageAssetTagNames);

		WikiTestUtil.updatePage(
			page, TestPropsValues.getUserId(), StringUtil.randomString(),
			nondefaultVersionPageServiceContext);

		ServiceContext renamePageServiceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		WikiPageLocalServiceUtil.renamePage(
			TestPropsValues.getUserId(), _node.getNodeId(), page.getTitle(),
			"New Title", true, renamePageServiceContext);

		WikiPage renamedPage = WikiPageLocalServiceUtil.getPage(
			_node.getNodeId(), "New Title");

		String[] finalAssetTagNames = AssetTagLocalServiceUtil.getTagNames(
			WikiPage.class.getName(), renamedPage.getResourcePrimKey());

		_assertArrayEquals(
			nondefaultVersionPageAssetTagNames, finalAssetTagNames);
	}

	@Test
	public void testRenamePage() throws Exception {
		WikiPage page = WikiTestUtil.addPage(
			_group.getGroupId(), _node.getNodeId(), true);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		WikiPageLocalServiceUtil.renamePage(
			TestPropsValues.getUserId(), _node.getNodeId(), page.getTitle(),
			"New Title", true, serviceContext);

		WikiPage renamedPage = WikiPageLocalServiceUtil.getPage(
			_node.getNodeId(), "New Title");

		Assert.assertNotNull(renamedPage);

		checkPopulatedServiceContext(serviceContext, renamedPage, false);
	}

	@Test
	public void testRenamePagePermissions() throws Exception {
		WikiPage pageA = WikiTestUtil.addPage(
			TestPropsValues.getUserId(), _group.getGroupId(), _node.getNodeId(),
			"A", true);

		User user = UserTestUtil.addUser();

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		Assert.assertTrue(
			permissionChecker.hasPermission(
				_group.getGroupId(), WikiPage.class.getName(),
				String.valueOf(pageA.getResourcePrimKey()), ActionKeys.VIEW));

		RoleTestUtil.removeResourcePermission(
			RoleConstants.GUEST, WikiPage.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(pageA.getResourcePrimKey()), ActionKeys.VIEW);

		Assert.assertFalse(
			permissionChecker.hasPermission(
				_group.getGroupId(), WikiPage.class.getName(),
				String.valueOf(pageA.getResourcePrimKey()), ActionKeys.VIEW));

		WikiPageLocalServiceUtil.renamePage(
			TestPropsValues.getUserId(), _node.getNodeId(), "A", "B", true,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		WikiPage originalPageA = WikiPageLocalServiceUtil.getPage(
			_node.getNodeId(), "A");

		WikiPage pageB = WikiPageLocalServiceUtil.getPage(
			_node.getNodeId(), "B");

		Assert.assertFalse(
			permissionChecker.hasPermission(
				_group.getGroupId(), WikiPage.class.getName(),
				String.valueOf(pageB.getResourcePrimKey()), ActionKeys.VIEW));

		Assert.assertFalse(
			permissionChecker.hasPermission(
				_group.getGroupId(), WikiPage.class.getName(),
				String.valueOf(originalPageA.getResourcePrimKey()),
				ActionKeys.VIEW));
	}

	@Test(expected = DuplicatePageException.class)
	public void testRenamePageSameName() throws Exception {
		WikiPage page = WikiTestUtil.addPage(
			_group.getGroupId(), _node.getNodeId(), true);

		WikiPageLocalServiceUtil.renamePage(
			TestPropsValues.getUserId(), _node.getNodeId(), page.getTitle(),
			page.getTitle(), true,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@Test
	public void testRenamePageWithExpando() throws Exception {
		WikiPage page = WikiTestUtil.addPage(
			_group.getGroupId(), _node.getNodeId(), true);

		addExpandoValueToPage(page);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		WikiPageLocalServiceUtil.renamePage(
			TestPropsValues.getUserId(), _node.getNodeId(), page.getTitle(),
			"New Title", true, serviceContext);

		WikiPage renamedPage = WikiPageLocalServiceUtil.getPage(
			_node.getNodeId(), "New Title");

		Assert.assertNotNull(renamedPage);

		checkPopulatedServiceContext(serviceContext, renamedPage, true);
	}

	@Test
	public void testRenamePageWithNbspTitle() throws Exception {
		WikiPage page = WikiTestUtil.addPage(
			_group.getGroupId(), _node.getNodeId(), true);

		WikiPageLocalServiceUtil.renamePage(
			TestPropsValues.getUserId(), _node.getNodeId(), page.getTitle(),
			"New" + CharPool.NO_BREAK_SPACE + "Title", true,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		WikiPageLocalServiceUtil.getPage(_node.getNodeId(), "New Title");
	}

	@Test
	public void testRenameRenamedPage() throws Exception {
		WikiTestUtil.addPage(
			TestPropsValues.getUserId(), _group.getGroupId(), _node.getNodeId(),
			"A", true);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		WikiPageLocalServiceUtil.renamePage(
			TestPropsValues.getUserId(), _node.getNodeId(), "A", "B", true,
			serviceContext);

		WikiPageLocalServiceUtil.renamePage(
			TestPropsValues.getUserId(), _node.getNodeId(), "A", "C", true,
			serviceContext);

		WikiPage pageA = WikiPageLocalServiceUtil.getPage(
			_node.getNodeId(), "A");
		WikiPage pageB = WikiPageLocalServiceUtil.getPage(
			_node.getNodeId(), "B");
		WikiPage pageC = WikiPageLocalServiceUtil.getPage(
			_node.getNodeId(), "C");

		Assert.assertEquals("C", pageA.getRedirectTitle());
		Assert.assertEquals(StringPool.BLANK, pageB.getRedirectTitle());
		Assert.assertEquals(StringPool.BLANK, pageC.getRedirectTitle());
		Assert.assertEquals("Renamed as C", pageA.getSummary());
		Assert.assertEquals("Summary", pageB.getSummary());
		Assert.assertEquals(StringPool.BLANK, pageC.getSummary());
		Assert.assertEquals("[[C]]", pageA.getContent());
		Assert.assertEquals("[[B]]", pageC.getContent());
	}

	@Test
	public void testRestorePageFromTrash() throws Exception {
		testRestorePageFromTrash(false);
	}

	@Test
	public void testRestorePageFromTrashWithExpando() throws Exception {
		testRestorePageFromTrash(true);
	}

	@Test
	public void testRevertPage() throws Exception {
		testRevertPage(false);
	}

	@Test
	public void testRevertPageWithExpando() throws Exception {
		testRevertPage(true);
	}

	@Test
	public void testUpdatePagePreservesOriginalOwner() throws Exception {
		WikiPage page = WikiTestUtil.addPage(
			_group.getGroupId(), _node.getNodeId(), true);

		WikiPage updatedPage = WikiPageLocalServiceUtil.updatePage(
			_user.getUserId(), _node.getNodeId(), page.getTitle(),
			page.getVersion(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), false, page.getFormat(), null, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertNotEquals(page.getPageId(), updatedPage.getPageId());
		Assert.assertEquals(page.getUserId(), updatedPage.getUserId());
	}

	protected void addExpandoValueToPage(WikiPage page) throws Exception {
		ExpandoValue value = ExpandoTestUtil.addValue(
			PortalUtil.getClassNameId(WikiPage.class), page.getPrimaryKey(),
			RandomTestUtil.randomString());

		ExpandoBridge expandoBridge = page.getExpandoBridge();

		ExpandoColumn column = value.getColumn();

		expandoBridge.addAttribute(
			column.getName(), ExpandoColumnConstants.STRING, value.getString());
	}

	protected void checkPopulatedServiceContext(
			ServiceContext serviceContext, WikiPage page,
			boolean hasExpandoValues)
		throws Exception {

		long[] assetCategoryIds = AssetCategoryLocalServiceUtil.getCategoryIds(
			WikiPage.class.getName(), page.getResourcePrimKey());

		_assertArrayEquals(
			serviceContext.getAssetCategoryIds(), assetCategoryIds);

		AssetEntry assetEntry = AssetEntryLocalServiceUtil.getEntry(
			WikiPage.class.getName(), page.getResourcePrimKey());

		List<AssetLink> assetLinks = AssetLinkLocalServiceUtil.getLinks(
			assetEntry.getEntryId());

		long[] assetLinkEntryIds = ListUtil.toLongArray(
			assetLinks, AssetLink.ENTRY_ID2_ACCESSOR);

		_assertArrayEquals(
			serviceContext.getAssetLinkEntryIds(), assetLinkEntryIds);

		String[] assetTagNames = AssetTagLocalServiceUtil.getTagNames(
			WikiPage.class.getName(), page.getResourcePrimKey());

		_assertArrayEquals(serviceContext.getAssetTagNames(), assetTagNames);

		if (hasExpandoValues) {
			ExpandoBridge expandoBridge = page.getExpandoBridge();

			AssertUtils.assertEquals(
				expandoBridge.getAttributes(),
				serviceContext.getExpandoBridgeAttributes());
		}
	}

	protected AssetVocabulary getRequiredAssetVocabulary()
		throws PortalException {

		AssetVocabulary assetVocabulary =
			AssetVocabularyLocalServiceUtil.addDefaultVocabulary(
				_group.getGroupId());

		long classNameId = PortalUtil.getClassNameId(WikiPage.class.getName());

		assetVocabulary.setSettings(
			StringBundler.concat(
				"multiValued=true\nrequiredClassNameIds=", classNameId,
				":-1\nselectedClassNameIds=", classNameId, ":-1"));

		return AssetVocabularyLocalServiceUtil.updateAssetVocabulary(
			assetVocabulary);
	}

	protected void testChangeParent(boolean hasExpandoValues) throws Exception {
		WikiPage page = WikiTestUtil.addPage(
			_group.getGroupId(), _node.getNodeId(), true);

		if (hasExpandoValues) {
			addExpandoValueToPage(page);
		}

		WikiPage parentPage = WikiTestUtil.addPage(
			_group.getGroupId(), _node.getNodeId(), true);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		WikiPageLocalServiceUtil.changeParent(
			TestPropsValues.getUserId(), _node.getNodeId(), page.getTitle(),
			parentPage.getTitle(), serviceContext);

		WikiPage retrievedPage = WikiPageLocalServiceUtil.getPage(
			page.getResourcePrimKey());

		checkPopulatedServiceContext(
			serviceContext, retrievedPage, hasExpandoValues);
	}

	protected void testRestorePageFromTrash(boolean hasExpandoValues)
		throws Exception {

		WikiPage page = WikiTestUtil.addPage(
			_group.getGroupId(), _node.getNodeId(), true);

		if (hasExpandoValues) {
			addExpandoValueToPage(page);
		}

		page = WikiPageLocalServiceUtil.movePageToTrash(
			TestPropsValues.getUserId(), _node.getNodeId(), page.getTitle());

		WikiPageLocalServiceUtil.restorePageFromTrash(
			TestPropsValues.getUserId(), page);

		WikiPage restoredPage = WikiPageLocalServiceUtil.getPage(
			page.getResourcePrimKey());

		Assert.assertNotNull(restoredPage);

		if (hasExpandoValues) {
			ExpandoBridge expandoBridge = page.getExpandoBridge();

			ExpandoBridge restoredExpandoBridge =
				restoredPage.getExpandoBridge();

			AssertUtils.assertEquals(
				restoredExpandoBridge.getAttributes(),
				expandoBridge.getAttributes());
		}
	}

	protected void testRevertPage(boolean hasExpandoValues) throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		String originalContent = RandomTestUtil.randomString();

		WikiPage originalPage = WikiTestUtil.addPage(
			TestPropsValues.getUserId(), _node.getNodeId(),
			RandomTestUtil.randomString(), originalContent, true,
			serviceContext);

		if (hasExpandoValues) {
			addExpandoValueToPage(originalPage);
		}

		WikiPage updatedPage1 = WikiTestUtil.updatePage(
			originalPage, TestPropsValues.getUserId(),
			originalContent + "\nAdded second line.", serviceContext);

		Assert.assertNotEquals(originalContent, updatedPage1.getContent());

		WikiPage updatedPage2 = WikiTestUtil.updatePage(
			updatedPage1, TestPropsValues.getUserId(),
			updatedPage1.getContent() + "\nAdded third line.", serviceContext);

		Assert.assertNotEquals(originalContent, updatedPage2.getContent());

		WikiPage revertedPage = WikiPageLocalServiceUtil.revertPage(
			TestPropsValues.getUserId(), _node.getNodeId(),
			updatedPage2.getTitle(), originalPage.getVersion(), serviceContext);

		Assert.assertEquals(revertedPage.getContent(), originalContent);

		checkPopulatedServiceContext(
			serviceContext, revertedPage, hasExpandoValues);
	}

	private void _assertArrayEquals(long[] expectedArray, long[] actualArray) {
		Arrays.sort(expectedArray);
		Arrays.sort(actualArray);

		Assert.assertArrayEquals(expectedArray, actualArray);
	}

	private void _assertArrayEquals(
		String[] expectedArray, String[] actualArray) {

		Arrays.sort(expectedArray);
		Arrays.sort(actualArray);

		Assert.assertArrayEquals(expectedArray, actualArray);
	}

	private WikiPage _createPageWithDraftAttachments() throws Exception {
		WikiPage approvedPage = WikiTestUtil.addPage(
			_group.getGroupId(), _node.getNodeId(), true);

		WikiTestUtil.addPageAttachment(
			approvedPage.getUserId(), approvedPage.getNodeId(),
			approvedPage.getTitle(), getClass());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(approvedPage.getGroupId());

		// Update the page so the modifiedDate comes after attachment
		// modifiedDate to resemble the /wiki/edit_page action

		approvedPage = WikiTestUtil.updatePage(
			approvedPage, approvedPage.getUserId(), approvedPage.getTitle(),
			approvedPage.getContent(), true, serviceContext);

		WikiTestUtil.addPageAttachment(
			approvedPage.getUserId(), approvedPage.getNodeId(),
			approvedPage.getTitle(), getClass());

		WikiTestUtil.updatePage(
			approvedPage, approvedPage.getUserId(), approvedPage.getTitle(),
			approvedPage.getContent(), false, serviceContext);

		return approvedPage;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		WikiPageLocalServiceTest.class);

	@DeleteAfterTestRun
	private Group _group;

	private WikiNode _node;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private WikiPageLocalService _wikiPageLocalService;

	private static class AssetCategoryTestException extends PortalException {
	}

}