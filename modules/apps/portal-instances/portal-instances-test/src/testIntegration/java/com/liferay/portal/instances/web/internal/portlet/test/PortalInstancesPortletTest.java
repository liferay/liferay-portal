/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.instances.web.internal.portlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.instances.constants.PortalInstancesPortletKeys;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portlet.test.MockLiferayPortletContext;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.ResourceServingPortlet;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
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
 * @author Jorge Avalos
 */
@RunWith(Arquillian.class)
public class PortalInstancesPortletTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		Company company = CompanyTestUtil.addCompany();

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					company.getCompanyId())) {

			_companyAdminUser = UserTestUtil.getAdminUser(
				company.getCompanyId());
		}

		Bundle bundle = FrameworkUtil.getBundle(
			PortalInstancesPortletTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		Dictionary<String, Object> properties =
			HashMapDictionaryBuilder.<String, Object>put(
				"jakarta.portlet.name",
				PortalInstancesPortletKeys.PORTAL_INSTANCES
			).put(
				"mvc.command.name", _MVC_COMMAND_NAME
			).build();

		_serviceRegistrations.add(
			bundleContext.registerService(
				MVCActionCommand.class,
				(actionRequest, actionResponse) -> {
					_invoked = true;

					return true;
				},
				properties));
		_serviceRegistrations.add(
			bundleContext.registerService(
				MVCRenderCommand.class,
				(renderRequest, renderResponse) -> {
					_invoked = true;

					return MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH;
				},
				properties));
		_serviceRegistrations.add(
			bundleContext.registerService(
				MVCResourceCommand.class,
				(resourceRequest, resourceResponse) -> {
					_invoked = true;

					return true;
				},
				properties));
	}

	@AfterClass
	public static void tearDownClass() {
		for (ServiceRegistration<?> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}
	}

	@Test
	public void testProcessAction() throws Exception {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			ActionRequest.ACTION_NAME, _MVC_COMMAND_NAME);

		_assertInvoked(
			mockLiferayPortletActionRequest,
			() -> _portlet.processAction(
				mockLiferayPortletActionRequest,
				new MockLiferayPortletActionResponse()));
		_assertMustBeOmniadmin(
			mockLiferayPortletActionRequest,
			() -> _portlet.processAction(
				mockLiferayPortletActionRequest,
				new MockLiferayPortletActionResponse()),
			_companyAdminUser);
	}

	@Test
	public void testRender() throws Exception {
		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_createMockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.addParameter(
			"mvcRenderCommandName", _MVC_COMMAND_NAME);

		_assertInvoked(
			mockLiferayPortletRenderRequest,
			() -> _portlet.render(
				mockLiferayPortletRenderRequest,
				new MockLiferayPortletRenderResponse()));
		_assertRenderMustBeOmniadmin(
			mockLiferayPortletRenderRequest, _companyAdminUser);

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		RoleTestUtil.addResourcePermission(
			role, PortalInstancesPortletKeys.PORTAL_INSTANCES,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			ActionKeys.ACCESS_IN_CONTROL_PANEL);

		User user = UserTestUtil.addUser();

		_userLocalService.addRoleUser(role.getRoleId(), user);

		_assertRenderMustBeOmniadmin(mockLiferayPortletRenderRequest, user);

		MockLiferayPortletRenderRequest editMockLiferayPortletRenderRequest =
			_createMockLiferayPortletRenderRequest();

		editMockLiferayPortletRenderRequest.addParameter(
			"companyId", String.valueOf(TestPropsValues.getCompanyId()));
		editMockLiferayPortletRenderRequest.addParameter(
			"mvcRenderCommandName", "/portal_instances/edit_instance");

		_assertRenderMustBeOmniadmin(
			editMockLiferayPortletRenderRequest, _companyAdminUser);
	}

	@Test
	public void testServeResource() throws Exception {
		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		mockLiferayResourceRequest.setResourceID(_MVC_COMMAND_NAME);

		ResourceServingPortlet resourceServingPortlet =
			(ResourceServingPortlet)_portlet;

		_assertInvoked(
			mockLiferayResourceRequest,
			() -> resourceServingPortlet.serveResource(
				mockLiferayResourceRequest, new MockLiferayResourceResponse()));
		_assertMustBeOmniadmin(
			mockLiferayResourceRequest,
			() -> resourceServingPortlet.serveResource(
				mockLiferayResourceRequest, new MockLiferayResourceResponse()),
			_companyAdminUser);
	}

	private void _assertInvoked(
			PortletRequest portletRequest,
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		_invoked = false;

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					PortalInstancePool.getDefaultCompanyId())) {

			_invoke(
				portletRequest, unsafeRunnable,
				UserTestUtil.getAdminUser(
					PortalInstancePool.getDefaultCompanyId()));
		}

		Assert.assertTrue(_invoked);
	}

	private void _assertMustBeOmniadmin(
		PortletRequest portletRequest, UnsafeRunnable<Exception> unsafeRunnable,
		User user) {

		_invoked = false;

		PortletException portletException = Assert.assertThrows(
			PortletException.class,
			() -> _invoke(portletRequest, unsafeRunnable, user));

		Throwable causeThrowable = portletException.getCause();

		Assert.assertTrue(
			causeThrowable instanceof PrincipalException.MustBeOmniadmin);

		Assert.assertFalse(_invoked);
	}

	private void _assertRenderMustBeOmniadmin(
		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest,
		User user) {

		_assertMustBeOmniadmin(
			mockLiferayPortletRenderRequest,
			() -> _portlet.render(
				mockLiferayPortletRenderRequest,
				new MockLiferayPortletRenderResponse()),
			user);
	}

	private MockLiferayPortletRenderRequest
		_createMockLiferayPortletRenderRequest() {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		for (String path : new String[] {"/edit_instance.jsp", "/view.jsp"}) {
			mockLiferayPortletRenderRequest.setAttribute(
				MVCRenderConstants.
					PORTLET_CONTEXT_OVERRIDE_REQUEST_ATTIBUTE_NAME_PREFIX +
						path,
				new MockLiferayPortletContext(path));
		}

		return mockLiferayPortletRenderRequest;
	}

	private void _invoke(
			PortletRequest portletRequest,
			UnsafeRunnable<Exception> unsafeRunnable, User user)
		throws Exception {

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					user.getCompanyId())) {

			PermissionChecker permissionChecker =
				_permissionCheckerFactory.create(user);

			ThemeDisplay themeDisplay = new ThemeDisplay();

			themeDisplay.setPermissionChecker(permissionChecker);

			Group group = _groupLocalService.getGroup(
				user.getCompanyId(), GroupConstants.GUEST);

			themeDisplay.setScopeGroupId(group.getGroupId());

			portletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

			PermissionChecker originalPermissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			try {
				PermissionThreadLocal.setPermissionChecker(permissionChecker);

				unsafeRunnable.run();
			}
			finally {
				PermissionThreadLocal.setPermissionChecker(
					originalPermissionChecker);
			}
		}
	}

	private static final String _MVC_COMMAND_NAME =
		"/" + RandomTestUtil.randomString();

	private static User _companyAdminUser;
	private static boolean _invoked;
	private static final List<ServiceRegistration<?>> _serviceRegistrations =
		new ArrayList<>();

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Inject(
		filter = "jakarta.portlet.name=" + PortalInstancesPortletKeys.PORTAL_INSTANCES
	)
	private Portlet _portlet;

	@Inject
	private UserLocalService _userLocalService;

}