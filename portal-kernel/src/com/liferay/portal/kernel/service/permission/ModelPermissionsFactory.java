/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.permission;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.internal.service.permission.ModelPermissionsImpl;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jorge Ferrer
 */
public class ModelPermissionsFactory {

	public static final String MODEL_PERMISSIONS_PREFIX = "modelPermissions";

	public static final String ROLE_PERMISSIONS_PREFIX = "rolePermissions";

	public static ModelPermissions create(
		HttpServletRequest httpServletRequest) {

		return _createModelPermissions(
			httpServletRequest.getParameterMap(), null);
	}

	public static ModelPermissions create(
		HttpServletRequest httpServletRequest, String className) {

		return _createModelPermissions(
			httpServletRequest.getParameterMap(), className);
	}

	public static ModelPermissions create(
		Map<String, String[]> modelPermissionsParameterMap, String className) {

		if (className == null) {
			className = ModelPermissionsImpl.RESOURCE_NAME_FIRST_RESOURCE;
		}

		ModelPermissions modelPermissions = null;

		for (Map.Entry<String, String[]> entry :
				modelPermissionsParameterMap.entrySet()) {

			try {
				Role role = RoleLocalServiceUtil.getRole(
					CompanyThreadLocal.getCompanyId(), entry.getKey());

				if (modelPermissions == null) {
					modelPermissions = new ModelPermissionsImpl(className);
				}

				modelPermissions.addRolePermissions(
					role.getName(), entry.getValue());
			}
			catch (PortalException portalException) {
				if (_log.isInfoEnabled()) {
					_log.info("Unable to get role " + entry.getKey());
				}

				// LPS-52675

				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}
			}
		}

		return modelPermissions;
	}

	public static ModelPermissions create(PortletRequest portletRequest) {
		return _createModelPermissions(portletRequest.getParameterMap(), null);
	}

	public static ModelPermissions create(
		PortletRequest portletRequest, String className) {

		return _createModelPermissions(
			portletRequest.getParameterMap(), className);
	}

	public static ModelPermissions create(String className) {
		return new ModelPermissionsImpl(className);
	}

	public static ModelPermissions create(
		String[] groupPermissions, String[] guestPermissions) {

		return create(groupPermissions, guestPermissions, null);
	}

	public static ModelPermissions create(
		String[] groupPermissions, String[] guestPermissions,
		String className) {

		if (className == null) {
			className = ModelPermissionsImpl.RESOURCE_NAME_FIRST_RESOURCE;
		}

		ModelPermissions modelPermissions = new ModelPermissionsImpl(className);

		modelPermissions.addRolePermissions(
			RoleConstants.PLACEHOLDER_DEFAULT_GROUP_ROLE, groupPermissions);
		modelPermissions.addRolePermissions(
			RoleConstants.GUEST, guestPermissions);

		return modelPermissions;
	}

	public static ModelPermissions createForAllResources() {
		return new ModelPermissionsImpl(
			ModelPermissionsImpl.RESOURCE_NAME_ALL_RESOURCES);
	}

	public static ModelPermissions createWithDefaultPermissions(
		String className) {

		if (className == null) {
			throw new NullPointerException("Class name is null");
		}

		ModelPermissions modelPermissions = new ModelPermissionsImpl(className);

		List<String> modelResourceGroupDefaultActions =
			ResourceActionsUtil.getModelResourceGroupDefaultActions(className);

		modelPermissions.addRolePermissions(
			RoleConstants.PLACEHOLDER_DEFAULT_GROUP_ROLE,
			modelResourceGroupDefaultActions.toArray(new String[0]));

		List<String> modelResourceGuestDefaultActions =
			ResourceActionsUtil.getModelResourceGuestDefaultActions(className);

		modelPermissions.addRolePermissions(
			RoleConstants.GUEST,
			modelResourceGuestDefaultActions.toArray(new String[0]));

		return modelPermissions;
	}

	private static String _addClassNamePostfix(
		String parameterName, String className) {

		if (Validator.isNull(className)) {
			return parameterName;
		}

		return parameterName + StringPool.UNDERLINE + className;
	}

	private static void _addRolePermissions(
		ModelPermissions modelPermissions, Map<String, String[]> parameterMap) {

		boolean inputPermissionsShowAllRoles = MapUtil.getBoolean(
			parameterMap, "inputPermissionsShowAllRoles");

		if (inputPermissionsShowAllRoles) {
			for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
				String key = entry.getKey();

				if (!key.startsWith(ROLE_PERMISSIONS_PREFIX)) {
					continue;
				}

				String[] keyTokens = key.split(StringPool.UNDERLINE);

				if (keyTokens.length != 2) {
					continue;
				}

				try {
					Role role = RoleServiceUtil.fetchRole(
						GetterUtil.getLong(keyTokens[1]));

					if (role == null) {
						continue;
					}

					modelPermissions.addRolePermissions(
						role.getName(), entry.getValue());
				}
				catch (PortalException portalException) {
					if (_log.isInfoEnabled()) {
						_log.info("Unable to get role " + keyTokens[1]);
					}

					if (_log.isDebugEnabled()) {
						_log.debug(portalException);
					}
				}
			}
		}
	}

	private static ModelPermissions _createModelPermissions(
		Map<String, String[]> parameterMap, String className) {

		Map<String, String[]> modelPermissionsParameterMap =
			_getModelPermissionsParameterMap(parameterMap, className);

		if (!modelPermissionsParameterMap.isEmpty()) {
			return create(modelPermissionsParameterMap, className);
		}

		String[] groupPermissions = parameterMap.get(
			_addClassNamePostfix("groupPermissions", className));
		String[] guestPermissions = parameterMap.get(
			_addClassNamePostfix("guestPermissions", className));
		String inputPermissionsViewRole = MapUtil.getString(
			parameterMap, "inputPermissionsViewRole");

		if ((groupPermissions != null) || (guestPermissions != null) ||
			Validator.isNotNull(inputPermissionsViewRole)) {

			ModelPermissions modelPermissions = create(
				groupPermissions, guestPermissions, className);

			_addRolePermissions(modelPermissions, parameterMap);

			return modelPermissions;
		}

		if (Validator.isNull(className)) {
			return null;
		}

		return createWithDefaultPermissions(className);
	}

	private static Map<String, String[]> _getModelPermissionsParameterMap(
		Map<String, String[]> parameterMap, String className) {

		Map<String, String[]> modelPermissionsParameterMap = new HashMap<>();

		for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
			String parameterName = entry.getKey();

			if (!parameterName.startsWith(MODEL_PERMISSIONS_PREFIX)) {
				continue;
			}

			parameterName = parameterName.substring(
				MODEL_PERMISSIONS_PREFIX.length());

			if (Validator.isNotNull(className)) {
				if (!parameterName.startsWith(className)) {
					continue;
				}

				parameterName = parameterName.substring(className.length());
			}

			modelPermissionsParameterMap.put(parameterName, entry.getValue());
		}

		return modelPermissionsParameterMap;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ModelPermissionsFactory.class);

}