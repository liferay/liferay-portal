/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.menu.web.internal.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationParameterMapFactoryUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.layout.exporter.PortletPreferencesPortletConfigurationExporter;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletConstants;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portlet.display.template.test.util.BaseExportImportTestCase;
import com.liferay.site.navigation.constants.SiteNavigationMenuPortletKeys;
import com.liferay.site.navigation.menu.item.layout.constants.SiteNavigationMenuItemTypeConstants;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalService;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalService;
import com.liferay.site.navigation.test.util.SiteNavigationMenuItemTestUtil;
import com.liferay.site.navigation.test.util.SiteNavigationMenuTestUtil;
import com.liferay.sites.kernel.util.Sites;

import jakarta.portlet.PortletPreferences;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Moral
 */
@RunWith(Arquillian.class)
public class SiteNavigationMenuExportImportTest
	extends BaseExportImportTestCase {

	@Override
	public String getPortletId() throws Exception {
		return PortletIdCodec.encode(
			SiteNavigationMenuPortletKeys.SITE_NAVIGATION_MENU,
			RandomTestUtil.randomString());
	}

	@Test
	public void testExportImport() throws Exception {
		_setUpLocalStaging();

		_layout = LayoutTestUtil.addTypePortletLayout(_stagingGroup);

		_setUpSiteNavigationMenu(_stagingGroup);

		String portletId = LayoutTestUtil.addPortletToLayout(
			_layout, SiteNavigationMenuPortletKeys.SITE_NAVIGATION_MENU,
			HashMapBuilder.put(
				"rootMenuItemExternalReferenceCode",
				new String[] {
					_siteNavigationMenuItem.getExternalReferenceCode()
				}
			).put(
				"siteNavigationMenuExternalReferenceCode",
				new String[] {_siteNavigationMenu.getExternalReferenceCode()}
			).build());

		_publishAllLayouts();

		Layout layout = _layoutLocalService.getLayoutByUuidAndGroupId(
			_layout.getUuid(), _liveGroup.getGroupId(),
			_layout.isPrivateLayout());

		PortletPreferences portletPreferences =
			_portletPreferencesLocalService.getPreferences(
				_liveGroup.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
				portletId);

		Assert.assertEquals(
			_siteNavigationMenu.getExternalReferenceCode(),
			portletPreferences.getValue(
				"siteNavigationMenuExternalReferenceCode", StringPool.BLANK));
		Assert.assertNull(
			portletPreferences.getValue(
				"siteNavigationMenuGroupExternalReferenceCode", null));
		Assert.assertEquals(
			_siteNavigationMenuItem.getExternalReferenceCode(),
			portletPreferences.getValue(
				"rootMenuItemExternalReferenceCode", StringPool.BLANK));
	}

	@Override
	@Test
	public void testExportImportAssetLinks() throws Exception {
	}

	@Test
	public void testExportImportChildLayoutSiteNavigationMenuItems()
		throws Exception {

		_setUpLocalStaging();

		_setUpSiteNavigationMenu(_stagingGroup);

		Layout parentLayout = LayoutTestUtil.addTypePortletLayout(
			_stagingGroup);

		SiteNavigationMenuItem parentSiteNavigationMenuItem =
			SiteNavigationMenuItemTestUtil.addLayoutTypeSiteNavigationMenuItem(
				_siteNavigationMenu, parentLayout, 0L);

		Layout childLayout1 = LayoutTestUtil.addTypePortletLayout(
			_stagingGroup);

		SiteNavigationMenuItem childSiteNavigationMenuItem1 =
			SiteNavigationMenuItemTestUtil.addLayoutTypeSiteNavigationMenuItem(
				_siteNavigationMenu, childLayout1,
				parentSiteNavigationMenuItem.getSiteNavigationMenuItemId());

		Layout childLayout2 = LayoutTestUtil.addTypePortletLayout(
			_stagingGroup);

		SiteNavigationMenuItem childSiteNavigationMenuItem2 =
			SiteNavigationMenuItemTestUtil.addLayoutTypeSiteNavigationMenuItem(
				_siteNavigationMenu, childLayout2,
				childSiteNavigationMenuItem1.getSiteNavigationMenuItemId());

		_publishLayouts(new long[] {childLayout2.getLayoutId()});

		childSiteNavigationMenuItem2 =
			_siteNavigationMenuItemLocalService.
				getSiteNavigationMenuItemByExternalReferenceCode(
					childSiteNavigationMenuItem2.getExternalReferenceCode(),
					_liveGroup.getGroupId());

		childSiteNavigationMenuItem1 =
			_siteNavigationMenuItemLocalService.fetchSiteNavigationMenuItem(
				childSiteNavigationMenuItem2.
					getParentSiteNavigationMenuItemId());

		Assert.assertEquals(
			_liveGroup.getGroupId(), childSiteNavigationMenuItem1.getGroupId());
	}

	@Test
	public void testExportImportEmptyPortletPreferences() throws Exception {
		_setUpLocalStaging();

		_layout = LayoutTestUtil.addTypePortletLayout(_stagingGroup);

		String portletId = LayoutTestUtil.addPortletToLayout(
			_layout, SiteNavigationMenuPortletKeys.SITE_NAVIGATION_MENU,
			HashMapBuilder.put(
				"siteNavigationMenuType", new String[] {"1"}
			).build());

		_publishAllLayouts();

		Layout layout = _layoutLocalService.getLayoutByUuidAndGroupId(
			_layout.getUuid(), _liveGroup.getGroupId(),
			_layout.isPrivateLayout());

		PortletPreferences portletPreferences =
			_portletPreferencesLocalService.getPreferences(
				_liveGroup.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
				portletId);

		Assert.assertEquals(
			"1",
			portletPreferences.getValue(
				"siteNavigationMenuType", StringPool.BLANK));
	}

	@Test
	public void testExportImportGroupEmbeddedPortlet() throws Exception {
		_setUpLocalStaging();

		_layout = LayoutTestUtil.addTypePortletLayout(_stagingGroup);

		_setUpSiteNavigationMenu(_stagingGroup);

		String portletInstanceId = PortletIdCodec.encode(
			SiteNavigationMenuPortletKeys.SITE_NAVIGATION_MENU,
			RandomTestUtil.randomString());

		Portlet portlet = _portletLocalService.getPortletById(
			_stagingGroup.getCompanyId(),
			SiteNavigationMenuPortletKeys.SITE_NAVIGATION_MENU);

		_addGroupEmbeddedPortlet(
			portletInstanceId, portlet,
			_getPortletPreferencesXML(
				"siteNavigationMenuExternalReferenceCode",
				new String[] {_siteNavigationMenu.getExternalReferenceCode()}));

		_publishAllLayouts();

		PortletPreferences portletPreferences =
			_portletPreferencesLocalService.getPreferences(
				_liveGroup.getCompanyId(), _liveGroup.getGroupId(),
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
				PortletKeys.PREFS_PLID_SHARED, portletInstanceId);

		Assert.assertEquals(
			_siteNavigationMenu.getExternalReferenceCode(),
			portletPreferences.getValue(
				"siteNavigationMenuExternalReferenceCode", StringPool.BLANK));
		Assert.assertNull(
			portletPreferences.getValue(
				"rootMenuItemExternalReferenceCode", null));
		Assert.assertNull(
			portletPreferences.getValue(
				"siteNavigationMenuGroupExternalReferenceCode", null));
	}

	@Test
	public void testExportImportWithDeletedSiteNavigationMenu()
		throws Exception {

		_setUpLocalStaging();

		SiteNavigationMenu siteNavigationMenu =
			SiteNavigationMenuTestUtil.addSiteNavigationMenu(_stagingGroup);

		_publishAllLayouts();

		Assert.assertNotNull(
			_siteNavigationMenuLocalService.
				fetchSiteNavigationMenuByExternalReferenceCode(
					siteNavigationMenu.getExternalReferenceCode(),
					_liveGroup.getGroupId()));

		_siteNavigationMenuLocalService.deleteSiteNavigationMenu(
			siteNavigationMenu.getSiteNavigationMenuId());

		_publishAllLayouts();

		Assert.assertNull(
			_siteNavigationMenuLocalService.
				fetchSiteNavigationMenuByExternalReferenceCode(
					siteNavigationMenu.getExternalReferenceCode(),
					_liveGroup.getGroupId()));
	}

	@Test
	public void testExportImportWithDeletedSiteNavigationMenuItem()
		throws Exception {

		_setUpLocalStaging();

		Layout stagingLayout1 = LayoutTestUtil.addTypePortletLayout(
			_stagingGroup);
		Layout stagingLayout2 = LayoutTestUtil.addTypePortletLayout(
			_stagingGroup);

		_setUpSiteNavigationMenu(_stagingGroup);

		SiteNavigationMenuItem layoutItem1 =
			SiteNavigationMenuItemTestUtil.addLayoutTypeSiteNavigationMenuItem(
				_siteNavigationMenu, stagingLayout1, 0L);

		SiteNavigationMenuItem layoutItem2 =
			SiteNavigationMenuItemTestUtil.addLayoutTypeSiteNavigationMenuItem(
				_siteNavigationMenu, stagingLayout2, 0L);

		SiteNavigationMenuItem urlItem =
			SiteNavigationMenuItemTestUtil.addSiteNavigationMenuItem(
				_siteNavigationMenu, SiteNavigationMenuItemTypeConstants.URL,
				UnicodePropertiesBuilder.put(
					"name", "Liferay"
				).put(
					"url", "https://www.liferay.com/"
				).put(
					"useNewTab", Boolean.FALSE.toString()
				).buildString());

		_publishAllLayouts();

		Assert.assertNotNull(
			_siteNavigationMenuItemLocalService.
				fetchSiteNavigationMenuItemByExternalReferenceCode(
					layoutItem1.getExternalReferenceCode(),
					_liveGroup.getGroupId()));
		Assert.assertNotNull(
			_siteNavigationMenuItemLocalService.
				fetchSiteNavigationMenuItemByExternalReferenceCode(
					layoutItem2.getExternalReferenceCode(),
					_liveGroup.getGroupId()));
		Assert.assertNotNull(
			_siteNavigationMenuItemLocalService.
				fetchSiteNavigationMenuItemByExternalReferenceCode(
					urlItem.getExternalReferenceCode(),
					_liveGroup.getGroupId()));

		_siteNavigationMenuItemLocalService.deleteSiteNavigationMenuItem(
			layoutItem1.getSiteNavigationMenuItemId());

		Map<String, String[]> parameterMap =
			ExportImportConfigurationParameterMapFactoryUtil.
				buildFullPublishParameterMap();

		parameterMap.put(
			PortletDataHandlerKeys.DELETIONS,
			new String[] {Boolean.TRUE.toString()});

		StagingUtil.publishLayouts(
			TestPropsValues.getUserId(), _stagingGroup.getGroupId(),
			_liveGroup.getGroupId(), false, parameterMap);

		Assert.assertNull(
			_siteNavigationMenuItemLocalService.
				fetchSiteNavigationMenuItemByExternalReferenceCode(
					layoutItem1.getExternalReferenceCode(),
					_liveGroup.getGroupId()));
		Assert.assertNotNull(
			_siteNavigationMenuItemLocalService.
				fetchSiteNavigationMenuItemByExternalReferenceCode(
					layoutItem2.getExternalReferenceCode(),
					_liveGroup.getGroupId()));
		Assert.assertNotNull(
			_siteNavigationMenuItemLocalService.
				fetchSiteNavigationMenuItemByExternalReferenceCode(
					urlItem.getExternalReferenceCode(),
					_liveGroup.getGroupId()));
	}

	@Test
	public void testExportImportWithLayoutDeletedFromLive() throws Exception {
		_setUpLocalStaging();

		Layout stagingLayout1 = LayoutTestUtil.addTypePortletLayout(
			_stagingGroup);
		Layout stagingLayout2 = LayoutTestUtil.addTypePortletLayout(
			_stagingGroup);
		Layout stagingLayout3 = LayoutTestUtil.addTypePortletLayout(
			_stagingGroup);

		_setUpSiteNavigationMenu(_stagingGroup);

		SiteNavigationMenuItem layoutItem1 =
			SiteNavigationMenuItemTestUtil.addLayoutTypeSiteNavigationMenuItem(
				_siteNavigationMenu, stagingLayout1, 0L);

		SiteNavigationMenuItem layoutItem2 =
			SiteNavigationMenuItemTestUtil.addLayoutTypeSiteNavigationMenuItem(
				_siteNavigationMenu, stagingLayout2, 0L);

		SiteNavigationMenuItem layoutItem3 =
			SiteNavigationMenuItemTestUtil.addLayoutTypeSiteNavigationMenuItem(
				_siteNavigationMenu, stagingLayout3, 0L);

		_publishAllLayouts();

		Layout liveLayout3 = _layoutLocalService.getLayoutByUuidAndGroupId(
			stagingLayout3.getUuid(), _liveGroup.getGroupId(),
			stagingLayout3.isPrivateLayout());

		_layoutLocalService.deleteLayout(liveLayout3);

		_publishLayouts(
			new long[] {
				stagingLayout1.getLayoutId(), stagingLayout2.getLayoutId()
			});

		Assert.assertNotNull(
			_siteNavigationMenuItemLocalService.
				fetchSiteNavigationMenuItemByExternalReferenceCode(
					layoutItem1.getExternalReferenceCode(),
					_liveGroup.getGroupId()));
		Assert.assertNotNull(
			_siteNavigationMenuItemLocalService.
				fetchSiteNavigationMenuItemByExternalReferenceCode(
					layoutItem2.getExternalReferenceCode(),
					_liveGroup.getGroupId()));
		Assert.assertNull(
			_siteNavigationMenuItemLocalService.
				fetchSiteNavigationMenuItemByExternalReferenceCode(
					layoutItem3.getExternalReferenceCode(),
					_liveGroup.getGroupId()));
	}

	@Test
	@TestInfo("LPD-37038")
	public void testExportImportWithSiteNavigationMenuFromDifferentGroup()
		throws Exception {

		_setUpLocalStaging();

		_layout = LayoutTestUtil.addTypePortletLayout(_stagingGroup);

		Group curGroup = GroupTestUtil.addGroup();

		_setUpSiteNavigationMenu(curGroup);

		String portletId = LayoutTestUtil.addPortletToLayout(
			_layout, SiteNavigationMenuPortletKeys.SITE_NAVIGATION_MENU,
			HashMapBuilder.put(
				"rootMenuItemExternalReferenceCode",
				new String[] {
					_siteNavigationMenuItem.getExternalReferenceCode()
				}
			).put(
				"siteNavigationMenuExternalReferenceCode",
				new String[] {_siteNavigationMenu.getExternalReferenceCode()}
			).put(
				"siteNavigationMenuGroupExternalReferenceCode",
				new String[] {curGroup.getExternalReferenceCode()}
			).build());

		_publishAllLayouts();

		Assert.assertNull(
			_siteNavigationMenuLocalService.
				fetchSiteNavigationMenuByExternalReferenceCode(
					_siteNavigationMenu.getExternalReferenceCode(),
					_liveGroup.getGroupId()));

		Layout layout = _layoutLocalService.getLayoutByUuidAndGroupId(
			_layout.getUuid(), _liveGroup.getGroupId(),
			_layout.isPrivateLayout());

		PortletPreferences portletPreferences =
			_portletPreferencesLocalService.getPreferences(
				_liveGroup.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
				portletId);

		Assert.assertEquals(
			_siteNavigationMenu.getExternalReferenceCode(),
			portletPreferences.getValue(
				"siteNavigationMenuExternalReferenceCode", StringPool.BLANK));
		Assert.assertEquals(
			curGroup.getExternalReferenceCode(),
			portletPreferences.getValue(
				"siteNavigationMenuGroupExternalReferenceCode",
				StringPool.BLANK));
		Assert.assertEquals(
			_siteNavigationMenuItem.getExternalReferenceCode(),
			portletPreferences.getValue(
				"rootMenuItemExternalReferenceCode", StringPool.BLANK));
	}

	@Test
	@TestInfo("LPD-98716")
	public void testExportImportWithSiteNavigationMenuFromStagedGroup()
		throws Exception {

		_setUpLocalStaging();

		_layout = LayoutTestUtil.addTypePortletLayout(_stagingGroup);

		_setUpSiteNavigationMenu(_stagingGroup);

		String portletId = LayoutTestUtil.addPortletToLayout(
			_layout, SiteNavigationMenuPortletKeys.SITE_NAVIGATION_MENU,
			HashMapBuilder.put(
				"rootMenuItemExternalReferenceCode",
				new String[] {
					_siteNavigationMenuItem.getExternalReferenceCode()
				}
			).put(
				"siteNavigationMenuExternalReferenceCode",
				new String[] {_siteNavigationMenu.getExternalReferenceCode()}
			).put(
				"siteNavigationMenuGroupExternalReferenceCode",
				new String[] {_stagingGroup.getExternalReferenceCode()}
			).build());

		_publishAllLayouts();

		Layout layout = _layoutLocalService.getLayoutByUuidAndGroupId(
			_layout.getUuid(), _liveGroup.getGroupId(),
			_layout.isPrivateLayout());

		PortletPreferences portletPreferences =
			_portletPreferencesLocalService.getPreferences(
				_liveGroup.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
				portletId);

		Assert.assertEquals(
			_liveGroup.getExternalReferenceCode(),
			portletPreferences.getValue(
				"siteNavigationMenuGroupExternalReferenceCode",
				StringPool.BLANK));
	}

	@Test
	@TestInfo("LPD-98716")
	public void testGetPortletConfigurationWithLocalStaging() throws Exception {
		_setUpLocalStaging();

		_layout = LayoutTestUtil.addTypePortletLayout(_stagingGroup);

		String portletId = LayoutTestUtil.addPortletToLayout(
			_layout, SiteNavigationMenuPortletKeys.SITE_NAVIGATION_MENU,
			HashMapBuilder.put(
				"siteNavigationMenuGroupExternalReferenceCode",
				new String[] {_stagingGroup.getExternalReferenceCode()}
			).build());

		ExportImportThreadLocal.setPortletStagingInProcess(true);

		Map<String, Object> portletConfiguration =
			_portletPreferencesPortletConfigurationExporter.
				getPortletConfiguration(_layout.getPlid(), portletId);

		Assert.assertEquals(
			_liveGroup.getExternalReferenceCode(),
			portletConfiguration.get(
				"siteNavigationMenuGroupExternalReferenceCode"));

		ExportImportThreadLocal.setPortletStagingInProcess(false);
	}

	private void _addGroupEmbeddedPortlet(
		String portletInstanceId, Portlet portlet, String portletPreferences) {

		_portletPreferencesLocalService.addPortletPreferences(
			_stagingGroup.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, _layout.getPlid(),
			portletInstanceId, portlet, PortletConstants.DEFAULT_PREFERENCES);
		_portletPreferencesLocalService.addPortletPreferences(
			_stagingGroup.getCompanyId(), _stagingGroup.getGroupId(),
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, PortletKeys.PREFS_PLID_SHARED,
			portletInstanceId, portlet, portletPreferences);
	}

	private String _getPortletPreferencesXML(String name, String[] values) {
		StringBundler sb = new StringBundler();

		sb.append("<portlet-preferences>");

		if ((name != null) || (values != null)) {
			sb.append("<preference>");

			if (name != null) {
				sb.append("<name>");
				sb.append(name);
				sb.append("</name>");
			}

			if (values != null) {
				for (String value : values) {
					sb.append("<value>");
					sb.append(value);
					sb.append("</value>");
				}
			}

			sb.append("</preference>");
		}

		sb.append("</portlet-preferences>");

		return sb.toString();
	}

	private void _publishAllLayouts() throws Exception {
		Map<String, String[]> parameterMap =
			ExportImportConfigurationParameterMapFactoryUtil.
				buildParameterMap();

		parameterMap.put(
			PortletDataHandlerKeys.PORTLET_DATA,
			new String[] {Boolean.TRUE.toString()});
		parameterMap.put(
			PortletDataHandlerKeys.PORTLET_DATA_ALL,
			new String[] {Boolean.TRUE.toString()});

		StagingUtil.publishLayouts(
			TestPropsValues.getUserId(), _stagingGroup.getGroupId(),
			_liveGroup.getGroupId(), false, parameterMap);
	}

	private void _publishLayouts(long[] layoutIds) throws Exception {
		Map<String, String[]> parameterMap =
			ExportImportConfigurationParameterMapFactoryUtil.
				buildParameterMap();

		parameterMap.put(
			PortletDataHandlerKeys.PORTLET_DATA,
			new String[] {Boolean.TRUE.toString()});
		parameterMap.put(
			PortletDataHandlerKeys.PORTLET_DATA_ALL,
			new String[] {Boolean.TRUE.toString()});

		StagingUtil.publishLayouts(
			TestPropsValues.getUserId(), _stagingGroup.getGroupId(),
			_liveGroup.getGroupId(), false, layoutIds, parameterMap);
	}

	private void _setUpLocalStaging() throws Exception {
		_liveGroup = GroupTestUtil.addGroup();

		GroupTestUtil.enableLocalStaging(
			_liveGroup, TestPropsValues.getUserId());

		_stagingGroup = _liveGroup.getStagingGroup();
	}

	private void _setUpSiteNavigationMenu(Group stagingGroup) throws Exception {
		_siteNavigationMenu = SiteNavigationMenuTestUtil.addSiteNavigationMenu(
			stagingGroup);

		_siteNavigationMenuItem =
			SiteNavigationMenuItemTestUtil.addSiteNavigationMenuItem(
				_siteNavigationMenu);
	}

	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@DeleteAfterTestRun
	private Group _liveGroup;

	@Inject
	private PortletLocalService _portletLocalService;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Inject
	private PortletPreferencesPortletConfigurationExporter
		_portletPreferencesPortletConfigurationExporter;

	private SiteNavigationMenu _siteNavigationMenu;
	private SiteNavigationMenuItem _siteNavigationMenuItem;

	@Inject
	private SiteNavigationMenuItemLocalService
		_siteNavigationMenuItemLocalService;

	@Inject
	private SiteNavigationMenuLocalService _siteNavigationMenuLocalService;

	@Inject
	private Sites _sites;

	private Group _stagingGroup;

}