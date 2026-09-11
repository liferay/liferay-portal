/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.token.definition.internal.jaxrs.application;

import com.liferay.frontend.token.definition.internal.FrontendTokenDefinitionImpl;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.URLUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.ws.rs.core.Response;

import java.io.File;

import java.net.URL;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.mockito.Mockito;

/**
 * @author Saurasish Basak
 */
public class FrontendTokenDefinitionApplicationTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_frontendTokenDefinitionApplication, "_jsonFactory",
			new JSONFactoryImpl());

		ReflectionTestUtil.setFieldValue(
			_frontendTokenDefinitionApplication, "_language",
			Mockito.mock(Language.class));
	}

	@Test
	public void testGetResponseIgnoresTempFileExtension() throws Exception {
		Response response = _getResponse(
			_getTempFile("upload_00000002.json"),
			RandomTestUtil.randomString() + ".txt");

		Assert.assertEquals(
			Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}

	@Test
	public void testGetResponseWithJSONFileName() throws Exception {
		Response response = _getResponse(
			_getTempFile("upload_00000001.tmp"),
			RandomTestUtil.randomString() + ".json");

		Assert.assertEquals(
			Response.Status.OK.getStatusCode(), response.getStatus());
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private Response _getResponse(File file, String fileName) {
		return ReflectionTestUtil.invoke(
			_frontendTokenDefinitionApplication, "_getResponse",
			new Class<?>[] {File.class, String.class, Locale.class}, file,
			fileName, LocaleUtil.US);
	}

	private File _getTempFile(String name) throws Exception {
		File file = temporaryFolder.newFile(name);

		URL url = FrontendTokenDefinitionImpl.class.getResource(
			"dependencies/frontend-token-definition.json");

		FileUtil.write(file, URLUtil.toString(url));

		return file;
	}

	private final FrontendTokenDefinitionApplication
		_frontendTokenDefinitionApplication =
			new FrontendTokenDefinitionApplication();

}