/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.runtime.integration.internal.security.permission.resource;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.workflow.configuration.WorkflowDefinitionConfiguration;
import com.liferay.portal.workflow.constants.WorkflowActionKeys;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(
	configurationPid = "com.liferay.portal.workflow.configuration.WorkflowDefinitionConfiguration",
	property = "resource.name=" + WorkflowConstants.RESOURCE_NAME,
	service = PortletResourcePermission.class
)
public class WorkflowPortletResourcePermission
	implements PortletResourcePermission {

	@Override
	public void check(
			PermissionChecker permissionChecker, Group group, String actionId)
		throws PrincipalException {

		check(permissionChecker, group.getGroupId(), actionId);
	}

	@Override
	public void check(
			PermissionChecker permissionChecker, long groupId, String actionId)
		throws PrincipalException {

		if (!contains(permissionChecker, groupId, actionId)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, WorkflowConstants.RESOURCE_NAME, 0,
				actionId);
		}
	}

	@Override
	public boolean contains(
		PermissionChecker permissionChecker, Group group, String actionId) {

		return contains(permissionChecker, group.getGroupId(), actionId);
	}

	@Override
	public boolean contains(
		PermissionChecker permissionChecker, long groupId, String actionId) {

		try {
			if (Objects.equals(
					actionId, WorkflowActionKeys.START_WORKFLOW_INSTANCE)) {

				return permissionChecker.hasPermission(
					groupId, WorkflowConstants.RESOURCE_NAME, groupId,
					actionId);
			}

			return _contains(permissionChecker, groupId);
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return false;
		}
	}

	@Override
	public String getResourceName() {
		return WorkflowConstants.RESOURCE_NAME;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		WorkflowDefinitionConfiguration workflowDefinitionConfiguration =
			ConfigurableUtil.createConfigurable(
				WorkflowDefinitionConfiguration.class, properties);

		_companyAdministratorCanPublish =
			workflowDefinitionConfiguration.companyAdministratorCanPublish();
	}

	private boolean _contains(PermissionChecker permissionChecker, long groupId)
		throws PortalException {

		if (permissionChecker.isOmniadmin() ||
			(_companyAdministratorCanPublish &&
			 permissionChecker.isCompanyAdmin())) {

			return true;
		}

		if (groupId == 0) {
			return false;
		}

		Group group = _groupLocalService.fetchGroup(groupId);

		AccountEntry accountEntry =
			_accountEntryLocalService.fetchUserAccountEntry(
				permissionChecker.getUserId(), group.getClassPK());

		if (accountEntry == null) {
			return false;
		}

		return !Objects.equals(
			accountEntry.getExternalReferenceCode(), "L_AI_HUB");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		WorkflowPortletResourcePermission.class);

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	private volatile boolean _companyAdministratorCanPublish;

	@Reference
	private GroupLocalService _groupLocalService;

}