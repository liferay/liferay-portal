/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.data.engine.rest.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.resource.v2_0.DataDefinitionResource;
import com.liferay.data.engine.rest.test.util.DataDefinitionTestUtil;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.exportimport.data.handler.DLExportableRepositoryPublisher;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFileShortcutConstants;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppHelperLocalService;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFileShortcutLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFolderLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLTrashServiceUtil;
import com.liferay.document.library.test.util.BaseDLAppTestCase;
import com.liferay.document.library.test.util.DLAppTestUtil;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.exportimport.kernel.lar.DataLevel;
import com.liferay.exportimport.kernel.lar.ExportImportDateUtil;
import com.liferay.exportimport.kernel.lar.ManifestSummary;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerBoolean;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerControl;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.exportimport.kernel.service.StagingLocalServiceUtil;
import com.liferay.exportimport.test.util.lar.BasePortletDataHandlerTestCase;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.RepositoryLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LongWrapper;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.repository.liferayrepository.LiferayRepository;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portlet.PortletPreferencesImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Zsolt Berentey
 */
@RunWith(Arquillian.class)
public class DLPortletDataHandlerTest extends BasePortletDataHandlerTestCase {

	public static final String NAMESPACE = "document_library";

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		super.setUp();
	}

	@After
	public void tearDown() {
		if (_serviceRegistrations != null) {
			_serviceRegistrations.forEach(ServiceRegistration::unregister);
		}
	}

	@Test
	public void testCustomRepositoryEntriesExport() throws Exception {
		initContext();

		addRepositoryEntries();

		portletDataContext.setEndDate(getEndDate());

		portletDataHandler.exportData(
			portletDataContext, portletId, new PortletPreferencesImpl());

		ManifestSummary manifestSummary =
			portletDataContext.getManifestSummary();

		Map<String, LongWrapper> modelAdditionCounters =
			manifestSummary.getModelAdditionCounters();

		LongWrapper fileEntryModelAdditionCounter = modelAdditionCounters.get(
			DLFileEntry.class.getName());

		Assert.assertEquals(0, fileEntryModelAdditionCounter.getValue());

		LongWrapper folderModelAdditionCounter = modelAdditionCounters.get(
			DLFolder.class.getName());

		Assert.assertEquals(0, folderModelAdditionCounter.getValue());
	}

	@Test
	public void testCustomRepositoryEntriesPrepareManifestSummary()
		throws Exception {

		initContext();

		addRepositoryEntries();

		portletDataContext.setEndDate(getEndDate());

		portletDataHandler.prepareManifestSummary(portletDataContext);

		ManifestSummary manifestSummary =
			portletDataContext.getManifestSummary();

		Map<String, LongWrapper> modelAdditionCounters =
			manifestSummary.getModelAdditionCounters();

		LongWrapper fileEntryModelAdditionCounter = modelAdditionCounters.get(
			DLFileEntry.class.getName());

		Assert.assertEquals(0, fileEntryModelAdditionCounter.getValue());

		LongWrapper folderModelAdditionCounter = modelAdditionCounters.get(
			DLFolder.class.getName());

		Assert.assertEquals(0, folderModelAdditionCounter.getValue());
	}

	@Test
	public void testDeleteAllFolders() throws Exception {
		Group group = GroupTestUtil.addGroup();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId());

		Folder parentFolder = DLAppServiceUtil.addFolder(
			null, group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, "parent",
			RandomTestUtil.randomString(), serviceContext);

		Folder childFolder = DLAppServiceUtil.addFolder(
			null, group.getGroupId(), parentFolder.getFolderId(), "child",
			RandomTestUtil.randomString(), serviceContext);

		DLTrashServiceUtil.moveFolderToTrash(childFolder.getFolderId());

		DLTrashServiceUtil.moveFolderToTrash(parentFolder.getFolderId());

		DLAppServiceUtil.deleteFolder(parentFolder.getFolderId());

		GroupLocalServiceUtil.deleteGroup(group);

		Assert.assertEquals(
			0,
			DLFolderLocalServiceUtil.getFoldersCount(
				group.getGroupId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID));
	}

	@Test
	public void testDeleteGroup() throws Exception {
		Class<?> clazz = getClass();

		String json = StringUtil.read(
			clazz.getResourceAsStream(
				"dependencies/valid_data_definition.json"));

		Group group = GroupTestUtil.addGroup();

		DataDefinition dataDefinition =
			DataDefinitionTestUtil.addDataDefinition(
				"document-library", _dataDefinitionResourceFactory,
				group.getGroupId(), json, TestPropsValues.getUser());

		Assert.assertNotNull(
			_ddmStructureLocalService.fetchDDMStructure(
				dataDefinition.getId()));

		_groupLocalService.deleteGroup(group);

		Assert.assertNull(
			_ddmStructureLocalService.fetchDDMStructure(
				dataDefinition.getId()));
	}

	@Test
	public void testDLExportableRepositoryPublisherIsInvokedWhenExporting()
		throws Exception {

		AtomicInteger atomicInteger = new AtomicInteger(0);

		_registerService(
			new CountingDLExportableRepositoryPublisher(atomicInteger));

		initContext();

		portletDataHandler.exportData(
			portletDataContext, DLPortletKeys.DOCUMENT_LIBRARY, null);

		Assert.assertTrue(atomicInteger.get() >= 1);
	}

	@Test
	public void testDLExportableRepositoryPublisherIsInvokedWhenPreparingSummary()
		throws Exception {

		AtomicInteger atomicInteger = new AtomicInteger(0);

		_registerService(
			new CountingDLExportableRepositoryPublisher(atomicInteger));

		initContext();

		portletDataHandler.prepareManifestSummary(portletDataContext);

		Assert.assertTrue(atomicInteger.get() >= 1);
	}

	@Ignore
	@Override
	@Test
	public void testGetExportConfigurationControls() throws Exception {
	}

	@Test
	public void testGetManifestSummaryWithFolderAndFile() throws Exception {
		StagingLocalServiceUtil.enableLocalStaging(
			TestPropsValues.getUserId(), this.stagingGroup, false, false,
			new ServiceContext());

		Group stagingGroup = this.stagingGroup.getStagingGroup();

		Folder folder = DLAppServiceUtil.addFolder(
			null, stagingGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			new ServiceContext());

		FileEntry fileEntry = DLAppServiceUtil.addFileEntry(
			null, folder.getGroupId(), folder.getFolderId(),
			RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, BaseDLAppTestCase.CONTENT.getBytes(), null, null,
			null, new ServiceContext());

		FileVersion fileVersion = fileEntry.getFileVersion();

		DLFileEntryLocalServiceUtil.updateStatus(
			TestPropsValues.getUserId(), fileVersion.getFileVersionId(),
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext(), new HashMap<>());

		initContext();

		Map<String, String[]> parameterMap =
			portletDataContext.getParameterMap();

		parameterMap.put(
			ExportImportDateUtil.RANGE,
			new String[] {ExportImportDateUtil.RANGE_FROM_LAST_PUBLISH_DATE});

		portletDataContext.setParameterMap(parameterMap);

		portletDataContext.setEndDate(getEndDate());
		portletDataContext.setScopeGroupId(stagingGroup.getGroupId());

		portletDataHandler.prepareManifestSummary(portletDataContext);

		ManifestSummary manifestSummary =
			portletDataContext.getManifestSummary();

		Assert.assertEquals(
			1, manifestSummary.getModelAdditionCount(DLFolder.class.getName()));
		Assert.assertEquals(
			1,
			manifestSummary.getModelAdditionCount(DLFileEntry.class.getName()));
	}

	@Test
	public void testGetManifestSummaryWithFolderAndFileInTrash()
		throws Exception {

		StagingLocalServiceUtil.enableLocalStaging(
			TestPropsValues.getUserId(), this.stagingGroup, false, false,
			new ServiceContext());

		Group stagingGroup = this.stagingGroup.getStagingGroup();

		Folder folder = DLAppServiceUtil.addFolder(
			null, stagingGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			new ServiceContext());

		FileEntry fileEntry = DLAppServiceUtil.addFileEntry(
			null, folder.getGroupId(), folder.getFolderId(),
			RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, BaseDLAppTestCase.CONTENT.getBytes(), null, null,
			null, new ServiceContext());

		FileVersion fileVersion = fileEntry.getFileVersion();

		DLFileEntryLocalServiceUtil.updateStatus(
			TestPropsValues.getUserId(), fileVersion.getFileVersionId(),
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext(), new HashMap<>());

		_dlAppHelperLocalService.moveFolderToTrash(
			TestPropsValues.getUserId(), folder);

		initContext();

		Map<String, String[]> parameterMap =
			portletDataContext.getParameterMap();

		parameterMap.put(
			ExportImportDateUtil.RANGE,
			new String[] {ExportImportDateUtil.RANGE_FROM_LAST_PUBLISH_DATE});

		portletDataContext.setParameterMap(parameterMap);

		portletDataContext.setEndDate(getEndDate());
		portletDataContext.setScopeGroupId(stagingGroup.getGroupId());

		portletDataHandler.prepareManifestSummary(portletDataContext);

		ManifestSummary manifestSummary =
			portletDataContext.getManifestSummary();

		Assert.assertEquals(
			0, manifestSummary.getModelAdditionCount(DLFolder.class.getName()));
		Assert.assertEquals(
			0,
			manifestSummary.getModelAdditionCount(DLFileEntry.class.getName()));
	}

	@Test
	public void testPublishedCustomRepositoryEntriesExport() throws Exception {
		long repositoryId = addRepositoryEntries();

		_registerService(
			new ConstantDLExportableRepositoryPublisher(repositoryId));

		initContext();

		portletDataContext.setEndDate(getEndDate());

		portletDataHandler.exportData(
			portletDataContext, portletId, new PortletPreferencesImpl());

		ManifestSummary manifestSummary =
			portletDataContext.getManifestSummary();

		Map<String, LongWrapper> modelAdditionCounters =
			manifestSummary.getModelAdditionCounters();

		LongWrapper fileEntryModelAdditionCounter = modelAdditionCounters.get(
			DLFileEntry.class.getName());

		Assert.assertTrue(fileEntryModelAdditionCounter.getValue() >= 1);

		LongWrapper folderModelAdditionCounter = modelAdditionCounters.get(
			DLFolder.class.getName());

		Assert.assertTrue(folderModelAdditionCounter.getValue() >= 1);
	}

	@Test
	public void testPublishedCustomRepositoryEntriesPrepareManifestSummary()
		throws Exception {

		long repositoryId = addRepositoryEntries();

		_registerService(
			new ConstantDLExportableRepositoryPublisher(repositoryId));

		initContext();

		portletDataContext.setEndDate(getEndDate());

		portletDataHandler.prepareManifestSummary(portletDataContext);

		ManifestSummary manifestSummary =
			portletDataContext.getManifestSummary();

		Map<String, LongWrapper> modelAdditionCounters =
			manifestSummary.getModelAdditionCounters();

		LongWrapper fileEntryModelAdditionCounter = modelAdditionCounters.get(
			DLFileEntry.class.getName());

		Assert.assertTrue(fileEntryModelAdditionCounter.getValue() >= 1);

		LongWrapper folderModelAdditionCounter = modelAdditionCounters.get(
			DLFolder.class.getName());

		Assert.assertTrue(folderModelAdditionCounter.getValue() >= 1);
	}

	@Override
	protected void addParameters(Map<String, String[]> parameterMap) {
		addBooleanParameter(
			parameterMap, "document_library", "repositories", true);
	}

	protected long addRepositoryEntries() throws Exception {
		Repository repository = RepositoryLocalServiceUtil.addRepository(
			null, TestPropsValues.getUserId(), stagingGroup.getGroupId(),
			PortalUtil.getClassNameId(LiferayRepository.class.getName()),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			PortletKeys.BACKGROUND_TASK, StringPool.BLANK,
			PortletKeys.BACKGROUND_TASK, new UnicodeProperties(), true,
			ServiceContextTestUtil.getServiceContext());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				stagingGroup.getGroupId(), TestPropsValues.getUserId());

		Folder folder = DLAppServiceUtil.addFolder(
			null, repository.getRepositoryId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);

		DLAppLocalServiceUtil.addFileEntry(
			null, TestPropsValues.getUserId(), repository.getRepositoryId(),
			folder.getFolderId(), RandomTestUtil.randomString() + ".txt",
			ContentTypes.TEXT_PLAIN, TestDataConstants.TEST_BYTE_ARRAY, null,
			null, null, serviceContext);

		return repository.getRepositoryId();
	}

	@Override
	protected void addStagedModels() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				stagingGroup.getGroupId(), TestPropsValues.getUserId());

		Folder folder = DLAppServiceUtil.addFolder(
			null, stagingGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			stagingGroup.getGroupId(), DLFileEntryMetadata.class.getName());

		DLFileEntryType dlFileEntryType =
			DLFileEntryTypeLocalServiceUtil.addFileEntryType(
				null, TestPropsValues.getUserId(), stagingGroup.getGroupId(),
				ddmStructure.getStructureId(), null,
				Collections.singletonMap(LocaleUtil.US, "New File Entry Type"),
				Collections.singletonMap(LocaleUtil.US, "New File Entry Type"),
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_SCOPE_DEFAULT,
				serviceContext);

		DLAppTestUtil.populateServiceContext(
			serviceContext, dlFileEntryType.getFileEntryTypeId());

		FileEntry fileEntry = DLAppLocalServiceUtil.addFileEntry(
			null, TestPropsValues.getUserId(), stagingGroup.getGroupId(),
			folder.getFolderId(), RandomTestUtil.randomString() + ".txt",
			ContentTypes.TEXT_PLAIN, TestDataConstants.TEST_BYTE_ARRAY, null,
			null, null, serviceContext);

		DLAppLocalServiceUtil.addFileShortcut(
			null, TestPropsValues.getUserId(), stagingGroup.getGroupId(),
			folder.getFolderId(), fileEntry.getFileEntryId(), serviceContext);
	}

	@Override
	protected DataLevel getDataLevel() {
		return DataLevel.PORTLET_INSTANCE;
	}

	@Override
	protected String[] getDataPortletPreferences() {
		return new String[0];
	}

	@Override
	protected PortletDataHandlerControl[] getExportControls() {
		return new PortletDataHandlerControl[] {
			new PortletDataHandlerBoolean(
				NAMESPACE, "repositories", true, false, null,
				Repository.class.getName(),
				StagedModelType.REFERRER_CLASS_NAME_ALL),
			new PortletDataHandlerBoolean(
				NAMESPACE, "folders", true, false, null,
				DLFolderConstants.getClassName()),
			new PortletDataHandlerBoolean(
				NAMESPACE, "documents", true, false,
				new PortletDataHandlerControl[] {
					new PortletDataHandlerBoolean(
						NAMESPACE, "previews-and-thumbnails")
				},
				DLFileEntryConstants.getClassName()),
			new PortletDataHandlerBoolean(
				NAMESPACE, "document-types", true, false, null,
				DLFileEntryType.class.getName()),
			new PortletDataHandlerBoolean(
				NAMESPACE, "shortcuts", true, false, null,
				DLFileShortcutConstants.getClassName())
		};
	}

	@Override
	protected PortletDataHandlerControl[] getImportControls() {
		return getExportControls();
	}

	@Override
	protected String getPortletId() {
		return DLPortletKeys.DOCUMENT_LIBRARY;
	}

	@Override
	protected List<StagedModel> getStagedModels() {
		List<StagedModel> stagedModels = new ArrayList<>();

		List<DLFolder> folders = DLFolderLocalServiceUtil.getFolders(
			portletDataContext.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		stagedModels.addAll(folders);

		for (DLFolder folder : folders) {
			stagedModels.addAll(
				DLFileEntryLocalServiceUtil.getFileEntries(
					portletDataContext.getGroupId(), folder.getFolderId()));

			stagedModels.addAll(
				DLFileShortcutLocalServiceUtil.getFileShortcuts(
					portletDataContext.getGroupId(), folder.getFolderId(), true,
					WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS));
		}

		stagedModels.addAll(
			DLFileEntryLocalServiceUtil.getFileEntries(
				portletDataContext.getGroupId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID));

		stagedModels.addAll(
			DLFileShortcutLocalServiceUtil.getFileShortcuts(
				portletDataContext.getGroupId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, true,
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS));

		stagedModels.addAll(
			RepositoryLocalServiceUtil.getGroupRepositories(
				portletDataContext.getGroupId()));

		stagedModels.addAll(
			DLFileEntryTypeLocalServiceUtil.getFileEntryTypes(
				new long[] {portletDataContext.getGroupId()}));

		return stagedModels;
	}

	@Override
	protected PortletDataHandlerControl[] getStagingControls() {
		return getExportControls();
	}

	@Override
	protected boolean isDataPortalLevel() {
		return false;
	}

	@Override
	protected boolean isDataPortletInstanceLevel() {
		return true;
	}

	@Override
	protected boolean isDataSiteLevel() {
		return false;
	}

	@Override
	protected boolean isDeleteDataTested() {
		return true;
	}

	@Override
	protected boolean isDisplayPortlet() {
		return super.isDisplayPortlet();
	}

	@Override
	protected boolean isExportImportDataTested() {
		return true;
	}

	@Override
	protected boolean isGetExportConfigurationControlsTested() {
		return true;
	}

	@Override
	protected boolean isGetExportModelCountTested() {
		return true;
	}

	private void _registerService(
		DLExportableRepositoryPublisher dlExportableRepositoryPublisher) {

		Bundle bundle = FrameworkUtil.getBundle(DLPortletDataHandlerTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistrations.add(
			bundleContext.registerService(
				DLExportableRepositoryPublisher.class,
				dlExportableRepositoryPublisher, null));
	}

	@Inject
	private static DDMStructureLocalService _ddmStructureLocalService;

	@Inject
	private static GroupLocalService _groupLocalService;

	@Inject
	private DataDefinitionResource.Factory _dataDefinitionResourceFactory;

	@Inject
	private DLAppHelperLocalService _dlAppHelperLocalService;

	private final Collection
		<ServiceRegistration<DLExportableRepositoryPublisher>>
			_serviceRegistrations = new ArrayList<>();

	private static class ConstantDLExportableRepositoryPublisher
		implements DLExportableRepositoryPublisher {

		public ConstantDLExportableRepositoryPublisher(long repositoryId) {
			_repositoryId = repositoryId;
		}

		@Override
		public void publish(long groupId, Consumer<Long> repositoryIdConsumer) {
			repositoryIdConsumer.accept(_repositoryId);
		}

		private final long _repositoryId;

	}

	private static class CountingDLExportableRepositoryPublisher
		implements DLExportableRepositoryPublisher {

		public CountingDLExportableRepositoryPublisher(
			AtomicInteger atomicInteger) {

			_atomicInteger = atomicInteger;
		}

		@Override
		public void publish(long groupId, Consumer<Long> repositoryIdConsumer) {
			_atomicInteger.incrementAndGet();
		}

		private final AtomicInteger _atomicInteger;

	}

}