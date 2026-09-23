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
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.exception.FragmentEntryContentException;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.DefaultFragmentEntryProcessorContext;
import com.liferay.fragment.processor.FragmentEntryProcessorContext;
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
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.ThemeLocalService;
import com.liferay.portal.kernel.test.TestInfo;
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
import java.util.Map;

import org.hamcrest.CoreMatchers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.junit.After;
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

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), TestPropsValues.getUserId());

		_serviceContext.setRequest(_getMockHttpServletRequest());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);

		_fragmentCollection = _fragmentCollectionService.addFragmentCollection(
			null, _group.getGroupId(), "Fragment Collection", StringPool.BLANK,
			_serviceContext);

		_layout = LayoutTestUtil.addTypePortletLayout(_group);
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testAddFragmentEntryWithFragmentElementId() throws Exception {
		FragmentEntry fragmentEntry = _fragmentEntryService.addFragmentEntry(
			null, _group.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(), "fragment-entry",
			"Fragment Entry", null, "${fragmentElementId}", null, false,
			StringPool.BLANK, null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, _serviceContext);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.createFragmentEntryLink(0);

		fragmentEntryLink.setHtml(fragmentEntry.getHtml());

		DefaultFragmentEntryProcessorContext
			defaultFragmentEntryProcessorContext =
				new DefaultFragmentEntryProcessorContext(
					_serviceContext.getCompanyId(),
					_serviceContext.getRequest(), new MockHttpServletResponse(),
					LocaleUtil.getDefault(), null,
					_serviceContext.getScopeGroupId());

		defaultFragmentEntryProcessorContext.setFragmentElementId("elementId");

		Assert.assertEquals(
			"elementId",
			_getProcessedHTML(
				_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
					fragmentEntryLink, defaultFragmentEntryProcessorContext)));
	}

	@Test
	@TestInfo("LPD-57548")
	public void testAddFragmentEntryWithFragmentName() throws Exception {
		FragmentEntry fragmentEntry = _fragmentEntryService.addFragmentEntry(
			null, _group.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(), "fragment-entry",
			"Fragment Entry", null, "${fragmentName}", null, false,
			StringPool.BLANK, null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, _serviceContext);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.createFragmentEntryLink(0);

		fragmentEntryLink.setGroupId(fragmentEntry.getGroupId());
		fragmentEntryLink.setFragmentEntryERC(
			fragmentEntry.getExternalReferenceCode());
		fragmentEntryLink.setHtml(fragmentEntry.getHtml());

		Assert.assertEquals(
			"Fragment Entry",
			_getProcessedHTML(
				_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
					fragmentEntryLink,
					new DefaultFragmentEntryProcessorContext(
						_serviceContext.getCompanyId(),
						_serviceContext.getRequest(),
						new MockHttpServletResponse(), LocaleUtil.getDefault(),
						null, _serviceContext.getScopeGroupId()))));
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

			FragmentEntry draftFragmentEntry =
				_fragmentEntryService.addFragmentEntry(
					null, _group.getGroupId(),
					_fragmentCollection.getFragmentCollectionId(),
					"fragment-entry", "Fragment Entry", null,
					_readFileToString(
						"fragment_entry_with_invalid_freemarker_variable.html"),
					null, false, null, null, 0, false, false,
					FragmentConstants.TYPE_COMPONENT, null,
					WorkflowConstants.STATUS_DRAFT, _serviceContext);

			ServiceContextThreadLocal.pushServiceContext(_serviceContext);

			_fragmentEntryService.publishDraft(draftFragmentEntry);
		}
	}

	@Test
	public void testAddFragmentEntryWithLayoutMode() throws Exception {
		FragmentEntry fragmentEntry = _fragmentEntryService.addFragmentEntry(
			null, _group.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(), "fragment-entry",
			"Fragment Entry", null, "${layoutMode}", null, false,
			StringPool.BLANK, null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, _serviceContext);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.createFragmentEntryLink(0);

		fragmentEntryLink.setHtml(fragmentEntry.getHtml());

		Assert.assertEquals(
			Constants.VIEW,
			_getProcessedHTML(
				_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
					fragmentEntryLink,
					new DefaultFragmentEntryProcessorContext(
						_serviceContext.getCompanyId(),
						_serviceContext.getRequest(),
						new MockHttpServletResponse(), LocaleUtil.getDefault(),
						null, _serviceContext.getScopeGroupId()))));

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest();

		mockHttpServletRequest.setParameter("p_l_mode", Constants.EDIT);

		Assert.assertEquals(
			Constants.EDIT,
			_getProcessedHTML(
				_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
					fragmentEntryLink,
					new DefaultFragmentEntryProcessorContext(
						_serviceContext.getCompanyId(), mockHttpServletRequest,
						new MockHttpServletResponse(), LocaleUtil.getDefault(),
						null, _serviceContext.getScopeGroupId()))));
	}

	@Test
	@TestInfo("LPD-93455")
	public void testAddFragmentEntryWithRESTClientDataDuringImport()
		throws Exception {

		ExportImportThreadLocal.setLayoutImportInProcess(true);

		try {
			Assert.assertNotNull(
				_addFragmentEntry(
					"fragment_entry_with_rest_client_data.html", null));
		}
		finally {
			ExportImportThreadLocal.setLayoutImportInProcess(false);
		}
	}

	@Test
	public void testProcessFragmentEntryLinkHTML() throws Exception {
		FragmentEntry fragmentEntry = _addFragmentEntry(
			"fragment_entry.html", null);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.createFragmentEntryLink(0);

		fragmentEntryLink.setHtml(fragmentEntry.getHtml());

		Assert.assertEquals(
			_getProcessedHTML(
				_readFileToString("expected_processed_fragment_entry.html")),
			_getProcessedHTML(
				_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
					fragmentEntryLink,
					new DefaultFragmentEntryProcessorContext(
						_serviceContext.getCompanyId(),
						_serviceContext.getRequest(),
						new MockHttpServletResponse(), null, null,
						_serviceContext.getScopeGroupId()))));
	}

	@Test
	public void testProcessFragmentEntryLinkHTMLWithConfiguration()
		throws Exception {

		Assert.assertEquals(
			_getProcessedHTML(
				_readFileToString(
					"expected_processed_fragment_entry_with_configuration." +
						"html")),
			_getProcessedHTML(
				_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
					_getFragmentEntryLink(
						"configuration.json",
						"fragment_entry_link_editable_values_with_" +
							"configuration.json",
						"fragment_entry_with_configuration.html",
						new HashMap<>()),
					new DefaultFragmentEntryProcessorContext(
						_serviceContext.getCompanyId(),
						_serviceContext.getRequest(),
						new MockHttpServletResponse(), null, null,
						_serviceContext.getScopeGroupId()))));
	}

	@Test
	public void testProcessFragmentEntryLinkHTMLWithConfigurationCollectionSelector()
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), 0,
			PortalUtil.getClassNameId(JournalArticle.class),
			HashMapBuilder.put(
				LocaleUtil.US, "t1"
			).build(),
			null,
			HashMapBuilder.put(
				LocaleUtil.US, "c1"
			).build(),
			LocaleUtil.getSiteDefault(), false, true, _serviceContext);

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addDynamicAssetListEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				"Collection Title",
				_getTypeSettings(
					_group.getGroupId(), journalArticle.getClassNameId()),
				_serviceContext);

		Assert.assertEquals(
			_getProcessedHTML(
				_readFileToString(
					"expected_processed_fragment_entry_with_configuration_" +
						"collectionselector_dynamic_collection.html",
					HashMapBuilder.put(
						"title", journalArticle.getTitle()
					).build())),
			_getProcessedHTML(
				_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
					_getFragmentEntryLink(
						"configuration_collectionselector.json",
						"fragment_entry_link_editable_values_with_" +
							"configuration_collectionselector_dynamic_" +
								"collection.json",
						HashMapBuilder.put(
							"classNameId",
							String.valueOf(
								_portal.getClassNameId(
									AssetListEntry.class.getName()))
						).put(
							"classPK",
							String.valueOf(assetListEntry.getAssetListEntryId())
						).put(
							"itemType", assetListEntry.getAssetEntryType()
						).put(
							"title", assetListEntry.getTitle()
						).put(
							"type",
							InfoListItemSelectorReturnType.class.getName()
						).build(),
						"fragment_entry_with_configuration_" +
							"collectionselector_dynamic_collection.html",
						new HashMap<>()),
					new DefaultFragmentEntryProcessorContext(
						_serviceContext.getCompanyId(),
						_serviceContext.getRequest(),
						new MockHttpServletResponse(), null, null,
						_serviceContext.getScopeGroupId()))));
	}

	@Test
	@TestInfo("LPD-70061")
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

		FragmentEntryLink fragmentEntryLink = _getFragmentEntryLink(
			"configuration_itemselector.json",
			"fragment_entry_link_editable_values_with_configuration_" +
				"itemselector.json",
			"fragment_entry_with_configuration_itemselector_file_entry.html",
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

		DefaultFragmentEntryProcessorContext
			defaultFragmentEntryProcessorContext =
				new DefaultFragmentEntryProcessorContext(
					_serviceContext.getCompanyId(),
					_serviceContext.getRequest(), new MockHttpServletResponse(),
					null, null, _serviceContext.getScopeGroupId());

		Assert.assertEquals(
			_getProcessedHTML(
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
					).build())),
			_getProcessedHTML(
				_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
					fragmentEntryLink, defaultFragmentEntryProcessorContext)));

		fragmentEntryLink.setHtml(
			"<div class=\"fragment_name\">[#if itemSelector1Object??]" +
				"${itemSelector1Object}[/#if]</div>");

		_assertContainsFileEntryInfo(
			_getProcessedHTML(
				fragmentEntryLink,
				JSONUtil.put(
					"itemSelector1",
					JSONUtil.put(
						"className", FileEntry.class.getName()
					).put(
						"classPK", fileEntry.getFileEntryId()
					)),
				defaultFragmentEntryProcessorContext),
			fileEntry);

		_assertContainsFileEntryInfo(
			_getProcessedHTML(
				fragmentEntryLink,
				JSONUtil.put(
					"itemSelector1",
					JSONUtil.put(
						"className", FileEntry.class.getName()
					).put(
						"externalReferenceCode",
						fileEntry.getExternalReferenceCode()
					)),
				defaultFragmentEntryProcessorContext),
			fileEntry);

		Group group = _groupLocalService.getGroup(TestPropsValues.getGroupId());

		fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, fileName,
			ContentTypes.IMAGE_JPEG,
			FileUtil.getBytes(
				FreeMarkerFragmentEntryProcessorTest.class,
				"dependencies/image.jpg"),
			null, null, null, new ServiceContext());

		_assertContainsFileEntryInfo(
			_getProcessedHTML(
				fragmentEntryLink,
				JSONUtil.put(
					"itemSelector1",
					JSONUtil.put(
						"className", FileEntry.class.getName()
					).put(
						"externalReferenceCode",
						fileEntry.getExternalReferenceCode()
					).put(
						"scopeExternalReferenceCode",
						group.getExternalReferenceCode()
					)),
				defaultFragmentEntryProcessorContext),
			fileEntry);
	}

	@Test
	@TestInfo("LPD-70061")
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
			LocaleUtil.getSiteDefault(), false, true, _serviceContext);

		FragmentEntryLink fragmentEntryLink = _getFragmentEntryLink(
			"configuration_itemselector.json",
			"fragment_entry_link_editable_values_with_configuration_" +
				"itemselector.json",
			"fragment_entry_with_configuration_itemselector_journal_article." +
				"html",
			HashMapBuilder.put(
				"className", journalArticle.getModelClassName()
			).put(
				"classNameId", String.valueOf(journalArticle.getClassNameId())
			).put(
				"classPK", String.valueOf(journalArticle.getResourcePrimKey())
			).build());

		DefaultFragmentEntryProcessorContext
			defaultFragmentEntryProcessorContext =
				new DefaultFragmentEntryProcessorContext(
					_serviceContext.getCompanyId(),
					_serviceContext.getRequest(), new MockHttpServletResponse(),
					null, null, _serviceContext.getScopeGroupId());

		journalArticle = _journalArticleLocalService.getArticle(
			journalArticle.getId());

		Assert.assertEquals(
			_getProcessedHTML(
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
					).build())),
			_getProcessedHTML(
				_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
					fragmentEntryLink, defaultFragmentEntryProcessorContext)));

		fragmentEntryLink.setHtml(
			"<div class=\"fragment_name\">[#if itemSelector1Object??]" +
				"${itemSelector1Object}[/#if]</div>");

		_assertContainsJournalArticleInfo(
			_getProcessedHTML(
				fragmentEntryLink,
				JSONUtil.put(
					"itemSelector1",
					JSONUtil.put(
						"className", JournalArticle.class.getName()
					).put(
						"classPK",
						String.valueOf(journalArticle.getResourcePrimKey())
					)),
				defaultFragmentEntryProcessorContext),
			journalArticle);

		_assertContainsJournalArticleInfo(
			_getProcessedHTML(
				fragmentEntryLink,
				JSONUtil.put(
					"itemSelector1",
					JSONUtil.put(
						"className", JournalArticle.class.getName()
					).put(
						"externalReferenceCode",
						journalArticle.getExternalReferenceCode()
					)),
				defaultFragmentEntryProcessorContext),
			journalArticle);

		Group group = _groupLocalService.getGroup(TestPropsValues.getGroupId());

		journalArticle = JournalTestUtil.addArticle(
			group.getGroupId(), 0,
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
			LocaleUtil.getSiteDefault(), false, true, _serviceContext);

		_assertContainsJournalArticleInfo(
			_getProcessedHTML(
				fragmentEntryLink,
				JSONUtil.put(
					"itemSelector1",
					JSONUtil.put(
						"className", JournalArticle.class.getName()
					).put(
						"externalReferenceCode",
						journalArticle.getExternalReferenceCode()
					).put(
						"scopeExternalReferenceCode",
						group.getExternalReferenceCode()
					)),
				defaultFragmentEntryProcessorContext),
			journalArticle);
	}

	@Test
	public void testProcessFragmentEntryLinkHTMLWithConfigurationLocalizable()
		throws Exception {

		FragmentEntryLink fragmentEntryLink = _getFragmentEntryLink(
			"configuration_localizable.json",
			"fragment_entry_link_editable_values_with_configuration_" +
				"localizable.json",
			"fragment_entry_with_configuration_localizable.html", null);

		Assert.assertThat(
			_getProcessedHTML(
				_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
					fragmentEntryLink,
					new DefaultFragmentEntryProcessorContext(
						_serviceContext.getCompanyId(),
						_serviceContext.getRequest(),
						new MockHttpServletResponse(),
						LocaleUtil.fromLanguageId("en_US"), Constants.VIEW,
						_serviceContext.getScopeGroupId()))),
			CoreMatchers.containsString("Style - dark"));
		Assert.assertThat(
			_getProcessedHTML(
				_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
					fragmentEntryLink,
					new DefaultFragmentEntryProcessorContext(
						_serviceContext.getCompanyId(),
						_serviceContext.getRequest(),
						new MockHttpServletResponse(),
						LocaleUtil.fromLanguageId("es_ES"), Constants.VIEW,
						_serviceContext.getScopeGroupId()))),
			CoreMatchers.containsString("Style - light"));
		Assert.assertThat(
			_getProcessedHTML(
				_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
					fragmentEntryLink,
					new DefaultFragmentEntryProcessorContext(
						_serviceContext.getCompanyId(),
						_serviceContext.getRequest(),
						new MockHttpServletResponse(),
						LocaleUtil.fromLanguageId("fr_FR"), Constants.VIEW,
						_serviceContext.getScopeGroupId()))),
			CoreMatchers.containsString("Style - dark"));
	}

	@Test
	public void testProcessFragmentEntryLinkHTMLWithContextInfoItem()
		throws Exception {

		JournalArticle journalArticle1 = JournalTestUtil.addArticle(
			_group.getGroupId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		FragmentEntryLink fragmentEntryLink = _getFragmentEntryLink(
			"configuration_itemselector.json",
			"fragment_entry_link_editable_values_with_configuration_" +
				"itemselector.json",
			"fragment_entry_with_configuration_itemselector_journal_article." +
				"html",
			HashMapBuilder.put(
				"className", journalArticle1.getModelClassName()
			).put(
				"classNameId", String.valueOf(journalArticle1.getClassNameId())
			).put(
				"classPK", String.valueOf(journalArticle1.getResourcePrimKey())
			).build());

		fragmentEntryLink.setHtml(
			"<div class=\"fragment_name\">[#if itemSelector1Object??]" +
				"${itemSelector1Object}[/#if]</div>");

		JSONObject jsonObject = JSONUtil.put(
			"itemSelector1",
			JSONUtil.put(
				"className", JournalArticle.class.getName()
			).put(
				"classPK", String.valueOf(journalArticle1.getResourcePrimKey())
			));

		DefaultFragmentEntryProcessorContext
			defaultFragmentEntryProcessorContext =
				new DefaultFragmentEntryProcessorContext(
					_serviceContext.getCompanyId(),
					_serviceContext.getRequest(), new MockHttpServletResponse(),
					null, null, _serviceContext.getScopeGroupId());

		JournalArticle journalArticle2 = JournalTestUtil.addArticle(
			_group.getGroupId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		defaultFragmentEntryProcessorContext.setContextInfoItem(
			journalArticle2);

		_assertContainsJournalArticleInfo(
			_getProcessedHTML(
				fragmentEntryLink, jsonObject,
				defaultFragmentEntryProcessorContext),
			journalArticle2);

		defaultFragmentEntryProcessorContext.setContextInfoItem(null);

		_assertContainsJournalArticleInfo(
			_getProcessedHTML(
				fragmentEntryLink, jsonObject,
				defaultFragmentEntryProcessorContext),
			journalArticle1);
	}

	@Test
	public void testProcessFragmentEntryLinkHTMLWithInvalidFreeMarker()
		throws Exception {

		expectedException.expect(FragmentEntryContentException.class);
		expectedException.expectMessage("FreeMarker syntax is invalid");
		expectedException.expectMessage(
			"Syntax error in template \"template_id\" in line 5, column 12");

		FragmentEntry fragmentEntry = _addFragmentEntry(
			"fragment_entry_invalid_freemarker.html", null);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.createFragmentEntryLink(0);

		fragmentEntryLink.setHtml(fragmentEntry.getHtml());

		_getProcessedHTML(
			_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
				fragmentEntryLink,
				new DefaultFragmentEntryProcessorContext(
					_serviceContext.getCompanyId(),
					_serviceContext.getRequest(), new MockHttpServletResponse(),
					LocaleUtil.getDefault(), null,
					_serviceContext.getScopeGroupId())));
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

		String configuration = null;

		if (configurationFile != null) {
			configuration = _readFileToString(configurationFile);

			configuration = StringUtil.replace(
				configuration, "${", "}", values);
		}

		return _fragmentEntryService.addFragmentEntry(
			null, _group.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(), "fragment-entry",
			"Fragment Entry", null, _readFileToString(htmlFile), null, false,
			configuration, null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, _serviceContext);
	}

	private void _assertContainsFileEntryInfo(
		String html, FileEntry fileEntry) {

		Assert.assertTrue(
			html.contains(
				"\"externalReferenceCode\": \"" +
					fileEntry.getExternalReferenceCode()));
		Assert.assertTrue(
			html.contains("\"fileEntryId\": " + fileEntry.getFileEntryId()));
		Assert.assertTrue(
			html.contains("\"title\": \"" + fileEntry.getFileName()));
	}

	private void _assertContainsJournalArticleInfo(
		String html, JournalArticle journalArticle) {

		Assert.assertTrue(
			html.contains(
				"\"externalReferenceCode\": \"" +
					journalArticle.getExternalReferenceCode()));
		Assert.assertTrue(
			html.contains(
				"\"resourcePrimKey\": " + journalArticle.getResourcePrimKey()));
		Assert.assertTrue(
			html.contains("\"urlTitle\": \"" + journalArticle.getUrlTitle()));
	}

	private FragmentEntryLink _getFragmentEntryLink(
			String configurationFile, String editableValuesFile,
			Map<String, String> editableValuesMap, String htmlFile,
			Map<String, String> values)
		throws Exception {

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.createFragmentEntryLink(0);

		FragmentEntry fragmentEntry = _addFragmentEntry(
			htmlFile, configurationFile, values);

		fragmentEntryLink.setHtml(fragmentEntry.getHtml());
		fragmentEntryLink.setConfiguration(fragmentEntry.getConfiguration());

		fragmentEntryLink.setEditableValues(
			_readJSONFileToString(editableValuesFile, editableValuesMap));

		return fragmentEntryLink;
	}

	private FragmentEntryLink _getFragmentEntryLink(
			String configurationFile, String editableValuesFile,
			String htmlFile, Map<String, String> values)
		throws Exception {

		return _getFragmentEntryLink(
			configurationFile, editableValuesFile, null, htmlFile, values);
	}

	private MockHttpServletRequest _getMockHttpServletRequest()
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(mockHttpServletRequest));

		return mockHttpServletRequest;
	}

	private String _getProcessedHTML(
			FragmentEntryLink fragmentEntryLink, JSONObject jsonObject,
			FragmentEntryProcessorContext fragmentEntryProcessorContext)
		throws Exception {

		fragmentEntryLink.setEditableValues(
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				jsonObject
			).toString());

		return _getProcessedHTML(
			_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
				fragmentEntryLink, fragmentEntryProcessorContext));
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
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
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

	private FragmentCollection _fragmentCollection;

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
	private GroupLocalService _groupLocalService;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@DeleteAfterTestRun
	private Layout _layout;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	@Inject
	private Portal _portal;

	private ServiceContext _serviceContext;

	@Inject
	private ThemeLocalService _themeLocalService;

}