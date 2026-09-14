/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.staging.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationParameterMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.ExportImportHelperUtil;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalServiceUtil;
import com.liferay.exportimport.kernel.service.ExportImportLocalServiceUtil;
import com.liferay.exportimport.kernel.service.StagingLocalService;
import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.exportimport.test.util.ExportImportTestUtil;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.page.template.test.util.LayoutPageTemplateTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.User;
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
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.site.navigation.constants.SiteNavigationConstants;
import com.liferay.site.navigation.menu.item.layout.constants.SiteNavigationMenuItemTypeConstants;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalService;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalService;

import java.io.File;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jaime León Rosado
 */
@RunWith(Arquillian.class)
public class StagedSiteLARImportTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext());

		UserTestUtil.setUser(TestPropsValues.getUser());

		_sourceGroup = GroupTestUtil.addGroup();

		_sourceLayout = LayoutTestUtil.addTypePortletLayout(_sourceGroup);

		_stagedGroup = GroupTestUtil.addGroup();

		_stagingLocalService.enableLocalStaging(
			TestPropsValues.getUserId(), _stagedGroup, false, false,
			ServiceContextTestUtil.getServiceContext(
				_stagedGroup.getGroupId(), TestPropsValues.getUserId()));
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	@TestInfo("LPD-100541")
	public void testImportLARIntoStagingGroupAndPublish() throws Exception {
		Group stagingGroup = _stagedGroup.getStagingGroup();

		ExportImportTestUtil.assertBackgroundTaskSuccessful(
			ExportImportTestUtil.importLayoutsInBackground(
				stagingGroup,
				ExportImportTestUtil.exportLayoutsAsFile(
					_sourceGroup, _sourceLayout)));

		String sourceLayoutName = _sourceLayout.getName(LocaleUtil.US);

		List<String> stagingGroupLayoutNames = _getLayoutNames(
			stagingGroup.getGroupId());

		Assert.assertTrue(
			stagingGroupLayoutNames.toString(),
			stagingGroupLayoutNames.contains(sourceLayoutName));

		List<String> liveGroupLayoutNames = _getLayoutNames(
			_stagedGroup.getGroupId());

		Assert.assertFalse(
			liveGroupLayoutNames.toString(),
			liveGroupLayoutNames.contains(sourceLayoutName));

		ExportImportTestUtil.assertBackgroundTaskSuccessful(
			StagingUtil.publishLayouts(
				TestPropsValues.getUserId(), stagingGroup.getGroupId(),
				_stagedGroup.getGroupId(), false,
				ExportImportConfigurationParameterMapFactoryUtil.
					buildFullPublishParameterMap()));

		liveGroupLayoutNames = _getLayoutNames(_stagedGroup.getGroupId());

		Assert.assertTrue(
			liveGroupLayoutNames.toString(),
			liveGroupLayoutNames.contains(sourceLayoutName));
	}

	@Test
	@TestInfo("LRQA-44517")
	public void testImportLARIntoStagingGroupWithDisplayPageLayoutPageTemplateEntry()
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_sourceGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_sourceGroup.getGroupId(),
				_portal.getClassNameId(JournalArticle.class),
				journalArticle.getDDMStructureKey());

		Layout layoutPageTemplateEntryLayout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		FragmentEntry fragmentEntry = _addFragmentEntry();

		String titleEditableId = RandomTestUtil.randomString();

		_addFragmentEntryLinkToLayout(
			_buildEditableValues(
				titleEditableId, "mappedField", "JournalArticle_title"),
			fragmentEntry, layoutPageTemplateEntryLayout);

		String contentEditableId = RandomTestUtil.randomString();

		_addFragmentEntryLinkToLayout(
			_buildEditableValues(
				contentEditableId, "mappedField", "JournalArticle_content"),
			fragmentEntry, layoutPageTemplateEntryLayout);

		Group stagingGroup = _stagedGroup.getStagingGroup();

		_importLayoutsIntoStagingGroup();

		LayoutPageTemplateEntry importedLayoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					layoutPageTemplateEntry.getExternalReferenceCode(),
					stagingGroup.getGroupId());

		Assert.assertEquals(
			"JournalArticle_content",
			_getEditableValue(
				stagingGroup.getGroupId(),
				importedLayoutPageTemplateEntry.getPlid(), contentEditableId,
				"mappedField"));
		Assert.assertEquals(
			"JournalArticle_title",
			_getEditableValue(
				stagingGroup.getGroupId(),
				importedLayoutPageTemplateEntry.getPlid(), titleEditableId,
				"mappedField"));
	}

	@Test
	@TestInfo("LRQA-44517")
	public void testImportLARIntoStagingGroupWithLayoutPageTemplateEntry()
		throws Exception {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateCollection(
				_sourceGroup.getGroupId());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.BASIC,
				WorkflowConstants.STATUS_APPROVED);

		Layout layoutPageTemplateEntryLayout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		String editableId = RandomTestUtil.randomString();
		String editableValue = RandomTestUtil.randomString();

		_addFragmentEntryLinkToLayout(
			_buildEditableValues(editableId, "en_US", editableValue),
			_addFragmentEntry(), layoutPageTemplateEntryLayout);

		Layout layout = _addLayout(layoutPageTemplateEntry);

		Group stagingGroup = _stagedGroup.getStagingGroup();

		_importLayoutsIntoStagingGroup();

		LayoutPageTemplateEntry importedLayoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					layoutPageTemplateEntry.getExternalReferenceCode(),
					stagingGroup.getGroupId());

		Assert.assertEquals(
			editableValue,
			_getEditableValue(
				stagingGroup.getGroupId(),
				importedLayoutPageTemplateEntry.getPlid(), editableId,
				"en_US"));

		Layout importedLayout =
			_layoutLocalService.getLayoutByExternalReferenceCode(
				layout.getExternalReferenceCode(), stagingGroup.getGroupId());

		Assert.assertEquals(
			editableValue,
			_getEditableValue(
				stagingGroup.getGroupId(), importedLayout.getPlid(), editableId,
				"en_US"));
	}

	@Test
	@TestInfo("LRQA-44517")
	public void testImportLARIntoStagingGroupWithSiteNavigationMenu()
		throws Exception {

		SiteNavigationMenu siteNavigationMenu =
			_siteNavigationMenuLocalService.addSiteNavigationMenu(
				null, TestPropsValues.getUserId(), _sourceGroup.getGroupId(),
				RandomTestUtil.randomString(),
				SiteNavigationConstants.TYPE_DEFAULT, false,
				ServiceContextTestUtil.getServiceContext(
					_sourceGroup.getGroupId(), TestPropsValues.getUserId()));

		_addLayoutTypeSiteNavigationMenuItem(siteNavigationMenu, _sourceLayout);
		_addLayoutTypeSiteNavigationMenuItem(
			siteNavigationMenu,
			LayoutTestUtil.addTypeContentLayout(_sourceGroup));

		String url = "https://www.liferay.com/";

		_addSiteNavigationMenuItem(
			siteNavigationMenu, SiteNavigationMenuItemTypeConstants.URL,
			UnicodePropertiesBuilder.create(
				true
			).put(
				"name", RandomTestUtil.randomString()
			).put(
				"url", url
			).put(
				"useNewTab", Boolean.FALSE.toString()
			).buildString());

		Group stagingGroup = _stagedGroup.getStagingGroup();

		_importLayoutsIntoStagingGroup();

		SiteNavigationMenu importedSiteNavigationMenu =
			_siteNavigationMenuLocalService.
				fetchSiteNavigationMenuByExternalReferenceCode(
					siteNavigationMenu.getExternalReferenceCode(),
					stagingGroup.getGroupId());

		List<SiteNavigationMenuItem> siteNavigationMenuItems =
			_siteNavigationMenuItemLocalService.getSiteNavigationMenuItems(
				importedSiteNavigationMenu.getSiteNavigationMenuId());

		List<String> types = TransformUtil.transform(
			siteNavigationMenuItems, SiteNavigationMenuItem::getType);

		Assert.assertEquals(types.toString(), 3, types.size());
		Assert.assertEquals(
			types.toString(), 2,
			Collections.frequency(
				types, SiteNavigationMenuItemTypeConstants.LAYOUT));
		Assert.assertEquals(
			types.toString(), 1,
			Collections.frequency(
				types, SiteNavigationMenuItemTypeConstants.URL));

		SiteNavigationMenuItem urlSiteNavigationMenuItem = _getFirst(
			siteNavigationMenuItems, SiteNavigationMenuItemTypeConstants.URL);

		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.fastLoad(
			urlSiteNavigationMenuItem.getTypeSettings()
		).build();

		Assert.assertEquals(url, unicodeProperties.getProperty("url"));
	}

	private FragmentEntry _addFragmentEntry() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_sourceGroup.getGroupId(), TestPropsValues.getUserId());

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(), _sourceGroup.getGroupId(),
				RandomTestUtil.randomString(), StringPool.BLANK,
				serviceContext);

		return _fragmentEntryLocalService.addFragmentEntry(
			null, TestPropsValues.getUserId(), _sourceGroup.getGroupId(),
			fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			StringPool.BLANK, RandomTestUtil.randomString(), StringPool.BLANK,
			false, "{fieldSets: []}", null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

	private void _addFragmentEntryLinkToLayout(
			String editableValues, FragmentEntry fragmentEntry, Layout layout)
		throws Exception {

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			editableValues, fragmentEntry.getCss(),
			fragmentEntry.getConfiguration(),
			fragmentEntry.getExternalReferenceCode(), null,
			fragmentEntry.getHtml(), fragmentEntry.getJs(), layout,
			fragmentEntry.getFragmentEntryKey(),
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid()),
			fragmentEntry.getType());
	}

	private Layout _addLayout(LayoutPageTemplateEntry layoutPageTemplateEntry)
		throws Exception {

		return _layoutLocalService.addLayout(
			null, TestPropsValues.getUserId(), _sourceGroup.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			_portal.getClassNameId(LayoutPageTemplateEntry.class),
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			HashMapBuilder.put(
				LocaleUtil.getSiteDefault(), RandomTestUtil.randomString()
			).build(),
			Collections.emptyMap(), Collections.emptyMap(),
			Collections.emptyMap(), Collections.emptyMap(),
			LayoutConstants.TYPE_CONTENT, StringPool.BLANK, false, false,
			Collections.emptyMap(), null,
			ServiceContextTestUtil.getServiceContext(
				_sourceGroup.getGroupId(), TestPropsValues.getUserId()));
	}

	private void _addLayoutTypeSiteNavigationMenuItem(
			SiteNavigationMenu siteNavigationMenu, Layout layout)
		throws Exception {

		_addSiteNavigationMenuItem(
			siteNavigationMenu, SiteNavigationMenuItemTypeConstants.LAYOUT,
			UnicodePropertiesBuilder.create(
				true
			).put(
				"externalReferenceCode", layout.getExternalReferenceCode()
			).put(
				"privateLayout", String.valueOf(layout.isPrivateLayout())
			).put(
				"title", layout.getName(LocaleUtil.US)
			).buildString());
	}

	private void _addSiteNavigationMenuItem(
			SiteNavigationMenu siteNavigationMenu, String type,
			String typeSettings)
		throws Exception {

		_siteNavigationMenuItemLocalService.addSiteNavigationMenuItem(
			null, TestPropsValues.getUserId(), _sourceGroup.getGroupId(),
			siteNavigationMenu.getSiteNavigationMenuId(), 0, type, typeSettings,
			ServiceContextTestUtil.getServiceContext(
				_sourceGroup.getGroupId(), TestPropsValues.getUserId()));
	}

	private String _buildEditableValues(
		String editableId, String key, String value) {

		return JSONUtil.put(
			FragmentEntryProcessorConstants.
				KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
			JSONUtil.put(editableId, JSONUtil.put(key, value))
		).toString();
	}

	private String _getEditableValue(
			long groupId, long plid, String editableId, String key)
		throws Exception {

		for (FragmentEntryLink fragmentEntryLink :
				_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
					groupId, plid)) {

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
				fragmentEntryLink.getEditableValues());

			JSONObject editableValuesJSONObject = JSONUtil.getValueAsJSONObject(
				jsonObject,
				"JSONObject/" +
					FragmentEntryProcessorConstants.
						KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
				"JSONObject/" + editableId);

			if (editableValuesJSONObject != null) {
				return editableValuesJSONObject.getString(key);
			}
		}

		return null;
	}

	private SiteNavigationMenuItem _getFirst(
		List<SiteNavigationMenuItem> siteNavigationMenuItems, String type) {

		for (SiteNavigationMenuItem siteNavigationMenuItem :
				siteNavigationMenuItems) {

			if (type.equals(siteNavigationMenuItem.getType())) {
				return siteNavigationMenuItem;
			}
		}

		return null;
	}

	private List<String> _getLayoutNames(long groupId) {
		return TransformUtil.transform(
			_layoutLocalService.getLayouts(groupId, false),
			layout -> layout.getName(LocaleUtil.US));
	}

	private Map<String, String[]> _getParameterMap() {
		return LinkedHashMapBuilder.put(
			PortletDataHandlerKeys.DATA_STRATEGY,
			new String[] {PortletDataHandlerKeys.DATA_STRATEGY_MIRROR_OVERWRITE}
		).put(
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
			PortletDataHandlerKeys.PORTLET_SETUP_ALL,
			new String[] {Boolean.TRUE.toString()}
		).build();
	}

	private void _importLayoutsIntoStagingGroup() throws Exception {
		User user = TestPropsValues.getUser();

		File larFile = ExportImportLocalServiceUtil.exportLayoutsAsFile(
			ExportImportConfigurationLocalServiceUtil.
				addDraftExportImportConfiguration(
					user.getUserId(),
					ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildExportLayoutSettingsMap(
							user, _sourceGroup.getGroupId(), false,
							ExportImportHelperUtil.getLayoutIds(
								_layoutLocalService.getLayouts(
									_sourceGroup.getGroupId(), false)),
							_getParameterMap())));

		Group stagingGroup = _stagedGroup.getStagingGroup();

		ExportImportLocalServiceUtil.importLayouts(
			ExportImportConfigurationLocalServiceUtil.
				addDraftExportImportConfiguration(
					user.getUserId(),
					ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildImportLayoutSettingsMap(
							user, stagingGroup.getGroupId(), false, null,
							_getParameterMap())),
			larFile);
	}

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject
	private SiteNavigationMenuItemLocalService
		_siteNavigationMenuItemLocalService;

	@Inject
	private SiteNavigationMenuLocalService _siteNavigationMenuLocalService;

	@DeleteAfterTestRun
	private Group _sourceGroup;

	private Layout _sourceLayout;

	@DeleteAfterTestRun
	private Group _stagedGroup;

	@Inject
	private StagingLocalService _stagingLocalService;

}