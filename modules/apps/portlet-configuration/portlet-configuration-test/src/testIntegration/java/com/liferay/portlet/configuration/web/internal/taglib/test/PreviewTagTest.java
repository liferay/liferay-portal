/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.configuration.web.internal.taglib.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.taglib.portletext.PreviewTag;

import jakarta.portlet.Portlet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import org.junit.After;
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
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockRequestDispatcher;
import org.springframework.mock.web.MockServletContext;

/**
 * @author Antonio Ortega
 */
@RunWith(Arquillian.class)
public class PreviewTagTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		Bundle bundle = FrameworkUtil.getBundle(PreviewTagTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_portletId = "TEST_PORTLET_" + RandomTestUtil.randomString();

		_serviceRegistration = bundleContext.registerService(
			Portlet.class, new MVCPortlet(),
			HashMapDictionaryBuilder.put(
				"jakarta.portlet.name", _portletId
			).build());
	}

	@After
	public void tearDown() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testDoTagDoesNotRenderPreviewWithoutConfigurationPermission()
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(_user = UserTestUtil.addUser());

		TestServletContext testServletContext = new TestServletContext();

		_tag(mockHttpServletRequest, testServletContext);

		Assert.assertEquals(0, testServletContext.getIncludesCount());

		Assert.assertNull(
			mockHttpServletRequest.getAttribute(
				"liferay-portlet:preview:portletName"));
	}

	@Test
	public void testDoTagRendersPreviewWithConfigurationPermission()
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(TestPropsValues.getUser());

		TestServletContext testServletContext = new TestServletContext();

		_tag(mockHttpServletRequest, testServletContext);

		Assert.assertEquals(1, testServletContext.getIncludesCount());

		Assert.assertEquals(
			_portletId,
			mockHttpServletRequest.getAttribute(
				"liferay-portlet:preview:portletName"));
	}

	private MockHttpServletRequest _getMockHttpServletRequest(User user)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setLayout(_layout);
		themeDisplay.setLayoutTypePortlet(
			(LayoutTypePortlet)_layout.getLayoutType());
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(user);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockHttpServletRequest;
	}

	private void _tag(
			MockHttpServletRequest mockHttpServletRequest,
			ServletContext servletContext)
		throws Exception {

		PreviewTag.doTag(
			"/" + RandomTestUtil.randomString() + ".jsp", _portletId, null,
			false, null, servletContext, mockHttpServletRequest,
			new MockHttpServletResponse());
	}

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;
	private String _portletId;
	private ServiceRegistration<Portlet> _serviceRegistration;

	@DeleteAfterTestRun
	private User _user;

	private static class TestServletContext extends MockServletContext {

		public int getIncludesCount() {
			return _includesCount;
		}

		@Override
		public RequestDispatcher getRequestDispatcher(String path) {
			return new MockRequestDispatcher(path) {

				@Override
				public void include(
					ServletRequest servletRequest,
					ServletResponse servletResponse) {

					_includesCount++;

					super.include(servletRequest, servletResponse);
				}

			};
		}

		private int _includesCount;

	}

}