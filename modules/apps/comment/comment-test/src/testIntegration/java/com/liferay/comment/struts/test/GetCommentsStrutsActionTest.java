/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.comment.struts.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class GetCommentsStrutsActionTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		_blogsEntry = _blogsEntryLocalService.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), serviceContext);

		_commentManager.addComment(
			TestPropsValues.getUserId(), _group.getGroupId(),
			BlogsEntry.class.getName(), _blogsEntry.getEntryId(),
			RandomTestUtil.randomString(), className -> serviceContext);
	}

	@Test
	@TestInfo("LPD-109768")
	public void testExecute() throws Exception {
		HttpURLConnection httpURLConnection = _openHttpURLConnection(
			"_namespace_", "randomNamespace_");

		Assert.assertEquals(200, httpURLConnection.getResponseCode());

		String content = StringUtil.read(httpURLConnection.getInputStream());

		Assert.assertTrue(
			content, content.contains("randomNamespace_message_"));

		httpURLConnection.disconnect();

		_testExecuteWithInvalidNamespace();
		_testExecuteWithInvalidRandomNamespace();
	}

	private void _assertBadRequest(
			HttpURLConnection httpURLConnection, String value)
		throws Exception {

		Assert.assertEquals(400, httpURLConnection.getResponseCode());

		String content = StringUtil.read(httpURLConnection.getErrorStream());

		Assert.assertFalse(content, content.contains(value));

		httpURLConnection.disconnect();
	}

	private HttpURLConnection _openHttpURLConnection(
			String namespace, String randomNamespace)
		throws Exception {

		String urlString =
			"http://localhost:" + PortalUtil.getPortalServerPort(false) +
				"/c/portal/comment/discussion/get_comments";

		urlString = HttpComponentsUtil.addParameter(
			urlString, "className", BlogsEntry.class.getName());
		urlString = HttpComponentsUtil.addParameter(
			urlString, "classPK", _blogsEntry.getEntryId());
		urlString = HttpComponentsUtil.addParameter(
			urlString, "namespace", namespace);
		urlString = HttpComponentsUtil.addParameter(
			urlString, "randomNamespace", randomNamespace);
		urlString = HttpComponentsUtil.addParameter(
			urlString, "rootIndexPage", 1);

		URL url = new URL(urlString);

		HttpURLConnection httpURLConnection =
			(HttpURLConnection)url.openConnection();

		httpURLConnection.setRequestMethod("GET");

		return httpURLConnection;
	}

	private void _testExecuteWithInvalidNamespace() throws Exception {
		_assertBadRequest(
			_openHttpURLConnection("_name\"space_", "randomNamespace_"),
			"name\"space");
	}

	private void _testExecuteWithInvalidRandomNamespace() throws Exception {
		_assertBadRequest(
			_openHttpURLConnection("_namespace_", "random\"Namespace_"),
			"random\"Namespace");
	}

	private BlogsEntry _blogsEntry;

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Inject
	private CommentManager _commentManager;

	@DeleteAfterTestRun
	private Group _group;

}