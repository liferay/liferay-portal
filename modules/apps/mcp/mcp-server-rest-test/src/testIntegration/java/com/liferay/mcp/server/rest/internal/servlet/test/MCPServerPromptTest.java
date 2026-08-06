/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.servlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.mcp.server.rest.test.util.MCPServerTestUtil;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.exception.ObjectEntryValuesException;
import com.liferay.object.exception.ObjectValidationRuleEngineException;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpError;
import io.modelcontextprotocol.spec.McpSchema;

import java.io.Serializable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jaime León Rosado
 */
@FeatureFlags(featureFlags = @FeatureFlag("LPD-63311"))
@RunWith(Arquillian.class)
public class MCPServerPromptTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() {
		MCPServerTestUtil.processBatchEngineUnits();
	}

	@Test
	public void testAddMCPServerPromptObjectEntry() throws Exception {
		ObjectEntry objectEntry =
			MCPServerTestUtil.addMCPServerPromptObjectEntry(
				RandomTestUtil.randomString(), _randomIdentifier(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				null);

		objectEntry = _objectEntryLocalService.getObjectEntry(
			objectEntry.getObjectEntryId());

		Map<String, Serializable> values = objectEntry.getValues();

		Assert.assertEquals("inactive", values.get("promptStatus"));
	}

	@Test
	public void testAddMCPServerPromptObjectEntryWithDuplicateIdentifier()
		throws Exception {

		String identifier = _randomIdentifier();
		String name = RandomTestUtil.randomString();

		MCPServerTestUtil.addMCPServerPromptObjectEntry(
			RandomTestUtil.randomString(), identifier, name,
			RandomTestUtil.randomString(), null);

		MCPServerTestUtil.addMCPServerPromptObjectEntry(
			RandomTestUtil.randomString(), "copy-of-" + identifier,
			"Copy of " + name, RandomTestUtil.randomString(), null);

		for (String duplicateIdentifier :
				new String[] {identifier, "copy-of-" + identifier}) {

			try {
				MCPServerTestUtil.addMCPServerPromptObjectEntry(
					RandomTestUtil.randomString(), duplicateIdentifier,
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(), null);

				Assert.fail(duplicateIdentifier);
			}
			catch (ObjectEntryValuesException.UniqueValueConstraintViolation
						objectEntryValuesException) {
			}
		}
	}

	@Test
	public void testAddMCPServerPromptObjectEntryWithInvalidIdentifier()
		throws Exception {

		for (String identifier :
				new String[] {
					"-leading-hyphen", "UPPERCASE", "double--hyphen",
					"trailing-hyphen-", "with space", "with_underscore"
				}) {

			try {
				MCPServerTestUtil.addMCPServerPromptObjectEntry(
					RandomTestUtil.randomString(), identifier,
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(), null);

				Assert.fail(identifier);
			}
			catch (ModelListenerException modelListenerException) {
				Assert.assertTrue(
					modelListenerException.getCause() instanceof
						ObjectValidationRuleEngineException);
			}
		}
	}

	@Test
	public void testAddMCPServerPromptObjectEntryWithoutRequiredFields()
		throws Exception {

		String description = RandomTestUtil.randomString();
		String identifier = _randomIdentifier();
		String name = RandomTestUtil.randomString();
		String prompt = RandomTestUtil.randomString();


		_testAddMCPServerPromptObjectEntryWithoutRequiredField(
			description, identifier, name, null);

		_testAddMCPServerPromptObjectEntryWithoutRequiredField(
			description, identifier, null, prompt);
		_testAddMCPServerPromptObjectEntryWithoutRequiredField(
			description, null, name, prompt);
		_testAddMCPServerPromptObjectEntryWithoutRequiredField(
			null, identifier, name, prompt);
	}

	@Test
	public void testDeleteMCPServerPromptObjectEntry() throws Exception {
		ObjectEntry objectEntry =
			MCPServerTestUtil.addMCPServerPromptObjectEntry(
				RandomTestUtil.randomString(), _randomIdentifier(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				"active");

		_objectEntryLocalService.deleteObjectEntry(
			objectEntry.getObjectEntryId());

		Assert.assertNull(
			_objectEntryLocalService.fetchObjectEntry(
				objectEntry.getObjectEntryId()));
	}

	@Test
	public void testMCPServerPromptObjectEntryPermissions() throws Exception {
		ObjectEntry objectEntry =
			MCPServerTestUtil.addMCPServerPromptObjectEntry(
				RandomTestUtil.randomString(), _randomIdentifier(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				null);

		_user = UserTestUtil.addUser();

		UserTestUtil.setUser(_user);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_MCP_SERVER_PROMPT", TestPropsValues.getCompanyId());

		try {
			_objectEntryService.addObjectEntry(
				0, objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"description", RandomTestUtil.randomString()
				).put(
					"identifier", _randomIdentifier()
				).put(
					"name", RandomTestUtil.randomString()
				).put(
					"prompt", RandomTestUtil.randomString()
				).build(),
				ServiceContextTestUtil.getServiceContext());

			Assert.fail();
		}
		catch (PrincipalException principalException) {
		}

		try {
			_objectEntryService.getObjectEntry(objectEntry.getObjectEntryId());

			Assert.fail();
		}
		catch (PrincipalException principalException) {
		}
	}

	@Test
	public void testPrompts() throws Exception {
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

			String description = RandomTestUtil.randomString();
			String identifier = _randomIdentifier();
			String name = RandomTestUtil.randomString();
			String prompt =
				"Line 1\nLine 2\t\"quoted\" <tag> & {\"key\": \"value\"}";

			MCPServerTestUtil.addMCPServerPromptObjectEntry(
				description, identifier, name, prompt, "active");

			String inactiveIdentifier = _randomIdentifier();

			ObjectEntry inactiveObjectEntry =
				MCPServerTestUtil.addMCPServerPromptObjectEntry(
					RandomTestUtil.randomString(), inactiveIdentifier,
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(), null);

			McpSyncClient mcpSyncClient = _getMcpSyncClient();

			mcpSyncClient.initialize();

			McpSchema.Prompt mcpSchemaPrompt = _getPrompt(
				identifier, mcpSyncClient);

			Assert.assertEquals(description, mcpSchemaPrompt.description());
			Assert.assertEquals(name, mcpSchemaPrompt.title());

			Assert.assertNull(_getPrompt(inactiveIdentifier, mcpSyncClient));

			McpSchema.GetPromptResult getPromptResult = mcpSyncClient.getPrompt(
				new McpSchema.GetPromptRequest(
					identifier, Collections.emptyMap()));

			Assert.assertEquals(description, getPromptResult.description());

			List<McpSchema.PromptMessage> promptMessages =
				getPromptResult.messages();

			Assert.assertEquals(
				promptMessages.toString(), 1, promptMessages.size());

			McpSchema.PromptMessage promptMessage = promptMessages.get(0);

			Assert.assertEquals(McpSchema.Role.USER, promptMessage.role());

			McpSchema.TextContent textContent =
				(McpSchema.TextContent)promptMessage.content();

			Assert.assertEquals(prompt, textContent.text());

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					"io.modelcontextprotocol.spec.McpClientSession",
					LoggerTestUtil.ERROR)) {

				mcpSyncClient.getPrompt(
					new McpSchema.GetPromptRequest(
						inactiveIdentifier, Collections.emptyMap()));

				Assert.fail();
			}
			catch (McpError mcpError) {
			}

			mcpSyncClient.closeGracefully();

			_objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(),
				inactiveObjectEntry.getObjectEntryId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				HashMapBuilder.<String, Serializable>putAll(
					inactiveObjectEntry.getValues()
				).put(
					"promptStatus", "active"
				).build(),
				ServiceContextTestUtil.getServiceContext());

			mcpSyncClient = _getMcpSyncClient();

			mcpSyncClient.initialize();

			Assert.assertNotNull(_getPrompt(inactiveIdentifier, mcpSyncClient));

			mcpSyncClient.closeGracefully();
		}
	}

	@Test
	public void testUpdateMCPServerPromptObjectEntryWithInvalidIdentifier()
		throws Exception {

		ObjectEntry objectEntry =
			MCPServerTestUtil.addMCPServerPromptObjectEntry(
				RandomTestUtil.randomString(), _randomIdentifier(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				null);

		CentralizedThreadLocal.clearShortLivedCentralizedThreadLocals();

		try {
			_objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				HashMapBuilder.<String, Serializable>putAll(
					objectEntry.getValues()
				).put(
					"identifier", "Invalid Identifier"
				).build(),
				ServiceContextTestUtil.getServiceContext());

			Assert.fail();
		}
		catch (ModelListenerException modelListenerException) {
			Assert.assertTrue(
				modelListenerException.getCause() instanceof
					ObjectValidationRuleEngineException);
		}
	}

	private McpSyncClient _getMcpSyncClient() {
		String userNameAndPassword =
			"test@liferay.com:" + PropsValues.DEFAULT_ADMIN_PASSWORD;

		return McpClient.sync(
			HttpClientStreamableHttpTransport.builder(
				"http://localhost:" + PortalUtil.getPortalServerPort(false) +
					"/o/"
			).customizeRequest(
				builder -> builder.header(
					"Authorization",
					"Basic " + Base64.encode(userNameAndPassword.getBytes()))
			).endpoint(
				"mcp"
			).build()
		).build();
	}

	private McpSchema.Prompt _getPrompt(
		String identifier, McpSyncClient mcpSyncClient) {

		McpSchema.ListPromptsResult listPromptsResult =
			mcpSyncClient.listPrompts();

		for (McpSchema.Prompt prompt : listPromptsResult.prompts()) {
			if (Objects.equals(identifier, prompt.name())) {
				return prompt;
			}
		}

		return null;
	}

	private String _randomIdentifier() {
		return StringUtil.toLowerCase(RandomTestUtil.randomString());
	}

	private void _testAddMCPServerPromptObjectEntryWithoutRequiredField(
			String description, String identifier, String name, String prompt)
		throws Exception {

		try {
			MCPServerTestUtil.addMCPServerPromptObjectEntry(
				description, identifier, name, prompt, null);

			Assert.fail();
		}
		catch (ObjectEntryValuesException.Required objectEntryValuesException) {
		}
	}

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectEntryService _objectEntryService;

	@DeleteAfterTestRun
	private User _user;

}