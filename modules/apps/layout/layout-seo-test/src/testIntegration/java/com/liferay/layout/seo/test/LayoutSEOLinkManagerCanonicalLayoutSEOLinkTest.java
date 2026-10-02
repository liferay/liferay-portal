/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.seo.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.asset.display.page.model.AssetDisplayPageEntry;
import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalService;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.info.item.InfoItemReference;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.seo.kernel.LayoutSEOLink;
import com.liferay.layout.seo.kernel.LayoutSEOLinkManager;
import com.liferay.layout.seo.service.LayoutSEOEntryLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutFriendlyURLRandomizerBumper;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.VirtualLayoutConstants;
import com.liferay.portal.kernel.model.impl.VirtualLayout;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Alicia Garcia
 */
@RunWith(Arquillian.class)
public class LayoutSEOLinkManagerCanonicalLayoutSEOLinkTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		LayoutTestUtil.addTypePortletLayout(_group);

		_layout = LayoutTestUtil.addTypePortletLayout(_group);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), TestPropsValues.getUserId());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		_themeDisplay = _getThemeDisplay();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		_themeDisplay.setRequest(mockHttpServletRequest);

		_serviceContext.setRequest(mockHttpServletRequest);

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@FeatureFlag("LPD-57283")
	@Test
	public void testGetAlternateURLsDesignLibraryContainingLayoutFriendlyURL()
		throws Exception {

		Group designLibraryGroup = _addConnectedDesignLibraryGroup();
		String friendlyURL = StringPool.SLASH.concat(
			RandomTestUtil.randomString(
				LayoutFriendlyURLRandomizerBumper.INSTANCE));

		designLibraryGroup = _groupLocalService.updateFriendlyURL(
			designLibraryGroup.getGroupId(), friendlyURL.concat("-designs"));

		String spainFriendlyURL = friendlyURL.concat("-es");

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			designLibraryGroup.getGroupId(), false,
			HashMapBuilder.put(
				LocaleUtil.SPAIN, RandomTestUtil.randomString()
			).put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.SPAIN, spainFriendlyURL
			).put(
				LocaleUtil.US, friendlyURL
			).build());

		_group = GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(), Arrays.asList(LocaleUtil.SPAIN, LocaleUtil.US),
			LocaleUtil.US);

		Layout virtualLayout = new VirtualLayout(layout, _group);

		Map<Locale, String> alternateURLs = _portal.getAlternateURLs(
			_portal.getCanonicalURL(
				RandomTestUtil.randomString(), _themeDisplay, virtualLayout,
				true, false),
			_themeDisplay, virtualLayout,
			Collections.singleton(LocaleUtil.SPAIN));

		String alternateURL = alternateURLs.get(LocaleUtil.SPAIN);

		Assert.assertTrue(
			alternateURL,
			alternateURL.contains(
				StringBundler.concat(
					VirtualLayoutConstants.CANONICAL_URL_SEPARATOR,
					designLibraryGroup.getFriendlyURL(), StringPool.SLASH)));
		Assert.assertTrue(
			alternateURL,
			alternateURL.endsWith(layout.getFriendlyURL(LocaleUtil.SPAIN)));
	}

	@FeatureFlag("LPD-57283")
	@Test
	public void testGetAlternateURLsDesignLibraryDisplayPageMatchesCanonicalURL()
		throws Exception {

		Group designLibraryGroup = _addConnectedDesignLibraryGroup();

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				designLibraryGroup.getGroupId(),
				_portal.getClassNameId(JournalArticle.class.getName()), null,
				true, WorkflowConstants.STATUS_APPROVED);

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		Layout virtualLayout = new VirtualLayout(layout, _group);

		String canonicalURL = _portal.getCanonicalURL(
			RandomTestUtil.randomString(), _themeDisplay, virtualLayout, true,
			false);

		Map<Locale, String> alternateURLs = _portal.getAlternateURLs(
			canonicalURL, _themeDisplay, virtualLayout,
			Collections.singleton(LocaleUtil.getSiteDefault()));

		Assert.assertEquals(
			alternateURLs.toString(), canonicalURL,
			alternateURLs.get(LocaleUtil.getSiteDefault()));
	}

	@Test
	public void testGetCanonicalAssetDisplayPageURL() throws Exception {
		JournalArticle journalArticle = _addJournalArticle();

		AssetDisplayPageEntry assetDisplayPageEntry =
			_addJournalArticleAssetDisplayPageEntry(
				_portal.getClassNameId(JournalArticle.class.getName()),
				journalArticle, AssetDisplayPageConstants.TYPE_SPECIFIC);

		Layout layout = _layoutLocalService.getLayout(
			assetDisplayPageEntry.getPlid());

		ThemeDisplay themeDisplay = _getThemeDisplay(journalArticle, layout);

		String url = _assetDisplayPageFriendlyURLProvider.getFriendlyURL(
			new InfoItemReference(
				JournalArticle.class.getName(),
				journalArticle.getResourcePrimKey()),
			journalArticle, themeDisplay);

		_pushServiceContext(themeDisplay, url);

		_testWithLayoutSEOCompanyConfiguration(
			() -> {
				LayoutSEOLink canonicalLayoutSEOLink =
					_layoutSEOLinkManager.getCanonicalLayoutSEOLink(
						layout, LocaleUtil.getSiteDefault(), url, themeDisplay);

				Assert.assertEquals(url, canonicalLayoutSEOLink.getHref());
			});
	}

	@Test
	public void testGetCanonicalAssetDisplayPageURLCustomDisplayPage()
		throws Exception {

		JournalArticle journalArticle = _addJournalArticle();

		long classNameId = _portal.getClassNameId(
			JournalArticle.class.getName());

		AssetDisplayPageEntry assetDisplayPageEntry =
			_addJournalArticleAssetDisplayPageEntry(
				classNameId, journalArticle,
				AssetDisplayPageConstants.TYPE_SPECIFIC);

		Layout layout = _layoutLocalService.getLayout(
			assetDisplayPageEntry.getPlid());

		ThemeDisplay themeDisplay = _getThemeDisplay(journalArticle, layout);

		String url = _getCustomDisplayPageURL(
			classNameId, journalArticle, layout, themeDisplay);

		_pushServiceContext(themeDisplay, url);

		_testWithLayoutSEOCompanyConfiguration(
			() -> {
				LayoutSEOLink canonicalLayoutSEOLink =
					_layoutSEOLinkManager.getCanonicalLayoutSEOLink(
						layout, LocaleUtil.getSiteDefault(), url, themeDisplay);

				Assert.assertEquals(
					_assetDisplayPageFriendlyURLProvider.getFriendlyURL(
						new InfoItemReference(
							JournalArticle.class.getName(),
							journalArticle.getResourcePrimKey()),
						journalArticle, themeDisplay),
					canonicalLayoutSEOLink.getHref());
			});
	}

	@Test
	public void testGetCanonicalAssetDisplayPageURLCustomDisplayPageNoneAssetDisplayPageEntry()
		throws Exception {

		JournalArticle journalArticle = _addJournalArticle();

		long classNameId = _portal.getClassNameId(
			JournalArticle.class.getName());

		AssetDisplayPageEntry assetDisplayPageEntry =
			_addJournalArticleAssetDisplayPageEntry(
				classNameId, journalArticle,
				AssetDisplayPageConstants.TYPE_NONE);

		Layout layout = _layoutLocalService.getLayout(
			assetDisplayPageEntry.getPlid());

		ThemeDisplay themeDisplay = _getThemeDisplay(journalArticle, layout);

		String url = _getCustomDisplayPageURL(
			classNameId, journalArticle, layout, themeDisplay);

		_pushServiceContext(themeDisplay, url);

		_testWithLayoutSEOCompanyConfiguration(
			() -> {
				LayoutSEOLink canonicalLayoutSEOLink =
					_layoutSEOLinkManager.getCanonicalLayoutSEOLink(
						layout, LocaleUtil.getSiteDefault(), url, themeDisplay);

				Assert.assertEquals(url, canonicalLayoutSEOLink.getHref());
			});
	}

	@Test
	public void testGetCanonicalLayoutURL() throws Exception {
		String canonicalURL = _portal.getCanonicalURL(
			RandomTestUtil.randomString(), _themeDisplay, _layout, false,
			false);

		LayoutSEOLink canonicalLayoutSEOLink =
			_layoutSEOLinkManager.getCanonicalLayoutSEOLink(
				_layout, LocaleUtil.getDefault(), canonicalURL, _themeDisplay);

		Assert.assertEquals(canonicalURL, canonicalLayoutSEOLink.getHref());
	}

	@Test
	public void testGetCanonicalLayoutURLCustomCanonicalURLDisabled()
		throws Exception {

		String canonicalURL = _portal.getCanonicalURL(
			RandomTestUtil.randomString(), _themeDisplay, _layout, false,
			false);

		_layoutSEOEntryLocalService.updateLayoutSEOEntry(
			TestPropsValues.getUserId(), _group.getGroupId(), false,
			_layout.getLayoutId(), false,
			Collections.singletonMap(LocaleUtil.getDefault(), _CANONICAL_URL),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		LayoutSEOLink canonicalLayoutSEOLink =
			_layoutSEOLinkManager.getCanonicalLayoutSEOLink(
				_layout, LocaleUtil.getDefault(), canonicalURL, _themeDisplay);

		Assert.assertEquals(canonicalURL, canonicalLayoutSEOLink.getHref());
	}

	@Test
	public void testGetCanonicalLayoutURLCustomCanonicalURLEnabled()
		throws Exception {

		_layoutSEOEntryLocalService.updateLayoutSEOEntry(
			TestPropsValues.getUserId(), _group.getGroupId(), false,
			_layout.getLayoutId(), true,
			Collections.singletonMap(LocaleUtil.getDefault(), _CANONICAL_URL),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		String canonicalURL = _portal.getCanonicalURL(
			RandomTestUtil.randomString(), _themeDisplay, _layout, true, false);

		LayoutSEOLink canonicalLayoutSEOLink =
			_layoutSEOLinkManager.getCanonicalLayoutSEOLink(
				_layout, LocaleUtil.getDefault(), canonicalURL, _themeDisplay);

		Assert.assertEquals(_CANONICAL_URL, canonicalLayoutSEOLink.getHref());
	}

	@FeatureFlag("LPD-57283")
	@Test
	public void testGetCanonicalLayoutURLDesignLibraryCustomCanonicalURLNotApplied()
		throws Exception {

		Group designLibraryGroup = _addConnectedDesignLibraryGroup();

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				designLibraryGroup.getGroupId(),
				_portal.getClassNameId(JournalArticle.class.getName()), null,
				true, WorkflowConstants.STATUS_APPROVED);

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		_layoutSEOEntryLocalService.updateLayoutSEOEntry(
			TestPropsValues.getUserId(), _group.getGroupId(),
			layout.isPrivateLayout(), layout.getLayoutId(), true,
			Collections.singletonMap(LocaleUtil.getDefault(), _CANONICAL_URL),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Layout virtualLayout = new VirtualLayout(layout, _group);

		String canonicalURL = _portal.getCanonicalURL(
			RandomTestUtil.randomString(), _themeDisplay, virtualLayout, false,
			false);

		LayoutSEOLink canonicalLayoutSEOLink =
			_layoutSEOLinkManager.getCanonicalLayoutSEOLink(
				virtualLayout, LocaleUtil.getDefault(), canonicalURL,
				_themeDisplay);

		Assert.assertEquals(canonicalURL, canonicalLayoutSEOLink.getHref());

		for (LayoutSEOLink layoutSEOLink :
				_layoutSEOLinkManager.getLocalizedLayoutSEOLinks(
					virtualLayout, LocaleUtil.getDefault(), canonicalURL,
					Collections.singleton(LocaleUtil.getDefault()))) {

			Assert.assertNotEquals(_CANONICAL_URL, layoutSEOLink.getHref());
		}
	}

	@Test
	public void testGetCanonicalLayoutURLLocalizedURL() throws Exception {
		String canonicalURL = _portal.getCanonicalURL(
			RandomTestUtil.randomString(), _themeDisplay, _layout, false,
			false);

		Map<Locale, String> alternateURLs = _portal.getAlternateURLs(
			canonicalURL, _themeDisplay, _layout);

		_testWithLayoutSEOCompanyConfiguration(
			() -> {
				LayoutSEOLink canonicalLayoutSEOLink =
					_layoutSEOLinkManager.getCanonicalLayoutSEOLink(
						_layout, LocaleUtil.CHINA, canonicalURL, _themeDisplay);

				Assert.assertEquals(
					alternateURLs.getOrDefault(LocaleUtil.CHINA, canonicalURL),
					canonicalLayoutSEOLink.getHref());
			});
	}

	@FeatureFlag("LPD-57283")
	@Test
	public void testGetCanonicalLayoutURLSharedTemplateCustomCanonicalURLNotApplied()
		throws Exception {

		Group designLibraryGroup = _addConnectedDesignLibraryGroup();

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				designLibraryGroup.getGroupId(),
				_portal.getClassNameId(JournalArticle.class.getName()), null,
				true, WorkflowConstants.STATUS_APPROVED);

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		_layoutSEOEntryLocalService.updateLayoutSEOEntry(
			TestPropsValues.getUserId(), designLibraryGroup.getGroupId(),
			layout.isPrivateLayout(), layout.getLayoutId(), true,
			Collections.singletonMap(LocaleUtil.getDefault(), _CANONICAL_URL),
			ServiceContextTestUtil.getServiceContext(
				designLibraryGroup.getGroupId()));

		Layout virtualLayout = new VirtualLayout(layout, _group);

		String canonicalURL = _portal.getCanonicalURL(
			RandomTestUtil.randomString(), _themeDisplay, virtualLayout, false,
			false);

		LayoutSEOLink canonicalLayoutSEOLink =
			_layoutSEOLinkManager.getCanonicalLayoutSEOLink(
				virtualLayout, LocaleUtil.getDefault(), canonicalURL,
				_themeDisplay);

		Assert.assertEquals(canonicalURL, canonicalLayoutSEOLink.getHref());

		for (LayoutSEOLink layoutSEOLink :
				_layoutSEOLinkManager.getLocalizedLayoutSEOLinks(
					virtualLayout, LocaleUtil.getDefault(), canonicalURL,
					Collections.singleton(LocaleUtil.getDefault()))) {

			Assert.assertNotEquals(_CANONICAL_URL, layoutSEOLink.getHref());
		}
	}

	private Group _addConnectedDesignLibraryGroup() throws Exception {
		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			TestPropsValues.getCompanyId(), true, "LPD-57283");

		_depotEntry = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			DepotConstants.TYPE_DESIGN_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			_depotEntry.getDepotEntryId(), _group.getGroupId());

		return _depotEntry.getGroup();
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
			long classNameId, JournalArticle journalArticle, int type)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(), classNameId,
				journalArticle.getDDMStructureKey(), true,
				WorkflowConstants.STATUS_APPROVED);

		return _assetDisplayPageEntryLocalService.addAssetDisplayPageEntry(
			TestPropsValues.getUserId(), _group.getGroupId(), classNameId,
			journalArticle.getResourcePrimKey(),
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(), type,
			_serviceContext);
	}

	private String _getCustomDisplayPageURL(
			long classNameId, JournalArticle journalArticle, Layout layout,
			ThemeDisplay themeDisplay)
		throws Exception {

		return StringBundler.concat(
			_portal.getGroupFriendlyURL(
				_group.getPublicLayoutSet(), themeDisplay, false, false),
			FriendlyURLResolverConstants.URL_SEPARATOR_X_CUSTOM_ASSET,
			layout.getFriendlyURL(LocaleUtil.getSiteDefault()),
			StringPool.SLASH, classNameId, StringPool.SLASH,
			journalArticle.getResourcePrimKey());
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		Company company = _companyLocalService.getCompany(
			_group.getCompanyId());

		company.setVirtualHostname(_VIRTUAL_HOSTNAME);

		themeDisplay.setCompany(company);

		themeDisplay.setLayoutSet(_layout.getLayoutSet());
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setPlid(_layout.getPlid());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSecure(false);
		themeDisplay.setServerName(_VIRTUAL_HOSTNAME);
		themeDisplay.setServerPort(_SERVER_PORT);
		themeDisplay.setSiteGroupId(_group.getGroupId());

		return themeDisplay;
	}

	private ThemeDisplay _getThemeDisplay(
			JournalArticle journalArticle, Layout layout)
		throws Exception {

		ThemeDisplay themeDisplay = ContentLayoutTestUtil.getThemeDisplay(
			_companyLocalService.getCompany(_group.getCompanyId()), _group,
			layout);

		themeDisplay.setPortalURL(
			"http://localhost:" + PortalUtil.getPortalServerPort(false));

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(WebKeys.LAYOUT, layout);

		LayoutDisplayPageObjectProvider<JournalArticle>
			layoutDisplayPageObjectProvider =
				_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
					_group.getGroupId(),
					journalArticle.getUrlTitle(LocaleUtil.getDefault()));

		Assert.assertNotNull(layoutDisplayPageObjectProvider);

		mockHttpServletRequest.setAttribute(
			LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER,
			layoutDisplayPageObjectProvider);

		themeDisplay.setRequest(mockHttpServletRequest);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return themeDisplay;
	}

	private void _pushServiceContext(ThemeDisplay themeDisplay, String url) {
		themeDisplay.setURLCurrent(url);

		_serviceContext.setRequest(themeDisplay.getRequest());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	private void _testWithLayoutSEOCompanyConfiguration(
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					_LAYOUT_SEO_CONFIGURATION_PID,
					HashMapDictionaryBuilder.<String, Object>put(
						"canonicalURL", "localized-url"
					).build())) {

			unsafeRunnable.run();
		}
	}

	private static final String _CANONICAL_URL = "http://example.com";

	private static final String _LAYOUT_SEO_CONFIGURATION_PID =
		"com.liferay.layout.seo.internal.configuration." +
			"LayoutSEOCompanyConfiguration";

	private static final int _SERVER_PORT = 8080;

	private static final String _VIRTUAL_HOSTNAME = "test.com";

	@Inject
	private AssetDisplayPageEntryLocalService
		_assetDisplayPageEntryLocalService;

	@Inject
	private AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	private Layout _layout;

	@Inject(
		filter = "component.name=com.liferay.journal.web.internal.layout.display.page.JournalArticleLayoutDisplayPageProvider"
	)
	private LayoutDisplayPageProvider<JournalArticle>
		_layoutDisplayPageProvider;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutSEOEntryLocalService _layoutSEOEntryLocalService;

	@Inject
	private LayoutSEOLinkManager _layoutSEOLinkManager;

	@Inject
	private Portal _portal;

	private ServiceContext _serviceContext;
	private ThemeDisplay _themeDisplay;

}