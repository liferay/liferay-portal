/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.servlet.test;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.mcp.server.rest.test.util.MCPServerTestUtil;
import com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata;
import com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata;
import com.liferay.oauth.client.persistence.service.OAuthClientASLocalMetadataLocalService;
import com.liferay.oauth.client.persistence.service.OAuthClientPRLocalMetadataLocalService;
import com.liferay.oauth2.provider.model.OAuth2Authorization;
import com.liferay.oauth2.provider.service.OAuth2AuthorizationLocalService;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpSchema;

import java.io.Serializable;

import java.net.URI;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hamcrest.CoreMatchers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;

/**
 * @author Alejandro Tardín
 * @author Beni Herrero
 */
@FeatureFlags(
	featureFlags = {
		@FeatureFlag("LPD-63311"), @FeatureFlag("LPD-63415"),
		@FeatureFlag("LPD-63416")
	}
)
@RunWith(Arquillian.class)
public class MCPServerServletTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		MCPServerTestUtil.processBatchEngineUnits();
	}

	@Test
	public void testService() throws Exception {
		Assert.assertEquals(
			404, HTTPTestUtil.invokeToHttpCode(null, "mcp", Http.Method.GET));

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						"com.liferay.mcp.server.rest.internal.configuration." +
							"MCPServerConfiguration",
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", true
						).build())) {

			_assertInvalidTokenChallenge(
				_getResponse("Bearer " + RandomTestUtil.randomString()),
				"Access token is unknown or revoked");

			String expiredAccessToken = RandomTestUtil.randomString();

			_addOAuth2Authorization(
				expiredAccessToken,
				new Date(System.currentTimeMillis() - Time.HOUR),
				Collections.singletonList(_getMCPURL()));

			_assertInvalidTokenChallenge(
				_getResponse("Bearer " + expiredAccessToken),
				"Access token has expired");

			String invalidAccessToken = RandomTestUtil.randomString();

			_addOAuth2Authorization(
				invalidAccessToken,
				new Date(System.currentTimeMillis() + Time.HOUR),
				Collections.singletonList(RandomTestUtil.randomString()));

			_assertInvalidTokenChallenge(
				_getResponse("Bearer " + invalidAccessToken),
				"Access token is not bound to this MCP server");

			Http.Response response = _getResponse(null);

			Assert.assertEquals(401, response.getResponseCode());

			String wwwAuthenticate = response.getHeader(
				HttpHeaders.WWW_AUTHENTICATE);

			Assert.assertTrue(
				wwwAuthenticate,
				wwwAuthenticate.startsWith("Bearer realm=\"mcp\""));
			Assert.assertTrue(
				wwwAuthenticate,
				wwwAuthenticate.contains("resource_metadata="));
			Assert.assertFalse(
				wwwAuthenticate, wwwAuthenticate.contains("error="));

			_assertInvalidTokenChallenge(
				_getResponse(RandomTestUtil.randomString()),
				"Authorization header is not a bearer token");

			String userNameAndPassword =
				"test@liferay.com:" + PropsValues.DEFAULT_ADMIN_PASSWORD;

			for (String authorization :
					List.of(
						"Basic " +
							Base64.encode(userNameAndPassword.getBytes()),
						"Bearer " + _getAccessToken())) {

				_testServiceWithDataMasks(authorization);
				_testServiceWithModifiedProfile(authorization);
				_testServiceWithNoContentResponse(authorization);
				_testServiceWithoutProfile(authorization);
				_testServiceWithoutSession(authorization);
				_testServiceWithProfile(authorization);
			}
		}
	}

	private OAuth2Authorization _addOAuth2Authorization(
			String accessTokenContent, Date accessTokenExpirationDate,
			List<String> audiencesList)
		throws Exception {

		return _oAuth2AuthorizationLocalService.addOAuth2Authorization(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomLong(),
			RandomTestUtil.randomLong(), accessTokenContent, new Date(),
			accessTokenExpirationDate, audiencesList,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			null, null);
	}

	private ObjectEntry _addObjectEntry(String name, String... tools)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_MCP_SERVER_PROFILE", TestPropsValues.getCompanyId());

		return _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"description", RandomTestUtil.randomString()
			).put(
				"name", name
			).put(
				"tools", StringUtil.merge(tools, "\n")
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private void _assertInvalidTokenChallenge(
		Http.Response response, String description) {

		Assert.assertEquals(401, response.getResponseCode());

		String wwwAuthenticate = response.getHeader(
			HttpHeaders.WWW_AUTHENTICATE);

		Assert.assertTrue(
			wwwAuthenticate,
			wwwAuthenticate.contains("error=\"invalid_token\""));
		Assert.assertTrue(
			wwwAuthenticate,
			wwwAuthenticate.contains(
				"error_description=\"" + description + "\""));
	}

	private void _assertTool(
			McpSchema.Tool tool, String expectedName,
			String expectedSchemaFileName)
		throws Exception {

		Assert.assertEquals(expectedName, tool.name());

		JSONAssert.assertEquals(
			StringUtil.read(
				MCPServerServletTest.class.getResourceAsStream(
					"dependencies/" + expectedSchemaFileName)),
			new ObjectMapper(
			).writeValueAsString(
				tool.inputSchema()
			),
			false);
	}

	private ObjectEntry _fetchMCPServerProfileDataMaskObjectEntry(
			long dataMaskObjectEntryId,
			String mcpServerProfileExternalReferenceCode)
		throws Exception {

		ObjectDefinition mcpServerProfileDataMaskObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_MCP_SERVER_PROFILE_DATA_MASK",
					TestPropsValues.getCompanyId());

		ObjectEntry dataMaskObjectEntry =
			_objectEntryLocalService.fetchObjectEntry(dataMaskObjectEntryId);

		for (ObjectEntry mcpServerProfileDataMaskObjectEntry :
				_objectEntryLocalService.getObjectEntries(
					0,
					mcpServerProfileDataMaskObjectDefinition.
						getObjectDefinitionId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

			Map<String, Serializable> values =
				mcpServerProfileDataMaskObjectEntry.getValues();

			String dataMaskExternalReferenceCode = (String)values.get(
				"dataMaskExternalReferenceCode");

			if (Objects.equals(
					dataMaskExternalReferenceCode,
					dataMaskObjectEntry.getExternalReferenceCode()) &&
				Objects.equals(
					mcpServerProfileExternalReferenceCode,
					values.get("mcpServerProfileExternalReferenceCode"))) {

				return mcpServerProfileDataMaskObjectEntry;
			}
		}

		return null;
	}

	private String _get(String url) throws Exception {
		Http.Options options = new Http.Options();

		options.setFollowRedirects(false);
		options.setLocation(url);

		return _http.URLtoString(options);
	}

	private String _getAccessToken() throws Exception {
		String portalURL =
			"http://localhost:" + PortalUtil.getPortalServerPort(false);

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			_oAuthClientASLocalMetadataLocalService.
				fetchOAuthClientASLocalMetadata(
					TestPropsValues.getCompanyId(), portalURL);

		if (oAuthClientASLocalMetadata != null) {
			_oAuthClientASLocalMetadataLocalService.
				deleteOAuthClientASLocalMetadata(oAuthClientASLocalMetadata);
		}

		_oAuthClientASLocalMetadataLocalService.addOAuthClientASLocalMetadata(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			portalURL + "/o/oauth2/authorize", portalURL,
			portalURL + "/o/oauth2/jwks", true,
			portalURL + "/o/oauth2/register",
			new String[] {"authorization_code", "client_credentials"},
			new String[] {"everything"}, new String[] {"public"},
			portalURL + "/o/oauth2/token", null);

		String mcpResourceURI = _getMCPURL();

		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata =
			_oAuthClientPRLocalMetadataLocalService.
				fetchOAuthClientPRLocalMetadata(
					TestPropsValues.getCompanyId(), mcpResourceURI);

		if (oAuthClientPRLocalMetadata != null) {
			_oAuthClientPRLocalMetadataLocalService.
				deleteOAuthClientPRLocalMetadata(oAuthClientPRLocalMetadata);
		}

		_oAuthClientPRLocalMetadataLocalService.addOAuthClientPRLocalMetadata(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			new String[] {portalURL}, new String[] {"header"}, true,
			mcpResourceURI, "Liferay MCP Server", new String[] {"everything"});

		Http.Options options = new Http.Options();

		options.setFollowRedirects(false);
		options.setLocation(_getMCPURL());

		_http.URLtoString(options);

		Http.Response response = options.getResponse();

		Assert.assertEquals(401, response.getResponseCode());

		Matcher matcher = _resourceMetadataPattern.matcher(
			response.getHeader("WWW-Authenticate"));

		Assert.assertTrue(matcher.find());

		JSONObject protectedResourceMetadataJSONObject =
			JSONFactoryUtil.createJSONObject(
				_get(
					portalURL + "/.well-known/oauth-protected-resource/o/mcp"));

		String authorizationServerURL = JSONUtil.getValueAsString(
			protectedResourceMetadataJSONObject,
			"JSONArray/authorization_servers", "Object/0");

		JSONObject authorizationServerMetadataJSONObject =
			JSONFactoryUtil.createJSONObject(
				_get(
					authorizationServerURL +
						"/.well-known/oauth-authorization-server"));

		String clientId = null;
		String redirectURI = "https://example.com/callback";

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						"com.liferay.oauth2.provider.rest.internal." +
							"configuration." +
								"OAuth2DynamicRegistrationConfiguration",
						HashMapDictionaryBuilder.<String, Object>put(
							"oauth2.dynamic.registration.allowed.grant.types",
							new String[] {"*"}
						).put(
							"oauth2.dynamic.registration.allowed.hosts",
							new String[] {"*"}
						).put(
							"oauth2.dynamic.registration.allowed.redirect." +
								"uri.patterns",
							new String[] {"*"}
						).put(
							"oauth2.dynamic.registration.allowed.scopes",
							new String[] {"*"}
						).put(
							"oauth2.dynamic.registration.require.initial." +
								"access.token",
							false
						).build())) {

			options = new Http.Options();

			options.addHeader("Content-Type", ContentTypes.APPLICATION_JSON);
			options.setBody(
				JSONUtil.put(
					"client_name", RandomTestUtil.randomString()
				).put(
					"grant_types", JSONUtil.putAll("authorization_code")
				).put(
					"redirect_uris", JSONUtil.putAll(redirectURI)
				).put(
					"response_types", JSONUtil.putAll("code")
				).put(
					"scope", "everything"
				).put(
					"token_endpoint_auth_method", "none"
				).toString(),
				ContentTypes.APPLICATION_JSON, StringPool.UTF8);
			options.setFollowRedirects(false);
			options.setLocation(
				authorizationServerMetadataJSONObject.getString(
					"registration_endpoint"));
			options.setPost(true);

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
				_http.URLtoString(options));

			clientId = jsonObject.getString("client_id");
		}

		portalURL = "http://localhost:" + PortalUtil.getPortalServerPort(false);

		options = new Http.Options();

		options.setLocation(portalURL + "/web/guest");

		String body = _http.URLtoString(options);

		matcher = _authTokenPattern.matcher(body);

		Assert.assertTrue(matcher.find());

		User user = TestPropsValues.getUser();

		options = new Http.Options();

		options.setBody(
			StringBundler.concat(
				"login=", URLCodec.encodeURL(user.getEmailAddress()),
				"&p_auth=", matcher.group(2), "&password=",
				URLCodec.encodeURL(PropsValues.DEFAULT_ADMIN_PASSWORD)),
			ContentTypes.APPLICATION_X_WWW_FORM_URLENCODED, StringPool.UTF8);
		options.setCookies(_http.getCookies());
		options.setLocation(portalURL + "/c/portal/login");
		options.setPost(true);

		_http.URLtoString(options);

		String authorizationEndpoint =
			authorizationServerMetadataJSONObject.getString(
				"authorization_endpoint");

		String authorizationURL = HttpComponentsUtil.addParameter(
			authorizationEndpoint, "client_id", clientId);

		String codeVerifier = RandomTestUtil.randomString(50);

		authorizationURL = HttpComponentsUtil.addParameter(
			authorizationURL, "code_challenge",
			StringUtil.removeChar(
				StringUtil.replace(
					DigesterUtil.digestBase64(
						DigesterUtil.SHA_256, codeVerifier),
					new char[] {CharPool.PLUS, CharPool.SLASH},
					new char[] {CharPool.MINUS, CharPool.UNDERLINE}),
				CharPool.EQUAL));
		authorizationURL = HttpComponentsUtil.addParameter(
			authorizationURL, "code_challenge_method", "S256");
		authorizationURL = HttpComponentsUtil.addParameter(
			authorizationURL, "redirect_uri", redirectURI);
		authorizationURL = HttpComponentsUtil.addParameter(
			authorizationURL, "resource",
			protectedResourceMetadataJSONObject.getString("resource"));
		authorizationURL = HttpComponentsUtil.addParameter(
			authorizationURL, "response_type", "code");
		authorizationURL = HttpComponentsUtil.addParameter(
			authorizationURL, "scope", "everything");
		authorizationURL = HttpComponentsUtil.addParameter(
			authorizationURL, "state", RandomTestUtil.randomString());

		options = new Http.Options();

		options.addHeader("Accept", ContentTypes.TEXT_HTML);
		options.setCookies(_http.getCookies());
		options.setFollowRedirects(false);
		options.setLocation(authorizationURL);

		_http.URLtoString(options);

		response = options.getResponse();

		Map<String, String[]> parameterMap = HttpComponentsUtil.getParameterMap(
			_getQueryString(response.getHeader("Location")));

		StringBundler sb = new StringBundler();

		sb.append("oauthDecision=allow");

		for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
			String key = entry.getKey();

			if (!key.startsWith("oauth2_")) {
				continue;
			}

			sb.append(StringPool.AMPERSAND);
			sb.append(URLCodec.encodeURL(key.substring(7)));
			sb.append(StringPool.EQUAL);
			sb.append(URLCodec.encodeURL(entry.getValue()[0]));
		}

		options = new Http.Options();

		options.setBody(
			sb.toString(), ContentTypes.APPLICATION_X_WWW_FORM_URLENCODED,
			StringPool.UTF8);
		options.setCookies(_http.getCookies());
		options.setFollowRedirects(false);
		options.setLocation(authorizationEndpoint + "/decision");
		options.setPost(true);

		_http.URLtoString(options);

		response = options.getResponse();

		Map<String, String[]> decisionParameterMap =
			HttpComponentsUtil.getParameterMap(
				_getQueryString(response.getHeader("Location")));

		String code = decisionParameterMap.get("code")[0];

		String tokenEndpoint = authorizationServerMetadataJSONObject.getString(
			"token_endpoint");

		options = new Http.Options();

		options.setBody(
			StringBundler.concat(
				"client_id=", URLCodec.encodeURL(clientId), "&code=",
				URLCodec.encodeURL(code), "&code_verifier=",
				URLCodec.encodeURL(codeVerifier),
				"&grant_type=authorization_code&redirect_uri=",
				URLCodec.encodeURL(redirectURI), "&resource=",
				URLCodec.encodeURL(
					protectedResourceMetadataJSONObject.getString("resource"))),
			ContentTypes.APPLICATION_X_WWW_FORM_URLENCODED, StringPool.UTF8);
		options.setFollowRedirects(false);
		options.setLocation(tokenEndpoint);
		options.setPost(true);

		return JSONFactoryUtil.createJSONObject(
			_http.URLtoString(options)
		).getString(
			"access_token"
		);
	}

	private McpSyncClient _getMcpSyncClient(
		String authorization, String profileName) {

		return McpClient.sync(
			HttpClientStreamableHttpTransport.builder(
				"http://localhost:" + PortalUtil.getPortalServerPort(false) +
					"/o/"
			).customizeRequest(
				builder -> builder.header("Authorization", authorization)
			).endpoint(
				(profileName != null) ? "mcp/" + profileName : "mcp"
			).build()
		).capabilities(
			McpSchema.ClientCapabilities.builder(
			).elicitation(
				true, true
			).build()
		).build();
	}

	private String _getMCPURL() {
		return "http://localhost:" + PortalUtil.getPortalServerPort(false) +
			"/o/mcp";
	}

	private String _getQueryString(String url) {
		URI uri = URI.create(url);

		return uri.getRawQuery();
	}

	private Http.Response _getResponse(String authorization) throws Exception {
		Http.Options options = new Http.Options();

		if (authorization != null) {
			options.addHeader("Authorization", authorization);
		}

		options.setLocation(_getMCPURL());

		_http.URLtoString(options);

		return options.getResponse();
	}

	private void _testServiceWithDataMasks(String authorization)
		throws Exception {

		String profileName = RandomTestUtil.randomString();

		ObjectEntry mcpServerProfileObjectEntry =
			MCPServerTestUtil.addMCPServerProfileObjectEntry(
				_TEST_EMAIL_ADDRESS, profileName,
				"mcp-server-profiles getMCPServerProfilesPage");

		McpSyncClient mcpSyncClient = _getMcpSyncClient(
			authorization, profileName);

		mcpSyncClient.initialize();

		McpSchema.CallToolResult callToolResult = mcpSyncClient.callTool(
			new McpSchema.CallToolRequest(
				"getMCPServerProfilesPage", Collections.emptyMap()));

		List<McpSchema.Content> content = callToolResult.content();

		McpSchema.TextContent textContent = (McpSchema.TextContent)content.get(
			0);

		Assert.assertThat(
			textContent.text(),
			CoreMatchers.allOf(
				CoreMatchers.containsString("[EMAIL_ADDRESS]"),
				CoreMatchers.not(
					CoreMatchers.containsString(_TEST_EMAIL_ADDRESS))));

		ObjectEntry dataMaskObjectEntry =
			MCPServerTestUtil.fetchDataMaskObjectEntry("Email Address");

		MCPServerTestUtil.deleteMCPServerProfileDataMaskObjectEntry(
			"Removed by test.",
			_fetchMCPServerProfileDataMaskObjectEntry(
				dataMaskObjectEntry.getObjectEntryId(),
				mcpServerProfileObjectEntry.getExternalReferenceCode()));

		callToolResult = mcpSyncClient.callTool(
			new McpSchema.CallToolRequest(
				"getMCPServerProfilesPage", Collections.emptyMap()));

		content = callToolResult.content();

		textContent = (McpSchema.TextContent)content.get(0);

		Assert.assertThat(
			textContent.text(),
			CoreMatchers.allOf(
				CoreMatchers.containsString(_TEST_EMAIL_ADDRESS),
				CoreMatchers.not(
					CoreMatchers.containsString("[EMAIL_ADDRESS]"))));

		mcpSyncClient.closeGracefully();
	}

	private void _testServiceWithModifiedProfile(String authorization)
		throws Exception {

		String name = RandomTestUtil.randomString();

		ObjectEntry objectEntry = _addObjectEntry(
			name, "mcp-server-profiles getMCPServerProfilesPage",
			"mcp-server-profiles postMCPServerProfile");

		McpSyncClient mcpSyncClient = _getMcpSyncClient(authorization, name);

		mcpSyncClient.initialize();

		McpSchema.ListToolsResult listToolsResult = mcpSyncClient.listTools();

		List<McpSchema.Tool> tools = listToolsResult.tools();

		Assert.assertEquals(tools.toString(), 2, tools.size());

		_assertTool(
			tools.get(0), "getMCPServerProfilesPage",
			"get_mcp_server_profiles.json");
		_assertTool(
			tools.get(1), "postMCPServerProfile",
			"post_mcp_server_profiles.json");

		mcpSyncClient.closeGracefully();

		_objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			HashMapBuilder.<String, Serializable>put(
				"description", RandomTestUtil.randomString()
			).put(
				"name", name
			).put(
				"tools", "mcp-server-profiles getMCPServerProfilesPage"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		mcpSyncClient = _getMcpSyncClient(authorization, name);

		mcpSyncClient.initialize();

		listToolsResult = mcpSyncClient.listTools();

		tools = listToolsResult.tools();

		Assert.assertEquals(tools.toString(), 1, tools.size());

		_assertTool(
			tools.get(0), "getMCPServerProfilesPage",
			"get_mcp_server_profiles.json");

		mcpSyncClient.closeGracefully();
	}

	private void _testServiceWithNoContentResponse(String authorization)
		throws Exception {

		McpSyncClient mcpSyncClient = _getMcpSyncClient(authorization, null);

		mcpSyncClient.initialize();

		McpSchema.CallToolResult callToolResult = mcpSyncClient.callTool(
			new McpSchema.CallToolRequest(
				"postToolSetToolSetNameToolInvoke",
				HashMapBuilder.<String, Object>put(
					"body",
					HashMapBuilder.<String, Object>put(
						"mCPServerProfileId",
						String.valueOf(
							_addObjectEntry(
								RandomTestUtil.randomString(),
								"mcp-server-profiles getMCPServerProfilesPage"
							).getObjectEntryId())
					).build()
				).put(
					"toolName", "deleteMCPServerProfile"
				).put(
					"toolSetName", "mcp-server-profiles"
				).build()));

		List<McpSchema.Content> contents = callToolResult.content();

		McpSchema.TextContent textContent = (McpSchema.TextContent)contents.get(
			0);

		Assert.assertFalse(textContent.text(), callToolResult.isError());
		Assert.assertEquals("Status code: 204", textContent.text());

		mcpSyncClient.closeGracefully();
	}

	private void _testServiceWithoutProfile(String authorization)
		throws Exception {

		McpSyncClient mcpSyncClient = _getMcpSyncClient(authorization, null);

		mcpSyncClient.initialize();

		McpSchema.ListToolsResult listToolsResult = mcpSyncClient.listTools();

		List<McpSchema.Tool> tools = listToolsResult.tools();

		Assert.assertEquals(tools.toString(), 4, tools.size());

		_assertTool(tools.get(0), "getToolSetsPage", "get_tool_sets.json");
		_assertTool(
			tools.get(1), "getToolSetToolSetNameToolSummariesPage",
			"get_tool_summaries.json");
		_assertTool(tools.get(2), "getToolSetToolSetNameTool", "get_tool.json");
		_assertTool(
			tools.get(3), "postToolSetToolSetNameToolInvoke",
			"invoke_tool.json");

		McpSchema.CallToolResult callToolResult = mcpSyncClient.callTool(
			new McpSchema.CallToolRequest(
				"getToolSetsPage", Collections.emptyMap()));

		List<McpSchema.Content> contents = callToolResult.content();

		McpSchema.TextContent textContent = (McpSchema.TextContent)contents.get(
			0);

		Assert.assertFalse(textContent.text(), callToolResult.isError());

		Assert.assertTrue(
			textContent.text(),
			JSONUtil.toStringList(
				JSONFactoryUtil.createJSONObject(
					textContent.text()
				).getJSONArray(
					"items"
				),
				"name"
			).contains(
				"mcp-server-profiles"
			));

		callToolResult = mcpSyncClient.callTool(
			new McpSchema.CallToolRequest(
				"getToolSetToolSetNameToolSummariesPage",
				HashMapBuilder.<String, Object>put(
					"toolSetName", "mcp-server-profiles"
				).build()));

		contents = callToolResult.content();

		textContent = (McpSchema.TextContent)contents.get(0);

		Assert.assertFalse(textContent.text(), callToolResult.isError());

		Assert.assertTrue(
			textContent.text(),
			JSONUtil.toStringList(
				JSONFactoryUtil.createJSONObject(
					textContent.text()
				).getJSONArray(
					"items"
				),
				"name"
			).contains(
				"getMCPServerProfilesPage"
			));

		callToolResult = mcpSyncClient.callTool(
			new McpSchema.CallToolRequest(
				"getToolSetToolSetNameTool",
				HashMapBuilder.<String, Object>put(
					"toolName", "getMCPServerProfilesPage"
				).put(
					"toolSetName", "mcp-server-profiles"
				).build()));

		contents = callToolResult.content();

		textContent = (McpSchema.TextContent)contents.get(0);

		Assert.assertFalse(textContent.text(), callToolResult.isError());

		JSONObject toolJSONObject = JSONFactoryUtil.createJSONObject(
			textContent.text());

		Assert.assertEquals(
			"getMCPServerProfilesPage", toolJSONObject.getString("name"));
		Assert.assertNotNull(toolJSONObject.getJSONObject("inputSchema"));

		callToolResult = mcpSyncClient.callTool(
			new McpSchema.CallToolRequest(
				"postToolSetToolSetNameToolInvoke",
				HashMapBuilder.<String, Object>put(
					"body", Collections.<String, Object>emptyMap()
				).put(
					"toolName", "getMCPServerProfilesPage"
				).put(
					"toolSetName", "mcp-server-profiles"
				).build()));

		contents = callToolResult.content();

		textContent = (McpSchema.TextContent)contents.get(0);

		Assert.assertFalse(textContent.text(), callToolResult.isError());

		Assert.assertNotNull(
			JSONFactoryUtil.createJSONObject(
				textContent.text()
			).getJSONArray(
				"items"
			));

		String entryName = RandomTestUtil.randomString();

		callToolResult = mcpSyncClient.callTool(
			new McpSchema.CallToolRequest(
				"postToolSetToolSetNameToolInvoke",
				HashMapBuilder.<String, Object>put(
					"body",
					HashMapBuilder.<String, Object>put(
						"body",
						HashMapBuilder.<String, Object>put(
							"description", RandomTestUtil.randomString()
						).put(
							"name", entryName
						).put(
							"tools",
							"mcp-server-profiles getMCPServerProfilesPage"
						).build()
					).build()
				).put(
					"toolName", "postMCPServerProfile"
				).put(
					"toolSetName", "mcp-server-profiles"
				).build()));

		contents = callToolResult.content();

		textContent = (McpSchema.TextContent)contents.get(0);

		Assert.assertFalse(textContent.text(), callToolResult.isError());

		Assert.assertEquals(
			entryName,
			JSONFactoryUtil.createJSONObject(
				textContent.text()
			).getString(
				"name"
			));

		mcpSyncClient.closeGracefully();
	}

	private void _testServiceWithoutSession(String authorization)
		throws Exception {

		String url =
			"http://localhost:" + PortalUtil.getPortalServerPort(false) +
				"/o/mcp";

		Http.Options options = new Http.Options();

		options.addHeader("Authorization", authorization);
		options.setLocation(url);

		_http.URLtoString(options);

		Http.Response response = options.getResponse();

		Assert.assertEquals("text/event-stream", response.getContentType());
		Assert.assertEquals(200, response.getResponseCode());

		options = new Http.Options();

		options.addHeader("Accept", "application/json, text/event-stream");
		options.addHeader("Authorization", authorization);
		options.addHeader("Content-Type", ContentTypes.APPLICATION_JSON);
		options.setBody(
			JSONUtil.put(
				"id", 1
			).put(
				"jsonrpc", "2.0"
			).put(
				"method", "initialize"
			).put(
				"params",
				JSONUtil.put(
					"capabilities", JSONFactoryUtil.createJSONObject()
				).put(
					"clientInfo",
					JSONUtil.put(
						"name", "test-client"
					).put(
						"version", "1.0.0"
					)
				).put(
					"protocolVersion", "2025-11-25"
				)
			).toString(),
			ContentTypes.APPLICATION_JSON, StringPool.UTF8);
		options.setLocation(url);
		options.setPost(true);

		_http.URLtoString(options);

		response = options.getResponse();

		Assert.assertNull(response.getHeader("Mcp-Session-Id"));
		Assert.assertEquals(200, response.getResponseCode());
	}

	private void _testServiceWithProfile(String authorization)
		throws Exception {

		String name = RandomTestUtil.randomString();

		_addObjectEntry(
			name, "mcp-server-profiles getMCPServerProfilesPage",
			"mcp-server-profiles postMCPServerProfile");

		McpSyncClient mcpSyncClient = _getMcpSyncClient(authorization, name);

		mcpSyncClient.initialize();

		McpSchema.ListToolsResult listToolsResult = mcpSyncClient.listTools();

		List<McpSchema.Tool> tools = listToolsResult.tools();

		Assert.assertEquals(tools.toString(), 2, tools.size());

		_assertTool(
			tools.get(0), "getMCPServerProfilesPage",
			"get_mcp_server_profiles.json");
		_assertTool(
			tools.get(1), "postMCPServerProfile",
			"post_mcp_server_profiles.json");

		String entryName = RandomTestUtil.randomString();

		McpSchema.CallToolResult callToolResult = mcpSyncClient.callTool(
			new McpSchema.CallToolRequest(
				"postMCPServerProfile",
				HashMapBuilder.<String, Object>put(
					"body",
					HashMapBuilder.<String, Object>put(
						"description", RandomTestUtil.randomString()
					).put(
						"name", entryName
					).put(
						"tools", "mcp-server-profiles getMCPServerProfilesPage"
					).build()
				).build()));

		List<McpSchema.Content> contents = callToolResult.content();

		McpSchema.TextContent textContent = (McpSchema.TextContent)contents.get(
			0);

		Assert.assertFalse(textContent.text(), callToolResult.isError());

		Assert.assertEquals(
			entryName,
			JSONFactoryUtil.createJSONObject(
				textContent.text()
			).getString(
				"name"
			));

		callToolResult = mcpSyncClient.callTool(
			new McpSchema.CallToolRequest(
				"getMCPServerProfilesPage", Collections.emptyMap()));

		contents = callToolResult.content();

		textContent = (McpSchema.TextContent)contents.get(0);

		Assert.assertFalse(textContent.text(), callToolResult.isError());

		Assert.assertTrue(
			JSONUtil.toStringList(
				JSONFactoryUtil.createJSONObject(
					textContent.text()
				).getJSONArray(
					"items"
				),
				"name"
			).contains(
				entryName
			));

		mcpSyncClient.closeGracefully();
	}

	private static final String _TEST_EMAIL_ADDRESS = "example@example.com";

	private static final Pattern _authTokenPattern = Pattern.compile(
		"authToken:\\s*(['\"])(((?!\\1).)*)\\1,");
	private static final Pattern _resourceMetadataPattern = Pattern.compile(
		"resource_metadata=\"([^\"]+)\"");

	@Inject
	private Http _http;

	@Inject
	private OAuth2AuthorizationLocalService _oAuth2AuthorizationLocalService;

	@Inject
	private OAuthClientASLocalMetadataLocalService
		_oAuthClientASLocalMetadataLocalService;

	@Inject
	private OAuthClientPRLocalMetadataLocalService
		_oAuthClientPRLocalMetadataLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}