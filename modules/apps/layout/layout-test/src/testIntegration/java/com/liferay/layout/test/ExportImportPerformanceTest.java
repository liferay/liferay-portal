/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactory;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.exportimport.kernel.service.StagingLocalService;
import com.liferay.exportimport.kernel.staging.MergeLayoutPrototypesThreadLocal;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.performance.PerformanceTimer;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.sites.kernel.util.Sites;

import java.io.Closeable;
import java.io.File;
import java.io.Serializable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pavel Savinov
 */
@Ignore
@RunWith(Arquillian.class)
public class ExportImportPerformanceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		Class<?> clazz = ExportImportPerformanceTest.class;

		Properties properties = PropertiesUtil.load(
			clazz.getResourceAsStream(
				"dependencies/export-import-performance.properties"),
			"UTF-8");

		_fragmentEntryLinksPerLayout = GetterUtil.getInteger(
			properties.getProperty("fragment.entry.links.per.layout"));
		_layoutsCount = GetterUtil.getInteger(
			properties.getProperty("layouts.count"));
		_layoutType = properties.getProperty("layout.type");
		_logFilePath = Paths.get(properties.getProperty("log.file"));
		_portletsPerContentLayout = GetterUtil.getInteger(
			properties.getProperty("portlets.per.content.layout"));
		_portletsPerPortletLayout = GetterUtil.getInteger(
			properties.getProperty("portlets.per.portlet.layout"));

		Files.deleteIfExists(_logFilePath);

		_writeToLogFile(
			"Properties:",
			StreamUtil.toString(
				clazz.getResourceAsStream(
					"dependencies/export-import-performance.properties")),
			"\nResults:");
	}

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
		_importGroup = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);

		long ddmGroupId = GetterUtil.getLong(
			_serviceContext.getAttribute("ddmGroupId"), _group.getGroupId());

		Locale defaultLocale = LocaleUtil.fromLanguageId(
			_group.getDefaultLanguageId());

		DDMForm ddmForm = DDMStructureTestUtil.getSampleDDMForm(
			new Locale[] {defaultLocale}, defaultLocale);

		_ddmStructure = DDMStructureTestUtil.addStructure(
			ddmGroupId, JournalArticle.class.getName(), ddmForm, defaultLocale);

		_ddmTemplate = DDMTemplateTestUtil.addTemplate(
			ddmGroupId, _ddmStructure.getStructureId(),
			_portal.getClassNameId(JournalArticle.class));

		_addLayouts();

		_layoutIds = ListUtil.toLongArray(
			_layoutLocalService.getLayouts(_group.getGroupId(), false),
			Layout::getLayoutId);

		_layoutSetPrototype = LayoutTestUtil.addLayoutSetPrototype(
			RandomTestUtil.randomString());
	}

	@Test
	public void testExportGroupToLAR() throws Exception {
		try (Closeable closeable = new PerformanceTimer(_logFilePath, 1000)) {
			Map<String, Serializable> exportLayoutSettingsMap =
				_exportImportConfigurationSettingsMapFactory.
					buildExportLayoutSettingsMap(
						TestPropsValues.getUser(), _group.getGroupId(), false,
						_layoutIds, new HashMap<>());

			_exportImportConfiguration =
				_exportImportConfigurationLocalService.
					addDraftExportImportConfiguration(
						TestPropsValues.getUserId(), "export-group",
						ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT,
						exportLayoutSettingsMap);

			_exportImportLocalService.exportLayoutsAsFile(
				_exportImportConfiguration);
		}
	}

	@Test
	public void testImportGroupFromLAR() throws Exception {
		Map<String, Serializable> exportLayoutSettingsMap =
			_exportImportConfigurationSettingsMapFactory.
				buildExportLayoutSettingsMap(
					TestPropsValues.getUser(), _group.getGroupId(), false,
					_layoutIds, new HashMap<>());

		_exportImportConfiguration =
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					TestPropsValues.getUserId(), "export-group",
					ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT,
					exportLayoutSettingsMap);

		File file = _exportImportLocalService.exportLayoutsAsFile(
			_exportImportConfiguration);

		try (Closeable closeable = new PerformanceTimer(_logFilePath, 1000)) {
			Map<String, Serializable> importLayoutSettingsMap =
				_exportImportConfigurationSettingsMapFactory.
					buildImportLayoutSettingsMap(
						TestPropsValues.getUser(), _importGroup.getGroupId(),
						false, _layoutIds, new HashMap<>());

			_exportImportConfiguration =
				_exportImportConfigurationLocalService.
					addDraftExportImportConfiguration(
						TestPropsValues.getUserId(), "import-group",
						ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT,
						importLayoutSettingsMap);

			_exportImportLocalService.importLayouts(
				_exportImportConfiguration, file);
		}
	}

	@Test
	public void testInitialStagingPublication() throws Exception {
		try (Closeable closeable = new PerformanceTimer(_logFilePath, 10000)) {
			_stagingLocalService.enableLocalStaging(
				TestPropsValues.getUserId(), _group, false, false,
				_serviceContext);
		}
	}

	@Test
	public void testSiteTemplatePropagation() throws Exception {
		MergeLayoutPrototypesThreadLocal.clearMergeComplete();

		for (long layoutId : _layoutIds) {
			Layout layout = _layoutLocalService.getLayout(
				_group.getGroupId(), false, layoutId);

			LayoutPrototype layoutPrototype = LayoutTestUtil.addLayoutPrototype(
				RandomTestUtil.randomString());

			Layout layoutPrototypeLayout = layoutPrototype.getLayout();

			layoutPrototypeLayout.setType(layout.getType());

			layoutPrototypeLayout = _layoutLocalService.updateLayout(
				layoutPrototypeLayout);

			_layoutLocalService.copyLayoutContent(
				layout, layoutPrototypeLayout);

			layout.setLayoutPrototypeUuid(layoutPrototype.getUuid());
			layout.setLayoutPrototypeLinkEnabled(true);

			_layoutLocalService.updateLayout(layout);
		}

		_sites.updateLayoutSetPrototypesLinks(
			_group, _layoutSetPrototype.getLayoutSetPrototypeId(), 0, true,
			true);

		try (Closeable closeable = new PerformanceTimer(_logFilePath, 1000)) {
			MergeLayoutPrototypesThreadLocal.clearMergeComplete();

			_sites.mergeLayoutSetPrototypeLayouts(
				_group, _group.getPublicLayoutSet());
		}
	}

	@Test
	public void testStagingPublication() throws Exception {
		_stagingLocalService.enableLocalStaging(
			TestPropsValues.getUserId(), _group, false, false, _serviceContext);

		try (Closeable closeable = new PerformanceTimer(_logFilePath, 1000)) {
			Group stagingGroup = _group.getStagingGroup();

			Map<String, Serializable> stagingSettingsMap =
				_exportImportConfigurationSettingsMapFactory.
					buildPublishLayoutLocalSettingsMap(
						TestPropsValues.getUser(), stagingGroup.getGroupId(),
						_group.getGroupId(), false, _layoutIds,
						new HashMap<>());

			_exportImportConfiguration =
				_exportImportConfigurationLocalService.
					addDraftExportImportConfiguration(
						TestPropsValues.getUserId(), "publish-group",
						ExportImportConfigurationConstants.
							TYPE_PUBLISH_LAYOUT_LOCAL,
						stagingSettingsMap);

			long backgroundTaskId = _staging.publishLayouts(
				TestPropsValues.getUserId(), _exportImportConfiguration);

			BackgroundTask backgroundTask =
				_backgroundTaskManager.getBackgroundTask(backgroundTaskId);

			Assert.assertTrue(backgroundTask.isCompleted());
		}
	}

	private static void _writeToLogFile(String... contents) throws Exception {
		Files.write(
			_logFilePath, Arrays.asList(contents), StandardOpenOption.APPEND,
			StandardOpenOption.CREATE, StandardOpenOption.WRITE);
	}

	private void _addFragmentEntryLinks(Layout layout) throws Exception {
		Layout draftLayout = layout.fetchDraftLayout();

		if (draftLayout == null) {
			return;
		}

		long defaultSegmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid());

		for (int i = 0; i < _fragmentEntryLinksPerLayout; i++) {
			FragmentEntry fragmentEntry =
				_fragmentCollectionContributorRegistry.getFragmentEntry(
					"FEATURED_CONTENT-highlights-circle");

			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				fragmentEntry.getFragmentEntryId(), defaultSegmentsExperienceId,
				draftLayout.getPlid(), fragmentEntry.getCss(),
				fragmentEntry.getHtml(), fragmentEntry.getJs(),
				fragmentEntry.getConfiguration(),
				StringUtil.replace(
					_TMPL_FRAGMENT_EDITABLE_VALUES, "${", "}",
					HashMapBuilder.put(
						"classNameId",
						String.valueOf(
							_portal.getClassNameId(JournalArticle.class))
					).put(
						"classPK_1",
						() -> {
							JournalArticle journalArticle1 =
								_addJournalArticle();

							return String.valueOf(
								journalArticle1.getResourcePrimKey());
						}
					).put(
						"classPK_2",
						() -> {
							JournalArticle journalArticle2 =
								_addJournalArticle();

							return String.valueOf(
								journalArticle2.getResourcePrimKey());
						}
					).put(
						"classPK_3",
						() -> {
							JournalArticle journalArticle3 =
								_addJournalArticle();

							return String.valueOf(
								journalArticle3.getResourcePrimKey());
						}
					).build()),
				StringPool.BLANK, 0, null, fragmentEntry.getType(),
				_serviceContext);
		}

		for (int i = 0; i < _portletsPerContentLayout; i++) {
			String instanceId = PortletIdCodec.generateInstanceId();

			_updateLayoutPortletSetup(
				draftLayout,
				PortletIdCodec.encode(JournalPortletKeys.JOURNAL, instanceId));

			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0, 0,
				defaultSegmentsExperienceId, draftLayout.getPlid(),
				StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
				StringPool.BLANK,
				StringUtil.replace(
					_TMPL_FRAGMENT_PORTLET, "${", "}",
					HashMapBuilder.put(
						"instanceId", instanceId
					).put(
						"portletName", JournalPortletKeys.JOURNAL
					).build()),
				StringPool.BLANK, 0, null, FragmentConstants.TYPE_COMPONENT,
				_serviceContext);
		}

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				TestPropsValues.getUserId(), _group.getGroupId(),
				draftLayout.getPlid(), defaultSegmentsExperienceId,
				_generateContentLayoutStructureJSONObject(draftLayout));

		_layoutLocalService.copyLayoutContent(draftLayout, layout);
	}

	private JournalArticle _addJournalArticle() throws Exception {
		Locale defaultLocale = LocaleUtil.fromLanguageId(
			_group.getDefaultLanguageId());

		String content = DDMStructureTestUtil.getSampleStructuredContent(
			RandomTestUtil.randomLocaleStringMap(defaultLocale),
			LocaleUtil.toLanguageId(defaultLocale));

		return _journalArticleLocalService.addArticle(
			null, _serviceContext.getUserId(), _group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, 0, StringPool.BLANK,
			true, JournalArticleConstants.VERSION_DEFAULT,
			RandomTestUtil.randomLocaleStringMap(defaultLocale),
			RandomTestUtil.randomLocaleStringMap(defaultLocale),
			RandomTestUtil.randomLocaleStringMap(defaultLocale), content,
			_ddmStructure.getStructureId(), _ddmTemplate.getTemplateKey(),
			StringPool.BLANK, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, true, 0, 0, 0, 0, 0,
			true, true, false, 0, 0, null, null, null, null, _serviceContext);
	}

	private void _addLayouts() throws Exception {
		for (int i = 0; i < _layoutsCount; i++) {
			Layout layout = _layoutLocalService.addLayout(
				null, TestPropsValues.getUserId(), _group.getGroupId(), false,
				0, 0, 0,
				HashMapBuilder.put(
					LocaleUtil.US, "Layout_" + i
				).build(),
				new HashMap<>(), new HashMap<>(), new HashMap<>(),
				new HashMap<>(), _layoutType, StringPool.BLANK, false, false,
				new HashMap<>(), 0, _serviceContext);

			if (Objects.equals(_layoutType, LayoutConstants.TYPE_CONTENT)) {
				_addFragmentEntryLinks(layout);
			}
			else {
				_addPortletIds(layout);
			}
		}
	}

	private void _addPortletIds(Layout layout) throws Exception {
		UnicodeProperties unicodeProperties =
			layout.getTypeSettingsProperties();

		String column1Value = unicodeProperties.getProperty("column-1");

		for (int i = 0; i < _portletsPerPortletLayout; i++) {
			String portletId = PortletIdCodec.encode(
				JournalPortletKeys.JOURNAL,
				PortletIdCodec.generateInstanceId());

			_updateLayoutPortletSetup(layout, portletId);

			if (Validator.isNotNull(column1Value)) {
				column1Value += StringPool.COMMA;
			}

			column1Value += portletId;
		}

		unicodeProperties.setProperty("column-1", column1Value);

		layout.setTypeSettingsProperties(unicodeProperties);

		_layoutLocalService.updateLayout(layout);
	}

	private String _generateContentLayoutStructureJSONObject(Layout layout) {
		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		LayoutStructureItem containerStyledLayoutStructureItem =
			layoutStructure.addContainerStyledLayoutStructureItem(
				rootLayoutStructureItem.getItemId(), 0);

		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
				_group.getGroupId(), layout.getPlid());

		for (int i = 0; i < fragmentEntryLinks.size(); i++) {
			FragmentEntryLink fragmentEntryLink = fragmentEntryLinks.get(i);

			layoutStructure.addFragmentStyledLayoutStructureItem(
				fragmentEntryLink.getFragmentEntryLinkId(),
				containerStyledLayoutStructureItem.getItemId(), i);
		}

		return layoutStructure.toString();
	}

	private void _updateLayoutPortletSetup(Layout layout, String portletId)
		throws Exception {

		JournalArticle journalArticle = _addJournalArticle();

		AssetEntry assetEntry = _assetEntryLocalService.getEntry(
			JournalArticle.class.getName(),
			journalArticle.getResourcePrimKey());

		PortletPreferencesFactoryUtil.getLayoutPortletSetup(
			layout, portletId,
			StringUtil.replace(
				_TMPL_PORTLET_PREFERENCES, "${", "}",
				HashMapBuilder.put(
					"articleId", journalArticle.getArticleId()
				).put(
					"assetEntryId", String.valueOf(assetEntry.getEntryId())
				).put(
					"groupId", String.valueOf(assetEntry.getGroupId())
				).build()));
	}

	private static final String _TMPL_FRAGMENT_EDITABLE_VALUES =
		StringUtil.read(
			ExportImportPerformanceTest.class,
			"dependencies/fragment-editable-values.tmpl");

	private static final String _TMPL_FRAGMENT_PORTLET = StringUtil.read(
		ExportImportPerformanceTest.class,
		"dependencies/fragment-portlet.tmpl");

	private static final String _TMPL_PORTLET_PREFERENCES = StringUtil.read(
		ExportImportPerformanceTest.class,
		"dependencies/portlet-preferences.tmpl");

	private static int _fragmentEntryLinksPerLayout;
	private static int _layoutsCount;
	private static String _layoutType;
	private static Path _logFilePath;
	private static int _portletsPerContentLayout;
	private static int _portletsPerPortletLayout;

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private BackgroundTaskManager _backgroundTaskManager;

	private DDMStructure _ddmStructure;
	private DDMTemplate _ddmTemplate;
	private ExportImportConfiguration _exportImportConfiguration;

	@Inject
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@Inject
	private ExportImportConfigurationSettingsMapFactory
		_exportImportConfigurationSettingsMapFactory;

	@Inject
	private ExportImportLocalService _exportImportLocalService;

	@Inject
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	private Group _group;
	private Group _importGroup;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	private long[] _layoutIds;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	private LayoutSetPrototype _layoutSetPrototype;

	@Inject
	private Portal _portal;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceContext _serviceContext;

	@Inject
	private Sites _sites;

	@Inject
	private Staging _staging;

	@Inject
	private StagingLocalService _stagingLocalService;

}