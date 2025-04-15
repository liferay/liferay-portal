/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.application.list.my.account.permissions.test;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PortalInstances;

import jakarta.portlet.GenericPortlet;
import jakarta.portlet.Portlet;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
 * @author Drew Brokke
 */
@RunWith(Arquillian.class)
public class PanelAppMyAccountPermissionsTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		Bundle bundle = FrameworkUtil.getBundle(
			PanelAppMyAccountPermissionsTest.class);

		_bundleContext = bundle.getBundleContext();
	}

	@Before
	public void setUp() {
		_testPortletId = "TEST_PORTLET_" + RandomTestUtil.randomString();
	}

	@After
	public void tearDown() throws Exception {
		for (ServiceRegistration<?> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}

		_serviceRegistrations.clear();

		PortletPreferencesLocalServiceUtil.deletePortletPreferences(
			TestPropsValues.getCompanyId(),
			PortletKeys.PREFS_OWNER_TYPE_COMPANY, LayoutConstants.DEFAULT_PLID,
			_testPortletId);
	}

	@Test
	public void testPermissionsAddedForAllCompaniesFromNewPanelApp()
		throws Exception {

		_testCompany = addCompany();

		_registerTestPortlet();

		long defaultCompanyId = TestPropsValues.getCompanyId();

		Assert.assertFalse(
			_hasMyAccountPermission(defaultCompanyId, _testPortletId));

		long testCompanyId = _testCompany.getCompanyId();

		Assert.assertFalse(
			_hasMyAccountPermission(testCompanyId, _testPortletId));

		_registerTestPanelApp();

		Assert.assertTrue(
			_hasMyAccountPermission(defaultCompanyId, _testPortletId));
		Assert.assertTrue(
			_hasMyAccountPermission(testCompanyId, _testPortletId));
	}

	@Test
	public void testPermissionsAddedForPanelAppFromNewCompany()
		throws Exception {

		_registerTestPortlet();

		_registerTestPanelApp();

		_testCompany = addCompany();

		Assert.assertTrue(
			_hasMyAccountPermission(
				_testCompany.getCompanyId(), _testPortletId));
	}

	protected Company addCompany() throws Exception {
		Company company = CompanyTestUtil.addCompany();

		PortalInstances.initCompany(company);

		return company;
	}

	private boolean _hasMyAccountPermission(long companyId, String portletId)
		throws Exception {

		Role userRole = RoleLocalServiceUtil.getRole(
			companyId, RoleConstants.USER);

		return ResourcePermissionLocalServiceUtil.hasResourcePermission(
			companyId, portletId, ResourceConstants.SCOPE_COMPANY,
			String.valueOf(companyId), userRole.getRoleId(),
			ActionKeys.ACCESS_IN_CONTROL_PANEL);
	}

	private void _registerTestPanelApp() {
		_serviceRegistrations.add(
			_bundleContext.registerService(
				PanelApp.class,
				new TestPanelApp(
					_portletLocalService.getPortletById(_testPortletId)),
				HashMapDictionaryBuilder.put(
					"panel.category.key", PanelCategoryKeys.USER_MY_ACCOUNT
				).build()));
	}

	private void _registerTestPortlet() {
		_serviceRegistrations.add(
			_bundleContext.registerService(
				Portlet.class, new TestPortlet(),
				HashMapDictionaryBuilder.put(
					"jakarta.portlet.name", _testPortletId
				).build()));
	}

	private static BundleContext _bundleContext;

	@Inject
	private PortletLocalService _portletLocalService;

	private final List<ServiceRegistration<?>> _serviceRegistrations =
		new CopyOnWriteArrayList<>();

	@DeleteAfterTestRun
	private Company _testCompany;

	private String _testPortletId;

	private static class TestPanelApp extends BasePanelApp {

		public TestPanelApp(com.liferay.portal.kernel.model.Portlet portlet) {
			_portlet = portlet;
		}

		@Override
		public com.liferay.portal.kernel.model.Portlet getPortlet() {
			return _portlet;
		}

		public String getPortletId() {
			return _portlet.getPortletId();
		}

		private final com.liferay.portal.kernel.model.Portlet _portlet;

	}

	private static class TestPortlet extends GenericPortlet {
	}

}