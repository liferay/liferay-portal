/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.internal.resource.v1_0;

import com.liferay.headless.admin.workflow.dto.v1_0.WorkflowDefinitionLink;
import com.liferay.headless.admin.workflow.resource.v1_0.WorkflowDefinitionLinkResource;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionRegistryUtil;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Victor Kammerer
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/workflow-definition-link.properties",
	scope = ServiceScope.PROTOTYPE,
	service = WorkflowDefinitionLinkResource.class
)
public class WorkflowDefinitionLinkResourceImpl
	extends BaseWorkflowDefinitionLinkResourceImpl {

	@Override
	public Page<WorkflowDefinitionLink>
			getWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLinksPage(
				String externalReferenceCode, Pagination pagination)
		throws Exception {

		WorkflowDefinition workflowDefinition =
			_workflowDefinitionManager.getWorkflowDefinition(
				externalReferenceCode, contextCompany.getCompanyId());

		List<com.liferay.portal.kernel.model.WorkflowDefinitionLink>
			workflowDefinitionLinks =
				_workflowDefinitionLinkService.getWorkflowDefinitionLinks(
					contextCompany.getCompanyId(), workflowDefinition.getName(),
					workflowDefinition.getVersion());

		return Page.of(
			transform(
				ListUtil.subList(
					workflowDefinitionLinks, pagination.getStartPosition(),
					pagination.getEndPosition()),
				workflowDefinitionLink -> _toWorkflowDefinitionLink(
					workflowDefinitionLink)),
			pagination, workflowDefinitionLinks.size());
	}

	@Override
	public Page<WorkflowDefinitionLink>
			getWorkflowDefinitionWorkflowDefinitionLinksPage(
				Long workflowDefinitionId, Pagination pagination)
		throws Exception {

		WorkflowDefinition workflowDefinition =
			_workflowDefinitionManager.getWorkflowDefinition(
				workflowDefinitionId);

		List<com.liferay.portal.kernel.model.WorkflowDefinitionLink>
			workflowDefinitionLinks =
				_workflowDefinitionLinkService.getWorkflowDefinitionLinks(
					contextCompany.getCompanyId(), workflowDefinition.getName(),
					workflowDefinition.getVersion());

		return Page.of(
			HashMapBuilder.put(
				"createBatch",
				addAction(
					ActionKeys.ADD_DEFINITION, workflowDefinitionId,
					"postWorkflowDefinitionWorkflowDefinitionLinkBatch",
					ModelResourcePermissionRegistryUtil.
						getModelResourcePermission(
							"com.liferay.portal.workflow.kaleo.model." +
								"KaleoDefinition"))
			).build(),
			transform(
				ListUtil.subList(
					workflowDefinitionLinks, pagination.getStartPosition(),
					pagination.getEndPosition()),
				workflowDefinitionLink -> _toWorkflowDefinitionLink(
					workflowDefinitionLink)),
			pagination, workflowDefinitionLinks.size());
	}

	@Override
	public WorkflowDefinitionLink
			postWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLink(
				String externalReferenceCode,
				WorkflowDefinitionLink workflowDefinitionLink)
		throws Exception {

		WorkflowDefinition workflowDefinition =
			_workflowDefinitionManager.getWorkflowDefinition(
				externalReferenceCode, contextCompany.getCompanyId());

		return _toWorkflowDefinitionLink(
			_workflowDefinitionLinkService.addWorkflowDefinitionLink(
				contextUser.getUserId(), contextCompany.getCompanyId(),
				workflowDefinitionLink.getGroupId(),
				workflowDefinitionLink.getClassName(), 0, 0,
				workflowDefinition.getName(), workflowDefinition.getVersion()));
	}

	@Override
	public WorkflowDefinitionLink postWorkflowDefinitionWorkflowDefinitionLink(
			Long workflowDefinitionId,
			WorkflowDefinitionLink workflowDefinitionLink)
		throws Exception {

		WorkflowDefinition workflowDefinition =
			_workflowDefinitionManager.getWorkflowDefinition(
				workflowDefinitionId);

		return _toWorkflowDefinitionLink(
			_workflowDefinitionLinkService.addWorkflowDefinitionLink(
				contextUser.getUserId(), contextCompany.getCompanyId(),
				workflowDefinitionLink.getGroupId(),
				workflowDefinitionLink.getClassName(), 0, 0,
				workflowDefinition.getName(), workflowDefinition.getVersion()));
	}

	@Override
	public WorkflowDefinitionLink
			putWorkflowDefinitionLinkByExternalReferenceCode(
				String externalReferenceCode,
				WorkflowDefinitionLink workflowDefinitionLink)
		throws Exception {

		return _toWorkflowDefinitionLink(
			_workflowDefinitionLinkService.updateWorkflowDefinitionLink(
				externalReferenceCode, contextUser.getUserId(),
				contextCompany.getCompanyId(),
				workflowDefinitionLink.getGroupId(),
				workflowDefinitionLink.getClassName(), 0, 0,
				workflowDefinitionLink.getWorkflowDefinitionName(),
				workflowDefinitionLink.getWorkflowDefinitionVersion()));
	}

	private WorkflowDefinitionLink _toWorkflowDefinitionLink(
			com.liferay.portal.kernel.model.WorkflowDefinitionLink
				serviceBuilderWorkflowDefinitionLink)
		throws PortalException {

		Group group;

		if (serviceBuilderWorkflowDefinitionLink.getGroupId() != 0) {
			group = _groupService.getGroup(
				serviceBuilderWorkflowDefinitionLink.getGroupId());
		}
		else {
			group = null;
		}

		return new WorkflowDefinitionLink() {
			{
				setClassName(
					serviceBuilderWorkflowDefinitionLink::getClassName);
				setExternalReferenceCode(
					serviceBuilderWorkflowDefinitionLink::
						getExternalReferenceCode);
				setGroupExternalReferenceCode(
					() -> (group != null) ? group.getExternalReferenceCode() :
						StringPool.BLANK);
				setGroupId(
					() -> (group != null) ? group.getGroupId() :
						serviceBuilderWorkflowDefinitionLink.getGroupId());
				setId(
					serviceBuilderWorkflowDefinitionLink::
						getWorkflowDefinitionLinkId);
				setWorkflowDefinitionName(
					serviceBuilderWorkflowDefinitionLink::
						getWorkflowDefinitionName);
				setWorkflowDefinitionVersion(
					serviceBuilderWorkflowDefinitionLink::
						getWorkflowDefinitionVersion);
			}
		};
	}

	@Reference
	private GroupService _groupService;

	@Reference
	private WorkflowDefinitionLinkService _workflowDefinitionLinkService;

	@Reference
	private WorkflowDefinitionManager _workflowDefinitionManager;

}