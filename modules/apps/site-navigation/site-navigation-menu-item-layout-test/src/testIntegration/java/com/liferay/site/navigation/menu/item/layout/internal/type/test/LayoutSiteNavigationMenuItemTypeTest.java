/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.menu.item.layout.internal.type.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
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

import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class LayoutSiteNavigationMenuItemTypeTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setScopeGroupId(_group.getGroupId());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), TestPropsValues.getUserId());

		_serviceContext.setRequest(mockHttpServletRequest);

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);

		_siteNavigationMenu =
			_siteNavigationMenuLocalService.addSiteNavigationMenu(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				"Primary Menu", SiteNavigationConstants.TYPE_PRIMARY, true,
				_serviceContext);
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testAddToAutoMenuFalseToMenu() throws PortalException {
		_layoutService.addLayout(
			null, _group.getGroupId(), false, 0,
			HashMapBuilder.put(
				LocaleUtil.getSiteDefault(), "welcome"
			).build(),
			new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(),
			LayoutConstants.TYPE_PORTLET,
			UnicodePropertiesBuilder.put(
				"addToAutoMenus", Boolean.FALSE.toString()
			).buildString(),
			false, new HashMap<>(), _serviceContext);

		List<SiteNavigationMenuItem> siteNavigationMenuItems =
			_siteNavigationMenuItemLocalService.getSiteNavigationMenuItems(
				_siteNavigationMenu.getSiteNavigationMenuId());

		Assert.assertEquals(
			siteNavigationMenuItems.toString(), 0,
			siteNavigationMenuItems.size());
	}

	@Test
	public void testAddToAutoMenuTrueToMenu() throws PortalException {
		SiteNavigationMenu autoSiteNavigationMenu =
			_siteNavigationMenuLocalService.addSiteNavigationMenu(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				"Auto Menu", SiteNavigationConstants.TYPE_DEFAULT, true,
				_serviceContext);

		_layoutService.addLayout(
			null, _group.getGroupId(), false, 0,
			HashMapBuilder.put(
				LocaleUtil.getSiteDefault(), "welcome"
			).build(),
			new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(),
			LayoutConstants.TYPE_PORTLET,
			UnicodePropertiesBuilder.put(
				"siteNavigationMenuId",
				StringUtil.merge(
					new long[] {
						autoSiteNavigationMenu.getSiteNavigationMenuId(),
						_siteNavigationMenu.getSiteNavigationMenuId()
					})
			).buildString(),
			false, new HashMap<>(), _serviceContext);

		List<SiteNavigationMenuItem> autoSiteNavigationMenuItems =
			_siteNavigationMenuItemLocalService.getSiteNavigationMenuItems(
				autoSiteNavigationMenu.getSiteNavigationMenuId());

		Assert.assertEquals(
			autoSiteNavigationMenuItems.toString(), 1,
			autoSiteNavigationMenuItems.size());

		List<SiteNavigationMenuItem> siteNavigationMenuItems =
			_siteNavigationMenuItemLocalService.getSiteNavigationMenuItems(
				_siteNavigationMenu.getSiteNavigationMenuId());

		Assert.assertEquals(
			siteNavigationMenuItems.toString(), 1,
			siteNavigationMenuItems.size());
	}

	@Test
	public void testAddToAutoMenuTrueToPrimaryMenu() throws PortalException {
		_layoutService.addLayout(
			null, _group.getGroupId(), false, 0,
			HashMapBuilder.put(
				LocaleUtil.getSiteDefault(), "welcome"
			).build(),
			new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(),
			LayoutConstants.TYPE_PORTLET,
			UnicodePropertiesBuilder.put(
				"siteNavigationMenuId",
				StringUtil.merge(
					new long[] {_siteNavigationMenu.getSiteNavigationMenuId()})
			).buildString(),
			false, new HashMap<>(), _serviceContext);

		List<SiteNavigationMenuItem> siteNavigationMenuItems =
			_siteNavigationMenuItemLocalService.getSiteNavigationMenuItems(
				_siteNavigationMenu.getSiteNavigationMenuId());

		Assert.assertEquals(
			siteNavigationMenuItems.toString(), 1,
			siteNavigationMenuItems.size());

		SiteNavigationMenuItem siteNavigationMenuItem =
			siteNavigationMenuItems.get(0);

		Assert.assertEquals("welcome", siteNavigationMenuItem.getName());
	}

	@Test
	public void testGetLayout() throws Exception {
		_assertGetLayout(null);
		_assertGetLayout(LayoutTestUtil.addTypePortletLayout(_group));
	}

	@Test
	public void testGetLayoutSiteNavigationMenuItemFromExportImport()
		throws Exception {

		SiteNavigationMenu siteNavigationMenu =
			_siteNavigationMenuLocalService.addSiteNavigationMenu(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(),
				SiteNavigationConstants.TYPE_DEFAULT, true, _serviceContext);

		Layout layout = _layoutService.addLayout(
			null, _group.getGroupId(), false, 0,
			HashMapBuilder.put(
				LocaleUtil.getSiteDefault(), "welcome"
			).build(),
			new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(),
			LayoutConstants.TYPE_PORTLET,
			UnicodePropertiesBuilder.put(
				"siteNavigationMenuId",
				StringUtil.merge(
					new long[] {_siteNavigationMenu.getSiteNavigationMenuId()})
			).buildString(),
			false, new HashMap<>(), _serviceContext);

		_siteNavigationMenuItemLocalService.addSiteNavigationMenuItem(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			siteNavigationMenu.getSiteNavigationMenuId(), 0,
			SiteNavigationMenuItemTypeConstants.LAYOUT,
			UnicodePropertiesBuilder.create(
				true
			).put(
				"externalReferenceCode", layout.getExternalReferenceCode()
			).put(
				"privateLayout", false
			).put(
				"title", RandomTestUtil.randomString()
			).buildString(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

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

		_layoutLocalService.deleteLayout(layout);

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

		SiteNavigationMenuItem siteNavigationMenuItem =
			siteNavigationMenuItems.get(0);

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				siteNavigationMenuItem.getTypeSettings()
			).build();

		Assert.assertNotNull(
			typeSettingsUnicodeProperties.get("externalReferenceCode"));
		Assert.assertNotNull(typeSettingsUnicodeProperties.get("title"));

		String updatedLayoutName = RandomTestUtil.randomString();

		typeSettingsUnicodeProperties.setProperty("title", updatedLayoutName);

		siteNavigationMenuItem =
			_siteNavigationMenuItemLocalService.updateSiteNavigationMenuItem(
				TestPropsValues.getUserId(),
				siteNavigationMenuItem.getSiteNavigationMenuItemId(),
				typeSettingsUnicodeProperties.toString(),
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		Assert.assertEquals(
			siteNavigationMenuItem.getName(), updatedLayoutName);
	}

	@Test
	public void testHasPermission() throws Exception {
		SiteNavigationMenuItemType siteNavigationMenuItemType =
			_siteNavigationMenuItemTypeRegistry.getSiteNavigationMenuItemType(
				SiteNavigationMenuItemTypeConstants.LAYOUT);

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		SiteNavigationMenuItem siteNavigationMenuItem =
			_siteNavigationMenuItemLocalService.addSiteNavigationMenuItem(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				_siteNavigationMenu.getSiteNavigationMenuId(), 0,
				SiteNavigationMenuItemTypeConstants.LAYOUT,
				UnicodePropertiesBuilder.create(
					true
				).put(
					"externalReferenceCode", layout.getExternalReferenceCode()
				).buildString(),
				_serviceContext);

		Assert.assertTrue(
			siteNavigationMenuItemType.hasPermission(
				PermissionThreadLocal.getPermissionChecker(),
				siteNavigationMenuItem));

		_layoutLocalService.deleteLayout(layout);

		Assert.assertFalse(
			siteNavigationMenuItemType.hasPermission(
				PermissionThreadLocal.getPermissionChecker(),
				siteNavigationMenuItem));
	}

	private void _assertGetLayout(Layout layout) throws Exception {
		SiteNavigationMenuItemType siteNavigationMenuItemType =
			_siteNavigationMenuItemTypeRegistry.getSiteNavigationMenuItemType(
				SiteNavigationMenuItemTypeConstants.LAYOUT);

		String externalReferenceCode = RandomTestUtil.randomString();

		if (layout != null) {
			externalReferenceCode = layout.getExternalReferenceCode();
		}

		SiteNavigationMenuItem siteNavigationMenuItem =
			_siteNavigationMenuItemLocalService.addSiteNavigationMenuItem(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				_siteNavigationMenu.getSiteNavigationMenuId(), 0,
				SiteNavigationMenuItemTypeConstants.LAYOUT,
				UnicodePropertiesBuilder.create(
					true
				).put(
					"externalReferenceCode", externalReferenceCode
				).put(
					"privateLayout", false
				).put(
					"title", RandomTestUtil.randomString()
				).buildString(),
				_serviceContext);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.site.navigation.menu.item.layout.internal.type." +
					"LayoutSiteNavigationMenuItemType",
				LoggerTestUtil.DEBUG)) {

			Assert.assertEquals(
				layout,
				siteNavigationMenuItemType.getLayout(siteNavigationMenuItem));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			if (layout != null) {
				Assert.assertEquals(
					logEntries.toString(), 0, logEntries.size());

				return;
			}

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				StringBundler.concat(
					"No layout found for site navigation menu item ID ",
					siteNavigationMenuItem.getSiteNavigationMenuItemId(),
					" with external reference code ", externalReferenceCode,
					" and group ID ", _group.getGroupId()),
				logEntry.getMessage());
		}
	}

	@Inject
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@Inject
	private ExportImportLocalService _exportImportLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutService _layoutService;

	private ServiceContext _serviceContext;
	private SiteNavigationMenu _siteNavigationMenu;

	@Inject
	private SiteNavigationMenuItemLocalService
		_siteNavigationMenuItemLocalService;

	@Inject
	private SiteNavigationMenuItemTypeRegistry
		_siteNavigationMenuItemTypeRegistry;

	@Inject
	private SiteNavigationMenuLocalService _siteNavigationMenuLocalService;

}