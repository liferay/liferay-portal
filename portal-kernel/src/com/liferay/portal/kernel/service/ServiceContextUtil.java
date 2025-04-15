/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.PortletPreferencesIds;
import com.liferay.portal.kernel.service.permission.ModelPermissionsFactory;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.PortletPreferences;

/**
 * @author Raymond Augé
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 */
public class ServiceContextUtil {

	public static Object deserialize(JSONObject jsonObject) {
		ServiceContext serviceContext = new ServiceContext();

		// Theme display

		serviceContext.setCompanyId(jsonObject.getLong("companyId"));
		serviceContext.setLayoutFullURL(jsonObject.getString("layoutFullURL"));
		serviceContext.setLayoutURL(jsonObject.getString("layoutURL"));
		serviceContext.setPathMain(jsonObject.getString("pathMain"));
		serviceContext.setPlid(jsonObject.getLong("plid"));
		serviceContext.setPortalURL(jsonObject.getString("portalURL"));
		serviceContext.setScopeGroupId(jsonObject.getLong("scopeGroupId"));
		serviceContext.setUserDisplayURL(
			jsonObject.getString("userDisplayURL"));
		serviceContext.setUserId(jsonObject.getLong("userId"));

		// Permissions

		serviceContext.setAddGroupPermissions(
			jsonObject.getBoolean("addGroupPermissions"));
		serviceContext.setAddGuestPermissions(
			jsonObject.getBoolean("addGuestPermissions"));

		String groupPermissions = jsonObject.getString("groupPermissions");
		String guestPermissions = jsonObject.getString("guestPermissions");

		if ((groupPermissions != null) || (guestPermissions != null)) {
			serviceContext.setModelPermissions(
				ModelPermissionsFactory.create(
					StringUtil.split(groupPermissions),
					StringUtil.split(guestPermissions)));
		}

		// Asset

		serviceContext.setAssetCategoryIds(
			StringUtil.split(jsonObject.getString("assetCategoryIds"), 0L));
		serviceContext.setAssetTagNames(
			StringUtil.split(jsonObject.getString("assetTagNames")));

		// Workflow

		serviceContext.setWorkflowAction(jsonObject.getInt("workflowAction"));

		return serviceContext;
	}

	public static PortletPreferences getPortletPreferences(
		ServiceContext serviceContext) {

		if (serviceContext == null) {
			return null;
		}

		PortletPreferencesIds portletPreferencesIds =
			serviceContext.getPortletPreferencesIds();

		if (portletPreferencesIds == null) {
			return null;
		}

		return PortletPreferencesLocalServiceUtil.getPreferences(
			portletPreferencesIds.getCompanyId(),
			portletPreferencesIds.getOwnerId(),
			portletPreferencesIds.getOwnerType(),
			portletPreferencesIds.getPlid(),
			portletPreferencesIds.getPortletId());
	}

}