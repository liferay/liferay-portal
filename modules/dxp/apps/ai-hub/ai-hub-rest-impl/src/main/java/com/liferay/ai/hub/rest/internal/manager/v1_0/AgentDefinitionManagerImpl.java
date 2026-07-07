/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.internal.manager.v1_0;

import com.liferay.account.model.AccountEntry;
import com.liferay.ai.hub.configuration.VertexAIConfiguration;
import com.liferay.ai.hub.rest.dto.v1_0.AgentDefinition;
import com.liferay.ai.hub.rest.dto.v1_0.Model;
import com.liferay.ai.hub.rest.dto.v1_0.Status;
import com.liferay.ai.hub.rest.dto.v1_0.Variable;
import com.liferay.ai.hub.rest.internal.resource.v1_0.AgentDefinitionResourceImpl;
import com.liferay.ai.hub.rest.manager.v1_0.AgentDefinitionManager;
import com.liferay.ai.hub.util.AccountEntryUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.ActionUtil;
import com.liferay.portal.workflow.constants.WorkflowDefinitionConstants;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;

import jakarta.ws.rs.BadRequestException;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Feliphe Marinho
 * @author João Victor Alves
 */
@Component(service = AgentDefinitionManager.class)
public class AgentDefinitionManagerImpl implements AgentDefinitionManager {

	@Override
	public void deleteAgentDefinition(
			long companyId, DTOConverterContext dtoConverterContext,
			String externalReferenceCode)
		throws Exception {

		ObjectEntry objectEntry = _objectEntryManager.getObjectEntry(
			companyId, dtoConverterContext, externalReferenceCode,
			_getObjectDefinition(companyId), null);

		_objectEntryManager.deleteObjectEntry(
			companyId, dtoConverterContext,
			objectEntry.getExternalReferenceCode(),
			_getObjectDefinition(companyId), null);

		String workflowDefinitionName = GetterUtil.getString(
			objectEntry.getPropertyValue("workflowDefinitionName"));

		if (_hasAgentDefinitionWithWorkflowDefinitionName(
				companyId, dtoConverterContext, workflowDefinitionName)) {

			return;
		}

		WorkflowDefinition workflowDefinition = _fetchLatestWorkflowDefinition(
			companyId, workflowDefinitionName);

		if (workflowDefinition == null) {
			return;
		}

		_workflowDefinitionManager.updateActive(
			false, workflowDefinition.getCompanyId(),
			workflowDefinition.getName(), dtoConverterContext.getUserId(),
			workflowDefinition.getVersion());

		_workflowDefinitionManager.undeployWorkflowDefinition(
			workflowDefinition.getCompanyId(), workflowDefinition.getName(),
			dtoConverterContext.getUserId(), workflowDefinition.getVersion());
	}

	@Override
	public AgentDefinition getAgentDefinition(
			long companyId, DTOConverterContext dtoConverterContext,
			String externalReferenceCode)
		throws Exception {

		return _toAgentDefinition(
			companyId, dtoConverterContext,
			_objectEntryManager.getObjectEntry(
				companyId, dtoConverterContext, externalReferenceCode,
				_getObjectDefinition(companyId), null));
	}

	@Override
	public Page<AgentDefinition> getAgentDefinitionsPage(
			long companyId, DTOConverterContext dtoConverterContext,
			String filterString, Pagination pagination, String search,
			Sort[] sorts)
		throws Exception {

		Map<String, Map<String, String>> actions = null;

		if (dtoConverterContext != null) {
			actions = HashMapBuilder.<String, Map<String, String>>put(
				"get",
				ActionUtil.addAction(
					ActionKeys.VIEW, AgentDefinitionResourceImpl.class, null,
					"getAgentDefinitionsPage",
					_kaleoDefinitionModelResourcePermission, (Long)null,
					dtoConverterContext.getUriInfo())
			).build();
		}

		Page<ObjectEntry> page = _objectEntryManager.getObjectEntries(
			companyId, _getObjectDefinition(companyId), null, null,
			dtoConverterContext, _getFilterString(filterString), pagination,
			search, sorts);

		return Page.of(
			actions,
			TransformUtil.transform(
				page.getItems(),
				objectEntry -> _toAgentDefinition(
					companyId, dtoConverterContext, objectEntry)),
			pagination, page.getTotalCount());
	}

