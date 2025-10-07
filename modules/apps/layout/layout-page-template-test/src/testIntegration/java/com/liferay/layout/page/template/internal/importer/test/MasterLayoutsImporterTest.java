/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.importer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.contributor.FragmentCollectionContributor;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalServiceUtil;
import com.liferay.layout.exporter.LayoutsExporter;
import com.liferay.layout.importer.LayoutsImportStrategy;
import com.liferay.layout.importer.LayoutsImporter;
import com.liferay.layout.importer.LayoutsImporterResultEntry;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateExportImportConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.util.structure.ColumnLayoutStructureItem;
import com.liferay.layout.util.structure.ContainerStyledLayoutStructureItem;
import com.liferay.layout.util.structure.DropZoneLayoutStructureItem;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.layout.util.structure.RowStyledLayoutStructureItem;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactory;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.IOUtils;

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
 * @author Rubén Pulido
 */
@RunWith(Arquillian.class)
public class MasterLayoutsImporterTest {

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

		Bundle bundle = FrameworkUtil.getBundle(
			MasterLayoutsImporterTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			FragmentCollectionContributor.class,
			new TestMasterPageFragmentCollectionContributor(), null);
	}

	@After
	public void tearDown() {
		_serviceRegistration.unregister();
	}

	@Test
	@TestInfo("LPS-102207")
	public void testExportImportMasterLayoutsWithThumbnail() throws Exception {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				null, RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, 0,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Repository repository = PortletFileRepositoryUtil.addPortletRepository(
			_group.getGroupId(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Class<?> clazz = getClass();

		FileEntry fileEntry = PortletFileRepositoryUtil.addPortletFileEntry(
			null, _group.getGroupId(), TestPropsValues.getUserId(),
			LayoutPageTemplateEntry.class.getName(),
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			RandomTestUtil.randomString(), repository.getDlFolderId(),
			clazz.getResourceAsStream("dependencies/thumbnail.png"),
			"thumbnail.png", ContentTypes.IMAGE_PNG, false);

		InputStream inputStream = fileEntry.getContentStream();

		_layoutPageTemplateEntryLocalService.updateLayoutPageTemplateEntry(
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			fileEntry.getFileEntryId());

		File file = _layoutsExporter.exportLayoutPageTemplateEntries(
			new long[] {layoutPageTemplateEntry.getLayoutPageTemplateEntryId()},
			LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT);

		_layoutPageTemplateEntryLocalService.deleteLayoutPageTemplateEntry(
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId());

		Assert.assertEquals(
			0,
			_layoutPageTemplateEntryService.getLayoutPageTemplateEntriesCount(
				_group.getGroupId(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT));

		List<LayoutsImporterResultEntry> layoutsImporterResultEntries =
			_getLayoutsImporterResultEntries(file);

		Assert.assertEquals(
			layoutsImporterResultEntries.toString(), 1,
			layoutsImporterResultEntries.size());

		LayoutPageTemplateEntry importedLayoutPageTemplateEntry =
			_getLayoutPageTemplateEntry(layoutsImporterResultEntries, 0);

		FileEntry importedFileEntry =
			PortletFileRepositoryUtil.getPortletFileEntry(
				importedLayoutPageTemplateEntry.getPreviewFileEntryId());

		Assert.assertTrue(
			IOUtils.contentEquals(
				inputStream, importedFileEntry.getContentStream()));
	}

	@Test
	@TestInfo("LPS-102207")
	public void testExportImportMasterLayoutWithFragmentsConfiguration()
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				null, RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, 0,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Layout layout = _layoutLocalService.fetchLayout(
			layoutPageTemplateEntry.getPlid());

		FragmentEntry fragmentEntry =
			_fragmentCollectionContributorRegistry.getFragmentEntry(
				"BASIC_COMPONENT-heading");

		Layout draftLayout = layout.fetchDraftLayout();

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put("headingLevel", "h1")
			).toString(),
			fragmentEntry.getCss(), fragmentEntry.getConfiguration(),
			fragmentEntry.getFragmentEntryId(), fragmentEntry.getHtml(),
			fragmentEntry.getJs(), layout.fetchDraftLayout(),
			fragmentEntry.getFragmentEntryKey(), fragmentEntry.getType(), null,
			0,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid()));

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		File file = _layoutsExporter.exportLayoutPageTemplateEntries(
			new long[] {layoutPageTemplateEntry.getLayoutPageTemplateEntryId()},
			LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT);

		_layoutPageTemplateEntryLocalService.deleteLayoutPageTemplateEntry(
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId());

		Assert.assertEquals(
			0,
			_layoutPageTemplateEntryService.getLayoutPageTemplateEntriesCount(
				_group.getGroupId(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT));

		LayoutPageTemplateEntry importedLayoutPageTemplateEntry =
			_getLayoutPageTemplateEntry(
				_getLayoutsImporterResultEntries(file), 0);

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					_group.getGroupId(),
					importedLayoutPageTemplateEntry.getPlid());

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getDefaultSegmentsExperienceData());

		LayoutStructureItem mainLayoutStructureItem =
			layoutStructure.getMainLayoutStructureItem();

		List<String> childrenItemIds =
			mainLayoutStructureItem.getChildrenItemIds();

		Assert.assertEquals(
			childrenItemIds.toString(), 2, childrenItemIds.size());

		FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem =
			(FragmentStyledLayoutStructureItem)
				layoutStructure.getLayoutStructureItem(childrenItemIds.get(0));

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentStyledLayoutStructureItem.getFragmentEntryLinkId());

		JSONObject configurationValuesJSONObject =
			fragmentEntryLink.getEditableValuesJSONObject();

		JSONObject freeMarkerJSONObject =
			configurationValuesJSONObject.getJSONObject(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR);

		Assert.assertEquals(
			"h1", freeMarkerJSONObject.getString("headingLevel"));
	}

	@Test
	@TestInfo("LPS-102207")
	public void testExportImportMasterLayoutWithLookAndFeel() throws Exception {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				null, RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, 0,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Layout layout = _layoutLocalService.fetchLayout(
			layoutPageTemplateEntry.getPlid());

		_layoutLocalService.updateLookAndFeel(
			_group.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			"dialect_WAR_dialecttheme", "01", StringPool.BLANK);

		File file = _layoutsExporter.exportLayoutPageTemplateEntries(
			new long[] {layoutPageTemplateEntry.getLayoutPageTemplateEntryId()},
			LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT);

		_layoutPageTemplateEntryLocalService.deleteLayoutPageTemplateEntry(
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId());

		Assert.assertEquals(
			0,
			_layoutPageTemplateEntryService.getLayoutPageTemplateEntriesCount(
				_group.getGroupId(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT));

		LayoutPageTemplateEntry importedLayoutPageTemplateEntry =
			_getLayoutPageTemplateEntry(
				_getLayoutsImporterResultEntries(file), 0);

		layout = _layoutLocalService.fetchLayout(
			importedLayoutPageTemplateEntry.getPlid());

		Assert.assertEquals("dialect_WAR_dialecttheme", layout.getThemeId());
	}

	@Test
	public void testImportMasterLayoutDropZone() throws Exception {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_importLayoutPageTemplateEntry("master-page-drop-zone");

		Assert.assertEquals(
			"Master Page Drop Zone", layoutPageTemplateEntry.getName());

		_validateLayoutPageTemplateStructureDropZone(
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					_group.getGroupId(), layoutPageTemplateEntry.getPlid()),
			new ArrayList<>(), true);
	}

	@Test
	public void testImportMasterLayoutDropZoneAllowedFragments()
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_importLayoutPageTemplateEntry(
				"master-page-drop-zone-allowed-fragments");

		Assert.assertEquals(
			"Master Page Drop Zone Allowed Fragments",
			layoutPageTemplateEntry.getName());

		_validateLayoutPageTemplateStructureDropZone(
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					_group.getGroupId(), layoutPageTemplateEntry.getPlid()),
			Arrays.asList(
				TestMasterPageFragmentCollectionContributor.
					TEST_MASTER_PAGE_FRAGMENT_COLLECTION_KEY,
				TestMasterPageFragmentCollectionContributor.
					TEST_MASTER_PAGE_FRAGMENT_COLLECTION_KEY + StringPool.DASH +
						TestMasterPageFragmentCollectionContributor.
							TEST_MASTER_PAGE_FRAGMENT_ENTRY_1,
				TestMasterPageFragmentCollectionContributor.
					TEST_MASTER_PAGE_FRAGMENT_COLLECTION_KEY + StringPool.DASH +
						TestMasterPageFragmentCollectionContributor.
							TEST_MASTER_PAGE_FRAGMENT_ENTRY_2),
			false);
	}

	@Test
	public void testImportMasterLayoutDropZoneUnallowedFragments()
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_importLayoutPageTemplateEntry(
				"master-page-drop-zone-unallowed-fragments");

		Assert.assertEquals(
			"Master Page Drop Zone Unallowed Fragments",
			layoutPageTemplateEntry.getName());

		_validateLayoutPageTemplateStructureDropZone(
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					_group.getGroupId(), layoutPageTemplateEntry.getPlid()),
			Arrays.asList(
				TestMasterPageFragmentCollectionContributor.
					TEST_MASTER_PAGE_FRAGMENT_COLLECTION_KEY,
				TestMasterPageFragmentCollectionContributor.
					TEST_MASTER_PAGE_FRAGMENT_COLLECTION_KEY + StringPool.DASH +
						TestMasterPageFragmentCollectionContributor.
							TEST_MASTER_PAGE_FRAGMENT_ENTRY_1,
				TestMasterPageFragmentCollectionContributor.
					TEST_MASTER_PAGE_FRAGMENT_COLLECTION_KEY + StringPool.DASH +
						TestMasterPageFragmentCollectionContributor.
							TEST_MASTER_PAGE_FRAGMENT_ENTRY_2),
			true);
	}

	@Test
	public void testImportMasterLayoutExistingNameNoOvewrite()
		throws Exception {

		String testCaseName = "master-page-drop-zone-allowed-fragments";

		_importLayoutPageTemplateEntry(testCaseName);

		List<LayoutsImporterResultEntry> layoutsImporterResultEntries =
			_getLayoutsImporterResultEntries(testCaseName);

		LayoutsImporterResultEntry layoutsImporterResultEntry =
			layoutsImporterResultEntries.get(0);

		Assert.assertEquals(
			LayoutsImporterResultEntry.Status.IGNORED,
			layoutsImporterResultEntry.getStatus());
		Assert.assertEquals(
			String.format(
				"%s/master-pages/%s/master-page.json was ignored because a " +
					"master page with the same key already exists.",
				testCaseName, testCaseName),
			layoutsImporterResultEntry.getErrorMessage(
				LocaleUtil.getSiteDefault()));
	}

	@Test
	public void testImportMasterLayouts() throws Exception {
		List<LayoutsImporterResultEntry> layoutsImporterResultEntries =
			_getLayoutsImporterResultEntries("master-page-multiple");

		Assert.assertEquals(
			layoutsImporterResultEntries.toString(), 2,
			layoutsImporterResultEntries.size());

		LayoutPageTemplateEntry layoutPageTemplateEntry1 =
			_getLayoutPageTemplateEntry(layoutsImporterResultEntries, 0);
		LayoutPageTemplateEntry layoutPageTemplateEntry2 =
			_getLayoutPageTemplateEntry(layoutsImporterResultEntries, 1);

		List<String> actualLayoutPageTemplateEntryNames = ListUtil.sort(
			new ArrayList() {
				{
					add(layoutPageTemplateEntry1.getName());
					add(layoutPageTemplateEntry2.getName());
				}
			});

		Assert.assertArrayEquals(
			new String[] {"Master Page One", "Master Page Two"},
			actualLayoutPageTemplateEntryNames.toArray(new String[0]));
	}

	@Test
	@TestInfo("LPS-102207")
	public void testImportMasterLayoutWithCompositionOfFragments()
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_importLayoutPageTemplateEntry(
				"master-pages-with-composition-of-fragments");

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					_group.getGroupId(), layoutPageTemplateEntry.getPlid());

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getDefaultSegmentsExperienceData());

		LayoutStructureItem mainLayoutStructureItem =
			layoutStructure.getMainLayoutStructureItem();

		List<String> childrenItemIds =
			mainLayoutStructureItem.getChildrenItemIds();

		Assert.assertEquals(
			childrenItemIds.toString(), 3, childrenItemIds.size());

		LayoutStructureItem dropZoneLayoutStructureItem =
			layoutStructure.getLayoutStructureItem(childrenItemIds.get(0));

		Assert.assertTrue(
			dropZoneLayoutStructureItem instanceof DropZoneLayoutStructureItem);

		LayoutStructureItem rowStyledLayoutStructureItem =
			layoutStructure.getLayoutStructureItem(childrenItemIds.get(1));

		Assert.assertTrue(
			rowStyledLayoutStructureItem instanceof
				RowStyledLayoutStructureItem);

		LayoutStructureItem columnLayoutStructureItem =
			layoutStructure.getLayoutStructureItem(
				rowStyledLayoutStructureItem.getChildrenItemId(0));

		Assert.assertTrue(
			columnLayoutStructureItem instanceof ColumnLayoutStructureItem);

		FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem =
			(FragmentStyledLayoutStructureItem)
				layoutStructure.getLayoutStructureItem(
					columnLayoutStructureItem.getChildrenItemId(0));

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentStyledLayoutStructureItem.getFragmentEntryLinkId());

		Assert.assertEquals(
			"BASIC_COMPONENT-button", fragmentEntryLink.getRendererKey());

		LayoutStructureItem containerStyledLayoutStructureItem =
			layoutStructure.getLayoutStructureItem(childrenItemIds.get(2));

		Assert.assertTrue(
			containerStyledLayoutStructureItem instanceof
				ContainerStyledLayoutStructureItem);

		fragmentStyledLayoutStructureItem =
			(FragmentStyledLayoutStructureItem)
				layoutStructure.getLayoutStructureItem(
					containerStyledLayoutStructureItem.getChildrenItemId(0));

		fragmentEntryLink =
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentStyledLayoutStructureItem.getFragmentEntryLinkId());

		Assert.assertEquals(
			"BASIC_COMPONENT-heading", fragmentEntryLink.getRendererKey());
	}

	@Test
	@TestInfo("LPS-102207")
	public void testImportMasterLayoutWithInlineContent() throws Exception {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_importLayoutPageTemplateEntry("master-pages-with-inline-content");

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					_group.getGroupId(), layoutPageTemplateEntry.getPlid());

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getDefaultSegmentsExperienceData());

		LayoutStructureItem mainLayoutStructureItem =
			layoutStructure.getMainLayoutStructureItem();

		FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem =
			(FragmentStyledLayoutStructureItem)
				layoutStructure.getLayoutStructureItem(
					mainLayoutStructureItem.getChildrenItemId(0));

		_assertFragmentEntryLinkEditableValue(
			"Modified text",
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentStyledLayoutStructureItem.getFragmentEntryLinkId()),
			"element-text");

		fragmentStyledLayoutStructureItem =
			(FragmentStyledLayoutStructureItem)
				layoutStructure.getLayoutStructureItem(
					mainLayoutStructureItem.getChildrenItemId(1));

		_assertFragmentEntryLinkEditableValue(
			"<div class=\"fragment-static-text\">\n\t" +
				"<div class=\"static-text\">This is a static text.</div>\n" +
					"</div>",
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentStyledLayoutStructureItem.getFragmentEntryLinkId()),
			"element-html");
	}

	@Test
	@TestInfo("LPS-102207")
	public void testImportMastersLayoutsWithInvalidValue() throws Exception {
		List<LayoutsImporterResultEntry> layoutsImporterResultEntries =
			_getLayoutsImporterResultEntries("master-page-invalid-value");

		Assert.assertEquals(
			layoutsImporterResultEntries.toString(), 1,
			layoutsImporterResultEntries.size());

		LayoutsImporterResultEntry layoutsImporterResultEntry =
			layoutsImporterResultEntries.get(0);

		Assert.assertEquals(
			"Custom Master Page Template could not be imported because its " +
				"page definition is invalid.",
			layoutsImporterResultEntry.getErrorMessage(
				LocaleUtil.getSiteDefault()));
	}

	private void _addZipWriterEntry(ZipWriter zipWriter, URL url)
		throws IOException {

		String entryPath = url.getPath();

		String zipPath = StringUtil.removeSubstring(entryPath, _BASE_PATH);

		try (InputStream inputStream = url.openStream()) {
			zipWriter.addEntry(zipPath, inputStream);
		}
	}

	private void _assertFragmentEntryLinkEditableValue(
			String expectedValue, FragmentEntryLink fragmentEntryLink,
			String key)
		throws Exception {

		JSONObject editableValuesJSONObject =
			fragmentEntryLink.getEditableValuesJSONObject();

		JSONObject editableJSONObject = editableValuesJSONObject.getJSONObject(
			FragmentEntryProcessorConstants.
				KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR);

		JSONObject jsonObject = editableJSONObject.getJSONObject(key);

		Assert.assertEquals(
			expectedValue,
			jsonObject.getString(
				LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault())));
	}

	private File _generateZipFile(String testCaseName) throws Exception {
		ZipWriter zipWriter = _zipWriterFactory.getZipWriter();

		Enumeration<URL> enumeration = _bundle.findEntries(
			StringBundler.concat(
				_BASE_PATH + testCaseName,
				StringPool.FORWARD_SLASH + _ROOT_FOLDER,
				StringPool.FORWARD_SLASH),
			LayoutPageTemplateExportImportConstants.FILE_NAME_MASTER_PAGE,
			true);

		try {
			while (enumeration.hasMoreElements()) {
				URL url = enumeration.nextElement();

				_populateZipWriter(zipWriter, url);
			}

			return zipWriter.getFile();
		}
		catch (Exception exception) {
			throw new Exception(exception);
		}
	}

	private LayoutPageTemplateEntry _getLayoutPageTemplateEntry(
		List<LayoutsImporterResultEntry> layoutsImporterResultEntries,
		int index) {

		LayoutsImporterResultEntry layoutsImporterResultEntry =
			layoutsImporterResultEntries.get(index);

		Assert.assertEquals(
			LayoutsImporterResultEntry.Status.IMPORTED,
			layoutsImporterResultEntry.getStatus());

		String layoutPageTemplateEntryKey = StringUtil.toLowerCase(
			layoutsImporterResultEntry.getName());

		layoutPageTemplateEntryKey = StringUtil.replace(
			layoutPageTemplateEntryKey, CharPool.SPACE, CharPool.DASH);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				_group.getGroupId(), layoutPageTemplateEntryKey);

		Assert.assertNotNull(layoutPageTemplateEntry);

		return layoutPageTemplateEntry;
	}

	private List<LayoutsImporterResultEntry> _getLayoutsImporterResultEntries(
			File file)
		throws Exception {

		List<LayoutsImporterResultEntry> layoutsImporterResultEntries = null;

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		try {
			layoutsImporterResultEntries = _layoutsImporter.importFile(
				_user.getUserId(), _group.getGroupId(), 0, file,
				LayoutsImportStrategy.DO_NOT_OVERWRITE, true);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}

		Assert.assertNotNull(layoutsImporterResultEntries);

		return layoutsImporterResultEntries;
	}

	private List<LayoutsImporterResultEntry> _getLayoutsImporterResultEntries(
			String testCaseName)
		throws Exception {

		return _getLayoutsImporterResultEntries(_generateZipFile(testCaseName));
	}

	private LayoutPageTemplateEntry _importLayoutPageTemplateEntry(
			String testCaseName)
		throws Exception {

		List<LayoutsImporterResultEntry> layoutsImporterResultEntries =
			_getLayoutsImporterResultEntries(testCaseName);

		Assert.assertEquals(
			layoutsImporterResultEntries.toString(), 1,
			layoutsImporterResultEntries.size());

		return _getLayoutPageTemplateEntry(layoutsImporterResultEntries, 0);
	}

	private void _populateZipWriter(ZipWriter zipWriter, URL url)
		throws IOException {

		String zipPath = StringUtil.removeSubstring(url.getFile(), _BASE_PATH);

		try (InputStream inputStream = url.openStream()) {
			zipWriter.addEntry(zipPath, inputStream);
		}

		String path = FileUtil.getPath(url.getPath());

		Enumeration<URL> enumeration = _bundle.findEntries(
			path, LayoutPageTemplateExportImportConstants.FILE_NAME_MASTER_PAGE,
			true);

		while (enumeration.hasMoreElements()) {
			URL elementURL = enumeration.nextElement();

			_addZipWriterEntry(zipWriter, elementURL);
		}

		enumeration = _bundle.findEntries(
			path,
			LayoutPageTemplateExportImportConstants.FILE_NAME_PAGE_DEFINITION,
			true);

		while (enumeration.hasMoreElements()) {
			URL elementURL = enumeration.nextElement();

			_addZipWriterEntry(zipWriter, elementURL);
		}
	}

	private void _validateLayoutPageTemplateStructureDropZone(
		LayoutPageTemplateStructure layoutPageTemplateStructure,
		List<String> expectedFragmentEntryKeys,
		boolean expectedIsAllowNewFragments) {

		Assert.assertNotNull(layoutPageTemplateStructure);

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getDefaultSegmentsExperienceData());

		LayoutStructureItem mainLayoutStructureItem =
			layoutStructure.getMainLayoutStructureItem();

		List<String> childrenItemIds =
			mainLayoutStructureItem.getChildrenItemIds();

		Assert.assertEquals(
			childrenItemIds.toString(), 1, childrenItemIds.size());

		String childItemId = childrenItemIds.get(0);

		LayoutStructureItem layoutStructureItem =
			layoutStructure.getLayoutStructureItem(childItemId);

		Assert.assertTrue(
			layoutStructureItem instanceof DropZoneLayoutStructureItem);

		DropZoneLayoutStructureItem dropZoneLayoutStructureItem =
			(DropZoneLayoutStructureItem)layoutStructureItem;

		Assert.assertNotNull(layoutStructure);

		Assert.assertTrue(
			expectedFragmentEntryKeys.containsAll(
				dropZoneLayoutStructureItem.getFragmentEntryKeys()));

		Assert.assertEquals(
			expectedIsAllowNewFragments,
			dropZoneLayoutStructureItem.isAllowNewFragmentEntries());
	}

	private static final String _BASE_PATH =
		"com/liferay/layout/page/template/internal/importer/test/dependencies/";

	private static final String _ROOT_FOLDER = "master-pages";

	private Bundle _bundle;

	@Inject
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private LayoutsExporter _layoutsExporter;

	@Inject
	private LayoutsImporter _layoutsImporter;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceRegistration<FragmentCollectionContributor>
		_serviceRegistration;
	private User _user;

	@Inject
	private ZipWriterFactory _zipWriterFactory;

	private static class TestMasterPageFragmentCollectionContributor
		implements FragmentCollectionContributor {

		public static final String TEST_MASTER_PAGE_FRAGMENT_COLLECTION_KEY =
			"test-master-page-fragment-collection-contributor";

		public static final String TEST_MASTER_PAGE_FRAGMENT_ENTRY_1 =
			"fragment-entry-1";

		public static final String TEST_MASTER_PAGE_FRAGMENT_ENTRY_2 =
			"fragment-entry-2";

		@Override
		public String getFragmentCollectionKey() {
			return TEST_MASTER_PAGE_FRAGMENT_COLLECTION_KEY;
		}

		@Override
		public List<FragmentEntry> getFragmentEntries() {
			return ListUtil.fromArray(
				_getFragmentEntry(TEST_MASTER_PAGE_FRAGMENT_ENTRY_1, 0),
				_getFragmentEntry(TEST_MASTER_PAGE_FRAGMENT_ENTRY_2, 0));
		}

		@Override
		public List<FragmentEntry> getFragmentEntries(int type) {
			return getFragmentEntries();
		}

		@Override
		public List<FragmentEntry> getFragmentEntries(int[] types) {
			return getFragmentEntries();
		}

		@Override
		public List<FragmentEntry> getFragmentEntries(Locale locale) {
			return Collections.emptyList();
		}

		@Override
		public String getName() {
			return "Test Master Page Fragment Collection Contributor";
		}

		@Override
		public Map<Locale, String> getNames() {
			return HashMapBuilder.put(
				LocaleUtil.getSiteDefault(), getName()
			).build();
		}

		private FragmentEntry _getFragmentEntry(String key, int type) {
			FragmentEntry fragmentEntry =
				FragmentEntryLocalServiceUtil.createFragmentEntry(0L);

			fragmentEntry.setFragmentEntryKey(key);
			fragmentEntry.setName(RandomTestUtil.randomString());
			fragmentEntry.setCss(null);
			fragmentEntry.setHtml(RandomTestUtil.randomString());
			fragmentEntry.setJs(null);
			fragmentEntry.setConfiguration(null);
			fragmentEntry.setType(type);

			return fragmentEntry;
		}

	}

}