/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.preferences.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.PortletPreferencesIds;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.Portlet;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 */
@RunWith(Arquillian.class)
public class PortletPreferencesFactoryImplGetPreferencesIdsTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		Bundle bundle = FrameworkUtil.getBundle(
			PortletPreferencesFactoryImplGetPreferencesIdsTest.class);

		_bundleContext = bundle.getBundleContext();
	}

	@Before
	public void setUp() throws Exception {
		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext());

		UserTestUtil.setUser(TestPropsValues.getUser());

		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypePortletLayout(_group, true);
	}

	@After
	public void tearDown() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testPreferencesOwnedByCompany() throws Exception {
		_registerCompanyWidePortlet();

		LayoutTestUtil.addPortletToLayout(
			TestPropsValues.getUserId(), _layout, _TEST_COMPANY_PORTLET_NAME,
			"column-1", null);

		PortletPreferencesIds portletPreferencesIds =
			PortletPreferencesFactoryUtil.getPortletPreferencesIds(
				_layout.getGroupId(), TestPropsValues.getUserId(), _layout,
				_TEST_COMPANY_PORTLET_NAME, false);

		Assert.assertEquals(
			"The owner type should be of type company",
			PortletKeys.PREFS_OWNER_TYPE_COMPANY,
			portletPreferencesIds.getOwnerType());
		Assert.assertEquals(
			"The owner ID should be the ID of the company",
			_layout.getCompanyId(), portletPreferencesIds.getOwnerId());
		Assert.assertEquals(
			"The PLID should not be a real value",
			PortletKeys.PREFS_PLID_SHARED, portletPreferencesIds.getPlid());
	}

	@Test
	public void testPreferencesOwnedByGroup() throws Exception {
		_registerPortlet(true, false, _TEST_GROUP_PORTLET_NAME);

		long siteGroupId = _layout.getGroupId();

		PortletPreferencesIds portletPreferencesIds =
			PortletPreferencesFactoryUtil.getPortletPreferencesIds(
				siteGroupId, TestPropsValues.getUserId(), _layout,
				_TEST_GROUP_PORTLET_NAME, false);

		Assert.assertEquals(
			"The owner type should be of type group",
			PortletKeys.PREFS_OWNER_TYPE_GROUP,
			portletPreferencesIds.getOwnerType());
		Assert.assertEquals(
			"The owner ID should be the ID of the group", siteGroupId,
			portletPreferencesIds.getOwnerId());
		Assert.assertEquals(
			"The PLID should not be a real value",
			PortletKeys.PREFS_PLID_SHARED, portletPreferencesIds.getPlid());
	}

	@Test
	public void testPreferencesOwnedByGroupLayout() throws Exception {
		_registerPortlet(true, true, _TEST_GROUP_LAYOUT_PORTLET_NAME);

		PortletPreferencesIds portletPreferencesIds =
			PortletPreferencesFactoryUtil.getPortletPreferencesIds(
				_layout.getGroupId(), TestPropsValues.getUserId(), _layout,
				_TEST_GROUP_LAYOUT_PORTLET_NAME, false);

		Assert.assertEquals(
			"The owner type should be of type layout",
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
			portletPreferencesIds.getOwnerType());
		Assert.assertEquals(
			"The owner ID should be the default value",
			PortletKeys.PREFS_OWNER_ID_DEFAULT,
			portletPreferencesIds.getOwnerId());
		Assert.assertEquals(
			"The PLID should be the PLID of the layout", _layout.getPlid(),
			portletPreferencesIds.getPlid());
	}

	@Test
	public void testPreferencesOwnedByUser() throws Exception {
		_registerPortlet(false, false, _TEST_USER_PORTLET_NAME);

		PortletPreferencesIds portletPreferencesIds =
			PortletPreferencesFactoryUtil.getPortletPreferencesIds(
				_layout.getGroupId(), TestPropsValues.getUserId(), _layout,
				_TEST_USER_PORTLET_NAME, false);

		Assert.assertEquals(
			"The owner type should be of type user",
			PortletKeys.PREFS_OWNER_TYPE_USER,
			portletPreferencesIds.getOwnerType());
		Assert.assertEquals(
			"The owner ID should be the ID of the user who added it",
			TestPropsValues.getUserId(), portletPreferencesIds.getOwnerId());
		Assert.assertEquals(
			"The PLID should not be a real value",
			PortletKeys.PREFS_PLID_SHARED, portletPreferencesIds.getPlid());
	}

	@Test
	public void testPreferencesOwnedByUserLayout() throws Exception {
		_registerPortlet(false, true, _TEST_USER_LAYOUT_PORTLET_NAME);

		PortletPreferencesIds portletPreferencesIds =
			PortletPreferencesFactoryUtil.getPortletPreferencesIds(
				_layout.getGroupId(), TestPropsValues.getUserId(), _layout,
				_TEST_USER_LAYOUT_PORTLET_NAME, false);

		Assert.assertEquals(
			"The owner type should be of type user",
			PortletKeys.PREFS_OWNER_TYPE_USER,
			portletPreferencesIds.getOwnerType());
		Assert.assertEquals(
			"The owner ID should be the ID of the user who added it",
			TestPropsValues.getUserId(), portletPreferencesIds.getOwnerId());
		Assert.assertEquals(
			"The PLID should be the PLID of the layout", _layout.getPlid(),
			portletPreferencesIds.getPlid());
	}

	@Test
	public void testPreferencesWithModeEditGuestInPublicLayoutWithPermission()
		throws Exception {

		_registerCompanyWidePortlet();

		_layout = LayoutTestUtil.addTypePortletLayout(_group, false);

		PortletPreferencesFactoryUtil.getPortletPreferencesIds(
			_layout.getGroupId(), TestPropsValues.getUserId(), _layout,
			_TEST_GROUP_PORTLET_NAME, true);
	}

	private void _registerCompanyWidePortlet() {
		_serviceRegistration = _bundleContext.registerService(
			Portlet.class, new MVCPortlet(),
			HashMapDictionaryBuilder.<String, Object>put(
				"com.liferay.portlet.preferences-company-wide", "true"
			).put(
				"jakarta.portlet.name", _TEST_COMPANY_PORTLET_NAME
			).build());
	}

	private void _registerPortlet(
		boolean owedByGroup, boolean uniquePerLayout, String portletName) {

		_serviceRegistration = _bundleContext.registerService(
			Portlet.class, new MVCPortlet(),
			HashMapDictionaryBuilder.<String, Object>put(
				"com.liferay.portlet.preferences-company-wide", "false"
			).put(
				"com.liferay.portlet.preferences-owned-by-group",
				Boolean.valueOf(owedByGroup)
			).put(
				"com.liferay.portlet.preferences-unique-per-layout",
				Boolean.valueOf(uniquePerLayout)
			).put(
				"jakarta.portlet.name", portletName
			).build());
	}

	private static final String _TEST_COMPANY_PORTLET_NAME =
		"com_liferay_portlet_PortletPreferencesFactoryImplGet" +
			"PreferencesIdsTest_TestCompanyPortlet";

	private static final String _TEST_GROUP_LAYOUT_PORTLET_NAME =
		"com_liferay_portlet_PortletPreferencesFactoryImplGet" +
			"PreferencesIdsTest_TestGroupLayoutPortlet";

	private static final String _TEST_GROUP_PORTLET_NAME =
		"com_liferay_portlet_PortletPreferencesFactoryImplGet" +
			"PreferencesIdsTest_TestGroupPortlet";

	private static final String _TEST_USER_LAYOUT_PORTLET_NAME =
		"com_liferay_portlet_PortletPreferencesFactoryImplGet" +
			"PreferencesIdsTest_TestUserLayoutPortlet";

	private static final String _TEST_USER_PORTLET_NAME =
		"com_liferay_portlet_PortletPreferencesFactoryImplGet" +
			"PreferencesIdsTest_TestUserPortlet";

	private static BundleContext _bundleContext;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;
	private ServiceRegistration<Portlet> _serviceRegistration;

}