	@Override
	public AgentDefinition patchAgentDefinitionUpdateActive(
			boolean active, long companyId,
			DTOConverterContext dtoConverterContext,
			String externalReferenceCode)
		throws Exception {

		ObjectEntry objectEntry = _objectEntryManager.partialUpdateObjectEntry(
			companyId, dtoConverterContext, externalReferenceCode,
			_getObjectDefinition(companyId),
			new ObjectEntry() {
				{
					setProperties(() -> Map.of("active", active));
				}
			},
			null);

		WorkflowDefinition workflowDefinition =
			_workflowDefinitionManager.getLatestWorkflowDefinition(
				companyId,
				GetterUtil.getString(
					objectEntry.getPropertyValue("workflowDefinitionName")));

		_workflowDefinitionManager.updateActive(
			active, companyId, workflowDefinition.getName(),
			dtoConverterContext.getUserId(), workflowDefinition.getVersion());

		return _toAgentDefinition(companyId, dtoConverterContext, objectEntry);
	}

	@Override
	public AgentDefinition postAgentDefinitionCopy(
			long companyId, DTOConverterContext dtoConverterContext,
			String externalReferenceCode)
		throws Exception {

		AccountEntry accountEntry = _getUserAccountEntry(dtoConverterContext);

		ObjectDefinition objectDefinition = _getObjectDefinition(companyId);

		ObjectEntry objectEntry = _objectEntryManager.getObjectEntry(
			companyId, dtoConverterContext, externalReferenceCode,
			objectDefinition, null);

		WorkflowDefinition workflowDefinition =
			_workflowDefinitionManager.getLatestWorkflowDefinition(
				companyId,
				GetterUtil.getString(
					objectEntry.getPropertyValue("workflowDefinitionName")));

		String content = workflowDefinition.getContent();

		Locale locale = dtoConverterContext.getLocale();

		String workflowDefinitionName = PortalUUIDUtil.generate();

		WorkflowDefinition copiedWorkflowDefinition =
			_workflowDefinitionManager.deployWorkflowDefinition(
				content.getBytes(), companyId, null,
				accountEntry.getAccountEntryGroupId(), workflowDefinitionName,
				WorkflowDefinitionConstants.SCOPE_AI,
				LanguageUtil.format(
					locale, "copy-of-x",
					workflowDefinition.getTitle(locale.getDisplayLanguage())),
				dtoConverterContext.getUserId());

		Map<String, String> title =
			(Map<String, String>)objectEntry.getPropertyValue("title_i18n");

		title.replaceAll(
			(key, value) -> LanguageUtil.format(locale, "copy-of-x", value));

		ObjectEntry copiedObjectEntry = new ObjectEntry() {
			{
				setProperties(
					() -> Map.of(
						"active",
						GetterUtil.getBoolean(
							objectEntry.getPropertyValue("active")),
						"description",
						GetterUtil.getString(
							objectEntry.getPropertyValue("description")),
						"inputVariables",
						GetterUtil.getString(
							objectEntry.getPropertyValue("inputVariables")),
						"outputVariable",
						GetterUtil.getString(
							objectEntry.getPropertyValue("outputVariable")),
						"r_accountToAIHubAgentDefinitions_accountEntryId",
						accountEntry.getAccountEntryId(), "title_i18n", title,
						"workflowDefinitionName", workflowDefinitionName));
			}
		};

		try {
			copiedObjectEntry = _objectEntryManager.addObjectEntry(
				dtoConverterContext, _getObjectDefinition(companyId),
				copiedObjectEntry, null);
		}
		catch (Exception exception) {
			_undeployWorkflowDefinition(
				dtoConverterContext, copiedWorkflowDefinition);

			throw exception;
		}

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				objectDefinition.getObjectDefinitionId(),
				"agentDefinitionsToContentRetrievers");

