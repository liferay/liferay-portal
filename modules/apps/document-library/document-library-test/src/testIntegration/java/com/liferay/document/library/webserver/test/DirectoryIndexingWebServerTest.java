/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.webserver.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.test.util.BaseWebServerTestCase;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.servlet.ContentDispositionUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.webdav.methods.Method;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.webserver.WebServerServlet;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class DirectoryIndexingWebServerTest extends BaseWebServerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		UnicodeProperties typeSettingsUnicodeProperties =
			group.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.setProperty(
			"directoryIndexingEnabled", Boolean.TRUE.toString());

		group = _groupLocalService.updateGroup(
			group.getGroupId(), typeSettingsUnicodeProperties.toString());

		_webServerServlet.init(new MockServletConfig());
	}

	@Test
	@TestInfo("LPS-82205")
	public void testServiceWithHTMLFileEntry() throws Exception {
		byte[] bytes = RandomTestUtil.randomBytes();

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".html", ContentTypes.TEXT_HTML,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), bytes,
			null, null, null,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		MockHttpServletResponse mockHttpServletResponse = service(
			Method.GET,
			group.getFriendlyURL() + StringPool.SLASH + fileEntry.getTitle(),
			Collections.emptyMap(), Collections.emptyMap(), null, null);

		Assert.assertArrayEquals(
			bytes, mockHttpServletResponse.getContentAsByteArray());
		Assert.assertEquals(
			ContentDispositionUtil.getContentDispositionHeaderValue(
				HttpHeaders.CONTENT_DISPOSITION_ATTACHMENT,
				fileEntry.getTitle()),
			mockHttpServletResponse.getHeader(HttpHeaders.CONTENT_DISPOSITION));
		Assert.assertEquals(
			HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());
	}

	@Test
	public void testServiceWithHTMLFileEntryTitle() throws Exception {
		String title = "<img src=x onerror=alert(1)>";

		_dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN, title,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomBytes(), null,
			null, null,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		MockHttpServletResponse mockHttpServletResponse = service(
			Method.GET, group.getFriendlyURL() + StringPool.SLASH,
			Collections.emptyMap(), Collections.emptyMap(), null, null);

		String content = mockHttpServletResponse.getContentAsString();

		Assert.assertTrue(content, content.contains(HtmlUtil.escape(title)));
		Assert.assertFalse(content, content.contains(title));

		Assert.assertEquals(
			HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());
	}

	@Override
	protected Servlet getServlet() {
		return _webServerServlet;
	}

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	private final WebServerServlet _webServerServlet = new WebServerServlet();

}