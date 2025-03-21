/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.defaultpermissions.web.internal.portlet.action;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.defaultpermissions.configuration.manager.PortalDefaultPermissionsConfigurationManager;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionParameters;
import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(
	property = {
		"jakarta.portlet.name=com_liferay_portlet_configuration_web_portlet_PortletConfigurationPortlet",
		"mvc.command.name=/configuration/edit_portal_default_permissions_configuration"
	},
	service = MVCActionCommand.class
)
public class EditPortalDefaultPermissionsConfigurationMVCActionCommand
	extends BaseMVCActionCommand {

	public static final String ACTION_SEPARATOR = "_ACTION_";

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		hideDefaultErrorMessage(actionRequest);
		hideDefaultSuccessMessage(actionRequest);

		try {
			String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

			String modelResource = ParamUtil.getString(
				actionRequest, "modelResource");
			String scope = ParamUtil.getString(actionRequest, "scope");

			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			if (cmd.equals("reset")) {
				if (!scope.equals(
						ExtendedObjectClassDefinition.Scope.GROUP.toString())) {

					return;
				}

				Map<String, Map<String, String[]>> defaultPermissions =
					_groupPortalDefaultPermissionsConfigurationManager.
						getDefaultPermissions(
							themeDisplay.getCompanyId(),
							themeDisplay.getSiteGroupId());

				defaultPermissions.remove(modelResource);

				_groupPortalDefaultPermissionsConfigurationManager.
					saveDefaultPermissions(
						themeDisplay.getSiteGroupId(), defaultPermissions);

				jsonObject.put("success", true);
			}
			else {
				Map<String, Map<String, String[]>> defaultPermissions = null;

				if (scope.equals(
						ExtendedObjectClassDefinition.Scope.GROUP.toString())) {

					defaultPermissions =
						_groupPortalDefaultPermissionsConfigurationManager.
							getDefaultPermissions(
								themeDisplay.getCompanyId(),
								themeDisplay.getSiteGroupId());
				}
				else {
					defaultPermissions =
						_companyPortalDefaultPermissionsConfigurationManager.
							getDefaultPermissions(
								themeDisplay.getCompanyId(),
								themeDisplay.getSiteGroupId());
				}

				Map<String, String[]> resourceDefaultPermissions =
					defaultPermissions.get(modelResource);

				if (resourceDefaultPermissions == null) {
					resourceDefaultPermissions = new HashMap<>();
				}

				for (long roleId :
						StringUtil.split(
							ParamUtil.getString(
								actionRequest,
								"rolesSearchContainerPrimaryKeys"),
							0L)) {

					Role role = _roleLocalService.fetchRole(roleId);

					if (role == null) {
						continue;
					}

					resourceDefaultPermissions.put(
						role.getName(),
						ArrayUtil.toStringArray(
							_getCheckedActionIds(
								actionRequest, roleId,
								value -> !Objects.equals(
									value, "indeterminate"))));
				}

				defaultPermissions.put(
					modelResource, resourceDefaultPermissions);

				if (scope.equals(
						ExtendedObjectClassDefinition.Scope.GROUP.toString())) {

					_groupPortalDefaultPermissionsConfigurationManager.
						saveDefaultPermissions(
							themeDisplay.getSiteGroupId(), defaultPermissions);
				}
				else {
					_companyPortalDefaultPermissionsConfigurationManager.
						saveDefaultPermissions(
							themeDisplay.getCompanyId(), defaultPermissions);
				}

				jsonObject.put("success", true);
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			jsonObject.put("success", false);
		}

		JSONPortletResponseUtil.writeJSON(
			actionRequest, actionResponse, jsonObject);
	}

	private List<String> _getCheckedActionIds(
		ActionRequest actionRequest, long roleId,
		Predicate<String> valuePredicate) {

		List<String> actionIds = new ArrayList<>();

		ActionParameters actionParameters = actionRequest.getActionParameters();

		for (String name : actionParameters.getNames()) {
			if (!name.startsWith(roleId + ACTION_SEPARATOR)) {
				continue;
			}

			if (valuePredicate.test(actionParameters.getValue(name))) {
				int pos = name.indexOf(ACTION_SEPARATOR);

				String actionId = name.substring(
					pos + ACTION_SEPARATOR.length());

				actionIds.add(actionId);
			}
		}

		return actionIds;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditPortalDefaultPermissionsConfigurationMVCActionCommand.class);

	@Reference(target = "(portal.default.permissions.scope=company)")
	private PortalDefaultPermissionsConfigurationManager
		_companyPortalDefaultPermissionsConfigurationManager;

	@Reference(target = "(portal.default.permissions.scope=group)")
	private PortalDefaultPermissionsConfigurationManager
		_groupPortalDefaultPermissionsConfigurationManager;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private RoleLocalService _roleLocalService;

}