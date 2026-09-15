/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.site.manager.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.product.importer.CPFileImporter;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReader;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.theme.ThemeDisplayFactory;
import com.liferay.site.constants.SitemapConstants;
import com.liferay.site.manager.SitemapManager;
import com.liferay.site.storage.helper.SitemapStorageHelper;

import java.time.OffsetDateTime;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Cheryl Tang
 */
@RunWith(Arquillian.class)
@Sync
public class CommerceSitemapManagerTest {

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

		Class<?> clazz = CommerceSitemapManagerTest.class;

		_cpFileImporter.createLayouts(
			_jsonFactory.createJSONArray(
				StringUtil.read(
					clazz.getResourceAsStream(
						"dependencies/cp-content-layouts.json"))),
			clazz.getClassLoader(), null,
			ServiceContextTestUtil.getServiceContext(
				_company.getCompanyId(), _group.getGroupId(),
				_user.getUserId()));

		_themeDisplay = ThemeDisplayFactory.create();

		_themeDisplay.setCompany(_company);
		_themeDisplay.setLayoutSet(
			_layoutSetLocalService.getLayoutSet(_group.getGroupId(), false));
		_themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));
		_themeDisplay.setPortalDomain(_company.getVirtualHostname());
		_themeDisplay.setPortalURL(_company.getPortalURL(_group.getGroupId()));
		_themeDisplay.setRequest(new MockHttpServletRequest());
		_themeDisplay.setScopeGroupId(_group.getGroupId());
		_themeDisplay.setServerPort(PortalUtil.getPortalServerPort(false));
		_themeDisplay.setSiteGroupId(_group.getGroupId());
		_themeDisplay.setUser(_user);
	}

	@Test
	public void testGetSitemapByAssetTypeEmitsEachCPDefinitionOnce()
		throws Exception {

		_addCommerceChannel();

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					_getCompanyConfigurationTemporarySwapper(false)) {

			CPDefinition cpDefinition = _addCPDefinition();

			FriendlyURLEntry friendlyURLEntry =
				_friendlyURLEntryLocalService.getMainFriendlyURLEntry(
					_classNameLocalService.getClassNameId(CProduct.class),
					cpDefinition.getCProductId());

			String xml = _getAssetTypeSitemap();

			Assert.assertEquals(
				xml, 1,
				StringUtil.count(
					xml, friendlyURLEntry.getUrlTitle() + "</loc>"));
		}
	}

	@Test
	public void testGetSitemapByAssetTypeWithoutCommerceChannel()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					_getCompanyConfigurationTemporarySwapper(false)) {

			Assert.assertNull(_getAssetTypeSitemap());
		}
	}

	@Test
	public void testGetSitemapIndexByAssetTypeEmitsLastmod() throws Exception {
		_addCommerceChannel();

		_addCPDefinition();

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					_getCompanyConfigurationTemporarySwapper(false)) {

			Document document = _saxReader.read(
				_sitemapManager.getSitemap(
					_group.getGroupId(), false, _themeDisplay));

			Element rootElement = document.getRootElement();

			String sitemapFileName =
				"sitemap-" + SitemapConstants.ASSET_TYPE_KEY_COMMERCE_PRODUCTS +
					".xml";

			Element sitemapElement = null;

			for (Element element : rootElement.elements()) {
				String loc = element.elementText("loc");

				if (loc.contains(sitemapFileName)) {
					sitemapElement = element;

					break;
				}
			}

			OffsetDateTime.parse(sitemapElement.elementText("lastmod"));
		}
	}

	@Test
	public void testRegenerateSitemapPaginatesCPDefinitions() throws Exception {
		_addCommerceChannel();

		for (int i = 0; i < 3; i++) {
			_addCPDefinition();
		}

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					_getCompanyConfigurationTemporarySwapper(true)) {

			ReflectionTestUtil.setFieldValue(
				_sitemapManager, "_maximumEntries", 1);

			try {
				_sitemapManager.regenerateSitemap(
					SitemapConstants.ASSET_TYPE_KEY_COMMERCE_PRODUCTS,
					_company.getCompanyId(), _group.getGroupId());

				Assert.assertTrue(
					_sitemapStorageHelper.hasSitemapFile(
						_company.getCompanyId(), _group.getGroupId(),
						SitemapConstants.ASSET_TYPE_KEY_COMMERCE_PRODUCTS, 2));
			}
			finally {
				ReflectionTestUtil.setFieldValue(
					_sitemapManager, "_maximumEntries",
					SitemapManager.MAXIMUM_ENTRIES);

				_sitemapStorageHelper.deleteSitemaps(
					_company.getCompanyId(), _group.getGroupId());
			}
		}
	}

	private CPDefinition _addCPDefinition() throws Exception {
		CPInstance cpInstance =
			CPTestUtil.addCPInstanceWithRandomSkuFromCatalog(
				_commerceCatalog.getGroupId());

		return cpInstance.getCPDefinition();
	}

	private void _addCommerceChannel() throws Exception {
		CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency.getCode());
	}

	private String _getAssetTypeSitemap() throws Exception {
		return _sitemapManager.getSitemap(
			_classNameLocalService.getClassNameId(CPDefinition.class), null,
			_group.getGroupId(), false, _themeDisplay);
	}

	private CompanyConfigurationTemporarySwapper
			_getCompanyConfigurationTemporarySwapper(
				boolean cachedGenerationEnabled)
		throws Exception {

		return new CompanyConfigurationTemporarySwapper(
			_company.getCompanyId(),
			"com.liferay.site.internal.configuration." +
				"SitemapCompanyConfiguration",
			HashMapDictionaryBuilder.<String, Object>put(
				"cachedGenerationEnabled", cachedGenerationEnabled
			).put(
				"xmlSitemapIndexEnabled", true
			).put(
				"xmlSitemapIndexMode", SitemapConstants.INDEX_MODE_ASSET_TYPE
			).build());
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	private CommerceCatalog _commerceCatalog;

	@DeleteAfterTestRun
	private CommerceCurrency _commerceCurrency;

	private Company _company;

	@Inject
	private CPFileImporter _cpFileImporter;

	@Inject
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	@Inject
	private SAXReader _saxReader;

	@Inject
	private SitemapManager _sitemapManager;

	@Inject
	private SitemapStorageHelper _sitemapStorageHelper;

	private ThemeDisplay _themeDisplay;

	@DeleteAfterTestRun
	private User _user;

}