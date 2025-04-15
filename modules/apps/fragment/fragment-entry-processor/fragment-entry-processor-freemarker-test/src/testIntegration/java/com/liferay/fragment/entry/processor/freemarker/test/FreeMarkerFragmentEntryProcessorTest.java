/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.entry.processor.freemarker.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.exception.FragmentEntryContentException;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.DefaultFragmentEntryProcessorContext;
import com.liferay.fragment.processor.FragmentEntryProcessorRegistry;
import com.liferay.fragment.service.FragmentCollectionService;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryService;
import com.liferay.item.selector.criteria.InfoListItemSelectorReturnType;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.ThemeLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.hamcrest.CoreMatchers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Rubén Pulido
 */
@RunWith(Arquillian.class)
public class FreeMarkerFragmentEntryProcessorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_company = _companyLocalService.getCompany(_group.getCompanyId());

		_layout = LayoutTestUtil.addTypePortletLayout(_group);
	}

	@Test
	public void testAddFragmentEntryWithFreeMarkerVariable() throws Exception {
		FragmentEntry fragmentEntry = _addFragmentEntry(
			"fragment_entry_with_freemarker_variable.html", null);

		Assert.assertNotNull(fragmentEntry);
	}

	@Test(expected = FragmentEntryContentException.class)
	public void testAddFragmentEntryWithInvalidFreeMarkerVariable()
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"freemarker.runtime", LoggerTestUtil.ERROR)) {

			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId());

			serviceContext.setRequest(_getMockHttpServletRequest());

			FragmentCollection fragmentCollection =
				_fragmentCollectionService.addFragmentCollection(
					null, _group.getGroupId(), "Fragment Collection",
					StringPool.BLANK, serviceContext);

			FragmentEntry draftFragmentEntry =
				_fragmentEntryService.addFragmentEntry(
					null, _group.getGroupId(),
					fragmentCollection.getFragmentCollectionId(),
					"fragment-entry", "Fragment Entry", null,
					_readFileToString(
						"fragment_entry_with_invalid_freemarker_variable.html"),
					null, false, null, null, 0, false, false,
					FragmentConstants.TYPE_COMPONENT, null,
					WorkflowConstants.STATUS_DRAFT, serviceContext);

			ServiceContextThreadLocal.pushServiceContext(serviceContext);

			_fragmentEntryService.publishDraft(draftFragmentEntry);
		}
	}

	@Test
	public void testAddFragmentWithFragmentElementId() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		FragmentCollection fragmentCollection =
			_fragmentCollectionService.addFragmentCollection(
				null, _group.getGroupId(), "Fragment Collection",
				StringPool.BLANK, serviceContext);

		FragmentEntry fragmentEntry = _fragmentEntryService.addFragmentEntry(
			null, _group.getGroupId(),
			fragmentCollection.getFragmentCollectionId(), "fragment-entry",
			"Fragment Entry", null, "${fragmentElementId}", null, false,
			StringPool.BLANK, null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.createFragmentEntryLink(0);

		fragmentEntryLink.setHtml(fragmentEntry.getHtml());

		DefaultFragmentEntryProcessorContext
			defaultFragmentEntryProcessorContext =
				new DefaultFragmentEntryProcessorContext(
					_getMockHttpServletRequest(), new MockHttpServletResponse(),
					null, LocaleUtil.getDefault());

		defaultFragmentEntryProcessorContext.setFragmentElementId("elementId");

		Assert.assertEquals(
			"elementId",
			_getProcessedHTML(
				_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
					fragmentEntryLink, defaultFragmentEntryProcessorContext)));
	}

	@Test
	public void testAddFragmentWithLayoutMode() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		FragmentCollection fragmentCollection =
			_fragmentCollectionService.addFragmentCollection(
				null, _group.getGroupId(), "Fragment Collection",
				StringPool.BLANK, serviceContext);

		FragmentEntry fragmentEntry = _fragmentEntryService.addFragmentEntry(
			null, _group.getGroupId(),
			fragmentCollection.getFragmentCollectionId(), "fragment-entry",
			"Fragment Entry", null, "${layoutMode}", null, false,
			StringPool.BLANK, null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.createFragmentEntryLink(0);

		fragmentEntryLink.setHtml(fragmentEntry.getHtml());

		DefaultFragmentEntryProcessorContext
			defaultFragmentEntryProcessorContext =
				new DefaultFragmentEntryProcessorContext(
					_getMockHttpServletRequest(), new MockHttpServletResponse(),
					null, LocaleUtil.getDefault());

		Assert.assertEquals(
			Constants.VIEW,
			_getProcessedHTML(
				_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
					fragmentEntryLink, defaultFragmentEntryProcessorContext)));

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest();

		mockHttpServletRequest.setParameter("p_l_mode", Constants.EDIT);

		defaultFragmentEntryProcessorContext =
			new DefaultFragmentEntryProcessorContext(
				mockHttpServletRequest, new MockHttpServletResponse(), null,
				LocaleUtil.getDefault());

		Assert.assertEquals(
			Constants.EDIT,
			_getProcessedHTML(
				_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
					fragmentEntryLink, defaultFragmentEntryProcessorContext)));
	}

	@Test
	public void testProcessFragmentEntryLinkHTML() throws Exception {
		FragmentEntry fragmentEntry = _addFragmentEntry(
			"fragment_entry.html", null);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.createFragmentEntryLink(0);

		fragmentEntryLink.setHtml(fragmentEntry.getHtml());

		DefaultFragmentEntryProcessorContext
			defaultFragmentEntryProcessorContext =
				new DefaultFragmentEntryProcessorContext(
					_getMockHttpServletRequest(), new MockHttpServletResponse(),
					null, null);

		String actualProcessedHTML = _getProcessedHTML(
			_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
				fragmentEntryLink, defaultFragmentEntryProcessorContext));

		String expectedProcessedHTML = _getProcessedHTML(
			_readFileToString("expected_processed_fragment_entry.html"));

		Assert.assertEquals(expectedProcessedHTML, actualProcessedHTML);
	}

	@Test
	public void testProcessFragmentEntryLinkHTMLWithConfiguration()
		throws Exception {

		FragmentEntry fragmentEntry = _addFragmentEntry(
			"fragment_entry_with_configuration.html", "configuration.json");

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.createFragmentEntryLink(0);

		fragmentEntryLink.setHtml(fragmentEntry.getHtml());
		fragmentEntryLink.setConfiguration(fragmentEntry.getConfiguration());
		fragmentEntryLink.setEditableValues(
			_readJSONFileToString(
				"fragment_entry_link_editable_values_with_configuration.json"));

		DefaultFragmentEntryProcessorContext
			defaultFragmentEntryProcessorContext =
				new DefaultFragmentEntryProcessorContext(
					_getMockHttpServletRequest(), new MockHttpServletResponse(),
					null, null);

		String actualProcessedHTML = _getProcessedHTML(
			_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
				fragmentEntryLink, defaultFragmentEntryProcessorContext));

		String expectedProcessedHTML = _getProcessedHTML(
			_readFileToString(
				"expected_processed_fragment_entry_with_configuration.html"));

		Assert.assertEquals(expectedProcessedHTML, actualProcessedHTML);
	}

	@Test
	public void testProcessFragmentEntryLinkHTMLWithConfigurationCollectionSelector()
		throws Exception {

		Map<Locale, String> titleMap = HashMapBuilder.put(
			LocaleUtil.US, "t1"
		).build();

		Map<Locale, String> contentMap = HashMapBuilder.put(
			LocaleUtil.US, "c1"
		).build();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), 0,
			PortalUtil.getClassNameId(JournalArticle.class), titleMap, null,
			contentMap, LocaleUtil.getSiteDefault(), false, true,
			serviceContext);

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addDynamicAssetListEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				"Collection Title",
				_getTypeSettings(
					_group.getGroupId(), journalArticle.getClassNameId()),
				serviceContext);

		Map<String, String> editableValuesValues = HashMapBuilder.put(
			"classNameId",
			String.valueOf(
				_portal.getClassNameId(AssetListEntry.class.getName()))
		).put(
			"classPK", String.valueOf(assetListEntry.getAssetListEntryId())
		).put(
			"itemType", assetListEntry.getAssetEntryType()
		).put(
			"title", assetListEntry.getTitle()
		).put(
			"type", InfoListItemSelectorReturnType.class.getName()
		).build();

		FragmentEntry fragmentEntry = _addFragmentEntry(
			"fragment_entry_with_configuration_collectionselector_dynamic_" +
				"collection.html",
			"configuration_collectionselector.json", new HashMap<>());

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.createFragmentEntryLink(0);

		fragmentEntryLink.setHtml(fragmentEntry.getHtml());
		fragmentEntryLink.setConfiguration(fragmentEntry.getConfiguration());
		fragmentEntryLink.setEditableValues(
			_readJSONFileToString(
				"fragment_entry_link_editable_values_with_configuration_" +
					"collectionselector_dynamic_collection.json",
				editableValuesValues));

		DefaultFragmentEntryProcessorContext
			defaultFragmentEntryProcessorContext =
				new DefaultFragmentEntryProcessorContext(
					_getMockHttpServletRequest(), new MockHttpServletResponse(),
					null, null);

		String actualProcessedHTML = _getProcessedHTML(
			_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
				fragmentEntryLink, defaultFragmentEntryProcessorContext));

		String expectedProcessedHTML = _getProcessedHTML(
			_readFileToString(
				"expected_processed_fragment_entry_with_configuration_" +
					"collectionselector_dynamic_collection.html",
				HashMapBuilder.put(
					"title", journalArticle.getTitle()
				).build()));

		Assert.assertEquals(expectedProcessedHTML, actualProcessedHTML);
	}

	@Test
	public void testProcessFragmentEntryLinkHTMLWithConfigurationItemSelectorFileEntry()
		throws Exception {

		String fileName = RandomTestUtil.randomString() + ".jpg";

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, fileName,
			ContentTypes.IMAGE_JPEG,
			FileUtil.getBytes(
				FreeMarkerFragmentEntryProcessorTest.class,
				"dependencies/image.jpg"),
			null, null, null, new ServiceContext());

		FragmentEntry fragmentEntry = _addFragmentEntry(
			"fragment_entry_with_configuration_itemselector_file_entry.html",
			"configuration_itemselector.json",
			HashMapBuilder.put(
				"className", FileEntry.class.getName()
			).put(
				"classNameId",
				String.valueOf(
					_classNameLocalService.getClassNameId(
						fileEntry.getModelClassName()))
			).put(
				"classPK", String.valueOf(fileEntry.getPrimaryKey())
			).build());

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.createFragmentEntryLink(0);

		fragmentEntryLink.setHtml(fragmentEntry.getHtml());
		fragmentEntryLink.setConfiguration(fragmentEntry.getConfiguration());
		fragmentEntryLink.setEditableValues(
			_readJSONFileToString(
				"fragment_entry_link_editable_values_with_configuration_" +
					"itemselector.json"));

		DefaultFragmentEntryProcessorContext
			defaultFragmentEntryProcessorContext =
				new DefaultFragmentEntryProcessorContext(
					_getMockHttpServletRequest(), new MockHttpServletResponse(),
					null, null);

		String actualProcessedHTML = _getProcessedHTML(
			_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
				fragmentEntryLink, defaultFragmentEntryProcessorContext));

		String expectedProcessedHTML = _getProcessedHTML(
			_readFileToString(
				"expected_processed_fragment_entry_with_configuration_" +
					"itemselector_file_entry.html",
				HashMapBuilder.put(
					"className", FileEntry.class.getName()
				).put(
					"classNameId",
					String.valueOf(
						_classNameLocalService.getClassNameId(
							fileEntry.getModelClassName()))
				).put(
					"classPK", String.valueOf(fileEntry.getPrimaryKey())
				).put(
					"fileName", fileName
				).build()));

		Assert.assertEquals(expectedProcessedHTML, actualProcessedHTML);
	}

	@Test
	public void testProcessFragmentEntryLinkHTMLWithConfigurationItemSelectorJournalArticle()
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), 0,
			PortalUtil.getClassNameId(JournalArticle.class),
			HashMapBuilder.put(
				LocaleUtil.SPAIN, "t1-es"
			).put(
				LocaleUtil.US, "t1"
			).build(),
			null,
			HashMapBuilder.put(
				LocaleUtil.SPAIN, "c1-es"
			).put(
				LocaleUtil.US, "c1"
			).build(),
			LocaleUtil.getSiteDefault(), false, true,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		FragmentEntry fragmentEntry = _addFragmentEntry(
			"fragment_entry_with_configuration_itemselector_journal_article." +
				"html",
			"configuration_itemselector.json",
			HashMapBuilder.put(
				"className", journalArticle.getModelClassName()
			).put(
				"classNameId", String.valueOf(journalArticle.getClassNameId())
			).put(
				"classPK", String.valueOf(journalArticle.getResourcePrimKey())
			).build());

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.createFragmentEntryLink(0);

		fragmentEntryLink.setHtml(fragmentEntry.getHtml());
		fragmentEntryLink.setConfiguration(fragmentEntry.getConfiguration());
		fragmentEntryLink.setEditableValues(
			_readJSONFileToString(
				"fragment_entry_link_editable_values_with_configuration_" +
					"itemselector.json"));

		DefaultFragmentEntryProcessorContext
			defaultFragmentEntryProcessorContext =
				new DefaultFragmentEntryProcessorContext(
					_getMockHttpServletRequest(), new MockHttpServletResponse(),
					null, null);

		String actualProcessedHTML = _getProcessedHTML(
			_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
				fragmentEntryLink, defaultFragmentEntryProcessorContext));

		journalArticle = _journalArticleLocalService.getArticle(
			journalArticle.getId());

		String expectedProcessedHTML = _getProcessedHTML(
			_readFileToString(
				"expected_processed_fragment_entry_with_configuration_" +
					"itemselector_journal_article.html",
				HashMapBuilder.put(
					"classNameId",
					String.valueOf(journalArticle.getClassNameId())
				).put(
					"classPK",
					String.valueOf(journalArticle.getResourcePrimKey())
				).put(
					"contentES", journalArticle.getContentByLocale("es_ES")
				).put(
					"contentUS", journalArticle.getContentByLocale("en_US")
				).put(
					"titleES", "t1-es"
				).put(
					"titleUS", "t1"
				).build()));

		Assert.assertEquals(expectedProcessedHTML, actualProcessedHTML);
	}

	@Test
	public void testProcessFragmentEntryLinkHTMLWithConfigurationLocalizable()
		throws Exception {

		FragmentEntry fragmentEntry = _addFragmentEntry(
			"fragment_entry_with_configuration_localizable.html",
			"configuration_localizable.json");

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.createFragmentEntryLink(0);

		fragmentEntryLink.setHtml(fragmentEntry.getHtml());
		fragmentEntryLink.setConfiguration(fragmentEntry.getConfiguration());
		fragmentEntryLink.setEditableValues(
			_readJSONFileToString(
				"fragment_entry_link_editable_values_with_configuration_" +
					"localizable.json"));

		DefaultFragmentEntryProcessorContext
			defaultFragmentEntryProcessorContext =
				new DefaultFragmentEntryProcessorContext(
					_getMockHttpServletRequest(), new MockHttpServletResponse(),
					Constants.VIEW, LocaleUtil.fromLanguageId("en_US"));

		String actualProcessedHTML = _getProcessedHTML(
			_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
				fragmentEntryLink, defaultFragmentEntryProcessorContext));

		Assert.assertThat(
			actualProcessedHTML, CoreMatchers.containsString("Style - dark"));

		defaultFragmentEntryProcessorContext =
			new DefaultFragmentEntryProcessorContext(
				_getMockHttpServletRequest(), new MockHttpServletResponse(),
				Constants.VIEW, LocaleUtil.fromLanguageId("es_ES"));

		actualProcessedHTML = _getProcessedHTML(
			_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
				fragmentEntryLink, defaultFragmentEntryProcessorContext));

		Assert.assertThat(
			actualProcessedHTML, CoreMatchers.containsString("Style - light"));

		defaultFragmentEntryProcessorContext =
			new DefaultFragmentEntryProcessorContext(
				_getMockHttpServletRequest(), new MockHttpServletResponse(),
				Constants.VIEW, LocaleUtil.fromLanguageId("fr_FR"));

		actualProcessedHTML = _getProcessedHTML(
			_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
				fragmentEntryLink, defaultFragmentEntryProcessorContext));

		Assert.assertThat(
			actualProcessedHTML, CoreMatchers.containsString("Style - dark"));
	}

	@Test
	public void testProcessFragmentEntryLinkHTMLWithInvalidFreeMarker()
		throws Exception {

		FragmentEntry fragmentEntry = _addFragmentEntry(
			"fragment_entry_invalid_freemarker.html", null);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.createFragmentEntryLink(0);

		fragmentEntryLink.setHtml(fragmentEntry.getHtml());

		DefaultFragmentEntryProcessorContext
			defaultFragmentEntryProcessorContext =
				new DefaultFragmentEntryProcessorContext(
					_getMockHttpServletRequest(), new MockHttpServletResponse(),
					null, LocaleUtil.getDefault());

		expectedException.expect(FragmentEntryContentException.class);
		expectedException.expectMessage("FreeMarker syntax is invalid");
		expectedException.expectMessage(
			"Syntax error in template \"template_id\" in line 5, column 12");

		_getProcessedHTML(
			_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
				fragmentEntryLink, defaultFragmentEntryProcessorContext));
	}

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private FragmentEntry _addFragmentEntry(
			String htmlFile, String configurationFile)
		throws Exception {

		return _addFragmentEntry(htmlFile, configurationFile, null);
	}

	private FragmentEntry _addFragmentEntry(
			String htmlFile, String configurationFile,
			Map<String, String> values)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		FragmentCollection fragmentCollection =
			_fragmentCollectionService.addFragmentCollection(
				null, _group.getGroupId(), "Fragment Collection",
				StringPool.BLANK, serviceContext);

		String configuration = null;

		if (configurationFile != null) {
			configuration = _readFileToString(configurationFile);

			configuration = StringUtil.replace(
				configuration, "${", "}", values);
		}

		return _fragmentEntryService.addFragmentEntry(
			null, _group.getGroupId(),
			fragmentCollection.getFragmentCollectionId(), "fragment-entry",
			"Fragment Entry", null, _readFileToString(htmlFile), null, false,
			configuration, null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

	private MockHttpServletRequest _getMockHttpServletRequest()
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(mockHttpServletRequest));

		return mockHttpServletRequest;
	}

	private String _getProcessedHTML(String bodyHtml) {
		String processedHTML = bodyHtml;

		for (int i = 1; i <= 2; i++) {
			Document document = Jsoup.parseBodyFragment(processedHTML);

			document.outputSettings(
				new Document.OutputSettings() {
					{
						prettyPrint(true);
					}
				});

			Element bodyElement = document.body();

			processedHTML = bodyElement.html();
		}

		return processedHTML;
	}

	private ThemeDisplay _getThemeDisplay(HttpServletRequest httpServletRequest)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setLayout(_layout);

		LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
			_group.getGroupId(), false);

		themeDisplay.setLookAndFeel(
			_themeLocalService.getTheme(
				_company.getCompanyId(), layoutSet.getThemeId()),
			null);

		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setRequest(httpServletRequest);
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private String _getTypeSettings(long groupId, long classNameId) {
		return UnicodePropertiesBuilder.create(
			true
		).put(
			"anyAssetType", String.valueOf(classNameId)
		).put(
			"anyClassTypeDLFileEntryAssetRendererFactory", "true"
		).put(
			"anyClassTypeJournalArticleAssetRendererFactory", "true"
		).put(
			"classNameIds", String.valueOf(classNameId)
		).put(
			"groupIds", String.valueOf(groupId)
		).put(
			"orderByColumn1", "modifiedDate"
		).put(
			"orderByColumn2", "title"
		).put(
			"orderByType1", "DESC"
		).put(
			"orderByType2", "ASC"
		).put(
			"subtypeFieldsFilterEnabledDLFileEntryAssetRendererFactory", "false"
		).put(
			"subtypeFieldsFilterEnabledJournalArticleAssetRendererFactory",
			"false"
		).buildString();
	}

	private String _readFileToString(String fileName) throws Exception {
		return _readFileToString(fileName, null);
	}

	private String _readFileToString(
			String fileName, Map<String, String> values)
		throws Exception {

		Class<?> clazz = getClass();

		String template = StringUtil.read(
			clazz.getClassLoader(),
			"com/liferay/fragment/entry/processor/freemarker/test" +
				"/dependencies/" + fileName);

		return StringUtil.replace(template, "${", "}", values);
	}

	private String _readJSONFileToString(String jsonFileName) throws Exception {
		return _readJSONFileToString(jsonFileName, null);
	}

	private String _readJSONFileToString(
			String jsonFileName, Map<String, String> values)
		throws Exception {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			_readFileToString(jsonFileName, values));

		return jsonObject.toString();
	}

	@Inject
	private AssetListEntryLocalService _assetListEntryLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private FragmentCollectionService _fragmentCollectionService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryProcessorRegistry _fragmentEntryProcessorRegistry;

	@Inject
	private FragmentEntryService _fragmentEntryService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@DeleteAfterTestRun
	private Layout _layout;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private ThemeLocalService _themeLocalService;

}