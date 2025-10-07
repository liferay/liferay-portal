/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLinkService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.test.util.IndexerFixture;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Ricardo Couso
 */
@RunWith(Arquillian.class)
public class LayoutPublishedSearchTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_setUpLayoutIndexerFixture();
	}

	@Test
	public void testContentLayoutAfterExecuteReindexAll() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		String content = RandomTestUtil.randomString();

		_addFragmentEntryLinkWithNoEditableTextToLayout(draftLayout, content);

		_layoutIndexerFixture.searchNoOne(content);

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		_layoutIndexerFixture.searchOnlyOne(content);

		indexer.reindex(new String[] {String.valueOf(_group.getCompanyId())});

		_layoutIndexerFixture.searchOnlyOne(content);
	}

	@Test
	public void testContentLayoutAfterRemoveViewPermissionForGuest()
		throws Exception {

		String name = RandomTestUtil.randomString();

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group, name);

		Layout draftLayout = layout.fetchDraftLayout();

		_layoutIndexerFixture.searchNoOne(name);

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		_layoutIndexerFixture.searchOnlyOne(name);

		RoleTestUtil.removeResourcePermission(
			RoleConstants.GUEST, AssetCategory.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(layout.getPlid()), ActionKeys.VIEW);

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		_layoutIndexerFixture.searchOnlyOne(name);
	}

	@Test
	public void testContentLayoutWithAndWithoutCategory() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		AssetCategory assetCategory = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		_layoutIndexerFixture.searchNoOne(
			assetCategory.getTitle(LocaleUtil.getDefault()));

		_layoutLocalService.updateAsset(
			TestPropsValues.getUserId(), layout,
			new long[] {assetCategory.getCategoryId()}, new String[0]);

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		_layoutIndexerFixture.searchOnlyOne(
			assetCategory.getTitle(LocaleUtil.getDefault()));

		_layoutLocalService.updateAsset(
			TestPropsValues.getUserId(), layout, new long[0], new String[0]);

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		_layoutIndexerFixture.searchNoOne(
			assetCategory.getTitle(LocaleUtil.getDefault()));
	}

	@Test
	public void testContentLayoutWithInlineContentInADropZone()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					_group.getGroupId(), draftLayout.getPlid());

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getDefaultSegmentsExperienceData());

		LayoutStructureItem rowStyledLayoutStructureItem =
			layoutStructure.addRowStyledLayoutStructureItem(
				layoutStructure.getMainItemId(), 0, 1);

		LayoutStructureItem columnLayoutStructureItem =
			layoutStructure.addColumnLayoutStructureItem(
				rowStyledLayoutStructureItem.getItemId(), 0);

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), null,
				ServiceContextTestUtil.getServiceContext());

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				fragmentCollection.getFragmentCollectionId(),
				StringUtil.randomString(), StringUtil.randomString(),
				RandomTestUtil.randomString(),
				"<div class=\"fragment_1\"><h1> Drop Zone 1 </h1>" +
					"<lfr-drop-zone></lfr-drop-zone></div>",
				RandomTestUtil.randomString(), false, "{fieldSets: []}", null,
				0, false, false, FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkService.addFragmentEntryLink(
				null, _group.getGroupId(), 0,
				fragmentEntry.getFragmentEntryId(),
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(draftLayout.getPlid()),
				draftLayout.getPlid(), fragmentEntry.getCss(),
				fragmentEntry.getHtml(), fragmentEntry.getJs(),
				fragmentEntry.getConfiguration(), StringPool.BLANK,
				StringPool.BLANK, 0, fragmentEntry.getFragmentEntryKey(),
				fragmentEntry.getType(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		LayoutStructureItem fragmentStyledLayoutStructureItem =
			layoutStructure.addFragmentStyledLayoutStructureItem(
				fragmentEntryLink.getFragmentEntryLinkId(),
				columnLayoutStructureItem.getItemId(), 0);

		LayoutStructureItem fragmentDropZoneLayoutStructureItem =
			layoutStructure.addFragmentDropZoneLayoutStructureItem(
				fragmentStyledLayoutStructureItem.getItemId(), 0);

		FragmentEntry contributedFragmentEntry =
			_fragmentCollectionContributorRegistry.getFragmentEntry(
				"BASIC_COMPONENT-heading");

		String deutschContent = RandomTestUtil.randomString();

		FragmentEntryLink inlineFragmentEntryLink =
			_fragmentEntryLinkService.addFragmentEntryLink(
				null, _group.getGroupId(), 0,
				contributedFragmentEntry.getFragmentEntryId(),
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(draftLayout.getPlid()),
				draftLayout.getPlid(), contributedFragmentEntry.getCss(),
				contributedFragmentEntry.getHtml(),
				contributedFragmentEntry.getJs(),
				contributedFragmentEntry.getConfiguration(),
				JSONUtil.put(
					FragmentEntryProcessorConstants.
						KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
					JSONUtil.put(
						"element-text",
						JSONUtil.put(
							"config", JSONFactoryUtil.createJSONObject()
						).put(
							"defaultValue", "default value"
						).put(
							draftLayout.getDefaultLanguageId(),
							RandomTestUtil.randomString()
						).put(
							"de_DE", deutschContent
						))
				).toString(),
				StringPool.BLANK, 0,
				contributedFragmentEntry.getFragmentEntryKey(),
				contributedFragmentEntry.getType(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		layoutStructure.addFragmentStyledLayoutStructureItem(
			inlineFragmentEntryLink.getFragmentEntryLinkId(),
			fragmentDropZoneLayoutStructureItem.getItemId(), 0);

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				TestPropsValues.getUserId(), _group.getGroupId(),
				draftLayout.getPlid(),
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(draftLayout.getPlid()),
				layoutStructure.toString());

		_layoutIndexerFixture.searchNoOne(deutschContent, LocaleUtil.GERMANY);

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		_layoutIndexerFixture.searchOnlyOne(deutschContent, LocaleUtil.GERMANY);
	}

	@Test
	public void testContentLayoutWithNoEditableText() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		String content = RandomTestUtil.randomString();

		_addFragmentEntryLinkWithNoEditableTextToLayout(draftLayout, content);

		_layoutIndexerFixture.searchNoOne(content);

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		_layoutIndexerFixture.searchOnlyOne(content);
	}

	@Test
	public void testContentLayoutWithoutPublishedVersion() throws Exception {
		String name = RandomTestUtil.randomString();

		LayoutTestUtil.addTypeContentLayout(_group, name);

		_layoutIndexerFixture.searchNoOne(name);
	}

	@Test
	public void testPrivateContentLayoutWithInlineContent() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(
			_group, true, false);

		String content = RandomTestUtil.randomString();

		_addFragmentEntryLinkWithInlineContentToLayout(
			layout.fetchDraftLayout(), content);

		_layoutIndexerFixture.searchNoOne(content);

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		_layoutIndexerFixture.searchOnlyOne(content);
	}

	@Test
	public void testPublicContentLayoutWithInlineContent() throws Exception {
		String name = RandomTestUtil.randomString();

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group, name);

		_layoutIndexerFixture.searchNoOne(name);

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		_layoutIndexerFixture.searchOnlyOne(name);
	}

	@Inject(
		filter = "indexer.class.name=com.liferay.portal.kernel.model.Layout"
	)
	protected Indexer<Layout> indexer;

	private void _addFragmentEntryLinkToLayout(
			long fragmentEntryLinkId, long plid, long segmentsExperienceId)
		throws Exception {

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(_group.getGroupId(), plid);

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getDefaultSegmentsExperienceData());

		LayoutStructureItem rowStyledLayoutStructureItem =
			layoutStructure.addRowStyledLayoutStructureItem(
				layoutStructure.getMainItemId(), 0, 1);

		LayoutStructureItem columnLayoutStructureItem =
			layoutStructure.addColumnLayoutStructureItem(
				rowStyledLayoutStructureItem.getItemId(), 0);

		layoutStructure.addFragmentStyledLayoutStructureItem(
			fragmentEntryLinkId, columnLayoutStructureItem.getItemId(), 0);

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				TestPropsValues.getUserId(), _group.getGroupId(), plid,
				segmentsExperienceId, layoutStructure.toString());
	}

	private void _addFragmentEntryLinkWithInlineContentToLayout(
			Layout draftLayout, String value)
		throws Exception {

		FragmentEntry contributedFragmentEntry =
			_fragmentCollectionContributorRegistry.getFragmentEntry(
				"BASIC_COMPONENT-heading");

		long defaultSegmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid());

		FragmentEntryLink inlineFragmentEntryLink =
			_fragmentEntryLinkService.addFragmentEntryLink(
				null, _group.getGroupId(), 0,
				contributedFragmentEntry.getFragmentEntryId(),
				defaultSegmentsExperienceId, draftLayout.getPlid(),
				contributedFragmentEntry.getCss(),
				contributedFragmentEntry.getHtml(),
				contributedFragmentEntry.getJs(),
				contributedFragmentEntry.getConfiguration(),
				JSONUtil.put(
					FragmentEntryProcessorConstants.
						KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
					JSONUtil.put(
						"element-text",
						JSONUtil.put(
							"config", JSONFactoryUtil.createJSONObject()
						).put(
							"defaultValue", "default value"
						).put(
							draftLayout.getDefaultLanguageId(), value
						))
				).toString(),
				StringPool.BLANK, 0,
				contributedFragmentEntry.getFragmentEntryKey(),
				contributedFragmentEntry.getType(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_addFragmentEntryLinkToLayout(
			inlineFragmentEntryLink.getFragmentEntryLinkId(),
			draftLayout.getPlid(), defaultSegmentsExperienceId);
	}

	private void _addFragmentEntryLinkWithNoEditableTextToLayout(
			Layout draftLayout, String value)
		throws Exception {

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), null,
				ServiceContextTestUtil.getServiceContext());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				fragmentCollection.getGroupId());

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(),
				serviceContext.getScopeGroupId(),
				fragmentCollection.getFragmentCollectionId(), StringPool.BLANK,
				RandomTestUtil.randomString(), StringPool.BLANK,
				"<div>" + value + "</div>", StringPool.BLANK, false,
				StringPool.BLANK, null, 0, false, false,
				FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext());

		long defaultSegmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid());

		FragmentEntryLink inlineFragmentEntryLink =
			_fragmentEntryLinkService.addFragmentEntryLink(
				null, _group.getGroupId(), 0,
				fragmentEntry.getFragmentEntryId(), defaultSegmentsExperienceId,
				draftLayout.getPlid(), fragmentEntry.getCss(),
				fragmentEntry.getHtml(), fragmentEntry.getJs(),
				fragmentEntry.getConfiguration(), StringPool.BLANK,
				StringPool.BLANK, 0, fragmentEntry.getFragmentEntryKey(),
				fragmentEntry.getType(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_addFragmentEntryLinkToLayout(
			inlineFragmentEntryLink.getFragmentEntryLinkId(),
			draftLayout.getPlid(), defaultSegmentsExperienceId);
	}

	private void _setUpLayoutIndexerFixture() {
		_layoutIndexerFixture = new IndexerFixture<>(Layout.class);
	}

	@Inject
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Inject
	private FragmentEntryLinkService _fragmentEntryLinkService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private IndexerFixture<Layout> _layoutIndexerFixture;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}