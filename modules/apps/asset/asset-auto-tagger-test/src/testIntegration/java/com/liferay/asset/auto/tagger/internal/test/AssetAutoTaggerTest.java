/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.auto.tagger.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.auto.tagger.AssetAutoTagger;
import com.liferay.asset.auto.tagger.model.AssetAutoTaggerEntry;
import com.liferay.asset.auto.tagger.service.AssetAutoTaggerEntryLocalService;
import com.liferay.asset.auto.tagger.test.BaseAssetAutoTaggerTestCase;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
@Sync
public class AssetAutoTaggerTest extends BaseAssetAutoTaggerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			SynchronousDestinationTestRule.INSTANCE);

	@Test
	public void testAutoTagsANewAssetOnCreation() throws Exception {
		AssetEntry assetEntry = addFileEntryAssetEntry(
			ServiceContextTestUtil.getServiceContext(group.getGroupId(), 0));

		assertContainsAssetTagName(assetEntry, ASSET_TAG_NAME_AUTO);
	}

	@Test
	public void testAutoTagsANewAssetOnPublishAfterDraft() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId(), 0);

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		FileEntry fileEntry = DLAppServiceUtil.addFileEntry(
			null, group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(), new byte[0],
			null, null, null, serviceContext);

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		DLAppServiceUtil.updateFileEntry(
			fileEntry.getFileEntryId(), fileEntry.getFileName(),
			fileEntry.getMimeType(), fileEntry.getTitle(),
			StringUtil.randomString(), fileEntry.getDescription(),
			RandomTestUtil.randomString(), DLVersionNumberIncrease.MAJOR,
			fileEntry.getContentStream(), fileEntry.getSize(),
			fileEntry.getDisplayDate(), fileEntry.getExpirationDate(),
			fileEntry.getReviewDate(), serviceContext);

		AssetEntry assetEntry = AssetEntryLocalServiceUtil.getEntry(
			DLFileEntryConstants.getClassName(), fileEntry.getFileEntryId());

		assertContainsAssetTagName(assetEntry, ASSET_TAG_NAME_AUTO);
	}

	@Test
	public void testAutoTagsAssetOnUpdateIfEnabled() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId(), 0);

		FileEntry fileEntry = addFileEntry(serviceContext);

		serviceContext.setAssetTagNames(new String[0]);
		serviceContext.setAttribute("updateAutoTags", Boolean.TRUE);

		AssetEntry assetEntry = updateFileEntryAssetEntry(
			fileEntry, serviceContext);

		assertContainsAssetTagName(assetEntry, ASSET_TAG_NAME_AUTO);
	}

	@Test
	public void testDeletesAssetAutoTaggerEntriesWhenAssetIsDeleted()
		throws Exception {

		AssetEntry assetEntry = addFileEntryAssetEntry(
			ServiceContextTestUtil.getServiceContext(group.getGroupId(), 0));

		List<AssetAutoTaggerEntry> assetAutoTaggerEntries =
			_assetAutoTaggerEntryLocalService.getAssetAutoTaggerEntries(
				assetEntry);

		Assert.assertEquals(
			assetAutoTaggerEntries.toString(), 1,
			assetAutoTaggerEntries.size());

		DLAppServiceUtil.deleteFileEntry(assetEntry.getClassPK());

		assetAutoTaggerEntries =
			_assetAutoTaggerEntryLocalService.getAssetAutoTaggerEntries(
				assetEntry);

		Assert.assertEquals(
			assetAutoTaggerEntries.toString(), 0,
			assetAutoTaggerEntries.size());
	}

	@Test
	public void testDoesNotAddTagsWhenDisabled() throws Exception {
		withAutoTaggerDisabled(
			() -> {
				AssetEntry assetEntry = addFileEntryAssetEntry(
					ServiceContextTestUtil.getServiceContext(
						group.getGroupId(), 0));

				List<AssetTag> assetTags = assetEntry.getTags();

				Assert.assertTrue(assetTags.isEmpty());
			});
	}

	@Test
	public void testDoesNotAutoTagANewAssetOnDraft() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId(), 0);

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		AssetEntry assetEntry = addFileEntryAssetEntry(serviceContext);

		assertDoesNotContainAssetTagName(assetEntry, ASSET_TAG_NAME_AUTO);
	}

	@Test
	public void testDoesNotAutoTagANewAssetWhenImportIsInProcess()
		throws Exception {

		boolean portletImportInProcess =
			ExportImportThreadLocal.isPortletImportInProcess();

		ExportImportThreadLocal.setPortletImportInProcess(true);

		try {
			AssetEntry assetEntry = addFileEntryAssetEntry(
				ServiceContextTestUtil.getServiceContext(
					group.getGroupId(), 0));

			assertDoesNotContainAssetTagName(assetEntry, ASSET_TAG_NAME_AUTO);
		}
		finally {
			ExportImportThreadLocal.setPortletImportInProcess(
				portletImportInProcess);
		}
	}

	@Test
	public void testDoesNotAutoTagANewAssetWhenStagingIsInProcess()
		throws Exception {

		boolean portletStagingInProcess =
			ExportImportThreadLocal.isPortletStagingInProcess();

		ExportImportThreadLocal.setPortletStagingInProcess(true);

		try {
			AssetEntry assetEntry = addFileEntryAssetEntry(
				ServiceContextTestUtil.getServiceContext(
					group.getGroupId(), 0));

			assertDoesNotContainAssetTagName(assetEntry, ASSET_TAG_NAME_AUTO);
		}
		finally {
			ExportImportThreadLocal.setPortletStagingInProcess(
				portletStagingInProcess);
		}
	}

	@Test
	public void testDoesNotAutoTagAssetOnUpdateIfDisabled() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId(), 0);

		FileEntry fileEntry = addFileEntry(serviceContext);

		serviceContext.setAssetTagNames(new String[0]);
		serviceContext.setAttribute("updateAutoTags", Boolean.FALSE);

		AssetEntry assetEntry = updateFileEntryAssetEntry(
			fileEntry, serviceContext);

		assertDoesNotContainAssetTagName(assetEntry, ASSET_TAG_NAME_AUTO);
	}

	@Test
	public void testKeepsAssetTagCount() throws Exception {
		AssetEntry assetEntry = addFileEntryAssetEntry(
			ServiceContextTestUtil.getServiceContext(group.getGroupId(), 0));

		AssetTag assetTag = _assetTagLocalService.getTag(
			group.getGroupId(), ASSET_TAG_NAME_AUTO);

		Assert.assertEquals(1, assetTag.getAssetCount());

		_assetAutoTagger.untag(assetEntry);

		assetTag = _assetTagLocalService.getTag(
			group.getGroupId(), ASSET_TAG_NAME_AUTO);

		Assert.assertEquals(0, assetTag.getAssetCount());
	}

	@Test
	public void testRemovesAutoTags() throws Exception {
		AssetEntry assetEntry = addFileEntryAssetEntry(
			ServiceContextTestUtil.getServiceContext(group.getGroupId(), 0));

		assertContainsAssetTagName(assetEntry, ASSET_TAG_NAME_AUTO);

		applyAssetTagName(assetEntry, ASSET_TAG_NAME_MANUAL);

		_assetAutoTagger.untag(assetEntry);

		String[] assetTagNames = assetEntry.getTagNames();

		Assert.assertEquals(assetTagNames.toString(), 1, assetTagNames.length);

		assertContainsAssetTagName(assetEntry, ASSET_TAG_NAME_MANUAL);
	}

	@Inject
	private AssetAutoTagger _assetAutoTagger;

	@Inject
	private AssetAutoTaggerEntryLocalService _assetAutoTaggerEntryLocalService;

	@Inject
	private AssetTagLocalService _assetTagLocalService;

}