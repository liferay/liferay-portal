/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionLocalService;
import com.liferay.portal.workflow.kaleo.service.base.KaleoDefinitionServiceBaseImpl;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Nathaly Gomes
 */
@Component(
	property = {
		"json.web.service.context.name=kaleo",
		"json.web.service.context.path=KaleoDefinition"
	},
	service = AopService.class
)
public class KaleoDefinitionServiceImpl extends KaleoDefinitionServiceBaseImpl {

	@Override
	public KaleoDefinition addKaleoDefinition(
			String externalReferenceCode, String name, String title,
			String description, String content, String scope, int version,
			ServiceContext serviceContext)
		throws PortalException {

		_checkPermissions(serviceContext);

		return _kaleoDefinitionLocalService.addKaleoDefinition(
			externalReferenceCode, name, title, description, content, scope,
			version, serviceContext);
	}

	@Override
	public KaleoDefinition getKaleoDefinition(long kaleoDefinitionId)
		throws PortalException {

		KaleoDefinition kaleoDefinition =
			kaleoDefinitionPersistence.findByPrimaryKey(kaleoDefinitionId);

		_kaleoDefinitionModelResourcePermission.check(
			getPermissionChecker(), kaleoDefinition, ActionKeys.VIEW);

		return kaleoDefinition;
	}

	@Override
	public KaleoDefinition getKaleoDefinition(
			String externalReferenceCode, long companyId)
		throws PortalException {

		KaleoDefinition kaleoDefinition =
			_kaleoDefinitionLocalService.
				getKaleoDefinitionByExternalReferenceCode(
					externalReferenceCode, companyId);

		_kaleoDefinitionModelResourcePermission.check(
			getPermissionChecker(), kaleoDefinition, ActionKeys.VIEW);

		return kaleoDefinition;
	}

	@Override
	public KaleoDefinition getKaleoDefinition(
			String name, ServiceContext serviceContext)
		throws PortalException {

		KaleoDefinition kaleoDefinition = kaleoDefinitionPersistence.findByC_N(
			serviceContext.getCompanyId(), name);

		_kaleoDefinitionModelResourcePermission.check(
			getPermissionChecker(), kaleoDefinition, ActionKeys.VIEW);

		return kaleoDefinition;
	}

	@Override
	public List<KaleoDefinition> getScopeKaleoDefinitions(
			String scope, boolean active, int start, int end,
			OrderByComparator<KaleoDefinition> orderByComparator,
			ServiceContext serviceContext)
		throws PortalException {

		_kaleoDefinitionModelResourcePermission.check(
			getPermissionChecker(), null, ActionKeys.VIEW);

		return _kaleoDefinitionLocalService.getScopeKaleoDefinitions(
			scope, active, start, end, orderByComparator, serviceContext);
	}

	@Override
	public List<KaleoDefinition> getScopeKaleoDefinitions(
			String scope, int start, int end,
			OrderByComparator<KaleoDefinition> orderByComparator,
			ServiceContext serviceContext)
		throws PortalException {

		_kaleoDefinitionModelResourcePermission.check(
			getPermissionChecker(), null, ActionKeys.VIEW);

		return _kaleoDefinitionLocalService.getScopeKaleoDefinitions(
			scope, start, end, orderByComparator, serviceContext);
	}

	@Override
	public KaleoDefinition updateKaleoDefinition(
			String externalReferenceCode, long kaleoDefinitionId, String title,
			String description, String content, ServiceContext serviceContext)
		throws PortalException {

		_checkPermissions(serviceContext);

		return _kaleoDefinitionLocalService.updatedKaleoDefinition(
			externalReferenceCode, kaleoDefinitionId, title, description,
			content, serviceContext);
	}

	private void _checkPermissions(ServiceContext serviceContext)
		throws PrincipalException {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if ((permissionChecker == null) ||
			!GetterUtil.getBoolean(
				serviceContext.getAttribute("checkPermission"), true)) {

			return;
		}

		_portletResourcePermission.check(
			permissionChecker, serviceContext.getScopeGroupId(),
			ActionKeys.ADD_DEFINITION);
	}

	@Reference
	private KaleoDefinitionLocalService _kaleoDefinitionLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.portal.workflow.kaleo.model.KaleoDefinition)"
	)
	private ModelResourcePermission<KaleoDefinition>
		_kaleoDefinitionModelResourcePermission;

	@Reference(
		target = "(resource.name=" + WorkflowConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}