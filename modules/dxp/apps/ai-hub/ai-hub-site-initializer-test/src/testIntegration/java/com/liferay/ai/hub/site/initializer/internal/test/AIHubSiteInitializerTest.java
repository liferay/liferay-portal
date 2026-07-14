/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.site.initializer.internal.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountRole;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.utility.page.kernel.constants.LayoutUtilityPageEntryConstants;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryLocalService;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.notification.model.NotificationTemplate;
import com.liferay.notification.service.NotificationTemplateLocalService;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.workflow.constants.WorkflowDefinitionConstants;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.initializer.SiteInitializerRegistry;

import java.io.Serializable;

import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Feliphe Marinho
 */
@FeatureFlag("LPD-62272")
@RunWith(Arquillian.class)
public class AIHubSiteInitializerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testInitialize() throws Exception {
		SiteInitializer siteInitializer =
			_siteInitializerRegistry.getSiteInitializer(
				"com.liferay.ai.hub.site.initializer");

		siteInitializer.initialize(TestPropsValues.getGroupId());

		_assertAccountRoleExists(
			"L_AI_HUB_ADMINISTRATOR", "AI Hub Administrator");
		_assertAccountRoleExists(
			"L_AI_HUB_AGENT_ADMINISTRATOR", "AI Hub Agent Administrator");
		_assertAccountRoleExists(
			"L_AI_HUB_AGENT_VIEWER", "AI Hub Agent Viewer");
		_assertAccountRoleObjectEntryResourcePermission(
			"L_AI_HUB_AGENT_DEFINITION", "L_AI_HUB_ADMINISTRATOR", "DELETE",
			"PERMISSIONS", "UPDATE", "VIEW");
		_assertAccountRoleObjectEntryResourcePermission(
			"L_AI_HUB_CHATBOT", "L_AI_HUB_ADMINISTRATOR", "DELETE",
			"PERMISSIONS", "UPDATE", "VIEW");
		_assertAccountRoleObjectEntryResourcePermission(
			"L_AI_HUB_CONTENT_RETRIEVER", "L_AI_HUB_ADMINISTRATOR", "DELETE",
			"PERMISSIONS", "UPDATE", "VIEW");
		_assertAccountRoleObjectEntryResourcePermission(
			"L_AI_HUB_CRAWLER_JOB", "L_AI_HUB_ADMINISTRATOR", "DELETE",
			"PERMISSIONS", "UPDATE", "VIEW");
		_assertAccountRoleObjectEntryResourcePermission(
			"L_AI_HUB_INSTRUCTION_DEFINITION", "L_AI_HUB_ADMINISTRATOR",
			"DELETE", "PERMISSIONS", "UPDATE", "VIEW");
		_assertAccountRoleObjectEntryResourcePermission(
			"L_AI_HUB_MCP_SERVER", "L_AI_HUB_ADMINISTRATOR", "DELETE",
			"PERMISSIONS", "UPDATE", "VIEW");
		_assertAccountRoleObjectEntryResourcePermission(
			"L_AI_HUB_AGENT_DEFINITION", "L_AI_HUB_AGENT_ADMINISTRATOR",
			"DELETE", "PERMISSIONS", "UPDATE", "VIEW");
		_assertAccountRoleObjectEntryResourcePermission(
			"L_AI_HUB_AGENT_DEFINITION", "L_AI_HUB_AGENT_VIEWER", "VIEW");
		_assertAccountRolePortletResourcePermission(
			"L_AI_HUB_AGENT_DEFINITION", "L_AI_HUB_ADMINISTRATOR",
			"ADD_OBJECT_ENTRY");
		_assertAccountRolePortletResourcePermission(
			"L_AI_HUB_CHATBOT", "L_AI_HUB_ADMINISTRATOR", "ADD_OBJECT_ENTRY");
		_assertAccountRolePortletResourcePermission(
			"L_AI_HUB_CONTENT_RETRIEVER", "L_AI_HUB_ADMINISTRATOR",
			"ADD_OBJECT_ENTRY");
		_assertAccountRolePortletResourcePermission(
			"L_AI_HUB_CRAWLER_JOB", "L_AI_HUB_ADMINISTRATOR",
			"ADD_OBJECT_ENTRY");
		_assertAccountRolePortletResourcePermission(
			"L_AI_HUB_INSTRUCTION_DEFINITION", "L_AI_HUB_ADMINISTRATOR",
			"ADD_OBJECT_ENTRY");
		_assertAccountRolePortletResourcePermission(
			"L_AI_HUB_MCP_SERVER", "L_AI_HUB_ADMINISTRATOR",
			"ADD_OBJECT_ENTRY");
		_assertAccountRolePortletResourcePermission(
			"L_AI_HUB_AGENT_DEFINITION", "L_AI_HUB_AGENT_ADMINISTRATOR",
			"ADD_OBJECT_ENTRY");
		_assertLayoutExists("/account-management", "Account Management");
		_assertLayoutExists("/guardrails", "Guardrails");
		_assertLayoutUtilityPageEntryExists(
			"L_AI_HUB_CREATE_ACCOUNT_UTILITY_PAGE",
			LayoutUtilityPageEntryConstants.TYPE_CREATE_ACCOUNT);
		_assertListTypeDefinitionExists(
			"L_AI_HUB_CRAWLER_JOB_STATUSES", "abandoned", "dispatched",
			"failed", "queued", "running", "succeeded");
		_assertListTypeDefinitionExists(
			"L_AI_HUB_GUARDRAIL_CONFIDENCE_LEVELS", "high", "lowAndAbove",
			"mediumAndAbove");
		_assertListTypeDefinitionExists(
			"L_AI_HUB_GUARDRAIL_RESPONSIBLE_AI_LEVELS", "high", "lowAndAbove",
			"mediumAndAbove", "none");
		_assertListTypeDefinitionExists(
			"L_AI_HUB_GUARDRAIL_TYPES", "input", "output");
		_assertListTypeDefinitionExists(
			"L_AI_HUB_INSTRUCTION_DEFINITION_SCOPES", "clickToChat", "cms",
			"everywhere");
		_assertListTypeDefinitionUserRoleViewPermission(
			"L_AI_HUB_CRAWLER_JOB_STATUSES");
		_assertListTypeDefinitionUserRoleViewPermission(
			"L_AI_HUB_GUARDRAIL_CONFIDENCE_LEVELS");
		_assertListTypeDefinitionUserRoleViewPermission(
			"L_AI_HUB_GUARDRAIL_RESPONSIBLE_AI_LEVELS");
		_assertListTypeDefinitionUserRoleViewPermission(
			"L_AI_HUB_GUARDRAIL_TYPES");
		_assertListTypeDefinitionUserRoleViewPermission(
			"L_AI_HUB_INSTRUCTION_DEFINITION_SCOPES");
		_assertNotificationTemplateExists(
			"L_AI_HUB_ACCOUNT_INVITE_USER_EMAIL_NOTIFICATION_TEMPLATE");
		_assertObjectDefinitionExists("L_AI_HUB_AGENT_DEFINITION");
		_assertObjectDefinitionExists("L_AI_HUB_CHATBOT");
		_assertObjectDefinitionExists("L_AI_HUB_CONFIGURATION");
		_assertObjectDefinitionExists("L_AI_HUB_CONTENT_RETRIEVER");
		_assertObjectDefinitionExists("L_AI_HUB_CRAWLER_JOB");
		_assertObjectDefinitionExists("L_AI_HUB_GUARDRAIL");
		_assertObjectDefinitionExists("L_AI_HUB_INSTRUCTION_DEFINITION");
		_assertObjectDefinitionExists("L_AI_HUB_MCP_SERVER");
		_assertObjectEntryExists(
			"L_AI_HUB_INSTRUCTION_DEFINITION", "L_AI_HUB_ACTION_BOUNDARY");
		_assertObjectEntryExists(
			"L_AI_HUB_INSTRUCTION_DEFINITION", "L_AI_HUB_AI_TRANSPARENCY");
		_assertObjectEntryExists(
			"L_AI_HUB_INSTRUCTION_DEFINITION",
			"L_AI_HUB_DATA_MINIMIZATION_IN_CONTEXT");
		_assertObjectEntryExists(
			"L_AI_HUB_INSTRUCTION_DEFINITION", "L_AI_HUB_NO_HARMFUL_CONTENT");
		_assertObjectEntryExists(
			"L_AI_HUB_INSTRUCTION_DEFINITION", "L_AI_HUB_PII_NONGENERATION");
		_assertObjectEntryExists(
			"L_AI_HUB_INSTRUCTION_DEFINITION", "L_AI_HUB_PROHIBITED_PRACTICES");
		_assertObjectFieldDefaultValue(
			"L_AI_HUB_GUARDRAIL", "location", "europe-west1");
		_assertObjectFieldSettingValue(
			"L_AI_HUB_CHATBOT", "avatar",
			ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE, "512000");
		_assertObjectFieldsExist(
			"L_AI_HUB_AGENT_DEFINITION", "active", "description",
			"inputVariables", "outputVariable",
			"r_accountToAIHubAgentDefinitions_accountEntryId", "system",
			"title", "workflowDefinitionName");
		_assertObjectFieldsExist(
			"L_AI_HUB_CHATBOT", "active", "avatar", "description",
			"introMessage", "notificationMessage", "placeholderMessage",
			"r_accountToAIHubChatbots_accountEntryId", "title");
		_assertObjectFieldsExist(
			"L_AI_HUB_CONFIGURATION", "environmentURLs",
			"r_accountToAIHubConfigurations_accountEntryId");
		_assertObjectFieldsExist(
			"L_AI_HUB_CONTENT_RETRIEVER", "crawlDate", "description",
			"indexName", "r_accountToAIHubContentRetrievers_accountEntryId",
			"title", "type", "url");
		_assertObjectFieldsExist(
			"L_AI_HUB_CRAWLER_JOB", "crawlerJobStatus", "endDate",
			"errorMessage", "executionId", "indexedDocumentCount",
			"r_accountToAIHubCrawlerJobs_accountEntryId",
			"r_contentRetrieverToCrawlerJobs_aiHubContentRetrieverId",
			"startDate");
		_assertObjectFieldsExist(
			"L_AI_HUB_GUARDRAIL", "active", "description", "guardrailType",
			"location", "maliciousUriFilterEnabled",
			"multilanguageDetectionEnabled", "piAndJailbreakConfidenceLevel",
			"piAndJailbreakFilterEnabled",
			"r_accountToAIHubGuardrails_accountEntryId", "raiDangerousLevel",
			"raiHarassmentLevel", "raiHateSpeechLevel",
			"raiSexuallyExplicitLevel", "sdpFilterEnabled", "title");
		_assertObjectFieldsExist(
			"L_AI_HUB_INSTRUCTION_DEFINITION", "active", "description",
			"instruction", "occasion",
			"r_accountToAIHubInstructionDefinitions_accountEntryId", "scope",
			"system", "title");
		_assertObjectFieldsExist(
			"L_AI_HUB_MCP_SERVER", "r_accountToAIHubMCPServers_accountEntryId",
			"title", "url");
		_assertObjectRelationshipExists(
			ObjectRelationshipConstants.DELETION_TYPE_CASCADE,
			"L_ACCOUNT_TO_L_AI_HUB_AGENT_DEFINITIONS", "L_ACCOUNT",
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		_assertObjectRelationshipExists(
			ObjectRelationshipConstants.DELETION_TYPE_CASCADE,
			"L_ACCOUNT_TO_L_AI_HUB_CONFIGURATIONS", "L_ACCOUNT",
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		_assertObjectRelationshipExists(
			ObjectRelationshipConstants.DELETION_TYPE_CASCADE,
			"L_ACCOUNT_TO_L_AI_HUB_CONTENT_RETRIEVERS", "L_ACCOUNT",
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		_assertObjectRelationshipExists(
			ObjectRelationshipConstants.DELETION_TYPE_CASCADE,
			"L_ACCOUNT_TO_L_AI_HUB_CRAWLER_JOBS", "L_ACCOUNT",
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		_assertObjectRelationshipExists(
			ObjectRelationshipConstants.DELETION_TYPE_CASCADE,
			"L_ACCOUNT_TO_L_AI_HUB_GUARDRAILS", "L_ACCOUNT",
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		_assertObjectRelationshipExists(
			ObjectRelationshipConstants.DELETION_TYPE_CASCADE,
			"L_ACCOUNT_TO_L_AI_HUB_MCP_SERVERS", "L_ACCOUNT",
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		_assertObjectRelationshipExists(
			ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE,
			"L_AI_HUB_AGENT_DEFINITIONS_TO_L_AI_HUB_CONTENT_RETRIEVERS",
			"L_AI_HUB_AGENT_DEFINITION",
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);
		_assertObjectRelationshipExists(
			ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE,
			"L_AI_HUB_AGENT_DEFINITIONS_TO_L_AI_HUB_GUARDRAILS",
			"L_AI_HUB_AGENT_DEFINITION",
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);
		_assertObjectRelationshipExists(
			ObjectRelationshipConstants.DELETION_TYPE_CASCADE,
			"L_AI_HUB_CONTENT_RETRIEVER_TO_L_AI_HUB_CRAWLER_JOBS",
			"L_AI_HUB_CONTENT_RETRIEVER",
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		_assertWorkflowDefinitionExists(
			_ACCOUNT_EXTERNAL_REFERENCE_CODE_AI_HUB,
			WorkflowDefinitionConstants.EXTERNAL_REFERENCE_CODE_CHANGE_TONE,
			WorkflowDefinitionConstants.NAME_CHANGE_TONE);
		_assertWorkflowDefinitionExists(
			_ACCOUNT_EXTERNAL_REFERENCE_CODE_AI_HUB,
			WorkflowDefinitionConstants.
				EXTERNAL_REFERENCE_CODE_CONTENT_GAP_ANALYSIS,
			WorkflowDefinitionConstants.NAME_CONTENT_GAP_ANALYSIS);
		_assertWorkflowDefinitionExists(
			_ACCOUNT_EXTERNAL_REFERENCE_CODE_AI_HUB,
			WorkflowDefinitionConstants.
				EXTERNAL_REFERENCE_CODE_FIND_MATCHING_ASSETS,
			WorkflowDefinitionConstants.NAME_FIND_MATCHING_ASSETS);
		_assertWorkflowDefinitionExists(
			_ACCOUNT_EXTERNAL_REFERENCE_CODE_AI_HUB,
			WorkflowDefinitionConstants.
				EXTERNAL_REFERENCE_CODE_FIX_SPELLING_AND_GRAMMAR,
			WorkflowDefinitionConstants.NAME_FIX_SPELLING_AND_GRAMMAR);
		_assertWorkflowDefinitionExists(
			_ACCOUNT_EXTERNAL_REFERENCE_CODE_AI_HUB,
			WorkflowDefinitionConstants.
				EXTERNAL_REFERENCE_CODE_GENERATE_CONTENT,
			WorkflowDefinitionConstants.NAME_GENERATE_CONTENT);
		_assertWorkflowDefinitionExists(
			_ACCOUNT_EXTERNAL_REFERENCE_CODE_AI_HUB,
			WorkflowDefinitionConstants.EXTERNAL_REFERENCE_CODE_GENERATE_IMAGE,
			WorkflowDefinitionConstants.NAME_GENERATE_IMAGE);
		_assertWorkflowDefinitionExists(
			_ACCOUNT_EXTERNAL_REFERENCE_CODE_AI_HUB,
			WorkflowDefinitionConstants.EXTERNAL_REFERENCE_CODE_IMPROVE_WRITING,
			WorkflowDefinitionConstants.NAME_IMPROVE_WRITING);
		_assertWorkflowDefinitionExists(
			_ACCOUNT_EXTERNAL_REFERENCE_CODE_AI_HUB,
			WorkflowDefinitionConstants.EXTERNAL_REFERENCE_CODE_LIFERAY_SEARCH,
			WorkflowDefinitionConstants.NAME_LIFERAY_SEARCH);
		_assertWorkflowDefinitionExists(
			_ACCOUNT_EXTERNAL_REFERENCE_CODE_AI_HUB,
			WorkflowDefinitionConstants.EXTERNAL_REFERENCE_CODE_MAKE_LONGER,
			WorkflowDefinitionConstants.NAME_MAKE_LONGER);
		_assertWorkflowDefinitionExists(
			_ACCOUNT_EXTERNAL_REFERENCE_CODE_AI_HUB,
			WorkflowDefinitionConstants.EXTERNAL_REFERENCE_CODE_MAKE_SHORTER,
			WorkflowDefinitionConstants.NAME_MAKE_SHORTER);
		_assertWorkflowDefinitionExists(
			_ACCOUNT_EXTERNAL_REFERENCE_CODE_SEO_STUDIO,
			WorkflowDefinitionConstants.
				EXTERNAL_REFERENCE_CODE_SEO_STUDIO_DESCRIPTION_GENERATOR,
			WorkflowDefinitionConstants.NAME_SEO_STUDIO_DESCRIPTION_GENERATOR);
		_assertWorkflowDefinitionExists(
			_ACCOUNT_EXTERNAL_REFERENCE_CODE_SEO_STUDIO,
			WorkflowDefinitionConstants.
				EXTERNAL_REFERENCE_CODE_SEO_STUDIO_TITLE_GENERATOR,
			WorkflowDefinitionConstants.NAME_SEO_STUDIO_TITLE_GENERATOR);
	}

	private void _assertAccountRoleExists(
			String externalReferenceCode, String name)
		throws Exception {

		AccountRole accountRole =
			_accountRoleLocalService.fetchAccountRoleByExternalReferenceCode(
				externalReferenceCode, TestPropsValues.getCompanyId());

		Assert.assertEquals(name, accountRole.getRoleName());
	}

	private void _assertAccountRoleObjectEntryResourcePermission(
			String objectDefinitionExternalReferenceCode,
			String roleExternalReferenceCode, String... actionIds)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					objectDefinitionExternalReferenceCode,
					TestPropsValues.getCompanyId());

		_assertAccountRoleResourcePermission(
			roleExternalReferenceCode, objectDefinition.getClassName(),
			actionIds);
	}

	private void _assertAccountRolePortletResourcePermission(
			String objectDefinitionExternalReferenceCode,
			String roleExternalReferenceCode, String... actionIds)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					objectDefinitionExternalReferenceCode,
					TestPropsValues.getCompanyId());

		_assertAccountRoleResourcePermission(
			roleExternalReferenceCode, objectDefinition.getResourceName(),
			actionIds);
	}

	private void _assertAccountRoleResourcePermission(
			String externalReferenceCode, String resourceName,
			String... actionIds)
		throws Exception {

		AccountRole accountRole =
			_accountRoleLocalService.fetchAccountRoleByExternalReferenceCode(
				externalReferenceCode, TestPropsValues.getCompanyId());

		ResourcePermission resourcePermission =
			_resourcePermissionLocalService.fetchResourcePermission(
				TestPropsValues.getCompanyId(), resourceName,
				ResourceConstants.SCOPE_GROUP_TEMPLATE, "0",
				accountRole.getRoleId());

		for (String actionId : actionIds) {
			Assert.assertTrue(resourcePermission.hasActionId(actionId));
		}
	}

	private void _assertLayoutExists(String friendlyURL, String name)
		throws Exception {

		Layout layout = _layoutLocalService.fetchLayoutByFriendlyURL(
			TestPropsValues.getGroupId(), false, friendlyURL);

		Assert.assertEquals(name, layout.getName(LocaleUtil.getSiteDefault()));
	}

	private void _assertLayoutUtilityPageEntryExists(
			String externalReferenceCode, String type)
		throws Exception {

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryLocalService.
				fetchDefaultLayoutUtilityPageEntry(
					TestPropsValues.getGroupId(), type);

		Assert.assertEquals(
			externalReferenceCode,
			layoutUtilityPageEntry.getExternalReferenceCode());
	}

	private void _assertListTypeDefinitionExists(
			String externalReferenceCode, String... listTypeEntryKeys)
		throws Exception {

		ListTypeDefinition listTypeDefinition =
			_listTypeDefinitionLocalService.
				fetchListTypeDefinitionByExternalReferenceCode(
					externalReferenceCode, TestPropsValues.getCompanyId());

		for (String listTypeEntryKey : listTypeEntryKeys) {
			ListTypeEntry listTypeEntry =
				_listTypeEntryLocalService.getListTypeEntry(
					listTypeDefinition.getListTypeDefinitionId(),
					listTypeEntryKey);

			Assert.assertTrue(listTypeEntry.isSystem());
		}
	}

	private void _assertListTypeDefinitionUserRoleViewPermission(
			String externalReferenceCode)
		throws Exception {

		ListTypeDefinition listTypeDefinition =
			_listTypeDefinitionLocalService.
				fetchListTypeDefinitionByExternalReferenceCode(
					externalReferenceCode, TestPropsValues.getCompanyId());
		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.USER);

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				TestPropsValues.getCompanyId(),
				ListTypeDefinition.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(listTypeDefinition.getListTypeDefinitionId()),
				role.getRoleId(), ActionKeys.VIEW));
	}

	private void _assertNotificationTemplateExists(String externalReferenceCode)
		throws Exception {

		NotificationTemplate notificationTemplate =
			_notificationTemplateLocalService.
				fetchNotificationTemplateByExternalReferenceCode(
					externalReferenceCode, TestPropsValues.getCompanyId());

		Assert.assertNotNull(notificationTemplate);
	}

	private void _assertObjectDefinitionExists(String externalReferenceCode)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					externalReferenceCode, TestPropsValues.getCompanyId());

		Assert.assertTrue(objectDefinition.isApproved());
		Assert.assertTrue(objectDefinition.isSystem());
	}

	private void _assertObjectEntryExists(
			String objectDefinitionExternalReferenceCode,
			String objectEntryExternalReferenceCode)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					objectDefinitionExternalReferenceCode,
					TestPropsValues.getCompanyId());

		ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
			objectEntryExternalReferenceCode, 0,
			objectDefinition.getObjectDefinitionId());

		Map<String, Serializable> values = objectEntry.getValues();

		Assert.assertEquals(
			objectEntry.toString(), Boolean.TRUE, values.get("active"));
		Assert.assertEquals(
			objectEntry.toString(), "everywhere", values.get("scope"));
		Assert.assertEquals(
			objectEntry.toString(), Boolean.TRUE, values.get("system"));
	}

	private void _assertObjectFieldDefaultValue(
			String objectDefinitionExternalReferenceCode,
			String objectFieldName, String value)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					objectDefinitionExternalReferenceCode,
					TestPropsValues.getCompanyId());

		ObjectField objectField = _objectFieldLocalService.fetchObjectField(
			objectDefinition.getObjectDefinitionId(), objectFieldName);

		ObjectFieldSetting objectFieldSetting =
			_objectFieldSettingLocalService.fetchObjectFieldSetting(
				objectField.getObjectFieldId(),
				ObjectFieldSettingConstants.NAME_DEFAULT_VALUE);

		Assert.assertEquals(value, objectFieldSetting.getValue());
	}

	private void _assertObjectFieldSettingValue(
			String objectDefinitionExternalReferenceCode,
			String objectFieldName, String objectFieldSettingName, String value)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					objectDefinitionExternalReferenceCode,
					TestPropsValues.getCompanyId());

		ObjectField objectField = _objectFieldLocalService.fetchObjectField(
			objectDefinition.getObjectDefinitionId(), objectFieldName);

		ObjectFieldSetting objectFieldSetting =
			_objectFieldSettingLocalService.fetchObjectFieldSetting(
				objectField.getObjectFieldId(), objectFieldSettingName);

		Assert.assertEquals(value, objectFieldSetting.getValue());
	}

	private void _assertObjectFieldsExist(
			String objectDefinitionExternalReferenceCode,
			String... objectFieldNames)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					objectDefinitionExternalReferenceCode,
					TestPropsValues.getCompanyId());

		for (String objectFieldName : objectFieldNames) {
			Assert.assertNotNull(
				_objectFieldLocalService.fetchObjectField(
					objectDefinition.getObjectDefinitionId(), objectFieldName));
		}
	}

	private void _assertObjectRelationshipExists(
			String deletionType, String externalReferenceCode,
			String objectDefinitionExternalReferenceCode, String type)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					objectDefinitionExternalReferenceCode,
					TestPropsValues.getCompanyId());

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.
				fetchObjectRelationshipByExternalReferenceCode(
					externalReferenceCode,
					objectDefinition.getObjectDefinitionId());

		Assert.assertEquals(deletionType, objectRelationship.getDeletionType());
		Assert.assertEquals(type, objectRelationship.getType());
	}

	private void _assertWorkflowDefinitionExists(
			String accountEntryExternalReferenceCode,
			String externalReferenceCode, String name)
		throws Exception {

		WorkflowDefinition workflowDefinition =
			_workflowDefinitionManager.getWorkflowDefinition(
				TestPropsValues.getCompanyId(), externalReferenceCode);

		Assert.assertEquals(name, workflowDefinition.getName());

		AccountEntry accountEntry =
			_accountEntryLocalService.getAccountEntryByExternalReferenceCode(
				accountEntryExternalReferenceCode,
				TestPropsValues.getCompanyId());

		Assert.assertEquals(
			accountEntry.getAccountEntryGroupId(),
			workflowDefinition.getGroupId());
	}

	private static final String _ACCOUNT_EXTERNAL_REFERENCE_CODE_AI_HUB =
		"L_AI_HUB";

	private static final String _ACCOUNT_EXTERNAL_REFERENCE_CODE_SEO_STUDIO =
		"L_SEO_STUDIO";

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private AccountRoleLocalService _accountRoleLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutUtilityPageEntryLocalService
		_layoutUtilityPageEntryLocalService;

	@Inject
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@Inject
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	@Inject
	private NotificationTemplateLocalService _notificationTemplateLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectFieldSettingLocalService _objectFieldSettingLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private SiteInitializerRegistry _siteInitializerRegistry;

	@Inject
	private WorkflowDefinitionManager _workflowDefinitionManager;

}