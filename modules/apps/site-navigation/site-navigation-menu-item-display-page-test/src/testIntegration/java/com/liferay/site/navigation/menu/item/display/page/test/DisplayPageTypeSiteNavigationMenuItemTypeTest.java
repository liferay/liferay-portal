/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.menu.item.display.page.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemClassDetails;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.display.page.LayoutDisplayPageMultiSelectionProvider;
import com.liferay.layout.page.template.info.item.capability.DisplayPageInfoItemCapability;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.site.navigation.constants.SiteNavigationConstants;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalService;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalService;
import com.liferay.site.navigation.type.DefaultSiteNavigationMenuItemTypeContext;
import com.liferay.site.navigation.type.SiteNavigationMenuItemType;
import com.liferay.site.navigation.type.SiteNavigationMenuItemTypeContext;
import com.liferay.site.navigation.type.SiteNavigationMenuItemTypeRegistry;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class DisplayPageTypeSiteNavigationMenuItemTypeTest {

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

		_assetVocabulary = _assetVocabularyLocalService.addVocabulary(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), _serviceContext);

		_assetCategory = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), _assetVocabulary.getVocabularyId(),
			_serviceContext);
	}

	@Test
	public void testDisplayPageTypeMultiSelection() throws Exception {
		ServiceRegistration<LayoutDisplayPageMultiSelectionProvider>
			serviceRegistration = null;

		try {
			SiteNavigationMenuItemType siteNavigationMenuItemType =
				_siteNavigationMenuItemTypeRegistry.
					getSiteNavigationMenuItemType(
						JournalArticle.class.getName());

			Assert.assertFalse(siteNavigationMenuItemType.isMultiSelection());

			Bundle bundle = FrameworkUtil.getBundle(
				LayoutDisplayPageMultiSelectionProvider.class);

			BundleContext bundleContext = bundle.getBundleContext();

			serviceRegistration = bundleContext.registerService(
				LayoutDisplayPageMultiSelectionProvider.class,
				new LayoutDisplayPageMultiSelectionProvider() {

					@Override
					public String getClassName() {
						return JournalArticle.class.getName();
					}

					@Override
					public String getPluralLabel(Locale locale) {
						return LanguageUtil.get(locale, "articles");
					}

				},
				new HashMapDictionary<String, String>());

			Assert.assertTrue(siteNavigationMenuItemType.isMultiSelection());
		}
		finally {
			if (serviceRegistration != null) {
				serviceRegistration.unregister();
			}
		}
	}

	@Test
	public void testDisplayPageTypeMultiSelectionCategories() throws Exception {
		SiteNavigationMenuItemType siteNavigationMenuItemType =
			_siteNavigationMenuItemTypeRegistry.getSiteNavigationMenuItemType(
				AssetCategory.class.getName());

		Assert.assertTrue(siteNavigationMenuItemType.isMultiSelection());
	}

	@Test
	public void testGetLabel() throws Exception {
		Locale locale = _portal.getSiteDefaultLocale(_group.getGroupId());

		List<SiteNavigationMenuItemType> siteNavigationMenuItemTypes =
			_siteNavigationMenuItemTypeRegistry.
				getSiteNavigationMenuItemTypes();

		for (SiteNavigationMenuItemType siteNavigationMenuItemType :
				siteNavigationMenuItemTypes) {

			Assert.assertNotNull(siteNavigationMenuItemType.getLabel(locale));
		}
	}

	@Test
	public void testHasPermission() throws Exception {
		SiteNavigationMenuItemType siteNavigationMenuItemType =
			_siteNavigationMenuItemTypeRegistry.getSiteNavigationMenuItemType(
				JournalArticle.class.getName());

		Assert.assertNotNull(siteNavigationMenuItemType);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setAddGuestPermissions(false);

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, serviceContext);

		SiteNavigationMenu siteNavigationMenu =
			_siteNavigationMenuLocalService.addSiteNavigationMenu(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(),
				SiteNavigationConstants.TYPE_DEFAULT, true, _serviceContext);

		SiteNavigationMenuItem siteNavigationMenuItem =
			_siteNavigationMenuItemLocalService.addSiteNavigationMenuItem(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				siteNavigationMenu.getSiteNavigationMenuId(), 0,
				AssetCategory.class.getName(),
				UnicodePropertiesBuilder.put(
					"classNameId",
					_portal.getClassNameId(JournalArticle.class.getName())
				).put(
					"classPK", journalArticle.getResourcePrimKey()
				).buildString(),
				_serviceContext);

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
	public void testIsAvailable() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, false, true, false,
				false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"TestObject", null, "control_panel.sites",
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				false, ObjectDefinitionConstants.SCOPE_SITE,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(), null);

		ObjectField objectField = ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).userId(
				TestPropsValues.getUserId()
			).indexed(
				true
			).indexedAsKeyword(
				true
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"myText"
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).build());

		objectDefinition.setTitleObjectFieldId(objectField.getObjectFieldId());

		objectDefinition = _objectDefinitionLocalService.updateObjectDefinition(
			objectDefinition);

		objectDefinition =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId());

		SiteNavigationMenuItemType siteNavigationMenuItemType =
			_siteNavigationMenuItemTypeRegistry.getSiteNavigationMenuItemType(
				objectDefinition.getClassName());

		SiteNavigationMenuItemTypeContext siteNavigationMenuItemTypeContext =
			new DefaultSiteNavigationMenuItemTypeContext(
				_companyLocalService.getCompany(_group.getCompanyId()));

		Assert.assertTrue(
			siteNavigationMenuItemType.isAvailable(
				siteNavigationMenuItemTypeContext));

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(
					_userLocalService.getGuestUser(_group.getCompanyId())));

			Assert.assertFalse(
				siteNavigationMenuItemType.isAvailable(
					siteNavigationMenuItemTypeContext));
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}
	}

	@Test
	public void testSiteNavigationMenuItemDisplayPageTypes() {
		for (InfoItemClassDetails infoItemClassDetails :
				_infoItemServiceRegistry.getInfoItemClassDetails(
					DisplayPageInfoItemCapability.KEY)) {

			Assert.assertNotNull(
				_siteNavigationMenuItemTypeRegistry.
					getSiteNavigationMenuItemType(
						infoItemClassDetails.getClassName()));
		}
	}

	@Test
	public void testSiteNavigationMenuItemDisplayPageURL() throws Exception {
		DisplayPageTemplateTestUtil.addDisplayPageTemplate(
			_group.getGroupId(),
			_portal.getClassNameId(AssetCategory.class.getName()), 0, true,
			WorkflowConstants.STATUS_APPROVED);

		SiteNavigationMenu siteNavigationMenu =
			_siteNavigationMenuLocalService.addSiteNavigationMenu(
				null, TestPropsValues.getUserId(), _group.getGroupId(), "Menu",
				SiteNavigationConstants.TYPE_DEFAULT, true, _serviceContext);

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.put(
				"classNameId",
				String.valueOf(
					_portal.getClassNameId(AssetCategory.class.getName()))
			).put(
				"classPK", String.valueOf(_assetCategory.getCategoryId())
			).build();

		SiteNavigationMenuItem siteNavigationMenuItem =
			_siteNavigationMenuItemLocalService.addSiteNavigationMenuItem(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				siteNavigationMenu.getSiteNavigationMenuId(), 0,
				AssetCategory.class.getName(),
				typeSettingsUnicodeProperties.toString(), _serviceContext);

		Assert.assertEquals(
			1,
			_siteNavigationMenuItemLocalService.getSiteNavigationMenuItemsCount(
				siteNavigationMenu.getSiteNavigationMenuId()));

		ThemeDisplay themeDisplay = _getThemeDisplay();

		String friendlyURL =
			_assetDisplayPageFriendlyURLProvider.getFriendlyURL(
				new InfoItemReference(
					_portal.getClassName(
						GetterUtil.getLong(
							typeSettingsUnicodeProperties.get("classNameId"))),
					new ClassPKInfoItemIdentifier(
						GetterUtil.getLong(
							typeSettingsUnicodeProperties.get("classPK")))),
				themeDisplay);

		SiteNavigationMenuItemType siteNavigationMenuItemType =
			_siteNavigationMenuItemTypeRegistry.getSiteNavigationMenuItemType(
				siteNavigationMenuItem.getType());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		Assert.assertEquals(
			friendlyURL,
			siteNavigationMenuItemType.getRegularURL(
				mockHttpServletRequest, siteNavigationMenuItem));

		SiteNavigationMenuItemType defaultSiteNavigationMenuItemType =
			new SiteNavigationMenuItemType() {

				@Override
				public String getLabel(Locale locale) {
					return null;
				}

			};

		Assert.assertEquals(
			defaultSiteNavigationMenuItemType.getStatusIcon(
				siteNavigationMenuItem),
			siteNavigationMenuItemType.getStatusIcon(siteNavigationMenuItem));
	}

	@Test
	public void testSiteNavigationMenuItemTitleUseCustomNameDisabled()
		throws Exception {

		Locale locale = _portal.getSiteDefaultLocale(_group.getGroupId());

		SiteNavigationMenuItem siteNavigationMenuItem =
			_createSiteNavigationMenuItem(locale, "{}");

		SiteNavigationMenuItemType siteNavigationMenuItemType =
			_siteNavigationMenuItemTypeRegistry.getSiteNavigationMenuItemType(
				siteNavigationMenuItem.getType());

		Assert.assertEquals(
			_assetCategory.getTitle(locale),
			siteNavigationMenuItemType.getTitle(
				siteNavigationMenuItem, locale));
	}

	@Test
	public void testSiteNavigationMenuItemTitleUsingCustomName()
		throws Exception {

		String expectedTitle = RandomTestUtil.randomString();
		Locale locale = _portal.getSiteDefaultLocale(_group.getGroupId());

		SiteNavigationMenuItem siteNavigationMenuItem =
			_createSiteNavigationMenuItem(
				locale,
				JSONUtil.put(
					LocaleUtil.toLanguageId(locale), expectedTitle
				).toString());

		SiteNavigationMenuItemType siteNavigationMenuItemType =
			_siteNavigationMenuItemTypeRegistry.getSiteNavigationMenuItemType(
				siteNavigationMenuItem.getType());

		Assert.assertEquals(
			expectedTitle,
			siteNavigationMenuItemType.getTitle(
				siteNavigationMenuItem, locale));
	}

	@Test
	public void testSiteNavigationMenuItemTitleUsingCustomNameNondefaultLocale()
		throws Exception {

		String expectedTitle = RandomTestUtil.randomString();
		Locale defaultLocale = _portal.getSiteDefaultLocale(
			_group.getGroupId());

		Locale nondefaultLocale = null;

		for (Locale locale : LanguageUtil.getAvailableLocales()) {
			if (!Objects.equals(defaultLocale, locale)) {
				nondefaultLocale = locale;

				break;
			}
		}

		Assert.assertNotNull(nondefaultLocale);

		SiteNavigationMenuItem siteNavigationMenuItem =
			_createSiteNavigationMenuItem(
				defaultLocale,
				JSONUtil.put(
					LocaleUtil.toLanguageId(defaultLocale),
					RandomTestUtil.randomString()
				).put(
					LocaleUtil.toLanguageId(nondefaultLocale), expectedTitle
				).toString());

		SiteNavigationMenuItemType siteNavigationMenuItemType =
			_siteNavigationMenuItemTypeRegistry.getSiteNavigationMenuItemType(
				siteNavigationMenuItem.getType());

		Assert.assertEquals(
			expectedTitle,
			siteNavigationMenuItemType.getTitle(
				siteNavigationMenuItem, nondefaultLocale));
	}

	@Test
	public void testSiteNavigationMenuItemTitleUsingCustomNameNontranslatedLocale()
		throws Exception {

		String expectedTitle = RandomTestUtil.randomString();
		Locale defaultLocale = _portal.getSiteDefaultLocale(
			_group.getGroupId());

		Locale nontranslatedLocale = null;

		for (Locale locale : LanguageUtil.getAvailableLocales()) {
			if (!Objects.equals(defaultLocale, locale)) {
				nontranslatedLocale = locale;

				break;
			}
		}

		Assert.assertNotNull(nontranslatedLocale);

		SiteNavigationMenuItem siteNavigationMenuItem =
			_createSiteNavigationMenuItem(
				defaultLocale,
				JSONUtil.put(
					LocaleUtil.toLanguageId(defaultLocale), expectedTitle
				).toString());

		SiteNavigationMenuItemType siteNavigationMenuItemType =
			_siteNavigationMenuItemTypeRegistry.getSiteNavigationMenuItemType(
				siteNavigationMenuItem.getType());

		Assert.assertEquals(
			expectedTitle,
			siteNavigationMenuItemType.getTitle(
				siteNavigationMenuItem, nontranslatedLocale));
	}

	@Test
	public void testSiteNavigationMenuItemWithNoDisplayPage() throws Exception {
		SiteNavigationMenu siteNavigationMenu =
			_siteNavigationMenuLocalService.addSiteNavigationMenu(
				null, TestPropsValues.getUserId(), _group.getGroupId(), "Menu",
				SiteNavigationConstants.TYPE_DEFAULT, true, _serviceContext);

		SiteNavigationMenuItem siteNavigationMenuItem =
			_siteNavigationMenuItemLocalService.addSiteNavigationMenuItem(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				siteNavigationMenu.getSiteNavigationMenuId(), 0,
				AssetCategory.class.getName(),
				UnicodePropertiesBuilder.put(
					"classNameId",
					String.valueOf(
						_portal.getClassNameId(AssetCategory.class.getName()))
				).put(
					"classPK", String.valueOf(_assetCategory.getCategoryId())
				).buildString(),
				_serviceContext);

		Assert.assertEquals(
			1,
			_siteNavigationMenuItemLocalService.getSiteNavigationMenuItemsCount(
				siteNavigationMenu.getSiteNavigationMenuId()));

		SiteNavigationMenuItemType siteNavigationMenuItemType =
			_siteNavigationMenuItemTypeRegistry.getSiteNavigationMenuItemType(
				siteNavigationMenuItem.getType());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		Assert.assertEquals(
			StringPool.BLANK,
			siteNavigationMenuItemType.getRegularURL(
				mockHttpServletRequest, siteNavigationMenuItem));

		Assert.assertEquals(
			"warning-full",
			siteNavigationMenuItemType.getStatusIcon(siteNavigationMenuItem));
	}

	private SiteNavigationMenuItem _createSiteNavigationMenuItem(
			Locale defaultLocale, String localizedNames)
		throws Exception {

		SiteNavigationMenu siteNavigationMenu =
			_siteNavigationMenuLocalService.addSiteNavigationMenu(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(),
				SiteNavigationConstants.TYPE_DEFAULT, true, _serviceContext);

		return _siteNavigationMenuItemLocalService.addSiteNavigationMenuItem(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			siteNavigationMenu.getSiteNavigationMenuId(), 0,
			AssetCategory.class.getName(),
			UnicodePropertiesBuilder.create(
				true
			).put(
				Field.DEFAULT_LANGUAGE_ID,
				LocaleUtil.toLanguageId(defaultLocale)
			).put(
				"className", AssetCategory.class.getName()
			).put(
				"classNameId",
				String.valueOf(
					_portal.getClassNameId(AssetCategory.class.getName()))
			).put(
				"classPK", String.valueOf(_assetCategory.getCategoryId())
			).put(
				"localizedNames", localizedNames
			).put(
				"title", _assetCategory.getTitle(defaultLocale)
			).put(
				"type",
				ResourceActionsUtil.getModelResource(
					defaultLocale, AssetCategory.class.getName())
			).put(
				"useCustomName",
				String.valueOf(!Objects.equals(localizedNames, "{}"))
			).buildString(),
			_serviceContext);
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

	private AssetCategory _assetCategory;

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;

	private AssetVocabulary _assetVocabulary;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

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