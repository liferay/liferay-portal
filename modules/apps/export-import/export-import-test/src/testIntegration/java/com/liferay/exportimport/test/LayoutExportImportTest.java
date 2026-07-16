/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.test.util.DLAppTestUtil;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationFactory;
import com.liferay.exportimport.kernel.exception.LARTypeException;
import com.liferay.exportimport.kernel.lar.ExportImportHelperUtil;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.exportimport.test.util.lar.BaseExportImportTestCase;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.FragmentRendererRegistry;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.friendly.url.constants.FriendlyURLEntryConstants;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.friendly.url.LayoutFriendlyURLEntryHelper;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.LayoutServiceContextHelper;
import com.liferay.layout.utility.page.kernel.constants.LayoutUtilityPageEntryConstants;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryLocalService;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.LayoutFriendlyURLsException;
import com.liferay.portal.kernel.exception.LocaleException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eduardo García
 */
@RunWith(Arquillian.class)
public class LayoutExportImportTest extends BaseExportImportTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		UserTestUtil.setUser(TestPropsValues.getUser());
	}

	@Test
	public void testDeleteMissingLayouts() throws Exception {
		Layout layout1 = LayoutTestUtil.addTypePortletLayout(group);
		Layout layout2 = LayoutTestUtil.addTypePortletLayout(group);

		long[] layoutIds = ExportImportHelperUtil.getLayoutIds(
			_layoutLocalService.getLayouts(group.getGroupId(), false));

		exportImportLayouts(layoutIds, getImportParameterMap());

		Assert.assertEquals(
			_layoutLocalService.getLayoutsCount(group, false),
			_layoutLocalService.getLayoutsCount(importedGroup, false));

		LayoutTestUtil.addTypePortletLayout(importedGroup);

		Map<String, String[]> parameterMap = getImportParameterMap();

		parameterMap.put(
			PortletDataHandlerKeys.DELETE_MISSING_LAYOUTS,
			new String[] {Boolean.TRUE.toString()});

		layoutIds = new long[] {layout1.getLayoutId()};

		exportImportLayouts(layoutIds, parameterMap);

		Assert.assertEquals(
			_layoutLocalService.getLayoutsCount(group, false),
			_layoutLocalService.getLayoutsCount(importedGroup, false));

		Layout importedLayout1 =
			_layoutLocalService.fetchLayoutByUuidAndGroupId(
				layout1.getUuid(), importedGroup.getGroupId(), false);

		Assert.assertNotNull(importedLayout1);

		Layout importedLayout2 =
			_layoutLocalService.fetchLayoutByUuidAndGroupId(
				layout2.getUuid(), importedGroup.getGroupId(), false);

		Assert.assertNotNull(importedLayout2);
	}

	@Test
	public void testDeleteMissingLayoutsSameGroupWithPromoteContentFeatureFlags()
		throws Exception {

		Group originalImportedGroup = importedGroup;

		try {
			Layout layoutA = LayoutTestUtil.addTypePortletLayout(group);
			Layout layoutB = LayoutTestUtil.addTypePortletLayout(group);

			long[] layoutIds = {layoutA.getLayoutId(), layoutB.getLayoutId()};

			exportLayouts(layoutIds, getExportParameterMap());

			Layout layoutC = LayoutTestUtil.addTypePortletLayout(group);

			importedGroup = group;

			Map<String, String[]> parameterMap = getImportParameterMap();

			parameterMap.put(
				PortletDataHandlerKeys.DELETE_MISSING_LAYOUTS,
				new String[] {Boolean.TRUE.toString()});

			importLayouts(parameterMap);

			Layout fetchedLayoutA =
				_layoutLocalService.fetchLayoutByUuidAndGroupId(
					layoutA.getUuid(), group.getGroupId(), false);

			Assert.assertNotNull(fetchedLayoutA);

			Layout fetchedLayoutB =
				_layoutLocalService.fetchLayoutByUuidAndGroupId(
					layoutB.getUuid(), group.getGroupId(), false);

			Assert.assertNotNull(fetchedLayoutB);

			Layout fetchedLayoutCAfterImport =
				_layoutLocalService.fetchLayoutByUuidAndGroupId(
					layoutC.getUuid(), group.getGroupId(), false);

			Assert.assertNull(fetchedLayoutCAfterImport);
		}
		finally {
			importedGroup = originalImportedGroup;
		}
	}

	@Test
	public void testExportImportCompanyGroupInvalidLARType() throws Exception {

		// Import a layout set to a company layout set

		Group originalImportedGroup = importedGroup;
		Group originalGroup = group;

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		importedGroup = company.getGroup();

		long[] layoutIds = new long[0];

		try {
			exportImportLayouts(layoutIds, getImportParameterMap(), true);

			Assert.fail();
		}
		catch (LARTypeException larTypeException) {
			if (_log.isDebugEnabled()) {
				_log.debug(larTypeException);
			}
		}
		finally {
			importedGroup = originalImportedGroup;
		}

		// Import a company layout set to a layout set

		group = company.getGroup();
		importedGroup = originalGroup;

		try {
			exportImportLayouts(layoutIds, getImportParameterMap(), true);

			Assert.fail();
		}
		catch (LARTypeException larTypeException) {
			if (_log.isDebugEnabled()) {
				_log.debug(larTypeException);
			}
		}
		finally {
			importedGroup = originalImportedGroup;
			group = originalGroup;
		}
	}

	@Test
	@TestInfo({"LPS-88378", "LPS-128533", "LRQA-77950"})
	public void testExportImportContentLayoutCircularLinkReference()
		throws Exception {

		Layout contentLayoutA = LayoutTestUtil.addTypeContentLayout(
			group, "Test Circular Link Reference A");
		Layout contentLayoutB = LayoutTestUtil.addTypeContentLayout(
			group, "Test Circular Link Reference B");

		_addLayoutLinkFragmentEntryLink(contentLayoutA, contentLayoutB);
		_addLayoutLinkFragmentEntryLink(contentLayoutB, contentLayoutA);

		exportImportLayouts(
			new long[] {
				contentLayoutA.getLayoutId(), contentLayoutB.getLayoutId()
			},
			getImportParameterMap());

		Layout importedContentLayoutA =
			_layoutLocalService.fetchLayoutByExternalReferenceCode(
				contentLayoutA.getExternalReferenceCode(),
				importedGroup.getGroupId());

		Assert.assertNotNull(importedContentLayoutA);

		Layout importedContentLayoutB =
			_layoutLocalService.fetchLayoutByExternalReferenceCode(
				contentLayoutB.getExternalReferenceCode(),
				importedGroup.getGroupId());

		Assert.assertNotNull(importedContentLayoutB);

		_assertLayoutLinkFragmentEntryLink(
			importedContentLayoutA, importedContentLayoutB);
		_assertLayoutLinkFragmentEntryLink(
			importedContentLayoutB, importedContentLayoutA);
	}

	@Test
	@TestInfo("LPS-169440")
	public void testExportImportContentLayoutWithBrokenImageReference()
		throws Exception {

		Layout contentLayout = LayoutTestUtil.addTypeContentLayout(
			group, "Test Broken Image Reference");

		FileEntry fileEntry = DLAppTestUtil.addFileEntry(group.getGroupId());

		_addMappedImageFragmentEntryLink(contentLayout, fileEntry);

		_dlAppLocalService.deleteFileEntry(fileEntry.getFileEntryId());

		exportImportLayouts(
			new long[] {contentLayout.getLayoutId()}, getImportParameterMap());

		Layout importedLayout =
			_layoutLocalService.fetchLayoutByExternalReferenceCode(
				contentLayout.getExternalReferenceCode(),
				importedGroup.getGroupId());

		Assert.assertNotNull(importedLayout);
	}

	@Test
	public void testExportImportLayoutFromMasterLayoutPageTemplateAndDraftLayoutMappingOnImportSide()
		throws Exception {

		// This line is needed to reproduce LPD-18967

		LayoutTestUtil.addTypePortletLayout(group, true);

		LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), group.getGroupId(), 0, null,
				"Test Master Page",
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, 0,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		Layout masterPageTemplateLayout = _layoutLocalService.getLayout(
			masterLayoutPageTemplateEntry.getPlid());

		Layout masterPageTemplateDraftLayout =
			masterPageTemplateLayout.fetchDraftLayout();

		Layout contentLayout = LayoutTestUtil.addTypeContentLayout(
			group, "Test Page From Master Layout Page Template");

		_fragmentEntryLinkLocalService.addFragmentEntryLink(
			null, TestPropsValues.getUserId(), group.getGroupId(), null,
			RandomTestUtil.randomString(), null,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				masterPageTemplateDraftLayout.getPlid()),
			masterPageTemplateDraftLayout.getPlid(), StringPool.BLANK,
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			StringUtil.replace(
				_getContent(
					"fragment_entry_link_editable_values_with_configuration." +
						"json"),
				new String[] {
					"$GROUP_ID", "$LAYOUT_ID", "$LAYOUT_UUID", "$TITLE"
				},
				new String[] {
					String.valueOf(group.getGroupId()),
					String.valueOf(contentLayout.getLayoutId()),
					contentLayout.getUuid(), contentLayout.getName("en_US")
				}),
			StringPool.BLANK, 0, StringPool.BLANK,
			FragmentConstants.TYPE_COMPONENT,
			ServiceContextTestUtil.getServiceContext());

		exportImportLayouts(
			new long[] {contentLayout.getLayoutId()}, getImportParameterMap());

		Layout importedLayout =
			_layoutLocalService.fetchLayoutByExternalReferenceCode(
				contentLayout.getExternalReferenceCode(),
				importedGroup.getGroupId());

		Layout importedDraftLayout = importedLayout.fetchDraftLayout();

		Assert.assertTrue(importedDraftLayout.isDraftLayout());
		Assert.assertEquals(
			importedLayout.getName(), importedDraftLayout.getName());

		Layout importedMasterPageTemplateLayout =
			_layoutLocalService.fetchLayoutByExternalReferenceCode(
				masterPageTemplateLayout.getExternalReferenceCode(),
				importedGroup.getGroupId());

		Layout importedDraftLayoutOfMasterPageTemplate =
			importedMasterPageTemplateLayout.fetchDraftLayout();

		Assert.assertTrue(
			importedDraftLayoutOfMasterPageTemplate.isDraftLayout());
		Assert.assertEquals(
			importedMasterPageTemplateLayout.getName(),
			importedDraftLayoutOfMasterPageTemplate.getName());
	}

	@Test
	public void testExportImportLayoutPrototypeInvalidLARType()
		throws Exception {

		// Import a layout prototype to a layout set

		LayoutPrototype layoutPrototype = LayoutTestUtil.addLayoutPrototype(
			RandomTestUtil.randomString());

		group = layoutPrototype.getGroup();

		importedGroup = GroupTestUtil.addGroup();

		long[] layoutIds = new long[0];

		try {
			exportImportLayouts(layoutIds, getImportParameterMap(), true);

			Assert.fail();
		}
		catch (LARTypeException larTypeException) {
			if (_log.isDebugEnabled()) {
				_log.debug(larTypeException);
			}
		}

		// Import a layout prototype to a layout set pototype

		LayoutSetPrototype layoutSetPrototype =
			LayoutTestUtil.addLayoutSetPrototype(RandomTestUtil.randomString());

		importedGroup = layoutSetPrototype.getGroup();

		try {
			exportImportLayouts(layoutIds, getImportParameterMap(), true);

			Assert.fail();
		}
		catch (LARTypeException larTypeException) {
			if (_log.isDebugEnabled()) {
				_log.debug(larTypeException);
			}
		}
		finally {
			_layoutSetPrototypeLocalService.deleteLayoutSetPrototype(
				layoutSetPrototype);

			importedGroup = null;
		}
	}

	@Test
	public void testExportImportLayouts() throws Exception {
		LayoutTestUtil.addTypePortletLayout(group);

		exportImportLayouts(
			ExportImportHelperUtil.getLayoutIds(
				_layoutLocalService.getLayouts(group.getGroupId(), false)),
			getImportParameterMap());

		Assert.assertEquals(
			_layoutLocalService.getLayoutsCount(group, false),
			_layoutLocalService.getLayoutsCount(importedGroup, false));
	}

	@Test
	public void testExportImportLayoutSetInvalidLARType() throws Exception {

		// Import a layout set to a layout prototype

		LayoutPrototype layoutPrototype = LayoutTestUtil.addLayoutPrototype(
			RandomTestUtil.randomString());

		importedGroup = layoutPrototype.getGroup();

		long[] layoutIds = new long[0];

		try {
			exportImportLayouts(layoutIds, getImportParameterMap(), true);

			Assert.fail();
		}
		catch (LARTypeException larTypeException) {
			if (_log.isDebugEnabled()) {
				_log.debug(larTypeException);
			}
		}

		// Import a layout set to a layout set prototype

		LayoutSetPrototype layoutSetPrototype =
			LayoutTestUtil.addLayoutSetPrototype(RandomTestUtil.randomString());

		importedGroup = layoutSetPrototype.getGroup();

		try {
			exportImportLayouts(layoutIds, getImportParameterMap(), true);

			Assert.fail();
		}
		catch (LARTypeException larTypeException) {
			if (_log.isDebugEnabled()) {
				_log.debug(larTypeException);
			}
		}
		finally {
			_layoutSetPrototypeLocalService.deleteLayoutSetPrototype(
				layoutSetPrototype);

			importedGroup = null;
		}
	}

	@Test
	public void testExportImportLayoutSetPrototypeInvalidLARType()
		throws Exception {

		// Import a layout set prototype to a layout set

		LayoutSetPrototype layoutSetPrototype =
			LayoutTestUtil.addLayoutSetPrototype(RandomTestUtil.randomString());

		try {
			group = layoutSetPrototype.getGroup();
			importedGroup = GroupTestUtil.addGroup();

			long[] layoutIds = new long[0];

			try {
				exportImportLayouts(layoutIds, getImportParameterMap(), true);

				Assert.fail();
			}
			catch (LARTypeException larTypeException) {
				if (_log.isDebugEnabled()) {
					_log.debug(larTypeException);
				}
			}

			// Import a layout set prototype to a layout prototyope

			LayoutPrototype layoutPrototype = LayoutTestUtil.addLayoutPrototype(
				RandomTestUtil.randomString());

			importedGroup = layoutPrototype.getGroup();

			try {
				exportImportLayouts(layoutIds, getImportParameterMap(), true);

				Assert.fail();
			}
			catch (LARTypeException larTypeException) {
				if (_log.isDebugEnabled()) {
					_log.debug(larTypeException);
				}
			}
		}
		finally {
			_layoutSetPrototypeLocalService.deleteLayoutSetPrototype(
				layoutSetPrototype);

			group = null;
		}
	}

	@Test
	public void testExportImportLayoutsInvalidAvailableLocales()
		throws Exception {

		testAvailableLocales(
			Arrays.asList(LocaleUtil.US, LocaleUtil.SPAIN),
			Arrays.asList(LocaleUtil.US, LocaleUtil.GERMANY), true);
	}

	@Test
	public void testExportImportLayoutsPriorities() throws Exception {
		Layout layout1 = LayoutTestUtil.addTypePortletLayout(group);
		Layout layout2 = LayoutTestUtil.addTypePortletLayout(group);
		Layout layout3 = LayoutTestUtil.addTypePortletLayout(group);

		int priority = layout1.getPriority();

		layout1.setPriority(layout3.getPriority());

		layout3.setPriority(priority);

		layout1 = _layoutLocalService.updateLayout(layout1);
		layout3 = _layoutLocalService.updateLayout(layout3);

		long[] layoutIds = {layout1.getLayoutId(), layout2.getLayoutId()};

		exportImportLayouts(layoutIds, getImportParameterMap());

		Layout importedLayout1 =
			_layoutLocalService.fetchLayoutByUuidAndGroupId(
				layout1.getUuid(), importedGroup.getGroupId(), false);

		Assert.assertNotEquals(
			layout1.getPriority(), importedLayout1.getPriority());

		Layout importedLayout2 =
			_layoutLocalService.fetchLayoutByUuidAndGroupId(
				layout2.getUuid(), importedGroup.getGroupId(), false);

		Assert.assertNotEquals(
			layout2.getPriority(), importedLayout2.getPriority());

		exportImportLayouts(
			ExportImportHelperUtil.getLayoutIds(
				_layoutLocalService.getLayouts(group.getGroupId(), false)),
			getImportParameterMap());

		importedLayout1 = _layoutLocalService.fetchLayoutByUuidAndGroupId(
			layout1.getUuid(), importedGroup.getGroupId(), false);

		Assert.assertEquals(
			layout1.getPriority(), importedLayout1.getPriority());

		importedLayout2 = _layoutLocalService.fetchLayoutByUuidAndGroupId(
			layout2.getUuid(), importedGroup.getGroupId(), false);

		Assert.assertEquals(
			layout2.getPriority(), importedLayout2.getPriority());

		Layout importedLayout3 =
			_layoutLocalService.fetchLayoutByUuidAndGroupId(
				layout3.getUuid(), importedGroup.getGroupId(), false);

		Assert.assertEquals(
			layout3.getPriority(), importedLayout3.getPriority());
	}

	@Test
	public void testExportImportLayoutsValidAvailableLocales()
		throws Exception {

		testAvailableLocales(
			Arrays.asList(LocaleUtil.US, LocaleUtil.US),
			Arrays.asList(LocaleUtil.US, LocaleUtil.SPAIN, LocaleUtil.US),
			false);
	}

	@Test
	@TestInfo("LPD-96103")
	public void testExportImportLayoutsWithStagingDepotMappedContent()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		GroupTestUtil.enableLocalStaging(group);

		Group stagingGroup = group.getStagingGroup();

		DepotEntry liveDepotEntry = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		Group liveDepotGroup = liveDepotEntry.getGroup();

		GroupTestUtil.enableLocalStaging(liveDepotGroup);

		Group stagingDepotGroup = liveDepotGroup.getStagingGroup();

		DepotEntry stagingDepotEntry =
			_depotEntryLocalService.getGroupDepotEntry(
				stagingDepotGroup.getGroupId());

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			stagingDepotEntry.getDepotEntryId(), stagingGroup.getGroupId());

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			stagingDepotGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		String defaultLanguageId = journalArticle.getDefaultLanguageId();

		String title = journalArticle.getTitle(defaultLanguageId);

		StagingUtil.publishLayouts(
			TestPropsValues.getUserId(),
			ExportImportConfigurationFactory.
				buildDefaultLocalPublishingExportImportConfiguration(
					TestPropsValues.getUser(), stagingDepotGroup.getGroupId(),
					liveDepotGroup.getGroupId(), false));

		JournalArticle liveJournalArticle =
			_journalArticleLocalService.getJournalArticleByUuidAndGroupId(
				journalArticle.getUuid(), liveDepotGroup.getGroupId());

		Assert.assertEquals(
			title, liveJournalArticle.getTitle(defaultLanguageId));

		Layout layout = LayoutTestUtil.addTypeContentLayout(stagingGroup);

		Layout draftLayout = layout.fetchDraftLayout();

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					"itemSelector",
					JSONUtil.put(
						"className", JournalArticle.class.getName()
					).put(
						"classNameId",
						_portal.getClassNameId(JournalArticle.class)
					).put(
						"classPK", journalArticle.getResourcePrimKey()
					).put(
						"classTypeId", journalArticle.getDDMStructureId()
					).put(
						"externalReferenceCode",
						journalArticle.getExternalReferenceCode()
					).put(
						"scopeExternalReferenceCode",
						stagingDepotGroup.getExternalReferenceCode()
					).put(
						"template",
						HashMapBuilder.put(
							"infoItemRendererKey",
							"com.liferay.journal.web.internal.info.item." +
								"renderer.JournalArticleTitleInfoItemRenderer"
						).build()
					))
			).toString(),
			_fragmentRendererRegistry.getFragmentRenderer(
				"com.liferay.fragment.internal.renderer." +
					"ContentObjectFragmentRenderer"),
			draftLayout, null, 0,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid()));

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		StagingUtil.publishLayouts(
			TestPropsValues.getUserId(),
			ExportImportConfigurationFactory.
				buildDefaultLocalPublishingExportImportConfiguration(
					TestPropsValues.getUser(), stagingGroup.getGroupId(),
					group.getGroupId(), false));

		Layout liveLayout =
			_layoutLocalService.getLayoutByExternalReferenceCode(
				layout.getExternalReferenceCode(), group.getGroupId());

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				liveLayout.getPlid());

		String html = ContentLayoutTestUtil.getRenderLayoutHTML(
			liveLayout, _layoutServiceContextHelper, _layoutStructureProvider,
			segmentsExperienceId);

		Assert.assertTrue(
			html + " does not contain " + title, html.contains(title));

		String updatedTitle = RandomTestUtil.randomString();

		journalArticle = JournalTestUtil.updateArticle(
			journalArticle, updatedTitle);

		Assert.assertEquals(
			updatedTitle, journalArticle.getTitle(defaultLanguageId));

		html = ContentLayoutTestUtil.getRenderLayoutHTML(
			liveLayout, _layoutServiceContextHelper, _layoutStructureProvider,
			segmentsExperienceId);

		Assert.assertFalse(
			html + " contains " + updatedTitle, html.contains(updatedTitle));
		Assert.assertTrue(
			html + " does not contain " + title, html.contains(title));
	}

	@Test
	@TestInfo("LPD-90359")
	public void testExportImportLayoutThemeSettings() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(
			group.getGroupId(),
			UnicodePropertiesBuilder.put(
				"lfr-theme:regular:show-header-search", Boolean.FALSE.toString()
			).put(
				"lfr-theme:regular:show-maximize-minimize-application-links",
				Boolean.TRUE.toString()
			).buildString());

		exportImportLayouts(
			new long[] {layout.getLayoutId()}, getImportParameterMap());

		Layout importedLayout = _layoutLocalService.getLayoutByUuidAndGroupId(
			layout.getUuid(), importedGroup.getGroupId(), false);

		Assert.assertEquals(
			Boolean.FALSE.toString(),
			importedLayout.getTypeSettingsProperty(
				"lfr-theme:regular:show-header-search"));
		Assert.assertEquals(
			Boolean.TRUE.toString(),
			importedLayout.getTypeSettingsProperty(
				"lfr-theme:regular:show-maximize-minimize-application-links"));
	}

	@Test
	@TestInfo("LPD-77689")
	public void testExportImportLayoutUtilityPageEntryWithPreviewFileEntryWithBatch()
		throws Exception {

		_testExportImportLayoutUtilityPageEntryWithPreviewFileEntry();
	}

	@Test
	@TestInfo("LPD-90359")
	public void testExportImportPrivateLayouts() throws Exception {
		Layout privateLayout = LayoutTestUtil.addTypePortletLayout(group, true);

		exportLayouts(
			new long[] {privateLayout.getLayoutId()}, getExportParameterMap(),
			false, true);

		importLayouts(getImportParameterMap(), false, true);

		Assert.assertNotNull(
			_layoutLocalService.fetchLayoutByUuidAndGroupId(
				privateLayout.getUuid(), importedGroup.getGroupId(), true));
		Assert.assertEquals(
			_layoutLocalService.getLayoutsCount(group, true),
			_layoutLocalService.getLayoutsCount(importedGroup, true));
	}

	@Test
	public void testExportImportSelectedLayouts() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(group);

		long[] layoutIds = {layout.getLayoutId()};

		exportImportLayouts(layoutIds, getImportParameterMap());

		Assert.assertEquals(
			layoutIds.length,
			_layoutLocalService.getLayoutsCount(importedGroup, false));

		importedLayout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
			layout.getUuid(), importedGroup.getGroupId(), false);

		Assert.assertNotNull(importedLayout);
	}

	@Test
	public void testExportImportUnselectedChildLayouts() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(group);

		Layout childLayout = LayoutTestUtil.addTypePortletLayout(
			group, layout.getPlid());

		Map<Long, Boolean> selectedLayouts = HashMapBuilder.put(
			LayoutConstants.DEFAULT_PLID, true
		).put(
			layout.getPlid(), false
		).build();

		List<Layout> layouts = _layoutLocalService.getLayouts(
			group.getGroupId(), false);

		Map<String, String[]> exportParameterMap = getExportParameterMap();

		exportParameterMap.put(Constants.CMD, new String[] {Constants.EXPORT});

		exportLayouts(
			ExportImportHelperUtil.getLayoutIds(selectedLayouts),
			exportParameterMap);

		importLayouts(getImportParameterMap());

		Assert.assertNotEquals(
			layouts.size(),
			_layoutLocalService.getLayoutsCount(importedGroup, false));

		importedLayout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
			childLayout.getUuid(), importedGroup.getGroupId(), false);

		Assert.assertNull(importedLayout);
	}

	@Test
	public void testFriendlyURLCollision() throws Exception {
		String defaultLanguageId = LocaleUtil.toLanguageId(
			LocaleUtil.getDefault());

		Layout layoutA = LayoutTestUtil.addTypePortletLayout(group);

		String friendlyURLA = layoutA.getFriendlyURL();

		layoutA = _layoutLocalService.updateFriendlyURL(
			layoutA.getUserId(), layoutA.getPlid(), friendlyURLA + "-de", "de");

		Layout layoutB = LayoutTestUtil.addTypePortletLayout(group);

		String friendlyURLB = layoutB.getFriendlyURL();

		layoutB = _layoutLocalService.updateFriendlyURL(
			layoutB.getUserId(), layoutB.getPlid(), friendlyURLB + "-de", "de");

		long[] layoutIds = {layoutA.getLayoutId(), layoutB.getLayoutId()};

		exportImportLayouts(layoutIds, getImportParameterMap());

		layoutA = _layoutLocalService.updateFriendlyURL(
			layoutA.getUserId(), layoutA.getPlid(), "/temp", defaultLanguageId);

		layoutA = _layoutLocalService.updateFriendlyURL(
			layoutA.getUserId(), layoutA.getPlid(), "/temp-de", "de");

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.fetchFriendlyURLEntry(
				group.getGroupId(),
				_layoutFriendlyURLEntryHelper.getClassNameId(
					layoutA.isPrivateLayout()),
				FriendlyURLEntryConstants.
					FRIENDLY_URL_ENTRY_PARENT_CLASS_PK_DEFAULT,
				friendlyURLA);

		_friendlyURLEntryLocalService.deleteFriendlyURLEntry(
			friendlyURLEntry.getFriendlyURLEntryId());

		layoutB = _layoutLocalService.updateFriendlyURL(
			layoutB.getUserId(), layoutB.getPlid(), friendlyURLA,
			defaultLanguageId);

		_layoutLocalService.updateFriendlyURL(
			layoutB.getUserId(), layoutB.getPlid(), friendlyURLA + "-de", "de");

		friendlyURLEntry = _friendlyURLEntryLocalService.fetchFriendlyURLEntry(
			group.getGroupId(),
			_layoutFriendlyURLEntryHelper.getClassNameId(
				layoutB.isPrivateLayout()),
			FriendlyURLEntryConstants.
				FRIENDLY_URL_ENTRY_PARENT_CLASS_PK_DEFAULT,
			friendlyURLB);

		_friendlyURLEntryLocalService.deleteFriendlyURLEntry(
			friendlyURLEntry.getFriendlyURLEntryId());

		layoutA = _layoutLocalService.updateFriendlyURL(
			layoutA.getUserId(), layoutA.getPlid(), friendlyURLB,
			defaultLanguageId);

		_layoutLocalService.updateFriendlyURL(
			layoutA.getUserId(), layoutA.getPlid(), friendlyURLB + "-de", "de");

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.batch.engine.internal." +
					"BatchEngineImportTaskExecutorImpl",
				LoggerTestUtil.ERROR)) {

			exportImportLayouts(layoutIds, getImportParameterMap());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(
				logEntries.toString(), layoutIds.length, logEntries.size());

			for (LogEntry logEntry : logEntries) {
				Throwable throwable = logEntry.getThrowable();

				Assert.assertTrue(
					String.valueOf(throwable),
					throwable instanceof LayoutFriendlyURLsException);
			}
		}

		_assertFriendlyURL(layoutA, friendlyURLA);
		_assertFriendlyURL(layoutB, friendlyURLB);
	}

	@FeatureFlag("LPD-34594")
	@Test
	public void testPromotedPageWithSamePriorityTakesPrecedence()
		throws Exception {

		Layout layout1 = LayoutTestUtil.addTypePortletLayout(group);
		Layout layout2 = LayoutTestUtil.addTypePortletLayout(group);
		Layout layout3 = LayoutTestUtil.addTypePortletLayout(group);

		exportImportLayouts(
			ExportImportHelperUtil.getLayoutIds(
				_layoutLocalService.getLayouts(group.getGroupId(), false)),
			getImportParameterMap());

		Layout importedLayout1 =
			_layoutLocalService.fetchLayoutByUuidAndGroupId(
				layout1.getUuid(), importedGroup.getGroupId(), false);

		Layout importedLayout3 =
			_layoutLocalService.fetchLayoutByUuidAndGroupId(
				layout3.getUuid(), importedGroup.getGroupId(), false);

		importedLayout3.setPriority(1);

		_layoutLocalService.updateLayout(importedLayout3);

		importedLayout1.setPriority(2);

		_layoutLocalService.updateLayout(importedLayout1);

		Layout importedLayout2 =
			_layoutLocalService.fetchLayoutByUuidAndGroupId(
				layout2.getUuid(), importedGroup.getGroupId(), false);

		importedLayout2.setPriority(3);

		_layoutLocalService.updateLayout(importedLayout2);

		exportImportLayouts(
			new long[] {layout1.getLayoutId()}, getImportParameterMap());

		importedLayout1 = _layoutLocalService.fetchLayoutByUuidAndGroupId(
			layout1.getUuid(), importedGroup.getGroupId(), false);
		importedLayout3 = _layoutLocalService.fetchLayoutByUuidAndGroupId(
			layout3.getUuid(), importedGroup.getGroupId(), false);

		Assert.assertTrue(
			importedLayout1.getPriority() < importedLayout3.getPriority());

		importedLayout2 = _layoutLocalService.fetchLayoutByUuidAndGroupId(
			layout2.getUuid(), importedGroup.getGroupId(), false);

		Assert.assertTrue(
			importedLayout2.getPriority() > importedLayout3.getPriority());
	}

	protected void testAvailableLocales(
			Collection<Locale> sourceAvailableLocales,
			Collection<Locale> targetAvailableLocales, boolean expectFailure)
		throws Exception {

		group = GroupTestUtil.updateDisplaySettings(
			group.getGroupId(), sourceAvailableLocales, null);
		importedGroup = GroupTestUtil.updateDisplaySettings(
			importedGroup.getGroupId(), targetAvailableLocales, null);

		LayoutTestUtil.addTypePortletLayout(group);

		long[] layoutIds = new long[0];

		try {
			exportImportLayouts(layoutIds, getImportParameterMap(), true);

			Assert.assertFalse(expectFailure);
		}
		catch (LocaleException localeException) {
			if (_log.isDebugEnabled()) {
				_log.debug(localeException);
			}

			Assert.assertTrue(expectFailure);
		}
	}

	private void _addLayoutLinkFragmentEntryLink(
			Layout layout, Layout linkedLayout)
		throws Exception {

		Layout draftLayout = layout.fetchDraftLayout();

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			StringUtil.replace(
				_getContent(
					"fragment_entry_link_editable_values_with_configuration." +
						"json"),
				new String[] {
					"$GROUP_ID", "$LAYOUT_ID", "$LAYOUT_UUID", "$TITLE"
				},
				new String[] {
					String.valueOf(group.getGroupId()),
					String.valueOf(linkedLayout.getLayoutId()),
					linkedLayout.getUuid(), linkedLayout.getName("en_US")
				}),
			draftLayout,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid()));

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);
	}

	private void _addMappedImageFragmentEntryLink(
			Layout layout, FileEntry fileEntry)
		throws Exception {

		Layout draftLayout = layout.fetchDraftLayout();

		_fragmentEntryLinkLocalService.addFragmentEntryLink(
			null, TestPropsValues.getUserId(), group.getGroupId(), null,
			RandomTestUtil.randomString(), null,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid()),
			draftLayout.getPlid(), StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, StringPool.BLANK,
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					"image-square",
					JSONUtil.put(
						"className", FileEntry.class.getName()
					).put(
						"classNameId",
						String.valueOf(
							_portal.getClassNameId(FileEntry.class.getName()))
					).put(
						"classPK", String.valueOf(fileEntry.getFileEntryId())
					).put(
						"classTypeId", "0"
					).put(
						"fieldId", "smallImage"
					))
			).toString(),
			StringPool.BLANK, 0, StringPool.BLANK,
			FragmentConstants.TYPE_COMPONENT,
			ServiceContextTestUtil.getServiceContext());
	}

	private void _assertFriendlyURL(Layout layout, String friendlyURL)
		throws Exception {

		Layout importedLayout = _layoutLocalService.getLayoutByUuidAndGroupId(
			layout.getUuid(), importedGroup.getGroupId(),
			layout.isPrivateLayout());

		Assert.assertEquals(
			friendlyURL,
			importedLayout.getFriendlyURL(LocaleUtil.getDefault()));

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.fetchFriendlyURLEntry(
				importedGroup.getGroupId(),
				_layoutFriendlyURLEntryHelper.getClassNameId(
					importedLayout.isPrivateLayout()),
				FriendlyURLEntryConstants.
					FRIENDLY_URL_ENTRY_PARENT_CLASS_PK_DEFAULT,
				friendlyURL);

		Assert.assertEquals(
			importedLayout.getPlid(), friendlyURLEntry.getClassPK());
	}

	private void _assertLayoutLinkFragmentEntryLink(
			Layout layout, Layout expectedLinkedLayout)
		throws Exception {

		Layout draftLayout = layout.fetchDraftLayout();

		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
				layout.getGroupId(), draftLayout.getPlid());

		if (fragmentEntryLinks.isEmpty()) {
			fragmentEntryLinks =
				_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
					layout.getGroupId(), layout.getPlid());
		}

		Assert.assertEquals(
			fragmentEntryLinks.toString(), 1, fragmentEntryLinks.size());

		FragmentEntryLink fragmentEntryLink = fragmentEntryLinks.get(0);

		JSONObject editableValuesJSONObject = JSONFactoryUtil.createJSONObject(
			fragmentEntryLink.getEditableValues());

		JSONObject editableProcessorJSONObject =
			editableValuesJSONObject.getJSONObject(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR);

		JSONObject linkJSONObject = editableProcessorJSONObject.getJSONObject(
			"link");

		JSONObject configJSONObject = linkJSONObject.getJSONObject("config");

		JSONObject layoutJSONObject = configJSONObject.getJSONObject("layout");

		Assert.assertEquals(
			editableValuesJSONObject.toString(),
			expectedLinkedLayout.getExternalReferenceCode(),
			layoutJSONObject.getString("externalReferenceCode"));
	}

	private String _getContent(String fileName) throws Exception {
		Class<?> clazz = getClass();

		InputStream inputStream = clazz.getResourceAsStream(
			"dependencies/" + fileName);

		Scanner scanner = new Scanner(inputStream);

		scanner.useDelimiter("\\Z");

		return scanner.next();
	}

	private void _testExportImportLayoutUtilityPageEntryWithPreviewFileEntry()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId());

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryLocalService.addLayoutUtilityPageEntry(
				null, TestPropsValues.getUserId(), group.getGroupId(), 0, 0,
				false, RandomTestUtil.randomString(),
				LayoutUtilityPageEntryConstants.TYPE_SC_NOT_FOUND, null,
				serviceContext);

		Repository repository = PortletFileRepositoryUtil.addPortletRepository(
			group.getGroupId(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		String randomString = StringUtil.randomString();

		FileEntry previewFileEntry =
			PortletFileRepositoryUtil.addPortletFileEntry(
				null, group.getGroupId(), TestPropsValues.getUserId(),
				LayoutPageTemplateEntry.class.getName(),
				layoutUtilityPageEntry.getLayoutUtilityPageEntryId(),
				RandomTestUtil.randomString(), repository.getDlFolderId(),
				new ByteArrayInputStream(randomString.getBytes()),
				RandomTestUtil.randomString(), ContentTypes.IMAGE_PNG, false);

		_layoutUtilityPageEntryLocalService.updateLayoutUtilityPageEntry(
			layoutUtilityPageEntry.getLayoutUtilityPageEntryId(),
			previewFileEntry.getFileEntryId(), serviceContext);

		exportImportLayouts(new long[0], getImportParameterMap(), true);

		LayoutUtilityPageEntry importedLayoutUtilityPageEntry =
			_layoutUtilityPageEntryLocalService.
				getLayoutUtilityPageEntryByExternalReferenceCode(
					layoutUtilityPageEntry.getExternalReferenceCode(),
					importedGroup.getGroupId());

		FileEntry importedPreviewFileEntry =
			PortletFileRepositoryUtil.getPortletFileEntry(
				importedLayoutUtilityPageEntry.getPreviewFileEntryId());

		Assert.assertEquals(
			StreamUtil.toString(previewFileEntry.getContentStream()),
			StreamUtil.toString(importedPreviewFileEntry.getContentStream()));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutExportImportTest.class);

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Inject
	private FragmentRendererRegistry _fragmentRendererRegistry;

	@Inject
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private LayoutFriendlyURLEntryHelper _layoutFriendlyURLEntryHelper;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private LayoutServiceContextHelper _layoutServiceContextHelper;

	@Inject
	private LayoutSetPrototypeLocalService _layoutSetPrototypeLocalService;

	@Inject
	private LayoutStructureProvider _layoutStructureProvider;

	@Inject
	private LayoutUtilityPageEntryLocalService
		_layoutUtilityPageEntryLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}