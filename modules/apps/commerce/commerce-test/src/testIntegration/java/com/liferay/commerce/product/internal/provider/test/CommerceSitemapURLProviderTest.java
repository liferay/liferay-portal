/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.provider.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.importer.CPFileImporter;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.url.CPFriendlyURL;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReader;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.theme.ThemeDisplayFactory;
import com.liferay.site.provider.SitemapURLProvider;

import jakarta.servlet.http.HttpServletRequest;

import java.io.InputStream;

import java.util.Collections;
import java.util.List;
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
 * @author Alec Sloan
 */
@RunWith(Arquillian.class)
@Sync
public class CommerceSitemapURLProviderTest {

	@ClassRule
	@Rule
	public static AggregateTestRule aggregateTestRule = new AggregateTestRule(
		new LiferayIntegrationTestRule(),
		PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_company = CompanyLocalServiceUtil.getCompany(_group.getCompanyId());

		_user = UserTestUtil.addUser();

		_httpServletRequest = new MockHttpServletRequest();

		_themeDisplay = ThemeDisplayFactory.create();

		_themeDisplay.setCompany(_company);
		_themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));
		_themeDisplay.setPortalDomain(_company.getVirtualHostname());
		_themeDisplay.setPortalURL(_company.getPortalURL(_group.getGroupId()));
		_themeDisplay.setScopeGroupId(_group.getGroupId());
		_themeDisplay.setServerPort(8080);
		_themeDisplay.setSignedIn(true);
		_themeDisplay.setSiteGroupId(_group.getGroupId());
		_themeDisplay.setUser(_user);

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			_company.getCompanyId());

		CommerceChannel commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency.getCode());

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_company.getCompanyId(), _group.getGroupId(), _user.getUserId());

		AccountEntry accountEntry =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				_user.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString() + "@liferay.com",
				RandomTestUtil.randomString(), _serviceContext);

		_httpServletRequest.setAttribute(
			"LIFERAY_SHARED_CURRENT_COMMERCE_ACCOUNT_ID_" +
				commerceChannel.getGroupId(),
			accountEntry.getAccountEntryId());

		_themeDisplay.setRequest(_httpServletRequest);

		Class<?> clazz = CommerceSitemapURLProviderTest.class;

		InputStream inputStream = clazz.getResourceAsStream(
			"dependencies/layouts.json");

		String json = StringUtil.read(inputStream);

		JSONArray jsonArray = _jsonFactory.createJSONArray(json);

		_cpFileImporter.createLayouts(
			jsonArray, CommerceSitemapURLProviderTest.class.getClassLoader(),
			null, _serviceContext);
	}

	@Test
	public void testAssetCategorySitemapURLProvider() throws Exception {
		String title = RandomTestUtil.randomString();

		Map<Locale, String> titleMap = Collections.singletonMap(
			LocaleUtil.getSiteDefault(), title);

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				_serviceContext.getUserId(), _company.getGroupId(),
				_group.getName(_themeDisplay.getLocale()), _serviceContext);

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			null, _serviceContext.getUserId(), assetVocabulary.getGroupId(),
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID, titleMap, null,
			assetVocabulary.getVocabularyId(), new String[0], _serviceContext);

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.addFriendlyURLEntry(
				_company.getGroupId(),
				_portal.getClassNameId(AssetCategory.class),
				assetCategory.getCategoryId(), title, _serviceContext);

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

		LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
			_group.getGroupId(), false);

		long plid = _portal.getPlidFromPortletId(
			layoutSet.getGroupId(), layoutSet.isPrivateLayout(),
			CPPortletKeys.CP_CATEGORY_CONTENT_WEB);

		Layout layout = _layoutLocalService.getLayout(plid);

		_httpServletRequest.setAttribute(WebKeys.LAYOUT, layout);

		_themeDisplay.setLayoutSet(layout.getLayoutSet());

		_assetCategorySitemapURLProvider.visitLayout(
			rootElement, layout.getUuid(), layoutSet, _themeDisplay);

		Assert.assertTrue(rootElement.hasContent());

		List<Node> nodes = rootElement.content();

		Node node = nodes.get(0);

		String currentSiteURL = _portal.getGroupFriendlyURL(
			layout.getLayoutSet(), _themeDisplay, false, false);

		String urlSeparator = _cpFriendlyURL.getAssetCategoryURLSeparator(
			_themeDisplay.getCompanyId());

		String categoryFriendlyURL =
			currentSiteURL + urlSeparator +
				friendlyURLEntry.getUrlTitle(_themeDisplay.getLanguageId());

		Assert.assertTrue(node.hasContent());

		String xml = node.asXML();

		Assert.assertTrue(xml.contains(categoryFriendlyURL));
	}

	@Test
	public void testCPDefinitionSitemapURLProvider() throws Exception {
		CommerceCatalog commerceCatalog = CommerceTestUtil.addCommerceCatalog(
			_company.getCompanyId(), _group.getGroupId(), _user.getUserId(),
			_commerceCurrency.getCode());

		CPInstance cpInstance =
			CPTestUtil.addCPInstanceWithRandomSkuFromCatalog(
				commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

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

		LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
			_group.getGroupId(), false);

		long plid = _portal.getPlidFromPortletId(
			layoutSet.getGroupId(), layoutSet.isPrivateLayout(),
			CPPortletKeys.CP_CONTENT_WEB);

		Layout layout = _layoutLocalService.getLayout(plid);

		_httpServletRequest.setAttribute(WebKeys.LAYOUT, layout);

		_themeDisplay.setLayoutSet(layout.getLayoutSet());

		_cpDefinitionSitemapURLProvider.visitLayout(
			rootElement, layout.getUuid(), layoutSet, _themeDisplay);

		Assert.assertTrue(rootElement.hasContent());

		List<Node> nodes = rootElement.content();

		Node node = nodes.get(0);

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.getMainFriendlyURLEntry(
				_portal.getClassNameId(CProduct.class),
				cpDefinition.getCProductId());

		String currentSiteURL = _portal.getGroupFriendlyURL(
			layout.getLayoutSet(), _themeDisplay, false, false);

		String urlSeparator = _cpFriendlyURL.getProductURLSeparator(
			_themeDisplay.getCompanyId());

		String productFriendlyURL =
			currentSiteURL + urlSeparator +
				friendlyURLEntry.getUrlTitle(_themeDisplay.getLanguageId());

		Assert.assertTrue(node.hasContent());

		String xml = node.asXML();

		Assert.assertTrue(xml.contains(productFriendlyURL));
	}

	private static Company _company;
	private static User _user;

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject(
		filter = "component.name=com.liferay.commerce.product.internal.provider.AssetCategorySitemapURLProvider",
		type = SitemapURLProvider.class
	)
	private SitemapURLProvider _assetCategorySitemapURLProvider;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	private CommerceCurrency _commerceCurrency;

	@Inject(
		filter = "component.name=com.liferay.commerce.product.internal.provider.CPDefinitionSitemapURLProvider",
		type = SitemapURLProvider.class
	)
	private SitemapURLProvider _cpDefinitionSitemapURLProvider;

	@Inject
	private CPFileImporter _cpFileImporter;

	@Inject
	private CPFriendlyURL _cpFriendlyURL;

	@Inject
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	private Group _group;
	private HttpServletRequest _httpServletRequest;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private SAXReader _saxReader;

	private ServiceContext _serviceContext;
	private ThemeDisplay _themeDisplay;

}