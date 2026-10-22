/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.ExportImportGroup;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.model.ExportImportReportEntry;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.constants.LayoutDataItemTypeConstants;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.kernel.zip.ZipReaderFactory;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Petteri Karttunen
 */
@FeatureFlags(
	featureFlags = {
		@FeatureFlag(value = "LPD-57655"), @FeatureFlag("LPD-85946")
	}
)
@RunWith(Arquillian.class)
public class GroupExportImportTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_companyGroup = _stagingGroupHelper.fetchCompanyGroup(
			TestPropsValues.getCompanyId());

		_group = GroupTestUtil.addGroup();
	}

	@After
	public void tearDown() {
		FileUtil.delete(_file);
	}

	@Test
	public void testExportGroups() throws Exception {
		File file = _exportLayouts(_group);

		List<ExportImportGroup> exportImportGroups = _getExportImportGroups(
			file);

		Assert.assertEquals(
			exportImportGroups.toString(), 1, exportImportGroups.size());

		ExportImportGroup exportImportGroup = exportImportGroups.get(0);

		Assert.assertEquals(
			_group.getExternalReferenceCode(),
			exportImportGroup.getExternalReferenceCode());
		Assert.assertEquals(
			_group.getGroupId(), exportImportGroup.getGroupId());
		Assert.assertEquals(
			_group.getDescriptiveName(
				LocaleUtil.fromLanguageId(_group.getDefaultLanguageId())),
			exportImportGroup.getDescriptiveName());

		Assert.assertNull(
			exportImportGroup.getParentGroupExternalReferenceCode());

		String path = exportImportGroup.getPath();

		Assert.assertTrue(
			path,
			path.endsWith(
				_group.getDescriptiveName(
					LocaleUtil.fromLanguageId(_group.getDefaultLanguageId()))));
	}

	@Test
	@TestInfo("LPD-111134")
	public void testExportGroupsWhenGroupIsNotTheInstanceGroup()
		throws Exception {

		_assertExportGroupsIsSkipped(
			_groupLocalService.getCompanyGroup(TestPropsValues.getCompanyId()));
		_assertExportGroupsIsSkipped(GroupTestUtil.addGroup());
	}

	@Test
	public void testExportGroupsWithChildGroup() throws Exception {
		Group childGroup = GroupTestUtil.addGroup(_group.getGroupId());

		List<ExportImportGroup> exportImportGroups = _getExportImportGroups(
			_exportLayouts(_group, childGroup));

		ExportImportGroup childExportImportGroup = _getExportImportGroup(
			exportImportGroups, childGroup.getExternalReferenceCode());

		Assert.assertEquals(
			_group.getExternalReferenceCode(),
			childExportImportGroup.getParentGroupExternalReferenceCode());

		ExportImportGroup exportImportGroup = _getExportImportGroup(
			exportImportGroups, _group.getExternalReferenceCode());

		Assert.assertEquals(1, exportImportGroup.getChildGroupsCount());
	}

	@Test
	public void testExportGroupsWithGroupManifest() throws Exception {
		File file = _exportLayouts(_group);

		try (ZipReader zipReader = _zipReaderFactory.getZipReader(file)) {
			String manifest = _getManifest(
				zipReader, _getGroupManifestPath(_group));

			Assert.assertFalse(manifest.contains("<groups>"));
		}
	}

	@Test
	public void testExportGroupsWithMultipleLayouts() throws Exception {
		Group group2 = GroupTestUtil.addGroup();

		LayoutTestUtil.addTypePortletLayout(_group);
		LayoutTestUtil.addTypePortletLayout(_group);

		File file = _exportLayouts(_group, group2);

		List<ExportImportGroup> exportedGroups = _getExportImportGroups(file);

		Assert.assertEquals(
			exportedGroups.toString(), 2, exportedGroups.size());

		Assert.assertNotNull(
			exportedGroups.toString(),
			_getExportImportGroup(
				exportedGroups, _group.getExternalReferenceCode()));
		Assert.assertNotNull(
			exportedGroups.toString(),
			_getExportImportGroup(
				exportedGroups, group2.getExternalReferenceCode()));
	}

	@Test
	public void testExportGroupsWithSelectedGroup() throws Exception {
		File file = _exportLayouts(_group);

		try (ZipReader zipReader = _zipReaderFactory.getZipReader(file)) {
			String manifest = _getManifest(zipReader);

			Assert.assertTrue(manifest.contains("<groups>"));
			Assert.assertTrue(
				manifest.contains(
					"external-reference-code=\"" +
						_group.getExternalReferenceCode() + "\""));

			Assert.assertNotNull(
				zipReader.getEntryAsInputStream(_getGroupManifestPath(_group)));
		}
	}

	@Test
	public void testExportGroupsWithUnselectedGroup() throws Exception {
		Group group2 = GroupTestUtil.addGroup();

		File file = _exportLayouts(_group);

		try (ZipReader zipReader = _zipReaderFactory.getZipReader(file)) {
			String manifest = _getManifest(zipReader);

			Assert.assertFalse(
				manifest.contains(
					"external-reference-code=\"" +
						group2.getExternalReferenceCode() + "\""));

			Assert.assertNull(
				zipReader.getEntryAsInputStream(_getGroupManifestPath(group2)));
		}
	}

	@Test
	public void testExportGroupsWithoutSelectedGroups() throws Exception {
		File file = _exportLayouts();

		try (ZipReader zipReader = _zipReaderFactory.getZipReader(file)) {
			String manifest = _getManifest(zipReader);

			Assert.assertFalse(manifest.contains("<groups>"));
		}
	}

	@Test
	public void testImportGroupLayoutsIntoOtherGroupWithReferences()
		throws Exception {

		FileEntry fileEntry = _addFileEntry(_group);

		Layout layout1 = LayoutTestUtil.addTypeContentLayout(_group);
		Layout layout2 = LayoutTestUtil.addTypeContentLayout(_group);

		_addItemToLayout(layout1, fileEntry, layout2);

		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.addStyleBookEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), false,
				null, null, RandomTestUtil.randomString(), StringPool.BLANK,
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		layout1 = _layoutLocalService.getLayout(layout1.getPlid());

		layout1.setStyleBookEntryERC(styleBookEntry.getExternalReferenceCode());

		_layoutLocalService.updateLayout(layout1);

		File file = _exportGroupLayouts(_group);

		Group group = GroupTestUtil.addGroup();

		List<ExportImportReportEntry> exportImportReportEntries =
			_importLayoutsWithMissingReferences(
				() -> _importGroupLayouts(file, group));

		_assertMissingReference(
			FileEntry.class.getName(), fileEntry.getExternalReferenceCode(),
			group.getGroupId(), exportImportReportEntries);
		_assertMissingReference(
			StyleBookEntry.class.getName(),
			styleBookEntry.getExternalReferenceCode(), group.getGroupId(),
			exportImportReportEntries);

		Assert.assertNotNull(
			_dlAppLocalService.fetchFileEntryByExternalReferenceCode(
				group.getGroupId(), fileEntry.getExternalReferenceCode()));
		Assert.assertNotNull(
			_styleBookEntryLocalService.
				fetchStyleBookEntryByExternalReferenceCode(
					styleBookEntry.getExternalReferenceCode(),
					group.getGroupId()));
	}

	@Test
	public void testImportGroups() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		File file = _exportLayouts(_group);

		_deleteLayout(layout);

		_importLayouts(file, _getExternalReferenceCodes(_group));

		Assert.assertNotNull(
			_layoutLocalService.fetchLayoutByExternalReferenceCode(
				layout.getExternalReferenceCode(), _group.getGroupId()));
	}

	@Test
	public void testImportGroupsTwice() throws Exception {
		LayoutTestUtil.addTypePortletLayout(_group);

		int layoutsCount = _layoutLocalService.getLayoutsCount(_group, false);

		File file = _exportLayouts(_group);

		_importLayouts(file, _getExternalReferenceCodes(_group));
		_importLayouts(file, _getExternalReferenceCodes(_group));

		Assert.assertEquals(
			layoutsCount, _layoutLocalService.getLayoutsCount(_group, false));
	}

	@Test
	public void testImportGroupsWithChildGroup() throws Exception {
		Group childGroup = GroupTestUtil.addGroup(_group.getGroupId());

		File file = _exportLayouts(_group, childGroup);

		_updateParentGroupId(childGroup, 0);

		_importLayouts(file, _getExternalReferenceCodes(_group, childGroup));

		Group importedChildGroup = _groupLocalService.getGroup(
			childGroup.getGroupId());

		Assert.assertEquals(
			_group.getGroupId(), importedChildGroup.getParentGroupId());
	}

	@Test
	public void testImportGroupsWithMissingGroup() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();

		File file = _exportLayouts(_group);

		ExportImportConfiguration exportImportConfiguration = _importLayouts(
			file, _group.getExternalReferenceCode(), externalReferenceCode);

		List<ExportImportReportEntry> exportImportReportEntries =
			_exportImportReportEntryLocalService.getExportImportReportEntries(
				TestPropsValues.getCompanyId(),
				exportImportConfiguration.getExportImportConfigurationId());

		Assert.assertTrue(
			exportImportReportEntries.toString(),
			ListUtil.exists(
				exportImportReportEntries,
				exportImportReportEntry -> {
					if (Objects.equals(
							externalReferenceCode,
							exportImportReportEntry.
								getClassExternalReferenceCode()) &&
						(exportImportReportEntry.getType() ==
							ExportImportReportEntryConstants.TYPE_ERROR)) {

						return true;
					}

					return false;
				}));
	}

	@Test
	public void testImportGroupsWithReferencesToOtherGroup() throws Exception {
		_testImportGroupsWithReferencesToOtherGroup(
			GroupTestUtil.addGroup(), _group);
		_testImportGroupsWithReferencesToOtherGroup(
			_group, GroupTestUtil.addGroup());
	}

	@Test
	public void testImportGroupsWithUnselectedGroup() throws Exception {
		Group group2 = GroupTestUtil.addGroup();

		Layout layout1 = LayoutTestUtil.addTypePortletLayout(_group);
		Layout layout2 = LayoutTestUtil.addTypePortletLayout(group2);

		File file = _exportLayouts(_group, group2);

		_deleteLayout(layout1);
		_deleteLayout(layout2);

		_importLayouts(file, _getExternalReferenceCodes(_group));

		Assert.assertNotNull(
			_layoutLocalService.fetchLayoutByExternalReferenceCode(
				layout1.getExternalReferenceCode(), _group.getGroupId()));

		Assert.assertNull(
			_layoutLocalService.fetchLayoutByExternalReferenceCode(
				layout2.getExternalReferenceCode(), group2.getGroupId()));
	}

	private FileEntry _addFileEntry(Group group) throws Exception {
		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".jpg", ContentTypes.IMAGE_JPEG,
			RandomTestUtil.randomBytes(), null, null, null,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));
	}

	private void _addItemToLayout(
			Layout layout, FileEntry linkedFileEntry, Layout linkedLayout)
		throws Exception {

		Layout draftLayout = layout.fetchDraftLayout();

		ContentLayoutTestUtil.addItemToLayout(
			JSONUtil.put(
				"link",
				JSONUtil.put(
					"layout",
					JSONUtil.put(
						"groupId", linkedLayout.getGroupId()
					).put(
						"layoutId", linkedLayout.getLayoutId()
					).put(
						"privateLayout", linkedLayout.isPrivateLayout()
					))
			).put(
				"styles",
				JSONUtil.put(
					"backgroundImage",
					JSONUtil.put(
						"classNameId", _portal.getClassNameId(FileEntry.class)
					).put(
						"classPK", linkedFileEntry.getFileEntryId()
					).put(
						"fileEntryId", linkedFileEntry.getFileEntryId()
					))
			).toString(),
			LayoutDataItemTypeConstants.TYPE_CONTAINER, draftLayout,
			_layoutStructureProvider,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid()));

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);
	}

	private void _assertExportGroupsIsSkipped(Group group) throws Exception {
		File file = _exportLayouts(group.getGroupId(), _group);

		try (ZipReader zipReader = _zipReaderFactory.getZipReader(file)) {
			String manifest = _getManifest(zipReader);

			Assert.assertFalse(manifest.contains("<groups>"));

			Assert.assertNull(
				zipReader.getEntryAsInputStream(_getGroupManifestPath(_group)));
		}
		finally {
			FileUtil.delete(file);
		}
	}

	private void _assertMissingReference(
		String expectedClassName, String expectedExternalReferenceCode,
		long expectedGroupId,
		List<ExportImportReportEntry> exportImportReportEntries) {

		Assert.assertTrue(
			ListUtil.exists(
				exportImportReportEntries,
				exportImportReportEntry ->
					(exportImportReportEntry.getClassNameId() ==
						_portal.getClassNameId(expectedClassName)) &&
					Objects.equals(
						exportImportReportEntry.getClassExternalReferenceCode(),
						expectedExternalReferenceCode) &&
					(exportImportReportEntry.getGroupId() == expectedGroupId)));
	}

	private void _deleteLayout(Layout layout) throws Exception {
		_layoutLocalService.deleteLayout(
			layout,
			ServiceContextTestUtil.getServiceContext(
				layout.getGroupId(), TestPropsValues.getUserId()));
	}

	private File _exportGroupLayouts(Group group) throws Exception {
		_file = _exportImportLocalService.exportLayoutsAsFile(
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					TestPropsValues.getUserId(),
					ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildExportLayoutSettingsMap(
							TestPropsValues.getUser(), group.getGroupId(),
							false,
							_exportImportHelper.getAllLayoutIds(
								group.getGroupId(), false),
							_getGroupParameterMap())));

		return _file;
	}

	private File _exportLayouts(Group... groups) throws Exception {
		return _exportLayouts(_companyGroup.getGroupId(), groups);
	}

	private File _exportLayouts(long groupId, Group... groups)
		throws Exception {

		_file = _exportImportLocalService.exportLayoutsAsFile(
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					TestPropsValues.getUserId(),
					ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildExportLayoutSettingsMap(
							TestPropsValues.getUser(), groupId, false, null,
							_getParameterMap(
								_getExternalReferenceCodes(groups)))));

		return _file;
	}

	private ExportImportGroup _getExportImportGroup(
		List<ExportImportGroup> exportImportGroups,
		String externalReferenceCode) {

		for (ExportImportGroup exportedGroup : exportImportGroups) {
			if (externalReferenceCode.equals(
					exportedGroup.getExternalReferenceCode())) {

				return exportedGroup;
			}
		}

		return null;
	}

	private List<ExportImportGroup> _getExportImportGroups(File file)
		throws Exception {

		FileEntry fileEntry = null;

		try (InputStream inputStream = new FileInputStream(file)) {
			fileEntry = _layoutService.addTempFileEntry(
				_companyGroup.getGroupId(),
				GroupExportImportTest.class.getName(),
				RandomTestUtil.randomString() + ".lar", inputStream,
				ContentTypes.APPLICATION_ZIP);
		}

		try {
			return _exportImportHelper.getExportImportGroups(fileEntry);
		}
		finally {
			_layoutService.deleteTempFileEntry(
				_companyGroup.getGroupId(),
				GroupExportImportTest.class.getName(), fileEntry.getFileName());
		}
	}

	private String[] _getExternalReferenceCodes(Group... groups) {
		return TransformUtil.transformToArray(
			Arrays.asList(groups), Group::getExternalReferenceCode,
			String.class);
	}

	private String _getGroupManifestPath(Group group) {
		return StringBundler.concat(
			"/group/", group.getGroupId(), "/manifest.xml");
	}

	private Map<String, String[]> _getGroupParameterMap() {
		return HashMapBuilder.put(
			PortletDataHandlerKeys.PORTLET_CONFIGURATION,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_CONFIGURATION_ALL,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_DATA,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_DATA_ALL,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_DATA_CONTROL_DEFAULT,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_SETUP_ALL,
			new String[] {Boolean.TRUE.toString()}
		).build();
	}

	private String _getManifest(ZipReader zipReader) throws Exception {
		return _getManifest(zipReader, "/manifest.xml");
	}

	private String _getManifest(ZipReader zipReader, String path)
		throws Exception {

		try (InputStream inputStream = zipReader.getEntryAsInputStream(path)) {
			Assert.assertNotNull(inputStream);

			return new String(inputStream.readAllBytes());
		}
	}

	private Map<String, String[]> _getParameterMap(
		String... groupExternalReferenceCodes) {

		Map<String, String[]> parameterMap = HashMapBuilder.put(
			PortletDataHandlerKeys.PORTLET_CONFIGURATION,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_DATA,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_DATA_ALL,
			new String[] {Boolean.FALSE.toString()}
		).build();

		if (groupExternalReferenceCodes.length == 0) {
			return parameterMap;
		}

		parameterMap.put(
			PortletDataHandlerKeys.GROUP_EXTERNAL_REFERENCE_CODES,
			groupExternalReferenceCodes);

		return parameterMap;
	}

	private ExportImportConfiguration _importGroupLayouts(
			File file, Group group)
		throws Exception {

		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					TestPropsValues.getUserId(),
					ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildImportLayoutSettingsMap(
							TestPropsValues.getUser(), group.getGroupId(),
							false, null, _getGroupParameterMap()));

		_exportImportLocalService.importLayouts(
			exportImportConfiguration, file);

		return exportImportConfiguration;
	}

	private ExportImportConfiguration _importLayouts(
			File file, String... groupExternalReferenceCodes)
		throws Exception {

		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					TestPropsValues.getUserId(),
					ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildImportLayoutSettingsMap(
							TestPropsValues.getUser(),
							_companyGroup.getGroupId(), false, null,
							_getParameterMap(groupExternalReferenceCodes)));

		_exportImportLocalService.importLayouts(
			exportImportConfiguration, file);

		return exportImportConfiguration;
	}

	private List<ExportImportReportEntry> _importLayoutsWithMissingReferences(
			UnsafeSupplier<ExportImportConfiguration, Exception> unsafeSupplier)
		throws Exception {

		List<ExportImportReportEntry> addedExportImportReportEntries =
			new ArrayList<>();
		List<Long> deletedExportImportReportEntryIds = new ArrayList<>();

		Bundle bundle = FrameworkUtil.getBundle(GroupExportImportTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceRegistration<?> serviceRegistration =
			bundleContext.registerService(
				ModelListener.class,
				new BaseModelListener<ExportImportReportEntry>() {

					@Override
					public Class<?> getModelClass() {
						return ExportImportReportEntry.class;
					}

					@Override
					public void onAfterCreate(
						ExportImportReportEntry exportImportReportEntry) {

						if (exportImportReportEntry.getType() ==
								ExportImportReportEntryConstants.
									TYPE_MISSING_REFERENCE) {

							addedExportImportReportEntries.add(
								exportImportReportEntry);
						}
					}

					@Override
					public void onAfterRemove(
						ExportImportReportEntry exportImportReportEntry) {

						deletedExportImportReportEntryIds.add(
							exportImportReportEntry.
								getExportImportReportEntryId());
					}

				},
				null);

		ExportImportConfiguration exportImportConfiguration = null;

		try {
			exportImportConfiguration = unsafeSupplier.get();
		}
		finally {
			serviceRegistration.unregister();
		}

		Assert.assertFalse(addedExportImportReportEntries.isEmpty());

		for (ExportImportReportEntry exportImportReportEntry :
				addedExportImportReportEntries) {

			Assert.assertTrue(
				deletedExportImportReportEntryIds.contains(
					exportImportReportEntry.getExportImportReportEntryId()));
		}

		Assert.assertFalse(
			ListUtil.exists(
				_exportImportReportEntryLocalService.
					getExportImportReportEntries(
						TestPropsValues.getCompanyId(),
						exportImportConfiguration.
							getExportImportConfigurationId()),
				exportImportReportEntry ->
					exportImportReportEntry.getType() ==
						ExportImportReportEntryConstants.
							TYPE_MISSING_REFERENCE));

		return addedExportImportReportEntries;
	}

	private void _testImportGroupsWithReferencesToOtherGroup(
			Group group1, Group group2)
		throws Exception {

		FileEntry fileEntry = _addFileEntry(group2);

		Layout layout1 = LayoutTestUtil.addTypeContentLayout(group1);
		Layout layout2 = LayoutTestUtil.addTypeContentLayout(group2);

		_addItemToLayout(layout1, fileEntry, layout2);

		File file = _exportLayouts(group1, group2);

		_deleteLayout(layout1);
		_deleteLayout(layout2);

		_dlAppLocalService.deleteFileEntry(fileEntry.getFileEntryId());

		List<ExportImportReportEntry> exportImportReportEntries =
			_importLayoutsWithMissingReferences(
				() -> _importLayouts(
					file, _getExternalReferenceCodes(group1, group2)));

		_assertMissingReference(
			FileEntry.class.getName(), fileEntry.getExternalReferenceCode(),
			group2.getGroupId(), exportImportReportEntries);
		_assertMissingReference(
			Layout.class.getName(), layout2.getExternalReferenceCode(),
			group2.getGroupId(), exportImportReportEntries);

		Assert.assertNotNull(
			_dlAppLocalService.fetchFileEntryByExternalReferenceCode(
				group2.getGroupId(), fileEntry.getExternalReferenceCode()));
		Assert.assertNotNull(
			_layoutLocalService.fetchLayoutByExternalReferenceCode(
				layout1.getExternalReferenceCode(), group1.getGroupId()));
		Assert.assertNotNull(
			_layoutLocalService.fetchLayoutByExternalReferenceCode(
				layout2.getExternalReferenceCode(), group2.getGroupId()));
	}

	private void _updateParentGroupId(Group group, long parentGroupId)
		throws Exception {

		_groupLocalService.updateGroup(
			group.getGroupId(), parentGroupId, group.getNameMap(),
			group.getDescriptionMap(), group.getType(), group.getTypeSettings(),
			group.isManualMembership(), group.getMembershipRestriction(),
			group.getFriendlyURL(), group.isInheritContent(), group.isActive(),
			null);
	}

	private Group _companyGroup;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@Inject
	private ExportImportHelper _exportImportHelper;

	@Inject
	private ExportImportLocalService _exportImportLocalService;

	@Inject
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

	private File _file;
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutService _layoutService;

	@Inject
	private LayoutStructureProvider _layoutStructureProvider;

	@Inject
	private Portal _portal;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject
	private StagingGroupHelper _stagingGroupHelper;

	@Inject
	private StyleBookEntryLocalService _styleBookEntryLocalService;

	@Inject
	private ZipReaderFactory _zipReaderFactory;

}