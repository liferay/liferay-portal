/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.menu.web.internal.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.staging.MergeLayoutPrototypesThreadLocal;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.navigation.constants.SiteNavigationMenuPortletKeys;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.test.util.SiteNavigationMenuTestUtil;
import com.liferay.sites.kernel.util.Sites;

import jakarta.portlet.PortletPreferences;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Moral
 * @author Jonathan McCann
 */
@RunWith(Arquillian.class)
public class SiteNavigationMenuPropagationTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layoutSetPrototype = LayoutTestUtil.addLayoutSetPrototype(
			RandomTestUtil.randomString());

		Group layoutSetPrototypeGroup = _layoutSetPrototype.getGroup();

		_prototypeLayout = LayoutTestUtil.addTypePortletLayout(
			layoutSetPrototypeGroup, true);

		MergeLayoutPrototypesThreadLocal.clearMergeComplete();

		_sites.updateLayoutSetPrototypesLinks(
			_group, _layoutSetPrototype.getLayoutSetPrototypeId(), 0, true,
			true);

		_layout = _layoutLocalService.getFriendlyURLLayout(
			_group.getGroupId(), false, _prototypeLayout.getFriendlyURL());

		_layout.setLayoutPrototypeUuid(_prototypeLayout.getUuid());
		_layout.setLayoutPrototypeLinkEnabled(true);

		_layout = _layoutLocalService.updateLayout(_layout);

		_siteNavigationMenu1 = SiteNavigationMenuTestUtil.addSiteNavigationMenu(
			layoutSetPrototypeGroup, RandomTestUtil.randomString());

		_siteNavigationMenu2 = SiteNavigationMenuTestUtil.addSiteNavigationMenu(
			layoutSetPrototypeGroup, RandomTestUtil.randomString());

		SiteNavigationMenuTestUtil.addSiteNavigationMenu(
			_siteNavigationMenu1.getExternalReferenceCode(), _group,
			_siteNavigationMenu1.getName());

		_portletId = _addSiteNavigationMenuWidgetToPage(
			_siteNavigationMenu1.getExternalReferenceCode());

		_propagateLayout();
	}

	@Test
	public void testSiteTemplatePropagationWhenDuplicateSiteNavigationMenusExist()
		throws Exception {

		String name = RandomTestUtil.randomString();

		SiteNavigationMenuTestUtil.addSiteNavigationMenu(_group, name);

		Group layoutSetPrototypeGroup = _layoutSetPrototype.getGroup();

		SiteNavigationMenu siteNavigationMenu =
			SiteNavigationMenuTestUtil.addSiteNavigationMenu(
				layoutSetPrototypeGroup, name);

		_addSiteNavigationMenuWidgetToPage(
			siteNavigationMenu.getExternalReferenceCode());

		_propagateLayout();

		LayoutSet layoutSet = _layoutSetPrototype.getLayoutSet();

		UnicodeProperties layoutSetPrototypeSettingsUnicodeProperties =
			layoutSet.getSettingsProperties();

		Assert.assertEquals(
			0,
			GetterUtil.getInteger(
				layoutSetPrototypeSettingsUnicodeProperties.getProperty(
					Sites.MERGE_FAIL_COUNT)));
	}

	@Test
	public void testSiteTemplatePropagationWhenSiteNavigationMenuDoesNotExist()
		throws Exception {

		LayoutTestUtil.updateLayoutPortletPreference(
			_prototypeLayout, _portletId,
			"siteNavigationMenuExternalReferenceCode",
			_siteNavigationMenu2.getExternalReferenceCode());

		_propagateLayout();

		_assertSiteNavigationMenuExternalReferenceCode(
			_siteNavigationMenu1.getExternalReferenceCode(), StringPool.BLANK);
	}

	@Test
	public void testSiteTemplatePropagationWithDifferentSiteNavigationMenu()
		throws Exception {

		SiteNavigationMenuTestUtil.addSiteNavigationMenu(
			_siteNavigationMenu2.getExternalReferenceCode(), _group,
			_siteNavigationMenu2.getName());

		LayoutTestUtil.updateLayoutPortletPreference(
			_prototypeLayout, _portletId,
			"siteNavigationMenuExternalReferenceCode",
			_siteNavigationMenu2.getExternalReferenceCode());

		_propagateLayout();

		_assertSiteNavigationMenuExternalReferenceCode(
			_siteNavigationMenu2.getExternalReferenceCode(), StringPool.BLANK);
	}

	@Test
	public void testSiteTemplatePropagationWithGlobalSiteNavigationMenu()
		throws Exception {

		Company company = _companyLocalService.getCompany(
			_group.getCompanyId());

		Group group = company.getGroup();

		SiteNavigationMenu siteNavigationMenu =
			SiteNavigationMenuTestUtil.addSiteNavigationMenu(
				group, RandomTestUtil.randomString());

		LayoutTestUtil.updateLayoutPortletPreference(
			_prototypeLayout, _portletId,
			"siteNavigationMenuExternalReferenceCode",
			siteNavigationMenu.getExternalReferenceCode());

		LayoutTestUtil.updateLayoutPortletPreference(
			_prototypeLayout, _portletId,
			"siteNavigationMenuGroupExternalReferenceCode",
			group.getExternalReferenceCode());

		_propagateLayout();

		_assertSiteNavigationMenuExternalReferenceCode(
			siteNavigationMenu.getExternalReferenceCode(),
			group.getExternalReferenceCode());
	}

	@Test
	public void testSiteTemplatePropagationWithRemovedSiteNavigationMenu()
		throws Exception {

		LayoutTestUtil.updateLayoutPortletPreference(
			_prototypeLayout, _portletId,
			"siteNavigationMenuExternalReferenceCode", StringPool.BLANK);

		_propagateLayout();

		_assertSiteNavigationMenuExternalReferenceCode(
			StringPool.BLANK, StringPool.BLANK);
	}

	private String _addSiteNavigationMenuWidgetToPage(
			String siteNavigationMenuExternalReferenceCode)
		throws Exception {

		return LayoutTestUtil.addPortletToLayout(
			_prototypeLayout,
			SiteNavigationMenuPortletKeys.SITE_NAVIGATION_MENU,
			HashMapBuilder.put(
				"siteNavigationMenuExternalReferenceCode",
				new String[] {siteNavigationMenuExternalReferenceCode}
			).build());
	}

	private void _assertSiteNavigationMenuExternalReferenceCode(
		String siteNavigationMenuExternalReferenceCode,
		String siteNavigationMenuGroupExternalReferenceCode) {

		PortletPreferences portletPreferences =
			_portletPreferencesLocalService.getPreferences(
				_group.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, _layout.getPlid(),
				_portletId);

		Assert.assertEquals(
			siteNavigationMenuExternalReferenceCode,
			portletPreferences.getValue(
				"siteNavigationMenuExternalReferenceCode", StringPool.BLANK));

		Assert.assertEquals(
			siteNavigationMenuGroupExternalReferenceCode,
			portletPreferences.getValue(
				"siteNavigationMenuGroupExternalReferenceCode",
				StringPool.BLANK));
	}

	private void _propagateLayout() throws Exception {
		MergeLayoutPrototypesThreadLocal.clearMergeComplete();

		MergeLayoutPrototypesThreadLocal.setSkipMerge(false);

		_sites.mergeLayoutSetPrototypeLayouts(
			_group, _group.getPublicLayoutSet());
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	private Group _group;
	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	private LayoutSetPrototype _layoutSetPrototype;
	private String _portletId;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	private Layout _prototypeLayout;
	private SiteNavigationMenu _siteNavigationMenu1;
	private SiteNavigationMenu _siteNavigationMenu2;

	@Inject
	private Sites _sites;

}