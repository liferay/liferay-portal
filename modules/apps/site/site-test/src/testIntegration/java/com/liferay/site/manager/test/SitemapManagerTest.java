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
import com.liferay.counter.kernel.service.CounterLocalServiceUtil;
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
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
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
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReader;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.redirect.model.RedirectEntry;
import com.liferay.redirect.service.RedirectEntryLocalService;
import com.liferay.site.constants.SitemapConstants;
import com.liferay.site.manager.SitemapManager;
import com.liferay.site.storage.helper.SitemapStorageHelper;
import com.liferay.translation.info.item.provider.InfoItemLanguagesProvider;

import java.io.Serializable;

import java.time.OffsetDateTime;

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

import org.junit.After;
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
		_assetCategoryClassNameId = PortalUtil.getClassNameId(
			AssetCategory.class);
		_journalArticleClassNameId = PortalUtil.getClassNameId(
			JournalArticle.class);
		_layoutClassNameId = PortalUtil.getClassNameId(Layout.class);
		_objectEntryClassNameId = PortalUtil.getClassNameId(ObjectEntry.class);

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

	@After
	public void tearDown() throws Exception {
		_sitemapManager.deleteRegenerateSitemapScheduledJobs(
			TestPropsValues.getCompanyId());

		if (_group != null) {
			_sitemapStorageHelper.deleteSitemaps(
				TestPropsValues.getCompanyId(), _group.getGroupId());
		}
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
	public void testSitemapByAssetTypeIgnoresRequestParameters()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						_PID_SITEMAP_COMPANY_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"cachedGenerationEnabled", true
						).put(
							"includeCategories", false
						).put(
							"includePages", true
						).put(
							"includeWebContent", false
						).put(
							"xmlSitemapIndexEnabled", true
						).put(
							"xmlSitemapIndexMode",
							SitemapConstants.INDEX_MODE_ASSET_TYPE
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

			String xml = _sitemapManager.getSitemap(
				_layoutClassNameId, null, _group.getGroupId(), 1, true,
				_themeDisplay);

			Assert.assertTrue(xml, xml.contains(_getLayoutCanonicalURL()));

			Assert.assertEquals(
				xml,
				StringUtil.read(
					_sitemapStorageHelper.getSitemapInputStream(
						TestPropsValues.getCompanyId(), _group.getGroupId(),
						SitemapConstants.ASSET_TYPE_KEY_PAGES, 1)));

			Assert.assertNull(
				_sitemapManager.getSitemap(
					_layoutClassNameId, null, _group.getGroupId(), 99, true,
					_themeDisplay));

			Assert.assertEquals(
				xml,
				StringUtil.read(
					_sitemapStorageHelper.getSitemapInputStream(
						TestPropsValues.getCompanyId(), _group.getGroupId(),
						SitemapConstants.ASSET_TYPE_KEY_PAGES, 1)));
		}
	}

	@Test
	public void testSitemapByAssetTypeObjectDefinitionRespectsIncludeFilter()
		throws Exception {

		_excludedObjectDefinition = _publishObjectDefinition();

		ObjectEntry excludedObjectEntry = _addObjectEntry(
			_excludedObjectDefinition);

		_addObjectDefinitionDisplayPage(_excludedObjectDefinition);

		_includedObjectDefinition = _publishObjectDefinition();

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						_PID_SITEMAP_COMPANY_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"companySitemapObjectDefinitionIds",
							new String[] {
								String.valueOf(
									_includedObjectDefinition.
										getObjectDefinitionId())
							}
						).put(
							"xmlSitemapIndexEnabled", true
						).put(
							"xmlSitemapIndexMode",
							SitemapConstants.INDEX_MODE_ASSET_TYPE
						).build())) {

			ObjectEntry includedObjectEntry = _addObjectEntry(
				_includedObjectDefinition);

			_addObjectDefinitionDisplayPage(_includedObjectDefinition);

			Role role = _roleLocalService.getRole(
				TestPropsValues.getCompanyId(), RoleConstants.GUEST);

			_resourcePermissionLocalService.setResourcePermissions(
				TestPropsValues.getCompanyId(),
				_includedObjectDefinition.getClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(includedObjectEntry.getObjectEntryId()),
				role.getRoleId(), new String[] {ActionKeys.VIEW});

			String xml = _sitemapManager.getSitemap(
				_objectEntryClassNameId, null, _group.getGroupId(), false,
				_themeDisplay);

			Document document = _saxReader.read(xml);

			Element rootElement = document.getRootElement();

			List<Element> urlElements = rootElement.elements("url");

			Assert.assertEquals(xml, 1, urlElements.size());

			Element urlElement = urlElements.get(0);

			Element locElement = urlElement.element("loc");

			String locElementText = locElement.getText();

			Assert.assertTrue(
				locElementText,
				locElementText.contains(
					_getObjectEntryFriendlyURL(
						includedObjectEntry, _includedObjectDefinition)));
			Assert.assertFalse(
				locElementText,
				locElementText.contains(
					_getObjectEntryFriendlyURL(
						excludedObjectEntry, _excludedObjectDefinition)));
		}
	}

	@Test
	public void testSitemapByAssetTypeOnDemandDoesNotStore() throws Exception {
		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						_PID_SITEMAP_COMPANY_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"cachedGenerationEnabled", false
						).put(
							"xmlSitemapIndexEnabled", true
						).put(
							"xmlSitemapIndexMode",
							SitemapConstants.INDEX_MODE_ASSET_TYPE
						).build())) {

			_addJournalArticleAssetDisplayPageEntry(_addJournalArticle());

			Assert.assertNotNull(
				_sitemapManager.getSitemap(
					_journalArticleClassNameId, null, _group.getGroupId(),
					false, _themeDisplay));

			Assert.assertFalse(
				_sitemapStorageHelper.hasSitemapFile(
					TestPropsValues.getCompanyId(), _group.getGroupId(),
					SitemapConstants.ASSET_TYPE_KEY_WEB_CONTENT, 1));
		}
	}

	@Test
	public void testSitemapByAssetTypePaginationAttributesAreAbsent()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						_PID_SITEMAP_COMPANY_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"cachedGenerationEnabled", true
						).put(
							"xmlSitemapIndexEnabled", true
						).put(
							"xmlSitemapIndexMode",
							SitemapConstants.INDEX_MODE_ASSET_TYPE
						).build())) {

			_addJournalArticleAssetDisplayPageEntry(_addJournalArticle());

			_sitemapManager.getSitemap(
				_journalArticleClassNameId, null, _group.getGroupId(), false,
				_themeDisplay);

			String xml = StringUtil.read(
				_sitemapStorageHelper.getSitemapInputStream(
					TestPropsValues.getCompanyId(), _group.getGroupId(),
					SitemapConstants.ASSET_TYPE_KEY_WEB_CONTENT, 1));

			Assert.assertFalse(xml.contains("_assetTypeKey"));
			Assert.assertFalse(xml.contains("_companyId"));
			Assert.assertFalse(xml.contains("_groupId"));
			Assert.assertFalse(xml.contains("_page"));
			Assert.assertFalse(xml.contains("entries="));
		}
	}

	@Test
	public void testSitemapByAssetTypePaginationStoresAndPrunesPages()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						_PID_SITEMAP_COMPANY_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"cachedGenerationEnabled", true
						).put(
							"xmlSitemapIndexEnabled", true
						).put(
							"xmlSitemapIndexMode",
							SitemapConstants.INDEX_MODE_ASSET_TYPE
						).build())) {

			ReflectionTestUtil.setFieldValue(
				_sitemapManager, "_maximumEntries", 3);

			try {
				for (int i = 0; i < 7; i++) {
					_addJournalArticleAssetDisplayPageEntry(
						_addJournalArticle());
				}

				_sitemapManager.getSitemap(
					_journalArticleClassNameId, null, _group.getGroupId(), 1,
					false, _themeDisplay);

				long companyId = TestPropsValues.getCompanyId();
				long groupId = _group.getGroupId();

				Assert.assertTrue(
					_sitemapStorageHelper.hasSitemapFile(
						companyId, groupId,
						SitemapConstants.ASSET_TYPE_KEY_WEB_CONTENT, 1));
				Assert.assertTrue(
					_sitemapStorageHelper.hasSitemapFile(
						companyId, groupId,
						SitemapConstants.ASSET_TYPE_KEY_WEB_CONTENT, 2));
				Assert.assertTrue(
					_sitemapStorageHelper.hasSitemapFile(
						companyId, groupId,
						SitemapConstants.ASSET_TYPE_KEY_WEB_CONTENT, 3));
				Assert.assertFalse(
					_sitemapStorageHelper.hasSitemapFile(
						companyId, groupId,
						SitemapConstants.ASSET_TYPE_KEY_WEB_CONTENT, 4));

				ReflectionTestUtil.setFieldValue(
					_sitemapManager, "_maximumEntries",
					SitemapManager.MAXIMUM_ENTRIES);

				_sitemapManager.regenerateSitemap(
					SitemapConstants.ASSET_TYPE_KEY_WEB_CONTENT, companyId,
					groupId);

				Assert.assertTrue(
					_sitemapStorageHelper.hasSitemapFile(
						companyId, groupId,
						SitemapConstants.ASSET_TYPE_KEY_WEB_CONTENT, 1));
				Assert.assertFalse(
					_sitemapStorageHelper.hasSitemapFile(
						companyId, groupId,
						SitemapConstants.ASSET_TYPE_KEY_WEB_CONTENT, 2));
				Assert.assertFalse(
					_sitemapStorageHelper.hasSitemapFile(
						companyId, groupId,
						SitemapConstants.ASSET_TYPE_KEY_WEB_CONTENT, 3));
			}
			finally {
				ReflectionTestUtil.setFieldValue(
					_sitemapManager, "_maximumEntries",
					SitemapManager.MAXIMUM_ENTRIES);
			}
		}
	}

	@Test
	public void testSitemapByAssetTypeRespectsIncludeFlag() throws Exception {
		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						_PID_SITEMAP_COMPANY_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeWebContent", false
						).put(
							"xmlSitemapIndexEnabled", true
						).put(
							"xmlSitemapIndexMode",
							SitemapConstants.INDEX_MODE_ASSET_TYPE
						).build())) {

			JournalArticle journalArticle = _addJournalArticle();

			_addJournalArticleAssetDisplayPageEntry(journalArticle);

			String xml = _sitemapManager.getSitemap(
				_journalArticleClassNameId, null, _group.getGroupId(), false,
				_themeDisplay);

			Assert.assertNull(xml);
		}
	}

	@Test
	public void testSitemapByAssetTypeWiringToProvider() throws Exception {
		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						_PID_SITEMAP_COMPANY_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"xmlSitemapIndexEnabled", true
						).put(
							"xmlSitemapIndexMode",
							SitemapConstants.INDEX_MODE_ASSET_TYPE
						).build())) {

			JournalArticle journalArticle = _addJournalArticle();

			_addJournalArticleAssetDisplayPageEntry(journalArticle);

			String xml = _sitemapManager.getSitemap(
				_journalArticleClassNameId, null, _group.getGroupId(), false,
				_themeDisplay);

			Document document = _saxReader.read(xml);

			Element rootElement = document.getRootElement();

			List<Element> elements = rootElement.elements();

			Assert.assertEquals("urlset", rootElement.getName());

			Assert.assertFalse(elements.toString(), elements.isEmpty());
		}
	}

	@Test
	public void testSitemapExcludesPrivateLayout() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(_group, true);

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

			_assertEmptySitemap(_group.getGroupId(), true, layout.getUuid());
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

			_assertEmptySitemap(_group.getGroupId(), _layout.getUuid());
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

			_assertEmptySitemap(_group.getGroupId(), _layout.getUuid());
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

			_assertEmptySitemap(_group.getGroupId(), _layout.getUuid());
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

		_layoutLocalService.updateTypeSettings(
			childLayout, typeSettingsUnicodeProperties.toString());

		_testSitemapIncludePagesCompanyEnabledGroupEnabled(
			childLayout.getUuid(), childLayoutCanonicalURL);

		_testEmptySitemapIncludePagesCompanyEnabledGroupEnabled(
			grandChildLayout.getUuid());

		typeSettingsUnicodeProperties = _layout.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.setProperty(
			"sitemap-include-child-layouts", "false");

		_layoutLocalService.updateTypeSettings(
			_layout, typeSettingsUnicodeProperties.toString());

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
					_group.getGroupId(), _journalArticleClassNameId, null, true,
					WorkflowConstants.STATUS_APPROVED);

			Layout layout = _layoutLocalService.getLayout(
				layoutPageTemplateEntry.getPlid());

			String[] urls = {
				StringBundler.concat(
					_themeDisplay.getPortalURL(), _portal.getPathContext(),
					"/sitemap.xml?p_l_id=", _layout.getPlid(), "&layoutUuid=",
					_layout.getUuid(), "&groupId=", _group.getGroupId()),
				StringBundler.concat(
					_themeDisplay.getPortalURL(), _portal.getPathContext(),
					"/sitemap.xml?p_l_id=", layout.getPlid(), "&layoutUuid=",
					layout.getUuid(), "&groupId=", _group.getGroupId())
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

			_assertEmptySitemap(_group.getGroupId(), _layout.getUuid());
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

			_assertEmptySitemap(_group.getGroupId(), _layout.getUuid());
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

			_assertEmptySitemap(_group.getGroupId(), _layout.getUuid());
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
	public void testSitemapIncludePagesWithLocaleAndRedirect()
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

			_layout.setTitle("Spanish Title", LocaleUtil.SPAIN);

			_layout = _layoutLocalService.updateLayout(_layout);

			String canonicalURL = _portal.getCanonicalURL(
				_portal.getLayoutFullURL(_layout, _themeDisplay), _themeDisplay,
				_layout);

			Map<Locale, String> alternateURLs =
				_sitemapManager.getAlternateURLs(
					canonicalURL, _themeDisplay, _layout);

			String spanishURL = alternateURLs.get(LocaleUtil.SPAIN);

			_assertSitemap(
				true, _group.getGroupId(), _layout.getUuid(), canonicalURL,
				spanishURL);

			String sourceURL = HttpComponentsUtil.getPath(spanishURL);

			_addRedirectEntry(sourceURL.substring(1));

			_assertSitemap(
				true, _group.getGroupId(), _layout.getUuid(), canonicalURL);
		}
	}

	@Test
	public void testSitemapIncludePagesWithRedirect() throws Exception {
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

			String canonicalURL = _portal.getCanonicalURL(
				_portal.getLayoutFullURL(_layout, _themeDisplay), _themeDisplay,
				_layout);

			_assertSitemap(
				true, _group.getGroupId(), _layout.getUuid(), canonicalURL);

			String sourceURL = HttpComponentsUtil.getPath(canonicalURL);

			_addRedirectEntry(sourceURL.substring(1));

			_assertEmptySitemap(_group.getGroupId(), _layout.getUuid());
		}
	}

	@Test
	public void testSitemapIncludePagesWithVirtualHostAndRedirect()
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

			String virtualHostname = StringUtil.randomString(8);

			_layoutSetLocalService.updateVirtualHosts(
				_group.getGroupId(), false,
				TreeMapBuilder.put(
					virtualHostname, _layout.getDefaultLanguageId()
				).build());

			Layout childLayout = LayoutTestUtil.addTypePortletLayout(
				_group.getGroupId(), _layout.getPlid());

			_setUpThemeDisplay(_group, childLayout, virtualHostname);

			String canonicalURL = _portal.getCanonicalURL(
				_portal.getLayoutFullURL(childLayout, _themeDisplay),
				_themeDisplay, childLayout);

			_assertSitemap(
				true, _group.getGroupId(), childLayout.getUuid(), canonicalURL);

			String sourceURL = HttpComponentsUtil.getPath(canonicalURL);

			_addRedirectEntry(sourceURL.substring(1));

			_assertEmptySitemap(_group.getGroupId(), childLayout.getUuid());
		}
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

			_assertEmptySitemap(_group.getGroupId(), layout.getUuid());
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

			_assertEmptySitemap(_group.getGroupId(), layout.getUuid());
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

			_assertEmptySitemap(_group.getGroupId(), layout.getUuid());
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

	@Test
	public void testSitemapIncludeWebContentWithRedirect() throws Exception {
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

			String sourceURL =
				FriendlyURLResolverConstants.URL_SEPARATOR_JOURNAL_ARTICLE +
					journalArticle.getUrlTitle();

			_addRedirectEntry(sourceURL.substring(1));

			_assertEmptySitemap(_group.getGroupId(), layout.getUuid());
		}
	}

	@Test
	public void testSitemapIndexByAssetType() throws Exception {
		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						_PID_SITEMAP_COMPANY_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"xmlSitemapIndexEnabled", true
						).put(
							"xmlSitemapIndexMode",
							SitemapConstants.INDEX_MODE_ASSET_TYPE
						).build())) {

			List<String> urls = new ArrayList<>();

			Map<Long, String> assetTypeKeys =
				_sitemapManager.getAssetTypeKeys();

			for (Map.Entry<Long, String> entry : assetTypeKeys.entrySet()) {
				if (entry.getKey() == _objectEntryClassNameId) {
					continue;
				}

				urls.add(
					StringBundler.concat(
						_themeDisplay.getPortalURL(), _portal.getPathContext(),
						"/sitemap-", entry.getValue(), ".xml?groupId=",
						_group.getGroupId()));
			}

			_assertSitemap(
				false, _group.getGroupId(), StringPool.BLANK,
				ArrayUtil.toStringArray(urls));
		}
	}

	@Test
	public void testSitemapIndexByAssetTypeEmitsLastmod() throws Exception {
		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						_PID_SITEMAP_COMPANY_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"xmlSitemapIndexEnabled", true
						).put(
							"xmlSitemapIndexMode",
							SitemapConstants.INDEX_MODE_ASSET_TYPE
						).build())) {

			JournalArticle journalArticle = _addJournalArticle();

			_addJournalArticleAssetDisplayPageEntry(journalArticle);

			String xml = _sitemapManager.getSitemap(
				null, _group.getGroupId(), false, _themeDisplay);

			Document document = _saxReader.read(xml);

			Element rootElement = document.getRootElement();

			Element webContentLocElement = _getLocElement(
				rootElement.elements(),
				_buildAssetTypeSitemapURL(_journalArticleClassNameId));

			Assert.assertNotNull(webContentLocElement);

			Element sitemapElement = webContentLocElement.getParent();

			Element lastmodElement = sitemapElement.element("lastmod");

			Assert.assertNotNull(lastmodElement);

			Assert.assertNotNull(
				OffsetDateTime.parse(lastmodElement.getText()));
		}
	}

	@Test
	public void testSitemapIndexByAssetTypePageParamWithMultiplePages()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						_PID_SITEMAP_COMPANY_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"xmlSitemapIndexEnabled", true
						).put(
							"xmlSitemapIndexMode",
							SitemapConstants.INDEX_MODE_ASSET_TYPE
						).build())) {

			ReflectionTestUtil.setFieldValue(
				_sitemapManager, "_maximumEntries", 3);

			try {
				for (int i = 0; i < 7; i++) {
					_addJournalArticleAssetDisplayPageEntry(
						_addJournalArticle());
				}

				Document document = _saxReader.read(
					_sitemapManager.getSitemap(
						null, _group.getGroupId(), false, _themeDisplay));

				Element rootElement = document.getRootElement();

				List<String> locs = TransformUtil.transform(
					rootElement.elements("sitemap"),
					sitemapElement -> {
						String loc = sitemapElement.elementText("loc");

						if (loc.contains(
								SitemapConstants.ASSET_TYPE_KEY_WEB_CONTENT)) {

							return loc;
						}

						return null;
					});

				Assert.assertEquals(locs.toString(), 3, locs.size());

				for (int i = 0; i < locs.size(); i++) {
					String loc = locs.get(i);

					Assert.assertTrue(loc.contains("&page=" + (i + 1)));
				}
			}
			finally {
				ReflectionTestUtil.setFieldValue(
					_sitemapManager, "_maximumEntries",
					SitemapManager.MAXIMUM_ENTRIES);
			}
		}
	}

	@Test
	public void testSitemapIndexByAssetTypePageParamWithSinglePage()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						_PID_SITEMAP_COMPANY_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"xmlSitemapIndexEnabled", true
						).put(
							"xmlSitemapIndexMode",
							SitemapConstants.INDEX_MODE_ASSET_TYPE
						).build())) {

			_addJournalArticleAssetDisplayPageEntry(_addJournalArticle());

			Document document = _saxReader.read(
				_sitemapManager.getSitemap(
					null, _group.getGroupId(), false, _themeDisplay));

			Element rootElement = document.getRootElement();

			for (Element sitemapElement : rootElement.elements("sitemap")) {
				String loc = sitemapElement.elementText("loc");

				Assert.assertFalse(loc.contains("&page="));
			}
		}
	}

	@Test
	public void testSitemapIndexByAssetTypeRespectsPerTypeFlagsCompany()
		throws Exception {

		String categoriesURL = _buildAssetTypeSitemapURL(
			_assetCategoryClassNameId);
		String pagesURL = _buildAssetTypeSitemapURL(_layoutClassNameId);

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						_PID_SITEMAP_COMPANY_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeWebContent", false
						).put(
							"xmlSitemapIndexEnabled", true
						).put(
							"xmlSitemapIndexMode",
							SitemapConstants.INDEX_MODE_ASSET_TYPE
						).build())) {

			_assertSitemap(
				false, _group.getGroupId(), StringPool.BLANK, categoriesURL,
				pagesURL);
		}

		String webContentURL = _buildAssetTypeSitemapURL(
			_journalArticleClassNameId);

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						_PID_SITEMAP_COMPANY_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeCategories", false
						).put(
							"xmlSitemapIndexEnabled", true
						).put(
							"xmlSitemapIndexMode",
							SitemapConstants.INDEX_MODE_ASSET_TYPE
						).build())) {

			_assertSitemap(
				false, _group.getGroupId(), StringPool.BLANK, pagesURL,
				webContentURL);
		}

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						_PID_SITEMAP_COMPANY_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"includePages", false
						).put(
							"xmlSitemapIndexEnabled", true
						).put(
							"xmlSitemapIndexMode",
							SitemapConstants.INDEX_MODE_ASSET_TYPE
						).build())) {

			_assertSitemap(
				false, _group.getGroupId(), StringPool.BLANK, categoriesURL,
				webContentURL);
		}

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
						).put(
							"xmlSitemapIndexEnabled", true
						).put(
							"xmlSitemapIndexMode",
							SitemapConstants.INDEX_MODE_ASSET_TYPE
						).build())) {

			_assertSitemap(false, _group.getGroupId(), StringPool.BLANK);
		}
	}

	@Test
	public void testSitemapIndexByAssetTypeStoresInDLStore() throws Exception {
		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						_PID_SITEMAP_COMPANY_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"cachedGenerationEnabled", true
						).put(
							"xmlSitemapIndexEnabled", true
						).put(
							"xmlSitemapIndexMode",
							SitemapConstants.INDEX_MODE_ASSET_TYPE
						).build())) {

			_sitemapManager.getSitemap(
				null, _group.getGroupId(), false, _themeDisplay);

			Assert.assertTrue(
				_sitemapStorageHelper.hasSitemapFile(
					TestPropsValues.getCompanyId(), _group.getGroupId()));
		}
	}

	@Test
	public void testSitemapIndexExcludesLayoutHiddenFromGuest()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.GUEST);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), Layout.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(layout.getPlid()), role.getRoleId(), new String[0]);

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						_PID_SITEMAP_COMPANY_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"xmlSitemapIndexEnabled", true
						).build())) {

			String xml = _sitemapManager.getSitemap(
				_group.getGroupId(), false, _themeDisplay);

			Document document = _saxReader.read(xml);

			Element rootElement = document.getRootElement();

			List<Element> elements = rootElement.elements();

			Assert.assertNotNull(
				xml, _getLocElement(elements, _buildLayoutSitemapURL(_layout)));
			Assert.assertNull(
				xml, _getLocElement(elements, _buildLayoutSitemapURL(layout)));
		}
	}

	@Test
	public void testSitemapURLsWithLayoutFriendlyURLPublicServletMappingDisabled()
		throws Exception {

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING_ENABLED",
					false)) {

			Group group = _groupLocalService.getGroup(
				TestPropsValues.getCompanyId(), GroupConstants.GUEST);

			_setUpThemeDisplay(
				group,
				_layoutLocalService.fetchFirstLayout(
					group.getGroupId(), false, 0),
				_company.getVirtualHostname());

			String[] guestLayoutURLs = _getSitemapLayoutURLs(
				group.getGroupId());

			Assert.assertTrue(ArrayUtil.isNotEmpty(guestLayoutURLs));

			for (String guestLayoutURL : guestLayoutURLs) {
				Assert.assertFalse(guestLayoutURL.contains("/web/"));
			}
		}
	}

	private void _addAssetCategoryAssetDisplayPageEntry() throws Exception {
		_addAssetDisplayPageEntry(
			_assetCategoryClassNameId, 0, null,
			AssetDisplayPageConstants.TYPE_DEFAULT);
	}

	private AssetDisplayPageEntry _addAssetDisplayPageEntry(
			long classNameId, long classPK, String classTypeKey, int type)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(), classNameId, classTypeKey, true,
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
			_journalArticleClassNameId, journalArticle.getResourcePrimKey(),
			journalArticle.getDDMStructureKey(),
			AssetDisplayPageConstants.TYPE_SPECIFIC);
	}

	private void _addObjectDefinitionDisplayPage(
			ObjectDefinition objectDefinition)
		throws Exception {

		DisplayPageTemplateTestUtil.addDisplayPageTemplate(
			_group.getGroupId(),
			_portal.getClassNameId(objectDefinition.getClassName()), null, true,
			WorkflowConstants.STATUS_APPROVED);
	}

	private ObjectEntry _addObjectEntry(ObjectDefinition objectDefinition)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"textObjectField", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private void _addRedirectEntry(String sourceURL) throws Exception {
		RedirectEntry redirectEntry =
			_redirectEntryLocalService.createRedirectEntry(
				CounterLocalServiceUtil.increment());

		redirectEntry.setUuid(PortalUUIDUtil.generate());
		redirectEntry.setGroupId(_group.getGroupId());
		redirectEntry.setCompanyId(TestPropsValues.getCompanyId());
		redirectEntry.setDestinationURL("https://liferay.com");
		redirectEntry.setPermanent(true);
		redirectEntry.setSourceURL(sourceURL);

		_redirectEntryLocalService.addRedirectEntry(redirectEntry);
	}

	private void _assertEmptySitemap(
			long groupId, boolean privateLayout, String uuid)
		throws Exception {

		String xml = _sitemapManager.getSitemap(
			uuid, groupId, privateLayout, _themeDisplay);

		Document document = _saxReader.read(xml);

		Element rootElement = document.getRootElement();

		Assert.assertTrue(
			xml,
			rootElement.elements(
			).isEmpty());
	}

	private void _assertEmptySitemap(long groupId, String uuid)
		throws Exception {

		_assertEmptySitemap(groupId, false, uuid);
	}

	private void _assertSitemap(
			boolean encodeURL, long groupId, String uuid, String... urls)
		throws Exception {

		_assertSitemap(StringPool.BLANK, encodeURL, groupId, uuid, urls);
	}

	private void _assertSitemap(
			String assetType, boolean encodeURL, long groupId, String uuid,
			String... urls)
		throws Exception {

		String xml = _sitemapManager.getSitemap(
			Validator.isNull(assetType) ? 0 : _portal.getClassNameId(assetType),
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

	private String _buildAssetTypeSitemapURL(long assetTypeClassNameId) {
		Map<Long, String> assetTypeKeys = _sitemapManager.getAssetTypeKeys();

		return StringBundler.concat(
			_themeDisplay.getPortalURL(), _portal.getPathContext(), "/sitemap-",
			assetTypeKeys.get(assetTypeClassNameId), ".xml?groupId=",
			_group.getGroupId());
	}

	private String _buildLayoutSitemapURL(Layout layout) {
		return StringBundler.concat(
			_themeDisplay.getPortalURL(), _portal.getPathContext(),
			"/sitemap.xml?p_l_id=", layout.getPlid(), "&layoutUuid=",
			layout.getUuid(), "&groupId=", _group.getGroupId());
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
						_assetCategoryClassNameId,
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

	private String _getLayoutCanonicalURL() throws Exception {
		return _sitemapManager.encodeXML(
			_portal.getCanonicalURL(
				_portal.getLayoutFullURL(_layout, _themeDisplay), _themeDisplay,
				_layout));
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

	private String _getObjectEntryFriendlyURL(
		ObjectEntry objectEntry, ObjectDefinition objectDefinition) {

		String urlTitle = objectEntry.getURLTitle(
			LocaleUtil.fromLanguageId(_themeDisplay.getLanguageId()));

		if (Validator.isNotNull(urlTitle)) {
			return urlTitle;
		}

		if (!objectDefinition.isDefaultStorageType()) {
			return objectEntry.getExternalReferenceCode();
		}

		return String.valueOf(objectEntry.getObjectEntryId());
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

	private ObjectDefinition _publishObjectDefinition() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition(
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"textObjectField"
					).objectFieldSettings(
						Collections.emptyList()
					).build()));

		return _objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());
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
		_setUpThemeDisplay(_group, _layout, _company.getVirtualHostname());
	}

	private void _setUpThemeDisplay(
			Group group, Layout layout, String serverName)
		throws Exception {

		_themeDisplay = ContentLayoutTestUtil.getThemeDisplay(
			_company, group, layout);

		_themeDisplay.setPortalDomain(serverName);
		_themeDisplay.setPortalURL(
			StringBundler.concat(
				"http://", serverName, ":",
				PortalUtil.getPortalServerPort(false)));

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(WebKeys.LAYOUT, layout);
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		_themeDisplay.setRequest(mockHttpServletRequest);

		_themeDisplay.setServerName(serverName);
		_themeDisplay.setServerPort(PortalUtil.getPortalServerPort(false));
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

			_assertEmptySitemap(guestGroupId, null);
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

			_assertEmptySitemap(_group.getGroupId(), uuid);
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

	private static long _assetCategoryClassNameId;
	private static CompanyConfigurationTemporarySwapper
		_companyConfigurationTemporarySwapper;
	private static long _journalArticleClassNameId;
	private static long _layoutClassNameId;
	private static long _objectEntryClassNameId;

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

	@DeleteAfterTestRun
	private ObjectDefinition _excludedObjectDefinition;

	@Inject
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@DeleteAfterTestRun
	private ObjectDefinition _includedObjectDefinition;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private RedirectEntryLocalService _redirectEntryLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private SAXReader _saxReader;

	private ServiceContext _serviceContext;

	@Inject
	private SitemapManager _sitemapManager;

	@Inject
	private SitemapStorageHelper _sitemapStorageHelper;

	private ThemeDisplay _themeDisplay;

}