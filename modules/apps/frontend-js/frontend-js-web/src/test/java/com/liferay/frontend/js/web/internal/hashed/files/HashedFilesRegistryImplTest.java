/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.web.internal.hashed.files;

import com.liferay.frontend.js.web.internal.util.FrontendJSWebUtil;
import com.liferay.frontend.js.web.test.util.FrontendJSWebTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.FIPSAlgorithmTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.ServletContext;

import java.net.URL;

import java.security.MessageDigest;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

import org.osgi.framework.BundleContext;

/**
 * @author Iván Zaera Avellón
 */
public class HashedFilesRegistryImplTest {

	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		FrontendJSWebUtil.clearCache();
	}

	@Test
	public void testGetResource() throws Exception {

		// Nonroot context plus proxy setup

		HashedFilesRegistryImpl hashedFilesRegistryImpl =
			_newHashedFilesRegistryImpl(
				"/dxp", "/liferay", "/dxp/o/frontend-js-web");

		Assert.assertNotNull(
			hashedFilesRegistryImpl.getResource(
				"/dxp/o/frontend-js-web/main.css"));

		// Nonroot context setup

		hashedFilesRegistryImpl = _newHashedFilesRegistryImpl(
			"/dxp", StringPool.BLANK, "/dxp/o/frontend-js-web");

		Assert.assertNotNull(
			hashedFilesRegistryImpl.getResource(
				"/dxp/o/frontend-js-web/main.css"));

		// Proxy setup

		hashedFilesRegistryImpl = _newHashedFilesRegistryImpl(
			StringPool.BLANK, "/liferay", "/o/frontend-js-web");

		Assert.assertNotNull(
			hashedFilesRegistryImpl.getResource("/o/frontend-js-web/main.css"));

		// Vanilla setup

		hashedFilesRegistryImpl = _newHashedFilesRegistryImpl(
			StringPool.BLANK, StringPool.BLANK, "/o/frontend-js-web");

		Assert.assertNotNull(
			hashedFilesRegistryImpl.getResource("/o/frontend-js-web/main.css"));
	}

	@Test
	public void testGetServletContextHash() throws Exception {
		Map<String, String> hashedFileURIs = HashMapBuilder.put(
			"/o/frontend-js-web/main.css",
			HashedFilesUtil.addHash(
				"/o/frontend-js-web/main.css", RandomTestUtil.randomString())
		).build();

		FIPSAlgorithmTestUtil.assertAlgorithmSwitch(
			DigesterUtil.MD5, MessageDigest.class, DigesterUtil.SHA_256,
			MessageDigest::getInstance,
			() -> ReflectionTestUtil.invoke(
				new HashedFilesRegistryImpl(), "_getServletContextHash",
				new Class<?>[] {Map.class}, hashedFileURIs));
	}

	private Map<String, HashedFilesRegistryImpl.DataBag> _mockDataBags(
			String servletContextPath)
		throws Exception {

		ServletContext servletContext = Mockito.mock(ServletContext.class);

		Mockito.when(
			servletContext.getResource("/main.css")
		).thenReturn(
			Mockito.mock(URL.class)
		);

		return HashMapBuilder.put(
			servletContextPath,
			new HashedFilesRegistryImpl.DataBag(
				HashMapBuilder.put(
					"/main.css",
					HashedFilesUtil.addHash(
						"/main.css",
						FrontendJSWebTestUtil.randomHashedFileHash())
				).build(),
				servletContext, null)
		).build();
	}

	private Portal _mockPortal(String contextPath, String proxyPath)
		throws Exception {

		Portal portal = Mockito.mock(Portal.class);

		Mockito.when(
			portal.getCDNHost(Mockito.any())
		).thenReturn(
			StringPool.BLANK
		);

		Mockito.when(
			portal.getPathContext()
		).thenReturn(
			proxyPath + contextPath
		);

		Mockito.when(
			portal.getPathProxy()
		).thenReturn(
			proxyPath
		);

		return portal;
	}

	private HashedFilesRegistryImpl _newHashedFilesRegistryImpl(
			String contextPath, String proxyPath, String servletContextPath)
		throws Exception {

		HashedFilesRegistryImpl hashedFilesRegistryImpl =
			new HashedFilesRegistryImpl();

		ReflectionTestUtil.setFieldValue(
			hashedFilesRegistryImpl, "_dataBags",
			_mockDataBags(servletContextPath));
		ReflectionTestUtil.setFieldValue(
			hashedFilesRegistryImpl, "_portal",
			_mockPortal(contextPath, proxyPath));

		hashedFilesRegistryImpl.activate(Mockito.mock(BundleContext.class));

		return hashedFilesRegistryImpl;
	}

}