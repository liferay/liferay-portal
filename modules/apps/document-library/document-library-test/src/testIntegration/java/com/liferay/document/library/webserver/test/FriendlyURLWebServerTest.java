/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.webserver.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.test.util.BaseWebServerTestCase;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.repository.friendly.url.resolver.FileEntryFriendlyURLResolver;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.webdav.methods.Method;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.webserver.WebServerServlet;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class FriendlyURLWebServerTest extends BaseWebServerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testHasFilesWithFileEntryFriendlyURLSeparator()
		throws Exception {

		Assert.assertFalse(
			WebServerServlet.hasFiles(
				_createMockHttpServletRequest(
					_getFileEntryFriendlyURL(RandomTestUtil.randomString()))));

		String urlTitle = RandomTestUtil.randomString();

		_addFileEntry(RandomTestUtil.randomString(), urlTitle);

		Assert.assertTrue(
			WebServerServlet.hasFiles(
				_createMockHttpServletRequest(
					_getFileEntryFriendlyURL(urlTitle))));
	}

	@Test
	public void testHasFilesWithFileEntryNameHasPlusSign() throws Exception {
		String nameBase = RandomTestUtil.randomString();

		String fileURL = nameBase + "%2B.txt";
		String fileName = nameBase + "+.txt";

		Assert.assertFalse(
			WebServerServlet.hasFiles(
				_createMockHttpServletRequest(
					String.format("/%s/0/%s", group.getGroupId(), fileURL))));

		_addFileEntry(fileName, RandomTestUtil.randomString());

		Assert.assertTrue(
			WebServerServlet.hasFiles(
				_createMockHttpServletRequest(
					String.format("/%s/0/%s", group.getGroupId(), fileURL))));
		Assert.assertFalse(
			WebServerServlet.hasFiles(
				_createMockHttpServletRequest(
					String.format("/%s/0/%s", group.getGroupId(), fileName))));
	}

	@Test
	public void testHasFilesWithFileEntryNameHasSpaces() throws Exception {
		String nameBase = RandomTestUtil.randomString();

		String fileURL = nameBase + "+.txt";
		String fileName = nameBase + " .txt";

		Assert.assertFalse(
			WebServerServlet.hasFiles(
				_createMockHttpServletRequest(
					String.format("/%s/0/%s", group.getGroupId(), fileURL))));

		_addFileEntry(fileName, RandomTestUtil.randomString());

		Assert.assertTrue(
			WebServerServlet.hasFiles(
				_createMockHttpServletRequest(
					String.format("/%s/0/%s", group.getGroupId(), fileURL))));
		Assert.assertTrue(
			WebServerServlet.hasFiles(
				_createMockHttpServletRequest(
					String.format(
						"/%s/0/%s", group.getGroupId(),
						nameBase + "%20.txt"))));
	}

	@Test
	public void testHasFilesWithFileEntryNameHasSpecialChars()
		throws Exception {

		String fileName = RandomTestUtil.randomString() + "%2B .txt";

		Assert.assertFalse(
			WebServerServlet.hasFiles(
				_createMockHttpServletRequest(
					String.format("/%s/0/%s", group.getGroupId(), fileName))));

		_addFileEntry(
			HttpComponentsUtil.decodeURL(fileName),
			RandomTestUtil.randomString());

		Assert.assertTrue(
			WebServerServlet.hasFiles(
				_createMockHttpServletRequest(
					String.format("/%s/0/%s", group.getGroupId(), fileName))));
	}

	@Test
	public void testHasFilesWithGroupIdUUIDFriendlyURL() throws Exception {
		Assert.assertFalse(
			WebServerServlet.hasFiles(
				_createMockHttpServletRequest(
					String.format(
						"/%s/%s", group.getGroupId(),
						RandomTestUtil.randomString()))));

		String urlTitle = RandomTestUtil.randomString();

		FileEntry fileEntry = _addFileEntry(
			RandomTestUtil.randomString(), urlTitle);

		Assert.assertTrue(
			WebServerServlet.hasFiles(
				_createMockHttpServletRequest(
					String.format(
						"/%s/%s", group.getGroupId(), fileEntry.getUuid()))));
	}

	@Test
	public void testServiceWithFileEntryFriendlyURLSeparator()
		throws Exception {

		MockHttpServletResponse mockHttpServletResponse = service(
			Method.GET, _getFileEntryFriendlyURL(RandomTestUtil.randomString()),
			HashMapBuilder.put(
				"Host", "localhost"
			).build(),
			Collections.emptyMap(), TestPropsValues.getUser(), null);

		Assert.assertEquals(
			HttpServletResponse.SC_NOT_FOUND,
			mockHttpServletResponse.getStatus());

		String urlTitle = RandomTestUtil.randomString();

		_addFileEntry(RandomTestUtil.randomString(), urlTitle);

		mockHttpServletResponse = service(
			Method.GET, _getFileEntryFriendlyURL(urlTitle),
			Collections.emptyMap(), Collections.emptyMap(),
			TestPropsValues.getUser(), null);

		Assert.assertEquals(
			HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());
	}

	private FileEntry _addFileEntry(String fileName, String urlTitle)
		throws Exception {

		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, fileName,
			ContentTypes.APPLICATION_OCTET_STREAM,
			RandomTestUtil.randomString(), urlTitle,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			(byte[])null, null, null, null,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));
	}

	private MockHttpServletRequest _createMockHttpServletRequest(String path)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest(Method.GET, "/documents" + path);

		mockHttpServletRequest.setAttribute(
			WebKeys.USER, TestPropsValues.getUser());
		mockHttpServletRequest.setContextPath("/documents");
		mockHttpServletRequest.setPathInfo(path);
		mockHttpServletRequest.setServletPath(StringPool.BLANK);

		return mockHttpServletRequest;
	}

	private String _getFileEntryFriendlyURL(String urlTitle) {
		return String.format(
			"%s%s/%s", FriendlyURLResolverConstants.URL_SEPARATOR_X_FILE_ENTRY,
			group.getFriendlyURL(), urlTitle);
	}

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private FileEntryFriendlyURLResolver _fileEntryFriendlyURLResolver;

	@Inject
	private Portal _portal;

}