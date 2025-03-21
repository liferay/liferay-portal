/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.exportimport.portlet.preferences.processor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.test.util.DLAppTestUtil;
import com.liferay.exportimport.controller.PortletExportController;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessor;
import com.liferay.exportimport.test.util.ExportImportTestUtil;
import com.liferay.exportimport.test.util.TestReaderWriter;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.comment.CommentManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.IdentityServiceContextFunction;
import com.liferay.portal.kernel.service.RepositoryLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.ratings.kernel.model.RatingsEntry;
import com.liferay.ratings.test.util.RatingsTestUtil;

import jakarta.portlet.PortletPreferences;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Cristina González
 */
@RunWith(Arquillian.class)
public class DLExportImportPortletPreferencesProcessorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypePortletLayout(_group.getGroupId());

		LayoutTestUtil.addPortletToLayout(
			TestPropsValues.getUserId(), _layout,
			DLPortletKeys.DOCUMENT_LIBRARY, "column-1",
			new HashMap<String, String[]>());

		_portletDataContextExport =
			ExportImportTestUtil.getExportPortletDataContext(
				_group.getGroupId());

		_portletDataContextExport.setPlid(_layout.getPlid());
		_portletDataContextExport.setPortletId(DLPortletKeys.DOCUMENT_LIBRARY);

		_portletDataContextImport =
			ExportImportTestUtil.getImportPortletDataContext(
				_group.getGroupId());

		_portletDataContextImport.setPlid(_layout.getPlid());
		_portletDataContextImport.setPortletId(DLPortletKeys.DOCUMENT_LIBRARY);

		_portletPreferences =
			PortletPreferencesFactoryUtil.getStrictPortletSetup(
				_layout, DLPortletKeys.DOCUMENT_LIBRARY);

		_portletPreferences.setValue("selectionStyle", "manual");
	}

	@Test
	public void testExportDLFileEntryIdWithComments() throws Exception {
		FileEntry fileEntry = DLAppTestUtil.addFileEntry(_group.getGroupId());

		_setPortletPreferences(fileEntry);

		User user = TestPropsValues.getUser();

		long commentPrimaryKey = CommentManagerUtil.addComment(
			user.getUserId(), _group.getGroupId(), DLFileEntry.class.getName(),
			fileEntry.getFileEntryId(), RandomTestUtil.randomString(),
			new IdentityServiceContextFunction(
				ServiceContextTestUtil.getServiceContext()));

		_exportPortlet();

		Map<String, String[]> parameterMap =
			_portletDataContextExport.getParameterMap();

		Assert.assertEquals(
			parameterMap.get(PortletDataHandlerKeys.COMMENTS)[0],
			Boolean.TRUE.toString());

		Set<String> primaryKeys = _portletDataContextExport.getPrimaryKeys();

		Assert.assertTrue(
			primaryKeys.toString(),
			primaryKeys.contains(
				StringBundler.concat(
					String.class.getName(), StringPool.POUND,
					DLFileEntry.class.getName(), StringPool.POUND,
					fileEntry.getFileEntryId())));

		Assert.assertTrue(
			primaryKeys.toString(),
			primaryKeys.contains(
				StringBundler.concat(
					String.class.getName(),
					"#com.liferay.message.boards.model.MBMessage#",
					commentPrimaryKey)));
	}

	@Test
	public void testExportDLFileEntryIdWithRatings() throws Exception {
		FileEntry fileEntry = DLAppTestUtil.addFileEntry(_group.getGroupId());

		_setPortletPreferences(fileEntry);

		RatingsEntry ratingsEntry = RatingsTestUtil.addEntry(
			DLFileEntry.class.getName(), fileEntry.getFileEntryId());

		_exportPortlet();

		Map<String, String[]> parameterMap =
			_portletDataContextExport.getParameterMap();

		Assert.assertEquals(
			parameterMap.get(PortletDataHandlerKeys.COMMENTS)[0],
			Boolean.TRUE.toString());

		Set<String> primaryKeys = _portletDataContextExport.getPrimaryKeys();

		Assert.assertTrue(
			primaryKeys.toString(),
			primaryKeys.contains(
				StringBundler.concat(
					String.class.getName(), StringPool.POUND,
					DLFileEntry.class.getName(), StringPool.POUND,
					fileEntry.getFileEntryId())));

		Assert.assertTrue(
			primaryKeys.toString(),
			primaryKeys.contains(
				StringBundler.concat(
					String.class.getName(), StringPool.POUND,
					RatingsEntry.class.getName(), StringPool.POUND,
					ratingsEntry.getEntryId())));
	}

	@Test
	public void testExportDLFileEntryInDifferentGroup() throws Exception {
		FileEntry fileEntry = DLAppTestUtil.addFileEntry(
			TestPropsValues.getGroupId());

		_setPortletPreferences(fileEntry);

		_exportPortlet();

		Set<String> primaryKeys = _portletDataContextExport.getPrimaryKeys();

		Assert.assertFalse(
			primaryKeys.toString(),
			primaryKeys.contains(
				StringBundler.concat(
					String.class.getName(), StringPool.POUND,
					DLFileEntry.class.getName(), StringPool.POUND,
					fileEntry.getFileEntryId())));
	}

	@Test
	public void testExportDLFileEntryInSameGroup() throws Exception {
		FileEntry fileEntry = DLAppTestUtil.addFileEntry(_layout.getGroupId());

		_setPortletPreferences(fileEntry);

		_exportPortlet();

		Set<String> primaryKeys = _portletDataContextExport.getPrimaryKeys();

		Assert.assertTrue(
			primaryKeys.toString(),
			primaryKeys.contains(
				StringBundler.concat(
					String.class.getName(), StringPool.POUND,
					DLFileEntry.class.getName(), StringPool.POUND,
					fileEntry.getFileEntryId())));
	}

	@Test
	public void testExportHomeFolder() throws Exception {
		FileEntry fileEntry = DLAppTestUtil.addFileEntry(_group.getGroupId());

		_setPortletPreferences(fileEntry);

		_exportPortlet();

		Set<String> primaryKeys = _portletDataContextExport.getPrimaryKeys();

		Assert.assertTrue(
			primaryKeys.toString(),
			primaryKeys.contains(
				_getPrimaryKey(
					DLFileEntry.class.getName(), fileEntry.getFileEntryId())));
	}

	@Test
	public void testExportImportAssetLibraryHomeFolder() throws Exception {
		DepotEntry depotEntry = _addDepotEntry();

		FileEntry fileEntry = DLAppTestUtil.addFileEntry(
			depotEntry.getGroupId());

		_testExportImport(fileEntry);
	}

	@Test
	public void testExportImportAssetLibrarySubfolder() throws Exception {
		DepotEntry depotEntry = _addDepotEntry();

		Folder folder = DLAppTestUtil.addFolder(depotEntry.getGroupId());

		FileEntry fileEntry = DLAppTestUtil.addFileEntry(folder);

		_testExportImport(fileEntry);
	}

	@Test
	public void testExportImportCustomPortletPreference() throws Exception {
		FileEntry fileEntry = DLAppTestUtil.addFileEntry(_group.getGroupId());

		_portletPreferences.setValue(
			"fileEntryId", String.valueOf(fileEntry.getFileEntryId()));

		_portletPreferences.store();

		PortletPreferences importedPortletPreferences =
			_exportImportPortletPreferences();

		String importedfileEntryId = importedPortletPreferences.getValue(
			"fileEntryId", "");

		Assert.assertEquals(
			fileEntry.getFileEntryId(),
			GetterUtil.getLong(importedfileEntryId));
	}

	@Test
	public void testExportImportHomeFolder() throws Exception {
		FileEntry fileEntry = DLAppTestUtil.addFileEntry(_group.getGroupId());

		_testExportImport(fileEntry);
	}

	@Test
	public void testExportImportHomeFolderInAssetLibraryWithStagingInProcessFromLive()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry();

		Group depotLiveGroup = depotEntry.getGroup();

		FileEntry liveFileEntry = DLAppTestUtil.addFileEntry(
			depotLiveGroup.getGroupId());

		GroupTestUtil.enableLocalStaging(depotLiveGroup);

		Group depotStagingGroup = depotLiveGroup.getStagingGroup();

		FileEntry stagingFileEntry = _dlAppLocalService.getFileEntry(
			depotStagingGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			liveFileEntry.getTitle());

		_testExportImportFolderInAssetLibraryWithStagingInProcess(
			_getPortletPreferencesValues(liveFileEntry),
			_getPortletPreferencesValues(stagingFileEntry));
	}

	@Test
	public void testExportImportHomeFolderInAssetLibraryWithStagingInProcessFromStaging()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry();

		Group depotLiveGroup = depotEntry.getGroup();

		FileEntry liveFileEntry = DLAppTestUtil.addFileEntry(
			depotLiveGroup.getGroupId());

		GroupTestUtil.enableLocalStaging(depotLiveGroup);

		Group depotStagingGroup = depotLiveGroup.getStagingGroup();

		FileEntry stagingFileEntry = _dlAppLocalService.getFileEntry(
			depotStagingGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			liveFileEntry.getTitle());

		_testExportImportFolderInAssetLibraryWithStagingInProcess(
			_getPortletPreferencesValues(stagingFileEntry),
			_getPortletPreferencesValues(liveFileEntry));
	}

	@Test
	public void testExportImportSubfolder() throws Exception {
		Folder folder = DLAppTestUtil.addFolder(_group.getGroupId());

		FileEntry fileEntry = DLAppTestUtil.addFileEntry(folder);

		_testExportImport(fileEntry);
	}

	@Test
	public void testExportImportSubfolderInAssetLibraryWithStagingInProcessFromLive()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry();

		Group depotLiveGroup = depotEntry.getGroup();

		Folder liveFolder = DLAppTestUtil.addFolder(
			depotLiveGroup.getGroupId());

		GroupTestUtil.enableLocalStaging(depotLiveGroup);

		Group depotStagingGroup = depotLiveGroup.getStagingGroup();

		Folder stagingFolder = _dlAppLocalService.getFolder(
			depotStagingGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, liveFolder.getName());

		_testExportImportFolderInAssetLibraryWithStagingInProcess(
			_getPortletPreferencesValues(liveFolder),
			_getPortletPreferencesValues(stagingFolder));
	}

	@Test
	public void testExportImportSubfolderInAssetLibraryWithStagingInProcessFromStaging()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry();

		Group depotLiveGroup = depotEntry.getGroup();

		Folder liveFolder = DLAppTestUtil.addFolder(
			depotLiveGroup.getGroupId());

		GroupTestUtil.enableLocalStaging(depotLiveGroup);

		Group depotStagingGroup = depotLiveGroup.getStagingGroup();

		Folder stagingFolder = _dlAppLocalService.getFolder(
			depotStagingGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, liveFolder.getName());

		_testExportImportFolderInAssetLibraryWithStagingInProcess(
			_getPortletPreferencesValues(stagingFolder),
			_getPortletPreferencesValues(liveFolder));
	}

	@Test
	public void testExportImportSubfolderWithStagingInProcess()
		throws Exception {

		GroupTestUtil.enableLocalStaging(_group);

		ExportImportThreadLocal.setPortletStagingInProcess(true);

		try {
			Group stagingGroup = _group.getStagingGroup();

			Folder folder = DLAppTestUtil.addFolder(stagingGroup.getGroupId());

			TestReaderWriter testReaderWriter = _getTestReaderWriter();

			Map<String, String> portletPreferencesValues =
				_getPortletPreferencesValues(folder);

			_setPortletPreferences(portletPreferencesValues);

			PortletPreferences exportedPortletPreferences =
				_exportImportPortletPreferencesProcessor.
					processExportPortletPreferences(
						_getExportPortletDataContext(
							stagingGroup, testReaderWriter),
						_portletPreferences);

			Assert.assertEquals(
				portletPreferencesValues,
				_getPortletPreferencesValues(exportedPortletPreferences));

			PortletDataContext importPortletDataContext =
				_getImportPortletDataContext(_group, testReaderWriter);

			Assert.assertNull(
				importPortletDataContext.getZipEntryAsString(
					String.format(
						"%s/staging-preferences-mapping.json",
						importPortletDataContext.getPortletId())));

			PortletPreferences importedPortletPreferences =
				_exportImportPortletPreferencesProcessor.
					processImportPortletPreferences(
						importPortletDataContext, exportedPortletPreferences);

			Assert.assertEquals(
				portletPreferencesValues,
				_getPortletPreferencesValues(importedPortletPreferences));
		}
		finally {
			ExportImportThreadLocal.setPortletStagingInProcess(false);
		}
	}

	@Test
	public void testExportRepository() throws Exception {
		Repository repository = DLAppTestUtil.addRepository(
			_group.getGroupId());

		FileEntry fileEntry = DLAppTestUtil.addFileEntry(repository);

		_setPortletPreferences(fileEntry);

		_exportPortlet();

		Set<String> primaryKeys = _portletDataContextExport.getPrimaryKeys();

		Assert.assertFalse(
			primaryKeys.toString(),
			primaryKeys.contains(
				_getPrimaryKey(
					DLFileEntry.class.getName(), fileEntry.getFileEntryId())));
		Assert.assertTrue(
			primaryKeys.toString(),
			primaryKeys.contains(
				_getPrimaryKey(
					DLFolder.class.getName(), fileEntry.getFolderId())));
		Assert.assertTrue(
			primaryKeys.toString(),
			primaryKeys.contains(
				_getPrimaryKey(
					Repository.class.getName(), fileEntry.getRepositoryId())));
	}

	@Test
	public void testExportSubfolder() throws Exception {
		Folder folder = DLAppTestUtil.addFolder(_group.getGroupId());

		FileEntry fileEntry = DLAppTestUtil.addFileEntry(folder);

		_setPortletPreferences(fileEntry);

		_exportPortlet();

		Set<String> primaryKeys = _portletDataContextExport.getPrimaryKeys();

		Assert.assertFalse(
			primaryKeys.toString(),
			primaryKeys.contains(
				_getPrimaryKey(
					DLFileEntry.class.getName(), fileEntry.getFileEntryId())));
		Assert.assertTrue(
			primaryKeys.toString(),
			primaryKeys.contains(
				_getPrimaryKey(
					DLFolder.class.getName(), folder.getFolderId())));
	}

	private DepotEntry _addDepotEntry() throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_depotEntries.add(depotEntry);

		return depotEntry;
	}

	private PortletPreferences _exportImportPortletPreferences()
		throws Exception {

		PortletPreferences exportedPortletPreferences =
			_exportImportPortletPreferencesProcessor.
				processExportPortletPreferences(
					_portletDataContextExport, _portletPreferences);

		return _exportImportPortletPreferencesProcessor.
			processImportPortletPreferences(
				_portletDataContextImport, exportedPortletPreferences);
	}

	private void _exportPortlet() throws Exception {
		Document document = SAXReaderUtil.createDocument();

		Element rootElement = document.addElement("root");

		_portletExportController.exportPortlet(
			_portletDataContextExport, _layout.getPlid(), rootElement, false,
			false, true, true, false);
	}

	private PortletDataContext _getExportPortletDataContext(
			Group group, ZipWriter zipWriter)
		throws Exception {

		PortletDataContext exportPortletDataContext =
			ExportImportTestUtil.getExportPortletDataContext(
				group.getGroupId());

		exportPortletDataContext.setPortletId(DLPortletKeys.DOCUMENT_LIBRARY);
		exportPortletDataContext.setZipWriter(zipWriter);

		return exportPortletDataContext;
	}

	private String _getFolderExternalReferenceCode(long folderId)
		throws Exception {

		if (folderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return StringPool.BLANK;
		}

		Folder folder = _dlAppLocalService.getFolder(folderId);

		return folder.getExternalReferenceCode();
	}

	private String _getGroupExternalReferenceCode(
			long groupId, Repository repository)
		throws Exception {

		Group group;

		if (repository == null) {
			group = _groupLocalService.getGroup(groupId);
		}
		else {
			group = _groupLocalService.getGroup(repository.getGroupId());
		}

		return group.getExternalReferenceCode();
	}

	private PortletDataContext _getImportPortletDataContext(
			Group group, ZipReader zipReader)
		throws Exception {

		PortletDataContext importPortletDataContext =
			ExportImportTestUtil.getImportPortletDataContext(
				group.getGroupId());

		importPortletDataContext.setPortletId(DLPortletKeys.DOCUMENT_LIBRARY);
		importPortletDataContext.setZipReader(zipReader);

		return importPortletDataContext;
	}

	private Map<String, String> _getPortletPreferencesValues(
			FileEntry fileEntry)
		throws Exception {

		Repository repository = _repositoryLocalService.fetchRepository(
			fileEntry.getRepositoryId());

		return _getPortletPreferencesValues(
			_getFolderExternalReferenceCode(fileEntry.getFolderId()),
			_getGroupExternalReferenceCode(
				fileEntry.getRepositoryId(), repository),
			_getRepositoryExternalReferenceCode(repository));
	}

	private Map<String, String> _getPortletPreferencesValues(Folder folder)
		throws Exception {

		Repository repository = _repositoryLocalService.fetchRepository(
			folder.getRepositoryId());

		return _getPortletPreferencesValues(
			folder.getExternalReferenceCode(),
			_getGroupExternalReferenceCode(
				folder.getRepositoryId(), repository),
			_getRepositoryExternalReferenceCode(repository));
	}

	private Map<String, String> _getPortletPreferencesValues(
		PortletPreferences portletPreferences) {

		return _getPortletPreferencesValues(
			portletPreferences.getValue(
				"rootFolderExternalReferenceCode", null),
			portletPreferences.getValue(
				"selectedGroupExternalReferenceCode", null),
			portletPreferences.getValue(
				"selectedRepositoryExternalReferenceCode", null));
	}

	private Map<String, String> _getPortletPreferencesValues(
		String rootFolderExternalReferenceCode,
		String selectedGroupExternalReferenceCode,
		String selectedRepositoryExternalReferenceCode) {

		return HashMapBuilder.put(
			"rootFolderExternalReferenceCode", rootFolderExternalReferenceCode
		).put(
			"selectedGroupExternalReferenceCode",
			selectedGroupExternalReferenceCode
		).put(
			"selectedRepositoryExternalReferenceCode",
			selectedRepositoryExternalReferenceCode
		).build();
	}

	private String _getPrimaryKey(String className, Serializable classPK) {
		return StringBundler.concat(
			String.class.getName(), StringPool.POUND, className,
			StringPool.POUND, classPK);
	}

	private String _getRepositoryExternalReferenceCode(Repository repository) {
		if (repository == null) {
			return StringPool.BLANK;
		}

		return repository.getExternalReferenceCode();
	}

	private TestReaderWriter _getTestReaderWriter() {
		TestReaderWriter testReaderWriter = new TestReaderWriter();

		Document document = SAXReaderUtil.createDocument();

		Element manifestRootElement = document.addElement("root");

		manifestRootElement.addElement("header");

		testReaderWriter.addEntry("/manifest.xml", document.asXML());

		return testReaderWriter;
	}

	private void _setPortletPreferences(FileEntry fileEntry) throws Exception {
		_setPortletPreferences(_getPortletPreferencesValues(fileEntry));
	}

	private void _setPortletPreferences(
			Map<String, String> portletPreferencesValues)
		throws Exception {

		for (Map.Entry<String, String> entry :
				portletPreferencesValues.entrySet()) {

			_portletPreferences.setValue(entry.getKey(), entry.getValue());
		}

		_portletPreferences.store();
	}

	private void _testExportImport(FileEntry fileEntry) throws Exception {
		Map<String, String> portletPreferencesValues =
			_getPortletPreferencesValues(fileEntry);

		_setPortletPreferences(portletPreferencesValues);

		PortletPreferences importedPortletPreferences =
			_exportImportPortletPreferences();

		Assert.assertEquals(
			portletPreferencesValues,
			_getPortletPreferencesValues(importedPortletPreferences));
	}

	private void _testExportImportFolderInAssetLibraryWithStagingInProcess(
			Map<String, String> originalPortletPreferencesValues,
			Map<String, String> expectedPortletPreferencesValues)
		throws Exception {

		ExportImportThreadLocal.setPortletStagingInProcess(true);

		try {
			TestReaderWriter testReaderWriter = _getTestReaderWriter();

			_setPortletPreferences(originalPortletPreferencesValues);

			PortletPreferences exportedPortletPreferences =
				_exportImportPortletPreferencesProcessor.
					processExportPortletPreferences(
						_getExportPortletDataContext(_group, testReaderWriter),
						_portletPreferences);

			Assert.assertEquals(
				originalPortletPreferencesValues,
				_getPortletPreferencesValues(exportedPortletPreferences));

			PortletDataContext importPortletDataContext =
				_getImportPortletDataContext(_group, testReaderWriter);

			Assert.assertNotNull(
				importPortletDataContext.getZipEntryAsString(
					String.format(
						"%s/staging-preferences-mapping.json",
						importPortletDataContext.getPortletId())));

			PortletPreferences importedPortletPreferences =
				_exportImportPortletPreferencesProcessor.
					processImportPortletPreferences(
						importPortletDataContext, exportedPortletPreferences);

			Assert.assertEquals(
				expectedPortletPreferencesValues,
				_getPortletPreferencesValues(importedPortletPreferences));
		}
		finally {
			ExportImportThreadLocal.setPortletStagingInProcess(false);
		}
	}

	@DeleteAfterTestRun
	private final List<DepotEntry> _depotEntries = new ArrayList<>();

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject(filter = "jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY)
	private ExportImportPortletPreferencesProcessor
		_exportImportPortletPreferencesProcessor;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	private Layout _layout;

	@Inject
	private Portal _portal;

	private PortletDataContext _portletDataContextExport;
	private PortletDataContext _portletDataContextImport;

	@Inject
	private PortletExportController _portletExportController;

	private PortletPreferences _portletPreferences;

	@Inject
	private RepositoryLocalService _repositoryLocalService;

}