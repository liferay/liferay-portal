/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.rest.builder.internal.util.FileUtil;

import java.io.File;

import java.net.URL;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Daniel Raposo
 */
public class RESTBuilderTest {

	@Test
	public void testBuildWithInvalidOpenAPIFile() throws Exception {
		File configDir = temporaryFolder.newFolder("test-impl");

		FileUtil.write(
			new File(configDir, "rest-config.yaml"), _read("rest-config.yaml"));

		FileUtil.write(
			new File(configDir, "rest-openapi.yaml"),
			StringUtil.removeSubstring(
				_read("rest-openapi.yaml"), "    version: v1.0\n"));

		File apiFile = new File(
			configDir.getParentFile(),
			"test-client-js/src/v1_0/apis/FooAPI.ts");

		FileUtil.write(apiFile, StringUtil.randomString());

		RESTBuilder restBuilder = new RESTBuilder(
			new File(configDir, "copyright.txt"), configDir, null, null, null);

		try {
			restBuilder.build();

			Assert.fail();
		}
		catch (RuntimeException runtimeException) {
			Assert.assertTrue(apiFile.exists());

			String message = runtimeException.getMessage();

			Assert.assertTrue(
				message,
				message.contains(
					"File rest-openapi.yaml: Missing required field info: " +
						"version"));
		}
	}

	@Test
	public void testBuildWithSeveralOpenAPIFiles() throws Exception {
		File configDir = temporaryFolder.newFolder("test-impl");

		for (String fileName :
				new String[] {
					"rest-config.yaml", "rest-openapi-v2.0.yaml",
					"rest-openapi.yaml"
				}) {

			FileUtil.write(new File(configDir, fileName), _read(fileName));
		}

		RESTBuilder restBuilder = new RESTBuilder(
			new File(configDir, "copyright.txt"), configDir, null, null, null);

		restBuilder.build();

		File baseClientDir = new File(
			configDir.getParentFile(), "test-client-js");

		String indexString = FileUtil.read(
			new File(baseClientDir, "src/index.ts"));

		Assert.assertTrue(
			indexString,
			indexString.contains("export * as v1_0 from './v1_0';"));
		Assert.assertTrue(
			indexString,
			indexString.contains("export * as v2_0 from './v2_0';"));

		String packageJSONString = FileUtil.read(
			new File(baseClientDir, "package.json"));

		Assert.assertTrue(
			packageJSONString,
			packageJSONString.contains("\"./v1_0\": \"./src/v1_0/index.ts\""));
		Assert.assertTrue(
			packageJSONString,
			packageJSONString.contains("\"./v2_0\": \"./src/v2_0/index.ts\""));

		File version1APIsDir = new File(baseClientDir, "src/v1_0/apis");

		Assert.assertArrayEquals(
			new String[] {"FooAPI.ts"}, version1APIsDir.list());

		File version2APIsDir = new File(baseClientDir, "src/v2_0/apis");

		Assert.assertArrayEquals(
			new String[] {"BarAPI.ts"}, version2APIsDir.list());
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private String _read(String fileName) throws Exception {
		URL url = RESTBuilderTest.class.getResource("dependencies/" + fileName);

		return FileUtil.read(new File(url.toURI()));
	}

}