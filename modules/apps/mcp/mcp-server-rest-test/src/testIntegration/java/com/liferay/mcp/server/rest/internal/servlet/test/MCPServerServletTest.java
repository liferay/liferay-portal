/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.servlet.test;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.test.util.BatchEngineTestUtil;
import com.liferay.mcp.server.rest.test.util.MCPServerDataMaskTestUtil;
import com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata;
import com.liferay.oauth.client.persistence.service.OAuthClientASLocalMetadataLocalService;
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
	public void setUp() throws Exception {
		BatchEngineTestUtil.processBatchEngineUnits(
			"com.liferay.mcp.server.rest.impl", MCPServerServletTest.class,
			new String[] {
				".com.liferay.mcp.server.rest.internal.batch.01.object." +
					"definition",
				".com.liferay.mcp.server.rest.internal.batch.02.object." +
					"definition",
				".com.liferay.mcp.server.rest.internal.batch.04.object.entry"
			});
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

				_testServiceWithModifiedProfile(authorization);
				_testServiceWithNoContentResponse(authorization);
				_testServiceWithoutProfile(authorization);
				_testServiceWithoutSession(authorization);
				_testServiceWithProfile(authorization);
			}
		}
	}

	@Test
	public void testServiceWithDataMasks() throws Exception {
		String dataMaskingPrefix =
			".com.liferay.headless.data.mask.internal.batch.";

		BatchEngineTestUtil.processBatchEngineUnits(
			"com.liferay.headless.data.mask.impl", MCPServerServletTest.class,
			new String[] {
				dataMaskingPrefix + "01.list.type.definition",
				dataMaskingPrefix + "02.object.definition",
				dataMaskingPrefix + "03.object.entry"
			});

		BatchEngineTestUtil.processBatchEngineUnits(
			"com.liferay.mcp.server.rest.impl", MCPServerServletTest.class,
			new String[] {
				".com.liferay.mcp.server.rest.internal.batch.03.object." +
					"definition"
			});

		MCPServerDataMaskTestUtil.updateMCPServerConfiguration(true);

		try {

			// The email address is redacted in the profile response

			String profileName = RandomTestUtil.randomString();

			ObjectEntry profileObjectEntry =
				MCPServerDataMaskTestUtil.addProfile(
					profileName, "Contact: " + _SAMPLE_EMAIL,
					"mcp-server-profiles getMCPServerProfilesPage");

			ObjectEntry emailMaskObjectEntry = _findSystemMask("Email Address");

			MCPServerDataMaskTestUtil.addProfileDataMask(
				profileObjectEntry.getExternalReferenceCode(),
				emailMaskObjectEntry.getObjectEntryId(), 1);

			String responseText = _callListProfilesTool(profileName);

			Assert.assertThat(
				responseText, CoreMatchers.containsString("[EMAIL_ADDRESS]"));
			Assert.assertThat(
				responseText,
				CoreMatchers.not(CoreMatchers.containsString(_SAMPLE_EMAIL)));

			// The IPv4 address is redacted once in the profile response

			profileName = RandomTestUtil.randomString();

			MCPServerDataMaskTestUtil.addProfile(
				profileName, "Server at 192.168.1.42",
				"mcp-server-profiles getMCPServerProfilesPage");

			responseText = _callListProfilesTool(profileName);

			Assert.assertThat(
				responseText, CoreMatchers.containsString("192.168.1.0/24"));
			Assert.assertThat(
				responseText,
				CoreMatchers.not(
					CoreMatchers.containsString("192.168.1.0/24/24")));
			Assert.assertThat(
				responseText,
				CoreMatchers.not(CoreMatchers.containsString("192.168.1.42")));

			// The error response is redacted

			profileName = RandomTestUtil.randomString();

			profileObjectEntry = MCPServerDataMaskTestUtil.addProfile(
				profileName, RandomTestUtil.randomString(),
				"mcp-server-profiles getMCPServerProfilesPage");

			emailMaskObjectEntry = _findSystemMask("Email Address");

			MCPServerDataMaskTestUtil.addProfileDataMask(
				profileObjectEntry.getExternalReferenceCode(),
				emailMaskObjectEntry.getObjectEntryId(), 1);

			McpSchema.CallToolResult callToolResult = _callTool(
				profileName, "getMCPServerProfilesPage",
				HashMapBuilder.<String, Object>put(
					"filter", _SAMPLE_EMAIL
				).build());

			McpSchema.TextContent textContent =
				(McpSchema.TextContent)callToolResult.content(
				).get(
					0
				);

			responseText = textContent.text();

			Assert.assertTrue(responseText, callToolResult.isError());
			Assert.assertThat(
				responseText, CoreMatchers.containsString("[EMAIL_ADDRESS]"));
			Assert.assertThat(
				responseText,
				CoreMatchers.not(CoreMatchers.containsString(_SAMPLE_EMAIL)));

			// The email address is not redacted when the association is removed

			profileName = RandomTestUtil.randomString();

			profileObjectEntry = MCPServerDataMaskTestUtil.addProfile(
				profileName, "Contact: " + _SAMPLE_EMAIL,
				"mcp-server-profiles getMCPServerProfilesPage");

			emailMaskObjectEntry = _findSystemMask("Email Address");

			ObjectEntry emailProfileDataMaskObjectEntry = _findProfileDataMask(
				profileObjectEntry.getExternalReferenceCode(),
				emailMaskObjectEntry.getObjectEntryId());

			MCPServerDataMaskTestUtil.removeProfileDataMask(
				emailProfileDataMaskObjectEntry, "Removed by test.");

			responseText = _callListProfilesTool(profileName);

			Assert.assertThat(
				responseText, CoreMatchers.containsString(_SAMPLE_EMAIL));
			Assert.assertThat(
				responseText,
				CoreMatchers.not(
					CoreMatchers.containsString("[EMAIL_ADDRESS]")));

			// The REST invocation applies masks when the data masks header is
			// set

			MCPServerDataMaskTestUtil.addProfile(
				RandomTestUtil.randomString(), "Contact: " + _SAMPLE_EMAIL,
				"mcp-server-profiles getMCPServerProfilesPage");

			responseText = _invokeListProfilesViaRest(
				HashMapBuilder.put(
					"X-Liferay-Data-Masks", "L_DATA_MASK_EMAIL_ADDRESS"
				).build());

			Assert.assertThat(
				responseText, CoreMatchers.containsString("[EMAIL_ADDRESS]"));
			Assert.assertThat(
				responseText,
				CoreMatchers.not(CoreMatchers.containsString(_SAMPLE_EMAIL)));

			// The REST invocation respects the selected masks only

			MCPServerDataMaskTestUtil.addProfile(
				RandomTestUtil.randomString(),
				StringBundler.concat(
					"Contact: ", _SAMPLE_EMAIL, " ", _SAMPLE_PHONE),
				"mcp-server-profiles getMCPServerProfilesPage");

			responseText = _invokeListProfilesViaRest(
				HashMapBuilder.put(
					"X-Liferay-Data-Masks", "L_DATA_MASK_EMAIL_ADDRESS"
				).build());

			Assert.assertThat(
				responseText, CoreMatchers.containsString("[EMAIL_ADDRESS]"));
			Assert.assertThat(
				responseText,
				CoreMatchers.not(CoreMatchers.containsString(_SAMPLE_EMAIL)));
			Assert.assertThat(
				responseText, CoreMatchers.containsString(_SAMPLE_PHONE));

			// The REST invocation skips redaction when the data masks header is
			// unknown

			MCPServerDataMaskTestUtil.addProfile(
				RandomTestUtil.randomString(), "Contact: " + _SAMPLE_EMAIL,
				"mcp-server-profiles getMCPServerProfilesPage");

			responseText = _invokeListProfilesViaRest(
				HashMapBuilder.put(
					"X-Liferay-Data-Masks", "L_UNKNOWN_DATA_MASK_ERC"
				).build());

			Assert.assertThat(
				responseText, CoreMatchers.containsString(_SAMPLE_EMAIL));
			Assert.assertThat(
				responseText,
				CoreMatchers.not(
					CoreMatchers.containsString("[EMAIL_ADDRESS]")));

			// The REST invocation skips redaction when there is no data masks
			// header

			MCPServerDataMaskTestUtil.addProfile(
				RandomTestUtil.randomString(), "Contact: " + _SAMPLE_EMAIL,
				"mcp-server-profiles getMCPServerProfilesPage");

			responseText = _invokeListProfilesViaRest(Collections.emptyMap());

			Assert.assertThat(
				responseText, CoreMatchers.containsString(_SAMPLE_EMAIL));
			Assert.assertThat(
				responseText,
				CoreMatchers.not(
					CoreMatchers.containsString("[EMAIL_ADDRESS]")));
		}
		finally {
			MCPServerDataMaskTestUtil.updateMCPServerConfiguration(false);
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

	private String _callListProfilesTool(String profileName) throws Exception {
		McpSyncClient mcpSyncClient = _getMcpSyncClient(
			_getAuthorization(), profileName);

		try {
			mcpSyncClient.initialize();

			McpSchema.CallToolResult callToolResult = mcpSyncClient.callTool(
				new McpSchema.CallToolRequest(
					"getMCPServerProfilesPage", Collections.emptyMap()));

			List<McpSchema.Content> contents = callToolResult.content();

			McpSchema.TextContent content = (McpSchema.TextContent)contents.get(
				0);

			return content.text();
		}
		finally {
			mcpSyncClient.closeGracefully();
		}
	}

	private McpSchema.CallToolResult _callTool(
			String profileName, String toolName, Map<String, Object> arguments)
		throws Exception {

		McpSyncClient mcpSyncClient = _getMcpSyncClient(
			_getAuthorization(), profileName);

		try {
			mcpSyncClient.initialize();

			return mcpSyncClient.callTool(
				new McpSchema.CallToolRequest(toolName, arguments));
		}
		finally {
			mcpSyncClient.closeGracefully();
		}
	}

	private ObjectEntry _findProfileDataMask(
			String mcpServerProfileExternalReferenceCode,
			long maskObjectEntryId)
		throws Exception {

		ObjectDefinition profileDataMaskObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_MCP_SERVER_PROFILE_DATA_MASK",
					TestPropsValues.getCompanyId());

		for (ObjectEntry profileDataMaskObjectEntry :
				_objectEntryLocalService.getObjectEntries(
					0, profileDataMaskObjectDefinition.getObjectDefinitionId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

			Map<String, Serializable> values =
				profileDataMaskObjectEntry.getValues();

			String profileDataMaskERC = (String)values.get(
				"dataMaskExternalReferenceCode");

			if (Objects.equals(
					mcpServerProfileExternalReferenceCode,
					values.get("mcpServerProfileExternalReferenceCode")) &&
				Objects.equals(
					profileDataMaskERC,
					_objectEntryLocalService.fetchObjectEntry(
						maskObjectEntryId
					).getExternalReferenceCode())) {

				return profileDataMaskObjectEntry;
			}
		}

		return null;
	}

	private ObjectEntry _findSystemMask(String name) throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DATA_MASK", TestPropsValues.getCompanyId());

		if (objectDefinition == null) {
			return null;
		}

		for (ObjectEntry objectEntry :
				_objectEntryLocalService.getObjectEntries(
					0, objectDefinition.getObjectDefinitionId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

			Map<String, Serializable> values = objectEntry.getValues();

			if (name.equals(values.get("name"))) {
				return objectEntry;
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
			JSONFactoryUtil.createJSONObject(_get(matcher.group(1)));

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
							"configuration.DynamicRegistrationConfiguration",
						HashMapDictionaryBuilder.<String, Object>put(
							"dynamic.registration.anonymous.allowed.grant." +
								"types",
							new String[] {"*"}
						).put(
							"dynamic.registration.anonymous.allowed.hosts",
							new String[] {"*"}
						).put(
							"dynamic.registration.anonymous.allowed.redirect." +
								"uri.patterns",
							new String[] {"*"}
						).put(
							"dynamic.registration.anonymous.allowed.scopes",
							new String[] {"*"}
						).put(
							"dynamic.registration.anonymous.registrations." +
								"per.hour",
							0
						).put(
							"dynamic.registration.require.initial.access.token",
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

	private String _getAuthorization() {
		String userNameAndPassword =
			"test@liferay.com:" + PropsValues.DEFAULT_ADMIN_PASSWORD;

		return "Basic " + Base64.encode(userNameAndPassword.getBytes());
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

	private String _invokeListProfilesViaRest(Map<String, String> headers)
		throws Exception {

		String url = StringBundler.concat(
			"http://localhost:", PortalUtil.getPortalServerPort(false),
			"/o/mcp-server/v1.0/tool-sets/mcp-server-profiles/tools",
			"/getMCPServerProfilesPage/invoke");

		Http.Options options = new Http.Options();

		options.addHeader("Authorization", _getAuthorization());
		options.addHeader("Content-Type", "application/json");

		for (Map.Entry<String, String> entry : headers.entrySet()) {
			options.addHeader(entry.getKey(), entry.getValue());
		}

		options.setBody("{}", "application/json", "UTF-8");
		options.setLocation(url);
		options.setMethod(Http.Method.POST);

		return _http.URLtoString(options);
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

	private static final String _SAMPLE_EMAIL = "contact@example.com";

	private static final String _SAMPLE_PHONE = "+1-202-555-0199";

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
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}