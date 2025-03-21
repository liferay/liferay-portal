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

import java.util.Collections;
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
public class UploadServletRequestWhenCreatingFromParametrizedConstructorTest {

	@ClassRule
	@Rule
	public static final TestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_fileNameParameter = RandomTestUtil.randomString();
	}

	@Test
	public void testShouldNotPopulateParametersWithEmptyParameters() {
		Map<String, FileItem[]> fileParameters = Collections.emptyMap();

		LiferayServletRequest liferayServletRequest =
			PortletContainerTestUtil.getMultipartRequest(
				_fileNameParameter, _BYTES);

		UploadServletRequest uploadServletRequest =
			UploadTestUtil.createUploadServletRequest(
				(HttpServletRequest)liferayServletRequest.getRequest(),
				fileParameters, new HashMap<String, List<String>>());

		Map<String, FileItem[]> multipartParameterMap =
			uploadServletRequest.getMultipartParameterMap();

		Assert.assertNotNull(multipartParameterMap);
		Assert.assertTrue(
			multipartParameterMap.toString(), multipartParameterMap.isEmpty());

		Map<String, List<String>> regularParameterMap =
			uploadServletRequest.getRegularParameterMap();

		Assert.assertNotNull(regularParameterMap);
		Assert.assertTrue(
			regularParameterMap.toString(), regularParameterMap.isEmpty());
	}

	@Test
	public void testShouldPopulateMultipartParametersWithFileParameters()
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

		Map<String, FileItem[]> multipartParameterMap =
			uploadServletRequest.getMultipartParameterMap();

		Assert.assertNotNull(multipartParameterMap);
		Assert.assertEquals(
			multipartParameterMap.toString(), 1, multipartParameterMap.size());

		Map<String, List<String>> regularParameterMap =
			uploadServletRequest.getRegularParameterMap();

		Assert.assertNotNull(regularParameterMap);
		Assert.assertTrue(
			regularParameterMap.toString(), regularParameterMap.isEmpty());
	}

	@Test
	public void testShouldPopulateRegularParametersWithRegularParameters() {
		Map<String, List<String>> regularParameters =
			PortletContainerTestUtil.getRegularParameters(10);

		LiferayServletRequest liferayServletRequest =
			PortletContainerTestUtil.getMultipartRequest(
				_fileNameParameter, _BYTES);

		UploadServletRequest uploadServletRequest =
			UploadTestUtil.createUploadServletRequest(
				(HttpServletRequest)liferayServletRequest.getRequest(),
				new HashMap<String, FileItem[]>(), regularParameters);

		Map<String, FileItem[]> multipartParameterMap =
			uploadServletRequest.getMultipartParameterMap();

		Assert.assertNotNull(multipartParameterMap);
		Assert.assertTrue(
			multipartParameterMap.toString(), multipartParameterMap.isEmpty());

		Map<String, List<String>> regularParameterMap =
			uploadServletRequest.getRegularParameterMap();

		Assert.assertNotNull(regularParameterMap);
		Assert.assertEquals(
			regularParameterMap.toString(), 10, regularParameterMap.size());
	}

	private static final byte[] _BYTES =
		"Enterprise. Open Source. For Life.".getBytes();

	private static String _fileNameParameter;

}