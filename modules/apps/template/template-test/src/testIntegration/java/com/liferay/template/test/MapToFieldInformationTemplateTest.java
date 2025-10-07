/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.template.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.data.engine.rest.resource.v2_0.DataDefinitionResource;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesToFieldsConverter;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.renderer.FragmentRendererRegistry;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.info.field.InfoField;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.list.provider.item.selector.criterion.InfoListProviderItemSelectorReturnType;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.journal.util.JournalConverter;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.LayoutServiceContextHelper;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.template.model.TemplateEntry;
import com.liferay.template.test.util.TemplateTestUtil;

import java.util.Date;
import java.util.Locale;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class MapToFieldInformationTemplateTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_locale = _portal.getSiteDefaultLocale(_group.getGroupId());

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group, TestPropsValues.getUserId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);

		_assetCategory = _addAssetCategory();
		_blogsEntry = _addBlogsEntry();
		_dlFileEntry = _addDLFileEntry();
		_fragmentEntry = _addFragmentEntry();
		_journalArticle = _addJournalArticle();

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		_draftLayout = _layout.fetchDraftLayout();

		_segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_draftLayout.getPlid());
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testMapToContentDisplay() throws Exception {
		_mapToContentDisplay(
			AssetCategory.class.getName(),
			_portal.getClassNameId(AssetCategory.class.getName()), 0,
			_assetCategory.getCategoryId(), "name");
		_mapToContentDisplay(
			BlogsEntry.class.getName(),
			_portal.getClassNameId(BlogsEntry.class.getName()), 0,
			_blogsEntry.getEntryId(), "content");
		_mapToContentDisplay(
			FileEntry.class.getName(),
			_portal.getClassNameId(DLFileEntry.class.getName()),
			_dlFileEntry.getFileEntryTypeId(), _dlFileEntry.getFileEntryId(),
			"title");
		_mapToContentDisplay(
			JournalArticle.class.getName(),
			_portal.getClassNameId(JournalArticle.class.getName()),
			_journalArticle.getDDMStructureId(),
			_journalArticle.getResourcePrimKey(),
			"DDMStructure_" + _ddmFormField.getName());

		_assertRenderLayoutHTML(
			_assetCategory.getName(), _blogsEntry.getContent(), _fieldContent,
			_dlFileEntry.getTitle());
	}

	@Test
	public void testMapToInfoField() throws Exception {
		_mapToInfoField(
			AssetCategory.class.getName(), 0, _assetCategory.getCategoryId(),
			"name", StringPool.BLANK);
		_mapToInfoField(
			BlogsEntry.class.getName(), 0, _blogsEntry.getEntryId(), "content",
			StringPool.BLANK);
		_mapToInfoField(
			FileEntry.class.getName(), _dlFileEntry.getFileEntryTypeId(),
			_dlFileEntry.getFileEntryId(), "title",
			String.valueOf(_dlFileEntry.getFileEntryTypeId()));
		_mapToInfoField(
			JournalArticle.class.getName(), _journalArticle.getDDMStructureId(),
			_journalArticle.getResourcePrimKey(),
			"DDMStructure_" + _ddmFormField.getName(),
			String.valueOf(_journalArticle.getDDMStructureId()));

		_assertRenderLayoutHTML(
			_assetCategory.getName(), _blogsEntry.getContent(), _fieldContent,
			_dlFileEntry.getTitle());
	}

	@Test
	public void testMapToInfoFieldInCollectionDisplay() throws Exception {
		InfoField infoField = TemplateTestUtil.addTemplateEntryInfoField(
			"AssetEntry_title", AssetEntry.class.getName(), StringPool.BLANK,
			_infoItemServiceRegistry, _serviceContext);

		ContentLayoutTestUtil.addCollectionDisplayToLayout(
			JSONUtil.put(
				"itemType", AssetEntry.class.getName()
			).put(
				"key",
				"com.liferay.asset.internal.info.collection.provider." +
					"RecentContentInfoCollectionProvider"
			).put(
				"type", InfoListProviderItemSelectorReturnType.class.getName()
			),
			_draftLayout, _layoutStructureProvider, null, null, 0,
			_segmentsExperienceId,
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0, 0,
				_segmentsExperienceId, _draftLayout.getPlid(),
				_fragmentEntry.getCss(), _fragmentEntry.getHtml(),
				_fragmentEntry.getJs(), _fragmentEntry.getConfiguration(),
				JSONUtil.put(
					FragmentEntryProcessorConstants.
						KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
					JSONUtil.put(
						"element-text",
						JSONUtil.put(
							"collectionFieldId", infoField.getUniqueId()))
				).toString(),
				StringPool.BLANK, 0, _fragmentEntry.getFragmentEntryKey(),
				_fragmentEntry.getType(), _serviceContext));

		ContentLayoutTestUtil.publishLayout(_draftLayout, _layout);

		_assertRenderLayoutHTML(
			_blogsEntry.getTitle(), _dlFileEntry.getTitle(),
			_journalArticle.getTitle());
	}

	private AssetCategory _addAssetCategory() throws Exception {
		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		return AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());
	}

	private BlogsEntry _addBlogsEntry() throws Exception {
		return _blogsEntryLocalService.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), new Date(), true, true,
			new String[0], StringPool.BLANK, null, null, _serviceContext);
	}

	private DLFileEntry _addDLFileEntry() throws Exception {
		FileEntry fileEntry = _dlAppService.addFileEntry(
			null, _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, null, 0, null, null, null, _serviceContext);

		return _dlFileEntryLocalService.getFileEntry(
			fileEntry.getFileEntryId());
	}

	private FragmentEntry _addFragmentEntry() throws Exception {
		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				StringUtil.randomString(), StringPool.BLANK, _serviceContext);

		return _fragmentEntryLocalService.addFragmentEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			StringPool.BLANK,
			"<h1 data-lfr-editable-id=\"element-text\" " +
				"data-lfr-editable-type=\"text\">Heading Example</h1>",
			StringPool.BLANK, false, StringPool.BLANK, null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, _serviceContext);
	}

	private JournalArticle _addJournalArticle() throws Exception {
		_ddmFormField = _createDDMFormField();
		_fieldContent = RandomTestUtil.randomString();

		return JournalTestUtil.addJournalArticle(
			_dataDefinitionResourceFactory, _ddmFormField,
			_ddmFormValuesToFieldsConverter, _fieldContent, _group.getGroupId(),
			_journalConverter);
	}

	private void _assertRenderLayoutHTML(String... strings) throws Exception {
		String html = ContentLayoutTestUtil.getRenderLayoutHTML(
			_layout, _layoutServiceContextHelper, _layoutStructureProvider,
			_segmentsExperienceId);

		for (String string : strings) {
			Assert.assertTrue(
				html + " not contains " + string, html.contains(string));
		}
	}

	private DDMFormField _createDDMFormField() {
		DDMFormField ddmFormField = new DDMFormField(
			"name", DDMFormFieldTypeConstants.TEXT);

		ddmFormField.setDataType("text");
		ddmFormField.setIndexType("text");

		LocalizedValue localizedValue = new LocalizedValue(_locale);

		localizedValue.addString(_locale, RandomTestUtil.randomString(10));

		ddmFormField.setLabel(localizedValue);

		ddmFormField.setLocalizable(true);

		return ddmFormField;
	}

	private void _mapToContentDisplay(
			String className, long classNameId, long classTypeId, long classPK,
			String fieldName)
		throws Exception {

		TemplateEntry templateEntry = TemplateTestUtil.addTemplateEntry(
			className, String.valueOf(classTypeId),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			TemplateTestUtil.getSampleScriptFTL(fieldName), _serviceContext);

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					"itemSelector",
					JSONUtil.put(
						"className", className
					).put(
						"classNameId", String.valueOf(classNameId)
					).put(
						"classPK", String.valueOf(classPK)
					).put(
						"classTypeId", String.valueOf(classTypeId)
					).put(
						"template",
						JSONUtil.put(
							"infoItemRendererKey",
							"com.liferay.template.internal.info.item." +
								"renderer.TemplateInfoItemTemplatedRenderer"
						).put(
							"templateKey",
							String.valueOf(templateEntry.getTemplateEntryId())
						)
					))
			).toString(),
			_fragmentRendererRegistry.getFragmentRenderer(
				"com.liferay.fragment.internal.renderer." +
					"ContentObjectFragmentRenderer"),
			_draftLayout, null, 0, _segmentsExperienceId);

		ContentLayoutTestUtil.publishLayout(_draftLayout, _layout);
	}

	private void _mapToInfoField(
			String className, long classTypeId, long classPK, String fieldName,
			String infoItemFormVariationKey)
		throws Exception {

		InfoField infoField = TemplateTestUtil.addTemplateEntryInfoField(
			fieldName, className, infoItemFormVariationKey,
			_infoItemServiceRegistry, _serviceContext);

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					"element-text",
					JSONUtil.put(
						"className", className
					).put(
						"classNameId",
						String.valueOf(_portal.getClassNameId(className))
					).put(
						"classPK", String.valueOf(classPK)
					).put(
						"classTypeId", String.valueOf(classTypeId)
					).put(
						"fieldId", infoField.getUniqueId()
					))
			).toString(),
			_fragmentEntry.getCss(), _fragmentEntry.getConfiguration(),
			_fragmentEntry.getFragmentEntryId(), _fragmentEntry.getHtml(),
			_fragmentEntry.getJs(), _draftLayout,
			_fragmentEntry.getFragmentEntryKey(), _fragmentEntry.getType(),
			null, 0, _segmentsExperienceId);

		ContentLayoutTestUtil.publishLayout(_draftLayout, _layout);
	}

	private AssetCategory _assetCategory;
	private BlogsEntry _blogsEntry;

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Inject
	private DataDefinitionResource.Factory _dataDefinitionResourceFactory;

	private DDMFormField _ddmFormField;

	@Inject
	private DDMFormValuesToFieldsConverter _ddmFormValuesToFieldsConverter;

	@Inject
	private DLAppService _dlAppService;

	private DLFileEntry _dlFileEntry;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	private Layout _draftLayout;
	private String _fieldContent;

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	private FragmentEntry _fragmentEntry;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Inject
	private FragmentRendererRegistry _fragmentRendererRegistry;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	private JournalArticle _journalArticle;

	@Inject
	private JournalConverter _journalConverter;

	private Layout _layout;

	@Inject
	private LayoutServiceContextHelper _layoutServiceContextHelper;

	@Inject
	private LayoutStructureProvider _layoutStructureProvider;

	private Locale _locale;

	@Inject
	private Portal _portal;

	private long _segmentsExperienceId;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceContext _serviceContext;

}