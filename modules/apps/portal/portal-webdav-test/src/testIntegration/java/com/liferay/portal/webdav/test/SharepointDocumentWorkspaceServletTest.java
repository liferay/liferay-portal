/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.webdav.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpSession;

import java.lang.reflect.Constructor;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class SharepointDocumentWorkspaceServletTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	@TestInfo("LPD-110154")
	public void testGetDwsMetaDataResponse() throws Exception {

		// Group administrator

		Group group = GroupTestUtil.addGroup();

		User user = UserTestUtil.addUser();

		UserLocalServiceUtil.addGroupUser(group.getGroupId(), user.getUserId());

		String content = _getDwsMetaDataResponse(
			group, UserTestUtil.addGroupAdminUser(group));

		Assert.assertTrue(content, content.contains(user.getEmailAddress()));

		// Site member

		User siteMemberUser = UserTestUtil.addUser();

		UserLocalServiceUtil.addGroupUser(
			group.getGroupId(), siteMemberUser.getUserId());

		content = _getDwsMetaDataResponse(group, siteMemberUser);

		Assert.assertFalse(content, content.contains(user.getEmailAddress()));

		Role role = RoleLocalServiceUtil.getRole(
			group.getCompanyId(), RoleConstants.SITE_MEMBER);

		ResourcePermissionLocalServiceUtil.setResourcePermissions(
			group.getCompanyId(), Group.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(group.getGroupId()), role.getRoleId(),
			new String[] {ActionKeys.VIEW_MEMBERS});

		content = _getDwsMetaDataResponse(group, siteMemberUser);

		Assert.assertTrue(content, content.contains(user.getEmailAddress()));

		// User

		content = _getDwsMetaDataResponse(group, UserTestUtil.addUser());

		Assert.assertFalse(content, content.contains(user.getEmailAddress()));
		Assert.assertTrue(content, content.contains("&lt;Members/&gt;"));

		GroupTestUtil.deleteGroup(group);
	}

	private String _getDwsMetaDataResponse(Group group, User user)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest("POST", "/sharepoint/_vti_bin/dws.asmx");

		String xml = StringBundler.concat(
			"<GetDwsMetaData><document>/sharepoint", group.getFriendlyURL(),
			"/", RandomTestUtil.randomString(),
			"</document><minimal>true</minimal></GetDwsMetaData>");

		mockHttpServletRequest.setContent(xml.getBytes());

		HttpSession httpSession = mockHttpServletRequest.getSession();

		httpSession.setAttribute(WebKeys.USER, user);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user)) {

			ClassLoader classLoader = PortalClassLoaderUtil.getClassLoader();

			Class<?> clazz = classLoader.loadClass(
				"com.liferay.portal.sharepoint." +
					"SharepointDocumentWorkspaceServlet");

			Constructor<?> constructor = clazz.getDeclaredConstructor();

			HttpServlet httpServlet = (HttpServlet)constructor.newInstance();

			httpServlet.service(
				mockHttpServletRequest, mockHttpServletResponse);
		}

		String content = mockHttpServletResponse.getContentAsString();

		Assert.assertTrue(content, content.contains("GetDwsMetaDataResult"));

		return content;
	}

}