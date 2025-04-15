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
public class UploadPortletRequestWhenGettingParameterNamesTest {

	@ClassRule
	@Rule
	public static final TestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_portletNamespace = RandomTestUtil.randomString();
	}

	@Test
	public void testShouldMergeFileAndRequestParameters() throws Exception {
		Map<String, FileItem[]> fileParameters =
			PortletContainerTestUtil.getFileParameters(
				10, _portletNamespace, _BYTES);

		Map<String, List<String>> regularParameters =
			PortletContainerTestUtil.getRegularParameters(10);

		LiferayServletRequest liferayServletRequest =
			PortletContainerTestUtil.getMultipartRequest(
				_portletNamespace, _BYTES);

		String parameter = RandomTestUtil.randomString();

		MockHttpServletRequest mockHttpServletRequest =
			(MockHttpServletRequest)liferayServletRequest.getRequest();

		mockHttpServletRequest.addParameter(parameter, parameter);

		UploadPortletRequest uploadPortletRequest =
			UploadTestUtil.createUploadPortletRequest(
				UploadTestUtil.createUploadServletRequest(
					(HttpServletRequest)liferayServletRequest.getRequest(),
					fileParameters, regularParameters),
				null, _portletNamespace);

		Enumeration<String> enumeration =
			uploadPortletRequest.getParameterNames();

		List<String> parameterNamesList = Collections.list(enumeration);

		for (Map.Entry<String, List<String>> entry :
				regularParameters.entrySet()) {

			Assert.assertTrue(
				parameterNamesList.toString(),
				parameterNamesList.contains(entry.getKey()));
		}

		for (Map.Entry<String, FileItem[]> entry : fileParameters.entrySet()) {
			Assert.assertFalse(
				parameterNamesList.toString(),
				parameterNamesList.contains(entry.getKey()));

			String fileParameter = entry.getKey();

			fileParameter = fileParameter.substring(_portletNamespace.length());

			Assert.assertTrue(
				parameterNamesList.toString(),
				parameterNamesList.contains(fileParameter));
		}

		Assert.assertTrue(
			parameterNamesList.toString(),
			parameterNamesList.contains(parameter));
	}

	private static final byte[] _BYTES =
		"Enterprise. Open Source. For Life.".getBytes();

	private static String _portletNamespace;

}