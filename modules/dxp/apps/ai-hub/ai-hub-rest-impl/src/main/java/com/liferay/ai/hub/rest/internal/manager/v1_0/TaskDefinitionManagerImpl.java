/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.internal.manager.v1_0;

import com.liferay.ai.hub.rest.dto.v1_0.TagsList;
import com.liferay.ai.hub.rest.dto.v1_0.TaskDefinition;
import com.liferay.ai.hub.rest.internal.resource.v1_0.TaskDefinitionResourceImpl;
import com.liferay.ai.hub.rest.manager.v1_0.TaskDefinitionManager;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.ActionUtil;
import com.liferay.portal.vulcan.util.SearchUtil;
import com.liferay.portal.workflow.constants.WorkflowDefinitionConstants;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Feliphe Marinho
 * @author João Victor Alves
 */
@Component(service = TaskDefinitionManager.class)
public class TaskDefinitionManagerImpl implements TaskDefinitionManager {

	@Override
	public void deleteTaskDefinition(
			DTOConverterContext dtoConverterContext, long taskDefinitionId)
		throws Exception {

		WorkflowDefinition workflowDefinition =
			_workflowDefinitionManager.getWorkflowDefinition(taskDefinitionId);

		_workflowDefinitionManager.updateActive(
			workflowDefinition.getCompanyId(), dtoConverterContext.getUserId(),
			workflowDefinition.getName(), workflowDefinition.getVersion(),
			false);

		_workflowDefinitionManager.undeployWorkflowDefinition(
			workflowDefinition.getCompanyId(), dtoConverterContext.getUserId(),
			workflowDefinition.getName(), workflowDefinition.getVersion());
	}

