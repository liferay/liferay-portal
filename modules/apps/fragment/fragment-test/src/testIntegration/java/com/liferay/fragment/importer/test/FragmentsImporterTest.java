/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.importer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
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
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ScopeUtil;
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
import java.util.Collections;
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

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@After
	public void tearDown() throws Exception {
		ServiceContextThreadLocal.popServiceContext();

		FileUtil.delete(_file);

		FileUtil.delete(_resourcesFile);
	}

	@Test
	public void testImportEntriesWithSections() throws Exception {
		_importFragmentsByType(FragmentConstants.TYPE_SECTION);
	}

	@Test
	public void testImportFragmentEntries() throws Exception {
		List<FragmentCollection> fragmentCollections =
			_fragmentCollectionLocalService.getFragmentCollections(
				_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			fragmentCollections.toString(), 0, fragmentCollections.size());

		_fragmentsImporter.importFragmentEntries(
			_user.getUserId(), _group.getGroupId(), 0, _file,
			FragmentsImportStrategy.DO_NOT_OVERWRITE, false);

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
	@TestInfo("LPD-81251")
	public void testImportFragmentEntriesConfigurationJSONObject()
		throws Exception {

		List<FragmentCollection> fragmentCollections =
			_fragmentCollectionLocalService.getFragmentCollections(
				_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			fragmentCollections.toString(), 0, fragmentCollections.size());

		_file = _generateZipFile(_PATH_FRAGMENTS_WITH_FIELD_SETS + "fragments");

		_fragmentsImporter.importFragmentEntries(
			_user.getUserId(), _group.getGroupId(), 0, _file,
			FragmentsImportStrategy.DO_NOT_OVERWRITE, false);

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
				fragmentEntry.getName(), "Fragment With Field Sets"));

		Assert.assertEquals(
			filteredFragmentEntries.toString(), 1,
			filteredFragmentEntries.size());

		FragmentEntry fragmentEntry = filteredFragmentEntries.get(0);

		JSONObject configurationJSONObject = JSONFactoryUtil.createJSONObject(
			fragmentEntry.getConfiguration());

		JSONArray fieldSetsJSONArray = configurationJSONObject.getJSONArray(
			"fieldSets");

		JSONObject fieldSetJSONObject = fieldSetsJSONArray.getJSONObject(0);

		Assert.assertEquals(
			"{SimpleInputField} from @liferay/fragment-impl/api",
			fieldSetJSONObject.getString("customComponentModule"));
	}

	@Test
	@TestInfo("LPD-98539")
	public void testImportFragmentEntriesIntoDesignLibrarySkipsCompositions()
		throws Exception {

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			Collections.emptyMap(), DepotConstants.TYPE_DESIGN_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		List<FragmentsImporterResultEntry> fragmentsImporterResultEntries =
			_fragmentsImporter.importFragmentEntries(
				_user.getUserId(), depotEntry.getGroupId(), 0, _file,
				FragmentsImportStrategy.DO_NOT_OVERWRITE, false);

		_assertFragmentsImporterResultEntries(
			ListUtil.filter(
				fragmentsImporterResultEntries,
				fragmentsImporterResultEntry -> Objects.equals(
					fragmentsImporterResultEntry.getType(),
					FragmentsImporterResultEntry.Type.COMPOSITION)),
			FragmentsImporterResultEntry.Status.INVALID,
			FragmentsImporterResultEntry.Type.COMPOSITION);
		_assertFragmentsImporterResultEntries(
			ListUtil.filter(
				fragmentsImporterResultEntries,
				fragmentsImporterResultEntry -> ArrayUtil.contains(
					new String[] {
						"Fragment", "Fragment With Icon",
						"Input Fragment With Type Options"
					},
					fragmentsImporterResultEntry.getName())),
			FragmentsImporterResultEntry.Status.IMPORTED,
			FragmentsImporterResultEntry.Type.FRAGMENT);
	}

	@Test
	public void testImportFragmentEntriesSystem() throws Exception {
		List<FragmentCollection> fragmentCollections =
			_fragmentCollectionLocalService.getFragmentCollections(
				CompanyConstants.SYSTEM, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		long initialFragmentCollectionsCount = fragmentCollections.size();

		_fragmentsImporter.importFragmentEntries(
			_user.getUserId(), CompanyConstants.SYSTEM, 0, _file,
			FragmentsImportStrategy.DO_NOT_OVERWRITE, false);

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
	@TestInfo("LPD-83633")
	public void testImportFragmentEntriesWithFolderResources()
		throws Exception {

		_testImportFragmentEntriesWithFolderResources(
			false,
			HashMapBuilder.put(
				"folder1/image2.png", "image2.png"
			).put(
				"image1.png", "image1.png"
			).build(),
			HashMapBuilder.put(
				"folder1/image2 (1).png", "image2 (1).png"
			).put(
				"folder1/image2.png", "image2.png"
			).put(
				"image1 (1).png", "image1 (1).png"
			).put(
				"image1.png", "image1.png"
			).build());
		_testImportFragmentEntriesWithFolderResources(
			true,
			HashMapBuilder.put(
				"folder1/image2.png", "image2.png"
			).put(
				"image1.png", "image1.png"
			).build(),
			HashMapBuilder.put(
				"folder1/image2.png", "image2.png"
			).put(
				"image1.png", "image1.png"
			).build());
	}

	@Test
	public void testImportFragmentEntriesWithIcon() throws Exception {
		List<FragmentCollection> fragmentCollections =
			_fragmentCollectionLocalService.getFragmentCollections(
				_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			fragmentCollections.toString(), 0, fragmentCollections.size());

		_fragmentsImporter.importFragmentEntries(
			_user.getUserId(), _group.getGroupId(), 0, _file,
			FragmentsImportStrategy.DO_NOT_OVERWRITE, false);

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
	public void testImportFragmentEntriesWithInvalidConfiguration()
		throws Exception {

		_fragmentsImporter.importFragmentEntries(
			_user.getUserId(), _group.getGroupId(), 0, _file,
			FragmentsImportStrategy.DO_NOT_OVERWRITE, false);

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
	public void testImportFragmentEntriesWithInvalidHTML() throws Exception {
		_fragmentsImporter.importFragmentEntries(
			_user.getUserId(), _group.getGroupId(), 0, _file,
			FragmentsImportStrategy.DO_NOT_OVERWRITE, false);

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
	public void testImportFragmentEntriesWithInvalidReactConfiguration()
		throws Exception {

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

	@Test
	public void testImportFragmentEntriesWithName() throws Exception {
		List<FragmentCollection> fragmentCollections =
			_fragmentCollectionLocalService.getFragmentCollections(
				_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			fragmentCollections.toString(), 0, fragmentCollections.size());

		_file = _generateZipFile(
			_PATH_FRAGMENTS_WITH_UPDATED_NAME + "import-1/fragments");

		_fragmentsImporter.importFragmentEntries(
			_user.getUserId(), _group.getGroupId(), 0, _file,
			FragmentsImportStrategy.DO_NOT_OVERWRITE, false);

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

		_fragmentsImporter.importFragmentEntries(
			_user.getUserId(), _group.getGroupId(), 0, _file,
			FragmentsImportStrategy.OVERWRITE, false);

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
	public void testImportFragmentEntriesWithReservedNames() throws Exception {
		_fragmentsImporter.importFragmentEntries(
			_user.getUserId(), _group.getGroupId(), 0, _file,
			FragmentsImportStrategy.DO_NOT_OVERWRITE, false);

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
	@TestInfo("LPS-151013")
	public void testImportFragmentEntriesWithResources() throws Exception {
		_testImportFragmentEntriesWithResources(
			2, false, "[resources:image (1).png]");
	}

	@Test
	@TestInfo("LPS-151013")
	public void testImportFragmentEntriesWithResourcesPropagation()
		throws Exception {

		_testImportFragmentEntriesWithResources(
			1, true, "[resources:image.png]");
	}

	@Test
	public void testImportFragmentEntriesWithThumbnail() throws Exception {
		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						_group.getCompanyId(),
						FragmentServiceConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"propagateChanges", true
						).build())) {

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
					ScopeUtil.getItemScopeExternalReferenceCode(
						fragmentEntry.getGroupId(), _group.getGroupId()),
					0, 0, fragmentEntry.getCss(), fragmentEntry.getHtml(),
					fragmentEntry.getJs(), fragmentEntry.getConfiguration(),
					StringPool.BLANK, StringPool.BLANK, 0, StringPool.BLANK, 0,
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
	}

	@Test
	public void testImportFragmentEntriesWithTypeComponent() throws Exception {
		_importFragmentsByType(FragmentConstants.TYPE_COMPONENT);
	}

	@Test
	@TestInfo("LPS-96113")
	public void testImportFragmentEntriesWithValidation() throws Exception {
		_fragmentsImporter.importFragmentEntries(
			_user.getUserId(), _group.getGroupId(), 0,
			_generateZipFile(
				_PATH_DEPENDENCIES + "fragments-collection/collection-name"),
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

	@Test
	@TestInfo("LPD-91226")
	public void testImportFragmentResourcesPreservesIdentifiersWithPropagation()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						_group.getCompanyId(),
						FragmentServiceConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"propagateChanges", true
						).build())) {

			_fragmentsImporter.importFragmentEntries(
				_user.getUserId(), _group.getGroupId(), 0, _resourcesFile,
				FragmentsImportStrategy.OVERWRITE, false);

			List<FragmentCollection> fragmentCollections =
				_fragmentCollectionLocalService.getFragmentCollections(
					_group.getGroupId(), 0, 1);

			FragmentCollection fragmentCollection = fragmentCollections.get(0);

			List<FileEntry> resources = fragmentCollection.getResources();

			Assert.assertEquals(resources.toString(), 1, resources.size());

			FileEntry originalFileEntry = resources.get(0);

			_fragmentsImporter.importFragmentEntries(
				_user.getUserId(), _group.getGroupId(), 0, _resourcesFile,
				FragmentsImportStrategy.OVERWRITE, false);

			resources = fragmentCollection.getResources();

			Assert.assertEquals(resources.toString(), 1, resources.size());

			FileEntry reimportedFileEntry = resources.get(0);

			Assert.assertEquals(
				originalFileEntry.getUuid(), reimportedFileEntry.getUuid());
			Assert.assertEquals(
				originalFileEntry.getExternalReferenceCode(),
				reimportedFileEntry.getExternalReferenceCode());
			Assert.assertEquals(
				originalFileEntry.getFileEntryId(),
				reimportedFileEntry.getFileEntryId());
		}
	}

	@Test
	public void testImportInputFragmentEntriesWithTypeOptions()
		throws Exception {

		List<FragmentCollection> fragmentCollections =
			_fragmentCollectionLocalService.getFragmentCollections(
				_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			fragmentCollections.toString(), 0, fragmentCollections.size());

		_fragmentsImporter.importFragmentEntries(
			_user.getUserId(), _group.getGroupId(), 0, _file,
			FragmentsImportStrategy.DO_NOT_OVERWRITE, false);

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
	public void testImportInvalidEntriesWithFragmentComposition()
		throws Exception {

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

	private void _assertFragmentsImporterResultEntries(
		List<FragmentsImporterResultEntry> fragmentsImporterResultEntries,
		FragmentsImporterResultEntry.Status status,
		FragmentsImporterResultEntry.Type type) {

		Assert.assertFalse(
			fragmentsImporterResultEntries.toString(),
			fragmentsImporterResultEntries.isEmpty());

		for (FragmentsImporterResultEntry fragmentsImporterResultEntry :
				fragmentsImporterResultEntries) {

			Assert.assertEquals(
				fragmentsImporterResultEntry.toString(), status,
				fragmentsImporterResultEntry.getStatus());
			Assert.assertEquals(
				fragmentsImporterResultEntry.toString(), type,
				fragmentsImporterResultEntry.getType());
		}
	}

	private void _deleteFragmentCollections(
			List<FragmentCollection> fragmentCollections)
		throws Exception {

		for (FragmentCollection fragmentCollection : fragmentCollections) {
			_fragmentCollectionLocalService.deleteFragmentCollection(
				fragmentCollection);
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

		_addZipWriterEntry(
			zipWriter, _PATH_DEPENDENCIES + "fragments-with-folder-resources",
			FragmentExportImportConstants.FILE_NAME_COLLECTION);
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
		_fragmentsImporter.importFragmentEntries(
			_user.getUserId(), _group.getGroupId(), 0, _file,
			FragmentsImportStrategy.DO_NOT_OVERWRITE, false);

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

	private void _testImportFragmentEntriesWithFolderResources(
			boolean propagateChanges,
			Map<String, String>... expectedResourcesMaps)
		throws Exception {

		File zipFile = _generateZipFileWithFolderResources();

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						_group.getCompanyId(),
						FragmentServiceConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"propagateChanges", propagateChanges
						).build())) {

			for (Map<String, String> expectedResourcesMap :
					expectedResourcesMaps) {

				_testImportFragmentEntriesWithFolderResources(
					expectedResourcesMap, zipFile);
			}

			_deleteFragmentCollections(
				_fragmentCollectionLocalService.getFragmentCollections(
					_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS));
		}
		finally {
			FileUtil.delete(zipFile);
		}
	}

	private void _testImportFragmentEntriesWithFolderResources(
			Map<String, String> expectedResourcesMap, File zipFile)
		throws Exception {

		_fragmentsImporter.importFragmentEntries(
			_user.getUserId(), _group.getGroupId(), 0, zipFile,
			FragmentsImportStrategy.OVERWRITE, false);

		List<FragmentCollection> fragmentCollections =
			_fragmentCollectionLocalService.getFragmentCollections(
				_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		FragmentCollection fragmentCollection = fragmentCollections.get(0);

		Map<String, FileEntry> resourcesMap =
			fragmentCollection.getResourcesMap();

		Assert.assertEquals(
			resourcesMap.toString(), expectedResourcesMap.size(),
			resourcesMap.size());

		for (Map.Entry<String, String> entry :
				expectedResourcesMap.entrySet()) {

			FileEntry fileEntry = resourcesMap.get(entry.getKey());

			Assert.assertNotNull(fileEntry);
			Assert.assertEquals(entry.getValue(), fileEntry.getTitle());
		}
	}

	private void _testImportFragmentEntriesWithResources(
			int expectedNumberOfResources, boolean propagateChanges,
			String resourceReference)
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						_group.getCompanyId(),
						FragmentServiceConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"propagateChanges", propagateChanges
						).build())) {

			_fragmentsImporter.importFragmentEntries(
				_user.getUserId(), _group.getGroupId(), 0, _resourcesFile,
				FragmentsImportStrategy.OVERWRITE, false);

			List<FragmentCollection> fragmentCollections =
				_fragmentCollectionLocalService.getFragmentCollections(
					_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			FragmentCollection fragmentCollection = fragmentCollections.get(0);

			List<FileEntry> fileEntries = fragmentCollection.getResources();

			Assert.assertEquals(fileEntries.toString(), 1, fileEntries.size());

			_fragmentsImporter.importFragmentEntries(
				_user.getUserId(), _group.getGroupId(), 0, _resourcesFile,
				FragmentsImportStrategy.OVERWRITE, false);

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
	}

	private static final String _PATH_DEPENDENCIES =
		"com/liferay/fragment/dependencies/";

	private static final String _PATH_FRAGMENTS =
		_PATH_DEPENDENCIES + "fragments/";

	private static final String _PATH_FRAGMENTS_WITH_FIELD_SETS =
		_PATH_DEPENDENCIES + "fragments-with-field-sets/";

	private static final String _PATH_FRAGMENTS_WITH_FOLDER_RESOURCES =
		_PATH_DEPENDENCIES + "fragments-with-folder-resources/";

	private static final String _PATH_FRAGMENTS_WITH_UPDATED_NAME =
		_PATH_DEPENDENCIES + "fragments-with-updated-name/";

	private static final String _PATH_RESOURCES_COLLECTION =
		_PATH_DEPENDENCIES + "resources-collection/";

	private Bundle _bundle;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

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