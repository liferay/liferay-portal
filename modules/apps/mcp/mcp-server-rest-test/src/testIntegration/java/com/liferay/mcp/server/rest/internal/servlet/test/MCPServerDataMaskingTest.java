/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.servlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.test.util.BatchEngineTestUtil;
import com.liferay.mcp.server.rest.test.util.MCPServerDataMaskTestUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpSchema;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.hamcrest.CoreMatchers;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jose Luis Navarro
 */
@FeatureFlags(featureFlags = @FeatureFlag("LPD-63311"))
@RunWith(Arquillian.class)
public class MCPServerDataMaskingTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		MCPServerDataMaskTestUtil.updateMCPServerConfiguration(true);

		String dataMaskingPrefix =
			".com.liferay.headless.data.mask.internal.batch.";

		BatchEngineTestUtil.processBatchEngineUnits(
			"com.liferay.headless.data.mask.impl",
			MCPServerDataMaskingTest.class,
			new String[] {
				dataMaskingPrefix + "01.list.type.definition",
				dataMaskingPrefix + "02.object.definition",
				dataMaskingPrefix + "03.object.entry"
			});

		String prefix = ".com.liferay.mcp.server.rest.internal.batch.";

		BatchEngineTestUtil.processBatchEngineUnits(
			"com.liferay.mcp.server.rest.impl", MCPServerDataMaskingTest.class,
			new String[] {
				prefix + "01.object.definition",
				prefix + "02.object.definition", prefix + "03.object.definition"
			});
	}

	@After
	public void tearDown() throws Exception {
		MCPServerDataMaskTestUtil.updateMCPServerConfiguration(false);
	}

	@Test
	public void testCustomMaskCanBeDeletedWhileAttachedToProfile()
		throws Exception {

		ObjectEntry profileObjectEntry = MCPServerDataMaskTestUtil.addProfile(
			RandomTestUtil.randomString(), "no PII here",
			"mcp-server-profiles getMCPServerProfilesPage");

		ObjectEntry customMaskObjectEntry =
			MCPServerDataMaskTestUtil.addCustomMask(
				RandomTestUtil.randomString(), "\\d{4}", "[REDACTED]");

		ObjectEntry profileDataMaskObjectEntry =
			MCPServerDataMaskTestUtil.addProfileDataMask(
				profileObjectEntry.getExternalReferenceCode(),
				customMaskObjectEntry.getObjectEntryId(), 1);

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));

			_objectEntryLocalService.deleteObjectEntry(
				customMaskObjectEntry.getObjectEntryId());
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}

		Assert.assertNull(
			_objectEntryLocalService.fetchObjectEntry(
				customMaskObjectEntry.getObjectEntryId()));

		Assert.assertNull(
			_objectEntryLocalService.fetchObjectEntry(
				profileDataMaskObjectEntry.getObjectEntryId()));
	}

	@Test
	public void testEmailIsNotRedactedWhenAssociationIsRemoved()
		throws Exception {

		String profileName = RandomTestUtil.randomString();

		ObjectEntry profileObjectEntry = MCPServerDataMaskTestUtil.addProfile(
			profileName, "Contact: " + _SAMPLE_EMAIL,
			"mcp-server-profiles getMCPServerProfilesPage");

		ObjectEntry emailMaskObjectEntry = _findSystemMask("Email Address");

		ObjectEntry emailProfileDataMaskObjectEntry = _findProfileDataMask(
			profileObjectEntry.getExternalReferenceCode(),
			emailMaskObjectEntry.getObjectEntryId());

		MCPServerDataMaskTestUtil.removeProfileDataMask(
			emailProfileDataMaskObjectEntry, "Removed by test.");

		String responseText = _callListProfilesTool(profileName);

		Assert.assertThat(
			responseText, CoreMatchers.containsString(_SAMPLE_EMAIL));
		Assert.assertThat(
			responseText,
			CoreMatchers.not(CoreMatchers.containsString("[EMAIL_ADDRESS]")));
	}

	@Test
	public void testEmailIsRedactedInProfileResponse() throws Exception {
		String profileName = RandomTestUtil.randomString();

		ObjectEntry profileObjectEntry = MCPServerDataMaskTestUtil.addProfile(
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
	}

	@Test
	public void testErrorResponseIsRedacted() throws Exception {
		String profileName = RandomTestUtil.randomString();

		ObjectEntry profileObjectEntry = MCPServerDataMaskTestUtil.addProfile(
			profileName, RandomTestUtil.randomString(),
			"mcp-server-profiles getMCPServerProfilesPage");

		ObjectEntry emailMaskObjectEntry = _findSystemMask("Email Address");

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

		String responseText = textContent.text();

		Assert.assertTrue(responseText, callToolResult.isError());
		Assert.assertThat(
			responseText, CoreMatchers.containsString("[EMAIL_ADDRESS]"));
		Assert.assertThat(
			responseText,
			CoreMatchers.not(CoreMatchers.containsString(_SAMPLE_EMAIL)));
	}

	@Test
	public void testIPv4IsRedactedOnceInProfileResponse() throws Exception {
		String profileName = RandomTestUtil.randomString();

		MCPServerDataMaskTestUtil.addProfile(
			profileName, "Server at 192.168.1.42",
			"mcp-server-profiles getMCPServerProfilesPage");

		String responseText = _callListProfilesTool(profileName);

		Assert.assertThat(
			responseText, CoreMatchers.containsString("192.168.1.0/24"));
		Assert.assertThat(
			responseText,
			CoreMatchers.not(CoreMatchers.containsString("192.168.1.0/24/24")));
		Assert.assertThat(
			responseText,
			CoreMatchers.not(CoreMatchers.containsString("192.168.1.42")));
	}

	@Test
	public void testProfileDataMaskCannotBeRemovedWithoutDeleteReason()
		throws Exception {

		ObjectEntry profileObjectEntry = MCPServerDataMaskTestUtil.addProfile(
			RandomTestUtil.randomString(), "no PII here",
			"mcp-server-profiles getMCPServerProfilesPage");

		ObjectEntry emailMaskObjectEntry = _findSystemMask("Email Address");

		ObjectEntry profileDataMaskObjectEntry =
			MCPServerDataMaskTestUtil.addProfileDataMask(
				profileObjectEntry.getExternalReferenceCode(),
				emailMaskObjectEntry.getObjectEntryId(), 1);

		try {
			_objectEntryLocalService.deleteObjectEntry(
				profileDataMaskObjectEntry.getObjectEntryId());

			Assert.fail(
				"Removing a profile data mask without a delete reason should " +
					"have thrown");
		}
		catch (Exception exception) {
			Assert.assertThat(
				exception.getMessage(),
				CoreMatchers.containsString("delete reason"));
		}

		Assert.assertNotNull(
			_objectEntryLocalService.fetchObjectEntry(
				profileDataMaskObjectEntry.getObjectEntryId()));

		MCPServerDataMaskTestUtil.removeProfileDataMask(
			profileDataMaskObjectEntry, RandomTestUtil.randomString());

		Assert.assertNull(
			_objectEntryLocalService.fetchObjectEntry(
				profileDataMaskObjectEntry.getObjectEntryId()));
	}

	@Test
	public void testProfileDataMasksAreDeletedOnProfileRemove()
		throws Exception {

		String profileName = RandomTestUtil.randomString();

		ObjectEntry profileObjectEntry = MCPServerDataMaskTestUtil.addProfile(
			profileName, "Contact: " + _SAMPLE_EMAIL,
			"mcp-server-profiles getMCPServerProfilesPage");

		String mcpServerProfileExternalReferenceCode =
			profileObjectEntry.getExternalReferenceCode();

		Assert.assertEquals(
			_SYSTEM_MASK_COUNT,
			_countProfileDataMasks(mcpServerProfileExternalReferenceCode));

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));

			_objectEntryLocalService.deleteObjectEntry(profileObjectEntry);
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}

		Assert.assertEquals(
			0, _countProfileDataMasks(mcpServerProfileExternalReferenceCode));
	}

	@Test
	public void testRestApiInvokeAppliesMasksWhenDataMasksHeaderSet()
		throws Exception {

		MCPServerDataMaskTestUtil.addProfile(
			RandomTestUtil.randomString(), "Contact: " + _SAMPLE_EMAIL,
			"mcp-server-profiles getMCPServerProfilesPage");

		String responseText = _invokeListProfilesViaRest(
			HashMapBuilder.put(
				"X-Liferay-Data-Masks", "L_DATA_MASK_EMAIL_ADDRESS"
			).build());

		Assert.assertThat(
			responseText, CoreMatchers.containsString("[EMAIL_ADDRESS]"));
		Assert.assertThat(
			responseText,
			CoreMatchers.not(CoreMatchers.containsString(_SAMPLE_EMAIL)));
	}

	@Test
	public void testRestApiInvokeRespectsSelectedMasksOnly() throws Exception {
		MCPServerDataMaskTestUtil.addProfile(
			RandomTestUtil.randomString(),
			StringBundler.concat(
				"Contact: ", _SAMPLE_EMAIL, " ", _SAMPLE_PHONE),
			"mcp-server-profiles getMCPServerProfilesPage");

		String responseText = _invokeListProfilesViaRest(
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
	}

	@Test
	public void testRestApiInvokeSkipsRedactionWhenDataMasksHeaderIsUnknown()
		throws Exception {

		MCPServerDataMaskTestUtil.addProfile(
			RandomTestUtil.randomString(), "Contact: " + _SAMPLE_EMAIL,
			"mcp-server-profiles getMCPServerProfilesPage");

		String responseText = _invokeListProfilesViaRest(
			HashMapBuilder.put(
				"X-Liferay-Data-Masks", "L_UNKNOWN_DATA_MASK_ERC"
			).build());

		Assert.assertThat(
			responseText, CoreMatchers.containsString(_SAMPLE_EMAIL));
		Assert.assertThat(
			responseText,
			CoreMatchers.not(CoreMatchers.containsString("[EMAIL_ADDRESS]")));
	}

	@Test
	public void testRestApiInvokeSkipsRedactionWhenNoDataMasksHeader()
		throws Exception {

		MCPServerDataMaskTestUtil.addProfile(
			RandomTestUtil.randomString(), "Contact: " + _SAMPLE_EMAIL,
			"mcp-server-profiles getMCPServerProfilesPage");

		String responseText = _invokeListProfilesViaRest(
			Collections.emptyMap());

		Assert.assertThat(
			responseText, CoreMatchers.containsString(_SAMPLE_EMAIL));
		Assert.assertThat(
			responseText,
			CoreMatchers.not(CoreMatchers.containsString("[EMAIL_ADDRESS]")));
	}

	@Test
	public void testSystemMasksAreAutoAttachedOnProfileCreate()
		throws Exception {

		ObjectEntry profileObjectEntry = MCPServerDataMaskTestUtil.addProfile(
			RandomTestUtil.randomString(), "no PII here",
			"mcp-server-profiles getMCPServerProfilesPage");

		Assert.assertEquals(
			_SYSTEM_MASK_COUNT,
			_countProfileDataMasks(
				profileObjectEntry.getExternalReferenceCode()));
	}

	@Test
	public void testSystemMasksAreSeededOnDefaultProfile() throws Exception {
		ObjectEntry defaultProfileObjectEntry = _findProfile("default");

		Assert.assertEquals(
			_SYSTEM_MASK_COUNT,
			_countProfileDataMasks(
				defaultProfileObjectEntry.getExternalReferenceCode()));
	}

	private String _callListProfilesTool(String profileName) throws Exception {
		McpSyncClient mcpSyncClient = _getMcpSyncClient(profileName);

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

		McpSyncClient mcpSyncClient = _getMcpSyncClient(profileName);

		try {
			mcpSyncClient.initialize();

			return mcpSyncClient.callTool(
				new McpSchema.CallToolRequest(toolName, arguments));
		}
		finally {
			mcpSyncClient.closeGracefully();
		}
	}

	private int _countProfileDataMasks(
			String mcpServerProfileExternalReferenceCode)
		throws Exception {

		ObjectDefinition profileDataMaskObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_MCP_SERVER_PROFILE_DATA_MASK",
					TestPropsValues.getCompanyId());

		int count = 0;

		for (ObjectEntry profileDataMaskObjectEntry :
				_objectEntryLocalService.getObjectEntries(
					0, profileDataMaskObjectDefinition.getObjectDefinitionId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

			Map<String, Serializable> values =
				profileDataMaskObjectEntry.getValues();

			if (Objects.equals(
					mcpServerProfileExternalReferenceCode,
					values.get("mcpServerProfileExternalReferenceCode"))) {

				count++;
			}
		}

		return count;
	}

	private ObjectEntry _findProfile(String name) throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_MCP_SERVER_PROFILE", TestPropsValues.getCompanyId());

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

	private McpSyncClient _getMcpSyncClient(String profileName) {
		return McpClient.sync(
			HttpClientStreamableHttpTransport.builder(
				"http://localhost:" + PortalUtil.getPortalServerPort(false) +
					"/o/"
			).customizeRequest(
				builder -> builder.header("Authorization", _getAuthorization())
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

	private static final String _SAMPLE_EMAIL = "contact@example.com";

	private static final String _SAMPLE_PHONE = "+1-202-555-0199";

	private static final int _SYSTEM_MASK_COUNT = 9;

	@Inject
	private Http _http;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}