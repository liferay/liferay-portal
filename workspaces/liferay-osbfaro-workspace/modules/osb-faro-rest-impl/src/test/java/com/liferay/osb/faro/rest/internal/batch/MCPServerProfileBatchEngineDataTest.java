/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.batch;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.InputStream;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Marcos Martins
 */
public class MCPServerProfileBatchEngineDataTest {

	@Test
	public void testMCPServerProfile() throws Exception {
		String json = _read("00-object-entry.batch-engine-data.json");

		Assert.assertTrue(
			json.contains(
				"\"externalReferenceCode\": \"LDP_MCP_SERVER_PROFILE\""));
		Assert.assertTrue(json.contains("\"instructions\": \""));
		Assert.assertTrue(json.contains("\"name\": \"ldp\""));
		Assert.assertFalse(json.contains("profileStatus"));
		Assert.assertTrue(
			json.contains("\"updateStrategy\": \"PARTIAL_UPDATE\""));

		json = _read("02-object-entry.batch-engine-data.json");

		Assert.assertTrue(
			json.contains(
				"\"externalReferenceCode\": \"LDP_MCP_SERVER_PROFILE\""));
		Assert.assertTrue(json.contains("\"profileStatus\": \"active\""));
		Assert.assertTrue(
			json.contains("\"updateStrategy\": \"PARTIAL_UPDATE\""));
	}

	@Test
	public void testMCPServerProfileTools() throws Exception {
		String json = _read("01-object-entry.batch-engine-data.json");

		List<String> toolNames = _getValues(json, "toolName");

		Assert.assertEquals(
			TransformUtil.transform(
				toolNames, toolName -> "LDP_MCP_TOOL_" + toolName),
			_getValues(json, "externalReferenceCode"));
		Assert.assertEquals(
			Collections.nCopies(toolNames.size(), "LDP_MCP_SERVER_PROFILE"),
			_getValues(
				json, "r_mcpServerProfileToTools_l_mcpServerProfileERC"));
		Assert.assertEquals(
			_getValues(
				new String(
					Files.readAllBytes(Paths.get("rest-openapi.yaml")),
					StandardCharsets.UTF_8),
				Pattern.compile("operationId: (\\w+)")),
			toolNames);
		Assert.assertEquals(
			Collections.nCopies(toolNames.size(), "faro-rest-v1.0"),
			_getValues(json, "toolSetName"));
	}

	private List<String> _getValues(String content, Pattern pattern) {
		List<String> values = new ArrayList<>();

		Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {
			values.add(matcher.group(1));
		}

		Collections.sort(values);

		return values;
	}

	private List<String> _getValues(String content, String key) {
		return _getValues(
			content, Pattern.compile("\"" + key + "\": \"([^\"]+)\""));
	}

	private String _read(String fileName) throws Exception {
		ClassLoader classLoader =
			MCPServerProfileBatchEngineDataTest.class.getClassLoader();

		try (InputStream inputStream = classLoader.getResourceAsStream(
				"com/liferay/osb/faro/rest/internal/batch/" + fileName)) {

			return StringUtil.read(inputStream);
		}
	}

}