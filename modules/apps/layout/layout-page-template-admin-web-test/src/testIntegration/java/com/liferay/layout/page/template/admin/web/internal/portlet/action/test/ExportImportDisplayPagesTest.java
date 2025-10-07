/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.dynamic.data.mapping.constants.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.util.DDM;
import com.liferay.info.item.InfoItemFormVariation;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFormVariationsProvider;
import com.liferay.layout.importer.LayoutsImportStrategy;
import com.liferay.layout.importer.LayoutsImporter;
import com.liferay.layout.importer.LayoutsImporterResultEntry;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.RootLayoutStructureItem;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.io.File;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rubén Pulido
 */
@RunWith(Arquillian.class)
public class ExportImportDisplayPagesTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group1 = GroupTestUtil.addGroup();
		_group2 = GroupTestUtil.addGroup();

		_serviceContext1 = ServiceContextTestUtil.getServiceContext(
			_group1, TestPropsValues.getUserId());
		_serviceContext2 = ServiceContextTestUtil.getServiceContext(
			_group2, TestPropsValues.getUserId());
	}

	@Test
	public void testExportImportDisplayPage() throws Exception {
		_assertExportImportDisplayPage(
			_portal.getClassNameId(
				"com.liferay.asset.kernel.model.AssetCategory"),
			0, null, 0);
	}

	@Test
	public void testExportImportDisplayPageWithoutVariation() throws Exception {
		Assert.assertNull(
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormVariationsProvider.class,
				"com.liferay.commerce.product.model.CPDefinition"));

		_assertExportImportDisplayPage(
			_portal.getClassNameId(
				"com.liferay.commerce.product.model.CPDefinition"),
			0, null, 0);
	}

	@Test
	public void testFileEntryExportImportDisplayPage() throws Exception {
		_assertExportImportDisplayPageWithInfoItemFormVariation(
			"com.liferay.portal.kernel.repository.model.FileEntry");
	}

	@Test
	public void testFileEntryExportImportDisplayPageWithSiteTiedVariation()
		throws Exception {

		long ddmStructureClassNameId = _portal.getClassNameId(
			"com.liferay.document.library.kernel.model.DLFileEntryMetadata");

		String ddmStructureKey = RandomTestUtil.randomString();

		Locale locale = _portal.getSiteDefaultLocale(_group1);

		Map<Locale, String> ddmStructureNameMap =
			RandomTestUtil.randomLocaleStringMap(locale);

		DDMForm ddmForm = DDMStructureTestUtil.getSampleDDMForm(
			"name", new Locale[] {locale}, locale);

		DDMFormLayout ddmFormLayout = _ddm.getDefaultDDMFormLayout(ddmForm);

		DDMStructure ddmStructure1 = _ddmStructureLocalService.addStructure(
			null, TestPropsValues.getUserId(), _group1.getGroupId(), 0L,
			ddmStructureClassNameId, ddmStructureKey, ddmStructureNameMap, null,
			ddmForm, ddmFormLayout, StorageType.DEFAULT.toString(),
			DDMStructureConstants.TYPE_DEFAULT, _serviceContext1);

		String fileEntryTypeKey = RandomTestUtil.randomString();

		Map<Locale, String> dlFileEntryTypeNameMap =
			RandomTestUtil.randomLocaleStringMap(locale);

		DLFileEntryType dlFileEntryType1 =
			_dlFileEntryTypeLocalService.addFileEntryType(
				null, TestPropsValues.getUserId(), _group1.getGroupId(),
				ddmStructure1.getStructureId(), fileEntryTypeKey,
				dlFileEntryTypeNameMap, null,
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_SCOPE_DEFAULT,
				_serviceContext1);

		DDMStructure ddmStructure2 = _ddmStructureLocalService.addStructure(
			null, TestPropsValues.getUserId(), _group2.getGroupId(), 0L,
			ddmStructureClassNameId, ddmStructureKey, ddmStructureNameMap, null,
			ddmForm, ddmFormLayout, StorageType.DEFAULT.toString(),
			DDMStructureConstants.TYPE_DEFAULT, _serviceContext2);

		DLFileEntryType dlFileEntryType2 =
			_dlFileEntryTypeLocalService.addFileEntryType(
				null, TestPropsValues.getUserId(), _group2.getGroupId(),
				ddmStructure2.getStructureId(), fileEntryTypeKey,
				dlFileEntryTypeNameMap, null,
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_SCOPE_DEFAULT,
				_serviceContext2);

		_assertExportImportDisplayPage(
			_portal.getClassNameId(
				"com.liferay.portal.kernel.repository.model.FileEntry"),
			dlFileEntryType1.getFileEntryTypeId(), null,
			dlFileEntryType2.getFileEntryTypeId());
	}

	@Test
	public void testFileEntryExportImportDisplayPageWithSiteTiedVariationMissingInTargetSite()
		throws Exception {

		Locale locale = _portal.getSiteDefaultLocale(_group1);

		DDMForm ddmForm = DDMStructureTestUtil.getSampleDDMForm(
			"name", new Locale[] {locale}, locale);

		DDMStructure ddmStructure = _ddmStructureLocalService.addStructure(
			null, TestPropsValues.getUserId(), _group1.getGroupId(), 0L,
			_portal.getClassNameId(
				"com.liferay.document.library.kernel.model." +
					"DLFileEntryMetadata"),
			RandomTestUtil.randomString(),
			RandomTestUtil.randomLocaleStringMap(locale), null, ddmForm,
			_ddm.getDefaultDDMFormLayout(ddmForm),
			StorageType.DEFAULT.toString(), DDMStructureConstants.TYPE_DEFAULT,
			_serviceContext1);

		DLFileEntryType dlFileEntryType =
			_dlFileEntryTypeLocalService.addFileEntryType(
				null, TestPropsValues.getUserId(), _group1.getGroupId(),
				ddmStructure.getStructureId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomLocaleStringMap(locale), null,
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_SCOPE_DEFAULT,
				_serviceContext1);

		_assertExportImportDisplayPage(
			_portal.getClassNameId(
				"com.liferay.portal.kernel.repository.model.FileEntry"),
			dlFileEntryType.getFileEntryTypeId(),
			"x-could-not-be-imported-because-its-content-type-or-subtype-is-" +
				"missing",
			0);
	}

	@Test
	public void testJournalArticleExportImportDisplayPage() throws Exception {
		_assertExportImportDisplayPageWithInfoItemFormVariation(
			"com.liferay.journal.model.JournalArticle");
	}

	@Test
	public void testJournalArticleExportImportDisplayPageWithSiteTiedVariation()
		throws Exception {

		long classNameId = _portal.getClassNameId(
			"com.liferay.journal.model.JournalArticle");

		String ddmStructureKey = RandomTestUtil.randomString();

		Locale locale = _portal.getSiteDefaultLocale(_group1);

		Map<Locale, String> nameMap = RandomTestUtil.randomLocaleStringMap(
			locale);

		DDMForm ddmForm = DDMStructureTestUtil.getSampleDDMForm(
			"name", new Locale[] {locale}, locale);

		DDMFormLayout ddmFormLayout = _ddm.getDefaultDDMFormLayout(ddmForm);

		DDMStructure ddmStructure1 = _ddmStructureLocalService.addStructure(
			null, TestPropsValues.getUserId(), _group1.getGroupId(), 0L,
			classNameId, ddmStructureKey, nameMap, null, ddmForm, ddmFormLayout,
			StorageType.DEFAULT.toString(), DDMStructureConstants.TYPE_DEFAULT,
			_serviceContext1);

		DDMStructure ddmStructure2 = _ddmStructureLocalService.addStructure(
			null, TestPropsValues.getUserId(), _group2.getGroupId(), 0L,
			classNameId, ddmStructureKey, nameMap, null, ddmForm, ddmFormLayout,
			StorageType.DEFAULT.toString(), DDMStructureConstants.TYPE_DEFAULT,
			_serviceContext2);

		_assertExportImportDisplayPage(
			classNameId, ddmStructure1.getStructureId(), null,
			ddmStructure2.getStructureId());
	}

	@Test
	public void testJournalArticleExportImportDisplayPageWithSiteTiedVariationMissingInTargetSite()
		throws Exception {

		long classNameId = _portal.getClassNameId(
			"com.liferay.journal.model.JournalArticle");

		Locale locale = _portal.getSiteDefaultLocale(_group1);

		DDMForm ddmForm = DDMStructureTestUtil.getSampleDDMForm(
			"name", new Locale[] {locale}, locale);

		DDMStructure ddmStructure = _ddmStructureLocalService.addStructure(
			null, TestPropsValues.getUserId(), _group1.getGroupId(), 0L,
			classNameId, RandomTestUtil.randomString(),
			RandomTestUtil.randomLocaleStringMap(locale), null, ddmForm,
			_ddm.getDefaultDDMFormLayout(ddmForm),
			StorageType.DEFAULT.toString(), DDMStructureConstants.TYPE_DEFAULT,
			_serviceContext1);

		_assertExportImportDisplayPage(
			classNameId, ddmStructure.getStructureId(),
			"x-could-not-be-imported-because-its-content-type-or-subtype-is-" +
				"missing",
			0);
	}

	private void _assertExportImportDisplayPage(
			long classNameId, long classTypeId, String errorMessageKey,
			long expectedClassTypeId)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry1 =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, _serviceContext1.getUserId(),
				_serviceContext1.getScopeGroupId(), 0, null, classNameId,
				classTypeId, "Display Page Template One",
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE, 0,
				WorkflowConstants.STATUS_APPROVED, _serviceContext1);

		Layout layout1 = _layoutLocalService.fetchLayout(
			layoutPageTemplateEntry1.getPlid());

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				TestPropsValues.getUserId(), _group1.getGroupId(),
				layoutPageTemplateEntry1.getPlid(),
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(
						layoutPageTemplateEntry1.getPlid()),
				_read("export_import_display_page_layout_data.json"));

		Repository repository = PortletFileRepositoryUtil.addPortletRepository(
			_group1.getGroupId(), RandomTestUtil.randomString(),
			_serviceContext1);

		Class<?> clazz = getClass();

		FileEntry fileEntry = PortletFileRepositoryUtil.addPortletFileEntry(
			null, _group1.getGroupId(), TestPropsValues.getUserId(),
			LayoutPageTemplateEntry.class.getName(),
			layoutPageTemplateEntry1.getLayoutPageTemplateEntryId(),
			RandomTestUtil.randomString(), repository.getDlFolderId(),
			clazz.getResourceAsStream("dependencies/thumbnail.png"),
			RandomTestUtil.randomString(), ContentTypes.IMAGE_PNG, false);

		_layoutPageTemplateEntryLocalService.updateLayoutPageTemplateEntry(
			layoutPageTemplateEntry1.getLayoutPageTemplateEntryId(),
			fileEntry.getFileEntryId());

		File file = ReflectionTestUtil.invoke(
			_mvcResourceCommand, "getFile", new Class<?>[] {long[].class},
			new long[] {
				layoutPageTemplateEntry1.getLayoutPageTemplateEntryId()
			});

		List<LayoutsImporterResultEntry> layoutsImporterResultEntries = null;

		ServiceContextThreadLocal.pushServiceContext(_serviceContext2);

		try {
			layoutsImporterResultEntries = _layoutsImporter.importFile(
				TestPropsValues.getUserId(), _group2.getGroupId(), 0, file,
				LayoutsImportStrategy.DO_NOT_OVERWRITE, true);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}

		Assert.assertNotNull(layoutsImporterResultEntries);

		Assert.assertEquals(
			layoutsImporterResultEntries.toString(), 1,
			layoutsImporterResultEntries.size());

		LayoutsImporterResultEntry layoutsImporterResultEntry =
			layoutsImporterResultEntries.get(0);

		if (Validator.isNotNull(errorMessageKey)) {
			Assert.assertEquals(
				LayoutsImporterResultEntry.Status.INVALID,
				layoutsImporterResultEntry.getStatus());

			Assert.assertEquals(
				_language.format(
					LocaleUtil.getMostRelevantLocale(), errorMessageKey,
					"display-page-templates/display-page-template-one" +
						"/display-page-template.json"),
				layoutsImporterResultEntry.getErrorMessage(
					LocaleUtil.getSiteDefault()));

			return;
		}

		Assert.assertEquals(
			LayoutsImporterResultEntry.Status.IMPORTED,
			layoutsImporterResultEntry.getStatus());

		String layoutPageTemplateEntryKey = StringUtil.toLowerCase(
			layoutsImporterResultEntry.getName());

		layoutPageTemplateEntryKey = StringUtil.replace(
			layoutPageTemplateEntryKey, CharPool.SPACE, CharPool.DASH);

		LayoutPageTemplateEntry layoutPageTemplateEntry2 =
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				_group2.getGroupId(), layoutPageTemplateEntryKey);

		Assert.assertEquals(
			classNameId, layoutPageTemplateEntry2.getClassNameId());
		Assert.assertEquals(
			expectedClassTypeId, layoutPageTemplateEntry2.getClassTypeId());

		Layout layout2 = _layoutLocalService.fetchLayout(
			layoutPageTemplateEntry2.getPlid());

		Assert.assertNotNull(layout2);

		Assert.assertEquals(
			layout1.getMasterLayoutPlid(), layout2.getMasterLayoutPlid());

		Assert.assertEquals(
			layoutPageTemplateEntry1.getName(),
			layoutPageTemplateEntry2.getName());
		Assert.assertEquals(
			layoutPageTemplateEntry1.getType(),
			layoutPageTemplateEntry2.getType());

		LayoutPageTemplateStructure layoutPageTemplateStructure1 =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					layoutPageTemplateEntry1.getGroupId(),
					layoutPageTemplateEntry1.getPlid());
		LayoutPageTemplateStructure layoutPageTemplateStructure2 =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					layoutPageTemplateEntry2.getGroupId(),
					layoutPageTemplateEntry2.getPlid());

		LayoutStructure layoutStructure1 = LayoutStructure.of(
			layoutPageTemplateStructure1.getDefaultSegmentsExperienceData());
		LayoutStructure layoutStructure2 = LayoutStructure.of(
			layoutPageTemplateStructure2.getDefaultSegmentsExperienceData());

		_validateRootLayoutStructureItem(
			(RootLayoutStructureItem)
				layoutStructure1.getMainLayoutStructureItem(),
			(RootLayoutStructureItem)
				layoutStructure2.getMainLayoutStructureItem());
	}

	private void _assertExportImportDisplayPageWithInfoItemFormVariation(
			String className)
		throws Exception {

		long classNameId = _portal.getClassNameId(className);

		InfoItemFormVariationsProvider<?> infoItemFormVariationsProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormVariationsProvider.class, className);

		List<InfoItemFormVariation> infoItemFormVariations = new ArrayList<>(
			infoItemFormVariationsProvider.getInfoItemFormVariations(
				_serviceContext1.getScopeGroupId()));

		Assert.assertFalse(infoItemFormVariations.isEmpty());

		infoItemFormVariations.sort(
			Comparator.comparing(InfoItemFormVariation::getKey));

		InfoItemFormVariation infoItemFormVariation =
			infoItemFormVariations.get(0);

		long classTypeId = GetterUtil.getLong(infoItemFormVariation.getKey());

		_assertExportImportDisplayPage(
			classNameId, classTypeId, null, classTypeId);
	}

	private String _read(String fileName) throws Exception {
		return new String(
			FileUtil.getBytes(getClass(), "dependencies/" + fileName));
	}

	private void _validateRootLayoutStructureItem(
		RootLayoutStructureItem expectedRootLayoutStructureItem,
		RootLayoutStructureItem actualRootLayoutStructureItem) {

		Assert.assertEquals(
			expectedRootLayoutStructureItem.getChildrenItemIds(),
			actualRootLayoutStructureItem.getChildrenItemIds());

		JSONObject expectedItemConfigJSONObject =
			expectedRootLayoutStructureItem.getItemConfigJSONObject();
		JSONObject actualItemConfigJSONObject =
			actualRootLayoutStructureItem.getItemConfigJSONObject();

		Assert.assertEquals(
			expectedItemConfigJSONObject.toString(),
			actualItemConfigJSONObject.toString());

		Assert.assertEquals(
			expectedRootLayoutStructureItem.getItemType(),
			actualRootLayoutStructureItem.getItemType());
		Assert.assertEquals(
			expectedRootLayoutStructureItem.getParentItemId(),
			actualRootLayoutStructureItem.getParentItemId());
	}

	@Inject
	private DDM _ddm;

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@Inject
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@DeleteAfterTestRun
	private Group _group1;

	@DeleteAfterTestRun
	private Group _group2;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Inject
	private Language _language;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private LayoutsImporter _layoutsImporter;

	@Inject(
		filter = "mvc.command.name=/layout_page_template_admin/export_display_pages"
	)
	private MVCResourceCommand _mvcResourceCommand;

	@Inject
	private Portal _portal;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceContext _serviceContext1;
	private ServiceContext _serviceContext2;

}