	@Override
	public Page<TaskDefinition> getTaskDefinitions(
			long companyId, DTOConverterContext dtoConverterContext,
			Filter filter, Pagination pagination, String search, Sort[] sorts)
		throws Exception {

		Map<String, Map<String, String>> actions = null;

		if (dtoConverterContext != null) {
			actions = HashMapBuilder.<String, Map<String, String>>put(
				"get",
				ActionUtil.addAction(
					ActionKeys.VIEW, TaskDefinitionResourceImpl.class, null,
					"getTaskDefinitionsPage",
					_kaleoDefinitionModelResourcePermission, (Long)null,
					dtoConverterContext.getUriInfo())
			).build();
		}

		return SearchUtil.search(
			actions,
			booleanQuery -> {
				BooleanFilter booleanFilter =
					booleanQuery.getPreBooleanFilter();

				booleanFilter.addRequiredTerm("latest", Boolean.TRUE);
				booleanFilter.addRequiredTerm(
					"scope", WorkflowDefinitionConstants.SCOPE_AI);
			},
			filter, KaleoDefinitionVersion.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.NAME, Field.VERSION),
			searchContext -> searchContext.setCompanyId(companyId), sorts,
			document -> _toTaskDefinition(
				dtoConverterContext,
				_workflowDefinitionManager.getWorkflowDefinition(
					companyId, document.get(Field.NAME),
					GetterUtil.getInteger(document.get(Field.VERSION)))));
	}

	@Override
	public TaskDefinition patchTaskDefinitionUpdateActive(
			boolean active, DTOConverterContext dtoConverterContext,
			long taskDefinitionId)
		throws Exception {

		WorkflowDefinition workflowDefinition =
			_workflowDefinitionManager.getWorkflowDefinition(taskDefinitionId);

		workflowDefinition = _workflowDefinitionManager.updateActive(
			workflowDefinition.getCompanyId(), dtoConverterContext.getUserId(),
			workflowDefinition.getName(), workflowDefinition.getVersion(),
			active);

		return _toTaskDefinition(dtoConverterContext, workflowDefinition);
	}

	@Override
	public TaskDefinition postTaskDefinitionCopy(
			DTOConverterContext dtoConverterContext, long taskDefinitionId)
		throws Exception {

		WorkflowDefinition workflowDefinition =
			_workflowDefinitionManager.getWorkflowDefinition(taskDefinitionId);

		String content = workflowDefinition.getContent();

		Locale locale = dtoConverterContext.getLocale();

		workflowDefinition =
			_workflowDefinitionManager.deployWorkflowDefinition(
				null, workflowDefinition.getCompanyId(),
				workflowDefinition.getUserId(),
				LanguageUtil.format(
					locale, "copy-of-x",
					workflowDefinition.getTitle(locale.getDisplayLanguage())),
				StringUtil.randomString(), WorkflowDefinitionConstants.SCOPE_AI,
				content.getBytes());

		return _toTaskDefinition(dtoConverterContext, workflowDefinition);
	}

	private Map<String, String> _addAction(
		DTOConverterContext dtoConverterContext, String methodName,
		WorkflowDefinition workflowDefinition) {

		if (!Objects.equals(methodName, "postTaskDefinitionCopy") &&
			workflowDefinition.isSystem()) {

			return null;
		}

		return ActionUtil.addAction(
			ActionKeys.ADD_DEFINITION, TaskDefinitionResourceImpl.class,
			workflowDefinition.getWorkflowDefinitionId(), methodName,
			_kaleoDefinitionModelResourcePermission, (Long)null,
			dtoConverterContext.getUriInfo());
	}

	private TagsList[] _getTaskDefinitionTagsList(
		DTOConverterContext dtoConverterContext,
		WorkflowDefinition workflowDefinition) {

		List<TagsList> tagList = new ArrayList<>();

		Locale locale = dtoConverterContext.getLocale();

		tagList.add(
			new TagsList() {
				{
					if (workflowDefinition.isActive()) {
						setDisplayType("success");
						setLabel(LanguageUtil.get(locale, "active"));
					}
					else {
						setDisplayType("danger");
						setLabel(LanguageUtil.get(locale, "inactive"));
					}
				}
			});

		if (workflowDefinition.isSystem()) {
			tagList.add(
				new TagsList() {
					{
						setDisplayType("info");
						setLabel(LanguageUtil.get(locale, "system"));
					}
				});
		}

		return tagList.toArray(new TagsList[0]);
	}

	private TaskDefinition _toTaskDefinition(
			DTOConverterContext dtoConverterContext,
			WorkflowDefinition workflowDefinition)
		throws PortalException {

		return new TaskDefinition() {
			{
				if (dtoConverterContext != null) {
					setActions(
						() -> HashMapBuilder.put(
							"activate",
							() -> {
								if (workflowDefinition.isActive()) {
									return null;
								}

								return _addAction(
									dtoConverterContext,
									"patchTaskDefinitionUpdateActive",
									workflowDefinition);
							}
						).put(
							"copy",
							_addAction(
								dtoConverterContext, "postTaskDefinitionCopy",
								workflowDefinition)
						).put(
							"deactivate",
							() -> {
								if (!workflowDefinition.isActive()) {
									return null;
								}

								return _addAction(
									dtoConverterContext,
									"patchTaskDefinitionUpdateActive",
									workflowDefinition);
							}
						).put(
							"delete",
							() -> _addAction(
								dtoConverterContext, "deleteTaskDefinition",
								workflowDefinition)
						).build());
				}

				setActive(workflowDefinition::isActive);
				setDescription(workflowDefinition::getDescription);
				setExternalReferenceCode(
					workflowDefinition::getExternalReferenceCode);
				setId(workflowDefinition::getWorkflowDefinitionId);
				setName(workflowDefinition::getName);
				setTagsList(
					_getTaskDefinitionTagsList(
						dtoConverterContext, workflowDefinition));
				setTitle(
					() -> {
						if (dtoConverterContext == null) {
							return workflowDefinition.getTitle();
						}

						Locale locale = dtoConverterContext.getLocale();

						return workflowDefinition.getTitle(
							locale.getDisplayLanguage());
					});
				setVersion(workflowDefinition::getVersion);
			}
		};
	}

	@Reference(
		target = "(model.class.name=com.liferay.portal.workflow.kaleo.model.KaleoDefinition)"
	)
	private ModelResourcePermission<KaleoDefinition>
		_kaleoDefinitionModelResourcePermission;

	@Reference
	private WorkflowDefinitionManager _workflowDefinitionManager;

}