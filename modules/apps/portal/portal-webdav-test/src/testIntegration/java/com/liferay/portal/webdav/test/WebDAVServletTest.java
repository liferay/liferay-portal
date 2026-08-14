/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.webdav.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.ProtectedServletRequest;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.webdav.WebDAVUtil;
import com.liferay.portal.kernel.webdav.methods.Method;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.webdav.WebDAVServlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Jorge Gracía Jiménez
 * @author Nikoletta Buza
 */
@RunWith(Arquillian.class)
public class WebDAVServletTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testGetSession() throws Exception {
		ProtectedServletRequest protectedServletRequest =
			_createProtectedServletRequest("guest");
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_webDAVServlet.service(
			protectedServletRequest, mockHttpServletResponse);

		Assert.assertEquals(
			WebDAVUtil.SC_MULTI_STATUS, mockHttpServletResponse.getStatus());

		HttpSession httpSession = protectedServletRequest.getSession();

		User user = (User)httpSession.getAttribute(WebKeys.USER);

		Assert.assertNotNull(user);
		Assert.assertNotNull(user.getDigest());
		Assert.assertEquals(TestPropsValues.getUserId(), user.getUserId());
		Assert.assertTrue(user.isActive());
	}

	@Test
	public void testGetSiteDefaultLocale() throws Exception {
		_group = GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(), ListUtil.fromArray(LocaleUtil.HUNGARY),
			LocaleUtil.HUNGARY);

		ProtectedServletRequest protectedServletRequest =
			_createProtectedServletRequest(_group.getFriendlyURL());

		_webDAVServlet.service(
			protectedServletRequest, new MockHttpServletResponse());

		Locale actualSiteDefaultLocale =
			LocaleThreadLocal.getSiteDefaultLocale();

		Assert.assertEquals(LocaleUtil.HUNGARY, actualSiteDefaultLocale);
	}

	private ProtectedServletRequest _createProtectedServletRequest(
			String friendlyURL)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addHeader(HttpHeaders.USER_AGENT, "litmus");
		mockHttpServletRequest.setContextPath("/webdav");
		mockHttpServletRequest.setMethod(Method.PROPFIND);
		mockHttpServletRequest.setPathInfo(
			"/" + friendlyURL +
				"/document_library/Provided by Liferay/stars.png");

		return new ProtectedServletRequest(
			mockHttpServletRequest, String.valueOf(TestPropsValues.getUserId()),
			HttpServletRequest.DIGEST_AUTH);
	}

	private Group _group;
	private final WebDAVServlet _webDAVServlet = new WebDAVServlet();

}