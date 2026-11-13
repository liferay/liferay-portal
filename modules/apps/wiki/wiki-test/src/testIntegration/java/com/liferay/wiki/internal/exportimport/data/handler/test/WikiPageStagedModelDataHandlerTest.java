/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.exception.NoSuchFolderException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.exportimport.kernel.lar.ExportImportClassedModelUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerRegistryUtil;
import com.liferay.exportimport.test.util.lar.BaseWorkflowedStagedModelDataHandlerTestCase;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.RepositoryLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.ratings.kernel.model.RatingsEntry;
import com.liferay.ratings.kernel.service.RatingsEntryLocalService;
import com.liferay.wiki.attachments.test.WikiAttachmentsTest;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiNodeLocalService;
import com.liferay.wiki.service.WikiNodeLocalServiceUtil;
import com.liferay.wiki.service.WikiPageLocalServiceUtil;
import com.liferay.wiki.service.WikiPageServiceUtil;
import com.liferay.wiki.test.util.WikiTestUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Zsolt Berentey
 */
@RunWith(Arquillian.class)
public class WikiPageStagedModelDataHandlerTest
	extends BaseWorkflowedStagedModelDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testDeleteAttachmentsFileEntry() throws Exception {
		Map<String, List<StagedModel>> dependentStagedModelsMap =
			addDependentStagedModelsMap(stagingGroup);

		WikiPage wikiPage = (WikiPage)addStagedModel(
			stagingGroup, dependentStagedModelsMap);

		exportImportStagedModel(wikiPage);

		WikiPage importedWikiPage = (WikiPage)getStagedModel(
			wikiPage.getUuid(), liveGroup);

		Assert.assertEquals(
			1, importedWikiPage.getAttachmentsFileEntriesCount());

		List<FileEntry> attachmentsFileEntries =
			wikiPage.getAttachmentsFileEntries();

		FileEntry attachmentFileEntry = attachmentsFileEntries.get(0);

		WikiPageServiceUtil.movePageAttachmentToTrash(
			wikiPage.getNodeId(), wikiPage.getTitle(),
			attachmentFileEntry.getFileName());

		exportImportStagedModel(wikiPage);

		importedWikiPage = (WikiPage)getStagedModel(
			wikiPage.getUuid(), liveGroup);

		Assert.assertEquals(
			0, importedWikiPage.getAttachmentsFileEntriesCount());
	}

	@Test
	public void testUpdateAttachmentsWithFrontendWikiPage() throws Exception {
		Map<String, List<StagedModel>> dependentStagedModelsMap =
			addDependentStagedModelsMap(stagingGroup);

		WikiPage wikiPage = (WikiPage)addStagedModel(
			stagingGroup, dependentStagedModelsMap, "FrontPage");

		exportImportStagedModel(
			WikiPageLocalServiceUtil.getWikiPage(wikiPage.getPageId()));

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(wikiPage.getGroupId());

		serviceContext.setCommand(Constants.UPDATE);
		serviceContext.setLayoutFullURL("http://localhost");

		WikiPage updatedWikiPage = WikiTestUtil.updatePage(
			wikiPage, wikiPage.getUserId(), RandomTestUtil.randomString(),
			serviceContext);

		exportImportStagedModel(updatedWikiPage);

		exportImportStagedModel(
			WikiPageLocalServiceUtil.getWikiPage(wikiPage.getPageId()));

		WikiPage importedWikiPage = (WikiPage)getStagedModel(
			wikiPage.getUuid(), liveGroup);

		Assert.assertEquals(
			1, importedWikiPage.getAttachmentsFileEntriesCount());
	}

	@Test
	@TestInfo("LPD-85488")
	public void testWikiNodePreservesLastPostDateOnImport() throws Exception {
		Map<String, List<StagedModel>> dependentStagedModelsMap =
			addDependentStagedModelsMap(stagingGroup);

		List<StagedModel> dependentStagedModels = dependentStagedModelsMap.get(
			WikiNode.class.getSimpleName());

		WikiNode wikiNode1 = (WikiNode)dependentStagedModels.get(0);

		WikiPage wikiPage = WikiTestUtil.addPage(
			TestPropsValues.getUserId(), wikiNode1.getNodeId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), true,
			ServiceContextTestUtil.getServiceContext(
				stagingGroup.getGroupId()));

		wikiNode1 = _wikiNodeLocalService.getWikiNode(wikiNode1.getNodeId());

		try {
			ExportImportThreadLocal.setLayoutImportInProcess(true);

			exportImportStagedModel(wikiPage);
		}
		finally {
			ExportImportThreadLocal.setLayoutImportInProcess(false);
		}

		WikiNode wikiNode2 =
			WikiNodeLocalServiceUtil.getWikiNodeByUuidAndGroupId(
				wikiNode1.getUuid(), liveGroup.getGroupId());

		Assert.assertEquals(
			wikiNode1.getLastPostDate(), wikiNode2.getLastPostDate());
	}

	@Override
	protected Map<String, List<StagedModel>> addDefaultDependentStagedModelsMap(
			Group group)
		throws Exception {

		Map<String, List<StagedModel>> dependentStagedModelsMap =
			new HashMap<>();

		WikiNode node = WikiTestUtil.addDefaultNode(group.getGroupId());

		addDependentStagedModel(dependentStagedModelsMap, WikiNode.class, node);

		return dependentStagedModelsMap;
	}

	@Override
	protected StagedModel addDefaultStagedModel(
			Group group,
			Map<String, List<StagedModel>> dependentStagedModelsMap)
		throws Exception {

		return addStagedModel(group, dependentStagedModelsMap, "Front Page");
	}

	@Override
	protected Map<String, List<StagedModel>> addDependentStagedModelsMap(
			Group group)
		throws Exception {

		Map<String, List<StagedModel>> dependentStagedModelsMap =
			new HashMap<>();

		WikiNode node = WikiTestUtil.addNode(group.getGroupId());

		addDependentStagedModel(dependentStagedModelsMap, WikiNode.class, node);

		return dependentStagedModelsMap;
	}

	@Override
	protected StagedModel addStagedModel(
			Group group,
			Map<String, List<StagedModel>> dependentStagedModelsMap)
		throws Exception {

		return addStagedModel(
			group, dependentStagedModelsMap, RandomTestUtil.randomString());
	}

	protected StagedModel addStagedModel(
			Group group,
			Map<String, List<StagedModel>> dependentStagedModelsMap,
			String name)
		throws Exception {

		List<StagedModel> dependentStagedModels = dependentStagedModelsMap.get(
			WikiNode.class.getSimpleName());

		WikiNode node = (WikiNode)dependentStagedModels.get(0);

		WikiPage page = WikiTestUtil.addPage(
			TestPropsValues.getUserId(), node.getNodeId(), name,
			RandomTestUtil.randomString(), true,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		WikiTestUtil.addPageAttachment(
			TestPropsValues.getUserId(), node.getNodeId(), page.getTitle(),
			WikiAttachmentsTest.class);

		List<FileEntry> attachmentsFileEntries =
			page.getAttachmentsFileEntries();

		FileEntry fileEntry = attachmentsFileEntries.get(0);

		Folder folder = fileEntry.getFolder();

		while (folder != null) {
			addDependentStagedModel(
				dependentStagedModelsMap, DLFolder.class, folder);

			folder = folder.getParentFolder();
		}

		addDependentStagedModel(
			dependentStagedModelsMap, DLFileEntry.class,
			attachmentsFileEntries.get(0));
		addDependentStagedModel(
			dependentStagedModelsMap, Repository.class,
			RepositoryLocalServiceUtil.getRepository(
				fileEntry.getRepositoryId()));

		return page;
	}

	@Override
	protected List<StagedModel> addWorkflowedStagedModels(Group group)
		throws Exception {

		List<StagedModel> stagedModels = new ArrayList<>();

		WikiNode node = WikiTestUtil.addNode(group.getGroupId());

		WikiPage page = WikiTestUtil.addPage(
			group.getGroupId(), node.getNodeId(), true);

		stagedModels.add(page);

		WikiPage draftPage = WikiTestUtil.addPage(
			group.getGroupId(), node.getNodeId(), false);

		stagedModels.add(draftPage);

		return stagedModels;
	}

	@Override
	protected void deleteStagedModel(
			StagedModel stagedModel,
			Map<String, List<StagedModel>> dependentStagedModelsMap,
			Group group)
		throws Exception {

		StagedModelDataHandler<StagedModel> stagedModelDataHandler =
			(StagedModelDataHandler<StagedModel>)
				StagedModelDataHandlerRegistryUtil.getStagedModelDataHandler(
					ExportImportClassedModelUtil.getClassName(stagedModel));

		stagedModelDataHandler.deleteStagedModel(stagedModel);

		for (List<StagedModel> dependentStagedModels :
				dependentStagedModelsMap.values()) {

			for (StagedModel dependentStagedModel : dependentStagedModels) {
				try {
					stagedModelDataHandler =
						(StagedModelDataHandler<StagedModel>)
							StagedModelDataHandlerRegistryUtil.
								getStagedModelDataHandler(
									ExportImportClassedModelUtil.getClassName(
										dependentStagedModel));

					stagedModelDataHandler.deleteStagedModel(
						dependentStagedModel);
				}
				catch (NoSuchModelException noSuchModelException) {
					if (!(noSuchModelException instanceof
							NoSuchFileEntryException) &&
						!(noSuchModelException instanceof
							NoSuchFolderException)) {

						throw noSuchModelException;
					}
				}
			}
		}
	}

	@Override
	protected StagedModel getStagedModel(String uuid, Group group)
		throws PortalException {

		return WikiPageLocalServiceUtil.getWikiPageByUuidAndGroupId(
			uuid, group.getGroupId());
	}

	@Override
	protected Class<? extends StagedModel> getStagedModelClass() {
		return WikiPage.class;
	}

	@Override
	protected boolean isCommentableStagedModel() {
		return true;
	}

	@Override
	protected void validateImport(
			Map<String, List<StagedModel>> dependentStagedModelsMap,
			Group group)
		throws Exception {

		List<StagedModel> dependentStagedModels = dependentStagedModelsMap.get(
			WikiNode.class.getSimpleName());

		Assert.assertEquals(
			dependentStagedModels.toString(), 1, dependentStagedModels.size());

		WikiNode node = (WikiNode)dependentStagedModels.get(0);

		WikiNodeLocalServiceUtil.getWikiNodeByUuidAndGroupId(
			node.getUuid(), group.getGroupId());
	}

	@Override
	protected void validateImport(
			StagedModel stagedModel, StagedModelAssets stagedModelAssets,
			Map<String, List<StagedModel>> dependentStagedModelsMap,
			Group group)
		throws Exception {

		super.validateImport(
			stagedModel, stagedModelAssets, dependentStagedModelsMap, group);

		WikiPage page = (WikiPage)stagedModel;

		List<FileEntry> attachmentsFileEntries =
			page.getAttachmentsFileEntries();

		Assert.assertEquals(
			attachmentsFileEntries.toString(), 1,
			attachmentsFileEntries.size());

		WikiPage importedPage = (WikiPage)getStagedModel(page.getUuid(), group);

		RatingsEntry importedRatingsEntry =
			_ratingsEntryLocalService.fetchEntry(
				TestPropsValues.getUserId(), WikiPage.class.getName(),
				importedPage.getResourcePrimKey());

		RatingsEntry ratingsEntry = _ratingsEntryLocalService.getEntry(
			TestPropsValues.getUserId(), WikiPage.class.getName(),
			page.getResourcePrimKey());

		Assert.assertEquals(
			ratingsEntry.getScore(), importedRatingsEntry.getScore(), 0);

		validateImport(dependentStagedModelsMap, group);
	}

	@Override
	protected void validateImportedStagedModel(
			StagedModel stagedModel, StagedModel importedStagedModel)
		throws Exception {

		super.validateImportedStagedModel(stagedModel, importedStagedModel);

		WikiPage page = (WikiPage)stagedModel;
		WikiPage importedPage = (WikiPage)importedStagedModel;

		Assert.assertEquals(
			page.getExternalReferenceCode(),
			importedPage.getExternalReferenceCode());
		Assert.assertEquals(page.getTitle(), importedPage.getTitle());
		Assert.assertEquals(page.getVersion(), importedPage.getVersion(), 0L);
		Assert.assertEquals(page.isMinorEdit(), importedPage.isMinorEdit());
		Assert.assertEquals(page.getSummary(), importedPage.getSummary());
		Assert.assertEquals(page.getFormat(), importedPage.getFormat());
		Assert.assertEquals(page.isHead(), importedPage.isHead());
		Assert.assertEquals(
			page.getParentTitle(), importedPage.getParentTitle());
		Assert.assertEquals(
			page.getRedirectTitle(), importedPage.getRedirectTitle());
	}

	@Inject
	private RatingsEntryLocalService _ratingsEntryLocalService;

	@Inject
	private WikiNodeLocalService _wikiNodeLocalService;

}