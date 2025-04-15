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
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Manuel de la Peña
 */
@RunWith(Arquillian.class)
public class UploadServletRequestWhenGettingParameterNamesTest {

	@ClassRule
	@Rule
	public static final TestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_fileNameParameter = RandomTestUtil.randomString();
	}

	@Test
	public void testShouldMergeRegularFileAndRequestParameters()
		throws Exception {

		Map<String, FileItem[]> fileParameters =
			PortletContainerTestUtil.getFileParameters(10, _BYTES);

		Map<String, List<String>> regularParameters =
			PortletContainerTestUtil.getRegularParameters(10);

		LiferayServletRequest liferayServletRequest =
			PortletContainerTestUtil.getMultipartRequest(
				_fileNameParameter, _BYTES);

		String parameter = RandomTestUtil.randomString();

		MockHttpServletRequest mockHttpServletRequest =
			(MockHttpServletRequest)liferayServletRequest.getRequest();

		mockHttpServletRequest.addParameter(parameter, parameter);

		UploadServletRequest uploadServletRequest =
			UploadTestUtil.createUploadServletRequest(
				(HttpServletRequest)liferayServletRequest.getRequest(),
				fileParameters, regularParameters);

		Enumeration<String> enumeration =
			uploadServletRequest.getParameterNames();

		List<String> parameterNamesList = Collections.list(enumeration);

		for (Map.Entry<String, List<String>> entry :
				regularParameters.entrySet()) {

			Assert.assertTrue(
				parameterNamesList.toString(),
				parameterNamesList.contains(entry.getKey()));
		}

		for (Map.Entry<String, FileItem[]> entry : fileParameters.entrySet()) {
			Assert.assertTrue(
				parameterNamesList.toString(),
				parameterNamesList.contains(entry.getKey()));
		}

		Assert.assertTrue(
			parameterNamesList.toString(),
			parameterNamesList.contains(parameter));
	}

	private static final byte[] _BYTES =
		"Enterprise. Open Source. For Life.".getBytes();

	private static String _fileNameParameter;

}