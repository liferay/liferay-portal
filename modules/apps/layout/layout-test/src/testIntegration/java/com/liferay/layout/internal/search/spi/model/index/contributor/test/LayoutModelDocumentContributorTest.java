/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.search.spi.model.index.contributor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.data.engine.rest.resource.v2_0.DataDefinitionResource;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesToFieldsConverter;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.info.field.InfoField;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.journal.util.JournalConverter;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.test.util.IndexerFixture;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.util.ThemeFactoryUtil;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.template.test.util.TemplateTestUtil;

import jakarta.portlet.PortletPreferences;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
public class LayoutModelDocumentContributorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		_draftLayout = _layout.fetchDraftLayout();

		_locale = _portal.getSiteDefaultLocale(_group);

		_languageId = LocaleUtil.toLanguageId(_locale);

		_layoutIndexerFixture = new IndexerFixture<>(Layout.class);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@After
	public void tearDown() throws Exception {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testGetContentLayoutSummary() throws Exception {
		String defaultLocaleElementText = RandomTestUtil.randomString();
		String spanishElementText = RandomTestUtil.randomString();

		FragmentEntry fragmentEntry =
			_fragmentCollectionContributorRegistry.getFragmentEntry(
				"BASIC_COMPONENT-heading");

		Layout draftLayout = _layout.fetchDraftLayout();

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					"element-text",
					JSONUtil.put(
						_languageId, defaultLocaleElementText
					).put(
						"es_ES", spanishElementText
					))
			).toString(),
			fragmentEntry.getCss(), fragmentEntry.getConfiguration(),
			fragmentEntry.getFragmentEntryId(), fragmentEntry.getHtml(),
			fragmentEntry.getJs(), _draftLayout,
			fragmentEntry.getFragmentEntryKey(), fragmentEntry.getType(), null,
			0,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid()));

		ContentLayoutTestUtil.publishLayout(_draftLayout, _layout);

		Document document = _layoutIndexerFixture.searchOnlyOne(
			spanishElementText, LocaleUtil.SPAIN);

		Indexer<Layout> indexer = IndexerRegistryUtil.getIndexer(Layout.class);

		Summary summary = indexer.getSummary(document, LocaleUtil.SPAIN, null);

		Assert.assertEquals(spanishElementText, summary.getContent());
		Assert.assertEquals(_layout.getName(_locale), summary.getTitle());
	}

	@Test
	@TestInfo("LPD-43082")
	public void testReindexPublishedDraftLayoutWithFreeMarkerUsingLocale()
		throws Exception {

		String contentText = RandomTestUtil.randomString();

		_addFragmentEntryLinkToLayout(
			"{}", contentText + "[@liferay.language key=\"success\" /]",
			_draftLayout);

		ContentLayoutTestUtil.publishLayout(_draftLayout, _layout);

		_assertSearch(
			contentText + LanguageUtil.get(LocaleUtil.GERMANY, "success"),
			LocaleUtil.GERMANY);
	}

	@Test
	public void testReindexPublishedDraftLayoutWithLayoutLocalization()
		throws Exception {

		String elementText = RandomTestUtil.randomString();

		_setUpLayout(elementText, true, null);

		_assertReindex(elementText);

		String draftElementText = RandomTestUtil.randomString();

		_addFragmentEntryLinkToLayout(
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					"element-text", JSONUtil.put(_languageId, draftElementText))
			).toString(),
			_draftLayout);

		_assertReindexDraftLayout(draftElementText, _draftLayout);

		_assertSearch(elementText, _locale);
	}

	@Test
	public void testReindexPublishedLayout() throws Exception {
		_assertReindexPublishedLayout(null);
	}

	@Test
	public void testReindexPublishedLayoutFragmentEntryLinkWithInformationTemplate()
		throws Exception {

		String content = RandomTestUtil.randomString();

		JournalArticle journalArticle =
			JournalTestUtil.addArticleWithXMLContent(
				DDMStructureTestUtil.getSampleStructuredContent(
					"content",
					Collections.singletonList(
						HashMapBuilder.put(
							_locale, content
						).build()),
					_languageId),
				"BASIC-WEB-CONTENT", "BASIC-WEB-CONTENT");

		InfoField infoField = TemplateTestUtil.addTemplateEntryInfoField(
			"DDMStructure_content", JournalArticle.class.getName(),
			String.valueOf(journalArticle.getDDMStructureId()),
			_infoItemServiceRegistry, _serviceContext);

		_addFragmentEntryLinkToLayout(
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					"element-text",
					JSONUtil.put(
						"className", JournalArticle.class.getName()
					).put(
						"classNameId",
						String.valueOf(
							_portal.getClassNameId(
								JournalArticle.class.getName()))
					).put(
						"classPK",
						String.valueOf(journalArticle.getResourcePrimKey())
					).put(
						"classTypeId",
						String.valueOf(journalArticle.getDDMStructureId())
					).put(
						"fieldId", infoField.getUniqueId()
					))
			).toString(),
			_draftLayout);

		ContentLayoutTestUtil.publishLayout(_draftLayout, _layout);

		_assertReindex(content);
	}

	@Test
	public void testReindexPublishedLayoutFragmentEntryLinkWithPortlet()
		throws Exception {

		_assertReindexPublishedLayoutFragmentEntryLinkWithPortlet();
	}

	@Test
	public void testReindexPublishedLayoutFragmentEntryLinkWithPortletVirtualHostSite()
		throws Exception {

		_layoutSetLocalService.updateVirtualHosts(
			_group.getGroupId(), false,
			TreeMapBuilder.put(
				"myvirtualhost", LocaleUtil.toLanguageId(_locale)
			).build());

		_assertReindexPublishedLayoutFragmentEntryLinkWithPortlet();
	}

	@Test
	@TestInfo("LPD-22568")
	public void testReindexPublishedLayoutLayoutSetThemeNotAvailable()
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME_THEME_LOCAL_SERVICE_IMPL, LoggerTestUtil.INFO)) {

			LayoutSet layoutSet = _group.getPublicLayoutSet();

			_layoutSetLocalService.updateLookAndFeel(
				layoutSet.getGroupId(), layoutSet.isPrivateLayout(),
				"not_available_theme_id", layoutSet.getColorSchemeId(),
				layoutSet.getCss());

			_assertReindexPublishedLayout(null);
		}
	}

	@Test
	@TestInfo("LPD-22568")
	public void testReindexPublishedLayoutSpecificThemeAndLayoutSetThemeNotAvailable()
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME_THEME_LOCAL_SERVICE_IMPL, LoggerTestUtil.INFO)) {

			LayoutSet layoutSet = _group.getPublicLayoutSet();

			_layoutSetLocalService.updateLookAndFeel(
				layoutSet.getGroupId(), layoutSet.isPrivateLayout(),
				"not_available_theme_id", layoutSet.getColorSchemeId(),
				layoutSet.getCss());

			_assertReindexPublishedLayout(
				ThemeFactoryUtil.getDefaultRegularThemeId(
					TestPropsValues.getCompanyId()));
		}
	}

	@Test
	@TestInfo("LPD-22568")
	public void testReindexPublishedLayoutThemeNotAvailable() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME_THEME_LOCAL_SERVICE_IMPL, LoggerTestUtil.INFO)) {

			_assertReindexPublishedLayout("not_available_theme_id");
		}
	}

	@Test
	public void testReindexPublishedLayoutWithFragmentEntryLinkTypePortlet()
		throws Exception {

		String portletId = _addJournalContentPortletToDraftLayout();

		String content = RandomTestUtil.randomString();

		DDMFormField ddmFormField = _createDDMFormField(
			DDMFormFieldTypeConstants.TEXT);

		JournalArticle journalArticle = JournalTestUtil.addJournalArticle(
			_dataDefinitionResourceFactory, ddmFormField,
			_ddmFormValuesToFieldsConverter, content, _group.getGroupId(),
			_journalConverter);

		_setUpPortletPreferences(journalArticle, portletId);

		ContentLayoutTestUtil.publishLayout(_draftLayout, _layout);

		_assertPortletPreferences(journalArticle, _layout, portletId);

		_assertReindex(content);
	}

	@Test
	@TestInfo("LPD-50788")
	public void testReindexPublishedLayoutWithFragmentEntryLinkTypePortletWithLocalizedContent()
		throws Exception {

		String portletId = _addJournalContentPortletToDraftLayout();

		Map<Locale, String> contentMap = HashMapBuilder.put(
			LocaleUtil.SPAIN, RandomTestUtil.randomString()
		).put(
			LocaleUtil.US, RandomTestUtil.randomString()
		).build();

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), 0,
			_portal.getClassNameId(JournalArticle.class),
			HashMapBuilder.put(
				LocaleUtil.SPAIN, RandomTestUtil.randomString()
			).put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			null, contentMap, LocaleUtil.US, true, true, _serviceContext);

		_setUpPortletPreferences(journalArticle, portletId);

		ContentLayoutTestUtil.publishLayout(_draftLayout, _layout);

		_assertPortletPreferences(journalArticle, _layout, portletId);

		List<LogEntry> logEntries = _reindexLayoutsLogEntries();

		Assert.assertEquals(logEntries.toString(), 0, logEntries.size());

		_assertSearch(contentMap.get(LocaleUtil.SPAIN), LocaleUtil.SPAIN);
		_assertSearch(contentMap.get(LocaleUtil.US), LocaleUtil.GERMANY);
		_assertSearch(contentMap.get(LocaleUtil.US), LocaleUtil.US);
	}

	@Test
	public void testReindexPublishedLayoutWithFreeMarkerErrors()
		throws Exception {

		String elementText = RandomTestUtil.randomString();

		_setUpLayout(elementText, true, null);

		String html =
			"[#if /#] <h1 data-lfr-editable-id=\"element-text\" " +
				"data-lfr-editable-type=\"text\">Heading Example</h1>";

		_addFragmentEntryLinkToLayout(
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					"element-text", JSONUtil.put(_languageId, elementText))
			).toString(),
			html, _layout);

		_reindexLogEntries(_layout);

		Document document = _layoutIndexerFixture.searchOnlyOne(
			_layout.getName(_locale), _locale);

		Assert.assertNotNull(document);

		String content = document.get(
			Field.getLocalizedName(_locale, Field.CONTENT));

		Assert.assertEquals(elementText, content);

		Assert.assertEquals(
			document.get(Field.ENTRY_CLASS_PK),
			String.valueOf(_layout.getPlid()));
	}

	@Test
	public void testReindexPublishedLayoutWithPortletDisplayingJournalArticleWithGeolocationDDMFormField()
		throws Exception {

		String portletId = _addJournalContentPortletToDraftLayout();

		DDMFormField ddmFormField = _createDDMFormField(
			DDMFormFieldTypeConstants.GEOLOCATION);

		double lat = RandomTestUtil.randomDouble();
		double lng = RandomTestUtil.randomDouble();

		JournalArticle journalArticle = JournalTestUtil.addJournalArticle(
			_dataDefinitionResourceFactory, ddmFormField,
			_ddmFormValuesToFieldsConverter,
			JSONUtil.put(
				"lat", lat
			).put(
				"lng", lng
			).toString(),
			_group.getGroupId(), _journalConverter);

		_setUpPortletPreferences(journalArticle, portletId);

		ContentLayoutTestUtil.publishLayout(_draftLayout, _layout);

		_assertPortletPreferences(journalArticle, _layout, portletId);

		_assertReindex("\"lat\":" + lat, "\"lng\":" + lng);
	}

	@Test
	public void testReindexUnpublishedDraftLayout() throws Exception {
		String elementText = RandomTestUtil.randomString();

		_setUpLayout(elementText, false, null);

		_assertReindexDraftLayout(elementText, _layout);
	}

	@Test
	@TestInfo("LPS-152949")
	public void testSearchEmbeddedLayout() throws Exception {
		Layout layout = LayoutTestUtil.addTypeEmbeddedLayout(
			_group.getGroupId());

		Document document = _layoutIndexerFixture.searchOnlyOne(
			layout.getName(_locale), _locale);

		Assert.assertNotNull(document);
	}

	@Test
	@TestInfo("LPS-152949")
	public void testSearchFullPageApplicationLayout() throws Exception {
		Layout layout = LayoutTestUtil.addTypeFullPageApplicationLayout(
			_group.getGroupId());

		Document document = _layoutIndexerFixture.searchOnlyOne(
			layout.getName(_locale), _locale);

		Assert.assertNotNull(document);
	}

	@Test
	@TestInfo("LPS-152949")
	public void testSearchLinkToURLLayout() throws Exception {
		Layout layout = LayoutTestUtil.addTypeLinkToURLLayout(
			_group.getGroupId(), "https://www.liferay.com");

		Document document = _layoutIndexerFixture.searchOnlyOne(
			layout.getName(_locale), _locale);

		Assert.assertNotNull(document);
	}

	@Test
	@TestInfo("LPS-152949")
	public void testSearchPanelLayout() throws Exception {
		Layout layout = LayoutTestUtil.addTypePanelLayout(_group.getGroupId());

		Document document = _layoutIndexerFixture.searchOnlyOne(
			layout.getName(_locale), _locale);

		Assert.assertNotNull(document);
	}

	@Test
	@TestInfo("LPS-152949")
	public void testSearchPortletLayout() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		Document document = _layoutIndexerFixture.searchOnlyOne(
			layout.getName(_locale), _locale);

		Assert.assertNotNull(document);
	}

	private FragmentEntryLink _addFragmentEntryLinkToLayout(
			String editableValues, Layout layout)
		throws Exception {

		return _addFragmentEntryLinkToLayout(
			editableValues,
			"<h1 data-lfr-editable-id=\"element-text\" " +
				"data-lfr-editable-type=\"text\">Heading Example</h1>",
			layout);
	}

	private FragmentEntryLink _addFragmentEntryLinkToLayout(
			String editableValues, String html, Layout layout)
		throws Exception {

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), null, _serviceContext);

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				fragmentCollection.getFragmentCollectionId(), null,
				RandomTestUtil.randomString(), null, html, null, false, null,
				null, 0, false, false, FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED, _serviceContext);

		return ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			editableValues, fragmentEntry.getCss(),
			fragmentEntry.getConfiguration(),
			fragmentEntry.getFragmentEntryId(), fragmentEntry.getHtml(),
			fragmentEntry.getJs(), layout, fragmentEntry.getFragmentEntryKey(),
			fragmentEntry.getType(), null, 0,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid()));
	}

	private String _addJournalContentPortletToDraftLayout() throws Exception {
		JSONObject processAddPortletJSONObject =
			ContentLayoutTestUtil.addPortletToLayout(
				_draftLayout, JournalContentPortletKeys.JOURNAL_CONTENT);

		JSONObject fragmentEntryLinkJSONObject =
			processAddPortletJSONObject.getJSONObject("fragmentEntryLink");

		JSONObject editableValuesJSONObject =
			fragmentEntryLinkJSONObject.getJSONObject("editableValues");

		return PortletIdCodec.encode(
			editableValuesJSONObject.getString("portletId"),
			editableValuesJSONObject.getString("instanceId"));
	}

	private void _assertPortletPreferences(
		JournalArticle journalArticle, Layout layout, String portletId) {

		PortletPreferences portletPreferences =
			_portletPreferencesFactory.getPortletSetup(layout, portletId, null);

		Assert.assertEquals(
			String.valueOf(journalArticle.getExternalReferenceCode()),
			portletPreferences.getValue("articleExternalReferenceCode", null));
		Assert.assertEquals(
			String.valueOf(journalArticle.getGroupId()),
			portletPreferences.getValue("groupExternalReferenceCode", null));
	}

	private void _assertReindex(String... expectedContents) throws Exception {
		List<LogEntry> logEntries = _reindexLayoutsLogEntries();

		Assert.assertEquals(logEntries.toString(), 0, logEntries.size());

		for (String keywords : expectedContents) {
			_assertSearch(keywords, _locale);
		}
	}

	private void _assertReindexDraftLayout(String keywords, Layout layout)
		throws Exception {

		_layoutIndexerFixture.searchNoOne(keywords);

		List<LogEntry> logEntries = _reindexLogEntries(layout);

		Assert.assertEquals(logEntries.toString(), 0, logEntries.size());

		_layoutIndexerFixture.searchNoOne(keywords);
	}

	private void _assertReindexPublishedLayout(String themeId)
		throws Exception {

		String elementText = RandomTestUtil.randomString();

		_setUpLayout(elementText, true, themeId);

		List<LogEntry> logEntries = _reindexLogEntries(_layout);

		Assert.assertEquals(logEntries.toString(), 0, logEntries.size());

		_assertSearch(elementText, _locale);
	}

	private void _assertReindexPublishedLayoutFragmentEntryLinkWithPortlet()
		throws Exception {

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLinkToLayout(
			"{}", "<lfr-widget-web-content>", _draftLayout);

		String portletId = PortletIdCodec.encode(
			JournalContentPortletKeys.JOURNAL_CONTENT,
			fragmentEntryLink.getNamespace());

		String content = RandomTestUtil.randomString();

		DDMFormField ddmFormField = _createDDMFormField(
			DDMFormFieldTypeConstants.TEXT);

		JournalArticle journalArticle = JournalTestUtil.addJournalArticle(
			_dataDefinitionResourceFactory, ddmFormField,
			_ddmFormValuesToFieldsConverter, content, _group.getGroupId(),
			_journalConverter);

		_setUpPortletPreferences(journalArticle, portletId);

		ContentLayoutTestUtil.publishLayout(_draftLayout, _layout);

		_assertPortletPreferences(journalArticle, _layout, portletId);

		_assertReindex(content);
	}

	private void _assertSearch(String keywords, Locale locale) {
		Document document = _layoutIndexerFixture.searchOnlyOne(
			keywords, locale);

		Assert.assertNotNull(document);

		String content = document.get(
			Field.getLocalizedName(locale, Field.CONTENT));

		Assert.assertTrue(
			content, StringUtil.contains(content, keywords, StringPool.BLANK));

		Assert.assertEquals(
			document.get(Field.ENTRY_CLASS_PK),
			String.valueOf(_layout.getPlid()));
	}

	private DDMFormField _createDDMFormField(String type) {
		DDMFormField ddmFormField = new DDMFormField("name", type);

		ddmFormField.setDataType("text");
		ddmFormField.setIndexType("text");

		LocalizedValue localizedValue = new LocalizedValue(LocaleUtil.US);

		localizedValue.addString(
			LocaleUtil.US, RandomTestUtil.randomString(10));

		ddmFormField.setLabel(localizedValue);

		ddmFormField.setLocalizable(true);

		return ddmFormField;
	}

	private List<LogEntry> _reindexLayoutsLogEntries() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME_INCLUDE_TAG, LoggerTestUtil.DEBUG)) {

			_indexWriterHelper.reindex(
				TestPropsValues.getUserId(), "reindex",
				new long[] {_group.getCompanyId()}, Layout.class.getName(),
				null);

			return logCapture.getLogEntries();
		}
	}

	private List<LogEntry> _reindexLogEntries(Layout layout) throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME_LAYOUT_MODEL_DOCUMENT_CONTRIBUTOR,
				LoggerTestUtil.DEBUG)) {

			Indexer<Layout> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
				Layout.class);

			indexer.reindex(layout);

			return logCapture.getLogEntries();
		}
	}

	private void _setUpLayout(
			String elementText, boolean publish, String themeId)
		throws Exception {

		_addFragmentEntryLinkToLayout(
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					"element-text", JSONUtil.put(_languageId, elementText))
			).toString(),
			_draftLayout);

		if (themeId != null) {
			_draftLayout = _layoutLocalService.updateLookAndFeel(
				_draftLayout.getGroupId(), _draftLayout.isPrivateLayout(),
				_draftLayout.getLayoutId(), themeId,
				_draftLayout.getColorSchemeId(), _draftLayout.getCss());
		}

		if (publish) {
			ContentLayoutTestUtil.publishLayout(_draftLayout, _layout);

			_layout = _layoutLocalService.getLayout(_layout.getPlid());
		}
	}

	private void _setUpPortletPreferences(
			JournalArticle journalArticle, String portletId)
		throws Exception {

		PortletPreferences portletPreferences =
			_portletPreferencesFactory.getPortletSetup(
				_draftLayout, portletId, null);

		portletPreferences.setValue(
			"articleExternalReferenceCode",
			journalArticle.getExternalReferenceCode());
		portletPreferences.setValue(
			"groupExternalReferenceCode",
			String.valueOf(journalArticle.getGroupId()));

		portletPreferences.store();

		_assertPortletPreferences(journalArticle, _draftLayout, portletId);
	}

	private static final String _CLASS_NAME_INCLUDE_TAG =
		"com.liferay.taglib.util.IncludeTag";

	private static final String _CLASS_NAME_LAYOUT_MODEL_DOCUMENT_CONTRIBUTOR =
		"com.liferay.layout.internal.search.spi.model.index.contributor." +
			"LayoutModelDocumentContributor";

	private static final String _CLASS_NAME_THEME_LOCAL_SERVICE_IMPL =
		"com.liferay.portal.service.impl.ThemeLocalServiceImpl";

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private DataDefinitionResource.Factory _dataDefinitionResourceFactory;

	@Inject
	private DDMFormValuesToFieldsConverter _ddmFormValuesToFieldsConverter;

	private Layout _draftLayout;

	@Inject
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private IndexWriterHelper _indexWriterHelper;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Inject
	private JournalConverter _journalConverter;

	private String _languageId;
	private Layout _layout;
	private IndexerFixture<Layout> _layoutIndexerFixture;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	private Locale _locale;

	@Inject
	private Portal _portal;

	@Inject
	private PortletPreferencesFactory _portletPreferencesFactory;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceContext _serviceContext;

}