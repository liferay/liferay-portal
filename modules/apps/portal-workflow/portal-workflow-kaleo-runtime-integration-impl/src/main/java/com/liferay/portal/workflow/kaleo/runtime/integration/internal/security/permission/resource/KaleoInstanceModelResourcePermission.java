/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.runtime.integration.internal.security.permission.resource;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowInstance;
import com.liferay.portal.kernel.workflow.permission.WorkflowPermissionUtil;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;
import com.liferay.portal.workflow.kaleo.service.KaleoInstanceLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(
	property = "model.class.name=com.liferay.portal.workflow.kaleo.model.KaleoInstance",
	service = ModelResourcePermission.class
)
public class KaleoInstanceModelResourcePermission
	implements ModelResourcePermission<KaleoInstance> {

	@Override
	public void check(
			PermissionChecker permissionChecker, KaleoInstance kaleoInstance,
			String actionId)
		throws PortalException {

		if (!contains(permissionChecker, kaleoInstance, actionId)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, WorkflowInstance.class.getName(),
				kaleoInstance.getKaleoInstanceId(), actionId);
		}
	}

	@Override
	public void check(
			PermissionChecker permissionChecker, long primaryKey,
			String actionId)
		throws PortalException {

		check(
			permissionChecker,
			_kaleoInstanceLocalService.getKaleoInstance(primaryKey), actionId);
	}

	@Override
	public boolean contains(
			PermissionChecker permissionChecker, KaleoInstance kaleoInstance,
			String actionId)
		throws PortalException {

		return GetterUtil.getBoolean(
			WorkflowPermissionUtil.hasPermission(
				permissionChecker, kaleoInstance.getGroupId(),
				kaleoInstance.getClassName(), kaleoInstance.getClassPK(),
				actionId));
	}

	@Override
	public boolean contains(
			PermissionChecker permissionChecker, long primaryKey,
			String actionId)
		throws PortalException {

		return contains(
			permissionChecker,
			_kaleoInstanceLocalService.getKaleoInstance(primaryKey), actionId);
	}

	@Override
	public String getModelName() {
		return KaleoInstance.class.getName();
	}

	@Override
	public PortletResourcePermission getPortletResourcePermission() {
		return _portletResourcePermission;
	}

	@Reference
	private KaleoInstanceLocalService _kaleoInstanceLocalService;

	@Reference(
		target = "(resource.name=" + WorkflowConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}