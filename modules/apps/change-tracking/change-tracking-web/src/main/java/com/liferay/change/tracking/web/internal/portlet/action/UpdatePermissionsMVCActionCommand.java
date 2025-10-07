/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.portlet.action;

import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.web.internal.configuration.helper.CTSettingsConfigurationHelper;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gislayne Vitorino
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CTPortletKeys.PUBLICATIONS,
		"mvc.command.name=/change_tracking/update_permissions"
	},
	service = MVCActionCommand.class
)
public class UpdatePermissionsMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		Map<String, List<String>> permissions =
			(Map<String, List<String>>)_jsonFactory.looseDeserialize(
				ParamUtil.getString(actionRequest, "permissions"));

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setProductionModeWithSafeCloseable()) {

			_ctSettingsConfigurationHelper.save(
				themeDisplay.getCompanyId(),
				HashMapBuilder.<String, Object>put(
					"defaultOwnerActionIds",
					() -> {
						Role role = _roleLocalService.getRole(
							themeDisplay.getCompanyId(), RoleConstants.OWNER);

						List<String> ownerActionIds = permissions.remove(
							String.valueOf(role.getRoleId()));

						return ArrayUtil.toStringArray(ownerActionIds);
					}
				).build());

			for (Map.Entry<String, List<String>> entry :
					permissions.entrySet()) {

				_resourcePermissionLocalService.setResourcePermissions(
					themeDisplay.getCompanyId(), CTCollection.class.getName(),
					ResourceConstants.SCOPE_COMPANY,
					String.valueOf(themeDisplay.getCompanyId()),
					GetterUtil.getLong(entry.getKey()),
					ArrayUtil.toStringArray(entry.getValue()));
			}
		}
	}

	@Reference
	private CTSettingsConfigurationHelper _ctSettingsConfigurationHelper;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

}