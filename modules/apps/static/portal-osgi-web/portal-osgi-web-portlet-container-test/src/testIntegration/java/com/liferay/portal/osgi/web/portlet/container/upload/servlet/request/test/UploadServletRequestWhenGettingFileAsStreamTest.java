/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.portlet.container.upload.servlet.request.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upload.FileItem;
import com.liferay.portal.kernel.upload.UploadServletRequest;
import com.liferay.portal.osgi.web.portlet.container.test.util.PortletContainerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upload.LiferayServletRequest;
import com.liferay.portal.upload.test.util.UploadTestUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.io.InputStream;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

/**
 * @author Manuel de la Peña
 */
@RunWith(Arquillian.class)
public class UploadServletRequestWhenGettingFileAsStreamTest {

	@ClassRule
	@Rule
	public static final TestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_fileNameParameter = RandomTestUtil.randomString();
	}

	@Test
	public void testShouldReturnAStream() throws Exception {
		Map<String, FileItem[]> fileParameters =
			PortletContainerTestUtil.getFileParameters(1, _BYTES);

		LiferayServletRequest liferayServletRequest =
			PortletContainerTestUtil.getMultipartRequest(
				_fileNameParameter, _BYTES);

		UploadServletRequest uploadServletRequest =
			UploadTestUtil.createUploadServletRequest(
				(HttpServletRequest)liferayServletRequest.getRequest(),
				fileParameters, new HashMap<String, List<String>>());

		Map<String, FileItem[]> map =
			uploadServletRequest.getMultipartParameterMap();

		Assert.assertEquals(map.toString(), 1, map.size());

		for (Map.Entry<String, FileItem[]> entry : map.entrySet()) {
			String key = entry.getKey();

			InputStream inputStream = uploadServletRequest.getFileAsStream(key);

			Assert.assertNotNull(inputStream);

			inputStream = uploadServletRequest.getFileAsStream(key, true);

			Assert.assertNotNull(inputStream);
		}
	}

	@Test
	public void testShouldReturnNullIfFileParametersAreEmpty()
		throws Exception {

		LiferayServletRequest liferayServletRequest =
			PortletContainerTestUtil.getMultipartRequest(
				_fileNameParameter, _BYTES);

		UploadServletRequest uploadServletRequest =
			UploadTestUtil.createUploadServletRequest(
				(HttpServletRequest)liferayServletRequest.getRequest(),
				new HashMap<String, FileItem[]>(),
				new HashMap<String, List<String>>());

		Assert.assertNull(
			uploadServletRequest.getFileAsStream("irrelevantName"));
		Assert.assertNull(
			uploadServletRequest.getFileAsStream("irrelevantName", true));
	}

	@Test
	public void testShouldReturnNullIfNameIsNotAFileParameter()
		throws Exception {

		Map<String, FileItem[]> fileParameters =
			PortletContainerTestUtil.getFileParameters(1, _BYTES);

		LiferayServletRequest liferayServletRequest =
			PortletContainerTestUtil.getMultipartRequest(
				_fileNameParameter, _BYTES);

		UploadServletRequest uploadServletRequest =
			UploadTestUtil.createUploadServletRequest(
				(HttpServletRequest)liferayServletRequest.getRequest(),
				fileParameters, new HashMap<String, List<String>>());

		Assert.assertNull(
			uploadServletRequest.getFileAsStream("nonexistentFile"));
		Assert.assertNull(
			uploadServletRequest.getFileAsStream("nonexistentFile", true));
	}

	private static final byte[] _BYTES =
		"Enterprise. Open Source. For Life.".getBytes();

	private static String _fileNameParameter;

}