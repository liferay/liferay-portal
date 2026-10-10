/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.trash.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryServiceUtil;
import com.liferay.document.library.kernel.service.DLFolderLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLTrashServiceUtil;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.document.library.test.util.DLAppTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.ClassedModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.model.WorkflowedModel;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.SearchContextTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashHandlerRegistryUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.repository.liferayrepository.model.LiferayFileEntry;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.trash.TrashHelper;
import com.liferay.trash.exception.RestoreEntryException;
import com.liferay.trash.exception.TrashEntryException;
import com.liferay.trash.test.util.BaseTrashHandlerTestCase;
import com.liferay.trash.test.util.DefaultWhenIsAssetable;
import com.liferay.trash.test.util.DefaultWhenIsIndexableBaseModel;
import com.liferay.trash.test.util.WhenCanBeDuplicatedInTrash;
import com.liferay.trash.test.util.WhenHasDraftStatus;
import com.liferay.trash.test.util.WhenHasGrandParent;
import com.liferay.trash.test.util.WhenHasMyBaseModel;
import com.liferay.trash.test.util.WhenHasRecentBaseModelCount;
import com.liferay.trash.test.util.WhenIsAssetable;
import com.liferay.trash.test.util.WhenIsAssetableBaseModel;
import com.liferay.trash.test.util.WhenIsAssetableParentModel;
import com.liferay.trash.test.util.WhenIsIndexableBaseModel;
import com.liferay.trash.test.util.WhenIsMoveableFromTrashBaseModel;
import com.liferay.trash.test.util.WhenIsRestorableBaseModel;
import com.liferay.trash.test.util.WhenIsUpdatableBaseModel;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alexander Chow
 * @author Julio Camarero
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class DLFileEntryTrashHandlerTest
	extends BaseTrashHandlerTestCase
	implements WhenCanBeDuplicatedInTrash, WhenHasDraftStatus,
			   WhenHasGrandParent, WhenHasMyBaseModel,
			   WhenHasRecentBaseModelCount, WhenIsAssetableBaseModel,
			   WhenIsAssetableParentModel, WhenIsIndexableBaseModel,
			   WhenIsMoveableFromTrashBaseModel, WhenIsRestorableBaseModel,
			   WhenIsUpdatableBaseModel {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Override
	public BaseModel<?> addDraftBaseModelWithWorkflow(
			BaseModel<?> parentBaseModel, ServiceContext serviceContext)
		throws Exception {

		DLFolder dlFolder = (DLFolder)parentBaseModel;

		return addBaseModelWithWorkflow(
			dlFolder.getGroupId(), dlFolder.getFolderId(), false);
	}

	@Override
	public AssetEntry fetchAssetEntry(ClassedModel classedModel)
		throws Exception {

		return _whenIsAssetable.fetchAssetEntry(classedModel);
	}

	@Override
	public String getBaseModelName(ClassedModel classedModel) {
		DLFileEntry dlFileEntry = (DLFileEntry)classedModel;

		return dlFileEntry.getTitle();
	}

	@Override
	public int getMineBaseModelsCount(long groupId, long userId)
		throws Exception {

		return DLAppServiceUtil.getGroupFileEntriesCount(
			groupId, userId, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, null,
			WorkflowConstants.STATUS_APPROVED);
	}

	@Override
	public String getParentBaseModelClassName() {
		Class<DLFolder> dlFolderClass = DLFolder.class;

		return dlFolderClass.getName();
	}

	@Override
	public int getRecentBaseModelsCount(long groupId) throws Exception {
		return DLAppServiceUtil.getGroupFileEntriesCount(
			groupId, 0, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, null,
			WorkflowConstants.STATUS_APPROVED);
	}

	@Override
	public String getSearchKeywords() {
		return _FILE_ENTRY_TITLE;
	}

	@Override
	public boolean isAssetEntryVisible(ClassedModel classedModel, long classPK)
		throws Exception {

		return _whenIsAssetable.isAssetEntryVisible(classedModel, classPK);
	}

	@Override
	public BaseModel<?> moveBaseModelFromTrash(
			ClassedModel classedModel, Group group,
			ServiceContext serviceContext)
		throws Exception {

		BaseModel<?> parentBaseModel = getParentBaseModel(
			group, serviceContext);

		DLTrashServiceUtil.moveFileEntryFromTrash(
			(Long)classedModel.getPrimaryKeyObj(),
			(Long)parentBaseModel.getPrimaryKeyObj(), serviceContext);

		return parentBaseModel;
	}

	@Override
	public void moveParentBaseModelToTrash(long primaryKey) throws Exception {
		DLTrashServiceUtil.moveFolderToTrash(primaryKey);
	}

	@Override
	public int searchBaseModelsCount(Class<?> clazz, long groupId)
		throws Exception {

		return _whenIsIndexableBaseModel.searchBaseModelsCount(clazz, groupId);
	}

	@Override
	public int searchTrashEntriesCount(
			String keywords, ServiceContext serviceContext)
		throws Exception {

		return _whenIsIndexableBaseModel.searchTrashEntriesCount(
			keywords, serviceContext);
	}

	@Test
	public void testFileNameUpdateWhenUpdatingTitle() throws Exception {
		DLFileEntry dlFileEntry = (DLFileEntry)addBaseModelWithWorkflow(
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		moveBaseModelToTrash(dlFileEntry.getFileEntryId());

		TrashHandler trashHandler = TrashHandlerRegistryUtil.getTrashHandler(
			getBaseModelClassName());

		String title = RandomTestUtil.randomString();

		trashHandler.updateTitle(dlFileEntry.getFileEntryId(), title);

		dlFileEntry = DLFileEntryLocalServiceUtil.getFileEntry(
			dlFileEntry.getFileEntryId());

		DLFileVersion dlFileVersion = dlFileEntry.getLatestFileVersion(true);

		Assert.assertEquals(
			DLUtil.getSanitizedFileName(title, dlFileEntry.getExtension()),
			dlFileEntry.getFileName());

		Assert.assertEquals(
			DLUtil.getSanitizedFileName(title, dlFileVersion.getExtension()),
			dlFileVersion.getFileName());
	}

	@Test
	public void testRestoreExpiredFileEntry() throws Exception {
		DLFileEntry dlFileEntry = (DLFileEntry)addBaseModelWithWorkflow(
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		dlFileEntry = DLFileEntryLocalServiceUtil.updateStatus(
			dlFileEntry.getUserId(), dlFileEntry, dlFileEntry.getFileVersion(),
			WorkflowConstants.STATUS_EXPIRED,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()),
			Collections.emptyMap());

		Assert.assertEquals(
			WorkflowConstants.STATUS_EXPIRED, dlFileEntry.getStatus());

		moveBaseModelToTrash(dlFileEntry.getFileEntryId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_IN_TRASH, dlFileEntry.getStatus());

		_restoreTrashEntry(dlFileEntry.getFileEntryId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_EXPIRED, dlFileEntry.getStatus());

		_assertIndexed(
			dlFileEntry.getFileEntryId(), WorkflowConstants.STATUS_EXPIRED);
	}

	@Test
	public void testRestoreFileEntryWithDraftLatestVersion() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		DLFileEntry dlFileEntry = (DLFileEntry)addBaseModelWithWorkflow(
			serviceContext);

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		updateBaseModel(dlFileEntry.getFileEntryId(), serviceContext);

		_assertRestoredFileEntryVisible(dlFileEntry.getFileEntryId());
	}

	@Test
	public void testRestoreFileEntryWithExpiredLatestVersion()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		DLFileEntry dlFileEntry = (DLFileEntry)addBaseModelWithWorkflow(
			serviceContext);

		dlFileEntry = (DLFileEntry)updateBaseModel(
			dlFileEntry.getFileEntryId(), serviceContext);

		DLFileVersion dlFileVersion = dlFileEntry.getFileVersion();

		DLFileEntryLocalServiceUtil.updateStatus(
			dlFileEntry.getUserId(), dlFileVersion.getFileVersionId(),
			WorkflowConstants.STATUS_EXPIRED, serviceContext,
			Collections.emptyMap());

		_assertRestoredFileEntryVisible(dlFileEntry.getFileEntryId());
	}

	@Override
	@Test(expected = TrashEntryException.class)
	public void testTrashParentAndBaseModel() throws Exception {
		super.testTrashParentAndBaseModel();
	}

	@Override
	@Test(expected = RestoreEntryException.class)
	public void testTrashParentAndRestoreParentAndBaseModel() throws Exception {
		super.testTrashParentAndRestoreParentAndBaseModel();
	}

	@Override
	public BaseModel<?> updateBaseModel(
			long primaryKey, ServiceContext serviceContext)
		throws Exception {

		DLFileEntry dlFileEntry = DLFileEntryLocalServiceUtil.getDLFileEntry(
			primaryKey);

		String content = "Content: Enterprise. Open Source. For Life.";

		FileEntry fileEntry = DLAppServiceUtil.updateFileEntry(
			primaryKey, RandomTestUtil.randomString() + ".txt",
			ContentTypes.TEXT_PLAIN, dlFileEntry.getTitle(), StringPool.BLANK,
			StringPool.BLANK, StringPool.BLANK, DLVersionNumberIncrease.MINOR,
			content.getBytes(), dlFileEntry.getDisplayDate(),
			dlFileEntry.getExpirationDate(), dlFileEntry.getReviewDate(),
			serviceContext);

		LiferayFileEntry liferayFileEntry = (LiferayFileEntry)fileEntry;

		return liferayFileEntry.getDLFileEntry();
	}

	@Override
	protected BaseModel<?> addBaseModelWithWorkflow(
			BaseModel<?> parentBaseModel, ServiceContext serviceContext)
		throws Exception {

		DLFolder dlFolder = (DLFolder)parentBaseModel;

		return addBaseModelWithWorkflow(
			dlFolder.getGroupId(), dlFolder.getFolderId(), true);
	}

	protected BaseModel<?> addBaseModelWithWorkflow(
			long groupId, long folderId, boolean approved)
		throws Exception {

		FileEntry fileEntry = DLAppTestUtil.addFileEntryWithWorkflow(
			TestPropsValues.getUserId(), groupId, folderId,
			RandomTestUtil.randomString() + ".txt", getSearchKeywords(),
			approved,
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId()));

		return (DLFileEntry)fileEntry.getModel();
	}

	@Override
	protected BaseModel<?> addBaseModelWithWorkflow(
			ServiceContext serviceContext)
		throws Exception {

		return addBaseModelWithWorkflow(
			serviceContext.getScopeGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, true);
	}

	@Override
	protected void deleteParentBaseModel(
			BaseModel<?> parentBaseModel, boolean includeTrashedEntries)
		throws Exception {

		DLFolder dlFolder = (DLFolder)parentBaseModel;

		DLFolderLocalServiceUtil.deleteFolder(dlFolder.getFolderId(), false);
	}

	@Override
	protected BaseModel<?> getBaseModel(long primaryKey) throws Exception {
		return DLFileEntryLocalServiceUtil.getDLFileEntry(primaryKey);
	}

	@Override
	protected Class<?> getBaseModelClass() {
		return DLFileEntry.class;
	}

	@Override
	protected int getNotInTrashBaseModelsCount(BaseModel<?> parentBaseModel)
		throws Exception {

		DLFolder dlFolder = (DLFolder)parentBaseModel;

		return DLFileEntryServiceUtil.getFileEntriesCount(
			dlFolder.getGroupId(), dlFolder.getFolderId(),
			WorkflowConstants.STATUS_ANY);
	}

	@Override
	protected BaseModel<?> getParentBaseModel(
			Group group, long parentBaseModelId, ServiceContext serviceContext)
		throws Exception {

		Folder folder = DLAppServiceUtil.addFolder(
			null, group.getGroupId(), parentBaseModelId,
			RandomTestUtil.randomString(_FOLDER_NAME_MAX_LENGTH),
			RandomTestUtil.randomString(), serviceContext);

		return (DLFolder)folder.getModel();
	}

	@Override
	protected BaseModel<?> getParentBaseModel(
			Group group, ServiceContext serviceContext)
		throws Exception {

		return getParentBaseModel(
			group, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, serviceContext);
	}

	@Override
	protected String getUniqueTitle(BaseModel<?> baseModel) {
		DLFileEntry dlFileEntry = (DLFileEntry)baseModel;

		return _trashHelper.getOriginalTitle(dlFileEntry.getTitle());
	}

	@Override
	protected WorkflowedModel getWorkflowedModel(ClassedModel baseModel)
		throws Exception {

		DLFileEntry dlFileEntry = (DLFileEntry)baseModel;

		return dlFileEntry.getFileVersion();
	}

	@Override
	protected boolean isInTrashContainer(TrashedModel trashedModel) {
		return _trashHelper.isInTrashContainer(trashedModel);
	}

	@Override
	protected void moveBaseModelToTrash(long primaryKey) throws Exception {
		DLTrashServiceUtil.moveFileEntryToTrash(primaryKey);
	}

	private void _assertIndexed(long fileEntryId, int status) throws Exception {
		Indexer<DLFileEntry> indexer = IndexerRegistryUtil.getIndexer(
			DLFileEntry.class);

		SearchContext searchContext = SearchContextTestUtil.getSearchContext(
			group.getGroupId());

		searchContext.setAttribute(Field.STATUS, status);

		Hits hits = indexer.search(searchContext);

		Document[] documents = hits.getDocs();

		Assert.assertEquals(Arrays.toString(documents), 1, documents.length);

		Document document = documents[0];

		Assert.assertEquals(
			String.valueOf(fileEntryId), document.get(Field.ENTRY_CLASS_PK));
	}

	private void _assertRestoredFileEntryVisible(long fileEntryId)
		throws Exception {

		moveBaseModelToTrash(fileEntryId);

		_restoreTrashEntry(fileEntryId);

		Assert.assertTrue(
			isAssetEntryVisible(
				DLFileEntryLocalServiceUtil.getFileEntry(fileEntryId),
				fileEntryId));

		_assertIndexed(fileEntryId, WorkflowConstants.STATUS_APPROVED);
	}

	private void _restoreTrashEntry(long fileEntryId) throws Exception {
		TrashHandler trashHandler = TrashHandlerRegistryUtil.getTrashHandler(
			getBaseModelClassName());

		trashHandler.restoreTrashEntry(
			TestPropsValues.getUserId(), fileEntryId);
	}

	private static final String _FILE_ENTRY_TITLE = RandomTestUtil.randomString(
		255);

	private static final int _FOLDER_NAME_MAX_LENGTH = 100;

	@Inject
	private TrashHelper _trashHelper;

	private final WhenIsAssetable _whenIsAssetable =
		new DefaultWhenIsAssetable();
	private final WhenIsIndexableBaseModel _whenIsIndexableBaseModel =
		new DefaultWhenIsIndexableBaseModel();

}