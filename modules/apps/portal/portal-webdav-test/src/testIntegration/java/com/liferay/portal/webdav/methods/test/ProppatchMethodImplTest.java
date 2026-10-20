/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.webdav.methods.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.WebDAVProps;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WebDAVPropsLocalService;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.ProtectedServletRequest;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.webdav.WebDAVUtil;
import com.liferay.portal.kernel.webdav.methods.Method;
import com.liferay.portal.kernel.xml.QName;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.webdav.WebDAVServlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Set;

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
public class ProppatchMethodImplTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	@TestInfo("LPD-110156")
	public void testProcess() throws Exception {
		Group group = GroupTestUtil.addGroup();

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, "test.txt",
			ContentTypes.TEXT_PLAIN, "test.txt", null, null, null,
			RandomTestUtil.randomBytes(), null, null, null,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		User user = UserTestUtil.addGroupUser(group, RoleConstants.SITE_MEMBER);

		Assert.assertEquals(
			HttpServletResponse.SC_FORBIDDEN,
			_proppatch(group, fileEntry, user.getUserId()));

		Set<QName> propsSet = _getPropsSet(group, fileEntry);

		Assert.assertTrue(propsSet.toString(), propsSet.isEmpty());

		Assert.assertEquals(
			WebDAVUtil.SC_MULTI_STATUS,
			_proppatch(group, fileEntry, TestPropsValues.getUserId()));

		propsSet = _getPropsSet(group, fileEntry);

		Assert.assertEquals(propsSet.toString(), 1, propsSet.size());

		GroupTestUtil.deleteGroup(group);

		_userLocalService.deleteUser(user);
	}

	private Set<QName> _getPropsSet(Group group, FileEntry fileEntry)
		throws Exception {

		WebDAVProps webDAVProps = _webDAVPropsLocalService.getWebDAVProps(
			group.getCompanyId(), DLFileEntry.class.getName(),
			fileEntry.getFileEntryId());

		return webDAVProps.getPropsSet();
	}

	private int _proppatch(Group group, FileEntry fileEntry, long userId)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addHeader(HttpHeaders.USER_AGENT, "litmus");

		String xml = StringBundler.concat(
			"<?xml version=\"1.0\" encoding=\"utf-8\" ?><D:propertyupdate ",
			"xmlns:D=\"DAV:\" xmlns:Z=\"urn:test\"><D:set><D:prop>",
			"<Z:test>test</Z:test></D:prop></D:set></D:propertyupdate>");

		mockHttpServletRequest.setContent(xml.getBytes());

		mockHttpServletRequest.setContextPath("/webdav");
		mockHttpServletRequest.setMethod(Method.PROPPATCH);
		mockHttpServletRequest.setPathInfo(
			StringBundler.concat(
				group.getFriendlyURL(), "/document_library/",
				fileEntry.getTitle()));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_webDAVServlet.service(
			new ProtectedServletRequest(
				mockHttpServletRequest, String.valueOf(userId),
				HttpServletRequest.DIGEST_AUTH),
			mockHttpServletResponse);

		return mockHttpServletResponse.getStatus();
	}

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private WebDAVPropsLocalService _webDAVPropsLocalService;

	private final WebDAVServlet _webDAVServlet = new WebDAVServlet();

}