/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.internal.typescript;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.rest.builder.internal.freemarker.tool.java.parser.util.OpenAPIParserUtil;
import com.liferay.portal.tools.rest.builder.internal.freemarker.util.OpenAPIUtil;
import com.liferay.portal.tools.rest.builder.internal.util.FileUtil;
import com.liferay.portal.tools.rest.builder.internal.yaml.config.ConfigYAML;

import java.io.File;

import java.net.URL;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Adolfo Pérez
 */
public class TypeScriptClientUtilTest {

	@Test
	public void testGenerateTypeScriptClient() throws Exception {
		Map<String, String> models = _generateTypeScriptClient(false);

		String externalChildEntity1 = models.get("ExternalChildEntity1.ts");

		Assert.assertTrue(
			externalChildEntity1,
			externalChildEntity1.contains(
				"export class ExternalChildEntity1 extends ExternalEntity {"));
		Assert.assertEquals(
			externalChildEntity1, 1,
			StringUtil.count(
				externalChildEntity1, "\"externalProperty\"?: string;"));

		String externalChildEntity2 = models.get("ExternalChildEntity2.ts");

		Assert.assertTrue(
			externalChildEntity2,
			externalChildEntity2.contains(
				"export class ExternalChildEntity2 extends ExternalEntity {"));
		Assert.assertEquals(
			externalChildEntity2, 1,
			StringUtil.count(
				externalChildEntity2, "\"mixinProperty\"?: string;"));

		Assert.assertEquals(
			_generateTypeScriptClient(false), _generateTypeScriptClient(true));
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private Map<String, String> _generateTypeScriptClient(
			boolean mergePropertySchemas)
		throws Exception {

		Map<String, String> models = new HashMap<>();

		String rootDirName = StringUtil.randomString();

		File externalConfigDir = temporaryFolder.newFolder(
			rootDirName, "external-impl");

		FileUtil.write(
			new File(externalConfigDir, "rest-config.yaml"),
			_read("external-rest-config.yaml"));

		File externalOpenAPIFile = new File(
			externalConfigDir, "rest-openapi.yaml");

		FileUtil.write(
			externalOpenAPIFile, _read("external-rest-openapi.yaml"));

		ConfigYAML configYAML = new ConfigYAML();

		configYAML.setAuthor("Test Author");

		File testConfigDir = temporaryFolder.newFolder(
			rootDirName, "test-impl");

		configYAML.setBaseDir(testConfigDir.getPath());

		configYAML.setCompatibilityVersion(16);

		if (mergePropertySchemas) {
			OpenAPIUtil.getAllSchemas(
				configYAML,
				OpenAPIParserUtil.loadOpenAPIYAML(
					FileUtil.read(externalOpenAPIFile)));
		}

		TypeScriptClientUtil.generateTypeScriptClient(
			testConfigDir, configYAML, new File(testConfigDir, "copyright.txt"),
			_read("test-rest-openapi.yaml"));

		File modelsDir = new File(
			testConfigDir.getParentFile(), "test-client-js/src/models");

		for (File file : modelsDir.listFiles()) {
			models.put(file.getName(), FileUtil.read(file));
		}

		return models;
	}

	private String _read(String fileName) throws Exception {
		URL url = TypeScriptClientUtilTest.class.getResource(
			"dependencies/" + fileName);

		return FileUtil.read(new File(url.toURI()));
	}

}