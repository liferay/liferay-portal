/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.announcements.web.internal.upgrade.v2_1_0;

import com.liferay.announcements.constants.AnnouncementsPortletKeys;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.model.ExternalReferenceCodeModel;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.upgrade.BasePortletPreferencesUpgradeProcess;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletPreferences;

import java.util.function.Function;

/**
 * @author Marco Galluzzi
 */
public class UpgradePortletPreferences
	extends BasePortletPreferencesUpgradeProcess {

	public UpgradePortletPreferences(
		GroupLocalService groupLocalService,
		OrganizationLocalService organizationLocalService,
		RoleLocalService roleLocalService,
		UserGroupLocalService userGroupLocalService) {

		_groupLocalService = groupLocalService;
		_organizationLocalService = organizationLocalService;
		_roleLocalService = roleLocalService;
		_userGroupLocalService = userGroupLocalService;
	}

	@Override
	protected String[] getPortletIds() {
		return new String[] {AnnouncementsPortletKeys.ANNOUNCEMENTS + "%"};
	}

	@Override
	protected String upgradePreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId, String xml)
		throws Exception {

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.fromXML(
				companyId, ownerId, ownerType, plid, portletId, xml);

		_upgradeSelectedScopeIds(
			portletPreferences, "Group", _groupLocalService::fetchGroup);
		_upgradeSelectedScopeIds(
			portletPreferences, "Organization",
			_organizationLocalService::fetchOrganization);
		_upgradeSelectedScopeIds(
			portletPreferences, "Role", _roleLocalService::fetchRole);
		_upgradeSelectedScopeIds(
			portletPreferences, "UserGroup",
			_userGroupLocalService::fetchUserGroup);

		return PortletPreferencesFactoryUtil.toXML(portletPreferences);
	}

	private <T extends ExternalReferenceCodeModel> void
			_upgradeSelectedScopeIds(
				PortletPreferences portletPreferences, String scope,
				Function<Long, T> fetchFunction)
		throws Exception {

		String selectedScopeIds = portletPreferences.getValue(
			"selectedScope" + scope + "Ids", null);

		if (Validator.isBlank(selectedScopeIds)) {
			return;
		}

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray(
			TransformUtil.transformToList(
				StringUtil.split(selectedScopeIds, 0L),
				id -> {
					if (id == 0) {
						return null;
					}

					T model = fetchFunction.apply(id);

					if (model == null) {
						return null;
					}

					return HtmlUtil.escape(model.getExternalReferenceCode());
				}));

		if (jsonArray.length() > 0) {
			portletPreferences.setValue(
				"selectedScope" + scope + "ExternalReferenceCodes",
				jsonArray.toString());
		}
	}

	private final GroupLocalService _groupLocalService;
	private final OrganizationLocalService _organizationLocalService;
	private final RoleLocalService _roleLocalService;
	private final UserGroupLocalService _userGroupLocalService;

}