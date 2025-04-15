/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.portlet.container.upload.servlet.request.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upload.FileItem;
import com.liferay.portal.kernel.upload.UploadServletRequest;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ProgressTracker;
import com.liferay.portal.osgi.web.portlet.container.test.util.PortletContainerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upload.LiferayServletRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

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
public class UploadServletRequestWhenCreatingFromMainConstructorTest {

	@ClassRule
	@Rule
	public static final TestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_fileNameParameter = RandomTestUtil.randomString();
	}

	@Test
	public void testShouldAddProgressTrackerToSession() {
		LiferayServletRequest liferayServletRequest =
			PortletContainerTestUtil.getMultipartRequest(
				_fileNameParameter, _BYTES);

		HttpServletRequest mockHttpServletRequest =
			(HttpServletRequest)liferayServletRequest.getRequest();

		_portal.getUploadServletRequest(mockHttpServletRequest);

		HttpSession mockHttpSession = mockHttpServletRequest.getSession();

		Assert.assertNotNull(
			mockHttpSession.getAttribute(ProgressTracker.PERCENT));
	}

	@Test
	public void testShouldNotPopulateParameters() {
		LiferayServletRequest liferayServletRequest =
			PortletContainerTestUtil.getMultipartRequest(
				_fileNameParameter, _BYTES);

		HttpServletRequest mockHttpServletRequest =
			(HttpServletRequest)liferayServletRequest.getRequest();

		UploadServletRequest uploadServletRequest =
			_portal.getUploadServletRequest(mockHttpServletRequest);

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

	private static final byte[] _BYTES =
		"Enterprise. Open Source. For Life.".getBytes();

	private static String _fileNameParameter;

	@Inject
	private Portal _portal;

}