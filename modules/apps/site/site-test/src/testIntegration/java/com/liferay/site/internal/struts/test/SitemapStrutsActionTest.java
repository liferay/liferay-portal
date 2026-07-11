/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.internal.struts.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalService;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.VirtualHostLocalService;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReader;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.constants.SitemapConstants;
import com.liferay.site.manager.SitemapManager;
import com.liferay.site.storage.helper.SitemapStorageHelper;

import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Jonathan McCann
 */
@RunWith(Arquillian.class)
public class SitemapStrutsActionTest {

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
					"xmlSitemapIndexEnabled", true
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
		if (_group != null) {
			_sitemapStorageHelper.deleteSitemaps(
				TestPropsValues.getCompanyId(), _group.getGroupId());
		}
	}

	@Test
	public void testExecute() throws Exception {
		Group liveGroup = GroupTestUtil.addGroup();

		Group stagingGroup = GroupTestUtil.addGroup();

		stagingGroup.setLiveGroupId(liveGroup.getGroupId());

		stagingGroup = GroupLocalServiceUtil.updateGroup(stagingGroup);

		LayoutSet layoutSet = stagingGroup.getPublicLayoutSet();

		_virtualHostLocalService.updateVirtualHosts(
			stagingGroup.getCompanyId(), layoutSet.getLayoutSetId(),
			TreeMapBuilder.put(
				"virtual", StringPool.BLANK
			).build());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addHeader("Host", "virtual");

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_company.getGuestUser()));

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockHttpServletRequest.setParameter(
			"currentURL",
			"http://localhost:" + PortalUtil.getPortalServerPort(false) +
				"/sitemap.xml");

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_sitemapStrutsAction.execute(
			mockHttpServletRequest, mockHttpServletResponse);

		Assert.assertEquals(500, mockHttpServletResponse.getStatus());

		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));

		mockHttpServletResponse = new MockHttpServletResponse();

		_sitemapStrutsAction.execute(
			mockHttpServletRequest, mockHttpServletResponse);

		Assert.assertEquals(200, mockHttpServletResponse.getStatus());
	}

	@Test
	public void testExecuteReturnsSitemapIndexXMLFromDLStore()
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

			MockHttpServletResponse mockHttpServletResponse = _executeRequest(
				null, null);

			Assert.assertEquals(200, mockHttpServletResponse.getStatus());

			Document document = _saxReader.read(
				mockHttpServletResponse.getContentAsString());

			Element rootElement = document.getRootElement();

			Assert.assertEquals("sitemapindex", rootElement.getName());

			List<Element> elements = rootElement.elements();

			Assert.assertFalse(elements.isEmpty());

			Assert.assertTrue(
				_sitemapStorageHelper.hasSitemapFile(
					TestPropsValues.getCompanyId(), _group.getGroupId()));
		}
	}

	@Test
	public void testExecuteReturnsWebContentSitemapXMLFromDLStore()
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

			Map<Long, String> assetTypeKeys =
				_sitemapManager.getAssetTypeKeys();

			MockHttpServletResponse mockHttpServletResponse = _executeRequest(
				assetTypeKeys.get(
					_portal.getClassNameId(JournalArticle.class.getName())),
				null);

			Assert.assertEquals(200, mockHttpServletResponse.getStatus());

			Document document = _saxReader.read(
				mockHttpServletResponse.getContentAsString());

			Element rootElement = document.getRootElement();

			Assert.assertEquals("urlset", rootElement.getName());

			List<Element> elements = rootElement.elements();

			Assert.assertFalse(elements.isEmpty());

			Assert.assertTrue(
				_sitemapStorageHelper.hasSitemapFile(
					TestPropsValues.getCompanyId(), _group.getGroupId(),
					"web-content", 1));
		}
	}

	@Test
	public void testExecuteWithIndexDisabledDoesNotStore() throws Exception {
		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						_PID_SITEMAP_COMPANY_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"xmlSitemapIndexEnabled", false
						).build())) {

			MockHttpServletResponse mockHttpServletResponse = _executeRequest(
				null, null);

			Assert.assertEquals(200, mockHttpServletResponse.getStatus());

			Assert.assertFalse(
				_sitemapStorageHelper.hasSitemapFile(
					TestPropsValues.getCompanyId(), _group.getGroupId()));
		}
	}

	@Test
	public void testExecuteWithPageLayoutModeDoesNotStore() throws Exception {
		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						_PID_SITEMAP_COMPANY_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"xmlSitemapIndexEnabled", true
						).put(
							"xmlSitemapIndexMode",
							SitemapConstants.INDEX_MODE_PAGE_LAYOUT
						).build())) {

			MockHttpServletResponse mockHttpServletResponse = _executeRequest(
				null, _layout.getUuid());

			Assert.assertEquals(200, mockHttpServletResponse.getStatus());

			Assert.assertFalse(
				_sitemapStorageHelper.hasSitemapFile(
					TestPropsValues.getCompanyId(), _group.getGroupId()));
		}
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

	private void _addJournalArticleAssetDisplayPageEntry(
			JournalArticle journalArticle)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(),
				_portal.getClassNameId(JournalArticle.class.getName()),
				journalArticle.getDDMStructureKey(), true,
				WorkflowConstants.STATUS_APPROVED);

		_assetDisplayPageEntryLocalService.addAssetDisplayPageEntry(
			TestPropsValues.getUserId(), _group.getGroupId(),
			_portal.getClassNameId(JournalArticle.class.getName()),
			journalArticle.getResourcePrimKey(),
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			AssetDisplayPageConstants.TYPE_SPECIFIC, _serviceContext);
	}

	private MockHttpServletResponse _executeRequest(
			String assetTypeKey, String layoutUuid)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(WebKeys.LAYOUT, _layout);
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);
		mockHttpServletRequest.setParameter(
			"groupId", String.valueOf(_group.getGroupId()));

		_themeDisplay.setRequest(mockHttpServletRequest);

		if (assetTypeKey != null) {
			mockHttpServletRequest.setParameter("assetTypeKey", assetTypeKey);
		}

		if (layoutUuid != null) {
			mockHttpServletRequest.setParameter("layoutUuid", layoutUuid);
		}

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_sitemapStrutsAction.execute(
			mockHttpServletRequest, mockHttpServletResponse);

		return mockHttpServletResponse;
	}

	private void _setUpThemeDisplay() throws Exception {
		_themeDisplay = ContentLayoutTestUtil.getThemeDisplay(
			_company, _group, _layout);

		_themeDisplay.setPortalDomain(_company.getVirtualHostname());
		_themeDisplay.setPortalURL(_company.getPortalURL(_group.getGroupId()));
		_themeDisplay.setServerName(_company.getVirtualHostname());
		_themeDisplay.setServerPort(PortalUtil.getPortalServerPort(false));
	}

	private static final String _PID_SITEMAP_COMPANY_CONFIGURATION =
		"com.liferay.site.internal.configuration.SitemapCompanyConfiguration";

	private static CompanyConfigurationTemporarySwapper
		_companyConfigurationTemporarySwapper;

	@Inject
	private AssetDisplayPageEntryLocalService
		_assetDisplayPageEntryLocalService;

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject
	private Portal _portal;

	@Inject
	private SAXReader _saxReader;

	private ServiceContext _serviceContext;

	@Inject
	private SitemapManager _sitemapManager;

	@Inject
	private SitemapStorageHelper _sitemapStorageHelper;

	@Inject(
		filter = "component.name=com.liferay.site.internal.struts.SitemapStrutsAction"
	)
	private StrutsAction _sitemapStrutsAction;

	private ThemeDisplay _themeDisplay;

	@Inject
	private VirtualHostLocalService _virtualHostLocalService;

}