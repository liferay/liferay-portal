/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.site.provider.test;

import com.liferay.account.model.AccountGroup;
import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.account.service.AccountGroupRelLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.importer.CPFileImporter;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.url.CPFriendlyURL;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.configuration.test.util.GroupConfigurationTemporarySwapper;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.language.Language;
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
import com.liferay.portal.kernel.service.VirtualHostLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.PrefsPropsTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReader;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.theme.ThemeDisplayFactory;
import com.liferay.site.provider.SitemapURLProvider;

import jakarta.servlet.http.HttpServletRequest;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TreeMap;

import org.junit.After;
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

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			_company.getCompanyId());

		_user = UserTestUtil.addUser();

		_commerceCatalog = CommerceTestUtil.addCommerceCatalog(
			_company.getCompanyId(), _group.getGroupId(), _user.getUserId(),
			_commerceCurrency.getCode());

		CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency.getCode());

		_httpServletRequest = new MockHttpServletRequest();

		_themeDisplay = ThemeDisplayFactory.create();

		_themeDisplay.setCompany(_company);
		_themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));
		_themeDisplay.setPortalDomain(_company.getVirtualHostname());
		_themeDisplay.setPortalURL(_company.getPortalURL(_group.getGroupId()));
		_themeDisplay.setRequest(_httpServletRequest);
		_themeDisplay.setScopeGroupId(_group.getGroupId());
		_themeDisplay.setServerPort(PortalUtil.getPortalServerPort(false));
		_themeDisplay.setSignedIn(true);
		_themeDisplay.setSiteGroupId(_group.getGroupId());
		_themeDisplay.setUser(_user);

		Class<?> clazz = CommerceSitemapURLProviderTest.class;

		InputStream inputStream = clazz.getResourceAsStream(
			"dependencies/layouts.json");

		String json = StringUtil.read(inputStream);

		JSONArray jsonArray = _jsonFactory.createJSONArray(json);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_company.getCompanyId(), _group.getGroupId(), _user.getUserId());

		_cpFileImporter.createLayouts(
			jsonArray, CommerceSitemapURLProviderTest.class.getClassLoader(),
			null, _serviceContext);
	}

	@After
	public void tearDown() throws Exception {
		if (_assetVocabulary != null) {
			_assetVocabularyLocalService.deleteVocabulary(_assetVocabulary);
		}
	}

	@Test
	public void testAssetCategorySitemapURLProvider() throws Exception {
		_assetVocabulary = AssetTestUtil.addVocabulary(_company.getGroupId());

		AssetCategory assetCategory = _addAssetCategory(
			_assetVocabulary,
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID);

		Element element = _visitLayout(
			_assetCategorySitemapURLProvider,
			CPPortletKeys.CP_CATEGORY_CONTENT_WEB);

		Assert.assertTrue(element.hasContent());

		List<String> sitemapURLs = _getSitemapURLs(element.elements());

		Assert.assertTrue(
			sitemapURLs.toString(),
			sitemapURLs.contains(_getAssetCategoryFriendlyURL(assetCategory)));
	}

	@Test
	public void testAssetCategorySitemapURLProviderFriendlyURLTranslation()
		throws Exception {

		List<Locale> companyAvailableLocales = new ArrayList<>(
			_language.getCompanyAvailableLocales(_company.getCompanyId()));
		Locale siteDefaultLocale = LocaleUtil.getSiteDefault();

		GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(), companyAvailableLocales, siteDefaultLocale);

		Locale translatedLocale = null;

		for (Locale companyAvailableLocale : companyAvailableLocales) {
			if (!companyAvailableLocale.equals(siteDefaultLocale)) {
				translatedLocale = companyAvailableLocale;

				break;
			}
		}

		Assert.assertNotNull(translatedLocale);

		_assetVocabulary = AssetTestUtil.addVocabulary(_company.getGroupId());

		AssetCategory assetCategory = _addAssetCategory(
			_assetVocabulary,
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID);

		String translatedURLTitle =
			"translated-" +
				StringUtil.toLowerCase(RandomTestUtil.randomString());

		_friendlyURLEntryLocalService.updateFriendlyURLEntryLocalization(
			_friendlyURLEntryLocalService.getMainFriendlyURLEntry(
				_portal.getClassNameId(AssetCategory.class),
				assetCategory.getCategoryId()),
			_language.getLanguageId(translatedLocale), translatedURLTitle);

		Element element = _visitLayout(
			_assetCategorySitemapURLProvider,
			CPPortletKeys.CP_CATEGORY_CONTENT_WEB);

		String assetCategoryFriendlyURL = _getAssetCategoryFriendlyURL(
			assetCategory);
		String assetCategoryURLSeparator =
			_cpFriendlyURL.getAssetCategoryURLSeparator(
				_themeDisplay.getCompanyId());

		List<Element> elements = ListUtil.filter(
			element.elements(),
			curElement -> {
				String locString = curElement.elementText("loc");

				return Objects.equals(locString, assetCategoryFriendlyURL) ||
					   locString.endsWith(
						   assetCategoryURLSeparator + translatedURLTitle);
			});

		List<String> sitemapURLs = _getSitemapURLs(elements);

		Assert.assertEquals(sitemapURLs.toString(), 2, sitemapURLs.size());
		Assert.assertTrue(
			sitemapURLs.toString(),
			sitemapURLs.remove(assetCategoryFriendlyURL));
		Assert.assertNotEquals(
			_getAssetCategoryFriendlyURL(translatedURLTitle),
			sitemapURLs.get(0));

		for (Element curElement : elements) {
			List<String> hrefLangs = _getHrefLangs(curElement);

			Assert.assertTrue(
				hrefLangs.toString(),
				hrefLangs.contains(
					LocaleUtil.toW3cLanguageId(siteDefaultLocale)));
			Assert.assertTrue(
				hrefLangs.toString(),
				hrefLangs.contains(
					LocaleUtil.toW3cLanguageId(translatedLocale)));
			Assert.assertTrue(
				hrefLangs.toString(), hrefLangs.contains("x-default"));
		}
	}

	@Test
	public void testAssetCategorySitemapURLProviderWithChildAssetCategories()
		throws Exception {

		_assetVocabulary = AssetTestUtil.addVocabulary(_company.getGroupId());

		AssetCategory assetCategory = _addAssetCategory(
			_assetVocabulary,
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID);

		AssetCategory childAssetCategory = _addAssetCategory(
			_assetVocabulary, assetCategory.getCategoryId());

		Element element = _visitLayout(
			_assetCategorySitemapURLProvider,
			CPPortletKeys.CP_CATEGORY_CONTENT_WEB);

		List<String> sitemapURLs = _getSitemapURLs(element.elements());

		Assert.assertTrue(
			sitemapURLs.toString(),
			sitemapURLs.contains(_getAssetCategoryFriendlyURL(assetCategory)));
		Assert.assertTrue(
			sitemapURLs.toString(),
			sitemapURLs.contains(
				_getAssetCategoryFriendlyURL(childAssetCategory)));
	}

	@Test
	public void testCPDefinitionSitemapURLProvider() throws Exception {
		CPDefinition cpDefinition = _addCPDefinition();

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.getMainFriendlyURLEntry(
				_portal.getClassNameId(CProduct.class),
				cpDefinition.getCProductId());

		String productFriendlyURL = StringBundler.concat(
			_portal.getGroupFriendlyURL(
				_layoutSetLocalService.getLayoutSet(_group.getGroupId(), false),
				_themeDisplay, false, false),
			_cpFriendlyURL.getProductURLSeparator(_themeDisplay.getCompanyId()),
			friendlyURLEntry.getUrlTitle(_themeDisplay.getLanguageId()));

		Element element = _visitLayout(
			_cpDefinitionSitemapURLProvider, CPPortletKeys.CP_CONTENT_WEB);

		String xml = element.asXML();

		Assert.assertTrue(
			xml.contains(
				"hreflang=\"" +
					LocaleUtil.toW3cLanguageId(_themeDisplay.getLocale()) +
						"\""));
		Assert.assertTrue(xml.contains("hreflang=\"x-default\""));
		Assert.assertTrue(xml.contains(productFriendlyURL));
		Assert.assertTrue(xml.contains("rel=\"alternate\""));
	}

	@Test
	public void testCPDefinitionSitemapURLProviderGetModifiedDate()
		throws Exception {

		CPDefinition cpDefinition1 = _addCPDefinition();

		cpDefinition1.setModifiedDate(
			new Date(System.currentTimeMillis() - Time.DAY));

		_cpDefinitionLocalService.updateCPDefinition(cpDefinition1);

		CPDefinition cpDefinition2 = _addCPDefinition();

		Assert.assertEquals(
			cpDefinition2.getModifiedDate(),
			_cpDefinitionSitemapURLProvider.getModifiedDate(
				_company.getCompanyId(), _group.getGroupId()));
	}

	@Test
	public void testCPDefinitionSitemapURLProviderIsInclude() throws Exception {
		Assert.assertTrue(
			_cpDefinitionSitemapURLProvider.isInclude(
				_company.getCompanyId(), _group.getGroupId()));

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						_company.getCompanyId(),
						"com.liferay.site.internal.configuration." +
							"SitemapCompanyConfiguration",
						HashMapDictionaryBuilder.<String, Object>put(
							"includeCommerceProducts", false
						).build())) {

			Assert.assertFalse(
				_cpDefinitionSitemapURLProvider.isInclude(
					_company.getCompanyId(), _group.getGroupId()));
		}

		try (GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						_group.getGroupId(),
						"com.liferay.site.internal.configuration." +
							"SitemapGroupConfiguration",
						HashMapDictionaryBuilder.<String, Object>put(
							"includeCommerceProducts", false
						).build())) {

			Assert.assertFalse(
				_cpDefinitionSitemapURLProvider.isInclude(
					_company.getCompanyId(), _group.getGroupId()));
		}
	}

	@Test
	public void testCPDefinitionSitemapURLProviderReflectsTranslatedFriendlyURL()
		throws Exception {

		List<Locale> companyAvailableLocales = new ArrayList<>(
			_language.getCompanyAvailableLocales(_company.getCompanyId()));

		GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(), companyAvailableLocales,
			companyAvailableLocales.get(0));

		CPDefinition cpDefinition = _addCPDefinition();

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.getMainFriendlyURLEntry(
				_portal.getClassNameId(CProduct.class),
				cpDefinition.getCProductId());

		String translatedURLTitle =
			"translated-" +
				StringUtil.toLowerCase(RandomTestUtil.randomString());

		_friendlyURLEntryLocalService.updateFriendlyURLEntryLocalization(
			friendlyURLEntry,
			_language.getLanguageId(companyAvailableLocales.get(1)),
			translatedURLTitle);

		Element element = _visitLayout(
			_cpDefinitionSitemapURLProvider, CPPortletKeys.CP_CONTENT_WEB);

		String xml = element.asXML();

		Assert.assertTrue(xml.contains(translatedURLTitle));
	}

	@Test
	public void testCPDefinitionSitemapURLProviderVisitLayoutSet()
		throws Exception {

		_addCPDefinition();

		Element layoutElement = _visitLayout(
			_cpDefinitionSitemapURLProvider, CPPortletKeys.CP_CONTENT_WEB);
		Element layoutSetElement = _visitLayoutSet();

		Assert.assertEquals(layoutElement.asXML(), layoutSetElement.asXML());
	}

	@Test
	public void testCPDefinitionSitemapURLProviderVisitLayoutSetExcludesDraftCPDefinitions()
		throws Exception {

		CPDefinition cpDefinition = _addCPDefinition();
		CPDefinition draftCPDefinition = _addDraftCPDefinition();

		Element element = _visitLayoutSet();

		String xml = element.asXML();

		Assert.assertTrue(xml.contains(_getURLTitle(cpDefinition)));
		Assert.assertFalse(xml.contains(_getURLTitle(draftCPDefinition)));
	}

	@Test
	public void testCPDefinitionSitemapURLProviderVisitLayoutSetOrdersByCPDefinitionId()
		throws Exception {

		CPDefinition cpDefinition1 = _addCPDefinition("B");
		CPDefinition cpDefinition2 = _addCPDefinition("A");

		Element element = _visitLayoutSet();

		String xml = element.asXML();

		int index1 = xml.indexOf(_getURLTitle(cpDefinition1) + "</loc>");
		int index2 = xml.indexOf(_getURLTitle(cpDefinition2) + "</loc>");

		Assert.assertTrue((index1 >= 0) && (index1 < index2));
	}

	@Test
	public void testCPDefinitionSitemapURLProviderVisitLayoutSetWithDefaultDisplayPageTemplate()
		throws Exception {

		_layoutLocalService.deleteLayout(
			_portal.getPlidFromPortletId(
				_group.getGroupId(), false, CPPortletKeys.CP_CONTENT_WEB));

		String urlTitle = _getURLTitle(_addCPDefinition());

		Element element = _visitLayoutSet();

		String xml = element.asXML();

		Assert.assertFalse(xml.contains(urlTitle));

		DisplayPageTemplateTestUtil.addDisplayPageTemplate(
			_group.getGroupId(), _portal.getClassNameId(CPDefinition.class),
			null, true, WorkflowConstants.STATUS_APPROVED);

		element = _visitLayoutSet();

		xml = element.asXML();

		Assert.assertTrue(xml.contains(urlTitle));
	}

	@Test
	public void testCPDefinitionSitemapURLProviderVisitLayoutSetWithMultipleBatches()
		throws Exception {

		List<CPDefinition> cpDefinitions = new ArrayList<>();

		for (int i = 0; i < 3; i++) {
			cpDefinitions.add(_addCPDefinition());
		}

		Element element = _visitLayoutSet();

		String xml = element.asXML();

		for (CPDefinition cpDefinition : cpDefinitions) {
			Assert.assertTrue(
				xml.contains(_getURLTitle(cpDefinition) + "</loc>"));
		}

		int batchSize = ReflectionTestUtil.getFieldValue(
			_cpDefinitionSitemapURLProvider, "_batchSize");

		ReflectionTestUtil.setFieldValue(
			_cpDefinitionSitemapURLProvider, "_batchSize", 1);

		try {
			element = _visitLayoutSet();

			Assert.assertEquals(xml, element.asXML());
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_cpDefinitionSitemapURLProvider, "_batchSize", batchSize);
		}
	}

	@FeatureFlag("LPD-109588")
	@Test
	public void testCPDefinitionSitemapURLProviderWhenGuestBrowsingIsDisabled()
		throws Exception {

		CPDefinition cpDefinition = _addCPDefinition();

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.getMainFriendlyURLEntry(
				_portal.getClassNameId(CProduct.class),
				cpDefinition.getCProductId());

		CommerceTestUtil.runWithGuestBrowsingDisabled(
			_commerceChannelLocalService.getCommerceChannelGroupIdBySiteGroupId(
				_group.getGroupId()),
			() -> {
				Element element = _visitLayout(
					_cpDefinitionSitemapURLProvider,
					CPPortletKeys.CP_CONTENT_WEB);

				String xml = element.asXML();

				Assert.assertFalse(
					xml.contains(
						friendlyURLEntry.getUrlTitle(
							_themeDisplay.getLanguageId())));
			});
	}

	@Test
	public void testCPDefinitionSitemapURLProviderWithGuestAccountGroup()
		throws Exception {

		CPDefinition cpDefinition = _addCPDefinition();

		cpDefinition.setAccountGroupFilterEnabled(true);

		cpDefinition = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition);

		AccountGroup accountGroup =
			_accountGroupLocalService.checkGuestAccountGroup(
				_company.getCompanyId());

		_accountGroupRelLocalService.addAccountGroupRel(
			accountGroup.getAccountGroupId(), CPDefinition.class.getName(),
			cpDefinition.getCPDefinitionId());

		Assert.assertEquals(
			cpDefinition.getModifiedDate(),
			_cpDefinitionSitemapURLProvider.getModifiedDate(
				_company.getCompanyId(), _group.getGroupId()));

		Element element = _visitLayoutSet();

		String xml = element.asXML();

		Assert.assertTrue(xml.contains(_getURLTitle(cpDefinition)));
	}

	@Test
	public void testCPDefinitionSitemapURLProviderWithLocalePrependedFriendlyURLStyle()
		throws Exception {

		_addCPDefinition();

		try (SafeCloseable safeCloseable =
				PrefsPropsTestUtil.swapWithSafeCloseable(
					_company.getCompanyId(),
					PropsKeys.LOCALE_PREPEND_FRIENDLY_URL_STYLE, "2")) {

			Element element = _visitLayout(
				_cpDefinitionSitemapURLProvider, CPPortletKeys.CP_CONTENT_WEB);

			String xml = element.asXML();

			Assert.assertTrue(
				xml.contains("/" + LocaleUtil.US.getLanguage() + "/"));
		}
	}

	@Test
	public void testCPDefinitionSitemapURLProviderWithVirtualHost()
		throws Exception {

		LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
			_group.getGroupId(), false);

		_virtualHostLocalService.updateVirtualHosts(
			_company.getCompanyId(), layoutSet.getLayoutSetId(),
			TreeMapBuilder.put(
				StringBundler.concat(
					"www.", RandomTestUtil.randomString(), ".test"),
				StringPool.BLANK
			).build());

		try {
			_addCPDefinition();

			try (SafeCloseable safeCloseable =
					PrefsPropsTestUtil.swapWithSafeCloseable(
						_company.getCompanyId(),
						PropsKeys.LOCALE_PREPEND_FRIENDLY_URL_STYLE, "2")) {

				Element element = _visitLayout(
					_cpDefinitionSitemapURLProvider,
					CPPortletKeys.CP_CONTENT_WEB);

				String xml = element.asXML();

				Assert.assertFalse(xml.matches("(?s).*://[^/]*//.*"));
			}
		}
		finally {
			_virtualHostLocalService.updateVirtualHosts(
				_company.getCompanyId(), layoutSet.getLayoutSetId(),
				new TreeMap<>());
		}
	}

	private AssetCategory _addAssetCategory(
			AssetVocabulary assetVocabulary, long parentAssetCategoryId)
		throws Exception {

		AssetCategory assetCategory = AssetTestUtil.addCategory(
			assetVocabulary.getGroupId(), assetVocabulary.getVocabularyId(),
			parentAssetCategoryId);

		_friendlyURLEntryLocalService.addFriendlyURLEntry(
			_company.getGroupId(), _portal.getClassNameId(AssetCategory.class),
			assetCategory.getCategoryId(),
			assetCategory.getTitle(LocaleUtil.getSiteDefault()),
			_serviceContext);

		return assetCategory;
	}

	private CPDefinition _addCPDefinition() throws Exception {
		CPInstance cpInstance =
			CPTestUtil.addCPInstanceWithRandomSkuFromCatalog(
				_commerceCatalog.getGroupId());

		return cpInstance.getCPDefinition();
	}

	private CPDefinition _addCPDefinition(String name) throws Exception {
		CPDefinition cpDefinition = _addCPDefinition();

		_cpDefinitionLocalService.updateCPDefinitionLocalization(
			cpDefinition, cpDefinition.getDefaultLanguageId(), StringPool.BLANK,
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, name,
			StringPool.BLANK);

		return cpDefinition;
	}

	private CPDefinition _addDraftCPDefinition() throws Exception {
		CPDefinition cpDefinition = _addCPDefinition();

		cpDefinition.setStatus(WorkflowConstants.STATUS_DRAFT);

		return _cpDefinitionLocalService.updateCPDefinition(cpDefinition);
	}

	private Element _createURLSetElement() {
		Document document = _saxReader.createDocument();

		document.setXMLEncoding("UTF-8");

		Element element = document.addElement(
			"urlset", "http://www.sitemaps.org/schemas/sitemap/0.9");

		element.addAttribute(
			"xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		element.addAttribute(
			"xsi:schemaLocation",
			"http://www.w3.org/1999/xhtml " +
				"http://www.w3.org/2002/08/xhtml/xhtml1-strict.xsd");
		element.addAttribute("xmlns:xhtml", "http://www.w3.org/1999/xhtml");

		return element;
	}

	private String _getAssetCategoryFriendlyURL(AssetCategory assetCategory)
		throws Exception {

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.getMainFriendlyURLEntry(
				_portal.getClassNameId(AssetCategory.class),
				assetCategory.getCategoryId());

		return _getAssetCategoryFriendlyURL(
			friendlyURLEntry.getUrlTitle(_themeDisplay.getLanguageId()));
	}

	private String _getAssetCategoryFriendlyURL(String urlTitle)
		throws Exception {

		return StringBundler.concat(
			_portal.getGroupFriendlyURL(
				_layoutSetLocalService.getLayoutSet(_group.getGroupId(), false),
				_themeDisplay, false, false),
			_cpFriendlyURL.getAssetCategoryURLSeparator(
				_themeDisplay.getCompanyId()),
			urlTitle);
	}

	private List<String> _getHrefLangs(Element urlElement) {
		return TransformUtil.transform(
			urlElement.elements(),
			childElement -> {
				String elementName = childElement.getName();

				if (elementName.equals("link")) {
					return childElement.attributeValue("hreflang");
				}

				return null;
			});
	}

	private List<String> _getSitemapURLs(List<Element> elements) {
		return TransformUtil.transform(
			elements, element -> element.elementText("loc"));
	}

	private String _getURLTitle(CPDefinition cpDefinition) throws Exception {
		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.getMainFriendlyURLEntry(
				_portal.getClassNameId(CProduct.class),
				cpDefinition.getCProductId());

		return friendlyURLEntry.getUrlTitle();
	}

	private Element _visitLayout(
			SitemapURLProvider sitemapURLProvider, String portletId)
		throws Exception {

		Element element = _createURLSetElement();

		LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
			_group.getGroupId(), false);

		Layout layout = _layoutLocalService.getLayout(
			_portal.getPlidFromPortletId(
				layoutSet.getGroupId(), layoutSet.isPrivateLayout(),
				portletId));

		_httpServletRequest.setAttribute(WebKeys.LAYOUT, layout);

		_themeDisplay.setLayoutSet(layout.getLayoutSet());

		sitemapURLProvider.visitLayout(
			element, layout.getUuid(), layoutSet, _themeDisplay);

		return element;
	}

	private Element _visitLayoutSet() throws Exception {
		Element element = _createURLSetElement();

		LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
			_group.getGroupId(), false);

		_themeDisplay.setLayoutSet(layoutSet);

		_cpDefinitionSitemapURLProvider.visitLayoutSet(
			element, layoutSet, _themeDisplay);

		return element;
	}

	@Inject
	private AccountGroupLocalService _accountGroupLocalService;

	@Inject
	private AccountGroupRelLocalService _accountGroupRelLocalService;

	@Inject(
		filter = "component.name=com.liferay.commerce.product.internal.site.provider.AssetCategorySitemapURLProvider",
		type = SitemapURLProvider.class
	)
	private SitemapURLProvider _assetCategorySitemapURLProvider;

	private AssetVocabulary _assetVocabulary;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	private CommerceCatalog _commerceCatalog;

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	private CommerceCurrency _commerceCurrency;
	private Company _company;

	@Inject
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Inject(
		filter = "component.name=com.liferay.commerce.product.internal.site.provider.CPDefinitionSitemapURLProvider",
		type = SitemapURLProvider.class
	)
	private SitemapURLProvider _cpDefinitionSitemapURLProvider;

	@Inject
	private CPFileImporter _cpFileImporter;

	@Inject
	private CPFriendlyURL _cpFriendlyURL;

	@Inject
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private HttpServletRequest _httpServletRequest;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private Language _language;

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
	private User _user;

	@Inject
	private VirtualHostLocalService _virtualHostLocalService;

}