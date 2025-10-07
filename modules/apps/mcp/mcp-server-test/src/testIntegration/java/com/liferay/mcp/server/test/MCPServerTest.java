/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.spec.McpSchema;

import java.io.UnsupportedEncodingException;

import java.util.Base64;
import java.util.Collections;
import java.util.List;

import org.hamcrest.CoreMatchers;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 */
@FeatureFlag("LPD-63311")
@RunWith(Arquillian.class)
public class MCPServerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void test() throws Exception {
		McpSyncClient mcpSyncClient = McpClient.sync(
			HttpClientSseClientTransport.builder(
				"http://localhost:8080/o/mcp/"
			).sseEndpoint(
				"sse"
			).customizeRequest(
				builder -> builder.header("Authorization", _getAuthorization())
			).build()
		).build();

		mcpSyncClient.initialize();

		McpSchema.ListToolsResult listToolsResult = mcpSyncClient.listTools();

		List<McpSchema.Tool> tools = listToolsResult.tools();

		Assert.assertEquals(tools.toString(), 3, tools.size());

		McpSchema.Tool tool1 = tools.get(0);

		Assert.assertEquals("call-http-endpoint", tool1.name());

		McpSchema.Tool tool2 = tools.get(1);

		Assert.assertEquals("get-openapi", tool2.name());

		McpSchema.Tool tool3 = tools.get(2);

		Assert.assertEquals("get-openapis", tool3.name());

		McpSchema.CallToolResult callToolResult = mcpSyncClient.callTool(
			new McpSchema.CallToolRequest(
				"get-openapis", Collections.emptyMap()));

		List<McpSchema.Content> contents = callToolResult.content();

		McpSchema.TextContent content = (McpSchema.TextContent)contents.get(0);

		callToolResult = mcpSyncClient.callTool(
			new McpSchema.CallToolRequest(
				"get-openapi",
				HashMapBuilder.<String, Object>put(
					"url",
					JSONFactoryUtil.createJSONObject(
						content.text()
					).getJSONArray(
						"/test"
					).getString(
						0
					)
				).build()));

		contents = callToolResult.content();

		content = (McpSchema.TextContent)contents.get(0);

		Assert.assertThat(content.text(), CoreMatchers.containsString("/test"));

		callToolResult = mcpSyncClient.callTool(
			new McpSchema.CallToolRequest(
				"call-http-endpoint",
				HashMapBuilder.<String, Object>put(
					"method", "GET"
				).put(
					"path", "/test/v1.0/test-entities"
				).build()));

		contents = callToolResult.content();

		content = (McpSchema.TextContent)contents.get(0);

		Assert.assertNotNull(
			JSONFactoryUtil.createJSONObject(
				content.text()
			).getJSONArray(
				"items"
			));

		mcpSyncClient.closeGracefully();
	}

	private String _getAuthorization() {
		try {
			Base64.Encoder encoder = Base64.getEncoder();

			String userNameAndPassword =
				"test@liferay.com:" + PropsValues.DEFAULT_ADMIN_PASSWORD;

			return "Basic " +
				new String(
					encoder.encode(userNameAndPassword.getBytes("UTF-8")),
					"UTF-8");
		}
		catch (UnsupportedEncodingException unsupportedEncodingException) {
			throw new RuntimeException(unsupportedEncodingException);
		}
	}

}