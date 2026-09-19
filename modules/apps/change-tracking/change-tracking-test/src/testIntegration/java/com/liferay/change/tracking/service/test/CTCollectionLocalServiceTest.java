/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.change.tracking.conflict.ConflictInfo;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTCollectionService;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.change.tracking.service.CTProcessLocalService;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalFolderLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Preston Crary
 */
@RunWith(Arquillian.class)
public class CTCollectionLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_ctCollection1 = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, CTCollectionLocalServiceTest.class.getSimpleName(), null);

		_ctCollections.add(_ctCollection1);

		_group = GroupTestUtil.addGroup();
		_journalArticleClassNameId = _classNameLocalService.getClassNameId(
			JournalArticle.class);
		_journalFolderClassNameId = _classNameLocalService.getClassNameId(
			JournalFolder.class);
		_layoutClassNameId = _classNameLocalService.getClassNameId(
			Layout.class);
	}

	@Test
	public void testCheckConflictsWithJournalArticles() throws Exception {
		Map<Long, List<ConflictInfo>> conflictInfoMap =
			_ctCollectionLocalService.checkConflicts(_ctCollection1);

		Assert.assertTrue(
			conflictInfoMap.toString(), conflictInfoMap.isEmpty());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setCommand(Constants.ADD);
		serviceContext.setLayoutFullURL("http://localhost");

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, StringPool.BLANK,
			true,
			HashMapBuilder.put(
				LocaleUtil.getSiteDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getSiteDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getSiteDefault(), RandomTestUtil.randomString()
			).build(),
			null, LocaleUtil.getSiteDefault(), null, false, false,
			serviceContext);

		JournalArticle ctJournalArticle1 = null;
		JournalArticle ctJournalArticle2 = null;

		serviceContext.setScopeGroupId(_group.getGroupId());

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection1.getCtCollectionId())) {

			ctJournalArticle1 = _journalArticleLocalService.updateArticle(
				journalArticle.getUserId(), journalArticle.getGroupId(),
				journalArticle.getFolderId(), journalArticle.getArticleId(),
				journalArticle.getVersion(), journalArticle.getContent(),
				serviceContext);

			ctJournalArticle2 = _journalArticleLocalService.updateArticle(
				ctJournalArticle1.getUserId(), ctJournalArticle1.getGroupId(),
				ctJournalArticle1.getFolderId(),
				ctJournalArticle1.getArticleId(),
				ctJournalArticle1.getVersion(), ctJournalArticle1.getContent(),
				serviceContext);
		}

		JournalArticle productionJournalArticle1 =
			_journalArticleLocalService.updateArticle(
				journalArticle.getUserId(), journalArticle.getGroupId(),
				journalArticle.getFolderId(), journalArticle.getArticleId(),
				journalArticle.getVersion(), journalArticle.getContent(),
				serviceContext);

		JournalArticle productionJournalArticle2 =
			_journalArticleLocalService.updateArticle(
				productionJournalArticle1.getUserId(),
				productionJournalArticle1.getGroupId(),
				productionJournalArticle1.getFolderId(),
				productionJournalArticle1.getArticleId(),
				productionJournalArticle1.getVersion(),
				productionJournalArticle1.getContent(), serviceContext);

		Assert.assertNotEquals(productionJournalArticle1, ctJournalArticle1);

		Assert.assertNotEquals(productionJournalArticle2, ctJournalArticle2);

		Assert.assertEquals(1.0, journalArticle.getVersion(), 0.01);

		Assert.assertEquals(1.1, productionJournalArticle1.getVersion(), 0.01);
		Assert.assertEquals(1.2, productionJournalArticle2.getVersion(), 0.01);

		Assert.assertEquals(1.1, ctJournalArticle1.getVersion(), 0.01);
		Assert.assertEquals(1.2, ctJournalArticle2.getVersion(), 0.01);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection1.getCtCollectionId())) {

			List<JournalArticle> journalArticles =
				_journalArticleLocalService.getArticlesByResourcePrimKey(
					journalArticle.getResourcePrimKey());

			Assert.assertEquals(
				journalArticles.toString(), 3, journalArticles.size());

			Assert.assertEquals(ctJournalArticle2, journalArticles.get(0));
			Assert.assertEquals(ctJournalArticle1, journalArticles.get(1));
			Assert.assertEquals(journalArticle, journalArticles.get(2));
		}

		List<JournalArticle> journalArticles =
			_journalArticleLocalService.getArticlesByResourcePrimKey(
				journalArticle.getResourcePrimKey());

		Assert.assertEquals(
			journalArticles.toString(), 3, journalArticles.size());

		Assert.assertEquals(productionJournalArticle2, journalArticles.get(0));
		Assert.assertEquals(productionJournalArticle1, journalArticles.get(1));
		Assert.assertEquals(journalArticle, journalArticles.get(2));

		conflictInfoMap = _ctCollectionLocalService.checkConflicts(
			_ctCollection1);

		List<ConflictInfo> conflictInfos = conflictInfoMap.remove(
			_journalArticleClassNameId);

		Assert.assertEquals(conflictInfos.toString(), 2, conflictInfos.size());

		conflictInfos.sort(
			Comparator.comparing(ConflictInfo::getTargetPrimaryKey));

		ConflictInfo conflictInfo = conflictInfos.get(0);

		Assert.assertTrue(conflictInfo.isResolved());
		Assert.assertEquals(
			productionJournalArticle1.getPrimaryKey(),
			conflictInfo.getTargetPrimaryKey());
		Assert.assertEquals(
			ctJournalArticle1.getPrimaryKey(),
			conflictInfo.getSourcePrimaryKey());

		conflictInfo = conflictInfos.get(1);

		Assert.assertTrue(conflictInfo.isResolved());
		Assert.assertEquals(
			productionJournalArticle2.getPrimaryKey(),
			conflictInfo.getTargetPrimaryKey());
		Assert.assertEquals(
			ctJournalArticle2.getPrimaryKey(),
			conflictInfo.getSourcePrimaryKey());

		conflictInfos = conflictInfoMap.remove(
			_classNameLocalService.getClassNameId(AssetEntry.class));

		Assert.assertTrue(
			conflictInfoMap.toString(), conflictInfoMap.isEmpty());

		conflictInfo = conflictInfos.get(0);

		Assert.assertTrue(conflictInfo.isResolved());
	}

	@Test
	public void testCheckConflictsWithJournalFolders() throws Exception {
		Map<Long, List<ConflictInfo>> conflictInfoMap =
			_ctCollectionLocalService.checkConflicts(_ctCollection1);

		Assert.assertTrue(
			conflictInfoMap.toString(), conflictInfoMap.isEmpty());

		String conflictingFolderName = "conflictingFolderName";

		JournalFolder ctJournalFolder = null;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection1.getCtCollectionId())) {

			ctJournalFolder = JournalTestUtil.addFolder(
				_group.getGroupId(), conflictingFolderName);
		}

		JournalFolder productionJournalFolder = JournalTestUtil.addFolder(
			_group.getGroupId(), conflictingFolderName);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection1.getCtCollectionId())) {

			List<JournalFolder> journalFolders =
				_journalFolderLocalService.getFolders(
					_group.getGroupId(),
					JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

			Assert.assertEquals(
				journalFolders.toString(), 1, journalFolders.size());

			Assert.assertEquals(ctJournalFolder, journalFolders.get(0));
		}

		List<JournalFolder> journalFolders =
			_journalFolderLocalService.getFolders(
				_group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		Assert.assertEquals(
			journalFolders.toString(), 1, journalFolders.size());

		Assert.assertEquals(productionJournalFolder, journalFolders.get(0));

		conflictInfoMap = _ctCollectionLocalService.checkConflicts(
			_ctCollection1);

		Assert.assertEquals(
			conflictInfoMap.toString(), 1, conflictInfoMap.size());

		List<ConflictInfo> conflictInfos = conflictInfoMap.remove(
			_journalFolderClassNameId);

		Assert.assertTrue(
			conflictInfoMap.toString(), conflictInfoMap.isEmpty());

		Assert.assertEquals(conflictInfos.toString(), 1, conflictInfos.size());

		ConflictInfo conflictInfo = conflictInfos.get(0);

		Assert.assertFalse(conflictInfo.isResolved());
		Assert.assertEquals(
			productionJournalFolder.getPrimaryKey(),
			conflictInfo.getTargetPrimaryKey());
		Assert.assertEquals(
			ctJournalFolder.getPrimaryKey(),
			conflictInfo.getSourcePrimaryKey());
	}

	@Test
	public void testCheckConflictsWithPublishedPublication() throws Exception {
		JournalFolder journalFolder = JournalTestUtil.addFolder(
			_group.getGroupId(), RandomTestUtil.randomString());

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection1.getCtCollectionId())) {

			journalFolder.setDescription(RandomTestUtil.randomString());

			journalFolder = _journalFolderLocalService.updateJournalFolder(
				journalFolder);
		}

		_ctCollectionService.publishCTCollection(
			TestPropsValues.getUserId(), _ctCollection1.getCtCollectionId());

		_ctCollection2 = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, CTCollectionLocalServiceTest.class.getSimpleName(), null);

		_ctCollections.add(_ctCollection2);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection2.getCtCollectionId())) {

			_journalFolderLocalService.deleteFolder(journalFolder);
		}

		Map<Long, List<ConflictInfo>> conflictInfoMap =
			_ctCollectionLocalService.checkConflicts(_ctCollection2);

		Assert.assertTrue(conflictInfoMap.isEmpty());
	}

	@Test
	public void testDeletePredeletedLayout() throws Exception {
		Layout layout1 = LayoutTestUtil.addTypePortletLayout(_group);

		Layout layout2 = LayoutTestUtil.addTypePortletLayout(_group);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection1.getCtCollectionId())) {

			_layoutLocalService.deleteLayout(layout1);

			Assert.assertNull(
				_layoutLocalService.fetchLayout(layout1.getPlid()));

			_layoutLocalService.deleteLayout(layout2);

			Assert.assertNull(
				_layoutLocalService.fetchLayout(layout2.getPlid()));
		}

		_layoutLocalService.deleteLayout(layout1.getPlid());

		Assert.assertNull(_layoutLocalService.fetchLayout(layout1.getPlid()));

		_ctProcessLocalService.addCTProcess(
			_ctCollection1.getUserId(), _ctCollection1.getCtCollectionId());

		_ctCollection2 = _ctCollectionLocalService.undoCTCollection(
			_ctCollection1.getCtCollectionId(), _ctCollection1.getUserId(),
			_ctCollection1.getName() + " (undo)", StringPool.BLANK);

		_ctCollections.add(_ctCollection2);

		_ctProcessLocalService.addCTProcess(
			_ctCollection2.getUserId(), _ctCollection2.getCtCollectionId());

		Assert.assertNull(_layoutLocalService.fetchLayout(layout1.getPlid()));

		Assert.assertEquals(
			layout2, _layoutLocalService.fetchLayout(layout2.getPlid()));
	}

	@Test
	public void testDeletePredeletedLayoutWithTwoCollections()
		throws Exception {

		Layout layout1 = LayoutTestUtil.addTypePortletLayout(_group);

		Layout layout2 = LayoutTestUtil.addTypePortletLayout(_group);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection1.getCtCollectionId())) {

			_layoutLocalService.deleteLayout(layout1);

			Assert.assertNull(
				_layoutLocalService.fetchLayout(layout1.getPlid()));

			_layoutLocalService.deleteLayout(layout2);

			Assert.assertNull(
				_layoutLocalService.fetchLayout(layout2.getPlid()));
		}

		Assert.assertEquals(
			layout1, _layoutLocalService.getLayout(layout1.getPlid()));
		Assert.assertEquals(
			layout2, _layoutLocalService.getLayout(layout2.getPlid()));

		_ctCollection2 = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, StringUtil.randomString(), StringUtil.randomString());

		_ctCollections.add(_ctCollection2);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection2.getCtCollectionId())) {

			_layoutLocalService.deleteLayout(layout1);

			Assert.assertNull(
				_layoutLocalService.fetchLayout(layout1.getPlid()));
		}

		Assert.assertEquals(
			layout1, _layoutLocalService.getLayout(layout1.getPlid()));

		_ctProcessLocalService.addCTProcess(
			_ctCollection1.getUserId(), _ctCollection1.getCtCollectionId());

		Assert.assertNull(_layoutLocalService.fetchLayout(layout1.getPlid()));
		Assert.assertNull(_layoutLocalService.fetchLayout(layout2.getPlid()));

		_ctProcessLocalService.addCTProcess(
			_ctCollection2.getUserId(), _ctCollection2.getCtCollectionId());

		_ctCollection3 = _ctCollectionLocalService.undoCTCollection(
			_ctCollection1.getCtCollectionId(), _ctCollection1.getUserId(),
			_ctCollection1.getName() + " (undo)", StringPool.BLANK);

		_ctCollections.add(_ctCollection3);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection3.getCtCollectionId())) {

			Assert.assertEquals(
				layout1, _layoutLocalService.getLayout(layout1.getPlid()));
			Assert.assertEquals(
				layout2, _layoutLocalService.getLayout(layout2.getPlid()));
		}

		_ctCollection4 = _ctCollectionLocalService.undoCTCollection(
			_ctCollection2.getCtCollectionId(), _ctCollection2.getUserId(),
			_ctCollection2.getName() + " (undo)", StringPool.BLANK);

		_ctCollections.add(_ctCollection4);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection4.getCtCollectionId())) {

			Assert.assertNull(
				_layoutLocalService.fetchLayout(layout1.getPlid()));
			Assert.assertNull(
				_layoutLocalService.fetchLayout(layout2.getPlid()));
		}

		_ctProcessLocalService.addCTProcess(
			_ctCollection3.getUserId(), _ctCollection3.getCtCollectionId());

		Assert.assertEquals(
			layout1, _layoutLocalService.getLayout(layout1.getPlid()));
		Assert.assertEquals(
			layout2, _layoutLocalService.getLayout(layout2.getPlid()));

		Map<Long, List<ConflictInfo>> conflictInfosMap =
			_ctCollectionLocalService.checkConflicts(_ctCollection4);

		Assert.assertFalse(conflictInfosMap.isEmpty());
	}

	@Test
	public void testDiscardCTEntry() throws Exception {
		WorkflowDefinitionLink workflowDefinitionLink =
			_workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
				TestPropsValues.getUserId(), TestPropsValues.getCompanyId(),
				_group.getGroupId(), JournalFolder.class.getName(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				JournalArticleConstants.DDM_STRUCTURE_ID_ALL, "Single Approver",
				1);

		try {
			JournalArticle journalArticle;

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						_ctCollection1.getCtCollectionId())) {

				journalArticle = JournalTestUtil.addArticleWithWorkflow(
					_group.getGroupId(),
					JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, true);

				Assert.assertEquals(
					WorkflowConstants.STATUS_PENDING,
					journalArticle.getStatus());

				Assert.assertNotNull(
					_workflowInstanceLinkLocalService.fetchWorkflowInstanceLink(
						TestPropsValues.getCompanyId(), _group.getGroupId(),
						JournalArticle.class.getName(),
						journalArticle.getId()));
			}

			_ctCollectionLocalService.discardCTEntry(
				_ctCollection1.getCtCollectionId(), _journalArticleClassNameId,
				journalArticle.getId(), false);

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						_ctCollection1.getCtCollectionId())) {

				Assert.assertNull(
					_workflowInstanceLinkLocalService.fetchWorkflowInstanceLink(
						TestPropsValues.getCompanyId(), _group.getGroupId(),
						JournalArticle.class.getName(),
						journalArticle.getId()));
			}
		}
		finally {
			_workflowDefinitionLinkLocalService.deleteWorkflowDefinitionLink(
				workflowDefinitionLink);
		}
	}

	@Test
	public void testMoveCTEntryFromExpiredCTCollection() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, serviceContext);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection1.getCtCollectionId())) {

			journalArticle = _journalArticleLocalService.updateArticle(
				TestPropsValues.getUserId(), _group.getGroupId(),
				journalArticle.getFolderId(), journalArticle.getArticleId(),
				journalArticle.getVersion(), journalArticle.getContent(),
				serviceContext);
		}

		_ctCollection1.setStatus(WorkflowConstants.STATUS_EXPIRED);

		_ctCollection1 = _ctCollectionLocalService.updateCTCollection(
			_ctCollection1);

		_ctCollection2 = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), null);

		_ctCollections.add(_ctCollection2);

		_ctCollectionService.moveCTEntry(
			_ctCollection1.getCtCollectionId(),
			_ctCollection2.getCtCollectionId(),
			_classNameLocalService.getClassNameId(JournalArticle.class),
			journalArticle.getId());

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection2.getCtCollectionId())) {

			journalArticle = _journalArticleLocalService.getArticle(
				journalArticle.getId());
		}

		Assert.assertEquals(
			_ctCollection2.getCtCollectionId(),
			journalArticle.getCtCollectionId());
	}

	@Test
	public void testRelatedCTEntriesMapWithConflictedCTEntries()
		throws Exception {

		JournalArticle journalArticle1 = null;
		JournalArticle journalArticle2 = null;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection1.getCtCollectionId())) {

			journalArticle1 = JournalTestUtil.addArticle(
				_group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

			journalArticle2 = JournalTestUtil.updateArticle(journalArticle1);
		}

		CTEntry ctEntry = _ctEntryLocalService.fetchCTEntry(
			_ctCollection1.getCtCollectionId(), _journalArticleClassNameId,
			journalArticle2.getId());

		Assert.assertNotNull(ctEntry);

		List<CTEntry> relatedCTEntries =
			_ctCollectionLocalService.getRelatedCTEntries(
				_ctCollection1.getCtCollectionId(),
				new long[] {ctEntry.getCtEntryId()});

		int count = relatedCTEntries.size();

		_ctCollectionLocalService.discardCTEntry(
			_ctCollection1.getCtCollectionId(), _journalArticleClassNameId,
			journalArticle2.getId(), false);

		ctEntry = _ctEntryLocalService.fetchCTEntry(
			_ctCollection1.getCtCollectionId(), _journalArticleClassNameId,
			journalArticle1.getId());

		Assert.assertNotNull(ctEntry);

		relatedCTEntries = _ctCollectionLocalService.getRelatedCTEntries(
			_ctCollection1.getCtCollectionId(),
			new long[] {ctEntry.getCtEntryId()});

		Assert.assertTrue(count < relatedCTEntries.size());
	}

	@Test
	public void testUndoCTCollection() throws Exception {
		Layout addedLayout = null;

		Layout deletedLayout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout modifiedLayout = LayoutTestUtil.addTypeContentLayout(_group);

		String tagName1 = "layoutcttesttag1";
		String tagName2 = "layoutcttesttag2";

		_layoutLocalService.updateAsset(
			modifiedLayout.getUserId(), modifiedLayout, null,
			new String[] {tagName1});

		String originalFriendlyURL = modifiedLayout.getFriendlyURL();

		String newFriendlyURL = "/testModifyLayout";

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection1.getCtCollectionId())) {

			addedLayout = LayoutTestUtil.addTypePortletLayout(_group);

			_layoutLocalService.deleteLayout(deletedLayout);

			modifiedLayout.setFriendlyURL(newFriendlyURL);

			modifiedLayout = _layoutLocalService.updateLayout(modifiedLayout);

			_layoutLocalService.updateAsset(
				modifiedLayout.getUserId(), modifiedLayout, null,
				new String[] {tagName2});
		}

		_ctProcessLocalService.addCTProcess(
			_ctCollection1.getUserId(), _ctCollection1.getCtCollectionId());

		AssetEntry assetEntry = _assetEntryLocalService.getEntry(
			Layout.class.getName(), modifiedLayout.getPlid());

		List<AssetTag> assetTags = _assetTagLocalService.getEntryTags(
			assetEntry.getEntryId());

		Assert.assertEquals(assetTags.toString(), 1, assetTags.size());

		AssetTag assetTag = assetTags.get(0);

		Assert.assertEquals(tagName2, assetTag.getName());
		Assert.assertEquals(1, assetTag.getAssetCount());

		Assert.assertEquals(
			addedLayout,
			_layoutLocalService.fetchLayout(addedLayout.getPlid()));

		Assert.assertNull(
			_layoutLocalService.fetchLayout(deletedLayout.getPlid()));

		modifiedLayout = _layoutLocalService.fetchLayout(
			modifiedLayout.getPlid());

		Assert.assertEquals(newFriendlyURL, modifiedLayout.getFriendlyURL());

		_ctCollection2 = _ctCollectionLocalService.undoCTCollection(
			_ctCollection1.getCtCollectionId(), _ctCollection1.getUserId(),
			_ctCollection1.getName() + " (undo)", StringPool.BLANK);

		_ctCollections.add(_ctCollection2);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection2.getCtCollectionId())) {

			Assert.assertNull(
				_layoutLocalService.fetchLayout(addedLayout.getPlid()));

			Assert.assertEquals(
				deletedLayout,
				_layoutLocalService.fetchLayout(deletedLayout.getPlid()));

			modifiedLayout = _layoutLocalService.fetchLayout(
				modifiedLayout.getPlid());

			Assert.assertEquals(
				originalFriendlyURL, modifiedLayout.getFriendlyURL());

			assetTags = _assetTagLocalService.getEntryTags(
				assetEntry.getEntryId());

			Assert.assertEquals(assetTags.toString(), 1, assetTags.size());

			assetTag = assetTags.get(0);

			Assert.assertEquals(tagName1, assetTag.getName());
			Assert.assertEquals(1, assetTag.getAssetCount());
		}

		_ctProcessLocalService.addCTProcess(
			_ctCollection2.getUserId(), _ctCollection2.getCtCollectionId());

		Assert.assertNull(
			_layoutLocalService.fetchLayout(addedLayout.getPlid()));

		Assert.assertEquals(
			deletedLayout,
			_layoutLocalService.fetchLayout(deletedLayout.getPlid()));

		modifiedLayout = _layoutLocalService.fetchLayout(
			modifiedLayout.getPlid());

		Assert.assertEquals(
			originalFriendlyURL, modifiedLayout.getFriendlyURL());
	}

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private AssetTagLocalService _assetTagLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	private CTCollection _ctCollection1;
	private CTCollection _ctCollection2;
	private CTCollection _ctCollection3;
	private CTCollection _ctCollection4;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private CTCollectionService _ctCollectionService;

	@DeleteAfterTestRun
	private final List<CTCollection> _ctCollections = new ArrayList<>();

	@Inject
	private CTEntryLocalService _ctEntryLocalService;

	@Inject
	private CTProcessLocalService _ctProcessLocalService;

	private Group _group;
	private long _journalArticleClassNameId;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	private long _journalFolderClassNameId;

	@Inject
	private JournalFolderLocalService _journalFolderLocalService;

	private long _layoutClassNameId;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@Inject
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

}