/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.portlet.container.upload.portlet.request.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upload.FileItem;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.osgi.web.portlet.container.test.util.PortletContainerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upload.LiferayServletRequest;
import com.liferay.portal.upload.test.util.UploadTestUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.io.InputStream;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

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
public class UploadPortletRequestWhenGettingFilesAsStreamTest {

	@ClassRule
	@Rule
	public static final TestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_portletNamespace = RandomTestUtil.randomString();
	}

	@Test
	public void testShouldReturnNullIfFileParametersAreEmpty()
		throws Exception {

		LiferayServletRequest liferayServletRequest =
			PortletContainerTestUtil.getMultipartRequest(
				_portletNamespace, _BYTES);

		UploadPortletRequest uploadPortletRequest =
			UploadTestUtil.createUploadPortletRequest(
				UploadTestUtil.createUploadServletRequest(
					(HttpServletRequest)liferayServletRequest.getRequest(),
					new HashMap<String, FileItem[]>(),
					new HashMap<String, List<String>>()),
				null, _portletNamespace);

		Assert.assertNull(
			uploadPortletRequest.getFilesAsStream("irrelevantName"));
	}

	@Test
	public void testShouldReturnNullIfNameIsNotAFileParameter()
		throws Exception {

		Map<String, FileItem[]> fileParameters =
			PortletContainerTestUtil.getFileParameters(
				1, _portletNamespace, _BYTES);

		LiferayServletRequest liferayServletRequest =
			PortletContainerTestUtil.getMultipartRequest(
				_portletNamespace, _BYTES);

		UploadPortletRequest uploadPortletRequest =
			UploadTestUtil.createUploadPortletRequest(
				UploadTestUtil.createUploadServletRequest(
					(HttpServletRequest)liferayServletRequest.getRequest(),
					fileParameters, new HashMap<String, List<String>>()),
				null, _portletNamespace);

		Assert.assertNull(
			uploadPortletRequest.getFilesAsStream("nonexistentFile"));
	}

	@Test
	public void testShouldReturnStreamsFromFileParameters() throws Exception {
		Map<String, FileItem[]> fileParameters =
			PortletContainerTestUtil.getFileParameters(
				10, _portletNamespace, _BYTES);

		LiferayServletRequest liferayServletRequest =
			PortletContainerTestUtil.getMultipartRequest(
				_portletNamespace, _BYTES);

		UploadPortletRequest uploadPortletRequest =
			UploadTestUtil.createUploadPortletRequest(
				UploadTestUtil.createUploadServletRequest(
					(HttpServletRequest)liferayServletRequest.getRequest(),
					fileParameters, new HashMap<String, List<String>>()),
				null, _portletNamespace);

		Map<String, FileItem[]> map =
			uploadPortletRequest.getMultipartParameterMap();

		Assert.assertEquals(map.toString(), 10, map.size());

		for (Map.Entry<String, FileItem[]> entry : map.entrySet()) {
			InputStream[] inputStreams = uploadPortletRequest.getFilesAsStream(
				entry.getKey());

			FileItem[] fileItems = entry.getValue();

			Assert.assertEquals(
				Arrays.toString(inputStreams), fileItems.length,
				inputStreams.length);

			Assert.assertEquals(
				Arrays.toString(inputStreams), 2, inputStreams.length);

			for (int i = 0; i < inputStreams.length; i++) {
				Assert.assertTrue(
					IOUtils.contentEquals(
						fileItems[i].getInputStream(), inputStreams[i]));
			}
		}
	}

	private static final byte[] _BYTES =
		"Enterprise. Open Source. For Life.".getBytes();

	private static String _portletNamespace;

}