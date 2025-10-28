/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.importer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.configuration.FragmentServiceConfiguration;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.constants.FragmentExportImportConstants;
import com.liferay.fragment.importer.FragmentsImportStrategy;
import com.liferay.fragment.importer.FragmentsImporter;
import com.liferay.fragment.importer.FragmentsImporterResultEntry;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.fragment.util.comparator.FragmentEntryCreateDateComparator;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactory;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.File;
import java.io.InputStream;

import java.net.URL;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class FragmentsImporterTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_bundle = FrameworkUtil.getBundle(getClass());

		_group = GroupTestUtil.addGroup();

		_user = TestPropsValues.getUser();

		_file = _generateZipFile(_PATH_DEPENDENCIES + "fragments");

		_resourcesFile = _generateResourcesZipFile();
	}

	@After
	public void tearDown() throws Exception {
		FileUtil.delete(_file);
	}

	@Test
	public void testImportComponents() throws Exception {
		_importFragmentsByType(FragmentConstants.TYPE_COMPONENT);
	}

	@Test
	@TestInfo("LPS-151013")
	public void testImportFragmentResourcesCreatesNewResourceWithoutPropagation()
		throws Exception {

		_testResources(2, "[resources:image (1).png]");
	}

	@Test
	@TestInfo("LPS-151013")
	public void testImportFragmentResourcesCreatesNoNewResourceWithPropagation()
		throws Exception {

		_configurationProvider.saveCompanyConfiguration(
			FragmentServiceConfiguration.class, _group.getCompanyId(),
			HashMapDictionaryBuilder.<String, Object>put(
				"propagateChanges", true
			).build());

		try {
			_testResources(1, "[resources:image.png]");
		}
		finally {
			_configurationProvider.saveCompanyConfiguration(
				FragmentServiceConfiguration.class, _group.getCompanyId(),
				HashMapDictionaryBuilder.<String, Object>put(
					"propagateChanges", false
				).build());
		}
	}

	@Test
	public void testImportFragments() throws Exception {
		List<FragmentCollection> fragmentCollections =
			_fragmentCollectionLocalService.getFragmentCollections(
				_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			fragmentCollections.toString(), 0, fragmentCollections.size());

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		try {
			_fragmentsImporter.importFragmentEntries(
				_user.getUserId(), _group.getGroupId(), 0, _file,
				FragmentsImportStrategy.DO_NOT_OVERWRITE, false);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}

		fragmentCollections =
			_fragmentCollectionLocalService.getFragmentCollections(
				_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			fragmentCollections.toString(), 1, fragmentCollections.size());

		FragmentCollection fragmentCollection = fragmentCollections.get(0);

		List<FragmentEntry> fragmentEntries =
			_fragmentEntryLocalService.getFragmentEntries(
				fragmentCollection.getFragmentCollectionId());

		Assert.assertFalse(fragmentEntries.isEmpty());
	}

	@Test
	public void testImportFragmentsSystemWide() throws Exception {
		List<FragmentCollection> fragmentCollections =
			_fragmentCollectionLocalService.getFragmentCollections(
				CompanyConstants.SYSTEM, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		long initialFragmentCollectionsCount = fragmentCollections.size();

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(_user.getCompanyId());

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			_fragmentsImporter.importFragmentEntries(
				_user.getUserId(), CompanyConstants.SYSTEM, 0, _file,
				FragmentsImportStrategy.DO_NOT_OVERWRITE, false);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}

		fragmentCollections =
			_fragmentCollectionLocalService.getFragmentCollections(
				CompanyConstants.SYSTEM, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			fragmentCollections.toString(), initialFragmentCollectionsCount + 1,
			fragmentCollections.size());

		FragmentCollection fragmentCollection = fragmentCollections.get(0);

		List<FragmentEntry> fragmentEntries =
			_fragmentEntryLocalService.getFragmentEntries(
				fragmentCollection.getFragmentCollectionId());

		Assert.assertFalse(fragmentEntries.isEmpty());
	}

	@Test
	public void testImportFragmentsWithFolderResources() throws Exception {
		File fileWithFolderResources = _generateZipFileWithFolderResources();

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		try {
			_fragmentsImporter.importFragmentEntries(
				_user.getUserId(), _group.getGroupId(), 0,
				fileWithFolderResources, FragmentsImportStrategy.OVERWRITE,
				false);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}

		List<FragmentCollection> fragmentCollections =
			_fragmentCollectionLocalService.getFragmentCollections(
				_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			fragmentCollections.toString(), 1, fragmentCollections.size());

		FragmentCollection fragmentCollection = fragmentCollections.get(0);

		Map<String, FileEntry> resourcesMap =
			fragmentCollection.getResourcesMap();

		Assert.assertEquals(resourcesMap.toString(), 2, resourcesMap.size());

		Assert.assertNotNull(resourcesMap.get("image1.png"));
		Assert.assertNotNull(resourcesMap.get("folder1/image2.png"));

		FileEntry fileEntry = resourcesMap.get("image1.png");

		Assert.assertEquals("image1.png", fileEntry.getTitle());

		fileEntry = resourcesMap.get("folder1/image2.png");

		Assert.assertEquals("image2.png", fileEntry.getTitle());

		FileUtil.delete(fileWithFolderResources);
	}

	@Test
	public void testImportFragmentsWithReservedNames() throws Exception {
		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		try {
			_fragmentsImporter.importFragmentEntries(
				_user.getUserId(), _group.getGroupId(), 0, _file,
				FragmentsImportStrategy.DO_NOT_OVERWRITE, false);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}

		List<FragmentCollection> fragmentCollections =
			_fragmentCollectionLocalService.getFragmentCollections(
				_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		FragmentCollection fragmentCollection = fragmentCollections.get(0);

		List<String> fragmentEntryNames = TransformUtil.transform(
			_fragmentEntryLocalService.getFragmentEntries(
				fragmentCollection.getFragmentCollectionId()),
			FragmentEntry::getFragmentEntryKey);

		Assert.assertTrue(fragmentEntryNames.contains("resource"));
	}

	@Test
	public void testImportFragmentsWithThumbnailPathAndPropagation()
		throws Exception {

		_configurationProvider.saveCompanyConfiguration(
			FragmentServiceConfiguration.class, _group.getCompanyId(),
			HashMapDictionaryBuilder.<String, Object>put(
				"propagateChanges", true
			).build());

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		try {
			_fragmentsImporter.importFragmentEntries(
				_user.getUserId(), _group.getGroupId(), 0, _file,
				FragmentsImportStrategy.OVERWRITE, false);

			FragmentEntry fragmentEntry =
				_fragmentEntryLocalService.fetchFragmentEntry(
					_group.getGroupId(), "heading");

			FragmentEntryLink fragmentEntryLink =
				_fragmentEntryLinkLocalService.addFragmentEntryLink(
					null, _user.getUserId(), _group.getGroupId(), null,
					fragmentEntry.getExternalReferenceCode(),
					fragmentEntry.getScopeERC(), 0, 0, fragmentEntry.getCss(),
					fragmentEntry.getHtml(), fragmentEntry.getJs(),
					fragmentEntry.getConfiguration(), StringPool.BLANK,
					StringPool.BLANK, 0, StringPool.BLANK, 0,
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId()));

			Assert.assertTrue(fragmentEntryLink.isLatestVersion());

			_fragmentsImporter.importFragmentEntries(
				_user.getUserId(), _group.getGroupId(), 0, _file,
				FragmentsImportStrategy.OVERWRITE, false);

			fragmentEntryLink =
				_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
					fragmentEntryLink.getFragmentEntryLinkId());

			Assert.assertTrue(fragmentEntryLink.isLatestVersion());
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();

			_configurationProvider.saveCompanyConfiguration(
				FragmentServiceConfiguration.class, _group.getCompanyId(),
				HashMapDictionaryBuilder.<String, Object>put(
					"propagateChanges", false
				).build());
		}
	}

	@Test
	public void testImportFragmentWithIcon() throws Exception {
		List<FragmentCollection> fragmentCollections =
			_fragmentCollectionLocalService.getFragmentCollections(
				_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			fragmentCollections.toString(), 0, fragmentCollections.size());

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		try {
			_fragmentsImporter.importFragmentEntries(
				_user.getUserId(), _group.getGroupId(), 0, _file,
				FragmentsImportStrategy.DO_NOT_OVERWRITE, false);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}

		fragmentCollections =
			_fragmentCollectionLocalService.getFragmentCollections(
				_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			fragmentCollections.toString(), 1, fragmentCollections.size());

		FragmentCollection fragmentCollection = fragmentCollections.get(0);

		List<FragmentEntry> filteredFragmentEntries = ListUtil.filter(
			_fragmentEntryLocalService.getFragmentEntries(
				fragmentCollection.getFragmentCollectionId()),
			fragmentEntry -> Objects.equals(
				fragmentEntry.getName(), "Fragment With Icon"));

		Assert.assertEquals(
			filteredFragmentEntries.toString(), 1,
			filteredFragmentEntries.size());

		FragmentEntry headingFragmentEntry = filteredFragmentEntries.get(0);

		Assert.assertEquals("heading", headingFragmentEntry.getIcon());
	}

	@Test
	public void testImportFragmentWithInvalidConfiguration() throws Exception {
		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		try {
			_fragmentsImporter.importFragmentEntries(
				_user.getUserId(), _group.getGroupId(), 0, _file,
				FragmentsImportStrategy.DO_NOT_OVERWRITE, false);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}

		List<FragmentCollection> fragmentCollections =
			_fragmentCollectionLocalService.getFragmentCollections(
				_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		FragmentCollection fragmentCollection = fragmentCollections.get(0);

		List<FragmentEntry> filteredFragmentEntries = ListUtil.filter(
			_fragmentEntryLocalService.getFragmentEntries(
				fragmentCollection.getFragmentCollectionId()),
			fragmentEntry -> Objects.equals(
				fragmentEntry.getName(),
				"Fragment With Invalid Configuration"));

		Assert.assertEquals(
			filteredFragmentEntries.toString(), 1,
			filteredFragmentEntries.size());

		FragmentEntry fragmentEntry = filteredFragmentEntries.get(0);

		Assert.assertTrue(fragmentEntry.isDraft());
	}

	@Test
	public void testImportFragmentWithInvalidHTML() throws Exception {
		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		try {
			_fragmentsImporter.importFragmentEntries(
				_user.getUserId(), _group.getGroupId(), 0, _file,
				FragmentsImportStrategy.DO_NOT_OVERWRITE, false);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}

		List<FragmentCollection> fragmentCollections =
			_fragmentCollectionLocalService.getFragmentCollections(
				_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		FragmentCollection fragmentCollection = fragmentCollections.get(0);

		List<FragmentEntry> filteredFragmentEntries = ListUtil.filter(
			_fragmentEntryLocalService.getFragmentEntries(
				fragmentCollection.getFragmentCollectionId()),
			fragmentEntry -> Objects.equals(
				fragmentEntry.getName(), "Fragment With Invalid HTML"));

		Assert.assertEquals(
			filteredFragmentEntries.toString(), 1,
			filteredFragmentEntries.size());

		FragmentEntry fragmentEntry = filteredFragmentEntries.get(0);

		Assert.assertTrue(fragmentEntry.isDraft());
	}

	@Test
	public void testImportFragmentWithUpdatedName() throws Exception {
		List<FragmentCollection> fragmentCollections =
			_fragmentCollectionLocalService.getFragmentCollections(
				_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			fragmentCollections.toString(), 0, fragmentCollections.size());

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_file = _generateZipFile(
			_PATH_FRAGMENTS_WITH_UPDATED_NAME + "import-1/fragments");

		try {
			_fragmentsImporter.importFragmentEntries(
				_user.getUserId(), _group.getGroupId(), 0, _file,
				FragmentsImportStrategy.DO_NOT_OVERWRITE, false);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}

		fragmentCollections =
			_fragmentCollectionLocalService.getFragmentCollections(
				_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			fragmentCollections.toString(), 1, fragmentCollections.size());

		FragmentCollection fragmentCollection = fragmentCollections.get(0);

		List<FragmentEntry> filteredFragmentEntries = ListUtil.filter(
			_fragmentEntryLocalService.getFragmentEntries(
				fragmentCollection.getFragmentCollectionId()),
			fragmentEntry -> Objects.equals(
				fragmentEntry.getName(), "Fragment One"));

		FragmentEntry filteredFragmentEntry = filteredFragmentEntries.get(0);

		String fragmentEntryKey = "fragment-one";

		Assert.assertEquals(
			fragmentEntryKey, filteredFragmentEntry.getFragmentEntryKey());

		Assert.assertEquals(
			filteredFragmentEntries.toString(), 1,
			filteredFragmentEntries.size());

		_file = _generateZipFile(
			_PATH_FRAGMENTS_WITH_UPDATED_NAME + "import-2/fragments");

		try {
			_fragmentsImporter.importFragmentEntries(
				_user.getUserId(), _group.getGroupId(), 0, _file,
				FragmentsImportStrategy.OVERWRITE, false);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}

		fragmentCollections =
			_fragmentCollectionLocalService.getFragmentCollections(
				_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			fragmentCollections.toString(), 1, fragmentCollections.size());

		fragmentCollection = fragmentCollections.get(0);

		filteredFragmentEntries = ListUtil.filter(
			_fragmentEntryLocalService.getFragmentEntries(
				fragmentCollection.getFragmentCollectionId()),
			fragmentEntry -> Objects.equals(
				fragmentEntry.getName(), "Fragment One Updated"));

		Assert.assertEquals(
			filteredFragmentEntries.toString(), 1,
			filteredFragmentEntries.size());

		filteredFragmentEntry = filteredFragmentEntries.get(0);

		Assert.assertEquals(
			fragmentEntryKey, filteredFragmentEntry.getFragmentEntryKey());
	}

	@Test
	public void testImportInputFragmentWithTypeOptions() throws Exception {
		List<FragmentCollection> fragmentCollections =
			_fragmentCollectionLocalService.getFragmentCollections(
				_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			fragmentCollections.toString(), 0, fragmentCollections.size());

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		try {
			_fragmentsImporter.importFragmentEntries(
				_user.getUserId(), _group.getGroupId(), 0, _file,
				FragmentsImportStrategy.DO_NOT_OVERWRITE, false);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}

		fragmentCollections =
			_fragmentCollectionLocalService.getFragmentCollections(
				_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			fragmentCollections.toString(), 1, fragmentCollections.size());

		FragmentCollection fragmentCollection = fragmentCollections.get(0);

		List<FragmentEntry> filteredFragmentEntries = ListUtil.filter(
			_fragmentEntryLocalService.getFragmentEntries(
				fragmentCollection.getFragmentCollectionId()),
			fragmentEntry -> Objects.equals(
				fragmentEntry.getName(), "Input Fragment With Type Options"));

		Assert.assertEquals(
			filteredFragmentEntries.toString(), 1,
			filteredFragmentEntries.size());

		FragmentEntry fragmentEntry = filteredFragmentEntries.get(0);

		Assert.assertNotNull(fragmentEntry.getTypeOptions());

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			fragmentEntry.getTypeOptions());

		JSONArray jsonArray = jsonObject.getJSONArray("fieldTypes");

		Assert.assertNotNull(jsonArray);
		Assert.assertEquals(1, jsonArray.length());

		String fieldType = jsonArray.getString(0);

		Assert.assertEquals("string", fieldType);
	}

	@Test
	@TestInfo("LPS-188478")
	public void testImportInvalidFragmentComposition() throws Exception {
		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		try {
			List<FragmentsImporterResultEntry>
				filteredFragmentsImporterResultEntries = ListUtil.filter(
					_fragmentsImporter.importFragmentEntries(
						_user.getUserId(), _group.getGroupId(), 0, _file,
						FragmentsImportStrategy.DO_NOT_OVERWRITE, false),
					fragmentsImporterResultEntry -> Objects.equals(
						fragmentsImporterResultEntry.getName(),
						"Fragment./Composition"));

			Assert.assertEquals(
				filteredFragmentsImporterResultEntries.toString(), 1,
				filteredFragmentsImporterResultEntries.size());

			FragmentsImporterResultEntry fragmentsImporterResultEntry =
				filteredFragmentsImporterResultEntries.get(0);

			Assert.assertEquals(
				FragmentsImporterResultEntry.Status.INVALID,
				fragmentsImporterResultEntry.getStatus());
			Assert.assertEquals(
				FragmentsImporterResultEntry.Type.COMPOSITION,
				fragmentsImporterResultEntry.getType());
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	public void testImportReactFragmentWithInvalidConfiguration()
		throws Exception {

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		try {
			List<FragmentsImporterResultEntry>
				filteredFragmentsImporterResultEntries = ListUtil.filter(
					_fragmentsImporter.importFragmentEntries(
						_user.getUserId(), _group.getGroupId(), 0, _file,
						FragmentsImportStrategy.DO_NOT_OVERWRITE, false),
					fragmentsImporterResultEntry -> Objects.equals(
						fragmentsImporterResultEntry.getName(),
						"React Fragment With Invalid Configuration"));

			Assert.assertEquals(
				filteredFragmentsImporterResultEntries.toString(), 1,
				filteredFragmentsImporterResultEntries.size());

			FragmentsImporterResultEntry fragmentsImporterResultEntry =
				filteredFragmentsImporterResultEntries.get(0);

			Assert.assertEquals(
				FragmentsImporterResultEntry.Status.INVALID,
				fragmentsImporterResultEntry.getStatus());
			Assert.assertEquals(
				FragmentsImporterResultEntry.Type.FRAGMENT,
				fragmentsImporterResultEntry.getType());
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	public void testImportSections() throws Exception {
		_importFragmentsByType(FragmentConstants.TYPE_SECTION);
	}

	@Test
	@TestInfo("LPS-96113")
	public void testValidateFragments() throws Exception {
		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		try {
			_fragmentsImporter.importFragmentEntries(
				_user.getUserId(), _group.getGroupId(), 0,
				_generateZipFile(
					_PATH_DEPENDENCIES +
						"fragments-collection/collection-name"),
				FragmentsImportStrategy.DO_NOT_OVERWRITE, false);

			_fragmentsImporter.importFragmentEntries(
				_user.getUserId(), _group.getGroupId(), 0,
				_generateZipFile(
					_PATH_DEPENDENCIES + "fragments-collection/freemarker"),
				FragmentsImportStrategy.DO_NOT_OVERWRITE, false);

			_fragmentsImporter.importFragmentEntries(
				_user.getUserId(), _group.getGroupId(), 0,
				_generateZipFile(
					_PATH_DEPENDENCIES + "fragments-collection/widgets"),
				FragmentsImportStrategy.DO_NOT_OVERWRITE, false);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	private void _addFragmentEntryType(JSONObject jsonObject) {
		int type = FragmentConstants.getTypeFromLabel(
			jsonObject.getString("type"));

		List<String> fragmentEntryKeys = _fragmentEntryTypes.computeIfAbsent(
			type, key -> new ArrayList<>());

		fragmentEntryKeys.add(jsonObject.getString("fragmentEntryKey"));
	}

	private void _addZipWriterEntry(
			ZipWriter zipWriter, String path, String key)
		throws Exception {

		if (Validator.isNull(key)) {
			return;
		}

		String entryPath = path + StringPool.FORWARD_SLASH + key;

		String zipPath = StringUtil.removeSubstring(entryPath, _PATH_FRAGMENTS);

		zipPath = StringUtil.removeSubstring(zipPath, _PATH_DEPENDENCIES);

		URL url = _bundle.getEntry(entryPath);

		try (InputStream inputStream = url.openStream()) {
			zipWriter.addEntry(zipPath, inputStream);
		}
	}

	private File _generateResourcesZipFile() throws Exception {
		ZipWriter zipWriter = _zipWriterFactory.getZipWriter();

		_addZipWriterEntry(
			zipWriter, _PATH_DEPENDENCIES + "resources-collection",
			"collection.json");
		_addZipWriterEntry(
			zipWriter, _PATH_RESOURCES_COLLECTION + "resources", "image.png");
		_populateZipWriter(_PATH_RESOURCES_COLLECTION, zipWriter, false);

		return zipWriter.getFile();
	}

	private File _generateZipFile(String path) throws Exception {
		ZipWriter zipWriter = _zipWriterFactory.getZipWriter();

		URL collectionURL = _bundle.getEntry(
			path + StringPool.FORWARD_SLASH +
				FragmentExportImportConstants.FILE_NAME_COLLECTION);

		try (InputStream inputStream = collectionURL.openStream()) {
			_addZipWriterEntry(
				zipWriter, path,
				FragmentExportImportConstants.FILE_NAME_COLLECTION);
		}

		_populateZipWriter(path, zipWriter, true);

		return zipWriter.getFile();
	}

	private File _generateZipFileWithFolderResources() throws Exception {
		ZipWriter zipWriter = _zipWriterFactory.getZipWriter();

		URL collectionURL = _bundle.getEntry(
			_PATH_FRAGMENTS_WITH_FOLDER_RESOURCES +
				FragmentExportImportConstants.FILE_NAME_COLLECTION);

		try (InputStream inputStream = collectionURL.openStream()) {
			zipWriter.addEntry(
				FragmentExportImportConstants.FILE_NAME_COLLECTION,
				inputStream);
		}

		_addZipWriterEntry(
			zipWriter, _PATH_FRAGMENTS_WITH_FOLDER_RESOURCES + "resources",
			"image1.png");
		_addZipWriterEntry(
			zipWriter,
			_PATH_FRAGMENTS_WITH_FOLDER_RESOURCES + "resources/folder1",
			"image2.png");

		_populateZipWriter(
			_PATH_FRAGMENTS_WITH_FOLDER_RESOURCES, zipWriter, true);

		return zipWriter.getFile();
	}

	private void _importFragmentsByType(int type) throws Exception {
		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		try {
			_fragmentsImporter.importFragmentEntries(
				_user.getUserId(), _group.getGroupId(), 0, _file,
				FragmentsImportStrategy.DO_NOT_OVERWRITE, false);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}

		List<FragmentCollection> fragmentCollections =
			_fragmentCollectionLocalService.getFragmentCollections(
				_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		FragmentCollection fragmentCollection = fragmentCollections.get(0);

		List<FragmentEntry> actualFragmentEntries = ListUtil.filter(
			_fragmentEntryLocalService.getFragmentEntries(
				fragmentCollection.getFragmentCollectionId()),
			fragmentEntry -> fragmentEntry.getType() == type);

		List<String> expectedFragmentsEntries = _fragmentEntryTypes.get(type);

		Assert.assertEquals(
			actualFragmentEntries.toString(), expectedFragmentsEntries.size(),
			actualFragmentEntries.size());
	}

	private void _populateZipWriter(
			String basePath, ZipWriter zipWriter,
			boolean calculateFragmentEntryType)
		throws Exception {

		Enumeration<URL> enumeration = _bundle.findEntries(
			basePath, FragmentExportImportConstants.FILE_NAME_FRAGMENT, true);

		while (enumeration.hasMoreElements()) {
			URL url = enumeration.nextElement();

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
				URLUtil.toString(url));

			if (calculateFragmentEntryType) {
				_addFragmentEntryType(jsonObject);
			}

			String path = FileUtil.getPath(url.getPath());

			_addZipWriterEntry(
				zipWriter, path,
				FragmentExportImportConstants.FILE_NAME_FRAGMENT);
			_addZipWriterEntry(
				zipWriter, path, jsonObject.getString("configurationPath"));
			_addZipWriterEntry(
				zipWriter, path, jsonObject.getString("cssPath"));
			_addZipWriterEntry(
				zipWriter, path, jsonObject.getString("htmlPath"));
			_addZipWriterEntry(zipWriter, path, jsonObject.getString("jsPath"));
			_addZipWriterEntry(
				zipWriter, path, jsonObject.getString("thumbnailPath"));
		}

		enumeration = _bundle.findEntries(
			basePath,
			FragmentExportImportConstants.FILE_NAME_FRAGMENT_COMPOSITION, true);

		if (enumeration == null) {
			return;
		}

		while (enumeration.hasMoreElements()) {
			URL url = enumeration.nextElement();

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
				URLUtil.toString(url));

			String path = FileUtil.getPath(url.getPath());

			_addZipWriterEntry(
				zipWriter, path,
				FragmentExportImportConstants.FILE_NAME_FRAGMENT_COMPOSITION);
			_addZipWriterEntry(
				zipWriter, path,
				jsonObject.getString("fragmentCompositionDefinitionPath"));
		}
	}

	private void _testResources(
			int expectedNumberOfResources, String resourceReference)
		throws Exception {

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		try {
			_fragmentsImporter.importFragmentEntries(
				_user.getUserId(), _group.getGroupId(), 0, _resourcesFile,
				FragmentsImportStrategy.OVERWRITE, false);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}

		List<FragmentCollection> fragmentCollections =
			_fragmentCollectionLocalService.getFragmentCollections(
				_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		FragmentCollection fragmentCollection = fragmentCollections.get(0);

		List<FileEntry> fileEntries = fragmentCollection.getResources();

		Assert.assertEquals(fileEntries.toString(), 1, fileEntries.size());

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		try {
			_fragmentsImporter.importFragmentEntries(
				_user.getUserId(), _group.getGroupId(), 0, _resourcesFile,
				FragmentsImportStrategy.OVERWRITE, false);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}

		fragmentCollections =
			_fragmentCollectionLocalService.getFragmentCollections(
				_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		fragmentCollection = fragmentCollections.get(0);

		fileEntries = fragmentCollection.getResources();

		Assert.assertEquals(
			fileEntries.toString(), expectedNumberOfResources,
			fileEntries.size());

		List<FragmentEntry> fragmentEntries =
			_fragmentEntryLocalService.getFragmentEntries(
				_group.getGroupId(),
				fragmentCollection.getFragmentCollectionId(), "resource",
				QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				FragmentEntryCreateDateComparator.getInstance(true));

		FragmentEntry fragmentEntry = fragmentEntries.get(0);

		String css = fragmentEntry.getCss();

		Assert.assertTrue(css, css.contains(resourceReference));

		String html = fragmentEntry.getHtml();

		Assert.assertTrue(html, html.contains(resourceReference));
	}

	private static final String _PATH_DEPENDENCIES =
		"com/liferay/fragment/dependencies/";

	private static final String _PATH_FRAGMENTS =
		_PATH_DEPENDENCIES + "fragments/";

	private static final String _PATH_FRAGMENTS_WITH_FOLDER_RESOURCES =
		_PATH_DEPENDENCIES + "fragments-with-folder-resources/";

	private static final String _PATH_FRAGMENTS_WITH_UPDATED_NAME =
		_PATH_DEPENDENCIES + "fragments-with-updated-name/";

	private static final String _PATH_RESOURCES_COLLECTION =
		_PATH_DEPENDENCIES + "resources-collection/";

	private Bundle _bundle;

	@Inject
	private ConfigurationProvider _configurationProvider;

	private File _file;

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	private final Map<Integer, List<String>> _fragmentEntryTypes =
		new HashMap<>();

	@Inject
	private FragmentsImporter _fragmentsImporter;

	@DeleteAfterTestRun
	private Group _group;

	private File _resourcesFile;
	private User _user;

	@Inject
	private ZipWriterFactory _zipWriterFactory;

}