/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.resource.v1_0.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.ai.hub.cell.configuration.AIHubCellConfiguration;
import com.liferay.ai.hub.rest.manager.v1_0.ContentRetrieverManager;
import com.liferay.ai.hub.rest.resource.v1_0.test.util.SseEventSourceTestUtil;
import com.liferay.ai.hub.rest.resource.v1_0.test.util.TokenTestUtil;
import com.liferay.ai.hub.rest.resource.v1_0.util.SseUtil;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.field.builder.LongTextObjectFieldBuilder;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowInstance;
import com.liferay.portal.kernel.workflow.WorkflowInstanceManager;
import com.liferay.portal.kernel.workflow.WorkflowLog;
import com.liferay.portal.kernel.workflow.WorkflowNode;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.test.util.IdempotentRetryAssert;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.workflow.constants.WorkflowDefinitionConstants;
import com.liferay.portal.workflow.kaleo.runtime.util.WorkflowContextUtil;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;
import com.liferay.portal.workflow.manager.WorkflowLogManager;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.initializer.SiteInitializerRegistry;

import java.io.InputStream;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Feliphe Marinho
 */
@FeatureFlags(
	featureFlags = {
		@FeatureFlag(value = "LPD-62272"), @FeatureFlag(value = "LPD-63311")
	}
)
@RunWith(Arquillian.class)
public class AgentInstanceResourceTest
	extends BaseAgentInstanceResourceTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaseAgentInstanceResourceTestCase.setUpClass();

		_accountEntry = _accountEntryLocalService.addAccountEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString() + "@liferay.com", null,
			RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext());

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			_accountEntry.getAccountEntryId(), TestPropsValues.getUserId());

		_classNameLocalService.invalidate();

		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));

		_originalName = PrincipalThreadLocal.getName();

		ConfigurationTestUtil.saveConfiguration(
			AIHubCellConfiguration.class.getName(),
			HashMapDictionaryBuilder.<String, Object>put(
				"clientId", RandomTestUtil.randomString()
			).put(
				"clientSecret", RandomTestUtil.randomString()
			).put(
				"serviceURL",
				"http://localhost:" + PortalUtil.getPortalServerPort(false)
			).build());

		PrincipalThreadLocal.setName(TestPropsValues.getUserId());

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));

		SiteInitializer siteInitializer =
			_siteInitializerRegistry.getSiteInitializer(
				"com.liferay.ai.hub.site.initializer");

		siteInitializer.initialize(TestPropsValues.getGroupId());

		AccountEntry aiHubAccountEntry =
			_accountEntryLocalService.getAccountEntryByExternalReferenceCode(
				"L_AI_HUB", TestPropsValues.getCompanyId());

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			aiHubAccountEntry.getAccountEntryId(), TestPropsValues.getUserId());

		_agentDefinitionObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_AI_HUB_AGENT_DEFINITION",
					TestPropsValues.getCompanyId());
		_contentRetrieverObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_AI_HUB_CONTENT_RETRIEVER",
					TestPropsValues.getCompanyId());
		_instructionObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_AI_HUB_INSTRUCTION_DEFINITION",
					TestPropsValues.getCompanyId());
		_mcpServerObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_AI_HUB_MCP_SERVER", TestPropsValues.getCompanyId());
		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition(
			List.of(
				new LongTextObjectFieldBuilder(
				).labelMap(
					RandomTestUtil.randomLocaleStringMap()
				).name(
					"description"
				).indexed(
					true
				).build(),
				new TextObjectFieldBuilder(
				).labelMap(
					RandomTestUtil.randomLocaleStringMap()
				).name(
					"name"
				).indexed(
					true
				).indexedAsKeyword(
					true
				).build()));

		_objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_mcpServerObjectDefinition.getObjectDefinitionId(), 0,
			LocaleUtil.toLanguageId(LocaleUtil.getDefault()),
			HashMapBuilder.<String, Serializable>put(
				"externalReferenceCode", "L_LIFERAY_AI_HUB_MCP_SERVER"
			).put(
				"r_accountToAIHubMCPServers_accountEntryId",
				aiHubAccountEntry.getAccountEntryId()
			).put(
				"url",
				"http://localhost:" + PortalUtil.getPortalServerPort(false) +
					"/o/mcp"
			).build(),
			ServiceContextTestUtil.getServiceContext(
				GroupTestUtil.addGroup(), TestPropsValues.getUserId()));

		_addAgentDefinitionObjectEntry(
			"L_AI_DECISION_NODE_WORKFLOW_DEFINITION", "content",
			"ai-decision-node-workflow-definition.json",
			"AI Decision Node Workflow Definition");
		_addAgentDefinitionObjectEntry(
			"L_AI_DECISION_NODE_WITH_TOOL_WORKFLOW_DEFINITION", "question",
			"ai-decision-node-with-tool-workflow-definition.json",
			"AI Decision Node With Tool Workflow Definition");
		_addAgentDefinitionObjectEntry(
			"L_LLM_NODE_WITH_RAG_WORKFLOW_DEFINITION", "userMessage",
			"llm-node-with-rag-workflow-definition.json",
			"LLM Node With RAG Workflow Definition");
		_addAgentDefinitionObjectEntry(
			"L_LLM_NODE_WITH_TOOL_WORKFLOW_DEFINITION", "userMessage",
			"llm-node-with-tool-workflow-definition.json",
			"LLM Node With Tool Workflow Definition");
		_addAgentDefinitionObjectEntry(
			"L_WORKFLOW_DEFINITION", "text", "workflow-definition.json",
			"Workflow Definition");
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);

		_objectDefinitionLocalService.deleteObjectDefinition(
			_mcpServerObjectDefinition.getObjectDefinitionId());
		_objectDefinitionLocalService.deleteObjectDefinition(
			_objectDefinition.getObjectDefinitionId());

		PrincipalThreadLocal.setName(_originalName);
	}

	@After
	public void tearDown() throws Exception {
		ServiceContextThreadLocal.popServiceContext();
		SseUtil.closeAll();
		ConfigurationTestUtil.deleteConfiguration(
			AIHubCellConfiguration.class.getName());
	}

	@Override
	@Test
	public void testGetAgentInstanceSubscribe() throws Exception {
		Assert.assertNotNull(
			SseEventSourceTestUtil.open(
				List.of(), new ArrayList<>(), "agent-instances/subscribe"));
	}

	@Ignore
	@Override
	@Test
	public void testPostAgentInstance() throws Exception {
		_testPostAgentInstance();
		_testPostAgentInstanceWithTypeAIDecisionNodeWithToolWorkflowDefinition();
		_testPostAgentInstanceWithTypeAIDecisionNodeWorkflowDefinition();
		_testPostAgentInstanceWithTypeFixSpellingAndGrammarWithInstruction();
		_testPostAgentInstanceWithTypeLLMNodeWithRAGWorkflowDefinition();
		_testPostAgentInstanceWithTypeLLMNodeWithRAGWorkflowDefinitionWithRestrictedUser();
		_testPostAgentInstanceWithTypeLLMNodeWithToolWorkflowDefinition();
		_testPostAgentInstanceWithTypeMakeShorter();
		_testPostAgentInstanceWithTypeMakeShorterAndExhaustedQuota();
		_testPostAgentInstanceWithTypePageBuilder();
	}

	private static void _addAgentDefinitionObjectEntry(
			String externalReferenceCode, String inputVariables,
			String workflowDefinitionFileName, String workflowDefinitionName)
		throws Exception {

		_workflowDefinitionManager.deployWorkflowDefinition(
			_getContentBytes(workflowDefinitionFileName),
			TestPropsValues.getCompanyId(), null,
			_accountEntry.getAccountEntryGroupId(), workflowDefinitionName,
			WorkflowDefinitionConstants.SCOPE_AI, StringUtil.randomId(),
			TestPropsValues.getUserId());

		_objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_agentDefinitionObjectDefinition.getObjectDefinitionId(), 0,
			LocaleUtil.toLanguageId(LocaleUtil.getDefault()),
			HashMapBuilder.<String, Serializable>put(
				"active", true
			).put(
				"externalReferenceCode", externalReferenceCode
			).put(
				"inputVariables", inputVariables
			).put(
				"outputVariable", "output"
			).put(
				"r_accountToAIHubAgentDefinitions_accountEntryId",
				_accountEntry.getAccountEntryId()
			).put(
				"title_i18n",
				(Serializable)HashMapBuilder.put(
					LocaleUtil.toLanguageId(LocaleUtil.getDefault()),
					workflowDefinitionName
				).build()
			).put(
				"workflowDefinitionName", workflowDefinitionName
			).build(),
			ServiceContextTestUtil.getServiceContext(
				GroupTestUtil.addGroup(), TestPropsValues.getUserId()));
	}

	private static byte[] _getContentBytes(String fileName) throws Exception {
		String content = _read(fileName);

		return content.getBytes();
	}

	private static String _read(String fileName) throws Exception {
		InputStream inputStream =
			AgentInstanceResourceTest.class.getResourceAsStream(
				"dependencies/" + fileName);

		return StringUtil.read(inputStream);
	}

	private ObjectEntry _addOrUpdateInstructionDefinitionObjectEntry(
			Map<String, Serializable> values)
		throws Exception {

		return _objectEntryLocalService.addOrUpdateObjectEntry(
			"L_AI_HUB_INSTRUCTION_DEFINITION", 0, TestPropsValues.getUserId(),
			_instructionObjectDefinition.getObjectDefinitionId(), 0,
			HashMapBuilder.<String, Serializable>put(
				"r_accountToAIHubInstructionDefinitions_accountEntryId",
				_accountEntry.getAccountEntryId()
			).put(
				"title_i18n",
				(Serializable)RandomTestUtil.randomLanguageIdStringMap()
			).putAll(
				values
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private String _getExpectedPromptInput(
			Map<String, Serializable> instructionDefinitionObjectEntryValues,
			String instructionDefinitionScope)
		throws Exception {

		String prompt = StringBundler.concat(
			"You are an expert linguistic editor. Your sole task is to ",
			"correct all grammatical, spelling, and punctuation errors in the ",
			"provided text while preserving its meaning, tone, and style. Do ",
			"not alter structure or wording beyond what is necessary for ",
			"grammatical precision and natural fluency. Output only the ",
			"corrected text, with no explanations or commentary. If the text ",
			"is already correct, return it unchanged.");

		if (!GetterUtil.getBoolean(
				instructionDefinitionObjectEntryValues.get("active"))) {

			return prompt;
		}

		String scope = GetterUtil.getString(
			instructionDefinitionObjectEntryValues.get("scope"));

		if (!StringUtil.equals(scope, "everywhere") &&
			!StringUtil.equals(instructionDefinitionScope, scope)) {

			return prompt;
		}

		String instruction = GetterUtil.getString(
			instructionDefinitionObjectEntryValues.get("instruction"));
		String occasion = GetterUtil.getString(
			instructionDefinitionObjectEntryValues.get("occasion"));

		if (Validator.isNull(occasion)) {
			return StringUtil.replace(
				_read("expected-prompt-input-with-instruction.txt"),
				new String[] {"${instruction}", "${prompt}"},
				new String[] {instruction, prompt});
		}

		return StringUtil.replace(
			_read("expected-prompt-input-with-instruction-and-occasion.txt"),
			new String[] {"${instruction}", "${occasion}", "${prompt}"},
			new String[] {
				StringUtil.lowerCaseFirstLetter(instruction),
				StringUtil.removeLast(occasion, StringPool.PERIOD), prompt
			});
	}

	private JSONObject _postAgentInstance(
			String agentDefinitionExternalReferenceCode, String inputText,
			String inputVariable, String sseEventSinkKey)
		throws Exception {

		return _postAgentInstance(
			agentDefinitionExternalReferenceCode, inputText, inputVariable,
			null, sseEventSinkKey);
	}

	private JSONObject _postAgentInstance(
			String agentDefinitionExternalReferenceCode, String inputText,
			String inputVariable, String instructionDefinitionScope,
			String sseEventSinkKey)
		throws Exception {

		JSONObject tokenJSONObject = TokenTestUtil.postToken();

		return HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"agentDefinitionExternalReferenceCode",
				agentDefinitionExternalReferenceCode
			).put(
				"context", JSONUtil.put(inputVariable, inputText)
			).put(
				"instructionDefinitionScope", instructionDefinitionScope
			).put(
				"sseEventSinkKey", sseEventSinkKey
			).toString(),
			"ai-hub/v1.0/agent-instances",
			HashMapBuilder.put(
				"Authorization",
				"Bearer " + tokenJSONObject.getString("accessToken")
			).put(
				"Liferay-AI-Hub-Cell-On-Behalf-Of",
				tokenJSONObject.getString("userToken")
			).build(),
			Http.Method.POST);
	}

	private void _testPostAgentInstance() throws Exception {
		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"agentDefinitionExternalReferenceCode", "L_WORKFLOW_DEFINITION"
			).put(
				"context", JSONUtil.put("text", RandomTestUtil.randomString())
			).put(
				"sseEventSinkKey", RandomTestUtil.randomString()
			).toString(),
			"ai-hub/v1.0/agent-instances", Http.Method.POST);

		WorkflowInstance workflowInstance =
			_workflowInstanceManager.getWorkflowInstance(
				TestPropsValues.getCompanyId(),
				jsonObject.getLong("externalReferenceCode"));

		Assert.assertEquals(
			"Workflow Definition",
			workflowInstance.getWorkflowDefinitionName());

		Assert.assertEquals(1, workflowInstance.getWorkflowDefinitionVersion());

		_workflowDefinitionManager.deployWorkflowDefinition(
			_getContentBytes("workflow-definition.json"),
			TestPropsValues.getCompanyId(), null,
			_accountEntry.getAccountEntryGroupId(), "Workflow Definition",
			WorkflowDefinitionConstants.SCOPE_AI, StringUtil.randomId(),
			TestPropsValues.getUserId());

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"agentDefinitionExternalReferenceCode", "L_WORKFLOW_DEFINITION"
			).put(
				"context", JSONUtil.put("text", RandomTestUtil.randomString())
			).put(
				"sseEventSinkKey", RandomTestUtil.randomString()
			).toString(),
			"ai-hub/v1.0/agent-instances", Http.Method.POST);

		workflowInstance = _workflowInstanceManager.getWorkflowInstance(
			TestPropsValues.getCompanyId(),
			jsonObject.getLong("externalReferenceCode"));

		Assert.assertEquals(2, workflowInstance.getWorkflowDefinitionVersion());
	}

	private void _testPostAgentInstanceWithTypeAIDecisionNodeWithToolWorkflowDefinition()
		throws Exception {

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		JSONObject jsonObject = _postAgentInstance(
			"L_AI_DECISION_NODE_WITH_TOOL_WORKFLOW_DEFINITION",
			"Is the \"get_openapis\" tool available?", "question",
			RandomTestUtil.randomString());

		IdempotentRetryAssert.retryAssert(
			5, TimeUnit.SECONDS, 1, TimeUnit.SECONDS,
			() -> {
				WorkflowInstance workflowInstance =
					_workflowInstanceManager.getWorkflowInstance(
						TestPropsValues.getCompanyId(),
						jsonObject.getLong("externalReferenceCode"));

				List<WorkflowNode> workflowNodes =
					workflowInstance.getCurrentWorkflowNodes();

				WorkflowNode workflowNode = workflowNodes.get(0);

				Assert.assertEquals("approved", workflowNode.getName());

				return null;
			});

		Assert.assertEquals(
			originalPermissionChecker,
			PermissionThreadLocal.getPermissionChecker());
	}

	private void _testPostAgentInstanceWithTypeAIDecisionNodeWorkflowDefinition()
		throws Exception {

		_testPostAgentInstanceWithTypeAIDecisionNodeWorkflowDefinition(
			"Blue banana, or Blue Java, is a variety of a banana that grows " +
				"in Brazil.",
			"approved");
		_testPostAgentInstanceWithTypeAIDecisionNodeWorkflowDefinition(
			"Innovative technology transforms everyday life with smarter " +
				"digital solutions.",
			"rejected");
	}

	private void _testPostAgentInstanceWithTypeAIDecisionNodeWorkflowDefinition(
			String content, String workflowNodeName)
		throws Exception {

		JSONObject jsonObject = _postAgentInstance(
			"L_AI_DECISION_NODE_WORKFLOW_DEFINITION", content, "content",
			RandomTestUtil.randomString());

		IdempotentRetryAssert.retryAssert(
			10, TimeUnit.SECONDS, 1, TimeUnit.SECONDS,
			() -> {
				WorkflowInstance workflowInstance =
					_workflowInstanceManager.getWorkflowInstance(
						TestPropsValues.getCompanyId(),
						jsonObject.getLong("externalReferenceCode"));

				List<WorkflowNode> workflowNodes =
					workflowInstance.getCurrentWorkflowNodes();

				WorkflowNode workflowNode = workflowNodes.get(0);

				Assert.assertEquals(workflowNodeName, workflowNode.getName());

				return null;
			});
	}

	private void _testPostAgentInstanceWithTypeFixSpellingAndGrammarWithInstruction()
		throws Exception {

		// Active, scope clickToChat

		ObjectEntry instructionDefinitionObjectEntry =
			_addOrUpdateInstructionDefinitionObjectEntry(
				HashMapBuilder.<String, Serializable>put(
					"active", true
				).put(
					"instruction", "Respond in ALL CAPS."
				).put(
					"scope", "clickToChat"
				).build());

		_testPostAgentInstanceWithTypeFixSpellingAndGrammarWithInstruction(
			"THIS TEXT IS WRONG.", "Thi text ix wrong.",
			instructionDefinitionObjectEntry, "clickToChat");
		_testPostAgentInstanceWithTypeFixSpellingAndGrammarWithInstruction(
			"This text is wrong.", "Thi text ix wrong.",
			instructionDefinitionObjectEntry, "cms");

		// Active, scope everywhere

		instructionDefinitionObjectEntry =
			_addOrUpdateInstructionDefinitionObjectEntry(
				HashMapBuilder.<String, Serializable>put(
					"active", true
				).put(
					"instruction",
					"Preserve all grammar errors exactly as they appear."
				).put(
					"scope", "everywhere"
				).build());

		_testPostAgentInstanceWithTypeFixSpellingAndGrammarWithInstruction(
			"Thi text ix wrong.", "Thi text ix wrong.",
			instructionDefinitionObjectEntry, "clickToChat");
		_testPostAgentInstanceWithTypeFixSpellingAndGrammarWithInstruction(
			"Thi text ix wrong.", "Thi text ix wrong.",
			instructionDefinitionObjectEntry, "cms");

		// Active, scope everywhere with occasion

		instructionDefinitionObjectEntry =
			_addOrUpdateInstructionDefinitionObjectEntry(
				HashMapBuilder.<String, Serializable>put(
					"active", true
				).put(
					"instruction",
					"Preserve all grammar errors exactly as they appear."
				).put(
					"occasion", "When the text is a poem or song lyrics."
				).put(
					"scope", "everywhere"
				).build());

		_testPostAgentInstanceWithTypeFixSpellingAndGrammarWithInstruction(
			"Song she sang to me, song she brang to me.",
			"Song she sang to me, song she brang to me.",
			instructionDefinitionObjectEntry, "everywhere");
		_testPostAgentInstanceWithTypeFixSpellingAndGrammarWithInstruction(
			"This text is wrong.", "Thi text ix wrong.",
			instructionDefinitionObjectEntry, "everywhere");

		// Inactive, scope everywhere

		_testPostAgentInstanceWithTypeFixSpellingAndGrammarWithInstruction(
			"This text is wrong.", "Thi text ix wrong.",
			_addOrUpdateInstructionDefinitionObjectEntry(
				HashMapBuilder.<String, Serializable>put(
					"active", false
				).put(
					"instruction", "Respond in ALL CAPS."
				).put(
					"scope", "everywhere"
				).build()),
			null);
	}

	private void
			_testPostAgentInstanceWithTypeFixSpellingAndGrammarWithInstruction(
				String expectedOutput, String input,
				ObjectEntry instructionDefinitionObjectEntry,
				String instructionDefinitionScope)
		throws Exception {

		CountDownLatch countDownLatch = new CountDownLatch(4);
		List<String> lines = new ArrayList<>();

		String sseEventSinkKey = SseEventSourceTestUtil.open(
			List.of(countDownLatch), lines, "agent-instances/subscribe");

		JSONObject jsonObject = _postAgentInstance(
			"L_FIX_SPELLING_AND_GRAMMAR", input, "text",
			instructionDefinitionScope, sseEventSinkKey);

		Assert.assertTrue(countDownLatch.await(10, TimeUnit.SECONDS));

		Assert.assertEquals(lines.toString(), 4, lines.size());
		Assert.assertEquals("event: L_FIX_SPELLING_AND_GRAMMAR", lines.get(2));
		JSONAssert.assertEquals(
			JSONUtil.put(
				"data", expectedOutput
			).put(
				"nodeName", "fixSpellingAndGrammar"
			).toString(),
			StringUtil.removeSubstring(lines.get(3), "data: "),
			JSONCompareMode.LENIENT);

		IdempotentRetryAssert.retryAssert(
			5, TimeUnit.SECONDS, 1, TimeUnit.SECONDS,
			() -> {
				WorkflowInstance workflowInstance =
					_workflowInstanceManager.getWorkflowInstance(
						TestPropsValues.getCompanyId(),
						jsonObject.getLong("externalReferenceCode"));

				Assert.assertEquals(
					expectedOutput,
					MapUtil.getString(
						workflowInstance.getWorkflowContext(),
						"rewrittenText"));

				List<WorkflowLog> workflowLogs =
					_workflowLogManager.getWorkflowLogsByWorkflowInstance(
						TestPropsValues.getCompanyId(),
						workflowInstance.getWorkflowInstanceId(),
						List.of(WorkflowLog.NODE_USAGE_METADATA),
						QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

				Assert.assertEquals(
					workflowLogs.toString(), 1, workflowLogs.size());

				WorkflowLog workflowLog = workflowLogs.get(0);

				Map<String, Serializable> workflowContext =
					WorkflowContextUtil.convert(
						workflowLog.getWorkflowContext());

				int inputTokensCount = GetterUtil.getInteger(
					workflowContext.get("inputTokensCount"));

				Assert.assertTrue(inputTokensCount > 0);

				Assert.assertEquals(
					expectedOutput, workflowContext.get("output"));

				int outputTokensCount = GetterUtil.getInteger(
					workflowContext.get("outputTokensCount"));

				Assert.assertTrue(outputTokensCount > 0);

				Assert.assertEquals(
					_getExpectedPromptInput(
						instructionDefinitionObjectEntry.getValues(),
						instructionDefinitionScope),
					workflowContext.get("promptInput"));
				Assert.assertEquals(
					inputTokensCount + outputTokensCount,
					GetterUtil.getInteger(
						workflowContext.get("totalTokenCount")));
				Assert.assertEquals(
					"This is the text to be fixed: " + input,
					workflowContext.get("userMessageInput"));

				return null;
			});

		SseUtil.closeAll();
	}

	private void _testPostAgentInstanceWithTypeLLMNodeWithRAGWorkflowDefinition()
		throws Exception {

		CountDownLatch countDownLatch1 = new CountDownLatch(4);
		CountDownLatch countDownLatch2 = new CountDownLatch(6);
		List<String> lines = new ArrayList<>();

		String sseEventSinkKey = SseEventSourceTestUtil.open(
			List.of(countDownLatch1, countDownLatch2), lines,
			"agent-instances/subscribe");

		_postAgentInstance(
			"L_LLM_NODE_WITH_RAG_WORKFLOW_DEFINITION",
			"What is Feliphe's favorite food?", "userMessage", sseEventSinkKey);

		Assert.assertTrue(countDownLatch1.await(10, TimeUnit.SECONDS));

		Assert.assertEquals(lines.toString(), 4, lines.size());

		String response = StringUtil.toLowerCase(lines.get(3));

		Assert.assertFalse(response, response.contains("brazilian barbecue"));
		Assert.assertTrue(response, response.contains("\"nodename\":\"llm\""));

		_objectEntryLocalService.addObjectEntry(
			0L, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(), 0,
			LocaleUtil.toLanguageId(LocaleUtil.getDefault()),
			HashMapBuilder.<String, Serializable>put(
				"description", "His favorite food is Brazilian barbecue."
			).put(
				"name", "Feliphe"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_postAgentInstance(
			"L_LLM_NODE_WITH_RAG_WORKFLOW_DEFINITION",
			"What is Feliphe's favorite food?", "userMessage", sseEventSinkKey);

		Assert.assertTrue(countDownLatch2.await(10, TimeUnit.SECONDS));

		Assert.assertEquals(lines.toString(), 6, lines.size());

		response = StringUtil.toLowerCase(lines.get(5));

		Assert.assertTrue(response, response.contains("brazilian barbecue"));
		Assert.assertTrue(response, response.contains("\"nodename\":\"llm\""));

		SseUtil.closeAll();
	}

	private void _testPostAgentInstanceWithTypeLLMNodeWithRAGWorkflowDefinitionWithRestrictedUser()
		throws Exception {

		CountDownLatch countDownLatch1 = new CountDownLatch(4);
		CountDownLatch countDownLatch2 = new CountDownLatch(6);
		List<String> lines = new ArrayList<>();

		String password = RandomTestUtil.randomString();

		User user = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			password, RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), LocaleUtil.getDefault(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			ServiceContextTestUtil.getServiceContext());

		user.setEmailAddressVerified(true);

		user = _userLocalService.updateUser(user);

		long userId = user.getUserId();

		_objectEntryLocalService.addObjectEntry(
			0L, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(), 0,
			LocaleUtil.toLanguageId(LocaleUtil.getDefault()),
			HashMapBuilder.<String, Serializable>put(
				"description", "His favorite food is Brazilian barbecue."
			).put(
				"name", "Feliphe"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		String sseEventSinkKey = SseEventSourceTestUtil.open(
			List.of(countDownLatch1, countDownLatch2), lines,
			"agent-instances/subscribe");

		HTTPTestUtil.customize(
		).withCredentials(
			user.getEmailAddress(), password
		).apply(
			() -> {
				_postAgentInstance(
					"L_LLM_NODE_WITH_RAG_WORKFLOW_DEFINITION",
					"What is Feliphe's favorite food?", "userMessage",
					sseEventSinkKey);

				Assert.assertTrue(countDownLatch1.await(10, TimeUnit.SECONDS));

				Assert.assertEquals(lines.toString(), 4, lines.size());

				String response = StringUtil.toLowerCase(lines.get(3));

				Assert.assertFalse(
					response, response.contains("brazilian barbecue"));
				Assert.assertTrue(
					response, response.contains("\"nodename\":\"llm\""));

				Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

				_resourcePermissionLocalService.setResourcePermissions(
					TestPropsValues.getCompanyId(),
					_objectDefinition.getClassName(),
					ResourceConstants.SCOPE_COMPANY,
					String.valueOf(TestPropsValues.getCompanyId()),
					role.getRoleId(), new String[] {ActionKeys.VIEW});

				_userLocalService.addRoleUser(role.getRoleId(), userId);

				_postAgentInstance(
					"L_LLM_NODE_WITH_RAG_WORKFLOW_DEFINITION",
					"What is Feliphe's favorite food?", "userMessage",
					sseEventSinkKey);

				Assert.assertTrue(countDownLatch2.await(10, TimeUnit.SECONDS));

				Assert.assertEquals(lines.toString(), 6, lines.size());

				response = StringUtil.toLowerCase(lines.get(5));

				Assert.assertTrue(
					response, response.contains("brazilian barbecue"));
				Assert.assertTrue(
					response, response.contains("\"nodename\":\"llm\""));
			}
		);

		SseUtil.closeAll();
	}

	private void _testPostAgentInstanceWithTypeLLMNodeWithToolWorkflowDefinition()
		throws Exception {

		CountDownLatch countDownLatch = new CountDownLatch(4);
		List<String> lines = new ArrayList<>();

		String sseEventSinkKey = SseEventSourceTestUtil.open(
			List.of(countDownLatch), lines, "agent-instances/subscribe");

		_postAgentInstance(
			"L_LLM_NODE_WITH_TOOL_WORKFLOW_DEFINITION",
			"Is the \"get_openapi\" tool available?", "userMessage",
			sseEventSinkKey);

		Assert.assertTrue(countDownLatch.await(10, TimeUnit.SECONDS));

		Assert.assertEquals(lines.toString(), 4, lines.size());

		String response = StringUtil.toLowerCase(lines.get(3));

		Assert.assertTrue(response, response.contains("\"nodename\":\"llm\""));
		Assert.assertTrue(response, response.contains("yes"));

		SseUtil.closeAll();
	}

	private void _testPostAgentInstanceWithTypeMakeShorter() throws Exception {
		CountDownLatch countDownLatch = new CountDownLatch(4);
		List<String> lines = new ArrayList<>();

		String sseEventSinkKey = SseEventSourceTestUtil.open(
			List.of(countDownLatch), lines, "agent-instances/subscribe");

		String inputText =
			"This is a long and detailed sentence that should be shortened " +
				"by the AI model for testing purposes.";

		JSONObject jsonObject = _postAgentInstance(
			"L_MAKE_SHORTER", inputText, "text", sseEventSinkKey);

		Assert.assertTrue(countDownLatch.await(10, TimeUnit.SECONDS));

		Assert.assertEquals(lines.toString(), 4, lines.size());
		Assert.assertEquals("event: L_MAKE_SHORTER", lines.get(2));

		JSONObject outputJSONObject = _jsonFactory.createJSONObject(
			StringUtil.removeSubstring(lines.get(3), "data: "));

		Assert.assertEquals(
			"makeShorter", outputJSONObject.getString("nodeName"));

		String output = outputJSONObject.getString("data");

		Assert.assertTrue(output.length() < inputText.length());

		IdempotentRetryAssert.retryAssert(
			5, TimeUnit.SECONDS, 1, TimeUnit.SECONDS,
			() -> {
				WorkflowInstance workflowInstance =
					_workflowInstanceManager.getWorkflowInstance(
						TestPropsValues.getCompanyId(),
						jsonObject.getLong("externalReferenceCode"));

				Map<String, Serializable> workflowContext =
					workflowInstance.getWorkflowContext();

				String rewrittenText = GetterUtil.getString(
					workflowContext.get("rewrittenText"));

				Assert.assertTrue(rewrittenText.length() < inputText.length());

				return null;
			});

		SseUtil.closeAll();
	}

	private void _testPostAgentInstanceWithTypeMakeShorterAndExhaustedQuota()
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_AI_HUB_QUOTA", TestPropsValues.getCompanyId());

		_objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), 0,
			LocaleUtil.toLanguageId(LocaleUtil.getDefault()),
			HashMapBuilder.<String, Serializable>put(
				"externalReferenceCode",
				"quota-" + _accountEntry.getAccountEntryId()
			).put(
				"limit", 0
			).put(
				"r_accountToAIHubQuotas_accountEntryId",
				_accountEntry.getAccountEntryId()
			).put(
				"usage", 0
			).build(),
			ServiceContextTestUtil.getServiceContext());

		CountDownLatch countDownLatch = new CountDownLatch(3);
		List<String> lines = new ArrayList<>();

		String sseEventSinkKey = SseEventSourceTestUtil.open(
			List.of(countDownLatch), lines, "agent-instances/subscribe");

		_postAgentInstance(
			"L_MAKE_SHORTER", "This is a long text.", "text", sseEventSinkKey);

		Assert.assertTrue(countDownLatch.await(10, TimeUnit.SECONDS));

		Assert.assertEquals(lines.toString(), 4, lines.size());

		String line = lines.get(3);

		Assert.assertTrue(
			line, line.contains("You have exceeded your token quota"));

		SseUtil.closeAll();
	}

	private void _testPostAgentInstanceWithTypePageBuilder() throws Exception {
		CountDownLatch countDownLatch = new CountDownLatch(4);
		List<String> lines = new ArrayList<>();

		String sseEventSinkKey = SseEventSourceTestUtil.open(
			List.of(countDownLatch), lines, "agent-instances/subscribe");

		_postAgentInstance(
			"L_PAGE_BUILDER",
			"Create a page called \"Hello\" with a heading that says \"Hello " +
				"World\".",
			"instruction", sseEventSinkKey);

		Assert.assertTrue(countDownLatch.await(10, TimeUnit.SECONDS));

		Assert.assertEquals(lines.toString(), 4, lines.size());
		Assert.assertEquals("event: L_PAGE_BUILDER", lines.get(2));

		JSONObject outputJSONObject = _jsonFactory.createJSONObject(
			StringUtil.removeSubstring(lines.get(3), "data: "));

		Assert.assertEquals(
			"pageBuilder", outputJSONObject.getString("nodeName"));

		String data = outputJSONObject.getString("data");

		Assert.assertTrue(data, data.contains("BASIC_COMPONENT-heading"));
		Assert.assertTrue(data, data.contains("ContentPage"));
		Assert.assertTrue(data, data.contains("ContentPageSpecification"));
		Assert.assertTrue(data, data.contains("Hello World"));

		SseUtil.closeAll();
	}

	private static AccountEntry _accountEntry;

	@Inject
	private static AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private static AccountEntryUserRelLocalService
		_accountEntryUserRelLocalService;

	private static ObjectDefinition _agentDefinitionObjectDefinition;

	@Inject
	private static ClassNameLocalService _classNameLocalService;

	private static ObjectDefinition _contentRetrieverObjectDefinition;
	private static ObjectDefinition _instructionObjectDefinition;
	private static ObjectDefinition _mcpServerObjectDefinition;
	private static ObjectDefinition _objectDefinition;

	@Inject
	private static ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private static ObjectEntryLocalService _objectEntryLocalService;

	private static String _originalName;
	private static PermissionChecker _originalPermissionChecker;

	@Inject
	private static SiteInitializerRegistry _siteInitializerRegistry;

	@Inject
	private static WorkflowDefinitionManager _workflowDefinitionManager;

	@Inject
	private ContentRetrieverManager _contentRetrieverManager;

	@Inject
	private DTOConverterRegistry _dtoConverterRegistry;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private SearchEngineAdapter _searchEngineAdapter;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private WorkflowInstanceManager _workflowInstanceManager;

	@Inject
	private WorkflowLogManager _workflowLogManager;

}