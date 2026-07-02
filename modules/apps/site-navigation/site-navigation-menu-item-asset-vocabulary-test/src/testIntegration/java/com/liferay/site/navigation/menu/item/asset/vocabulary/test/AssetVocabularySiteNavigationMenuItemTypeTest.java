/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.menu.item.asset.vocabulary.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.navigation.admin.constants.SiteNavigationAdminPortletKeys;
import com.liferay.site.navigation.constants.SiteNavigationConstants;
import com.liferay.site.navigation.menu.item.layout.constants.SiteNavigationMenuItemTypeConstants;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalService;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalService;
import com.liferay.site.navigation.type.SiteNavigationMenuItemType;
import com.liferay.site.navigation.type.SiteNavigationMenuItemTypeRegistry;

import java.io.File;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class AssetVocabularySiteNavigationMenuItemTypeTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), TestPropsValues.getUserId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);

		_assetVocabulary = _assetVocabularyLocalService.addVocabulary(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), _serviceContext);
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testGetAssetVocabularyFromSiteNavigationMenuItemAcrossSites()
		throws Exception {

		AssetVocabulary assetVocabulary1 =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), _serviceContext);

		Group group = GroupTestUtil.addGroup();

		SiteNavigationMenu siteNavigationMenu =
			_siteNavigationMenuLocalService.addSiteNavigationMenu(
				null, TestPropsValues.getUserId(), group.getGroupId(),
				RandomTestUtil.randomString(),
				SiteNavigationConstants.TYPE_DEFAULT, true, _serviceContext);

		SiteNavigationMenuItem siteNavigationMenuItem =
			_siteNavigationMenuItemLocalService.addSiteNavigationMenuItem(
				null, TestPropsValues.getUserId(), group.getGroupId(),
				siteNavigationMenu.getSiteNavigationMenuId(), 0,
				SiteNavigationMenuItemTypeConstants.ASSET_VOCABULARY,
				UnicodePropertiesBuilder.create(
					true
				).put(
					"externalReferenceCode",
					assetVocabulary1.getExternalReferenceCode()
				).put(
					"scopeExternalReferenceCode",
					_group.getExternalReferenceCode()
				).put(
					"title", assetVocabulary1.getTitle()
				).put(
					"type", "asset-vocabulary"
				).buildString(),
				_serviceContext);

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				siteNavigationMenuItem.getTypeSettings()
			).build();

		String scopeExternalReferenceCode = typeSettingsUnicodeProperties.get(
			"scopeExternalReferenceCode");

		ERCInfoItemIdentifier ercInfoItemIdentifier = new ERCInfoItemIdentifier(
			typeSettingsUnicodeProperties.get("externalReferenceCode"),
			scopeExternalReferenceCode);

		group = _groupLocalService.fetchGroupByExternalReferenceCode(
			ercInfoItemIdentifier.getScopeExternalReferenceCode(),
			siteNavigationMenu.getCompanyId());

		AssetVocabulary assetVocabulary2 =
			_assetVocabularyLocalService.
				fetchAssetVocabularyByExternalReferenceCode(
					ercInfoItemIdentifier.getExternalReferenceCode(),
					group.getGroupId());

		Assert.assertEquals(assetVocabulary1, assetVocabulary2);
	}

	@Test
	public void testGetAssetVocabularySiteNavigationMenuItemFromExportImport()
		throws Exception {

		Locale locale = _portal.getSiteDefaultLocale(_group.getGroupId());

		String name = RandomTestUtil.randomString();

		SiteNavigationMenuItem siteNavigationMenuItem =
			_addSiteNavigationMenuItem(
				_assetVocabulary, locale,
				JSONUtil.put(
					LocaleUtil.toLanguageId(locale), name
				).toString(),
				false);

		SiteNavigationMenu siteNavigationMenu =
			_siteNavigationMenuLocalService.fetchSiteNavigationMenu(
				siteNavigationMenuItem.getSiteNavigationMenuId());

		File larFile = _exportImportLocalService.exportLayoutsAsFile(
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					TestPropsValues.getUserId(),
					ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildExportLayoutSettingsMap(
							TestPropsValues.getUser(), _group.getGroupId(),
							false, new long[0],
							HashMapBuilder.put(
								PortletDataHandlerKeys.PORTLET_DATA,
								new String[] {Boolean.TRUE.toString()}
							).put(
								PortletDataHandlerKeys.PORTLET_DATA + "_" +
									SiteNavigationAdminPortletKeys.
										SITE_NAVIGATION_ADMIN,
								new String[] {Boolean.TRUE.toString()}
							).build())));

		_siteNavigationMenuLocalService.deleteSiteNavigationMenu(
			siteNavigationMenu);

		_assetVocabularyLocalService.deleteAssetVocabulary(_assetVocabulary);

		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					TestPropsValues.getUserId(),
					ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildImportLayoutSettingsMap(
							TestPropsValues.getUser(), _group.getGroupId(),
							false, new long[0],
							HashMapBuilder.put(
								PortletDataHandlerKeys.PORTLET_DATA,
								new String[] {Boolean.TRUE.toString()}
							).put(
								PortletDataHandlerKeys.PORTLET_DATA + "_" +
									SiteNavigationAdminPortletKeys.
										SITE_NAVIGATION_ADMIN,
								new String[] {Boolean.TRUE.toString()}
							).build()));

		_exportImportLocalService.importLayouts(
			exportImportConfiguration, larFile);

		siteNavigationMenu =
			_siteNavigationMenuLocalService.
				fetchSiteNavigationMenuByExternalReferenceCode(
					siteNavigationMenu.getExternalReferenceCode(),
					_group.getGroupId());

		List<SiteNavigationMenuItem> siteNavigationMenuItems =
			_siteNavigationMenuItemLocalService.getSiteNavigationMenuItems(
				siteNavigationMenu.getSiteNavigationMenuId());

		siteNavigationMenuItem = siteNavigationMenuItems.get(0);

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				siteNavigationMenuItem.getTypeSettings()
			).build();

		Assert.assertNotNull(
			typeSettingsUnicodeProperties.get("localizedNames"));

		String updatedName = RandomTestUtil.randomString();

		typeSettingsUnicodeProperties.setProperty(
			"localizedNames",
			JSONUtil.put(
				LocaleUtil.toLanguageId(locale), updatedName
			).toString());

		siteNavigationMenuItem =
			_siteNavigationMenuItemLocalService.updateSiteNavigationMenuItem(
				TestPropsValues.getUserId(),
				siteNavigationMenuItem.getSiteNavigationMenuItemId(),
				typeSettingsUnicodeProperties.toString(),
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		SiteNavigationMenuItemType siteNavigationMenuItemType =
			_siteNavigationMenuItemTypeRegistry.getSiteNavigationMenuItemType(
				SiteNavigationMenuItemTypeConstants.ASSET_VOCABULARY);

		Assert.assertEquals(
			updatedName,
			siteNavigationMenuItemType.getTitle(
				siteNavigationMenuItem, locale));
	}

	@Test
	public void testGetChildrenSiteNavigationMenuItems() throws Exception {
		_addAssetCategory(0);
		_addAssetCategory(0);

		AssetCategory rootAssetCategory = _addAssetCategory(0);

		_addAssetCategory(rootAssetCategory.getCategoryId());
		_addAssetCategory(rootAssetCategory.getCategoryId());

		AssetCategory firstLevelAssetCategory = _addAssetCategory(
			rootAssetCategory.getCategoryId());

		_addAssetCategory(firstLevelAssetCategory.getCategoryId());
		_addAssetCategory(firstLevelAssetCategory.getCategoryId());

		AssetCategory secondLevelAssetCategory = _addAssetCategory(
			firstLevelAssetCategory.getCategoryId());

		_addAssetCategory(secondLevelAssetCategory.getCategoryId());
		_addAssetCategory(secondLevelAssetCategory.getCategoryId());

		AssetCategory thirdLevelAssetCategory = _addAssetCategory(
			secondLevelAssetCategory.getCategoryId());

		_addAssetCategory(thirdLevelAssetCategory.getCategoryId());
		_addAssetCategory(thirdLevelAssetCategory.getCategoryId());
		_addAssetCategory(thirdLevelAssetCategory.getCategoryId());

		Assert.assertEquals(
			15,
			_assetCategoryLocalService.getVocabularyCategoriesCount(
				_assetVocabulary.getVocabularyId()));
		Assert.assertEquals(
			3,
			_assetCategoryLocalService.getChildCategoriesCount(
				rootAssetCategory.getCategoryId()));
		Assert.assertEquals(
			3,
			_assetCategoryLocalService.getChildCategoriesCount(
				firstLevelAssetCategory.getCategoryId()));
		Assert.assertEquals(
			3,
			_assetCategoryLocalService.getChildCategoriesCount(
				secondLevelAssetCategory.getCategoryId()));
		Assert.assertEquals(
			3,
			_assetCategoryLocalService.getChildCategoriesCount(
				thirdLevelAssetCategory.getCategoryId()));

		Locale locale = _portal.getSiteDefaultLocale(_group.getGroupId());

		_assertGetChildrenSiteNavigationMenuItems(
			locale, 0, _addSiteNavigationMenuItem(locale, "{}", false),
			_getThemeDisplay());
	}

	@Test
	public void testGetChildrenSiteNavigationMenuItemsAssetCategoryWithoutViewPermission()
		throws Exception {

		AssetCategory permissionAssetCategory = _addAssetCategory(0);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setAddGuestPermissions(false);

		AssetCategory noPermissionAssetCategory =
			_assetCategoryLocalService.addCategory(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(),
				_assetVocabulary.getVocabularyId(), false, null,
				serviceContext);

		Assert.assertEquals(
			2,
			_assetCategoryLocalService.getVocabularyCategoriesCount(
				_assetVocabulary.getVocabularyId()));

		Locale locale = _portal.getSiteDefaultLocale(_group.getGroupId());

		SiteNavigationMenuItem siteNavigationMenuItem =
			_addSiteNavigationMenuItem(locale, "{}", false);

		ThemeDisplay themeDisplay = _getThemeDisplay();

		_assertGetChildrenSiteNavigationMenuItems(
			ListUtil.fromArray(
				permissionAssetCategory, noPermissionAssetCategory),
			locale, siteNavigationMenuItem, themeDisplay);

		User guestUser = _userLocalService.getGuestUser(_group.getCompanyId());

		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(guestUser));
		themeDisplay.setUser(guestUser);

		_assertGetChildrenSiteNavigationMenuItems(
			ListUtil.fromArray(permissionAssetCategory), locale,
			siteNavigationMenuItem, themeDisplay);
	}

	@Test
	public void testGetChildrenSiteNavigationMenuItemsEmptyAssetVocabulary()
		throws Exception {

		Assert.assertEquals(
			0,
			_assetCategoryLocalService.getVocabularyCategoriesCount(
				_assetVocabulary.getVocabularyId()));

		Locale locale = _portal.getSiteDefaultLocale(_group.getGroupId());

		_assertGetChildrenSiteNavigationMenuItems(
			locale, 0, _addSiteNavigationMenuItem(locale, "{}", false),
			_getThemeDisplay());
	}

	@Test
	public void testGetRegularURLAssetCategoryTypeWithDisplayPageTemplate()
		throws Exception {

		DisplayPageTemplateTestUtil.addDisplayPageTemplate(
			_group.getGroupId(),
			_portal.getClassNameId(AssetCategory.class.getName()), null, true,
			WorkflowConstants.STATUS_APPROVED);

		AssetCategory assetCategory = _addAssetCategory(0);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = _getThemeDisplay();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		SiteNavigationMenuItemType siteNavigationMenuItemType =
			_siteNavigationMenuItemTypeRegistry.getSiteNavigationMenuItemType(
				SiteNavigationMenuItemTypeConstants.ASSET_VOCABULARY);

		SiteNavigationMenuItem assetCategorySiteNavigationMenuItem =
			_getAssetCategorySiteNavigationMenuItem(
				assetCategory, mockHttpServletRequest,
				_portal.getSiteDefaultLocale(_group.getGroupId()),
				siteNavigationMenuItemType);

		Assert.assertEquals(
			_assetDisplayPageFriendlyURLProvider.getFriendlyURL(
				new InfoItemReference(
					AssetCategory.class.getName(),
					new ERCInfoItemIdentifier(
						assetCategory.getExternalReferenceCode(),
						_group.getExternalReferenceCode())),
				themeDisplay),
			siteNavigationMenuItemType.getRegularURL(
				mockHttpServletRequest, assetCategorySiteNavigationMenuItem));
	}

	@Test
	public void testGetRegularURLAssetCategoryTypeWithoutDisplayPageTemplate()
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		SiteNavigationMenuItemType siteNavigationMenuItemType =
			_siteNavigationMenuItemTypeRegistry.getSiteNavigationMenuItemType(
				SiteNavigationMenuItemTypeConstants.ASSET_VOCABULARY);

		Assert.assertEquals(
			StringPool.BLANK,
			siteNavigationMenuItemType.getRegularURL(
				mockHttpServletRequest,
				_getAssetCategorySiteNavigationMenuItem(
					mockHttpServletRequest,
					_portal.getSiteDefaultLocale(_group.getGroupId()),
					siteNavigationMenuItemType)));
	}

	@Test
	public void testGetRegularURLAssetVocabularyType() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		SiteNavigationMenuItemType siteNavigationMenuItemType =
			_siteNavigationMenuItemTypeRegistry.getSiteNavigationMenuItemType(
				SiteNavigationMenuItemTypeConstants.ASSET_VOCABULARY);

		Assert.assertEquals(
			StringPool.BLANK,
			siteNavigationMenuItemType.getRegularURL(
				mockHttpServletRequest,
				_addSiteNavigationMenuItem(
					_portal.getSiteDefaultLocale(_group.getGroupId()), "{}",
					false)));
	}

	@Test
	public void testGetSiteNavigationMenuItemsAssetCategoryType()
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		SiteNavigationMenuItemType siteNavigationMenuItemType =
			_siteNavigationMenuItemTypeRegistry.getSiteNavigationMenuItemType(
				SiteNavigationMenuItemTypeConstants.ASSET_VOCABULARY);

		SiteNavigationMenuItem assetCategorySiteNavigationMenuItem =
			_getAssetCategorySiteNavigationMenuItem(
				mockHttpServletRequest,
				_portal.getSiteDefaultLocale(_group.getGroupId()),
				siteNavigationMenuItemType);

		List<SiteNavigationMenuItem> siteNavigationMenuItems =
			siteNavigationMenuItemType.getSiteNavigationMenuItems(
				mockHttpServletRequest, assetCategorySiteNavigationMenuItem);

		Assert.assertEquals(
			siteNavigationMenuItems.toString(), 1,
			siteNavigationMenuItems.size());

		Assert.assertEquals(
			assetCategorySiteNavigationMenuItem,
			siteNavigationMenuItems.get(0));
	}

	@Test
	public void testGetSiteNavigationMenuItemsAssetVocabularyTypeShowAssetVocabularyLevelDisabled()
		throws Exception {

		_addAssetCategory(0);
		_addAssetCategory(0);
		_addAssetCategory(0);

		List<AssetCategory> assetCategories =
			_assetCategoryLocalService.getVocabularyCategories(
				0, _assetVocabulary.getVocabularyId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			assetCategories.toString(), 3, assetCategories.size());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		Locale locale = _portal.getSiteDefaultLocale(_group.getGroupId());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		SiteNavigationMenuItemType siteNavigationMenuItemType =
			_siteNavigationMenuItemTypeRegistry.getSiteNavigationMenuItemType(
				SiteNavigationMenuItemTypeConstants.ASSET_VOCABULARY);

		SiteNavigationMenuItem assetVocabularySiteNavigationMenuItem =
			_addSiteNavigationMenuItem(locale, "{}", false);

		List<SiteNavigationMenuItem> siteNavigationMenuItems =
			siteNavigationMenuItemType.getSiteNavigationMenuItems(
				mockHttpServletRequest, assetVocabularySiteNavigationMenuItem);

		Assert.assertEquals(
			siteNavigationMenuItems.toString(), 3,
			siteNavigationMenuItems.size());

		for (AssetCategory assetCategory : assetCategories) {
			Assert.assertNotNull(
				_getSiteNavigationMenuItemByCategoryId(
					assetCategory, locale, siteNavigationMenuItems));
		}
	}

	@Test
	public void testGetSiteNavigationMenuItemsAssetVocabularyTypeShowAssetVocabularyLevelEnabled()
		throws Exception {

		_addAssetCategory(0);
		_addAssetCategory(0);
		_addAssetCategory(0);

		Assert.assertEquals(
			3,
			_assetCategoryLocalService.getVocabularyCategoriesCount(
				_assetVocabulary.getVocabularyId()));

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		SiteNavigationMenuItemType siteNavigationMenuItemType =
			_siteNavigationMenuItemTypeRegistry.getSiteNavigationMenuItemType(
				SiteNavigationMenuItemTypeConstants.ASSET_VOCABULARY);

		SiteNavigationMenuItem assetVocabularySiteNavigationMenuItem =
			_addSiteNavigationMenuItem(
				_portal.getSiteDefaultLocale(_group.getGroupId()), "{}", true);

		List<SiteNavigationMenuItem> siteNavigationMenuItems =
			siteNavigationMenuItemType.getSiteNavigationMenuItems(
				mockHttpServletRequest, assetVocabularySiteNavigationMenuItem);

		Assert.assertEquals(
			siteNavigationMenuItems.toString(), 1,
			siteNavigationMenuItems.size());

		Assert.assertEquals(
			assetVocabularySiteNavigationMenuItem,
			siteNavigationMenuItems.get(0));
	}

	@Test
	public void testGetStatusIconEmptyAssetVocabulary() throws Exception {
		Assert.assertEquals(
			0,
			_assetCategoryLocalService.getVocabularyCategoriesCount(
				_assetVocabulary.getVocabularyId()));

		SiteNavigationMenuItemType siteNavigationMenuItemType =
			_siteNavigationMenuItemTypeRegistry.getSiteNavigationMenuItemType(
				SiteNavigationMenuItemTypeConstants.ASSET_VOCABULARY);

		Assert.assertEquals(
			"warning-full",
			siteNavigationMenuItemType.getStatusIcon(
				_addSiteNavigationMenuItem(
					_portal.getSiteDefaultLocale(_group.getGroupId()), "{}",
					false)));
	}

	@Test
	public void testGetStatusIconNotEmptyAssetVocabulary() throws Exception {
		_addAssetCategory(0);

		Assert.assertEquals(
			1,
			_assetCategoryLocalService.getVocabularyCategoriesCount(
				_assetVocabulary.getVocabularyId()));

		SiteNavigationMenuItemType siteNavigationMenuItemType =
			_siteNavigationMenuItemTypeRegistry.getSiteNavigationMenuItemType(
				SiteNavigationMenuItemTypeConstants.ASSET_VOCABULARY);

		Assert.assertEquals(
			StringPool.BLANK,
			siteNavigationMenuItemType.getStatusIcon(
				_addSiteNavigationMenuItem(
					_portal.getSiteDefaultLocale(_group.getGroupId()), "{}",
					false)));
	}

	@Test
	public void testGetTitleAssetCategoryType() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		AssetCategory assetCategory = _addAssetCategory(0);

		SiteNavigationMenuItemType siteNavigationMenuItemType =
			_siteNavigationMenuItemTypeRegistry.getSiteNavigationMenuItemType(
				SiteNavigationMenuItemTypeConstants.ASSET_VOCABULARY);

		Locale locale = _portal.getSiteDefaultLocale(_group.getGroupId());

		SiteNavigationMenuItem assetCategorySiteNavigationMenuItem =
			_getAssetCategorySiteNavigationMenuItem(
				assetCategory, mockHttpServletRequest, locale,
				siteNavigationMenuItemType);

		Assert.assertEquals(
			assetCategory.getTitle(locale),
			siteNavigationMenuItemType.getTitle(
				assetCategorySiteNavigationMenuItem, locale));
	}

	@Test
	public void testGetTitleAssetVocabularyType() throws Exception {
		SiteNavigationMenuItemType siteNavigationMenuItemType =
			_siteNavigationMenuItemTypeRegistry.getSiteNavigationMenuItemType(
				SiteNavigationMenuItemTypeConstants.ASSET_VOCABULARY);

		Locale locale = _portal.getSiteDefaultLocale(_group.getGroupId());

		SiteNavigationMenuItem assetVocabularySiteNavigationMenuItem =
			_addSiteNavigationMenuItem(locale, "{}", false);

		Assert.assertEquals(
			_assetVocabulary.getTitle(locale),
			siteNavigationMenuItemType.getTitle(
				assetVocabularySiteNavigationMenuItem, locale));
	}

	@Test
	public void testGetTitleAssetVocabularyTypeUseCustomName()
		throws Exception {

		SiteNavigationMenuItemType siteNavigationMenuItemType =
			_siteNavigationMenuItemTypeRegistry.getSiteNavigationMenuItemType(
				SiteNavigationMenuItemTypeConstants.ASSET_VOCABULARY);

		Locale locale = _portal.getSiteDefaultLocale(_group.getGroupId());

		String expectedTitle = RandomTestUtil.randomString();

		SiteNavigationMenuItem assetVocabularySiteNavigationMenuItem =
			_addSiteNavigationMenuItem(
				locale,
				JSONUtil.put(
					LocaleUtil.toLanguageId(locale), expectedTitle
				).toString(),
				false);

		Assert.assertEquals(
			expectedTitle,
			siteNavigationMenuItemType.getTitle(
				assetVocabularySiteNavigationMenuItem, locale));
	}

	@Test
	public void testGetTitleAssetVocabularyTypeUseCustomNameNondefaultLocale()
		throws Exception {

		SiteNavigationMenuItemType siteNavigationMenuItemType =
			_siteNavigationMenuItemTypeRegistry.getSiteNavigationMenuItemType(
				SiteNavigationMenuItemTypeConstants.ASSET_VOCABULARY);

		Locale locale = _portal.getSiteDefaultLocale(_group.getGroupId());

		String expectedTitle = RandomTestUtil.randomString();

		SiteNavigationMenuItem assetVocabularySiteNavigationMenuItem =
			_addSiteNavigationMenuItem(
				locale,
				JSONUtil.put(
					LocaleUtil.toLanguageId(locale),
					RandomTestUtil.randomString()
				).put(
					LocaleUtil.toLanguageId(LocaleUtil.SPAIN), expectedTitle
				).toString(),
				false);

		Assert.assertEquals(
			expectedTitle,
			siteNavigationMenuItemType.getTitle(
				assetVocabularySiteNavigationMenuItem, LocaleUtil.SPAIN));
	}

	@Test
	public void testGetTitleAssetVocabularyTypeUseCustomNameNontranslatedLocale()
		throws Exception {

		SiteNavigationMenuItemType siteNavigationMenuItemType =
			_siteNavigationMenuItemTypeRegistry.getSiteNavigationMenuItemType(
				SiteNavigationMenuItemTypeConstants.ASSET_VOCABULARY);

		Locale locale = _portal.getSiteDefaultLocale(_group.getGroupId());

		String expectedTitle = RandomTestUtil.randomString();

		SiteNavigationMenuItem assetVocabularySiteNavigationMenuItem =
			_addSiteNavigationMenuItem(
				locale,
				JSONUtil.put(
					LocaleUtil.toLanguageId(locale), expectedTitle
				).toString(),
				false);

		Assert.assertEquals(
			expectedTitle,
			siteNavigationMenuItemType.getTitle(
				assetVocabularySiteNavigationMenuItem, LocaleUtil.SPAIN));
	}

	@Test
	public void testHasPermission() throws Exception {
		SiteNavigationMenuItemType siteNavigationMenuItemType =
			_siteNavigationMenuItemTypeRegistry.getSiteNavigationMenuItemType(
				SiteNavigationMenuItemTypeConstants.ASSET_VOCABULARY);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setAddGuestPermissions(false);

		SiteNavigationMenuItem siteNavigationMenuItem =
			_addSiteNavigationMenuItem(
				_assetVocabularyLocalService.addVocabulary(
					TestPropsValues.getUserId(), _group.getGroupId(),
					RandomTestUtil.randomString(), serviceContext),
				_portal.getSiteDefaultLocale(_group.getGroupId()), "{}", false);

		Assert.assertTrue(
			siteNavigationMenuItemType.hasPermission(
				PermissionThreadLocal.getPermissionChecker(),
				siteNavigationMenuItem));
		Assert.assertFalse(
			siteNavigationMenuItemType.hasPermission(
				PermissionCheckerFactoryUtil.create(
					_userLocalService.getGuestUser(_group.getCompanyId())),
				siteNavigationMenuItem));
	}

	@Test
	public void testIsBrowsableAssetCategoryTypeWithDisplayPageTemplate()
		throws Exception {

		DisplayPageTemplateTestUtil.addDisplayPageTemplate(
			_group.getGroupId(),
			_portal.getClassNameId(AssetCategory.class.getName()), null, true,
			WorkflowConstants.STATUS_APPROVED);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		SiteNavigationMenuItemType siteNavigationMenuItemType =
			_siteNavigationMenuItemTypeRegistry.getSiteNavigationMenuItemType(
				SiteNavigationMenuItemTypeConstants.ASSET_VOCABULARY);

		Assert.assertTrue(
			siteNavigationMenuItemType.isBrowsable(
				_getAssetCategorySiteNavigationMenuItem(
					mockHttpServletRequest,
					_portal.getSiteDefaultLocale(_group.getGroupId()),
					siteNavigationMenuItemType)));
	}

	@Test
	public void testIsBrowsableAssetCategoryTypeWithoutDisplayPageTemplate()
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		SiteNavigationMenuItemType siteNavigationMenuItemType =
			_siteNavigationMenuItemTypeRegistry.getSiteNavigationMenuItemType(
				SiteNavigationMenuItemTypeConstants.ASSET_VOCABULARY);

		Assert.assertFalse(
			siteNavigationMenuItemType.isBrowsable(
				_getAssetCategorySiteNavigationMenuItem(
					mockHttpServletRequest,
					_portal.getSiteDefaultLocale(_group.getGroupId()),
					siteNavigationMenuItemType)));
	}

	@Test
	public void testIsBrowsableAssetVocabularyType() throws Exception {
		SiteNavigationMenuItemType siteNavigationMenuItemType =
			_siteNavigationMenuItemTypeRegistry.getSiteNavigationMenuItemType(
				SiteNavigationMenuItemTypeConstants.ASSET_VOCABULARY);

		SiteNavigationMenuItem siteNavigationMenuItem =
			_addSiteNavigationMenuItem(
				_portal.getSiteDefaultLocale(_group.getGroupId()), "{}", false);

		Assert.assertFalse(
			siteNavigationMenuItemType.isBrowsable(siteNavigationMenuItem));
	}

	@Test
	public void testRenderEditPage() throws Exception {
		SiteNavigationMenuItemType siteNavigationMenuItemType =
			_siteNavigationMenuItemTypeRegistry.getSiteNavigationMenuItemType(
				SiteNavigationMenuItemTypeConstants.ASSET_VOCABULARY);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			new MockLiferayPortletRenderResponse());
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		Group group = GroupTestUtil.addGroup();

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), group.getGroupId(),
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(
					group.getGroupId(), TestPropsValues.getUserId()));

		SiteNavigationMenu siteNavigationMenu =
			_siteNavigationMenuLocalService.addSiteNavigationMenu(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(),
				SiteNavigationConstants.TYPE_DEFAULT, true, _serviceContext);

		SiteNavigationMenuItem siteNavigationMenuItem =
			_siteNavigationMenuItemLocalService.addSiteNavigationMenuItem(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				siteNavigationMenu.getSiteNavigationMenuId(), 0,
				SiteNavigationMenuItemTypeConstants.ASSET_VOCABULARY,
				UnicodePropertiesBuilder.create(
					true
				).put(
					"externalReferenceCode",
					assetVocabulary.getExternalReferenceCode()
				).put(
					"scopeExternalReferenceCode",
					group.getExternalReferenceCode()
				).put(
					"title", assetVocabulary.getTitle()
				).put(
					"type", "asset-vocabulary"
				).buildString(),
				_serviceContext);

		_groupLocalService.deleteGroup(group);

		siteNavigationMenuItemType.renderEditPage(
			mockHttpServletRequest, new MockHttpServletResponse(),
			siteNavigationMenuItem);
	}

	private AssetCategory _addAssetCategory(long parentAssetCategoryId)
		throws Exception {

		return _assetCategoryLocalService.addCategory(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			parentAssetCategoryId, RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			_assetVocabulary.getVocabularyId(), false, null, _serviceContext);
	}

	private SiteNavigationMenuItem _addSiteNavigationMenuItem(
			AssetVocabulary assetVocabulary, Locale defaultLocale,
			String localizedNames, boolean showAssetVocabularyLevel)
		throws Exception {

		SiteNavigationMenu siteNavigationMenu =
			_siteNavigationMenuLocalService.addSiteNavigationMenu(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(),
				SiteNavigationConstants.TYPE_DEFAULT, true, _serviceContext);

		return _siteNavigationMenuItemLocalService.addSiteNavigationMenuItem(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			siteNavigationMenu.getSiteNavigationMenuId(), 0,
			SiteNavigationMenuItemTypeConstants.ASSET_VOCABULARY,
			UnicodePropertiesBuilder.create(
				true
			).put(
				Field.DEFAULT_LANGUAGE_ID,
				LocaleUtil.toLanguageId(defaultLocale)
			).put(
				"externalReferenceCode",
				assetVocabulary.getExternalReferenceCode()
			).put(
				"localizedNames", localizedNames
			).put(
				"showAssetVocabularyLevel",
				String.valueOf(showAssetVocabularyLevel)
			).put(
				"title", assetVocabulary.getTitle(defaultLocale)
			).put(
				"type", "asset-vocabulary"
			).put(
				"useCustomName",
				String.valueOf(!Objects.equals(localizedNames, "{}"))
			).buildString(),
			_serviceContext);
	}

	private SiteNavigationMenuItem _addSiteNavigationMenuItem(
			Locale defaultLocale, String localizedNames,
			boolean showAssetVocabularyLevel)
		throws Exception {

		return _addSiteNavigationMenuItem(
			_assetVocabulary, defaultLocale, localizedNames,
			showAssetVocabularyLevel);
	}

	private void _assertGetChildrenSiteNavigationMenuItems(
			List<AssetCategory> assetCategories, Locale locale,
			SiteNavigationMenuItem siteNavigationMenuItem,
			ThemeDisplay themeDisplay)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		SiteNavigationMenuItemType siteNavigationMenuItemType =
			_siteNavigationMenuItemTypeRegistry.getSiteNavigationMenuItemType(
				SiteNavigationMenuItemTypeConstants.ASSET_VOCABULARY);

		List<SiteNavigationMenuItem> childrenSiteNavigationMenuItems =
			siteNavigationMenuItemType.getChildrenSiteNavigationMenuItems(
				mockHttpServletRequest, siteNavigationMenuItem);

		Assert.assertEquals(
			childrenSiteNavigationMenuItems.toString(), assetCategories.size(),
			childrenSiteNavigationMenuItems.size());

		for (AssetCategory assetCategory : assetCategories) {
			SiteNavigationMenuItem childrenSiteNavigationMenuItem =
				_getSiteNavigationMenuItemByCategoryId(
					assetCategory, locale, childrenSiteNavigationMenuItems);

			_assertGetChildrenSiteNavigationMenuItems(
				locale, assetCategory.getCategoryId(),
				childrenSiteNavigationMenuItem, themeDisplay);
		}
	}

	private void _assertGetChildrenSiteNavigationMenuItems(
			Locale locale, long parentAssetCategoryId,
			SiteNavigationMenuItem siteNavigationMenuItem,
			ThemeDisplay themeDisplay)
		throws Exception {

		_assertGetChildrenSiteNavigationMenuItems(
			_assetCategoryLocalService.getVocabularyCategories(
				parentAssetCategoryId, _assetVocabulary.getVocabularyId(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null),
			locale, siteNavigationMenuItem, themeDisplay);
	}

	private SiteNavigationMenuItem _getAssetCategorySiteNavigationMenuItem(
			AssetCategory assetCategory,
			MockHttpServletRequest mockHttpServletRequest, Locale locale,
			SiteNavigationMenuItemType siteNavigationMenuItemType)
		throws Exception {

		SiteNavigationMenuItem assetVocabularySiteNavigationMenuItem =
			_addSiteNavigationMenuItem(locale, "{}", false);

		List<SiteNavigationMenuItem> childrenSiteNavigationMenuItems =
			siteNavigationMenuItemType.getChildrenSiteNavigationMenuItems(
				mockHttpServletRequest, assetVocabularySiteNavigationMenuItem);

		Assert.assertEquals(
			childrenSiteNavigationMenuItems.toString(), 1,
			childrenSiteNavigationMenuItems.size());

		SiteNavigationMenuItem assetCategorySiteNavigationMenuItem =
			childrenSiteNavigationMenuItems.get(0);

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				assetCategorySiteNavigationMenuItem.getTypeSettings()
			).build();

		Assert.assertEquals(
			"asset-category", typeSettingsUnicodeProperties.get("type"));
		Assert.assertEquals(
			assetCategory.getExternalReferenceCode(),
			GetterUtil.getString(
				typeSettingsUnicodeProperties.get("externalReferenceCode")));

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.getAssetVocabulary(
				assetCategory.getVocabularyId());

		Assert.assertEquals(
			assetVocabulary.getExternalReferenceCode(),
			GetterUtil.getString(
				typeSettingsUnicodeProperties.get(
					"assetVocabularyExternalReferenceCode")));

		Assert.assertEquals(
			assetCategory.getTitle(locale),
			typeSettingsUnicodeProperties.get("title"));

		return assetCategorySiteNavigationMenuItem;
	}

	private SiteNavigationMenuItem _getAssetCategorySiteNavigationMenuItem(
			MockHttpServletRequest mockHttpServletRequest, Locale locale,
			SiteNavigationMenuItemType siteNavigationMenuItemType)
		throws Exception {

		AssetCategory assetCategory = _addAssetCategory(0);

		return _getAssetCategorySiteNavigationMenuItem(
			assetCategory, mockHttpServletRequest, locale,
			siteNavigationMenuItemType);
	}

	private SiteNavigationMenuItem _getSiteNavigationMenuItemByCategoryId(
			AssetCategory assetCategory, Locale locale,
			List<SiteNavigationMenuItem> siteNavigationMenuItems)
		throws Exception {

		SiteNavigationMenuItem assetCategorySiteNavigationMenuItem = null;

		for (SiteNavigationMenuItem siteNavigationMenuItem :
				siteNavigationMenuItems) {

			UnicodeProperties typeSettingsUnicodeProperties =
				UnicodePropertiesBuilder.fastLoad(
					siteNavigationMenuItem.getTypeSettings()
				).build();

			if (!Objects.equals(
					typeSettingsUnicodeProperties.get("type"),
					"asset-category") ||
				!Objects.equals(
					assetCategory.getExternalReferenceCode(),
					GetterUtil.getString(
						typeSettingsUnicodeProperties.get(
							"externalReferenceCode")))) {

				continue;
			}

			AssetVocabulary assetVocabulary =
				_assetVocabularyLocalService.getAssetVocabulary(
					assetCategory.getVocabularyId());

			Assert.assertEquals(
				assetVocabulary.getExternalReferenceCode(),
				GetterUtil.getString(
					typeSettingsUnicodeProperties.get(
						"assetVocabularyExternalReferenceCode")));

			Assert.assertEquals(
				assetCategory.getTitle(locale),
				typeSettingsUnicodeProperties.get("title"));

			assetCategorySiteNavigationMenuItem = siteNavigationMenuItem;

			break;
		}

		Assert.assertNotNull(assetCategorySiteNavigationMenuItem);

		return assetCategorySiteNavigationMenuItem;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;

	@DeleteAfterTestRun
	private AssetVocabulary _assetVocabulary;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@Inject
	private ExportImportLocalService _exportImportLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private Portal _portal;

	private ServiceContext _serviceContext;

	@Inject
	private SiteNavigationMenuItemLocalService
		_siteNavigationMenuItemLocalService;

	@Inject
	private SiteNavigationMenuItemTypeRegistry
		_siteNavigationMenuItemTypeRegistry;

	@Inject
	private SiteNavigationMenuLocalService _siteNavigationMenuLocalService;

	@Inject
	private UserLocalService _userLocalService;

}