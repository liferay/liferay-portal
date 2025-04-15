/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.test.portlet.MockPortletRequest;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadServletRequest;
import com.liferay.portal.kernel.util.File;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.osgi.web.portlet.container.test.util.PortletContainerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upload.LiferayServletRequest;
import com.liferay.portal.upload.UploadServletRequestImpl;

import jakarta.servlet.http.HttpServletRequest;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Peter Fellwock
 */
@RunWith(Arquillian.class)
public class PortalImplTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetCanonicalURLWithURLSeparatorInFriendlyURL()
		throws Exception {

		ThemeDisplay themeDisplay = _getThemeDisplay();

		Layout layout = _layoutLocalService.addLayout(
			null, TestPropsValues.getUserId(), themeDisplay.getScopeGroupId(),
			false, LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			StringPool.BLANK, LayoutConstants.TYPE_CONTENT, false, "/abc/w/def",
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));

		String canonicalURL = _portal.getCanonicalURL(
			String.format(
				"http://liferay.com/web/%s/abc/w/def", _group.getGroupKey()),
			themeDisplay, layout, false, false);

		String expectedSuffix = String.format(
			"/web/%s/abc/w/def", StringUtil.toLowerCase(_group.getGroupKey()));

		Assert.assertTrue(
			canonicalURL + " does not end with suffix " + expectedSuffix,
			canonicalURL.endsWith(expectedSuffix));
	}

	@Test
	public void testGetPortletTitleFromPortletRequestWithDeployedPortletId()
		throws Exception {

		Assert.assertEquals(
			"Server Administration",
			_portal.getPortletTitle(
				_mockPortletRequest(PortletKeys.SERVER_ADMIN)));
	}

	@Test
	public void testGetPortletTitleFromPortletRequestWithUndeployedPortletId()
		throws Exception {

		String portletId = "TEST_PORTLET_" + RandomTestUtil.randomString();

		Assert.assertEquals(
			portletId, _portal.getPortletTitle(_mockPortletRequest(portletId)));
	}

	@Test
	public void testGetPortletTitleWithDeployedPortletId() {
		String portletId = PortletKeys.SERVER_ADMIN;

		Assert.assertEquals(
			"Server Administration",
			_portal.getPortletTitle(portletId, LocaleUtil.US));
	}

	@Test
	public void testGetPortletTitleWithUndeployedPortletId() {
		String portletId = "TEST_PORTLET_" + RandomTestUtil.randomString();

		Assert.assertEquals(
			portletId, _portal.getPortletTitle(portletId, LocaleUtil.US));
	}

	@Test
	public void testGetUploadPortletRequestWithInvalidHttpServletRequest() {
		try {
			_portal.getUploadPortletRequest(new MockPortletRequest());

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertTrue(exception instanceof RuntimeException);
			Assert.assertEquals(
				"Unable to unwrap the portlet request from " +
					MockPortletRequest.class,
				exception.getMessage());
		}
	}

	@Test
	public void testGetUploadPortletRequestWithValidHttpServletRequest()
		throws Exception {

		Class<?> clazz = getClass();

		try (InputStream inputStream = clazz.getResourceAsStream(
				"/com/liferay/portal/util/test/dependencies/test.txt")) {

			LiferayServletRequest liferayServletRequest =
				PortletContainerTestUtil.getMultipartRequest(
					"fileParameterName", _file.getBytes(inputStream));

			UploadServletRequest uploadServletRequest =
				_portal.getUploadServletRequest(
					(HttpServletRequest)liferayServletRequest.getRequest());

			Assert.assertTrue(
				uploadServletRequest instanceof UploadServletRequestImpl);
		}
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));

		_group = GroupTestUtil.addGroup();

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		themeDisplay.setLayout(layout);
		themeDisplay.setLayoutSet(layout.getLayoutSet());

		themeDisplay.setLocale(LocaleUtil.US);
		themeDisplay.setPlid(layout.getPlid());
		themeDisplay.setPortalURL("http://localhost:8080");
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private MockPortletRequest _mockPortletRequest(String portletId)
		throws Exception {

		ThemeDisplay themeDisplay = _getThemeDisplay();

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest() {
				{
					setAttribute(WebKeys.CTX, getServletContext());
					setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);
				}
			};

		return new MockPortletRequest() {
			{
				setAttribute(WebKeys.PORTLET_ID, portletId);
				setAttribute(
					PortletServlet.PORTLET_SERVLET_REQUEST,
					mockHttpServletRequest);
				setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);
			}
		};
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private File _file;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private Language _language;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private Portal _portal;

}