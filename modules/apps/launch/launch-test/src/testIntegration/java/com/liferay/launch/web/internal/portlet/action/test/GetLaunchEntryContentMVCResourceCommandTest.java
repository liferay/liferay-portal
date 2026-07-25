/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.test.util.DLAppTestUtil;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author David Truong
 */
@RunWith(Arquillian.class)
public class GetLaunchEntryContentMVCResourceCommandTest {

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
	public void testServeResourceWhenClassNameIsBlogsEntry() throws Exception {
		BlogsEntry blogsEntry = _blogsEntryLocalService.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		String updatedTitle = RandomTestUtil.randomString();

		_blogsEntryLocalService.updateEntry(
			TestPropsValues.getUserId(), blogsEntry.getEntryId(), updatedTitle,
			blogsEntry.getContent(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		String ignoredClassVersion = RandomTestUtil.randomString();

		JSONObject jsonObject = _serveResource(
			BlogsEntry.class.getName(), blogsEntry.getEntryId(),
			ignoredClassVersion);

		Assert.assertEquals(updatedTitle, jsonObject.getString("title"));
	}

	@Test
	public void testServeResourceWhenClassNameIsDLFileEntry() throws Exception {
		FileEntry fileEntry = DLAppTestUtil.addFileEntry(_group.getGroupId());

		String originalTitle = fileEntry.getTitle();
		String originalVersion = fileEntry.getVersion();

		_dlAppLocalService.updateFileEntry(
			TestPropsValues.getUserId(), fileEntry.getFileEntryId(), null,
			fileEntry.getMimeType(), RandomTestUtil.randomString(),
			fileEntry.getTitle(), StringPool.BLANK, StringPool.BLANK,
			DLVersionNumberIncrease.MAJOR, RandomTestUtil.randomBytes(), null,
			null, null,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		JSONObject jsonObject = _serveResource(
			DLFileEntry.class.getName(), fileEntry.getFileEntryId(),
			originalVersion);

		Assert.assertEquals(originalTitle, jsonObject.getString("title"));
		Assert.assertEquals(originalVersion, jsonObject.getString("version"));
	}

	@Test
	public void testServeResourceWhenClassNameIsJournalArticle()
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		String originalTitle = journalArticle.getTitle(LocaleUtil.US);
		double originalVersion = journalArticle.getVersion();

		JournalTestUtil.updateArticle(
			journalArticle, RandomTestUtil.randomString());

		JSONObject jsonObject = _serveResource(
			JournalArticle.class.getName(), journalArticle.getResourcePrimKey(),
			String.valueOf(originalVersion));

		Assert.assertEquals(originalTitle, jsonObject.getString("title"));
		Assert.assertEquals(
			String.valueOf(originalVersion), jsonObject.getString("version"));
	}

	@Test
	public void testServeResourceWhenClassNameIsUnregistered()
		throws Exception {

		MockLiferayResourceRequest mockLiferayResourceRequest =
			_getMockLiferayResourceRequest(
				RandomTestUtil.randomString(), RandomTestUtil.randomLong(),
				RandomTestUtil.randomString());

		TestMockLiferayResourceResponse testMockLiferayResourceResponse =
			new TestMockLiferayResourceResponse();

		_mvcResourceCommand.serveResource(
			mockLiferayResourceRequest, testMockLiferayResourceResponse);

		Assert.assertEquals(
			String.valueOf(HttpServletResponse.SC_NOT_FOUND),
			testMockLiferayResourceResponse.getProperty(
				ResourceResponse.HTTP_STATUS_CODE));
	}

	private MockLiferayResourceRequest _getMockLiferayResourceRequest(
			String className, long classPK, String classVersion)
		throws Exception {

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			new MockLiferayPortletRenderResponse());

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setLocale(LocaleUtil.US);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayResourceRequest.setAttribute(
			PortletServlet.PORTLET_SERVLET_REQUEST, mockHttpServletRequest);

		mockLiferayResourceRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);
		mockLiferayResourceRequest.setParameter("className", className);
		mockLiferayResourceRequest.setParameter(
			"classPK", String.valueOf(classPK));
		mockLiferayResourceRequest.setParameter("classVersion", classVersion);

		return mockLiferayResourceRequest;
	}

	private JSONObject _serveResource(
			String className, long classPK, String classVersion)
		throws Exception {

		MockLiferayResourceRequest mockLiferayResourceRequest =
			_getMockLiferayResourceRequest(className, classPK, classVersion);

		MockLiferayResourceResponse mockLiferayResourceResponse =
			new MockLiferayResourceResponse();

		_mvcResourceCommand.serveResource(
			mockLiferayResourceRequest, mockLiferayResourceResponse);

		ByteArrayOutputStream byteArrayOutputStream =
			(ByteArrayOutputStream)
				mockLiferayResourceResponse.getPortletOutputStream();

		return JSONFactoryUtil.createJSONObject(
			byteArrayOutputStream.toString());
	}

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(filter = "mvc.command.name=/launch/get_launch_entry_content")
	private MVCResourceCommand _mvcResourceCommand;

	private static class TestMockLiferayResourceResponse
		extends MockLiferayResourceResponse {

		@Override
		public String getProperty(String name) {
			return _properties.get(name);
		}

		@Override
		public void setProperty(String name, String value) {
			_properties.put(name, value);
		}

		private final Map<String, String> _properties = new HashMap<>();

	}

}