		DefaultObjectEntryManager defaultObjectEntryManager =
			(DefaultObjectEntryManager)_objectEntryManager;

		Page<ObjectEntry> objectEntriesPage =
			defaultObjectEntryManager.getRelatedObjectEntries(
				dtoConverterContext, objectEntry.getId(), objectRelationship,
				Pagination.of(QueryUtil.ALL_POS, QueryUtil.ALL_POS));

		if (objectEntriesPage.getTotalCount() != 0) {
			for (ObjectEntry contentRetrieverObjectEntry :
					objectEntriesPage.getItems()) {

				defaultObjectEntryManager.
					addObjectRelationshipMappingTableValues(
						dtoConverterContext, objectRelationship,
						copiedObjectEntry.getId(),
						contentRetrieverObjectEntry.getId());
			}
		}

		return _toAgentDefinition(
			companyId, dtoConverterContext, copiedObjectEntry);
	}

	@Override
	public AgentDefinition postAgentDefinitionDraft(
			long companyId, DTOConverterContext dtoConverterContext)
		throws Exception {

		AccountEntry accountEntry = _getUserAccountEntry(dtoConverterContext);
		String workflowDefinitionContent = _getWorkflowDefinitionContent();
		String workflowDefinitionName = PortalUUIDUtil.generate();

		WorkflowDefinition workflowDefinition =
			_workflowDefinitionManager.deployWorkflowDefinition(
				workflowDefinitionContent.getBytes(), companyId, null,
				accountEntry.getAccountEntryGroupId(), workflowDefinitionName,
				WorkflowDefinitionConstants.SCOPE_AI,
				_language.get(
					dtoConverterContext.getLocale(), "untitled-workflow"),
				dtoConverterContext.getUserId());

		ObjectEntry objectEntry = new ObjectEntry() {
			{
				setProperties(
					() -> Map.of(
						"active", false,
						"r_accountToAIHubAgentDefinitions_accountEntryId",
						accountEntry.getAccountEntryId(),
						"workflowDefinitionName", workflowDefinitionName));
				setStatus(
					() -> new com.liferay.object.rest.dto.v1_0.Status() {
						{
							setCode(() -> WorkflowConstants.STATUS_DRAFT);
						}
					});
			}
		};

		try {
			objectEntry = _objectEntryManager.addObjectEntry(
				dtoConverterContext, _getObjectDefinition(companyId),
				objectEntry, null);
		}
		catch (Exception exception) {
			_undeployWorkflowDefinition(
				dtoConverterContext, workflowDefinition);

			throw exception;
		}

		return _toAgentDefinition(companyId, dtoConverterContext, objectEntry);
	}

	private Map<String, String> _addAction(
		DTOConverterContext dtoConverterContext, String methodName,
		WorkflowDefinition workflowDefinition) {

		if (Objects.equals(
				methodName, "postAgentDefinitionByExternalReferenceCodeCopy")) {

			return ActionUtil.addAction(
				ActionKeys.VIEW, AgentDefinitionResourceImpl.class,
				workflowDefinition.getWorkflowDefinitionId(), methodName,
				_kaleoDefinitionModelResourcePermission, (Long)null,
				dtoConverterContext.getUriInfo());
		}

		if (workflowDefinition.isSystem()) {
			return null;
		}

		return ActionUtil.addAction(
			ActionKeys.ADD_DEFINITION, AgentDefinitionResourceImpl.class,
			workflowDefinition.getWorkflowDefinitionId(), methodName,
			_kaleoDefinitionModelResourcePermission, (Long)null,
			dtoConverterContext.getUriInfo());
	}

	private WorkflowDefinition _fetchLatestWorkflowDefinition(
		long companyId, String workflowDefinitionName) {

		if (Validator.isNull(workflowDefinitionName)) {
			return null;
		}

		try {
			return _workflowDefinitionManager.getLatestWorkflowDefinition(
				companyId, workflowDefinitionName);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to get the latest workflow definition with name " +
						workflowDefinitionName,
					exception);
			}

			return null;
		}
	}

	private String _getFilterString(String filterString) {
		if (Validator.isNull(filterString)) {
			return "externalReferenceCode ne 'L_PAGE_BUILDER'";
		}

		return "(" + filterString +
			") and externalReferenceCode ne 'L_PAGE_BUILDER'";
	}

	private ObjectDefinition _getObjectDefinition(long companyId)
		throws Exception {

		return _objectDefinitionLocalService.getObjectDefinition(
			companyId, "AIHubAgentDefinition");
	}

	private Status _getStatus(
		DTOConverterContext dtoConverterContext, ObjectEntry objectEntry,
		WorkflowDefinition workflowDefinition) {

		if (dtoConverterContext == null) {
			return null;
		}

		Locale locale = dtoConverterContext.getLocale();

		com.liferay.object.rest.dto.v1_0.Status dtoStatus =
			objectEntry.getStatus();

		if ((dtoStatus != null) &&
			(dtoStatus.getCode() == WorkflowConstants.STATUS_DRAFT)) {

			return _toStatus("draft", locale);
		}

		if ((workflowDefinition != null) && workflowDefinition.isActive()) {
			return _toStatus("active", locale);
		}

		return _toStatus("inactive", locale);
	}

	private AccountEntry _getUserAccountEntry(
			DTOConverterContext dtoConverterContext)
		throws Exception {

		AccountEntry accountEntry = AccountEntryUtil.getUserAccountEntry(
			dtoConverterContext.getUserId());

		if (accountEntry == null) {
			throw new BadRequestException(
				"The current user is not associated with an AI Hub account");
		}

		return accountEntry;
	}

	private String _getWorkflowDefinitionContent() throws Exception {
		return StringUtil.read(
			AgentDefinitionManagerImpl.class.getClassLoader(),
			"com/liferay/ai/hub/rest/internal/manager/v1_0/dependencies" +
				"/workflow-definition.xml");
	}

	private boolean _hasAgentDefinitionWithWorkflowDefinitionName(
			long companyId, DTOConverterContext dtoConverterContext,
			String workflowDefinitionName)
		throws Exception {

		if (Validator.isNull(workflowDefinitionName)) {
			return false;
		}

		Page<ObjectEntry> objectEntriesPage =
			_objectEntryManager.getObjectEntries(
				companyId, _getObjectDefinition(companyId), null, null,
				dtoConverterContext,
				"workflowDefinitionName eq '" +
					StringUtil.replace(workflowDefinitionName, '\'', "''") +
						"'",
				Pagination.of(1, 1), null, null);

		if (objectEntriesPage.getTotalCount() > 0) {
			return true;
		}

		return false;
	}

	private AgentDefinition _toAgentDefinition(
			long companyId, DTOConverterContext dtoConverterContext,
			ObjectEntry objectEntry)
		throws PortalException {

		String workflowDefinitionName = GetterUtil.getString(
			objectEntry.getPropertyValue("workflowDefinitionName"));

		WorkflowDefinition workflowDefinition = _fetchLatestWorkflowDefinition(
			companyId, workflowDefinitionName);

		return new AgentDefinition() {
			{
				setActions(
					() -> {
						if ((dtoConverterContext == null) ||
							(workflowDefinition == null)) {

							return null;
						}

						return HashMapBuilder.put(
							"activate",
							() -> {
								if (workflowDefinition.isActive()) {
									return null;
								}

								return _addAction(
									dtoConverterContext,
									"patchAgentDefinitionByExternalReference" +
										"CodeUpdateActive",
									workflowDefinition);
							}
						).put(
							"copy",
							_addAction(
								dtoConverterContext,
								"postAgentDefinitionByExternalReferenceCode" +
									"Copy",
								workflowDefinition)
						).put(
							"deactivate",
							() -> {
								if (!workflowDefinition.isActive()) {
									return null;
								}

								return _addAction(
									dtoConverterContext,
									"patchAgentDefinitionByExternalReference" +
										"CodeUpdateActive",
									workflowDefinition);
							}
						).put(
							"delete",
							() -> _addAction(
								dtoConverterContext,
								"deleteAgentDefinitionByExternalReferenceCode",
								workflowDefinition)
						).put(
							"permissions",
							() -> {
								Map<String, Map<String, String>> actions =
									objectEntry.getActions();

								if (MapUtil.isEmpty(actions)) {
									return null;
								}

								return actions.get("permissions");
							}
						).build();
					});
				setActive(
					() -> GetterUtil.getBoolean(
						objectEntry.getPropertyValue("active")));
				setDescription(
					() -> GetterUtil.getString(
						objectEntry.getPropertyValue("description")));
				setExternalReferenceCode(
					() -> GetterUtil.getString(
						objectEntry.getPropertyValue("externalReferenceCode")));
				setId(
					() -> GetterUtil.getLong(
						objectEntry.getPropertyValue("id")));
				setInputVariables(
					() -> TransformUtil.transform(
						StringUtil.split(
							GetterUtil.getString(
								objectEntry.getPropertyValue(
									"inputVariables"))),
						inputVariable -> _toVariable(inputVariable),
						Variable.class));
				setModel(() -> _toModel(dtoConverterContext, companyId));
				setOutputVariable(
					() -> _toVariable(
						GetterUtil.getString(
							objectEntry.getPropertyValue("outputVariable"))));
				setStatus(
					() -> _getStatus(
						dtoConverterContext, objectEntry, workflowDefinition));
				setSystem(
					() -> GetterUtil.getBoolean(
						objectEntry.getPropertyValue("system")));
				setTitle(
					() -> GetterUtil.getString(
						objectEntry.getPropertyValue("title")));
				setVersion(
					() -> {
						if (workflowDefinition == null) {
							return null;
						}

						return workflowDefinition.getVersion();
					});
				setWorkflowDefinitionName(
					() -> {
						if (workflowDefinition == null) {
							return workflowDefinitionName;
						}

						return workflowDefinition.getName();
					});
			}
		};
	}

	private Model _toModel(
			DTOConverterContext dtoConverterContext, long companyId)
		throws ConfigurationException {

		VertexAIConfiguration vertexAIConfiguration =
			_configurationProvider.getCompanyConfiguration(
				VertexAIConfiguration.class, companyId);

		return new Model() {
			{
				setLabel(
					() -> _language.get(
						dtoConverterContext.getLocale(),
						vertexAIConfiguration.modelName()));
				setName(vertexAIConfiguration::modelName);
				setProviderLabel(
					() -> _language.get(
						dtoConverterContext.getLocale(), "google"));
			}
		};
	}

	private Status _toStatus(String label, Locale locale) {
		Status status = new Status();

		status.setLabel(() -> label);
		status.setLabel_i18n(() -> _language.get(locale, label));

		return status;
	}

	private Variable _toVariable(String variableName) {
		return new Variable() {
			{
				setName(() -> variableName);
				setType(() -> "string");
			}
		};
	}

	private void _undeployWorkflowDefinition(
		DTOConverterContext dtoConverterContext,
		WorkflowDefinition workflowDefinition) {

		try {
			_workflowDefinitionManager.updateActive(
				false, workflowDefinition.getCompanyId(),
				workflowDefinition.getName(), dtoConverterContext.getUserId(),
				workflowDefinition.getVersion());

			_workflowDefinitionManager.undeployWorkflowDefinition(
				workflowDefinition.getCompanyId(), workflowDefinition.getName(),
				dtoConverterContext.getUserId(),
				workflowDefinition.getVersion());
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to undeploy orphaned workflow definition " +
						workflowDefinition.getName(),
					exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AgentDefinitionManagerImpl.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference(
		target = "(model.class.name=com.liferay.portal.workflow.kaleo.model.KaleoDefinition)"
	)
	private ModelResourcePermission<KaleoDefinition>
		_kaleoDefinitionModelResourcePermission;

	@Reference
	private Language _language;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference(target = "(object.entry.manager.storage.type=default)")
	private ObjectEntryManager _objectEntryManager;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference
	private WorkflowDefinitionManager _workflowDefinitionManager;

}