/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.analytics.internal.servlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.Servlet;

import org.junit.Assert;
import org.junit.Before;
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
public class ResolveFileEntryUUIDServletTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	@TestInfo("LPD-107157")
	public void testDoGetWhenFileEntryNotViewable() throws Exception {
		FileEntry fileEntry = _addFileEntry(false);

		MockHttpServletResponse mockHttpServletResponse1 =
			new MockHttpServletResponse();

		_servlet.service(
			_getMockHttpServletRequest(RandomTestUtil.randomString(), null),
			mockHttpServletResponse1);

		MockHttpServletResponse mockHttpServletResponse2 =
			new MockHttpServletResponse();

		_servlet.service(
			_getMockHttpServletRequest(fileEntry.getUuid(), null),
			mockHttpServletResponse2);

		_assertEquals(mockHttpServletResponse1, mockHttpServletResponse2);

		_user = UserTestUtil.addUser();

		MockHttpServletResponse mockHttpServletResponse3 =
			new MockHttpServletResponse();

		_servlet.service(
			_getMockHttpServletRequest(fileEntry.getUuid(), _user),
			mockHttpServletResponse3);

		_assertEquals(mockHttpServletResponse1, mockHttpServletResponse3);
	}

	@Test
	@TestInfo("LPD-107157")
	public void testDoGetWhenFileEntryViewable() throws Exception {
		FileEntry fileEntry = _addFileEntry(true);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_servlet.service(
			_getMockHttpServletRequest(fileEntry.getUuid(), null),
			mockHttpServletResponse);

		Assert.assertEquals(200, mockHttpServletResponse.getStatus());

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			mockHttpServletResponse.getContentAsString());

		Assert.assertEquals(
			fileEntry.getFileEntryId(), jsonObject.getLong("fileEntryId"));
	}

	private FileEntry _addFileEntry(boolean viewableByGuest) throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGroupPermissions(viewableByGuest);
		serviceContext.setAddGuestPermissions(viewableByGuest);

		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
			RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, RandomTestUtil.randomBytes(), null, null, null,
			serviceContext);
	}

	private void _assertEquals(
			MockHttpServletResponse expectedMockHttpServletResponse,
			MockHttpServletResponse actualMockHttpServletResponse)
		throws Exception {

		Assert.assertEquals(
			expectedMockHttpServletResponse.getStatus(),
			actualMockHttpServletResponse.getStatus());
		Assert.assertEquals(
			expectedMockHttpServletResponse.getContentAsString(),
			actualMockHttpServletResponse.getContentAsString());
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
			String uuid, User user)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest("GET", StringPool.SLASH);

		mockHttpServletRequest.setAttribute(
			WebKeys.COMPANY_ID, TestPropsValues.getCompanyId());

		if (user != null) {
			mockHttpServletRequest.setAttribute(WebKeys.USER, user);
		}

		mockHttpServletRequest.setParameter(
			"groupId", String.valueOf(_group.getGroupId()));
		mockHttpServletRequest.setParameter("uuid", uuid);

		return mockHttpServletRequest;
	}

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject(
		filter = "osgi.http.whiteboard.servlet.name=com.liferay.document.library.analytics.internal.servlet.ResolveFileEntryUUIDServlet"
	)
	private Servlet _servlet;

	@DeleteAfterTestRun
	private User _user;

}