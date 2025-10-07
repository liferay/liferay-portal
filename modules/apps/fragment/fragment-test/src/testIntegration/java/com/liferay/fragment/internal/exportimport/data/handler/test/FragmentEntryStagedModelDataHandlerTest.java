/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.test.util.lar.BaseStagedModelDataHandlerTestCase;
import com.liferay.fragment.configuration.FragmentServiceConfiguration;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.DefaultFragmentRendererContext;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.fragment.test.util.FragmentTestUtil;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Kyle Miho
 */
@RunWith(Arquillian.class)
public class FragmentEntryStagedModelDataHandlerTest
	extends BaseStagedModelDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_layout = LayoutTestUtil.addTypeContentLayout(stagingGroup);
	}

	@Test
	public void testDeletePreviewFileEntryWithStagingEnabled()
		throws Exception {

		Map<String, List<StagedModel>> dependentStagedModelsMap =
			addDependentStagedModelsMap(stagingGroup);

		StagedModel stagedModel = addStagedModel(
			stagingGroup, dependentStagedModelsMap);

		FragmentEntry fragmentEntry = (FragmentEntry)stagedModel;

		Repository repository = PortletFileRepositoryUtil.addPortletRepository(
			stagingGroup.getGroupId(), FragmentPortletKeys.FRAGMENT,
			ServiceContextTestUtil.getServiceContext(
				stagingGroup.getGroupId(), TestPropsValues.getUserId()));

		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		FileEntry fileEntry = PortletFileRepositoryUtil.addPortletFileEntry(
			null, stagingGroup.getGroupId(), TestPropsValues.getUserId(),
			FragmentEntry.class.getName(), fragmentEntry.getFragmentEntryId(),
			FragmentPortletKeys.FRAGMENT, repository.getDlFolderId(),
			classLoader.getResourceAsStream(
				"com/liferay/fragment/dependencies/liferay.png"),
			RandomTestUtil.randomString(), ContentTypes.IMAGE_PNG, false);

		stagedModel = _fragmentEntryLocalService.updateFragmentEntry(
			fragmentEntry.getFragmentEntryId(), fileEntry.getFileEntryId());

		_exportImportStagedModel(stagedModel);

		StagedModel importedStagedModel = getStagedModel(
			stagedModel.getUuid(), liveGroup);

		FragmentEntry importedFragmentEntry =
			(FragmentEntry)importedStagedModel;

		long importedPreviewFileEntryId =
			importedFragmentEntry.getPreviewFileEntryId();

		fileEntry = PortletFileRepositoryUtil.getPortletFileEntry(
			importedPreviewFileEntryId);

		Assert.assertNotNull(fileEntry);

		PortletFileRepositoryUtil.deletePortletFileEntry(
			fileEntry.getFileEntryId());

		stagedModel = _fragmentEntryLocalService.updateFragmentEntry(
			fragmentEntry.getFragmentEntryId(), 0);

		_exportImportStagedModel(stagedModel);

		importedStagedModel = getStagedModel(stagedModel.getUuid(), liveGroup);

		importedFragmentEntry = (FragmentEntry)importedStagedModel;

		Assert.assertEquals(0, importedFragmentEntry.getPreviewFileEntryId());

		fileEntry = null;

		try {
			fileEntry = PortletFileRepositoryUtil.getPortletFileEntry(
				importedPreviewFileEntryId);
		}
		catch (NoSuchFileEntryException noSuchFileEntryException) {
			Assert.assertEquals(
				StringBundler.concat(
					"No FileEntry exists with the key {fileEntryId=",
					importedPreviewFileEntryId, "}"),
				noSuchFileEntryException.getMessage());
		}

		Assert.assertNull(fileEntry);
	}

	@Test
	public void testUpdateFragmentEntryWithFragmentEntryLink()
		throws Exception {

		Map<String, List<StagedModel>> dependentStagedModelsMap =
			addDependentStagedModelsMap(stagingGroup);

		StagedModel stagedModel = addStagedModel(
			stagingGroup, dependentStagedModelsMap);

		FragmentEntry fragmentEntry = (FragmentEntry)stagedModel;

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), stagingGroup.getGroupId(), 0,
				fragmentEntry.getFragmentEntryId(),
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(_layout.getPlid()),
				stagingGroup.getDefaultPublicPlid(), fragmentEntry.getCss(),
				fragmentEntry.getHtml(), fragmentEntry.getJs(),
				fragmentEntry.getConfiguration(), StringPool.BLANK,
				StringPool.BLANK, 0, StringPool.BLANK, fragmentEntry.getType(),
				ServiceContextTestUtil.getServiceContext(
					stagingGroup.getGroupId(), TestPropsValues.getUserId()));

		stagedModel = _fragmentEntryLocalService.updateFragmentEntry(
			TestPropsValues.getUserId(), fragmentEntry.getFragmentEntryId(),
			fragmentEntry.getFragmentCollectionId(), fragmentEntry.getName(),
			"css", "html", "js", false, "{fieldSets: []}", StringPool.BLANK,
			fragmentEntry.getPreviewFileEntryId(), false,
			fragmentEntry.getTypeOptions(), WorkflowConstants.STATUS_APPROVED);

		_exportImportStagedModel(stagedModel);

		StagedModel importedStagedModel = getStagedModel(
			stagedModel.getUuid(), liveGroup);

		FragmentEntry importedFragmentEntry =
			(FragmentEntry)importedStagedModel;

		Assert.assertNotNull(importedStagedModel);

		Assert.assertNotEquals(
			fragmentEntryLink.getCss(), importedFragmentEntry.getCss());
		Assert.assertNotEquals(
			fragmentEntryLink.getHtml(), importedFragmentEntry.getHtml());
		Assert.assertNotEquals(
			fragmentEntryLink.getJs(), importedFragmentEntry.getJs());
		validateImportedStagedModel(stagedModel, importedStagedModel);
	}

	@Test
	@TestInfo({"LPS-129852", "LPS-167932"})
	public void testUpdateFragmentEntryWithFragmentEntryLinkAddingDropZone()
		throws Exception {

		FragmentEntry fragmentEntry = _addFragmentEntry(
			StringPool.BLANK, stagingGroup, "<div class=\"fragment_1\"></div>");

		Layout draftLayout = _layout.fetchDraftLayout();

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				stagingGroup.getGroupId(), TestPropsValues.getUserId());

		String itemId = ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), stagingGroup.getGroupId(), 0,
				fragmentEntry.getFragmentEntryId(), segmentsExperienceId,
				draftLayout.getPlid(), fragmentEntry.getCss(),
				fragmentEntry.getHtml(), fragmentEntry.getJs(),
				fragmentEntry.getConfiguration(), StringPool.BLANK,
				StringPool.BLANK, 0, StringPool.BLANK, fragmentEntry.getType(),
				serviceContext),
			draftLayout, null, 0, segmentsExperienceId);

		String dropZoneId1 = RandomTestUtil.randomString();
		String dropZoneId2 = RandomTestUtil.randomString();

		fragmentEntry = _updateFragmentEntryWithPropagation(
			fragmentEntry,
			StringBundler.concat(
				"<div class=\"fragment_1\"><h1> Drop Zone 1 </h1>",
				"<lfr-drop-zone data-lfr-drop-zone-id=\"", dropZoneId1,
				"\"></lfr-drop-zone><h1> Drop Zone 2 </h1>",
				"<lfr-drop-zone data-lfr-drop-zone-id=\"", dropZoneId2,
				"\"></lfr-drop-zone></div>"));

		Locale locale = _portal.getSiteDefaultLocale(stagingGroup);

		FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem =
			_addHeadingFragmentEntryLinks(
				itemId, draftLayout, locale, segmentsExperienceId,
				serviceContext, dropZoneId1 + "HeadingContent",
				dropZoneId2 + "HeadingContent");

		ContentLayoutTestUtil.publishLayout(draftLayout, _layout);

		FragmentEntryLink publishedFragmentEntryLink =
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				stagingGroup.getGroupId(),
				fragmentStyledLayoutStructureItem.getFragmentEntryLinkId(),
				_layout.getPlid());

		Company company = _companyLocalService.getCompany(
			stagingGroup.getCompanyId());

		_assertHTML(
			_getFragmentEntryLinkRenderHTML(
				company, publishedFragmentEntryLink, stagingGroup, _layout,
				locale),
			"<h1> Drop Zone 1 </h1>", dropZoneId1 + "HeadingContent",
			"<h1> Drop Zone 2 </h1>", dropZoneId2 + "HeadingContent");

		_exportImportStagedModel(fragmentEntry, _layout);

		Layout liveLayout = _layoutLocalService.fetchLayout(
			_layout.getUuid(), liveGroup.getGroupId(),
			_layout.isPrivateLayout());

		_assertHTML(
			_getFragmentEntryLinkRenderHTML(
				company,
				_fragmentEntryLinkLocalService.
					getFragmentEntryLinkByUuidAndGroupId(
						publishedFragmentEntryLink.getUuid(),
						liveGroup.getGroupId()),
				liveGroup, liveLayout, locale),
			"<h1> Drop Zone 1 </h1>", dropZoneId1 + "HeadingContent",
			"<h1> Drop Zone 2 </h1>", dropZoneId2 + "HeadingContent");

		String addedDropZoneId = RandomTestUtil.randomString();

		fragmentEntry = _updateFragmentEntryWithPropagation(
			fragmentEntry,
			StringBundler.concat(
				"<div class=\"fragment_1\"><h1> Drop Zone 1 </h1>",
				"<lfr-drop-zone data-lfr-drop-zone-id=\"", dropZoneId1,
				"\"></lfr-drop-zone><h1> Added Drop Zone </h1>",
				"<lfr-drop-zone data-lfr-drop-zone-id=\"", addedDropZoneId,
				"\"></lfr-drop-zone><h1> Drop Zone 2 </h1>",
				"<lfr-drop-zone data-lfr-drop-zone-id=\"", dropZoneId2,
				"\"></lfr-drop-zone></div>"));

		_assertHTML(
			_getFragmentEntryLinkRenderHTML(
				company,
				_fragmentEntryLinkLocalService.getFragmentEntryLink(
					publishedFragmentEntryLink.getFragmentEntryLinkId()),
				stagingGroup, _layout, locale),
			"<h1> Drop Zone 1 </h1>", dropZoneId1 + "HeadingContent",
			"<h1> Added Drop Zone </h1>", "<h1> Drop Zone 2 </h1>",
			dropZoneId2 + "HeadingContent");

		_exportImportStagedModel(fragmentEntry, _layout);

		_assertHTML(
			_getFragmentEntryLinkRenderHTML(
				company,
				_fragmentEntryLinkLocalService.
					getFragmentEntryLinkByUuidAndGroupId(
						publishedFragmentEntryLink.getUuid(),
						liveGroup.getGroupId()),
				liveGroup, liveLayout, locale),
			"<h1> Drop Zone 1 </h1>", dropZoneId1 + "HeadingContent",
			"<h1> Added Drop Zone </h1>", "<h1> Drop Zone 2 </h1>",
			dropZoneId2 + "HeadingContent");
	}

	@Override
	protected StagedModel addStagedModel(
			Group group,
			Map<String, List<StagedModel>> dependentStagedModelsMap)
		throws Exception {

		return _addFragmentEntry(
			_read("configuration-valid-all-types.json"), group,
			RandomTestUtil.randomString());
	}

	@Override
	protected StagedModel getStagedModel(String uuid, Group group)
		throws PortalException {

		return _fragmentEntryLocalService.getFragmentEntryByUuidAndGroupId(
			uuid, group.getGroupId());
	}

	@Override
	protected Class<? extends StagedModel> getStagedModelClass() {
		return FragmentEntry.class;
	}

	@Override
	protected void validateImportedStagedModel(
			StagedModel stagedModel, StagedModel importedStagedModel)
		throws Exception {

		FragmentEntry fragmentEntry = (FragmentEntry)stagedModel;
		FragmentEntry importedFragmentEntry =
			(FragmentEntry)importedStagedModel;

		Assert.assertEquals(
			importedFragmentEntry.getCss(), fragmentEntry.getCss());
		Assert.assertEquals(
			importedFragmentEntry.getHtml(), fragmentEntry.getHtml());
		Assert.assertEquals(
			importedFragmentEntry.getJs(), fragmentEntry.getJs());
		Assert.assertEquals(
			importedFragmentEntry.getConfiguration(),
			fragmentEntry.getConfiguration());
	}

	private FragmentEntry _addFragmentEntry(
			String configuration, Group group, String html)
		throws Exception {

		FragmentCollection fragmentCollection =
			FragmentTestUtil.addFragmentCollection(group.getGroupId());

		return _fragmentEntryLocalService.addFragmentEntry(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), html, RandomTestUtil.randomString(),
			false, configuration, null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId()));
	}

	private FragmentStyledLayoutStructureItem _addHeadingFragmentEntryLinks(
			String itemId, Layout layout, Locale locale,
			long segmentsExperienceId, ServiceContext serviceContext,
			String... contents)
		throws Exception {

		LayoutStructure layoutStructure =
			_layoutStructureProvider.getLayoutStructure(
				layout.getPlid(), segmentsExperienceId);

		FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem =
			(FragmentStyledLayoutStructureItem)
				layoutStructure.getLayoutStructureItem(itemId);

		List<String> childrenItemIds =
			fragmentStyledLayoutStructureItem.getChildrenItemIds();

		Assert.assertEquals(
			childrenItemIds.toString(), contents.length,
			childrenItemIds.size());

		FragmentEntry fragmentEntry =
			_fragmentCollectionContributorRegistry.getFragmentEntry(
				"BASIC_COMPONENT-heading");

		for (int i = 0; i < childrenItemIds.size(); i++) {
			ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
				_fragmentEntryLinkLocalService.addFragmentEntryLink(
					null, TestPropsValues.getUserId(),
					stagingGroup.getGroupId(), 0,
					fragmentEntry.getFragmentEntryId(), segmentsExperienceId,
					layout.getPlid(), fragmentEntry.getCss(),
					fragmentEntry.getHtml(), fragmentEntry.getJs(),
					fragmentEntry.getConfiguration(),
					JSONUtil.put(
						FragmentEntryProcessorConstants.
							KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
						JSONUtil.put(
							"element-text",
							JSONUtil.put(
								LocaleUtil.toLanguageId(locale), contents[i]))
					).toString(),
					StringPool.BLANK, 0, StringPool.BLANK,
					fragmentEntry.getType(), serviceContext),
				layout, childrenItemIds.get(i), 0, segmentsExperienceId);
		}

		return fragmentStyledLayoutStructureItem;
	}

	private void _assertHTML(String html, String... strings) throws Exception {
		String content = html;

		for (String string : strings) {
			Assert.assertTrue(
				html + " not contains " + string, content.contains(string));

			content = content.substring(content.indexOf(string));
		}
	}

	private void _exportImportStagedModel(StagedModel... stagedModels)
		throws Exception {

		ExportImportThreadLocal.setPortletImportInProcess(true);

		try {
			for (StagedModel stagedModel : stagedModels) {
				exportImportStagedModel(stagedModel);
			}
		}
		finally {
			ExportImportThreadLocal.setPortletImportInProcess(false);
		}
	}

	private String _getFragmentEntryLinkRenderHTML(
			Company company, FragmentEntryLink fragmentEntryLink, Group group,
			Layout layout, Locale locale)
		throws Exception {

		DefaultFragmentRendererContext defaultFragmentRendererContext =
			new DefaultFragmentRendererContext(fragmentEntryLink);

		defaultFragmentRendererContext.setLocale(locale);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_fragmentRenderer.render(
			defaultFragmentRendererContext,
			_getMockHttpServletRequest(company, group, layout),
			mockHttpServletResponse);

		return mockHttpServletResponse.getContentAsString();
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
			Company company, Group group, Layout layout)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(WebKeys.LAYOUT, layout);

		ThemeDisplay themeDisplay = ContentLayoutTestUtil.getThemeDisplay(
			company, group, layout);

		themeDisplay.setRequest(mockHttpServletRequest);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockHttpServletRequest;
	}

	private String _read(String fileName) throws Exception {
		return new String(
			FileUtil.getBytes(getClass(), "dependencies/" + fileName));
	}

	private FragmentEntry _updateFragmentEntryWithPropagation(
			FragmentEntry fragmentEntry, String html)
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						FragmentServiceConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"propagateChanges", true
						).build())) {

			return _fragmentEntryLocalService.updateFragmentEntry(
				TestPropsValues.getUserId(), fragmentEntry.getFragmentEntryId(),
				fragmentEntry.getFragmentCollectionId(),
				fragmentEntry.getName(), fragmentEntry.getCss(), html,
				fragmentEntry.getJs(), false, fragmentEntry.getConfiguration(),
				StringPool.BLANK, fragmentEntry.getPreviewFileEntryId(), false,
				fragmentEntry.getTypeOptions(),
				WorkflowConstants.STATUS_APPROVED);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.fragment.internal.renderer.FragmentEntryFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutStructureProvider _layoutStructureProvider;

	@Inject
	private Portal _portal;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}