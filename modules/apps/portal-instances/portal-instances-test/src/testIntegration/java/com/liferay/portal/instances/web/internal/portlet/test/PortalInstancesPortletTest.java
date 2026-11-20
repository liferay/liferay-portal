/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.instances.web.internal.portlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.instances.constants.PortalInstancesPortletKeys;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portlet.test.MockLiferayPortletContext;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.ResourceServingPortlet;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

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

		_companyAdminUser = UserTestUtil.addCompanyAdminUser(company);
	}

	@Test
	public void testProcessAction() throws Exception {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		_setThemeDisplay(mockLiferayPortletActionRequest, _companyAdminUser);

		_assertPrincipalException(
			() -> _portlet.processAction(
				mockLiferayPortletActionRequest,
				new MockLiferayPortletActionResponse()));
	}

	@Test
	public void testRender() throws Exception {
		_render(TestPropsValues.getUser());

		_assertPrincipalException(() -> _render(_companyAdminUser));
	}

	@Test
	public void testServeResource() throws Exception {
		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		_setThemeDisplay(mockLiferayResourceRequest, _companyAdminUser);

		ResourceServingPortlet resourceServingPortlet =
			(ResourceServingPortlet)_portlet;

		_assertPrincipalException(
			() -> resourceServingPortlet.serveResource(
				mockLiferayResourceRequest, new MockLiferayResourceResponse()));
	}

	private void _assertPrincipalException(
		UnsafeRunnable<Exception> unsafeRunnable) {

		PortletException portletException = Assert.assertThrows(
			PortletException.class, unsafeRunnable::run);

		Assert.assertTrue(
			portletException.getCause() instanceof
				PrincipalException.MustBeOmniadmin);
	}

	private void _render(User user) throws Exception {
		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setAttribute(
			MVCRenderConstants.
				PORTLET_CONTEXT_OVERRIDE_REQUEST_ATTIBUTE_NAME_PREFIX +
					"/view.jsp",
			new MockLiferayPortletContext("/view.jsp"));

		_setThemeDisplay(mockLiferayPortletRenderRequest, user);

		_portlet.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());
	}

	private void _setThemeDisplay(PortletRequest portletRequest, User user)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setPermissionChecker(
			_permissionCheckerFactory.create(user));

		portletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);
	}

	private static User _companyAdminUser;

	@Inject
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Inject(
		filter = "jakarta.portlet.name=" + PortalInstancesPortletKeys.PORTAL_INSTANCES
	)
	private Portlet _portlet;

}