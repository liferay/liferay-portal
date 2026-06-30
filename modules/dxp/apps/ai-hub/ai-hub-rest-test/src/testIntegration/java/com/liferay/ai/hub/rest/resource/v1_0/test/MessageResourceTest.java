/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.resource.v1_0.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.ai.hub.cell.configuration.AIHubCellConfiguration;
import com.liferay.ai.hub.configuration.VertexAIConfiguration;
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
import com.liferay.object.test.util.ObjectRelationshipTestUtil;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowInstanceManager;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.initializer.SiteInitializerRegistry;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Feliphe Marinho
 */
@FeatureFlag("LPD-62272")
@RunWith(Arquillian.class)
public class MessageResourceTest extends BaseMessageResourceTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
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

		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));

		_originalName = PrincipalThreadLocal.getName();

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
	}

	@AfterClass
	public static void tearDownClass() {
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);
		PrincipalThreadLocal.setName(_originalName);
		ServiceContextThreadLocal.popServiceContext();
	}

	@After
	public void tearDown() throws Exception {
		SseUtil.closeAll();
		ConfigurationTestUtil.deleteConfiguration(
			AIHubCellConfiguration.class.getName());
	}

	@Ignore
	@Override
	@Test
	public void testPostChatByExternalReferenceCodeMessage() throws Exception {
		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						VertexAIConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"location", TestPropsValues.VERTEX_AI_LOCATION
						).put(
							"modelName", TestPropsValues.VERTEX_AI_MODEL_NAME
						).put(
							"projectId", TestPropsValues.VERTEX_AI_PROJECT_ID
						).build())) {

			CountDownLatch countDownLatch1 = new CountDownLatch(4);
			CountDownLatch countDownLatch2 = new CountDownLatch(6);
			List<String> lines = new ArrayList<>();

			String sseEventSinkKey = SseEventSourceTestUtil.open(
				List.of(countDownLatch1, countDownLatch2), lines,
				"chats/subscribe");

			String text = "this is a short text.";

			JSONObject jsonObject = _postChatByExternalReferenceCodeMessage(
				"Expand the following text: " + text, sseEventSinkKey);

			Assert.assertEquals(
				"Expand the following text: " + text,
				jsonObject.getString("text"));

			Assert.assertEquals(lines.toString(), 2, lines.size());

			Assert.assertTrue(countDownLatch1.await(10, TimeUnit.SECONDS));

			Assert.assertEquals(lines.toString(), 4, lines.size());
			Assert.assertEquals("event: Chat Message Sent", lines.get(2));

			String expandedText = lines.get(3);

			Assert.assertTrue(expandedText.length() > text.length());

			jsonObject = _postChatByExternalReferenceCodeMessage(
				"What was the first message that I sent in this chat?",
				sseEventSinkKey);

			Assert.assertEquals(
				"What was the first message that I sent in this chat?",
				jsonObject.getString("text"));

			Assert.assertEquals(lines.toString(), 4, lines.size());

			Assert.assertTrue(countDownLatch2.await(10, TimeUnit.SECONDS));

			Assert.assertEquals(lines.toString(), 6, lines.size());
			Assert.assertEquals("event: Chat Message Sent", lines.get(2));

			String firstMessageSent = lines.get(5);

			Assert.assertTrue(
				firstMessageSent, firstMessageSent.contains(text));
		}
	}

	@Ignore
	@Test
	public void testPostChatByExternalReferenceCodeMessageWithUnassociatedAgentDefinition()
		throws Exception {

		ObjectDefinition chatbotObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_AI_HUB_CHATBOT", TestPropsValues.getCompanyId());

		String chatbotExternalReferenceCode = RandomTestUtil.randomString();

		ObjectEntry chatbotObjectEntry =
			_objectEntryLocalService.addObjectEntry(
				0, TestPropsValues.getUserId(),
				chatbotObjectDefinition.getObjectDefinitionId(), 0,
				LocaleUtil.toLanguageId(LocaleUtil.getDefault()),
				HashMapBuilder.<String, Serializable>put(
					"externalReferenceCode", chatbotExternalReferenceCode
				).put(
					"r_accountToAIHubChatbots_accountEntryId",
					_accountEntry.getAccountEntryId()
				).put(
					"title_i18n",
					(Serializable)HashMapBuilder.put(
						LocaleUtil.toLanguageId(LocaleUtil.getDefault()),
						RandomTestUtil.randomString()
					).build()
				).build(),
				ServiceContextTestUtil.getServiceContext(
					TestPropsValues.getGroupId(), TestPropsValues.getUserId()));

		ObjectDefinition agentDefinitionObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_AI_HUB_AGENT_DEFINITION",
					TestPropsValues.getCompanyId());

		ObjectEntry agentDefinitionObjectEntry =
			_objectEntryLocalService.getObjectEntry(
				"L_MAKE_SHORTER", 0L,
				agentDefinitionObjectDefinition.getObjectDefinitionId());

		ObjectRelationshipTestUtil.relateObjectEntries(
			agentDefinitionObjectEntry.getObjectEntryId(),
			chatbotObjectEntry.getObjectEntryId(),
			_objectRelationshipLocalService.
				fetchObjectRelationshipByExternalReferenceCode(
					"L_AI_HUB_AGENT_DEFINITIONS_TO_L_AI_HUB_CHATBOTS",
					agentDefinitionObjectDefinition.getObjectDefinitionId()),
			TestPropsValues.getUserId());

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						VertexAIConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"location", TestPropsValues.VERTEX_AI_LOCATION
						).put(
							"modelName", TestPropsValues.VERTEX_AI_MODEL_NAME
						).put(
							"projectId", TestPropsValues.VERTEX_AI_PROJECT_ID
						).build())) {

			CountDownLatch countDownLatch1 = new CountDownLatch(4);
			CountDownLatch countDownLatch2 = new CountDownLatch(6);
			List<String> lines = new ArrayList<>();

			String sseEventSinkKey = SseEventSourceTestUtil.open(
				List.of(countDownLatch1, countDownLatch2), lines,
				"chats/subscribe");

			_postChatByExternalReferenceCodeMessage(
				chatbotExternalReferenceCode,
				"This is a long and detailed sentence that should be " +
					"shortened by the AI model for testing purposes.",
				sseEventSinkKey);

			Assert.assertTrue(countDownLatch1.await(10, TimeUnit.SECONDS));

			Assert.assertEquals(lines.toString(), 4, lines.size());
			Assert.assertEquals("event: Chat Message Sent", lines.get(2));

			ObjectDefinition objectDefinition =
				ObjectDefinitionTestUtil.publishObjectDefinition(
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
				0L, TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId(), 0,
				LocaleUtil.toLanguageId(LocaleUtil.getDefault()),
				HashMapBuilder.<String, Serializable>put(
					"description", "His favorite food is Brazilian barbecue."
				).put(
					"name", "Feliphe"
				).build(),
				ServiceContextTestUtil.getServiceContext());

			_postChatByExternalReferenceCodeMessage(
				chatbotExternalReferenceCode,
				"What is Feliphe's favorite food?", sseEventSinkKey);

			Assert.assertTrue(countDownLatch2.await(10, TimeUnit.SECONDS));

			Assert.assertEquals(lines.toString(), 6, lines.size());
			Assert.assertEquals("event: Chat Message Sent", lines.get(4));

			String response = StringUtil.toLowerCase(lines.get(5));

			Assert.assertFalse(
				response, response.contains("brazilian barbecue"));

			SseUtil.closeAll();
		}
	}

	private JSONObject _postChatByExternalReferenceCodeMessage(
			String inputText, String sseEventSinkKey)
		throws Exception {

		return _postChatByExternalReferenceCodeMessage(
			null, inputText, sseEventSinkKey);
	}

	private JSONObject _postChatByExternalReferenceCodeMessage(
			String chatbotExternalReferenceCode, String inputText,
			String sseEventSinkKey)
		throws Exception {

		JSONObject tokenJSONObject = TokenTestUtil.postToken();

		return HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"chatbotExternalReferenceCode", chatbotExternalReferenceCode
			).put(
				"text", inputText
			).toString(),
			"ai-hub/v1.0/chats/by-external-reference-code/" + sseEventSinkKey +
				"/messages",
			HashMapBuilder.put(
				"Authorization",
				"Bearer " + tokenJSONObject.getString("accessToken")
			).put(
				"Liferay-AI-Hub-Cell-On-Behalf-Of",
				tokenJSONObject.getString("userToken")
			).build(),
			Http.Method.POST);
	}

	private static AccountEntry _accountEntry;

	@Inject
	private static AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private static AccountEntryUserRelLocalService
		_accountEntryUserRelLocalService;

	private static String _originalName;
	private static PermissionChecker _originalPermissionChecker;

	@Inject
	private static SiteInitializerRegistry _siteInitializerRegistry;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Inject
	private WorkflowInstanceManager _workflowInstanceManager;

}