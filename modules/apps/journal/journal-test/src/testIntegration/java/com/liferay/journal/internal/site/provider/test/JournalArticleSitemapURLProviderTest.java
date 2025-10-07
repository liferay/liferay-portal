/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.site.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.seo.service.LayoutSEOEntryLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReader;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.provider.SitemapURLProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class JournalArticleSitemapURLProviderTest {

	@ClassRule
	@Rule
	public static AggregateTestRule aggregateTestRule = new AggregateTestRule(
		new LiferayIntegrationTestRule(),
		PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layoutSet = _layoutSetLocalService.getLayoutSet(
			_group.getGroupId(), false);

		_initThemeDisplay();

		LayoutTestUtil.addTypePortletLayout(_group);
	}

	@Test
	public void testJournalArticleSitemapURLProviderDefaultDisplayPage()
		throws Exception {

		Element rootElement = _getRootElement();

		JournalArticle article = JournalTestUtil.addArticleWithWorkflow(
			_group.getGroupId(), true);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(),
				_portal.getClassNameId(JournalArticle.class.getName()),
				article.getDDMStructureId(), true,
				WorkflowConstants.STATUS_APPROVED);

		_assertRootElement(
			article,
			_layoutLocalService.getLayout(layoutPageTemplateEntry.getPlid()),
			rootElement,
			FriendlyURLResolverConstants.URL_SEPARATOR_JOURNAL_ARTICLE);
	}

	@Test
	public void testJournalArticleSitemapURLProviderDefaultDisplayPageCanonicalURLEnabled()
		throws Exception {

		Element rootElement = _getRootElement();

		JournalArticle article = JournalTestUtil.addArticleWithWorkflow(
			_group.getGroupId(), true);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(),
				_portal.getClassNameId(JournalArticle.class.getName()),
				article.getDDMStructureId(), true,
				WorkflowConstants.STATUS_APPROVED);

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		_layoutSEOEntryLocalService.updateLayoutSEOEntry(
			TestPropsValues.getUserId(), _group.getGroupId(),
			layout.isPrivateLayout(), layout.getLayoutId(), true,
			new HashMap<>(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_journalArticleSitemapURLProvider.visitLayout(
			rootElement, layout.getUuid(), _layoutSet, _themeDisplay);

		Assert.assertFalse(rootElement.hasContent());
	}

	@Test
	public void testJournalArticleSitemapURLProviderDefaultDisplayPageRobotsWithNofollow()
		throws Exception {

		_assertVisitLayoutDefaultDisplayPage("nofollow");
	}

	@Test
	public void testJournalArticleSitemapURLProviderDefaultDisplayPageRobotsWithNoindex()
		throws Exception {

		_assertVisitLayoutDefaultDisplayPage("noindex");
	}

	@Test
	public void testJournalArticleSitemapURLProviderDefaultDisplayPageWhenArticleInGlobal()
		throws Exception {

		JournalArticle article = null;

		try {
			Company company = _companyLocalService.getCompany(
				_group.getCompanyId());

			article = JournalTestUtil.addArticleWithWorkflow(
				company.getGroupId(), true);

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				DisplayPageTemplateTestUtil.addDisplayPageTemplate(
					_group.getGroupId(),
					_portal.getClassNameId(JournalArticle.class.getName()),
					article.getDDMStructureId(), true,
					WorkflowConstants.STATUS_APPROVED);

			_assertRootElement(
				article,
				_layoutLocalService.getLayout(
					layoutPageTemplateEntry.getPlid()),
				_getRootElement(),
				FriendlyURLResolverConstants.URL_SEPARATOR_JOURNAL_ARTICLE);
		}
		finally {
			_journalArticleLocalService.deleteArticle(article);
		}
	}

	@Test
	public void testJournalArticleSitemapURLProviderDefaultDisplayPageWhenJournalArticleInConnectedDepotEntry()
		throws Exception {

		DepotEntry depotEntry = null;

		try {
			depotEntry = _depotEntryLocalService.addDepotEntry(
				HashMapBuilder.put(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()
				).build(),
				HashMapBuilder.put(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()
				).build(),
				DepotConstants.TYPE_ASSET_LIBRARY,
				ServiceContextTestUtil.getServiceContext());

			JournalArticle article = JournalTestUtil.addArticleWithWorkflow(
				depotEntry.getGroupId(), true);

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				DisplayPageTemplateTestUtil.addDisplayPageTemplate(
					_group.getGroupId(),
					_portal.getClassNameId(JournalArticle.class.getName()),
					article.getDDMStructureId(), true,
					WorkflowConstants.STATUS_APPROVED);

			_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
				depotEntry.getDepotEntryId(), _group.getGroupId());

			_assertRootElement(
				article,
				_layoutLocalService.getLayout(
					layoutPageTemplateEntry.getPlid()),
				_getRootElement(),
				FriendlyURLResolverConstants.URL_SEPARATOR_JOURNAL_ARTICLE);
		}
		finally {
			_depotEntryLocalService.deleteDepotEntry(
				depotEntry.getDepotEntryId());
		}
	}

	@Test
	public void testJournalArticleSitemapURLProviderDefaultLayout()
		throws Exception {

		Element rootElement = _getRootElement();

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId());

		JournalArticle article = JournalTestUtil.addArticleWithWorkflow(
			_group.getGroupId(), true);

		article = _journalArticleLocalService.updateArticle(
			article.getUserId(), article.getGroupId(), article.getFolderId(),
			article.getArticleId(), article.getVersion(), article.getTitleMap(),
			article.getDescriptionMap(), article.getContent(), layout.getUuid(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_assertRootElement(
			article, layout, rootElement,
			JournalArticleConstants.CANONICAL_URL_SEPARATOR);
	}

	@Test
	public void testJournalArticleSitemapURLProviderDefaultLayoutCanonicalURLEnabled()
		throws Exception {

		Element rootElement = _getRootElement();

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId());

		_layoutSEOEntryLocalService.updateLayoutSEOEntry(
			TestPropsValues.getUserId(), _group.getGroupId(),
			layout.isPrivateLayout(), layout.getLayoutId(), true,
			new HashMap<>(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		JournalArticle article = JournalTestUtil.addArticleWithWorkflow(
			_group.getGroupId(), true);

		_journalArticleLocalService.updateArticle(
			article.getUserId(), article.getGroupId(), article.getFolderId(),
			article.getArticleId(), article.getVersion(), article.getTitleMap(),
			article.getDescriptionMap(), article.getContent(), layout.getUuid(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_journalArticleSitemapURLProvider.visitLayout(
			rootElement, layout.getUuid(), _layoutSet, _themeDisplay);

		Assert.assertFalse(rootElement.hasContent());
	}

	@Test
	public void testJournalArticleSitemapURLProviderDefaultLayoutRobotsContainingNofollow()
		throws Exception {

		_assertVisitLayoutDefaultLayout(
			RandomTestUtil.randomString() + ", nofollow," +
				RandomTestUtil.randomString());
	}

	@Test
	public void testJournalArticleSitemapURLProviderDefaultLayoutRobotsWithNofollow()
		throws Exception {

		_assertVisitLayoutDefaultLayout("nofollow");
	}

	@Test
	public void testJournalArticleSitemapURLProviderDefaultLayoutRobotsWithNoindex()
		throws Exception {

		_assertVisitLayoutDefaultLayout("noindex");
	}

	@Test
	public void testJournalArticleSitemapWithADisabledLanguageId()
		throws Exception {

		Element rootElement = _getRootElement();

		JournalArticle article = JournalTestUtil.addArticleWithWorkflow(
			_group.getGroupId(), true);

		Assert.assertTrue(
			ArrayUtil.contains(article.getAvailableLanguageIds(), "de_DE"));

		UnicodeProperties typeSettingsUnicodeProperties =
			_group.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.setProperty(
			"inheritLocales", Boolean.FALSE.toString());

		typeSettingsUnicodeProperties.setProperty(
			PropsKeys.LOCALES, "en_US,es_ES");

		_groupLocalService.updateGroup(
			_group.getGroupId(), typeSettingsUnicodeProperties.toString());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(),
				_portal.getClassNameId(JournalArticle.class.getName()),
				article.getDDMStructureId(), true,
				WorkflowConstants.STATUS_APPROVED);

		_assertRootElement(
			article,
			_layoutLocalService.getLayout(layoutPageTemplateEntry.getPlid()),
			rootElement,
			FriendlyURLResolverConstants.URL_SEPARATOR_JOURNAL_ARTICLE);
	}

	private void _assertRootElement(
			JournalArticle article, Layout layout, Element rootElement,
			String urlSeparator)
		throws Exception {

		_journalArticleSitemapURLProvider.visitLayout(
			rootElement, layout.getUuid(), _layoutSet, _themeDisplay);

		Assert.assertTrue(rootElement.hasContent());

		String[] availableLanguageIds = _getAvailableLanguageIds(article);

		List<Element> elements = rootElement.elements();

		Assert.assertEquals(
			elements.toString(), availableLanguageIds.length, elements.size());

		String friendlyURL = StringUtil.toLowerCase(
			StringBundler.concat(
				StringPool.SLASH, _group.getGroupKey(), urlSeparator,
				article.getTitle()));

		for (Element element : elements) {
			String journalArticleLocalizedURL = element.elementText("loc");

			Assert.assertNotNull(journalArticleLocalizedURL);
			Assert.assertTrue(journalArticleLocalizedURL.endsWith(friendlyURL));
		}
	}

	private void _assertVisitLayoutDefaultDisplayPage(String keyword)
		throws Exception {

		JournalArticle article = JournalTestUtil.addArticleWithWorkflow(
			_group.getGroupId(), true);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(),
				_portal.getClassNameId(JournalArticle.class.getName()),
				article.getDDMStructureId(), true,
				WorkflowConstants.STATUS_APPROVED);

		Element rootElement = _getRootElement();

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		layout.setRobotsMap(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), keyword
			).build());

		layout = _layoutLocalService.updateLayout(layout);

		_journalArticleSitemapURLProvider.visitLayout(
			rootElement, layout.getUuid(), _layoutSet, _themeDisplay);

		Assert.assertFalse(rootElement.hasContent());
	}

	private void _assertVisitLayoutDefaultLayout(String keyword)
		throws Exception {

		Element rootElement = _getRootElement();

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId());

		layout.setRobotsMap(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), keyword
			).build());

		layout = _layoutLocalService.updateLayout(layout);

		JournalArticle article = JournalTestUtil.addArticleWithWorkflow(
			_group.getGroupId(), true);

		_journalArticleLocalService.updateArticle(
			article.getUserId(), article.getGroupId(), article.getFolderId(),
			article.getArticleId(), article.getVersion(), article.getTitleMap(),
			article.getDescriptionMap(), article.getContent(), layout.getUuid(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_journalArticleSitemapURLProvider.visitLayout(
			rootElement, layout.getUuid(), _layoutSet, _themeDisplay);

		Assert.assertFalse(rootElement.hasContent());
	}

	private String[] _getAvailableLanguageIds(JournalArticle journalArticle) {
		Set<Locale> siteAvailableLocales = _language.getAvailableLocales(
			_group.getGroupId());

		if (SetUtil.isEmpty(siteAvailableLocales)) {
			return new String[0];
		}

		List<String> availableLanguageIds = new ArrayList<>();

		for (String availableLanguageId :
				journalArticle.getAvailableLanguageIds()) {

			if (siteAvailableLocales.contains(
					LocaleUtil.fromLanguageId(availableLanguageId))) {

				availableLanguageIds.add(availableLanguageId);
			}
		}

		return ArrayUtil.toStringArray(availableLanguageIds);
	}

	private Element _getRootElement() {
		Document document = _saxReader.createDocument();

		document.setXMLEncoding("UTF-8");

		Element rootElement = document.addElement(
			"urlset", "http://www.sitemaps.org/schemas/sitemap/0.9");

		rootElement.addAttribute(
			"xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		rootElement.addAttribute(
			"xsi:schemaLocation",
			"http://www.w3.org/1999/xhtml " +
				"http://www.w3.org/2002/08/xhtml/xhtml1-strict.xsd");
		rootElement.addAttribute("xmlns:xhtml", "http://www.w3.org/1999/xhtml");

		return rootElement;
	}

	private void _initThemeDisplay() throws Exception {
		_themeDisplay = new ThemeDisplay();

		Company company = CompanyLocalServiceUtil.getCompany(
			_group.getCompanyId());

		_themeDisplay.setCompany(company);

		_themeDisplay.setLanguageId(_group.getDefaultLanguageId());
		_themeDisplay.setLayoutSet(_layoutSet);
		_themeDisplay.setLocale(
			LocaleUtil.fromLanguageId(_group.getDefaultLanguageId()));
		_themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));
		_themeDisplay.setPortalDomain(company.getVirtualHostname());
		_themeDisplay.setPortalURL(company.getPortalURL(_group.getGroupId()));
		_themeDisplay.setRequest(new MockHttpServletRequest());
		_themeDisplay.setScopeGroupId(_group.getGroupId());
		_themeDisplay.setServerPort(8080);
		_themeDisplay.setSignedIn(true);
		_themeDisplay.setSiteGroupId(_group.getGroupId());
		_themeDisplay.setUser(TestPropsValues.getUser());
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject(
		filter = "component.name=com.liferay.journal.internal.site.provider.JournalArticleSitemapURLProvider",
		type = SitemapURLProvider.class
	)
	private SitemapURLProvider _journalArticleSitemapURLProvider;

	@Inject
	private Language _language;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutSEOEntryLocalService _layoutSEOEntryLocalService;

	private LayoutSet _layoutSet;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private SAXReader _saxReader;

	private ThemeDisplay _themeDisplay;

}