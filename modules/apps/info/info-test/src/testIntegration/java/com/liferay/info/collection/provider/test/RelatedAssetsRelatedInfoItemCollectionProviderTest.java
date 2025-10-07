/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.collection.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.link.service.AssetLinkLocalService;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.renderer.DefaultFragmentRendererContext;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererRegistry;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.info.list.provider.item.selector.criterion.InfoListProviderItemSelectorReturnType;
import com.liferay.info.search.InfoSearchClassMapperRegistry;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.LayoutServiceContextHelper;
import com.liferay.layout.util.structure.CollectionStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.util.Locale;
import java.util.Map;

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
public class RelatedAssetsRelatedInfoItemCollectionProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_locale = _portal.getSiteDefaultLocale(_group);

		_languageId = LocaleUtil.toLanguageId(_locale);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);

		_blogsEntry = _addBlogsEntry();
		_dlFileEntry = _addDLFileEntry();
		_fragmentEntry = _addFragmentEntry();
		_journalArticle = _addJournalArticle();

		_addAssetLinks();
	}

	@Test
	@TestInfo({"LPS-112360", "LPS-127023"})
	public void testCollectionDisplayWithInfoListRenderer() throws Exception {
		Layout layout = _addDefaultDisplayPageTemplateLayout(
			_portal.getClassNameId(FileEntry.class.getName()),
			_dlFileEntry.getFileEntryTypeId());

		Layout draftLayout = layout.fetchDraftLayout();

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid());

		_mapCollectionDisplayWithInfoListRenderer(
			layout, "FileEntry_title", segmentsExperienceId);

		_assertRenderLayoutHTML(
			_getInfoItemAttributesMap(
				FileEntry.class.getName(), _dlFileEntry.getFileEntryId(),
				_dlAppService.getFileEntry(_dlFileEntry.getFileEntryId())),
			layout, segmentsExperienceId);
	}

	@Test
	@TestInfo({"LPS-112360", "LPS-127023"})
	public void testMapContentDisplayInCollectionDisplay() throws Exception {
		Layout layout = _addDefaultDisplayPageTemplateLayout(
			_portal.getClassNameId(BlogsEntry.class.getName()), 0);

		Layout draftLayout = layout.fetchDraftLayout();

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid());

		_mapContentDisplayInCollectionDisplay(
			layout, "BlogsEntry_title", segmentsExperienceId);

		_assertRenderLayoutHTML(
			_getInfoItemAttributesMap(
				BlogsEntry.class.getName(), _blogsEntry.getEntryId(),
				_blogsEntry),
			layout, segmentsExperienceId);
	}

	@Test
	@TestInfo({"LPD-32486", "LPS-112360", "LPS-127023"})
	public void testMapInfoFieldInCollectionDisplay() throws Exception {
		Layout layout = _addDefaultDisplayPageTemplateLayout(
			_portal.getClassNameId(JournalArticle.class.getName()),
			_journalArticle.getDDMStructureId());

		Layout draftLayout = layout.fetchDraftLayout();

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid());

		_mapInfoFieldInCollectionDisplay(
			layout, "JournalArticle_title", segmentsExperienceId);

		Group companyGroup = _groupLocalService.getCompanyGroup(
			TestPropsValues.getCompanyId());

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			companyGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		try {
			AssetEntry assetEntry = _assetEntryLocalService.getEntry(
				JournalArticle.class.getName(),
				journalArticle.getResourcePrimKey());

			_assetLinkLocalService.addLink(
				TestPropsValues.getUserId(), assetEntry.getEntryId(),
				_journalArticleAssetEntry.getEntryId(), 0, 1);

			String html = _assertRenderLayoutHTML(
				_getInfoItemAttributesMap(
					JournalArticle.class.getName(),
					_journalArticle.getResourcePrimKey(), _journalArticle),
				layout, segmentsExperienceId);

			Assert.assertTrue(html, html.contains(journalArticle.getTitle()));
		}
		finally {
			_journalArticleLocalService.deleteArticle(journalArticle);
		}
	}

	@Test
	@TestInfo("LPS-127024")
	public void testMapInfoFieldInCollectionDisplayNestedInCollectionDisplayWithAssetEntriesWithSameAssetCategory()
		throws Exception {

		Layout layout = _addDefaultDisplayPageTemplateLayout(
			_portal.getClassNameId(AssetCategory.class.getName()), 0);

		Layout draftLayout = layout.fetchDraftLayout();

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid());

		_mapInfoFieldInCollectionDisplayNestedInCollectionDisplay(
			"com.liferay.asset.categories.admin.web.internal.info.collection." +
				"provider.AssetEntriesWithSameAssetCategoryRelatedInfoItem" +
					"CollectionProvider",
			layout, "AssetCategory_name", segmentsExperienceId);

		AssetCategory assetCategory = _addAssetCategory();

		String html = _assertRenderLayoutHTML(
			_getInfoItemAttributesMap(
				AssetCategory.class.getName(), assetCategory.getCategoryId(),
				assetCategory),
			2, layout, segmentsExperienceId);

		Assert.assertTrue(html, html.contains(assetCategory.getName()));
	}

	@Test
	@TestInfo("LPS-127024")
	public void testMapInfoFieldInCollectionDisplayNestedInCollectionDisplayWithHighestRatedAssets()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid());

		_mapInfoFieldInCollectionDisplayNestedInCollectionDisplay(
			"com.liferay.asset.internal.info.collection.provider." +
				"HighestRatedAssetsInfoCollectionProvider",
			layout, null, segmentsExperienceId);

		_assertRenderLayoutHTML(
			_getInfoItemAttributesMap(
				JournalArticle.class.getName(),
				_journalArticle.getResourcePrimKey(), _journalArticle),
			2, layout, segmentsExperienceId);
	}

	private AssetCategory _addAssetCategory() throws Exception {
		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		AssetCategory assetCategory = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		long[] assetCategoryIds = {assetCategory.getCategoryId()};

		_blogsEntryLocalService.updateAsset(
			TestPropsValues.getUserId(), _blogsEntry, assetCategoryIds,
			new String[0],
			new long[] {
				_dlFileEntryAssetEntry.getEntryId(),
				_journalArticleAssetEntry.getEntryId()
			},
			null);
		_journalArticleLocalService.updateAsset(
			TestPropsValues.getUserId(), _journalArticle, assetCategoryIds,
			new String[0],
			new long[] {
				_blogsEntryAssetEntry.getEntryId(),
				_dlFileEntryAssetEntry.getEntryId()
			},
			null);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId(),
				assetCategoryIds);

		serviceContext.setAssetLinkEntryIds(
			new long[] {
				_blogsEntryAssetEntry.getEntryId(),
				_journalArticleAssetEntry.getEntryId()
			});

		_dlAppService.updateFileEntry(
			_dlFileEntry.getFileEntryId(), _dlFileEntry.getFileName(),
			_dlFileEntry.getMimeType(), _dlFileEntry.getTitle(),
			StringPool.BLANK, _dlFileEntry.getDescription(), StringPool.BLANK,
			DLVersionNumberIncrease.MINOR, new byte[0], null, null, null,
			serviceContext);

		return assetCategory;
	}

	private void _addAssetLinks() throws Exception {
		_blogsEntryAssetEntry = _assetEntryLocalService.getEntry(
			BlogsEntry.class.getName(), _blogsEntry.getEntryId());
		_dlFileEntryAssetEntry = _assetEntryLocalService.getEntry(
			DLFileEntry.class.getName(), _dlFileEntry.getFileEntryId());

		_assetLinkLocalService.addLink(
			TestPropsValues.getUserId(), _blogsEntryAssetEntry.getEntryId(),
			_dlFileEntryAssetEntry.getEntryId(), 0, 1);

		_journalArticleAssetEntry = _assetEntryLocalService.getEntry(
			JournalArticle.class.getName(),
			_journalArticle.getResourcePrimKey());

		_assetLinkLocalService.addLink(
			TestPropsValues.getUserId(), _blogsEntryAssetEntry.getEntryId(),
			_journalArticleAssetEntry.getEntryId(), 0, 1);
		_assetLinkLocalService.addLink(
			TestPropsValues.getUserId(), _dlFileEntryAssetEntry.getEntryId(),
			_journalArticleAssetEntry.getEntryId(), 0, 1);
	}

	private BlogsEntry _addBlogsEntry() throws Exception {
		return _blogsEntryLocalService.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			DateUtil.newDate(System.currentTimeMillis() - Time.DAY), true, true,
			new String[0], StringPool.BLANK, null, null, _serviceContext);
	}

	private Layout _addDefaultDisplayPageTemplateLayout(
			long classNameId, long classTypeId)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(), classNameId, classTypeId, true,
				WorkflowConstants.STATUS_APPROVED);

		return _layoutLocalService.getLayout(layoutPageTemplateEntry.getPlid());
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

	private void _addHeadingWithMappedField(
			Layout layout, String mappedField, long segmentsExperienceId)
		throws Exception {

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0, 0,
				segmentsExperienceId, layout.getPlid(), _fragmentEntry.getCss(),
				_fragmentEntry.getHtml(), _fragmentEntry.getJs(),
				_fragmentEntry.getConfiguration(),
				JSONUtil.put(
					FragmentEntryProcessorConstants.
						KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
					JSONUtil.put(
						"element-text",
						JSONUtil.put("mappedField", mappedField))
				).toString(),
				StringPool.BLANK, 0, _fragmentEntry.getFragmentEntryKey(),
				_fragmentEntry.getType(), _serviceContext),
			layout, null, 0, segmentsExperienceId);
	}

	private void _addInfoFieldInCollectionDisplayToLayout(
			Layout layout, String parentItemId, long segmentsExperienceId)
		throws Exception {

		ContentLayoutTestUtil.addCollectionDisplayToLayout(
			JSONUtil.put(
				"itemType", AssetEntry.class.getName()
			).put(
				"key",
				"com.liferay.asset.internal.info.collection.provider." +
					"RelatedAssetsRelatedInfoItemCollectionProvider"
			).put(
				"type", InfoListProviderItemSelectorReturnType.class.getName()
			),
			layout, _layoutStructureProvider, null, parentItemId, 0,
			segmentsExperienceId,
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0, 0,
				segmentsExperienceId, layout.getPlid(), _fragmentEntry.getCss(),
				_fragmentEntry.getHtml(), _fragmentEntry.getJs(),
				_fragmentEntry.getConfiguration(),
				JSONUtil.put(
					FragmentEntryProcessorConstants.
						KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
					JSONUtil.put(
						"element-text",
						JSONUtil.put("collectionFieldId", "AssetEntry_title"))
				).toString(),
				StringPool.BLANK, 0, _fragmentEntry.getFragmentEntryKey(),
				_fragmentEntry.getType(), _serviceContext));
	}

	private JournalArticle _addJournalArticle() throws Exception {
		return JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);
	}

	private String _assertRenderLayoutHTML(
			Map<String, Object> attributes, int count, Layout layout,
			long segmentsExperienceId)
		throws Exception {

		String html = ContentLayoutTestUtil.getRenderLayoutHTML(
			attributes, layout, _layoutServiceContextHelper,
			_layoutStructureProvider, segmentsExperienceId);

		Assert.assertEquals(
			html, count, StringUtil.count(html, _blogsEntry.getTitle()));
		Assert.assertEquals(
			html, count, StringUtil.count(html, _dlFileEntry.getTitle()));
		Assert.assertEquals(
			html, count, StringUtil.count(html, _journalArticle.getTitle()));

		return html;
	}

	private String _assertRenderLayoutHTML(
			Map<String, Object> attributes, Layout layout,
			long segmentsExperienceId)
		throws Exception {

		return _assertRenderLayoutHTML(
			attributes, 1, layout, segmentsExperienceId);
	}

	private Map<String, Object> _getInfoItemAttributesMap(
		String className, long classPK, Object infoItem) {

		LayoutDisplayPageProvider<?> layoutDisplayPageProvider =
			_layoutDisplayPageProviderRegistry.
				getLayoutDisplayPageProviderByClassName(className);

		return HashMapBuilder.<String, Object>put(
			InfoDisplayWebKeys.INFO_ITEM, infoItem
		).put(
			InfoDisplayWebKeys.INFO_ITEM_DETAILS,
			() -> {
				InfoItemDetailsProvider infoItemDetailsProvider =
					_infoItemServiceRegistry.getFirstInfoItemService(
						InfoItemDetailsProvider.class, className);

				return infoItemDetailsProvider.getInfoItemDetails(infoItem);
			}
		).put(
			LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER,
			layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				new InfoItemReference(className, classPK))
		).put(
			LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_PROVIDER,
			layoutDisplayPageProvider
		).build();
	}

	private void _mapCollectionDisplayWithInfoListRenderer(
			Layout layout, String mappedField, long segmentsExperienceId)
		throws Exception {

		Layout draftLayout = layout.fetchDraftLayout();

		_addHeadingWithMappedField(
			draftLayout, mappedField, segmentsExperienceId);

		ContentLayoutTestUtil.addCollectionDisplayToLayout(
			JSONUtil.put(
				"itemType", AssetEntry.class.getName()
			).put(
				"key",
				"com.liferay.asset.internal.info.collection.provider." +
					"RelatedAssetsRelatedInfoItemCollectionProvider"
			).put(
				"type", InfoListProviderItemSelectorReturnType.class.getName()
			),
			draftLayout, _layoutStructureProvider,
			"com.liferay.asset.info.internal.list.renderer." +
				"AssetEntryBorderedBasicInfoListRenderer",
			null, 0, segmentsExperienceId);

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);
	}

	private void _mapContentDisplayInCollectionDisplay(
			Layout layout, String mappedField, long segmentsExperienceId)
		throws Exception {

		Layout draftLayout = layout.fetchDraftLayout();

		_addHeadingWithMappedField(
			draftLayout, mappedField, segmentsExperienceId);

		FragmentRenderer fragmentRenderer =
			_fragmentRendererRegistry.getFragmentRenderer(
				"com.liferay.fragment.internal.renderer." +
					"ContentObjectFragmentRenderer");

		DefaultFragmentRendererContext defaultFragmentRendererContext =
			new DefaultFragmentRendererContext(null);

		ContentLayoutTestUtil.addCollectionDisplayToLayout(
			JSONUtil.put(
				"itemType", AssetEntry.class.getName()
			).put(
				"key",
				"com.liferay.asset.internal.info.collection.provider." +
					"RelatedAssetsRelatedInfoItemCollectionProvider"
			).put(
				"type", InfoListProviderItemSelectorReturnType.class.getName()
			),
			draftLayout, _layoutStructureProvider, null, null, 0,
			segmentsExperienceId,
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), draftLayout.getGroupId(), 0,
				0, segmentsExperienceId, draftLayout.getPlid(),
				StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
				JSONFactoryUtil.toString(
					fragmentRenderer.getConfigurationJSONObject(
						defaultFragmentRendererContext)),
				"{}", StringPool.BLANK, 0, fragmentRenderer.getKey(),
				fragmentRenderer.getType(), _serviceContext));

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);
	}

	private void _mapInfoFieldInCollectionDisplay(
			Layout layout, String mappedField, long segmentsExperienceId)
		throws Exception {

		Layout draftLayout = layout.fetchDraftLayout();

		_addHeadingWithMappedField(
			draftLayout, mappedField, segmentsExperienceId);

		_addInfoFieldInCollectionDisplayToLayout(
			draftLayout, null, segmentsExperienceId);

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);
	}

	private void _mapInfoFieldInCollectionDisplayNestedInCollectionDisplay(
			String infoCollectionProviderKey, Layout layout, String mappedField,
			long segmentsExperienceId)
		throws Exception {

		Layout draftLayout = layout.fetchDraftLayout();

		if (Validator.isNotNull(mappedField)) {
			_addHeadingWithMappedField(
				draftLayout, mappedField, segmentsExperienceId);
		}

		String itemId = ContentLayoutTestUtil.addCollectionDisplayToLayout(
			JSONUtil.put(
				"itemType", AssetEntry.class.getName()
			).put(
				"key", infoCollectionProviderKey
			).put(
				"type", InfoListProviderItemSelectorReturnType.class.getName()
			),
			draftLayout, _layoutStructureProvider, null, null, 0,
			segmentsExperienceId);

		LayoutStructure layoutStructure =
			_layoutStructureProvider.getLayoutStructure(
				draftLayout.getPlid(), segmentsExperienceId);

		CollectionStyledLayoutStructureItem
			collectionStyledLayoutStructureItem =
				(CollectionStyledLayoutStructureItem)
					layoutStructure.getLayoutStructureItem(itemId);

		_addInfoFieldInCollectionDisplayToLayout(
			draftLayout,
			collectionStyledLayoutStructureItem.getChildrenItemId(0),
			segmentsExperienceId);

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);
	}

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private AssetLinkLocalService _assetLinkLocalService;

	private BlogsEntry _blogsEntry;
	private AssetEntry _blogsEntryAssetEntry;

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Inject
	private DLAppService _dlAppService;

	private DLFileEntry _dlFileEntry;
	private AssetEntry _dlFileEntryAssetEntry;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

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
	private GroupLocalService _groupLocalService;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Inject
	private InfoSearchClassMapperRegistry _infoSearchClassMapperRegistry;

	private JournalArticle _journalArticle;
	private AssetEntry _journalArticleAssetEntry;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	private String _languageId;

	@Inject
	private LayoutDisplayPageProviderRegistry
		_layoutDisplayPageProviderRegistry;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutServiceContextHelper _layoutServiceContextHelper;

	@Inject
	private LayoutStructureProvider _layoutStructureProvider;

	private Locale _locale;

	@Inject
	private Portal _portal;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceContext _serviceContext;

}