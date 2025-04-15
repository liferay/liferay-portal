/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.permission.propagator;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourcePermissionServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Hugo Huijser
 */
public abstract class BasePermissionPropagator implements PermissionPropagator {

	protected Set<String> getActionIds(String className) {
		List<String> actionIds = ResourceActionsUtil.getModelResourceActions(
			className);

		return SetUtil.fromCollection(actionIds);
	}

	protected Set<String> getAvailableActionIds(
			long companyId, String className, long primKey, long roleId,
			Set<String> actionIds)
		throws PortalException {

		List<String> availableActionIds =
			ResourcePermissionLocalServiceUtil.
				getAvailableResourcePermissionActionIds(
					companyId, className, ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(primKey), roleId, actionIds);

		return SetUtil.fromCollection(availableActionIds);
	}

	protected void propagateRolePermissions(
			ActionRequest actionRequest, long roleId, String parentClassName,
			long parentPrimKey, String childClassName, long childPrimKey)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Set<String> parentActionIds = getActionIds(parentClassName);
		Set<String> childActionIds = getActionIds(childClassName);

		Set<String> parentAndChildCommonActionIds = new HashSet<>();

		for (String actionId : childActionIds) {
			if (parentActionIds.contains(actionId)) {
				parentAndChildCommonActionIds.add(actionId);
			}
		}

		Set<String> parentAvailableActionIds = getAvailableActionIds(
			themeDisplay.getCompanyId(), parentClassName, parentPrimKey, roleId,
			parentActionIds);
		Set<String> childAvailableActionIds = getAvailableActionIds(
			themeDisplay.getCompanyId(), childClassName, childPrimKey, roleId,
			childActionIds);

		List<String> actionIds = new ArrayList<>();

		for (String actionId : parentAndChildCommonActionIds) {
			if (parentAvailableActionIds.contains(actionId)) {
				actionIds.add(actionId);
			}
		}

		for (String actionId : childAvailableActionIds) {
			if (!parentAndChildCommonActionIds.contains(actionId)) {
				actionIds.add(actionId);
			}
		}

		ResourcePermissionServiceUtil.setIndividualResourcePermissions(
			themeDisplay.getScopeGroupId(), themeDisplay.getCompanyId(),
			childClassName, String.valueOf(childPrimKey), roleId,
			actionIds.toArray(new String[0]));
	}

}