/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.portlet.container.upload.servlet.request.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.servlet.ServletInputStreamAdapter;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upload.FileItem;
import com.liferay.portal.kernel.upload.UploadServletRequest;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.osgi.web.portlet.container.test.util.PortletContainerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upload.LiferayInputStream;
import com.liferay.portal.upload.LiferayServletRequest;
import com.liferay.portal.upload.test.util.UploadTestUtil;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;

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
public class UploadServletRequestWhenGettingInputStreamTest {

	@ClassRule
	@Rule
	public static final TestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_fileNameParameter = RandomTestUtil.randomString();
	}

	@Test
	public void testShouldNotReturnLiferayInputStream() throws Exception {
		LiferayServletRequest liferayServletRequest =
			PortletContainerTestUtil.getMultipartRequest(
				_fileNameParameter, _BYTES);

		UploadServletRequest uploadServletRequest =
			_portal.getUploadServletRequest(
				(HttpServletRequest)liferayServletRequest.getRequest());

		ServletInputStream servletInputStream =
			uploadServletRequest.getInputStream();

		Assert.assertFalse(servletInputStream instanceof LiferayInputStream);

		uploadServletRequest = UploadTestUtil.createUploadServletRequest(
			(HttpServletRequest)liferayServletRequest.getRequest(),
			new HashMap<String, FileItem[]>(),
			new HashMap<String, List<String>>());

		servletInputStream = uploadServletRequest.getInputStream();

		Assert.assertFalse(servletInputStream instanceof LiferayInputStream);
	}

	@Test
	public void testShouldReturnServletInputStreamAdapter() throws Exception {
		LiferayServletRequest liferayServletRequest =
			PortletContainerTestUtil.getMultipartRequest(
				_fileNameParameter, _BYTES);

		UploadServletRequest uploadServletRequest =
			_portal.getUploadServletRequest(
				(HttpServletRequest)liferayServletRequest.getRequest());

		ServletInputStream servletInputStream =
			uploadServletRequest.getInputStream();

		Assert.assertTrue(
			servletInputStream instanceof ServletInputStreamAdapter);
	}

	private static final byte[] _BYTES =
		"Enterprise. Open Source. For Life.".getBytes();

	private static String _fileNameParameter;

	@Inject
	private Portal _portal;

}