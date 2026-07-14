/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.resource.v1_0.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.ai.hub.configuration.VertexAIConfiguration;
import com.liferay.ai.hub.rest.client.dto.v1_0.AgentDefinition;
import com.liferay.ai.hub.rest.client.dto.v1_0.Model;
import com.liferay.ai.hub.rest.client.dto.v1_0.Status;
import com.liferay.ai.hub.rest.client.dto.v1_0.Variable;
import com.liferay.ai.hub.rest.client.pagination.Page;
import com.liferay.ai.hub.rest.client.pagination.Pagination;
import com.liferay.ai.hub.rest.client.resource.v1_0.AgentDefinitionResource;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.exception.NoSuchObjectEntryException;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
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
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.NoSuchWorkflowDefinitionException;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedFieldsContext;
import com.liferay.portal.vulcan.fields.NestedFieldsContextThreadLocal;
import com.liferay.portal.workflow.constants.WorkflowDefinitionConstants;
import com.liferay.portal.workflow.kaleo.exception.NoSuchDefinitionException;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.initializer.SiteInitializerRegistry;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Feliphe Marinho
 * @author João Victor Alves
 */
@FeatureFlag("LPD-62272")
@RunWith(Arquillian.class)
public class AgentDefinitionResourceTest
	extends BaseAgentDefinitionResourceTestCase {

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

		_dtoConverterContext = new DefaultDTOConverterContext(
			false, Map.of(), _dtoConverterRegistry, null,
			LocaleUtil.getDefault(), null, TestPropsValues.getUser());

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

		_aiHubAccountEntry =
			_accountEntryLocalService.getAccountEntryByExternalReferenceCode(
				"L_AI_HUB", TestPropsValues.getCompanyId());

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			_aiHubAccountEntry.getAccountEntryId(),
			TestPropsValues.getUserId());

		_seoStudioAccountEntry =
			_accountEntryLocalService.getAccountEntryByExternalReferenceCode(
				"L_SEO_STUDIO", TestPropsValues.getCompanyId());
	}

	@AfterClass
	public static void tearDownClass() {
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);

		PrincipalThreadLocal.setName(_originalName);

		ServiceContextThreadLocal.popServiceContext();
	}

	@Override
	@Test
	public void testDeleteAgentDefinitionByExternalReferenceCode()
		throws Exception {

		_testDeleteAgentDefinitionByExternalReferenceCode();
		_testDeleteAgentDefinitionByExternalReferenceCodeWithSharedWorkflowDefinition();
	}

	@Override
	@Test
	public void testGetAgentDefinitionsPage() throws Exception {
		_testGetAgentDefinitionsPage();
		_testGetAgentDefinitionsPageWithFilter();
		_testGetAgentDefinitionsPageWithMissingWorkflowDefinition();
		_testGetAgentDefinitionsPageWithModel();
		_testGetAgentDefinitionsPageWithPermissions();
		_testGetAgentDefinitionsPageWithSort();
	}

	@Ignore
	@Override
	@Test
	public void testGetAgentDefinitionsPageWithPagination() {
	}

	@Override
	@Test
	public void testPatchAgentDefinitionByExternalReferenceCodeUpdateActive()
		throws Exception {

		AgentDefinition agentDefinition = _addAgentDefinition();

		agentDefinition =
			agentDefinitionResource.
				patchAgentDefinitionByExternalReferenceCodeUpdateActive(
					agentDefinition.getExternalReferenceCode(), false);

		Assert.assertFalse(agentDefinition.getActive());

		agentDefinition =
			agentDefinitionResource.
				patchAgentDefinitionByExternalReferenceCodeUpdateActive(
					agentDefinition.getExternalReferenceCode(), true);

		Assert.assertTrue(agentDefinition.getActive());
	}

	@Override
	@Test
	public void testPostAgentDefinitionByExternalReferenceCodeCopy()
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			(DefaultObjectEntryManager)_objectEntryManager;

		NestedFieldsContext nestedFieldsContext =
			NestedFieldsContextThreadLocal.getAndSetNestedFieldsContext(
				new NestedFieldsContext(
					1, List.of("agentDefinitionsToContentRetrievers")));

		try {
			ObjectEntry objectEntry1 =
				defaultObjectEntryManager.partialUpdateObjectEntry(
					TestPropsValues.getCompanyId(), _dtoConverterContext,
					WorkflowDefinitionConstants.
						EXTERNAL_REFERENCE_CODE_CHANGE_TONE,
					_getObjectDefinition(),
					new ObjectEntry() {
						{
							properties = HashMapBuilder.<String, Object>put(
								"agentDefinitionsToContentRetrievers",
								List.of(
									HashMapBuilder.<String, Object>put(
										"crawlDate",
										() -> {
											OffsetDateTime offsetDateTime =
												OffsetDateTime.now(
												).withNano(
													0
												);

											return offsetDateTime.format(
												DateTimeFormatter.
													ISO_OFFSET_DATE_TIME);
										}
									).put(
										"description_i18n",
										JSONUtil.put(
											"en_US",
											StringUtil.toLowerCase(
												RandomTestUtil.randomString()))
									).put(
										"externalReferenceCode",
										StringUtil.toLowerCase(
											RandomTestUtil.randomString())
									).put(
										"indexName",
										StringUtil.toLowerCase(
											RandomTestUtil.randomString())
									).put(
										"r_accountToAIHubContentRetrievers_" +
											"accountEntryId",
										_accountEntry.getAccountEntryId()
									).put(
										"title_i18n",
										JSONUtil.put(
											"en_US",
											StringUtil.toLowerCase(
												RandomTestUtil.randomString()))
									).put(
										"type",
										StringUtil.toLowerCase(
											RandomTestUtil.randomString())
									).put(
										"url",
										StringUtil.toLowerCase(
											RandomTestUtil.randomString())
									).build())
							).build();
						}
					},
					null);

			AgentDefinition agentDefinition =
				agentDefinitionResource.
					postAgentDefinitionByExternalReferenceCodeCopy(
						WorkflowDefinitionConstants.
							EXTERNAL_REFERENCE_CODE_CHANGE_TONE);

			ObjectEntry objectEntry2 = _objectEntryManager.getObjectEntry(
				TestPropsValues.getCompanyId(), _dtoConverterContext,
				agentDefinition.getExternalReferenceCode(),
				_getObjectDefinition(), null);

			Assert.assertEquals(
				objectEntry1.getPropertyValue("active"),
				objectEntry2.getPropertyValue("active"));
			Assert.assertEquals(
				objectEntry1.getPropertyValue("description"),
				objectEntry2.getPropertyValue("description"));
			Assert.assertNotEquals(
				objectEntry1.getPropertyValue("externalReferenceCode"),
				objectEntry2.getPropertyValue("externalReferenceCode"));
			Assert.assertEquals(
				objectEntry1.getPropertyValue("inputVariables"),
				objectEntry2.getPropertyValue("inputVariables"));
			Assert.assertEquals(
				objectEntry1.getPropertyValue("outputVariable"),
				objectEntry2.getPropertyValue("outputVariable"));
			Assert.assertEquals(
				_accountEntry.getAccountEntryId(),
				objectEntry2.getPropertyValue(
					"r_accountToAIHubAgentDefinitions_accountEntryId"));
			Assert.assertNotEquals(
				objectEntry1.getPropertyValue("title_i18n"),
				objectEntry2.getPropertyValue("title_i18n"));
			Assert.assertNotEquals(
				objectEntry1.getPropertyValue("workflowDefinitionName"),
				objectEntry2.getPropertyValue("workflowDefinitionName"));

			ObjectEntry[] objectEntries1 =
				(ObjectEntry[])objectEntry1.getPropertyValue(
					"agentDefinitionsToContentRetrievers");

			objectEntry1 = objectEntries1[0];

			ObjectEntry[] objectEntries2 =
				(ObjectEntry[])objectEntry2.getPropertyValue(
					"agentDefinitionsToContentRetrievers");

			objectEntry2 = objectEntries2[0];

			Assert.assertEquals(
				objectEntry1.getPropertyValue("crawlDate"),
				objectEntry2.getPropertyValue("crawlDate"));
			Assert.assertEquals(
				objectEntry1.getPropertyValue("description_i18n"),
				objectEntry2.getPropertyValue("description_i18n"));
			Assert.assertEquals(
				objectEntry1.getPropertyValue("externalReferenceCode"),
				objectEntry2.getPropertyValue("externalReferenceCode"));
			Assert.assertEquals(
				objectEntry1.getPropertyValue("indexName"),
				objectEntry2.getPropertyValue("indexName"));
			Assert.assertEquals(
				objectEntry1.getPropertyValue("title_i18n"),
				objectEntry2.getPropertyValue("title_i18n"));
			Assert.assertEquals(
				objectEntry1.getPropertyValue("type"),
				objectEntry2.getPropertyValue("type"));
			Assert.assertEquals(
				objectEntry1.getPropertyValue("url"),
				objectEntry2.getPropertyValue("url"));

			WorkflowDefinition workflowDefinition1 =
				_workflowDefinitionManager.getLatestWorkflowDefinition(
					TestPropsValues.getCompanyId(),
					WorkflowDefinitionConstants.NAME_CHANGE_TONE);

			WorkflowDefinition workflowDefinition2 =
				_workflowDefinitionManager.getWorkflowDefinition(
					TestPropsValues.getCompanyId(),
					agentDefinition.getWorkflowDefinitionName(), 1);

			Assert.assertEquals(
				workflowDefinition1.getContentAsXML(),
				workflowDefinition2.getContentAsXML());
			Assert.assertEquals(
				workflowDefinition1.getDescription(),
				workflowDefinition2.getDescription());
			Assert.assertNotEquals(
				workflowDefinition1.getExternalReferenceCode(),
				workflowDefinition2.getExternalReferenceCode());
			Assert.assertEquals(
				_accountEntry.getAccountEntryGroupId(),
				workflowDefinition2.getGroupId());
			Assert.assertNotEquals(
				workflowDefinition1.getName(), workflowDefinition2.getName());
			Assert.assertNotEquals(
				workflowDefinition1.getWorkflowDefinitionId(),
				workflowDefinition2.getWorkflowDefinitionId());
		}
		finally {
			NestedFieldsContextThreadLocal.setNestedFieldsContext(
				nestedFieldsContext);
		}
	}

	@Override
	@Test
	public void testPostAgentDefinitionDraft() throws Exception {
		AgentDefinition agentDefinition =
			agentDefinitionResource.postAgentDefinitionDraft();

		Assert.assertFalse(agentDefinition.getActive());

		Status status = agentDefinition.getStatus();

		Assert.assertEquals("draft", status.getLabel());

		String workflowDefinitionName =
			agentDefinition.getWorkflowDefinitionName();

		Assert.assertNotNull(workflowDefinitionName);

		WorkflowDefinition workflowDefinition =
			_workflowDefinitionManager.getWorkflowDefinition(
				TestPropsValues.getCompanyId(), workflowDefinitionName, 1);

		Assert.assertEquals(
			_accountEntry.getAccountEntryGroupId(),
			workflowDefinition.getGroupId());

		ObjectEntry objectEntry = _objectEntryManager.getObjectEntry(
			TestPropsValues.getCompanyId(), _dtoConverterContext,
			agentDefinition.getExternalReferenceCode(), _getObjectDefinition(),
			null);

		com.liferay.object.rest.dto.v1_0.Status objectEntryStatus =
			objectEntry.getStatus();

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, (int)objectEntryStatus.getCode());

		_objectEntryManager.updateObjectEntry(
			TestPropsValues.getCompanyId(), _dtoConverterContext,
			agentDefinition.getExternalReferenceCode(), _getObjectDefinition(),
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"active", true
					).put(
						"description", RandomTestUtil.randomString()
					).put(
						"inputVariables", "text"
					).put(
						"outputVariable", "result"
					).put(
						"r_accountToAIHubAgentDefinitions_accountEntryId",
						_accountEntry.getAccountEntryId()
					).put(
						"title_i18n",
						HashMapBuilder.put(
							"en_US", RandomTestUtil.randomString()
						).build()
					).put(
						"workflowDefinitionName", workflowDefinitionName
					).build();
				}
			},
			null);

		objectEntry = _objectEntryManager.getObjectEntry(
			TestPropsValues.getCompanyId(), _dtoConverterContext,
			agentDefinition.getExternalReferenceCode(), _getObjectDefinition(),
			null);

		objectEntryStatus = objectEntry.getStatus();

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED,
			(int)objectEntryStatus.getCode());

		agentDefinitionResource.deleteAgentDefinitionByExternalReferenceCode(
			agentDefinition.getExternalReferenceCode());
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"active", "externalReferenceCode", "inputVariables",
			"outputVariable", "version", "workflowDefinitionName"
		};
	}

	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {"dateCreated", "dateModified"};
	}

	@Override
	protected AgentDefinition
			testBatchEngineDeleteImportTask_addAgentDefinition()
		throws Exception {

		return _addAgentDefinition();
	}

	@Override
	protected AgentDefinition
			testDeleteAgentDefinitionByExternalReferenceCode_addAgentDefinition()
		throws Exception {

		return _addAgentDefinition();
	}

	@Override
	protected AgentDefinition testGetAgentDefinitionsPage_addAgentDefinition(
		AgentDefinition agentDefinition) {

		return agentDefinition;
	}

	private AgentDefinition _addAgentDefinition() throws Exception {
		return agentDefinitionResource.
			postAgentDefinitionByExternalReferenceCodeCopy(
				WorkflowDefinitionConstants.
					EXTERNAL_REFERENCE_CODE_CHANGE_TONE);
	}

	private ObjectEntry _addAgentDefinitionObjectEntry(
			String workflowDefinitionName)
		throws Exception {

		return _objectEntryManager.addObjectEntry(
			_dtoConverterContext, _getObjectDefinition(),
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"active", true
					).put(
						"description", RandomTestUtil.randomString()
					).put(
						"inputVariables", "text"
					).put(
						"outputVariable", "rewrittenText"
					).put(
						"r_accountToAIHubAgentDefinitions_accountEntryId",
						_aiHubAccountEntry.getAccountEntryId()
					).put(
						"title_i18n",
						HashMapBuilder.put(
							"en_US", RandomTestUtil.randomString()
						).build()
					).put(
						"workflowDefinitionName", workflowDefinitionName
					).build();
				}
			},
			null);
	}

	private void _assertAgentDefinitionModel(
		String expectedLabel, String expectedName, String expectedProviderLabel,
		AgentDefinition agentDefinition) {

		Model model = agentDefinition.getModel();

		Assert.assertEquals(expectedLabel, model.getLabel());
		Assert.assertEquals(expectedName, model.getName());
		Assert.assertEquals(expectedProviderLabel, model.getProviderLabel());
	}

	private Page<AgentDefinition> _getAgentDefinitionsPageWithModel(
			String modelName)
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						VertexAIConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"modelName", modelName
						).build())) {

			return agentDefinitionResource.getAgentDefinitionsPage(
				null, null, Pagination.of(1, 1), null);
		}
	}

	private ObjectDefinition _getObjectDefinition() throws Exception {
		return _objectDefinitionLocalService.getObjectDefinition(
			TestPropsValues.getCompanyId(), "AIHubAgentDefinition");
	}

	private void _testDeleteAgentDefinitionByExternalReferenceCode()
		throws Exception {

		AgentDefinition agentDefinition = _addAgentDefinition();

		ObjectDefinition objectDefinition = _getObjectDefinition();

		ObjectEntry objectEntry = _objectEntryManager.getObjectEntry(
			TestPropsValues.getCompanyId(), _dtoConverterContext,
			agentDefinition.getExternalReferenceCode(), objectDefinition, null);

		WorkflowDefinition workflowDefinition =
			_workflowDefinitionManager.getWorkflowDefinition(
				TestPropsValues.getCompanyId(),
				agentDefinition.getWorkflowDefinitionName(), 1);

		agentDefinitionResource.deleteAgentDefinitionByExternalReferenceCode(
			agentDefinition.getExternalReferenceCode());

		AssertUtils.assertFailure(
			NoSuchObjectEntryException.class,
			StringBundler.concat(
				"No ObjectEntry exists with the key {externalReferenceCode=",
				objectEntry.getExternalReferenceCode(),
				", groupId=0, companyId=", TestPropsValues.getCompanyId(),
				", objectDefinitionId=",
				objectDefinition.getObjectDefinitionId(), "}"),
			() -> _objectEntryManager.getObjectEntry(
				TestPropsValues.getCompanyId(), _dtoConverterContext,
				agentDefinition.getExternalReferenceCode(), objectDefinition,
				null));
		AssertUtils.assertFailure(
			NoSuchWorkflowDefinitionException.class,
			NoSuchDefinitionException.class.getName() +
				": No KaleoDefinition exists with the primary key " +
					workflowDefinition.getWorkflowDefinitionId(),
			() -> _workflowDefinitionManager.getWorkflowDefinition(
				workflowDefinition.getWorkflowDefinitionId()));
	}

	private void _testDeleteAgentDefinitionByExternalReferenceCodeWithSharedWorkflowDefinition()
		throws Exception {

		AgentDefinition agentDefinition = _addAgentDefinition();

		String workflowDefinitionName =
			agentDefinition.getWorkflowDefinitionName();

		ObjectEntry objectEntry = _addAgentDefinitionObjectEntry(
			workflowDefinitionName);

		WorkflowDefinition workflowDefinition =
			_workflowDefinitionManager.getWorkflowDefinition(
				TestPropsValues.getCompanyId(), workflowDefinitionName, 1);

		agentDefinitionResource.deleteAgentDefinitionByExternalReferenceCode(
			agentDefinition.getExternalReferenceCode());

		WorkflowDefinition latestWorkflowDefinition =
			_workflowDefinitionManager.getLatestWorkflowDefinition(
				TestPropsValues.getCompanyId(), workflowDefinitionName);

		Assert.assertTrue(latestWorkflowDefinition.isActive());

		agentDefinitionResource.deleteAgentDefinitionByExternalReferenceCode(
			objectEntry.getExternalReferenceCode());

		AssertUtils.assertFailure(
			NoSuchWorkflowDefinitionException.class,
			NoSuchDefinitionException.class.getName() +
				": No KaleoDefinition exists with the primary key " +
					workflowDefinition.getWorkflowDefinitionId(),
			() -> _workflowDefinitionManager.getWorkflowDefinition(
				workflowDefinition.getWorkflowDefinitionId()));
	}

	private void _testGetAgentDefinitionsPage() throws Exception {
		Page<AgentDefinition> page =
			agentDefinitionResource.getAgentDefinitionsPage(
				null, null, Pagination.of(1, 20), null);

		assertEquals(
			_systemAgentDefinitions, (List<AgentDefinition>)page.getItems());
	}

	private void _testGetAgentDefinitionsPageWithFilter() throws Exception {

		// Active as false

		Page<AgentDefinition> page =
			agentDefinitionResource.getAgentDefinitionsPage(
				null, "(active eq false)", Pagination.of(1, 20), null);

		assertEquals(
			ListUtil.filter(
				_systemAgentDefinitions,
				systemAgentDefinition -> !systemAgentDefinition.getActive()),
			(List<AgentDefinition>)page.getItems());

		// Active as true

		page = agentDefinitionResource.getAgentDefinitionsPage(
			null, "(active eq true)", Pagination.of(1, 20), null);

		assertEquals(
			ListUtil.filter(
				_systemAgentDefinitions, AgentDefinition::getActive),
			(List<AgentDefinition>)page.getItems());

		// Date created

		page = agentDefinitionResource.getAgentDefinitionsPage(
			null, "dateCreated gt 2000-01-01T00:00:00Z", Pagination.of(1, 20),
			null);

		assertEquals(
			_systemAgentDefinitions, (List<AgentDefinition>)page.getItems());

		// Date modified

		page = agentDefinitionResource.getAgentDefinitionsPage(
			null, "dateModified gt 2000-01-01T00:00:00Z", Pagination.of(1, 20),
			null);

		assertEquals(
			_systemAgentDefinitions, (List<AgentDefinition>)page.getItems());
	}

	private void _testGetAgentDefinitionsPageWithMissingWorkflowDefinition()
		throws Exception {

		ObjectEntry objectEntry = _addAgentDefinitionObjectEntry(
			RandomTestUtil.randomString());

		Page<AgentDefinition> page =
			agentDefinitionResource.getAgentDefinitionsPage(
				null, null, Pagination.of(1, 20), null);

		AgentDefinition agentDefinition = null;

		for (AgentDefinition curAgentDefinition : page.getItems()) {
			if (Objects.equals(
					curAgentDefinition.getExternalReferenceCode(),
					objectEntry.getExternalReferenceCode())) {

				agentDefinition = curAgentDefinition;

				break;
			}
		}

		Assert.assertNotNull(agentDefinition);
		Assert.assertNull(agentDefinition.getVersion());

		agentDefinitionResource.deleteAgentDefinitionByExternalReferenceCode(
			objectEntry.getExternalReferenceCode());
	}

	private void _testGetAgentDefinitionsPageWithModel() throws Exception {

		// Mapped model name

		Page<AgentDefinition> page = _getAgentDefinitionsPageWithModel(
			"gemini-2.5-flash");

		_assertAgentDefinitionModel(
			"Gemini 2.5 Flash", "gemini-2.5-flash", "Google",
			page.fetchFirstItem());

		// Unmapped model name

		String modelName = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		page = _getAgentDefinitionsPageWithModel(modelName);

		_assertAgentDefinitionModel(
			modelName, modelName, "Google", page.fetchFirstItem());
	}

	private void _testGetAgentDefinitionsPageWithPermissions()
		throws Exception {

		String password = RandomTestUtil.randomString();

		User user = UserTestUtil.addUser(
			testCompany.getCompanyId(), TestPropsValues.getUserId(), password,
			RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), LocaleUtil.getDefault(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			ServiceContextTestUtil.getServiceContext());

		user.setEmailAddressVerified(true);

		user = _userLocalService.updateUser(user);

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			_aiHubAccountEntry.getAccountEntryId(), user.getUserId());
		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			_seoStudioAccountEntry.getAccountEntryId(), user.getUserId());

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_userLocalService.addRoleUser(role.getRoleId(), user.getUserId());

		ObjectDefinition objectDefinition = _getObjectDefinition();

		_resourcePermissionLocalService.setResourcePermissions(
			testCompany.getCompanyId(), objectDefinition.getClassName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(testCompany.getCompanyId()), role.getRoleId(),
			new String[] {ActionKeys.VIEW});

		AgentDefinitionResource agentDefinitionResource =
			AgentDefinitionResource.builder(
			).authentication(
				user.getEmailAddress(), password
			).endpoint(
				testCompany.getVirtualHostname(),
				PortalUtil.getPortalServerPort(false), "http"
			).locale(
				LocaleUtil.getDefault()
			).build();

		Page<AgentDefinition> page =
			agentDefinitionResource.getAgentDefinitionsPage(
				null, null, Pagination.of(1, 20), null);

		for (AgentDefinition agentDefinition : page.getItems()) {
			Map<String, Map<String, String>> actions =
				agentDefinition.getActions();

			Assert.assertFalse(actions.containsKey("permissions"));
		}

		_resourcePermissionLocalService.setResourcePermissions(
			testCompany.getCompanyId(), objectDefinition.getClassName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(testCompany.getCompanyId()), role.getRoleId(),
			new String[] {ActionKeys.PERMISSIONS, ActionKeys.VIEW});

		page = agentDefinitionResource.getAgentDefinitionsPage(
			null, null, Pagination.of(1, 20), null);

		for (AgentDefinition agentDefinition : page.getItems()) {
			Map<String, Map<String, String>> actions =
				agentDefinition.getActions();

			Assert.assertTrue(actions.containsKey("permissions"));
		}
	}

	private void _testGetAgentDefinitionsPageWithSort() throws Exception {

		// Date created

		_testGetAgentDefinitionsPageWithSort("dateCreated");

		// Date modified

		_testGetAgentDefinitionsPageWithSort("dateModified");
	}

	private void _testGetAgentDefinitionsPageWithSort(String sortField)
		throws Exception {

		Page<AgentDefinition> ascPage =
			agentDefinitionResource.getAgentDefinitionsPage(
				null, null, Pagination.of(1, 20), sortField + ":asc");

		Page<AgentDefinition> descPage =
			agentDefinitionResource.getAgentDefinitionsPage(
				null, null, Pagination.of(1, 20), sortField + ":desc");

		List<AgentDefinition> agentDefinitions = ListUtil.fromCollection(
			descPage.getItems());

		Collections.reverse(agentDefinitions);

		assertEquals(
			(List<AgentDefinition>)ascPage.getItems(), agentDefinitions);
	}

	private static AccountEntry _accountEntry;

	@Inject
	private static AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private static AccountEntryUserRelLocalService
		_accountEntryUserRelLocalService;

	private static AccountEntry _aiHubAccountEntry;
	private static DTOConverterContext _dtoConverterContext;

	@Inject
	private static DTOConverterRegistry _dtoConverterRegistry;

	private static String _originalName;
	private static PermissionChecker _originalPermissionChecker;
	private static AccountEntry _seoStudioAccountEntry;

	@Inject
	private static SiteInitializerRegistry _siteInitializerRegistry;

	private static final List<AgentDefinition> _systemAgentDefinitions =
		List.of(
			new AgentDefinition() {
				{
					active = true;
					externalReferenceCode = "L_AUTO_CATEGORIZE";
					inputVariables = new Variable[] {
						new Variable() {
							{
								name = "candidateCategories";
								type = "string";
							}
						},
						new Variable() {
							{
								name = "content";
								type = "string";
							}
						},
						new Variable() {
							{
								name = "count";
								type = "string";
							}
						}
					};
					outputVariable = new Variable() {
						{
							name = "suggestedCategories";
							type = "string";
						}
					};
					version = 1;
					workflowDefinitionName = "Auto Categorize";
				}
			},
			new AgentDefinition() {
				{
					active = true;
					externalReferenceCode =
						WorkflowDefinitionConstants.
							EXTERNAL_REFERENCE_CODE_CHANGE_TONE;
					inputVariables = new Variable[] {
						new Variable() {
							{
								name = "text";
								type = "string";
							}
						},
						new Variable() {
							{
								name = "tone";
								type = "string";
							}
						}
					};
					outputVariable = new Variable() {
						{
							name = "rewrittenText";
							type = "string";
						}
					};
					version = 1;
					workflowDefinitionName =
						WorkflowDefinitionConstants.NAME_CHANGE_TONE;
				}
			},
			new AgentDefinition() {
				{
					active = true;
					externalReferenceCode =
						WorkflowDefinitionConstants.
							EXTERNAL_REFERENCE_CODE_CONTENT_GAP_ANALYSIS;
					inputVariables = new Variable[] {
						new Variable() {
							{
								name = "contentCoverage";
								type = "string";
							}
						},
						new Variable() {
							{
								name = "focusScope";
								type = "string";
							}
						},
						new Variable() {
							{
								name = "projectContext";
								type = "string";
							}
						}
					};
					outputVariable = new Variable() {
						{
							name = "gapAnalysis";
							type = "string";
						}
					};
					version = 1;
					workflowDefinitionName =
						WorkflowDefinitionConstants.NAME_CONTENT_GAP_ANALYSIS;
				}
			},
			new AgentDefinition() {
				{
					active = true;
					externalReferenceCode =
						WorkflowDefinitionConstants.
							EXTERNAL_REFERENCE_CODE_FIND_MATCHING_ASSETS;
					inputVariables = new Variable[] {
						new Variable() {
							{
								name = "cmsGroupId";
								type = "string";
							}
						},
						new Variable() {
							{
								name = "funnelStageId";
								type = "string";
							}
						},
						new Variable() {
							{
								name = "keywords";
								type = "string";
							}
						},
						new Variable() {
							{
								name = "personaId";
								type = "string";
							}
						},
						new Variable() {
							{
								name = "portalURL";
								type = "string";
							}
						},
						new Variable() {
							{
								name = "tasks";
								type = "string";
							}
						}
					};
					outputVariable = new Variable() {
						{
							name = "matchingAssets";
							type = "string";
						}
					};
					version = 1;
					workflowDefinitionName =
						WorkflowDefinitionConstants.NAME_FIND_MATCHING_ASSETS;
				}
			},
			new AgentDefinition() {
				{
					active = true;
					externalReferenceCode =
						WorkflowDefinitionConstants.
							EXTERNAL_REFERENCE_CODE_FIX_SPELLING_AND_GRAMMAR;
					inputVariables = new Variable[] {
						new Variable() {
							{
								name = "text";
								type = "string";
							}
						}
					};
					outputVariable = new Variable() {
						{
							name = "rewrittenText";
							type = "string";
						}
					};
					version = 1;
					workflowDefinitionName =
						WorkflowDefinitionConstants.
							NAME_FIX_SPELLING_AND_GRAMMAR;
				}
			},
			new AgentDefinition() {
				{
					active = false;
					externalReferenceCode =
						WorkflowDefinitionConstants.
							EXTERNAL_REFERENCE_CODE_GENERATE_IMAGE;
					inputVariables = new Variable[] {
						new Variable() {
							{
								name = "description";
								type = "string";
							}
						},
						new Variable() {
							{
								name = "style";
								type = "string";
							}
						}
					};
					outputVariable = new Variable() {
						{
							name = "imageBase64";
							type = "string";
						}
					};
					version = 1;
					workflowDefinitionName =
						WorkflowDefinitionConstants.NAME_GENERATE_IMAGE;
				}
			},
			new AgentDefinition() {
				{
					active = true;
					externalReferenceCode =
						WorkflowDefinitionConstants.
							EXTERNAL_REFERENCE_CODE_GENERATE_CONTENT;
					inputVariables = new Variable[] {
						new Variable() {
							{
								name = "brief";
								type = "string";
							}
						},
						new Variable() {
							{
								name = "count";
								type = "string";
							}
						}
					};
					outputVariable = new Variable() {
						{
							name = "output";
							type = "string";
						}
					};
					version = 1;
					workflowDefinitionName =
						WorkflowDefinitionConstants.NAME_GENERATE_CONTENT;
				}
			},
			new AgentDefinition() {
				{
					active = true;
					externalReferenceCode = "L_GENERATE_TAGS";
					inputVariables = new Variable[] {
						new Variable() {
							{
								name = "content";
								type = "string";
							}
						},
						new Variable() {
							{
								name = "count";
								type = "string";
							}
						},
						new Variable() {
							{
								name = "existingTags";
								type = "string";
							}
						}
					};
					outputVariable = new Variable() {
						{
							name = "suggestedTags";
							type = "string";
						}
					};
					version = 1;
					workflowDefinitionName = "Generate Tags";
				}
			},
			new AgentDefinition() {
				{
					active = true;
					externalReferenceCode =
						WorkflowDefinitionConstants.
							EXTERNAL_REFERENCE_CODE_IMPROVE_WRITING;
					inputVariables = new Variable[] {
						new Variable() {
							{
								name = "text";
								type = "string";
							}
						}
					};
					outputVariable = new Variable() {
						{
							name = "rewrittenText";
							type = "string";
						}
					};
					version = 1;
					workflowDefinitionName =
						WorkflowDefinitionConstants.NAME_IMPROVE_WRITING;
				}
			},
			new AgentDefinition() {
				{
					active = true;
					externalReferenceCode =
						WorkflowDefinitionConstants.
							EXTERNAL_REFERENCE_CODE_LIFERAY_SEARCH;
					inputVariables = new Variable[] {
						new Variable() {
							{
								name = "request";
								type = "string";
							}
						}
					};
					outputVariable = new Variable() {
						{
							name = "response";
							type = "string";
						}
					};
					version = 1;
					workflowDefinitionName =
						WorkflowDefinitionConstants.NAME_LIFERAY_SEARCH;
				}
			},
			new AgentDefinition() {
				{
					active = true;
					externalReferenceCode =
						WorkflowDefinitionConstants.
							EXTERNAL_REFERENCE_CODE_MAKE_LONGER;
					inputVariables = new Variable[] {
						new Variable() {
							{
								name = "text";
								type = "string";
							}
						}
					};
					outputVariable = new Variable() {
						{
							name = "rewrittenText";
							type = "string";
						}
					};
					version = 1;
					workflowDefinitionName =
						WorkflowDefinitionConstants.NAME_MAKE_LONGER;
				}
			},
			new AgentDefinition() {
				{
					active = true;
					externalReferenceCode =
						WorkflowDefinitionConstants.
							EXTERNAL_REFERENCE_CODE_MAKE_SHORTER;
					inputVariables = new Variable[] {
						new Variable() {
							{
								name = "text";
								type = "string";
							}
						}
					};
					outputVariable = new Variable() {
						{
							name = "rewrittenText";
							type = "string";
						}
					};
					version = 1;
					workflowDefinitionName =
						WorkflowDefinitionConstants.NAME_MAKE_SHORTER;
				}
			},
			new AgentDefinition() {
				{
					active = true;
					externalReferenceCode =
						"L_SEO_STUDIO_DESCRIPTION_GENERATOR";
					inputVariables = new Variable[] {
						new Variable() {
							{
								name = "pageContent";
								type = "string";
							}
						}
					};
					outputVariable = new Variable() {
						{
							name = "metaDescription";
							type = "string";
						}
					};
					version = 1;
					workflowDefinitionName = "SEO Studio Description Generator";
				}
			},
			new AgentDefinition() {
				{
					active = true;
					externalReferenceCode = "L_SEO_STUDIO_TITLE_GENERATOR";
					inputVariables = new Variable[] {
						new Variable() {
							{
								name = "pageContent";
								type = "string";
							}
						}
					};
					outputVariable = new Variable() {
						{
							name = "titleTag";
							type = "string";
						}
					};
					version = 1;
					workflowDefinitionName = "SEO Studio Title Generator";
				}
			},
			new AgentDefinition() {
				{
					active = false;
					externalReferenceCode = "L_SITE_BUILDER";
					inputVariables = new Variable[] {
						new Variable() {
							{
								name = "generationExternalReferenceCode";
								type = "string";
							}
						},
						new Variable() {
							{
								name = "request";
								type = "string";
							}
						}
					};
					outputVariable = new Variable() {
						{
							name = "output";
							type = "string";
						}
					};
					version = 1;
					workflowDefinitionName = "Site Builder";
				}
			});

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject(filter = "object.entry.manager.storage.type=default")
	private ObjectEntryManager _objectEntryManager;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private WorkflowDefinitionManager _workflowDefinitionManager;

}