/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.manager.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.asset.display.page.model.AssetDisplayPageEntry;
import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalService;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyConstants;
import com.liferay.asset.kernel.service.AssetCategoryService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyService;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.url.CPFriendlyURL;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.admin.kernel.model.LayoutTypePortletConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.configuration.test.util.GroupConfigurationTemporarySwapper;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReader;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.manager.SitemapManager;
import com.liferay.translation.info.item.provider.InfoItemLanguagesProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class SitemapManagerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_companyConfigurationTemporarySwapper =
			new CompanyConfigurationTemporarySwapper(
				TestPropsValues.getCompanyId(),
				_PID_SITEMAP_COMPANY_CONFIGURATION,
				HashMapDictionaryBuilder.<String, Object>put(
					"xmlSitemapIndexEnabled", false
				).build());
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_companyConfigurationTemporarySwapper.close();
	}

	@Before
	public void setUp() throws Exception {
		_company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypePortletLayout(_group);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());

		_setUpThemeDisplay();
	}

	@Test
	public void testCompanySitemap() throws Exception {
		Group group = _groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.GUEST);

		_setUpThemeDisplay(
			group,
			_layoutLocalService.fetchFirstLayout(group.getGroupId(), false, 0),
			"localhost");

		String[] guestLayoutURLs = _getSitemapLayoutURLs(group.getGroupId());

		Assert.assertTrue(ArrayUtil.isNotEmpty(guestLayoutURLs));

		_testCompanySitemapIncludePages(
			new long[0], group.getGroupId(), guestLayoutURLs);
	}

	@Test
	public void testCompanySitemapWithAdditionalGroupIdConfigured()
		throws Exception {

		Group group = _groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.GUEST);

		_setUpThemeDisplay(
			group,
			_layoutLocalService.fetchFirstLayout(group.getGroupId(), false, 0),
			"localhost");

		String[] guestLayoutURLs = _getSitemapLayoutURLs(group.getGroupId());

		Assert.assertTrue(ArrayUtil.isNotEmpty(guestLayoutURLs));

		_testCompanySitemapIncludePages(
			new long[] {_group.getGroupId()}, group.getGroupId(),
			ArrayUtil.append(
				guestLayoutURLs,
				_portal.getCanonicalURL(
					_portal.getLayoutFullURL(_layout, _themeDisplay),
					_themeDisplay, _layout)));
	}

	@Test
	public void testCompanySitemapWithAdditionalGroupIdConfiguredButNotCompanyVirtualHost()
		throws Exception {

		Group group = _groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.GUEST);

		_setUpThemeDisplay(
			group,
			_layoutLocalService.fetchFirstLayout(group.getGroupId(), false, 0),
			"myvirtualhost");

		String[] guestLayoutURLs = _getSitemapLayoutURLs(group.getGroupId());

		Assert.assertTrue(ArrayUtil.isNotEmpty(guestLayoutURLs));

		_testCompanySitemapIncludePages(
			new long[] {_group.getGroupId()}, group.getGroupId(),
			guestLayoutURLs);
	}

	@Test
	public void testCompanySitemapWithAdditionalGroupWithVirtualHostConfigured()
		throws Exception {

		Group group = _groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.GUEST);

		_setUpThemeDisplay(
			group,
			_layoutLocalService.fetchFirstLayout(group.getGroupId(), false, 0),
			"localhost");

		String[] guestLayoutURLs = _getSitemapLayoutURLs(group.getGroupId());

		Assert.assertTrue(ArrayUtil.isNotEmpty(guestLayoutURLs));

		_layoutSetLocalService.updateVirtualHosts(
			_group.getGroupId(), false,
			TreeMapBuilder.put(
				"myvirtualhost", _layout.getDefaultLanguageId()
			).build());

		_testCompanySitemapIncludePages(
			new long[] {_group.getGroupId()}, group.getGroupId(),
			guestLayoutURLs);
	}

	@Test
	public void testCompanySitemapWithDeletedtGroupIdConfigured()
		throws Exception {

		Group group = _groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.GUEST);

		_setUpThemeDisplay(
			group,
			_layoutLocalService.fetchFirstLayout(group.getGroupId(), false, 0),
			"localhost");

		String[] guestLayoutURLs = _getSitemapLayoutURLs(group.getGroupId());

		Assert.assertTrue(ArrayUtil.isNotEmpty(guestLayoutURLs));

		_testCompanySitemapIncludePages(
			new long[] {RandomTestUtil.randomLong()}, group.getGroupId(),
			guestLayoutURLs);
	}

	@Test
	public void testCompanySitemapWithGuestVirtualHostConfigured()
		throws Exception {

		Group group = _groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.GUEST);

		_setUpThemeDisplay(
			group,
			_layoutLocalService.fetchFirstLayout(group.getGroupId(), false, 0),
			"localhost");

		LayoutSet layoutSet = group.getPublicLayoutSet();

		NavigableMap<String, String> originalVirtualHostnames =
			layoutSet.getVirtualHostnames();

		try {
			_layoutSetLocalService.updateVirtualHosts(
				group.getGroupId(), false,
				TreeMapBuilder.put(
					"myvirtualhost",
					LocaleUtil.toLanguageId(
						_portal.getSiteDefaultLocale(group.getGroupId()))
				).build());

			_testEmptyCompanySitemapIncludePages(group.getGroupId());
		}
		finally {
			_layoutSetLocalService.updateVirtualHosts(
				group.getGroupId(), false,
				new TreeMap<>(originalVirtualHostnames));
		}
	}

	@Test
	public void testSitemapIncludeCategoriesCompanyDisabledGroupDisabled()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						_PID_SITEMAP_COMPANY_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeCategories", false
						).put(
							"includePages", false
						).put(
							"includeWebContent", false
						).build());
			GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						_group.getGroupId(), _PID_SITEMAP_GROUP_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeCategories", false
						).put(
							"includePages", false
						).put(
							"includeWebContent", false
						).build())) {

			_setUpAssetCategoryDisplayPage();

			_assertEmptySitemap(_layout.getUuid());
		}
	}

	@Test
	public void testSitemapIncludeCategoriesCompanyDisabledGroupEnabled()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						_PID_SITEMAP_COMPANY_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeCategories", false
						).put(
							"includePages", false
						).put(
							"includeWebContent", false
						).build());
			GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						_group.getGroupId(), _PID_SITEMAP_GROUP_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeCategories", true
						).put(
							"includePages", false
						).put(
							"includeWebContent", false
						).build())) {

			_setUpAssetCategoryDisplayPage();

			_assertEmptySitemap(_layout.getUuid());
		}
	}

	@Test
	public void testSitemapIncludeCategoriesCompanyEnabledGroupDisabled()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						_PID_SITEMAP_COMPANY_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeCategories", true
						).put(
							"includePages", false
						).put(
							"includeWebContent", false
						).build());
			GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						_group.getGroupId(), _PID_SITEMAP_GROUP_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeCategories", false
						).put(
							"includePages", false
						).put(
							"includeWebContent", false
						).build())) {

			_setUpAssetCategoryDisplayPage();

			_assertEmptySitemap(_layout.getUuid());
		}
	}

	@Test
	public void testSitemapIncludeCategoriesCompanyEnabledGroupEnabled()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						_PID_SITEMAP_COMPANY_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeCategories", true
						).put(
							"includePages", false
						).put(
							"includeWebContent", false
						).build());
			GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						_group.getGroupId(), _PID_SITEMAP_GROUP_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeCategories", true
						).put(
							"includePages", false
						).put(
							"includeWebContent", false
						).build())) {

			_setUpAssetCategoryDisplayPage();

			_assertSitemap(
				true, _group.getGroupId(), _layout.getUuid(),
				_getExpectedAssetCategoryUrls());
		}
	}

	@Test
	public void testSitemapIncludeChildPages() throws Exception {
		Layout childLayout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), _layout.getPlid());

		String childLayoutCanonicalURL = _portal.getCanonicalURL(
			_portal.getLayoutFullURL(childLayout, _themeDisplay), _themeDisplay,
			childLayout);

		_testSitemapIncludePagesCompanyEnabledGroupEnabled(
			childLayout.getUuid(), childLayoutCanonicalURL);

		Layout grandChildLayout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), childLayout.getPlid());

		_testSitemapIncludePagesCompanyEnabledGroupEnabled(
			grandChildLayout.getUuid(),
			_portal.getCanonicalURL(
				_portal.getLayoutFullURL(grandChildLayout, _themeDisplay),
				_themeDisplay, grandChildLayout));

		UnicodeProperties typeSettingsUnicodeProperties =
			childLayout.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.setProperty(
			"sitemap-include-child-layouts", "false");

		_layoutLocalService.updateLayout(
			childLayout.getGroupId(), false, childLayout.getLayoutId(),
			typeSettingsUnicodeProperties.toString());

		_testSitemapIncludePagesCompanyEnabledGroupEnabled(
			childLayout.getUuid(), childLayoutCanonicalURL);

		_testEmptySitemapIncludePagesCompanyEnabledGroupEnabled(
			grandChildLayout.getUuid());

		typeSettingsUnicodeProperties = _layout.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.setProperty(
			"sitemap-include-child-layouts", "false");

		_layoutLocalService.updateLayout(
			_layout.getGroupId(), false, _layout.getLayoutId(),
			typeSettingsUnicodeProperties.toString());

		_testEmptySitemapIncludePagesCompanyEnabledGroupEnabled(
			childLayout.getUuid());
		_testEmptySitemapIncludePagesCompanyEnabledGroupEnabled(
			grandChildLayout.getUuid());
	}

	@Test
	public void testSitemapIncludeLayoutPageTemplateEntryWithXMLSitemapIndexEnabled()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						_PID_SITEMAP_COMPANY_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"xmlSitemapIndexEnabled", true
						).build());
			GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						_group.getGroupId(), _PID_SITEMAP_GROUP_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"xmlSitemapIndexEnabled", true
						).build())) {

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				DisplayPageTemplateTestUtil.addDisplayPageTemplate(
					_group.getGroupId(),
					_portal.getClassNameId(JournalArticle.class.getName()), 0,
					true, WorkflowConstants.STATUS_APPROVED);

			Layout layout = _layoutLocalService.getLayout(
				layoutPageTemplateEntry.getPlid());

			String[] urls = {
				StringBundler.concat(
					_themeDisplay.getPortalURL(), _portal.getPathContext(),
					"/sitemap.xml?p_l_id=", _layout.getPlid(), "&layoutUuid=",
					_layout.getUuid(), "&groupId=", _group.getGroupId(),
					"&privateLayout=", _layout.isPrivateLayout()),
				StringBundler.concat(
					_themeDisplay.getPortalURL(), _portal.getPathContext(),
					"/sitemap.xml?p_l_id=", layout.getPlid(), "&layoutUuid=",
					layout.getUuid(), "&groupId=", _group.getGroupId(),
					"&privateLayout=", layout.isPrivateLayout())
			};

			_assertSitemap(false, _group.getGroupId(), StringPool.BLANK, urls);
		}
	}

	@Test
	public void testSitemapIncludePagesCompanyDisabledGroupDisabled()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						_PID_SITEMAP_COMPANY_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeCategories", false
						).put(
							"includePages", false
						).put(
							"includeWebContent", false
						).build());
			GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						_group.getGroupId(), _PID_SITEMAP_GROUP_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeCategories", false
						).put(
							"includePages", false
						).put(
							"includeWebContent", false
						).build())) {

			_assertEmptySitemap(_layout.getUuid());
		}
	}

	@Test
	public void testSitemapIncludePagesCompanyDisabledGroupEnabled()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						_PID_SITEMAP_COMPANY_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeCategories", false
						).put(
							"includePages", false
						).put(
							"includeWebContent", false
						).build());
			GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						_group.getGroupId(), _PID_SITEMAP_GROUP_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeCategories", false
						).put(
							"includePages", true
						).put(
							"includeWebContent", false
						).build())) {

			_assertEmptySitemap(_layout.getUuid());
		}
	}

	@Test
	public void testSitemapIncludePagesCompanyEnabledGroupDisabled()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						_PID_SITEMAP_COMPANY_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeCategories", false
						).put(
							"includePages", true
						).put(
							"includeWebContent", false
						).build());
			GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						_group.getGroupId(), _PID_SITEMAP_GROUP_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeCategories", false
						).put(
							"includePages", false
						).put(
							"includeWebContent", false
						).build())) {

			_assertEmptySitemap(_layout.getUuid());
		}
	}

	@Test
	public void testSitemapIncludePagesCompanyEnabledGroupEnabled()
		throws Exception {

		_testSitemapIncludePagesCompanyEnabledGroupEnabled(
			_layout.getUuid(),
			_portal.getCanonicalURL(
				_portal.getLayoutFullURL(_layout, _themeDisplay), _themeDisplay,
				_layout));
	}

	@Test
	public void testSitemapIncludeWebContentCompanyDisabledGroupDisabled()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						_PID_SITEMAP_COMPANY_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeCategories", false
						).put(
							"includePages", false
						).put(
							"includeWebContent", false
						).build());
			GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						_group.getGroupId(), _PID_SITEMAP_GROUP_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeCategories", false
						).put(
							"includePages", false
						).put(
							"includeWebContent", false
						).build())) {

			JournalArticle journalArticle = _addJournalArticle();

			AssetDisplayPageEntry assetDisplayPageEntry =
				_addJournalArticleAssetDisplayPageEntry(journalArticle);

			Layout layout = _layoutLocalService.getLayout(
				assetDisplayPageEntry.getPlid());

			_assertEmptySitemap(layout.getUuid());
		}
	}

	@Test
	public void testSitemapIncludeWebContentCompanyDisabledGroupEnabled()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						_PID_SITEMAP_COMPANY_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeCategories", false
						).put(
							"includePages", false
						).put(
							"includeWebContent", false
						).build());
			GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						_group.getGroupId(), _PID_SITEMAP_GROUP_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeCategories", false
						).put(
							"includePages", false
						).put(
							"includeWebContent", true
						).build())) {

			JournalArticle journalArticle = _addJournalArticle();

			AssetDisplayPageEntry assetDisplayPageEntry =
				_addJournalArticleAssetDisplayPageEntry(journalArticle);

			Layout layout = _layoutLocalService.getLayout(
				assetDisplayPageEntry.getPlid());

			_assertEmptySitemap(layout.getUuid());
		}
	}

	@Test
	public void testSitemapIncludeWebContentCompanyEnabledGroupDisabled()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						_PID_SITEMAP_COMPANY_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeCategories", false
						).put(
							"includePages", false
						).put(
							"includeWebContent", true
						).build());
			GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						_group.getGroupId(), _PID_SITEMAP_GROUP_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeCategories", false
						).put(
							"includePages", false
						).put(
							"includeWebContent", false
						).build())) {

			JournalArticle journalArticle = _addJournalArticle();

			AssetDisplayPageEntry assetDisplayPageEntry =
				_addJournalArticleAssetDisplayPageEntry(journalArticle);

			Layout layout = _layoutLocalService.getLayout(
				assetDisplayPageEntry.getPlid());

			_assertEmptySitemap(layout.getUuid());
		}
	}

	@Test
	public void testSitemapIncludeWebContentCompanyEnabledGroupEnabled()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						_PID_SITEMAP_COMPANY_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeCategories", false
						).put(
							"includePages", false
						).put(
							"includeWebContent", true
						).build());
			GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						_group.getGroupId(), _PID_SITEMAP_GROUP_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeCategories", false
						).put(
							"includePages", false
						).put(
							"includeWebContent", true
						).build())) {

			JournalArticle journalArticle = _addJournalArticle();

			AssetDisplayPageEntry assetDisplayPageEntry =
				_addJournalArticleAssetDisplayPageEntry(journalArticle);

			Layout layout = _layoutLocalService.getLayout(
				assetDisplayPageEntry.getPlid());

			_assertSitemap(
				true, _group.getGroupId(), layout.getUuid(),
				_portal.getCanonicalURL(
					StringBundler.concat(
						_portal.getGroupFriendlyURL(
							_layout.getLayoutSet(), _themeDisplay, false,
							false),
						FriendlyURLResolverConstants.
							URL_SEPARATOR_JOURNAL_ARTICLE,
						journalArticle.getUrlTitle()),
					_themeDisplay, _layout));
		}
	}

	private void _addAssetCategoryAssetDisplayPageEntry() throws Exception {
		_addAssetDisplayPageEntry(
			_portal.getClassNameId(AssetCategory.class.getName()), 0, 0,
			AssetDisplayPageConstants.TYPE_DEFAULT);
	}

	private AssetDisplayPageEntry _addAssetDisplayPageEntry(
			long classNameId, long classPK, long classTypeId, int type)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(), classNameId, classTypeId, true,
				WorkflowConstants.STATUS_APPROVED);

		return _assetDisplayPageEntryLocalService.addAssetDisplayPageEntry(
			TestPropsValues.getUserId(), _group.getGroupId(), classNameId,
			classPK, layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			type, _serviceContext);
	}

	private JournalArticle _addJournalArticle() throws Exception {
		Locale locale = LocaleUtil.getSiteDefault();

		return JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, StringPool.BLANK,
			true, RandomTestUtil.randomLocaleStringMap(locale),
			RandomTestUtil.randomLocaleStringMap(locale),
			RandomTestUtil.randomLocaleStringMap(locale), null, locale, null,
			false, false, _serviceContext);
	}

	private AssetDisplayPageEntry _addJournalArticleAssetDisplayPageEntry(
			JournalArticle journalArticle)
		throws Exception {

		return _addAssetDisplayPageEntry(
			_portal.getClassNameId(JournalArticle.class.getName()),
			journalArticle.getResourcePrimKey(),
			journalArticle.getDDMStructureId(),
			AssetDisplayPageConstants.TYPE_SPECIFIC);
	}

	private void _assertEmptySitemap(String uuid) throws Exception {
		Assert.assertEquals(
			StringPool.BLANK,
			_sitemapManager.getSitemap(
				uuid, _group.getGroupId(), false, _themeDisplay));
	}

	private void _assertSitemap(
			boolean encodeURL, long groupId, String uuid, String... urls)
		throws Exception {

		String xml = _sitemapManager.getSitemap(
			uuid, groupId, false, _themeDisplay);

		Document document = _saxReader.read(xml);

		Element rootElement = document.getRootElement();

		List<Element> elements = rootElement.elements();

		Assert.assertEquals(elements.toString(), urls.length, elements.size());

		for (String url : urls) {
			if (encodeURL) {
				url = _sitemapManager.encodeXML(url);
			}

			Assert.assertNotNull(_getLocElement(elements, url));
		}
	}

	private Set<Locale> _getAvailableLocales(Layout layout)
		throws PortalException {

		Set<Locale> availableLocales = new HashSet<>();

		InfoItemLanguagesProvider<Layout> infoItemLanguagesProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemLanguagesProvider.class, Layout.class.getName());

		for (String availableLanguageId :
				infoItemLanguagesProvider.getAvailableLanguageIds(layout)) {

			availableLocales.add(
				LocaleUtil.fromLanguageId(availableLanguageId));
		}

		return availableLocales;
	}

	private String[] _getExpectedAssetCategoryUrls() throws Exception {
		List<String> urls = new ArrayList<>();

		String assetCategoryURLSeparator =
			_cpFriendlyURL.getAssetCategoryURLSeparator(
				TestPropsValues.getCompanyId());

		for (AssetVocabulary assetVocabulary :
				_assetVocabularyService.getGroupVocabularies(
					_company.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			if (assetVocabulary.getVisibilityType() ==
					AssetVocabularyConstants.VISIBILITY_TYPE_INTERNAL) {

				continue;
			}

			List<AssetCategory> assetCategories =
				_assetCategoryService.getVocabularyCategories(
					assetVocabulary.getVocabularyId(), QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null);

			for (AssetCategory assetCategory : assetCategories) {
				FriendlyURLEntry friendlyURLEntry =
					_friendlyURLEntryLocalService.getMainFriendlyURLEntry(
						_portal.getClassNameId(AssetCategory.class),
						assetCategory.getCategoryId());

				urls.add(
					_portal.getCanonicalURL(
						StringBundler.concat(
							_portal.getGroupFriendlyURL(
								_layout.getLayoutSet(), _themeDisplay, false,
								false),
							assetCategoryURLSeparator,
							friendlyURLEntry.getUrlTitle()),
						_themeDisplay, _layout));
			}
		}

		return ArrayUtil.toStringArray(urls);
	}

	private Element _getLocElement(List<Element> elements, String url) {
		for (Element element : elements) {
			if (!Objects.equals(element.getName(), "sitemap") &&
				!Objects.equals(element.getName(), "url")) {

				continue;
			}

			Element locElement = element.element("loc");

			if ((locElement != null) &&
				Objects.equals(url, locElement.getData())) {

				return locElement;
			}
		}

		return null;
	}

	private String[] _getSitemapLayoutURLs(long groupId) {
		return ArrayUtil.append(
			TransformUtil.transformToArray(
				_layoutLocalService.getLayouts(groupId, false),
				layout -> {
					if (layout.isSystem()) {
						return null;
					}

					UnicodeProperties typeSettingsUnicodeProperties =
						layout.getTypeSettingsProperties();

					if (GetterUtil.getBoolean(
							typeSettingsUnicodeProperties.getProperty(
								LayoutTypePortletConstants.SITEMAP_INCLUDE),
							true)) {

						Map<Locale, String> alternateURLMap =
							_portal.getAlternateURLs(
								_portal.getCanonicalURL(
									_portal.getLayoutFullURL(
										layout, _themeDisplay),
									_themeDisplay, layout),
								_themeDisplay, layout,
								_getAvailableLocales(layout));

						String[] alternateURLs = ArrayUtil.toStringArray(
							alternateURLMap.values());

						_testSitemapIncludePagesCompanyEnabledGroupEnabled(
							groupId, layout.getUuid(), alternateURLs);

						return alternateURLs;
					}

					return null;
				},
				String[].class));
	}

	private void _setUpAssetCategoryDisplayPage() throws Exception {
		LayoutTestUtil.addPortletToLayout(
			_layout, CPPortletKeys.CP_CATEGORY_CONTENT_WEB);

		_addAssetCategoryAssetDisplayPageEntry();

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _company.getGroupId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomLocaleStringMap(), Collections.emptyMap(),
				StringPool.BLANK, _serviceContext);

		AssetTestUtil.addCategory(
			assetVocabulary.getGroupId(), assetVocabulary.getVocabularyId());

		Assert.assertEquals(
			1,
			_assetCategoryService.getVocabularyCategoriesCount(
				assetVocabulary.getGroupId(),
				assetVocabulary.getVocabularyId()));
	}

	private void _setUpThemeDisplay() throws Exception {
		_setUpThemeDisplay(_group, _layout, "localhost");
	}

	private void _setUpThemeDisplay(
			Group group, Layout layout, String serverName)
		throws Exception {

		_themeDisplay = ContentLayoutTestUtil.getThemeDisplay(
			_company, group, layout);

		_themeDisplay.setPortalURL("http://" + serverName + ":8080");

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(WebKeys.LAYOUT, layout);
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		_themeDisplay.setRequest(mockHttpServletRequest);

		_themeDisplay.setServerName(serverName);
		_themeDisplay.setServerPort(8080);
	}

	private void _testCompanySitemapIncludePages(
			long[] companySitemapGroupIds, long guestGroupId, String... urls)
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						_PID_SITEMAP_COMPANY_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"companySitemapGroupIds",
							ArrayUtil.toStringArray(companySitemapGroupIds)
						).put(
							"includeCategories", false
						).put(
							"includePages", true
						).put(
							"includeWebContent", false
						).build());
			GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						guestGroupId, _PID_SITEMAP_GROUP_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeCategories", false
						).put(
							"includePages", true
						).put(
							"includeWebContent", false
						).build())) {

			_assertSitemap(true, guestGroupId, null, urls);
		}
	}

	private void _testEmptyCompanySitemapIncludePages(long guestGroupId)
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						_PID_SITEMAP_COMPANY_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"companySitemapGroupIds", new String[0]
						).put(
							"includeCategories", false
						).put(
							"includePages", true
						).put(
							"includeWebContent", false
						).build());
			GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						guestGroupId, _PID_SITEMAP_GROUP_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeCategories", false
						).put(
							"includePages", true
						).put(
							"includeWebContent", false
						).build())) {

			Assert.assertEquals(
				StringPool.BLANK,
				_sitemapManager.getSitemap(
					null, guestGroupId, false, _themeDisplay));
		}
	}

	private void _testEmptySitemapIncludePagesCompanyEnabledGroupEnabled(
			String uuid)
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						_PID_SITEMAP_COMPANY_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeCategories", false
						).put(
							"includePages", true
						).put(
							"includeWebContent", false
						).build());
			GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						_group.getGroupId(), _PID_SITEMAP_GROUP_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeCategories", false
						).put(
							"includePages", true
						).put(
							"includeWebContent", false
						).build())) {

			_assertEmptySitemap(uuid);
		}
	}

	private void _testSitemapIncludePagesCompanyEnabledGroupEnabled(
			long groupId, String uuid, String... urls)
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						_PID_SITEMAP_COMPANY_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeCategories", false
						).put(
							"includePages", true
						).put(
							"includeWebContent", false
						).build());
			GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						groupId, _PID_SITEMAP_GROUP_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeCategories", false
						).put(
							"includePages", true
						).put(
							"includeWebContent", false
						).build())) {

			_assertSitemap(true, groupId, uuid, urls);
		}
	}

	private void _testSitemapIncludePagesCompanyEnabledGroupEnabled(
			String uuid, String... urls)
		throws Exception {

		_testSitemapIncludePagesCompanyEnabledGroupEnabled(
			_group.getGroupId(), uuid, urls);
	}

	private static final String _PID_SITEMAP_COMPANY_CONFIGURATION =
		"com.liferay.site.internal.configuration.SitemapCompanyConfiguration";

	private static final String _PID_SITEMAP_GROUP_CONFIGURATION =
		"com.liferay.site.internal.configuration.SitemapGroupConfiguration";

	private static CompanyConfigurationTemporarySwapper
		_companyConfigurationTemporarySwapper;

	@Inject
	private AssetCategoryService _assetCategoryService;

	@Inject
	private AssetDisplayPageEntryLocalService
		_assetDisplayPageEntryLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject
	private AssetVocabularyService _assetVocabularyService;

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private CPFriendlyURL _cpFriendlyURL;

	@Inject
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private SAXReader _saxReader;

	private ServiceContext _serviceContext;

	@Inject
	private SitemapManager _sitemapManager;

	private ThemeDisplay _themeDisplay;

}